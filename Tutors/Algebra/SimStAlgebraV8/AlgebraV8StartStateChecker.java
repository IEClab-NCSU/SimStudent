package SimStAlgebraV8;


import edu.cmu.pact.miss.StartStateChecker;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.PeerLearning.SimStSolver;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.Sai;
import java.util.Vector;
import edu.cmu.pact.miss.PeerLearning.SimStSolver.SolutionStepInfo;
import jess.JessException;
import edu.cmu.pact.miss.WebStartFileDownloader;

public class AlgebraV8StartStateChecker extends StartStateChecker {
 	 public final static String EXPERT_PRODUCTION_RULE_FILE = "productionRulesExpert.pr";

	private static char[] invalidVariables = {'d', 'e', 'f', 'l', 'D', 'E', 'F', 'L'};

	 
	 public boolean checkStartState(String problem, BR_Controller brController){
	 	boolean returnValue=true;

	 	String jessFilesDirectory=brController.getPreferencesModel().getStringValue(CTAT_Controller.PROBLEM_DIRECTORY);	
		String productionRulesFile=null;
		String wmeTypesFile=null;
		String initialFactsFile=null;
		String pleConfig=null;

		boolean variableSeen  = false;
		char variableChar = '\0';





	/*Check if problem has more than one variables*/
	if (problem!=null ){
		for(int i=0;i<problem.length();i++)
		{
			char current = problem.charAt(i);
		
				if(Character.isLetter(current)) 
			{
				for(int j = 0; j < invalidVariables.length; j++) 
				{
					if(current == invalidVariables[j]) 
					{
						return false;///formatInvalidVariableUsedMessage();
					}
				}

				if(!variableSeen)
				{
					variableChar = current;
					variableSeen = true;
				} 
				else 
				{
					if(current != variableChar)
						return false;//"You can use only one letter as a variable term in the equation.";
					else if(current == problem.charAt(i-1) && i > 0)
						return false;//"Did you forget to put an operator between " + current + " ?";
				}
			}
		}
	
	
	}

System.out.println("***Trying to validate start state!!!!!  1");
	if (brController.getMissController().getSimSt().isWebStartMode()){
		 productionRulesFile=WebStartFileDownloader.SimStWebStartDir +"/"+ EXPERT_PRODUCTION_RULE_FILE;
		 wmeTypesFile=WebStartFileDownloader.SimStWebStartDir +"/"+ SimSt.WME_TYPE_FILE;  
		 initialFactsFile=WebStartFileDownloader.SimStWebStartDir +"/"+  SimSt.INIT_STATE_FILE; 
		 pleConfig=WebStartFileDownloader.SimStWebStartDir +"/"+SimStPLE.CONFIG_FILE;

	}	
	else{
		productionRulesFile=jessFilesDirectory+ EXPERT_PRODUCTION_RULE_FILE;
		wmeTypesFile=jessFilesDirectory+ SimSt.WME_TYPE_FILE;  
		 initialFactsFile=jessFilesDirectory+  SimSt.INIT_STATE_FILE; 
		 pleConfig=brController.getMissController().getSimSt().getProjectDir()+"/"+SimStPLE.CONFIG_FILE;
		 }


		String quizProblem=SimSt.convertFromSafeProblemName(problem);
 
		/*Create new solver to solve the current problem*/
		SimStSolver solver=new SimStSolver(quizProblem,productionRulesFile,wmeTypesFile, initialFactsFile, pleConfig, brController.getMissController().getSimSt());
		Vector<Sai> solution=new Vector();
		Vector<SolutionStepInfo> solutionStepsInfo=new Vector<SolutionStepInfo>();

		try {
			solver.createStartState();
			solution=solver.solve();
		} catch (Exception e1) {
			returnValue=false;
			
		System.out.println("***Trying to validate start state: An exception occurred!");

		}
		
		
		System.out.println("***Trying to validate start state!!!!!  5");
			
		if (problem.contains("--") || problem.contains("*-") || problem.contains("++") || problem.contains("**"))
			returnValue=false;
		
	
		System.out.println("***Start state validated, outcome is " + returnValue);
				
	 return returnValue;
	 
	 }





}