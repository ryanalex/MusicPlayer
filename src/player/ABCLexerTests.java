package player;

import static org.junit.Assert.*;

import org.junit.Test;

/*Notes on Testing Strategy
 * These test check every possible code path within the lexer to ensure all possible cases are handled
 * These tests also include the tests necessary for the parser-specific tests
 */
public class ABCLexerTests {
    @Test
    //Tests handling of an empty input, should return END_OF_LINE token
    public void emptyinputtest() {
        String input = "";
        ABCLexer lexer = new ABCLexer(input);
        assertEquals(Tokens.Type.END_OF_LINE,lexer.GetNextTokenType());
    }
    
    @Test
    //Tests handling of index number, should return FIELD_NUMBER token
    public void indexnumbertest() {
        String input = "X: 1";
        ABCLexer lexer = new ABCLexer(input);
        assertEquals(Tokens.Type.FIELD_NUMBER,lexer.GetNextTokenType());
    }
    
    @Test
    //Tests handling of title field, should return FIELD_TITLE token
    public void titletest() {
        String input = "T: Super Song";
        ABCLexer lexer = new ABCLexer(input);
        assertEquals(Tokens.Type.FIELD_TITLE,lexer.GetNextTokenType());
    }
    
    @Test
    //Tests handling of tempo field, should return FIELD_TEMPO token
    public void tempotest() {
        String input = "Q: 200";
        ABCLexer lexer = new ABCLexer(input);
        assertEquals(Tokens.Type.FIELD_TEMPO,lexer.GetNextTokenType());
    }
    
    @Test
    //Tests handling of meter field, should return FIELD_METER token
    public void metertest() {
        String input = "M: 3/4";
        ABCLexer lexer = new ABCLexer(input);
        assertEquals(Tokens.Type.FIELD_METER,lexer.GetNextTokenType());
    }
    
    @Test
    //Tests handling of length field, should return FIELD_DEFAULT_LENGTH token
    public void lengthtest() {
        String input = "L: 1/4";
        ABCLexer lexer = new ABCLexer(input);
        assertEquals(Tokens.Type.FIELD_DEFAULT_LENGTH,lexer.GetNextTokenType());
    }
    
    @Test
    //Tests handling of key field, should return FIELD_KEY token
    public void keytest() {
        String input = "K: C";
        ABCLexer lexer = new ABCLexer(input);
        assertEquals(Tokens.Type.FIELD_KEY,lexer.GetNextTokenType());
    }
    
    @Test
    //Tests handling of composer field, should return FIELD_COMPOSER token
    public void composertest() {
        String input = "C: Skrillex";
        ABCLexer lexer = new ABCLexer(input);
        assertEquals(Tokens.Type.FIELD_COMPOSER,lexer.GetNextTokenType());
    }
    
    @Test
    //Tests handling of voice field, should return FIELD_VOICE token
    public void voicetest() {
        String input = "V: 2";
        ABCLexer lexer = new ABCLexer(input);
        assertEquals(Tokens.Type.FIELD_VOICE,lexer.GetNextTokenType());
    }

    @Test
    //Tests handling of notes
    public void notetest() {
        String inputs = "abcdefgABCDEFG";
        for (int i = 0; i < inputs.length(); i++) {
            String input = inputs.substring(i, i+1);
            ABCLexer lexer = new ABCLexer(input);
            assertEquals(Tokens.Type.NOTE,lexer.GetNextTokenType());
        }
    }
    
    @Test
    //Tests handling of rests
    public void resttest() {
        String[] inputs = {"z1/4", "z/4", "z/", "z", "z2", "z3", "z4", "z6", "z8", "z,1/4", "z,/4", "z,/", "z,", "z,2", "z,3", "z,4", "z,6", "z,8"};
        for (String input : inputs) {
            ABCLexer lexer = new ABCLexer(input);
            assertEquals(Tokens.Type.REST,lexer.GetNextTokenType());
        }
    }
    
    
    @Test
    //Tests handling of octaves
    public void octavetest() {
        String[] inputs = {"C,,,,,", "D,,,,", "E,,,", "F,,", "c,", "d", "e'", "f''", "G'''", "a''''"};
        for (String input : inputs) {
            ABCLexer lexer = new ABCLexer(input);
            assertEquals(Tokens.Type.NOTE,lexer.GetNextTokenType());
        }
    }
    
    @Test
    //Tests handling of note lengths
    public void notelengthtest() {
        String[] inputs = {"A1/4", "A/4", "A/", "A", "A2", "A3", "A4", "A6", "A8", "A,1/4", "A,/4", "A,/", "A,", "A,2", "A,3", "A,4", "A,6", "A,8"};
        for (String input : inputs) {
            ABCLexer lexer = new ABCLexer(input);
            assertEquals(Tokens.Type.NOTE,lexer.GetNextTokenType());
        }
    }
    
    @Test
    //Tests handling of accidentals
    public void accidentaltest() {
        String[] inputs = {"^A1/4", "^^A/4", "=A/", "_A", "__A2", "_A3", "^^A4", "^A6", "=A8", "_A,1/4", "__A,/4", "^A,/", "^^A,", "=A,2"};
        for (String input : inputs) {
            ABCLexer lexer = new ABCLexer(input);
            assertEquals(Tokens.Type.NOTE,lexer.GetNextTokenType());
        }
    }
    
    @Test
    //Tests handling of bar lines
    public void barlinetest() {
        String[] inputs = {"|", "||", "[|", "|]", ":|", "|:"};
        for (String input : inputs) {
            ABCLexer lexer = new ABCLexer(input);
            assertEquals(Tokens.Type.BARLINE,lexer.GetNextTokenType());
        }
    }
    
    @Test
    //Tests handling of chords
    public void chordtest() {
        String[] inputs = {"[A]", "[BA]", "[CAB]", "[Gaf]", "[_B/^A1/4=c^^D]"};
        for (String input : inputs) {
            ABCLexer lexer = new ABCLexer(input);
            assertEquals(Tokens.Type.CHORD,lexer.GetNextTokenType());
        }
    }

    @Test
    //Tests handling of tuplets
    public void tuplettest() {
        String[] inputs = {"(2AB", "(3GAB", "(4FGAB", "(2^^a=b/", "(2a/[Gaf]"};
        for (String input : inputs) {
            ABCLexer lexer = new ABCLexer(input);
            assertEquals(Tokens.Type.TUPLET,lexer.GetNextTokenType());
        }
    }
    

    @Test
    //Tests handling of comments
    public void commenttest() {
        String input = "%comment";
        ABCLexer lexer = new ABCLexer(input);
        assertEquals(Tokens.Type.COMMENT,lexer.GetNextTokenType());
    }
    
    @Test (expected = IllegalArgumentException.class)
    //Tests handling of invalid note, should throw IllegalArgumentException
    public void badnotetest() {
        String input = "h";
        ABCLexer lexer = new ABCLexer(input);
        lexer.GetNextTokenType();
    }
    
    @Test (expected = IllegalArgumentException.class)
    //Tests handling of invalid chord, should throw IllegalArgumentException
    public void badchordtest() {
        String input = "[]";
        ABCLexer lexer = new ABCLexer(input);
        lexer.GetNextTokenType();
    }
    
    
    @Test (expected = IllegalArgumentException.class)
    //Tests handling of invalid tuplet, should throw IllegalArgumentException
    public void badtuplettest() {
        String input = "(3ab(2ab";
        ABCLexer lexer = new ABCLexer(input);
        lexer.getNextNoteToken();
    }    
    
    @Test (expected = IllegalArgumentException.class)
    //Tests handling of invalid barline, should throw IllegalArgumentException
    public void invalidBarlineTest() {
        String input = "||||:::|";
        ABCLexer lexer = new ABCLexer(input);
        lexer.getNextElemToken();
        lexer.getNextElemToken();
        lexer.getNextElemToken();
        lexer.getNextElemToken();
        lexer.getNextElemToken();
        lexer.getNextElemToken();        
        
    }
    
    @Test (expected = IllegalArgumentException.class)
    //Tests handling of invalid rest, should throw IllegalArgumentException
    public  void invalidRestTest() {
        String input = "^z";
        ABCLexer lexer = new ABCLexer(input);
        lexer.getNextNoteToken();
    }
    
    @Test (expected = IllegalArgumentException.class)
    //Tests handling of invalid note, should throw IllegalArgumentException
    public  void invalidNoteTest() {
        String input = "^_B";
        ABCLexer lexer = new ABCLexer(input);
        lexer.getNextNoteToken();
    }
    
    @Test (expected = IllegalArgumentException.class)
    //Tests handling of invalid note, should throw IllegalArgumentException
    public  void invalidNoteTest2() {
        String input = "==B";
        ABCLexer lexer = new ABCLexer(input);
        lexer.getNextNoteToken();
    }
    
    @Test (expected = IllegalArgumentException.class)
    //Tests handling of invalid note, should throw IllegalArgumentException
    public  void invalidNoteTest3() {
    	String input = "*G";
        ABCLexer lexer = new ABCLexer(input);
        lexer.getNextNoteToken();
    }
    
    @Test (expected = IllegalArgumentException.class)
    //Tests handling of invalid compound note, should throw IllegalArgumentException
    public  void invalidCompoundNoteTest() {
        String input = "(3g//2ab4";
        ABCLexer lexer = new ABCLexer(input);
        lexer.getNextNoteToken();
    }
    
    @Test (expected = IllegalArgumentException.class)
    //Tests handling of invalid compound note, should throw IllegalArgumentException
    public  void invalidCompoundNoteTest2() {
        String input = "(4abc";
        ABCLexer lexer = new ABCLexer(input);
        lexer.getNextNoteToken();
    }
    
    @Test (expected = IllegalArgumentException.class)
    //Tests handling of invalid compound note, should throw IllegalArgumentException
    public  void invalidCompoundNoteTest3() {
        String input = "(5abced";
        ABCLexer lexer = new ABCLexer(input);
        lexer.getNextNoteToken();
    }

    @Test (expected = IllegalArgumentException.class)
    //Tests handling of invalid compound note, should throw IllegalArgumentException
    public  void invalidCompoundNoteTest4() {
        String input = "[A[Bc]e]";
        ABCLexer lexer = new ABCLexer(input);
        lexer.getNextNoteToken();
    }
    
    @Test (expected = IllegalArgumentException.class)
    //Tests handling of mismatched inputs and the corresponding build tokens, should throw IllegalArgumentException
    public  void invalidCall1() {
    	String input = "A";        
        ABCLexer lexer = new ABCLexer(input);
        lexer.getNextElemToken();        
    }
    
    @Test (expected = IllegalArgumentException.class)
    //Tests handling of mismatched inputs and the corresponding build tokens, should throw IllegalArgumentException
    public  void invalidCall2(){
    	String input = "(3abc";        
        ABCLexer lexer = new ABCLexer(input);
        lexer.getNextElemToken();        
    }

    @Test (expected = IllegalArgumentException.class)
    //Tests handling of mismatched inputs and the corresponding build tokens, should throw IllegalArgumentException
    public  void invalidCall3(){
    	String input = "[ab]";        
        ABCLexer lexer = new ABCLexer(input);
        lexer.getNextElemToken();        
    }
    
    @Test (expected = IllegalArgumentException.class)
    //Tests handling of mismatched inputs and the corresponding build tokens, should throw IllegalArgumentException
    public  void invalidCall4(){
    	String input = "z/3";        
        ABCLexer lexer = new ABCLexer(input);
        lexer.getNextElemToken();        
    }    
   

    
}