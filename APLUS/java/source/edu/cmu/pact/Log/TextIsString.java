/*
 * Carnegie Mellon Univerity, Human Computer Interaction Institute
 * Copyright 2005
 * All Rights Reserved
 */
package edu.cmu.pact.Log;

/**
 * Marker interface to extract text from more elaborate elements.
 *
 * @author Alida Skogsholm
 * @version $Revision: 5015 $
 * <BR>Last modified by: $Author: zzhang $
 * <BR>Last modified on: $Date: 2005-09-27 10:36:37 -0400 (Tue, 27 Sep 2005) $
 * <!-- $KeyWordsOff: $ -->
 */
public interface TextIsString {
    /**
     * Ensure that a toString method is present.
     * see {@link java.lang.Object#toString()}
     */
    String toString();
}
