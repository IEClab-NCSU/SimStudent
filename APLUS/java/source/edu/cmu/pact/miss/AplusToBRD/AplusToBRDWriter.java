package edu.cmu.pact.miss.AplusToBRD;

import java.util.ArrayList;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.miss.DStoBRD.SimStudentBRDWriter;
//import edu.cmu.pact.miss.DStoBRD.SimStudentBRDWriter.NodeEdgePairs;
import edu.cmu.pact.miss.DStoBRD.SimStudentBRDWriter.StudentTransactionItems;

public class AplusToBRDWriter extends SimStudentBRDWriter {

	/**
	 * Method to detect if an action is "New Problem Entered". 
	 * @param cf_action
	 * @return true if cf_action is "New Problem Entered"
	 */
	private boolean isActionProblemStart(String cf_action){
	return  (cf_action.equals(AplusToBRDConverter.NEW_PROBLEM_ENTERED_ACTION));	
	}
	
	/**
	 * Method to detect if an action is " Problem Restarted".
	 * @param cf_action
	 * @return true if cf_action is "Problem Restarted"
	 */
	private boolean isActionRestart(String cf_action){
		return  (cf_action.equals(AplusToBRDConverter.PROBLEM_RESTARTED_ACTION));		
	}
	
	/**
	 * Method to detect if an action is " Step undone ".
	 * @param cf_action
	 * @return true if cf_action is "Step Undone"
	 */
	 private boolean isActionStepUndone(String cf_action){
			return  (cf_action.equals(AplusToBRDConverter.STEP_UNDONE_ACTION));		
		}
	 
	  
	 
	 /***
	  * Class to hold all the necessary transaction information to create a brd edge. This class had to be extended because
	  * we now need additional information to create a brd edge (i.e. cf_action that indicates if its problem restart or step undone, 
	  * student respond that says if we must proceed to next node) and also because validBrdTransaction had to be overriden (original
	  * function was interface dependent). 
	  * 
	  * @author nbarba
	  *
	  */
	 protected static class StudentTransactionItemsExtended extends StudentTransactionItems{
	
	    private String cf_action="";
	    private String proceed="";		//proceed that determines if we should proceed on the graph or not
		public StudentTransactionItemsExtended(String studentID, String probName,
				String selection, String action, String input,
				String subskillName, String outcome, String cf_action, String proceed) {
			super(studentID, probName, selection, action, input, subskillName, outcome);
			this.cf_action=cf_action;
			this.proceed=proceed;
		}
		 
	    public  boolean validBrdTransaction() {          	            
	            return (subskillName.equalsIgnoreCase("hint") || (input != null && subskillName != null ) );	            
	    } 
		 
	 }
	 	 	
	   /**
	    * Method created to abstract the process of geting the problem name from the input file. 
	    * Parent class had domain dependent code to trim "Eq##"
	    * 
	    * @param probName
	    * @return the problem name (in this class it just returns the input problem name - no need to trim anything)
	    */
	 	@Override
	 	protected String parseProblemName(String probName){
	    	//return probName.substring(5);
	    	return probName;
	 	}
	
	
	 
	 	/***
	 	 *  Method that determines if we should advance to next node or not. A typical brd  advances to next node only if the 
	 	 *  sai is correct. In this case, since brd is created out of human student answers which may be wrong, brd advances to the next node 
	 	 *  whenever human student a) pressed the "yes" button or b) demonstrated a step (even if the sai is incorrect).
	 	 *  That information is hold in the "proceed" variable of StudentTransactionItemsExtended.
	 	 * 
	 	 * @param item Object that holds all the necessary transaction information about a brd edge. 
	 	 * @return true if brd must advance to the next node.
	 	 */
	 	@Override
	 	protected boolean proceedToNextNode(Object item) {
			return ((StudentTransactionItemsExtended) item).proceed.equalsIgnoreCase("OK");
		}
	 	
	 	

	     
	 	/***
	 	 * Method that determines if a current transaction does not actually correspond ton a brd "edge" but
	 	 * to a transaction that "points" to the next current node (e.g. new problem entered transaction or 
	 	 * action step undone transaction).
	 	 * 
	 	 * @param item Object that holds all the necessary transaction information about a brd edge. 
	 	 * @return true if an edge should be extended or not.
	 	 */
	 	@Override
	    protected boolean transactionNotActualBrdEdge(Object item){
	    	boolean returnValue=false;
	    	int startNodeID=1;
            if (isActionRestart(((StudentTransactionItemsExtended) item).cf_action))	{
            				this.setLastCorrectNodeID(startNodeID);
            				returnValue=true;
            }
            
            if (isActionStepUndone(((StudentTransactionItemsExtended) item).cf_action)){
            		setLastCorrectNodeID(getLastCorrectNodeIDSource());
            	returnValue=true;
            }

	    	return returnValue;
	    }
	    
	  
	    /***
	     * Method that creates parses the input file and creates a "transaction item" object, that holds
	     * all the necessary information about an edge of a brd. Overriden because now the "transaction item"
	     * holds more information than the one used in the parent class.
	     * 
	     * @param fileLine: a line from the BRDWritter input file
	     * @return Object that holds all the necessary transaction information about a brd edge. 
	     */
	 	@Override
	    protected Object createTransactionItem(String fileLine){
    		String[] tokens = fileLine.split("\t");
    	 
    		 int base = 1;
             String student = tokens[base+0];
             String problem = tokens[base+1];
             String cf_action = tokens[base+2];
             String selection="";
             String action="";
             String input="";
             String skill="";
             String outcome="";
             String proceed="";
             if (!isActionProblemStart(cf_action) &&  !isActionRestart(cf_action)){
            	 selection = tokens[base+3];
            	 action = tokens[base+4];
            	 input = tokens[base+5];
            	 skill = tokens[base+6];
            	 proceed = tokens[base+7];
            	 outcome=tokens[base+8];
             }
             
             
             StudentTransactionItemsExtended item = 
                     new StudentTransactionItemsExtended(student, problem, selection, action, input, skill, outcome, cf_action,proceed);
             
             
             return item;
    }
	    
	    
	
	   /***
	  	* Method that determines if a transaction is important (i.e. contains useful information related to the creation of the brd file) 
	  	* in order to be added to the transaction list (that is used to create the actual brd file). In this class, 
	  	* it returns false for transactions related to "New Problem Entered", since they are only used to identify if a new problem was given.
	  	*  
	  	* @param item Object that holds all the necessary transaction information about a brd edge. 
	  	* @return true if transaction is "New Problem Entered"
	  	*/
	 	@Override
	    protected boolean transactionImportant(Object item){
	    	return !isActionProblemStart(((StudentTransactionItemsExtended) item).cf_action);    	
	    }
    
    
	 	 /***
	     * Method that given an transaction line (inputItem) and a list of transactions that all belong to the same problem, 
	     * determines if inputItem is a new Problem. Overriden because now inputItem has information when a new problem is 
	     * entered (i.e. cf_action).
	     * 
	     * @param inputItem
	     * @param transactionList
	     * @return true if item and transactions in list belong to the same item. 
	     */
	 	@Override
	    protected boolean newProblemTransaction(Object item, ArrayList transactionList){
	    	return (isActionProblemStart(((StudentTransactionItemsExtended) item).cf_action) && !transactionList.isEmpty());
	    }
     
	 
}
