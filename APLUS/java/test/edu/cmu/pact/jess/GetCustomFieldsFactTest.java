package edu.cmu.pact.jess;

import jess.Fact;
import jess.Rete;
import jess.ValueVector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.Utilities.TextOutput;

public class GetCustomFieldsFactTest extends TestCase {

	private static final String deftemplateScript =
			"(deftemplate "+GetCustomFieldsFact.CUSTOM_FIELDS+
			"  (slot name)"+
			"  (slot street)"+
			"  (multislot phone-nos))";
	private static final String addFirstScript =
			"(assert ("+GetCustomFieldsFact.CUSTOM_FIELDS+
			"  (name Fred)"+
			"  (street \"Forbes Ave\")"+
			"  (phone-nos \"800-555-1212\" \"900-SUM-SCAM\")))";
	private static final String toXMLOutput =
			  "<custom_field>"+
				"<name>name</name>"+
				"<value>Fred</value>"+
			  "</custom_field>"+
			  "<custom_field>"+
			    "<name>street</name>"+
			    "<value>Forbes Ave</value>"+
			  "</custom_field>"+
			  "<custom_field>"+
			    "<name>phone-nos</name>"+
			    "<value>800-555-1212;900-SUM-SCAM</value>"+
			  "</custom_field>";
	private Rete rete = null;
	
	public static Test suite() {
        return new TestSuite(GetCustomFieldsFactTest.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		rete = new Rete();
	}

	/**
	 * Test method for {@link GetCustomFieldsFact#get(Rete)}.
	 */
	public void testGet() throws Exception {
		assertNull("get() result non-null before template defined", GetCustomFieldsFact.get(rete));
		rete.eval(deftemplateScript);
		assertNull("get() result non-null before fact asserted", GetCustomFieldsFact.get(rete));
		rete.eval(addFirstScript);
		Fact f = GetCustomFieldsFact.get(rete);
		assertNotNull("get() result null after 1st fact asserted");
		
		String s;
		assertEquals("Slot value mismatch on "+(s="name"), "Fred", f.getSlotValue(s).stringValue(rete.getGlobalContext()));
		assertEquals("Slot value mismatch on "+(s="street"), "Forbes Ave", f.getSlotValue(s).stringValue(rete.getGlobalContext()));
		ValueVector vv = f.getSlotValue("phone-nos").listValue(rete.getGlobalContext());
		assertEquals("Multislot size wrong", 2, vv.size());
		assertEquals("Multislot element[0] wrong", "800-555-1212", vv.get(0).stringValue(rete.getGlobalContext()));
		assertEquals("Multislot element[1] wrong", "900-SUM-SCAM", vv.get(1).stringValue(rete.getGlobalContext()));
	}

	/**
	 * Test method for {@link GetCustomFieldsFact#get(Rete)}.
	 */
	public void testToXML() throws Exception {
		rete.eval(deftemplateScript);
		rete.eval(addFirstScript);
		Fact f = GetCustomFieldsFact.get(rete);
		assertEquals("XML output", toXMLOutput, GetCustomFieldsFact.toXML(f, rete.getGlobalContext()));
	}

	
}
