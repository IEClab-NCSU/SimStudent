/*
 * Created on Mar 9, 2005
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import org.jdom.Element;

import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.hcii.ctat.CTATRandom;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.MessageTank;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageBuilder;
import edu.cmu.pact.BehaviorRecorder.Dialogs.MergeMassProductionDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerPath;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerTracer;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupEditorContext;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExpressionMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATSerializable;
import edu.cmu.pact.BehaviorRecorder.View.ActionLabel;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.BehaviorRecorder.View.RuleLabel;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.EmptyIterator;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.ctat.model.ProblemSummary;
import edu.cmu.pact.ctat.model.SCORM;
import edu.cmu.pact.ctat.model.Skill;
import edu.cmu.pact.ctat.model.Skills;
import fri.patterns.interpreter.parsergenerator.Parser;

/**
 * @author mpschnei
 * 
 * Created on: Mar 9, 2005
 */
public class ProblemModel implements Serializable {
	
	/** The proportion of steps that should get unrequested hints. */
	public static final double UNREQUESTED_HINT_FRACTION = (1.0+Double.MIN_NORMAL)/3;
	/** For the Serializable interface; should be set to a revision number. */
	static final long serialVersionUID = 3100;                   // for now, CTAT version number * 100
//			Long.parseLong(VersionInformation.RELEASE_NUMBER.replaceAll("[0-9]", ""))*100; 
    public static final String GROUP = "group";
    // start metadata
    private String problemName;
    private String problemFullName;
    private String courseName;
    private String unitName;
    private String sectionName;
//    Skills skills;
    // end metadata
    private ProblemNode startNode;
    private ProblemGraph problemGraph;
    private GroupEditorContext editContext;
    //private static List<ExampleTracerLink> copiedEdges;

    /**
     * Skills (rules, by ACT-R theory) associated with this problem.
     * Map key is "<i>ruleName productionSetName</i>", all in lower-case, to permit
     * case-insensitive lookups. Values are {@link RuleProduction} entries listed in the .brd.
     */
    private Map<String, RuleProduction> ruleProductionMap;
    private boolean startNodeCreatedFlag;

    private List<MessageObject> startNodeMessageVector;

    /** XML attribute name for {@link #behaviorRecorderMode}. */
    public static final String BEHAVIOR_RECORDER_MODE = "behaviorRecorderMode";
    
    /** XML attribute name for {@link #studentBeginsHereState}. */
    public static final String STUDENT_BEGINS_HERE = "startStateNodeName";
    
    /** Fixed variable XML attribute value for {@link #studentBeginsHereState}. */
    public static final String STUDENT_BEGINS_HERE_VAR = MergeMassProductionDialog.VAR_LEFT_DELIMITER
    		+ STUDENT_BEGINS_HERE + MergeMassProductionDialog.VAR_RIGHT_DELIMITER;
    
    private Vector checkAllEdges;

    private Vector checkAllNodes;

    // this vector holds all of groups
    // each element is a vector with the first element as the group name
    // followed by all of links in this group
    private Vector linksGroups;

    private Vector willDeleteLinks;

    private Vector willRemovedLinkGroups;

    private boolean useCommWidgetFlag;

    private boolean problemLoadedFromLispTutor;

    private boolean searchPathFlag;
    
    /** 
     * Whether to lock a widget after a correct action on it.
     * True means we should lock it, to avoid student overwriting
     * a correct answer.
     */
    private boolean lockWidget;

    /** 
     * Whether to display feedback to the student.
     * True means that hints and correct/incorrect indications should
     * not be passed to the student interface.
     */
    private FeedbackEnum suppressStudentFeedback = FeedbackEnum.DEFAULT;

    /** 
     * Whether the exact matcher should be sensitive to uppercase-
     * lowercase differences. False means that upper- and lowercase
     * strings with the same letters differ.
     */
    private boolean caseInsensitive;
    
    /**
     * The policy of letting the hints be biased by
     * current state
     */
    private HintPolicyEnum hintPolicy = HintPolicyEnum.DEFAULT; 
    
    private HashSet listeners;

    private BR_Controller controller;

	/** Whether we're adding the first node. */
    private boolean firstNode;

	// FIXME remove when old group represention deleted
	private boolean needToRedoLinksGroups;

	/** Max node identifier used so far. */
	private int nodeUniqueIDGenerator;

	/** Max link identifier used so far. */
	private int edgeUniqueIDGenerator;

	/** New Example Tracer's graph. */
	private ExampleTracerGraph exampleTracerGraph;

	/** Variable table for {@link ExpressionMatcher}. */
	private VariableTable variableTable;

	/** Whether to highlight a proper component upon a NO-MODEL trace. */
	private boolean highlightRightSelection;

	/** To collect results for the current problem. */
	private ProblemSummary problemSummary;

	/** Set of nodes that occur above the student_begins_state. */
	private Set<Integer> beforeStudentBeginsStates;

	/** Start-problem node; if null, same as node 0. */
	private ProblemNode studentBeginsHereState;

	/**
	 * Switch into this mode after loading the brd. No-op if null. Else value is one of
	 * {@link CtatModeModel#EXAMPLE_TRACING_MODE}, {@link CtatModeModel#JESS_MODE}, etc.
	 */
	private String behaviorRecorderMode;
	
	/**
	 * Whether the UI should warn the student that the
	 * pressing the Done button will prevent further work on this problem. If null,
	 * then consider this value true when {@link #suppressStudentFeedback} is
	 * {@link FeedbackEnum#HIDE_ALL_FEEDBACK}. 
	 */
	private Boolean confirmDone;

	/** Return to the start state at the earliest opportunity. */
	private ProblemNode requestGoToState;

	/** Feedback message to display when a step has a correct SAI but is out of order. */
	private String outOfOrderMessage;
	
	/** True if hint randomization is on for this problem. See {@link #setHintRandomization(boolean)}. */
	private boolean randomizeHints = false;
	
	/** Sorted list of depths at which to deliver an unrequested hint. See {@link #getUnrequestedHint(int)}. */
	private int[] unrequestedHintDepths = new int[0];
	
	/** The unrequested hint last delivered. See {@link #getUnrequestedHint(int)}. */
	private List<MessageObject> unrequestedHintMsgs = null;

	/** Text of the feedback message for a step that's correct but out of order. */
	public static final String DEFAULT_OUT_OF_ORDER_MESSAGE =
			"Instead of the step you are working on, please work on the highlighted step.";

	/** Attribute name for this-step-is-correct-but-out-of-order message in behavior graph file. */
	public static final String OUT_OF_ORDER_MESSAGE = "outOfOrderMessage";
	
	/** Regular expression used to find Mass Production variable references. */
	private static final Pattern MassProductionVarPattern = Pattern.compile("%\\([^)][^)]*\\)%");
	
	/**
	 * Common code for constructors. Initialize {@link #problemGraph}, vectors.
	 * @param controller
	 */
	private void init(BR_Controller controller) {
		if (trace.getDebugCode("pm")) trace.out("pm", "controller "+controller);
		if (trace.getDebugCode("vtm")) trace.outNT("vtm", "ProblemModel.init()");
		problemName = "";
		problemFullName = "";
		courseName = "";
		unitName = "";
		sectionName = "";
//        exampleTracerGraph = null; sewall 2010/02/05 don't do this: disables groups (?!)
		if(controller!=null)
			this.controller = controller;
		setStartNode(null);		
        problemGraph = new ProblemGraph();
        if(listeners == null)//Don't reset listeners if they exist
        	listeners = new HashSet<ProblemModelListener>();
        if(!Utils.isRuntime()) {
        	if(editContext==null)
        		editContext = new GroupEditorContext(getExampleTracerGraph().getGroupModel());
        	else
        		editContext.clear();
        }
        
        /*
         * sewall 2010/02/03 CTAT2326: w/o controller (as for skill matrix), exampleTracerGraph
         * needs to listen; w/ controller (as when tracing), linkage in PseudoTutorMessageHandler
         */
        if (this.controller == null)
        	addProblemModelListener(getExampleTracerGraph());
        
        ruleProductionMap = new LinkedHashMap<String, RuleProduction>();
        setStartNodeCreatedFlag(false);
        setStartNodeMessageVector(new Vector());      
        checkAllEdges = new Vector();
        checkAllNodes = new Vector();
        setLinksGroups(new Vector());
        willDeleteLinks = new Vector();
        willRemovedLinkGroups = new Vector();
        useCommWidgetFlag = true;
        problemLoadedFromLispTutor = false;
        searchPathFlag = false;
        lockWidget = true;
        hintPolicy = HintPolicyEnum.DEFAULT;
        suppressStudentFeedback = FeedbackEnum.DEFAULT;
        caseInsensitive = true;

        setFirstNode(true);        
        needToRedoLinksGroups = true;
        nodeUniqueIDGenerator = 0;
        edgeUniqueIDGenerator = 0;       
      //  if(exampleTracerGraph!=null)
      //  	getExampleTracerGraph().initGraph(true, false);
        //if the ProblemModel has a controller it should check whether or not to make an authorTime variable table
        ctatFunctions = null;
        if(this.controller !=null)
        	setVariableTable( new VariableTable(Utils.isRuntime()));
        else
        	setVariableTable( new VariableTable());
        setHighlightRightSelection(false);
        problemSummary = null;
		if (trace.getDebugCode("pm")) trace.out("pm", "init("+studentBeginsHereState+"=>null)");
        studentBeginsHereState = null;
        beforeStudentBeginsStates = new LinkedHashSet<Integer>();
        
        getProblemGraph().clear();         
        
        if (this.controller == null)
        	return;
        //initialize graph properties from the preference settings
        PreferencesModel pm = this.controller.getPreferencesModel();
        
        if (pm == null)
        	return;
        Enum fe = pm.getEnumValue(BR_Controller.SUPPRESS_STUDENT_FEEDBACK);
        if (fe != null)
        	setSuppressStudentFeedback((FeedbackEnum) fe);
        Enum hpe = pm.getEnumValue(BR_Controller.HINT_POLICY);
        if (hpe != null)
        	setHintPolicy((HintPolicyEnum) hpe);
        Boolean b = pm.getBooleanValue(BR_Controller.HIGHLIGHT_RIGHT_WIDGET);
        if (b != null)
        	setHighlightRightSelection(b.booleanValue());
        b = pm.getBooleanValue(BR_Controller.CASE_SENSITIVE);
        if (b != null)
        	setCaseInsensitive(!(b.booleanValue()));
        b = pm.getBooleanValue(BR_Controller.LOCK_WIDGETS);
        if (b != null)
        	setLockWidget(b.booleanValue());
	}

	public ProblemModel(BR_Controller controller) {
    	if (trace.getDebugCode("vtm")) trace.outNT("vtm", "ProblemModel Constructor");
		init(controller);             
    }

    //////////////////////////////////////////////////////
    /**
     * find the in-edge to send Comm Message to update Interface State: by the
     * priority:
     * 
     * 1. has the check status which is consistent with the author intent. 2.
     * check status is success. 3. check stutus is firable-bug. 4. author intent
     * is correct. 5. author intent is firable-bug.
     * 
     */
    //////////////////////////////////////////////////////
    public ProblemEdge findIncomingEdgeForCommMsg(ProblemNode atNode) {
    	if (atNode == null)
            return null;

        ProblemEdge tempEdge;

		// based on the model check result
//		if (this.controller.getMode().equalsIgnoreCase(CtatModeModel.PRODUCTION_SYSTEM_MODE)) {
        if (this.controller.getCtatModeModel().isJessMode()
        	|| this.controller.getCtatModeModel().isTDKMode()	) {
			tempEdge = findEdgeOnCheckStatus(atNode);
	        if (tempEdge != null)
	            return tempEdge;	
		}
		
        // based on the author intent
        tempEdge = findFirableBugEdge(atNode);
        if (tempEdge != null)
            return tempEdge;

        return null;

    }
    
    public boolean hasPreferredPath(ProblemNode outNode) {
        if (outNode == null)
            return false;

        ProblemEdge tempEdge;
        EdgeData myEdge;

        // correct authorIntent edge first
        Enumeration connectingEdges = getProblemGraph().getConnectingEdges(outNode);

        while (connectingEdges.hasMoreElements()) {
            tempEdge = (ProblemEdge) connectingEdges.nextElement();
            if (outNode == tempEdge.getNodes()[ProblemEdge.SOURCE]) {
                myEdge = tempEdge.getEdgeData();
                if (myEdge.isPreferredEdge())
                    return true;
            }
        }

        return false;
    }

    public ProblemEdge updatePreferredPath(ProblemNode outNode, ProblemEdge excludeEdge, boolean throwException) throws ProblemModelException {
        if (outNode == null)
            return null;

		if (outNode.isLeaf())
			return null;
		
		if (outNode.getOutDegree() == 1
			&& excludeEdge != null){
			return null;
		}
		
        ProblemEdge tempEdge;
        EdgeData myEdge;

        // correct authorIntent edge first
        Enumeration iterEdges = getProblemGraph().getOutgoingEdges(outNode);

        while (iterEdges.hasMoreElements()) {
            tempEdge = (ProblemEdge) iterEdges.nextElement();

            if (outNode == tempEdge.getNodes()[ProblemEdge.SOURCE]
                    && tempEdge != excludeEdge) {
                myEdge = tempEdge.getEdgeData();
                if (myEdge.getActionType().equalsIgnoreCase(
                        EdgeData.CORRECT_ACTION)) {
                    myEdge.setPreferredEdge(true);
                    ProblemModelEvent e = new EdgeUpdatedEvent(this, myEdge.getEdge(),  true);
                    fireProblemModelEvent(e);
                    return tempEdge;
                }
            }
        }

        // if fail try firable bug authorIntent
        iterEdges = getProblemGraph().getConnectingEdges(outNode);

        while (iterEdges.hasMoreElements()) {
            tempEdge = (ProblemEdge) iterEdges.nextElement();

            if (outNode == tempEdge.getNodes()[ProblemEdge.SOURCE]
                    && tempEdge != excludeEdge) {
                myEdge = tempEdge.getEdgeData();
                if (myEdge.getActionType().equalsIgnoreCase(
                        EdgeData.FIREABLE_BUGGY_ACTION)) {
                    myEdge.setPreferredEdge(true);
                    ProblemModelEvent e = new EdgeUpdatedEvent(this, myEdge.getEdge(),  true);
					fireProblemModelEvent(e);
                    return tempEdge;
                }
            }
        }

//        brFrame.totalRepaint();
//        ProblemModelEvent e = new ProblemModelEvent (this, ProblemModelEvent.NO_PREFERRED_PATH_SET, null, null);
//		fireProblemModelEvent(e);
        if(throwException)
        	throw new ProblemModelException ("No preferred path defined");
        return null;
    }

    // ////////////////////////////////////////////////////
    /**
     * find the in-edge to send Comm Message to update Interface State: based
     * on the author Intent by the priority:
     * 
     * 1. author intent is correct and preferred. 2. author intent is correct. 3. author intent is firable-bug.
     * 
     */
    // ////////////////////////////////////////////////////
    private ProblemEdge findFirableBugEdge(ProblemNode atNode) {

        ProblemEdge returnEdge = null;
		
        ProblemEdge tempEdge;
        EdgeData tempMyEdge;
        ProblemNode parentTemp;
        String authorIntent;

        Enumeration iter = getProblemGraph().getConnectingEdges(atNode);

        while (iter.hasMoreElements()) {
            tempEdge = (ProblemEdge) iter.nextElement();
            parentTemp = tempEdge.getNodes()[ProblemEdge.SOURCE];

            if (parentTemp != atNode) {
                tempMyEdge = tempEdge.getEdgeData();
                authorIntent = tempMyEdge.getActionType();

                // find the authorIntent is "Correct Action"
                if (authorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION)) {
					returnEdge = tempEdge;
                
					// found the preferred & correct edge
					if (tempEdge.isPreferredEdge())
						return tempEdge;
                }
				
                // if no iterated edge with authorIntent as "Correct Action",
                // find the authorIntent is "Fireable Buggy Action".
                if (authorIntent
                        .equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION)
                        && returnEdge == null)
                    returnEdge = tempEdge;
            }
        }

        return returnEdge;
    }

    // ////////////////////////////////////////////////////
    /**
     * find the in-edge to send Comm Message to update Interface State: based
     * on the checked status by the priority:
     * 
     * 1. has the check stutus which is consistent with the author intent
     * (correct or firable-bug only). 2. check status is success. 3. check
     * status is firable-bug.
     */
    // ////////////////////////////////////////////////////
    private ProblemEdge findEdgeOnCheckStatus(ProblemNode atNode) {
        ProblemEdge returnEdge = null;

        ProblemEdge tempEdge;
        EdgeData tempMyEdge;
        ProblemNode parentTemp;
        String checkedStatus;
        String authorIntent;

        Enumeration iter = getProblemGraph().getConnectingEdges(atNode);

        while (iter.hasMoreElements()) {
            tempEdge = (ProblemEdge) iter.nextElement();
            parentTemp = tempEdge.getNodes()[ProblemEdge.SOURCE];

            if (parentTemp != atNode) {
                tempMyEdge = tempEdge.getEdgeData();
				returnEdge = tempEdge;
                checkedStatus = tempMyEdge.getCheckedStatus();
                authorIntent = tempMyEdge.getActionType();

                // find the checked status is "SUCCESS"
                if (checkedStatus.equalsIgnoreCase(EdgeData.SUCCESS))
                    returnEdge = tempEdge;

                // if no iterated edge with checked status as "SUCCESS",
                // find the checked status is "FIREABLE-BUG".
                if (checkedStatus.equalsIgnoreCase(EdgeData.FIREABLE_BUG)
                        && returnEdge == null)
                    returnEdge = tempEdge;

                // find the edge checkedStatus and authorIntent consistent
                if ((checkedStatus.equalsIgnoreCase(EdgeData.SUCCESS) && authorIntent
                        .equalsIgnoreCase(EdgeData.CORRECT_ACTION))
                        || (checkedStatus
                                .equalsIgnoreCase(EdgeData.FIREABLE_BUG) && authorIntent
                                .equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION)))
                    return tempEdge;
            }
        }

        return returnEdge;
    }

    /**
     * Find the {@link ExampleTracerGraph}'s best path from the start node to atNode.
     * @param atNode
     * @return path; empty path if atNode is start node.
     */
    public ExampleTracerPath findPath(ProblemNode atNode) {
    	if (atNode.equals(getStartNode()))
    		return new ExampleTracerPath();
    	Set<ExampleTracerPath> paths = getExampleTracerGraph().findAllPaths();
    	Set<ExampleTracerPath> goodPaths = new HashSet<ExampleTracerPath>();
    	for(ExampleTracerPath path : paths) {
    		
    		for(ExampleTracerLink link : path) {
    			if(getExampleTracerGraph().getNode(link.getNextNode()).getProblemNode().equals(atNode)) {
    				goodPaths.add(path);
    				break;  // sewall 2008/12/21: don't need to scan rest of this path
    			}
    		}
    	}
    	ExampleTracerPath path = ExampleTracerPath.getBestPath(goodPaths);
    	if (trace.getDebugCode("et")) trace.outNT("et", "ProblemModel.findPath("+atNode+"): "+paths.size()+" paths, "+
    			goodPaths.size()+" goodPaths, best path: "+path);
    	ExampleTracerPath restrictedPath = new ExampleTracerPath();
    	if(path != null)
    	{	
	    	for(ExampleTracerLink link : path) {
		    	restrictedPath.addLink(link);
		    	if(getExampleTracerGraph().getNode(link.getNextNode()).getProblemNode().equals(atNode))
		    		break;    	
		    }
    	}
    	
    	return restrictedPath; 
    }

    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * among in-edge links to testNode, priorities are: 1. preferred correct
     * link 2. correct link 3. firable bug link
     */
    // ////////////////////////////////////////////////////////////////////////////////
    ProblemEdge findInEdge(ProblemNode testNode) {
        ProblemEdge edgeTemp = null;
        EdgeData myEdgeTemp;

        Enumeration iter = getProblemGraph().getConnectingEdges(testNode);

        // get parent links
        Vector parentLinks = new Vector();
        while (iter.hasMoreElements()) {
            edgeTemp = (ProblemEdge) iter.nextElement();
            if (edgeTemp.getNodes()[ProblemEdge.SOURCE] != testNode)
                parentLinks.addElement(edgeTemp);
        }

        int sizeOfParentLinks = parentLinks.size();

        // first try correct prefered link
        for (int i = sizeOfParentLinks - 1; i >= 0; i--) {
            edgeTemp = (ProblemEdge) parentLinks.elementAt(i);
            myEdgeTemp = edgeTemp.getEdgeData();
            if (myEdgeTemp.isPreferredEdge()
                    && myEdgeTemp.getActionType().equalsIgnoreCase(
                            EdgeData.CORRECT_ACTION))
                return edgeTemp;
        }

        // second try correct link
        for (int i = sizeOfParentLinks - 1; i >= 0; i--) {
            edgeTemp = (ProblemEdge) parentLinks.elementAt(i);
            myEdgeTemp = edgeTemp.getEdgeData();
            if (myEdgeTemp.getActionType().equalsIgnoreCase(
                    EdgeData.CORRECT_ACTION))
                return edgeTemp;
        }

        // third try firable bug link
        for (int i = sizeOfParentLinks - 1; i >= 0; i--) {
            edgeTemp = (ProblemEdge) parentLinks.elementAt(i);
            myEdgeTemp = edgeTemp.getEdgeData();
            if (myEdgeTemp.getActionType().equalsIgnoreCase(
                    EdgeData.FIREABLE_BUGGY_ACTION))
                return edgeTemp;
        }

        // no any desired match
        return null;
    }

    public static boolean checkForValidProblemName(String problemName) {

        if (problemName == null || problemName.trim().equals("")) {
            return false;
        }

        Pattern p = Pattern.compile("\\W");
        java.util.regex.Matcher m;

        String textChar;

        for (int i = 0; i < problemName.length(); i++) {
            textChar = problemName.substring(i, i + 1);
            m = p.matcher(textChar);
            if (m.find() && !textChar.equals("+") && !textChar.equals("-")) {
                return false;
            }
        }

        return true;
    }

    /**
     * Send start state to the rule engine. Can wait for {@value MsgType#INTERFACE_DESCRIPTION}
     * messages, if the argument says we need them and there are none in the given messages.
     * @param messages start state messages to send
     * @param utp
     * @param wantInterfaceDescriptions if true and messages contains no InterfaceDescription 
     */
    public void loadStartStateMessages(Vector<MessageObject> messages, UniversalToolProxy utp) {
    	
        if (messages != null)
            setStartNodeMessageVector(messages);
        else
            setStartNodeMessageVector(messages = new Vector<MessageObject>());
        
        sendStartStateMessagesToRuleEngine(utp, messages, getClass().getSimpleName()+".loadStartStateMessages()");
    }

    /**
     * Send the current internal list from {@link #getStartNodeMessageVector()} to the rule engine.
     * @param utp to call {@link UniversalToolProxy#sendProperty(MessageObject)}
     * @param msgs start state messages to send
     * @param caller calling method's name, for debugging
     */
    private void sendStartStateMessagesToRuleEngine(UniversalToolProxy utp, List<MessageObject>msgs, String caller) {
        
    	if(!getController().getCtatModeModel().isRuleEngineTracing()){
    		return;
    	
    	}
    	if(trace.getDebugCode("mt"))
    		trace.out("mt", "from "+caller+": Sending "+(msgs==null?-1:msgs.size())+" start state Comm MSGs to LISP");
        int i = 0;
        for(Iterator<MessageObject> it = msgs.iterator(); it.hasNext(); i++) {
			MessageObject msg = it.next();
			if (msg != null)
				utp.sendProperty(msg);
        }
	}

	public void updateStartStateMessages(Vector messages, UniversalToolProxy utp) {
        if (messages != null)
            setStartNodeMessageVector(messages);
        else
            setStartNodeMessageVector(new Vector<MessageObject>());
      
        sendStartStateMessagesToRuleEngine(utp, messages, getClass().getSimpleName()+".updateStartStateMessages()");
		
        setStartNodeCreatedFlag(true);
    }
    

    public static void checkAddRules(String ruleNameProdutionSetText,
            Vector ruleProductionList, Vector problemSkillFrequency) {
        if (ruleNameProdutionSetText.indexOf(" ") <= 0)
            return;

        if (ruleNameProdutionSetText.equals("unnamed rule"))
            return;

        // checking the exiting rules
        int ruleLength = ruleProductionList.size();
        String tempRule;

        for (int i = 0; i < ruleLength; i++) {
            tempRule = (String) ruleProductionList.elementAt(i);

            // add 1 more frequency at the matched rule
            if (ruleNameProdutionSetText.equals(tempRule)) {
                Integer tempInteger = (Integer) problemSkillFrequency
                        .elementAt(i);
                problemSkillFrequency.setElementAt(new Integer(1 + tempInteger
                        .intValue()), i);

                return;
            }
        }

        // add new rule element
        ruleProductionList.addElement(ruleNameProdutionSetText);
        problemSkillFrequency.addElement(new Integer(1));

        return;
    }

    public boolean edgeLispChecked(ProblemEdge edgeTest) {
        boolean checkedFlag = false;

        int checkedSize = getCheckAllEdges().size();
        ProblemEdge tempEdge;
        for (int i = 0; !checkedFlag && (i < checkedSize); i++) {
            tempEdge = (ProblemEdge) getCheckAllEdges().elementAt(i);
            if (tempEdge == edgeTest)
                checkedFlag = true;
        }

        return checkedFlag;
    }

    public boolean nodeChecked(ProblemNode nodeTest) {
        boolean checkedFlag = false;

        int checkedSize = getCheckAllNodes().size();
        ProblemNode tempNode;
        for (int i = 0; !checkedFlag && (i < checkedSize); i++) {
            tempNode = (ProblemNode) getCheckAllNodes().elementAt(i);
            if (tempNode == nodeTest)
                checkedFlag = true;
        }

        return checkedFlag;
    }

    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * // check whether the authorIntent is consistent with lispCheckresult
     */
    public// ////////////////////////////////////////////////////////////////////////////////
    boolean checkConsistency(String tutorCheckResult, String authorIntent) {
        if (trace.getDebugCode("popup")) trace.out("popup", "checkConsistency: tutorCheckResult = " + tutorCheckResult + ", authorIntent = " + authorIntent);
        return ( ((tutorCheckResult.equalsIgnoreCase(EdgeData.SUCCESS) || 
                   tutorCheckResult.equalsIgnoreCase(ActionLabel.FAILBEFORE_CORRECT))
                  && 
                  authorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION))
                || 
                 (tutorCheckResult.equalsIgnoreCase(EdgeData.NO_MODEL) 
                  && 
                  authorIntent.equalsIgnoreCase(EdgeData.UNTRACEABLE_ERROR))
                || 
                 ((tutorCheckResult.equalsIgnoreCase(EdgeData.BUGGY) || 
                   tutorCheckResult.equalsIgnoreCase(ActionLabel.CORRECTBEFORE_INCORRECT))
                  && 
                  authorIntent.equalsIgnoreCase(EdgeData.BUGGY_ACTION)) 
                || 
                 (tutorCheckResult.equalsIgnoreCase(EdgeData.FIREABLE_BUG) 
                  &&
                  authorIntent.equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION))
                ||
                  // Thu Sep 14 23:35:19 LDT 2006 :: Noboru
                  // This must be very ad-hoc, but needed to be asserted for SimSt
                  // to be accurate on the model-trace.  This method used to return true 
                  // then a rule matched, but just recently, it started to return false.
                  // As a result, 
                  // LispResultCheckMessageHandler.processLispCheckResultSimStMode
                  // now shows an inappropriate pop-up window
                 (controller.getCtatModeModel().isSimStudentMode()
                  &&
                  tutorCheckResult.equalsIgnoreCase(EdgeData.NO_MODEL)
                  &&
                  authorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION))
                 );
    }

    /**
     * Find a path from the atNode to the toNode. This algorithm works forward down the outlinks.
     * When it finds the toNode, it builds the path as it returns up the call stack.
     * @param atNode
     * @param toNode
     * @return path or null
     */
    private List<ProblemEdge> findDirectedPath(final ProblemNode atNode, final ProblemNode toNode) {
    	List<ProblemEdge> result = null;
    	if (atNode.getOutDegree() < 1)       // end of path with no find
    		return null;
    	for (ProblemEdge outEdge : atNode.getOutgoingEdges()) {
    		ProblemNode destNode = outEdge.getDest(); 
    		if (destNode == toNode)
    			result = new LinkedList<ProblemEdge>();
    		else
    			result = findDirectedPath(destNode, toNode);
    		if (result != null) {
        		result.add(0, outEdge);      // found by child: prefix our edge & pass back
        		return result;
    		}
    	}
    	return null;
    }

    boolean checkDeleteProductionRule(ProblemNode thisNode) {
        // add codes to check that the deleted subgrapg has no rules with
        // productionRule
        return true;
    }

    public boolean isLeaf(ProblemNode thisNode) {
        return (thisNode.getOutDegree() == 0);
    }


    // check that the checkedNode has no parent
    public boolean checkNoParent(ProblemNode checkedNode) {
        return (checkedNode.getInDegree()== 0);
    }

	
	
	
    boolean isEdgeInWillDeleteLinks(ProblemEdge testEdge) {

        ProblemEdge tempEdge;

        int willDeleteLinksSize = getWillDeleteLinks().size();

        for (int i = 0; i < willDeleteLinksSize; i++) {
            tempEdge = (ProblemEdge) getWillDeleteLinks().elementAt(i);
            if (tempEdge == testEdge)
                return true;
        }

        return false;
    }

    // to find the directed path from fromNode to toNode
    public List<ProblemEdge> makeDirectedPath(final ProblemNode fromNode,
            final ProblemNode toNode) {

        List<ProblemEdge> path = findDirectedPath(fromNode, toNode);
        if (trace.getDebugCode("pm")) trace.out("pm", "checkDirectedPath(from "+fromNode+", to "+toNode+") found "+path);

        return path;
    }

	/**
	 * Return a subpath linking the given nodes. Uses 
	 * {@link ProblemModel#makeDirectedPath(ProblemNode, ProblemNode)}.
	 * @param fromNode
	 * @param toNode
	 * @param pm ProblemModel with graph
	 * @return path; null if toNode not reachable from fromNode
	 */
	public ExampleTracerPath makeSubpath(ProblemNode fromNode, ProblemNode toNode) {
		if (getController() == null || getController().getCtatModeModel().isExampleTracingMode()) {
			ExampleTracerGraph graph = getExampleTracerGraph();
			if (graph == null)
				return null;
			return graph.getBestSubpath(fromNode.getUniqueID(), toNode.getUniqueID());
		}
		List<ProblemEdge> edges = makeDirectedPath(fromNode, toNode);
		if (edges == null)
			return null;
		ExampleTracerGraph graph = getExampleTracerGraph();
		if (graph == null)
			return null;
		ExampleTracerPath result = new ExampleTracerPath();
		for (ProblemEdge edge : edges)
			result.addLink(graph.getLink(edge));
		return result;
	}
	
    /**
     * Remove the unnamed rules from the list of rules passed in.
     * 
     * @param rules
     *            list to scan
     * @return copy of list with "unnamed" rules removed
     */
    public static Vector getNamedRules(Vector rules) {
        Vector namedRules = new Vector();
        for (Iterator it = rules.iterator(); it.hasNext();) {
            String rule = (String) it.next();
            if (trace.getDebugCode("log")) trace.out("log", "rule=" + rule + "; log="
                    + !("unnamed".equalsIgnoreCase(rule.trim())));
            if (!("unnamed".equalsIgnoreCase(rule.trim())))
                namedRules.add(rule);
        }
        return namedRules;
    }



    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * to test whether the testedge is on testedVector
     */
    // ////////////////////////////////////////////////////////////////////////////////
    public static boolean testEdgeInVector(ProblemEdge testEdge,
            Vector testedVector) {
        ProblemEdge tempEdge;

        for (int i = 0; i < testedVector.size(); i++) {
            tempEdge = (ProblemEdge) testedVector.elementAt(i);

            if (testEdge == tempEdge)
                return true;
        }

        return false;
    }

	
//	 ////////////////////////////////////////////////////////////////////////////////
    /**
     * to test whether the testNode is on testedVector
     */
    // ////////////////////////////////////////////////////////////////////////////////
    public boolean testNodeInVector(ProblemNode testNode,
									Vector testedVector) {
        ProblemNode tempNode;

        for (int i = 0; i < testedVector.size(); i++) {
			tempNode = (ProblemNode) testedVector.elementAt(i);

            if (testNode == tempNode)
                return true;
        }

        return false;
    }


    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * validate wether there is a link group whose member is a child of the
     * atNode if find then return this link group's index otherwise return -1.
     */
    //////////////////////////////////////////////////////////////////////////////////    public
  /*  public int validateLinksGroupsOnaddNewState(ProblemNode atNode) {

        ProblemEdge tempEdge;
        int testIndex = -1;

        Enumeration iter = getProblemGraph().getOutgoingEdges(atNode);
        while (iter.hasMoreElements()) {
            tempEdge = (ProblemEdge) iter.nextElement();

            testIndex = isInLinksGroups(tempEdge, 2);
            if (testIndex >= 0)
                return testIndex;
        }

        return testIndex;
    }*/

    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * pretest which link groups are to be deleted based on willDeleteLinks
     */
    // ////////////////////////////////////////////////////////////////////////////////
  /*  public void preTestWillRemovedLinksGroups() {
        // pretest affected link group
        ProblemEdge tempEdge;
        int findLinkGroupIndex;
        Vector singleGroup;
        String linkGroupName;

        if (getWillDeleteLinks().size() > 0) {

            for (int i = 0; i < getWillDeleteLinks().size(); i++) {
                tempEdge = (ProblemEdge) getWillDeleteLinks().elementAt(i);
                findLinkGroupIndex = isInLinksGroups(tempEdge, 1);

                if (findLinkGroupIndex >= 0) {
                    singleGroup = (Vector) getLinksGroups().elementAt(
                            findLinkGroupIndex);
                    linkGroupName = (String) singleGroup.elementAt(0);
                    addToRemoveLinkGroups(linkGroupName);
                }
            }
        }

        // trace.out(5, this, "willRemovedLinkGroups.size() = " +
        // willRemovedLinkGroups.size());

        return;
    }
*/
  /*  public void removeWillDeletedLinkGroups() {
        String linkGroupName;

        for (int i = 0; i < getWillRemovedLinkGroups().size(); i++) {
            linkGroupName = (String) getWillRemovedLinkGroups().elementAt(i);
            removeLinkGroup(linkGroupName);
        }

        return;
    }*/

  /*  void removeLinkGroup(String linkGroupName) {
        Vector singleLinkGroup;
        String tempLinkName;
        int i = -1;

        for (i = 0; i < getLinksGroups().size(); i++) {
            singleLinkGroup = (Vector) getLinksGroups().elementAt(i);
            tempLinkName = (String) singleLinkGroup.elementAt(0);
            if (linkGroupName.equals(tempLinkName))
                break;
        }

        if (i < getLinksGroups().size() && i > -1)
            getLinksGroups().removeElementAt(i);

        return;

    }
*/
    void addToRemoveLinkGroups(String addLinkGroupName) {
        String tempLinkName;

        for (int i = 0; i < getWillRemovedLinkGroups().size(); i++) {
            tempLinkName = (String) getWillRemovedLinkGroups().elementAt(i);
            if (addLinkGroupName.equals(tempLinkName))
                return;
        }

        getWillRemovedLinkGroups().addElement(addLinkGroupName);
    }

    // first find the parent node with preferred path
    // if none of the parent has preferred path take the last one of the parent
    public static ProblemNode findParentNodeForDeletedNode(ProblemNode deletedNode, ProblemGraph problemGraph) {
        ProblemNode foundedNode = null;

        ProblemEdge tempEdge;
        EdgeData tempMyEdge;
        boolean hasPreferPathSet = false;

        Enumeration iter = problemGraph.getConnectingEdges(deletedNode);
        while (iter.hasMoreElements() && !hasPreferPathSet) {
            tempEdge = (ProblemEdge) iter.nextElement();
            if (deletedNode != tempEdge.getNodes()[ProblemEdge.SOURCE]) {
                foundedNode = tempEdge.getNodes()[ProblemEdge.SOURCE];
                tempMyEdge = tempEdge.getEdgeData();
                if (tempMyEdge.isPreferredEdge())
                    hasPreferPathSet = true;
            }
        }

        return foundedNode;
    }

    public ProblemEdge returnsEdge(ProblemNode sourceNode,
            ProblemNode destinateNode) {
        ProblemEdge tempEdge;

        Enumeration iter = getProblemGraph().getConnectingEdges(sourceNode);
        while (iter.hasMoreElements()) {
            tempEdge = (ProblemEdge) iter.nextElement();
            if (sourceNode == tempEdge.getNodes()[ProblemEdge.SOURCE]
                    && destinateNode == tempEdge.getNodes()[ProblemEdge.DEST]) {
                return tempEdge;
            }
        }

        return null;
    }

    // find all parent locked edges from the thisNode to the startNode
    // this is a recursive method until atNode is startNode.
    public void findParentEdgesList(ProblemNode atNode, Vector parentEdgesList) {
        if (atNode == getStartNode())
            return;

        ProblemEdge edgeTemp;

        Enumeration iter = getProblemGraph().getConnectingEdges(atNode);
        while (iter.hasMoreElements()) {
            edgeTemp = (ProblemEdge) iter.nextElement();

            if (edgeTemp.getNodes()[ProblemEdge.SOURCE] != atNode) {

                if (!testEdgeInVector(edgeTemp, parentEdgesList)) {
                    parentEdgesList.addElement(edgeTemp);

                    findParentEdgesList(
                            edgeTemp.getNodes()[ProblemEdge.SOURCE],
                            parentEdgesList);
                }
            }
        }

        return;
    }
	
	public void findAncestorNodesListIgnoringLinkX(ProblemNode atNode, Vector<ProblemNode> ancestorNodesList, ProblemEdge linkX){
		 if (atNode == getStartNode())
			 return;
		 ProblemEdge tempEdge;
		 ProblemNode tempNode;
		 Enumeration iter = getProblemGraph().getConnectingEdges(atNode);
		 while (iter.hasMoreElements()) {
			 tempEdge = (ProblemEdge) iter.nextElement();
			 if(tempEdge == linkX)
				 continue;
			 tempNode = tempEdge.getNodes()[ProblemEdge.SOURCE];
			 if (tempNode != atNode && !testNodeInVector(tempNode, ancestorNodesList)) {
				 ancestorNodesList.addElement(tempNode);
				 findAncestorNodesListIgnoringLinkX(tempNode, ancestorNodesList, linkX);
	            }
		 }
		 return;	
	}
    
	// find all locked Ancestor edges from the atNode to the startNode
    // this is a recursive method until atNode is startNode.
    public void findAncestorNodesList(ProblemNode atNode, Vector ancestorNodesList) {
        if (atNode == getStartNode())
            return;

        ProblemEdge tempEdge;
		ProblemNode tempNode;

        Enumeration iter = getProblemGraph().getConnectingEdges(atNode);
        while (iter.hasMoreElements()) {
			tempEdge = (ProblemEdge) iter.nextElement();
			tempNode = tempEdge.getNodes()[ProblemEdge.SOURCE];
            if (tempNode != atNode
				&& !testNodeInVector(tempNode, ancestorNodesList)) {
				ancestorNodesList.addElement(tempNode);

				findAncestorNodesList(
							tempNode,
							ancestorNodesList);
            }
        }

        return;
    }

    public static boolean containsEdge(Vector edgeList, ProblemEdge edge) {
        Enumeration enumeration = edgeList.elements();
        while (enumeration.hasMoreElements()) {
            if (enumeration.nextElement().equals(edge)) {
                return true;
            }
        }
        return false;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * // check all out_edges of parentNode if a edge is set preferPathMark then
     * return false, else return true
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public boolean checkSameParentEdges(ProblemNode parentNode) {
        ProblemEdge tempEdge;
        EdgeData tempMyEdge;
        boolean hasPreferPathSet = false;

        Enumeration iter = getProblemGraph().getOutgoingEdges(parentNode);
        while (iter.hasMoreElements() && !hasPreferPathSet) {
            tempEdge = (ProblemEdge) iter.nextElement();
            tempMyEdge = tempEdge.getEdgeData();
            if (tempMyEdge.isPreferredEdge())
                hasPreferPathSet = true;
        }

        return !hasPreferPathSet;
    }

    // /////////////////////////////////////////////////////////////////////////////////
    /**
     * used in unordered case:
     * 
     * test assumptions that: links having the same selection-action-input have
     * the same author intent;
     */
    public // ////////////////////////////////////////////////////////////////////////////////
    String checkSameTripleSameAuthorIntent(Vector tripleGroups) {

        Vector singleGroup;

		for (int i = 0; i < tripleGroups.size(); i++) {
			singleGroup = (Vector) tripleGroups.elementAt(i);
			String str = checkSingleGroupSameTripleSameAuthorIntent(singleGroup);
			if (str.length() != 0)
				return str;
		}
	
		return "";
    }
	
	
	private String checkSingleGroupSameTripleSameAuthorIntent (Vector singleGroup) {
		String singleGroupAuthorIntent;
		
		ProblemEdge tempEdge1;
		ProblemEdge tempEdge2;
        EdgeData tempMyEdge;
        String tempAuthorIntent;
	
	    for (int i = 0; i < singleGroup.size(); i++) {
			tempEdge1 = (ProblemEdge) singleGroup.elementAt(i);
		    tempMyEdge = tempEdge1.getEdgeData();
		    singleGroupAuthorIntent = tempMyEdge.getActionType();
			
			for (int j=0; j<singleGroup.size(); j++) {
				if (i == j)
					continue;
				
		        tempEdge2 = (ProblemEdge) singleGroup.elementAt(j);
		        tempMyEdge = tempEdge2.getEdgeData();
		        tempAuthorIntent = tempMyEdge.getActionType();
		
		        if (!singleGroupAuthorIntent.equalsIgnoreCase(tempAuthorIntent)) {
		
		            // trace.out(
		            // "there exist two edges which have the same triple value
		            // but different author intents.");
					ProblemNode sourceNode;
					ProblemNode destinateNode;
					
					sourceNode = tempEdge1.getNodes()[ProblemEdge.SOURCE];
					destinateNode = tempEdge1.getNodes()[ProblemEdge.DEST];
					
					String str = "The edge from the node '" + sourceNode.getNodeView().getText();
					
					str += "' to the node '" + destinateNode.getNodeView().getText() + "'";
					
					str += " has Author Intent '" + singleGroupAuthorIntent + "'.\n";
						
					sourceNode = tempEdge2.getNodes()[ProblemEdge.SOURCE];
					destinateNode = tempEdge2.getNodes()[ProblemEdge.DEST];
					
					str += "But the edge from the node '" + sourceNode.getNodeView().getText();
					
					str += "' to the node '" + destinateNode.getNodeView().getText() + "'";
					
					str += " has Author Intent '" + tempAuthorIntent + "'.\n";
					
		            return str;
		        }
			}
	    }
		
		return "";
	}
	

    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * if the addEdge has the same selection-action-input triple in some
     * singleGroup then add addEdge to this singleGroup. Otherwise create a new
     * singleGroup, add addEdge to this new singleGrou and add this new
     * singleGrou to the tripleGroups.
     */
    public // ////////////////////////////////////////////////////////////////////////////////
    void addEdgeToTripleGroup(ProblemEdge addEdge, Vector tripleGroups) {

        Vector singleGroup;

        ProblemEdge tempEdge;
        EdgeData tempMyEdge;

        EdgeData addMyEdge = addEdge.getEdgeData();

        // if the addEdge has the same selection-action-input
        // in some singleGroup then add this edge
        for (int i = 0; i < tripleGroups.size(); i++) {
            singleGroup = (Vector) tripleGroups.elementAt(i);

            tempEdge = (ProblemEdge) singleGroup.elementAt(0);
            tempMyEdge = tempEdge.getEdgeData();

            if (compareTwoStatesSame(addMyEdge, tempMyEdge, true)) {

                singleGroup.addElement(addEdge);

                return;
            }
        }

        // no any match so create a new singleGroup
        Vector newSingleGroup = new Vector();
        newSingleGroup.addElement(addEdge);

        tripleGroups.addElement(newSingleGroup);

        return;
    }

    /* Tests whether the node is Buggy (has edges coming in,
     * but none of them are correct/fireablebuggy).
     */
    public boolean isBuggyNode(ProblemNode node){
    	Enumeration iter = this.getProblemGraph().getIncomingEdges(node);
    	if(iter.hasMoreElements()){
    		ProblemEdge tempEdge;
    		while (iter.hasMoreElements()) {
                tempEdge = (ProblemEdge) iter.nextElement();
                if (tempEdge.isCorrectorFireableBuggy()){
                	return false;
                }
            }
    		return true;
    	}
    	return false;
    }
	public boolean canNodesBeMerged(ProblemNode target, ProblemNode source, BR_Controller controller) {
		if(target==null || source ==null)
			return false;
		if (target == source)
			return false;
		if(getStartNode() == source)
			return false;
		if(getStartNode() == target)
			return false;
		if (source.isDoneState() ^ target.isDoneState())
	        return false;
		if (source.isBuggyNode() ^ target.isBuggyNode()){
			return false;
		}
		if(source.isDoneState() ^ target.isDoneState())
			return false;
		
		//Prevent cycles.
		if (source.isAncestorNode(target))
	        return false;
		if (target.isAncestorNode(source))
	        return false;
		
		//To prevent duplicate Edges (edges with same source/destination)
		Vector<ProblemNode> targetChildren = target.getChildren();
		Vector<ProblemNode> sourceChildren = source.getChildren();
		int i;
		for(i=0; i < targetChildren.size(); i++){
			if(sourceChildren.contains(targetChildren.get(i)))
				return false;
		}
		Vector<ProblemNode> targetParents = target.getParents();
		Vector<ProblemNode> sourceParents = source.getParents();
		for(i=0; i < targetParents.size(); i++){
			if(sourceParents.contains(targetParents.get(i)))
				return false;
		}
		return true;
	}
    

    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////////////////////////////////
    public String checkSecondaryEffects(ProblemEdge parentEdge) {
        String effectText = "";

        ProblemNode childTemp = parentEdge.getNodes()[ProblemEdge.DEST];
        ProblemEdge edge;
        EdgeData myEdge = parentEdge.getEdgeData();

        String secondaryTextGood = "    Because arc "
                + myEdge.getUniqueID()
                + " is now consistent you have discovered the following good things:\n";
        String secondaryTextBad = " Because arc "
                + myEdge.getUniqueID()
                + " is now consistent you have discovered the following new bad problems:\n";
        boolean goodFlag = false;
        boolean badFlag = false;

        Enumeration iterOutEdge = getProblemGraph().getOutgoingEdges(childTemp);
        while (iterOutEdge.hasMoreElements()) {
            edge = (ProblemEdge) iterOutEdge.nextElement();

            if (!edgeLispChecked(edge)) {
                getCheckAllEdges().addElement(edge);

                myEdge = edge.getEdgeData();

                if (myEdge.getPreLispCheckLabel().preCheckedStatus
                        .equalsIgnoreCase(EdgeData.NOTAPPLICABLE)) {
                    // trace.out ( "inside checkSecondaryEffects
                    // preCheckedStatus.equalsIgnoreCase(ActionLabel.NOTAPPLICABLE).");
                    if (checkConsistency(myEdge.getCheckedStatus(),
                            myEdge.getActionType())) {
                        // trace.out ( "inside checkSecondaryEffects good.");

                        goodFlag = true;
                        secondaryTextGood = secondaryTextGood + "    Arc "
                                + myEdge.getUniqueID()
                                + " that used to be "
                                + myEdge.getPreLispCheckLabel().preCheckedStatus
                                + " is now consistent\n";
                    } else {
                        // trace.out ( "inside checkSecondaryEffects bad.");
                        badFlag = true;
                        secondaryTextBad = secondaryTextBad + "  Arc "
                                + myEdge.getUniqueID()
                                + " that used to be "
                                + myEdge.getPreLispCheckLabel().preCheckedStatus
                                + " in now inconsistent\n";
                    }
                }
            }
        }

        if (goodFlag)
            effectText = secondaryTextGood;
        if (badFlag)
            effectText = effectText + secondaryTextBad;

        return effectText;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * // find the node containing Vertex
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public ProblemNode getProblemNodeForNodeView(final NodeView Vertex) {
        return getNodeForVertexUniqueID(Vertex.getUniqueID(), problemGraph);
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    // find the node containing Vertex
    public static ProblemNode getNodeForVertexUniqueID(int verTexUniqueID,
            ProblemGraph problemGraph) {
        ProblemNode node;
        NodeView curr;

        Enumeration iter = problemGraph.nodes();
        while (iter.hasMoreElements()) {
            node = (ProblemNode) iter.nextElement();
            //curr = node.getNodeView();
            if (node.getUniqueID() == verTexUniqueID)
                return node;
        }

        return null;
    }
    
    // find source state node from given action label
    /**
     * 
     */
    public ProblemEdge getEdgeForESE_Label(final ActionLabel ese_label) {
        return getEdge(ese_label.getUniqueID());
    }


    /**
     * Find a graph edge by, alas, linear-time lookup by {@link EdgeData#getUniqueID()}.
     * @param uniqueID id of edge to match
     * @return found edge, or null
     */
    public ProblemEdge getEdge(int uniqueID) {
        ProblemEdge edge;
        EdgeData myEdge;
        Enumeration iter = getProblemGraph().edges();
        while (iter.hasMoreElements()) {
            edge = (ProblemEdge) iter.nextElement();
            myEdge = edge.getEdgeData();
            if (myEdge.getUniqueID() == uniqueID)
                return edge;
        }
        return null;
    }


    // ////////////////////////////////////////////////////////////////////////////////
    // find the same state based on the new interface info: selectionP, actionP,
    // inputP
    // ////////////////////////////////////////////////////////////////////////////////
    public Vector findSameStates(ProblemNode currNodeP, Vector selectionP,
            Vector actionP, Vector inputP) {
    	// hold all of matched same states , as return vector
        Vector findMatchNodes = new Vector();        

        ProblemEdge valuesMatchedEdge;
        ProblemEdge tempEdge;

        Vector matchedEdges;

        // test whether there is an edge with the same values of selectionP,
        // actionP, inputP
        matchedEdges = findSameTripleEdges(selectionP, actionP, inputP);
        int sizeOfMatchedEdges = matchedEdges.size();

        // no any match, so findMatchNodes is empty
        if (sizeOfMatchedEdges == 0)
            return findMatchNodes;

        // find the list of correct states from the currNodeP to the startNode
        Vector correctEdgesListCurr = new Vector();
        findEdgesList(currNodeP, correctEdgesListCurr,
                (ProblemEdge) matchedEdges.elementAt(0));

        // iterate through matchedEdges to find the matched same states
        int sizeOfCurr = correctEdgesListCurr.size();

        // hold the path edges from the start node to the matchedEdges edge's
        // SOURCE node
        Vector correctEdgesListMatch;
        int sizeOfMatch;

        Vector correctEdgesListCurrCopy;
        boolean noMatchEdgeFlag;

        // find the first matched equal state
        // ??? for multiple matched states what should we do???
        for (int i = 0; i < sizeOfMatchedEdges; i++) {
            valuesMatchedEdge = (ProblemEdge) matchedEdges.elementAt(i);
            noMatchEdgeFlag = false;

            correctEdgesListMatch = new Vector();
            // find the list of correct states from start state to (Node)
            // valuesMatchedEdge.getNodes()[Edge.SOURCE]
            findEdgesList(valuesMatchedEdge.getNodes()[ProblemEdge.SOURCE],
                    correctEdgesListMatch, valuesMatchedEdge);

            // test if each state in correctEdgesListMatch has a match state in
            // correctEdgesListCurr
            sizeOfMatch = correctEdgesListMatch.size();

            // matched path to startNode is longer than the path of currentNode
            // to startNode
            if (sizeOfMatch > sizeOfCurr)
                continue;

            correctEdgesListCurrCopy = (Vector) correctEdgesListCurr.clone();
            // each state edge in matched path should have an equal state edge
            // in correctEdgesListCurrCopy
            for (int j = 0; !noMatchEdgeFlag && j < sizeOfMatch; j++) {
                tempEdge = (ProblemEdge) correctEdgesListMatch.elementAt(j);
                if (!findMatchEdgeAndUpdateList(tempEdge, correctEdgesListCurrCopy)) {
                    // check if tempEdge has the match in
                    // correctEdgesListCurrCopy only based on selection & action
                    noMatchEdgeFlag = !testInList(tempEdge,
                            correctEdgesListCurrCopy);
                }
            }

            if (noMatchEdgeFlag)
                continue;

            // all correctEdgesListCurr path states matched
            if (correctEdgesListCurrCopy.size() == 0) {
                addNodeToMatchedNodes(
                        valuesMatchedEdge.getNodes()[ProblemEdge.DEST],
                        findMatchNodes);

                continue;
            }

            // try all of down path from (Node)
            // valuesMatchedEdge.getNodes()[Edge.DEST]
            // until matched all of remaining state edges in
            // correctEdgesListCurr
            // then store the last matched state node in findMatchNode

            // zz 10/28/03
            // Vector correctEdgesListMatchCopy = (Vector)
            // correctEdgesListMatch.clone();
            findMatchedStateNode(
                    valuesMatchedEdge.getNodes()[ProblemEdge.DEST],
                    correctEdgesListCurrCopy, findMatchNodes);
        }

        return findMatchNodes;
    }

    // zz add 10/27/03 test if atEdge is in edgesList only based on selection &
    // action.
    boolean testInList(ProblemEdge atEdge, Vector edgesList) {
        ProblemEdge tempEdge;
        EdgeData tempMyAtEdge = atEdge.getEdgeData();
        EdgeData tempMyEdge;

        int sizeOfEdgesList = edgesList.size();

        for (int i = 0; i < sizeOfEdgesList; i++) {
            tempEdge = (ProblemEdge) edgesList.elementAt(i);
            tempMyEdge = tempEdge.getEdgeData();
            if (compareTwoStatesSame(tempMyAtEdge, tempMyEdge, false))
                return true;
        }

        return false;

    }

    // ////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////////////////////////////////
    //
    void addNodeToMatchedNodes(ProblemNode atNode, Vector findMatchNodes) {
        boolean noDuplicateFlag = true;
        int sizeOfFindMatchNodes = findMatchNodes.size();
        ProblemNode tempNode;

        for (int i = 0; i < sizeOfFindMatchNodes; i++) {
            tempNode = (ProblemNode) findMatchNodes.elementAt(i);
            if (tempNode == atNode) {
                noDuplicateFlag = false;
                break;
            }
        }

        if (noDuplicateFlag)
            findMatchNodes.addElement(atNode);

        return;
    }

    // zz modified 10/27/03
    // test whether atEdge is on correctEdgesListCurr, if so:
    // 1. remove atEdge from correctEdgesListCurr;
    // 2. return true;
    // otherwise return false

    boolean findMatchEdgeAndUpdateList(ProblemEdge atEdge, Vector correctEdgesListCurrCopy) {
        ProblemEdge tempEdge;
        EdgeData myEdge1 = atEdge.getEdgeData();
        EdgeData myEdge2;

        int sizeOfCurr = correctEdgesListCurrCopy.size();
        for (int i = 0; i < sizeOfCurr; i++) {
            tempEdge = (ProblemEdge) correctEdgesListCurrCopy.elementAt(i);
            myEdge2 = tempEdge.getEdgeData();
            if (compareTwoStatesSame(myEdge1, myEdge2, true)) {
                correctEdgesListCurrCopy.removeElement(tempEdge);
                return true;
            }
        }

        return false;
    }

    public String testNewDestNodeForLink(ProblemEdge problemEdge, ProblemNode newChildNode){
		ProblemNode parentNode =problemEdge.getSource();
    	ProblemNode childNode = problemEdge.getDest();
    	//This is to avoid getting an annoying pop up message when you are trying to click on
    	//a state and you by mistake click on the edge
    	if(newChildNode.equals(problemEdge.getDest()))
			return "ignore";
		if(parentNode.equals(newChildNode)){
			return "You cannot set the destination to be the same as the source. Drag the link to a different state.\n";
		}
		
    	//hmmm.. eventually we might want Done to be defined by the node rather then the edge.
		if(childNode.isDoneState()){
			if(!newChildNode.isDoneState())
				return "The graph cannot except done edges going to none done-states .\n";
		}
		if(newChildNode.isDoneState()){
			if(!childNode.isDoneState())
				return "You cannot have an edge that isn't \"done button pressed \" go to a done state.\n";
		}
		
		//for rewiring a buggylink:
		boolean oldChildBuggy = childNode.isBuggyNode();
		boolean newChildBuggy = newChildNode.isBuggyNode();
		if(oldChildBuggy && !newChildNode.isLeaf()){
			return "You cannot have a buggy link lead to a state that has outgoing edges.\n";
		}
		if(newChildBuggy && !oldChildBuggy){
			return "You cannot have a state with incoming correct and incorrect links.\n";
		}
		if(oldChildBuggy & !newChildBuggy){
			if((newChildNode.getInDegree() >0) && (newChildNode.getOutDegree() == 0))
				return "You cannot have a state with incoming correct and incorrect links.\n";
		}
		
		
		if(this.getProblemGraph().doesEdgeExist(parentNode, newChildNode)){
			return "There already exists an edge between " + parentNode.getName() + " and " + newChildNode.getName() + ". Drag the link to a different state.\n";
		}
		Vector ancestorNodesList = new Vector();
		findAncestorNodesListIgnoringLinkX(parentNode, ancestorNodesList, problemEdge);
        if(newChildNode.getProblemModel().testNodeInVector(newChildNode, ancestorNodesList)){
			return "Changing the destination of " + problemEdge.getEdgeData().getName() + " to " + newChildNode.getName()+ " would create a cycle in the graph. Drag the link to a different state.\n";
		}
    	return null;
    }
    public String testNewSourceNodeForLink(ProblemEdge problemEdge, ProblemNode newSourceNode){
		ProblemNode childNode =problemEdge.getDest();
		if(newSourceNode.equals(problemEdge.getSource()))
			return "ignore";
    	if(childNode.equals(newSourceNode)){
			return "You cannot set the source to be the same as the destination. Drag the link to a different state.\n";
		}
		if(newSourceNode.isDoneState()){
			return "You cannot have the source of a link be a done state. Drag the link to a different state.\n";
		}

		if(newSourceNode.isBuggyNode()){
				return "You cannot have the source of a link be a buggy state. Drag the link to a different state.\n";
		}
		
		if(this.getProblemGraph().doesEdgeExist(newSourceNode, childNode)){
			return "There already exists an edge between " + newSourceNode.getName() + " and " + childNode.getName() + ". Drag the link to a different state.\n";
		}
		Vector ancestorNodesList = new Vector();
		findAncestorNodesListIgnoringLinkX(newSourceNode, ancestorNodesList, problemEdge);
        if(newSourceNode.getProblemModel().testNodeInVector(childNode, ancestorNodesList)){
			return "Changing the source of " + problemEdge.getEdgeData().getName() + " to " + newSourceNode.getName()+ " would create a cycle in the graph. Drag the link to a different state.\n";
		}
    	return null;
    }
    // search down from atNode to find matched state in correctEdgesListCurrCopy
    void findMatchedStateNode(ProblemNode atNode,
            Vector correctEdgesListCurrCopy,
            /* Vector correctEdgesListMatchCopy, */
            Vector findMatchNodes) {
        // no further match needed
        if (correctEdgesListCurrCopy.size() == 0)
            return;

        ProblemEdge tempEdge;
        EdgeData myEdge;
        Enumeration iterOutEdge = getProblemGraph().getOutgoingEdges(atNode);
        while (iterOutEdge.hasMoreElements()) {
            tempEdge = (ProblemEdge) iterOutEdge.nextElement();
            myEdge = tempEdge.getEdgeData();

            if (myEdge.getActionType().equalsIgnoreCase(
                    EdgeData.CORRECT_ACTION)) {
                Vector correctEdgesListCurrP = (Vector) correctEdgesListCurrCopy
                        .clone();
                if (findMatchEdgeAndUpdateList(tempEdge, correctEdgesListCurrP)) {
                    if (correctEdgesListCurrP.size() == 0) {
                        addNodeToMatchedNodes(
                                tempEdge.getNodes()[ProblemEdge.DEST],
                                findMatchNodes);
                        continue;
                    }
                    // correctEdgesListMatchCopy.addElement(tempEdge);
                    findMatchedStateNode(tempEdge.getNodes()[ProblemEdge.DEST],
                            correctEdgesListCurrP, findMatchNodes);

                } else if (testInList(tempEdge, correctEdgesListCurrP)) {
                    // correctEdgesListMatchCopy.addElement(tempEdge);
                    findMatchedStateNode(tempEdge.getNodes()[ProblemEdge.DEST],
                            correctEdgesListCurrP, findMatchNodes);
                } else
                    continue;
            } else
                findMatchedStateNode(tempEdge.getNodes()[ProblemEdge.DEST],
                        correctEdgesListCurrCopy, findMatchNodes);
        }

        return;
    }

    public ProblemEdge findMatchSAIChildEdge(ProblemNode atNode,
            ProblemEdge edgeTest) {
        ProblemEdge edgeTemp;

        Enumeration iter = this.getProblemGraph().getOutgoingEdges(atNode);
        while (iter.hasMoreElements()) {
            edgeTemp = (ProblemEdge) iter.nextElement();
            if (compareTwoStatesSame(edgeTemp, edgeTest, true))
                return edgeTemp;
        }

        return null;
    }

    // find the list of edges from the atNode to the startNode
    void findEdgesList(ProblemNode atNode, Vector correctEdgesList,
            ProblemEdge matchedStateEdge) {
        if (atNode == getStartNode())
            return;

        ProblemEdge tempEdge;

        Enumeration iter = this.getProblemGraph().getConnectingEdges(atNode);
        while (iter.hasMoreElements()) {
            tempEdge = (ProblemEdge) iter.nextElement();

            if (tempEdge.getNodes()[ProblemEdge.SOURCE] != atNode) {

                if (!testSameStateEdges(matchedStateEdge, tempEdge))
                    addToList(correctEdgesList, tempEdge);

                findEdgesList(tempEdge.getNodes()[ProblemEdge.SOURCE],
                        correctEdgesList, matchedStateEdge);
                return;
            }
        }

        return;
    }

    // zz add 10/27/03: testing if two Edges are the same state based on
    // selection & action
    boolean testSameStateEdges(ProblemEdge edge1, ProblemEdge edge2) {
        EdgeData myEdge1 = edge1.getEdgeData();
        EdgeData myEdge2 = edge2.getEdgeData();

        String tempSelectionName1 = myEdge1.getSelection()
                .toString();
        String tempSelectionName2 = myEdge2.getSelection()
                .toString();

        String tempActionName1 = myEdge1.getAction().toString();
        String tempActionName2 = myEdge2.getAction().toString();

        if (tempSelectionName1.equalsIgnoreCase(tempSelectionName2)
                && tempActionName1.equalsIgnoreCase(tempActionName2))
            return true;

        return false;
    }

    // zz add 10/27/03
    // if addedEdge is not on addToEdgesList, then add addedEdge to
    // addToEdgesList
    void addToList(Vector addToEdgesList, ProblemEdge addedEdge) {
        ProblemEdge tempEdge;

        int addToEdgesListSize = addToEdgesList.size();
        boolean flag = true;

        for (int i = 0; flag && (i < addToEdgesListSize); i++) {
            tempEdge = (ProblemEdge) addToEdgesList.elementAt(i);

            if (testSameStateEdges(addedEdge, tempEdge))
                flag = false;
        }

        if (flag)
            addToEdgesList.addElement(addedEdge);

    }

    // find edges in the state graph with the same selection-action-input as
    // atEdge
    Vector findSameTripleEdges(ProblemEdge atEdge) {
        if (atEdge == null)
            return null;

        EdgeData atMyEdge = atEdge.getEdgeData();

        return findSameTripleEdges(atMyEdge.getSelection(),
                atMyEdge.getAction(), atMyEdge
                        .getInput());

    }

    // find edges in the state graph with the same values of selectionP,
    // actionP, inputP
    public Vector findSameTripleEdges(Vector selectionP, Vector actionP,
            Vector inputP) {
        Vector matchedEdges = new Vector();
        ProblemEdge tempEdge;
        EdgeData myEdge;

        Enumeration iter = this.getProblemGraph().edges();
        while (iter.hasMoreElements()) {
            tempEdge = (ProblemEdge) iter.nextElement();
            myEdge = tempEdge.getEdgeData();

            if (matchStates(myEdge, selectionP, actionP, inputP))
                matchedEdges.addElement(tempEdge);

        }

        return matchedEdges;
    }

    // find edges with the first defined Production Rule the same as
    // ruleLabelText
    public Vector findSameProductionSetsEdge(String ruleLabelText) {
        Vector matchedEdges = new Vector();

        ProblemEdge tempEdge;
        EdgeData tempMyEdge;
        int tempNumberOfRules;
        RuleLabel tempRuleLabel;
        boolean continueFlag;

        Enumeration iter = this.getProblemGraph().edges();
        while (iter.hasMoreElements()) {
            tempEdge = (ProblemEdge) iter.nextElement();
            tempMyEdge = tempEdge.getEdgeData();
            tempNumberOfRules = tempMyEdge.getRuleLabels().size();

            continueFlag = true;
            for (int i = 0; i < tempNumberOfRules && continueFlag; i++) {
                tempRuleLabel = (RuleLabel) tempMyEdge.getRuleLabels().elementAt(i);
                if (tempRuleLabel.isNameSet()) {
                    if (tempRuleLabel.getText().equals(ruleLabelText))
                        matchedEdges.addElement(tempEdge);

                    continueFlag = false;
                }
            }
        }

        return matchedEdges;
    }

    // if the child state from the currNode state has the same selection,
    // action, input
    // then return the matched child node, otherwise return null
    public ProblemNode findSameChildState(ProblemNode currNode,
            Vector selectionP, Vector actionP, Vector inputP) {

        ProblemEdge edgeTemp;
        EdgeData myEdge;

        ProblemNode nodeTemp;

        Enumeration iter = this.getProblemGraph().getConnectingEdges(currNode);
        while (iter.hasMoreElements()) {
            edgeTemp = (ProblemEdge) iter.nextElement();
            myEdge = edgeTemp.getEdgeData();
            nodeTemp = edgeTemp.getNodes()[ProblemEdge.DEST];
            if (nodeTemp != currNode) {
                if (matchStates(myEdge, selectionP, actionP, inputP))
                    return nodeTemp;
            }
        }

        return null;
    }

    /**
     * Tests the given selection, action, input against the 
     * outgoing edges of the given node to see if any 
     * of them match.  Returns that edge if true,
     * otherwise null.
     * 
     * @param selection
     * @param action
     * @param input
     * @param node
     * @return
     */
    public ProblemEdge findMatchingEdge(
			Vector selection, 
			Vector action, 
			Vector input, 		
			ProblemNode node) 
    {
    	return findMatchingEdge(selection,action,input,"Student", node);
    }
    public ProblemEdge findMatchingEdge(
									Vector selection, 
									Vector action, 
									Vector input, 
									String actor,
									ProblemNode node) {
		ProblemEdge tempEdge;
		ProblemEdge matchedEdge = null;
		
		// find the preferred or correct match 
        Enumeration iterEdges = getProblemGraph().getOutgoingEdges(node);
        while (iterEdges.hasMoreElements()) {
			tempEdge = (ProblemEdge) iterEdges.nextElement();

            if (matchStates(tempEdge, selection, action, input, actor)) {
				// get the correct match
				if (tempEdge.isCorrect()) {
					matchedEdge = tempEdge;
				
					// take the preferred edge
					if (matchedEdge.isPreferredEdge())
						return matchedEdge;
				}
            }
           
        }
		
		if (matchedEdge != null)
			return matchedEdge;

		// find buggy match
		iterEdges = getProblemGraph().getOutgoingEdges(node);
        while (iterEdges.hasMoreElements()) {
			tempEdge = (ProblemEdge) iterEdges.nextElement();

            if (matchStates(tempEdge, selection, action, input, actor)) {
				matchedEdge = tempEdge;
				if (matchedEdge.isCorrectorFireableBuggy())
					return matchedEdge;
            }
        }
		
		return matchedEdge;
    }

    
    public boolean compareTwoStatesSame(ProblemEdge edge1, ProblemEdge edge2,
            boolean compareInputFlag) {
        if (edge1 == null || edge2 == null)
            return false;

        EdgeData myEdge1 = edge1.getEdgeData();
        EdgeData myEdge2 = edge2.getEdgeData();

        if (myEdge1 == null || myEdge2 == null)
            return false;

        return compareTwoStatesSame(myEdge1, myEdge2, compareInputFlag);
    }

    // if both myEdges have the same selection, action and input (if
    // compareInputFlag is true),
    // then return true, otherwise return false
    public boolean compareTwoStatesSame(EdgeData myEdge1, EdgeData myEdge2,
            boolean compareInputFlag) {
        Vector selection2 = myEdge2.getSelection();
        Vector action2 = myEdge2.getAction();

        // zz 02/12/04: Vector input2 = null; //myEdge2.actionLabel.getInput();

        Vector input2 = myEdge2.getInput();

        if (compareInputFlag) {
            boolean inputCompare;

            if (isCaseInsensitive())
                inputCompare = (myEdge1.getInput().toString()
                        .equalsIgnoreCase(input2.toString()));
            else
                inputCompare = (myEdge1.getInput().toString()
                        .equals(input2.toString()));

            if (!inputCompare)
                return false;
        }

        if (myEdge1.getSelection().toString().equalsIgnoreCase(
                selection2.toString())
                && myEdge1.getAction().toString().equalsIgnoreCase(
                        action2.toString()))
            return true;

        return false;
    }

    public boolean matchStates(ProblemEdge atEdge, Vector selectionP,
            Vector actionP, Vector inputP) {

        EdgeData edge = atEdge.getEdgeData();

        return matchStates(edge, selectionP, actionP, inputP,"Student");
    }
    public boolean matchStates(ProblemEdge atEdge, Vector selectionP,
            Vector actionP, Vector inputP, String actor) {

        EdgeData edge = atEdge.getEdgeData();

        return matchStates(edge, selectionP, actionP, inputP, actor);
    }

    /**
     * Call the matcher for this edge with the given selection, action and input
     * 
     * @param edge
     * @param selectionP
     * @param actionP
     * @param inputP
     * @return true if matched, false otherwise
     */
    public boolean matchStates(EdgeData edge, Vector selectionP,
            Vector actionP, Vector inputP){
    	return matchStates(edge, selectionP, actionP,inputP, "Student");
    }
    public boolean matchStates(EdgeData edge, Vector selectionP,
            Vector actionP, Vector inputP, String actorP) {

    	//trace.out("entered matchStates");
    	
        edge.getMatcher().setUseAlgebraicEquivalence(isUseCommWidgetFlag());
        edge.getMatcher().setCaseInsensitive(isCaseInsensitive());
        if (trace.getDebugCode("lispcheckresult")) trace.out("lispcheckresult", "MATCH STATES: case insensitive = " + isCaseInsensitive() + 
        		" input = " + inputP + " matcher input = " + edge.getMatcher().getInput());
        boolean match = false;

        
/*        if (controller.getMissController().getSimSt().getInputMatcher()==null){
	        	//CTAT was not called with a ssInputMatcher parameter*/
        	
        	
	        if (edge.getMatcher().getMatcherType().equals("Exact Match"))
	        {
	        	//trace.out(" Exact Match         REMOVE THIS");
	        	match = edge.getMatcher().match(selectionP,actionP, inputP, actorP);
	        }
	        else {
	        	//trace.out(" NOT Exact Match         REMOVE THIS");
	        	match= edge.getMatcher().match(selectionP, actionP, inputP);	
	        }
        
/*        else {
        	//an input-matcher is specified
        	match= edge.getMatcher().match(selectionP, actionP, inputP);
        } */
        
        //Exception-throwing code
        /*try{
        	int b = 1/0;
        }
        catch(Exception e){
        	e.printStackTrace();
        }*/
        
        return match;
        
    }
 
    
    /**
     * @param startNodeCreatedFlag
     *            The startNodeCreatedFlag to set.
     */
    public void setStartNodeCreatedFlag(boolean startNodeCreatedFlag) {
//    	trace.printStack("sp", "setStartNodeCreatedFlag(): was "+
//    			this.startNodeCreatedFlag+", now "+startNodeCreatedFlag);
        this.startNodeCreatedFlag = startNodeCreatedFlag;
    }

    /**
     * @return Returns the startNodeCreatedFlag.
     */
    public boolean getStartNodeCreatedFlag() {
        return startNodeCreatedFlag;
    }

    /**
     * @param startNodeMessageVector copy this list into {@link #startNodeMessageVector}
     */
    public void setStartNodeMessageVector(Vector startNodeMessageVector) {
        this.startNodeMessageVector = new ArrayList<MessageObject>(startNodeMessageVector);
    	if (trace.getDebugCode("sp")) trace.out("sp", "SETTING START STATE vector; size "+
    			(startNodeMessageVector == null ? -1 : startNodeMessageVector.size()));
    	if (trace.getDebugCode("startstate")) trace.out("startstate", "SETTING START STATE vector; size "+
    			(startNodeMessageVector == null ? -1 : startNodeMessageVector.size()));
    }

    /**
     * Add the given message to the {@link #getStartNodeMessageVector()}.
     * Will insert the message just before the StartStateEnd message.
     * @param o message to append
     */
    public void appendStartNodeMessage(MessageObject o) {

		
		
    	List<MessageObject> snmv = getStartNodeMessageVector();
    	if (snmv == null) {
    		setStartNodeMessageVector(new Vector());
    		snmv = getStartNodeMessageVector();
    	}
    	for (int i = snmv.size()-1; i >= 0; i--) {
    		MessageObject oi = snmv.get(i);
    		if (oi == null) continue;
    		if ("StartStateEnd".equalsIgnoreCase(oi.getMessageType())) {
    			snmv.add(i, o);
    			return;
    		}
    	}
    	snmv.add(o);
    	return;
    }

    /**
     * Public access to the start state messages. Returns 
     * {@link UniversalToolProxy#startNodeMessagesIterator(ProblemModel)} if available.
     * Else returns {@link #startNodeMessagesIteratorForStartStateModel()}.
     * @return see above
     */
    public Iterator<MessageObject> startNodeMessagesIterator() {
    	if(getController() != null && getController().getUniversalToolProxy() != null) {
    		Iterator<MessageObject> result =
    				getController().getUniversalToolProxy().startNodeMessagesIterator(this);
    		if(result != null)
    			return result;
    	}
    	return startNodeMessagesIteratorForStartStateModel();
    }

    /**
     * Access to the contents of {@link #getStartNodeMessageVector()}.
     * @return Iterator on {@link #getStartNodeMessageVector()}; empty iterator if null
     */
    public Iterator<MessageObject> startNodeMessagesIteratorForStartStateModel() {
    	if(getStartNodeMessageVector() == null)
    		return EmptyIterator.instance();
    	return getStartNodeMessageVector().iterator();
    }
    
    /**
     * @return Returns the startNodeMessageVector.
     */
    private List<MessageObject> getStartNodeMessageVector() {
        return startNodeMessageVector;
    }

	/**
	 * @param message add this to {@link #getStartNodeMessageVector()}
	 */
	public void addStartNodeMessage(MessageObject message) {	
		if(getStartNodeMessageVector() != null)
			getStartNodeMessageVector().add(message);
	}

    /**
     * @param checkAllEdges
     *            The checkAllEdges to set.
     */
    public void setCheckAllEdges(Vector checkAllEdges) {
        this.checkAllEdges = checkAllEdges;
    }

    /**
     * @return Returns the checkAllEdges.
     */
    public Vector getCheckAllEdges() {
        return checkAllEdges;
    }

    /**
     * @param checkAllNodes
     *            The checkAllNodes to set.
     */
    public void setCheckAllNodes(Vector checkAllNodes) {
        this.checkAllNodes = checkAllNodes;
    }

    /**
     * @return Returns the checkAllNodes.
     */
    public Vector getCheckAllNodes() {
        return checkAllNodes;
    }

 
    /**
     * @param problemName
     *            The problemName to set.
     */
    public void setProblemName(String problemName) {
    	//trace.out("mg", "ProblemModel (setProblemName): setting " + this.problemName
    	//			+ " to " + problemName);
        this.problemName = problemName;
        if (problemName != null && problemName.length() > 0)
        	problemSummary = new ProblemSummary(problemName, null,
        			getSuppressStudentFeedback() == FeedbackEnum.HIDE_ALL_FEEDBACK);
    }

    /**
     * @return Returns the problemName.
     */
    public String getProblemName() {
        return problemName;
    }

    /**
     * @param problemFullName
     *            The problemFullName to set.
     */
    public void setProblemFullName(String problemFullName) {
        this.problemFullName = problemFullName;
    }

    /**
     * @return Returns the problemFullName.
     */
    public String getProblemFullName() {
        return problemFullName;
    }
 
    /** Set the course name in this object and in {@link Logger}. */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
        controller.getLogger().setCourseName(courseName);
    }

    /** Set the unit name in this object and in {@link Logger}. */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
        controller.getLogger().setUnitName(unitName);
    }

    /** Set the section name in this object and in {@link Logger}. */
    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
        controller.getLogger().setSectionName(sectionName);
    }

    /**
     * @return Returns the courseName.
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * @return Returns the unitName.
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * @return Returns the sectionName.
     */
    public String getSectionName() {
        return sectionName;
    }

    /**
     * @param startNode
     *            The startNode to set.
     */
    public void setStartNode(ProblemNode startNode) {
        this.startNode = startNode;
    }

    /**
     * @return Returns the startNode.
     */
    public ProblemNode getStartNode() {
        return (ProblemNode) this.startNode;
    }
    
    public GroupEditorContext getEditContext() {
    	return editContext;
    }

	/**
	 * Find a node by name.  This method is linear in the number of nodes
	 * in the graph.  
	 * @param name node name to seek
	 * @return 1st node returned by {@link ProblemGraph#nodes()} with
	 *         which getName() matches name; null if none matches
	 */
	public ProblemNode getNode(String name) {
		return getProblemGraph().getNode(name);
	}

	/**
	 * Convenience method to return the {@link ProblemNode} from {@link #exampleTracerGraph}
	 * given its node identifier.
	 * @param nodeID
	 * @return null if {@link #getExampleTracerGraph()} null or nodeID not found; else result of
	 *     {@link ExampleTracerGraph#getNode(int)}.{@link ExampleTracerNode#getProblemNode() getProblemNode()}
	 */
	public ProblemNode getProblemNode(int nodeID) {
		ExampleTracerNode etnode = getExampleTracerGraph().getNode(nodeID);
		if (etnode == null)
			return null;
		else
			return etnode.getProblemNode();
	}
	
    /**
     * @param problemGraph
     *            The problemGraph to set.
     */
    public void setProblemGraph(ProblemGraph problemGraph) {
        this.problemGraph = problemGraph;
    }

    /**
     * @return Returns the problemGraph.
     */
    public ProblemGraph getProblemGraph() {
        return problemGraph;
    }


    public boolean getLockWidget() {
        return this.isLockWidget();
    }

    /**
     * @param lockWidget
     *            The lockWidget to set.
     */
    public void setLockWidget(boolean lockWidget) {
        this.lockWidget = lockWidget;
    }

    /**
     * @return Returns the lockWidget.
     */
    public boolean isLockWidget() {
        return lockWidget;
    }
    
   
    public void setHintPolicy(HintPolicyEnum policy) {
    	hintPolicy = policy;
    }
    
    public HintPolicyEnum getHintPolicy() {
    	return hintPolicy;
    }
    
    public boolean areHintsBiasedByPriorError() {
    	return hintPolicy.isBiasedByPriorError();
    }
    
    public boolean areHintsBiasedByCurrentSelection() {
    	return hintPolicy.isBiasedByCurrentSelection();
    }
    
    public ExampleTracerGraph getExampleTracerGraph() {
    	Boolean isUnordered = (controller == null ? Boolean.FALSE :
    		controller.getPreferencesModel().getBooleanValue(BR_Controller.COMMUTATIVITY));
    	if(exampleTracerGraph==null){
    		exampleTracerGraph =
    				new ExampleTracerGraph(isUnordered == null ? false : isUnordered.booleanValue(), false);
    		//addProblemModelListener(controller.pseudoTutorMessageHandler);
    	}
    	return exampleTracerGraph;
	}

	public void setAllowToolMode(boolean toolMode) {
		
//        ProblemModelEvent evt 
//        		= new ProblemModelEvent (this, 
//										ProblemModelEvent.SET_ALLOW_TOOL_MODE,
//										new Boolean (toolMode), 
//										new Boolean (toolMode));
	//	trace.out("AllowToolMode:"+toolMode);
        controller.getPreferencesModel().setBooleanValue(BR_Controller.ALLOW_TOOL_REPORTED_ACTIONS, new Boolean (toolMode));
      //  trace.out(controller.getPreferencesModel().getValue(BR_Controller.ALLOW_TOOL));
//		fireProblemModelEvent(evt);
    }
  public void setMaxStudents(int num) {
        // ProblemModelEvent evt
        // = new ProblemModelEvent (this,
        // ProblemModelEvent.MAX_STUDENT,
        // new Integer(num),
        // new Integer(num));

        controller.getPreferencesModel().setIntegerValue(
                BR_Controller.MAX_STUDENT, num);

        //fireProblemModelEvent(evt);

    }

    /**
     * @return Returns the {@link #unorderedMode}.
     */
    public boolean isUnorderedMode() {	
		return !getExampleTracerGraph().getGroupModel().isGroupOrdered(
					getExampleTracerGraph().getGroupModel().getTopLevelGroup());
    }

    /**
     * @param linksGroups
     *            The linksGroups to set.
     */
    public void setLinksGroups(Vector linksGroups) {
        this.linksGroups = linksGroups;
    }

    /**
     * @return Returns the linksGroups.
     * @deprecated revise for new example tracer
     */
    public Vector getLinksGroups() {
    	if (needToRedoLinksGroups) {
    		if (linksGroups == null)
    			linksGroups = new Vector();
    		else
    			linksGroups.clear();
    		for (LinkGroup group : getExampleTracerGraph().getGroupModel().getGroupSubgroups(getExampleTracerGraph().getGroupModel().getTopLevelGroup())) {
    			Vector singleGroup = new Vector();
    			singleGroup.add(getExampleTracerGraph().getGroupModel().getGroupName(group));
    			for (ExampleTracerLink link : getExampleTracerGraph().getGroupModel().getGroupLinks(group)) {
    				ProblemEdge edge = link.getEdge().getEdge();
    				singleGroup.add(edge);
    			}
    			linksGroups.add(singleGroup);
    		}
    		if (trace.getDebugCode("br")) trace.out("br", "redo linksGroups "+linksGroups);
    		needToRedoLinksGroups = false;    		
    	}
        return linksGroups;
    }

 
    /**
     * @param willDeleteLinks
     *            The willDeleteLinks to set.
     */
    public void setWillDeleteLinks(Vector willDeleteLinks) {
        this.willDeleteLinks = willDeleteLinks;
    }

    /**
     * @return Returns the willDeleteLinks.
     */
    public Vector getWillDeleteLinks() {
        return willDeleteLinks;
    }

    /**
     * @param willRemovedLinkGroups
     *            The willRemovedLinkGroups to set.
     */
    public void setWillRemovedLinkGroups(Vector willRemovedLinkGroups) {
        this.willRemovedLinkGroups = willRemovedLinkGroups;
    }

    /**
     * @return Returns the willRemovedLinkGroups.
     */
    public Vector getWillRemovedLinkGroups() {
        return willRemovedLinkGroups;
    }

    /**
     * Sets the {@link #caseInsensitive}.
     * @param caseInsensitive new value
     */
    public void setCaseInsensitive(boolean caseInsensitive) {
    	this.caseInsensitive = caseInsensitive;
    }

    /**
     * @return Returns the {@link #caseInsensitive}.
     */
    public boolean isCaseInsensitive() {
    	return caseInsensitive;
    }

    public void setUseCommWidgetFlag(boolean useCommWidgetFlag) {
        this.useCommWidgetFlag = useCommWidgetFlag;
    }

    /**
     * @return Returns the useCommWidgetFlag.
     */
    public boolean isUseCommWidgetFlag() {
        return useCommWidgetFlag;
    }

    // debug test
    static void printoutESEGraph(final Vector ESEGraph) {
        int nodesNum = ESEGraph.size();
        trace.out("Ese-Graph size: " + nodesNum);

        if (nodesNum > 0) {
            Vector parentChildList;
            Vector childrenList;
            Vector childNode;
            int childrenNum;

            Integer uniqeID;
            Vector tempVector;

            for (int i = 0; i < nodesNum; i++) {
                parentChildList = (Vector) ESEGraph.elementAt(i);

                if (parentChildList.size() > 1)
                    childrenList = (Vector) parentChildList.elementAt(1);
                else
                    childrenList = new Vector();

                childrenNum = childrenList.size();

                if (trace.getDebugCode("test")) trace.out("test", "ZZZ node "
                        + (String) parentChildList.elementAt(0) + " has "
                        + childrenNum + " children: ");

                for (int j = 0; j < childrenNum; j++) {
                    childNode = (Vector) childrenList.elementAt(j);
                    if (trace.getDebugCode("test")) trace.out("test", "Node: "
                            + (String) childNode.elementAt(0));

                    uniqeID = (Integer) childNode.elementAt(1);
                    trace.out("UniqeID: " + uniqeID.intValue());

                    if (trace.getDebugCode("test")) trace.out("test", "AuthorIntent: "
                            + (String) childNode.elementAt(2));

                    tempVector = (Vector) childNode.elementAt(3);
                    trace.out("Selection: " + tempVector.toString());

                    tempVector = (Vector) childNode.elementAt(4);
                    trace.out("Action: " + tempVector.toString());

                    tempVector = (Vector) childNode.elementAt(5);
                    trace.out("Input: " + tempVector.toString());
                }
                trace.out("XXXXXXXXXXXXXXXXXXXXXX");
            }
        } else
            trace.out("No Problem Graph is built yet.");
    }

    /**
     * @param problemLoadedFromLispTutor
     *            The problemLoadedFromLispTutor to set.
     */
    public void setProblemLoadedFromLispTutor(boolean problemLoadedFromLispTutor) {
        this.problemLoadedFromLispTutor = problemLoadedFromLispTutor;
    }
	
    /**
     * @return Returns the problemLoadedFromLispTutor.
     */
    public boolean isProblemLoadedFromLispTutor() {
        return problemLoadedFromLispTutor;
    }
    
    /**
     * @param searchPathFlag
     *            The searchPathFlag to set.
     */
    public void setSearchPathFlag(boolean searchPathFlag) {
        this.searchPathFlag = searchPathFlag;
    }

    /**
     * @return Returns the searchPathFlag.
     */
    public boolean isSearchPathFlag() {
        return searchPathFlag;
    }


    public void addProblemModelListener(ProblemModelListener l) {
        listeners.add(l);
    }

    public void removeProblemModelListener (ProblemModelListener l) {
        listeners.remove(l);
    }

    /**
     * Send the given event to all on the {@link #listeners} list.
     * @param e event to send
     */
    public void fireProblemModelEvent (ProblemModelEvent e) {
    	//trace.out("Firing event");
    	//trace.out(e.toString());
    	//if(exampleTracerGraph!=null)
        //	exampleTracerGraph.problemModelEventOccurred(e);
    	try {
    		Iterator i = listeners.iterator();
    		while (i.hasNext()) {
    			ProblemModelListener listener = (ProblemModelListener) i.next();
    			listener.problemModelEventOccurred(e);
    		}
    	} catch (ConcurrentModificationException cme) {
        	// sewall 2011/12/01 error handling here: don't allow concurrent mod ex to
        	// kill thread: can happen during init'zation as listeners are registering
    		trace.errStack("Continuing after error while firing ProblemModelEvent("+e+")", cme);
    	}
    }
    
    /**
     * @param thisNode
     */
    public void removeNode(ProblemNode thisNode, boolean fireEvent) {
        problemGraph.removeNode(thisNode);
        trace.out ("XXXX remove node !!!");
        if(fireEvent){
        	NodeDeletedEvent e = new NodeDeletedEvent (thisNode);
			fireProblemModelEvent(e);
        }
		
    }
    
    /**
     * Reinitialize the problem model.
     * @param problemName {@link #problemName} to restore
     * @param problemFullName {@link #problemFullName} to restore
     */
    public void reset(String problemName, String problemFullName) {
        init(null);  // null => don't change existing controller
        setProblemName(problemName);
        setProblemFullName(problemFullName); 
    }


    /**
     * Set {@link #variableTable}. This is only called after using 
     * PseudoTutorMessageHandler and ExampleTracer functions.
     * @param vt new value for {@link #variableTable}
     */
    public void setVariableTable(VariableTable vt) {
    	if (trace.getDebugCode("vt")) trace.outNT("vt", "ProblemModel.setVariableTable() oldVT = VariableTable #"+
    			(variableTable==null? "null":variableTable.getInstance())+" newVT = VariableTable #"+
    			(vt == null ? "null" : vt.getInstance()));
    	VariableTable vtOld = variableTable;
    	variableTable = vt;
    	getCtatFunctions().setVariableTable(vt);
    	ProblemModelEvent vtChange = 
    		new VariableTableChangeEvent(this,"VariableTable",vtOld,variableTable);
    	fireProblemModelEvent(vtChange);
    }

    /**
     * @return value of {@link #variableTable}
     */
    public VariableTable getVariableTable() {
        return variableTable;
    }

    /**
     * Assign a value to {@link #variableTable} and all interpretations' variable tables.
     * @param key variable name
     * @param value variable value
     * @return result of
     *         {@link #variableTable}.{@link VariableTable#put(Object, Object) put(key, value)}
     */
    public Object assignVariable(String key, Object value) {
    	if (trace.getDebugCode("vt")) trace.outNT("vt", "PM.assignVariable("+key+") PM.variableTable #"+
    			this.variableTable.getInstance()+", PM.getVariableTable() #"+
    			getVariableTable().getInstance());
    	VariableTable variableTable = getVariableTable();
    	if (variableTable == null || key == null)
    		return null;
    	Object result = variableTable.put(key, value);
    	ExampleTracerTracer tracer =
    		(getExampleTracerGraph() == null ? null : getExampleTracerGraph().getExampleTracer());
    	if (tracer != null)
    		tracer.assignVariable(key, value);
    	return result;
    }

    public int getNodeCount() {
	return problemGraph.getNodeCount();
    }

    public int getEdgeCount() {
	return problemGraph.getEdgeCount();
    }

	/**
     * Find the preferred path from a given node to an end state. 
     * @param atNode starting node
     * @return list of edges
     */
    public Vector<ProblemEdge> findPathForProblemSkillsMatrix(ProblemNode atNode) {
    	Vector<ProblemEdge> pathEdges = new Vector<ProblemEdge>();
    	findPathForProblemSkillsMatrix(atNode, pathEdges);
    	return pathEdges;
    }
    
    /**
     * Find the preferred path from a given node to an end state. Recursive algorithm:<ol>
     * <li>add the given node's preferred outgoing link to the accumulating list and</li>
     * <li>recurse down the graph from that link's destination node</li>
     * </ol> 
     * @param atNode starting node
     * @param pathEdges accumulates result
     */
    private void findPathForProblemSkillsMatrix(ProblemNode atNode, Vector<ProblemEdge> pathEdges) {
        if (atNode == null)
            return;
        ProblemGraph problemDGraph = getProblemGraph();
        Enumeration iterEdges = problemDGraph.getOutgoingEdges(atNode);
        if (!iterEdges.hasMoreElements())
            return;

        ProblemEdge tempEdge;
        EdgeData myEdge;
        ProblemEdge foundEdge = null;

        while (iterEdges.hasMoreElements()) {
            tempEdge = (ProblemEdge) iterEdges.nextElement();
            myEdge = tempEdge.getEdgeData();

            if (myEdge.isPreferredEdge()) {
                pathEdges.addElement(tempEdge);
                findPathForProblemSkillsMatrix(tempEdge.getNodes()[ProblemEdge.DEST], pathEdges);
                return;
            } else if (myEdge.getActionType()
                    .equalsIgnoreCase(EdgeData.CORRECT_ACTION))
                foundEdge = tempEdge;
            else if (myEdge.getActionType().equalsIgnoreCase(
                    EdgeData.FIREABLE_BUGGY_ACTION)
                    && foundEdge == null)
                foundEdge = tempEdge;
        }
        if (foundEdge != null) {
        	// pathEdges.addElement(foundEdge);  if want to fill gaps in preferredEdge path
            findPathForProblemSkillsMatrix(foundEdge.getNodes()[ProblemEdge.DEST], pathEdges);
        }
        return;
    }

    public void setSuppressStudentFeedback(FeedbackEnum suppressFeedback) {
        if (trace.getDebugCode("br"))
        	trace.out("br", "set suppress feedback: " + suppressFeedback);
        this.suppressStudentFeedback = suppressFeedback;
    }

    public FeedbackEnum getSuppressStudentFeedback() {
    	return suppressStudentFeedback;
    }

	/**
	 * @return {@link #controller}
	 */
	public BR_Controller getController() {
		return controller;
	}

	/**
	 * Convenience method for access to {@link BR_Controller#getFormulaParser()}
	 * @return {@link #controller}.getFormulaParser() or null
	 */
	public Parser getFormulaParser() {
		if (controller == null)
			return null;
		else
			return controller.getFormulaParser();
	}

	/**
	 * @return the {@link #firstNode}
	 */
	public boolean isFirstNode() {
		return firstNode;
	}

	/**
	 * @param firstNode new value for {@link #firstNode}
	 */
	void setFirstNode(boolean firstNode) {
		this.firstNode = firstNode;
	}

	/**
	 * @return the {@link #edgeUniqueIDGenerator}
	 */
	public int getEdgeUniqueIDGenerator() {
		return edgeUniqueIDGenerator;
	}

	/**
	 * @return the {@link #nodeUniqueIDGenerator}
	 */
	public int getNodeUniqueIDGenerator() {
		return nodeUniqueIDGenerator;
	}

	/**
	 * @param edgeUniqueIDGenerator new value for {@link #edgeUniqueIDGenerator}
	 */
	public void updateEdgeUniqueIDGenerator(int uniqueID) {
		edgeUniqueIDGenerator = Math.max(uniqueID, edgeUniqueIDGenerator);
	}

	/**
	 * @param nodeUniqueIDGenerator new value for {@link #nodeUniqueIDGenerator}
	 */
	public void updateNodeUniqueIDGenerator(int uniqueID) {
		nodeUniqueIDGenerator = Math.max(uniqueID, nodeUniqueIDGenerator);
	}

	/**
	 * @return the {@link #edgeUniqueIDGenerator}
	 */
	public int getNextEdgeUniqueIDGenerator() {
		++edgeUniqueIDGenerator;
		return edgeUniqueIDGenerator;
	}

	/**
	 * @return {@link #highlightRightSelection}
	 */
	public boolean getHighlightRightSelection() {
		return highlightRightSelection;
	}

	/**
	 * @param highlightRightSelection new value for {@link #highlightRightSelection}
	 */
	public void setHighlightRightSelection(boolean highlightRightSelection) {
		this.highlightRightSelection = highlightRightSelection;
	}

	/**
	 * Tell whether any of the PROPERTYVALUES values is {@link CTATFunctions#interpolatable(String)}.
	 * @param mo
	 * @return logical OR of interpolatable() on all values
	 */
	public static boolean interpolatable(MessageObject mo) {
		if (mo == null)
			return false;
		try {
			for (Object pv: mo.getPropertyValues()) {
				// trace.outln("functions", "pv = " + pv);
				if (pv instanceof Vector) {
					for (Object value: (Vector)pv) {
						if (value != null && CTATFunctions.interpolatable(value.toString()))
							return true;
					}
				} else if (pv!=null) {
					if (CTATFunctions.interpolatable(pv.toString()))
						return true;
				}
			}
		} catch (Exception de) {
			trace.err("Error getting PROPERTYVALUES: "+de+". MessageObject was:\n"+mo);
			return false;
		}
	
	    return false;
	}
    
    // //////////////////////////////////////////////////////////////////////
    // Counts the number of done states and returns the next appropriate name
    // for a done state
    // //////////////////////////////////////////////////////////////////////
    public String nextDoneName() {
    	ProblemGraph graph = getProblemGraph();
    	if (graph == null)
    		return ProblemGraph.DONE_NODE_NAME;
    	else
    		return graph.nextDoneName();
    }

    /**
     * For error conditions, return a response with an empty {@link ProblemSummary}.
     * @param dummyProblemName argument to ProblemSummary constructor: name for non-existent problem
     * @return message with ProblemSummary from {@link ProblemSummary#ProblemSummary(String, Skills, boolean)}
     */
    public static MessageObject makeEmptyProblemSummaryResponse(String dummyProblemName) {
    	return handleProblemSummaryRequest(new ProblemSummary(dummyProblemName, null, false));
    }
    
	/**
	 * @return the {@link #problemSummary}
	 */
	public ProblemSummary getProblemSummary() {
		RuleProduction.Catalog rpc = updateOpportunityCounts();
		List<RuleProduction> rules = (rpc == null ? null : rpc.getRuleProductionList(true, false));
		if (trace.getDebugCode("problemsummary"))
			trace.out("problemsummary", "getProblemSummary() ruleNames "+rules+", XML "+
					(problemSummary == null ? null : "\n "+problemSummary.toXML()));
		if (problemSummary == null) {
			String problemName = getProblemName();
			if (problemName == null || problemName.length() < 1) {
				problemName = "NoProblemDefined";
				problemSummary = new ProblemSummary(problemName, null,
						getSuppressStudentFeedback() == FeedbackEnum.HIDE_ALL_FEEDBACK);
			}
		}
		if (trace.getDebugCode("skills")) trace.outNT("skills", "getProblemSummary().getSkills() pre  "+
				(problemSummary.getSkills() == null ? null : problemSummary.getSkills().getAllSkills()));
		if (problemSummary.getSkills() == null) {
			if (rules == null || rules.size() < 1)
				return problemSummary;
			else {
				Skills skills = new Skills();
				skills.setVersion(getController().getCommShellVersion());
				for (RuleProduction rp : rules) {
					Skill skill = new Skill(rp.getDisplayName());
					skill.setLabel(rp.getLabel());
					skill.setDescription(rp.getDescription());
					skills.add(skill);
				}
				problemSummary.setSkills(skills);
			}
		}
		if (trace.getDebugCode("skills")) trace.outNT("skills", "getProblemSummary().getSkills() post "+
				(problemSummary.getSkills() == null ? null : problemSummary.getSkills().getAllSkills()));
	    return problemSummary;
	}

	/**
	 * Generate a message containing the current {@link #problemSummary}.
	 * @return Comm message {@link #problemSummary} in XML
	 */
	public MessageObject handleProblemSummaryRequest() {
		return handleProblemSummaryRequest(getProblemSummary());
	}

	/**
	 * Generate a message containing the current {@link #problemSummary}.
	 * @return Comm message {@link #problemSummary} in XML
	 */
	private static MessageObject handleProblemSummaryRequest(ProblemSummary ps) {
		MessageObject resp = MessageObject.create(MsgType.PROBLEM_SUMMARY_RESPONSE);
		resp.setVerb("NotePropertySet");
		String xml = "";
		if (ps != null)
			xml = ps.toXML();
		resp.setProperty(SCORM.LESSON_STATUS, SCORM.getLessonStatus(ps));
		resp.setProperty(SCORM.RAW_SCORE, SCORM.getRawScore(ps));
		resp.setProperty(SCORM.EXIT, SCORM.getExitReason(ps));
		resp.setProperty(SCORM.SESSION_TIME, SCORM.getSessionTime(ps));
		resp.setProperty("ProblemSummary", xml);
		resp.setProperty(MessageTank.END_OF_TRANSACTION, "true");
        return resp;
	}

	/**
	 * Method meant for saving the {@link #studentBeginsHereState} to the .brd file.
	 * @return the {@link #studentBeginsHereState}; if null, {@link #STUDENT_BEGINS_HERE_VAR}
	 */
	String getStudentBeginsHereNameForBRD() {
		if (studentBeginsHereState != null)
			return studentBeginsHereState.getName();
		else
			return STUDENT_BEGINS_HERE_VAR;
	}

	/**
	 * @return the {@link #studentBeginsHereState}; if null, {@link #getStartNode()}
	 */
	public ProblemNode getStudentBeginsHereState() {
		if (studentBeginsHereState != null)
			return studentBeginsHereState;
		else
			return getStartNode();
	}

	/**
	 * Access to {@link #setStudentBeginsHereState(ProblemNode)} by state name instead of ID.
	 * @param studentBeginsHereStateName name shown on graph
	 */
	public void setStudentBeginsHereState(String studentBeginsHereStateName) {
        ProblemNode node = getNode(studentBeginsHereStateName);
		setStudentBeginsHereState(node);  // ok if node null 
	}

	/**
	 * Change the {@link #studentBeginsHereState}. Side effect: replaces
	 * {@link #beforeStudentBeginsStates} with new set based on path
	 * from origin of graph to the given state. Also notifies nodes to be recolored.
	 * @param studentBeginsHereState new value for {@link #studentBeginsHereState};
	 *        if null, effect is {@link #studentBeginsHereState} == {@link #getStartNode()}
	 */
	public synchronized void setStudentBeginsHereState(ProblemNode studentBeginsHereState) {
		if (trace.getDebugCode("pm")) trace.out("pm", "setStudentBeginsHereState("+this.studentBeginsHereState+"=>"+
				studentBeginsHereState+")");
		if (this.studentBeginsHereState == studentBeginsHereState)
			return;
		Set<Integer> newBSBSs = new LinkedHashSet<Integer>();
		Set<ProblemNode> nodesToNotify = new HashSet<ProblemNode>();

		ExampleTracerGraph graph = getExampleTracerGraph();
		if (graph != null) { 
			ExampleTracerPath pathFromOrigin = (studentBeginsHereState == null ? 
					new ExampleTracerPath() : findPath(studentBeginsHereState));
			if (trace.getDebugCode("br")) trace.out("br", "setStudentBeginsHere() path "+pathFromOrigin);
			for (ExampleTracerLink link : pathFromOrigin.getLinks()) {
				Integer nodeId = new Integer(link.getPrevNode());
				newBSBSs.add(nodeId);
				if (!(this.beforeStudentBeginsStates.contains(nodeId)))
					nodesToNotify.add(getProblemNode(nodeId));  // repaint newly marked state
			}
			for (Integer nodeId : this.beforeStudentBeginsStates) {
				if (!(newBSBSs.contains(nodeId)))
					nodesToNotify.add(getProblemNode(nodeId));  // repaint now-unmarked state
			}
		}
		this.studentBeginsHereState =
			(studentBeginsHereState == getStartNode() ? null : studentBeginsHereState);
		this.beforeStudentBeginsStates = newBSBSs;
		if (trace.getDebugCode("br")) trace.out("br", "setStudentBeginsHereState("+studentBeginsHereState+", id="+
				(studentBeginsHereState == null ? -1 : studentBeginsHereState.getUniqueID())+
				"), states before "+beforeStudentBeginsStates);

		for (ProblemNode node : nodesToNotify) {
			if (node != null)                        
				fireProblemModelEvent(new NodeUpdatedEvent(getController(), node));
		}
//		controller.fireCtatModeEvent(CtatModeEvent.REPAINT);
	}
	
	/**
	 * Tell whether a given state precedes the {@link #studentBeginsHereState}.
	 * @param node state to test
	 * @return true if given node is a member of {@link #beforeStudentBeginsStates}
	 */
	public boolean isBeforeStudentBegins(ProblemNode node) {
		if (beforeStudentBeginsStates == null)
			return false;
		else
			return beforeStudentBeginsStates.contains(new Integer(node.getUniqueID()));
	}

	/**
	 * Whether the UI should warn the student that the
	 * pressing the Done button will prevent further work on this problem.
	 * @return {@link #confirmDone} can be null
	 */
	public Boolean getConfirmDone() {
		return confirmDone;
	}

	/**
	 * Whether the UI should warn the student that the
	 * pressing the Done button will prevent further work on this problem.
	 * @return {@link #confirmDone} if the field is null, returns true only if
	 *         {@link #getSuppressStudentFeedback()} returns {@value FeedbackEnum#HIDE_ALL_FEEDBACK}
	 */
	public boolean getEffectiveConfirmDone() {
		Boolean property = getConfirmDone();
		if (property == null) {
			FeedbackEnum fb = getSuppressStudentFeedback();
			property = new Boolean(FeedbackEnum.HIDE_ALL_FEEDBACK.equals(fb));
		}
		if (trace.getDebugCode("cd"))
			trace.out("cd", "getEffectiveConfirmDone() returns "+property+"; confirmDone "+
					getConfirmDone()+"; feedback "+getSuppressStudentFeedback()); 
		return property.booleanValue();
	}

	/**
	 * @param confirmDone new value for {@link #confirmDone}
	 */
	public void setConfirmDone(Boolean confirmDone) {
		if (trace.getDebugCode("cd"))
			trace.out("cd", "setConfirmDone("+confirmDone+") replaces "+this.confirmDone); 
		this.confirmDone = confirmDone;
	}

	/**
	 * Update a list of skills according to the given transaction result.
	 * @param transactionResult one of
	 *        {@link Skill#CORRECT()}, {@link Skill#INCORRECT}, {@link Skill#HINT}
	 * @param skillNames skills to update
	 * @param stepID identifier for this step, to ensure no step is updated more than once
	 * @return list of skills modified
	 */
	public List<Skill> updateSkills(String transactionResult, Collection<String> skillNames, String stepID) {
		List<Skill> result = new ArrayList<Skill>();
		if (controller != null && controller.isRestoringProblemState(null))
			return result;
		Skills skills = getProblemSummary().getSkills();
		if (skills == null)
			return result;                              // not tracing any skills
		for (String skillName : skillNames) {
			Skill modifiedSkill = skills.updateSkill(transactionResult, skillName, stepID);
			if(modifiedSkill != null)
				result.add(modifiedSkill);
		}
		return result;
	}

	/**
	 * @return {@link Skills#getSkillBarVector()}; returns null if no skills
	 */
	public Vector<String> getSkillBarVector() {
		Skills skills = getProblemSummary().getSkills();
		if (skills == null)
			return null;            // not tracing any skills
		else
			return skills.getSkillBarVector();
	}

	/**
	 * 
	 */
	public void startSkillTransaction() {
		Skills skills = getProblemSummary().getSkills();
		if (skills == null)
			return;            // not tracing any skills
		else
			skills.startTransaction();
	}

	/**
	 * Calls {@link ProblemGraph#renameNode(ProblemNode, String, String)}.
	 * @param problemNode
	 * @param oldName
	 * @param newName
	 */
	public void renameNode(ProblemNode problemNode, String oldName, String newName) {
		ProblemGraph graph = getProblemGraph();
		if (graph == null)
			return;
		graph.renameNode(problemNode, oldName, newName);
	}

	/**
	 * Obtain a {@link Map<String, RuleProduction>} having only instances of rules used in this
	 * problem, with opportunity counts updated for this problem's preferred path.
	 * @return map of RuleProduction
	 */
	public RuleProduction.Catalog updateOpportunityCounts() {
		RuleProduction.Catalog rulesInUse = 
				controller.getRuleProductionCatalog().removeUnusedRuleProductions(getProblemGraph());
		if (trace.getDebugCode("skills")) trace.out("skills", "rulesInUse after removeUnused: "+rulesInUse);
		if (controller.getCtatModeModel().isExampleTracingMode()) {
			List<ProblemEdge> preferredPathEdges =
				findPathForProblemSkillsMatrix(getStudentBeginsHereState());
			rulesInUse.updateOpportunityCounts(preferredPathEdges);
		}
		return rulesInUse;
	}

    /* --------------------------------------------------
     * Deletion functionality.
     * 
     * (collinl) I have moved much of the functionality for
     * node and edge deletion here as it was all being done 
     * by calls to the model and manipulations of the model
     * anyway.
     * ----------------------------------------------- */

    /**
     * Return the reference {@link BR_Controller#getSessionStorage()}.
     * @return  {@link BR_Controller#getSessionStorage()}
     */
	public Map getSessionStorage() {
		if (getController() == null)
			return null;
		else
			return getController().getSessionStorage();
	}

	public void handleUntutoredAction(MessageObject mo) {
		if (trace.getDebugCode("vt")) trace.out("vt","handleUntutoredAction Recieved "+mo);
    	Object v = mo.getProperty("Selection");
    	List selection = (List) (v instanceof List ? v : null);
    	v = mo.getProperty("Input");
    	List input = (List) (v instanceof List ? v : null);
        v = mo.getProperty("Action");
        List action = (List) (v instanceof List ? v : null);
        
        //updates the saiTable with the most recent UntutoredActionSAI, if in authoring mode
        if (!Utils.isRuntime()) {
        	controller.updateSAITable(selection, action, input,"Untutored");
        }
        
        if (selection == null || input==null)
        	return;
        int nVars = Math.min(selection.size(), input.size());
        for (int i=0; i<nVars; i++) {
        	if (trace.getDebugCode("vt")) trace.out("vt","vt.put("+selection.get(i)+", "+input.get(i)+")");
        	Object s = selection.get(i);
        	if(s != null) {
        		assignVariable(s.toString(), input.get(i));
        		assignVariable(s.toString()+".action", action.get(i));
        	}
        }    
	}

    /**
     * If the given message has selection and input elements, then bind variables in the
     * {@link VariableTable} for as many selection,input pairs as you have.
     * @param msg
     */
    public void addInterfaceVariables(MessageObject msg) {
    	Object v = msg.getProperty("Selection");
    	List selection = (List) (v instanceof List ? v : null);
    	v = msg.getProperty("Input");
    	List input = (List) (v instanceof List ? v : null);
        if (selection == null || input==null)
        	return;
        
        int nVars = Math.min(selection.size(), input.size());
        for (int i=0; i<nVars; i++) {
        	Object s = selection.get(i);
            assignVariable(s == null ? null : s.toString(), input.get(i));
        }

        if (trace.getDebugCode("functions")) trace.out("functions", "variable table #"+getVariableTable().getInstance()+" after adding "+
        		selection + ":" + input + " == " + getVariableTable());
    }

	/**
	 * @return the {@link #behaviorRecorderMode}
	 */
	public String getBehaviorRecorderMode() {
		return behaviorRecorderMode;
	}

	/**
	 * @param behaviorRecorderMode new value for {@link #behaviorRecorderMode}
	 */
	void setBehaviorRecorderMode(String behaviorRecorderMode) {
		this.behaviorRecorderMode = behaviorRecorderMode;
	}

	/**
	 * Get the delimiter between fields in the skill bars from {@link #getSkillBarVector()}.
	 * @return result of {@link Skills#getSkillBarDelimiter()}; if no skills, gets value of
	 *         {@link Skill#SKILL_BAR_DELIMITER}, currently {@value Skill#SKILL_BAR_DELIMITER} 
	 */
	public String getSkillBarDelimiter() {
		Skills skills = getProblemSummary().getSkills();
		if (skills == null)
			return Skill.SKILL_BAR_DELIMITER;  // default
		else
			return skills.getSkillBarDelimiter();
	}

	/**
	 * Request a return to the start state at the earliest opportunity.
	 * @param state target node to set up the request; null to cancel it
	 */
	public void requestGoToState(ProblemNode state) {
		this.requestGoToState = state;		
	}

	/**
	 * Act on the {@link #requestGoToStartState}. Clears {@link #requestGoToStartState}.
	 */
	public void checkRequestGoToState() {
		ProblemNode node = requestGoToState;
		requestGoToState = null;                // read field only once: threading
		if (node == null)
			return;
		if (trace.getDebugCode("pm"))
			trace.out("pm", "checkRequestGoToState("+node.toString()+")");
		BR_Controller ctlr = getController(); 
		if (ctlr == null)
			return;
		ctlr.goToStartState(node, true);
	}

	/**
	 * @return {@link #outOfOrderMessage}; if null or empty,
	 *         return {@value ProblemModel#DEFAULT_OUT_OF_ORDER_MESSAGE}
	 */
	public String getOutOfOrderMessage() {
		if (outOfOrderMessage == null || outOfOrderMessage.length() < 1)
			return ProblemModel.DEFAULT_OUT_OF_ORDER_MESSAGE;
		else
			return outOfOrderMessage;
	}
	
	/**
	 * @param outOfOrderMessage new value for {@link #outOfOrderMessage}
	 */
	public void setOutOfOrderMessage(String outOfOrderMessage) {
		this.outOfOrderMessage = outOfOrderMessage;
	}

	/**
	 * If the given condition name has the strings "hint" and "random", then turn on hint
	 * randomization for this problem. This feature was added for Ilya Goldin's study on the
	 * Mathtutor site, Fall 2012.
	 * @param conditionName study condition name for logging
	 * @return true if turned on hint randomization
	 */
	public boolean checkHintRandomization(String conditionName) {
		if (conditionName == null)
			return false;
		String cn = conditionName.toLowerCase();
		if (cn.indexOf("hint") < 0 || cn.indexOf("random") < 0)  // if either string absent
			return false;
		setRandomizeHints(true);
		return true;
	}

	/**
	 * Turn on hint randomization for this problem. This feature was added for Ilya Goldin's
	 * study on the Mathtutor site, Fall 2012.
	 * @param b new value for {@link #randomizeHints}
	 */
	private void setRandomizeHints(boolean b) {
		randomizeHints = b;		
	}

	/**
	 * @return the {@link #randomizeHints}
	 */
	public boolean getRandomizeHints() {
		return randomizeHints;
	}

	/**
	 * If {@link #getRandomizeHints()}, find a step on which to deliver an unrequested hint.
	 * Sets {@link #unrequestedHintDepths} to the fraction {@value #UNREQUESTED_HINT_FRACTION}
	 * of randomly-chosen indices of steps on the preferred path. 
	 * @param currentNode calculate the path from this node
	 * @param depthSoFar number of links traversed already
	 * @return ShowHintsMessage for the link to hint, if any
	 */
	public List<MessageObject> getUnrequestedHint(ProblemNode currentNode, int depthSoFar) {
		if (!getRandomizeHints())
			return null;
		ExampleTracerNode node = getExampleTracerGraph().getNode(currentNode.getUniqueID());
		Set<ExampleTracerPath> paths = getExampleTracerGraph().findPathsFromNode(node);
		Set<ExampleTracerPath> pathsToDone = new HashSet<ExampleTracerPath>();
		ExampleTracerPath shortest = null;
		for (ExampleTracerPath p : paths) {
			if (p.isDonePath() && 
					(shortest == null || p.getLinks().size() < shortest.getLinks().size()))
				shortest = p;
		}
		if (shortest == null)
			return null;
		int shortestLen = shortest.getLinks().size();
		CTATRandom cr = new CTATRandom();
		unrequestedHintDepths = cr.randomIndices(shortestLen, UNREQUESTED_HINT_FRACTION);
		for(int i = 0; i < unrequestedHintDepths.length; ++i)
			unrequestedHintDepths[i] += depthSoFar;
		Arrays.sort(unrequestedHintDepths);  // sort for binary search below
		if (trace.getDebugCode("hints"))
			trace.out("hints", "PM.getUnrequestedHint() currentNode "+currentNode.getUniqueID()+
					", depthSoFar "+depthSoFar+", shortestLen "+shortestLen+
					", fraction "+UNREQUESTED_HINT_FRACTION+
					", unrequestedHintDepths "+Arrays.toString(unrequestedHintDepths));
		return getUnrequestedHint(depthSoFar);
	}

	/**
	 * If {@link #unrequestedHintDepths} contains a value equal to the depthSoFar argument,
	 * return a {@link MsgType#SHOW_HINTS_MESSAGE} message and the associated messages
	 * to simulate a tutor-performed hint request.
	 * @param depthSoFar the depth of the last-traversed step
	 * @return {@value MsgType#SHOW_HINTS_MESSAGE} message and related messages or null
	 */
	public List<MessageObject> getUnrequestedHint(int depthSoFar) {
		if (trace.getDebugCode("hints"))
			trace.out("hints", "PM.getUnrequestedHint("+depthSoFar+") unrequestedHintDepths "+
					Arrays.toString(unrequestedHintDepths));
		if (!getRandomizeHints())
			return null;
		if (Arrays.binarySearch(unrequestedHintDepths, depthSoFar) < 0)
			return null;
		if (controller == null)
			return null;

		Vector<String> selection = new Vector<String>(); selection.add("Hint"); 
		Vector<String> action    = new Vector<String>(); action.add("ButtonPressed"); 
		Vector<String> input     = new Vector<String>(); input.add("-1"); 
		ExampleTracerEvent[] result = new ExampleTracerEvent[1]; 
		ProblemEdge hintLink = controller.getExampleTracer().doHint(selection, action, input,
				 "Student", result, false);  // trace as if student requested
		if (trace.getDebugCode("hints"))
			trace.out("hints", "PM.getUnrequestedHint("+depthSoFar+") hintLink "+hintLink);
		if (hintLink == null)
			return null;
		result[0].setActor(Matcher.DEFAULT_TOOL_ACTOR);  // replace actor since tutor-initiated
		
		unrequestedHintMsgs = new ArrayList<MessageObject>();
		MessageObject toolMsg = PseudoTutorMessageBuilder.buildToolInterfaceAction(selection, action,
				input, PseudoTutorMessageBuilder.TRIGGER_DATA, PseudoTutorMessageBuilder.TUTOR_PERFORMED);
		controller.setSemanticEventId(toolMsg.getTransactionId());  // set up id for tutor msgs below 
		unrequestedHintMsgs.add(toolMsg);
		if (result[0].isSolverResult()) {
			String stepID = Skill.makeStepID(result[0].getTutorSelection(), result[0].getTutorAction());
			unrequestedHintMsgs.add(PseudoTutorMessageBuilder.buildHintsMsg(result[0].getTutorAdvice(),
					result[0].getTutorSelection(), result[0].getTutorAction(),
					result[0].getTutorInput(), Integer.toString(hintLink.getUniqueID()), null,
					null, controller));
			unrequestedHintMsgs.add(PseudoTutorMessageBuilder.buildAssocRulesFromEvent(result[0], controller));
		} else {
//			hintLink.getEdgeData().updateSkills(Skill.HINT);
			unrequestedHintMsgs.add(PseudoTutorMessageBuilder.buildHintsMsg(hintLink, controller));
			unrequestedHintMsgs.add(PseudoTutorMessageBuilder.buildAssociatedRules(hintLink,
					PseudoTutorMessageBuilder.HINT, Matcher.DEFAULT_TOOL_ACTOR, controller, null));
		}

		return unrequestedHintMsgs;
	}
	
	/**
	 * Prevent further unrequested hints by removing the given depth from {@link #unrequestedHintDepths}.
	 * @param depthToRemove element to delete from {@link #unrequestedHintDepths}
	 */
	public void cancelUnrequestedHint(int depthToRemove) {
		int i = Arrays.binarySearch(unrequestedHintDepths, depthToRemove);
		if(i < 0)      // element not found
			return;
		int[] result = new int[unrequestedHintDepths.length - 1];
		if(result.length < 1)
			return;
		for(int j = 0, k = 0; j < unrequestedHintDepths.length; j++) {
			if(j != i)
				result[k++] = unrequestedHintDepths[j];
		}
		if(trace.getDebugCode("hints"))
			trace.out("hints", "cancelUnrequestedHint("+depthToRemove+") unrequestedHintDepths was "+
					Arrays.toString(unrequestedHintDepths)+", now "+Arrays.toString(result));
		unrequestedHintDepths = result;
	}

	/**
	 * @return {@value CTATSerializable.IncludeIn#sparse}
	 */
	public CTATSerializable.IncludeIn getInterfaceDescriptionFilter() {
		return CTATSerializable.IncludeIn.sparse;
	}
	
	public boolean isEmpty() {
		return (getStartNode() == null);
	}
	
	public void printSelectedLinks() {
		//this.editContext.getSelectedLinks(ExampleTracerLink link);
		Set<ExampleTracerLink> links = this.editContext.getSelectedLinks();
		for(ExampleTracerLink link : links) {
			trace.out("mg", "ProblemModel (printSelectedLinks): " + link.getID());
		}
	}
	
	public Set<ExampleTracerLink> getSelectedLinks() {
		return this.editContext.getSelectedLinks();
		/*
		trace.out("mg", "ProblemModel (copySelectedLinks): start");
		copiedEdges.clear();
		Set<ExampleTracerLink> links = this.editContext.getSelectedLinks();
		this.editContext.getSelectedGroup();
		for(ExampleTracerLink link : links) {
			copyLink(link);
		}
		*/
	}

	/**
	 * Revise the problem name in {@link #setProblemName(String)}, {@link #getStartNode()}, the
	 * initial {@value MsgType#START_PROBLEM} message in {@link #getStartNodeMessageVector()}.
	 * @param newProblemName
	 */
	public void renameProblem(String newProblemName) {
		setProblemName(newProblemName);
		getStartNode().setName(newProblemName);
		if(getStartNodeMessageVector() != null && getStartNodeMessageVector().size() > 0) {
			MessageObject obj = (MessageObject) getStartNodeMessageVector().get(0);
			if (MsgType.START_PROBLEM.equals(obj.getMessageType()))
				obj.setProperty("ProblemName", newProblemName);
			getStartNodeMessageVector().set(0, obj);
		}
		getStartNode().getNodeView().setText(newProblemName);
	}

	/**
	 * Tell whether a string contains a reference to a Mass Production variable.
	 * Uses {@link #MassProductionVarPattern}.
	 * @param str string to scan
	 * @return true if the pattern occurs in the string
	 */
	public static boolean hasMassProductionVarPattern(String str) {
		return getMassProductionVarPattern().matcher(str).find();
	}

	/**
	 * @return the {@link #MassProductionVarPattern}
	 */
	public static Pattern getMassProductionVarPattern() {
		return MassProductionVarPattern;
	}

	/** Saved instance of formula parser. */
    private CTATFunctions ctatFunctions;

    /**
     * @return {@link #ctatFunctions}; creates one if null
     */
    private CTATFunctions getCtatFunctions() {
        if (ctatFunctions==null)
            ctatFunctions = new CTATFunctions(getVariableTable(), this, getFormulaParser());
        return ctatFunctions;
    }
    
    /**
     * Evaluate any formula reference in the given message and substitute the result back in place.
     * @param mo message to interpret
     * @return revised message
     */
    public MessageObject interpolateAllValues(MessageObject mo) {
    	edu.cmu.pact.ctat.MessageObject mo2 = mo.copy();
        for (String name: mo2.getPropertyNames()) {
            Object pv =  mo2.getProperty(name);

            if (trace.getDebugCode("functions")) trace.outln("functions", "pv = " + pv);
            if (pv instanceof Vector) {
                Vector interpolatedValues = new Vector<String>();

                for (Object value: (Vector)pv)
                    interpolatedValues.add(getCtatFunctions().interpolate(value.toString()));
                mo2.setProperty(name, interpolatedValues);
            } else if (pv instanceof Element) {
            	mo2.setProperty(name, pv);  // don't try to interpolate Elements
            } else if (pv!=null)
                mo2.setProperty(name, getCtatFunctions().interpolate(pv.toString()));
        }
        return mo2;
    }    
}
