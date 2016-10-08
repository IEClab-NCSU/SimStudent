package edu.cmu.old_pact.dormin;

/**
* CommonObjectProxy supports messages, common for tools and tutors.
**/

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class CommonObjectProxy extends ObjectProxy {
	
	public CommonObjectProxy(){
		super();
	}
	
	public CommonObjectProxy(	String type, 
						String name, 
						ObjectProxy parent) {
		this(type, name, parent, null, -9999);
	}
	public CommonObjectProxy(	String type, 
						ObjectProxy parent, 
						String id) {				
		this(type, null, parent, id, -9999);
	}
	
	public CommonObjectProxy(	String type, 
						ObjectProxy parent,
						int position) {			
		this(type, null, parent, null, position);
	}
	
	public CommonObjectProxy(	ObjectProxy parent, 
						String type){
		this(type, null, parent, null, -9999);
	}
	
	public CommonObjectProxy(String type){
		this(type, null, null, null, -9999);
	}
	
	public CommonObjectProxy(	String type, 
						String name,
						ObjectProxy parent,
						String id, 
						int position) {
		super(type, name, parent, id, position);
	} 

	public void treatMessage(MessageObject mo, String inVerb) throws DorminException{
		inVerb = inVerb.toUpperCase();
		boolean completed = false;
		trace.out (20, this, "treatMessage: inVerb = " + inVerb);
		
		if(inVerb.equalsIgnoreCase("SETPROPERTY")){
			try{
				setProperty(mo);
				completed = true;
			}catch (DorminException e){
				throw e;
			}
		}
		
		else if(inVerb.equalsIgnoreCase("GETPROPERTY")){
			try{
				getProperty(mo);
				completed = true;
			}catch (DorminException e){
				throw e;
			}
		}
		else if(inVerb.equalsIgnoreCase("STARTOBSERVING")){
			startObserving(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("STARTPROBLEM")){
			startProblem(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("STOPOBSERVING")){
			stopObserving(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("SHOWMESSAGE")){
			showMessage(mo);
			completed = true;
		}
		
		else if(inVerb.equalsIgnoreCase("RESPONSETOGETPROPERTY")){
			responseToGetProperty(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("RESPONSETOALLPROPERTIES")){
			responseToAllProperties(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("ALLPROPERTIES")){
			allProperties(mo);
			completed = true;
		}
		else if(inVerb.equalsIgnoreCase("RESEND")){
			resendMessage(mo);
			completed = true;
		}
		else {
			trace.out(10, this, "The verb " + inVerb + " does not exist in the object " + type);
			throw new NoSuchVerbException("The verb " + inVerb + " does not exist in the object " + type);
		}
	}
	
	public void resendMessage(MessageObject mo){
		try{
			int messageNum = mo.extractIntValue("OLDMESSAGENUMBER");
			MessageObject oldMessage = Communicator.getMessage(messageNum);
			send(oldMessage);
		}catch (DorminException e) { }
	}
	
	public void getProperty(MessageObject mo) throws DorminException{
		try{
			Vector propertyNames = mo.extractListValue("PROPERTYNAMES");
			Hashtable properties = ((Sharable)getObject()).getProperty(propertyNames);
			Vector proNames = new Vector();
			Vector proValues = new Vector();
			Enumeration elems = properties.elements();
			Enumeration keys = properties.keys();
    		while( elems.hasMoreElements() ) {
    			proNames.addElement(keys.nextElement());
    			proValues.addElement(elems.nextElement());
    		}
			int messageNum = mo.extractIntValue("MESSAGENUMBER");
			MessageObject outEvent = new MessageObject("NoteGetProperty");
			outEvent.addParameter("OBJECT", this);
			outEvent.addParameter("PROPERTYNAMES",proNames);
			outEvent.addParameter("PROPERTYVALUES", proValues);
			outEvent.addParameter("INRESPONSETO", messageNum);
			send(outEvent);
		}catch (DorminException e) {
			throw e; 
		}
	}
		
	public  void setProperty(MessageObject mo) throws DorminException{
		Sharable realObj = (Sharable)getObject();
		try{
			setRealObjectProperties(realObj, mo);
		} catch (DorminException e) { 
			throw e;
		}
	}
	public  void startObserving(MessageObject mo) throws DorminException { };
	public  void stopObserving(MessageObject mo) throws DorminException { };
	public  void showMessage(MessageObject mo) throws DorminException { };
	public  void startProblem(MessageObject mo) throws DorminException { };
	public  void responseToGetProperty(MessageObject mo) throws DorminException { };
	public  void responseToAllProperties(MessageObject mo) throws DorminException { };
	public  void allProperties(MessageObject mo) throws DorminException { };
	
	/**
	* these Tutor side methods can be used by any proxy.
	**/
	public void sendSetProperty(Object objDesc, Vector pNames, Vector pValues){
		MessageObject mo = new MessageObject("SetProperty");
		if(objDesc instanceof ObjectProxy)
			mo.addParameter("Object", (ObjectProxy)objDesc);
		else
			mo.addObjectParameter("Object", (String)objDesc);
			
		if(pNames != null){
			mo.addParameter("PROPERTYNAMES", pNames);
			mo.addParameter("PROPERTYVALUES", pValues);
		}
		send(mo);
		pNames = null;
		pValues = null;
		mo = null;
	}
	
	public void sendCreate(String objDesc, String objType, Vector pNames, Vector pValues){
		MessageObject mo = new MessageObject("Create");
		mo.addObjectParameter("Object", objDesc);
		mo.addParameter("ObjectType", objType);
		if(pNames != null){
			mo.addParameter("PROPERTYNAMES", pNames);
			mo.addParameter("PROPERTYVALUES", pValues);
		}
		send(mo);
		pNames = null;
		pValues = null;
		mo = null;
	}
	
	
	
	public void sendShowMessage(Object objDesc, Object messages, 
								String title, Vector pointers, 
								String fileDir, String winName, int startFrom, 
								String targerName){
		MessageObject mo = new MessageObject("ShowMessage");
		if(objDesc instanceof ObjectProxy)
			mo.addParameter("Object", (ObjectProxy)objDesc);
		else
			mo.addObjectParameter("Object", (String)objDesc);
		Vector v;
		if(messages instanceof String){
			v = new Vector();
			v.addElement(messages);
		}
		else
			v = (Vector) messages;
		mo.addParameter("Message", v);
		if(title != null)
			mo.addParameter("Title", title);
		if(pointers != null && pointers.size() != 0)
			mo.addParameter("Pointers", pointers);
		if(fileDir != null)
			mo.addParameter("FileDir", fileDir);
		if(winName != null)
			mo.addParameter("Name", winName);
		if(startFrom != 0)
			mo.addParameter("StartFrom", startFrom);
		if(targerName == null)
			send(mo);
		else
			send(mo, targerName);
		v = null;
		mo = null;
	}
	
	public void sendShowMessage(Object objDesc, Object messages, 
								String title, Vector pointers, 
								String fileDir, String winName, int startFrom){
		sendShowMessage(objDesc, messages, title, pointers, fileDir, winName, startFrom, null);
	}

}