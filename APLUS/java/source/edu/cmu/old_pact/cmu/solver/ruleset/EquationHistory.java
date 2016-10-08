package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.Equation;
import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.old_pact.cmu.sm.query.StringQuery;

/*this class encapsulates the current equation, the original equation,
  and the target variable.  It's passed into the rules instead of just
  using the current equation so that hint & bug messages are always
  able to use this additional info.*/

public class EquationHistory extends Equation{
	private String targetVar;
	private String origEq;

	public EquationHistory(Equation currentEq, String var, String oldEq){
		super(currentEq.getLeft(),currentEq.getRight());
		targetVar = var;
		origEq = oldEq;
	}

	public String getTargetVar(){
		return targetVar;
	}

	public String getOriginalEquation(){
		return origEq;
	}

	/*handle our two special properties here, and pass anything else
      on up to the parent equation*/
	public Queryable getProperty(String prop) throws NoSuchFieldException{
		Queryable result;
		if(prop.equalsIgnoreCase("target variable")){
			return new StringQuery(targetVar);
		}
		else if(prop.equalsIgnoreCase("original equation")){
			try{
				return new Equation(origEq);
			}
			catch(BadExpressionError bee){
				return new StringQuery(" - error parsing original equation: " + origEq + " - ");
			}
		}
		else{
			result = super.getProperty(prop);
		}

		return result;
	}

	/*all of the other queryable methods just pass thru to the equation*/
	/*public void setProperty(String prop,String value) throws NoSuchFieldException{
	  eq.setProperty(prop,value);
	  }
	  
	  public Queryable evalQuery(String query) throws NoSuchFieldException{
	  return eq.evalQuery(query);
	  }
	  
	  public Queryable applyOp(String op,Vector args) throws NoSuchFieldException{
	  return eq.applyOp(op,args);
	  }
	  
	  public Number getNumberValue(){
	  return eq.getNumberValue();
	  }
	  
	  public boolean getBooleanValue(){
	  return eq.getBooleanValue();
	  }
	  
	  public String getStringValue(){
	  return eq.getStringValue();
	  }
	  
	  public Queryable[] getArrayValue(){
	  return eq.getArrayValue();
	  }*/
}
