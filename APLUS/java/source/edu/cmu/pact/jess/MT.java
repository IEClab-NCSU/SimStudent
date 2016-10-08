//new model-tracing code - realtime why-not
// This version makes the check with lisp non blocking

package edu.cmu.pact.jess;

import java.awt.HeadlessException;
import java.io.File;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import jess.Deftemplate;
import jess.Fact;
import jess.FactIDValue;
import jess.Funcall;
import jess.JessException;
import jess.RU;
import jess.Rete;
import jess.Userfunction;
import jess.Userpackage;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.Log.LogReteChanges;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.Utilities.MessageEvent;
import edu.cmu.pact.Utilities.MessageEventListener;
import edu.cmu.pact.Utilities.MessageEventSupport;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.model.ProblemSummary;

/**
 * This class has functionality for model tracing and also for handling the comm messages
 * 
 * @author sanket@wpi.edu
 */
/**
 * @author sdemi
 *
 */
/**
 * @author sdemi
 *
 */
public class MT {

	/**
	 * Get the Jess version.
	 * @return jess-version-string function result; null if any error
	 */
	public static String getJessVersion() {
		if (!VersionInformation.includesJess())
			return null;
		try {
			jess.Rete r = new jess.Rete();
			jess.Value result = r.eval("(jess-version-string)");
			String strResult = result.stringValue(null);
			if (strResult != null && strResult.length() > 0)
				return strResult;
			else
				return null;
		} catch (jess.JessException je) {
			je.printStackTrace();
			return null;
		}
	}

    ArrayList ruleSeq;
/**
 * This list holds the sequence of stack of rules after the model tracing algorithm
 */
    ArrayList activationSeq;

    // Mon Oct 10 17:16:11 2005 :: Noboru
    // Keep a sequence of rules used to model trace the current step
    ArrayList mtRuleSeq = null;
    public ArrayList getMtRuleSeq() { return mtRuleSeq; }

/**
 * A vector for holding the hints messages
 */	
	Vector messages = new Vector();
	/**
	 * Vector for holding the buggy messages
	 */
	Vector buggyMessages = new Vector();
	
	/** An externally-defined Rete instance. */
	private MTRete externallyDefinedRete;
	    
    private MTRete rete;
    Stack ruleStack;
    
    private WMEEditor wmeEditor;

    private String problemName;
	
	/** The Jess Console itself. */
	private JessConsole console;
	
	/**
	 * Boolean variable indicating whether to use the debugger or not
	 */    
    boolean useDebugger = true;

	/**
	 * List of Jess commands from InterfaceDescription msgs to define
	 * deftemplates for the user interface elements (widgets) in working
	 * memory.  Each entry is a List of commands.
	 */
	private java.util.List interfaceTemplatesList = null;
	
	/**
	 * Lists of Jess commands from InterfaceDescription msgs to assert facts
	 * that instantiate the user interface elements (widgets) in the rule
	 * engine.  Each entry is a List of commands.
	 */
	private java.util.List interfaceInstanceLists = null;
	
	/**
	 * List of {@link InterfaceAction} to delay Jess modify commands from
	 * InterfaceAction messages until after facts have been asserted.
	 */
	private java.util.List<InterfaceAction> interfaceActionsList = null;
	
	/**
	 * Map to {@link jess.Fact}s that represent student interface components
	 * or widgets. Key is (String) name (as seen in selection field of student
	 * interaction messages); value is Fact.
	 */
	private java.util.Map<String, Fact> interfaceFactsMap = null;

	/**
	 * List of special facts used for model tracing.
	 */
	private java.util.List specialFactsList = null;


	Vector edgeList = new Vector();

	/**
	 * Flag indicates whether the state has been loaded from a file
	 * or not.
	 */
	private boolean bloadSuccessful = false;

	/**
	 * Flag indicates whether the wme types have been loaded from a file
	 * or not. Previous name was result1.
	 */
	private boolean wmeTypesFromFile = false;

	/**
	 * Flag indicates whether the production rules have been loaded
	 * or not.
	 */
	private boolean prRulesFromFile = false;

	/**
	 * Boolean indicating whether the wme instances have been loaded from
	 * a file or not. Previous name was result2.
	 */
	private boolean wmeInstancesFromFile = false;

	static String NOTAPPLICABLE = "No-Applicable";
	static String SUCCESS = "SUCCESS";
	static String NOMODEL = "NO-MODEL";
	static String BUG = "BUG";
	static String FIREABLEBUG = "FIREABLE-BUG";
	
	JessModelTracing jmt;
	// to enable asynchronous communication between the client and the server 

	Vector<MessageObject> groupDescriptionMessages = new Vector<MessageObject>();
	
	ArrayList initialState = new ArrayList();			
				
	boolean useProtege = false;

	/**
	 * Delegate for generating {@link edu.cmu.pact.Utilities.MessageEvent} events.
	 */
	private MessageEventSupport msgEvtSupport = new MessageEventSupport();
	
	/**	For running messages in order. */
	private ProdSysCheckMessgHandler.Queue prodSysCheckMessgHandlerQ =
		new ProdSysCheckMessgHandler.Queue();

	/**
	 * Class holds initial value settings from InterfaceAction messages.
	 * Allows us to save these from the Start State messages until the
	 * Start State End message: then we can evaluate rules before asserting
	 * facts.
	 */
	private class InterfaceAction {

		/** Selection vector from message. */
		final List selectionList;

		/** Action vector from message. */
		final List actionList;

		/** Input vector from message. */
		final List inputList;

		/**
		 * Constructor for vector s-a-i elements.
		 * @param selection
		 * @param action
		 * @param input
		 */
		InterfaceAction(List selection, List action, List input) {
			selectionList = (selection == null ? new LinkedList() : selection);
			actionList = (action == null ? new LinkedList() : action);
			inputList = (input == null ? new LinkedList() : input);
		}
	}

	private CTAT_Controller controller;
	
	/**
	 * Reference to the {@link AlgExprFunctions} instance, for updating. The instance is loaded
	 * in {@link #loadDefaultUserfunctions(Object, Rete, JessModelTracing)}
	 */
	private AlgExprFunctions algExprFns;

	/**
	 * Get a named preference port.
	 * @param preferenceName preference to get
	 * @return -1 if no interface is present; else the port number from the
	 *         {@link PreferencesModel}
	 */
	private int getEclipsePluginPort(String preferenceName) {
    	if (controller == null)
			return -1; 
	    PreferencesModel pm = controller.getPreferencesModel();
		if (pm == null)
			return -1;
		Integer port = pm.getIntegerValue(preferenceName);
		if (port == null)
			return -1;
		else
			return port.intValue();
	}

	/**
	 * Create a new instance of the {@link MTRete} rule engine.
	 * Sets {@link #rete}.
	 */
	public void clearRete() {
		if(trace.getDebugCode("mt"))
			trace.printStack("mt", "*****MT.clearRete() called*****");
		
		if(rete != null)               // try to conserve memory: housekeeping for the listeners
			rete.removeListeners(controller.getPreferencesModel());
		
		specialFactsList = new java.util.LinkedList();
		interfaceInstanceLists = new java.util.LinkedList();
		interfaceTemplatesList = new java.util.LinkedList();
		interfaceActionsList = new java.util.LinkedList<InterfaceAction>();
		interfaceFactsMap = new java.util.HashMap<String, Fact>();
		bloadSuccessful = false;
		wmeTypesFromFile = false;
		prRulesFromFile = false;
		wmeInstancesFromFile = false;
		
		if (externallyDefinedRete != null) {
			rete = externallyDefinedRete;
			rete.setMT(this);
			return;
		}
		rete = new MTRete(controller);
		rete.setMT(this);
		try {
			rete.reset();
			rete.showActivations("clearRete: reset");
		} catch (JessException e) {
			rete.getTextOutput().append("\nclearRete() error on (reset): "+e);
			e.printStackTrace();
		}
		loadDefaultUserfunctions(this, rete, jmt);
		if (jmt != null) {
			jmt.setRete(rete);
			rete.setJmt(jmt);
		}
		clearRuleActivationTree();
		if (wmeEditor != null)
			wmeEditor.setRete(rete);
		if (console != null) {
			console.setRete(rete);
			console.getTextOutput().append("\n\n*****Engine Cleared*****\n\n");
		}
		MTRete.Routers.dumpRouters("clearRete()", rete);
  	}

	/**
	 * Load the default sets of (@link Userfunction}s and {@link Userpackage}s. 
	 * @param obj use this to get the package name; if an instance of {@link MT}, also gets variable table
	 * @param rete
	 * @param jmt
	 */
    public static void loadDefaultUserfunctions(Object obj, Rete rete, JessModelTracing jmt) {
		String thisPkg = obj.getClass().getPackage().getName();
		String[] userfunctions = {
			thisPkg+".ConstructMessage",	
			thisPkg+".ConstructErrorMessage",	// sewall: CTAT2026 13 Aug 2008
			thisPkg+".ConstructHintMessage",	// sewall: CTAT2026 13 Aug 2008
			thisPkg+".ConstructSuccessMessage",	// sewall: CTAT2026 1 Aug 2008
			thisPkg+".PredictObservableAction",
			thisPkg+".LHSPredictObservableAction", //eep: 6/6/2011
			thisPkg+".predict",
			thisPkg+".TestSAI",
			thisPkg+".PredictAlgebraInput",
            thisPkg+".HereIsTheListOfFoas", //Gustavo 10 May 2007
			thisPkg+".PredictStoichInput",
			thisPkg+".eval",
			thisPkg+".LogWorkingMemory",
			thisPkg+".PerformTutorAction",
			thisPkg+".MTRegexpMatch",
			thisPkg+".IsHint",
			thisPkg+".UseStudentValuesFact",
			thisPkg+".SetMaximumChainDepth",
			thisPkg+".UseProblemSummary",
			thisPkg+".GetCustomFieldsFact",
			thisPkg+".SetHintPolicy",
			thisPkg+".SetDefaultSkillCategory"
		};
		if(trace.getDebugCode("dumpjessinfo")) {
			System.out.println("Jess Userfunction instances loaded:");
			for(String ufn : userfunctions) {
				try {
					Class<Userfunction> cls = (Class<Userfunction>) Class.forName(ufn);
					Userfunction uf;
					uf = cls.newInstance();
					System.out.printf(" (%s ...)\n", uf.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		FunctionPackage fp = new FunctionPackage(userfunctions, jmt);
		if (fp.size() < userfunctions.length)
			trace.err("Jess Model Tracer Warning: only "+fp.size()+" of "+
					userfunctions.length+" user functions loaded");
		rete.addUserpackage(fp);
		
		AlgExprFunctions algExprFns = new AlgExprFunctions();
		if(trace.getDebugCode("dumpjessinfo")) {
			for(Userfunction uf : algExprFns) {
				try {
					System.out.printf(" (%s ...)\n", uf.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(trace.getDebugCode("functions"))
			trace.out("functions", "MT.loadDefaultUserfunctions() #algExprFns "+algExprFns.size()+
					", obj "+obj);
		Userpackage[] userpackages = {
				algExprFns
		};
		for (Userpackage up : userpackages)
			rete.addUserpackage(up);
		if(obj instanceof MT)
			((MT) obj).setAlgExprFunctions(algExprFns);
	}

    /**
     * Also calls {@link #setVariableTableInFunctions()}.
     * @param algExprFns new value for {@link #algExprFns}
     */
    private void setAlgExprFunctions(AlgExprFunctions algExprFns) {
		this.algExprFns = algExprFns;
		setVariableTableInFunctions();
	}

	/**
     * Constructor for internally-defined {@link MTRete} instance.
     */
    public MT(CTAT_Controller controller) {
    	this(controller, null);
    }

    /**
     * Constructor for externally-defined {@link MTRete}.
     * @param controller
     * @param externallyDefinedRete
     */
	MT(CTAT_Controller controller, MTRete externallyDefinedRete){
		if (trace.getDebugCode("mt")) trace.out("mt", "MT constructor");
		this.controller = controller;
		this.setExternalRete(externallyDefinedRete);
		
		
		
    }
    
    /**
     * Parse an input stream in the operational Rete.
     * @param rdr stream to parse
     * @return result of {@link MTRete#parse(Reader) rete.parse(rdr)}
     * @throw JessException from parse
     */
    public Value parse(Reader rdr) 
    		throws JessException {
    	if (getRete() == null)
    		return null;
    	else
    		return getRete().parse(rdr);
    }

	/**
	 * Add a listener for {@link edu.cmu.pact.Utilities.MessageEvent} objects
	 * emitted when this class receives or sends a MessageObject.
	 *
	 * @param  listener listener instance to add
	 */
	public void addMessageEventListener(MessageEventListener listener) {
		msgEvtSupport.addMessageEventListener(listener);
	}

	/**
	 * Remove a listener for {@link edu.cmu.pact.Utilities.MessageEvent} objects
	 * emitted when this class receives or sends a MessageObject.
	 *
	 * @param  listener listener instance to remove
	 */
	public void removeMessageEventListener(MessageEventListener listener) {
		msgEvtSupport.removeMessageEventListener(listener);
	}

	
	public WMEEditor getWmeEditor() {
		return wmeEditor;
	}
	
    /**
	 * @return Returns the console.
	 */
	public JessConsole getConsole() {
		return console;
	}

	/**
	 * @return Returns the jmt.
	 */
	public JessModelTracing getModelTracing() {
		return jmt;
	}

	public void destroy(){
		ruleSeq = null;
		activationSeq = null;
		ruleStack = null;
		rete = null;
		
//		console.dispose();
		jmt.dispose();		
//		tree.dispose();
    }

    /**
     * tests whether the current selection-action-input match the corresponding 
     * selection-action-input in the current state vector
     * 
     * @param SAI       The selection-action-input fact from the current state
     * @param selection the selection string
     * @param action    the action string
     * @param input     input string
     * 
     * @return true - if the selection-action-input matches
     *         false - if it does not match
     */
    public boolean doesMatchSAI(Fact SAI, String selection, String action, String  input){
		try{
	    
		    String s = "", a = "", i = "";
			try{
				s = SAI.getSlotValue("selection").stringValue(null).trim();
			}catch (JessException e) {
				if (console != null && console.getTextOutput() != null)
					console.getTextOutput().append("\nThe selection slot in the special" +
							"-tutor-fact should contain a value of type String. " +
							"The value should be " + selection);
				if (console != null && console.getTextOutput() != null)
					console.getTextOutput().append(e.toString());						
				e.printStackTrace();
			}
			try{
				a = SAI.getSlotValue("action").stringValue(null).trim();
			}catch(JessException e){
				if (console != null && console.getTextOutput() != null)
					console.getTextOutput().append("\nThe action slot in the special" +
							"-tutor-fact should contain a value of type String. " +
							"The value should be " + action);
				if (console != null && console.getTextOutput() != null)
					console.getTextOutput().append(e.toString());						
				e.printStackTrace();
			}
			try{
				i = SAI.getSlotValue("input").stringValue(null).trim();
			}catch(JessException e){
				if (console != null && console.getTextOutput() != null)
					console.getTextOutput().append("\nThe input slot in the special" +
					"-tutor-fact should contain a value of type String. " +
					"The value should be " + input);
				if (console != null && console.getTextOutput() != null)
					console.getTextOutput().append(e.toString());						
				e.printStackTrace();
			}
	    
		    if (s.equals(selection.trim()) && a.equals(action.trim()) && i.equals(input.trim()))
				return true;
		    else
				return false;
		}catch(Exception e){
		    if (trace.getDebugCode("mt")) trace.out("mt", "error in selection action action input fact");
		    e.printStackTrace();
		}
		return false;
    }

	/**
	 * Method extractRuleName.
	 * @param buggyRule
	 * @return String the name of the rule if exists else null
	 */
	String extractRuleName(String buggyRule) {
		StringTokenizer st = new StringTokenizer(buggyRule," ");
		while (st.hasMoreTokens()) {
			if(st.nextToken().indexOf("defrule") != -1){
				break;
			}
		}
		if(st.hasMoreTokens()){
			return st.nextToken();
		}
		return null;
	}

    public ArrayList getRuleSeq(){
		return ruleSeq;
    }

    /**
     * this method determines whether any buggy rules were used in model tracing.
     * 
     * @param rules  The list of rule sequences found by model-tracing algorithm
     * 
     * @return boolean true if buggy rules used false other wise
     */
    public boolean noBuggyRulesPresent(ArrayList<String> rules){
    	Iterator<String> it = rules.iterator();
    	String rule;
    	while (it.hasNext()) {
    		rule = (String)it.next();
			if (rule.indexOf("buggy") != -1 || rule.indexOf("BUGGY") != -1 || rule.indexOf("Buggy") != -1){
				return false;
			}
    	}
		return true;
    }

    public MTRete getRete(){
		return rete;
    }
    
    /**
     * Convenience method for logging author actions.  Calls target method
     * {@link LoggingSupport#authorActionLog(String, String, String, String, String)}.
     * @param toolName argument for target method
     * @param actionType argument for target method
     * @param argument argument for target method
     * @param result argument for target method
     * @param result2 argument for target method
     * @return false if logging environment not set up; else result of
     *         target method
     */
    boolean authorActionLog(String toolName, String actionType,
    		String argument, String result, String result2) {
    	if (controller == null)
    		return false;
    	LoggingSupport ls = controller.getLoggingSupport();
    	if (ls == null)
    		return false;
    	return ls.authorActionLog(toolName, actionType, argument,
    			result, result2);
    }
    
    /**
     * Get the directory named by the preference {@value CTAT_Controller#PROBLEM_DIRECTORY}.
     * @param subdir optional subdirectory to append
     * @return preference value, with trailing {@link File#separator}
     */
    String getCognitiveModelDirectory(String subdir) {
    	return getPreferenceDirectory(CTAT_Controller.PROBLEM_DIRECTORY, null, subdir);
    }
    
    /**
     * Get the directory named by the given preference.
     * @param pref preference name
     * @param defaultValue optional default value if preference not found or empty; null means no default
     * @param subdir optional subdirectory to append
     * @return preference value, with trailing {@link File#separator}; null if empty and no default
     */
    String getPreferenceDirectory(String pref, String defaultValue, String subdir) { 
    	String result = defaultValue;
    	if (controller != null) {
    		PreferencesModel pm = controller.getPreferencesModel();
    		if (pm != null) {
    			String prefValue = pm.getStringValue(pref);
    			if (prefValue != null && prefValue.length() > 0)
    				result = prefValue; 
    		}
    	}
    	if(result == null)
    		return null;

    	if(subdir != null && subdir.length() > 0)
        	result = Utils.appendSlash(result) + subdir;

    	return Utils.appendSlash(result);
    }
    
    /**
     * Process a comm message extract the values and update the facts
     * 
     * @param mo      The Comm Object
     */

    public MessageObject handleCommMessage (final MessageObject mo) {
        //int lispCheckResult;
        String checkResult = NOTAPPLICABLE;

        
        if (trace.getDebugCode("mt")) trace.out("mt", "message received: " + mo);

       // trace.out("miss", "@@@@@ handleCommMessage: globalContext = " + getRete().getGlobalContext());
        //trace.out("miss", "+++++++++++++++++++++ message received: +++++++++++++++++++");
        // trace.out("miss", "message received: " + mo.toString());

         
        Enumeration enumerator = null;

        if ("ActionRequest".equalsIgnoreCase(mo.getVerb()))
            return null;
        
        Vector<String> selection = null;
        Vector<String> action = null;
        Vector<String> input = null;

        //Vector<String> ruleNames = new Vector<String>(); // unused?
        //Integer actionLabelTagId; // unused?
        //String commWidget, commName;
        //Boolean updateEachCycle; // unused?
        int numberOfTables;
        //int numberOfRows, numberOfColumns;

        //      MTRete.stopModelTracing = false;
        // setting text area for displaying the exceptions thrown from Jess during model-tracing
        jmt.setErrorArea(console == null ? null : console.getTextOutput());

        String messageType = mo.getMessageType();
        if(messageType == null){
        	trace.err("null messageType to MT.handleCommMessage()");
            return null;
        }
        if (trace.getDebugCode("mt")) trace.out("mt", "message type: " + messageType);

        msgEvtSupport.fireMessageEvent(new MessageEvent(this, false,
                messageType, mo));

        // trace.out("miss", "@@@@@ handleCommMessage: globalContext<2> = " + getRete().getGlobalContext());
        
        if (messageType.equalsIgnoreCase("InterfaceDescription")) {
            if (!wmeTypesFromFile || !wmeInstancesFromFile){	

                Vector<String> jessDeftemplates = (Vector<String>) mo.getProperty("jessDeftemplates");
                Vector<String> jessInstances = (Vector<String>)mo.getProperty("jessInstances");

              
                
                // check for null values of the two vectors
                // store deftemplates until StartStateEnd msg
                if(jessDeftemplates != null && !wmeTypesFromFile)
                    interfaceTemplatesList.addAll(jessDeftemplates);

                
               
                
                // store fact assertion for processing after rules evaluated
                if (jessInstances != null && !wmeInstancesFromFile)
                    interfaceInstanceLists.add(jessInstances);
            }
            return null;
        }else if (messageType.equalsIgnoreCase("NumberOfTables")) {
            numberOfTables = 0;	   
        }else if (messageType.equalsIgnoreCase("StartProblem")) {
            if (trace.getDebugCode("mt")) trace.out("mt", "StartProblem");
            clearRete();
//            System.gc();
            MTRete.stopModelTracing = false;
            String fullName = getController().getProblemFullName();
            if(fullName != null && fullName.length() > 0) {
            	String[] path = fullName.split("[\\\\/]");
            	String baseName = path[path.length-1];
            	if(baseName.toLowerCase().endsWith(".brd"))
            		problemName = baseName.substring(0, baseName.length()-4);
            	else
            		problemName = baseName;
            } else {
            	Object name = mo.getProperty(MsgType.PROBLEM_NAME);
            	problemName = (name instanceof String ? name.toString() : "");
            }
            return null;
        }else if (messageType.equalsIgnoreCase("StartStateEnd")) {
            loadJessFiles(problemName, !Utils.isRuntime());  // sets wmeTypesFromFile, etc.  
            if (!wmeTypesFromFile) {
            	try{
            		Deftemplate dt;
            		if (rete.findDeftemplate("problem") == null){
            			String deftemplateStr = "(deftemplate problem (slot name) (multislot interface-elements) (multislot subgoals) (slot done) (slot description))";
            			rete.eval(deftemplateStr);
            			dt = rete.findDeftemplate("problem");				
            		}else{
            			if (trace.getDebugCode("mt")) trace.out("mt", "Deftemplate problem found.");
            		}
            	}catch (JessException je){
    				if (console != null && console.getTextOutput() != null)
    					console.getTextOutput().append(je.toString());
            		if(MTRete.breakOnExceptions){
            			MTRete.stopModelTracing = true;
            		}
            	}
            	catch(Exception ex){
            		ex.printStackTrace();
            	}
            }
            try {
                if (!bloadSuccessful) {
                    // define the global variables
                    this.rete.eval("(defglobal ?*sSelection* = "+MTRete.NOT_SPECIFIED+")");
                    this.rete.eval("(defglobal ?*sAction* = "+MTRete.NOT_SPECIFIED+")");
                    this.rete.eval("(defglobal ?*sInput* = "+MTRete.NOT_SPECIFIED+")");

                    
                    // define a deftemplate for storing the student input values.
//                  if(rete.findDeftemplate("studentValues") == null){
//                      String deftemplate = "(deftemplate studentValues (slot selection) (slot action) (slot input))";
//                      rete.eval(deftemplate);
//                  }
              }

              // loading the rules from the file "productionRules.pr"
//            if (!prRulesFromFile)
//            prRulesFromFile = pre.loadRuleSet();

            } catch (HeadlessException e1) {
                e1.printStackTrace();
            } catch (JessException je) {
                String errMsg = "Error defining selection-action-input globals";
				if (console != null && console.getTextOutput() != null)
					console.getTextOutput().append("\n"+errMsg+":\n"+je);
                if (trace.getDebugCode("mt"))
                	trace.out("mt", "ERROR CREATING S/A/I GLOBALS: "+je);
                Utils.showExceptionOccuredDialog(je, errMsg+":\n"+je.getDetail(), "Jess Warning");
            }

            if(!wmeInstancesFromFile){
				if (console != null && console.getTextOutput() != null)
					console.getTextOutput().append("\nWME file not found,"+
							" creating instances from interface definitions\n");
                if (trace.getDebugCode("mt")) trace.out("mt", "StartStateEND");
                try {
//                    loadSpecialFacts();
                    if (trace.getDebugCode("mt")) trace.out("mt", "loading interface facts upon StartStateEnd");
                    ValueVector interfaceInstancesValue =
                        loadInterfaceInstances();
                    loadInterfaceActions();
                    if(rete.findDeftemplate("problem") != null){
                        Fact problemFact = new Fact(rete.findDeftemplate("problem"));
                        problemFact.setSlotValue("name", new Value(problemName, RU.SYMBOL));
                        if (interfaceInstancesValue != null)
                            problemFact.setSlotValue("interface-elements",
                                    new Value(interfaceInstancesValue, RU.LIST));
//                        trace.out("wmefacts", "assertFact() called");
                        rete.assertFact(problemFact);

                        if (trace.getDebugCode("mt")) trace.out("mt", "StartStateEnd: new problem fact id: " +
                                new FactIDValue(problemFact));
                    }

                    if(rete.isUseBackwardChaining()){
                        // make the deftemplates backward chaining reactive
                        rete.eval("(do-backward-chaining" + MTRete.SAINAME + ")");
                        rete.eval("(do-backward-chaining" + MTRete.CORRECTSAINAME + ")");
                        rete.eval("(do-backward-chaining" + MTRete.BUGGYSAINAME + ")");
                    }	

                    rete.saveState(findCognitiveModelDirectory()+problemName+".bload");
                    if(wmeEditor != null)
						wmeEditor.getPanel().refresh();
                }catch(JessException je){
    				if (console != null && console.getTextOutput() != null)
    					console.getTextOutput().append("\n" + je.toString() + "\n");
                    if(MTRete.breakOnExceptions){
                        MTRete.stopModelTracing = true;
                    }
                } 
                catch(Exception ex){
    				if (console != null && console.getTextOutput() != null)
    					console.getTextOutput().append("\n" + ex.toString() + "\n");
                    ex.printStackTrace();
                }
            }			 

            // add the grouping information
            for(int i = 0; i < groupDescriptionMessages.size(); i++){
                MessageObject obj = (MessageObject)groupDescriptionMessages.get(i);
                assertGroupFact(obj);
            }

            // processStartState calls author's start state hook, if present
            try {
                if (startStateHooks != null&&!startStateHooks.isEmpty())
                    callStartStateHooks();
            } catch (JessException je) {
				if (console != null && console.getTextOutput() != null)
					console.getTextOutput().append("\n" + je.toString() + "\n");
                if(MTRete.breakOnExceptions){
                    MTRete.stopModelTracing = true;
                }
            } 
            setVariableTableInFunctions();

            if(wmeEditor != null)
				wmeEditor.getPanel().refresh();
            msgEvtSupport.fireMessageEvent(new MessageEvent(this, true,
                    "StartStateComplete",
                    null));
            clearRuleActivationTree();
            MTRete.Routers.dumpRouters("startStateEnd", rete);
            rete.showActivations("StartStateEnd");  // debug routine

       
            //may be performance inhibiting
            // rete.saveState(this.initialState);
            return null;
        }else if (messageType.equalsIgnoreCase("Go_To_WM_State")) {

            // set the global variables
            Vector<String> selectionList = (Vector<String>) mo.getProperty("SelectionList");
            Vector<String> inputList = (Vector<String>) mo.getProperty("InputList");
            Vector<String> actionList = (Vector<String>) mo.getProperty("ActionList");
            
           
            // Mon Oct 24 15:11:56 2005:: Noboru
            // CTAT_Controller has been added so that update model on faliure in
            // GoToWMMessgHandler will work for Sim. St. validation
            GoToWMMessgHandler handler = new GoToWMMessgHandler(mo, jmt, controller);

            // make a call to GO_TO_WM handler.
            // this call is blocking
            //String returnValue = handler.processMessage();	// blocking call until all the arcs have been model traced
            handler.processMessage();	// blocking call until all the arcs have been model traced
            
            
            return handler.getMessageObject();

        }else if (messageType.equalsIgnoreCase("LISPCheck")) {
            // if the previous arcs can be traced correctly then only check this arc
            // set the global variables
            selection = mo.getSelection();
            input = mo.getInput();
            action = mo.getAction();


            if (trace.getDebugCode("mt")) trace.out("mt", "selection: " + selection + ";");
            if (trace.getDebugCode("mt")) trace.out("mt", "action: " + action + ";");
            if (trace.getDebugCode("mt")) trace.out("mt", "input: " + input + ";");
    	
           
            if(selection.size() > 0 && action.size() > 0 && input.size() > 0){
                this.rete.setGlobalSAI((String)selection.get(0), (String)action.get(0), (String)input.get(0));
            }		

          
            
            if(Utils.isRuntime())
            	ProdSysCheckMessgHandler.executeSynchronously(mo, jmt, controller, wmeEditor);
            else                // run the model trace asynchronously, off the Event thread
            	prodSysCheckMessgHandlerQ.add(mo, jmt, controller, wmeEditor);
            
        } else if (messageType.equalsIgnoreCase("Send_ESEGraph")) {
            Vector eseGraph = (Vector)mo.getProperty("ESEGraph");
            Vector parentChildList,childrenInfoList, childInfo, edgeCheckResult, edgeList;
            //Enumeration enumParentChildList;
            Enumeration enumChildrenInfoList;

            Enumeration eseEnum = eseGraph.elements();

            edgeList = new Vector();

            Vector edgesAlreadyChecked = new Vector();
            Vector resultsOfEdgesChecked = new Vector();
            // save the initial state
            String fileName = (String)((Vector)eseGraph.get(0)).get(0) + ".state";
            rete.saveState(".",fileName);
            String childStateName;

            for (int i = 0; eseEnum.hasMoreElements(); ++i) {
                parentChildList = (Vector)eseEnum.nextElement();
                if (trace.getDebugCode("mtt")) trace.out("mtt", "eseGraph["+i+"] fileName "+fileName+
                        ", parentChildList.size "+parentChildList.size());

                if(parentChildList.size() > 1) {
                    String parentChildName = (String)parentChildList.elementAt(0);		
                    childrenInfoList = (Vector) parentChildList.elementAt(1);
                    enumChildrenInfoList = childrenInfoList.elements();

                    for (int j = 0; enumChildrenInfoList.hasMoreElements(); ++j) {	
                        childInfo = (Vector) enumChildrenInfoList.nextElement();
                        String name = (String)childInfo.elementAt(0);
                        Integer uID = (Integer)childInfo.elementAt(1);
                        String auIntent = (String) childInfo.elementAt(2);
                        selection = (Vector)childInfo.elementAt(3);
                        action = (Vector)childInfo.elementAt(4);
                        input = (Vector)childInfo.elementAt(5);

                        // call model-trace and find the rule sequence
                        // construct the checkAllStates result
                        // send it to student Inteface.

                        childStateName = name + ".state";

                        // load the WME's corresponding to this state
                        fileName = parentChildName + ".state";

                        if (trace.getDebugCode("mtt")) trace.out("mtt", "eseGraph["+i+","+j+"] parentChildName "+parentChildName+
                                ": "+name+", "+uID+", "+auIntent+", "+selection+", "+action+", "+input+
                                ", statePending(.,"+fileName+") "+rete.statePending(".", fileName));

                        if(rete.statePending(".", fileName)) {
                            rete.loadState(".",fileName);			    

                            enumerator = selection.elements();
                            while (enumerator.hasMoreElements()) {
                                Object selObject = enumerator.nextElement();
                                final StringBuffer sb1 = new StringBuffer((String) selObject);

                                // now sb1 contains the fact name 			
                                int selectionIndex = selection.indexOf(selObject); // the index of the selection element in the selection vector

                                // check if the action for this selection is update....		
                                Object actionObject = action.get(selectionIndex); // get the action for the selection element
                                final StringBuffer actionString = new StringBuffer((String)actionObject);
                                try {
                                    if (JessModelTracing.isSAIToBeModelTraced(sb1.toString(),
                                            actionString.toString())) {
                                        final String inputString = (String)input.get(selectionIndex);

                                        rete.setGlobalSAI(sb1.toString(), actionString.toString(),
                                        		inputString);

                                        checkResult = jmt.runModelTrace(false, false,
                                                sb1.toString(), actionString.toString(), inputString,
                                                null);

                                        // see if the author intent is same as the result of model tracing
                                        // if yes then save the state WM for this child node

                                        if ((checkResult.equalsIgnoreCase(EdgeData.SUCCESS)
                                                && auIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION))
                                                || (checkResult.equalsIgnoreCase(EdgeData.FIREABLE_BUG)
                                                        && auIntent.equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION))) {
                                            rete.saveState(".",childStateName);
                                        } 

                                        edgeCheckResult = new Vector();
                                        edgeCheckResult.addElement(uID);
                                        edgeCheckResult.addElement(checkResult);
                                        edgeList.addElement(edgeCheckResult);

                                        // Mon Oct 10 17:11:11 2005 :: Noboru
                                        // Keep a sequence of rules used to model trace the step
                                        mtRuleSeq = jmt.getRuleSeq();

                                    } // end if (((i = actionString.indexOf("update")) != -1) || ((i = actionString.indexOf("Update")) != -1))
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }   // end try 
                            } // end while (enumerator.hasMoreElements())
                        }else{
                            // the current state is unreachable from the start state hence result of 
                            // model trace should be no-model
                            checkResult = NOTAPPLICABLE;
                            edgeCheckResult = new Vector();
                            edgeCheckResult.addElement(uID);
                            edgeCheckResult.addElement(checkResult);
                            edgeList.addElement(edgeCheckResult);
                        } // end if(r.statePending(".", fileName)) 
                    } // end while (enumChildrenInfoList.hasMoreElements())
                } // end if(parentChildList.size() > 1)
            }// end while while (eseEnum.hasMoreElements())

            // constructing the result of checkAllStates for sending to execution space editor.
            MessageObject resultMessageObject = MessageObject.create(MsgType.CHECK_ALL_STATES_RESULT);
            resultMessageObject.setVerb("SetProperty");
            resultMessageObject.setProperty("EdgeList", edgeList, true);

            // end of constructing the result 
            msgEvtSupport.fireMessageEvent(
                    new MessageEvent(
                            this, 
                            true,
                            "CheckAllStatesResult",
                            resultMessageObject));

            return resultMessageObject;

        } else if (messageType.equalsIgnoreCase("InterfaceAction")) {
            if (trace.getDebugCode("mt")) trace.out("mt", "InterfaceAction");
            
            selection = mo.getSelection();
            input = mo.getInput();
            action = mo.getAction();
            InterfaceAction ia = new InterfaceAction(selection,
                    action, input);
            interfaceActionsList.add(ia);
            return null;

        }else if (messageType.equalsIgnoreCase("SendRuleProductionSet")){

//          pre.show();
            return null;
        }
        else if (messageType.equalsIgnoreCase("SendSelectedElements")){

            trace.err("***ERROR*** MT.handleCommMessage() received msg type SendSelectedElements");
            return null;

        }else if(messageType.equalsIgnoreCase("GroupDescriptionStart")){

            groupDescriptionMessages.clear();

        }else if(messageType.equalsIgnoreCase("GroupDescriptionEnd")){

        }else if(messageType.equalsIgnoreCase("GroupDescription")){

            groupDescriptionMessages.add(mo);

        }else if(messageType.equalsIgnoreCase("AttributeDescription")){

            groupDescriptionMessages.add(mo);

        }else if(messageType.equalsIgnoreCase("RestorJessInitialWMState")){
        	clearRete();
            if(wmeEditor != null)
				wmeEditor.getPanel().refresh();
//        	System.gc();           
        }else if(messageType.equalsIgnoreCase("RestorInitialWMState")){
        	// need to be mrege with  RestorJessInitialWMState later   
        }
        else{
            trace.err("MT.handleCommMessage(): Unknown msg type: " +
                    messageType);
        }
        return null;   
    }

    /**
     * Update the {@link VariableTable} references in each {@link AlgExprFunctions} function
     * in {@link #algExprFns}.
     */
    private void setVariableTableInFunctions() {
		CTAT_Controller ctlr = getController();
		ProblemModel pm = (ctlr == null ? null : ctlr.getProblemModel());
		VariableTable vt = (pm == null ? null : pm.getVariableTable());
		if(trace.getDebugCode("functions"))
			trace.out("functions", "MT.setVariableTableInFunctions()"+
					" # algExprFns "+(algExprFns == null ? null : algExprFns.size())+
					", ctlr "+ctlr+", pm "+pm+", vt "+vt+" size() "+vt.size());
        if(algExprFns == null)
        	return;
		algExprFns.setVariableTable(vt);
	}

	/**
     * Clear the Conflict Tree.
     */
    private void clearRuleActivationTree() {
    	if (jmt == null)
    		return;
    	RuleActivationTree tree = jmt.getRuleActivationTree();
    	if (tree != null) {
    		tree.clearTree(null);
    	}
	}

	/**
     * Add a @link ModelTracingUserfunction to be called on each element of the start state
     * if a function with the same underlying class already exists, do nothing
     * The function will be added at the end of the calling queue
     * @param function 
     * @return a boolean indicating whether the function was actually added to the list
     */
     boolean addStartStateHookCall(ModelTracingUserfunction function)
    {
        if(startStateHooks==null)
            startStateHooks=new Vector();
        else
        {
            if(MTRete.findUserfuction(function,startStateHooks)!=-1)
                return false;
        }

        startStateHooks.add(function);
        return true;
    }
    
    /**
     * Remove a function with the same underlying class from the queue of functions
     * to be called on the start state
     * @param functio
     * @return a boolean indicating whether it was removed or not
     */
    boolean removeStartStateHook(ModelTracingUserfunction function)
    {
        int index=MTRete.findUserfuction(function,startStateHooks);
        if(index!=-1)
        {
            startStateHooks.remove(index);
            return true;
        }
        return false;
    }
    /**
	 * extracts SAI tuples from the start state and calls startstateFunction on each of them
	 * @param startStateFunction 
	 * @throws JessException
	 */
	private void callStartStateHooks() throws JessException {
		if (interfaceActionsList == null||startStateHooks==null)
			return;
        Iterator funcIter=startStateHooks.iterator();
        while(funcIter.hasNext())
        {
		Iterator<InterfaceAction> interfaceIter=interfaceActionsList.iterator();
        ModelTracingUserfunction curFunc=(ModelTracingUserfunction)funcIter.next();
        
    		while(interfaceIter.hasNext())
    		{
    			InterfaceAction ia=(InterfaceAction)interfaceIter.next();
    			List selectionList=ia.selectionList;
    			List actionList=ia.actionList;
    			List inputList=ia.inputList;
    			for(int tuplenum=0; tuplenum<selectionList.size(); tuplenum++)
    			{
    				//process all the start state SAIs and send them to startStateFunction
    				String s=(String)selectionList.get(tuplenum);
    				try {
    					String a=(String)actionList.get(tuplenum);
    					String i=(String)inputList.get(tuplenum);
    					ValueVector args=curFunc.getArguments(s,a,i,getRete());
    					curFunc.javaCall(args,getRete().getGlobalContext());
    				} catch (ArrayIndexOutOfBoundsException oobe) {
    					String errMsg = "Error calling startStateFunction "+curFunc.getName()+
    							" at selection index "+tuplenum+", selection "+s;
    					if (console != null && console.getTextOutput() != null)
    						console.getTextOutput().append("\n"+errMsg+":\n"+oobe);
    					if (trace.getDebugCode("mt")) trace.out("mt", errMsg+": "+oobe);
    					break;
    				}
    			}
    		}
        }
	}

	/**
	 * Create one of the special tutor facts whose deftemplate is derived from
	 * the base deftemplate {@link MTRete.SAINAME}.
	 * @param templateName
	 * @param multislotName
	 * @param factsList list to which to append this fact
	 * @return fact created
	 * @throws JessException
	 */
/*	private Fact createSpecialFactDerived(String templateName, String multislotName, List factsList) throws JessException {
		Deftemplate dt;
		if ((dt = rete.findDeftemplate(templateName)) == null){
			// extend the special-tutor-fact to create special-tutor-fact-correct wme type
			Deftemplate parentTemplate = rete.findDeftemplate(MTRete.SAINAME);
			if(parentTemplate != null){
				dt = new Deftemplate(templateName,"", parentTemplate,this.rete);
				dt.addMultiSlot(multislotName, Funcall.NILLIST);
				dt = rete.addDeftemplate(dt);
			}else{
				System.err.println("Deftemplate " + MTRete.SAINAME + " not found.");
			}
		}else{
			 if (trace.getDebugCode("mt")) trace.out("mt", "Deftemplate "+templateName+" found.");
		}
		Fact saiFact = new Fact(rete.findDeftemplate(templateName));
		if(saiFact != null){
			saiFact.setSlotValue("selection", new Value(MTRete.NOT_SPECIFIED, RU.SYMBOL));
			saiFact.setSlotValue("action", new Value(MTRete.NOT_SPECIFIED, RU.SYMBOL));
			saiFact.setSlotValue("input", new Value(MTRete.NOT_SPECIFIED, RU.SYMBOL));
			factsList.add(saiFact);
		}
		return saiFact;
	}*/

	/**
	 * Create one of the special tutor facts.
	 * @param templateName name of the deftemplate
	 * @param deftemplateStr deftemplate definition
	 * @param traceStr label for debugging
	 * @param factsList list to which to append this fact
	 * @return fact created
	 * @throws JessException
	 */
/*	private Fact createSpecialFact(String templateName, String deftemplateStr, String traceStr, List factsList)
			throws JessException {
		Deftemplate dt;
		Fact saiFact;
		if((dt = rete.findDeftemplate(templateName)) == null){
			rete.eval(deftemplateStr);
		}else{
			if (trace.getDebugCode("mt")) trace.out("mt", traceStr);
		}

		saiFact = new Fact(rete.findDeftemplate(templateName));
		if(saiFact != null){
			saiFact.setSlotValue("selection", new Value(MTRete.NOT_SPECIFIED, RU.SYMBOL));
			saiFact.setSlotValue("action", new Value(MTRete.NOT_SPECIFIED, RU.SYMBOL));
			saiFact.setSlotValue("input", new Value(MTRete.NOT_SPECIFIED, RU.SYMBOL));
			factsList.add(saiFact);
		}
		return saiFact;
	}*/

	/**
	 * Assert a fact 
	 * @param slotValues
	 * @param mo
	 */
	private void assertGroupFact(MessageObject mo) {
		Deftemplate groupTemplate = null;	// current Group deftemplate
		Fact groupFact = null;	// current group fact
		int attributeCounter = 0;
		ArrayList<Value> slotValues = new ArrayList<Value>();		
		String messgType = mo.getMessageType();
		if(messgType.equals("GroupDescription")){

			slotValues.clear();

			String groupName = (String) mo.getProperty("Name");
			String type = (String) mo.getProperty("Type");
			Vector attributes = (Vector) mo.getProperty("Attributes");
			attributeCounter = attributes.size();

			try {
				groupTemplate = new Deftemplate(type, "A group of interface elements", rete);
				groupTemplate.addSlot("name", Funcall.NIL, "SYMBOL");
			} catch (JessException e2) {
				e2.printStackTrace();
			}
		}

		if(messgType.equals("AttributeDescription")){
			String groupName = (String) mo.getProperty("GroupName");
			String attributeName = (String) mo.getProperty("AttributeName");
			String cardinality = (String) mo.getProperty("Cardinality");
			String type = (String) mo.getProperty("Type");
			Vector values = (Vector) mo.getProperty("Values");
			Vector valueTypes = (Vector) mo.getProperty("ValueTypes");

			if(attributeCounter > 0){
				attributeCounter--;
			}
			// if the cardinality of the attribute is single then it is 
			// a single slot other wise it is a multislot
			if(cardinality.equalsIgnoreCase("single")){
				try {
					if(groupTemplate != null){
						groupTemplate.addSlot(attributeName, Funcall.NIL, "SYMBOL");

						//										// add the value for the attribute in the slotvalues list
						if(values.size() > 0){
							// get the fact-id for the fact from the working memory
							String commWidgetName = (String) values.get(0);
							//											Value val = new FactIDValue(getFactForCommName(commWidgetName));
							//											slotValues.add(val);
							slotValues.add(new Value(commWidgetName, RU.SYMBOL));
						}
					}
				} catch (JessException e2) {
					e2.printStackTrace();
				}
			}else if(cardinality.equalsIgnoreCase("multiple")){
				try {
					if(groupTemplate != null){
						groupTemplate.addMultiSlot(attributeName, Funcall.NILLIST);

						//										// add the value for the attribute in the instance
						ValueVector vv = new ValueVector();
						for(int z = 0; z < values.size(); z++){
							//											String commWidgetName = (String) values.get(z);
							//											Value val = new FactIDValue(getFactForCommName(commWidgetName));
							//											vv.add(val);
							vv.add(new Value((String) values.get(z), RU.SYMBOL));
						}
						slotValues.add(new Value(vv, RU.LIST));
					}
				} catch (JessException e2) {
					e2.printStackTrace();
				}
			}else{
				if (trace.getDebugCode("mt")) trace.out("mt", "Invlid cardinality: " + cardinality);
			}
			// add the deftemplate and the fact for the group to jess if all the
			// attributes have been added
			if(attributeCounter == 0){
				try {
					// add the deftemplate to Jess
					Deftemplate dt;
					if((dt = rete.findDeftemplate(groupTemplate.getBaseName())) == null){
						rete.addDeftemplate(groupTemplate);
					}
					try {
						groupFact = new Fact(groupTemplate);
						groupFact.setSlotValue("name", new Value(groupName, RU.SYMBOL));
						for(int k = 0; k < slotValues.size(); k++){
							groupFact.setSlotValue(groupTemplate.getSlotName(k+1), (Value)slotValues.get(k));
						}
						//                                                        trace.out("wmefacts", "assertFact() called");
						rete.assertFact(groupFact);
					} catch (JessException e3) {
						e3.printStackTrace();
					}
				} catch (JessException e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	/**
	 * Given an SAI value, execute a (modify) to set the named interface
	 * fact(s) to the given input value(s).  Walks the given vectors and
	 * performs a (modify) for all entries whose action is "update...".
     * @param  selection selection vector from InterfaceAction message
     * @param  action action vector from InterfaceAction message
     * @param  input input vector from InterfaceAction message
     * @return last fact modified
     */
    private Fact modifyFromSAI(List selection, List action, List input) 
    		throws JessException {
    //	trace.out("miss","*******************");
    //	Thread.dumpStack();
    //	trace.out("miss","*******************");
    	Fact lastFact = null;         			
        Iterator iterator;
        iterator = selection.iterator();
        for (int selectionIndex = 0; iterator.hasNext(); ++selectionIndex) {
        	Object selObject = iterator.next();
        	String selectionString = selObject.toString();

        	// now sb1 contains the fact name        	
        	if(action.size() <= selectionIndex || input.size() <= selectionIndex)
        		break;

        	// check if the action for this selection is update....		
        	Object actionObject = action.get(selectionIndex); // get the action for the selection element
        	String actionString = (actionObject == null ? "" : actionObject.toString().toLowerCase());
        	if (actionString.indexOf("update") < 0)
        		continue;
        	
        	Object inputObject = input.get(selectionIndex);
        	String inputString = (inputObject == null ? null : inputObject.toString());
        	Value[] inpV = { null };
        	int type = edu.cmu.pact.jess.Utils.getJessType(inputString, inpV);
        	
        	//if (chunksExist)
        	//update any wme-chunks that might be defined    
        	try{
        		this.rete.updateChunkValues(selectionString, inputString);
        	}
        	catch (Exception ex){
        		
        	}
        	
        	Fact fact = (Fact) interfaceFactsMap.get(selectionString);
        	if (fact == null)
        		fact = getFactByName(rete, selectionString);
        	if (fact != null) {
        		Deftemplate deft = fact.getDeftemplate();
        		if(deft.getSlotIndex("value") < 0) {
            		if (trace.getDebugCode("mt")) trace.out("mt", "modifyFromSAI("+selectionString+
            				"): no value slot in deftemplate "+deft.getBaseName());
        			continue;
        		}
        		if (trace.getDebugCode("mt")) trace.out("mt", "modifyFromSAI("+selectionString+","+
        				inpV[0].stringValue(null)+","+RU.getTypeName(type)+")");
        		lastFact = rete.modify(fact, "value", inpV[0]);
        	}else{
				if (console != null && console.getTextOutput() != null)
					console.getTextOutput().append("\nInterface fact not found: "+selectionString);
        	}
        }
        return lastFact;
    }

	



   
   public String removeModuleName(String rule, String moduleName){
		StringBuffer sb = new StringBuffer(rule);
		int start;
		while((start = sb.indexOf(moduleName)) != -1){
			sb.replace(start, start + moduleName.length(), "");
		}
		return sb.toString();		
   }

	/**
	 * Method getElement.
	 * @param list
	 * @return String
	 */
	public String getElement(Vector list){
		Vector selection = (Vector)list.get(0);
		
		return (String)selection.get(selection.size() - 1);
	}


   /**
    * This method is used to set the value of a property in the property values vector
    * 
    * @param obj    The value of the parameter to be set
    * @param propertyNames
    *               The vector of property names
    * @param propertyValues
    *               The vector of property values
    * @param propertyName
    *               The name of the property to be set
    */
   public void setValue(Vector propertyNames, Vector propertyValues, String propertyName, Object obj){
       int pos = fieldPosition (propertyNames, propertyName);
       if (pos != -1)
		   propertyValues.set(pos,obj);
       else{
	//	   trace.out("Property: " + propertyName + " does not exist in the property list.");
		   propertyValues.add(obj);
		   propertyNames.add(propertyName);
       }
   }

   /**
       Extract the desired value from propertyValues and return it

       @param propertyNames Property name vector from Comm message
       @param propertyValues Property value vector from Comm message
       @param propertyName The property name of the value being sought

       @returns The property value requested, or null if not found
    */
   public static Object getValue (Vector propertyNames, Vector propertyValues, String propertyName) {
       int pos = fieldPosition (propertyNames, propertyName);
       if (pos != -1)
		   return  propertyValues.elementAt (pos);
       else
			return null;
   }

   /**
       Extracts a field position from a comm message vector
    */
   public static int fieldPosition(Vector from, String fieldName){
       int toret = -1;
       int s = from.size();
       for(int i=0; i<s; i++) {
	   if(((String)from.elementAt(i)).equalsIgnoreCase(fieldName))
	       return i;
       }
       return toret;
   }

   /**
    * This function returns a Fact given a fact name 
    * 
    * @param r      The MTRete engine that contains the fact
    * @param name   The fact name to find
    * 
    * @return The Fact object
    */
   public Fact getFactByName(MTRete r,String name){
       Iterator it;
       Fact fact = null;
       it = r.listFacts();
       try {
		   while (it.hasNext()) {
		       fact = (Fact)it.next();
		       if (fact.getDeftemplate().getSlotIndex("name") != -1) {
	    
				   Value v = fact.getSlotValue("name");
				   
				   if (v.stringValue(null).trim().equalsIgnoreCase(name)) {
				       return fact;
				   }
		       }
		       fact = null;
		   }
			
       }catch(JessException je){
			if (console != null && console.getTextOutput() != null)
				console.getTextOutput().append(je.toString());
		if(MTRete.breakOnExceptions){
			MTRete.stopModelTracing = true;
		}

//			ta.setVisible(true);
       }
       catch (Exception ex){
		   ex.printStackTrace();
       }
       finally{
       		it = null;   
       }
       return fact;
   }
   /**
    * this method id used to get the fact from the rete engine from the deftemplate name
    * 
    * @param r      the rete engine
    * @param name   the name of the deftemplate
    * 
    * @return the Fact corresponding to the fact name
    */
   public Fact getFactByTemplateName(MTRete r,String name){
       Iterator it = null;
       Fact fact = null;
       it = r.listFacts();
       try {
		   while (it.hasNext()) {
		       fact = (Fact)it.next();
		       
		       if (fact.getDeftemplate().getBaseName().equals(name)) {
				   break;
		       }
		   fact = null;
		   }
	   }catch (Exception ex){
	   		ex.printStackTrace();
       }
       return fact;
   }
	
	/**
	 * @param rule
	 * @param string
	 * @return result of {@link Rete#eval(java.lang.String)}
	 */
	public boolean assertRule(String rule, String ruleName) {

		try {
			Value v = rete.eval(rule);
			// pre.appendText(rule);
			// pre.saveFile();
			return Funcall.TRUE.equals(v); // return pre.evaluate();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	

	private MTRete lastRete = null;

    /**
     * a list of @link ModelTracingUserFunction which is called in queue order on all elements 
     * of the start state when the state is created
     */
    private Vector startStateHooks;
	
    public void setExternalRete(MTRete external){
    	this.externallyDefinedRete = external;

		clearRete();  // call early this to set rete for logging

		// rete = new MTRete(controller);
		// rete.setMT(this);
		if(VersionInformation.isRunningSimSt() && !Utils.isRuntime())
			jmt = new SimStJessModelTracing(rete, controller);
		else
			jmt = new JessModelTracing(rete, controller);
		rete.setJmt(jmt);
		jmt.setRete(rete);

		if(!Utils.isRuntime()) {
			wmeEditor = new WMEEditor(rete, this, null, true, false);
			wmeEditor.setProblemName(controller.getLogger().getStudentName());
			addMessageEventListener(wmeEditor);
			
			console = new JessConsole("Jess Console", rete, true, controller);
		}		
    }
    
	/**
	 * Load the deftemplates and facts files for a given test.  If useBinary
	 * is set, tries to Rete.bload() that file first.  Sets wmeTypesFromFile,
	 * wmeInstancesFromFile, rulesFromFile, bloadSuccessful. 
	 *
	 * @param  basename base part of filename for files to load; e.g.,
	 *             use "AdditionChaining" to load AdditionChaining.clp,
	 *             AdditionChaining.wme, AdditionChaining.clp
	 * @param  useBinary true means try to load entire state from binary file
	 * @return array showing which files were read
	 */
	private void loadJessFiles(String problemName, boolean useBinary) {
		
		bloadSuccessful = false;
		wmeTypesFromFile = false;
		prRulesFromFile = false;
		wmeInstancesFromFile = false;
		
		String[] filenames = { problemName + ".bload",             // cf use of
							   WMEEditor.wmeTypeFileName,          // indices in
							   WMEEditor.rulesFileName, // results[]
							   problemName + ".wme"
						};             // below
		
		boolean[] results = null;

		String cmDir=findCognitiveModelDirectory();  // returns directory name with trailing "/"
		for (int i = 0; i < filenames.length; ++i)
		{
			
			if (cmDir!=null)
				filenames[i] = cmDir + filenames[i];   // assume dir already has trailing "/" separator
			else filenames[i]=null;
					
		}
		
		
		if (!useBinary)
			filenames[0] = null;

                // trace.out("miss", "@@@@@ MT.getRete().getGlobalContext() = " + getRete().getGlobalContext());
                
		try {
                    // Oct 10, 2007 Noboru
                    // This is necessary to avoid an occasional error when loading a deftemplate (*.clp) file
                    // For some unknown reason (possibly a delay in multithread processes), sometimes
                    // loadJessFiles failed to load a defteplate file, especially when loadJessFiles is called
                    // over and over again in a bach mode. 
		    rete.clear();

		  // trace.err("Here we go : Filenames-0:"+filenames[0]+",1:"+filenames[1]+",2:" +
		   // 		filenames[2]+",3:"+filenames[3]);
		    
		    updateWMEEditorCognitiveModelFolder(filenames[1]);
		    
		  	
		    
			results = rete.loadJessFiles(filenames[0], filenames[1],
					filenames[2], filenames[3], interfaceTemplatesList);
			
			
			
		
			
			
			
		} catch (Exception e) {
			trace.err("Error "+e+" trying to load Jess files");
			return;     // results[] null after exception: leave booleans false
		}

		bloadSuccessful = results[0];
		wmeTypesFromFile = (results[1] || results[0]);  // cf filenames[] above
		prRulesFromFile = (results[2] || results[0]);
		wmeInstancesFromFile = (results[3] || results[0]);
		
		//for author action logging:
		if (!Utils.isRuntime() && lastRete != null)
		{
			LogReteChanges lrc = new LogReteChanges(getRete().getEventLogger(), 
					lastRete, rete);
			lrc.logReteChanges();
		}
		lastRete = rete;
	}	

	/**
	 * Update the Cognitive Model Folder label in the {@link WMEEditor} with the directory
	 * of the production rules file just loaded. 
	 * @param fn productionRules filename, with path
	 */
	private void updateWMEEditorCognitiveModelFolder(String fn) {
		if(Utils.isRuntime())
			return;
	    if (fn!=null) 
	    {
	    	if (trace.getDebugCode("eep")) trace.out("eep","updateWMEEditorCognitiveModelFolder fn in:"+fn);

	    	//if the filename ends with '/'+somefilename then remove '/'+somefilename
	    	String dir = fn;
	    	if (dir.contains(File.separator))
	   			dir = dir.substring(0,dir.lastIndexOf(File.separator));
	    	else if (dir.contains("/"))
	   			dir = dir.substring(0,dir.lastIndexOf("/"));

	    	// if there are still multiple path elements, get the last one
	    	if (dir.contains(File.separator))
	   			fn = dir.substring(dir.lastIndexOf(File.separator)+1,dir.length());
	    	else if (dir.contains("/"))
	   			fn = dir.substring(dir.lastIndexOf("/")+1,dir.length());
	    	else
	    		fn = dir;

    		String path;
	   		if (dir.length() < 1) {
	    		//this shouldn't happen...
	    		trace.err("findCognitiveModelDirectory returned empty path in loadJessFiles");
	    		path = fn;
	    	} else {
	    		try {
	    			File f = new File(dir);
	    			path = f.getAbsolutePath();
	    		} catch(Exception e) {
	    			path = dir;  // handles case where dir is a URL
	    		}
	    		if (trace.getDebugCode("eep")) 
	    			trace.out("eep","updateWMEEditorCognitiveModelFolder out: "+fn+", "+path);
                if(wmeEditor != null)
                	wmeEditor.getPanel().updateCognitiveModelFolder(fn, path);
	    	}
	    } else {
	    	if (trace.getDebugCode("eep")) trace.out("eep","updateWMEEditorCognitiveModelFolder fn is null");
            if(wmeEditor != null)
            	wmeEditor.getPanel().updateCognitiveModelFolder("Files Not Found","Jess mode files (pr,clp,wme) could not be found");
	    }
		// TODO Auto-generated method stub
		
	}

	/**
	 * Test successive paths for a directory that could have the Jess files for this problem.
	 * Returns the first of these that's found and readable:<ol>
	 *   <li>the sibling {@value CTAT_Controller#COGNITIVE_MODEL} subdirectory if the .brd is in
	 *   a {@value CTAT_Controller#FINAL_BRDS} or {@value CTAT_Controller#PROBLEMS} subdirectory;</li>
	 *   <li>the {@value CTAT_Controller#COGNITIVE_MODEL} or <tt>JessFiles/</tt> subdirectory
	 *       of the .brd's directory;</li>
	 *   <li>the preference {@value CTAT_Controller#PROBLEM_DIRECTORY}, if defined, trying first its
	 *       {@value CTAT_Controller#COGNITIVE_MODEL} or JessFiles/ subdirectory;</li>
	 *   <li>the .brd's directory itself;</li>
	 *   <li>the preference {@value BR_Controller#PROJECTS_DIR}, trying first its
	 *       {@value CTAT_Controller#COGNITIVE_MODEL} or JessFiles/ subdirectory;</li>
	 *   <li>the current directory, trying first its {@value CTAT_Controller#COGNITIVE_MODEL} or
	 *       JessFiles/ subdirectory.</li>
  	 * </ol>
	 * @return first directory that's readable, with trailing "/" if didn't have one already
	 */
	String findCognitiveModelDirectory() {
		String result = null;
		File f = null;
		int trial = 1;
		String dirname = getProblemSetCognitiveModelDirectory();
		if (Utils.isDirectoryReadable(dirname, this))
			result = dirname;

		if (result==null) {
			dirname = getProblemDirectory(CTAT_Controller.COGNITIVE_MODEL);
			if (Utils.isDirectoryReadable(dirname, this))
				result = dirname;
			trial++;
		}
		if (result==null) {
			dirname = getProblemDirectory("JessFiles/");
			if (Utils.isDirectoryReadable(dirname, this))
				result = dirname;
			trial++;
		}
		if (result == null) {
			dirname = getCognitiveModelDirectory(CTAT_Controller.COGNITIVE_MODEL);
			if (Utils.isDirectoryReadable(dirname, this))
				result = dirname;
			trial++;
		}
		if (result == null) {
			dirname = getCognitiveModelDirectory("JessFiles");
			if (Utils.isDirectoryReadable(dirname, this))
				result = dirname;
			trial++;
		}
		if (result == null) {
			dirname = getCognitiveModelDirectory(null);
			if (Utils.isDirectoryReadable(dirname, this))
				result = dirname;
			trial++;
		}
		if (result==null) {
			dirname = getProblemDirectory("");  // .BRD file directory 
			if (Utils.isDirectoryReadable(dirname, this))
				result = dirname;
			trial++;
		}
		if (result==null) {
			dirname = getPreferenceDirectory(BR_Controller.PROJECTS_DIR, null, CTAT_Controller.COGNITIVE_MODEL);
			if (Utils.isDirectoryReadable(dirname, this))
				result = dirname;
			trial++;
		}
		if (result==null) {
			dirname = getPreferenceDirectory(BR_Controller.PROJECTS_DIR, null, "JessFiles/");
			if (Utils.isDirectoryReadable(dirname, this))
				result = dirname;
			trial++;
		}
		if (result==null) {
			dirname = getPreferenceDirectory(BR_Controller.PROJECTS_DIR, null, null);
			if (Utils.isDirectoryReadable(dirname, this))
				result = dirname;
			trial++;
		}
		if (result==null) {
			dirname="./"+CTAT_Controller.COGNITIVE_MODEL+"/";
			if (Utils.isDirectoryReadable(dirname, this))
				result = dirname;
			trial++;
		}
		if (result==null) {
			dirname="./JessFiles/";
			if (Utils.isDirectoryReadable(dirname, this))
				result = dirname;
			trial++;
		}
		if (result==null) {
			result="./";  // current directory assumed to be readable
			trial++;
		}

		result = Utils.appendSlash(result);
		if (trace.getDebugCode("mt"))
			trace.out("mt","findCognitiveModelDirectory() after "+trial+" trials returning: "+result);
		return result;
	}

	/**
	 * Return the directory part of {@link CTAT_Controller#getProblemFullName()}. If there
	 * are no directory names in the path, substitutes ".", for the current directory.
	 * @param subdir if not null, append this subdirectory path
	 * @return modified path, with trailing "/"
	 */
	private String getProblemDirectory(String subdir) {
		String problemFullName = (controller == null ? null : controller.getProblemFullName());
		if (problemFullName == null || problemFullName.trim().length() < 1)
			return null;
		StringBuilder sb = new StringBuilder(problemFullName.replaceAll("\\\\", "/"));
		int offset = sb.lastIndexOf("/");
		if (offset < 0)
			sb.replace(offset = 0, sb.length(), "./");   // current directory if path empty
		if(subdir == null)
			subdir = "";
		sb.replace(offset+1, sb.length(), subdir);
		if(sb.charAt(sb.length()-1) != '/')
			sb.append('/');
		return sb.toString();
	}

	/**
	 * If the problem file is in a {@value CTAT_Controller#FINAL_BRDS} subdirectory
	 * or in a {@value CTAT_Controller#PROBLEMS} subdirectory,
	 * return the sibling {@value CTAT_Controller#COGNITIVE_MODEL} subdirectory.
	 * @return modified path, with trailing "/"; null if {@link CTAT_Controller#getProblemFullName()}
	 *         doesn't contain {@value CTAT_Controller#FINAL_BRDS}
	 */
	private String getProblemSetCognitiveModelDirectory() {
		String problemFullName = (controller == null ? null : controller.getProblemFullName());
		if(trace.getDebugCode("mt"))
			trace.outNT("mt", "MT.getProblemSetCognitiveModelDirectory() problemFullName "+problemFullName);
		if (problemFullName == null || problemFullName.trim().length() < 1)
			return null;
		StringBuilder sb = new StringBuilder(problemFullName.replaceAll("\\\\", "/"));
		int offset = sb.lastIndexOf("/"+CTAT_Controller.FINAL_BRDS+"/");
		if (offset >= 0) {
			sb.replace(offset+1, sb.length(), CTAT_Controller.COGNITIVE_MODEL);
			sb.append('/');
			return sb.toString();
		}
		offset = sb.lastIndexOf("/"+CTAT_Controller.PROBLEMS+"/");
		if (offset >= 0) {
			sb.replace(offset+1, sb.length(), CTAT_Controller.COGNITIVE_MODEL);
			sb.append('/');
			return sb.toString();
		}
		return null;
	}
	
	/**
	 * Read just the production rules file. Sets {@link #prRulesFromFile}
	 * if successful. Deletes .bload file if successful. Logs changes to Rete.
	 * @return true if file loaded successfully
	 */
	public boolean reloadProductionRulesFile() {

		boolean result = false;
		// assume dir already has trailing "/" separator
		String filename = null;

		try {
			filename = findCognitiveModelDirectory() + WMEEditor.rulesFileName;
			if(getController() != null && getController().getMissController() != null && 
	        		getController().getMissController().isPLEon())
				result = rete.reloadProductionRulesFile(filename, false);
			else
				result = rete.reloadProductionRulesFile(filename, true);
		} catch (Exception e) {
			trace.err("Error "+e+" trying to load Jess files");
			return result;     // results[] null after exception: leave booleans false
		}
		clearRuleActivationTree();
		prRulesFromFile = true;
		rete.deleteBload();

		//		for author action logging:
		if (lastRete != null)
		{
			LogReteChanges lrc = new LogReteChanges(getRete().getEventLogger(), 
					lastRete, rete);
			lrc.logReteChanges();
		}
		lastRete = rete;
		
		return result;
	}
	
	/**
	 * Process the {@link #interfaceInstanceLists}. This will assert the facts
	 * that were sent in InterfaceDescription messages: these facts
	 * instantiate the user interface elements (widgets) in the rule engine.
	 * Each entry is a List of commands. The last fact asserted
	 * by each is saved to the ValueVector result for entry into the
	 * interface-elements slot of the problem fact.
	 *
	 * @return ValueVector with list of facts asserted
	 */
	private ValueVector loadInterfaceInstances() {
		ValueVector resultVV = new ValueVector();
		List lastFacts = new ArrayList();

		if (interfaceInstanceLists == null) {
			System.err.println("NULL value in MT.loadInterfaceInstances():" +
							   " interfaceInstanceLists is null");
			return resultVV;
		}

		try {
			if (trace.getDebugCode("mt")) trace.out("mt", "rete "+rete.hashCode()+" (rules) (agenda)");
			if (trace.getDebugCode("mt")) {
				rete.eval("(rules)");
				rete.eval("(agenda)");
			}
		} catch (JessException je) {
			je.printStackTrace();
		}
		boolean firstTime = true;
		for (Iterator it = interfaceInstanceLists.iterator(); it.hasNext(); ) {
			List assertCmds = (List) it.next();
			Value lastFactId = null;
			for (Iterator it2 = assertCmds.iterator(); it2.hasNext(); firstTime = false) {
				String cmd = (String) it2.next();
				
				/*tmandel - check if a WME with same name is already defined.
				 * If so, do not initialize this WME */
				if (trace.getDebugCode("mt")) trace.out("mt", "cmd "+cmd);
				String[] r = cmd.split("\\(name ");
				r = (r[1]).split("\\)");
				String wmeName = r[0];
				if(rete.getFactByName(wmeName)!=null)
				{
					if (trace.getDebugCode("mt")) trace.out("mt", "Definition for "+ wmeName +" already exists!");
					continue;
				}
				
				
				if (firstTime && rete.getTextOutput() != null)
		    		rete.getTextOutput().append("\nLoading instances from interface definitions.");
				try {
					Value val = rete.eval(cmd);
					if (trace.getDebugCode("mtt")) trace.out("mtt", "rete "+rete.hashCode()+
							" asserted interface fact, result "+val+", type "+
							RU.getTypeName(val.type())+":\n"+cmd);					
					if (val.type() == RU.FACT) {
						Fact fact = val.factValue(rete.getGlobalContext());
						lastFactId = new FactIDValue(fact);
						Value nameVal = fact.getSlotValue("name");
						if (nameVal != null) {
							String name =
								nameVal.stringValue(rete.getGlobalContext());
							//System.out.println("**** " + name + " fact = " + fact);
							interfaceFactsMap.put(name, fact);
						}
					}
				} catch (JessException e1) {
					String errMsg = "Error executing command \""+cmd+"\":\n  "+e1+
							(e1.getCause() == null ? "" : ";\n  "+e1.getCause().toString());
					trace.err(errMsg);
					e1.printStackTrace();
					if (rete.getTextOutput() != null)
						rete.getTextOutput().append("\n"+errMsg+"\n");
				}
			}

			// Append the last interface element fact to the lastFacts list
			// for return.
			try{
				if (lastFactId != null && lastFactId != Funcall.FALSE) {
					lastFacts.add(lastFactId);
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		/*
		 * Sort the last interface element facts from the InterfaceDescription
		 * msgs to define the order of occurrence in the output ValueVector,
		 * which will be the value of the problem fact's interface-elements slot. 
		 */
		sortByName(lastFacts, resultVV);
		return resultVV;
	}
	
	/**
	 * Sort interface element Facts according the values of their name slots.
	 * Any facts with no name slot or a nil value in the name slot will be
	 * sorted to the tail of the list.
	 * @param facts List of {@link Fact} instances to sort
	 * @param vv if not null, write the sorted list to this ValueVector
	 */
	private void sortByName(List facts, ValueVector vv) {
		Comparator byNameSorter = new Comparator() {
			/**
			 * Compare 2 {@link FactIDValue} instances according to the
			 * value of their name slots. A Fact with no name slot will
			 * be last in order. A Fact with a nil name value will be
			 * next-to-last.
			 * @param obj0 left-hand operand in comparison
			 * @param obj1 right-hand operand
			 * @return -1 negative int if obj0 < obj1, 0 if obj0==obj1,
			 *            positive int if obj0 > obj1
			 * @see Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			public int compare(Object obj0, Object obj1) {
				if (!(obj0 instanceof FactIDValue))
					throw new ClassCastException("left operand "+obj0+
							" is not a Fact");
				if (!(obj1 instanceof FactIDValue))
					throw new ClassCastException("right operand "+obj1+
							" is not a Fact");
				
				Value v0 = (FactIDValue) obj0;
				Value v1 = (FactIDValue) obj1;
				try {
					Fact f0 = v0.factValue(rete.getGlobalContext());
					v0 = f0.getSlotValue("name");
				} catch (Exception e) {
					trace.err("byNameSorter: Exception getting name slot from left operand "+
							v0+": "+e);
					v0 = null;
				}
				try {
					Fact f1 = v1.factValue(rete.getGlobalContext());
					v1 = f1.getSlotValue("name");
				} catch (Exception e) {
					trace.err("byNameSorter: Exception getting name slot from right operand "+
							v1+": "+e);
					v1 = null;
				}
				if (v0 == null)
					return (v1 == null ? 0 : 1);
				else if (v1 == null)
					return -1;       // sort null values to tail
				
				if (v0.equals(Funcall.NIL))
					return (v1.equals(Funcall.NIL) ? 0 : 1);
				else if (v1.equals(Funcall.NIL))
					return -1;       // sort nil values to tail, but before null
				
				String name0 = null, name1 = null;
				try {
					name0 = v0.stringValue(rete.getGlobalContext());
				} catch (JessException je) {
					name0 = null;
				}
				try {
					name1 = v1.stringValue(rete.getGlobalContext()); 
				} catch (JessException je) {
					name1 = null;
				}
				if (name0 == null)
					return (name1 == null ? 0 : 1);  
				else if (name1 == null)
					return -1;       // sort null or nil values to tail
				return name0.compareTo(name1);
			}
		};
		Collections.sort(facts, byNameSorter);
		if (vv == null)
			return;
		for (Iterator it = facts.iterator(); it.hasNext();) {
			FactIDValue fIdV = (FactIDValue) it.next();
			vv.add(fIdV);
		}
	}
	
	/**
	 * Process the {@link #interfaceActionsList} to set the initial values
	 * in the interface WMEs.
	 */
	private void loadInterfaceActions() {
	
		if (interfaceActionsList == null) {
			System.err.println("NULL value in MT.loadInterfaceActions():" +
							   " interfaceActionsList is null");
			return;
		}
		boolean first = true;
		for (Iterator<InterfaceAction> it = interfaceActionsList.iterator(); it.hasNext(); first = false) {
			if (first && console != null && console.getTextOutput() != null)
				console.getTextOutput().append("\nSetting start state values in working memory.");
			InterfaceAction ia = (InterfaceAction) it.next(); 
			Value factId = null;
			try {
				Fact f = modifyFromSAI(ia.selectionList, ia.actionList, ia.inputList);
				if (trace.getDebugCode("mt")) trace.out("mt", "InterfaceAction: " + f);
//				Value val = rete.eval(modifyCmd);
//				if (val.type() != RU.FACT ||
//						null == (factId = new FactIDValue(val.factValue(null)))) {
//					throw new JessException("MT.loadInterfaceActions()",
//							"bad modify result",
//							(val == null ? "(null)" : val.toString()));
//				}
//				trace.out("mt", "(modify) returned fact-id " + factId.toString());
				
				
				
				
				
			} catch (JessException je) {
				String errMsg = "Error setting initial value for selection " +
						(ia.selectionList.size() < 1 ?
								"(empty)" : ia.selectionList.toString());
				trace.err(errMsg);
				je.printStackTrace();
				if (console != null && console.getTextOutput() != null)
					console.getTextOutput().append("\n" + errMsg + ": " + je);
			}
		}
	}

	/**
	 * Resume from the current breakpoint.
	 * @param b if true, resume
	 */
	public void setResume(boolean b) {
		getModelTracing().resumeBreak.setResume(b);
	}
	
	/**
	 * Accessor for other classes in this package to the external environment.
	 * @return {@link #controller}
	 */
	public CTAT_Controller getController() {
		return controller;
	}

	/**
	 * @return the {@link #problemName}
	 */
	public String getProblemName() {
		return problemName;
	}

	/**
	 * Revise the facts that hold ProblemSummary and Skill info. 
	 * @param ps
	 */
	public void updateProblemSummaryFacts(ProblemSummary ps) {
		if(getRete() == null || !(getRete().getUseProblemSummary()))
			return;
		ProblemSummaryAccess psa = new ProblemSummaryAccess();
		psa.updateProblemSummaryFacts(ps, getRete(), null);
		if(wmeEditor != null && wmeEditor.getPanel() != null)
			wmeEditor.getPanel().refresh();
	}

	public JessModelTracing getJMT() {
		// TODO Auto-generated method stub
		return jmt;
	}
}
