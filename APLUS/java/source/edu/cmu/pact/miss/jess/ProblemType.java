package edu.cmu.pact.miss.jess;

import java.io.Serializable;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.minerva_3_1.Problem;

public class ProblemType implements Userfunction, Serializable {

	private static final long serialVersionUID = 1L;

	private static final String PROBLEM_TYPE = "problem-type";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public ProblemType(){
		this(null);
	}
	
	public ProblemType(ModelTracer amt) {
		this.amt = amt;	
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		if(!vv.get(0).stringValue(context).equals(PROBLEM_TYPE)) {
			throw new JessException(PROBLEM_TYPE, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problemList = SimStRete.NOT_SPECIFIED;
		String problem = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				problemList = vv.get(1).resolveValue(context).stringValue(context);

				if(vv.size() > 2) {
					problem = vv.get(2).resolveValue(context).stringValue(context);
					String token[] = problem.split("=");
					if(token.length == 2) {
						problem = " " + token[0] + "=" + token[1] + " "; 
					} else {
						return Funcall.FALSE;
					}
				}
			}
			
			boolean matched = false;
			
			if(problemList != SimStRete.NOT_SPECIFIED && problem != SimStRete.NOT_SPECIFIED) {

				Problem studentP = new Problem(problem);
				String abstractedStudentP = studentP.getSignedAbstraction();
				
				String[] list = problemList.split(":");
				String listP = "";
				for(int i=0; i < list.length; i++) {
					
					String prob = list[i];
					String token[] = prob.split("=");
					if(token.length == 2) {
						listP = " " + token[0] + "=" + token[1] + " "; // minerva bug 
					} else {
						return Funcall.FALSE;
					}
					
					Problem suggestedP = new Problem(listP);
					String abstractedSuggestedP = suggestedP.getSignedAbstraction();
					if(abstractedStudentP.split("=")[0].trim().equalsIgnoreCase(abstractedSuggestedP.split("=")[0].trim())
							&& abstractedStudentP.split("=")[1].trim().equalsIgnoreCase(abstractedSuggestedP.split("=")[1].trim())) {
						
						matched = true;
						break;
					}
				}
				
				if(matched){
					return Funcall.TRUE;
				} else {
					return Funcall.FALSE;
				}
			}
		}
		
		return Funcall.FALSE;
	}

	@Override
	public String getName() {
		return PROBLEM_TYPE;
	}
}
