package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.Dialogs.RuleNamesDisplayDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemGraph;
import edu.cmu.pact.Utilities.trace;

/**
 * A container object for the information stored about a production rule within the Behavior Recorder graph.
 */
public class RuleProduction {
	
	/**
	 * A set {@link Map<String, RuleProduction>} of RuleProductions. Keys from
	 * {@link RuleProduction#getKey()}.
	 */
	public static class Catalog extends LinkedHashMap<String, RuleProduction> {

	    /**
	     * Find the rule with the given names in {@link #ruleProductionMap}.
	     * @param ruleName
	     * @param productionSet name
	     * @return case-insensitive matching entry from {@link #ruleProductionMap};
	     *         returns null if argument lacks the " " delimiter between ruleName and productionSet 
	     */
	    public RuleProduction getRuleProduction(String ruleName, String productionSetText) {
	    	return getRuleProduction(ruleName + " " + productionSetText);
	    }

	    /**
	     * Find the rule with the given display name in {@link #ruleProductionMap}.
	     * @param ruleNameProdutionSetText name in the form "<i>ruleName productionSetName</i>"
	     * @return case-insensitive matching entry from {@link #ruleProductionMap};
	     *         returns null if argument lacks the " " delimiter between ruleName and productionSet 
	     */
	    public RuleProduction getRuleProduction(String ruleNameProdutionSetText) {
	        if (ruleNameProdutionSetText.indexOf(" ") <= 0)  // not rule-productionSet form
	        	return null;
	        return get(ruleNameProdutionSetText.toLowerCase());
	    }

	    /**
	     * Add the given {@link RuleProduction} if no matching one is there. Calls
	     * {@link RuleNamesDisplayDialog#resetRuleProduction(boolean)} if adds.
	     * @param targetRuleProduction
	     * @param argument if target added; existing instance if key already found (no-op)
	     */
	    public RuleProduction checkAddRuleProduction(RuleProduction targetRuleProduction) {
	    	String key = targetRuleProduction.getKey();
	    	RuleProduction existing = get(key);
	    	if (existing != null)
		    	return existing;
	    	put(key, targetRuleProduction);
	    	return targetRuleProduction;
	    }

	    public RuleProduction checkAddRuleName(String newRuleNameText, String newProductionSet) {
	    	RuleProduction result = new RuleProduction(newRuleNameText, newProductionSet);
	    	return checkAddRuleProduction(result);
	    }
	    
	    public void checkAddRuleProblem(String ruleNameProdutionSetText,
	            Map<String, RuleProduction> problemRuleProductionList) {

	    	// checking the existing rules

	        int temp = ruleNameProdutionSetText.indexOf(" ");

	        // not rule-productionSet form
	        if (temp <= 0)
	            return;
	        if (problemRuleProductionList.get(ruleNameProdutionSetText.toLowerCase()) != null)
	        	return;
	        problemRuleProductionList.put(ruleNameProdutionSetText,
	        		new RuleProduction(ruleNameProdutionSetText));
	    }

	    /**
	     * Ensure the {@link RuleProduction#getOpportunityCount()} values are up-to-date.
	     * Revises entries according to skills on links in the given preferred path.
	     * @param preferredPathEdges the preferred path
	     */
	    public void updateOpportunityCounts(List<ProblemEdge> preferredPathEdges) {
	    	if (trace.getDebugCode("pm")) trace.out("pm", "updateOpportunityCounts() preferredPath "+preferredPathEdges);
	    	clearOpportunityCounts();
	    	for (ProblemEdge edge : preferredPathEdges) {
	    		EdgeData edgeData = edge.getEdgeData();
	    		List<String> ruleNames = null;
	    		if (edgeData == null || (ruleNames = edgeData.getRuleNames()) == null)
	    			continue;
	        	if (trace.getDebugCode("pm")) trace.out("pm", "updateOpportunityCounts() link "+edge+" rules "+ruleNames);
	    		for (String ruleName : ruleNames) {
	    			if (!RuleProduction.isRealRuleName(ruleName))
	    				continue;
	    			RuleProduction rp = getRuleProduction(ruleName);
	    			if (rp == null) {
	    				rp = new RuleProduction(ruleName);
	    				addRuleProduction(rp);
	    			}
	    			rp.incrementOpportunityCount(1);
	    		}
	    	}
	    	if (trace.getDebugCode("pm")) trace.out("pm", "updateOpportunityCounts() map result "+this);
	    }

	    /**
	     * Zero all {@link RuleProduction#getOpportunityCount()} values.
	     */
	    private void clearOpportunityCounts() {
	    	for (RuleProduction rp : values())
	    		rp.setOpportunityCount(0);
		}

	    /**
	     * Insert the given instance into {@link #ruleProductionMap}. Replaces existing item with
	     * given one if matching key found.
	     * @param ruleProduction
	     * @return true if this {@link RuleProduction#getKey()} is new;
	     *         false if a matching one was replaced
	     */
	    public boolean addRuleProduction(RuleProduction ruleProduction) {
	    	RuleProduction oldValue = put(ruleProduction.getKey(), ruleProduction);
	        return oldValue == null;
	    }

		/**
		 * Get a deep copy of {@link #ruleProductionMap}.
		 * @return contents of {@link #ruleProductionMap}, as List
		 */
		public List<RuleProduction> getRuleProductionList() {
			return getRuleProductionList(false);
		}

		/**
		 * Get a deep copy of {@link #ruleProductionMap}.
		 * @param omitUnnamed if true, omit entries where {@link RuleProduction#isUnnamed()}
		 *         is true; if false, return all entries
		 * @return contents of {@link #ruleProductionMap}, as List
		 */
		public List<RuleProduction> getRuleProductionList(boolean omitUnnamed) {
			return getRuleProductionList(omitUnnamed, false);
		}

		/**
		 * Get a deep copy of {@link #ruleProductionMap} values.
		 * @param omitUnnamed if true, omit entries where {@link RuleProduction#isUnnamed()}
		 *         is true; if false, return all entries
		 * @param omitUncounted if true, omit entries where
		 *         {@link RuleProduction#getOpportunityCount()} is zero or null	
		 * @return contents of {@link #ruleProductionMap}, as List
		 */
		public List<RuleProduction> getRuleProductionList(boolean omitUnnamed, boolean omitUncounted) {
			List<RuleProduction> result = new ArrayList<RuleProduction>();
			for (RuleProduction rp : values()) {
				if (omitUnnamed && rp.isUnnamed())
					continue;
				if (omitUncounted) {
					Integer count = rp.getOpportunityCount();
					if (count == null || count.intValue() < 1)
						continue;
				}
				result.add(rp.clone());
			}
			return result;
		}

		/**
		 * Get a list of all rule names, as from
		 * {@link #getRuleDisplayNames(boolean) getRuleDisplayNames(false)}.
		 * @return list of strings in the form "<i>ruleName productionSet</i>"
		 */
		public List<String> getRuleDisplayNames() {
			return getRuleDisplayNames(false);
		}

		/**
		 * Get a list of rule names ({@link RuleProduction#getDisplayName()}) from
		 * {@link #ruleProductionMap}.
		 * @param omitUnnamed if true, omit entries where {@link RuleProduction#isUnnamed()}
		 *         is true; if false, return all entries
		 * @return list of strings in the form "<i>ruleName productionSet</i>"
		 */
		public List<String> getRuleDisplayNames(boolean omitUnnamed) {
			List<String> result = new ArrayList<String>();
			for (RuleProduction rp : values()) {
				if (omitUnnamed && rp.isUnnamed())
					continue;
				result.add(rp.getDisplayName());
			}
			return result;
		}

		/**
		 * Remove from {@link #ruleProductionMap} those entries no longer on any {@link EdgeData}.
		 * @param problemGraph graph to scan
		 * @return subset Catalog containing only entries on given graph's links; 
		 *         result is a deep copy, independent of this Catalog 
		 */
		public Catalog removeUnusedRuleProductions(ProblemGraph problemGraph) {
			if (problemGraph == null)
				return null;
			Set<String> keysInUse = new HashSet<String>(); 
			Enumeration<ProblemEdge> en = problemGraph.edges();
			boolean foundRealEdge = false;
			while (en.hasMoreElements()) {
				ProblemEdge edge = en.nextElement();
				if (edge.getUniqueID() < 0)      // this edge added by rule tracing
					continue;
				foundRealEdge = true;
				for (String key : edge.getEdgeData().getRuleNames())
					keysInUse.add(key.toLowerCase());
			}
			if (!foundRealEdge)                  // happens when tracing from rules 
				return this;

			Catalog result = new Catalog();
			for (String key : keysInUse) {
				if (!RuleProduction.isRealRuleName(key))
					continue;
				RuleProduction oldRP = get(key);
				if (oldRP == null)
					result.put(key, new RuleProduction(key));
				else
					result.put(key, oldRP.clone());
			}
			return result;
		}
	}
	
	/**
	 * Number of times student has an opportunity to demonstrate this skill while solving.
	 * Can be null if there's no opportunity on the preferred path.
	 */
	private Integer opportunityCount;
	
	/** The skill name. */
    private String ruleName;

    /** The skill set name. */
    private String productionSet;

    private String productionRule;

    private Vector<String> hints;

	private String label;

	private String description;
	
	/** Value for the {@link #ruleName} of a skill or rule not yet specified. */
    public static final String UNNAMED = "unnamed";
    
    /**
     * For debugging.
     * @return string with fields
     */
    public String toString() {
    	return "[" + getDisplayName() + " " + getOpportunityCount() + "]";
    }
    
    /**
     * Return a copy.
     * @return clone of this object
     */
    public RuleProduction clone() {
    	RuleProduction result = new RuleProduction();
    	result.opportunityCount = opportunityCount;
    	result.ruleName = ruleName;
    	result.productionSet = productionSet;
    	result.productionRule = productionRule;
    	result.label = label;
    	result.description = description;
    	result.hints = (Vector<String>) hints.clone();
    	return result;
    }

    public RuleProduction() {
    	this("", "");
    }

    /**
     * Equivalent to {@link #RuleProduction(String, String) RuleProduction(displayName, null)}
     * @param displayName split off the {@link #productionSet} as the first word of this string;
     *                    the rest is the {@link #ruleName}
     */
    public RuleProduction(String displayName) {
    	this(displayName, null);
    }

    /**
     * Create an instance with the given 2-part name.
     * @param ruleName rule or skill name; if empty or null, uses {@link #UNNAMED}
     * @param productionSet rule set or skill group name; if null, uses last word of ruleName
     */
    public RuleProduction(String ruleName, String productionSet) {
        this.ruleName = (ruleName == null || ruleName.length() < 1 ? UNNAMED : ruleName);
    	this.productionSet = "";
        if (productionSet != null)
        	this.productionSet = productionSet;
    	else {
        	int p = ruleName.indexOf(" ");
        	if (p >= 0) {
        		this.ruleName = ruleName.substring(0, p);
        		this.productionSet = ruleName.substring(p+1);
        	}
        }
        productionRule = "";
        hints = new Vector<String>();
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getProductionSet() {
        return productionSet;
    }

    public void setHints(Vector<String> hints) {
        this.hints = (Vector<String>) hints.clone();
        if (trace.getDebugCode("br")) trace.out("br", "RuleProduction.setHints()\n"+dumpHints());
    }

    /**
     * Format the {@link #hints} for debugging.
     * @return hints, one to a line, numbered from 1; empty string if null or empty
     */
    public String dumpHints() {
    	if (hints == null || hints.size() < 1)
    		return "";
    	StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hints.size(); i++) {
        	sb.append(i+1 < 10 ? " ":"").append(i+1).append(". ").append(hints.get(i));
        	if (i+1 < hints.size())
        		sb.append('\n');
        }
		return sb.toString();
	}

	public Vector<String> getHints() {
        return hints;
    }

    public void addHintItem(String hintItem) {
        this.hints.addElement(hintItem);
    }

	/**
	 * Number of times student has an opportunity to demonstrate this skill while solving.
	 * Can be null if there's no opportunity for this skill on the preferred path.
	 * @return the {@link #opportunityCount}
	 */
	public Integer getOpportunityCount() {
		return opportunityCount;
	}

	/**
	 * @param opportunityCount new value for {@link #opportunityCount}
	 */
	void setOpportunityCount(Integer opportunityCount) {
		this.opportunityCount = opportunityCount;
	}

	/**
	 * Value for looking this RuleProduction up in a map.
	 * @return {@link #getDisplayName()}.toLowerCase()
	 */
	public String getKey() {
		return getDisplayName().toLowerCase();
	}

	/**
	 * Name for display on skillometer, graph labels, etc.
	 * @return "{@link #ruleName} + ' ' + {@link #productionSet}"
	 */
	public String getDisplayName() {
		if (productionSet != null && productionSet.length() >  0)
			return getRuleName() + " " + productionSet;
		else
			return getRuleName();
	}

	/**
	 * @return the {@link #productionRule}
	 */
	public String getProductionRule() {
		return productionRule;
	}

	/**
	 * @param productionRule new value for {@link #productionRule}
	 */
	public void setProductionRule(String productionRule) {
		this.productionRule = productionRule;
	}

	/**
	 * Alter the {@link #opportunityCount} by the given increment.
	 * @param delta increment (if negative, decrement)
	 * @return revised opportunityCount value
	 */
	public int incrementOpportunityCount(int delta) {
		if (opportunityCount == null)
			opportunityCount = new Integer(delta);
		else
			opportunityCount = new Integer(opportunityCount.intValue()+delta);
		return opportunityCount.intValue();
	}

	/**
	 * @return true if {@link #getDisplayName()} matches {@value #UNNAMED}
	 */
	public boolean isUnnamed() {
		return UNNAMED.equalsIgnoreCase(getDisplayName());
	}

	/** 
	 * Tell if a rule name satisfies the requirements of a real rule name.
	 * @param displayName name with productionSet
	 * @return false if displayName matches {@link #UNNAMED} or lacks a space (which is
	 *         the {@link #productionSet} delimiter)
	 */
	public static boolean isRealRuleName(String displayName) {
		if (UNNAMED.equalsIgnoreCase(displayName))
			return false;
		return (displayName.indexOf(" ") > 0);
	}

	/**
	 * @param label new value for {@link #label}.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the {@link #label}
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param description new value for {@link #description}.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return {@link #description}
	 */
	public String getDescription() {
		return description;
	}
}
