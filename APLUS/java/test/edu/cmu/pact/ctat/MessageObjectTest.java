/**
 * 
 */
package edu.cmu.pact.ctat;

import java.util.List;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jdom.Element;
import org.jdom.Verifier;

import edu.cmu.pact.Utilities.trace;

/**
 * Unit tests for {@link MessageObject}.
 */
public class MessageObjectTest extends TestCase {
	
	public static Test suite() {
		return new TestSuite(MessageObjectTest.class);
	}
	
	/**
	 * A test harness for XML characters.
	 * @param args will analyze last arg character-by-character
	 */
	public static void main(String[] args) {
		if (args.length < 1)
			return;
		String arg = args[args.length-1];
		for (int i = 0; i < arg.length(); ++i) {
			char c = arg.charAt(i);
			System.out.printf("isXMLCharacter('%c') = %b\n", c, Verifier.isXMLCharacter(c));
		}
	}

	public void testCreate() {
		String msgType, verb, xid, xmlChars, escXmlChars;
		MessageObject mo = MessageObject.create(msgType = MsgType.INTERFACE_FORCE_DISCONNECT);
		mo.setVerb(verb = "Disconnect");
		mo.setTransactionId(xid = "quitTransactionId");
		mo.setProperty("xmlChars", xmlChars = "\'<&>\""); escXmlChars="'&lt;&amp;&gt;\"";
		assertEquals("<message><verb>"+verb+"</verb><properties><MessageType>"+msgType+
				"</MessageType><transaction_id>"+xid+"</transaction_id>"+
				"<xmlChars>"+escXmlChars+"</xmlChars></properties></message>",
				mo.toString());
		assertEquals("msgType", msgType, mo.getMessageType());
		assertEquals("verb", verb, mo.getVerb());
		assertEquals("transaction_id", xid, mo.getTransactionId());
		assertEquals("xmlChars", xmlChars, mo.getProperty("xmlChars"));
	}
	
	public void testToXML() {
		String msgType = MsgType.INTERFACE_ACTION;
		String verb = "NotePropertySet", xid = "70DCF8C7887394634205D5";
		String selection = "firstDenConv", action = "UpdateTextArea", input = "12";
		Vector<String> sv, iv;
		String indentedMsg =
			"<message>\r\n"+
			"    <verb>"+verb+"</verb>\r\n"+
			"    <properties>\r\n"+
			"        <MessageType>"+msgType+"</MessageType>\r\n"+
			"        <transaction_id>"+xid+"</transaction_id>\r\n"+
			"        <Selection>\r\n"+
			"            <value>"+selection+"</value>\r\n"+
			"        </Selection>\r\n"+
			"        <Action>\r\n"+
			"            <value>"+action+"</value>\r\n"+
			"        </Action>\r\n"+
			"        <Input>\r\n"+
			"            <value>"+input+"</value>\r\n"+
			"        </Input>\r\n"+
			"    </properties>\r\n"+
			"</message>";
		MessageObject mo = MessageObject.create(msgType);
		mo.setVerb(verb);
		mo.setTransactionId(xid);
		mo.setSelection(selection);
		mo.setAction(action);
		mo.setInput(input);

		assertEquals("InterfaceAction toXML()", indentedMsg, mo.toXML());

		String msg = indentedMsg.replace("    ", "").replace("\r\n", "");
		assertEquals("InterfaceAction toString()", msg, mo.toString());
		
		String newSelection, newInput;
		sv = mo.getSelection();
		iv = mo.getInput();
		sv.set(0, newSelection = "new selection");
		iv.set(0, newInput = "new input");
		mo.setProperty(MessageObject.SELECTION, sv);
		assertEquals("selection[0] after change", newSelection, mo.getSelection().get(0));
		assertEquals("input[0] after change selection", input, mo.getInput().get(0));
	}
	public void testSkills() {
		String log_service_url = "http://learnlab.web.cmu.edu/log/server";
		String log_to_disk = "false";
		String dataset_level_name1 = "6.06";
		String CommShellVersion = "2.11";
		String sk1pKnown = "0.25";
		String sk2description = "Enter Given Value";
		String msg =
		    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		      "<message>"+
		      "<verb>NotePropertySet</verb>"+
		      "<properties>"+
		        "<MessageType>"+MsgType.SET_PREFERENCES+"</MessageType>"+
		        "<log_service_url>"+log_service_url+"</log_service_url>"+
		        "<log_to_remote_server>false</log_to_remote_server>"+
		        "<log_to_disk>"+log_to_disk+"</log_to_disk>"+
		        "<user_guid>pctm43</user_guid>"+
		        "<problem_name>molly</problem_name>"+
		        "<question_file>tutors/problem_sets/15/6.06/FinalBRDs/2-molly.brd</question_file>"+
		        "<class_name>Default Class</class_name>"+
		        "<school_name>Site Visitor School</school_name>"+
		        "<instructor_name>pctm43</instructor_name>"+
		        "<session_id>S825b92ecb338531184709f5795c16994</session_id>"+
		        "<source_id>FLASH_PSEUDO_TUTOR</source_id>"+
		        "<dataset_name>Mathtutor</dataset_name>"+
		        "<dataset_level_name1>"+dataset_level_name1+"</dataset_level_name1>"+
		        "<dataset_level_type1>ProblemSet</dataset_level_type1>"+
		        "<skills>"+
		          "<skill pSlip=\"0.1\" description=\"Trade Value\" label=\"Trade Value\" pKnown=\"0.25\" name=\"TradeValue\" category=\"PlaceValue\" pLearn=\"0.2\" pGuess=\"0.2\" />"+
		          "<skill pSlip=\"0.1\" description=\"No Trade\" label=\"No Trade\" pKnown=\""+sk1pKnown+"\" name=\"NoTrade\" category=\"PlaceValue\" pLearn=\"0.2\" pGuess=\"0.2\" />"+
		          "<skill pSlip=\"0.1\" description=\""+sk2description+"\" label=\"Enter Given Value\" pKnown=\"0.25\" name=\"enterGivenValue\" category=\"PlaceValue\" pLearn=\"0.2\" pGuess=\"0.2\" />"+
		        "</skills>"+
		        "<ProblemName>tutors/problem_sets/15/6.06/FinalBRDs/2-molly.brd</ProblemName>"+
		        "<Comm_Shell_Version>"+CommShellVersion+"</Comm_Shell_Version>"+
		      "</properties>"+
		    "</message>";
		MessageObject mo = MessageObject.parse(msg);
		
		assertEquals("SetPreferences message type", MsgType.SET_PREFERENCES, mo.getMessageType());
		assertEquals("SetPreferences log_to_disk", log_to_disk, mo.getProperty("log_to_disk"));
		assertEquals("SetPreferences Comm_Shell_Version", CommShellVersion, mo.getProperty("Comm_Shell_Version"));

		List<Element> skills = (List<Element>) mo.getProperty("skills");
		assertEquals("SetPreferences sk1pKnown", sk1pKnown, skills.get(1).getAttributeValue("pKnown"));
		assertEquals("SetPreferences sk2description", sk2description, skills.get(2).getAttributeValue("description"));
		
		assertEquals("toString() for 3 skills", msg, mo.toString());
	}
	
	public void testSingleSkill() {
		String problem_name = "1416-correct";
		String question_file = "TS/FractionAdditionRun/1416-correct.brd";
		String CommShellVersion = "2.11.0";
		String name = "convert-numerator";
		String category = "fraction-addition";
		String description = "convert-numerator fraction-addition";
		String label = "convert-numerator";
		String pGuess = "0.2";
		String pKnown = "0.25";
		String pLearn = "0.15";
		String pSlip = "0.3";
		String msg =
		    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		    "<message>"+
		      "<verb>NotePropertySet</verb>"+
		      "<properties>"+
		        "<MessageType>SetPreferences</MessageType>"+
		        "<log_to_remote_server>false</log_to_remote_server>"+
		        "<log_to_disk>false</log_to_disk>"+
		        "<problem_name>"+problem_name+"</problem_name>"+
		        "<question_file>"+question_file+"</question_file>"+
		        "<source_id>QA_CTAT_FLASH</source_id>"+
		        "<Skills>"+
		          "<Skill name=\"convert-numerator\" category=\"fraction-addition\" description=\"convert-numerator fraction-addition\" label=\"convert-numerator\" pGuess=\"0.2\" pKnown=\"0.25\" pLearn=\"0.15\" pSlip=\"0.3\" />"+
		        "</Skills>"+
		        "<ProblemName>TS/FractionAdditionRun/1416-correct.brd</ProblemName>"+
		        "<CommShellVersion>"+CommShellVersion+"</CommShellVersion>"+
		      "</properties>"+
		    "</message>";
		MessageObject mo = MessageObject.parse(msg);

		assertEquals("SetPreferences message type", MsgType.SET_PREFERENCES, mo.getMessageType());
		assertEquals("SetPreferences problem_name", problem_name, mo.getProperty("problem_name"));
		assertEquals("SetPreferences question_file", question_file, mo.getProperty("question_file"));
		assertEquals("SetPreferences CommShellVersion", CommShellVersion, mo.getProperty("CommShellVersion"));

		Object obj = mo.getProperty("skills");
		trace.out("msg", "single skill type "+obj.getClass().getSimpleName()+", toString:\n  "+obj);
		Element skill = (Element) obj;

		assertEquals("SetPreferences sk0 name", name, skill.getAttributeValue("name"));
		assertEquals("SetPreferences sk0 category", category, skill.getAttributeValue("category"));
		assertEquals("SetPreferences sk0 description", description, skill.getAttributeValue("description"));
		assertEquals("SetPreferences sk0 label", label, skill.getAttributeValue("label"));
		assertEquals("SetPreferences sk0 pGuess", pGuess, skill.getAttributeValue("pGuess"));
		assertEquals("SetPreferences sk0 pKnown", pKnown, skill.getAttributeValue("pKnown"));
		assertEquals("SetPreferences sk0 pLearn", pLearn, skill.getAttributeValue("pLearn"));
		assertEquals("SetPreferences sk0 pSlip", pSlip, skill.getAttributeValue("pSlip"));
		
		assertEquals("toString() for single skill", msg, mo.toString());
	}

	public void testParse() {
		String verb = "NotePropertySet", xid = "70DCF8C7887394634205D5";
		String selection = "first<b>Den</b>Conv", cdataSelection = "<![CDATA[first<b>Den</b>Conv]]>";
		String action = "UpdateTextArea";
		String input = "12'<&>\"", escInput = "12'&lt;&amp;&gt;\"";
		String msg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<message>"+
		  "<verb>"+verb+"</verb>"+
		  "<properties>"+
			"<MessageType>"+MsgType.INTERFACE_ACTION+"</MessageType>"+
			"<transaction_id>"+xid+"</transaction_id>"+
			"<Selection><value>"+cdataSelection+"</value></Selection>"+
			"<Action><value>"+action+"</value></Action>"+
			"<Input><value name=\"den\" type=\"String\">"+escInput+"</value></Input>"+
		  "</properties>"+
		"</message>";
		MessageObject mo = MessageObject.parse(msg);
		assertEquals("msgType", MsgType.INTERFACE_ACTION, mo.getMessageType());
		assertEquals("verb", verb, mo.getVerb());
		assertEquals("transaction_id", xid, mo.getTransactionId());

		Vector<String> s = mo.getSelection();
		assertEquals("selection size", 1, s.size());
		assertEquals("selection", MessageObject.s2v(selection), s);
		assertEquals("selection[0]", selection, s.get(0));

		Vector<String> a = mo.getAction();
		assertEquals("action size", 1, a.size());
		assertEquals("action", MessageObject.s2v(action), a);

		Vector<String> i = mo.getInput();
		assertEquals("input size", 1, i.size());
		assertEquals("input", MessageObject.s2v(input), i);
		
        Element pElt = mo.getPropertiesElement();
        List<Element> listElt = null;
        Element vElt = null;
        assertEquals("propertiesElement nChild", 5, pElt.getContentSize());
        assertEquals("propertiesElement nSelection", 1, (listElt = pElt.getChildren("Selection")).size());
        assertEquals("propertiesElement Selection[0]",
        		selection, listElt.get(0).getChildText(MessageObject.VALUE_TAG));
        assertEquals("propertiesElement nInput", 1, (listElt = pElt.getChildren("Input")).size());
        vElt = listElt.get(0).getChild(MessageObject.VALUE_TAG);
        assertEquals("propertiesElement Input[0]", input, vElt.getText());
        assertEquals("propertiesElement Input[0] name", "den", vElt.getAttributeValue("name"));
        assertEquals("propertiesElement Input[0] type", "String", vElt.getAttributeValue("type"));
	}
	
	public void testSetProperty() {
		int primitiveInt = 5;
        String childTag, attr1, attr1name, childText, topTag, attr2, attr2name; 
        Element childElt = new Element(childTag = "ChildElement");
        childElt.setAttribute(attr1name = "attr1", attr1 = "child attribute");
        childElt.setText(childText = "child text");
        Element topElt = new Element(topTag = "TopLevelElement");
        topElt.setAttribute(attr2name = "attr2", attr2 = "top attribute");
        topElt.addContent(childElt);
        
        MessageObject newMessage = MessageObject.create(MsgType.LISP_CHECK, "SendNoteProperty");
        newMessage.setProperty("primitiveInt", primitiveInt);
        newMessage.setProperty("primitiveIntAsIs", primitiveInt, true);
		newMessage.setProperty("someElement", topElt);
		
        assertEquals("primitiveInt", new Integer(primitiveInt), newMessage.getPropertyAsInteger("primitiveInt"));
        assertEquals("primitiveIntAsIs", primitiveInt, newMessage.getProperty("primitiveIntAsIs"));
        assertEquals("primitiveIntAsIs", new Integer(primitiveInt), newMessage.getPropertyAsInteger("primitiveIntAsIs"));
        assertSame("someElement", topElt, newMessage.getProperty("someElement"));
	}

	public void testSetTransactionId() {
		String xid = "", xid2 = null;
        MessageObject newMessage = MessageObject.create(MsgType.LISP_CHECK, "SendNoteProperty");
        newMessage.setTransactionId(xid);
        xid2 = newMessage.getTransactionId();
        assertTrue("Empty transaction id not replaced", xid2.length() > 0);
		try {
			newMessage.lockTransactionId(xid);
			fail("lockTransactionId(\"\") should have thrown exception");
		} catch (IllegalArgumentException iae) {
			assertEquals("lockTransactionId(\"\") error should leave unchanged", xid2, newMessage.getTransactionId());
		}
		newMessage.lockTransactionId(xid = MessageObject.makeTransactionId());
		assertEquals("lockTransactionId(\"\") did not set", xid, newMessage.getTransactionId());

		newMessage.setTransactionId(xid2 = MessageObject.makeTransactionId());
		assertFalse("makeTransactionId did not calculate new value", xid.equals(xid2));
		assertEquals("lockTransactionId() did not prevent change", xid, newMessage.getTransactionId());
	}
}
