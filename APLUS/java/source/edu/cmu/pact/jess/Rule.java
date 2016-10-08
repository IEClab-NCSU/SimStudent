package edu.cmu.pact.jess;


public class Rule {
	public Rule(String r) {
		rule = r;
		examples = 0;
		numpassed = 0;
		startIndex = 0;
		lastIndex = -1;
		patIndex = -1;
	}
	public String rule;
	public int examples;
	public int numpassed;
	int lastIndex, startIndex, patIndex;
}