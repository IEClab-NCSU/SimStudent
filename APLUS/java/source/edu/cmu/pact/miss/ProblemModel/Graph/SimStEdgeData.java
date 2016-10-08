package edu.cmu.pact.miss.ProblemModel.Graph;

import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.Utilities.trace;

/**
 * Class to hold the selection, action, input and actionType
 * (Correct / Incorrect) for an edge connection the source 
 * and destination nodes. Modifying it to extend EdgeData
 * to be compatible with the current oracle.
 */
public class SimStEdgeData extends EdgeData{

	/** */
	private Object sel;
	
	/** */
	private Object act;
	
	/**	 */
	private Object inp;
	
	/** */
	private String actionType;
	
	public SimStEdgeData(){
		super();
	}

	public Vector getSelection() {
		Vector selVector = new Vector();
		selVector.add(sel);
		return selVector;
	}

	public void setSelection(Object sel) {
		this.sel = sel;
	}

	public Vector getAction() {
		Vector actVector = new Vector();
		actVector.add(act);
		return actVector;
	}

	public void setAction(Object act) {
		this.act = act;
	}

	public Vector getInput() {
		Vector inpVector = new Vector();
		inpVector.add(inp);
		return inpVector;
	}

	public void setInput(Object inp) {
		this.inp = inp;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
	public void setMatcher(Matcher m) {
        //if (trace.getDebugCode("functions")) trace.outln("functions", "setMatcher(" + m.getClass().getName() + ")");
        matcher = m;
    }

}
