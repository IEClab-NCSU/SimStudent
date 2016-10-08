package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.cmu.pact.Utilities.trace;

public class ExampleTracerPath implements Iterable<ExampleTracerLink> {	
	public static class ExampleTracerPathComparator implements Comparator<ExampleTracerPath> {
	    
		//x<y iff x is a better path than y
	    public int compare (ExampleTracerPath p1, ExampleTracerPath p2) {
	    	if (trace.getDebugCode("et"))
	    		trace.out("et", "comparing paths("+p1+","+p2+")");
	    	if(p1.isDonePath() && !p2.isDonePath())
	    		return -1;
	    	else if(p2.isDonePath() && !p1.isDonePath()) // done path
	    		return 1;
	    	if(p2.isIncorrectPath() && !p1.isIncorrectPath())
	    		return -1;
	    	else if(p1.isIncorrectPath() && !p2.isIncorrectPath()) // not incorrect
	    		return 1;
	    	int i1 = p1.getNumberOfPreferredPrefixLinks();
	    	int i2 = p2.getNumberOfPreferredPrefixLinks();
	    	if (i1>i2)
	    		return -1;   // longer initial sequence of preferred links
	    	else if (i1<i2)
	    		return 1;
	    	i1 = p1.getNumberOfSuboptimalLinks();
	    	i2 = p2.getNumberOfSuboptimalLinks();
	    	if (i1<i2)
	    		return -1;
	    	else if (i1>i2)  // fewer suboptimal links
	    		return 1;
	    	i1 = p1.getNumberOfPreferredLinks();
	    	i2 = p2.getNumberOfPreferredLinks();
	    	if (i1>i2)
	    		return -1;   // longer total count of preferred links
	    	else if (i1<i2)
	    		return 1;
	    	i1 = p1.getLinks().size();
	    	i2 = p2.getLinks().size();
	    	if (i1<i2)
	    		return -1;   // shorter path
	    	else if (i1>i2)
	    		return 1;	    	
	    	if (trace.getDebugCode("et"))
	    		trace.out("et", "About to call breakByLowerLinkID("+p1+","+p2+")");
	    	return breakByLowerLinkID(p1,p2);
	    }
	    
	    /**
	     * Compare 2 paths by linkID on the highest link not shared by the paths. 
	     * @param i1 left-hand operand of <
	     * @param i2 right-hand operand of <
	     * @return -1 if i1 < i2; else 1; will not return 0
	     */
	    private int breakByLowerLinkID(ExampleTracerPath i1, ExampleTracerPath i2) {
	    	Iterator<ExampleTracerLink> links1 = i1.iterator();
	    	Iterator<ExampleTracerLink> links2 = i2.iterator();
	    	ExampleTracerLink link1 = null;
			ExampleTracerLink link2 = null;

			int result = 0;
			do {
				if(!links1.hasNext())    // path 1 is shorter (both end together only if identical)
					result = -1;
				else if(!links2.hasNext())                                    // path 2 is shorter
					result = 1;
				else {                                             // get the links to compare IDs
					link1 = links1.next();
					link2 = links2.next();
				}
			} while(result == 0 && link1.getUniqueID() == link2.getUniqueID());
			if(result == 0)
				result = (link1.getUniqueID() < link2.getUniqueID() ? -1 : 1);
			
			if(trace.getDebugCode("et"))
				trace.outNT("et", "breakByLowerLinkID result "+result+", i1 size "+
	    				i1.getLinks().size()+", i2 size "+i2.getLinks().size());
			return result;
	    }
	} 

	
	/**
	 * The links in the path, not ordered.
	 */
	private Set<ExampleTracerLink> links;
	
	/** Sorted links. */
	private ArrayList<ExampleTracerLink> sortedLinks = null;
	
	/** The length of the initial subpath consisting only of preferred links. */
	private Integer numberOfPreferredPrefixLinks = null;
	
	/** The total count of preferred links anywhere in the path. */
	private Integer numberOfPreferredLinks = null;
	
	/** Number of {@link ExampleTracerTracer#SUBOPTIMAL_ACTION} links in the path. */
	private Integer numberOfSuboptimalLinks = null;
	
	/** Whether this path leads to a Done state. */
	private Boolean donePath = null;
	
	/** Whether this path includes an {@link ExampleTracerTracer.INCORRECT_ACTION} link. */
	private Boolean incorrectPath = null;

	public ExampleTracerPath() {
		this(null);
	}	
	
	ExampleTracerPath(Set<ExampleTracerLink> links) {
		if(links==null)
			this.links = new HashSet<ExampleTracerLink>();
		else
			this.links = new HashSet<ExampleTracerLink>(links);
		sortedLinks = null;
		numberOfPreferredPrefixLinks = null;
		numberOfPreferredLinks = null;
		numberOfSuboptimalLinks = null;
		donePath = null;
		incorrectPath = null;
	}
	
	public int getNumberOfPreferredPrefixLinks() {
		if (numberOfPreferredPrefixLinks != null)
			return numberOfPreferredPrefixLinks.intValue();
		int prefs=0;
		for(ExampleTracerLink link : this) {
			if(link.getEdge().isPreferredEdge())
				prefs++;
			else
				break;
		}
		numberOfPreferredPrefixLinks = new Integer(prefs);
		return prefs;
	}

	/**
	 * Count the number of {@link #getLinks()} entries whose 
	 *         {@link ExampleTracerLink#getEdge()} satisfies
	 *         {@link EdgeData#isPreferredEdge()}
	 * @return count 
	 */
	public int getNumberOfPreferredLinks() {
		if (numberOfPreferredLinks != null)
			return numberOfPreferredLinks.intValue();
		int count = 0;
		for(ExampleTracerLink link : getLinks()) {
			if(link.getEdge().isPreferredEdge())
				count++;
		}
		numberOfPreferredLinks = new Integer(count);
		return count;
	}
	
	public boolean isIncorrectPath() {
		if (incorrectPath != null)
			return incorrectPath.booleanValue();
		for(ExampleTracerLink link : getLinks()) {
			if(link.getType().equals(ExampleTracerTracer.INCORRECT_ACTION)) {
				incorrectPath = Boolean.TRUE;
				return true;
			}
		}
		incorrectPath = Boolean.FALSE;
		return false;
	}
	
	public boolean isDonePath() {
		if (donePath != null)
			return donePath.booleanValue();
		for(ExampleTracerLink link : getLinks()) {
			if(link.getEdge().isDone()) {
				donePath = Boolean.TRUE;
				return true;
			}
		}
		donePath = Boolean.FALSE;
		return false;
	}
	
	/**
	 * Count the number of {@link #getLinks()} entries whose 
	 *         {@link ExampleTracerLink#getType()} matches
	 *         {@link ExampleTracerTracer#SUBOPTIMAL_ACTION}
	 * @return count 
	 */
	public int getNumberOfSuboptimalLinks() {
		if (numberOfSuboptimalLinks != null)
			return numberOfSuboptimalLinks.intValue();
		int result = 0;
		for (Iterator it = getLinks().iterator(); it.hasNext(); ) {
			ExampleTracerLink link = (ExampleTracerLink) it.next();
			if (ExampleTracerTracer.SUBOPTIMAL_ACTION.equals(link.getType()))
				result++;
		}
		numberOfSuboptimalLinks = new Integer(result);
		return result;
	}
	
	/**
	 * @return - returns the list of links
	 */
	public Set<ExampleTracerLink> getLinks() {
		return this.links;
	}
	
	/**
	 * Create a subpath that ends at the first link on this path having the given node
	 * as its destination node.
	 * @param destNodeID
	 * @return subpath to given destNode; null if none
	 */
	public ExampleTracerPath subpath(int destNodeID) {
		ExampleTracerPath result = new ExampleTracerPath();
		Iterator<ExampleTracerLink> it = iterator();
		while (it.hasNext()) {
			ExampleTracerLink link = it.next();
			result.addLink(link);
			if (link.getNextNode() == destNodeID)
				return result;
		}
		return null;
	}
	
	/**
	 * Dump for debugging. Shows {@link #group}, {@link #subSegments}.
	 * @return group, subSegments
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("ExampleTracerPath: links");
		for (ExampleTracerLink link : this)
			sb.append(" ").append(link);
		return sb.toString();
	}

	public ExampleTracerLink getLastLink() {
		ExampleTracerLink curr = null;
		for(ExampleTracerLink link: links) {
			if(curr==null || curr.getDepth()<link.getDepth())
				curr = link;
		}
		return curr; 
	}
	
	/**
	 * Number of links in the path.
	 * @return {@link #links}.{@link Set#size()}
	 */
	public int size() {
		return links.size();
	}

	/**
	 * If we only want the path up to a certain point.  Take the
	 * deepest link from the set given and return the path from
	 * there up.
	 * @param matchedLinks
	 * @return
	 */
	public Set<ExampleTracerLink> getLinksRestricted(
			ArrayList<ExampleTracerLink> matchedLinks) {
		ExampleTracerLink deepest = getDeepestLink(matchedLinks);
		Set<ExampleTracerLink> restrictedLinks = new HashSet<ExampleTracerLink>();
		for(ExampleTracerLink link: links) {
			if(link.getDepth()<=deepest.getDepth())
				restrictedLinks.add(link);
		}
		return restrictedLinks;
	}
	
	public static ExampleTracerLink getDeepestLink(Collection<ExampleTracerLink> c) {
		ExampleTracerLink curr = null;
		for(ExampleTracerLink link : c) {
			if(curr==null || curr.getDepth()<link.getDepth())
				curr = link;
		}
		return curr;
	}

	public void addLink(ExampleTracerLink exampleTracerLink) {
		links.add(exampleTracerLink);
		sortedLinks = null;
		numberOfPreferredPrefixLinks = null;  // recalculate on next get()
		numberOfPreferredLinks = null;
		numberOfSuboptimalLinks = null;
		donePath = null;
		incorrectPath = null;
	}
	
	public static ExampleTracerPath getBestPath(Set<ExampleTracerPath> paths) {
		ExampleTracerPath bestPath = null;
		ExampleTracerPath.ExampleTracerPathComparator comp = new ExampleTracerPath.ExampleTracerPathComparator();
		for(ExampleTracerPath path : paths) {
			if(bestPath == null || comp.compare(bestPath, path)>0) {
				bestPath = path;
			}			
		}
		return bestPath;
	}
	
	/**
	 * Custom iterator returns links in chain order.  Disables {@link Iterator#remove()}.
	 * @return new {@link ExampleTracerPath.PathIterator} instance
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<ExampleTracerLink> iterator() {
		PathIterator result = new PathIterator(sortedLinks);  // reuse sortedLinks if still current
		sortedLinks = result.sortedLinks;
		return result;
	}
	
	/**
	 * Custom iterator returns links in chain order.  Disables {@link Iterator#remove()}.
	 */
	private class PathIterator implements Iterator<ExampleTracerLink>{
		private ArrayList<ExampleTracerLink> sortedLinks;
		int i=0;
		public PathIterator(ArrayList<ExampleTracerLink> sortedLinks){
			if (sortedLinks != null)
				this.sortedLinks = sortedLinks;
			else {
				this.sortedLinks = new ArrayList<ExampleTracerLink>(getLinks());
				Collections.sort(this.sortedLinks, new ExampleTracerLink.LinkDepthComparator());
			}
		}
		public boolean hasNext() {
			return i<sortedLinks.size();
		}

		public ExampleTracerLink next() {
			return sortedLinks.get(i++);
		}

		public void remove(){
			throw new UnsupportedOperationException("Remove not supported in ExampleTracerPath iterator");
		}
		
	}
}