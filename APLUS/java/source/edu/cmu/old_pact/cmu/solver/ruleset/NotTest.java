package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.query.Queryable;

//NotTest does a negation
public class NotTest extends Test {
	Test internalTest;
	
	public NotTest(Test internal) {
		internalTest = internal;
	}
	
	public boolean passes(Queryable info) {
		boolean internalPasses = internalTest.passes(info,true);
		return !internalPasses;
	}
	
	public String toString() {
		return "[NOT: "+internalTest.toString()+"]";
	}
}
