/**
 * f:/Project/CTAT/ML/ISS/miss/Rule.java
 *
 *
 * Created: Fri Dec 31 21:47:01 2004
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss;

import java.awt.Point;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.SimStRete;

public class Rule implements Serializable{

    // - pali apo edw kai pera
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - 
    // -

    // The name of a production rule for "DONE" state
    public static final String DONE_NAME = "DONE";
    private static final String DONE_SELECTION = "done";
    private static final String DONE_ACTION = "ButtonPressed";
    private static final String DONE_INPUT = "-1";
    boolean isDoneRule() {
        return getName().toUpperCase().equals( Rule.DONE_NAME );
    }
    
    // A default skill name used in the CTAT
    public final static String NONAME = "unnamed";
    
    // A default skill name for unlabled learning
    public final static String UNLABELED_SKILL = "unlabeled-skill";

    // The prefix used for the negation of a predicate
    public final static String NOT_STRING="(not (";
    
    // The left-arrow showing a variable-value assignment for WMW retrieval
    private String WME_PATH_LEFT_ARROW = "<-";

    /**
     * the number of focuses of attention used by this rule(not including the input)
     */
    private int numFoA;
    public int getNumFoA() { return numFoA; }
    public void setNumFoA(int numFoa) { this.numFoA = numFoa; }
    
    // The name of this production rule
    private String name;
    String getName() { return name; }
    private void setName( String name ) { this.name = name; }

    private String selection;
    private String getSelection() { return selection; }
    private void setSelection( String selection ) {
	this.selection = selection;
    }

    private String action;
    private String getAction() { return action; }
    private void setAction( String action ) { this.action = action; }

    private String input;
    private String getInput() { return input; }
    private void setInput( String input ) { this.input = input; }
    private int getInputVal() { return Integer.parseInt( this.input ); }
    
    
    private int uses = 0;
    public int getUses() { return uses; }
    private int acceptedUses = 0;
    public int getAcceptedUses() { return acceptedUses; }
    public double getAcceptedRatio() {
    	if(uses == 0) return 0;
    	return ((double) acceptedUses)/uses; }
   
    public static int count = 0;
    public int identity;
    
    /*
	 The overall success ratio 'r' is computed per individual production separately with the formula: r=b/a,
	 where
	 	a: Number of times the production appeared in an agenda
		b: Number of times the production was actually used
	Important note:
		- The first ratio (b/a), is also not ideal, as it shows not the "success" ratio of the production,  but rather the "pick-up" ratio. One idea to fix this is to introduce a new variable 'd' which is the number of times this production was picked-up AND produced a correct input, 
		then the first fraction of overall success should be d/c (i.e a true "success" ratio). 
     */
   
        
    public void addAcceptedUse(/*String selection*/ Vector foas)
    {
  
    	uses++;
    	acceptedUses++;
    	String foaString = "";
    	for(int i=0;i<foas.size();i++)
    		foaString += foas.get(i);
    	Point pair = selectionUses.get(foaString);
    	if(pair == null)
    		pair = new Point(1,1);
    	else
    	{
    		pair.x = pair.x + 1;
    		pair.y = pair.y + 1;
    	}
    	selectionUses.put(foaString, pair);
    }

    public void addRejectedUse(/*String selection*/ Vector foas)
    {
    	uses++;
    	String foaString = "";
    	for(int i=0;i<foas.size();i++)
    		foaString += foas.get(i);
    	Point pair = selectionUses.get(foaString);
    	if(pair == null)
    		pair = new Point(0,1);
    	else
    	{
    		pair.y = pair.y + 1;
    	}
    	selectionUses.put(foaString, pair);
        
    }

    private ArrayList /* String */ rhsOp = new ArrayList();
    public ArrayList /* String */ getRhsOp() { return this.rhsOp; }
    private void addRhsOp( String op ) { this.rhsOp.add( op ); }

    private ArrayList lhsPath = new ArrayList();
    ArrayList getLhsPath() { return this.lhsPath; }
    private void addLhsPath( String lhsPath ) { 
    	lhsPath=lhsPath.replace("(value )", "(value nil)");
    	this.lhsPath.add( lhsPath ); }

    private ArrayList lhsTopologicalConsts = new ArrayList();
    ArrayList getLhsTopologicalConsts() {
	return this.lhsTopologicalConsts;
    }
    private void addLhsTopologicalConsts( String constraint ) {
	this.lhsTopologicalConsts.add( constraint );
    }
    private void setLhsTopologicalConsts( String constraints ) {

	StringTokenizer tokenizer = new StringTokenizer( constraints, "|" );
	while ( tokenizer.hasMoreTokens() ) {
	    addLhsTopologicalConsts( tokenizer.nextToken() );
	}
    }

    private ArrayList /* String[] */ lhsFeatures = new ArrayList();
    private void resetLhsFeatures() { lhsFeatures = new ArrayList(); }
    ArrayList /* String[] */ getLhsFeatures() {
	return this.lhsFeatures;
    }
    private void addLhsFeatures( String[] lhsFeatures ) {
	this.lhsFeatures.add( lhsFeatures );
    }
    // Called by SimSt.generateRules() after FOIL found feature
    // predicates
    void setFeatures( Vector /* Vector of Vector of String */ features ) {
	    resetLhsFeatures();
	for (int i = 0; i < features.size(); i++) {
	    Vector /* String */ feature = (Vector)features.get(i);
	    String[] featureSyms = new String[ feature.size() ];
	    for (int j = 0; j < feature.size(); j++) {
		featureSyms[j] = (String)feature.get(j);
	    }
	    addLhsFeatures( featureSyms );
	}
    }

    
    //Store individual selection uses as a point x = number of times accepted,
    //y = number of times used total
    private Hashtable<String,Point> selectionUses = new Hashtable<String,Point>();
    public double getSelectionAcceptRatio(Vector foas)
    {
    	if(foas == null)  return 0.5;
    	
    	String foaString = "";
    	for(int i=0;i<foas.size();i++)
    		foaString += foas.get(i);
    	Point pair = selectionUses.get(foaString);
    	if(pair == null) return 0.5;  //Haven't seen for this foa before, just as likely as not
    	return ((double) pair.getX())/pair.getY();
    }
    
    
    // = = = = = = = = = = = = = = = = = = = = = = = = =
    // Rule Synthesis
    //
   
    private final String SPECIAL_TUTOR_FACT = "?special-tutor-fact-correct";
    private final String STF_WME = "(special-tutor-fact-correct)";

    // A list of slot names that should be excluded in searching for
    // WMEs in Jess production rule
//    private final String SLOTS_EXCLUDE = "\\(description .+?\\)|\\(done .+?\\)|\\(subgoals .*?\\)|\\(turn .*?\\)|\\(name .+?\\)|\\(position .+?\\)|\\(row-number .+?\\)|\\(column-number .+?\\)";
    private final String SLOTS_EXCLUDE = "\\(description .+?\\)|\\(done .+?\\)|\\(subgoals .*?\\)|\\(turn .*?\\)|\\(position .+?\\)|\\(row-number .+?\\)|\\(column-number .+?\\)|\\(table-number .+?\\)";

    private final String HINT_MSG =
	"(hint-message (construct-message [ enter ?input ]))";

    private final String DONE_HINT_MSG =
	"(hint-message (construct-message [ press DONE button ]))";

    private final String SELECTION = "?selection";
    private final String INPUTVAR = "?input";

    // A common preamble that must be inserted at the begining of the
    // file
    static String format = "MMMMMMMMM dd, yyyy KK:mm:ss a";
    static SimpleDateFormat dateFormat =
	new SimpleDateFormat( format, Locale.US );
    static String dateCreated = dateFormat.format( new Date() );
    public final static String RULE_PREAMBLE_1 = 
";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n" + 
";; Jess Production Rules for a Cognitive Tutor\n" + 
";;\n" + 
";; This file was automatically generated by a Simulated Student.\n" + 
";; Date of creation: " + dateCreated + "\n" + 
";;\n" + 
";; PSLC/CTAT Simulated Student Project, 2005, 2006\n" + 
";; Carnegie Mellon University\n" +
";;\n" +
"\n" +
";; Removing a following line will jeopardize the tutor's model tracing\n" +
";; capability\n" +
";; \n" +
";; You may need to add similar statements if you wish to add your own\n" +
";; predicates and operator symbols.  See\n" +
";; http://herzberg.ca.sandia.gov/jess/docs/70/extending.html for details\n" +
";; \n";

    /**
     * String used to prefix bindings of FOIL output variables
     */
    private static final String OUTPUT_PREFIX = "?output";
    /**
     * a list of features(feature predicate objects) that need to be patterned match as WME facts rather than test patterns
     */
    protected  Vector/* of FeaturePredicate*/ factFeatures;

    // -
    // - Constructor - - - - - - - - - - - - - - - - - - - -
    // -

    /**
     * Creates a new <code>Rule</code> instance.
     *
     */
    public Rule( String name, List lhs, List rhsOp ) {

	this( name, new Vector(lhs), null, null, new Vector(rhsOp), "" );
    }

    /**
     * @param name
     * @param lhsPath
     * @param lhsFeatures
     * @param featuresToTestAsFacts a vector of FeaturePredicate which need to be tested as facts
     * @param rhsOps
     * @param action
     */
    public Rule( String name,
		 Vector /* String */ lhsPath,
		 Vector /* String[] */ lhsFeatures,
		 Vector /* FeaturePredicate */ featuresToTestAsFacts,
		 Vector /* String */ rhsOps,
		 String action ) {
    
    identity = count;
	count++;
    
	setName( name );
	setAction( action );

	factFeatures = featuresToTestAsFacts;

	if ( lhsPath != null ) {

    for (int i = 0; i < lhsPath.size(); i++) {

		String wmePath = (String)lhsPath.get(i);

		// The last WME-path might contain topological
		// constraints
		if ( i == lhsPath.size() -1 ) {
		    
		    int index = wmePath.indexOf('|');
		    if ( index > 0 ) {

			String constraints = wmePath.substring( index +1 );
			setLhsTopologicalConsts( constraints );
			wmePath = wmePath.substring( 0, index );
		    }
		}
		if(trace.getDebugCode("miss"))trace.out("miss-rule", "Rule [" + name+ "]: wmePath = " + wmePath);
		
		  
		addLhsPath( wmePath );
	    }
	}

	if ( rhsOps != null ) {
	    for (int i = 0; i < rhsOps.size(); i++) {
		addRhsOp( (String)rhsOps.get(i) );
	    }
	}

	if ( lhsFeatures != null ) {
	    for (int i = 0; i < lhsFeatures.size(); i++) {
		addLhsFeatures( (String[])lhsFeatures.get(i) );
	    }
	}
	

	
	
    }

    // -
    // - Methods - - - - - - - - - - - - - - - - - - - - - -
    // - 

    /**
     * Returns TRUE if the specified selection-action-input triple
     * reflects the user's action to press [Done] button, which, by
     * definition, must be "done", "ButtonPressed," and "-1."
     *
     * @param selection a <code>String</code> value
     * @param action a <code>String</code> value
     * @param input a <code>String</code> value
     * @return a <code>boolean</code> value
     **/
    public static boolean isDoneAction( String selection, String action,
					String input ) {

	return selection.toUpperCase().equals(DONE_SELECTION.toUpperCase()) &&
	    action.toUpperCase().equals( DONE_ACTION.toUpperCase() ) &&
	    input.toUpperCase().equals( DONE_INPUT );
    }

    // = = = = = = = = = = = = = = = = = = = = = = = = =
    // Print out Production Rule in Jess
    //

    private String DEFRULE = "defrule";
    private String JESS_ARROW = "=>";
    // Used to store a Jess variable name for the "problem" WME.
    // Bound by printWmePaths and referred by printOperators.  Very
    // ad-hoc... 
    private String problemVar = null;
	
    public String toString(Vector<Instruction> instructions, SimStRete ssRete) {
   	
   
	// reset problemVar
	problemVar = null;

	// The value of the "selection" WME
	String saiVal = null;
	// Jess variable to which the "selection" WME is bound
	String saiJessVar = null;

	if ( !isDoneRule() ) {
	    
	    // Identify the value of the "selection" WME
	    String saiOp = "";
	    if ( getRhsOp().size() > 0 ) {
		saiOp = (String)getRhsOp().get(getRhsOp().size()-1);
	    }
	    // We need the 2nd element
	    saiVal = saiOp.split(" ")[1];

	}
	
//    if(trace.getDebugCode("nbarbaDebug"))trace.out("nbarbaDebug", "LHSPath for "+getName() + " : " + getLhsPath());

	// Identify a Jess variable to which the "selection" WME is bound
	String saiPath = (String)getLhsPath().get(getLhsPath().size()-1);
	String[] saiPathNode = saiPath.split("\n");
	String saiWmeNode = saiPathNode[saiPathNode.length-1];
        // saiWmeNode == "?var14 <- (MAIN::cell (name commTable2_C1R2) (value nil)...)"
	saiJessVar = saiWmeNode.split(" ")[0];

	String jessRule = "(" + DEFRULE + " " + getName() + "\n\n";
	// printWmePaths bind a global variable problemVar
	jessRule += printWmePaths( saiVal, instructions,ssRete);
	jessRule += printTopologicalConditions() + "\n";
	jessRule += printFeatureConditions( numFoA ) + "\n";
	// jessRule += printTutorFactPath() + "\n";
	jessRule += JESS_ARROW + "\n\n";
	// printOperators may refer to a global variable problemVar
	
	jessRule += printOperators( saiJessVar, saiVal ) + "\n";
	// jessRule += printModifyTutorFact( saiVal ) + "\n";
        jessRule += ")\n";

	if ( !isDoneRule() ) { 
	    jessRule =
		jessRule.replaceAll( saiVal.substring(1),
				     INPUTVAR.substring(1) );
	
	}

	return jessRule;
    }

    
    
 
    /**  
     *  Method that retrieves the name of the foa from a wme path 
     *  @param wmePath: the wme path in a string format
     *  @return the name of the foa
     *  */
  	private String getWmeNameFromWmePath(String wmePath){
  		String[] path=wmePath.split("\n");
  		String lastLine=path[path.length-1];
  		 		
  	 	Pattern  p=Pattern.compile("\\(name .*?\\)",Pattern.DOTALL);
  	   	Matcher m = p.matcher(lastLine);
  	   	
  	   	String foaPattern="";
  	   	while (m.find()){   		
  	   		foaPattern=m.group(0);
  	   		
  	   	}
  	   	
  	   	foaPattern=foaPattern.replaceAll("\\(|\\)", "");
 	    
  		return foaPattern.split(" ")[1];
  	}
    
  	
  	
  	
    // Given a WME value for the "selection" WME
    private String printWmePaths( String saiVal, Vector<Instruction> instructions, SimStRete ssRete) {
    
	String str = "";
	int no = 0;

	// The SEED WMEs have consecutive value names
	int seedNum = getLhsPath().size() - (isDoneRule() ? 0 : 1);
	
	/*static string to fix the jess not accepting multiple bindings*/
	String[] letters={"a","b","c","d","e","f","g","h","i","j","k","l"};
	int lastLetterUsed=0;
	HashSet<String> set=new HashSet<String>();
	for (int i = 0; i < seedNum ; i++) {
		
		
		/*old code when learning foa constraints was here
		 int foaValueConstraint=getFoaValueConstraint(i, instructions,ssRete);
		//get i-th focus of attention from the first instruction in the vector 		
		String wmeNilValue;
		//String wmeName=getWmeNameFromInstruction(instructions.get(0).getFocusOfAttention(i));
	    String wmeName=getWmeNameFromWmePath((String)getLhsPath().get(i));
		//and get its nill value
		wmeNilValue=ssRete.nilValue(wmeName);
		String line = replaceValName((String)getLhsPath().get(i), no++, foaValueConstraint,wmeNilValue);
		*/
		
			
		
			String line=this.updateIntermediateValuesButLast((String)getLhsPath().get(i));
			no++;
	
			/*fix jess not accepting multiple bindings*/
			int indx=line.indexOf("<-");
			String firstVar=line.substring(0, indx-1);
			if (set.contains(firstVar)){
				line=line.replace(firstVar+" ", firstVar+letters[i]+" ");
				}
			set.add(firstVar);		
			/*end of fix jess not accepting multiple bindings (there is also something further down on the selection wme)*/
			
            line = processNameSlotValue(line,i);
            str += line+"\n";
            lastLetterUsed=i;
	}
	
	
	
	
	// Strip off "MAIN::" from a Jess WME object
	str = str.replaceAll( "MAIN::", "" );
	// Strip off irrelevant slots
	str = str.replaceAll( SLOTS_EXCLUDE, "" );
	str = str.replaceAll( "\\<Fact-.+?\\>", "?" );
	
	if ( !isDoneRule() ) {
	    // The SELECTION WME must have the value name that is
	    // consistent with the last variable of the RHS operator
	    // 8.17.2006 Noboru :: The "selection" must be nil 
	    // This may not be necessarily true...  
		
		
		String selectionWme =replaceValName((String)getLhsPath().get(no), saiVal +"&nil");

		/*fix jess not accepting multiple bindings*/
		int indx=selectionWme.indexOf("<-");
		String firstVar=selectionWme.substring(0, indx-1);
		lastLetterUsed++;
		if (set.contains(firstVar)){
			selectionWme=selectionWme.replace(firstVar+" ", firstVar+letters[lastLetterUsed]+" ");
		}
		set.clear();
		/*end of fix jess not accepting multiple bindings*/
		
		// Strip off "MAIN::" from a Jess WME object
	    selectionWme = selectionWme.replaceAll( "MAIN::", "" );
	  
        selectionWme = selectionWme.replaceAll("\\(name .+?\\)", ""); //Gustavo 10May2007: removing name slots
            
	    // Strip off irrelevant slots
	    selectionWme = selectionWme.replaceAll( SLOTS_EXCLUDE, "" );
	    
	    // The SELECTION WME must refer its "name" slot so that the
	    // special-tutor-fact can be updated properly
	    selectionWme = assertNameBinding( selectionWme );
	    str += selectionWme + "\n";
	}

        problemVar = str.split(" ")[0];
        
	return str;
    }

    // Awfully bad code -- must replaced
    // Stop using "cell" but use "<-" as the index to extract the last 
    // WME from a "line", which is a WME path (i.e., a list of WME).
    // 
    // See if a "line" contains "cell", and if so, replace the "name" slot value with
    // "?foaN" where N is a FoA index number.  This was necessary to pass a list of 
    // FoA to the RHS of a production rule for a "here-is-the-list-of-foas" clause.
    // 
    private String processNameSlotValue(String line, int foaIndex) {
        
        int lastWmeIndex = line.lastIndexOf(WME_PATH_LEFT_ARROW);
        
        String successorWME = line.substring(0, lastWmeIndex);
        String targetWME = line.substring(lastWmeIndex);
        
        // Generalize the target WME by replacing the name slot value with "\?foa?"
        String foaString = "?foa" + foaIndex;
        targetWME = targetWME.replaceAll("\\(name .+?\\)", "(name " + foaString + ")");
        // Generalize WME path by dropping all other name slots
        successorWME = successorWME.replaceAll("\\(name .+?\\)", "");

        return successorWME + targetWME;
    }
    
   
    
    // Replace the "value" slot value with "?valN" where N is a
    // specified id.  Used to add "&~nil" at the end if notNil is true.
    // Now that we have chunks, the type of "nill" is specified from the rete. 
    // E.g. a chunk may have "[nil,[nil,nil]]" as nil value.
    private String replaceValName( String path, int id, int foaValueConstraint, String wmeNilValue) { 	
	//String newVal = "(value " + "?val" + id + (notNil ? "&~nil" : "&nil" ) + ")";
    
    	
    	
  /*  String newVal = "(value " + "?val" + id + foaConstraintValueToPrint(foaValueConstraint,wmeNilValue) + ")";
	String result=updateIntermediateValues(path);
	result = replaceLast(result, "\\(value .+?\\)", newVal);
	
        //replace (name *) with (name ?foa_index)
       return result;
      */
     
    	
       String result=updateIntermediateValuesButLast(path);
     //  String e=result.replace("\"", "");
      // e=e.replace("\\", "");
       
       return result;
    }

    private String replaceValName( String path, String saiVal) {
    	
    	path=updateIntermediateValues(path);
    	return replaceLast(path, "\\(value .+?\\)", "(value " + saiVal + ")"); 
        //return path.replaceFirst( "\\(value .+?\\)","(value " + val + ")" );	
    }
    
    /**
     * Method that updates all the node values in a wme path to ?, except
     * from the last node (which has the correct value set by the wme path learner)
     * 
     * @param path
     * @return
     */
    protected String updateIntermediateValuesButLast(String path){ 
        String returnPath="";
    	String[] line=path.split("\n");
    	for (int i=0;i<line.length-1;i++){
    		String anyVal = "(value ?)";
    		String tmp=line[i].replaceFirst("\\(value .+?\\)", anyVal);	  		
    		returnPath+=tmp+"\n";
    		
    	}
    	//last line must be unharmed, its set by the foa learning.
    	String lastLine=line[line.length-1];
    	   	
    	lastLine=lastLine.replaceFirst("\"", "");   	
    	if (lastLine.contains("&nil"))
    		lastLine=lastLine.replace("&nil\"", "&nil");
    	else if (lastLine.contains("&~nil")){
    		lastLine=lastLine.replace("&~nil\"", "&~nil");
    	}
    	else if (lastLine.contains("val4\"")){
    		lastLine=lastLine.replaceFirst("val4\"", "val4");   
    	}
    	else if (lastLine.contains("val3\"")){
    		lastLine=lastLine.replaceFirst("val3\"", "val3");   
    	}
    	else if (lastLine.contains("val2\"")){
    		lastLine=lastLine.replaceFirst("val2\"", "val2");   
    	}
    	else if (lastLine.contains("val1\"")){
    		lastLine=lastLine.replaceFirst("val1\"", "val1");   
    	}
    	else if (lastLine.contains("val0\"")){
    		lastLine=lastLine.replaceFirst("val0\"", "val0");   
    	}
    	else {
    		lastLine=lastLine.replace("\\", "");
    		lastLine=replaceLast(lastLine, "\"", "");
    	}

    	
    	returnPath+=lastLine+"\n";

    	return returnPath;
    }
    
    /**
     * Method that updates all values with 
     * @param path
     * @return
     */
    protected String updateIntermediateValues(String path){ 
        String returnPath="";
    	String[] line=path.split("\n");
    	for (int i=0;i<line.length;i++){
    		String anyVal = "(value ?)";
    		String tmp=line[i].replaceFirst("\\(value .+?\\)", anyVal);	  		
    		returnPath+=tmp+"\n";
    		
    	}
  	
    	return returnPath;
    }
    

    // Assert (name ?selection) into the "Selection" WME
    // For example, "?var14 <- (cell (value ?val2) (column-number 1))"
    // gets "?var14 <- (cell (name ?selection) (value ?val2) (column..."
    private String assertNameBinding( String selectionWme ) {	
    	/*nbarba 08/27/2014: in the past, only terminal elements had values. Now chunks
    	 * may also have values so just add (name ?selection) before last instance of (value)*/
    	return replaceLast(selectionWme, "\\(value", "(name " + SELECTION + ") (value");
    	//return selectionWme.replaceFirst( "\\(value", "(name " + SELECTION + ") (value" );
    }

    //function that given an instruction line extracts the foa type.
    // instruction line is a string formated as  wmeType | wmeName | value
    private String getWmeNameFromInstruction(String instructionLine){
		String[] tmp=instructionLine.split("\\|");
    	return tmp[1];
    	
    }
    
    
    private String printTopologicalConditions() {

	String str = "";

	for (int i = 0; i < getLhsTopologicalConsts().size(); i++) {
	    str += "(test " + (String)getLhsTopologicalConsts().get(i) + ")\n";
	}
	return str;
    }

    // Given a number of WME paths besides SAI, return a list of (test
    // ...) clauses as the Jess conditional elements
    private String printFeatureConditions( int numFoA ) {

	String str;

	if ( getLhsFeatures().isEmpty() ) {
	    str = ";; No feature conditions found...\n";
	} else {

	    ArrayList /* String[] */ lhsFeatures = getLhsFeatures();
	    boolean disjunctive = lhsFeatures.size() > 1;
	    str =  disjunctive ? "(or\n" : "";

	    for (int i = 0; i < lhsFeatures.size(); i++) {

		String[] features = (String[])lhsFeatures.get(i);

		boolean conjunctive = disjunctive && features.length > 1;
		if ( conjunctive ) {
		    str += "  (and\n";
		}

		for (int j = 0; j < features.length; j++) {
		    String condition = features[j];
            String convertedCondition=replaceConditionVar( condition, numFoA );
		    
		    boolean matchAsFact=isFactCondition(condition);
		    String pattern;
		    if(matchAsFact)
		    {
		    	String wmeName=getNameFromCondition(condition);
		    	pattern=makeWMEPattern(wmeName,getArgs(convertedCondition),convertedCondition.indexOf(NOT_STRING)!=-1);
		    }
		    else
		    	pattern="(test " + convertedCondition + ")\n";
		    
		    str += (conjunctive ? "    " : (disjunctive ? "  " : "")) +
		    pattern;
		}

		if ( conjunctive ) {
		    str += "  )\n";
		}
	    }

	    if (disjunctive) {
		str += ")\n";
	    }
	}

	
	return str;
    }
    private String getArgs(String convertedCondition)
    {
        //remove the name, outer parens, and any not, to get just the arguments of a condtion
        int argStartIndex;
        int argEndIndex=convertedCondition.lastIndexOf(")");//exclusive bound
        int notIndex=convertedCondition.indexOf(NOT_STRING);
        if(notIndex==-1)
            argStartIndex=convertedCondition.indexOf(" ")+1;
        else
            argStartIndex=convertedCondition.indexOf(" ",notIndex+NOT_STRING.length())+1;
        return convertedCondition.substring(argStartIndex,argEndIndex);
        
    }
    /**
     * 
     * @param predicateName the jess name of a predicate
     * @param args the jess variable arguments
     * @param isNegation true if the pattern should be negated
     * @return a fact pattern for use on the lhs of a rule
     */
    
    private String makeWMEPattern(String predicateName,String args,boolean isNegation)
    {
        FeaturePredicate p=getFactFeatureByName(predicateName);
    	Vector argNames=p.getArgNames();
        
    	
        String pattern;
        if(isNegation)
            pattern=NOT_STRING;

        else
            pattern="(";
        pattern+=predicateName;
    	String[] argsArray=args.split(" ");
    	for(int argNum=0; argNum<argNames.size(); argNum++)
    	{
    		pattern+=" "+"("+argNames.get(argNum)+" "+argsArray[argNum]+")";
    		
    	}
        if(isNegation)
            pattern+=")";
    	pattern+=")\n";
        return pattern;
    
    	
    	
    }
    /**
     * search the list of facts to test as features for predicateName
     * @param predicateName the jess name of a predicate
     * @return a FeaturePredicate object if found or null
     */
    private FeaturePredicate getFactFeatureByName(String predicateName) 
    {
    	if(factFeatures==null)
    		return null;
    	Iterator predIter=factFeatures.iterator();
    	while(predIter.hasNext())
    	{
    		FeaturePredicate curPred=(FeaturePredicate)predIter.next();
    		if(curPred.getName().equals(predicateName))
    		{
    			if(trace.getDebugCode("miss"))trace.out("miss","Predicate found "+predicateName);
    			return curPred;
    		}
    			
    		
    	}
    	return null;
    		
	}
    /**
     * 
     * @param condition a string representing a feature condition
     * @return true if this condition needs to be matched as a fact, false otherwise
     */
    private boolean isFactCondition(String condition) 
    {
    	if(factFeatures.size()==0)
    		return false;
        
        
           String predicateName=getNameFromCondition(condition);
           if(trace.getDebugCode("miss"))trace.out("miss","NAME IS: "+predicateName);
           return getFactFeatureByName(predicateName)!=null;
	}
    /**
     * 
     * @param condition a String representing a predicate condition
     * @return the name of the predicate
     */
    private static String getNameFromCondition(String condition)
    {
    	
        int notIndex=condition.indexOf(NOT_STRING);
        
        int leftParenIndex=condition.indexOf("(");
        int nameStartIndex;
        int nameEndIndex;
        if(notIndex==-1)
           nameStartIndex=leftParenIndex+1;
        else
            nameStartIndex=notIndex+NOT_STRING.length();
        
        
           nameEndIndex=condition.indexOf(" ",nameStartIndex);
           return condition.substring(nameStartIndex,nameEndIndex);
           
    }
	private String replaceConditionVar( String condition, int numFoA ) {
	String newCond = "";
	String[] terms = condition.split( " " );
	for (int i = 0; i < terms.length; i++) {

		int outputCount=0;//number of output variables bound
	    if ( terms[i].startsWith("?") ) {
		int valN = terms[i].charAt(1) - 'A';
		if ( valN < numFoA)   
		{
		    terms[i] = "?val" + valN;
		}
		else
		{
			if(valN>=numFoA+outputCount)
				outputCount++;
			terms[i]=OUTPUT_PREFIX+(valN-numFoA);
		}
		
		}
	    newCond += terms[i] + " ";
	}

	return newCond;
    }

    private String printTutorFactPath() {
	
	return SPECIAL_TUTOR_FACT + " <- " + STF_WME + "\n";
    }

    // Given a Jess variable to which the "Input" value is bound
    private String printOperators( String saiJessVar, String input ) {

	String str = "";

	if ( !isDoneRule() ) {

	    for (int i = 0; i < getRhsOp().size(); i++) {
	    	str += (String)getRhsOp().get(i) + "\n";
	    }
	    
	    //String[] opBinds = str.split("\n");
	    //String saiBind = opBinds[opBinds.length-1];
	    //String saiValue = saiBind.split(" ")[1];
	}
            //Gustavo 10 May 2007:
    str += printHereIsTheListOfFoas();
    if( !isDoneRule()) {
	    str += printPredictSpecialInput(input);

	    //Gustavo, 18Oct2006: this line adds a value to the WME cell, namely the student's input
	    // (?*sInput*). As can be seen from the code commented-out, it previously put the input
	    //calculated with the operator sequence. When there was no input-matching, this made no
	    //difference because they were identical.
	    str += "(modify " + saiJessVar + " (value ?*sInput*))\n";
	    //str += "(modify " + saiJessVar + " (value " + saiValue + "))\n";
	    // Add the Hint message using construct-message
		str += "(construct-message \"[ Enter\" ?input \".]\")\n";
	
	} else {
            
	    str += printPredictSpecialInput(input);
	    str += "(modify " + problemVar + " (done TRUE))\n";
	}
        
	return str;
    }

    //Gustavo 10 May 2007
    //makes the Jess call to here-is-the-list-of-foas
    private String printHereIsTheListOfFoas() {
        int seedNum = getLhsPath().size() - (isDoneRule() ? 0 : 1);
        String str = "(here-is-the-list-of-foas";
        for (int i=0; i<seedNum; i++)
            str += " ?foa"+i;
        str+=")\n";
        return str;
    }
    
    
    private String printPredictSpecialInput(String input) {

        String selectionStr = isDoneRule() ? DONE_SELECTION : SELECTION;
        String actionStr = isDoneRule() ? DONE_ACTION : getAction();
        String inputStr = isDoneRule() ? DONE_INPUT : input;
        
        //String str = "(predict-algebra-input ";
        //String str = "(predict-"+SimSt.getDomainName()+"-input"+" ";
        String str = "(" + SimSt.getPredictObservableActionName() + " ";
        
        
        str += selectionStr + " ";
        str += actionStr + " ";
        str += inputStr + " ";
        str += ")\n";
        
        return str;
    }
    
    private String printModifyTutorFact( String input ) {

	String str = "(modify " + SPECIAL_TUTOR_FACT + "\n";

        String selectionStr = isDoneRule() ? DONE_SELECTION : SELECTION;
	str += "  (selection " + selectionStr + ")\n";
        String actionStr = isDoneRule() ? DONE_ACTION : getAction();
	str += "  (action " + actionStr + ")\n";
        String inputStr = isDoneRule() ? DONE_INPUT : input;
	str += "  (input " + inputStr + ")\n";
	String hintStr = isDoneRule() ? DONE_HINT_MSG : HINT_MSG;
	str += "  " + hintStr + ")\n";

	return str;
    }

    
    //Gustavo 8 May 2007
    //removes the "&" and everything after it
    public static String getRuleBaseName(String ruleName){
        int ampersandIndex = ruleName.indexOf("&");        
        return ((ampersandIndex==-1) ? ruleName : ruleName.substring(0,ampersandIndex));
    }
    
    public static String replaceLast(String input, String regex, String replacement) {   	
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
           return input;
        }
        int lastMatchStart=0;
        do {
          lastMatchStart=matcher.start();
        } while (matcher.find());

        matcher.find(lastMatchStart);
        StringBuffer sb = new StringBuffer(input.length());
        matcher.appendReplacement(sb, replacement);
        matcher.appendTail(sb);
        return sb.toString();

    }
    
    
    /* 

    public String toStringOld() {

	String inputOp = "";
	if ( getRhsOp().size() > 0 ) {
	    inputOp = (String)getRhsOp().get(getRhsOp().size()-1);
	}
	int inputIndex = inputOp.indexOf( ' ' );
	String inputVal = (inputIndex != -1 ) ?
	    inputOp.substring( 0, inputIndex ) : "";

	String str = "<RULE name: " + name + ">\n";
	int valNo = 0;
	// The SEED WMEs have consecutive value names
	for (int i = 0; i < getLhsPath().size() -1; i++) {
	    str += replaceValName((String)getLhsPath().get(i), valNo++) + "\n";
	}
	// The SELECTION WME must have the value name that is
	// consistent with the last variable of the RHS operator
	str += replaceValName((String)getLhsPath().get(valNo), inputVal)
	    + "\n";

	// Topological constraints
	for (int i = 0; i < getLhsTopologicalConsts().size(); i++) {

	    str += "[TEST] " + (String)getLhsTopologicalConsts().get(i) + "\n";
	}
	str += "\n";

	// LHS Feature tests
	for (int i = 0; i < getLhsFeatures().size(); i++) {
	    str += "[test] " + (String)getLhsFeatures().get(i) + "\n";
	}
	str += "\n";

	str += "==>\n\n";

	// RHS operators
	for (int i = 0; i < getRhsOp().size(); i++) {
	    str += "   " + (String)getRhsOp().get(i) + "\n";
	}
	str += "\n</RULE>";

	return str;
    }
    */
   
    final int FOA_NILL=1;
    final int FOA_NOT_NILL=-1;
    final int FOA_BOTH=0;
    
    //  1 means foa was nill of all previous instructions
     //  0 means foa was both nill and not nill of all previous instructions
     // -1 means foa was not nill of all previous instructions
   
    int getFoaValueConstraint(int i, Vector<Instruction> instrunctions,SimStRete ssRete){

    	int returnValue=FOA_NOT_NILL; 			// random initial value to indicate returnValue is uninitialized   	
    	boolean firstIteration=true;	//flag to detect first iteration.
    	
    	for (Instruction inst:instrunctions){
    		
    		String currentValue=inst.getValues().get(i+1).toString();
    		//int curValue= currentValue.equals("nil")? FOA_NILL:FOA_NOT_NILL;
    		int curValue= ssRete.isNilVal(currentValue)? FOA_NILL:FOA_NOT_NILL;
    					
    		if (firstIteration){	//on the first iteration returnValue just copy the value
    			returnValue=curValue;
    			firstIteration=false;
    		}
    		else{
    			//if returnValue is not the same as current value then we have both cases (nil and not-nil).
    			if (returnValue!=curValue){
    				returnValue=FOA_BOTH;
    				break;
    			}
    		}
    			
    	}
    	    			
    	return returnValue;
    }
 
    
    private String foaConstraintValueToPrint(int value,String wmeNilValue){
    	
    	//returnValue="&\"[nil,[nil,nil]]\"";
    	String returnValue;
    	if (value==FOA_NILL)
    		returnValue="&"+wmeNilValue;
    	else if (value==FOA_NOT_NILL)
    		returnValue="&~"+wmeNilValue;
    	else
    		returnValue="";
    	return returnValue;
    }
    
}

//
// end of f:/Project/CTAT/ML/ISS/miss/Rule.java
// 


