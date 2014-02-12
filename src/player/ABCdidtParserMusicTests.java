package player;
import org.junit.Test;
public class ABCdidtParserMusicTests {
	
	/*Notes on Testing Strategy
	 * These are all invalid input tests since these check every possible code path. 
	 * It is difficult to match strings without depending on the lexer, thus we checked strings by eye. Those tests are in the nondidit class.
	 * Finally, since the holistic tests also test the parser, we felt the parser-specific tests should, in the interest of 
	 * isolation be composed of invalid input tests.   
	 */
	
	@Test (expected = IllegalArgumentException.class)
    //making sure invalid inputs throw the correct exceptions
    public void tooManyNotesInAMeasure() {
        String input = "X:1\nT:Paddy O'Rafferty\nC:Trad.\nM:6/8\nQ:200\nK:D\naaaaaaa|]";
        ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
    }
	
	@Test (expected = IllegalArgumentException.class)
    //making sure invalid inputs throw the correct exceptions
    public void missingXField() {
        String input = "T:Paddy O'Rafferty\nC:Trad.\nM:6/8\nQ:200\nK:D\naaaaaa|]";
        ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
    }
	
	@Test (expected = IllegalArgumentException.class)
    //making sure invalid inputs throw the correct exceptions
    public void negativeTempo() {
        String input = "X: 1\nT:Paddy O'Rafferty\nC:Trad.\nM:6/8\nQ:-200\nK:D\naaaaaa|]";
        ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
    }
	
	@Test (expected = IllegalArgumentException.class)
    //making sure invalid inputs throw the correct exceptions
    public void missingTitle() {
        String input = "X: 1\nC:Trad.\nM:6/8\nQ:200\nK:D\naaaaaa|]";
        ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
    }
	
	@Test (expected = IllegalArgumentException.class)
    //making sure invalid inputs throw the correct exceptions
    public void doubleNatural() {
        String input = "X:1\nT:Paddy O'Rafferty\nC:Trad.\nM:6/8\nQ:200\nK:D\naaaaaa==a|]";
        ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
    }
	@Test (expected = IllegalArgumentException.class)
    //making sure invalid inputs throw the correct exceptions
    public void tripleSharp() {
        String input = "X:1\nT:Paddy O'Rafferty\nC:Trad.\nM:6/8\nQ:200\nK:D\naaaa^^^aaa|]";
        ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
    }
	
	@Test (expected = IllegalArgumentException.class)
    //making sure invalid inputs throw the correct exceptions
    public void accidentalChord() {
        String input = "X:1\nT:Paddy O'Rafferty\nC:Trad.\nM:6/8\nQ:200\nK:D\n^[abcdefg]aaaaa|]";
        ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
    }
	
	@Test (expected = IllegalArgumentException.class)
    //making sure invalid inputs throw the correct exceptions
    public void chordWithTuples() {
        String input = "X:1\nT:Paddy O'Rafferty\nC:Trad.\nM:6/8\nQ:200\nK:D\n[ab(3cde fg]aaaaa|]";
        ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
    }
	
	@Test (expected = IllegalArgumentException.class)
    //making sure invalid inputs throw the correct exceptions
    public void tupleWithChords() {
        String input = "X:1\nT:Paddy O'Rafferty\nC:Trad.\nM:6/8\nQ:200\nK:D\n(2[ab]a aaa|]";
        ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
    }

	@Test (expected = IllegalArgumentException.class)
    //making sure invalid inputs throw the correct exceptions
    public void nestedChords() {
        String input = "X:1\nT:Paddy O'Rafferty\nC:Trad.\nM:6/8\nQ:200\nK:D\n[aa[bb]] aaa aa |]";
        ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
    }

}
