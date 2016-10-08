/*****************************************
 * LinkInspectorException
 * @author Collin Lynch
 * @date 06/22/2009
 * @copyright 2009 CTAT Project.
 */

package edu.cmu.pact.BehaviorRecorder.LinkInspector;

import edu.cmu.pact.BehaviorRecorder.BehaviorRecorderException;

/**
 * This class provides exceptions for the LinkInspector
 * tracking errors as needed.
 *
 * At some point the CommException should be moved to
 * the current code.
 */
public class LinkInspectorException extends BehaviorRecorderException {
    LinkInspectorException() { super(); }
    LinkInspectorException(String M) { super(M); }
} 
