package edu.cmu.old_pact.cmu.solver.ruleset;
/**
* used for rules like ax+b=c, where x = y^n, n = 1,2,3,...
**/

import edu.cmu.old_pact.cmu.sm.Equation;
import edu.cmu.old_pact.cmu.sm.Expression;
import edu.cmu.old_pact.cmu.sm.ParseException;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;
import edu.cmu.old_pact.cmu.sm.query.Queryable;


public class VarEncapsulation {
	
	// Works only with int degrees for now
	public static Expression encapsulateVar(Expression exp, double degree){
		int degreeInt = (int)degree;
		
		int k = degreeInt-1;
		Queryable st = null;
		boolean onlyOneDegree = true;
		while(k>1){
			try{
				st = exp.getProperty("term with degree "+String.valueOf(k));
			}catch (NoSuchFieldException e) { }
			if(st != null && !st.getStringValue().startsWith("0")){
				onlyOneDegree = false;
				break;
			}
			k--;
		}
		if(onlyOneDegree){
			try{
				st = exp.getProperty("term with degree 1");
			} catch (NoSuchFieldException e) { }
			if(st != null && !st.getStringValue().startsWith("0"))
				onlyOneDegree = false;

		}
		if(!onlyOneDegree)
			return exp;
		
		String degStr = "^"+String.valueOf(degreeInt);
		String expStr = exp.toString();
		
		int degreeLen = degStr.length();
		int expStrLen = expStr.length();
		
		int ind = expStr.indexOf(degStr);
		char ch = expStr.charAt(ind-1);
		//do it rec;
		String s;
		if(ind+degreeLen==expStrLen)
			s = expStr.substring(0,ind);
		else
			s = expStr.substring(0,ind)+expStr.substring(ind+degreeLen);

		SymbolManipulator sm = new SymbolManipulator();
		sm.setMaintainVarList(true);
		try{
			exp = sm.parse(s);
		}catch (ParseException ex) { }
		exp.setEncapsulateVar(true);
		sm = null;
		return exp;
	}
	
	public static Equation tryEncapsulateVar(Equation userEq){
		Expression user_left = userEq.getLeft();
		Expression user_right = userEq.getRight();
		double degree = user_left.degree();
		boolean found = false;
		if(degree > 1) {
			found = true;
			user_left = encapsulateVar(user_left, degree);
		}
		if(user_right != null){
			degree = user_right.degree();
			if(degree > 1){
				found = true;
				user_right = encapsulateVar(user_right,degree);
			}
		}
		if(!found)
			return userEq;
		return new Equation(user_left, user_right);
	}
}
