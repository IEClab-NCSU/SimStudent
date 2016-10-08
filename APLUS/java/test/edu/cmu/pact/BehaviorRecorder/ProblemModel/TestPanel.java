/**
 * 
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import javax.swing.JPanel;

import pact.CommWidgets.JCommTable;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;

public class TestPanel extends JPanel {
    JCommTable Table0 = new JCommTable();
    CTAT_Options options = new CTAT_Options();
    
    public TestPanel() {
    	options.setUseExampleTracingTutor(true);
        Table0.setRows(4);
        Table0.setColumns(4);
        setLayout(null);
        Table0.setCommName("Table0");
        add(Table0);
        Table0.setBounds(10, 10, 200, 200);
    }
}
