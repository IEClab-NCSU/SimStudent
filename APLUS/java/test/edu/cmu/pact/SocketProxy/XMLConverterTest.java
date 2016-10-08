/*
 * Created on Apr 30, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package edu.cmu.pact.SocketProxy;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.pact.Utilities.trace;

/**
 * @author mpschnei
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMLConverterTest extends TestCase {

	static final String TRANSACTION_ID_TAG = "_%_TRANSACTION_ID_%_";
	static final String MESSAGENUMBER = "&MESSAGENUMBER=I:";
	static final String COMM_MESSAGE_1 =
		"SE/1.2&VERB=S:15:NotePropertySet&PROPERTYNAMES=L:2:[S:11:MessageType,S:11:ProblemName]&PROPERTYVALUES=L:2:[S:12:StartProblem,S:11:assistment2]&MESSAGENUMBER=I:1:1&";
	static final String XML_MESSAGE_1 =
		"<message><verb>NotePropertySet</verb><properties><MessageType>StartProblem</MessageType><ProblemName>assistment2</ProblemName><transaction_id>"+
		TRANSACTION_ID_TAG+"</transaction_id></properties></message>";
	static final String PROPERTY_NAME_1 = "MessageType";
	static final String PROPERTY_NAME_2 = "Selection";
	static final String PROPERTY_VALUE_1 = "InterfaceAction";
	static final String[] PROPERTY_VALUE_2 = { "resultPost" };
	static final String THE_TRANSACTION_ID = "TheTransactionId";
	static final String XML_MESSAGE_1_WITH_WHITESPACE =
		"<message><verb>NotePropertySet</verb>\n<properties><MessageType>StartProblem</MessageType>\n<ProblemName>assistment2</ProblemName></properties></message>";

	static final String XML_MESSAGE_2 =
		"<message><verb>NotePropertySet</verb><properties><MessageType>StartProblem</MessageType><ProblemName>My Start Node</ProblemName></properties></message>";
	static final String XML_MESSAGE_3 = 
		"<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceAction</MessageType><Selection><value>resultPost</value></Selection><Action><value>setDenominator</value></Action><Input><value>2</value></Input><transaction_id>TheTransactionId</transaction_id></properties></message>";
	
	/** ObjectToolProxy instance needed for some {@link MessageObject} constructors. */
	UniversalToolProxy utp;

	protected void setUp() throws Exception {
		super.setUp();
        utp = new UniversalToolProxy();
		//trace.addDebugCode("mo");               // MessageObject debug code
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(XMLConverterTest.class);
		return suite;
	}

	public void testCOMMtoXML() {
        MessageObject mo = new MessageObject(COMM_MESSAGE_1, null);
		String result = XMLConverter.commToXml(mo);
		String expected = XML_MESSAGE_1.replaceAll(TRANSACTION_ID_TAG,
				mo.getTransactionId());
		assertEquals(expected, result);
	}

	public void _testXMLtoCOMM() {
		String result = XMLConverter.xmlToComm(XML_MESSAGE_1);
		String expected = COMM_MESSAGE_1;

		assertEquals(expected, result);
	}


	public void testXMLwithWhitespaceToCOMM() {
	
		String result = XMLConverter.xmlToComm(XML_MESSAGE_1_WITH_WHITESPACE);
		int msgNumOffset = COMM_MESSAGE_1.indexOf(MESSAGENUMBER);
		String expected = COMM_MESSAGE_1.substring(0, msgNumOffset);
		trace.out("mo", "testXMLwithWhitespaceToCOMM: expected\n" + expected +
				  "\n  result\n" + result);
	
		assertEquals(expected,
					 result.substring(0, result.indexOf(MESSAGENUMBER)));
	}
	
	
	public void testXMLtoCOMM2() {
		String commString = XMLConverter.xmlToComm(XML_MESSAGE_1_WITH_WHITESPACE);

		MessageObject mo = new MessageObject(commString, null);
		Vector propertyNames = null;
		Vector propertyValues = null;
		try {
			propertyNames = mo.extractListValue("PROPERTYNAMES");
			propertyValues = mo.extractListValue("PROPERTYVALUES");
		} catch (DorminException e) {
			trace.out("mo", "comm exception: " + e);
			fail("error retrieving "+
					(propertyNames == null ? "PROPERTYNAMES" : "PROPERTYVALUES"));
		}
		assertEquals(mo.toString(), commString);
	}

	public void testXMLtoCOMM3() {
		MessageObject mo = XMLConverter.xmlToCommObject(XML_MESSAGE_3);

		Vector propertyNames = null;
		Vector propertyValues = null;
		try {
			propertyNames = mo.extractListValue("PROPERTYNAMES");
			propertyValues = mo.extractListValue("PROPERTYVALUES");
		} catch (DorminException e) {
			fail("error getting PROPERTYNAMES or VALUES: "+e);
		}

		assertEquals(PROPERTY_NAME_1, (String) propertyNames.elementAt(0));
		assertEquals(PROPERTY_NAME_2, (String) propertyNames.elementAt(1));
		assertEquals(PROPERTY_VALUE_1, (String) propertyValues.elementAt(0));
		assertEquals(Arrays.asList(PROPERTY_VALUE_2), (Vector) propertyValues.elementAt(1));
		assertEquals("transaction id", THE_TRANSACTION_ID, mo.getTransactionId());
	}

	public void testXmlObjectToCommObject() {
		List<String> lcLtrs = Arrays.asList(new String[] { "a", "b", "c" });
		List<String> ucLtrs = Arrays.asList(new String[] { "A", "B", "C" });
		List<String> digits = Arrays.asList(new String[] { "1", "2", "3" });
		Vector lud = new Vector(Arrays.asList(new List[] { lcLtrs, ucLtrs, digits }));
		Vector ud = new Vector(Arrays.asList(new List[] { ucLtrs, digits }));
		lud.add(0, lud.size());  // first element now Integer
		ud.add(ud.size());       // last element now Integer
		
		String msgType = "VectorsOfLists", verb = "NotePropertySet", xid="someXid";
		int nProperties = 4;
		edu.cmu.pact.ctat.MessageObject xmo = edu.cmu.pact.ctat.MessageObject.create(msgType, verb);
		xmo.setProperty("lud", lud);
		xmo.setProperty("ud", ud, true);  // true => use value without copying
		xmo.setTransactionId(xid);
		xmo.setProperty("nProperties", nProperties, true);
		
		MessageObject mo = XMLConverter.xmlObjectToCommObject(xmo);

		Vector propertyNames = null;
		Vector propertyValues = null;
		try {
			propertyNames = mo.extractListValue("PROPERTYNAMES");
			propertyValues = mo.extractListValue("PROPERTYVALUES");
		} catch (DorminException e) {
			fail("error getting PROPERTYNAMES or VALUES: "+e);
		}
		int i = 0, j = 0;
		for (; i < propertyNames.size(); ++i) {
			String name = (String) propertyNames.get(i);
			if ("MessageType".equals(name)) {
				assertEquals("msgType", msgType, propertyValues.get(i));
				++j;
			} else if ("lud".equals(name)) {
				assertEquals("lud", lud, propertyValues.get(i));
				assertNotSame("lud", lud, propertyValues.get(i));
				++j;
			} else if ("ud".equals(name)) {
				assertEquals("ud", ud, propertyValues.get(i));
				assertSame("ud", ud, propertyValues.get(i));
				++j;
			} else if ("transaction_id".equals(name)) {
				assertEquals("xid", xid, propertyValues.get(i));
				++j;
			} else if ("nProperties".equals(name)) {
				Object obj = propertyValues.get(i);
				assertEquals("nProperties", nProperties, obj);
				assertTrue("nProperties instanceof Integer", obj instanceof Integer);
				++j;
			}
		}
		assertEquals("not all properties found", i, j);
	}
}
