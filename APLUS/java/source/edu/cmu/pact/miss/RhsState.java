/**
 * $RCSfile$
 *
 *	A state representation for searching RHS of the production
 *	rules.
 *
 * Created: Fri Dec 31 22:34:01 2004
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version $Id: RhsState.java 21616 2014-11-18 17:32:40Z nikolaos $
 */

package edu.cmu.pact.miss;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;

import jess.JessException;
import jess.Value;
import edu.cmu.pact.Utilities.trace;


// These are the nodes in the search space


public class RhsState implements Cloneable {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - 
    // -

    // = = = = = = = = = = 
    // A list of seeds upon which the "Input" is determined.
    // 
    // private Vector /* of MTVariable */ mtVariables = new Vector();
    // Add a seed
    // private void addMTvar( MTVariable var ) { this.mtVariables.add( var ); }

    // Number of variables in the state
    private int numVar = 0;

    // = = = = = = = = = = 
    // The AmlRete network
    //
    private AmlRete amlRete;
    private void setAmlRete( AmlRete amlRete ) { this.amlRete = amlRete; }
    AmlRete getAmlRete() { return this.amlRete; }
    
    // = = = = = = = = = = 
    // A list of the pairs of variable and expressions each
    // corresponds to a (bind ?var (operation ... )) statement in RHS
    // 
    private Vector /* of BindPair */ expList = new Vector();

    private double likelihood;
    
    private BindPair lookupBindPairByVar(String var) {
	for (int i = 0; i < expList.size(); i++) {
	    BindPair bindPair = (BindPair)expList.get(i);
	    if (var.equals(bindPair.getVar()))
		return bindPair;
	}
	return null;
    }
    Vector /* of BindPair */ getExpList() { return this.expList; }
    /*
    void pushExpList( String var, String exp ) {
	expList.add( new BindPair( var, exp ) );
    }
    */
    // Given a RHS operator, which has gotten arguments set by 
    // RhsSearchSuccessorFn.getSuccessors*(), verify if their type 
    // matches with the values computed by the operators in ancestor states
    boolean pushExpList( FeaturePredicate rhsOp ) {
    	boolean pushExpList = false;
    	boolean	typeMatch = argTypeMatch(rhsOp); 

    	if (typeMatch) {
    		BindPair existingBP = hasRepeatedBindPair(rhsOp);
    		
    		if(existingBP != null){
				pushVarList(existingBP.getVar());
				pushExpList = true;
				return pushExpList;
    		}
    		
    		String var = genVarSym();
    		BindPair bp = new BindPair( var, rhsOp ); 		
    		    		
    		//update this state's success-likelihood score
    		//          Vector foaWmes = getFoaWMEs(rhsOp.getArgs()); //returns the CommNames of the WMEs, instead of val*
    		//          this.likelihood = foaLikelihood(foaWmes); //returns the likelihood of the set of WMEs

    		expList.add( bp );

    		pushVarList( var );
    		pushExpList = true;
    	} 
    	return pushExpList;
      }
    
    
    private BindPair hasRepeatedBindPair(FeaturePredicate rhsOp) {
    	Vector expList = getExpList();

    	for(int i = 0; i < expList.size(); i++)
    	{
    		BindPair existingBP = (BindPair)expList.get(i);
			
    		if(existingBP.getExp() != null){
				if(existingBP.getExp().equals(rhsOp)){
					return existingBP;
				}
    		}
		}
    	
    	return null;
    }
    
    // Used to add a "null" operator indicating a seed

    /*
    String parseFoA(String s){	
	//after 2 '|'
	int i = 0;
	int barCount=0;
	char c;
	while(i<s.length()&&barCount<2) { //for each char in expString
		c = s.charAt(i);
		if (c=='|')
		    barCount++;
		i++;
	}
	return s.substring(i);	
    }
    */
    
    
    /** assumes independence: computes the product of the probability of each wme
     * 
     */
    private double foaLikelihood(Vector /*of String*/ foaWmes) {
        double product=1;        
        for (int i=0; i<foaWmes.size(); i++){
            String wme = (String) foaWmes.get(i);
            product*=wmeLikelihood(wme);
        }        
        return product;
    }
    
    
    private double wmeLikelihood(String wme) {
        int howManyBack = FoaGetter.howManyBack(wme);
        return 1 - 0.01*howManyBack;
    }
    
    
    
    void pushExpList(String foa) {
	
	// foa -> "MAIN::cell|commTable1_C1R1|-7y+3+6y"
	String foaValue = foa.split("\\|")[2];
	String var = genVarSym();

	// Make a BindPair with "var" and null as its expression,
	// which means that the "var" is a seed
	expList.add(new BindPair(var, FeaturePredicate.valueType(foaValue)));
	pushVarList(var);
    }

    // 
    void setExpList( Vector expList ) { this.expList = expList; }


    //this function tells us whether ALL arguments in rhsOp's arglist match the type of
    //the variable (found using the lookup).  Comment by Gustavo: 1 Nov 2006
    private boolean argTypeMatch(FeaturePredicate rhsOp) {
    	if(trace.getDebugCode("rhs-typechecking"))trace.out("rhs-typechecking", "entered argTypeMatch: rhsOp = " + rhsOp);
        boolean argTypeMatch = true;
        Vector args = rhsOp.getArgs();
        for (int i = 0; i < args.size(); i++) {
            String var = (String)args.get(i);
            BindPair bindPair = lookupBindPairByVar(var);
            if(trace.getDebugCode("rhs-typechecking"))trace.out("rhs-typechecking", "argTypeMatch: bindPair.getArgType() = " + bindPair.getArgType() + 
                    ", rhsOp.getArgValueType(i) = " + rhsOp.getArgValueType(i));
            //tmandel: Now checks if  types are compatible
            int givenType = bindPair.getArgType();
            int expectedType = rhsOp.getArgValueType(i);
            /*Checking if types are compatible...*/
           // if ( !FeaturePredicate.isCompatibleType(givenType, expectedType) ) {
            if ( !FeaturePredicate.typeMatcher(givenType, expectedType) ) {
                //if (bindPair.getArgType() != rhsOp.getArgValueType(i)) {
                argTypeMatch = false;
                break;
            }
        }
        trace.out("rhs-typechecking", "argTypeMatch: returning " + argTypeMatch);
        return argTypeMatch;
    }

    
    // Number of RHS operators found so far
    int numRhsOperators() {
        int numRhsOperators = 0;
        for (int i = 0; i < expList.size(); i++) {
            BindPair bindPair = (BindPair)expList.get(i);
            FeaturePredicate exp = bindPair.getExp();
            if ( exp != null ) {
                numRhsOperators++;
            }
        }
        
        return numRhsOperators;
        }
    
    // Returns a "var" for the "rhsOp" in the ExpList
    String getExpVar( FeaturePredicate rhsOp ) {

	String var = null;
	for ( int i = 0; i < expList.size(); i++ ) {

	    BindPair bindPair = (BindPair)expList.get(i);
	    FeaturePredicate exp = bindPair.getExp();
	    if ( exp != null && exp.equals( rhsOp ) ) {
		var = bindPair.getVar();
		break;
	    }
	}
	return var;
    }
    // Return TRUE if the variable is a seed variable
    boolean isGivenVar( String var ) {

	boolean test = true;

	for (int i = 0; i < expList.size(); i++) {

	    BindPair bindPair = (BindPair)expList.get(i);
	    if ( bindPair.getVar().equals( var ) ) {

		if ( bindPair.getExp() != null ) {
		    test = false;
		}
		break;
	    }
	}
	return test;
    }

    // = = = = = = = = = = 
    // A list of variables that are not used in algebraic operations
    // 
    private Vector /* of String */ varList = new Vector();
    void pushVarList( String var ){ varList.add( var ); }
    Vector /* of String */ getVarList() { return this.varList; }
    void setVarList( Vector varList ) { this.varList = varList; }
    void removeVarList( String var ) { this.varList.remove( var ); }
    void removeVarList( Vector /* of String */ vars ) {
	for (int i = 0; i < vars.size(); i++) {
	    this.varList.remove( (String)vars.get(i) );
	}
    }


    // String getterClassStr = getFoaGetter().substring(0,getFoaGetter().lastIndexOf('.'));
    // String getterMethodStr = getFoaGetter().substring(getFoaGetter().lastIndexOf('.') +1);

    // = = = = = = = = = =
    // Matching function for hasValidOperations
    // 
    // A matcher method must take two String arguments and return a non-null String, 
    // which by definition means that the two Strings are semantically equivalent
    ///////////////////////
    //
    private static String matcher = null;
    // 
    private String getMatcher() { return matcher; }
    public void setMatcher(String theMatcher) {
        matcher = theMatcher;
    }
//    private static Method matcherMethod = null;
//    private Method getMatcherMethod() {
//        if (matcherMethod == null) {
//            try {
//                String matcherMethodStr = getMatcher().substring(getMatcher().lastIndexOf('.')+1);
//                Class[] parameterTypes = new Class[] {String.class, String.class};
//                matcherMethod = getMatcherClass().getMethod(matcherMethodStr, parameterTypes);
//            } catch (Exception e ) {
//                e.printStackTrace();
//            }
//        } 
//        return matcherMethod;
//    }
    private static Class matcherClass = null;
    private Class getMatcherClass() {
        if (matcherClass == null) {
            try {
//                String matcherClassStr = getMatcher().substring(0, getMatcher().lastIndexOf('.'));
//                matcherClass = Class.forName(matcherClassStr);
                matcherClass = Class.forName(getMatcher());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return matcherClass;
    }
    private static FeaturePredicate matcherInstance = null;
    private FeaturePredicate getMatcherInstance() {
        if (matcherInstance == null) {
            try {
            	if(trace.getDebugCode("miss"))trace.out("miss", "matcherClass = " + getMatcherClass());
                matcherInstance = (FeaturePredicate)getMatcherClass().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return matcherInstance;
    }
    
    // -
    // - Constructor - - - - - - - - - - - - - - - - - - - - 
    // -

    /**
     * Creates an initial search state with the <seeds> 
     *
     * @param wmeTypeFile a <code>String</code> value
     * @param numSeeds 
     * @param matcher 
     */
    public RhsState( String wmeTypeFile, Vector /* String */ seeds, String matcher ) {
	
    	
    	
        setAmlRete( new AmlRete() );
        setMatcher( matcher );
        // setSeeds(seeds);
	try {
	    getAmlRete().reset();
	    //getAmlRete().executeCommand( "(batch \"" + wmeTypeFile + "\")");
        ClassLoader cl = this.getClass().getClassLoader();
        trace.out("miss","RhsState reading file " + wmeTypeFile);
        InputStream is = cl.getResourceAsStream(wmeTypeFile);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        try {
        		trace.out("miss","Paring wmeTypes file with AmlRete....");
				Value val = getAmlRete().parse(br, false);
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JessException e) {
			e.printStackTrace();
		}

	for (int i = 0; i < seeds.size(); i++) {
	    // Put a "null" operator indicating a seed into the ExpList
	    pushExpList((String)seeds.get(i));
	}
	if(trace.getDebugCode("miss"))trace.out("miss", "RhsState initialized");
    }

    
    // -
    // - Methods - - - - - - - - - - - - - - - - - - - -
    // -

    //12Dec2006: this function indicates whether the RHS-state explains 'instruction'.(?)
    //
    //6March2007: it works by evaluating the expression and then comparing
    //against the BRD input.
    //the binding part creates a unifier, and puts partial evaluations there.
    //
    //side-effects on this RhsState:?
    boolean hasValidOperations(Instruction instruction) {
    	// Extract the seeds from the instruction
    	// Vector /* of String */ seeds = (Vector)instruction.getSeeds().clone();

    	Vector /* of String */ seeds = (Vector)instruction.getSeeds();

    	//System.out.println("instruction = " + instruction);
    	
    	int seedIndex = 0;
    	// For variable unification
    	// Hashtable unifier = new Hashtable();
    	HashMap unifier = new edu.cmu.pact.miss.HashMap();
    	//System.out.println("seeds = " + seeds);
    	
    	// Extract the list of expressions from the state <rhs>
    	Vector /* of BindPair */ expList = getExpList();
    	
    	// Apply a chain of expressions to see if the "input" hold
    	for (int i = 0; i < expList.size(); i++) {
    		BindPair bindPair = (BindPair)expList.get(i);
    		String val = null;
    		String var = bindPair.getVar();
    		FeaturePredicate exp = bindPair.getExp();


    		// If the "exp" is null, which means that the "var" is an
    		// independent (seed) variable, then read the value off
    		// the seed-WME
    		if ( exp == null  ) {
    			// Get a first seed
    			String seed="";
    			if (seedIndex<seeds.size())
    				 seed = (String)seeds.elementAt(seedIndex++);
    			// Get the last "token" from the seed, which looks like 
    			// "MAIN::cell|commTable1_C1R1|3.48+6.82x"
    			val = seed.substring( seed.lastIndexOf( '|' ) +1 );
    		} else {
    			//args is a Vector. Need iterator?
    			val = evalExp( exp, unifier );
    			setLastVal( val );
    			// If the evaluation turned out to be invalid, then abort
    		}

    		if (( val == null ) || val.equals("")){
    			return false;
    			//WAS: break;   but breaking out of the 'for' will still put "" into unifier, which is bad.
    		}

    		// Update the unification 
    		unifier.put( var, val );
    	}


	boolean isValid = compairInput(getLastVal(), instruction.getInput());
	return isValid;

    }	
    
    private boolean compairInput(String lastVar, String stInput) {

	boolean isValid = (lastVar != null);

	if (isValid) {
		//if(trace.getDebugCode("ssDebug"))trace.out("ssDebug", "Input matcher comparing " + lastVar + " and " + stInput);
	    if (getMatcher() == null) {

		isValid = lastVar.equals(stInput);

	    } else {

		String cachedResult = cachedCompairInput(lastVar, stInput);
		// String cachedResult = null;

		if (cachedResult != null) {
		    isValid = cachedResult.equals("T");

		} else {
		    try {
			// Method matcher = getMatcherMethod();
			FeaturePredicate matcherInstance = getMatcherInstance();			
			// Object[] args = new Object[] {lastVal, stInput};
			//String result = (String)matcher.invoke(matcherInstance, args);
			String result = matcherInstance.inputMatcher(lastVar, stInput);
			putCachedCompairInput(lastVar, stInput, result);
			isValid = (result != null);
                        //remove 'result'
                        //isValid = matcherInstance.inputMatcher(lastVal, stInput);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	}
	return isValid;
    }
    
    // private static edu.cmu.pact.miss.HashMap compairInputCache = new edu.cmu.pact.miss.HashMap();
    private static HashMap compairInputCache = new HashMap();

    private String cachedCompairInput(String lastVar, String stInput) {
        
        String cachedCompairInput = null;
        
        /*
        edu.cmu.pact.miss.HashMap inputCache = 
            (edu.cmu.pact.miss.HashMap)compairInputCache.get(lastVar);
            */
        HashMap inputCache = (HashMap)compairInputCache.get(lastVar);
        
        if (inputCache != null) {
            cachedCompairInput = (String)inputCache.get(stInput);
        }
        
        return cachedCompairInput;
    }

    private void putCachedCompairInput(String lastVar, String stInput, String result) {
        
        /*
        edu.cmu.pact.miss.HashMap inputCache = 
            (edu.cmu.pact.miss.HashMap)compairInputCache.get(lastVar);
            */
        HashMap inputCache = (HashMap)compairInputCache.get(lastVar);
        
        if (inputCache == null) {
            // inputCache = new edu.cmu.pact.miss.HashMap();
            inputCache = new HashMap();
            compairInputCache.put(lastVar, inputCache);
        }

        inputCache.put(stInput, result == null ? "F" : "T");
    }

    public static void printInputCache() {
        Iterator keys = compairInputCache.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            HashMap hashMap = (HashMap)compairInputCache.get(key);
            Iterator values = hashMap.keySet().iterator();
            while (values.hasNext()) {
                String value = (String)values.next();
                String match = (String)hashMap.get(value);
                System.out.println(key + (match.equals("T") ? " == " : " != ") + value);
            }
        }
    }
    
    // The value of an expression evaluated lastly
    private String lastVal = null;
    String getLastVal() { return this.lastVal; }
    void setLastVal( String lastVal ) { 
    	this.lastVal = lastVal; 
    }

    
    //e.g. evalExp( (last-var-term ?val1), {?val2=2+2y, ?val0=2+2y, ?val1=-9y+9})
    private String evalExp( FeaturePredicate exp, HashMap unifier ) {        

    	// ----- Debug -----
    	// RhsGoalTest.setGoalTest( exp + " [" + unifier + "]" );
    	// ----- Debug -----

    	Vector /* of String */ argsUnified = new Vector();

    	Vector /* of String */ args = exp.getArgs();

    	for (int i = 0; i < args.size(); i++) {
    		String argStr = (String)args.get(i);
    		argsUnified.add( unifier.get( argStr ) );
    	}

    	/*
	Iterator iterator = args.iterator();
	while ( iterator.hasNext() ) {
	    String argStr = (String)iterator.next();
	    argsUnified.add( unifier.get( argStr ) );
	}
    	 */

    	String value = null;
    	try {
    		value = exp.cachedApply( argsUnified );
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	return value;
    }
    
    
    public Object clone() {

	RhsState state = null;
	try {
	    state = (RhsState)super.clone();
	    // state.setAmlRete( (AmlRete)getAmlRete().clone() );
	    state.setExpList( (Vector)getExpList().clone() );
	    state.setVarList( (Vector)getVarList().clone() );

	} catch (CloneNotSupportedException e) {
	    e.printStackTrace();
	}

	return state;
    }

    String genVarSym() {
	return "?val" + String.valueOf( numVar++ );
    }

    public String toString() {

	String expList = this.expList.toString();
	String varList = this.varList.toString();
	return "<RhsState> expLlist: " + expList + " varList: " + varList;
    }
}

//
// end of $RCSfile$
// 
