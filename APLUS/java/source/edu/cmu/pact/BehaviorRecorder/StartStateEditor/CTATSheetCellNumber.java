/**------------------------------------------------------------------------------------
 $Author: blojasie $ 
 $Date: 2012-05-31 11:09:39 -0400 (Thu, 31 May 2012) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.1  2011/06/10 12:44:01  vvelsen
 Added missing files and changed the w3c parser to the jdom parser.


 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.text.Document;

public class CTATSheetCellNumber extends JTextField
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected CTATNumberFieldFilter numberFieldFilter;
    
    /**
	 * 
	 */	    
    public CTATSheetCellNumber() 
    {
        this(10,CTATNumberFieldFilter.DECIMAL);
        numberFieldFilter = new CTATNumberFieldFilter();        
    }
    /**
	 * 
	 */	
    public CTATSheetCellNumber(int iMaxLen)
    {    	
        this(iMaxLen,CTATNumberFieldFilter.NUMERIC);
        numberFieldFilter = new CTATNumberFieldFilter();        
    }
    /**
	 * 
	 */	
    public CTATSheetCellNumber(int iMaxLen, int iFormat)
    {
    	if (numberFieldFilter==null)
            numberFieldFilter = new CTATNumberFieldFilter();
    	
        setMaxLength(iMaxLen);
        setFormat(iFormat);

        super.setDocument (numberFieldFilter);
    }
    /**
	 * 
	 */	
    public void setMaxLength(int maxLen)
    {
        if (maxLen > 0)
        	numberFieldFilter.maxLength = maxLen;
        else
        	numberFieldFilter.maxLength = 0;
    }
    /**
	 * 
	 */	
    public int getMaxLength()
    {
      return numberFieldFilter.maxLength;
    }
    /**
	 * 
	 */	
    public void setEnabled(boolean enable)
    {
        super.setEnabled(enable);

        if( enable )
        {
            setBackground(Color.white);
            setForeground(Color.black);
        }
        else
        {
            setBackground(Color.lightGray);
            setForeground(Color.darkGray);
        }
    }
    /**
	 * 
	 */	
    public void setEditable(boolean enable)
    {
        super.setEditable(enable);

        if( enable )
        {
            setBackground(Color.white);
            setForeground(Color.black);
        }
        else
        {
            setBackground(Color.lightGray);
            setForeground(Color.darkGray);
        }
    }
    /**
	 * 
	 */	
    public void setPrecision(int iPrecision)
    {
        if( numberFieldFilter.format == CTATNumberFieldFilter.NUMERIC )
            return;

        if (iPrecision >= 0)
        	numberFieldFilter.precision = iPrecision;
        else
        	numberFieldFilter.precision = CTATNumberFieldFilter.DEF_PRECISION;
    }
    /**
	 * 
	 */	
    public int getPrecision()
    {
      return numberFieldFilter.precision;
    }
    /**
	 * 
	 */	
    public Number getNumber()
    {
        Number number = null;
        if( numberFieldFilter.format == CTATNumberFieldFilter.NUMERIC )
            number = new Integer(getText());
        else
            number = new Double(getText());

        return number;
    }
    /**
	 * 
	 */	
    public void setNumber(Number value)
    {
        setText(String.valueOf(value));
    }
    /**
	 * 
	 */	
    public int getInt()
    {
        return Integer.parseInt(getText());
    }
    /**
	 * 
	 */	
    public void setInt(int value)
    {
        setText(String.valueOf(value));
    }
    /**
	 * 
	 */	
    public float getFloat()
    {
        return (new Float(getText())).floatValue();
    }
    /**
	 * 
	 */	
    public void setFloat(float value)
    {
        setText(String.valueOf(value));
    }
    /**
	 * 
	 */	
    public double getDouble()
    {
        return (new Double(getText())).doubleValue();
    }
    /**
	 * 
	 */	
    public void setDouble(double value)
    {
        setText(String.valueOf(value));
    }
    /**
	 * 
	 */	
    public int getFormat()
    {
        return numberFieldFilter.format;
    }
    /**
	 * 
	 */	
    public void setFormat(int iFormat)
    {
        switch(iFormat)
        {
            case CTATNumberFieldFilter.NUMERIC:
            default:
            	numberFieldFilter.format = CTATNumberFieldFilter.NUMERIC;
            	numberFieldFilter.precision = 0;
            	numberFieldFilter.allowedChars = CTATNumberFieldFilter.FM_NUMERIC;
                break;

            case CTATNumberFieldFilter.DECIMAL:
            	numberFieldFilter.format = CTATNumberFieldFilter.DECIMAL;
            	numberFieldFilter.precision = CTATNumberFieldFilter.DEF_PRECISION;
            	numberFieldFilter.allowedChars = CTATNumberFieldFilter.FM_DECIMAL;
                break;
        }
    }
    /**
	 * 
	 */	
    public void setAllowNegative(boolean b)
    {
    	numberFieldFilter.allowNegative = b;

        if( b )
        	numberFieldFilter.negativeChars = ""+CTATNumberFieldFilter.NEGATIVE;
        else
        	numberFieldFilter.negativeChars = CTATNumberFieldFilter.BLANK;
    }
    /**
	 * 
	 */	
    public boolean isAllowNegative()
    {
        return numberFieldFilter.allowNegative;
    }
    /**
	 * 
	 */	
    public void setDocument(Document document)
    {
    	
    }
}				
