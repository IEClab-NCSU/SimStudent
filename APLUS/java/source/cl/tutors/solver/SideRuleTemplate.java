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

package cl.tutors.solver;

import java.text.MessageFormat;

import cl.common.SolverActionType;
import cl.common.SolverConstants;
import cl.common.SolverKeys;
import cl.common.SolverOperation;
import cl.tutors.tre.Tutor;
import cl.utilities.Logging.Logger;
import cl.utilities.sm.BadExpressionError;
import cl.utilities.sm.NumberExpression;
import cl.utilities.sm.SMParserSettings;
import cl.utilities.sm.SymbolManipulator;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;

/**The SideRuleTemplate is used to generate hint messages for typein
   mode that basically just tell the student what to type.*/

public class SideRuleTemplate implements SolverConstants{
	private String[] messages;
	
	/**
	 * This may (now) refer to a side _or_ a term.  The BtA Fall 2005 release includes
	 * a set of solver problems that include one-or-two typein fields for a given expression.
	 * @param problem
	 * @param currState
     * @param result to avoid getting hints for type-in subexpressions: CTAT only has type-in for whole side
	 */
	public SideRuleTemplate(SolverTutorProblem problem,int currState, ExampleTracerEvent result){
        /*figure out what side we're talking about*/
        String sideName;
        boolean isSide = false;
        switch(currState){
        case RIGHTNOTSET:
			sideName = "right";
			isSide = true;
            break;
        default:
            if( Logger.LoggingOn )
            {
                Logger.log("SideRuleTemplate.SideRuleTemplate: ERROR: currState is not left or right (" + currState + "); defaulting to left");
            }
        case LEFTNOTSET:
            sideName = "left";
            isSide = true;
            break;
        case TERMONENOTSET:
            sideName = TERM_1_STR;
            break;
        case TERMTWONOTSET:
            sideName = TERM_2_STR;
            break;
        }

//        init(problem,sideName,true);
        init(problem, sideName, isSide, result);
    }

    public SideRuleTemplate(SolverTutorProblem problem, ExampleTracerEvent result){
        /*assume we're talking about an expression*/
        init(problem,"self",false, result);
    }

    /**
     * 
     * @param problem
     * @param sideName
     * @param isSide
     * @param result to avoid getting hints for type-in subexpressions: CTAT only has type-in for whole side
     */
    private void init(SolverTutorProblem problem, String sideName, boolean isSide, ExampleTracerEvent result) {
        /*action is the description of the action the student is trying to carry out (e.g. "combine like terms")*/
    	SolverOperation op = problem.getOperation();
    	String input = problem.getInput();
//		String firstHint = op.getTypeinHint(input,sideName);   // firstHint is no longer used, so commenting this out

        String equation = null;
        equation = problem.getPreviousProblem().toString();

        /*capitalize the first letter*/
//		if (firstHint != null)   // firstHint is no longer used, so commenting this out
//			firstHint = (firstHint.substring(0, 1)).toUpperCase()+firstHint.substring(1);   // firstHint is no longer used, so commenting this out

        /*for actions involving logs, we want to make a note that the
          base is implicit*/
        if(op == SolverOperation.LOG || op == SolverOperation.LOGBASE){
            messages = new String[3];
            messages[1] = SolverKeys.getString(SolverKeys.LOG_AUTO_BASE);
        }
        /*for trig identities, remind the student what the identity
          says*/
        else if(op.getActionType() == SolverActionType.AT_SIMP_TRIG){
            messages = new String[3];
            String[] id = SymbolManipulator.getIdByName(op.getOpcode());
            //mmmBUG TODO i18n
            messages[1] = "The " + SymbolManipulator.getLongIdName(op.getOpcode()) +
                " identity says that <expression>" + 
                id[0] + "=" + id[1] + "</expression>.";
        }
        /*for logs with a base other than 10, tell the student how to
          calculate it on a calculator that only does base-10 logs*/
        else if(SymbolManipulator.getDefaultLogBase() != 10 &&
        		op == SolverOperation.EVAL &&
                equation.indexOf("log") != -1){
            messages = new String[3];
            messages[1] = SolverKeys.getString(SolverKeys.LOG_CHANGE_BASE);
        }
        else{
            messages = new String[2];
        }

        /*the first-level hint just tells the student to perform the action he selected.*/
//        messages[0] = op.getTypeinHint(input,isSide ? sideName : null);
        messages[0] = op.getTypeinHint(input, sideName, isSide);

        /*the bottom-out hint just tells the student what to type*/
		String value = "";
		try{
            /*get the expression from the tutor, in case a side-swap
              has occured.*/
			if(problem.isSimpExpression()){
				value = problem.getProperty("unsimp").toString();
			}
			else{
				value = problem.getProperty("unsimp").getProperty(sideName).toString();
			}

            /*and pass it thru a sm no-op to round off to 4 decimal places*/
			if (Logger.LoggingOn) {
				Logger.log("solverdebug", "SideRuleTemplate.init :: Setting local SM instance to use Parser : ["+ Tutor.sm.getParserSettings() + "]");
			}
            SymbolManipulator sm = new SymbolManipulator(Tutor.sm.getParserSettings());
            int oldDP = sm.getPrintDecimalPlaces();
            sm.setPrintDecimalPlaces(NumberExpression.defaultMathMLDecimalPlaces);
            try{
                value = sm.noOp(value);
            }
            catch(BadExpressionError bee){
                Logger.log("SideRuleTemplate.init: error in no-op for rounding: " + bee);
                /*value should remain unchanged here, which is
                  probably the best we can do anyway*/
            }
            /** before we're done with the SM, let's make sure we have 
             * *something* in the first message index...bug 8710 currently leads to 
             * a 'blank' first hint, which really (really) seems odd, but pre-8710
             * appears to be the intended logic path.
             * 
             * TODO NOTE: initially restricting this to problems configured as 
             * useInitialTypein(), but can certainly see this expanding the 
             * hint generation into a general op message.
             * 
             * TODO make this part of the base solver logic to dynamically construct
             * the initial user propmt; "operator of term 2" based upon bug 8710.
             */
            // find the number of terms
            // list each term (unfenced) trailed by a ',' when length of terms > 2; last term has an " and " preceding it
            try {
	            if (input==null && !isSide && problem.isInitialTypeinProblem() ) {
	                int nbrOfTerms = Integer.parseInt(sm.runScript("length of terms", problem.toString()));
	                if (Logger.isLoggingOn()) {
		                Logger.log("solverdebug", "SideRuleTemplate.init :: Length of terms == [" + nbrOfTerms + "]");
	                }
	                StringBuffer sb = new StringBuffer(50);	// this should be large enough for most messages
	                String tempScript = null;
	                sb.append("Combine ");
	                for (int x=1; x<=nbrOfTerms-1; x++) {
	                    tempScript = "[unfence] [term " + x + "]";
	                    sb.append(HTML_EXPRESSION_STARTTAG);
	                    sb.append(sm.runScript(tempScript, problem.toString()));
	                    sb.append(HTML_EXPRESSION_ENDTAG);
	                    sb.append(HTML_SPACE_VALUE);
	                    if (nbrOfTerms>2) {
	                        sb.append(",");
	                    }
	                }
                    tempScript = "[unfence] [term " + nbrOfTerms + "]";
                    sb.append(" and ");
                    sb.append(HTML_EXPRESSION_STARTTAG);
                    sb.append(sm.runScript(tempScript, problem.toString()));
                    sb.append(HTML_EXPRESSION_ENDTAG);
                    sb.append(HTML_SPACE_VALUE);
                    sb.append(". ");
	                if (Logger.isLoggingOn()) {
	                    Logger.log("solverdebug", "SideRuleTemplate.init :: Setting msg[0] to [" + sb.toString() + "]");
	                }
                    messages[0] = sb.toString(); 
	            }            
            } catch (Exception e) {
                Logger.log(e);
            }
            /*this is a global setting, so we have to put it back the
              way it was even though this is just a local instance of
              sm*/
            sm.setPrintDecimalPlaces(oldDP);
		}
		catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			Logger.log(e);
		}

		/** one last possible modification upon the <code>value</code>...if
		 * we are processing the typein for a BtA (add/sub or mult/div) problem
		 * the typein field vlue may be the first _or_ second term.  We need to isolate
		 * the individual term/factor out of the current value result before placing it 
		 * within the hint message.
		 * Example:  Result of RWImprop action is "217/27 + 222/27" and we're
		 * hinting for the first typein term.  The value for the hint message should be 
		 * "217/27" instead of the entire poly. */
		if (op.isTermTypeinOp() && result == null) {  // sewall 2011/02/25: CTAT only has type-in whole side
			try { // modify the value if we are a BtA op AND we have a poly, term, or ratio expression
				boolean isPoly = Tutor.sm.isPolyFormExpression(value); 
				boolean isTerm = Tutor.sm.isTermFormExpression(value); 
				boolean isRatio = Tutor.sm.isNonNumericFractionForm(value); 
				if (Logger.LoggingOn) {
					Logger.log("solverdebug", "SideRuleTemplate.init :: isPoly==[" + isPoly + "]  isTerm==[" + isTerm + "]  isRatio==[" + isRatio + "]");
				}
				if ( isPoly || isTerm || isRatio ) {
					// grab the appropriate expression: polyExprs have terms; termExprs have factors; ratioExprs have a numer and denom
					int termNumber = 0;
					// this is the setup for a script-run
					if (sideName.equals(SolverConstants.TERM_2_STR)) {
						termNumber = 2;
					} else {
						termNumber = 1;
					}
					String newValue = new String(value);
					if (isPoly) {
						newValue = Tutor.sm.runScript("item " + termNumber + " of terms", value);
					} else if (isTerm) {
						newValue = Tutor.sm.runScript("item " + termNumber + " of factors", value);
					} else if (isRatio) {
						if (termNumber==1) {
							newValue = Tutor.sm.runScript("numerator", value);
						} else {
							newValue = Tutor.sm.runScript("denominator", value);
						}
					}
					if (!newValue.equals(value)) {
						if (Logger.LoggingOn) {
							Logger.log("solverdebug", "SideRuleTemplate.init.problem, sideName, isSide :: The hinted value has been modified; orig ["+ value + "] new ["+ newValue + "]");
						}
						value = newValue;
					} else {
						if (Logger.LoggingOn) {
							Logger.log("solverdebug", "SideRuleTemplate.init.problem, sideName, isSide :: Not modifying the hint-value ["+ value + "] for sideName ["+ sideName + "] with op ["+ op + "]");
						}
					}
				}
			}			
			catch (BadExpressionError bee) {
				Logger.log("SideRuleTemplate.init.problem, sideName, isSide :: ***BadExpressionError caught; message==["+ bee.getLocalizedMessage() + "] on expression [" + value + "]");
			}
			catch (NoSuchFieldException nsfe) {
				Logger.log("SideRuleTemplate.init.problem, sideName, isSide :: ***NoSuchFieldException caught; message==["+ nsfe.getLocalizedMessage() + "] on expression [" + value + "]");
			}
		}
//		messages[messages.length-1] = "Type in <expression>"+value+"</expression>";
		String hintTemplate = SolverKeys.getString(SolverKeys.TYPEIN_MESSAGE_TEMPLATE);
		messages[messages.length-1] = MessageFormat.format(hintTemplate, new Object[] { value });
        if(isSide){
//        	messages[messages.length-1] += " on the "+sideName+".";
        	hintTemplate = SolverKeys.getString(SolverKeys.TYPEIN_MESSAGE_SIDE_TEMPLATE);
        	messages[messages.length-1] += MessageFormat.format(hintTemplate, new Object[] { sideName });
        }
	}
	
	public String[] getMessages(){
		return messages;
	}
}
