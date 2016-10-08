package edu.cmu.pact.miss.userDef.algebra.expression;
import mylib.MathLib;


/**
 * A class representing a fraction where both the numerator and denominator are constants
 * @author ajzana
 *
 */
public class ConstantFraction extends Constant {

	protected Constant Numerator;
	protected Constant Denominator;
	public ConstantFraction(Constant n, Constant d) 
	{
		Numerator=n;
		Denominator=d;
		isNegative=Numerator.isNegative() ^ Denominator.isNegative();
		isFraction=true;
	}
	 ConstantFraction(AlgExp n, AlgExp d) 
	 {
		 this((Constant)n,(Constant)d);
	}
	public double getVal() 
	{
		
		return Numerator.getVal()/Denominator.getVal();
	}
	public Constant getNumerator() 
	{

		return Numerator;
	}
	public Constant getDenominator() 
	{

		return Denominator;
	}
	public String toString()
	{
		
		if(Denominator.isNegative() && !Numerator.isNegative())
			return Numerator.negate()+"/"+Denominator.negate();
		return Numerator+"/"+Denominator;
		
	}
	
	public AlgExp add(ConstantFraction c) 
	{
		Constant d1=Denominator;
		Constant d2=c.getDenominator();
		if(d1 instanceof IntConst && d2 instanceof IntConst)
		{
		
			int d1value=(int)d1.getVal();
			int d2value=(int)d2.getVal();
			
			
			
			
			int commonDenominator=MathLib.lcm(d1value,d2value);
			
			
			int multiplier1=commonDenominator/d1value;
			int multiplier2=commonDenominator/d2value;
			int n1value=(int)Numerator.getVal();
			int n2value=(int)c.getNumerator().getVal();
			int newNValue=n1value*multiplier1+n2value*multiplier2;
			
			
			return new ConstantFraction(new IntConst(newNValue),new IntConst(commonDenominator)).reduce();
		}
		else
			return new Polynomial(this,c);
			
			
		
	}
	
	
	public AlgExp mul(ConstantFraction c) 
	{
		return new ConstantFraction(Numerator.mul(c.getNumerator()),Denominator.mul(c.getDenominator())).reduce();
	}

	
	
	
	
	public AlgExp invert()
	{
		if(Numerator.equals(AlgExp.ONE))
			return Denominator;
		return new ConstantFraction(Denominator,Numerator);
	}
	

	
	
	public boolean equals(ConstantFraction f) 
	{
		AlgExp thisSimple=this.reduce();
		AlgExp fSimple=f.reduce();
		if(thisSimple.isFraction() && fSimple .isFraction())
		{
			ConstantFraction f1=(ConstantFraction)thisSimple;
			ConstantFraction f2=(ConstantFraction)fSimple;
			return f1.getNumerator().equals(f2.getNumerator())&&f1.getDenominator().equals(f2.getDenominator());
		}
			
		return thisSimple.equals(fSimple);
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
	public boolean equals(IntConst c) 
	{
		AlgExp reduced=this.reduce();
		if(!reduced.isFraction())
			return reduced.equals(c);
		return false;
	}
	
	public boolean equals(DoubleConst c) 
	{
	
		
		AlgExp reduced=this.reduce();
		if(!reduced.isFraction())
			return reduced.equals(c);
		return DoubleConst.doubleEq(getVal(),c.getVal());
		
	}

	public boolean equals(Variable v) {

		return false;
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
	public AlgExp mul(IntConst c) 
	{
        int d1Val=(int)Denominator.getVal();
        int d2Val=(int)c.getVal();
        if(d1Val % d2Val==0) 
        {
            int div=d1Val/d2Val;
            if(div==1)
                return Numerator;
            else
                return new ConstantFraction(Numerator,new IntConst(div));
            
        }
        if(d2Val % d1Val==0)
        {
            int div=d2Val/d1Val;
            
            
                return Numerator.mul(new IntConst(div));
            
        }
        

		return new ConstantFraction(Numerator.mul(c),Denominator);
	}
	public AlgExp mul(DoubleConst c) 
	{
		return new ConstantFraction(Numerator.mul(c),Denominator);
		
	}
	public AlgExp div(IntConst c) 
	{
		return mul(c.invert());
		
	}
	public AlgExp div(DoubleConst c) 
	{
		return mul(c.invert());
	}
	public AlgExp add(IntConst c) 
	{
	
		return new Polynomial(this,c);
	}
	public AlgExp add(DoubleConst c) 
	{

		return new Polynomial(this,c);
	}
        
	public AlgExp eval() {

            /*
            System.out.println("Numerator = " + Numerator.getVal() + ", Denominator = " + Denominator);
            System.out.println("Numerator.isInt() = " + Numerator.isInt() + ", Denominator.isInt() = " + Denominator.isInt());
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            
            if (!Numerator.isInt() || !Denominator.isInt()) 
                return this;
            
            int n = (int)Numerator.getVal();
            int d = (int)Denominator.getVal();
            if (n % d == 0) 
                return new IntConst(n/d);

            /*
            AlgExp div=Numerator.div(Denominator);
            if(div.isInt())
                return div;
             */
            
            return this.reduce();
	}
	
	public AlgExp reduce() {
            
            AlgExp reduce = null;
            
	    if(Denominator.equals(AlgExp.ONE)) {
                // return Numerator;
                reduce = Numerator;

            } else if(Numerator.isInt() && Denominator.isInt()) {

                int nValue=(int)((IntConst)Numerator).getVal();
	        int dValue=(int)((IntConst)Denominator).getVal();
	        int gcd=MathLib.gcd(nValue,dValue);
                // System.out.println("GCD = " + gcd);
                
	        if(gcd==1) {
	            // return this;
                    reduce = this;
                } else {
                    int newNValue=nValue/gcd;
                    int newDValue=dValue/gcd;

                    if (newDValue == 1) {
                        // return new IntConst(newNValue);
                        reduce = new IntConst(newNValue);
                    } else {
                        
                        // return new ConstantFraction(new IntConst(nValue),new IntConst(dValue));
                        reduce = new ConstantFraction(new IntConst(newNValue),new IntConst(newDValue));
                    }
                }

            } else if(Numerator.isFraction() || Denominator.isFraction()) {
                
	        // return Numerator.div(Denominator);
                reduce = Numerator.div(Denominator);

            } else {
                
                // return this;
                reduce = this;
            }
            
            // System.out.println("reduce(" + Numerator + "/" + Denominator + ") = " + reduce);
            return reduce;
	}
        
    public AlgExp divDecimal(IntConst c) {
        new Exception().printStackTrace();
        return null;
    }

}
