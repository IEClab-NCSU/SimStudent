package edu.cmu.pact.miss;

import java.util.ArrayList;
import java.util.Collections;

/* Author: Tasmia
 *  This class finds near similar equations which are in edit distance 1.
 *  For example: given a string 3x+6 = 9, this class helps you find another equation that looks
 *  like 3x = 9, -3x+6 = 9, 3x-6 = 9, 3x+6 = -9. This class will come in handy when tutee and tutor
 *  brainstorm together to find a solution step.
 *  
 *  */

public class convertjessPredicatesToJavaClass {
	
	public static String upperCaseFirstLetterOmmittingDelimeter(String s, String delim) {
		if(s.contains(delim) == false) return "";
		String[] arr = s.split(delim);
		StringBuffer sb = new StringBuffer();

	    for (int i = 0; i < arr.length; i++) {
	        sb.append(Character.toUpperCase(arr[i].charAt(0)))
	            .append(arr[i].substring(1));
	    }
	    
	    return sb.toString().trim();
	}
	
	public static String getPredicatePart(String givenString) {
		String feature_name = "";
		if(!givenString.contains("not")) {
			if(givenString.contains("?"))
				feature_name = givenString.substring(0, givenString.indexOf("?"));
			if(feature_name.contains("("))
				feature_name = feature_name.replace("(", "");
			
			feature_name = feature_name.replace(" ", "");
		}
		else {
			givenString = givenString.replace("(", "");
			givenString = givenString.replace(")", "");
			givenString = givenString.trim();
			feature_name = givenString.split(" ")[1];
		}
		return feature_name;
	}
	
	public static String getVariablePart(String givenString) {
		String variable_name = "";
		if(givenString.contains("?"))
			variable_name = givenString.substring(givenString.indexOf("?"), givenString.indexOf(")"));
		
		return variable_name.trim();
	}
	
	public static String getPredicatePart(String givenString, HashMap predicate_args) {
		String feature_name = "";
		String args = "";
		if(givenString.contains("?")) {
			feature_name = givenString.substring(0, givenString.indexOf("?"));
			args = givenString.substring(givenString.indexOf("?"), givenString.indexOf(")"));
		}
		if(feature_name.contains("("))
			feature_name = feature_name.replace("(", "");
		
		feature_name = feature_name.replace(" ", "");
		predicate_args.put(feature_name, args);
		return feature_name;
	}
	
	public static String constructWmeElement(String args, ArrayList lhsPath) {
		if(args.trim().contains(" ")) return "";
		else {
			String wme = getNameSlot(lhsPath, args.trim());
			return wme;
		}
		
	}
	
	public static String getNameSlot(ArrayList lhsPath, String value) {
		/*
		 * input:
		lhsPath[i] = ?var1 <- (MAIN::problem (name init) (interface-elements ? ? ? ? ? ? ? ?var8 ? ? ?) (subgoals ) (done nil) (description nil))
		?var8 <- (MAIN::table (name dorminTable1) (columns ?var9))
		?var9 <- (MAIN::column (name dorminTable1_Column1) (cells ?var10 ? ? ? ? ?) (position 1) (description nil))
		?var10 <- (MAIN::cell (name dorminTable1_C1R1) (value "?val0&~nil") (description nil) (row-number 1) (column-number 1))
		value = ?val0	
			output: dorminTable1_C1R1
		 */
		for (int h = 0; h < lhsPath.size(); h++) {
			String path = (String) lhsPath.get(h);
			if(path.contains("MAIN::cell") && path.contains(value)) {
				String[] cell_info = path.split("MAIN::cell");
				// cell_info[1] contains
				// (name dorminTable1_C1R1) (value "?val0&~nil") (description nil) (row-number 1) (column-number 1))
				String cell_value = cell_info[1].substring(cell_info[1].indexOf("name")+4, cell_info[1].indexOf(")"));
				return cell_value;
			}
			else {
				return "";
			}
		
		}
		return "";
		
	}
}
