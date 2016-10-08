package edu.cmu.pact.miss.jess;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
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
public class GetResource implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String GET_RESOURCE = "get-resource";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/**	Link to the current variable Context, and thence to Rete. */
	protected transient Context context;
	
	/**
	 * No argument constructor.
	 */
	public GetResource(){
		
	}
	
	/**
	 * Constructor with link to the Model Tracer.
	 * @param controller
	 */
	public GetResource(ModelTracer amt) {
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
		if(!vv.get(0).stringValue(context).equals(GET_RESOURCE)) {
			throw new JessException(GET_RESOURCE, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}
		
		String problem = SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {
			
			if(vv.size() > 1) {
				
				problem = vv.get(1).resolveValue(context).stringValue(context);
			}
			
			if(amt == null) {
				amt = ((SimStRete)context.getEngine()).getAmt();
				return Funcall.FALSE;
			}
			String resource;
			String resource1="Examples";
			String resource2="UnitOrerview";
				Random randomGenerator = new Random();
			    double num1=randomGenerator.nextDouble();
			    double num2=randomGenerator.nextDouble();			    
			    if( num1 > num2) 
			    {
			    	resource=resource1;
			    	
			    }
			    else{
			    	resource=resource2;
			    }
			    
			    
			 /*get a random resource
			ArrayList<String> resources=amt.getController().getMissController().getSimStPLE().getAvailableResources();
			Random generator = new Random(); 
			int i = generator.nextInt(resources.size());
			resource=resources.get(i);
			*/
			    
			    
			Value rtnValue = new Value(resource, RU.STRING);
			
			return rtnValue;

		}
		return Funcall.NIL;
		
	}

	@Override
	public String getName() {
		return GET_RESOURCE;
	}

}
