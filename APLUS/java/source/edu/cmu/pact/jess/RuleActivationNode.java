/*
 * Created on Oct 6, 2003
 *
 */
package edu.cmu.pact.jess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import jess.Activation;
import jess.ActivationData;
import jess.Context;
import jess.Fact;
import jess.HasLHS;
import jess.JessException;
import jess.Rete;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Utilities.EventLogger;
import edu.cmu.pact.Utilities.TextOutput;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.SimSt;

/**
 * @author sanket
 *
 */
public class RuleActivationNode extends DefaultMutableTreeNode{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7915549455481477714L;

	/** Static to generate unique node labels. */
	private static long nodeIdGenerator = 0;

	/** Used by {@link #saveState(Rete)} and {@link #saveState(Rete)}. */
	private byte[] reteState = null;
	
	/** To capture the error output from the Activation's RHS. */
	private TextOutput errorOutput;
	
	/**
	 * Unique identifier for this node: formed from {@link #ruleName}
	 * suffixed by '_' and current value of {@link #nodeIdGenerator}.
	 */
	private final String nodeId;
	
	/** Instance for testing predicted vs. student selection, action, input. */
	private Matcher matcher = null;
	
	/** Whether the HTML text needs to be recalculated. */
	private boolean redoHtmlText = false;
	
	/** Comparison of actual and req s/a/i formatted as an HTML table. */
	private String htmlText = "";

	/** Depth of this node. */
	private final int searchDepth;
	
	/** 0-based position in agenda of our Activation. */
	private int agendaIndex = -2;
	
	public int getAgendaIndex()
	{
		return agendaIndex;
	}
	
	public void setAgendaIndex(int index)
	{
		agendaIndex = index;
	}
	
	/** Information to find a matching Activation in the agenda, possibly after bsave-bload. */
	private ActivationData actData = null;
	
	/**
	 * The activation for this node.
     * An agenda entry, a rule that is ready to fire
	 */
	private Activation activation;
	
	/** Rule name from this activation. */
	private final String ruleName;
	
	/** Rule's display name. */
	private final String displayName;
	
	/**
	 * True if this is a chain node. Chain nodes are not activations, but
	 * instead are links from parent activations to children.
	 */
	private final boolean chainNode;
	
	/** Hint messages constructed by this activation. */
	private Vector hintMessages = new Vector();

	/** CL Hint messages constructed by this activation. */
	private Vector clHintMessages = new Vector();
	
	/** Success messages constructed by this activation. */
	private Vector successMessages = new Vector();

	/** Buggy messages constructed by this activation. */
	private Vector buggyMessages = new Vector();
	
	/** The selection predicted by the rule engine. */
	private String actualSelection = MTRete.NOT_SPECIFIED;
	
	/** The action predicted by the rule engine. */
	private String actualAction = MTRete.NOT_SPECIFIED;
	
	/** The input predicted by the rule engine. */
	private String actualInput = MTRete.NOT_SPECIFIED;
	
	/** The input predicted by the rule engine. */
	private String actualInputTestFunction = MTRete.NOT_SPECIFIED;
	
	/** The selection sent from the student. */	
	private String reqSelection = MTRete.NOT_SPECIFIED;
	
	/** The action sent from the student. */	
	private String reqAction = MTRete.NOT_SPECIFIED;
	
	/** The input sent from the student. */	
	private String reqInput = MTRete.NOT_SPECIFIED;
	
	//Gustavo 10 May 2007
        //new field added in order to make available the FOAs through which the rule fired
	private Vector /* of String */ foas = null;

	public void addRuleFoa(String foa) {
	    if (foas==null) //lazy object construction, to save memory
                foas = new Vector();
	    foas.add(foa);
	    
	  // trace.err("adding foa for rule" + getName() + " (and foa size is " + foas.size());
	    
	}

    public Vector getRuleFoas() {
            return foas;
     }
        
        
        
	/** For saving the Rete's input and output routers through (bsave), (bload). */
	private MTRete.Routers routers;

	/**
	 * Factory for most constructor calls. Creates a parent-less node.
	 * @param act
	 * @param depth
	 * @return
	 */
	public static RuleActivationNode create(Activation act, int depth) {
		return create((act == null ? -1 : 0), act, depth, null);
	}

	/**
	 * Factory for internal constructor calls. Creates child node of given parent.
	 * @param agendaIndex 0-based position in agenda of given Activation;
	 *        if less than 0, then this is a chain node
	 * @param act Activation; null if this is a chain node
	 * @param searchDepth 0-based level in searchDepth-limited search tree 
	 * @param parent parent node in search tree
	 */
	static RuleActivationNode create(int agendaIndex, Activation act, int searchDepth,
			RuleActivationNode parent) {
		if(VersionInformation.isRunningSimSt()  && !edu.cmu.pact.Utilities.Utils.isRuntime())
			return new SimStRuleActivationNode((act == null ? -1 : 0), act, searchDepth, parent);
		else
			return new RuleActivationNode((act == null ? -1 : 0), act, searchDepth, parent);
	}

	/**
	 * The constructor for the node.
	 * @param agendaIndex 0-based position in agenda of given Activation;
	 *        if less than 0, then this is a chain node
	 * @param act Activation; null if this is a chain node
	 * @param searchDepth 0-based level in searchDepth-limited search tree 
	 * @param parent parent node in search tree
	 */
	public RuleActivationNode(int agendaIndex, Activation act, int searchDepth,
			RuleActivationNode parent) {
		this.agendaIndex = agendaIndex;
		this.activation = act;
		if (act != null) {
			try {
				actData = new ActivationData(act);
				if (trace.getDebugCode("mt")) trace.out("mt", "new RuleActivationNode["+agendaIndex+"]: "+actData);
			} catch (JessException je) {
				trace.err("error getting activation data from act["+agendaIndex+"]="+act+": "+je);
			}
			chainNode = false;
			ruleName = act.getRule().getName();
			displayName = act.getRule().getDisplayName();
			errorOutput = TextOutput.getTextOutput(new StringWriter());
			// TODO:  use null output at student time
		}else{ 
			chainNode = true;
			ruleName = displayName = CHAIN;
			errorOutput = TextOutput.getNullOutput();
		}
		if (displayName.indexOf('&') >= 0) {
		    Exception e = new Exception("ERROR: rule display name has \"&\": ruleName "+
										ruleName+", displayName "+displayName);
		    e.printStackTrace();
		}
		nodeId = ruleName + "_" + (++nodeIdGenerator);
		this.searchDepth = searchDepth;
		if (parent != null)
			parent.insert(this, parent.getChildCount());
	}
	
	public Activation getActivation(){
		return this.activation;
	}
	
	/**
	 * Returns the rule name.
	 * @return result of {@link #getName()}
	 */
	public String toString(){
		return getName();
	}
	
	/**
	 * @return Returns the {@link #nodeId}.
	 */
	String getNodeId() {
		return nodeId;
	}
	
	/**
	 * Load the state of the Rete as it stood before {@link #fire(MTRete)}
	 * was called on this node. Gets state from own {@link #reteState} if
	 * this is a chain node, else from {@link #parent}'s reteState.
	 * @param rete instance to load
	 * @return error message from failure; null if success
	 */
	public String loadPriorState(MTRete rete) {
		try {
			if (isChainNode())
				loadState(rete);
			else
				((RuleActivationNode) parent).loadState(rete);
			return null;
		} catch (Exception e) {
			String errMsg = "Error loading prior engine state: "+e;
			trace.err("Node "+getNodeId()+": "+errMsg);
			e.printStackTrace();
			return errMsg;
		}
	}

	/**
	 * Establish the rule engine state needed to {@link #fire(MTRete)} a child
	 * node.  Call this before calling {@link #fire(MTRete)} on any child node.
	 * @param rete engine to save or load
	 * @param siblingIndex index of this node:<ul>
	 *        <li>if siblingIndex is less than 1, no-op; must previously save the
	 *        Rete to {@link #reteState}: this provides a backtrack state for
	 *        siblings with higher indices;</li>
	 *        <li>else if siblingIndex is at least 1, will load the Rete
	 *        from the state saved in {@link #reteState}</li>
	 *        </ul>
	 */
	public void setUpState(MTRete rete, int siblingIndex) throws Exception {
		if (!isChainNode())
			throw new UnsupportedOperationException("setUpState() called on"+
					" on non-chain node "+this);
		if (siblingIndex < 1) 
			return; // was saveState(rete);
		else  
			loadState(rete);
	}
	
	
	/**
	 * Load the Rete from {@link #reteState}.
	 * @return true if the load completed without errors 
	 */
	public boolean loadState(MTRete rete) throws IOException, ClassNotFoundException {
		if (reteState == null)
			throw new IllegalStateException("load without prior save in node "+
					this);
		ByteArrayInputStream bais = new ByteArrayInputStream(reteState);
		rete.loadState(bais, routers);
		if (trace.getDebugCode("mt")) trace.out("mt", "loaded state in node "+getNodeId());
		return true;
	}
	
	
	
	/**
	 * Return a new {@link MTRete} instance with the state saved in
	 * {@link #reteState}.
	 * @return MTRete instance; null if {@link #reteState} is null or
	 *         an exception occurs
	 */
	MTRete getState() {
		if (reteState == null)
			return null;
		MTRete result = new MTRete();
		try {
			loadState(result);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * Save the Rete to {@link #reteState}, {@link #routers}.
	 * @param rete Rete to save
	 * @return true if success
	 */
	public boolean saveState(MTRete rete) {
	    try {
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        routers = rete.saveState(baos);
	        reteState = baos.toByteArray(); 
	        if (trace.getDebugCode("mt")) trace.out("mt", "saved state in node " + getNodeId() + "; length=" + reteState.length);
	        return true;
	    } catch( Exception e ) {
	    	trace.err("saveState() error in node "+this+": "+e);
	    	e.printStackTrace();
	    	return false;
	    }
	}
        
		
	private String firstFewString(byte[] array) {
            String str = "";
            int num = Math.min(100, array.length);
            for (int i = 0; i < num; i++) {
                str += array[i];
            }
            return str;
        }

	/**
	 * Copy the state saved by {@link #saveState(MTRete)} in the given node
	 * to this node. This is a shallow copy.
	 * @param node node whose state we want
	 */
	public void copyState(RuleActivationNode node) {
		reteState = node.reteState;
		routers = node.routers;
	}
	
  	/**
  	 * Return the rule name.
  	 * @return {@link #ruleName}
  	 */
	public String getName(){
		return ruleName;
	}
	
	/**
	 * Return the rule's display name, whatever that is.
	 * @return {@link #displayName}
	 */
	public String getDisplayName() {
	    return displayName;
	}
	
	/**
	 * @return Returns the errorOutput.
	 */
	TextOutput getErrorOutput() {
		return errorOutput;
	}

	/**
	 * @param errorOutput The errorOutput to set.
	 */
	void setErrorOutput(TextOutput errOutput) {
		this.errorOutput = errOutput;
	}

	/**
	 * Tell whether this node's rule models an error.
	 * @return result of {@link MTRete#isCorrectRuleName(String)} on {@link #ruleName}
	 */
	public boolean isCorrectRule() {
		return MTRete.isCorrectRuleName(ruleName);
	}
	
	/**
	 * Replace the list {@link #children} with nodes formed from active
	 * entries on the given agenda.
	 * @param agenda partial or complete List of activations from
	 *        {@link Rete#listActivations()}
	 * @param omitBuggyRules if true, will ignore activations of buggy rules 
	 * @return number of children added
	 * @throws JessException
	 */
	public int createChildren(List agenda, boolean omitBuggyRules) {
		children = new Vector();
		for (int i = 0, j = 0; i < agenda.size(); ++i) {
			Activation act = (Activation) agenda.get(i);
			if (act == null || act.isInactive())
				continue;
			if (omitBuggyRules && !MTRete.isCorrectRule(act.getRule()))
				continue;
                        
			RuleActivationNode child =
				new RuleActivationNode(j++, act, searchDepth+1, this);
		}
//                trace.out("gusIL", "children.size() = " + children.size());
		return children.size();
	}
	
	/**
	 * Access to {@link #children} List.
	 * @return {@link #children}
	 */
	public List getChildren() {
		return children;
	}
	
	/**
	 * Test the given selection, action and input against that predicted by
	 * the special tutor fact. Calls {@link #setReqSelection(String)},
	 * {@link #setReqAction(String)}, {@link #setReqInput(String)}.
	 * @param selection student-selected interface component
	 * @param action student action
	 * @param input student input
	 * @param isHint
	 * @param ctx Rete context
	 * @return 	<ul>
	 *          <li>{@link #MATCH} - selection action input matched;</li>
	 * 			<li>{@link #NO_MATCH} - selection action input differ from
	 *              that predicted by the rule and differ from default values</li>
	 * 			<li>{@link #NOT_SPEC} selection action input values are default:
	 *              that is, the rule does not test SAI</li>
	 *          </ul> 
	 */
	int isStudentSAIFound(String selection, String action, String input,
			String actualInputTestFunction, boolean isHint, Context ctx) {
		setReqSelection(selection);
		setReqAction(action);
		setReqInput(input);
		return isSAIFound(selection, action, input,
				actualSelection, actualAction, actualInput,
				actualInputTestFunction, isHint, ctx);
	}
	
	public int isStudentSAIFound(String selection, String action, String input, boolean isHint, Context ctx) {
		
		return isStudentSAIFound(selection, action, input, MTRete.NOT_SPECIFIED, isHint, ctx);
	}

	/**
	 * Attempt to match 2 given selection-action-input triples. 
	 * @param selection student-selected component
	 * @param action student action
	 * @param input student input value
	 * @param predictedSelection rule-predicted component
	 * @param predictedAction rule-predicted action
	 * @param predictedInput rule-predicted input
	 * @param predictedInputTestFunction input test function name
	 * @param isHint true if seeking a hint: can return {@value #SELECTION_FAILED}
	 * @param ctx Context for input test function
	 * @return {@value #NOT_SPEC}, {@value #SELECTION_FAILED},
	 *         {@value #MATCH} or {@value #NO_MATCH}
	 */
	int isSAIFound(String selection, String action, String input,
			String predictedSelection, String predictedAction,
			String predictedInput, String predictedInputTestFunction,
			boolean isHint, Context ctx) {

		if(trace.getDebugCode("rr"))
			trace.out("***isSAIFound() s " + selection + "=?" +
					  predictedSelection + ", a " + action + "=?" +
					  predictedAction + ", i " + input + "=?" +
					  predictedInput + "," + predictedInputTestFunction + ";"); //,"mt"
		final int S = 0x1;   // whether we've matched student's selection
		final int A = 0x2;   // whether we've matched student's action
		final int I = 0x4;   // whether we've matched student's input
		int result = 0;      // match results so far
		int m;
	
		
		m = testSingleValue(predictedSelection, selection, "selection",
				MTRete.NOT_SPECIFIED, isHint, ctx);
		if (m == NO_MATCH) {             // sewall 2013/11/01: special return for hint requests
			if(isHint)
				return (predictedAction.equals(MTRete.NOT_SPECIFIED) || predictedInput.equals(MTRete.NOT_SPECIFIED) ?
						SELECTION_FAILED : NO_MATCH);
			else
				return NO_MATCH;   // selection was required but failed to match
		} else if (m == MATCH) {
			if(isHint)
				return (predictedAction.equals(MTRete.NOT_SPECIFIED) || predictedInput.equals(MTRete.NOT_SPECIFIED) ?
						NOT_SPEC : MATCH);
			else
				result |= S;      // fall through to test action, input
		}
		m = testSingleValue(predictedAction, action, "action",
				MTRete.NOT_SPECIFIED, ctx);
		if (m == NO_MATCH)
			return NO_MATCH;      // action was required but failed to match
		else if (m == MATCH)
			result |= A;
		m = testSingleValue(predictedInput, input, "input",
				predictedInputTestFunction, ctx);
		if (m == NO_MATCH)
			return NO_MATCH;      // input was required but failed to match
		else if (m == MATCH)
			result |= I;
		if ((result & (S|A|I)) == (S|A|I))
			return MATCH;                   // all test results were MATCH
		else
			return NOT_SPEC;       // at least one of S/A/I is unspecified
	}

	/**
	 * Attempt to match 2 given selection values.
	 * @param studentSelection student-selected component
	 * @param predictedSelection rule-predicted component
	 * @param ctx for {@link #testSingleValue(String, String, String, String, Context)}
	 * @return {@link RuleActivationNode#NOT_SPEC},
	 *         {@link RuleActivationNode#MATCH} or 				
	 *         {@link RuleActivationNode#NO_MATCH}
	 */
	int isSelectionFound(String studentSelection, String predictedSelection,
			Context ctx) {
		if (trace.getDebugCode("mt")) trace.out("mt", "***isSelectionFound() s " + studentSelection + "=?" +
				  predictedSelection);
		return testSingleValue(predictedSelection, studentSelection,
				"selection", MTRete.NOT_SPECIFIED, ctx);
	}
	
	/**
	 * Test a single student selection, action or input value against the
	 * rule's specified value.
	 * @param rv rule-required value; can be NOT_SPEC
	 * @param sv student-entered value
	 * @param label tag for debugging msg
	 * @param rf optional input test function.
	 * @param ctx Context for calling test function 
	 * @return NO_MATCH if match failed, MATCH if succeeded, else NOT_SPEC  
	 */
	private int testSingleValue(String rv, String sv,
			String label, String rf, Context ctx) {
		return testSingleValue(rv, sv, label, rf, false, ctx);
	}
	
	/**
	 * Test a single student selection, action or input value against the
	 * rule's specified value.
	 * @param rv rule-required value; can be NOT_SPEC
	 * @param sv student-entered value
	 * @param label tag for debugging msg
	 * @param rf optional input test function.
	 * @param emptyMatchesAll if true, an empty student value matches any rule value
	 * @param ctx Context for calling test function 
	 * @return NO_MATCH if match failed, MATCH if succeeded, else NOT_SPEC  
	 */
	private int testSingleValue(String rv, String sv,
			String label, String rf, boolean emptyMatchesAll, Context ctx) {
		if (trace.getDebugCode("mt")) trace.out("mt", "Jess testSingleValue " + rv + " " + sv);
		int result = NO_MATCH;
		String[] errorMessage = new String[1];
		
		if (rv.equals(MTRete.NOT_SPECIFIED))
			result = NOT_SPEC;
		else if (rv.equals(MTRete.DONT_CARE))                         // all values MATCH
			result = MATCH;
		else if (emptyMatchesAll && (sv == null || sv.length() < 1))  // all values MATCH
			result = MATCH;
		else if(!MTRete.NOT_SPECIFIED.equals(rf)) {
			inputFunction = rf;
			String rfCall = String.format("(%s %s %s)", rf,
					Utils.escapeString(rv, true), Utils.escapeString(sv, true));
			result = evalBooleanExpr(rfCall, ctx, errorMessage);
			if(errorMessage[0] == null)
				inputFunctionResult = result;
			else
				inputFunctionResult = NOT_SPEC;
		} else {                                     // Jess (= ...) is default comparison fn
			String cmpCall = String.format("(= %s %s)",
					Utils.escapeString(rv, true), Utils.escapeString(sv, true));
			result = evalBooleanExpr(cmpCall, ctx, errorMessage);
		}
		if (trace.getDebugCode("mt")) trace.out("mt", "testSingleValue "+label+" returns "+
				matchIntToString(result) + "("+result+")");
		return result;
	}

	/**
	 * Execute the command and try to interpret the result as a boolean.
	 * @param cmd
	 * @param ctx for {@link Rete#eval(String, Context)}
	 * @param errorMessage fill in element[0] with any error message
	 * @return {@link #MATCH} if result is "TRUE", else {@link #NO_MATCH} 
	 */
	private int evalBooleanExpr(String cmd, Context ctx, String[] errorMessage) {
		int result;
		errorMessage[0] = null;  // good result
		try {			
			Value v =  ctx.getEngine().eval(cmd, ctx);
			if (v != null)
				v = v.resolveValue(ctx);
			if (trace.getDebugCode("mtt")) trace.out("mtt", "after eval: " + cmd + " = " + v);
			if (v != null && v.toString().equals("TRUE"))
				result = MATCH;
			else
				result = NO_MATCH;
		} catch (JessException je) {
			errorMessage[0] = "Error evaluating \""+cmd+"\":"+
					(je.getDetail() == null ? "" : je.getDetail()+". ")+
					(je.getData() == null ? "" : je.getData());
    		//trace.err(errorMessage[0]);
    		if (ctx.getEngine() instanceof MTRete) {
    			MTRete mtRete = (MTRete) ctx.getEngine();
    			TextOutput textOutput = mtRete.getTextOutput();
    			if (textOutput != null)
    				textOutput.append("\n"+errorMessage[0]+"\n");
    			EventLogger eventLogger = mtRete.getEventLogger();
    			if (eventLogger != null)
    				eventLogger.log(true, AuthorActionLog.JESS_CONSOLE, 
    					"RuleActivationNode.testSingleValue() function error",
    					cmd, errorMessage[0], "");
    		}
			result = NO_MATCH;
		}
		if(trace.getDebugCode("mt"))
			trace.out("mt", "RAN.evalBooleanExpr("+cmd+") => "+matchIntToString(result));
		return result;
	}

	/**
	 * Test the student's selection against that predicted by
	 * the special tutor fact.
	 * @param selection
	 * @param ctx for {@link #isSelectionFound(String, String, Context)}
	 * @return 	<ul>
	 *          <li>{@link #MATCH} - selection action input matched;</li>
	 * 			<li>{@link #NO_MATCH} - selection action input differ from
	 *              that predicted by the rule and differ from default values</li>
	 * 			<li>{@link #NOT_SPEC} selection action input values are default:
	 *              that is, the rule does not test SAI</li>
	 *          </ul> 
	 */
	public int isStudentSelectionFound(String selection, Context ctx){
		setReqSelection(selection);
		return isSelectionFound(selection, actualSelection, ctx);
	}

	/**
	 * Set {@link #reqSelection}, {@link #reqAction},
	 * {@link #reqInput} from the arguments. Call this method from a
	 * function that tests SAI directly from a rule. Also sets
	 * 
	 * @param studentSelection
	 * @param studentAction
	 * @param studentInput
	 */
	public void setStudentSAI(String studentSelection, String studentAction,
			String studentInput) {
		setReqSelection(studentSelection);
		setReqAction(studentAction);
		setReqInput(studentInput);
	}

	/**
	 * Set {@link #actualSelection}, {@link #actualAction},
	 * {@link #actualInput} from the arguments. Call this method from a
	 * function that tests SAI directly from a rule. Also sets
	 * 
	 * @param predictedSelection
	 * @param predictedAction
	 * @param predictedInput
	 */
	public synchronized void setRuleSAI(String predictedSelection,
			String predictedAction, String predictedInput, String predictedInputTestFunction) {
		setRuleSAI( predictedSelection, predictedAction,  predictedInput);
		setActualInputTestFunction(predictedInputTestFunction);

	}
	
	/**
	 * Set {@link #actualSelection}, {@link #actualAction},
	 * {@link #actualInput} from the arguments. Call this method from a
	 * function that tests SAI directly from a rule. Also sets
	 * 
	 * @param predictedSelection
	 * @param predictedAction
	 * @param predictedInput
	 */
	public synchronized void setRuleSAI(String predictedSelection,
			String predictedAction, String predictedInput) {
		if (trace.getDebugCode("mtt")) trace.out("mtt", "RAN.setRuleSAI() ruleSAIHasBeenSet "+ruleSAIHasBeenSet);
		if (ruleSAIHasBeenSet)
			return;	   
		trace.out("webAuth"," -----> Predicted SAI for rule " + this.getName() +" is " +  predictedSelection + " " + predictedInput);
	
		setActualSelection(predictedSelection);
		setActualAction(predictedAction);
		setActualInput(predictedInput);
		ruleSAIHasBeenSet = true;
	}
		
	/**
	 * Set {@link #actualSelection}, {@link #actualAction},
	 * {@link #actualInput} from the special-tutor-fact. Call this method
	 * after firing the nodes activation.
	 * @param rete rule engine instance to query
	 */
	synchronized void setRuleSAI(MTRete rete) {
            
		if (ruleSAIHasBeenSet){
			return;
		}
		setActualSelection(MTRete.NOT_SPECIFIED);
		setActualAction(MTRete.NOT_SPECIFIED);
		setActualInput(MTRete.NOT_SPECIFIED);
		ruleSAIHasBeenSet = true;
	}

	/**
	 * @return Returns the matchResult.
	 */
	public int getMatchResult() {
		return matchResult;
	}

	/**
	 * @param matchResult The matchResult to set.
	 */
	void setMatchResult(int matchResult) {
		if(trace.getDebugCode("hints"))
			trace.out("hints", "RAN.setMatchResult("+matchIntToString(matchResult)+"["+matchResult+"])");
	
		this.matchResult = matchResult;
	}

	public List fire(SimStRete ssRete) throws JessException {
	
		Activation act = null;
		Set priorActSet = new HashSet();
		List siblings = ((RuleActivationNode) getParent()).getChildren();
		List newAgenda = new ArrayList();
		List<Activation> debugAgenda = new ArrayList<Activation>();
		Iterator agenda = ssRete.listActivations();
		int ai = 0;
		while (agenda.hasNext()) {
	        Activation priorAct = (Activation) agenda.next();
	        debugAgenda.add(priorAct);
	        if (priorAct.isInactive())
	            continue;
	        if (actData.match(priorAct)) { // sewall 2008/10/29 was if (ai == agendaIndex)
	        	act = priorAct;
	        }
	        RuleActivationNode sibling =
	            (RuleActivationNode) (ai < siblings.size() ? siblings.get(ai) : null); 
	        if (trace.getDebugCode("mt")) trace.outNT("mt", "node.fire() at ai="+ai+" agenda, sibling:\n"+
	                priorAct.toString()+"\n"+
	                (sibling == null ? "null sib" : sibling.getActivation().toString()));
	        //if (rete.getPrunePriorActivations())
	        //    priorActSet.add(priorAct);   // leave set empty if not pruning 
	        ++ai;
	    }
	    if (ai <= agendaIndex) //ai is going to be the number of things in 'agenda'.
	        throw new ArrayIndexOutOfBoundsException("agendaIndex ("+agendaIndex+
	                ") beyond end of agenda (" + ai + "); ruleName "+ruleName);
	    if (act == null) { // was if (!ruleName.equals(act.getRule().getName()))
	    	String errMsg = "RuleActivationNode not found on agenda: "+this;
	    	ssRete.dumpAgenda("err", errMsg, true);
	    	throw new IllegalStateException(errMsg);
	    }
	    if (act.isInactive())
	        throw new IllegalStateException("activation inactive: ruleName "+
	                ruleName);
	    try {
	    	trace.out("webAuth"," -----> Now firing activation node " + this.getName());
	        ssRete.setActivationToFire(act);
	        ssRete.dumpAgenda("node.fire("+agendaIndex+"): agenda prior");
	        int runResult = ssRete.run(1);
	        //setRuleSAI(ssRete);
	        ssRete.setActivationToFire(null);
	        //getMessages(rete);
	        //ssRete.resetSAIFact();  // changes WM, so do before get agenda
	        ssRete.dumpAgenda("node.fire() runResult "+runResult+": agenda after");
	        for (agenda = ssRete.listActivations(); agenda.hasNext();) {//adds newly-appeared rules to newAgenda
	            Activation afterAct = (Activation) agenda.next();
	            if (!afterAct.isInactive() && !priorActSet.contains(afterAct))
	                newAgenda.add(afterAct);
	        }
	    } catch (JessException je) {
	        trace.err("Exception running agenda["+agendaIndex+"] "+ruleName+
	                ": "+je);
	        je.printStackTrace();
	        je.printStackTrace(ssRete.getTextOutput().getWriter());
	        je.printStackTrace(getErrorOutput().getWriter());
	    } catch (RuntimeException re) {
	        trace.err("Exception running agenda["+agendaIndex+"] "+ruleName+
	                ": "+re);
	        re.printStackTrace();
	        re.printStackTrace(getErrorOutput().getWriter());
	        throw re;
	    }
	    	
	    return newAgenda;			
		}
	
	/**
	 * Fire this node's activation. <b>The returned list assumes depth-first
	 * search.</b> That is, the list will include only that part of the agenda
	 * that preceded our own activation. Calls {@link #setRuleSAI(MTRete)}. 
	 * @param rete rule engine to use  
	 * @return List of newly-added {@link Activation}s
	 */
	public List fire(MTRete rete) throws JessException {
	    Activation act = null;
	    Set priorActSet = new HashSet();
	    List siblings = ((RuleActivationNode) getParent()).getChildren();
	    List newAgenda = new ArrayList();
	    List<Activation> debugAgenda = new ArrayList<Activation>();
	    Iterator agenda = rete.listActivations();
	    
	   
 	
	    int ai = 0;                         // agenda position
	    while (agenda.hasNext()) {
	        Activation priorAct = (Activation) agenda.next();
	        debugAgenda.add(priorAct);
	        if (priorAct.isInactive())
	            continue;
	        if (actData.match(priorAct)) // sewall 2008/10/29 was if (ai == agendaIndex)
	        	act = priorAct;

	        RuleActivationNode sibling =
	            (RuleActivationNode) (ai < siblings.size() ? siblings.get(ai) : null); 
	        if (trace.getDebugCode("mt")) trace.outNT("mt", "node.fire() at ai="+ai+" agenda, sibling:\n"+
	                priorAct.toString()+"\n"+
	                (sibling == null ? "null sib" : sibling.getActivation().toString()));
	        if (rete.getPrunePriorActivations())
	            priorActSet.add(priorAct);   // leave set empty if not pruning 
	        ++ai;
	    }
	    if (ai <= agendaIndex) //ai is going to be the number of things in 'agenda'.
	        throw new ArrayIndexOutOfBoundsException("agendaIndex ("+agendaIndex+
	                ") beyond end of agenda (" + ai + "); ruleName "+ruleName);
	    if (act == null) { // was if (!ruleName.equals(act.getRule().getName()))
	    	String errMsg = "RuleActivationNode not found on agenda: "+this;
	    	rete.dumpAgenda("err", errMsg, true);
	    	throw new IllegalStateException(errMsg);
	    }
	    if (act.isInactive())
	        throw new IllegalStateException("activation inactive: ruleName "+
	                ruleName);
	    try {
	        rete.setActivationToFire(act);
	        rete.dumpAgenda("node.fire("+agendaIndex+"): agenda prior");
	        int runResult = rete.run(1);
	        setRuleSAI(rete);
	        rete.setActivationToFire(null);
	        //getMessages(rete);
//	        rete.resetSAIFact();  // changes WM, so do before get agenda
	        rete.dumpAgenda("node.fire() runResult "+runResult+": agenda after");
	        for (agenda = rete.listActivations(); agenda.hasNext();) {//adds newly-appeared rules to newAgenda
	        	Activation afterAct = (Activation) agenda.next();
	            if (!afterAct.isInactive() && !priorActSet.contains(afterAct))
	                newAgenda.add(afterAct);
	        }
	    } catch (JessException je) {
	        trace.err("Exception running agenda["+agendaIndex+"] "+ruleName+
	                ": "+je);
	        je.printStackTrace();
	        je.printStackTrace(rete.getTextOutput().getWriter());
	        je.printStackTrace(getErrorOutput().getWriter());
	    } catch (RuntimeException re) {
	        trace.err("Exception running agenda["+agendaIndex+"] "+ruleName+
	                ": "+re);
	        re.printStackTrace();
	        re.printStackTrace(getErrorOutput().getWriter());
	        throw re;
	    }
	    return newAgenda;
	}
		
	
	
	public List fireOracle(JessOracleRete oracleRete) throws JessException {
	    Activation act = null;
	    Set priorActSet = new HashSet();
	    if (getParent()==null) return null;
	    List siblings = ((RuleActivationNode) getParent()).getChildren();
	    List newAgenda = new ArrayList();
	    List<Activation> debugAgenda = new ArrayList<Activation>();
	    Iterator agenda = oracleRete.listActivations();
	    
	    	
	    int ai = 0;                         // agenda position
	    while (agenda.hasNext()) {
	        Activation priorAct = (Activation) agenda.next();
	        debugAgenda.add(priorAct);
	        if (priorAct.isInactive())
	            continue;
	        if (actData.match(priorAct)) // sewall 2008/10/29 was if (ai == agendaIndex)
	        	act = priorAct;

	        RuleActivationNode sibling =
	            (RuleActivationNode) (ai < siblings.size() ? siblings.get(ai) : null); 
	        if (trace.getDebugCode("mt")) trace.outNT("mt", "node.fire() at ai="+ai+" agenda, sibling:\n"+
	                priorAct.toString()+"\n"+
	                (sibling == null ? "null sib" : sibling.getActivation().toString()));
	        if (oracleRete.getPrunePriorActivations())
	            priorActSet.add(priorAct);   // leave set empty if not pruning 
	        ++ai;
	    }
	    if (ai <= agendaIndex) //ai is going to be the number of things in 'agenda'.
	        throw new ArrayIndexOutOfBoundsException("agendaIndex ("+agendaIndex+
	                ") beyond end of agenda (" + ai + "); ruleName "+ruleName);
	    if (act == null) { // was if (!ruleName.equals(act.getRule().getName()))
	    	String errMsg = "RuleActivationNode not found on agenda: "+this;
	    	oracleRete.dumpAgenda("err", errMsg, true);
	    	throw new IllegalStateException(errMsg);
	    }
	    if (act.isInactive())
	        throw new IllegalStateException("activation inactive: ruleName "+
	                ruleName);
	    try {
	    	oracleRete.setActivationToFire(act);
	    	oracleRete.dumpAgenda("node.fire("+agendaIndex+"): agenda prior");
	        int runResult = oracleRete.run(1);
	        setRuleSAI(oracleRete);
	        oracleRete.setActivationToFire(null);
	        //getMessages(rete);
//	        rete.resetSAIFact();  // changes WM, so do before get agenda
	        oracleRete.dumpAgenda("node.fire() runResult "+runResult+": agenda after");
	        for (agenda = oracleRete.listActivations(); agenda.hasNext();) {//adds newly-appeared rules to newAgenda
	        	Activation afterAct = (Activation) agenda.next();
	            if (!afterAct.isInactive() && !priorActSet.contains(afterAct))
	                newAgenda.add(afterAct);
	        }
	    } catch (JessException je) {
	        trace.err("Exception running agenda["+agendaIndex+"] "+ruleName+
	                ": "+je);
	        je.printStackTrace();
	        je.printStackTrace(oracleRete.getTextOutput().getWriter());
	        je.printStackTrace(getErrorOutput().getWriter());
	    } catch (RuntimeException re) {
	        trace.err("Exception running agenda["+agendaIndex+"] "+ruleName+
	                ": "+re);
	        re.printStackTrace();
	        re.printStackTrace(getErrorOutput().getWriter());
	        throw re;
	    }
	    return newAgenda;
	}
	/**
	 * Append to the {@link #successMessages} from a vector passed from Jess.
	 * Side effect: Initializes {@link #successMessages} to empty list if null. 
	 * @param msgVV list of success or buggy messages generated by rule
	 * @param ctx veriable context from rule's RHS
	 */
	void setSuccessMessages(ValueVector msgVV, Context ctx)
			throws JessException {
	    if (successMessages == null)
	    	successMessages = new Vector();
		setMessages(successMessages, msgVV, ctx);
	}
	
	/**
	 * Append to the {@link #buggyMessages} from a vector passed from Jess.
	 * Side effect: Initializes {@link #buggyMessages} to empty list if null. 
	 * @param msgVV list of buggy or buggy messages generated by rule
	 * @param ctx veriable context from rule's RHS
	 */
	void setBuggyMessages(ValueVector msgVV, Context ctx)
	

	
			throws JessException {
	    if (buggyMessages == null)
	    	buggyMessages = new Vector();
		setMessages(buggyMessages, msgVV, ctx);
	}
	
	/**
	 * Append to the {@link #hintMessages} from a vector passed from Jess.
	 * Side effect: Initializes {@link #hintMessages} to empty list if null. 
	 * @param msgVV list of hint or buggy messages generated by rule
	 * @param ctx veriable context from rule's RHS
	 */
	public void setHintMessages(ValueVector msgVV, Context ctx)
			throws JessException {
	    if (hintMessages == null)
	    	hintMessages = new Vector();
		setMessages(hintMessages, msgVV, ctx);
	}
	
	/**
	 * Append to the given list of messages the messages from Jess.
	 * @param tgtMsgList message list to which to add
	 * @param msgVV list of hint or buggy messages generated by rule
	 * @param ctx veriable context from rule's RHS
	 */
	private void setMessages(List<String> tgtMsgList, ValueVector msgVV, Context ctx)
			throws JessException {
	    for (int i = 0; i < msgVV.size(); ++i) {
	    	Value msgV = msgVV.get(i);
	    	Value resolvedV = msgV.resolveValue(ctx);
	    	String msg = resolvedV.stringValue(ctx);
	    	if (trace.getDebugCode("mt")) trace.out("mt", "setMessages["+i+"]: \""+msg+"\"");
	    	if (msg != null)
	    		msg = msg.trim();
	    	if (msg.length() > 0)
	    		tgtMsgList.add(msg);
	    	
	    		
	    }
	}
	
	/**
	 * 
	 * @param msgVV
	 * @param ctx
	 * @throws JessException
	 */
	public void setCLHintMessages(ValueVector msgVV, Context ctx) throws 
		JessException {
		if(clHintMessages == null)
			clHintMessages = new Vector();
		setMessages(clHintMessages, msgVV, ctx);
	}
	
	/**
	 * Extract the messages from the given slot and append them to the given list.
	 * @param  fact Fact to read
	 * @param  slotName name of slot in fact holding messages
	 * @param  msgList list of messages to append to
	 * @return count of messages extracted
	 */
	private int extractMessages(MTRete rete, Fact fact, String slotName,
			List msgList) {
	    int count = 0;
		try {
			Value value = fact.getSlotValue(slotName);
			ValueVector vv = value.listValue(null);
			for (count = 0; count < vv.size(); count++){
				String msg = vv.get(count).stringValue(null).trim();
				if (msgList != null && msg.length() > 0){
					if (trace.getDebugCode("mt")) trace.out("mt", "extractMessages() adding msg["+
							msgList.size()+"]="+msg);
					msgList.add(msg);
				}
			}
		}catch (Exception e) {
		    e.printStackTrace();
			rete.getTextOutput().append("\n" + e.toString());
			if(MTRete.breakOnExceptions){
				MTRete.stopModelTracing = true;
			}
		}
	    return count;
	}
	
	/**
	 * Create a list of the ruleNames in the chain of activations from the
	 * root of the search tree through this node. Uses recursion and call
	 * stack to get the list in root-to-leaf order and to maintain
	 * parent-to-child linkage.
	 * @param nodeSeq List to set; will clear() before adding; if null,
	 *        creates a List
	 * @return list of nodes in this chain, skipping chain nodes, starting at root
	 */
	public ArrayList<RuleActivationNode> getNodeSequence(ArrayList<RuleActivationNode> nodeSeq) {
		if(trace.getDebugCode("mtt"))
			trace.out("mtt", "RAN.getNodeSequence() this "+getNodeId()+", isChainNode "+isChainNode()+
					", parent "+(parent instanceof RuleActivationNode ? ((RuleActivationNode) parent).getNodeId() : null));
		if (parent != null)                         // get result from parent
			nodeSeq = ((RuleActivationNode) parent).getNodeSequence(nodeSeq);
		else {                                      // root creates the result
			if (nodeSeq == null)
				nodeSeq = new ArrayList<RuleActivationNode>();
			else
				nodeSeq.clear();
		}
		if (!isChainNode())                         // skip chain nodes
			nodeSeq.add(this);
		return nodeSeq;
	}

	/**
	 * Translate an internal selection or action or input value to an external one.
	 * @param  saiElementValue selection or action to translate
	 * @return translation for DONT-CARE, etc., or actual content
	 */
	private static String translate(String saiElementValue) {
		if (saiElementValue == null)
			return "Not Specified";
		if (saiElementValue.equals(MTRete.NOT_SPECIFIED))
			return "Not Specified";
		else if (saiElementValue.equals(MTRete.DONT_CARE))
			return "Don't Care";
		else
			return saiElementValue;
	}
	
	/**
	 * @return {@link #searchDepth}
	 */
	public int getSearchDepth() {
		return searchDepth;
	}

	/**
	 * @param activation
	 */
	public void setActivation(Activation activation) {
		this.activation = activation;
	}

	/**
	 * @return
	 */
	public String getActualAction() {
		return actualAction;
	}

	/**
	 * @param actualAction
	 */
	private void setActualAction(String actualAction) {
		if (!this.actualAction.equals(actualAction))
			redoHtmlText = true;
		if (actualAction == null)
			this.actualAction = MTRete.NOT_SPECIFIED;
		else 
			this.actualAction = actualAction;
	}

	/**
	 * @return
	 */
	public String getActualInput() {
		return actualInput;
	}

	/**
	 * @param actualInput
	 * actualInput has the value produced by the production rule.
         * c.f.: reqInput has the value input by the student (read by
         *  tutor interface)
         */ 
	private void setActualInput(String actualInput) {
	    if (!this.actualInput.equals(actualInput))
	        redoHtmlText = true;
	    if (actualInput == null)
	        this.actualInput = MTRete.NOT_SPECIFIED;
	    else 
	        this.actualInput = actualInput;
	}

	/**
	 * @return
	 */
	public String getActualInputTestFunction() {
		return actualInputTestFunction;
	}

	/**
	 * @param actualInputTestFunction
	 */
	private void setActualInputTestFunction(String actualInputTestFunction) {
		if (!this.actualInputTestFunction.equals(actualInputTestFunction))
			redoHtmlText = true;
		if (actualInputTestFunction == null)
			this.actualInputTestFunction = MTRete.NOT_SPECIFIED;
		else 
			this.actualInputTestFunction = actualInputTestFunction;
	}
	
	
	/**
	 * @return Returns the buggyMessages.
	 */
	public Vector getBuggyMessages() {
		return buggyMessages;
	}

	/**
	 * @return the {@link #successMessages}
	 */
	Vector getSuccessMessages() {
		return successMessages;
	}

	/**
	 * @return Returns the hintMessages.
	 */
	public Vector getHintMessages() {
		return hintMessages;
	}
	
	public Vector getCLHintMessages() {
		return clHintMessages;
	}
	
	/**
	 * @return
	 */
	public String getActualSelection() {
		return actualSelection;
	}

	/**
	 * @param actualSelection
	 */
	public void setActualSelection(String actualSelection) {
		if (!this.actualSelection.equals(actualSelection))
			redoHtmlText = true;
		if (actualSelection == null)
			this.actualSelection = MTRete.NOT_SPECIFIED;
		else 
			this.actualSelection = actualSelection;
	}

	/**
	 * @param reqAction
	 */
	public void setReqAction(String reqAction) {
		if (!this.reqAction.equals(reqAction))
			redoHtmlText = true;
		if (reqAction == null)
			this.reqAction = MTRete.NOT_SPECIFIED;
		else 
			this.reqAction = reqAction;
	}

	/**
	 * @param reqInput
	 */
	public void setReqInput(String reqInput) {
		if (!this.reqInput.equals(reqInput))
			redoHtmlText = true;
		if (reqInput == null)
			this.reqInput = MTRete.NOT_SPECIFIED;
		else 
			this.reqInput = reqInput;
	}

	/**
	 * @param reqSelection
	 */
	public void setReqSelection(String reqSelection) {
		if (!this.reqSelection.equals(reqSelection))
			redoHtmlText = true;
		if (reqSelection == null)
			this.reqSelection = MTRete.NOT_SPECIFIED;
		else 
			this.reqSelection = reqSelection;
	}

	/**
	 * @return
	 */
	public String getReqAction() {
		return reqAction;
	}

	/**
	 * @return
	 */
	public String getReqInput() {
		return reqInput;
	}

	/**
	 * @return
	 */
	public String getReqSelection() {
		return reqSelection;
	}

	/** Selection failed to match on a hint request. */
	public static final int SELECTION_FAILED = 5;
	/** Match result not yet calculated.  */
	public static final int RESULT_UNSET = 4;
	/** Match result indicating rule does not test this value. */
	public static final int NOT_SPEC = 3;
	/** Match result indicating rule-specified and student values don't match. */ 
	public static final int NO_MATCH = 2;
	/** Match result indicating rule-specified and student values match. */ 
	public static final int MATCH = 1;
	/** Match result indicating rule matches all values. */ 
	public static final int ANY_MATCH = 0;

	/**	For dumping out match results. @see matchIntToString(int). */
	private static final String[] matchStrings = {
		"ANY_MATCH",       // 0
		"MATCH",           // 1
		"NO_MATCH",        // 2
		"NOT_SPEC",        // 3
		"UNSET",           // 4
		"SELECTION_FAILED" // 5
	};

	/** Name for chain nodes. */
	static final String CHAIN = "Chain";
	
	/** Result from function testing match for node now firing. */
	private int matchResult = RuleActivationNode.RESULT_UNSET;
	
	/**
	 * Whether values for the fields {@link #actualSelection},
	 * {@link #actualAction}, {@link #actualInput} have yet been determined.
	 */
	private boolean ruleSAIHasBeenSet = false;

	/** Name of the input test function. Null if none. */
	private String inputFunction = null;

	/** Result from {@link #inputFunction} test. */
	private int inputFunctionResult = NOT_SPEC;
	
	/**
	 * For dumping out match results.
	 * @param matchInt one of the match results {@link #MATCH}, etc.
	 * @return string representation
	 */
	protected static String matchIntToString(int matchInt) {
		if (0 <= matchInt && matchInt < matchStrings.length)
			return matchStrings[matchInt];
		else
			return "(undefined match int "+matchInt+")";
	}

	public int selectionMatches(){
		if (matcher != null)
			return testMatcherSelectionInternal();
		if(!this.actualSelection.equals(MTRete.NOT_SPECIFIED)){
			if(this.actualSelection.equals(this.reqSelection)){
				return MATCH;
			}else if(this.actualSelection.equals(MTRete.DONT_CARE)){
				return ANY_MATCH;
			}else{
				return NO_MATCH;
			}
		}
		return NOT_SPEC;
	}

	public int actionMatches(){
		if (matcher != null)
			return testMatcherActionInternal();
		if(!this.actualAction.equals(MTRete.NOT_SPECIFIED)){
			if(this.actualAction.equals(this.reqAction)){
				return MATCH;
			}else if(this.actualAction.equals(MTRete.DONT_CARE)){
				return ANY_MATCH;
			}else{
				return NO_MATCH;
			}
		}
		return NOT_SPEC;
	}

	public int inputMatches(){
		if (matcher != null)
			return testMatcherInputInternal();
		if (inputFunction != null)
			return inputFunctionResult ;
		if(!this.actualInput.equals(MTRete.NOT_SPECIFIED)){
			if(this.actualInput.equals(this.reqInput)){
				return MATCH;
			}else if(this.actualInput.equals(MTRete.DONT_CARE)){
				return ANY_MATCH;
			}else{
				return NO_MATCH;
			}
		}
		return NOT_SPEC;
	}
	
	/**
	 * 	This method formats the tooltip text for the associated node
	 * 	@param node: associated node
	 *	@return: textString for the tooltip
	 */
	public String getNodeToolTipText() {

		if (!this.redoHtmlText)
			return htmlText;
		
		if (actualSelection.equals(MTRete.NOT_SPECIFIED)
				&& actualAction.equals(MTRete.NOT_SPECIFIED)
				&& actualInput.equals(MTRete.NOT_SPECIFIED)) {
			redoHtmlText = false;
			return "";
		}
		
		String pSelection = translate(getActualSelection());
		String pAction = translate(getActualAction());
		String pInput = translate(getActualInput());
		
		String sSelection = translate(getReqSelection());
		String sAction = translate(getReqAction());
		String sInput = translate(getReqInput());
		
		StringBuffer result = new StringBuffer("<html>");
		result.append("<table border=\"1\" bgcolor=\"white\">");
		result.append("<caption><b>Rule's Predicted vs. Student's Actual Values</b></caption>");
		
		result.append("<tr><th>&nbsp;</th><th>Selection (S)</th><th>Action (A)</th><th>Input (I)</th></tr>");
		
		// format predicted
		result.append("<tr><th width=\"100\">Rule</th>");
		
		// pSelection
		result.append("<td align=\"center\" width=\"100\"");
		
		// set pSelection back ground color
		int selectionMatch = selectionMatches();
		result.append(getBackgroundColorText(selectionMatch) + ">");
		
		// pSelection text
		result.append(pSelection);
		result.append("</td>");
		
		// pAction
		result.append("<td align=\"center\" width=\"100\"");
		
		// set pAction back ground color
		int actionMatch = actionMatches();
		result.append(getBackgroundColorText(actionMatch) + ">");
		
		// pAction text
		result.append(pAction);
		result.append("</td>");
		
		// pInput
		result.append("<td align=\"center\" width=\"100\"");
		
		// set pInput back ground color
		int inputMatch = inputMatches();
		result.append(getBackgroundColorText(inputMatch) + ">");
		
		// pInput text
		result.append(pInput);
		result.append("</td>");
		result.append("</tr>");
		
		// format student
		result.append("<tr><th width=\"100\">Student</th>");
				
		// sSelection
		result.append("<td align=\"center\" width=\"100\"");
		
		// set sSelection back ground color
		result.append(getBackgroundColorText(selectionMatch) + ">");
		
		// pSelection text
		result.append(sSelection);
		result.append("</td>");
		
		// sAction
		result.append("<td align=\"center\" width=\"100\"");
		
		// set sAction back ground color
		result.append(getBackgroundColorText(actionMatch) + ">");
		
		// sAction text
		result.append(sAction);
		result.append("</td>");
		
		// sInput
		result.append("<td align=\"center\" width=\"100\"");
		
		// set sInput back ground color	
		result.append(getBackgroundColorText(inputMatch) + ">");
		
		// pInput text
		result.append(sInput);
		result.append("</td>");
		result.append("</tr>");
	
		result.append("</table></html>");
		
		htmlText = result.toString();
		redoHtmlText = false;
		return htmlText;
	}
	
	/**
	 * 	This method formats the cell's background 
	 * 	color text based on the matchtype.
	 * 	@param matchType: takes values: MATCH, ANY_MATCH, NO_MATCH and NOT_SPEC
	 *	@return: textString for the color
	 */
	
	static private String getBackgroundColorText (int matchType) {
		if (matchType == MATCH || matchType == ANY_MATCH)
			// green
			return " bgcolor=\"rgb(85,255,85)\""; // "bgcolor=\"rgb(0,255,0)\"";
		else if (matchType == NO_MATCH)
			// red
			return " bgcolor=\"rgb(255,85,85)\""; // "bgcolor=\"rgb(255,0,0)\"";
		else if (matchType == NOT_SPEC)
			// white
			return " bgcolor=\"white\""; // "bgcolor=\"rgb(255,255,255)\"";
		else // no color setting
			return "";
	}

	/**
	 * @return Returns the chainNode.
	 */
	public boolean isChainNode() {
		return chainNode;
	}

	/**
	 * @return the {@link #matcher}
	 */
	public Matcher getMatcher() {
		return matcher;
	}

	/**
	 * @param matcher new value for {@link #matcher}
	 */
	void setMatcher(Matcher matcher) {
		this.matcher = matcher;
	}

	/**
	 * Ask the matcher to test the student's selection individually.
	 * @return {@link #MATCH}, {@link #NO_MATCH}, {@link #NOT_SPEC}
	 */
	public int testMatcherSelection() {
		int result = testMatcherParameterByIndex(matcher.getSelectionIndex(), getReqSelection());
		if (ANY_MATCH == result)
			return MATCH;
		else
			return result;
	}

	/**
	 * Ask the matcher to test the student's selection individually.
	 * @return {@link #MATCH}, {@link #NO_MATCH}, {@link #NOT_SPEC}
	 */
	private int testMatcherSelectionInternal() {
		return testMatcherParameterByIndex(matcher.getSelectionIndex(), getReqSelection());
	}

	/**
	 * Ask the matcher to test the student's action individually.
	 * @return {@link #MATCH}, {@link #NO_MATCH}, {@link #NOT_SPEC}
	 */
	private int testMatcherActionInternal() {
		return testMatcherParameterByIndex(matcher.getActionIndex(), getReqAction());
	}

	/**
	 * Ask the matcher to test the student's input individually.
	 * @return {@link #MATCH}, {@link #NO_MATCH}, {@link #NOT_SPEC}
	 */
	private int testMatcherInputInternal() {
		return testMatcherParameterByIndex(matcher.getInputIndex(), getReqInput());
	}

	/**
	 * Ask the matcher to test an individual selection, action or input
	 * parameter, using the parameter index,
	 * as in {@link Matcher#setParameterByIndex(String, int)}.
	 * @param i parameter index
	 * @param studentValue student selection, action or input to test
	 * @return {@link #MATCH}, {@link #NO_MATCH}, {@link #NOT_SPEC}
	 */
	private int testMatcherParameterByIndex(int i, String studentValue) {
		int result;
		if (matcher.isParamNotSpecified(i))
			result = NOT_SPEC;
		else if (matcher.isParamDontCare(i))
			result = ANY_MATCH;
		else {
			Vector v = new Vector();
			v.add(studentValue);  // student's selection, action or input
			if (matcher.match(i, v))
				result = MATCH;
			else
				result = NO_MATCH;
		}
		if (trace.getDebugCode("mtt")) trace.out("mtt", "testMatcherParameterByIndex("+i+","+
				studentValue+") returns "+matchIntToString(result));
		return result;
	}

	/**
	 * Return a 
	 * @param isHint whether we're in a 
	 * @return
	 */
	public int testMatcherSAI(boolean isHint) {
		final int S = 0x1;   // whether we've matched student's selection
		final int A = 0x2;   // whether we've matched student's action
		final int I = 0x4;   // whether we've matched student's input
		int result = 0;      // match results so far
		int mv;

		mv = testMatcherSelectionInternal();
		if (mv == NO_MATCH) {
			return NO_MATCH;      // selection was required but failed to match
//			if(isHint)
//				return (predictedAction.equals(MTRete.NOT_SPECIFIED) || predictedInput.equals(MTRete.NOT_SPECIFIED) ?
//						SELECTION_FAILED : NO_MATCH);
//			else
//				return NO_MATCH;   // selection was required but failed to match
		} else if (mv == MATCH || mv == ANY_MATCH)
			result |= S;
		mv = testMatcherActionInternal();
		if (mv == NO_MATCH)
			return NO_MATCH;      // action was required but failed to match
		else if (mv == MATCH || mv == ANY_MATCH)
			result |= A;
		mv = testMatcherInputInternal();
		if (mv == NO_MATCH)
			return NO_MATCH;      // input was required but failed to match
		else if (mv == MATCH || mv == ANY_MATCH)
			result |= I;
		if ((result & (S|A|I)) == (S|A|I))
			return MATCH;                   // all test results were MATCH
		else
			return NOT_SPEC;       // at least one of S/A/I is unspecified
	}

	/**
	 * Extract skill names from defrule descriptions. Syntax for rule author
	 * 
	 *   "... end of the description. [Skills: skill1 [category1] [; skill2 [category2] ...]
     * or
     *   "... end of the description. [Skills: skill1 [category1] [; ~ category2 ...]
     *
     * A new Jess function will let model developers set the default skill category:
     *            (set-default-skill-category "string")
     *  <ul>
     *     <li>if left unset, the default will be a rule's defmodule name;</li>
     *     <li>the default module name is MAIN</li>
     *  </ul>
     *  To extract meta-skills from defrule descriptions<ol>
     *  <li>scan for the last occurrence the label "skills: " (case-insensitive)</li>
     *   <li><li>if none, the skill name and category will match the rule name and category</li></li>
     *  <li>split any substring after the label by semi-colons into into skill elements</li>
     *  <li>in the individual skill elements, consider any substring before the first embedded space to be the skill name, any after to be the skill category
     *  <li>if the skill name is a tilde ("~"), copy it from the previous skill name</li>
     *  </ol
     * @param rete engine to look up rule
     * @param defaultCategory if not null, use this where a skill category is missing;
     *        if null, default is module name 
   	 * @return string described above
	 */
	public List<String> getSkillNames(Rete rete, String defaultCategory) {
		List<String> result = new ArrayList<String>();
		HasLHS rule = rete.findDefrule(ruleName);
		if(rule == null)
			return result;
		String docStr = rule.getDocstring();
		if(docStr == null || docStr.length() < 1)
			return result;
		int tag = docStr.toLowerCase().lastIndexOf("skills:");
		if(tag < 0)
			return result;
		String previousCategory = null;
		String[] skills = docStr.substring(tag+7).split(";");  // +7 is length of "skills:"
		for(String skill : skills) {
			skill = skill.trim();
			String[] nc = skill.split(" ");
			int i = 0;
			if(nc[i] == null || nc[i].length() < 1)
				continue;
			String name = nc[i], category = null;
			while(++i < nc.length && category == null) {
				if(nc[i] == null || nc[i].length() < 1)
					continue;
				category = nc[i];
			}
			if("~".equals(category))
				category = previousCategory;
			if(category == null)
				category = (defaultCategory != null ? defaultCategory : rule.getModule());
			previousCategory = category;
				
			result.add(name+' '+category);
		}
		return result;
	}
}
