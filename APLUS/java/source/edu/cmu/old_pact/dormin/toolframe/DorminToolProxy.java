//d:/Pact-CVS-Tree/Tutor_Java/./src/Middle-School/Java/dormin/toolframe/DorminToolProxy.java
package edu.cmu.old_pact.dormin.toolframe;
//The only thing, which DorminToolProxy does: creates a new menu upon Tutor request.

import java.util.Vector;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.ToolProxy;
import edu.cmu.old_pact.dormin.menu.DorminListeningMenu;

public class DorminToolProxy extends ToolProxy {
	
	public DorminToolProxy() {
		super();
	}
	
	public DorminToolProxy(	String type, 
						String name, 
						ObjectProxy parent) {
		this(type, name, parent, null, -9999);
	}
	public DorminToolProxy(	String type, 
						ObjectProxy parent, 
						String id) {				
		this(type, null, parent, id, -9999);
	}
	
	public DorminToolProxy(	String type, 
						ObjectProxy parent,
						int position) {			
		this(type, null, parent, null, position);
	}
	
	public DorminToolProxy(	ObjectProxy parent, 
						String type){
		this(type, null, parent, null, -9999);
	}
	
	public DorminToolProxy(String type){
		this(type, null, null, null, -9999);
	}
	
	public DorminToolProxy(	String type, 
						String name,
						ObjectProxy parent,
						String id, 
						int position) {
		super(type, name, parent, id, position);
	} 
	
	public  void create(MessageObject inEvent) throws DorminException{
		try{
			String childType = "";
			childType = inEvent.extractStrValue("OBJECTTYPE");
			if(childType.equalsIgnoreCase("MENU")) {
				try{
				Vector propertyNames = inEvent.extractListValue("PROPERTYNAMES");
				Vector propertyValues = inEvent.extractListValue("PROPERTYVALUES");
				int namePos = indexOfStr(propertyNames,"Name");
				String menuLabel = (String)propertyValues.elementAt(namePos);
				DorminToolFrame tf = (DorminToolFrame)getObject();
				DorminListeningMenu dlm = new DorminListeningMenu(menuLabel,this,tf);
				(tf.getMenuBar()).add(dlm);
				propertyNames.removeAllElements();
				propertyValues.removeAllElements();
				propertyNames = null;
				propertyValues= null;
				} catch (NullPointerException e) { }
			}
			else 
				super.create(inEvent);
		}
		catch(DorminException e) { 
			throw e; 
		}
	}
	
	int indexOfStr(Vector v,String str){
		int s = v.size();
		int toret = -1;
		for(int i=0; i<s; i++){
			if(((String)v.elementAt(i)).equalsIgnoreCase(str))
				return i;
		}
		return toret;
	}
}
	
