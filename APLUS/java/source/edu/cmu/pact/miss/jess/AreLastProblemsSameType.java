package edu.cmu.pact.miss.jess;



import java.io.Serializable;
import java.util.Stack;
import java.util.Vector;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.AlgebraProblemAssessor;
import edu.cmu.pact.miss.AskHint;
import edu.cmu.pact.miss.AskHintInBuiltClAlgebraTutor;
import edu.cmu.pact.miss.ProblemAssessor;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.PeerLearning.SimStSolver;
import edu.cmu.pact.miss.PeerLearning.SimStSolver.SolutionStepInfo;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStNode;

/**
 * 
 */
public class AreLastProblemsSameType implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String ARE_LAST_PROBLEMS_SAME_TYPE = "are-last-problems-same-type";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/**	Link to the current variable Context, and thence to Rete. */
	protected transient Context context;
	
	/**
	 * No argument constructor.
	 */
	public AreLastProblemsSameType(){
		
	}
	
	/**
	 * Constructor with link to the Model Tracer.
	 * @param controller
	 */
	public AreLastProblemsSameType(ModelTracer amt) {
		this.amt = amt;
	}
	
	/**
	 * Gets the CL SAI for the current step and stores it in the RHS of the production
	 * rule. 
	 * @param vv argument list
	 * @param context Jess context for resolving values
	 * @return {@link jess.Funcall#FALSE} when the CL SAI is not set else {@link jess.Value}
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		if(!vv.get(0).stringValue(context).equals(ARE_LAST_PROBLEMS_SAME_TYPE)) {
			throw new JessException(ARE_LAST_PROBLEMS_SAME_TYPE, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problemList = SimStRete.NOT_SPECIFIED;
		Stack<String> st;
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				
				problemList = vv.get(1).resolveValue(context).stringValue(context);
					
			}
			
			if(amt == null) {
				amt = ((SimStRete)context.getEngine()).getAmt();
				return Funcall.FALSE;
			}

			String windowSizeString=vv.get(2).resolveValue(context).stringValue(context);
			int windowSizeInt=Integer.parseInt(windowSizeString);			
			/*Stack<String> tmp=(Stack<String>)amt.getController().getMissController().getSimSt().getModelTraceWM().allTutoredProblemList.clone();
			
		
			
			String tmpType=null;
			
			for (int i=0;i<windowSizeInt;i++){
				String prob = tmp.pop();
				ProblemAssessor assessor = new AlgebraProblemAssessor();
				String problemType=assessor.classifyProblem(prob);
				problemType=problemType.toLowerCase();			
				
				if (tmpType==null){
					tmpType=problemType;
				}
				else if (!tmpType.equals(problemType)){
							return Funcall.FALSE;
				}
				
				
			}
			return Funcall.TRUE;
			*/
			
			problemList=problemList.replace("[", "");
			problemList=problemList.replace("]", "");
			String[] parts=problemList.split(",");
			String tmpType=null;
			for (int i=parts.length;i>(parts.length-windowSizeInt);i--){
				String problem=parts[i-1];
				ProblemAssessor assessor = new AlgebraProblemAssessor();
				String problemType=assessor.classifyProblem(problem);
				problemType=problemType.toLowerCase();			
				
				if (tmpType==null){
					tmpType=problemType;
				}
				else if (!tmpType.equals(problemType)){
							return Funcall.FALSE;
				}
				
			}
			return Funcall.TRUE;
			
		
		}
		return Funcall.FALSE;
		
	}

	@Override
	public String getName() {
		return ARE_LAST_PROBLEMS_SAME_TYPE;
	}

}
