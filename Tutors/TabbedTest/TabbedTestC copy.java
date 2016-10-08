package TabbedTest;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.storage.StorageClient;
import TabbedTest.TestCheckBox;


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

public class TabbedTestC extends javax.swing.JPanel implements DoneButton, TabbedTest {

	private static final long serialVersionUID = 1L;
	public Hashtable<String, JCommWidget> interfaceElements;	//JCommWidget because list now also contains DoneButton (additional to JCommRecover elements)
	public RecoverFileHandler recoverFileHandler; 
	
	public static final String equationImage = "TabbedTest/C_eq_problems_2_4_4.png";//"TabbedTest/A_eq_problems_empty.png";
	public static final String effectiveImage = "TabbedTest/C_effective.png";
	public static final String varConstImage = "TabbedTest/C_LT_var_const.png";
	public static final String likeTermImage = "TabbedTest/C_LT_like_term.png";
	public static final String demo1Image = "TabbedTest/C_demo1.png";
	public static final String demo2Image = "TabbedTest/C_demo2.png";
	public static final String demo3Image = "TabbedTest/C_demo3.png";
	public static final String QUESTIONNAIRE_BRD = "questionnaireMT.brd";
	public static final String DEMOGRAPHIC_BRD = "demog1.brd";
	public static final String PRETEST_STEM = "Pre-Test";
	public static final String POSTTEST_STEM = "Post-Test";
	public static final String DELAYEDTEST_STEM = "Delayed-Test";
	BR_Controller brController;
	
	public TabbedTestC(BR_Controller _brController) {
		try {
		    // Set cross-platform Java L&F (also called "Metal")
	        UIManager.setLookAndFeel(
	            UIManager.getCrossPlatformLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {}
	    catch (ClassNotFoundException e) {}
	    catch (InstantiationException e) {}
	    catch (IllegalAccessException e) {}
	    
		this.brController=_brController;
		
	    initComponents();
	    initializeHash();

	    }
	
	/**
	 * Method that populates the interface elements list
	 */
	protected void initializeHash(){
        interfaceElements=new Hashtable();
        
		for (Field field : this.getClass().getDeclaredFields()) {
			field.setAccessible(true); // You might want to set modifier to public first.
			Object value=null;
			try {
				value = field.get(this);	
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} 
			if (value != null) {
				if (value instanceof JCommMultipleChoiceRecover)  {   		
					JCommMultipleChoiceRecover multipleChoice=(JCommMultipleChoiceRecover) value;
					multipleChoice.setCommName(field.getName());	
					interfaceElements.put(field.getName(),multipleChoice);
				}
				else if (value instanceof JCommTextAreaRecover)  {   		
					JCommTextAreaRecover textArea=(JCommTextAreaRecover) value;
					textArea.setCommName(field.getName());	        		
					interfaceElements.put(field.getName(),textArea);
				}
				else if (value instanceof JCommTextFieldRecover)  {   		
					JCommTextFieldRecover textArea=(JCommTextFieldRecover) value;
					textArea.setCommName(field.getName());	        		
					interfaceElements.put(field.getName(),textArea);
				}
				else if (value instanceof JCommButton){
					JCommButton btn=(JCommButton) value;
					btn.setCommName(field.getName());
					interfaceElements.put(field.getName(),btn);
				}
				else if (value instanceof JCommComboBoxRecover){
					JCommComboBoxRecover comboBox=(JCommComboBoxRecover) value;
					comboBox.setCommName(field.getName());
					interfaceElements.put(field.getName(),comboBox);
				}
			}
		}
	}
	
	
	public void initComponents() {

		URL urlTmp;
		
        cTAT_Options1 = new edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options();
        cTAT_Options2 = new edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options();
        
        
        effective_instructions = new javax.swing.JLabel();
        effective = new pact.CommWidgets.JCommLabel();

        demo_instructions = new javax.swing.JLabel();
        demo1 = new pact.CommWidgets.JCommLabel();
        demo2 = new pact.CommWidgets.JCommLabel();
        demo3 = new pact.CommWidgets.JCommLabel();
        demo1_line = new pact.CommWidgets.JCommLabel();
        demo2_line = new pact.CommWidgets.JCommLabel();
        demo3_line = new pact.CommWidgets.JCommLabel();
        demo1_exp = new pact.CommWidgets.JCommLabel();
        demo2_exp = new pact.CommWidgets.JCommLabel();
        demo3_exp = new pact.CommWidgets.JCommLabel();
        
        equation_instructions = new javax.swing.JLabel();
        equations = new pact.CommWidgets.JCommLabel();
        
        vocab_instructions = new javax.swing.JLabel();
        LT_var_const = new pact.CommWidgets.JCommLabel();
        LT_like_term = new pact.CommWidgets.JCommLabel();
        
        eq_problem1_box = new JCommTextFieldRecover(brController);//pact.CommWidgets.JCommTextField();
        eq_problem2_box = new JCommTextFieldRecover(brController);//pact.CommWidgets.JCommTextField();
        eq_problem3_box = new JCommTextFieldRecover(brController);//pact.CommWidgets.JCommTextField();
        eq_problem4_box = new JCommTextFieldRecover(brController);//pact.CommWidgets.JCommTextField();
        eq_problem5_box = new JCommTextFieldRecover(brController);//pact.CommWidgets.JCommTextField();
        eq_problem6_box = new JCommTextFieldRecover(brController);//pact.CommWidgets.JCommTextField();
        eq_problem7_box = new JCommTextFieldRecover(brController);//pact.CommWidgets.JCommTextField();
        eq_problem8_box = new JCommTextFieldRecover(brController);//pact.CommWidgets.JCommTextField();
        eq_problem9_box = new JCommTextFieldRecover(brController);//pact.CommWidgets.JCommTextField();
        eq_problem10_box = new JCommTextFieldRecover(brController);//pact.CommWidgets.JCommTextField();
        
      effective_problem1_option1 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        effective_problem1_option2 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        effective_problem1_option3 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        effective_problem1_option4 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();

        effective_problem2_option1 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        effective_problem2_option2 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        effective_problem2_option3 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        effective_problem2_option4 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();

        LT_problem1_option1 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem1_option2 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem1_option3 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem1_option4 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem1_option5 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem1_option6 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        
        LT_problem2_option1 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem2_option2 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem2_option3 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem2_option4 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem2_option5 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem2_option6 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();

        LT_problem3_option1 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem3_option2 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem3_option3 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem3_option4 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem3_option5 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem3_option6 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();

        LT_problem4_option1 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem4_option2 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem4_option3 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem4_option4 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem4_option5 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        LT_problem4_option6 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();

        demo_problem1 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        demo_problem2 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();
        demo_problem3 = new JCommMultipleChoiceRecover(brController);//new pact.CommWidgets.JCommMultipleChoice();

        demo_problem1_box = new JCommTextAreaRecover(brController);//pact.CommWidgets.JCommTextArea();
        demo_problem2_box = new JCommTextAreaRecover(brController);//pact.CommWidgets.JCommTextArea();
        demo_problem3_box = new JCommTextAreaRecover(brController);//pact.CommWidgets.JCommTextArea();
        
        horizontalLine1 = new pact.CommWidgets.HorizontalLine();
        horizontalLine2 = new pact.CommWidgets.HorizontalLine();
        horizontalLine3 = new pact.CommWidgets.HorizontalLine();
        
        coverPanel = new javax.swing.JPanel();
        jPanel1 = 	new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();

        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        
        jTabbedPane1 = new javax.swing.JTabbedPane();
        Done = new pact.CommWidgets.JCommButton();
        promptSuccess = new javax.swing.JLabel();
        test = new pact.CommWidgets.JCommLabel();
        done_instructions = new javax.swing.JLabel();
        
        cover_instructions = new javax.swing.JLabel();
        
        goPage2 = new javax.swing.JLabel();
        goPage3 = new javax.swing.JLabel();
        goDone = new javax.swing.JLabel();
        
        cTAT_Options1.setSeparateHintWindow(true);
        cTAT_Options2.setSeparateHintWindow(true);

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(200, 200));
        setMinimumSize(new java.awt.Dimension(200, 200));
        setPreferredSize(new Dimension(911, 771));
        setLayout(null);

        test.setFont(new java.awt.Font("SansSerif", 0, 18));
        test.setText("<HTML><b>Test</b> (Version %(test_version)%)");
        add(test);
        test.setBounds(20, 19, 420, 50);
        
        jTabbedPane1.setBackground(new java.awt.Color(204, 204, 255));
        jTabbedPane1.setForeground(new java.awt.Color(0, 51, 204));
        jTabbedPane1.setAutoscrolls(true);
        jTabbedPane1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(690, 690));

       
        
        
        coverPanel.setBackground(new java.awt.Color(255, 255, 255));
        coverPanel.setPreferredSize(new java.awt.Dimension(0, 0));
        coverPanel.setLayout(null);
        
        cover_instructions.setFont(new java.awt.Font("SansSerif", 1, 12));
        cover_instructions.setText("<HTML>"+
        		"<p>This test will see what you know about solving algebra equations.  It is ok if you don't know everything on the test, and your results will not affect your class grade.  There are 3 pages, and you can use the tabs to switch to the different pages.  You will have 20 minutes to complete the test.</p>"+
        		"<br><p>You will receive one point for a correct answer, you will lose one point for a wrong answer, and you will not receive or lose any points if you select 'Not Sure'. </p>"+
        		"<br><p>You are not permitted to use a calculator.  </p>"+
        		"<br><p>For one section, you will be given paper to show your work.  Do your calculations on the paper and input your final answer on the screen.  If applicable, you may leave your answers as simplified proper or improper fractions.  As a reminder, fractions like 4/5 or 8/7 are acceptable, but fractions like 3/.01 are not acceptable.</p>"+
        		"<br><p>Please be sure to work through all of the problems.</p>");
        coverPanel.add(cover_instructions);
        cover_instructions.setBounds(10, 100, 685, 300);
        cover_instructions.getAccessibleContext().setAccessibleName("<HTML>"+
        		"<p>This test will see what you know about solving algebra equations.  It is ok if you don't know everything on the test, and your results will not affect your class grade.  There are 3 pages, and you can use the tabs to switch to the different pages.  You will have 20 minutes to complete the test.</p>"+
        		"<br><p>You will receive one point for a correct answer, you will lose one point for a wrong answer, and you will not receive or lose any points if you select 'Not Sure'. </p>"+
        		"<br><p>You are not permitted to use a calculator.  </p>"+
        		"<br><p>For one section, you will be given paper to show your work.  Do your calculations on the paper and input your final answer on the screen.  If applicable, you may leave your answers as simplified proper or improper fractions.  As a reminder, fractions like 4/5 or 8/7 are acceptable, but fractions like 3/.01 are not acceptable.</p>"+
        		"<br><p>Please be sure to work through all of the problems.</p>");
        
        jTabbedPane1.addTab("Instructions", coverPanel);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        
        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel1.setLayout(null);
        
          effective_problem1_option1.setBackground(new java.awt.Color(255, 255, 255));
         effective_problem1_option1.setNChoices(3);
        effective_problem1_option1.setChoiceLayout(1);
        effective_problem1_option1.setChoiceTexts("Agree,Disagree,NotSure");
        effective_problem1_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option1.setQuestionText("");
        jPanel1.add(effective_problem1_option1);
        effective_problem1_option1.setBounds(350, 190, 290, 25);
        
        effective_problem1_option2.setBackground(new java.awt.Color(255, 255, 255));
         effective_problem1_option2.setNChoices(3);
        effective_problem1_option2.setChoiceLayout(1);
        effective_problem1_option2.setChoiceTexts("Agree,Disagree,NotSure");
        effective_problem1_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option2.setQuestionText("");
        jPanel1.add(effective_problem1_option2);
        effective_problem1_option2.setBounds(350, 220, 290, 25);
           
        
        effective_problem1_option3.setBackground(new java.awt.Color(255, 255, 255));
           effective_problem1_option3.setNChoices(3);
        effective_problem1_option3.setChoiceLayout(1);
        effective_problem1_option3.setChoiceTexts("Agree,Disagree,NotSure");
        effective_problem1_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option3.setQuestionText("");
        jPanel1.add(effective_problem1_option3);
        effective_problem1_option3.setBounds(350, 250, 290, 25); 
        
        effective_problem1_option4.setBackground(new java.awt.Color(255, 255, 255));
           effective_problem1_option4.setNChoices(3);
        effective_problem1_option4.setChoiceLayout(1);
        effective_problem1_option4.setChoiceTexts("Agree,Disagree,NotSure");
        effective_problem1_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option4.setQuestionText("");
        jPanel1.add(effective_problem1_option4);
        effective_problem1_option4.setBounds(350, 280, 290, 25);
                
        
        effective_problem2_option1.setBackground(new java.awt.Color(255, 255, 255));
        effective_problem2_option1.setNChoices(3);
        effective_problem2_option1.setChoiceLayout(1);
        effective_problem2_option1.setChoiceTexts("Agree,Disagree,NotSure");
        effective_problem2_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option1.setQuestionText("");
        jPanel1.add(effective_problem2_option1);
        effective_problem2_option1.setBounds(350, 370, 290, 25);
  
        
        
        effective_problem2_option2.setBackground(new java.awt.Color(255, 255, 255));
            effective_problem2_option2.setNChoices(3);
        effective_problem2_option2.setChoiceLayout(1);
        effective_problem2_option2.setChoiceTexts("Agree,Disagree,NotSure");
        effective_problem2_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option2.setQuestionText("");
        jPanel1.add(effective_problem2_option2);
        effective_problem2_option2.setBounds(350, 400, 290, 25);

        
        effective_problem2_option3.setBackground(new java.awt.Color(255, 255, 255));
            effective_problem2_option3.setNChoices(3);
        effective_problem2_option3.setChoiceLayout(1);
        effective_problem2_option3.setChoiceTexts("Agree,Disagree,NotSure");
        effective_problem2_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option3.setQuestionText("");
        jPanel1.add(effective_problem2_option3);
        effective_problem2_option3.setBounds(350, 430, 290, 25);

        
        effective_problem2_option4.setBackground(new java.awt.Color(255, 255, 255));
        effective_problem2_option4.setNChoices(3);
        effective_problem2_option4.setChoiceLayout(1);
        effective_problem2_option4.setChoiceTexts("Agree,Disagree,NotSure");
        effective_problem2_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option4.setQuestionText("");
        jPanel1.add(effective_problem2_option4);
        effective_problem2_option4.setBounds(350, 460, 290, 25);

        effective_instructions.setFont(new java.awt.Font("SansSerif", 1, 12));
        effective_instructions.setForeground(new java.awt.Color(255, 0, 0));
        effective_instructions.setText("<HTML>Adding 3 to both sides of 2x-3=5 is a good move to solve the equation, but adding 5 to both sides of 2x-3=5 is not a good move.  For each of the following equations answer all 4 questions (Q1-Q4) if a suggested move is a good move or not. Click AGREE if you think it is a good move, and DISAGREE otherwise.  If you don't think you know an answer, just click the NOT SURE option.    It won't help or hurt your score, but guessing incorrectly will count against you. Make sure to answer all 4 questions.");
        jPanel1.add(effective_instructions);
        effective_instructions.setBounds(10, 50, 685, 80);
        effective_instructions.getAccessibleContext().setAccessibleName("<HTML>Adding 3 to both sides of 2x-3=5 is a good move to solve the equation, but adding 5 to both sides of 2x-3=5 is not a good move.  For each of the following equations answer all 4 questions (Q1-Q4) if a suggested move is a good move or not. Click AGREE if you think it is a good move, and DISAGREE otherwise.");
    
        urlTmp = getClass().getClassLoader().getResource(effectiveImage);
       
        effective.setIcon(new ImageIcon(urlTmp));
        effective.setText("");
        jPanel1.add(effective);
        effective.setBounds(40, 155, 310, 325);
        
        demo_instructions.setFont(new java.awt.Font("SansSerif", 1, 12));
        demo_instructions.setForeground(new java.awt.Color(255, 0, 0));
        demo_instructions.setText("<HTML>Some students have solved the following equations, but they are not solved correctly.  Each equation has exactly one incorrect step in the work shown.   Read the work carefully, determine the number of the line with the error, and write it in the blank.  Below that, explain in your own words why the step you chose is incorrect.");
        jPanel1.add(demo_instructions);
        demo_instructions.setBounds(10, 500, 685, 80);
        demo_instructions.getAccessibleContext().setAccessibleName("<HTML>Some students have solved the following equations, but they are not solved correctly.  Each equation has exactly one incorrect step in the work shown.   Read the work carefully, determine the number of the line with the error, and write it in the blank.  Below that, explain in your own words why the step you chose is incorrect.");

        urlTmp = getClass().getClassLoader().getResource(demo1Image);
        demo1.setIcon(new ImageIcon(urlTmp));
        demo1.setText("");
        jPanel1.add(demo1);
        demo1.setBounds(40, 580, 310, 170);
        
        demo1_line.setFont(new java.awt.Font("SansSerif", 1, 12));
        demo1_line.setText("Which step is incorrect?  Select the line number  ");
        jPanel1.add(demo1_line);
        demo1_line.setBounds(30, 760, 300, 50);
        
        demo_problem1.setBackground(new java.awt.Color(255, 255, 255));
 
        demo_problem1.setNChoices(4);
        demo_problem1.setChoiceLayout(1);
        demo_problem1.setChoiceTexts("1,2,3,NotSure");
        demo_problem1.setCorrectColor(new java.awt.Color(0, 0, 0));
        demo_problem1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        demo_problem1.setQuestionText("");
        jPanel1.add(demo_problem1);
        demo_problem1.setBounds(300, 770, 280, 30);
 
        
        demo1_exp.setFont(new java.awt.Font("SansSerif", 1, 12));
        demo1_exp.setText("<HTML>Please explain in the following box why the step you chose is incorrect:");
        jPanel1.add(demo1_exp);
        demo1_exp.setBounds(40, 810, 540, 30);
        
        demo_problem1_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        demo_problem1_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        jPanel1.add(demo_problem1_box);
        demo_problem1_box.setBounds(30, 840, 600, 90);
		demo_problem1.textBox=demo_problem1_box;


        urlTmp = getClass().getClassLoader().getResource(demo2Image);
        demo2.setIcon(new ImageIcon(urlTmp));
        demo2.setText("");
        jPanel1.add(demo2);
        demo2.setBounds(40, 950, 310, 170);
        
        demo2_line.setFont(new java.awt.Font("SansSerif", 1, 12));
        demo2_line.setText("Which step is incorrect?  Select the line number  ");
        jPanel1.add(demo2_line);
        demo2_line.setBounds(30, 1130, 300, 50);
        
        demo_problem2.setBackground(new java.awt.Color(255, 255, 255));
        demo_problem2.setNChoices(4);
        demo_problem2.setChoiceLayout(1);
        demo_problem2.setChoiceTexts("1,2,3,NotSure");
        demo_problem2.setCorrectColor(new java.awt.Color(0, 0, 0));
        demo_problem2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        demo_problem2.setQuestionText("");
        jPanel1.add(demo_problem2);
        demo_problem2.setBounds(300, 1140, 280, 30);

            
        demo2_exp.setFont(new java.awt.Font("SansSerif", 1, 12));
        demo2_exp.setText("<HTML>Please explain in the following box why the step you chose is incorrect:");
        jPanel1.add(demo2_exp);
        demo2_exp.setBounds(40, 1180, 540, 30);
        
        demo_problem2_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        demo_problem2_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        jPanel1.add(demo_problem2_box);
        demo_problem2_box.setBounds(30, 1210, 600, 90);
		demo_problem2.textBox=demo_problem2_box;


        urlTmp = getClass().getClassLoader().getResource(demo3Image);
        demo3.setIcon(new ImageIcon(urlTmp));
        demo3.setText("");
        jPanel1.add(demo3);
        demo3.setBounds(40, 1320, 310, 240);
        
        demo3_line.setFont(new java.awt.Font("SansSerif", 1, 12));
        demo3_line.setText("Which step is incorrect?  Select the line number  ");
        jPanel1.add(demo3_line);
        demo3_line.setBounds(30, 1570, 300, 50);
        
        demo_problem3.setBackground(new java.awt.Color(255, 255, 255));
        demo_problem3.setNChoices(6);
        demo_problem3.setChoiceLayout(1);
        demo_problem3.setChoiceTexts("1,2,3,4,5,NotSure");
        demo_problem3.setCorrectColor(new java.awt.Color(0, 0, 0));
        demo_problem3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        demo_problem3.setQuestionText("");
        jPanel1.add(demo_problem3);
        demo_problem3.setBounds(300, 1580, 350, 30);


        demo3_exp.setFont(new java.awt.Font("SansSerif", 1, 12));
        demo3_exp.setText("<HTML>Please explain in the following box why the step you chose is incorrect:");
        jPanel1.add(demo3_exp);
        demo3_exp.setBounds(40, 1620, 540, 30);
        
        demo_problem3_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        demo_problem3_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        jPanel1.add(demo_problem3_box);
        demo_problem3_box.setBounds(30, 1650, 600, 90);
        demo_problem3.textBox=demo_problem3_box;


        jPanel1.add(horizontalLine1);
        horizontalLine1.setBounds(30, 1770, 570, 10);
        
        goPage2.setFont(new java.awt.Font("SansSerif", 1, 12));
        goPage2.setForeground(new java.awt.Color(255, 0, 0));
        goPage2.setText("<HTML>Click the [Page 2] tab at the top to continue to the next page.");
        jPanel1.add(goPage2);
        goPage2.setBounds(30, 1790, 540, 50);
        
        JLabel tmp = new JLabel();
        tmp.setBounds(30, 1850, 540, 10);
        jPanel1.add(tmp);
        
        jScrollPane1.setViewportView(jPanel1);
        jTabbedPane1.addTab("Page 1", jScrollPane1);

        
        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        
        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel2.setLayout(null);
        
        eq_problem1_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem1_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem1_box.setShowBorder(false);
        jPanel2.add(eq_problem1_box);
        eq_problem1_box.setBounds(510, 90, 90, 30);
	
		TestCheckBox tickBox_eq_problem1 = new TestCheckBox(eq_problem1_box);
		tickBox_eq_problem1.setBounds(610, 90, 90, 30);
		jPanel2.add(tickBox_eq_problem1);
		

        eq_problem2_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem2_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem2_box.setShowBorder(false);
        jPanel2.add(eq_problem2_box);
        eq_problem2_box.setBounds(510, 150, 90, 30);
        
        TestCheckBox tickBox_eq_problem2 = new TestCheckBox(eq_problem2_box);
		tickBox_eq_problem2.setBounds(610, 150, 90, 30);
		jPanel2.add(tickBox_eq_problem2);
		
		
        eq_problem3_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem3_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem3_box.setShowBorder(false);
        jPanel2.add(eq_problem3_box);
        eq_problem3_box.setBounds(510, 210, 90, 30);

	
		TestCheckBox tickBox_eq_problem3 = new TestCheckBox(eq_problem3_box);
		tickBox_eq_problem3.setBounds(610, 210, 90, 30);
		jPanel2.add(tickBox_eq_problem3);
	
	

        eq_problem4_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem4_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem4_box.setShowBorder(false);
        jPanel2.add(eq_problem4_box);
        eq_problem4_box.setBounds(510, 270, 90, 30);
		
		TestCheckBox tickBox_eq_problem4 = new TestCheckBox(eq_problem4_box);
		tickBox_eq_problem4.setBounds(610, 270, 90, 30);
		jPanel2.add(tickBox_eq_problem4);
	
	
        eq_problem5_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem5_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem5_box.setShowBorder(false);
        jPanel2.add(eq_problem5_box);
        eq_problem5_box.setBounds(510, 340, 90, 30);

		TestCheckBox tickBox_eq_problem5 = new TestCheckBox(eq_problem5_box);
		tickBox_eq_problem5.setBounds(610, 340, 90, 30);
		jPanel2.add(tickBox_eq_problem5);
		
		
		
        eq_problem6_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem6_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem6_box.setShowBorder(false);
        jPanel2.add(eq_problem6_box);
        eq_problem6_box.setBounds(510, 420, 90, 30);

		
		TestCheckBox tickBox_eq_problem6 = new TestCheckBox(eq_problem6_box);
		tickBox_eq_problem6.setBounds(610, 420, 90, 30);
		jPanel2.add(tickBox_eq_problem6);
	
	

        eq_problem7_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem7_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem7_box.setShowBorder(false);
        jPanel2.add(eq_problem7_box);
        eq_problem7_box.setBounds(510, 490, 90, 30);



		TestCheckBox tickBox_eq_problem7 = new TestCheckBox(eq_problem7_box);
		tickBox_eq_problem7.setBounds(610, 490, 90, 30);
		jPanel2.add(tickBox_eq_problem7);
	
	
        eq_problem8_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem8_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem8_box.setShowBorder(false);
        jPanel2.add(eq_problem8_box);
        eq_problem8_box.setBounds(510, 550, 90, 30);



		TestCheckBox tickBox_eq_problem8 = new TestCheckBox(eq_problem8_box);
		tickBox_eq_problem8.setBounds(610, 550, 90, 30);
		jPanel2.add(tickBox_eq_problem8);
	
	
	
        eq_problem9_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem9_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem9_box.setShowBorder(false);
        jPanel2.add(eq_problem9_box);
        eq_problem9_box.setBounds(510, 610, 90, 30);


		TestCheckBox tickBox_eq_problem9 = new TestCheckBox(eq_problem9_box);
		tickBox_eq_problem9.setBounds(610, 610, 90, 30);
		jPanel2.add(tickBox_eq_problem9);
	
	
        eq_problem10_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem10_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem10_box.setShowBorder(false);
        jPanel2.add(eq_problem10_box);
        eq_problem10_box.setBounds(510, 670, 90, 30);


		TestCheckBox tickBox_eq_problem10 = new TestCheckBox(eq_problem10_box);
		tickBox_eq_problem10.setBounds(610, 670, 90, 30);
		jPanel2.add(tickBox_eq_problem10);
	
	
        jPanel2.add(horizontalLine2);
        horizontalLine2.setBounds(30, 720, 570, 10);

        goPage3.setFont(new java.awt.Font("SansSerif", 1, 12));
        goPage3.setForeground(new java.awt.Color(255, 0, 0));
        goPage3.setText("<HTML>Click the [Page 3] tab at the top to continue to the next page.");
        jPanel2.add(goPage3);
        goPage3.setBounds(30, 740, 540, 50);
        
        tmp = new JLabel();
        tmp.setBounds(30, 800, 540, 10);
        jPanel2.add(tmp);

        equation_instructions.setFont(new java.awt.Font("SansSerif", 1, 12));
        equation_instructions.setForeground(new java.awt.Color(255, 0, 0));
        equation_instructions.setText("<HTML><b> Solve the following equations, show your work on the test form and enter answers in corresponding boxes below.</b>");
        jPanel2.add(equation_instructions);
        equation_instructions.setBounds(10, 10, 685, 60);
        
        urlTmp = getClass().getClassLoader().getResource(equationImage);
        equations.setIcon(new ImageIcon(urlTmp));
        equations.setText("");
        jPanel2.add(equations);
        equations.setBounds(30, 90, 500, 620);
        
        jScrollPane2.setViewportView(jPanel2);
        jTabbedPane1.addTab("Page 2", jScrollPane2);
        
        jScrollPane3.setBackground(new java.awt.Color(255, 255, 255));
        
        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel3.setLayout(null);
        
        LT_problem1_option1.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem1_option1.setNChoices(3);
        LT_problem1_option1.setChoiceLayout(1);
        LT_problem1_option1.setChoiceTexts("True,False,NotSure");
        LT_problem1_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option1.setQuestionText("");
        jPanel3.add(LT_problem1_option1);
        LT_problem1_option1.setBounds(490, 150, 230, 30);


        LT_problem1_option2.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem1_option2.setNChoices(3);
        LT_problem1_option2.setChoiceLayout(1);
        LT_problem1_option2.setChoiceTexts("True,False,NotSure");
        LT_problem1_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option2.setQuestionText("");
        jPanel3.add(LT_problem1_option2);
        LT_problem1_option2.setBounds(490, 180, 230, 30);
        
        LT_problem1_option3.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem1_option3.setName("LT_problem1_option3");
        LT_problem1_option3.setNChoices(3);
        LT_problem1_option3.setChoiceLayout(1);
        LT_problem1_option3.setChoiceTexts("True,False,NotSure");
        LT_problem1_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option3.setQuestionText("");
        jPanel3.add(LT_problem1_option3);
        LT_problem1_option3.setBounds(490, 210, 230, 30);
        
        LT_problem1_option4.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem1_option4.setNChoices(3);
        LT_problem1_option4.setChoiceLayout(1);
        LT_problem1_option4.setChoiceTexts("True,False,NotSure");
        LT_problem1_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option4.setQuestionText("");
        jPanel3.add(LT_problem1_option4);
        LT_problem1_option4.setBounds(490, 240, 230, 30);


        LT_problem1_option5.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem1_option5.setNChoices(3);
        LT_problem1_option5.setChoiceLayout(1);
        LT_problem1_option5.setChoiceTexts("True,False,NotSure");
        LT_problem1_option5.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option5.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option5.setQuestionText("");
        jPanel3.add(LT_problem1_option5);
        LT_problem1_option5.setBounds(490, 270, 230, 30);
        LT_problem1_option6.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem1_option6.setNChoices(3);
        LT_problem1_option6.setChoiceLayout(1);
        LT_problem1_option6.setChoiceTexts("True,False,NotSure");
        LT_problem1_option6.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option6.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option6.setQuestionText("");
        jPanel3.add(LT_problem1_option6);
        LT_problem1_option6.setBounds(490, 300, 230, 30);

        LT_problem2_option1.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem2_option1.setNChoices(3);
        LT_problem2_option1.setChoiceLayout(1);
        LT_problem2_option1.setChoiceTexts("True,False,NotSure");
        LT_problem2_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option1.setQuestionText("");
        jPanel3.add(LT_problem2_option1);
        LT_problem2_option1.setBounds(490, 360, 230, 30);
 

        LT_problem2_option2.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem2_option2.setNChoices(3);
        LT_problem2_option2.setChoiceLayout(1);
        LT_problem2_option2.setChoiceTexts("True,False,NotSure");
        LT_problem2_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option2.setQuestionText("");
        jPanel3.add(LT_problem2_option2);
        LT_problem2_option2.setBounds(490, 390, 230, 30);


        LT_problem2_option3.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem2_option3.setNChoices(3);
        LT_problem2_option3.setChoiceLayout(1);
        LT_problem2_option3.setChoiceTexts("True,False,NotSure");
        LT_problem2_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option3.setQuestionText("");
        jPanel3.add(LT_problem2_option3);
        LT_problem2_option3.setBounds(490, 420, 230, 30);


        LT_problem2_option4.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem2_option4.setNChoices(3);
        LT_problem2_option4.setChoiceLayout(1);
        LT_problem2_option4.setChoiceTexts("True,False,NotSure");
        LT_problem2_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option4.setQuestionText("");
        jPanel3.add(LT_problem2_option4);
        LT_problem2_option4.setBounds(490, 450, 230, 30);


        LT_problem2_option5.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem2_option5.setNChoices(3);
        LT_problem2_option5.setChoiceLayout(1);
        LT_problem2_option5.setChoiceTexts("True,False,NotSure");
        LT_problem2_option5.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option5.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option5.setQuestionText("");
        jPanel3.add(LT_problem2_option5);
        LT_problem2_option5.setBounds(490, 480, 230, 30);


        LT_problem2_option6.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem2_option6.setNChoices(3);
        LT_problem2_option6.setChoiceLayout(1);
        LT_problem2_option6.setChoiceTexts("True,False,NotSure");
        LT_problem2_option6.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option6.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option6.setQuestionText("");
        jPanel3.add(LT_problem2_option6);
        LT_problem2_option6.setBounds(490, 510, 230, 30);

        LT_problem3_option1.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem3_option1.setNChoices(3);
        LT_problem3_option1.setChoiceLayout(1);
        LT_problem3_option1.setChoiceTexts("True,False,NotSure");
        LT_problem3_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option1.setQuestionText("");
        jPanel3.add(LT_problem3_option1);
        LT_problem3_option1.setBounds(310, 570, 240, 30);

        LT_problem3_option2.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem3_option2.setNChoices(3);
        LT_problem3_option2.setChoiceLayout(1);
        LT_problem3_option2.setChoiceTexts("True,False,NotSure");
        LT_problem3_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option2.setQuestionText("");
        jPanel3.add(LT_problem3_option2);
        LT_problem3_option2.setBounds(310, 600, 240, 30);
        
        LT_problem3_option3.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem3_option3.setNChoices(3);
        LT_problem3_option3.setChoiceLayout(1);
        LT_problem3_option3.setChoiceTexts("True,False,NotSure");
        LT_problem3_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option3.setQuestionText("");
        jPanel3.add(LT_problem3_option3);
        LT_problem3_option3.setBounds(310, 630, 240, 30);


        LT_problem3_option4.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem3_option4.setNChoices(3);
        LT_problem3_option4.setChoiceLayout(1);
        LT_problem3_option4.setChoiceTexts("True,False,NotSure");
        LT_problem3_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option4.setQuestionText("");
        jPanel3.add(LT_problem3_option4);
        LT_problem3_option4.setBounds(310, 660, 240, 30);

        LT_problem3_option5.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem3_option5.setNChoices(3);
        LT_problem3_option5.setChoiceLayout(1);
        LT_problem3_option5.setChoiceTexts("True,False,NotSure");
        LT_problem3_option5.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option5.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option5.setQuestionText("");
        jPanel3.add(LT_problem3_option5);
        LT_problem3_option5.setBounds(310, 690, 240, 30);

        LT_problem3_option6.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem3_option6.setNChoices(3);
        LT_problem3_option6.setChoiceLayout(1);
        LT_problem3_option6.setChoiceTexts("True,False,NotSure");
        LT_problem3_option6.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option6.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option6.setQuestionText("");
        jPanel3.add(LT_problem3_option6);
        LT_problem3_option6.setBounds(310, 720, 240, 30);
        

        LT_problem4_option1.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem4_option1.setNChoices(3);
        LT_problem4_option1.setChoiceLayout(1);
        LT_problem4_option1.setChoiceTexts("True,False,NotSure");
        LT_problem4_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option1.setQuestionText("");
        jPanel3.add(LT_problem4_option1);
        LT_problem4_option1.setBounds(310, 780, 240, 30);


        LT_problem4_option2.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem4_option2.setNChoices(3);
        LT_problem4_option2.setChoiceLayout(1);
        LT_problem4_option2.setChoiceTexts("True,False,NotSure");
        LT_problem4_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option2.setQuestionText("");
        jPanel3.add(LT_problem4_option2);
        LT_problem4_option2.setBounds(310, 810, 240, 30);


        LT_problem4_option3.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem4_option3.setNChoices(3);
        LT_problem4_option3.setChoiceLayout(1);
        LT_problem4_option3.setChoiceTexts("True,False,NotSure");
        LT_problem4_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option3.setQuestionText("");
        jPanel3.add(LT_problem4_option3);
        LT_problem4_option3.setBounds(310, 840, 240, 30);


        LT_problem4_option4.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem4_option4.setNChoices(3);
        LT_problem4_option4.setChoiceLayout(1);
        LT_problem4_option4.setChoiceTexts("True,False,NotSure");
        LT_problem4_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option4.setQuestionText("");
        jPanel3.add(LT_problem4_option4);
        LT_problem4_option4.setBounds(310, 870, 240, 30);


        LT_problem4_option5.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem4_option5.setNChoices(3);
        LT_problem4_option5.setChoiceLayout(1);
        LT_problem4_option5.setChoiceTexts("True,False,NotSure");
        LT_problem4_option5.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option5.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option5.setQuestionText("");
        jPanel3.add(LT_problem4_option5);
        LT_problem4_option5.setBounds(310, 900, 240, 30);
        
        LT_problem4_option6.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem4_option6.setNChoices(3);
        LT_problem4_option6.setChoiceLayout(1);
        LT_problem4_option6.setChoiceTexts("True,False,NotSure");
        LT_problem4_option6.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option6.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option6.setQuestionText("");
        jPanel3.add(LT_problem4_option6);
        LT_problem4_option6.setBounds(310, 930, 240, 30);

        jPanel3.add(horizontalLine3);
        horizontalLine3.setBounds(30, 990, 570, 10);

        goDone.setFont(new java.awt.Font("SansSerif", 1, 12));
        goDone.setForeground(new java.awt.Color(255, 0, 0));
        goDone.setText("<HTML>Click the [Done and Submit] tab at the top to continue to the submission page.");
        jPanel3.add(goDone);
        goDone.setBounds(30, 1000, 540, 50);
        
        tmp = new JLabel();
        tmp.setBounds(30, 1050, 540, 10);
        jPanel3.add(tmp);
       
        vocab_instructions.setFont(new java.awt.Font("SansSerif", 1, 12));
        vocab_instructions.setForeground(new java.awt.Color(255, 0, 0));
        vocab_instructions.setText("<html>For each of the following, click TRUE if the statement is true and click FALSE otherwise.  If you don't think you know an answer, just click the NOT SURE option.  It won't help or hurt your score, but guessing incorrectly will count against you.");
        jPanel3.add(vocab_instructions);
        vocab_instructions.setBounds(10, 30, 685, 61);
        
        urlTmp = getClass().getClassLoader().getResource(varConstImage);
        LT_var_const.setIcon(new ImageIcon(urlTmp));
        LT_var_const.setText("");
        jPanel3.add(LT_var_const);
        LT_var_const.setBounds(10, 150, 475, 387);
        
        urlTmp = getClass().getClassLoader().getResource(likeTermImage);
        LT_like_term.setIcon(new ImageIcon(urlTmp));
        LT_like_term.setText("");
        jPanel3.add(LT_like_term);
        LT_like_term.setBounds(10, 570, 390, 390);
        
        jScrollPane3.setViewportView(jPanel3);
        jTabbedPane1.addTab("Page 3", jScrollPane3);
        
        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(null);


        Done.setText("<HTML><b>I'm Done</b>, Submit My Answers");
        Done.setCommName("done");
        Done.addStudentActionListener(new pact.CommWidgets.event.StudentActionListener() {
            public void studentActionPerformed(pact.CommWidgets.event.StudentActionEvent evt) {  
                promptNow(evt);              
            }

        });

        jPanel4.add(Done);
        Done.setBounds(230, 290, 250, 30);
        done_instructions.setForeground(new java.awt.Color(255, 0, 0));
        done_instructions.setText("<HTML><b>Click on the button that says [I'm done, Submit My Answers] to officially submit your responses.</b>");
        jPanel4.add(done_instructions);
        done_instructions.setBounds(60, 160, 686, 100);

        promptSuccess.setFont(new java.awt.Font("SansSerif", 1, 14));
        promptSuccess.setForeground(new java.awt.Color(0, 153, 0));
        jPanel4.add(promptSuccess);
        promptSuccess.setBounds(216, 332, 320, 80);
        jTabbedPane1.addTab("Page 4", jPanel4);
        
        btnTimeout = new JButton("Timeout");
        btnTimeout.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		new TestTimeOut(interfaceElements);
        	}
        });
        btnTimeout.setBounds(306, 450, 117, 29);
        jPanel4.add(btnTimeout);

        
        add(jTabbedPane1);
        jTabbedPane1.setBounds(10, 70, 895, 604);
        jTabbedPane1.getAccessibleContext().setAccessibleName("");
        
       // jTabbedPane1.setEnabled(false);
    	
    	
        jScrollPane1.getVerticalScrollBar().setSize(15,15);
        jScrollPane2.getVerticalScrollBar().setSize(15,15);
        jScrollPane3.getVerticalScrollBar().setSize(15,15);
        

        LT_problem1_option1.setBackground(Color.WHITE);
        LT_problem1_option2.setBackground(Color.WHITE);
        LT_problem1_option3.setBackground(Color.WHITE);
        LT_problem1_option4.setBackground(Color.WHITE);
        LT_problem1_option5.setBackground(Color.WHITE);
        LT_problem1_option6.setBackground(Color.WHITE);
        
        LT_problem2_option1.setBackground(Color.WHITE);
        LT_problem2_option2.setBackground(Color.WHITE);
        LT_problem2_option3.setBackground(Color.WHITE);
        LT_problem2_option4.setBackground(Color.WHITE);
        LT_problem2_option5.setBackground(Color.WHITE);
        LT_problem2_option6.setBackground(Color.WHITE);

        LT_problem3_option1.setBackground(Color.WHITE);
        LT_problem3_option2.setBackground(Color.WHITE);
        LT_problem3_option3.setBackground(Color.WHITE);
        LT_problem3_option4.setBackground(Color.WHITE);
        LT_problem3_option5.setBackground(Color.WHITE);
        LT_problem3_option6.setBackground(Color.WHITE);

        LT_problem4_option1.setBackground(Color.WHITE);
        LT_problem4_option2.setBackground(Color.WHITE);
        LT_problem4_option3.setBackground(Color.WHITE);
        LT_problem4_option4.setBackground(Color.WHITE);
        LT_problem4_option5.setBackground(Color.WHITE);
        LT_problem4_option6.setBackground(Color.WHITE);
        
        demo_problem1.setBackground(Color.WHITE);
        demo_problem2.setBackground(Color.WHITE);
        demo_problem3.setBackground(Color.WHITE);
        
        effective_problem1_option1.setBackground(Color.WHITE);
        effective_problem1_option2.setBackground(Color.WHITE);
        effective_problem1_option3.setBackground(Color.WHITE);
        effective_problem1_option4.setBackground(Color.WHITE);

        effective_problem2_option1.setBackground(Color.WHITE);
        effective_problem2_option2.setBackground(Color.WHITE);
        effective_problem2_option3.setBackground(Color.WHITE);
        effective_problem2_option4.setBackground(Color.WHITE);

	}

	public static void main(String[] argv) throws IOException {

    	CTAT_Launcher launch = new CTAT_Launcher(argv);
    	    	        
    	TabbedTestC test=new TabbedTestC(launch.getController());
    	launch.launch (test); 	
    	BR_Controller brController = launch.getController();
        PreferencesModel pm = brController.getPreferencesModel();
    /*    if (pm != null)
        {
        	pm.setBooleanValue(BR_Controller.USE_DISK_LOGGING, true);
        	pm.setStringValue(BR_Controller.DISK_LOGGING_DIR, WebStartFileDownloader.SimStWebStartDir + "log");
        }
      */  
		//new TestRecover(test.interfaceElements,brController,test.jTabbedPane1).resumeTest();
		
		argv1=argv;
	
		
		
  		
		
    }
	static String argv1[];

	private static void updateBrd(String str){
		for (int i=0;i<argv1.length;i++){		
  			if (argv1[i].contains("DProblemFileURL=jar:http://10.16.0.133:2401/studyTests/lib_nb/tabbedtest.jar!/")){
  				argv1[i]="-DProblemFileURL=jar:http://10.16.0.133:2401/studyTests/lib_nb/tabbedtest.jar!/TabbedTest/"+str;
  				break;
  			}
  		}
		
	}
	
	
	private static String getCondition(){
		String condition="";
		for (int i=0;i<argv1.length;i++){		
  			if(argv1[i].contains(PRETEST_STEM)){
  				condition=PRETEST_STEM;
  				break;
  			}
  			else if (argv1[i].contains(POSTTEST_STEM)){
  				condition=POSTTEST_STEM;
  				break;
  			}
  			else{
  				condition=DELAYEDTEST_STEM;
  			}
  		}
		return condition;
	}
    protected void promptNow(pact.CommWidgets.event.StudentActionEvent evt) {//GEN-FIRST:event_promptNow

        // TODO add your handling code here:

        promptSuccess.setText("Congratulations! You've completed the Test!");
        
        String condition=getCondition();
		brController.closeStudentInterface();
		if (condition.equals(POSTTEST_STEM)){

		//	int dialogResult = JOptionPane.showConfirmDialog (null, "Congratulations, you have completed the quiz! \nWould you like to take a questionaire about your experience in the SimStudent study?","SimStudent Online Test",JOptionPane.YES_NO_OPTION);						
			AfterTestDialog dialog = new AfterTestDialog();
			int dialogResult=dialog.showDialog();
			
			if(dialogResult == AfterTestDialog.YES_OPTION){
				updateBrd(QUESTIONNAIRE_BRD);

				CTAT_Launcher launch1 = new CTAT_Launcher(argv1);

				launch1.getController().getLogger().setUnitName("Questionnaire");
				QuestionnaireMT questionaire = new QuestionnaireMT(launch1.getController());
				launch1.launch (questionaire);

			}
			else{
				JOptionPane.showMessageDialog(null, "Thank you for your participation in the study!");
				brController.closeApplication(true);
			}
		}
		else if (condition.equals(PRETEST_STEM)){
			
			//int dialogResult = JOptionPane.showConfirmDialog (null, "Congratulations, you have completed the quiz! \nWould you like to take a short demographics questionaire?","SimStudent Online Test",JOptionPane.YES_NO_OPTION);
			AfterTestQuestionaire dialog = new AfterTestQuestionaire();
			int dialogResult=dialog.showDialog();
			
			
			if(dialogResult == AfterTestQuestionaire.YES_OPTION){
				updateBrd(DEMOGRAPHIC_BRD);
				CTAT_Launcher launch1 = new CTAT_Launcher(argv1);
				launch1.getController().getLogger().setUnitName("Demographics");
				QuestionnaireDemog demog = new QuestionnaireDemog();
				launch1.launch (demog);
			}
			else{
				JOptionPane.showMessageDialog(null, "Thank you for your participation in the study!");
				brController.closeApplication(true);
			}
			
			
		}
		else{
			JOptionPane.showMessageDialog(null, "Thank you for your participation in the study!");
			brController.closeApplication(true);
		}
  		

         Done.setEnabled(false);
   

    }//GEN-LAST:event_promptNow
    
    public java.awt.Dimension getPreferredSize()
    {

        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();

        return new java.awt.Dimension(982, (int)((tk.getScreenSize().height)*0.8));

    }
    
    public void setTabHeight(int height)
    {
    	Rectangle tabBounds = jTabbedPane1.getBounds();
    	jTabbedPane1.setBounds(tabBounds.x, tabBounds.y, tabBounds.width+10, height);
    	
    }

	@Override
	public JCommButton getDoneButton() {
		return Done;
	}

	protected pact.CommWidgets.JCommButton Done;

    protected edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options cTAT_Options1;
    protected edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options cTAT_Options2;

    protected javax.swing.JLabel effective_instructions;
    protected pact.CommWidgets.JCommLabel effective;
    protected javax.swing.JLabel demo_instructions;
    protected pact.CommWidgets.JCommLabel demo1;
    protected pact.CommWidgets.JCommLabel demo2;
    protected pact.CommWidgets.JCommLabel demo3;
    protected pact.CommWidgets.JCommLabel demo1_line;
    protected pact.CommWidgets.JCommLabel demo2_line;
    protected pact.CommWidgets.JCommLabel demo3_line;
    protected pact.CommWidgets.JCommLabel demo1_exp;
    protected pact.CommWidgets.JCommLabel demo2_exp;
    protected pact.CommWidgets.JCommLabel demo3_exp;
    
    protected javax.swing.JLabel equation_instructions;
    protected pact.CommWidgets.JCommLabel equations;
    
    protected javax.swing.JLabel vocab_instructions;
    protected pact.CommWidgets.JCommLabel LT_var_const;
    protected pact.CommWidgets.JCommLabel LT_like_term;

    protected JCommTextFieldRecover eq_problem1_box;
    protected JCommTextFieldRecover eq_problem2_box;
    protected JCommTextFieldRecover eq_problem3_box;
    protected JCommTextFieldRecover eq_problem4_box;
    protected JCommTextFieldRecover eq_problem5_box;
    protected JCommTextFieldRecover eq_problem6_box;
    protected JCommTextFieldRecover eq_problem7_box;
    protected JCommTextFieldRecover eq_problem8_box;
    protected JCommTextFieldRecover eq_problem9_box;
    protected JCommTextFieldRecover eq_problem10_box;
    
    protected JCommMultipleChoiceRecover effective_problem1_option1;
    protected JCommMultipleChoiceRecover effective_problem1_option2;
    protected JCommMultipleChoiceRecover effective_problem1_option3;
    protected JCommMultipleChoiceRecover effective_problem1_option4;
    
    protected JCommMultipleChoiceRecover effective_problem2_option1;
    protected JCommMultipleChoiceRecover effective_problem2_option2;
    protected JCommMultipleChoiceRecover effective_problem2_option3;
    protected JCommMultipleChoiceRecover effective_problem2_option4;
    
    protected JCommMultipleChoiceRecover LT_problem1_option1;
    protected JCommMultipleChoiceRecover LT_problem1_option2;
    protected JCommMultipleChoiceRecover LT_problem1_option3;
    protected JCommMultipleChoiceRecover LT_problem1_option4;
    protected JCommMultipleChoiceRecover LT_problem1_option5;
    protected JCommMultipleChoiceRecover LT_problem1_option6;
    
    protected JCommMultipleChoiceRecover LT_problem2_option1;
    protected JCommMultipleChoiceRecover LT_problem2_option2;
    protected JCommMultipleChoiceRecover LT_problem2_option3;
    protected JCommMultipleChoiceRecover LT_problem2_option4;
    protected JCommMultipleChoiceRecover LT_problem2_option5;
    protected JCommMultipleChoiceRecover LT_problem2_option6;

    protected JCommMultipleChoiceRecover LT_problem3_option1;
    protected JCommMultipleChoiceRecover LT_problem3_option2;
    protected JCommMultipleChoiceRecover LT_problem3_option3;
    protected JCommMultipleChoiceRecover LT_problem3_option4;
    protected JCommMultipleChoiceRecover LT_problem3_option5;
    protected JCommMultipleChoiceRecover LT_problem3_option6;

    protected JCommMultipleChoiceRecover LT_problem4_option1;
    protected JCommMultipleChoiceRecover LT_problem4_option2;
    protected JCommMultipleChoiceRecover LT_problem4_option3;
    protected JCommMultipleChoiceRecover LT_problem4_option4;
    protected JCommMultipleChoiceRecover LT_problem4_option5;
    protected JCommMultipleChoiceRecover LT_problem4_option6;
    
    protected JCommMultipleChoiceRecover demo_problem1;
    protected JCommMultipleChoiceRecover demo_problem2;
    protected JCommMultipleChoiceRecover demo_problem3;
    
    protected JCommTextAreaRecover demo_problem1_box;
    protected JCommTextAreaRecover demo_problem2_box;
    protected JCommTextAreaRecover demo_problem3_box;
    
    protected pact.CommWidgets.HorizontalLine horizontalLine1;
    protected pact.CommWidgets.HorizontalLine horizontalLine2;
    protected pact.CommWidgets.HorizontalLine horizontalLine3;
    
    protected javax.swing.JPanel coverPanel;
    
    protected javax.swing.JPanel jPanel1;

    protected javax.swing.JPanel jPanel2;

    protected javax.swing.JPanel jPanel3;

    protected javax.swing.JPanel jPanel4;
    
    protected javax.swing.JScrollPane jScrollPane1;

    protected javax.swing.JScrollPane jScrollPane2;

    protected javax.swing.JScrollPane jScrollPane3;

    public javax.swing.JTabbedPane jTabbedPane1;

    protected javax.swing.JLabel promptSuccess;
    protected pact.CommWidgets.JCommLabel test;
    protected javax.swing.JLabel done_instructions;
    
    protected javax.swing.JLabel cover_instructions;
    
    protected javax.swing.JLabel goPage2;
    protected javax.swing.JLabel goPage3;
    protected javax.swing.JLabel goDone;
    private JButton btnTimeout;
	
	
}
