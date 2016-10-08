/**
 * f:/Project/CTAT/ML/ISS/miss/FeaturePredicate.java
 *
 *	Feature predicate must extend this class and implement
 *	Userfunction (for Jess external code) as well as Serializable
 *	(for Model Tracing).
 *
 *	A feature predicate returns Jess Value.  
 *
 * Created: Sat Feb 26 17:32:32 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 **/

package edu.cmu.pact.miss;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

import jess.Context;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public abstract class FeaturePredicate
    implements Userfunction, Cloneable, Serializable {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - -
    // - 
	private static final long serialVersionUID =  -4416689206189754101L;
	// Count of how many times this FeaturePredicate has been applied
	public int freqCount;
	
    // The name of this predicate
    private String name;
    /**
     * testAsWME indicates whether the predicate out to be tested as a WME or as part of a test pattern
     */
    protected boolean testAsWME=false;
    /**
     * flag to indicate whether this predicate represents a relationship between a decomposed element and larger represention
     */
    protected boolean isDecomposedRelationship=false;
    public String getName() { return this.name; }
    
    public void setName( String name ) { this.name = name; }
    // A list of arguments
    private Vector /* of String */ args = new Vector();
    public Vector /* of String */ getArgs() { return this.args; }
    // RhsSearchSuccessorFn calls this method to assign actual
    // arguments (different instance of instruction has different
    // argument values)
    public void setArgs( Vector /* of String */ args ) { this.args = args; }
    void addArgs( String arg ) { this.args.add( arg ); }

    // A number of arguments required for the operator:: This is
    // needed even if one could read the arity off the argument list,
    // because at the time when a FeaturePredicate is instantiated by
    // RhsSearchSuccessorFn, no argument is assigned yet but still
    // need to know the arity.
    private int arity;
    public int getArity() { return arity; }
    public void setArity( int arity ) { this.arity = arity; }

    
    
    /*Cache for isValidArgument*/
    private HashMap validArqumentCashe=new edu.cmu.pact.miss.HashMap();
    public boolean getValidArqumentCashe( Vector args ) {
    	return (Boolean) validArqumentCashe.get( args );
     } 
    private void putValidArqumentCashe( Vector /* String */ args, boolean value ) {
    	int size = validArqumentCashe.size();
    	if (SimSt.FP_CACHE_CAPACITY >= 1 && size == SimSt.FP_CACHE_CAPACITY) {	
    	    	validArqumentCashe.clear();
    	 }    
    	validArqumentCashe.put( args, value );
    }
    
    
//    boolean isValidArgs( Vector /* of String */ args ) throws Exception {
//
//	boolean test = false;
//
//	if ( args.size() == getArity() ) {
//	    test = true;
//	} else {
//	    throw new Exception( "Invalid arguments: " + args.toString() );
//	}
//	return test;
//    }

    /** Set TRUE for the operators that returns the same value when
    arguments are rotated (i.e., commutative functions).
    */
    private boolean commutative = false;
    public boolean isCommutative() { return this.commutative; }
    void setIsCommutative( boolean test ) { this.commutative = test; }

    // Cache for apply() 
    // 
    private HashMap applyCashe = new edu.cmu.pact.miss.HashMap();
    /**
     * a vector of names for the argruments(optional, may not be set)
     */
	protected Vector argNames=null;
    public String getApplyCache( Vector /* String */ args ) {
	return (String)applyCashe.get( args );
    }
    private void putApplyCache( Vector /* String */ args, String value ) {
	int size = applyCashe.size();
	if (SimSt.FP_CACHE_CAPACITY >= 1) {
	    if ( size == SimSt.FP_CACHE_CAPACITY) {
		applyCashe.clear();
	    }
	    applyCashe.put( args, value );
	}
    }
    
    public static boolean isValidSimpleSkill(String exp) {
    	 
        return true;
    }
    
    public Iterator applyCasheKeys() { return applyCashe.keySet().iterator(); }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Type of arguments and output value
    //
    // As a search heuristic to prune irrelevant node expansion in RHS operator search,
    // we now care about the type of arguments for each operator. Basically, the 
    // search agent does not propose an operator sequence that has a type mismatch, 
    // namely, if an output from OP1 is fed into OP2, then the type of return value
    // of OP1 must be consistent with the type of argument of OP2. 
    // 
    // A general type encompassing all other types
	public static final int TYPE_OBJECT = 5;
    // Algebraic expresssions
    public static final int TYPE_ARITH_EXP = 1;
    // A list (e.g., "[-3x 3 5x]")
    public static final int TYPE_EXP_LIST = 2;
    // A single word (no space) representing a skill used in CL Algebra I 
    public static final int TYPE_SIMPLE_SKILL = 3;
    // A simple skills followed by a space and an argument (e.g., "add 3x")
    public static final int TYPE_SKILL_OPERAND = 4;
    //Stoichiometry Reason(Unit conversion, Given Value, etc.)
    public static final int TYPE_REASON = 6;
    //Stoichiometry Unit (mL, g, etc.)
    public static final int TYPE_UNIT = 7;
    //Stoichiometry Substance (mL, g, etc.)
    public static final int TYPE_SUBSTANCE = 8;
    
    //Function of these types unknown.
    public static final int TYPE_CANCEL = 9;
    public static final int TYPE_TUPLE = 10;
    
    private int returnValueType;
    public void setReturnValueType(int type) {
	this.returnValueType = type;
    }
    int getReturnValueType() { return returnValueType; }
    
    private int[] argValueType;
    public void setArgValueType(int[] argValueType) {
	this.argValueType = argValueType;
    }
    int getArgValueType(int idx) {
    
    	if(idx >= argValueType.length)
    		return -1;
    	return argValueType[idx]; 
    }
    
    
    /**
     *  High level method that invokes the domain dependent type checker defined by the
     *  command line argument "ssTypeChecker". All calls to the type checker should be made
     *  using this method (i.e. FeaturePredicate.valueType() ).
     *  If no command line argument is defined, the default algebra type checker is used.
     *  
     *  @param value 
     *  @return true or false, depending if type matcher things these two types match.
     */
    public static int valueType(String value) {
 
        int valueType = -1;
        
        // int valueType = valueTypeForAlgebra(value);
        // int valueType = TypeCheckerForStoich.valueType(value);
        String typeChecker = SimSt.getTypeChecker();
       // System.out.println("Type checker used is " + typeChecker);
        String className = typeChecker.substring(0, typeChecker.lastIndexOf('.'));
       // System.out.println("Class name is " + className);
        String methodName = typeChecker.substring(typeChecker.lastIndexOf('.') +1);
        //System.out.println("Method name is " + methodName);
        Method method = null;    
        Class[] argTypes = new Class[] {String.class};
        Object[] args = new Object[] {value};
        try {
            method = Class.forName(className).getMethod(methodName, argTypes);
            valueType = ((Integer)method.invoke(null, args)).intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return valueType;
    }
          
     
   /**
    *  Method to invoke the domain dependent function that checks if an input value contains a valid skill. 
    *  
    * @param value function name that checks for a valid skill.
    * @return true or false, depending if a skill is actually valid.
    */
    public static boolean isSkillValid(String value) {

        boolean valueType = false;
        
        // int valueType = valueTypeForAlgebra(value);
        // int valueType = TypeCheckerForStoich.valueType(value);
        String typeChecker = SimSt.getSsValidSkillChecker();
        String className = typeChecker.substring(0, typeChecker.lastIndexOf('.'));
        String methodName = typeChecker.substring(typeChecker.lastIndexOf('.') +1);
        Method method = null;
        Class[] argTypes = new Class[] {String.class};
        Object[] args = new Object[] {value};
        try {
            method = Class.forName(className).getMethod(methodName, argTypes);
            valueType = ((Boolean) method.invoke(null, args)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return valueType;
    }
    
    /*Low level method that checks if two types are compatible or not*/
    /*02/02/09: tmandel: Checks compatibility of types, order of arguments matters*/
	 public static boolean isCompatibleType(Integer typeUncertain, Integer typeRequired){		 
	        if(typeUncertain.equals(typeRequired))
	        	return true;
	        if(typeRequired.equals(TYPE_OBJECT))
	        	return true;
	        return false;
	    }
	 /*End of low level method that checks if two types are compatible or not*/
	 
	 /**
	  *  
	  *  High level method that invokes the domain dependent type matcher defined by the
	  *  command line argument "ssTypeMatcher". All calls to the type matcher should be made 
	  *  using this method (i.e. FeaturePredicate.typeMatcher() ).
	  *  If no command line argument is defined, the default type matcher is invoked (that 
	  *  checks if two types are equal)
	  *  
	  * @param value1 
	  * @param value2 
	  * @return true or false, depending if type matcher things these two types match.
	  */
	 public static boolean typeMatcher(Integer value1, Integer value2) {

		 boolean valueType = false;

		 String typeMatcher = SimSt.getTypeMatcher();
		 String className = typeMatcher.substring(0, typeMatcher.lastIndexOf('.'));
		 String methodName = typeMatcher.substring(typeMatcher.lastIndexOf('.') +1);
		 Method method = null;
		 Class[] argTypes = new Class[] {Integer.class, Integer.class};
		 Object[] args = new Object[] {value1, value2};
		 try {			 
			 method = Class.forName(className).getMethod(methodName, argTypes);
			 valueType = ((Boolean) method.invoke(null, args)).booleanValue();
		 } catch (Exception e) {
			 e.printStackTrace();
		 }

		 return valueType;
	 }


	    
	    
    // Dec-16-2007 :: Noboru
    // We are futher relaxing the constraint -- now everything entered into the Algebra I Tutor is
    // the same type. This is necessary to let Foil have negative examples to prevent "*-typein" skills
    // being applied at the Skill-operand column
    // Dec-18-2007 :: Noboru
    // This indeed makes the learning performance worse.
    /*
    public static Integer valueTypeForAlgebra( String value ) {
        int valueType = -1;
        if (EqFeaturePredicate.isValidSimpleSkill(value) || 
                EqFeaturePredicate.isSkillOperand(value) ||
                EqFeaturePredicate.isArithmeticExpression(value) ) {
            valueType = TYPE_ARITH_EXP;
        } else if (EqFeaturePredicate.isExprList(value)) {
            valueType = TYPE_EXP_LIST;
        }
        return new Integer(valueType);
    }
    */
    
    // Dec-5-2007 :: Noboru
    // skillOperand and simpleSkill must be merged so that FOIL gets 
    // tuples with simpleSkill (e.g., "mt") for skill-operand (e.g., "add -4"
    public static Integer valueTypeForAlgebra( String value ) {
        int valueType = -1;
        if (EqFeaturePredicate.isValidSimpleSkill(value) || 
               EqFeaturePredicate.isSkillOperand(value) ) {
            valueType = TYPE_SKILL_OPERAND;
        } else if (EqFeaturePredicate.isArithmeticExpression(value)) {
            valueType = TYPE_ARITH_EXP;
        } else if (EqFeaturePredicate.isExprList(value)) {
            valueType = TYPE_EXP_LIST;
        } else valueType=TYPE_OBJECT;
        // should this return something? 
        return new Integer(valueType);
    }
    
    /*
    public static Integer valueTypeForAlgebra( String value ) {
        int valueType = -1;
        if (EqFeaturePredicate.isValidSimpleSkill(value)) {
            valueType = TYPE_SIMPLE_SKILL;
        } else if (EqFeaturePredicate.isSkillOperand(value)) {
            valueType = TYPE_SKILL_OPERAND;
        } else if (EqFeaturePredicate.isArithmeticExpression(value)) {
            valueType = TYPE_ARITH_EXP;
        } else if (EqFeaturePredicate.isExprList(value)) {
            valueType = TYPE_EXP_LIST;
        }
        return new Integer(valueType);
    }
    */
    
    
    private boolean isValidArgument(Vector /* String */ args) {
    	boolean isValidArgument = true;

    	
    	if (validArqumentCashe==null)
    		validArqumentCashe=new HashMap();
    	

    	
    	if ( validArqumentCashe.containsKey( args ) ) {
    		isValidArgument = getValidArqumentCashe( args );
    	} else {
    		for (int i = 0; i < args.size(); i++) {
    			//tmandel: Now checks if types are compatible
    			int givenType = valueType((String)args.get(i));
    			int expectedType = getArgValueType(i);

    			//check if types are compatible
    			// if ( !isCompatibleType(givenType, expectedType) ) {
    			if ( !typeMatcher(givenType, expectedType) ) {
    				isValidArgument = false;
    				break;
    			}

    		}
    		
    		this.putValidArqumentCashe( args, isValidArgument );
    	}
    	return isValidArgument;
    }
    // -
    // - Construction - - - - - - - - - - - - - - - - - - - - - -
    // -

    /**
     * Creates a new <code>FeaturePredicate</code> instance.
     *
     */
    public FeaturePredicate() {
	// featureDescription = new Describable();
    }

    public Value call( ValueVector jessArgs, Context c ) throws JessException {
    	
	Vector argv = new Vector();
	for (int i = 1; i < jessArgs.size(); i++) {
	    argv.add( jessArgs.get(i).stringValue(c) );
	}
	// TODO
	// **** type check ****
	String returnVal = null;
	if (isValidArgument(argv)) {
	    returnVal = apply( argv );
	}
	return FeaturePredicate.applyReturnToValue(returnVal);
    }

    // -
    // - Abstract Methods - - - - - - - - - - - - - - - - - - - -
    // -

    /**
     * Invokes the predicate and returns its value
     *
     * @param args a <code>Vector</code> of String to be bound to
     * arguments
     * @return Non-null value if the predicate call succeeded.
     * Returns null when the predicate call fails or invalid arguments
     * are assinged.
     * @exception Exception if an error occurs */
    public abstract String apply( Vector /* String */ args );

    // -
    // - Methods  - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    public String cachedApply( Vector /* String */ args ) {

	String value = null;
	
	if (isValidArgument(args)) {
	
	    if ( applyCashe.containsKey( args ) ) {
		value = getApplyCache( args );
	    } else {
		try {
		    value = apply( args );
		} catch (Exception e) {
		    if (trace.getDebugCode("missalgebra")) {
			e.printStackTrace();
		    }
		}
		putApplyCache( args, value );
	    }
	}
	return value;
    }

    /**
     * Describe <code>actionStr</code> method here.
     *
     * @param args a <code>Vector</code> value
     * @return a <code>String</code> value
     */
    public String actionStr( Vector /* String */ args ) {

	String argStr = "";
	if ( args.size() > 0 ) {
	    for (int i = 0; i < getArity(); i++) {
		argStr += (String)args.get(i);
		if ( i < getArity() -1 ) {
		    argStr += " ";
		}
	    }
	}
	return "(" + getName() + " " + argStr + ")";
    }
    
    public String actionStr() {
        return "(" + getName() + ")";
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // A method to test if two "input" strings are semantically equivalent.
    // Returns non-null value when two strings are equivalent, null if not 
    // 
    // This method must be overwritten.
    // 
    // This method is needed especially for algebra domain to tell that 
    // "x+2" and "2+x" are equal.
    // 
    
    private static int callCounter = 0;
    public String inputMatcher( String exp1, String exp2 ) {
        String result = exp1.equals(exp2) ? "T" : null;
        return result;
    }
    
    //Gustavo 20Jan2007: this function converts a boolean into the "T"/null format
    public String convertBoolean(boolean b){
        if (b==true)
            return "T";
        else
            return null;
    }               

    
    
    public String inputMatcher_nonstatic( String exp1, String exp2 ) {
        
        String result = exp1.equals(exp2) ? "T" : null;
        
        return result;
    }

    
    
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Testing predicate & operator symbols
    // 
    public void testUserDefSymbols( String testName,
			   Object[] /* String */ args,
			   String expectedVal)
    {
    	/**
    	 * 
    	 * wrapper for the old method for compatibility
    	 * @author ajzana 
    	 */
    	testUserDefSymbols(testName,args,expectedVal,true);
    }
    public static boolean testUserDefSymbols( String testName,
					   Object[] /* String */ args,
					   String expectedVal, boolean printIfPassed ) {
    	//modified 5-26-06 ajz 
    	// to return a boolean value indication whether the test succeeded or not
    	//added a parameter to disable printing of passed results
	Vector argV = new Vector();
	for (int i = 0; i < args.length; i++) {
	    argV.add( args[i] );
	}
	String actualVal = testUserDefSymbols( testName, argV );

	String methodCall = testName + "(";
	for (int i = 0; i < args.length; i++) {
	    methodCall +=
		(String)args[i] + ( i < args.length -1 ? "," : "");
	}
	methodCall += ")";
	
	String msg = methodCall; 
	if ( (actualVal != null && !actualVal.equals(expectedVal)) ||
	     (actualVal == null && expectedVal != null ) ) {

	    msg = "NG === " + msg;
	    msg += " got " + actualVal + ", but should be " + expectedVal;
	    
	} else {
	    
	    msg = "OK ... " + msg;
	    msg += " = " + actualVal;
	    
	}
	boolean passed;
	if(expectedVal==null)
	{
		if(actualVal==null)
			passed=true;
		else
			passed=false;
	}
	else
	{	
		if(expectedVal.equals(actualVal))
			passed=true;
		else
			passed=false;
	}
	if(!passed || printIfPassed) {
		if(trace.getDebugCode("miss")) trace.out("miss", msg );
	}
	return passed;
    
    }
	
	

    public static String testUserDefSymbols( String testName,
					     Vector /* String */ args ) {
    	
    	String returnVal = null;

	// Get the class for the target test class
	Class testClass = null;
	try {
	    testClass = Class.forName( testName );
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	if ( testClass != null ) {

	    // All arguments are String.class;
	    Class[] argTypes = new Class[] { Vector.class };
	    Object testingInstance = null;
	    Method testingMethod = null;
	    Object[] argArray = new Object[] { args };
	    try {
		testingInstance = testClass.newInstance();
		testingMethod = testClass.getMethod( "apply", argTypes );

		Object tempReturnVal = testingMethod.invoke( testingInstance, argArray );
		returnVal = (String) tempReturnVal;
			    
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return returnVal;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Overriding generic methods
    // 

    public String toString() { return actionStr( getArgs() ); }

    public Object clone() {

	FeaturePredicate cloneOp = null;

	try {
	    cloneOp = (FeaturePredicate)super.clone();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	cloneOp.setArgs( (Vector)cloneOp.getArgs().clone() );

	return cloneOp;
    }

    public boolean equals( FeaturePredicate op ) {

	boolean test = true;

	if(op.getName() == null)
	{
		return false;
	}
	if ( op.getName().equals(getName()) && op.getArity() == getArity() ) {

	    for (int i = 0; i < getArity(); i++) {

		String opArg = (String)op.getArgs().get(i);
		String thisArg = (String)getArgs().get(i);
		if ( !opArg.equals( thisArg ) ) {
		    test = false;
		    break;
		}
	    }
	} else {
	    test = false;
	}
	return test;
    }
    
    /**
     * 
     * @return true if this predicate should be tested directly against a fact with the same name in working memory
     * (rather than in a (test) pattern
     */
	public boolean doTestAsWME() {
		return testAsWME;
	}
	/**
	 * set whether this predicate should be tested directly as a fact from working memory(true) or in a (test) pattern(false)
	 * @param testAsWME
	 */
	public void setTestAsWME(boolean testAsWME) {
		this.testAsWME = testAsWME;
	}
	/**
	 * Given a fully a qualified class name(e.g. edu.cmu.pact.miss.userDef.MyPredicate) return a FeaturePredicate object of that class
	 * @param className a fully qualified class name of a FeaturePredicate
	 * @return an is instance of the FeaturePredicate with className
	 */
	public static FeaturePredicate getPredicateByClassName(String className)
	{
		FeaturePredicate predicate=null;
			try 
			{
			
				Class classDef = Class.forName( className );
				predicate = (FeaturePredicate)classDef.newInstance();
		    } 
			catch (InstantiationException e) 
			{
				e.printStackTrace();
		    } 
			catch (ClassNotFoundException e) {
				e.printStackTrace();
		    } 
			catch (IllegalAccessException e) 
		    {
		    	e.printStackTrace();
			
		    }
		    return predicate;
		    
	}

	/**
	 * 
	 * @return a vector of string naming the arguments of null if no name has been specified
	 */
	public Vector /*of String */getArgNames() {

		return argNames;
	}
	/**
	 * Given a String return value from a call to apply, return the apporpriate  jess Value
	 * @param returnValue a String returned by a call to apply()
	 * @return a jess.Value
	 * @throws JessException
	 */
	protected static Value applyReturnToValue(String returnValue) throws JessException
	 {
		 if(returnValue==null)
			 return new Value("FALSE",RU.SYMBOL);
		 else
			 return new Value(returnValue,RU.STRING);
	 }
	/**
	 * returns true if this predicate represents a decomposed relationship 
	 * @return a boolean 
	 */
	public boolean isDecomposedRelationship() {
		return isDecomposedRelationship;
	}
	/**
	 * set whether this predicate represents a decomposed relationship
	 * @param isDecomposedRelationship boolean
	 */
	public void setDecomposedRelationship(boolean isDecomposedRelationship) {
		this.isDecomposedRelationship = isDecomposedRelationship;
	}

    /////////////////////////////////////////////////////////////////

    // Variable that holds the descriptions for a feature
    private Describable featureDescription = new Describable();

    public Describable getFeatureDescription() {
	return this.featureDescription;
    }

    public void setFeatureDescription(Describable pDescription) {
	this.featureDescription = pDescription;
    }

    /** Shorthand function to quickly set a feature description
      * @param pFeatName Feature name
      * @param pFeatDesc Feature descriptions/definition
      */
    
    public void setFeatureDescription(String pFeatName, String pFeatDesc) {
	this.featureDescription = new Describable(pFeatName, pFeatDesc);
    }
    
}
 
 

//
// end of f:/Project/CTAT/ML/ISS/miss/FeaturePredicate.java
// 
