package player;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ABCTerminalLexer {
    // Storage for the input string
    private String nonTerminal;
    // Location in the string currently operating
    private int position;
    // Mapping of terminals to the appropriate regexes
    private Map<Terminals, String> TerminalRegex = new HashMap<Terminals, String>();
    // The string used to check for and return values
    private String lookFor;
    // All the valid Terminals used for construction of the Regex Table
    public enum Terminals {
        DIGIT,
        TEXT,
        FRACTION,
        KEY_ACCIDENTAL,
        MODE_MINOR,
        OCTAVE,
        ACCIDENTAL,
        BASENOTE,
        REST
    }
    /**
     * Creates a Lexer that only identifies Terminal sequences as described
     * by the ABC Grammar subset understood by the program
     * 
     * @param input, the string upon which to build an ABCTerminalLexer
     * @param startLoc, an offset to impose upon the ABCTerminalLexer at creation
     */
    public ABCTerminalLexer(String input, int startLoc) {
        this.nonTerminal = input;
        this.position = startLoc;
        BuildTerminalRegex();
    }
    
    /**
     * Creates a Mapping between Terminals for the ABC Grammar of the Lexer
     * and a regular expression that represents that terminal
     */
    private void BuildTerminalRegex(){
        TerminalRegex.put(Terminals.DIGIT, "[0-9]++");
        TerminalRegex.put(Terminals.TEXT, "[\\w \\t\\p{Punct}]++");
        TerminalRegex.put(Terminals.FRACTION, "/");
        TerminalRegex.put(Terminals.KEY_ACCIDENTAL, "[#b]");
        TerminalRegex.put(Terminals.MODE_MINOR, "[m]");
        TerminalRegex.put(Terminals.OCTAVE, "((,)++|(')++)");
        TerminalRegex.put(Terminals.ACCIDENTAL, "[\\^{1,2}_{1,2}=]");
        TerminalRegex.put(Terminals.BASENOTE, "[A-Ga-g]");
        TerminalRegex.put(Terminals.REST, "[z]");
    }
    /**
     * Determines if a given Terminal exists at the current position that the ABCTerminalLexer is at
     * 
     * @param It, a given Terminal in the ABC Grammar for which to verify
     * @return A boolean representing if the desired Terminal exists at the current location of the Lexer
     */
    public boolean IsItThere(Terminals It){
        lookFor = "^.{" + position + "}\\s*" +
                         TerminalRegex.get(It) + ".*";
        return nonTerminal.matches(lookFor);
    }
    // The generic fetch that gets the string that matches the
    // Terminal still loaded in lookFor if it exists
    // Should never be run directly only after running IsItThere
    // then running the appropriate Fetch

    /**
     * Fetches a substring of the ABCTerminalLexer input that matches the current value in
     * stored in the system state, relies on running IsItThere beforehand to check for a desired
     * value and store that value to the system state
     * 
     * @return A String matching the Terminal in system state with leading whitespace removed
     */
    private String FetchGeneric(){
        Pattern search = Pattern.compile(lookFor.substring(0,(lookFor.length()-2)));
        Matcher fetch = search.matcher(nonTerminal);
        fetch.find();
        String value = fetch.group().substring(position);
        position += value.length();
        return value.replaceAll("^\\s","");
    }
    // Returns the matching digit
    public int FetchDigit(){
        return Integer.parseInt(FetchGeneric());
    }
    // Returns the matching text
    public String FetchText(){
        return FetchGeneric();
    }
    // Increments the position to simulate fetching the "/"
    public void FetchFraction(){
        position += 1;
    }
    // Returns the numerical representation of the accidental
    public int FetchKeyAccidental(){
        if(FetchGeneric().charAt(0) == '#'){
            return 1;
        } else {
            return -1;
        }
    }
    // If there is a minor, it must be there so use fetch to increment the search
    // then just return the -1 for octaves
    public int FetchModeMinor(){
        String dummy = FetchGeneric();
        return -1 * dummy.length();
    }
    // Returns the numerical representation of octaves
    public int FetchOctave(){
        String value = FetchGeneric();
        if(value.charAt(0) == ','){
            return (-1 * value.length());
        } else {
            return value.length();
        }
    }
    // Return the numerical representation of accidentals
    public int FetchAccidental(){
        String value = FetchGeneric();
        switch (value.charAt(0)){
        case '^':
            return 1 * value.length();
        case '_':
            return (-1 * value.length());
        default:
            return 0;
        }
    }
    // Returns the basenote
    public char FetchBasenote(){
        return FetchGeneric().charAt(0);
    }
    // Returns the rest
    public char FetchRest(){
    	return FetchGeneric().replaceAll("\\s","").charAt(0);
    }
}
