package player;

import org.junit.Test;

/**
 * Visual checks to make sure headers and selections of music are being processed correctly.
 * Since the info is delivered via System.out, assertEquals is not necessary and thus is 
 * @category no_didit
 */
public class ABCParserMusicTests {
	

    @Test
    //Sanity check to make sure the header is parsed correctly
    public void sanityCheck1() {
        String input = "X:1\nT:Paddy O'Rafferty\nC:Trad.\nM:6/8\nQ:200\nK:D\naaaaaa|]";
        ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();	
    }
    
    @Test
    //Sanity check to make sure the header is parsed correctly
    public void sanityCheck2() {
        String input = "X:8628\nT:Prelude BWV 846 no. 1\nC:Johann Sebastian Bach\nM:4/4\nL:1/16\nQ:280\nV:1\nK:C\nV:1\nz2 Gc eGce z2 Gc eGce|]";
        ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();	
    }
    
    @Test
    //Sanity check on a short piece of music to enusre it is handling the notes correctly
    //Since the output from the toString method is different from input abc format, but only slightly, it is easier to leave this test
    //as a visual check rather than an assertEquals
    public void sanityCheck3() {
        String input = "X:1\nT:Simple scale\nC:Unknown\nM:4/4\nL:1/4\nQ:120\nK:C\nC D E F | G A B c | c B A G | F E D C |";
        ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		System.out.println(music.toString());
    }    
    
	@Test
	public void sanityCheck4(){
	    String input = "X: 1\nT:Piece No.1\nM:4/4\nL:1/4\nQ: 140\nK:C\nC C C3/4 D/4 E | E3/4 D/4 E3/4 F/4 G2 | (3c/c/c/ (3G/G/G/ (3E/E/E/ (3C/C/C/ | G3/4 F/4 E3/4 D/4 C2 |]";
        ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		System.out.println(music.toString());
	}
}
	

