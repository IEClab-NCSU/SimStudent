package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups;


/**
 * @author Eric Schwelm
 * 
 * Created on: June 02, 2008
 * 
 * This class represents the change made to the group state of
 * the ExampleTracerGraph class.  It is currently limited to 
 * representing the addition or deletion of a group
 */
public class GroupChangeEvent {		
	public static final int GROUPDELETED = 1;
	public static final int GROUPORDERCHANGED = 2;
	public static final int GROUPNAMECHANGED = 3;
	public static final int GROUPLINKADDED = 4;
	public static final int GROUPLINKREMOVED = 5;
	public static final int GROUPCREATED= 6;
	public static final int GROUPSCLEARED= 7;
	public static final int OTHER = 8;
	
	private int eventType;
	private LinkGroup group;
	
	//Lazy event
	public GroupChangeEvent() {
		eventType = OTHER;
	}
	
	public GroupChangeEvent(LinkGroup groupChanged, int eventType)
	{
		group = groupChanged;
		this.eventType = eventType;
	}
	public GroupChangeEvent(int eventType) {
		this.eventType = eventType;
	}
	public LinkGroup getGroupTargeted() {
		return group;
	}
	public int getEventType() {
		return eventType;
	}
}
