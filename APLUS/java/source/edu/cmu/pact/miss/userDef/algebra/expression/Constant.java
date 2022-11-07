package edu.cmu.pact.miss.userDef.algebra.expression;

import java.util.HashSet;
import java.util.Set;

/**
 * An abstract class representing a simple Constant number
 * @author ajzana
 *
 */
public abstract class Constant extends AlgExp {
	
	protected double value;
	public abstract double getVal();
	
    public Constant()
    {
        isSimple=true;
        isConstant=true;
        isMonomial=true;
        
    }
	public AlgExp div(Variable v) 
	{

		return mul(v.invert());
	}
	public AlgExp div(ConstantFraction f) 
	{
		
		return mul(f.invert());
	}
        
	public AlgExp div(SimpleTerm t) {

	    AlgExp coefficient = div(t.getConstant());
            // trace.out("coefficient = " + coefficient);
            return coefficient.mul(t.getVariable().invert());
            // return mul(t.invert());
	}
        
	public AlgExp div(Polynomial e) {

		return mul(e.invert());
	}
	public AlgExp div(ComplexTerm ct) 
	{
			return mul(ct.invert());
		
	}
	
    public AlgExp div(ComplexFraction cf)
    {
        return mul(cf.invert());
    }
	public AlgExp add(Variable v) 
	{
		return v.add(this);
	}
	public AlgExp add(ConstantFraction f) 
	{

		return f.add(this);
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
    public AlgExp add(ComplexFraction cf)
    {
        return cf.add(this);
    }
	
	public AlgExp mul(Variable v) 
	{
		return v.mul(this);
	}
	public AlgExp mul(ConstantFraction f) 
	{

		return f.mul(this);
	}
	public AlgExp mul(SimpleTerm t) 
	{
		return t.mul(this);
		
	}
	public AlgExp mul(Polynomial e) 
	{
		return e.mul(this);
	}
	public AlgExp mul(ComplexTerm ct) 
	{
		return ct.mul(this);
	}

    public AlgExp mul(ComplexFraction cf)
    {
        return cf.mul(this);
    }
	public AlgExp invert()
	{
		return new ConstantFraction(AlgExp.ONE,this);
	}
	
	public boolean equals(ConstantFraction f) 
	{
		
		return f.equals(this);
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



	public boolean equals(ComplexFraction cf) 
	{

		return cf.equals(this);
	}
	public boolean equals(Variable v) 
	{

		return false;
	}
	public boolean hasVariable(String varName)
	{
		return false;
	}
	public Set getAllVars()
	{
		return new HashSet();
	}
}
