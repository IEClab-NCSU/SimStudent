package edu.cmu.old_pact.cmu.messageInterface;

import java.util.Hashtable;

import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.InvalidPropertyValueException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;

public class DorminUserMessageWindow extends UserMessageWindow {
	MessageProxy mProxy=null;
	private boolean sendNoteDelete = true;
	UniversalToolProxy utp=null;
	
	public DorminUserMessageWindow(){
		super("UserMessageWindow");
		//addFocusListener(this);
	}

	public DorminUserMessageWindow(String urlBase, String imageName, UniversalToolProxy _utp) {
		super(urlBase, imageName);
		Sharable app = (Sharable)ObjectRegistry.getObject("Application");
		mProxy = new MessageProxy(app.getObjectProxy());
		mProxy.setRealObject(this);
		setToolFrameProxy(mProxy);
		createButtonProxy();
		utp = _utp;
		//addFocusListener(this);
	}
	
	public DorminUserMessageWindow(String urlBase) {
		this (urlBase, null);
		//addFocusListener(this);
	}
	
	public DorminUserMessageWindow(String urlBase, String imageName) {
		super(urlBase, imageName);
		Sharable app = (Sharable)ObjectRegistry.getObject("Application");
		mProxy = new MessageProxy(app.getObjectProxy());
		mProxy.setRealObject(this);
		setToolFrameProxy(mProxy);
		createButtonProxy();
		//addFocusListener(this);
	}
	
	public void delete(){
		mProxy = null;
		super.delete();
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		try{
			Hashtable properties = getAllProperties();
			if(propertyName.equalsIgnoreCase("TITLE")) {
				properties.put(propertyName.toUpperCase(), propertyValue);
				setTitle((String)propertyValue);
			}
			else if (propertyName.equalsIgnoreCase("Name")) {
				properties.put(propertyName.toUpperCase(), propertyValue);
				setName((String)propertyValue);
			}
			else if (propertyName.equalsIgnoreCase("isVisible") && !DataConverter.getBooleanValue(propertyName,propertyValue)) {
				sendNoteDelete = false;
				super.setProperty(propertyName, propertyValue);
				removeFromRegistries();
				sendNoteDelete = true;
			}
			else if (propertyName.equalsIgnoreCase("CurrentMessageNumber")) {
				int cnum = DataConverter.getIntValue(propertyName,propertyValue)-1;
				if(numOfMess <= cnum)
					throw new InvalidPropertyValueException("Message Dialog : can't display message # "+cnum+". Number of messages equals "+numOfMess);
				currMess = cnum;
				resetView();
			}
			else
				super.setProperty(propertyName, propertyValue);
		} catch(DorminException e){
			throw e;
		}catch (DataFormattingException ex){
  			throw getDataFormatException(ex);
  		}
	}
	//overwritten in MenuUserMessageWindow
	public void removeFromRegistries(){ }
	
	public void setName(String n){
		if(mProxy != null){
			mProxy.setName(n);
			mProxy.defaultNameDescription();
		}
		super.setName(n);
	}
	
	public void referenceByPosition(){
		if(mProxy != null)
			mProxy.defaultPosDescription();
	}
	
	public boolean doNext(){
		boolean resp = super.doNext();
		if(resp) {
			sendNotePropertySet("CurrentMessageNumber",Integer.valueOf(String.valueOf(currMess+1)));
			getAllProperties().put("CURRENTMESSAGENUMBER",Integer.valueOf(String.valueOf(currMess+1))); 
			this.toFront();
			this.requestFocus();
			/* FIXME: Commented out by Kim K.C. Mar 18 2005
			if(utp != null){
				utp.logHintRequest(utp.lastHintSelection);
				utp.logHintMsg(utp.lastHintSelection, userMessage[currMess].getText());
			}
			*/
		}
		return resp;
	}
	
	public boolean doPrevious(){
		boolean resp = super.doPrevious();
		if(resp) {
			sendNotePropertySet("CurrentMessageNumber",new Integer(currMess+1));
			getAllProperties().put("CURRENTMESSAGENUMBER",new Integer(currMess+1)); 
			this.toFront();
			this.requestFocus();
			/* FIXME: Commented out by Kim K.C. Mar 18 2005
			if(utp != null){
				utp.logHintRequest(utp.lastHintSelection);
				utp.logHintMsg(utp.lastHintSelection, userMessage[currMess].getText());
			}
			*/
		}
		return resp;
	}
	 
	public void clearWindow() {
		if(mProxy != null && currMess != -1 && sendNoteDelete){
			MessageObject mo = new MessageObject("NoteDelete");
			mo.addParameter("Object", mProxy);
			mProxy.send(mo);
		}
		
		super.clearWindow();
		
	}
		
	public void presentMessages(UserMessage[] userMessage, int startFrom) {
		super.presentMessages(userMessage, startFrom);
		getAllProperties().put("CURRENTMESSAGENUMBER",new Integer(currMess+1));
		toFront();
		requestFocus();
	}	
}	
	
		
	
	
			
