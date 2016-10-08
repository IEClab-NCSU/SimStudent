/***********************************
 * BehaviorRecorderException.java
 * @author Collin Lynch
 * @date 6/22/2009
 * @copyright 2009 CTAT project.
 */
package edu.cmu.pact.BehaviorRecorder;

import edu.cmu.pact.PactException;

/**
 * This class implements a Behavior Recorder specific 
 * exception class for use in subclasses.
 */
public class BehaviorRecorderException extends PactException {
    
    public BehaviorRecorderException() { super(); }
    public BehaviorRecorderException(String M) { super(M); }
}