package edu.cmu.old_pact.cmu.spreadsheet;

import java.util.Vector;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.ToolProxy;

public class RealCellProxy extends ToolProxy {
	
	public RealCellProxy(String typ, String name, ObjectProxy parent) {
		 super(typ, name, parent);
	}
	
	public  void showMessage(MessageObject inEvent){

		String image = "";
		try{
			image = inEvent.extractStrValue("Image");
		} catch (DorminException e) { }
		String title = "";
		try{
			title = inEvent.extractStrValue("Title");
		} catch (DorminException e) { }
		String fileDir = "";
		try{
			fileDir = inEvent.extractStrValue("FileDir");
		} catch (DorminException e) { }
		Vector pointers = null;
		try{
			pointers = inEvent.extractListValue("Pointers");
		} catch (DorminException e) { }
		try{
			Vector mes = inEvent.extractListValue("Message");
			((DorminGridElement)getObject()).showMessage(mes, image, title, pointers, fileDir);
		}
		catch (DorminException e) { 
			System.out.println("RealCellProxy showMessage "+e.toString());
		}
	}

	public  void select(MessageObject mo){ 
		((DorminGridElement)getObject()).select();
	}
	
	public  void constructChildProxy(MessageObject mo, Vector description) { }

}