package SimStAlgebraV8;

import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommTable.TableCell;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.InterfaceElementGetter;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;

public class AlgebraV8AdhocInterfaceElementGetter implements InterfaceElementGetter {

	/**	Configuration file specifying domain dependent element names, foa names, component names */
	private static final String CONFIG_FILE = "simSt-config.txt";

	private ArrayList<String> startStateElements;

	public AlgebraV8AdhocInterfaceElementGetter() {
		if(trace.getDebugCode("rr"))
			trace.out("rr", "Empty constructor of AlgebraV8AdhocInterfaceElementGetter");
		init();
	}
	
	private void init() {
		
		String file = WebStartFileDownloader.SimStAlgebraPackage+"/"+CONFIG_FILE;
		ClassLoader cl = this.getClass().getClassLoader();
		InputStream is = cl.getResourceAsStream(file);
		if(is != null) {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = null;
			br = new BufferedReader(isr);
			try {
				String line = br.readLine();
				while(line != null) {
					if(line.equalsIgnoreCase(SimStPLE.START_STATE_ELEMENTS_HEADER)) {
						readStartStateElements(br);
					} 
					if(line != null) {
						line = br.readLine();
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		} else {
			new Exception("Resource file " + file + "  was not found. Cannot populate the interface elements");
		}
	}

	private void readStartStateElements(BufferedReader br) {
		
		String line;
		try {
			startStateElements = new ArrayList<String>();
			line = br.readLine();
			while(line != null && line.length() > 0) {
				startStateElements.add(line);
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String[] getComponentNames() {
		return null;
	}

	@Override
	public String[] getFoAElements() {
		return null;
	}

	@Override
	public ArrayList<String> getStartStateElements() {
			return startStateElements;
	}
	
	public void simulateStartStateElementEntry(BR_Controller controller, String problemName) {
		
		if(controller == null || problemName.length() < 0 || problemName.split("=").length!=2)
			return;
		
		String[] problem = problemName.split("=");
		if(problem.length == 2 && problem[0].length() > 0 && problem[1].length() > 0) {
			
			for(int i = 0; i < problem.length; i++) {

				String cellName = startStateElements.get(i);
				String cellValue = problem[i];
				
				TableCell cell = (TableCell) controller.lookupWidgetByName(cellName);
				FocusEvent e = new FocusEvent(cell, FocusEvent.FOCUS_LOST, false, cell);
				cell.setText(cellValue);
			
				int index = cellName.indexOf('_');
				String tableName = cellName.substring(0, index);
				JCommTable commTable = (JCommTable) controller.lookupWidgetByName(tableName);
				commTable.focusLost(e);
			}
		}
	}

}
