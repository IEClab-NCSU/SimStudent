/*
 * Created on Oct 11, 2003
 *	Assumption:
 *	When using the breakpoints: Breakpoints are disabled in the GO_TO_WM state message
 *  ie. Breakpoints are disabled for all the arcs before the current arc on which check with 
 * 	production system is done.
 * 	eg. Check with production system is done on fist arc then the breakpoints are disable for
 * 	the first arc.
 * 	Break points are only enabled for the arc on which check with production system is done
 * 	This is because the GO_TO_WM calls will be blocking calls and the check with production
 * 	system calls in the MT.handleCommMessage() is non blocking.
 * 	Also it is assumed that before doing a check with production system the previous arc is
 * 	working correctly. Otherwise no detailed feedback will be provided to the user about what
 * 	is wrong.
 */
package edu.cmu.pact.jess;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import jess.Context;
import jess.Fact;
import jess.JessException;
import jess.Rete;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.TextOutput;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.ProblemSummary;


/**
 * @author sanket
 *
 */
/**
 * @author Jonathan Sewall
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JessModelTracing {

	/** The root node of the activation tree, used if the tree is not in use. */
    private RuleActivationNode rootActivation;
    
	/**
	 * The tree representing the activations
	 */
	private RuleActivationTree tree;
	
	/**
	 * Rete engine
	 */
	private MTRete rete;
	
	/**
	 * boolean indicating whether the sai is found or not
	 */
	boolean saiFound = false;

	/**
	 * List of the {@link RuleActivationNode} sequence that traces the sai.
	 */
	private ArrayList<RuleActivationNode> nodeSeq = new ArrayList<RuleActivationNode>();

	/** Author's default string for missing skill categories. */
	private String defaultSkillCategory = null;

    // Mon Oct 10 17:12:33 2005 :: Noboru
    // Pass the result of model tracing to Sim. St.
	/**
	 * Return the list of rule names from the node sequence.
	 */
    public ArrayList<String> getRuleSeq() {
    	ArrayList<String> ruleSeq = new ArrayList<String>();
    	for (Iterator<RuleActivationNode> it = nodeSeq.iterator(); it.hasNext(); )
    		ruleSeq.add(it.next().getDisplayName());
    	return ruleSeq;
    }

    /**
     * @return list of {@link RuleActivationNode#getSkillName()}
     */
	public ArrayList<String> getSkillSeq() {
    	ArrayList<String> skillSeq = new ArrayList<String>();
    	for (Iterator<RuleActivationNode> it = nodeSeq.iterator(); it.hasNext(); )
    		skillSeq.addAll(it.next().getSkillNames(rete, defaultSkillCategory));
		return skillSeq;
	}
	
	static final int CORRECT = 1;
	static final int NOMODEL = 2;
	static final int BUGGY = 3;
	static final int FIREABLE = 4;
	static final int HINT = 5;
	
	/** Console output. */
	private TextOutput textOutput = TextOutput.getNullOutput();
	
	/** Number of rule firings during the current trace. */
	private int nFirings = 0;
	
	/**
	 * Maximum allowed depth on current iteration of
	 * {@link #iterativeDeepening(int, boolean, String, String, String, Vector)
	 */
	private int maxDepth = 0;

	/**
	 * semaphore - used to synchronize the model-tracing and the breakpoints - shared between RuleActivationTree and the JessModelTracing classes
	 * it is set when the user selects Resume from the Cognitive Model menu
	 */
	ResumeBreak resumeBreak;
	/**
	 * true only for lispCheck messages
	 */
	boolean useBreakPoints = false;
 
	/**
	 * Constructor allows execution without conflict tree display.
	 *
	 * @param r Rete engine to execute in
	 * @param threadConflictTree true means start the Conflict Tree in a
	 *            separate thread; false means just construct it in this
	 *            thread
	 */
	public JessModelTracing(MTRete r, final CTAT_Controller controller) {
		this.rete = r;
		this.resumeBreak = new ResumeBreak();
		rootActivation = RuleActivationNode.create(null, 0);
		if (controller == null || Utils.isRuntime())
			return;
		this.tree = controller.getRuleActivationTree();
		updateRuleActivationTree();
	}
	
	/**
	 * @return Returns the isHint.
	 */
	public boolean isHintTrace() {
		return isHint;
	}

	/**
	 * @return Returns the studentAction.
	 */
	public String getStudentAction() {
		return studentAction;
	}

	/**
	 * @return Returns the studentInput.
	 */
	public String getStudentInput() {
		return studentInput;
	}

	/**
	 * @return Returns the studentSelection.
	 */
	public String getStudentSelection() {
		return studentSelection;
	}

	/**
	 * @return Returns the rete.
	 */
	public MTRete getRete() {
		return rete;
	}

	public void dispose(){
		this.rete = null;
		this.nodeSeq.clear();
		this.nodeSeq = null;
		this.textOutput = TextOutput.getNullOutput();
	}
	
	/**
	 * Connect the console output {@link #textOutput} to a stream.
	 * @param  t new {@link edu.cmu.pact.Utilities.TextOutput} value for
	 *         {@link #textOutput}; if null, uses null output stream
	 */
	public void setErrorArea(TextOutput t){
	    if (t == null)
	        textOutput = TextOutput.getNullOutput();
	    else
	        textOutput = t;
	}

	public void setUseBreakPoints(boolean b){
		this.useBreakPoints = b;
	}
	
	/**
	 * Accessor for Conflict Tree.
	 * @return {@link #tree}
	 */
	RuleActivationTree getRuleActivationTree() {
		return tree;
	}
	
	/**
	 * Setter for Conflict Tree {@link #tree}. Also sets our {@link #rete} and
	 * {@link #resumeBreak} into the given tree.
	 * <br />
	 * 08/02/2013: Since this is only ever called with this.tree as an argument, this
	 * has been renamed from setRuleActivationTree(RuleActivationTree t) to
	 * updateRuleActivationTree().
	 * 
	 * @param t tree to set
	 */
	public void updateRuleActivationTree() {
		if (this.tree != null) {
			this.tree.setRete(this.rete);
			this.tree.setResumeBreak(this.resumeBreak);
		}
	}	
	
	/**
	 * Display or update the Conflict Tree.
	 */
	private void displayTree() {
		if (trace.getDebugCode("mtt"))
			trace.out("mtt", "JMT.displayTree() tree "+tree+", skipTree "+skipTree);
		if (tree != null && !skipTree) {
			tree.displayTree();
			tree.getDisplayPanel().validate(tree);
		}
	}

	/** String status for NOTAPPLICABLE result. */
	static final String NOTAPPLICABLE = "No-Applicable";

	/** String status for {@link #CORRECT} result. */
	public static final String SUCCESS = "SUCCESS";

	/** String status for {@link #NOMODEL} result. */
	public static final String NO_MODEL = "NO-MODEL";

	/** String status for {@link #BUGGY} result. */
	public static final String BUG = "BUG";

	/** String status for FIREABLEBUG result. */
	public static final String FIREABLEBUG = "FIREABLE-BUG";

	/** String status for FIREABLEBUG result. */
	public static final String HINT_RESULT = "HINT";

	/** If true, this is a genuine hint cycle: make every effort to find a hint. False for silent hint cycles. */
	private boolean hintMsgsRequired;
	
	/** Whether the model-tracer is processing a hint request. True for silent hint cycles. */
	private boolean isHint;
	
	/** Student's selection. */
	private String studentSelection;
	
	/** Student's action. */
	private String studentAction;
	
	/** Student's input. */
	private String studentInput;

	/** Tutor's selection. */
	private String tutorSelection = null;

	/** Tutor's action. */
	private String tutorAction = null;

	/** Tutor's input. */
	private String tutorInput = null;

	/** Node currently firing. */
	protected RuleActivationNode nodeNowFiring;

    public RuleActivationNode getNodeNowFiring() {
		return nodeNowFiring;
	}

	public void setNodeNowFiring(RuleActivationNode nodeNowFiring) {
		this.nodeNowFiring = nodeNowFiring;
	}

	/** Node that model traced student's input. */
	private RuleActivationNode ruleModeltraced=null;


	public RuleActivationNode getRuleModeltraced() {
		return ruleModeltraced;
	}

	public void setRuleModeltraced(RuleActivationNode ruleModeltraced) {
		this.ruleModeltraced = ruleModeltraced;
	}

	/**
     * list of @link ModelTracingUserfunction called on the SAI of every step prior to model tracing
     */
    private Vector modelTracingHooks;

    /** List of formatted images of working memory. */
	private List<String> wmImages;

	/** If false, will not display the Conflict Tree. */
	private boolean skipTree;

	/** If true, will skip {@link MTRete#bsave(java.io.OutputStream)} calls needed only for {@link WhyNot}. */
	private boolean skipWhyNotSaves = false;
	
	/**
	 * Convert a value returned from
	 * {@link #modelTrace(String, String, String, Vector)} to a string.
	 * @param returnValue value returned from
	 *        {@link #modelTrace(String, String, String, Vector)}
	 * @return String corresponding to returnValue
	 */
	static String resultToString(int returnValue) {
		switch (returnValue) {
		case CORRECT: return SUCCESS;
		case NOMODEL: return NO_MODEL;
		case BUGGY: return BUG;
		case FIREABLE: return FIREABLEBUG;
		case HINT: return HINT_RESULT;
		default:
			return "(undefined)";
		}
	}
	
	/**
	 * Determine whether a rule sequence is buggy or not.
	 * @param  nodeSeq List of rule names
	 * @return true if any rule name in the list satisfies
	 *         {@link MTRete#isBuggyRuleName(String)}; else false
	 */
	boolean isBuggyRuleSequence(List<RuleActivationNode> nodeSeq) {
	    for (Iterator<RuleActivationNode> it = nodeSeq.iterator(); it.hasNext(); ) {
	    	RuleActivationNode node = it.next();
	        if (MTRete.isBuggyRuleName(node.getName()))
	            return true;
	    }
	    return false;
    }
	
	/**
	 * Determine whether a rule sequence has a {@link #FIREABLEBUG} rule.
	 * @param  nodeSeq List of rule names
	 * @return true if any rule name in the list satisfies
	 *         {@link MTRete#isFireableBuggyRuleName(String)} and no name satisfies 
	 *         {@link MTRete#isBuggyRuleName(String)} ; else false
	 */
	boolean isFireableBuggyRuleSequence(List<RuleActivationNode> nodeSeq) {
		boolean result = false;
	    for (Iterator<RuleActivationNode> it = nodeSeq.iterator(); it.hasNext(); ) {
	    	RuleActivationNode node = it.next();
	        if (MTRete.isBuggyRuleName(node.getName()))
	        	return false;
	        if (MTRete.isFireableBuggyRuleName(node.getName()))
	            result = true;
	    }
	    return result;
    }
	
	/**
	 * Determine whether a rule sequence has only ordinary {@link #CORRECT} rules.
	 * @param  nodeSeq List of rule names
	 * @return true if all rule names in the list satisfy
	 *         {@link MTRete#isCorrectRuleName(String)} ; else false
	 */
	boolean isCorrectRuleSequence(List<RuleActivationNode> nodeSeq) {
	    for (Iterator<RuleActivationNode> it = nodeSeq.iterator(); it.hasNext(); ) {
	    	RuleActivationNode node = it.next();
	        if (!MTRete.isCorrectRuleName(node.getName()))
	        	return false;
	    }
	    return true;
    }
	
	/**
	 * Run the model tracer under mutex control.
	 * @param useBreakPoints argument for
	 *        {@link JessModelTracing#setUseBreakPoints(boolean)}
	 * @param isHint true if a hint
	 * @param sel selection
	 * @param act action
	 * @param inp input
	 * @return result of trace as String: "SUCCESS", etc.
	 */
	public String runModelTrace(final boolean useBreakPoints, final boolean isHint,
			final String sel, final String act, final String inp,
			final Vector<String> messages) {
	
		long startTime = (new Date()).getTime();
		int rtnVal = Integer.MIN_VALUE;
		if (trace.getDebugCode("mt"))
			trace.out("mt", "to call modelTrace after synch: isHint "+isHint+
					", sel "+sel+", act "+act+", inp "+inp);
		synchronized(this) {
			setUseBreakPoints(useBreakPoints);
			rtnVal = modelTrace(isHint, sel, act, inp, messages);
		}
		long endTime = (new Date()).getTime();
		long duration = endTime - startTime;
		if (trace.getDebugCode("mt"))
			trace.out("mt", "modelTrace(isHint "+isHint+", sel "+sel+", act "+act+", inp "+inp+
					") returns "+rtnVal+"; duration "+duration);
		return JessModelTracing.resultToString(rtnVal);
	}

	/**
	 * Given the current working memory find the sequence of rules that can 
	 * produce the selection action input.
	 * 
	 * Description:
	 * 1. Perform iterative deepening search until the selection action input matches 
	 *    or there are no rules that can be fired
	 * 2. Add all the buggy rules and see if the selection, action and the input 
	 *    can be produced
	 * 3. return the result
	 * 
	 * Algo:
	 * 1.	iterativeDeepening(state, maxDepth, selection, action, input)
	 * 2.	If nodeSeq.size > 0
	 * 3.		return correct
	 * 4.	else
	 * 5.		Add all the buggy rules to the rete
	 * 6.		iterativeDeepening()
	 * 7.		if nodeSeq.size > 0
	 * 8.			return buggy
	 * 9.		else 
	 * 10.			return no-model
	 * @param isHint true on a hint request; false on a SAI trace 
	 * @param selection student selection
	 * @param action student action
	 * @param input student input
	 * @param msgs to return success or buggy msgs
	 */
	public int modelTrace(boolean isHint, String selection, String action,
			 String input, Vector<String> msgs){
		
		wmImages = null;
		
		//System.out.println("entered JessModelTracing's modelTrace(...)   REMOVE THIS PRINT");

		if (trace.getDebugCode("mt")) trace.out("mt", "modelTrace() rete="+rete.hashCode());
		
        //test if a user function has been set up and if so call it
		if(modelTracingHooks!=null && !modelTracingHooks.isEmpty())
		    callHookFunctions(selection,action,input);

		// call to iterative deepening
		if(trace.getDebugCode("ishint"))
			trace.out("ishint", "JMT.modelTrace()@"+hashCode()+" isHint was "+this.isHint+", now "+isHint);
		this.isHint = hintMsgsRequired = isHint;
		studentSelection = selection;
		studentAction = action;
		studentInput = input;
		clearTutorSAI();
		nodeSeq.clear();
		saiFound = false;
		rete.unloadBuggyRules();
		
		iterativeDeepening(tree, rete.getMaxDepth(), selection, action, input);

		if(trace.getDebugCode("mtt")) trace.out("mtt","nodeSeq.size() after 1st iterativeDeepening "+
				nodeSeq.size()+"; clearing hintMsgsRequired, was "+hintMsgsRequired);
		hintMsgsRequired = false;   // see IsHint: should be true only during first trace
		
		
		
		if (nodeSeq.size() <= 0 && !rete.useSinglePassTrace()) {
			// add the buggy rules and do iterative deepening
			try {
				rete.loadBuggyRules(this.textOutput);
			} catch (Exception e) {
				e.printStackTrace();
			}
			iterativeDeepening(tree, rete.getMaxDepth(), selection, action, input);
			if(trace.getDebugCode("mtt"))
				trace.out("mtt","nodeSeq.size() after +buggy iterativeDeepening " + nodeSeq.size());
		}

		int rtnVal = -1;
		if (nodeSeq.size() > 0) {
			setRuleModeltraced(nodeSeq.get(0));
			if (msgs != null)
				msgs.addAll(getMessages(isHint, nodeSeq));
			rtnVal = (isHint ? HINT : (
					isBuggyRuleSequence(nodeSeq) ? BUGGY : (
							isFireableBuggyRuleSequence(nodeSeq) ? FIREABLE : CORRECT)));
			setTutorSAI(isHint, nodeSeq);
		} else {
			rtnVal = NOMODEL;
		}

		displayTree();  // display the conflict tree from the attempt, not the silent hint

		if (!isHint && (rtnVal == BUGGY || rtnVal == NOMODEL)) { // CTAT2973: for incorrect
			clearTutorSAI();
			nodeSeq.clear();
			saiFound = false;
			HintFact.setHintFact(true, getRete());
			this.isHint = true;
			iterativeDeepening(null, rete.getMaxDepth(), selection, action, "");  // silent hint
			setTutorSAI(this.isHint, nodeSeq);
		}
		
		return rtnVal;
	}

	/**
	 * Null each of {@link #tutorSelection}, {@link #tutorAction}, {@link #tutorInput}.
	 */
	private void clearTutorSAI() {
		tutorSelection = null;
		tutorAction = null;
		tutorInput = null;
	}

	/**
	 * Perform an iterative deepening search until the selection action and the input
	 * matches or there are no rules that can be fired.
	 * Algo:
	 * 1.	maxDepth = 0
	 * 2.	while depth < maxDepth and more successors can be generated
	 * 3.		remove all the nodes from the tree
	 * 4.		moreSuccessors = depthLimited(depth)
	 * 5. 		depth++
	 * 
	 * @param tree the conflict tree to connect to
	 * @param maxDepth - maximum depth to which search is to be done. Specified by the 
	 * 					user from the preferences panel.
	 * @param isHint true if this is a search for a hint;
	 *               false if a search to match SAI
	 * @param selection student's selection to match
	 * @param action student's action to match
	 * @param input student's input to match
	 * @return
	 */
	private boolean iterativeDeepening(/* MTRete rete, */ RuleActivationTree tree, int maxDepth,
			String selection, String action, String input){
		
		if (trace.getDebugCode("mt")) trace.out("mt", "iterativeDeepening("+maxDepth+","+
				(isHint ? "HINT" : "SAI")+","+selection+","+action+","+input);                
		clearTutorSAI();
		nFirings = 0;
		this.maxDepth = maxDepth;
		int depth = 1;
		boolean searchSucceeded = false;
		try{		
			do{
				// remove all the nodes from the tree
		    	rootActivation = RuleActivationNode.create(null, 0);
			    if (tree != null)
			        tree.reset(rootActivation);
				
				// display the iteration number of the iterative deepening search						
				if(MTRete.displayChain || MTRete.displayFired){
				    textOutput.append("\nIteration: " + depth);
				}
			
				if (trace.getDebugCode("mt")) trace.out("mt", "jmt.iterativeDeepening() depth=" + depth);
				// loading the initial state for this iteration
//				rete.resetSAIFact();  // changes WM, so do before get agenda
				RuleActivationNode root = rootActivation;
				root.saveState(rete);  // save can chg agenda: do before create children
				List wholeAgenda = rete.getAgendaAsList(null);
				root.createChildren(wholeAgenda, isHint);
				List children = root.getChildren();
				if (trace.getDebugCode("mt")) trace.out("mt", "root.children.size() "+children.size());
				for (int i = 0; !searchSucceeded && i < children.size(); ++i) {
					RuleActivationNode child = (RuleActivationNode) children.get(i);
					root.setUpState(rete, i);
					searchSucceeded = newDepthLimited(child, isHint, depth,
							selection, action, input);
				}
				++(this.maxDepth);
			} while(!searchSucceeded && this.maxDepth <= rete.getMaxDepth() &&
					 !MTRete.stopModelTracing);
			/*
			 * Restore Rete state after hint request or failed search or
			 * matched buggy rule.  But restore only if fired any rules.
			 */
			if (isHint || !searchSucceeded || isBuggyRuleSequence(nodeSeq)) {
				if (nFirings > 0)
					rootActivation.loadState(rete);
			}
			return searchSucceeded;
		} catch (Exception e) {
			trace.err("Error from iterativeDeepening at depth "+depth+": "+e);
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Recursive portion of the search algorithm. Outline of algorithm:
	 * <pre>
	 * if depth > maxDepth
	 *   return false;
	 * fire the node
	 * if node's predicted SAI matches student's SAI
	 *   return true;     // success
	 * for each new activation created
	 *   create a child node;
	 *   if newDepthLimited(child, depth+1, ...)
	 *     return true;   // success
	 * return false;
	 * </pre>
	 * @param node node in search tree to fire; must not be a chain node
	 * @param isHint true if to deliver a hint; false to trace a student action
	 * @param depth nesting level in search tree
	 * @param selection student SAI element
	 * @param action student SAI element
	 * @param input student SAI element
	 * @return true if should preserve remaining state
	 */
	private boolean newDepthLimited(RuleActivationNode node, boolean isHint,
			int depth, String selection, String action, String input) throws Exception {
            
		if (trace.getDebugCode("mt")) trace.out("mt", "newDepthLimited: depth "+depth+" this.maxDepth "+
				this.maxDepth);
		if (depth > this.maxDepth)
			return false;
                // Noboru 3/12/2007 with Jonathan
                // "no need" & "need" is put to figure out which part we need for gatherActivationList
		node.setStudentSAI(selection, action, input); // no need
		nodeNowFiring = node; // need
                // childActivation shows the ruleActivation after firing the rule
		List childActivations = node.fire(rete); // need
		++nFirings; // no need
		nodeNowFiring = null; // no need
		// display the name of the rule that is fired.
		if(rete.displayChain)
			textOutput.append(indent(depth) + depth + ". " + node.getName());
		processBreakpoint(node.getName(), maxDepth);
		int result = node.getMatchResult(); // no need 

		//Gustavo 10Oct2006
		//Matcher matcher = node.getEdge().getMatcher();	
		
		if (result == RuleActivationNode.RESULT_UNSET) {
			result = node.isStudentSAIFound(selection, action, input, isHint, rete.getGlobalContext());
		}
		
		//result
		
		if (trace.getDebugCode("mt")) trace.out("mt", "isSAIFound() returns "+result);
		setTutorSAI(isHint, result, node);

		{
			boolean toSaveState = ((!Utils.isRuntime() && !skipWhyNotSaves) || childActivations.size() > 1);
					
			if (trace.getDebugCode("whynot")) trace.out("whynot",
					String.format("To save state if %b <= (! runtime %b && ! skip %b) || (nChild %d > 1)",
							toSaveState, Utils.isRuntime(), skipWhyNotSaves, childActivations.size()));
			if (toSaveState)             // sewall 2013/09/10: avoid save if just 1 child
				node.saveState(rete);    // Rete for post-fire WM Editor in WhyNot
		}

		boolean[] endSearch = new boolean[1]; 
		if (endChain(isHint, result, node.getNodeSequence(new ArrayList<RuleActivationNode>()), endSearch))
			return endSearch[0];
		else if (depth >= this.maxDepth)
			return false;                 // can't chain: ran into depth limit

		RuleActivationNode chainNode =
			RuleActivationNode.create(-1, null, depth, node);  // chain has parent's depth
		chainNode.copyState(node);                             // rete for conflict tree
		if (childActivations.size() < 1)
			return false;                 // no children to continue
		chainNode.createChildren(childActivations, isHint);
		List children = chainNode.getChildren();
		if (trace.getDebugCode("mt")) trace.out("mt", "newDepthLimited("+depth+"): "+chainNode.getNodeId()+
				" children.size() "+children.size());
                
		for (int i = 0; i < children.size(); ++i) {
			RuleActivationNode child = (RuleActivationNode) children.get(i); 
			chainNode.setUpState(rete, i);   //loadState, except on first child
			boolean childResult = newDepthLimited(child, isHint, depth+1,
					selection, action, input);
			if (trace.getDebugCode("mt")) trace.out("mt", "newDepthLimited("+depth+"): child["+i+"] "+
					child.getNodeId()+" result "+childResult);
			if (childResult)
				return true; //"first match"? returning the first child that returns true?
		}
		return false;
	}

	/**
	 * If not a hint request, copy the given list to {@link #nodeSeq} and return. For a hint, replace the
	 * contents of {@link #nodeSeq} only if this node sequence is better than that already in {@link #nodeSeq}.
	 * @param isHint true if a hint request
	 * @param nodeSeq list of nodes to put in {@link #nodeSeq}
	 * @return true if search is complete; false if there's possibly a better match to find
	 */
	private boolean setNodeSeq(boolean isHint, ArrayList<RuleActivationNode> nodeSeq) {
		if(!isHint) {
			this.nodeSeq = new ArrayList<RuleActivationNode>(nodeSeq);
			return true;
		}
		
		// characterize each chain by match quality and number of hint messages 
		int oldResult = getMatchResult(this.nodeSeq), newResult = getMatchResult(nodeSeq);
		int oldNHints = countHints(this.nodeSeq),     newNHints = countHints(nodeSeq);
		if(trace.getDebugCode("mt"))
			trace.out("mt", String.format("JMT.setNodeSeq(isHint %b) old, new result %s, %s; nHints %d, %d",
					isHint,	RuleActivationNode.matchIntToString(oldResult),
					RuleActivationNode.matchIntToString(newResult), oldNHints, newNHints));

		// sewall 2013/11/21: no longer distinguish criteria for silent vs ordinary hints
		
		if(this.nodeSeq == null || this.nodeSeq.isEmpty()) {            // at end of 1st chain found
			this.nodeSeq = new ArrayList<RuleActivationNode>(nodeSeq);  // always harvest 1st chain
			if( /* !hintMsgsRequired || */ newNHints > 0)               // silent hint or have hints
				return (newResult == RuleActivationNode.MATCH);         // done if this was a match
			else
				return false;                                           // keep looking for chain w/ hints
		}
		
		if(newResult == RuleActivationNode.MATCH) {                     // latest chain succeeded
			if(oldResult == RuleActivationNode.MATCH) {
				if(oldNHints <= 0 && newNHints > 0) {                   // if now have hints
					this.nodeSeq = new ArrayList<RuleActivationNode>(nodeSeq);  // harvest hints, done
					return true;
				}
				return false;                                           // latest chain no better
			} else {
				if (newNHints > 0)
					this.nodeSeq = new ArrayList<RuleActivationNode>(nodeSeq);  // latest chain is success
				// if could return a chain that matched but lacked hints, save it here
				return (newNHints > 0);                                 // keep searching if matched w/o hints
			}
		}

		if(oldNHints <= 0 && newNHints > 0)			                    // now have hints; didn't before
			this.nodeSeq = new ArrayList<RuleActivationNode>(nodeSeq);  // harvest hints
		return false;                                                   // keep searching for a match
	}

	/**
	 * Characterize this node sequence by the first match result that's <i>not</i>
	 * {@link RuleActivationNode#NOT_SPEC}.
	 * @param nodeSeq nodes on which to call {@link RuleActivationNode#getMatchResult()}
	 * @return chosen result; default {@value RuleActivationNode#NO_MATCH}
	 */
	private int getMatchResult(ArrayList<RuleActivationNode> nodeSeq) {
		int result = -1;
		if(nodeSeq == null)
			result = RuleActivationNode.RESULT_UNSET;
		else {
			for(int i = 0; i < nodeSeq.size() && result < 0; ++i) {
				RuleActivationNode node = nodeSeq.get(i);
				int matchResult = node.getMatchResult();
				if(matchResult != RuleActivationNode.NOT_SPEC && matchResult != RuleActivationNode.RESULT_UNSET)
					result = matchResult;
			}
		}
		if(result < 0)                      // should happen only if ran out of trace depth or activations
			result = RuleActivationNode.NO_MATCH;
		if(trace.getDebugCode("hints"))
			trace.out("hints", String.format("JMT.getMatchResult() returns %s[%d]",
					RuleActivationNode.matchIntToString(result), result));
		return result;
	}

	/**
	 * @param nodeSeq nodes on which to call {@link RuleActivationNode#getHintMessages()}
	 * @return total number of hint messages in the sequence
	 */
	private int countHints(List<RuleActivationNode> nodeSeq) {
		int nHints = 0;
		if(nodeSeq == null)
			return nHints;
		for (RuleActivationNode node : nodeSeq)
			nHints += node.getHintMessages().size();
		return nHints;
	}

	/**
	 * Decide whether a match result from
	 * {@link RuleActivationNode#isStudentSAIFound(String, String, String)} warrants
	 * collecting hint or buggy messages.
	 * @param isHint true if this is a hint request
	 * @param nodeSeq where to get the messages
	 * @return message list
	 */
	private Vector<String> getMessages(boolean isHint, List<RuleActivationNode> nodeSeq) {
		Vector<String> msgs = new Vector<String>();
		boolean isBuggy = !isCorrectRuleSequence(nodeSeq);
			
		for (RuleActivationNode node : nodeSeq) {
			if (isBuggy)
				msgs.addAll(node.getBuggyMessages());
			else if (isHint)
				msgs.addAll(node.getHintMessages());
			else
				msgs.addAll(node.getSuccessMessages());
		}
		return msgs;
	}
	
	/**
	 * Decide whether a match result from
	 * {@link RuleActivationNode#isStudentSAIFound(String, String, String)} indicates
	 * a successful end to this search. 
	 * @param isHint true if this is a hint request
	 * @param matchResult result to test
	 * @return true if matchResult is {@link RuleActivationNode#MATCH} or
	 *         this is a hint request and matchResult is 
	 *         {@link RuleActivationNode#NO_MATCH}
	 */
	private boolean searchSucceeded(boolean isHint, int matchResult) {
		if (isHint) {
			return (matchResult == RuleActivationNode.MATCH ||
					matchResult == RuleActivationNode.NO_MATCH);
		}
		return matchResult == RuleActivationNode.MATCH;
	}
	
	/**
	 * Decide whether to stop searching this subtree. Possibly harvest
	 * the given node sequence as the answer.
	 * @param isHint true if this is a hint request
	 * @param matchResult result to test
	 * @param nodeSeq chain to test and maybe harvest
	 * @param endSearch tell whether to end the search completely
	 * @return true if matchResult is {@link RuleActivationNode#MATCH} or
	 *         this is a hint request and matchResult is 
	 *         {@link RuleActivationNode#NO_MATCH}
	 */
	private boolean endChain(boolean isHint, int matchResult,
			ArrayList<RuleActivationNode> nodeSeq, boolean[] endSearch) {
		boolean result = false;
		if (searchSucceeded(isHint, matchResult)) {
			endSearch[0] = setNodeSeq(isHint, nodeSeq);
			result = true;            // end chain
		} else if (matchResult == RuleActivationNode.NO_MATCH) {
//			endSearch[0] = setNodeSeq(isHint, nodeSeq);
			result = true;            // end chain
		}
		if (trace.getDebugCode("mt")) trace.out("mt", "endSearch("+isHint+","+matchResult+
				") returns "+result);
		return result;
	}
	
	/**
	 * Format indent for console output.
     * @param  depth (number of tabs) of indentation
     * @return String with leading newline and depth number of tabs
     */
    private String indent(int depth) {
        StringBuffer result = new StringBuffer("\n");
        for(int z = 0; z < depth; z++)
            result.append("  ");
        return result.toString();
    }

    /**
	 * Check if a breakpoint is set on the named rule.
	 * Gets list of rules with breakpoints from {@link #tree}.
	 * @param  ruleName rule to check
	 * @param  maxDepth depth to test against {@link #tree}
	 * @return true if a breakpoint is set on this rule
	 */
	private boolean isBreakpointSet(String ruleName, int maxDepth) {
	    if (tree == null)
	        return false;
		List v = tree.getBreakPointRules();	 // list of rules with breakpoints
		if (useBreakPoints && maxDepth >= tree.getMinDepth() &&
		        v.contains(ruleName))
		    return true;
		else
		    return false;
	}
	
	/**
	 * Halt model-tracing if a breakpoint is set. Saves rete state to location
	 * "./breakState.tmp"; restores after resume from breakpoint.
	 * @param  ruleName rule to check
	 * @param  maxDepth depth arg for {@link #isBreakpointSet(String, int)}
	 */
	private void processBreakpoint(String ruleName, int maxDepth) {
	    try{
	        if (isBreakpointSet(ruleName, maxDepth)) {
		        // save the state before break
		        rete.saveState(".", "breakState.tmp");
		        
	            // halt the model-tracing and enable the resume button in the debugger
	            //		tree.validate();
	            // display in modeltracing that the break point is set
	            this.textOutput.append("\nBreakpoint on rule " + ruleName + " reached. " +
	            "\nSelect \"Resume\" from the Cognitive Model menu to continue.");
	            if (tree != null)
	                tree.getDisplayPanel().show(tree);
	            resumeBreak.isResume();
	            rete.loadState(".", "breakState.tmp");
	        }
	    }finally{
	        File f = new File(".", "breakState.tmp");
	        try { f.delete(); } catch (Exception e) {}
	        f = null;
	    }
	}


	public void saveState(String dirName, String fileName){
		long startTime = (new Date()).getTime();
		rete.saveState(dirName, fileName);
		if (trace.getDebugCode("mtt")) trace.out("mtt", "time(saveState) = " +
				   ((new Date()).getTime() - startTime));
	}
	
	public void loadState(String dirName, String fileName){
		long startTime = (new Date()).getTime();
		rete.loadState(dirName, fileName);
		if (trace.getDebugCode("mtt")) trace.out("mtt", "time(loadState) = " +
				   ((new Date()).getTime() - startTime));
	}

	/**
	 * Halt the Rete. Throws a {@link JessException} to stop Rete execution
	 * immediately. Also calls {@link MessageTank#clear()} to cancel any TPAs queued.
	 * N.B. {@link Rete#halt()} apparently only prevents the execution of further rules.
	 * @param routine name of calling routine
	 * @throws JessException to halt Rete
	 */
	void haltRete(String routine) throws JessException {
		MT mt;
		BR_Controller controller;
        if (rete != null
        		&& (mt = rete.getMT()) != null
        		&& (controller = (BR_Controller) mt.getController()) != null) {
        	controller.getMessageTank().clear();
        }
		getRete().haltRete("JessModelTracing.haltRete(), called by "+routine);
	}

	/**
	 * Set a {@link Matcher} in the {@link #nodeNowFiring} and evaluate the
	 * student input. 
	 * No-op (returns {@link RuleActivationNode#MATCH}) if
	 * {@link #nodeNowFiring} null.
	 * @param m Matcher for node
	 * @param isHint true if this is a hint
	 * @return {@link RuleActivationNode#NOT_SPEC},
	 *         {@link RuleActivationNode#MATCH} or 				
	 *         {@link RuleActivationNode#NO_MATCH}
	 */
	public int testFiringNodeSAI(Matcher m, boolean isHint) {
		if (nodeNowFiring == null) {
			trace.err("JessModelTracing.testFiringNodeSelection("+
					Utils.getSimpleName(m.getClass().getName())+") called when no node now firing");
			return RuleActivationNode.MATCH;
		} else {
			int result;
			nodeNowFiring.setMatcher(m);
			if (isHint)
				result = nodeNowFiring.testMatcherSelection();
			else
				result = nodeNowFiring.testMatcherSAI(isHint);
			nodeNowFiring.setMatchResult(result);
			return result;
		}
	}

	/**
	 * Attempt to match 2 given selection-action-input triples. 
	 * No-op (returns {@link RuleActivationNode#MATCH}) if
	 * {@link #nodeNowFiring} null.
	 * @param selectionCombo student-selected component
	 * @param actionCombo student action
	 * @param inputCombo student input value
	 * @param predictedSelection rule-predicted component
	 * @param predictedAction rule-predicted action
	 * @param predictedInput rule-predicted input
	 * @param predictedInputTestFunction rule-predicted input test function
	 * @param ctx Context for input test function
	 * @return {@link RuleActivationNode#NOT_SPEC},
	 *         {@link RuleActivationNode#MATCH} or 				
	 *         {@link RuleActivationNode#NO_MATCH}
	 */
	int testFiringNodeSAI(String predictedSelection, String predictedAction,
			String predictedInput, String predictedInputTestFunction,
			Context ctx) {
		return testFiringNodeSAI(predictedSelection, predictedAction,
				predictedInput, predictedInputTestFunction,
				false, ctx);
	}

	/**
	 * Attempt to match 2 given selection-action-input triples. 
	 * No-op (returns {@link RuleActivationNode#MATCH}) if
	 * {@link #nodeNowFiring} null.
	 * @param selectionCombo student-selected component
	 * @param actionCombo student action
	 * @param inputCombo student input value
	 * @param predictedSelection rule-predicted component
	 * @param predictedAction rule-predicted action
	 * @param predictedInput rule-predicted input
	 * @param predictedInputTestFunction rule-predicted input test function
	 * @param isHint if true, match only S of S,A,I elements; else match all 
	 * @param ctx Context for input test function
	 * @return {@link RuleActivationNode#NOT_SPEC},
	 *         {@link RuleActivationNode#MATCH} or 				
	 *         {@link RuleActivationNode#NO_MATCH}
	 */
	int testFiringNodeSAI(String predictedSelection, String predictedAction,
			String predictedInput, String predictedInputTestFunction,
			boolean isHint, Context ctx) {
		if (nodeNowFiring == null) {
			trace.err("JessModelTracing.testFiringNodeSelection("+
					predictedSelection+") called when no node now firing");
			return RuleActivationNode.MATCH;
		} else {
			int result = nodeNowFiring.isSAIFound(getStudentSelection(),
					getStudentAction(), getStudentInput(), predictedSelection, 
					predictedAction, predictedInput, predictedInputTestFunction,
					isHint, ctx);
			nodeNowFiring.setMatchResult(result);
			return result; 
		}
	}	

	int testFiringNodeSAI(boolean Matchresult) {
		int result = (Matchresult == true ? 1 : 3);
		nodeNowFiring.setMatchResult(result);
		return result; 
	}	

	/** Argument for {@link #setFiringNodeMessages(MessageGroup, ValueVector, Context)} */
    static enum MessageGroup { Undefined, Hint, Success, Buggy };
	
	/**
	 * Set the given hint or success or buggy messages into the {@link #nodeNowFiring}.
	 * No-op if {@link #nodeNowFiring} null. Will only set success messages on explicit
	 * whichMsgs == @value JessModelTracing.MessageGroup#Success}.
	 * @param whichMsgs whether to set hint, buggy, etc.
	 * @param msgVV list of hint or buggy messages set by the rule
	 * @param ctx context for Jess evaluation  
	 */
	void setFiringNodeMessages(MessageGroup whichMsgs, ValueVector msgVV, Context ctx)  
			throws JessException {
		  
		if (nodeNowFiring == null) {
			trace.err("JessModelTracing.setFiringNodeMessages("+
					msgVV.toStringWithParens()+", "+ctx+
					") called when no node now firing");
			return;
		}
		switch (whichMsgs) {
		case Hint:
			nodeNowFiring.setHintMessages(msgVV, ctx); break;
		case Success:
			nodeNowFiring.setSuccessMessages(msgVV, ctx); break;
		case Buggy:
			nodeNowFiring.setBuggyMessages(msgVV, ctx); break;
		default:
			if (isHintTrace())
				nodeNowFiring.setHintMessages(msgVV, ctx);
			else if (!nodeNowFiring.isCorrectRule())
				nodeNowFiring.setBuggyMessages(msgVV, ctx);
			else
				nodeNowFiring.setHintMessages(msgVV, ctx);
				//; // only set success messages on explicit instruction
				
		}
	}
	
	/**
	 * Set the rule selection, action, input values in {@link #nodeNowFiring}.
	 * No-op if {@link #nodeNowFiring} null.
	 * @param predictedSelection
	 * @param predictedAction
	 * @param predictedInput
	 * @param predictedInputTestFunction
	 */
	void setRuleSAI(String predictedSelection, String predictedAction, String predictedInput, String predictedInputTestFunction) {
		if (nodeNowFiring == null)
			trace.err("JessModelTracing.setRuleSAI("+
					predictedSelection+", "+predictedAction+", "+predictedInput+
					") called when no node now firing");
		else
			nodeNowFiring.setRuleSAI(predictedSelection, predictedAction,
					predictedInput, predictedInputTestFunction);
	}
        

        
        void addRuleFoas(Vector /*of String*/ foa) {

                if (nodeNowFiring == null)
                        trace.err("JessModelTracing.addRuleFoas("+foa+")");
                else
                    for (int i=0; i<foa.size(); i++)
                    {
                        nodeNowFiring.addRuleFoa((String) foa.get(i));
                    }
        }
        
        

	/**
	 * Equivalent to {@link #setRuleSAI(String, String, String, String)
	 * setRuleSAI(predictedSelection, predictedAction, predictedInput, "NotSpecified")}.
	 * @param predictedSelection
	 * @param predictedAction
	 * @param predictedInput
	 */
	void setRuleSAI(String predictedSelection, String predictedAction, String predictedInput) {
		setRuleSAI(predictedSelection, predictedAction, predictedInput, MTRete.NOT_SPECIFIED);
	}
	
	/**
	 * Attempt to match 2 given selection-action-input triples. 
	 * No-op (returns {@link RuleActivationNode#MATCH}) if
	 * {@link #nodeNowFiring} null.
	 * @param predictedSelection rule-predicted component
	 * @param ctx for {@link RuleActivationNode#isSelectionFound(String, String, Context)}
	 * @return {@link RuleActivationNode#NOT_SPEC},
	 *         {@link RuleActivationNode#MATCH} or 				
	 *         {@link RuleActivationNode#NO_MATCH}
	 */
	int testFiringNodeSelection(String predictedSelection, Context ctx) {
		if (nodeNowFiring == null) {
			trace.err("JessModelTracing.testFiringNodeSelection("+
					predictedSelection+") called when no node now firing");
			return RuleActivationNode.MATCH;
		} else {
			int result = nodeNowFiring.isSelectionFound(getStudentSelection(),
				predictedSelection, ctx);
			nodeNowFiring.setMatchResult(result);
			return result;
		}
	}	
	
	/**
	 * Set {@link #tutorSelection}, {@link #tutorAction}, {@link #tutorInput}
	 * for retrieval by external users.
	 * @param isHint true if this was a hint request
	 * @param result return from node's
	 *        {@link RuleActivationNode#isStudentSelectionFound(String)}
	 * @param node get the new value from this node's
	 *        {@link RuleActivationNode#getActualSelection()}
	 */
	private void setTutorSAI(boolean isHint, int result,
			RuleActivationNode node) {
		if (result == RuleActivationNode.MATCH ||
				(isHint && result == RuleActivationNode.NO_MATCH)) {
		}
	}	
	
	/**
	 * Set {@link #tutorSelection}, {@link #tutorAction}, {@link #tutorInput}
	 * for retrieval by external users.
	 * @param isHint true if this was a hint request
	 * @param nodeSeq node sequence to analyze
	 */
	private void setTutorSAI(boolean isHint, List<RuleActivationNode> nodeSeq) {
		if(nodeSeq == null || nodeSeq.size() < 1)
			return;
		RuleActivationNode lastNode = nodeSeq.get(nodeSeq.size()-1);
		int lastResult = lastNode.getMatchResult();

		if (lastResult == RuleActivationNode.MATCH ||
				(isHint && lastResult == RuleActivationNode.NO_MATCH))
		{
			final int S = 1, A = 2, I = 4, SAI = S|A|I;  // bit labels
			int done = 0;
			for(int i = nodeSeq.size()-1; 0 <= i && done != SAI; i--) {
				RuleActivationNode node = nodeSeq.get(i);
				for(int e = S; e <= I; e <<= 1) {    // for each S, A, I element
					if(trace.getDebugCode("mt")) 
						trace.out("mt", String.format("JMT.setTutorSAI() nodeSeq[%d][%x]: done %x, actualSelection=%s, Action=%s, Inupt=%s;",
								i, e, done, node.getActualSelection(), node.getActualAction(), node.getActualInput()));
					if((done & e) != 0)
						continue;                    // already have value for this element
					String s = null;
					switch(e) {                                    // grab the proper element
					case S:	s = node.getActualSelection(); break;
					case A:	s = node.getActualAction(); break;
					case I:	s = node.getActualInput(); break;
					}
					if(s == null || s.trim().length() < 1 || MTRete.DONT_CARE.equalsIgnoreCase(s) ||
							MTRete.NOT_SPECIFIED.equalsIgnoreCase(s))
						continue;
					switch(e) {                                    // set the proper element
					case S: tutorSelection = s; done |= S; break;
					case A: tutorAction = s;    done |= A; break;
					case I: tutorInput = s;     done |= I; break;
					}
				}
			}
			if(trace.getDebugCode("mt")) 
				trace.out("mt", "JMT.setTutorSAI() at end of nodeSeq: tutorSelection="+tutorSelection+
						", tutorAction="+tutorAction+
						", tutorInput="+tutorInput+";");
			// set any yet undone from student values
			if((done & S) == 0) tutorSelection = lastNode.getReqSelection();
			if((done & A) == 0) tutorAction = lastNode.getReqAction();
			if((done & I) == 0) tutorInput = lastNode.getReqInput();
		}
	}

	/**
	 * @param b new value for #skipTree
	 */
	public void setSkipTree(boolean b) {
		this.skipTree = b;
	}

	/**
	 * Tell whether we're currently firing a {@link RuleActivationNode}.
	 * @return true if (@link #nodeNowFiring} is not null
	 */
	public boolean isModelTracing() {
		return nodeNowFiring != null;
	}

	/**
	 * @return the {@link #tutorSelection}.
	 */
	public String getTutorSelection() {
		return tutorSelection;
	}

	/**
	 * @return the {@link #tutorAction}
	 */
	public String getTutorAction() {
		return tutorAction;
	}

	/**
	 * @return the {@link #tutorInput}
	 */
	public String getTutorInput() {
		return tutorInput;
	}

	/**
	 * @param rete The rete to set.
	 */
	void setRete(MTRete rete) {
		this.rete = rete;
		updateRuleActivationTree();
	}
    /**
     * Add a @link ModelTracingUserfunction to be called after every input
     * Functions are called in queue order
     * If another function of the same underlying class is already in the list
     * the function is not added
     * @param function 
     * @return a boolean indicating whether the function was actually added
     */
     boolean addHookCall(ModelTracingUserfunction function)
    {
        if(modelTracingHooks==null)
                modelTracingHooks=new Vector();
        else
        {
            if(MTRete.findUserfuction(function,modelTracingHooks)!=-1)
                    return false;
            
        }
        modelTracingHooks.add(function);
        return true;
    }
    /**
     * Remove a @link ModelTracingUserfunction with the same underlying class in it is in the list of hook functions to call
     * @param function
     * @return a boolean indicating whether the function was removed or not
     */
    boolean removeHookCall(ModelTracingUserfunction function)
    {
        int index=MTRete.findUserfuction(function,modelTracingHooks);
        if(index==-1)
            return false;
        modelTracingHooks.remove(index);
        return true;
    }
    /**
     *call each function in the model tracing hook list on an SAI tuple
     * @param selection
     * @param action
     * @param input
     */
    private void callHookFunctions(String selection,String action,String input) {
        ValueVector args=null;
        if(modelTracingHooks==null || modelTracingHooks.isEmpty())
            return;
        Iterator iter=modelTracingHooks.iterator();
        while(iter.hasNext())
        {
            ModelTracingUserfunction curFunction=(ModelTracingUserfunction)iter.next();
            try
            {
            
            args=curFunction.getArguments(selection,action,input,getRete());
            
            curFunction.javaCall(args,rete.getGlobalContext());
            
            }
            catch(JessException e)
            {
                String argErrMessage="Error calling model trace hook,"+ curFunction.getName()+" arguments: "+ args;
                trace.err(argErrMessage);
                e.printStackTrace();
                PrintWriter jessErrStream=rete.getErrStream();
                jessErrStream.println(argErrMessage);
                e.printStackTrace(jessErrStream);
                
                
            }
        }
        
    }

	/**
	 * Predicate to decide from action and selection whether this request
	 * should be model-traced.
	 * @param sel student's selection
	 * @param act student's action
	 * @return true if action is "Update" or action is "ButtonPressed" and
	 *         selection is not Hint button
	 */
	static boolean isSAIToBeModelTraced(final String sel, final String act) {
		return ((act.indexOf("update")) != -1 ) ||
				((act.indexOf("Update")) != -1) ||
				(act.equalsIgnoreCase("ButtonPressed") &&
						!sel.equalsIgnoreCase("help") &&
						!sel.equalsIgnoreCase("hint"));
	}

	/**
	 * Fire the given node. This method is used by SimSt.
	 * Gustavo 12 March 2007: we created this function because calling fire()
	 * from SimSt wasn't working. When firing, we need to correctly set the
	 * field JMT 'nodeNowFiring', in order to prepare JessModelTracing.
	 * @param ran node whose activation should fire
	 * @throws JessException
	 */
	public void fireNode(RuleActivationNode ran) throws JessException {
	    nodeNowFiring = ran;
	    ran.fire(rete);
	    nodeNowFiring = null;
	}

	
	//nbarba: clone function of fireNode to externally pass the oracleRete... only for tesing purposes, will be deleted
	public void fireNodeOracle(RuleActivationNode ran, JessOracleRete oracleRete) throws JessException {
		
		List wholeAgenda = oracleRete.getAgendaAsList(null);
		System.out.println("############ Whole agenda mesa stin ruleActivation fireOracle:"+wholeAgenda);
		
		nodeNowFiring = ran;
	    ran.fireOracle(oracleRete);
	    nodeNowFiring = null;
	}
	
	
	/**
	 * Return a matcher instance by this class name. First tries argument
	 * as fully-qualified class name; then tries as if simple class name 
	 * in checks edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.*. 
	 * @param possible class name
	 * @return Matcher instance if name resolves to matcher; else null
	 */
	public Matcher getMatcher(String clsName) { 
		if (clsName == null)
			return null;
		String[] clsNames = {
			clsName,
			"edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher."+clsName
		};
		Class cls = null;
		for (int i = 0; i < clsNames.length && cls == null; ++i ) {
			try {
				cls = Class.forName(clsNames[i]);
			} catch (ClassNotFoundException cnfe) {
				if (trace.getDebugCode("mt")) trace.out("mt", "error finding class "+clsNames[i]+": "+cnfe);
			}
		}
		if (cls == null)
			return null;
		try {
			Object inst = cls.newInstance();
			if (inst instanceof Matcher)
				return (Matcher) inst;
			if (trace.getDebugCode("mt")) trace.out("mt", "error: class "+cls.getName()+" is not a subclass of Matcher");
		} catch (Exception e) {
			if (trace.getDebugCode("mt")) trace.out("mt", "error instantiating class "+cls.getName()+": "+e);
		}		
		return null;
	}

	/**
	 * @return {@link #wmImages}
	 */
	public List<String> getWMImages() {
		return wmImages;
	}
	
	/**
	 * Add an element to the list {@link #wmImages} of working memory images.
	 * @param image formatted string with fact contents 
	 */
	public synchronized void addWMImage(String image) {
		if (wmImages == null)
			wmImages = new ArrayList<String>();
		wmImages.add(image);
	}

	/**
	 * @return {@link #skipWhyNotSaves}
	 */
	boolean getSkipWhyNotSaves() {
		return skipWhyNotSaves;
	}

	/**
	 * @param skipWhyNotSaves new value for {@link #skipWhyNotSaves} 
	 */
	void setSkipWhyNotSaves(boolean skipWhyNotSaves) {
		this.skipWhyNotSaves = skipWhyNotSaves;
	}

	/**
	 * @return the defaultSkillCategory
	 */
	public String getDefaultSkillCategory() {
		return defaultSkillCategory;
	}

	/**
	 * @param defaultSkillCategory the defaultSkillCategory to set
	 */
	public void setDefaultSkillCategory(String defaultSkillCategory) {
		this.defaultSkillCategory = defaultSkillCategory;
	}

	/**
	 * @return the hintMsgsRequired
	 */
	boolean isHintMsgsRequired() {
		return hintMsgsRequired;
	}

	/**
	 * Clean up the breakfast table before a new request. Clear a bunch of fields
	 * to avoid effects of last trace. Call this before a new trace.
	 * @param hintMsgsRequired true if the forthcoming call is a true hint
	 */
	void setupForNewRequest(boolean hintMsgsRequired) {
		nFirings = 0;
		nodeSeq = new ArrayList<RuleActivationNode>();
		saiFound = false;
		studentSelection = studentAction = studentInput = null;
		tutorSelection = tutorAction = tutorInput = null;
		wmImages = null;
		this.isHint = false;
		this.hintMsgsRequired = hintMsgsRequired;
	}

	/**
	 * @param sel student selection
	 * @param act student action
	 * @return true if action is ButtonPressed and selection is help or hint
	 */
	public static boolean isHintRequest(String sel, String act) {
		return act.toString().equalsIgnoreCase("ButtonPressed") &&
				(sel.equalsIgnoreCase("hint") || sel.equalsIgnoreCase("help"));
	}
}
