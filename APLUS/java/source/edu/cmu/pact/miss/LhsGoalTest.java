/**
 * LhsGoalTest.java
 *
 *
 * Created: Wed Jan 12 13:41:06 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss;

import aima.search.framework.GoalTest;

public class LhsGoalTest implements GoalTest {

    /**
     * Creates a new <code>LhsGoalTest</code> instance.
     *
     */
    public LhsGoalTest() {
    }

    // Implementation of aima.search.framework.GoalTest

    /**
     * Since a successor function only generates states that are
     * consistent with examples, if a state is generated at the depth
     * that is equal to (# of seeds in examples) + 1, then that state
     * must satisify a goal condition (i.e., all the wme paths in LHS
     * are consistent with the seeds in examples.  Now, this test is
     * done by invoking isGoalState() method defined in LhsState. 
     *
     * @param object an <code>Object</code> value
     * @return a <code>boolean</code> value
     **/
    public final boolean isGoalState(final Object object) {

	LhsState lhs = (LhsState)object;
	return lhs.isGoalState();
    }
  
}

//
// end of LhsGoalTest.java
// 
