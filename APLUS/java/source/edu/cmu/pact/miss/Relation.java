/**
 * f:/Project/CTAT/ML/ISS/miss/Relation.java
 *
 *	Represent a single "relation" appearing in input data for FOIL
 *
 * Created: Fri Feb 25 17:15:00 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss;

import java.io.Serializable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import mylib.CombinatoricException;
import mylib.Permutations;
import edu.cmu.pact.Utilities.trace;

public class Relation implements Serializable {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

	private static final long serialVersionUID = 5883203199327368417L;
	
    // FeaturePredicate object to be applied
    //
	private FeaturePredicate predicate;
	FeaturePredicate getPredicate() { return this.predicate; }
	private void setPredicate( FeaturePredicate predicate ) {
		this.predicate = predicate;
	}
	// Feature predicate hash
	private HashMap featurePredicateHash;
	private void setFeaturePredicateHash( HashMap featurePredicateHash ) {
		this.featurePredicateHash = featurePredicateHash;
	}
	private FeaturePredicate getFeaturePredicate( String name ) {
		return (FeaturePredicate)featurePredicateHash.get( name );
	}
	private void putFeaturePredicate( String name, FeaturePredicate fp ) {
		this.featurePredicateHash.put( name, fp );
	}

    // "Header" of the relation 
    private String name;
    /**
     * Describe <code>getName</code> method here.
     *
     * @return a <code>String</code> value
     */
    String getName() { return this.name; }
    private void setName( String name ) { this.name = name; }

    // Boolean flag indicating if this relation is a target relation
    private boolean targetRelation = false;
    private boolean isTargetRelation() { return this.targetRelation; }
    private void setTargetRelation( boolean flag ) {
    	this.targetRelation = flag;
    }

    // Number of agruments appering in this relation
    private int arity;
    int getArity() { return this.arity; }
    private void setArity( int arity ) {
    	this.arity = arity;
    }

    // Number of INPUT arguments appearing in this relation
    private int inputArity;
    int getInputArity() { return this.inputArity; };
    private void setInputArity( String key ) {
    	this.inputArity = 0;
    	for (int i = 0; i < key.length(); i++) {
    		if ( key.charAt(i) == '#' ) {
    			this.inputArity++;
    		}
    	}
    }

    // A sequence of #'s and -'s showing the keys for this relation
    private String key;
    String getKey() { return this.key; }
    private void setKey( String key ) { this.key = key; }

    //  A list of "constants", which is a vector of String each
    //  represent a value to be assigned to this relation
    private Vector /* V of String */ positiveTuples = new Vector();
    Vector /* V of String */ getPositiveTuples() {
    	return this.positiveTuples;
    }
    void addPositiveTuple( Vector tuple ) {
    	if (isValidTupleType(tuple)) {
    		if ( !alreadyIn( getPositiveTuples(), tuple ) ) {
    			this.positiveTuples.add( tuple );
    		}
    		if ( alreadyIn( getNegativeTuples(), tuple ) ) {
    			if(trace.getDebugCode("miss"))trace.out("miss", tuple + "'s been claimed as a positive tuple.");
    			getNegativeTuples().remove( tuple );
    		}
    	}
    }

    private Vector /* V of String */ negativeTuples = new Vector();
    public Vector /* V of String */ getNegativeTuples() {
    	return this.negativeTuples;
    }

    // Prevent to print out tuples with "null" as its type value. 
    // Function calls with output variables might involve such tuple
    public static final int NO_NULL = 1;
    private Vector /* V of String */ getNegativeTuples( int mode ) {
	
	switch (mode) {
	case NO_NULL:
	    return getNegativeTuplesNoNull();
	}
	
	return null;
    }
    private Vector /* V of String */ getNegativeTuplesNoNull() {
	
	Vector /* V of String */ negativeTuples = new Vector();

	Vector /* V of String */ allNegativeTuples = getNegativeTuples();
	for (int i = 0; i < allNegativeTuples.size(); i++) {
	    Vector /* String */ tuple = (Vector)allNegativeTuples.get(i);
	    if ( !tuple.contains( null ) ) {
		negativeTuples.add( tuple );
	    }
	}
	
	return negativeTuples;
    }
    
    // Add a <tuple> only when it is not a member of positive tupels.
    // Called only for the target relation
    void addNegativeTuple( Vector /* String */ tuple ) {
//        new Exception ().printStackTrace();
        if (isValidTupleType(tuple)) {
            // 2/13/2006
	    // FOIL can not deal with "null"...
	    if ( !tuple.contains(null) ) {
		if ( !alreadyIn(getPositiveTuples(), tuple) &&  
			!alreadyIn(getNegativeTuples(), tuple) ) {
		    getNegativeTuples().add( tuple );
		}
	    }
	}
    }
    
    void addExplicitNegativeTuple( Vector tuple ) {
    	
    	if (isValidTupleType(tuple)) {
    		if ( !alreadyIn( getNegativeTuples(), tuple ) ) {
    		this.negativeTuples.add( tuple );
    	    }
    	    if ( alreadyIn( getPositiveTuples(), tuple ) ) {
    	    	if(trace.getDebugCode("miss"))trace.out("miss", tuple + "'s been claimed as a negative tuple.");
    		getPositiveTuples().remove( tuple );
    	    }
    	}
        }

    // Returns TRUE if the Vector v is a member of vv
    boolean alreadyIn( Vector /* of Vector */ vv, Vector /* of String */ v ) {

	boolean test = false;
	for (int i = 0; i < vv.size(); i++) {

	    Vector v0 = (Vector)vv.get(i);
	    if ( v0.equals( v ) ) {
		test = true;
		break;
	    }
	}
	return test;
    }

    // Return all positive and negative tuples
    private Vector /* V of String */ getAllTuples() {

	Vector allTuples = new Vector();
	allTuples.addAll( getPositiveTuples() );
	allTuples.addAll( getNegativeTuples() );
	return allTuples;
    }

    // Number of positive and negative tuples
    int numAllTuples() {
	return getPositiveTuples().size() + getNegativeTuples().size();
    }
    
    int numPosTuples() {
        return getPositiveTuples().size();
    }

    int numNegTuples() {
        return getNegativeTuples().size();
    }
    
    boolean hasTuples() {
	return numAllTuples() != 0;
    }
    boolean hasTuples( int mode ) {
	
	switch (mode) {
	case NO_NULL:
	    return hasTuplesNoNull();
	}
	
	return false;
    }
    boolean hasTuplesNoNull() {
	boolean hasTuplesNoNull =
	    !getPositiveTuples().isEmpty() || !getNegativeTuples(NO_NULL).isEmpty();
	return hasTuplesNoNull;
    }

    // A list of instructions
    //
    transient Vector /* of Instruction */ instructions = new Vector();

    // FALSE value for comparison
    //
    // Value FALSE_VALUE = new Value( false );

    // - - - - - - - - - - - - - - - - - - - - 
    // Argument Type for the target relation
    private int[] targetArgType = null;
    public int getTargetArgType(int i) {
    	return targetArgType[i];
    }
    private void initTargetArgType() {
    	targetArgType = new int[getArity()];
    }
    private void setTargetArgType(int i, int type) {
    	targetArgType[i] = type;
    }
    void setTargetArgType(Vector /* String */ focusOfAttention) {
    	initTargetArgType();
    	// Store the type of arguments for the target relation
    	// Skip the first FoA, which is the "input" 
    	//Added for debug. To be removed.
    	if(trace.getDebugCode("miss"))trace.out("miss", "Inside setTargetArgType:" + focusOfAttention.size());
    	for (int i = 1; i < focusOfAttention.size(); i++) {
    		String foa = (String)focusOfAttention.get(i);
    		String foaValue = foa.split("\\|")[2];  
    		setTargetArgType(i-1, FeaturePredicate.valueType(foaValue));
    	}
    }
    

    
    // Returns the type of the n-th argument, which is defined in  
    // FeaturePredicate.class
    public int getArgType(int n) {
    	int argTypeID = -1;
    	// Fri Nov 17 11:45:26 EST 2006 :: Noboru 
    	// I suspect that this would cause trouble with the relations 
    	// with output (i.e., with the key '-')
    	FeaturePredicate predicate = getPredicate();
    	if (predicate != null) {
    		argTypeID = predicate.getArgValueType(n);
    		
    	} else {
    		argTypeID = getTargetArgType(n);

    	}
    	return argTypeID;
    }

    
    private boolean isValidTupleType(Vector /* String */ args) {
	boolean isValidArgument = true;

	for (int i = 0; i < args.size(); i++) {	
		 if (FeaturePredicate.valueType((String)args.get(i)) != getArgType(i)) {
			isValidArgument = false;
		break;
	    }
	}

	return isValidArgument;
    }
    
    
    
    // -
    // - Construction - - - - - - - - - - - - - - - - - - - - - -
    // -

    /**
     * Creates a new <code>Relation</code> instance.
     * For relations use in the FoilData
     *
     * @param predicateDef in the form of "name(key,key,...)"
     */
    public Relation( String predicateDef, HashMap featurePredicateHash ) {

	int leftParenIndex = predicateDef.indexOf( '(' );
	String className = predicateDef.substring( 0, leftParenIndex );
	String keyStr = predicateDef.substring( leftParenIndex +1,
						predicateDef.length() -1 );

	// className might has dots '.' hence need to strip them off
	int dotIndex = className.lastIndexOf( '.' );
	String name = dotIndex == -1 ? className :
	    className.substring( dotIndex +1, className.length() );

	String theKey = "";
	StringTokenizer keys = new StringTokenizer( keyStr, ", " );
	while ( keys.hasMoreElements() ) {
	    theKey += keys.nextToken();
	}

	setName( name );
	setKey( theKey );
	setArity( theKey.length() );
	setInputArity( theKey );
	setFeaturePredicateHash( featurePredicateHash );

	FeaturePredicate predicate = getFeaturePredicate( className );

	if ( predicate == null ) {
		predicate=FeaturePredicate.getPredicateByClassName(className);
		putFeaturePredicate( className, predicate );
	}
	setPredicate( predicate );
    }

    /**
     * Creates a new <code>Relation</code> instance.
     * For the target relation used in the FoilData
     *
     * @param name a <code>String</code> value
     * @param arity an <code>int</code> value
     */
    public Relation( String name, int arity, HashMap featurePredicateHash) {

    	if(trace.getDebugCode("foasearch"))trace.out("foasearch", "constructing Relation \""+name+"\" with arity = " +
                arity);
        
        String theKey = "";
	// TODO Chunking decomposition must need "-" (output) as the key
	for (int i = 0; i < arity; i++) {
	    // The input key
	    theKey += "#";
	}

	setFeaturePredicateHash( featurePredicateHash );
	setTargetRelation( true );
	setName( name );
	setKey( theKey );
	setArity( arity );
	setInputArity( theKey );

    }
    // -
    // - Methods  - - - - - - - - - - - - - - - - - - - - - - - -
    // -
    
    // Given a feature predicate, extract tuple from its cache for
    // apply(), and set up tuples
    void setTuple( FeaturePredicate predicate ) {

	if ( predicate != null ) {

	    Iterator keys = predicate.applyCasheKeys();
	    while ( keys.hasNext() ) {
	    	
		Vector args = (Vector)((Vector)keys.next()).clone();
		String value = predicate.getApplyCache( args );
		// For predicates that has "output" value as the last
		// argument
		if ( args.size() != getArity() ) {
		    args.add( value );
		}

		if ( value != null ) {
		    addPositiveTuple( args );
		} else {
		    addNegativeTuple( args );
		}
	    }
	}
    }

    Vector /* String */ evalRelation( Vector /* String */ values ) {
        
        return (getArity() > 1) ?
        	testPredicate( values, getArity() ) : 
        	    testPredicate( values );
    }
    
    private Vector testPredicate( Vector /* String */ values ) {

	Vector /* String */ extValues = new Vector();

	for ( int i = 0; i < values.size(); i++ ) {

	    // Make an argument list
	    Vector arg = new Vector();
	    arg.add( (String)values.get(i) );
	    // Evaluate the predicate, which caches the result
	    String returnValue = applyPredicate( arg );
	    
	    // When the predicate has an output value, then keep it
	    if ( returnValue != null && getArity() != getInputArity() ) {
		extValues.add(returnValue);
	    }
	}
	return extValues.isEmpty() ? null : extValues;
    }

    private Vector testPredicate( Vector /* String */ values, int arity ) {
    	//if (arity > values.size() ) return null;
	Vector extValues = new Vector();
	
	Iterator iter=Relation.permuteArgs(values,this.getArity()).iterator();
	while(iter.hasNext()) {

	    // Make an argument list
	    Vector args=(Vector)iter.next();
	    // Evaluate the predicate, which caches the result
	    String returnValue = applyPredicate( args );

	    // When the predicate has an output value, then keep it
	    if ( returnValue != null && getArity() != getInputArity() ) {
		extValues.add( returnValue );
	    }
	}
	return extValues.isEmpty() ? null : extValues;
    }
    
    /**
     * 
     * @param values a Vector containing the values to be permuted
     * @param arity the number of arguments to permute on
     * @return a Vector of Vectors containing all sets of input arguments derived from values
     */
    public static Vector /*of Vector*/ permuteArgs(Vector values,int arity)
	{
    	Object[] vIndex = new Object[ values.size() ];
    	for (int i = 0; i < values.size(); i++) {
    	    vIndex[i] = new Integer(i);
    	}

    	Permutations vc = null;
    	try 
    	{
    	    vc = new Permutations( vIndex,arity );
    	} catch (CombinatoricException e) {
    	    e.printStackTrace();
    	    System.err.println("Relation.permuteArgs()'s gotten a fatal problem.");
    	    System.err.println("values = " + values + ", arity = " + arity);
            System.err.println("exiting...");
            System.exit(0);
    	}
    	Vector args=new Vector();
    	while ( vc.hasMoreElements() ) 
    	{
    	    // Make an argument list
    	    Object[] indexes = (Object[])vc.nextElement();
    	    Vector curArgs = new Vector();
    	    for (int i = 0; i < arity; i++) 
    	    {
    	    	int index = ((Integer)indexes[i]).intValue();
    	    	curArgs.add( values.get(index) );
    	    }
    	    args.add(curArgs);
    	}
    	return args;
    	
	}

    private String applyPredicate( Vector /* String */ args ) {

	/* 
	trace.out("args = ");
	for (int i = 0; i < args.size(); i++) {
	    trace.out((String)args.get(i) + " ");
	}
	*/

	String value = getPredicate().cachedApply( args );

	/*
	if ( value != null ) {
	    
	    Vector tuple = (Vector)args.clone();
	    
	    // If the predicate (i.e.,this relation) has an output value, 
	    // then add it to the end of the tuple.
	    if ( getArity() != getInputArity() ) {
		tuple.add( value );
	    }
	    // This must be redundunt, because setTuple does this
	    // addPositiveTuple( tuple );
	}
	*/
	
	return value;
    }

    /**
     * Returns a list (Vector) of values appearing in the n-th
     * position accross all tuples.  Duplications excluded.
     *
     * @param n an <code>int</code> value
     * @return a <code>Vector</code> value
     **/
    Vector /* String */ getTypesFor( int n ) {

	Vector /* V of String */ allTuples = getAllTuples();
	Vector types = new Vector();
	for (int i = 0; i < allTuples.size(); i++) {
	    Vector /* String */ tuple = (Vector)allTuples.get(i);
	    String constant = (String)tuple.get(n);
	    if ( !types.contains( constant ) ) {
		types.add( constant );
	    }
	}
	return types;
    }

    String v2string( Vector v ) {
	if (v == null) return null;
	String va = "";
	for (int i = 0; i < v.size(); i++) {
	    va += (String)v.get(i) + " ";
	}
	return va;
    }

    /**
     * Return a string representation of a relation in the following format:
     *
     *		*name(type, type, ..., type) key/key/.../key
     *		positive tuple
     *		positive tuple
     *		    ...
     *		;
     *		negative tuple
     *		negative tuple
     *		    ...
     *		.
     *
     * @return a <code>String</code> value
     **/
    /*
    public String toString() {
	return toString( -1, "" );
    }
    */
    public String toString() {

	// Head
	String relationHead = (isTargetRelation() ? "" : "*" ) + getName();
	relationHead += "(";

	for (int i = 0; i < getArity(); i++) {
	    relationHead += FoilData.TYPE_NAME + getArgType(i);
	    if ( i < getArity() -1 ) {
		relationHead += ", ";
	    }
	}
	relationHead += ")";
	// Key
	relationHead += " " + getKey() + "\n";

	// Number of input arguments
	int inputArity = getKey().indexOf('-');
	if ( inputArity < 0 ) inputArity = getKey().length();
	
	// Tuples
	String relationStr = "";
	// Positive tuples
	Vector /* V of String */ positiveTuples = getPositiveTuples();

	for (int i = 0; i < positiveTuples.size(); i++) {
	    Vector /* String */ posTuple = (Vector)positiveTuples.get(i);
	    relationStr += tupleString( posTuple ) + "\n";
	}
	
	// Negative tuples, if any...
	Vector /* V of String */ negativeTuples = getNegativeTuples(); 
	if ( !negativeTuples.isEmpty() ) {
	    relationStr += ";\n";
	    for (int i = 0; i < negativeTuples.size(); i++) {
		Vector /* String */ negTuple = (Vector)negativeTuples.get(i);
		relationStr += tupleString( negTuple ) + "\n";
	    }
	}
	
	return relationHead + relationStr + ".";
    }

    public String toString( int varNo, String varName ) {
	return toString( varNo, varName, null );
    }

    public String toString(int varNo, String varName, Vector validArgs) {

	String relationHead = "";
	relationHead += (isTargetRelation() ? "" : "*" ) + getName();
	if ( 0 <= varNo ) {
	    relationHead += "(";
	    for (int i = 0; i < getArity(); i++) {
		/*
		// ajzana changed to use one type for every predicate 
		// other than the target relation
		if(isTargetRelation()) {
		    relationHead += varName + (varNo+i); 
		} else {
		    relationHead += varName + (varNo);
		}
		*/
		relationHead += varName + getArgType(i);
		if ( i < getArity() -1 ) {
		    relationHead += ", ";
		}
	    }
	    relationHead += ")";
	}
	relationHead += " " + getKey() + "\n";

	// Number of input arguments
	int inputArity = getKey().indexOf('-');
	if ( inputArity < 0 ) inputArity = getKey().length();
	
	String relationStr = "";
	boolean validRelationFound = false; 
	// List positive tuples
	for (int i = 0; i < getPositiveTuples().size(); i++) {
	    Vector /* String */ posTuple = (Vector)getPositiveTuples().get(i);
	    if ( validArgs == null || isValidArgs( posTuple, inputArity, validArgs ) ) {
		relationStr += tupleString( posTuple ) + "\n";
		validRelationFound = true;
	    }
	}
	// List negative tuples, if any...
	Vector /* V of String */ negativeTuples = getNegativeTuples(); 
	if ( !negativeTuples.isEmpty() ) {
	    relationStr += ";\n";
	    for (int i = 0; i < negativeTuples.size(); i++) {
		Vector /* String */ negTuple = (Vector)negativeTuples.get(i);
		if ( validArgs == null || isValidArgs( negTuple, inputArity, validArgs ) ) {
		    relationStr += tupleString( negTuple ) + "\n";
		    validRelationFound = true;
		}
	    }
	}
	
	return validRelationFound ? relationHead + relationStr + "." : null;
    }

    /**
     * Return true if the first <arity> arguments in <args> are all in 
     * the <validArgs>
     *   
     * @param args
     * @param arity
     * @param validArgs
     * @return
     */
    private boolean isValidArgs(Vector args, int arity, Vector validArgs) {
	boolean isValidArgs = true;
	
	for (int i = 0; i < arity; i++) {
	    if (!validArgs.contains((String)args.get(i))) {
		isValidArgs = false;
		break;
	    }
	}
	
	return isValidArgs;
    }
    
    private String tupleString( Vector /* String */ tuple ) {

	String tupleStr = "";
	for (int i = 0; i < tuple.size(); i++) {
	    tupleStr += (String)tuple.get(i);
	    if ( i < tuple.size() -1 ) {
		tupleStr += ", ";
	    }
	}
	return escapeECs( tupleStr );
    }

    /**
     * Put an escape character '/' in from of a character that must be
     * escaped (e.d., a parenthesis)
     *
     * @param nativeStr a <code>String</code> value
     * @return a <code>String</code> value
     **/
    private String escapeECs( String nativeStr ) {

	// Escape open parenthesis
	String openP = nativeStr.replaceAll( "\\(", "\\\\(" );
	// Escape close parenthesis
	String closeP = openP.replaceAll( "\\)", "\\\\)" );

	String commas=FoilData.replaceCommas(closeP);
	return commas;
    }
    
}

//
// end of f:/Project/CTAT/ML/ISS/miss/Relation.java
// 
