package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class isNumberTest extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite(isNumberTest.class);
        return suite;
    }
	
	public final void testIsNumber() {
		Object o;
		isNumber isN = new isNumber();

		o = 4; assertTrue("hv("+o+")", isN.isNumber(o));
		o = "4"; assertFalse("hv("+o+")", isN.isNumber(o));
		o = "4"; assertTrue("hv("+o+", true)", isN.isNumber(o, true));
		o = new Integer(4); assertTrue("hv("+o+")", isN.isNumber(o));

		o = -1.0556293e-242; assertTrue("hv("+o+")", isN.isNumber(o));
		o = "-1.0556293e-242"; assertFalse("hv("+o+")", isN.isNumber(o));
		o = "-1.0556293e-242"; assertTrue("hv("+o+", true)", isN.isNumber(o, true));
		o = " -1.0556293e-242"; assertTrue("hv("+o+", true)", isN.isNumber(o, true));
		o = "-1.0556293e-242 "; assertTrue("hv("+o+", true)", isN.isNumber(o, true));
		o = "  -1.0556293e-242 "; assertTrue("hv("+o+", true)", isN.isNumber(o, true));
		o = "-no1.0556293e-242"; assertFalse("hv("+o+", true)", isN.isNumber(o, true));

		o = 1.0556293e-242; assertTrue("hv("+o+")", isN.isNumber(o));
		o = "1.0556293e-242"; assertFalse("hv("+o+")", isN.isNumber(o));
		o = "1.0556293e-242"; assertTrue("hv("+o+", true)", isN.isNumber(o, true));

		o = 02324.0556293; assertTrue("hv("+o+")", isN.isNumber(o));
		o = "02324.0556293"; assertFalse("hv("+o+")", isN.isNumber(o));
		o = "02324.0556293"; assertTrue("hv("+o+", true)", isN.isNumber(o, true));

		o = null; assertFalse("hv("+o+")", isN.isNumber(o));
		o = null; assertFalse("hv("+o+", true)", isN.isNumber(o, true));
		o = "null"; assertFalse("hv("+o+")", isN.isNumber(o));
	}
}
