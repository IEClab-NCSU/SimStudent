package TabbedTest.recover;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;

import pact.CommWidgets.JCommWidget;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadFileDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerGraph;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerPath;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.storage.StorageClient;

public class TestRecover {
	/*Hash table with the interface elements*/
	private Hashtable<String, JCommWidget> interfaceElements;
	
	/*RecoverFileHandler, to handle all interactions with the recover file*/
	private RecoverFileHandler fileHandler;
	private void setFileHandler(RecoverFileHandler _fileHander){this.fileHandler=_fileHander;}
	private RecoverFileHandler getFileHandler(){return this.fileHandler;}
	 
	/*BR Controller*/
	BR_Controller controller;
	private void setController(BR_Controller brController){this.controller=brController;}
	private BR_Controller getController(){return this.controller;}
	
	Vector<Sai> studentActions;
	
	/**
	 * Constructor for the TestRecover class
	 * @param interfaceElements2 list of all the interface elements
	 * @param brController brController (to get userID)
	 * @throws IOException 
	 */
	public TestRecover(Hashtable<String, JCommWidget> interfaceElements2, BR_Controller brController) throws IOException{
		interfaceElements=new Hashtable<String, JCommWidget>();
		interfaceElements.putAll(interfaceElements2);	

		/*Initializations*/
		setFileHandler(new RecoverFileHandler(brController));
		setController(brController);	
		getFileHandler().retrieveRecoverFile();	
		getRecoveredActions();

		this.logStudentID();
	}

	 public static final String TEST_STARTED = "Test Started";
	 public static final String TEST_RESTARTED = "Test Restarted";
	 public static final String ONLINE_TEST = "ONLINE_TEST";
	 
	/**
	 * Method that logs the studentID if (logging is enabled)
	 */
	public void logStudentID(){  
		SimStLogger logger=new SimStLogger(getController());	
		
		/*We need to enable logging here for the SimStLogger, because its initialized in SimStPLE initialization. Here we don't have a ple...*/
		String attempt=getFileHandler().getIsFirstAttempt() ? TEST_STARTED: TEST_RESTARTED;
		logger.enableLogging(true, true, getController().getMissController().getSimSt().getUserID());
	    logger.simStLog(ONLINE_TEST, attempt, "", logger.getCurrentTime());
		//logger.enableLogging(false, false, getController().getMissController().getSimSt().getUserID());
        
	}



	/**
	 * Method that populates the studentActions vector.
	 * @throws IOException 
	 */
	public void getRecoveredActions() throws IOException{
		studentActions=new Vector();

		String filename=getFileHandler().getRecoverFilename();

		if (getFileHandler().isWebstartMode())
			filename=WebStartFileDownloader.SimStWebStartDir + filename;

		/*if recover file does not exist, return*/
		File f=new File(filename);
		if (!f.exists()){
			trace.err("File "+filename+" does not exist!");
			return;
		}

		// Open the file
		FileInputStream fstream = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		while ((strLine = br.readLine()) != null)   {
			//each line has the coded as "selection,action,input"
			String[] part=strLine.split(",");
			studentActions.add(new Sai(part[0],part[1],part[2]));

		}
		br.close();

	}
		
	/**
	 * Method that reads the recover file and resumes the test. 
	 * @throws IOException
	 */
	public void resumeTest() throws IOException{
		/*resumeTest only if test is not completed*/
		if (checkIfTestCompleted()) {
		    JOptionPane.showMessageDialog(null, "You have already completed this test! Click Ok to exit.","Online Test", JOptionPane.INFORMATION_MESSAGE);
			getController().closeStudentInterface();
			getController().closeApplication(true);
		}
		
		getController().getPseudoTutorMessageHandler().getExampleTracer().resetTracer();
		
		for (Sai sai:studentActions){
		  String selection=sai.getS();
		  String input=sai.getI();
		  
		  JCommWidget obj=this.interfaceElements.get(selection);	
		  	if (obj instanceof JCommMultipleChoiceRecover){
					((JCommMultipleChoiceRecover) obj).recoverStudentAction(input);
			}
		  	else if (obj instanceof JCommTextFieldRecover){
				((JCommTextFieldRecover) obj).recoverStudentAction(input);
		  	}
		  	else if (obj instanceof JCommTextAreaRecover){
				((JCommTextAreaRecover) obj).recoverStudentAction(input);
		  	}
  
		}
			
	}	
	
	
	/***
	 * Method that checks if actions in recover file are adequate to complete the test.
	 * @return true if test is completed, false otherwise 
	 * @throws IOException
	 */
	public boolean checkIfTestCompleted() throws IOException{
		
		for (Sai sai:studentActions){			
		  /*create sai to see if its correct*/
			Vector selection = new Vector();
			Vector action = new Vector();
			Vector input = new Vector();
		 
			selection.add(sai.getS());
			action.add(sai.getA());
			input.add(sai.getI());
			 getController().getPseudoTutorMessageHandler().getExampleTracer().evaluate(selection, action, input,"Student");	
		}
		
		Vector selection = new Vector();
		Vector action = new Vector();
		Vector input = new Vector();
		
		selection.add("Done");
		action.add("ButtonPressed");
		input.add("-1");
		
		return getController().getPseudoTutorMessageHandler().getExampleTracer().evaluate(selection, action, input,"Student");
		
		
		 /*Short version
		Vector selection = new Vector();
		Vector action = new Vector();
		Vector input = new Vector();
		
		for (Sai sai:studentActions){			
				selection.add(sai.getS());
				action.add(sai.getA());
				input.add(sai.getI());
	
			}

			return getController().pseudoTutorMessageHandler.getExampleTracer().evaluate(selection, action, input,"Student");
		*/
	}	
		
	
}
