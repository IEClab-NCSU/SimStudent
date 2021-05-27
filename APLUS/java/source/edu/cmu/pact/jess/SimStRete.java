package edu.cmu.pact.jess;

import java.awt.HeadlessException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.swing.JOptionPane;

import org.apache.commons.lang.ArrayUtils;

import sun.security.action.GetBooleanAction;
import jess.Activation;
import jess.Context;
import jess.Defrule;
import jess.Deftemplate;
import jess.Fact;
import jess.FactIDValue;
import jess.Jesp;
import jess.JessException;
import jess.JessToken;
import jess.RU;
import jess.Rete;
import jess.Strategy;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.TextOutput;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.HashMap;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.WmePathNode;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStNode;
import edu.cmu.pact.miss.jess.ModelTracer;
import edu.cmu.pact.miss.jess.GetCorrectSAI;
import edu.cmu.pact.miss.jess.UserFunctionPackage;

/**
 * This class extends the Rete class from jess package and is customized for
 * SimStudent.
 */
public class SimStRete extends MTRete implements Serializable, JessParser {

	/** Serialized constant for compatibility */
	private static final long serialVersionUID = 1L;

	/** Current problem name */
	public static String PROBLEM_NAME = "";

	/** Action component of the SAI */
	public static final String ACTION = "UpdateTable";
	
	/** Constant to set the slots in working memory  */
	public static final String NOT_SPECIFIED = "NotSpecified";

	/**	Maximum Search depth for iterative deepening  */
	static int DEFAULT_MAX_DEPTH = 2;
	
	/**	 */
	private int maxDepth = DEFAULT_MAX_DEPTH;
	
	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
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

	/** BehaviorRecorder Controller */
	private transient BR_Controller controller;

	public BR_Controller getController() {
		return controller;
	}

	public void setController(BR_Controller controller) {
		this.controller = controller;
	}

	private ModelTracer amt;
	
	public ModelTracer getAmt() {
		return amt;
	}

	public void setAmt(ModelTracer amt) {
		this.amt = amt;
	}
	
	private String runType = "";
	
	public String getRunType() {
		return runType;
	}

	public void setRunType(String runType) {
		super.setRunType(runType);
		this.runType = runType;
	}

	private String projectDirectory = "";
	
	public String getProjectDirectory() {
		return projectDirectory;
	}

	public void setProjectDirectory(String projectDirectory) {
		super.setProjectDirectory(projectDirectory);
		this.projectDirectory = projectDirectory;
	}
	
	HashMap wmeChildSlots = new edu.cmu.pact.miss.HashMap();
    Vector getWmeChildSlots( String wmeType ) throws Exception {
    	Vector childSlots = (Vector)wmeChildSlots.get( wmeType );
    	return childSlots;
    }

    private final String DEFAULT_STUCTURE_FILE="wmeStructure.txt";
    
	
	/** JessModelTracing */
	//private JessModelTracing jmt;
	
	public JessModelTracing getJmt() {
		//return controller.getModelTracer().getModelTracing();
			return jmt;
	}


	public void setJmt(JessModelTracing jmt) {
			this.jmt = jmt;
	}

	/** ModelTracer */
	private transient MT mt;
	
	public MT getMt() {
		return mt;
	}

	private void setMt(MT mt) {
		this.mt = mt;
	}

	/**	Console like text output facility */
	private transient TextOutput textOutput = TextOutput.getNullOutput();
	
	public TextOutput getTextOutput() {
		return textOutput;
	}

	public void setTextOutput(TextOutput textOutput) {
		this.textOutput = textOutput;
	}
	/***
	 * Method that returns the value of a fact
	 * 
	 * @param foaName
	 * @return
	 */
	public String getFactActualValue(String foaName) {		
		String ret="";
		ArrayList currentFacts = getFacts();
		 foaName=foaName.replace(" ", "");
		 
		 Fact f;
	    	Value val;
	    	
	    	for(int i=0; i<currentFacts.size(); i++){
	    			f = (Fact) currentFacts.get(i);
	    			try {
	    			val = f.getSlotValue("value");
	    			
	    				if (f.getSlotValue("name").equals(foaName)){
	    					ret= ""+val+"";
	    					return ret;
	    					}

	    			}
	    			catch (JessException e){
   				
	    			}    			 
	    	}
			return ret;		
	}
	
	
	
	/**
	 * @param brController
	 * Constructor for the rete. References to mt and jmt can be removed if we use our own
	 * user defined functions for the jess.
	 */
	public SimStRete(BR_Controller brController) {

		super(brController);
		controller = brController;
		mt=controller.getModelTracer();
	//	mt = new MT(brController);  // why not pass self as externally defined Rete?
		jmt = mt.getModelTracing();

		MT.loadDefaultUserfunctions(controller.getModelTracer(), this, null);
		ConflictResolutionStrategy.setStrategy(this);

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
		else if ((getController().getMissController() != null) && (getController().getMissController().getSimSt().isValidationMode()))
		{
			startElements = new ArrayList<String>();
			//String file = WebStartFileDownloader.SimStAlgebraPackage+"/"+SimStPLE.CONFIG_FILE;
			String file = this.getController().getMissController().getSimSt().getPackageName()+"/"+SimStPLE.CONFIG_FILE;
			ClassLoader cl = this.getClass().getClassLoader();
 	
	    	InputStream is = cl.getResourceAsStream(file);
	    	InputStreamReader isr = new InputStreamReader(is);
	    	BufferedReader reader=null;
	    	try
	    	{
	    		//reader=new BufferedReader(new FileReader(file));
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
		else if(getController().getMissController() != null && getController().getMissController().getSimSt().isSsWebAuthoringMode()){
			startElements = new ArrayList<String>();
			
			BufferedReader br = null;
			try
	    	{
	    		//reader=new BufferedReader(new FileReader(file));
				String file = this.getController().getMissController().getSimSt().getProjectDirectory()+"/"+SimStPLE.CONFIG_FILE;

	        	br = new BufferedReader(new FileReader(file));
	    		String line = br.readLine();
	    		
	    		while(line != null)
	    		{
	    			if(line.equals(SimStPLE.START_STATE_ELEMENTS_HEADER))
	    			{
	    				line = br.readLine();
	    				while(line != null && line.length() > 0) //Blank line starts next section
	    				{
	    					startElements.add(line);
	    					line = br.readLine();
	    				}
	    			}
	    			else if(line.equals(SimStPLE.PROBLEM_DELIMITER_HEADER)){
	    				SimSt.problemDelimiter = br.readLine();
	    			}
	    			if(line != null)
	    			{
	    				line = br.readLine();
	    			}
	    		}

	    	}
			catch(Exception e)
	    	{
	    		if(trace.getDebugCode("miss"))trace.out("miss", "Unable to read config file: "+e.getMessage());
	    		e.printStackTrace();
	    	}
			finally 
	    	{
	    		if(br != null)
	    			br.close();
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
		

	   // if(problem.length == 2 && !(problem[0].isEmpty()) && !(problem[1]).isEmpty()) {
	  //		setSAIDirectly(startElements.get(1), SimStRete.ACTION, problem[1]);
	//		setSAIDirectly(startElements.get(0), SimStRete.ACTION, problem[0]);
	//	}
		
		String[] problem=SimStRete.PROBLEM_NAME.split(SimSt.problemDelimiter);
		if (problem[0].contains("="))  problem=SimStRete.PROBLEM_NAME.split("=");	
		if (!problem[0].isEmpty()){
				for (int i=0;i<startElements.size();i++){	
					setSAIDirectly(startElements.get(i), SimStRete.ACTION, problem[i]);
				
				}				
		}
		
	}
	

	 public void initSsRete(String wmeTypeFile, String initalWmeFile ) throws JessException{  		
			this.batch(wmeTypeFile);
			this.batch(initalWmeFile);
	  }
	 
	/**
	 * @param problemName
	 * @param useBinary
	 * Initialize the working memory by loading the jess files namely init.wme, wmeTypes.clp
	 * and productionRules.pr
	 */
	public void initWMUsingJessFiles(String problemName, boolean useBinary) {	


		loadJessFiles(problemName, useBinary);
	
		
		loadWMEStructureFromFile("wmeStructure.txt");

		 
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
                console.getTextOutput().append("\n" + ex.toString() + "\n");
                ex.printStackTrace();
            }
        }			 
        clearRuleActivationTree();
        showActivations("StartStateEnd");  // debug routine
	}
	
	public void setUp(String[] fileNames) {
		
		try {
			clear();
			this.eval("(set-salience-evaluation when-activated)");

			// Better to write our own user-functions instead of meddling around with the existing
			// user-functions and devising a hack to work. Also we don't need all User-functions
			// MT.loadDefaultUserfunctions(getMt(), this, null);
			
			// define the global variables for hint and student selection
			this.eval("(defglobal ?*hintRequest* = "+SimStRete.NOT_SPECIFIED+")");
			this.eval("(defglobal ?*studentSelection* = "+SimStRete.NOT_SPECIFIED+")");
			loadUserfunctions(this, getAmt());
			if(fileNames.length > 0) {
				for(int i=0; i < fileNames.length; i++) {
					loadJessFile(fileNames[i]);
				}
			}
			//loadJessFile(rulesFile);
			clearRuleActivationTree();
			showActivations("StartStateEnd");
		} catch (JessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to add the user defined functions to the {@link jess.Rete}
	 * @param simStRete
	 * @param amt
	 */
	private void loadUserfunctions(SimStRete simStRete, ModelTracer amt) {
		
		String thisPkg;
		if(amt != null) 
			thisPkg = amt.getClass().getPackage().getName();
		else 
			thisPkg = "edu.cmu.pact.miss.jess";
		
		String[] userFunctions = {
				thisPkg+".ConstructTutorHintMessage",
				thisPkg+".ConstructCLHintMessage",
				thisPkg+".GetLhsForProblem",
				thisPkg+".GetRhsForProblem",
				thisPkg+".PredictedSAI",
				thisPkg+".CompProblemWithHindsight",
				thisPkg+".UpdateWMIfRuleSAIEqualsStudentSAI",
				thisPkg+".UpdateFailedQuizPListAndTutoredQuizPList",
				thisPkg+".UpdateWMIfStudentProblemMatchesList",
				thisPkg+".UpdateWMIfRuleSAIEqualsStudentEnteredSide",
				thisPkg+".GetQuizStatus",
				thisPkg+".GetFailedQuizItems",
				thisPkg+".GetProblem",
				thisPkg+".GetSimilarProblem",
				thisPkg+".ProblemType",
				thisPkg+".GetFirstProblem",
				thisPkg+".HasFirstSuggestedProblemBeenTutored",
				thisPkg+".GetCurrentStudentEnteredProblem",
				thisPkg+".GetAbstractedProblem",
				thisPkg+".AddProblemIfNotInList",
				thisPkg+".RemoveProblemFromHeadOfList",
				thisPkg+".IsProblemSubsetOfList",
				thisPkg+".GetCorrectSAI",
				thisPkg+".CheckCorrectnessSimStStep",
				thisPkg+".CheckCorrectnessSimStStepJO",
				thisPkg+".GetMTProblemSuggestion",
				thisPkg+".GetCorrectSelection",
				thisPkg+".GetTypeOfProblem",
				thisPkg+".GetOptimalSteps",
				thisPkg+".CanSolveProblem",
				thisPkg+".GetLastTutoredProblem",
				thisPkg+".IsSolutionNonOptimal",
				thisPkg+".AreLastProblemsSameType",
				thisPkg+".GetResource",
				thisPkg+".GetCurrentQuizSection",
				thisPkg+".GetCurrentQuizLevel",
				thisPkg+".IsProblemFromPassedLevel"
		
		};
		UserFunctionPackage ufp = new UserFunctionPackage(userFunctions, amt);
		if(ufp.size() < userFunctions.length)
			trace.err("Warning: Only " + ufp.size() + " of " + userFunctions.length + " userFunctions defined were loaded");
		simStRete.addUserpackage(ufp);
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
	
	public void goToWMState(ProblemNode startNode, Vector edge, boolean loadFiles){
		
    	ArrayList currentFacts = getFacts();
    	Fact f;
    	Value val;
    	
    	if(edge.size() == 0 || startNode == null)
    		return;
    	
    	for(int i=0; i<currentFacts.size(); i++){
    		f = (Fact) currentFacts.get(i);
    		if(f.getName().contains("MAIN::column") || f.getName().contains("MAIN::table") ||
    				f.getName().contains("MAIN::problem") || f.getName().contains("MAIN::initial-fact")
    				|| f.getName().contains("MAIN::ModelTrace"))
    			continue; // These facts don't have any slot named value, so skip them.
    		try {
				val = f.getSlotValue("value");
				String strValue = val.toString();
				if(strValue.equalsIgnoreCase("NotSpecified")){
					Value inputVal = stringToValue("nil");
					modify(f, "value", inputVal); // execute the modify cmd to set it to nil
				}
    		} catch (JessException e) {
    			trace.err("" + f + "	"  +  f.getName());
    			e.printStackTrace();
    		}
    	}
    	
    	restoreInitialWMState(startNode, loadFiles);
    	for(int i=0; i< edge.size(); i++){
    		ProblemEdge currEdge = (ProblemEdge) edge.elementAt(i);
    		setSAIDirectly(currEdge.getSelection(), currEdge.getAction(), currEdge.getInput());
    	}
    	
    	
	}
	
	/**
	 * Updates the Working Memory / Clears the Working memory depending upon whether it
	 * is a new problem.
	 * @param ssNode
	 * @param hm
	 */
    public void updateWorkingMemory(SimStNode ssNode, Map hm){
    
    	ArrayList currentFacts = getFacts();
    	Fact f;
    	Value val;
    	if(ssNode == null)
    		return;
    	
    	for(int i=0; i<currentFacts.size(); i++){
    		f = (Fact) currentFacts.get(i);
    		if(f.getName().contains("MAIN::column") || f.getName().contains("MAIN::table") || f.getName().contains("MAIN::problem"))
    			continue; // These facts don't have any slot named value, so skip them.
    		try {
				val = f.getSlotValue("value");
				String strValue = val.toString();
				if(strValue.equalsIgnoreCase("NotSpecified")){
					Value inputVal = stringToValue("nil");
					modify(f, "value", inputVal); // execute the modify cmd to set it to nil
				}
    		} catch (JessException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	try {
	    	if(!ssNode.getName().contains("state")){
	    		reset();
	    		init(ssNode.getName(), true);
	    	} else {
	    		Iterator itr = hm.keySet().iterator();
	    		while(itr.hasNext()){
	    			RuleActivationNode key = (RuleActivationNode)itr.next();
	    			SimStNode node = (SimStNode) hm.get(key);
	    			if(node.equals(ssNode)){
	    				setSAIDirectly(key.getActualSelection(), key.getActualAction(), key.getActualInput());
	    			}
	    		}
	    	}
    	} catch(JessException je){
    		je.printStackTrace();
    	} catch(Exception e){
    		e.printStackTrace();
    	}
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

	/**
	 * Run the rule engine. If the number of rules fired become greater
	 * than the argument then the engine stops.
	 */
	public int run(int maxNum) throws JessException {
	
		try {
			return super.run(maxNum);
		} catch(HaltReteException e){
			e.printStackTrace();
			return -1;
		}
	}
	
	@Override
	public Value parse(Reader rdr) throws JessException {
		return parse(rdr, false);
	}

	/**
	 * Save this object's state to the given stream. 
	 * <blockquote> Before saving it removes the Working Memory object
	 * due to some unknown bug in the way it does the serialization. Discussed 
	 * with Jonathan as well about the same.</blockquote>
	 */
	Routers saveState(ByteArrayOutputStream baos) throws IOException {

	    Routers routers = new Routers(this);
		if(getAmt() != null && getAmt().isModelTraceMode() /*getController() != null && getController().getAmt(). != null && getController().getAmt()
				.isModelTraceMode()*/) {
			try {
				//this.remove(getController().getAmt().getClonedWorkingMemory());
				this.remove(getController().getMissController().getSimSt().getModelTraceWM());
			} catch (JessException e) {
				e.printStackTrace();
			}
		}
    	super.bsave(baos);
		return routers;
	}
	
	/**
	 * Read this object's state from the given stream.
	 * <blockquote> After restoring the Rete from the given stream adds the Working Memory
	 * object to the Rete to compensate for the fact that while saving the Working Memory
	 * object had been removed from the Rete.</blockquote>
	 */
	void loadState(ByteArrayInputStream bais, Routers routers)
	throws IOException, ClassNotFoundException {
		super.bload(bais);
		if(getAmt() != null && getAmt().isModelTraceMode() /*getController() != null && getController().getAmt(). != null && getController().getAmt()
			.isModelTraceMode()*/) {
			try {
				//this.add(getController().getAmt().getClonedWorkingMemory());
				this.add(getController().getMissController().getSimSt().getModelTraceWM());
			} catch (JessException e) {
			e.printStackTrace();
			}
		}
		if (routers != null)
			routers.setRouters(this);
	}
	
	/**
	 * Removes the object from the working memory.
	 * @param o
	 * @throws JessException
	 */
	public void remove(Object o) throws JessException {
        undefinstance(o);
	}
	
	/**
	 * Debug routine to show activation state.
	 * @param label tag for debug message
	 */
	void showActivations(String label) {
		if (!trace.getDebugCode("mt"))
			return;
		int nActs = 0;
		for (Iterator it = listActivations(); it.hasNext(); ++nActs)
			it.next();
		int nFacts = 0;
		for (Iterator it = listFacts(); it.hasNext(); ++nFacts)
			it.next();
		if (trace.getDebugCode("mt")) trace.out("mt", label+" nActs="+nActs+", nFacts "+nFacts+", rete "+hashCode());
	}

	public boolean setSAIDirectly(String selection, String action, String input) {
	
	 
		// Get the fact
		Fact f = getFactByName(this, selection);
		
		if(f == null) {
			trace.err("Unable to find fact for " + selection + " while updating working memory...");
			trace.err("Facts are " + getFacts());
			return false;
		}

		
		// Get the value associated with the fact
		try {
			f.getSlotValue("value");
		} catch (JessException e) {
			e.printStackTrace();
		}
		
		try {
			Value inputVal = stringToValue(input);
            modify(f, "value", inputVal);
		} catch (JessException e) {
			e.printStackTrace();
		}
	
		   
	   updateChunkValues(selection, input);
		  
			
		    
		    
		return true;
	}
	
	public ArrayList getFacts() {
		ArrayList facts = new ArrayList();
		for(Iterator it = this.listFacts(); it.hasNext();) {
			facts.add(it.next());
		}
		return facts;
	}

	/**
	 * Convert a string into a Jess {@link Value} object.
	 * @param s String to convert
	 * @return Value of proper type; type is RU.SYMBOL if s is null
	 * @throws JessException
	 */
	public Value stringToValue(String s) throws JessException {
		return stringToValue(s, getGlobalContext());
	}
	
	/**
	 * Convert a string into a Jess {@link Value} object.
	 * @param s String to convert
	 * @param ctx context for {@link JessToken#valueOf(jess.Context)}
	 * @return Value of proper type;  if s is null,type is RU.SYMBOL and value is "nil"
	 * @throws JessException
	 */
	public static Value stringToValue(String s, Context ctx) throws JessException {
		Value iv = null;
		if(s==null)
			iv= new Value("nil",RU.SYMBOL);
		else
		{
			Value[] returnValue=new Value[1];
			edu.cmu.pact.jess.Utils.getJessType(s,returnValue);
			iv=returnValue[0]; 
		}
		return iv;
	}

	/**
	 * 
	 * @param ssRete
	 * @param name
	 * @return
	 */
	public Fact getFactByName(SimStRete ssRete, String name) {
		Iterator it;
		Fact fact = null;
		
		it = ssRete.listFacts();
		
		
		try {
			while (it.hasNext()) {
				fact = (Fact) it.next();
				if (fact.getDeftemplate().getSlotIndex("name") != -1) {

					Value v = fact.getSlotValue("name");

					if (v.stringValue(null).trim().equalsIgnoreCase(name)) {
						return fact;
					}
				}
				fact = null;
			}

		} catch (JessException je) {
			console.getTextOutput().append(je.toString());
			if (MTRete.breakOnExceptions) {
				MTRete.stopModelTracing = true;
			}

			// ta.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			it = null;
		}
		return fact;
	}

	/**
	 * Load the jess files before running the rule engine.
	 * @param rulesFile
	 */
	private void loadJessFile(String rulesFile) {
		
		String fileName = rulesFile; 
		if(!getController().getMissController().getSimSt().isSsWebAuthoringMode())
		 fileName = getDirectory(rulesFile) + rulesFile;
		String[] fileNames = {fileName};
		Object[] files = findFiles(fileNames);
		
        Reader rdr = null;
        String resource = null;
        for(int i=0; i< files.length; i++) {
        if(getController().getMissController() != null 
        		&& getController().getMissController().isPLEon() || getController().getMissController().getSimSt().isSsWebAuthoringMode()){
	        parse(files[i], false, false);
        	
        }
        //System.out.println(" Completed parsing the file !!!!");
        }
	}
	
	/**
	 * Load the deftemplates and facts files for a given test. If useBinary is
	 * set, tries to Rete.bload() that file first. Sets wmeTypesFromFile,
	 * wmeInstancesFromFile, rulesFromFile, bloadSuccessful.
	 * 
	 * @param basename
	 *            base part of filename for files to load; e.g., use
	 *            "AdditionChaining" to load AdditionChaining.clp,
	 *            AdditionChaining.wme, AdditionChaining.clp
	 * @param useBinary
	 *            true means try to load entire state from binary file
	 * @return array showing which files were read
	 */
	private void loadJessFiles(String problemName, boolean useBinary) {

		
		bloadSuccessful = false;
		wmeTypesFromFile = false;
		prRulesFromFile = false;
		wmeInstancesFromFile = false;

		// String wmeInstanceFilename =problemName + ".wme";
		/* If wmeInstanceFile doesn't exist, try init.wme */

		String[] filenames = { problemName + ".bload", // cf use of
				WMEEditor.wmeTypeFileName, // indices in
				WMEEditor.rulesFileName, // results[]
				"init.wme", problemName + ".wme" }; // below


		boolean[] results = null;
		if(!getController().getMissController().getSimSt().isSsWebAuthoringMode()){
			for (int i = 0; i < filenames.length; ++i) {
				filenames[i] = getDirectory(filenames[i]) + filenames[i]; // trailing "/" separator
		   }

		}
		
			
		if (!useBinary)
			filenames[0] = null;

		try {
			clear();
			results = loadJessFiles(filenames[0], filenames[1],filenames[2], filenames[3], filenames[4],
					null);
		} catch (Exception e) {
			trace.err("*** Error " + e + " trying to load Jess files " + e);
	
			return; // results[] null after exception: leave booleans false
		}

		
		
		
		bloadSuccessful = results[0];
		wmeTypesFromFile = (results[1] || results[0]); // cf filenames[] above
		prRulesFromFile = (results[2] || results[0]);
		wmeInstancesFromFile = (results[4] || results[0]);

	}

	
	public void clear() throws JessException {
		//if (trace.getDebugCode("mt11")) trace.printStack("mt11", "clear override called");
		//Fact f1=getFactByName("dorminTable3_C1R1");
		//trace.out("fact before : " + f1);
		
		super.clear();
		
		//Fact f=getFactByName("dorminTable3_C1R1");
		//trace.out("fact after : " + f);
		ConflictResolutionStrategy.setStrategy(this);
	}

	/**
	 * @param origFilename
	 * @return the problem model directory
	 * Returns the problem model directory if the specified file is located
	 * there, otherwise, returns the project directory if the specified file is
	 * located there, otherwise, returns null
	 */
	protected String getDirectory(String origFilename) {
		//trace.out("miss","---> originalFilename="+origFilename);
		
		/* If initialization wme files not in cog. model dir, try project dir */
		File f = null;
		String dirname = getProblemDirectory();
		//trace.out("miss","---> getProblemDirectory="+getProblemDirectory());
		
		boolean validFilename = checkFilenameValid(dirname + origFilename);
		
		//trace.out("miss","---> checkFilenameValid="+validFilename);
		
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
	 * Get the problem directory.
	 */
	String getProblemDirectory() {
		String result = ".";
		if (controller != null) {
			PreferencesModel pm = controller.getPreferencesModel();
			if (pm != null) {
				String prefValue = pm
						.getStringValue(CTAT_Controller.PROBLEM_DIRECTORY);
				if (prefValue != null && prefValue.length() > 0)
					result = prefValue;
			}
		}
		if (!result.endsWith(File.separator) && !result.endsWith("/"))
			return result + File.separator;
		else
			return result;
	}

	boolean[] loadJessFiles(String bloadName, String templatesName, String rulesName,
	         String problemFactsName, List interfaceTemplatesList) 
	throws JessException {
		boolean[] temp = loadJessFiles(bloadName, templatesName, rulesName, "	init.wme", problemFactsName, interfaceTemplatesList);
		boolean[] output = new boolean[4];
		
		
		
		output[0] = temp[0];
		output[1] = temp[1];
		output[2] = temp[2];
		output[3] = temp[4];
		
		return output;
		
	}

	/**
	 * Load the given deftemplates, facts and rules files. Any null file
	 * argument will be skipped.
	 *
	 * @param  bloadName name of a binary load Name to use
	 * @param  templatesName name of a deftemplates file to use
	 * @param  rulesName name of rules file to load; will call
	 * 	       {@link #parse(Reader, boolean) parse(rdr, true)} to remove
	 *         buggy rules after parse
	 * @param  factsName name of working memory instances file to load
	 * @param  
	 * @return array showing which files were read
	 * @exception JessException on bload() or parse()
	 */
	boolean[] loadJessFiles(String bloadName, String templatesName, String rulesName,
	        String initFactsName, String problemFactsName, List interfaceTemplatesList)                
	throws JessException {
		initFactsName=templatesName.replace("wmeTypes.clp", "init.wme");
		
	    String[] filenames = {bloadName, templatesName,	rulesName, initFactsName, problemFactsName};
	  
	    
	  
	   
	    
	    
	    final int templateFileIndex = 1;
	    final int rulesFileIndex = 2;
	    final int factsFileIndex = 3;  // 
	    Object[] files = findFiles(filenames);
	
	    
	    File bloadFile = null;
	    boolean[] results = new boolean[files.length];  // init'zes all false
	    boolean doBload = false;


	    
	    if (files[0] instanceof File && ((File) files[0]).exists()) {
	        bloadFile = (File) files[0];
	        long bloadTime = bloadFile.lastModified();
	        doBload = true;
	        for (int i = 1; doBload && i < files.length; i++) {
	            if (!(files[i] instanceof File)){
	                doBload = false;               // breaks loop
	            }
	            else {
	                File fi = (File) files[i];
	                if (!fi.exists() || bloadTime <= fi.lastModified())
	                    doBload = false;
	                if (trace.getDebugCode("mt")) trace.out("mt", "doBload " + doBload + ", files[" + i +
	                        "] " + fi + ", exists " + fi.exists());
	            }
	        }
	    }

	    
	    if (doBload) {
	        try {
	            BufferedInputStream in =
	                new BufferedInputStream(new FileInputStream(bloadFile));
	            String fileLoadMsg = "\nLoading saved start state from binary file " +
	            bloadFile.getAbsolutePath();
	            if (trace.getDebugCode("mt")) trace.out("mt", fileLoadMsg); 	    				
	            textOutput.append(fileLoadMsg);
	            bload(in);
	            in.close();
	            results[0] = true;
	            textOutput.append("\n");
	            return results;
	        } catch (Exception e) {
	            trace.err("Error trying to load file " +
	                    bloadFile.getAbsolutePath() + ": " + e);
	            clear();  // clean partially-loaded state, go on to parse files
	        }
	    }
	    if (bloadFile != null && bloadFile.exists())  // remove obsolete cache
	        bloadFile.delete();
	    String errorMessage = null;
	

	    
	   
	    for (int i = 1; i < files.length; i++) {
	        //Reader rdr = null;
	        //String resource = null;
            // trace.out("miss", "loadJessFiles: globalContext<1> = " + this.getGlobalContext());
	       //    trace.out("miss", "@@@@@ i = " + i + ": parse(" + files[i] + ", " + (i == rulesFileIndex) + ", true)");
	        // trace.out("eep","getMT() not null:"+(getMT()!=null));
	        if (getController().getMissController() != null && getController().getMissController().isPLEon())
		        results[i] = parse(files[i], (i == rulesFileIndex), false);
	        else
	        	results[i] = parse(files[i], (i == rulesFileIndex), true);
	        
//	        trace.err(files[i] + " loading is " + results[i]);

	    /*  
	  	attempt to use batch instead of parse. It worked, but no speed improvement
	    if (files[i]==null){
	    	results[i]=false;
	    	continue;
	    }
	    	
	       File f = (File) files[i];
	          String fullPath=null;
			try {
				fullPath = f.getCanonicalPath();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			
	       try{
	  	    	batch(fullPath);
	  	    	results[i]=true;
	  	    }
	  	    catch (JessException je) {
	  	    	je.printStackTrace();
	  	         errorMessage = "Error parsing file "+fullPath+" at line "+
	  	        je.getLineNumber()+":\n"+
	  	        (je.getDetail() == null ? "" : je.getDetail()+". ")+
	  	        (je.getData() == null ? "" : je.getData());
	  	        if (trace.getDebugCode("mt")) trace.out("mt", errorMessage);
	  	        textOutput.append("\nError parsing file "+fullPath+":\n  "+je+"\n");
	  	      results[i]=false;
	  	    } catch (Exception e) {
	  	         errorMessage = "Error parsing file "+fullPath+":\n  "+e;
	  	        if (trace.getDebugCode("mt")) trace.out("mt", errorMessage);
	  	        textOutput.append("\n"+errorMessage);
	  	        results[i]=false;
	  	    } 
	        
	       */
	        
	        
	        // trace.out("miss", "&&&&& MTRete.hashCode() = " + this.hashCode());
	        // trace.out("miss", "@@@@@ results[" + i + "]  = " + results[i]);
	        // trace.out("miss", "loadJessFiles: globalContext<2> = " + this.getGlobalContext());
	        // Value v = batch(resource);
            }
	    textOutput.append("\n");
	      //if (interfaceTemplatesList!=null)
	    	//	loadInterfaceTemplates(interfaceTemplatesList);
	   /* System.out.println("Completed parsing the file !!!");
	    for(int i=0; i<results.length; i++)
	    	System.out.println(" File : "+files[i]+"  result : "+results[i]);*/
	    
	    return results;
	}

	private int loadInterfaceTemplates(List interfaceTemplatesList) {
    int count = 0;

	    
    
    if (interfaceTemplatesList == null) {
        trace.err("MT.loadInterfaceTemplates(): interfaceTemplatesList is null");
        return 0;
    }
    for (Iterator it = interfaceTemplatesList.iterator(); it.hasNext(); ++count) {
        String deftemplateCmd = (String) it.next();
        try {
            if (count < 1 && textOutput != null)
                textOutput.append("\nLoading deftemplates from interface definitions.");
            Value val = eval(deftemplateCmd);
            if (trace.getDebugCode("mt")) trace.out("mt", "rete "+this.hashCode()+" deftemplate, result "+val+", type "+
                    RU.getTypeName(val.type())+":\n"+deftemplateCmd);					
        } catch (JessException e1) {
            String errMsg = "Error executing deftemplate command "+(count+1)+
            ":\n  "+deftemplateCmd+":\n  "+e1+
            (e1.getCause() == null ? "" : ";\n  "+e1.getCause().toString());
            trace.err(errMsg);
            e1.printStackTrace();
            textOutput.append("\n"+errMsg+"\n");
        }
    }
    return count;
}

	
	
	/**
	 * Find the given list of filenames on the classpath.  For each
	 * filename, tries to resolve as first as File, then as URL.
	 *
	 * @param  filenames filenames (Strings) to try to find
	 * @return array of File or URL entries with result for each file
	 *             requested; null entries for files not found
	 */
	private Object[] findFiles(String[] filenames) {
            
		Object[] results = new Object[filenames.length];
		
		
	
		for (int i = 0; i < filenames.length; i++) {
            
			File f = null;
			if (filenames[i] != null && filenames[i].length() > 0)
				f = new File(filenames[i]);
			if (f != null && f.isAbsolute() && f.exists()) {
				//trace.out("miss","#@$&&#$@#%* " + filenames[i] + " exists!" );
				results[i] = f;
			} else {
				//trace.out("miss","	#@$&&#$@#%* " + filenames[i] + " not... going for getFileAsResource " );
				trace.out(" Directory : "+getController().getMissController().getSimSt().getProjectDirectory());
				if(this.getController().getMissController().getSimSt().isSsWebAuthoringMode()) {
					if(this.getController().getRunType().equals("springBoot")) {
						String tempFileName = (filenames[i].contains("/"))? filenames[i].split("/")[1] : filenames[i];
						results[i] = new File(this.getController().getMissController().getSimSt().getProjectDirectory()+"/"+tempFileName);
					} else {
						results[i] = new File(this.getController().getMissController().getSimSt().getProjectDirectory()+"/"+filenames[i]);
					}
				} else 
				    results[i] = Utils.getFileAsResource(filenames[i], this);
				if (null == results[i] || !((File) results[i]).exists()){
					
					
					String current="somewhere";
					
					try {
						current = new java.io.File( "." ).getCanonicalPath();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			      
					results[i] = Utils.getURL(filenames[i], this);
					
					if (results[i]==null){
						String name=filenames[i];
						if (name!=null){
							String newLocation=current+"/"+name.substring(2);
							f = new File(newLocation);
							if (f != null && f.isAbsolute() && f.exists()){
								results[i]=f;
							}
							else{
									
							}
						}
					}
					
					
					
				}
				}
					
            
			if (trace.getDebugCode("mt")) trace.out("mt", "filenames[" + i + "]=" + filenames[i] + ": " +
					  (results[i] instanceof File ?
					   ((File) results[i]).getAbsolutePath() :
					   (results[i] instanceof URL ? results[i] : null)));
		}
		return results;
  	}

	/**
	 * Parse the contents of a file or URL into Jess.
	 * @param file File or URL with input
	 * @param removeBuggyRules argument to {@link #parse(Reader, boolean)}
	 * @param popupError whether to popup an error dialog on error
	 * @return true if read the file; false on error or no file found
	 */
	private boolean parse(Object file, boolean removeBuggyRules, boolean popupError) {
		
	    String fullPath = null;
	    Reader rdr=null;
	    
	    try {
	        if (file instanceof File) {
	            File f = (File) file;
	            fullPath = f.getCanonicalPath();
	            rdr = new FileReader(f);
	        } else if (file instanceof URL) {
	            URL url = (URL) file;
	            fullPath = url.toString();
	            rdr = new InputStreamReader(url.openStream());
	        } else {
	            return false;
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
	        String errorMessage = "Error reading file "+fullPath+":\n  "+e;
	        if (trace.getDebugCode("mt")) trace.out("mt", errorMessage);
	        textOutput.append("\n"+errorMessage);
	        if (popupError)
	            JOptionPane.showMessageDialog(null, errorMessage,
	                    "Jess File Evaluation Error", JOptionPane.WARNING_MESSAGE);
	        return false;
	    }
	    return parse(rdr, fullPath, removeBuggyRules, popupError);
	   /* try{
	    	batch(fullPath);
	    	return true;
	    }
	    catch (JessException je) {
	    	je.printStackTrace();
	        String errorMessage = "Error parsing file "+fullPath+" at line "+
	        je.getLineNumber()+":\n"+
	        (je.getDetail() == null ? "" : je.getDetail()+". ")+
	        (je.getData() == null ? "" : je.getData());
	        if (trace.getDebugCode("mt")) trace.out("mt", errorMessage);
	        textOutput.append("\nError parsing file "+fullPath+":\n  "+je+"\n");
	        if (popupError)
	            JOptionPane.showMessageDialog(null, errorMessage,
	                    "Jess File Evaluation Error", JOptionPane.WARNING_MESSAGE);
	        return false;
	    } catch (Exception e) {
	        String errorMessage = "Error parsing file "+fullPath+":\n  "+e;
	        if (trace.getDebugCode("mt")) trace.out("mt", errorMessage);
	        textOutput.append("\n"+errorMessage);
	        if (popupError)
	            JOptionPane.showMessageDialog(null, errorMessage,
	                    "Jess File Evaluation Error", JOptionPane.WARNING_MESSAGE);
	        return false;
	    } 
	    */
	}

	/**
	 * Parse an input file. Calls {@link #parse(Reader, boolean)}. Presumes that input
	 * stream is from a file, not a console. Displays results on JessConsole and in
	 * popup error msg.
	 *
	 * @param  rdr Reader opened on file to parse
	 * @param  fullPath filename for diagnostic msgs 
	 * @param  removeBuggyRules if true, will remove buggy rules after the parse;
	 *         see {@link #extractBuggyRules()}
	 * @return true if parse successful
	 * @exception JessException from {@link Jesp.parse(boolean,Context)}
	 */
	private boolean parse(Reader rdr, String fullPath, boolean removeBuggyRules,
	        boolean popupError) {
	
		
	    try {
	        String fileParseMsg = "\nReading " + Utils.getBaseName(fullPath, false)+
	        " ("+fullPath+")"; 
	       // trace.err(fileParseMsg);
	        if (trace.getDebugCode("mt")) trace.out("mt", fileParseMsg);
	        //Thread.dumpStack();
	        textOutput.append(fileParseMsg);
	        Value lastV = parse(rdr, removeBuggyRules);
	        return true;
	    } catch (JessException je) {
	    	je.printStackTrace();
	        String errorMessage = "Error parsing file "+fullPath+" at line "+
	        je.getLineNumber()+":\n"+
	        (je.getDetail() == null ? "" : je.getDetail()+". ")+
	        (je.getData() == null ? "" : je.getData());
	        if (trace.getDebugCode("mt")) trace.out("mt", errorMessage);
	        textOutput.append("\nError parsing file "+fullPath+":\n  "+je+"\n");
	        if (popupError)
	            JOptionPane.showMessageDialog(null, errorMessage,
	                    "Jess File Evaluation Error", JOptionPane.WARNING_MESSAGE);
	        return false;
	    } catch (Exception e) {
	        String errorMessage = "Error parsing file "+fullPath+":\n  "+e;
	        if (trace.getDebugCode("mt")) trace.out("mt", errorMessage);
	        textOutput.append("\n"+errorMessage);
	        if (popupError)
	            JOptionPane.showMessageDialog(null, errorMessage,
	                    "Jess File Evaluation Error", JOptionPane.WARNING_MESSAGE);
	        return false;
	    } finally {
	        try {
	            if (rdr != null)
	                rdr.close();
	        } catch (Exception e) {}
	    }
	}

	/**
	 * Parse an input file. Uses {@link jess.Jesp}. Presumes that input
	 * stream is from a file, not a console.
	 *
	 * @param  rdr Reader opened on file to parse
	 * @param  removeBuggyRules if true, will remove buggy rules after the parse;
	 *         see {@link #extractBuggyRules()}
	 * @return result of the last parsed entity
	 * @exception JessException from {@link Jesp.parse(boolean,Context)}
	 */
	public Value parse(Reader rdr, boolean removeBuggyRules)
			throws JessException {

		//if (trace.getDebugCode("rr")) trace.out("rr", "Enter parse in MTRete");
		UID uid = new UID(); // for unique router name
		if (!(rdr instanceof BufferedReader || rdr instanceof StringReader))
			rdr = new BufferedReader(rdr);
		// addInputRouter(uid.toString(), rdr, false); // false=>not consoleLike
	//	System.out.println("Parsing ..............");
		Jesp jesp = new Jesp(rdr, this);
	//	System.out.println(" Jesp object .... "+jesp.toString());
	//	System.out.println(" Rete   : "+this.getGlobalContext().toString());
		trace.out(" Before Working memory : "+this.getController().getModelTracer().getRete().getFacts());

		Value result = jesp.parse(false, this.getGlobalContext());
		
		trace.out(" MTRete  Working memory : "+this.getController().getModelTracer().getRete().getFacts());
		//System.out.println(" SimSTRete After  Working memory : "+this.getController().getMissController().getSimSt().getSsRete().getFacts());

	//	System.out.println(" Result of parsing : "+result.toString());
		// removeInputRouter(uid.toString());
		// if (removeBuggyRules)
		// unloadBuggyRules();
		// if (trace.getDebugCode("rr")) trace.out("rr", "Exit parse in MTRete");
		//System.out.println("mpikame");
		//Thread.dumpStack();
		return result;
	}

    /**
     * Return some initial sequence of the agenda onto a list.
     * Copies all {@link jess.Activation} entries, regardless of whether active.
     * @param stopAct if not null, stop just before adding this activation
     */
    public List /* Activation */ getAgendaAsList(Activation stopAct) {
    	
        List result = new ArrayList();
        int i = 0;
        for (Iterator it = listActivations(); it.hasNext(); ++i) {
        	
            Activation act = (Activation) it.next();
            if (trace.getDebugCode("mt")) trace.out("mt", "agenda["+i+"]= "+
                    (act.isInactive() ? "IN "  : "AC ")+
                    act);
            if (stopAct != null && stopAct == act)
                break;
            result.add(act);
        }
        return result;
    }
   
    
	/**
	 * Debug method to dump the activation list.  No-op if debug code "mt" unset. 
	 * @param  label print this String before the agenda; "dumpAgenda" if null
	 */
	public void dumpAgenda(String label) {
	    dumpAgenda("mt", label, false);
	}
	
	/**
	 * Debug method to dump the activation list.  No-op if debug code not set. 
	 * @param  debugCode if "err", use {@link trace#err(String)};
	 *             use {@link trace#out(String, String) trace#out(debugCode, String)}
	 * @param  label print this String before the agenda; "dumpAgenda" if null
	 * @param  verbose true means long format of activation entries
	 */
	public void dumpAgenda(String debugCode, String label, boolean verbose) {
	    if (debugCode == null)
	        return;
	    boolean err = "err".equals(debugCode); 
	    if (!err && !trace.getDebugCode(debugCode))
	    	return;
	    StringBuffer sb =
	        new StringBuffer(label == null ? "dumpAgenda()" : label);
	    sb.append(":");
//	    Map actMap = new HashMap();
	    Iterator it = listActivations(); 
	    for (int i = 0; it.hasNext(); ++i) {
	        Activation a = (Activation) it.next(); 
//	        actMap.put(new Integer(a.hashCode()), a);
	        sb.append("\n ");
	        if (i < 10) sb.append(" ");
	        sb.append(i).append(".");
	        sb.append(a.isInactive() ? "IN " : "AC ");
	        if (!verbose)
	        	sb.append(a.toString());
	        else {
	        	sb.append('[').append(a.getRule() == null ? "(null rule)" : a.getRule().getName());
	        	sb.append(' ').append(a.getToken() == null ? "(null token)" : a.getToken().toString());
	        	sb.append("; salience ").append(a.getSalience()).append(']');
	        }
	    }
	    sb.append(" <").append(getClass().getName()).append(".dumpAgenda>");
	    if (err)
	    	trace.err(sb.toString());
	    else
	    	trace.out(debugCode, sb.toString());
	}

	/**
	 * Tell our Strategy which Activation we are firing now.
	 * Calls {@link BuggyRulesLaterStrategy#setActToFire(Activation).
	 * @param act the Activation to fire next; null to restore normal strategy
	 */
	public void setActivationToFire(Activation act) {
		Strategy s = getStrategy();
		if (!(s instanceof ConflictResolutionStrategy)){
			return;
		}
		((ConflictResolutionStrategy) s).setActToFire(act, this);
		try {
			setStrategy(s);  // prompt Rete to resort the agenda?
			if (trace.getDebugCode("mt")) trace.out("mt", "setActivationToFire() a["+
					getActivationIndex(act, this)+"]: "+act);
		} catch (JessException je) {
			je.printStackTrace();
		}
	}
	
	public static int getActivationIndex(Activation act, Rete rete) {
		if (act == null)
			return -2;
		int result = 0;
		for (Iterator it = rete.listActivations(); it.hasNext(); ++result) {
			if (act.equals(it.next()))
				return result;
		}
		return -1;
	}


	 
	    
	    
	    /**
		 * This retrieves the children of a wme based on hierarchy defined in
		 * structures.txt.
		 * @param f the fact
		 */
		 private Vector getChildren(Fact f){
			 
			 String ne=f.getName().toString().replace("MAIN::", "");
				Vector childs = new Vector();
				try {
					childs=getWmeChildSlots(ne);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 return childs;
		 }
	 
	 /**
	  *  this returns true if fact has a value slot
	  * @param f the fact to check
	  * */
	 boolean hasSlotValue(Fact f){
		 boolean returnValue=true;
		 Deftemplate factTemplate= f.getDeftemplate();
		 
		 if (factTemplate.getSlotIndex("value") < 0) returnValue=false;

			/*String[] slotNames=factTemplate.getSlotNames();
			if ( ArrayUtils.contains( slotNames, "value" ) ){
				returnValue=true;
			}*/
		 	 
		 return returnValue;
	 }

	 
	
	 
	 public boolean loadWMEStructureFromFile(String structureFilePath) 
	 {
	    	/*
	    	 * ;; is a commented line
	    	 * 
	    	 * 
	    	 * ;;WME Structure
	    	 (parent child+)+(each parent gets it's own line

			---------------(seperator)
			;;terminal WMEs
			WME+
			--------------
			;;Ignored WMEs
			WME*

	    	 */
		
		 	//File file=new File( this.getAmt().getController().getMissController().getSimSt().getProjectDir(), DEFAULT_STUCTURE_FILE );
		 
		 		File file;

	    	  BufferedReader reader=null;
	    	
	    	  boolean isWebstartMode= this.getController().getMissController().getSimSt().isWebStartMode();
	    	  

	          boolean isWebAuthoring = this.getController().getMissController().getSimSt().isSsWebAuthoringMode();
	          
	          if(isWebAuthoring){
	        	  /*ClassLoader cl = this.getController().getMissController().getSimSt().getClass().getClassLoader();
	        	  InputStream is = cl.getResourceAsStream(this.getController().getMissController().getSimSt().getPackageName()+"/wmeStructure.txt");
	              InputStreamReader isr = new InputStreamReader(is);*/
	        	  trace.out(" Project Directory : "+this.getController().getMissController().getSimSt().getProjectDirectory());
	        	  file=new File(this.getController().getMissController().getSimSt().getProjectDirectory()+"/"+structureFilePath);
	        	  try
		  	    	{
		  	    		reader=new BufferedReader(new FileReader(file));
		  	    	}
		  	    	catch(FileNotFoundException e)
		  	    	{
		  	    		e.printStackTrace();
		  	    		return false;
		  	    	}
		        	  
	          }
	          else if(!isWebstartMode) {
	   
	        	  file=new File(structureFilePath);
	        	  try
	  	    	{
	  	    		reader=new BufferedReader(new FileReader(file));
	  	    	}
	  	    	catch(FileNotFoundException e)
	  	    	{
	  	    		e.printStackTrace();
	  	    		return false;
	  	    	}
	        	  
	        	  
	          } else { // Added for WebStart
	      
	        		ClassLoader cl = this.getController().getMissController().getSimSt().getClass().getClassLoader();
	        		
	                InputStream is = cl.getResourceAsStream(WebStartFileDownloader.SimStAlgebraPackage+"/wmeStructure.txt");
	                InputStreamReader isr = new InputStreamReader(is);
	                reader = new BufferedReader(isr);
	        		
	        		
	        	  
	          }        
	    	
	    	
	   
	    	
	    	
	    	String curLine;
	    	boolean loadStructure=true;
	    	boolean loadTerminals=false;
	    	boolean loadIgnore=false;
	    	Pattern whitespace=Pattern.compile("\\s+");
	    	do
	    	{
	    		try
	    		{
	    			curLine=reader.readLine();
	    		}
	    		catch(IOException e)
	    		{
	    			e.printStackTrace();
	    			return false;
	    		}
	    		if(curLine==null)
	    			break;
	    		if(curLine.startsWith(";;") || whitespace.matcher(curLine).matches()||curLine.equals("") )
	    			continue;
	    		
	    		curLine=curLine.trim();
	    		if(curLine.startsWith("-"))
	    		{
	    			if(loadStructure)
	    			{
	    				loadStructure=false;
	    				loadTerminals=true;
	    			}
	    			
	    			else if(loadTerminals)
	    			{
	    				loadTerminals=false;
	    				loadIgnore=true;
	    			}
	    			continue;
	    		}
	    		if(loadStructure)
	    		{
	    			//While reading in contents, also fill in the wmeBranches hashMap
	    			String[] contents=whitespace.split(curLine);
	    			String parent=contents[0].replace("MAIN::","");
	    			Vector children=new Vector();
	    	  
	    			if (contents[1].contains(",")){ 				
	    				String[] tmp=contents[1].split(",");
	            		Collections.addAll(children, tmp);
	    			}
	    			else children.add(contents[1]);
	    			
	    			
	    			wmeChildSlots.put(parent,children);
		
	    			
	    		}
	    		
	    	} while(curLine!=null);
	    	
	    	if(reader != null){
	    		try {
	    			//System.out.println(" Successfully read the file");
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
				
			return true;
	    }
	 
	 
		
	 /**
		 * This returns true if "currentfact" is parent of "testFact".
		 * @param currentFact fact to test
		 * @param testFact potential parent fact
		 * */
		protected boolean isParent(Fact currentFact, Fact testFact) throws JessException{
			String multislotName=getMultislotName(testFact);
			Deftemplate template=currentFact.getDeftemplate();
			int numberSlots=template.getNSlots();
					for (int i=0;i<numberSlots;i++){
							
						if (template.getSlotName(i).equals(multislotName)){
							return true;
						}					
					}
							
				return false;			
		}
		
		/**
		 * This returns a string with the "multislot" name of a fact (i.e. MAIN::table --> tables).
		 * @param fact fact to convert
		 * */
		protected String getMultislotName(Fact fact){	
			String ret=fact.getName();
			String ret2=ret.replace("MAIN::", "")+"s";
			return ret2;
		}
		
	 
		/**
		 * This returns the parent of a fact 
		 * @param fact 
		 * */	
		protected Fact getFactParent(Fact fact) throws JessException{
			

			Iterator iter=this.listFacts();
			
			while(iter.hasNext())
	    	{
				Fact curFact=(Fact)iter.next();
	    			  		
	    		if (isParent(curFact,fact)){
 				
	    				Value tableValues=curFact.getSlotValue(getMultislotName(fact));    	    				
		    			ValueVector tables=tableValues.listValue(getGlobalContext());		    			
		    			for (int i=0;i<tables.size();i++){
		    				Fact tmpTable=tables.get(i).factValue(getGlobalContext());		    					
		    					if (tmpTable.getSlotValue("name").equals(fact.getSlotValue("name"))){
		    						return curFact;			
		    					}
		    			}
			
	    		}
			  
	    	}	
	    		return null;
		}
		
		
	 
		/**
		 * High level function that updates wme values
		 * @param selection
		 * @param input
		 * */	
	 public void updateChunkValues(String selection, String input){
		 		 
		 if (wmeChildSlots.isEmpty())
			 loadWMEStructureFromFile(DEFAULT_STUCTURE_FILE);
		 
		try {	 
			//we only have one selection so get the first elements
			updateParentChunkfromFact(getFactByName(selection),input); 
		 } catch (JessException e) {
			//e.printStackTrace();
		}
		 
	 }
	
	 	/**
	    *	Method that checks if a wmeValue is a list (e.g. [nil,[nil,nil]] is a list).
	    *	@param 
	    *	@return true if value is a list
	    */
	    static public boolean isList(String value){
	    	return (value.contains(","));
	    }
	    
	    /** Parser to recursively split a chunk into sub-chunks.
	     *  @param value is the string value of a chunk (e.g. [nil,[nil,nil]])
	     * */
	    static private  Vector<String> parseChunk(String value){	
	    	Vector<String> returnVector=new Vector();

	    	 String token="";
	    	 int depth=0;
	    	 char[] chars = value.toCharArray();
	    	 for (int i = 0, n = chars.length; i < n; i++) {
	    	     char c = chars[i];
		     
	    	    	 	 if (c=='['){ //if a new [ was detected then increase depth and add it to tken
	    	    	 		 depth++;
	    	    	 		 token+=c; 
	    	    	 	 }
	    	    	 	 else if (c==']'){	//if ] was detected add it to doken and decre
	    	    	  	 	token+=c; 
	    	    	 		 //if (depth!=0)
	    	    	 			depth--;
	    	    	 	 } 
	    	    	 	 else if ((c==',') &&  (depth==0)){ //if comma and depth is zero then we have a sub-chunk
	    	    	 		returnVector.add(token);
		    				token=""; 	 		 
	    	    	 	 }
	    	    	 	 else /*any other character*/{ 
	    	    	 		token+=c;
	    	    	 	 }
	    	    	

	    	 }  
	    	 returnVector.add(token);	//need to add it because we don't have a parenthesis at the end
	    	
	    	return returnVector;
	    	
	    }
	    
	    
	    /**
	     *	 Method that recursively finds the if a wme is "empty" (nil). This function accounts also for chunks, which is 
	     *   considered empty ONLY IF all its values are nil (i.e. [nil,[nil,3]] is not nil). 
	     *	 @param value: wme value to check
	     *   
	     */
	     static public boolean isNilVal(String value){
	 		boolean returnVal=true;

	 		if (value.charAt(0) == '[') {
	 			  if (value.charAt(value.length()-1) != ']') {
	 			    trace.err("an improper wme chunk was detected ");
	 			    new Exception().printStackTrace();
	 			  } else {
	 			     value = value.substring(1,value.length() -1);
	 			}
	 		}
	 		
	 		 Vector<String> chunks=parseChunk(value);	
	 	 
	 	 		for (String chunk:chunks){
	 	 				if (isList(chunk)){
	 	 					if (isNilVal(chunk)==false){
	 	 						returnVal=false; 	
	 	 						break;
	 	 					}
	 	 						
	 	 				}
	 	 				else{
	 	 					
	 	 					if (!chunk.equals("nil")){
	 	 						returnVal=false;
	 	 						break;
	 	 					}
	 	 				}
	 	 		}
 		 
	 		 return returnVal;
	 	 }
		 
		 
		 	 /** Method that returns the default nil value for a specific wme (e.g. for a fraction
		 	  * chunk the nil value is [nil,nil]).
			  * @throws JessException 
			  * @param wmeName is the name of the wme
			  * **/
			 public String nilValue(String wmeName){
				 if (!wmeName.contains("fraction"))  return "nil";
				 Fact fact=this.getFactByName(wmeName);	
			
			    String value="";
				try {
					//System.out.println("fact " + wmeName + " is " + fact);
					value = fact.getSlotValue("value").toString();
				} catch (JessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				 //replace all numeric values to nil 
				 String pattern = "(\\d)|(nil$)";	
				//return value.replaceAll(pattern, "nil") ;  
				 
			     String returnValue=value.replaceAll(pattern, "nil") ;
			   
			     if (!returnValue.contains("nil") || returnValue.contains("nilnil"))
			    	 returnValue="nil";
			 
			     return returnValue;	 
			 }
			 
		 
		 

	 
	 /**
	  * This function fills the value of a fact, based on the values of its children.
	  *  
	  * @param f the fact to update its value
	  * @param otherFactName factName of fact that if is a child of f, then f value is updated with his value (which is specified be @param oldValue)
	  * @param oldValue vale of "otherFactName". This must be passed as an argument because not all facts have slot "value".
	  * */
	 private String getNewFactValue(Fact f,String otherFactName,String oldValue) throws JessException{
		 Vector childs=getChildren(f);
		 String newValue="";

		
		 for (int e=0;e<childs.size();e++){
				
				Value v=f.getSlotValue((String) childs.get(e));
				ValueVector vv=v.listValue(getGlobalContext());
				
				for (int i=0;i<vv.size();i++){
 				Fact tmpvv=vv.get(i).factValue(getGlobalContext());
 				
   					if (tmpvv.getSlotValue("name").equals(otherFactName)){		
 						newValue+=oldValue;		
 						//trace.out("				1: old Value = " + oldValue + " and new is " + newValue);
 					}
 					else{					
 						newValue+=tmpvv.getSlotValue("value").toString()+",";
 						//trace.out("				2: new Value = " + newValue + " and we ignore old value which is " + oldValue);
 						newValue=newValue.substring(0,newValue.length()-1);				
 					}
 					newValue+=",";
 			}
				
			}
			newValue=newValue.substring(0,newValue.length()-1);
			newValue=newValue.replace("\"", "");
			return newValue;
	 }
	 
	/**
	 * This recursively updates the values of a wme in the wme path.
	 * @param f1 fact
	 * @param value the value of the fact. We need to pass this to next iterations, as not all wmes has slot "value". 
	 * */
		protected void updateParentChunkfromFact(Fact f1, String value) throws JessException{
		
			Fact f=getFactParent(f1);
			if (f==null) return ;
			else{
				 String newValue=""; 
		
				 		
						// If wme has a "value" slot, update it.
						if (hasSlotValue(f)){
							trace.err("I have slot value, now modify!!!!!");
							newValue="[" + getNewFactValue(f,f1.getSlotValue("name").toString(),value) + "]";
							Value iv=stringToValue(newValue);
							modify(f, "value", iv);
						}
						else newValue= getNewFactValue(f,f1.getSlotValue("name").toString(),value) ;
						
				updateParentChunkfromFact(f,newValue);
			}
				
		}
		
	
		
	/**
	 * Conflict resolution strategy that randomly orders rules randomly
	 * @author simstudent
	 *
	 */
		public static class RandomStrategy implements Strategy, Serializable {
				
			 public int compare(Activation act1, Activation act2) 
			  {
			    int returnVal;
			    Random randomGenerator = new Random();
			    double num1=randomGenerator.nextDouble();
			    double num2=randomGenerator.nextDouble();
			    
			    if( num1 > num2) 
			    {
			    	returnVal = 1;	
			    }
			    else if(num1<num2) 
			    {
			    	returnVal = -1;
			    }
			    else 
			    {
			    	returnVal = 0;
			    }
			    return returnVal;
			  }
	
			@Override
			public String getName() {
				return "strategy_random";
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
