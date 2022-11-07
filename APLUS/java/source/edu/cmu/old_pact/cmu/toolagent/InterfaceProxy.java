//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/InterfaceProxy.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Frame;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.InvalidPropertyValueException;
import edu.cmu.old_pact.dormin.MessageFormatException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.MissingParameterException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.ToolProxy;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.Utilities.trace;

public class InterfaceProxy extends ToolProxy {
	private Agentable agent = null;
	private Thread theThread = null; 
	
	// Constructors
	public InterfaceProxy() {
		super("Application");
		// trace.out (10, "cmu.toolagent.InterfaceProxy", "creating");
	}
		
	public InterfaceProxy(String typ, String name, ObjectProxy parent) {
		 super(typ, name, parent);
	}
	
	public void showMessage(MessageObject inEvent) throws InvalidPropertyValueException {
		// trace.out (15, "InterfaceProxy.java", "showMessage");
		String image = (String)(inEvent.getOptionalParameter("Image"));
		String title = (String)(inEvent.getOptionalParameter("Title"));
		if (image == null)
			image = "";
		if (title == null)
			title = "";
		int startFrom = 1;
		try{
			 startFrom = ((Integer)inEvent.getOptionalParameter("StartFrom")).intValue();
		}
		
		catch (NullPointerException ex) { }
		catch(NumberFormatException e) { }
		Vector mes = null;
		
		try{
			Object parm = inEvent.getParameter("Message");
			if (parm instanceof Vector)
				mes = (Vector)parm;
			else if (parm instanceof String) {
				mes = new Vector();
				mes.addElement(parm);
			}
			else
				throw new InvalidPropertyValueException("in ShowMessage, Message must be a string or list of strings");
		}
		catch (MissingParameterException ex) { 
			trace.out("No messages in showMessage ");
		}
		Vector pointers = (Vector)(inEvent.getOptionalParameter("Pointers"));
		if(pointers != null && pointers.size() == 0)
			pointers = null;
		String nam = (String)(inEvent.getOptionalParameter("Name"));
		if (nam == null)
			nam = "";
		((StudentInterface)getObject()).showMessage(mes, image, title, pointers,nam,startFrom);
	}
	
	public  void select(MessageObject inEvent){ 
		try{
			String TypeNameStr = inEvent.extractStrValue("Selection");
			Vector TypeNameDesc = getDescription(TypeNameStr);
			mailToProxy(inEvent, TypeNameDesc);
		}
		catch (DorminException e) { 
			trace.out("InterfaceProxy select "+e.toString());
		}
	}
	
	
	public  void delete(MessageObject inEvent){ 
	}
	
	public  void startProblem(MessageObject inEvent){ 
		// trace.out (10, "cmu.toolagent.1InterfaceProxy.java", "startProblem: event = " + inEvent);
		try{
			String p_name = inEvent.extractStrValue("ProblemName");
			((StudentInterface)getObject()).setProperty("ProblemName", p_name);
		}
		catch (DorminException e) {
			trace.out("InterfaceProxy startProblem "+e.toString());
		}
	}

	public  void create(MessageObject inEvent) throws DorminException{
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");
			if(childType.equalsIgnoreCase("ProblemStatement")) {
				inEvent = addToMessageObject(inEvent, "ProblemName", ((StudentInterface)getObject()).getProperty("PROBLEMNAME"));
				inEvent = addToMessageObject(inEvent, "urlBase", StudentInterface.fileDir);
				startTool("cl.tools.problemstatement.StatementProxy", childType, inEvent);	
			}
			else if (childType.equalsIgnoreCase("Worksheet")) {
				startTool("cmu.spreadsheet.WorksheetProxy", childType, inEvent);
			}
			
			else if (childType.equalsIgnoreCase("Skillometer")) {
				//special check for existing Skillometer: it's a Tool in Alg2 and Goem now
				ObjectProxy op = getChildByType(childType);
				if(op != null)
					throw new MessageFormatException("Object of type "+childType+" already exists");
				startTool("skillometer.SkillometerProxy", childType, inEvent);
			}
			else if(childType.equalsIgnoreCase("Solver")) {
				addToMessageObject(inEvent, "IMAGEBASE",StudentInterface.fileDir); 
				startTool("cmu.toolagent.GeomSolverProxy", childType, inEvent);
			}
			else if(childType.equalsIgnoreCase("Glossary")) {
				addToMessageObject(inEvent, "URLBASE",StudentInterface.fileDir); 
				startTool("cmu.toolagent.GlossaryProxy", childType, inEvent);
			}
			else if(childType.equalsIgnoreCase("ReasonTool")) {
				addToMessageObject(inEvent, "URLBASE",StudentInterface.fileDir); 
				startTool("cmu.toolagent.ReasonProxy", childType, inEvent);
			}	
			else if(childType.equalsIgnoreCase("Diagram")) {
				addToMessageObject(inEvent, "URLBASE",StudentInterface.fileDir); 
				startTool("cmu.toolagent.DiagramProxy", childType, inEvent);
			}
			else if(childType.equalsIgnoreCase("RatioTool")) 
				startTool("cmu.toolagent.RatioProxy", childType, inEvent);
			
			else if(childType.equalsIgnoreCase("DiagramDrawTool")) 
				startTool("geometrypad.DiagramDrawProxy", "Diagram", inEvent);
				
			else if (childType.equalsIgnoreCase("Frame")) 
				startTool("wizard.WizardProxy", childType, inEvent);
			
			else if (childType.equalsIgnoreCase("GraphingSetup")) 
				startTool("cmu.toolagent.GraphingSetupProxy", childType, inEvent);
				
			else if (childType.equalsIgnoreCase("Grapher")) 
				startTool("GRAPHER.GraphProxy", childType, inEvent);
			
			else if(childType.equalsIgnoreCase("FactoringTool")) 
				startTool("cmu.toolagent.FactoringProxy", childType, inEvent);
			 
			else if(childType.equalsIgnoreCase("QuadraticFormulaTool")) 
				startTool("cmu.toolagent.QFormulaProxy", childType, inEvent);
			
			else if (childType.equalsIgnoreCase("RationalExpressionTool")) 
				startTool("cmu.toolagent.RationalProxy", childType, inEvent);
			
			else {
				super.create(inEvent);
			}
		}
		catch (DorminException e) { 
			throw e; 
		}
	}

	private BR_Controller controller;
	
	public void quit(MessageObject inEvent){
		try{
			// trace.out (5, "cmu.toolagent.InterfaceProxy", "quitting");
			//hideAll();							
			//deleteProxy();
			//ObjectRegistry.knownObjects.getAllObjects().clear();
			if(agent != null)
				agent.stop();
				
			SingleSessionLauncher launcher = new SingleSessionLauncher();
			controller = launcher.getController();
			controller.closeApplication(false);		
			
		} catch (SecurityException e){
			// trace.out (10, "cmu.toolagent.InterfaceProxy","security exception: " + e);
			((StudentInterface)getObject()).setIsFinished(true);
		}
	}
	
	
	public void hideAll(){
		Hashtable hash = ObjectRegistry.getAllObjects();
		Enumeration objects = hash.elements();
		Object obj;
		while(objects.hasMoreElements()) {
  			obj = objects.nextElement();  	
  			if((obj instanceof Frame)){
  				//((Frame)obj).setVisible(false);
  				((Frame)obj).hide();	
			}
		}
	}
		
    public void hideAllButSkillometer()
    {
        Hashtable hash = ObjectRegistry.getAllObjects();
        Enumeration objects = hash.elements();
        Object obj;
        while(objects.hasMoreElements()) {
            obj = objects.nextElement();  	
//            if((obj instanceof Frame) && !(obj instanceof SkillometerFrame)){
//  				//((Frame)obj).setVisible(false);
//                ((Frame)obj).hide();	
//            }
        }
    }

	/**
	* allows to add a property name/value to the property list
	**/
	public MessageObject addToMessageObject(MessageObject mo, String pName, Object pValue){
		Vector proNames = null;
		try{
			proNames = mo.extractListValue("PROPERTYNAMES");
		} catch (DorminException e){ }
		if(proNames != null){
			mo.addParameterToVectors("PROPERTYNAMES", pName,"L");
			mo.addParameterToVectors("PROPERTYVALUES", pValue, "L");
		}
		else{
			Vector pNames = new Vector();
			pNames.addElement(pName);
			Vector pValues = new Vector();
			pValues.addElement(pValue);
			mo.addParameter("PROPERTYNAMES", pNames);
			mo.addParameter("PROPERTYVALUES", pValues);
		}
		return mo;
	}
	
	public void startTool(String toolEntryClass, String type, MessageObject inEvent){
		try{

			if (toolEntryClass.equals("skillometer.SkillometerProxy"))
				toolEntryClass =  "edu.cmu.old_pact." + toolEntryClass;	

			Class cl = Class.forName(toolEntryClass);
			Object clInst = cl.newInstance();
			ObjectProxy op =(ObjectProxy)clInst;
			op.init(this, type);
			op.mailToProxy(inEvent);
		}
		catch (ClassNotFoundException e){
			e.printStackTrace();
		}
	 	catch (InstantiationException e){
			e.printStackTrace();
		}
		catch (IllegalAccessException e){
			e.printStackTrace();
		}
		catch (DorminException e){
			e.printStackTrace();
		}
	}

	public void setAgent(Agentable t){
		agent = t;
	}
	
}
