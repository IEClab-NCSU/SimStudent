package edu.cmu.pact.miss.PeerLearning.GameShow;
import java.io.File;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;


import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
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
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

import static edu.cmu.pact.miss.InquiryClAlgebraTutor.findPathDepthFirst;


public class ContestMultiProdRules extends SimStInteractiveLearning{

	BR_Controller controller;
	SimSt simSt;
	Vector<File> prodRules;
	Hashtable<String,Integer> scores;
	String[] studentName;
	
	
	//public ContestMultiProdRules(BR_Controller brcont, String[] prodRules)
	public ContestMultiProdRules(SimSt ss, String[] prodRules)
	{
		super(ss);
		simSt = ss;
		controller = simSt.getBrController();

		scores = new Hashtable<String,Integer>();
		
        /*UniversalToolProxy utp = new UniversalToolProxy();
        controller.setUniversalToolProxy(utp);
        utp.init(controller);
        controller.getProperties().setProperty("isOnline", "true");*/
        
		loadProdRules(prodRules);
	}
	
	public void loadProdRules(String[] prodRuleFiles)
	{
		prodRules = new Vector<File>();
		studentName = new String[prodRuleFiles.length];
		
		for(int i=0;i<prodRuleFiles.length;i++)
		{
			File prFile = new File(prodRuleFiles[i]+".pr");
			prodRules.add(prFile);
			studentName[i] = prodRuleFiles[i];
			scores.put(prodRuleFiles[i], 0);
		}
	}
	
	public void contestOnProblem(String problemName)
	{
		// Enter a problem & create a start state
        createStartStateOnProblem(problemName);
        //Start the contest on it
        runContest();
	}

	public void contestOnWholeProblem(String problemName)
	{
		String solutions = "";
        for(int student=0;student<prodRules.size();student++)
        {
    		// Enter a problem & create a start state
            createStartStateOnProblem(problemName);
	        boolean result = true;
          	//File prFile = new File("productionRules2.pr");
			try
			{
				controller.getModelTracer().getRete().removeAllRules();
			}catch(Exception e){ e.printStackTrace(); }
			result = controller.getModelTracer().getRete().reloadProductionRulesFile(prodRules.get(student),false);
			solutions += "Student"+student+": "+this.solveWholeProblem(student).solutionPath+"\n";
			//JOptionPane.showMessageDialog(null, "Student "+student+ " done");
			
	    }
        trace.out("ss", solutions);
        
        //Start the contest on it
        //runContest();
	}
	
	public void createStartStateOnProblem(String problemName) {

		controller.startNewProblem();
        try{
            //if no BRD is loaded, create the start state
            if (controller.getProblemModel().getStartNode()==null){
                
                String[] sp = problemName.split("=");
                String c1r1Value = sp[0].trim();
                String c2r1Value = sp[1].trim();
                
                //update the cell text
                SimStInteractiveLearning.simulateCellTextEntry("commTable1_C1R1", c1r1Value);
                SimStInteractiveLearning.simulateCellTextEntry("commTable2_C1R1", c2r1Value);
                                
                String normalProblemName = problemName.replaceAll("=", "_").replaceAll(" ", "");
                normalProblemName = normalProblemName.replaceAll("/", "I");
                normalProblemName = normalProblemName.replaceAll("\\(", "C");
                normalProblemName = normalProblemName.replaceAll("\\)", "D");
                normalProblemName = normalProblemName.replaceAll("\\.", "P");
                trace.out("ss", "Problem Name created: "+normalProblemName);
                
                controller.createStartState(normalProblemName);

            } else {
            
                ProblemNode node = controller.getProblemModel().getStartNode();
                trace.out("miss", "ssContest: startNode = " + node);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
	
	//runs interactive learning on current node
    public void runContest() {
    	 
    	ProblemNode currentNode = controller.getProblemModel().getStartNode();

        boolean killMessageReceived = false; //whether a message to kill the IL thread has been received
        

        while (!killMessageReceived) {
            ProblemNode startNode = currentNode.getProblemModel().getStartNode();
            Vector<ProblemEdge> pathEdges = findPathDepthFirst(startNode, currentNode);
            String step = simSt.getStepNameGetter().getStepName(pathEdges, startNode);
        	simSt.setProblemStepString(step);
        	
        	trace.out("ss", "----------------"+step);
        	
            ProblemNode nextCurrentNode = null;
            
            
	            //TODO Temporary
	           /* String[] prs = new String[3];
	            prs[0] = "productionRules.pr";
	            prs[1] = "productionRules2.pr";
	            prs[2] = "productionRulesOdd.pr";
	            ContestMultiProdRules contest = new ContestMultiProdRules(brController, prs);
	            contest.contest(currentNode);
	            JOptionPane.showMessageDialog(null, "Pause");
	            */
	            
	            //TODO End Temporary
	            
	            //Vector activationList = simSt.gatherActivationList(currentNode);
	            
	           
	            //nextCurrentNode = inspectAgendaRuleActivations(currentNode, activationList);
            
            nextCurrentNode = contest(currentNode);
             
            // Null nextCurrentNode after the Oracle inquiry means that the user initiated a new problem
            if (nextCurrentNode !=null) {
                //update currentNode
                currentNode = nextCurrentNode;
                try {

                	Vector<Instruction> instnVector = new Vector<Instruction>();
                	controller.setCurrentNode2(currentNode);
                    trace.out("miss", "setCurrentNode2 to " + currentNode + " suceeded");
                    Instruction instn = simSt.lookupInstructionWithNode(controller.getCurrentNode());
        			instnVector.clear();
        			instnVector.add(instn);
                    trace.out("ss","opSequenceFound is: " + simSt.searchRhsOpsFor(instnVector));
                } catch (Exception e) {
                    trace.out("miss", "setCurrentNode2 to " + currentNode + " failed");
                }
                
                if(currentNode.getIncomingEdges().size() > 0)
                {
	                ProblemEdge edge = (ProblemEdge)currentNode.getIncomingEdges().get(0);
	                String input = edge.getInput();
	                if (EqFeaturePredicate.isValidSimpleSkill(input.split(" ")[0])) {
	                    simSt.setLastSkillOperand(input);
	                }
	                
	                if(controller.getMissController().isPLEon())
	                {
	                	JButton undoButton = controller.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getUndoButton();
	                	if(edge.getSelection().equalsIgnoreCase(Rule.DONE_NAME))
	                		undoButton.setText(controller.getMissController().getSimStPLE().getUndoButtonTitleString("done"));
	                	else
	                		undoButton.setText(controller.getMissController().getSimStPLE().getUndoButtonTitleString(input));
	                }
	                
                }

                
            } else { 
                
                killMessageReceived = true;
                simSt.setIsInteractiveLearning(false);
                trace.out("ss", "killMessageReceived is true.");
                //Finish SimStudent thinking

                if(controller.getMissController().isPLEon() && !controller.getMissController().getSimStPLE().isStartStatus())
                {
                	controller.getMissController().getSimStPLE().blockInput(true);
                }
            }
                
        
            trace.out("ss", "Current Node at end of loop: "+currentNode+":"+controller.getCurrentNode());
            //Do not continue after done state
            if(currentNode.getDoneState())
            {
            	break;
            }
        }
        for(String str:scores.keySet())
        {
        	trace.out(str+" "+scores.get(str));
        }
    }
	
    public String getScoreString()
    {
        String results = "";
        for(String str:scores.keySet())
        {
        	results += str+":"+scores.get(str)+" ";
        }
        return results;
    }
    
	public ProblemNode contest(ProblemNode problemNode)
	{
		String results = "";
		PriorityQueue<Answer> answers = gatherActivationList(problemNode);
		if(answers.isEmpty())
		{
			trace.out("ss", "No answers");
			return null;
		}
		//Answer answer = answers.remove();
		Answer goodAnswer = null;
		while(!answers.isEmpty())
		{
			Answer answer = answers.remove();
			trace.out(answer.toString());
			results+= answer.toString()+"\n";
			if(goodAnswer == null)
			{
				String result = simSt.builtInInquiryClTutor(answer.step.getActualSelection(), answer.step.getActualAction(),answer.step.getActualInput(), problemNode, problemNode.getProblemModel().getProblemName().replace("_", "="));
				//JOptionPane.showMessageDialog(null,problemNode.getProblemModel().getProblemName());
				if(result.startsWith("Correct"))
					goodAnswer = answer;
			}
		}
		trace.out(results);
		if(goodAnswer == null)
		{
			AskHint hint = new AskHintInBuiltClAlgebraTutor(brController, problemNode);
			return advanceNode(problemNode, hint.getSai(), hint.getSkillName());
		}
		else
		{
			scores.put(goodAnswer.student, scores.get(goodAnswer.student)+1);
			return advanceNode(problemNode, goodAnswer.step);
		}
		
		//return null;
	}
	
	private ProblemNode advanceNode(ProblemNode currentNode, RuleActivationNode ran) {

        ProblemNode nextCurrentNode = null;

        // Make sure that currentNode is the current node
        secureCurrentNode(currentNode);

        Sai sai = getSai(ran);
        ProblemNode successiveNode = simulatePerformingStep(currentNode, sai, false);

        ProblemNode startNode = brController.getProblemModel().getStartNode();
        String step = startNode.getName();
        if(simSt.getProblemAssessor() != null)
        	step = simSt.getProblemAssessor().findLastStep(startNode, currentNode);
        simSt.setProblemStep(step);

        //how could successiveNode be null?
        if (successiveNode != null) {

            String skillName = removeAmpersand(ran.getName()) + " simStIL";
            ProblemEdge edge = updateEdgeSkillName(currentNode, successiveNode, skillName);
        
            nextCurrentNode = successiveNode;

        }
            
        return nextCurrentNode;
        
	}

	private ProblemNode advanceNode(ProblemNode currentNode, Sai sai, String skillName) {

        ProblemNode nextCurrentNode = null;

        // Make sure that currentNode is the current node
        secureCurrentNode(currentNode);

        ProblemNode successiveNode = simulatePerformingStep(currentNode, sai, false);

        ProblemNode startNode = brController.getProblemModel().getStartNode();
        String step = startNode.getName();
        if(simSt.getProblemAssessor() != null)
        	step = simSt.getProblemAssessor().findLastStep(startNode, currentNode);
        simSt.setProblemStep(step);

        //how could successiveNode be null?
        if (successiveNode != null) {

            ProblemEdge edge = updateEdgeSkillName(currentNode, successiveNode, skillName);
        
            nextCurrentNode = successiveNode;

        }
            
        return nextCurrentNode;
        
	}

	
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
    	return gatherActivationList(problemNode, 0, prodRules.size());
    }

    public PriorityQueue<Answer> gatherActivationList(ProblemNode problemNode, int firstStudent, int nMaxStudents) {

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
	            
	            // MTRete mtRete = getBrController().getModelTracer().getRete();
	            // trace.out("miss", "&&&&& mtRete.hashCode() = " + mtRete.hashCode());
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
	            
	            //Go through and allow each student to figure production rules
		      for(int student=firstStudent;student<prodRules.size()&&student<nMaxStudents;student++)
		      {
		    	  activationList.clear(); //Clear the activation list for the next student's answers
		            boolean result = true;
	            	//File prFile = new File("productionRules2.pr");
					try
					{
						controller.getModelTracer().getRete().removeAllRules();
					}catch(Exception e){ e.printStackTrace(); }
					result = controller.getModelTracer().getRete().reloadProductionRulesFile(prodRules.get(student),false);
	
		            showActivationList();
		            
		            // Get a root rule-activation node
		            RuleActivationTree tree = controller.getRuleActivationTree();
		            TreeTableModel ttm = tree.getActivationModel();
		            RuleActivationNode root = (RuleActivationNode) ttm.getRoot();
		
		            // Save the current state
		            //MTRete mtRete = retes.get(reteNum);
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
		            
		
		            
		            for (int i = 0; i < children.size(); ++i) {
		                RuleActivationNode child = (RuleActivationNode)children.get(i);
		                // mtRete.dumpAgenda("gatherActivationList: before setUpState[" + i + "]");
		                root.setUpState(mtRete, i);
		                // mtRete.dumpAgenda("gatherActivationList:  after setUpState[" + i + "]");
		
		                jmt.fireNode(child);                //child.fire(mtRete);
		                activationList.add(child);
		            }
		
		     
		            for(int i=0;i<activationList.size();i++)
			        {
		            	int rand = (int)(Math.random()*prodRules.size());
		            	if(!activationList.get(i).getActualInput().equals("FALSE"))
		            		results.add(new Answer(studentName[student], i*prodRules.size()+rand, activationList.get(i)));
			        }
			        trace.out("Student "+student+" finished");
			        
		      }	
	    	
	      
	      }
	      catch(Exception e)
	      {
	        	e.printStackTrace();
	      } 
	     
        return results;
    }
    
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
    
    class Solution
    {
    	String solutionPath = "";
    	String answer = "";
    	int steps = 0;
    	boolean correctness = true;
    }
    
    
  //runs interactive learning on current node
  /*  public Solution solveWholeProblem(int student) {

        Solution solution = new Solution();
    	ProblemNode currentNode = controller.getProblemModel().getStartNode();

        boolean killMessageReceived = false; //whether a message to kill the IL thread has been received
        

        while (!killMessageReceived) {
            
        	String step =
        	simSt.setProblemStepString(step);
        	
        	trace.out("ss", "----------------"+step);
        	
            ProblemNode nextCurrentNode = null;
            
            
	            //TODO Temporary
	          
	            
	            //TODO End Temporary
	            
	            //Vector activationList = simSt.gatherActivationList(currentNode);
	            
	           
	            //nextCurrentNode = inspectAgendaRuleActivations(currentNode, activationList);
            
            nextCurrentNode = contest(currentNode);
             
            // Null nextCurrentNode after the Oracle inquiry means that the user initiated a new problem
            if (nextCurrentNode !=null) {
                //update currentNode
                currentNode = nextCurrentNode;
                try {

                	Vector<Instruction> instnVector = new Vector<Instruction>();
                	controller.setCurrentNode2(currentNode);
                    trace.out("miss", "setCurrentNode2 to " + currentNode + " suceeded");
                    Instruction instn = simSt.lookupInstructionWithNode(controller.getCurrentNode());
        			instnVector.clear();
        			instnVector.add(instn);
                    trace.out("ss","opSequenceFound is: " + simSt.searchRhsOpsFor(instnVector));
                } catch (Exception e) {
                    trace.out("miss", "setCurrentNode2 to " + currentNode + " failed");
                }
                
                if(currentNode.getIncomingEdges().size() > 0)
                {
	                ProblemEdge edge = (ProblemEdge)currentNode.getIncomingEdges().get(0);
	                String input = edge.getInput();
	                if (EqFeaturePredicate.isValidSimpleSkill(input.split(" ")[0])) {
	                    simSt.setLastSkillOperand(input);
	                }
	                
	                if(controller.getMissController().isPLEon())
	                {
	                	JButton undoButton = controller.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getUndoButton();
	                	if(edge.getSelection().equalsIgnoreCase(Rule.DONE_NAME))
	                		undoButton.setText(controller.getMissController().getSimStPLE().getUndoButtonTitleString("done"));
	                	else
	                		undoButton.setText(controller.getMissController().getSimStPLE().getUndoButtonTitleString(input));
	                }
	                
                }

                
            } else { 
                
                killMessageReceived = true;
                simSt.setIsInteractiveLearning(false);
                trace.out("ss", "killMessageReceived is true.");
                //Finish SimStudent thinking

                if(controller.getMissController().isPLEon() && !controller.getMissController().getSimStPLE().isStartStatus())
                {
                	controller.getMissController().getSimStPLE().blockInput(true);
                }
            }
                
        
            trace.out("ss", "Current Node at end of loop: "+currentNode+":"+controller.getCurrentNode());
            //Do not continue after done state
            if(currentNode.getDoneState())
            {
            	break;
            }
        }
        for(String str:scores.keySet())
        {
        	trace.out(str+" "+scores.get(str));
        }
        
        return solution;
    }*/
    
    
    private Solution solveWholeProblem(int student) {
    	
    	ProblemNode currentNode = controller.getProblemModel().getStartNode();

        boolean killMessageReceived = false; //whether a message to kill the IL thread has been received
       
        Solution solution = new Solution();
        
        //when running not from a BRD, it never gets "done" - reaching a done state breaks out of loop
        while (!killMessageReceived) {
            ProblemNode startNode = currentNode.getProblemModel().getStartNode();
            Vector<ProblemEdge> pathEdges = findPathDepthFirst(startNode, currentNode);
            String step = simSt.getStepNameGetter().getStepName(pathEdges, startNode);
        	simSt.setProblemStepString(step);
        	
            ProblemNode nextCurrentNode = null;
            
            //Should gather activation list
            PriorityQueue<Answer> activationList = gatherActivationList(currentNode, student, 1);
	           
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
	                
	                //Update undo button to mention last completed step
	                if(brController.getMissController().isPLEon())
	                {
	                	JButton undoButton = brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getUndoButton();
	                	if(edge.getSelection().equalsIgnoreCase(Rule.DONE_NAME))
	                		undoButton.setText(brController.getMissController().getSimStPLE().getUndoButtonTitleString("done"));
	                	else
	                		undoButton.setText(brController.getMissController().getSimStPLE().getUndoButtonTitleString(input));
	                }
                }
            } else { 
            	//next current node is null, stop learning on this problem
                
                killMessageReceived = true;
                simSt.setIsInteractiveLearning(false);
                trace.out("ss", "killMessageReceived is true.");
                
            }
           
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

 
    // Given a rule activationList, test each rule activation and take the first successrul
    // activation as a representative step performance. 
    // Blame all false rule firing and signal negative example.
    private ProblemNode inspectAgendaRuleActivations(ProblemNode currentNode, PriorityQueue<Answer> activationList, Solution solution) {

        ProblemNode nextCurrentNode = null;
        //ProblemNode backupNode = null;
        RuleActivationNode backupRan = null;

        //Tracks all activation rules looked at, but nothing is currently done to display this info
        String listAssessment = "";
        
        //Go through full activationList and inspect each until a good activation is found
        for (Answer ans: activationList) {
            RuleActivationNode ran = ans.step;
            
            //Do not process FALSE inputs, just move on to next
            if(ran.getActualInput().equalsIgnoreCase("FALSE"))
            	continue;
            if(nextCurrentNode != null)
            	break;
            String result = simSt.builtInInquiryClTutor(ran.getActualSelection(), ran.getActualAction(),ran.getActualInput(), currentNode, currentNode.getProblemModel().getProblemName().replace("_", "="));
			if(result.startsWith("Correct"))
			{
				nextCurrentNode = advanceNode(currentNode, ran);
				solution.solutionPath = solution.solutionPath+" "+ran.getActualInput();
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
        	solution.correctness = false;
        	nextCurrentNode = advanceNode(currentNode, backupRan);        	
        }
        else
        {
        	listAssessment+= "Not backup used\n";
        }


        return nextCurrentNode;
    }
    
    
	
}
