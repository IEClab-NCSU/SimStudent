package edu.cmu.old_pact.cmu.tutor;
import java.awt.Rectangle;

import edu.cmu.old_pact.dormin.ObjectProxy;

public interface TutoredTool extends SharedObject {
	public void flag(String selection);
	public void unflag(String selection);
	public Rectangle getBounds();
	public void tutorResponseComplete(String selection, String action, String input);
	public void requestFocus();
	public void suggestNewProblem();
	public void displayCompletionMessage();
	public ObjectProxy getObjectProxy();
}
