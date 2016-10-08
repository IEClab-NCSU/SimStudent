/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATURLParamEncoder.java,v 1.1 2012/09/14 13:58:29 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATURLParamEncoder.java,v $
 Revision 1.1  2012/09/14 13:58:29  vvelsen
 Forgot to add two source files

 $RCSfile: CTATURLParamEncoder.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATURLParamEncoder.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.hcii.ctat;

/**
 * 
 */
public class CTATURLParamEncoder 
{
	/**
	 * 
	 */
    public static String encode(String input) 
	{
        StringBuilder resultStr = new StringBuilder();
        
        for (char ch : input.toCharArray()) 
        {
            if (isUnsafe(ch)) 
            {
                resultStr.append('%');
                resultStr.append(toHex(ch / 16));
                resultStr.append(toHex(ch % 16));
            } 
            else 
            {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }
	/**
	 * 
	 */
    private static char toHex(int ch) 
	{
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }
	/**
	 * 
	 */
    private static boolean isUnsafe(char ch) 
	{
        if (ch > 128 || ch < 0)
            return true;
        return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
    }

}
