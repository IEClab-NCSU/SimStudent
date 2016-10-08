/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author sewall
 *
 */
public class firstNonNullTest extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite(firstNonNullTest.class);
        return suite;
    }

	/**
	 * Test method for {@link edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.firstNonNull#firstNonNull(java.lang.Object[])}.
	 */
	public void testFirstNonNull() {
		firstNonNull t = new firstNonNull();
		String s = null;
		
		assertNull("1 null arg", t.firstNonNull(s));
		assertNull("2 null arg", t.firstNonNull(s, s));
		s = "test arg 1"; assertEquals("non null arg 1", s, t.firstNonNull(s, null));
		s = "test arg 2"; assertEquals("non null arg 2", s, t.firstNonNull(null, s));
		s = "3";  assertEquals("integer arg 3", s, t.firstNonNull(null, null, 3));
		assertEquals("pi arg 2", Double.toString(Math.PI), t.firstNonNull(null, Math.PI, "junk"));
	}

}
