/*
 * Created on Sep 30, 2005
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.DefaultGroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.StateGraphElement;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;


/**
 * @author kimkc
 * 
 * TestCase will be to read test brd files, then output graph information
 * to a temporary BRD file that will be read by BR again. Test cases used
 * in ProblemStateReaderTest are replicated to ensure operation is consistent.
 *  
 */
public class ProblemStateWriterTestUtility extends TestCase {

	private static String brdOutputFileName = "test/tmpOutput.brd";	
	
	private static CTAT_Launcher cLauncher = new CTAT_Launcher(new String[] { CTAT_Launcher.SKIP_MONITOR_ARG });

	private static BR_Controller controller = cLauncher.getFocusedController();
	
	private ProblemStateWriter writer;

	public ProblemStateWriterTestUtility() {}

	public static TestSuite suite() {
		return new TestSuite (ProblemStateWriterTestUtility.class);
	}
	
	public ProblemStateWriterTestUtility(BR_Controller controller2) {
		this.controller = controller2;
		// changed 06/13/2013 to accommodate the change to multiple-problem model capability
		//writer = new ProblemStateWriter(controller2);
		writer = new ProblemStateWriter(this.controller);
	}
	
	protected void setUp() {      
		writer = new ProblemStateWriter(this.controller);
        
//		
//		SingleSessionLauncher launcher = new SingleSessionLauncher(new String[0], false);
//		controller = launcher.getController();
	}

	/*	 * Test for {@link ProblemStateWriter#saveBRDFile(String)}.
	 * Write the graph to a temp file and read it back. 
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws FactoryConfigurationError
	 */
	public void testWriteAndReread() throws IOException,
			ParserConfigurationException, SAXException, ProblemModelException,
			FactoryConfigurationError {
		writeAndReread(null);
	}

	/*	 * Test for {@link ProblemStateWriter#saveBRDFile(String)}.
	 * Write the graph to a temp file and read it back. 
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws FactoryConfigurationError
	 */
	public void writeAndReread(String problemName) throws IOException,
			ParserConfigurationException, SAXException, ProblemModelException,
			FactoryConfigurationError {
		if (problemName == null) {
			problemName = "tmp";
			controller.createStartState(problemName);
		}
		else 
		{
			controller.getProblemModel().renameProblem(problemName);
		}
		
		String tmpFile = writeToFile();
		openFile(tmpFile);
		ProblemModel pm = controller.getProblemModel();
		assertNotNull("controller.getProblemModel()", pm);
		ProblemNode startNode = pm.getStartNode();
		assertNotNull("pm.getStartNode()", startNode);
		assertEquals("pm.getStartNode.get", problemName, startNode.getName());
	}


	private String writeToFile() {
      	trace.out("br", "+++++entering saveBRDFile("+brdOutputFileName+")");
		return writer.saveBRDFile(brdOutputFileName);
	}
	

	private void openFile(String filename) throws IOException,
			ParserConfigurationException, SAXException,
			FactoryConfigurationError {
      	trace.out("br", "+++++entering openBRDiagramFile("+filename+")");
		controller.getProblemStateReader().openBRDiagramFile(filename);
      	trace.out("br", "+++++return from openBRDiagramFile("+filename+")");
		// Change to JDOM when JDOM reader is released
		//controller.getProblemStateReader().openBRDiagramFileJDom(filename);
	}
	
	/** For {@link #testWriteStartNodeMessages()}. */
	private static String[] startNodeMessages = {
			"        <message>\n"+
			"            <verb>NotePropertySet</verb>\n"+
			"            <properties>\n"+
			"                <MessageType>InterfaceAction</MessageType>\n"+
			"                <transaction_id>3D58ACC4F9A0</transaction_id>\n"+
			"                <Selection>\n"+
			"                    <value>firstNumGiven</value>\n"+
			"                </Selection>\n"+
			"                <Action>\n"+
			"                    <value>UpdateTextArea</value>\n"+
			"                </Action>\n"+
			"                <Input>\n"+
			"                    <value>3</value>\n"+
			"                </Input>\n"+
			"            </properties>\n"+
			"        </message>",
			"        <message>\n"+
			"            <verb>NotePropertySet</verb>\n"+
			"            <properties>\n"+
			"                <MessageType>InterfaceAction</MessageType>\n"+
			"                <transaction_id>AB092059EDC3</transaction_id>\n"+
			"                <Selection>\n"+
			"                    <value>firstDenGiven</value>\n"+
			"                </Selection>\n"+
			"                <Action>\n"+
			"                    <value>UpdateTextArea</value>\n"+
			"                </Action>\n"+
			"                <Input>\n"+
			"                    <value><![CDATA[10]]></value>\n"+
			"                </Input>\n"+
			"            </properties>\n"+
			"        </message>"	
	};
	
	public void testWriteStartNodeMessages() throws SAXException {
		for(int i = 0; i < startNodeMessages.length; ++i) {
			String s = startNodeMessages[i];
			
			Writer sw = new StringWriter();
			DataWriter dw = new DataWriter(sw);
			StateGraphElement xmlstategraph = new StateGraphElement();
			xmlstategraph.addGroupModel(new DefaultGroupModel());

			List<MessageObject> snms = new ArrayList<MessageObject>();
			snms.add(MessageObject.parse(s));
			writer.writeStartNodeMessages(dw, xmlstategraph, snms.iterator());
			writer.writeDocumentToFile(dw, xmlstategraph);
			
			s =
					"<?xml version=\"1.0\" standalone=\"yes\"?>\n"+
					"\n"+
					"<stateGraph>\n"+
					"    <startNodeMessages>\n"+
					s+"\n"+
					"    </startNodeMessages>\n"+
					"    <EdgesGroups ordered=\"true\"></EdgesGroups>\n"+
					"</stateGraph>\n\n";
			
			s = s.replaceAll("<!\\[CDATA\\[([^]]*)\\]\\]>", "$1");

			assertEquals("mismatch on message["+i+"]", s, sw.toString());
		}
	}
}
