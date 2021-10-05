package SimStAlgebraV8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.jess.MTRete;
import edu.cmu.pact.miss.AlgebraProblemAssessor;
import edu.cmu.pact.miss.NearSimilarProblemsGetter;
import edu.cmu.pact.miss.StringLengthListSort;
import jess.Fact;
import jess.Value;

public class NearSimilarEquationFinder extends NearSimilarProblemsGetter{

	public int pos_;
	ArrayList<Integer> var_term_pos;
	ArrayList<Integer> const_term_pos;
	public ArrayList<String> nearSimilarProblemsGetter(ProblemNode currentProblem)
    {
		pos_ = -1;
		var_term_pos = new ArrayList<Integer>();
		const_term_pos = new ArrayList<Integer>();
		ArrayList<String> similar_problems = new ArrayList<String>();
		AlgebraProblemAssessor apa = new AlgebraProblemAssessor();
		String problemString = apa.findLastStep(currentProblem.getProblemModel().getStartNode(), currentProblem);		
		
		String problemString_ = problemString.replace('=', '_');
		String new_problems = "";
		ArrayList<String> parsed_equation = parseEquationToEditDistances(problemString_);
		String type = whichTypeEquation(parsed_equation.size(), pos_, problemString_);
		if(type.contains("1")) {
			new_problems = dropCoefficient(problemString_, parsed_equation, type, var_term_pos.get(0));
			if(new_problems.equals(problemString_)) {
				// one step equation has no coefficient rather constant.
				dropConstant(problemString_, parsed_equation, type, similar_problems);
				
			}else {
				if(isValidEquation(new_problems) && !new_problems.equals(problemString_)) {
					similar_problems.add(new_problems);
				}
			}
		}
		else if(type.contains("2")) {
			dropConstant(problemString_, parsed_equation, type, similar_problems);
			new_problems = dropCoefficient(problemString_, parsed_equation, type, var_term_pos.get(0));
			if(isValidEquation(new_problems) && !new_problems.equals(problemString_)) {
				similar_problems.add(new_problems);
			}
		}
		else if(type.contains("3")) {
			dropConstant(problemString_, parsed_equation, type, similar_problems);
			dropVarTerm(problemString_, parsed_equation, type, similar_problems);
		}
		
		StringLengthListSort ss = new StringLengthListSort();
		Collections.sort(similar_problems, ss);
		return similar_problems;
    }
	
	
	public void dropConstant(String currentProblem, ArrayList<String> parsed_equation, String type, ArrayList<String> similar_problems){
		for(int i=0; i<const_term_pos.size(); i++) {
			String new_problems = dropOneConstant(currentProblem, parsed_equation, type, const_term_pos.get(i));
			new_problems = replaceFirstPositiveSign(new_problems);
			if(isValidEquation(new_problems)  && !new_problems.equals(currentProblem)) {
				similar_problems.add(new_problems);
			}
		}
	}
	public void dropVarTerm(String currentProblem, ArrayList<String> parsed_equation, String type, ArrayList<String> similar_problems){
		String droped_var_equation = "";
		for(int i=0; i<var_term_pos.size(); i++) {
			for(int j=0; j<parsed_equation.size(); j++) {
				if(var_term_pos.get(i) == j) droped_var_equation+="";
				else droped_var_equation+=parsed_equation.get(j);
			}
			droped_var_equation = replaceFirstPositiveSign(droped_var_equation);
			if(isValidEquation(droped_var_equation) && !droped_var_equation.equals(currentProblem)) {
				similar_problems.add(droped_var_equation);
			}
			droped_var_equation = "";
		}
	}
	
	public String dropOneConstant(String currentProblem, ArrayList<String> parsed_equation, String type, int const_term_pos_){
		String droped_const_equation = "";
		if(type.equals("2_L") && const_term_pos_ > pos_){
			return droped_const_equation;
		}
		else if(type.equals("2_R") && const_term_pos_ < pos_){
			return droped_const_equation;
		}
		else {
			for(int i=0; i<parsed_equation.size(); i++) {
				if(i == const_term_pos_) droped_const_equation+="";
				else droped_const_equation+=parsed_equation.get(i);
			}
		}
		return droped_const_equation;
	}
	
	
	public String dropCoefficient(String currentProblem, ArrayList<String> parsed_equation, String type, int var_term_pos_){
		
		String droped_coef_equation = "";
		String droped_coef_varToken = "";
		String varToken = parsed_equation.get(var_term_pos_);
		//String REGEX_numbers_with_sign = "[^a-zA-Z]";
		//String REGEX_numbers_without_sign = "[0-9]";
		Pattern pattern_numbers_with_sign = Pattern.compile("[^a-zA-Z]");
		Pattern pattern_numbers_without_sign = Pattern.compile("[0-9]");
		
		
		if(type.contains("1")) {
			droped_coef_varToken = varToken.replaceAll("[^a-zA-Z]", "");
		}
		else if(type.contains("2")) {
			if((type.equals("2_L") && var_term_pos_>0) || (type.equals("2_R") && var_term_pos_== parsed_equation.size()-1)){
				// sign of the token is not replaced and kept the same
				droped_coef_varToken = varToken.replaceAll("[0-9]", "");
			}
			else {
				droped_coef_varToken = varToken.replaceAll("[^a-zA-Z]", "");
			}
		}
		else if (type.contains("3")) {
			if((var_term_pos_> 0 && var_term_pos_< pos_)|| var_term_pos_ > pos_+1) {
				droped_coef_varToken = varToken.replaceAll("[0-9]", "");
			}
			else {
				droped_coef_varToken = varToken.replaceAll("[^a-zA-Z]", "");
			}
			
		}
		for(int i=0; i<parsed_equation.size(); i++) {
			if(i == var_term_pos_) droped_coef_equation+=droped_coef_varToken;
			else droped_coef_equation+=parsed_equation.get(i);
		}
		return droped_coef_equation;
		
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
				if(current_term.matches(".*[a-zA-Z]+.*")) var_term_pos.add(parsed_equation.size()-1);
				else const_term_pos.add(parsed_equation.size()-1);
				parsed_equation.add("_");
				pos_ = parsed_equation.size()-1;
	        	start = 0;
	        	current_term = "";
			}
			else if((currentProblem.charAt(i) == '+' || currentProblem.charAt(i) == '-') && start!=0) {
	            parsed_equation.add(current_term);
	            if(current_term.matches(".*[a-zA-Z]+.*")) var_term_pos.add(parsed_equation.size()-1);
	            else const_term_pos.add(parsed_equation.size()-1);
	            current_term = String.valueOf(currentProblem.charAt(i));
	            start = 1;
	            	
	        }
	        else {
	            start = 1;
	            current_term+= currentProblem.charAt(i); 
	        }
	    }
		parsed_equation.add(current_term);
		if(current_term.matches(".*[a-zA-Z]+.*")) {
			var_term_pos.add(parsed_equation.size()-1);
		}
		else const_term_pos.add(parsed_equation.size()-1);
		return parsed_equation;
	}
	/*
	 * Checks if the string of equation is valid or not
	 */	
	public static boolean isValidEquation(String problemName){
		String temp = problemName;
		temp = temp.replace("+", "");
		temp = temp.replace("-", "");
		
		if(StringUtils.isEmpty(temp) || temp.charAt(0)=='_' || temp.charAt(temp.length()-1)=='_') {
			return false;
		}
		
		String[] sides = temp.split("_");
		if(sides.length < 2) return false;
		boolean lhsNumeric=true, rhsNumeric=true;
		try {
            Double num = Double.parseDouble(sides[0]);
        } catch (NumberFormatException e) {
        	lhsNumeric = false;
        }
        try {
            Double num = Double.parseDouble(sides[1]);
        } catch (NumberFormatException e) {
        	rhsNumeric = false;
        }
        
        return !lhsNumeric || !rhsNumeric;
	}
	
	public static String whichTypeEquation(int total_tokens, int pos_, String equation) {
		// return value: 1_L: one step LHS var,  1_R: one step RHS var, 2_L: two-step LHS var,
		// 2_R: two-step RHS var, 3_N: var on both sides
		String LHS = equation.split("_")[0].trim();
		String RHS = equation.split("_")[1].trim();
		if(LHS.matches(".*[a-zA-Z]+.*") && RHS.matches(".*[a-zA-Z]+.*")) return "3_N";
		else if(LHS.matches(".*[a-zA-Z]+.*") && pos_ == 2) return "2_L";
		else if(LHS.matches(".*[a-zA-Z]+.*") && pos_ == 1) return "1_L";
		else if(RHS.matches(".*[a-zA-Z]+.*") && pos_ == 1 && total_tokens == 4) return "2_R";
		else if(RHS.matches(".*[a-zA-Z]+.*") && pos_ == 1 && total_tokens == 3) return "1_R";
		return "0_N";
	}
	
	public static String replaceFirstPositiveSign(String equation) {
		if(equation.equals("")) return equation;
		if(equation.charAt(0) == '+') equation = equation.substring(1);
		int pos = equation.indexOf("_");
		if(equation.charAt(pos+1) == '+') equation = equation.substring(0,pos+1)+equation.substring(pos+2);
		return equation;
	}
}
