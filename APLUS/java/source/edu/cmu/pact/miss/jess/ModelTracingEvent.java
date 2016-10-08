package edu.cmu.pact.miss.jess;

import java.util.ArrayList;
import java.util.Vector;

import edu.cmu.pact.jess.RuleActivationNode;


public class ModelTracingEvent extends java.util.EventObject {
	public int modelTracingResult;
	public RuleActivationNode node;
	public Vector<String> message;
	public String selection;
	public String action;
	public String input;
	
	public ModelTracingEvent(Object source, String selection, String action, String input, int modelTracingResult, RuleActivationNode node, Vector<String> message) {
		super(source);
		this.modelTracingResult = modelTracingResult;		
		this.node=node;
		this.message=message;
		this.selection=selection;
		this.action=action;
		this.input=input;
	}
	
}
