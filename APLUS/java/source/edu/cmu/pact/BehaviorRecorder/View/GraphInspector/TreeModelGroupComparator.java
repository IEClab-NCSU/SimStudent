package edu.cmu.pact.BehaviorRecorder.View.GraphInspector;

import java.util.Comparator;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
/**
 * This class defines the ordering on groups in the GroupTree.  It currently
 * sorts by group name lexicographically
 * @author Eric Schwelm
 *
 */
public class TreeModelGroupComparator implements Comparator<LinkGroup> {
	GroupModel groupModel;
	public TreeModelGroupComparator(GroupModel groupModel) {
		this.groupModel = groupModel;
	}
	public int compare(LinkGroup o1, LinkGroup o2) {		
		return groupModel.getGroupName(o1).compareTo(groupModel.getGroupName(o2));
	}
}
