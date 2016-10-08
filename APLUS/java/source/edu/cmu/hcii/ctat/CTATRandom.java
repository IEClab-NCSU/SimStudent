/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.hcii.ctat;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.cmu.pact.Utilities.trace;

/**
 * Simple random number support. 
 */
public class CTATRandom {

	/** Random number generator for {@link #randomOrder(int)}. */
	private static Random gen = new Random(System.currentTimeMillis());

	/**
	 * @param newSeed new seed for random number generator {@link #gen}.
	 */
	public void setSeed(long newSeed) {
		synchronized(gen) {
			gen.setSeed(newSeed);
		}
	}

	/**
	 * Generate indices 0, 1, ..., n-1 in random order.
	 * @param n number of indices to generate
	 * @return array of length n of randomized indices
	 */
	public int[] randomOrder(int n) {
		int[] result = new int[n];
		List<Integer> indices = new LinkedList<Integer>();  // LinkedList for O(1) insert, remove
		for(int i = 0; i < n; ++i)
			indices.add(new Integer(i));
		synchronized(gen) {
			for(int i = 0; n > 0; ++i, --n) {
				double r = gen.nextDouble();
				result[i] = indices.remove((int) (r*n));
				if(trace.getDebugCode("random"))
					trace.out("random", "r "+r+", (int) (r*n) "+((int) (r*n))+", result["+i+"]="+result[i]);
			}
		}
		return result;
	}

	/**
	 * Choose <i>fraction</i> of the indices 0, 1, ..., n-1 randomly.
	 * @param n number of indices to choose from
	 * @param fraction what portion of the indices to return
	 * @return array of length <i>n*fraction</i> with random values from 0, 1, ..., n-1;
	 *         empty array (not null) if <i>n*fraction</i> < 1
	 */
	public int[] randomIndices(int n, double fraction) {
		int[] randIndices = randomOrder(n);
		int resultLength = (int) Math.floor(n*fraction);
		if(resultLength < 1)
			return new int[0];
		return Arrays.copyOf(randIndices, resultLength);
	}
}
