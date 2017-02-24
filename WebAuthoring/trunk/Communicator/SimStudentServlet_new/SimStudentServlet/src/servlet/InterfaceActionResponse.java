package servlet;
import interaction.SAI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Class representing a response to requests for grading an input.
 * @author Patrick Nguyen
 *
 */
public class InterfaceActionResponse extends ResponseMessage {

	private SAI sai;
	
	/**
	 * Gets the SAI
	 * @return sai
	 */
	public SAI getSai() {
		return sai;
	}
	
	/**
	 * Sets the sai
	 * @param sai
	 */
	public void setSai(SAI sai) {
		this.sai = sai;
	}
	
	/**
	 * Method to package up the entire class into a single XML string.
	 * This is the message sent back to the tutoring interface.
	 * 
	 * @return xml representation of this object
	 */
	public String toXML() 
	{
		if(sai==null)return hardCodedFile();
		String xml = "<message>";
		xml+=wrapInXML("verb",getVerb());
		xml+="<properties>";
		
		xml+=wrapInXML("MessageType",getMessageType());
		SAI sai1 = getSai();
		xml+=wrapInXML("Selection",sai1.getSelection());
		xml+=wrapInXML("Action",sai1.getAction());
		xml+=wrapInXML("Input",sai1.getInput());		
		xml+=wrapInXML("transaction_id",getTransactionID());
		
		xml+="</properties>";
		xml+= "</message>";
		return xml;
		
	}
	
	/*
	 * There is a hard coded response saved on disk used for demo purposes. This returns that response.
	 */
	private String hardCodedFile(){
		String xml="";
        try(
            BufferedReader bfr = new BufferedReader(new FileReader(new File(staticFiles+"/text_input.txt")));
        ){
            String temp="";
            while((temp=bfr.readLine())!=null)
              xml += temp;//we don't need to worry about readability
        }catch(Exception e)
        {
            xml="Hint io failed";
        }
        return xml;
	}

}
