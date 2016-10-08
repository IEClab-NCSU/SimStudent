package edu.cmu.old_pact.cmu.spreadsheet;



import java.awt.Component;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.cmu.messageInterface.ToolPointerVector;
import edu.cmu.old_pact.cmu.messageInterface.UserMessage;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.PaintSharable;

public abstract class DorminGridElement extends GridElement implements PaintSharable { //Sharable
	protected String name = "";
	protected int row;
	protected int column;
	RealCellProxy rcell_obj;
	private Hashtable Properties;
	private Vector userActionNames;
	
	public DorminGridElement(int row, int column){
		super();
		textField = null;
		this.row = row;
		this.column = column;
		name = "R"+String.valueOf(row)+"C"+String.valueOf(column);
		userActionNames = new Vector();
		userActionNames.addElement("CurrentValue");
		userActionNames.addElement("CaretPosition");
	}
	
	protected void setInit(){
		super.setInit();
		textField.setName(name);
		Properties = new Hashtable();
	}
	
	public synchronized void clear(boolean delete_Proxy){
		if(delete_Proxy && rcell_obj != null){
			rcell_obj.deleteProxy();
			rcell_obj = null;
		}
		super.clear(delete_Proxy);
	}
	
	public void setName(String n){
		name = n;
		if(rcell_obj != null)
			rcell_obj.setName(n);
	}
	
	public int getRowPos(){
		return row;
	}
	
	public int getColumnPos(){
		return column;
	}
	
	public String getName(){
		return name;
	}
	
	public void createObjectProxy(ObjectProxy parent){
		Properties.put("NAME", name);
		Properties.put("ROW", Integer.valueOf(String.valueOf(row)));
		Properties.put("COLUMN", Integer.valueOf(String.valueOf(column)));
		Properties.put("LEVEL OF HELP", Integer.valueOf(String.valueOf(0)));
		Properties.put("BACKGROUND", "white");
		Properties.put("PARENT", parent);
		rcell_obj = new RealCellProxy("Cell", name, parent);
		setRealObject();
	}
		
	public void setObjectProxy(ObjectProxy parent){
		createObjectProxy(parent);
	}
	
	public void repaintObject() {
		//((Component)textField).repaint();
	}
	
	public  void setRealObject() {
		rcell_obj.setRealObject(this);
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		try{
			if(propertyName.equalsIgnoreCase("NAME"))
				setName((String)propertyValue);
			setCellProperty(propertyName, propertyValue);
		}
		catch (NoSuchFieldException e){
			throw new NoSuchPropertyException(e.getMessage());
		}
	}
	
	public void setProxyInRealObject(ObjectProxy op){
	}
	
	public ObjectProxy getObjectProxy() {
		return rcell_obj;
	}
	 
	public void showMessage(Vector mess, String imageName, String title, Vector pointersV, String urlBase) {
	
   		String imageBase = urlBase;
		if(imageName != "")
			imageBase = urlBase+imageName;
		ToolPointerVector pV = new ToolPointerVector(mess,pointersV,title,imageBase);
		UserMessage[] userMessage = pV.getUserMessages();
		textField.showMessage(userMessage, imageBase, title,1);
		
	}    	

	Frame getFrame() {
		Component parent = getParent();
		Component root = null;
		while (parent != null) {
			root = parent;
			parent = parent.getParent();
		}
		return ((Frame) root);
	}
	
	public void sendProperty(Vector pNames, Vector pValues){
		MessageObject mo = new MessageObject("NOTEPROPERTYSET");
		mo.addParameter("OBJECT",rcell_obj);
		mo.addParameter("PROPERTYNAMES",pNames);
		mo.addParameter("PROPERTYVALUES",pValues);
		rcell_obj.send(mo);
	}
    
	public  void propertyChange(PropertyChangeEvent evt){
		if(evt.getPropertyName().equalsIgnoreCase("USERVALUE")) {
			String value = (String)evt.getNewValue();
			MessageObject mo = new MessageObject("NOTEPROPERTYSET");
			mo.addParameter("OBJECT",rcell_obj);
			mo.addParameter("PROPERTY", "VALUE"); 
			mo.addParameter("VALUE", value);
			rcell_obj.send(mo);
			Properties.put("VALUE", value);
		}
		else if (evt.getPropertyName().equalsIgnoreCase("TRACEDUSERACTION")) {
			Object[] userAction = (Object[])evt.getNewValue();
			Vector tracedUserValues = new Vector();
			tracedUserValues.addElement(userAction[0]);
			tracedUserValues.addElement(userAction[1]);
			sendProperty(userActionNames, tracedUserValues);
			tracedUserValues.removeAllElements();
			tracedUserValues = null;
		}	
		else if(evt.getPropertyName().equalsIgnoreCase("GETHINT")) {
			String value = (String)evt.getNewValue();
			MessageObject mo = new MessageObject("GETHINT");
			mo.addParameter("OBJECT",rcell_obj);
			rcell_obj.send(mo);
		}
		else if(evt.getPropertyName().equalsIgnoreCase("FOCUSGAINED"))
			changes.firePropertyChange(evt.getPropertyName(), null, this); 
		else if(evt.getPropertyName().equalsIgnoreCase("FOCUSGAINEDBYCELL"))
			changes.firePropertyChange(evt.getPropertyName(), null, evt.getNewValue()); 
		
		else
			super.propertyChange(evt);
	}
	
	public Hashtable getProperty(Vector v) throws NoSuchPropertyException{
		try{
			Hashtable toret = getCellProperty(v);
			return toret;
		} catch (NoSuchFieldException e){
			throw new NoSuchPropertyException(e.getMessage());
		}
	}
	
	public void delete(){
		if(textField != null){
			((Component)textField).setVisible(false);
			remove((Component)textField);
		}
		userActionNames.removeAllElements();
		userActionNames = null;		
		rcell_obj = null;
	}
	
	public void deleteAll(){
		Properties.clear();
		Properties = null;
		super.deleteAll();
	}
}	
	