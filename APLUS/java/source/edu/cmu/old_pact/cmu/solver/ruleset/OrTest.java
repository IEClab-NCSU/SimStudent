package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.query.Queryable;

//OrTest does an Or (or an XOR)
public class OrTest extends Test {
	Test[] internalTests;
	boolean xor=false;
	
	public OrTest(Test[] internals) {
		internalTests = new Test[internals.length];
		for (int i=0;i<internals.length;++i)
			internalTests[i] = internals[i];
	}
	
	public OrTest(Test[] internals, boolean useXor) {
		this(internals);
		xor=useXor;
	}

	public boolean passes(Queryable info) {
		boolean foundOne=false;
		boolean allPass = true;
		if (xor) { //xor needs to do all the subtests
			for (int i=0;i<internalTests.length;++i) {
				boolean internalPasses = internalTests[i].passes(info,true);
				if (internalPasses)
					foundOne = true;
				else
					allPass = false;
			}
			return (foundOne && !allPass);
		}
		else { //don't do all the sub-tests if regular or
			for (int i=0;i<internalTests.length && !foundOne;++i) {
				boolean internalPasses = internalTests[i].passes(info,true);
				if (internalPasses)
					foundOne = true;
			}
			return foundOne;
		}
	}
	
	public String toString() {
		String theString="";
		for (int i=0;i<internalTests.length;++i) {
			theString += internalTests[i].toString();
			if (i < internalTests.length-1)
				theString += ",";
		}
		return "[OR: "+theString+"]";
	}
}
