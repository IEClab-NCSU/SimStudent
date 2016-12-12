package TabbedTest.recover;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadFileDialog;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.storage.StorageClient;

public class RecoverFileHandler {

	/*String to store the recover filename*/
	private String recoverFilename="test.txt";
	private void setRecoverFilename(String recoverFilename){this.recoverFilename=recoverFilename;}
	public String getRecoverFilename(){return this.recoverFilename;}
	
	/*boolean to indicate if we are on webstart mode*/
	private boolean webstartMode=true;
	public boolean isWebstartMode(){return webstartMode;}
	private void setIsWebstartMode(boolean flag){this.webstartMode=flag;}
	
	private boolean isFirstAttempt=false;
	public boolean getIsFirstAttempt(){return isFirstAttempt;}
	public void setIsFirstAttempt(boolean flag){this.isFirstAttempt=flag;}
	
	/**
	 * Constructor
	 * @param brController 
	 */
	public RecoverFileHandler(BR_Controller brController){
		String testVersion=(String) brController.getProperties().getProperty(LoadFileDialog.PROBLEM_FILE_LOCATION);
		boolean webstart=brController.getMissController().getSimSt().isWebStartMode();
		//String filename=brController.getMissController().getSimSt().getUserID()+"_Recover"+trimBrdFilename(testVersion)+".txt";
		String filename=brController.getMissController().getSimSt().getUserID()+"_Recover.txt";
		//setIsWebstartMode(webstart);
		setRecoverFilename(filename);
	}
	
	
	/**
	 * Remove extension from brd filename (each test has different brd so we differentiate the recovery files for each user
	 * on the server using the brd name). 
	 * @param brdFilename
	 * @return
	 */
	private String trimBrdFilename(String brdFilename){
		String returnValue;
		if (brdFilename==null){ //in case something goes wrong and brd filename in not yet set...
			Calendar now = Calendar.getInstance();
			int day=now.get(Calendar.DAY_OF_MONTH);
			brdFilename=Integer.toString(day);
			return brdFilename;
		}
		returnValue=brdFilename.substring(0, brdFilename.lastIndexOf('.'));
		returnValue=returnValue.substring(returnValue.lastIndexOf('/')+1,returnValue.length());
		return returnValue;
	}
	
	/**
	 * Method to save selection to the recovery file
	 * @param filename
	 */
	public void saveStudentActionToFile(String selection, String action, String input){	
		String filename=getRecoverFilename();

		if (isWebstartMode())
			filename=WebStartFileDownloader.SimStWebStartDir + getRecoverFilename();
		
		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		FileWriter fw;
		try {
			fw = new FileWriter(file.getAbsoluteFile(),true);
		
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(selection + "," + action + "," + input + "\n");
		bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*if on webstart mode, upload the file as well*/
		if (isWebstartMode()){		
			File acFile = new File(filename);			
			StorageClient sc = new StorageClient();
        	try {
				sc.storeFile(getRecoverFilename(),acFile.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Method to download (if on WebStart mode) the recover file from the server
	 * @return false if an error occurred during download, true if file downloaded or not on WebStart mode
	 * @throws IOException
	 */
	public boolean retrieveRecoverFile() throws IOException{
		boolean returnVal=true;
		/*If on webstart mode, download the recover file from the server*/
		if (this.isWebstartMode()){
	        	StorageClient sc = new StorageClient();
	        	returnVal=sc.retrieveFile(this.getRecoverFilename(), this.getRecoverFilename(),WebStartFileDownloader.SimStWebStartDir);
	        	if (!returnVal){
	        		trace.err("Unable to download recover file "+this.getRecoverFilename());
	        		this.setIsFirstAttempt(true);
	        	}
	        	
	     }
		return returnVal;
	}
	
	
	
	
}
