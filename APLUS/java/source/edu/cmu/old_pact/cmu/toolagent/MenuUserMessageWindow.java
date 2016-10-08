//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/MenuUserMessageWindow.java
package edu.cmu.old_pact.cmu.toolagent;


import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.old_pact.beanmenu.BeanMenuRegistry;
import edu.cmu.old_pact.cmu.messageInterface.DorminUserMessageWindow;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;

public class MenuUserMessageWindow extends DorminUserMessageWindow {
	/**
	* A title to be used for menu identification
	**/
	private String title="";
	
	public MenuUserMessageWindow(){
		super();
	}
	
	public MenuUserMessageWindow(String UrlBase){
		super(UrlBase);
	}
	
	public MenuUserMessageWindow (String UrlBase, String imageName) {
		super(UrlBase, imageName);
		
	}
	
	public MenuUserMessageWindow (String UrlBase, String imageName, UniversalToolProxy _utp) {
		super(UrlBase, imageName, _utp);
		
	}
	
	public void setName(String name){
		if(!name.equalsIgnoreCase("UserMessageWindow")){
			super.setName(name);
			ObjectRegistry.registerObject(name, this);
			try{
				BeanMenuRegistry.addToMenu("Windows", name);
			}catch (NullPointerException e) { }	
		}
		else
			super.setName(name);
	}
	
	public void setTitle(String t){
		if(title != null){
		if(t == null || t == "")
			t = "Messages";
		synchronized (this){
			//if(!title.equals(t)) {
				//removeFromRegistries();
			//}
			title = t;
			super.setTitle(title);
		}
		}
	}
	
	public void removeFromRegistries(){
		try{
			if(! getName().equalsIgnoreCase("UserMessageWindow")){
				BeanMenuRegistry.removeMenuItem("Windows", getName());
				ObjectRegistry.unregisterObject(getName());
			}
		}
		catch (NullPointerException e) { }
	}
	
	public void closeWindow(){
		removeFromRegistries();
		super.closeWindow();
	}
	
	
	public String getTitle(){
		return title;
	}
	
	
}
	
