/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;

/**
 * Variant of {@link Vector} for convenient access to elements of the
 * {@link #PROPERTYNAME} property of the "ChangeWMState" response.
 */
public class CheckLinksList extends Vector {
	
	/** Name for access via {@link MessageObject#getProperty(String name)}. */
	public static final String PROPERTYNAME = "checkLinksList";

	/**
	 * Constructor to create from an existing list. The new instance
	 * will be a deep copy of the source list.
	 * @param checkedLinksList source list
	 */
	private CheckLinksList(List checkedLinksList) {
		super();
		if (checkedLinksList == null)
			return;
		for (Iterator it = checkedLinksList.iterator(); it.hasNext(); ) {
			Vector checkedLink = (Vector) it.next();
			add(new Vector(checkedLink));
		}
	}
	
	/**
	 * Default constructor. Creates an empty list.
	 */
	CheckLinksList() {
		super();
	}
	
	/**
	 * Create an instance copied from the {@link #PROPERTYNAME} property
	 * in a Comm message.
	 * @param mo message to read
	 * @return instance created from value in mo; returns null if no
	 *         {@link #PROPERTYNAME} property was found
	 */
	public static CheckLinksList getCheckedLinksList(MessageObject mo) {
		Vector checkedLinksList =
			(Vector) mo.getProperty(PROPERTYNAME);
		//trace.out("checkedLinksList = " + checkedLinksList);
		
		if (checkedLinksList == null)
			return null;
		else
			return new CheckLinksList(checkedLinksList);
	}

    /**
     * Add a new element to the checkedLinksList vector.
     * @see #unpackCheckLink(Vector, int[], String[], Vector)
     * @param currentUniqueID linkID
     * @param checkResult result
     * @param ruleSeq sequence of rule names; omitted from result
     *        if null or empty
     * @return Vector created
     */
	void addLink(int currentUniqueID, String checkResult, List ruleSeq) {
		Vector singleCheckedLink = new Vector();
		singleCheckedLink.addElement(new Integer(currentUniqueID));
		singleCheckedLink.addElement(checkResult);
		if (ruleSeq != null && ruleSeq.size() > 0) {
			Vector ruleSeqCopy = new Vector(ruleSeq);
			singleCheckedLink.addElement(ruleSeqCopy);
			if (trace.getDebugCode("mtt"))
				trace.out("mtt", "addLink("+currentUniqueID+","+checkResult+","+ruleSeq+")");
		}
		add(singleCheckedLink);
	}

	/**
	 * Return the number of links in this list.
	 * @return {@link #size()}
	 */
	public int getNLinks() {
		return size();
	}
	
	/**
	 * Return the link identifier from the ith link in the path.
	 * @param linkIndex 0-based step (edge) number in path
	 * @return link id
	 */
	public int getLinkID(int linkIndex) {
		List linkElement = (List) get(linkIndex);
		Integer linkID = (Integer) linkElement.get(0);
		return linkID.intValue();
	}
	
	/**
	 * Return the checkResult from the ith link in the path.
	 * @param linkIndex 0-based step (edge) number in path
	 * @return checkResult
	 */
	public String getCheckResult(int linkIndex) {
		List linkElement = (List) get(linkIndex);
		String checkResult = (String) linkElement.get(1);
		return checkResult;
	}
	
	/**
	 * Return the rule sequence from the ith link in the path.
	 * @param linkIndex 0-based step (edge) number in path
	 * @return List of rule names; empty list if none
	 */
	public List getRuleSeq(int linkIndex) {
		List linkElement = (List) get(linkIndex);
		List ruleSeq = null;
		if (linkElement.size() > 2)
			ruleSeq = (List) linkElement.get(2);
		else
			ruleSeq = new Vector();
		return ruleSeq;
	}
        
        public String toString() {
            
            String printString = "";
            
            for (int i = 0; i < size(); i++) {
                String result = "[";
                result += getLinkID(i) + ": ";
                result += getCheckResult(i) + ", ";
                result += getRuleSeq(i) + "]";
                printString += result;
            }
            return printString; 
        }
}
