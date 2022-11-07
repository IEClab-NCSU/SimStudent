//since testing the equation form is such a common type of test,
//we subclass BooleanTest for it

package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.Equation;
import edu.cmu.old_pact.cmu.sm.Expression;
import edu.cmu.old_pact.cmu.sm.query.Queryable;

public class FormTest extends Test {
	private Equation matchForm;
	private String myForm;
	private boolean canEncapsulateVar = true;
	
	
	public FormTest(String form, boolean canEncapsulateVar) {
		this.canEncapsulateVar = canEncapsulateVar;
		myForm = form;
		try {
			 matchForm = Equation.makeForm(form);
		}
		catch (BadExpressionError err) {
			System.out.println("Can't parse "+form+" "+err);
			matchForm=null;
		}
	}
	public FormTest(String form) {
		this(form,true);
	}
	
	public boolean passes(Queryable info) {
		boolean result = false;
		//result = matchForm.patternMatches((Equation)info);
		if(info instanceof Equation){
			result = equationsPatternMatches(matchForm, (Equation)info);
			//trace.out("in FT myForm = "+myForm+" matchForm = "+matchForm+" eq = "+((Equation)info)+" result = "+result);
		}
		else if(info instanceof Expression){
			result = equationsPatternMatches(matchForm, (Expression)info);
		}
		return result;
	}

	public String toString() {
		return "[FormTest: "+myForm+"]";
	}
	
	private boolean safeExactMatch(Expression ex1,Expression ex2) {
		if (ex1 == null && ex2 == null)
			return true;
		else if (ex1 == null || ex2 == null)
			return false;
		else
			return ex1.exactEqual(ex2);
	}
	
	private boolean equationsPatternMatches(Equation formEq, Expression userEq) {
		return equationsPatternMatches(formEq,new Equation(userEq,null));
	}

	private boolean equationsPatternMatches(Equation formEq, Equation userEq) {
		Expression form_left = formEq.getBoundLeft();
		Expression form_right = formEq.getBoundRight();
		if(canEncapsulateVar)
			userEq = VarEncapsulation.tryEncapsulateVar(userEq);
		Expression user_left = userEq.getBoundLeft();
		Expression user_right = userEq.getBoundRight();
//trace.out("FormTest patterns formEq = "+formEq+" userEq = "+userEq);
//trace.out("FormTest patterns form_left = "+form_left+" form_right = "+form_right+" user_left = "+user_left+" user_right = "+user_right);
		
		boolean leftmatches = safeExactMatch(form_left,user_left);
		boolean rightmatches = safeExactMatch(form_right,user_right);
		boolean leftReverseMatches = safeExactMatch(form_left,user_right);
		boolean rightReverseMatches = safeExactMatch(form_right,user_left);
		return ((leftmatches && rightmatches) ||
				(leftReverseMatches && rightReverseMatches));
	}
}
