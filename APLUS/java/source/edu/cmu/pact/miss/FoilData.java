/**
 * f:/Project/CTAT/ML/ISS/miss/FoilData.java
 *
 *	A FoilData Object holds all information necessary to compile
 *	an input for FOIL to induce a single feature predicate.  All
 *	those information are gathered from instructions.  
 *
 * Created: Fri Feb 25 16:20:28 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0 */

package edu.cmu.pact.miss;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import edu.cmu.pact.Utilities.trace;


public class FoilData implements Serializable{

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // -

    // FOIL program
    // private final String FOIL_EXE = "f:/Project/CTAT/ML/FOIL/foil6.exe";
	private static final long serialVersionUID = 2893338886802027987L;
    
    private static String foilName = getFoilNameForOS();
    
    public static String foilBase = null;
    public static void setFoilBase( String foilBase ) {
        FoilData.foilBase = foilBase;
        FOIL_EXE = foilBase + "/" + foilName;
    }
    private void setFoilBase() {
        if(trace.getDebugCode("gusIL"))trace.out("gusIL", "SimSt.getHomeDir() = " + SimSt.getHomeDir());
	if ( isFoilExistIn( SimSt.getHomeDir() + "/FOIL6"  ) ) {
	    foilBase = SimSt.getHomeDir() + "/FOIL6";
	} else {
	    foilBase = "f:/Project/CTAT/ML/FOIL6";
	}
    }
    private boolean isFoilExistIn( String dir ) {
	return new File(dir).exists();
    }
    private static String FOIL_EXE;
    private static String FOIL_JARDIR = SimSt.getHomeDir()+"/bin";
    public String getFoilExe() { return FOIL_EXE; }
    
    private static String getFoilNameForOS() {
    	String os = System.getProperty("os.name").toLowerCase();
    	if(os.indexOf("win") >= 0) {
			return "foil6.exe";
		} else if(os.indexOf("mac") >= 0) {
			ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.command("bash", "-c", "sysctl -a | grep machdep.cpu.brand_string");
			try {
				Process process = processBuilder.start();
				StringBuilder output = new StringBuilder();

		        BufferedReader reader = new BufferedReader(
		                new InputStreamReader(process.getInputStream()));

		        String line;
		        while ((line = reader.readLine()) != null) {
		            output.append(line + "\n");
		        }

		        int exitVal = process.waitFor();
		        reader.close();
		        if (exitVal == 0) {
		            if (output.toString().toLowerCase().contains("intel")) {
		            	return "foil6_mac_intel";
		            }
		            else {
		            	return "foil6_mac_m1";
		            }
		        } else {
		        	trace.err("Your OS is not supported for the foil. You need to compile foil for your OS.");
		        	return "foil6";
		        }
			} catch (IOException e) {
		        e.printStackTrace();
		    } catch (InterruptedException e) {
		        e.printStackTrace();
		    }
			return "foil6";
		} else if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
			return "foil6_nix";
		} else {
			trace.err("Your OS is not supported for the foil. You need to compile foil for your OS.");
			return "foil6";
		}
    }
    
    public void setFoilDir() {    

    	
        foilBase = System.getProperty("ssFoilBase");
        if (foilBase!=null)
            foilBase = SimSt.stripQuotes(foilBase);

        if(trace.getDebugCode("gusIL"))trace.out("gusIL", "foilBase = " + foilBase);
        
        //if UNDEFINED
	if ( foilBase == null ) {         //if System property ssFoilBase is null
		if(trace.getDebugCode("gusIL"))trace.out("gusIL", "entered if");
	    setFoilBase(); //tries to set foilDir to HomeDir. If fails, sets it to default
	}
	FOIL_EXE = foilBase + "/" + foilName;
	if(trace.getDebugCode("miss"))trace.out("miss","setFoilDir for FOIL_EXE: " + FOIL_EXE);
    }

    // Checks if app. is running locally or using WebStart
    // Set when the application launches 
    public static boolean WEBSTARTMODE = false;
    public static boolean WEBAUTHORINGMODE = false;
    public static String WEB_AUTHORING_TRAINING_DIRECTORY="/Users/simstudent/Documents/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/SimStudentServlet/WEB-INF/classes/";
    private final String FOIL_LOG = "foil-log";
 
    private final String LOG_DIR = "./"; 
    private String foilLogDir = LOG_DIR;
    public static final String TYPE_NAME="V";
    public static final int TYPE_NO=0;
    private String getFoilLogDir() { return this.foilLogDir; }
    public void setFoilLogDir( String foilLogDir ) {
    if(trace.getDebugCode("miss"))trace.out("miss", "setFoilLogDir: " + foilLogDir);
	this.foilLogDir = foilLogDir;
	// Make sure that the logDir does exist
	File file = new File( foilLogDir );
	if ( !file.exists() || !file.isDirectory() ) {
	    file.delete();
	    file.mkdir();
	}
    }
    
    public final String USE_DEFAULT_TUPLES="Use default foil tuples";
    private String foilMaxTuples = USE_DEFAULT_TUPLES;
    private String getMaxTuples() { return this.foilMaxTuples; }
    private void setMaxTuples(String foilMaxtuples) {  this.foilMaxTuples = foilMaxtuples; }
    
    

    // private final String TMP_DIR = "c:/tmp/foil/";
    // private final String TMP_DIR = "f:/Project/CTAT/ML/ISS/miss/";

    // The first few lettres on the target line in the FOIL logfile
    // where the induced horn clause is recorded
    private final String FOIL_TARGET_LINE = "^Clause [0-9]+:.*";

    // Name of the target feature preducate
    // Just a "name", e.g., copy-rhs, no arguments in the parentheses
    private String targetName;
    String getTargetName() { return this.targetName; }
    private void setTargetName( String targetName ) {
	this.targetName = targetName;
    }

    private Relation targetRelation = null;
    // The target relation
    public Relation getTargetRelation() {
	return this.targetRelation;
    }
    /**
     * 
     * @return the number of arguments to the target relation
     */
    public int getTargetRelationArity()
    {
    	return targetRelation.getArity();
    }
    private void setTargetRelation( Relation targetRelation ) {
	this.targetRelation = targetRelation;
    }

    // A list of relations
    private Hashtable relationHash = new Hashtable();
    private void addRelation( String predicate, Relation relation ) {
	relationHash.put( predicate, relation );
    }
    private Relation getRelation( String predicate ) {
	return (Relation)relationHash.get( predicate );
    }
    private Enumeration getPredicates() { return relationHash.keys(); }
    
    // A list of predicates used for the RHS search
    private HashMap featurePredicateHash;
    void setFeaturePredicateHash( HashMap hash ) {
	this.featurePredicateHash = hash;
    }
    FeaturePredicate getFeaturePredicate( String predicateName ) {
	return (FeaturePredicate)featurePredicateHash.get( predicateName );
    }

    // -
    // - Constructor  - - - - - - - - - - - - - - - - - - - - - - - -
    // -
    public FoilData() {
	setFoilDir();    	
    }

    
    /**
     * Creates a new <code>FoilData</code> instance for the target
     * relation "name" with a specified number of "arity" and a list
     * of "predicates" being used in the body
     * 
     * focusOfAttention looks like this:
     * <MAIN::cell|commTable2_C1R2|b
     *  MAIN::cell|commTable2_C1R1|b-1
     *  MAIN::cell|commTable1_C1R1|5 >
     * Where the first element is "input" and the rest of elements are indeed the FoA
     *
     **/
    public FoilData( String name, int arity, 
	    	     Vector /* String */ predicates,
	    	     Vector /* String */ focusOfAttention,
	    	     HashMap featurePredicateHash, String foilLogDir, String maxTuples) {
	
    	
	this();
	// setTargetName( name + "-test" );
	setTargetName( name );
	setTargetRelation( new Relation( getTargetName(), arity, featurePredicateHash ) );
	setFeaturePredicateHash( featurePredicateHash );
	setFoilLogDir( foilLogDir );
	setMaxTuples( maxTuples);
	getTargetRelation().setTargetArgType(focusOfAttention);
	
	// Initialize relations
	for (int i = 0; i < predicates.size(); i++) {
	    // Each "predicate" is in the form of "name(#,#,-)".  See
	    // feature-predicates.txt for more details
	    String predicateName = (String)predicates.get(i);
	    addRelation( predicateName, new Relation( predicateName, featurePredicateHash ) );
	}
    }
    
    // -
    // - Methods  - - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    // A list of output values made by a function call appeared in a 
    // predicate
    Vector /* String */ allValues = new Vector();
    
    /**
     * list of decomposers to apply to initial input 
     */
    private Vector /* of Decomposer*/ decomposers;
    
    /**
     * Read appropriate information off the instruction and accumulate
     * it into this data
     *
     * @param instruction an <code>Instruction</code> value
     **/
    void addInstruction( Instruction instruction ) {

	// TODO
	/*NOTE:
	 * If the focus of attention ordering is not consistent, the automatic reordering does not
	 * reorder negative examples
	 */
	// First, store appropriate information about the target predicate call
	Vector targetArguments = instruction.targetArguments();
	
	getTargetRelation().addPositiveTuple( targetArguments );

	// Mon Apr 11 14:03:49 2005:: We no longer need this, because
	// the tuples for feature predicates can be read from the 
	// cache.  --- THIS DOES NOT HOLD

	// Extract all values appearing in the focus of attentions in
	// the instruction
	Vector newValues=instruction.getValues();

	allValues = addNewElements( allValues, newValues );
	
	//  if decomposition is being done, add the decomposed values to allValues
	if(decomposers!=null)
	    allValues=addNewElements(allValues,SimSt.chainDecomposedValues(newValues,decomposers));

	// Then, store information about other predicate calls
	// You may want to loop this twice to include all output values 
	Enumeration predicates = getPredicates();
	while ( predicates.hasMoreElements() ) {

	    String predicate = (String)predicates.nextElement();
	    Relation relation = getRelation( predicate );
	    
	    // For the relation with an output value, evalRelation adds
	    // the output value to the "values" vector, otherwise, it just evaluate
	    // the predicate and stores the result
	    Vector /* String */ outputValues = relation.evalRelation( allValues );
	    if (outputValues != null) {
		allValues = addNewElements( allValues, outputValues );

		String values = ""; 
		for (int i=0; i < outputValues.size(); i++) {
		    values += (String)outputValues.get(i) + ", ";
		}
	    }
	}
    }
    
    
    /**
     * Read appropriate information off the instruction and accumulate
     * it into this data
     *
     * @param instruction an <code>Instruction</code> value
     **/
    void addNegativeInstruction( Instruction instruction ) 
    {

		// Extract all values appearing in the focus of attentions in
		// the instruction
		Vector newValues=instruction.getValues();
		allValues = addNewElements( allValues, newValues );

		//  if decomposition is being done, add the decomposed values to allValues
		if(decomposers!=null)
		    allValues=addNewElements(allValues,SimSt.chainDecomposedValues(newValues,decomposers));
	
		// Then, store information about other predicate calls
		// You may want to loop this twice to include all output values 
		Enumeration predicates = getPredicates();
		while ( predicates.hasMoreElements() ) {
	
		    String predicate = (String)predicates.nextElement();
		    Relation relation = getRelation( predicate );
		    
		    // For the relation with an output value, evalRelation adds
		    // the output value to the "values" vector, otherwise, it just evaluate
		    // the predicate and stores the result
		    Vector /* String */ outputValues = relation.evalRelation( allValues );
		    if (outputValues != null) {
				allValues = addNewElements( allValues, outputValues );
		
				String values = ""; 
				for (int i=0; i < outputValues.size(); i++) {
				    values += (String)outputValues.get(i) + ", ";
				}
		    }
		}
    }
    
//    private Vector addNewValues( Vector /* String */ vOriginal, Vector /* String */ vNew ) {
//	
//	Vector newValues = new Vector( vOriginal );
//
//	for (int i = 0; i < vNew.size(); i++) {
//	    String val = (String)vNew.get(i);
//	    if (!vOriginal.contains(val)) {
//		newValues.add(val);
//	    }
//	}
//	
//	return newValues;
//    }
    
    
    /**
     * number of FoA in instruction.
     * number of tuple elements in the FoilData
     */
    public int getFoilArity(){
       return getTargetRelation().getArity();
    }

    void signalTargetNegative( Instruction instruction ) {

        // Number of focus of attention, by definition, includes
        // "Selection", hence subtract 1 to make the comparison fair
        // (the positive tuple only holds independent WME values)
        int instArity = instruction.numFocusOfAttention() -1;
        int theArity = getTargetRelation().getArity();

        //instArity >= foilDataArity

        /*NOTE:
         * If the focus of attention ordering is not consistent, the automatic reordering does not
         * reorder negative examples
         */

        if ( theArity == instArity ){
            Vector targetArguments = instruction.targetArguments();
            getTargetRelation().addNegativeTuple( targetArguments );
        }
        else {
            new Exception("Error: instruction's arity != foilData's arity").printStackTrace();
        }

    }
    
    void signalTargetExplicitNegative( Instruction instruction ) {

        // Number of focus of attention, by definition, includes
        // "Selection", hence subtract 1 to make the comparison fair
        // (the positive tuple only holds independent WME values)
        int instArity = instruction.numFocusOfAttention() -1;
        int theArity = getTargetRelation().getArity();

        //instArity >= foilDataArity

        /*NOTE:
         * If the focus of attention ordering is not consistent, the automatic reordering does not
         * reorder negative examples
         */

        if ( theArity == instArity ){
            Vector targetArguments = instruction.targetArguments();
            getTargetRelation().addExplicitNegativeTuple( targetArguments );
        }
        else {
           // new Exception("Error: instruction's arity != foilData's arity").printStackTrace();
        }

    }

//    private List makePossibleFoasList(Enumeration en, Instruction instruction) {
//        List l = new Vector();
//        while(en.hasMoreElements()){ //for each possible FOA
//            Object[] wmeArray = (Object[])en.nextElement();            
//            Vector wmeV = arrayToVector( wmeArray );
//            
//            clearCurrentFoA();
//            
//            //for each element of wmeV, i.e., each WME, make it use the correct seed format, given by foaString()
//            for (int i=0; i<wmeV.size(); i++){
//                String selection = (String) wmeV.get(i);
//                String wmeType = getRete().wmeType( selection );
//
//                //all for the purpose of getting foaString()
//                TableExpressionCell cell = (TableExpressionCell)getBrController().lookupWidgetByName( selection );
//                FoA foa = new FoA(cell);
//                addFoA(foa);
//                String foaStr = ((FoA)getCurrentFoA().get(i)).foaString();
//
//                //fix it
//                wmeV.set(i, foaStr);
//                
//                // idea: look it up from WMEs
////                Fact wmeFact = rete.getFactByName(selection); //gets the WME fact for the given selection
////                try {
////                    Value inputValue = wmeFact.getSlotValue("value");
////                    String input = SimSt.stripQuotes(inputValue.toString());
////                }
//
//            
//            }
//
//            String instrSelection = instruction.getSelection().split("\\|")[1];
//            
//            //all for the purpose of getting foaString()
//            TableExpressionCell inputCell = (TableExpressionCell)getBrController().lookupWidgetByName( instrSelection );
//            FoA inputFoa = new FoA(inputCell);
//            addFoA(inputFoa);
//            String inputFoaStr = ((FoA)getCurrentFoA().get(this.currentFoA.size()-1)).foaString();
//
//            //there should be a function to return inputFoaStr, given selection
//            wmeV.add(0, inputFoaStr); //previously "stub"
//
//
//            
//            l.add(wmeV);
//        }
//        return l;
//    }
    
    
    /** Gustavo: 26 Oct 2007. This was copied from RhsSearchSuccessorFn (via SimSt).
     * 
     * @param array
     * @return
     */
    private Vector arrayToVector( Object[] array ) {

        Vector v = new Vector();
        for (int i = 0; i < array.length; i++) {
            v.add( array[i] );
        }
        return v;
    }


    /**
     * turn an Enumeration into a List<List>
     */
    private List makeList(Enumeration en) {
        List l = new Vector();

        while(en.hasMoreElements()){ //for each possible FOA
            Object[] wmeArray = (Object[])en.nextElement();            
            Vector wmeV = arrayToVector( wmeArray );
        }
        return l;
    }
    
    public void printInfo() {
    	
    	if(trace.getDebugCode("miss")) trace.out("miss",this.FOIL_EXE);
    	if(trace.getDebugCode("miss")) trace.out("miss",this.getFoilLogDir());
    }
    
    /**
     * Invoke FOIL and return a vector of vector of features, a
     * predicate symbols appering in the horn clauses found by FOIL.
     * By calling, runFoil(), this method also creates a FOIL input file.
     * 
     * @return a <code>Vector</code> value
     **/
    Vector /* Vector of Vector of String */ searchFeatures(String numRules, String numSteps) {
        Vector /* Vector of Vector of String */ features = null;

	// Number of tuples
	// int numT = getTargetRelation().numAllTuples();
	String numP = "00" + getTargetRelation().numPosTuples();
	numP = numP.substring( Math.min( numP.length() - 2, 2 ) );
	String numN = "00" + getTargetRelation().numNegTuples();
	numN = numN.substring( Math.min( numN.length() - 2, 2 ) );
	String id = "R" + numRules + "S" + numSteps + "-" + "P" + numP + "N" + numN; 

	// File name for the FOIL data to be stored
	String dataFileName = getFoilLogDir();
	dataFileName += getTargetName() + "-" + id + ".d";

	// File name for FOIL log to be saved
	String foilLogFileName = getFoilLogDir();
	foilLogFileName += getTargetName() + "-" + id + "-log.txt";

	// File name for a DOS batch command file
	// String dosBATfile = TMP_DIR;
	// dosBATfile += "foil-" + getTargetName() + "." + numT + ".bat";

	// Invoke FOIL as an external process. 
	runFoil( dataFileName, foilLogFileName, true );

	/* Run Foil using a dll instead of as an external process. To revert to using a foil.exe
	 * uncomment runFoil( dataFileName, foilLogFileName, true ) and comment out the function
	 * 	runFoilDLL (dataFileName, foilLogFileName, true).
	 */
	//runFoilDLL (dataFileName, foilLogFileName, true);
	
	// Check if the application is running locally or via webstart
	

	
	if(!FoilData.WEBSTARTMODE) {
       	try
        {
            dataFileName = (new File(dataFileName)).getCanonicalPath();
            foilLogFileName = (new File(foilLogFileName)).getCanonicalPath();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    } else { // Running via webstart

	    try {
			foilLogFileName = new File(foilLogFileName ).getCanonicalPath();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
   
	  /* if (FoilData.WEBAUTHORINGMODE){
			dataFileName=WEB_AUTHORING_TRAINING_DIRECTORY+dataFileName;
			foilLogFileName=WEB_AUTHORING_TRAINING_DIRECTORY+foilLogFileName;
		}
	 */
	 
	// Read the result
	try {
	    FileReader logReader = new FileReader( foilLogFileName );
	    BufferedReader logFile = new BufferedReader( logReader );
	    String logLine = "";
	    while ( (logLine = logFile.readLine()) != null ) {
		
		if ( logLine.matches( FOIL_TARGET_LINE ) ) {
		    // Sat Oct 28 21:44:06 LDT 2006 :: Noboru
		    // feature should be null then FOIL generated a recursive hypothesis
		    Vector /* String */ feature = extractFeaturesFromLogLine( logLine );
		    if (feature != null) {
			// Accumulate features
			if ( features == null ) features = new Vector();
			features.add( feature );
		    } else {
			// When FOIL generated a recursive hypothesis, just  
			// discard them (can't stop FOIL doing recursion!)
			features = null;
			break;
		    }
		}
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
	return features;
    }

	
    // Return a list of feature tests extraceted from a FOIL log like
    // "Clause 0: div-rhs(A,B,C) :- Coefficient(A,D), Coefficient(C,E), A<>C."
    private Vector /* String */ extractFeaturesFromLogLine( String logLine ) {

	Vector features = new Vector();
	
	// Get the head of the Prolog clause output by FOIL
	String headString = logLine.split("\\(")[0].split(": ")[1];
	// Get the "body" of the Prolog clause output by FOIL 
	String featureString = logLine.split(":- ")[1].replaceAll(".$","");
			

	// Sat Oct 28 21:44:06 LDT 2006 :: Noboru
	// If recursive, return null immediately
	if (featureString.indexOf(headString) >= 0) {
		if(trace.getDebugCode("miss"))trace.out("miss", "FOIL: recursive call detected >>>>>>>>>>>>>>>> discarded...");
	    return null;
	}
	
	// Futther split the body into individual goals
	String[] featurePredicate = featureString.split(", ");
	
	for (int i = 0; i < featurePredicate.length; i++) {
	    String predicate = featurePredicate[i];
	    String jessName = predicateJessName( predicate );
	    features.add( jessName );
	}
	
	return features;
    }

    // Given a predicate symbols used in FOIL (e.g., "not(Homogeneous(B))" 
    // or "IsConstant(A)"), returns
    // a Jess function name defined in a corresponding
    // FeaturePredicate class
    private String predicateJessName( String predicateSymbol ) {

	return isArithmeticOp( predicateSymbol ) ?
	    predicateJessNameArithmeticOp( predicateSymbol ) :
	    predicateJessNameUserDefined( predicateSymbol );
    }

    private boolean isArithmeticOp( String predicateName ) {
	return ( predicateName.indexOf( "=" ) != -1 ) ||
	    ( predicateName.indexOf( "<>" ) != -1 );
    }

    private String predicateJessNameArithmeticOp( String predicateName ) {

	String jessName = "";
	String closeP = "";
	if ( predicateName.indexOf( "=" ) != -1 ) {
	    jessName += "(eq ";
	    closeP = ")";
	} else if ( predicateName.indexOf( "<>" ) != -1 ) {
	    jessName += "(not (eq ";
	    closeP = "))";
	}

	jessName += "?" + predicateName.charAt(0) + " ";
	jessName += "?" + predicateName.charAt(predicateName.length()-1) + " ";
	jessName += closeP;

	return jessName;
    }
    
    // Predicate symbol -> "not(Homogeneous(B))" or "IsConstant(A)", etc...
    private String predicateJessNameUserDefined( String predicateSymbol ) {
	    
	boolean isNegative = predicateSymbol.startsWith("not(");

	// strip off "not(" and ")" 
	if ( isNegative ) {
	    predicateSymbol = predicateSymbol.replaceAll("not\\(","");
	    predicateSymbol = predicateSymbol.replaceAll("\\)\\)","\\)");
	}

	String predicateHead = predicateSymbol.split("\\(")[0];
	String predicateArgs = predicateSymbol.replaceAll( predicateHead, "" );
	
	Relation relation = null;
	Enumeration allPredicates = getPredicates();
	while ( allPredicates.hasMoreElements() ) {
	    String predicate = (String)allPredicates.nextElement();
	   
	    
	     //String predicateName = predicate.split("\\.")[6]; // edu.cmu.pact.miss.userDef.algebra.FeaturePredicate(#) Doing exact comparison now instead of wildCard comparison
		 // 08/03/2014 nbarba: old predicateName was hardcoded to be extracted from edu.cmu.pact... 
		 // no it automatically detects number of . and takes the string after last . 
		 int count = predicate.length() - predicate.replace(".", "").length();
		 String	predicateName=predicate.split("\\.")[count]; // edu.cmu.pact
	    
	    
	    int parenthesesIndex = predicateName.indexOf('(');
	    predicateName = predicateName.substring(0, parenthesesIndex);
	    if ( predicateName.matches( predicateHead ) ) {
		    relation = getRelation( predicate );
		    break;
	    }
	}

	String args = "";
	String[] argV = predicateArgs.split("\\p{Punct}");
	for (int i = 1; i < argV.length; i++) {
	    args += "?" + argV[i] + " ";
	}

	String jessName = isNegative ? "(not (" : "(";
	jessName += relation.getPredicate().getName() + " ";
	jessName += args;
	jessName += isNegative ? "))" : ")";
	
	return jessName;
    }

    /**
     * Invoke FOIL upon this data set
     *
     */
    int procNum = 0;
    void runFoil() {

	/*
	// Identifier 
	String ID = "." + procNum++;
	// Number of tuples
	int numT = getTargetRelation().numAllTuples();
	*/

	// Number of tuples
	String numP = "00" + getTargetRelation().numPosTuples();
	numP = numP.substring( Math.min( numP.length() - 2, 2 ) );
	String numN = "00" + getTargetRelation().numNegTuples();
	numN = numN.substring( Math.min( numN.length() - 2, 2 ) );
	String id = numP + "-" + numN; 

	// File name for the FOIL data to be stored
	String dataFileName = getFoilLogDir();
	dataFileName += getTargetName() + "-" + id + ".d";

	// File name for FOIL log to be saved
	String foilLogFileName = getFoilLogDir();
	foilLogFileName += getTargetName() + "-" + id + "-log.txt";

	// File name for a DOS batch command file
	// String dosBATfile = TMP_DIR;
	// dosBATfile += "foil-" + getTargetName() + "." + numT + ID + ".bat";

	runFoil( dataFileName, foilLogFileName, true );
    }

    private final static String FOIL_EXECUTABLE = "foil6";
    // Make a FOIL data file, DOS batch command to run FOIL, and run
    // the bach file
    public void runFoil( String dataFileName, String foilLogFileName, boolean createDataFile ) {
        
    // Check if FOIL is running locally or via web-start
    if(!FoilData.WEBSTARTMODE) {
    	try {
	    dataFileName = new File( dataFileName ).getCanonicalPath();
	    foilLogFileName = new File( foilLogFileName ).getCanonicalPath();
	} catch (IOException e) {
	    e.printStackTrace();
	}
 	
   /* if (FoilData.WEBAUTHORINGMODE){
    		dataFileName=WEB_AUTHORING_TRAINING_DIRECTORY+dataFileName;
    		foilLogFileName=WEB_AUTHORING_TRAINING_DIRECTORY+foilLogFileName;
    }
    */
    
    if(createDataFile){
	    // Make a FOIL data file
	    try {

	        File parentDir = new File(dataFileName).getParentFile();
	        if (!parentDir.exists()) {
	            parentDir.mkdirs();
	        }

	        FileOutputStream out = new FileOutputStream( dataFileName );
	        PrintWriter writer = new PrintWriter( out );
	        writer.println( this );
	        writer.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	}
	// Clean up old the log file
	File logFile = new File( foilLogFileName );
	logFile.delete();

	// Execute FOIL program
	Process foilProcess = null;
	try {
	    // Sat Jun 04 11:36:53 2005: FOIL_EXE may contain a white
	    // space, hence need to get quated
	    // String[] foilName = { FOIL_EXE };
	    // 
	    // Sun Oct 22 23:04:10 LDT 2006 :: Noboru
	    // Have FOIL not produce hypothesis with the best guess.  In other words, 
	    // prevent FOIL from generating hypothesis that violates any of negative 
	    // tuples.
		String[] foilName;
	    if (getMaxTuples()==null || getMaxTuples().equals(USE_DEFAULT_TUPLES)){
	    	foilName = new String[2];
	    	foilName[0]=FOIL_EXE; 
	    	foilName[1]="-a100";
	    }    
	    else {
	    	foilName = new String[3];
	    	foilName[0]=FOIL_EXE; 
	    	foilName[1]="-a100"; 
	    	foilName[2]="-m " + getMaxTuples(); 	
	    }
	    foilProcess = Runtime.getRuntime().exec( foilName );
	} catch (IOException e) {
	    e.printStackTrace();
	}

	// Open redirect I/O files 
	FileOutputStream fos = null;
	FileInputStream fis = null;
	StreamGobbler outputGobbler = null;
	StreamGobbler inputGobbler = null;
	try {
	    fos = new FileOutputStream( foilLogFileName );
	    outputGobbler = 
		new StreamGobbler( foilProcess.getInputStream(), fos, true );
	
	    fis = new FileInputStream( dataFileName );
	    inputGobbler =
		new StreamGobbler( fis, foilProcess.getOutputStream() );
	
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}

	// Wait until FOIL terminates.  Print a dot while the execution.
	inputGobbler.start();
	outputGobbler.start();
	// outputGobbler.waitForComplition();
	try {
	    outputGobbler.join();
	} catch (InterruptedException e1) {
	    e1.printStackTrace();
	}
	
	// close files
	try {
	    fos.close();
	    fis.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	} else {
		// Running foil using webstart 
		
		try {
		    dataFileName = new File(dataFileName).getCanonicalPath();
		    foilLogFileName = new File(foilLogFileName).getCanonicalPath();
		} catch (IOException e) {
		    e.printStackTrace();
		}

		File parentDir = null;

		if(createDataFile){
		    // Make a FOIL data file
		    try {
		        parentDir = new File(dataFileName).getParentFile();
		        if (!parentDir.exists()) {
		            parentDir.mkdirs();
		        }

		        FileOutputStream out = new FileOutputStream( dataFileName );
		        PrintWriter writer = new PrintWriter( out );
		        writer.println( this );
		        writer.close();
		    } catch (FileNotFoundException e) {
		        e.printStackTrace();
		    }
		}
		// Clean up old the log file
		File logFiles = new File( foilLogFileName );
		logFiles.delete();
		
		Process foilProcess = null;
		try {
		    // Sat Jun 04 11:36:53 2005: FOIL_EXE may contain a white
		    // space, hence need to get quated
		    // String[] foilName = { FOIL_EXE };
		    // 
		    // Sun Oct 22 23:04:10 LDT 2006 :: Noboru
		    // Have FOIL not produce hypothesis with the best guess.  In other words, 
		    // prevent FOIL from generating hypothesis that violates any of negative 
		    // tuples.
		    String[] foilName = { WebStartFileDownloader.SimStWebStartDir +  FOIL_EXECUTABLE, "-a100" };
	 	    if(trace.getDebugCode("miss"))trace.out("WebStartMode: " + FoilData.WEBSTARTMODE + "    " + foilName[0] +  "    " + getFoilLogDir());
	 	    foilProcess = Runtime.getRuntime().exec( foilName );
	 	    
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		FileOutputStream fos = null;
		FileInputStream fis = null;
		StreamGobbler outputGobbler = null;
		StreamGobbler inputGobbler = null;
		try {
		    fos = new FileOutputStream( foilLogFileName );
		    outputGobbler = 
			new StreamGobbler( foilProcess.getInputStream(), fos, true );
		
		    fis = new FileInputStream( dataFileName );
		    inputGobbler =
			new StreamGobbler( fis, foilProcess.getOutputStream() );
		
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		}

		// Wait until FOIL terminates.  Print a dot while the execution.
		inputGobbler.start();
		outputGobbler.start();
		// outputGobbler.waitForComplition();
		try {
		    outputGobbler.join();
		} catch (InterruptedException e1) {
		    e1.printStackTrace();
		}
		
		// close files
		try {
		    fos.close();
		    fis.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
    }

    // Make a FOIL data file, DOS batch command to run FOIL, and run
    // the bach file
    private synchronized void runFoilDos( String dataFileName,
					  String foilLogFileName,
					  String dosBATfile ) {

    if(trace.getDebugCode("miss"))trace.out("miss", "= = = = = = = = = = = = = = = = = = = = =");
    if(trace.getDebugCode("miss"))trace.out("miss", "Running FOIL on " + getTargetName() );
    if(trace.getDebugCode("miss"))trace.out("miss", "dataFileName: " + dataFileName);
    if(trace.getDebugCode("miss"))trace.out("miss", "foilLogFileName: " + foilLogFileName);
    if(trace.getDebugCode("miss"))trace.out("miss", "- - - - - - - - - - - - - - - - - - - - -");

	// Make a FOIL data file
	try {
	    FileOutputStream out = new FileOutputStream( dataFileName );
	    PrintWriter writer = new PrintWriter( out );
	    writer.println( this );
	    writer.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}

	// Make a DOS batch file to run FOIL as the data file being an
	// input file and foilLogFileName a logging file
	try {
	    FileOutputStream out = new FileOutputStream( dosBATfile );
	    PrintWriter writer = new PrintWriter( out );
	    String cmd =
		FOIL_EXE + " < " + dataFileName + " > " + foilLogFileName;

	    writer.println( cmd );
	    writer.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}

	File logFile = new File( foilLogFileName );
	// Clean up old the log file
	logFile.delete();

	// Execute the batch file
	Process foilProcess = null;
	try {
	    foilProcess = Runtime.getRuntime().exec( dosBATfile );
	} catch (IOException e) {
	    e.printStackTrace();
	}
	// Wait until FOIL terminates.  Print a dot while the execution.
	try {
	    wait();
	} catch (InterruptedException  e) {
	    e.printStackTrace();
	}
	/*
	int n = 0;
	while ( true ) {
	    try {

		int processTerminated = foilProcess.exitValue();
		break;

	    } catch (IllegalThreadStateException e) {
		if ( n++ == 500 ) {
		    System.out.print(".");
		    n = 0;
		}
		try {
		    Thread.sleep(100);
		} catch (InterruptedException ei) {
		    ei.printStackTrace();
		}
	    }
	}
	*/
    }

    /**
     * Return the content of a FOIL input data file 
     *
     * @return a <code>String</code> value
     */
    public String toString() {
        
    
	// Keys of the typeHash are types appearing in this FoilData.
	// The value of each key is a Vector of constant (String)
	// appearing in the instructions.
	Hashtable typeHash = new Hashtable();

	// Target Predicate
	String dataStr = "";
	dataStr += getTargetRelation().toString() + "\n";
	// Type of the arguments appeared in the targetRelation
	for (int i = 0; i < getTargetRelation().getArity(); i++) {
	    int argType = getTargetRelation().getTargetArgType(i);
	    String typeStr = TYPE_NAME + argType;
	    Vector /* String */ types = (Vector)typeHash.get(typeStr);
	    Vector /* String */ addTypes = getTargetRelation().getTypesFor(i);
	    if (types != null) {
		types = addNewElements(types, addTypes);
	    } else {
		types = addTypes;
	    }
	    typeHash.put( typeStr, types );
	}
	
	// Relations
	Enumeration predicates = getPredicates();
	while ( predicates.hasMoreElements() ) {
			
	    // predicateName -> "edu.cmu.pact.miss.userDef.Monomial(#)"
	    String predicateName = (String)predicates.nextElement();
	    Relation relation = getRelation( predicateName );
	    if ( relation != null ) {

		int index = predicateName.indexOf('(');
		predicateName = predicateName.substring( 0, index );
		FeaturePredicate fp = getFeaturePredicate( predicateName );
		//trace.out("checking for predicateName " + predicateName);
		// Given a feature predicate, extract tuple from its cache for
		// apply(), and set up tuples		
		relation.setTuple( fp );
		
		if ( relation.hasTuples() ) {
			//trace.out("has positive tupples");
		    String tmpStr=relation.toString();
		    if ( tmpStr != null ) {
			dataStr += tmpStr + "\n";
		    }
		    for (int i = 0; i < relation.getArity(); i++) {
			int argType = relation.getArgType(i);
			String typeStr = TYPE_NAME + argType;
			Vector /* String */ types = (Vector)typeHash.get(typeStr);
			Vector /* String */ addTypes = relation.getTypesFor(i);
			if (types != null) {
			    types = addNewElements(types, addTypes);
			} else {
			    types = addTypes;
			}

			typeHash.put( typeStr, types );
		    }
		}
	    }
	}

	// Types
	String typeStr = toStringType(typeHash);
	return typeStr + dataStr;
    }
    
    private Vector addNewElements(Vector v1, Vector v2) {
	Vector addAll = new Vector(v1);
	for (int i = 0; i < v2.size(); i++) {
	    if (!v1.contains(v2.get(i))) {
		addAll.add(v2.get(i));
	    }
	}
	return addAll;
    }
    
    // Traverse the typeHash, which contains all types and valused, and 
    // returns a list of <type>: {<value>}*
    //     Returns something like
    //     "P0: 9, 3x, x.\nT1: 9.\nT0: 3x."
    private String toStringType(Hashtable typeHash) {

	String typeStr = "";

	Enumeration typeElements = typeHash.keys();
	while ( typeElements.hasMoreElements() ) {

	    String theType = (String)typeElements.nextElement();
	    Vector theConstants = (Vector)typeHash.get( theType );
	    theType += ": ";
	    for (int i = 0; i < theConstants.size(); i++) {
		String val = (String)theConstants.get(i);
		// add "val == null" in the test, if you wish to include "null" 
		// in the type list
		//if (validArgs.contains(val)) {
		    theType += val + ", ";
		//}
	    }
	    theType = theType.substring(0, theType.length()-2);
	    theType += ".\n";
	    typeStr += theType;
	}
	typeStr = escapeECs( typeStr ) + "\n";

	// String va = v2string( validArgs );
	// return va + "\n\n" + typeStr + dataStr;
	return typeStr;
    }

    String v2string( Vector v ) {
	if (v == null) return null;
	String va = "";
	for (int i = 0; i < v.size(); i++) {
	    va += (String)v.get(i) + " ";
	}
	return va;
    }
    
    /**
     * Given an initial set of types (arguments to the predicates), which is generated
     * from the target predicate and a set of all types observed, collect only those types
     * that contributes the target predicate.  The valid arguments are either appering in 
     * the target predicate or an output of a predicate with valid arguments as its 
     * input.
     * 
     * @param initialArgs
     * @param typeHash
     * @return 
     */
    private Vector getValidArgs(Vector initialArgs, Vector /* Relation */ relations ) {
	
	Vector validArgs = new Vector( initialArgs );
	
	boolean isIntact = false;
	while (!isIntact) {
	    isIntact = true;
	    for (int i = 0; i < relations.size(); i++) {

		Relation relation = (Relation)relations.get(i);

		String key = relation.getKey();
		// Number of input arguments
		int arity = key.indexOf('-');
		if ( arity < 0 ) arity = key.length();

		Vector /* V String */ tuples = relation.getPositiveTuples();
		for (int j = 0; j < tuples.size(); j++) {
		    Vector /* String */ types = (Vector)tuples.get(j);
		   if (isValidArgs( types, arity, validArgs )) { 
			for (int k = 0; k < types.size(); k++) {
			    String arg = (String)types.get(k);
			    if (!validArgs.contains(arg)) {
				validArgs.add(arg);
				isIntact = false;
			    }
			}
		    }
		}
	    }
	}
	return validArgs;
    }
    
    /**
     * Return true if the first <arity> arguments in <args> are all in 
     * the <validArgs>
     *   
     * @param args
     * @param arity
     * @param validArgs
     * @return
     */
    private boolean isValidArgs(Vector args, int arity, Vector validArgs) {
	boolean isValidArgs = true;
	for (int i = 0; i < arity; i++) {
	    if (!validArgs.contains((String)args.get(i))) {
		isValidArgs = false;
		break;
	    }
	}
	
	return isValidArgs;
    }
    
    static public String replaceCommas(String value){
    	
    	if (!value.contains(",")) return value;
    	
    	String resultString="";
     	 int mode=0;
    	 String tmp="";
     	 char[] chars = value.toCharArray();
     	 for (int i = 0, n = chars.length; i < n; i++) {
     	   char c = chars[i];
     	   tmp+=c;
     	   if (c=='[' && mode==0){
     		   mode=1; 
     	   }
     	   if (mode==1 && c==','){
     		   tmp="\\,";
     	   }
     	   if (mode==1 && c==']')
     		   mode=0;
     	 	   
     	   resultString+=tmp;
     	   tmp="";
     	  
     	 }
     
     	return resultString;
       }
    
    
    /**
     * Put an escape character '/' in from of a character that must be
     * escaped (e.d., a parenthesis)
     *
     * @param nativeStr a <code>String</code> value
     * @return a <code>String</code> value
     **/
    private String escapeECs( String nativeStr ) {

	// Escape open parenthesis
	String openP = nativeStr.replaceAll( "\\(", "\\\\(" );
	// Escape close parenthesis
	String closeP = openP.replaceAll( "\\)", "\\\\)" );
	
	String comma=replaceCommas(closeP);
	
	return comma;
    }
	/**
	 * @return the list of decomposers this FoilData is using to decompose input
	 */
    public Vector getDecomposers() {
		return decomposers;
	}
	/**
	 * 
	 * @param decomposers a Vector of chunkers to apply to this foil data's initial input 
	 */
    public void setDecomposers(Vector decomposers) {
		this.decomposers = decomposers;
	}

    // Redirect input and output for an external process
    // http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
    // 
    class StreamGobbler extends Thread {

    	InputStream is;
    	String type;
    	OutputStream os;

    	boolean notifyWhenDone = false;
    	boolean completed = false;

    	StreamGobbler(InputStream is, OutputStream redirect ) {
    		this(is, redirect, false );
    	}

    	StreamGobbler(InputStream is, OutputStream redirect,
    			boolean notifyWhenDone ) {
    		this.is = is;
    		this.os = redirect;
    		this.notifyWhenDone = notifyWhenDone;
    	}
    	
    	StreamGobbler(InputStream is, String type){
    		
    	    this.is = is;
    	    this.type = type;
       }
    	   

    	public void waitForComplition() {
    	    while ( !completed ) {
    	        System.out.print(".");
    	    }
   	}

    	public void run() {

    		try {

    			PrintWriter pw = null;
    			if (os != null)
    				pw = new PrintWriter(os);

    			InputStreamReader isr = new InputStreamReader(is);
    			BufferedReader br = new BufferedReader(isr);
    			
    			String line=null;
    			while ( (line = br.readLine()) != null) {

    				line += "\n";
    				if (pw != null) {
    					pw.print(line);
    					pw.flush();
    				}
    			}

    			if (pw != null) {
    				pw.flush();
    				pw.close();
    			}

    			completed = true;

    		} catch (IOException ioe) {
    			ioe.printStackTrace();  
    		}
    	}
    }
}


//
// end of f:/Project/CTAT/ML/ISS/miss/FoilData.java
// 
