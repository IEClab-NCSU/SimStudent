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
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.AlgebraProblemAssessor;
import edu.cmu.pact.miss.ProblemAssessor;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.PeerLearning.SimStSolver;
import edu.cmu.pact.miss.PeerLearning.SimStSolver.SolutionStepInfo;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStGraphNavigator;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStNode;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStProblemGraph;

public class CanSolveProblem implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;
	
	private static final String CAN_SOLVE_PROBLEM = "can-solve-problem";
	
	protected transient ModelTracer amt;
	
	protected transient Context context;
	
	public CanSolveProblem() {
		this(null);
	}
	
	public CanSolveProblem(ModelTracer amt) {
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		Value returnValue=Funcall.NIL;
		if(!vv.get(0).stringValue(context).equals(CAN_SOLVE_PROBLEM)) {
			throw new JessException(CAN_SOLVE_PROBLEM, "called but ValueVector head differs", vv.get(0).stringValue(context));
		}

		String problem = SimStRete.NOT_SPECIFIED;
		String lhs=SimStRete.NOT_SPECIFIED;
		String rhs=SimStRete.NOT_SPECIFIED;
		
		if(context.getEngine() instanceof SimStRete) {

			if(vv.size() > 1) {
				lhs = vv.get(1).resolveValue(context).stringValue(context);
				rhs = vv.get(2).resolveValue(context).stringValue(context);
			}

			if(amt == null) {
				amt = ((SimStRete)context.getEngine()).getAmt();
			}
			if (lhs != SimStRete.NOT_SPECIFIED && rhs != SimStRete.NOT_SPECIFIED)
			problem=lhs + "="+rhs;
			
			ProblemAssessor assessor = new AlgebraProblemAssessor();
			String problemType=assessor.classifyProblem(problem);
			
			if (problemType.equalsIgnoreCase("OneStep")){
				return Funcall.TRUE;	
			}
			
			
			
			if(problem.length() > 0 && problem != SimStRete.NOT_SPECIFIED && amt!=null /* && amt.getController().getMissController().getSimSt().getBrController().getProblemModel().getStartNode()!=null)*/) {

				
				
			
				/*Necessary jess files that must be passed to the solver */
				String jessFilesDirectory= amt.getController().getPreferencesModel().getStringValue(CTAT_Controller.PROBLEM_DIRECTORY);//brController.getPreferencesModel().getStringValue(BR_Controller.PROJECTS_DIR);//this.getSimSt().getProjectDir();	
				String productionRulesFile=jessFilesDirectory+ SimSt.PRODUCTION_RULE_FILE;
				String wmeTypesFile=jessFilesDirectory+ SimSt.WME_TYPE_FILE;  
				String initialFactsFile=jessFilesDirectory+  SimSt.INIT_STATE_FILE; 
				String pleConfig=amt.getController().getMissController().getSimSt().getProjectDir()+"/"+SimStPLE.CONFIG_FILE;

				String quizProblem=SimSt.convertFromSafeProblemName(problem);
				
				/*Create new solver to solve the current problem*/
				SimStSolver solver=new SimStSolver(quizProblem,productionRulesFile,wmeTypesFile, initialFactsFile, pleConfig, amt.getController().getMissController().getSimSt());
				Vector<Sai> solution=new Vector();
				Vector<SolutionStepInfo> solutionStepsInfo=new Vector<SolutionStepInfo>();

				try {
					solver.createStartState();
					solution=solver.solve();
				} catch (JessException e1) {

					e1.printStackTrace();
				}
	
				System.out.println("solution is " + solution);
				SimStProblemGraph solutionGraph = null;
				String safeProblemName = SimSt.convertToSafeProblemName(problem);
				solutionGraph = new SimStProblemGraph();
				SimStNode startNode = new SimStNode(safeProblemName, solutionGraph);
				solutionGraph.setStartNode(startNode);
				solutionGraph.addSSNode(startNode);
				
				SimStNode currentNode=solutionGraph.getStartNode();
				
				String raTestMethod =  amt.getController().getMissController().getSimSt().getRuleActivationTestMethod();
				 amt.getController().getMissController().getSimSt().setRuleActivationTestMethod( amt.getController().getMissController().getSimSt().getQuizGradingMethod());
				
				
				
				returnValue=Funcall.TRUE;				
				for (int i=0;i<solution.size();i++){
					//get all the necessary information for that solution step
					Sai sai= solution.get(i);		
					String inquiryResult = amt.getController().getMissController().getSimSt().builtInInquiryClTutor( sai.getS(), sai.getA(), sai.getI(), currentNode, solutionGraph.getStartNode().getName());
					if (!inquiryResult.equals(EdgeData.CORRECT_ACTION)){
							returnValue=Funcall.FALSE;
							break;
					}
					SimStNode successiveNode = new SimStGraphNavigator().simulatePerformingStep(currentNode, sai);
						if (successiveNode==null) {
							returnValue=Funcall.NIL;
							break;
						}	
						else  currentNode=successiveNode;
				}
				

			}
		}

		

		return Funcall.FALSE;
	}

	@Override
	public String getName() {
		return CAN_SOLVE_PROBLEM;
	}

}
