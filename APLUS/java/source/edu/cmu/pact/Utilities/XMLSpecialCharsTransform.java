package edu.cmu.pact.Utilities;


/////////////////////////////////////////////////////////////////////////
/**	
  * Author: zzhang
  * 
  *  This class is designed to transform XML special chars
  *  for reading and writing stringns in XML format file
  *  in the general stream without the SAXParser.
*/
/////////////////////////////////////////////////////////////////////////

public class XMLSpecialCharsTransform {
	// xml format special chars replaces
    final static String     AMP_REPLACE = "&amp;";
    final static String     LESS_THAN_REPLACE = "&lt;";
    final static String     GREATER_THAN_REPLACE = "&gt;";
	final static String     QUOTE_REPLACE = "&quot;";
	
	public XMLSpecialCharsTransform () {};
	
	
	/**
	 * 	Used to write string into the xml format file.
	 * 
     * 	@param str: the general text string
     * 
     * 	@return: the xml format string with special chars are replaced.
     */
	  //
	  // Ecoding special char '&' to '&amp;', but ignore already encode char which start with '&#'.
	  // 
	   public static String EncodeSpecialChar_amp(String str) {
			int index = -1;           
			String strA = null, strB = null; 
	               
	         //       if (str == null) return str;
			index = str.indexOf('&'); // search for special char '&'
	                if (index == -1) return str; // if no '&' in the string, no nothing
	                else if (index == str.length() - 1) return str.replaceFirst("\\&", "&amp;"); // '&' is the last char, just replace it
			else if (str.charAt(index+1) != '#') // '&' is not part of an encoded char, so encode it to '&amp;'
	                {   
	                    strA = str.substring(0, index + 1);
	                    strB = str.substring(index + 1);
	                    strA = strA.replaceFirst("\\&", "&amp;");
	                }
	                else // '&' is part of the encoded char &#xxx , so skip '#' and the decimal code of the character.
	                {
	                 strA = str.substring(0, index + 2);
	                 strB = str.substring(index + 2);    
	                }
	                 
	                return strA + EncodeSpecialChar_amp(strB);
		}
	   
	public static String transformSpecialChars(String str) {
        
        if (str == null)
            return str;
       
        str = EncodeSpecialChar_amp(str);
    //    str = str.replaceAll("\\&", AMP_REPLACE);
        str = str.replaceAll("\\<", LESS_THAN_REPLACE);
        str = str.replaceAll("\\>", GREATER_THAN_REPLACE);
        str = str.replaceAll("\\\"", QUOTE_REPLACE);
        return str;
    }

	/**
	 * 	Used to transform back from the xml format string without the SAXParser.
	 * 
     * 	@param str: the xml format text string with special chars are replaced.
     * 
     * 	@return: the general format string.
     */
	
    public static String transformBackSpecialChars(String str) {
        if (str == null)
            return str;
        
        str = str.replaceAll(AMP_REPLACE, "&");
        str = str.replaceAll(LESS_THAN_REPLACE, "<");
        str = str.replaceAll(GREATER_THAN_REPLACE, ">");
        str = str.replaceAll(QUOTE_REPLACE, "\"");
        
        return str;
    }

}
