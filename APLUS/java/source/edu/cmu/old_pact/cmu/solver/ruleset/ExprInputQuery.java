package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.Equation;
import edu.cmu.old_pact.cmu.sm.Expression;
import edu.cmu.old_pact.cmu.sm.ParseException;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;
import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.old_pact.cmu.sm.query.StringQuery;

/*this class encapsulates the previous expression along with the
  user's input, allowing tests to perform queries on both expressions.
  it also provides the "target variable" property a la
  EquationHistory.*/

public class ExprInputQuery extends Equation{
	private String targetVar;
	private Expression input = null;
	//private Expression expr;
	private Expression prevInput = null;
	private Expression expectedInput = null;

	public ExprInputQuery(Expression ex,String inp,String var,String prevInp,String expctInp) throws ParseException{
		super(ex,null);
		targetVar = var;
		//expr = ex;
		//prevInput = prevInp;

		SymbolManipulator sm = new SymbolManipulator();
		if(prevInp != null){
			prevInput = sm.parse(prevInp,new String[] {targetVar});
		}
		if(inp != null){
			input = sm.parse(inp,new String[] {targetVar});
		}
		if(expctInp != null){
			expectedInput = sm.parse(expctInp,new String[] {targetVar});
		}
	}

	public String getTargetVar(){
		return targetVar;
	}

	public Expression getInput(){
		return input;
	}

	public Expression getExpr(){
		return getLeft();
	}

	public Expression getPrevInput(){
		return prevInput;
	}

	public Queryable getProperty(String prop) throws NoSuchFieldException{
		//trace.out("EIQ.gP(" + prop + ")");
		Queryable result;
		if(prop.equalsIgnoreCase("input") && (input != null)){
			return input;
		}
		else if(prop.equalsIgnoreCase("target variable")){
			return new StringQuery(targetVar);
		}
		else if(prop.equalsIgnoreCase("previnput") && (prevInput != null)){
			return prevInput;
		}
		else if(prop.equalsIgnoreCase("expectedinput") && (expectedInput != null)){
			return expectedInput;
		}
		else{
			return super.getProperty(prop);
		}
	}

	public String toString(){
		return "[ExprInputQuery: " + getLeft() + "," + input + "," + targetVar + "]";
	}

	/*public void setProperty(String prop,String value) throws NoSuchFieldException{
	  expr.setProperty(prop,value);
	  }
	
	  public Queryable evalQuery(String query) throws NoSuchFieldException{
	  return expr.evalQuery(query);
	  }
	
	  public Queryable applyOp(String op,Vector args) throws NoSuchFieldException{
	  return expr.applyOp(op,args);
	  }
	
	  public Number getNumberValue(){
	  return expr.getNumberValue();
	  }
	
	  public boolean getBooleanValue(){
	  return expr.getBooleanValue();
	  }
	
	  public String getStringValue(){
	  return expr.getStringValue();
	  }
	
	  public Queryable[] getArrayValue(){
	  return expr.getArrayValue();
	  }*/
}
