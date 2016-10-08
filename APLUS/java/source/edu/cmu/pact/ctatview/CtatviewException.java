/***********************************
 * CtatviewException.java
 * @author Collin Lynch
 * @date 6/28/2009
 * @copyright 2009 CTAT project.
 */
package edu.cmu.pact.ctatview;

import edu.cmu.pact.PactException;

/**
 * This class implements a Dock Manager specific 
 * exception class for use in subclasses.
 */
public class CtatviewException extends PactException {
    
    public CtatviewException() { super(); }
    public CtatviewException(String M) { super(M); }
}