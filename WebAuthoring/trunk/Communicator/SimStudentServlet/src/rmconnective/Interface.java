package rmconnective;

import javax.swing.JPanel;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;
import pact.CommWidgets.JCommTextField;
import pact.CommWidgets.JCommLabel;
import pact.CommWidgets.HorizontalLine;
import java.awt.Color;

public class Interface extends JPanel {
	private CTAT_Options t_Options;
	private JCommTextField leftOperand_field;
	private JCommTextField connective_field;
	private JCommTextField rightOperandField;
	private JCommLabel commLabel;
	private JCommLabel commLabel_1;
	private JCommTextField rm_connective_field;
	private HorizontalLine horizontalLine;
	private JCommTextField simplified_field;

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
		
		leftOperand_field = new JCommTextField();
		leftOperand_field.setBounds(17, 35, 86, 40);
		leftOperand_field.setName("leftOperand_field");
		add(leftOperand_field);
		
		connective_field = new JCommTextField();
		connective_field.setBounds(115, 35, 54, 40);
		connective_field.setName("connective_field");
		add(connective_field);
		
		rightOperandField = new JCommTextField();
		rightOperandField.setBounds(183, 35, 86, 40);
		rightOperandField.setName("rightOperandField");
		add(rightOperandField);
		
		commLabel = new JCommLabel();
		commLabel.setText("Formula");
		commLabel.setBounds(17, 17, 78, 16);
		add(commLabel);
		
		commLabel_1 = new JCommLabel();
		commLabel_1.setText("Negation Normal Form");
		commLabel_1.setBounds(18, 122, 266, 16);
		add(commLabel_1);
		
		rm_connective_field = new JCommTextField();
		rm_connective_field.setBounds(17, 150, 252, 40);
		rm_connective_field.setName("rm_connective_field");
		add(rm_connective_field);
		
		horizontalLine = new HorizontalLine();
		horizontalLine.setBounds(17, 140, 252, 10);
		add(horizontalLine);
		
		simplified_field = new JCommTextField();
		simplified_field.setName("simplified_field");
		simplified_field.setBounds(17, 196, 252, 40);
		add(simplified_field);
	}
}
