package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.Equation;
import edu.cmu.old_pact.cmu.sm.ParseException;
import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.pact.Utilities.trace;

/*a typein bug rule applies to an expression (that is, one side of the
  equation).  It is conditioned on the previous expression in addition
  to the student's action and the student's input.  A test can use the
  "input" property to refer to the student's input or the "previnput"
  property to refer to the previous input in a query.  For example, if
  the student inputs "3x+4+1" after subtracting 1 from "3x+4",
  {constant terms} => [4], {constant terms of input} => [4,1], and
  {previnput} => 1

  This is not a subclass of BugRule because during a typein step we're
  already committed to the action, so there is no alternate desired
  action.  And, although there is a desired input, that can be
  calculated through the symbol manipulator (?).

  A typein bug rule needs to be able to apply tests to the user input.
  So TypeinBugRule doesn't call testActionAndInput, because the action
  is already cemented.*/

public class TypeinBugRule extends Rule {
	private static final int SIDE_UNK = 0;
	private static final int SIDE_LEFT = 1;
	private static final int SIDE_RIGHT = 2;

	//constructors
	public TypeinBugRule(String nme, Test[] cond,String act,String inp,String[] mess){
		super(nme,cond,act,inp,mess);
	}

	public TypeinBugRule(String nme, Test cond,String act,String inp,String[] mess){
		super(nme,cond,act,inp,mess);
	}

	/*info is an equation with no right side (essentially an
	  expression) -- in reality it might be the left or right side of
	  the equation
	  userAction is the operation
	  userInput is the arg to that operation
	  typeinInput is the user's result for the given operation
	  for example: canFire("3x+4","subtract","4","3x")*/
	public RuleMatchInfo canFire(Queryable info,String userAction,String userInput,
								 String typeinInput,String expectedInput){
		/*trace.out("TBR.cF(" + info + "," + userAction + "," + userInput + "," +
		  typeinInput + "," + expectedInput + ")");*/
		RuleMatchInfo ret = new RuleMatchInfo();
		ret.setBoolean(false);
		ret.setRule(this);
		ret.setAction(userAction);
		ret.setInput(userInput);
		boolean cfhLeft = false;
		ExprInputQuery leftExpr = null;

		try{
			leftExpr = new ExprInputQuery(((Equation)info).getLeft(),
										  typeinInput,
										  ((EquationHistory)info).getTargetVar(),
										  userInput,
										  expectedInput);

			//trace.out("TBR.cF: calling tCFH(" + leftExpr + ")");
			cfhLeft = testConditionsForHelp(leftExpr);
		}
		catch(ParseException pe){
			System.out.println("TBR.cF: " + pe);
		}
		catch(ClassCastException cce){
			System.out.println("TBR.cF: " + cce);
		}

		if(cfhLeft){
			boolean actionOK;
			if(action == null){
				actionOK = true;
			}
			else{
				actionOK = action.equalsIgnoreCase(userAction);
			}
			ret.setBoolean(actionOK);
		}

		return ret;
	}

	public String[] getMessages(Equation eq,String userInput,String typeinInput) {
		Equation info=eq;
		try{
			ExprInputQuery eiq=null;
			eiq = new ExprInputQuery(((Equation)info).getLeft(),
									 typeinInput,
									 ((EquationHistory)info).getTargetVar(),
									 userInput,
									 null);
			info = eiq;
		}
		catch(ParseException pe){ }

		if(messages != null){
			(info.getLeft()).setEncapsulateVar(canEncapsulateVar);
			String resolvedMessages[] = new String[messages.length];
			for (int i=0;i<messages.length;++i) {
				resolvedMessages[i] = resolveMessage(messages[i],info);
			}
			if (isTraced()){
				resolvedMessages[0] = getName()+": "+resolvedMessages[0];
			}
			return resolvedMessages;
		}
		else{
			trace.out("Rule.getMessages: Warning: rule '" + getName() + "' fired for help but has no messages");
			return new String[] {"Sorry, I can't help you here."};
		}
	}
}
