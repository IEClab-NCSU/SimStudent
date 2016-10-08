package edu.cmu.pact.miss.userDef.algebra.expression;

import java.util.HashSet;
import java.util.Set;
/**
 * a class to represent a single variable(e.g. x)
 * @author ajzana
 *
 */
public class Variable extends AlgExp 
{

	protected String name;
	/**
	 *  the order (power) of the variable, currently not supported
	 */
	protected AlgExp order;
	public Variable(String s) 
	{
		name=s;
        isVariable=true;
        isSimple=true;
        order=AlgExp.ONE;
        hasVariable=true;
        isMonomial=true;
	}

	
	
	public String getName() 
	{
	
		return name;
	}
	public AlgExp mul(Variable v) 
	{
		return new ComplexTerm(this,v);
	}
	public AlgExp add(Variable v) 
	{
		if(v.getName().equals(name))
			return new SimpleTerm(AlgExp.TWO,this);
		
		return new Polynomial(this,v);
		
	}
	public AlgExp div(Variable v) 
	{
		if(v.equals(this))
			return AlgExp.ONE;
		
		return new ComplexFraction(this,v);
	}

	
	public AlgExp div(IntConst c) 
	{
	
		return new SimpleTerm(c.invert(),this);
	}
	public AlgExp div(DoubleConst c) 
	{
		if(c.equals(AlgExp.ONE))
			return this;
		if(c.equals(AlgExp.ZERO))
			return AlgExp.ZERO;
		return new SimpleTerm(c,this);
	
	}
	
	public AlgExp add(IntConst c) 
	{
	
		return new Polynomial(this,c);
	}
	public AlgExp add(DoubleConst c) 
	{
	
		return new Polynomial(this,c);
	}
	public AlgExp mul(IntConst c) 
	{
		if(c.equals(AlgExp.ONE))
			return this;
		if(c.equals(AlgExp.ZERO))
			return AlgExp.ZERO;
		return new SimpleTerm(c,this).eval();
	
	}
	
	public AlgExp mul(DoubleConst c) 
	{
		if(c.equals(AlgExp.ONE))
			return this;
		if(c.equals(AlgExp.ZERO))
			return AlgExp.ZERO;
		return new SimpleTerm(c,this);
	
	}
	public AlgExp add(ConstantFraction f) 
	{

		return (new SimpleTerm(f.getDenominator(),this)).add(f.getNumerator());
	}
	public AlgExp mul(ConstantFraction f) 
	{

		return new SimpleTerm(f,this);
	}
	

	public AlgExp div(ConstantFraction f) 
	{
		return new SimpleTerm(f.invert(),this);

	}

	public AlgExp add(SimpleTerm t) 
	{
		return t.add(this);
		
	}
	public AlgExp add(Polynomial e) 
	{
		return e.add(this);
	}
	public AlgExp add(ComplexTerm ct) 
	{
		return ct.add(this);
	}
	
	
	

	
	public AlgExp mul(Polynomial e) 
	{
		return e.mul(this);
	}
	public AlgExp mul(ComplexTerm ct) 
	{
		return ct.mul(this);
	}
	

	
	
	public AlgExp mul(SimpleTerm t) 
	{
		return t.mul(this);
		
	}


	

	

	public AlgExp invert()
	{
		return new ComplexFraction(AlgExp.ONE,this);
	}
	
	public AlgExp div(SimpleTerm t) 
	{
		return mul(t.invert());
	}

	public AlgExp div(Polynomial e) 
	{
		
		return mul(e.invert());
	}

	public AlgExp div(ComplexTerm ct) 
	{
		return mul(ct.invert());
	}

	
    public AlgExp add(ComplexFraction cf)
    {
        return cf.add(this);
    }



    public AlgExp mul(ComplexFraction cf)
    {
        return cf.mul(this);
        
    }



    public AlgExp div(ComplexFraction cf)
    {
        return mul(cf.invert());
    }
	public boolean equals(Object o)
	{
		return equals((Variable)o);
	}
	public boolean equals(Variable v)
	{
		return (v.getName()).equalsIgnoreCase(this.getName());
	}
public String toString()
{
	return name;
}



public boolean equals(Constant c) 
{

	return false;
}



public boolean equals(ConstantFraction f) 
{
	
	return false;
}



public boolean equals(SimpleTerm t) 
{
	return t.equals(this);
}



public boolean equals(Polynomial e) 
{
	return e.equals(this);
}



public boolean equals(ComplexTerm ct) 
{
	return ct.equals(this);
}



public boolean equals(ComplexFraction cf) {

	return cf.equals(this);
}



public boolean equals(IntConst c) 
{

	return false;
}



public boolean equals(DoubleConst c) 
{

	return false;
}



public AlgExp eval() 

{
	return this;
}



public boolean hasVariable(String varName) 
{

	return name.equalsIgnoreCase(varName);
}



public Set getAllVars() 
{
	Set var=new HashSet();
	var.add(name);
	return var;
}


public AlgExp divDecimal(IntConst c) {
    new Exception().printStackTrace();
    return null;
}




}
