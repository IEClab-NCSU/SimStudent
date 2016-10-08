package edu.cmu.pact.miss.jess;

import java.util.HashMap;

import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.miss.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ProactiveMTController {

	AplusController amt;
	AplusController getAmt(){return amt;}
	void setAmt(AplusController amt){this.amt=amt;}
	
	
	BR_Controller brController;
	private void setBrController(BR_Controller brController){this.brController=brController;}
	private BR_Controller getBrController(){return this.brController;}
	public static final String PROACTIVE_MESSAGE_PREFIX="P";
	public static final String FOLLOWUP_MESSAGE_PREFIX="F";
	
	
	/*Occurence count for each production rule*/
	HashMap<String, Integer> occurenceCount; 
	private int addOccurence(String rulename){
		if (!occurenceCount.containsKey(rulename))
			occurenceCount.put(rulename, new Integer(1));
		else{
			int val=occurenceCount.get(rulename);
			val=val+1;
			System.out.println("val is " + val);
			occurenceCount.put(rulename,val++);
		}
		
		return occurenceCount.get(rulename);
	}

	
	
	public ProactiveMTController(BR_Controller brController){
		setBrController(brController);
		setAmt(brController.getAmt());
		occurenceCount= new HashMap<String, Integer>();
	}
	

	
	/**
	 * Method to trigger pro-actively give hint message
	 * @param rulename
	 * @param sel
	 * @param act
	 * @param inp
	 * @return
	 */
	boolean processRule(String rulename, Vector messages){
		
		if (isTimeToBeProactive(rulename)){
			/*get the proactive the message part */
			ArrayList<String> returnMessage=getProperMessage(messages);
			/*show the message*/
			this.getBrController().getMissController().getAPlusHintMessagesManager().setMessages(returnMessage);
			String message = this.getBrController().getMissController().getAPlusHintMessagesManager().getFirstMessage();
			if(this.getBrController().getMissController() != null && this.getBrController().getMissController().getSimStPLE() != null
					&& this.getBrController().getMissController().getSimStPLE().getSimStPeerTutoringPlatform() != null) {
				this.getBrController().getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getAPlusHintDialogInterface().showMessage(message);
			}
			
			//todo : logging should be added here....
			/**/
			//getAmt().setProactiveHintGiven(true);
			
		}
		
		return true;
	}
	
	
	/**
	 * Method to get the appropriate message
	 * @param messages
	 * @return
	 */
	private ArrayList<String> getProperMessage(Vector<String> messages){
		ArrayList<String> returnMessage=new ArrayList<String>();
		String message = "";
		for(int i=0; i< messages.size(); i++){
			if(returnMessage == null){
				returnMessage = new ArrayList<String>();
			}

			if (messages.get(i).startsWith(PROACTIVE_MESSAGE_PREFIX)){
				message="";
				message += (String)messages.get(i)+";";
			
				returnMessage.add(clean(message));
			}
			
		}
		return returnMessage;
	}
	
	
	/**
	 * Method to return if method must be shown
	 * @param rulename
	 * @return
	 */
	private boolean isTimeToBeProactive(String rulename){
		boolean returnValue=false;
		
		int timesOccuredSoFar=addOccurence(rulename);
				
		if (timesOccuredSoFar<2)
			returnValue=false;
		else if (timesOccuredSoFar==2){
			returnValue=true;
		}
		else{
			if (Math.random() < .5)
				returnValue = true;
		}
		
		
		return returnValue;
	}
	
	
	/*utility method to clean up string. */
	public static String clean(String message){
		String returnMessage;
		returnMessage=message.substring(1,message.length());
		returnMessage=returnMessage.replace("[", "");
		returnMessage=returnMessage.replace("]", "");
		returnMessage=returnMessage.replace(";", "");
		return returnMessage;
	}
	
	
}
