//version of the equation solving tutor that interacts with the applet interface


package edu.cmu.old_pact.cmu.solver;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.Equation;
import edu.cmu.old_pact.cmu.sm.Expression;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;
import edu.cmu.old_pact.cmu.sm.VariableExpression;
import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.old_pact.cmu.sm.query.StringQuery;
import edu.cmu.old_pact.cmu.solver.ruleset.BugRuleSet;
import edu.cmu.old_pact.cmu.solver.ruleset.EquationHistory;
import edu.cmu.old_pact.cmu.solver.ruleset.Rule;
import edu.cmu.old_pact.cmu.solver.ruleset.RuleDefiner;
import edu.cmu.old_pact.cmu.solver.ruleset.RuleMatchInfo;
import edu.cmu.old_pact.cmu.solver.ruleset.RuleSet;
import edu.cmu.old_pact.cmu.solver.ruleset.SkillRule;
import edu.cmu.old_pact.cmu.solver.ruleset.SkillRuleSet;
import edu.cmu.old_pact.cmu.solver.ruleset.TypeinBugRule;
import edu.cmu.old_pact.cmu.solver.ruleset.TypeinBugRuleSet;
import edu.cmu.old_pact.cmu.solver.ruleset.TypeinSkillRuleSet;
import edu.cmu.old_pact.cmu.tutor.TranslatorProxy;
import edu.cmu.old_pact.cmu.tutor.Tutor;
import edu.cmu.old_pact.cmu.uiwidgets.SolverFrame;
import edu.cmu.old_pact.dormin.DataFormatException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.pact.Utilities.trace;

public class SolverTutor implements Tutor {

	private TranslatorProxy trans;
	private String originalEquation = "";
	private String currentEquation = "";
	private SkillRule[] currentRules=null;
	private Equation currentEquationInfo = null;
	private Equation previousEquationInfo = null;
	private VariableExpression targetVarInfo = null;
	private String targetVar = "";
	private String[] validActions;

	private Equation prevTIeq;
	private String prevTIaction;
	private String prevTIinput;

	private Hashtable ruleUpdates;

	private static int cycle=0;
	private static int updateNoCycle=1;
	private static int giveHint=2;
	private static int cycleTypein=3;
	private static int checkDone=4;
	private static int typeinNotCompleted=5;

	static final int LEFTSIDE = 0;
	static final int RIGHTSIDE = 1;

	public SolverTutor () {
		RuleDefiner.defineRules();
		/*this is cleared after every problem, so it should stay
          pretty small*/
		ruleUpdates = new Hashtable(19);
		if(System.getProperty("solvertrace") != null){
			Rule.setTraceAllRules(true);
		}
		if(SolverFrame.debug()){
			Rule.setDebug(true);
		}
	}

	private RuleSet strategicRules(){
		return RuleDefiner.getStrategicRules(true);
	}

	private SkillRuleSet skillRules(){
		return RuleDefiner.getSkillRules(true);
	}

	private TypeinSkillRuleSet typeinSkillRules(){
		return RuleDefiner.getTypeinSkillRules(true);
	}

	private BugRuleSet bugRules(){
		return RuleDefiner.getStrategicBugRules(true);
	}

	private TypeinBugRuleSet typeinBugRules(){
		return RuleDefiner.getTypeinBugRules(true);
	}

	public void setTranslator(TranslatorProxy translator) {
		trans = translator;
	}
	
	public TranslatorProxy getTranslator() {
		return trans;
	}

        /*this picks the first variable on the left if there is one,
          on the right otherwise.  If there is no variable at all,
          returns null*/
	public static String guessTargetVar(Equation e){
		String var = null;
		Queryable[] q;

		try{
			q = e.getProperty("variable side expression").getProperty("variables").getArrayValue();
			if(q.length > 0){
				var = ((StringQuery)q[0]).getStringValue();
				//trace.out("    using variable from left side: " + var);
			}
		}
		catch(NoSuchFieldException nsfe){
			//just return null
			trace.out("ST: exception guessing target variable: " + nsfe.toString());
		}
		catch(NullPointerException npe){
			//this happens when the first line of the above try
			//statement fails somewhere in the middle (eg if the
			//user enters the 'equation' "1+2"
			trace.out("ST: exception guessing target variable: " + npe.toString());
		}

		return var;
	}

        /*returns true if the variable v appears in the equation e,
          false otherwise*/
        public boolean verifyTargetVar(Equation e,VariableExpression v){
            boolean ret = false;

            /*trace.out("vTV(" + e.getStringValue() + "," +
              v.getStringValue() + ")");*/

            try{
                Expression ls = (Expression)e.getProperty("left");
                Queryable[] vars = (Queryable[])(ls.getProperty("variables").getArrayValue());
                for(int i=0;i<vars.length;i++){
                    //trace.out("vTV: checking variable " + i + " of left side");
                    if(v.getStringValue().equalsIgnoreCase(vars[i].getStringValue())){
                        ret = true;
                        //trace.out("     money.");
                        break;
                    }
                }

                /*if we didn't find it on the left, try the right*/
                if(!ret){
                    Expression rs = (Expression)e.getProperty("right");
                    vars = (Queryable[])(rs.getProperty("variables").getArrayValue());
                    for(int i=0;i<vars.length;i++){
                        //trace.out("vTV: checking variable " + i + " of right side");
                        if(v.getStringValue().equalsIgnoreCase(vars[i].getStringValue())){
                            ret = true;
                            //trace.out("     money.");
                            break;
                        }
                    }
                }
            }
            catch(NoSuchFieldException nsfe){
                //trace.out("vTV: failure because " + nsfe.toString());
            }

            return ret;
        }

	public void showSkills(){
		if(!ruleUpdates.isEmpty()){
			trace.out("SolverTutor: skills updated: ");
			for(Enumeration e = ruleUpdates.keys();e.hasMoreElements();){
				trace.out(e.nextElement().toString());
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
	public void startProblem(String equation) {
            //blow away the old state
            targetVar = "";
            targetVarInfo = null;
            //forget about old variable settings, too
            SymbolManipulator.forgetVarList();
			//and erase all the information about what skills have been updated already
			//showSkills();
			ruleUpdates.clear();

            int semicolonPos = equation.indexOf(';');
            /*does the equation string include a variable to solve for?*/
            if(semicolonPos > 0){
				/*throw out the goal prompt if there is one*/
				if(equation.indexOf(';',semicolonPos+1) != -1){
					equation = equation.substring(0,equation.indexOf(';',semicolonPos+1));
				}

                targetVar = equation.substring(semicolonPos+1);
                /*make sure that the target var actually occurs in the equation*/
                try{
                    /*trace.out("startProblem: new Equation(" +
					  equation.substring(0,semicolonPos) +
					  ") for target verification");*/
                    Equation test = new Equation(equation.substring(0,semicolonPos));
                    //test.getProperty("target variable side " + targetVar);
                    targetVarInfo = new VariableExpression(targetVar);
                    if(!verifyTargetVar(test,targetVarInfo)){
                        trace.out("startProblem: verification error");
                        targetVar = "";
                        targetVarInfo = null;
                    }
                }
                catch(BadExpressionError bee){
                    trace.out("startProblem: verification error: " + bee.toString());
                    targetVar = "";
                    targetVarInfo = null;
                }
            }
            else{
                /*semicolonPos is used later on as the index of the
                  end of the actual equation in the string 'equation',
                  so it can't be -1*/
                semicolonPos = equation.length();
            }

            if(targetVarInfo == null){
                /*the caller didn't specify a variable to solve for,
                  so we'll just take a guess*/
                try{
                    /*trace.out("startProblem: new Equation(" +
					  equation.substring(0,semicolonPos) +
					  ") for target guessing");*/
                    targetVar = guessTargetVar(new Equation(equation.substring(0,semicolonPos)));
                    targetVarInfo = new VariableExpression(targetVar);
                }
                catch(BadExpressionError bee){}
            }

            if(targetVar == null){
                /*we're out of luck here -- no valid variable
                  specified by the user, and no variables in the
                  equation.*/
                /*mmmBUG ... not yet sure what's the correct thing to
                  do here*/
                targetVar = "x";
                targetVarInfo = new VariableExpression(targetVar);
            }

            /*this needs to happen after targetVar has been set,
              because it calls getValidActions()*/
            setCurrentEquation(equation.substring(0,semicolonPos));
			originalEquation = currentEquation;

			if(SolverFrame.debug()){
				trace.out("ST.startProblem(): solving " + currentEquation + " for " + targetVar);
			}
	}

	public void setCurrentEquation(String equation) {
            currentEquation = equation;
            previousEquationInfo = currentEquationInfo;
            currentEquationInfo = null;

            try {
				SymbolManipulator.setVarList(new String[] {targetVar});
                currentEquationInfo = new Equation(equation,new String[] {targetVar});
            }
            catch (BadExpressionError err) {
                //trace.out("Equation "+equation+" does not parse");
                return;
            }

            if(SolverFrame.debug()){
				try{
					Expression ls = (Expression)currentEquationInfo.getProperty("left");
					Expression rs = (Expression)currentEquationInfo.getProperty("right");
					trace.out("ST.sCE: " + ls + " = " + rs);
					trace.out("ST.sCE: " + ls.debugForm() +
									   " = " + rs.debugForm());
					/*trace.out("setCurrentEquation: makeForm(" + currentEquation + ") == " +
					  currentEquationInfo.makeForm(currentEquation).toString());*/
				}
				catch(Exception e){}
			}

            getValidActions();
	}

	// get valid actions dynamically
	private synchronized void getValidActions(){
		try {
			String serialActions = trans.getProperty("Tool","actions");
			ByteArrayInputStream inStream = new ByteArrayInputStream(serialActions.getBytes());
			ObjectInputStream is = new ObjectInputStream(inStream);
			Object rawObj = is.readObject();
			validActions = (String[])rawObj;
		}
		catch (Exception e) {
			trace.out("exception getting actions: "+e);
		}
	}
	
	//getTutorAction tells the tutor what to do in response to the user action
	private int getTutorAction(String action) {
		if (action.equalsIgnoreCase("new") ||
			action.equalsIgnoreCase("erase") ||
			action.equalsIgnoreCase("finalizetypein")) //new and erase are always OK -- don't cycle tutor
			return updateNoCycle;
		else if (action.equalsIgnoreCase("hint"))
			return giveHint;
		else if (action.equalsIgnoreCase("typein") ||
				 action.equalsIgnoreCase("left") ||
				 action.equalsIgnoreCase("right")){
			return cycleTypein;
		}
		else if (action.equalsIgnoreCase("done") ||
				action.equalsIgnoreCase("DoneNoSolution") ||
				action.equalsIgnoreCase("DoneInfiniteSolutions"))
			return checkDone;
		else if (action.equalsIgnoreCase("stepNotCompleted"))
			return typeinNotCompleted;
		else
			return cycle;
	}
	
	public RuleMatchInfo checkStudentAction(String selection,String action,String input) {
		//trace.out("ST.cSA(" + selection + "," + action + "," + input + ")");
		RuleMatchInfo tempRuleMatchInfo = null;
		int toDo = getTutorAction(action);
		if (toDo == cycle) {
			prevTIeq = currentEquationInfo;
			prevTIaction = action;
			prevTIinput = input;
			try {
				tempRuleMatchInfo = cycleTutor(selection, action, input);
			} catch (DorminException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (toDo == updateNoCycle) {
			// do nothing here
		}
		else if (toDo == giveHint) {
			getTutorHint(selection);
		}
		else if (toDo == cycleTypein) {
			cycleTutorTypein(selection,action,input);
		}
		else if (toDo == checkDone) {
			try {
				tempRuleMatchInfo = cycleTutorDone(selection, action, input);
			} catch (DorminException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (toDo == typeinNotCompleted){
		// What to do here?
			trace.out("TypeIn step "+currentEquation+" is not completed!");
		}
		return tempRuleMatchInfo;
	}
	
	public void startNextStep(String action) {
		String nextEquation = null;
		try {
			nextEquation = trans.getProperty("Tool","current equation");
		}
		catch (NoSuchPropertyException err) {
			trace.out("can't get current equation of solver tool");
			nextEquation = "3x+4=5"; //yeech
		}
		int toDo = getTutorAction(action);
		if (toDo == cycle) {
           setCurrentEquation(nextEquation);
		}
		else if (toDo == updateNoCycle) {
           setCurrentEquation(nextEquation);
		}
	}
	
	public void updateSkill(String skillname,String gradient,String currentEquation) throws DorminException{
		if(!ruleUpdates.containsKey(skillname)){
			trans.updateSkill(skillname,gradient,currentEquation);
			//doesn't really matter what value we associate with this key ...
			ruleUpdates.put(skillname,new Object());
		}
		/*else{
		  trace.out("ST.uS: skill " + skillname + " has already been updated");
		  }*/
	}

	//updateSkillsForRule finds all the skills that correspond to the given rule and updates them
	public void updateSkillsForRule(Rule modelRule,boolean up) throws DorminException {
		/*need to do this every time in case typein rules need
          currentRules to be set correctly*/
		currentRules = skillRules().findAllRulesToFire(new EquationHistory(currentEquationInfo,
																		 targetVar,
																		 originalEquation),
													 modelRule.getName());

		String directionCode;
		if (currentRules != null) {
			if(SolverFrame.debug()){
				trace.out("ST.uSFR: found "+currentRules.length+" skills for rule: " + modelRule);
			}
			for (int i=0;i<currentRules.length;++i) {
				SkillRule foundSkill = currentRules[i];
				if (foundSkill.isTraced())
						trace.out("***Found Skill rule "+foundSkill.getName()+" subskill: "+foundSkill.getSubskillName());
				//for some reason, we pass a code of 1 for up and 0 for down
				if (up)
					directionCode = "1";
				else
					directionCode = "0";
				
				updateSkill(foundSkill.getSubskillName(),directionCode, currentEquationInfo.toString());
			}
			setSkillInfo(true);
		}
		else if(SolverFrame.debug()){
			trace.out("ST.uSFR: warning: no skills found for rule: " + modelRule);
		}
	}

	//Olga
	private RuleMatchInfo cycleTutorDone(String selection, String action, String input) throws DorminException {
		boolean isDone = false;
		RuleMatchInfo foundRule = strategicRules().findRuleToFire(new EquationHistory(currentEquationInfo,
																					targetVar,
																					originalEquation),
																action,
																input);
		if (foundRule != null && foundRule.getBoolean() == true) { //found rule, so can find skill to increment
			if (foundRule.getRule().isTraced())
				trace.out("***Found Done rule "+foundRule.getRule().getName());
			updateSkillsForRule(foundRule.getRule(),true);
			
//			SkillRule foundSkill = skillRules.findRuleToFire(currentEquationInfo,(foundRule.getRule()).getName());
//			if (foundSkill != null && (foundSkill.getName().toLowerCase()).indexOf("done") != -1) {
//				if (foundRule.getRule().isTraced())
//					trace.out("***Found Skill rule "+foundSkill.getName()+" subskill: "+foundSkill.getSubskillName());
//				isDone = true;
//				trans.updateSkill(foundSkill.getSubskillName(),"1", currentEquationInfo.toString());
//				trans.suggestNewProblem();
//			}	
			isDone = true;
			if(SolverFrame.debug()){
				trans.displayCompletionMessage();
			}
			//showSkills();
                        //SMILLER removed this because "done should not cause new equation window to pop up
                        //			trans.suggestNewProblem();
		}
		
		if(!isDone) {
			//trace.out("ST.cTD: No Action rule");
			trans.setFlag(selection,true);
			//SkillRule foundSkill = skillRules.findRuleToFire(currentEquationInfo,"Done");
			updateSkillsForRule(strategicRules().getRuleByName("doneleft"),false); //no rule found, decrement done
//			if (foundSkill != null) {
//				trans.updateSkill(foundSkill.getSubskillName(),"0", currentEquationInfo.toString());
//			}
			getValidActions();
			Rule helpRule = strategicRules().findRuleForHelp(new EquationHistory(currentEquationInfo,
																			   targetVar,
																			   originalEquation),
														   validActions);
			Rule bugRule = null;
			if(helpRule != null){
				if(helpRule.isTraced()){

				}
				//currentRule = helpRule.getName();
				updateSkillsForRule(helpRule,false);
				bugRule = bugRules().findRuleToFire(new EquationHistory(currentEquationInfo,
																		targetVar,
																		originalEquation),
													action,
													input,
													"",
													"");
				if(bugRule != null){
					String[] messages = bugRule.getMessages(new EquationHistory(currentEquationInfo,
																				targetVar,
																				originalEquation));
					trans.showMessages(selection,messages,currentEquation+"; "+action+" "+input);
				}
			}
			else if(SolverFrame.debug()){
				trace.out("Can't find help rule (done)");
			}
			if(bugRule == null){
				if(SolverFrame.debug()){
					trace.out("ST.cTD: no bug rule for failed done action; using generic message.");
				}
				/*even if we can't find a help/bug rule, we want to
                  give the student some feedback as to why nothing is
                  happening when he selects done*/
				trans.showMessages(selection,
								   new String[] {"You are not done."},
								   currentEquation + "; " + action + " " + input);
			}
		}

		return foundRule;
	}
	// end Olga
	
	private RuleMatchInfo cycleTutor(String selection, String action, String input) throws DorminException {
		//check the strategic rules
		//trace.out("about to cycle tutor on "+currentEquationInfo.toString()+" "+action+" "+input);
		//trace.out("pattern is "+currentEquationInfo.getPattern());
		
		long start = System.currentTimeMillis();

		currentRules = null;
		/*these only need to be reset when starting a new problem, and
          the tool side (SolverFrame) does that for us
		  //reset skills for both sides. Feedback is used in typein mode.
		  setSkillInfo(false,LEFTSIDE);
		  setSkillInfo(false,RIGHTSIDE);*/
//		Test.clearHash();
		RuleMatchInfo foundRule = strategicRules().findRuleToFire(new EquationHistory(currentEquationInfo,
																					targetVar,
																					originalEquation),
																action,
																input);
		if (foundRule != null && foundRule.getBoolean() == true) { //found rule, so can find skill to increment
			if (foundRule.getRule().isTraced())
				trace.out("***Found Strategic rule "+foundRule.getRule().getName());
			//currentRule = (foundRule.getRule()).getName();
			updateSkillsForRule(foundRule.getRule(),true);
//				SkillRule foundSkill = skillRules.findRuleToFire(currentEquationInfo,(foundRule.getRule()).getName());
//				if (foundSkill != null) {
//					if (foundRule.getRule().isTraced())
//						trace.out("***Found Skill rule "+foundSkill.getName()+" subskill is "+foundSkill.getSubskillName());
//					trans.updateSkill(foundSkill.getSubskillName(),"1", currentEquationInfo.toString());
		}
		else { //no strategic rule found -- check for bugs and decrement skills
			trans.setFlag(selection,true);
			getValidActions();
			Rule helpRule = strategicRules().findRuleForHelp(new EquationHistory(currentEquationInfo,
																			   targetVar,
																			   originalEquation),
														   validActions);
			if (helpRule != null) {
				if (helpRule.isTraced())
					trace.out("***Found help rule to decrement skill: "+helpRule.getName());
				//currentRule = helpRule.getName();
//				SkillRule foundSkill = skillRules.findRuleToFire(currentEquationInfo,helpRule.getName());
//				if (foundSkill != null) {
//					if (helpRule.isTraced())
//						trace.out("***Skill to decrement is : "+foundSkill.getSubskillName());
//					trans.updateSkill(foundSkill.getSubskillName(),"0", currentEquationInfo.toString());
//				}
//				else
//					trace.out("No skill found for rule "+helpRule.getName());
				updateSkillsForRule(helpRule,false); //decrement skills corresponding to help rule
				//check for a bug rule
				Rule bugRule = bugRules().findRuleToFire(new EquationHistory(currentEquationInfo,
																					 targetVar,
																					 originalEquation),
													   action,
													   input,
													   helpRule.getAction(),
													   helpRule.getInput());
				if (bugRule != null) {
//trace.out("Found ST bug rule "+bugRule.getName());
					String[] messages = bugRule.getMessages(new EquationHistory(currentEquationInfo,
																			targetVar,
																			originalEquation));
					trans.showMessages(selection,messages,currentEquation+"; "+action+" "+input);
				}
			}
			else if(SolverFrame.debug()){
				trace.out("Can't find help rule");
			}
		}
		long end = System.currentTimeMillis();
		
//trace.out("cycle took "+((double)end-(double)start)/1000.0+" seconds");
		return foundRule;
	}
	
	private void getTutorHint(String selection) {
//		Test.clearHash();
		int currentState=0;
		try{
			String stateStr = trans.getProperty("Tool","current state");
			currentState = Integer.parseInt(stateStr);
		} catch (NoSuchPropertyException err) { }
		
		switch(currentState){
		/** 0 - step completed
		* 	1 - left side not set
		*	 2 - right side not set
		*/
		case 0:
			getValidActions();
			Rule helpRule = strategicRules().findRuleForHelp(new EquationHistory(currentEquationInfo,
																					 targetVar,
																					 originalEquation),
														   validActions);
			if (helpRule != null) {
				if (helpRule.isTraced())
					trace.out("***Found Help rule "+helpRule.getName());
				String[] messages = helpRule.getMessages(new EquationHistory(currentEquationInfo,
																			targetVar,
																			originalEquation));
				//trans.showMessages(selection,messages,currentEquation);
				trans.showMessages(selection,messages,Rule.resolveMessage("{equation}",currentEquationInfo));
				
try {
					//				SkillRule foundSkill = skillRules.findRuleToFire(currentEquationInfo,helpRule.getName());
//				if (foundSkill != null) {
//					if (helpRule.isTraced())
//						trace.out("***In Hint, Found Skill rule "+foundSkill.getName()+" subskill is "+foundSkill.getSubskillName());
//					trans.updateSkill(foundSkill.getSubskillName(),"2", currentEquationInfo.toString());
//				}
					updateSkillsForRule(helpRule,false); //decrement skills corresponding to this help
				} catch (DorminException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else {
				String[] messages = new String[] {"Sorry, I can't help you here."};
				//trans.showMessages(selection,messages,currentEquation);
				trans.showMessages(selection,messages,Rule.resolveMessage("{equation}",currentEquationInfo));
			}
			break;
		case 1:
		case 2:
			SideRuleTemplate sideTemplate = new SideRuleTemplate(trans,currentState);
			String[] messages1 = sideTemplate.getMessages();
			String currSides="";
			int skillSide = LEFTSIDE;
			if(currentState == 2)
				skillSide = RIGHTSIDE;
			try{
				currSides = trans.getProperty("Tool", "current sides");
			} catch (NoSuchPropertyException err) { }
			trans.showMessages(selection,messages1,currSides);
			///mmmBUG will the value of currentRules be correct when this is called?
			updateSkillsTypeIn("2", skillSide);
			break;
		}
	}
	
	private void updateSkillsTypeIn(String skillGradient, int skillSide){
		//trace.out("ST updateSkillsTypeIn skillGradient = "+skillGradient);
		if (currentRules != null){
			boolean foundASkill = false;
			//Typein skills use PREVIOUS equation info (i.e. they refer to the equation before the strategic step was taken)
			EquationHistory eh = new EquationHistory(previousEquationInfo,targetVar,originalEquation);
			if(SolverFrame.debug()){
				trace.out("ST.uSTI: finding typein skills for " + currentRules.length + " skills");
			}
			for(int i=0;i<currentRules.length;i++){
				SkillRule[] foundSkills = typeinSkillRules().findAllRulesToFire(eh,currentRules[i].getSubskillName());
				if(foundSkills != null){
					foundASkill = true;
					if(SolverFrame.debug()){
						trace.out("ST.uSTI: found " + foundSkills.length +
										   " typein skill(s) for skill rule " + currentRules[i].getSubskillName());
					}
					for(int j=0;j<foundSkills.length;j++){
						/*SkillRule foundSkill = typeinSkillRules.findRuleToFire(new EquationHistory(previousEquationInfo,
						  targetVar,
						  originalEquation),
						  currentRule);*/
						String currSides="";
						try{
							currSides = trans.getProperty("Tool", "current sides");
						} catch (DorminException err) { }
						if (foundSkills[j].isTraced())
							trace.out("***Found Skill rule "+foundSkills[j].getName()+
											   " subskill: "+foundSkills[j].getSubskillName());
						try {
							updateSkill(foundSkills[j].getSubskillName(),skillGradient, currSides);
						} catch (DorminException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if(SolverFrame.debug()){
					trace.out("ST.uSTI: no typein skills found for skill rule " + currentRules[i].getName() +
									   "(" + currentRules[i].getSubskillName() + ")");
				}
			}
			if(foundASkill){
				//setSkillInfo(true,skillSide);
				setSkillInfo(true);
			}
			else if(SolverFrame.debug()){
				//this failure is more problematic
				trace.out("ST.uSTI: warning: no typein skills found for skill rules {" + currentRules[0]);
				if(currentRules.length > 1){
					trace.out(", ...}");
				}
				else{
					trace.out("}");
				}
			}
		}
		else if(SolverFrame.debug()){
			trace.out("ST.uSTI: no skill rules, so can't search for typein skill rules");
		}
	}

	private void setSkillInfo(boolean val){
		try{
			trans.setProperty("Tool","skills set", String.valueOf(val));
		}
		catch(DorminException de){ }
	}

	private void setSkillInfo(boolean val,int side){
		try{
			String sidestr;
			if(side == LEFTSIDE){
				sidestr = "left side skills set";
			}
			else{
				sidestr = "right side skills set";
			}
			trans.setProperty("Tool",sidestr, String.valueOf(val));
		}
		catch(DorminException de){ }
	}

	private boolean getSkillInfo(){
		boolean ret = false;

		try{
			String prop = trans.getProperty("Tool","skills set");
			if(prop.equalsIgnoreCase("true")){
				ret = true;
			}
		}
		catch(NoSuchPropertyException nspe){
			trace.out("ST.gSI: " + nspe);
		}

		return ret;
	}

	private boolean getSkillInfo(int side){
		boolean ret = false;

		try{
			String sidestr;
			if(side == LEFTSIDE){
				sidestr = "left side skills set";
			}
			else{
				sidestr = "right side skills set";
			}
			String prop = trans.getProperty("Tool",sidestr);
			if(prop.equalsIgnoreCase("true")){
				ret = true;
			}
		}
		catch(NoSuchPropertyException nspe){
			trace.out("ST.gSI: " + nspe);
		}

		return ret;
	}

	private void cycleTutorTypein(String selection, String action, String input) {
		/*trace.out("ST.cTT("+selection+","+action+","+input+")");
		  trace.out("ST.cTT: Current equation: "+currentEquation);*/
//		Test.clearHash();
		SymbolManipulator sm = new SymbolManipulator();
		sm.setMaintainVarList(true);
		String comparison;
		int skillSide;
		try {
			if (selection.substring(0,4).equalsIgnoreCase("left")){
				comparison = sm.runScript("left",currentEquation);
				skillSide = LEFTSIDE;
			}
			else {
				comparison = sm.runScript("right",currentEquation);
				skillSide = RIGHTSIDE;
			}
			//trace.out("ST.cTT: comparing: " + input + " =?= " + comparison);
			if (typeinEqual(selection,input,comparison)) {
				//trace.out("ST.cTT: input " + input + " matches " + comparison);
				trans.setFlag(selection,false);
				updateSkillsTypeIn("1", skillSide);
			}
			else{
				if(SolverFrame.debug()){
					trace.out("ST.cTT: input " + input + " does not match " + comparison);
				}
				trans.setFlag(selection,true);
				updateSkillsTypeIn("0", skillSide);

				//check for a bug rule
				EquationHistory eh;
				if(skillSide == LEFTSIDE){
					eh = new EquationHistory(new Equation(prevTIeq.getLeft(),null),
											 targetVar,
											 originalEquation);
				}
				else{
					eh = new EquationHistory(new Equation(prevTIeq.getRight(),null),
											 targetVar,
											 originalEquation);
				}
				TypeinBugRule bugRule = (TypeinBugRule)typeinBugRules().findRuleToFire(eh,
																					 prevTIaction,
																					 prevTIinput,
																					 input,
																					 comparison);
				if(bugRule != null){
					//mmmBUG probably need to pass in input here, too
					String[] messages = bugRule.getMessages(eh,
															prevTIinput,
															input);
					trans.showMessages(selection,messages,
									   currentEquation+"; " + action + " " + input);
				}
			}
		}
		catch (BadExpressionError err) {
			trans.setFlag(selection,true);
			trace.out("Bad expression in cycleTutorTypein...");
		}
		catch (NoSuchFieldException err) {
			trace.out("No such field in cycleTutorTypein");
		}
	}

	/*returns true if input is an acceptable version of comparison.
      This uses the prevTI* data, so the result is contextualized
      based on the operation being performed.  On an action-specific
      basis, we want to allow the student to make some implicit
      simplifications, but not others.  See below for specifics.*/
	private boolean typeinEqual(String selection,String input,String comparison) throws BadExpressionError{
		/*trace.out("ST.tE: checking user input " + input + " against " + comparison);
		  trace.out("ST.tE: context: " + prevTIeq + "; " + prevTIaction + "; " + prevTIinput);*/
		SymbolManipulator sm = new SymbolManipulator();
		sm.setMaintainVarList(true);
		sm.allowExtraParens = false;
		sm.allowDoubleSigns = false;
		sm.distributeDenominator = SolverFrame.getSelf().getSM().distributeDenominator;

		/*we always allow the expected equation*/
		if(sm.similar(sm.removeExtraParens(input),sm.removeExtraParens(comparison))){
			//trace.out("ST.tE: similar; true");
			return true;
		}

		String prevTIside;
		if(selection.substring(0,4).equalsIgnoreCase("left")){
			prevTIside = prevTIeq.getLeft().toString();
		}
		else{
			prevTIside = prevTIeq.getRight().toString();
		}

		/*something must have changed ...*/
		if(sm.exactEqual(prevTIside,input)){
			//trace.out("ST.tE: nothing was changed: false");
			return false;
		}

		/*if autosimplify/autostandardize is on, we allow any
          intermediate equation, as well*/
		if(SolverFrame.getSelf().getSM().autoSimplify &&
		   sm.similar(sm.simplify(input),comparison)){
			//trace.out("ST.tE: not fully simplified: true");
			return true;
		}
		else if(SolverFrame.getSelf().getSM().autoStandardize &&
				sm.similar(sm.standardize(input),comparison)){
			//trace.out("ST.tE: not fully standardized: true");
			return true;
		}

		/*when combining like terms, we allow "intermediate" versions.
          E.g., 3x+4x+11 and 7x+5+6 are legal answers for clt on
          3x+4x+5+6 (but 3x+4x+5+6 is not).*/
		if(prevTIaction.equalsIgnoreCase("clt")){
			/*if they just didn't combine everything that could be
              combined, that's fine.  removeExtraParens allows 2(3+4)
              to clt to 2(7) as well as 2*7*/
			if(sm.similar(sm.removeExtraParens(sm.combineLikeTerms(input)),
						  comparison) ||
			   sm.similar(sm.removeExtraParens(sm.combineLikeTerms(input)),
						  sm.removeExtraParens(sm.combineLikeTerms(comparison)))){
				return true;
			}
			else{
				return false;
			}
		}
		/*when distributing, we allow the student to perform none,
          some, or all of the resulting multiplications.  However, we
          don't allow the terms resulting from those multiplications
          to be combined.  So, distributing on 3(4+5) can give:
		  3*4+3*5
		  12+3*5
		  12+15
		  but not 27*/
		else if(prevTIaction.equalsIgnoreCase("distribute")){
			if(sm.similar(sm.multiplyThrough(input),sm.multiplyThrough(comparison))){
				return true;
			}
			else{
				return false;
			}
		}
		else if(prevTIaction.equalsIgnoreCase("mt")){
			/*if they just didn't combine everything that could be
              combined, that's fine*/
			if(sm.similar(sm.multiplyThrough(input),comparison)){
				return true;
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
		else if(prevTIaction.equalsIgnoreCase("add") ||
				prevTIaction.equalsIgnoreCase("subtract")){
			return exprCLTequiv(input,comparison,prevTIinput);
		}
		/*when multiplying/dividing (including when
          cross-multiplying), the student can distribute, perform
          multiplication, or reduce fractions.  For example, on
          3x=6+9, divide by 3, we accept x=2+3 in addition to
          3x/3=6/3+9/3 and 3x/3=(6+9)/3 -- but we don't accept x=5*/
		else if(prevTIaction.equalsIgnoreCase("multiply") ||
				prevTIaction.equalsIgnoreCase("divide") ||
				prevTIaction.equalsIgnoreCase("cm")){
			/*ugh ... sure wish I knew whether these simplification
              ops commute or not.  unfortunately they probably don't*/
			String modInput,modInput2,modComparison;
			sm.autoDistribute = sm.autoMultiplyThrough = sm.autoReduceFractions = true;
			modComparison = sm.noOp(comparison);
			sm.autoDistribute = sm.autoMultiplyThrough = sm.autoReduceFractions = false;
			modInput = sm.distribute(input);
			//trace.out("ST.tE: checking mod input " + modInput + " against mod " + modComparison);
			if(sm.similar(modInput,modComparison)){
				return true;
			}
			else{
				modInput2 = sm.multiplyThrough(modInput);
				if(sm.similar(modInput2,modComparison) ||
				   sm.similar(sm.reduceFractions(modInput2),modComparison)){
					return true;
				}
				else{
					modInput2 = sm.reduceFractions(modInput2);
					if(sm.similar(modInput2,modComparison) ||
					   sm.similar(sm.multiplyThrough(modInput2),modComparison)){
						return true;
					}
				}
			}

			modInput = sm.multiplyThrough(input);
			if(sm.similar(modInput,modComparison)){
				return true;
			}
			else{
				modInput2 = sm.reduceFractions(modInput);
				if(sm.similar(modInput2,modComparison) ||
				   sm.similar(sm.distribute(modInput2),modComparison)){
					return true;
				}
				else{
					modInput2 = sm.distribute(modInput2);
					if(sm.similar(modInput2,modComparison) ||
					   sm.similar(sm.reduceFractions(modInput2),modComparison)){
						return true;
					}
				}
			}

			modInput = sm.reduceFractions(input);
			if(sm.similar(modInput,modComparison)){
				return true;
			}
			else{
				modInput2 = sm.distribute(modInput);
				if(sm.similar(modInput2,modComparison) ||
				   sm.similar(sm.multiplyThrough(modInput2),modComparison)){
					return true;
				}
				else{
					modInput2 = sm.multiplyThrough(modInput2);
					if(sm.similar(modInput2,modComparison) ||
					   sm.similar(sm.distribute(modInput2),modComparison)){
						return true;
					}
				}
			}

			return false;
		}
		else{
			if(SolverFrame.debug()){
				trace.out("ST.tE: nothing specific to action " + prevTIaction + "; false");
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
		//trace.out("ST.eCLTe(" + ex1 + "," + ex2 + "," + term + ")");
		SymbolManipulator sm = new SymbolManipulator();
		sm.setMaintainVarList(true);
		sm.allowExtraParens = false;
		sm.allowDoubleSigns = false;

		String diff = sm.subtract(ex1,ex2);

		/*anything that was left out has to have been combinable
          (which means it was equal to 0)*/
		if(!sm.algebraicEqual(diff,"0")){
			//trace.out("         " + diff + " != 0: false");
			return false;
		}

		try{
			//String termPattern = sm.getPattern(term + "=0").getLeft().toString();
			Queryable[] termTerms = sm.runArrayScript("terms",term);
			String[] termPatterns = new String[termTerms.length];
			for(int i=0;i<termTerms.length;i++){
				termPatterns[i] = sm.getPattern(termTerms[i].getStringValue() + "=0").getLeft().toString();
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
			}
			else{
				diff = "0";
			}

			//trace.out("         " + diff + " == 0: checking terms");

			int termCount = Integer.valueOf(sm.runScript("length of terms",diff)).intValue();

			for(int i=1;i<=termCount;i++){
				//trace.out("         checking term: " + sm.runScript("absolute value of item " + i + " of terms",diff));
				boolean found = false;
				for(int j=0;j<termPatterns.length && !found;j++){
					//trace.out("               against: " + termPatterns[j]);
					try{
						sm.runScript("term matching " + termPatterns[j] + " of absolute value of item " + i + " of terms",diff);
						found = true;
					}
					catch(NoSuchFieldException nsfe){ }
				}
				if(!found){
					throw new NoSuchFieldException("term number " + i + " does not match");
				}
			}

			/*all of the terms match the pattern, so combining them was legal in this case*/
			//trace.out("         all matched: true");
			return true;
		}
		catch(NoSuchFieldException nsfe){
			/*one of the terms didn't match, so it wasn't legal to combine it*/
			//trace.out("         failed: " + nsfe.toString());
			return false;
		}
	}

	/*calculates the diff of q1 and q2, assuming they're expressions
      and using sm.exactEqual() as the test for equality*/
	private Queryable[] eeDiff(Queryable[] q1,Queryable[] q2) throws BadExpressionError{
		SymbolManipulator sm = new SymbolManipulator();
		sm.setMaintainVarList(true);

		int total = q1.length + q2.length;
		for(int i=0;i<q1.length;i++){
			for(int j=0;j<q2.length;j++){
				if((q1[i] != null) && (q2[j] != null) &&
				   sm.exactEqual(((Queryable)q1[i]).getStringValue(),
								 ((Queryable)q2[j]).getStringValue())){
					q1[i] = q2[j] = null;
					total -= 2;
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

	public Object getProperty(String theProp) throws NoSuchPropertyException {
		throw new NoSuchPropertyException("getProperty: SolverTutor does not have property: "+theProp);
	}
	
	public void setProperty(String theProp,Object theValue) throws NoSuchPropertyException, DataFormatException {
		throw new NoSuchPropertyException("setProperty: SolverTutor does not have property: "+theProp);
	}
}
