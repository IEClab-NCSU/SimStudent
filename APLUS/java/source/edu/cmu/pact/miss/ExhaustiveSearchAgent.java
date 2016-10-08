package edu.cmu.pact.miss;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import aima.basic.Agent;
import aima.search.framework.Metrics;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.util.AbstractQueue;

/**
 * @author mazda
 *
 */
public class ExhaustiveSearchAgent extends Agent {
    
    private List actionList;
    private Metrics searchMetrics;
    private Search search;
    private Problem problem;

    public ExhaustiveSearchAgent(Problem p, Search search) {
	setSearch(search);
	setProblem(p);
    }
	
    public List search() throws Exception {
        
        actionList = getSearch().search(getProblem());
        searchMetrics = search.getMetrics();
        
        return actionList;
    }
    
    public Properties getInstrumentation() {

        Properties retVal = new Properties();

        Iterator iter = searchMetrics.keySet().iterator();
	while (iter.hasNext()) {
	    String key = (String) iter.next();
	    String value = searchMetrics.getString(key);
	    retVal.setProperty(key, value);
	}
	return retVal;
    }

    public List getActionList() {
        return actionList;
    }

    public void setActionList(List actionList) {
        this.actionList = actionList;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public Metrics getSearchMetrics() {
        return searchMetrics;
    }

    public void setSearchMetrics(Metrics searchMetrics) {
        this.searchMetrics = searchMetrics;
    }

    public void setQueueAndDepthFromWhichToBeginSearch(AbstractQueue queueToStartFrom, int depthToStartFrom) {
        Class searchClass = getSearch().getClass();
        
        //if using ExhaustiveIDS
        if (searchClass.toString().indexOf("ExhaustiveIDS") != -1){
            ExhaustiveIDS search = (ExhaustiveIDS) getSearch();
            search.setQueueAndDepthFromWhichToBeginSearch(queueToStartFrom, depthToStartFrom);
        }
    }
    
}

