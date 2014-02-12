package player;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Main entry point of your application.
 */
public class Main {
    /**
     * Plays the input file using Java MIDI API and displays
     * header information to the standard output stream.
     * 
     * (Your code should not exit the application abnormally using
     * System.exit().)
     * 
     * @param file the name of input abc file
     */
    public static void play(String file) {
       BufferedReader reader = null;
       try {
    	   reader = new BufferedReader( new FileReader (file) );
       } catch (FileNotFoundException e1) {
    	   e1.printStackTrace();
       }
       String         line = null;
       StringBuilder  stringBuilder = new StringBuilder();

        try {
        	while( ( line = reader.readLine() ) != null ) {
		        	stringBuilder.append( line );
		        	stringBuilder.append( "\n" );
		    	}
       	} catch (IOException e) {
		e.printStackTrace();
		}
        try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        String input = stringBuilder.toString();
		ABCLexer lexer = new ABCLexer(input);
		ABCParser parser = new ABCParser(lexer);
		ABCMusic music = parser.parse();
		music.DisplayInfo();
		music.PlayMusic();
    }

    /**
     * Plays a selected ABC file, if no file is selected plays the file at "./sample_abc/fur_elise.abc"
     */
    public static void main(String[] args) {
        
    	String wholeURL = "";
    	for(int i = 0; i < args.length;i++){
    		wholeURL = wholeURL + " " + args[i];
    	}
    	if(args.length == 0){
    	    play("./sample_abc/fur_elise.abc");
    	} else{
    	    play(wholeURL.replaceFirst(" ", ""));
    	}
    }

}
