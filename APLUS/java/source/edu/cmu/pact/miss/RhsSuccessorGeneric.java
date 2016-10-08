/**
 * source/edu/cmu/pact/miss/RhsSuccessorGeneric.java
 *
 *	Generate a list of successor states that allows repetitive
 *	use of operators and arguments
 *
 * Created: Sun Oct 02 22:46:53 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 **/

package edu.cmu.pact.miss;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class RhsSuccessorGeneric extends RhsSearchSuccessorFn {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - - 
    // -

    // -
    // - Constructors - - - - - - - - - - - - - - - - - - - - - - - 
    // -

    /**
     * Creates a new <code>RhsSuccessorGeneric</code> instance.
     *
     * @param rhsOpFile a name file of the from which a list of RhsOp
     * classes for RHS composition must read
     **/
    public RhsSuccessorGeneric( Vector rhsOp, HashMap rhsOpCache ) {

	// Store hash for FeaturePredicate 
	setRhsOpCache( rhsOpCache );
	// initialize rhsOpList
	setRhsOpList( rhsOp );
    }

    /**
     * Fri Nov 11 00:07:25 2005
     *	No op cache version
     *
     * @param rhsOp a <code>Vector</code> value
     **/
    public RhsSuccessorGeneric( Vector rhsOp ) {

	setRhsOpList( rhsOp );
    }

    public RhsSuccessorGeneric(Vector rhsOp, HashMap rhsOpCache, boolean heuristicBasedIDS) {
    	
    	if(trace.getDebugCode("miss"))trace.out("miss", "RhsSuccessorGeneric with heuristicBasedIDS");
    	setRhsOpCache(rhsOpCache);
    	setRhsOpList(rhsOp);
    	setHeuristicBasedIDS(heuristicBasedIDS);
    }
    
    // -
    // - Methods - - - - - - - - - - - - - - - - - - - - - - - - - 
    // -

    // Implementation of aima.search.framework.SuccessorFunction

    
    
    
    /*
     * This class uses the same method as the one defined in
     * RhsSearchSuccessorFn, except makeRhsChildState() where the
     * variables in the varList in RhsState won't be reduced
     *
     **/
    // public final List getSuccessors(final Object object) {}

    // Since this method would vary from an algorithm to an algorithm
    // for a particular search schema, this can't be 'private' so that
    // above private methods can call an appropriate makeRhsChildState()
    RhsState makeRhsChildState( RhsState parent, FeaturePredicate rhsOp ) {

	//the child node is initialized as a clone of the parent node.
	//Later in this function, it will have one operator added to it (see "child.pushExpList(...)")
	RhsState child = (RhsState)parent.clone();
	
	// Push value-exp pair to the ExpList only when rhsOp is not 
	// the void operator
	String opName = rhsOp.getClass().getName();
	if ( !opName.equals( VOID_OP_CLASS) ) {

            boolean opTypeMatch = child.pushExpList((FeaturePredicate)rhsOp.clone());
            if (!opTypeMatch) {
                return null;
            }
        }

	// Sun Oct 02 23:02:56 2005:: This must be turned off for this
	// successor
	// 
	// child.removeVarList( rhsOp.getArgs() );

	return child;
    }

}

//
// end of source/edu/cmu/pact/miss/RhsSuccessorGeneric.java
// 
