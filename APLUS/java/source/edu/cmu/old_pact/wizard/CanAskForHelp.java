package edu.cmu.old_pact.wizard;
// must be the bean which sends firePropertyChange("CURRENTFOCUS", null, this) 
// on focusGained event.
import java.beans.PropertyChangeListener;

public interface CanAskForHelp{
	public void addPropertyChangeListener(PropertyChangeListener l);
	public void removePropertyChangeListener(PropertyChangeListener l);
	public boolean askedForHelp();
}