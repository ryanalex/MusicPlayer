package player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import player.ABC_ADT.Bars;
import player.ABC_ADT.Meters;
import player.ABC_ADT.Voice;
import player.Tokens.NoteToken;
import sound.Pitch;
import sound.SequencePlayer;

// Where the magic should happen at the end
public class ABCMusic {
    
    public List<Voice> VoicesList = new ArrayList<Voice>();
    
    private final String name, title, IDNum;
    private final int bpm;
    private int[] meterSum = new int[2];
    private int[] noteLength = new int[2];
    private final NoteToken key;
    
    private Map<Character, Integer> keySignature = new HashMap<Character, Integer>();
    private Map<String,Integer> keyAccidental = new HashMap<String,Integer>();
    private SequencePlayer player;
    
    /**
     * The final ADT that represents the union of the ABCHeader and ABCMusic halves of
     * a .abc file as described by the ABC Grammar understood by the ABCParser as a Piece of Music
     * 
     * @param VoicesList, a list of Voices
     * @param Name, the Name of the Piece
     * @param Key, the Key Signature of the Piece
     * @param Length, the Length of a default note in the Piece
     * @param Meter, the # of default length notes in the Piece
     * @param Tempo, the BPM of the Piece
     * @param Title, the Title of the Piece
     * @param ID, the Unique ID # of the Piece
     */
    public ABCMusic(List<Voice> VoicesList, String Name, NoteToken Key, 
                    int[] Length, int[] Meter, int Tempo, String Title, String ID){
        this.VoicesList = VoicesList;
        this.name = Name;
        this.key = Key;
        this.noteLength = Length;
        this.meterSum = Meter;
        this.bpm = Tempo;
        this.title = Title;
        this.IDNum = ID;
    }
    @Override public String toString(){
    	StringBuilder output = new StringBuilder();
    	for (Iterator<Voice> i = VoicesList.iterator(); i.hasNext();){
    		output.append("New Voice: \n");
    		output.append(i.next().toString());
    	}
    	return output.toString();
    }
    
    // Generic returns for internal variables
    public int[] getNoteLength(){return this.noteLength;}
    public int[] getMeterSum(){return this.meterSum;}
    public String getName(){return this.name;}
    public String getTitle(){return this.title;}
    public String getID(){return this.IDNum;}
    public int getTempo(){return this.bpm;}
    public NoteToken getKey(){return this.key;}
    
    /**
     * Prints to Sdt.out the information about the Piece of Music
     */
    public void DisplayInfo(){
        System.out.println("Piece ID:" + getID());
        System.out.println("Piece Title:" + getTitle());
        System.out.println("Piece Composer:" + getName());
        System.out.println("Piece Meter:" + getMeterSum()[0] + "/" + getMeterSum()[1]);
        System.out.println("Piece Default Note Duration:" + getNoteLength()[0] + "/" + getNoteLength()[1]);
        System.out.println("Piece Tempo:" + getTempo());
        
        char minor, accidental;
        switch(getKey().getAccidental()){
            case 1:
                accidental = '#';
            case -1:
                accidental = 'b';
            default:
                accidental = ' ';
        }
        if(getKey().getOctave() == -1){
            minor = 'm';
        } else {
            minor = ' ';
        }
        System.out.println("Piece Key:" + getKey().getNote() + accidental + minor);
    }
    
    /**
     * Turns a NoteToken into a Pitch by equating their representations and then enforcing the
     * Key of the Piece and the Accidentals encountered in the given meter
     * 
     * @param workingNote, the NoteToken from which to make a Pitch
     * @return A Pitch that represents the given input NoteToken
     */
    private Pitch constructNote(NoteToken workingNote){
        Pitch workingPitch;
        int octave = 0;               
        // Add the note initally
        if('A' <= workingNote.getNote() && workingNote.getNote() <= 'G'){
            workingPitch = new Pitch(workingNote.getNote());
        } else {
        	octave++;
        	workingPitch = new Pitch(Character.toUpperCase(workingNote.getNote())).transpose(Pitch.OCTAVE);
        }
        // Transpose by the octaves
        octave+=workingNote.getOctave();
        workingPitch = workingPitch.octaveTranspose(workingNote.getOctave());
        // Collect accidentals, use them to modify the accidental list
        switch(workingNote.getNote()){
        case 'A': case 'a':
            if(workingNote.getAccidental() == Integer.MIN_VALUE){
            	keyAccidental.put("A"+octave, 0);
            } else if(workingNote.getAccidental() == 0){
                keyAccidental.put("A"+octave, -1*keySignature.get('A'));
            } else {
                keyAccidental.put("A"+octave, -1*keySignature.get('A')+workingNote.getAccidental());
            }
            break;
        case 'B': case 'b':
            if(workingNote.getAccidental() == Integer.MIN_VALUE){
            	keyAccidental.put("B"+octave, 0);
            } else if(workingNote.getAccidental() == 0){
                keyAccidental.put("B"+octave, -1*keySignature.get('B'));
            } else {
                keyAccidental.put("B"+octave, -1*keySignature.get('B')+workingNote.getAccidental());
            } 
            break;
        case 'C': case 'c':
            if(workingNote.getAccidental() == Integer.MIN_VALUE){
            	keyAccidental.put("C"+octave, 0);
            } else if(workingNote.getAccidental() == 0){
                keyAccidental.put("C"+octave, -1*keySignature.get('C'));
            } else {
                keyAccidental.put("C"+octave, -1*keySignature.get('C')+workingNote.getAccidental());
            } 
            break;
        case 'D': case 'd':
            if(workingNote.getAccidental() == Integer.MIN_VALUE){
            	keyAccidental.put("D"+octave, 0);
            } else if(workingNote.getAccidental() == 0){
                keyAccidental.put("D"+octave, -1*keySignature.get('D'));
            } else {
                keyAccidental.put("D"+octave, -1*keySignature.get('D')+workingNote.getAccidental());
            } 
            break;
        case 'E': case 'e':
            if(workingNote.getAccidental() == Integer.MIN_VALUE){
            	keyAccidental.put("E"+octave, 0);
            } else if(workingNote.getAccidental() == 0){
                keyAccidental.put("E"+octave, -1*keySignature.get('E'));
            } else {
                keyAccidental.put("E"+octave, -1*keySignature.get('E')+workingNote.getAccidental());
            } 
            break;
        case 'F': case 'f':
            if(workingNote.getAccidental() == Integer.MIN_VALUE){
                keyAccidental.put("F"+octave, 0);
            } else if(workingNote.getAccidental() == 0){
                keyAccidental.put("F"+octave, -1*keySignature.get('F'));
            } else {
                keyAccidental.put("F"+octave, -1*keySignature.get('F')+workingNote.getAccidental());
            } 
            break;
        case 'G': case 'g':
            if(workingNote.getAccidental() == Integer.MIN_VALUE){
            	keyAccidental.put("G"+octave, 0);
            } else if(workingNote.getAccidental() == 0){
                keyAccidental.put("G"+octave, -1*keySignature.get('G'));
            } else {
                keyAccidental.put("G"+octave, -1*keySignature.get('G')+workingNote.getAccidental());
            } 
            break;
        }
        // Now modify by the accidental and key signature in tandem
        switch(workingNote.getNote()){        
        case 'A': case 'a':
            workingPitch = workingPitch.accidentalTranspose(keyAccidental.get("A"+octave));
            workingPitch = workingPitch.accidentalTranspose(keySignature.get('A'));
            break;
        case 'B': case 'b':
            workingPitch = workingPitch.accidentalTranspose(keyAccidental.get("B"+octave));
            workingPitch = workingPitch.accidentalTranspose(keySignature.get('B'));
            break;
        case 'C': case 'c':
            workingPitch = workingPitch.accidentalTranspose(keyAccidental.get("C"+octave));
            workingPitch = workingPitch.accidentalTranspose(keySignature.get('C'));
            break;
        case 'D': case 'd':
            workingPitch = workingPitch.accidentalTranspose(keyAccidental.get("D"+octave));
            workingPitch = workingPitch.accidentalTranspose(keySignature.get('D'));
            break;
        case 'E': case 'e':
            workingPitch = workingPitch.accidentalTranspose(keyAccidental.get("E"+octave));
            workingPitch = workingPitch.accidentalTranspose(keySignature.get('E'));
            break;
        case 'F': case 'f':
            workingPitch = workingPitch.accidentalTranspose(keyAccidental.get("F"+octave));
            workingPitch = workingPitch.accidentalTranspose(keySignature.get('F'));
            break;
        case 'G': case 'g':
            workingPitch = workingPitch.accidentalTranspose(keyAccidental.get("G"+octave));
            workingPitch = workingPitch.accidentalTranspose(keySignature.get('G'));
            break;
        }
        return workingPitch;
    }
    

    /**
     * Uses the internal Key of the Piece to determine the correct Key Signature Mapping for the Piece
     * to be played in and then return that Key Signature
     * 
     * @return A Mapping of notes in a standard music scale to integers representing Sharps and Flats
     * to apply to them to apply a specific Key
     */
    private Map<Character, Integer> getKeySignature(){
        Map<Character, Integer> keyMap = new HashMap<Character, Integer>();
        // If in the Major Scales
        if(getKey().getOctave() == 0){
            switch(getKey().getNote()){
            case 'A':
                // A flat major
                if(getKey().getAccidental() == -1){
                    keyMap.put('F', 0);
                    keyMap.put('C', 0);
                    keyMap.put('G', 0);
                    keyMap.put('D', -1);
                    keyMap.put('A', -1);
                    keyMap.put('E', -1);
                    keyMap.put('B', -1);
                // A major
                } else if(getKey().getAccidental() == 0) {
                    keyMap.put('F', 1);
                    keyMap.put('C', 1);
                    keyMap.put('G', 1);
                    keyMap.put('D', 0);
                    keyMap.put('A', 0);
                    keyMap.put('E', 0);
                    keyMap.put('B', 0);
                } else {
                    throw new IllegalArgumentException("Cannot have Key Signature A Sharp Major");
                }
                break;
            case 'B':
                // B flat major
                if(getKey().getAccidental() == -1){
                    keyMap.put('F', 0);
                    keyMap.put('C', 0);
                    keyMap.put('G', 0);
                    keyMap.put('D', 0);
                    keyMap.put('A', 0);
                    keyMap.put('E', -1);
                    keyMap.put('B', -1);
                // B major
                } else if(getKey().getAccidental() == 0) {
                    keyMap.put('F', 1);
                    keyMap.put('C', 1);
                    keyMap.put('G', 1);
                    keyMap.put('D', 1);
                    keyMap.put('A', 1);
                    keyMap.put('E', 0);
                    keyMap.put('B', 0);
                } else {
                    throw new IllegalArgumentException("Cannot have Key Signature B Sharp Major");
                }
                break;
            case 'C':
                // C flat Major
                if(getKey().getAccidental() == -1){
                    keyMap.put('F', -1);
                    keyMap.put('C', -1);
                    keyMap.put('G', -1);
                    keyMap.put('D', -1);
                    keyMap.put('A', -1);
                    keyMap.put('E', -1);
                    keyMap.put('B', -1);
                // C sharp Major
                } else if (getKey().getAccidental() == 1){
                    keyMap.put('F', 1);
                    keyMap.put('C', 1);
                    keyMap.put('G', 1);
                    keyMap.put('D', 1);
                    keyMap.put('A', 1);
                    keyMap.put('E', 1);
                    keyMap.put('B', 1);
                // C major
                } else {
                    keyMap.put('F', 0);
                    keyMap.put('C', 0);
                    keyMap.put('G', 0);
                    keyMap.put('D', 0);
                    keyMap.put('A', 0);
                    keyMap.put('E', 0);
                    keyMap.put('B', 0);
                }
                break;
            case 'D':
                // D flat major
                if(getKey().getAccidental() == -1){
                    keyMap.put('F', 0);
                    keyMap.put('C', 0);
                    keyMap.put('G', -1);
                    keyMap.put('D', -1);
                    keyMap.put('A', -1);
                    keyMap.put('E', -1);
                    keyMap.put('B', -1);
                // D major
                } else if(getKey().getAccidental() == 0) {
                    keyMap.put('F', 1);
                    keyMap.put('C', 1);
                    keyMap.put('G', 0);
                    keyMap.put('D', 0);
                    keyMap.put('A', 0);
                    keyMap.put('E', 0);
                    keyMap.put('B', 0);
                } else {
                    throw new IllegalArgumentException("Cannot have Key Signature D Sharp Major");
                }
                break;
            case 'E':
                // E flat major
                if(getKey().getAccidental() == -1){
                    keyMap.put('F', 0);
                    keyMap.put('C', 0);
                    keyMap.put('G', 0);
                    keyMap.put('D', 0);
                    keyMap.put('A', -1);
                    keyMap.put('E', -1);
                    keyMap.put('B', -1);
                // E major
                } else if(getKey().getAccidental() == 0) {
                    keyMap.put('F', 1);
                    keyMap.put('C', 1);
                    keyMap.put('G', 1);
                    keyMap.put('D', 1);
                    keyMap.put('A', 0);
                    keyMap.put('E', 0);
                    keyMap.put('B', 0);
                } else {
                    throw new IllegalArgumentException("Cannot have Key Signature E Sharp Major");
                }
                break;
            case 'F':
                // F major
                if(getKey().getAccidental() == 0){
                    keyMap.put('F', 0);
                    keyMap.put('C', 0);
                    keyMap.put('G', 0);
                    keyMap.put('D', 0);
                    keyMap.put('A', 0);
                    keyMap.put('E', 0);
                    keyMap.put('B', -1);
                // F sharp major
                } else if(getKey().getAccidental() == 1) {
                    keyMap.put('F', 1);
                    keyMap.put('C', 1);
                    keyMap.put('G', 1);
                    keyMap.put('D', 1);
                    keyMap.put('A', 1);
                    keyMap.put('E', 1);
                    keyMap.put('B', 0);
                } else {
                    throw new IllegalArgumentException("Cannot have Key Signature F Flat Major");
                }
                break;
            case 'G':
                // G flat major
                if(getKey().getAccidental() == -1){
                    keyMap.put('F', 0);
                    keyMap.put('C', -1);
                    keyMap.put('G', -1);
                    keyMap.put('D', -1);
                    keyMap.put('A', -1);
                    keyMap.put('E', -1);
                    keyMap.put('B', -1);
                // G major
                } else if(getKey().getAccidental() == 0) {
                    keyMap.put('F', 1);
                    keyMap.put('C', 0);
                    keyMap.put('G', 0);
                    keyMap.put('D', 0);
                    keyMap.put('A', 0);
                    keyMap.put('E', 0);
                    keyMap.put('B', 0);
                } else {
                    throw new IllegalArgumentException("Cannot have Key Signature G Sharp Major");
                }
                break;
            }     
        // else it must be a minor scale
        } else {
            switch(getKey().getNote()){
            case 'A':
                // A flat minor
                if(getKey().getAccidental() == -1){
                    keyMap.put('F', -1);
                    keyMap.put('C', -1);
                    keyMap.put('G', -1);
                    keyMap.put('D', -1);
                    keyMap.put('A', -1);
                    keyMap.put('E', -1);
                    keyMap.put('B', -1);
                // A sharp minor
                } else if (getKey().getAccidental() == 1){
                    keyMap.put('F', 1);
                    keyMap.put('C', 1);
                    keyMap.put('G', 1);
                    keyMap.put('D', 1);
                    keyMap.put('A', 1);
                    keyMap.put('E', 1);
                    keyMap.put('B', 1);
                // A minor
                } else {
                    keyMap.put('F', 0);
                    keyMap.put('C', 0);
                    keyMap.put('G', 0);
                    keyMap.put('D', 0);
                    keyMap.put('A', 0);
                    keyMap.put('E', 0);
                    keyMap.put('B', 0);
                }
                break;
            case 'B':
                // B flat minor
                if(getKey().getAccidental() == -1){
                    keyMap.put('F', 0);
                    keyMap.put('C', 0);
                    keyMap.put('G', -1);
                    keyMap.put('D', -1);
                    keyMap.put('A', -1);
                    keyMap.put('E', -1);
                    keyMap.put('B', -1);
                // B minor
                } else if(getKey().getAccidental() == 0) {
                    keyMap.put('F', 1);
                    keyMap.put('C', 1);
                    keyMap.put('G', 0);
                    keyMap.put('D', 0);
                    keyMap.put('A', 0);
                    keyMap.put('E', 0);
                    keyMap.put('B', 0);
                } else {
                    throw new IllegalArgumentException("Cannot have Key Signature B Sharp Minor");
                }
                break;
            case 'C':
                // C minor
                if(getKey().getAccidental() == 0){
                    keyMap.put('F', 0);
                    keyMap.put('C', 0);
                    keyMap.put('G', 0);
                    keyMap.put('D', 0);
                    keyMap.put('A', -1);
                    keyMap.put('E', -1);
                    keyMap.put('B', -1);
                // C sharp minor
                } else if(getKey().getAccidental() == 1) {
                    keyMap.put('F', 1);
                    keyMap.put('C', 1);
                    keyMap.put('G', 1);
                    keyMap.put('D', 1);
                    keyMap.put('A', 0);
                    keyMap.put('E', 0);
                    keyMap.put('B', 0);
                } else {
                    throw new IllegalArgumentException("Cannot have Key Signature C Flat Minor");
                }
                break;
            case 'D':
                // D minor
                if(getKey().getAccidental() == 0){
                    keyMap.put('F', 0);
                    keyMap.put('C', 0);
                    keyMap.put('G', 0);
                    keyMap.put('D', 0);
                    keyMap.put('A', 0);
                    keyMap.put('E', 0);
                    keyMap.put('B', -1);
                // D sharp minor
                } else if(getKey().getAccidental() == 1) {
                    keyMap.put('F', 1);
                    keyMap.put('C', 1);
                    keyMap.put('G', 1);
                    keyMap.put('D', 1);
                    keyMap.put('A', 1);
                    keyMap.put('E', 1);
                    keyMap.put('B', 0);
                } else {
                    throw new IllegalArgumentException("Cannot have Key Signature D Flat Minor");
                }
                break;
            case 'E':
                // E flat minor
                if(getKey().getAccidental() == -1){
                    keyMap.put('F', 0);
                    keyMap.put('C', -1);
                    keyMap.put('G', -1);
                    keyMap.put('D', -1);
                    keyMap.put('A', -1);
                    keyMap.put('E', -1);
                    keyMap.put('B', -1);
                // E minor
                } else if(getKey().getAccidental() == 0) {
                    keyMap.put('F', 1);
                    keyMap.put('C', 0);
                    keyMap.put('G', 0);
                    keyMap.put('D', 0);
                    keyMap.put('A', 0);
                    keyMap.put('E', 0);
                    keyMap.put('B', 0);
                } else {
                    throw new IllegalArgumentException("Cannot have Key Signature E Sharp Minor");
                }
                break;
            case 'F':
                // F minor
                if(getKey().getAccidental() == 0){
                    keyMap.put('F', 0);
                    keyMap.put('C', 0);
                    keyMap.put('G', 0);
                    keyMap.put('D', -1);
                    keyMap.put('A', -1);
                    keyMap.put('E', -1);
                    keyMap.put('B', -1);
                // F sharp minor
                } else if(getKey().getAccidental() == 1) {
                    keyMap.put('F', 1);
                    keyMap.put('C', 1);
                    keyMap.put('G', 1);
                    keyMap.put('D', 0);
                    keyMap.put('A', 0);
                    keyMap.put('E', 0);
                    keyMap.put('B', 0);
                } else {
                    throw new IllegalArgumentException("Cannot have Key Signature F Flat Minor");
                }
                break;
            case 'G':
                // G minor
                if(getKey().getAccidental() == 0){
                    keyMap.put('F', 0);
                    keyMap.put('C', 0);
                    keyMap.put('G', 0);
                    keyMap.put('D', 0);
                    keyMap.put('A', 0);
                    keyMap.put('E', -1);
                    keyMap.put('B', -1);
                // G sharp minor    
                } else if(getKey().getAccidental() == 1){
                    keyMap.put('F', 1);
                    keyMap.put('C', 1);
                    keyMap.put('G', 1);
                    keyMap.put('D', 1);
                    keyMap.put('A', 1);
                    keyMap.put('E', 0);
                    keyMap.put('B', 0);
                } else {
                    throw new IllegalArgumentException("Cannot have Key Signature G Flat Minor");
                }
                break;
            } 
        }
        return keyMap;
    }
   
    // A method for setting up an empty accidental at the end of each meter
    private void resetKeyAccidental(){
    	keyAccidental.clear();
    }
    
    /**
     * Transforms the Voices of the Piece into Pitches which are then subscribed according to timing to
     * an internal SequencePlayer, then plays that SequencePlayer to play the Piece out loud
     * 
     * Catches SequencePlayer Errors internally to avoid them being part of the ABCMusic's responsibility
     * 
     * Note: because tempo must be converted to quarter notes, tempos that are not divisible by 4 will have rounding
     * errors that propagate into the music itself, its fairly negligible, but it exists.
     * 
     * Note: Assumes that 1/16 of the default note length is the shortest note that would be desired to play, any note shorter will play
     * for 1/16 of a default note duration
     */
    public void PlayMusic(){
    	try{
            // Right now it converts bpm to be based on the number of 1/4 given how many L*Q notes exist
            // with the quantized time being
            int quartersPerMin = (getTempo()*getNoteLength()[0]*4)/(getNoteLength()[1]);
            int ticksPerQuarter = (getNoteLength()[1]*16)/(getNoteLength()[0]*4);
            player = new SequencePlayer( quartersPerMin , ticksPerQuarter);
            // Figure out what the Key means, and keep it as a map to edit notes as they are added
            keySignature = getKeySignature();
            // Initialize the accidentals as none, then use that as what gets edited through a meter
            resetKeyAccidental();
            
            for(Iterator<Voice> i = VoicesList.iterator(); i.hasNext();){
                int startTick = 0;
                for(Iterator<Bars> j = i.next().BarsList.iterator(); j.hasNext();){
                    for(Iterator<Meters> k = j.next().MetersList.iterator(); k.hasNext();){
                        for(Iterator<NoteToken> l = k.next().getElts().iterator(); l.hasNext();){
                            NoteToken workingNote = l.next();
                            if(workingNote.getType() == Tokens.Type.CHORD){
                                int noteTicks = 0;
                                for(int m = 0; m < workingNote.getElts().length; m++){
                                    Pitch newNote = constructNote(workingNote.getElts()[m]);
                                    // Length of a note in terms of quarters
                                    double noteLength = ((double) workingNote.getElts()[m].getLength()[0]/(double) workingNote.getElts()[m].getLength()[1])*((double) (getNoteLength()[0]*4)/((double) getNoteLength()[1]));
                                    // Convert the quarters to valid ticks
                                    noteTicks = (int) (noteLength * ticksPerQuarter);
                                    player.addNote(newNote.toMidiNote(), startTick, noteTicks);
                                }   
                                startTick += noteTicks;
                            } else if(workingNote.getType() == Tokens.Type.REST) {
                                // Don't need to make rests
                                // Length of a note in terms of quarters
                                double noteLength = ((double) workingNote.getLength()[0]/(double) workingNote.getLength()[1])*((double) (getNoteLength()[0]*4)/((double) getNoteLength()[1]));
                                // Convert the quarters to valid ticks
                                // Add ticks for the rests skipping time
                                int noteTicks = (int) (noteLength * ticksPerQuarter);
                                startTick += noteTicks;
                            } else {
                                Pitch newNote = constructNote(workingNote);
                                // Length of a note in terms of quarters
                                double noteLength = ((double) workingNote.getLength()[0]/(double) workingNote.getLength()[1])*((double) (getNoteLength()[0]*4)/((double) getNoteLength()[1]));
                                // Convert the quarters to valid ticks
                                int noteTicks = (int) (noteLength * ticksPerQuarter);
                                player.addNote(newNote.toMidiNote(), startTick, noteTicks);
                                startTick += noteTicks;
                            }
                        }
                        // Reset Accidentals at the end of the Meter
                        resetKeyAccidental();
                    }
                }
            }
            // Play the finished SequencePlayer with all Voice there-in
            player.play();
            // Catch undesirable errors
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        
    }
    
}
