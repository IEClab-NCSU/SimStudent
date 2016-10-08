/*
 * Created on May 6, 2004
 *
 */
package pact.CommWidgets.event;

import java.util.EventListener;

/**
 * @author sanket
 *
 */
public interface ProblemDoneListener extends EventListener {
	
	public void problemDone(ProblemDoneEvent e);
}
