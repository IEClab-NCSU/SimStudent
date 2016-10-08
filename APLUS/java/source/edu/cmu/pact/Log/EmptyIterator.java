
/*
 * Carnegie Mellon Univerity, Human Computer Interaction Institute
 * Copyright 2005
 * All Rights Reserved
 */
package edu.cmu.pact.Log;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Empty Iterator is useful so something can be returned.
 *
 * @author Alida Skogsholm
 * @version $Revision: 5015 $
 * <BR>Last modified by: $Author: zzhang $
 * <BR>Last modified on: $Date: 2005-09-27 10:36:37 -0400 (Tue, 27 Sep 2005) $
 * <!-- $KeyWordsOff: $ -->
 */
public final class EmptyIterator implements Iterator {

    /** This is a singleton. */
    private static EmptyIterator instance = new EmptyIterator();

    /**
     * Get the one and only instance of this class.
     * @return instance of this class
     */
    public static EmptyIterator getInstance() { return instance; }

    /**
     * Disabled default constructor for singleton.
     */
    private EmptyIterator() {
        //do nothing
    }

    /** Always returns false.
        @return constant false */
    public boolean hasNext() {
        return false;
    }

    /** Always throws {@link java.util.NoSuchElementException}.
        @return never returns */
    public Object next() {
        throw new NoSuchElementException(getClass().getName());
    }

    /** Always throws {@link java.lang.IllegalStateException}. */
    public void remove() {
        throw new IllegalStateException(getClass().getName());
    }
} // end class EmptyIterator

