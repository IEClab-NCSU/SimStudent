package edu.cmu.pact.miss.userDef.algebra.expression;

import java.util.HashSet;
import java.util.Set;




/**
 * a class to represnt a variable with a coeffient
 * @author ajzana
 *
 */
public class SimpleTerm extends AlgExp 
{

protected Constant constant;
protected Variable variable;
public SimpleTerm(AlgExp constant, AlgExp variable) 
{
	this((Constant)constant,(Variable)variable);
	
}
public SimpleTerm(Constant c, Variable v)
{
	variable=v;
	constant=c;
	isTerm=true;
	isSimpleTerm=true;
	hasVariable=true;
	isSimple=true;
	isMonomial=true;
	isNegative=c.isNegative();
	isFraction=c.isFraction();
}
public Constant getConstant()
{
	return constant;
}
public Variable getVariable()
{
	return variable;
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
	if(v.equals(variable))
    {
	    AlgExp newCoe=constant.add(AlgExp.ONE);
        if(newCoe.equals(AlgExp.ZERO))
            return AlgExp.ZERO;
        if(newCoe.equals(AlgExp.ONE))
        	return new Variable(v.getName());
        return new SimpleTerm(newCoe,v);
    }
	else
		return new Polynomial(this,v);

}
public AlgExp add(ConstantFraction f) 
{

	return new Polynomial(this,f);
}
public AlgExp add(SimpleTerm t) 
{
	if(t.getVariable().equals(variable))
	{
		AlgExp newCoe=constant.add(t.getConstant());
		
		if(newCoe.equals(AlgExp.ZERO))
				return AlgExp.ZERO;
		if(newCoe.equals(AlgExp.ONE))
        	return new Variable(variable.getName());
		return new SimpleTerm(newCoe,variable);
	}
		
	else
		return new Polynomial(this, t);
}

public AlgExp mul(IntConst c) 
{
	AlgExp result=c.mul(constant);
	if(result.equals(AlgExp.ZERO))
		return ZERO;
	if(result.equals(AlgExp.ONE))
		return new Variable(variable.getName());
	return new SimpleTerm(result,variable );
}
public AlgExp mul(Variable v) 
{
	return new ComplexTerm(this,v);

}
public AlgExp mul(ConstantFraction f) 
{
	AlgExp result=f.mul(constant);
	if(result.equals(AlgExp.ZERO))
		return ZERO;
	if(result.equals(AlgExp.ONE))
		return new Variable(variable.getName());
	return new SimpleTerm(result,variable);

}
public AlgExp mul(SimpleTerm t) 
{
	AlgExp constantResult=t.mul(constant);
	if(constantResult.equals(AlgExp.ZERO))
		return ZERO;
	if(constantResult.equals(AlgExp.ONE))
		return new Variable(variable.getName());
	
	return new ComplexTerm(constant.mul(t.getConstant()),variable.mul(t.getVariable()));

}

public AlgExp div(IntConst c) {
    // trace.out("SimpleTerm.div(IntConst " + c + ") on " + this);
    // trace.out("constant = " + constant + ", variable = " + variable);
    
    AlgExp constantResult=constant.div(c);
    if(constantResult.equals(AlgExp.ZERO))
        return ZERO;
    if(constantResult.equals(AlgExp.ONE))
        return new Variable(variable.getName());
    return new SimpleTerm(constant.div(c),variable);
}

public AlgExp div(DoubleConst c) 
{

	AlgExp constantResult=constant.div(c);
	if(constantResult.equals(AlgExp.ZERO))
		return ZERO;
	if(constantResult.equals(AlgExp.ONE))
		return new Variable(variable.getName());
	return new SimpleTerm(constant.div(c),variable);
}


public AlgExp div(Variable v) 
{
	if(variable.equals(v))
	{
		try
		{
		return parseExp(constant.toString());
		}
		catch(ExpParseException e)
		{
			e.printStackTrace();
			return null;
		}
	}
		
	return constant.mul(v.mul(variable));
	

}
public AlgExp div(ConstantFraction f) 
{
	return mul(f.invert());
		
}
public AlgExp div(SimpleTerm t) 
{
	if(this.equals(t))
		return AlgExp.ONE;
	if(t.getVariable().equals(variable))
		return constant.div(t.getConstant());
	else
		return constant.div(t.getConstant()).mul(new ComplexFraction(variable,t.getVariable()));
	
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
public AlgExp invert() 
{

	return new ComplexFraction(AlgExp.ONE,this);
}

public boolean equals(SimpleTerm t)
{
//    trace.out("entered SimpleTerm.equals():");
//    trace.out("t.getConstant() = " + t.getConstant());
//    trace.out("constant = " + constant + "; constant.getClass() = " + constant.getClass());
//    trace.out("t.getConstant().equals(constant) = " + t.getConstant().equals(constant) +
//            "; t.getConstant().getClass() =" + t.getConstant().getClass());    
    return t.getConstant().equals(constant) && t.getVariable().equals(variable);
}
public boolean equals (Variable v)
{
    return variable.equals(v)&&constant.equals(AlgExp.ONE);
    
}

public String toString()
{	
	if(constant.equals(AlgExp.NEGONE))
		return "-"+variable;
	if(constant.isFraction())
	{
		ConstantFraction f=(ConstantFraction)constant;
		if(f.getNumerator().equals(AlgExp.ONE))
		{	if(f.getDenominator().isNegative())
				return "-"+variable+"/"+f.getDenominator().negate();
			else
				return variable+"/"+f.getDenominator();
		}
		if(f.getNumerator().equals(AlgExp.NEGONE))
			return "-"+variable+"/"+f.getDenominator();
		
		
		 
	}
	return constant.toString().concat(variable.toString());
}
public boolean equals(IntConst c) 
{
	
	if(c.equals(AlgExp.ZERO)&&constant.equals(AlgExp.ZERO))
		return true;
	return false;
}
public boolean equals(ConstantFraction f) {

	return false;
}
public boolean equals(Polynomial e) {
	return e.equals(this);
}
public boolean equals(ComplexTerm ct) 
{
 
	return ct.equals(this);
}
public boolean equals(ComplexFraction cf) {

	return cf.equals(this);
}
public AlgExp mul(DoubleConst c) 
{

	return new SimpleTerm(constant.mul(c),variable);
}
public boolean equals(DoubleConst c) {

	return false;
}
public AlgExp eval() 
{
	if(constant.isFraction)
	{
		/*trace.out("exp is " + this.parseRep()); //gustavo
		trace.out(".getConstant() = " + this.getConstant()); //gustavo
		trace.out(".getConstant().getVal() = " + this.getConstant().getVal()); //gustavo
		trace.out(".getVariable() = " + this.getVariable()); //gustavo
		trace.out("constant = " + constant); //gustavo
		trace.out("getConstant().isFraction() = " + getConstant().isFraction()); //gustavo*/
		AlgExp newCoe=constant.eval();

		double d1 = getConstant().getVal();
		int i = (int) d1;
		double d2 = (double) i;
		
		//trace.out("i = " + i + "    d1 = " + d1); //gustavo		
		//trace.out("equals = " + (d1==v2));//new Integer(i).equals(new Double(d1)));
		
		if (d1==d2){
			//the fraction can be simplified to an integer value
			if(d1==1)
				return new Variable(variable.getName());
			return new SimpleTerm(new IntConst(d1),variable);
		}
		
		
		if(!newCoe.toString().equals(constant))
		{
			//trace.out("entered if. newCoe = " + newCoe); //gustavo
			if(newCoe.equals(AlgExp.ONE))
				return new Variable(variable.getName());
			return new SimpleTerm(newCoe,variable);
		}
	}
	return this;		
}

		
/*		try{
			int i = (int) getConstant().getVal();
			Constant c = new Constant (i);
			return new SimpleTerm(i,getVariable());
		}
		catch(Exception e){
		*/	

public boolean hasVariable(String varName) {

	return variable.getName().equalsIgnoreCase(varName);
}
public Set getAllVars() {

	
	Set var=new HashSet();
	var.add(variable.getName());
	return var;
	
}

public AlgExp divDecimal(IntConst c) {
    new Exception().printStackTrace();
    return null;
}


}
