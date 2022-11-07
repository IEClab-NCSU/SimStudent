/*
 * Created on Sep 11, 2005
 * 
 * Author: Kuok Chiang, Kim
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jgraph.graph.GraphLayoutCache;
import org.xml.sax.SAXException;

import pact.CommWidgets.UniversalToolProxy;

import com.megginson.sax.DataWriter;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.DialogueSystemInfo;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.SolverMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.VectorMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.ActionLabelElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.EdgeElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.FocusOfAttentionElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.MatcherElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.MessageElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.NodeElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.PropertiesElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.SimStElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.SolverMatcherElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.StartNodeMessagesElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.StateGraphElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.VectorMatcherElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.VectorProperty;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.BehaviorRecorder.View.RuleLabel;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.BR_JGraphNode;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.BR_JGraphVertexView;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.model.StartStateModel;
import edu.cmu.pact.ctat.view.CtatFrame;

/**
 * @author Kuok Chiang, Kim
 * This controller creates the necessary data structures 
 * with respect to the XML elements in a brd file. 
 * Graph States are extracted from BR_Controller and dumped into
 * an XML file.
 */
public class ProblemStateWriter {
	
	/** Output generator for multi-line string output. */
	public static final XMLOutputter multiLineOutputter = new XMLOutputter();
	static {
		Format fmt = Format.getPrettyFormat();
		fmt.setOmitDeclaration(true);
		fmt.setLineSeparator("\r\n");
		fmt.setIndent("    ");
		multiLineOutputter.setFormat(fmt);
	}
	
    private BR_Controller controller;
    
    /* Modified 06/14/2013 to take in a model/view pair to attach to, allocating
     * one reader/writer per pair rather than a single reader/writer for all pairs
     * (which results in serialization issues). 
     */
    public ProblemStateWriter(BR_Controller controller) {
    	this.controller = controller;
    }

    /**
     * Save the current graph to the given file.
     * @param problemFullName filename
     * @return filename if successful; null if not
     */
    public String saveBRDFile(String problemFullName) {
    	return saveBRDFile(problemFullName, null);
    }

    /**
     * Save the current graph to the given file.
     * @param problemFullName filename
     * @param imageToWrite if not null, byte array already holding image to write
     * @return filename if successful; null if not
     */
    public String saveBRDFile(String problemFullName, byte[] imageToWrite) {
        try {
        	final FileOutputStream fos = new FileOutputStream(problemFullName);
        	if (imageToWrite == null)
        		imageToWrite = createBRDDiskImage();
        	fos.write(imageToWrite);
        	fos.close();
        	if (this.controller != null && this.controller.getProblemStateReader() != null)
        		this.controller.getProblemStateReader().setSavedImage(imageToWrite);
        	controller.modifyStartState(controller.getProblemModel().getProblemName());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Throwable cause = ioe.getCause();
            String errMsg = ioe.toString() + (cause == null ? "" : "; cause: "+cause.toString());
            Utils.showExceptionOccuredDialog(null, errMsg, "Error saving graph");  // null: no stack trace on error display
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            String errMsg = e.toString() + (cause == null ? "" : "; cause: "+cause.toString());
            Utils.showExceptionOccuredDialog(null, errMsg, "Error saving graph");  // null: no stack trace on error display
            return null;
        }
        controller.getLoggingSupport().saveFileToAuthorLog(problemFullName, BR_Controller.SAVE_FILE);
        return problemFullName;
    }

    /**
     * Serialize the current graph to a buffer.
     * @return byte array with the disk image of the file
     * @throws Exception
     */
    private byte[] createBRDDiskImage() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String errMsg = saveBRDFile(baos);
		if (errMsg != null) {
            Utils.showExceptionOccuredDialog(null, errMsg, "Error saving graph");  // null: no stack trace on error display
			throw new Exception(errMsg);
		}
		return baos.toByteArray();
	}
    
    /************ Public Mirror for UNDO TEST 1337 ******************/

    public byte[] createBRDDiskImagePublic() throws Exception {
    	trace.out("***1337*** CREATEBRDISKIMAGEPUBLIC");
    	return createBRDDiskImage();
    }
    

	/**
     * Save the current graph to the given output stream.
     * @param outputStream
     * @return null if successful; error message if an exception was thrown
     */
    public String saveBRDFile(OutputStream outputStream) {
        try {
        	PrintWriter pWriter = new PrintWriter(outputStream);
            DataWriter writer = new DataWriter(pWriter);
        
        	StateGraphElement xmlstategraph = writeRootElement();
        	//update frame to reflect that the brd tutor type may have changed
        	try {
        		if(!Utils.isRuntime()) {
        			CtatFrame cf = (controller.getCtatFrameController() == null ?
        					null : controller.getCtatFrameController().getDockedFrame());
        			if(cf != null)
        				cf.setTutorTypeLabel(controller.getCtatModeModel().getCurrentMode());
        		}
        	} catch (Exception e) {
        		trace.errStack("error setting tutorType frame", e);
        	}
        	
            writeStartNodeMessages(writer, xmlstategraph);
        
            writeNodeElements(xmlstategraph);
            
            writeEdgeElements(writer, xmlstategraph);
        
            writeProductionRules(xmlstategraph);
            
            writeLinkGroups(xmlstategraph);
                        
            writeDocumentToFile(writer, xmlstategraph);    		
            outputStream.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            return e.toString() + (cause == null ? "" : "; cause "+cause.toString());
        } catch (SAXException e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            return e.toString() + (cause == null ? "" : "; cause "+cause.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            return e.toString() + (cause == null ? "" : "; cause "+cause.toString());
        }
        return null;
    }


    private MessageElement writeCommMsg(MessageObject m, DataWriter writer) {
        if (m == null)
            return null;
        
        MessageElement xmlmsg = new MessageElement();
        xmlmsg.addverb(m.getVerb());
        try {            
        	PropertiesElement xmlprop = new PropertiesElement();
			
            List<String> propertyNames = m.getPropertyNames();
			
            for (int j = 0; j < propertyNames.size(); j++) {
            	String propertyName = propertyNames.get(j);
            	Object propertyValue = m.getProperty(propertyName);
                
            	// elements that have multiple instances of <value>
                if (propertyValue instanceof Vector) {
					VectorProperty vectorProperty =
						new VectorProperty(propertyName, (Vector) propertyValue);
					xmlprop.addProperty(propertyName, vectorProperty);
                } else if (propertyValue instanceof Element) {
					xmlprop.addProperty(propertyName, propertyValue);
                } else {
                	String inputvalue = propertyValue.toString();
					xmlprop.addProperty(propertyName, inputvalue);
                }
				
                xmlmsg.addproperties(xmlprop);
            }
        } catch (Exception e) {
            trace.err("ProblemStateWriter.java: exception = " + e);
        }
        return xmlmsg;
    }

    /**
     * @param writer
     * @param xmlstategraph
     * @throws SAXException
     */
    void writeDocumentToFile(DataWriter writer, StateGraphElement xmlstategraph) throws SAXException {
        // print out XML to file
        writer.setIndentStep(4);
        writer.startDocument();
        xmlstategraph.printXML(writer);
        writer.endDocument();
        
    }


    /**
     * @param xmlstategraph
     */
    private void writeLinkGroups(StateGraphElement xmlstategraph) {
    	xmlstategraph.addGroupModel(controller.getExampleTracerGraph().getGroupModel());            
    }


    /**
     * Serialize the {@link ProblemModel#getRuleProductionList()} into &lt;productionRule&gt;
     * elements.
     * @param xmlstategraph
     */
    private void writeProductionRules(StateGraphElement xmlstategraph) {

    	RuleProduction.Catalog rpc = controller.getRuleProductionCatalog();
    	
    	// sewall 2009/06/26: old code got the RuleProductions from EdgeData#getRuleLabel() calls
    	if (trace.getDebugCode("pm")) trace.out("pm", "writeProductionRules() list before culling: "+rpc);

    	ProblemModel pm = this.controller.getProblemModel();
    	Map<String, RuleProduction> rulesInUse = pm.updateOpportunityCounts();
    	for (RuleProduction rp : rulesInUse.values()) {
            xmlstategraph.addProductionRule(rp.getRuleName(), rp.getProductionSet(),
            		rp.getHints(), rp.getOpportunityCount(),
            		rp.getLabel(), rp.getDescription());
        }
    }


    /**
     * Creates a matcher element from a matcher
     * @param tmpMatcher
     * @return
     */
    public static MatcherElement getMatcherElement(Matcher tmpMatcher)
    {
    	MatcherElement xmlMatcher = new MatcherElement();
    	xmlMatcher.setReplacementFormula(tmpMatcher.getReplacementFormula());
    	xmlMatcher.addmatcherType(tmpMatcher.getMatcherClassType());
    	for (int p = 0; p < tmpMatcher.getParameterCount(); ++p) {
    		Matcher.MatcherParameter param = tmpMatcher.getMatcherParameter(p);
    		xmlMatcher.addmatcherParameter(param);
    	}
    	return xmlMatcher;
    }
    
    /**
     * @param writer
     * @param xmlstategraph
     */
    private void writeEdgeElements(DataWriter writer, StateGraphElement xmlstategraph) {
       
        ProblemNode vertexNode;
        //save edge info based on creation orders
        //trace.out ( "save edges");

        Vector edgesVector = new Vector();

        Enumeration<ProblemEdge> iterEdges = this.controller.getProblemModel().getProblemGraph().edges();
        while (iterEdges.hasMoreElements())
            edgesVector.addElement(iterEdges.nextElement());

        int edgesNumber = edgesVector.size();

        ProblemEdge tempEdge;
        EdgeData myEdge;
        RuleLabel tempRuleLabel;
        for (int i = edgesNumber - 1; i >= 0; i--) {
        	// <edge>
        	EdgeElement xmledge = new EdgeElement();                
            tempEdge = (ProblemEdge) edgesVector.elementAt(i);
            myEdge = tempEdge.getEdgeData();

            //save action-label                
            // <actionLabel>
            ActionLabelElement xmlActionLabel = new ActionLabelElement();
            xmlActionLabel.addpreferPathMark(myEdge.isPreferredEdge());  
            // add for integration of Dialogue System
			
			DialogueSystemInfo dialogueSystemInfo = myEdge.getDialogueSystemInfo();
			
            xmlActionLabel.addstudentHintRequest(dialogueSystemInfo.getStudent_Hint_Request());
            xmlActionLabel.addstepSuccessfulCompletion(dialogueSystemInfo.getStep_Successful_Completion());
            xmlActionLabel.addstepStudentError(dialogueSystemInfo.getStep_Student_Error());
            
            xmlActionLabel.adduniqueID(myEdge.getUniqueID());
            
            MessageElement tmpmsg = writeCommMsg(myEdge.getDemoMsgObj(), writer);
            if (tmpmsg != null)
            	xmlActionLabel.addmessage(tmpmsg);
            
            xmlActionLabel.addbuggyMessage(myEdge.getBuggyMsg());
            xmlActionLabel.addsuccessMessage(myEdge.getSuccessMsg());

            for (String hint : myEdge.getAllNonEmptyHints() )
                xmlActionLabel.addhintMessage(hint);                 
            
            xmlActionLabel.setCallbackFn(myEdge.getCallbackFn());

            xmlActionLabel.addactionType(myEdge.getActionType());
            xmlActionLabel.addoldActionType(myEdge.getOldActionType());
            xmlActionLabel.addcheckedStatus(myEdge.getCheckedStatus());
            
            xmlActionLabel.setMinTraversals(myEdge.getMinTraversalsStr());
            xmlActionLabel.setMaxTraversals(myEdge.getMaxTraversalsStr());
            
            // <matcher>
            Matcher tmpMatcher = myEdge.getMatcher();
            if (tmpMatcher != null) {
            	if(tmpMatcher instanceof SolverMatcher)
            	{
            		VectorMatcherElement xmlSolverMatcher = new SolverMatcherElement((SolverMatcher)tmpMatcher);
            		xmlActionLabel.addVectorMatcher(xmlSolverMatcher);
            	}
            	else if(tmpMatcher instanceof VectorMatcher)
            	{
            		VectorMatcherElement xmlVectorMatcher = new VectorMatcherElement((VectorMatcher)tmpMatcher);
            		xmlActionLabel.addVectorMatcher(xmlVectorMatcher);
            	}
            	else
            	{
            		MatcherElement xmlMatcher = getMatcherElement(tmpMatcher);
	            	xmlActionLabel.addMatcher(xmlMatcher);
            	}
            }
            // </matcher>
            
            
            xmledge.addactionLabel(xmlActionLabel);
            // </actionLabel>
                            
            xmledge.addprecheckedStatus(myEdge.getPreLispCheckLabel().preCheckedStatus);
            
            //save rule-labels
            int ruleNumbers = myEdge.getRuleLabels().size();
            // <rule>
            for (int j = 0; j < ruleNumbers; j++) {	
                tempRuleLabel = (RuleLabel) myEdge.getRuleLabels().elementAt(j);                    
                xmledge.addrule(tempRuleLabel.getText(), -1);
            }
            // </rule>
            
            //save Source and DEST nodes UniqueIDs
            vertexNode =
                tempEdge
                    .getNodes()[ProblemEdge
                    .SOURCE];
            xmledge.addsourceID(vertexNode.getUniqueID());
            
            vertexNode =
                tempEdge.getNodes()[ProblemEdge.DEST];
            xmledge.adddestID(vertexNode.getUniqueID());
            xmledge.addtraversalCount(myEdge.getTraversalCount());
            
            Hashtable foAMap;
            Vector foAList;
            
            //get FoATable from SimSt.
            if(controller.getCtatModeModel().isSimStudentMode()){
            	foAMap = controller.getMissController().getSimSt().getFoaTable();
            	// trace.out("miss", "processEdge() with miss : " + foAMap);
            	Hashtable foAMapTemp;
            	
            	if(this.controller.getProblemStateReader().getProblemStateReaderJDom() != null){
            		foAMapTemp = this.controller.getProblemStateReader()
					.getProblemStateReaderJDom().getFoATable();    	
            		// trace.out("miss", "problemstatewriter.processEdges() from JDOM");
            	
            		foAMap.putAll(foAMapTemp);
            	}
            	
                //get FoA list that is associated with edge
                foAList = (Vector)foAMap.get(tempEdge);
                //if FoA list exists, add to the graph
	            //<SimSt>
	            if(foAList != null){
	            	SimStElement simElem = new SimStElement(); 
		            for(int k=0; k < foAList.size(); k++){
		            	FocusOfAttentionElement foAElem = new FocusOfAttentionElement();
		            	foAElem.addTarget((String)foAList.get(k));
		            	simElem.addFoA(foAElem);
		            }
		            xmledge.addSimSt(simElem);
	            }
            } 
            else if(this.controller.getProblemStateReader().getProblemStateReaderJDom() != null){
                // trace.out("miss", "problemstatewriter.processEdges() without miss active");
	            foAMap = this.controller.getProblemStateReader()
				.getProblemStateReaderJDom().getFoATable();    	
	            //get FoA list that is associated with edge
	             foAList = (Vector)foAMap.get(tempEdge);
            
	            //if FoA list exists, add to the graph
	            //<SimSt>
	            if(foAList != null){
	            	SimStElement simElem = new SimStElement(); 
		            for(int k=0; k < foAList.size(); k++){
		            	FocusOfAttentionElement foAElem = new FocusOfAttentionElement();
		            	foAElem.addTarget((String)foAList.get(k));
		            	simElem.addFoA(foAElem);
		            }
		            xmledge.addSimSt(simElem);
	            }
            }
            
            for(int l=0;l<myEdge.getAssociatedElements().size();l++) {
            	xmledge.addassociation(myEdge.getAssociatedElements().get(l).toString(), 
            						   myEdge.getAssociatedElementsValues().get(l).toString());                    
            }
            
            xmlstategraph.addEdge(xmledge);
            // </edge>
        }// end of for loop
    }


    /**
     * @param xmlstategraph
     */
    private void writeNodeElements(StateGraphElement xmlstategraph) {
        //save state nodes info based on creation orders
        Vector nodesVector = new Vector();

        Enumeration<ProblemNode> iterNodes = this.controller.getProblemModel().getProblemGraph().nodes();
        while (iterNodes.hasMoreElements())
            nodesVector.addElement(iterNodes.nextElement());

        int nodesNumber = nodesVector.size();

        //trace.out ( "save state nodes");

        ProblemNode tempNode;
        NodeView nodeView = null;
        // <node>
        for (int i = nodesNumber - 1; i >= 0; i--) {
            tempNode = (ProblemNode) nodesVector.elementAt(i);
            nodeView = tempNode.getNodeView();

            NodeElement xmlnode = new NodeElement();
            xmlnode.addlocked(nodeView.getLocked());
            xmlnode.adddoneState(tempNode.getDoneState());
            
            xmlnode.addtext(nodeView.getText());
            xmlnode.adduniqueID(tempNode.getUniqueID());
            
            BR_JGraphNode graphNode = nodeView.getProblemNode().getJGraphNode();
            if(!Utils.isRuntime()) {
            	GraphLayoutCache graphView =
            			this.controller.getJGraphWindow().getJGraphController().getGraphView();
            	BR_JGraphVertexView jgraphNodeView = (BR_JGraphVertexView) graphView.getMapping(graphNode, false);
            	if (jgraphNodeView == null) trace.err("jgraphNodeView null: graphNode "+graphNode+", graphView "+
            			graphView+", nodeView "+nodeView+", .text "+nodeView.getText());
            	Rectangle2D rect = jgraphNodeView.getBounds();
                xmlnode.adddimension((int) rect.getX(), (int) rect.getY());
            }
            
            xmlstategraph.addNode(xmlnode);
        }
        // </node>
    }


    /**
     * @return
     */
    private StateGraphElement writeRootElement() {
        StateGraphElement xmlstategraph = new StateGraphElement();
        ProblemModel pm = this.controller.getProblemModel();
        xmlstategraph.addcaseInsensitive(pm.isCaseInsensitive());
        xmlstategraph.addfirstCheckAllStates(controller.isFirstCheckAllStatesFlag());
        xmlstategraph.addlockWidget(pm.isLockWidget());
        xmlstategraph.addHintPolicy(pm.getHintPolicy());
        xmlstategraph.addversion(ProblemStateReader.CURRENT_BRD_VERSION);
        xmlstategraph.addunordered(pm.isUnorderedMode());
        xmlstategraph.addSuppressStudentFeedback(pm.getSuppressStudentFeedback());
        xmlstategraph.addHighlightRightSelection(pm.getHighlightRightSelection());
        xmlstategraph.addConfirmDone(pm.getConfirmDone());  // could return null
        xmlstategraph.addStudentBeginsHereStateName(pm.getStudentBeginsHereNameForBRD());
        xmlstategraph.addTutorType(controller);
        xmlstategraph.addOutOfOrderMessage(pm.getOutOfOrderMessage());
        
        return xmlstategraph;
    }


    /**
     * Get the start node messages from {@link UniversalToolProxy#startStateModelIterator()}
     * if available. Else from {@link ProblemModel#startNodeMessagesIterator()}.
     * @param writer
     * @param xmlstategraph
     */
    private void writeStartNodeMessages(DataWriter writer, StateGraphElement xmlstategraph) {
    	writeStartNodeMessages(writer, xmlstategraph,
    			controller.getProblemModel().startNodeMessagesIterator());
    }


    /**
     * Get the start node messages from {@link UniversalToolProxy#startStateModelIterator()}
     * if available. Else from {@link ProblemModel#startNodeMessagesIterator()}.
     * @param writer
     * @param xmlstategraph
     * @param it source of start state messages
     */
    void writeStartNodeMessages(DataWriter writer, StateGraphElement xmlstategraph, Iterator<MessageObject> it) {
        StartNodeMessagesElement xmlstartnodemessages = new StartNodeMessagesElement();
        if(controller == null) {
            xmlstategraph.addStartNodeMessages(xmlstartnodemessages);
            return;
        }
        while(it.hasNext()) {
        	MessageObject msg = it.next();
        	if (!StartStateModel.isProperStartStateMessage(msg))
        		continue;
        	MessageElement tmpmsg = writeCommMsg(msg, writer);	
        	if (tmpmsg != null)
        		xmlstartnodemessages.addmessage(tmpmsg);
        }
        xmlstategraph.addStartNodeMessages(xmlstartnodemessages);
    }
    
    /**
     * Add a message to the {@link StartNodeMessagesElement}.
     * @param msg message to add
     * @param writer
     * @param xmlstartnodemessages
     */
    private void addToSNMElement(MessageObject msg, DataWriter writer,
    		StartNodeMessagesElement xmlstartnodemessages) {
    }

}// end of ProblemStateWriter
