package edu.cmu.old_pact.dragregistry;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;

public class DragRegistry {
	public static DragRegistry knownObjects = new DragRegistry();
	
	private Hashtable hashtable = new Hashtable(); 
	
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
		knownObjects.hashtable.remove(name);
	}
	
	static public Object getObjectAtLocation(int x, int y){
		if(knownObjects.hashtable.size() == 0)
			return null;
		Enumeration objects = knownObjects.hashtable.elements();
		Object obj;
		while(objects.hasMoreElements()){
			obj = objects.nextElement();
			if((obj instanceof Component) && ((Component)obj).isVisible()){
				Point p = ((Component)obj).getLocationOnScreen();
		
				Rectangle b = ((Component)obj).getBounds();
				if(	(x >= p.x && x <= p.x+b.width) &&
					(y >= p.y && y <= p.y+b.height)){
					return obj;
				}
			}
		}
		return null;
	}
	
	static synchronized public Object getObject(String type) {
		return knownObjects.hashtable.get(type);
	}
	
	static synchronized public Hashtable getAllObjects() {
		return knownObjects.hashtable;
	}
			
}
