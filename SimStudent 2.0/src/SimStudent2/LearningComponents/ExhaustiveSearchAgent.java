/**
 * Thu Apr 06 23:50:20 2006
 * 
 * Exhaustive Search Agent that deploys Exhaustive IDS
 * 
 * @author mazda
 * (c) Noboru Matsuda 2006-2014
 *
 */

package SimStudent2.LearningComponents;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import aima.basic.Agent;
import aima.search.framework.Metrics;
import aima.search.framework.Problem;
import aima.search.framework.Search;

public class ExhaustiveSearchAgent extends Agent {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	private List<String> actionList;
	private Metrics searchMetrics;
	private Search search;
	private Problem problem;

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getter & Setter
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public List<String> getActionList() { return actionList; }
	private void setActionList(List<String> actionList) { this.actionList = actionList; }

	public Problem getProblem() { return problem; }
	public void setProblem(Problem problem) { this.problem = problem; }

	public Search getSearch() {	return search; }
	public void setSearch(Search search) { this.search = search; }

	public Metrics getSearchMetrics() { return searchMetrics; }
	public void setSearchMetrics(Metrics searchMetrics) { this.searchMetrics = searchMetrics; }
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public ExhaustiveSearchAgent(Problem p, Search search) {
		setSearch(search);
		setProblem(p);
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	@SuppressWarnings("unchecked")
	public List<String> search() throws Exception {

		List<String> actionList = getSearch().search(getProblem());
		
		setActionList(actionList);
		setSearchMetrics(getSearch().getMetrics());
		
		/*
		int depthToStartFrom = getSearch().getLastDepth();
		AbstractQueue queueToStartFrom = getSearch().getQueue();
		setQueueAndDepthFromWhichToBeginSearch(queueToStartFrom, depthToStartFrom);
		
    	getGoalTest().setLastState(lastRhsState);
		getLastRhsState();
		*/
		
		return actionList;
	}

	@SuppressWarnings("rawtypes")
	public Properties getInstrumentation() {

		Properties retVal = new Properties();

		Iterator iter = searchMetrics.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			String value = searchMetrics.get(key);
			retVal.setProperty(key, value);
		}
		return retVal;
	}

	/*
	public void setQueueAndDepthFromWhichToBeginSearch(AbstractQueue queueToStartFrom, int depthToStartFrom) {
		
		String searchClassName = getSearch().getClass().getName();

		// Is there a native Java method to do this??
		int dotIdx = searchClassName.lastIndexOf(".");
		String classBaseName = searchClassName.substring(dotIdx + 1);

		//if using ExhaustiveIDS
		if (classBaseName.equals("ExhaustiveIDS")){
			ExhaustiveIDS search = (ExhaustiveIDS) getSearch();
			search.setQueueAndDepthFromWhichToBeginSearch(queueToStartFrom, depthToStartFrom);
		}
	}
	*/

}

