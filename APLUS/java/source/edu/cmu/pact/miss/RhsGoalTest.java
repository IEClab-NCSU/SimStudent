/**
 * $RCSfile$
 *
 * Describe class RhsGoalTest here.
 *
 *
 * Created: Fri Dec 31 22:41:16 2004
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version $Id: RhsGoalTest.java 21309 2014-10-02 14:02:01Z nikolaos $
 */

package edu.cmu.pact.miss;

import java.util.Iterator;
import java.util.Vector;

import aima.search.framework.GoalTest;

public class RhsGoalTest implements GoalTest {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    // ----- Debug -----
    static private String goalTest = null;
    public static String getGoalTest() { return goalTest; }
    public static void setGoalTest( String test ) { goalTest = test; }

    static private String rhsState = null;
    public static String getRhsState() { return rhsState; }
    public void setRhsState( RhsState rs ) { rhsState = rs.toString(); }
    // ----- Debug -----

    // A list of Instructions being examined
    private Vector /* of Instructions */ instructions = new Vector();
    Vector getInstructions() { return this.instructions; }
    void setInstructions( Vector instructions ) {
	this.instructions = instructions;
    }
    Iterator getInstructionsIterator() { return this.instructions.iterator(); }

    // -
    // - Constructor - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    public RhsGoalTest(){}
    
    /**
     * Creates a new <code>RhsGoalTest</code> instance.
     *
     */
    public RhsGoalTest( Vector /* of Instruction */ instructions ) {
	setInstructions( instructions );
    }

    // -
    // - Methods  - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    // Implementation of aima.search.framework.GoalTest

    /**
     * Describe <code>isGoalState</code> method here.
     *
     * @param object an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isGoalState(final Object object) {

    	boolean isConsistent = true;

    	// For the specified state, which is a RhsState, ...
    	RhsState rhs = (RhsState)object;

    	// ----- debug -----
    	// setRhsState( rhs );
    	// ----- debug -----

    	
    	// Test if all instructions are consistent with the sequence
    	// of operators in the target state
    	Iterator instructions = getInstructionsIterator();
    	while ( instructions.hasNext() ) {

    		Instruction instruction = (Instruction)instructions.next();

  	   	
    		// If there exists an instruction that disagrees with the
    		// state, then break the loop and returns false
    		if ( !rhs.hasValidOperations( instruction ) ) {
    			isConsistent = false;
    			break;
    		}
    	}
    	return isConsistent;
    }

}

//
// end of $RCSfile$
// 
