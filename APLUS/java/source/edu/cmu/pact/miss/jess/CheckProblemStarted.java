package edu.cmu.pact.miss.jess;

import java.io.Serializable;

import jess.Context;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.minerva_3_1.Problem;

/**
 * User defined function for the SimStRete. Purpose of this class is to validate 
 * the problem entered by the student and see if the problem is similar to the 
 * list of problems which SimStudent failed on the quiz / is similar to the 
 * problem suggested by the Problem Bank.
 */
public class CheckProblemStarted implements Userfunction, Serializable {

	/**	Default serial version ID */
	private static final long serialVersionUID = 1L;

	/** Constant to denote the name of this user-defined function */
	private static final String CHECK_PROBLEM_STARTED = "check-problem-started";
	
	/** Link to the APlus Environment */
	protected SimSt simSt;
	
	/**	Link to current variable context and, thence, to the Rete */
	protected transient Context context;
	
	/**	Link to the minerva abstraction. Problem instance can be used to create signed/unsigned abstraction */
	private Problem abstractedProblem;
	
	/**	 */
	public CheckProblemStarted(){
		this(null);
	}
	
	/**
	 * @param ss
	 */
	public CheckProblemStarted(SimSt ss) {
		this.simSt = ss;
	}

	/**
	 * Called at runtime from the jess package
	 */
//	public Value call(ValueVector vv, Context context) throws JessException {
//	
//		this.context = context;
//		
//		if(!vv.get(0).stringValue(context).equals(CHECK_PROBLEM_STARTED)) {
//			throw new JessException(CHECK_PROBLEM_STARTED, "called but ValueVector head differs", vv.get(0).stringValue(context));
//		}
//		
//		String problem = SimStRete.NOT_SPECIFIED;
//		String problemList = SimStRete.NOT_SPECIFIED;
//		
//		if(context.getEngine() instanceof SimStRete) {
//			if(vv.size() > 1) {
//				problem = vv.get(1).resolveValue(context).stringValue(context);
//				if(vv.size() > 2) {
//					problemList = vv.get(2).resolveValue(context).stringValue(context);
//				}
//			}
//		}
//		
//		if(problem != SimStRete.NOT_SPECIFIED && problemList != SimStRete.NOT_SPECIFIED) {			
//			String[] list = problemList.split(":");
//			for(int i=0; i < list.length; i++) {
//				if(problem.equalsIgnoreCase(list[i])) { // Student entered problem is the same as the quizProblemPassedList/quizProblemFailedList
//					if(simSt.getBrController().getAmt() != null)
//						simSt.getBrController().getAmt().setRuleSAI(problem, "StartProblem", "-1");
//					//return Funcall.TRUE;
//					return new Value(problem, RU.STRING);
//					//return new Value("quizProblem", RU.STRING);
//				}
//			}
//		} else {
//			return Funcall.FALSE;
//		}
//		
//		/*
//		String startStateElements = SimStRete.NOT_SPECIFIED;
//		String problemList = SimStRete.NOT_SPECIFIED;
//		String problem = "";
//		
//		if(context.getEngine() instanceof SimStRete) {
//			if(vv.size() > 1) {
//				startStateElements = vv.get(1).resolveValue(context).stringValue(context);
//				if(vv.size() > 2) {
//					problemList = vv.get(2).resolveValue(context).stringValue(context);
//				}
//			}
//		}
//		
//		if(startStateElements != SimStRete.NOT_SPECIFIED && problemList != SimStRete.NOT_SPECIFIED) {
//			if(simSt != null && simSt.getBrController() != null) {
//				String[] startState = startStateElements.split(":");
//				if(startState.length != START_STATE_ELEMENTS_LENGTH) {
//					for(int i=0; i < startState.length; i++) {
//						String element = startState[i];
//						Object widget = simSt.getBrController().lookupWidgetByName(element);
//						if(widget != null && widget instanceof TableExpressionCell) {
//							TableExpressionCell cell = (TableExpressionCell)widget;
//							String input = cell.getText();
//							problem += input;
//						}
//					}
//					String[] list = problemList.split(":");
//					for(int i=0; i < list.length; i++) {
//						
//					}
//				} else {
//					return Funcall.FALSE;
//				}
//			}
//		}
//		*/
//		
//		if(simSt.getBrController() != null && simSt.getBrController().getAmt() != null) {
//			simSt.getBrController().getAmt().setRuleSAI("", "", "");
//		}
//		return Funcall.FALSE;
//	}

	@Override
	public String getName() {
		return CHECK_PROBLEM_STARTED;
	}

	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		// TODO Auto-generated method stub
		return null;
	}

}
