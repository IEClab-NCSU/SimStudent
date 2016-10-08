package TabbedTest;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.storage.StorageClient;
import javax.swing.JCheckBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import pact.CommWidgets.JCommButton;
import pact.CommWidgets.JCommMultipleChoice;
import pact.CommWidgets.JCommWidget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;


import TabbedTest.recover.AfterTestQuestionaire;
import TabbedTest.recover.AfterTestDialog;
import TabbedTest.recover.JCommComboBoxRecover;
import TabbedTest.recover.JCommMultipleChoiceRecover;
import TabbedTest.recover.JCommRecover;
import TabbedTest.recover.JCommTextAreaRecover;
import TabbedTest.recover.JCommTextFieldRecover;
import TabbedTest.recover.RecoverFileHandler;
import TabbedTest.recover.TestRecover;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TestCheckBox extends JCheckBox{
	JCommTextFieldRecover textField;
	ActionListener actionListener;
	
	public TestCheckBox(JCommTextFieldRecover field){
		textField=field;
		setText("Not Sure");
		
	  actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
        boolean selected = abstractButton.getModel().isSelected();
        if (selected){
            textField.recoverStudentAction("Not Sure");
            textField.setForeground(Color.LIGHT_GRAY);
            textField.setBackground(Color.LIGHT_GRAY);
        	textField.setText("Not Sure");
        	textField.setFocusable(false);
        }else {
             textField.setForeground(Color.BLACK);
             textField.setBackground(Color.WHITE);

        	textField.setFocusable(true);
        	textField.setText("");
        
        }
      }
    };
    
	  addActionListener(actionListener);
	  setOpaque(false);
	}
}
