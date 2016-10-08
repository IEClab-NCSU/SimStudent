/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelManager;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemStateReaderJDom;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.RuleProduction;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.DefaultLinkGroup;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.Utilities.trace;

/**
 * Dialogue to choose a Behavior Recorder Diagram (.brd) file and insert its whole graph
 * as a subgraph, as with {@link NodeView#paste(ProblemNode)}.
 */
public class InsertSubgraphDialog {

	/** To uniquify group names. */
	private static int insertCount = 0;
	
	/** Controller needed for dialog placement, . */
    private BR_Controller controller;
	
	/** The paste-to node in the author's {@link ProblemModel}. */
	private ProblemNode pasteNode;

	/**
	 * Create the ProblemsOrganizer dialog.
	 * @param controller needed for superclass constructor
	 * @param pasteNode node in the author's {@link ProblemModel} where the subgraph will be pasted 
	 */
    public InsertSubgraphDialog(BR_Controller controller, ProblemNode pasteNode) {
    	this.controller = controller;
    	this.pasteNode = pasteNode;
	}

	/**
     * Display a file chooser to choose a .brd file.
     * @param controller
	 * @param pasteNode node in the author's {@link ProblemModel} where the subgraph will be pasted 
     */
    public static void doDialog(BR_Controller controller, ProblemNode pasteNode) {
    	InsertSubgraphDialog isd = new InsertSubgraphDialog(controller, pasteNode);
    	isd.insertSubgraph(isd.getBRDOtherLocation());
    }

    /**
     * Select a .brd filename using default directory from
     * {@link SaveFileDialog#getBrdFileOtherLocation(BR_Controller)}.
     * @return file chosen; null if none or canceled
     */
    protected File getBRDOtherLocation() {
        String targetDir = SaveFileDialog.getBrdFileOtherLocation(controller);
        File f = DialogUtilities.chooseFile(targetDir, new BrdFilter(), "Choose a subgraph file",
        		"Open", controller);
        return f;
    }

    /**
     * Read the given BRD file and paste its graph at {@link #pasteNode}.
     * @param brd graph to read; no-op if null
     */
	void insertSubgraph(File brd) {
		if (brd == null)
			return;
		try {
			ProblemModel pm = loadBRD(brd);
			NodeView nv = pasteNode.getNodeView();
			ProblemNode copyNode = pm.getStartNode();
			trace.out("mg", "NodeView (insertSubgraph): setting copy node to " +
					((pm.getStartNode() == null) ? "null" : "non-null"));
			controller.setCopySubgraphNode(pm.getStartNode());
			if (trace.getDebugCode("br")) trace.out("br", "insertSubgraph("+brd+") copyNode "+copyNode+", pasteNode "+pasteNode);
			Map<ProblemEdge, ProblemEdge> oldToNewEdges = nv.paste(copyNode);
			insertCount++;
			copyAllGroups(pm, pasteNode.getProblemModel(), oldToNewEdges);
		} catch (Exception e) {
			String errMsg = "Error loading subgraph from BRD file "+brd+": "+e+
					(e.getCause() == null ? "" : "; cause "+e.getCause());
			trace.errStack(errMsg, e);
			JOptionPane.showMessageDialog(controller == null ? null : controller.getActiveWindow(),
					errMsg, "Error loading subgraph from BRD file", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void insertSubgraphFromClipboard(ProblemNode sourceNode, ProblemNode destNode) {
		pasteNode = destNode;
		ProblemModel pm = sourceNode.getProblemModel();
		if(trace.getDebugCode("mg"))
			trace.out("mg", "insertSubgraphFromClipboard("+sourceNode+", "+destNode+")");
		try {
			NodeView nv = pasteNode.getNodeView();
			Map<ProblemEdge, ProblemEdge> oldToNewEdges = nv.paste(sourceNode);
			insertCount++;
			copyAllGroups(pm, pasteNode.getProblemModel(), oldToNewEdges);
		} catch (Exception e) {
			String errMsg = "Error loading subgraph from problem model "+pm.getProblemName()+": "+e+
					(e.getCause() == null ? "" : "; cause "+e.getCause());
			trace.errStack(errMsg, e);
			JOptionPane.showMessageDialog(controller == null ? null : controller.getActiveWindow(),
					errMsg, "Error loading subgraph from BRD file", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Construct in a destination graph the original group structure of an inserted subgraph.
	 * @param fromPM ProblemModel holding the subgraph
	 * @param toPM destination graph
	 * @param oldToNewMapping mapping generated by {@link NodeView#paste(ProblemNode)}
	 */
    private void copyAllGroups(ProblemModel fromPM, ProblemModel toPM, Map<ProblemEdge, ProblemEdge> oldToNewMapping) {
    	Map<Integer, ProblemEdge> oldEdgeIdMap = new LinkedHashMap<Integer, ProblemEdge>();
    	for (ProblemEdge edge : oldToNewMapping.keySet())
    		oldEdgeIdMap.put(edge.getUniqueID(), edge);  // this trouble because ProblemModel.getEdge(id) is linear time!
    	ExampleTracerGraph fromGraph = fromPM.getExampleTracerGraph();
    	ExampleTracerGraph toGraph = toPM.getExampleTracerGraph();
    	GroupModel fromGM = fromGraph.getGroupModel();
    	GroupModel toGM = toGraph.getGroupModel();
    	Set<String> fromGNames = fromGM.getAllGroupNames();
    	for (String gName : fromGNames) {
    		DefaultLinkGroup fromGrp = (DefaultLinkGroup) fromGM.getGroupByName(gName);
    		String newName = Integer.toString(insertCount)+'-'+gName;
    		Set<ExampleTracerLink> newLinks = getNewLinksInOldGroup(fromGrp, toGraph,
    				oldToNewMapping, oldEdgeIdMap);
    		toGM.addGroup(newName, fromGrp.isOrdered(), newLinks);
    	}
	}

    /**
     * Given a group in an old graph, find the corresponding links in a new graph.
     * @param fromGrp group to clone
     * @param newGraph graph holding new links to collect 
     * @param oldToNewMapping mapping of old {@link ProblemEdge}s to new ones
     * @param oldEdgeIdMap lookup for old edges by id (could use {@link ProblemModel#getEdge(int)}, but it's slow)
     * @return links from the new graph that correspond to members of the old group
     */
	private Set<ExampleTracerLink> getNewLinksInOldGroup(DefaultLinkGroup fromGrp,
			ExampleTracerGraph newGraph, Map<ProblemEdge, ProblemEdge> oldToNewMapping,
			Map<Integer, ProblemEdge> oldEdgeIdMap) {
		Set<ExampleTracerLink> result = new HashSet<ExampleTracerLink>();
		for (ExampleTracerLink oldLink : fromGrp.getLinks()) {
			ProblemEdge oldEdge = oldEdgeIdMap.get(oldLink.getUniqueID());
			ProblemEdge newEdge = oldToNewMapping.get(oldEdge);
			ExampleTracerLink newLink = newGraph.getLink(newEdge.getUniqueID());
			result.add(newLink);
		}
		return result;
	}

	/**
     * Read a .brd file into a new {@link ProblemModel}.
     * @param name simple filename
     * @param directory path, with trailing separator
     * @return result of {@link ProblemStateReaderJDom#loadBRDFileIntoProblemModel(String, RuleProduction.Catalog)}
     */
	private ProblemModel loadBRD(File brd) throws Exception {
		ProblemStateReaderJDom psrj = new ProblemStateReaderJDom(null); 
		ProblemModel pm = psrj.loadBRDFileIntoProblemModel(brd.getCanonicalPath(), new RuleProduction.Catalog());
		return pm;
	}
}