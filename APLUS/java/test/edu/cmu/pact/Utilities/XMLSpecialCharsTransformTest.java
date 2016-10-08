/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.Utilities;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author sewall
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class XMLSpecialCharsTransformTest extends TestCase {

	public final void testTransformSpecialChars() {
		String s, r;
		r = XMLSpecialCharsTransform.transformSpecialChars(s = "string should be unchanged");
		assertEquals(s, r);
		r = XMLSpecialCharsTransform.transformSpecialChars(s = "string should be <changed");
		assertEquals("string should be &lt;changed", r);
		r = XMLSpecialCharsTransform.transformSpecialChars(s = "ampersand&&ampersand");
		assertEquals("ampersand&amp;&amp;ampersand", r);
		r = XMLSpecialCharsTransform.transformSpecialChars(s = "<a href=\"url\">link</a>");
		assertEquals("&lt;a href=&quot;url&quot;&gt;link&lt;/a&gt;", r);
	}

	public final void testTransformBackSpecialChars() {
		String s, r;
		r = XMLSpecialCharsTransform.transformBackSpecialChars(s = "string should be unchanged");
		assertEquals(s, r);
		r = XMLSpecialCharsTransform.transformBackSpecialChars(s = "string should be &lt;changed");
		assertEquals("string should be <changed", r);
		r = XMLSpecialCharsTransform.transformBackSpecialChars(s = "ampersand&amp;&amp;ampersand");
		assertEquals("ampersand&&ampersand", r);
		r = XMLSpecialCharsTransform.transformBackSpecialChars(s = "&lt;a href=\"url\"&gt;link&lt;/a&gt;");
		assertEquals("<a href=\"url\">link</a>", r);
	}


    public static Test suite() {
        // Any void method that starts with "test" 
        // will be run automatically using this construct
        return new TestSuite(XMLSpecialCharsTransformTest.class);
    }

}
