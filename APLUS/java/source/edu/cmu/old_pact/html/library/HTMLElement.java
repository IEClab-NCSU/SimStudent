package edu.cmu.old_pact.html.library;


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


//public class HTMLElement extends Object{
public class HTMLElement{
	private Hashtable Attributes;
	private String text = "";
	private String identifier = null;
	private HTMLWrapper wr = null;
	
	public HTMLElement(String identifier) {
		this.identifier = identifier;
		Attributes = new Hashtable(5);
	}
	
	public HTMLElement(String identifier, String text) {
		this(identifier);
		this.text = text;
	}
	
	public String getHTMLText(){
		wr = new HTMLWrapper(this);
		return wr.getText();
	}
	
	public void setAttribute(String attrName, String subName, String value) {
			attrName = attrName.toUpperCase();
			subName = subName.toUpperCase();
			Hashtable included = (Hashtable)Attributes.get(attrName);
			if(included == null) 
				included = new Hashtable(5);
				
			included.put(subName, value);
		
		Attributes.put(attrName, included);
	}
	
	public void delete(){
		Attributes.clear();
		Attributes = null;
	}
		
	
	public void setAttribute(String key, String value) {
		setAttribute(key, "DEFAULT", value);
	}
	
	public void setAttribute(String key) {
		setAttribute(key, "DEFAULT", "");
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getText() {
//trace.out("in HTMLElement getText = "+text);
		return text;
	}
	
	public Hashtable getAttribute(String key) {
		return (Hashtable)Attributes.get(key);
	}
	
	public Vector getAttributeNames() {
		return getAttributeNames(Attributes);
	}
	
	public Vector getsubAttributeNames(String attrName) {
		Hashtable sub = getSubAttributes(attrName);
		if(sub == null)
			return null;
		return getAttributeNames(sub);
	}
	
	private  Vector getAttributeNames(Hashtable hashAttr) {
		Vector toret = new Vector(10);
		if(hashAttr.size() == 0)
			return null;
		for (Enumeration e = hashAttr.keys() ; e.hasMoreElements() ;) 
			toret.addElement((String)e.nextElement());
		return toret;
	}
	
	private Hashtable getSubAttributes(String attrName) {
		return (Hashtable)Attributes.get(attrName);
	}
	
	public boolean attributeExist(String attrName) {
		return Attributes.containsKey(attrName);
	}
	
	public String getAttributeValue(String attrName, String subName) {
		String toret = "";
		if(subName.equals(""))
			subName = "DEFAULT";
		Hashtable sub = getSubAttributes(attrName);
		toret = (String)sub.get(subName);
		return toret;
	}
	
	public String getAttributeValue(String attrName) {
		return getAttributeValue(attrName, "DEFAULT");
	}
	
}