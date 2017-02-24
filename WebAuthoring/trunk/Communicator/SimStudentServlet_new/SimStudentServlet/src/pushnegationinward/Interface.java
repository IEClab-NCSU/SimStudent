package pushnegationinward;

import java.awt.Color;

import javax.swing.JPanel;

import pact.CommWidgets.HorizontalLine;
import pact.CommWidgets.JCommLabel;
import pact.CommWidgets.JCommTextField;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;

public class Interface extends JPanel {
	
	private CTAT_Options t_Options;
	private JCommTextField formula_field;
	private JCommLabel commLabel;
	private JCommLabel commLabel_1;
	private JCommTextField step1_field;
	private HorizontalLine horizontalLine;
	private JCommTextField step2_field;
	private HorizontalLine horizontalLine_1;
	
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
		setBackground(Color.WHITE);
		setLayout(null);
		
		t_Options = new CTAT_Options();
		t_Options.setBounds(92, 68, 1, 1);
		add(t_Options);
		
		formula_field = new JCommTextField();
		formula_field.setBounds(6, 45, 297, 40);
		formula_field.setName("formula_field");
		add(formula_field);
		
		commLabel = new JCommLabel();
		commLabel.setText("Formula");
		commLabel.setBounds(6, 17, 78, 16);
		add(commLabel);
		
		commLabel_1 = new JCommLabel();
		commLabel_1.setText("Move Negation Inward");
		commLabel_1.setBounds(6, 126, 266, 16);
		add(commLabel_1);
		
		step1_field = new JCommTextField();
		step1_field.setBounds(6, 154, 299, 40);
		step1_field.setName("step1_field");
		add(step1_field);
		
		horizontalLine = new HorizontalLine();
		horizontalLine.setBounds(6, 143, 299, 16);
		add(horizontalLine);
		
		step2_field = new JCommTextField();
		step2_field.setName("step2_field");
		step2_field.setBounds(6, 206, 297, 40);
		add(step2_field);
		
		horizontalLine_1 = new HorizontalLine();
		horizontalLine_1.setBounds(4, 34, 299, 16);
		add(horizontalLine_1);
	}
}
