package player;

/**
 * The Token Super Class for ABC files, contains an Enums of types so that all tokens can be compared regardless
 * of delegation due to the existence of Comments and Voices
 * 
 * Is also the construct for EoL Tokens as they do not require methods to function properly
 * 
 * @author Xearim
 *
 */
public class Tokens {
    // Enum with all possible token types, visible for use by Lexer
    public enum Type {
        FIELD_NUMBER,
        FIELD_TITLE,
        FIELD_COMPOSER,
        FIELD_DEFAULT_LENGTH,
        FIELD_METER,
        FIELD_TEMPO,
        FIELD_VOICE,
        FIELD_KEY,
        NOTE,
        REST,
        TUPLET,
        CHORD,
        BARLINE,
        REPEAT,
        COMMENT,
        END_OF_LINE
    }
    
    // All Tokens have a type, and can return that type
    protected Type type;
    // The End of Line Constructor
    public Tokens(){
        this.type = Type.END_OF_LINE;
    }
    // Return method for type
    public Type getType() {
        return this.type;
    }
    
    /**
     * The Generic Class of All Tokens expected in the ABCMusic half of an .abc file
     * contains information about the given note construct as well as methods for fetching
     * that information
     * 
     * Should by all rights be immutable, but its uses are non-dependent on immutability
     * 
     * @author Xearim
     *
     */
    public class NoteToken extends Tokens {
        // Basenote of a note (value of r denotes a non-basenote token)
        private final char note;
        // Octave and Accidental as +/- modifiers
        private final int octave;
        private final int accidental;
        // Note length fraction as numerator/denominator or/and length of chord/tuple
        private final int[] length = new int[2];
        // List of notes for tuples and chords
        private NoteToken[] noteList;
        
        // Constructor for Rests
        public NoteToken(int numerator, int denominator){
            this(Type.REST, 'r', 0, 0, numerator, denominator, null);
        }
        
        // Constructor for Notes
        public NoteToken(char note, int octave, int accidental,
                         int numerator, int denominator){
            this(Type.NOTE, note, octave, accidental, numerator, denominator, null);
        }
        
        // Constructor for Tuplet/Chord
        public NoteToken(Type type,int numerator,int denominator, NoteToken[] noteList){
            this(type, 'r', 0, noteList.length, numerator, denominator, noteList);
        }
        
        // Hidden full constructor
        
        private NoteToken(Type type, char note,
                  int octave, int accidental,
                  int lengthNumerator, int lengthDenominator,
                  NoteToken[] noteList){
            this.type = type;
            this.note = note;
            this.octave = octave;
            this.accidental = accidental;
            this.length[0] = lengthNumerator;
            this.length[1] = lengthDenominator;  
            this.noteList = noteList;
        }
        
        // Library of returns for internal elements
        public char getNote(){return this.note;}
        public int getOctave(){return this.octave;}
        public int getAccidental(){return this.accidental;}
        public int[] getLength(){return this.length;}
        public NoteToken[] getElts(){
            if(this.noteList != null){
                return this.noteList;
            } else {
                throw new UnsupportedOperationException("Cannot getElts() of Token of type: " + this.type);
            }
        }
        
        // To string as requested for testing
        @Override public String toString(){
            StringBuilder output = new StringBuilder();
            switch(this.type){
            case NOTE:
                switch(getAccidental()){
                case -2:
                    output.append("__");
                    break;
                case -1:
                    output.append('_');
                    break;
                case 0:
                    output.append('=');
                    break;
                case 1:
                    output.append('^');
                    break;
                case 2:
                    output.append("^^");
                    break;
                default:
                    break;
                }
                output.append(getNote());
                if(getOctave() > 0){
                    for(int i = 0; i > getOctave(); i++){
                        output.append("'"); 
                    }
                } else if (getOctave() < 0) {
                    for(int i = 0; i < getOctave(); i--){
                        output.append(","); 
                    }
                }
                output.append(getLength()[0]);
                output.append('/');
                output.append(getLength()[1]);
                return output.toString();
            case CHORD:
                output.append('[');
                for(NoteToken note: getElts()){
                    output.append(note.toString());
                }
                output.append(']');
                return output.toString();
            case TUPLET:
                output.append('(');
                output.append(getLength()[1]);
                for(NoteToken note: getElts()){
                    output.append(note.toString());
                }
                return output.toString();
            case REST:
                output.append(getNote());
                output.append(getLength()[0]);
                output.append('/');
                output.append(getLength()[1]);
                return output.toString();
            default:
                return "NaN:Not a Note";
            }
        }
        
        // For Tuplets, this is how the length is modified
        public NoteToken changeLength(int numerator, int denominator){
            return new NoteToken(this.note, this.octave, this.accidental,this.length[0]*numerator, this.length[1]*denominator);
        }
        
    }
    
    /**
     * The Generic Class of All Tokens expected in the ABCHeader half of a .abc music file
     * contains methods for accessing the information expected of the given construct, does not
     * contain default toString() implementations as they are unnecessary as the ADT they become
     * subsets of delegates their printing
     * 
     * @author Xearim
     *
     */
    public class ElemToken extends Tokens {
        // Values of Elems
        private final String text;
        // Note token representing the key of a FIELD_KEY
        private final NoteToken key;
        // Numerical storage for FIELD's that support it
        // For Barline and Repeat has special functionality
        // For Barline, length[0] is type of barline
        // For Repeat, length[0] is number of repeat
        private final int[] length = new int[2];
        
        // Title, Composer, Number, Comment and Voice Constructor
        public ElemToken(Type type, String text){
            this(type, text, null, 0, 0);
        }
        // Default-Length, Meter, Barline and Repeat Constructor
        public ElemToken(Type type, int numerator, int denominator){
            this(type, null, null, numerator, denominator);
        }
        // Tempo Constructor
        public ElemToken(int tempo){
            this(Type.FIELD_TEMPO, null, null, tempo, 1);
        }
        // Key Constructor
        public ElemToken(NoteToken key){
            this(Type.FIELD_KEY, null, key, 0, 0);
        }
        
        private ElemToken(Type type, String text, NoteToken key,
                          int numerator, int denominator){
            this.type = type;
            this.text = text;
            this.key = key;
            this.length[0] = numerator;
            this.length[1] = denominator;
        }
        
        //Library of returns for internal elements
        public String getText(){
            if(this.text != null){
                return this.text;
            } else {
                throw new UnsupportedOperationException("Cannot getText() of Token of type: " + this.type);
            }
        }
        public NoteToken getKey(){
            if(this.key != null){
                return this.key;
            } else {
                throw new UnsupportedOperationException("Cannot getKey() of Token of type: " + this.type);
            }
        }
        public int[] getVals(){
            if(this.length[1] != 0){
                return this.length;
            } else {
                throw new UnsupportedOperationException("Cannot getVals() of Token of type: " + this.type);
            }
        }
        
    }
    
}
