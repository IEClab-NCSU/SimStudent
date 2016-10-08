package edu.cmu.old_pact.dormin;

import java.util.Hashtable;
import java.util.Vector;

public class ProxyHashtable extends Hashtable {
	
	public ProxyHashtable() {
		super();
	}
	
	public void addElement(ObjectProxy obj) {
		//trace.out (5, this, "Adding object " + obj.type + " to proxy hash table");
		String type = obj.type.toUpperCase();
		Vector onFly = (Vector)this.get(type);
		if(onFly == null) { 
			onFly = new Vector();
			this.put(type, onFly);
		}
		onFly.addElement(obj);
	}
	
	
	public int indexOf(ObjectProxy obj) {
		int toret = -1;
		String type = obj.type.toUpperCase();
		Vector onFly = (Vector)this.get(type);
		if(onFly != null)
			toret = onFly.indexOf(obj);
		return toret;
	}
	
	public void insertElementAt(ObjectProxy obj, int movePos){
		String type = obj.type.toUpperCase();
		Vector onFly = (Vector)this.get(type);
		if(onFly != null)
			onFly.insertElementAt(obj,movePos);
	}
	
	public void removeElementAt(ObjectProxy obj, int removePos){
		//removeElement(obj);
		String type = obj.type.toUpperCase();
		Vector onFly = (Vector)this.get(type);
		if(onFly != null) {
			onFly.removeElementAt(removePos);
		}	
	}
	
	public void removeElement(ObjectProxy obj){
		String type = obj.type.toUpperCase();
		Vector onFly = (Vector)this.get(type);

		if(onFly != null) {
			onFly.removeElement(obj);
		}				
	}
	
	public ObjectProxy elementAt(String type, int Pos){
		ObjectProxy toret = null;
		type = type.toUpperCase();
		Vector onFly = (Vector)this.get(type);
		if(onFly != null)
			toret = (ObjectProxy)onFly.elementAt(Pos);
		return toret;
	}
	
	
	public int size(String type) {
		int toret = -1;
		Vector onFly = (Vector)this.get(type);
		if(onFly != null)
			toret = onFly.size();
		return toret;
	}
	
	public Object get(String key){
		key = key.toUpperCase();
		return super.get(key);
	}
	
}