package edu.cmu.pact.miss.jess;



import java.io.Serializable;
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
public class IsSolutionNonOptimal implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String IS_SOLUTION_NON_OPTIMAL = "is-solution-non-optimal";
	
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/**	Link to the current variable Context, and thence to Rete. */
	protected transient Context context;
	
	/**
	 * No argument constructor.
	 */
	public IsSolutionNonOptimal(){
		
	}
	
	/**
	 * Constructor with link to the Model Tracer.
	 * @param controller
	 */
	public IsSolutionNonOptimal(ModelTracer amt) {
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
		if(!vv.get(0).stringValue(context).equals(IS_SOLUTION_NON_OPTIMAL)) {
			throw new JessException(IS_SOLUTION_NON_OPTIMAL, "called but ValueVector head differs", vv.get(0).stringValue(context));
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

			
			
		problem=amt.getController().getMissController().getSimSt().getModelTraceWM().allTutoredProblemList.peek();

		if(problem.length() > 0 && problem != SimStRete.NOT_SPECIFIED) {				
			String jessFilesDirectory=amt.getController().getMissController().getSimSt().getBrController().getPreferencesModel().getStringValue(CTAT_Controller.PROBLEM_DIRECTORY);//brController.getPreferencesModel().getStringValue(BR_Controller.PROJECTS_DIR);//this.getSimSt().getProjectDir();	
			String productionRulesFile=jessFilesDirectory+ SimSt.PRODUCTION_RULE_FILE;
			String wmeTypesFile=jessFilesDirectory+ SimSt.WME_TYPE_FILE;  
			String initialFactsFile=jessFilesDirectory+  SimSt.INIT_STATE_FILE; 
			String pleConfig=amt.getController().getMissController().getSimSt().getProjectDir()+"/"+SimStPLE.CONFIG_FILE;

	
			/*Create new solver to solve the current problem*/
			SimStSolver solver=new SimStSolver(problem,productionRulesFile,wmeTypesFile, initialFactsFile, pleConfig, amt.getController().getMissController().getSimSt());
			Vector<Sai> solution=new Vector();
			Vector<SolutionStepInfo> solutionStepsInfo=new Vector<SolutionStepInfo>();

			try {
				solver.createStartState();
				solution=solver.solve();

			} catch (JessException e1) {

				//e1.printStackTrace();
			}


			
			solutionStepsInfo=solver.getSolutionStepInfo();
				
			int optimalSteps=solver.getSolutionStepInfo().size();

			int actualSteps=amt.getController().getMissController().getSimSt().getModelTraceWM().getSolutionSteps();
			
			
			
			problem=problem.replace(" ","");
			ProblemAssessor assessor = new AlgebraProblemAssessor();
			String problemType=assessor.classifyProblem(problem);
			if (problemType.equals("OneStep"))
				optimalSteps=4;
			else if (problemType.equals("TwoStep"))
				optimalSteps=7;
			else if (problemType.equals("BothSides"))
				optimalSteps=10;
			else
				optimalSteps=20;
			
			//System.out.println("****** optimal steps " + optimalSteps);
			//System.out.println("****** actual steps " + actualSteps);
			
			
			if (optimalSteps<actualSteps)
				return Funcall.TRUE;
			else return Funcall.FALSE;
			
		
			
		}
		}
		return Funcall.NIL;
		
	}

	@Override
	public String getName() {
		return IS_SOLUTION_NON_OPTIMAL;
	}

}
