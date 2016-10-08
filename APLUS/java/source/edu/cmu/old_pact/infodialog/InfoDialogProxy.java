package edu.cmu.old_pact.infodialog;

import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.ToolProxy;


public class InfoDialogProxy extends ToolProxy {
	
	public InfoDialogProxy(ObjectProxy parent) {
		 super(parent, "InfoDialog");
	}
	
	public InfoDialogProxy(){
		super();
	}
	
	public void delete() {}
	
	public void init(ObjectProxy parent){
					// type, name, parent
		super.init("InfoDialog", "InfoDialog", parent);
	}
	
}