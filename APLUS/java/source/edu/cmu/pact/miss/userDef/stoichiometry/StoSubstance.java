package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * StoSubstance - Reid Van Lehn <rvanlehn@mit.edu>
 * Represents a substance in stoichiometry
 * Contains methods for breaking down into atomic components
 * for determining molar ratios. 
 */

public class StoSubstance {

	private Vector components; 
	private String substanceName;
	
	public StoSubstance(String name) {
		components = decomposeSubstance(name);
		substanceName = name; //when decomposition isn't useful
	}
	
	/** 
	 * Parses substance into individual atoms with associated molar ratio.
	 * Looks for substrings of form 'letter' 'numbers' 
	 * Looks for anything of form X#(ABC)# or similar
	 * 
	 * TODO: Rig it so that adding same element sums number in array (i.e. H2(OH)3 
	 * has 5 H atoms).
	 * @param substance
	 */
	private Vector decomposeSubstance(String substance) {
		Vector parsed = new Vector();
		//String regex = "\\([A-Za-z0-9]+\\)[0-9]*|[A-Z][a-z]?[0-9]*";
		String polyAtomic = "\\([A-Za-z0-9]+\\)[0-9]*"; 
		String regular =  "[A-Z][a-z]?[0-9]*";
		String lettersRegex = "[A-Z][a-z]?";
		Pattern poly = Pattern.compile(polyAtomic);
		Pattern reg = Pattern.compile(regular);
		Pattern letPat = Pattern.compile(lettersRegex);
		//During first pass, look for (XY)# and store components
		Matcher polyMatch = poly.matcher(substance);
		Matcher letters;
		String[] component;
		while (polyMatch.find()) {
			String polyFound = polyMatch.group();
			String number = polyFound.substring(polyFound.indexOf(")")+1);
			Matcher regLetters = reg.matcher(polyFound);
			//Find each individual set of atoms and store
			// i.e. (SO3)2 = [S, 2] [O, 6]
			// Also concatenate together to generate interior
			StringBuffer sb = new StringBuffer();
			while (regLetters.find()) {
				component = new String[2];
				Matcher m = letPat.matcher(regLetters.group());
				m.find();
				component[0] = m.group();
				//Check to see if there is a number associated with 
				// this element; if so, multiple by outer number
				if (m.end() != regLetters.group().length()) {
					int newNum = Integer.valueOf(regLetters.group().
									substring(m.end())).intValue();
					int outsideNum = Integer.valueOf(number).intValue();
					component[1] = String.valueOf(newNum*outsideNum);
				}
				else
					component[1] = number;
				sb.append(regLetters.group());
				parsed.add(component);
			}
			//now add full string
			component = new String[2];
			component[0] = sb.toString();
			component[1] = number;
			parsed.add(component);
		}
		//Remove (XY)# from string and match for rest
		String trimmed = polyMatch.replaceAll("");
		Matcher regMatch = reg.matcher(trimmed);
		int lastMatchIndex = -1;
		while(regMatch.find()) {
			//Divide into atom/number as array
			component = new String[2];
			letters = letPat.matcher(regMatch.group());
			letters.find(); //should always be true
			component[0] = letters.group();
			int numIndex = letters.end();
			//if (numIndex == regMatch.group().length()-1)
			//	component[1] = "1";
			//else
				//now return rest of string as number
			component[1] = regMatch.group().substring(numIndex);
			if (component[1].equals(""))
				component[1] = "1";
			//finally, add to output vector
			parsed.add(component);
			lastMatchIndex = regMatch.end();
		}
		if (lastMatchIndex != trimmed.length()) {
			//Not a full match, so not of correct form
			parsed.clear();
			parsed.add(substance);
		}
		return parsed;
	}
	
	public Vector getComponents() {
		return components;
	}
	
	public String toString() {
		return substanceName;
	}
}
