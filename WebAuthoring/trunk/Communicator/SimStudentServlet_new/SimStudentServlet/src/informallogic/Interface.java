package informallogic;

import javax.swing.JPanel;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;
import pact.CommWidgets.JCommLabel;
import pact.CommWidgets.HorizontalLine;
import pact.CommWidgets.JCommTextField;
import java.awt.Color;
import javax.swing.border.BevelBorder;

public class Interface extends JPanel {
	private CTAT_Options t_Options;
	private JCommLabel englishLabel;
	private HorizontalLine horizontalLine;
	private JCommLabel propositionalLogicLabel;
	private HorizontalLine horizontalLine_1;
	private JCommTextField prob_table_c1;
	private JCommTextField prob_table_c2;
	private JCommTextField prob_table_c4;
	private JCommTextField prob_table_c3;
	private JCommTextField prob_table_c5;
	private JCommTextField ans_table_c2;
	private JCommTextField ans_table_c3;
	private JCommTextField ans_table_c4;
	private JCommTextField ans_table_c5;
	private JCommTextField ans_table_c6;
	private JCommTextField prob_table_c6;

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
		t_Options.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		t_Options.setBackground(Color.WHITE);
		t_Options.setBounds(209, 120, 1, 1);
		add(t_Options);
		
		englishLabel = new JCommLabel();
		englishLabel.setText("Assertion in Informal Language");
		englishLabel.setBounds(17, 6, 207, 16);
		add(englishLabel);
		
		horizontalLine = new HorizontalLine();
		horizontalLine.setBounds(17, 24, 706, 16);
		add(horizontalLine);
		
		propositionalLogicLabel = new JCommLabel();
		propositionalLogicLabel.setText("Assertion in Propositional Logic");
		propositionalLogicLabel.setBounds(17, 143, 230, 16);
		add(propositionalLogicLabel);
		
		horizontalLine_1 = new HorizontalLine();
		horizontalLine_1.setBounds(17, 162, 588, 10);
		add(horizontalLine_1);
		
		prob_table_c1 = new JCommTextField();
		prob_table_c1.setBounds(17, 34, 113, 39);
		add(prob_table_c1);
		prob_table_c1.setName("prob_table_c1");
		
		prob_table_c2 = new JCommTextField();
		prob_table_c2.setBounds(137, 34, 113, 39);
		add(prob_table_c2);
		prob_table_c2.setName("prob_table_c2");
		
		prob_table_c3 = new JCommTextField();
		prob_table_c3.setBounds(255, 34, 113, 39);
		add(prob_table_c3);
		prob_table_c3.setName("prob_table_c3");
		
		prob_table_c4 = new JCommTextField();
		prob_table_c4.setBounds(372, 34, 113, 39);
		add(prob_table_c4);
		prob_table_c4.setName("prob_table_c4");

		prob_table_c5 = new JCommTextField();
		prob_table_c5.setBounds(492, 34, 113, 39);
		add(prob_table_c5);
		prob_table_c5.setName("prob_table_c5");
		
		
		ans_table_c2 = new JCommTextField();
		ans_table_c2.setName("ans_table_c2");
		ans_table_c2.setBounds(17, 171, 113, 39);
		add(ans_table_c2);
		
		ans_table_c3 = new JCommTextField();
		ans_table_c3.setName("ans_table_c3");
		ans_table_c3.setBounds(137, 171, 113, 39);
		add(ans_table_c3);
		
		ans_table_c4 = new JCommTextField();
		ans_table_c4.setName("ans_table_c4");
		ans_table_c4.setBounds(255, 171, 113, 39);
		add(ans_table_c4);
		
		ans_table_c5 = new JCommTextField();
		ans_table_c5.setName("ans_table_c5");
		ans_table_c5.setBounds(372, 171, 113, 39);
		add(ans_table_c5);
		
		ans_table_c6 = new JCommTextField();
		ans_table_c6.setName("ans_table_c6");
		ans_table_c6.setBounds(492, 171, 113, 39);
		add(ans_table_c6);
		
		prob_table_c6 = new JCommTextField();
		prob_table_c6.setName("prob_table_c6");
		prob_table_c6.setBounds(610, 34, 113, 39);
		add(prob_table_c6);
	}
}
