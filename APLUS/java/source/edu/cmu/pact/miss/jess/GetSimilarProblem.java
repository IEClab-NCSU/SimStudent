package edu.cmu.pact.miss.jess;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Random;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.minerva_3_1.Problem;

public class GetSimilarProblem implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	private static final String GET_SIMILAR_PROBLEM = "get-similar-problem";
	
	/**	 */
	private static char[] variables = {'a','b','c','g','h','j','k','m','n','p','q','r','s','t','u','v','w','x','y','z'};
	
	/**	 */
	protected transient ModelTracer amt;
	
	/**	 */
	protected transient Context context;
	
	/**	 */
	private static String lastAbstractedProblem;
	private static String lastSimilarProblem;
	private static int lastAbstractedProblemSolvedCount;
	
	public GetSimilarProblem() {
		this(null);
	}
	
	public GetSimilarProblem(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
	
		if(!vv.get(0).stringValue(context).equals(GET_SIMILAR_PROBLEM)) {
			throw new JessException(GET_SIMILAR_PROBLEM, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problem = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				
				problem = vv.get(1).resolveValue(context).stringValue(context);
			}
			
			String[] token = problem.split("=");
			
			// Check if the problem is symmetric {Ex: 3x+6 = 3x+6}. If so return the problem (the student has entered one side
			// only) as we are model tracing the student entered problem
			if(token.length == 2) {
				
				if(token[0].trim().equals(token[1].trim()) ) {
					
					return new Value(problem, RU.STRING);
				}
			}
			
			if(token.length == 2 && token[0].trim().length() > 0 && token[1].trim().length() > 0) {
				
				problem = " " + token[0].trim() + "=" + " " + token[1].trim();
				Problem abstractor = new Problem(problem);
				String abstractedPWithSignPreserved = abstractor.getSignedAbstraction();
				
				//trace.out(" Abstracted problem :  "+abstractedPWithSignPreserved);
				
				
				int	problemCount = ModelTraceWorkingMemory.quizProblemsTutoredListAllSections.getOrDefault(abstractedPWithSignPreserved, new Integer(0)) ;
				//trace.out(" Problem Count : "+problemCount+"  lastAbstractedProblemSolvedCount  : "+lastAbstractedProblemSolvedCount);
				
				//trace.out(" Last Abstracted Problem : "+lastAbstractedProblem);
				
				if(abstractedPWithSignPreserved.equals(lastAbstractedProblem)
						&& lastAbstractedProblemSolvedCount == problemCount ) {
				
					return new Value(lastSimilarProblem, RU.STRING);
				} else { // generate a similar problem 
					
					String similarP = generate(abstractedPWithSignPreserved);
					
					// Update the static variables for tracking
					lastAbstractedProblem = abstractedPWithSignPreserved;
					lastSimilarProblem = similarP;
					 ModelTraceWorkingMemory.suggestedProblem = similarP;
					 
						//if(problemCount > 0)
						lastAbstractedProblemSolvedCount = ModelTraceWorkingMemory.quizProblemsTutoredListAllSections.getOrDefault(lastAbstractedProblem, new Integer(0));
				
					return new Value(similarP, RU.STRING);
				}
			}
		}

		return Funcall.NIL;
	}

	@Override
	public String getName() {
		return GET_SIMILAR_PROBLEM;
	}

	private static String generate(String problem) {
		
		int count = 0;
		for(int i=0; i < problem.length(); i++) {
			char current = problem.charAt(i);
			if(current == 'N') {
				++count;
			}
		}

		int val[] = new int[count];
		for(int i=0; i < count; i++) {
			
			Random rand = new SecureRandom();
			val[i] = (rand.nextInt(Integer.MAX_VALUE) % 10) + 2;
			//val[i] = (int) (Math.random() * 10) + 2;
		}
		
		char var = variables[(int)(Math.random()*variables.length)];
		for(int i=0; i < count; i++) {
			problem = problem.replaceFirst("N", "" + val[i]);
		}
		
		problem = problem.replaceAll("[Vv]", ""+var);		

		String[] token = problem.split("=");
		if(token.length == 2 && token[0].trim().length() > 0 && token[1].trim().length() > 0) {

			problem = token[0].trim() + "  =  " +  token[1].trim();
		}
		
		return problem;
	}
}
