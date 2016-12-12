/**
 * LhsSearchSuccessorFn.java
 *
 *	A successor function that expands states for LHS search
 *
 *	LHS is a collection of WME paths each of which is a chain of
 *	WME instance for a "feed" or the "selection" specified in
 *	examples.  
 *
 * Created: Wed Jan 12 13:35:13 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0 */

package edu.cmu.pact.miss;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.SimStRete;
import jess.Fact;
import jess.JessException;
import jess.RU;
import jess.Value;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class LhsSearchSuccessorFn implements SuccessorFunction {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - - 
    // -

    // A list of wme paths
    //
    // Vector /* of String */ wmePath = new Vector();
    // private void addWmwPath( String wmePath ) { wmePath.add( wmePath ); }

    // Rete network
    // 
	private boolean generalWmePaths;
    private AmlRete rete = new AmlRete();
    AmlRete getRete() { return rete; }
    private void initRete( String wmeTypeFile, String initalWmeFile, String wmeStructureFile ) {

	wmeTypeFile = wmeTypeFile.replace('\\','/');
	initalWmeFile = initalWmeFile.replace('\\','/');
	wmeStructureFile=wmeStructureFile.replace('\\','/');
	try {
	    getRete().reset();
	    getRete().readFile( wmeTypeFile );
	    getRete().readFile( initalWmeFile );
	    getRete().loadWMEStructureFromFile(wmeStructureFile);
	} catch (JessException e) {
	    e.printStackTrace();
	}
    }
    /**
     * a list of constraints to be tested during topological constraint search
     */
    private Vector /* of WMEConstraintPredicate */ constraintPredicates;
    // A list of WMEs
    // 
    private Vector /* of (WME) Fact */ wmeList = new Vector();
    // 
    private void addWmeList( Fact fact ) { this.wmeList.add( fact ); }
    // Extract all facts from the Rete net and store it into wmeList
    private void initWmeList( Instruction instruction ) {

	// Todo :: Store only WME with the same type as the seeds and
	// the selection in the instruction
	Iterator facts = getRete().listFacts();
	while ( facts.hasNext() ) {
	    addWmeList( (Fact)facts.next() );
	}
	
	
    }

    // A list of instructions
    //
    private Vector /* of Instruction */ instructions;
    private Vector /* of Instruction */ getInstructions() {
	return this.instructions;
    }
    // Return the first instruction
    private Instruction getFirstInstruction() {
	return (Instruction)getInstructions().elementAt(0);
    }
    // The instructions, but the first one.  Used to test a newly
    // generated wme-path
    private Vector /* of Instruction */ instructionsCdr;
    private Vector /* of Instruction */ getInstructionsCdr() {
	return this.instructionsCdr;
    }
    private void setInstructions( Vector instructions ) {
	this.instructions = instructions;
	this.instructionsCdr = (Vector)instructions.clone();
	this.instructionsCdr.remove(0);
    }
   
    SimStRete ssRete;
    private void setSsRete (SimStRete ssRete){ this.ssRete=ssRete;}
    private SimStRete getSsRete(){ return this.ssRete;}
    
    // -
    // - Constructor - - - - - - - - - - - - - - - - - - - -
    // -
   
    /**
     * Creates a new <code>LhsSearchSuccessorFn</code> instance.
     *
     * @param wmeTypeFile a name of the file from which WME
     * definitions must read
     * @param initialWmeFile a name of the file from which initial
     * WMEs must be read
     * @param wmeStructureFile -a file giving the WME hierarchy
     * @param instructions a list of instructions
     * @param constraintPredicates a list (of WMEConstraintPredicate) to be tested during the constraint search
     **/
    public LhsSearchSuccessorFn( String wmeTypeFile,
				 String initialWmeFile, String wmeStructureFile,
				 Vector /* of Instruction */ instructions, Vector /* of WMEConstraintPredicate */ constraintPredicates,
				 boolean generalWmePaths,SimStRete ssRete) {
    setSsRete(ssRete);
    this.generalWmePaths = generalWmePaths;
	// Initiate Rete network with the type definition file and the
	// initial WME file
    // Added for webstart
    try {
		getRete().reset();
	} catch (JessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
    trace.out("miss"," working folder is " + System.getProperty("user.dir"));
    trace.out("miss","trying to get file " + wmeTypeFile);
    parse(wmeTypeFile);
	parse(initialWmeFile);
	loadWMEStructureFromReader(wmeStructureFile);
	
	//initRete( wmeTypeFile, initialWmeFile,wmeStructureFile );

//	if(trace.getDebugCode("nbarbaDebug"))trace.out("nbarbaDebug", "######################### LHSSearchSuccessoreFn constructor. Inistructions : " + (Instruction)instructions.get(0));
	
	
	// Extract WMEs and put them into a list
	initWmeList( (Instruction)instructions.get(0) );
    trace.out("miss","wme list initialized for instruction" + (Instruction)instructions.get(0)  );

	// Store instructions
	setInstructions( instructions );
	this.constraintPredicates=constraintPredicates;
    }

    public void parse(String fileName) {
    	InputStreamReader isr = null;
    	if(SimSt.WEBSTARTENABLED){
    		ClassLoader cl = this.getClass().getClassLoader();  
            trace.out("miss","LHSSearch reading file " + fileName);
            InputStream is = cl.getResourceAsStream(fileName);
            isr = new InputStreamReader(is);
    	}
    	else{
    		InputStream is = null;
			try {
				  trace.out("miss","LHSSearch reading file " + fileName);
				is = new FileInputStream(fileName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isr = new InputStreamReader(is);
    	}
    	
        BufferedReader br = new BufferedReader(isr);
        try {
			getRete().parse(br, false);
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void loadWMEStructureFromReader(String fileName) {

    	InputStreamReader isr = null;
    	if(SimSt.WEBSTARTENABLED){
        	ClassLoader cl = this.getClass().getClassLoader();
            InputStream is = cl.getResourceAsStream(fileName);
            isr = new InputStreamReader(is);
        }
    	else{
    		InputStream is = null;
			try {
				is = new FileInputStream(fileName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isr = new InputStreamReader(is);
    	}
        BufferedReader br = new BufferedReader(isr);
		getRete().loadWMEStructureFromReader(br);
    }

    // -
    // - Methods - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    // Implementation of aima.search.framework.SuccessorFunction

    
    
    /***
     * Method that given an instruction, and a targetWme, returns
     * the order of that wme in the wme path. 
     * 
     *  
     * @param inst
     * @param targetWme
     * @return
     */
    private int getFoaOrderInWmePath(Instruction inst, String targetWme){
    	int returnValue=-1;
    	for (int i=0;i<inst.numFocusOfAttention();i++){
    		String str=inst.getFocusOfAttention(i);
    		String[] token=str.split("\\|");
    		if (token[1].equals(inst.getInstructionValue(targetWme)))
    			returnValue=i;

    	}
    	return returnValue;
    }   
    

    /*Possible return values for the getFoaValueConstraints*/
	final int FOA_NIL=1;
	final int FOA_NOT_NIL=-1;
	final int FOA_BOTH=0;
	
	/***
	 * Method that determines if a wme is nil or not across all instructions (foa
	 * constraint learning). 
	 * 
	 * @param targetWme the wme we want to check across all instructions.
	 * @param instrunctions the instructions.
	 * @return 1 if foa was nill of all previous instructions, 0 if foa was both nill and not nill of all previous instructions
     * 		   and -1 if foa was not nill of all previous instructions
	 */
	int getFoaValueConstraint(String targetWme, Vector<Instruction> instrunctions){

	int returnValue=FOA_NIL; 			// random initial value to indicate returnValue is uninitialized   	
	boolean firstIteration=true;	//flag to detect first iteration.
	
	//get the order of the Wme in the wme path 
	int order=getFoaOrderInWmePath((Instruction) instructions.get(0) , targetWme);
	
	
	for (Instruction inst:instrunctions){
		
		String currentValue=inst.getValues().get(order).toString();
		int curValue= getSsRete().isNilVal(currentValue)? FOA_NIL:FOA_NOT_NIL;
					
		if (firstIteration){	//on the first iteration returnValue just copy the value
			returnValue=curValue;
			firstIteration=false;
		}
		else{
			if (returnValue!=curValue){
				returnValue=FOA_BOTH;
				break;
			}
		}
			
	}
	    			
	return returnValue;
	}

	/***
	 * Method that takes the nil value of a wme and appends to it either &~ or & or nothing,
	 * depending if the wme was nil across all instructions
	 * 
	 * @param foaConstraint either FOA_NIL, FOA_NOT_NIL, or BOTH, estimated through {@link edu.cmu.pact.miss.LhsSearchSuccessorFn.getFoaValueConstraint}  
	 * @param wmeNilValue the nil value of the wme
	 * @return
	 */
	 private String appendFoaConstraint(int foaConstraint,String wmeNilValue){
			if (wmeNilValue.contains("Add") || wmeNilValue.contains("Find a Common Denominator") || wmeNilValue.contains("Simplify") || wmeNilValue.contains("Complex Fraction"))
	    		wmeNilValue="nil";
	    	if (wmeNilValue.contains("Specified") || wmeNilValue.contains("nilnil"))
	    		wmeNilValue="nil";
	    	
		 
		 
	    	String returnValue;
	    	if (foaConstraint==FOA_NIL)
	    		returnValue="&"+wmeNilValue;
	    	else if (foaConstraint==FOA_NOT_NIL)
	    		returnValue="&~"+wmeNilValue;
	    	else
	    		returnValue="";
	    	return returnValue;
	    }
	 
	 /**
	  * High level method that constructs the learned foa value for a wme. Learned value
	  * depends on previous values on all instructions.
	  * @param targetWme
	  * @return new targeWme value
	  */
	 private String getNewValueForFoa(String targetWme){
		 String newValue;

		 //get foaConstraintCategory (i.e. FOA_NIL, FOA_NOT_NIL, BOTH)
		 Instruction inst=(Instruction) getInstructions().get(0);
		//get nil value
		 int foaConstraintCategory=getFoaValueConstraint(targetWme,getInstructions());
		 String nilVal=ssRete.nilValue(inst.getInstructionValue(targetWme));
		 //create the value
		 newValue=appendFoaConstraint(foaConstraintCategory,nilVal);
		 //create the ?valx, where x is the position in wmepath.
		 int orderInWmePath=getFoaOrderInWmePath((Instruction) getInstructions().get(0),targetWme);
		 orderInWmePath--;	//first instruction is selection. 
		//concatenate everything to create the new value;
		 String genVal="?val"+orderInWmePath;
		 newValue=genVal+newValue;

		 return newValue;
	 }
    /**
     * Describe <code>getSuccessors</code> method here.
     *
     * @param object an <code>Object</code> value
     * @return a <code>List</code> value
     */
    public final List getSuccessors(final Object object) {

	// The return value
	List successors = new ArrayList();
	// The current state being expanded
	LhsState lhs = (LhsState)object;
	

	// Get a target WME that must be taken care of next
	String targetWme = lhs.nextTargetWme();

	// Expand state only when there are more WMEs that must be
	// involved in LHS 
    // AND, the search went not too long (Sat Sep 30 21:20:15 LDT 2006 :: Noboru)
	if ( targetWme == null && !SimSt.isRunningOutOfTime("LHS")) return successors;

	// The order of the current target WMEs in the targetWme
	// list.  
	int numWmeDone = lhs.numWmeProcessed();
	
	//	Retreive all wme-paths for the target WME, and ...
	Vector /* WmePath */ wmePaths = findWmePaths( targetWme );
	

	//addition to add foa constraint learning
	String newValue=getNewValueForFoa(targetWme);
	

	/*
	 * Reimplement for t-chunking
	 * 5
	//Check to see if this targetWme has multiple parents
	// and requires branched
	Vector branchedWmes = getRete().getWmeBranchParents(targetWme);
	Vector allPaths = new Vector(); // contains all paths (all branches) to this targetWme
	if (branchedWmes == null) {
		// Retreive all wme-paths for the target WME, and ...
		allPaths.add(findWmePaths( targetWme, null ));
	}
	else {
		for (int i=0; i<branchedWmes.size(); i++) {
			//Retrieve each Vector, insert into larger vector of wmePaths
			allPaths.add(findWmePaths(targetWme, (String)branchedWmes.get(i)));
		}
	}
	//Permute all of the paths to generate Vector of Vectors
	// Each Vector in wmePaths will have one entry from each of the types specified
	// in branchedWmes (i.e. one wme-path with row, one with column) 
	Vector permutedPaths = permutePaths(allPaths);
	*/
	// make a new successor states for each of the wme-path
	Iterator thePaths = wmePaths.iterator();
	while ( thePaths.hasNext() ) {
	    WmePath wmePath = (WmePath)thePaths.next();
		
	    // targetWme was extracted from the first instruction,
	    // hence cdr of instructions are enough here
	    if ( isMakeSence( wmePath, getInstructionsCdr(), numWmeDone ) ) {
		/* Vector WmeConstraint */ 
	        Vector satisfiableWMEs = testWMEConstraint(lhs, wmePath, getInstructions());  //Vector allConstraintSets = allSetsOfWmeConstraints( lhs, wmePath );
	        Vector Csw = powerSet(satisfiableWMEs);
	        
	        for (int i=0; i<Csw.size(); i++){
	            Vector constSet = (Vector) Csw.get(i);
	            
	            
	            //Get the last node (i.e. foa) and update its value
	            WmePathNode lastNode=wmePath.getLastNode(); 
	            Value gValue;
				try {					
					gValue = new Value( newValue, RU.STRING);
					lastNode.getWme().setSlotValue("value",gValue);
				} catch (JessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			 
				 
	            LhsState child = makeLhsState( lhs, wmePath, constSet );
	            String action = actionStr( wmePath, constSet );
	            Successor s = new Successor( action, child );
	            successors.add( 0, s );
	        }

//	    // For each set of constraints, test its validity
//		// and make a new state
//		Iterator constraintSets = allConstraintSets.iterator();
//		while ( constraintSets.hasNext() ) {
//			
//		    /* WmeConstraint */ 
//		    Vector constSet = (Vector)constraintSets.next();
//
//		    // Verify if all instructions agree with the
//		    // constraint about to be asserted
//
//		    if ( testConstraints( lhs, constSet, getInstructions() ) ) {
//			LhsState child = makeLhsState( lhs, wmePath, constSet );
//			String action = actionStr( wmePath, constSet );
//			Successor s = new Successor( action, child );
//			// Add the successor at the top of the list, since
//			// AIMA scans the list from head to tail and put
//			// each element at the head of the search queue
//			successors.add( 0, s );
//		    }
//		}
	    }
	    else{
	    	//if(trace.getDebugCode("nbarbaDebug"))trace.out("nbarbaDebug", "######################### IS MAKE SENSE IS FALSE ");
	    }
	}
	// printSuccessors(successors);
	
	//if(trace.getDebugCode("nbarbaDebug"))trace.out("nbarbaDebug", "Successors List size "+successors.size());
	
	return successors;
    }

    //powerSet({A,B,C}) returns {{},{A},{B},{C},{A,B},{A,C},{B,C},{A,B,C}}
    private Vector powerSet(Vector satisfiableWMEs) {

        Vector /* Vector WmeConstraint */ wmeConsts = new Vector();

        Vector /* WmeConstraint */ consts = satisfiableWMEs; //findWmeConstraints( lhs, wmePath );
        
        int numConst = consts.size();
        for ( int i = (1<<numConst)-1; i > -1; i-- ) {

            Vector tmpConst = new Vector();
            for ( int j = 0; j < numConst; j++ ) {

                if ( (i & 1<<j) != 0 ) {
                    tmpConst.add( (WmeConstraint)consts.get(j) );
                }
            }
            wmeConsts.add( tmpConst );
        }
        return wmeConsts;
    }
    
    //returns the set of good WmeConstraints
    private Vector testWMEConstraint(LhsState lhs, WmePath wmePath, Vector instructions) {
        Vector  /* WmeConstraint */ constrs = findWmeConstraints( lhs, wmePath );	

        Vector res = new Vector();
        for (int i=0; i<constrs.size(); i++){
            WmeConstraint constr = (WmeConstraint) constrs.get(i);
            if(isGoodConstraint(lhs, constr))
                res.add(constr);
        }
        return res;
    }
    
    
    //returns true iff apply() returns true for all instructions
    private boolean isGoodConstraint(LhsState lhs, WmeConstraint constr) {
        boolean test = true;

        Vector /*of Instruction*/ instructions = getInstructions();

        for (int i=0; i<instructions.size(); i++)//iterate through instructions
        {
            
            Instruction instruction = (Instruction) instructions.get(i);
            Fact[] args = identifyWmePathNode( constr, lhs, instruction );

            if ( !constr.apply( args ) ) {
                test = false;
                break;
            }
                     
        }
        return test;
    }

    
    /** 
     * Reimplement for chunking
     * 
     * 
     * Paths contains a number of vectors, each of which contains all of the generalized wme-paths
     * associated with a particular branch in the tree (i.e. all of the paths
     * with a 'row' wme type, all of the paths with a 'column' wme type, both of which
     * end in the 'cell' wme type).
     * 
     * Permutes all of the elements so that returns a Vector of Vectors. Each inner Vector
     * contains one wme-path of each type above (i.e. one 'row', one 'column'). Produces
     * all such permutations (unordered, so no duplicates).
     * 
     * 
     * @param paths
     * @return
    private Vector permutePaths(Vector paths) {
    	Vector permuted = new Vector();
    	Iterator allPathsIter = paths.iterator();
    	Vector first = (Vector)allPathsIter.next();
    	//First add all elements of the first vector to permuted
    	for (int i=0; i<first.size(); i++) {
    		Vector newVec = new Vector();
    		newVec.add(first.get(i));
    		permuted.add(newVec);
    	}
     	while (allPathsIter.hasNext()) {
     		// Get next Vector
     		Vector branchHolderVec = (Vector)allPathsIter.next();
     		// Retrieve vectors from permuted, add next level of 
     		// elements to the vectors
     		Iterator currentPermutedIter = permuted.iterator();
     		while (currentPermutedIter.hasNext()) {
     			// Foreach vector in permuted, remove
     			// the vector and add all elements of branchHolderVec 
     			// to it
     			Iterator branchIter = branchHolderVec.iterator();
     			Vector permutedVec = (Vector)currentPermutedIter.next();
     			permuted.remove(permutedVec);
     			while (branchIter.hasNext()) {
     				Vector newPermutedVec = (Vector)permutedVec.clone();
     				newPermutedVec.add(branchIter.next());
     				permuted.add(newPermutedVec);
     			}
     		}
     	}
     	return (permuted);
    }
    */
    
    private String actionStr(WmePath wmePath, Vector /* WmeConstraint */ cv) {

	String str = wmePath.toString();
	for ( int i = 0; i < cv.size(); i++ ) {

	    WmeConstraint wmeConst = (WmeConstraint)cv.get(i);
	    str += "|" + wmeConst;
	}
	return str;
    }

    // Return all combination of WmeConstraints given a WME path
    // combined with the LHS state
    private Vector allSetsOfWmeConstraints( LhsState lhs, WmePath wmePath ) {
	/* of Vector WmeConstraint */

	Vector /* Vector WmeConstraint */ wmeConsts = new Vector();

	// Find all WME constraints that must be
	// considered when the target path, wmePath, is
	// added to the current LHS
	Vector /* WmeConstraint */ consts = findWmeConstraints( lhs, wmePath );
	
	// Given a set of WME constraints, make all
	// combinations, and for each of them test if it
	// makes sense when embedded into the current LHS
	// along with the target WME path
	int numConst = consts.size();
	for ( int i = (1<<numConst)-1; i > -1; i-- ) {

	    Vector tmpConst = new Vector();
	    for ( int j = 0; j < numConst; j++ ) {

		if ( (i & 1<<j) != 0 ) {
		    tmpConst.add( (WmeConstraint)consts.get(j) );
		}
	    }
	    wmeConsts.add( tmpConst );
	}

	return wmeConsts;
    }


    // Return all different combinations of topological constraints
    // defined by pairing the leaf WmePathNode in the wmePath, which
    // is about to be added to the current LHS, and the existing
    // leaf WmePathNode's in LHS.
    /* WmeConstraint */ 
    private Vector findWmeConstraints( LhsState lhs, WmePath wmePath ) {
	
	Vector /* WmeConstraint */ constraints = new Vector();

	// Find all binary combinations among the floaring WMEs and
	// compile them into topological constraints
	if ( lhs.numWmePath() > 0 ) {
	    
	    // The focus of attention, which is the leaf node of wmePath
	    WmePathNode leafNode = wmePath.getLastNode();
	    for (int i = 0; i < lhs.numWmePath(); i++) {

		// Get the i-th WmePath
		WmePath lhsWmePath = lhs.getWmePath(i);
		// Get WmePathNodes in the WmePath
		WmePathNode lhsLeafNode = lhsWmePath.getLastNode();
			
			if ( !lhsLeafNode.hasSameSymbol( leafNode ) &&
			     lhsLeafNode.hasSameWmeType( leafNode ) ) {
	
			    for ( int k = 0; k < constraintPredicates.size(); k++ ) {	    				    		    
				// A topological constraint must be binary
				WmePathNode[] args = { lhsLeafNode, leafNode };
				//trace.out("const pred: " + constraintPredicates.get(k));
				//for (int e=0;e<args.length;e++)
				//	trace.out("	adding args " + args[e]);
				//trace.out("****");
				WmeConstraint wmeConst =
				    new WmeConstraint( getRete(), (WMEConstraintPredicate)constraintPredicates.get(k), args );
				constraints.add( wmeConst );
			    }
			}
			
			for (int j = i+1; j < lhs.numWmePath(); j++) {

				// Get the i-th WmePath
				WmePath lhsWmePathJ = lhs.getWmePath(j);
				// Get WmePathNodes in the WmePath
				WmePathNode lhsLeafNodeJ = lhsWmePathJ.getLastNode();

				if ( !lhsLeafNode.hasSameSymbol( lhsLeafNodeJ ) &&
				     lhsLeafNode.hasSameWmeType( lhsLeafNodeJ ) ) {
			
					for ( int k = 0; k < constraintPredicates.size(); k++ ) {
						// A topological constraint must be binary
						WmePathNode[] args = { lhsLeafNode, lhsLeafNodeJ };
						WmeConstraint wmeConst =
							new WmeConstraint( getRete(), (WMEConstraintPredicate)constraintPredicates.get(k), args );
						constraints.add( wmeConst );
				    }
				}
			}			
	    }
	}
	return constraints;
    }

    HashSet jessVars = null;
   
    String[] letters={"","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","w","x","y","z"};
    int letterCounter=0;
    
    // Given a targetWme, find all possible chain of WME instantiation
    // from the root WME, i.e., the problem WME, to targetWME.
    private Vector /* WmePath */ findWmePaths( String targetWme ) {
   
    
    	
    	Vector /* of WmePath */ paths = new Vector();

    	// Get a most specific path for the seed
    	StringTokenizer seedTokenizer = new StringTokenizer( targetWme, "|" );
    	String wmeType = seedTokenizer.nextToken();
    	String wmeName = seedTokenizer.nextToken();

    	Vector curPathSet = getRete().getWmePath( wmeType, wmeName,letters[letterCounter] );
    	
    	//Only check first path
    	WmePath path = (WmePath)curPathSet.get(0);
    	paths.add(path);
    	int pathLength =  1 << path.length();

    	for ( int i = 2; i < pathLength; i += 2) {
    		WmePath generalizedPath; 
    		if  ( (generalizedPath = path.generalize(i)) != null ) {
    			//paths.add( generalizedPath );	
    			// I want to test out what reversing the order for generalized paths will do to the alg.
    			if(this.generalWmePaths)
    				paths.add( 0, generalizedPath );
    			else
    				paths.add(generalizedPath);
    		}
	}
	
	
	
	
	return paths;
    }
    
    /** 
     * 
     * Reimplement for chunking
     * 
     * Given a targetWme, find all possible chain of WME instantiation
     * from the root WME, i.e., the problem WME, to targetWME.
     * If branch name is non-null, only return the vector of paths that contain
     * the wme of the given name.
    
    private Vector findWmePaths_chunking_version( String targetWme, String branchName ) {

	Vector paths = new Vector();
	
	// Get a most specific path for the seed
	StringTokenizer seedTokenizer = new StringTokenizer( targetWme, "|" );
	String wmeType = seedTokenizer.nextToken();
	String wmeName = seedTokenizer.nextToken();
	 Vector curPathSet = getRete().getWmePath( wmeType, wmeName, branchName );
	 
	 Iterator iter=curPathSet.iterator();
	 while(iter.hasNext())
	 {
		WmePath path=(WmePath)iter.next();
		paths.add( path );
	
		// Add all sort of generalized paths for the path
		// 
		// The number of different patterns of generalization 
		int pathLength =  1 << path.length();
		for ( int i = 2; i < pathLength; i += 2) {
	
		    WmePath generalizedPath; 
		    if  ( (generalizedPath = path.generalize(i)) != null ) {
			paths.add( generalizedPath );	 
		    }
		}
	 }
	return paths;
    }
    */
    
    // Verify if all newly found constraints agree with all the
    // instructions
    private boolean testConstraints( LhsState lhs, 
				     Vector /* WmeConstraint */ constraints,
				     Vector /* Instruction */ instructions ) {

	boolean test = true;

	for (int i = 0; i < instructions.size(); i++) {

	    Instruction instruction = (Instruction)instructions.get(i);
	    if ( !testConstraints( lhs, constraints, instruction ) ) {

		test = false;
		break;
	    }
	}
	return test;
    }

    // Test the constraints against an individual instruction
    private boolean testConstraints( LhsState lhs,
				     Vector /* WmeConstraint */ constraints,
				     Instruction instruction ) {

	boolean test = true;

	for ( int i = 0; i < constraints.size(); i++ ) {

	    WmeConstraint theConst = (WmeConstraint)constraints.get(i);

	    // The constraint represents relations held in the target
	    // WME.  Thus to test the constraint against the
	    // instruction, we need to map the variables (i.e.,
	    // WmePathNode) specified in the constraint onto the WME
	    // paths in instruction
	    Fact[] args = identifyWmePathNode( theConst, lhs, instruction );

	    if ( !theConst.apply( args ) ) {
		test = false;
		break;
	    }
	}
	return test;
    }

    // Given a topological constraint, map its arguments (i.e.,
    // WmePathNode) onto the instruction and identify corresponding
    // WmePathNodes against which the constraint would be tested
    private Fact[] identifyWmePathNode( WmeConstraint constraint,
					LhsState lhs,
					Instruction instruction ) {
	Fact[] target = new Fact[ constraint.getArity() ];
	
	for ( int i = 0; i < constraint.getArity(); i++ ) {
	    WmePathNode wmePathNode = constraint.getNthArg(i);
	    int wmePathIndex = lhs.wmePathIndexOf( wmePathNode );

	    
	    // The constraint is about the newly added WME
	    if ( wmePathIndex == -1 ) {
		wmePathIndex = lhs.numWmeProcessed();
	    }
	    // Instruction has SAI on the first line, where as
	    // LhsState has it at the end.  Thus, the first FoA in the
	    // LhsState is really the 2nd line in Instruction.
	    wmePathIndex++;
	    
	    if ( wmePathIndex == instruction.numFocusOfAttention() ) {
		wmePathIndex = 0;
	    }

	    

	    // The target focus of attention 
	    
	    String targetFoA = instruction.getFocusOfAttention( wmePathIndex );

	    
	    
	    // targetFoA -> "WME-type|WME-name|value"
	    int delimIndex = targetFoA.lastIndexOf('|');
	    String wmeTypeName = targetFoA.substring( 0, delimIndex );
	    delimIndex = wmeTypeName.indexOf('|');
	    String wmeType = wmeTypeName.substring( 0, delimIndex );
	    String wmeName = wmeTypeName.substring( delimIndex +1 );
	    target[i] = getRete().lookupWme( wmeType, wmeName );
	    
	}
	return target;
    }

    // Verify if the newly found wmePath agree with all instructions
    private boolean isMakeSence( WmePath wmePath,
				 Vector /* Instruction */ instructions,
				 int numWmeProcessed ) {

	boolean theAnswer = true;
	
	
	Iterator instructionIterator = instructions.iterator();
	try {
	    while ( instructionIterator.hasNext() ) {
		Instruction instruction = (Instruction)instructionIterator.next();
		if ( !isMakeSence( wmePath, instruction, numWmeProcessed ) ) {
			
		    theAnswer = false;
		    break;
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return theAnswer;
    }

    // See if the (numWmeProcessed + 1)th target WME (i.e., a seed or
    // a selection WME) in the instruction is "consistent" with wmePath.
    // 
    private boolean isMakeSence( WmePath wmePath, Instruction instruction,
				 int numWmeProcessed ) throws Exception {

	// Identify a WME in instruction against which the validity of
	// wmePath is examied
	String targetWme = null;
	Vector /* of String */ seeds = instruction.getSeeds();
	
	if ( numWmeProcessed < seeds.size() ) {
	    targetWme = (String)seeds.get(numWmeProcessed);
	    targetWme = targetWme.substring( 0, targetWme.lastIndexOf('|') );
	} else if ( numWmeProcessed == seeds.size() ) {
	    targetWme = instruction.getSelection();
	} else {
	    throw new Exception( "numWmeProceed out of index" );
	}

	
	
	/* 
	 * Chunking version

	// Get a wme-path for the targetWme
	String wmeType = targetWme.substring( 0, targetWme.indexOf('|') );
	String wmeName = targetWme.substring( targetWme.indexOf('|') + 1 );
	//FIX THIS
	Vector targetPaths=getRete().getWmePath( wmeType, wmeName, null );
	Iterator iter=targetPaths.iterator();
	boolean test=true;

	while(iter.hasNext())
	{
		WmePath targetWmePath =(WmePath)iter.next(); 
	
		// Finally, see if the targetWmePath is unifiable with the
		// specified wmePath
		test = test && wmePath.isUnifiable( targetWmePath );
		if (!test)
			break;
	}
	return test;
*/

	
//	 Get a wme-path for the targetWme
	     String wmeType = targetWme.substring( 0, targetWme.indexOf('|') );
		 String wmeName = targetWme.substring( targetWme.indexOf('|') + 1 );
		 Vector wmePathVector = getRete().getWmePath( wmeType, wmeName ,letters[letterCounter]);
		
		WmePath targetWmePath = (WmePath)wmePathVector.get(0);
		

		
	   // Finally, see if the targetWmePath is unifiable with the
	   // specified wmePath
	   boolean test = wmePath.isUnifiable( targetWmePath );
	   return test;
    }

    // Make a new state based on the given parent with additional
    // wme-path
    private LhsState makeLhsState( LhsState parent, WmePath path ) {

	return makeLhsState( parent, path, null );
    }

    // Make a new state based on the given parent, additional
    // wme-path, and new WME constraint
    private LhsState makeLhsState( LhsState parent,
				   WmePath path,
				   Vector /* WmeConstraint */ constraint ) {

	LhsState child = (LhsState)parent.clone();
	child.addWmePath( path );
	child.addWmeConstraint( constraint );
	return child;
    }

}

//
// end of LhsSearchSuccessorFn.java
// 
