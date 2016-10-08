package edu.cmu.old_pact.cmu.sm;


//ExpressionArray is a class that provides arrays of Expression objects
//It also manages the allocation of these objects

public class ExpressionArray {
	private static ExpressionArray arrays[] = new ExpressionArray[50];
	private static int arrayIndex = 0;
	
	private int numExpressions=0;
	private Expression expressions[];
	public boolean ownsExpressions;
	
	private ExpressionArray() {
		expressions = new Expression[50];
		ownsExpressions = true;
//		System.out.println("***created ExpressionArray");
	}
	
	//when given an array of expressions, we copy the array
	private ExpressionArray(Expression theExpressions[],int numEx) {
		this();
		for (int i=0;i<numEx;++i)
			expressions[i] = theExpressions[i];
		numExpressions = numEx;
	}
	
	private void setExpressions(Expression theExpressions[],int numEx) {
		for (int i=0;i<numEx;++i)
			expressions[i] = theExpressions[i];
		numExpressions = numEx;
	}
	
	public static ExpressionArray allocate () {
		if (arrayIndex > 0)
			return arrays[--arrayIndex];
		else
			return new ExpressionArray();
	}
	
	public static ExpressionArray allocate (Expression theExpressions[],int numEx) {
		if (arrayIndex > 0) {
			ExpressionArray toReturn = arrays[--arrayIndex];
			toReturn.setExpressions(theExpressions,numEx);
			return toReturn;
		}
		else
			return new ExpressionArray(theExpressions,numEx);
	}
	
	public static void deallocate (ExpressionArray theArray) {
		if (theArray.ownsExpressions) {
			Expression theExpressions[] = theArray.getExpressions();
			for (int i=0;i<theArray.size();++i)
				theExpressions[i] = null;
			theArray.setSize(0);
			arrays[arrayIndex++] = theArray;
//			if (arrayIndex > 1)
//				System.out.println("dealloc>1, index is now "+arrayIndex);
		}
		else {
			theArray.loseExpressions();
			arrays[arrayIndex++] = theArray;
		}
	}
	
	public int size() {
		return numExpressions;
	}
	
	public void setSize(int newSize) {
		numExpressions = newSize;
	}
	
	public Expression[] getExpressions() {
		return expressions;
	}
		
	public void loseExpressions() {
		expressions = null;
		numExpressions = 0;
	}
	
	public Expression expressionAt(int place) {
		return expressions[place];
	}
	
	public void setExpressionAt(Expression ex,int place) {
		expressions[place]=ex;
	}
	
	public void addExpression(Expression ex) {
		expressions[numExpressions++] = ex;
	}
}
