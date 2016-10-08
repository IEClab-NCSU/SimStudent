
package pact.CommWidgets;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class JCommDocument extends PlainDocument {

	public boolean locked;

	public void insertString(int offset, String s, AttributeSet as) throws BadLocationException {
		if (locked) 
			return;
		
		super.insertString (offset, s, as);
	
	}
	
	public void remove(int offset, int len) throws BadLocationException {
		if (locked) 
			return;
		
		super.remove (offset, len);
	}
	
//	public Position createPosition(int offset) throws BadLocationException {return super.createPosition(offset);} 
	
}
