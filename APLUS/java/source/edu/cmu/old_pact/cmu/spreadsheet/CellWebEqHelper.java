
package edu.cmu.old_pact.cmu.spreadsheet;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;
import edu.cmu.old_pact.html.library.WebEqImage;
import edu.cmu.old_pact.settings.ParameterSettings;
import edu.cmu.pact.Utilities.trace;


public class CellWebEqHelper {

	private static SymbolManipulator sm = new SymbolManipulator();
	private static Color fgColor;
		
	synchronized public static WebEqImage createWebEqImage(String text, Font font,//int fontSize,
										 Color fgc, Component cell)
	{
	
		sm.autoStandardize = false;
		sm.autoCombineLikeTerms = false;
		sm.setOutputType(SymbolManipulator.mathMLOutput);
		WebEqImage webEqImage = new WebEqImage();
		fgColor = fgc;
		String textWithMathML = convertExprToMathML(text,font);
		//synchronized(webEqImage){
			webEqImage.setPointsize(font.getSize());
			webEqImage.setForeColor(fgColor);
 			webEqImage.createEquation(textWithMathML, cell);
		//}
		  
 		//return webEqImage.getEqImage();  
 		return webEqImage;    			
    }        			
      			
    /*-----------------------------------------------------*/
	// convert the argument string into MathML 
	// (using symbol manipulator)
	// ASSUMPTION: arg string represents one wellformed expression
	/*-----------------------------------------------------*/
	private static String convertExprToMathML(String expr, Font font) {
		String res = "";
		String resultMathML ="";
		String style;
		
 		try {
		  	resultMathML = sm.noOpExprOrEquation(expr);
		  }
			catch (BadExpressionError err) {
				trace.out("SM error: can't convert to MathML...");
		  }	
  
   		  // FOR NOW (before 'pi' is handled properly by SM)
		  //   replace all occurances of 'pi' with '&pi;' (skip those with &;)	 		  
		  resultMathML = replaceSubstrings(resultMathML, 
						"<mi>p</mi> <mi>i</mi>", "<mn>&pi;</mn>");
		  
		  res = res + "<mstyle fontfamily='"+font.getFamily() + "'"+
		  		" fontweight='normal'" + " fontstyle='normal'"+
		  		" fontcolor='"+
		  		ParameterSettings.getWebColor(fgColor)+"'>"+ 
		  		resultMathML + "</mstyle>";
		  return res;
	}
	
		// replace all occurences of oldStr with newStr in str
	private static String replaceSubstrings(String str, String oldStr, String newStr){
		int ind = -1;
		String res = "";
	

		ind = str.indexOf(oldStr);
		if (ind == -1)
		  return str;
		
		while (ind != -1) {
		  	res = res + str.substring(0, ind) + newStr;
		  	str = str.substring(ind + oldStr.length());
		  	// find next oldStr
		  	ind = str.indexOf(oldStr);				 
		} // end while  
		
		res = res + str;
		return res;
	}
	
	
}	