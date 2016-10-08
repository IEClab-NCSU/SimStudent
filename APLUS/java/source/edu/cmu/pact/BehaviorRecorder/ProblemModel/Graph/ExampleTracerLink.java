package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;
import java.util.Comparator;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.View.ActionLabel;
import edu.cmu.pact.Utilities.trace;

public class ExampleTracerLink {
	
    public static class LinkDepthComparator implements Comparator<ExampleTracerLink> {
	public int compare(ExampleTracerLink arg0, ExampleTracerLink arg1) {
	    return arg0.getDepth()-arg1.getDepth();
	}		
    }
    
    private int uniqueID;
    private EdgeData edge;
    private int depth;
    private int prevNode;
    private int nextNode;
    
    /*
     * Constructor
     */
    public ExampleTracerLink (int uniqueID, int prevNode, int nextNode) {
	this.uniqueID = uniqueID;
	this.prevNode = prevNode;
	this.nextNode = nextNode;
    }
    
    /*
     * Constructor 
     */
    ExampleTracerLink (EdgeData edge) {
	this.edge = edge;
	this.uniqueID = edge.getUniqueID();
    }
    
    public ExampleTracerLink (EdgeData edge, int prevNode, int nextNode) {
	this.edge = edge;
	this.prevNode = prevNode;
	this.nextNode = nextNode;
	this.uniqueID = edge.getUniqueID();
    }
    
    /**
     * For debugging.
     * @return {@link ActionLabel} if available, else {@link #uniqueID}
     */
    public String toString() {
	StringBuffer sb = new StringBuffer("[link ");
	sb.append(getUniqueID());
	sb.append(" (").append(getPrevNode()).append('-').append(getNextNode());
	sb.append(") ").append(getType());
	if (edge != null && edge.getActionLabel() != null)
	    sb.append(" ").append(edge.getActionLabel().getText());
	sb.append("]");
	return sb.toString();
    }
    
    /**
     * @return - returns uniqueID of the link 
     */
    public int getUniqueID() {
	return uniqueID;
    }
    
    /**
     * Sets the uniqueID of the link with the given id
     * @param id
     */
    public void setUniqueID(int id) {
	this.uniqueID = id;
    }
    
    /**
     * @return - returns the id of previous node
     */
    public int getPrevNode() {
	return this.prevNode;
    }
    
    /**
     * Sets the prevNode with the given id
     * @param prevNode
     */
    void setPrevNode(int prevNode) {
	this.prevNode = prevNode;
    }
    
    /**
     * @return - returns the id of next node
     */
    public int getNextNode() {
	return this.nextNode;
    }
    
    /**
     * sets the nextNode with the given id
     * @param toNode
     */
    void setNextNode(int nextNode) {
	this.nextNode = nextNode;
    }
    
    public int getDepth() {
	return depth;
    }

    public void setDepth(int depth) {
	this.depth = depth;
    }
    
    /**
     * @return - returns the type of the link
     */
    String getType() {
	return edge.getActionType();
    }
    
    /**
     * @return - returns the matcher of the link
     */
    Matcher getMatcher() {
	boolean caseInsensitive = edge.getProblemModel().isCaseInsensitive();
	Matcher m = edge.getMatcher();
	m.setCaseInsensitive(caseInsensitive);
	return m;
    }
    
    /**
     * Checks whether the link's sai matches given sai
     * @param sai input to matcher
     * @param result to store results of match
     * @param vt interp specific vt, if null refers to problemmodel(best interp)'s vt 
     * @return - true or false depending whether sai matches or not
     */
    boolean matchesSAI (ExampleTracerSAI sai, VariableTable vt) {
    	Matcher m = getMatcher();
    	getEdge().setInterpolateSAI(sai.getSelectionAsString(), sai.getActionAsString(), sai.getInputAsString());
    	boolean mResult = m.match(sai.getSelectionAsVector(),
    			sai.getActionAsVector(),
    			sai.getInputAsVector(),
    			sai.getActor(), vt);
    	if (trace.getDebugCode("et"))
    		trace.outNT("et", "ExampleTracerLink.matchesSAI("+sai+") matcher "+m+" returns "+mResult);
//    	if (mResult)
//    		result.setTutorSAI(new ExampleTracerSAI(m.getSelection(), m.getAction(),
//    				m.getEvaluatedInput(), m.getActor()));
    	return mResult;
    }
    
    /**
     * Tries to match just the selection and actor elements of the SAI. 
     * Even if matches ok, rejects if hint texts are desired but the link has no hints.
     * To make this check fast, we don't evaluate hints, only check whether any hint
     * is defined.
     * @param sai
     * @param result to check {@link ExampleTracerEvent#getWantReportableHints()}
     * @return true if match and has hints if wanted
     */
    boolean matchesSAIforHint (ExampleTracerSAI sai, ExampleTracerEvent result) {
    	if (!getMatcher().matchForHint(sai.getSelectionAsVector(),sai.getActionAsVector(),sai.getActor()))
    		return false;
    	if (result != null && result.getWantReportableHints()) {
    		EdgeData edgeData = getEdge();
    		int nStaticHints = (edgeData == null ? 0 : edgeData.getAllHints().size());
    		if (nStaticHints < 1)
    			return false;  // caller wants hints, but this link doesn't have any
    	}
    	return true;
    }
    
    /**
     * Checks whether the link's type matches given type
     * @param type
     * @return - true or false depending whether type matches or not
     */
    boolean matchesType (String type) {
	return this.getType().equals(type);
    }	
    
    public EdgeData getEdge() {
	return edge;
    }
    
    /**
     * Return the integer identifier.
     * @return {@link #getEdge()}.{@link ProblemEdge#getUniqueID()}
     */
    public int getID() {
	return getEdge().getEdge().getUniqueID();
    }

}
