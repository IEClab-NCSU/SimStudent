package edu.cmu.old_pact.cmu.uiwidgets;

import java.util.Vector;

import edu.cmu.old_pact.cmu.solver.SolverTutor;
import edu.cmu.old_pact.cmu.tutor.TranslatorProxy;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.ToolProxy;
import edu.cmu.pact.Utilities.trace;

public class SolverProxy extends ToolProxy {
	
	public SolverProxy(ObjectProxy parent) {
		 super(parent, "Solver");
	}
	
	public SolverProxy(){
		super();
	}
	
	public  void create(MessageObject inEvent) throws DorminException{
		trace.out (5, "SolverProxy.java", "create solver");
		SolverFrame solver;
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");
			if(childType.equalsIgnoreCase("SOLVER")) {
				synchronized (this){
				trace.out (5, "SolverProxy.java", "XX");
				
				
				solver = new edu.cmu.old_pact.cmu.uiwidgets.SolverFrame("Solver"); 
				
				trace.out (5, this, "YY");
				this.setRealObject(solver);
				solver.setProxyInRealObject(this);
			
				SolverTutor theTutor = new SolverTutor();
				TranslatorProxy theTranslator = new TranslatorProxy(solver,theTutor);
				theTutor.setTranslator(theTranslator);
				solver.setTranslator(theTranslator);
				
				setRealObjectProperties((Sharable)solver, inEvent);
				//solver.setVisible(true);
				trace.out (5, this, "ZZ");
				
				}
			}
			else if(childType.equalsIgnoreCase("MENU")) {
				try{
				trace.out (5, "solverproxy", "create menu");
				Vector propertyNames = inEvent.extractListValue("PROPERTYNAMES");
				Vector propertyValues = inEvent.extractListValue("PROPERTYVALUES");
				int namePos = indexOfStr(propertyNames,"Name");
				String menuLabel = (String)propertyValues.elementAt(namePos);
				solver = (SolverFrame)getObject();
				solver.addMenu(menuLabel);
				propertyNames.removeAllElements();
				propertyValues.removeAllElements();
				propertyNames = null;
				propertyValues= null;
				} catch (NullPointerException e) { }
			}
			else 
				super.create(inEvent);
		}
		catch(DorminException e) { 
			throw e; 
		}
	}
	
	int indexOfStr(Vector v,String str){
		int s = v.size();
		int toret = -1;
		for(int i=0; i<s; i++){
			if(((String)v.elementAt(i)).equalsIgnoreCase(str))
				return i;
		}
		return toret;
	}
	
	public void setImageBase(String base){
		try{
		((Sharable)getObject()).setProperty("IMAGEBASE", base);
		} catch (DorminException e) { }
	}
}