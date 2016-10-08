// *********************************************************************
// 
//                           Copyright (C) 2004
//                         Carnegie Learning Inc.
// 
//                          All Rights Reserved.
// 
//  This program is the subject of trade secrets and intellectual
//  property rights owned by Carnegie Learning.
// 
//  This legend must continue to appear in the source code despite
//  modifications or enhancements by any party.
// 
// *********************************************************************

/*
  $Id: SolverTutorProblem.java 12714 2011-07-14 23:40:40Z sewall $
  $Name$
*/

package cl.tutors.solver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import cl.common.PropertyConstants;
import cl.common.SolverActionType;
import cl.common.SolverKeys;
import cl.common.SolverOperation;
import cl.common.SolverProblem;
import cl.communication.AddressableNode;
import cl.communication.MessagingNode;
import cl.communication.SendMessage;
import cl.communication.SimpleAddressableNode;
import cl.tutors.solver.rule.SolverGoal;
import cl.tutors.tre.TutorVerbRecipient;
import cl.tutors.tre.TutoredStep;
import cl.ui.tools.tutorable.SolverTool;
import cl.utilities.StringMap;
import cl.utilities.Logging.Logger;
import cl.utilities.sm.BadExpressionError;
import cl.utilities.sm.Equation;
import cl.utilities.sm.Expression;
import cl.utilities.sm.SubexpressionIdentifier;
import cl.utilities.sm.SymbolManipulator;
import cl.utilities.sm.VariableExpression;
import cl.utilities.sm.query.Queryable;
import cl.utilities.sm.query.StandardMethods;
import cl.utilities.sm.query.StringQuery;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerTracer;
import edu.cmu.pact.Utilities.trace;

/**This class represents a problem being tutored by a
   <code>SolverTutor</code>.  The problem might be an equation that is
   to be solved or an expression that is to be simplified.  In the
   future support could be added for inequalities as well as
   equations.  Simultaneous equations are dealt with entirely on the
   interface side, with one tutor for each equation, so this class
   does not handle that case at all.

   <p>
   The solver's rules operate on instances of this class via its
   <code>Queryable</code> interface.*/
public class SolverTutorProblem extends SolverProblem implements Queryable, TutoredStep, TutorVerbRecipient{
	
	/**When solving an equation, this is the variable for which we're
       solving.  When simplifying an expression, this remains
       <code>null</code>.*/
    private String targetVar = null;

    private int leftState = TYPEIN_SET;
    private int rightState = TYPEIN_SET;

    /**The operation that resulted in this step in the problem*/
    private SolverOperation operation = null;

    /**The input that resulted in this step in the problem*/
    private String input = null;

    /**If true, the only acceptable operation is "separate
       plus/minus"*/
    private boolean containsPlusMinus = false;

    /**Identifier for the current subexpression being worked on in a
       scratchpad*/
    private SubexpressionIdentifier subexId = null;
 
    /**Side of the equation containing the current subexpression being
       worked on in a scratchpad*/
    private int subexIdSide = LEFTSIDE;

    /** The prompt presented to the user, describing the goal of this problem */
    private String prompt = null;
    
    /** added for B2A Typein */
    private int targetCount;
    private String termOneTarget;
    private String termTwoTarget;
    private int termOneState = TYPEIN_SET; 
    private int termTwoState = TYPEIN_SET; 

    /**Private constructor used by <code>makeNextStep</code> to
       initialize <code>origProb</code> and <code>targetVar</code>.*/
    private SolverTutorProblem(SolverTutorProblem parent,String targVar){
    	super(parent);
        targetVar = targVar;
    }

    /**
	 * Private constructor used by <code>getExpressionProblem</code>. Does
	 * not link the new problem into the messaging hierarchy.
	 */
    private SolverTutorProblem(){
    	;
    }

    /**Used to create the original problem.  <code>problem</code> is
       either an expression to be simplified or an equation to be
       solved.  This method figures out which and calls the
       appropriate helper method (<code>initExpression</code> or
       <code>initEquation</code>).*/
    public SolverTutorProblem(String problem,SolverTutor parentTutor){
    	super();
    	parentTutor.addChild(this);

        //forget about old variable settings
        SymbolManipulator.forgetVarList();

        StringTokenizer problemTok = new StringTokenizer(problem,";");
        problem = problemTok.nextToken();
        try{
            targetVar = problemTok.nextToken();
            prompt = problemTok.nextToken();
        }
        catch(NoSuchElementException nsee){
            /*
			 * okay, no target variable or prompt specified; we'll deal with
			 * this later if it turns out that the problem is even an equation
			 */
        }

        /** spk 7/6/05 -- check to see if what we had was a "expr;prompt"
         * instead of the (default) "expr;var;prompt" */
        if (targetVar!=null && prompt==null && targetVar.length() > 5 /* <-- arbitrary number */) {
            prompt = targetVar;
            targetVar=null;
        }
        
        int iEq = problem.indexOf('=');

        if(iEq == -1){
            /*expression to simplify*/
            ex = initExpression(problem, parentTutor.getSM());
            unsimpEx = ex;
        }
        else{
            /*equation to solve*/
            eq = initEquation(problem,true);
            unsimpEq = eq;
        }

        /*generate default prompt if none supplied*/
        if(prompt == null){
            /** bug 8710: create the prompt for dynamically generated polynomial add/subt problems.
             * If it fails to create a message, this will return the SIMP_PROMPT below... */
    		if (parentTutor.useInitialTypein()) {
    		    prompt = generateDynamicPolynomialPrompt(unsimpEx.toString());
    		}
        	else if(isSimpExpression()) {
        		prompt = SolverKeys.getString(SolverKeys.SIMP_PROMPT);
        	}
        	else {
        		prompt = SolverKeys.getString(SolverKeys.SOLVE_FOR) + targetVar;
        	}
        }
        
        /**
         * If we are to display an initial typein entry for the user during Lesson subtypes,
         * then we also need to set the problem state and operation action for proper
         * (SideRuleTemplate) hinting.  
         */
		if (parentTutor.useInitialTypein()) {
		    Logger.log("solverdebug", "SolverTutorProblem.SolverTutorProblem :: Modifying the state from [" + getState() + "] to [" + LEFTNOTSET + "]");
//		    setStateForTypein(true);	// so we have LEFTNOTSET on the initial problem
			leftState = TYPEIN_UNSET;	// the 'normal' method did not work because the whole problem is not 
    		if(isSolveEquation()){		// a "typein"...so these three lines are replicated here
    			rightState = TYPEIN_UNSET;	// for direct configuration in this special case
    		}

    		Logger.log("solverdebug", "SolverTutorProblem.SolverTutorProblem :: Updating the step's operation to [" + parentTutor.getInitialTypeinOp() + "]");
		    setOperation(parentTutor.getInitialTypeinOp());
		}

        
    }
    /**
     * Generate a dynamic initial type-in prompt based upon the polynomial
     * expression passed in as a param.
     * @param expr String 
     * @return String
     */
    private String generateDynamicPolynomialPrompt(String expr) {
        StringBuffer sb = new StringBuffer(50);	// setting an initial size that should be (genrally) larger enough
        String tempScript = null;
        SymbolManipulator localSM = getParentTutor().getSM();	// use the parser settings of the parent tool
        
        try {
            /** find the number of terms...we need at least 2 to create this prompt 
             * initially (bug 8710) but I can certainly see this expanding in the future */
            int nbrOfTerms = Integer.parseInt(localSM.runScript("length of terms", expr));
            if (Logger.isLoggingOn()) {
                Logger.log("solverdebug", "SolverTutorProblem.generateDynamicPolynomialPrompt :: number of terms == [" + nbrOfTerms + "]");
            }
            if (nbrOfTerms<2) {	// for this pass at the method, don't do a thing and return (what would have been) the default
                return SolverKeys.getString(SolverKeys.SIMP_PROMPT);
            }
            
            String termOp = localSM.runScript("operator of term 2", expr);
            if (Logger.isLoggingOn()) {
                Logger.log("solverdebug", "SolverTutorProblem.generateDynamicPolynomialPrompt :: termOp == [" + termOp + "]");
            }
            // TODO come up with a better method....
            if (termOp !=null && termOp.equals("+")) {
                sb.append("Add ");
            }
            else if (termOp !=null && termOp.equals("-")) {
                sb.append("Subtract ");
            }
            /** we should only have (based on the 8710 problem set) two 
             * terms....but just in case, loop through all the terms except
             * the last one.
             */
            for (int x=1; x<=nbrOfTerms-1; x++) {
                tempScript = "[unfence] [term " + x + "]";
                sb.append(HTML_EXPRESSION_STARTTAG);
                sb.append(localSM.runScript(tempScript, expr));
                sb.append(HTML_EXPRESSION_ENDTAG);
                sb.append(HTML_SPACE_VALUE);
                if (nbrOfTerms>2) {
                    sb.append(",");
                }
            }
            sb.append(" and ");
            /** we are using the sign of the second term to determine the action description, 
             * so we don't want to include it with the second term...
             * NOTE: this falls apart when there is more than 2 terms 
             * (which doesn't currently exist in the problem sets of bug 8710)
             */
            tempScript = "[unfence] [bodynosign of term " + nbrOfTerms + "]";
            sb.append(HTML_EXPRESSION_STARTTAG);
            sb.append(localSM.runScript(tempScript, expr));
            sb.append(HTML_EXPRESSION_ENDTAG);
            sb.append(HTML_SPACE_VALUE);
            sb.append(":");
            if (Logger.isLoggingOn()) {
                Logger.log("solverdebug", "SolverTutorProblem.generateDynamicPolynomialPrompt :: Returning prompt :: [" + sb.toString() + "]");
            }
            
        } catch (Exception e) {
            Logger.log(e);
        }
        /** if we couldn't construct a message, return the default prompt for simplifying an expression */
        return sb!=null ? sb.toString() : SolverKeys.getString(SolverKeys.SIMP_PROMPT);
    }
    /**Initializes an equation solving problem.  If targetVar already
       has a value, it is verified; if not (or if verification fails)
       a guess is made for what it should be.*/
    private Equation initEquation(String equation,boolean checkTargetVar){
        if(checkTargetVar){
            if(targetVar != null){
                /*make sure that supplied the target var actually
                  occurs in the equation*/
                try{
                    Equation test = new Equation(equation);
                    VariableExpression targetVarInfo = new VariableExpression(targetVar);
                    if(!verifyTargetVar(test,targetVarInfo)){
                        Logger.log("solverdebug","SolverProblem.initEquation: verification error");
                        targetVar = null;
                    }
                }
                catch(BadExpressionError bee){
                    Logger.log("solverdebug","SolverProblem.initEquation: verification error: " + bee.toString());
                    targetVar = null;
                }
            }

            if(targetVar == null){
                /*the caller didn't specify a variable to solve for
                  (or specified an invalid one), so we'll just take a
                  guess*/
                try{
                    targetVar = guessTargetVar(new Equation(equation));
                }
                catch(BadExpressionError bee){
                    /*Equation didn't parse ... handled below by just
                      setting targetVar to "x".*/
                }
            }

            if(targetVar == null){
                /*we're out of luck here -- no valid variable
                  specified by the user, and no variables in the
                  equation.*/
                /*mmmBUG ... not yet sure what's the correct thing to
                  do here*/
                Logger.log("solverdebug","SolverProblem.initEquation: ERROR: unable to determine target variable for " + equation + "; setting it to 'x' for now");
                targetVar = "x";
            }
        }

        /*now that we have some sort of target variable, we can
          initialize the equation object*/
        Equation ret = null;
        try{
            SymbolManipulator.setVarList(new String[] {targetVar});
            ret = new Equation(equation,new String[] {targetVar});
        }
        catch(BadExpressionError bee){
            Logger.log("solverdebug","SolverProblem.initEquation: equation does not parse: " + equation);
            Logger.log(bee);
        }

        if(Logger.LoggingOn){
            try{
                Expression ls = (Expression)ret.getProperty("left");
                Expression rs = (Expression)ret.getProperty("right");
                Logger.log("solverdebug","SP.initEquation: " + ls + " = " + rs);
                Logger.log("solverdebug","SP.initEquation: " + ls.debugForm() +
                           " = " + rs.debugForm());
            }
            catch(Exception e){
                Logger.log("solverdebug",e);
            }
        }

        return ret;
    }

    /**
	 * Given an equation, tries to guess the variable that we should solve for.
	 * We pick the first variable on the left if there is one, on the right
	 * otherwise. If there is no variable at all, returns null.
	 */
    public static String guessTargetVar(Equation e){
        String var = null;
        Queryable[] q;

        try{
            q = e.getProperty("variable side expression").getProperty("variables").getArrayValue();
            if(q.length > 0){
                var = q[0].getStringValue();
                //Logger.log("solverdebug","    using variable from left side: " + var);
            }
        }
        catch(NoSuchFieldException nsfe){
            //just return null
            Logger.log("solverdebug","ST: exception guessing target variable: " + nsfe.toString());
        }
        catch(NullPointerException npe){
            //this happens when the first line of the above try
            //statement fails somewhere in the middle (eg if the user
            //enters the 'equation' "1+2"
            Logger.log("solverdebug","ST: exception guessing target variable: " + npe.toString());
        }

        return var;
    }

    /**returns true if the variable v appears in the equation e, false
       otherwise*/
    public boolean verifyTargetVar(Equation e,VariableExpression v){
        boolean ret = false;

        try{
            Expression ls = (Expression)e.getProperty("left");
            Queryable[] vars = ls.getProperty("variables").getArrayValue();
            for(int i=0;i<vars.length;i++){
                if(v.getStringValue().equalsIgnoreCase(vars[i].getStringValue())){
                    ret = true;
                    break;
                }
            }

            /*if we didn't find it on the left, try the right*/
            if(!ret){
                Expression rs = (Expression)e.getProperty("right");
                vars = rs.getProperty("variables").getArrayValue();
                for(int i=0;i<vars.length;i++){
                    if(v.getStringValue().equalsIgnoreCase(vars[i].getStringValue())){
                        ret = true;
                        break;
                    }
                }
            }
        }
        catch(NoSuchFieldException nsfe){
            //Logger.log("solverdebug","vTV: failure because " + nsfe.toString());
        }

        return ret;
    }

    /**
	 * Creates an identical SolverTutorProblem as a child of this problem. Used
	 * as a fallback when we can't actually calculate the result of an
	 * operation for some reason.
	 */
    protected SolverTutorProblem makeIdenticalNextStep(){
    	SolverTutorProblem rtn = null;
    	if(isSimpExpression()){
    		rtn = makeNextStep(getExpression().toString());
    	}
    	else{
    		rtn = makeNextStep(getEquation().toString());
    	}
    	return rtn;
    }

    /**Creates a new problem that shares this problem's original
       problem and (if applicable) target variable attributes.  The
       supplied <code>problem</code> is used as both the normal and
       unsimplified version of the problem.*/
    public SolverTutorProblem makeNextStep(String problem){
        return makeNextStep(problem,problem);
    }

    /**Creates a new problem that shares this problem's original
       problem and (if applicable) target variable attributes.

       @param problem The new problem (an expression or an equation)
       @param unsimpProblem The unsimplified version of the new
                            problem*/
    public SolverTutorProblem makeNextStep(String problem,String unsimpProblem){
        if(isSolveEquation()){
            SolverTutorProblem ret = new SolverTutorProblem(this,targetVar);
            ret.eq = initEquation(problem,false);
            ret.unsimpEq = initEquation(unsimpProblem,false);

            return ret;
        }
        else{
            /*simplifing expression*/
            SolverTutorProblem ret = new SolverTutorProblem(this,null);
            ret.ex = initExpression(problem, getParentTutor().getSM());
            ret.unsimpEx = initExpression(unsimpProblem, getParentTutor().getSMUnsimp());

            return ret;
        }
    }

    /**
	 * Calculates a new problem resulting from applying the given operation
	 * (with the given input, if applicable) to this problem. If a new problem
	 * can be calculated directly, it is returned. If a scratchpad is required,
	 * it is created, and <code>null</code> is returned.
	 */
    public SolverTutorProblem[] calculateNextSteps(SolverOperation op,String inp,
    		boolean typeinSides,boolean typeinEquation,ExampleTracerEvent result){
    	SolverTutorProblem[] nextSteps;

    	/** 2005/08/18 spk: for B2A, the best course is to call specific to 
    	 * the processing we need for the (much simpler and direct) B2A Solver problems. */
    	if (typeinSides 
    			&& (	getParentTutor().getGoal().equals(SolverGoal.SIMPLIFY_ADDSUB_MIXED_EXPRESSION) 
    					|| getParentTutor().getGoal().equals(SolverGoal.SIMPLIFY_MULTDIV_MIXED_EXPRESSION))
    				) {
    		nextSteps = calculateImmediateB2ASteps(op, inp);
    	}    	
    	/*call appropriate helper method for op's action type*/
    	else if(op.getActionType().getIsScratchpadAction()
    			&& (result == null || !getParentTutor().getUseTypein())) {
    		SolverTutorProblem nextStep = calculateScratchpadStep(op,inp,result);
    		if(nextStep != null){
    			nextSteps = new SolverTutorProblem[] {nextStep};
    		}
    		else{
    			nextSteps = null;	// TODO: completely useless line, since we only hit here if it already _is_ null
    		}
    	}
    	else{
    		nextSteps = calculateImmediateSteps(op,inp);
    	}

    	if(nextSteps != null && (typeinSides || typeinEquation)){
    		
    		if (Logger.LoggingOn)
				Logger.log("solverdebug","***STProblem.calculateNextSteps("+op+","+inp+","+
						typeinSides+","+typeinEquation+"), nextSteps "+Arrays.asList(nextSteps));

    		/*add addressable nodes to receive typein input*/
    		if(isSolveEquation()){
    			for(int i=0;i<nextSteps.length;i++){
        			nextSteps[i].setStateForTypein(true);

        			if(typeinEquation){
        				nextSteps[i].addTypeinStep(SolverOperation.EQN/* , result */);
        			}
        			else{
        				String nextLeft = nextSteps[i].getLeftOrExpression();
        				String nextRight = (nextSteps[i].getEquation() == null ? null
        						: nextSteps[i].getEquation().getRight().toString());
        				if(inp != null && (
        						inp.equalsIgnoreCase(RIGHT_STR)
        						|| (result != null && getLeftOrExpression().equalsIgnoreCase(nextLeft)))){
        					nextSteps[i].setLeftFinished(nextSteps[i].getLeftOrExpression());
        				}
        				else{
        					nextSteps[i].addTypeinStep(SolverOperation.LEFT/* , result */);
        				}

        				String currRight = (getEquation() == null ? "" : getEquation().getRight().toString());
        				if(inp != null && (
        						inp.equalsIgnoreCase(LEFT_STR)
        						|| (result != null && currRight.equalsIgnoreCase(nextRight)))){
        					nextSteps[i].setRightFinished(nextSteps[i].getEquation().getRight().toString());
        				}
        				else{
        					nextSteps[i].addTypeinStep(SolverOperation.RIGHT/* , result */);
        				}
//        	    		if (result != null) {
//        	    			String promptText = null;
//        	    			if (SolverTutor.INPUT_BOX.equals(promptInput.get(0))) {
//        	    				if (SolverTutor.INPUT_BOX.equals(promptInput.get(1)))
//        	    					promptText = "Enter left- and right-hand sides.";
//        	    				else
//        	    					promptText = "Enter left-hand side.";
//        	    			} else if (SolverTutor.INPUT_BOX.equals(promptInput.get(1)))
//        	    				promptText = "Enter right-hand side.";
//        	    			result.addIaMessage("promptTypein", promptInput, promptText);
//        	    		}
        			}
    			}
    		}
    		/** if we're a B2A typein we need to add 1 or 2 typeins, dependant upon form+action */
    		else if (getParentTutor().getGoal().equals(SolverGoal.SIMPLIFY_ADDSUB_MIXED_EXPRESSION)
    				|| getParentTutor().getGoal().equals(SolverGoal.SIMPLIFY_MULTDIV_MIXED_EXPRESSION)) {
    			for (int i=0; i < nextSteps.length; i++) {	// whitespace is your friend....
    				nextSteps[i].addTypeinStep(SolverOperation.TERM1/* , result */);
    				/** only add the second object if our target result has 2 terms/factors */
    				if (nextSteps[i].getTypeinObjectCount()==2) {
    					nextSteps[i].addTypeinStep(SolverOperation.TERM2/* , result */);
    				}
    				/** un-set the typein state(s) */
        			nextSteps[i].setStateForB2ATypein(true);
    			}
    		}
    		else{
    			for(int i=0;i<nextSteps.length;i++){
    			nextSteps[i].addTypeinStep(SolverOperation.EXPR/* , result */);
        			nextSteps[i].setStateForTypein(true);
    			}
    		}
    	}

    	return nextSteps;
    }

    /**
	 * Calculates a new problem resulting from applying the given operation
	 * (with the given input, if applicable) to this problem. In typein mode, a
	 * scratchpad is created and <code>null</code> is returned. In notypein
	 * mode, the step is calculated automatically and returned.
	 */
    private SolverTutorProblem calculateScratchpadStep(SolverOperation op,String inp){
    	return calculateScratchpadStep(op, inp, null);
    }

    /**
	 * Calculates a new problem resulting from applying the given operation
	 * (with the given input, if applicable) to this problem. In typein mode, a
	 * scratchpad is created and <code>null</code> is returned. In notypein
	 * mode, the step is calculated automatically and returned.
	 */
    private SolverTutorProblem calculateScratchpadStep(SolverOperation op,String inp,
    		ExampleTracerEvent result){
    	SolverTutorProblem nextStep;
    	if(inp == null){
    		/*no suitable subex was found for this op,
    		  so just repeat the prev. problem*/
    	    Logger.log("solvertrace", "SolverTutorProblem.calculateScratchpadStep :: ***WARNING: input was <null>, repeating current problem.");
    		nextStep = makeIdenticalNextStep();
        	if (result != null) {
        		result.setResult(ExampleTracerTracer.NULL_MODEL);
        		result.makeTutorSAI(null, SolverTutor.getOpcode(op), inp);
        	}
    	}
    	else{
    		/*get the subexpression identifier for the chosen subex*/
    		inp = setSubexIdVars(op,inp);

    		if(subexId == null){
                if( Logger.LoggingOn )
                {
                    Logger.log("SolverTutorProblem.calculateScratchpadStep: error getting subex '" + inp + "' from problem '" + this + "'");
                }
                nextStep = makeIdenticalNextStep();
            	if (result != null) {
            		result.setResult(ExampleTracerTracer.NULL_MODEL);
            		result.makeTutorSAI(null, SolverTutor.getOpcode(op), inp);
            	}
    		}
    		else{
    			if( Logger.LoggingOn )
    			{
    			    Logger.log("solverdebug","SolverTutorProblem.calculateScratchpadStep: creating scratchpad for subexpression: " + inp);
    			}
                String methodName = op.getActionMethodName( subexIdSide );  // will this work for qft???
                String paramTmpl = op.getActionParamTmpl( subexIdSide );

                if( methodName == null || paramTmpl == null )
                {
                    Logger.log( "solverdebug", "SolverTutorProblem.calculateScratchpadStep: could not find methodName or paramTmpl for action: " + op );
                    nextStep = makeIdenticalNextStep();
                	if (result != null) {
                		result.setResult(ExampleTracerTracer.NULL_MODEL);
                		result.makeTutorSAI(null, SolverTutor.getOpcode(op), inp);
                	}
                }
                else{
                	boolean madeScratchpad = false;
                	nextStep = null;
                	if(getParentTutor().getUseTypein() ||
                            //always show these scratchpads (bug 6240)
                			op == SolverOperation.QFT ||
							op == SolverOperation.FQ){
                        try{
                            makeScratchpadForStep( op, inp, result );
                            madeScratchpad = true;
                        }
                        catch(Throwable t){
                            Logger.log("\nSolverFrame.doSubExAction: : error "+
									   t.getMessage()+"  creating scratchpad; will attempt to calculate result without scratchpad\n");
                            //Logger.log(t);  // don't make it too noisy
                        }
                	}

                	if(!madeScratchpad){
                		// Use reflection to perform method of SymbolManiplulator - result will be simplified
                		String newSubEx = invokeSMMethod( methodName, paramTmpl, getParentTutor().getSM(), this, inp, inp, false );

                		// Use reflection to perform method of SymbolManiplulator - result will NOT be simplified
                		String newSubExUnsimp = invokeSMMethod( methodName, paramTmpl, getParentTutor().getSMUnsimp(), this, inp, inp, false );

                		Logger.log( "solverdebug", "SolverTutorProblem.calculateScratchpadStep: method '" + methodName + "' yields new subex: " + newSubEx );

                		if( newSubEx == null || newSubExUnsimp == null )
                		{
                			Logger.log( "solverdebug", "SolverTutorProblem.calculateScratchpadStep: could not calculate new side for action: " + op );
                			nextStep = makeIdenticalNextStep();
                	    	if (result != null) {
                	    		result.setResult(ExampleTracerTracer.NULL_MODEL);
                        		result.makeTutorSAI(null, SolverTutor.getOpcode(op), inp);
                	    	}
                		}
                		else{
                			nextStep = finishScratchpad( op, newSubEx, newSubExUnsimp );
                		}
                	}
                }
    		}
    	}

    	if(nextStep != null){
    		nextStep.setOperation(op);
    		nextStep.setInput(inp);
//	    	if (result != null)
//	    		result.addIaMessage(getEquation() != null ? "nextEquation" : "nextExpression",
//	    				getStringValue(), null);
    	}

    	return nextStep;
    }

    /**
	 * Helper method to send request to the TRESolverTutor to create a
	 * scratchpad tutor.
	 */
    private void makeScratchpadForStep(SolverOperation op,String inp,ExampleTracerEvent result) throws Exception{
    	if(op == SolverOperation.QFT){
            /*hack alert: we need to send the whole equation in this
              case, so that it can be displayed in the scratchpad.  We
              know that the missing side of the eqn will be '0', since
              that's the only case in which qft is applicable.*/
            if(getEquation().getLeft().toString().equals("0")){
                inp = "0=" + inp;
            }
            else{
                inp = inp + "=0";
            }
        }

    	
	    try {
	    	if (result == null)
	    		getParentTutor().getTREParent().startScratchpad(op,inp,subexId.isTerminal(),subexIdSide);
	    	else {
	    		List<String> promptInput = new ArrayList<String>();
	    		String promptText = "";
	    		if (subexIdSide == SolverTutor.LEFTSIDE) {
	    			promptInput.add(SolverTutor.INPUT_BOX);
	    			promptInput.add(getEquation().getRight().toString());
	    			promptText = "Enter left-hand side";
	    		} else {
	    			promptInput.add(getEquation().getLeft().toString());
	    			promptInput.add(SolverTutor.INPUT_BOX);
	    			promptText = "Enter right-hand side";
	    		}
				result.addIaMessage("promptTypein", promptInput, promptText);
	    	}
	    }
	    catch( NullPointerException e )
	    {
		    // ignore missing TREParent in testsolver mode (but need to throw this, for testable tutor side)...   HACK ALERT !!!
		    if( System.getProperty( "testsolver" ) == null )
			    throw e;
	    }
    }

    /**
	 * Callback method after a scratchpad operation has been completed. This can
	 * be called in notypein mode by the tutor after it has calculated the
	 * result itself, or in typein mode after the user has successfully
	 * completed the scratchpad. Creates a new problem based on this problem,
	 * substituting the given expression for the expression being worked on in a
	 * scratchpad.
	 */
    protected SolverTutorProblem finishScratchpad(SolverOperation op, String newExpr, String newExprUnsimp){
    	if (trace.getDebugCode("solverdebug"))
    		trace.printStack("solverdebug","SolverTutorProblem.finishScratchpad(" + op + "," + newExpr + "," + newExprUnsimp + ")");
        
        SolverTutorProblem nextStep = null;
        boolean nextStepContainsPlusMinus = false;

        /*set up initial variables to calculate new problem based on new subexpression*/
        String currentLeft, currentRight = null;
        String newLeft, newRight = null;
        String newLeftUnsimp, newRightUnsimp = null;

        if(isSolveEquation()){
            currentLeft = getEquation().getLeft().toString();
            currentRight = getEquation().getRight().toString();
            newLeftUnsimp = newLeft = currentLeft;
            newRightUnsimp = newRight = currentRight;
        }
        else{
            currentLeft = getExpression().toString();
            newLeftUnsimp = newLeft = currentLeft;
        }

        /*call out to SM to substitute in new subexpression*/
        try{
        	SymbolManipulator sm = getParentTutor().getSM();
            if(subexIdSide == LEFTSIDE){
                newLeft = sm.substituteNoAuto(currentLeft,subexId,newExpr);
                newLeftUnsimp = sm.substituteNoAuto(currentLeft,subexId,newExprUnsimp);
                // bug 6714 :  sometimes have extra parens if original subex was fenced.  remove them 
                newLeftUnsimp=sm.removeExtraParens( newLeftUnsimp );
                newLeft=sm.removeExtraParens( newLeft );

                /*the 'apply quad form' case is a bit odd -- treated
                  in most places as an operation on just one side, but
                  here we have to handle the other side, too*/
                if(op == SolverOperation.QFT){
                    newRight = sm.runScript("item 1 of variables",currentLeft);
                    newRightUnsimp = newRight;
                    nextStepContainsPlusMinus = true;
                }
            }
            else{
                /*should never happen in the expression simplification
                  case, so don't need to check for null*/
                newRight = sm.substituteNoAuto(currentRight,subexId,newExpr);
                newRightUnsimp = sm.substituteNoAuto(currentRight,subexId,newExprUnsimp);
                // bug 6714 :  sometimes have extra parens if original subex was fenced.  remove them
                newRightUnsimp=sm.removeExtraParens( newRightUnsimp );
                newRight=sm.removeExtraParens( newRight );
                /*the 'apply quad form' case is a bit odd -- treated
                  in most places as an operation on just one side, but
                  here we have to handle the other side, too*/
                if(op == SolverOperation.QFT){
                    newLeft = sm.runScript("item 1 of variables",currentRight);
                    newLeftUnsimp = newLeft;
                    nextStepContainsPlusMinus = true;
                }
            }

            if(isSolveEquation()){
            	nextStep = makeNextStep(newLeft + "=" + newRight,newLeftUnsimp + "=" + newRightUnsimp);
            }
            else{
            	nextStep = makeNextStep(newLeft,newLeftUnsimp);
            }

            nextStep.setContainsPlusMinus(nextStepContainsPlusMinus);
        }
        catch(BadExpressionError bee){
            Logger.log(bee);
        }
        catch(NoSuchFieldException nsfe){
            Logger.log(nsfe);
        }

        return nextStep;
    }

    /**
	 * Sets fields that track the subexpression identified by <code>inp</code>.
	 * May update the input in special circumstances.
	 * 
	 * @return the new input
	 */
    private String setSubexIdVars(SolverOperation op,String inp){
    	SymbolManipulator sm = getParentTutor().getSM();
    	try{
    		subexId = sm.getSubexId(getLeftOrExpression(),inp);
    		subexIdSide = LEFTSIDE;
    		if(subexId != null &&
    				getParentTutor().getCltPmWholeSide() &&
					(op == SolverOperation.MT ||
							op == SolverOperation.CLT)){
    			/*special handling for clt & pm when they should
    			 always apply to the whole side of the equation (bug
    			 6607) -- reset the subexid to point to the whole
    			 side*/
    			subexId = sm.getSubexId(getLeftOrExpression(),getLeftOrExpression());
    			inp = getLeftOrExpression();
    		}
    		else if(subexId != null &&
    				op.getActionType() == SolverActionType.AT_SIMP_EXPR_SUBEX &&
					(!getParentTutor().getUseTypein() ||
							  getParentTutor().getInTestMode() ) )
			{
								  /*special handling for factor: the subexpression is *not*
    			  the input in notypein mode -- it's the whole side*/
    			subexId = sm.getSubexId(getLeftOrExpression(),getLeftOrExpression());
    		}
    		if(subexId == null){
    			/*should only happen if we're an equation, so we
    			 shouldn't need to actively check for that case, but just in case ...*/
    			if(isSolveEquation()){
    				subexId = sm.getSubexId(getEquation().getRight().toString(),inp);
    				subexIdSide = RIGHTSIDE;
    				if(subexId != null &&
    						getParentTutor().getCltPmWholeSide() &&
							(op == SolverOperation.MT ||
									op == SolverOperation.CLT)){
    					/*again, special whole-side handling*/
    					String rhs = getEquation().getRight().toString();
    					subexId = sm.getSubexId(rhs,rhs);
    					inp = rhs;
    				}
    				else if(subexId != null &&
    						op.getActionType() == SolverActionType.AT_SIMP_EXPR_SUBEX &&
							(!getParentTutor().getUseTypein() ||
							  getParentTutor().getInTestMode()))
					{
    	    			/*again, special handling for factor*/
    					String rhs = getEquation().getRight().toString();
    					subexId = sm.getSubexId(rhs,rhs);
    				}
    			}
    		}
    	}
    	catch(BadExpressionError bee){
    		Logger.log(bee);
    	}

    	return inp;
    }

    /**
	 * Calculates the result of applying the given operation (with the given
	 * input, if applicable) to the current problem, and returns a new problem
	 * representing that result. Operations that produce multiple problems will
	 * return an array containing each of the new problems produced.
	 */
    private SolverTutorProblem[] calculateImmediateSteps(SolverOperation op,String inp){
    	
    	/*call out to helper method to handle steps that result in multiple new steps*/
    	if(op.getActionType() == SolverActionType.AT_SPLIT_IMPL){
    		return calculateSplitSteps(op,inp);
    	}

        /*set up sides vars depending on whether the RHS is defined*/
        String[] newSides;
        String[] newUnsimpSides;
        int[] sides;
        if(isSimpExpression()){
            newSides = new String[] {ex.toString()};
            newUnsimpSides = new String[] {ex.toString()};
            sides = new int[] {LEFTSIDE};
        }
        else{
            newSides = new String[] {eq.getLeft().toString(),eq.getRight().toString()};
            newUnsimpSides = new String[] {eq.getLeft().toString(),eq.getRight().toString()};
            sides = getApplicableSides(op,inp);
        }

        /*loop over available/applicable sides*/
        boolean swapsides = false;
        boolean calculatedNewSides = false;
        for(int iSide = 0;iSide < sides.length;iSide++){
            String methodName = op.getActionMethodName(sides[iSide]);
            String paramTmpl = op.getActionParamTmpl(sides[iSide]);

            if(methodName == null || paramTmpl == null){
            	/*try identity*/
            	String newSide = applyIdentityToSide(op,getParentTutor().getSM(),newSides[sides[iSide]]);
            	if(newSide != null){
            		/*identity worked -- go with it for unsimp side, too*/
            		newSides[sides[iSide]] = newSide;
            		newUnsimpSides[sides[iSide]] = applyIdentityToSide(op,getParentTutor().getSMUnsimp(),
            				newUnsimpSides[sides[iSide]]);

            		if (Logger.LoggingOn) {
						Logger.log("solverdebug","SolverTutorProblem.calculateImmediateSteps: identity for op '" + op + " yields new side: " + newSides[sides[iSide]]);
					}

            		/*only have to get one side via identity for it to count as a success*/
            		calculatedNewSides = true;
            	}
            }
            else{
                /**
                 * Since we have access to the <code>op</code>, use it along with the input check for 
                 * an empty String to prevent an operation that will result in "{orig Expression} {op} BadParse".
                 * Three checks:  Empty input, it's a Transformation action, and we're working with an Expression.
                 */
                if ((inp==null || inp.equals("")) && op.getActionType().equals(SolverActionType.AT_TRNS_EXPR) && isSimpExpression()) {
                    /** since the <code>newSides[]</code> array is already assigned the original, just set the boolean and let the normal processing occur within the loop */
            		calculatedNewSides = true;	// we can by-pass the additional invokeSMMethod() below by setting this boolean and continuing out of the remainder of this loop
            		continue;	// we know this is the only side, but let the loop finish normally
                }
            	/*pass in existing side as subex*/
            	String oldSide = newSides[sides[iSide]];
            	newSides[sides[iSide]] = invokeSMMethod(methodName, paramTmpl, getParentTutor().getSM(),
            			this, newSides[sides[iSide]], inp, swapsides);

            	/*check for and handle side-ambiguity*/
            	if(newSides[sides[iSide]] == null && //standard invocation didn't work
            			iSide == 0 && //this is the first side
						sides.length > 1 && //we have a second side to swap with
						op.getSidesAmbiguous()){ //the operation is side-ambiguous
            		Logger.log("solverdebug",
            		"SolverTutorProblem.calculateImmediateSteps: side-ambiguous op failed with standard invocation; attempting to swap side args");
            		newSides[sides[iSide]] = invokeSMMethod(methodName, paramTmpl, getParentTutor().getSM(),
            				this, newSides[sides[iSide+1]], inp, true);
            		
            		/*check if it worked*/
            		if(newSides[sides[iSide]] != null){
            			swapsides = true;
            			newSides[sides[iSide+1]] = oldSide;
            			String tmpSide = newUnsimpSides[sides[iSide]];
            			newUnsimpSides[sides[iSide]] = newUnsimpSides[sides[iSide+1]];
            			newUnsimpSides[sides[iSide+1]] = tmpSide;
            		}
            	}

            	/*pass in existing unsimp side as subex*/
            	newUnsimpSides[sides[iSide]] = invokeSMMethod(methodName,
            			paramTmpl, getParentTutor().getSMUnsimp(), this, newUnsimpSides[sides[iSide]], inp, swapsides);

            	Logger.log("solverdebug","SolverTutorProblem.calculateImmediateStep: method '" + methodName + "' yields new side: " + newSides[sides[iSide]]);

            	if(newSides[sides[iSide]] == null || newUnsimpSides[sides[iSide]] == null){
            		Logger.log("solverdebug","SolverTutorProblem.calculateImmediateStep: could not calculate new side for action: " + op);
            		//calculatedNewSides = false;
            		return null;
            	}
            	else{
            		calculatedNewSides = true;
            	}
            }
        }

        if(!calculatedNewSides){
    		/*identity didn't work -- on either side*/
    		Logger.log("solverdebug","SolverTutorProblem.calculateImmediateStep: could not find methodName, paramTmpl, or applicable identityNames for action: " + op);
    		return null;
    	}

        SolverTutorProblem nextStep;
        if(isSimpExpression()){
        	nextStep = makeNextStep(newSides[LEFTSIDE],newUnsimpSides[LEFTSIDE]);
        }
        else{
        	if(swapsides){
        		nextStep = makeNextStep(newSides[RIGHTSIDE] + "=" + newSides[LEFTSIDE],
        				newUnsimpSides[RIGHTSIDE] + "=" + newUnsimpSides[LEFTSIDE]);
        	}
        	else{
        		nextStep = makeNextStep(newSides[LEFTSIDE] + "=" + newSides[RIGHTSIDE],
        				newUnsimpSides[LEFTSIDE] + "=" + newUnsimpSides[RIGHTSIDE]);
        	}
        }

		if(nextStep != null){
			nextStep.setOperation(op);
			nextStep.setInput(inp);
		}

		return new SolverTutorProblem[] {nextStep};
    }
    /**
     * Logic specific to the B2A Solver typein processing.  
     * @param op SolverOperation
     * @param inputString String
     * @return SolverTutorProblem[]
     */
    private SolverTutorProblem[] calculateImmediateB2ASteps(SolverOperation op, String inputString) {
        SolverTutorProblem nextStep = null;
        /** bug 11246: the parameter for these two methods needs to represent the LHS of the problem */
        String methodName = op.getActionMethodName(0);
        String paramTmpl = op.getActionParamTmpl(0);
    	String newSide = invokeSMMethod(methodName, paramTmpl, getParentTutor().getSM(), this, getStringValue(), inputString, false);
    	//  whoohoo!!! if all works well, newSide == 15 36/27
    	if (newSide!=null) {
    		nextStep = makeNextStep(newSide);
    	} else {
    		if (Logger.LoggingOn) {
				Logger.log("solverdebug", "SolverTutorProblem.calculateImmediateB2ASteps.op, inputString :: ***ERROR: Cannot calculate a newSide for expression ["+ getStringValue() + "] input ["+ inputString + "] op ["+ op + "]");
			}
    	}
    	// STP 2-a is alive!!!
    	if(nextStep != null){
			nextStep.setOperation(op);
			nextStep.setInput(inputString);

			if (getParentTutor().getGoal().equals(SolverGoal.SIMPLIFY_ADDSUB_MIXED_EXPRESSION)) {
				// we're looking for the number of terms
				nextStep.setTypeinTargetTerms(newSide);
			} else if (getParentTutor().getGoal().equals(SolverGoal.SIMPLIFY_MULTDIV_MIXED_EXPRESSION)) {
				// we're looking for the number of factors
				nextStep.setTypeinTargetFactors(newSide);
			}

		}

		return new SolverTutorProblem[] {nextStep};
    	
    }
    /**
     * Find the number of factors in the target expression
     * and set the typein target objects for future comparison.
     * (used for Term and Ratio Expressions)
     * @param exprInput the resultant expression from the user input and solver-op
     * @return int number of factors contained in the expression
     */
    private void setTypeinTargetFactors(String exprInput) {
//    	int resultCount = 0;
    	try {
    		/** term and ratio behave a bit differently with respect to factors:
    		 * if we have only 1 factor, check to see if the expression is a ratio */
    		targetCount = Integer.parseInt(getParentTutor().getSM().runScript("length of factors", exprInput));
            // no sense continuing if we don't have any factors
            if (targetCount > 0) {
            	boolean isExprRatio = new Boolean(getParentTutor().getSM().runScript("isRatio", exprInput)).booleanValue();
            	/** now split based upon this new-found info */
            	if (isExprRatio) {
            		// term 1 == numerator, term 2 == denominator
            		targetCount = 2;
                	termOneTarget = getParentTutor().getSM().runScript("numerator", exprInput);
                	termTwoTarget = getParentTutor().getSM().runScript("denominator", exprInput);
            	}
            	else {
            		// we can have 1 or 2 factors within a term
                	// set the termOne target object
                	termOneTarget = getParentTutor().getSM().runScript("item 1 of factors", exprInput);
                	if (targetCount > 1) {
                    	termTwoTarget = getParentTutor().getSM().runScript("item 2 of factors", exprInput);
                	}
            	}
            	if (Logger.isLoggingOn()) {
					Logger.log("solverdebug", "SolverTutorProblem.setTypeinTargetFactors.exprInput :: termOneTarget==[" + termOneTarget + "] termTwoTarget==[" + termTwoTarget + "]");
				}
            }
    	}
    	catch (NoSuchFieldException nsfe) {
			Logger.log("SolverTutorProblem.setTypeinTargetFactors.exprInput :: NoSuchFieldException caught :: exprInput==["+ exprInput + "]["+ nsfe.getMessage() + "]");
    	}
    	catch (BadExpressionError bee) {
    		Logger.log("SolverToolProblem.getNumberOfFactors.exprInput : BadExpressionError caught :: exprInput==["+ exprInput + "]["+ bee.getMessage() + "]");
    	}
    	
//    	return resultCount;
    }
    /**
     * Find the number of terms in the target expression
     * and set the typein target objects for future comparison.
     * (used for Poly Expressions)
     * @param exprInput the resultant expression from the user input and solver-op
     * @return int number of factors contained in the expression
     */
    private void setTypeinTargetTerms(String exprInput) {
//    	int resultCount = 0;
    	try {
            targetCount = Integer.parseInt(getParentTutor().getSM().runScript("length of terms", exprInput));
            // no sense continuing if we don't have any factors
            if (targetCount > 0) {
            	// set the termOne target object
            	termOneTarget = getParentTutor().getSM().runScript("item 1 of terms", exprInput);
            	if (targetCount > 1) {
                	termTwoTarget = getParentTutor().getSM().runScript("item 2 of terms", exprInput);
            	}
            	if (Logger.isLoggingOn()) {
					Logger.log("solverdebug", "SolverTutorProblem.setTypeinTargetTerms.exprInput :: termOneTarget==[" + termOneTarget + "] termTwoTarget==[" + termTwoTarget + "]");
				}
            }
    	}
    	catch (NoSuchFieldException nsfe) {
			Logger.log("SolverTutorProblem.setTypeinTargetTerms.exprInput :: NoSuchFieldException caught :: exprInput==["+ exprInput + "]["+ nsfe.getMessage() + "]");
    	}
    	catch (BadExpressionError bee) {
    		Logger.log("SolverToolProblem.getNumberOfTerms.exprInput : BadExpressionError caught :: exprInput==["+ exprInput + "]["+ bee.getMessage() + "]");
    	}
    	
//    	return resultCount;
    }

    /**
	 * Helper method that returns an array of 1 or two ints identifying either
	 * the right, left, or both sides as applicable for the given operation and
	 * input.
	 */
    private int[] getApplicableSides(SolverOperation op,String inp){
        int[] ret = new int[] {LEFTSIDE,RIGHTSIDE};

        if(op.getActionType() == SolverActionType.AT_SIMP_SIDE){
            if(inp.equalsIgnoreCase(LEFT_STR)){
                ret = new int[] {LEFTSIDE};
            }
            else if(inp.equalsIgnoreCase(RIGHT_STR)){
                ret = new int[] {RIGHTSIDE};
            }
        }

        return ret;
    }

    /**
	 * Applies the function with the given name and parameter template via the
	 * given <code>SymbolManipulator</code>, filling in arguments based on
	 * the current equation and command info passed in.
	 */
    private static String invokeSMMethod(String methodName, String paramTmpl,
			SymbolManipulator localSM, SolverTutorProblem problem, String subex, String msg, boolean swapsides) {
		List[] argLists = getArgLists(paramTmpl, problem, subex, msg, swapsides);

		try {
			Method m = SymbolManipulator.class.getMethod(methodName,
					(Class[]) argLists[0].toArray(new Class[] {}));

			if (Logger.LoggingOn) {
				Logger.log("solverdebug", "SolverTutorProblem.invokeSMMethod: invoking method: " + m);
				Logger.log("solverdebug", "SolverTutorProblem.invokeSMMethod: with arguments: " + argLists[1]);
			}

			return (String)m.invoke(localSM, argLists[1].toArray());
		}
		catch (NoSuchMethodException nsme) {
			Logger.log("solverdebug", "SolverTutorProblem.invokeSMMethod: " + nsme);
			return null;
		}
		catch (IllegalAccessException iae) {
			Logger.log("solverdebug", "SolverTutorProblem.invokeSMMethod: " + iae);
			return null;
		}
		catch (InvocationTargetException ite) {
			Logger.log("solverdebug", "SolverTutorProblem.invokeSMMethod: " + ite);
			Logger.log("solverdebug", "SolverTutorProblem.invokeSMMethod: cause: " + ite.getCause());
			return null;
		}
	}

    /**
	 * Given a parameter template and information about the current equation and
	 * command, returns two <code>List</code>s: the first is a list of
	 * <code>Class</code> objects that descibe the types of the parameters in
	 * the template; the second is a list of the actual arguments themselves.
	 */
	private static List[] getArgLists(String paramTmpl,
			SolverTutorProblem problem, String subex, String msg, boolean swapsides) {
		List argList = new ArrayList();
		List argTypeList = new ArrayList();
		StringTokenizer argToks = new StringTokenizer(paramTmpl,
				SolverOperation.ARG_SEP);
		while (argToks.hasMoreTokens()) {
			String arg = argToks.nextToken();
			if ((!swapsides && arg.equals(SolverOperation.LHS_TOKEN)) ||
					(swapsides && arg.equals(SolverOperation.RHS_TOKEN))) {
				argList.add(problem.getLeftOrExpression());
				argTypeList.add(String.class);
			}
			else if ((!swapsides && arg.equals(SolverOperation.RHS_TOKEN)) ||
					(swapsides && arg.equals(SolverOperation.LHS_TOKEN))){
				if(problem.isSolveEquation()){
					argList.add(problem.getEquation().getRight().toString());
					argTypeList.add(String.class);
				}
				else{
					 if( Logger.LoggingOn )  Logger.log("SolverTutorProblem.getArgLists: ERROR: Can't get right side of expression problem: " + problem);
				}
			}
			else if (arg.equals(SolverOperation.MSG_TOKEN)) {
				argList.add(msg);
				argTypeList.add(String.class);
			}
			else if (arg.equals(SolverOperation.SUBEX_TOKEN)) {
				argList.add(subex);
				argTypeList.add(String.class);
			}
			else if (arg.equals(SolverOperation.TRUE_TOKEN)) {
				argList.add(Boolean.TRUE);
				argTypeList.add(Boolean.TYPE);
			}
			else if (arg.equals(SolverOperation.FALSE_TOKEN)) {
				argList.add(Boolean.FALSE);
				argTypeList.add(Boolean.TYPE);
			}
			else {
				argList.add(arg);
				argTypeList.add(String.class);
			}
		}

		return new List[] { argTypeList, argList };
	}

	/**
	 * Helper method to calculate the result of an operation using its
	 * associated identity. Returns the result, or <code>null</code> if the id
	 * could not be applied to the given side.
	 */
	private String applyIdentityToSide(SolverOperation op,SymbolManipulator sm,String side){
		String[] idNames = op.getIdentityNames();
		if(idNames == null){
			return null;
		}

		String result = tryApplyIdentities(idNames,side,false,sm);

		if(result == null && op.getAllowReverseIdentities()){
			result = tryApplyIdentities(idNames,side,true,sm);
		}

		return result;
	}

	/**
	 * Helper method to try to apply the given list of identities to the given
	 * expression (possibly reversing the id).
	 */
	private String tryApplyIdentities(String[] ids,String expr,boolean reverse,SymbolManipulator sm){
		for(int i=0;i<ids.length;i++){
			try{
				if(sm.canApplyIdentity(expr,ids[i],reverse)){
					return sm.applyIdentity(expr,ids[i],reverse);
				}
			}
			catch(BadExpressionError bee){
				;//ignore; try next id and return null if all fail
			}
		}

		return null;
	}
	/**
	 * Calculates the results of an operation that yields multiple new problems,
	 * and returns an array of all new problems. Assumes that the tutor has
	 * approved the action, and thus that the problem is in an appropriate form
	 * for the given operation.
	 */
	private SolverTutorProblem[] calculateSplitSteps(SolverOperation op,String input){
		if(isSimpExpression()){
			if( Logger.LoggingOn )
			{
			    Logger.log("SolverTutorProblem.calculateSplitSteps: split op not applicable to expr problem: " + op);
			}
			return new SolverTutorProblem[] {makeIdenticalNextStep()};
		}

		try{
			if(op == SolverOperation.SPLIT){
				/*determine side with multiple factors*/
				boolean factorsOnLeft = !getEquation().getLeft().getProperty("isNumber").getBooleanValue();

				Queryable[] factors;
				String[] factorStrs;

				/*generate array of factors from appropriate side*/
				if(factorsOnLeft){
					factors = getEquation().getLeft().getProperty("factors").getArrayValue();
				}
				else{
					factors = getEquation().getRight().getProperty("factors").getArrayValue();
				}

				/*copy into string array*/
				factorStrs = new String[factors.length];
				for(int i=0;i<factors.length;i++){
					factorStrs[i] = factors[i].getStringValue();
				}

				/*create a new problem for each factor that contains a variable*/
				List splitSteps = new ArrayList();
				for(int i=0;i<factors.length;i++){
					/*check that factor contains a variable*/
					if(factors[i].evalQuery("length of variables").getNumberValue().intValue() > 0){
						SolverTutorProblem newProb;
						/*maintain same sides as in original equation*/
						if(factorsOnLeft){
							newProb = makeNextStep(factorStrs[i] + "=0");
						}
						else{
							newProb = makeNextStep("0=" + factorStrs[i]);
						}

						newProb.setOperation(op);
						newProb.setInput(input);

						splitSteps.add(newProb);
					}
					/*mmmBUG TODO else if(typein){
					 stash non-variable factor somehow so it can optionally
					 be included in any of the split steps
					 }*/
				}

				return (SolverTutorProblem[])splitSteps.toArray(new SolverTutorProblem []{});
			}
			else if(op == SolverOperation.SEPPM){
				/*determine side with plusminus*/
				boolean plusMinusOnLeft = getEquation().getLeft().getProperty("isRatio").getBooleanValue();

				String plusMinusExpr;
				if(plusMinusOnLeft){
					plusMinusExpr = getEquation().getLeft().toString();
				}
				else{
					plusMinusExpr = getEquation().getRight().toString();
				}

				if( Logger.LoggingOn )
				{
				    Logger.log("SolverTutorProblem.calculateSplitSteps: plusMinusExpr: "
										+ plusMinusExpr);
				}

				/*call out to SM to calculate plus an minus cases*/
				SymbolManipulator sm = getParentTutor().getSM();
				String plusCase = sm.getPlusCase(plusMinusExpr);
				String minusCase = sm.getMinusCase(plusMinusExpr);

				if( Logger.LoggingOn )
				{
				    Logger.log("SolverTutorProblem.calculateSplitSteps: plusCase: "
										+ plusCase);
								Logger.log("SolverTutorProblem.calculateSplitSteps: minusCase: "
										+ minusCase);
				}
				
				/*create new equation strings based on two cases*/
				String[] newEqns = new String[2];
				if(plusMinusOnLeft){
					newEqns[0] = plusCase + "=0";
					newEqns[1] = minusCase + "=0";
				}
				else{
					newEqns[0] = "0=" + plusCase;
					newEqns[1] = "0=" + minusCase;
				}

				/*create new problems from new equation strings*/
				SolverTutorProblem[] newProblems = new SolverTutorProblem[2];
				for(int i=0;i<newEqns.length;i++){
					newProblems[i] = makeNextStep(newEqns[i]);
					newProblems[i].setOperation(op);
					newProblems[i].setInput(input);
				}

				return newProblems;
			}
		}
		catch(NoSuchFieldException nsfe){
			Logger.log(nsfe);
		}
		catch(NullPointerException npe){
			Logger.log(npe);
		}
		catch(BadExpressionError bee){
			Logger.log(bee);
		}

		if( Logger.LoggingOn )
		{
		    Logger.log("SolverTutorProblem.calculateSplitSteps: unhandled op: " + op);
		}
		return new SolverTutorProblem[] {makeIdenticalNextStep()};
	}

    /**
	 * If this is an equation solving problem, returns the variable for which
	 * we're solving, as a <code>String</code>. Otherwise, returns
	 * <code>null</code>.
	 */
    public String getTargetVar(){
        return targetVar;
    }

    /**
	 * Returns the current typein state of this problem, one of
	 * <code>LEFTNOTSET</code>, <code>RIGHTNOTSET</code>, or
	 * <code>STEPCOMPLETED</code.
	 */
    protected int getState(){
    	if(leftState == TYPEIN_UNSET){
    		return LEFTNOTSET;
    	}
    	else if(isSolveEquation() && rightState == TYPEIN_UNSET){
    		return RIGHTNOTSET;
    	}
    	else if (termOneState==TYPEIN_UNSET) {
    		return TERMONENOTSET;
    	}
    	else if (termTwoState==TYPEIN_UNSET) {
    		return TERMTWONOTSET;
    	}
    	else{
    		return STEPCOMPLETED;
    	}
    }

    /**
	 * Returns <code>true</code> if all typein input for this step has been
	 * completed; <code>false</code> otherwise.
	 */
    public boolean getTypeinCompleted(){
    	return leftState == TYPEIN_SET && (isSimpExpression() || rightState == TYPEIN_SET);
    }
    /**
	 * Returns <code>true</code> if all typein input for this step has been
	 * completed; <code>false</code> otherwise.
	 */
    public boolean getB2ATypeinCompleted(){
    	return termOneState == TYPEIN_SET && termTwoState == TYPEIN_SET;
    }

    /**
	 * Sets the left side of an equation-solving problem to have been completed
	 * in typein mode, with the given input.
	 */
    protected void setLeftFinished(String userLeft){
    	leftState = TYPEIN_SET;
    	eq = initEquation(userLeft + "=" + eq.getRight(),false);
    }

    /**
	 * Sets the right side of an equation-solving problem to have been completed
	 * in typein mode, with the given input.
	 */
    protected void setRightFinished(String userRight){
    	rightState = TYPEIN_SET;
    	eq = initEquation(eq.getLeft() + "=" + userRight,false);
    }

    /**
	 * Sets an expression-simplification problem to have been completed in
	 * typein mode, with the given input.
	 */
    protected void setExprFinished(String userExpr){
    	leftState = TYPEIN_SET;
    	ex = initExpression(userExpr, getParentTutor().getSM());
    }

    /**Initializes this problem's internal typein state.
     * 
     * @param typein If true, this step will have to be entered by the student.*/
    public void setStateForTypein(boolean typein){
    	if(typein){
			leftState = TYPEIN_UNSET;
    		if(isSolveEquation()){
    			rightState = TYPEIN_UNSET;
    		}
    	}
    	else{
    		leftState = rightState = TYPEIN_SET;
    	}
    }
    /**
     * Set (or reset) the term state for B2A units.
     * @param typein
     */
    public void setStateForB2ATypein(boolean typein){
    	if(typein) {
			termOneState = TYPEIN_UNSET;
			if (getTypeinObjectCount()==2) {
				termTwoState = TYPEIN_UNSET;
			}
    	} else {
			termOneState = termTwoState = TYPEIN_SET;
    	}
    }

    /**Returns the operation that yielded this step.*/
    public SolverOperation getOperation(){
    	return operation;
    }

    /**Sets the operation that yielded this step.*/
    private void setOperation(SolverOperation op){
    	operation = op;
    }

    /**Returns the input that yielded this step.*/
    public String getInput(){
    	return input;
    }

    /**Sets the input that yielded this step.*/
    private void setInput(String inp){
    	input = inp;
    }

    /**
	 * Returns the typein status of the named side, one of
	 * <code>TYPEIN_UNSET</code> or <code>TYPEIN_SET</code>.
	 */
    public int getSideStatus(String side){
    	if(side.equalsIgnoreCase(LEFT_STR)){
    		return leftState;
    	}
    	else{
    		return rightState;
    	}
    }

    /**
	 * Retrieves the prompt describing the goal of this problem. Queries the
	 * parent SolverTutorProblem if necessary.
	 */
    public String getPrompt(){
    	if(prompt == null && getMessagingParent() instanceof SolverTutorProblem){
    		prompt = ((SolverTutorProblem)getMessagingParent()).getPrompt();
    	}

    	return prompt;
    }

    /**Re-encapsulates one side of an equation solving problem as an
       expression simplification problem.  Note that
       <code>targetVar</code> will be defined for this SolverProblem,
       even though in other respects in looks like an expression
       simplification problem.  If this is an expression
       simplification problem, this method just does <code>return
       this;</code>.*/
    public SolverTutorProblem getExpressionProblem(int side){
        if(isSimpExpression()){
            return this;
        }

        SolverTutorProblem ret = new SolverTutorProblem();
        ret.targetVar = this.targetVar;

        if(side == LEFTSIDE){
            ret.ex = eq.getLeft();
            ret.unsimpEx = unsimpEq.getLeft();
        }
        else{
            ret.ex = eq.getRight();
            ret.unsimpEx = unsimpEq.getRight();
        }

        return ret;
    }

    public boolean[] getEncapsulateSettings(){
        if(eq != null){
            return new boolean[] {eq.getLeft().getEncapsulateVar(),
                                  eq.getRight().getEncapsulateVar()};
        }
        else{
            return new boolean[] {ex.getEncapsulateVar()};
        }
    }

    public void putEncapsulateSettings(boolean[] encap){
        if(eq != null){
            eq.getLeft().setEncapsulateVar(encap[0]);
            eq.getRight().setEncapsulateVar(encap[1]);
        }
        else{
            ex.setEncapsulateVar(encap[0]);
        }
    }

    public void setEncapsulateVar(boolean encap){
        putEncapsulateSettings(new boolean[] {encap,encap});
    }

    protected void setContainsPlusMinus(boolean pm){
    	containsPlusMinus = pm;
    }

    /**Properties defined at this level:
       <ul>
       <li>target variable
       <li>original equation
       <li>original expression
       <li>original problem
       <li>equation
       <li>expression
       <li>unsimp
       </ul>

       All other properties are delegated to the expression or
       equation object.*/
	public Queryable getProperty(String prop) throws NoSuchFieldException{
        if(prop.equalsIgnoreCase("target variable")){
            return new StringQuery(targetVar);
        }
        else if(prop.equalsIgnoreCase("original equation")){
            return ((SolverTutorProblem)getOriginalProblem()).getProperty("equation");
		}
        else if(prop.equalsIgnoreCase("original expression")){
            return ((SolverTutorProblem)getOriginalProblem()).getProperty("expression");
		}
        else if(prop.equalsIgnoreCase("original problem")){
            return ((SolverTutorProblem)getOriginalProblem()).getProperty("problem");
		}
        else if(prop.equalsIgnoreCase("equation")){
            if(eq != null){
                return eq;
            }
            else{
                throw new NoSuchFieldException("SolverProblem: expression simplification problem does not have property 'equation'");
            }
        }
        else if(prop.equalsIgnoreCase("expression")){
            if(ex != null){
                return ex;
            }
            else{
                throw new NoSuchFieldException("SolverProblem: equation solving problem does not have property 'expression'");
            }
        }
        else if(prop.equalsIgnoreCase("unsimp")){
            if(unsimpEq != null){
                return unsimpEq;
            }
            else{
                return unsimpEx;
            }
        }
        else if(eq != null){
            return eq.getProperty(prop);
        }
        else if(ex != null){
            return ex.getProperty(prop);
        }
        else{
            throw new NoSuchFieldException("ERROR: SolverProblem: both expression and equation are null");
        }
    }

	public void setProperty(String prop,String value) throws NoSuchFieldException{
        if(eq != null){
            eq.setProperty(prop,value);
        }
        else if(ex != null){
            ex.setProperty(prop,value);
        }
        else{
            throw new NoSuchFieldException("ERROR: SolverProblem: both expression and equation are null");
        }
    }

	public Queryable evalQuery(String query) throws NoSuchFieldException{
		return StandardMethods.evalQuery(query,this);
    }


	public Queryable evalQuery(String[] query) throws NoSuchFieldException{
		return StandardMethods.evalQuery(query,this);
    }

	public Queryable applyOp(String op,Vector args)  throws NoSuchFieldException{
        if(eq != null){
            return eq.applyOp(op,args);
        }
        else if(ex != null){
            return ex.applyOp(op,args);
        }
        else{
            throw new NoSuchFieldException("ERROR: SolverProblem: both expression and equation are null");
        }
    }

	public Number getNumberValue(){
        if(eq != null){
            return eq.getNumberValue();
        }
        else if(ex != null){
            return ex.getNumberValue();
        }
        else{
            //mmmBUG umm ... ?
            return new Integer(0);
        }
    }

	public boolean getBooleanValue(){
        if(eq != null){
            return eq.getBooleanValue();
        }
        else if(ex != null){
            return ex.getBooleanValue();
        }
        else{
            //mmmBUG umm ... ?
            return false;
        }
    }

	public String getStringValue(){
        if(eq != null){
            return eq.getStringValue();
        }
        else if(ex != null){
            return ex.getStringValue();
        }
        else{
            //mmmBUG umm ... ?
            return "";
        }
    }

	public Queryable[] getArrayValue(){
        if(eq != null){
            return eq.getArrayValue();
        }
        else if(ex != null){
            return ex.getArrayValue();
        }
        else{
            //mmmBUG umm ... ?
            return new Queryable[] {};
        }
    }

	public void noteValueSet(Object value) {
		Logger.log("SolverTutorProblem.noteValueSet: ERROR: unsupported tutor verb; value: " + value);
	}

	/** Handles hint requests for this problem by passing them to the SolverTutor */
	public void requestHint() {
		SolverTutor tut = getParentTutor();
		tut.checkStudentAction(this,SolverOperation.HINT,null,null);
	}

	/**
	 * Recurses up the messaging containment hierarchy to retrieve the
	 * SolverTutor ancestor of this problem.
	 */
	private SolverTutor getParentTutor(){
		MessagingNode parent = getMessagingParent();
		while(parent != null && !(parent instanceof SolverTutor)){
			parent = parent.getMessagingParent();
		}
		if(parent instanceof SolverTutor){
			return (SolverTutor)parent;
		}
		else{
			return null;
		}
	}

	/**
	 * Returns the previous problem, or this problem if there is no previous
	 * problem.  Never returns <code>null</code>.
	 */
	public SolverTutorProblem getPreviousProblem(){
		MessagingNode parent = getMessagingParent();
		if(parent instanceof SolverTutorProblem){
			return (SolverTutorProblem)parent;
		}
		else{
			return this;
		}
	}

	/**
	 * Returns all siblings of this problem that are in a typein-incomplete
	 * state (i.e. for which <code>getTypeinCompleted()</code> returns false).
	 */
	public SolverTutorProblem[] getTypeinIncompleteSiblings(){
		Set incompleteSiblings = new HashSet();

		SolverTutorProblem stepParent = getPreviousProblem();

		if(stepParent == this){
			/*will only happen if currentProblem == originalProblem,
			 in which case we shouldn't be typing in one of many
			 equation steps anyway -- in other words, this should
			 never happen.*/
			if( Logger.LoggingOn )
			{
			    Logger.log("SolverTutorProblem.getTypeinIncompleteSiblings: ERROR: failed to get STP parent for problem: " + this);
			}
		}
		else{
			Set siblingNames = stepParent.getChildNames();

			/*loop over child names*/
			for (Iterator sibNameIter = siblingNames.iterator(); sibNameIter.hasNext();) {
				/*retrieve named child*/
				String siblingName = (String) sibNameIter.next();
				try{
					SolverTutorProblem sibling = (SolverTutorProblem) stepParent.getChildByName(siblingName);

					/*check typein-completion-state of named child*/
					if(!sibling.getTypeinCompleted()){
						incompleteSiblings.add(sibling);
					}
				}
				catch(ClassCastException cce){
					/*shouldn't happen*/
					Logger.log(cce);
				}
			}
		}

		/*convert set to array and return it*/
		return (SolverTutorProblem[]) incompleteSiblings.toArray(new SolverTutorProblem[]{});
	}

	/**
	 * Handles requests for new problems to be created (by applying a named
	 * operation to this step) by passing them to the SolverTutor
	 */
	public void requestCreate(String objectType, StringMap props) {
		/*objectType should always be the same, so we'll ignore it ...*/
		SolverOperation op = SolverOperation.getOpByCode(props.getProperty(PropertyConstants.OPERATION).toString());
		String createInput = (String)props.getProperty(PropertyConstants.INPUT);
		SolverTutor tut = getParentTutor();
		tut.checkStudentAction(this,op,createInput,null);
	}

	/** Handles requests to undo this step by passing them to the SolverTutor */
	public void requestDelete() {
		getParentTutor().requestDeleteStep(this);
	}

	public void noteCreate(String objectType) {
		Logger.log("SolverTutorProblem.noteCreate: ERROR: unsupported tutor verb; objectType: " + objectType);
	}

	public void noteDelete() {
		Logger.log("SolverTutorProblem.noteDelete: ERROR: unsupported tutor verb");
	}

	/**Utility method to remove a messaging child based on its name*/
	protected void deleteChildByName(String name){
		AddressableNode child = getChildByName(name);
		if(child != null){
			SendMessage.sendDelete(child.getMessagingAddress());
			removeChild(child);
		}
	}

	protected void addTypeinStep(SolverOperation op /*, ExampleTracerEvent result */){
		TypeinStep ts = new TypeinStep(op /*, result */);
		addChild(ts);
	}

	/**
	 * Recursive routine that will traverse the child messaging tree
	 * and return the last node (or leaf) response.  Since factoring quadratics "splits"
	 * a Solver problem into two children, we may have more than one leaf-node
	 * to evaluate.
	 * @return boolean
	 */
	protected boolean foundUnclassifiedChildren() {
	    // check the size of our child list
		int siblingCount = getChildNames().size();
		// if ==0, then this is the leaf...otherwise, process the children individually
		if (siblingCount>0) {
		    boolean haveAnUnclassifiedChild = false;
		    Iterator iter = getChildNames().iterator();
		    while (iter.hasNext() && !haveAnUnclassifiedChild) {	// set to exit on the first child found to have need of classification
		        SolverTutorProblem stp = (SolverTutorProblem)getChildByName((String)iter.next());
		        haveAnUnclassifiedChild = stp.foundUnclassifiedChildren();	// recurse
		    }
		    return haveAnUnclassifiedChild;	// the result of one of more children
		} else {
		    // we're the last child, so send our value
		    return !isClassified();
		}
	}
	/**
	 * Used for determining initial type-in status within the SideRuleTemplate
	 * when generating the first hint message...
	 * @return boolean
	 */
	public boolean isInitialTypeinProblem() {
	    if (getParentTutor()!=null) {
	        return getParentTutor().useInitialTypein();
	    }
	    // if we can't identify the parent (which should not be impossible), then we can't return 'true'
	    return false;
	}
	/**
	 * Returns the number of typein target terms/fators
	 * for this B2A problem.
	 * @return int
	 */
	public int getTypeinObjectCount() {		
		return targetCount;
	}
	/**
	 * Updates the B2A typein objects state
	 * @param termObject int
	 * @param newState int
	 */
	public void setTypeinTermState(int termObject, int newState) {
		switch (termObject) {
		case 1:
			termOneState = newState;
			break;
		case 2:
			termTwoState = newState;
			break;
		default:
			if (Logger.isLoggingOn()) {
				Logger.log("solverdebug", "SolverTutorProblem.setTypeinTermState.termObject, newState :: ***ERROR: invalid termObject specified : ["+ termObject + "], newState : ["+ newState + "]");
			}
		}
	}
	public String getTermOneTargetValue() {
		return termOneTarget;
	}
	public String getTermTwoTargetValue() {
		return termTwoTarget;
	}
	/**
	 * Simple class used to receive messages from the typein text fields in the
	 * interface associated with this step and pass them to the SolverTutor.
	 */
	protected class TypeinStep extends SimpleAddressableNode implements TutoredStep{
		SolverOperation typeinOp;
//		ExampleTracerEvent result;

		public TypeinStep(SolverOperation op /*, ExampleTracerEvent result */){
			super(op.getOpcode());
			typeinOp = op;
//			this.result = result;
		}

		public void noteValueSet(Object value) {
			if(value != null){
				SolverTutor tutor = getParentTutor();
				String typeinInput = value.toString();
				tutor.checkStudentAction(SolverTutorProblem.this,typeinOp,typeinInput,null /* result */);
			}
		}

		public void requestHint() {
			SolverTutor tutor = getParentTutor();
			tutor.checkStudentAction(SolverTutorProblem.this,SolverOperation.HINT,null,null /* result */);
		}
	}
}
