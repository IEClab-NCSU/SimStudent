package edu.cmu.old_pact.cmu.tutor;

import java.util.Vector;

import edu.cmu.old_pact.cmu.uiwidgets.SolverFrame;
import edu.cmu.old_pact.cmu.uiwidgets.SolverProxy;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.ToolProxy;
import edu.cmu.old_pact.skillometer.SkillometerFrame;
import edu.cmu.old_pact.skillometer.SkillometerProxy;

public class SolverApplicationProxy extends ToolProxy {

	// Constructors
	public SolverApplicationProxy(String name) {
		super(name);
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
		try{
			Vector mes = inEvent.extractListValue("Message");
			Vector pointers = inEvent.extractListValue("Pointers");
			//((JPSolver)getObject()).showMessage(mes, image, title, pointers);
		}
		catch (DorminException e) { 
			System.out.println("SolverApplicationProxy showMessage "+e.toString());
		}
	}
	
	
	public  void create(MessageObject inEvent) throws DorminException{
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");

			if(childType.equalsIgnoreCase("Solver")) {
				SolverProxy sp = new SolverProxy(this);
				try{
				 	sp.mailToProxy(inEvent, (new Vector()));
				 }
				catch (DorminException e) {
				 	throw e;
				 }
				SolverFrame.getSelf().setStandalone(true);
				
			}
		
			else if (childType.equalsIgnoreCase("Skillometer")) {
				SkillometerProxy skm_obj = new SkillometerProxy(this);
				
				// create Skillometer here : strange packages incompatibility.
				SkillometerFrame skm = new SkillometerFrame("Student");
				setRealObjectProperties((Sharable)skm, inEvent);
				skm_obj.setRealObject(skm);
				skm.setProxyInRealObject(skm_obj);
				skm.setVisible(true);
				// implement MenuBar here. Don't mess up skillometer package.
				//MenuBar menuBar = MenuFactory.getGeneralMenuBar(skm, "Skillometer");
				//skm.setMenuBar(menuBar); 
				
			}
			else
				super.create(inEvent);
		}
		catch (DorminException e) { 
			throw e;
		}
	}	
}
