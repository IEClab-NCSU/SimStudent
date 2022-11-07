package edu.cmu.pact.miss.userDef.algebra;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import mylib.MathLib;
import cl.utilities.sm.BadExpressionError;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.FeaturePredicate;
import edu.cmu.pact.miss.InquiryClSolverTutor;
import edu.cmu.pact.miss.RhsGoalTest;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ComplexFraction;
import edu.cmu.pact.miss.userDef.algebra.expression.ComplexTerm;
import edu.cmu.pact.miss.userDef.algebra.expression.Constant;
import edu.cmu.pact.miss.userDef.algebra.expression.ConstantFraction;
import edu.cmu.pact.miss.userDef.algebra.expression.DoubleConst;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;
import edu.cmu.pact.miss.userDef.algebra.expression.IntConst;
import edu.cmu.pact.miss.userDef.algebra.expression.Polynomial;
import edu.cmu.pact.miss.userDef.algebra.expression.SimpleTerm;
import edu.cmu.pact.miss.userDef.algebra.expression.Variable;

public abstract class EqFeaturePredicate extends FeaturePredicate {

	private static final long serialVersionUID =  5920731597367735107L;
	
    // the idea is to pass these strings to AlgExpParser, which will set them.
    //
    public String s, sLO, sRO;

    private final static float OLD_ALLOWANCE = (float) 0.01;

    private final static float ALLOWANCE = (new Float(DoubleConst.EPISILON)).floatValue();

    private final static String LIST_SEPARATOR = " ";

    // There skills were once treated as only skills that take operand, but 
    // then it turned out that Algebra I data in DataShop had been changed and 
    // operandArithmeticSkills all take an operand
    public static String[] basicArithmeticSkills = {
        "add", "divide", "subtract", "multiply"
    };
    public static String[] operandArithmeticSkills = {
        "add", "divide", "subtract", "multiply", "clt", "mt", "distribute", "rf"
    };

    //these are the names of skills
    public static String[] validSimpleSkillNames = {
        "add", "clt", "combine", "divide", "rf", "subtract", "mt", "distribute", "rds",
        "aproot", "ivm", "multiply", "done"
    };
    public static String[] validSimplificationSkillNames = {
        "clt", "rf", "mt", "rds", "distribute"
    };

    public static boolean isBasicArithmeticSkill(String name) {
        return isListMember(name, basicArithmeticSkills);
    }
    
    public static boolean isOperandArithmeticSkill(String name) {
        return isListMember(name, operandArithmeticSkills);
    }
    
    public static boolean isValidSimpleSkill(String exp) {
        // see if "exp" is in the VALID_SIMPLE_SKILL
        return isListMember(exp, validSimpleSkillNames);
    }

    public static boolean isValidSimplificationSkill(String skill) {
        return isListMember(skill, validSimplificationSkillNames);
    }

    public static boolean isListMember(String name, String[] list) {
        boolean isListMember = false;
        if (name != null) {
            for(int i=0; i < list.length; i++) {
                if (name.equals(list[i])) {
                    isListMember = true;
                    break;
                }
            }
        }
        return isListMember;
    }

    static public boolean isArithmeticExpression( String exp ) {    
    	if (exp.length() < 1)
    		return false;
        if (exp.toUpperCase().indexOf("FALSE") > -1) 
            return false; 
        if (exp.indexOf(' ') > -1) 
            return false; 
        if (isValidSimpleSkill(exp)) 
            return false;
        if (isExprList(exp)) 
            return false;
        return true;
    }

    public static boolean isSkillOperand(String exp) {
        boolean isSkillOperand = false;
        int spaceIdx = exp.indexOf(' ');
        if (spaceIdx > 0) {
            String[] token = exp.split(" ");
            if (token.length == 2) {
            	if (isValidSimpleSkill(token[0]) && isArithmeticExpression(token[1])) {
            		isSkillOperand = true;
            	}
            }
        }
        return isSkillOperand;
    }

    public static String getSkillOperandSkill(String skillOperand) {
        String skill = null;
        if (isSkillOperand(skillOperand)) {
            skill = skillOperand.split(" ")[0];
        }
        return skill;
    }

    public static String getSkillOperandOperand(String skillOperand) {
        String operand = null;
        if (isSkillOperand(skillOperand)) {
            operand = skillOperand.split(" ")[1];
        }
        return operand;
    }

    //this function decides whether s is a list of expressions
    public static boolean isExprList (String s){

    	boolean isExprList = false;
    	
    	if (s != null && s.length()>0 && s.charAt(0)=='[' && s.charAt(s.length()-1)==']')
    		isExprList = true;
    	
    	return isExprList;
    }

    public String subTerm(String expString1,String expString2) {

    	// too ad-hoc, but for subTerm("-10/-10y", "-10/-10y") 
        if (expString1.equalsIgnoreCase(expString2)) {
        	return "0";
        }
    	
    	String subTerm = null;
        
        if (isArithmeticExpression(expString1) && isArithmeticExpression(expString2)) {
            String negExpString2 = reverseSign(expString2);
//            trace.out("negExpString2 = " + negExpString2);
            subTerm = addTerm(expString1,negExpString2);
        }
        return subTerm;
    
    }
    
    /**
     * 
     * @param expString1 a string representing an AlgExp
     * @param expString2 a String representing an AlgExp
     * @return the sum of those expressions, with any other additions simplified as well
     */
    public String addTerm(String expString1,String expString2) {

        String addTerm = null;

        if (isArithmeticExpression(expString1) && isArithmeticExpression(expString2)) {
            try {
                AlgExp e1=AlgExp.parseExp(expString1);
//                try{
//                    ComplexTerm ct1 = (ComplexTerm) e1;
//                }
//                catch (Exception e) {
//                }
                AlgExp e2=AlgExp.parseExp(expString2);
//                trace.out("e1 = " + e1 + ", e2 = " + e2);
                AlgExp result=e1.add(e2);

                if (result.isPolynomial()) {
                    addTerm = ((Polynomial)result).evalAdd().toString();
                } else {
                    addTerm = result.toString();
                }

            } catch(ExpParseException e) {
                e.printStackTrace();
            }
        }
        return addTerm;
    }


    public ComplexTerm[] getTerms(Polynomial p){
        ComplexTerm[] result = null;
        return result;
    }

    public Polynomial makePolynomial (){
        return null;
    }    


    /**
     * RESTRICTIONS: returns null in the following cases
     * -when one of the expressions is a complex fraction
     * -when performing the multiplication would create a second-order term
     *  -Does not cancel denominators of fractions
     * @param expString1 a string representing an AlgExp
     * @param expString2 a String representing an AlgExp
     * @return the product of those expressions, with any other multiplication simplified as well
     */
    public String mulTerm(String expString1, String expString2) {

        if (!isArithmeticExpression(expString1) || !isArithmeticExpression(expString2))
            return null;

        String mulTerm = null;

        try {
            AlgExp e1=AlgExp.parseExp(expString1);
            AlgExp e2=AlgExp.parseExp(expString2);

            if (e1.isPolynomial() || e2.isPolynomial()) return null;
            
            if (e1.isFraction()) {
            	AlgExp d1 = ((ComplexFraction)e1).getDenominator();
            	AlgExp n1 = ((ComplexFraction)e1).getNumerator();
//            	trace.out("d1 is " + d1.getClass() + ", n1 is " + n1.getClass());
//            	trace.out("isSimple(d1) " + d1.isSimple() + ", isSimple(n1) " + n1.isSimple());
            	if (!d1.isSimple() || !n1.isSimple()) return null;
            }
            if (e2.isFraction()) {
            	AlgExp d2 = ((ComplexFraction)e2).getDenominator();
            	AlgExp n2 = ((ComplexFraction)e2).getNumerator();
            	if (!d2.isSimple() || !n2.isSimple()) return null;
            }

            //check if they have the same variable
            if(e1.hasVariable() && e2.hasVariable()) {
                Object[] vars1=e1.getAllVars().toArray();
                Object[] vars2=e2.getAllVars().toArray();

                for(int outer=0; outer < vars1.length; outer++)
                    for(int inner=0; inner < vars2.length; inner++)
                    	if(!((String)vars1[outer]).equalsIgnoreCase((String)vars2[inner]))
                            return null;
            }

            AlgExp result = e1.mul(e2);
            // trace.out("result = " + result);
            
            if(result instanceof ComplexTerm) {
            	mulTerm = ((ComplexTerm)result).evalMul().toString();
            } else {
            	mulTerm = evalArithmetic(result.toString());
            	// mulTerm = result.toString();
            }

        } catch(ExpParseException e) {

            //trace.out("================= EXCEPTION CAUGHT ====================");
            trace.out("mulTerm(" + expString1 + "," + expString2 + ")");
            if (RhsGoalTest.getGoalTest() == null) {
            	trace.out("!! Uncomment setGoalTest at RhsState.evalExp() to read current GoalTest!!");
            } else {
            	trace.out(RhsGoalTest.getGoalTest());
            }
            if (RhsGoalTest.getRhsState() == null) {
            	trace.out("!! Uncomment setGoalTest at RhsExhaustiveGoalTest.isGoalState() and RhsGoalTest.isGoalState to read current RhsState!!");
            } else {
            	trace.out(RhsGoalTest.getRhsState());
            }
            //trace.out("--- STACK TRACE ---");
            e.printStackTrace();

            try {
                System.in.read();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return mulTerm;
    }

    /**
     * restrictions: returns null in the following cases
     * -when one of the expressions is a complex fraction
     * -when both expressions are complex
     *  -always checks if the expression
     * @param expString1 a string representing an AlgExp
     * @param expString2 a String representing an AlgExp
     * @return the quotient of those expressions, with any other division simplified as well
     */
    public String divTerm(String expString1, String expString2) {

        if (!isArithmeticExpression(expString1) || !isArithmeticExpression(expString2)) {
            return null;
        }

        try {
            //27Nov2006
            expString1 = evalArithmetic(expString1);
            expString2 = evalArithmetic(expString2);

            AlgExp e1=AlgExp.parseExp(expString1);
            AlgExp e2=AlgExp.parseExp(expString2);
            
            if (e1.equals(AlgExp.ZERO)) return "0";
            if (e1.equals(e2)) return "1";
            if (e1.isPolynomial()) return null;
            if (!e2.isSimple() || e2.equals(AlgExp.ZERO)) return null;

            // trace.out("divTerm:: e1 = " + e1.getClass() + ", e2 = " + e2.getClass());
            return e1.div(e2).toString();

        } catch(ExpParseException e) {
            trace.out("divTerm(" + expString1 + "," + expString2 + ")...");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            trace.out("divTerm(" + expString1 + "," + expString2 + ")...");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the quotient in decimal form 
     * 
     * @param divisor
     * @param divident
     * @return a string representation the decimal quotient
     */
    public String divTermDecimal(String divisor, String divident) {

        if (!isArithmeticExpression(divisor) || !isArithmeticExpression(divident)) {
            return null;
        }

        String divDecimal = null; 

        AlgExp div1, div2;
        try {
            div1 = AlgExp.parseExp(divisor);
            div2 = AlgExp.parseExp(divident);
            divDecimal = div1.divDecimal(div2).toString();
        } catch (ExpParseException e) {
            e.printStackTrace();
        }

        return divDecimal;
    }

    public static String coefficient( String expString ) {

    	if (!isArithmeticExpression(expString)) {
            return null;
        }

        try {

            AlgExp exp = AlgExp.parseExp( expString );
            if(exp.isVariable())
                return "1";
            if(exp.isTerm()) {
                if(exp.isSimpleTerm()){
                    return ((SimpleTerm)exp).getConstant().toString();
                }
                else{
                	return coefficient(((ComplexTerm)exp).getFirstTerm().toString());
                }
            }
            return null;

        } catch (ExpParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @param expString a string representing an AlgExp
     * @return if expString is a polynomial and contains a variable term, 
     * return the first such term, otherwise return null
     */
    public String firstVarTerm( String expString ) {

        // trace.out("firstVarTerm(" + expString + ") ...");

        String firstVarTerm = null;

        if (isArithmeticExpression(expString)) {
            try {

                AlgExp exp = AlgExp.parseExp( expString );

                if(exp.isPolynomial()) {
                    Polynomial p=(Polynomial)exp;
                    AlgExp varTerm = p.getFirstVarTerm();
                    if (varTerm != null) {
                        firstVarTerm = varTerm.toString();
                    }
                }

            } catch(ExpParseException e) {
                e.printStackTrace();
            }
        }

        // trace.out("firstVarTerm(" + expString + ") = " + firstVarTerm);

        return firstVarTerm;
    }

    /**
     * 
     * @param expString a string representing an AlgExp
     * @return if expString is a polynomial and contains a variable term, return the last such term, otherwise return null
     */
    public String lastVarTerm( String expString ) {

        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try {
            AlgExp exp = AlgExp.parseExp( expString );

            if(exp.isPolynomial()) {
                Polynomial p=(Polynomial)exp;
                AlgExp term = p.getLastVarTerm();
                if (term != null)
                    return term.toString();
            }
            return null;

        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }

    }


    /** @param expString a string representing an AlgExp
     * @return if expString is a polynomial  return the first term, otherwise return null
     */
    public String firstTerm( String expString ) 
    {

        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try 
        {
            AlgExp exp = AlgExp.parseExp( expString );

            if(exp.isPolynomial())
            {
                Polynomial p=(Polynomial)exp;
                AlgExp term = p.getFirstTerm();
                if (term != null)
                    return term.toString();
            }
            return null;

        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    /** @param expString a string representing an AlgExp
     * @return if expString is a polynomial  return the last term, otherwise return null
     */
    public String lastTerm( String expString ) {

        // trace.out("lastTerm(" + expString + ")");

        String lastTerm = null;

        if (isArithmeticExpression(expString)) {
            try 
            {
                AlgExp exp = AlgExp.parseExp( expString );
                // -59/x-63-59/x-63

                // trace.out("exp = " + exp);

                if(exp.isPolynomial())
                {
                    Polynomial p=(Polynomial)exp;
                    AlgExp term = p.getLastTerm();
                    if (term != null) 
                        lastTerm = term.toString();
                }
            }
            catch(ExpParseException e)
            {
                e.printStackTrace();
            }
        }

        return lastTerm;
    }


    /**
     * @return the last constant in the expression if it is a polynomial and has a constant as a term, null otherwise
     */
    public String lastConstTerm( String expString ) {

        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try 
        {
            AlgExp exp = AlgExp.parseExp( expString );

            if(exp.isPolynomial())
            {
                Polynomial p=(Polynomial)exp;
                AlgExp constant= p.getLastConstTerm();
                if(constant!=null)
                    return constant.toString();
            }
            return null;

        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 
     * @param expString1 a string respresenting an AlgExp
     * @param expString2 a string respresenting an AlgExp
     * @return a String representing the expression expString1+expString2
     */
    public String addTermBy(String expString1,String expString2)
    {
        if (!isArithmeticExpression(expString1) || !isArithmeticExpression(expString2)) {
            return null;
        }

        String withOp;
        if(expString2.charAt(0)=='-')
            withOp=expString2;
        else
            withOp="+"+expString2;
        return expString1+withOp;
    }


    /**
     * 
     * @param expString1 a string respresenting an AlgExp
     * @param expString2 a string respresenting an AlgExp
     * @return a String representing the expression expString1*expString2
     */
    public String mulTermBy(String expString1,String expString2) {

        if (!isArithmeticExpression(expString1) || !isArithmeticExpression(expString2)) {
            return null;
        }

        try
        {
            AlgExp exp1=AlgExp.parseExp(expString1);
            AlgExp exp2=AlgExp.parseExp(expString2);
            String rep1=expString1;
            String rep2=expString2;
            if(exp1.isPolynomial()||exp1.isFraction())
                rep1="("+expString1+")";
            if(exp2.isPolynomial()||exp2.isFraction())
                rep2="("+expString2+")";

            return rep1+"*"+rep2;

        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 
     * @param expString1 a string respresenting an AlgExp
     * @param expString2 a string respresenting an AlgExp
     * @return a String representing the expression expString1/expString2
     */
    public String divTermBy(String expString1,String expString2)
    {

        if (!isArithmeticExpression(expString1) || !isArithmeticExpression(expString2)) {
            return null;
        }

        try
        {
            AlgExp exp1=AlgExp.parseExp(expString1);
            AlgExp exp2=AlgExp.parseExp(expString2);
            String rep1=expString1;
            String rep2=expString2;
            if(!exp1.isSimple()||exp1.isNegative())
                rep1="("+expString1+")";
            if(!exp2.isSimple()||exp2.isSimpleTerm()||exp2.isNegative())
                rep2="("+expString2+")";

            return rep1+"/"+rep2;

        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @param expString a string representing an AlgExp
     * @return a string representing the AlgExp with its sign reversed
     */
    public String reverseSign(String expString) {
    	if(expString.indexOf('=') != -1)
    		return null;
    	
        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);

            return exp.negate().toString();

        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @param expString a string representing an AlgExp
     * @return the name of the variable if the expression is a monomial,null otherwise
     */
    public String varName(String expString) {

        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
            if(exp.isVariable())
                return ((Variable)exp).getName();
            if(exp.isSimpleTerm())
                return ((SimpleTerm)exp).getVariable().getName();
            return null;
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public String numerator(String expString) {

        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
           // if(!exp.isFraction())
           // {
                if(exp.isSimpleTerm())
                {
                    SimpleTerm t=(SimpleTerm)exp;
                    Constant coe=t.getConstant();
                    if(coe.isConstantFraction())
                        return ((ConstantFraction)coe).getNumerator().toString().concat(t.getVariable().toString());
                    if(!coe.isFraction()) {
                    	return t.toString();
                    }
                }
               // return null;
            //}

            if(exp.isConstant())
                return ((ConstantFraction)exp).getNumerator().toString();
            
            return ((ComplexFraction)exp).getNumerator().toString();
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String numeratorStatic(String expString) {

        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
           // if(!exp.isFraction())
           // {
                if(exp.isSimpleTerm())
                {
                    SimpleTerm t=(SimpleTerm)exp;
                    Constant coe=t.getConstant();
                    if(coe.isConstantFraction())
                        return ((ConstantFraction)coe).getNumerator().toString().concat(t.getVariable().toString());
                    if(!coe.isFraction()) {
                    	return t.toString();
                    }
                }
               // return null;
            //}

            if(exp.isConstant())
                return ((ConstantFraction)exp).getNumerator().toString();
            
            return ((ComplexFraction)exp).getNumerator().toString();
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    
    public String denominator(String expString) {

        if (!isArithmeticExpression(expString)) {
            return null;
        }

        String denominator = null;

        try {

            AlgExp exp=AlgExp.parseExp(expString);
            
//            trace.out("exp = " + exp);
//            trace.out("isFraction? " + exp.isFraction());

            if (exp.isFraction() && !(exp instanceof SimpleTerm)) {
            	if (exp.isConstant()) {
            		denominator = ((ConstantFraction)exp).getDenominator().toString();
            		
            	} else if (exp instanceof ComplexFraction) {
            		denominator = ((ComplexFraction)exp).getDenominator().toString(); 
            	} else {
            		trace.out("What about a fraction " + exp + "?");
            	}
            } else if (exp instanceof SimpleTerm) {

            	Constant coe=((SimpleTerm)exp).getConstant();
            	
            	if(coe != null && coe.isFraction()) {
            		denominator = ((ConstantFraction)coe).getDenominator().toString();
            	}
            }

        } catch(ExpParseException e) {
            e.printStackTrace();
        }
        return denominator;
    }

    
    public static String denominatorStatic(String expString) {

        if (!isArithmeticExpression(expString)) {
            return null;
        }

        String denominator = null;

        try {

            AlgExp exp=AlgExp.parseExp(expString);
            
//            trace.out("exp = " + exp);
//            trace.out("isFraction? " + exp.isFraction());

            if (exp.isFraction() && !(exp instanceof SimpleTerm)) {
            	if (exp.isConstant()) {
            		denominator = ((ConstantFraction)exp).getDenominator().toString();
            		
            	} else if (exp instanceof ComplexFraction) {
            		denominator = ((ComplexFraction)exp).getDenominator().toString(); 
            	} else {
            		trace.out("What about a fraction " + exp + "?");
            	}
            } else if (exp instanceof SimpleTerm) {

            	Constant coe=((SimpleTerm)exp).getConstant();
            	
            	if(coe != null && coe.isFraction()) {
            		denominator = ((ConstantFraction)coe).getDenominator().toString();
            	}
            }

        } catch(ExpParseException e) {
            e.printStackTrace();
        }
        return denominator;
    }
    
    
    public String copyTerm(String expString) {
        /*
        if (!isArithmeticExpression(expString)) {
            return null;
        }
         */
        return expString;
    }

    /*  modifying operations*/


    /** 16 Nov 2007
     *  This method is redundant. It is identical to the method varName(String).
     */
    /**
     *NOTE: does not handle complex terms(e.g. 5xy) 
     * @param expString a string representing an AlgExp
     * @return the variable,the term sans coefficent or null
     */
    public String ripCoefficient(String expString)
    {
        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
            if(exp.isVariable())
                return ((Variable)exp).getName();
            if(exp.isSimpleTerm())
                return ((SimpleTerm)exp).getVariable().getName();
                      
            return null;
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @param expString1 a string representing an AlgExp
     * @param expString2 an string representing an AlgExp
     * @return the greatest common divisior if both exps are ints, null otherwise
     */
    public String gcd(String expString1,String expString2)
    {
        if (!isArithmeticExpression(expString1) || !isArithmeticExpression(expString2)) {
            return null;
        }

        try
        {
            AlgExp exp1=AlgExp.parseExp(expString1);
            AlgExp exp2=AlgExp.parseExp(expString2);


            if(!(exp1.isInt() && exp2.isInt()))
                return null;
            int v1=(int)((IntConst)exp1).getVal();
            int v2=(int)((IntConst)exp2).getVal();
            if(v1==0)
                return null;
            try
            {
                int gcd=MathLib.gcd(v1,v2);
                if(gcd==1)
                    return null;
                return String.valueOf(gcd);
            }
            catch(ArithmeticException e)
            {
                return null;
            }
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @param expString1 a string representing an AlgExp
     * @param expString2 an string representing an AlgExp
     * @return the least common mutiple if both exps are ints, null otherwise
     */
    public String lcm(String expString1,String expString2)
    {
        if (!isArithmeticExpression(expString1) || !isArithmeticExpression(expString2)) {
            return null;
        }

        try
        {
            AlgExp exp1=AlgExp.parseExp(expString1);
            AlgExp exp2=AlgExp.parseExp(expString2);


            if(!(exp1.isInt() && exp2.isInt()))
                return null;
            int v1=(int)((IntConst)exp1).getVal();
            int v2=(int)((IntConst)exp2).getVal();
            try
            {
                int lcm=MathLib.lcm(v1,v2);
                if(lcm==0)
                    return null;
                return String.valueOf(lcm);
            }
            catch(ArithmeticException e)
            {
                return null;
            }
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @param expString a string representing an AlgExp
     * @return a string representing the expression divided by ten if the expression is an constant, otherwise return null; 
     */

    public String divTen(String expString) {

        if (!isArithmeticExpression(expString)) {
            return null;
        }
        String divTen = null;

        try {
            Double dbExp = Double.parseDouble(expString);

            if(dbExp / 10 % 1 == 0.0)
            	divTen = "" + (int)(dbExp / 10);
            else
            	divTen = "" + (dbExp / 10); 
        } catch (NumberFormatException e) {
            ;
        }

        return divTen;

    }

    /**
     * Returns an integer (in String) that is 10 times bigger than the given expression, 
     * only when the expression is an integer.  Otherwise, return null.
     * 
     * @param exp
     * @return
     */
    public String mulTen(String exp) {

        String mulTen = null;

        try {
            int intExp = Integer.parseInt(exp);

            mulTen = "" + (intExp * 10); 
        } catch (NumberFormatException e) {
            ;
        }

        return mulTen;
    }

    /**
     * 
     * @param expString a string representing an AlgExp
     * @return a string representing the expression modded by ten if the expression is an int, otherwise return null; 
     */

    public String modTen(String expString) {

        if (!isArithmeticExpression(expString)) {
            return null;
        }
        String modTen = null;

        try {
            int intExp = Integer.parseInt(expString);

            modTen = "" + (intExp % 10); 
        } catch (NumberFormatException e) {
            ;
        }

        return modTen;
    }

    /**
     * 
     * @param expString a string representing an AlgExp
     * @return a string representing the inverse of the expression if the expression is simple, otherwise return null; 
     */
    public String inverseTerm(String expString) {

        if (!isArithmeticExpression(expString) || expString.equals("0")) {
            return null;
        }

        String inverseTerm = null;

        try {

            AlgExp exp = AlgExp.parseExp(expString);

            if (exp.isFraction()) {
                // TODO
                //trace.out("invertTerm on fraction");
                inverseTerm = exp.invert().toString();
            } else if (exp.isSimple()) {
                inverseTerm = exp.invert().toString();
                //trace.out("inverseTerm(" + expString + ")=" + inverseTerm);
                //trace.out(exp.getClass());
            } else {
                //trace.out("inverseTerm(" + expString + ") gets null");
            }

        } catch(ExpParseException e) {
            e.printStackTrace();
        }

        return inverseTerm;
    }



    /**
     * 
     * @param expString a string representing an AlgExp
     * @return a string representing expression, having been completely evaled  return null if there's no evaluation to be done; 
     */
    public static String evalArithmetic(String expString) {

        //NOTE: As of 8-16-06 The equals method does not handle communtivity, 
        // this might cause problems with this method

        if (!isArithmeticExpression(expString)) {
            return null;
        }

        String evalArithmetic = null;

        ConstantFraction fr;
        
        try {

            AlgExp exp=AlgExp.parseExp(expString);

            //is 0.84y/0.84 a fraction?
            if(!exp.isConstant() || exp.isFraction()) {
                AlgExp evalExp=exp.eval();
                evalArithmetic = evalExp.toString();

            } else {
                evalArithmetic = AlgExp.cancelDoubleMinus(expString);
            }

        } catch(ExpParseException e) {
            e.printStackTrace();
        }

        return evalArithmetic;
    }

    /* PREDICATES */
    /**
     * @author ajzana
     * @return "T" if the expression represented by expString is a polynomial,null otherwise
     */
    public String polynomial(String expString) 
    {
        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
            if( exp.isPolynomial())
                return "T";
            return null;
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @param expString
     * @return "T" if the expression has a parentheses around it (doesn't care about the matching)
     */
    public String hasParentheses(String expString) {
    	
    	for(int i=0; i< expString.length(); i++){
    		if(expString.charAt(i) == '(') {
    			return "T";
    		}
    	}
    	
    	return null;
    }
    
    public String isNegative(String expString) {
    	
    	if(expString != null){
	    		if (!isArithmeticExpression(expString)) {
	            return null;
	        }
	
	        try
	        {
	            AlgExp exp=AlgExp.parseExp(expString);
	            if( exp.isNegative())
	                return "T";
	            return null;
	        }
	        catch(ExpParseException e)
	        {
	            e.printStackTrace();
	            return null;
	        }
    	}
    	return null;
    }
    
    public String hasCoefficient( String expString ) 
    {
        if (!isArithmeticExpression(expString)) {
            return null;
        }

        String coefficient = coefficient( expString );
        if ( coefficient != null && !coefficient.equals( "1" ) )
            return "T";

        return null;
    }

    /**
     * 
     * @param expString a String representing an AlgExp
     * @return "T" if the expression is a polynomial and contains a variable, null otherwise
     */
    public String hasVarTerm(String expString)
    {
    	
        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
            
          
            if(!exp.isPolynomial()){
            	return null;
            }

            if(exp.hasVariable())
                return "T";
            
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 
     * @param expString a String representing an AlgExp
     * @return "T" if the expression is a monomial and contains a variable, null otherwise
     */
    public String isAVarTerm(String expString) {
    	
    	if(!isArithmeticExpression(expString))
    		return null;
    	
    	try {
    		AlgExp exp = AlgExp.parseExp(expString);
        	if(exp.isMonomial() /*!exp.isPolynomial()*/ && exp.hasVariable() )
        		return "T";
    	} catch(ExpParseException e) {
    		e.printStackTrace();
    	}
    	
    	return null;
    }

    public String varTerm(String expString)
    {
        if(!isArithmeticExpression(expString))
            return null;
        try
        {
            AlgExp exp = AlgExp.parseExp(expString);
            if(!exp.isPolynomial() && exp.hasVariable())
                return "T";
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 
     * @param expString a String representing an AlgExp
     * @return "T" if the expression is a just a varible (e.g. x) false otherwise
     */
    public String isVariable(String expString)
    {
        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
            if(exp.isVariable())
                return "T";
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @author ajzana
     * @return "T" if the expression represented by expString is a monomial,null otherwise
     */
    public String monomial(String expString)
    {
        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
            if( exp.isMonomial())
                return "T";
            return null;
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * @author ajzana
     * @return "T" if the expression represented by expString is a polynomial containing a constant,null otherwise
     */
    public String hasConstTerm(String expString)
    {
        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
            if (exp.isPolynomial())
            {
                if(((Polynomial)exp).hasConstTerm())
                    return "T";
            }

            if (exp.isMonomial() && exp.isConstant())
                return "T";

            return null;
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @author ajzana
     * @return "T" if the expression represented by expString is a fraction,null otherwise
     */
    public String  isFractionTerm(String expString)
    {
        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
            if(exp.isFraction())
                return "T";
            return null;
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @param expString a String representing an AlgExp
     * @return "T" is if the expression is simple or if it's a polynomial whose terms are all 
     * variables or all constants, null otherwise
     * 
     */	
    public String homogeneous(String expString)
    {
        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
            if(exp.isSimple()) {
                return "T";
            }
            if(!exp.isPolynomial())
                return null;

            Polynomial p=(Polynomial)exp;
            AlgExp firstHalf = p.getFirstHalf();
            AlgExp secondHalf = p.getSecondHalf();
            String firstString=p.getFirstHalf().toString();
            String secondString=p.getSecondHalf().toString();

            if (homogeneous(firstString) != null && homogeneous(secondString) != null) {
                if ((isAVarTerm(firstString) != null || hasVarTerm(firstString) != null) && 
                        (isAVarTerm(secondString) != null || hasVarTerm(secondString) != null)) {
                    String firstVar = getFirstVar(firstString);
                    String secondVar = getFirstVar(secondString);
                    if (firstVar.equals(secondVar))
                        return "T";
                }

                if ((isAVarTerm(firstString) == null && hasVarTerm(firstString) == null) && 
                        (isAVarTerm(secondString) == null && hasVarTerm(secondString) == null))
                    return "T";
            }
            return null;
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *  Test if the expression represented by expString can be simplified
     * @param expString a String representing an AlgExp
     * @return "T" if true, null otherwise
     */
    public static String canBeSimplified(String expString)
    {
        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
            if(!exp.eval().equals(exp))
                return "T";

            return null; 
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * NOTE doesn't handle complex terms (e.g. 5xy and 2xy are not counted as like terms 
     * @param term1 a term of an expresssion
     * @param term2 another term of the expression
     * @return "T" if term1 and term2 can be combined(all fractions are considered combinable), false otherwise 
     * 
     */
    public String likeTerms(String term1, String term2)
    {
        if (!isArithmeticExpression(term1) || !isArithmeticExpression(term2)) {
            return null;
        }

        AlgExp exp1;
        AlgExp exp2;

        try
        {
            exp1=AlgExp.parseExp(term1);
            exp2=AlgExp.parseExp(term2);
        }
        catch(ExpParseException e)
        {
            return null;
        }
        //convert variables to simple terms for easy comparision
        if(exp1.isVariable())
            exp1=new SimpleTerm(AlgExp.ONE,(Variable)exp1);
        if(exp2.isVariable())
            exp1=new SimpleTerm(AlgExp.ONE,(Variable)exp2);

        if(exp1.isConstant() && exp2.isConstant()) 
            return "T";
        if(exp1.isSimpleTerm() && exp2.isSimpleTerm())
        {
            if(((SimpleTerm)exp1).getVariable().equals(((SimpleTerm)exp2).getVariable()))
                return "T";

        }
        return null;
    }	    

    /* Primative Predicates*/
    /**
     * 
     * @param termString a String representing a term of an AlgExp
     * @param expString a String representing an AlgExp
     * 
     * @return "T" if expString represents a polynomial, termString represents a  term and termString is a part of expString
     */
    public String isATermOf(String termString,String expString)
    {
        if (!isArithmeticExpression(termString) || !isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp term=AlgExp.parseExp(termString);
            AlgExp exp=AlgExp.parseExp(expString);
            if(!exp.isPolynomial() || term.isPolynomial())
                return null;
            Polynomial p=(Polynomial)exp;
            if(p.containsTerm(term))
                return "T";
            return null;


        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @param factorString a String representing an AlgExp
     * @param expString a String representing an AlgExp
     * @return "T" if the factorString expression is a factor of the expString expression
     */
    public String isAFactorOf(String factorString, String expString)
    {
        if (!isArithmeticExpression(factorString) || !isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
            AlgExp factor=AlgExp.parseExp(factorString);

            if(exp.isSimpleTerm())
            {
                SimpleTerm t=(SimpleTerm)exp;
                if(t.getConstant().equals(factor)|| t.getVariable().equals(factor))
                    return "T";
                return null;
            }
            if(exp.isTerm())
            {
                ComplexTerm ct=(ComplexTerm)exp;
                if(ct.containsFactor(factor))
                    return "T";
            }
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }


    public String isNumeratorOf(String numeratorString,String expString)
    {
        if (!isArithmeticExpression(numeratorString) || !isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
            if(exp.isFraction())
            {
                AlgExp numerator=AlgExp.parseExp(numeratorString);

                // trace.out("exp = " + exp + ", numerator = " + numerator);
                if(exp.isConstant())
                {
                    if(((ConstantFraction) exp).getNumerator().equals(numerator))
                        return "T";
                }
                else
                {
                    if(((ComplexFraction)exp).getNumerator().equals(numerator))
                        return "T";
                }
            }
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String isDenominatorOf(String denominatorString,String expString) {

        if (!isArithmeticExpression(denominatorString) || !isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            AlgExp exp=AlgExp.parseExp(expString);
            if(exp.isFraction())
            {
                AlgExp denominator=AlgExp.parseExp(denominatorString);
                if(exp.isConstant())
                {
                    if(((ConstantFraction) exp).getDenominator().equals(denominator))
                        return "T";
                }

                else
                {
                    if(((ComplexFraction)exp).getNumerator().equals(denominator))
                        return "T";
                }


            }
        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 
     * @param expString a String representing an AlgExp
     * @return "T" if the expression is a constant, or null
     */
    public String isConstant(String expString)
    {
    	if(expString.indexOf('=') != -1)
    		return null;
    	
        if (!isArithmeticExpression(expString)) {
            return null;
        }

        try
        {
            if(AlgExp.parseExp(expString).isConstant())
                return "T";

        }
        catch(ExpParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }


    /* = = = = = = = = = = = = = = = = 
     * Domain specific input matcher
     * 
     */                

    // private static int callCounter = 0;

    public boolean isInteger(String exp){

        try {
            Integer.parseInt(exp);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    public boolean isDouble(String exp){

        try {
            Double.parseDouble(exp);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    public static boolean isFloatingPointNumber(String exp){

        try {
            Float.parseFloat(exp);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    public boolean isFloatingPointNumber_nonstatic (String exp){

        try {
            Float.parseFloat(exp);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    public static boolean inputMatcher( Polynomial poly1, Polynomial poly2 ) {
        //trace.out("poly1 = " + poly1.parseRep() + "poly2 = " + poly2.parseRep());
        if (poly1.equals(poly2)) {
        	return true;
        }
        else {
            return inputMatcher(poly1.getFirstHalf(), poly2.getFirstHalf()) &&
            inputMatcher(poly1.getSecondHalf(), poly2.getSecondHalf());
        }
    }
    
    //get all parts of exp1, exp2, and check that all parts are equivalent (IsEquivalent)
    public static boolean inputMatcher( AlgExp exp1, AlgExp exp2 ) {

        if (exp1.equals(exp2)) {
            return true; //this equality is sufficient but not necessary
        }
        if (exp1.getClass()!=exp2.getClass())
            return false;

        //this goes for all properties of AlgExp
        /*if ((exp1.isPolynomial() && !exp2.isPolynomial()) ||
    			(!exp1.isPolynomial() && exp2.isPolynomial()))
    		return false; */
        if (exp1.isPolynomial() && exp2.isPolynomial()) {
            return inputMatcher((Polynomial) exp1, (Polynomial) exp2);
        }
        else {//neither is polynomial
            return false;
        }
    }

    //what happens when exp1 and exp2 are of different types,
    //e.g. floating number & alg. expr?
    public String inputMatcher( String exp1, String exp2 ) {

    	//if(trace.getDebugCode("miss"))trace.out("miss", "inputMatcher: " + exp1 + "   " + exp2);
        // having null in the arguments must be wrong
        if (exp1 == null || exp2 == null) return null;
    	/*
    	//jinyul - weak input matcher implementation for EqFeaturePredicate - parse out the negative sign in 
    	//         the instruction.
    	for(int x=0;x<exp1.length();x++) {
    		if(exp1.charAt(x)=='-') {
    			if(x<exp1.length()-1) {
    				exp1=exp1.substring(0,x).concat(exp1.substring(x+1));
    			}
    			else
    				exp1=exp1.substring(0,x);
    		}
    	}
    	for(int y=0;y<exp2.length();y++) {
    		if(exp2.charAt(y)=='-') {
    			if(y<exp2.length()-1) {
    				exp2=exp2.substring(0,y).concat(exp2.substring(y+1));
    			}
    			else
    				exp2=exp2.substring(0,y);
    		}
    	}*/

        // 8/27/2007 :: Noboru
        // This inputMatcher must be improved by introducing types
        // 
        if (isSkillOperand(exp1) && isSkillOperand(exp2)) {
            if (inputMatcher(exp1.split(" ")[1], exp2.split(" ")[1]) != null) {
                return (exp1.split(" ")[0].equalsIgnoreCase(exp2.split(" ")[0]) ? "T" : null);
            } else {
                return null;
            }
        }
        
        //2 Oct 2006
        // new operator "slip" was added to model arithmetic slips
        if (exp1.equalsIgnoreCase("slip") || exp2.equalsIgnoreCase("slip")){
            return "T";
        }

        String result = null;    //does null mean false?

        int idx1 = exp1.indexOf('.');
        int idx2 = exp2.indexOf('.');

        //trace.out("exp1 = [" + exp1 + "]  isFloatingPointNumber(exp1)=" + isFloatingPointNumber(exp1));
        //trace.out("exp2 = [" + exp2 + "]  isFloatingPointNumber(exp2)=" + isFloatingPointNumber(exp2));

        //determines whether two floating-point numbers are considered equivalent
        if ((isFloatingPointNumber(exp1) && isFloatingPointNumber(exp2)) &&
                ((idx1!=-1) && (idx2!=-1))) {
            // exp1 and exp2 are floating numbers with points(integers don't count)
            Float f1 = new Float(exp1);
            Float f2 = new Float(exp2);

            float diff = java.lang.Math.abs(f1.floatValue() - f2.floatValue());

            if (diff < OLD_ALLOWANCE && diff>ALLOWANCE){ //danger zone
                java.lang.System.exit(1);
            }

            if (diff < ALLOWANCE)                    
                return "T";
            else
                return null;

        } else if (exp1.indexOf(' ') >= 0 && exp2.indexOf(' ') >= 0) {
            // exp1 and exp2 are not algebraic expressions

            result = exp1.equalsIgnoreCase(exp2) ? "T" : null; //should ignore case
        }
        else {
            // Test this only when both exp1 and exp2 do not contain a space
            // E.g., "add 3x" would be excluded
            // trace.out("*");
            try {
                //trace.out("inputMatcher: else");
                AlgExp algExp1 = AlgExp.parseExp(exp1).eval();
                AlgExp algExp2 = AlgExp.parseExp(exp2).eval();
                
                //if one is a ConstantFraction and the other has a "."
                if (idx1 >= 0 && algExp2.isConstantFraction()) {
                    //trace.out("entered if-2");
                    String canoExp1 = algExp1.toString();
                    return isEqualDecimal(canoExp1, (ConstantFraction)algExp2);
                } else if (idx2 >= 0 && algExp1.isConstantFraction()) {
                    //trace.out("entered if-3");
                    String canoExp2 = algExp2.toString();
                    return isEqualDecimal(canoExp2, (ConstantFraction) algExp1);
                }

                //general case
                //boolean res = inputMatcher(algExp1, algExp2);
                boolean res = false;
        		try {
        			InquiryClSolverTutor icst = SimSt.iclSolverTutorForEqFeaturePredicate;
        			String clexp1 = icst.getSm().standardize(exp1, true);
        			String clexp2 = icst.getSm().standardize(exp2,true);
        			res = icst.getSm().algebraicEqual(clexp1, clexp2);
        		} catch(BadExpressionError err) {
        			err.printStackTrace();
        		}

                if (res) {
                    return "T";
                }
                else {
                    return null;
                }
            }

            catch (Exception e) {
                ;
            }
        }
        return result;
    }



    private static String isEqualDecimal(String decimalNum, ConstantFraction fraction) {

        // trace.out("isEqualDecimal(" + decimalNum + "," + fraction + ")");

        String isEqualDecimal = null;

        Constant numerator = (((ConstantFraction)fraction).getNumerator());
        Constant denominator = (((ConstantFraction)fraction).getDenominator());

        if (numerator.isConstant() && denominator.isConstant()) {
            AlgExp quotient = 
                new IntConst(numerator.getVal()).divDecimal(new IntConst(denominator.getVal()));

            int idx = decimalNum.indexOf('.');
            // number of digits right to the decimal point
            int sflen = decimalNum.length() - idx -1; 
            int sf = (sflen < 3) ? sflen : 3; 
            String decimalNumStr = decimalNum.substring(0, sf + idx +1);

            String quotientStr = quotient.toString();
            int idx2 = quotientStr.indexOf('.');
            quotientStr = quotientStr.substring(0, sf + idx2 +1);

            if (decimalNumStr.equals(quotientStr)) {
                isEqualDecimal = "T";
            }
        }

        return isEqualDecimal; 
    }

    public static String removeChar(String s, char c) {
        String r = "";
        for (int i = 0; i < s.length(); i ++) {
            if (s.charAt(i) != c) r += s.charAt(i);
        }
        return r;
    }

    public String removeParens(String s){
        if (!isArithmeticExpression(s))
            return null;
        return removeChar(removeChar(s,')'),'(');
    }

    public String addTermBy_keepMinus(String expString1,String expString2)    {
        if (!isArithmeticExpression(expString1) || !isArithmeticExpression(expString2)) {
            return null;
        }

        String withOp="+"+expString2;
        return expString1+withOp;
    }

//  25Oct2006: parses a LIST_SEPARATOR-separated list into a String[]. e.g. "[2 5]" becomes
//  the String[] containing "2" and "5".
    public String[] parseList(String listString){

        if (!isExprList(listString))
            return null;

        int length = listString.length();
        if((listString.charAt(0)!='[') || (listString.charAt(length-1)!=']')){
            return null;
        }
        String bracketlessListString = listString.substring(1, length-1);	
        return bracketlessListString.split(LIST_SEPARATOR);
    }

    public String makeList(String[] l){
        String listString;
        int length = l.length;
        if (length==0){
            listString = "";
        }
        else{
            listString = l[0];
            for (int i=1; i<l.length; i++)
                listString = listString + LIST_SEPARATOR + l[i];
        }

        return "["+ listString +"]";
    }

    public String makeList(Vector v){
        //trace.out("entered makeList(Vector v)   v = " + v);
        String[] l = new String[v.size()];
        //trace.out("before for");
        //trace.out("v.size() = " + v.size());
        for (int i=0; i<v.size(); i++){
            l[i] = v.get(i).toString();
        }
        return makeList(l);
    }

    public String listAddTerm(String listString) {

        if (!isExprList(listString))
            return null;

        String[] l = parseList(listString);
        int length = l.length;
        String result;

        if (length==0){
            result = null; }		
        else {
            result = l[0];
            for(int i=1; i<length; i++){
                result = addTerm(result, l[i]);
            }
        }	
        return result;
    }


    public String listAddTermBy(String listString) {

        if (!isExprList(listString))
            return null;

        String[] l = parseList(listString);

        int length = l.length;

        String result;

        if (length==0){
            result = null; }		
        else {
            result = l[0];
            for(int i=1; i<length; i++){
                result = addTermBy(result, l[i]);
            }
        }	
        return result;
    }

//  25Oct2006
//  parse numbers from an expression, i.e. strings of 0-9,.,-
    public String getNumSymbolsList(String expString) {

        if (!isArithmeticExpression(expString))
            return null;

        Vector numbers = new Vector();
        char c;
        int state = 0; //state 1 means we just got out of a numeric string.
        String numberString;

        int i=0, j=-1; //j is the location of the last non-numeric character
        while(i<expString.length()) { //for each char in expString

            c = expString.charAt(i);

            //we need the state check, so that in the string "1-2" we get out of numeric state upon reaching the '-'
            if ((c>='0'&&c<='9')||((c=='-')&&state==0)||(c=='.')) { //if are in the numeric string
                state = 1;
            }
            else{ //not a numeric string
                if (state==1){ //i.e. if we just got out of a numeric string
                    numberString = expString.substring(j+1,i);
                    numbers.add(numberString);
                }			
                state = 0;

                //save the index of the last non-numeric char
                if (c=='-')
                    j=i-1; //include the '-'
                else
                    j=i; 
            }		
            i++; //move to next char		
        }

        //to catch the last one
        if (state==1){ //i.e. if previous char was numeric
            numberString = expString.substring(j+1,i);
            numbers.add(numberString);
        }
        return makeList(numbers);
    }

//  25Oct2006
//  this function returns the first letter variable in the expression, in lowercase.
    public String getFirstVar(String expString) {

        if (!isArithmeticExpression(expString))
            return null;

        String s = expString.toLowerCase();	
        char c = 0;

        for(int i=0; i<s.length(); i++){
            c = s.charAt(i);
            if ((c>='a')&&(c<='z')) //if c is a letter
                return "" + c; //return the string of c
        }
        return null; //if no variable is found, return null
    }

//  public String getNumbers(String numbers) {
//  }

    public String getVariableSymbol(String expString) {
        return getFirstVar(expString);
    }

    public String algebraTest(String s) {
        return "";
    }

    public String[] getExpListTokens(String expList) {
    	String tokens = expList.substring(1, expList.length() -1);
		return tokens.split(" ");
	}

    public String appendVarSymbol(String s, String var) {

        if (!"T".equals(isConstant(s)) || !isVarExpression(var))
            return null;

        // trace.out("s=" + s + ", var=" + var);

        return s+var;
    }

    private boolean isVarExpression(String var) {
        char varChar = var.toLowerCase().charAt(0);
        return (var.length() == 1 && 'a' <= varChar && varChar <= 'z');
    }

//  this function tells whether String s contains a number character
    public boolean hasNumber(String s){
        char c = 0;
        for(int i=0; i<s.length(); i++){
            c = s.charAt(i);
            if ((c>='0')&&(c<='9')) //if c is a number character
                return true;
        }
        return false; //if no number is found, return null
    }


//  add the term to 'terms' if it meets the condition hasNumber
    public void IfHasNumberAddToTerms(int state, String expString, int i, int j, Vector terms){
        if (state==1){ //i.e. if we just got out of a term string
            String termString = expString.substring(j+1,i);
            if (hasNumber(termString))
                terms.add(termString); 		//trace.out("adding " + termString);
        }	
    }


//  25Oct2006: this function drops terms that don't show an explicit coefficient, e.g. "y", "-x"
    public String dropSimpleVarSymbol(String expString) {

        if (!isArithmeticExpression(expString))
            return null;

        String s = expString.toLowerCase();

        Vector terms = new Vector();

        char c;
        int state = 0; //state 1 means we just got out of a term string.

        String termString;

        int i=0, j=-1; //j is the location of the last character not in the current term

        while(i<s.length()) { //for each char in s

            c = s.charAt(i);

            //we need the state check, so that in the string "1-2" we get out of term-state upon reaching the '-'
            if ((c>='0'&&c<='9')||((c=='-')&&state==0)||(c=='.')||(c>='a'&&c<='z')) { //if are in the term string
                state = 1;
            }
            else{ //term ended
                IfHasNumberAddToTerms(state, expString,i,j,terms);
                state = 0;

                //save the index of the last non-numeric char
                if (c=='-')
                    j=i-1; //include the '-'
                else
                    j=i; 
            }
            i++; //move to next char		
        }

        //to catch the last one
        IfHasNumberAddToTerms(state, expString,i,j,terms);

        return listAddTermBy(makeList(terms));
    }


//  return all the terms that contain variables
    public String getVarTermSymbols(String s) {

        if (!isArithmeticExpression(s))
            return null;

        try{
            AlgExp exp = AlgExp.parseExp(s);

            if(!exp.isPolynomial())
                if (exp.hasVariable())//s is a VarTerm			
                    return s; 
                else //s is a non-var term
                    return "";

            if(exp.isPolynomial()){
                Polynomial p = (Polynomial) exp;
                Vector terms = p.getAllTerms();
                Vector varterms = new Vector();

                Iterator i = terms.iterator();
                AlgExp a;
                while (i.hasNext()) {
                    a = (AlgExp) i.next();
                    if (a.hasVariable()){
                        varterms.add(a);
                    }
                }
                String m = makeList(varterms);
                //now, varterms is populated with the var terms of s
                return listAddTermBy(m); //like listAddTerm
            }
        }
        catch(Exception e){
            return null;
        }
        return null;
    }


    public String getConstTermSymbols(String s) {

        if (!isArithmeticExpression(s))
            return null;

        try{
            AlgExp exp = AlgExp.parseExp(s);

            if(!exp.isPolynomial())
                if (!exp.hasVariable())//s is a VarTerm			
                    return s; 
                else //s is a non-var term
                    return "";

            if(exp.isPolynomial()){
                Polynomial p = (Polynomial) exp;
                Vector terms = p.getAllTerms();
                Vector varterms = new Vector();

                Iterator i = terms.iterator();
                AlgExp a;
                while (i.hasNext()) {
                    a = (AlgExp) i.next();
                    if (!a.hasVariable()){
                        varterms.add(a);
                    }
                }
                String m = makeList(varterms);
                //now, varterms is populated with the var terms of s
                return listAddTermBy(m); //like listAddTerm
            }
        }
        catch(Exception e){
            return null;
        }
        return null;
    }

    public String listFirstNegativeFunnyAdd(String listString){

        if (!isExprList(listString))
            return null;

        String[] l = parseList(listString);
        int length = l.length;
        String result;

        if (length==0){
            return null; }		
        else {
            //result = reverseSign(l[0]);

            result = l[0]; //first element
            boolean firstNegative = result.charAt(0)=='-' ? true : false;

            if (firstNegative)
                result = reverseSign(result); //make the first element positive

            for(int i=1; i<length; i++){
                result = addTerm(result, l[i]);
            }

            if (firstNegative)
                result = reverseSign(result); //reverse the sign of the whole expr
            return result;
        }
    }

    public String listAddSymbols(String s) {
        return listAddTerm(s);
    }

    public String butLastTerm(String s) {

        if (!isArithmeticExpression(s))
            return null;

        try{
            AlgExp exp = AlgExp.parseExp(s);

            if(!exp.isPolynomial())
                return "";

            if(exp.isPolynomial()){
                Polynomial p = (Polynomial) exp;
                Vector terms = p.getAllTerms();
                Vector varterms = new Vector();

                Iterator i = terms.iterator();
                AlgExp a;
                while (i.hasNext()) {
                    a = (AlgExp) i.next();
                    varterms.add(a);
                }

                varterms.remove(varterms.size()-1); //remove the last term
                String m = makeList(varterms); 
                //now, varterms is populated with the var terms of s
                return listAddTermBy(m); //like listAddTerm
            }
        }
        catch(Exception e){
            return null;
        }
        return null;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Weak string operators
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    // returns a first meaningful number chunk in the given string
    String getFirstNumberString(String string) {
        
        String number = null;
        boolean hasGottenNumber = false;
        
        for (int i = 0; i < string.length(); i++) {
            
            char c = string.charAt(i);
            if (Character.isDigit(c)) {
                if (!hasGottenNumber) {
                    number = "";
                    hasGottenNumber = true;
                }
                number += c;
            } else if (hasGottenNumber) {
                break;
            }
        }
        return number;
    }
    
    String getFirstVarString(String string) {
        
        String var = null;

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            char lc = Character.toLowerCase(c);
            if ( 'a' <= lc && lc <= 'z') {
                var = "" + lc;
                break;
            }
        }

        return var;
    }
    
    // Get numbers from the given string and simply sum them up
    // E.g.  calcNumbers("3/-4") -> -1 (3-4)
    // calcNumbers("2x/5") -> 7 (2+5)
    String calcNumbers(String string) {
        String numString = null;
        int num = 0;
        int sum = 0;
        int sign = 1;
        boolean gottenSignAlready = false;
        boolean gottenNumberAlready = false;
        boolean gottenSum = false;
        
        // 3x/-5
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == '-' || c == '+') {
                if (gottenSignAlready) return null;
                if (gottenNumberAlready) {
                    sum = sum + (num * sign);
                    trace.out("sum = " + sum);
                    gottenSum = true;
                    gottenNumberAlready = false;
                }
                sign = (c == '-' ? -1 : 1);
                gottenSignAlready = true;
            } else if (Character.isDigit(c)) {
                if (!gottenNumberAlready) {
                    num = 0;
                    gottenNumberAlready = true;
                }
                num = num * 10 + (c - '0');
            } else if (gottenNumberAlready) {
                sum = sum + (num * sign);
                trace.out("sum = " + sum);
                sign = 1;
                gottenSum = true;
                gottenNumberAlready = false;
                gottenSignAlready = false;
            }
        }
        if (gottenNumberAlready) { 
            sum = sum + (num * sign);
            trace.out("sum = " + sum);
            gottenSum = true;
        }
        
        if (gottenSum) {
            numString = "" + sum;
        }
        
        return numString;
    }
}
