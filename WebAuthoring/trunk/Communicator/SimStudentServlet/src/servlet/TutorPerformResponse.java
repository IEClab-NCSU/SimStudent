package servlet;

import java.util.ArrayList;

import interaction.SAI;
/**
 * Class to wrap up interface action response preformed by the tutor
 * @author Patrick Nguyen
 *
 */
public class TutorPerformResponse extends InterfaceActionResponse{
	private String trigger;
	private String subtype;
	
	ArrayList<String> hintList=null;
	
	void setHintMessages(ArrayList hints){this.hintList=hints;}
	
	ArrayList<String> getHintMessages(){return this.hintList;}
	public TutorPerformResponse(){
		setMessageType("InterfaceAction");
	}

	public String getTrigger() {
		return trigger;
	}


	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}


	public String getSubtype() {
		return subtype;
	}


	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}


	String feedbackMessage;
	public void setHint(String hint){	
		this.feedbackMessage=hint;
	}
	
	public String getHint(){return this.feedbackMessage;}
	

	
	
	
	public String toXML(){
		String xml = "<message>";
		xml+=wrapInXML("verb",getVerb());
		xml+="<properties>";
		
		xml+=wrapInXML("MessageType",getMessageType());
		
		SAI sai1 = getSai();
		if (sai1!=null){
			xml+=wrapInXML("Selection",sai1.getSelection());
			xml+=wrapInXML("Action",sai1.getAction());
			xml+=wrapInXML("Input",sai1.getInput());
		}		
		
		if (hintList!=null){
			
			for (String hint: hintList){
				
				xml+=wrapInXML("HintsMessage",wrapInXML("value",hint));
			}
				
		}
		
		
		
		xml+=wrapInXML("trigger",getTrigger());
		xml+=wrapInXML("subtype",getSubtype());
		xml+=wrapInXML("transaction_id",getTransactionID());
		
		xml+="</properties>";
		xml+= "</message>";
		return xml;
	}
}
