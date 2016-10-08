package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jdom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.megginson.sax.DataWriter;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemStateWriter;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.Utilities.trace;

public class DefaultGroupModel extends GroupModel {

    //XML tags
    private static final String MAIN_ELEMENT_NAME = "EdgesGroups";
    private static final String ORDERED_ATTR = "ordered";
    private static final String REENTERABLE_ATTR = "reenterable";
    private static final String LEGACY_UNORDERED_ATTR = "unordered";
    private static final String NAME_ATTR = "name";
    private static final String LINK_ELEMENT_NAME = "link";
    private static final String GROUP_ELEMENT_NAME = "group";
    private static final String LINK_ID_ATTR = "id";
    
    DefaultLinkGroup TopLevel;
    boolean isDefaultReenterable;
    
    public DefaultGroupModel() {
	clear();		
    }


    public String addGroup(String name, boolean isOrdered,
			   Set<ExampleTracerLink> links) {
	LinkGroup naturalContainingGroup;
	try {
	    naturalContainingGroup 
		= getNaturalContainingGroup(getTopLevelGroup(), links);
	}		
	catch (Exception e) {
	    return e.getMessage();
	}
	DefaultLinkGroup newGroup 
	    = new DefaultLinkGroup(name, isOrdered, isDefaultReenterable, links);

	addSubgroupPreserveSanity(naturalContainingGroup, newGroup);
	notifyListeners(new GroupChangeEvent(newGroup, 
					     GroupChangeEvent.GROUPCREATED));
	return "";
    }


    public String isLinkSetAddableAsGroup(Set<ExampleTracerLink> links) {
	try {
	    getNaturalContainingGroup(getTopLevelGroup(), links);
	}		
	catch (Exception e) {
	    return e.getMessage();
	}
	return "";
    }


    private LinkGroup getNaturalContainingGroup(LinkGroup group, Set<ExampleTracerLink> links) throws Exception {
	if(links.size()==getGroupLinks(group).size()) {
	    for(ExampleTracerLink link : getGroupLinks(group)) {
		if(!links.contains(link))
		    throw new Exception("Invalid link selection: Partial overlap with existing group: "+group);
	    }
	    throw new Exception("Group containing this set of links already exists: " + group);
	}
	if(links.size()>=getGroupLinks(group).size())
	    throw new Exception("Invalid link selection: Partial overlap with existing group: "+group);
	for(ExampleTracerLink link : links) {
	    if(!isLinkInGroup(group, link))
		throw new Exception("Invalid link selection: Partial overlap with existing group: "+group);
	    for(LinkGroup subgroup : getGroupSubgroups(group)) {
		/* if a subgroup contains a link, it must either 
		 * contain them all or contain exactly a subset */
		if(isLinkInGroup(subgroup, link)) {
		    if (getGroupLinkCount(subgroup)>=links.size())
			return getNaturalContainingGroup(subgroup, links);
		    else {
			for(ExampleTracerLink subgroupLink : getGroupLinks(subgroup)) {
			    if(!links.contains(subgroupLink))
				throw new Exception("Invalid link selection: Partial overlap with existing group: "+group);
			}
		    }						
		}
	    }
	}		
	return group;
    }	

    private void addSubgroupPreserveSanity(LinkGroup parent, LinkGroup child) {
	Iterator<LinkGroup> iter = getGroupSubgroups(parent).iterator();
	while(iter.hasNext()){
	    LinkGroup parentSubgroup = iter.next();
	    Set<ExampleTracerLink> parentSubgroupLinks = getGroupLinks(parentSubgroup);
	    if(parentSubgroupLinks.size()==0) {
		iter.remove();
		addSubgroup(child, parentSubgroup);				
	    }
	    else if(isLinkInGroup(child, parentSubgroupLinks.iterator().next())) {
		iter.remove();
		addSubgroup(child, parentSubgroup);					
	    }
	}
	addSubgroup(parent, child);
    }

    private void addSubgroup(LinkGroup parent, LinkGroup child) {
	DefaultLinkGroup group = (DefaultLinkGroup) parent;
	group.addSubgroup(child);
	((DefaultLinkGroup)child).setParent(group);
    }
	
    public void addLinkToGroup(LinkGroup group, ExampleTracerLink link) {
	removeLinkFromGroup(getTopLevelGroup(), link);
	for(;group!=null; group = getGroupParent(group))
	    internalAddLinkToGroup(group, link);
	notifyListeners(new GroupChangeEvent());
    }
	
    public void addLinksForLoadingBRD(ArrayList<ExampleTracerLink> links){
    	int i = 0;
    	for(i = 0; i < links.size(); i++){
    		TopLevel.addLink(links.get(i));
    	}
    	notifyListeners(new GroupChangeEvent());
    }

    private void internalAddLinkToGroup(LinkGroup grp, ExampleTracerLink link) {
	DefaultLinkGroup group = (DefaultLinkGroup) grp;
	group.addLink(link);}
    
    private boolean internalRemoveLinkFromGroup(LinkGroup grp, ExampleTracerLink link) {
	DefaultLinkGroup group = (DefaultLinkGroup) grp;
	return group.removeLink(link);}
	
    public Set<String> getAllGroupNames() {
	Set<String> groupNames = new LinkedHashSet<String>();
	for(LinkGroup group : this) {
	    groupNames.add(getGroupName(group));
	}
	return groupNames;
    }
    
    public Set<ExampleTracerLink> getGroupLinks(LinkGroup grp) {
	DefaultLinkGroup group = (DefaultLinkGroup) grp;
	return group.getLinks();
    }
    
    public String getGroupName(LinkGroup grp) {
	DefaultLinkGroup group = (DefaultLinkGroup) grp;
	return group.getName();
    }
    
    public LinkGroup getGroupParent(LinkGroup grp) {
	DefaultLinkGroup group = (DefaultLinkGroup) grp;
	return group.getParent();
    }
    
    public Set<LinkGroup> getGroupSubgroups(LinkGroup grp) {
	DefaultLinkGroup group = (DefaultLinkGroup) grp;
	return (group != null ? group.getSubgroups() : null);
    }

    public LinkGroup getTopLevelGroup() {
	return TopLevel;
    }

    public LinkGroup getUniqueContainingGroup(ExampleTracerLink link) {
	for(LinkGroup group : this)
	    if(getUniqueLinks(group).contains(link))
		return group;
	return null;
    }
    public Set<ExampleTracerLink> getUniqueLinks(LinkGroup group) {
	//Populate uniqueLinks with all links
	Set<ExampleTracerLink> uniqueLinks = 
	    new LinkedHashSet<ExampleTracerLink>(getGroupLinks(group));
	
	for(LinkGroup subgroup : getGroupSubgroups(group)) {
	    for(ExampleTracerLink link : getGroupLinks(subgroup))				
		uniqueLinks.remove(link); //Remove all links contained by subgroups
	}
	return uniqueLinks; //remaining links are the unique links
    }
    public boolean isGroupOrdered(LinkGroup grp) {
	DefaultLinkGroup group = (DefaultLinkGroup) grp;
	return group.isOrdered();
    }
    public boolean isLinkAddable(LinkGroup group, ExampleTracerLink link) {	
	if(isLinkInGroup(group, link))
	    return false;
	
	//Build set of parents of group
	Set<LinkGroup> parents = new LinkedHashSet<LinkGroup>();
	LinkGroup currentGroup = getGroupParent(group);
	for(;currentGroup!=null; currentGroup = getGroupParent(currentGroup))		
	    parents.add(currentGroup);
	
	//Iterate through all groups
	for(LinkGroup linkGroup : this) {
	    /* If a group contains the link and is not a 
	     * parent of group, return false; */
	    if(isLinkInGroup(linkGroup, link) && !parents.contains(linkGroup))
		return false;
	}
	return true;
    }
    
    public boolean isLinkInGroup(LinkGroup grp, ExampleTracerLink link) {
	DefaultLinkGroup group = (DefaultLinkGroup) grp;
	return group.containsLink(link);
    }	
	
    public Iterator<LinkGroup> iterator() {
	return new GroupIterator(getTopLevelGroup());
    }
    
    public void removeSubgroup(LinkGroup grp, LinkGroup subgroup) {
	DefaultLinkGroup group = (DefaultLinkGroup) grp;
	group.removeSubgroup(subgroup);
	notifyListeners(new GroupChangeEvent(subgroup, GroupChangeEvent.GROUPDELETED));
    }
    
    public void removeAllGroupSubgroups(LinkGroup grp) {
	DefaultLinkGroup group = (DefaultLinkGroup) grp;
	if (trace.getDebugCode("groups"))
		trace.outNT("groups", "removeAllGroupSubgroups("+group.getName()+") nSubgroups "+group.getSubgroups().size());
	Iterator<LinkGroup> iter = new GroupIterator(group);
	ArrayList<LinkGroup> subgroups = new ArrayList<LinkGroup>();
	iter.next();//Iter hits group and all its subgroups.  Drop group here.  Only want subgroups
	//Save subgroups to notify later with them
	while(iter.hasNext()) {
	    subgroups.add(iter.next());
	}
	group.removeAllSubGroups();
	for(LinkGroup subgroup : subgroups)
	    notifyListeners(new GroupChangeEvent(subgroup, GroupChangeEvent.GROUPDELETED));		
    }
	
    public void removeGroupKeepSubgroups(LinkGroup group) {
	LinkGroup parent = getGroupParent(group);
	Set<LinkGroup> subgroups = getGroupSubgroups(group);
	if (trace.getDebugCode("groups"))
		trace.outNT("groups", "removeGroupKeepSubgroups("+((DefaultLinkGroup)group).getName()+") nSubgroups "+subgroups.size());
	for(LinkGroup subgroup : subgroups) {
	    addSubgroup(parent, subgroup);
	}
	removeSubgroup(parent, group);
	notifyListeners(new GroupChangeEvent(group, GroupChangeEvent.GROUPDELETED));
    }	
    
    public void setGroupName(LinkGroup grp, String name) {
	DefaultLinkGroup group = (DefaultLinkGroup) grp;
	group.setName(name);
	notifyListeners(new GroupChangeEvent());
    }
	
    public void setGroupOrdered(LinkGroup grp, boolean isOrdered) {
    	DefaultLinkGroup group = (DefaultLinkGroup) grp;
    	if (trace.getDebugCode("pm")) trace.printStack("pm", "isOrdered "+isOrdered+" group "+group);
    	group.setOrdered(isOrdered);
    	notifyListeners(new GroupChangeEvent());
    }
    
    //This class iterates over the current group and all the groups in 
    //its subgroup's iterators
    class GroupIterator implements Iterator<LinkGroup> {
	LinkGroup currentGroup;
	Iterator<LinkGroup> currentSubIterator;
	Iterator<LinkGroup> iteratorIterator;
	public GroupIterator(LinkGroup group) {
	    currentGroup = group;
	    iteratorIterator = getGroupSubgroups(group).iterator();
	    if(iteratorIterator.hasNext())
		currentSubIterator = new GroupIterator(iteratorIterator.next());
	}
	public boolean hasNext() {
	    return currentGroup!=null;
	}
	
	public LinkGroup next() {
	    if(currentGroup==null)
		throw new NoSuchElementException();
	    LinkGroup temp = currentGroup;
	    if(currentSubIterator==null)
		currentGroup=null;
	    else if(currentSubIterator.hasNext())
		currentGroup = currentSubIterator.next();
	    else if (iteratorIterator.hasNext())
		{
		    currentSubIterator = new GroupIterator(iteratorIterator.next());			
		    currentGroup = currentSubIterator.next();
		}
	    else
		currentGroup=null;
	    
	    
	    return temp;
	}	
	
	public void remove() { 			
	}		
    }
    
    public LinkGroup getGroupByName(String name) {
	for(LinkGroup group : this) {
	    if(getGroupName(group).equals(name))
		return group;
	}
	return null;
    }
	
    public LinkGroup getLowestLevelGroupOfLink(ExampleTracerLink link){
    	LinkGroup temp = getImmediateGroupOfLink(link, getTopLevelGroup());
    	if(temp!=null)
    		return temp;
    	if(isLinkInGroup(getTopLevelGroup(), link))
    		return getTopLevelGroup();
    	return null;
    }
    
    private LinkGroup getImmediateGroupOfLink(ExampleTracerLink link, LinkGroup group){
    	LinkGroup temp = null;
    	for(LinkGroup subGroup : getGroupSubgroups(group)) {    		
    		if(isLinkInGroup(subGroup, link)){
    			temp = getImmediateGroupOfLink(link,subGroup);
    			if(temp!=null)
    				return temp;
    			else
    				return subGroup;
    		}
    	}
    	return null;
    }
    public Set<LinkGroup> getGroupsContainingLink(ExampleTracerLink link) {
	Set<LinkGroup> groups = new LinkedHashSet<LinkGroup>();
	for(LinkGroup group : this) {
	    if(isLinkInGroup(group, link))
		groups.add(group);
	}
	return groups;
    }
	
    public int getHeight() {
	return height(getTopLevelGroup());
    }
    
    private int height(LinkGroup group) {
	int max=0;
	for(LinkGroup subgroup : getGroupSubgroups(group)) {
	    int height = height(subgroup);
	    if(height>max)
		max = height;
	}
	return max+1;
    }

    public boolean isGroupNameValid(String name) {
	if(name==null || name.length()<1)
	    return false;
	for(LinkGroup group : this) {
	    if(getGroupName(group).equals(name))
		return false;
	}
	return true;
    }

    /**
     * @return multi-line, indented format of {@link #toElement()}
     */
    public String toXMLString() {
    	return ProblemStateWriter.multiLineOutputter.outputString(toElement());   	    	
    }
    
    /**
     * Generate an XML Element for a the entire group collection, with subgroups.
     * @return Element created
     */
    public Element toElement() {
    	Element elt = new Element(MAIN_ELEMENT_NAME);
    	elt.setAttribute(ORDERED_ATTR, Boolean.toString(isGroupOrdered(getTopLevelGroup())));
    	for(LinkGroup group : getGroupSubgroups(getTopLevelGroup()))
    		elt.addContent(makeGroupElement(group));
    	return elt;
    }

    /**
     * Generate an XML Element for a single group, with subgroups.
     * @param group
     * @return Element created
     */
    private Element makeGroupElement(LinkGroup group) {
    	Element elt = new Element(GROUP_ELEMENT_NAME);
    	elt.setAttribute(NAME_ATTR, getGroupName(group));
    	elt.setAttribute(ORDERED_ATTR, Boolean.toString(isGroupOrdered(group))); 
        elt.setAttribute(REENTERABLE_ATTR, Boolean.toString(isGroupReenterable(group)));
        
        for (ExampleTracerLink link : getUniqueLinks(group)) {
        	Element child = new Element(LINK_ELEMENT_NAME);
        	child.setAttribute(LINK_ID_ATTR, Integer.toString(link.getID()));
        	elt.addContent(child);
        }
        for (LinkGroup subgroup : getGroupSubgroups(group))
        	elt.addContent(makeGroupElement(subgroup));
        
        return elt;
	}


	public void printXML(DataWriter w) throws SAXException {
	AttributesImpl atts = new AttributesImpl();
	atts.addAttribute("", ORDERED_ATTR, "", "Boolean", Boolean.toString(isGroupOrdered(getTopLevelGroup())));
	w.startElement("", MAIN_ELEMENT_NAME, "", atts);
	for(LinkGroup group : getGroupSubgroups(getTopLevelGroup())) {
	    printGroupXML(w, group);
	}
	w.endElement(MAIN_ELEMENT_NAME);
    }
    
    public void printGroupXML(DataWriter w, LinkGroup group) throws SAXException {
	AttributesImpl atts = new AttributesImpl();
        atts.addAttribute("", NAME_ATTR, "", "String", getGroupName(group));
        atts.addAttribute("", ORDERED_ATTR, "", "Boolean",
			  Boolean.toString(isGroupOrdered(group)));
        atts.addAttribute("", REENTERABLE_ATTR, "", "Boolean",
			  Boolean.toString(isGroupReenterable(group)));
        
        w.startElement("", GROUP_ELEMENT_NAME, "", atts);
        for (ExampleTracerLink link : getUniqueLinks(group)) {
	    atts.clear();
	    atts.addAttribute("", LINK_ID_ATTR, "", "Integer",
			      Integer.toString(link.getID()));
	    w.emptyElement("", LINK_ELEMENT_NAME, "", atts);
        }
        for (LinkGroup subgroup : getGroupSubgroups(group)) {
	    printGroupXML(w, subgroup);
        }
	w.endElement(GROUP_ELEMENT_NAME);
    }

    /**
     * Build from an XML element.
     * @param element
	 * @param topLevelUnordered - For older format XML, a separate indicator for whether
	 *        the top-level group is ordered (false, the default) or unordered.
     * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel#readFromXML(org.jdom.Element, boolean)
     */
    public void readFromXML(Element element, boolean topLevelUnordered) {
    	Map<Integer, ExampleTracerLink> idToEdgeMap = createIdToLinkMap();
    	//Old XML format lacks ORDERED_ATTR
    	if(element.getAttributeValue(ORDERED_ATTR)==null) {
    		for(Element groupElement : (List<Element>)element.getChildren()) {
    			legacyReadFromXML(groupElement, idToEdgeMap);
    		}
        	setGroupOrdered(getTopLevelGroup(), !topLevelUnordered);
    		return;
    	}

    	setGroupOrdered(getTopLevelGroup(), 
    			Boolean.valueOf(element.getAttributeValue(ORDERED_ATTR)));
    	for(Element groupElement : (List<Element>)element.getChildren()) {
    		readFromXML(groupElement, idToEdgeMap);
    	}
    }

    private Set<ExampleTracerLink> readFromXML(Element element, Map<Integer, ExampleTracerLink> idToEdgeMap) {
	Set<ExampleTracerLink> links = new LinkedHashSet<ExampleTracerLink>();
	
	if(!element.getName().equals(GROUP_ELEMENT_NAME)) {
	    trace.err("Undefined element <"+element.getName()+"> in edge groups");
            return links;                      // ignore other elements
	}
	
	String name = element.getAttributeValue(NAME_ATTR);
	if (!isGroupNameValid(name))
	    return links;
	
	Boolean isOrdered = Boolean.valueOf(element.getAttributeValue(ORDERED_ATTR));
	
	Boolean isReenterable = Boolean.valueOf(element.getAttributeValue(REENTERABLE_ATTR));
	
	for (Element subelement : (List<Element>)element.getChildren()) {
	    if (subelement.getName().equals(LINK_ELEMENT_NAME)) {
		String edgeIdStr = subelement.getAttributeValue(LINK_ID_ATTR);
		try {
		    Integer edgeId = Integer.valueOf(edgeIdStr);
		    ExampleTracerLink link = idToEdgeMap.get(edgeId);
		    if (link == null)
			throw new RuntimeException("undefined <"+LINK_ID_ATTR+"> number: "+edgeId);
		    links.add(link);
		} 
		catch (NumberFormatException nfe) {
		    throw new RuntimeException("undefined <"+LINK_ID_ATTR+"> value: "+nfe);}				
	    } 
	    else 
		links.addAll(readFromXML(subelement, idToEdgeMap));
	}
	
	addGroup(name, isOrdered, links);
	setGroupReenterable(getGroupByName(name), isReenterable);
	return links;
    }
    private void legacyReadFromXML(Element element, Map<Integer, ExampleTracerLink> idToEdgeMap) {
	if(!element.getName().equals(GROUP_ELEMENT_NAME)) {
	    trace.err("Undefined element <"+element.getName()+"> in edge groups");
            return;                      // ignore other elements
	}
	
	String name = element.getAttributeValue(NAME_ATTR);
	if (!isGroupNameValid(name))
	    return;
	
	Boolean isUnordered = Boolean.valueOf(element.getAttributeValue(LEGACY_UNORDERED_ATTR));
	
	Set<ExampleTracerLink> links = new LinkedHashSet<ExampleTracerLink>();
	
	for (Element subelement : (List<Element>)element.getChildren()) {
	    if (subelement.getName().equals(LINK_ELEMENT_NAME)) {
		String edgeIdStr = subelement.getAttributeValue(LINK_ID_ATTR);
		try {
		    Integer edgeId = Integer.valueOf(edgeIdStr);
		    ExampleTracerLink link = idToEdgeMap.get(edgeId);
		    if (link == null)
			throw new RuntimeException("undefined <"+LINK_ID_ATTR+"> number: "+edgeId);
		    links.add(link);
		} 
		catch (NumberFormatException nfe) {
		    throw new RuntimeException("undefined <"+LINK_ID_ATTR+"> value: "+nfe);}				
	    } 
	    else 
		legacyReadFromXML(subelement, idToEdgeMap);
	}
	
	addGroup(name, !isUnordered, links);
    }
    
    private Map<Integer, ExampleTracerLink> createIdToLinkMap() {
	Map<Integer, ExampleTracerLink> map = new HashMap<Integer, ExampleTracerLink>();
		for(ExampleTracerLink link : getGroupLinks(getTopLevelGroup())) {
		    map.put(Integer.valueOf(link.getID()), link);
		}
		return map;
    }
    
    public void removeLinkFromGroup(LinkGroup group, ExampleTracerLink link) {		
		if (trace.getDebugCode("groups"))
			trace.out("groups", "DGM.removeLinkFromGroup(L="+link.getUniqueID()+", G="+group+")");
    	internalRemoveLinkFromGroupRecursive(group, link, false);
		notifyListeners(new GroupChangeEvent());
    }
	
    private void internalRemoveLinkFromGroupRecursive(LinkGroup grp, ExampleTracerLink link, boolean canRemoveFromTopLevel) {
    	DefaultLinkGroup group = (DefaultLinkGroup) grp;
    	if(group.containsLink(link)) {
    		for(LinkGroup subgroup : getGroupSubgroups(grp))   // CTAT2953: delete depth-first
    			removeLinkFromGroup(subgroup, link);
    		if(!group.equals(getTopLevelGroup()) || canRemoveFromTopLevel) {
    			if (trace.getDebugCode("groups"))
    				trace.out("groups", "DGM.internalRemoveLinkFromGroup(L="+link.getUniqueID()+", G="+group+")");
    			internalRemoveLinkFromGroup(group, link);
    		}
    	}
    }
    
    /* This is for removing a link from the actual graph, not just from a group.
     * For this reason we recurse over all the groups, merging duplicate groups,
     * and removing empty groups.
     * 
     * (non-Javadoc)
     * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel#removeLinkFromModel(edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink)
     */
    public void removeLinkFromModel(ExampleTracerLink link) {
    	//internalRemoveLinkFromGroupRecursive(getTopLevelGroup(), link, true);
    	LinkGroup topG = getTopLevelGroup();

    	List<LinkGroup> subgroupsToDelete = new ArrayList<LinkGroup>();
    	for(LinkGroup subgroup : getGroupSubgroups(topG)){  // CTAT2953: delete depth-first
    		if (((DefaultLinkGroup)subgroup).containsLink(link))
    			subgroupsToDelete.add(subgroup);
	    	removeLinkUpdateGroups((DefaultLinkGroup)subgroup, link);
	    }
    	if (trace.getDebugCode("groups"))
    		trace.out("groups", "removeLinkFromModel(L="+link.getUniqueID()+"): top-level containing groups: "+
    				subgroupsToDelete);
    	for (LinkGroup subgroup : subgroupsToDelete) {
    		if (((DefaultLinkGroup)subgroup).getLinks().size() < 1)
    			removeGroupKeepSubgroups(subgroup);
    	}

    	internalRemoveLinkFromGroup(topG, link);    	//this removes the link
    	
    	//mergeOnDuplicateGroups(getTopLevelGroup());
    	notifyListeners(new GroupChangeEvent());
    }
    
    private void mergeOnDuplicateGroups(LinkGroup grp){
    	DefaultLinkGroup group = (DefaultLinkGroup) grp;
    	Set<LinkGroup> subGroups = group.getSubgroups();
    	if(subGroups.size()==1){
    		DefaultLinkGroup temp = (DefaultLinkGroup) subGroups.iterator().next();
    		//a duplicate group.. merge and recurse since we might have triplicate groups
    		if(temp.getLinks().size() == group.getLinks().size()){
    			removeGroupKeepSubgroups(temp);
    			mergeOnDuplicateGroups(grp);
    		}
    	}else{
    		for(LinkGroup subgroup : subGroups){
    			mergeOnDuplicateGroups(subgroup);
    	    }
    	}
    	return;
    }
    
    /**
     * Remove a link from a group and its subgroups. Merge groups that are then empty.
     * This algo is depth-first to avoid concurrent modification exceptions in the subgroups loop.
     * @param group
     * @param link
     */
    private void removeLinkUpdateGroups(DefaultLinkGroup group, ExampleTracerLink link){
    	boolean hasLink = group.containsLink(link);
    	if (trace.getDebugCode("groups"))
    		trace.outNT("groups", "removeLinkUpdateGroups(G="+group.getName()+
    				",L="+link.getUniqueID()+") hasLink "+hasLink);
    	if (!hasLink)
    		return;

    	List<LinkGroup> subgroupsToDelete = new ArrayList<LinkGroup>();
    	for(LinkGroup subgroup : getGroupSubgroups(group)) { // depth-first: CTAT2953
	    	removeLinkUpdateGroups((DefaultLinkGroup)subgroup, link);
        	if(((DefaultLinkGroup)subgroup).getLinks().size() == 0)    // remove empty group    
        		subgroupsToDelete.add(subgroup);
    	}
    	for (LinkGroup subgroup : subgroupsToDelete)
    		removeGroupKeepSubgroups(subgroup);

		boolean wasRemoved = internalRemoveLinkFromGroup(group, link);
    	if (!wasRemoved)
    		trace.err("removeLinkUpdateGroups(G="+group.getName()+
    				",L="+link.getUniqueID()+") link not found on delete");

    	if(group.getLinks().size() == 0)    // remove empty group    
    		removeAllGroupSubgroups(group);
    }
    
    public void clear() {
	TopLevel = new DefaultLinkGroup("defaultName", true, isDefaultReenterable);
	notifyListeners(new GroupChangeEvent(GroupChangeEvent.GROUPSCLEARED));
    }
    
    public boolean isGroupReenterable(LinkGroup grp) {
	DefaultLinkGroup group = (DefaultLinkGroup) grp;
	return group.isReenterable();
    }
	
    public void setGroupReenterable(LinkGroup grp, boolean isReenterable) {
	DefaultLinkGroup group = (DefaultLinkGroup) grp;
	group.setReenterable(isReenterable);
    }
    
    public boolean isDefaultReenterable() {
	return isDefaultReenterable;
    }
    
    public void setDefaultReenterable(boolean isDefaultReenterable) {
	this.isDefaultReenterable = isDefaultReenterable;
    }

    public String getTreeText(LinkGroup grp) {
	DefaultLinkGroup group = (DefaultLinkGroup) grp;
	return group.getTreeDisp();
    }
}
