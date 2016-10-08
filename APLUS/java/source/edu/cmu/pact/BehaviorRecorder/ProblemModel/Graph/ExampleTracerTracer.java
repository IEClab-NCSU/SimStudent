package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.cmu.pact.client.HintMessagesManager;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageBuilder;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExactMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExpressionMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.SolverMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.VectorMatcher;
import edu.cmu.pact.Utilities.trace;

/**
 * Top-level algorithms for the example tracer.
 * @author eschwelm, after aleven
 */
public class ExampleTracerTracer {
	
	public static final String INCORRECT_ACTION = EdgeData.BUGGY_ACTION;
	public static final String SUBOPTIMAL_ACTION = EdgeData.FIREABLE_BUGGY_ACTION;
	public static final String CORRECT_ACTION = EdgeData.CORRECT_ACTION;
	public static final String NULL_MODEL = EdgeData.NO_MODEL;
	public static final String HINT = PseudoTutorMessageBuilder.HINT;

	/** Result code for interactions that shouldn't be considered tutorable steps. */
	public static final String NOT_A_TRANSACTION = "NotATransaction";
	
	private VariableTable startStateVT;
	private ArrayList<ExampleTracerInterpretation> interpretations;
	private Map<Integer, ExampleTracerLink> incorrectActionMatches;
	private ArrayList<ExampleTracerSAI> studentSAIs;
	private ExampleTracerGraph graph;
	private ExampleTracerEvent result;
	private boolean isDemonstrateMode;
	private static int count =0;
	private int instance;

	private HashSet<ExampleTracerEventListener> listeners;
	
	/** Last known best interpretation. */
	private ExampleTracerInterpretation bestInterpretation;
	
	/* This function should be called only if you just declared a new exampletracer
	 * and need to make sure the initial variabltable for each trace has the startstate
	 * variables.
	 * 
	 * If a new problem is loaded this can be called to reset the tracer to have the new
	 * VT, or if the start-state variable table is changed.
	 * 
	 */
	
	public void initialize(VariableTable startStateVariableTable){
		if(startStateVariableTable==null)
			this.startStateVT = null;
		else
			this.setStartStateVT(startStateVariableTable);
		resetTracer();
	}
	
	//need to somehow give this guy the basic variabletable..
	public void resetTracer() {
		//trace.outPlain("borg", "ETT.resetTracer");
		if(interpretations==null)
			interpretations = new ArrayList<ExampleTracerInterpretation>();
		else
			interpretations.clear();
		ExampleTracerInterpretation tempInterp = new ExampleTracerInterpretation(getAllPaths());
		if(startStateVT!=null)
			tempInterp.setVariableTable(startStateVT);
		interpretations.add(tempInterp);
		bestInterpretation = tempInterp;

		if(incorrectActionMatches == null)
			incorrectActionMatches = new HashMap<Integer, ExampleTracerLink>();
		else
			incorrectActionMatches.clear();
		
		if(studentSAIs==null)
			studentSAIs = new ArrayList<ExampleTracerSAI>();
		else
			studentSAIs.clear();

		for (ExampleTracerLink link : getGraph().getLinks())
			link.getMatcher().reset();
	}
	
	/**
	 * Constructor
	 * @param graph
	 */
	public ExampleTracerTracer (ExampleTracerGraph graph) {
		instance = count++;
		listeners = new HashSet<ExampleTracerEventListener>();
		if (trace.getDebugCode("eti")) trace.outNT("eti","creating listener list for TracerTracer #"+instance);
		this.graph = graph;
		resetTracer();
	}
	

	private Set<ExampleTracerPath> getAllPaths() {
		return graph.findAllPaths();
	}
	
	/**
	 * @return - Returns the current list of student SAIs in the tracer.
	 *           Each element is of the type {@link ExampleTracerSAI}
	 */
	public ArrayList<ExampleTracerSAI> getStudentSAIs() {
		return studentSAIs;
	}
	
	/**
	 * Internal accessor
	 * @return - Returns the current interpretations in the tracer
	 */
	ArrayList<ExampleTracerInterpretation> getInterpretationsInternal() {
		return interpretations;
	} 
	
	/**
	 * Public accessor
	 * @return - Returns a clone of the current list of interpretations
	 */
	public ArrayList<ExampleTracerInterpretation> getInterpretations(){
		return interpretations == null ? null : (ArrayList<ExampleTracerInterpretation>)interpretations.clone();
	}
	
	/**
	 * Sets {@link #startStateVT} to deep copy of given {@link VariableTable}.
	 * @param vt
	 */
	public void setStartStateVT(VariableTable vt){
		startStateVT = (VariableTable) vt.clone();
	}
	
	/**
	 * Internal accessor.
	 * @return {@link #startStateVT}
	 */
	VariableTable getStartStateVTInternal(){
		return startStateVT;
	}
	
	/**
	 * Public accessor.
	 * @return {@link #startStateVT}.{@link VariableTable#clone()}
	 */
	public VariableTable getStartStateVT(){
		return startStateVT == null ? null : (VariableTable) startStateVT.clone();
	}
	
	private void setInterpretations(Collection<ExampleTracerInterpretation> c) {		
		interpretations.clear();
		if(c.size()==0)
			return;
		interpretations.addAll(c);
		bestInterpretation = null;
	}
	public ExampleTracerInterpretation getBestInterpretation() {
		if (bestInterpretation == null)
			bestInterpretation = getBestInterpretation(getInterpretationsInternal());
		return bestInterpretation;
	}
	private ExampleTracerInterpretation getBestInterpretation(Iterable<ExampleTracerInterpretation> iter) {
		ExampleTracerInterpretationComparator comp = new ExampleTracerInterpretationComparator();
		ExampleTracerInterpretation bestInterp=null;
		for(ExampleTracerInterpretation interp : iter) {
			if(bestInterp==null)
				bestInterp = interp;
			else {
				int compResult = comp.compare(interp, bestInterp);
				if (trace.getDebugCode("etcomp"))
					trace.out("etcomp", "interps compare("+interp+","+bestInterp+")="+compResult);
				if (compResult>0)
					bestInterp = interp;
			}
		}
		return bestInterp;
	}
	
	/**
	 * 
	 * @return - Returns the graph of the tracer
	 */
	ExampleTracerGraph getGraph() {
		return this.graph;
	}
	
	/**
	 * Return the results of the last trace. 
	 * @return {@link #result}; null if no trace
	 */
	public ExampleTracerEvent getResult() {
		return result;
	}
	/**
	 * Top-level method to process a hint request.
	 * @param selection 
	 * @param action
	 * @param input
	 * @param rtnResult if not null, put result here
	 * @return result of {@link #doHint(ExampleTracerSAI)}
	 */
	public ProblemEdge doHint(Vector selection, Vector action, Vector input,
			String actor, ExampleTracerEvent[] rtnResult, boolean allowHintBias) {
		ProblemEdge link = null;
		Vector previousFocus = null;
		Vector previousAction = null;
		if (trace.getDebugCode("ett")) trace.out("ett", "doHint("+selection+","+action+","+input+")");
		if (allowHintBias
				&& action != null && action.size() > 1
				&& action.get(1).equals(HintMessagesManager.PREVIOUS_FOCUS)
				&& selection != null && selection.size() > 1
				&& selection.get(1)!= null
				&& !(selection.get(1).equals("null"))) {
			previousFocus = new Vector();
			previousFocus.add(selection.get(1));
			
			if (action.size() > 2) {
				previousAction = new Vector();
				for (int i=2; i<action.size(); i++) 
					previousAction.add(action.get(i));
			} 
		} 

		result = new ExampleTracerEvent(this,
				new ExampleTracerSAI(previousFocus, previousAction, null, actor));
		link = matchForHint(result);
		
		if (rtnResult != null)
			rtnResult[0] = result;
		return link;
	}
	
	/**
	 * Calculate the current node as <ul>:
	 * <li>{@link ExampleTracerInterpretation#getCurrentState() getCurrentState()}
	 *     from the first element of {@link #interpretations}; or</li>
	 * <li>the start node if no interpretations are defined.</li>
	 * </ul>
	 * @return {@link ExampleTracerNode@getProblemNode()} on found node
	 */
	public ProblemNode getCurrentNode(boolean isDemonstrateMode) {
		return getCurrentNode(isDemonstrateMode, false);
	}
	
	/**
	 * Calculate the current node as <ul>:
	 * <li>{@link ExampleTracerInterpretation#getCurrentState() getCurrentState()}
	 *     from the first element of {@link #interpretations}; or</li>
	 * <li>the start node if no interpretations are defined.</li>
	 * </ul>
	 * @return {@link ExampleTracerNode@getProblemNode()} on found node
	 */
	public ProblemNode getCurrentNode(boolean isDemonstrateMode, boolean wantHint) {
		if(isDemonstrateMode) {
			ExampleTracerLink deepest = ExampleTracerPath.getDeepestLink(getBestInterpretation().getMatchedLinks());
			if(deepest!=null)
				return graph.getNode(deepest.getNextNode()).getProblemNode();
		}
		
		// sewall 9/09: below, why not interp[0] instead of getBestInterpretation()
		ExampleTracerInterpretation bestInterp = getBestInterpretation();
		if(bestInterp != null 
				&& bestInterp.getMatchedLinks().size()!=0
				&& bestInterp.getLastMatchedLink().getEdge().isDone()){
			return graph.getNode(bestInterp.getLastMatchedLink().getNextNode()).getProblemNode();
		}
		ProblemNode[] endNode = new ProblemNode[1];
		ExampleTracerLink bestNextLink = getBestNextLink(wantHint, endNode,
				new ExampleTracerInterpretation[] { bestInterp });
		if(bestNextLink==null) {
			if (endNode[0] != null)
				return endNode[0];
			if(graph.getStartNode()!=null) {
				return graph.getStartNode().getProblemNode();
			}
			return null;
		}else
			return graph.getNode(bestNextLink.getPrevNode()).getProblemNode();
	}

	/**
	 * Find an unvisited link that would be a valid next step. 
	 * @param wantHint require link to have hints
	 * @param rtnLastNode if not null, return in element 0 the last-reached node in a traversed path
	 * @param rtnInterp if not null, supply (in element 0) or return the interpretation used
	 * @return highest unvisited link that would be a good next step
	 */
	ExampleTracerLink getBestNextLink(boolean wantHint, ProblemNode[] rtnLastNode,
			ExampleTracerInterpretation[] rtnInterp) {
		//graph.redoLinkDepths();
		ExampleTracerInterpretation interp =
				(rtnInterp != null && rtnInterp[0] != null ? rtnInterp[0] : null);
		if (interp == null)
			interp = getBestInterpretation();
		if (rtnInterp != null)
			rtnInterp[0] = interp;
		ExampleTracerPath path = ExampleTracerPath.getBestPath(interp.getPaths());
		ExampleTracerLink highestLink = getHighestUntraversedLink(interp, path, wantHint);
		if (highestLink != null)
			return highestLink;
		ExampleTracerLink lastLink = null;
		for(ExampleTracerLink link : path) {
			lastLink = link;
			if(interp.getTraversalCount(link)<link.getEdge().getMaxTraversals()
					&& (!wantHint || nHints(link, interp.getVariableTable()) > 0)
					&& graph.observesOrderingConstraints(interp.getMatchedLinks(), link, path.getLinks(), result)
					&& doneStepOK(interp, link, path.getLinks())) 
 				return link;
		}
		if (rtnLastNode != null && lastLink != null)
			rtnLastNode[0] = lastLink.getEdge().getEndProblemNode();
		return null;
	}

	/**
	 * Find the first (moving from the start state) unvisited link in an interpretation, such
	 * that it would be a preferred next step.
	 * @param interp interpretation with set of visited links
	 * @param path preferred path within the interp
	 * @param wantHint require link to have hints
	 * @return null if no link qualifies
	 */
	private ExampleTracerLink getHighestUntraversedLink(ExampleTracerInterpretation interp,
			ExampleTracerPath path, boolean wantHint) {
		ExampleTracerEvent result = new ExampleTracerEvent(this);
		boolean lastLinkVisited = true;
		ArrayList<ExampleTracerLink> suggestedLinks = new ArrayList<ExampleTracerLink>();

		for(ExampleTracerLink link : path) {
			if (interp.isTraversed(link) || isNoOp(link)) {
				lastLinkVisited = true;
				continue;
			}
			if (lastLinkVisited) {
				ExampleTracerNode pNode = graph.getNode(link.getPrevNode());
				List<ExampleTracerLink> outLinks = pNode.getOutLinks();
				for (ExampleTracerLink outLink : outLinks) {
					if (wantHint && nHints(outLink, interp.getVariableTable()) < 1)
						continue;
					if (interp.getTraversalCount(outLink) >= outLink.getEdge().getMaxTraversals())
						continue;
					suggestedLinks.add(outLink);
				}
			}
			EdgeData edgeData = link.getEdge();
			lastLinkVisited = (edgeData == null ? false :
					edgeData.getMinTraversals() <= interp.getTraversalCount(link));
		}
		
		Collections.sort(suggestedLinks, new ExampleTracerLinkComparator(interp));
		for (ExampleTracerLink suggestedLink : suggestedLinks) {
			if (isPathOK(suggestedLink, interp, path, isDemonstrateMode, result))
				return suggestedLink;
			Iterator<ExampleTracerPath> iter = interp.getPaths().iterator();
			while(iter.hasNext()) {        			
				ExampleTracerPath otherPath = iter.next();
				if (path != otherPath) {          // no need to recheck given path
					if (isPathOK(suggestedLink, interp, otherPath, isDemonstrateMode, result))
						return suggestedLink;
				}
			}
		}
		return null;
	}

	/**
	 * Return the number of hints on an edge.
	 * @param link
	 * @param vt VariableTable for evaluating formulas in hints
	 * @return number of non-empty hints, evaluated dynamically
	 */
	private int nHints(ExampleTracerLink link, VariableTable vt) {
		return nHints(link.getEdge(), vt);
	}

	/**
	 * Return the number of nonempty hints on an edge.
	 * @param edgeData
	 * @param vt VariableTable for evaluating formulas in hints
	 * @return number of non-empty hints, evaluated dynamically
	 */
	private int nHints(EdgeData edgeData, VariableTable vt) {
		if (edgeData == null)
			return 0;
		List<String> hints = edgeData.getAllNonEmptyHints();
		if (hints == null || hints.size() < 1)
			return 0;
		if (getResult() == null)
			return hints.size();
		ExampleTracerSAI sai = getResult().getStudentSAI();
		edgeData.interpolateHints(vt, sai.getSelectionAsString(),
				sai.getActionAsString(), sai.getInputAsString());
		hints = edgeData.getHints();  // gets only the nonempty hints
		return hints.size();
	}

	/**
	 * Tell whether the given link is a no-op for example tracing. A no-op link
	 * is automatically considered traversed in a path, but generates no transaction or log.
	 * @param link
	 * @return true if minTraversals and maxTraversals are both zero
	 */
	private boolean isNoOp(ExampleTracerLink link) {
		EdgeData edgeData = link.getEdge();
		if (edgeData == null)
			return false;
		else
			return edgeData.getMinTraversals() < 1 && edgeData.getMaxTraversals() < 1;
	}

	public ProblemEdge traceForHint (ExampleTracerEvent result) {
		boolean eval = evaluate(result, true, false);		
		if (result.getResult().equals(this.CORRECT_ACTION)) {
			EdgeData edgeData = result.getReportableLink().getEdge();
			return edgeData.getEdge();
		}
		return null;
	}
	
	/**
	 * Find all the links with the given sai.
	 * @param sai student's sai
	 * @param hint are we finding matching links for a hint trace?
	 * @param vt -interp specific vt, if null refers to problemmodel(best interp)'s vt 
	 * @return links that match...
	 */
	ArrayList<ExampleTracerLink> findSAIMatchingLinks(ExampleTracerSAI sai, boolean hint, VariableTable vt) {
		if (trace.getDebugCode("et")) trace.outNT("et", "findSAIMatchingLinks("+sai+","+hint+
				",vt#"+vt.getInstance()+")");
		ArrayList<ExampleTracerLink> matchingLinks = new ArrayList<ExampleTracerLink>();
		for (ExampleTracerLink link :  graph.getLinks()) {
			if (!hint) {
				if (link.matchesSAI(sai, vt)) {
					matchingLinks.add(link);
				}
			}
			else if (link.matchesSAIforHint(sai, getResult())) {
				matchingLinks.add(link);
			}			
		}
		return matchingLinks;
	}	
	/**
	 * Tell whether a step is valid with respect to achieving a problem-done state.
	 * Returns true if <ul>
	 * <li>the matched link is not a Done step or
	 * <li>it's an incorrect action or</li>
	 * <li>there is one path in the extension with all links visited.
	 * </ul>
	 * @param interp check min traversal counts in this interpretation
	 * @param newLink return false if link is a Done step and it's premature
	 * @param path check this path
	 * @return
	 */
	boolean doneStepOK (ExampleTracerInterpretation interp, ExampleTracerLink newLink, Set<ExampleTracerLink> path) {		
		if (trace.getDebugCode("ET")) trace.out("ET", "doneStepOK("+interp+", "+newLink+", ...)");
		if (newLink==null || !(newLink.getEdge().isDone()))
			return true;
		if (newLink.getType().equals(INCORRECT_ACTION))
			return true;
		if (trace.getDebugCode("ET")) trace.out("ET", "doneStepOK("+interp+", "+newLink+", "+path+")");
		Map<ExampleTracerLink, Integer> visitCount = new HashMap<ExampleTracerLink, Integer>();
		for(ExampleTracerLink link : path) {
			if (link.getUniqueID() == newLink.getUniqueID())
				continue;
			int traversalCount = interp.getTraversalCount(link);
			if (trace.getDebugCode("ET")) trace.out("ET", "doneStepOK() "+link+" traversalCount "+traversalCount);
			if (traversalCount < link.getEdge().getMinTraversals())
				return false;
		}
		return true;
	
	}
	/**
	 * Method for unit tests.
	 * @param sai selection, action, input for {@link ExampleTracerEvent}
	 * @return result of {@link #evaluate(ExampleTracerEvent)} on sai
	 */
	boolean evaluate(ExampleTracerSAI sai) {
		result = new ExampleTracerEvent(this);
		result.setStudentSAI(sai);
		return evaluate(result, false, true);
	}
	
	/**
	 * Evaluates the given sai and updates the tracer with new interpretations
	 * If there are no link matches or only incorrect-action link matches, then
	 * the state of the tracer remains unchanged.
	 * @param result instance to hold trace results
	 * @return true, if there are any correct/suboptimal action link matches
	 */
	boolean evaluate(ExampleTracerEvent result, boolean isHintTrace, boolean doUpdate) {
    	Collection<ExampleTracerInterpretation> newInterps = new HashSet<ExampleTracerInterpretation>();
        ArrayList<ExampleTracerLink> saiLinkMatches = result.getPreloadedLinkMatches();
		//graph.redoLinkDepths();//Excessive
		if (trace.getDebugCode("ett")) trace.out("ett", "PreloadedLinkMatches: "+saiLinkMatches);
		//graph.redoLinkDepths();//Excessive
		
		for(ExampleTracerInterpretation interpretation : interpretations) {
			if (trace.getDebugCode("ett")) trace.out("ett", "trying interpretation: "+interpretation);
			saiLinkMatches = result.getPreloadedLinkMatches();
			if (saiLinkMatches == null)
				saiLinkMatches = findSAIMatchingLinks(result.getStudentSAI(), isHintTrace, interpretation.getVariableTable());
			if (trace.getDebugCode("ET")) trace.out("ET", "number of link matches: "+saiLinkMatches.size());
			for(ExampleTracerLink link : saiLinkMatches) {
				if (trace.getDebugCode("ett")) trace.out("ett", "Using link: "+link+", traversals "+interpretation.getTraversalCount(link));
           		if(interpretation.getTraversalCount(link)>=link.getEdge().getMaxTraversals())
    				continue;
        		ExampleTracerInterpretation newInterp = interpretation.clone();
    			if (trace.getDebugCode("ET")) trace.out("ET", "ClonedInterp: "+newInterp);
        		Iterator<ExampleTracerPath> iter = newInterp.getPaths().iterator();
        		while(iter.hasNext()) {        			
        			ExampleTracerPath path = iter.next();
        			if (trace.getDebugCode("ET")) trace.out("ET", "Trying Path: "+path);
        			if(!isPathOK(link, newInterp, path, isDemonstrateMode, result))
        				iter.remove();
        		}
        		if(newInterp.getPaths().size()>0) {
        			Matcher m = link.getMatcher();
        			result.setTutorSAI(new ExampleTracerSAI(m.getSelection(), m.getAction(),
        					m.getEvaluatedInput(), m.getActor()));
            		boolean solverResult = checkSolver(link, result.getStudentSAI(), isHintTrace, result);
            		if (trace.getDebugCode("solverdebug")) trace.out("solverdebug", "solverResult "+result.getResult()+
            				", InterfaceActions: "+result.getInterfaceActions());
        			fixupMatcherForPreloadedLinkMatches(link, newInterp, result);  // sewall 2010-11-18 moved from below
            		if (!solverResult) {
            			;  // FIXME no new interp if solver not completed?
            		}
        			newInterp.addLink(link);
        			if (doUpdate) {
        				Vector replacementInput = replaceInput(link, result.getStudentSAI(), newInterp);
        				newInterp.updateVariableTable(result.getStudentSAI(), replacementInput, link);
        			}
        			newInterps.add(newInterp);
        		}
        	}        		
        }
		
		if (trace.getDebugCode("et")) trace.out("et", "newInterps.size() "+newInterps);
		result.setNumberOfInterpretations(newInterps.size());
        if(newInterps.size()==0) {
        	if(!saiLinkMatches.isEmpty() && saiLinkMatches.get(0).getEdge().isDone())
        		result.setDoneStepFailed(true);
        	result.setResult(NULL_MODEL);
        	return false;
        }
        
        // How to ensure bestInterp has solver link?
        ExampleTracerInterpretation bestInterp = getBestInterpretation(newInterps);
        String type = null;
        if (result.isSolverResult())
        	type = (NULL_MODEL.equalsIgnoreCase(result.getResult()) ?
        			INCORRECT_ACTION : result.getResult());
        else
        	type = bestInterp.getLastMatchedLink().getType();              
        if (trace.getDebugCode("et")) trace.out("et", "bestInterp "+bestInterp+", lastMatchedLinktype "+type);
        
        if (doUpdate) {
        	studentSAIs.add(result.getStudentSAI());
			if (type.equals(CORRECT_ACTION) || type.equals(SUBOPTIMAL_ACTION)) {			
				//Remove all interpretations which just matched to an incorrect action
				Iterator<ExampleTracerInterpretation> iter = newInterps.iterator();
				while(iter.hasNext())
					if(iter.next().getLastMatchedLink().getType().equals(INCORRECT_ACTION))
						iter.remove();
				
				setInterpretations(newInterps);
				if (trace.getDebugCode("et")) trace.out("et", "Update Example Tracer");				
				incorrectActionMatches.clear();			
			}
			else {			
				result.setNumberOfInterpretations(0);  // don't report incorrect interps
				incorrectActionMatches.clear();
				for(ExampleTracerInterpretation interp : newInterps) {
					incorrectActionMatches.put(Integer.valueOf(interp.getLastMatchedLink().getUniqueID()),
							interp.getLastMatchedLink());
				}
			}			
			if(type.equals(INCORRECT_ACTION))//Only keep list of correct SAIs
				studentSAIs.remove(studentSAIs.size()-1);
        }
		ExampleTracerLink link = bestInterp.getLastMatchedLink();
		if (trace.getDebugCode("et")) trace.out("et", "updateExampleTracer: reportableLink "+link);
		if (!result.isSolverResult())   // solver already set result
			result.setResult(type);
		result.setReportableLink(link);
		result.setReportableVariableTable(bestInterp.getVariableTable());

		ExampleTracerSAI sai = getResult().getStudentSAI();
		link.getEdge().interpolateHints(bestInterp.getVariableTable(), sai.getSelectionAsString(),
				sai.getActionAsString(), sai.getInputAsString());
		result.setReportableHints(link.getEdge().getHints());  // gets only the nonempty hints
        
		fireExampleTracerEvent(result);
		//Null model?
        return !type.equals(INCORRECT_ACTION);      
    }

	/**
	 * If indicated by {@link EdgeData#replaceInput()}, calculate a new value for the student's
	 * input using {@link EdgeData#evaluateReplacement(Vector, Vector, Vector, VariableTable)}.
	 * @param link {@link ExampleTracerLink#getEdge()} has
	 *        {@link EdgeData#evaluateReplacement(Vector, Vector, Vector, VariableTable)}
	 * @param sai student's input data
	 * @param newInterp supplied variable table to use
	 * @return replacementInput; null if no replacement indicated
	 */
	private Vector replaceInput(ExampleTracerLink link, ExampleTracerSAI sai,
			ExampleTracerInterpretation newInterp) {
		EdgeData edgeData = link.getEdge();
        if (edgeData == null || !edgeData.replaceInput())
        	return null;
		Vector replacementInput = edgeData.evaluateReplacement(sai.getSelectionAsVector(),
				sai.getActionAsVector(), sai.getInputAsVector(), newInterp.getVariableTable());
       	return replacementInput;
	}

	/**
	 * Tell whether a solver link succeeds. 
	 * @param link
	 * @param studentSAI
	 * @return always returns true to preserve this interpretation
	 */
    private boolean checkSolver(ExampleTracerLink link, ExampleTracerSAI studentSAI,
    		boolean isHintTrace, ExampleTracerEvent result) {

    	Matcher m = link.getMatcher();
    	if (!(m instanceof SolverMatcher))
    		return true;
    	if (isHintTrace) {
    		int nHints = ((SolverMatcher) m).requestHint(result);
    		if (trace.getDebugCode("et")) trace.out("et", "link "+link.getID()+": getHintMessages() returns "+nHints);
    	} else {
    		Vector<String> s = studentSAI.getSelectionAsVector();
    		Vector<String> a = studentSAI.getActionAsVector();
    		Vector<String> i = studentSAI.getInputAsVector();
    		boolean success = ((SolverMatcher) m).doStep(s, a, i, result);
    		if (trace.getDebugCode("et")) trace.out("et", "link "+link.getID()+": evaluate("+s+","+a+","+i+") returns "+result+
    				", InterfaceActions: "+result.getInterfaceActions());
    	}
    	return ((SolverMatcher) m).isDone();
	}

	private void fixupMatcherForPreloadedLinkMatches(ExampleTracerLink link,
			ExampleTracerInterpretation interp,	ExampleTracerEvent result) {
    	if (result.getPreloadedLinkMatches() == null)
    		return;
    	if (!(result.getPreloadedLinkMatches().contains(link)))
    		return;
		EdgeData edgeData = link.getEdge();
		setInterpolateSAI(edgeData);
        Matcher m = link.getMatcher();
        ExampleTracerSAI sai = result.getStudentSAI();
		boolean mResult = m.match(sai.getSelectionAsVector(), sai.getActionAsVector(),
				sai.getInputAsVector(), sai.getActor(), interp.getVariableTable());
		ExampleTracerSAI tutorSAI = new ExampleTracerSAI(m.getSelection(), m.getAction(),
				m.getDefaultInput(), m.getActor());
		if (m instanceof VectorMatcher) {
			Matcher inputMatcher = (Matcher) ((VectorMatcher) m)
					.getMatchers(2).get(0);
			if (inputMatcher instanceof ExpressionMatcher) {
				if (((ExpressionMatcher) inputMatcher).isEqualRelation()) {
					String evaluatedInput = ((ExpressionMatcher) inputMatcher).getLastResult();
					result.setEvaluatedInput(evaluatedInput);				
					edgeData.setStudentInput(evaluatedInput);  // for travel through the BR state by click on the node.
					inputMatcher.setDefaultInput(((ExpressionMatcher) inputMatcher)
							.getLastResult());
					tutorSAI.setInput(inputMatcher.getEvaluatedInput());
				}
			} else if (inputMatcher instanceof ExactMatcher) {
				edgeData.setStudentInput(((VectorMatcher) m).getInputMatcher());
				tutorSAI.setInput(((VectorMatcher) m).getInputMatcher());
				edgeData.setStudentAction(((VectorMatcher) m).getActionMatcher());
				edgeData.setStudentSelection(((VectorMatcher) m).getSelectionMatcher());
//				System.err.println("[" + edgeData.getStudentSelection() + " , " 
//						+ edgeData.getStudentAction() + " , " + edgeData.getStudentInput() +"]");
			}
		} else if (m instanceof ExactMatcher) {
			edgeData.setStudentInput(m.getDefaultInput());
			tutorSAI.setInput(m.getDefaultInput());
			edgeData.setStudentAction(m.getDefaultAction());
			edgeData.setStudentSelection(m.getDefaultSelection());
		}
		
		result.setTutorSAI(tutorSAI);
		
	}

	/**
     * 
     * @param newLink The link we're trying to extend the interpretation/path with    
     * @param interp
     * @param path
     * @param isDemonstrateMode if we're in demonstrate mode, ignore the path beyond
     * the deepest traced link and recheck the interpretation fully
     * @return True if the newLink can be added, false otherwise
     */
    private boolean isPathOK(ExampleTracerLink newLink, ExampleTracerInterpretation interp,
    		ExampleTracerPath path, boolean isDemonstrateMode, ExampleTracerEvent result) {
    	Set<ExampleTracerLink> pathLinks;
    	
		if (trace.getDebugCode("ET")) trace.out("ET", "isPathOK("+newLink+", "+interp+", "+path+", "+isDemonstrateMode+")");
		//Since demonstrating may change the path ahead of where we are,
    	//don't check ordering beyond where we are.
    	if(isDemonstrateMode)    {		
    		ArrayList<ExampleTracerLink> allLinks = new ArrayList<ExampleTracerLink>(interp.getMatchedLinks());
    		allLinks.add(newLink);
    		pathLinks = path.getLinksRestricted(allLinks);
    	}
    	else
    		pathLinks = path.getLinks();
		if (trace.getDebugCode("ET")) trace.out("ET", "isPathOK() pathLinks: "+pathLinks);
    	
    	//Recheck old traversed links because the path may have changed
    	if(isDemonstrateMode) {
    		ArrayList<ExampleTracerLink> traversedLinks = new ArrayList<ExampleTracerLink>();
    		for(ExampleTracerLink link : interp.getMatchedLinks()) {
    			if(!graph.observesOrderingConstraints(traversedLinks, link, pathLinks, result)
    					|| !doneStepOK(interp, link, pathLinks))
    				return false;
    			traversedLinks.add(link);
    		}
    	}
    	
    	if(newLink.getType().equals(INCORRECT_ACTION)) {
			if(!graph.isIncorrectLinkOK(interp.getMatchedLinks(), newLink, pathLinks, interp))
				return false;
		}
		else {
			if(!graph.observesOrderingConstraints(interp.getMatchedLinks(), newLink, pathLinks, result))
				return false;
			if(!doneStepOK(interp, newLink, pathLinks))
				return false;
		}
    	return true;
    }
    
	
	private String linkName(ExampleTracerLink link, String s) {
		return "link" + link.getUniqueID() + "." + s;
	}
	
	/**
	 * Call {@link #evaluate(EdgeData)} for each edge in the given list.
	 * Can use this to advance the tracer across a path in a graph.
	 * @param edges List of edges in reverse order of evaluation
	 * @param results if not null, return {@link ExampleTracerEvent} from each edge-trace  
	 * @return List of edges actually traversed, in order of traversal
	 */
	public List<EdgeData> evaluateEdges(ExampleTracerPath path, List<ExampleTracerEvent> results) {
		resetTracer();
		List<EdgeData> traversedEdges = new ArrayList<EdgeData>();
		for (ExampleTracerLink link : path) {
			EdgeData data = link.getEdge();
			boolean traced = evaluate(data);
			ExampleTracerEvent result = getResult();
			if (trace.getDebugCode("br")) trace.outln("br", "evaluateEdges edgeID("+data+") returns "+traced+"; result "+result);
			if (results != null)
				results.add(result);
			if (traced)
				traversedEdges.add(data);
		}
		return traversedEdges;
	}
	
	/**
	 * Call {@link #evaluate(Vector, Vector, Vector, String)} for the given edge.
	 * @param edgeData
	 * @return result from evaluate()
	 */
	public boolean evaluate(EdgeData edgeData) {
		return evaluate(edgeData.getUniqueID(), edgeData.getSelection(),edgeData.getAction(),
				edgeData.getInput(),edgeData.getActor());
	}
	/**
	 * Trace an attempt against the graph.
	 * @param linkID preselected link, used for tutor-performed actions
	 * @param selection student selection
	 * @param action student action
	 * @param input student input
	 * @param actor whether student or tutor performed the action
	 * @return result of {@link #evaluate(ExampleTracerEvent)}
	 */
	public boolean evaluate(int linkID, Vector selection, Vector action, Vector input, String actor) {
		result = new ExampleTracerEvent(this);
		result.setStudentSAI(selection, action, input, actor);
		if (linkID > 0) {
			ExampleTracerLink link = graph.getLink(linkID);
			if(link == null) {
			}
			result.addPreloadedLinkMatch(link);
		}
		if (trace.getDebugCode("et")) trace.out("et", "ExTracerTracer(link"+linkID+") calling evaluate(), result "+result);
		return evaluate(result, false, true);
	}
	
	/**
	 * Don't know what this code does, but following link.matchesSAI()
	 */
	private void setInterpolateSAI(EdgeData edgeData) {
		String selection = "";
		if (edgeData.getSelection() != null)
			selection = edgeData.getSelection().get(0).toString();
		String action = null;
		if (edgeData.getAction() != null)
			action = edgeData.getAction().get(0).toString();
		String input = null;
		if (edgeData.getInput() != null)
			input = edgeData.getInput().get(0).toString();
	    edgeData.setInterpolateSAI(selection, action, input);
	}
	/**
	 * Trace an attempt against the graph.
	 * @param selection student selection
	 * @param action student action
	 * @param input student input
	 * @param actor whether student or tutor performed the action
	 * @return result of {@link #evaluate(ExampleTracerEvent)}
	 */
	public boolean evaluate(Vector selection, Vector action, Vector input, String actor) {
		return evaluate(-1, selection, action, input, actor);
	}
	
	public class ExampleTracerInterpretationComparator implements Comparator {
	    public int compare(Object d1, Object d2) {
	    	return compare((ExampleTracerInterpretation)d1,(ExampleTracerInterpretation)d2);
	    }
	    //i1>i2 iff i1 is a better interpretaion than i2
	    private int compare (ExampleTracerInterpretation i1, ExampleTracerInterpretation i2) {
	    	String i1Type = i1.getType();
	    	int result = compareLinkTypes(i1Type, i2.getType());
	    	if (trace.getDebugCode("et"))
	    		trace.out("et", "i1Type="+i1Type+"; compareLinkType("+i1+","+i2+") = "+result);
	    	if (result != 0)                          // for link types, t1<t2 if t1 is better
	    		return (result < 0 ? 1 : -1);
	    	// if comparing 2 incorrect, look first at the buggy links themselves
	    	if (INCORRECT_ACTION.equalsIgnoreCase(i1Type)) {
	    		ExampleTracerLink w1 = i1.getLastMatchedLink();
	    		ExampleTracerLink w2 = i2.getLastMatchedLink();
		    	int m = Matcher.compare(w1.getMatcher(), w2.getMatcher());
		    	if (trace.getDebugCode("et"))
		    		trace.out("et", "compare matchers("+w1+","+w2+") = "+m);
		    	if (m != 0)
		    		return m;                 // prefer more specific Matchers	    		
	    	}
	    	ExampleTracerPath p1 = ExampleTracerPath.getBestPath(i1.getPaths());
	    	ExampleTracerPath p2 = ExampleTracerPath.getBestPath(i2.getPaths());
	    	Set<ExampleTracerPath> bestPair = new HashSet<ExampleTracerPath>();
	    	bestPair.add(p1);
	    	bestPair.add(p2);
	    	ExampleTracerPath best = ExampleTracerPath.getBestPath(bestPair);
	    	if (trace.getDebugCode("et"))
	    		trace.out("et", "getBestPath("+p1+","+p2+") = "+(best == p2 ? "p2" : "p1"));
	    	return (best == p2 ? -1 : 1);
	    }
	    
	    private int breakByLowerLinkID(ExampleTracerInterpretation i1, ExampleTracerInterpretation i2) {
	    	Iterator links1 = i1.getMatchedLinks().iterator();
	    	Iterator links2 = i2.getMatchedLinks().iterator();
	    	
	    	ExampleTracerLink link1=null;
			ExampleTracerLink link2=null;
	    	
			
	    	while (links1.hasNext() && links2.hasNext()) {
	    		link1 = (ExampleTracerLink)links1.next();
	    		link2 = (ExampleTracerLink)links2.next();
	    		
	    		if (link1.getUniqueID()!=link2.getUniqueID())
	    			break;
	    	}
	    	
	    	/* Not testing for null as the list of matched links has to atleast contain one link*/
	    	if (link1.getUniqueID()<link2.getUniqueID())
	    		return -1;
	    	else
	    		return 1;
	    }
	} 

	/**
	 * Comparator to choose the better of 2 {@link ExampleTracerLink}s to suggest.
	 * @author sewall
	 */
	public class ExampleTracerLinkComparator implements Comparator<ExampleTracerLink> {
		
		/** Interpretation context for getting traversal counts, e.g. */
		private ExampleTracerInterpretation interp;

		/**
		 * Set the interpretation context for the link comparisons.
		 * @param interp
		 */
		public ExampleTracerLinkComparator(ExampleTracerInterpretation interp) {
			this.interp = interp;
		}

		/**
		 * Compare links pairwise. Criteria (in this order): <ol>
		 * <li>result of {@link ExampleTracerTracer#compareLinkTypes(String, String)};</li>
		 * <li>prefer the link with hints if one of the pair lacks them;</li>
		 * <li>prefer links higher (that is, earlier) in the path;</li>
		 * <li>prefer necessary links to optional links;</li>
		 * <li>if only 1 of the pair is {@link EdgeData#isPreferredEdge()}, choose it;</li>
		 * <li>choose the link preferred by {@link Matcher#compare(Matcher, Matcher)};</li>
		 * <li>choose the link with the lower {@link EdgeData#getUniqueID()}.</li>
		 * </ol>
		 * @param l1 1st link to compare
		 * @param l2 2nd link to compare
		 * @return -1 to prefer 1st, 1 to prefer 2nd
		 */
	    public int compare(ExampleTracerLink l1, ExampleTracerLink l2) {
	    	String t1 = l1.getType(), t2 = l2.getType();
	    	int m = compareLinkTypes(t1, t2);
	    	if (m != 0)
	    		return m;

	    	int h1 = nHints(l1, interp.getVariableTable());
	    	int h2 = nHints(l2, interp.getVariableTable());
			if (h1 > 0 && h2 <= 0)        // a link with no hints is deprecated
				return -1;
			else if (h1 <= 0 && h2 > 0)
				return 1;
			
			EdgeData e1 = l1.getEdge(), e2 = l2.getEdge();
			
			if (l1.getDepth() < l2.getDepth())
				return -1;                    // prefer links higher in the path
			else if (l1.getDepth() > l2.getDepth())
				return 1;
			
			int tc1 = interp.getTraversalCount(l1) - e1.getMinTraversals(); 
			int tc2 = interp.getTraversalCount(l2) - e2.getMinTraversals();
			if (tc1 < 0 && tc2 >= 0)
				return -1;               // prefer necessary over optional links
			else if (tc1 >= 0 && tc2 < 0)
				return 1;

			// links at same depth in same interpretation should be siblings?
			
			if (e1.isPreferredEdge())
	    		return -1;
	    	else if (e2.isPreferredEdge())
	    		return 1;
			
	    	if ((m = Matcher.compare(l1.getMatcher(), l2.getMatcher())) != 0)
	    		return m;                 // prefer more specific Matchers

	    	if (l1.getUniqueID() < l2.getUniqueID())
	    		return -1;
	    	else
	    		return 1;
	    }
	} 
	
	/**
	 * Attempt to find a link using the given sai or, if that fails,
	 * using {@link #getBestNextLink()}.
	 * This method resembles a hint request, but accepts the first selection
	 * element instead of pulling the previous focus from an ordinary hint
	 * request.
	 * @param result with {@link ExampleTracerEvent#getStudentSAI()} set
	 * @return CORRECT link that best matches selection, or result of 
	 *         {@link #getBestNextLink()}; can return null
	 */
	public ProblemEdge matchForHint(ExampleTracerEvent result) {
		Vector selection = (result.getStudentSAI() == null
				? null : result.getStudentSAI().getSelectionAsVector());
		if (selection != null && selection.size() > 0) {
			result.setWantReportableHints(true);  // to collect hints in result
			ProblemEdge link = traceForHint(result);
			if (link != null && result.getReportableHints().size() > 0) {
				link.getEdgeData().setInterpolatedHints(result.getReportableHints());
				return link;
			}
		}
		return getBestNextLink(true, result);  // no selection => nothing to match
	} 
	
	/**
	 * Attempt to find a link using the given sai or, if that fails,
	 * using {@link #getBestNextLink()}.
	 * This method resembles a hint request, but accepts the first selection
	 * element instead of pulling the previous focus from an ordinary hint
	 * request.
	 * @param wantHint if true, return null if the link chosen has no hints
	 * @return CORRECT link that best matches selection, or result of 
	 *         {@link #getBestNextLink()}; can return null
	 */
	public ProblemEdge getBestNextLink(boolean wantHint, ExampleTracerEvent result) {
		ProblemEdge link = null;

		ProblemNode[] endNode = new ProblemNode[1];
		ExampleTracerInterpretation[] interp = new ExampleTracerInterpretation[1];
		ExampleTracerLink etLink = getBestNextLink(wantHint, endNode, interp); 
		if (etLink == null) {   // means there's no next step
			trace.err("matchForHint(): getBestNextLink returns null; endNode "+endNode[0]);
			return null;
		}
		EdgeData edgeData = etLink.getEdge();
		if (edgeData == null) {   // means there's no next step
			trace.err("matchForHint(): getBestNextLink returns etLink "+etLink+" with null EdgeData");
			return null;
		}
		link = edgeData.getEdge();
		if (etLink.getType().equals(INCORRECT_ACTION)) {
			trace.err("matchForHint(): getBestNextLink returns INCORRECT "+link);
			return null;	// means there's no correct or suboptimal next step
		}
		else {
			Matcher m = edgeData.getMatcher();
			if (m instanceof SolverMatcher)
				((SolverMatcher) m).requestHint(result);
			else
				edgeData.interpolateHints(interp[0].getVariableTable());
			result.setReportableLink(etLink);
			return link;
		}
	}
	
	/**
	 * Compare 2 link types. The arguments should be Strings from this list:<ol>
	 * <li>{@link ExampleTracerTracer#CORRECT_ACTION} (best)</li>
	 * <li>{@link ExampleTracerTracer#SUBOPTIMAL_ACTION}</li>
	 * <li>{@link ExampleTracerTracer#INCORRECT_ACTION} (worst)</li>
	 * </ol>
	 * @param t1 one type
	 * @param t2
	 * @return -1 if t1 is preferred to t2; 1 if t2 is preferred; else 0
	 */
	public static int compareLinkTypes(String t1, String t2) {
		if (t1 == null)
			return (t2 == null ? 0 : 1);
		if (t2 == null)
			return -1;
		if (t1.equals(t2))
			return 0;
		if (t1.equals(ExampleTracerTracer.CORRECT_ACTION))
			return -1;
		else if (t2.equals(ExampleTracerTracer.CORRECT_ACTION))
			return 1;
		else if (t1.equals(ExampleTracerTracer.SUBOPTIMAL_ACTION))
			return -1;
		else if (t2.equals(ExampleTracerTracer.SUBOPTIMAL_ACTION))
			return 1;
		else if (t1.equals(ExampleTracerTracer.INCORRECT_ACTION))
			return -1;
		else if (t2.equals(ExampleTracerTracer.INCORRECT_ACTION))
			return 1;
		return 0;
	}

	/**
	 * Try to trace an attempt against the graph, but don't change the state of
	 * the tracer if you succeed.
	 * @param selection student selection
	 * @param action student action
	 * @param input student input
	 * @param actor whether student or tutor performed the action
	 * @return result of {@link #evaluate(ExampleTracerEvent)}
	 */
	public boolean tryEvaluate(Vector selection, Vector action, Vector input, String actor) {
		result = new ExampleTracerEvent(this);
		result.setStudentSAI(selection, action, input, actor);
		return evaluate(result, false, false);
	}
	/**
	 * Tell whether a link has been visited.
	 * @param uniqueID
	 * @return result of {@link ExampleTracerInterpretation#isVisited(int)
	 */
	public boolean isLinkVisited(int uniqueID) {
		boolean visited = false;
		if (interpretations != null && interpretations.size() > 0) {
			ExampleTracerInterpretation interp =getBestInterpretation();
			visited = interp.isVisited(uniqueID);
		}
		if (!visited) {
			Object linkMatch = incorrectActionMatches.get(new Integer(uniqueID));
			visited = (linkMatch != null);
		}
		return visited;
	}
	public String toString() {
		String s = "TracerState\n";
		for(ExampleTracerInterpretation interp : interpretations) {
			s+=interp.toString()+"\n";
		}
		return s;
	}
	public void setDemonstrateMode(boolean b) {
		//Switchting the mode may corrupt the state, so reset 
		if(isDemonstrateMode!=b) {
//			initialize();          sewall 8/29/08 CTAT2070: don't reset on change mode
			isDemonstrateMode = b;
		}
	}
	public boolean isDemonstrateMode(){
		return isDemonstrateMode;
	}
	public void extendPaths() {
		if(!isDemonstrateMode) {
			System.err.println("ExampleTracerTracer.extendPaths() was called when not in demonstrate mode");
			return;
		}
		for(ExampleTracerInterpretation interp : interpretations) {
			Set<ExampleTracerPath> paths = getAllPaths();		
			if (trace.getDebugCode("ET")) trace.out("ET", "extendPaths(): interp "+interp+", paths "+paths);
			interp.setPaths(paths);
		}
		bestInterpretation = null;  // paths changes rank
	}
	
	/**
	 * Count the interpretations
	 * @return {@link #interpretations}.size()
	 */
	public int getInterpretationCount() {
		return interpretations.size();
	}

	/**
	 * Propagate the given key, value pair to all {@link #interpretations}' variable tables.
	 * @param key
	 * @param value
	 */
	public void assignVariable(String key, Object value) {
		if (interpretations == null)
			return;
		for (ExampleTracerInterpretation interp : interpretations) {
			VariableTable vt = interp.getVariableTable();
			if (vt == null)
				continue;
			vt.put(key, value);
		}
	}
	
	public void addExampleTracerEventListener (ExampleTracerEventListener l){
		listeners.add(l);
	}
	
	public int getInstance(){
		return instance;
	}
	
	public void fireExampleTracerEvent (ExampleTracerEvent e){
		if (trace.getDebugCode("eti")) trace.outNT("eti","TracerTracer #"+instance+": fireExampleTraceEvent()");
		Iterator<ExampleTracerEventListener> i = listeners.iterator();
        while (i.hasNext()) {
            ExampleTracerEventListener listener = (ExampleTracerEventListener) i.next();
            listener.ExampleTracerEventOccurred(e);
        }   
	}
}
