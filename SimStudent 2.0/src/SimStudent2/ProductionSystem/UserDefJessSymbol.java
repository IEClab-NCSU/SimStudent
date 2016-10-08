/**
 * An abstract class to define a user defined operator functions and feature predicates
 * 
 * Created: Dec 16, 2013
 * @author mazda
 * (c) Noboru Matsuda 2013-2014
 *  
 */
package SimStudent2.ProductionSystem;

import java.util.ArrayList;
import java.util.Vector;

import jess.Context;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import SimStudent2.TraceLog;

/**
 * @author mazda
 *
 */
public abstract class UserDefJessSymbol implements Userfunction {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // - Abstract Methods  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	/**
     * Invokes the predicate and returns its value
     *
     * @param args a <code>Vector</code> of String to be bound to arguments
     * @return Non-null value if the predicate call succeeded.  The empty string may be a valid return.
     * Returns null when the predicate call fails or invalid arguments are assigned.
     * @exception Exception if an error occurs 
     */
    public abstract String apply(ArrayList<String> args);
    

    /**
     * Classifies the type of the argument 
     * 
     * @param arg
     * @return The int value representing the type of arg, which must be defined in the domain dependent class as well.
     * @throws Exception 
     */
    public abstract int valueType(String arg) throws JessException;


	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	// The name of the user defined jess function
	// 
	private String name;
	
	// TRUE if the operators that returns the same value when arguments are rotated (i.e., commutative functions).
    //
    private boolean commutative = false;
	
    // A list of arguments
	//
	// RhsSearchSuccessorFn calls this method to assign actual arguments (different 
    // instance of examples have different argument values)
	// 
	// private Vector<String> args = new Vector<String>();

    // A number of arguments required for the operator:: This is
    // needed even if one could read the arity off the argument list,
    // because at the time when a FeaturePredicate is instantiated by
    // RhsSearchSuccessorFn, no argument is assigned yet but still
    // need to know the arity.
    //
    private int arity;

    // An array of int showing arg types, which must be defined for each domain
    // 
    private int[] argValueType;

    // A return type of the user defined functions
    //
    private int returnValueType;

    // Type of arguments and output value
    //
    // As a search heuristic to prune irrelevant node expansion in RHS operator search,
    // the type of arguments for each operator must be specified. Basically, the 
    // search agent does not propose an operator sequence that has a type mismatch, 
    // namely, if an output from OP1 is fed into OP2, then the type of return value
    // of OP1 must be consistent with the type of argument of OP2.
    //
    // This must be specified for each domain as follows:
    //
    // By default, TYPE_OBJECT is compatible for any other type
    // 
    public static final int TYPE_OBJECT = Integer.MAX_VALUE;
    //
    public static final int TYPE_NULL = Integer.MIN_VALUE;

    // A reserved name for the void operator that doesn't do anything
    // 
    public static final String VOID_OP_NAME = "void";

    // A return value from the feature predicate
    // 
    public static final String TRUE_VALUE = "TRUE%fp@value";
    public static final String FALSE_VALUE = "FALSE%fp@value";

    // Cache for apply()
    // 
    /*
    private Hashtable<Vector<String>, String> applyCache = new Hashtable<Vector<String>, String>();
    */

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
    // Operator invocation
    // 
    
	/**
	 * For feature predicates (or any other boolean functions), return value should be RU.SYMBOL "FALSE" for 
	 * non-true computation. Anything else will be considered as true. 
	 * 
	 * Having the apply() method return null, false, true would still be a good idea for other modules; e.g., 
	 * J48 classifier will appreciate the difference between null (i.e., the function invocation does not make sense) 
	 * and false (i.e., the given case, represented as a set of arguments, falls in the false category). 
	 * @throws JessException 
	 * 
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	@Override
	public Value call(ValueVector jessArgs, Context c) throws JessException {

		Value value = null;
		
		ArrayList<String> argv = new ArrayList<String>();
		for (int i = 1; i < jessArgs.size(); i++) {
			argv.add(jessArgs.get(i).stringValue(c));
		}

		String returnValue = null;
		if (isValidArgument(argv)) {
			returnValue = apply(argv);
		}
		
		if (returnValue == null || returnValue.equals(FALSE_VALUE))
			value = new Value("FALSE",RU.SYMBOL);
		else
			value = new Value(returnValue,RU.STRING);
		
		return value;
	}

	/*
    public String cachedApply( Vector<String> argv ) {

    	String value = null;
    	
    	if (isValidArgument(argv)) { 

    		if ( getApplyCache().containsKey( argv ) ) { 

    			value = getApplyCache(argv);
    			
    		} else {
    		
    			try {

    				value = apply( argv );
        			putApplyCache( argv, value );
    				
    			} catch (Exception e) { e.printStackTrace(); }
    		}
    	}
    	
    	return value;
    }
    */
	
	// Verify if the given arguments make sense to call the user defined function
	public boolean isValidArgument(ArrayList<String> argv) throws JessException {
		
    	boolean isValidArgument = true;
    	
        for (int i = 0; i < argv.size(); i++) {
            
        	int givenType = valueType((String)argv.get(i));
        	int expectedType = getArgValueType(i);
            
        	if ( !isCompatibleType(givenType, expectedType) ) {
                isValidArgument = false;
                break;
            }
        }

        // TraceLog.out("isValidArgument(" + argv + ") @ " + this + " == " + isValidArgument);
        return isValidArgument;
    }
	
	// This method should be overwritten if type checking is more complicated than a simple comparison.
	// Therefore the visibility must be public
	// 
	public boolean isCompatibleType(int typeUncertain, int typeRequired){

		return (typeUncertain == typeRequired) || (typeRequired == TYPE_OBJECT );
    }
    
	
    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
    // Printing out the operator call in the Jess friendly form
    //
    
	/**
	 * Returns a Jess representation of the function call 
	 * 
	 * @param args
	 * @return
	 */
    public String actionStr( Vector<String> args ) {
    	
    	String argStr = "";
    	
    	for (String arg : args) {
    		argStr += arg + " ";
    	}
    	if (argStr.length() > 0) {
    		argStr = argStr.substring(0, argStr.length()-1);
    	}

    	return "(" + getName() + " " + argStr + ")";
    }
    
    public String actionStr() {
    	return "(" + getName() + ")";
    }

    
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getters and Setters 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	@Override
	public String getName() { return this.name; }
	public void setName(String name) { this.name = name; }
	
	/*
	public void setArgs( Vector<String> args ) { this.args = args; }
    public Vector<String> getArgs() { return this.args; }
    void addArgs( String arg ) { this.args.add( arg ); }
    */
    
    public int getArity() { return arity; }
    public void setArity( int arity ) { this.arity = arity; }

    public void setArgValueType(int[] argValueType) { this.argValueType = argValueType; }
    public int getArgValueType(int idx) {
    	return (idx < argValueType.length) ? argValueType[idx] : -1;
    }
    
    public void setReturnValueType(int type) { this.returnValueType = type; }
    int getReturnValueType() { return returnValueType; }

    /*
	private Hashtable<Vector<String>, String> getApplyCache() { return applyCache; }
	private String getApplyCache(Vector<String> argv) {	return this.applyCache.get(argv); }
	private void putApplyCache(Vector<String> argv, String value) {
		this.applyCache.put(argv, value);
	}
	*/

    public boolean isCommutative() { return this.commutative; }
    protected void setIsCommutative( boolean test ) { this.commutative = test; }
	
    

}
