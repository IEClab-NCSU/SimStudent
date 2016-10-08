/**
 * 
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import org.jdom.Element;

import cl.common.SolverActionType;
import cl.common.SolverOperation;
import cl.tutors.solver.SolverTutor;
import cl.tutors.solver.SolverTutorProblem;
import cl.tutors.solver.rule.SolverGoal;
import cl.utilities.Logging.Logger;
import cl.utilities.TestableTutor.InitializationException;
import cl.utilities.TestableTutor.InvalidParamException;
import cl.utilities.TestableTutor.InvalidStepException;
import cl.utilities.sm.SMParserSettings;
import cl.utilities.sm.SymbolManipulator;
import cl.utilities.sm.query.Queryable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerTracer;
import edu.cmu.pact.Utilities.trace;

/**
 * An interface to enable the Example Tracer to accommodate steps that invoke
 * Carnegie Learning's Solver Tutor.
 */
public class SolverMatcher extends VectorMatcher {
	
	/**
	 * For expression parsing in
	 * {@link scratchpadTool(Vector<String>, String, Vector<String>, ExampleTracerEvent)}.
	 */
	private static SymbolManipulator sm = new SymbolManipulator(SMParserSettings.HS_DEFAULT);

	/**
	 * Associate action codes from the UI with opcodes from {@link SolverTutor} and prompts.
	 */
	private static class SolverTutorAction {
		private final String studentAction;
		private final String opcode;
		private final String prompt;
		SolverTutorAction(String studentAction, String opcode) {
			this(studentAction, opcode, null);
		}
		SolverTutorAction(String studentAction, String opcode, String prompt) {
			this.studentAction = studentAction;
			this.opcode = opcode;
			this.prompt = prompt;
		}
		String getKey() { return studentAction; }
	}
	
	/** Translate Flash component's action values to SolverTutor's opCodes. */
	private static final SolverTutorAction[] flashComponent2SolverTutorAction = {
		
		new SolverTutorAction("Solver_requestaddtobothsides", "request", "Add "+SolverTutor.INPUT_BOX+" to both sides."),
		new SolverTutorAction("Solver_requestsubtractfrombothsides", "request", "Subtract "+SolverTutor.INPUT_BOX+" from both sides."),
		new SolverTutorAction("Solver_requestmultiplybothsides", "request", "Multiply both sides by "+SolverTutor.INPUT_BOX+"."),
		new SolverTutorAction("Solver_requestdividebothsides", "request", "Divide both sides by "+SolverTutor.INPUT_BOX+"."),
		
		new SolverTutorAction("Solver_addtobothsides", "add"),
		new SolverTutorAction("Solver_subtractfrombothsides", "subtract"),
		new SolverTutorAction("Solver_multiplybothsides", "multiply"),
		new SolverTutorAction("Solver_dividebothsides", "divide"),
		
		new SolverTutorAction("Solver_requestaddorsubtractterms", "clt", "Add/subtract terms in which subexpression?"),
		new SolverTutorAction("Solver_requestperformmultiplication", "mt", "Perform multiplication in which subexpression?"),
		new SolverTutorAction("Solver_requestsimplifyfractions", "rf", "Simplify which fraction?"),
		new SolverTutorAction("Solver_requestsimplifysigns", "simp", "Simplify signs in which subexpression?"),
		new SolverTutorAction("Solver_requestdistribute", "distribute", "Distribute factor in which subexpression?"),
		
		new SolverTutorAction("Solver_addorsubtractterms", "clt", "Add/subtract terms in which subexpression?"),
		new SolverTutorAction("Solver_performmultiplication", "mt", "Perform multiplication in which subexpression?"),
		new SolverTutorAction("Solver_simplifyfractions", "rf", "Simplify which fraction?"),
		new SolverTutorAction("Solver_simplifysigns", "simp"),                             // right??
		new SolverTutorAction("Solver_distribute", "distribute"),                          // right??
		
		new SolverTutorAction("Solver_factornumerator", "factors of numerator"),           // used??
		new SolverTutorAction("Solver_factordenominator", "factors of denominator"),       // used??
		new SolverTutorAction("Solver_restart", "Solver_restart"),
		new SolverTutorAction("Solver_finished", "done")
	};
	/*
	public static final SolverActionType AT_SIMP_SUBEX = new SolverActionType(
			"simp_subex",true,false);

	 * A parameterized simplification that affects some subexpression within the
	 * equation. In notypein mode, the user is prompted for an expression that
	 * defines the simplification operation. (In typein mode, the tutor presents
	 * a scratchpad that includes this prompt.) The user is also prompted for a
	 * side/subexpression on which to apply the simplification.

	public static final SolverActionType AT_SIMP_EXPR_SUBEX = new SolverActionType(
			"simp_expr_subex",true,false);

	 * An implicit transformation, affecting the entire equation. In notypein
	 * mode, the user is not prompted. In typein mode, a scratchpad is used.

	public static final SolverActionType AT_TRNS_IMPL_SP = new SolverActionType(
			"trns_impl_sp",true,true);

	 */
	
	/** Translate Flash component's action values to SolverTutor's opCodes. */
	private static Map<String, SolverTutorAction> flashComponent2SolverTutorActionMap = 
		new LinkedHashMap<String, SolverTutorAction>();

	static {
		for (int i = 0; i < flashComponent2SolverTutorAction.length; ++i) {
			String key   = flashComponent2SolverTutorAction[i].getKey();
			SolverTutorAction value = flashComponent2SolverTutorAction[i];
			flashComponent2SolverTutorActionMap.put(key, value);
		}
		cl.tutors.solver.test.Test.disableCache(); //otherwise we run out of memory
	}
	
	/** Instance name of the solver component on the student interface. */
	private List<String> selection;

	/** Original equation or expression to solve or simplify. */
	private String origProblemSpec;
	
	/** Current equation or expression to solve or simplify. */
	private String problemSpec;
	
	/** The tutor instance itself. */
	private SolverTutor solverTutor;

	/** {@link SolverTutor} parameter. */
	private boolean autoSimplify;

	/** {@link SolverTutor} parameter. */
	private boolean typeinMode;

	/** {@link SolverTutor} parameter. */
	private SolverGoal goal;

	/** Name for {@link #goal}. */
	private String goalName;	

	/** for the mult/div BtA layout. This will store the greatest width
	 * needed to display the problem descriptions. */
	protected double problemDescriptionMaxColumnWidth = 0;

	/**
	 * @return {@link SolverGoal#getName()} values from {@link SolverGoal#getAllGoals()}
	 */
	public static String[] getAllGoalNames() {
		SolverGoal[] goals = SolverGoal.getAllGoals();
		String[] result = new String[goals.length];
		for (int i = 0; i < result.length; ++i)
			result[i] = goals[i].getName();
		return result;
	}
	
	/**
	 * @param concat
	 * @param matchers
	 * @param matchers2
	 * @param matchers3
	 * @param actor
	 * @param autoSimplify
	 * @param typeinMode
	 * @param goalName) 
	 */
	public SolverMatcher(boolean concat, List<Matcher> sMatchers,
			List<Matcher> aMatchers, List<Matcher> iMatchers, String actor,
			String autoSimplify, String typeinMode, String goalName) 
			throws IllegalArgumentException, InitializationException  {

		super(concat, sMatchers, aMatchers, iMatchers, actor);

		if (sMatchers == null || sMatchers.size() < 1)
			throw new IllegalArgumentException("selection matchers null or empty");
		if (iMatchers == null || iMatchers.size() < 1)
			throw new IllegalArgumentException("input matchers null or empty");

		Matcher iMatcher = iMatchers.get(0);
		origProblemSpec = problemSpec = iMatcher.getSingle();
		this.autoSimplify = Boolean.parseBoolean(autoSimplify);
		this.typeinMode = Boolean.parseBoolean(typeinMode);
		this.goalName = goalName;
		this.goal = (goalName == null ? SolverGoal.DEFAULT_GOAL : SolverGoal.getGoalByName(goalName));
		init();
	}

	/**
	 * Create and initialize the {@link #solverTutor}.
	 */
	private void init() {
		if (trace.getDebugCode("solver")) trace.out("solver", "autoSimplify "+autoSimplify+", typeinMode "+typeinMode+
				", problemSpec: "+problemSpec);
		solverTutor = new SolverTutor();
		// sewall 2010-12-08: a TRESolverTutor parent would make us more like CL student runtime
		// and less like CL's testable tutor, but this needs more study first.
//		TRESolverTutor parent = new TRESolverTutor();
//		parent.setProperty(SolverTutor.SUBTYPE_PROP, SolverTutor.TOOL_SUBTYPE);  // as if student interface exists
//		solverTutor.setParent(parent);
//		ExampleTracerEvent result =	new ExampleTracerEvent(this,
//				new ExampleTracerSAI(getSelection(), getAction(), problemSpec)); 
		try {
			solverTutor.startProblem(problemSpec, null);
		} catch (Exception e) {
			trace.err("Error from SolverTutor.startProblem(\""+problemSpec+"\"): "+e+
					(e.getCause() == null ? "" : ";\n  cause "+e.getCause()));
			throw new IllegalArgumentException("Bad algebraic expression \""+problemSpec+"\": "+e);
		}
		try {
            solverTutor.setParameter("Solver", "AutoSimplify", Boolean.toString(this.autoSimplify));
            solverTutor.setParameter("Solver", "TypeinMode",  Boolean.toString(this.typeinMode));
//          solverTutor.setGoal(getGoal()); // FIXME : learn to use goals; also SolverMatcherPanel 
		} catch (InvalidParamException ipe) {   // programming error
			trace.err("Programming error in SolverMatcher constructor: "+ipe);
		}
	}

	/**
	 * @return {@link #goal}; if null, returns {@link SolverGoal#SIMPLIFY_EXPRESSION} or {@link SolverGoal#DEFAULT_GOAL}
	 */
	private SolverGoal getGoal() {
		SolverGoal result = goal;
		if (result == null) {
			if (solverTutor.getCurrentProblem().isSimpExpression())
				result = SolverGoal.SIMPLIFY_EXPRESSION;
			else
				result = SolverGoal.DEFAULT_GOAL;
		}
		if (trace.getDebugCode("solverdebug")) trace.outNT("solverdebug", "SolverMatcher.getGoal() returning "+result);
		return result;
	}

	/**
	 * Run the step in the {@link #solverTutor}.
	 * @param s
	 * @param a
	 * @param i
	 * @param result
	 * @return
	 */
	public boolean doStep(Vector<String> s, Vector<String> a, Vector<String> i,
			ExampleTracerEvent result) {
		if (trace.getDebugCode("solverdebug")) trace.out("solverdebug", "doStep("+s+","+a+","+i+")");
		try {
			String action = a.get(0);
			String opcode;
			SolverTutorAction sta = flashComponent2SolverTutorActionMap.get(action); 
			if (sta != null)
				opcode = sta.opcode;
			else if (action.length() >= 7 && action.substring(0,7).equalsIgnoreCase("Solver_"))
				opcode = action.substring(7);
			else
				opcode = action;
			
			boolean rtn = false;
			if ("Solver_restart".equalsIgnoreCase(action))
				rtn = restart(s, a, i, result);
			else if ("done".equalsIgnoreCase(opcode))  // CL's done rules want null input
				rtn = done(s, opcode, result);
			else if ("request".equals(opcode))    // must be in flashComponent2SolverTutorActionMap
				rtn = requestTransformationOperand(action, sta.prompt, result);
			else {
				String[] subExs = needScratchpadSubExs(opcode, i.get(0));
				if (subExs != null && subExs.length > 1)
					rtn = scratchpadTool(s, opcode, subExs, sta.prompt, result);
				else {
					String promptInput = (subExs != null && subExs.length > 0 ? subExs[0] : i.get(0)); 
					if (action.contains("request")) {
						String newPrompt = editPrompt(sta.prompt, promptInput);
						result.addIaMessage("promptLabel", result.s2v(promptInput),
								newPrompt, false); // false=>no input
					}
					rtn = solverTutor.doStep(s.get(0), opcode, promptInput, result);
//					if (result.getResult() != ExampleTracerTracer.CORRECT_ACTION
//							&& result.getInterfaceActions().size() < 1) {
//						addDefaultIaMessage(result);
//					}
				}
			}
			result.setFromSolver(true);
			return rtn;
		} catch (InvalidStepException ise) {
			trace.err("Error on step ("+s+","+a+","+i+": "+ise);
			ise.printStackTrace();
			return false;
		}
	}

	/**
	 * Create a default "nextEquation" or "nextExpression" step with the current expression.
	 * @param result
	 */
	private void addDefaultIaMessage(ExampleTracerEvent result) {
		String iaAction = (solverTutor.getCurrentProblem().isSimpExpression() ?
				"nextExpression" : "nextEquation");
		String iaInput = solverTutor.getCurrentProblem().getStringValue();
		result.addIaMessage(iaAction, iaInput, "");
	}

	/** Pattern for replacing "which {subexpression|fraction|...}" in prompt. */
	private static Pattern whichSubex = Pattern.compile("which ([a-z]+)");
	
	/**
	 * Replace any phrase "which {subexpression|fraction|...}" in the prompt with the promptInput.
	 * @param prompt
	 * @param promptInput
	 * @return edited prompt
	 */
	private String editPrompt(String prompt, String promptInput) {
		java.util.regex.Matcher m = whichSubex.matcher(prompt);
		StringBuffer result = new StringBuffer();
		int s = 0;
		while (m.find(s)) {
			int e = m.start();
			result.append(prompt.substring(s, e));
			result.append(promptInput);
			s = m.end();
		}
		result.append(prompt.substring(s, prompt.length()));
		return result.toString();
	}

	/**
	 * Send the UI a request for the operand for this transformation.
	 * @param action SAI element from UI
	 * @param prompt prompt for student
	 * @param result to store result
	 * @return false if this is an expression problem; else true
	 */
	private boolean requestTransformationOperand(String action, String prompt,
			ExampleTracerEvent result) {
		SolverTutorProblem currentProblem = getSolverTutor().getCurrentProblem();
		if (currentProblem.isSimpExpression()) {
			String currentExpression = currentProblem.getExpression().toString();
			int nHints = requestHint(result);
			result.addIaMessage("promptLabel", currentExpression, prompt);
			result.setTutorAdvice((String) null);
			result.setResult(ExampleTracerTracer.NULL_MODEL);
			result.setIncorrectMsg("Transformation actions are not applicable when no equation is present.");
			result.addIaMessage("nextExpression", currentExpression, currentExpression, false);
			return false;
		}
		result.addIaMessage("promptOperand", null, prompt);
		result.setResult(ExampleTracerTracer.NOT_A_TRANSACTION);
		return true;
	}

	/**
	 * Following the top half of {@link SolverMenuItem#actionPerformed(ActionEvent)}, get an array of
	 * subexpressions if this is a simplifying operation. 
	 * @param opcode 
	 * @param input the whole expression or equation
	 * @return array of strings for subexpressions to choose; null if not a simplifying operation
	 * @throws InvalidStepException
	 */
	private String[] needScratchpadSubExs(String opcode, String input) throws InvalidStepException {
		SolverOperation op = getOpByCode(opcode);
		if (!op.getActionType().getIsScratchpadAction())
			return null;
		if (typeinMode
				&& (op.getActionType() == SolverActionType.AT_SIMP_EXPR_SUBEX
						|| op.getActionType() == SolverActionType.AT_SIMP_SUBEX ))
			return null;
		String subcompProp = op.getSubcomponentBooleanProp();
		final String[] subExStrs;
		if( subcompProp == null ) {
			trace.err( "SolverMenuItem.actionPerformed: ERROR: could not find subcomponent prop for action: " + op );
			return null;
		}
		try			/* build an array of queryable expressions */
		{
			Queryable[] subExs = sm.runArrayScript( "components with property " + subcompProp, input );
			if( subExs == null || subExs.length == 0 )
			{
				if (trace.getDebugCode("solverdebug")) trace.outNT("solverdebug", "SolverMatcher.actionPerformed: no subcomps suitable for action: " + op );
				subExStrs = null; // we'll leave subcomp as null, and let the tutor handle it from there
			}
			else if( subExs.length == 1 )           // only one possible subexpression, so that's what we'll use
				subExStrs = new String[] {subExs[0].toString()};
			else if( op == SolverOperation.SIMP ) 	//bug 6580: simplify should not prompt for subexpressions
				subExStrs = new String[] {subExs[0].toString()}; 
			else
			{                          // put up a dialog to see which subexpression the user wants to muck with
				subExStrs = new String[subExs.length];
				for( int i = 0; i < subExs.length; i++ )
					subExStrs[i] = subExs[i].getStringValue();
			}
			return subExStrs;
		} catch (Exception e) {
			throw new InvalidStepException("Error analyzing action \""+opcode+"\", input \""+input+"\": "+e);
		}
	}

	/**
	 * Get a {@link SolverOperation} instance for this opcode.
	 * @param opcode
	 * @return
	 * @throws InvalidStepException if opcode is invalid
	 */
	private static SolverOperation getOpByCode(String opcode)
			throws InvalidStepException {
		SolverOperation op = SolverOperation.getOpByCode(opcode);
		if (op == null)
			throw (new InvalidStepException("solver operation \""+opcode+"\" undefined"));
		return op;
	}

	/**
	 * Check whether the problem is finished.
	 * @param s solver component instance name
	 * @param a ["done"]
	 * @param result
	 * @return true
	 */
	private boolean done(Vector<String> s, String a, ExampleTracerEvent result)
			throws InvalidStepException {
		// Solver wants null input on done step
		boolean rtn = solverTutor.doStep(s.get(0), a, null, result);	
		List<String> tutorAdvice = result.getTutorAdvice();
		if (!ExampleTracerTracer.CORRECT_ACTION.equalsIgnoreCase(result.getResult())
				&& (tutorAdvice == null || tutorAdvice.isEmpty()))
			result.setTutorAdvice("You have not finished yet.");
		return rtn;
	}

	/**
	 * Reinitialize the tutor at a prior step. Changes {@link #problemSpec} and
	 * calls {@link #init()}.
	 * @param s solver component instance name
	 * @param a ["Solver_restart"]
	 * @param i ["<i>equation from prior step</i>"]
	 * @param result
	 * @return true
	 */
	private boolean restart(Vector<String> s, Vector<String> a, Vector<String> i,
			ExampleTracerEvent result) {
		problemSpec = i.get(0);
		result.setResult(ExampleTracerTracer.CORRECT_ACTION);
		result.makeTutorSAI(s.get(0), a.get(0), i.get(0));
//		result.addIaMessage("nextEquation", i, "Restart at prior step.");  not needed: tool has eq
		init();
		return true;
	}

	/**
	 * Get a hint using the path that the interactive CL program uses, where
	 * hint requests are addressed to the SolverTutorProblem.
	 * @param result stuff the hints in here
	 * @return number of hints
	 */
	public int requestHint(ExampleTracerEvent result) {
		solverTutor.requestHint(result);
		result.setFromSolver(true);
		result.makeTutorSAI(getSelection(), null, null);
		List<String> hints = result.getTutorAdvice();
		int nHints = (hints == null ? 0 : hints.size());
		if (trace.getDebugCode("solverdebug")) trace.out("solverdebug", "nHints "+nHints+": "+hints);
		return nHints;
	}

	/**
	 * Ordinary match (for hints and even for steps) tests only selection and actor, to ensure this is a solver entry.
	 * @param selection should be solver component to match
	 * @param action ignored here
	 * @param input ignored here
	 * @param actor used as for other matchers
	 * @param vt argument to {@link #match(Vector, Vector, String, VariableTable)}
	 * @return true if matched
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.VectorMatcher#match(java.util.Vector, java.util.Vector, java.util.Vector, java.lang.String, edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable)
	 */
	public boolean match(Vector selection, Vector action, Vector input, String actor, VariableTable vt){
		boolean matched = match(selection, null, actor, vt);
		if (trace.getDebugCode("et"))
			trace.out("et", getClass().getSimpleName()+".match("+selection+","+selection+","+action+","+
					input+","+vt+") returns "+matched);
		return matched;
	}

	/**
	 * @return the {@link #solverTutor}
	 */
	public SolverTutor getSolverTutor() {
		return solverTutor;
	}

	/**
	 * Recreate the {@link #solverTutor} with problem {@link #problemSpec}.
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#reset()
	 */
	public void reset() {
		init();
	}

	/**
	 * Simulate the scratchpad tools in CL in prompting for a choice of subexpressions.
	 * @param s
	 * @param action
	 * @param subExs
	 * @param result
	 * @return
	 */
	private boolean scratchpadTool(Vector<String> s, String action,
			String[] subExs, String prompt, ExampleTracerEvent result) {
		result.addIaMessage("chooseSubexpression", new Vector<String>(Arrays.asList(subExs)), prompt);
		result.setResult(ExampleTracerTracer.NOT_A_TRANSACTION);
		return true;
	}

	/**
	 * For BtA expressions, this will return a count of the components
	 * that exist in the given expression.  The catch: we have 3 different
	 * source-expression possibilities.  Add/Sub use polynomials, so
	 * a simple call to "length of terms" gives exactly what we need.  The 
	 * Mult/Div problems are a bit trickier (and based upon factors, not 
	 * terms; length of terms will always be '1'):  
	 * if the operation between components is Division, then the length of
	 * factors is == '1',  but the given expression is a Ratio, we return '2';
	 * the numerator and denominator.  
	 * If we are to multiply then all we need is the "length of factors".  
	 * @return int the number of components withint the top-level expression
	 * @see cl.ui.modules.solver.SolverToolProblem#determineTheNumberOfTerms()
	 */
	private int determineTheNumberOfTerms(String problemAsText, String action) {
	    int result = -1;
	    action = action.toLowerCase();
	    /** three different outcomes...polynomials are the easiest to account for... */
	    try {
	        /** check the easiest case first */
	        if (action.contains("add") || action.contains("subtract")) {
		        result = Integer.parseInt(sm.runScript("length of terms", problemAsText));
	        }
	        /** use the if-conditional just-in-case... */
	        else if (action.contains("multiply") || action.contains("divide")) {
	            result = Integer.parseInt(sm.runScript("length of factors", problemAsText));
	            /** if the result==1, then check if the expression is a ratio */
	            if (result==1) {
	                boolean opIsDivision = new Boolean(sm.runScript("isRatio", problemAsText)).booleanValue();
	                /** and now check...if it's a div-op then we return '2' */
	                if (opIsDivision) {
	                    result = 2;
//	                    this.termOp = "&#0247";  // OPERATOR_DIVIDE_CODE
	                }
	            }
	            else {	// it's a term-op
//                    this.termOp = "&#0215";  // OPERATOR_MULTIPLY_X;
	            }
	        }
	    } catch (Exception e) {
	        Logger.log(e);
	        result = -1;	// this will be an unrecoverable error...
	    }		
	    
	    return result;
	}
	
	/**
	 * For Addition/Subtraction problems in BtA(2005), this will retrieve
	 * the operator used for the current problem expression
	 * @return String representation of the operator
	 * @see cl.ui.modules.solver.SolverToolProblem#determineTheOperationOfTerms()
	 */
	private String determineOperationOfTerms(String problemAsText) {
	    String secondTermOp = "";
	    try {
	        secondTermOp = sm.runScript("operator of term 2", problemAsText);
	    } catch (Exception e) {
	        Logger.log(e);
	        return null;	// this will be an unrecoverable error...
	    }		
        return secondTermOp;
	}

	/**
	 * @return {@link SolverTutor#isDone(String)}
	 */
	public boolean isDone() {
		// FIXME solverTutor.isDone() doesn't work until after cycleTutorDone()
		return solverTutor.isDone("dummy");
	}

	/**
	 * Tell how many traversals a visit to this link represents. For some
	 * matchers, such as {@link SolverMatcher}, a visit may not be the same as a traversal.
	 * @return constant 1 for this default implementation
	 */
	public int getTraversalIncrement() {
		return isDone() ? 1 : 0;
	}

	/**
	 * @return the {@link #autoSimplify}
	 */
	public boolean getAutoSimplify() {
		return autoSimplify;
	}

	/**
	 * @return the {@link #typeinMode}
	 */
	public boolean getTypeinMode() {
		return typeinMode;
	}

	public String getGoalName() {
		return goalName;
	}

	/**
	 * @return the {@link #problemSpec}
	 */
	public String getProblemSpec() {
		return problemSpec;
	}

	/**
	 * Override to set solver-specific attributes for the top-level XML element.
	 * @param elt Element to receive the attributes
	 */
	protected void setXMLAttributes(Element elt) {
		elt.setAttribute("AutoSimplify", Boolean.toString(getAutoSimplify()));
		elt.setAttribute("TypeinMode", Boolean.toString(getTypeinMode()));
		if (getGoalName() != null)
			elt.setAttribute("Goal", getGoalName());
	}	
}
