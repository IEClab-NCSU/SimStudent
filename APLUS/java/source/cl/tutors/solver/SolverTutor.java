// *********************************************************************
// 
//                           Copyright (C) 2003
//                         Carnegie Learning Inc.
// 
//                          All Rights Reserved.
// 
//  This program is the subject of intellectual property rights licensed
//  by Carnegie Learning Inc from Carnegie Mellon University
// 
//  This legend must continue to appear in the source code despite
//  modifications or enhancements by any party.
// 
// *********************************************************************

//version of the equation solving tutor that interacts with the applet interface


package cl.tutors.solver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//import java.util.MissingResourceException;
import java.util.Set;
import java.util.Vector;
import java.util.Properties;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import cl.common.CL_Problem;
import cl.common.CL_TutorMessage;
import cl.common.CommonKeys;
import cl.common.PropertyConstants;
import cl.common.SolverActionType;
import cl.common.SolverConstants;
import cl.common.SolverKeys;
import cl.common.SolverOperation;
import cl.communication.AddressableNode;
import cl.communication.MessageDeliveryException;
import cl.communication.MessageObject;
import cl.communication.MessagingAddress;
import cl.communication.MessagingNode;
import cl.communication.MessagingNodeChildren;
import cl.communication.SendMessage;
import cl.tutors.solver.rule.BugRuleSet;
import cl.tutors.solver.rule.Rule;
import cl.tutors.solver.rule.RuleDefiner;
import cl.tutors.solver.rule.RuleMatchInfo;
import cl.tutors.solver.rule.RuleSet;
import cl.tutors.solver.rule.SkillRule;
import cl.tutors.solver.rule.SkillRuleSet;
import cl.tutors.solver.rule.SolverGoal;
import cl.tutors.solver.rule.SubexRule;
import cl.tutors.solver.rule.TypeinBugRule;
import cl.tutors.solver.rule.TypeinBugRuleSet;
import cl.tutors.solver.rule.TypeinSkillRuleSet;
import cl.tutors.tre.ResearchProtocol;
import cl.tutors.tre.Tutor;
import cl.tutors.tre.solver.TRESolverTutor;
import cl.utilities.StringMap;
import cl.utilities.Logging.Logger;
import cl.utilities.TestableTutor.InvalidParamException;
import cl.utilities.TestableTutor.SAI;
import cl.utilities.TestableTutor.SerializedState;
import cl.utilities.TestableTutor.State;
import cl.utilities.TestableTutor.TestableTutor;
import cl.utilities.sm.BadExpressionError;
import cl.utilities.sm.DivideByZeroException;
import cl.utilities.sm.Equation;
import cl.utilities.sm.Relation;
import cl.utilities.sm.SMParserSettings;
import cl.utilities.sm.SymbolManipulator;
import cl.utilities.sm.query.Queryable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerTracer;
import edu.cmu.pact.Utilities.trace;

//mainline for Solver tutor application

public class SolverTutor implements TestableTutor,Serializable,SolverConstants,MessagingNode {
    /**The current problem being solved*/
    private SolverTutorProblem currentProblem = null;

    /**The original form of the problem being solved*/
    private SolverTutorProblem originalProblem = null;

    /**The goal for the current problem (dictates which rules are used)*/
    private SolverGoal goal = SolverGoal.DEFAULT_GOAL;

    /**In typein mode, the action from the step that got us to the
       current state.*/
	private SolverOperation prevTIaction;
    /**In typein mode, the input from the step that got us to the
       current state.*/
	private String prevTIinput;
    
	private SkillRule[] currentRules=null;

	/**Indicates whether the rules in currentRules correspond to a
       correct student action (true) or an incorrect student action
       (false)*/
	private boolean currentRulesPassed = false;
	/** bug 11504, 11673: since this property will be accessed in multiple
     * locations, we'll use a boolean to store the property */
	private boolean showClassificationMenu = false;

	private TRESolverTutor treParent = null;

    private static SymbolManipulator sm = new SymbolManipulator(SMParserSettings.HS_DEFAULT);

    private static SymbolManipulator smUnsimp = new SymbolManipulator(SMParserSettings.HS_DEFAULT);

    static{
		sm.setMaintainVarList(true);
		/*settings as specified by Rox, 12/20/00*/
		sm.autoStandardize = false;
		sm.autoSimplify = true;
		sm.autoDistribute = false;
		/*decided to turn off autosort (~ 05/07/01)*/
		sm.autoSort = false;
		sm.distributeDenominator = false;
		sm.setOutputType(SymbolManipulator.intermediateOutput); //produce ascii with implied parens

        smUnsimp.setMaintainVarList(true);
        /*for smUnsimp, the default settings should be okay --
          simplifications are off by default*/
    }

    /**Controls whether the scratchpad for CLT and PM will work with
       the entire side of the equation or just the applicable
       sub-expression (bug 6607)*/
	private boolean cltPmWholeSide = false;

	/**This handles all of the <code>MessagingNode</code> methods*/
	private MessagingNodeChildren children = new MessagingNodeChildren(this);

	// TestableTutor stuff
	private boolean testReady = false;
	//private SolverFrame testSolverFrame = null;
    private static final SolverOperation[] tempValidActions;

    /** Category for all skills generated by solver. */
	public static final String SOLVER_SKILL_CATEGORY = "Solver";
    
    static{
    	SolverOperation[] allOps = SolverOperation.getAllOperations();
    	/*exclude the generic 'simplify' op for testing*/
    	tempValidActions = new SolverOperation[allOps.length-1];

    	int j=0;
    	for(int i=0;i<allOps.length;i++){
    		if(allOps[i] != SolverOperation.SIMP){
    			tempValidActions[j++] = allOps[i];
    		}
    	}
    }

    private boolean testableTutorDone = false;
	
	private HashSet ruleUpdates;

	private boolean useTypein = false;
	/** Tool properties used to have the user enter the starting expression
	 * while in the 'LESSON' Solver subtype. */
	private boolean useInitialProblemTypein = false;
	private SolverOperation initialTypeinOperation;
	
	
	public SolverTutor () {
		if (trace.getDebugCode("solvertraceallrules"))
			Rule.setTraceAllRules(true);
		
		if(!RuleDefiner.rulesReady()){
			RuleDefiner.defineRules();
		}
		else if(Logger.LoggingOn){
			Logger.log("solverdebug","SolverTutor init: rules already defined.");
		}
		/*this is cleared after every problem, so it should stay
          pretty small*/
		ruleUpdates = new HashSet(19);
		//if(System.getProperty("solvertrace") != null){
		//	Rule.setTraceAllRules(true);
		//}
	}

	private RuleSet strategicRules(){
		if (Logger.LoggingOn)
			Logger.log("solverdebug", "strategicRules() goal "+goal);
		return RuleDefiner.getStrategicRules(true,goal);
	}

	private SkillRuleSet skillRules(){
		return RuleDefiner.getSkillRules(true,goal);
	}

	private TypeinSkillRuleSet typeinSkillRules(){
		return RuleDefiner.getTypeinSkillRules(true,goal);
	}

	private BugRuleSet bugRules(){
		return RuleDefiner.getStrategicBugRules(true,goal);
	}

	private TypeinBugRuleSet typeinBugRules(){
		return RuleDefiner.getTypeinBugRules(true,goal);
	}

	public void showSkills(){
		if(!ruleUpdates.isEmpty()){
			if (Logger.LoggingOn) {
				Logger.log("solverdebug","SolverTutor: skills updated: ");
				for(Iterator i = ruleUpdates.iterator();i.hasNext();){
					Logger.log("solverdebug","  " + i.next());
				}
			}
		}
	}

	/*'equation' might just be an equation, or it might be an equation
	  plus a variable to solve for, like "ax+b=c;x", which means
	  'solve the equation "ax+b=c" for the variable "x"'.  If there is
	  no target variable specified, we just use the first variable
	  that occurs in the equation as a guess.  There might also be a
	  second semicolon followed by a prompt for the student.  We don't
	  care about this here, so it's disregarded.*/
	public void startProblem(String problem) {
		startProblem(problem, null);
	}

	/*'equation' might just be an equation, or it might be an equation
	  plus a variable to solve for, like "ax+b=c;x", which means
	  'solve the equation "ax+b=c" for the variable "x"'.  If there is
	  no target variable specified, we just use the first variable
	  that occurs in the equation as a guess.  There might also be a
	  second semicolon followed by a prompt for the student.  We don't
	  care about this here, so it's disregarded.*/
	public void startProblem(String problem, ExampleTracerEvent result) {
		if (Logger.LoggingOn) {
			Logger.log( "SolverTutor.startProblem: '"+problem+"'");
		}
        //erase all the information about what skills have been updated already
        ruleUpdates.clear();
        testableTutorDone = false;
        removeChild(originalProblem);

        /**
         * These two properties need to be set before the SolverTutorProblem
         * is created, so that it can use them when determining the 
         * original problem's behavior.
         */
        String useInitTI = null;
        String opString = null;
        if (treParent!=null) {	// if using JUnit, the treParent doesn't exist
            useInitTI = (String)treParent.getProperty(USE_INIT_TYPEIN_PROP);
            opString = (String)treParent.getProperty(INIT_TYPEIN_OP_PROP);
        }
	    setInitialTypein(useInitTI!=null && useInitTI.equalsIgnoreCase(TRUE_BOOLEAN_STRING));
	    setInitialTypeinOp(SolverOperation.getOpByCode(opString));

	    originalProblem = currentProblem = new SolverTutorProblem(problem,this);

		// this breaks solver testcode since there's no mc
	    // MessageCommunicator.logMessagingHierarchy(  MessageCommunicator.getTutorCommunicator().getRoot());

        StringMap props = new StringMap(PROBLEM_PROP,originalProblem.toString());
        props.setProperty(PROMPT_PROP,originalProblem.getPrompt());

		if( getTREParent() != null )  // not testable tutor
		{
			/*update done menu*/
			//mmmBUG TODO is this the right precedence?
			String subtype = (String)getTREParent().getProperty(SUBTYPE_PROP);
			/** bug 10587: the tool needs to know the tutor subtype in order to supress
			 * the initial solver problem. */
	        props.setProperty(SUBTYPE_PROP, subtype);
//			if (getGoal().equals(SolverGoal.SIMPLIFY_ADDSUB_MIXED_EXPRESSION)
//			        || getGoal().equals(SolverGoal.SIMPLIFY_MULTDIV_MIXED_EXPRESSION)) {
	    	/** bug 11504, 11673: the classify/done menu is shown by a toolspec property */
	        showClassificationMenu = new Boolean((String)getTREParent().getProperty(SHOW_CLASSIFY_MENU_PROP)).booleanValue(); // even if null, the boolean will be false
			if (showClassificationMenu) {
				if(subtype.equalsIgnoreCase(TOOL_SUBTYPE)){
					getTREParent().setDoneMenuMode(TRESolverTutor.DONE_TOOL);
				}
				else if(originalProblem.isSimpExpression()){
					getTREParent().setDoneMenuMode(TRESolverTutor.DONE_SIMP);
				}
				else{
					getTREParent().setDoneMenuMode(TRESolverTutor.DONE_SINGLE_EQ);
				}
			}
			else {
				getTREParent().setDoneMenuMode(TRESolverTutor.DONE_NO_MENU);
			    if (Logger.LoggingOn) {
	                Logger.log("solverdebug", "SolverTutor.startProblem :: Supressing the 'Classify Solution' menu...");
	            }
			}

		    /**
		     * Addtional tests to set/reset the goals for free-form entry problems.
		     * Depending upon whether the user enters an expression or equation, we'll 
		     * want to use a different set of rules.
		     * Setting the goal changes the ruleSet used, but only re/set if the 
		     * subtype is <code>TOOL_SUBTYPE</code>.
		     */
	        /** related to bug 8645, since we need the solver goal to change 
	         * when a quadratic is entered in order to use the appropriate rules. */
			if(subtype.equalsIgnoreCase(TOOL_SUBTYPE)) {	// if subtype==Tool
			    if (originalProblem.isSimpExpression()) {	// if the new problem is an expression
			        /** At least initially, the expression will either use Quadratic Expr or 
			         * just (plain) Expression rules. */
			        if (containsQuadraticExpr(originalProblem.getLeftOrExpression())) {
				        setGoal(SolverGoal.SIMPLIFY_QUADRATIC_EXPRESSION);
		            } 
			        else {
				        setGoal(SolverGoal.SIMPLIFY_EXPRESSION);
		            }
			    } 
			    else {	// the new problem is an equation
			        /** an equation (initially) will have three possible sets to choose from:
			         * Quadratics, Trig functions, and Linear equations...First, check for Quads... */
			        if (containsQuadraticExpr(originalProblem.getLeftOrExpression())
			                || containsQuadraticExpr(originalProblem.getEquation().getRight().toString())) {
				        setGoal(SolverGoal.SOLVE_QUAD_CUBIC_EQUATION);
		            } 	// check for Trig functions
			        else if (containsFunctionApp(originalProblem.getLeftOrExpression())
			                || containsFunctionApp(originalProblem.getEquation().getRight().toString())) {
				        setGoal(SolverGoal.SOLVE_TRIG_EQUATION);
		            } 
			        else {	// we use our default if none of the otehr tests pass 
				        setGoal(SolverGoal.SOLVE_LINEAR_EQUATION);
		            }
			    }
			}
			if (Logger.LoggingOn)
				Logger.log("solverdebug", "startProblem("+problem+") subtype "+subtype+", goal "+goal);
			
			/**
			 * Use the property Strings defined before the creation of the STP
			 * within the property object sent to the tool.
			 */
		    props.setProperty(USE_INIT_TYPEIN_PROP, useInitTI);
		    props.setProperty(INIT_TYPEIN_OP_PROP, opString);
		    
			if (Logger.isLoggingOn()) {
				Logger.log("solvertrace", "SolverTutor.startProblem :: ***Solver Goal set to [" + goal + "]");
				Logger.log("solvertrace", "SolverTutor.startProblem :: Creating new STP with properties [" + props + "]");
			}
			if (result == null)
				SendMessage.sendCreate(treParent.getMessagingAddress(),PROBLEM_PROP,props);
		}
    }

    public String getOriginalProblem(){
        return currentProblem.getOriginalProblemString();
    }
    public SolverTutorProblem getCurrentProblem(){
        return currentProblem;
    }

    public String getTargetVar(){
        return currentProblem.getTargetVar();
    }

    /**
     * Get the PrevTIinput value.
     * @return the PrevTIinput value.
     */
    public String getPrevTIinput() {
        return prevTIinput;
    }

    /**
     * Set the PrevTIinput value.
     * @param newPrevTIinput The new PrevTIinput value.
     */
    public void setPrevTIinput(String newPrevTIinput) {
        this.prevTIinput = newPrevTIinput;
    }

    /**
     * Get the PrevTIaction value.
     * @return the PrevTIaction value.
     */
    public SolverOperation getPrevTIaction() {
        return prevTIaction;
    }

    /**
     * Set the PrevTIaction value.
     * @param newPrevTIaction The new PrevTIaction value.
     */
    public void setPrevTIaction(SolverOperation newPrevTIaction) {
        this.prevTIaction = newPrevTIaction;
    }

	// get valid actions dynamically
	private synchronized SolverOperation[] getValidActions(){
		if(treParent == null){
			//testable tutor case
			return tempValidActions;
		}
		else{
			return treParent.getValidActions();
		}
	}

	/**getTutorAction tells the tutor what to do in response to the
       user action*/
	private int getTutorAction(SolverOperation op) {
		if (op == SolverOperation.NEW ||
				op == SolverOperation.NEWEX ||
				op == SolverOperation.ERASE){
			return UPDATE_NO_CYCLE;
		}
		else if (op == SolverOperation.HINT){
			return GIVE_HINT;
		}
		else if (op == SolverOperation.LEFT ||
				op == SolverOperation.RIGHT ||
				op == SolverOperation.EXPR){
			return CYCLE_TYPEIN;
		}
		else if (op == SolverOperation.TERM1 ||
				op == SolverOperation.TERM2){
			return CYCLE_TYPEIN_B2A;
		}
		else if(op == SolverOperation.EQN){
			return CYCLE_TYPEIN_EQUATION;
		}
		else if (op == SolverOperation.DONE ||
				op == SolverOperation.DONE_NO_MENU ||  // added for bug 11522
				op == SolverOperation.DONENOSOLUTION ||
				op == SolverOperation.DONEINFINITESOLUTIONS){
			return CHECK_DONE;
		}
		else{
			/*all other "regular" ops just cycle the tutor*/
			return CYCLE;
		}
	}

	/**
	 * This is the main entry point for student actions coming from the
	 * interface. The real tutoring work is delegated to a helper method based
	 * on the type of the operation.
	 * @param newCurrentProb new value for {@link #currentProblem}
	 * @param op
	 * @param input
	 * @param result
	 * @return true if step ok, false if illegal
	 */
	public boolean checkStudentAction(SolverTutorProblem newCurrentProb,
			SolverOperation op, String input, ExampleTracerEvent result) {
		currentProblem = newCurrentProb;
		return checkStudentAction(op, input, result);
	}
	/** 
	 * Split this method so the TRESolverTutor could access the cSA method
	 * without changing (or specifying) a new currentProblem or interrupting
	 * the current processing/access of the general method.
	 * @param op
	 * @param input
	 * @param result stuff this with evaluation data
	 * @return true if step ok, false if illegal
	 */
	public boolean checkStudentAction(SolverOperation op, String input,
			ExampleTracerEvent result) {
//		currentProblem = newCurrentProb;

		if (Logger.LoggingOn) {
			Logger.log("solverdebug","ST.cSA(" + op + "," + input + ")");
		}
		
		int toDo = getTutorAction(op);
		if (toDo == CYCLE) {
			checkStudentStandardAction(op,input,result);
		}
		else if (toDo == UPDATE_NO_CYCLE) {
			// do nothing here
		}
		else if (toDo == GIVE_HINT) {
			getTutorHint(result);
		}
		else if (toDo == CYCLE_TYPEIN) {
			checkStudentTypeinAction(op,input,result);
		}
		else if (toDo == CYCLE_TYPEIN_B2A) {
			checkB2AStudentTypeinAction(op,input,result);
		}
		else if (toDo == CYCLE_TYPEIN_EQUATION){
			checkStudentTypeinEquation(op,input,result);
		}
		else if (toDo == CHECK_DONE) {
			cycleTutorDone(op, input, result);
		}
		if (result != null) {
			if(Logger.isLoggingOn())
				Logger.log("solverdebug", "ST.cSA("+op.getOpcode()+","+input+") result "+
						result.getResult()+", "+result.getInterfaceActions()+", skills "+
						result.getSkillNames());
			return ExampleTracerTracer.CORRECT_ACTION.equalsIgnoreCase(result.getResult());
		} 
		return true;   // dummy return 
	}
	/**
	 * The logic from the (old) SolverFrame needs a new home: the user should be prevented from 
	 * multiplying or dividing by zero.  Also, to prevent equations like "-1y=0" from becoming "-1=0/y" 
	 * (bug 8654) we'll need to check the variable for an evaluation of zero with the SM.solverFor() method.
	 * The input may be an expression, and also needs evaluation for zero equivalence (ex. x+2=0;x, input==x+2)
	 * 
	 * NOTE: We already know that we're working with an equation at this point in the processing...
	 * @param op SolverOperation the transformation action 
	 * @param input String user entered value for equation transformation
	 * @return String assembled error message to return to the user
	 */
	private String checkForMultDivByZero(SolverOperation op,String input) {
        /** explicitly disallow multiplication and division by zero 
         * (we check string equality directly rather than using exactEqual()
         * to avoid round-off errors and bug messages like ".0001 is equal to 0").*/
        try {
            /** this conditional pulled straight from 2004's SolverFrame, with mods to the communication call (+ boolean return) */
            String errorText = null;
            if ( sm.cannonicalize( input ).equals( "0" ) ) {
                if ( input.equals( "0" ) ) {
                    // msg == You cannot {0} by zero.
                    return MessageFormat.format(SolverKeys.getString(SolverKeys.OP_BY_ZERO),new Object[] {op.getOpcode()});
                }
                else {
                    // msg == {0} is equal to 0.  You cannot {1} by zero.
                    return MessageFormat.format(SolverKeys.getString(SolverKeys.OP_INPUT_ZERO),new Object[] {input, op.getOpcode()});
                }
            }
            /*also disallow division by the variable, if the variable is (going to turn out to be) equal to 0.*/
            if ( op==SolverOperation.DIVIDE ) {
                /** find what variable we're solving the equation for */
                String targetVar = currentProblem.getTargetVar();
                /** get the entire list of variables used in the user input.
                 * This will be used to determine if our target variable is within the 
                 * denominator, given that we're processing a Divide action */
                Vector inputVars = sm.variablesUsed(input);

                /** we use these in the new solve-for as well as the fall-back code from 2004's SolverFrame */
                String left = currentProblem.getLeftOrExpression();
                String right = currentProblem.getEquation().getRight().toString();
                
                /** the input could be an expression containing the variable. Example: x+2=0; divide by "x+2"
                 * We only want this section to process if the target variable is used within the denominator.
                 * If this section processes normally (ie. sm.solveFor() returns a non-null or "" value)
                 * it effectively replaces the code section below that comes from the 2004 SolverFrame.
                 * 
                 * NOTE: The 2004 code is used as a fall-back if solveFor() fails to determine the value of
                 * the target variable, but only accounts for the input being exactly-equal tot he target variable */
                if (targetVar!=null && inputVars!=null && inputVars.contains(targetVar)) {
                    /** find the solution of our target variable */
                    String targetVarResult = sm.solveFor(left, right, targetVar);
                    String subsInput = null;
                    /** if we found the result, test the input expression...otherwise, we'll hit the fall-back test */
                    if (targetVarResult!=null && !targetVarResult.equals("")) {
                        subsInput = sm.substitute( input, targetVar, targetVarResult );
                        if (sm.cannonicalize(subsInput).equals("0")) {
                            // msg == {0} is equal to 0.  You cannot {1} by zero.
                            return MessageFormat.format(SolverKeys.getString(SolverKeys.OP_INPUT_ZERO),new Object[] {input, op.getOpcode()});
                        }
                        /** the denominator is NOT zero, return 'null' to continue processing */
                        else {
                    		if (Logger.LoggingOn) {
                    			Logger.log("solverdebug", "SolverTutor.checkForMultDivByZero :: the imput passed the tests, returning null to continue processing...");
                    		}
                            return null;
                        }
                    }
                }
                
                /** for one reason or another the solveFor() has falied, so its onto plan B...
                 * ...use the updated SolverFrame logic */
                /*check for division by common expressions containing the variable.
                This is not intended to be comprehensive, just to catch the most
                common problem cases (see bug 8654).*/
                String[] vars = null;
        		String var = null;
        		/** test to see if we can ID the target variable directly */
                if (targetVar!=null && !targetVar.equals("")) {
                    var = targetVar;	// use it for the remaining logic
                }
                /** if that fails, then fallback to SM's getVarList() */
                else {
                    vars = SymbolManipulator.getVarList();	// only make the call if we can't identify the target var directly
                    if (vars != null && vars.length>0) {
	            		var = vars[0];	// assume we're only dealing with one variable
                    }
                }

                /** now, just like the solveFor() attempt, see if we have the target variable within the input */
        		boolean inputContainsVar = false;
                try{
                    /** if the collection of variables from the solveFor() is null or empty
                     * then use the process from SolverFrame */
                    if (inputVars==null || inputVars.size()==0) {
	            		/*get factors of msg and check each for equality w/ var*/
	            		Queryable[] factors = sm.runArrayScript("factors", input);
	            		for(int i=0;!inputContainsVar && i<factors.length;i++){
	            			if(sm.algebraicEqual(var,factors[i].getStringValue())){
	            				inputContainsVar = true;
	            			}
	            		}
                    }
                    else {	// we have an array of all variables used in the input expression, simply match against 'var'
                        if (inputVars.contains(var)) {
            				inputContainsVar = true;
                        }
                    }
                }
                catch(BadExpressionError bee){
                	/*ignore, assume msg does not contain var*/
                }
                catch(NoSuchFieldException nsfe){
                	/*ignore, assume msg does not contain var*/
                }

                /** if the target variable is within the user's input, take a shot at evaluating
                 * whether they are trying to divide by zero */ 
                if ( inputContainsVar ) {
                    try {
                        left = sm.substitute( left, var, "0" );
                        right = sm.substitute( right, var, "0" );
                        if ( sm.algebraicEqual( left, right ) ) {
                            // msg == {0} is equal to 0.  You cannot {1} by zero.
                            return MessageFormat.format(SolverKeys.getString(SolverKeys.OP_INPUT_ZERO),new Object[] {input, op.getOpcode()});
                        }
                    }
                    catch ( DivideByZeroException dbze ) {
                        /*doesn't necessarily indicate something bad; could happen for eqns like 1/x=2*/
                    }
                }
            }
        } catch ( BadExpressionError bee ) {
            Logger.log( "Bad argument to " + op + " ..." + bee );
//			Logger.log(bee);
        }
        /** if we hit this point, no message needs to be sent to the user and we should continue processing as normal */
        return null;
	}
	
	/**
	 * This method cycles the tutor on a "standard" (aka strategic) student
	 * action, calculates the result of the step, and then notifes the interface
	 * of the result.
	 * @param op
	 * @param input
	 * @param result if not null, pack results here & don't send to interface
	 */
	private void checkStudentStandardAction(SolverOperation op, String input,
			ExampleTracerEvent result) {
		/*stash the operation and input for later use by typein (bug) rules*/
		prevTIaction = op;
		prevTIinput = input;

		/** we've had problems with the TTF widgets submitting multiple times
		 * upon a single focus-lost.  Check if the currentProblem is in an incomplete
		 * state before continuing... */
		if (useTypein && !currentProblem.getTypeinCompleted()) { // if a typein is incomplete, we shouldn't even be in this method
			if (Logger.isLoggingOn()) {
				Logger.log("solverdebug", "SolverTutor.checkStudentStandardAction.op, input :: The current problem is incomplete ["+ currentProblem.getMessagingName() + "]; returning");
			}
			return;
		}

		/**
		 * Since the next action is to match the Rules, the prevention of multiplying 
		 * or dividing by zero needs to occur...
		 * 
		 * The logic from the (old) SolverFrame needs a new home: the user should be prevented from 
		 * multiplying or dividing by zero.  Also, to prevent equations like "-1y=0" from becoming "-1=0/y" 
		 * (bug 8654) we'll need to check the variable for an evaluation of zero with the SM.solverFor() method.
		 * The input may be an expression, and also needs evaluation for zero equivalence (ex. x+2=0;x, input==x+2)
		 * 
		 * NOTE: a difference from the pre-2005 code is we're also going to check the current 
		 * problem to make sure we're working with an equation.  If the problem is an expression, niether
		 * of these two operations are valid and will be blocked elsewhere in processing.
		 */
		if (currentProblem.isSolveEquation() && (op==SolverOperation.MULTIPLY || op==SolverOperation.DIVIDE)) {
		    String errorMessage = checkForMultDivByZero(op, input);	// the logic was too unwieldy to be directly in this method
		    if (errorMessage!=null && !errorMessage.equals("")) {	// ....so, determine if we should continue processing or we should return 
				if (Logger.LoggingOn) {
					Logger.log("solverdebug", "SolverTutor.checkStudentAction :: Sending an error message to the user: [" + errorMessage + "]; discontinue processing current action...");
				}
				if (result == null)
					SendMessage.sendShowJITMessage(currentProblem.getMessagingAddress(),
							new CL_TutorMessage[]{new CL_TutorMessage(errorMessage)},0);
				else {
					result.setIncorrectMsg(errorMessage+" Please delete this step and try again.");
					result.setResult(ExampleTracerTracer.INCORRECT_ACTION);
					result.makeTutorSAI(null, op.getOpcode(), input);
				}

		        return;	// stop processing the current action by returning out of this method
		    }
		}	// end of M/D test
		/**	BUG 8710 :: SPK 5/13/05 
		 * Another condition we need to check for, before cycling the tutor: Prevent a Simplification
		 * action if an incomplete initial typein field exists (bug 8710).
		 */
	    if (Logger.isLoggingOn()) {
	        Logger.log("solver", "op "+op+", originalProblem "+originalProblem);
	        Logger.log("solverdebug", 
	                "SolverTutor.checkStudentAction :: useInitialTypein() [" + useInitialTypein() 
	                + "] op.getActionType() [" + op.getActionType() + "][" + isSimpMenuItem(op.getActionType()) 
	                + "] originalProblem.getState() [" + originalProblem.getState() + "]");
	    }
		if (useInitialTypein()	// only for bug 8710 items 
		        && isSimpMenuItem(op.getActionType())	// action(s) from the Simplfication menu 
		        && originalProblem.getState() == LEFTNOTSET	// ...and the original problem isn't approved yet 
		        ) {
//		    String blockActionMessage = "You cannot perform a Simplification action until the type-in entry is approved.";
		    String blockActionMessage = SolverKeys.getString(SolverKeys.OP_ACTION_RESTRICTED);
		    if (Logger.isLoggingOn()) {
		        Logger.log("solverdebug", "SolverTutor.checkStudentAction :: Sending an error message to the user: [" + blockActionMessage + "]; discontinue processing current action...");
		    }
		    if (result == null)
		    	SendMessage.sendShowJITMessage(currentProblem.getMessagingAddress(),
		    			new CL_TutorMessage[]{new CL_TutorMessage(blockActionMessage)},0);
		    else {
		    	result.setIncorrectMsg(blockActionMessage);
		    	result.setResult(ExampleTracerTracer.INCORRECT_ACTION);
				result.makeTutorSAI(null, op.getOpcode(), input);
		    }
	        return;	// stop processing the current action by returning out of this method
		}
        if (result != null) {
        	result.setResult(ExampleTracerTracer.NULL_MODEL);
			result.makeTutorSAI(null, op.getOpcode(), input);
        }
		/*cycle the tutor on the student's action/input*/
		RuleMatchInfo rmi = cycleTutor(op, input, result);

        /*if we auto-corrected the input (but not the action),
          store the corrected input for use in typein bug rules*/
        if(rmi != null){
        	if((rmi.getAction() != null &&
        			rmi.getAction() == op) &&
					(rmi.getInput() != null &&
							!rmi.getInput().equals(input))){
        		prevTIinput = rmi.getInput();
        	}

        	op = rmi.getAction();
        	input = rmi.getInput();
            
            if (result != null) {
//            	result.addSolverProperty("op", opcode);
//            	result.addSolverProperty("input", input);
            	result.setResult(rmi.getBoolean() ?
            			ExampleTracerTracer.CORRECT_ACTION : ExampleTracerTracer.INCORRECT_ACTION);
				result.makeTutorSAI(null, getOpcode(rmi), rmi.getInput());
             }
        }
        /** spk 6/24/05 -- if the SolverGoal == mixed/improper then we want to prevent
         * another step from being created unless an appropriate rule was found... */
        if (rmi==null && 
                (getGoal().equals(SolverGoal.SIMPLIFY_ADDSUB_MIXED_EXPRESSION) 
                        ||getGoal().equals(SolverGoal.SIMPLIFY_MULTDIV_MIXED_EXPRESSION))) {
            if (Logger.isLoggingOn()) {
                Logger.log("solverdebug", "SolverTutor.checkStudentStandardAction ::  RMI returned [NULL], preventing next step creation...");
            }
            if (result != null)  // repeat the current step
            	result.addIaMessage("nextExpression", currentProblem.getStringValue(), "");
            return;
        }
        
        /** 2005/08/18 spk: The B2A typein is similar in process, but not the logic.  Intertwining the 
         * two processes would be an absolute mess.  So instead of intermingling, the process will fork
         * here and and any common parts to the two typein process will be reused... */
        if (rmi!=null && useTypein 
        		&& (	getGoal().equals(SolverGoal.SIMPLIFY_ADDSUB_MIXED_EXPRESSION) 
        			||  getGoal().equals(SolverGoal.SIMPLIFY_MULTDIV_MIXED_EXPRESSION)
        			)) {
//TODO spk
			/*helper method actually calculates the resulting step(s)*/
			SolverTutorProblem[] newProblem = currentProblem.calculateNextSteps(op, input, true, false, result);
//			TODO spk
			/*send create msg to interface for new step*/
			if(newProblem != null){
				updateForNewSteps(newProblem, op, input, true, false, result);
			}
//			if (result != null)
//				result.setResult(ExampleTracerTracer.CORRECT_ACTION);  // made some progress?
            return;
        }

		/*calculate new step (unless tutor doesn't approve and operation requires tutor approval)*/
		if((rmi != null && rmi.getBoolean()) || !op.getRequireRule()){
		    /** adding a check to make sure we want another step displayed.
		     * If the problem is an expression and the op is a Transformation, then 
		     * we have a choice of duplicating the step or preventing a new step from being 
		     * generated.  Either case, a JIT message is displayed to let the user 
		     * know the step was invalid.
		     */
		    if (op.getActionType().equals(SolverActionType.AT_TRNS_EXPR) && currentProblem.isSimpExpression()) {
		        // lethal combination...as of 5/9/2005 do NOT create another step
				if (Logger.LoggingOn) {
					Logger.log("solverdebug", "SolverTutor.checkStudentStandardAction :: op [" + op + "] is invalid on an Expression; returning without creating a new step...");
				}
				if (result != null)
					result.setIncorrectMsg("Transformation actions are not applicable when no equation is present.");
		        return;
		    }
/*!!*/			boolean typeinSidesForNewStep = useTypein && (!op.getActionType().getIsScratchpadAction() || result != null);
			boolean typeinEquationForNewStep = false;
			if(op.getActionType() == SolverActionType.AT_SPLIT_IMPL){
				if(op == SolverOperation.SPLIT){
					/*always use typein for set factors to 0*/ 
					typeinEquationForNewStep = true;
				}
				/*never use typein for separate roots*/
			}
			else if(op.getActionType() == SolverActionType.AT_SIMP_IMPL){
				typeinSidesForNewStep = false;
			}
			if (trace.getDebugCode("solverdebug")) trace.out("solverdebug", "cSSA() "+
					(result == null ? "null" : result.getResult())+", op "+op+
					", typeinSides "+typeinSidesForNewStep+", rmi "+rmi+", bool "+
					(rmi == null ? "null" : Boolean.toString(rmi.getBoolean())));

			/*helper method actually calculates the resulting step(s)*/
			SolverTutorProblem[] newProblem = currentProblem.calculateNextSteps(op,input,
					typeinSidesForNewStep,typeinEquationForNewStep, result);

			/*send create msg to interface for new step*/
			if(newProblem != null){
				/*!! if new step same as old, duplicate next step as below */ 
				if (result != null && useTypein && newProblem[0].getTypeinCompleted())
					updateForNewSteps(new SolverTutorProblem[] {currentProblem.makeIdenticalNextStep()},
							op,input,false,false,result);
				else
					updateForNewSteps(newProblem,op,input,typeinSidesForNewStep,typeinEquationForNewStep,result);
			}
		}
		else if(op.getActionType() == SolverActionType.AT_SPLIT_IMPL){
			/*can't really "fake" a split step, so don't even create a new step*/
			if (Logger.LoggingOn) {
				Logger.log("solverdebug", "SolverTutor.checkStudentAction: split action not accepted, so not creating new step");
			}
		}
		else{
			/*otherwise, just duplicate the previous step*/
			if (Logger.LoggingOn) {
				Logger.log("solverdebug", "SolverTutor.checkStudentAction: op requiring rule not accepted, so duplicating previous step");
			}
			updateForNewSteps(new SolverTutorProblem[] {currentProblem.makeIdenticalNextStep()},
					op,input,false,false,result);
		}
	}

	/**
	 * Cycles the tutor on a typein action for an expression or side of an
	 * equation. If the action is correct and represents a completed typein
	 * step, then the interface is updated to reflect the end of the typein
	 * step.
	 */
	private void checkStudentTypeinAction(SolverOperation op,String input,
			ExampleTracerEvent result){
		/*cycle the tutor on the typein input*/
		RuleMatchInfo rmi = cycleTutorTypein(op, input, result);

		/*if tutor accepted the input ...*/
		if(rmi != null && rmi.getBoolean()){
			if (result != null) {
				result.setResult(ExampleTracerTracer.CORRECT_ACTION);
				result.makeTutorSAI(null, getOpcode(rmi), rmi.getInput());
			}

			/*... update typein state of step*/
			//mmmBUG TODO switching sides will probably break here
			if(op == SolverOperation.LEFT){
				currentProblem.setLeftFinished(rmi.getInput());
			}
			else if(op == SolverOperation.EXPR){
				currentProblem.setExprFinished(rmi.getInput());
			}
			else if(op == SolverOperation.RIGHT){
				currentProblem.setRightFinished(rmi.getInput());
			}

			/*mmmBUG TODO auto-correct user input if necessary*/
			
			if (Logger.isLoggingOn())
				Logger.log("solverdebug", "ST.cSTA("+op+", "+input+") -> rmi "+rmi
						+", typeinCompleted "+currentProblem.getTypeinCompleted());

			/*if this input completed the typein portion of the step ...*/
			if(currentProblem.getTypeinCompleted()){
				/*... delete the typein steps and create a finished step*/
				String probStr;
				if(currentProblem.isSolveEquation()){
					probStr = currentProblem.getEquation().toString();
					if (result == null) {
						currentProblem.deleteChildByName(LEFT_STR);
						currentProblem.deleteChildByName(RIGHT_STR);
					} else
						result.addIaMessage("nextEquation", probStr, null, false);
				}
				else{
					probStr = currentProblem.getExpression().toString();
					if (result == null)
						currentProblem.deleteChildByName(EXPRESSION_PROP);
					else
						result.addIaMessage("nextExpression", probStr, null);
				}

				if (result == null) {
					MessagingAddress probAddr = currentProblem.getMessagingAddress();
					SendMessage.sendSetValue(probAddr,probStr);
				}
			}
		}
	}

	/**
	 * Cycles the tutor on a typein action for an entire equation. If the action
	 * is correct, then the interface is updated to reflect the end of the
	 * typein step. The equation input is verified against the current problem
	 * and any unfinished siblings.
	 */
	private void checkStudentTypeinEquation(SolverOperation op,String input,
			ExampleTracerEvent result){
		/*find all uncompleted siblings of current problem*/
		SolverTutorProblem[] incompleteSiblings = currentProblem.getTypeinIncompleteSiblings();

		/*loop over uncompleted siblings checking for a match*/
		SolverTutorProblem matchingSibling = null;
		for(int i=0;matchingSibling == null && i<incompleteSiblings.length;i++){
			/*check entered equation against current problem's equation*/
			if (Logger.LoggingOn) {
				Logger.log("solverdebug", "SolverTutor.checkStudentTypeinEquation: checking sibling " + i + ": "
					+ incompleteSiblings[i]);
			}
			if(equationMatchesTypein(incompleteSiblings[i].getEquation(),input)){
				if (Logger.LoggingOn) {
					Logger.log("solverdebug", "SolverTutor.checkStudentTypeinEquation: sibling " + i + " matched");
				}
				matchingSibling = incompleteSiblings[i];
			}
		}

		if(matchingSibling != null){
			if (Logger.LoggingOn) {
				Logger.log("solverdebug", "SolverTutor.checkStudentTypeinEquation: matching sibling: "
					+ matchingSibling);
			}
			/*we found a matching problem, so update its state*/
			matchingSibling.setLeftFinished(matchingSibling.getLeftOrExpression());
			matchingSibling.setRightFinished(matchingSibling.getEquation().getRight().toString());

			/*swap messaging addrs if necessary*/
			if(matchingSibling != currentProblem){
				if (Logger.LoggingOn) {
					Logger.log("solverdebug", "SolverTutor.checkStudentTypeinEquation: matched different sibling; swapping messaging addresses");
				}
				MessagingAddress.swapMessagingAddresses(currentProblem,matchingSibling);
			}

			/*delete equation child from matching sibling (tutor and interface)*/
			matchingSibling.deleteChildByName(SolverOperation.EQN.getOpcode());

			/*send messages to interface to update its state*/
			String probStr = matchingSibling.getEquation().toString();
			MessagingAddress probAddr = matchingSibling.getMessagingAddress();
			if (result == null)
				SendMessage.sendSetValue(probAddr,probStr);
			else {
				result.setResult(ExampleTracerTracer.CORRECT_ACTION);
				result.makeTutorSAI(null, SolverOperation.EQN.getOpcode(), probStr);
			}
		}
		else{
			if (Logger.LoggingOn) {
				Logger.log("solverdebug", "SolverTutor.checkStudentTypeinEquation: no matching siblings");
			}
			/*otherwise, flag interface*/
			MessagingAddress eqnAddr = currentProblem.getMessagingAddress();
			eqnAddr.addAddressLevel(SolverOperation.EQN.getOpcode());
			if (result == null)
				SendMessage.sendFlag(eqnAddr);
			else {
				result.setResult(ExampleTracerTracer.NULL_MODEL);
				result.makeTutorSAI(null, SolverOperation.EQN.getOpcode(), null);
			}
		}
	}

	private boolean equationMatchesTypein(Equation expected,String input){
		if (Logger.LoggingOn) {
			Logger.log("solverdebug", "SolverTutor.equationMatchesTypein: expected: " + expected);
			Logger.log("solverdebug", "SolverTutor.equationMatchesTypein: input: " + input);
		}

		try{
			Relation expectedRel = new Relation(Relation.EQ,expected.getLeft(),expected.getRight());
			Relation inputRel = new Relation(input);
			return expectedRel.matches(inputRel);
		}
		catch(BadExpressionError bee){
			Logger.log(bee);
			return false;
		}
	}

	/**
	 * Notifies the interface (via CREATE messages) that one or more new steps
	 * have been created, usually as the result of an operation by the user
	 */
	private void updateForNewSteps(SolverTutorProblem[] newProblems,SolverOperation op,String input,
			boolean createTypeinSideSteps,boolean createTypeinEquationStep,
			ExampleTracerEvent result){

		if (Logger.isLoggingOn())
			Logger.log("solverdebug", "***SolverTutor.updateForNewSteps("+Arrays.asList(newProblems)+
					", "+op+", "+input+", "+createTypeinSideSteps+", "+createTypeinEquationStep+", "+result+")");
		
		/*create the new step(s) under the current step, so get its address*/
		MessagingAddress oldProbAddr = (result == null ? currentProblem.getMessagingAddress() : null);

		currentProblem = newProblems[0];

		/** spk 6/24/05 -- if the SolverGoal is mixed/improper, then no 
		 * classification menu exists...set the problem (by default) to be 
		 * 'Classified' */
//		if (getGoal().equals(SolverGoal.SIMPLIFY_ADDSUB_MIXED_EXPRESSION)
//		        || getGoal().equals(SolverGoal.SIMPLIFY_MULTDIV_MIXED_EXPRESSION)) {
    	/** bug 11504, 11673: the classify/done menu is shown/supressed by a toolspec property */
		if (showClassificationMenu) {
		    currentProblem.setClassified(false);
		}
		/*set basic properties for the new step*/
		StringMap props = new StringMap(PropertyConstants.OPERATION,op.getOpcode());
		if(input != null){
			props.setProperty(PropertyConstants.INPUT,input);
		}
		if (result != null)
			result.makeTutorSAI(null, op.getOpcode(), input);

		props.setProperty(STEP_COUNT,new Integer(newProblems.length));

		/*create entry boxes or the result itself depending on typein settings*/
		if(createTypeinEquationStep){
			/*type in the whole equation*/
			props.setProperty(TYPEIN_PROP,EQUATION_PROP);
		}
		/** B2A Typein adds a new property for the tool: the number
		 * of terms for which we're setting up typein fields */
		else if(createTypeinSideSteps 
				&& (getGoal().equals(SolverGoal.SIMPLIFY_ADDSUB_MIXED_EXPRESSION) 
					||getGoal().equals(SolverGoal.SIMPLIFY_MULTDIV_MIXED_EXPRESSION))) {

			props.setProperty(TYPEIN_PROP, EXPRESSION_PROP);
			/** bug 11509: if we have an expression type-in that doesn't use the 
			 * inline scratchpads, we need to provide an expression string so the 
			 * 'new' step can create without a NullPointerException. */
			props.setProperty(EXPRESSION_PROP,currentProblem.getExpression().toString());
		
		}
		else if(createTypeinSideSteps){
			props.setProperty(TYPEIN_PROP,EXPRESSION_PROP);

			/*set the initial left- or right-hand sides, if the operation was
			  only being performed on one side of the equation*/
			if(currentProblem.isSolveEquation()){
	    		List<String> promptInput = new ArrayList<String>();
				Equation prevEq = currentProblem.getPreviousProblem().getEquation();
				if (Logger.isLoggingOn())
					Logger.log("solverdebug", prevEq == null ? "previousProblem.getEquation() null!"
							: "***eq.getLeft() "+prevEq.getLeft()+"; eq.getRight() "+prevEq.getRight());
				/*for typein for sides of the equation, set left or right properties
				  if we're only doing typein on one side or the other*/
				if(input != null && input.equalsIgnoreCase(RIGHT_STR)) {
					List<String> newLefts = new ArrayList(newProblems.length);
					for(int i=0;i<newProblems.length;i++){
						newLefts.add(newProblems[i].getLeftOrExpression());
					}
					props.setProperty(LEFT_STR,newLefts);
					if(result != null) {
						for(int i=0;i<newProblems.length;i++) {
							promptInput.add(newProblems[i].getLeftOrExpression());
							promptInput.add(SolverTutor.INPUT_BOX);
						}
						ExampleTracerEvent.InterfaceAction ia = result.addIaMessage("promptTypein",
								promptInput, "Enter right-hand side");
					}
				} else if(input != null && input.equalsIgnoreCase(LEFT_STR)){
					List<String> newRights = new ArrayList(newProblems.length);
					for(int i=0;i<newProblems.length;i++){
						newRights.add(newProblems[i].getEquation().getRight().toString());
					}
					props.setProperty(RIGHT_STR,newRights);
					if(result != null) {
						for(int i=0;i<newProblems.length;i++) {
							promptInput.add(SolverTutor.INPUT_BOX);
							promptInput.add(newProblems[i].getEquation().getRight().toString());
						}
						ExampleTracerEvent.InterfaceAction ia = result.addIaMessage("promptTypein",
								promptInput, "Enter left-hand side");
					}
				} else { // prompt for typein both sides
					if(result != null) {
						ExampleTracerEvent.InterfaceAction ia = result.addIaMessage("promptTypein",
								null, "Enter result ["+LEFT_STR+"] = ["+RIGHT_STR+"]");
						for(int i=0;i<newProblems.length;i++) {
//							ia.addIaOutput(newProblems[i].getLeftOrExpression());
							ia.addIaOutput(SolverTutor.INPUT_BOX);
//							ia.addIaOutput(newProblems[i].getEquation().getRight().toString());
							ia.addIaOutput(SolverTutor.INPUT_BOX);
						}
					}
				}
			}
			else {
				/** bug 11509: if we have an expression type-in that doesn't use the 
				 * inline scratchpads, we need to provide an expression string so the 
				 * 'new' step can create without a NullPointerException. */
				props.setProperty(EXPRESSION_PROP,currentProblem.getExpression().toString());
				if(result != null) {
		    		List<String> promptInput = new ArrayList<String>();
		    		promptInput.add(SolverTutor.INPUT_BOX);
					ExampleTracerEvent.InterfaceAction ia = result.addIaMessage("promptExpression",
							promptInput, "Enter simplified expression");
				}
			}
		}
		else{
			/*non-typein mode*/
			props.setProperty(TYPEIN_PROP,Boolean.FALSE);
			if(currentProblem.isSolveEquation()){
				/*create resulting equation(s)*/
				props.setProperty(EQUATION_PROP,Arrays.asList(newProblems));
			}
			else{
				/*create resulting expression*/
				props.setProperty(EXPRESSION_PROP,currentProblem.getExpression().toString());
			}
		}
		
		
		/*actually send the create message, with the appropriate type*/
		if(currentProblem.isSolveEquation()){
			if (result == null)
				SendMessage.sendCreate(oldProbAddr,EQUATION_PROP,props);
			else {
				if (!createTypeinSideSteps && !createTypeinEquationStep) {
					ExampleTracerEvent.InterfaceAction ia = result.addIaMessage("nextEquation",
							currentProblem.getEquation().toString(), "", false);
					ia.addSolverProperties(stringMap2Map(props));
				}
			}
		}
		else{	// TODO spk make sure this works for B2A as well
			if (result == null)
				SendMessage.sendCreate(oldProbAddr,EXPRESSION_PROP,props);
			else {
				if (!createTypeinSideSteps && !createTypeinEquationStep) {
					ExampleTracerEvent.InterfaceAction ia = result.addIaMessage("nextExpression",
							currentProblem.getExpression().toString(), "");
					ia.addSolverProperties(stringMap2Map(props));
				}					
			}
		}

		/*update done menu for simult eqns if needed*/
		if(newProblems.length > 1 && getTREParent() != null ){
			getTREParent().setDoneMenuMode(TRESolverTutor.DONE_SIMULT_EQ);
		}
	}

	/**
	 * Convert a {@link StringMap} to a {@link Map} for use outside this package.
	 * @param sm StringMap to convert
	 * @return
	 */
	public static Map<String, Object> stringMap2Map(StringMap sm) {
		Map<String, Object> m = new HashMap<String, Object>();
		Set keys = sm.getKeys();
		for (Iterator it = keys.iterator(); it.hasNext();) {
			String key = (String) it.next(); 
			m.put(key, sm.getProperty(key));
		}
		return m;
	}

	/**
	 * Callback after a scratchpad has been completed. Creates a new step
	 * representing the result of scratchpad operation.
	 */
	public void finishScratchpad(SolverOperation op,String input,String newExpr,String newExprUnsimp,
			ExampleTracerEvent result){
		updateForNewSteps(new SolverTutorProblem[] {currentProblem.finishScratchpad(op,newExpr,newExprUnsimp)},
				op,input,false,false,result);
	}

	/**Processes a deletion request for the given step*/
	protected void requestDeleteStep(SolverTutorProblem step){
		requestDeleteStep(step, null);
	}

	/**Processes a deletion request for the given step*/
	protected void requestDeleteStep(SolverTutorProblem step, ExampleTracerEvent result){
		currentProblem = step;

		/*check that we're not trying to delete the "root" problem*/
		if(originalProblem != currentProblem){
		    /** based upon feedback from Matt and SteveR, if the user 
		     * deletes/undoes the unnec-elems step, then the step that 
		     * invoked the unnec-elems operation should also be deleted/undone */
		    boolean doubleDelete = false;
		    /** found a nice little bug while testing bug 8934....the operation could be 'null' for the step we're deleting */
		    if (currentProblem.getOperation()!=null && currentProblem.getOperation().equals(SolverOperation.UNNEC_ELEMS)) {
		        doubleDelete = true;
		    }
			MessagingAddress addrToDelete = currentProblem.getMessagingAddress();
			SolverTutorProblem newCurrentProblem = (SolverTutorProblem)currentProblem.getMessagingParent();
			newCurrentProblem.removeChild(currentProblem);
			currentProblem = newCurrentProblem;
			if (result == null)
				SendMessage.sendDelete(addrToDelete);
			else {
				result.setResult(ExampleTracerTracer.CORRECT_ACTION);
				result.makeTutorSAI(null,step.getOperation().getOpcode(), 
						step.isSolveEquation() ? step.getEquation().getStringValue() : step.getExpression().getStringValue());
			}

			/*check for siblings to delete*/
			Set siblingNames = currentProblem.getChildNames();
			if(siblingNames.size() > 0 && getTREParent() != null ){
				/*change done mode*/
				getTREParent().setDoneMenuMode(TRESolverTutor.DONE_SINGLE_EQ);
			}

			for (Iterator siblingIt = siblingNames.iterator(); siblingIt.hasNext();) {
				String siblingName = (String) siblingIt.next();
				AddressableNode sibling = currentProblem.getChildByName(siblingName);
				addrToDelete = sibling.getMessagingAddress();
				currentProblem.removeChild(sibling);
				if (result == null)
					SendMessage.sendDelete(addrToDelete);
			}
			/** test if our new current problem was an auto-step (ex. SolverOp.UNNEC-ELEMS) */
			if (doubleDelete) {	// we want to delete this one as well....
			    requestDeleteStep(newCurrentProblem, result);
			}
		}
		else{
			//send JIT msg
			String noMoreToUndo = "There are no more steps to undo.";
			if (result == null) {
				SendMessage.sendShowJITMessage(currentProblem.getMessagingAddress(),
					//mmmBUG TODO i18n
					new CL_TutorMessage[] {new CL_TutorMessage(noMoreToUndo)},0);
			} else
				result.setTutorAdvice(noMoreToUndo);
		}
	}

	public void updateSkill(String skillname,String gradient){
		if(!ruleUpdates.contains(skillname)){
			Tutor.updateSkillometer(skillname,gradient);
			ruleUpdates.add(skillname);
		}
		/*else{
		  Logger.log("solverdebug","ST.uS: skill " + skillname + " has already been updated");
		  }*/
	}

	/**
	 * updateSkillsForRule finds all the skills that correspond to the given
	 * rule and updates them
	 */
	public void updateSkillsForRule(Rule modelRule,boolean up,ExampleTracerEvent result) {
		/*need to do this every time in case typein rules need
          currentRules to be set correctly*/
		
		currentRulesPassed = up;
		if (modelRule!=null) {
		    currentRules = skillRules().findAllRulesToFire(currentProblem,modelRule.getName());
			if(Logger.LoggingOn){
				Logger.log("solverdebug","ST.uSFR: found "+(currentRules == null ? 0 : currentRules.length)+
						" skills for rule: " + modelRule);
			}
		} else { 
		    currentRules = null;
		}

		String directionCode;
		if (currentRules != null) {
			if(Logger.LoggingOn){
				Logger.log("solverdebug","ST.uSFR: found "+currentRules.length+" skills for rule: " + modelRule);
			}
			for (int i=0;i<currentRules.length;++i) {
				SkillRule foundSkill = currentRules[i];
				if (foundSkill.isTraced()) {
					if (Logger.LoggingOn) {
						Logger.log("solvertrace","***Found Skill rule "+foundSkill.getName()+" subskill: "+foundSkill.getSubskillName());
					}
				}
				//for some reason, we pass a code of 1 for up and 0 for down
				if (up)
					directionCode = "1";
				else
					directionCode = "0";
				if (result == null)
					updateSkill(foundSkill.getSubskillName(),directionCode);
				else
					addSkillName(result, foundSkill.getSubskillName());
			}
		}
		else if(Logger.LoggingOn){
			Logger.log("solverdebug","ST.uSFR: warning: no skills found for rule: " + modelRule);
		}
	}

	/**
	 * Convert a SolverTutor subskill name to CTAT's "name category" format and
	 * add it to the skills passed back in the result object. 
	 * @param result
	 * @param subskillName
	 */
	private void addSkillName(ExampleTracerEvent result, String subskillName) {
		if (trace.getDebugCode("solverdebug"))
			trace.outNT("solverdebug", "SolverTutor.addSkillName("+subskillName+")");
		if (result == null)
			return;
		if (subskillName == null || subskillName.length() < 1)
			return;
		String newName = subskillName.replace(' ', '_');  // spaces not allowed in skill names
		result.addSkillName(newName+' '+SOLVER_SKILL_CATEGORY);
	}

	/** For {@link #checkTutorDone()} to see whether a hint message contains "click the done button." */
	private static Pattern ClickTheDoneButton =
		Pattern.compile("[Cc]lick\\s+the\\s+(<[^>]*>)?[Dd]one(<[^>]*>)?\\s+[Bb]utton");

	/** For {@link #checkTutorDone()} to see whether a hint message contains "you have solved the equation." */
	private static Pattern YouHaveSolved =
		Pattern.compile("[Yy]ou have (solved|simplified) the (equation|expression)");
	
	/**
	 * Check whether the problem is done.
	 * Like {@link #cycleTutorDone(SolverOperation, String, ExampleTracerEvent, boolean)},
	 * but without side effects.
	 * @return true if {@link #cycleTutorDone(SolverOperation, String, ExampleTracerEvent, boolean)}
	 *         would set {@link #testableTutorDone} true
	 */
	private boolean checkTutorDone() {
		int currentState=currentProblem.getState();
		if (currentState != STEPCOMPLETED) {
			if (Logger.LoggingOn) {
				Logger.log("solverdebug","checkTutorDone() currentState "+currentState+" != STEPCOMPLETED ("+
						STEPCOMPLETED+")");
			}
			return false;
		}
		/*not in the middle of a typein step, so query the strategic rule set*/
		Rule helpRule = strategicRules().findRuleForHelp(currentProblem,getValidActions());
		String[] messages = (helpRule == null
				? new String[] {CommonKeys.getString(CommonKeys.DONE_MESSAGE)}
				: helpRule.getMessages(currentProblem));
		if (Logger.LoggingOn) {
			Logger.log("solverdebug","checkTutorDone() "+(helpRule == null ? "No" : "Found")+" Help rule "+
					(helpRule == null ? "" : helpRule.getName()+", messages "+Arrays.asList(messages)));
		}
		if (messages != null) {
			for (String msg : messages) {
				Matcher mDone = ClickTheDoneButton.matcher(msg);
				if (mDone.find())
					return true;
				Matcher mSolved = YouHaveSolved.matcher(msg);
				if (mSolved.find())
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks that a student's done action is correct, and updates the interface
	 * appropriately. If not, searches for an appropriate bug message.
	 * @param action should be one of {@link SolverOperation#DONE}, etc. 
	 * @param input current problem state
	 * @param result for CTAT tracing
	 * @return true if done
	 */
	private RuleMatchInfo cycleTutorDone(SolverOperation action, String input,
			ExampleTracerEvent result) {
		
		if (result != null) {
			result.setResult(ExampleTracerTracer.NULL_MODEL);  // init'ze for early returns
			result.makeTutorSAI(null, action.getOpcode(), input);
		}
		
		/** bug 11217: we've been so focused on BtA that a nasty NPE has crept into the 
		 * optional-solver tools.  Now that TRESolverTutor.fireDoneRules() exists
		 * this method gets called even for "TOOL" sub-type Solvers.
		 * Adding this check to prevent the NPE. */
		if (currentProblem==null) {
			if (Logger.isLoggingOn()) {
				Logger.log("solverdebug", "SolverTutor.cycleTutorDone.action, input :: ["+ action + "]["+ input + "] while currentProblem==NULL. Returning a null RuleMatchIfo object to caller.");
			}
			return null;
		}
		boolean isDone = false;
		RuleMatchInfo foundRule = strategicRules().findRuleToFire(currentProblem,
                                                                  action,
                                                                  input,
                                                                  getValidActions());
		
		if (foundRule != null && foundRule.getBoolean() == true) { //found rule, so can find skill to increment
			if (foundRule.getRule().isTraced()) {
				if (Logger.LoggingOn) {
					Logger.log("solvertrace","***Found Done rule "+foundRule.getRule().getName());
				}
			}
			updateSkillsForRule(foundRule.getRule(),true,result);
			
			isDone = true;
			testableTutorDone = true;
			if (result != null) {
				result.setSolverDone(true);
				result.setResult(ExampleTracerTracer.CORRECT_ACTION);
				result.makeTutorSAI(null, getOpcode(foundRule), foundRule.getInput());
			}

			/** if we're aren't using the Classification menu items(s), then we don't want
			 * to process these messages.
			 * TODO spk: use the unit property instead of BtA Goals */
			if (getGoal().equals(SolverGoal.SIMPLIFY_ADDSUB_MIXED_EXPRESSION) || getGoal().equals(SolverGoal.SIMPLIFY_MULTDIV_MIXED_EXPRESSION)) {
				if (Logger.isLoggingOn()) {
					Logger.log("solverdebug", "SolverTutor.cycleTutorDone.action, input :: By-passing classification display...");
				}
			}
			else if(action == SolverOperation.DONE){
                if(currentProblem.isSolveEquation()){
                	displayCompletionMessage(STEP_COMPLETED_UNIQUE_SOLUTION, result);
                }
                else{
                    displayCompletionMessage(STEP_COMPLETED_SIMPLIFIED, result);
                }
            }
            else if(action == SolverOperation.DONENOSOLUTION){
                    displayCompletionMessage(STEP_COMPLETED_NO_SOLUTION, result);
            }
            else{
                    displayCompletionMessage(STEP_COMPLETED_INFINITE_SOLUTION, result);
            }
		}

        /* if the done rule doesn't match, then see whether there is a Help or Bug rule that matches.
           if so, hint/bug that.;
           else we shuld assume that they are really done, and that the'done' rules aren't recognizing it.
           see bugs 6711 and 6820 -- esp the latter, since we can't tell whether factor is a menu option
           or not, within the done rule.  so we'll tell if we don't get a hint.  */
		if(!isDone) {
			if (Logger.LoggingOn) {
				Logger.log("solverdebug","ST.cTD: No Action rule for 'done'");
			}

			Rule helpRule = strategicRules().findRuleForHelp(currentProblem,getValidActions());
			Rule bugRule = null;
			if(helpRule != null)
            {
				updateSkillsForRule(helpRule,false,result);
				/** bug 11217: the 'Done' action progresses through the LMSTutorManager to display a
				 * not-done message.  Generation of the message to the user happens within
				 * TRESolverTutor.getNotDoneJITMessage(), over-riding any message created here. 
				 * NOTE: This  change affects both BtA and non-BtA Solvers. */
//				bugRule = bugRules().findRuleToFire(currentProblem,
//													action,
//													input,
//													null,	// desired action
//													"");	// desired input
//				if(bugRule != null){
//					CL_TutorMessage[] tutMsgs = CL_TutorMessage.makeMessageArray(bugRule.getMessages(currentProblem));
//					SendMessage.sendShowJITMessage(currentProblem.getMessagingAddress(),tutMsgs,0);
//				}
//                else // help rule but no bug rule
//                {
//                    if(Logger.LoggingOn){
//                        Logger.log("solverdebug","ST.cTD: no bug rule for failed done action; using generic message.");
//                    }
//                    /*even if we can't find a bug rule, we want to
//                      give the student some feedback as to why nothing is
//                      happening when he selects done*/
//                    SendMessage.sendShowJITMessage(currentProblem.getMessagingAddress(),
//                    		//TODO i18n
//                    		new CL_TutorMessage[]{new CL_TutorMessage("You are not done.  Ask for a hint.")},0);
//                }
            }
			else
            {
                /*No hint rule.  There may be bug rules, but we don't
                  want to search for them (see bug 6862) here.*/
                // if no help and no bug and no done, treat as catchall for done ?
                if( Logger.LoggingOn )
                {
                    Logger.log("solverdebug", "===============  SolverTutor CATCHALL:  no done, help or bug rule matches for 'done' ===  treat as DONE : "+currentProblem);
                }
                foundRule = new RuleMatchInfo( true, null, action, input );
                isDone = true;
                testableTutorDone = true;  // sewall 2010-11-19: record done for other use
			}

            if(isDone){
                /*if we've fallen through to the catch-all case,
                  increment skills and un-flag*/
                if (result == null)
                	SendMessage.sendApprove(currentProblem.getMessagingAddress());
                else {
                	result.setSolverDone(true);
                	result.setResult(ExampleTracerTracer.CORRECT_ACTION);
    				result.makeTutorSAI(null,
    						(foundRule != null ? getOpcode(foundRule) : null),
    						(foundRule != null ? foundRule.getInput() : null));
                }
//                updateSkillsForRule(strategicRules().getRuleByName("doneleft"),false);  //handled by bug 11240 logic (below)
            }
            else{
                /*otherwise, we never found a done rule, so decrement
                  the skills and flag*/
                if (result == null)
                	SendMessage.sendFlag(currentProblem.getMessagingAddress());
                else {
                	result.setResult(ExampleTracerTracer.NULL_MODEL);
    				result.makeTutorSAI(null,
    						(foundRule != null ? getOpcode(foundRule) : null),
    						(foundRule != null ? foundRule.getInput() : null));
                }
//                updateSkillsForRule(strategicRules().getRuleByName("doneleft"),false);  //handled by bug 11240 logic (below)
            }
            /** bug 11240: find all done-action rules and valid skills.  
             * for each applicable skill, decrement the skills */
            Rule[] actionRules = strategicRules().findInvalidActionRules(currentProblem, getValidActions());
            if (actionRules!=null) {
            	// find the set that apply to the done action used
            	List doneActionList = new ArrayList();
            	for (int x=0; x<actionRules.length; x++) {
            		// NOTE: it's absolutely imperative that you check the action for null;  the CatchAll rule has a 'null' action
            		if (actionRules[x].getAction()!=null && actionRules[x].getAction().equals(action)) {
            			doneActionList.add(actionRules[x]);
            		}
            	}
            	// see if we have anything to process
            	if (doneActionList!=null && doneActionList.size()>0) {
                	Rule[] doneActionRules = new Rule[doneActionList.size()];
                	doneActionList.toArray(doneActionRules);
                	if (Logger.isLoggingOn()) {
						Logger.log("solverdebug", "SolverTutor.cycleTutorDone.action, input :: doneActionRules count ["+ doneActionRules.length + "]");
					}
                	
                	// for each of the rules, find the skills that apply
                	for (int y=0; y<doneActionRules.length; y++) {
                		if (Logger.isLoggingOn()) {
							Logger.log("solverdebug", "SolverTutor.cycleTutorDone.action, input :: Decreasing skills for Done-rule: ["+ doneActionRules[y] + "]");
						}
            			updateSkillsForRule(doneActionRules[y], false, result);
                	}
            	}
            	
            }
		}

		return foundRule;
	}

	/**
	 * Sends a message describing the completion of the current problem (aka the
	 * classification of the solution) to the interface.
	 */
	private void displayCompletionMessage(String msg, ExampleTracerEvent result){
		//TODO i18n msg
		if (result == null)
			SendMessage.sendSetProperty(currentProblem.getMessagingAddress(),
					new StringMap("CompletionMessage",msg));
		else
			result.setTutorAdvice("Completed: "+msg);
		/** if we have a completion message, then this leaf is done */
		currentProblem.setClassified(true);

	}

	/**
	 * Cycles the tutor on the given strategic operation/input pair. This is
	 * done by querying the strategic rule set to find a rule that fires, and
	 * also querying the strategic bug rule set for a bug rule that fires (if no
	 * strategic rule matches).
	 */
	private RuleMatchInfo cycleTutor(SolverOperation action, String input, ExampleTracerEvent result) {
		//check the strategic rules
		//Logger.log("solverdebug","about to cycle tutor on "+currentEquationInfo.toString()+" "+action+" "+input);
		//Logger.log("solverdebug","pattern is "+currentEquationInfo.getPattern());
		if (result != null) {
			result.setResult(ExampleTracerTracer.NULL_MODEL);
			result.makeTutorSAI(null, action.getOpcode(), input);
		}
		
		currentRules = null;
		currentRulesPassed = false;
		SolverOperation action_to_use = action;
		if( action == SolverOperation.FACTOR_IN_TESTMODE )
			action_to_use = SolverOperation.FACT;

		if (Logger.LoggingOn) {
			Logger.log("solvertrace", "SolverTutor.cycleTutor :: Using Goal [" + getGoal() + "]");
		}

		RuleMatchInfo foundRule = strategicRules().findRuleToFire(currentProblem,
                                                                  action_to_use,
                                                                  input,
                                                                  getValidActions());

		if (foundRule != null && foundRule.getBoolean() == true) { //found rule, so can find skill to increment
			if( action == SolverOperation.FACTOR_IN_TESTMODE )
				foundRule.setAction( action );
			
			if (foundRule.getRule().isTraced()) {
				if (Logger.LoggingOn) {
					Logger.log("solvertrace","***Found Strategic rule "+foundRule.getRule().getName());
				}
			}
			updateSkillsForRule(foundRule.getRule(),true, result);
			if (result == null)
				SendMessage.sendApprove(currentProblem.getMessagingAddress());
			else {
				result.setResult(ExampleTracerTracer.CORRECT_ACTION);
				result.makeTutorSAI(null, action.getOpcode(), input);
			}

            ResearchProtocol.recordCorrectTransaction(currentProblem,
                                                      action,
                                                      input,
                                                      foundRule.getRule(),
                                                      currentRules);
		}
		else { //no strategic rule found -- check for bugs and decrement skills
			if (result == null)
				SendMessage.sendFlag(currentProblem.getMessagingAddress());
			else {                                                 // until find a bugRule
				result.setResult(ExampleTracerTracer.NULL_MODEL);
				result.makeTutorSAI(null, action.getOpcode(), input);
			}				
			Rule helpRule = strategicRules().findRuleForHelp(currentProblem,getValidActions());
			if (helpRule != null) {
				if (helpRule.isTraced()) {
					if (Logger.LoggingOn) {
						Logger.log("solvertrace","***Found help rule to decrement skill: "+helpRule.getName());
					}
				}
				if (result != null)
					result.makeTutorSAI(null, getOpcode(helpRule), helpRule.getInput());
				updateSkillsForRule(helpRule,false,result); //decrement skills corresponding to help rule
				//check for a bug rule
				Rule bugRule = bugRules().findRuleToFire(currentProblem,
                                                         action,
                                                         input,
                                                         helpRule.getAction(),
                                                         helpRule.getInput(),
                                                         // extended signiture to include valid menu options for filtering...
                                                         getValidActions());
				if (bugRule != null) {
                    //Logger.log("solverdebug","Found ST bug rule "+bugRule.getName());
					CL_TutorMessage[] messages = CL_TutorMessage.makeMessageArray(bugRule.getMessages(currentProblem));
					if (result == null)
						SendMessage.sendShowJITMessage(currentProblem.getMessagingAddress(),messages,0);
					else {
						result.setResult(ExampleTracerTracer.INCORRECT_ACTION);
//						result.makeTutorSAI(null, helpRule.getAction()...);  see above
						result.setTutorAdvice(getMessageTexts(messages));
					}
                    ResearchProtocol.recordBuggyTransaction(currentProblem,
                                                            action,
                                                            input,
                                                            bugRule,
                                                            currentRules,
                                                            messages);
				}
                else{
                    ResearchProtocol.recordErrorTransaction(currentProblem,
                                                            action,
                                                            input,
                                                            currentRules);
                }
			}
			else{
                if(Logger.LoggingOn){
                    Logger.log("solverdebug","Can't find help rule");
                }
                ResearchProtocol.recordErrorTransaction(currentProblem,
                                                        action,
                                                        input,
                                                        null);
			}
		}

		return foundRule;
	}

	/**
	 * Retrieves a hint from the strategic rule set, or constructs a typein
	 * hint, as appropriate
	 */
	private void getTutorHint(ExampleTracerEvent result) {
		int currentState=currentProblem.getState();
		if (Logger.LoggingOn) {
			Logger.log("solverdebug","getTutorHint() currentState "+currentState);
		}
		if (result != null)
			result.setResult(ExampleTracerTracer.HINT);

		switch(currentState){
		case STEPCOMPLETED:
			/*not in the middle of a typein step, so query the strategic rule set*/
			Rule helpRule = strategicRules().findRuleForHelp(currentProblem,getValidActions());
			if (helpRule != null) {
				if (Logger.LoggingOn) {
					Logger.log("solverdebug","***Found Help rule "+helpRule.getName()+
							", isTraced "+helpRule.isTraced());
				}
				CL_TutorMessage[] messages = CL_TutorMessage.makeMessageArray(helpRule.getMessages(currentProblem));
				if (result == null)
					SendMessage.sendShowHintMessage(currentProblem.getMessagingAddress(),messages,0);
				else {
					result.makeTutorSAI(null, getOpcode(helpRule), helpRule.getInput());
					result.setTutorAdvice(getMessageTexts(messages));
				}
				updateSkillsForRule(helpRule,false,result); //decrement skills corresponding to this help
				ResearchProtocol.recordHintTransaction(currentProblem,
                                                       helpRule,
                                                       currentRules,
                                                       messages);
			}
			else {
				/*no help rule found!*/
				/** bug 1110: rolled the changes from bug 8703 into the current code.  
				 * This now gives the user a you-are-done messages instead of the no-further-assist message */
				/*bug 8703: in this case, if we don't have a "normal" hint message,
				  we will accept "done" anyway.  So we give a canned "done" hint
				  here instead of the no-help message.  (We have to keep it generic
				  though, because we don't know any details about the problem.)*/
//				CL_TutorMessage[] messages = new CL_TutorMessage[] {new CL_TutorMessage(Rule.noHelpMessage)};
				CL_TutorMessage[] messages = new CL_TutorMessage[] {new CL_TutorMessage(CommonKeys.getString(CommonKeys.DONE_MESSAGE))};
				if (Logger.LoggingOn) {
					Logger.log("solverdebug", "SolverTutor.getTutorHint STEPCOMPLETED: ERROR: no help rule found for: "+
							currentProblem+"; messages "+Arrays.asList(messages));
				}
				if (result == null)
					SendMessage.sendShowHintMessage(currentProblem.getMessagingAddress(),messages,0);
				else
					result.setTutorAdvice(getMessageTexts(messages));
				ResearchProtocol.recordHintTransaction(currentProblem,
                                                       null,
                                                       null,
                                                       messages);
			}
			break;
		case LEFTNOTSET:
		case RIGHTNOTSET:
			/*
			 * in the middle of a typein step, so use the SideRuleTemplate to
			 * construct a generic hint message
			 */
			SideRuleTemplate sideTemplate;
            if(currentProblem.isSolveEquation()){
                sideTemplate = new SideRuleTemplate(currentProblem,currentState,result);
            }
            else{
                sideTemplate = new SideRuleTemplate(currentProblem,result);
            }
			CL_TutorMessage[] messages1 = CL_TutorMessage.makeMessageArray(sideTemplate.getMessages());
			int skillSide = LEFTSIDE;
			if(currentState == RIGHTNOTSET)
				skillSide = RIGHTSIDE;
			if (Logger.LoggingOn) {
				Logger.log("solverdebug", "SolverTutor.getTutorHint "+(skillSide == RIGHTSIDE ? "RIGHT" : "LEFT")+
						"NOTSET: "+currentProblem+"; SideRuleTemplate messages "+Arrays.asList(messages1));
			}
			if (null == result)
				SendMessage.sendShowHintMessage(currentProblem.getMessagingAddress(),messages1,0);
			else
				result.setTutorAdvice(getMessageTexts(messages1));
			///mmmBUG will the value of currentRules be correct when this is called?
			SkillRule[] skillRules = updateSkillsTypeIn("2", skillSide);
            ResearchProtocol.recordHintTransaction(currentProblem,
                                                   null,
                                                   skillRules,
                                                   messages1);
			break;
		case TERMONENOTSET:
		case TERMTWONOTSET:
			/* we're in the middle of a typein step, so use the SideRuleTemplate to
			 * construct a generic hint message */
			SideRuleTemplate termSideTemplate;
			/** this is going to come back to haunt me....but (right now) all of these are expressions */
            termSideTemplate = new SideRuleTemplate(currentProblem, currentState, result);
            /** use the resulting messages as our hints */
			CL_TutorMessage[] termMessages = CL_TutorMessage.makeMessageArray(termSideTemplate.getMessages());
			int termSkillSide = TERMONE;
			if(currentState == TERMTWONOTSET) {
				termSkillSide = TERMTWO;
			}
			if (Logger.LoggingOn) {
				Logger.log("solverdebug", "SolverTutor.getTutorHint TERM"+(termSkillSide == TERMTWO ? "TWO" : "ONE")+
						"NOTSET: "+currentProblem+"; SideRuleTemplate messages "+Arrays.asList(termMessages));
			}
			if (result == null)
				SendMessage.sendShowHintMessage(currentProblem.getMessagingAddress(), termMessages, 0);
			else
				result.setTutorAdvice(getMessageTexts(termMessages));
			/** the first param is gradient: 1==increase, !1==decrease.
			 * the second param doesn't appear to be used at all... */
			SkillRule[] termSkillRules = updateSkillsTypeIn("2", termSkillSide);
            ResearchProtocol.recordHintTransaction(currentProblem,
                                                   null,
                                                   termSkillRules,
                                                   termMessages);
			break;
		}
	}

	/**
	 * Extract the {@link SolverOperation#getOpcode()} from a SolverOperation.
	 * @param op
	 * @return op.getOpcode() or null
	 */
    static String getOpcode(SolverOperation op) {
    	return op == null ? null : op.getOpcode(); // was getMenuLabel
	}

	/**
	 * Extract the {@link SolverOperation#getOpcode()} from a Rule.
	 * @param rule
	 * @return rule.getAction().getOpcode() or null
	 */
    static String getOpcode(Rule rule) {
    	return rule == null ? null : getOpcode(rule.getAction());
	}

	/**
	 * Extract the {@link SolverOperation#getOpcode()} from a RuleMatchInfo.
	 * @param rmi
	 * @return rule.getAction().getOpcode() or null
	 */
    static String getOpcode(RuleMatchInfo rmi) {
    	return rmi == null ? null : getOpcode(rmi.getAction());
	}

	/**Returns an array of skills that were updated, or
       <code>null</code> if none were changed.*/
	private SkillRule[] updateSkillsTypeIn(String skillGradient, int skillSide){
        SkillRule[] foundSkills = null;

		//Logger.log("solverdebug","ST updateSkillsTypeIn skillGradient = "+skillGradient);
		if (currentRules != null){
			boolean foundASkill = false;
			//Typein skills use PREVIOUS equation info (i.e. they refer to the equation before the strategic step was taken)
			if(Logger.LoggingOn){
				Logger.log("solverdebug","ST.uSTI: finding typein skills for " + currentRules.length + " skills");
			}
			for(int i=0;i<currentRules.length;i++){
				foundSkills = typeinSkillRules().findAllRulesToFire(currentProblem.getPreviousProblem(),currentRules[i].getSubskillName());
				if(foundSkills != null){
					foundASkill = true;
					if(Logger.LoggingOn){
						Logger.log("solverdebug","ST.uSTI: found " + foundSkills.length +
										   " typein skill(s) for skill rule " + currentRules[i].getSubskillName());
					}
					for(int j=0;j<foundSkills.length;j++){
						if (foundSkills[j].isTraced()) {
							if (Logger.LoggingOn) {
								Logger.log("solvertrace","***Found Skill rule "+foundSkills[j].getName()+
											   " subskill: "+foundSkills[j].getSubskillName());
							}
						}		
						updateSkill(foundSkills[j].getSubskillName(),skillGradient);
					}
				}
				else if(Logger.LoggingOn){
					Logger.log("solverdebug","ST.uSTI: no typein skills found for skill rule " + currentRules[i].getName() +
									   "(" + currentRules[i].getSubskillName() + ")");
				}
			}
			if(foundASkill){
				//setSkillInfo(true);
			}
			else if(Logger.LoggingOn){
				//this failure is more problematic
				Logger.log("solverdebug","ST.uSTI: warning: no typein skills found for skill rules {" + currentRules[0]);
				if(currentRules.length > 1){
					Logger.log("solverdebug",", ...}");
				}
				else{
					Logger.log("solverdebug","}");
				}
			}
		}
		else if(Logger.LoggingOn){
			Logger.log("solverdebug","ST.uSTI: no skill rules, so can't search for typein skill rules");
		}

        return foundSkills;
	}

	/**
	 * Verifies student input for typing in an expression, and updates the
	 * interface accordingly. This handles typing in the whole expression in
	 * expression-simplification problems, and typing in one side of an equation
	 * in equation-solving problems. If the student's input does not match, also
	 * checks for bug rules and sends the appropriate bug message to the
	 * interface.
	 */
	private RuleMatchInfo cycleTutorTypein(SolverOperation side, String input,
			ExampleTracerEvent result) {
		if(Logger.LoggingOn){
			Logger.log("solverdebug","ST.cTT("+side+","+input+")");
			Logger.log("solverdebug","ST.cTT: Current problem: " + currentProblem);
		}
		/*re-arrange the params so they match what they were back in
          the day, when this used to be called from the call to
          runTutor in TranslatorProxy.recordStep()*/
		String comparison = null;
		int skillSide;
        RuleMatchInfo ret = null;

        if (side == SolverOperation.LEFT){
            skillSide = LEFTSIDE;
        }
        else{
            skillSide = RIGHTSIDE;
        }

        if (result != null) {
        	result.setResult(ExampleTracerTracer.NULL_MODEL);
        	result.makeTutorSAI(null, side.getOpcode(), input);
        }
		try {
            ret = cycleTutorTypeinInternal(side.getOpcode(),input,true);
            
			if(ret.getBoolean()){
				//Logger.log("solverdebug","ST.cTT: input " + input + " matches " + comparison);
				if (result == null) {
					MessagingAddress sideAddr = currentProblem.getMessagingAddress();
					if(sideAddr == null){
						//happens normally w/ testable tutor
						if (Logger.LoggingOn) {
							Logger.log("SolverTutor.cycleTutorTypein: sideAddr is null!");
						}
					}
					else{
						sideAddr.addAddressLevel(side.getOpcode());
					}
					SendMessage.sendApprove(sideAddr);
				} else {
					result.setResult(ExampleTracerTracer.CORRECT_ACTION);
					result.makeTutorSAI(null, side.getOpcode(), input);
				}
				/*Since the typein skills are based on the strategic
                  rules describing the student's action, we can only
                  modify the typein skills if the strategic action
                  matched a rule.  Otherwise we have no way of
                  figuring out the typein skills that correspond to
                  the unmatched strategic action.*/
				if(currentRulesPassed){
					SkillRule[] skillRules = updateSkillsTypeIn("1", skillSide);
					if (result != null && skillRules != null) {
						for (SkillRule sr : skillRules)
							addSkillName(result, sr.getSubskillName());
					}
                    ResearchProtocol.recordCorrectTransaction(currentProblem,
                                                              side,
                                                              input,
                                                              null,
                                                              skillRules);
				}
				else{
					if (Logger.LoggingOn) {
						Logger.log("solverdebug","ST.cTT: previous action was not correct, so not incrementing typein skills");
					}
                    ResearchProtocol.recordCorrectTransaction(currentProblem,
                                                              side,
                                                              input,
                                                              null,
                                                              null);
				}
			}
			else{
				MessagingAddress sideAddr = null;
				if (result == null) {
					sideAddr = currentProblem.getMessagingAddress();
					if(sideAddr == null){
						//happens normally w/ testable tutor
						if (Logger.LoggingOn) {
							Logger.log("SolverTutor.cycleTutorTypein: sideAddr is null!");
						}
					}
					else{
						sideAddr.addAddressLevel(side.getOpcode());
					}
					SendMessage.sendFlag(sideAddr);
				}
				else {
					result.setResult(ExampleTracerTracer.INCORRECT_ACTION);
					result.makeTutorSAI(null, side.getOpcode(), input);
				}
                SkillRule[] skillRules = null;
				if(currentRulesPassed){
					skillRules = updateSkillsTypeIn("0", skillSide);
					if (result != null) {
						if (skillRules == null)
							if (Logger.LoggingOn) {
								Logger.log("solverdebug","ST.cTT: updateSkillsTypeIn(0) returns no skills");
							}
						else {
							for (SkillRule sr : skillRules)
								result.addSkillName(sr.getSubskillName());
						}
					}
				}
				else{
					if (Logger.LoggingOn) {
						Logger.log("solverdebug","ST.cTT: previous action was not correct, so not decrementing typein skills");
					}
				}

				//check for a bug rule
                Queryable expProb = currentProblem.getPreviousProblem().getExpressionProblem(skillSide);
                if(currentProblem.isSimpExpression()){
                    comparison = currentProblem.getStringValue();
                }
				else if(skillSide == LEFTSIDE){
                    comparison = currentProblem.getProperty("left").getStringValue();
				}
				else{
                    comparison = currentProblem.getProperty("right").getStringValue();
				}
				TypeinBugRule bugRule = (TypeinBugRule)typeinBugRules().findRuleToFire(expProb,
                                                                                       prevTIaction,
                                                                                       prevTIinput,
                                                                                       input,
                                                                                       comparison);
				if(bugRule == null && currentProblem.isSolveEquation()){
					//check for bug rule on other side
                    String otherSideName;
					if(skillSide == LEFTSIDE){
                        otherSideName = "right";
                        expProb = currentProblem.getPreviousProblem().getExpressionProblem(RIGHTSIDE);
					}
					else{
                        otherSideName = "left";
                        expProb = currentProblem.getPreviousProblem().getExpressionProblem(LEFTSIDE);
					}
                    int otherSideStat = currentProblem.getSideStatus(otherSideName);
                    boolean otherSideUnset = otherSideStat == TYPEIN_UNSET;

                    if(otherSideUnset){
                        comparison = currentProblem.getProperty(otherSideName).getStringValue();

                        bugRule = (TypeinBugRule)typeinBugRules().findRuleToFire(expProb,
                                                                                 prevTIaction,
                                                                                 prevTIinput,
                                                                                 input,
                                                                                 comparison);
                    }
                    else{
                		if (Logger.LoggingOn) {
                			Logger.log("solverdebug","SolverTutor.cycleTutorTypein: other side is already set, so not checking other side bug rules");
                		}
                    }
				}

				if(bugRule != null){
					CL_TutorMessage[] messages = CL_TutorMessage.makeMessageArray(bugRule.getMessages(expProb,
							prevTIinput,
							input,
							comparison));
					if (result == null)
						SendMessage.sendShowJITMessage(sideAddr,messages,0);
					else {
						result.setResult(ExampleTracerTracer.INCORRECT_ACTION);
						result.makeTutorSAI(null, getOpcode(bugRule), bugRule.getInput());
						result.setTutorAdvice(getMessageTexts(messages));
					}
                    ResearchProtocol.recordBuggyTransaction(currentProblem,
                                                            side,
                                                            input,
                                                            bugRule,
                                                            skillRules,
                                                            messages);
				}
                else{
                    ResearchProtocol.recordErrorTransaction(currentProblem,
                                                            side,
                                                            input,
                                                            skillRules);
                }
			}
		}
		catch (BadExpressionError err) {
			if (result == null) {
				MessagingAddress sideAddr = currentProblem.getMessagingAddress();
				sideAddr.addAddressLevel(side.getOpcode());
				SendMessage.sendFlag(sideAddr);
			}
			else
				result.setResult(ExampleTracerTracer.NULL_MODEL);

			if (Logger.LoggingOn) {
				Logger.log("Bad expression in cycleTutorTypein..."+err);
			}
			//Logger.log(err);
		}
		catch (NoSuchFieldException err) {
			Logger.log("No such field in cycleTutorTypein");
			Logger.log(err);
		}

		return ret;
	}

	/**
	 * Checks a student's typein answer against the expected input, allowing for
	 * side-switching and handling expressions as well as sides of equations
	 * 
	 * @param updateSides If <code>true</code> and the student's input is
	 *            correct but on the wrong side,
	 *            <code>reverseEquationSides</code> will be called to update
	 *            data members accordingly.
	 */
    private RuleMatchInfo cycleTutorTypeinInternal(String side,String input,boolean updateSides) throws NoSuchFieldException, BadExpressionError{
		RuleMatchInfo ret = new RuleMatchInfo(false,null,null,input);
        String comparison = null;
        int skillSide;

        int leftstat = currentProblem.getSideStatus(LEFT_STR);
        boolean leftUnset = leftstat == TYPEIN_UNSET;

        int rightstat = currentProblem.getSideStatus(RIGHT_STR);
        boolean rightUnset = rightstat == TYPEIN_UNSET;

        if (currentProblem.isSimpExpression()){
            comparison = currentProblem.getStringValue();
            String unsimpComparison = currentProblem.getProperty("unsimp").getStringValue();
            /*don't check the unsimplified equation if it hasn't
              changed from the previous step (this will be the case
              for 'simplification' ops like CLT, MT, etc.)*/
            boolean checkUnsimp = !sm.exactEqual(unsimpComparison,currentProblem.getPreviousProblem().getStringValue());
            skillSide = LEFTSIDE;
            ret = checkTypeinAnswer(side,input,comparison);
            if(checkUnsimp &&
               !ret.getBoolean() &&
               !comparison.equals(unsimpComparison)){
                /*if we don't match the expected equation, try with
                  the unsimplified version*/
                ret = checkTypeinAnswer(side,input,unsimpComparison);
            }
        }
        else if (side.equalsIgnoreCase("left")){
            comparison = currentProblem.getProperty("left").getStringValue();
            String unsimpComparison = currentProblem.evalQuery("left of unsimp").getStringValue();
            /*don't check the unsimplified equation if it hasn't
              changed from the previous step (this will be the case
              for 'simplification' ops like CLT, MT, etc.)*/
            boolean checkUnsimp = !sm.exactEqual(unsimpComparison,currentProblem.getPreviousProblem().getProperty("left").getStringValue());
            skillSide = LEFTSIDE;
            ret = checkTypeinAnswer(side,input,comparison);
            if(checkUnsimp &&
               !ret.getBoolean() &&
               !comparison.equals(unsimpComparison)){
                /*if we don't match the expected equation, try with
                  the unsimplified version*/
                ret = checkTypeinAnswer(side,input,unsimpComparison);
            }
            /*if we don't match the left side and we haven't entered
              anything on the right side, see if we're swapping
              sides*/
            if(!ret.getBoolean() && rightUnset){
                //Logger.log("solverdebug","ST.cTT: checking LHS input against swapped RHS");
                comparison = currentProblem.getProperty("right").getStringValue();
                unsimpComparison = currentProblem.evalQuery("right of unsimp").getStringValue();
                checkUnsimp = !sm.exactEqual(unsimpComparison,currentProblem.getPreviousProblem().getProperty("right").getStringValue());
                ret = checkTypeinAnswer("right",input,comparison);
                if(checkUnsimp &&
                   !ret.getBoolean() &&
                   !comparison.equals(unsimpComparison)){
                    ret = checkTypeinAnswer("right",input,unsimpComparison);
                }
                if(ret.getBoolean()){
            		if (Logger.LoggingOn) {
            			Logger.log("solverdebug","ST.cTT: LHS input matches RHS; swapping sides of current & previous equations");
            		}
                    /*reverse sides of current equation*/
                    if(updateSides){
                        reverseEquationSides();
                    }
                }
            }
        }
        else{
            comparison = currentProblem.getProperty("right").getStringValue();
            String unsimpComparison = currentProblem.evalQuery("right of unsimp").getStringValue();
            boolean checkUnsimp = !sm.exactEqual(unsimpComparison,currentProblem.getPreviousProblem().getProperty("right").getStringValue());
            skillSide = RIGHTSIDE;
            ret = checkTypeinAnswer(side,input,comparison);
            if(checkUnsimp &&
               !ret.getBoolean() &&
               !comparison.equals(unsimpComparison)){
                ret = checkTypeinAnswer(side,input,unsimpComparison);
            }
            /*if we don't match the right side and we haven't entered
              anything on the left side, see if we're swapping sides*/
            if(!ret.getBoolean() && leftUnset){
                //Logger.log("solverdebug","ST.cTT: checking RHS input against swapped LHS");
                comparison = currentProblem.getProperty("left").getStringValue();
                unsimpComparison = currentProblem.evalQuery("left of unsimp").getStringValue();
                checkUnsimp = !sm.exactEqual(unsimpComparison,currentProblem.getPreviousProblem().getProperty("left").getStringValue());
                ret = checkTypeinAnswer("left",input,comparison);
                if(checkUnsimp &&
                   !ret.getBoolean() &&
                   !comparison.equals(unsimpComparison)){
                    ret = checkTypeinAnswer("left",input,unsimpComparison);
                }
                if(ret.getBoolean()){
            		if (Logger.LoggingOn) {
            			Logger.log("solverdebug","ST.cTT: RHS input matches LHS; swapping sides of current & previous equations");
            		}
                    /*reverse sides of current equation*/
                    if(updateSides){
                        reverseEquationSides();
                    }
                }
            }
        }
        //Logger.log("solverdebug","ST.cTT: comparing: " + input + " =?= " + comparison);
        ret.setSide((skillSide == LEFTSIDE) ? "Left" : "Right");

        if(Logger.LoggingOn && !ret.getBoolean()){
            Logger.log("solverdebug","ST.cTT: input " + input + " does not match " + comparison);
        }

        return ret;
    }


    private void reverseEquationSides(){
        currentProblem.reverseEquationSides();
        //previousProblem.reverseEquationSides();
    }

	/**
	 * Checks a typein answer against the expected answer, correcting for
	 * round-off error. The RuleMatchInfo that is returned may have a different
	 * input specified. This will be the case when the student's input needs to
	 * be auto-corrected. The only case in which this currently happens is with
	 * decimals -- if the student's input is "correct" (i.e. matches to 2
	 * decimal places) but not "precise" (has missing or incorrect decimal
	 * digits past the first 2), we need to correct it so that it doesn't throw
	 * off the calculation in any subsequent steps.
	 */
	private RuleMatchInfo checkTypeinAnswer(String side,String input,String comparison) throws BadExpressionError{
		String newInput = input;
        if(sm.algebraicEqual(input,comparison)){
            newInput = sm.removeExtraParens(sm.fixNumbers(input,comparison));
        }
        else{
            /*try truncating the input to the displayed accuracy*/
            /*mmmBUG this probably won't work for typein on/autosimp
              on, but currently we never do that*/
            /*mmmBUG shouldn't assume that we're using default
              dec. places*/

            /*loop over, trying every permissible # of dec. places*/
            int oldDP = sm.getPrintDecimalPlaces();
            int minDP = sm.getCompareDecimalPlaces();
            //int minDP = NumberExpression.defaultMathMLDecimalPlaces;
            for(int dp=oldDP-1;dp >= minDP;dp--){
                /*Logger.log("solverdebug","ST.cTA: trying to match with " + dp +
                  " dec. places");*/
                sm.setPrintDecimalPlaces(dp);

                /*truncate the comparison to reflect what the student
                  sees in the display*/
                String newComparison = sm.noOp(comparison);

                /*reset the dec. places for the rest of these
                  comparisons*/
                sm.setPrintDecimalPlaces(oldDP);

        		if (Logger.LoggingOn) {
        			Logger.log("solverdebug","ST.cTA: comparing: " + input +
                           " =?= " + newComparison);
        		}
                if(sm.algebraicEqual(input,newComparison)){
                    newInput = sm.removeExtraParens(sm.fixNumbers(input,comparison));
                    /*technically, results of fixNumbers when its args
                      aren't algEqual are undefined.  We should be
                      okay in most cases, and this sanity check should
                      catch any problems.*/
                    if(sm.algebraicEqual(newInput,comparison)){
                        /*we've got a good replacement, so break out
                          of the for loop*/
                        break;
                    }
                    else{
                		if (Logger.LoggingOn) {
                			Logger.log("solverdebug","ST.cTA: WARNING: based on truncated comparison (" +
                                   newComparison + "), input (" + input + ") corrects to " +
                                   newInput + ", which is not equal to original comparison (" +
                                   comparison + "); so abandoning the correction");
                		}
                        newInput = input;
                    }
                }
            }
        }

		RuleMatchInfo ret = new RuleMatchInfo(typeinEqual(side,input,comparison),
											  null,null,input);

		if(ret.getBoolean()){
			/*matched, use fixed version of original input*/
			ret.setInput(newInput);
			//Logger.log("solverdebug","ST.cTA: fixed input: " + input + " --> " + newInput);
		}
		else{
			/*try to match against the fixed input*/
			ret = new RuleMatchInfo(typeinEqual(side,newInput,comparison),
									null,null,input);
			if(ret.getBoolean()){
				/*matched, use fixed version of original input*/
				ret.setInput(newInput);
				//Logger.log("solverdebug","ST.cTA: matched on fixed input: " + input + " --> " + newInput);
			}
			/*else{
			  Logger.log("solverdebug","ST.cTA: could not reconcile input: " + input + " --> " + newInput);
			  }*/
		}

		return ret;
	}

	/**Checks whether if input is an acceptable version of comparison.
	   This uses the prevTI* data, so the result is contextualized
	   based on the operation being performed.  On an action-specific
	   basis, we want to allow the student to make some implicit
	   simplifications, but not others.  See below for specifics.*/
	private boolean typeinEqual(String side,String input,String comparison) throws BadExpressionError{
		if(Logger.LoggingOn){
			Logger.log("solverdebug","ST.tE: checking user input " + input + " against " + comparison + " on " + side);
			Logger.log("solverdebug","ST.tE: context: " + prevTIaction + "; " + prevTIinput);
		}
		SymbolManipulator typeinSM = new SymbolManipulator(SMParserSettings.HS_DEFAULT);	// TODO *** use the same parser as the base-tutor
		typeinSM.setMaintainVarList(true);
		typeinSM.allowExtraParens = false;
		typeinSM.allowDoubleSigns = false;
		typeinSM.distributeDenominator = false;//mmmBUG TODO used to get this from the tool -- is hardcoding okay?
		boolean unchanged = false;

		/*we always allow the expected equation*/
		if(typeinSM.similar(typeinSM.distributeDenominator(typeinSM.removeExtraParens(input)),
					  typeinSM.distributeDenominator(typeinSM.removeExtraParens(comparison)))){
			//Logger.log("solverdebug","ST.tE: similar; true");
			return true;
		}

		String prevTIside = null;
        try{
            if(currentProblem.isSimpExpression()){
                prevTIside = currentProblem.getPreviousProblem().getStringValue();
            }
            else{
                prevTIside = currentProblem.getPreviousProblem().getProperty(side).getStringValue();
            }
        }
        catch(NoSuchFieldException nsfe){
            /*should never happen*/
            Logger.log(nsfe);
        }

		/*(in most cases) something must have changed ...*/
		if(typeinSM.exactEqual(prevTIside,input)){
			if (Logger.LoggingOn) {
				Logger.log("solverdebug","ST.tE: nothing was changed");
			}
			unchanged = true;
		}

		//mmmBUG why don't we just check algebraicEqual here?

		/*if autosimplify/autostandardize is on, we allow any
          intermediate equation, as well*/
		if(!unchanged &&
		   sm.autoSimplify &&
		   typeinSM.similar(typeinSM.simplify(input),comparison)){
			//Logger.log("solverdebug","ST.tE: not fully simplified: true");
			return true;
		}
		else if(!unchanged &&
				sm.autoStandardize &&
				typeinSM.similar(typeinSM.standardize(input),comparison)){
			//Logger.log("solverdebug","ST.tE: not fully standardized: true");
			return true;
		}

		/*when combining like terms, we allow "intermediate" versions.
          E.g., 3x+4x+11 and 7x+5+6 are legal answers for clt on
          3x+4x+5+6 (but 3x+4x+5+6 is not).*/
		if(prevTIaction == SolverOperation.CLT){
			/*if they just didn't combine everything that could be
              combined, that's fine.  removeExtraParens allows 2(3+4)
              to clt to 2(7) as well as 2*7*/
			if(typeinSM.similar(typeinSM.removeExtraParens(typeinSM.combineLikeTerms(input)),
						  comparison) ||
			   typeinSM.similar(typeinSM.removeExtraParens(typeinSM.combineLikeTerms(input)),
						  typeinSM.removeExtraParens(typeinSM.combineLikeTerms(comparison)))){
				return !unchanged;
			}
			else{
				return false;
			}
		}
		/*when distributing, we allow the student to perform none,
          some, or all of the resulting multiplications/divisions.
          However, we don't allow the terms resulting from those
          multiplications to be combined.  So, distributing on 3(4+5)
          can give:
		  3*4+3*5
		  12+3*5
		  12+15
		  but not 27*/
		else if(prevTIaction == SolverOperation.DISTRIBUTE){
			if(!typeinSM.exactEqual(comparison,prevTIside)){
				if(typeinSM.similar(typeinSM.reduceFractions(typeinSM.multiplyThrough(input)),
							  typeinSM.reduceFractions(typeinSM.multiplyThrough(comparison)))){
					return !unchanged;
				}
				else{
					return false;
				}
			}
			else{
				//Logger.log("solverdebug","ST.tE: dist: didn't do anything, so not allowing mt / rf");
				return false;
			}
		}
		else if(prevTIaction == SolverOperation.MT){
			/*if they just didn't combine everything that could be
              combined, that's fine*/
			if(typeinSM.similar(typeinSM.multiplyThrough(input),comparison)){
				return !unchanged;
			}
			else{
				return false;
			}
		}
		/*when adding/subtracting, the student can combine only terms
          involved in the operation.  For example, on 3x+4x+5=6,
          subtract 5, we accept 3x+4x=1 in addition to 3x+4x+5-5=6-5
          -- but we don't accept 7x=1, because the x-terms weren't
          involved in the subtraction*/
		else if(prevTIaction == SolverOperation.ADD ||
				prevTIaction == SolverOperation.SUBTRACT){
			return exprCLTequiv(input,comparison,prevTIinput);
		}
		/*when multiplying/dividing (including when
          cross-multiplying), the student can distribute, perform
          multiplication, or reduce fractions.  For example, on
          3x=6+9, divide by 3, we accept x=2+3 in addition to
          3x/3=6/3+9/3 and 3x/3=(6+9)/3 -- but we don't accept x=5*/
		else if(prevTIaction == SolverOperation.MULTIPLY ||
				prevTIaction == SolverOperation.DIVIDE ||
				prevTIaction == SolverOperation.CM){
			/*ugh ... sure wish I knew whether these simplification
              ops commute or not.  unfortunately they probably don't*/
			String modInput,modInput2,modComparison;
			typeinSM.autoDistribute = typeinSM.autoMultiplyThrough = typeinSM.autoReduceFractions = true;
			modComparison = typeinSM.noOp(comparison);
			typeinSM.autoDistribute = typeinSM.autoMultiplyThrough = typeinSM.autoReduceFractions = false;
			modInput = typeinSM.distribute(input);
			//Logger.log("solverdebug","ST.tE: checking mod input " + modInput + " against mod " + modComparison);
			if(typeinSM.similar(modInput,modComparison)){
				//Logger.log("solverdebug","ST.tE: similar; returning true");
				return true;
			}
			else{
				modInput2 = typeinSM.multiplyThrough(modInput);
				if(typeinSM.similar(modInput2,modComparison) ||
				   typeinSM.similar(typeinSM.reduceFractions(modInput2),modComparison)){
					return true;
				}
				else{
					modInput2 = typeinSM.reduceFractions(modInput2);
					if(typeinSM.similar(modInput2,modComparison) ||
					   typeinSM.similar(typeinSM.multiplyThrough(modInput2),modComparison)){
						return true;
					}
				}
			}

			modInput = typeinSM.multiplyThrough(input);
			if(typeinSM.similar(modInput,modComparison)){
				return true;
			}
			else{
				modInput2 = typeinSM.reduceFractions(modInput);
				if(typeinSM.similar(modInput2,modComparison) ||
				   typeinSM.similar(typeinSM.distribute(modInput2),modComparison)){
					return true;
				}
				else{
					modInput2 = typeinSM.distribute(modInput2);
					if(typeinSM.similar(modInput2,modComparison) ||
					   typeinSM.similar(typeinSM.reduceFractions(modInput2),modComparison)){
						return true;
					}
				}
			}

			modInput = typeinSM.reduceFractions(input);
			if(typeinSM.similar(modInput,modComparison)){
				return true;
			}
			else{
				modInput2 = typeinSM.distribute(modInput);
				if(typeinSM.similar(modInput2,modComparison) ||
				   typeinSM.similar(typeinSM.multiplyThrough(modInput2),modComparison)){
					return true;
				}
				else{
					modInput2 = typeinSM.multiplyThrough(modInput2);
					if(typeinSM.similar(modInput2,modComparison) ||
					   typeinSM.similar(typeinSM.distribute(modInput2),modComparison)){
						return true;
					}
				}
			}

			return false;
		}
        /*mmmBUG: In theory this is too permissive, but it's not
          likely that anyone will care.  What we're trying to handle
          here is e.g. x*log(10:10)=3.  We want students to be able to
          type in 'x' for the LHS, in addition to the 'x*1' that is
          computed when autosimplify is off.  It's less certain
          whether we want to allow '9' as the LHS for
          '3*log(10:1000)=x'.*/
		else if(prevTIaction == SolverOperation.EVAL){
            if(typeinSM.similar(typeinSM.reduceFractions(typeinSM.multiplyThrough(input)),
                          typeinSM.reduceFractions(typeinSM.multiplyThrough(comparison)))){
                return !unchanged;
            }
            else{
                return false;
            }
        }
        /*When applying the power rule for logarithms, the student can
          just eliminate resulting logarithms, if they're equal to 1.
          Thus, the student can go from log(10:10^x) to x.  But you
          can't go from log(10:1000^x) to 3x.*/
        else if(prevTIaction == SolverOperation.LOGPOW){
            /*check if the logarithm evaluates to 1*/
            if(typeinSM.algebraicEqual(comparison,
                                 typeinSM.applyIdentity(comparison,
                                                  "x*log(10:y)",
                                                  "x*1"))){
                /*check if the input matches the evaluated logarithm.*/
                typeinSM.autoMultiplyThrough = true;
                if(typeinSM.similar(input,
                              typeinSM.noOp(typeinSM.applyIdentity(comparison,
                                                       "x*log(10:y)",
                                                       "x*1")))){
                    return !unchanged;
                }
                else{
                    return false;
                }
            }
            else{
                return false;
            }
        }
        /*for function application, allow cancelling out inverses:
          e.g. arcsin(sin(x)) --> x is okay.  also allow implicit
          evaluation of the function: e.g. arcsin(1) --> 90 is okay.*/
        //mmmBUG should we allow this sort of thing for logs, too?
        else if(prevTIaction == SolverOperation.ARCSIN ||
                prevTIaction == SolverOperation.ARCCOS ||
                prevTIaction == SolverOperation.ARCTAN ||
                prevTIaction == SolverOperation.ARCCSC ||
                prevTIaction == SolverOperation.ARCSEC ||
                prevTIaction == SolverOperation.ARCCOT ||
                prevTIaction == SolverOperation.SIN ||
                prevTIaction == SolverOperation.COS ||
                prevTIaction == SolverOperation.TAN ||
                prevTIaction == SolverOperation.CSC ||
                prevTIaction == SolverOperation.SEC ||
                prevTIaction == SolverOperation.COT){
            /*evalFuncs covers both kinds of implicit evaluation*/
            if(typeinSM.similar(typeinSM.evalFuncs(input),
                          typeinSM.evalFuncs(comparison))){
                return !unchanged;
            }
            else{
                return false;
            }
        }
        else if(prevTIaction == SolverOperation.APFUN){
            try{
                String fun = typeinSM.runScript(new String[] {"inverse","function"},
                                          prevTIside);
                /*for generic function application, compare the
                  pre-existing function to the input*/
                if(fun.equalsIgnoreCase(prevTIinput) &&
                   typeinSM.similar(typeinSM.evalFuncs(input),
                              typeinSM.evalFuncs(comparison))){
                    return !unchanged;
                }
                else{
                    return false;
                }
            }
            catch(NoSuchFieldException nsfe){
                /*the thing we're applying the function to is not
                  itself a function, so there's no way to cancel
                  inverses*/
                return false;
            }
        }
        else if(prevTIaction == SolverOperation.APINVFUN){
            /*for generic application of the inverse of some function,
              don't bother getting the inverse of the pre-existing
              function ...*/
            try{
                String fun = typeinSM.runScript("function",
                                          prevTIside);
                /*... and compare the function name directly to the
                  user's input*/
                if(fun.equalsIgnoreCase(prevTIinput) &&
                   typeinSM.similar(typeinSM.evalFuncs(input),
                              typeinSM.evalFuncs(comparison))){
                    return !unchanged;
                }
                else{
                    return false;
                }
            }
            catch(NoSuchFieldException nsfe){
                /*the thing we're applying the function to is not
                  itself a function, so there's no way to cancel
                  inverses*/
                return false;
            }
        }
		else{
			if(Logger.LoggingOn){
				Logger.log("solverdebug","ST.tE: nothing specific to action " + prevTIaction + "; false");
			}
			return false;
		}
	}

	/*tests whether ex1 and ex2 are equal modulo combining terms of
      the same form as 'term'.

	  Examples:
	  3x+4x+5-5,3x+4x,5 ==> true
	  ax+bx,ax+bx+c-c,c ==> true
	  3x+4x+5-5,7x,5 ==> false

	  also supports more complex values for term.  For example:
	  2+4x-1-3x,1+x,1+3x ==> true (since 2 & 1 match the "1" term of
	  'term', and 4x & 3x match the "3x" term of 'term'*/
	private boolean exprCLTequiv(String ex1,String ex2,String term) throws BadExpressionError{
		//Logger.log("solverdebug","ST.eCLTe(" + ex1 + "," + ex2 + "," + term + ")");
		SymbolManipulator sm = new SymbolManipulator(SMParserSettings.HS_DEFAULT);
		sm.setMaintainVarList(true);
		sm.allowExtraParens = false;
		sm.allowDoubleSigns = false;

		String diff = sm.subtract(ex1,ex2);

		/*anything that was left out has to have been combinable
          (which means it was equal to 0)*/
		if(!sm.algebraicEqual(diff,"0")){
			//Logger.log("solverdebug","         " + diff + " != 0: false");
			return false;
		}

		try{
			Queryable[] termTerms = sm.runArrayScript("terms",sm.sort(term));
			String[] termPatterns = new String[termTerms.length];
			for(int i=0;i<termTerms.length;i++){
				termPatterns[i] = termTerms[i].getStringValue();
				//termPatterns[i] = sm.getPattern(termTerms[i].getStringValue() + "=0").getLeft().toString();
			}

			/*we need to manually construct a version of diff with only
			  some of its terms combined (CLT in the SM is
			  all-or-nothing), so we can check its terms.*/
			Queryable ex1Terms[] = sm.runArrayScript("terms",sm.sort(ex1));
			Queryable ex2Terms[] = sm.runArrayScript("terms",sm.sort(ex2));
			Queryable diffTerms[] = eeDiff(ex1Terms,ex2Terms);
			if(diffTerms.length > 0){
				diff = diffTerms[0].getStringValue();
				for(int i=1;i<diffTerms.length;i++){
					diff = sm.add(diffTerms[i].getStringValue(),diff);
				}

				//Logger.log("solverdebug","         " + diff + " == 0: checking terms");

				int termCount = Integer.valueOf(sm.runScript("length of terms",diff)).intValue();

				for(int i=1;i<=termCount;i++){
					//Logger.log("solverdebug","         checking term: " + sm.runScript("absolute value of item " + i + " of terms",diff));
					boolean found = false;
					for(int j=0;j<termPatterns.length && !found;j++){
						//Logger.log("solverdebug","               against: " + termPatterns[j]);
						try{
							sm.runScript("term like " + termPatterns[j] + " of absolute value of item " + i + " of terms",diff);
							found = true;
						}
						catch(NoSuchFieldException nsfe){
							//Logger.log("solverdebug","               doesn't match: " + nsfe);
							/*terms don't match; we'll keep looping*/
						}
					}
					if(!found){
						throw new NoSuchFieldException("term number " + i + " does not match");
					}
				}

				/*all of the terms match the pattern, so combining them was legal in this case*/
				//Logger.log("solverdebug","         all matched: true");
				return true;
			}
			else{
				return true;
			}
		}
		catch(NoSuchFieldException nsfe){
			/*one of the terms didn't match, so it wasn't legal to combine it*/
			//Logger.log("solverdebug","         failed: " + nsfe.toString());
			return false;
		}
	}

	/*calculates the diff of q1 and q2, assuming they're expressions
      and using sm.exactEqual() as the test for equality.  leaves "0"
      terms out of the result.*/
	private Queryable[] eeDiff(Queryable[] q1,Queryable[] q2) throws BadExpressionError{
		int total = q1.length + q2.length;
		for(int i=0;i<q1.length;i++){
			for(int j=0;j<q2.length;j++){
				if((q1[i] != null) && (q2[j] != null) &&
				   sm.exactEqual(sm.reduceFractions(sm.multiplyThrough(q1[i].getStringValue())),
								 sm.reduceFractions(sm.multiplyThrough(q2[j].getStringValue())))){
					q1[i] = q2[j] = null;
					total -= 2;
				}
				else{
					if((q1[i] != null) &&
					   sm.exactEqual(q1[i].getStringValue(),"0")){
						q1[i] = null;
						total--;
					}
					if((q2[j] != null) &&
					   sm.exactEqual(q2[j].getStringValue(),"0")){
						q2[j] = null;
						total--;
					}
				}
			}
		}

		Queryable[] ret = new Queryable[total];
		int k=0;
		for(int i=0;i<q1.length;i++){
			if(q1[i] != null){
				ret[k] = q1[i];
				k++;
			}
		}
		for(int i=0;i<q2.length;i++){
			if(q2[i] != null){
				ret[k] = q2[i];
				k++;
			}
		}

		return ret;
	}

	public SAI solveIt(ExampleTracerEvent result, boolean isSimSt) {

		trace.out("miss", "SolverTutor.solveIt" + "   originalProblem: " + originalProblem + "   currentProblem: " + currentProblem);
		boolean foundDoneStep = false;
		SAI nextStep = null;
		
		try {
			for(int i=0;!foundDoneStep && (i < 25) && !isDone("Solver"); i++) {
				nextStep = getNextStep("Solver");
				trace.out("miss", "getNextStep  >>"  + nextStep); 
				if(isDoneStep(nextStep))
					foundDoneStep = true;
				else {
					doStep(nextStep, result);
                    trace.out("miss", "doStep >>" + nextStep);
					
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// Get the final answer of the form "x=a"
		// where a is the solution of the equation
		nextStep = getNextStep("Solver");
		trace.out("miss", "getNextStep  >>"  + nextStep); 
		trace.out("miss", "Exit from solveIt");
		return nextStep;
	}
	
	public void solveIt(ExampleTracerEvent result){
		if (Logger.LoggingOn) {
			Logger.log("solverdebug","Auto-solve: begin");
		}
        boolean foundDoneStep = false;

		try{
			for(int i=0;!foundDoneStep && (i < 25) && !isDone("Solver");i++){
				SAI nextStep = getNextStep("Solver");
                if(isDoneStep(nextStep)){
            		if (Logger.LoggingOn) {
            			Logger.log("solverdebug","Auto-solve: not doing done step: " + nextStep);
            		}
                    foundDoneStep = true;
                }
                else{
            		if (Logger.LoggingOn) {
            			Logger.log("solverdebug","Auto-solve: doing step " +
                               i + ": " + nextStep);
            		}
                    doStep(nextStep, result);
                }
			}
		}
		catch(Exception e){
			Logger.log("solverdebug","Auto-solve: " + e);
			Logger.log(e);
		}

		if (Logger.LoggingOn) {
		if(foundDoneStep || isDone("Solver")){
			Logger.log("solverdebug","Auto-solve: done");
		}
		else{
			Logger.log("solverdebug","Auto-solve: failed");
		}
		}		
	}

	/*-------------------------------------\
	|  BEGIN TestableTutor implementation  |
	\-------------------------------------*/
	public SAI getNextStep(String tutorName) { // TODO is there a purpose for this param?
		int currentState = currentProblem.getState();
		String action = "right";

		switch(currentState){
		case STEPCOMPLETED:
			Rule helpRule = strategicRules().findRuleForHelp(currentProblem,tempValidActions);
			/** helpRule is returning as 'null' in some cases */
			if (helpRule==null) {
				if (Logger.isLoggingOn()) {
					Logger.log("solvertrace", "SolverTutor.getNextStep :: ***ERROR helpRule returned as NULL");
				}
				return null;
			}
			SolverOperation op = helpRule.getAction();
			if( op == SolverOperation.FACT && helpRule instanceof SubexRule ) {
				op = SolverOperation.FACTOR_IN_TESTMODE;
			}
			SAI ret = new SAI(currentProblem.toString(),op.getOpcode(),getInputForRule(helpRule));
			return ret;
		case LEFTNOTSET:
			action = "left";
		default: //RIGHTNOTSET:
			String selection = null;//solver doesn't care about selection
			String input = null;
			try{
				if(currentProblem.isSimpExpression()){
					input = currentProblem.toString();
				}
				else{
					input = currentProblem.getEquation().getProperty(action).getStringValue();
				}
			}
			catch (NoSuchFieldException e) {
				Logger.log(e);
			}
			return new SAI(selection,action,input);
		}
	}
	/**
	 * Used to kick-off the recursive check for classified children problems.
	 * @return boolean true if unclassified children exist, false if all children are classified
	 */
	public boolean hasUnclassifiedSolutions() {
	    return originalProblem.foundUnclassifiedChildren();
	}
	

	/** Get all possible next steps which are matched. */
	public SAI[] getAllNextSteps(String tutorName) {
        int currentState = currentProblem.getState();
        SAI[] ret;

        switch(currentState){
        case STEPCOMPLETED:
            Rule[] helpRules = strategicRules().findAllowedRules(currentProblem,tempValidActions);
            ret = new SAI[helpRules.length];
            for (int i = 0; i < helpRules.length; i++) {
                ret[i] = new SAI(currentProblem.toString(),helpRules[i].getAction().getOpcode(),
                                 //mmmBUG should expand to give separate step for every possible input
                                 getInputForRule(helpRules[i]));
            }
            return ret;
        default:
                int sidecount = 0;
                int leftstat = currentProblem.getSideStatus(LEFT_STR);
                boolean leftUnset = leftstat == TYPEIN_UNSET;
                if(leftUnset) sidecount++;

                int rightstat = currentProblem.getSideStatus(RIGHT_STR);
                boolean rightUnset = rightstat == TYPEIN_UNSET;
                if(rightUnset) sidecount++;

                /*cut down on the branching factor ...*/
                if(System.getProperty("trimtypeinsteps") != null){
                    sidecount = 1;
                    if(leftUnset) rightUnset = false;
                }

                ret = new SAI[sidecount];
                int i=0;
                if(leftUnset){
                    String input;
                    if(currentProblem.isSimpExpression()){
                    	input = currentProblem.toString();
                    }
                    else{
                    	input = currentProblem.getEquation().getLeft().toString();
                    }
                    ret[i] = new SAI(null,"left",input);
                    i++;
                }
                if(rightUnset){
                    String input = currentProblem.getEquation().getRight().toString();
                    ret[i] = new SAI(null,"right",input);
                    i++;
                }

                return ret;
        }
	}

    private String getInputForRule(Rule r){
        String[] inputs = getInputsForRule(r);
        if(inputs != null && inputs.length > 0){
            return inputs[0];
        }
        else{
            return null;
        }
    }

    /**Helper method to resolve the inputs for the given rule*/
    private String[] getInputsForRule(Rule helpRule){
        String[] inp = null;
        if(helpRule instanceof SubexRule){
            /*special handling to figure out input, because rule does
              not specify it*/
            String subcompProp = helpRule.getAction().getSubcomponentBooleanProp();
            try{
                Queryable[] subExs = sm.runArrayScript("components with property " + subcompProp,
                                                       currentProblem.toString());
                if(subExs != null && subExs.length > 0){
                    inp = new String[subExs.length];
                    for(int i=0;i<subExs.length;i++){
                        inp[i] = subExs[i].getStringValue();
                    }
                }
            }
            catch(BadExpressionError bee){
				Logger.log("SolverTutor.getInputsForRule.helpRule :: ***BadExpressionError caught processing rule:["+ helpRule + "] msg [" + bee.getMessage() + "]");
                return null;
            }
            catch(NoSuchFieldException nsfe){
				Logger.log("SolverTutor.getInputsForRule.helpRule :: ***NoSuchFieldException caught processing rule:["+ helpRule + "] msg [" + nsfe.getMessage() + "]");
                return null;
            }
        }
        else{
            inp = Rule.interpretInput(currentProblem,helpRule.getInput());
        }
        return inp;
    }

	/**
	 * Perform the step which is passed as an argument.
	 * @param step student's selection, action, input
	 * @return result of {@link #doStep(SAI, ExampleTracerEvent) doStep(step, null)}
	 */
	public boolean doStep(SAI step)
			throws cl.utilities.TestableTutor.InvalidStepException {
		return doStep(step, null);
	}

	/**
	 * Extract the texts from an array of CL hint messages.
	 * @param messages
	 * @return array with each element a {@link CL_TutorMessage#getMessageText()}
	 */
    public static String[] getMessageTexts(CL_TutorMessage[] messages) {
    	if (messages == null)
    		return null;
    	ArrayList<String> result = new ArrayList<String>();
    	for (int i = 0; i < messages.length; ++i) {
    		String msg = messages[i].getMessageText();
    		if (msg != null && msg.length() > 0)
    			result.add(msg);
    	}
    	if (result.size() < 1)
    		return null;
    	else
    		return result.toArray(new String[result.size()]);
	}
	
	/**
	 * Perform the step which is passed as an argument.
	 * @param step student's selection, action, input
	 * @param result stuff this event with evaluation data
	 * @return true
	 */
	public boolean doStep(String selection, String action, String input, ExampleTracerEvent result)
			throws cl.utilities.TestableTutor.InvalidStepException {
		SAI step = new SAI(selection, action, input);
		boolean stepResult = doStep(step, result);
		trace.out("solverdebug", "doStep("+step+") returns "+stepResult+"("+result.getResult()+")"+
				": "+result.getInterfaceActions());
		return stepResult;
	}

	/**
	 * Perform the step which is passed as an argument. Updates {@link #testableTutorDone} for
	 * {@link #isDone(String)} after trace.
	 * @param step student's selection, action, input
	 * @param result stuff this event with evaluation data
	 * @return return from
	 *  {@link #checkStudentAction(SolverTutorProblem, SolverOperation, String, ExampleTracerEvent)}
	 */
	public boolean doStep(SAI step, ExampleTracerEvent result)
	throws cl.utilities.TestableTutor.InvalidStepException {
	    /** in some cases, the SAI is actually null... */
		if (step==null || step.getAction() == null) {
			throw (new cl.utilities.TestableTutor.InvalidStepException());
		}
		SolverOperation op = SolverOperation.getOpByCode(step.getAction());
		if (op==null) {
			throw (new cl.utilities.TestableTutor.InvalidStepException());
		}
		boolean stepResult = checkStudentAction(currentProblem, op, step.getInput(), result);
		
		// Update testableTutorDone for isDone().
		if (!isDoneStep(step) && result != null) {
			result.setTutorSAILocked(true);                 // avoid overwriting SAI already derived
			testableTutorDone = checkTutorDone();
			result.setSolverDone(testableTutorDone);
			result.setTutorSAILocked(false);                // unlock for silent hint, etc.
		}
				
		//SolverFrame.getSelf().performAction(step.getAction(),step.getInput(),TEST_ACT_SRC);
		// return value should eventually indicate whether the step was actually performed
		return stepResult;
	}

	/** Start a problem with the equation information passed as an argument. */
	public void startProblem(String[] arg) throws cl.utilities.TestableTutor.InitializationException {
		String semicolonProblem = "";
		// convert arg to format "equation;variable;prompt"
		for (int i = 0; i < arg.length; i++) {
			semicolonProblem += arg[i];
			if (i < (arg.length-1)) {
				semicolonProblem += ";";
			}
		}

        try {
        	startProblem(semicolonProblem);
        } catch (Exception e) {
            Logger.log("SolverTutor.startProblem: " + e);
            Logger.log(e);
        }

        //testSolverFrame.setNextEquation(arg[0]); // this way seems to work too

		//Logger.log("solverdebug",temp);
	}

    public boolean isDoneStep(SAI step){
        return getTutorAction(SolverOperation.getOpByCode(step.getAction())) == CHECK_DONE;
    }

    public boolean isDone(String tutorName){
    	return testableTutorDone;
        /*String doneParam = getParameter(tutorName,"isDone");
         return Boolean.valueOf(doneParam).booleanValue();*/
    }

	/** Get the current value of a parameter. */
	public String getParameter(String tutorName,String paramName) {
        /*if(testSolverFrame != SolverFrame.getSelf()){
            Logger.log("solver frames not equal: " + testSolverFrame + " != " + SolverFrame.getSelf());
        }*/
        //Logger.log("currently on step number " + SolverFrame.getSelf().getCurrentStepNumber());

		// NOT YET FULLY IMPLEMENTED
		if (paramName.equalsIgnoreCase("EQUATION")) {
			return currentProblem.toString();
		}
		/*else if(testSolverFrame != null){
//			try{
				return testSolverFrame.getProperty(paramName).toString();
//			}
//			catch(CommException de){
//				return "invalid parameter";
//			}
		}*/
		else{
			return "invalid parameter";
		}
	}

	/** Set the current value of a parameter. */
	public void setParameter(String tutorName,String paramName,String paramValue) throws InvalidParamException{
		// NOT YET FULLY IMPLEMENTED
		// should probably include here a way to set the currently available Solver menu items
		if (paramName.equalsIgnoreCase(EQUATION_PROP)) {
            currentProblem = new SolverTutorProblem(paramValue,this);
		}
		else if(paramName.equalsIgnoreCase(TYPEIN_PROP)){
			setUseTypein(Boolean.valueOf(paramValue).booleanValue());
		}
		else if(paramName.equalsIgnoreCase(AUTOSIMPLIFY_PROP)){
			setAutoSimplify(Boolean.valueOf(paramValue).booleanValue());
		}
		else if(paramName.equalsIgnoreCase(GOAL_PROP)){
			setGoal(SolverGoal.getGoalByName(paramValue));
		}
	}

	public State getCurrentState(){
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try{
            ObjectOutputStream outObj = new ObjectOutputStream(outStream);

            /*if(testSolverFrame != null){
                testSolverFrame.setIconImage(null);
            }
            if(SolverFrame.getSelf() != null){
                SolverFrame.getSelf().setIconImage(null);
            }*/

            outObj.writeObject(currentProblem);
            outObj.writeObject(currentRules);
            outObj.writeBoolean(currentRulesPassed);
            outObj.writeBoolean(testReady);
            //outObj.writeObject(tempValidActions);
            outObj.writeObject(prevTIaction);
            outObj.writeObject(prevTIinput);
            outObj.writeObject(ruleUpdates);
            outObj.writeBoolean(testableTutorDone);

            outObj.flush();
        }
        catch(IOException ioe){
            Logger.log(ioe);
            System.exit(1);
        }

        return new SerializedState(outStream.toByteArray());
    }

	public void setState(State s){
        if(s instanceof SerializedState){
            ByteArrayInputStream inStream = new ByteArrayInputStream(((SerializedState)s).getSerialized());
            try{
                ObjectInputStream inObj = new ObjectInputStream(inStream);

                currentProblem = (SolverTutorProblem)inObj.readObject();
                currentRules = (SkillRule[])inObj.readObject();
                currentRulesPassed = inObj.readBoolean();
                testReady = inObj.readBoolean();
                //testSolverFrame = SolverFrame.getSelf();
                //tempValidActions = (SolverOperation[])inObj.readObject();
                prevTIaction = (SolverOperation)inObj.readObject();
                prevTIinput = (String)inObj.readObject();
                ruleUpdates = (HashSet)inObj.readObject();
                testableTutorDone = inObj.readBoolean();
            }
            catch(IOException ioe){
                Logger.log(ioe);
            }
            catch(ClassNotFoundException cnfe){
                Logger.log(cnfe);
            }
        }
    }

    public String getStateString(){
        return getParameter("Solver","EQUATION");
    }

	// Model Query Methods
	/** Is the given step valid according to the model? */
	public boolean isStepOK(SAI step) throws cl.utilities.TestableTutor.InvalidStepException {
		if (step.getAction() == null) {
			throw (new cl.utilities.TestableTutor.InvalidStepException());
		}
		return (getModelRule(step) != null);
	}

	/** What is the name of the rule matched with the given step? */
	public String getModelRule(SAI step) throws cl.utilities.TestableTutor.InvalidStepException {
        int currentState = currentProblem.getState();
        RuleMatchInfo foundRule = null;

        switch(currentState){
        case STEPCOMPLETED:
    		if (Logger.LoggingOn) {
    			Logger.log("solvertrace", "SolverTutor.getModelRule :: Using Goal [" + getGoal() + "]");
    		}

            foundRule = strategicRules().findRuleToFire(currentProblem,
                                                        SolverOperation.getOpByCode(step.getAction()),
                                                        step.getInput(),
                                                        tempValidActions);
            break;
        default:
            try{
                foundRule = cycleTutorTypeinInternal(step.getAction(),step.getInput(),false);
            }
            catch(NoSuchFieldException nsfe){;}
            catch(BadExpressionError bee){;}
    		if (Logger.LoggingOn) {
    			Logger.log("typein getModelRule foundRule: " + foundRule);
    		}
            break;
        }
        if (foundRule != null && foundRule.getBoolean() == true) {
            if(foundRule.getRule() != null){
                return (foundRule.getRule().getName());
            }
            else{
                return "null rule";
            }
        }
        else {
            return null;
        }
	}
	
	// Bug Query Methods
	/** Is there a bug message for the given step? */
	public boolean hasBugMessage(SAI step) throws cl.utilities.TestableTutor.InvalidStepException {
		return (getBugMessage(step) != null);
	}

	/** What is the bug message for the given step? */
	public String[] getBugMessage(SAI step) throws cl.utilities.TestableTutor.InvalidStepException {
		String[] ret = null;
		Rule bugRule = bugRules().findRuleToFire(currentProblem,SolverOperation.getOpByCode(step.getAction()),step.getInput(),null,"");
		if (bugRule != null) {
			ret = bugRule.getMessages(currentProblem);
		}
		return ret;
	}

	/** What is the name of the bug rule for the given step? */
	public String getBugRule(SAI step) throws cl.utilities.TestableTutor.InvalidStepException {
		String ret = null;
		if (Logger.LoggingOn) {
			Logger.log("solvertrace", "SolverTutor.getBugRule :: Using Bug-Goal [" + getGoal() + "]");
		}
		Rule bugRule = bugRules().findRuleToFire(currentProblem,SolverOperation.getOpByCode(step.getAction()),step.getInput(),null,"");
		if (bugRule != null) {
			ret = bugRule.getName();
		}
		if (Logger.LoggingOn) {
			Logger.log("solvertrace", "SolverTutor.getBugRule :: Found match == [" + ret + "]");
		}
		return ret;
	}
	
	// Hint Query Methods
	/** Is there a hint message for the given step? */
	public boolean hasHintMessage(SAI step) throws cl.utilities.TestableTutor.InvalidStepException {
		return (getHintMessage(step) != null);
	}

	/** Handles hint requests for this problem by passing them to the SolverTutor */
	public void requestHint(ExampleTracerEvent result) {
		checkStudentAction(SolverOperation.HINT, null, result);
	}

	/**
	 * Get the hint information for the current step.
	 * @param selection
	 * @param action
	 * @param input
	 * @param result
	 * @return
	 * @throws cl.utilities.TestableTutor.InvalidStepException
	 */
	public boolean getHintMessage(String selection, String action, String input, ExampleTracerEvent result)
			throws cl.utilities.TestableTutor.InvalidStepException {
		SAI step = new SAI(selection, action, input);
		String ret = null;
		//getValidActions(); // We're just hardcoding the valid actions for now...
		if (Logger.LoggingOn) {
			Logger.log("solvertrace", "SolverTutor.getHintRule :: Using Goal == [" + getGoal() + "]");
		}
		Rule helpRule = strategicRules().findRuleForHelp(currentProblem,tempValidActions);
		if (helpRule == null)
			return false;
		if (result != null) {
			result.setTutorAdvice(helpRule.getMessages(currentProblem));
			addSkillName(result, helpRule.getName());
		}
		return true;
	}

	/** What is the hint message for the given step? */
	public String[] getHintMessage(SAI step) throws cl.utilities.TestableTutor.InvalidStepException {
		String[] ret = null;
		//getValidActions(); // We're just hardcoding the valid actions for now...
		Rule helpRule = strategicRules().findRuleForHelp(currentProblem,tempValidActions);
		if (helpRule != null) {
			ret = helpRule.getMessages(currentProblem);
		}
		return ret;
	}

	/** What is the name of the hint rule for the given step? */
	public String getHintRule(SAI step) throws cl.utilities.TestableTutor.InvalidStepException {
		String ret = null;
		//getValidActions(); // We're just hardcoding the valid actions for now...
		if (Logger.LoggingOn) {
			Logger.log("solvertrace", "SolverTutor.getHintRule :: Using Goal == [" + getGoal() + "]");
		}
		Rule helpRule = strategicRules().findRuleForHelp(currentProblem,tempValidActions);
		if (helpRule != null) {
			ret = helpRule.getName();
		}
		return ret;
	}
	
	// Skill Query Methods
	/** Which subskill names are involved in the given step? */
	public String[] getSkills(SAI step) throws cl.utilities.TestableTutor.InvalidStepException {
        if(step.getAction().equalsIgnoreCase("left") ||
           step.getAction().equalsIgnoreCase("right")){
            //use typein skills for left/right actions
            Set ret = new HashSet();
            if(currentRules != null){
                for(int i=0;i<currentRules.length;i++){
                    SkillRule[] foundSkills = typeinSkillRules().findAllRulesToFire(currentProblem.getPreviousProblem(),currentRules[i].getSubskillName());
                    if(foundSkills != null){
                        for(int j=0;j<foundSkills.length;j++){
                            ret.add(foundSkills[j].getSubskillName());
                        }
                    }
                }
            }

            int retSize = ret.size();
            String[] retS = new String[retSize];
            int i=0;
            for(Iterator it = ret.iterator();it.hasNext();){
                retS[i++] = (String)it.next();
            }
            return retS;
        }
        else{
            //use normal skills for all other actions
            String[] ret = null;
            Rule modelRule = null;
            SkillRule[] currentRules = null;
            RuleMatchInfo foundRule = strategicRules().findRuleToFire(currentProblem,
                                                                      SolverOperation.getOpByCode(step.getAction()),
                                                                      step.getInput(),
                                                                      tempValidActions);
            if (foundRule != null && foundRule.getBoolean() == true) { // found rule
                modelRule = foundRule.getRule();
                currentRules = skillRules().findAllRulesToFire(currentProblem,modelRule.getName());
            }
            if (currentRules != null) {
                ret = new String[currentRules.length];
                SkillRule foundSkill = null;
                for (int i = 0; i < currentRules.length; i++) {
                    foundSkill = currentRules[i];
                    ret[i] = foundSkill.getSubskillName();
                }
            }
            return ret;
        }
	}
	/*------------------------------------\
	|  END TestableTutor implementation   |
	\------------------------------------*/

	public Object getProperty(String prop){
        try{
            if(prop.equalsIgnoreCase("left") ||
               prop.equalsIgnoreCase("right") ||
               prop.equalsIgnoreCase("self")){
                return currentProblem.getProperty(prop);
            }
        }
        catch(NoSuchFieldException nsfe){
            ;//throw new NoSuchPropertyException(nsfe.toString());
        }
		return null;//throw new NoSuchPropertyException("getProperty: SolverTutor does not have property: "+prop);
	}
	
	public void setProperty(String theProp,Object theValue){// throws NoSuchPropertyException, DataFormatException {
		//throw new NoSuchPropertyException("setProperty: SolverTutor does not have property: "+theProp);
	}

	public void setUseTypein(boolean typein){
		useTypein = typein;
	}

	public boolean getUseTypein(){
		return useTypein;
	}

	public void setGoal(SolverGoal newGoal){
		if(newGoal != null){
			goal = newGoal;
			/** if the new Goal is to simplify Mixed and Improper expressions
			 * then we need to make some changes to the exisiting SM objects
			 * so we can process the mixed numbers that inevitably follow...
			 * NOTE: this method is called from both the UI and JUnit interfaces,
			 * which (at least initially) makes it an ideal spot for this 'extension'. */
			if (goal.equals(SolverGoal.SIMPLIFY_ADDSUB_MIXED_EXPRESSION) 
			        || goal.equals(SolverGoal.SIMPLIFY_MULTDIV_MIXED_EXPRESSION)) {
			    /** the parser setting needs to change to handle mixed numbers */
			    sm.setParserSettings(SMParserSettings.MIXED_NUMBERS_E_AS_VAR);
			    smUnsimp.setParserSettings(SMParserSettings.MIXED_NUMBERS_E_AS_VAR);
			    /** if add/sub processing is the goal, then we need autoMT==true to
			     * prevent the fractions after an LCD to be in an un-processable state 
			     * (given the available menu options).  
			     * Example: "1/3 + 1/2" -LCD-> "2/(3*2) + 3/(2*3)" */
				if (goal.equals(SolverGoal.SIMPLIFY_ADDSUB_MIXED_EXPRESSION)) {
				    /** and we need auto-MT on to prevent term expressions within the 
				     * fractions after an LCD op */
				    sm.autoMultiplyThrough = true;
				    smUnsimp.autoMultiplyThrough = true;
				} else {
					/** on the flip-side, we need to make sure the Mult/div goal does NOT
					 * have autoMT on, or else the entire problem goes to solution with a simple step. */
				    sm.autoMultiplyThrough = false;
				    smUnsimp.autoMultiplyThrough = false;
				}
			}
			/** bug 6714: we need SM to allow the rewrite of non-denom terms */
			else if (goal.equals(SolverGoal.SIMPLIFY_RATIONAL_EXPRESSION)) {
			    /** the parser setting should change to the default (?) */
			    sm.setParserSettings(SMParserSettings.HS_DEFAULT);
			    smUnsimp.setParserSettings(SMParserSettings.HS_DEFAULT);
				sm.autoLCDRewriteNonDenomTerms=true;
				smUnsimp.autoLCDRewriteNonDenomTerms=true;
			}
		}
		else{
			if (Logger.LoggingOn) {
				Logger.log("SolverTutor.setGoal: ERROR: goal is null");
			}
		}

		if (Logger.LoggingOn) {
			Logger.log("solverdebug", "SolverTutor.setGoal: goal: " + goal);
		}
	}

	public SolverGoal getGoal(){
		return goal;
	}

	public void setCltPmWholeSide(boolean cltpm){
		cltPmWholeSide = cltpm;
	}

	public boolean getCltPmWholeSide(){
		return cltPmWholeSide;
	}

	public TRESolverTutor getTREParent(){
		return treParent;
	}

	protected SymbolManipulator getSM(){
		return sm;
	}

	public void setAutoSimplify(boolean as){
		if(sm != null){
			sm.autoSimplify = as;
		}
	}

	protected SymbolManipulator getSMUnsimp(){
		return smUnsimp;
	}

	/*MessagingNode implementation*/

    public MessagingNode getMessagingParent(){
    	return getTREParent();
    }

    public void setParent(MessagingNode p){
        /*check for duplicate names*/
        if(p != null){
            Set childNames = getChildNames();
            Iterator childIt = childNames.iterator();
            while(childIt.hasNext()){
                String name = (String)childIt.next();
                if(p.getChildByName(name) != null){
            		if (Logger.LoggingOn) {
            			Logger.log("Cannot set new parent " + p + " of anonymous node " + this + " because its children's names conflict with existing children of the parent");
            		}
                    return;
                }
            }
        }

        /*no dupes; proceed*/
    	try{
    		treParent = (TRESolverTutor)p;
    	}
    	catch(ClassCastException cce){
    		Logger.log("SolverTutor's parent must be of type TRESolverTutor: " + p);
    	}
    }

	public void deliverMessage(MessageObject mo) throws MessageDeliveryException{
        children.deliverMessage(mo);
    }

    public void addChild(MessagingNode child){
        children.addChild(child);
    }

    public void removeChild(MessagingNode child){
        children.removeChild(child);
    }

    public AddressableNode getChildByName(String name){
        return children.getChildByName(name);
    }

    public Set getChildNames(){
        return children.getChildNames();
    }

	/**
	 * allows the problem to be set as a CL_Problem *
	 */
	public void setCurrentProblem( CL_Problem p )
	{
		;  // noop here (problem is a solverproblem, not a cl_problem)
	}
	/**
	 * identify yourself as being in TEST MODE
	 */

	private boolean in_test_mode = false;

	/** CTAT: Indicate in text where a field for student input is needed. */
	public static final String INPUT_BOX = "[input]";
	public boolean getInTestMode()
	{
		return in_test_mode;
	}

	public void setInTestMode( boolean b )
	{
		in_test_mode = b;
	}
	/**
	 * This is used when the tool subtype == 'TOOL' (ie. it's not a required tool) 
	 * and the user can enter any type of equation or expression they desire.
	 * In order to process the expression or equation, we need to specify which 
	 * rule groups to use when determining valid actions, hints, and error messages.
	 * If the expression parameter has any components qualify for the quadratic formula use
	 * we'll return "true".  
	 * @param expr String representing either the left or right side of an equation
	 * @return boolean true if any components qualify for the quadratic formula use; false otherwise
	 */
	private boolean containsQuadraticExpr(String expr) {
        String retValue = null;
	    try {
            retValue = sm.runScript("length of components with property canApplyQuadraticFormula", expr);
            if (retValue!=null && Integer.parseInt(retValue)>0) {
                return true;
            }
        } catch (Exception e) {
            Logger.log(e);
        }
        return false;
	}
	/**
	 * This is used when the tool subtype == 'TOOL' (ie. it's not a required tool) 
	 * and the user can enter any type of equation or expression they desire.
	 * In order to process the expression or equation, we need to specify which 
	 * rule groups to use when determining valid actions, hints, and error messages.
	 * If the expression parameter has any components that are Trig functions
	 * we'll return "true".  
	 * @param expr String representing either the left or right side of an equation
	 * @return boolean true if any components are Trig functions; false otherwise
	 */
	private boolean containsFunctionApp(String expr) {
        String retValue = null;
	    try {
            retValue = sm.runScript("length of components with property isFunApp", expr);
            if (retValue!=null && Integer.parseInt(retValue)>0) {
                return true;
            }
        } catch (Exception e) {
            Logger.log(e);
        }
        return false;
	}

	/**
	 * Used during the start of a new problem to set the use
	 * of an initial expression-entry field during Lesson subtypes.
	 * @param onOff boolean
	 */
	private void setInitialTypein(boolean onOff) {
	    this.useInitialProblemTypein = onOff;
	}
	/**
	 * Returns a boolean that indicates if this problem uses an 
	 * initial typein entry field.
	 * @return boolean
	 */
	public boolean useInitialTypein() {
	    return this.useInitialProblemTypein;
	}
	/**
	 * If an initial typein entry field is displayed, this represents
	 * the intial solver operation the user is carrying out by
	 * entering the initial expression.
	 * @param op SolverOperation
	 */
	private void setInitialTypeinOp(SolverOperation op) {
	    this.initialTypeinOperation = op;
	}
	/**
	 * Returns the initial solver op the user is carrying out by 
	 * entering the initial expression; Lesson subtypes.
	 * @return SolverOperation
	 */
	public SolverOperation getInitialTypeinOp() {
	    return this.initialTypeinOperation;
	}
	/**
	 * Wanted a clean method call to test if the action can be 
	 * a part of the Simplification menu.  Since we have multiple
	 * action types possible, the conditional for bug 8710 within 
	 * cSA() was growing a'bit unwieldy.
	 * @param action SolverActionType
	 * @return boolean
	 */
	private boolean isSimpMenuItem(SolverActionType action) {
	    return action.equals(SolverActionType.AT_SIMP_SUBEX)
				|| action.equals(SolverActionType.AT_SIMP_IMPL)
				|| action.equals(SolverActionType.AT_SIMP_SIDE);
	}

	public void processToolParameters( Properties params )
	{
		if ( params == null || params.size() == 0 )
		{
			return;
		}
		Enumeration keys = params.keys();
		Enumeration els = params.elements();
		String key;
		Object val;

		while ( keys.hasMoreElements() )
		{
			key = keys.nextElement().toString();
			val = els.nextElement();
			try { setParameter( "solver", key, val.toString()); }
			catch( InvalidParamException e ) { Logger.log( e );  }
		}
	}
	/**
	 * Process the typein step for B2A add/sub and mult/div problem
	 * types.
	 * @param solverOp SolverOperation
	 * @param userInput String
	 */
	private void checkB2AStudentTypeinAction(SolverOperation solverOp, String userInput,
			ExampleTracerEvent result) {
		/*cycle the tutor on the typein input*/
		RuleMatchInfo rmi = cycleB2ATutorTypein(solverOp, userInput);

		/*if tutor accepted the input ...*/
		if(rmi != null && rmi.getBoolean()){
			/*... update typein state of step*/
			if (solverOp.equals(SolverOperation.TERM1)) {
				currentProblem.setTypeinTermState(1, TYPEIN_SET);
			}
			else if (solverOp.equals(SolverOperation.TERM2)) {
				currentProblem.setTypeinTermState(2, TYPEIN_SET);
			}

			/*if this input completed the typein portion of the step ...*/
			if(currentProblem.getB2ATypeinCompleted()){
				/*... delete the typein steps and create a finished step*/
				/** remove any type-in objects that exist */
				Iterator iter = currentProblem.getChildNames().iterator();
				while (iter.hasNext()) {
					currentProblem.deleteChildByName((String)iter.next());
				}
				// save the currentStep
				SolverTutorProblem[] reStep =  new SolverTutorProblem[] { currentProblem };
				requestDeleteStep(currentProblem, result);
				/** we need to re-add the saved step to the tree, since we aren't actually creating a brandy-new one
				 * and it's the constructr that adds the children to the messaging tree. */
				currentProblem.addChild(reStep[0]);
				updateForNewSteps(reStep,reStep[0].getOperation(), reStep[0].getStringValue(),
						false, false, result);
			}
		}
	}
	/**
	 * 
	 * @param termOp
	 * @param userInput
	 * @return
	 */
	private RuleMatchInfo cycleB2ATutorTypein(SolverOperation termOp, String userInput) {
		RuleMatchInfo retRMI = null;
		
		if(Logger.LoggingOn){
			Logger.log("solverdebug","ST.cycleB2ATutorTypein(" + termOp + ", " + userInput + ")");
			Logger.log("solverdebug","ST.cycleB2ATutorTypein: Current problem: " + currentProblem);
		}

		try {
            retRMI = cycleB2ATutorTypeinInternal(termOp.getOpcode(), userInput);

            MessagingAddress termTypeinAddr = currentProblem.getMessagingAddress();
			if (termTypeinAddr == null){
				//happens normally w/ testable tutor
				if (Logger.LoggingOn) {
					Logger.log("solverdebug", "SolverTutor.cycleTutorTypein: sideAddr is null!");
				}
			} else {
				termTypeinAddr.addAddressLevel(termOp.getOpcode());
			}
			
			if (retRMI != null && retRMI.getBoolean()) {
				//Logger.log("solverdebug","ST.cTT: input " + input + " matches " + comparison);
				SendMessage.sendApprove(termTypeinAddr);
				SkillRule[] skillRules = updateSkillsTypeIn("1", TERMONE);	// the 2nd param isn't used at all...
                ResearchProtocol.recordCorrectTransaction(currentProblem,
                										  termOp,
                                                          userInput,
                                                          null,
                                                          skillRules);

			}
			else{
				SendMessage.sendFlag(termTypeinAddr);
				SkillRule[] skillRules = updateSkillsTypeIn("0", TERMONE);	// the 2nd param isn't used at all...
                ResearchProtocol.recordErrorTransaction(currentProblem,
								                        termOp,
								                        userInput,
								                        skillRules);

			}
		}
		catch (BadExpressionError err) {
			MessagingAddress sideAddr = currentProblem.getMessagingAddress();
			sideAddr.addAddressLevel(termOp.getOpcode());
			SendMessage.sendFlag(sideAddr);
			Logger.log("SolverTutor.cycleB2ATutorTypein :: ***Exception BadExpressionError caught ["+ err + "]");
		}
		catch (NoSuchFieldException err) {
			Logger.log("No such field in cycleTutorTypein");
			Logger.log(err);
		}
		
		return retRMI;
	}
	/**
	 * 
	 * @param opCode
	 * @param input
	 * @return
	 * @throws NoSuchFieldException
	 * @throws BadExpressionError
	 */
    private RuleMatchInfo cycleB2ATutorTypeinInternal(String opCode, String input) throws NoSuchFieldException, BadExpressionError {
		RuleMatchInfo retRMI = null;

        retRMI = checkB2ATypeinAnswer(opCode, input);
        //Logger.log("solverdebug","ST.cTT: comparing: " + input + " =?= " + comparison);
        retRMI.setSide("Left");	// this is still an expression...so default to 'left'

        if(Logger.LoggingOn && !retRMI.getBoolean()){
            Logger.log("solverdebug","ST.cTT: input " + input + " does not match. ");
        }

        return retRMI;
    }
    /**
     * 
     * @param term
     * @param input
     * @return
     * @throws BadExpressionError
     */
	private RuleMatchInfo checkB2ATypeinAnswer(String term, String input) throws BadExpressionError {

		boolean isEqual = b2aTypeinEqual(term, input);
		return new RuleMatchInfo(isEqual, null, null, input);

	}
	/**
	 * With the current BtA problem sets the only answer is an exact match.
	 * @param term
	 * @param userInput
	 * @return
	 * @throws BadExpressionError
	 */
	private boolean b2aTypeinEqual(String term, String userInput) throws BadExpressionError {
		boolean isExactMatch = false;
		
		
		if (term.equals(TERM_1_STR)) {
			isExactMatch = sm.exactEqual(currentProblem.getTermOneTargetValue(), userInput);
		}
		else if (term.equals(TERM_2_STR)) {
			isExactMatch = sm.exactEqual(currentProblem.getTermTwoTargetValue(), userInput);
		}

		return isExactMatch;
	}
}
