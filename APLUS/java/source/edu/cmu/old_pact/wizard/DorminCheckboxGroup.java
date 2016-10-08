package edu.cmu.old_pact.wizard;

import java.awt.CheckboxGroup;
import java.util.Vector;

import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DataFormatException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;

public class DorminCheckboxGroup extends CheckboxGroup implements Sharable {
	ObjectProxy parentProxy;
	Vector checkboxesInGroup;
	
	public DorminCheckboxGroup(ObjectProxy parent) {
		super();
		parentProxy = parent;
		checkboxesInGroup = new Vector();
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		try {
			if (propertyName.equalsIgnoreCase("MEMBERS")) {
				Vector checkboxNames = DataConverter.getListValue(propertyName, propertyValue);
				int numItems = checkboxNames.size();
				
				for (int i=0;i<numItems;i++) {
					DorminCheckbox groupMember = new DorminCheckbox(parentProxy, (String)checkboxNames.elementAt(i), this);
					checkboxesInGroup.addElement(groupMember);
				}
			}
			else throw new NoSuchPropertyException("No such property: "+propertyName+" for Object of type DorminCheckboxGroup'");
		} catch(DataFormattingException ex){
  			String st = ex.getMessage()+" for Object of type DorminCheckboxGroup";
  			throw new DataFormatException(st);
  		}
	}
	
	public Vector getCheckboxes() {
		return checkboxesInGroup;
	}
	
	public ObjectProxy getObjectProxy() {
		return null;
	}
	
	public void setProxyInRealObject(ObjectProxy op) {
	}
	
	public java.util.Hashtable getProperty(Vector PropertyNames) throws NoSuchPropertyException {
		return null;
	}
	
	public void delete() {
	}
}
