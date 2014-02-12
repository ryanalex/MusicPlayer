package player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;


/** These are holistic test which ensure our entire system is robust and all the components work together. * 
 * The following sanity Checks are checking given/online abc files to make sure they play correctly
 * The Header is checked visually and the music is checked by ear
 * The toString result for each piece is also checked visually, once again since formatting is slightly different from the 
 * initial abc file, we can't use an automated check. Therefore:
 * @category no_didit
 */
public class ABCGenericTests {
	//Reader to pass the .abc files into the program	
	private String readFile( String file ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader (file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();

        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( "\n" );
        }
        reader.close();
        return stringBuilder.toString();
    }		

	@Test
	public void rowRowTest(){
	    String input = "";
        try {
             input = readFile("./sample_abc/piece1.abc");
        } catch (IOException e) {
            e.printStackTrace();
        }
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}
	
	@Test
	public void marioMusicTest(){
	    String input = "";
        try {
             input = readFile("./sample_abc/piece2.abc");
        } catch (IOException e) {
            e.printStackTrace();
        }
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}
	
	@Test
	
	public void littleNightTest(){
	    String input = "";
        try {
             input = readFile("./sample_abc/little_night_music.abc");
        } catch (IOException e) {
            e.printStackTrace();
        }
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}
	
	@Test 
	
	public void paddyTest(){
	    String input = "";
        try {
             input = readFile("./sample_abc/paddy.abc");
        } catch (IOException e) {
            e.printStackTrace();
        }
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}
	


	@Test
	public void furEliseTest(){
		String input = "";
		try {
			 input = readFile("./sample_abc/fur_elise.abc");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}
	
	@Test
	public void inventionTest(){
		String input = "";
		try {
			 input = readFile("./sample_abc/invention.abc");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}
	
	@Test
	public void preludeTest(){
		String input = "";
		try {
			 input = readFile("./sample_abc/prelude.abc");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}
	
	@Test
	public void scaleTest(){
		String input = "";
		try {
			 input = readFile("./sample_abc/scale.abc");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}
	
	@Test
	public void harryPotterIntroTest(){
		String input = "";
		try {
			 input = readFile("./sample_abc/harryp.abc");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}
	
	@Test
	public void smoothCriminalTest(){
		String input = "";
		try {
			 input = readFile("./sample_abc/smoothcriminal.abc");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}
	
	@Test
	public void waltzingTest(){
		String input = "";
		try {
			 input = readFile("./sample_abc/waltzing.abc");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}
	
	//Handwritten abc file #1. Tests Octaves, chords, repeats
	@Test
	public void userCreated1Test(){
		String input = "";
		try {
			 input = readFile("./sample_abc/user1.abc");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}

	
	//Handwritten abc file #2. Tests tuplets, rests, numbered repeats
	@Test
	public void userCreated2Test(){
		String input = "";
		try {
			 input = readFile("./sample_abc/user2.abc");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}
	
	//Handwritten abc file #3. Tests key signatures, various note lengths, comment and tempo changes
	@Test
	public void userCreated3Test(){
		String input = "";
		try {
			 input = readFile("./sample_abc/user3.abc");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}
	
	//Handwritten abc file #4. Tests multiple voices
	@Test
	public void userCreated4Test(){
		String input = "";
		try {
			 input = readFile("./sample_abc/user4.abc");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
		System.out.println(music.toString());
	}

}
	
	
