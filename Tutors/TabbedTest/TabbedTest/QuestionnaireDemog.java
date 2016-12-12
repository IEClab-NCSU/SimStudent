package TabbedTest;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.miss.WebStartFileDownloader;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import pact.CommWidgets.JCommButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class QuestionnaireDemog extends javax.swing.JPanel implements DoneButton {

	private static final long serialVersionUID = 1L;
	
	public QuestionnaireDemog() {
		try {
		    // Set cross-platform Java L&F (also called "Metal")
	        UIManager.setLookAndFeel(
	            UIManager.getCrossPlatformLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {}
	    catch (ClassNotFoundException e) {}
	    catch (InstantiationException e) {}
	    catch (IllegalAccessException e) {}
	    
	    initComponents();


	    }
	
	public void initComponents() {


        cTAT_Options1 = new edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options();
        cTAT_Options2 = new edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options();
        
        instructions = new javax.swing.JLabel();
        
        demog1 = new pact.CommWidgets.JCommLabel();
        demog2 = new pact.CommWidgets.JCommLabel();
        demog3 = new pact.CommWidgets.JCommLabel();
        demog4 = new pact.CommWidgets.JCommLabel();
        demog5 = new pact.CommWidgets.JCommLabel();
        
        question1_demog = new pact.CommWidgets.JCommMultipleChoice();
        question2_demog = new pact.CommWidgets.JCommMultipleChoice();
        question3_demog = new pact.CommWidgets.JCommMultipleChoice();
        question4_demog = new pact.CommWidgets.JCommMultipleChoice();
        question5_demog = new pact.CommWidgets.JCommMultipleChoice();
        
        jPanel1 = new javax.swing.JPanel();
        
        jScrollPane1 = new javax.swing.JScrollPane();
        
        Done = new pact.CommWidgets.JCommButton();
        promptSuccess = new javax.swing.JLabel();
        test = new pact.CommWidgets.JCommLabel();

        horizontalLine1 = new pact.CommWidgets.HorizontalLine();
        
        cTAT_Options1.setSeparateHintWindow(true);
        cTAT_Options2.setSeparateHintWindow(true);

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(200, 200));
        setMinimumSize(new java.awt.Dimension(200, 200));
        setPreferredSize(new java.awt.Dimension(200, 200));
        setLayout(null);

        test.setFont(new java.awt.Font("SansSerif", 0, 18));
        test.setText("<HTML><b>Questionnaire</b> (Version %(test_version)%)");
        add(test);
        test.setBounds(20, 10, 420, 50);
        
        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        
        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel1.setLayout(null);
        
   
        demog1.setFont(new java.awt.Font("SansSerif", 1, 12));
        demog1.setText("1. What is your gender?");
        jPanel1.add(demog1);
        demog1.setBounds(20, 120, 450, 30);
        
        demog2.setFont(new java.awt.Font("SansSerif", 1, 12));
        demog2.setText("2. What race do you consider yourself?");
        jPanel1.add(demog2);
        demog2.setBounds(20, 180, 450, 30);
        
        demog3.setFont(new java.awt.Font("SansSerif", 1, 12));
        demog3.setText("3. What is your age?");
        jPanel1.add(demog3);
        demog3.setBounds(20, 390, 450, 30);
        
        demog4.setFont(new java.awt.Font("SansSerif", 1, 12));
        demog4.setText("4. Have you used the SimStudent program in a previous study?");
        jPanel1.add(demog4);
        demog4.setBounds(20, 450, 475, 30);
        
        demog5.setFont(new java.awt.Font("SansSerif", 1, 12));
        demog5.setText("5. What grade are you in at school?");
        jPanel1.add(demog5);
        demog5.setBounds(20, 510, 475, 30);
        
        question1_demog.setBackground(new java.awt.Color(255, 255, 255));
        question1_demog.setNChoices(2);
        question1_demog.setChoiceLayout(1);
        question1_demog.setChoiceTexts("Male,Female");
        question1_demog.setCorrectColor(new java.awt.Color(0, 0, 0));
        question1_demog.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question1_demog.setQuestionText("");
        jPanel1.add(question1_demog);
        question1_demog.setBounds(50, 150, 280, 30);
        
        question2_demog.setBackground(new java.awt.Color(255, 255, 255));
        question2_demog.setNChoices(7);
        question2_demog.setChoiceLayout(2);
        question2_demog.setChoiceTexts("American Indian or Alaska Native,Asian,Black or African American,Hispanic or Latino,Native Hawaiian or Other Pacific Islander,White,Multiracial");
        question2_demog.setCorrectColor(new java.awt.Color(0, 0, 0));
        question2_demog.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question2_demog.setQuestionText("");
        jPanel1.add(question2_demog);
        question2_demog.setBounds(50, 210, 280, 180);

        question3_demog.setBackground(new java.awt.Color(255, 255, 255));
        question3_demog.setNChoices(6);
        question3_demog.setChoiceLayout(1);
        question3_demog.setChoiceTexts("10,11,12,13,14,15");
        question3_demog.setCorrectColor(new java.awt.Color(0, 0, 0));
        question3_demog.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question3_demog.setQuestionText("");
        jPanel1.add(question3_demog);
        question3_demog.setBounds(50, 420, 350, 30);
        
        question4_demog.setBackground(new java.awt.Color(255, 255, 255));
        question4_demog.setNChoices(2);
        question4_demog.setChoiceLayout(1);
        question4_demog.setChoiceTexts("Yes,No");
        question4_demog.setCorrectColor(new java.awt.Color(0, 0, 0));
        question4_demog.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question4_demog.setQuestionText("");
        jPanel1.add(question4_demog);
        question4_demog.setBounds(50, 480, 280, 30);
        
        question5_demog.setBackground(new java.awt.Color(255, 255, 255));
        question5_demog.setNChoices(4);
        question5_demog.setChoiceLayout(1);
        question5_demog.setChoiceTexts("6th Grade, 7th Grade, 8th Grade,9th Grade");
        question5_demog.setCorrectColor(new java.awt.Color(0, 0, 0));
        question5_demog.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question5_demog.setQuestionText("");
        jPanel1.add(question5_demog);
        question5_demog.setBounds(50, 540, 380, 30);
        
        instructions.setFont(new java.awt.Font("SansSerif", 1, 12));
        instructions.setForeground(new java.awt.Color(255, 0, 0));
        instructions.setText("<HTML>Please answer the following demographic questions about yourself.  The information you provide will help us better understand the types of people who use our system.  Your participation in this questionnaire is optional.");
        jPanel1.add(instructions);
        instructions.setBounds(10, 50, 685, 80);
        
        jScrollPane1.setViewportView(jPanel1);
        

        Done.setText("<HTML><b>I'm Done</b>, Submit My Answers");
        Done.addStudentActionListener(new pact.CommWidgets.event.StudentActionListener() {
            public void studentActionPerformed(pact.CommWidgets.event.StudentActionEvent evt) {
                promptNow(evt);
            }

        });

        jPanel1.add(Done);
        Done.setBounds(230, 640, 250, 30);
        
        promptSuccess.setFont(new java.awt.Font("SansSerif", 1, 14));
        promptSuccess.setForeground(new java.awt.Color(0, 153, 0));
        jPanel1.add(promptSuccess);
        promptSuccess.setBounds(160, 570, 720, 80);
               

        jPanel1.add(horizontalLine1);
        horizontalLine1.setBounds(30, 650, 570, 10);
        
        
        add(jScrollPane1);
        jScrollPane1.setBounds(10, 70, 900, 500);
        jScrollPane1.getAccessibleContext().setAccessibleName("");
        
        jScrollPane1.getVerticalScrollBar().setSize(15,15);
        
        question1_demog.setBackground(Color.WHITE);
        question2_demog.setBackground(Color.WHITE);
        question3_demog.setBackground(Color.WHITE);
        question4_demog.setBackground(Color.WHITE);
        question5_demog.setBackground(Color.WHITE);
        
	}

    public static void main(String[] argv) {

    	CTAT_Launcher launch = new CTAT_Launcher(argv);
    	launch.launch (new QuestionnaireDemog());
    	/*BR_Controller brController = launch.getController();
        PreferencesModel pm = brController.getPreferencesModel();
       
        if (pm != null)
        {
        	pm.setBooleanValue(BR_Controller.USE_DISK_LOGGING, true);
        	pm.setStringValue(BR_Controller.DISK_LOGGING_DIR, WebStartFileDownloader.SimStWebStartDir + "log");
        }*/

    	

    }



    protected void promptNow(pact.CommWidgets.event.StudentActionEvent evt) {//GEN-FIRST:event_promptNow

        // TODO add your handling code here:

         promptSuccess.setText("Congratulations! You've completed the Questionnaire! You may now close this window!");

         //grading.setText(gradeNow());
         //writeAnswerFile();
         
         Done.setEnabled(false);

    }//GEN-LAST:event_promptNow
    
    public java.awt.Dimension getPreferredSize()

    {

        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();

        return new java.awt.Dimension(982, (int)((tk.getScreenSize().height)*0.8));

    }
    

	@Override
	public JCommButton getDoneButton() {
		return Done;
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables

    protected pact.CommWidgets.JCommButton Done;

    protected edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options cTAT_Options1;
    protected edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options cTAT_Options2;

    protected javax.swing.JLabel instructions;
    protected pact.CommWidgets.JCommLabel demog1;
    protected pact.CommWidgets.JCommLabel demog2;
    protected pact.CommWidgets.JCommLabel demog3;
    protected pact.CommWidgets.JCommLabel demog4;
     protected pact.CommWidgets.JCommLabel demog5;
    
    protected pact.CommWidgets.JCommMultipleChoice question1_demog;
    protected pact.CommWidgets.JCommMultipleChoice question2_demog;
    protected pact.CommWidgets.JCommMultipleChoice question3_demog;
    protected pact.CommWidgets.JCommMultipleChoice question4_demog;
    protected pact.CommWidgets.JCommMultipleChoice question5_demog;
    
    
    protected javax.swing.JPanel jPanel1;

    protected javax.swing.JScrollPane jScrollPane1;

    protected javax.swing.JLabel promptSuccess;
    protected pact.CommWidgets.JCommLabel test;

    protected pact.CommWidgets.HorizontalLine horizontalLine1;
    
}
