package edu.cmu.pact.miss.PeerLearning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import jess.*;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.ConstructMessage;
import edu.cmu.pact.jess.HereIsTheListOfFoas;
import edu.cmu.pact.jess.HereIsTheListOfFoas_SimStSolver;
import edu.cmu.pact.jess.PredictAlgebraInput;
import edu.cmu.pact.jess.PredictAlgebraInput_SimStSolver;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.Rule;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStNode;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStProblemGraph;


/*Class for SimStudent to solve problem
 * User on the Quiz*/
public class SimStSolver {
	
		/*The name of the problem the solver will solve*/
		String problemName=null;
		void setProblemName(String problemName){ this.problemName=problemName;}
		String getProblemName(){return this.problemName;}
		
		/*The rete engine*/
		Rete reteEngine=null;
		void setReteEngine(Rete ssRete){this.reteEngine=ssRete;}
		Rete getReteEngine(){return reteEngine;}
		
		/*String defining the problem delimiter, when creating a start state for the problem*/
		String problemDelimiter="=";
		public void setProblemDelimiter(String delimiter){ this.problemDelimiter=delimiter;	}
		private String getProblemDelimiter(){return this.problemDelimiter;}
		
	
		/*PLE Config file, needed to get the start state elements to initialize the wm*/
		String pleConfigFile=null;
		private void setPleConfigFile(String pleConfigFile){this.pleConfigFile=pleConfigFile;}
		private String getPleConfigFile(){return this.pleConfigFile;}
		
				
		/*Array to store the start state elements (which will be read from the config file)*/
		ArrayList<String> startElements = null;
		public void addStartStateElement(String element){
			startElements.add(element);
		}
		
		/*SimSt necessary to pass it to conflict resolution strategy to orde the rules*/
		SimSt simSt;
		private void setSimSt(SimSt simSt){this.simSt=simSt;}
		private SimSt getSimSt(){return this.simSt;}
		
		
		/*vector containing addition information about the solution (other than sai)*/
		Vector<SolutionStepInfo> solutionInfo=null; 
		public Vector<SolutionStepInfo> getSolutionStepInfo(){return solutionInfo;};
		
		HashSet<String> skillsUsed;
		public HashSet<String> getSkillsUsed(){return skillsUsed;};
		
		
		
		/*================
		 * Constructor
		 =================*/
		/**
		 * Basic constructor of the SimSt solver. 
		 * @param problemName
		 * @param productionRulesFile
		 * @param wmeTypesFile
		 * @param initialFactsFile
		 * @param pleConfigFile
		 * @param simSt
		 * @param rete 	
		 */
		public SimStSolver(String problemName, String productionRulesFile, String wmeTypesFile, String initialFactsFile, String pleConfigFile, SimSt simSt, Rete rete){		
			
			setProblemName(problemName);
			setPleConfigFile(pleConfigFile);
			
			setSimSt(simSt);
			
			startElements=new ArrayList();		
			solutionInfo=new Vector<SolutionStepInfo>();
			skillsUsed = new HashSet<String>();
			
			reteEngine=rete;//
			initializeSolverReteEngine(productionRulesFile,wmeTypesFile,initialFactsFile);
			
		}
	
		/**
		 * Constructor for the SimStSolver class. Uses a default Jess Rete. 
		 * @param problemName
		 * @param productionRulesFile
		 * @param wmeTypesFile
		 * @param initialFactsFile
		 * @param pleConfigFile
		 * @param simSt
		 */
		public SimStSolver(String problemName, String productionRulesFile, String wmeTypesFile, String initialFactsFile, String pleConfigFile, SimSt simSt){		
				this( problemName,  productionRulesFile,  wmeTypesFile,  initialFactsFile,  pleConfigFile,  simSt,new Rete());			
		}
		/*================
		 * Methods
		 =================*/
		
		/**
		 * Method to initialize the rete engine. It loads the jess files, the user defined functions
		 * and initialized the global variables.
		 * @param productionRulesFile
		 * @param wmeTypesFile
		 * @param initialFactsFile
		 */
		public void initializeSolverReteEngine(String productionRulesFile,String wmeTypesFile,String initialFactsFile){
			try {
				loadJessFiles(productionRulesFile,wmeTypesFile,initialFactsFile);
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			loadUserDefinedFunctions();
			
			
			try {
				reteEngine.eval("(defglobal ?*sSelection* = "+SimStRete.NOT_SPECIFIED+")");
				reteEngine.eval("(defglobal ?*sAction* = "+SimStRete.NOT_SPECIFIED+")");
				reteEngine.eval("(defglobal ?*sInput* = "+SimStRete.NOT_SPECIFIED+")");
				reteEngine.eval("(defglobal ?*sSai* = "+SimStRete.NOT_SPECIFIED+")");
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		/**
		 * Method to load the user defined functions
		 */
		private void loadUserDefinedFunctions(){
			
			reteEngine.addUserfunction( new HereIsTheListOfFoas_SimStSolver());
			reteEngine.addUserfunction( new PredictAlgebraInput_SimStSolver());
			reteEngine.addUserfunction( new ConstructMessage());
			
		}
		
		
		/**
		 * Method to initialize the working memory for the problem specified in the constructor
		 * @throws JessException
		 */
		public void createStartState() throws JessException{

			/*read the start state elements from the config file*/
			BufferedReader reader=null;
			try
			{
				reader=new BufferedReader(new FileReader(this.getPleConfigFile()));
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
			}


			
			
			/*Update the working memory*/
			String[] problem=getProblemName().split(problemDelimiter);
			if (!problem[0].isEmpty()){
				for (int i=0;i<startElements.size();i++){	
					this.updateWorkingMemory(startElements.get(i), problem[i]);

				}				
			}	

		}
		
		
		/**
		 * Method to load the jess files (i.e. init facts file, types, production rules)
		 * @throws JessException 
		 */
		private void loadJessFiles(String productionRulesFile, String wmeTypesFile, String initialFactsFile ) throws JessException{

			reteEngine.batch(initialFactsFile);
			reteEngine.batch(wmeTypesFile);
			reteEngine.batch(productionRulesFile);
		
		}
		 
		
		/***
		 * Main method to solve a problem
		 * @return
		 * @throws JessException
		 */
		public Vector<Sai> solve() throws JessException{
			int runRules=-2;
			
		
			reteEngine.setStrategy(new SuccessRatioConflictResolutiionStrategy(simSt));
		
			//reteEngine.eval("(watch facts)");
			String selection="";
		
		     /*Vector of sai's to hold the solution*/
			 Vector<Sai> solution=new Vector<Sai>();
	
			 
			 //keep a list of all previous selections.
			 Set<String> selections = new TreeSet<String>();
			 
			 //HashMap agendaPerStep = new HashMap(); //this is used for debug purposes only
			while (runRules!=0 && !selection.equalsIgnoreCase("done")){
			
				/*get the name of the rule about to fire*/
				String ruleAboutToFire=getFiringRule();
				
				if (ruleAboutToFire.equals("")) break;
				
				//Vector<String> agenda=this.getAgendaWithRating();  //for debuging purposes
				
				/*run the engine (i.e. fire the rule), returning the number of rule fired.*/
				runRules=reteEngine.run(1);
			
				/*get the sai*/
				Value sai_value=reteEngine.eval("?*sSai*"); 
				Sai step_sai=(Sai) sai_value.javaObjectValue(reteEngine.getGlobalContext());

				selection=step_sai.getS();
				if (!selections.contains(selection)){
					selections.add(selection);
					solution.add(step_sai);
					solutionInfo.add(new SolutionStepInfo(step_sai,ruleAboutToFire,getAgenda()));
					skillsUsed.add(ruleAboutToFire.replace("MAIN::", ""));
				}

			}
		    
			return solution;
		}
		
		
		/**
		 * Method to print the solution in a pretty format (debug flag -nq must be set)
		 * @param saiVec
		 * @param agendaMap
		 */
		void printSolution(Vector<Sai> saiVec, HashMap agendaMap){

			for (int i=0;i<saiVec.size();i++){
				trace.out(saiVec.get(i).toString());
				Vector<String> agenda=(Vector<String>) agendaMap.get(saiVec.get(i).getS());

				for (int j=0;j<agenda.size();j++)
					if(trace.getDebugCode("nq")) trace.out("         "+agenda.get(j));

			}


		}
		
		
		
		/**
		 * Method to get all the facts of the Rete, used for debugging purposes
		 * @return
		 */
		public ArrayList getFacts() {
			ArrayList facts = new ArrayList();
			for(Iterator it = reteEngine.listFacts(); it.hasNext();) {
				facts.add(it.next());
			}
			return facts;
		}


		/**
		 * Method that returns the rule which is about to fire (i.e. the first rule in the agenda)
		 * @return
		 */
		public String getFiringRule(){

			String returnValue="";
			Iterator it = reteEngine.listActivations();
			Activation topActivation;
			if (it.hasNext()){
				topActivation= (Activation) it.next();
				returnValue= topActivation.getRule().getName();
			}
			return returnValue;

		}


		/**
		 * Method that returns the agenda, along with the score of each rule. Used 
		 * for debugging purposes only
		 * @return
		 */
		public Vector<String> getAgenda(){
			int i=0;
			Vector<String> agenda=new Vector<String>();

			for (Iterator it = reteEngine.listActivations(); it.hasNext(); ++i) {    	
				Activation act = (Activation) it.next();
				agenda.add(act.getRule().getName());
			}


			return agenda;
		}



		/**
		 * Method that returns the agenda, along with the score of each rule. Used 
		 * for debugging purposes only
		 * @return
		 */
		public Vector<String> getAgendaWithRating(){
			int i=0;
			Vector<String> agenda=new Vector();
			for (Iterator it = reteEngine.listActivations(); it.hasNext(); ++i) {    	
				Activation act = (Activation) it.next();

				String rule1Name = Rule.getRuleBaseName(act.getRule().getName()).replaceAll("MAIN::", "");
				Rule rule1 = simSt.getRule(rule1Name);


				Vector rule1Foas=((SuccessRatioConflictResolutiionStrategy) reteEngine.getStrategy()).ruleFoas.get(rule1Name);

				double rule1Rating = rule1.getAcceptedRatio()+rule1.getSelectionAcceptRatio(null);

				agenda.add(act.getRule().getName() + ", Overall=" + rule1Rating + " ["+rule1.getAcceptedRatio()+" + "+rule1.getSelectionAcceptRatio(null)+"]");

			}


			return agenda;
		}



		/**
		 * Method to update the working memory of the rete.
		 * @param element
		 * @param newValue
		 * @return
		 * @throws JessException
		 */
		public boolean updateWorkingMemory(String element, String newValue) throws JessException{
			boolean returnValue=false;
			/*go get the fact*/
			Fact fact = getFact(element);
			if (fact==null)
				return returnValue;

			//modify its value
			reteEngine.modify(fact, "value", new Value(newValue,RU.STRING));

			return true;	 
		}




		/**
		 * Method to get a specific fact specified by the element
		 * @param element
		 * @return
		 * @throws JessException
		 */
		public Fact getFact(String element) throws JessException{
			Fact fact = null;
			Iterator it= reteEngine.listFacts();	//get all facts
			while(it.hasNext()){
				fact = (Fact) it.next();
				if (fact.getDeftemplate().getSlotIndex("name") != -1) {			//if fact has a "name" slot
					Value v = fact.getSlotValue("name");						//get the name
					if (v.stringValue(null).trim().equalsIgnoreCase(element)) { //if it matches what we are looking for then break
						break;
					}
				}
				fact = null;	 
			}
			return fact;
		}



		/**
		 * Method to get the facts of the engine (Used for debugging puprposes)
		 * @return
		 */
		public ArrayList getReteFacts() {
			ArrayList facts = new ArrayList();
			for(Iterator it = this.reteEngine.listFacts(); it.hasNext();) {
				facts.add(it.next());
			}
			return facts;
		}


		/**
		 * Structure to hold a solution step. Method {@link #solve()} used to return 
		 * only sai's, but it turned out populating the quiz graph needs not only
		 * the sai in each step but also the rule name
		 * @author simstudent
		 *
		 */
		public class SolutionStepInfo{
			public Sai sai;
			public String firedRuleName;	
			public Vector<String> agenda;

			SolutionStepInfo(Sai sai, String rulename, Vector<String> agenda){
				firedRuleName=rulename;
				this.sai=sai;
				this.agenda=agenda;
			}

		}




		/**
		 * Conflict resolution strategy that orders the agenda according to the overall success ratio
		 * of each rule.
		 * 
		 * @author simstudent
		 *
		 */
		public static class SuccessRatioConflictResolutiionStrategy implements Strategy, Serializable {

			private static final long serialVersionUID = 1L;
			
			SimSt simSt;
			public HashMap<String, Vector<String>> ruleFoas;

			public SuccessRatioConflictResolutiionStrategy(SimSt simSt){
				super();
				this.simSt=simSt;		
				this.ruleFoas=ruleFoas;

			}




			/**
			 * Conflict resolution strategy that orders rules in the agenda based on their "success ration"
			 * @param act1
			 * @param act2
			 * @return -1 means act1 should be before act2
			 * 		 0 means they are the same in order
			 * 		 1 means act1 should be after act2
			 */
			public int compare(Activation act1, Activation act2) 
			{

				String rule1Name = Rule.getRuleBaseName(act1.getRule().getName()).replaceAll("MAIN::", "");
				Rule rule1 = simSt.getRule(rule1Name);
			
				double rule1Rating=0;
				if (rule1!=null)
					rule1Rating = rule1.getAcceptedRatio();	

				String rule2Name = Rule.getRuleBaseName(act2.getRule().getName()).replaceAll("MAIN::", "");
				Rule rule2 = simSt.getRule(rule2Name);
				
				double rule2Rating =0;
				
				if (rule2!=null)
					rule2Rating=rule2.getAcceptedRatio(); 



				if(rule1Rating < rule2Rating) return 1;
				if(rule1Rating > rule2Rating) return -1;
				if(rule1!=null && rule2!=null && (rule1.getUses() < rule2.getUses())) return 1;
				if(rule1!=null && rule2!=null && (rule1.getUses() > rule2.getUses())) return -1;

				int compare = rule1Name.compareTo(rule2Name);

				if(compare == 0 && !act1.equals(act1)) return 1;

				return compare;

			}

			@Override
			public String getName() {
				return "strategy_success_ratio";
			}


		}
		
	
}
