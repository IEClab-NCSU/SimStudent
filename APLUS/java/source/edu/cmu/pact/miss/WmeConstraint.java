/**
 * Defines a bunch of topological constraints among WMEs 
 *Represents a p particular constrain on a particular pair of arguments
 *
 * Created: Tue Mar 22 11:08:00 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss;

import edu.cmu.pact.Utilities.trace;
import jess.Fact;

public class WmeConstraint {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - -
    // -


    // The Rete net
    private AmlRete rete;
    private AmlRete getRete() { return this.rete; }
    private void setRete( AmlRete rete ) { this.rete = rete; }

    /**
     * the predicate that this constraint makes use of
     */
    private WMEConstraintPredicate predicate;
    // The arguments
    private WmePathNode[] args;
    private WmePathNode[] getArgs() { return this.args; }
    private void setArgs( WmePathNode[] args ) { this.args = args; }
    /**
     * Returns the n-th argument in the args[] list
     *
     * @param n an <code>int</code> value
     * @return a <code>WmePathNode</code> value
     */
    WmePathNode getNthArg( int n ) {
	return this.args[n];
    }
    /**
     * Returns a number of arguments
     *
     * @return an <code>int</code> value representing the number be
     * arguments 
     **/
    int getArity() { 
    	return args.length; };

    // -
    // - Constructor - - - - - - - - - - - - - - - - - - - - - - -
    // - 

    /**
     * @param rete
     * @param predicate the WMEConstraintPredicate this constriant applies
     * @param args 
     */
    public WmeConstraint( AmlRete rete,
			  WMEConstraintPredicate predicate,
			  WmePathNode[] /* WmePathNode */ args ) {
    	

	setRete( rete );
	this.predicate=predicate;
	setArgs( args );
    }

    // -
    // - Methods - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    /**
     * Describe <code>apply</code> method here.
     *
     * @param args a <code>Fact[]</code> value
     * @return a <code>boolean</code> value
     */
    boolean apply( Fact[] args) {

	boolean test = false;
	String testStr = applyLookupCache(  args );
	// String testStr = null;

	if ( testStr == null ) {		
	    test = applyNewArgs( args); 
	    saveApplyCache( args,  test );
	} else {
	    test = testStr.equals("T");
	}
	return test;
    }

    private HashMap applyCache = new edu.cmu.pact.miss.HashMap();
    String applyLookupCache( Fact[] args ) {
	
	return (String)applyCache.get(  args  );

    }
    void saveApplyCache( Fact[] args, boolean result ) {

	applyCache.put( args , result ? "T" : "F" );
    }

    boolean applyNewArgs( Fact[] args) {

	boolean test = false;
	if(predicate.apply(args,rete)!=null)
		test=true;
	else
		test=false;
		
	
	return test;
    }
    // -
    // - Override methods
    // -

    public String toString() {

	String str = "("+predicate.getName()+" "; 
	
	for (int i = 0; i < getArity(); i++) {
	    str += getNthArg(i).getVariable();
	    if ( i < getArity() -1 ) {
		str += " ";
	    }
	}
	str += ")";
	return str;
    }
    
}

