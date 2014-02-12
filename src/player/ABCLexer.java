package player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import player.ABCTerminalLexer.Terminals;
import player.Tokens.ElemToken;
import player.Tokens.NoteToken;

public class ABCLexer {
	// Storage for the input abc file
	private String input;
	// Location in the abc file
	private int position;
	// Mapping of terminals to the appropriate regexes
	private Map<Tokens.Type, String> regex = new HashMap<Tokens.Type, String>();
	// The initial string combo used to compile token values
	private String lookFor;
	private Tokens defaultToken = new Tokens();
	private Tokens.Type currentTokenType;

	/**
	 * Creates a Lexer that identifies major Terminal and NonTerminal sequences
	 * as described by the ABC Grammar subset understood by the program
	 * 
	 * @param input
	 *            , the string upon which to build an ABCLexer
	 */
	public ABCLexer(String input) {
		this.input = input;
		this.position = 0;
		BuildRegex();
	}

	/**
	 * Creates a Mapping between Terminals/NonTerminals for the ABC Grammar of
	 * the Lexer and a regular expression that represents that sequence
	 * 
	 * Note:A fair few of these are dense, and many rely on greed initialization
	 * and negative lookahead to function properly, tread lightly if edited
	 * 
	 * (Also, Escape Characters must be escaped to survive Java.String, thus the
	 * double escapes)
	 */
	private void BuildRegex() {
		regex.put(Tokens.Type.BARLINE,
				"((\\|[\\]\\|:])|(\\[\\|)|(:\\|)|(\\|(?!(\\[[12]))))");
		regex.put(Tokens.Type.CHORD, "\\[[A-Ga-g\\^_=0-9,'/z]+\\]");
		regex.put(Tokens.Type.COMMENT, "%[\\w \\t\\p{Punct}]*+");
		regex.put(Tokens.Type.END_OF_LINE, "[\\n\\r]");
		regex.put(Tokens.Type.FIELD_COMPOSER, "C:[\\w \\t\\p{Punct}]++");
		regex.put(Tokens.Type.FIELD_DEFAULT_LENGTH, "L:[ ]*+[0-9]+/[0-9]+");
		regex.put(Tokens.Type.FIELD_KEY, "K:[ ]*+[A-Ga-g][m]?[#b]?");
		regex.put(Tokens.Type.FIELD_METER, "M:[ ]*+((C\\|?)|(\\d+?/\\d+?))");
		regex.put(Tokens.Type.FIELD_NUMBER, "X:[ ]*+[0-9]++");
		regex.put(Tokens.Type.FIELD_TEMPO, "Q:[ ]*+[0-9]++");
		regex.put(Tokens.Type.FIELD_TITLE, "T:[\\w \\t\\p{Punct}]++");
		regex.put(Tokens.Type.FIELD_VOICE, "V:[\\w \\t\\p{Punct}]++");
		regex.put(Tokens.Type.NOTE,
				"(\\^{1,2}+|_{1,2}+|=)?[A-Ga-g](,++|'++)?+([0-9]*/?[0-9]*)?+");
		regex.put(Tokens.Type.REPEAT, "\\|?+\\[[12]");
		regex.put(Tokens.Type.REST, "z[0-9]*/?[0-9]*");
		regex.put(Tokens.Type.TUPLET, "\\([234][A-Ga-g\\^_=0-9,'/z]++");
	}

	/**
	 * Without incrementing the ABCLexer, determine the next token type that
	 * would be encountered to avoid race conditions with Maps and due to the
	 * nature of newlines in Java.String, it must search for EoL prior to
	 * utilizing the Map to search for all other Token types
	 * 
	 * @return A Tokens.Type corresponding to the next Token available from the
	 *         ABCLexer
	 */
	public Tokens.Type GetNextTokenType() {
		// For all Types of tokens
		if (IsDone()) {
			return Tokens.Type.END_OF_LINE;

		}
		lookFor = "^.{" + position + "}\\s*"
				+ regex.get(Tokens.Type.END_OF_LINE) + ".*";
		if (Pattern.compile(lookFor, 32).matcher(input).matches()) {
			return Tokens.Type.END_OF_LINE;
		}

		for (Tokens.Type key : Tokens.Type.values()) {
			// Build a regex pattern that ignores 'position' characters before
			// And then checks for that type of token
			lookFor = "^.{" + position + "}\\s*" + regex.get(key) + ".*";
			// Return a successful match
			// Compile a matcher where . characters match newLine characters
			if (Pattern.compile(lookFor, 32).matcher(input).matches()) {
				return key;
			}
		}
		// If no matches, then throw bad input exception
		throw new IllegalArgumentException(
				"Bad input found at starting at position: " + position);
	}

	/**
	 * Sees if the ABCLexer has been exhausted of inputs
	 * 
	 * @return A Boolean representing if the ABCLexer has lexer'd all of its
	 *         input
	 */
	public boolean IsDone() {
		return (position >= input.length());
	}

	/**
	 * Finds and Isolates the NonTerminal String that matched the last check for
	 * next token type Depends on a prior use of GetNextTokensType() as well as
	 * IsDone() = FALSE to succeed
	 * 
	 * @return A String representing the match for the desired NonTerminal
	 *         Character with leading whitespace removed
	 */
	private String GetNonTerminalString() {
		Pattern seek = Pattern.compile(
				lookFor.substring(0, (lookFor.length() - 2)), 32);
		Matcher find = seek.matcher(input);
		find.find();
		String value = find.group().substring(position);
		position += value.length();
		return value.replaceAll("^\\s", "");
	}

	/**
	 * @return A NoteToken representing a Rest
	 */
	private NoteToken BuildRestToken() {
		String nonTerminal = GetNonTerminalString();
		ABCTerminalLexer terminalCollector = new ABCTerminalLexer(nonTerminal,
				1);
		// Construct a terminal lexer
		// Get the variables for timing necessary
		int numerator, denominator;
		// See what vars are supplied and set values appropriately
		if (terminalCollector.IsItThere(Terminals.DIGIT)) {
			numerator = terminalCollector.FetchDigit();
		} else {
			numerator = 1;
		}

		if (terminalCollector.IsItThere(Terminals.FRACTION)) {
			terminalCollector.FetchFraction();
			if (terminalCollector.IsItThere(Terminals.DIGIT)) {
				denominator = terminalCollector.FetchDigit();
			} else {
				denominator = 2;
			}
		} else {
			denominator = 1;
		}
		// Return the new rest token
		return defaultToken.new NoteToken(numerator, denominator);
	}

	/**
	 * @return A NoteToken representing a Note
	 */
	private NoteToken BuildNoteToken() {
		String nonTerminal = GetNonTerminalString();
		ABCTerminalLexer terminalCollector = new ABCTerminalLexer(nonTerminal,
				0);
		// Construct a terminal lexer
		// Get the variables for timing necessary
		int numerator, denominator, octave, accidental;
		char note;
		// See what vars are supplied and set values appropriately

		if (terminalCollector.IsItThere(Terminals.ACCIDENTAL)) {
			accidental = terminalCollector.FetchAccidental();
		} else {
			// MinValue is the dummy used to represent no accidental
			accidental = Integer.MIN_VALUE;
		}

		if (terminalCollector.IsItThere(Terminals.BASENOTE)) {
			note = terminalCollector.FetchBasenote();
		} else {
			// MinValue is the dummy used to represent no accidental
			throw new IllegalArgumentException(
					"Somehow recieved note without a basenote");
		}

		if (terminalCollector.IsItThere(Terminals.OCTAVE)) {
			octave = terminalCollector.FetchOctave();
		} else {
			octave = 0;
		}

		if (terminalCollector.IsItThere(Terminals.DIGIT)) {
			numerator = terminalCollector.FetchDigit();
		} else {
			numerator = 1;
		}

		if (terminalCollector.IsItThere(Terminals.FRACTION)) {
			terminalCollector.FetchFraction();
			if (terminalCollector.IsItThere(Terminals.DIGIT)) {
				denominator = terminalCollector.FetchDigit();
			} else {
				denominator = 2;
			}
		} else {
			denominator = 1;
		}

		// Return the new note token
		return defaultToken.new NoteToken(note, octave, accidental, numerator,
				denominator);
	}

	/**
	 * @return A NoteToken representing a Chord or Tuplet
	 * 
	 * Note: Tuplets may contain Chords and Notes, but not other Tuplets, this is an assumption of the system to avoid
	 * over quantization of time which would distort the music
	 * 
	 * Note: Chords may only contain notes, this is a musical assumption as nested chords are just one chord, and tuplets cannot
	 * be a part of chords, also assumes that all notes in a chord will start at the same time if of different length
	 */
	private NoteToken BuildCompoundNoteToken() {
		currentTokenType = GetNextTokenType();
		String nonTerminal = GetNonTerminalString().replaceAll("\\s", "");
		int numerator, denominator;
		// Grab the nonTerminal string, then
		// Check to see what type of compound we are dealing with
		// The recursively build the appropriate list
		if (nonTerminal.charAt(0) == '(') {
			ABCLexer miniLex = new ABCLexer(nonTerminal.substring(2));
			NoteToken[] noteList;
			// Quickly determine timing of tuplet based on internal notes
			switch (nonTerminal.charAt(1)) {
			// Duplets 2 => 3
			case '2':
				noteList = new NoteToken[2];
				for (int i = 0; i < 2; i++) {
					miniLex.GetNextTokenType();
					if (miniLex.GetNextTokenType() == Tokens.Type.NOTE
							|| miniLex.GetNextTokenType() == Tokens.Type.CHORD) {
						noteList[i] = miniLex.getNextNoteToken();
					} else {
						throw new IllegalArgumentException(
								"Tuplets cannot contain Tokens of Type: "
										+ miniLex.GetNextTokenType());
					}
				}
				numerator = 3;
				denominator = 2;
				return defaultToken.new NoteToken(currentTokenType, numerator,
						denominator, noteList);
				// Triplet 3 => 2
			case '3':
				noteList = new NoteToken[3];
				for (int i = 0; i < 3; i++) {
					miniLex.GetNextTokenType();
					if (miniLex.GetNextTokenType() == Tokens.Type.NOTE
							|| miniLex.GetNextTokenType() == Tokens.Type.CHORD) {
						noteList[i] = miniLex.getNextNoteToken();
					} else {
						throw new IllegalArgumentException(
								"Tuplets cannot contain Tokens of Type: "
										+ miniLex.GetNextTokenType());
					}
				}
				numerator = 2;
				denominator = 3;
				return defaultToken.new NoteToken(currentTokenType, numerator,
						denominator, noteList);
				// Quadruplet 4 => 3
			case '4':
				noteList = new NoteToken[4];
				for (int i = 0; i < 4; i++) {
					miniLex.GetNextTokenType();
					if (miniLex.GetNextTokenType() == Tokens.Type.NOTE
							|| miniLex.GetNextTokenType() == Tokens.Type.CHORD) {
						noteList[i] = miniLex.getNextNoteToken();
					} else {
						throw new IllegalArgumentException(
								"Tuplets cannot contain Tokens of Type: "
										+ miniLex.GetNextTokenType());
					}
				}
				numerator = 3;
				denominator = 4;
				return defaultToken.new NoteToken(currentTokenType, numerator,
						denominator, noteList);
			default:
				throw new IllegalArgumentException(
						"Malfored Tuple does not have a first element, or is not of size 2,3, or 4");
			}
		} else {
			ABCLexer miniLex = new ABCLexer(nonTerminal.substring(1,
					(nonTerminal.length() - 1)));
			List<NoteToken> noteList = new ArrayList<NoteToken>();
			while (!miniLex.IsDone()) {
				miniLex.GetNextTokenType();
				if (miniLex.GetNextTokenType() == Tokens.Type.NOTE) {
					noteList.add(miniLex.getNextNoteToken());
				} else {
					throw new IllegalArgumentException(
							"Chords cannot contain Tokens of Type: "
									+ miniLex.GetNextTokenType());
				}
			}
			// All notes in a chord should be the same length
			numerator = noteList.get(0).getLength()[0];
			denominator = noteList.get(0).getLength()[1];
			NoteToken[] retList = new NoteToken[noteList.size()];
			return defaultToken.new NoteToken(currentTokenType, numerator,
					denominator, noteList.toArray(retList));
		}
	}

	/**
	 * @return A NoteToken representing the Type found by GetNextTokenType
	 */
	public NoteToken getNextNoteToken() {
		Tokens.Type currentTokenType = GetNextTokenType();
		switch (currentTokenType) {
		case REST:
			return BuildRestToken();		
		case NOTE:
			return BuildNoteToken();
		case TUPLET:
		case CHORD:
			return BuildCompoundNoteToken();
		default:
			throw new IllegalArgumentException(
					"Cannot construct NoteToken from: " + currentTokenType);
		}
	}

	/**
	 * @return An ElemToken that contains a String
	 */
	private ElemToken BuildStringElemToken() {
		String nonTerminal = GetNonTerminalString();
		// Skip the appropriate number of begining elts to capture what matters
		ABCTerminalLexer terminalCollector;
		if (nonTerminal.charAt(0) == '%') {
			terminalCollector = new ABCTerminalLexer(nonTerminal, 1);
		} else {
			terminalCollector = new ABCTerminalLexer(nonTerminal, 2);
		}
		// The variables used to collect the text
		String text;
		// Check and collect for Text
		if (terminalCollector.IsItThere(Terminals.TEXT)) {
			text = terminalCollector.FetchText();
		} else {
			text = "Field Was Blank";
		}

		return defaultToken.new ElemToken(currentTokenType, text);
	}

	/**
	 * @return An ElemToken that contains a Fraction
	 */
	private ElemToken BuildNumberElemToken() {
		String nonTerminal = GetNonTerminalString();
		ABCTerminalLexer terminalCollector = new ABCTerminalLexer(nonTerminal,
				2);

		int numerator, denominator;

		if (nonTerminal.matches(".*C(?!\\|).*")) {
			return defaultToken.new ElemToken(currentTokenType, 4, 4);
		} else if (nonTerminal.matches(".*C\\|.*")) {
			return defaultToken.new ElemToken(currentTokenType, 2, 2);
		} else {
			if (terminalCollector.IsItThere(Terminals.DIGIT)) {
				numerator = terminalCollector.FetchDigit();
			} else {
				throw new IllegalArgumentException(
						"Somehow recieved meter or length without a numerator");
			}

			if (terminalCollector.IsItThere(Terminals.FRACTION)) {
				terminalCollector.FetchFraction();
				if (terminalCollector.IsItThere(Terminals.DIGIT)) {
					denominator = terminalCollector.FetchDigit();
				} else {
					throw new IllegalArgumentException(
							"Somehow recieved meter or length without a denominator");
				}
			} else {
				throw new IllegalArgumentException(
						"Somehow recieved meter or length without a fraction bar");
			}
			return defaultToken.new ElemToken(currentTokenType, numerator,
					denominator);
		}
	}

	/**
	 * @return An ElemToken that contains a Barline Character
	 */
	private ElemToken BuildBarlineToken() {
		// Kill spaces on barlines
		String nonTerminal = GetNonTerminalString().replaceAll("\\s", "");
		if (nonTerminal.length() == 1) {
			return defaultToken.new ElemToken(currentTokenType, 1, 1);
		} else {
			switch (nonTerminal.charAt(1)) {
			case ']':
				return defaultToken.new ElemToken(currentTokenType, 2, 1);
			case '|':
				if (nonTerminal.charAt(0) == '|') {
					return defaultToken.new ElemToken(currentTokenType, 2, 1);
				} else if (nonTerminal.charAt(0) == ':') {
					return defaultToken.new ElemToken(currentTokenType, 3, 1);
				} else if (nonTerminal.charAt(0)== '['){
					return defaultToken.new ElemToken(currentTokenType, 5, 1);
				}else{
					throw new IllegalArgumentException(
							"Somehow recieved a Barline with invalid matchings");
					}
			case ':':
				return defaultToken.new ElemToken(currentTokenType, 4, 1);
			default:
				throw new IllegalArgumentException(
						"Somehow recieved a Barline with invalid matchings");
			}
		}

	}

	/**
	 * @return An ElemToken that contains a Repeat Character
	 */
	private ElemToken BuildRepeatToken() {
		String nonTerminal = GetNonTerminalString();
		if (nonTerminal.matches(".*1.*")) {
			return defaultToken.new ElemToken(currentTokenType, 1, 1);
		} else {
			return defaultToken.new ElemToken(currentTokenType, 2, 1);
		}
	}

	/**
	 * @return An ElemToken that contains a Key Signature
	 */
	private ElemToken BuildKeyToken() {
		String nonTerminal = GetNonTerminalString();
		ABCTerminalLexer terminalCollector = new ABCTerminalLexer(nonTerminal,
				2);
		// Construct a terminal lexer
		// Get the variables for timing necessary
		int octave, accidental;
		char note;
		// Grab the keynote
		if (terminalCollector.IsItThere(Terminals.BASENOTE)) {
			note = terminalCollector.FetchBasenote();
		} else {
			throw new IllegalArgumentException(
					"Somehow recieved keynote without a basenote");
		}
		// now the accidental
		if (terminalCollector.IsItThere(Terminals.KEY_ACCIDENTAL)) {
			accidental = terminalCollector.FetchKeyAccidental();
		} else {
			accidental = 0;
		}
		// finally get the minor or not
		if (terminalCollector.IsItThere(Terminals.MODE_MINOR)) {
			octave = terminalCollector.FetchModeMinor();
		} else {
			octave = 0;
		}
		return defaultToken.new ElemToken(defaultToken.new NoteToken(note,
				octave, accidental, 1, 1));
	}

	/**
	 * @return An ElemToken that contains a Tempo
	 */
	private ElemToken BuildTempoToken() {
		String nonTerminal = GetNonTerminalString();
		ABCTerminalLexer terminalCollector = new ABCTerminalLexer(nonTerminal,
				2);
		int numerator;
		if (terminalCollector.IsItThere(Terminals.DIGIT)) {
			numerator = terminalCollector.FetchDigit();
		} else {
			throw new IllegalArgumentException(
					"Somehow recieved tempo without a bpm");
		}
		return defaultToken.new ElemToken(numerator);
	}

	/**
	 * @return An ElemToken representing the Type found by GetNextTokenType
	 */
	public ElemToken getNextElemToken() {
		Tokens.Type currentTokenType = GetNextTokenType();
		switch (currentTokenType) {
		case FIELD_TITLE:
		case FIELD_COMPOSER:
		case FIELD_NUMBER:
		case FIELD_VOICE:
		case COMMENT:
			return BuildStringElemToken();
		case FIELD_DEFAULT_LENGTH:
		case FIELD_METER:
			return BuildNumberElemToken();
		case BARLINE:
			return BuildBarlineToken();
		case REPEAT:
			return BuildRepeatToken();
		case FIELD_KEY:
			return BuildKeyToken();
		case FIELD_TEMPO:
			return BuildTempoToken();
		default:
			throw new IllegalArgumentException(
					"Cannot construct ElemToken from: " + currentTokenType);
		}
	}

	/**
	 * @return An EoL Token iff Type found by GetNextTokenType == EoL
	 */
	public Tokens getEndOfLineToken() {
		Tokens.Type currentTokenType = GetNextTokenType();
		if (currentTokenType == Tokens.Type.END_OF_LINE) {
			position += 1;
			return new Tokens();
		} else {
			throw new IllegalArgumentException(
					"Cannot construct End of Line Token from: "
							+ currentTokenType);
		}
	}

}
