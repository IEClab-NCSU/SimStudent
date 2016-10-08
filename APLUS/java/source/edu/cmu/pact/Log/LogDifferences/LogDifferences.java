package edu.cmu.pact.Log.LogDifferences;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Log.LogConsole;
import edu.cmu.pact.Log.TutorActionLogV4;

public class LogDifferences {

	//=========================================================================
	//	Fields
	//=========================================================================
	
	List<Contents> oldMessages = new ArrayList<Contents>();
	List<Contents> newMessages = new ArrayList<Contents>();
	
	//=========================================================================
	//	Public methods
	//=========================================================================

	public void addTutorMessagePair(TutorActionLogV4 oldMsg, TutorActionLogV4 newMsg){
		oldMessages.add(new TutorMessageContents(oldMsg));
		newMessages.add(new TutorMessageContents(newMsg));
	}
	
	/**
	 * @return ArrayList of String[]. The first entry holds the column headers.
	 * Every subsequent 2 elements are a pair of old and new messages compared to each other.
	 */
	public ArrayList<String[]> exportResults(){
		ArrayList<String[]> output = new ArrayList<String[]>();
		
		//make and add the column headers
		AlphabetizedCustomsColumn column = new AlphabetizedCustomsColumn(oldMessages, newMessages);
		output.add(column.toArray());
		
		//make and add the basic row and then the comparison row
		for(int i = 0; i < oldMessages.size(); i++){
			BasicRow oldRow = new BasicRow(oldMessages.get(i), column);
			output.add(oldRow.toArray());
			
			ComparisonRow newRow = new ComparisonRow(newMessages.get(i), oldRow, column);
			output.add(newRow.toArray());
		}
		
		return output;
	}
	
	public void writeToFile(BR_Controller controller, LogConsole logConsole, String sessionId){
		LogDifferencesWriter writer = 
				new LogDifferencesWriter(exportResults(), controller, logConsole, sessionId);
		
		writer.writeDifferencesToFile();
	}
}
