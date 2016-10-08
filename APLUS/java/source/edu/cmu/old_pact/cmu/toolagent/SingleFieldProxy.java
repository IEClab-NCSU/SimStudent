//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/SingleFieldProxy.java
package edu.cmu.old_pact.cmu.toolagent;

import java.util.Vector;

import edu.cmu.old_pact.cmu.messageInterface.ToolPointerVector;
import edu.cmu.old_pact.cmu.messageInterface.UserMessage;
import edu.cmu.old_pact.cmu.spreadsheet.Gridable;
import edu.cmu.old_pact.dormin.DataFormatException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.ToolProxy;
import edu.cmu.old_pact.dormin.toolframe.DorminToolFrame;


public class SingleFieldProxy extends ToolProxy {
	String instanceType = "Cell";
	
	public SingleFieldProxy(ObjectProxy parent) {
		this(parent, "Cell");
	}
	
	public SingleFieldProxy(ObjectProxy parent , String t){
		super(parent, t);
		instanceType = t;
	}
	
	public void create(MessageObject inEvent) throws DorminException{
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");
			if(childType.equalsIgnoreCase(instanceType)) {
				Object realCont = getRealParent();
				SingleTextField singeField = new SingleTextField();
				this.setRealObject(singeField);
				singeField.setProxyInRealObject(this);
				singeField.setSize(singeField.preferredSize());
				if(realCont instanceof DorminToolFrame)
					((DorminToolFrame)realCont).addField(singeField);
				setRealObjectProperties((Gridable)singeField, inEvent);
			}
			else
				super.create(inEvent);
		}catch (DorminException e) { 
			throw e; 
		}
	}
	
	public  void showMessage(MessageObject inEvent){ 
		String image = "";
		try{
			image = inEvent.extractStrValue("Image");
		} catch (DorminException e) { }
		String title = "";
		try{
			title = inEvent.extractStrValue("Title");
		} catch (DorminException e) { }
		Vector pointers = null;
		try{
			pointers = inEvent.extractListValue("Pointers");
		} catch (DorminException e) { }
		try{
			Vector mes = inEvent.extractListValue("Message");
			resendShowMessage(mes, pointers, image, title, 1);
		}
		catch (DorminException e) { 
			System.out.println("SingleFieldProxy showMessage "+e.toString());
		}
	}
	
	private void resendShowMessage(Vector mes, Vector pointersV, String imageName, String title,int stFrom){
		//String urlBase = StudentInterface.fileDir;
		//for Applications only.
		String urlBase = System.getProperty("user.dir");
		String imageBase = "file:///"+urlBase;
		if(imageName != "")
			imageBase = urlBase+imageName;
		ToolPointerVector pV = new ToolPointerVector(mes,pointersV,title,imageBase);
		UserMessage[] userMessage = pV.getUserMessages();
		((Gridable)getObject()).showMessage(userMessage, imageBase, title,1);
	}

	public  void select(MessageObject mo){ 
		((Gridable)getObject()).setSelected(true);
	}
	
	
	public  void delete(MessageObject mo){ 
		this.deleteProxy();
	}
	
	public void setRealObjectProperties(Gridable realObj, MessageObject inEvent) throws DorminException{
		Vector propertyNames,propertyValues;
		try{
			propertyNames = inEvent.extractListValue("PROPERTYNAMES");
			propertyValues = inEvent.extractListValue("PROPERTYVALUES");
		}catch (DorminException e){
			return;
		}	
		try{
			int s = propertyNames.size();
			if(s == 0 ) return;
			if(s != propertyValues.size()){
				throw new DataFormatException("Not equal sizes of propertyNames : "+s+" and propertyValues : "+propertyValues.size()+"\n"+"propertyNames = "+propertyNames+"\n"+"propertyValues = "+propertyValues);
			}
			String currName;
			for(int i=0; i<s; i++) {
				currName = (String)propertyNames.elementAt(i);
				if(currName.equalsIgnoreCase("NAMINGPREFERENCE") ||currName.equalsIgnoreCase("NAMINGPREFERENCES"))
					Properties.put("Default",((String)propertyValues.elementAt(i)).toUpperCase());
				else{
					try{
						realObj.setOwnProperty(currName, propertyValues.elementAt(i));
					} catch (NoSuchFieldException es) {
						throw new NoSuchPropertyException("Object '"+type+"' doesn't have property "+es.getMessage()+"'."); 
					}
				}
			}
		}catch (DorminException e) { 
			throw e;
		}
	}
			
}