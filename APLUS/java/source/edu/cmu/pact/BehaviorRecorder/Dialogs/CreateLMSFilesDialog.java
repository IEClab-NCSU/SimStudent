package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctatview.CTATtoLMS;

public class CreateLMSFilesDialog implements ActionListener, ItemListener{
	
	private JFrame frame; //will hold the mainPane, and any message dialogs
	
	private JButton create;
	private JButton cancel;
	private JButton browse;
	
	private JTextField filepath;
	private JTextField studentInterface;
	private JTextField platform;
	
	private JCheckBox excel;
	private JCheckBox xcur;
	private JCheckBox unit;
	private JCheckBox bas;
	
//	private JFileChooser chooser;
	
	private String defaultStudentInterface = " ";
	private String defaultPlatform = " ";
	private char[] options = {'n', 'y', 'y', 'y'};
	private static final String defaultDebugOptions = "NYY";
	private String debugOptions;
	
	public CreateLMSFilesDialog()
	{
		this(defaultDebugOptions);
	}
	
	public CreateLMSFilesDialog(String debugOptions)
	{
		this.debugOptions = debugOptions; 
	    
	    JPanel mainPane = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 2;
		c.insets = new Insets(5, 5, 5, 5);
		
		c.gridx = 0;
		c.gridy = 0;
		mainPane.add(new JLabel("FilePath to .brd or folder"), c);
		
		browse = new JButton("Browse");
		browse.addActionListener(this);
		c.gridx = 3;
		c.gridy = 1;
		mainPane.add(browse, c);
		
		filepath = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(filepath); }
		c.gridx = 0;
		c.gridwidth = 3;
		mainPane.add(filepath, c);
		
		c.gridy = 2;
		mainPane.add(new JLabel("(Optional) Student Interface"), c);
		
		studentInterface = new JTextField(defaultStudentInterface, 15);
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(studentInterface); }
		studentInterface.setText("");
		c.gridy = 3;
		c.gridwidth = 3;
		mainPane.add(studentInterface, c);
		
		c.gridy = 4;
		mainPane.add(new JLabel("(Optional) Platform"), c);
		
		platform = new JTextField(defaultPlatform, 15);
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(platform); }
		platform.setText("");
		c.gridy = 5;
		mainPane.add(platform, c);
	    
		//sets up options
		excel = new JCheckBox("Excel", true);
		excel.addItemListener(this);
		c.gridy = 6;
		mainPane.add(excel, c);

		xcur = new JCheckBox("XCUR", true);
		xcur.addItemListener(this);
		c.gridy = 7;
		mainPane.add(xcur, c);
		
		unit = new JCheckBox("UNIT", true);
		unit.addItemListener(this);
		c.gridy = 8;
		mainPane.add(unit, c);
		
		bas = new JCheckBox("BAS", true);
		bas.addItemListener(this);
		c.gridy = 9;
		mainPane.add(bas, c);
		
		//the buttons
		create = new JButton("Create");
		create.addActionListener(this);
		c.gridx = 2;
		c.gridy = 10;
		c.gridwidth = 1;
		mainPane.add(create, c);
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		c.gridx = 3;
		mainPane.add(cancel, c);
		
		frame = new JFrame("Create LMS Files");
		frame.setContentPane(mainPane);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null); //umm should this be relative to something else??
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	//debugging
	public static void main(String[] args)
	{
		CreateLMSFilesDialog t = new CreateLMSFilesDialog("NYY");
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == browse)
		{
			File f = DialogUtilities.chooseFile(".", null, new BrdFilter(),
					"Find files", "Open", frame);
	        if(f != null)
	        	filepath.setText(f.getAbsolutePath());
	    }
		else if(ae.getSource() == cancel)
		{
			frame.setVisible(false);
			frame.dispose();
		}
		else if(ae.getSource() == create)
		{			
			String[] args = new String[5];
			args[0] = filepath.getText();
			args[1] = platform.getText();
			if(args[1] == null) //shouldn't happen unless somebody messes with the defaults
				args[1] = " ";
			args[2] = studentInterface.getText();
			if(args[2] == null)
				args[2] = "";
			args[3] = new String(options);
			args[4] = debugOptions;
			
			String status = CTATtoLMS.run(args, true);
			
			frame.setVisible(false);
			
			if(status != null && !status.equals(""))
			{
				JScrollPane jsp = new JScrollPane(new JTextArea(status));
				//				JOptionPane.showMessageDialog(frame, status, "Warning!", JOptionPane.WARNING_MESSAGE);
				frame.setContentPane(jsp);
				frame.setVisible(true);
			}
			else
			{
				JOptionPane.showMessageDialog(frame,
					"Success! Please move\r\n.xcur file into \"Carnegie Learning\\2006\\Administrative\\Curricula\"\r\n" +
					".unit file(s) into \"Carnegie Learning\\2006\\Administrative\\Curricula\\Unit Data\"\r\n" +
					".bas file(s) into \"Carnegie Learning\\2006\\Administrative\\Curricula\\Problem Data\"");
			
				frame.dispose();
			}
		}
	}
	
	public void itemStateChanged(ItemEvent ie)
	{
		int choice = 0;
		char val = 0;
		if(ie.getSource() == excel)
			choice = 0;
		else if(ie.getSource() == xcur)
			choice = 1;
		else if(ie.getSource() == unit)
			choice = 2;
		else if(ie.getSource() == bas)
			choice = 3;
		
		if(ie.getStateChange() == ItemEvent.DESELECTED)
			val = 'n';
		else if(ie.getStateChange() == ItemEvent.SELECTED)
			val = 'y';
		else
			trace.out("Check box is messed up?");
		options[choice] = val;
	}
}
