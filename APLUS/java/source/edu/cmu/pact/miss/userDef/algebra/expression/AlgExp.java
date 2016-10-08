package edu.cmu.pact.miss.userDef.algebra.expression;
import java.util.Set;

/**
 * Abstract class used to represent an arbitrary Algebra Expression
 * @author ajzana
 *
 */
public abstract class AlgExp 
{
	//features of this expression, with false default values, construtor's responsibility to set the true ones
	protected  boolean isFraction=false;
	protected boolean isSimple=false;
    protected boolean isConstant=false;
    protected boolean isVariable=false;
    protected boolean isMonomial=false;
    protected boolean isPolynomial=false;
    protected boolean isSimpleTerm=false;
    protected boolean isTerm=false;
    protected boolean isInt=false;
    protected boolean hasVariable=false;
    protected boolean isNegative=false;
    private boolean parenBit=false; //used only for parsing, ignore otherwise
    
	/**
	 * 
	 * @return true if the string which created this expression was surrounded by parens, ignored otherwise
	 */
	boolean hasParens() 
	{
		return parenBit;
	}
	
	void setParenBit(boolean parenBit) 
	{
		this.parenBit = parenBit;
	}
	/**
	 * 
	 * @return true if this expression is any kind of a constant, false otherwise
	 */
	public boolean isConstant()
	{
		return isConstant;
	}
	/**
	 * 
	 * @return true if this expression is just a variable;
	 */
	public boolean isVariable()
	{
		return  isVariable;
	}
	/**
	 * 
	 * @return true if this expression is a fraction  
	 */
	public boolean isFraction()
	{
		return isFraction;
	}
        
        public boolean isConstantFraction() {
            return getClass().equals(ConstantFraction.class);
        }
        
	/**
	 * 
	 * @return true if this expression is an integer
	 */
	public boolean isInt()
	{
		return isInt;
	}
	/**
	 * 
	 * @return true if this expression is negative
	 */
	public boolean isNegative()
	{
		return isNegative;
	}
	/**
	 * @return true if this expression has a variable
	 */ 
	public  boolean hasVariable()
	{
		return hasVariable;
	}
	
	
		
	
	/**
	 * 
	 * @return true if this expression is a simple term (a varible and a coefficent, false otherwise)
	 */
	public boolean isSimpleTerm()
	{
		return isSimpleTerm;
	}
	/**
	 * 
	 * @return true if this expression is a multiplication
	 */

	public boolean isTerm()
	{
		return isTerm; 
	}
	/**
	 * 
	 * @return true if this expression is simple(a constant, a variable, or the multiplication of a constant and variable)
	 */
	public boolean isSimple()
	{
		 return isSimple;
	}
	
	/**
	 * tests whether this expression is a monomial 
	 * @return
	 */
	public boolean isMonomial()
	{
		return isMonomial;
	}
	/**
	 * 
	 * @return true if this expression is a polynomial
	 */
	public boolean isPolynomial()
    {
	    return isPolynomial;
    }
    /**
     * @return the commuted version of this expression if the operation is commnative, e.g. a+b=b+a, ab=ba, else return this
     */ 
    public AlgExp commute()
    {
        return this;
    }
	
	//add methods	
	public abstract AlgExp add(Variable v);
	public abstract AlgExp add(ConstantFraction f);
	public abstract AlgExp add(SimpleTerm t);
	public abstract AlgExp add(Polynomial e);
	public abstract AlgExp add(ComplexTerm ct);
	public abstract AlgExp add(ComplexFraction cf);
	public abstract AlgExp add(IntConst c);
    public abstract AlgExp add(DoubleConst c);

    //mul methods
	public abstract AlgExp mul(Variable v);
	public abstract AlgExp mul(ConstantFraction f);
	public abstract AlgExp mul(SimpleTerm t);
	public abstract AlgExp mul(Polynomial e);
	public abstract AlgExp mul(ComplexTerm ct);
	
    public abstract AlgExp mul(ComplexFraction cf);
    public abstract AlgExp mul(IntConst c);
    public abstract AlgExp mul(DoubleConst c);

    
    //div methods	
	public abstract AlgExp div(Variable v);
	public abstract AlgExp div(ConstantFraction f);
	public abstract AlgExp div(SimpleTerm t);
	public abstract AlgExp div(Polynomial e);
	public abstract AlgExp div(ComplexTerm ct);
    public abstract AlgExp div(ComplexFraction cf);
    public abstract AlgExp div(IntConst c);
    public abstract AlgExp div(DoubleConst c);
    
    public abstract AlgExp divDecimal(IntConst c);
	
    
    //equals methods
    public abstract boolean equals(IntConst c);
    public abstract boolean equals(DoubleConst c);
	public abstract boolean equals(Variable v);
	public abstract boolean equals(ConstantFraction f);
	public abstract boolean equals(SimpleTerm t);
	public abstract boolean equals(Polynomial e);
	public abstract boolean equals(ComplexTerm ct);
    public abstract boolean equals(ComplexFraction cf);
    
    /**
     * 
     * @return a new expression which represents this expression with all arithmetic evaluated
     */
    
    public abstract AlgExp eval();
    
	/**
	 * 
	 * @return 1/(this)
	 */
    public abstract AlgExp invert();
    
/* ********magic casting methods used to convert a generic AlgExp parameter to a more specific type ****/
	
	public AlgExp add(Constant c)
	{
		if(c.getClass().equals(IntConst.class))
			return add((IntConst)c);
		
		if(c.getClass().equals(DoubleConst.class))
			return add((DoubleConst)c);
		if(c.getClass().equals(ConstantFraction.class))
			return add((ConstantFraction)c);
		throw new RuntimeException("Unknown class in add Constant: "+c.getClass());
	}
	public AlgExp mul(Constant c)
	{
		if(c.getClass().equals(IntConst.class))
			return mul((IntConst)c);
		
		if(c.getClass().equals(DoubleConst.class))
			return mul((DoubleConst)c);
		if(c.getClass().equals(ConstantFraction.class))
			return mul((ConstantFraction)c);
		throw new RuntimeException("Unknown class in mul Constant: "+c.getClass());
	}

	
//	since all the subclasses implement 'add', why don't we just delete this function? Gustavo 20Sep2006
//	except in the case of a zero, this function casts e to the same type as 'this', before adding it.
        public AlgExp add(AlgExp e) 	{
        //System.out.println("e.getClass() = " + e.getClass());
		if(e.equals(AlgExp.ZERO))
			return eval();
		if(e.getClass().equals(Constant.class))
			return add((Constant)e);
		if(e.getClass().equals(SimpleTerm.class))
			return add((SimpleTerm)e);
		if(e.getClass().equals(Variable.class))
			return add((Variable)e);
		if(e.getClass().equals(ConstantFraction.class))
			return add((ConstantFraction)e);
		if(e.getClass().equals(ComplexTerm.class))
			return add((ComplexTerm)e);
		if(e.getClass().equals(Polynomial.class))
			return add((Polynomial)e);
		if(e.getClass().equals(ComplexFraction.class))
            return add((ComplexFraction)e);
		if(e.getClass().equals(IntConst.class))
            return add((IntConst)e);
		if(e.getClass().equals(DoubleConst.class))
            return add((DoubleConst)e);
				
		throw new RuntimeException("Unknown class " +e.getClass().getName() + " in add");
	}
	
	public AlgExp mul(AlgExp e)
	{
		if(e.equals(AlgExp.ONE))
            return eval();  //was: return this;
        if(e.equals(AlgExp.ZERO))
            return AlgExp.ZERO;
        
		if(e.getClass().equals(Constant.class))
			return mul((Constant)e);
		if(e.getClass().equals(SimpleTerm.class))
			return mul((SimpleTerm)e);
		if(e.getClass().equals(Variable.class))
			return mul((Variable)e);
		if(e.getClass().equals(ConstantFraction.class))
			return mul((ConstantFraction)e);
		if(e.getClass().equals(ComplexTerm.class))
			return mul((ComplexTerm)e);
		if(e.getClass().equals(Polynomial.class))
			return mul((Polynomial)e);
	    if(e.getClass().equals(ComplexFraction.class))
            return mul((ComplexFraction)e);
	    if(e.getClass().equals(IntConst.class))
            return mul((IntConst)e);
		if(e.getClass().equals(DoubleConst.class))
            return mul((DoubleConst)e);
				
		throw new RuntimeException("Unknown class " +e.getClass().getName() + " in mul");
	}
	
	// Fri Sep 8 1:43PM :: Noboru
        // div(Constant) must be integrated into div(AlgExp) 
        /*
        public AlgExp div(Constant c) {
            if(c.getClass().equals(IntConst.class))
                return div((IntConst)c);
            if(c.getClass().equals(DoubleConst.class))
                return div((DoubleConst)c);
            if(c.getClass().equals(ConstantFraction.class))
                return div((ConstantFraction)c);
            throw new RuntimeException("Unknown class in div Constant: "+c.getClass());
        }
        */

        public AlgExp divDecimal(AlgExp divident) {
            /*
            System.out.println("divisor class is " + getClass());
            System.out.println("divident class is " + divident.getClass());
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            return (isInt() && divident.isInt()) ? divDecimal((IntConst)divident) : div(divident);
        }
        
	public AlgExp div(AlgExp e) {
	    
            // System.out.println("AlgExp.div(" + e.getClass() + " on " + getClass());
            
	    if(e.equals(AlgExp.ONE))
	        return this;
	    if(e.getClass().equals(Constant.class))
	        return div((Constant)e);
	    if(e.getClass().equals(SimpleTerm.class))
	        return div((SimpleTerm)e);
	    if(e.getClass().equals(Variable.class))
	        return div((Variable)e);
	    if(e.getClass().equals(ConstantFraction.class))
	        return div((ConstantFraction)e);
	    if(e.getClass().equals(ComplexTerm.class))
	        return div((ComplexTerm)e);
	    if(e.getClass().equals(Polynomial.class))
	        return div((Polynomial)e);
	    if(e.getClass().equals(ComplexFraction.class))
	        return div((ComplexFraction)e);
	    if(e.getClass().equals(IntConst.class))
	        return div((IntConst)e);
	    if(e.getClass().equals(DoubleConst.class))
	        return div((DoubleConst)e);
	    // From div(Constant c)
            if(e.getClass().equals(IntConst.class))
                return div((IntConst)e);
            if(e.getClass().equals(DoubleConst.class))
                return div((DoubleConst)e);
            if(e.getClass().equals(ConstantFraction.class))
                return div((ConstantFraction)e);
	    throw new RuntimeException("Unknown class " +e.getClass().getName() + " in div");
	}
	
	public boolean equals(AlgExp e)
	{
		if(e.getClass().equals(Constant.class))
			return equals((Constant)e);
		if(e.getClass().equals(SimpleTerm.class))
			return equals((SimpleTerm)e);
		if(e.getClass().equals(Variable.class))
			return equals((Variable)e);
		if(e.getClass().equals(ConstantFraction.class))
			return equals((ConstantFraction)e);
		if(e.getClass().equals(ComplexTerm.class))
			return equals((ComplexTerm)e);
		if(e.getClass().equals(Polynomial.class))
			return equals((Polynomial)e);
	    if(e.getClass().equals(ComplexFraction.class))
            return equals((ComplexFraction)e);
	    if(e.getClass().equals(IntConst.class))
            return equals((IntConst)e);
		if(e.getClass().equals(DoubleConst.class))
            return equals((DoubleConst)e);
		
		throw new RuntimeException("Unknown class " +e.getClass().getName() + " in equals");
	}
	/**
	 * return this expressio multipled by negative one
	 * @return a new AlgExp
	 */
	public AlgExp negate()
	{
		return mul(AlgExp.NEGONE);
	}
	
	/**
	 * 
	 * @return if applicable, overloaded in subclasses to return 
	 * a string representation with extra parens to show how it was parsed, otherwise return the value of toString()
	 */  
	public String parseRep()
	{
		return toString();
	}
	//constants
	public final static AlgExp ONE=new IntConst("1");
	public final static AlgExp NEGONE=new IntConst("-1");
	public final static AlgExp ZERO=new IntConst("0");
	public final static AlgExp TWO=new IntConst("2");
	/**
	 * Parse a string into an AlgExp and return it
	 * @param s
	 * @return an AlgExp that the String s represents
	 * @throws ExpParseException if the string couldn't be parsed
	 */
		
	public static AlgExp parseExp(String s) throws ExpParseException 
	{
            // -59/x-63-59/x-63
	    return AlgExpParser.parse(s);
	}
	
	public static String cancelDoubleMinus(String s) {
	    	return AlgExpParser.cancelDoubleMinus(s);
	}
	
	/**
	 * @author ajzana
	 * @param varName the variable to search for
	 * @return true if any part of this expression contains that variable, varName
	 */
	public abstract boolean hasVariable(String varName);
	/**
	 * 
	 * @return a Set containing all (unique) variables appearing in this expression, return an empty set if there are none
	 */
	public abstract Set getAllVars();

	
}
