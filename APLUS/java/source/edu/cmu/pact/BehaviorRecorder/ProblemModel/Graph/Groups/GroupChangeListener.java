package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups;

import java.util.EventListener;

/**
 * @author Eric Schwelm
 * 
 * Created on: June 02, 2008
 * 
 * This class is a listener for changes in the ExampleTracerGroups inside
 * the ExampleTracerGraph class
 */
public interface GroupChangeListener extends EventListener{
	public void groupChanged(GroupChangeEvent e);
}
