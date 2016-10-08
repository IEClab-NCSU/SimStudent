/*
 * Created on Apr 22, 2004
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;

import pact.CommWidgets.TutorWrapper;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.DialogueSystemInfo;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.AnyMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExactMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.RangeMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.RegexMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.WildcardMatcher;
import edu.cmu.pact.BehaviorRecorder.SolutionStateModel.SolutionState;
import edu.cmu.pact.BehaviorRecorder.View.ActionLabel;
import edu.cmu.pact.BehaviorRecorder.View.CheckLispLabel;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.BehaviorRecorder.View.RuleLabel;
import edu.cmu.pact.SocketProxy.SocketToolProxy;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.model.CtatModeModel;

/**
 * 
 * Added ProblemStateWriterTest cases in each test method. Opens the file and
 * outputs to temporary BRD file that is re-read. by Kim K.C. on 9/30/05
 * 
 */
public class ProblemStateReaderTest extends TestCase {
	
	static {
    	trace.addDebugCode("node");
	}

    private final static String brdFile = "test/edu/cmu/pact/BehaviorRecorder/ProblemModel/ProblemStateReaderTest.brd";

    private final static String brdFalse = "test/testFalse.brd";

    private final static String brdTrue = "test/testTrue.brd";

    private final static String brdMultiple = "test/testMultiple.brd";

    private final static String brdEdge = "test/OneEdge.brd";
    
    private final static String brdSkills = "test/678plus187.brd";
    
    /** BRD file for {@link #testMatcherJDOM()}. */
    private final static String brdMatcher = "test/Matcher.brd";
    
    /** BRD file for {@link #testStateGraphJDom()}. */
    private static final String stateGraphElementBrd = "test/1-3plus2-5Jess.brd";
    
    private final static String dialogueStudentHintRequest =
    	"This is the hint request to the dialogue system.";
    private final static String dialogueStepSuccessfulCompletion =
    	"This is the success request to the dialogue system.";
    private final static String dialogueStepStudentError =
    	"This is the error request to the dialogue system.";

    
    
    private static CTAT_Launcher cLauncher = new CTAT_Launcher(new String[] { CTAT_Launcher.SKIP_MONITOR_ARG });
    
    private static BR_Controller controller = cLauncher.getFocusedController();

    private ProblemStateWriterTestUtility writerTest;

    private Vector nodeVector = new Vector();

    private Vector edgeVector = new Vector();

    private ActionLabel testLabel;

    private EdgeData testEdgeData;

    private ProblemEdge edge1;

    public void _testEdgeJDom() {

        readFileWithJDom(brdEdge);
        createEdgeVector();
        EdgeTest();

        return;
    }

    public void _testEdgeSAX() throws IOException, ParserConfigurationException,
            SAXException, ProblemModelException, FactoryConfigurationError {

        readFileWithSax(brdEdge);
        createEdgeVector();
        EdgeTest();
        writerTest.testWriteAndReread();
        createEdgeVector();
        EdgeTest();

        return;
    }

    public void testLoadGraph() throws IOException,
            ParserConfigurationException, SAXException,
            ProblemModelException, FactoryConfigurationError {
        controller.getProblemStateReader().openBRDiagramFile(brdFile);
        String problemName = controller.getProblemModel().getProblemName();
        writerTest.writeAndReread(problemName);
    }

    public void testSetupJDOM() throws FactoryConfigurationError {

        readFileWithJDom(brdTrue);
        _testAllTrue();

        readFileWithJDom(brdFalse);
        _testAllFalse();

    }

    public void _testSetupSAX() throws IOException,
            ParserConfigurationException, ProblemModelException, SAXException,
            FactoryConfigurationError {

        /* Test All True */
        readFileWithSax(brdTrue);
        _testAllTrue();
        // test writer output
        writerTest.testWriteAndReread();
        _testAllTrue();

        /* Test All False */
        readFileWithSax(brdFalse);
        _testAllFalse();
        writerTest.testWriteAndReread();
        _testAllFalse();
    }

//    public void testNodeSAX() throws IOException, ParserConfigurationException,
//            SAXException, FactoryConfigurationError {
//
//        InitNodeVector();
//        readFileWithSax(brdMultiple);
//        NodeTest();
//        writerTest.writeAndReread();
//        NodeTest();
//
//    }

    public void testNodeJDom() throws FactoryConfigurationError {

        InitNodeVector();
        readFileWithJDom(brdMultiple);
        NodeTest();

    }
    
    public void testStateGraphJDom() throws FactoryConfigurationError {
        readFileWithJDom(stateGraphElementBrd);
        ProblemModel pm = controller.getProblemModel();
        assertEquals(stateGraphElementBrd+": stateGraph.behaviorRecorderMode",
        		CtatModeModel.JESS_MODE, pm.getBehaviorRecorderMode());
        assertNull(stateGraphElementBrd+": stateGraph.confirmDone",
        		pm.getConfirmDone());
        assertFalse(stateGraphElementBrd+": stateGraph.highlightRightSelection",
        		pm.getHighlightRightSelection());
        assertFalse(stateGraphElementBrd+": stateGraph.lockWidget",
        		pm.getLockWidget());
        assertFalse(stateGraphElementBrd+": stateGraph.caseInsensitive",
        		pm.isCaseInsensitive());
        assertEquals(stateGraphElementBrd+": stateGraph.studentBeginsHereState",
        		"1-3plus2-5", pm.getStudentBeginsHereState().toString());
        assertEquals(stateGraphElementBrd+": stateGraph.suppressFeedback",
        		FeedbackEnum.SHOW_ALL_FEEDBACK, pm.getSuppressStudentFeedback());
        assertFalse(stateGraphElementBrd+": stateGraph.effectiveConfirmDone",
        		pm.getEffectiveConfirmDone());
    }


    public void testStartNodeMessagesJDom() throws FactoryConfigurationError {
        readFileWithJDom(brdTrue);
        _testStartNode();

        return;
    }

    public void _testStartNodeMessagesSAX() throws
            IOException, ParserConfigurationException, ProblemModelException, SAXException,
            FactoryConfigurationError {
        // remove message objects -> property names and compare each one
        readFileWithSax(brdTrue);
        _testStartNode();
        writerTest.testWriteAndReread();
        _testStartNode();
        return;
    }

   
    public static Test suite() {
        return new TestSuite(ProblemStateReaderTest.class);
    }

    /** For restoring value to {@link Utils#setSuppressDialogs(boolean)}. */
    private static Boolean saveSuppressDialogs = null;
    
    /**
     * Create a {@link CTAT_Launcher} to set {@link #controller}.
     * On 1st call, save value of {@link Utils#getSuppressDialogs()}.
     */
    public ProblemStateReaderTest() {
		super();
        
    	
    	if(saveSuppressDialogs == null)
    		saveSuppressDialogs = Boolean.valueOf(Utils.getSuppressDialogs());
	}

	/*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        Utils.setSuppressDialogs(true);

        SocketToolProxy socketToolProxy = new SocketToolProxy(controller);
        socketToolProxy.setLogOnly(true);
        socketToolProxy.init(controller);


        //controller.setBehaviorRecorderMode(CtatModeModel.SIMULATED_STUDENT_MODE);
        controller.setModeSimStAndDestroyProdRules();
        
        // setup ProblemStateWriterTest
        writerTest = new ProblemStateWriterTestUtility(controller);

    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        Utils.setSuppressDialogs(saveSuppressDialogs);
    }

    public void _testAllFalse() {

        assertFalse("FirstCheckAllStates", controller.isFirstCheckAllStatesFlag());

        assertFalse("CaseInsensitive", controller.getProblemModel().isCaseInsensitive());

        assertFalse("Unordered", controller.getProblemModel().isUnorderedMode());

        assertFalse("LockWidget", controller.getProblemModel().isLockWidget());

        assertFalse("HighlightRightSelection", controller.getProblemModel().getHighlightRightSelection());

        /* testSetStartNodeMessageVector */
        assertTrue(controller.getProblemModel().startNodeMessagesIterator().hasNext());
    }

    private void _testAllTrue() {

        assertTrue("FirstCheckAllStates", controller.isFirstCheckAllStatesFlag());

        assertTrue("CaseInsensitive", controller.getProblemModel().isCaseInsensitive());

        assertTrue("Unordered", controller.getProblemModel().isUnorderedMode());

        assertTrue("LockWidget", controller.getProblemModel().isLockWidget());

        assertTrue("HighlightRightSelection", controller.getProblemModel().getHighlightRightSelection());

    }

    private void _testStartNode() throws FactoryConfigurationError {
        // TODO Check if there are other parameters in sNode in SAX
		Iterator<MessageObject> it = controller.getProblemModel().startNodeMessagesIterator();
        if (!it.hasNext())
            return;
        MessageObject obj = it.next();

        // Make PropertyNames Vector
        Vector comp = new Vector();
        comp.addElement("MessageType");
        comp.addElement("ProblemName");

        Vector vals = new Vector();
        vals.addElement("StartProblem");
        vals.addElement("aaa");

        Vector names = new Vector(obj.getPropertyNames());        
        Vector values = new Vector(obj.getPropertyValues());

        assertEquals(comp, names);
        assertEquals(vals, values);

    }

    private void createEdgeVector() {
        // TODO Add bounds checking

        NodeView source1 = new NodeView(controller);
        NodeView dest1 = new NodeView(controller);
        testEdgeData = new EdgeData(controller.getProblemModel());

        CheckLispLabel lisp = new CheckLispLabel(testEdgeData, controller);

        // Action Label Data
        testEdgeData.setPreferredEdge(true);
        testEdgeData.setTraversalCount(0);
        testEdgeData.setPreLispCheckLabel(lisp);
        testEdgeData.getPreLispCheckLabel().resetAll(
                testEdgeData.getUniqueID(), "SUCCESS");
        testEdgeData.setBuggyMsg("This is not correct");
        testEdgeData.setSuccessMsg("Success Message");
        testEdgeData.setActionType("Correct Action");
        testEdgeData.setCheckedStatus("SUCCESS");

        testEdgeData.addRuleName("unnamed");

        // testLabel = new ActionLabel("[1], [table1_C6R4], [UpdateTable]",
        // testData, controller);

        testLabel = new ActionLabel(testEdgeData, controller.getProblemModel());

        testEdgeData.setActionLabel(testLabel);

        ProblemNode sourcenode = new ProblemNode(source1, controller.getProblemModel());
        ProblemNode destnode = new ProblemNode(dest1, controller.getProblemModel());

        /*
         * sourcenode.setNextNode(); sourcenode.setPrevNode();
         * destnode.setNextNode(); destnode.setPrevNode();
         */

        edge1 = new ProblemEdge(sourcenode, destnode, testEdgeData);

        edgeVector.add(edge1);

    }

    /**
     * Test reading and writing of Matcher elements.
     */
    public void testMatcherJDOM()
            throws IOException, ParserConfigurationException,
            SAXException, ProblemModelException, FactoryConfigurationError {
        controller.setStudentInterface(new TutorWrapper(controller));
        readFileWithJDom(brdMatcher);
        matcherTest();
        writerTest.writeAndReread("123plus567");
        matcherTest();
    }
    
    /**
     * Test reading and writing of Matcher elements.
     */
    public void _testMatcherSAX()
            throws IOException, ParserConfigurationException,
            SAXException, ProblemModelException, FactoryConfigurationError {
        readFileWithSax(brdMatcher);
        matcherTest();
        writerTest.testWriteAndReread();
        matcherTest();
    }
    
    /**
     * Test get-accessor for ProblemModel's problemDirectory property.
     */
    public void getProblemDirectoryTest() {
//    	ProblemModel pm = controller.getProblemModel();
//    	String problemDir = pm.getProblemDirectory();
//    	assertEquals("default problemDirectory", ".", problemDir);
    	
    }
    
    /**
     * Check the instances of
     * {@link edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher}
     */
    private void matcherTest() {
      	trace.out("br", "+++++entering matcherTest()");
        Enumeration countEdges =
        	controller.getProblemModel().getProblemGraph().edges();
        int i;
        for (i = 0; countEdges.hasMoreElements(); ++i)
        	trace.outNT("br", "countEdges "+((ProblemEdge) countEdges.nextElement()).getUniqueID());
        assertEquals("countEdges not match", 6, i);
      	trace.out("br", "+++++matcherTest() countEdges "+i);
        Enumeration edges =
        	controller.getProblemModel().getProblemGraph().edges();
        for (int e = 0; edges.hasMoreElements(); ++e) {
        	ProblemEdge edge = (ProblemEdge) edges.nextElement();
        	EdgeData edgeData = edge.getEdgeData();
        	Matcher m = edgeData.getMatcher();
        	switch(e) {
        	case 5:
        		assertTrue("matcher["+e+"] is wrong type: "+m, m instanceof ExactMatcher);
        		assertEquals("replacementFormula", "simplify(input)", m.getReplacementFormula());
        		assertEquals("UpdateTable", m.getAction());
        		assertEquals("0", m.getInput());
        		assertEquals("table1_C6R4", m.getSelection());
        		break;
        	case 4:
        		assertTrue("matcher["+e+"] is wrong type: "+m, m instanceof AnyMatcher);
        		assertNull("replacementFormula", m.getReplacementFormula());
        		assertEquals("UpdateTable", m.getAction());
        		assertEquals("table1_C5R1", m.getSelection());
        		break;
        	case 3:
        		assertTrue("matcher["+e+"] is wrong type: "+m, m instanceof RangeMatcher);
        		assertNull("replacementFormula", m.getReplacementFormula());
        		assertEquals(1.0, ((RangeMatcher) m).getMinimum(), 0);
        		assertEquals(9.0, ((RangeMatcher) m).getMaximum(), 0);
        		assertEquals("table1_C5R4", m.getSelection());
        		assertEquals("UpdateTable", m.getAction());
        		break;
        	case 2:
        		assertTrue("matcher["+e+"] is wrong type: "+m, m instanceof WildcardMatcher);
        		assertNull("replacementFormula", m.getReplacementFormula());
        		assertEquals("UpdateTable", ((WildcardMatcher) m).getSimpleActionPattern());
        		assertEquals("6*3", ((WildcardMatcher) m).getSimpleInputPattern());
        		assertEquals("table1_C4R4", ((WildcardMatcher) m).getSimpleSelectionPattern());
        		break;
        	case 1:
        		assertTrue("matcher["+e+"] is wrong type: "+m, m instanceof RegexMatcher);
        		assertNull("replacementFormula", m.getReplacementFormula());
        		assertEquals("UpdateTable", ((RegexMatcher) m).getActionPattern());
        		assertEquals("[0-9]", ((RegexMatcher) m).getInputPattern());
        		assertEquals("table1_C3R4", ((RegexMatcher) m).getSelectionPattern());
        		break;
        	}
        }
    }
    
    /**
     * Test reading and writing of EdgesGroups elements.
     */
    public void testEdgeGroupsJDOM()
            throws IOException, ParserConfigurationException,
            SAXException, ProblemModelException, FactoryConfigurationError {
        controller.setStudentInterface(new TutorWrapper(controller));
        readFileWithJDom(brdMatcher);
        edgeGroupsTest();
        writerTest.writeAndReread("123plus567");
        edgeGroupsTest();
    }
    
    /**
     * Test {@link ProblemStateReader#trySaveAndTestForChange(ProblemStateWriter, ByteArrayOutputStream)}
     */
    public void testTrySaveAndTestForChange() throws IOException {
    	trace.addDebugCode("psw");
    	String problemFileLocation = "test/1416-formula.brd";
        controller.openBRFromURL(problemFileLocation);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int value = controller.getProblemStateReader().trySaveAndTestForChange(controller.getProblemStateWriter(), baos);
        assertEquals("brd file should be unchanged, but diff with ./junk.txt", JOptionPane.NO_OPTION, value);
    }
    
    /**
     * Test reading skills from preferred path.
     */
    public void testReadSkills() {
    	Map rulesIncidence = new LinkedHashMap();
    	ProblemStateReaderJDom psrj = new ProblemStateReaderJDom(controller); 
    	String errMsg = psrj.getRulesFromBRDFile(brdSkills, rulesIncidence);
    	assertEquals("unique rule count", 5, rulesIncidence.size());
    }

    /**
     * Test reading and writing of EdgesGroups elements.
     */
    public void _testEdgeGroupsSAX()
    		throws IOException, ParserConfigurationException,
            SAXException, ProblemModelException, FactoryConfigurationError {
        readFileWithSax(brdMatcher);
        edgeGroupsTest();
        writerTest.testWriteAndReread();
        edgeGroupsTest();
    }
    
    /**
     * Check the instances of {@link ProblemModel#getLinksGroups()}.
     * These are unordered groups of edges within an ordered graph.
     */
    private void edgeGroupsTest() {
    	GroupModel groupModel = controller.getExampleTracerGraph().getGroupModel();
    	LinkGroup onesTens = groupModel.getGroupByName("OnesTens");
    	assertTrue(onesTens!=null);
    	assertEquals(2, groupModel.getGroupLinkCount(onesTens));
    	for(ExampleTracerLink link : groupModel.getGroupLinks(onesTens)) {
    		assertTrue(link.getUniqueID()==1 || link.getUniqueID()==3);
    	}
    	
    	LinkGroup others = groupModel.getGroupByName("Others");
    	assertTrue(others!=null);
    	assertEquals(3, groupModel.getGroupLinkCount(others));
    	for(ExampleTracerLink link : groupModel.getGroupLinks(others)) {
    		assertTrue(link.getUniqueID()==5 || link.getUniqueID()==7
    				|| link.getUniqueID()==11);
    	}
    }

    private void EdgeTest() {

        Enumeration edges = controller.getProblemModel().getProblemGraph()
                .edges();

        int e = 0;

        e++;
        ProblemEdge edge = (ProblemEdge) edges.nextElement();

        checkActionLabel(edge);
        checkRuleLabels(edge);
        
        // check source
        ProblemNode testNodeS = edge.source;
        int sourceID = 1;
        ProblemNode dataNodeS = ProblemModel.getNodeForVertexUniqueID(sourceID,
                controller.getProblemModel().getProblemGraph());
        assertEquals(dataNodeS, testNodeS);

        // check destination
        ProblemNode testNodeD = edge.dest;
        int destID = 2;
        ProblemNode dataNodeD = ProblemModel.getNodeForVertexUniqueID(destID,
                controller.getProblemModel().getProblemGraph());
        assertEquals(dataNodeD, testNodeD);
        
        // check dialogue system info
        DialogueSystemInfo dsi = edge.getEdgeData().getDialogueSystemInfo();
        assertEquals(dialogueStudentHintRequest,dsi.getStudent_Hint_Request());
        assertEquals(dialogueStepSuccessfulCompletion,dsi.getStep_Successful_Completion());
        assertEquals(dialogueStepStudentError,dsi.getStep_Student_Error());

        return;
    }

    private void checkRuleLabels(ProblemEdge oldEdge) {
        EdgeData oldEdgeData = oldEdge.getEdgeData();
        Vector oldDataRuleLabels = oldEdgeData.getRuleLabels();
        Vector testDataRuleLabels = testEdgeData.getRuleLabels();
        Iterator it2 = testDataRuleLabels.iterator();
        for (Iterator it = oldDataRuleLabels.iterator(); it.hasNext();) {
            RuleLabel testRule = (RuleLabel) it.next();
            RuleLabel rule = (RuleLabel) it2.next();

            assertEquals(testRule.getText(), rule.getText());
            Dimension tmpDimension = testRule.getSize();
            assertEquals(tmpDimension.height, rule.getHeight());

        }

        trace.out ("old data rule labels = " + new ArrayList (oldDataRuleLabels));
        trace.out ("test data rule labels = " + new ArrayList (testDataRuleLabels));
        
        assertEquals(oldDataRuleLabels.size(), testDataRuleLabels.size());
    }

    private void checkActionLabel(ProblemEdge edge) {

        EdgeData oldData = edge.getEdgeData();

        // Preferred Path
        assertTrue(oldData.isPreferredEdge() == testEdgeData
                .isPreferredEdge());

        // Successful Completion
        assertTrue(oldData.getStep_Succesful_Completion() == testEdgeData
                .getStep_Succesful_Completion());

        // Student Error
        assertTrue(oldData.getStep_Student_Error() == testEdgeData
                .getStep_Student_Error());

        // Buggy message
        assertTrue(testEdgeData.getBuggyMsg().equals(oldData.getBuggyMsg()));

        // Success message
        assertTrue(testEdgeData.getSuccessMsg().equals(oldData.getSuccessMsg()));

        // hints
        // assertTrue

        // Action type
        assertTrue(oldData.getActionType().equals(testEdgeData.getActionType()));

        // Checked Status
        assertTrue(oldData.getCheckedStatus().equals(
                testEdgeData.getCheckedStatus()));

        // Dimension

        return;
    }

    private void InitNodeVector() {
        // Repeat
        // Build Nodes
        NodeView node1 = new NodeView("aaa", controller);
        NodeView node2 = new NodeView("bbb", controller);
        NodeView node3 = new NodeView("ccc", controller);
        NodeView node4 = new NodeView("ddd", controller);
        ProblemModel pm = controller.getProblemModel();
        ProblemNode pNode1 = new ProblemNode(node1,pm);
        ProblemNode pNode2 = new ProblemNode(node2,pm);
        ProblemNode pNode3 = new ProblemNode(node3,pm);
        ProblemNode pNode4 = new ProblemNode(node4,pm);
        

        // Node 1
        node1.setLocked(false);
        pNode1.setDoneState(true);
        // node1.setText("aaa");
        pNode1.setUniqueID(1);
        node1.setAlignmentX(233);
        node1.setAlignmentY(30);
        node1.setSize(34, 25);

        // Node 2
        node2.setLocked(true);
        pNode2.setDoneState(true);
        // node2.setText("bbb");
        pNode2.setUniqueID(2);
        node2.setAlignmentX(233);
        node2.setAlignmentY(30);
        node2.setSize(34, 26);

        // Node 3
        node3.setLocked(false);
        pNode3.setDoneState(false);
        // node3.setText("ccc");
        pNode3.setUniqueID(3);
        node3.setAlignmentX(233);
        node3.setAlignmentY(30);
        node3.setSize(34, 27);

        // Node 4
        node4.setLocked(true);
        pNode4.setDoneState(false);
        // node4.setText("ddd");
        pNode4.setUniqueID(1);
        node4.setAlignmentX(233);
        node4.setAlignmentY(30);
        node4.setSize(34, 28);

        // ProblemNode node = new ProblemNode(node1,pm);
        // Add Nodes to vector

        nodeVector.add(node4);
        nodeVector.add(node3);
        nodeVector.add(node2);
        nodeVector.add(node1);

    }

    private void NodeTest() {
        // TODO add in locked, donestate, id, dimension,

        // Setup
        ProblemGraph graph = controller.getProblemModel().getProblemGraph();
        Enumeration nodes = graph.nodes();

        // controlfgefefeefasf.getProblemModel().getCheckAllNodes();

        for (Iterator it = nodeVector.iterator(); it.hasNext();) {

            NodeView node = (NodeView) it.next();
            NodeView node2 = (NodeView) ((ProblemNode) nodes.nextElement())
                    .getNodeView();

            String text = node.getText();
            String txt = node2.getText();
            // text
            assertTrue(node.getText().equals(node2.getText()));

            // Check locked/doneState
            assertTrue(node.getLocked() == node2.getLocked());
            // assertTrue(node.getDoneState() == node2.getDoneState());
            // Check id
            // assertTrue(node.getUniqueID() == node2.getUniqueID());

            // dimension

            // x,y
            // assertTrue(node.getX() == node2.getX());
            // assertTrue(node.getY() == node2.getY());
        }

        return;
    }

    private void readFileWithSax(String filename) throws IOException,
            ParserConfigurationException, SAXException,
            FactoryConfigurationError {
        controller.getProblemStateReader().openBRDiagramFile(filename);
    }

    private void readFileWithJDom(String filename) {
        controller.getProblemStateReader().openBRDiagramFile(filename);
      	trace.out("br", "+++++return from openBRDiagramFile("+filename+")");
    }
    
    /**
     * For specialized tests.
     * @param args 1st arg is filename
     */
    public static void main(String[] args) {
    	String filename = null;
    	try {
    		filename = args[0];
    		Document doc = readSpecial(filename);
    		parseSpecial(doc.getRootElement(), 0);
    	} catch (Exception e) {
    		System.err.println(e+"\nUsage: ProblemStateReaderTest <filename>");
    		System.exit(2);
    	}
    }

    /**
     * For specialized tests.
     * @param problemFullName XML file to read
     */
    private static Document readSpecial(String problemFullName)
    		throws IOException, JDOMException {
    	trace.out("readSpecial: " + problemFullName);
    	if (problemFullName == null || problemFullName.length() <= 0)
    		throw new IllegalArgumentException("problemFullName "+problemFullName);
    	SAXBuilder builder = new SAXBuilder();
    	Document doc = builder.build(problemFullName);
    	return doc;
    }

    /**
     * For specialized tests.
     * @param elt XML element to dump
     */
    private static void parseSpecial(Element elt, int indent) {
    	System.out.println(spaces(indent) + elt.toString());
    	List attrs = elt.getAttributes();
    	for (Iterator it = attrs.iterator(); it.hasNext(); ) {
    		System.out.println(spaces(indent)+" "+((Attribute) it.next()).toString());
    	}
    	System.out.println(spaces(indent)+" "+elt.getText());
        List elts = elt.getChildren();
    	for (Iterator it = elts.iterator(); it.hasNext(); ) {
    		parseSpecial(((Element) it.next()), indent+2);
    	}
    }
    
    /**
     * For indenting.
     * @param number of spaces
     * @return String of that many spaces
     */
    private static String spaces(int indent) {
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < indent; ++i)
    		sb.append(" ");
    	return sb.toString();
    }
}
