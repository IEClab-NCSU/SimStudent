package edu.cmu.pact.miss.ProblemModel.Graph;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemStateReader;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExactMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherFactory;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.SolverMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.VectorMatcher;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;

public class SimStBrdGraphReader {

	
	private SimStProblemGraph simStGraph;
	
	public SimStProblemGraph getProblemGraph()
	{
		return simStGraph;
	}
	
	
	 /**
     * Common entry point to load a BRD file. 
     * @param filename path to load
     */
    public boolean openBRDFile(String filename) {
        if (filename == null || filename.length() <= 0)
            return false;

        InputStream is;
    	File f;
    	String absolutePath = null;
        String title = "Error loading graph file";  // for exceptions
        try {
            trace.out("br", "READ FILE WITH JDOM: " + filename);
            f = null;
            URL url = new URL(filename);
            is = url.openStream();
            f = Utils.getFileAsResource(url);
            try {
                 absolutePath = f.getCanonicalPath();
            } catch (Exception d){
            	absolutePath = filename; // if getCanonicalPath fails because
            									 // reading brd from jar, then nasty but O.K.
            									 // since we'll never do save to jar in student use.
            }
        } catch (Exception e1) {
        	try {
        		f = new File(filename);
        		is = new FileInputStream(f);
        		absolutePath = f.getCanonicalPath();
            } catch (Exception e2) {
            	String message = "<html>Error opening file " + filename + ":<br/>"+
            		e2+(e2.getCause() == null ? "" : ".<br/>Cause: "+e2.getCause());
                e2.printStackTrace();
                return false; // table left empty if bad file
            }
        }
        /*ByteArrayOutputStream baos = new ByteArrayOutputStream(64*1024); 
        try {
        	BufferedInputStream bis = new BufferedInputStream(is);
        	for (int c = -1; 0 <= (c = bis.read()); baos.write(c));
        } catch (Exception e2) {
        	String message = "<html>Error reading file " + filename + ":<br/>"+
    		e2+(e2.getCause() == null ? "" : ".<br/>Cause: "+e2.getCause());
        	e2.printStackTrace();
        	return false; // table left empty if bad file
        }*/
		
	    boolean result = loadBRDFileIntoSimStGraph(is);
        
    	return result;
    }
    
    /**
     * Load a BRD given a file name.
     * 
     * @param problemFullName
     * @return true if successful
     */
    boolean loadBRDFileIntoSimStGraph(InputStream inputStream) {
    	
    	simStGraph = new SimStProblemGraph();
    	
        try {
            if (trace.getDebugCode("br")) trace.out("br", "READ InputStream WITH JDOM");
            Document doc = parse (inputStream);
            
            processDocument(doc, simStGraph);
            
        } catch (Exception e) {
            if (trace.getDebugCode("pm")) trace.out("pm", "error reading inputStream: " + e);
            e.printStackTrace();
            return false; // table left empty if bad file
        }
        return true;
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
    private void processDocument(Document document, SimStProblemGraph graph)
    		throws InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException 
	{
        Element parentElt = document.getRootElement();
       // HashSet<TreeModelListener> treeListeners = gtm.getTreeListners();
       // gtm.setTreeListeners(null);
        List elts = parentElt.getChildren();
        //processStateGraphElement(parentElt, pm);
        //else if (elementName.equals("node"))
        for (Iterator it = elts.iterator(); it.hasNext();) {
            Element e = (Element) it.next();
            if(e.getName().equals("node"))
            	processNode(e, graph);
            else if(e.getName().equals("edge"))
            	processEdge(e, graph);
        }
               
        //postProcess(parentElt, pm);
    }
    
    private void processNode(Element elt, SimStProblemGraph graph) 
    {
      Element text = elt.getChild("text");
      String nodeidStr = elt.getChild("uniqueID").getValue();
      int nodeid = (new Integer(nodeidStr)).intValue();
      String name = text.getValue();
		
      final SimStNode problemNode = new SimStNode(name, graph);
      problemNode.setUniqueID(nodeid);
      
		if (elt.getAttributeValue("doneState") != null) {
			boolean doneState = elt.getAttributeValue("doneState").equals("true");
			problemNode.setDoneState(doneState);
		}

      SimStNode tempNode = graph.addSSNode(problemNode);
      
      if (graph.getStartNode() == null) {
          graph.setStartNode(tempNode);
      }
      
  }
    

    private void processEdge(Element edgeElement, SimStProblemGraph graph)
        throws InstantiationException, IllegalAccessException, ClassNotFoundException, 
            SecurityException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException 
    {
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

            SimStEdgeData edgeData = createEdgeDataForEdge(graph,actionLabelElement,
            		ID, buggymsg, success, actionType, checked, isPreferPath);
            
            addMatcherForEdge(actionLabelElement, messageObject, edgeData);
            
            edgeData.setMinTraversalsStr(actionLabelElement.getAttributeValue("minTraversals"));
            edgeData.setMaxTraversalsStr(actionLabelElement.getAttributeValue("maxTraversals"));
            
            addRulesForEdge(edgeElement, edgeData);
            
            SimStNode sourceNode = getNodeForVertexUniqueID(sourceID, graph);
            SimStNode destinationNode = getNodeForVertexUniqueID(destID, graph);
            if (destinationNode == null) {
                throw new RuntimeException(
                        "Could not locate destination node for UniqueID "
                                + destID);
            }
            if (sourceNode == null) {
                throw new RuntimeException(
                        "Could not locate source node for UniqueID "
                                + sourceID);
            }
            edgeData.setTraversalCount(traversalCount);
            
            SimStEdge newEdge = graph.addSSEdge(sourceNode, destinationNode, edgeData);
            
            if (newEdge == null) {
                throw new RuntimeException(
                        "Error occuring adding edge between source "
                                + sourceNode + " and destination "
                                + destinationNode);
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
    private SimStEdgeData createEdgeDataForEdge(SimStProblemGraph graph,
    		Element actionLabelElement,	int ID, String buggymsg, String success, String actionType,
    		String checked, boolean isPreferPath) {
        SimStEdgeData edgeData = new SimStEdgeData();

        List hintList = actionLabelElement.getChildren("hintMessage");
        for (Iterator hints = hintList.iterator(); hints.hasNext();) {
            Element hint = (Element) hints.next();
            String hintMessage = hint.getText();
            if (trace.getDebugCode("jdomreader")) trace.out("jdomreader", "hint message = " + hintMessage);
            edgeData.addHint(hintMessage);
        }
        
        edgeData.setBuggyMsg(buggymsg);
        edgeData.setCheckedStatus(checked);
        if (actionType != null)
            edgeData.setActionType(actionType);
        edgeData.setSuccessMsg(success);
        edgeData.setPreferredEdge(isPreferPath);
        edgeData.setUniqueID(ID);
        return edgeData;
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
     * Add rules defined in the <edge> element. They may be echoed in the
     * <productionRule> elements
     * @param edgeElement
     * @param edgeData
     * @param rpc
     */
    private void addRulesForEdge(Element edgeElement, SimStEdgeData edgeData) {
    	
        List ruleElements = edgeElement.getChildren("rule");

        for (Iterator elements = ruleElements.iterator(); elements.hasNext();) {

            Element ruleElement = (Element) elements.next();
            String ruleLabelText = ruleElement.getChildText("text");
            edgeData.addRuleName(ruleLabelText);
        }
    }
    
 // find the node containing Vertex
    public static SimStNode getNodeForVertexUniqueID(int vertexUniqueID,
            SimStProblemGraph problemGraph) {
        SimStNode node = problemGraph.getFirstNode();
                
        while (node != null)
        {
            if (node.getUniqueID() == vertexUniqueID)
                return node;
            node = node.getNextNode();
        }

        return null;
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
    private void addMatcherForEdge(Element actionLabelElement, MessageObject messageObject, 
    		SimStEdgeData edgeData) throws InstantiationException, IllegalAccessException, 
    		ClassNotFoundException, SecurityException, IllegalArgumentException, 
    		NoSuchMethodException, InvocationTargetException 
    {
        Element matcherElement = actionLabelElement.getChild("matcher");
        
        Vector<String> currentSelection = messageObject.getSelection();
        Vector<String> currentAction = messageObject.getAction();
        Vector<String> currentInput = messageObject.getInput();
        
        if (matcherElement != null) {
            Matcher m = createMatcher(matcherElement);
            edgeData.setMatcher(m);
            
			m.setDefaultSelectionVector(currentSelection);
            m.setDefaultActionVector(currentAction);
            m.setDefaultInputVector(currentInput);
            
        } else {
            ExactMatcher m = buildDefaultExactMatcher(messageObject, edgeData);
            if (m != null)
                edgeData.setMatcher(m);
        }
        
        edgeData.setSelection(currentSelection.get(0));
        edgeData.setAction(currentAction.get(0));
        edgeData.setInput(currentInput.get(0));
                
    }
    
    private Matcher createMatcher(Element matcherElement) throws InstantiationException,
	IllegalAccessException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException 
	{ 
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
	    
	    List paramList = matcherElement.getChildren("matcherParameter");
	    int count = 0;
	    for (Iterator params = paramList.iterator(); params.hasNext();) {
	        Element e = (Element) params.next();
	        m.setParameter(e, count);
	        count++;
	    }
	    m.setReplacementFormula(matcherElement.getAttributeValue("replacementFormula"));
	    
	    return m;
	}
	
	/**
     * @param messageObject
     * @param edgeData
     * @return
     */
    private ExactMatcher buildDefaultExactMatcher(MessageObject messageObject, SimStEdgeData edgeData) {
    	Vector<String> selection = messageObject.getSelection();
    	Vector<String> action = messageObject.getAction();
    	Vector<String> input = messageObject.getInput();
    	ExactMatcher m = new ExactMatcher(selection, action, input);
    	edgeData.setMatcher(m);
    	return m;
    }
    
}
