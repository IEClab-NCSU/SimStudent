package edu.cmu.pact.Log.LogDifferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.DialogUtilities;
import edu.cmu.pact.BehaviorRecorder.Dialogs.TxtFilter;
import edu.cmu.pact.Log.LogConsole;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;

public class LogDifferencesWriter {
	//=========================================================================
	//	Fields
	//=========================================================================
	
	ArrayList<String[]> file;
	BR_Controller controller;
	LogConsole logConsole;
	String sessionId = null;//use this for the file name. There is one unique sessionId per log file

	//=========================================================================
	//	Constructors
	//=========================================================================
	
	public LogDifferencesWriter(ArrayList<String[]> file, BR_Controller controller, LogConsole logConsole, String sessionId) {
		this.file = file;
		this.controller = controller;
		this.logConsole = logConsole;
		this.sessionId = sessionId;
	}
	
	//=========================================================================
	//	Write method
	//=========================================================================

	//Much of this code was taken from LogServlet
	public void writeDifferencesToFile() {//taken and modified from LogServlet
		String dirName = getPreference(BR_Controller.DISK_LOGGING_DIR, Logger.DISK_LOG_DIR_PROPERTY);
		String fileName = sessionId+"_replay.txt";
		do {
			File chosenFile = DialogUtilities.chooseFile(dirName, fileName, new TxtFilter(),
					"Please set the file name", "Save", controller); 
			if (chosenFile == null)
				return;
			if (chosenFile.exists()) {
				int overwrite =
						JOptionPane.showConfirmDialog(controller.getCtatFrameController().getDockedFrame(),
								"File "+chosenFile.getPath()+" already exists. Overwrite?",
								"Overwrite prior log", JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.WARNING_MESSAGE);
				if (overwrite == JOptionPane.CANCEL_OPTION)
					return;
				dirName = chosenFile.getParent();
				if (overwrite == JOptionPane.NO_OPTION)
					continue;
			}
			try {
				//Actual writing method here.
				writeDifferencesHelper(chosenFile);
				
			} catch (Exception e) {
				String errMsg = "Error saving log difference "+chosenFile.getPath();
				trace.errStack(errMsg, e);
				Utils.showExceptionOccuredDialog(e, errMsg, "Error saving log difference");
			}
			return;
		} while (true);
	}

	//=========================================================================
	//	Private methods
	//=========================================================================

	private void writeDifferencesHelper(File chosenFile) throws IOException {
		FileWriter fw = new FileWriter(chosenFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		Iterator<String[]> rows = file.iterator();
		
		//Add column + new line
		if(rows.hasNext()){
			bw.write(arrayToString(rows.next()));
			bw.newLine();
			bw.newLine();
		}
		
		while(rows.hasNext()){//assuming there can only be pairs of 2 from here on
			bw.write(arrayToString(rows.next()));
			bw.newLine();
			bw.write(arrayToString(rows.next()));
			
			bw.newLine();
			bw.newLine();
		}
		bw.close();
	}
	
	private String arrayToString(String[] row){
		StringBuilder concat = new StringBuilder();
		for(int i = 0; i < row.length; i++){
			if(row[i] != null){
				concat.append(row[i]);
			}
			
			concat.append("\t");
		}
		
		return concat.toString();
	}

	/**
	 * Return a String preference from {@link #prefs}, possibly overridden by {@link #setPrefsMsg}. 
	 * @param prefName preference name for {@link PreferencesModel#getStringValue(String)}
	 * @param propName if not null, property name for {@link MessageObject#getProperty(String)}.
	 * @return value from {@link #setPrefsMsg} or {@link #prefs} or null
	 */
	private String getPreference(String prefName, String propName) {
		String result = null;
		result = logConsole.getController().getPreferencesModel().getStringValue(prefName);
		if(result == null || result.length() < 1)
			result = logConsole.getController().getPreferencesModel().getStringValue(propName);
		if (trace.getDebugCode("log")) trace.out("log", "getPref("+prefName+", "+propName+") => "+result);
		return result;
	}
}
