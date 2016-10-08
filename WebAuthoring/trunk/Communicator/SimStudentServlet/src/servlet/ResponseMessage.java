package servlet;
import java.util.List;

/**
 * Abstract class representing an arbitrary response message.
 * @author Patrick Nguyen
 *
 */
public abstract class ResponseMessage {
	static String staticFiles;//used for the hard coded files only
	private String messageType;
	private String verb = "DUMMY";
	private String transactionID;

	/**
	 * Gets the message type
	 * @return messageType - The type of message this object represents
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * Sets the message type
	 * @param messageType - The type of message this object represents
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	/**
	 * Gets the verb.
	 * 
	 * Note that verb is now ignored by the tutoring interface.
	 * @return verb
	 */
	public String getVerb() {
		return verb;
	}
	
	/**
	 * Sets the verb.
	 * 
	 * Note that verb is now ignored by the tutoring interface.
	 * @param verb
	 */
	public void setVerb(String verb){
		this.verb=verb;
	}
	
	/**
	 * Gets the transaction ID
	 * @return transactionID
	 */
	public String getTransactionID() {
		return transactionID;
	}

	/**
	 * Sets the transaction ID
	 * @param transactionID
	 */
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	
	/*
	 * Helper method for toXML()
	 */
	protected String wrapInXML(String name,String val){
		return "<"+name+">"+val+"</"+name+">";
	}
	
	/*
	 * Helper method for toXML()
	 */
	protected String wrapInXML(String name, List<String> vals){
		String s="<"+name+">";
		for(String t:vals)
			s+="<value>"+t+"</value>";
		s+="</"+name+">";
		return s;
	}
	
	/*
	 * Method to package up the entire class into a single XML string.
	 * This is the message sent back to the tutoring interface.
	 */
	abstract String toXML();
}
