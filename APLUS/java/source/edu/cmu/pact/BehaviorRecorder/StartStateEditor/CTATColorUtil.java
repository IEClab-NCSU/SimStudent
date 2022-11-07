/**------------------------------------------------------------------------------------
 $Author: blojasie $ 
 $Date: 2012-05-31 11:09:39 -0400 (Thu, 31 May 2012) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.3  2011/08/26 13:12:12  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.2  2011/07/19 19:54:32  vvelsen
 Major commit that now has all of the main start state editor functionality in place. It will take more finishing touches however. Message are sent when tables are changed but values aren't properly propagated yet from cell editors back into table cells. Also we need to add functionality to properly take in interface actions in the start state, map them to existing instances and show them in the SAI list.

 Revision 1.1  2011/05/31 20:11:16  vvelsen
 Fixed a package namespace problem. Added a couple of cell renderers and further refined the serialization classes.


 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

import edu.cmu.pact.Utilities.trace;

import java.awt.Color;

/**
 * This clas extends the color class and 
 * add features like valueOf to the color class
 * to convert HTML color format to a Color object.
 */
public class CTATColorUtil  
{
	/** 
	 * Parses the specified Color as a string. 
	 * @param representation of the color as a 24-bit integer, the format of the string can be either htmlcolor #xxxxxx or xxxxxx.
	 * @return the new color.
	 * @exception NumberFormatException if the format of the string does not comply with rules or illegal charater for the 24-bit integer as a string.
	 */
	public static Color parse(String nm ) throws NumberFormatException 
	{
		if (nm.equals("")==true)
			return (new Color (255,255,255));
	  
		if ( nm.startsWith("#") ) 
		{
			nm = nm.substring(1);
		}
    
		if ( nm.startsWith("0x") || nm.startsWith("0X")) 
		{
			nm = nm.substring(2);
		} 
       
		nm = nm.toLowerCase();
		
		/*
		if (nm.length() > 6) 
		{
			throw new NumberFormatException("nm is not a 24 bit representation of the color, string too long"); 
		}
		 */
		
		trace.out("nm=" + nm );
		Color color=new Color(Integer.parseInt(nm,16));
		return color;
	} 
	/**
	 *
	 */   	
	public static String toHex (Color aColor)
	{
		return (Integer.toHexString(aColor.getRGB() & 0x00ffffff));
	}
}


