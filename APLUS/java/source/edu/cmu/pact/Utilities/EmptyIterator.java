/*
 * $Id: EmptyIterator.java 12714 2011-07-14 23:40:40Z sewall $
 */
package edu.cmu.pact.Utilities;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that returns no elements.
 */
public class EmptyIterator implements Iterator {

	/** The singleton instance. */
	private static Iterator singleton = new EmptyIterator();

	/**
	 * Disabled default constructor for singleton.
	 */
	private EmptyIterator() {}

	/**
	 * A pre-built instance, since only one iterator like this is needed.
	 */
	public static Iterator instance() {
		return singleton;
	}

	/**
	 * Always returns false.
	 *
	 * @return false
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return false;
	}

	/**
	 * Always throws {@link java.util.NoSuchElementException}.
	 *
	 * @return never returns
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		throw new NoSuchElementException("empty iterator");
	}

	/**
	 * Always throws {@link java.util.IllegalStateException}.
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new IllegalStateException("empty iterator");
	}
}
