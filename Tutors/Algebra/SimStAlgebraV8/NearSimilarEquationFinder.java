package SimStAlgebraV8;

import java.util.ArrayList;
import java.util.Collections;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.miss.NearSimilarProblemsGetter;
import edu.cmu.pact.miss.StringLengthListSort;

public class NearSimilarEquationFinder extends NearSimilarProblemsGetter{

	public ArrayList<String> nearSimilarProblemsGetter(ProblemNode currentProblem)
    {
		ArrayList<String> similar_problems = new ArrayList<String>();
		similar_problems = dropTermsAndChangeSign(currentProblem.getName(), similar_problems);
		
		StringLengthListSort ss = new StringLengthListSort();
		Collections.sort(similar_problems, ss);
		return similar_problems;
    }
	
	public ArrayList<String> dropTermsAndChangeSign(String currentProblem, ArrayList<String> similar_problems){
		ArrayList<String> parsed_equation = parseEquationToEditDistances(currentProblem);
		int i = 0;
		//int start = 0;
		while(i<parsed_equation.size()) {
			String new_equation_1 = "";
			String new_equation_2 = "";
			String temp_term = "";
			if(parsed_equation.get(i).contains("+")) {					
				temp_term = parsed_equation.get(i).replace("+", "-");
			}
			else if(parsed_equation.get(i).contains("-")) {
				temp_term = parsed_equation.get(i).replace("-", "+");
			}
			else {
				temp_term = "-"+parsed_equation.get(i);
			}
			for (int l = 0; l < parsed_equation.size(); l++) {
				if(i!=l) {
						
						new_equation_1+=parsed_equation.get(l);
						new_equation_2+=parsed_equation.get(l);
				}
				else {
					new_equation_2+=temp_term;
				}
			}
			if(isValidEquation(new_equation_1)) {
				similar_problems.add(replaceFirstPositiveSign(new_equation_1));
			}
			if(isValidEquation(new_equation_2)) {
				similar_problems.add(replaceFirstPositiveSign(new_equation_2));
			}
			i++;
			if(i<parsed_equation.size() && parsed_equation.get(i).contains("_")) {
				new_equation_1+=parsed_equation.get(i);
				new_equation_2+=parsed_equation.get(i);
				i++;
			}
		}
		
		return similar_problems;
		
	} 
	
	
	public ArrayList<String> dropCoefficient(String currentProblem, ArrayList<String> similar_problems){
		
		
		return similar_problems;
		
	} 
	/*
	 * from a string of equation "3x+9_-15", returns an arraylist of string 
	 * that looks like [3x, +9, _, -15] for further operations
	 */
	public ArrayList<String> parseEquationToEditDistances(String currentProblem){
		ArrayList<String> parsed_equation = new ArrayList<String>();
		String current_term = "";
		int start = 0;			
		for (int i = 0; i < currentProblem.length(); i++) {
			if(currentProblem.charAt(i) == '_'){
				parsed_equation.add(current_term);
				parsed_equation.add("_");
	        	start = 0;
	        	current_term = "";
			}
			else if((currentProblem.charAt(i) == '+' || currentProblem.charAt(i) == '-') && start!=0) {
	            parsed_equation.add(current_term);
	            current_term = String.valueOf(currentProblem.charAt(i));
	            start = 1;
	            	
	        }
	        else {
	            start = 1;
	            current_term+= currentProblem.charAt(i); 
	        }
	    }
		parsed_equation.add(current_term);
		return parsed_equation;
	}
	/*
	 * Checks if the string of equation is valid or not
	 */
	public static boolean isValidEquation(String problemName){
		String temp = problemName;
		temp = temp.replace("+", "");
		temp = temp.replace("-", "");
		String[] sides = temp.split("_");
		if(sides.length < 2) return false;
		boolean lhs=true, rhs=true;
		try {
            Double num = Double.parseDouble(sides[0]);
        } catch (NumberFormatException e) {
            lhs = false;
        }
        try {
            Double num = Double.parseDouble(sides[1]);
        } catch (NumberFormatException e) {
            rhs = false;
        }
		
		if(lhs == true && rhs == true) return false;
		return true;
	}
	
	public static String replaceFirstPositiveSign(String equation) {
		if(equation.charAt(0) == '+') equation = equation.substring(1);
		int pos = equation.indexOf("_");
		if(equation.charAt(pos+1) == '+') equation = equation.substring(0,pos+1)+equation.substring(pos+2);
		return equation;
	}
}
