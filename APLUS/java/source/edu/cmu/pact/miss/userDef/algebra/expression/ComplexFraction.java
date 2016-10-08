package edu.cmu.pact.miss.userDef.algebra.expression;

import java.util.HashSet;
import java.util.Set;
/**
 * class to represent complex fractions (i.e. fractions where the numerator and denominator are not both constants) 
 * @author ajzana
 *
 */
public class ComplexFraction extends AlgExp
{

    AlgExp numerator;
    AlgExp denominator;
    public ComplexFraction(AlgExp e1, AlgExp e2)
    {
            isFraction=true;
            numerator=e1;
            denominator=e2;
            hasVariable=numerator.hasVariable()||denominator.hasVariable();
            isMonomial=numerator.isMonomial()&& denominator.isMonomial();
            isNegative=numerator.isNegative() ^ denominator.isNegative();
    }
    public AlgExp getNumerator()
    {
        return numerator;
    }
    public AlgExp getDenominator()
    {
        return denominator;
    }
    public AlgExp add(IntConst c)
    {
        return new Polynomial(this,c);
    }
    public AlgExp add(DoubleConst c)
    {
        return new Polynomial(this,c);
    }
    
    public AlgExp add(Variable v)
    {
        return new Polynomial(this,v);
    }
    public AlgExp add(ConstantFraction f)
    {

        return new Polynomial(this,f);
    }
    public AlgExp add(SimpleTerm t)
    {

        return new Polynomial(this,t);
    }
    public AlgExp add(Polynomial e)
    {
        
        return e.add(this);
    }
    public AlgExp add(ComplexTerm ct)
    {

        return new Polynomial(this,ct);
    }
    public AlgExp add(ComplexFraction cf)
    {

        if(denominator.equals(cf.getDenominator()))
            return new ComplexFraction(numerator.add(cf.getNumerator()),denominator);
        return new Polynomial(this,cf);
    }
    public AlgExp mul(IntConst c)
    {

        return new ComplexFraction(numerator.mul(c),denominator);
    }
    public AlgExp mul(DoubleConst c)
    {

        return new ComplexFraction(numerator.mul(c),denominator);
    }
    public AlgExp mul(Variable v)
    {

        return new ComplexFraction(numerator.mul(v),denominator);
        
    }
    public AlgExp mul(ConstantFraction f)
    {

        return new ComplexFraction(numerator.mul(f.getNumerator()),denominator.mul(f.getDenominator()));
        
    }
    public AlgExp mul(SimpleTerm t)
    {

        return new ComplexFraction(numerator.mul(t),denominator);
        
        
    }
    public AlgExp mul(Polynomial e)
    {
        
        return new ComplexFraction(numerator.mul(e),denominator);
    }
    public AlgExp mul(ComplexTerm ct)
    {

        return new ComplexFraction(numerator.mul(ct),denominator).eval();
    }
    public AlgExp mul(ComplexFraction cf)
    {

        return new ComplexFraction(numerator.mul(cf.getNumerator()),denominator.mul(cf.getDenominator()));
    }
    
    
    public AlgExp div(IntConst c)
    {
        return mul(c.invert());
    }
    public AlgExp div(DoubleConst c)
    {
        return mul(c.invert());
    }
    public AlgExp div(Variable v)
    {
        return mul(v.invert());
    }
    public AlgExp div(ConstantFraction f)
    {
        return mul(f.invert());
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
    public AlgExp div(ComplexFraction cf)
    {
        return mul(cf.invert());            }
    public AlgExp invert()
    {
        if(numerator.equals(AlgExp.ONE))
            return denominator;
        return new ComplexFraction(denominator,numerator);
    }
    public String parseRep()
    {
    	
    	return "("+numerator+")"+"/"+"("+denominator+")";
    }
    
    public String toString()
    {
    	
    	String firstTermRep;
    	String secondTermRep;
    	if(numerator.isConstant() && denominator.isConstant())
    		return numerator+"/"+denominator;
    	if(!numerator.isPolynomial())
    		firstTermRep=numerator.toString();
    	else
    		firstTermRep="("+numerator.toString()+")";
    	if(denominator.isSimple() && !(denominator.isSimpleTerm()))
    		secondTermRep=denominator.toString();
    	else
    		secondTermRep="("+denominator.toString()+")";
    	return firstTermRep.concat("/").concat(secondTermRep);
    	
    	
    		
    }
    public boolean equals(IntConst c)
    {
        return denominator.equals(AlgExp.ONE) && numerator.equals(c);
       
    }
    public boolean equals(DoubleConst c)
    {
        return denominator.equals(AlgExp.ONE) && numerator.equals(c);
       
    }
    public boolean equals(Variable v)
    {
        return denominator.equals(AlgExp.ONE) && numerator.equals(v);
    }
    public boolean equals(ConstantFraction f)
    {
        return denominator.equals(f.getDenominator()) && numerator.equals(f.getNumerator());
    }
    public boolean equals(SimpleTerm t)
    {
        return denominator.equals(AlgExp.ONE) && numerator.equals(t);
    }
    public boolean equals(Polynomial e)
    {
        return denominator.equals(AlgExp.ONE) && numerator.equals(e);
    }
    public boolean equals(ComplexTerm ct)
    {
         if(denominator.equals(AlgExp.ONE) && numerator.equals(ct))
             return true;
         return ct.equals(this);
    }
    public boolean equals(ComplexFraction cf)
    {
        
         return denominator.equals(cf.getDenominator()) && numerator.equals(cf.getNumerator());
    }
    /**
     * attempt to evaluate the division, does NOT reduce the fraction
     */
	public AlgExp eval() 
	{
		
		return numerator.eval().div(denominator.eval());
	}
	
	public AlgExp negate()
	{
		return new ComplexFraction(numerator.negate(),denominator);
	}
	
	public boolean hasVariable(String varName) 
	{

		return numerator.hasVariable(varName)||denominator.hasVariable(varName);
	}
	public Set getAllVars() 
	{
		Set vars=new HashSet();
		Set numSet=numerator.getAllVars();
		if(!numSet.isEmpty())
			vars.addAll(numSet);
		Set denSet=denominator.getAllVars();
		if(!denSet.isEmpty())
			vars.addAll(denSet);
		return vars;
	}
    
    public AlgExp divDecimal(IntConst c) {
        new Exception().printStackTrace();
        return null;
    }

    

}
