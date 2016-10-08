package edu.cmu.old_pact.cmu.spreadsheet;

import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DataFormatException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;

public abstract class DorminMatrixElement extends MatrixElement implements Sharable {
	protected String name = "";
	private int row;
	private int column;
	ObjectProxy mel_obj=null;
	private boolean justCreated;
	
	public DorminMatrixElement(int pos, String name, CellMatrix cellMatrix){
		super(pos, name, cellMatrix);
		justCreated = true;
	}
	
	public void createObjectProxy(ObjectProxy parent, String type){
		mel_obj = new MatrixElementProxy(parent,type);
		if(justCreated) {
			createNotify();
			justCreated = false;
		}
		setRealObject();
	}
	
	public  void setRealObject() {
		mel_obj.setRealObject(this);
	}
	
	public void setProxyInRealObject(ObjectProxy op){
		mel_obj = op;
	}
	
	public ObjectProxy getObjectProxy() {
		return mel_obj;
	}
	
	public void delete(){
		mel_obj = null;
	}
	
	protected void deleteObjectProxy(){
		if(mel_obj != null){
			mel_obj.deleteProxy();
			mel_obj = null;
		}
	}
	
	protected void createNotify(){
		if(cellMatrix.isAdded()){
			MessageObject mo = new MessageObject("NOTECREATE");
			mo.addParameter("OBJECT",mel_obj);
			mel_obj.send(mo);
			cellMatrix.setAdded(false);
		}
	}
	
	protected void sendNoteDelete(){
		MessageObject mo = new MessageObject("NOTEDELETE");
		mo.addParameter("OBJECT",mel_obj);
		mel_obj.send(mo);
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		try{
		if(propertyName.equalsIgnoreCase("CANDELETE"))
			setCanDelete(DataConverter.getBooleanValue(propertyName,propertyValue));
		else if(propertyName.equalsIgnoreCase("HEIGHT"))
			setHeight(DataConverter.getIntValue(propertyName,propertyValue));
		else if(propertyName.equalsIgnoreCase("WIDTH")) 
			setWidth(DataConverter.getIntValue(propertyName,propertyValue));
		else if(propertyName.equalsIgnoreCase("ISSELECTED"))
			select(DataConverter.getBooleanValue(propertyName,propertyValue));
		else 
			throw new NoSuchPropertyException("Row/Column object : no property : "+propertyName);
		} catch (DataFormattingException ex) {
			throw new DataFormatException("For Row/Column object "+ex.getMessage());
		}
	}
	
	public Hashtable getProperty(Vector proNames) throws NoSuchPropertyException{
		Hashtable allPro = getAllProperties();
		allPro.put("CANDELETE", Boolean.valueOf(String.valueOf(getCanDelete())));
		int s = proNames.size();
		if(s == 1 && ((String)proNames.elementAt(0)).equalsIgnoreCase("ALL"))
			return allPro;
		Hashtable toret = new Hashtable();
		String currName;
		for(int i=0; i<s; i++) {
			currName = ((String)proNames.elementAt(i)).toUpperCase();
			Object ob = allPro.get(currName);
			if(ob == null)
				throw new NoSuchPropertyException("No such property "+currName);
			toret.put(currName, ob);
		}
		return toret;
	}
}	
	