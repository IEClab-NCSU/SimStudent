package edu.cmu.pact.miss.userDef.algebra.expression;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;


/**
 * Class to represent a complex term (a multiplication of anything other than one constant and one variable
 * @author ajzana
 *
 */
public class ComplexTerm extends AlgExp 
{
	public ComplexTerm(AlgExp e1, AlgExp e2) 
	{
		firstTerm=e1;
		secondTerm=e2;
		
        isTerm=true;
        hasVariable=firstTerm.hasVariable()||secondTerm.hasVariable();
        isMonomial=firstTerm.isMonomial()&& secondTerm.isMonomial();
        isNegative=firstTerm.isNegative() ^ secondTerm.isNegative();
	}
	protected AlgExp firstTerm;
	protected AlgExp secondTerm;
	
    public AlgExp getFirstTerm()
    {
        return firstTerm;
    }
    public AlgExp getSecondTerm()
    {
        return secondTerm;
    }
    
    public AlgExp commute()
    {
        return new ComplexTerm(secondTerm,firstTerm);
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
        if(e.equals(firstTerm) && secondTerm.isConstant())
        {
            if(secondTerm.equals(AlgExp.NEGONE))
                return AlgExp.ZERO;
           return new ComplexTerm(firstTerm,secondTerm.add(AlgExp.ONE));
            
        
        }
        if(e.equals(secondTerm) && firstTerm.isConstant())
        {
            if(firstTerm.equals(AlgExp.NEGONE))
                return AlgExp.ZERO;
           return new ComplexTerm(firstTerm.add(AlgExp.ONE),secondTerm);
            
        }
        return e.add(this);
    }
    public AlgExp add(ComplexTerm ct)
    {
        if(this.firstTerm.equals(ct.getFirstTerm()))
            return new ComplexTerm(secondTerm.add(ct.getSecondTerm()),firstTerm);
        if(this.firstTerm.equals(ct.getSecondTerm()))
            return new ComplexTerm(secondTerm.add(ct.getFirstTerm()),firstTerm);
        if(this.secondTerm.equals(ct.getFirstTerm()))
            return new ComplexTerm(firstTerm.add(ct.getSecondTerm()),secondTerm);
        if(this.secondTerm.equals(ct.getSecondTerm()))
            return new ComplexTerm(firstTerm.add(ct.getFirstTerm()),secondTerm);
        return new Polynomial(this,ct);
        
        
        
        
    }
    
    public AlgExp mul(IntConst c)
    {

        return doMul(c);
    }
    public AlgExp mul(DoubleConst c)
    {

        return doMul(c);
    }
    
    public AlgExp mul(Variable v)
    {
        return doMul(v);
    }
    /**
     * Multiply this expression with a constant fraction
     * NOTE: currently does not cancel the denominator
     */
    public AlgExp mul(ConstantFraction f)
    {
    	return doMul(f);    
    }
    public AlgExp mul(SimpleTerm t)
    {
        return  doMul(t);
    }
    public AlgExp mul(Polynomial e)
    {
        return new ComplexTerm(this,e);
    }
    public AlgExp mul(ComplexTerm ct)
    {
        return new ComplexTerm(this,ct);
            
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

        return new ComplexFraction(this,v);
    }
    public AlgExp div(ConstantFraction f)
    {
        
        return mul(f.invert());
    }
    public AlgExp div(SimpleTerm t)
    {

        return new ComplexFraction(this,t);
    }
    public AlgExp div(Polynomial e)
    {

        return e.div(this.invert());
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
    
    
    
    
    public AlgExp invert()
    {
        
        
        return new ComplexTerm(AlgExp.ONE,this);
    }
    /**
     * 
     * @return a String showing how this expression was parsed(using parens)
     */
     
     
    public String parseRep()
    {
    	
    	return "("+firstTerm.parseRep()+")"+"("+secondTerm.parseRep()+")";
    }
    public String toString()
    {

    	String firstTermRep;
    	String secondTermRep;
    	
    	
    	
    	if(firstTerm.isConstant() && secondTerm.isConstant())
    		return firstTerm+"*"+secondTerm;
    	if(!firstTerm.isPolynomial())
    	{
    		if(firstTerm.equals(AlgExp.NEGONE))
    			firstTermRep="-";
    		else
    			firstTermRep=firstTerm.toString();
    	}
    	else
    		firstTermRep="("+firstTerm.toString()+")";
    	if(!secondTerm.isPolynomial())
    	{
    		if(secondTerm.equals(AlgExp.NEGONE))
    			secondTermRep="-";
    		else
    			secondTermRep=secondTerm.toString();
    	}
    	else
    		secondTermRep="("+secondTerm.toString()+")";
    	
    	return firstTermRep.concat(secondTermRep);
    	
    	
    		
    }
    public boolean equals(IntConst c)
    {
        if(firstTerm.equals(AlgExp.ONE)&&secondTerm.equals(c))
            return true;
        if(secondTerm.equals(AlgExp.ONE)&&firstTerm.equals(c))
            return true;
        
        return false;
    }
    public boolean equals(DoubleConst c)
    {
        if(firstTerm.equals(AlgExp.ONE)&&secondTerm.equals(c))
            return true;
        if(secondTerm.equals(AlgExp.ONE)&&firstTerm.equals(c))
            return true;
        
        return false;
    }
    public boolean equals(Variable v)
    {

        if(firstTerm.equals(AlgExp.ONE)&&secondTerm.equals(v))
            return true;
        if(secondTerm.equals(AlgExp.ONE)&&firstTerm.equals(v))
            return true;
        
        return false;
    }
    public boolean equals(ConstantFraction f)
    {
        if(firstTerm.equals(AlgExp.ONE)&&secondTerm.equals(f))
            return true;
        if(secondTerm.equals(AlgExp.ONE)&&firstTerm.equals(f))
            return true;
        
        return false;
    }
    public boolean equals(SimpleTerm t)
    {
        if(firstTerm.equals(AlgExp.ONE)&&secondTerm.equals(t))
            return true;
        if(secondTerm.equals(AlgExp.ONE)&&firstTerm.equals(t))
            return true;
        
        return false;
    }
    public boolean equals(Polynomial e)
    {
        if(firstTerm.equals(AlgExp.ONE)&&secondTerm.equals(e))
            return true;
        if(secondTerm.equals(AlgExp.ONE)&&firstTerm.equals(e))
            return true;
        
        return false;
    }
    public boolean equals(ComplexTerm ct)
    {
        
        return firstTerm.equals(ct.firstTerm)&&secondTerm.equals(ct.secondTerm);
    }
    public boolean equals(ComplexFraction cf)
    {
        return this.equals(cf.getNumerator())&&cf.getDenominator().equals(AlgExp.ONE);
    }
    private AlgExp doMul(AlgExp e)
    {
    	if(firstTerm.isPolynomial())
    		return new ComplexTerm(firstTerm.mul(e),secondTerm);
    	if(secondTerm.isPolynomial())
    		return new ComplexTerm(firstTerm,secondTerm.mul(e));
    	if(firstTerm.isSimple())
    		return new ComplexTerm(firstTerm.mul(e),secondTerm);
    	if(secondTerm.isSimple())
    		return new ComplexTerm(firstTerm,secondTerm.mul(e));
    	
    	
    	return new ComplexTerm(firstTerm.mul(e),secondTerm);
    	
    	
    }
	public AlgExp eval() 
	{
		AlgExp result;
		AlgExp lastResult=this;
		do
		{
			
			 result=firstTerm.eval().mul(secondTerm.eval());
			 lastResult=result;
		}
		while(!result.equals(lastResult));
		return result;	
	}	
	/**
	 * @return an AlgExp containing only multiplications simplified
	 */
	public AlgExp evalMul()
	{
		AlgExp newFirst;
		AlgExp newSecond;
		if(firstTerm.isTerm()&&!firstTerm.isSimple())
			newFirst=((ComplexTerm)firstTerm).evalMul();
		else
			newFirst=firstTerm;
		if(secondTerm.isTerm()&&!secondTerm.isSimple())
			newSecond=((ComplexTerm)secondTerm).evalMul();
		else
			newSecond=secondTerm;
		return newFirst.mul(newSecond);
		
	}
	public AlgExp negate()
	{
		if(firstTerm.equals(AlgExp.NEGONE))
			return secondTerm;
		if(secondTerm.equals(AlgExp.NEGONE))
			return firstTerm;
		if(this.isNegative())
		{
			if(firstTerm.isNegative())
				return new ComplexTerm(firstTerm.negate(),secondTerm);
			if(secondTerm.isNegative())
				return new ComplexTerm(firstTerm,secondTerm.negate());
			
			
		}
		if(firstTerm.isNegative()&&secondTerm.isNegative())
			return new ComplexTerm(firstTerm,secondTerm.negate());
		return new ComplexTerm(firstTerm.negate(),secondTerm);
		
		
	}
	public boolean hasVariable(String varName) 
	{

		return firstTerm.hasVariable(varName)||secondTerm.hasVariable(varName);
	}
	public Set getAllVars() 
	{
		Set vars=new HashSet();
		Set s=firstTerm.getAllVars();
		if(!s.isEmpty())
			vars.addAll(s);
		s=secondTerm.getAllVars();
		if(!s.isEmpty())
			vars.addAll(s);
		return vars;
	}
	
	/**
	 * 
	 * @return a Vector containing all the factors of this expression
	 */
	public Vector getFactors()
	{
		Vector factors=new Vector();
		if(firstTerm.isTerm())
		{
			if(firstTerm.isSimpleTerm())
			{
				SimpleTerm t=(SimpleTerm)firstTerm;
				factors.add(t.getConstant());
				factors.add(t.getVariable());
			}
			else
				factors.addAll(((ComplexTerm)firstTerm).getFactors());
				
			
		}
		else
			factors.add(firstTerm);
		
		if(secondTerm.isTerm())
		{
			if(secondTerm.isSimpleTerm())
			{
				SimpleTerm t=(SimpleTerm)secondTerm;
				factors.add(t.getConstant());
				factors.add(t.getVariable());
			}
			else
				factors.addAll(((ComplexTerm)secondTerm).getFactors());
				
			
		}
		else
			factors.add(secondTerm);
		return factors;
	}
    /**
     * 
     * @param factor AlgExp which might be a factor of this expression
     * @return true if this expression contains factor
     */
	
	public boolean containsFactor(AlgExp factor)
	{
		
			if(firstTerm.equals(factor))
				return true;
			if(secondTerm.equals(factor))
				return true;
			
			if(firstTerm.isSimpleTerm())
			{
				SimpleTerm t=(SimpleTerm)firstTerm;
				if(t.getConstant().equals(factor)|| t.getVariable().equals(factor))
					return true;
				
					
			}
			else
			{
				if(firstTerm.isTerm())
				{
					if(((ComplexTerm)firstTerm).containsFactor(factor))
							return true;
				}
			}
			if(secondTerm.isSimpleTerm())
			{
				SimpleTerm t=(SimpleTerm)secondTerm;
				if(t.getConstant().equals(factor)|| t.getVariable().equals(factor))
					return true;
			}
			else
			{
				if(secondTerm.isTerm())
				{
					if(((ComplexTerm)secondTerm).containsFactor(factor))
							return true;
				}
			}
			return false;
			
		
	}

    public AlgExp divDecimal(IntConst c) {
        new Exception().printStackTrace();
        return null;
    }
}
