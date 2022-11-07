package edu.cmu.pact.miss.jess;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

import javax.swing.JOptionPane;

import jess.Context;
import jess.Fact;
import jess.JessException;
import jess.RU;
import jess.Strategy;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.RuleActivationNode;
import edu.cmu.pact.jess.RuleActivationTree;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.SimStCognitiveTutor;
import edu.cmu.pact.miss.MetaTutor.MetaTutorAvatarComponent;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;


/**
 * Class to do the model tracing. For every action on the interface, receives the event/action
 * and runs the model tracer.
 */
public class ModelTracer {

	private static final long serialVersionUID = 1L;

	/** File containing the rules and the deftemplates */
	public static final String JESS_RULES_FILE= "mtbehavior.clp";

	/** File containing the deftemplates for the meta-tutor	 */
	public static final String WME_TYPES_MT_FILE = "wmeTypesMT.clp";
	
	/** File containing the production rules for the meta-tutor	 */
	public static final String PRODUCTION_RULES_MT_FILE = "productionRulesMT.pr";
	
	/** File containing the production rules for the meta-tutor	(Obsolete, this was a test - not used) */
	public static final String PRODUCTION_RULES_MT_COG_FILE = "productionRulesMTCognitive.pr";
	
	/** File containing the production rules for the meta-tutor	(Obsolete, this was a test - not used) */
	public static final String PRODUCTION_RULES_MT_METACOG_FILE = "productionRulesMTMetaCognitive.pr";
	
	/** File containing the production rules for the meta-tutor	for Aplus Control */
	public static final String PRODUCTION_RULES_MT_APLUS_CTRL_FILE = "productionRulesMTAplusControl.pr";

	
	
	/**	File to initialize the working memory by asserting the facts */
	public static final String INIT_WM_MT_FILE = "initMT.wme";
	
	/**	Constant to denote the outcome of the model-tracing */
	public static final int NOT_APPLICABLE = -1;
	
	
	static final int CORRECT = 1;
	static final int NOMODEL = 2;
	static final int BUGGY = 3;
	static final int FIREABLE = 4;
	static final int HINT = 5;
	
	/**	Queue to keep the SAI events that arrive */
	//private ProdSysSAIHandler.Queue prodSysSAIHandlerQ = new ProdSysSAIHandler.Queue();

	/**	Reference to the {@link BR_Controller} */
	private CTAT_Controller controller;
	
	public CTAT_Controller getController() {
		return controller;
	}

	public void setController(CTAT_Controller controller) {
		this.controller = controller;
	}
	
	private SimStLogger logger;
	
	public void setLogger(SimStLogger log)
	{
		logger = log;
	}
	
	public SimStLogger getLogger()
	{
		return logger;
	}

	/**	 */
	private transient jess.Fact studentValuesFact = null;
	
	/**	Boolean to denote if the SAI needs to be model-traced. */
	private boolean modelTraceMode = false;
	
	public boolean isModelTraceMode() {
		return modelTraceMode;
	}

	public void setModelTraceMode(boolean modelTraceMode) {
		this.modelTraceMode = modelTraceMode;
	}

	/** Boolean to denote that this is a hint request */
	private boolean isHint = false;
	
	public boolean isHintTrace(){
		return isHint;
	}
	
	/**	Boolean to denote that this is a CL hint request */
	private boolean isCLHint = false;
	
	public boolean isCLHintTrace(){
		return isCLHint;
	}
	
	/**	Root node of the tree */
	private RuleActivationNode rootActivation;
	
	public RuleActivationNode getRootActivation() {
		return rootActivation;
	}

	public void setRootActivation(RuleActivationNode rootActivation) {
		this.rootActivation = rootActivation;
	}

	/**	Tree to expand the activated nodes */
	private RuleActivationTree tree;
	
	public RuleActivationTree getTree() {
		return tree;
	}

	public void setTree(RuleActivationTree tree) {
		this.tree = tree;
	}

	/**	Current firing node in the tree */
	private RuleActivationNode nodeNowFiring;
	public RuleActivationNode getNodeNowFiring() {
		return nodeNowFiring;
	}
	public void setNodeNowFiring(RuleActivationNode nodeNowFiring) {
		this.nodeNowFiring = nodeNowFiring;
	}

	/**	Maximum Depth for the iterative deepening */
	private int maxDepth;
	public int getMaxDepth() {
		return maxDepth;
	}
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	/**	 */
	private ArrayList nodeSeq = new ArrayList();
	
	/** Rete engine	 */
	private SimStRete ssRete;
	public SimStRete getSsRete() {
		return ssRete;
	}
	public void setSsRete(SimStRete ssRete) {
		this.ssRete = ssRete;
	}
	
	
	/**	 */
	private String hintSelection;
	
	/**	Student's selection for the SAI component */
	private String studentSelection;
	public String getStudentSelection() {
		return studentSelection;
	}
	public void setStudentSelection(String studentSelection) {
		this.studentSelection = studentSelection;
	}

	/** MenuOption messages constructed by this activation. */
	private ArrayList<ArrayList<String>> menuOptionMessages = new ArrayList<ArrayList<String>>();
	public ArrayList<ArrayList<String>> getMenuOptionMessages() {
		return menuOptionMessages;
	}
	public void setMenuOptionMessages(
			ArrayList<ArrayList<String>> menuOptionMessages) {
		this.menuOptionMessages = menuOptionMessages;
	}

	/**	Student's action for the SAI component */
	private String studentAction;
	public String getStudentAction() {
		return studentAction;
	}
	public void setStudentAction(String studentAction) {
		this.studentAction = studentAction;
	}

	/**	Student's Input for the SAI component */
	private String studentInput;
	public String getStudentInput() {
		return studentInput;
	}
	public void setStudentInput(String studentInput) {
		this.studentInput = studentInput;
	}
	
	private RuleActivationNode matchedNode;
	public RuleActivationNode getMatchedNode() {
		return matchedNode;
	}
	public void setMatchedNode(RuleActivationNode node){
		matchedNode = node;
		/*if(matchedNode != null)
		trace.out(" This node taken : "+matchedNode.getDisplayName());*/
	}
	
	
	private RuleActivationNode hintNode;
	public RuleActivationNode getHintNode() {
		return hintNode;
	}
	public void setHintNode(RuleActivationNode node){
		hintNode = node;
	}
	
	
	RuleActivationNode noMatchedMetaCogNode=null;
	private void setNotMatchedMetaCogNode(RuleActivationNode ran){ this.noMatchedMetaCogNode=ran; }
	public RuleActivationNode getNotMatchedMetaCogNode(){return this.noMatchedMetaCogNode;}
	
	private Queue<Boolean> traceHistory;
	private int traceHistorySize = 10;
	private int incorrectCount = 0;
	

	public void addTraceHistory(boolean trace)
	{
		traceHistory.add(trace);
		if(!trace) incorrectCount++;
		if(traceHistory.size() > traceHistorySize)
		{
			boolean remove = traceHistory.remove();
			if(!remove) incorrectCount--;
		}
	}
	
	public void setTraceHistorySize(int n)
	{
		traceHistorySize = n;
	}
	
	public int getTraceHistorySize()
	{
		return traceHistorySize;
	}
	
	public int getTraceHistoryIncorrectCount()
	{
		return incorrectCount;
	}

	/*2.2.2015: this was commented because it wasn't used. 
	HashMap<String, Integer> correctFirings; 
	private void addCorrectFiring(String rulename){
		if (!correctFirings.containsKey(rulename))
			correctFirings.put(rulename, new Integer(1));
		else{
			int val=correctFirings.get(rulename);
			val=val+1;
			correctFirings.put(rulename,val);
		}
	}
	public HashMap<String, Integer> getCorrectFirings(){return this.correctFirings;}
	*/
	private String targetWindow = "";
	/**
	 * @param rete
	 */
	public ModelTracer(SimStRete rete){
		ssRete = rete;
		traceHistory = new LinkedList<Boolean>();
	}
	
	/**
	 * 
	 * @param rete
	 * @param controller
	 */
	public ModelTracer(SimStRete rete, final CTAT_Controller controller){
		this(rete);
		this.controller = controller;
		if(controller == null)
			return;
		logger = controller.getMissController().getSimStPLE().getSsInteractiveLearning().getLogger();
		tree = new RuleActivationTree((CTAT_Controller) controller);
		setTree(tree);
		traceHistory = new LinkedList<Boolean>();
		
	}
	
	/**
	 * 
	 * @param isHint
	 * @param isCLHint
	 * @param selection
	 * @param action
	 * @param input
	 * @param messages
	 * @return
	 */
	public int runModelTrace(final boolean isHint, final boolean isCLHint, final String selection, final String action, final String input,
			final Vector messages) {
		
		setModelTraceMode(true);
		int rtnVal = Integer.MIN_VALUE;
		long startTime = (new Date()).getTime();
		synchronized(this) {
			rtnVal = modelTrace(isHint, isCLHint, selection,action,input,messages);
		}
		long endTime = (new Date()).getTime();
		long duration = endTime-startTime;
		setModelTraceMode(false);
		return rtnVal;
	}
	
	/**
	 * 
	 * @param isHint
	 * @param selection
	 * @param action
	 * @param input
	 * @param messages
	 * @return
	 */
	public int modelTrace(boolean isHint, boolean isCLHint, String selection, String action, String input,
			Vector messages){
		
		this.isHint = isHint;
		this.isCLHint = isCLHint;
		studentSelection = selection;
		studentAction = action;
		studentInput = input;
		nodeSeq.clear();
		
	
		
		try {
			
			String productionRulesFileUsed=PRODUCTION_RULES_MT_FILE;
					
			if (controller != null && controller.getMissController() != null && controller.getMissController().getSimSt() != null && controller.getMissController().getSimSt().getSsMetaTutorModeLevel().equals(SimSt.COGNITIVE))
				productionRulesFileUsed=PRODUCTION_RULES_MT_COG_FILE;
			else if (controller != null && controller.getMissController() != null && controller.getMissController().getSimSt() != null && controller.getMissController().getSimSt().getSsMetaTutorModeLevel().equals(SimSt.METACOGNITIVE))
				productionRulesFileUsed=PRODUCTION_RULES_MT_METACOG_FILE;
		
			
			
			if (controller != null && controller.getMissController() != null && controller.getMissController().getSimSt() != null && controller.getMissController().getSimSt().isSsCogTutorMode()){
				productionRulesFileUsed=PRODUCTION_RULES_MT_APLUS_CTRL_FILE;
			}
						
			//ssRete.clear(); // Added
			ssRete.setUp(new String[] {
					ModelTracer.WME_TYPES_MT_FILE,
					ModelTracer.INIT_WM_MT_FILE,
					productionRulesFileUsed/*ModelTracer.PRODUCTION_RULES_MT_FILE*/
					});
			//ssRete.add(clonedWorkingMemory);
			ssRete.add(this.getController().getMissController().getSimSt().getModelTraceWM());
			
			
		
			if(controller != null && controller.getMissController() != null && 
					controller.getMissController().getSimSt() != null && controller.getMissController().getSimSt().getSsInteractiveLearning()
					!= null && controller.getMissController().getSimSt().getSsInteractiveLearning().isTakingQuiz()) {
				
			} else {
				
				if(controller != null && controller.getMissController() != null && controller.getMissController().
						getSimSt() != null && controller.getMissController().getSimSt().getSsInteractiveLearning()
						!= null) {
					
					SimSt simStudent = controller.getMissController().getSimSt();
					ProblemNode currentNode = simStudent.getBrController().getSolutionState().getCurrentNode();
					if(currentNode != null && currentNode.getParents() != null && 
							currentNode.getParents().isEmpty()) {
						
						ssRete.init(currentNode.getName(), false);
					} else if(currentNode != null && currentNode.getParents() != null &&
							!currentNode.getParents().isEmpty()) {
						
						ProblemNode startNode = simStudent.getBrController().getProblemModel().getStartNode();
						ProblemNode endNode = currentNode;
						//boolean pathFound = simStudent.getBrController().findPreferredPath(startNode, endNode, pathEdges);
						Vector pathEdges = MetaTutorAvatarComponent.findPathDepthFirst(startNode, endNode);
						ssRete.goToWMState(startNode, pathEdges, false);
					}
				}
			}
	
			studentValuesFact = new Fact(ssRete.findDeftemplate("studentValues"));
			studentValuesFact.setSlotValue("selection", new Value(selection, RU.STRING));
			studentValuesFact.setSlotValue("action", new Value(action, RU.STRING));
			studentValuesFact.setSlotValue("input", new Value(input, RU.STRING));
			ssRete.assertFact(studentValuesFact);
			
			ArrayList activations = new ArrayList();
			Iterator itr = ssRete.listActivations();
			while(itr.hasNext()) {
				activations.add(itr.next());
			}
			
			
			if(MetaTutorAvatarComponent.isWaitingForActivationList && selection.equalsIgnoreCase("activations")) {
				MetaTutorAvatarComponent.getaListDrop().put(activations);
				return HINT;
			}
			
			if(isHint || isCLHint) {
				ssRete.eval("(bind ?*hintRequest* " + WorkingMemoryConstants.TRUE+")");
			} else {
				ssRete.eval("(bind ?*hintRequest* " + WorkingMemoryConstants.FALSE+")");
			}
			
		} catch (JessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		iterativeDeepening(ssRete.getMaxDepth(), selection, action, input);
				
	//  trace.out(ssRete.findDefrule());
		if(nodeSeq.size() > 0  && !isNodeSeqBuggy(nodeSeq)/*&& isHint || isCLHint*/ && !containsHelperProductionRule()) {
			//if(isHint || isCLHint)
				setMatchedNode((RuleActivationNode)(nodeSeq.get(0)));
			
			if(messages != null) {
				messages.addAll(getMessages(isHint, nodeSeq));
			}
			if(messages != null)
				messages.addAll(getCLHintMessages(isCLHint, nodeSeq));
			int rtnVal = (isHint ? HINT : (isBuggyRuleSequence(nodeSeq)) ? BUGGY : CORRECT);
			return rtnVal;
		}
		else if (getNotMatchedMetaCogNode()!=null){	
			setMatchedNode(noMatchedMetaCogNode);
			messages.addAll(this.getNotMatchedMetaCogNode().getHintMessages());	
		}

		
		
		
		return NOMODEL;	
	}
	
	
	/****
	 * method that ignores the helper production rules
	 */
	public boolean containsHelperProductionRule(){
		HashSet<String> rules = readHelperProduction();
		
		 for (Iterator it = nodeSeq.iterator(); it.hasNext(); ) {
		    	RuleActivationNode node = (RuleActivationNode) it.next();
		        if (rules.contains(node.getName().replaceAll("MAIN::","")))
		            return true;
		    }
		    return false;
	}
	
	
	public HashSet<String> readHelperProduction(){
		HashSet<String> names = new HashSet<String>();
		/*InputStreamReader isr = null;
		String line = "";
    	if(SimSt.WEBSTARTENABLED){
    		ClassLoader cl = this.getClass().getClassLoader();  
            InputStream is = cl.getResourceAsStream("HelperProductionRules.txt");
            isr = new InputStreamReader(is);
    	}
    	else{
    		InputStream is = null;
			try {
				  
				is = new FileInputStream("HelperProductionRules.txt");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isr = new InputStreamReader(is);
    	}
    	
        BufferedReader br = new BufferedReader(isr);
        
        try {
        	trace.out("Reading the file ");
			while((line=br.readLine()) != null)
				names.add(line);
 		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        */
		names.add("simst-tab-clicked");
		return names;
	}
	boolean proactiveHintGiven=false;
	
	boolean isNodeSeqBuggy(List<RuleActivationNode> nodeSeq){
		boolean returnValue=false;
		
		for (RuleActivationNode node : nodeSeq) {
			if(node.getName().contains("BUG")){
				returnValue=true; break;
			}
		}
		return returnValue;
		
	}
	
	/**
	 * 
	 * @param isHint
	 * @param nodeSeq
	 * @return
	 */
	private Vector<String> getMessages(boolean isHint, List<RuleActivationNode> nodeSeq) {

		Vector<String> msgs = new Vector<String>();
			
		for (RuleActivationNode node : nodeSeq) {
			if(isHint)
				msgs.addAll(node.getHintMessages());
		}
		return msgs;
	}

	/**
	 * 
	 * @param isCLHint
	 * @param nodeSeq
	 * @return
	 */
	private Vector<String> getCLHintMessages(boolean isCLHint, List<RuleActivationNode> nodeSeq) {
		
		Vector<String> msgs = new Vector<String>();
			
		for (RuleActivationNode node : nodeSeq) {
			if(isCLHint)
				msgs.addAll(node.getCLHintMessages());
		}
		return msgs;
	}
	
	/**
	 * 
	 * @param nodeSeq
	 * @return
	 */
	boolean isBuggyRuleSequence(List nodeSeq) {
	    for (Iterator it = nodeSeq.iterator(); it.hasNext(); ) {
	    	RuleActivationNode node = (RuleActivationNode) it.next();
	        //if (MTRete.isBuggyRuleName(node.getName()))
	        //    return true;
	    }
	    return false;
    }
	
	
	public boolean proactiveGiven=false;
	/**
	 * @param maxDepth
	 * @param selection
	 * @param action
	 * @param input
	 * @return
	 */
	private boolean iterativeDeepening(int maxDepth, String selection, String action, String input){
		this.maxDepth = maxDepth;
		int depth = 1;
		boolean searchSucceeded = false;
		boolean hint = false;
		boolean successfulTrace = false;
		boolean traced = false;
		String ruleSuccess = "";
		String ruleFail = "";
		
		String step = controller.getMissController().getSimSt().getProblemStepString();
		boolean nonMatchingMetaCogNodeFound=false;	
		
			
			
		setNotMatchedMetaCogNode(null);
		
		if(isCLHint || isHint){
			hint = true;
		}
		
	
		try {
			do {
				// remove all the nodes from the tree
				rootActivation = RuleActivationNode.create(null, 0);
				if(tree != null)
					tree.reset(rootActivation);
				
				RuleActivationNode root = rootActivation;
				root.saveState(ssRete);
				//ssRete.add(clonedWorkingMemory);
				/* FIXME: For some reason after restoring the rete object after saving, it was not
				 * having the same values in its slots. */
				ssRete.add(this.getController().getMissController().getSimSt().getModelTraceWM());
				List wholeAgenda = ssRete.getAgendaAsList(null);
				//trace.err("**************");
				root.createChildren(wholeAgenda, false);
				List children = root.getChildren();
				for(int i=0; !searchSucceeded && i<children.size(); ++i){

					RuleActivationNode child = (RuleActivationNode) children.get(i);
					root.setUpState(ssRete, i);

					searchSucceeded = newDepthLimited(child, hint, depth, selection, action, input);
						
					if (SimStCognitiveTutor.firingNode.equals("")){
						SimStCognitiveTutor.firingNode=child.getName();
					}
					
				  // trace.err("---> ---> " + selection+ "  " + action+ " " + input+ " for " + child.getName() + " is " + searchSucceeded);
			      // trace.err("---> ---> " + child.getHintMessages());
					
					
					
				//	if (searchSucceeded && !isHint && !isCLHint){
				//		addCorrectFiring(child.getName());
				//	}
					Sai sai = new Sai(selection, action,input);
				
					
					if(searchSucceeded == true && !isHint && !isCLHint) {
						if(!child.getName().contains("update")) {
							String ruleName = child.getName().replaceAll("MAIN::", "");
							if(trace.getDebugCode("rr")) trace.err("Success for rule: " + ruleName + "  for selection: " + selection);
							logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR, SimStLogger.METATUTOR_MODEL_TRACING_ACTION, step,
									ruleName, "", sai, true);
							
							/*If the rule is buggy but has a  message, then we would want to show it */
							if (!isHint && !isCLHint && child.getHintMessages().size()>0 && ruleName.startsWith("BUG")){
								setNotMatchedMetaCogNode(child);
								nonMatchingMetaCogNodeFound=true;
							}
					
							if(!ruleName.startsWith("BUG"))
							{
								successfulTrace = true;
								traced = true;
								ruleSuccess = ruleName;
							}
						}
							
					} else if(!isHint && !isCLHint) {
						if(!child.getName().contains("update")) {
							String ruleName = child.getName().replaceAll("MAIN::", "");
							/*get the first ran that did not match, and keep it to give proactive message*/
							if (!isHint && !isCLHint && !nonMatchingMetaCogNodeFound && child.getHintMessages().size()>0){
								//trace.out(" Proactive Message to be shown ");
								setNotMatchedMetaCogNode(child);
								this.getController().getMissController().getSimStPLE().getSimStPeerTutoringPlatform().setTargetWindow (ssRete.findDefrule(child.getName()).getDocstring());
								//addIncorrectFiring(child.getName());
								nonMatchingMetaCogNodeFound=true;
							}
							
							if(trace.getDebugCode("rr")) trace.err("Failure for rule: " + ruleName);
							logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR, SimStLogger.METATUTOR_MODEL_TRACING_ACTION, step,
									ruleName, "", sai, false, 
									child.getActualSelection(), child.getActualAction(), child.getActualInput());

							if(!ruleName.startsWith("BUG"))
							{
								traced = true;
								ruleFail = ruleName;
							}
						}
					}
					
				}
				++(this.maxDepth);
			} while(!searchSucceeded && this.maxDepth <= ssRete.getMaxDepth());

			if(traced)
				addTraceHistory(successfulTrace);
			
			
			
			/*
			 * Restore the rete state after a hint request or failed search or matched buggy 
			 * rule. 
			 */
			//if(isHint || !searchSucceeded) {
			//	
			//	ssRete.eval("(facts)");
			//	rootActivation.loadState(ssRete);
			//	this.getController().getMissController().getSimSt().setModelTraceWM(clonedWorkingMemory);
			//	ssRete.eval("(facts)");
			//}
			
			return searchSucceeded;
		} catch(Exception e) {
			//			trace.err("Error from interativeDeepening at depth: " + depth + ": " + e);
			//		e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * @param node
	 * @param depth
	 * @param selection
	 * @param action
	 * @param input
	 * @return
	 * @throws Exception
	 */
	private boolean newDepthLimited(RuleActivationNode node, boolean isHint, int depth, String selection,
			String action, String input) throws Exception {
		
		if(depth > this.maxDepth)
			return false;
		
		node.setStudentSAI(selection, action, input);
		nodeNowFiring = node;
		List childActivations = node.fire(ssRete);
		nodeNowFiring = null;
		int result = node.getMatchResult();
		if(result == RuleActivationNode.RESULT_UNSET) {
			result = (isHint ? node.isStudentSelectionFound(selection, ssRete.getGlobalContext()):
				node.isStudentSAIFound(selection, action, input, isHint, ssRete.getGlobalContext()));
		}

		setHintSelection(isHint, result, node);
		
		node.saveState(ssRete);
		if(searchSucceeded(isHint, result)) {
			node.getNodeSequence(nodeSeq);
			return true;
		} else if(endSearch(isHint, result)) {
			return false;
		} else if(depth >= this.maxDepth) {
			return false;
		}
		
		/*
		RuleActivationNode chainNode = new RuleActivationNode(-1, null, depth, node);
		chainNode.copyState(node);
		if(childActivations.size() < 1)
			return false;
		chainNode.createChildren(childActivations, isHint);
		List children = chainNode.getChildren();
		
		for(int i=0; i < children.size(); i++) {
			RuleActivationNode child = (RuleActivationNode)children.get(i);
			chainNode.setUpState(ssRete, i);
			boolean childResult = newDepthLimited(child, isHint, depth + 1, selection, action, input);
			if(childResult)
				return true;
		}
		*/
		
		return false;		
	}

	/**
	 * 
	 * @param isHint
	 * @param result
	 * @return
	 */
	private boolean endSearch(boolean isHint, int result) {
		boolean outcome = false;
		if(searchSucceeded(isHint, result))
			outcome =  true;
		else if(result == RuleActivationNode.NO_MATCH)
			outcome = true;
		
		return outcome;
	}

	/**
	 * 
	 * @param isHint
	 * @param result
	 * @return
	 */
	private boolean searchSucceeded(boolean isHint, int result) {
		if(isHint) {
			return (result == RuleActivationNode.MATCH ||
					result == RuleActivationNode.NO_MATCH);
		}
		return result == RuleActivationNode.MATCH;
	}

	/**
	 * 
	 * @param sel
	 * @param act
	 * @return
	 */
	public static boolean isSAIToBeModelTraced(final String sel, final String act) {
		return ((act.indexOf("UpdateTable") != -1) || (act.indexOf("ButtonPressed") != -1)
				|| (act.indexOf("TabClicked") != -1) || (act.indexOf("StartProblem") != -1) 
				|| (act.indexOf("implicit") != -1) && !(sel.equalsIgnoreCase("hint")) 
				&& !(sel.equalsIgnoreCase("help")) && !(sel.equalsIgnoreCase("activations")));
	}

	/**
	 * 
	 * @param predictedSelection
	 * @param predictedAction
	 * @param predictedInput
	 */
	public void setRuleSAI(String predictedSelection, String predictedAction,
			String predictedInput) {
		if(nodeNowFiring == null)
			trace.err("setRuleSAI called with (" + predictedSelection + ", " + predictedAction + ", " + 
					predictedInput + " ) when nodeNowFiring is null");
		else {
			nodeNowFiring.setRuleSAI(predictedSelection, predictedAction, predictedInput, SimStRete.NOT_SPECIFIED);
		}
	}

	/**
	 * @param isHint
	 * @param result
	 * @param node
	 */
	private void setHintSelection(boolean isHint, int result, RuleActivationNode node) {
		
		if(!isHint)
			return;
		if(result == RuleActivationNode.MATCH)
			hintSelection = node.getActualSelection();
		else {
			if(hintSelection == null && result == RuleActivationNode.NO_MATCH)
				hintSelection = node.getActualSelection();
		}
	}
	
	/**
	 * 
	 * @param returnVV
	 * @param context
	 */
	public void setFiringNodeMessages(ValueVector msgVV, Context context) {
		
		if(nodeNowFiring == null)
			trace.err("setFiringNodeMessages called (" + msgVV.toStringWithParens() + ", " + context
					+ ") when nodeNowFiring is null");
		else {
			try {
				nodeNowFiring.setHintMessages(msgVV, context);
			} catch (JessException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Sets the hint messages received from the Carnegie Learning. The messages are
	 * set if and only if the next step is a transformation step as for the equation
	 * type-in step the Carnegie Learning does not have hint messages.
	 * @param msgVV
	 * @param context
	 */
	public void setFiringNodeCLHintMessages(ValueVector msgVV, Context context) {
		
		if(nodeNowFiring == null) {
			trace.err("setFiringNodeMessages called (" + msgVV.toStringWithParens() + ", " + context
					+ ") when nodeNowFiring is null");
		} else {
			try {
				nodeNowFiring.setCLHintMessages(msgVV, context);
			} catch(JessException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param msgVV
	 * @param context
	 */
	public void setMenuOptionMessages(ValueVector msgVV, Context context){
		
		menuOptionMessages = new ArrayList<ArrayList<String>>();
		try {
			setMenuMessages(menuOptionMessages, msgVV, context);
		} catch (JessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param menuOptionMessages
	 * @param msgVV
	 * @param context
	 * @throws JessException
	 */
	private void setMenuMessages(ArrayList<ArrayList<String>> menuOptionMessages,ValueVector msgVV,
			Context context) throws JessException {

		for(int i=0; i < msgVV.size(); i++) {
			Value msgV = msgVV.get(i);
			Value resolvedV = msgV.resolveValue(context);
			String msg = resolvedV.stringValue(context);
			if(msg != null)
				msg = msg.trim();
			if(msg.length() > 0) {
				//TODO add the menuOption msg here
				// msg format is "CognitiveHint : Hint msg" || "MetaCognitiveHint : Hint msg"
				String[] tokens = msg.split(":");
				if(tokens.length == 2){
					ArrayList<String> msgContent = new ArrayList<String>();
					msgContent.add(tokens[0]);
					msgContent.add(tokens[1]);
					menuOptionMessages.add(msgContent);
				} else {
					trace.err("Format of menuMessages is incorrect");
				}
			}
		}
	}

	public String getTargetWindow() {
		return targetWindow;
	}

	public void setTargetWindow(String targetWindow) {
		this.targetWindow = targetWindow;
		this.getController().getMissController().getSimStPLE().getSimStPeerTutoringPlatform().setTargetWindow (targetWindow);
	}
}
