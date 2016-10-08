/**
 * 
 * Curriculum Browser used in SimSt Peer Learning Environment
 * 
 */
package edu.cmu.pact.miss.PeerLearning;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.trace;

/**
 * @author mazda
 *
 */
public class StudentConfigurationView extends JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    private int scWidth = 500;
    private int scHeight = 300;
    final String SC_TITLE = "Configure SimStudent";
    final String NAME_STUDENT_LABEL = "What would you like to name your simulated student?";
    
    private JLabel nameStudentLabel;
    private JTextField nameStudentField;
    private JComboBox selectStudentCombo;
    private JLabel studentImage;
    
    private JButton okButton;
    private JButton cancelButton;
    
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public StudentConfigurationView() {
        
    	Dimension size = new Dimension(scWidth,scHeight);
        setBackground(Color.CYAN);
        this.setPreferredSize(size);
        this.setSize(size);
        this.setTitle(this.SC_TITLE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        this.add(panel);

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        
        nameStudentLabel = new JLabel(NAME_STUDENT_LABEL);
        namePanel.add(nameStudentLabel);
        
        nameStudentField = new JTextField();
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(nameStudentField); }
        nameStudentField.setMaximumSize(new Dimension(scWidth, 30));
        namePanel.add(nameStudentField);
        
        
        JPanel studentPanel = new JPanel();
        studentPanel.setLayout(new BoxLayout(studentPanel, BoxLayout.Y_AXIS));
        
        selectStudentCombo = new JComboBox();
        selectStudentCombo.setMaximumSize(new Dimension(scWidth, 30));
        studentPanel.add(selectStudentCombo);
            	
        studentImage = new JLabel(new ImageIcon("stacy.jpg"));
        studentImage.setBorder(BorderFactory.createEtchedBorder());
        studentPanel.add(studentImage);

        JPanel combinedPanel = new JPanel();
        combinedPanel.setLayout(new BoxLayout(combinedPanel, BoxLayout.X_AXIS));
        combinedPanel.add(namePanel);
        combinedPanel.add(studentPanel);
        
        panel.add(combinedPanel);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        okButton = new JButton("OK");
        buttonPanel.add(okButton);
        
        cancelButton = new JButton("CANCEL");
        buttonPanel.add(cancelButton);
        
        panel.add(buttonPanel);
        
        setModal(true);
    }
    
    
    

}
