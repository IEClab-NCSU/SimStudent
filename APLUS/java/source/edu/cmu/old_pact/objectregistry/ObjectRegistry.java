package edu.cmu.old_pact.objectregistry;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.cmu.old_pact.toolframe.ToolFrame;


public class ObjectRegistry { 
	public static ObjectRegistry knownObjects = new ObjectRegistry();
	
		// used to store objects (menus and windows) during a session
	private Hashtable hashtable = new Hashtable(); 
		// used to store object data between problems (during a session) 
	private Hashtable storeDataHashtable = new Hashtable(); 
	private ToolFrame activeWindow = null;
	private static int globalFontSizeIndex = 1;
	private  boolean isMac = false;
	
	public ObjectRegistry(){
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
			isMac = true;
	}
	
	static synchronized public void registerObject (String type, Object obj) {
		if(knownObjects.hashtable.containsKey(type)){
			int numOfKeys = 0;
			Enumeration keys = knownObjects.hashtable.keys();
			while(keys.hasMoreElements()){
				String nextKey = (String)keys.nextElement();
				if(nextKey.startsWith(type))
					numOfKeys++;
			}
			type = type+" "+String.valueOf(numOfKeys);
			if(obj instanceof Component)
				((Component)obj).setName(type);
		}
		knownObjects.hashtable.put(type, obj);
		
	}
	
	
	static synchronized public void unregisterObject (String name) {
	
		// before unregistring an object store its data (if the object
		// is an instance of ToolFrame
		Object obj = getObject(name);
		if((obj instanceof ToolFrame)){
			int fsInd = ((ToolFrame)obj).getCurrentFontIndex();
			Dimension size = ((ToolFrame)obj).getSize();
			Point loc = ((ToolFrame)obj).getLocation();
			
		    ObjectData data = new ObjectData(fsInd,size,loc);		   
		    knownObjects.storeDataHashtable.put(name,data);
		}
		   // now unregister
		knownObjects.hashtable.remove(name);
	}
	  					
  						
	static synchronized public Object getObject(String type) {
		return knownObjects.hashtable.get(type);
	}
	
	static synchronized public Hashtable getAllObjects() {
		return knownObjects.hashtable;
	}
	
	static synchronized private ObjectData getObjectData(String type) {
		return (ObjectData)knownObjects.storeDataHashtable.get(type);
	}

	
	static public int getWindowFontSize(String type){ 
	
		// if this object has data stored from the previous problem
		// return font size index stored there, otherwise return global parameter
		ObjectData data = getObjectData(type);
		if(data != null)
			return data.getFontSizeInd();
  		else  
		  return globalFontSizeIndex;			 			    
  	}
  	
  	static public Dimension getWindowSize(String type){ 	
		// if this object has data stored from the previous problem
		// return window size stored there, otherwise return null
		ObjectData data = getObjectData(type);
		if(data != null)
			return data.getWindowSize();
  		else  
		  return null;			 			    
  	}
  	
  	static public Point getWindowLocation(String type){ 	
		// if this object has data stored from the previous problem
		// return window location stored there, otherwise return null
		ObjectData data = getObjectData(type);
		if(data != null)
			return data.getWindowLocation();
  		else  
		  return null;			 			    
  	}
  	
  	static public void setGlobalFontSizeIndex(int ind){
	 	globalFontSizeIndex = ind;
	}	

  	static public int getGlobalFontSizeIndex(){
  		return globalFontSizeIndex;
  	}
  	
  	public static void setActiveWindow(ToolFrame tf){
  		if(knownObjects.activeWindow != null && !knownObjects.isMac && knownObjects.activeWindow != tf){
  			knownObjects.activeWindow.sendTextFieldValue();
  		
  			knownObjects.activeWindow = tf;
  		}
  	}  	
}
