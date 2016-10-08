package edu.cmu.old_pact.cmu.solver.ruleset;


//a named testSet is just a list of Tests which is given a name

public class NamedTestSet {
	public Test[] tests;
	public String myName;
	
	public NamedTestSet (String inName,Test[] inTests) {
		tests = new Test[inTests.length];
		for (int i=0;i<inTests.length;++i)
			tests[i] = inTests[i];
		myName = inName;
	}
}