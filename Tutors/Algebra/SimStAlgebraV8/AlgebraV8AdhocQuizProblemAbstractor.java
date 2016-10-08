package SimStAlgebraV8;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import pact.CommWidgets.JCommTable.TableExpressionCell;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.HashMap;
import edu.cmu.pact.miss.minerva_3_1.Problem;
import edu.cmu.pact.miss.jess.ModelTraceWorkingMemory;

import edu.cmu.pact.miss.MetaTutor.APlusQuizProblemAbstractor;
import edu.cmu.pact.miss.PeerLearning.GameShow.GameShowUtilities;


/**
 * 
 */
public class AlgebraV8AdhocQuizProblemAbstractor implements APlusQuizProblemAbstractor {

	/**  */
	private static final long serialVersionUID = 1L;

	private static char[] variables = {'a','b','c','g','h','j','k','m','n','p','q','r','s','t','u','v','w','x','y','z'};
	
	/**	 */
	private Problem abstractor;
	
	/**	 */
	private LinkedList<String> solvedCorrectly;
	
	/**	 */
	private LinkedList<String> solvedIncorrectly;
	
	/**	 */
	public AlgebraV8AdhocQuizProblemAbstractor(){}
	
	private String suggestedProblem;
	
	/**	 */
	public String[] getQuizItemsSolvedCorrectly() {
		return null;
	}

	/**	 */
	public String[] getQuizItemsSolvedIncorrectly() {
		return null;
	}
	
	/**	 */
	public void setProblemsToTutor(boolean isQuiz, ModelTraceWorkingMemory wm){
		
		char nextConstant = 'A';
		char varUpper = 'V';
		char varLower = 'v';
		StringBuffer sb = new StringBuffer();
		String list = "";
		if(isQuiz) {
			if((solvedIncorrectly != null) && (solvedIncorrectly.size() > 0)) {
				String problems = "<html>";
				for(int i=0; i < solvedIncorrectly.size(); i++) {
					nextConstant = 'A';
					String abstractedProblem = solvedIncorrectly.get(i);
					for(int j=0; j < abstractedProblem.length(); j++) {
						char current = abstractedProblem.charAt(j);
						if(current == varUpper || current == varLower) {
							sb.append(current);
							continue;
						}
						if(Character.isLetter(current)) {
							sb.append(nextConstant++);
						} else {
							sb.append(current);
						}
					}
					
					list += sb.toString()+":";
					problems += "<br>" + generate(sb.toString());
					sb = new StringBuffer();
				}
				
				problems += "<br></html>";
				//wm.setSuggestedProblems(problems);
				//wm.setQuizProblemsFailed(list);
			}
			
			list = "";
			if((solvedCorrectly != null) && (solvedCorrectly.size() > 0)) {
				for(int i=0; i< solvedCorrectly.size(); i++) {
					nextConstant = 'A';
					String abstractedProblem = solvedCorrectly.get(i);
					for(int j=0; j < abstractedProblem.length(); j++) {
						char current = abstractedProblem.charAt(j);
						if(current == varUpper || current == varLower) {
							list += current;
							continue;
						}
						if(Character.isLetter(current)) {
							list += nextConstant++;
						} else {
							list += current;
						}
					}
					list += ":";
				}
			}
			wm.setQuizProblemsPassed(list);
		} else {
			String abstractedProblem = suggestedProblem;
			for(int j=0; j < abstractedProblem.length(); j++) {
				char current = abstractedProblem.charAt(j);
				if(current == varUpper || current == varLower) {
					sb.append(current);
					continue;
				}
				if(Character.isLetter(current)) {
					sb.append(nextConstant++);
				} else {
					sb.append(current);
				}
			}
		
			/*
			String problem = generate(sb.toString());
			wm.setSuggestedProblem(problem);
			*/
		}
	}
	
	/**
	 * Contains a mapping of quiz problem to correctness of the quiz problem
	 * Example: Key 		Value
	 * 		    3x=6        true
	 * 		   3x+6=15 		false
	 * @param hm
	 */
	public void abstractQuizProblems(HashMap hm, ModelTraceWorkingMemory wm){
		
		solvedCorrectly = new LinkedList<String>();
		solvedIncorrectly = new LinkedList<String>();
		Iterator itr = hm.keySet().iterator();
		while(itr.hasNext()) {
			Object key = itr.next();
			Object val = hm.get(key);
			abstractor = new Problem((String)key);
			String abstractedQuizProblemWithSign = abstractor.getSignedAbstraction();
			if(Boolean.TRUE.equals(val)) {
				solvedCorrectly.add(abstractedQuizProblemWithSign);
			} else if(Boolean.FALSE.equals(val)) {
				solvedIncorrectly.add(abstractedQuizProblemWithSign);
			}
		}
	
		setProblemsToTutor(true, wm);
	}
	
	public String abstractProblem(String problem, ModelTraceWorkingMemory wm){
	
		abstractor = new Problem(problem);
		String abstractedProblemWithSign = abstractor.getSignedAbstraction();
		if(wm == null) {
			String prob = "";
			char nextConstant = 'A';
			char varUpper = 'V';
			char varLower = 'v';
			
			for(int i=0; i< abstractedProblemWithSign.length(); i++) {
				char current = abstractedProblemWithSign.charAt(i);
				if(current == varUpper || current == varLower) {
					prob += current;
					continue;
				}
				
				if(Character.isLetter(current)) {
					prob += nextConstant++;
				} else {
					prob += current;
				}
			}
			return prob;
		}
		
		suggestedProblem = abstractedProblemWithSign;
		setProblemsToTutor(false, wm);
		return "";
	}
	
	public String abstractProblem(String problem) {

		abstractor = new Problem(problem);
		String abstractedProblemWithSign = abstractor.getSignedAbstraction();
		return abstractedProblemWithSign;
	}
	
	public String abstractProblem(ArrayList<String> startStateElements, BR_Controller controller) {
		
		String problem = " ";
		for(int i=0; i < startStateElements.size(); i++) {
			String element = startStateElements.get(i);
			Object widget = controller.lookupWidgetByName(element);
			if(widget != null && widget instanceof TableExpressionCell) {
				TableExpressionCell cell = (TableExpressionCell) widget;
				String input = cell.getText();
				if(i+1 == startStateElements.size()) { 
					problem += "=";
					problem += input;
					problem += " ";
				} else {
					problem += input;
				}
			}
		}

		abstractor = new Problem(problem);
		String abstractedProblemWithSign = abstractor.getSignedAbstraction();
		
		char nextConstant = 'A';
		char varUpper = 'V';
		char varLower = 'v';
		
		problem = "";
		for(int j=0; j < abstractedProblemWithSign.length(); j++) {
			char current = abstractedProblemWithSign.charAt(j);
			if(current == varUpper || current == varLower) {
				problem += current;
				continue;
			}
			if(Character.isLetter(current)) {
				problem += nextConstant++;
			} else {
				problem += current;
			}
		}

		return problem;
	}
	
	/**
	 * 
	 * @param problem
	 * @return
	 */
	private static String generate(String problem) {
	
		int coefficients[] = new int[2];
		int constants[] = new int[3];
		char varUpper = 'V';
		char varLower = 'v';
		int index = 0, coeff_index = 0, const_index = 0;
		int val[] = new int[4];
		
		for(int i=0; i < problem.length(); i++) {
			char current = problem.charAt(i);
			if(current == varUpper || current == varLower)
				continue;
			if(Character.isLetter(current)) {
				if(i+1 < problem.length()) {
					char temp = problem.charAt(i+1);
					if(temp == varUpper || temp == varLower)
						coefficients[coeff_index++] = index++;
					else 
						constants[const_index++] = index++;
				} else {
					if(current == varUpper || current == varLower) {
						continue;
					} else {
						constants[const_index++] = index++;
					}
				}
			}
		}
		
		val[0] = (int) (Math.random() * 10) + 2;
		val[1] = (int) (Math.random() * 10) + 2;
		val[2] = (int) (Math.random() * 10) + 2;
		val[3] = (int) (Math.random() * 10) + 2;
		
		if(coeff_index == 2) {
			if(val[coefficients[0]] == val[coefficients[1]])
				val[coefficients[1]]++;
		}
		if(const_index == 2) {
			if(val[constants[0]] == val[constants[1]])
				val[constants[1]]++;
		}
		
		char var = variables[(int)(Math.random()*variables.length)];
		problem = problem.replaceAll("A", ""+val[0]);
		problem = problem.replaceAll("B", ""+val[1]);
		problem = problem.replaceAll("C", ""+val[2]);
		problem = problem.replaceAll("D", ""+val[3]);
		problem = problem.replaceAll("[Vv]", ""+var);		
		return problem;
	}
	
	
}