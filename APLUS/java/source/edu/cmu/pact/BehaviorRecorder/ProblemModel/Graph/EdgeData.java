package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.jdom.Element;

import pact.CommWidgets.JCommButton;
import pact.CommWidgets.JCommRadioButton;
import pact.CommWidgets.JCommWidget;
import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.hcii.ctat.CTATRandom;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageBuilder;
import edu.cmu.pact.BehaviorRecorder.Controller.RuleLabelHandler;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExactMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.View.ActionLabel;
import edu.cmu.pact.BehaviorRecorder.View.CheckLispLabel;
import edu.cmu.pact.BehaviorRecorder.View.RuleLabel;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.model.Skill;

// used as edge attribute in ESDigraph

/**
 * EdgeData Class.
 * The Ede Data class represents all of the essential Link data items.  
 * Each link in the problem Graph has a single EdgeData attribute that
 * will represent hints and other features.
 *
 * TODO:: The Class itself is a model class.  Yet it contains a number
 * of display elements such as calls ro make the ActionLabel string 
 * as well as calls such as displayTraversalCount which govern display
 * information and probably should not be here.
 */
public class EdgeData implements Serializable {

	private static final long serialVersionUID = 7752570842488326993L;

	private ActionLabel actionLabel;

    private CheckLispLabel preLispCheckLabel;

    private ProblemEdge edge;

    private boolean updatedInSkillometer = false;

    private int traversalCount;


    /** Minimum number of times this link may be traced. Value 0 means traversal is optional. */
    private int minTraversals = 1;

    /** For storing variables in {@link #minTraversals}. */
    private String minTraversalsStr = Integer.toString(minTraversals);

    /** Maximum number of times this link may be traced. Value 0 prevents traversal. */
    private int maxTraversals = 1;

    /** For storing variables in {@link #maxTraversals}. */
    private String maxTraversalsStr = Integer.toString(maxTraversals);

    // for the Dialogue System
    private String dialogueName = "";

    private boolean step_Succesful_Completion = false;

    private boolean step_Student_Error = false;
	
	private DialogueSystemInfo dialogueSystemInfo;
	
	/** Graphical labels for rules or skills associated with this edge. */
    private Vector<RuleLabel> ruleLabels = null;

	/** List of associated rules or skills. Like {@link #ruleLabels}, but used outside of author's UI. */
	private Vector<String> ruleNames;

    private ProblemModel problemModel;

    private MessageObject demoMsgObj;

    private String actionType = CORRECT_ACTION;

    private String oldActionType;

    private String buggyMsg = "";

    private String successMsg;

    private Vector<String> interpolatedHints, hints = new Vector<String>();
    private String interpolateSelection, interpolateAction, interpolateInput;

    private boolean isPreferredEdge;
    
    private String callbackFn = "";

    private Object studentInput;

    private Object studentAction;

    private Object studentSelection;
    
    private String actor = "Student";

    protected Matcher matcher;

    // These ArrayLists store the elements name and the values that are
    // associated with this action
    private Vector associatedElements = new Vector();

    private Vector associatedElementsValues = new Vector();

    private String checkedStatus;

    private int localUniqueID = -1;  // initializer matches old ActionLabel.getUniqueID()
    
    /** WARNING. Changing the following action type names might 
     * have dangerous side-effects on sim student. Talk to 
     * Noboru before changing.  11/24/09
     */
    public final static String CORRECT_ACTION = "Correct Action";

    public final static String CLT_ERROR_ACTION = "Error Action";
    
    public final static String UNTRACEABLE_ERROR = "Untraceable Error";

    public final static String BUGGY_ACTION = "Buggy Action";

    public final static String FIREABLE_BUGGY_ACTION = "Fireable Buggy Action";
    
    private static EdgeData _copyData;
    
    /**
     * END WARNING.
     */
    // Fri Dec 29 21:39:06 EST 2006 :: Noboru
    // Added for SimSt for students' log analysis and bootstrapping
    // "Hint Action" is for log analyis -- human students' hint seeking is coded
    // as "Hint Action" and embedded into a BRD
    // "Given Action" is for bootstrapping for Stoichiometry (ad-hoc) -- the first
    // two steps, which are to set the unit and substance for the last column, is
    // coded as "Given Action" so that SimSt can skip these steps from learning.
    // 
    public final static String HINT_ACTION = "Hint Action";
    public final static String GIVEN_ACTION = "Given Action";

    public final static String SKIP_ACTION = "Skip Action";
    
    public final static String UNKNOWN_ACTION = "Unknown Action";

    public final static String NEVER_CHECKED = "Never Checked";

    public final static String FIREABLE_BUG = "FIREABLE-BUG";

    public final static String SUCCESS = "SUCCESS";

    public final static String NO_MODEL = "NO-MODEL";

    public final static String NOTAPPLICABLE = "No-Applicable";

    public final static String BUGGY = "BUG";

    public final static String TABLE_FRONT = "Please enter " + "'";

    public final static String TABLE_REAR = "'" + " in the highlighted cell.";

    public final static String COMBOBOX_FRONT = "Please select " + "'";

    public final static String COMBOBOX_REAR = "'"
            + " from the highlighted menu.";

    public final static String CHOOSER_FRONT = "Please select " + "'";

    public final static String CHOOSER_REAR = "'"
            + " from the left one of the two highlighted lists. Then click on '>>' to move the value over to the list on the right.";

    public final static String LIST_FRONT = "Please select " + "'";

    public final static String LIST_REAR = "'" + " from the highlighted list.";

    public final static String TEXTFIELD_FRONT = "Please enter " + "'";

    public final static String TEXTFIELD_REAR = "'"
            + " in the highlighted field.";

    public final static String TEXTAREA_FRONT = "Please enter " + "'";

    public final static String TEXTAREA_REAR = "'"
            + " in the highlighted field.";

    public final static String BUTTON_DEFAULT = "Please click on the highlighted button.";

    public final static String BUTTON_FRONT = "Please click on the highlighted button "
            + "('";

    public final static String BUTTON_REAR = "').";

    public final static String DONE_BUTTON_FRONT = "You have answered all the questions. Please click on the ";

    public final static String DONE_BUTTON_REAR = " button to proceed to the next problem.";

    public final static String RADIOBUTTON_DEFAULT = "Please choose among the highlighted radio buttons.";

    public final static String RADIOBUTTON_FRONT = "Please select " + "'";

    public final static String RADIOBUTTON_REAR = "'"
            + " from the highlighted items.";

    public final static String COMPOSER_FRONT = "Please use the highlighted menus to input "
            + "'";

    public final static String COMPOSER_REAR = "' - "
            + " click on 'Add' once you have made the right menu selections.";

    public final static String DEFAULT_HINT_FRONT = "Please provide your answer "
            + "'";

    public final static String DEFAULT_HINT_REAR = "'"
            + " in the highlighted element.";

    /** Plain color for ordinary actions. */
	public static final Color PlainColor = Color.black;

    /** Distinctive color for tutor-performed actions. */
	public static final Color TPAColor = new Color(41,152,255);

    /**
     * Original hint message numbers, in the order actually presented.
     * If null, the order is 1, 2, ... nHints. See {@link #randomizeHintOrder()}.
     */
	private int[] modifiedHintOrder = null;

    /**
     * Enable a calling routine to set transient fields after deserializing.
     * @param controller value for {@link #controller}
     */
    public void restoreTransients(BR_Controller controller) {
    	if (actionLabel != null)
    		actionLabel.restoreTransients(controller);
    	if (preLispCheckLabel != null)
    		preLispCheckLabel.restoreTransients(controller);
    	if (dialogueSystemInfo != null)
    		dialogueSystemInfo.restoreTransients(controller);
    	if (ruleLabels != null) {
    		for (RuleLabel rl : ruleLabels)
    			rl.restoreTransients(controller);
    	}
    }
    
    /**
     * Create an edge for model tracing.
     * @param problemModel
     */
    public EdgeData() {}

    public EdgeData(ProblemModel problemModel) {

		this.problemModel = problemModel;
		setUniqueID(problemModel.getNextEdgeUniqueIDGenerator());
		// create an unnamed actionType label
		if(!Utils.isRuntime())
			setActionLabel(new ActionLabel(this, problemModel));
		
		BR_Controller ctlr = problemModel.getController();
		if (ctlr != null && !Utils.isRuntime()) {
		    setPreLispCheckLabel(new CheckLispLabel(this, ctlr));
		}
	        setCheckedStatus(NEVER_CHECKED);
		
	        // default now
	        setActionType (EdgeData.CORRECT_ACTION);
	        setOldActionType (EdgeData.CORRECT_ACTION);
	
	        setBuggyMsg ("No, this is not correct.");
	        setSuccessMsg ("");
	        setHints(new Vector());
	        setPreferredEdge (false);
	
	        // the default matcher
	        setMatcher (new ExactMatcher());
			
			// set the dialogueSystem
	        if (ctlr != null)
	        	dialogueSystemInfo = new DialogueSystemInfo(ctlr);

    }

    /**
     * @return {@link #problemModel}
     */
    public ProblemModel getProblemModel() {
    	return problemModel;
    }
    
    // zz add for the Dialogue System feature
    public DialogueSystemInfo getDialogueSystemInfo() {
	return dialogueSystemInfo;
    }
    
    public EdgeData cloneEdgeData() {
        return cloneEdgeData(problemModel);
    }
    
    public EdgeData cloneEdgeData(ProblemModel problemModel) {
        EdgeData newEdgeData = new EdgeData(problemModel);

        newEdgeData.setActor(getActor());
        newEdgeData.setSelection(copyVector(getSelection()));
        newEdgeData.setAction(copyVector(getAction()));
        newEdgeData.setInput(copyVector(getInput()));
        newEdgeData.setDemoMsgObj(getDemoMsgObj().copy());
        newEdgeData.setActionType(getActionType());
        newEdgeData.setHints(copyVector(getAllHints()));
        newEdgeData.setBuggyMsg(getBuggyMsg());
        newEdgeData.setSuccessMsg(getSuccessMsg());
        newEdgeData.setPreferredEdge(isPreferredEdge());
        newEdgeData.setOldActionType(getOldActionType());
        newEdgeData.setMatcher((Matcher) getMatcher().clone());
        newEdgeData.setMinTraversalsStr(getMinTraversalsStr());
        newEdgeData.setMaxTraversalsStr(getMaxTraversalsStr());

        newEdgeData.getPreLispCheckLabel().resetCheckedStatus(
        		getPreLispCheckLabel() == null ? EdgeData.NOTAPPLICABLE : getPreLispCheckLabel().getCheckedStatus());
        
        newEdgeData.ruleNames = (Vector<String>) ruleNames.clone();
        newEdgeData.ruleLabels = null;    // will recreate on demand
	
        return newEdgeData;
	
    }
    
    public Vector copyVector(Vector fromVector) {
        Vector toVector = new Vector();

        if (fromVector == null)
            return toVector;

        for (int i = 0; i < fromVector.size(); i++) {
            toVector.addElement(fromVector.elementAt(i));
        }

        return toVector;
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    /**
     */
    // /////////////////////////////////////////////////////////////////////////////////////
    public boolean getUpdatedInSkillometer() {
        return updatedInSkillometer;
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    /**
     */
    // /////////////////////////////////////////////////////////////////////////////////////
    public void setUpdatedInSkillometer(boolean updated) {
        updatedInSkillometer = updated;
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // /////////////////////////////////////////////////////////////////////////////////////
    public ProblemNode getSourceProblemNode() {
        if (edge == null)
            return null;
        return edge.getNodes()[ProblemEdge.SOURCE];
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // /////////////////////////////////////////////////////////////////////////////////////
    public void incrementTraversalCount() {
        traversalCount++;
        if (actionLabel != null)
        	actionLabel.update();
    }

    public int getTraversalCount() {
        return traversalCount;
    }

    public void setTraversalCount(int newCount) {
        traversalCount = newCount;
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // /////////////////////////////////////////////////////////////////////////////////////
    public ActionLabel getActionLabel() {
        return actionLabel;
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // /////////////////////////////////////////////////////////////////////////////////////
    public ProblemNode getEndProblemNode() {
        if (edge == null)
            return null;
        return edge.getNodes()[ProblemEdge.DEST];
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // /////////////////////////////////////////////////////////////////////////////////////
    public void setEdge(ProblemEdge e) {
        edge = e;
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // /////////////////////////////////////////////////////////////////////////////////////
    public ProblemEdge getEdge() {
        return edge;
    }
	
    /**
     * Tell whether this edge represents the student's assertion that work on 
     * the problem is finished.
     * @return true if the first element of {@link #getSelection()} is "Done"
     *         and the corresponding element of {@link #getAction()} is 
     */
    public boolean isDone() {
	
        Vector s = getSelection();
        Vector a = getAction();
        
        if(s.size() > 0 && a.size() > 0)
        	return MsgType.DONE.equalsIgnoreCase((String) s.get(0)) &&
        			MsgType.BUTTON_PRESSED.equalsIgnoreCase((String) a.get(0));

        return false;
    }

    /**
     * All access to {@link #ruleLabels} must be through this method.
     * @return {@link #ruleLabels}; callers MUST consider this read-only
     */
    public Vector<RuleLabel> getRuleLabels() {
    	if (ruleLabels == null)
    		createRuleLabels();
        return ruleLabels;
    }
    
    /**
     * Generate {@link #ruleLabels} from {@link #ruleNames}.
     */
    private void createRuleLabels() {
		ruleLabels = new Vector<RuleLabel>();
    	if (getRuleNames() == null)
    		return;
    	for (String ruleName : getRuleNames()) {
			RuleLabel ruleLabel = new RuleLabel(ruleName, getController());
			ruleLabels.addElement(ruleLabel);
			ruleLabel.addMouseListener(new RuleLabelHandler(ruleLabel, getEdge(), getController()));    		
    	}
    }

    public Vector<String> getSkills() {
    	if (getRuleNames() == null)
    		return new Vector<String>();
    	else
    		return (Vector<String>) getRuleNames().clone();
    }

    public boolean hasRealRule() {
    	if (getRuleNames() == null)
    		return false;
    	for (String ruleName : getRuleNames()) {
            if (ruleName.indexOf(" ") > 0)
                return true;
    	}
    	return false;
    }

    /**
     * This method returns the operative value for the minimum traversals permitted.
     * @return {@link #minTraversals}; a value of 0 means traversal is optional.
     */
    public int getMinTraversals() {
    	return minTraversals;
    }
    
    /**
     * If {@link #minTraversals} is a variable, this method returns that variable.
     * @return {@link #minTraversalsStr}
     */
    public String getMinTraversalsStr() {
    	return minTraversalsStr;
    }
    
    /**
     * Set {@link #minTraversals} and {@link #minTraversalsStr}.
     * @param minTraversals
     */
    public void setMinTraversals(int minTraversals) {
    	this.minTraversals = minTraversals;
    	this.minTraversalsStr = Integer.toString(minTraversals);
    }

    /**
     * Set {@link #minTraversals} and {@link #minTraversalsStr}.
     * The argument can be a variable, which sets {@link #minTraversals} to the default value.
     * @param minTraversals if not numeric, effective value is 1
     */
    public void setMinTraversalsStr(String minTraversals) {
    	//If input is null or empty, set min to 1
    	//(Lowest sensible value)
    	if (minTraversals == null || minTraversals.length() < 1)
    	{
    		this.minTraversalsStr = "1";
    		this.minTraversals = 1;
    	}
    	else
    	{
    		try 
    		{
    			//Try to parse input
    			this.minTraversalsStr = minTraversals;
    		    this.minTraversals = Integer.parseInt(minTraversals.trim());
    	    	} catch (Exception e) 
    	    	{
    	    		//If input is not numeric, set min to 1
    	    		//(Lowest sensible value)
	    		    this.minTraversals = 1;
    	    	}
    		}
    }

    /**
     * This method returns the operative value for the maximum traversals 
     * permitted.
     * @return {@link #minTraversals}; a value of 0 means traversal is prevented.
     */
    public int getMaxTraversals() {
    	return maxTraversals;
    }
    
    /**
     * If {@link #maxTraversals} is a variable, this 
     * method returns that variable.
     * @return {@link #maxTraversalsStr}
     */
    public String getMaxTraversalsStr() {
    	return maxTraversalsStr;
    }
    
    /**
     * Set {@link #maxTraversals} and {@link #maxTraversalsStr}.
     * @param maxTraversals
     */
    public void setMaxTraversals(int maxTraversals) {
    	this.maxTraversals = maxTraversals;
    	this.maxTraversalsStr = Integer.toString(maxTraversals);
    }
    
    /**
     * Set {@link #maxTraversals} and {@link #maxTraversalsStr}.
     * The argument can be a variable, which sets {@link #maxTraversals} 
     * to the default value.
     * @param maxTraversals if not numeric, effective value is 1
     */
    public void setMaxTraversalsStr(String maxTraversals) {
    	//If input is null or empty, set max to current min value
    	//(CANNOT go lower than current min value)
    	if (maxTraversals == null || maxTraversals.length() < 1)
    	{
    		this.maxTraversalsStr = this.minTraversalsStr;
    		this.maxTraversals = this.minTraversals;
    	}
    	else
    	{
    		try 
    		{
    			//Try to parse input
    			this.maxTraversalsStr = maxTraversals;
    		    this.maxTraversals = Integer.parseInt(maxTraversals.trim());
    	    	} catch (Exception e) 
    	    	{
    	    		//If input is not numeric, set max to current min value
    	        	//(CANNOT go lower than current min value)
    	    		this.maxTraversals = this.minTraversals;
    	    	}
    	}
    }



    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setSuccessMsg(String successMsg) {
        this.successMsg  = successMsg;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public String getSuccessMsg() {
        return successMsg;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setOldActionType(String oldActionType) {
        this.oldActionType = oldActionType;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public String getOldActionType() {
        return oldActionType;
    }

    public void setPreferredEdge(boolean isPreferredEdge) {
        this.isPreferredEdge = isPreferredEdge;
    }

    public boolean isPreferredEdge() {
        return isPreferredEdge;
    }

    public String getCallbackFn(){
    	return callbackFn;
    }
    
    public void setCallbackFn(String newValue){
	    this.callbackFn = newValue;
    }
    
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    public// ///////////////////////////////////////////////////////////////////////////////////////////////
    void setAction(Vector action) {
        getMatcher().setDefaultAction(action.elementAt(0) == null ? "" : action.elementAt(0).toString());
        // this.action = action;
        // trace.out (5, this, "ACTION SET");
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setSelection(Vector selection) {
        // this.selection = selection;
        if (trace.getDebugCode("mps")) trace.out("mps", " matcher = " + getMatcher() + " selection = " + selection);
        getMatcher().setDefaultSelection(selection.elementAt(0) == null ? "" : selection.elementAt(0).toString());
        // trace.out (5, this, "SELECTION SET");
    }
    
    public void setActor(String actor){
    	
    	getMatcher().setDefaultActor(actor);
    }
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public Vector getSelection() {
        // if (matcher != null) {
        return getMatcher().getDefaultSelectionVector();
        // }
        // return selection;
    }
    
    public String getActor()
    {
    	return getMatcher().getDefaultActor();
    }
    
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public Vector getAction() {
        // if (matcher != null) {
        return getMatcher().getDefaultActionVector();
        // }
        // return action;
    }

    

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setInput(Vector input) {
        // this.input = input;
        getMatcher().setDefaultInput(input.elementAt(0) == null ? "" : input.elementAt(0).toString());
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public Vector getInput() {
        // if (matcher != null) {
        return getMatcher().getDefaultInputVector();
        // }
        // return input;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public String getActionType() {
        return actionType;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setActionType(String actionType) {
        if (actionType == null || (!actionType.equals(CORRECT_ACTION) &&
				   !actionType.equals(FIREABLE_BUGGY_ACTION) &&
				   !actionType.equals(BUGGY_ACTION) &&
				   !actionType.equals(HINT_ACTION) &&
				   !actionType.equals(UNTRACEABLE_ERROR) &&
				   !actionType.equals(CLT_ERROR_ACTION) &&
				   !actionType.equals(GIVEN_ACTION) ))
            throw new IllegalArgumentException("invalid action type "+actionType);
        this.actionType = actionType;
        if (actionLabel != null)
	    actionLabel.resetForeground();
    }
    
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public String getCheckedStatus() {
        return checkedStatus;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setCheckedStatus(String checkedStatus) {
        this.checkedStatus = checkedStatus;
    }

    /**
     * Evaluate expressions or formulas in a success or buggy message.
     * @param success if true use {@link #successMsg}; else {@link #buggyMsg}
     * @return {@link #successMsg} or {@link #buggyMsg} with formulas evaluated;
     *         returns empty string if indicated message field is null
     */
    public String getInterpolatedSuccessOrBuggyMsg(boolean success) {
    	String msg = (success ? successMsg : buggyMsg);
    	if (trace.getDebugCode("functions")) trace.outln("functions", "interpolating " + msg);
	
    	if (msg == null || msg.length() == 0) { return ""; }
	
    	CTATFunctions functions = new CTATFunctions(getProblemModel().getVariableTable(),
    			getProblemModel(), getProblemModel().getFormulaParser());
	
    	String interpolatedMsg = functions.interpolate(msg.trim(), interpolateSelection, 
				    interpolateAction, interpolateInput);
    	if (trace.getDebugCode("functions")) trace.outln("functions", "interpolated: " + interpolatedMsg);
        return interpolatedMsg;
    }
    
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public String getBuggyMsg() {
        return buggyMsg;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setBuggyMsg(String buggyMsg) {
        this.buggyMsg = buggyMsg;
    }

    public void setInterpolateSAI(String s, String a, String i) {
        interpolateSelection = s;
        interpolateAction = a;
        interpolateInput = i;
    }
    
    void setInterpolatedHints(List<String> reportableHints) {
    	this.interpolatedHints = new Vector<String>(reportableHints);
    }
    
    private Vector<String> interpolatedHints() {
        if (interpolatedHints==null || interpolatedHints.size()==0)
            interpolateHints(null);
        return interpolatedHints;
    }
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Get the hints that are not the empty string
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public Vector<String> getHints() {
        Vector<String> v = new Vector<String>();

        for (String hint : interpolatedHints()) {
            if (!hint.equals(""))
                v.addElement(hint);
        }

        return v;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Get all hints, including the empty strings. Needed by the
     * HelpSuccessPanel for editing.
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public Vector<String> getAllHints() {
        return hints;
    }

    /**
     * Get all hints, excluding the empty strings. 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public List<String> getAllNonEmptyHints() {
    	List<String> NonEmptyHints = new ArrayList<String>();
        for (String hint : hints) {
            if (!hint.trim().equals(""))
            	NonEmptyHints.add(hint);
        }
        return NonEmptyHints;
    }
    
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setHints(Vector hints) {
        // trace.addDebugCode ("mps");
        // trace.printStack ("mps");
        // for (int i = 0; i < hints.size(); i++) {
        // trace.out ("hint = " + hints.elementAt(i));
        // }
        if (trace.getDebugCode("functions")) trace.outln("functions", "setting hints " + hints);
        interpolatedHints = null;
        this.hints = hints;
        // trace.out (5, this, "HINTS SET");
        updateToolTip();
    }

    public void addHint(String newHint) {
        if (trace.getDebugCode("functions")) trace.outln("functions", "adding hint " + newHint);
        if (newHint != null) {
            if (!newHint.equals("")) {
                this.hints.addElement(newHint);
                interpolatedHints = null;
            }
        }
        updateToolTip();

        return;
    }
    
    public void interpolateHints(VariableTable vt, String s, String a, String i) {
        setInterpolateSAI(s, a, i);
        interpolateHints(vt);
    }
    
    public void interpolateHints(VariableTable vt) {
        if (trace.getDebugCode("functions")) trace.outln("functions", "interpolating " + hints);
        interpolatedHints = new Vector<String>();

        if (hints==null || hints.size()==0)
        	return;
        
        if (vt == null)
        	vt = getProblemModel().getVariableTable();

        CTATFunctions functions = new CTATFunctions(vt, getProblemModel(),
        		getProblemModel().getFormulaParser());
        
        for (String hint : hints)
            interpolatedHints.add(functions.interpolate(hint, interpolateSelection, interpolateAction, interpolateInput));
        if (trace.getDebugCode("functions")) trace.outln("functions", "hints interpolated: " + interpolatedHints);
    }
        
    /**
     * This is called to hold the actual input entered by the student. This is
     * not always the same as what the Author has entered since we added new
     * matching options.
     * 
     * @param object
     */
    public void setStudentInput(Object object) {
        // trace.out ("mps", "SET INPUT: " + object);
        studentInput = object;
    }

    public Vector getStudentInput() {
        // if (studentInput == null && input.size() > 0)
        // return input;

        if (getMatcher() != null && studentInput == null)
            return getMatcher().getDefaultInputVector();

        Vector studentInputVector = new Vector();

        studentInputVector.addElement(studentInput);

        return studentInputVector;
    }

    public void setStudentSelection(Object object) {
        studentSelection = object;
    }
  

    public Vector getStudentSelection() {

        if (studentSelection == null) {
            return getMatcher().getDefaultSelectionVector();
        }
        Vector studentSelectionVector = new Vector();

        studentSelectionVector.addElement(studentSelection);

        return studentSelectionVector;
    }

    public void setStudentAction(Object object) {
        studentAction = object;
    }

    public Vector getStudentAction() {
        if (studentAction == null)
            return getMatcher().getDefaultActionVector();

        Vector studentActionVector = new Vector();

        studentActionVector.addElement(studentAction);

        return studentActionVector;
    }

    /**
     * @return
     */
    public Matcher getMatcher() {
        return matcher;
    }

    public void setMatcher(Matcher m) {
        if (trace.getDebugCode("functions")) trace.outln("functions", "setMatcher(" + m.getClass().getName() + ")");
        matcher = m;
        m.setExternalResources(getProblemModel().getVariableTable(), getProblemModel(),
        		getProblemModel().getFormulaParser());
    }

    // -sanket
    /**
     * @return the vector of elements that are associated with this arc
     */
    public Vector getAssociatedElements() {
        return associatedElements;
    }

    /**
     * @return the vector of element values that are associated with this arc
     */
    public Vector getAssociatedElementsValues() {
        return associatedElementsValues;
    }

    /**
     * @param vector
     */
    public void setAssociatedElements(Vector vector) {
        this.getAssociatedElements().removeAllElements();
        this.getAssociatedElements().addAll(vector);
    }

    /**
     * @param vector
     */
    public void setAssociatedElementsValues(Vector vector) {
        this.getAssociatedElementsValues().removeAllElements();
        this.getAssociatedElementsValues().addAll(vector);
    }

    public void clearAssociatedElements() {
        this.getAssociatedElements().removeAllElements();
        this.getAssociatedElementsValues().removeAllElements();
    }

    // -sanket

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public String getDialogueName() {
        return dialogueName;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setDialogueName(String dialogueName) {
        this.dialogueName = dialogueName;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public boolean getStep_Succesful_Completion() {
        return step_Succesful_Completion;
    }


    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public boolean getStep_Student_Error() {
        return step_Student_Error;
    }

    // test whether the hints only contains the default hint
    public boolean defaultHintOnly() {
        boolean defaultHintOnlyFlag = false;

        if (getHints() == null)
            return true;

        if (getHints().size() == 1) {
            String currentHint = (String) (getHints().elementAt(0));
            if (currentHint.equals(formDefaultHint()))
                defaultHintOnlyFlag = true;
        }

        return defaultHintOnlyFlag;
    }

    public boolean haveNoneDefaultHint() {
        if (getHints().size() == 0)
            return false;
        else if (getHints().size() == 1)
            return !defaultHintOnly();
        else
            return true;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * // used in 1. generating default hint and 2. check that the hint is
     * default // hint
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public String formDefaultHint() {

        String defaultHint = "";

        Vector inputs = getInput();
        if (inputs.size() == 0)
            return defaultHint;

        String inputText = (String) inputs.elementAt(0);

        Vector actions = getAction();
        if (actions.size() == 0)
            return defaultHint;

        String actionName = (String) actions.elementAt(0);

        if (getMatcher() != null
                && getMatcher().getClass().getName().equals(
                        "pact.BehaviorRecorder.Matcher.AnyMatcher"))
            inputText = "Any Value";

        defaultHint = DEFAULT_HINT_FRONT + inputText + DEFAULT_HINT_REAR;
        
        BR_Controller controller = problemModel.getController();

        if (actionName.equals("UpdateTable"))
            defaultHint = TABLE_FRONT + inputText + TABLE_REAR;
        else if (actionName.equals("UpdateComboBox"))
            defaultHint = COMBOBOX_FRONT + inputText + COMBOBOX_REAR;
        else if (actionName.equals("UpdateChooser"))
            defaultHint = CHOOSER_FRONT + inputText + CHOOSER_REAR;
        else if (actionName.equals("UpdateList"))
            defaultHint = LIST_FRONT + inputText + LIST_REAR;
        else if (actionName.equals("UpdateTextField"))
            defaultHint = TEXTFIELD_FRONT + inputText + TEXTFIELD_REAR;
        else if (actionName.equals("UpdateTextArea"))
            defaultHint = TEXTAREA_FRONT + inputText + TEXTAREA_REAR;
        else if (actionName.equals("ButtonPressed")) {
            Vector selections = getSelection();
            String firstSelection = (String) selections.elementAt(0);
            if (!(firstSelection.equalsIgnoreCase("Help") || firstSelection
                    .equalsIgnoreCase("Hint")))
                defaultHint = BUTTON_DEFAULT;
            if (firstSelection.equalsIgnoreCase("Done")) {
            	JCommWidget d = null;
            	try {
            		if (controller != null)
            			d = controller.getCommWidget(firstSelection);
            	} catch (RuntimeException re) {
            		d = null;
            	}
                if (d != null) {
                    JCommButton dd = (JCommButton) d;
                    inputText = dd.getText();
                    defaultHint = DONE_BUTTON_FRONT + inputText
                            + DONE_BUTTON_REAR;
                }
            }
        } else if (actionName.equals("UpdateRadioButton")) {
            // radioButton's text
            defaultHint = RADIOBUTTON_DEFAULT;
            
            // FIXME: this is a place when the controller directly accesses the interface
            if (controller != null && controller.getInterfaceLoaded() == false)
            	return defaultHint;
            
            Vector selections = getSelection();
            String selectionName = (String) selections.elementAt(0);
            JCommWidget d = null;
            if (controller != null)
            	d = controller.getCommWidget(selectionName);
            if (d != null) {
                JCommRadioButton dd = (JCommRadioButton) d;
                inputText = dd.getText();
                defaultHint = RADIOBUTTON_FRONT + inputText + RADIOBUTTON_REAR;
            }
        } else if (actionName.equals("UpdateComposer"))
            defaultHint = COMPOSER_FRONT + inputText + COMPOSER_REAR;

        return defaultHint;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void updateDefaultHint() {
        if (getAction().size() == 0 || getInput().size() == 0
                || hints.size() != 0)
            return;

        boolean addDefaultHintFlag = false;

        if (hints == null)
            addDefaultHintFlag = true;

        if (hints.size() == 0)
            addDefaultHintFlag = true;

        if (addDefaultHintFlag) {
            // add default hint
            
            String defaultHint = formDefaultHint();

            if (!defaultHint.equals(""))
                hints.addElement(defaultHint);
        }

    }

    //Integer classview is no-op now.. until we implement new meaningful views.
    public String getActionLabelText(boolean mouseEntered, int classView) {
        if (trace.getDebugCode("functions")) trace.out("functions", getClass().getName() + ".getActionLabelText()");

        StringBuffer text = new StringBuffer();
        int view; 
        
        text.append(getUniqueID()).append(". ");
        
        if (trace.getDebugCode("functions")) trace.out("functions", getClass().getName() + " matcher: " + getMatcher());
        text.append(getMatcher().getActionLabelText(ActionLabel.classView));
        
        return (text.toString());

    }
    
    private static String truncateNumericStr(String numStr) {
    	try {
    		int t = Integer.parseInt(numStr);
    		return Integer.toString(t);
    	} catch (NumberFormatException nfe) {
    		return numStr.substring(0, Math.min(numStr.length(), 2));
    	}
    }
    
    /**
     * (collinl) Why have a method on a class that always exists which 
     * would only be called in service of a termporary class then call 
     * back to that class conditionally?
     */
    private boolean displayTraversalCount() {
    	BR_Controller ctlr = getController();
    	return ctlr != null && ctlr.getTraversalCountEnabled();
    }

    /**
     * Revise the Selection, Action, Input properties in {@link #demoMsgObj} from the values in
     * {@link #getMatcher()}.{@link Matcher#getDefaultSelectionVector() getDefaultXxxxVector()}.
     */
	public void resetCommMessage() {
        demoMsgObj.setProperty("Selection", getMatcher().getDefaultSelectionVector());
        demoMsgObj.setProperty("Action", getMatcher().getDefaultActionVector());
        demoMsgObj.setProperty("Input", getMatcher().getDefaultInputVector());
    }

    private int getIndex(Vector vector, String string) {
        for (int i = 0; i < vector.size(); i++) {
            if (vector.elementAt(i).equals (string))
                return i;
        }
        return -1;
    }

    /**
     * @param demoMsgObj The demoMsgObj to set.
     */
    public void setDemoMsgObj(MessageObject demoMsgObj) {
        if (demoMsgObj == null)
            trace.printStack("mo");
        this.demoMsgObj = demoMsgObj;
    }

    /**
     * @return Returns the demoMsgObj.
     */
    public MessageObject getDemoMsgObj() {
        return demoMsgObj;
    }
    
	public String toString() {
        try {
            return getSourceProblemNode().getNodeView().getText();
        } catch (NullPointerException e) {
            // do nothing
        }
        return "This EdgeData has no source node";
	}

    /**
     * @param actionLabel The actionLabel to set.
     */
    public void setActionLabel(ActionLabel actionLabel) {
        this.actionLabel = actionLabel;
    }

    /**
     * @param preLispCheckLabel The preLispCheckLabel to set.
     */
    public void setPreLispCheckLabel(CheckLispLabel preLispCheckLabel) {
        this.preLispCheckLabel = preLispCheckLabel;
    }

    /**
     * @return Returns the preLispCheckLabel.
     */
    public CheckLispLabel getPreLispCheckLabel() {
        return preLispCheckLabel;
    }

	public BR_Controller getController() {
		return problemModel.getController();
	}

	/**
	 * @return {@link #localUniqueID}.
	 */
    public int getUniqueID() {
        return localUniqueID;
    }
    
    /**
     * Set {@link #localUniqueID}. If {@link #problemModel} is non-null, update
     * {@link ProblemModel#edgeUniqueIDGenerator}
     * @param uniqueIDValue
     */
    public void setUniqueID(int uniqueIDValue) {
        localUniqueID = uniqueIDValue;
        if (problemModel != null)
        	problemModel.updateEdgeUniqueIDGenerator(localUniqueID);
    }
    
    /**
     * Return a name for this edge.
     * @return result of "link"+{@link #getUniqueID()}
     */
    public String getName() {
    	return "link"+getUniqueID();    	
    }

	/**
	 * Translate the strings used for checkResult ({@link #BUGGY}, e.g.) to
	 * those used for {@link #actionType} ({@link #BUGGY_ACTION}).
	 * @param checkResult
	 * @return actionType; returns empty string if can't translate
	 */
	public static String checkResultToActionType(String checkResult) {
		if (checkResult == null)
			return "";
		else if (checkResult.equalsIgnoreCase(SUCCESS))
			return CORRECT_ACTION;
		else if (checkResult.equalsIgnoreCase(BUGGY))
			return BUGGY_ACTION;
		else if (checkResult.equalsIgnoreCase(FIREABLE_BUG))
			return FIREABLE_BUGGY_ACTION;
		else
			return "";
	}

	public String getTooltipText() {
		String tooltipText;
		tooltipText = "<html><b>Click to edit.<br>";
		tooltipText += "Link number:</b> "+getUniqueID()+", "+getActionType()+"<br>";
		
		tooltipText +="<b>Selection (" + matcher.getSelectionMatcherType();
		tooltipText +="):</b>  " + matcher.getSelectionToolTipText()+"<br>";
		tooltipText +="<b>Action (" + matcher.getActionMatcherType();
		tooltipText +="):</b>  " + matcher.getActionToolTipText()+"<br>";
		tooltipText +="<b>Input (" + matcher.getInputMatcherType();
		tooltipText +="):</b>  " + matcher.getInputToolTipText()+"<br>";
		
		tooltipText +="<b>Allowed Traversals (min-max):</b>  " 
			+ minTraversalsStr + "-" + maxTraversalsStr + "<br>"; 
		if(displayTraversalCount())
		tooltipText +="<b>Actual Traversals:</b>  "+getTraversalCount() + "<br>";
		tooltipText +="<b>Actor:</b>  "+getActor()+ "<br>";		
		
		
		if (getHints().size() == 0) {
			tooltipText += "No hints defined.<br>";
		} else {
			for (int i = 0; i<getHints().size(); i++) {
				String hint = (String) getHints().elementAt(i);
				if (hint.equals(""))
					continue;
				if (hint.length() > 75) {
					hint = hint.substring(0, 74);
					hint += "...";
				}
				if (defaultHintOnly())
					tooltipText += "<b>Default Hint:</b> " + hint+"<br>";
				else
					tooltipText += "<b>Hint:</b> " + hint+"<br>";
			}
		}	
		if(getSuccessMsg()!="")
			tooltipText +="<b>Success Message:</b>  "+getInterpolatedSuccessOrBuggyMsg(true)+ "<br>";
		if(getBuggyMsg()!="")
			tooltipText +="<b>Bug Message:</b>  "+getInterpolatedSuccessOrBuggyMsg(false)+ "<br>";
		tooltipText += "</html>";
		return tooltipText;
	}

	/**
	 * Revise current skill values according to the latest transaction.
	 * @param transactionResult update to apply: one of
	 *        {@link Skill#CORRECT()}, {@link Skill#INCORRECT}, {@link Skill#HINT}
	 */
	public void updateSkills(String transactionResult) {
		if (trace.getDebugCode("ps")) trace.out("ps", "updateSkills["+getUniqueID()+"]("+transactionResult+
				") for ruleNames "+getRuleNames());
		ProblemModel pm = getProblemModel();
		if (pm == null)
			return;
		pm.updateSkills(transactionResult, getRuleNames(),
				Skill.makeStepID(getMatcher().getDefaultSelectionVector(), getMatcher().getDefaultActionVector()));
	}

	/**
	 * @return {@link #ruleNames}
	 */
	public Vector<String> getRuleNames() {
		if (ruleNames == null)
			ruleNames = new Vector<String>();
		return ruleNames;
	}

	/**
	 * Add the given name to {@link #ruleNames}. Will not add a duplicate name.
	 * @param ruleLabelText
	 */
	public void addRuleName(String ruleLabelText) {
		if (ruleNames == null)
			ruleNames = new Vector<String>();
		if (ruleNames.contains(ruleLabelText))
			return;
		ruleNames.add(ruleLabelText);
		ruleLabels = null;
	}

	/**
	 * Insert the given new name to {@link #ruleNames} before or after the given old rule name.
	 * Will not add a duplicate name.
	 * @param oldRuleLabelText insert next to this rule name; add at end if not found
	 * @param newRuleLabelText
	 * @param after true means insert after; false means before
	 */
	public void insertRuleName(String oldRuleLabelText, String newRuleLabelText, boolean after) {
		if (ruleNames == null)
			ruleNames = new Vector<String>();
		if (ruleNames.contains(newRuleLabelText))
			return;
		for (int i = 0; i < ruleNames.size(); ++i) {
			if (!ruleNames.get(i).equals(newRuleLabelText))
				continue;
			ruleNames.insertElementAt(newRuleLabelText, (after ? i+1 : i));
			return;
		}
		ruleNames.add(newRuleLabelText);
	}

	/**
	 * Replace the existing name in {@link #ruleNames} with this new one. If not present, add it.
	 * @param oldRuleLabelText
	 * @param newRuleLabelText
	 */
	public void replaceRuleName(String oldRuleLabelText, String newRuleLabelText) {
		if (ruleNames == null)
			addRuleName(newRuleLabelText);
		else {
			int rnIndex = ruleNames.indexOf(oldRuleLabelText);
			if (rnIndex < 0)
				ruleNames.add(newRuleLabelText);
			else
				ruleNames.set(rnIndex, newRuleLabelText);
		}
		ruleLabels = null;
	}

	/**
	 * Remove from {@link #ruleNames} the given entry.
	 * @param ruleNameProductionSet
	 */
	public void removeRuleName(String ruleNameProductionSet) {
		int i = ruleNames.indexOf(ruleNameProductionSet);
		if (i >= 0)
			ruleNames.remove(i);
	}
	
    //[Kevin Zhao](kzhao) - This method was created in order to allow EdgeData to handle
    //						'Demonstrate This Link' mode, but what the actual method does is
    //						it handles an interface action and will change the EdgeData data
    //						to match the interface action.
    /**
     * @param selection - The selection
     * @param action - The action
     * @param input - The input
     * @param messageObject
     * @param actionType
     */
    public void handleInterfaceActionEditing(Vector selection, Vector action, 
    		Vector input, MessageObject messageObject, String actionType) {
    	setSelection(selection);
    	setAction(action);
        setInput(input);
        setDemoMsgObj(messageObject);
        setActionType(actionType);
    	return;
    }
    
    public void handleDemonstrateThisLinkInput(Vector selectionD, Vector actionD, 
    		Vector inputD, MessageObject messageObjectD, String actionTypeD) {
    	
    	//Check if its not a leaf
    	if (!edge.dest.isLeaf()) {    	
    		String tempSelectionString;
    			for (int i = 0; i < selectionD.size(); i++) {
    				tempSelectionString = (String) selectionD.elementAt(i);
    				if (tempSelectionString.equalsIgnoreCase("Done")) {
    		    		JOptionPane.showMessageDialog(problemModel.getController().getActiveWindow(),
    		    				"You may not demonstrate a done step in the middle of this graph",
    		    				"Warning",
    		    				JOptionPane.WARNING_MESSAGE);
    		    		return;
    				}
    			}
        }
    	
    	handleInterfaceActionEditing(selectionD, actionD, inputD, messageObjectD, actionTypeD);
    	problemModel.getController().getCtatModeModel().exitDemonstrateThisLinkMode();
    	
    	ActionLabel thisActionLabel = getActionLabel();
    	thisActionLabel.resetForeground();
    	thisActionLabel.update();
		getController().fireCtatModeEvent(CtatModeEvent.REPAINT);
        
        //Undo checkpoint for creating blank node ID: 1337
    	ActionEvent ae = new ActionEvent(this, 0, "Demonstrate this Link");
		getController().getUndoPacket().getCheckpointAction().actionPerformed(ae);
    }

    public void updateMovedFromEdgeView() {
    	getActionLabel().update();
        
        getPreLispCheckLabel().update(getUniqueID(),getSourceProblemNode(),getEndProblemNode());
        
        int ruleCount = getRuleLabels().size();
        for (int i=0; i< ruleCount; i++) {
            RuleLabel l = (RuleLabel) getRuleLabels().elementAt(i);
			l.update(getSourceProblemNode(),getEndProblemNode(), i, ruleCount);
        }
        //trace.out (5, this, "UPDATE DONE");
    }

    /**
     * Tell whether this link should replace the student's input in the tutor response.
     * @return false if not a {@value #CORRECT_ACTION} or {@value #FIREABLE_BUGGY_ACTION} link;
     *         else result of {@link Matcher#replaceInput()}
     */
	public boolean replaceInput() {
		if(!CORRECT_ACTION.equalsIgnoreCase(getActionType())
				&& !FIREABLE_BUGGY_ACTION.equalsIgnoreCase(getActionType()))
			return false;
		return getMatcher().replaceInput();
	}

	public Vector evaluateReplacement(Vector selection, Vector action, Vector input, VariableTable vt) {
		return getMatcher().evaluateReplacement(selection, action, input, vt, getProblemModel());
	}

	public Element getActionLabelElement() {
		Element elt = new Element("actionLabel");
		elt.setAttribute("preferPathMark", Boolean.toString(isPreferredEdge()));
		
		DialogueSystemInfo dialogueSystemInfo = getDialogueSystemInfo();
        elt.addContent(new Element("studentHintRequest").setText(dialogueSystemInfo.getStudent_Hint_Request()));
        elt.addContent(new Element("stepSuccessfulCompletion").setText(dialogueSystemInfo.getStep_Successful_Completion()));
        elt.addContent(new Element("stepStudentError").setText(dialogueSystemInfo.getStep_Student_Error()));
        
        elt.addContent(new Element("uniqueID").setText(Integer.toString(getUniqueID())));
        
        if (getDemoMsgObj() != null)
        	elt.addContent(getDemoMsgObj().toElement());
        
        elt.addContent(new Element("buggyMessage").setText(getBuggyMsg()));
        elt.addContent(new Element("successMessage").setText(getSuccessMsg()));
        
        for (String hint : getAllNonEmptyHints())
        	elt.addContent(new Element("hintMessage").setText(hint));
        
        elt.addContent(new Element("callbackFn").setText(getCallbackFn()));
        
        elt.addContent(new Element("actionType").setText(getActionType()));
        elt.addContent(new Element("oldActionType").setText(getOldActionType()));
        elt.addContent(new Element("checkedStatus").setText(getCheckedStatus()));

        elt.setAttribute("minTraversals", getMinTraversalsStr());
        elt.setAttribute("maxTraversals", getMaxTraversalsStr());
        
        elt.addContent(getMatcher().toElement());
        
		return elt;
	}

	/**
	 * Use {@link CTATRandom#randomOrder(int)} to reorder the elements in
	 * {@link #hints} and {@link #interpolatedHints()}.
	 * @throws IllegalStateException if {@link #hints}.size doesn't match {@link #interpolatedHints()}.size
	 */
	public void randomizeHintOrder() {
		CTATRandom random = new CTATRandom();
		modifiedHintOrder = random.randomOrder(hints.size());

		String[] copyHints = hints.toArray(new String[modifiedHintOrder.length]);
		if(trace.getDebugCode("hints")) {
			trace.outNT("hints", "ED.randomizeHintOrder() before reordering:");
			for(int i = 0; i < copyHints.length; ++i)
				System.out.printf("  [%d] %.40s;\n", i, copyHints[i]);
		}
		for (int i = 0; i < modifiedHintOrder.length; ++i)
			hints.set(i, copyHints[modifiedHintOrder[i]]);
		if(trace.getDebugCode("hints")) {
			trace.outNT("hints", "ED.randomizeHintOrder() after "+Arrays.toString(modifiedHintOrder));
			for(int i = 0; i < hints.size(); ++i)
				System.out.printf("  [%d] %.40s;\n", modifiedHintOrder[i], hints.get(i));
		}

		if (interpolatedHints != null) {
			if (interpolatedHints.size() != modifiedHintOrder.length)
				throw new IllegalStateException("randomizeHintOrder(): no. of interpolated hints "+
						interpolatedHints.size()+" does not match no. of Hints "+modifiedHintOrder.length);

			copyHints = interpolatedHints.toArray(new String[modifiedHintOrder.length]);
			for (int i = 0; i < modifiedHintOrder.length; ++i)
				interpolatedHints.set(i, copyHints[modifiedHintOrder[i]]);
		}
		updateToolTip();
	}

	/**
	 * Show the order of hint messages, as an array of integers where the value for the first hint is 1.
	 * If {@link #modifiedHintOrder} is null (because {@link #randomizeHintOrder()}, e.g. was never called),
	 * then this method returns null to indicate that the order is simply [1, 2, ..., <i>nHints</i>]. 
	 * @return the {@link #modifiedHintOrder}; if null, return null to show hint order unmodified
	 */
	public int[] getModifiedHintOrder() {
		if (modifiedHintOrder == null)
			return null;
		int[] result = new int[modifiedHintOrder.length];
		for (int i = 0; i < result.length; ++i)
			result[i] = modifiedHintOrder[i]+1;
		return result;
	}

	/**
	 * Call {@link ActionLabel#updateToolTip()}.
	 */
	public void updateToolTip() {
		if(getActionLabel() != null)
			getActionLabel().updateToolTip();
	}

	/**
	 * Change the {@link #actionLabel} font to that returned by
	 * {@link #getController()}.{@link BR_Controller#getOriginalEdgeFont()}
	 */
	public void resetLabelFont() {
		if(getActionLabel() != null)
    		getActionLabel().setFont(getController().getOriginalEdgeFont());
	}

	/**
	 * Call {@link #actionLabel}.{@link ActionLabel#resetSize()}.
	 */
	public void resetLabelSize() {
		if(getActionLabel() != null)
			getActionLabel().resetSize();
	}

	/**
	 * Show the modified hint order as an added element in a text list. If {@link #modifiedHintOrder}
	 * is not null, insert the contents of {@link #modifiedHintOrder} in the format
	 * "HintOrder[<i>m, n, ...</i>]", where <i>m, n, ...</i> are the original
	 * hint numbers (first hint is 1) shown in their modified order.
	 * @param edgeData source of {@link #modifiedHintOrder} to use
	 * @param texts list of texts to modify (appends a new element)
	 * @return texts, possibly modified by {@link #modifiedHintOrder}
	 */
	public Vector appendHintOrder(Vector texts) {
		int[] mho = getModifiedHintOrder();
		if(mho == null || mho.length < 1)
			return texts;
		Vector result;
		if(texts == null)
			result = new Vector<String>();
		else
			result = new Vector(texts);
		result.add("HintOrder"+Arrays.toString(mho));
		if(trace.getDebugCode("hints"))
			trace.outNT("hints", "EdgeData.appendHintOrder() result["+(result.size()-1)+"]: "+result.get(result.size()-1));
		return result;
	}
	
	public static void setCopyData(EdgeData copyData) {
		_copyData = copyData;
	}
	
	public static EdgeData getCopyData() {
		return _copyData;
	}

	/**
	 * Tell whether this link could be tutor-performed. Does not check number of sibling links.
	 * Instead, tests <ul>
	 * <li>{@link #getActionType()} is {@value #CORRECT_ACTION};</li>
	 * <li>linkTriggered argument, if not null, matches {@link Matcher#isLinkTriggered()};</li>
	 * <li>{@link Matcher#isTutorActor(String, boolean)} returns true on {@link #getActor()}.</li>
	 * </ul>
	 * @param linkTriggered if not null, {@link Matcher#isLinkTriggered()} must match the value;
	 *        if null, test not applied
	 * @return true if could be tutor-performed
	 */
	public boolean isTutorPerformed(Boolean linkTriggered) {
		if (!CORRECT_ACTION.equalsIgnoreCase(getActionType()))
			return false;                               // must be a correct step
		Matcher m = getMatcher();
		if (m == null)
			return false;
		if(linkTriggered != null && linkTriggered.booleanValue() != m.isLinkTriggered())
			return false;                               // trigger must match arg
		String actor = getActor();
		if (Matcher.isTutorActor(actor, true))
			return true;                                // actor must be tutor or any
		return false;
	}

	/**
	 * Send the {@value MsgType#INTERFACE_ACTION} message associated with this link as if it's
	 * a tutor-performed action. Does not update position in problem solution. Remove any delay
	 * in {@link #getAction()} before sending.
	 */
	public void testTPA() {
        MessageObject mo = PseudoTutorMessageBuilder.buildToolInterfaceAction(getSelection(),
        		getAction(), getInput(), PseudoTutorMessageBuilder.TRIGGER_DATA,
        		PseudoTutorMessageBuilder.TUTOR_PERFORMED);
    	String action = mo.getFirstAction();
    	String[] revisedAction = new String[1];
    	int delay = UniversalToolProxy.isDelayedAction(action, revisedAction);		
    	if(delay > 0)
    		mo.setAction(revisedAction[0]);
		if(trace.getDebugCode("br"))
			trace.out("br", "EdgeData.testTPA() removed delay "+delay+", to send "+mo.summary());
		getController().handleMessageUTP(mo);  // low-level send to avoid MessageTank updates
	}

	/**
	 * @return {@value #TPAColor} if {@link #isTutorPerformed(Boolean)}, else {@value #PlainColor} 
	 */
	public Color getDefaultColor() {
		return (isTutorPerformed(null) ? TPAColor : PlainColor);
	}

	/**
	 * Compare 2 instances by serializing and comparing the XML elements.
	 * @param ed0 one instance 
	 * @param ed1 the other instance 
	 * @return true if same except for {@link EdgeData#getUniqueID()}
	 */
	public static boolean sameSettings(EdgeData ed0, EdgeData ed1) {
		EdgeData[] eds = { ed0, ed1 };
		String[] elts = new String[2];
		for(int i = 0; i < eds.length; ++i) {
			if(eds[i].getEdge() == null) {
				ProblemEdge pe = new ProblemEdge();
				eds[i].setEdge(pe);
				pe.setEdgeData(eds[i]);
			}
			elts[i] = eds[i].getEdge().toXMLString();
			if(trace.getDebugCode("editstudentinput"))
				trace.out("editstudentinput", "ED.hasSameSettings["+i+"] "+eds[i].getEdge().toXMLString());
		}
		return sameExceptForID(elts[0], elts[1]);
	}
	
	/** Match the &lt;uniqueID&gt; element in the serialized XML. */
	private static final Pattern UniqueIDInXML = Pattern.compile("(<uniqueID>)([0-9]+)(</uniqueID>)");

	/**
	 * Compare 2 serialized elements ignoring differences in {@link #getUniqueID()}.
	 * @param xml0
	 * @param xml1
	 * @return true if same but for uniqueID
	 */
	private static boolean sameExceptForID(String xml0, String xml1) {
		String c0 = UniqueIDInXML.matcher(xml0).replaceFirst("\\1 \\3");
		String c1 = UniqueIDInXML.matcher(xml1).replaceFirst("\\1 \\3");
		return c0.equals(c1);
	}
}
