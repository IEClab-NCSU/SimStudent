package SimStFractionAdditionV1;

import javax.swing.JPanel;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;
import pact.CommWidgets.JCommTable;
import javax.swing.JLabel;
import java.awt.Font;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import pact.CommWidgets.JCommTextField;
import java.awt.Color;
import pact.CommWidgets.JCommLabel;
import pact.CommWidgets.JCommComboBox;


public class fraction extends JPanel {
	private CTAT_Options t_Options;
	private JCommTable dorminTable1;
	private JCommTable dorminTable2;
	private JCommTable dorminTable3;
	private JCommTable dorminTable4;
	private JCommTable dorminTable5;
	private JCommTable dorminTable6;
	private JCommTable dorminTable7;
	private JCommTable dorminTable8;
	private JLabel label;
	private JLabel label_1;
	private JLabel label_2;
	private JLabel label_3;
	private JLabel label_4;
	private JLabel label_6;
	private JLabel label_5;

	
	private JCommTextField commTextField1;
	private JCommTextField commTextField2;
	private JCommTextField commTextField3;
	private JCommTextField commTextField4;
	private JCommTextField commTextField5;
	private JCommTextField commTextField6;
	private JCommTextField commTextField7;
	private JCommTextField commTextField8;

	private JCommComboBox commComboBox1;
	private JCommComboBox commComboBox2;
	private JCommComboBox commComboBox3;
	private JCommComboBox commComboBox4;
		
	private JCommTextField complexfraction1;
	private JCommTextField complexfraction2;	
	private JCommTextField complexfraction3;
	private JCommTextField complexfraction4;
	private JCommTextField complexfraction5;
	private JCommTextField complexfraction6;
	private JCommTextField complexfraction7;
	private JCommTextField complexfraction8;
    private pact.CommWidgets.JCommButton done;
	/**
	 * Create the panel.
	 */
	public fraction() {

		initComponents();
	}
	private void initComponents() {
		setLayout(null);
		
		t_Options = new edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options();
		  
		done = new pact.CommWidgets.JCommButton();
		done.setText("Problem is Solved");
		done.setName("done");
        add(done);
        done.setBounds(10, 315, 540, 40);

		//t_Options = new CTAT_Options();
		//t_Options.setBounds(111, 192, 1, 1);
		//add(t_Options);
		
		dorminTable1 = new JCommTable();
		dorminTable1.setName("dorminTable1");
		dorminTable1.setRows(2);
		dorminTable1.setColumns(1);
		dorminTable1.setBounds(45, 22, 49, 83);
		add(dorminTable1);
		
		dorminTable2 = new JCommTable();
		dorminTable2.setName("dorminTable2");
		dorminTable2.setRows(2);
		dorminTable2.setColumns(1);
		dorminTable2.setBounds(138, 22, 49, 83);
		add(dorminTable2);
		
		label = new JLabel("+");
		label.setFont(new Font("Lucida Grande", Font.PLAIN, 22));
		label.setBounds(106, 53, 19, 27);
		add(label);
		
		dorminTable3 = new JCommTable();
		dorminTable3.setName("dorminTable3");
		dorminTable3.setRows(2);
		dorminTable3.setColumns(1);
		dorminTable3.setBounds(278, 22, 49, 83);
		add(dorminTable3);
		
		dorminTable4 = new JCommTable();
		dorminTable4.setName("dorminTable4");
		dorminTable4.setRows(2);
		dorminTable4.setColumns(1);
		dorminTable4.setBounds(410, 22, 49, 83);
		add(dorminTable4);
		
		dorminTable5 = new JCommTable();
		dorminTable5.setName("dorminTable5");
		dorminTable5.setRows(2);
		dorminTable5.setColumns(1);
		dorminTable5.setBounds(278, 115, 49, 83);
		add(dorminTable5);
		
		dorminTable6 = new JCommTable();
		dorminTable6.setName("dorminTable6");
		dorminTable6.setRows(2);
		dorminTable6.setColumns(1);
		dorminTable6.setBounds(410, 115, 49, 83);
		add(dorminTable6);
		
			
		dorminTable7 = new JCommTable();
		dorminTable7.setName("dorminTable7");
		dorminTable7.setRows(2);
		dorminTable7.setColumns(1);
		dorminTable7.setBounds(278, 204, 49, 83);
		add(dorminTable7);
		
		
		dorminTable8 = new JCommTable();
		dorminTable8.setName("dorminTable8");
		dorminTable8.setRows(2);
		dorminTable8.setColumns(1);
		dorminTable8.setBounds(410, 204, 49, 83);
		add(dorminTable8);
		
		
		label_1 = new JLabel("+");
		label_1.setFont(new Font("Lucida Grande", Font.PLAIN, 22));
		label_1.setBounds(337, 53, 19, 27);
		add(label_1);
		
		label_2 = new JLabel("=");
		label_2.setFont(new Font("Lucida Grande", Font.PLAIN, 22));
		label_2.setBounds(199, 53, 19, 27);
		add(label_2);
		
		label_3 = new JLabel("=");
		label_3.setFont(new Font("Lucida Grande", Font.PLAIN, 22));
		label_3.setBounds(199, 143, 19, 27);
		add(label_3);
		
		label_4 = new JLabel("=");
		label_4.setFont(new Font("Lucida Grande", Font.PLAIN, 22));
		label_4.setBounds(199, 240, 19, 27);
		add(label_4);
		
		
		label_5 = new JLabel("+");
		label_5.setFont(new Font("Lucida Grande", Font.PLAIN, 22));
		label_5.setBounds(337, 143, 19, 27);
		add(label_5);
		
		label_6 = new JLabel("+");
		label_6.setFont(new Font("Lucida Grande", Font.PLAIN, 22));
		label_6.setBounds(337, 240, 19, 27);
		add(label_6);
		
		
		
		commTextField1 = new JCommTextField();
		commTextField1.setName("commTextField1");
		commTextField1.setInvisible(true);
		commTextField1.setBounds(45, 88, 52, 41);
		add(commTextField1);
		
		commTextField2 = new JCommTextField();
		commTextField2.setName("commTextField2");
		commTextField2.setInvisible(true);
		commTextField2.setBounds(135, 88, 52, 41);
		add(commTextField2);
		
		commTextField3 = new JCommTextField();
		commTextField3.setName("commTextField3");
		commTextField3.setBounds(224, 45, 52, 41);
		add(commTextField3);
		
		commTextField4 = new JCommTextField();
		commTextField4.setName("commTextField4");
		commTextField4.setBounds(357, 45, 52, 41);
		add(commTextField4);
		
		commTextField5 = new JCommTextField();
		commTextField5.setName("commTextField5");
		commTextField5.setBounds(224, 128, 52, 41);
		add(commTextField5);
		
		commTextField6 = new JCommTextField();
		commTextField6.setName("commTextField6");
		commTextField6.setBounds(357, 128, 52, 41);
		add(commTextField6);
		

		commTextField7 = new JCommTextField();
		commTextField7.setName("commTextField7");
		commTextField7.setBounds(224, 223, 52, 41);
		add(commTextField7);
		
		
		commTextField8 = new JCommTextField();
		commTextField8.setName("commTextField8");
		commTextField8.setBounds(357, 223, 52, 41);
		add(commTextField8);
			
		
		complexfraction1 = new JCommTextField();
		complexfraction1.setName("complex-fraction1");
		complexfraction1.setInvisible(true);
		add(complexfraction1);
		
		complexfraction2 = new JCommTextField();
		complexfraction2.setName("complex-fraction2");
		complexfraction2.setInvisible(true);
		add(complexfraction2);
		
		
		complexfraction3 = new JCommTextField();
		complexfraction3.setName("complex-fraction3");
		complexfraction3.setInvisible(true);
		add(complexfraction3);
			
		complexfraction4 = new JCommTextField();
		complexfraction4.setName("complex-fraction4");
		complexfraction4.setInvisible(true);
		add(complexfraction4);
		
		
		complexfraction5 = new JCommTextField();
		complexfraction5.setName("complex-fraction5");
		complexfraction5.setInvisible(true);
		add(complexfraction5);
			
		complexfraction6 = new JCommTextField();
		complexfraction6.setName("complex-fraction6");
		complexfraction6.setInvisible(true);
		add(complexfraction6);
			
		complexfraction7 = new JCommTextField();
		complexfraction7.setName("complex-fraction7");
		complexfraction7.setInvisible(true);
		add(complexfraction7);
				
		complexfraction8 = new JCommTextField();
		complexfraction8.setName("complex-fraction8");
		complexfraction8.setInvisible(true);
		add(complexfraction8);
			
			
			
			
		commComboBox1 = new JCommComboBox();
		commComboBox1.setName("commComboBox1");
		commComboBox1.setInvisible(true);
		commComboBox1.setBounds(59, 131, 128, 28);
		add(commComboBox1);
		
		commComboBox2 = new JCommComboBox();
		commComboBox2.setName("commComboBox2");
		commComboBox2.setValues("Select a goal,Reduce,Add,Simplify,Conversion");
		commComboBox2.setBounds(459, 40, 128, 28);
		add(commComboBox2);
		
		commComboBox3 = new JCommComboBox();
		commComboBox3.setName("commComboBox3");
		commComboBox3.setValues("Select a goal,Reduce,Add,Simplify,Conversion");
		commComboBox3.setBounds(459, 133, 128, 28);
		add(commComboBox3);
		
		commComboBox4 = new JCommComboBox();
		commComboBox4.setName("commComboBox4");
		commComboBox4.setValues("Select a goal,Reduce,Add,Simplify,Conversion");
		commComboBox4.setBounds(459, 225, 128, 28);
		add(commComboBox4);
		
		
		
	}
	
	public static void main(String args[]) { 
		  new CTAT_Launcher(args).launch(new fraction());
		}
}
