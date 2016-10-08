package edu.cmu.pact.miss;

import java.util.List;
import java.util.Vector;

import edu.cmu.pact.Utilities.trace;


/**
 * @author mazda
 *
 */
public class RhsExhaustiveGoalTest extends RhsGoalTest {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // -
	
	private boolean useAllFOAs;
    private Instruction instruction = null;
    public Instruction getInstruction() {
        return instruction;
    }
    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }
    
    
    /*Cache for isValidOperations*/
    private HashMap validOperationCashe=new edu.cmu.pact.miss.HashMap();
    public boolean getValidOperationCashe( String key ) {
    	return (Boolean) validOperationCashe.get( key );
     } 
    private void putValidOperationCashe( String key, boolean value ) {
    	int size = validOperationCashe.size();
    	if (SimSt.FP_CACHE_CAPACITY >= 1 && size == SimSt.FP_CACHE_CAPACITY) {	
    	    	validOperationCashe.clear();
    	 }    
    	validOperationCashe.put( key, value );
    }
    
    
    // Wed Jun 7 3:35:00  Noboru
    // Need to be static for the memory window issue -- 
    // getLastState() was once called by Instruction.getLastRhsState() through 
    // accessing getGoalTest().  However, due to the memory window limitation, 
    // SimStudent might no longer have an access to the most recently expanded 
    // RhsState node, which is associated with the goatTest, which is associated 
    // with the Instruction object (which is subject of memory window control).
    // Hence, it is decided to make this static. 
    RhsState lastState = null;
    
    public RhsState getLastState() {
        return lastState;
    }
    
    public void setLastState(RhsState theLastState) {
        lastState = theLastState;
    }
    
    // -
    // - Constructor- - - - - - - - - - - - - - - - - - - - - - - -
    // -

    /**
     * @param instructions
     */
    public RhsExhaustiveGoalTest(Instruction instruction) {
    	setInstruction(instruction);
    }
    
    public RhsExhaustiveGoalTest(Instruction instruction, boolean useAllFOAsFlag) {
    	setInstruction(instruction);
    	setUseAllFOAs(useAllFOAsFlag);
    }

    // -
    // - Methods  - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    public boolean isGoalState(final Object object) {
    	RhsState rhsState = (RhsState)object;
    	// ----- debug -----
    	// setRhsState( rhsState );
    	// ----- debug -----
    	//always return true if instruction is DONE
    	//Name is set to DONE_NAME in SimSt.stepDemonstrated
    	boolean isGoalState;
    	
    	/*create the key for the hash*/
    	Vector operatorSequence=rhsState.getExpList();	 
    	String key="";
    	for (int i=0;i<operatorSequence.size();i++){
    		BindPair pair=(BindPair) operatorSequence.get(i);
    		key+=pair.toString(); 		
    	}
    	
    	String foasString="";
    	Vector instructionFoa=getInstruction().getFocusOfAttention();
    	for (int i=0;i<instructionFoa.size();i++){
    		String foa=(String) instructionFoa.get(i);
    		String foas[]=foa.split("\\|");
    		foasString+=foas[2];  	
    	}
    	
    	key=key+"$"+foasString; //Key is structured 'sequenceOfOperators$sai'
	
	    if (getInstruction().getName().equals( Rule.DONE_NAME))
	    	isGoalState = true;
	    else {
	    	if ( validOperationCashe.containsKey( key ) ) {
	    		isGoalState = getValidOperationCashe( key );
	 	    } 
	    	else{
	    		
	    	isGoalState = rhsState.hasValidOperations(getInstruction());
	    	}
	    
	    }
	    	    	    
	   // System.out.println(isGoalState + " " + useAllFOAs);
		// if useAllFOAs is true, each foa must be used in sequence of instructions
    	if(isGoalState && useAllFOAs) {
    		boolean[] foaUsed = new boolean[getInstruction().numFocusOfAttention()-1];
    		
    		Vector<BindPair> expList = rhsState.getExpList();
    		for(BindPair e : expList) {	    			
    			if(e.getExp() != null)
    			{
    				List<String> args = e.getExp().getArgs();
    			
    				//System.out.println(args.toString());
    				
    				for(String s: args) {
    					int index = Integer.parseInt(s.substring(4));
    			
    					if(index < foaUsed.length)
    						foaUsed[index] = true;
    				}
    			}
    		}
    		
    		/*
    		System.out.println(expList.toString());
    		System.out.println(Arrays.toString(foaUsed));
    		System.out.println();
    		*/
    		
    		for(int i = 0; i < foaUsed.length; i++)
    		{
    			if(foaUsed[i] == false)
    	    		isGoalState = false;
    		}
    	}
	    
	    if (isGoalState) {
	    	setLastState(rhsState);
	    } else {
	    	if(trace.getDebugCode("rhs"))trace.out("rhs", "goalTest failed on " + rhsState + " for " + getInstruction());
    	}
	    
    	return isGoalState;
    }
    
    public void setUseAllFOAs(boolean b) {
    	useAllFOAs = b;
    }
}
