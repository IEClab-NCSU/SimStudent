package edu.cmu.pact.Log;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.Log.AuthorActionLog.MsgProperty;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.view.AbstractCtatWindow;
import edu.cmu.pact.jess.JessConsole;

public class AuthorActionLogTest extends TestCase {

	public AuthorActionLogTest() {
		super();
	}

	public AuthorActionLogTest(String arg0) {
		super(arg0);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(AuthorActionLogTest.class);
		return suite;
	}

	public void testConstructors()
	{
		AuthorActionLog aal = new AuthorActionLog("this is the action type",
				"this is the argument", "this is the result",
				"this is the result2");
		assertEquals("this is the action type", aal.getActionType());
		assertEquals("this is the argument", aal.getArgument());
		assertEquals("this is the result", aal.getResult());
		assertEquals("this is the result2", aal.getResultDetails());
		
		aal = new AuthorActionLog("at", "arg");
		assertEquals("at", aal.getActionType());
		assertEquals("arg", aal.getArgument());
		assertEquals("", aal.getResult());
		assertNull(aal.getResultDetails());
		
		aal = new AuthorActionLog("res", new Integer(1));
		assertEquals("", aal.getActionType());
		assertEquals("", aal.getArgument());
		assertEquals("res", aal.getResult());
		assertEquals(new Integer(1), aal.getResultDetails());
		
		
	}

	public void testTopElementTypes() {
		AuthorActionLog aal = new AuthorActionLog(
				AuthorActionLog.CURRICULUM_MSG_ELEMENT);
		assertEquals(aal.getTopElementType(),
				AuthorActionLog.CURRICULUM_MSG_ELEMENT);
		aal.setTopElementType(AuthorActionLog.MSG_ELEMENT);
		assertEquals(aal.getTopElementType(), AuthorActionLog.MSG_ELEMENT);

		try {
			aal = new AuthorActionLog("bad element type");
			fail();
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testAddChildElements() {
		AuthorActionLog aal = new AuthorActionLog("this is the action type",
				"this is the argument", "this is the result",
				"this is the result2");
		Element root = new Element("rootelement");

		aal.addChildElements(root);

		assertEquals(root.getChildren().size(), 4);

		String child = root.getChildText(AuthorActionLog.ACTION_TYPE_ELEMENT);
		assertEquals(child, "this is the action type");
		child = root.getChildText(AuthorActionLog.ARGUMENT_ELEMENT);
		assertEquals(child, "this is the argument");
		child = root.getChildText(AuthorActionLog.RESULT_ELEMENT);
		assertEquals(child, "this is the result");
		child = root.getChildText(AuthorActionLog.RESULT_DETAILS_ELEMENT);
		assertEquals(child, "this is the result2");
	}

	public void testConstructorFromElement() {
		Element e = new Element(AuthorActionLog.MSG_ELEMENT);
		e.addContent(AuthorActionLog.stringToElement(
				AuthorActionLog.ACTION_TYPE_ELEMENT, "at"));
		e.addContent(AuthorActionLog.stringToElement(
				AuthorActionLog.ARGUMENT_ELEMENT, "a"));
		e.addContent(AuthorActionLog.stringToElement(
				AuthorActionLog.RESULT_ELEMENT, "r"));
		e.addContent(AuthorActionLog.stringToElement(
				AuthorActionLog.RESULT_DETAILS_ELEMENT, "r2"));

		AuthorActionLog aal = new AuthorActionLog(e);
		aal.parseActionMsgElement(e);

		assertEquals("at", aal.getActionType());
		assertEquals("a", aal.getArgument());
		assertEquals("r", aal.getResult());
		assertEquals("r2", aal.getResultDetails());
	}

	public void testGetElement() {
		AuthorActionLog aal = new AuthorActionLog("at", "arg", "res", "res2");
		aal.setSchoolName("school1");
		aal.setTopElementType(AuthorActionLog.CURRICULUM_MSG_ELEMENT);

		Element e = aal.getElement();
		assertEquals("school1", e
				.getChildText(AuthorActionLog.SCHOOLNAME_ELEMENT));
		assertNull(e.getChildText(AuthorActionLog.COURSENAME_ELEMENT));

		aal.setTopElementType(AuthorActionLog.MSG_ELEMENT);
		aal.addMsgProperty("propertyname1", "propertyvalue1");
		aal.addMsgProperty("propertyname2", "propertyvalue2");

		e = aal.getElement();
		List kids = e.getChildren();
		for (int i = 0; i < kids.size(); i++) {
			// test that there are exactly 2 properties
			if (((Element) kids.get(i)).getAttributeValue("name").equals(
					"propertyname1"))
				assertEquals("propertyvalue1", ((Element) kids.get(i))
						.getValue());
			else if (((Element) kids.get(i)).getAttributeValue("name").equals(
					"propertyname2"))
				assertEquals("propertyvalue2", ((Element) kids.get(i))
						.getValue());
			else
				fail();
		}
	}

	public void testParseElement() throws JDOMException {
		Element e = new Element(AuthorActionLog.CURRICULUM_MSG_ELEMENT);
		e.setAttribute("attempt_id", "1");
		e.addContent(AuthorActionLog.stringToElement(
				AuthorActionLog.SCHOOLNAME_ELEMENT, "school1"));
		e.addContent(AuthorActionLog.stringToElement(
				AuthorActionLog.COURSENAME_ELEMENT, "math"));
		e.addContent(AuthorActionLog.stringToElement(
				AuthorActionLog.UNITNAME_ELEMENT, "unit1"));
		e.addContent(AuthorActionLog.stringToElement(
				AuthorActionLog.SECTIONNAME_ELEMENT, "addition"));
		e.addContent(AuthorActionLog.stringToElement(
				AuthorActionLog.PROBLEMNAME_ELEMENT, "5+4"));

		e.addContent(AuthorActionLog.stringToElement(
				AuthorActionLog.ACTION_TYPE_ELEMENT, "actiontype1"));
		e.addContent(AuthorActionLog.stringToElement(
				AuthorActionLog.ARGUMENT_ELEMENT, "argument1"));
		e.addContent(AuthorActionLog.stringToElement(
				AuthorActionLog.RESULT_ELEMENT, "result1"));
		// e.addContent(AuthorActionLog.stringToElement(AuthorActionLog.RESULT2_ELEMENT,
		// "result2"));

		AuthorActionLog aal = new AuthorActionLog();
		aal.parseElement(e);

		assertEquals(AuthorActionLog.CURRICULUM_MSG_ELEMENT, aal
				.getTopElementType());
		assertEquals("1", aal.getAttemptId());
		assertEquals("school1", aal.getSchoolName());
		assertEquals("math", aal.getCourseName());
		assertEquals("unit1", aal.getUnitName());
		assertEquals("addition", aal.getSectionName());
		assertEquals("5+4", aal.getProblemName());

		assertEquals("actiontype1", aal.getActionType());
		assertEquals("argument1", aal.getArgument());
		assertEquals("result1", aal.getResult());
		assertNull(aal.getResultDetails());

		e = new Element(AuthorActionLog.MSG_ELEMENT);
		e.addContent(AuthorActionLog.stringToElement(
				AuthorActionLog.PROPERTY_ELEMENT, "val1"));
		e.getChild(AuthorActionLog.PROPERTY_ELEMENT).setAttribute("name",
				"name1");

		aal.parseElement(e);

		Iterator it = aal.msgPropertiesIterator();
		MsgProperty mp = (MsgProperty) (it.next());
		assertEquals("name1", mp.getName());
		assertEquals("val1", mp.getStringValue());
		try {
			it.next();
			fail("There should be only one property");
		} catch (NoSuchElementException ex) {
		}
	}

	public void testAddCustomField() {
		AuthorActionLog aal = new AuthorActionLog("at", "a", "r", "r2");
		aal.addCustomField("cf1name", "cf1val");
		aal.addCustomField("cf2name", "cf2val");
		aal.addCustomField("cf3name", "cf3val");
		Iterator cfIter = aal.customFieldsIterator();
		assertEquals("cf1name", cfIter.next());
		assertEquals("cf2name", cfIter.next());
		assertEquals("cf3name", cfIter.next());
	}

	public void testAddMessageProperty() {
		AuthorActionLog aal = new AuthorActionLog("at", "a", "r", "r2");
		aal.addMsgProperty("mp1name", "mp1value");
		ArrayList al = new ArrayList();
		aal.addMsgProperty("mp2name", al);
		Iterator iter = aal.msgPropertiesIterator();

		MsgProperty mp = (MsgProperty) iter.next();
		assertEquals("mp1name", mp.getName());
		assertFalse(mp.isList());
		assertEquals("mp1value", mp.getStringValue());
		Element e = new Element(AuthorActionLog.MsgProperty.ELEMENT);
		e.setAttribute("name", "mp1name");
		e.setText("mp1value");
		// System.out.println(e.getAttribute("name").getParent());
		// System.out.println(mp.getElement().getAttribute("name").getParent());
		assertEquals(e.toString(), mp.getElement().toString());
		// for some reason, the two elements weren't comparing as equal, even
		// though they were. So a toString comparison is the closest accurate
		// comparison I can get.
		// Note that it doesn't actually ensure that the two elements are the
		// same.

		MsgProperty mp2 = (MsgProperty) iter.next();
		assertEquals("mp2name", mp2.getName());
		assertTrue(mp2.isList());
		assertEquals(al, mp2.getList());
	}

	public void testCheckElementName() throws JDOMException {
		Element e = new Element(AuthorActionLog.MSG_ELEMENT);
		AuthorActionLog.checkElementName(e, AuthorActionLog.MSG_ELEMENT);
		try {
			AuthorActionLog.checkElementName(e, "wrong name");
			fail();
		} catch (JDOMException j) {
		}
	}

	public void testGetElementID() throws JDOMException {
		Element e = new Element(AuthorActionLog.MSG_ELEMENT);
		e.setAttribute("id", "123");
		AuthorActionLog.getElementId(e, "123");

		e = null;
		try {
			AuthorActionLog.getElementId(e, "id");
			fail();
		} catch (JDOMException j) {
		}

		e = new Element(AuthorActionLog.MSG_ELEMENT);
		try {
			AuthorActionLog.getElementId(e, "id");
			fail();
		} catch (JDOMException j) {
		}
	}

	public void testDiskLogging() throws IOException, JDOMException {
		BR_Controller brc = new CTAT_Launcher(new String[0]).getFocusedController();
		brc.getProperties().setProperty(LoggingSupport.ENABLE_AUTHOR_LOGGING, Boolean.TRUE.toString());
		brc.getProperties().setProperty(BR_Controller.USE_DISK_LOGGING, Boolean.TRUE.toString());

		LoggingSupport ls = brc.getLoggingSupport();
		ls.authorActionLog(AuthorActionLog.BEHAVIOR_RECORDER,
				BR_Controller.GO_TO_START_STATE, "argument", "result",
				"result2");
		ls.authorActionLog(AuthorActionLog.CTAT_WINDOW,
				AbstractCtatWindow.FOCUS_GAINED, "argument-2", "result-2",
				"result2-2");
		ls.authorActionLog(AuthorActionLog.JESS_CONSOLE, JessConsole.CLEAR,
				"argument-3", "result-3", "result2-3");

		File f = new File(ls.lastLogFile);

		LogFormatUtils.makeValidXML(f);

		SAXBuilder saxb = new SAXBuilder();
		Document d = saxb.build(f);
		
		List kids = d.getRootElement().getChildren();
		String sessionID = ((Element) (kids.get(0)))
				.getAttributeValue(Logger.SESSION_ID_PROPERTY);
		String userName = ((Element) (kids.get(0)))
				.getAttributeValue(Logger.STUDENT_NAME_PROPERTY);
		for (int j = 0; j < kids.size(); j++) {
			Element e = (Element) kids.get(j);
			assertEquals(sessionID, e.getAttributeValue(Logger.SESSION_ID_PROPERTY));
			switch (j) {
			case 0:
				trace.out("log", "expect log_session_start: "+e);
				assertEquals(userName, e.getAttributeValue(Logger.STUDENT_NAME_PROPERTY));
				assertEquals(e.getName(), "log_session_start");
				break;
			default:
				assertEquals("x", e.getAttributeValue(Logger.STUDENT_NAME_PROPERTY));
				assertEquals(e.getName(), "log_action");
				assertEquals("author_action_message", e.getAttributeValue("action_id"));
				String innerXml = e.getText();
				
				innerXml = LogFormatUtils.unescape(innerXml);
//				innerXml = innerXml.replaceAll("%3C", "<").replaceAll("%3F",
//						"?").replaceAll("%3D", "=").replaceAll("%22", "\"")
//						.replaceAll("%3E", ">").replaceAll("%0A", " ")
//						.replaceAll("%2F", "/").replaceAll("\\+", " ");

				
				SAXBuilder sb = new SAXBuilder();
				
				Document innerDoc = sb.build(new StringReader(innerXml));
				Element innerElem = innerDoc.getRootElement().getChild("author_action_message");
				if (innerElem == null)
					fail("author_action_message element not present");

				switch(j)
				{
				case 1:
					assertEquals(BR_Controller.GO_TO_START_STATE, innerElem.getChildText(AuthorActionLog.ACTION_TYPE_ELEMENT));
					assertEquals("argument", innerElem.getChildText(AuthorActionLog.ARGUMENT_ELEMENT));
					assertEquals("result", innerElem.getChildText(AuthorActionLog.RESULT_ELEMENT));
					assertEquals("result2", innerElem.getChildText(AuthorActionLog.RESULT_DETAILS_ELEMENT));
					break;
				case 2:
					assertEquals(AbstractCtatWindow.FOCUS_GAINED, innerElem.getChildText(AuthorActionLog.ACTION_TYPE_ELEMENT));
					assertEquals("argument-2", innerElem.getChildText(AuthorActionLog.ARGUMENT_ELEMENT));
					assertEquals("result-2", innerElem.getChildText(AuthorActionLog.RESULT_ELEMENT));
					assertEquals("result2-2", innerElem.getChildText(AuthorActionLog.RESULT_DETAILS_ELEMENT));
					break;
				case 3:
					assertEquals(JessConsole.CLEAR, innerElem.getChildText(AuthorActionLog.ACTION_TYPE_ELEMENT));
					assertEquals("argument-3", innerElem.getChildText(AuthorActionLog.ARGUMENT_ELEMENT));
					assertEquals("result-3", innerElem.getChildText(AuthorActionLog.RESULT_ELEMENT));
					assertEquals("result2-3", innerElem.getChildText(AuthorActionLog.RESULT_DETAILS_ELEMENT));
					break;
				}
			}
		}
	}
	

	 
	/** a helper function. prints out a DOM tree of the root element. */
	/*private*/ static void printTree(Element root) {
		System.out.print(root + " ");
		List l2 = root.getAttributes();
		for (int i = 0; i < l2.size(); i++)
			System.out.print(l2.get(i) + " ");
		System.out.println(root.getValue());

		List l = root.getChildren();
		for (int i = 0; i < l.size(); i++) {
			System.out.print("  ");
			printTree((Element) (l.get(i)));
		}
	}

	public static final String usageMsg = "Usage:\n"
			+ "  AuthorActionLogTest [-d] [test ...]\n"
			+ "where--\n"
			+ "  -d means turn on debugging;\n"
			+ "  test ... are individual tests to run; if none specified, runs suite().\n";

	public static void main(String args[]) {
		boolean debugOn = false;
		int i;

		for (i = 0; i < args.length && args[i].charAt(0) == '-'; i++) {
			char opt = args[i].charAt(1);
			switch (opt) {
			case 'd':
				debugOn = true;
				break;
			case 'f':
				File f = new File(args[i+1]);
				try{
					LogFormatUtils.makeValidXML(f);
					LogFormatUtils.unescapeAll(f);
				} catch(IOException e) {e.printStackTrace();}
				return;
			default:
				System.err.println("Undefined command-line option " + opt
						+ ". " + usageMsg);
				System.exit(1);
			}
		}
		if (debugOn)
			trace.addDebugCode("util");
		if (i >= args.length)
			junit.textui.TestRunner.run(AuthorActionLogTest.suite());
		else {
			for (; i < args.length; ++i) {
				junit.textui.TestRunner.run(new AuthorActionLogTest(args[i]));
			}
		}
	}


}
