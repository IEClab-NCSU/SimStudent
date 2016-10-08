package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class hasValueTest extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite(hasValueTest.class);
        return suite;
    }
		
	public final void testHasValue() {
		Object o;
		hasValue hv = new hasValue();

		assertFalse("hv()", hv.hasValue());

		assertFalse("hv(null)", hv.hasValue(null));
		
		assertTrue("hv(this, is, not, _null_)", hv.hasValue(this, "is", "not", "_null_"));
		
		assertFalse("hv(this, last arg is, null)", hv.hasValue(this, "last arg is", null));
		
		assertFalse("hv(\"(nULL)\")", hv.hasValue("(nULL)"));
		
		assertFalse("hv(\"NULL\")", hv.hasValue("NULL"));		
		
		assertFalse("hv(\" (NULL  )\")", hv.hasValue(" (NULL  )"));		
		
		assertTrue("hv(\" (NULL  \")", hv.hasValue(" (NULL  "));		
	}
}
