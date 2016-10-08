package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.Utilities.trace;

public class ExampleTracerInterpretation implements Cloneable {
	
	/** Links traversed, in the order traversed--including duplicates if traversed more than once. */
	private ArrayList<ExampleTracerLink> traversedLinks;
	
	/**
	 * A map <LinkID->traversalCount> to support fast implementations of {@link #isVisited(int)},
	 * {@link #getTraversalCount(ExampleTracerLink)}.
	 */
	private Map<Integer, int[]> linkIdTraversalCountMap;
	
	private VariableTable vt;
	/** Paths valid with the traversedLinks so far. */
	private Set<ExampleTracerPath> validPaths;	
	
	/** The type of the least-good link in {@link #traversedLinks}. See {@link #getType()}. */
	private String worstLinkType = null;
	
	ExampleTracerInterpretation(Set<ExampleTracerPath> validPaths) {
		traversedLinks = new ArrayList<ExampleTracerLink>();
		linkIdTraversalCountMap = new HashMap<Integer, int[]>();
		this.worstLinkType = ExampleTracerTracer.CORRECT_ACTION; // ok until worse added
		this.validPaths = new HashSet<ExampleTracerPath>();
		this.validPaths.addAll(validPaths);
		vt = new VariableTable();
	}
	
	/**
	 * Set the student SAI variables (linkN.selection, linkN.action, etc.) for this link.
	 * Also for correct or suboptimal links, set the selection variable.
	 * @param student_sai
	 * @param replacementInput if not null, use this value instead of
	 *        {@link ExampleTracerSAI#getInputAsString() student_sai.getInputAsString()}
	 * @param link
	 */
	public void updateVariableTable(ExampleTracerSAI student_sai, Vector replacementInput,
			ExampleTracerLink link){
		String inputStr = student_sai.getInputAsString();
		if (replacementInput != null && replacementInput.size() > 0)
			inputStr = (String) replacementInput.get(0);
		vt.put(nameLink(link, "selection"), student_sai.getSelectionAsString());
		vt.put(nameLink(link, "action"), student_sai.getActionAsString());
		vt.put(nameLink(link, "input"), inputStr);
		EdgeData edgeData = link.getEdge();
		if (trace.getDebugCode("ET")) trace.out("ET", "interp "+this+".updateVT("+student_sai+","+link+"): "+edgeData.getActionType());
		if (EdgeData.CORRECT_ACTION.equalsIgnoreCase(edgeData.getActionType()) ||
				EdgeData.FIREABLE_BUGGY_ACTION.equalsIgnoreCase(edgeData.getActionType())) {
			String sel = student_sai.getSelectionAsString();
			if (sel != null && sel.length() > 0)
				vt.put(sel, inputStr);
		}
	}

	/**
	 * Generate the variable name for a link variable.
	 * @param link
	 * @param s suffix ("selection", "action", e.g.)
	 * @return "link" + linkID + "." + s
	 */
	private String nameLink(ExampleTracerLink link, String s) {
		return "link" + link.getUniqueID() + "." + s;
	}
	
	public ExampleTracerInterpretation clone() {
		ExampleTracerInterpretation interp = new ExampleTracerInterpretation(validPaths);
		interp.traversedLinks.addAll(traversedLinks);
		for (Map.Entry<Integer, int[]> entry : linkIdTraversalCountMap.entrySet())  // deep copy!!
			interp.linkIdTraversalCountMap.put(new Integer(entry.getKey()), entry.getValue().clone());
		interp.worstLinkType = worstLinkType;
		VariableTable vtCopy = (VariableTable) vt.clone();
		interp.setVariableTable(vtCopy);
		return interp;
	}
	
	 void setVariableTable(VariableTable vt){
		 this.vt = vt;
	 }
	 public VariableTable getVariableTable(){
		 return vt;
	 }
	
	/**
	 * A characterization of the whole interpretation by its least desirable link, one of <ol>
	 * <li>{@link ExampleTracerTracer#INCORRECT_ACTION} (worst)</li>
	 * <li>{@link ExampleTracerTracer#SUBOPTIMAL_ACTION}</li>
	 * <li>{@link ExampleTracerTracer#CORRECT_ACTION} (best)</li>
	 * </ol>
	 * @return {@link #worstLinkType}
	 */
	public String getType() {
		return worstLinkType;
	}

	public Set<ExampleTracerPath> getPaths() {
		return validPaths;
	}
	public void removePath(ExampleTracerPath path) {
		validPaths.remove(path);
	}
	
	/**
	 * Adds the link to the path of interpretation
	 * @param link
	 * @return
	 */
	void addLink(ExampleTracerLink link) {
		traversedLinks.add(link);  //FIXME won't work w/ solver
		Matcher matcher = link.getMatcher();
		int increment = (matcher == null ? 1 : matcher.getTraversalIncrement());
		int[] traversalCount = linkIdTraversalCountMap.get(new Integer(link.getUniqueID()));
		if (trace.getDebugCode("ett")) trace.out("ett", "addLink: matcher "+matcher+", old count "+
				(traversalCount == null ? -1 : traversalCount[0])+", increment "+increment+" "+link.getID());
		if (traversalCount != null)
			traversalCount[0] += increment;
		else
			linkIdTraversalCountMap.put(link.getUniqueID(), traversalCount = new int[] { increment });
		String linkType = link.getType();
		if (ExampleTracerTracer.compareLinkTypes(worstLinkType, linkType) < 0)
			worstLinkType = linkType;
	}	
	
	/**
	 * Checks whether the given link has already been traversed or not. A link is traversed
	 * with respect to this interpretation if its count in {@link #linkIdTraversalCountMap}
	 * is at least {@link EdgeData#getMinTraversals()}. 
	 * @param link
	 * @return true as above; false if no entry in {@link #linkIdTraversalCountMap}
	 */
	public boolean isTraversed(ExampleTracerLink link) {
		int[] traversalCount = linkIdTraversalCountMap.get(new Integer(link.getUniqueID()));
		if (traversalCount == null)
			return false;
		return traversalCount[0] >= link.getEdge().getMinTraversals(); 
	}
	
	/**
	 * Checks whether the given link has already been visited or not
	 * @param linkId
	 * @return - boolean value depending whether the link has been visited or not
	 */
	public boolean isVisited(int uniqueID) {
	    return linkIdTraversalCountMap.containsKey(new Integer(uniqueID));
	}
	
	public ArrayList<ExampleTracerLink> getMatchedLinks() {
		return traversedLinks;
	}
	
	ExampleTracerLink getLastMatchedLink() {
		if(traversedLinks.size()==0)
			return null;
		return traversedLinks.get(traversedLinks.size()-1);
	}
	
	int getNumberOfPreferredPrefixLinks() {
		int numberOfLinks = 0;
		Iterator links = getMatchedLinks().iterator();
		while (links.hasNext()) {
			ExampleTracerLink link = (ExampleTracerLink)links.next();
			if (link.getEdge().isPreferredEdge())
				numberOfLinks++;
			else
				break;
		}
		return numberOfLinks;
	}
	
	int getNumberOfPreferredLinks() {
		int numberOfLinks = 0;
		for(ExampleTracerLink link : traversedLinks) {
			if (link.getEdge().isPreferredEdge())
				numberOfLinks++;
		}
		return numberOfLinks;
	}

	public int getTraversalCount(ExampleTracerLink target) {
		if(target == null) {
		}
		
		int[] traversalCount = linkIdTraversalCountMap.get(new Integer(target.getUniqueID()));
		return traversalCount == null ? 0 : traversalCount[0];
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer("{");
		for(ExampleTracerLink link : traversedLinks) {
			s.append(link.getUniqueID()).append(", ");
		}
		if (s.toString().endsWith(", "))
			s.delete(s.lastIndexOf(", "), s.length());
		s.append(" (").append(validPaths == null ? -1 : validPaths.size()).append(" paths)");
		s.append(" var tbl "+vt);
		s.append("}");
		return s.toString();
	}

	public void setPaths(Set<ExampleTracerPath> paths) {
		validPaths.addAll(paths);
	}
}
