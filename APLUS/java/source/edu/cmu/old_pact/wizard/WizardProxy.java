package edu.cmu.old_pact.wizard;

import java.awt.Checkbox;
import java.util.Vector;

import edu.cmu.old_pact.dormin.DataFormatException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Range;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.toolframe.DorminToolProxy;
import edu.cmu.pact.Utilities.trace;

public class WizardProxy extends DorminToolProxy {
	
	public WizardProxy(ObjectProxy parent, String type) {
		 super(parent, type);
	}
	
	public WizardProxy(){
		super();
	}
	
	
	public  void create(MessageObject inEvent)  throws DorminException{
		trace.out (10, this, "creating Wizard");
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");
			Object objToAdd;
			trace.out (10, this, "child type = " + childType);			
			//Checkbox groups are not components so a special case is necessary
			if(childType.equalsIgnoreCase("CheckboxGroup")){
				Vector checkboxes = new Vector();
				Object parent = getObject();
				
				DorminCheckboxGroup new_group = new DorminCheckboxGroup(this);
				setRealObjectProperties(new_group, inEvent);
				
				if ((parent instanceof WizardFrame)) {
					checkboxes = new_group.getCheckboxes();
					for (int i=0;i<checkboxes.size();i++) {
						((WizardFrame)parent).addObject((Checkbox)checkboxes.elementAt(i));
					}
				}
				else if (parent instanceof DorminPanel) {
					checkboxes = new_group.getCheckboxes();
					for (int i=0;i<checkboxes.size();i++) {
						((DorminPanel)parent).addObject((Checkbox)checkboxes.elementAt(i));
					}
				}
					
			}
			else {
			
				// if the object to be created is a Frame, create it here
				//if(childType.equalsIgnoreCase("Frame"))
				if(childType.equalsIgnoreCase(type)) {
					trace.out (10, this, "creating wizard frame");
					objToAdd = new WizardFrame(this);
			
				} else {
					//All types other components are created by this call
					trace.out (10, this, "creating using WizardGenerator.getObject");
					objToAdd = WizardGenerator.getObject(childType, this);
				}
				
				if(objToAdd != null && objToAdd instanceof Sharable){
					if(objToAdd instanceof WizardFrame){
						this.setRealObject(objToAdd);
					}
					else {
						Object parent = getObject();
						if(parent instanceof WizardFrame)
							((WizardFrame)parent).addObject(objToAdd);
						else if(parent instanceof DorminPanel)
							((DorminPanel)parent).addObject(objToAdd);
					}
					try{
						setRealObjectProperties((Sharable)objToAdd, inEvent);
					}catch (DorminException e) { throw e;}
				} 
				if(objToAdd == null)
					super.create(inEvent);
					//throw new NoSuchObjectException(getName()+": Can't create an object "+childType);
			}
		}catch (DorminException e) { 
			throw e; 
		}
	}
	
	public  void setProperty(MessageObject mo) throws DorminException { 
		try{
			char objType = mo.getObjectType("OBJECT");
			Vector pn = mo.extractListValue("PROPERTYNAMES");
			Vector pv = mo.extractListValue("PROPERTYVALUES");
			int s = pn.size();
			if(s == 0) return;
			Sharable ps = (Sharable)getObject();
		
			switch (objType){
				case 'O':
						for(int i=0; i<s; i++){
							ps.setProperty((String)pn.elementAt(i), pv.elementAt(i));
						}
						break;
				case 'R':
						String propertyName = (String)pn.elementAt(0);
						if(s == 1 && propertyName.equalsIgnoreCase("HIGHLIGHT")){
							if(((String)pv.elementAt(0)).equalsIgnoreCase("FALSE"))	{
								ps.setProperty(propertyName, (new Vector()));
								return;
							}
							//Range range = mo.extractRangeValue("OBJECT");
							Range range = (Range)(mo.getParameter("OBJECT"));
							String rangeType = range.getRangeType();
							if(rangeType.equalsIgnoreCase("CHARACTER")){
								Vector tags = range.getStartEndPairs();
								ps.setProperty(propertyName, tags);
							}
							else throw new DataFormatException("WisardProxy doesn't know how to highlight ranges of type "+rangeType);
						}
						break;
			}			
		}
		catch (DorminException e) {
			throw e;
		}	
	}
}