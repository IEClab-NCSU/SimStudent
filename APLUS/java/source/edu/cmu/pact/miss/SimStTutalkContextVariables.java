package edu.cmu.pact.miss;

import java.util.Vector;

// The struct for a context variable
// We can use that to replace variables in context in the actual question

public class SimStTutalkContextVariables {

    public SimStTutalkContextVariables() {
    
    }

    public void addVariable(String pName, String pValue) {
	allContextVariables.add(new contextVariable(pName, pValue));
    }

    public int size() {
	return allContextVariables.size();
    }
    
    /* Resets all replacement variables */
    public void clear() {
	allContextVariables.clear();
    }

    public String getNthVarName(int nth) {
	return (allContextVariables.get(nth)).getName();
    }

    public String getNthVarValue(int nth) {
	return (allContextVariables.get(nth)).getValue();
    }

    private class contextVariable {
	private String varName;
	private String varValue;

	/** Constructor for a replacement variable
	    * @param pVarName The variable name to be replaced
	    * @param pVarValue The actual variable value to be replaced
	    */
	public contextVariable(String pVarName, String pVarValue) {
	    varName = pVarName;
	    varValue = pVarValue;
	}

	public String getName() {
	    return varName;
	}

	public String getValue() {
	    return varValue;
	}
    }

    private Vector<contextVariable> allContextVariables = new Vector();
}