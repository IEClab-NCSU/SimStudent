/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2011-06-10 08:44:01 -0400 (Fri, 10 Jun 2011) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

class CTATNumberFieldFilter extends PlainDocument
{    	        
	//private static final long serialVersionUID = 1L;
	public static final char DOT = '.';
	public static final char NEGATIVE = '-';
	public static final String BLANK = "";
	public static final int DEF_PRECISION = 2;

	public static final int NUMERIC = 2;
	public static final int DECIMAL = 3;

	public static final String FM_NUMERIC = "0123456789";
	public static final String FM_DECIMAL = FM_NUMERIC + DOT;

	public int maxLength = 0;
	public int format = NUMERIC;
	public String negativeChars = BLANK;
	public String allowedChars = null;
	public boolean allowNegative = false;
	public int precision = 0;
        
	private static final long serialVersionUID = 1L;

	public CTATNumberFieldFilter()
	{
		super();
	}

	public void insertString(int offset, String  str, AttributeSet attr) throws BadLocationException
	{
		String text = getText(0,offset) + str + getText(offset,(getLength() - offset));

		if( str == null || text == null)
			return;

		for(int i=0; i<str.length(); i++)
		{
			if((allowedChars+negativeChars).indexOf(str.charAt(i)) == -1)
				return;
		}

		int precisionLength = 0, dotLength = 0, minusLength = 0;
		int textLength = text.length();

		try
		{
			if( format == NUMERIC )
			{
				new Long(text);
			}
			else if( format == DECIMAL )
			{
				new Double(text);

				int dotIndex = text.indexOf(DOT);
				if( dotIndex != -1 )
				{
					dotLength = 1;
					precisionLength = textLength - dotIndex - dotLength;

					if( precisionLength > precision )
						return;
				}
			}
		}
		catch(Exception ex)
		{
			return;
		}

		if(text.startsWith(""+NEGATIVE) )
		{
			if( !allowNegative )
				return;
			else
				minusLength = 1;
		}

		if( maxLength < (textLength - dotLength - precisionLength - minusLength) )
			return;

		super.insertString(offset, str, attr);
	}
}
