package edu.cmu.pact.miss;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JOptionPane;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.RuleActivationNode;

public class ActivationList implements	Comparator<RuleActivationNode>,SortedSet<RuleActivationNode> {

	private TreeSet<RuleActivationNode> activationList;
	private List<RuleActivationNode> activationListUnsort;
	private SimSt simSt;
	
	public ActivationList(Vector list, SimSt ss)
	{
		simSt = ss;
		activationList = new TreeSet<RuleActivationNode>((Comparator)this);
		activationListUnsort = new LinkedList<RuleActivationNode>();
		for(int i=0;i<list.size();i++)
		{
			activationList.add((RuleActivationNode)list.get(i));	
			activationListUnsort.add((RuleActivationNode)list.get(i));			
		}
	}
	
	boolean sort = true;
	

	
	@Override
	public int compare(RuleActivationNode ran1, RuleActivationNode ran2) {
		if(sort)
		{
			String rule1Name = Rule.getRuleBaseName(ran1.getName()).replaceAll("MAIN::", "");
	    	Rule rule1 = simSt.getRule(rule1Name);
	    	//trace.out("ss", "RAN1: "+ran1.getName()+" "+ran1.getRuleFoas()+" "+rule1);
	    	double rule1Rating = rule1.getAcceptedRatio() +rule1.getSelectionAcceptRatio(ran1.getRuleFoas());
	
			String rule2Name = Rule.getRuleBaseName(ran2.getName()).replaceAll("MAIN::", "");
	    	Rule rule2 = simSt.getRule(rule2Name);
	    	//trace.out("ss", "RAN2: "+ran2.getName()+" "+ran2.getRuleFoas()+" "+rule2);
	    	double rule2Rating = rule2.getAcceptedRatio() +rule2.getSelectionAcceptRatio(ran2.getRuleFoas());
			
	    	if(rule1Rating < rule2Rating) return 1;
	    	if(rule1Rating > rule2Rating) return -1;
	    	if(rule1.getUses() < rule2.getUses()) return 1;
	    	if(rule1.getUses() > rule2.getUses()) return -1;
			int compare = rule1Name.compareTo(rule2Name);
			if(compare == 0 && !ran1.equals(ran2)) return 1;
			trace.out("ss", "Resort to alphabetic");
			return compare;
		}
		else
		{
			String rule1Name = Rule.getRuleBaseName(ran1.getName()).replaceAll("MAIN::", "");
	    	Rule rule1 = simSt.getRule(rule1Name);
	    	
			String rule2Name = Rule.getRuleBaseName(ran2.getName()).replaceAll("MAIN::", "");
	    	Rule rule2 = simSt.getRule(rule2Name);
	    	
	    	if(rule1.identity < rule2.identity) return 1;
	    	if(rule1.identity > rule2.identity) return -1;
			int compare = rule1Name.compareTo(rule2Name);
			if(compare == 0 && !ran1.equals(ran2)) return 1;
			return compare;
		}
	}
	
	public void printList()
	{
		System.out.println("--------------------------------------------");
		System.out.println("Activation List");
		Iterator<RuleActivationNode> iterator = iterator();
		//Iterator<RuleActivationNode> iterator = activationListUnsort.iterator();
		while(iterator.hasNext())
		{
			RuleActivationNode ran = iterator.next();
			String ruleName = Rule.getRuleBaseName(ran.getName()).replaceAll("MAIN::", "");
			Rule rule = simSt.getRule(ruleName);
			if(rule == null)
				JOptionPane.showMessageDialog(null, "Rule "+ruleName+" is null");
			int recencyValue = Rule.count;
			recencyValue -= (rule.identity+rule.getAcceptedUses());
			double ruleRating = rule.getAcceptedRatio()+rule.getSelectionAcceptRatio(ran.getRuleFoas());
			System.out.println(ruleName+": "+ruleRating+" ("+rule.getAcceptedRatio()+"+"+
					rule.getSelectionAcceptRatio(ran.getRuleFoas())+") "+rule.getAcceptedUses() + " ["+recencyValue+"]");
		}
		System.out.println("--------------------------------------------");
	}

	@Override
	public Comparator<RuleActivationNode> comparator() {
		return this;
	}

	@Override
	public RuleActivationNode first() {
		return activationList.first();
	}

	@Override
	public SortedSet<RuleActivationNode> headSet(RuleActivationNode toElement) {
		return activationList.headSet(toElement);
	}

	@Override
	public RuleActivationNode last() {
		return activationList.last();
	}

	@Override
	public SortedSet<RuleActivationNode> subSet(RuleActivationNode fromElement,
			RuleActivationNode toElement) {
		return activationList.subSet(fromElement, toElement);
	}

	@Override
	public SortedSet<RuleActivationNode> tailSet(RuleActivationNode fromElement) {
		return activationList.tailSet(fromElement);
	}

	@Override
	public boolean add(RuleActivationNode e) {
		return activationList.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends RuleActivationNode> c) {
		return activationList.addAll(c);
	}

	@Override
	public void clear() {
		activationList.clear();
	}

	@Override
	public boolean contains(Object o) {
		return activationList.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return activationList.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return activationList.isEmpty();
	}

	@Override
	public Iterator<RuleActivationNode> iterator() {
		return activationList.iterator();
		//return activationListUnsort.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return activationList.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return activationList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return activationList.retainAll(c);
	}

	@Override
	public int size() {
		return activationList.size();
	}

	@Override
	public Object[] toArray() {
		return activationList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return activationList.toArray(a);
	}

}
