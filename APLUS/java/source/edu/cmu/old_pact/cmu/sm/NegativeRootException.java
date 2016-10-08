package edu.cmu.old_pact.cmu.sm;

public class NegativeRootException extends ArithmeticException{
	NumericExpression base=null,exp=null;

	public NegativeRootException(){
		super();
	}

	public NegativeRootException(String s){
		super(s);
	}

	public NegativeRootException(NumericExpression base,NumericExpression exp,String s){
		super(s);
	    this.base = base;
		this.exp = exp;
	}

	public NumericExpression getBase(){
		return base;
	}

	public NumericExpression getExp(){
		return exp;
	}
}
