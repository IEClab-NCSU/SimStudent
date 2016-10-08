//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/Fraction.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;
import edu.cmu.old_pact.cmu.spreadsheet.AltTextField;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.linkvector.LinkVector;
import edu.cmu.old_pact.toolframe.Hintable;
import edu.cmu.old_pact.toolframe.LineCanvas;


public class Fraction extends Panel implements Sharable,Hintable{
	private FractionProxy fProxy;
	private SingleTextField numerator, denominator;
	private Hashtable Properties;
	protected LinkVector links;
	
	public Fraction(ObjectProxy parent, LinkVector links){
		fProxy = new FractionProxy(parent);
		fProxy.setRealObject(this);
		this.links = links;
		setLayout(new GridBagLayout());
		numerator = createField(fProxy, "Numerator", 35);
		denominator = createField(fProxy, "Denominator", 35);
		
		
		Panel numPanel = new Panel();
		numPanel.setLayout(new FlowLayout(1));
		numPanel.add(numerator);
		GridbagCon.viewset(this,numPanel, 0, 0, 1, 1, 0, 0, 0 ,0);
		LineCanvas lc = new LineCanvas();
		Panel denomPanel = new Panel();
		denomPanel.setLayout(new FlowLayout(1));
		denomPanel.add(denominator);
		GridbagCon.viewset(this,lc, 0, 1, 1, 1, 0, 0, 0 ,0);
		GridbagCon.viewset(this,denomPanel, 0, 2, 1, 1, 0, 0, 0 ,0);
		
		Properties = new Hashtable();
	}
	
	private SingleTextField createField(ObjectProxy parent,String name, int width){
		SingleTextField toret = new SingleTextField(width, parent, name);
		toret.setGrow(AltTextField.NO_GROW);
//		links.addVecticalLink(toret);
//		links.addHorisontalLink(toret);
		toret.addPropertyChangeListener(links);
		return toret;
	}
	
	public ObjectProxy getObjectProxy() {
		return fProxy;
	}
	
	public void setProxyInRealObject(ObjectProxy op) {
		fProxy = (FractionProxy)op;
	}
	
	public void delete(){
		removeAll();
		//removeNotify();
		numerator.removePropertyChangeListener(links);
		denominator.removePropertyChangeListener(links);
		Properties.clear();
		Properties = null;
		numerator = null;
		denominator = null;
		links = null;
		fProxy = null;
	}
	
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		if(propertyName.equalsIgnoreCase("NAME")){
			Properties.put(propertyName.toUpperCase(), propertyValue);
			if(fProxy != null)
				fProxy.setName((String)propertyValue);
		}
		else if(propertyName.equalsIgnoreCase("NAMINGPREFERENCE")){
		}
		else
			throw new NoSuchPropertyException("Object 'Fraction' doesn't have property '"+propertyName+"'");
	}
	
	public Hashtable getProperty(Vector proNames) throws NoSuchPropertyException{
 		int s = proNames.size();
 		if(s == 1 && ((String)proNames.elementAt(0)).equalsIgnoreCase("ALL"))
 			return Properties;
 		Hashtable toret = new Hashtable();
 		String currName;
 		for(int i=0; i<s; i++){
 			currName = ((String)proNames.elementAt(i)).toUpperCase();
 			Object ob = Properties.get(currName);
 			if(ob == null)
 				throw new NoSuchPropertyException("Object 'Fraction' doesn't have property "+currName);
 			toret.put(currName, ob);
 		}
 		return toret;
 	}
 	
 	public boolean asksForHint(){
 		if(numerator.canAskForHelp()){
 			numerator.askHint();
 			return true;
 		}
 		if(denominator.canAskForHelp()){
 			denominator.askHint();
 			return true;
 		}
 		return false;
 	}
			
}