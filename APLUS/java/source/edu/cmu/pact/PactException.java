/***********************************
 * PactException.java
 * @author Collin Lynch
 * @date 6/28/2009
 * @copyright 2009 CTAT project.
 */
package edu.cmu.pact;


/**
 * This class implements a core Pact exception used as a 
 * root class for ctat exceptions.
 */   
public class PactException extends Exception {
    
    public PactException() { super(); }
    public PactException(String M) { super(M); }
}