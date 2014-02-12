package player;

import static org.junit.Assert.assertEquals;


import org.junit.Test;

import player.ABCTerminalLexer.Terminals;

/*Notes on Testing Strategy
 * These cases test every possible terminal since they are the foundation for the grammar *  
 */

public class ABCTerminalLexerTests {
    @Test
    //Sanity check to make sure terminal Regexs work
    public void textWithSpaces() {
        String input = "askfld   j;dsj";
        ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
        assertEquals(true, lexer.IsItThere(Terminals.TEXT));
    }
    
    @Test
    //Sanity check to make sure terminal Regexs work
    public void digitsWithSpaces() {
        String input = "987  89";
        ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
        assertEquals(true, lexer.IsItThere(Terminals.DIGIT));
    }
    @Test
    //Sanity check to make sure terminal Regexs work
    public void basenotesWithSpaces() {
    	String[] inputs = {"A","B","C","D  ","E","F  ","G","a  ","b","c","d","e  ","f","g",};
    	for (String input: inputs){
    		ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
    		assertEquals(true, lexer.IsItThere(Terminals.BASENOTE));
    	}
    }
    @Test
    //Sanity check to make sure terminal Regexs work
    public void accidentalChecks() {
        String[] inputs = {"  ^^","^","  __","_","="};
        for (String input: inputs)
        {
        	ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
            assertEquals(true, lexer.IsItThere(Terminals.ACCIDENTAL));
        }
    }
    
    @Test
    //Sanity check to make sure terminal Regexs work
    public void minorWithSpaces() {
        String input = "m   ";
        ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
        assertEquals(true, lexer.IsItThere(Terminals.MODE_MINOR));
    }
    
    @Test    
    //Sanity check to make sure terminal Regexs work
    public void fractionCheck() {
        String input = "/";
        ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
        assertEquals(true, lexer.IsItThere(Terminals.FRACTION));
    }
    @Test
    //Sanity check to make sure terminal Regexs work
    public void keySigWithSpaces() {
    	String[] inputs = {"   b","  #"};
    	for (String input: inputs)
        {
    		ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
    		assertEquals(true, lexer.IsItThere(Terminals.KEY_ACCIDENTAL));
        }
    }
    @Test
    //Sanity check to make sure terminal Regexs work
    public void octaveCheck() {
        String[] inputs = {",,",",","'","''"};
        for (String input: inputs)
        {
        	ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
        	assertEquals(true, lexer.IsItThere(Terminals.OCTAVE));
        }
        
    }
    
    @Test
    //Sanity check to make sure terminal Regexs work
    public void restCheck() {
        String input = "z";
        ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
        assertEquals(true, lexer.IsItThere(Terminals.REST));
    }
    
        
    @Test 
    //Tests handling of mismatched terminals    
    public void falseTerminalTest() {
    	String[] inputs = {",,","z","2","/"};
    	for (String input:inputs){
    		ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);       
    		assertEquals(false, lexer.IsItThere(Terminals.BASENOTE));
    	}
    }
    //Testing the token specific subfunctions
    public void restSubFunction() {
        String input = "  z  ";
        ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
        lexer.IsItThere(Terminals.REST);        
        assertEquals("z",lexer.FetchRest());
    }
    
    //Testing the token specific subfunctions
    public void noteSubFunction() {
        String input = "  G  ";
        ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
        lexer.IsItThere(Terminals.BASENOTE);        
        assertEquals("G",lexer.FetchRest());
    }
    //Testing the token specific subfunctions
    public void accidentalSubFunction() {
        String input = "  __ ";
        ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
        lexer.IsItThere(Terminals.BASENOTE);        
        assertEquals(-2,lexer.FetchRest());
    }
    //Testing the token specific subfunctions
    public void octaveSubFunction() {
        String input = "  '' ";
        ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
        lexer.IsItThere(Terminals.BASENOTE);        
        assertEquals(2,lexer.FetchRest());
    }
    
    //Testing the token specific subfunctions
    public void minorSubFunction() {
        String input = "  m ";
        ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
        lexer.IsItThere(Terminals.MODE_MINOR);        
        assertEquals(-1,lexer.FetchRest());
    }

    public void keyAccidentalSubFunction() {
        String input = "  # ";
        ABCTerminalLexer lexer = new ABCTerminalLexer(input,0);
        lexer.IsItThere(Terminals.KEY_ACCIDENTAL);        
        assertEquals(1,lexer.FetchRest());
    }   


}
