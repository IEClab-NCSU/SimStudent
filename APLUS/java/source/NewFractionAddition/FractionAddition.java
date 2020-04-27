package NewFractionAddition;

import javax.swing.JPanel;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;
import pact.CommWidgets.JCommTextField;
import pact.CommWidgets.HorizontalLine;
import java.awt.Color;

public class FractionAddition extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CTAT_Options t_Options;
	private JCommTextField commTextField;
	private JCommTextField commTextField_1;
	private HorizontalLine horizontalLine;
	private JCommTextField commTextField_2;
	private HorizontalLine horizontalLine_1;
	private JCommTextField commTextField_3;
	private JCommTextField commTextField_4;
	private HorizontalLine horizontalLine_2;
	private JCommTextField commTextField_5;
	private JCommTextField commTextField_6;
	private HorizontalLine horizontalLine_3;
	private JCommTextField commTextField_7;
	private JCommTextField commTextField_8;
	private HorizontalLine horizontalLine_4;
	private JCommTextField commTextField_9;
	private JCommTextField commTextField_10;
	private HorizontalLine horizontalLine_5;
	private JCommTextField commTextField_11;

	/**
	 * Create the panel.
	 */
	public FractionAddition() {
			
		initComponents();
	}
	private void initComponents() {
		setLayout(null);
		
		t_Options = new CTAT_Options();
		t_Options.setBounds(146, 195, 1, 1);
		add(t_Options);
		
		commTextField = new JCommTextField();
		commTextField.setCommName("firstNumGiven");
		commTextField.setBounds(54, 68, 70, 28);
		add(commTextField);
		
		commTextField_1 = new JCommTextField();
		commTextField_1.setCommName("firstDenGiven");
		commTextField_1.setBounds(54, 110, 70, 28);
		add(commTextField_1);
		
		horizontalLine = new HorizontalLine();
		horizontalLine.setBackground(Color.BLACK);
		horizontalLine.setBounds(54, 102, 82, 10);
		add(horizontalLine);
		
		commTextField_2 = new JCommTextField();
		commTextField_2.setCommName("secNumGiven");
		commTextField_2.setBounds(54, 168, 70, 28);
		add(commTextField_2);
		
		horizontalLine_1 = new HorizontalLine();
		horizontalLine_1.setBackground(Color.BLACK);
		horizontalLine_1.setBounds(54, 202, 82, 10);
		add(horizontalLine_1);
		
		commTextField_3 = new JCommTextField();
		commTextField_3.setCommName("secDenGiven");
		commTextField_3.setBounds(54, 210, 70, 28);
		add(commTextField_3);
		
		commTextField_4 = new JCommTextField();
		commTextField_4.setCommName("firstNumCon");
		commTextField_4.setBounds(207, 68, 70, 28);
		add(commTextField_4);
		
		horizontalLine_2 = new HorizontalLine();
		horizontalLine_2.setBackground(Color.BLACK);
		horizontalLine_2.setBounds(207, 102, 82, 10);
		add(horizontalLine_2);
		
		commTextField_5 = new JCommTextField();
		commTextField_5.setCommName("firstDenCon");
		commTextField_5.setBounds(207, 110, 70, 28);
		add(commTextField_5);
		
		commTextField_6 = new JCommTextField();
		commTextField_6.setCommName("secNumCon");
		commTextField_6.setBounds(207, 168, 70, 28);
		add(commTextField_6);
		
		horizontalLine_3 = new HorizontalLine();
		horizontalLine_3.setBackground(Color.BLACK);
		horizontalLine_3.setBounds(207, 202, 82, 10);
		add(horizontalLine_3);
		
		commTextField_7 = new JCommTextField();
		commTextField_7.setCommName("secDenCon");
		commTextField_7.setBounds(207, 210, 70, 28);
		add(commTextField_7);
		
		commTextField_8 = new JCommTextField();
		commTextField_8.setCommName("ansNum1");
		commTextField_8.setBounds(207, 255, 70, 28);
		add(commTextField_8);
		
		horizontalLine_4 = new HorizontalLine();
		horizontalLine_4.setBackground(Color.BLACK);
		horizontalLine_4.setBounds(207, 295, 82, 10);
		add(horizontalLine_4);
		
		commTextField_9 = new JCommTextField();
		commTextField_9.setCommName("ansDen1");
		commTextField_9.setBounds(207, 305, 70, 28);
		add(commTextField_9);
		
		commTextField_10 = new JCommTextField();
		commTextField_10.setCommName("ansNumFinal1");
		commTextField_10.setBounds(323, 255, 70, 28);
		add(commTextField_10);
		
		horizontalLine_5 = new HorizontalLine();
		horizontalLine_5.setBackground(Color.BLACK);
		horizontalLine_5.setBounds(323, 289, 82, 10);
		add(horizontalLine_5);
		
		commTextField_11 = new JCommTextField();
		commTextField_11.setCommName("ansDenFinal1");
		commTextField_11.setBounds(323, 297, 70, 28);
		add(commTextField_11);
	}
	
	public static void main(String args[]){
		  new CTAT_Launcher(args).launch(new FractionAddition());

	}
}
