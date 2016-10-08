package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.query.Queryable;

//AndTest does an And.  This usually isn't necessary since multiple
//tests at the top level of a rule are treated as an implicit "and".
//But in order to do something like or(and(a,b),and(c,d)) you need an
//AndTest.
public class AndTest extends Test {
	Test[] internalTests;
	
	public AndTest(Test[] internals) {
		internalTests = new Test[internals.length];
		for (int i=0;i<internals.length;++i)
			internalTests[i] = internals[i];
	}
	
	public boolean passes(Queryable info) {
		boolean allPass = true;
		for (int i=0;i<internalTests.length;++i) {
			boolean internalPasses = internalTests[i].passes(info,true);
			if (!internalPasses){
				allPass = false;
			}
		}

		return allPass;
	}
	
	public String toString() {
		String theString="";
		for (int i=0;i<internalTests.length;++i) {
			theString += internalTests[i].toString();
			if (i < internalTests.length-1)
				theString += ",";
		}
		return "[AND: "+theString+"]";
	}
}
