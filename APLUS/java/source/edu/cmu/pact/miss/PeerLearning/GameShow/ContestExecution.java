package edu.cmu.pact.miss.PeerLearning.GameShow;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import jess.JessException;


import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.JessModelTracing;
import edu.cmu.pact.jess.MTRete;
import edu.cmu.pact.jess.RuleActivationNode;
import edu.cmu.pact.jess.RuleActivationTree;
import edu.cmu.pact.jess.RuleActivationTree.TreeTableModel;
import edu.cmu.pact.miss.AskHint;
import edu.cmu.pact.miss.AskHintInBuiltClAlgebraTutor;
import edu.cmu.pact.miss.Instruction;
import edu.cmu.pact.miss.Rule;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.SimStInteractiveLearning;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.console.controller.MissController;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

/*
 * ContestExecution
 * A class to handle the technical details of solving the problems in the contest, using the productionRules
 * to list executions, evaluate these.  A game show specific version of a quiz-style execution
 */
public class ContestExecution extends SimStInteractiveLearning{

	BR_Controller controller;
	SimSt simSt;
	File prodRules;
	String studentName;
	SimStLogger logger;
    int numProblems = 0;
    Contestant contestant;
	
	public static final String DEFAULT_PRODRULES_FILE = "productionRules1.pr";
	
	public ContestExecution(SimSt ss, String prodRules,Contestant contestant)
	{
		super(ss);
		simSt = ss;
		controller = simSt.getBrController();

		loadProdRules(prodRules);
		
		logger = new SimStLogger(controller);
		this.contestant = contestant;
	}
	
	/*
	 * Load the specified production rule file into the BR Controller
	 */
	public void loadProdRules(String prodRuleFile)
	{
		trace.out("miss", "loadProdRules: " + prodRuleFile);
		if(prodRuleFile != null)  {
			trace.out("miss", "prodRuleFile is not null");
			prodRules = new File(prodRuleFile);
		}
		else {
			trace.out("miss", "prodRuleFile is null");
			prodRuleFile = DEFAULT_PRODRULES_FILE;
			prodRules = new File(prodRuleFile);
		}

		//If the specified production rule file does not exist, load a default file instead
		if(!prodRules.exists()) {
			if(!getSimSt().isWebStartMode()) {
				trace.out("miss", "Local: Using the default prodRule file");
				prodRules = new File(DEFAULT_PRODRULES_FILE);
			} else {
				// get the file from the server as it exists only on the server
				// Key for the default prodRules is "default-prodRules"
				try {
					trace.out("miss", "WebStart: Downloading the default prodRule file");
					boolean fileDownloaded = ((MissController) controller.getMissController()).getStorageClient().retrieveFile(DEFAULT_PRODRULES_FILE, DEFAULT_PRODRULES_FILE, WebStartFileDownloader.SimStWebStartDir);
					if(fileDownloaded)
						prodRules = new File(WebStartFileDownloader.SimStWebStartDir+DEFAULT_PRODRULES_FILE);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		String wmeTypefile = "wmeTypes.clp";
		File prDir = prodRules.getParentFile();
		if(prDir != null)
			wmeTypefile = prDir.getPath()+System.getProperty("file.separator")+wmeTypefile;
		
		trace.out("miss", "wmeTypeFile: " + wmeTypefile);
		
		//Attempt to remove all old rules and replace them with those in the specified production
		//rules file
		try
		{
			controller.getModelTracer().getRete().removeAllRules();
		}catch(Exception e){ 
			e.printStackTrace(); }
		try {
			controller.getModelTracer().getRete().batch(wmeTypefile);
		} catch (JessException e) {
			JOptionPane.showMessageDialog(null, "Could not batch wmeTypes "+wmeTypefile);
			e.printStackTrace();
		}
		reload();

		
	}
	
	/*
	 * Perform the actual reload of the production rules
	 */
	private void reload()
	{
		boolean result = controller.getModelTracer().getRete().reloadProductionRulesFile(prodRules,false);
		trace.out("ss", "Production rule file "+prodRules+" loaded: "+result);
	}
	
	public void reset()
	{
		numProblems = 0;
	}
	
	/*
	 * Solve a problem all the way to a final solution.
	 */
	public Solution contestOnWholeProblem(String problemName)
	{
				
    	// Enter a problem & create a start state
        createStartStateOnProblem(problemName);

		logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.PROBLEM_START_ACTION, "problem"+(numProblems+1),
				"", "", 0, "",contestant.rating);

        long startTime = (new Date()).getTime();
        
        //Solve the problem
	    Solution solution = solveWholeProblem();
  
		
	    //Format the solution for display
	    ProblemNode startState = brController.getProblemModel().getStartNode();
        Vector<ProblemEdge> solution2 = simSt.getProblemAssessor().findSolutionPath(startState);
                
        String solutionString = getSimSt().getProblemAssessor().formatSolution(solution2, problemName);
        //If either LHS or RHS has not been replaced already, remove it
        solutionString = solutionString.replace("LHS", "");
        solutionString = solutionString.replace("RHS", "");
        
        //Save the formatted solution
        solution.solutionPath = "<html>"+solutionString+"</html>";
        trace.out("ss", solution.solutionPath);
        
        //Check if the solution is a correct solution - looking only at the answer rather than
        //intermediate steps
        solution.correctness = getSimSt().getProblemAssessor().isSolution(problemName, solution.answer);
        

		long endTime = (new Date()).getTime();
        
        long duration = endTime - startTime;
        
        logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.ANSWER_SUBMIT_ACTION, solution.answer, 
        		solution.answer, "", solution.correctness, (int) duration,contestant.rating);

		numProblems++;
        return solution;
	}
	
	/*
	 * Create a start state from a string
	 * @see edu.cmu.pact.miss.SimStInteractiveLearning#createStartStateOnProblem(java.lang.String)
	 */
	public void createStartStateOnProblem(String problemName) {

		controller.startNewProblem();
        try{
            //if no BRD is loaded, create the start state
            if (controller.getProblemModel().getStartNode()==null){
                
                String[] sp = problemName.split("=");
                String c1r1Value = sp[0].trim();
                String c2r1Value = sp[1].trim();
                
                //update the cell text
                SimStInteractiveLearning.simulateCellTextEntry("dorminTable1_C1R1", c1r1Value);
                SimStInteractiveLearning.simulateCellTextEntry("dorminTable2_C1R1", c2r1Value);
                                
                String normalProblemName = SimSt.convertToSafeProblemName(problemName);
                trace.out("ss", "Problem Name created: "+normalProblemName);

                controller.createStartState(normalProblemName);
                
                reload();

            } else {
            
                ProblemNode node = controller.getProblemModel().getStartNode();
                trace.out("miss", "ssContest: startNode = " + node);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
	
	/*
	 * From a current node, move forward to a new node based on the specified rule activation.
	 * Note whether this step is correct based on the specified correctness.
	 */
	protected ProblemNode advanceNode(ProblemNode currentNode, RuleActivationNode ran, boolean correct) {

        ProblemNode nextCurrentNode = null;

        // Make sure that currentNode is the current node
        secureCurrentNode(currentNode);

        Sai sai = getSai(ran);
        //Create the new node for that step being done
        ProblemNode successiveNode = simulatePerformingStep(currentNode, sai);

        ProblemNode startNode = brController.getProblemModel().getStartNode();
        String step = startNode.getName();
        if(simSt.getProblemAssessor() != null)
        	step = simSt.getProblemAssessor().findLastStep(startNode, currentNode);
        simSt.setProblemStep(step);

        //how could successiveNode be null?
        if (successiveNode != null) {

            String skillName = removeAmpersand(ran.getName()) + " simStIL";
            ProblemEdge edge = updateEdgeSkillName(currentNode, successiveNode, skillName);
            //Set up the correctness of the step on the edge
            if(correct)
            	edge.getEdgeData().setActionType(EdgeData.CORRECT_ACTION);
            else
            	edge.getEdgeData().setActionType(EdgeData.CLT_ERROR_ACTION);
            	
        
            nextCurrentNode = successiveNode;

        }
            
        //Return the new node it has moved to
        return nextCurrentNode;
        
	}


	/*
	 * An answer object to track different answers
	 */
	class Answer implements Comparable<Object>
	{
		RuleActivationNode step;
		int ranking; // lower is better
		String student;
		
		Answer(String stud, int rank, RuleActivationNode ran)
		{
			step = ran;
			ranking = rank;
			student = stud;
		}
		
		public String toString()
		{
			return student+"-"+ranking+"-"+step.getActualInput();
		}

		@Override
		public int compareTo(Object arg0) {
			Integer otherRank = ((Answer) arg0).ranking;
			return new Integer(ranking).compareTo(otherRank);
		}
		
		
	}
	
	// Returns a list of rule activations for a give problemNode
    //
    public PriorityQueue<Answer> gatherActivationList(ProblemNode problemNode) {

    	PriorityQueue<Answer> results = new PriorityQueue<Answer>();

    	
	        Vector<RuleActivationNode> activationList = new Vector<RuleActivationNode>();
	

	        showActivationList();
	        
	        try{
	            if (problemNode != controller.getSolutionState().getCurrentNode()) {
	                trace.out("miss", "gatherActivationList: problemNode ==>> " + problemNode); 
	                trace.out("miss", "gatherActivationList: currentNode ==>> " + controller.getSolutionState().getCurrentNode()); 
	                // Go to the state
	                controller.setCurrentNode2(problemNode);
	            }
	
	            ProblemNode currentNode = controller.getSolutionState().getCurrentNode();
	            trace.out("miss", "gatherActivationList: currentNode ==>> " + currentNode);
	            
	            if (problemNode.getParents().isEmpty()) {
	            	trace.out("ss", "Entered problemNode.getParents().isEmpty()");
	                // The problemNode is a Start State
	                controller.goToStartStateForRuleTutors();
	            } else {
	            	trace.out("ss", "In the else part as getParents.isEmpty()is false");
	                // Go to the given state and get the productions fire
	
	                boolean useInterfaceTemplate = MTRete.getUseInterfaceTemplates();
	                MTRete.setUseInterfaceTemplates(false);
	                boolean loadJessFilesSucceeded = false;
	
	                while (!loadJessFilesSucceeded) {
	                   controller.checkProductionRulesChainNew(currentNode);
	                	
	                    if (!MTRete.loadInterfacetemplatesFailed()) {
	                        loadJessFilesSucceeded = true;
	                    } else {
	                        trace.out("miss", "gatherActivationList: RETRYING checkProductionRulesChainNew...");
	                    }
	                }
	
	                MTRete.setUseInterfaceTemplates(useInterfaceTemplate);
	            }
	            

                reload();
	            showActivationList();

		            // Get a root rule-activation node
		            RuleActivationTree tree = controller.getRuleActivationTree();
		            TreeTableModel ttm = tree.getActivationModel();
		            RuleActivationNode root = (RuleActivationNode) ttm.getRoot();
		
		            // Save the current state
		            MTRete mtRete = controller.getModelTracer().getRete();
		            root.saveState(mtRete);
		
		            
		            showActivationList();
		            
		            // Wed Oct  3 16:55:15 EDT 2007 :: Noboru
		            // There may be inactive activations in wholeAgenda, but they are 
		            // excluded in the RuleActivationTree created by createChildren() below
		            List /* Activation */ wholeAgenda = mtRete.getAgendaAsList(null);
		            // printInactiveActivations(wholeAgenda);
		
		            boolean omitBuggyRules = false;
		            root.createChildren(wholeAgenda, omitBuggyRules);
		            List children = root.getChildren();
		            JessModelTracing jmt = mtRete.getJmt();
		            
		            trace.out("ss", "Children: "+children.size()+" Whole Agenda: "+wholeAgenda.size());
		    		
		            for (int i = 0; i < children.size(); ++i) {
		                RuleActivationNode child = (RuleActivationNode)children.get(i);
		                root.setUpState(mtRete, i);
		
		                jmt.fireNode(child);               
		                activationList.add(child);
		            }
		            
		            activationList = removeDuplicateActivations(activationList);
		
		            for(int i=0;i<activationList.size();i++)
			        {
		            	if(!activationList.get(i).getActualInput().equals("FALSE"))
		            		results.add(new Answer(studentName, i, activationList.get(i)));
			        }
			        
		      
	    	
	      
	      }
	      catch(Exception e)
	      {
	        	e.printStackTrace();
	      } 
	     
        return results;
    }
    
  //Returns a new vector without any of the SAIs duplicated
    //does not affect the original list
    private Vector<RuleActivationNode> removeDuplicateActivations(Vector<RuleActivationNode> activationList)
    {
    	Vector<Sai> goodSais = new Vector<Sai>();
    	Vector<RuleActivationNode> goodActivationList = new Vector<RuleActivationNode>();
    	for(RuleActivationNode ran:activationList)
    	{
    		Sai sai = new Sai(ran.getActualSelection(), ran.getActualAction(),ran.getActualInput());
    		if(!goodSais.contains(sai))
    		{
    			goodSais.add(sai);
    			goodActivationList.add(ran);
    		}
    	}
    	return goodActivationList;
    }
    
    /*
     * Print out the activation list
     */
    private void showActivationList() {

        MTRete rete = controller.getModelTracer().getRete();

        RuleActivationTree rat = controller.getRuleActivationTree();
        TreeTableModel ttm = rat.getActivationModel();
        RuleActivationNode ran = (RuleActivationNode)ttm.getRoot();
        
        List wholeAgenda = rete.getAgendaAsList(null);
        ran.createChildren(wholeAgenda, false);
        List children = ran.getChildren();
        for (int i = 0; i < children.size(); i++) {
            RuleActivationNode child = (RuleActivationNode)children.get(i);
            trace.out("miss", "Child #" + i + " = " + child.getName());
        }
        
    }
    
    /*
     * A class to store the solution to a problem
     */
    class Solution
    {
    	String solutionPath = "";
    	String answer = "";
    	int steps = 0;
    	int incorrectSteps = 0;
    	boolean correctness = true;
    }
        
    /*
     * Go through a problem step by step until a solution is reached or no more activations are available
     */
    private Solution solveWholeProblem() {
    	
    	ProblemNode currentNode = controller.getProblemModel().getStartNode();

        boolean killMessageReceived = false; //whether a message to kill the IL thread has been received
       
        Solution solution = new Solution();
        solution.answer = SimSt.convertFromSafeProblemName(currentNode.getName());
        
        //when running not from a BRD, it never gets "done" - reaching a done state breaks out of loop
        while (!killMessageReceived) {

        	String step = simSt.getProblemAssessor().calcProblemStepString(currentNode.getProblemModel().getStartNode(), currentNode,simSt.getLastSkillOperand());
        	simSt.setProblemStepString(step);
        	
        	logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.STEP_STARTED_ACTION, step, "", "", 0, contestant.rating);
            long startTime = (new Date()).getTime();
        	
            ProblemNode nextCurrentNode = null;
            
            //Should gather activation list
            PriorityQueue<Answer> activationList = gatherActivationList(currentNode);
	         
	       nextCurrentNode = inspectAgendaRuleActivations(currentNode, activationList, solution);
	            
            // Null nextCurrentNode after the Oracle inquiry means that the user initiated a new problem
            if (nextCurrentNode !=null) {
            	
            	solution.steps++;
                //update currentNode
                currentNode = nextCurrentNode;
                try {

                	//Apply instructions for selected next node
                	Vector<Instruction> instnVector = new Vector<Instruction>();
                	brController.setCurrentNode2(currentNode);
                    trace.out("miss", "setCurrentNode2 to " + currentNode + " suceeded");
                    Instruction instn = simSt.lookupInstructionWithNode(brController.getCurrentNode());
        			instnVector.clear();
        			instnVector.add(instn);
        			trace.out("ss", instn.toString());
                    //trace.out("ss","opSequenceFound is: " + simSt.searchRhsOpsFor(instnVector));
                } catch (Exception e) {
                    trace.out("miss", "setCurrentNode2 to " + currentNode + " failed");
                }
                
                //successfully past start state/not unreachable node
                if(currentNode.getIncomingEdges().size() > 0)
                {
	                ProblemEdge edge = (ProblemEdge)currentNode.getIncomingEdges().get(0);
	                String input = edge.getInput();
	                //update skillname on transformation steps
	                if (EqFeaturePredicate.isValidSimpleSkill(input.split(" ")[0])) {
	                    simSt.setLastSkillOperand(input);
	                }
	                
                }
            } else { 
            	//next current node is null, stop learning on this problem
                
                killMessageReceived = true;
                simSt.setIsInteractiveLearning(false);
                trace.out("ss", "killMessageReceived is true.");
                
            }
            
            long endTime = (new Date()).getTime();
            long duration = endTime - startTime;
            
            logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.STEP_COMPLETED_ACTION, step, "",
            		"", (int)duration, contestant.rating);
           
            trace.out("ss", "Current Node at end of loop: "+currentNode+":"+brController.getCurrentNode());
            //Do not continue after done state
            if(currentNode.getDoneState())
            {
                simSt.setIsInteractiveLearning(false);
            	return solution;
            }
        }
        solution.correctness = false;
        return solution;
    }

    
    // Given a rule activationList, test each rule activation and take the first successful
    // activation as a representative step performance. 
    // Blame all false rule firing and signal negative example.
    protected ProblemNode inspectAgendaRuleActivations(ProblemNode currentNode, PriorityQueue<Answer> activationList, Solution solution) {

        ProblemNode nextCurrentNode = null;
        //ProblemNode backupNode = null;
        RuleActivationNode backupRan = null;

        //Tracks all activation rules looked at, but nothing is currently done to display this info
        String listAssessment = "";
        AskHint hint = simSt.getCorrectSAI(brController, currentNode);
        
        //Go through full activationList and inspect each until a good activation is found
        for (Answer ans: activationList) {
            RuleActivationNode ran = ans.step;
            
            //Do not process FALSE inputs, just move on to next
            if(ran.getActualInput().equalsIgnoreCase("FALSE"))
            	continue;
            if(nextCurrentNode != null)
            	break;
            String result = "";
            try
            {
            	result = simSt.builtInInquiryClTutor(ran.getActualSelection(), ran.getActualAction(),ran.getActualInput(), currentNode, currentNode.getProblemModel().getProblemName().replace("_", "="));
            } catch(NullPointerException npe)
            {
            	result = "Error";
            }
            
			
			Sai sai = new Sai(ran.getActualSelection(),ran.getActualAction(), ran.getActualInput());
            logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.STEP_INPUT_ACTION, 
            		simSt.getProblemStepString(), "", "", sai, result.equals(EdgeData.CORRECT_ACTION),
					hint.getSelection(), hint.getAction(), hint.getInput(),contestant.rating);
            
			if(result.startsWith("Correct"))
			{
				nextCurrentNode = advanceNode(currentNode, ran, true);
				solution.solutionPath = solution.solutionPath+" "+ran.getActualInput();
				
				updateCurrentSolution(solution, ran);
			}
	         //Valid backups can be anything at the start state (last operand is null),
	         // a skill which was not also the last skill used, or any typein step.
	         else
	         {
	     			String lastSkill = "";
	    			//Only include the operator of the last skill operand in the variable lastSkill
	    			if(simSt.getLastSkillOperand() != null && simSt.getLastSkillOperand().indexOf(' ') >= 0)
	    			{
	        			lastSkill = simSt.getLastSkillOperand().substring(0, simSt.getLastSkillOperand().indexOf(' '));
	    			}	 
	    			
		        	if(!ran.getActualInput().contains("FALSE") && 
		            (simSt.getLastSkillOperand() == null 
		            	|| !ran.getName().contains(lastSkill))
		            	|| ran.getName().contains("typein"))
		        	{
		        		listAssessment+= "Backup: Input: "+ran.getActualInput()+" "+lastSkill+" ("+ran.getName()+")\n";
			            backupRan = ran;
		        	}
	         }
	            		
        }
        
        //if we've gotten through all the nodes and have a backup for the quiz but no selected answer
        //use the backup
        if(nextCurrentNode == null && backupRan != null)
        {
        	listAssessment+= "Backup used: "+backupRan.getName()+" "+backupRan.getActualInput()+"\n";
        	solution.solutionPath = solution.solutionPath+" "+backupRan.getActualInput();
        	updateCurrentSolution(solution, backupRan);
        	solution.correctness = false;
        	solution.incorrectSteps++;
        	nextCurrentNode = advanceNode(currentNode, backupRan, false);        	
        }
        else
        {
        	listAssessment+= "Not backup used\n";
        }


        return nextCurrentNode;
    }
    
    /*
     * Track the answer that would be given for the problem if it was submitted at that step
     */
    private void updateCurrentSolution(Solution solution, RuleActivationNode ran)
    {
		if(!ran.getName().contains("-typein") && !ran.getName().contains("done"))
		{
			//If it's not a typein step and not done, then it was an operation and there is no
			//answer until that operation is performed
			solution.answer = "";
		}
		else if(!ran.getName().contains("done"))
		{
			//It is a typein step
			if(solution.answer.length() == 0)
			{
				//If there isn't any answer in place already, this is the first side of it
				solution.answer = ran.getActualInput()+"=";
			}
			else
			{
				//If there is already part of the answer, this goes on the other side
				solution.answer += ran.getActualInput();
			}
		}
		//If the step is done, the answer submitted is the one that was already in the solution
		//Done does not affect the actual answer.
    }
    
    
	
}
