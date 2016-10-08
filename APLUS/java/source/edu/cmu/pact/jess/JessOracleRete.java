package edu.cmu.pact.jess;

import java.awt.HeadlessException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

import sun.security.action.GetBooleanAction;
import jess.Activation;
import jess.Context;
import jess.Defrule;
import jess.Fact;
import jess.FactIDValue;
import jess.Jesp;
import jess.JessException;
import jess.JessToken;
import jess.RU;
import jess.Rete;
import jess.Strategy;
import jess.Value;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.TextOutput;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStNode;
import edu.cmu.pact.miss.jess.ModelTracer;
import edu.cmu.pact.miss.jess.GetCorrectSAI;
import edu.cmu.pact.miss.jess.UserFunctionPackage;



public class JessOracleRete extends SimStRete implements Serializable,JessParser{
	
	/** Serialized constant for compatibility */
	private static final long serialVersionUID = 1L;
	/*for the time being, Jess Oracle production rules file must be specific. Todo: pass it as a command line arqument*/
	public static final String JESS_ORACLE_PRODUCTION_RULES_FILE = "productionRulesJessOracle.pr";
	
	public static final String INIT_FILE = "init.wme";
	
	
	/** JessModelTracing */
//	public JessModelTracing jmt;
	
//	public JessModelTracing getJ`() {
	//	//return controller.getModelTracer().getModelTracing();
	//	return jmt;
//	}
	
	/** ModelTracer */
	private  MT mt;
	
	public MT getMt() {
		return mt;
	}

	private void setMt(MT mt) {
		this.mt = mt;
	}
	
	public JessOracleRete(BR_Controller brController) {
		super(brController);
		controller = brController;
		mt=controller.getModelTracer();
		//jmt = mt.getModelTracing();
		setJmt( mt.getModelTracing());
		MT.loadDefaultUserfunctions(controller.getModelTracer(), this, null);
		ConflictResolutionStrategy.setStrategy(this);
	}

	/** Boolean flag to indicate if .bload file was successfully found/loaded. */
	private boolean bloadSuccessful = false;

	public boolean isBloadSuccessful() {
		return bloadSuccessful;
	}

	public void setBloadSuccessful(boolean bloadSuccessful) {
		this.bloadSuccessful = bloadSuccessful;
	}

	/** Boolean flag to indicate if wmeTypes.clp file was successfully found/loaded */
	private boolean wmeTypesFromFile = false;

	public boolean isWmeTypesFromFile() {
		return wmeTypesFromFile;
	}

	public void setWmeTypesFromFile(boolean wmeTypesFromFile) {
		this.wmeTypesFromFile = wmeTypesFromFile;
	}

	/** Boolean flag to indicate if productionRules.pr file was successfully found/loaded */
	private boolean prRulesFromFile = false;

	public boolean isPrRulesFromFile() {
		return prRulesFromFile;
	}	
	
	
	public void setPrRulesFromFile(boolean prRulesFromFile) {
		this.prRulesFromFile = prRulesFromFile;
	}

	/** Boolean flag to indicate if init.wme file was successfully found/loaded. */
	private boolean wmeInstancesFromFile = false;

	public boolean isWmeInstancesFromFile() {
		return wmeInstancesFromFile;
	}

	public void setWmeInstancesFromFile(boolean wmeInstancesFromFile) {
		this.wmeInstancesFromFile = wmeInstancesFromFile;
	}
	
	/** Console for Jess */
	private transient JessConsole console;
	
	public JessConsole getConsole() {
		return console;
	}

	public void setConsole(JessConsole console) {
		this.console = console;
	}

	/**
	 * @param problemName
	 * @param useBinary
	 * Initialize the working memory by loading the jess files namely init.wme, wmeTypes.clp
	 * and productionRules.pr
	 */
	public void initWMUsingJessFiles(String problemName, boolean useBinary) {	

		loadJessFiles(problemName, useBinary);
		
       try {
           if (!bloadSuccessful) {
               // define the global variables
               this.eval("(defglobal ?*sSelection* = "+SimStRete.NOT_SPECIFIED+")");
               this.eval("(defglobal ?*sAction* = "+SimStRete.NOT_SPECIFIED+")");
               this.eval("(defglobal ?*sInput* = "+SimStRete.NOT_SPECIFIED+")");
           }
       } catch (HeadlessException e1) {
           e1.printStackTrace();
       } catch (JessException je) {
           String errMsg = "Error defining selection-action-input globals";
           console.getTextOutput().append("\n"+errMsg+":\n"+je);
           if (trace.getDebugCode("mt")) trace.out("mt", "ERROR CREATING S/A/I GLOBALS: "+je);
           JOptionPane.showMessageDialog(null,errMsg+":\n"+je.getDetail(),
                   "Jess Warning",JOptionPane.WARNING_MESSAGE);
       }

       if(!wmeInstancesFromFile){
           try {
               if(findDeftemplate("problem") != null){
                   Fact problemFact = new Fact(findDeftemplate("problem"));
                   problemFact.setSlotValue("name", new Value(problemName, RU.SYMBOL));
                   assertFact(problemFact);
                   if (trace.getDebugCode("mt")) trace.out("mt", "StartStateEnd: new problem fact id: " +
                           new FactIDValue(problemFact));
               }
           }catch(Exception ex){
              // console.getTextOutput().append("\n" + ex.toString() + "\n");
              // ex.printStackTrace();
           }
       }			 
       clearRuleActivationTree();
       showActivations("StartStateEnd");  // debug routine
	}

	/**	Clears the rule activation tree */
	private void clearRuleActivationTree(){
		if(controller.getModelTracer().getModelTracing() == null)
			return;
		RuleActivationTree tree= controller.getModelTracer().getModelTracing().getRuleActivationTree();
		if(tree != null){
			tree.clearTree(null);
		}
	}
	
	
public void restoreInitialWMState(ProblemNode startNode, boolean loadJessFiles) {
		
		try {
			init(startNode.getName(), loadJessFiles);
		} catch (JessException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
}
	
private void loadJessFiles(String problemName, boolean useBinary) {

		String[] filenames = { problemName + ".bload", 
				WMEEditor.wmeTypeFileName, 
				JESS_ORACLE_PRODUCTION_RULES_FILE, 
				INIT_FILE, problemName + ".wme" }; 

		//System.out.println("ss" + Arrays.toString(filenames));

		boolean[] results = null;
		for (int i = 0; i < filenames.length; ++i) {
			filenames[i] = getDirectory(filenames[i]) + filenames[i]; // getDirectory has a trailing "/" separator
		}

		if (!useBinary)
			filenames[0] = null;

		try {
			clear();
			results = loadJessFiles(filenames[0], filenames[1],
					filenames[2], filenames[3], filenames[4],
					null);
		} catch (Exception e) {
			trace.err("Error " + e + " trying to load Jess files");
			return; // results[] null after exception: leave booleans false
		}
	}


/** BehaviorRecorder Controller */
private transient BR_Controller controller;

public BR_Controller getController() {
	return controller;
}

/**
* @param origFilename
* @return the problem model directory
* Returns the problem model directory if the specified file is located
* there, otherwise, returns the project directory if the specified file is
* located there, otherwise, returns null
*/
protected String getDirectory(String origFilename) {
	
	/* If initialization wme files not in cog. model dir, try project dir */
	File f = null;
	String dirname = getProblemDirectory();
	boolean validFilename = checkFilenameValid(dirname + origFilename);
	if (trace.getDebugCode("rr"))
		trace.out("rr","MT.getDirectory(): getProblemDirectory() result "
								+ dirname + "+" + origFilename + " valid? "
								+ validFilename);
	if (!validFilename) {
		dirname = controller.getPreferencesModel().getStringValue(
				BR_Controller.PROJECTS_DIR)
				+ "/";
		if (!checkFilenameValid(dirname + origFilename))
			dirname = null;
	}

	return dirname;
}

/* Does the file exist? */
private boolean checkFilenameValid(String filename) {
	File f;
	if (filename != null && filename.length() > 0)
		f = new File(filename);
	else
		return false;
	if (f == null || !f.isAbsolute() || !f.exists()) {
		File result = Utils.getFileAsResource((filename), this);
		if (trace.getDebugCode("rr"))
			trace.out("rr", "rr.checkFilenameValid(" + filename
					+ ") file result " + result);
		if (null == result || !((File) result).exists()) {
			URL url = Utils.getURL(filename, this);
			if (trace.getDebugCode("rr"))
				trace.out("rr", "rr.checkFilenameValid(" + filename
						+ ") URL result " + url);
			return (url != null);
		}
	}
	return true;
}


/**
* @param problemName
* Loads the user-defined functions, initialize the working memory for the problem-name.
* @throws Exception 
*/
public void init(String problemName, boolean loadJessFiles) throws Exception {
		
	ArrayList<String> startElements = null;
	if((getController().getMissController() != null) && (getController().getMissController().getSimStPLE() != null)){
		
		startElements = getController().getMissController().getSimStPLE().getStartStateElements();
	}
	else if((getController().getMissController() != null) && (getController().getMissController().getSimSt().isValidationMode()))
	{
		startElements = new ArrayList<String>();
		String file = this.getController().getMissController().getSimSt().getPackageName()+"/"+SimStPLE.CONFIG_FILE;
		
		
   	ClassLoader cl = this.getClass().getClassLoader();
   	InputStream is = cl.getResourceAsStream(file);
   	InputStreamReader isr = new InputStreamReader(is);
   	BufferedReader reader=null;
   	try
   	{
       	reader = new BufferedReader(isr);
   		String line = reader.readLine();
   		
   		while(line != null)
   		{
   			if(line.equals(SimStPLE.START_STATE_ELEMENTS_HEADER))
   			{
   				line = reader.readLine();
   				while(line != null && line.length() > 0) //Blank line starts next section
   				{
   					startElements.add(line);
   					line = reader.readLine();
   				}
   			}
   			if(line != null)
   			{
   				line = reader.readLine();
   			}
   		}

   	}catch(Exception e)
   	{
   		if(trace.getDebugCode("miss"))trace.out("miss", "Unable to read config file: "+e.getMessage());
   		e.printStackTrace();
   	}finally 
   	{
   		try{reader.close();}catch(Exception e){	}
   	}
	}
	else {
		throw new Exception("Need to initialize the working memory with start state elements.");
	}

	
	
	SimStRete.PROBLEM_NAME = SimSt.convertFromSafeProblemName(problemName); // Before updating the WM, convert the equation to its original form  
	//String[] problem = SimStRete.PROBLEM_NAME.split("=");
	//getController().resetAllWidgets();
	
	
	if(loadJessFiles) {
		MT.loadDefaultUserfunctions(controller.getModelTracer(), this, null);
		initWMUsingJessFiles(problemName, false);
	}
	
	/* 
	 * if(problem.length == 2 && !(problem[0].isEmpty()) && !(problem[1]).isEmpty()) {
			setSAIDirectly(startElements.get(1), SimStRete.ACTION, problem[1]);
			setSAIDirectly(startElements.get(0), SimStRete.ACTION, problem[0]);
		}
	 */	
	
	String[] problem=SimStRete.PROBLEM_NAME.split(controller.getMissController().getSimSt().getProblemDelimiter());
	if (problem[0].contains("="))
		problem=SimStRete.PROBLEM_NAME.split("=");
	if (problem[0].isEmpty()) return;
	
				for (int i=0;i<startElements.size();i++){				
						setSAIDirectly(startElements.get(i), SimStRete.ACTION, problem[i]);
				}
		
}

/**
* Class for conflict resolution. Does the reordering of the agenda in the activation
* and determines which agenda in the activation to fire next.
*/
public static class ConflictResolutionStrategy implements Strategy, Serializable {

	private static final long serialVersionUID = 1L;

	/** The strategy that was in use before this one. */
	private Strategy oldStrategy = null;
	
	/** The activation to fire. */
	private Activation actToFire = null;
	
	/** Cached reference to {@link ConflictResolutionStrategy#actToFire}. */
	private Activation cachedActToFire = null;
	
	private int cacheUse;
	
	private void setActToFire(Activation act, Rete r){
		actToFire = act;
		cachedActToFire = null;
		cacheUse = 0;
	}
	
	private static Strategy setStrategy(Rete r){
		
		Strategy oldStrategy = null;
		try {
			oldStrategy = r.getStrategy();
			ConflictResolutionStrategy crs = new ConflictResolutionStrategy();
			crs.oldStrategy = oldStrategy;
			r.setStrategy(crs);
			return oldStrategy;
		} catch(JessException e){
			e.printStackTrace();
			return oldStrategy;
		}
	}
	
	/**
	 * Compares two activations and determines which one to fire next.
	 * @param arg0 - first activation
	 * @param arg1 - second activation
	 * 
	 */
	@Override
	public int compare(Activation arg0, Activation arg1) {
		
		int result = 0;
		Defrule r0 = arg0.getRule();
		Defrule r1 = arg1.getRule();
		if(cachedActToFire != null){
			cacheUse++;
			if(arg0 == cachedActToFire) {
				result = -1;
			} else if(arg1 == cachedActToFire) {
				result = 1;
			}
		} else if(actToFire != null) {
			if(arg0.equals(actToFire)){
				cachedActToFire = arg0;
				result = -1;
			} else if(arg1 == actToFire){
				cachedActToFire = arg1;
				result = 1;
			}
		}
		
		if(result == 0) {
			result = oldStrategy.compare(arg0, arg1);
		}
		return result;
	}

	@Override
	public String getName() {
		return "conflict-resolution-strategy";
	}
}	

}