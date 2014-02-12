package player;

import static org.junit.Assert.*;

import org.junit.Test;

/*Notes on Testing Strategy
 * These test check every category of tokens to ensure their functions are correct   
 */
public class TokenTest {
	@Test
	//checking that a rest's token is correctly implemented
	public void restTest() {
		Tokens defaultToken = new Tokens();
		Tokens.NoteToken rest = defaultToken.new NoteToken(3,4);
		int[] ans = {3,4};
		assertEquals(Tokens.Type.REST, rest.getType());
		assertEquals('r',rest.getNote());
		assertEquals(0,rest.getOctave());
		assertEquals(0,rest.getAccidental());
		assertArrayEquals(ans, rest.getLength());		
	}
	
	@Test (expected = UnsupportedOperationException.class)
	//checking that getElts cannot be called on a rest token
	public void breakingRestTest() {
		Tokens defaultToken = new Tokens();
		Tokens.NoteToken rest = defaultToken.new NoteToken(3,4);
		rest.getElts();		
	}
	
	@Test
	//checking that a note's token is correctly implemented
	public void noteTest() {
		Tokens defaultToken = new Tokens();
		Tokens.NoteToken note = defaultToken.new NoteToken('g',1,-1,3,4);
		int[] ans = {3,4};
		assertEquals(Tokens.Type.NOTE, note.getType());
		assertEquals('g',note.getNote());
		assertEquals(1,note.getOctave());
		assertEquals(-1,note.getAccidental());
		assertArrayEquals(ans, note.getLength());		
	}
	
	@Test (expected = UnsupportedOperationException.class)
	//checking that getElts cannot be called on a note token
	public void breakingNoteTest() {
		Tokens defaultToken = new Tokens();
		Tokens.NoteToken note = defaultToken.new NoteToken('g',1,-1,3,4);
		note.getElts();		
	}
	
	@Test
	//checking that a compound note's token is correctly implemented
	public void compoundNoteTest() {
		Tokens defaultToken = new Tokens();
		Tokens.NoteToken note1 = defaultToken.new NoteToken('g',1,-1,3,4);
		Tokens.NoteToken note2 = defaultToken.new NoteToken('A',1,1,3,4);
		Tokens.NoteToken note3 = defaultToken.new NoteToken('b',2,1,3,4);
		Tokens.NoteToken[] noteList = {note1,note2,note3};
		Tokens.NoteToken note = defaultToken.new NoteToken(Tokens.Type.CHORD,1,2,noteList);
		int[] ans = {1,2};
		assertEquals(Tokens.Type.CHORD, note.getType());
		assertEquals('r',note.getNote());
		assertEquals(0,note.getOctave());
		assertEquals(3,note.getAccidental());
		assertArrayEquals(ans, note.getLength());	
		assertArrayEquals(noteList,note.getElts());
	}
	
	@Test
	//checking that a Title Token works
	//Title Tokens have the same format as Composer, Numbers, Comments, and Voices thus only 1 test is sufficient to ensure the code path works
	public void titleTokenTest() {
		Tokens defaultToken = new Tokens();
		Tokens.ElemToken title = defaultToken.new ElemToken(Tokens.Type.FIELD_TITLE,"Sonata");
		assertEquals(Tokens.Type.FIELD_TITLE, title.getType());
		assertEquals("Sonata",title.getText());				
	}
	
	@Test (expected = UnsupportedOperationException.class)
	//checking that certain non-applicable sub-functions cannot be called on a title token
	public void getKeyTitleTest() {
		Tokens defaultToken = new Tokens();
		Tokens.ElemToken title = defaultToken.new ElemToken(Tokens.Type.FIELD_TITLE,"Sonata");
		title.getKey();		
	}
	
	@Test (expected = UnsupportedOperationException.class)
	//checking that certain non-applicable sub-functions cannot be called on a title token
	public void getValsTitleTest() {
		Tokens defaultToken = new Tokens();
		Tokens.ElemToken title = defaultToken.new ElemToken(Tokens.Type.FIELD_TITLE,"Sonata");
		title.getVals();		
	}
	
	@Test
	//checking that a Meter Token works
	//Title Tokens have the same format as Barlines and Repeats and thus only 1 test is sufficient to ensure the code path works
	public void meterTokenTest() {
		Tokens defaultToken = new Tokens();
		Tokens.ElemToken meter = defaultToken.new ElemToken(Tokens.Type.FIELD_METER,4,4);
		assertEquals(Tokens.Type.FIELD_METER, meter.getType());
		int [] ans = {4,4};
		assertArrayEquals(ans,meter.getVals());				
	}
	
	@Test (expected = UnsupportedOperationException.class)
	//checking that certain non-applicable sub-functions cannot be called on a title token
	public void getKeyMeterTest() {
		Tokens defaultToken = new Tokens();
		Tokens.ElemToken meter = defaultToken.new ElemToken(Tokens.Type.FIELD_METER,4,4);
		meter.getKey();		
	}
	
	@Test (expected = UnsupportedOperationException.class)
	//checking that certain non-applicable sub-functions cannot be called on a title token
	public void getTextMeterTest() {
		Tokens defaultToken = new Tokens();
		Tokens.ElemToken meter = defaultToken.new ElemToken(Tokens.Type.FIELD_METER,4,4);
		meter.getText();		
	}
	
	@Test
	//checking that a Tempo Token works
	public void tempoTokenTest() {
		Tokens defaultToken = new Tokens();
		Tokens.ElemToken tempo = defaultToken.new ElemToken(200);
		assertEquals(Tokens.Type.FIELD_TEMPO, tempo.getType());
		int [] ans = {200,1};
		assertArrayEquals(ans,tempo.getVals());				
	}
	
	@Test (expected = UnsupportedOperationException.class)
	//checking that certain non-applicable sub-functions cannot be called on a title token
	public void getKeyTempoTest() {
		Tokens defaultToken = new Tokens();
		Tokens.ElemToken tempo = defaultToken.new ElemToken(200);
		tempo.getKey();		
	}
	
	@Test (expected = UnsupportedOperationException.class)
	//checking that certain non-applicable sub-functions cannot be called on a title token
	public void getTextTempoTest() {
		Tokens defaultToken = new Tokens();
		Tokens.ElemToken tempo = defaultToken.new ElemToken(200);
		tempo.getText();		
	}
	
	@Test
	//checking that a Key Token works
	public void KeyTokenTest() {
		Tokens defaultToken = new Tokens();
		Tokens.NoteToken keyNote = defaultToken.new NoteToken('g',1,-1,3,4);
		Tokens.ElemToken key = defaultToken.new ElemToken(keyNote);
		assertEquals(Tokens.Type.FIELD_KEY, key.getType());
		assertEquals(keyNote,key.getKey());				
	}
	
	@Test (expected = UnsupportedOperationException.class)
	//checking that certain non-applicable sub-functions cannot be called on a title token
	public void getTextKeyTest() {
		Tokens defaultToken = new Tokens();
		Tokens.NoteToken keyNote = defaultToken.new NoteToken('g',1,-1,3,4);
		Tokens.ElemToken key = defaultToken.new ElemToken(keyNote);
		key.getText();		
	}
	
	@Test (expected = UnsupportedOperationException.class)
	//checking that certain non-applicable sub-functions cannot be called on a title token
	public void getValsKeyTest() {
		Tokens defaultToken = new Tokens();
		Tokens.NoteToken keyNote = defaultToken.new NoteToken('g',1,-1,3,4);
		Tokens.ElemToken key = defaultToken.new ElemToken(keyNote);
		key.getText();		
	}
}
