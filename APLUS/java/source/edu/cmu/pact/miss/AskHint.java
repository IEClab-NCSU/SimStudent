package edu.cmu.pact.miss;

import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

public abstract class AskHint {

    public static final String HINT_METHOD_HD = "humanDemonstration";
    public static final String HINT_METHOD_FTS = "fakeTutoringService";
    public static final String HINT_METHOD_BRD = "BRD";
    public static final String HINT_METHOD_CL = "clAlgebraTutor";
    public static final String HINT_METHOD_FAKE_CLT = "fakeClAlgebraTutor";
	public static final String HINT_METHOD_SOLVER_TUTOR = "builtInClSolverTutor";
	public static final String HINT_METHOD_JESS_ORACLE = "JessOracle";
	public static final String HINT_METHOD_WEBAUTHORING = "WebAuthoring";
	
	
	
	
    private String selection = null;
	public String getSelection() { return selection; }
	public void setSelection(String selection) { this.selection = selection; }

	private String action = "UpdateTable";
	public String getAction() { return action; }
	public void setAction(String action) { this.action = action; }

	private String input = null;
	private String ruleName = null;
	public String getInput() { return input; }
	public void setInput(String input) { this.input = input; }
	public void setRuleName(String ruleName) { this.ruleName = ruleName;}
	public String getRuleName() { return ruleName; }
	

	private String[] hintMsg = null;
    public String[] getHintMsg() { return hintMsg; }
	public void setHintMsg(String[] hintMsg) { this.hintMsg = hintMsg; }

	private Sai sai = null;
    public Sai getSai() { return sai; }
    public void setSai(Sai sai) { this.sai = sai; }

    public String skillName = null;
    // A successive node representing a state after performing a step indicated as the hint
    public ProblemNode node = null;
    public ProblemEdge edge = null;

    // 17 May 2007
    // This field gets used when ssFoaGetterClass is not specified.
    // In these cases, the relevant subclass of AskHint must set this field, before it gets used.
    // modeled after the identical field inside RuleActivationNode.
    public Vector foas = null;
    
    public ProblemEdge getEdge() {
        return edge;
    }
    public void setEdge(ProblemEdge edge) {
        this.edge = edge;
    }
    public ProblemNode getNode() {
        return node;
    }
    public void setNode(ProblemNode node) {
        this.node = node;
    }
    public String getSkillName() {
        return skillName;
    }
    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

	protected void updateNodeInBR(BR_Controller brController,
			ProblemNode currentNode) {
		SimSt simSt = brController.getMissController().getSimSt();
        SimStNodeEdge newNodeEdge = simSt.makeNewNodeAndEdge(getSai(), currentNode);
        setNode(newNodeEdge.node);
        setEdge(newNodeEdge.edge);
	}
    
    /* get the hint and sets the fields
     */
    public abstract void getHint(BR_Controller brController, ProblemNode currentNode);
    
    public void getRetry(BR_Controller brController, ProblemNode currentNode)
    {
    	getHint(brController, currentNode);
    }
    
    public String[] getRetryMessage()
    {
    	String[] message = new String[1];
    	message[0] = "retry";
    	return message;
    }
    
    public String toString() {
        return "<AskHint for " + skillName + " on " + node + "(" + edge + ")" + " with " + sai + ">";
    }
}
