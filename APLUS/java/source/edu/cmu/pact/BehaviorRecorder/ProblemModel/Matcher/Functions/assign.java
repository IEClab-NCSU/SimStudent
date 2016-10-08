package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.io.Serializable;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;
import edu.cmu.pact.Utilities.trace;

/**
 * Mechanism for saving values in the current {@link VariableTable} object,
 * specific to the interpretation in whose context this function is called.
 * See {@link CTATFunctions#FUNCALL(Object, Object, Object, Object)}.
 */
public class assign implements UsesVariableTable, Serializable {
	
	/** The variable table to assign to. */
	private VariableTable vt;

    /**
     * <p>Remove a variable (if any exists) by the given name.</p>
	 * @param name
	 * @return boolean true, so this can be used in AND expressions
     */
    public boolean assign(String name) {
    	Object oldValue = vt.remove(name);
    	if (trace.getDebugCode("functions"))
    		trace.out("functions", "assign() removes \""+name+"\"; old value \""+oldValue+"\"");
    	return true;
    }

    /**
     * <p>Create a variable (if none exists) by the given name and assign this value.</p>
	 * @param name
	 * @param value
	 * @return boolean true, so this can be used in AND expressions
     */
    public boolean assign(String name, Object value) {
    	Object oldValue = vt.put(name, value);
    	if (trace.getDebugCode("functions"))
    		trace.out("functions", "assign() vt#"+vt.getInstance()+" \""+name+"\" = \""+value+"\"; old value \""+oldValue+"\"");
    	return true;
    }

    /**
     * Set the internal reference to the variable table.
     * @param vt
     * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.UsesVariableTable#setVariableTable(edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable)
     */
    public void setVariableTable(VariableTable vt) {
    	this.vt = vt;
    }
}

