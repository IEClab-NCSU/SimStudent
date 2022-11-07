package edu.cmu.pact.ctat;

import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class Communicator extends ExternalObject implements Communicable { 
	ObjectProxy topProxy=null;
	protected String name;
	public static int messageNumber = 1;
	public static Hashtable allMessages = new Hashtable();
	public static boolean doSendError = true;
        
        public static void reset() {
            messageNumber = 1;
            allMessages = new Hashtable();
        }
	
	public Communicator() {
	}
	
	public Communicator(ObjectProxy top) {
		topProxy = top;
		name = topProxy.getName();
	}
	
	public String getName() {
		return name;
	}
	
	public ObjectProxy getTopObjectProxy(){
		return topProxy;
	}
	
	public static void addMessage(int index, MessageObject outEvent){
		allMessages.put(Integer.valueOf(String.valueOf(index)), outEvent);
                trace.out("missmto", "Communicator.allMessages.size() = " + allMessages.size());
	}
	
	public static MessageObject getMessage(int index){
		return (MessageObject)allMessages.get(Integer.valueOf(String.valueOf(index)));
	}
	
	public void setProxyObject(ObjectProxy top) {
		topProxy = top;
	}
	
	public void addTarget(Target reply) {
		if(topProxy != null) { 
			topProxy.addTarget(reply);	
		}
	}
	
	public MultiTarget getTarget() {
		if(topProxy != null)
			return topProxy.getTarget();
		else
			return null;
	}
	
	public void sendErrorMessage(Exception e, MessageObject msgParsed, String moString){
//		trace.out("sending error message for "+moString);
		MessageObject mo = MessageObject.create("sendErrorMessage");
		mo.setVerb("NoteError");
		int messageNum = -1;
		Integer parsedNum = (Integer)(msgParsed.getProperty("MESSAGENUMBER"));
		if (parsedNum == null)
			messageNum = getMessageNumber(moString);
		else
			messageNum = parsedNum.intValue();

		//if(e instanceof java.lang.ClassCastException ) 
		//e.printStackTrace();
		mo.setProperty("OBJECT",topProxy);
		mo.setProperty("InResponseTo", messageNum);
		String errorClass = e.getClass().getName();
		if ((errorClass.toUpperCase()).startsWith("COMM."))
			errorClass = errorClass.substring(7);
		mo.setProperty("ErrorClass", errorClass);
		mo.setProperty("ErrorText", e.getMessage()); 
		if(doSendError)
			topProxy.send(mo);	
		else if(MessageObject.showMessage)
			trace.out("ERROR "+mo.toString());
	}
	
	protected boolean isIgnorable(MessageObject mo, String exceptionName){
// chc		try{
			Vector ignore = (Vector) mo.getProperty("IGNOREERRORCLASSES");
			if(ignore == null) return false;
			int s = ignore.size();
			if(s == 1 && ((String)ignore.elementAt(0)).equalsIgnoreCase("ALL"))
				return true;
			String inVector = "";
			for(int i=0; i<s; i++){
				inVector = "COMM."+(String)ignore.elementAt(i);
				if(inVector.equalsIgnoreCase(exceptionName)) {
					return true;
				}
			}
			return false;
// chc		}catch (CommException ex){
// chc			return false;
// chc		}
	}
	
	private int getMessageNumber(String moString){
		int ind = (moString.toUpperCase()).indexOf("MESSAGENUMBER");
		if(ind == -1)
			return -1;
		ind = moString.indexOf(":", ind);
		ind = moString.indexOf(":", ind+1)+1;
		int end = moString.indexOf("&",ind);
		String numStr = moString.substring(ind,end);
		try{
			int toret = Integer.parseInt(numStr);
			return toret;
		} catch (NumberFormatException e){
			return -1;
		}
	}
	
	public void handleMessage(String inString) {
		//if(MessageObject.showMessage) 
		System.out.println (inString);
/*
		trace.out("mt", "RECEIVED MESSAGE by "+name+" : "+ inString);
		
		MessageObject inEvent = new MessageObject(inString, topProxy);
		//MessageObject inEvent = null;
		if (inEvent.getParseError() == null) {
			trace.out("mt", "Handling message");
			handleMessage(inEvent);
		}
		else if (!isIgnorable(inEvent, inEvent.getParseError().getClass().getName())) {
			trace.err("error in message format: " + inEvent.getParseError().getMessage());
			MessageFormatException me = new MessageFormatException("Message cannot be interpreted because "+
																	inEvent.getParseError().getMessage());
			sendErrorMessage(me,inEvent,inString);
		}
*/		
	}
	
	public void handleMessage(MessageObject inEvent){
		try{
//			char objType = inEvent.getObjectType("OBJECT");
//			String strDesc = inEvent.extractObjectValue("OBJECT");
//			Vector allObjDesc = topProxy.getDescription(strDesc);
//			topProxy.mailToProxy(inEvent, allObjDesc);
			Object objParam = inEvent.getProperty("OBJECT");
			String verb = inEvent.getVerb();
			trace.out("mt", "VERB = " + verb);
			if (objParam == null) {
				trace.err("Error: missing object parameter: " + inEvent.getPropertyNames());
				sendErrorMessage(new MissingParameterException("Object",(Vector) inEvent.getPropertyNames()),inEvent,inEvent.toString());
			} else if (verb == null) {
				trace.out (10, this, "Error: missing verb parameter");
				sendErrorMessage(new MissingParameterException("Verb",(Vector) inEvent.getPropertyNames()),inEvent,inEvent.toString());
			} else  {
				trace.out("mt", "object to treat response (class "+
						objParam.getClass().getName()+") is "+objParam);
				((ObjectProxy)objParam).treatMessage(inEvent,verb); //try and handle message
			}
		//catch any exceptions, since we never want to throw out of this method			
		
		} catch (MissingParameterException ex) {
		//} catch (Exception ex) {
		//} catch (MissingParameterException ex) {
			trace.out (5, this, "MissingParamterException e = " + ex);
			//sendErrorMessage(new DataFormatException("Error "+ex.getMessage()+" parsing message: "+inEvent.toString()),inEvent,inEvent.toString());
			sendErrorMessage(new DataFormatException("Internal error (missing parameter) "+ex+" handling: "+ inEvent.getParsedParameterString()),inEvent,inEvent.toString());
			//ex.printStackTrace();		
		} catch (CommException e) {
			trace.out (5, this, "Comm exception e = " + e);
			if(!isIgnorable(inEvent, e.getClass().getName()))
				sendErrorMessage(e,inEvent, inEvent.toString());
		}
		catch (Exception ex) {
			//sendErrorMessage(new DataFormatException("Error "+ex.getMessage()+" parsing message: "+inEvent.toString()),inEvent,inEvent.toString());
			sendErrorMessage(new DataFormatException("Internal error "+ex+" handling: "+inEvent.getParsedParameterString()),inEvent,inEvent.toString());
			ex.printStackTrace();		
		}
	}
}
