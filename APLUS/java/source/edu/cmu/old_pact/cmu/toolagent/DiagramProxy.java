//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/DiagramProxy.java
package edu.cmu.old_pact.cmu.toolagent;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.ToolProxy;
import edu.cmu.old_pact.wizard.WizardGenerator;

public class DiagramProxy extends ToolProxy {
	
	public DiagramProxy(ObjectProxy parent) {
		 super(parent, "Diagram");
	}
	
	public DiagramProxy(){
		super();
	}
	
	public  void create(MessageObject inEvent)  throws DorminException{
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");
			if(childType.equalsIgnoreCase("Diagram")) {
				Object realCont = getRealParent();
				Diagram diagram = new Diagram();
				this.setRealObject(diagram);
				diagram.setProxyInRealObject(this);
				setRealObjectProperties((Sharable)diagram, inEvent);
			}
			else if (childType.equalsIgnoreCase("Cell")) {
				SingleFieldProxy dfp = new SingleFieldProxy(this);
				//dfp.mailToProxy(inEvent, (new Vector()));
				dfp.mailToProxy(inEvent, null);
			}
			else if (childType.equalsIgnoreCase("HtmlPanel")) {
				Object objToAdd =  WizardGenerator.getObject(childType, this);
				Diagram diagram = (Diagram)getObject();
				diagram.addObject(objToAdd);
				try{
					setRealObjectProperties((Sharable)objToAdd, inEvent);
				}catch (DorminException e) { throw e;}
				//diagram.validate();
				diagram.redraw();
			}
		
			else {
				super.create(inEvent);
			}
		}catch (DorminException e) { 
			throw e; 
		}  
	}	
}