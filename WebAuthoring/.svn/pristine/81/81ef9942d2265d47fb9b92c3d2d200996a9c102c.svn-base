package digt_1_3;

import javax.swing.JPanel;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;
import pact.CommWidgets.JCommTable;
import java.awt.Color;

public class Interface extends JPanel {
	private CTAT_Options t_Options;
	private JCommTable table;

	public static void main(String args[]) { 
		  new CTAT_Launcher(args).launch(new Interface());
	}
	
	/**
	 * Create the panel.
	 */
	public Interface() {

		initComponents();
	}
	private void initComponents() {
		setLayout(null);
		
		t_Options = new CTAT_Options();
		t_Options.setBounds(78, 143, 1, 1);
		add(t_Options);
		table = new JCommTable();
		table.setRows(5);
		table.setHintBtn(true);
		table.setBackground(Color.WHITE);
		table.setBounds(66, 43, 320, 216);
		table.setName("table");
		add(table);
		
		table.getCell(0, 0).setBackground(Color.LIGHT_GRAY);
		table.getCell(0, 1).setBackground(Color.LIGHT_GRAY);
		table.getCell(0, 2).setBackground(Color.LIGHT_GRAY);
		table.getCell(0, 3).setBackground(Color.LIGHT_GRAY);

		table.getCell(0, 0).setText("");
		table.getCell(0, 1).setText("");
		table.getCell(0, 2).setText("");
		table.getCell(0, 3).setText("");
	}
}
