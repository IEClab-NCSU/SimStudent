package edu.cmu.pact.BehaviorRecorder.View.GraphInspector;

import java.util.Comparator;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
/**
 * This class defines the ordering on links in the GroupTree.  It currently
 * sorts from smallest link id to largest
 * @author Eric Schwelm
 *
 */
public class TreeModelLinkComparator implements Comparator<ExampleTracerLink> {
	public int compare(ExampleTracerLink o1, ExampleTracerLink o2) {		
		return o1.getID()-o2.getID();
	}
}
