package edu.cmu.pact.miss.userDef.algebra.expression;
/**
 * Class to represent floating point constants, note that this class may have precision issues
 * @author ajzana
 *
 */
public class DoubleConst extends Constant {

	private double value;
	
	 DoubleConst(String s) 
	{
		value=Double.parseDouble(s);
		isNegative=value<0;
	}
	DoubleConst(double d) 
	{
		value=d;
		isNegative=value<0;
	}
	public double getVal() 
	{
		
		return value;
	}
	public static final double EPISILON=0.0001D;//precison for comparing doubles
	/* operations*/
	
	/*actually handled operations*/
	public AlgExp add(IntConst c) 
	{
		double result=value+c.getVal();
		if(result==Math.floor(result))
			return new IntConst((int)result);
		return new DoubleConst(result);
	}
	public AlgExp mul(IntConst c) 
	{
	
		double result=value*c.getVal();
		if(result==Math.floor(result))
			return new IntConst((int)result);
		return new DoubleConst(result);
	}
	public AlgExp div(IntConst c) 
	{
		double result=value/c.getVal();
		if(result==Math.floor(result))
			return new IntConst((int)result);
		return new DoubleConst(result);
	}
	public AlgExp add(DoubleConst c) 
	{
		java.math.BigDecimal d1 = new java.math.BigDecimal("" + value);
		java.math.BigDecimal d2 = new java.math.BigDecimal("" + c.getVal());
		java.math.BigDecimal dresult = d1.add(d2);		
				
		double result = dresult.doubleValue(); //doubleValue() + d2.doubleValue());
		
		//trace.out("add result = " + result);//gustavo
		if(result==Math.floor(result))
			return new IntConst((int)result);
		return new DoubleConst(result);
	}
	public AlgExp mul(DoubleConst c) 
	{
	
		double result=value*c.getVal();
		if(result==Math.floor(result))
			return new IntConst((int)result);
		return new DoubleConst(result);
	}
	public AlgExp div(DoubleConst c) 
	{
	
		double result=value/c.getVal();
		if(result==Math.floor(result))
			return new IntConst((int)result);
		return new DoubleConst(result);
	}
	
	public String toString()
	{
		if(Math.floor(value)==value)
			return String.valueOf((int)value);
		return String.valueOf(value);
	}
	public boolean equals(IntConst c)
	{

		return doubleEq(value,c.getVal());
	}
	
	public boolean equals(DoubleConst c)
	{

		return doubleEq(value,c.getVal());
	}
	public AlgExp eval() 
	{
		return this;
	}
	/**
	 * compare to doubles within DoubleConst.Epsilon
	 */
	public static boolean doubleEq(double d1,double d2)
	{
		if(Math.abs(d1-d2)<EPISILON)
			return true;
		return false;
	}

    public AlgExp divDecimal(IntConst c) {
        new Exception().printStackTrace();
        return null;
    }

}
