/*
 * Created on Jul 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

//Will ProblemStateReaderJDOM ever be used to open to BRDs?


import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.JDOMParseException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExactMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherFactory;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.SolverMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.VectorMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.ProductionRuleElement;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.ctatview.CtatMenuBar;

/**
 * @author pact
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ProblemStateReaderJDom {

	/**
	 * For converting embedded XML to strings.
	 */
	private static XMLOutputter outputter =
		new XMLOutputter(
			    Format.getCompactFormat().setOmitDeclaration(true).setLineSeparator("").setIndent("")
		);

    private BR_Controller controller;

    private Document doc = null;

    /** Older format BRD files have this attribute in the stateGraph element. */
    private boolean unorderedFlag = false;
    
    /** True if we've seen an EdgesGroups element, so can ignore {@link #unorderedFlag}. */
    private boolean hasEdgesGroups = false;
    
    private Vector currentAssociatedElements = new Vector();

    private Vector currentAssociatedElementsValues = new Vector();

    private ProblemModel problemModel;
    
    private Hashtable foAMap;
    
    private ExampleTracerGraph graph;

	private String brdVersion = "";
	private ArrayList<NodeCreatedEvent> nodeCreatedEvents;
	private ArrayList<EdgeCreatedEvent> edgeCreatedEvents;
	private String studentBeginsHereStateName;
	private String behaviorRecorderMode;
    
    public ProblemStateReaderJDom(BR_Controller controller) {
    	nodeCreatedEvents = new ArrayList<NodeCreatedEvent>();
    	edgeCreatedEvents = new ArrayList<EdgeCreatedEvent>();
        this.controller = controller; // may be null!
        foAMap = new Hashtable();
    }
    
    private Document parse (InputStream is) throws Exception {
    	Document doc;
        SAXBuilder builder = new SAXBuilder();	
    	InputStreamReader isr = new InputStreamReader(is, "ISO-8859-1");    	
		doc = builder.build(isr);
		isr.close();
    	return doc;
    }
    /**
     * Load a BRD given a file name.
     * 
     * @param problemFullName
     * @param authorMode authorMode to enter after loading
     * @return true if successful
     */
    boolean loadBRDFileIntoMainProblemState(InputStream is, String problemFullName, String absolutePath) {
    	String title = "Error parsing graph file";  // for exceptions
        try {
        	doc = parse (is);
        } catch (JDOMParseException je) {
        	//                String message = "<html>The format of the file is not recognized. <br>"
        	//            		+ "Please check the file and try again.";
        	String message = "<html>Error reading file: " + problemFullName + "<br>" + 
        	"The format of the file is not recognized. <br>" + je 
        	+ "<br>Please check the file and try again.";
        	Utils.showExceptionOccuredDialog(null, message, title);
        	return false; // table left empty if bad file
        } catch (Exception e2) {
        	String message = "<html>Error reading file " + problemFullName + ":<br/>"+
        	e2+(e2.getCause() == null ? "" : ".<br/>Cause: "+e2.getCause());
        	e2.printStackTrace();
        	Utils.showExceptionOccuredDialog(null, message, title);
        	return false; // table left empty if bad file
        }
        try {
            

            // TODO :: Old Behavior Recorder must be cleared
            // This did not improve the memory problem.  Indeed the Behavior Graph does not 
            // show the graph
            // controller.setProblemModel(new ProblemModel(controller));
            
        	problemModel = controller.getProblemModel();
            RuleProduction.Catalog rpc = controller.getRuleProductionCatalog(); 
            if (problemModel != null) {
            	String problemName = Utils.getBaseName(problemFullName);
             	problemModel.reset(problemName, absolutePath);
            }

            processDocument(doc, problemModel, rpc);
            problemModel = controller.getProblemModel(); // in case ProblemModel changes

            //          2006/02/01 chc added the following codes to generate curriculum_message.
            // 2007/03/02 sewall deleted these: the caller openBRDiagramFile sends the start state
            // and generates the curriculum msg
//            controller.sendCommMsgs(problemModel.getStartNode(), problemModel.getStartNode());
//            controller.sendLoadBRDFileSuccessMsg(problemFullName);

            controller.updateStatusPanel(problemFullName);

        } catch (Exception e) {
        	String message = "<html>Error processing file " + problemFullName + ":<br/>"+
        			e+(e.getCause() == null ? "" : ".<br/>Cause: "+e.getCause());
        	e.printStackTrace();
        	Utils.showExceptionOccuredDialog(null, message, title);
        	return false; // table left empty if bad file
        }
        return true;
    }
    
    /**
     * @param element
     * @param psrj instance of this class for reading {@link #currentAssociatedElements}
     * @param problemModel
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException 
     * @throws NoSuchMethodException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    private void processTopLevelElement(Element element, ProblemStateReaderJDom psrj,
    		ProblemModel problemModel, RuleProduction.Catalog rpc)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException {
        String elementName = element.getName(); 
        if (trace.getDebugCode("jdomreader")) trace.out("jdomreader", "element name = " + elementName);

        if (elementName.equals("startNodeMessages"))
            processStartNodeElement(element, problemModel);
        else if (elementName.equals(ProblemNode.ELEMENT_NAME))
            processNode(element, problemModel);
        else if (elementName.equals("element")) {
        	if (psrj != null)
                processElement(element, psrj.currentAssociatedElements, psrj.currentAssociatedElementsValues);
        	else
        		processElement(element, null, null);
        }
        else if (elementName.equals(ProblemEdge.ELEMENT_NAME))
            processEdge(element, problemModel, rpc);
        else if (elementName.equals("EdgesGroups")) {
            problemModel.getExampleTracerGraph().getGroupModel().readFromXML(element, unorderedFlag);
            hasEdgesGroups = true;
        } else if (elementName.equals(ProductionRuleElement.PRODUCTION_RULE_ELEMENT_NAME))
            processProductionRuleElement(element, rpc);
    }

    private void processProductionRuleElement(Element element, RuleProduction.Catalog rpc) {
    	if (rpc == null)
    		return;
        String productionRuleName = element.getChildText("ruleName");
        String productionSet = element.getChildText("productionSet");

//        RuleProduction ruleProduction = new RuleProduction(productionRuleName,
//                productionSet);
//        problemModel.addRuleProduction(ruleProduction);
        // avoid to add duplicate copies 
        // process processTopLevelElement "edge" might add one copy already
        RuleProduction rp = rpc.checkAddRuleName(productionRuleName, productionSet);
        rp.setLabel(element.getChildText("label"));
        rp.setDescription(element.getChildText("description"));
        List hints = element.getChildren("hintMessage");
        if (hints.size() > 0) {
        	rp.setHints(new Vector<String>());    // clear existing hints
        	for (Iterator it = hints.iterator(); it.hasNext(); )
        		rp.addHintItem(((Element) it.next()).getText());
        }
    }

    private void processEdge(Element edgeElement, ProblemModel problemModel,
    		RuleProduction.Catalog rpc)
        throws InstantiationException, IllegalAccessException, ClassNotFoundException, 
            SecurityException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException {
        {
            if (trace.getDebugCode("jdomreader")) trace.out("jdomreader", "processing edge for problem "+problemModel.getProblemFullName());
            Element sourceElement = edgeElement.getChild("sourceID");
            Element destinationElement = edgeElement.getChild("destID");
            Element traversalCountElt = edgeElement.getChild("traversalCount");
            
            Element actionLabelElement = edgeElement.getChild("actionLabel");

            Element actionLabelUniqueIDElement = actionLabelElement.getChild("uniqueID");
            Element messageElement = actionLabelElement.getChild("message");
            
            MessageObject messageObject = getMessageObject(messageElement);
            
            int ID = Integer.parseInt(actionLabelUniqueIDElement.getValue());
            int sourceID = Integer.parseInt(sourceElement.getValue());
            int destID = Integer.parseInt(destinationElement.getValue());
            int traversalCount =
              Integer.parseInt(traversalCountElt == null ? "-1" : traversalCountElt.getValue());

            String buggymsg = actionLabelElement.getChildText("buggyMessage");
            String success = actionLabelElement.getChildText("successMessage");
            String actionType = actionLabelElement.getChildText("actionType");
            String checked = actionLabelElement.getChildText("checkedStatus");
            boolean isPreferPath = actionLabelElement.getAttributeValue(
                    "preferPathMark").equals("true");            

            if (trace.getDebugCode("jdomreader")) trace.out("jdomreader", "read edge: " + edgeElement.getChildren());

            EdgeData edgeData = createEdgeDataForEdge(problemModel,actionLabelElement,
            		messageObject, ID, buggymsg, success, actionType, checked, isPreferPath);
            
            edgeData.setMinTraversalsStr(actionLabelElement.getAttributeValue("minTraversals"));
            edgeData.setMaxTraversalsStr(actionLabelElement.getAttributeValue("maxTraversals"));
            
            addMatcherForEdge(actionLabelElement, messageObject, edgeData);
            
            if(actionLabelElement.getChild("callbackFn") != null){
            	trace.out("Callback function is = " + actionLabelElement.getChild("callbackFn").getValue());
            	edgeData.setCallbackFn(actionLabelElement.getChild("callbackFn").getValue());
            }
            
            addRulesForEdge(edgeElement, edgeData, rpc);
            
            ProblemNode sourceNode = ProblemModel.getNodeForVertexUniqueID(
                    sourceID, problemModel.getProblemGraph());
            ProblemNode destinationNode = ProblemModel
                    .getNodeForVertexUniqueID(destID, problemModel.getProblemGraph());
            if (destinationNode == null) {
                throw new RuntimeException(
                        "Could not location destination node for UniqueID "
                                + destID);
            }
            if (sourceNode == null) {
                throw new RuntimeException(
                        "Could not location source node for UniqueID "
                                + sourceID);
            }
            edgeData.setTraversalCount(traversalCount);
            
            ProblemEdge newEdge = problemModel.getProblemGraph().addEdge(
                    sourceNode, destinationNode, edgeData);
            
            createSimStudentFOA(edgeElement, newEdge);

            if (newEdge == null) {
                throw new RuntimeException(
                        "Error occuring adding edge between source "
                                + sourceNode + " and destination "
                                + destinationNode);
            }

    		// 11-06-09 :: Noboru getting rid of Old BR
    		boolean updateAuthorUI = (!Utils.isRuntime() && problemModel.getController() != null);
    		if (updateAuthorUI)
    		{
    	        // replace BRPanel.addEdgeLabels
    	        newEdge.addEdgeLabels(); // 08-16-2008 chc moved from BR to ProblemEdge
    	        
//    	        myEdge.getEdgeView().update();
    		}
    		edgeCreatedEvents.add(new EdgeCreatedEvent(this,newEdge));
    		//problemModel.fireProblemModelEvent(new EdgeCreatedEvent(this, newEdge));
        }
    }

    /**
     * Get an integer attribute.
     * @param attrName
     * @param defaultResult return this if attribute value is not an integer
     * @param elt element to query.
     * @param eltType for debugging: "node", "link", ... whatever the ID identifies
     * @param ID for debugging: to identify element we're parsing
     * @return attribute value or defaultResult 
     */
    private int getIntAttr(String attrName, int defaultResult, Element elt, String eltType, int ID) {
		String attrVal = elt.getAttributeValue(attrName);
		try {
			return Integer.parseInt(attrVal);
		} catch (NumberFormatException nfe) {
			trace.err("Error reading "+attrName+" for "+eltType+ID+"; using default "+defaultResult+": "+nfe);
			return defaultResult;
		}
	}

	/**
     * @param pm
     * @param actionLabelElement
     * @param actionLabelTextElement
     * @param messageObject
     * @param ID
     * @param buggymsg
     * @param success
     * @param actionType
     * @param checked
     * @param isPreferPath
     * @return
     */
    private EdgeData createEdgeDataForEdge(ProblemModel pm,
    		Element actionLabelElement, MessageObject messageObject,
    		int ID, String buggymsg, String success, String actionType,
    		String checked, boolean isPreferPath) {
        EdgeData edgeData = new EdgeData(pm);

        List hintList = actionLabelElement.getChildren("hintMessage");
        for (Iterator hints = hintList.iterator(); hints.hasNext();) {
            Element hint = (Element) hints.next();
            String hintMessage = hint.getText();
            if (trace.getDebugCode("jdomreader")) trace.out("jdomreader", "hint message = " + hintMessage);
            edgeData.addHint(hintMessage);
        }
        if (pm.getRandomizeHints())
        	edgeData.randomizeHintOrder();
        
        edgeData.setBuggyMsg(buggymsg);
        edgeData.setCheckedStatus(checked);
        if (actionType != null)
            edgeData.setActionType(actionType);
        edgeData.setSuccessMsg(success);
        edgeData.setPreferredEdge(isPreferPath);
        edgeData.setUniqueID(ID);
        edgeData.setDemoMsgObj(messageObject);
        return edgeData;
    }

    /**
     * @param actionLabelElement
     * @param messageObject
     * @param edgeData
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException 
     * @throws NoSuchMethodException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private void addMatcherForEdge(Element actionLabelElement, MessageObject messageObject, EdgeData edgeData) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException {
        Element matcherElement = actionLabelElement.getChild("matcher");
        Element solverMatcherElement = actionLabelElement.getChild("solverMatcher");
        Element vectorMatcherElement = actionLabelElement.getChild("matchers");

        Vector<String> currentSelection = messageObject.getSelection();
        Vector<String> currentAction = messageObject.getAction();
        Vector<String> currentInput = messageObject.getInput();
        
        if (matcherElement != null) {
            Matcher m = createMatcher(matcherElement);
            edgeData.setMatcher(m);
            
			m.setDefaultSelectionVector(currentSelection);
            m.setDefaultActionVector(currentAction);
            m.setDefaultInputVector(currentInput);
            
            /*
            m.setDefaultAction((String) currentAction.get(0));
            m.setDefaultSelection((String) currentSelection.get(0));
            m.setDefaultInput((String) currentInput.get(0));
            */
        } else if(solverMatcherElement != null) {
        	SolverMatcher sm = createSolverMatcher(solverMatcherElement, currentSelection,
        			currentInput);
        	if (sm != null)
        		edgeData.setMatcher(sm);
        } else if(vectorMatcherElement != null) {
        	VectorMatcher vm = createVectorMatcher(vectorMatcherElement);
        	
			vm.setDefaultSelectionVector(currentSelection);
            vm.setDefaultActionVector(currentAction);
            vm.setDefaultInputVector(currentInput);
            
            edgeData.setMatcher(vm);
        } else {
            ExactMatcher m = buildDefaultExactMatcher(messageObject, edgeData);
            if (m != null)
                edgeData.setMatcher(m);
        }
    }

    /**
     * @param edgeElement
     * @param newEdge
     */
    private void createSimStudentFOA(Element edgeElement, ProblemEdge newEdge) {
        //adding FOA to SimSt element if it exists
        Element simStElement = edgeElement.getChild("SimSt");
        Vector foAWidgetList = new Vector();
        
        if(simStElement != null){
            List foAList = simStElement.getChildren("focusOfAttention");
            
            for(int i=0; i < foAList.size(); i++){
            	Element foAElement = (Element)foAList.get(i);
                String widgetName = foAElement.getChildText("target");
                if (trace.getDebugCode("miss-jdom")) trace.out("miss-jdom", "sungjoo_ProblemStateReader.ProcessEdge: widget = " + widgetName
                		+ " JDom = " + this);
                foAWidgetList.add(widgetName);
            }
            foAMap.put(newEdge, foAWidgetList);
        } 
    }

    /**
     * @param messageObject
     * @param edgeData
     * @return
     */
    private ExactMatcher buildDefaultExactMatcher(MessageObject messageObject, EdgeData edgeData) {
    	Vector<String> selection = messageObject.getSelection();
    	Vector<String> action = messageObject.getAction();
    	Vector<String> input = messageObject.getInput();
    	ExactMatcher m = new ExactMatcher(selection, action, input);
    	edgeData.setMatcher(m);
    	return m;
    }

    /**
     * Add rules defined in the <edge> element. They may be echoed in the
     * <productionRule> elements
     * @param edgeElement
     * @param edgeData
     * @param rpc
     */
    private void addRulesForEdge(Element edgeElement, EdgeData edgeData,
    		RuleProduction.Catalog rpc) {
    	if (rpc == null)
    		return;
        List ruleElements = edgeElement.getChildren("rule");

        for (Iterator elements = ruleElements.iterator(); elements.hasNext();) {

            Element ruleElement = (Element) elements.next();
            String ruleLabelText = ruleElement.getChildText("text");
            edgeData.addRuleName(ruleLabelText);
            
            if (ruleLabelText.contains(" ")) {
				int separatorPosition = ruleLabelText.indexOf(" ");
				String newRuleNameText = ruleLabelText.substring(0,
						separatorPosition);
				String newProductionSet = ruleLabelText.substring(
						separatorPosition + 1, ruleLabelText.length());
				rpc.checkAddRuleName(newRuleNameText, newProductionSet);
			} else if (!ruleLabelText.equalsIgnoreCase("unnamed"))
				rpc.checkAddRuleName(ruleLabelText, "");  // It should only happen at "unnamed" case.
        }
    }

    /**
     * Call the {@link SolverMatcher} constructor. 
     * @param solverMatcherElement
     * @param defaultSelection for debugging
     * @param defaultInput for debugging
     * @return new Matcher instance
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private SolverMatcher createSolverMatcher(Element solverMatcherElement,
    		Vector defaultSelection, Vector defaultInput)
    		throws InstantiationException, IllegalAccessException, ClassNotFoundException,
    		SecurityException, NoSuchMethodException, IllegalArgumentException,
    		InvocationTargetException
    {
    	try {
    		String autoSimplify = solverMatcherElement.getAttributeValue("AutoSimplify", "false");
    		String typeinMode = solverMatcherElement.getAttributeValue("TypeinMode", "false");
    		String goal = solverMatcherElement.getAttributeValue("Goal", (String) null);
    		List<Matcher> sList =
    			createChildMatcher(solverMatcherElement, "Selection", Matcher.SELECTION, true);
    		List<Matcher> aList =
    			createChildMatcher(solverMatcherElement, "Action", Matcher.ACTION, false);
    		List<Matcher> iList =
    			createChildMatcher(solverMatcherElement, "Input", Matcher.INPUT, false);
			boolean[] linkTriggered = new boolean[1];
			String actor = findActorInMatcherElement(solverMatcherElement, linkTriggered);

    		SolverMatcher sm = new SolverMatcher(true, sList, aList, iList, actor,
    				autoSimplify, typeinMode, goal);
			sm.setLinkTriggered(linkTriggered[0]);

    		return sm;
    	} catch (Exception ie) {
    		trace.err("Error creating SolverMatcher for selection "+defaultSelection+
    				", input "+defaultInput+": "+ie);
    		ie.printStackTrace();
    		return null;
    	}
    }
    
    private VectorMatcher createVectorMatcher(Element vectorMatcherElement)
    		throws InstantiationException, IllegalAccessException, ClassNotFoundException,
    		SecurityException, NoSuchMethodException, IllegalArgumentException,
    		InvocationTargetException
    {
    	boolean concat = Boolean.valueOf(vectorMatcherElement.getAttributeValue("Concatenation"));

    	List<Matcher> sList =
    		createChildMatcher(vectorMatcherElement, "Selection", Matcher.SELECTION, concat);

    	List<Matcher> aList =
    		createChildMatcher(vectorMatcherElement, "Action", Matcher.ACTION, concat);

    	List<Matcher> iList =
    		createChildMatcher(vectorMatcherElement, "Input", Matcher.INPUT, concat);

    	boolean[] linkTriggered = new boolean[1];
    	String actor = findActorInMatcherElement(vectorMatcherElement, linkTriggered);
    	
    	VectorMatcher vm = new VectorMatcher(concat, sList, aList, iList, actor);
    	vm.setLinkTriggered(linkTriggered[0]);
    	
    	return vm;
    }
    
    private List<Matcher> createChildMatcher(Element vectorMatcherElement,
			String tag, int role, boolean concat)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException,
    		NoSuchMethodException, InvocationTargetException {
    	Element elt = vectorMatcherElement.getChild(tag);
    	List<Element> childElts = (List<Element>) elt.getChildren();
    	List<Matcher> matchers = new LinkedList<Matcher>();
    	for(int i = 0; i < childElts.size(); i ++)
    		matchers.add(createMatcher(childElts.get(i), true, concat, role));
		return matchers;
	}

	/**
     * Get the actor value from a Matcher child element.
     * @param matcherElement
	 * @param linkTriggered to return flag whether tutor-performed action is link-triggered (true) or state-triggered
     * @return actor value
     */
	private String findActorInMatcherElement(Element matcherElement, boolean[] linkTriggered) {
    	String actor = null;
    	Element actorElement = matcherElement.getChild("Actor");
		linkTriggered[0] = false;
    	if(actorElement == null)
    		actor = Matcher.DEFAULT_ACTOR;
    	else
    	{
    		actor = actorElement.getValue();
	    	if (!actor.equals(Matcher.DEFAULT_TOOL_ACTOR)
	    			&& !actor.equals(Matcher.ANY_ACTOR)
	    			&& !actor.equals(Matcher.DEFAULT_STUDENT_ACTOR)
	    			&& !actor.equals(Matcher.UNGRADED_TOOL_ACTOR)) {
	    		trace.err("PSRJDom: actorElement value \""+actor+"\" not defined; using "+Matcher.DEFAULT_ACTOR);
	    		actor = Matcher.DEFAULT_ACTOR;
	    	}
	    	
	    	linkTriggered[0] = Boolean.parseBoolean(actorElement.getAttributeValue(Matcher.TRIGGER_ATTR,
	    			Boolean.FALSE.toString()));
    	}
    	if(trace.getDebugCode("actor"))
    		trace.out("actor", "PSRJ.findActorInMatcherElement() returns "+actor);
    	return actor;
	}
    
    private Matcher createMatcher(Element matcherElement) throws InstantiationException,
    	IllegalAccessException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException { 
    	return createMatcher(matcherElement, false, false, Matcher.NON_SINGLE); //the last two args are ignored anyway ...
    }
    
    private Matcher createMatcher(Element matcherElement, boolean single, boolean concat, int vector) throws SecurityException, IllegalArgumentException, InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException
    {
        if (matcherElement == null)
            return null;

        String matcherType = matcherElement.getChildText("matcherType");
        
        Matcher m = null;
        if(single)
        	m = MatcherFactory.buildSingleMatcher(matcherType, concat, vector);
        else
        	m = MatcherFactory.buildMatcher(matcherType);
        if (trace.getDebugCode("functions")) trace.outln("functions", "created matcher " + m.getClass() + " for type " + matcherType);
        
        if (controller != null)
        	m.setSessionStorage(controller.getSessionStorage());

        List paramList = matcherElement.getChildren("matcherParameter");
        int count = 0;
        for (Iterator params = paramList.iterator(); params.hasNext();) {
            Element e = (Element) params.next();
            if (e.getAttributeValue(ProblemStateReader.VERSION_ATTR) == null)
            	e.setAttribute(ProblemStateReader.VERSION_ATTR, brdVersion);
            m.setParameter(e, count);
            count++;
        }
        m.setReplacementFormula(matcherElement.getAttributeValue("replacementFormula"));
        
        return m;
    }

    private void processElement(Element elt, List assocElts, List assocVals) {
        {
        	if (assocElts == null || assocVals == null)
        		return;
            Element text = elt.getChild("text");
            Element value = elt.getChild("value");
            assocElts.add(text.getValue());
            assocVals.add(value.getValue());
        }
    }
    
    private void processNode(Element elt, ProblemModel pm) {
//        trace.out("sp", "processNode(): isStartNodeCreatedFlag() "
//                + pm.getStartNodeCreatedFlag());

        Element text = elt.getChild("text");
        Element dimension = elt.getChild("dimension");
        String nodeidStr = elt.getChild("uniqueID").getValue();
        int nodeid = (new Integer(nodeidStr)).intValue();
        
		NodeView currentNodeView = null;

		// change to if in GPPMP (check if problem model is in one)
		boolean updateAuthorUI = (!Utils.isRuntime() && pm.getController() != null);
		if (updateAuthorUI)
		{
			currentNodeView = new NodeView(text.getValue(), pm.getController());
		
			// Check for attribute: locked. Kim K.C. 9/25/05
			if (elt.getAttributeValue("locked") != null) {
				boolean locked = elt.getAttributeValue("locked").equals("true");
				currentNodeView.setLocked(locked);
			}
			
			currentNodeView.setLocation(new Integer(dimension.getChild("x")
	                .getValue()).intValue(), new Integer(dimension.getChild("y")
	                .getValue()).intValue());
		}
		
        ProblemGraph r = pm.getProblemGraph();

        final ProblemNode problemNode = (updateAuthorUI ?
        		new ProblemNode(currentNodeView, pm) : new ProblemNode(pm, text.getValue()));
        problemNode.setUniqueID(nodeid);
        
		if (elt.getAttributeValue("doneState") != null) {
			boolean doneState = elt.getAttributeValue("doneState").equals("true");
			problemNode.setDoneState(doneState);
		}

        ProblemNode tempNode = r.addProblemNode(problemNode);
        
        if (pm.isFirstNode()) {
            pm.setStartNode(tempNode);
            pm.setStartNodeCreatedFlag(true);
            if (pm.getController() != null)
            	pm.getController().getSolutionState().setCurrentNode(tempNode);
            if (updateAuthorUI)          	
            	currentNodeView.setFont(CtatMenuBar.defaultFont);
            pm.setFirstNode(false);
        }
        nodeCreatedEvents.add(new NodeCreatedEvent(this, problemNode, null));
        //NodeCreatedEvent nce = new NodeCreatedEvent(this, problemNode, null);
        //pm.fireProblemModelEvent(nce);
    }
    
    /**
     * Process the root element of the BRD file without side effects.
     * @param elt root element of the BRD
     * @param pm ProblemModel to set up
     * @return BRD version; null if no version attribute
     */
    private String processStateGraphElement(Element elt, ProblemModel pm) {
    	String brdVersion  = elt.getAttributeValue(ProblemStateReader.VERSION_ATTR);
    	
    	if (elt.getAttributeValue("lockWidget") != null) {
    		boolean LW = elt.getAttributeValue("lockWidget").equals("true");
    		pm.setLockWidget(LW);
    	}
    	
    	if (elt.getAttributeValue(BR_Controller.HINT_POLICY) != null)
    		pm.setHintPolicy(HintPolicyEnum.fromString(elt.getAttributeValue(BR_Controller.HINT_POLICY)));
    	
    	pm.setStartNodeMessageVector(new Vector());
    	
    	// Starting state, if different from state 0.
    	behaviorRecorderMode = elt.getAttributeValue(pm.BEHAVIOR_RECORDER_MODE);
    	if (behaviorRecorderMode == null)
    		behaviorRecorderMode = elt.getAttributeValue(pm.BEHAVIOR_RECORDER_MODE.replaceFirst("b", "B"));
    	
    	// Starting state, if different from state 0.
    	studentBeginsHereStateName = elt.getAttributeValue(pm.STUDENT_BEGINS_HERE);

    	// Whether to highlight proper widget; default is true.
    	String hrs = elt.getAttributeValue("highlightRightSelection");
    	if (Boolean.FALSE.toString().equalsIgnoreCase(hrs))
    		pm.setHighlightRightSelection(false);
    	else
    		pm.setHighlightRightSelection(true);
    	
    	// Whether to warn the student on pressing the Done button in suppress feedback mode.
    	String cd = elt.getAttributeValue("confirmDone");
    	pm.setConfirmDone(cd == null ?
    			null : new Boolean(Boolean.FALSE.toString().equalsIgnoreCase(cd) ? false : true));
    	
    	//epfeifer July 5 2011 - brd header variable to indicate the tutorType
    	String tutorType = elt.getAttributeValue("tutorType");
		if (trace.getDebugCode("eep")) trace.out("eep","brd tutorType header is "+tutorType);
		try {
			//default type should be Example Tracing (no change) to maintain backwards compatibility
			if (tutorType==null || !CtatModeModel.isDefinedModeType(tutorType)) {
				if (Utils.isRuntime())       // don't change current tutorType at author time
	        		controller.getCtatModeModel().setMode(CtatModeModel.EXAMPLE_TRACING_MODE);
			} else if (CtatModeModel.isDefinedModeType(tutorType) && controller != null) {
				// NOTE: controller may be null (e.g., a load-into-file subgraph)
        		controller.getCtatModeModel().setMode(tutorType);
        		if (!Utils.isRuntime())
        			controller.getCtatFrameController().getDockedFrame().setTutorTypeLabel(tutorType);
			}
			if (tutorType!=null && !CtatModeModel.isDefinedModeType(tutorType))
				trace.err("brd tutorType header:"+tutorType+" is not recognized");
		} catch (Exception e) {
			trace.errStack("error setting tutorType frame", e);
		}
    	
    	// Unordered Mode
    	String ptsc = elt.getAttributeValue("protoTypeStateCommutative");
    	if (ptsc == null)
    		ptsc = elt.getAttributeValue("commutative");
    	if (ptsc == null)
    		ptsc = elt.getAttributeValue("unordered");

    	unorderedFlag = Boolean.parseBoolean(ptsc);

    	// -- Case Insensitive
    	String caseInsensitive = elt.getAttributeValue("protoTypeModeCaseInsensitive");
    	if (caseInsensitive == null)
    		caseInsensitive = elt.getAttributeValue("caseInsensitive");

    	pm.setCaseInsensitive((caseInsensitive != null)
    			&& caseInsensitive.equals("true"));

    	String tutorTheStudent = elt.getAttributeValue("tutorTheStudent");
    	String suppressFeedback = elt.getAttributeValue(BR_Controller.SUPPRESS_STUDENT_FEEDBACK);
    	if (tutorTheStudent != null) {
    		Boolean b = new Boolean (tutorTheStudent);
    		boolean bb = b.booleanValue();
    		suppressFeedback = new Boolean (!bb).toString();
    	}

    	FeedbackEnum suppress = FeedbackEnum.fromString(suppressFeedback);
    	pm.setSuppressStudentFeedback(suppress);

    	BR_Controller ctlr = pm.getController();
    	if (ctlr != null) {
    		boolean FCAS = elt.getAttributeValue("firstCheckAllStates").equalsIgnoreCase("true");
    		ctlr.setFirstCheckAllStatesFlag(FCAS);
    	}
    	
    	pm.setOutOfOrderMessage(elt.getAttributeValue(ProblemModel.OUT_OF_ORDER_MESSAGE));
    	
    	return brdVersion;
    }

    /**
     * Process the start state messages from the BRD.
     * @param startNodeElement parent element of start node messages
     * @param pm ProblemModel to populate
     */
    private void processStartNodeElement(Element startNodeElement, ProblemModel pm) {
        List messageElements = startNodeElement.getChildren("message");
        for (Iterator it = messageElements.iterator(); it.hasNext();) {
            Element messageElement = (Element) it.next();
            
            MessageObject message = getMessageObject(messageElement);
            pm.addStartNodeMessage(message);
//            sewall 2013/05/15: redundant w/ BR_Controller.sendStartStateMsg()
//            if (controller != null)
//            	controller.getProblemModel().addInterfaceVariables(message);
        }
        //steelers... here initialize the ETT with new VT.
        if (controller != null)
        	controller.getExampleTracer().resetTracer();
    }

    /**
     * Convert a &lt;message&gt; element into a Comm message.
     * @param messageElement
     * @return MessageObject created
     */
    private MessageObject getMessageObject(Element messageElement) {
    	return MessageObject.fromElement(messageElement);
    }
    

    /**
     * This function returns the hashtable (key: edge, obj: FoA).
     * FoA is only used by SimSt, but if the file is read not in
     * the batch file mode, or SimSt is not activated, hashtable 
     * is updated in processEdge function.
     * 
     * @return Focus of Attention Map
     */
    public Hashtable getFoATable(){
    	return this.foAMap;
    }
    
    /**
     * Get the incidence of rules on the preferred path in the given BRD file.
     * @param problemFullName full path name of BRD file to read
     * @param rulesIncidence Map with key=rule name, value=rule count
     * @return error message of form "[problemFullName]: [error description]";
     *         null if no error
     */
    public String getRulesFromBRDFile(String problemFullName, Map<String,Integer> rulesIncidence) {
        try {
            if (trace.getDebugCode("skill")) trace.out("skill", "loadBRDFileIntoProblemGraph("+problemFullName+")");
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(problemFullName);
            String result = getRulesFromPreferredPath(document, rulesIncidence);
            if (result != null)
            	result = problemFullName+": "+result;
            return result;
        } catch (JDOMException je) {
        	String result = problemFullName+": "+je.toString()+"; cause: "+je.getCause();
        	trace.err("DOM error reading problem file "+result);
        	return result;
        } catch (Exception e) {
        	String result = problemFullName+": "+e;
        	trace.err("Error reading problem file "+result);
        	return result;
        }
    }

    /**
     * Get the incidence of rules on the preferred path in the given document,
     * read from a BRD file.
     * @param document DOM document to scan
     * @param rulesIncidence  Map with key=rule name, value=rule count
     * @return error description; null if no error
     */
	private String getRulesFromPreferredPath(Document document, Map<String,Integer> rulesIncidence) {
		StringBuffer result = new StringBuffer();
		Element root = document.getRootElement();
		List edgeElts = root.getChildren("edge");
		int e = 1;
		for (Iterator it = edgeElts.iterator(); it.hasNext(); e++) {
			Element edgeElt = (Element) it.next();
			Element actionLabelElt = edgeElt.getChild("actionLabel");
			if (actionLabelElt == null) {
				result.append("Error on edge #"+e+": no actionLabel. ");
				continue;
			}
			String ppm = actionLabelElt.getAttributeValue("preferPathMark");
			if (!(Boolean.valueOf(ppm)).booleanValue())
				continue;
	        List ruleElts = edgeElt.getChildren("rule");
	        int r = 1;
	        for (Iterator it2 = ruleElts.iterator(); it2.hasNext(); r++) {
	            Element ruleElt = (Element) it2.next();
	            String ruleText = ruleElt.getChildText("text");
	            ruleText = (ruleText == null ? "" : ruleText.trim());
	            if (ruleText.length() < 1) {
					result.append("Error on edge #"+e+", rule #"+r+": no text. ");
					continue;
	            }
	            if (ruleText.equals("unnamed rule") || ruleText.equals("unnamed"))
	            	continue;
	            Integer count = (Integer) rulesIncidence.get(ruleText);
	            if (count == null)
	            	rulesIncidence.put(ruleText, new Integer(1));
	            else
	            	rulesIncidence.put(ruleText, new Integer(count.intValue()+1));
	        }
		}
		return result.toString();
	}

	/**
	 * Read a graph without side effects.
	 * @param doc DOM Document node
	 * @param pm ProblemModel to fill in
	 * @param rpc skill map to populate 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
    private void processDocument(Document document, ProblemModel pm, RuleProduction.Catalog rpc)
    		throws InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException {
    	
        Element parentElt = document.getRootElement();

        // HashSet<TreeModelListener> treeListeners = gtm.getTreeListners();
       // gtm.setTreeListeners(null);

        processStateGraphElement(parentElt, pm);

        Element groupElt = null;
        List<Element> elts = (List<Element>) parentElt.getChildren();
        for (Element elt : elts) {
        	if ("EdgesGroups".equalsIgnoreCase(elt.getName()))
        		groupElt = elt;    // must process this one after generating BRDLoadedEvent
        	else
        		processTopLevelElement(elt, this, pm, rpc);
        }
        
        ArrayList<ProblemModelEvent> creationEvents = new ArrayList<ProblemModelEvent>();
        creationEvents.addAll(nodeCreatedEvents);
        creationEvents.addAll(edgeCreatedEvents);
        BRDLoadedEvent fireMe = new BRDLoadedEvent(this, creationEvents);
        pm.fireProblemModelEvent(fireMe);
        // gtm.setTreeListeners(treeListeners);
        
        if (groupElt != null)
    		processTopLevelElement(groupElt, this, pm, rpc);

        postProcess(parentElt, pm);
    }

    /**
     * Complete any processing on the graph that requires inputs from elements in
     * different parts of the XML document. This includes<ul>
     * <li>setting the starting node (must wait until know which nodes are above it).</li>
     * </ul>
     * @param root top-level Element in the document
     * @param pm ProblemModel to update
     */
    private void postProcess(Element parentElt, ProblemModel pm) {
    	//pm.getExampleTracerGraph().redoLinkDepths();
    	if (trace.getDebugCode("br")) trace.out("br", "postProcess() hasEdgesGroups "+hasEdgesGroups+
    			", unorderedFlag "+unorderedFlag);
    	if (!hasEdgesGroups)
    		pm.getExampleTracerGraph().getGroupModel().setGroupOrdered(
    				pm.getExampleTracerGraph().getGroupModel().getTopLevelGroup(), !unorderedFlag);
    	
    	if (trace.getDebugCode("pm")) trace.outln("pm", "setStudentBeginsHereState("+studentBeginsHereStateName+
    			"), setBehaviorRecorderMode("+behaviorRecorderMode+")");
		pm.setStudentBeginsHereState(studentBeginsHereStateName);
		pm.setBehaviorRecorderMode(behaviorRecorderMode);
	}

	/**
     * Load a BRD without side effects. Returns a ProblemModel disconnected
     * from the current BR_Controller.
     * @param problemFullName  filename
     * @param ruleProductionCatalog add RuleProduction instances to this map
     * @return ProblemModel created; null if unsuccessful
     */
    public ProblemModel loadBRDFileIntoProblemModel(String problemFullName,
    		RuleProduction.Catalog ruleProductionCatalog) {
        try {
            if (trace.getDebugCode("br")) trace.out("br", "READ ProblemModel WITH JDOM: " + problemFullName);

            SAXBuilder builder = new SAXBuilder();
            
            Document document = null;
            
            File problemFile = new File(problemFullName);
            URI uri = problemFile.toURI();
            try
            {
            	document = builder.build(problemFile);
            }
            catch(Exception e) {}
            if(document == null)
            	problemFile = Utils.getFileAsResource(uri.toURL());
        	
            if(uri.getScheme().equals("jar"))
            {
            	String uriString = uri.toString();
            	String jarfilename = uriString.substring(0, uriString.lastIndexOf(".jar") + 4);
            	String jarentryname = uriString.substring(uriString.lastIndexOf(".jar") + 6);
            	JarFile jarfile = new JarFile(new File(jarfilename));
        		JarEntry jarentry = jarfile.getJarEntry(jarentryname);
        		document = builder.build(jarfile.getInputStream(jarentry));
            }
            else
            {
            	document = builder.build(problemFile);
            }
            
            ProblemModel problemModel = new ProblemModel(null);  // null=>no BR_Controller
            if (problemModel != null) {
            	String problemName = Utils.getBaseName(problemFullName);
            	problemModel.reset(problemName, problemFullName); // set subproblem's name
                processDocument(document, problemModel, ruleProductionCatalog);
            }
            return problemModel;

        } catch (Exception e) {
            if (trace.getDebugCode("pm")) trace.out("pm", "error reading file " + problemFullName + ": " + e);
            e.printStackTrace();
            return null;
        }
    }

	/**
	 * @return the {@link #behaviorRecorderMode}
	 */
	public String getBehaviorRecorderMode() {
		return behaviorRecorderMode;
	}
}
