//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/SingleTextField.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.cmu.spreadsheet.AltTextField;
import edu.cmu.old_pact.cmu.spreadsheet.OrderedTextField;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.PaintSharable;

public class SingleTextField extends OrderedTextField implements PaintSharable{ //Sharable
	ObjectProxy fProxy;
	private int min_h = 20;
	private int delta = 1;
	private boolean needToRefresh = false; 
	
	public SingleTextField(){
		super();
		setAlignment (AltTextField.ALIGN_MIDDLE, AltTextField.ALIGN_CENTER);
		setGrow(AltTextField.HORIZONTAL_GROW);
		setHasBounds(true);
		setMinWidth(50);
		setWidth(50);
	}
	
	public SingleTextField(int w, ObjectProxy parent, String name, String type){
		this();
		setMinWidth(w);
		setWidth(w);
		createProxy(parent, type);
		
		if(name != null)
			setName(name);
	}
	
	public SingleTextField(int w, ObjectProxy parent, String name){
		this(w, parent, name, "Cell");
	}
	
	public SingleTextField(int w, ObjectProxy parent){
		this(w, parent, null);
	}
	
	public void createProxy(ObjectProxy parent, String type){
		fProxy = new SingleFieldProxy(parent, type);
		fProxy.setRealObject(this);
	}
	
	public void removeAll(){
		if(fProxy != null)
			fProxy.deleteProxy();
		fProxy = null;
	}
	
	
	public void setName(String n){
		super.setName(n);
		if(fProxy != null){
			fProxy.setName(n);
			fProxy.defaultNameDescription();
		}
	}
	
	public Dimension preferredSize(){
		Dimension d;
		if(getSize().width == 0) 
			d = new Dimension(getMinWidth(), min_h);
		else 
			d = new Dimension(getWidth(), getHeight());
		return d;
	}
	
	public void repaintObject() {
		//repaint();
		//if(needToRefresh) {
   		//	refreshFrame();
   		//	needToRefresh = false;
   		//}
	}
	
	public void setText(String t) {
 		super.setText(t);
 		needToRefresh = true;
 	}
 		
	public void setWidth(int w){
    	Dimension d = getSize();
    	int h = min_h;
    	if(d.height > 0)
    		h = d.height;
    	setSize(w, h);
	    repaint();
    }
    
    public void setHeight(int h){
    	Dimension d = getSize();
    	setSize(d.width, h);
    	repaint();
    }
	
	public void doUpdateWidth(int oldw, int neww) {
   		setWidth(neww);
   }
   
   
   public void doUpdateHeight(int oldh, int newh) {	
   		setHeight(newh);
   }
 
   public void setDisplayWebEq(boolean d) {
   		super.setDisplayWebEq(d);
   		if(d)
   			needToRefresh = true;
   }
   
   	public void refreshFrame(){
   		Frame f = getFrame();
   		if(f != null){
   			Dimension si = f.getSize();
   			f.setSize(si.width+delta, si.height);
   			delta = (-1)*delta;
   		}
   	}
   	
	public ObjectProxy getObjectProxy() {
		return fProxy;
	}
	
	public void setProxyInRealObject(ObjectProxy op) {
		fProxy = op;
	}
		
	public void sendUserValue(String oldText, String newText){
		if(fProxy != null && !locked()){
			MessageObject mo = new MessageObject("NOTEPROPERTYSET");
			mo.addParameter("OBJECT",fProxy);
			Vector pNames = new Vector();
			pNames.addElement("VALUE");
			Vector pValues = new Vector();
			pValues.addElement(newText);
			mo.addParameter("PROPERTYNAMES", pNames); 
			mo.addParameter("PROPERTYVALUES", pValues);
			fProxy.send(mo);
			pNames.removeAllElements();
			pNames = null;
			pValues.removeAllElements();
			pValues = null;
			mo = null;
		}
	}
	
	public void sendIsSelected(boolean b){
		if(fProxy != null){
			MessageObject mo = new MessageObject("NOTEPROPERTYSET");
			mo.addParameter("OBJECT",fProxy);
			Vector pNames = new Vector();
			pNames.addElement("IsSelected");
			Vector pValues = new Vector();
			pValues.addElement(Boolean.valueOf(String.valueOf(b)));
			mo.addParameter("PROPERTYNAMES", pNames); 
			mo.addParameter("PROPERTYVALUES", pValues);
			fProxy.send(mo);
			pNames.removeAllElements();
			pNames = null;
			pValues.removeAllElements();
			pValues = null;
			mo = null;
		}
	}
	
	/*
	public void keyPressed(KeyEvent evt){	
		int key = evt.getKeyCode();
		if (key == 10 || key == 3) 
			sendUserValue("", getText());
		
		super.keyPressed(evt);
	}
	*/
	public void askHint(){
		if(fProxy != null){
			MessageObject mo = new MessageObject("GETHINT");
			mo.addParameter("OBJECT",fProxy);
			fProxy.send(mo);
		}
	}
	
	public boolean askedForHelp(){
		if(canAskForHelp()){
			askHint();
			return true;
		}
		return false;
	}
	
	public boolean canAskForHelp(){
		if(isFocused() && isEditable())
			return true;
		return false;
	}
	
	public void setProperty(String proName, Object proValue)  throws NoSuchPropertyException{
		try{
			setOwnProperty(proName, proValue);
			
		} catch (NoSuchFieldException e){
			throw new NoSuchPropertyException(e.getMessage());
		}
	}
	
	public Hashtable getProperty(Vector proNames)  throws NoSuchPropertyException{
		try{
			return getOwnProperty(proNames);
		} catch (NoSuchFieldException e){
			throw new NoSuchPropertyException(e.getMessage());
		}
	}
	
	public void delete(){
		changes.firePropertyChange("RemoveLink", null, this);
		clear();
		Container par = getParent();
		if(par != null)
			par.remove(this);
		fProxy = null;
	}
	
	public Point getBugPointPosition(){
 		Dimension d = getSize();
 		int x = d.width;
 		int y = 4;
 		return new Point(x, y);
 	}	
}		