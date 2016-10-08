package edu.cmu.old_pact.cmu.sm;

//DivideByZeroException - exception representing division by zero

public class DivideByZeroException extends ArithmeticException {
	public DivideByZeroException() {
		super();
	}
	
	public DivideByZeroException(String msg) {
		super(msg);
	}
	
	public DivideByZeroException(Expression errEx, String whileDoing) {
		super("Divide by zero while "+whileDoing+": "+errEx);
	}
}
