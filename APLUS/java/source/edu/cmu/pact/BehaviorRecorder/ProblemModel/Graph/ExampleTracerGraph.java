package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.BRDLoadedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeCreatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeDeletedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeRewiredEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeUpdatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NewProblemEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NodeCreatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NodeDeletedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.DefaultGroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.LinkGroup;
import edu.cmu.pact.Utilities.trace;

public class ExampleTracerGraph implements ProblemModelListener {
	
    /** Name of the universal group, which contains all links and subgroups. */
    private static final String TOP_LEVEL = "Top Level";
	
    private ExampleTracerTracer exampleTracerTracer;
    private ArrayList<ExampleTracerNode> nodes;
    private ArrayList<ExampleTracerLink> links;	
    private LinkedHashMap<Integer, ExampleTracerNode> nodeMap;	
    private GroupModel groupModel;
    /**
     * Constructor.  Equivalent to
     * {@link #ExampleTracerGraph(boolean, boolean) ExampleTracerGraph(false, false)}
     */
    public ExampleTracerGraph() {
    	this(false, false);
    }
	
 
    /**
     * Constructor. Equivalent to
     * {@link #ExampleTracerGraph(boolean, boolean) ExampleTracerGraph(false, youStartYouFinish)}
     * @param youStartYouFinish
     */
    ExampleTracerGraph(boolean youStartYouFinish) {
	this(false, youStartYouFinish);
    }
	
    /**
     * Constructor
     * @param isUnordered true if the top-level group should be unordered,
     *                    false if ordered
     * @param youStartYouFinish
     */
    public ExampleTracerGraph(boolean isUnordered, boolean youStartYouFinish) {
    	initGraph(!isUnordered, youStartYouFinish);
    }

    /**
     * Constructor workhorse.  Create all fixed data structures.
     * @param isUnordered true if the top-level group should be unordered,
     *                    false if ordered
     * @param youStartYouFinish
     */
    public void initGraph(boolean isOrdered, boolean youStartYouFinish) {
		if(trace.getDebugCode("et"))
			trace.out("et", "ETG: initgraph("+isOrdered+","+youStartYouFinish+")");
    	links = new ArrayList<ExampleTracerLink>();
		nodes = new ArrayList<ExampleTracerNode>();		
		nodeMap = new LinkedHashMap<Integer, ExampleTracerNode>();				
		
		if(groupModel==null)
	    	groupModel = new DefaultGroupModel();
		if(trace.getDebugCode("et"))
			trace.printStack("et", "ETG.initGraph: groupModel "+groupModel);
		groupModel.clear();
		groupModel.setDefaultReenterable(!youStartYouFinish);
		groupModel.setGroupOrdered(groupModel.getTopLevelGroup(), isOrdered);
		groupModel.setGroupName(groupModel.getTopLevelGroup(), TOP_LEVEL);
		//do I need to declare a new one and if so I should probably call a reset...
		ExampleTracerTracer newTracer = new ExampleTracerTracer(this);
		if(exampleTracerTracer !=null){
			exampleTracerTracer.fireExampleTracerEvent(new ExampleTracerTracerChangedEvent(this, exampleTracerTracer, newTracer));
		}
		exampleTracerTracer = newTracer;
	}
	
    public ExampleTracerTracer getExampleTracer(){
    	return exampleTracerTracer;
    }
	
    public GroupModel getGroupModel() {
    	return groupModel;
    }
	
    /**
     * @return - returns an array containing the links of the graph
     */
    public ArrayList<ExampleTracerLink> getLinks() {
    	return this.links;
    }
	
    public void addLink(ExampleTracerLink link, LinkGroup groupToAddTo){
    	links.add(link);
		groupModel.addLinkToGroup(groupToAddTo, link);
    }
    /**
     * Adds the given link to the graph
     * @param link
     */
    public void addLink(ExampleTracerLink link) {
    	links.add(link);
		groupModel.addLinkToGroup(groupModel.getTopLevelGroup(), link);
    }
	
    /**
     * @return - returns the nodeMap(nodeID->node)
     */
    LinkedHashMap<Integer, ExampleTracerNode> getNodeMap() {
    	return this.nodeMap;
    }
	
    /**
     * Adds the node to the graph
     * @param node
     */
    public void addNode(ExampleTracerNode node) {
		nodes.add(node);
		nodeMap.put(new Integer(node.getNodeID()),node);
    }
	
    /**
     * Remove a node from the graph. This method undoes the work of
     * {@link #addNode(ExampleTracerNode)}. It does not update any
     * {@link ExampleTracerLink} or {@link LinkGroup} instances.
     * @param nodeID numeric id of node to delete
     * @return boolean, true if the node was actually deleted
     */
    boolean removeNode(int nodeID) {
		ExampleTracerNode node = (ExampleTracerNode) nodeMap.remove(new Integer(nodeID));
		if (node == null)
		    return false;
		for (ListIterator<ExampleTracerNode> it = nodes.listIterator(); it.hasNext(); ) {
		    if (it.next() == node) {
			it.remove();
			break;
		    }
		}
		return true;
    }
	
    /**
     * Find a node in {@link #nodeMap} given its identifier.
     * @param nodeID
     * @return {@link ExampleTracerNode} instance from map; null if not found
     */
    public ExampleTracerNode getNode(int nodeID) {
	return (ExampleTracerNode)nodeMap.get(new Integer(nodeID));
    }
	
    /**
     * Checks whether the extension observes the ordering constraints or not
     * @param traversedLinks links already matched
     * @param newLink candidate link for checking
     * @param path check ordering with respect to this path
     * @param result if not null, record result with {@link ExampleTracerEvent#setOutOfOrder(boolean)}
     * @return returns false or true depending on whether it observes 
     *         ordering Constraints or not
     */
    boolean observesOrderingConstraints (List<ExampleTracerLink> traversedLinks,
					 ExampleTracerLink newLink,
					 Set<ExampleTracerLink> path,
					 ExampleTracerEvent result) {
		
	if(path==null || path.size()==0 || newLink==null) {
	    if (result != null)
		result.setOutOfOrder(false);
	    return true;
	}
	/*
	  System.out.println(traversedLinks);
	  System.out.println(newLink);
	  System.out.println(path);
	  System.out.println(isOrderOK(traversedLinks, newLink, path));
	  System.out.println();*/
		
	if(path.contains(newLink)
	   && isOrderOK(traversedLinks, newLink, path)
	   && isReenteringOK(traversedLinks, newLink, path)) {
	    if (result != null)
		result.setOutOfOrder(false);
	    return true;
	}
	if (result != null)
	    result.setOutOfOrder(true);
	return false;		
    }
	
    private boolean isReenteringOK(List<ExampleTracerLink> traversedLinks,
				   ExampleTracerLink newLink,
				   Set<ExampleTracerLink> path) {
    	if (trace.getDebugCode("ET")) trace.out("ET", "isReenteringOK("+newLink+")");
	ArrayList<LinkGroup> groups= findGroupsOfLink(newLink);//Too slow, maybe not
		
	//Did I enter something that was exited?
	boolean entered = true;
	for(LinkGroup group : groups) {
	    //Should ignore topLevel group but doesn't
	    if(entered && !groupModel.isGroupReenterable(group)) {
		entered = false;
		for(ExampleTracerLink link : traversedLinks) {
		    if(groupModel.isLinkInGroup(group, link)) {
			entered = true;
		    } 
		    else if(entered == true)
			return false;
		}
	    }
	    else if(!entered)
		break;
	}
		
	//Did I exit something that will be reentered?
	if(traversedLinks.size()!=0) {			
	    Collection<LinkGroup> exitedGroups = findGroupsOfLink(traversedLinks.get(traversedLinks.size()-1));

	    for(LinkGroup group : groups) {
		exitedGroups.remove(group);
	    }
	    Iterator<LinkGroup> iter = exitedGroups.iterator();
	    while(iter.hasNext()) {
		LinkGroup group = iter.next();
		if(groupModel.isGroupReenterable(group))
		    iter.remove();
	    }

	    for(LinkGroup group : exitedGroups) {
		for(ExampleTracerLink link : path) {					
		    if(getTraversalCount(traversedLinks, link)<link.getEdge().getMinTraversals() 
		       && groupModel.isLinkInGroup(group, link))
			return false;
		}			
	    }
	}
	return true;
    }
	
    private boolean isOrderOK(List<ExampleTracerLink> visitedLinks,
			      ExampleTracerLink newLink,
			      Set<ExampleTracerLink> path) {
    	//want random access
    	ArrayList<LinkGroup> groups= findGroupsOfLink(newLink);//Too slow
    	if (trace.getDebugCode("ET")) trace.out("ET", "isOrderOK() newLink "+newLink+", visited "+visitedLinks+"\n in path"+path);

    	for(int i=0; i<groups.size(); i++) {
    		LinkGroup currentGroup = groups.get(i);
    		if(groupModel.isGroupOrdered(currentGroup)) {
    			if(i!=groups.size()-1) {
    				if(!checkOrderedGroup(currentGroup, getFirstLinkOnPath(groups.get(i+1), path), visitedLinks, path))
    					return false;
    			}
    			else if(!checkOrderedGroup(currentGroup, newLink, visitedLinks, path))
    				return false;
    		}								
    	}
    	return true;
    }
	
    //too long?
    private boolean checkOrderedGroup(
				      LinkGroup orderedParent, 
				      ExampleTracerLink link, 
				      List<ExampleTracerLink> traversedLinks,
				      Set<ExampleTracerLink> path) {
		
	int prevPos=-1;
	Object prev=null; //Group or link
	int nextPos=Integer.MAX_VALUE;
	Object next=null; //Group or link
		
	for(LinkGroup group : groupModel.getGroupSubgroups(orderedParent)) {
	    if(!groupModel.isLinkInGroup(group, link) && groupModel.getGroupLinkCount(group)!=0) {
		ExampleTracerLink temp = getFirstLinkOnPath(group, path);
		if(temp!=null) {
		    int groupPos = temp.getDepth();//Empty group with respect to path, null pointer exception!
		    if(groupPos<link.getDepth() && groupPos>prevPos && (!isGroupOptional(group) || isGroupStarted(group, traversedLinks))) {
			prevPos = groupPos;
			prev = group;
		    }
		    if(groupPos>link.getDepth() && groupPos<nextPos && (!isGroupOptional(group) || isGroupStarted(group, traversedLinks))) {
			nextPos = groupPos;
			next = group;
		    }
		}
	    }
	}
	//Only those on path
	for(ExampleTracerLink parentLink : groupModel.getUniqueLinks(orderedParent)) {
	    if(path.contains(parentLink)) {
		int linkPos = parentLink.getDepth();
		if(linkPos<link.getDepth() && linkPos>prevPos && (parentLink.getEdge().getMinTraversals()>0 || traversedLinks.contains(parentLink))) {
		    prevPos = linkPos;
		    prev = parentLink;
		}
		if(linkPos>link.getDepth() && linkPos<nextPos && (parentLink.getEdge().getMinTraversals()>0 || traversedLinks.contains(parentLink))) {
		    nextPos = linkPos;
		    next = parentLink;
		}
	    }
	}
	if(prev!=null) {
	    if(prev instanceof ExampleTracerLink) {
		ExampleTracerLink prevLink = (ExampleTracerLink) prev;
		if(getTraversalCount(traversedLinks, prevLink)<prevLink.getEdge().getMinTraversals())
		    return false;
	    }
	    else if(prev instanceof LinkGroup) {
		LinkGroup prevGroup = (LinkGroup) prev;
		if(!isGroupFinished(prevGroup, traversedLinks, path))
		    return false;
	    }
	}
	if(next!=null) {
	    if(next instanceof ExampleTracerLink && 
	       traversedLinks.contains((ExampleTracerLink)next))
		return false;
	    else if(next instanceof LinkGroup) {
		LinkGroup nextGroup = (LinkGroup) next;
		if(isGroupStarted(nextGroup, traversedLinks))
		    return false;
	    }
	}
	return true;
    }
	
    public boolean isGroupStarted(LinkGroup group, List<ExampleTracerLink> traversedLinks) {
	for(ExampleTracerLink link : traversedLinks) {
	    if(groupModel.isLinkInGroup(group, link))
		return true;
	}
	return false;
    }
    public boolean isGroupOptional(LinkGroup group) {
	for(ExampleTracerLink link : groupModel.getGroupLinks(group)) {
	    if(link.getEdge().getMinTraversals()!=0)
		return false;
	}
	return true;
    }
	
   /* private GroupModel getLowestLevelGroup(ExampleTracerLink link){
	Set<LinkGroup> groups = groupModel.getGroupSubgroups(groupModel.getTopLevelGroup());
	while(groups.size() >1){
	    for(LinkGroup group: groups) { 
		if(groupModel.isLinkInGroup(group, link)){
		    groups = groupModel.getGroupSubgroups(group);
		    break;
		}
			
	    }
	}
	return null;
    }*/
	
    //Nonsensical function written to satisfy test-cases :)
    //It tests for the edge case where:
    //Given: the path-sibling of the inc-link belongs to the same 
    //unordered group as the inc-link's smallest group.
    //Returns True if:
    //All the links along the path to the group have been maxed out
    //and all the links belonging in the group and the path have been maxed out.
    //Returns false if:
    //any of the links on the path leading up to the group
    //or in the group have not been maxed out.
    //Note: assumes that a group is contiguous on a path.
    //Note: Doesn't take into account whether the inclink is actually reachable
    //      Basically we only test, if the path/group is maxed, regardless of whether
    //		we traversed the links needed to reach incLink.
    private boolean isGroupMaxedOnPath(LinkGroup smallestGroup, Set<ExampleTracerLink> path, ExampleTracerInterpretation interp){
	ExampleTracerPath orderedPath = new ExampleTracerPath(path);
	Iterator<ExampleTracerLink> orderedIterator = orderedPath.iterator();
	boolean inGroup = false;
	while(orderedIterator.hasNext()){
	    ExampleTracerLink path_link = orderedIterator.next();
	    if(!inGroup){
		if(groupModel.isLinkInGroup(smallestGroup, path_link)){
		    inGroup = true;
		}
	    }
	    else{
		if(!groupModel.isLinkInGroup(smallestGroup, path_link))
		    return true;
	    }
	    if(interp.getTraversalCount(path_link) < path_link.getEdge().getMaxTraversals())
		return false;
	}
	return true;
    }
	
    /**
     * Tries to find a traversable sibling on the path, ignoring max traversals.
     * If none, return false.
     * @param traversedLinks arg to {@link #isOrderOK(List<ExampleTracerLink>,
     ExampleTracerLink, Set<ExampleTracerLink>)}
     * @param incLink the incorrect action link to check
     * @param path path in which to find a correct action link
     * @return true if finds a sibling (see above)
     */
    public boolean isIncorrectLinkOK(ArrayList<ExampleTracerLink> traversedLinks,
				     ExampleTracerLink incLink, Set<ExampleTracerLink> path,
				     ExampleTracerInterpretation interp) {
	boolean siblingOnPath = false;
	ExampleTracerLink sibLink = null;
	List<ExampleTracerLink> outLinks = getNode(incLink.getPrevNode()).getOutLinks();
    if (trace.getDebugCode("ET")) trace.out("ET", "isIncorrectLinkOK() sibling links "+outLinks
    		+"\n in path "+path+"\n in interp "+interp);
	for(ExampleTracerLink link : outLinks) {
		if (link.getType().equals(ExampleTracerTracer.INCORRECT_ACTION))
			continue;                  // sewall 2010/07/13: also avoids incLink
	    if(path.contains(link)) {
	    	siblingOnPath = true;
	    	boolean siblingOrderOk = isOrderOK(traversedLinks, link, path);
	    	if (trace.getDebugCode("ET")) trace.out("ET", "isIncorrectLinkOK() siblingOrderOk "+siblingOrderOk+" on sib "+link);
	    	if(!siblingOrderOk)
	    		return false;  // sewall 7/12/2010: continue? instead of return false
	    	sibLink = link;
	    	break;
	    }
	}
	if (sibLink == null)
		return false;
	// why only first orderOK() sibling? why not any orderOK sibling? note we discard the sibling
	if(!siblingOnPath) {
	    if (trace.getDebugCode("ET")) trace.out("ET", "isIncorrectLinkOK(): no siblings of link "+incLink+" in path "+path);
	    return false;
	}
		
	//if(!isGroupValidOnPath(incLink,))
	//	return false;
	/* 9/10/08: simplify incorrect action to just see if sibling link could be traversed;
	 *   don't check group membership of incorrect action link
	 // why go through *all* groups; why not just siblings' and parents'?
	 */
	for(LinkGroup group: groupModel) { 
		if(groupModel.isGroupOrdered(group)) {
			boolean orderedGroupIncLink =
				isOrderedGroupIncLinkOK(group, traversedLinks ,incLink, path);
		    if (trace.getDebugCode("ET")) trace.out("ET", "isIncorrectLinkOK(): orderedGroupIncLink "+orderedGroupIncLink);
			if(!orderedGroupIncLink)
				return false;
		}
	}	
	//Testing whether the path/group to which incLink would belong to is maxed or not:
	//not-maxed meaning that there is either a member of the group that isn't maxed along 
	//the path or that an ancestor along the path to the group isn't maxed.
	if(siblingOnPath){
	    //ArrayList<LinkGroup> groups =  findGroupsOfLink(incLink);
	    LinkGroup incSmallestGroup = getSmallestContainingGroup(incLink);
	    if(groupModel.isLinkInGroup(incSmallestGroup, sibLink) && (!groupModel.isGroupOrdered(incSmallestGroup))){
		if(isGroupMaxedOnPath(incSmallestGroup, path, interp))
		    return false;
	    }
	}
	return true;
    }
	
    /**
     * Check whether the ordering of an incorrect link is ok. Returns false if<ul>
     * <li>any subgroup has a first link on this path and that first link is a sibling and the group is finished;</li>
     * <li>any sibling member of this group has been traversed and can't be traversed again;</li>
     * <li>the parent link on this path is in the group (but not the smallest group(?)) and is not traversed.</li>
     * </ul>
     * @param group ordered group
     * @param traversedLinks links traversed (in this interpretation?)
     * @param incLink incorrect link to check
     * @param path path we're checking
     * @return true if the incorrect link is in order with respect to these checks
     */
    private boolean isOrderedGroupIncLinkOK(LinkGroup group,
					    ArrayList<ExampleTracerLink> traversedLinks,
					    ExampleTracerLink incLink, Set<ExampleTracerLink> path) {
    	if (trace.getDebugCode("ET")) trace.out("ET", "isOrderedGroupIncLinkOK(): group "+group+
		  ", incLink "+incLink+", path "+path);
	for(LinkGroup subgroup : groupModel.getGroupSubgroups(group)) {
	    ExampleTracerLink link = getFirstLinkOnPath(subgroup, path); // getFirst..(subgroup,..)?
	    if(link!=null) {
		if(link.getPrevNode()==incLink.getPrevNode()) {
			boolean subgroupFinished = isGroupFinished(subgroup, traversedLinks, path);
			if (trace.getDebugCode("ET")) trace.out("ET", "isOrderedGroupIncLinkOK(): subgroupFinished "+subgroupFinished+
					" in subgroup "+subgroup);
		    if(subgroupFinished)
			return false;
		}
		// why fail if *any* other group on path is finished				
		// else if(isGroupFinished(subgroup, traversedLinks, path)) {
		//	return false;  
		// why relevant? because link is first on this path? 
		//				}
	    }
	}
	if (trace.getDebugCode("ET")) trace.out("ET", "incLink "+incLink+" ok for isGroupFinished()");
	for(ExampleTracerLink subLink : groupModel.getUniqueLinks(group)) {
	    // if any? sibling link is finished, rtn false
	    if(subLink.getPrevNode() == incLink.getPrevNode()) {   
		if(getTraversalCount(traversedLinks, subLink)>=subLink.getEdge().getMaxTraversals()) // was MinTr
		    return false;
	    }
	    // if parent link on this path untraversed, rtn false
	    else if(subLink.getNextNode() == incLink.getPrevNode()
		    && path.contains(subLink)) {  
		//List<LinkGroup> groups = findGroupsOfLink(subLink);
		// care if group ordered?
		LinkGroup smallestContainingGroup = getSmallestContainingGroup(subLink);
		// why ok if parent & incLink in scg?
		if(!groupModel.isLinkInGroup(smallestContainingGroup, incLink))  
		    if(getTraversalCount(traversedLinks, subLink)<subLink.getEdge().getMinTraversals())
			return false;
	    }
	}
	if (trace.getDebugCode("ET")) trace.out("ET", "isOrderedGroupIncLinkOK() returns true");
	return true;
    }
	
    private ExampleTracerLink getFirstLinkOnPath(LinkGroup group, Set<ExampleTracerLink> path) {
	int minDepth = Integer.MAX_VALUE;
	ExampleTracerLink firstLink=null;;
	for(ExampleTracerLink link : groupModel.getGroupLinks(group)) {
	    if(link.getDepth()<minDepth && path.contains(link)) {
		minDepth = link.getDepth();
		firstLink = link;
	    }
	}
	return firstLink;
    }
    public boolean isGroupFinished(LinkGroup group, 
				   List<ExampleTracerLink> traversedLinks, Set<ExampleTracerLink> path) {
	for(ExampleTracerLink link : groupModel.getGroupLinks(group)) {
	    if(path.contains(link)) {
		if(getTraversalCount(traversedLinks, link)<link.getEdge().getMinTraversals())
		    return false;
	    }
	}
	return true;
    }
	
    private int getTraversalCount(List<ExampleTracerLink> traversedLinks, ExampleTracerLink target) {
	int traversals = 0;
	for(ExampleTracerLink link : traversedLinks) {
	    if(target.equals(link))
		traversals++;
	}
	return traversals;
    }	
    /**
     * A link's depth is defined as follows.  A link's depth is
     * equal to the maximum of all preceding link's depths plus
     * 1.  A given link's preceding links are the set of links whose
     * next node is equal to the given link's previous node.  If
     * a link has no preciding links, it's depth is 0. 
     */
    public void redoLinkDepths() {
	buildInLinks();
	for(ExampleTracerLink link : getLinks())
	    link.setDepth(-1);
	for(ExampleTracerLink link : getLinks()) {
	    if(link.getDepth()==-1)
		redoLinkDepths(link);
	}
    }
    private void redoLinkDepths(ExampleTracerLink link) {
		ExampleTracerNode prevNode = getNode(link.getPrevNode());
		//this shouldn't be true but now that CTAT supports
		//graph rewiring and disconnected graphs, you never know :)
		if(prevNode==null)
			return;
		int max = -1;
		for(ExampleTracerLink prevLink : prevNode.getInLinks()) {
		    if(prevLink.getDepth()==-1)
			redoLinkDepths(prevLink);
		    if(prevLink.getDepth()>max)
			max = prevLink.getDepth();
		}
		link.setDepth(max+1);
    }
	
    /**
     * @return - returns a list containing all the paths in the given graph
     */
    public Set<ExampleTracerPath> findAllPaths() {
	return findPathsFromNode(getStartNode());
    }
	
    public boolean isNodeConnected(int nodeId){
//    	ExampleTracerNode reachMe = nodes.get(nodeId);
    	ExampleTracerNode reachMe = nodeMap.get(nodeId);
    	Set<ExampleTracerNode> reachables =  new HashSet<ExampleTracerNode>();
    	if(reachMe ==null)
    		return false;
    	getNodesReachableFrom(getStartNode(), reachables);
    	return reachables.contains(reachMe);
    }
    public void getNodesReachableIgnoringX(ExampleTracerNode fromNode, ExampleTracerNode xNode, Set<ExampleTracerNode> reachables){
    	ArrayList<ExampleTracerLink> outLinks = fromNode.getOutLinks();
    	int i;
    	ExampleTracerNode curr;
    	for(i = 0; i < outLinks.size(); i++){
    		curr = nodeMap.get(outLinks.get(i).getNextNode());
    		if(curr==null || curr == xNode)
    			continue;
    		boolean added = reachables.add(curr);
    		//if(added)
    		//	trace.out("borg", curr.getProblemNode().getName());
    		getNodesReachableIgnoringX(curr, xNode, reachables);
    	}
    }
    
    /* Sets the argument set 'reachables' to contain all of the
     * nodes reachable from fromNode. No repeats of course
     */
    public void getNodesReachableFrom(ExampleTracerNode fromNode, Set<ExampleTracerNode> reachables){
    	ArrayList<ExampleTracerLink> outLinks = fromNode.getOutLinks();
    	int i;
    	ExampleTracerNode curr;
    	for(i = 0; i < outLinks.size(); i++){
    		curr = nodeMap.get(outLinks.get(i).getNextNode());
    		if(curr==null)
    			continue;
    		boolean added = reachables.add(curr);
    		//if(added)
    		//	trace.out("borg", curr.getProblemNode().getName());
    		getNodesReachableFrom(curr, reachables);
    	}
    }
    /**
     * Finds the paths starting from a given node
     * @param nodeID
     * @param path
     * @param allPaths
     */
    public Set<ExampleTracerPath> findPathsFromNode (ExampleTracerNode node) {
	Set<ExampleTracerPath> paths = new HashSet<ExampleTracerPath>();

	if(node==null || node.getOutLinks().size()==0) {
	    paths.add(new ExampleTracerPath());
	    return paths;
	}
		
	for(ExampleTracerLink outLink : node.getOutLinks()) {
	    if(!outLink.getType().equals(ExampleTracerTracer.INCORRECT_ACTION)) {			
	    	Set<ExampleTracerPath> childPaths = findPathsFromNode(getNode(outLink.getNextNode()));
	    	for(ExampleTracerPath childPath : childPaths) {
	    		childPath.addLink(outLink);
	    	}
	    	paths.addAll(childPaths);
	    }
	}
	if(paths.size()==0)
	    paths.add(new ExampleTracerPath());
	return paths;
    }
	
    public LinkGroup getSmallestContainingGroup(ExampleTracerLink link){
    	return ((DefaultGroupModel)groupModel).getLowestLevelGroupOfLink(link);
    }
    /**
     * Returns all the groups containing the given link in the nested order.
     * @param graph
     * @param link
     * @return
     */
    public ArrayList<LinkGroup> findGroupsOfLink(ExampleTracerLink link) {
    //	groupModel.getg
	Set<LinkGroup> groupsSet = groupModel.getGroupsContainingLink(link);
	ArrayList<LinkGroup> groups = new ArrayList<LinkGroup>(groupsSet);
	Collections.sort(groups, new LinkGroupSizeComparator());
	return groups;
    }
	
    /**
     * Find a link by id. This method is linear in the number of links.
     * @param id
     * @return element from {@link #getLinks()} with matching id; null if none
     */
    ExampleTracerLink findLinkByID(int id) {
	for (Iterator it = getLinks().iterator(); it.hasNext(); ) {
	    ExampleTracerLink link = (ExampleTracerLink) it.next();
	    if (link.getID() == id)
		return link;
	}
	return null;
    }

    /**
     * Handle problem model events as needed.  As with others this
     * will recursively handle subevents.  
     **/
    private boolean resetFlag;
    private boolean extendPathsFlag;
    public void problemModelEventOccurred(ProblemModelEvent event) {
    	handleProblemModelEvent(event);
    }
    public boolean handleProblemModelEvent(ProblemModelEvent event) {	
    	resetFlag = false;
    	extendPathsFlag = false;
    	//kurwa
    	//boolean demonstrateMode = exampleTracerTracer.isDemonstrateMode();
    	handlePMEventRecursive(event);
    	if(resetFlag){
    		exampleTracerTracer.resetTracer();
    		redoLinkDepths();
    	}else if (extendPathsFlag){
    		redoLinkDepths();
    		if(exampleTracerTracer.isDemonstrateMode())
    			exampleTracerTracer.extendPaths();
    		else
    			return true;
    	}
    	return resetFlag;
    }
    
    private void handlePMEventRecursive(ProblemModelEvent event){
    	if (trace.getDebugCode("br")) trace.out ("br", "problem model event: " + event);
		//trace.outPlain("borg", "EGT PMEvent : " + event.getClass().toString());
		//System.out.flush();
    	if(event instanceof BRDLoadedEvent){
	    	ArrayList<ProblemModelEvent> nodeCreatedEvents = new ArrayList<ProblemModelEvent> (event.collectTypeSubevents(NodeCreatedEvent.class, true, true, true));
	    	handleLoadedBRDNodes(nodeCreatedEvents);
	    	ArrayList<ProblemModelEvent> edgeCreatedEvents = new ArrayList<ProblemModelEvent> (event.collectTypeSubevents(EdgeCreatedEvent.class, true, true, true));
	    	handleLoadedBRDEdges(edgeCreatedEvents);
	    	redoLinkDepths();
	    	return;
	    }
        if (event instanceof NodeCreatedEvent) {
        	extendPathsFlag = true;
            handleNodeCreatedEvent((NodeCreatedEvent) event);
          //  resetFlag = true;
        }
        
        if (event instanceof NodeDeletedEvent) {
        	resetFlag =  (handleNodeDeletedEvent ((NodeDeletedEvent) event)) || resetFlag;
        }

        if (event instanceof NewProblemEvent) {
        	//redoLinkDepths = true;
            handleNewProblemEvent((NewProblemEvent)(event));
        }
        if (event instanceof EdgeRewiredEvent){
        	EdgeRewiredEvent ere = (EdgeRewiredEvent)event;
        	resetFlag = extendPathsFlag = true;
        	handleEdgeDeletedEvent (ere.getEdgeDeletedEvent());
        	handleEdgeCreatedEvent (ere.getEdgeCreatedEvent());
        }
        if (event instanceof EdgeCreatedEvent) {
        	extendPathsFlag = true;
        	handleEdgeCreatedEvent ((EdgeCreatedEvent) event);
        }
        
        if (event instanceof EdgeUpdatedEvent) {
        	handleEdgeUpdatedEvent ((EdgeUpdatedEvent) event);
        }
        
        if (event instanceof EdgeDeletedEvent) {
        	resetFlag = (handleEdgeDeletedEvent ((EdgeDeletedEvent) event)) || resetFlag;
        }
        /* Handle a compound event by recursively calling the 
         * command on all subevents. */
        if (event.isCompoundEventP()) {
        	for (ProblemModelEvent E : event.getSubevents()) {
        		this.handlePMEventRecursive(E);
        	}
        }
    }


	
	/**
     * Delete the link (w/ subgraph) with the given id. This method assumes an acyclic graph.
     * @param id unique identifier of the link
     * @return true if the link was found and deleted; false if not found
     */
  /*  boolean deleteLink(int id) {
	ExampleTracerLink link = findLinkByID(id);
	if (link == null)
	    return false;
	buildInLinks();
	boolean result = deleteLinkSubGraph(link);
	ExampleTracerNode source = getNode(link.getPrevNode());
	for (ListIterator it = source.getOutLinks().listIterator(); it.hasNext(); ) {
	    if (it.next() == link) {
		it.remove();
		break;
	    }
	}
	return result;
    }*/
	
    /**
     * Clear all {@link ExampleTracerNode} inLinks and recalculate them.
     */
    private void buildInLinks() {
	for (Iterator it = nodes.iterator(); it.hasNext(); )
	    ((ExampleTracerNode) it.next()).clearInLinks();
	ExampleTracerNode startNode = getStartNode();
	if(startNode==null || startNode.getOutLinks()==null)
	    return;
	for (Iterator it = startNode.getOutLinks().iterator(); it.hasNext(); )
	    updateInLinkSubGraph((ExampleTracerLink) it.next());
    }

    /**
     * Set the {@link ExampleTracerNode} inLinks in the subgraph starting
     * at the given link's destination node.
     * @param link
     */
    private void updateInLinkSubGraph(ExampleTracerLink link) {
	ExampleTracerNode dest = getNode(link.getNextNode());
	if (dest == null)
	    return;
	dest.addInLink(link);
	for (Iterator it = dest.getOutLinks().iterator(); it.hasNext(); )
	    updateInLinkSubGraph((ExampleTracerLink) it.next());
    }
	
    /**
     * Remove the subgraph that would be cut off if the given link is deleted.
     * Then remove the link itself. Must call {@link #buildInLinks()} before
     * calling this recursive method.
     * @param link link to remove
     * @return true if anything was deleted
     */
 /*   private boolean deleteLinkSubGraph(ExampleTracerLink link) {
		ExampleTracerNode dest = getNode(link.getNextNode());
		if (dest == null)
		    return false;
		if (dest.getInLinks().size() == 1 && dest.containsInLink(link)) {
		    for (Iterator it = dest.getOutLinks().iterator(); it.hasNext(); )
			deleteLinkSubGraph((ExampleTracerLink) it.next());
		    removeNode(dest.getNodeID());
		}
		removeLink(link);
		return true;
    }*/
	
    /**
     * Remove the given link from groups and from those permanent
     * data structures that refer to it directly. Updates all groups
     * having this link. 
     * @param link link to remove
     */
    private void removeLink(ExampleTracerLink link, boolean updateGroups) {
    	if(updateGroups)
    		groupModel.removeLinkFromModel(link);
    	else
    		groupModel.removeLinkFromGroup(groupModel.getTopLevelGroup(), link);
    	links.remove(link);
    }

	
    private void handleEdgeUpdatedEvent(EdgeUpdatedEvent event) {
	if (trace.getDebugCode("et")) trace.outNT("et", "ExampleTracerGraph.handleEdgeUpdatedEvent("+event+")");
		
	//int sourceID = event.getEdge().source.getUniqueID();
	//int targetID = event.getEdge().dest.getUniqueID();
	//ExampleTracerLink link = new ExampleTracerLink(event.getEdge().getEdgeData(),
	//		sourceID, targetID);
	//addLink(link);
	//ExampleTracerNode source = getNode(sourceID);
	//ExampleTracerNode dest = getNode(targetID);
	//source.addOutLink(link);	
	//dest.addInLink(link);
    }

    private void handleEdgeCreatedEvent(EdgeCreatedEvent event) {
		int sourceID = event.getEdge().source.getUniqueID();
		int targetID = event.getEdge().dest.getUniqueID();
		LinkGroup groupToAddTo = event.getGroupToAddTo();
		if(groupToAddTo==null)
			groupToAddTo = groupModel.getTopLevelGroup();
		ExampleTracerLink link = new ExampleTracerLink(event.getEdge().getEdgeData(),
							       sourceID, targetID);
		addLink(link, groupToAddTo);
		ExampleTracerNode source = getNode(sourceID);
		ExampleTracerNode dest = getNode(targetID);
		source.addOutLink(link);	
		dest.addInLink(link);		
    }
    private void handleLoadedBRDEdges(ArrayList<ProblemModelEvent> edgeCreatedEvents){
    	int i;
    	//ArrayList<ExampleTracerLink> linksForGroup;
    	for(i = 0; i < edgeCreatedEvents.size(); i++){
    		ProblemEdge edge = ((EdgeCreatedEvent)edgeCreatedEvents.get(i)).getEdge();
    		int sourceID = edge.source.getUniqueID();
    		int targetID = edge.dest.getUniqueID();
    		ExampleTracerLink link = new ExampleTracerLink(edge.getEdgeData(),
    							       sourceID, targetID);
    		links.add(link);
    		ExampleTracerNode source = getNode(sourceID);
    		ExampleTracerNode dest = getNode(targetID);
    		source.addOutLink(link);	
    		dest.addInLink(link);
    	}
    	groupModel.addLinksForLoadingBRD(links);
    }
    



    /**
     * Process an {@link EdgeDeletedEvent} from the author interface.
     * 
     * NOTE: This function ends up calling a function that RECURSIVELY
     * deletes the subgraph!
     * @param event includes reference to edge
     * @return whether or not an edge was actually was deleted
     */
    private boolean handleEdgeDeletedEvent(EdgeDeletedEvent event) {
    	if (trace.getDebugCode("et")) trace.outNT("et", "ExampleTracerGraph.handleSingleEdgeDeletedEvent("+event+")");
    	if (event.getEdge() == null)
    	    return false;
    	// Be sure to avoid failure if edge already deleted.
    	EdgeData edgeData = event.getEdge().getEdgeData();
    	if (edgeData == null)
    	    return false;
    	int id = edgeData.getUniqueID();
    	ExampleTracerLink link = findLinkByID(id);
    	if (link == null)
    	    return false;
    	//buildInLinks();
    	//boolean result = deleteLinkSubGraph(link);
    	removeLink(link, true);
    	ExampleTracerNode source = getNode(link.getPrevNode());
    	if(source!=null){
	    	for (ListIterator it = source.getOutLinks().listIterator(); it.hasNext(); ) {
	    	    if (it.next() == link) {
	    		it.remove();
	    		break;
	    	    }
	    	}
    	}
    	ExampleTracerNode dest = getNode(link.getNextNode());
    	if(dest!=null){
	    	dest.getInLinks().remove(link);
    	}
    	return true;
    }
	
    /**
     * Respond to the new-problem signal by calling {@link #initGraph(boolean, boolean)}.
     */
    private void handleNewProblemEvent(NewProblemEvent event) {
    	boolean isOrdered = !event.isUnordered();
    	initGraph(isOrdered, false);
    }
    
    private boolean handleNodeDeletedEvent(NodeDeletedEvent event) {
    	return removeNode(event.getNode().getUniqueID());
    }
    
    private void handleLoadedBRDNodes(ArrayList<ProblemModelEvent> nodeCreatedEvents){
    	int i;
    	if (trace.getDebugCode("br")) trace.out("br", "Loading the nodes of a BRD");
    	for(i = 0; i < nodeCreatedEvents.size(); i++){
    		ExampleTracerNode node = new ExampleTracerNode(((NodeCreatedEvent)(nodeCreatedEvents.get(i))).getNode());
    		addNode(node);
    		if (trace.getDebugCode("br")) trace.out("br", "NodeID: "+node.getNodeID());
    	}
    	if (trace.getDebugCode("br")) trace.out("br", "End Loading nodes of a BRD");
    }
    private void handleNodeCreatedEvent(NodeCreatedEvent event) {
		ExampleTracerNode node = new ExampleTracerNode(event.getNode());
		addNode(node);
		if (trace.getDebugCode("br")) trace.out("br", "NodeID: "+node.getNodeID());
    }

    /**
     * Get the node with nodeID 1.
     * FIXME shouldn't rely on it being id 1.
     * @return result of {@link #getNode(int) getNode(1)}
     */
    public ExampleTracerNode getStartNode() {
	return getNode(1);
    }
	
    public ExampleTracerLink getLink(ProblemEdge edge)
    {
	for(ExampleTracerLink link : links)
	    {
		if(link.getEdge().getEdge().equals(edge))
		    return link;
	    }
	return null;
    }

    /**
     * Return the link object for a given link id.
     * @param linkID
     * @return link object from {@link #links}; null if none matches
     */
    public ExampleTracerLink getLink(int linkID) {
	for (Iterator it = links.iterator(); it.hasNext(); ) {
	    ExampleTracerLink link = (ExampleTracerLink) it.next();
	    if (link.getID() == linkID)
		return link;
	}
	return null;
    }
	
    /**
     * Compares groups such that x<y <=> x has more links than y
     * @author Eric Schwelm
     *
     */
    class LinkGroupSizeComparator implements Comparator<LinkGroup> {
	public int compare(LinkGroup arg0, LinkGroup arg1) {
	    return groupModel.getGroupLinkCount(arg1) - groupModel.getGroupLinkCount(arg0); 
	}
		
    }

    /**
     * Find the best path from the given source node to the given destination node. 
     * @param fromNodeID
     * @param toNodeID
     * @return
     */
	public ExampleTracerPath getBestSubpath(int fromNodeID, int toNodeID) {
		ExampleTracerNode fromNode = getNode(fromNodeID);
		ExampleTracerNode toNode = getNode(toNodeID);
		if (fromNode == null || toNode == null)
			return null;
		Set<ExampleTracerPath> pathsFromNode = findPathsFromNode(fromNode);
		Set<ExampleTracerPath> pathsFromNodeToNode = new HashSet<ExampleTracerPath>();
		for (ExampleTracerPath path : pathsFromNode) {
			ExampleTracerPath subpath = path.subpath(toNodeID);
			if (subpath != null)
				pathsFromNodeToNode.add(subpath);
		}
		if (pathsFromNodeToNode.size() < 1)
			return null;
		else
			return ExampleTracerPath.getBestPath(pathsFromNodeToNode);
	}
}
