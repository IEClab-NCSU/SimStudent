package writeCSVFile;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.CSVWriter;

public class CsvFileGenerater {
	
	public static void csvFileGenerate(ArrayList<String[]> strs) {
		
		String newCsv = "OpenEdxData.csv";
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(newCsv));
			String[] firstLine = new String[] {"Anon Student Id", "Session Id", "Time", "Time Zone", 
					"Student Response Type", "Student Response Subtype", "Tutor Response Type", 
					"Tutor Response Subtype", "Level ()", "Problem Name", "Problem View", "Problem Start Time", 
					"Step Name", "Attempt At Step", "Outcome", "Selection", "Action", "Input", "Feedback Text", "Feedback Classification", 
					"Help Level", "Total Num Hints", "Condition Name", "Condition Type", "KC ()", "KC Category ()", "School", "Class", "CF ()"};
			writer.writeNext(firstLine);
			
			// start to write all the column values to csv file
			for(int i = 0; i < strs.size(); i++) {
				writer.writeNext(strs.get(i));
			}
			
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
	}
	
	
}
