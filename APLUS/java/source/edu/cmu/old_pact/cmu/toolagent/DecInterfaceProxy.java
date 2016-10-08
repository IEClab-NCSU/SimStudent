//d:/Pact-CVS-Tree/Tutor_Java/./src/Decimal/Pc/Interface_with_tool_2/DecInterfaceProxy.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Frame;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.cmu.spreadsheet.DecimalWorksheetProxy;
import edu.cmu.old_pact.cmu.spreadsheet.DecimalWorksheetProxy_labels;
import edu.cmu.old_pact.cmu.spreadsheet.DecimalWorksheetProxy_money;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.toolframe.DorminToolFrame;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;
import edu.cmu.old_pact.wizard.WizardProxy;
import edu.cmu.pact.Utilities.trace;

public class DecInterfaceProxy extends InterfaceProxy {

	// Constructors
	public DecInterfaceProxy() {
		super();
	}
				
	public  void create(MessageObject inEvent) throws DorminException{
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");
			
			trace.out (10, "cmu.toolagent.DecinterfaceProxy", "Create: " + childType);
			if (childType.equalsIgnoreCase("Frame")) {
				WizardProxy wsp = new WizardProxy(this, childType);
				 wsp.mailToProxy(inEvent, (new Vector()));
				 DorminToolFrame tf = (DorminToolFrame)wsp.getObject();
				 try{
				 	Object p_name = ((StudentInterface)getObject()).getProperty("PROBLEMNAME");
				 	if(p_name != null)
				 		tf.setProperty("ProblemName",p_name); 
				 } catch (NoSuchPropertyException e) { }
			}
			// for DeciamlArithTool without any Labels		
			else if (childType.equalsIgnoreCase("DecimalArithTool_no_labels")) {
				DecimalWorksheetProxy wsp = new DecimalWorksheetProxy(this);
				wsp.mailToProxy(inEvent, (new Vector()));
				DorminToolFrame tf = (DorminToolFrame)wsp.getObject();
				 try{
				 	Object p_name = ((StudentInterface)getObject()).getProperty("PROBLEMNAME");
				 	if(p_name != null)
				 		tf.setProperty("ProblemName",p_name); 
				 } catch (NoSuchPropertyException e) { }
			}
			// for DecimalArithTool with Place Value Labels 
			else if (childType.equalsIgnoreCase("DecimalArithTool_labels")) {
				DecimalWorksheetProxy_labels wsp = new DecimalWorksheetProxy_labels(this);
				wsp.mailToProxy(inEvent, (new Vector()));
				DorminToolFrame tf = (DorminToolFrame)wsp.getObject();
				 try{
				 	Object p_name = ((StudentInterface)getObject()).getProperty("PROBLEMNAME");
				 	if(p_name != null)
				 		tf.setProperty("ProblemName",p_name); 
				 } catch (NoSuchPropertyException e) { }
			}
			// for DecimalArithTool with Monetary Unit Labels 
			else if (childType.equalsIgnoreCase("DecimalArithTool_money")) {
				DecimalWorksheetProxy_money wsp = new DecimalWorksheetProxy_money(this);
				wsp.mailToProxy(inEvent, (new Vector()));
				DorminToolFrame tf = (DorminToolFrame)wsp.getObject();
				 try{
				 	Object p_name = ((StudentInterface)getObject()).getProperty("PROBLEMNAME");
				 	if(p_name != null)
				 		tf.setProperty("ProblemName",p_name); 
				 } catch (NoSuchPropertyException e) { }
			}
			
          		
			
			else if (childType.equalsIgnoreCase("Grapher")) 
				startTool("GRAPHER.GraphProxy", childType, inEvent);
			
			/*
			else if (childType.equalsIgnoreCase("Grapher")) {
				GrapherCreator gc = new GrapherCreator("Grapher", ((StudentInterface)getObject()).fileDir,this);
				GrapherFrame gf = gc.getGrapher();
				Vector proName = null;
				try{
					proName = inEvent.extractListValue("PROPERTYNAMES");
				} catch (DorminException e){ }
				if(proName != null){
					ObjectProxy gr_proxy = gf.getObjectProxy();
					gr_proxy.setRealObjectProperties((Sharable)gf, inEvent);
				}
				MenuBar menuBar = MenuFactory.getGeneralMenuBar(gf, gf.getName());
				gf.setMenuBar(menuBar);
				//gf.setVisible(true); 
			}
			*/
			else if (childType.equalsIgnoreCase("DiagramDrawTool") || 
					 childType.equalsIgnoreCase("DiagramDrawTool_Fraction") ||
					 childType.equalsIgnoreCase("DiagramDrawTool_Picture") ||
					 childType.equalsIgnoreCase("DiagramDrawTool_Picture_w_arrows") ||
					 childType.equalsIgnoreCase("DiagramDrawTool_Angles") ||
					 childType.equalsIgnoreCase("DiagramDrawTool_ScatterPlot")) {
				//DorminPadFrame pf = new DorminPadFrame(this);
				
				String mode = "default";
				
				if (childType.equalsIgnoreCase("DiagramDrawTool_Fraction"))
					mode = "Fraction";
				else if (childType.equalsIgnoreCase("DiagramDrawTool_Picture"))
					mode = "Picture";	
				else if (childType.equalsIgnoreCase("DiagramDrawTool_Picture_w_Arrows"))
					mode = "Picture_w_arrows";	
				else if (childType.equalsIgnoreCase("DiagramDrawTool_ScatterPlot"))
					mode = "ScatterPlot";
				else if (childType.equalsIgnoreCase("DiagramDrawTool_Angles"))
					mode = "Angles";
					
				trace.out (10, "cmu.toolagent.DecinterfaceProxy", "done creating");
			}

			
			else {
				super.create(inEvent);
			}
		}
		catch (DorminException e) { 
			throw e; 
		}
	}
	
	
	public void quit(MessageObject inEvent){
	
		trace.out (5, "DecinterfaceProxy", "quitting");
			
		try{
			closeAllWindows();
			System.exit(0);
		} catch (SecurityException e){
			System.exit(1);
		}
		
		super.quit(inEvent);
	}
	
	public void closeAllWindows(){
		Hashtable hash = ObjectRegistry.getAllObjects();
		if(hash.size() == 0)
			return;
		Enumeration objects = hash.elements();
		Object obj;
		while(objects.hasMoreElements()){
			obj = objects.nextElement();
			if((obj instanceof Frame)){
				synchronized(obj){
					((Frame)obj).hide();
				}
			}
		}
	}
		
}