package WebAPLUS;

import javax.swing.JPanel;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;
import pact.CommWidgets.JCommTextField;
public class webAplus extends JPanel {
	private CTAT_Options t_Options;
	private JCommTextField textbox1;
	private JCommTextField textbox2;
	private JCommTextField textbox3;
	private JCommTextField textbox4;
	private JCommTextField textbox5;
	private JCommTextField textbox6;
	private JCommTextField textbox7;
	private JCommTextField textbox8;
	private JCommTextField textbox9;
	private JCommTextField textbox10;
	private JCommTextField textbox11;
	private JCommTextField textbox12;
	private JCommTextField textbox13;
	private JCommTextField textbox14;
	private JCommTextField textbox15;

	/**
	 * Create the panel.
	 */
	public webAplus() {

		initComponents();
	}
	private void initComponents() {
		setLayout(null);
		
		t_Options = new CTAT_Options();
		t_Options.setBounds(69, 186, 1, 1);
		add(t_Options);
		
		textbox1 = new JCommTextField();
		textbox1.setBounds(16, 117, 71, 28);
		add(textbox1);
		
		textbox2 = new JCommTextField();
		textbox2.setBounds(100, 117, 71, 28);
		add(textbox2);
		
		textbox3 = new JCommTextField();
		textbox3.setBounds(228, 117, 71, 28);
		add(textbox3);
		
		textbox4 = new JCommTextField();
		textbox4.setBounds(16, 159, 71, 28);
		add(textbox4);
		
		textbox5 = new JCommTextField();
		textbox5.setBounds(100, 157, 71, 28);
		add(textbox5);
		
		textbox6 = new JCommTextField();
		textbox6.setBounds(228, 159, 71, 28);
		add(textbox6);
		
		textbox7 = new JCommTextField();
		textbox7.setBounds(16, 209, 71, 28);
		add(textbox7);
		
		textbox8 = new JCommTextField();
		textbox8.setBounds(100, 209, 71, 28);
		add(textbox8);
		
		textbox9 = new JCommTextField();
		textbox9.setBounds(228, 209, 71, 28);
		add(textbox9);
		
		textbox10 = new JCommTextField();
		textbox10.setBounds(16, 256, 71, 28);
		add(textbox10);
		
		textbox11 = new JCommTextField();
		textbox11.setBounds(100, 256, 71, 28);
		add(textbox11);
		
		textbox12 = new JCommTextField();
		textbox12.setBounds(228, 249, 71, 28);
		add(textbox12);
		
		textbox13 = new JCommTextField();
		textbox13.setBounds(16, 296, 71, 28);
		add(textbox13);
		
		textbox14 = new JCommTextField();
		textbox14.setBounds(100, 296, 71, 28);
		add(textbox14);
		
		textbox15 = new JCommTextField();
		textbox15.setBounds(228, 296, 71, 28);
		add(textbox15);
	}
    
	public static void main(String args[]){
		new CTAT_Launcher(args).launch(new webAplus());
	}
}
