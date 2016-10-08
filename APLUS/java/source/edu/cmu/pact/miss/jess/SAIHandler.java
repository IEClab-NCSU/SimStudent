package edu.cmu.pact.miss.jess;

import java.util.ArrayList;

import edu.cmu.pact.Utilities.CTAT_Controller;

public abstract class SAIHandler {

	String selection, action, input;
	ModelTracer amt;
	
	//Vector returnMessage;
	ArrayList<String> returnMessage;
	
	private CTAT_Controller controller;
	
	int result=ModelTracer.NOT_APPLICABLE;
	
	public SAIHandler(String sel, String act, String inp, ModelTracer mt, CTAT_Controller controller){
		this.selection = sel;
		this.action = act;
		this.input = inp;
		this.amt = mt;
		this.controller = controller;
	}
	
	
	public abstract String processSAI();
	
	public abstract void sendResult();
	
	public CTAT_Controller getController(){
		return controller;
	}
}
