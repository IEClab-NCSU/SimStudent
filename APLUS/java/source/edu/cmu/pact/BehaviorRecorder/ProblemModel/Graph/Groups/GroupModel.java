package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jdom.Element;
import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
/**
 * The class defines methods for a class that models a group structure.
 * The groups that are to be modeled have the following properties:
 * <br>
 * <br>
 * <ul>
 * <li>Each group contains a name(String) which may be queried or set
 * <li>Each group contains an ordering(boolean).  True means ordered.
 *   False means unordered.  The ordering may be queried or set
 * <li>Each group contains a set of ExampleTracerLinks which may be queried 
 * or set with restrictions
 * <li>Each group contains a set of groups.  These are its subgroups.  They may
 * be queried or set with restrictions
 * <li>There is a single "TopLevel" group which contains at least the union of
 *  all links in all the groups
 * </ul>
 * Note that if group B is a subgroup of group A, we refer to group A as group B's parent.
 * <br><br>  
 * The organization of groups and links has the following restrictions: 
 * <br><Br>
 * <ul>
 * <li>If group A is a subgroup of group B, groups A's links must be a subgroup 
 * of group B's links
 * <li>If group A contains link 1, at most one of link A's subgroups may contain
 * link 1
 *</ul>
 * We say the group sanity is preserved if these conditions are followed and that group
 * sanity is violated if they are not
 * <br>
 * <br>
 * @author Eric Schwelm
 *
 */
public abstract class GroupModel implements Iterable<LinkGroup>, Serializable {
	Set<GroupChangeListener> groupChangeListeners;
	public GroupModel() {
		groupChangeListeners = new HashSet<GroupChangeListener>();
	}
	public void addGroupChangeListener(GroupChangeListener listener) {
		groupChangeListeners.add(listener);
	}
	public void removeGroupChangeListener(GroupChangeListener listener){
		groupChangeListeners.remove(listener);
	}
	protected void notifyListeners(GroupChangeEvent e) {
		for(GroupChangeListener listener : groupChangeListeners) {
			listener.groupChanged(e);
		}
	}
	
	/**
	 * Removes all group information from the model
	 */
	public abstract void clear();
	/**
	 * 
	 * @return The height of the tree, defined in the usual way
	 */
	public abstract int getHeight();
	/**
	 * 
	 * @return The LinkGroup representing the TopLevel group
	 */
	public abstract LinkGroup getTopLevelGroup();
	/**
	 * 
	 * @return True if group may be reentered by default
	 */
	public abstract boolean isDefaultReenterable();
	/**
	 * 
	 * @param isDefaultReenterable - True is groups should be reenterable
	 * by default, false otherwise
	 */
	public abstract void setDefaultReenterable(boolean isDefaultReenterable); 
	/**
	 * 
	 * @param group - The group whose name is to be returned
	 * @return The name string associated with the given group
	 */
	public abstract String getGroupName(LinkGroup group);
	/**
	 * 
	 * @param group - The group whose parent is to be returned
	 * @return The group which contains the given group as a child.
	 * Null if the given group is the TopLevel group
	 */
	public abstract LinkGroup getGroupParent(LinkGroup group);
	/**
	 * 
	 * @param name - The name to search for
	 * @return A group such that getGroupName(group) the given 
	 * name.  Null if no such group exists.
	 */
	public abstract LinkGroup getGroupByName(String name);
	/**
	 * 
	 * @param name - The name to be checked for validity
	 * @return True if no other group exists with the same name
	 * and the given name is at least one character long
	 */
	public abstract boolean isGroupNameValid(String name);
	/**
	 * 
	 * @param group - The group whose name is to be set
	 * @param name - The name to associate with the given group
	 */
	public abstract void setGroupName(LinkGroup group, String name);
	/**
	 * 
	 * @param group - The group whose reenterable status is to be returned
	 * @return True if the group is reenterable, false if not
	 */	
	public abstract boolean isGroupReenterable(LinkGroup group);
	/**
	 * 
	 * @param group - The group whose reenterable status is to be set
	 * @param isReenterable - Should be true if the group is reenterable, 
	 * false if not
	 */
	public abstract void setGroupReenterable(LinkGroup group, boolean isReenterable);
	/**
	 * 
	 * @param group - The group whose ordering is to be returned
	 * @return True if the group is ordered, false if unordered
	 */	
	public abstract boolean isGroupOrdered(LinkGroup group);
	/**
	 * 
	 * @param group - The group whose ordered is to be set
	 * @param isOrdered - Should be true if the group is ordered, false
	 * if unordered
	 */
	public abstract void setGroupOrdered(LinkGroup group, boolean isOrdered);
	/**
	 * 
	 * @param group - The group whose subgroups are to be returned
	 * @return The set of groups which are a subgroup of the given group
	 */
	public abstract Set<LinkGroup> getGroupSubgroups(LinkGroup group);
	/**
	 * 
	 * @param group - The group whose subgroup count is to be returned 
	 * @return The number of subgroups the given group has
	 */
	public int getGroupSubgroupCount(LinkGroup group) {
		return getGroupSubgroups(group).size();
	}
	/**
	 * 
	 * @param group - The group whose links are to be returned
	 * @return The set of links which the given group contains
	 */
	public abstract Set<ExampleTracerLink> getGroupLinks(LinkGroup group);
	/**
	 * 
	 * @param group - The groups whose link count is to be returned
	 * @return The number of links the given group contains
	 */
	public  int getGroupLinkCount(LinkGroup group) {
		return getGroupLinks(group).size();
	}
	/**
	 * 
	 * @param group - The group whose unique links are to be returned
	 * @return The set of links which this group contains, but none of its
	 * subgroups contain.  May be an empty set
	 */
	public abstract Set<ExampleTracerLink> getUniqueLinks(LinkGroup group);
	/**
	 * 
	 * @param link
	 * @return The unique group which contains the given link in its set of 
	 * unique links
	 */
	public abstract LinkGroup getUniqueContainingGroup(ExampleTracerLink link);
	/**
	 * 
	 * @param link - The link which all returned groups must include
	 * @return A set of groups which contains the given link
	 */
	public abstract Set<LinkGroup> getGroupsContainingLink(ExampleTracerLink link);
	/**
	 * 
	 * @param link - the link in question
	 * @return The deepest LinkGroup that contains link.
	 */
	public abstract LinkGroup getLowestLevelGroupOfLink(ExampleTracerLink link);
	
	/**
	 * 
	 * @return A set containing the names of all groups
	 */
	public abstract Set<String> getAllGroupNames();
	/**
	 * The method queries whether adding the given link to the given group 
	 * and all the given group's parents would preserve group sanity
	 * 
	 * To return true, the following requirements must be met:
	 * <ul>
	 * <li>The group does not contain the link
	 * <li>All groups which currently contain the given link must be parents
	 * of the given group
	 * </ul>
	 * 
	 * @param group - The group to be added to
	 * @param link - The link to be added
	 * @return True if adding the link would preserve sanity, false if it 
	 * would not
	 */
	public abstract boolean isLinkAddable(LinkGroup group, ExampleTracerLink link);
	/**
	 * Adds the given link to the given group's set of links.  To ensure that
	 * group sanity is preserved, the link is first removed from all other groups
	 * which contain it, except the TopLevel group.  It is then added to the given
	 * group and all the given groups parents.
	 * 
	 * @param group - The group which is to contain the given link
	 * @param link - The link which is to be added
	 */
	public abstract void addLinkToGroup(LinkGroup group, ExampleTracerLink link);
	public abstract void addLinksForLoadingBRD(ArrayList<ExampleTracerLink> links);
	/**
	 * Removes the given link from all groups, including the topLevel group
	 * @param link - The link which is to be removed
	 */
	public abstract void removeLinkFromModel(ExampleTracerLink link);
	/**
	 * Removes the link from all subgroups of group.  Also removes the link from
	 * group, if group is not the topLevel group.
	 * @param group - The group which is to have its link removed
	 * @param link - The link to remove
	 */
	public abstract void removeLinkFromGroup(LinkGroup group, ExampleTracerLink link);
	/**
	 * 
	 * @param group - The group to be queried regarding link inclusion
	 * @param link - The link to be tested for
	 * @return True if the given group contains the given link, false otherwise
	 */
	public abstract boolean isLinkInGroup(LinkGroup group, ExampleTracerLink link);
	/**
	 * This method queries whether a group could be made containing the given set
	 * of links while preserving group sanity.
	 * 
	 * @param links - The set of links to be made a group
	 * @return True if the links may be added as a group, false otherwise
	 */
	public abstract String isLinkSetAddableAsGroup(Set<ExampleTracerLink> links);
	/**
	 * 
	 * @param name - The name to which the group maps
	 * @param isOrdered - True if the group maps to ordered, false if it maps to unordered
	 * @param links - The set of links the group will contain
	 * @return - Equivalent to isLinkSetAddableAsGroup(links)
	 */
	public abstract String addGroup(String name, boolean isOrdered, Set<ExampleTracerLink> links);
	/**
	 * This method deletes the given group and all information it maps to.  
	 * The given group's subgroups then become subgroups of the given group's
	 * parent.  The TopLevel group may not be removed.  This operation always
	 * preserves group sanity
	 *  
 	 * @param group - Group to be removed
	 */
	public abstract void removeGroupKeepSubgroups(LinkGroup group);
	/**
	 * This method deletes all the subgroups of the given group and all the
	 * information they map to.  It all deletes the subgroups' subgroups to
	 * any level of nesting.  This operation always preserves group sanity
	 * @param group
	 */
	public abstract void removeAllGroupSubgroups(LinkGroup group);
	/**
	 * 
	 * @param group
	 * @return The string to be displayed in the group tree for
	 * the given group
	 */
	public abstract String getTreeText(LinkGroup group);
	/**
	 * Returns an iterator that iterates through all groups contained by the group model
	 */
	public abstract Iterator<LinkGroup> iterator();
	/**
	 * Serializes the current group state
	 * @param w - Object to write to
	 * @throws SAXException
	 */
	public abstract void printXML(DataWriter w) throws SAXException;
	/**
	 * Deserializes the saved group state
	 * @param element - The head element of the group XML
	 * @param topLevelUnordered - For older format XML, a separate indicator for whether
	 *        the top-level group is ordered (false, the default) or unordered.
	 */
	public abstract void readFromXML(Element element, boolean topLevelUnordered);
}
