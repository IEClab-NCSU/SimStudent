/**
 * f:/Project/CTAT/ML/ISS/miss/Instruction.java
 *
 *	Represent a single instruction (or demonstration, if you will)
 *	for a problem-solving step demonstrated by an author
 *
 * Created: Wed Feb 23 18:06:49 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 **/

package edu.cmu.pact.miss;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import mylib.Permutations;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.util.AbstractQueue;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;

public class Instruction implements Serializable{

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // - 
	private static final long serialVersionUID = -2380283129886880784L;
    
    final int INSTRUCTION_DELIM = '|';
    
    private transient boolean recent = false;
    public void setRecent(boolean isRecent) { recent = isRecent; }
    public boolean isRecent() { return recent; }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Production Rule (or, Skill) Name

    private String name = Rule.NONAME;
    public void setName( String name ) { this.name = name; }
    String getName() { return this.name; }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Use All FOAs in initRHSSearch
    private boolean useAllFOAs;
    public void setUseAllFOAs(boolean useAllFOAs) { this.useAllFOAs = useAllFOAs; }
    boolean getUseAllFOAs() { return this.useAllFOAs; }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Focus of Attention
    // 

    
    
    List /*<Vector<String>>*/ possibleFoas;
    List /*<Vector<String>>*/ getPossibleFoas() {
        return this.possibleFoas;
    }
    void setPossibleFoas(List /*<List<String>>*/ possibleFoas){ //which is called when the instruction is demonstrated
        this.possibleFoas = possibleFoas;
    }
    
    
    
    void setToFirstPossibleFoa() {
      currentFoaIndex = 0;
      this.focusOfAttention = (Vector) possibleFoas.get(currentFoaIndex);
    }
    
    int currentFoaIndex=-1;
    public int getCurrentFoaIndex() {
        return currentFoaIndex;
    }
//    private void setCurrentFoaIndex(int currentFoaIndex) {
//        this.currentFoaIndex = currentFoaIndex;
//    }
//

    //MAINTAIN AN INDEX
    boolean setToNextPossibleFoa() {
    	if(trace.getDebugCode("foasearch"))trace.out("foasearch", "setToNextPossibleFoa: trying next FoA on instruction " + this.getInput());
        boolean success;

        //        int i = possibleFoas.indexOf(this.focusOfAttention);
//        if (i==-1) {
//            new Exception("current FoA is not a possible FoA!").printStackTrace();
//            java.lang.System.exit(1);
//        }
        if (currentFoaIndex != possibleFoas.size()-1){ //if not the last possibleFoA
            currentFoaIndex++;
            this.focusOfAttention = (Vector) possibleFoas.get(currentFoaIndex);
            success = true;
            if(trace.getDebugCode("foasearch"))trace.out("foasearch", "setToNextPossibleFoa: new FoA is: " + this.focusOfAttention);
        }
        else {
            success = false;
            if(trace.getDebugCode("foasearch"))trace.out("foasearch", "setToNextPossibleFoa: there is no next FoA!" );
        }
        return success;
    }
    

    
    
    /*
     * A focus of attention: WME-type/WME-name/value
     *
     * The first element shows the "selection" and the "input"
     * followed by "dependent" WMEs on which the "input" was
     * determined.
     */
    private Vector /* of String */ focusOfAttention;
    public Vector /* of String */ getFocusOfAttention() {
	return this.focusOfAttention;
    }

    /**
     * number of FoA in instruction.
     * number of tuple elements in the FoilData
     */
    public int getFoilArity(){
        return numFocusOfAttention() -1;
    }
    
    
    /**
     * Return the i-th focus of attention
     *
     * @param i an <code>int</code> value
     * @return a <code>String</code> value
     */
    String getFocusOfAttention( int i ) {
    	return (String)this.focusOfAttention.get(i);
    }
    void setFocusOfAttention(Vector /* of String */ focusOfAttention) {
	this.focusOfAttention = focusOfAttention;
    }
    // Add a Focus of Attention.  Called by Sim St when it's running
    // with CTAT
    void addFocusOfAttention( String foa ) {
    	this.focusOfAttention.add( foa );
    }
    
    
    void addFocusOfAttention( List /* of String*/ wmes ) {
        for (int i=0; i<wmes.size(); i++)
            this.focusOfAttention.add((String) wmes.get(i));
    }
    
    /**
     * Returns a number of forcus of attentions
     *
     * @return an <code>int</code> value
     */
    int numFocusOfAttention() {

	return getFocusOfAttention().size();
    }
    /**
     * Returns the independent focus of attentions
     *
     * @return a <code>Vector</code> of String 
     */
    Vector /* String */ getSeeds() {

	List subList = getFocusOfAttention().subList(1, numFocusOfAttention());
	return new Vector( subList );
    }
    /**
     * Return number of independent WME's, which by definition, is one
     * less than the number of focus of attention
     *
     * @return an <code>int</code> value
     **/
    int numSeeds() {

	return numFocusOfAttention() - 1;
    }
    /**
     * Return the "selection" of this instruction, which is the form
     * of "WME-type/WME-name" appearing in the first part of a focus
     * of attention
     *
     * @return a <code>String</code> value
     **/
    String getSelection() {

	String selectionInput = (String)getFocusOfAttention().get(0);
	int valueBorder = selectionInput.lastIndexOf( INSTRUCTION_DELIM );
	return selectionInput.substring( 0, valueBorder );
    }
    /**
     * Return the "Input" value, which by definition, is the value
     * part of the first focus of attention
     *
     * @return a <code>String</code> value
     **/
    String getInput() {

	String selectionInput = (String)getFocusOfAttention().get(0);
	return getInstructionValue( selectionInput );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Problem Node:: The problem node shown in the BR
    //

    private transient ProblemNode problemNode;
    public ProblemNode getProblemNode() { return this.problemNode; }
    private void setProblemNode( ProblemNode problemNode ) {
	this.problemNode = problemNode;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Action:: Action taken (a part of Selection/Input/Action triple)
    // for the step demonstrated
    //

    private String action;
    public String getAction() { return this.action; }
    public void setAction( String action ) { this.action = action; }

    // -
    // - Constructor  - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    /**
     * Creates a new <code>Instruction</code> instance.
     *
     */
    public Instruction( String name,
			Vector /* of String */ focusOfAttention ) {
    	   setName( name );
    	   setFocusOfAttention( focusOfAttention );

    }

    /**
     * Invoked by SimSt when running on CTAT where problem
     * instructions are created dynamically
     *
     * @param problemNode a <code>ProblemNode</code> value
     * @param sai a <code>String</code> value */
    public Instruction( ProblemNode problemNode, String sai ) {
 
	setProblemNode( problemNode );
	setFocusOfAttention( new Vector() );
	addFocusOfAttention( sai );
    }

    /**
     * Invoked by SimSt when running on CTAT where problem
     * instructions are created dynamically
     *
     * @param problemNode a <code>ProblemNode</code> value
     * @param sai a <code>String</code> value */
    public Instruction( ProblemNode problemNode, String sai,String instructionID,String previousID ) {
 
	setProblemNode( problemNode );
	setFocusOfAttention( new Vector() );
	addFocusOfAttention( sai );
	setInstructionID(instructionID);
	setPreviousID(previousID);
    }
    
    
  
    
    // -
    // - Methods  - - - - - - - - - - - - - - - - - - - - - - - - - -
    // - 
    
    // ------------------------------------------------------------
    // Instruction elements
    // 
    String getInstructionValue( String instruction ) {
 	
    int begin = instruction.lastIndexOf( INSTRUCTION_DELIM );
	return instruction.substring( begin +1 );
    }

    /**
     * Returns values (i.e. third part) appearing in the WMEs inside the
     * 'focusOfAttention' of this instruction.
     * 
     * @return a <code>Vector</code> value
     **/
    Vector targetArguments() {

	Vector /* of String */ targetArguments = getValues();
	targetArguments.remove(0);
	return targetArguments;
    }

    // 
    private Vector /* String */ values = null;

    /**
     * Returns values appearing in this instruction
     *
     * @return a <code>Vector</code> of String representing all values
     * appearing in this instruction
     **/
    Vector /* of String */ getValues() {

	if ( values == null ) { 
	    Vector /* of String */ tmpValues = new Vector();
	    for (int i = 0; i < getFocusOfAttention().size(); i++) {
		String instruction = (String)getFocusOfAttention().get(i);
		tmpValues.add( getInstructionValue( instruction ) );
	    }
	    // setValues(tmpValues);
	    return tmpValues;
	} else {
	    return values;
	}
    }
    
    void setValues( Vector /* String */ values ) {
    	this.values = values;
    }
    
    String instructionID="noId";
    public  String getInstructionID(){return this.instructionID;}
    public void setInstructionID(String id){this.instructionID=id;}
    
    String previousID="noId";
    public  String getPreviousID(){return this.previousID;}
    public void setPreviousID(String id){this.previousID=id;}
    
    
   /* Instruction previousInstruction=null;
    public  Instruction getPreviousInstruction(){return this.previousInstruction;}
    public void setPreviousInstruction(Instruction inst){this.previousInstruction=inst;}
    */
    
    
    public String toString() {

	String str = getName();
	for (int i = 0; i < getFocusOfAttention().size(); i++) {
	    str += "\n" + (String)getFocusOfAttention().get(i);
	}
	// Added 6-9-06 by Reid Van Lehn <rvanlehn@mit.edu> 
	// Also output action name associated with this instruction
	str += "\n" + getAction();
	//Added 18-2-2015
	str += "\nId: " + getInstructionID();
	str += "\nPreviousId: " + getPreviousID();
	return str;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Search for the RHS operator sequence 
    //
    // It is now cached in the instruction to deal with the unordered 
    // focus of attention problem. 
    // 

    // 13Nov2006: this is now set by a command-line argument
    private static int MAX_SEARCH_DEPTH = 6; //final 
    
    private Vector /* String */ rhsOpSeq = null;
    private transient ExhaustiveSearchAgent rhsSearchAgent;
    private boolean searchFailed = false;
    private transient RhsExhaustiveGoalTest goalTest; 
    
    /**
     * Initialize a search agent. Things must be stored for back-up 
     * 
     * @param opCached
     * @param opList
     * @param fpCache
     * @param wmeTypeFile
     * 
     */
    void initRhsSearch(boolean opCached, boolean heuristicBasedIDS, Vector opList, HashMap fpCache, String wmeTypeFile, String matcher) {
    	
    	if(isSearchFailed())
    	{
    		/*trace.err("Initializing a search that has already failed");
    		new Exception().printStackTrace();*/
    		setSearchFailed(false);
    	}
    	
    	Collection val =  fpCache.values();
    	Iterator itr = val.iterator();
    	
    	RhsState initState = new RhsState( wmeTypeFile, getSeeds(), matcher );

    	
    	RhsSearchSuccessorFn rhsSucFn = 
    		opCached ? (heuristicBasedIDS? new RhsSuccessorGeneric(opList, fpCache, heuristicBasedIDS) :new RhsSuccessorGeneric( opList, fpCache )) : new RhsSuccessorGeneric( opList );
    		
    	RhsExhaustiveGoalTest goalTest;
    	
    	if(!useAllFOAs) { 
    		goalTest = new RhsExhaustiveGoalTest(this);
    	}
    	else {
    		goalTest = new RhsExhaustiveGoalTest(this, true); 
    	}
    	
    	setGoalTest(goalTest);
    
    	Problem problem = new Problem( initState, rhsSucFn, goalTest );
    	Search search = new ExhaustiveIDS( MAX_SEARCH_DEPTH );
    	ExhaustiveSearchAgent rhsSearchAgent = new ExhaustiveSearchAgent(problem, search);
    	
    	setRhsSearchAgent( rhsSearchAgent );
    }
    
    // overloaded initRhsSearch with boolean argument for useAllFOAs option
    void initRhsSearch(boolean opCached, Vector opList, HashMap fpCache, String wmeTypeFile, String matcher, boolean useAllFOAsFlag) {
    	
    	RhsState initState = new RhsState( wmeTypeFile, getSeeds(), matcher );
    	RhsSearchSuccessorFn rhsSucFn = 
    		opCached ?   new RhsSuccessorGeneric( opList, fpCache ) : new RhsSuccessorGeneric( opList );
    		
    	RhsExhaustiveGoalTest goalTest = new RhsExhaustiveGoalTest(this, useAllFOAsFlag);
    	setGoalTest(goalTest);
    	
    	Problem problem = new Problem( initState, rhsSucFn, goalTest );
    	Search search = new ExhaustiveIDS( MAX_SEARCH_DEPTH );
    	ExhaustiveSearchAgent rhsSearchAgent = new ExhaustiveSearchAgent(problem, search);
    	
    	setRhsSearchAgent( rhsSearchAgent );
    }
    
    public static void setMaxSearchDepth(int depth) {
    	MAX_SEARCH_DEPTH = depth;
    }

    /**
     * 'queue' determines the place from which the search should start
     */
    void searchRhsOpSeq(AbstractQueue queue) {

    }

    /**
     * Find a next possible RHS operator sequence.
     * Search for a RHS operator sequence that generates the "input" from 
     * the dependent focus of attention 
     */
    void searchRhsOpSeq() {

    	// Start from the most recently reached state
    	List rhsOps = null;

    	try {
    		if(trace.getDebugCode("miss-rule"))trace.out("miss-rule", "Calling search() inside Instruction.java");
    		rhsOps = getRhsSearchAgent().search();
    		if(trace.getDebugCode("miss-rule"))trace.out("miss-rule", "Done search() inside Instruction.java");
    	} catch(StackOverflowError err)
    	{
    		//TODO Don't catch stackoverflowerrors - temporary fix
    		trace.err(err.getMessage());
    		err.printStackTrace();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}

    	// Store the search result
    	if (rhsOps != null && !rhsOps.isEmpty()) {
    		setRhsOpSeq(new Vector(rhsOps));
    	} else {
    		setSearchFailed(true);
    		if(trace.getDebugCode("miss"))trace.out("miss", "Search failed - RhsOpsSeq is empty as shown below");
    		setRhsOpSeq(null);
    	}
    	
    	//	Properties searchProp = getRhsSearchAgent().getInstrumentation();
    	//	Enumeration props = searchProp.propertyNames();
    	//	while ( props.hasMoreElements() ) {
    	//	    String key = (String)props.nextElement();
    	//	    String propVal = searchProp.getProperty( key );
    	//	}
    }

    /**
     * returns true if 'rhsState' explains this instruction.
     * As a side-effect, this.focusOfAttention gets set to the first permutation of the seeds that explains
     * the instruction.
     * In case no permutation is found, the last permutation stays.
     * @param rhsState
     * @return
     */
    boolean mapFocusOfAttention(RhsState rhsState) {

        boolean mapFocusOfAttention = false;

        // Make a temporal vector with the "seeds"
        Object[] tmpSeeds = getSeeds().toArray();
        
        //we use the AutoOrderFOA flag because this is an expensive operation
        if(SimSt.isAutoOrderFOA()) {
        	
            // Permutate the "seeds"
            Permutations p = null;
            try {
                p = new Permutations(tmpSeeds);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Try to find an instance of permutation that fits the operator 
            // sequence found in the give RHS state
            while (p.hasMoreElements()) {

                Object[] theSeeds = (Object[])p.nextElement();
                Vector /* String */ v = new Vector();
                v.add(getFocusOfAttention(0));
                for (int i = 0; i < numSeeds(); i++) { //set the seeds for the current permutation
                    v.add(theSeeds[i]);
                }
                setFocusOfAttention(v);
                if (rhsState.hasValidOperations(this)) {
                    mapFocusOfAttention = true;
                    break;
                }
            }
        } else {
            Vector v = new Vector();
            v.add(getFocusOfAttention(0));
            for (int i = 0; i < numSeeds(); i++) 
                v.add(tmpSeeds[i]);
            setFocusOfAttention(v);
            mapFocusOfAttention = rhsState.hasValidOperations(this);
        }
        return mapFocusOfAttention;
    }

    /**
     * @return the RhsState corresponding to the last time that isGoalState() returned true.
     */
    RhsState getLastRhsState() {
    	// return RhsExhaustiveGoalTest.getLastState();
    	return getGoalTest().getLastState();
    }

    public void setLastRhsState(RhsState lastRhsState) {
    	getGoalTest().setLastState(lastRhsState);
    }


    // for backing up, we will need:
    // * rhsState (lastWorkingRhsStateForThisSublist)
    // * depth
    // * queue

    /**
     * having lastWorkingRhsStateForThisSublist is necessary in the FoA search, so
     * that the RHS-search can back-up to the right place.
     */
    transient RhsState lastWorkingRhsState;
    RhsState getLastWorkingRhsState() {
    	return lastWorkingRhsState;
    }
    void setLastWorkingRhsState(RhsState rhsState) {
    	lastWorkingRhsState = rhsState;
    }

    int lastWorkingDepth;
    public int getLastWorkingDepth() {
    	return lastWorkingDepth;
    }
    public void setLastWorkingDepth(int lastWorkingDepth) {
    	this.lastWorkingDepth = lastWorkingDepth;
    }

    transient AbstractQueue lastWorkingQueue;
    public AbstractQueue getLastWorkingQueue() {
    	return lastWorkingQueue;
    }
    public void setLastWorkingQueue(AbstractQueue lastWorkingQueue) {
    	this.lastWorkingQueue = lastWorkingQueue;
    }



    Vector /* String */ getRhsOpSeq() {
    	return rhsOpSeq;
    }
    void setRhsOpSeq(Vector /* String */ rhsState) {
    	this.rhsOpSeq = rhsState;
    }
    public ExhaustiveSearchAgent getRhsSearchAgent() {
    	return rhsSearchAgent;
    }
    public void setRhsSearchAgent(ExhaustiveSearchAgent rhsSearchAgent) {
    	this.rhsSearchAgent = rhsSearchAgent;
    }
    public boolean isSearchFailed() {
    	return searchFailed;
    }
    public void setSearchFailed(boolean searchFailed) {
    	this.searchFailed = searchFailed;
    }
    public RhsExhaustiveGoalTest getGoalTest() {
    	return goalTest;
    }
    public void setGoalTest(RhsExhaustiveGoalTest goalTest) {
    	this.goalTest = goalTest;
    }
    /**
     * Given a String representing an FOA in the format WmeType|WmeName|Input
     * return the WmeName(selection)
     * @param foa
     * @return
     */
    public static String getNameFromFoa(String foa)
    {
    	return foa.split("\\|")[1];
    }

    public void setQueueAndDepthFromWhichToBeginSearch(AbstractQueue queueToStartFrom,
    		int depthToStartFrom) {
    	getRhsSearchAgent().setQueueAndDepthFromWhichToBeginSearch(queueToStartFrom,depthToStartFrom);
    }

    public Search getSearch(){
    	return getRhsSearchAgent().getSearch();
    }
}


//
// end of f:/Project/CTAT/ML/ISS/miss/Instruction.java
// 
