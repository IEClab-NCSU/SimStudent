package edu.cmu.pact.miss;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.RuleActivationNode;

public class QuizStateSuccessorFn implements SuccessorFunction {

	public static final String COMM_STEM = "commTable";
	
	// Need to cache the successors as it takes long time to create successive states
	// using the activationList and it does not change for the particular problem and 
	// a particular state. 
	HashMap successorStateCache = new HashMap();
	
	/*	Given a QuizState this finds all the successors using the activation list.
	 * (non-Javadoc)
	 * @see aima.search.framework.SuccessorFunction#getSuccessors(java.lang.Object)
	 */
	@Override
	public List getSuccessors(Object arg0) {
		// TODO Auto-generated method stub
		
		List successors = new ArrayList();

		// Current QuizState
		QuizState current = (QuizState) arg0;
		
		String probNode = ""+current.getProbNode();
		// If the successors have already been found then get them and return without 
		// going through the activationList steps again which is time consuming
		if(successorStateCache.containsKey(probNode)) {
			if(trace.getDebugCode("miss"))trace.out("miss", "No need to expand the state using the activationList");
			successors = (List) successorStateCache.get(probNode);
			print(successors);
			return successors;
		}

		// If the QuizState is a done state no need to expand it further
		if(current.isDoneStep() || current.equals(null)) {
			return successors;
		}
		
		// Create new QuizStates using the activationList. Who sends it the activationList
		long startTime = System.currentTimeMillis();
		Vector activationList = current.getSsInteractiveLearning().getSimSt().gatherActivationList(current.getProbNode());
		long duration = (System.currentTimeMillis() - startTime);
		if(trace.getDebugCode("miss"))trace.out("miss", "ActivationList took  " + duration +  "  ms.");
		print(activationList);
		
		// If there is no activation for a given state simply return
		if(activationList.size() > 0) {
			// Make a new state for each of the activationList and put it into the List
			Vector /*successor Children*/ children = new Vector();
			children = makeChildStatesUsingActivations(current, activationList);
		
			for(int i=0; i< children.size(); i++) {
				successors.add(new Successor(activationList.elementAt(i).toString(), children.elementAt(i)));
			}
		} else {
			if(trace.getDebugCode("miss"))trace.out("miss", "No activation for the current quiz state: " + current);
		}
	
		// Put the expanded states into the HashMap for faster retrieval later
		successorStateCache.put(probNode, successors);
		//print(successors);
		return successors;
	}
	
	/* Utility function */ 
	void print(List successor) {
    	for(int i=0; i< successor.size(); i++)
    		if(trace.getDebugCode("miss"))trace.out("miss","activationList at index  " + i + "  is" + successor.get(i));		
	}
	
	private Vector makeChildStatesUsingActivations(QuizState current, Vector activationList) {
		
		
		Vector children = new Vector();
		
		for(int i=0; i< activationList.size(); i++) {
			RuleActivationNode ran = (RuleActivationNode) activationList.get(i);
			
			if(ran.getActualInput().equalsIgnoreCase("FALSE"))
				continue;
			
			QuizState child = createStateUsingRuleActivation(current, ran);
			if(child != null)
				children.add(child);			
		}
		
		return children;
	}

	private QuizState createStateUsingRuleActivation(QuizState current, RuleActivationNode ran) {
		
		QuizState succState = null;
		ProblemNode nextCurrentNode = null;
		
		ProblemNode currentNode = current.getProbNode();
		// Make sure that the currentNode is the current node
        current.getSsInteractiveLearning().secureCurrentNode(currentNode);

		Sai sai = new Sai(ran.getActualSelection(), ran.getActualAction(), ran.getActualInput());
		if(sai.getS().equals("NotSpecified") || sai.getI().equals("NotSpecified"))
			return null;
		
		// The node we get if we run this step
		ProblemNode successiveNode = current.getSsInteractiveLearning().simulatePerformingStep(currentNode, sai);
		
		if(successiveNode != null) {
			// Create a new QuizState succState using the new ProblemNode i.e. successiveNode
			succState = new QuizState(current.getSimStPLE(), current.getSsInteractiveLearning(), current.getOriginalEqn());
			
			String succStateStep = "";
	
			// Done is a special step treated differently
			if(!sai.getS().equals("done")) {
				String table = sai.getS().substring(COMM_STEM.length(), COMM_STEM.length()+1);
				int tableIndex = Integer.parseInt(table);
			
				// Update the currentStep and the previousStep for the new QuizState succState
				switch(tableIndex) {
				case 1:
					if(!current.isTypeInStep()){
						succStateStep = sai.getI();
					} else {
						String step = sai.getI() + " = " + current.getCurrentStep();
						succStateStep = step;
					}
					succState.setCurrentStep(succStateStep);
					succState.setPreviousStep(current.getCurrentStep());
					succState.setProbNode(successiveNode);
					succState.setTypeInStep(true);
					break;
				case 2:
					if(!current.isTypeInStep()){
						succStateStep = sai.getI();
					} else {
						String step = current.getCurrentStep() + " = " + sai.getI();
						succStateStep = step;
					}
					succState.setCurrentStep(succStateStep);
					succState.setPreviousStep(current.getCurrentStep());
					succState.setProbNode(successiveNode);
					succState.setTypeInStep(true);
					break;
				case 3:
					succStateStep = current.getCurrentStep();
					succStateStep += " [" + sai.getI() + "]";
					succState.setCurrentStep(succStateStep);
					succState.setPreviousStep(current.getCurrentStep());
					succState.setProbNode(successiveNode);
					succState.setTypeInStep(false);
					break;
				default:
					break;
				}
			} else {
				// When it is a done step then the currentStep is set to the same as that
				// of the previous state but the boolean isDoneStep is set to true
				//succStateStep = current.getCurrentStep() + " [" + "Done" + "]";
				succStateStep = current.getCurrentStep();
				succState.setDoneStep(true);
				succState.setCurrentStep(succStateStep);
				succState.setPreviousStep(current.getCurrentStep());
				succState.setProbNode(successiveNode);
				succState.setTypeInStep(false);
	 		}

		}
		
		return succState;
	}
}
