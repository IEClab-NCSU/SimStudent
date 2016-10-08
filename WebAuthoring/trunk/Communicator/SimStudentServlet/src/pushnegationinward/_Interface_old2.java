package pushnegationinward;

import java.awt.Color;

import javax.swing.JPanel;

import pact.CommWidgets.HorizontalLine;
import pact.CommWidgets.JCommLabel;
import pact.CommWidgets.JCommTextField;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;
import java.awt.Font;

public class _Interface_old2 extends JPanel {
	
	private CTAT_Options t_Options;
	private JCommTextField formula_left;
	private JCommLabel commLabel;
	private JCommLabel commLabel_1;
	private HorizontalLine horizontalLine;
	private HorizontalLine horizontalLine_1;
	private JCommTextField not_field;
	private JCommLabel commLabel_4;
	private JCommLabel commLabel_6;
	private JCommLabel commLabel_3;
	private JCommLabel commLabel_7;
	private JCommTextField formula_conn;
	private JCommTextField formula_right;
	private JCommTextField step1_left;
	private JCommTextField step2_left;
	private JCommTextField step1_conn;
	private JCommTextField step1_right;
	private JCommLabel commLabel_2;
	private JCommTextField step2_right;
	private JCommTextField step2_conn;
	private JCommLabel commLabel_5;
	private JCommLabel commLabel_8;
	private JCommLabel commLabel_9;
	private JCommLabel commLabel_10;
	private JCommLabel commLabel_11;
	private JCommTextField step3_left;
	private JCommTextField step3_conn;
	private JCommTextField step3_right;
	private JCommLabel commLabel_12;
	
	public static void main(String args[]) { 
		  new CTAT_Launcher(args).launch(new _Interface_old2());
	}
	/**
	 * Create the panel.
	 */
	public _Interface_old2() {
		initComponents();
	}
	
	private void initComponents() {
		setBackground(Color.WHITE);
		setLayout(null);
		
		t_Options = new CTAT_Options();
		t_Options.setBounds(92, 68, 1, 1);
		add(t_Options);
		
		formula_left = new JCommTextField();
		formula_left.setBounds(69, 45, 132, 40);
		formula_left.setName("formula_left");
		add(formula_left);
		
		commLabel = new JCommLabel();
		commLabel.setText("Formula");
		commLabel.setBounds(6, 17, 78, 16);
		add(commLabel);
		
		commLabel_1 = new JCommLabel();
		commLabel_1.setText("Move Negation Inward");
		commLabel_1.setBounds(6, 120, 266, 16);
		add(commLabel_1);
		
		horizontalLine = new HorizontalLine();
		horizontalLine.setBounds(6, 144, 405, 16);
		add(horizontalLine);
		
		horizontalLine_1 = new HorizontalLine();
		horizontalLine_1.setBounds(4, 34, 407, 16);
		add(horizontalLine_1);
		
		not_field = new JCommTextField();
		not_field.setName("not_field");
		not_field.setBounds(9, 45, 48, 40);
		add(not_field);
		
		commLabel_4 = new JCommLabel();
		commLabel_4.setText("(");
		commLabel_4.setBounds(60, 45, 4, 40);
		add(commLabel_4);
		
		commLabel_6 = new JCommLabel();
		commLabel_6.setText(")");
		commLabel_6.setBounds(396, 45, 15, 40);
		add(commLabel_6);
		
		commLabel_3 = new JCommLabel();
		commLabel_3.setText("(");
		commLabel_3.setBounds(60, 157, 4, 40);
		add(commLabel_3);
		
		commLabel_7 = new JCommLabel();
		commLabel_7.setText("(");
		commLabel_7.setBounds(60, 209, 4, 40);
		add(commLabel_7);
		
		formula_conn = new JCommTextField();
		formula_conn.setName("formula_conn");
		formula_conn.setBounds(204, 45, 48, 40);
		add(formula_conn);
		
		formula_right = new JCommTextField();
		formula_right.setName("formula_right");
		formula_right.setBounds(255, 45, 132, 40);
		add(formula_right);
		
		step1_left = new JCommTextField();
		step1_left.setName("step1_left");
		step1_left.setBounds(69, 157, 132, 40);
		add(step1_left);
		
		step2_left = new JCommTextField();
		step2_left.setName("step2_left");
		step2_left.setBounds(69, 209, 132, 40);
		add(step2_left);
		
		step1_conn = new JCommTextField();
		step1_conn.setName("step1_conn");
		step1_conn.setBounds(204, 157, 48, 40);
		add(step1_conn);
		
		step1_right = new JCommTextField();
		step1_right.setName("step1_right");
		step1_right.setBounds(255, 157, 132, 40);
		add(step1_right);
		
		commLabel_2 = new JCommLabel();
		commLabel_2.setText(")");
		commLabel_2.setBounds(396, 157, 15, 40);
		add(commLabel_2);
		
		step2_right = new JCommTextField();
		step2_right.setName("step2_right");
		step2_right.setBounds(255, 209, 132, 40);
		add(step2_right);
		
		step2_conn = new JCommTextField();
		step2_conn.setName("step2_conn");
		step2_conn.setBounds(204, 209, 48, 40);
		add(step2_conn);
		
		commLabel_5 = new JCommLabel();
		commLabel_5.setText(")");
		commLabel_5.setBounds(396, 209, 15, 40);
		add(commLabel_5);
		
		commLabel_8 = new JCommLabel();
		commLabel_8.setText("Step 1:");
		commLabel_8.setBounds(6, 166, 54, 24);
		add(commLabel_8);
		
		commLabel_9 = new JCommLabel();
		commLabel_9.setText("Step 2:");
		commLabel_9.setBounds(6, 214, 51, 24);
		add(commLabel_9);
		
		commLabel_10 = new JCommLabel();
		commLabel_10.setText("Step 3:");
		commLabel_10.setBounds(6, 266, 51, 24);
		add(commLabel_10);
		
		commLabel_11 = new JCommLabel();
		commLabel_11.setText("(");
		commLabel_11.setBounds(60, 261, 4, 40);
		add(commLabel_11);
		
		step3_left = new JCommTextField();
		step3_left.setAutoCapitalize(true);
		step3_left.setName("step3_left");
		step3_left.setBounds(69, 261, 132, 40);
		add(step3_left);
		
		step3_conn = new JCommTextField();
		step3_conn.setName("step3_conn");
		step3_conn.setBounds(204, 261, 48, 40);
		add(step3_conn);
		
		step3_right = new JCommTextField();
		step3_right.setName("step3_right");
		step3_right.setBounds(255, 261, 132, 40);
		add(step3_right);
		
		commLabel_12 = new JCommLabel();
		commLabel_12.setText(")");
		commLabel_12.setBounds(396, 261, 15, 40);
		add(commLabel_12);
	}
}
