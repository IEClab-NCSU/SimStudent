package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;

/**
 * A set of links with defined ordering constraints. 
 * Groups can nest--that is, a group can have another group
 * completely inside it--to any depth. But groups cannot overlap.
 */
public class DefaultLinkGroup implements Comparable<DefaultLinkGroup>, LinkGroup {
	
	private String groupName;
	//True means ordered, false means unordered
	private boolean isOrdered;
	private boolean isReenterable;
	private Set<ExampleTracerLink> links;
	private Set<LinkGroup> subgroups;
	private LinkGroup parent;
	
	/**
	 * Constructor
	 * @param groupName
	 */
	DefaultLinkGroup(String groupName, boolean isOrdered, boolean isReenterable) {
		this(groupName, isOrdered, isReenterable, null);
	}
	
	/**
	 * Constructor
	 * @param groupName
	 */
	DefaultLinkGroup(String groupName, boolean isOrdered, boolean isReenterable,Set<ExampleTracerLink> links) {
		this.groupName = groupName;
		this.isOrdered = isOrdered;
		this.isReenterable = isReenterable;
		if(links!=null)
			this.links = new LinkedHashSet<ExampleTracerLink>(links);
		else
			this.links = new LinkedHashSet<ExampleTracerLink>();
		subgroups = new LinkedHashSet<LinkGroup>();
		parent=null;
	}	
	
	/**
	 * Single-level (non-nested) dump of group contents.
	 * @return "name(mode){linkId, ..., subGroupName, ...}"
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer(getName());
		sb.append('(').append(getMode()).append(')');
		sb.append('{');		
		for (Iterator<ExampleTracerLink> it = getLinks().iterator(); it.hasNext(); ) {
			sb.append(((ExampleTracerLink) it.next()).getUniqueID());
			if (it.hasNext())
				sb.append(',');
		}
		for (Iterator<LinkGroup> it = getSubgroups().iterator(); it.hasNext(); )
			sb.append(',').append(((DefaultLinkGroup) it.next()).getName());
		sb.append('}');
		return sb.toString();
	}
	
	public String getTreeDisp() {
		StringBuffer sb = new StringBuffer(getName());
		sb.append('(').append(getMode());
		sb.append(',');
		if(isReenterable())
			sb.append(" Reenterable");
		else
			sb.append(" Not Reenterable");
		sb.append(')');
		
		return sb.toString();
	}

	public String getName() {
		return groupName;}
	
	public void setName(String name) {
		groupName = name;}

	/**
	 * @return - returns the ordering of the group as a string
	 */
	String getMode() {
		if(isOrdered)
			return "Ordered";
		return "Unordered";
	}
	
	public boolean isOrdered() {
		return isOrdered;}
	
	public void setOrdered(boolean isOrdered) {
		this.isOrdered = isOrdered;}
	
	public boolean isReenterable() {
		return isReenterable;}
	
	public void setReenterable(boolean isReenterable) {
		this.isReenterable = isReenterable;}
	
	public Set<ExampleTracerLink> getLinks() {
		return links;}
	
	void addLink(ExampleTracerLink link) {
		links.add(link);}
	
	public boolean containsLink(ExampleTracerLink link) {
		return links.contains(link);}

	boolean removeLink(ExampleTracerLink link) {
		return links.remove(link);}
	
	public Set<LinkGroup> getSubgroups() {
		return subgroups;}
	
	public void addSubgroup(LinkGroup toBeAdded) {
		subgroups.add(toBeAdded);}
	
	public void removeSubgroup(LinkGroup subGroup) {
		subgroups.remove(subGroup);}
	
	public void removeAllSubGroups() {
		subgroups.clear();}
	
	public LinkGroup getParent() {
		return parent;}
	
	public void setParent(LinkGroup group) {
		parent=group;}
	
	/**
	 * Method implemented for comparable. Compares each group object
	 * according to the number of links contained.
	 */
	public int compareTo(DefaultLinkGroup group) {
		if (links.size()<group.getLinks().size())
			return 1;
		else if (links.size()==group.getLinks().size())
			return 0;
		else
			return -1;
	}
}