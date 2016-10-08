package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class isIntegerTest extends TestCase {

	public static Test suite() {
        TestSuite suite = new TestSuite(isIntegerTest.class);
        return suite;
    }
	
	public final void testIsInteger() {
		Object o;
		isInteger isI = new isInteger();

		o = 4; assertTrue("hv("+o+")", isI.isInteger(o));
		o = -4; assertTrue("hv("+o+")", isI.isInteger(o));
		o = "4"; assertTrue("hv("+o+")", isI.isInteger(o));
		o = "4"; assertFalse("hv("+o+", false)", isI.isInteger(o, false));
		o = "4"; assertTrue("hv("+o+", true)", isI.isInteger(o, true));
		o = " 4"; assertTrue("hv("+o+", true)", isI.isInteger(o, true));
		o = "4 "; assertTrue("hv("+o+", true)", isI.isInteger(o, true));
		o = " -4"; assertTrue("hv("+o+", true)", isI.isInteger(o, true));
		o = "-4 "; assertTrue("hv("+o+", true)", isI.isInteger(o, true));
		o = new Integer(4); assertTrue("hv("+o+")", isI.isInteger(o));

		o = -1.0556293e-242; assertFalse("hv("+o+")", isI.isInteger(o));
		o = "-1.0556293e-242"; assertFalse("hv("+o+")", isI.isInteger(o));
		o = "-1.0556293e-242"; assertFalse("hv("+o+", true)", isI.isInteger(o, true));
		o = " -1.0556293e-242"; assertFalse("hv("+o+", true)", isI.isInteger(o, true));
		o = "-1.0556293e-242 "; assertFalse("hv("+o+", true)", isI.isInteger(o, true));
		o = "  -1.0556293e-242 "; assertFalse("hv("+o+", true)", isI.isInteger(o, true));
		o = "-no1.0556293e-242"; assertFalse("hv("+o+", true)", isI.isInteger(o, true));

		o = 1.0556293e-242; assertFalse("hv("+o+")", isI.isInteger(o));
		o = "1.0556293e-242"; assertFalse("hv("+o+")", isI.isInteger(o));
		o = "1.0556293e-242"; assertFalse("hv("+o+", true)", isI.isInteger(o, true));

		o = 02324.0556293; assertFalse("hv("+o+")", isI.isInteger(o));
		o = "02324.0556293"; assertFalse("hv("+o+")", isI.isInteger(o));
		o = "02324.0556293"; assertFalse("hv("+o+", true)", isI.isInteger(o, true));

		o = null; assertFalse("hv("+o+")", isI.isInteger(o));
		o = null; assertFalse("hv("+o+", true)", isI.isInteger(o, true));
		o = "null"; assertFalse("hv("+o+")", isI.isInteger(o));
	}

}
