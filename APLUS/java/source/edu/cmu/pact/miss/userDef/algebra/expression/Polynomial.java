package edu.cmu.pact.miss.userDef.algebra.expression;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;





/**
 * Class to represent all additions of AlgExps
 * Note that as of 8-16-06, commutivity is not handled and x+3 and 3+x are different expressions
 * @author ajzana
 *
 */
public class Polynomial extends AlgExp 
{
	protected AlgExp firstHalf;
	protected AlgExp secondHalf;
	

	public Polynomial(AlgExp e1, AlgExp e2) 
	{
		

		
			firstHalf=e1;
			secondHalf=e2;
		
		
		isSimple=false;
        isPolynomial=true;
        
        hasVariable=firstHalf.hasVariable||secondHalf.hasVariable();
		
	}
    public AlgExp commute()
    {
        return new Polynomial(secondHalf,firstHalf);
    }
    /**
     * @return the firstHalf of this polynomial
     */
    public AlgExp getFirstHalf() 
    {

		return firstHalf;
	}
    /**
     * @return the second half of this polynomial
     */
    public AlgExp getSecondHalf() 
    {

		return secondHalf;
	}
    public AlgExp add(IntConst c)
    {
    	return doAdd(c);
    }
    public AlgExp add(DoubleConst c)
    {
    	return doAdd(c);
    }
    public AlgExp add(Variable v)
    {
        	return doAdd(v);
    }
    public AlgExp add(ConstantFraction f)
    {
    	return doAdd(f);
    }
    public AlgExp add(SimpleTerm t)
    {
    	return doAdd(t);
    }
    public AlgExp add(Polynomial e)
    {
    	AlgExp result=add(e.getFirstHalf()).add(e.getSecondHalf());
    	
    	return result;
    }
    
	public AlgExp add(ComplexTerm ct)
    {
		return doAdd(ct);
    }
    
    public AlgExp add(ComplexFraction cf)
    {
        return doAdd(cf);
    }
    public AlgExp mul(IntConst c)
    {
    	return new Polynomial(firstHalf.mul(c),secondHalf.mul(c));
    }
    public AlgExp mul(DoubleConst c)
    {
    	return new Polynomial(firstHalf.mul(c),secondHalf.mul(c));
    }
    
    public AlgExp mul(Variable v)
    {
    	return new Polynomial(firstHalf.mul(v),secondHalf.mul(v));
    }
    public AlgExp mul(ConstantFraction f)
    {
    	return new Polynomial(firstHalf.mul(f),secondHalf.mul(f));
    }
    public AlgExp mul(SimpleTerm t)
    {
    	return new Polynomial(firstHalf.mul(t),secondHalf.mul(t));    
    }
	
    public AlgExp mul(Polynomial e)
    {     
        return new ComplexTerm(this,e);
    }
    public AlgExp mul(ComplexTerm ct)
    {
        return ct.mul(this);
    }
    
    public AlgExp mul(ComplexFraction cf)
    {
    	
        return cf.mul(this);
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
    	if(e.equals(this))
    		return AlgExp.ONE;
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
    public AlgExp invert()
    {

        return new ComplexFraction(AlgExp.ONE,this);
    }
    public String parseRep()
    {
    	return "("+firstHalf+")"+"+"+"("+secondHalf+")";
    }
    public String toString()
    {
    	String firstRep= firstHalf.toString();
    	String secondRep=secondHalf.toString();
    	
    	if(secondRep.charAt(0)=='-')
    		return firstRep.toString()+secondRep.toString();
    	else
    		return firstRep+"+"+secondRep;
    	
    
    }
    public int getNumTerms()
    {
    	return getNumTerms(this);
    }
    
    
    private int getNumTerms(AlgExp e) 
    {
    	if(!e.isPolynomial())
    		return 1;
    	Polynomial p=(Polynomial)e;
    	return  getNumTerms(p.getFirstHalf())+getNumTerms(p.getSecondHalf());
	}
	private AlgExp doAdd(AlgExp e)
    {
    	//recursive function to add one expression to the polynomial without simplifying anything else
    	AlgExp result;

    	
    	
    		result=secondHalf.add(e);
    		if(!result.isPolynomial())
            {
                if(result.equals(AlgExp.ZERO))
                {
                	if(firstHalf.isPolynomial())
                		
                		return ((Polynomial)firstHalf);
                	else
                		return firstHalf;
                }
                return new Polynomial(firstHalf,result);
            }
    		
    		
        	if(getNumTerms(result)==getNumTerms(secondHalf))
        		return new Polynomial(firstHalf,result);
        	
    	
    	
    		result=firstHalf.add(e);
    		if(!result.isPolynomial())
            {
                if(result.equals(AlgExp.ZERO))
                    
                {
                	if(secondHalf.isPolynomial())
                		
                		return ((Polynomial)secondHalf);
                	else
                		return secondHalf;
                }
                return new Polynomial(result,secondHalf);
                
            }
    		
    		if(getNumTerms(result)==getNumTerms(firstHalf))
        		return new Polynomial(result,secondHalf);
        		
    		return new Polynomial(this,e);
    	
    }
	public boolean equals(IntConst c) 
	{
		return false;
	}
	public boolean equals(DoubleConst c) 
	{
		return false;
	}
	public boolean equals(Variable v) 
	{
	
		return false;
	}
	public boolean equals(ConstantFraction f) 
	{
	
		return false;
	}
	public boolean equals(SimpleTerm t) 
	{
	
		return false;
	}
	public boolean equals(Polynomial e) 
	{

		return (firstHalf.equals(e.getFirstHalf())&&secondHalf.equals(e.getSecondHalf()));
	}
	public boolean equals(ComplexTerm ct) 
	{
		
		return false;
	}
	public boolean equals(ComplexFraction cf) 
	{
	
		return false;
	}
	/**
	 * @return a finally simplified version of this AlgExp
	 */
	public AlgExp eval() {
	    
	    AlgExp result;
	    AlgExp lastResult=this;
	    do
	    {
	        result=firstHalf.eval().add(secondHalf.eval());
	        lastResult=result;
	    }
	    while(!result.equals(lastResult));
	    return result;	
	}
	/**
	 * @return an AlgExp with just the addition of this expression evaluated
	 */
	public AlgExp evalAdd() {

	    // System.out.println("evalAdd:: " + firstHalf + " | " + secondHalf);
	    AlgExp newFirst;
	    AlgExp newSecond;
	    if(firstHalf.isPolynomial())
	        newFirst=((Polynomial)firstHalf).evalAdd();
	    else
	        newFirst=firstHalf.eval();
	    if(secondHalf.isPolynomial())
	        newSecond=((Polynomial)secondHalf).evalAdd();
	    else
	        newSecond=secondHalf.eval();
	    return newFirst.add(newSecond);
	}
	
	
	/**
	 * 
	 * @return the first variable term(the entire term, if complex) in this polynomial or null if it has none
	 */
	public AlgExp getFirstVarTerm()
	{
	
		if(firstHalf.isPolynomial())
		{
			AlgExp result=((Polynomial)firstHalf).getFirstVarTerm();
			if(result!=null)
				return result;
		}
		if(secondHalf.isPolynomial())
		{
			AlgExp result=((Polynomial)secondHalf).getFirstVarTerm();
			if(result!=null)
				return result;
		}
		if(firstHalf.hasVariable)
			return firstHalf;
		if(secondHalf.hasVariable())
			return secondHalf;
		return null;
		
		
		
		
	}
	


	/**
	 * 
	 * @return the last variable term(the entire term if it is complex) in this polynomial or null if it has none
	 */
	public AlgExp getLastVarTerm()
	{

		
		if(secondHalf.isPolynomial())
		{
			AlgExp result=((Polynomial)secondHalf).getLastVarTerm();
			if(result!=null)
				return result;
		}
		if(secondHalf.hasVariable())
			return secondHalf;
		if(firstHalf.isPolynomial())
		{
			AlgExp result=((Polynomial)firstHalf).getLastVarTerm();
			if(result!=null)
				return result;
		}
		
		if(firstHalf.hasVariable())
			return firstHalf;
		return null;
		
		
		
		
	}
	/**
	 *  @return the first term in this polynomial
	 */
	public AlgExp getFirstTerm()
	{
		if(firstHalf.isPolynomial())
		{
			AlgExp result=((Polynomial)firstHalf).getFirstTerm();
			if(result!=null)
				return result;
		}
		if(secondHalf.isPolynomial())
		{
			AlgExp result=((Polynomial)secondHalf).getFirstTerm();
			if(result!=null)
				return result;
		}
		return firstHalf;
		
		
		
	
	}
	/**
	 *  @return the last term in this polynomial
	 */
	public AlgExp getLastTerm()
	{

		
		if(secondHalf.isPolynomial())
		{
			AlgExp result=((Polynomial)secondHalf).getLastTerm();
			if(result!=null)
				return result;
		}
		else
		
			return secondHalf;
		if(firstHalf.isPolynomial())
		{
			AlgExp result=((Polynomial)firstHalf).getLastTerm();
			if(result!=null)
				return result;
		}
		
		return null;
		
		
		
		
	}
	/**
	 * @return the last simple constant in this expression or null if it has none
	 */
	public AlgExp getLastConstTerm() 
	{
		
		if(secondHalf.isPolynomial())
		{
			AlgExp result=((Polynomial)secondHalf).getLastConstTerm();
			if(result!=null)
				return result;
		}
		if(secondHalf.isConstant())
			return secondHalf;
		if(firstHalf.isPolynomial())
		{
			AlgExp result=((Polynomial)firstHalf).getLastConstTerm();
			if(result!=null)
				return result;
		}
		
		if(firstHalf.isConstant())
			return firstHalf;
		return null;

	}
	public AlgExp negate()
	{
		return new ComplexTerm(AlgExp.NEGONE,this);
	}
	/**
	 * 
	 * @return true if one of the terms in this polynomial is a constant false otherwise
	 */
	public boolean hasConstTerm()
	{
		if(secondHalf.isConstant())
			return true;
		if(firstHalf.isConstant())
			return true;
		boolean firstHasConst=false;
		boolean secondHasConst=false;
		if(secondHalf.isPolynomial())
			secondHasConst=((Polynomial)secondHalf).hasConstTerm();
		if(secondHasConst)
			return true;
			
		if(firstHalf.isPolynomial())
			firstHasConst=((Polynomial)firstHalf).hasConstTerm();
		if(firstHasConst)
			return true;
		return false;
	}
	public boolean hasVariable(String varName) 
	{

		return firstHalf.hasVariable(varName)||secondHalf.hasVariable(varName);
	}
	public Set getAllVars() 
	{
		Set vars=new HashSet();
		Set firstSet=firstHalf.getAllVars();
		if(!firstSet.isEmpty())
			vars.addAll(firstSet);
		Set secondSet=secondHalf.getAllVars();
			if(!secondSet.isEmpty())
				vars.addAll(secondSet);
		
		return vars;
	}

	/**
	 * used to order polymials in parsing 
	 * @param exp1 an AlgExp
	 * @param exp2 an AlgExp
	 * @return return true if exp1 should come before exp2 in a polynomial
	 */
	protected static boolean comesBefore(AlgExp exp1,AlgExp exp2)
	{
		if(exp1.hasVariable() && !exp2.hasVariable())
			return true;
		if(exp2.hasVariable() && !exp1.hasVariable())
			return false;
		if(exp1.isPolynomial())
			return true;
		if(exp2.isPolynomial())
			return true;
		
		//covert variables to simple terms for easy comparison
		if(exp1.isVariable())
			exp1=new SimpleTerm(AlgExp.ONE,(Variable)exp1);
		if(exp2.isVariable())
			exp2=new SimpleTerm(AlgExp.ONE,(Variable)exp2);
		if(exp1.isSimpleTerm() && exp2.isSimpleTerm())
		{
			SimpleTerm t1=(SimpleTerm)exp1;
			SimpleTerm t2=(SimpleTerm)exp2;
			if(t1.getVariable().equals(t2.getVariable()))
			{
				return t1.getConstant().getVal() > t2.getConstant().getVal();
			}
			else
				return t1.getVariable().getName().compareTo(t2.getVariable().getName())<1;
		}
		return true;
		
	}
	public boolean containsTerm(AlgExp term)
	{
		if(term.isPolynomial())
			return false;
		if(firstHalf.equals(term))
			return true;
		if(secondHalf.equals(term))
			return true;
		if(firstHalf.isPolynomial())
		{
			if(((Polynomial)firstHalf).containsTerm(term))
				return true;
		}
		if(secondHalf.isPolynomial())
		{
			if(((Polynomial)secondHalf).containsTerm(term))
				return true;
		}
		return false;
		
	
		
	}
	/**
	 * 
	 * @return a vector of all terms in this expression
	 */
	public  Vector getAllTerms()
	{
		Vector terms=new Vector();
		if(firstHalf.isPolynomial())
			terms.addAll(((Polynomial)firstHalf).getAllTerms());
		else
			terms.add(firstHalf);
		if(secondHalf.isPolynomial())
			terms.addAll(((Polynomial)secondHalf).getAllTerms());
		else
			terms.add(secondHalf);
		return terms;
	}
    
    public AlgExp divDecimal(IntConst c) {
        new Exception().printStackTrace();
        return null;
    }
}
