/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.hcii.ctat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.pact.Utilities.UtilsTest;
import edu.cmu.pact.Utilities.trace;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author sewall
 *
 */
public class CTATRandomTest extends TestCase {

	public static Test suite() {
		return new TestSuite(CTATRandomTest.class);
	}
	
	/**
	 * Test method for {@link edu.cmu.hcii.ctat.CTATRandom#randomOrder(int)}.
	 */
	public void testRandomOrder() {
		Set<String> results = new HashSet<String>();  // shouldn't get same order twice at length 20
		CTATRandom random = new CTATRandom();
		for(int i = 0; i < 20; ++i) {
			int n;
			int[] modifiedOrder = random.randomOrder(n=20);
			System.out.printf("randomOrder(%2d) => %s\n", n, Arrays.toString(modifiedOrder));
			assertTrue("duplicate order "+(Arrays.toString(modifiedOrder)),
					results.add(Arrays.toString(modifiedOrder)));
		}
	}
}
