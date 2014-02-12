package player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import player.ABC_ADT.Bars;
import player.ABC_ADT.Meters;
import player.ABC_ADT.Voice;
import player.Tokens.ElemToken;
import player.Tokens.NoteToken;

public class ABCParser {
    private final ABCLexer lexer;
    private Tokens.Type next;
    private ABCMusic ThePiece;
    private Voice workingVoice;
    private int workingIndex;
    private List<Bars> repeatTracker = new ArrayList<Bars>();
    
    /**
     * A Parser for .abc Files
     * 
     * @return A new ABCParser for the given .abc file contained within the Lexer
     * @param Lexer, a Valid ABCLexer from which to draw Tokens
     */
    public ABCParser(ABCLexer Lexer) {
        this.lexer = Lexer;
    }
    
    /**
     * Parses the given .abc File into an ADT, throws RuntimeExceptions upon encountering 
     * ABC Grammar breaking constructs
     * 
     * Note: allows a near indefinite break between the end of the Key signature and the start of music,
     * this break can contain comments, but once the music starts, it must adhere to the grammar
     * 
     * @return An ABCMusic file which contains all the methods necessary to both play and analyze
     * the .abc file contained by the ABCParser
     */
    public ABCMusic parse(){
        next = lexer.GetNextTokenType();
        if(next == Tokens.Type.FIELD_NUMBER){
            parseHeader();
            next = lexer.GetNextTokenType();
            while(next == Tokens.Type.END_OF_LINE){
            	checkAndTrashEoL();
            	next = lexer.GetNextTokenType();
            }
            if(!(next == Tokens.Type.FIELD_VOICE || next == Tokens.Type.COMMENT ||
               next == Tokens.Type.NOTE || next == Tokens.Type.CHORD || next == Tokens.Type.TUPLET || next == Tokens.Type.REST)){
                throw new IllegalArgumentException("Expected start of music instead got: " + next);
            }
            parseMusic();
            next = lexer.GetNextTokenType();
            if(next != Tokens.Type.END_OF_LINE){
                     throw new IllegalArgumentException("Expected music to be over, instead there was a: " + next);
                 }
        } else {
            throw new IllegalArgumentException("Expected a music ID, received: " + next);
        }   
        return ThePiece;
    }
    
    /**
     * Verifies an EoL Character terminates any given line for which it is run, as well as
     * Checking for Comment blocks and discarding them (since they are part of an EoL)
     * 
     * Note: Assumes that comments are unused or needed, and thus they will never be printed to screen
     */
    private void checkAndTrashEoL(){
        next = lexer.GetNextTokenType();
        if(next == Tokens.Type.END_OF_LINE){
            // Little dirty discard of EoL characters
            @SuppressWarnings("unused")
            Tokens dummy = lexer.getEndOfLineToken();
        } else if(next == Tokens.Type.COMMENT) {
            // Little dirty discard of Comments, they dont matter i think?
            // Otherwise this is where Comments would be set to print out
            @SuppressWarnings("unused")
            Tokens dummy = lexer.getNextElemToken();
            checkAndTrashEoL();
        } else {    
            throw new IllegalArgumentException("Expected a EoL, received: " + next);
        }
    }
    
    /**
     * Parses the ABCHeader information as prescribed by the ABC Grammar subset
     * understood by the ABCParser
     */
    private void parseHeader(){
        // All the components of a valid ABCMusic
        List<Voice> VoicesList = new ArrayList<Voice>();
        
        String name, title, IDNum;
        int bpm;
        int[] meterSum = new int[2];
        int[] noteLength = new int[2];
        NoteToken key;
        
        //Booleans for having set the optional quantities
        boolean setL = Boolean.FALSE;
        boolean setM = Boolean.FALSE;
        boolean setQ = Boolean.FALSE;
        boolean setC = Boolean.FALSE;
        
        //Establish Defaults for unnecessary fields so that we only need to add
        //the the vars
        
        bpm = 100;
        noteLength[0] = 1;
        noteLength[1] = 8;
        meterSum[0] = 4;
        meterSum[1] = 4;
        name = "Unknown";
        
        //Now grab X and T since they are in a manditory order
        IDNum = lexer.getNextElemToken().getText();
        checkAndTrashEoL();
        next = lexer.GetNextTokenType();
        if(next == Tokens.Type.FIELD_TITLE){
            title = lexer.getNextElemToken().getText();
        } else {
            throw new IllegalArgumentException("Expected a music Title, received: " + next);
        }
        checkAndTrashEoL();
        //Until you encounter the Field Key, look for all optional classes, and only allow them to be set once
        while(next != Tokens.Type.FIELD_KEY){
            next = lexer.GetNextTokenType();
            switch(next){
            case FIELD_DEFAULT_LENGTH:
                if(!setL){
                    ElemToken tempToken = lexer.getNextElemToken();
                    int[] settingArray = tempToken.getVals();
                    noteLength[0] = settingArray[0];
                    noteLength[1] = settingArray[1];
                    setL = Boolean.TRUE;
                } else {
                    throw new IllegalArgumentException("Cannot Set Default Length more than once");
                }
                               
                checkAndTrashEoL();
                break;
            case FIELD_METER:
                if(!setM){
                    ElemToken tempToken = lexer.getNextElemToken();
                    int[] settingArray = tempToken.getVals();
                    meterSum[0] = settingArray[0];
                    meterSum[1] = settingArray[1];
                    setM = Boolean.TRUE;
                } else {
                    throw new IllegalArgumentException("Cannot Set Meter Length more than once");
                }
                
                checkAndTrashEoL();
                break;
                
            case FIELD_TEMPO:
                if(!setQ){
                    ElemToken tempToken = lexer.getNextElemToken();
                    int[] settingArray = tempToken.getVals();
                    bpm = settingArray[0];
                    setQ = Boolean.TRUE;
                } else {
                    throw new IllegalArgumentException("Cannot Set Tempo more than once");
                }
                checkAndTrashEoL();
                break;
                
                
            case FIELD_COMPOSER:
                if(!setC){
                    ElemToken tempToken = lexer.getNextElemToken();
                    name = tempToken.getText();
                    setC = Boolean.TRUE;
                } else {
                    throw new IllegalArgumentException("Cannot Set Composer more than once");
                }
                
                checkAndTrashEoL();
                break;
                
            case FIELD_VOICE:
                VoicesList.add(new Voice(lexer.getNextElemToken().getText()));
                checkAndTrashEoL();
                break;
                
            default:
            	break;
            }
        }
        // Last element should be the field key
        if(next == Tokens.Type.FIELD_KEY){
            key = lexer.getNextElemToken().getKey();
        } else {
            throw new IllegalArgumentException("Expected a music Key for the piece, received: " + next);
        }
        checkAndTrashEoL();
        // If the file has no voices, then we need at least one for the ABCMusic file to be valid
        // Construct a dummy one
        if(VoicesList.size() == 0){
            VoicesList.add(new Voice("DummyVoice"));
        }
        // Initialize the new ABCMusic file
        ThePiece = new ABCMusic(VoicesList, name, key, noteLength, meterSum, bpm, title, IDNum);
    }
    
    /**
     * Parses the ABCMusic information as prescribed by the ABC Grammar understood by
     * the ABCParser, does so recursively via sub-functions and stores the information in
     * the ABCMusic output file's VoicesList
     */
    private void parseMusic(){
        workingIndex = 0;
        workingVoice = ThePiece.VoicesList.get(0);
        // Until we are out of Music
        while(!lexer.IsDone()){
            next = lexer.GetNextTokenType();
            if(next == Tokens.Type.END_OF_LINE || next == Tokens.Type.COMMENT){
                // Trash found comments and EoL chars
                checkAndTrashEoL();
            } else if (next == Tokens.Type.FIELD_VOICE){
                String nextVoice = lexer.getNextElemToken().getText();
                int index = 0;
                // Try all the voices to see what we should switch to
                for(Iterator<Voice> i = ThePiece.VoicesList.iterator(); i.hasNext();){
                    if(i.next().getName().equals(nextVoice)){
                        workingVoice = ThePiece.VoicesList.get(index);
                        workingIndex = index;
                    } else {
                        index++;
                        // If we have tried all Voices, then it must not exist
                        if(index >= ThePiece.VoicesList.size()){
                            throw new IllegalArgumentException("Invalid Voice name in the Music Section: " + nextVoice);
                        }
                    }
                }
            } else {
                // Recurse on a given Voice to parse it
                parseBar();
            }
        }
        // When we reach the end, all remaining meters to all Voices to finalize the piece
        for(int index = 0; index < ThePiece.VoicesList.size(); index++){
            workingVoice = ThePiece.VoicesList.get(index);
            workingIndex = index;
            workingVoice.addCopyElts(repeatTracker.get(workingIndex));
        }  
    }
    /**
     * Parses a given set of Meters in a given Voice until an EoL character is found
     * utilizes a list of mutable, working meters to manage repeats
     * 
     * Note: Highly dependent on proper use of copying references to a list versus copying the actual
     * list so edit carefully
     * 
     * Note: Assumes that nested repeats do not exist, however they are musically unsound so the assumption
     * seems valid
     */
    private void parseBar(){
        Bars workingBar;
        // If we havent used the voice, initialie its repeatList, else get its repeat list
        if(repeatTracker.size() < (workingIndex + 1)){
            workingBar = new Bars();
            repeatTracker.add(workingBar);
        } else {
            workingBar = repeatTracker.get(workingIndex);
        }
        // Construct our working meter and recursively get it
        Meters workingMeter;
        while(next != Tokens.Type.END_OF_LINE && next != Tokens.Type.COMMENT){
            workingMeter = parseMeter();
            workingBar.addElts(workingMeter);
            next = lexer.GetNextTokenType();
            // If we find an end of meter
            if(next == Tokens.Type.BARLINE){
                ElemToken barToken = lexer.getNextElemToken();
                switch(barToken.getVals()[0]){
                case 1:
                    // Do nothing for regular meters
                    break;
                case 3:
                    // Add all marked for repeat meters to the voice for repeats
                    workingVoice.addCopyElts(workingBar);
                    break;
                default:
                    // Add all unadded meters and clear what gets repeated otherwise
                    workingVoice.addCopyElts(workingBar);
                    workingBar.clear();
                    break;
                }
                next = lexer.GetNextTokenType();
            // If we find a # Repeat token
            } else if(next == Tokens.Type.REPEAT){
                int repeatNum = lexer.getNextElemToken().getVals()[0];
                // Only for the first repeat token
                if(repeatNum == 1){         
                    // Temporarily store the contents to be repeated
                	Bars tempBar = new Bars();
                	for (Iterator<Meters> i = workingBar.MetersList.iterator();i.hasNext();){
                		tempBar.addCopyElts(i.next());
                	}
                	// Recursively parse to the Repeat Barline char, which will add the meters
                	// Then clear the list and re-add everything we stored before getting the 
                	// First repeat information
                    parseBar();
                    workingBar.clear();
                    for (Iterator<Meters> i = tempBar.MetersList.iterator();i.hasNext();){
                		workingBar.addCopyElts(i.next());
                	}
                    // Now get the second repeat information
                    parseBar();
                } else {
                    // Just break the given nested parse for second repeats, they are
                    // handled by Barline characters that end them
                	break;                                
                }
                next = lexer.GetNextTokenType();
            } else if(next == Tokens.Type.END_OF_LINE || next == Tokens.Type.COMMENT){
            	break;
            } else {
                throw new IllegalArgumentException("Expected a bar to have a termination, got: " + next);
            }
        }
    }
    /**
     * The terminal recursion method that compiles the list of NoteTokens that make up a given meter
     * according to the ABC Grammar specifications
     * 
     * Note: Currently only enforces that a meter not have timing greater than specified in the ABCHeader
     * but does not care about truncated Meters, apparently those are okay in music
     * 
     * Note: Assumes that meters can be of any length if desired, even if those lengths violate the M field
     * this is due to the availability of truncated starts and ends, or switches to time mid music
     * 
     * @return An ADT Meters that contains a list of all the NoteTokens that exist in the meter it
     * begins from
     */
    private Meters parseMeter(){
        next = lexer.GetNextTokenType();
        Meters currentMeter = new Meters();
        // Create variables to enforce meter lengths
        double noteLength;
        double meterCount = 0;
        double meterLength = ((double) ThePiece.getMeterSum()[0])/((double) ThePiece.getMeterSum()[1]);
        // Until the given meter is terminated
        while(next != Tokens.Type.BARLINE && next != Tokens.Type.REPEAT &&
        	  next != Tokens.Type.END_OF_LINE && next != Tokens.Type.COMMENT && meterCount < meterLength){
        	NoteToken nextToken = lexer.getNextNoteToken();
            switch(next){
            case NOTE: case REST:
                // Construct the note or rest length and add it to the meter length
                noteLength = (((double) (nextToken.getLength()[0] * ThePiece.getNoteLength()[0]))/((double) (nextToken.getLength()[1] * (double)ThePiece.getNoteLength()[1])));
                meterCount += noteLength;
                // As long as the meter is not overloaded, add the note or rest to the Meters List
                if(meterCount <= meterLength){
                    currentMeter.addElts(nextToken);
                    next = lexer.GetNextTokenType();
                } else {
                    throw new IllegalArgumentException("Overloaded Meter with count greater than permitted");
                }
                next = lexer.GetNextTokenType();
                break;
            case TUPLET:
                // Calculate the Tuplet length as a product of the type of Tuplet and its inner note lengths
                // this is dependent on all Tuplet notes having the same length
            	noteLength = (((double) (nextToken.getElts()[0].getLength()[0] * ThePiece.getNoteLength()[0]))/((double) (nextToken.getElts()[0].getLength()[1] * (double)ThePiece.getNoteLength()[1])));
                noteLength = noteLength*nextToken.getLength()[0];
            	meterCount += noteLength;
            	// Add each note in the Tuplet as long as the whole tuplet would fit in the meter
                if(meterCount <= meterLength){
                    for(int i = 0; i < nextToken.getElts().length; i++){
                        currentMeter.addElts(nextToken.getElts()[i].changeLength(nextToken.getLength()[0], nextToken.getLength()[1]));
                    }
                    next = lexer.GetNextTokenType();
                } else {
                    throw new IllegalArgumentException("Overloaded Meter with a tuplet, is it counted wrong?");
                }
                next = lexer.GetNextTokenType();
                break;
            case CHORD:
                // A Chord is multiple notes played at once, so its length is simply the length of one of the elements
                // This relies on the Chord having all notes of the same length
                noteLength = (((double) (nextToken.getLength()[0] * ThePiece.getNoteLength()[0]))/((double) (nextToken.getLength()[1] * (double)ThePiece.getNoteLength()[1])));
                meterCount += noteLength;
                // If the Chord fits, add its construct to the Meters List
                if(meterCount <= meterLength){
                    currentMeter.addElts(nextToken);
                    next = lexer.GetNextTokenType();
                } else {
                    throw new IllegalArgumentException("Overloaded Meter with a tuplet, is it counted wrong?");
                }
                next = lexer.GetNextTokenType();
                break;
            default:
                throw new IllegalArgumentException("Somehow made a note token, but it was not a NOTE, TUPLET, CHORD or REST");
            }
        }
        //To check measure lengths vs meter, apparently this no longer matters, kept for legacy
        /*
        System.out.println(meterCount+" vs "+meterLength);
        if(meterCount != meterLength && meterCount + .000001 < meterLength){
            throw new IllegalArgumentException("Got a meter with the wrong timing, bad stuff mang");
        }
        */
        // Return the finished meter
        return currentMeter;
     }
}
