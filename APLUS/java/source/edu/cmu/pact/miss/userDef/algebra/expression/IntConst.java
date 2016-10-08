package edu.cmu.pact.miss.userDef.algebra.expression;

/**
 * a class to represent an simple integer constant
 * @author ajzana
 *
 */
public class IntConst extends Constant 
{

	
	 IntConst(String s) 
	{
		 super();
		value=Integer.parseInt(s);
		isNegative=value<0;
		isInt=true;
	}
	 public IntConst(int c)
	 {
		
		this((double)c);
	 }
	public IntConst(double d) 
	{	super();
		value=d;
		isNegative=value<0;
		isInt=true;
	}
	public double getVal() 
	{
		return value;
	}
	
	/* operations*/
	
	/*actually handled operations*/
	public AlgExp add(DoubleConst c) 
	{
	
		
		
		double result=value+c.getVal();
		if(result==Math.floor(result))
			return new IntConst((int)result);
		return new DoubleConst(result);
	}
	public AlgExp add(IntConst c)
	{
		return new IntConst(value+c.getVal());
	}
	public AlgExp mul(DoubleConst c) 
	{
	

		
		
		double result=value*c.getVal();
		if(result==Math.floor(result))
			return new IntConst((int)result);
		return new DoubleConst(result);
	}
	public AlgExp mul(IntConst c)
	{
		return new IntConst(value*c.getVal());
	}
	public AlgExp div(DoubleConst c) 
	{
		double result=value/c.getVal();
		if(result==Math.floor(result))
			return new IntConst((int)result);
		return new DoubleConst(result);
		
	}
        
	public AlgExp div(IntConst c) {

            int otherVal = (int)c.getVal();
            int thisVal = (int)value;

            /*
            System.out.println("div(IntConst)");
            System.out.println("value = " + value + " otherVal = " + (int)c.getVal());
            System.out.println("value % otherVal = " + value % otherVal);
            System.out.println("value/otherVal = " + value/otherVal);
            */
	    
	    if(thisVal % otherVal==0)
	        return new IntConst(thisVal/otherVal);

	    return new ConstantFraction(this,c).eval();
	    //return new DoubleConst(((double)value)/c.getVal());
	}
        
        public AlgExp divDecimal(IntConst divident) {
            return new DoubleConst(getVal()/divident.getVal());
        }
	
	public String toString()
	{
		return String.valueOf((int)value);
	}
	
	public boolean equals(IntConst c)
	{
		return c.getVal()==value;
	}
	public boolean equals(DoubleConst c) 
	{

		return DoubleConst.doubleEq(value,c.getVal());
	}
	public AlgExp eval() 
	{
		return this;
	}

	
	
	


}
