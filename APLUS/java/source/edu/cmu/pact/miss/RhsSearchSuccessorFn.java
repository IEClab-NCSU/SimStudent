/**
 * $RCSfile$
 *
 *	A class for generating a list of successor states
 *
 * Created: Fri Dec 31 22:40:09 2004
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version $Id: RhsSearchSuccessorFn.java 13832 2012-06-19 21:23:50Z keiser $
 */

package edu.cmu.pact.miss;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import mylib.Combinations;
import mylib.CombinatoricException;
import mylib.Permutations;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class RhsSearchSuccessorFn implements SuccessorFunction {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - - 
    // -

    /*
    private final String[] monomialOp = { "mod10", "dev10" };
    private final String[] binomialOp = { "-", "/", "mod" };
    private final String[] binomialOpCommutative = { "+", "*" };
    */

    // A class name of the VoidOp
    final String VOID_OP_CLASS = "edu.cmu.pact.miss.VoidOp";

    // A list of RhsOp's used to compose
    Vector /* String */ rhsOpList = new Vector();
    Vector /* String */ getRhsOpList() { return this.rhsOpList; }
    void setRhsOpList( Vector /* String */ rhsOpList ) {
	this.rhsOpList = rhsOpList;
    }
    private void addRhsOpList( String rhsOp ) { this.rhsOpList.add(rhsOp); }

    private void initRhsOpList( String fileName ) {

	BufferedReader reader = null;
	try {
	    reader = new BufferedReader(new FileReader(fileName));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}

	try {
	    String rhsOp = null;
	    while ( ( rhsOp = reader.readLine() ) != null ) {
		// Strip comment off
		int commentPos = rhsOp.indexOf(';');
		if ( commentPos != -1 ) {
		    rhsOp = rhsOp.substring( 0, commentPos );
		}
		// Strip spaces off
		rhsOp = rhsOp.replaceAll( " ", "" );
		if ( rhsOp.length() > 0 ) {
		    addRhsOpList( rhsOp );
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

	// Add the VoidOp to the operator list
	addRhsOpList( VOID_OP_CLASS );
    }

    // A pool of RHS operators.  There must be only one instance for
    // each operator through out the search so that
    // FeaturePredicate.cacheApply() works properly
    HashMap rhsOpCache = null;
    void setRhsOpCache( HashMap rhsOpCache ) { this.rhsOpCache = rhsOpCache; }
    FeaturePredicate getRhsOpCache( String rhsOpName ) {
	if ( rhsOpCache != null ) {
	    return (FeaturePredicate)rhsOpCache.get( rhsOpName );
	} else {
	    return null;
	}
    }
    void putRhsOpCache( String rhsOpName, FeaturePredicate rhsOp ) {
	if ( rhsOpCache != null ) {
	    rhsOpCache.put( rhsOpName, rhsOp );
	}
    }

    // Enables or Disables the heuristic based Iternative Deepening Search
    // If it is enabled then the operator sequence is searched on the basis
    // of frequency count.
    private boolean heuristicBasedIDS = false;

    public boolean isHeuristicBasedIDS() {
		return heuristicBasedIDS;
	}
    
	public void setHeuristicBasedIDS(boolean heuristicBasedIDS) {
		this.heuristicBasedIDS = heuristicBasedIDS;
	}
    
    // -
    // - Constructors - - - - - - - - - - - - - - - - - - - - - - - 
    // -

	/**
     * Creates a new <code>RhsSearchSuccessorFn</code> instance.
     *
     * @param rhsOpFile a name file of the from which a list of RhsOp
     * classes for RHS composition must read
     **/
    /*
    public RhsSearchSuccessorFn( String rhsOpFile, HashMap rhsOpCache ) {

	// Store hash for FeaturePredicate 
	setRhsOpCache( rhsOpCache );
	// initialize rhsOpList
	initRhsOpList( rhsOpFile );
    }
    */

    // This must be here for its subclass
    public RhsSearchSuccessorFn() {}

    public RhsSearchSuccessorFn( Vector rhsOp, HashMap rhsOpCache ) {

	// Store hash for FeaturePredicate 
	setRhsOpCache( rhsOpCache );
	// initialize rhsOpList
	setRhsOpList( rhsOp );
    }

    // -
    // - Methods - - - - - - - - - - - - - - - - - - - - - - - - - 
    // -

    // Implementation of aima.search.framework.SuccessorFunction

    /**
     * Describe <code>getSuccessors</code> method here.
     *
     * @param object an <code>Object</code> value
     * @return a <code>List</code> value
     */
    public final List getSuccessors(final Object object) {

    	// A list of successor states
    	List successors = new ArrayList();

    	// A current state
    	RhsState rhs = (RhsState)object;

    	// Sat Sep 30 21:20:15 LDT 2006 :: Noboru
    	// Search must fail when it took too long... 
    	if (rhs.numRhsOperators() <= SimSt.MAX_RHS_OPS && !SimSt.isRunningOutOfTime("RHS")) {

    		Vector /* of String */ varList = rhs.getVarList();

    		// Initialize an array used to calculate combinations of
    		// variables for polynomial operators
    		Object[] varArray = varList.toArray();
    		// Object[] varArray = vectorToArray( varList );
    		
    		Iterator rhsOpList = getRhsOpList().iterator();
    		while ( rhsOpList.hasNext() ) {

    			FeaturePredicate rhsOp = getRhsOp( (String)rhsOpList.next() );
    			int arity = rhsOp.getArity();

    			boolean isCommutative = rhsOp.isCommutative();

    			// If the operator requires more arguments than the
    			// elements in the varList, then skip the operator
    			if ( varList.size() >= arity ) {

    				Vector /* of Successor */ children = new Vector();
    				switch (arity) {
    				case 0:
    					children = getSuccessorSimple(rhs, rhsOp);
    					break;
    				case 1:
    					children = getSuccessorsStraight( rhs, rhsOp, varArray );
    					break;
    				default: //2 or more
    				children = getSuccessorsComb( rhs, rhsOp, varArray, arity, isCommutative); 
    				break;
    				}
    				for (int i = 0; i < children.size(); i++) {
    					successors.add( (Successor)children.get(i) );
    				}
    			}
    		}

    	} else {

    	}

    	// printSuccessors( successors );
    	return successors;
    }
    
    // Inner class to implement the Comparator interface. The compare() method sorts
    // the Vector of operators.
    class RhsOperatorComparator implements Comparator<Object> {

		@Override
		public int compare(Object arg0, Object arg1) {
			// TODO Auto-generated method stub
			int h1 = ((FeaturePredicate)arg0).freqCount;
			int h2 = ((FeaturePredicate)arg1).freqCount;
			
			if(h1 == h2)
				return 0;
			else if(h1 < h2)
				return -1;
			else 
				return +1;
		}
    }
    
    // Make a single successor for the operator rhsOp that does not take argument 
    // (i.e., a zero argument operator) 
    private Vector /* Successor */ getSuccessorSimple(RhsState rhs, FeaturePredicate rhsOp) {
        Vector /* Successor */ children = new Vector();
        RhsState child = makeRhsChildState( rhs, rhsOp );
        if (child != null) {
            String action = "(bind " + child.getExpVar(rhsOp) + " " + rhsOp.actionStr() + ")";
            children.add( new Successor( action, child ) );
        }
        return children;
    }
    
    private Vector /* Successor */
    getSuccessorsStraight( RhsState rhs, FeaturePredicate rhsOp, Object[] varArray ) {

    	Vector children = new Vector();
    	for (int i = 0; i < varArray.length; i++) {

    		String arg = (String)varArray[i];

    		// Avoid to apply VoidOp to non-given arguments
    		if ( !rhsOp.getClass().getName().equals(VOID_OP_CLASS) ||
    				rhs.isGivenVar( arg ) ) {

    			Vector argV = new Vector();
    			argV.add( arg );
    			rhsOp.setArgs( argV );
    			//if (!rhs.argTypeMatch(rhsOp)) continue;
    			//remove the inner type-matching, since this one will have this same result
    			//better: replace if (child!=null)
    			//with if (rhs.argTypeMatch(rhsOp)) and move 'RhsState child' inside the 'if'
    			RhsState child = makeRhsChildState( rhs, rhsOp );                
    			if (child != null) {
    				String action = "(bind " + child.getExpVar(rhsOp) + " " + rhsOp.actionStr(argV) + ")";                    
    				children.add( new Successor( action, child ) );
    			}
    		}
    	}
    	return children;
    }

    private Vector /* Successor */
    getSuccessorsComb( RhsState rhs, FeaturePredicate rhsOp, Object[] varArray, 
    		int arity, boolean isCommutative ) {

    	Vector children = new Vector();

    	// If the arity matches with the varList, then ...
    	Enumeration varPair = enumerate( varArray, arity, isCommutative );

    	while ( varPair.hasMoreElements() ) {

    		Object[] argArray = (Object[])varPair.nextElement();

    		Vector argV = arrayToVector( argArray );
    		rhsOp.setArgs( argV );

    		RhsState child = makeRhsChildState( rhs, rhsOp );

    		if (child != null) {
    			String action = "(bind " + child.getExpVar( rhsOp ) + " " + rhsOp.actionStr( argV ) + ")";
    			children.add( new Successor( action, child ) );
    		}
    	}
    	return children;
    }

    private Enumeration enumerate( Object[] varArray, int arity, boolean isCommutative ) {

	Enumeration varComb = null;

	if ( arity == 1 ) {
	    
	    varComb = new StringTokenizer( arrayToString( varArray ) );

	} else {

	    // calcumate all different combinations for variable
	    // assignments
	    try {
		if ( isCommutative ) {
		    varComb = new Combinations( varArray, arity );
		} else {
		    varComb = new Permutations( varArray, arity );
		}
	    } catch (CombinatoricException e) {
		e.printStackTrace();
	    }
	}
	return varComb;
    }

    // Since this method would vary from an algorithm to an algorithm
    // for a particular search schema, this can't be 'private' so that
    // above private methods can call an appropriate makeRhsChildState()
    RhsState makeRhsChildState( RhsState parent, FeaturePredicate rhsOp ) {

	RhsState child = (RhsState)parent.clone();

	// Push value-exp pair to the ExpList only when rhsOp is not
	// the void operator
	String opName = rhsOp.getClass().getName();
        
	if ( !opName.equals( VOID_OP_CLASS) ) {
	    // Wed Nov  1 00:39:50 LMT 2006 :: Noboru
	    // Check the type of argument so that inappropriate (meaningless) node
	    // won't get expanded
	    boolean opTypeMatch = child.pushExpList((FeaturePredicate)rhsOp.clone());
	    if (!opTypeMatch) {
		return null;
	    }
	}
	child.removeVarList( rhsOp.getArgs() );

	return child;
    }

    // Return an instance of FeaturePredicate object according to the
    // given name
    private FeaturePredicate getRhsOp( String rhsOpName ) {
	FeaturePredicate rhsOp = getRhsOpCache( rhsOpName );
	if ( rhsOp == null ) {
	    try {
		Class classDef = Class.forName( rhsOpName );
        rhsOp = (FeaturePredicate)classDef.newInstance();
        } catch (InstantiationException  e) {
		e.printStackTrace();
	    } catch (ClassNotFoundException e) {
		e.printStackTrace();
	    } catch (IllegalAccessException e) {
		e.printStackTrace();
	    }
	    putRhsOpCache( rhsOpName, rhsOp );
	}

	return rhsOp;
    }

    private String arrayToString( Object[] array ) {

	String str = "";
	for (int i = 0; i < array.length; i++) {
	    str += (String)array[i] + " ";
	}
	return str;
    }

    private Object[] vectorToArray( Vector v ) {

	Object[] array = new Object[ v.size() ];
	for (int i = 0; i < v.size(); i++) {
	    array[i] = v.get(i);
	}
	return array;
    }

    private Vector arrayToVector( Object[] array ) {

	Vector v = new Vector();
	for (int i = 0; i < array.length; i++) {
	    v.add( array[i] );
	}
	return v;
    }

    /**
     * Make a successor state with a monomial operator
     *
     * @param parent the current state
     * @param op the monomial opertor being applied
     * @param var1 the argument for the monomial operator
     * @return a new state
     */
    /*
    private RhsState makeRhsChildState( RhsState parent,
					String op, String var1 ) {

	RhsState child = (RhsState)parent.clone();
	String exp = "(" + op + " " + var1 + ")";
	String var = child.genVarSym();
	child.pushExpList( var, exp );
	child.pushVarList( var );
	child.removeVarList( var1 );

	return child;
    }

    private RhsState makeRhsChildState( RhsState parent,
					String op, String var1, String var2 ) {

	RhsState child = (RhsState)parent.clone();
	String exp = "(" + op + " " + var1 + " " + var2 + ")";
	String var = child.genVarSym();
	child.pushExpList( var, exp );
	child.pushVarList( var );
	child.removeVarList( var1 );
	child.removeVarList( var2 );

	return child;
    }
    */

}

//
// end of $RCSfile$
// 
