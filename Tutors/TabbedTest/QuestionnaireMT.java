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


public class QuestionnaireMT extends javax.swing.JPanel implements DoneButton {

	private static final long serialVersionUID = 1L;
	
	public static final String equationImage = "TabbedTest/A_eq_problems.png";
	public static final String effectiveImage = "TabbedTest/A_effective.png";
	public static final String varConstImage = "TabbedTest/A_LT_var_const.png";
	public static final String likeTermImage = "TabbedTest/A_LT_like_term.png";
	public static final String demo1Image = "TabbedTest/A_demo1.png";
	public static final String demo2Image = "TabbedTest/A_demo2.png";
	public static final String demo3Image = "TabbedTest/A_demo3.png";
	BR_Controller brController;
	public QuestionnaireMT(BR_Controller brController) {
		this.brController=brController;
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
        rating1 = new javax.swing.JLabel();
        rating7 = new javax.swing.JLabel();
        
        mastery1 = new pact.CommWidgets.JCommLabel();
        mastery2 = new pact.CommWidgets.JCommLabel();
        mastery3 = new pact.CommWidgets.JCommLabel();
        mastery4 = new pact.CommWidgets.JCommLabel();
        
        question1_mastery = new pact.CommWidgets.JCommMultipleChoice();
        question2_mastery = new pact.CommWidgets.JCommMultipleChoice();
        question3_mastery = new pact.CommWidgets.JCommMultipleChoice();
        question4_mastery = new pact.CommWidgets.JCommMultipleChoice();
        
        performance5 = new pact.CommWidgets.JCommLabel();
        performance6 = new pact.CommWidgets.JCommLabel();
        performance7 = new pact.CommWidgets.JCommLabel();
        performance8 = new pact.CommWidgets.JCommLabel();

        question5_performance = new pact.CommWidgets.JCommMultipleChoice();
        question6_performance = new pact.CommWidgets.JCommMultipleChoice();
        question7_performance = new pact.CommWidgets.JCommMultipleChoice();
        question8_performance = new pact.CommWidgets.JCommMultipleChoice();
        
        strategy9 = new pact.CommWidgets.JCommLabel();
        strategy10 = new pact.CommWidgets.JCommLabel();
        strategy11 = new pact.CommWidgets.JCommLabel();
        strategy12 = new pact.CommWidgets.JCommLabel();

        question9_strategy = new pact.CommWidgets.JCommMultipleChoice();
        question10_strategy = new pact.CommWidgets.JCommMultipleChoice();
        question11_strategy = new pact.CommWidgets.JCommMultipleChoice();
        question12_strategy = new pact.CommWidgets.JCommMultipleChoice();
        
        affect13 = new pact.CommWidgets.JCommLabel();
        affect14 = new pact.CommWidgets.JCommLabel();
        affect15 = new pact.CommWidgets.JCommLabel();
        affect16 = new pact.CommWidgets.JCommLabel();

        question13_affect = new pact.CommWidgets.JCommMultipleChoice();
        question14_affect = new pact.CommWidgets.JCommMultipleChoice();
        question15_affect = new pact.CommWidgets.JCommMultipleChoice();
        question16_affect = new pact.CommWidgets.JCommMultipleChoice();
        
        condition17 = new pact.CommWidgets.JCommLabel();
        condition18 = new pact.CommWidgets.JCommLabel();
        condition19 = new pact.CommWidgets.JCommLabel();
        condition20 = new pact.CommWidgets.JCommLabel();

        question17_condition = new pact.CommWidgets.JCommMultipleChoice();
        question18_condition = new pact.CommWidgets.JCommMultipleChoice();
        question19_condition = new pact.CommWidgets.JCommMultipleChoice();
        question20_condition = new pact.CommWidgets.JCommMultipleChoice();

        explain21 = new pact.CommWidgets.JCommLabel();
        
        question21_explain = new pact.CommWidgets.JCommTextArea();
        
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
        

        
        rating1.setFont(new java.awt.Font("SansSerif", 1, 12));
        rating1.setText("Not at all true");
        jPanel1.add(rating1);
        rating1.setBounds(650, 150, 150, 30);

        rating7.setFont(new java.awt.Font("SansSerif", 1, 12));
        rating7.setText("Very true");
        jPanel1.add(rating7);
        rating7.setBounds(900, 150, 150, 30);
                        
        mastery1.setFont(new java.awt.Font("SansSerif", 1, 12));
        mastery1.setText("1. %(q1_mastery)%");
        jPanel1.add(mastery1);
        mastery1.setBounds(20, 180, 625, 30);
        
        mastery2.setFont(new java.awt.Font("SansSerif", 1, 12));
        mastery2.setText("2. %(q2_mastery)%");
        jPanel1.add(mastery2);
        mastery2.setBounds(20, 210, 625, 30);
        
        mastery3.setFont(new java.awt.Font("SansSerif", 1, 12));
        mastery3.setText("3. %(q3_mastery)%");
        jPanel1.add(mastery3);
        mastery3.setBounds(20, 240, 625, 30);
        
        mastery4.setFont(new java.awt.Font("SansSerif", 1, 12));
        mastery4.setText("4. %(q4_mastery)%");
        jPanel1.add(mastery4);
        mastery4.setBounds(20, 270, 625, 30);
        
        question1_mastery.setBackground(new java.awt.Color(255, 255, 255));
        question1_mastery.setNChoices(7);
        question1_mastery.setChoiceLayout(1);
        question1_mastery.setChoiceTexts("1,2,3,4,5,6,7");
        question1_mastery.setCorrectColor(new java.awt.Color(0, 0, 0));
        question1_mastery.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question1_mastery.setQuestionText("");
        jPanel1.add(question1_mastery);
        question1_mastery.setBounds(650, 180, 330, 30);
        
        question2_mastery.setBackground(new java.awt.Color(255, 255, 255));
        question2_mastery.setNChoices(7);
        question2_mastery.setChoiceLayout(1);
        question2_mastery.setChoiceTexts("1,2,3,4,5,6,7");
        question2_mastery.setCorrectColor(new java.awt.Color(0, 0, 0));
        question2_mastery.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question2_mastery.setQuestionText("");
        jPanel1.add(question2_mastery);
        question2_mastery.setBounds(650, 210, 330, 30);

        question3_mastery.setBackground(new java.awt.Color(255, 255, 255));
        question3_mastery.setNChoices(7);
        question3_mastery.setChoiceLayout(1);
        question3_mastery.setChoiceTexts("1,2,3,4,5,6,7");
        question3_mastery.setCorrectColor(new java.awt.Color(0, 0, 0));
        question3_mastery.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question3_mastery.setQuestionText("");
        jPanel1.add(question3_mastery);
        question3_mastery.setBounds(650, 240, 330, 30);
        
        question4_mastery.setBackground(new java.awt.Color(255, 255, 255));
        question4_mastery.setNChoices(7);
        question4_mastery.setChoiceLayout(1);
        question4_mastery.setChoiceTexts("1,2,3,4,5,6,7");
        question4_mastery.setCorrectColor(new java.awt.Color(0, 0, 0));
        question4_mastery.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question4_mastery.setQuestionText("");
        jPanel1.add(question4_mastery);
        question4_mastery.setBounds(650, 270, 330, 30);
        
        
        performance5.setFont(new java.awt.Font("SansSerif", 1, 12));
        performance5.setText("5. %(q5_performance)%");
        jPanel1.add(performance5);
        performance5.setBounds(20, 330, 625, 30);
        
        performance6.setFont(new java.awt.Font("SansSerif", 1, 12));
        performance6.setText("6. %(q6_performance)%");
        jPanel1.add(performance6);
        performance6.setBounds(20, 360, 625, 30);
        
        performance7.setFont(new java.awt.Font("SansSerif", 1, 12));
        performance7.setText("7. %(q7_performance)%");
        jPanel1.add(performance7);
        performance7.setBounds(20, 390, 625, 30);
        
        performance8.setFont(new java.awt.Font("SansSerif", 1, 12));
        performance8.setText("8. %(q8_performance)%");
        jPanel1.add(performance8);
        performance8.setBounds(20, 420, 625, 30);

        question5_performance.setBackground(new java.awt.Color(255, 255, 255));
        question5_performance.setNChoices(7);
        question5_performance.setChoiceLayout(1);
        question5_performance.setChoiceTexts("1,2,3,4,5,6,7");
        question5_performance.setCorrectColor(new java.awt.Color(0, 0, 0));
        question5_performance.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question5_performance.setQuestionText("");
        jPanel1.add(question5_performance);
        question5_performance.setBounds(650, 330, 330, 30);
        

        question6_performance.setBackground(new java.awt.Color(255, 255, 255));
        question6_performance.setNChoices(7);
        question6_performance.setChoiceLayout(1);
        question6_performance.setChoiceTexts("1,2,3,4,5,6,7");
        question6_performance.setCorrectColor(new java.awt.Color(0, 0, 0));
        question6_performance.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question6_performance.setQuestionText("");
        jPanel1.add(question6_performance);
        question6_performance.setBounds(650, 360, 330, 30);

        question7_performance.setBackground(new java.awt.Color(255, 255, 255));
        question7_performance.setNChoices(7);
        question7_performance.setChoiceLayout(1);
        question7_performance.setChoiceTexts("1,2,3,4,5,6,7");
        question7_performance.setCorrectColor(new java.awt.Color(0, 0, 0));
        question7_performance.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question7_performance.setQuestionText("");
        jPanel1.add(question7_performance);
        question7_performance.setBounds(650, 390, 330, 30);

        question8_performance.setBackground(new java.awt.Color(255, 255, 255));
        question8_performance.setNChoices(7);
        question8_performance.setChoiceLayout(1);
        question8_performance.setChoiceTexts("1,2,3,4,5,6,7");
        question8_performance.setCorrectColor(new java.awt.Color(0, 0, 0));
        question8_performance.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question8_performance.setQuestionText("");
        jPanel1.add(question8_performance);
        question8_performance.setBounds(650, 420, 330, 30);


        strategy9.setFont(new java.awt.Font("SansSerif", 1, 12));
        strategy9.setText("9. %(q9_strategy)%");
        jPanel1.add(strategy9);
        strategy9.setBounds(20, 480, 625, 30);
        
        strategy10.setFont(new java.awt.Font("SansSerif", 1, 12));
        strategy10.setText("10. %(q10_strategy)%");
        jPanel1.add(strategy10);
        strategy10.setBounds(20, 510, 625, 30);
        
        strategy11.setFont(new java.awt.Font("SansSerif", 1, 12));
        strategy11.setText("11. %(q11_strategy)%");
        jPanel1.add(strategy11);
        strategy11.setBounds(20, 540, 625, 30);
        
        strategy12.setFont(new java.awt.Font("SansSerif", 1, 12));
        strategy12.setText("12. %(q12_strategy)%");
        jPanel1.add(strategy12);
        strategy12.setBounds(20, 570, 625, 30);

        
        question9_strategy.setBackground(new java.awt.Color(255, 255, 255));
        question9_strategy.setNChoices(7);
        question9_strategy.setChoiceLayout(1);
        question9_strategy.setChoiceTexts("1,2,3,4,5,6,7");
        question9_strategy.setCorrectColor(new java.awt.Color(0, 0, 0));
        question9_strategy.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question9_strategy.setQuestionText("");
        jPanel1.add(question9_strategy);
        question9_strategy.setBounds(650, 480, 330, 30);

        question10_strategy.setBackground(new java.awt.Color(255, 255, 255));
        question10_strategy.setNChoices(7);
        question10_strategy.setChoiceLayout(1);
        question10_strategy.setChoiceTexts("1,2,3,4,5,6,7");
        question10_strategy.setCorrectColor(new java.awt.Color(0, 0, 0));
        question10_strategy.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question10_strategy.setQuestionText("");
        jPanel1.add(question10_strategy);
        question10_strategy.setBounds(650, 510, 330, 30);

        question11_strategy.setBackground(new java.awt.Color(255, 255, 255));
        question11_strategy.setNChoices(7);
        question11_strategy.setChoiceLayout(1);
        question11_strategy.setChoiceTexts("1,2,3,4,5,6,7");
        question11_strategy.setCorrectColor(new java.awt.Color(0, 0, 0));
        question11_strategy.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question11_strategy.setQuestionText("");
        jPanel1.add(question11_strategy);
        question11_strategy.setBounds(650, 540, 330, 30);

        question12_strategy.setBackground(new java.awt.Color(255, 255, 255));
        question12_strategy.setNChoices(7);
        question12_strategy.setChoiceLayout(1);
        question12_strategy.setChoiceTexts("1,2,3,4,5,6,7");
        question12_strategy.setCorrectColor(new java.awt.Color(0, 0, 0));
        question12_strategy.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question12_strategy.setQuestionText("");
        jPanel1.add(question12_strategy);
        question12_strategy.setBounds(650, 570, 330, 30);


        affect13.setFont(new java.awt.Font("SansSerif", 1, 12));
        affect13.setText("13. %(q13_affect)%");
        jPanel1.add(affect13);
        affect13.setBounds(20, 630, 625, 30);
        
        affect14.setFont(new java.awt.Font("SansSerif", 1, 12));
        affect14.setText("14. %(q14_affect)%");
        jPanel1.add(affect14);
        affect14.setBounds(20, 660, 625, 30);
        
        affect15.setFont(new java.awt.Font("SansSerif", 1, 12));
        affect15.setText("15. %(q15_affect)%");
        jPanel1.add(affect15);
        affect15.setBounds(20, 690, 625, 30);
        
        affect16.setFont(new java.awt.Font("SansSerif", 1, 12));
        affect16.setText("16. %(q16_affect)%");
        jPanel1.add(affect16);
        affect16.setBounds(20, 720, 625, 30);

        
        question13_affect.setBackground(new java.awt.Color(255, 255, 255));
        question13_affect.setNChoices(7);
        question13_affect.setChoiceLayout(1);
        question13_affect.setChoiceTexts("1,2,3,4,5,6,7");
        question13_affect.setCorrectColor(new java.awt.Color(0, 0, 0));
        question13_affect.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question13_affect.setQuestionText("");
        jPanel1.add(question13_affect);
        question13_affect.setBounds(650, 630, 330, 30);

        question14_affect.setBackground(new java.awt.Color(255, 255, 255));
        question14_affect.setNChoices(7);
        question14_affect.setChoiceLayout(1);
        question14_affect.setChoiceTexts("1,2,3,4,5,6,7");
        question14_affect.setCorrectColor(new java.awt.Color(0, 0, 0));
        question14_affect.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question14_affect.setQuestionText("");
        jPanel1.add(question14_affect);
        question14_affect.setBounds(650, 660, 330, 30);

        question15_affect.setBackground(new java.awt.Color(255, 255, 255));
        question15_affect.setNChoices(7);
        question15_affect.setChoiceLayout(1);
        question15_affect.setChoiceTexts("1,2,3,4,5,6,7");
        question15_affect.setCorrectColor(new java.awt.Color(0, 0, 0));
        question15_affect.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question15_affect.setQuestionText("");
        jPanel1.add(question15_affect);
        question15_affect.setBounds(650, 690, 330, 30);

        question16_affect.setBackground(new java.awt.Color(255, 255, 255));
        question16_affect.setNChoices(7);
        question16_affect.setChoiceLayout(1);
        question16_affect.setChoiceTexts("1,2,3,4,5,6,7");
        question16_affect.setCorrectColor(new java.awt.Color(0, 0, 0));
        question16_affect.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question16_affect.setQuestionText("");
        jPanel1.add(question16_affect);
        question16_affect.setBounds(650, 720, 330, 30);


        condition17.setFont(new java.awt.Font("SansSerif", 1, 12));
        condition17.setText("17. %(q17_condition)%");
        jPanel1.add(condition17);
        condition17.setBounds(20, 780, 625, 30);
        
        condition18.setFont(new java.awt.Font("SansSerif", 1, 12));
        condition18.setText("18. %(q18_condition)%");
        jPanel1.add(condition18);
        condition18.setBounds(20, 810, 625, 30);
        
        condition19.setFont(new java.awt.Font("SansSerif", 1, 12));
        condition19.setText("19. %(q19_condition)%");
        jPanel1.add(condition19);
        condition19.setBounds(20, 840, 625, 30);
        
        condition20.setFont(new java.awt.Font("SansSerif", 1, 12));
        condition20.setText("20. %(q20_condition)%");
        jPanel1.add(condition20);
        condition20.setBounds(20, 870, 625, 30);

        
        question17_condition.setBackground(new java.awt.Color(255, 255, 255));
        question17_condition.setNChoices(7);
        question17_condition.setChoiceLayout(1);
        question17_condition.setChoiceTexts("1,2,3,4,5,6,7");
        question17_condition.setCorrectColor(new java.awt.Color(0, 0, 0));
        question17_condition.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question17_condition.setQuestionText("");
        jPanel1.add(question17_condition);
        question17_condition.setBounds(650, 780, 330, 30);

        question18_condition.setBackground(new java.awt.Color(255, 255, 255));
        question18_condition.setNChoices(7);
        question18_condition.setChoiceLayout(1);
        question18_condition.setChoiceTexts("1,2,3,4,5,6,7");
        question18_condition.setCorrectColor(new java.awt.Color(0, 0, 0));
        question18_condition.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question18_condition.setQuestionText("");
        jPanel1.add(question18_condition);
        question18_condition.setBounds(650, 810, 330, 30);

        question19_condition.setBackground(new java.awt.Color(255, 255, 255));
        question19_condition.setNChoices(7);
        question19_condition.setChoiceLayout(1);
        question19_condition.setChoiceTexts("1,2,3,4,5,6,7");
        question19_condition.setCorrectColor(new java.awt.Color(0, 0, 0));
        question19_condition.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question19_condition.setQuestionText("");
        jPanel1.add(question19_condition);
        question19_condition.setBounds(650, 840, 330, 30);

        question20_condition.setBackground(new java.awt.Color(255, 255, 255));
        question20_condition.setNChoices(7);
        question20_condition.setChoiceLayout(1);
        question20_condition.setChoiceTexts("1,2,3,4,5,6,7");
        question20_condition.setCorrectColor(new java.awt.Color(0, 0, 0));
        question20_condition.setIncorrectColor(new java.awt.Color(0, 0, 0));
        question20_condition.setQuestionText("");
        jPanel1.add(question20_condition);
        question20_condition.setBounds(650, 870, 330, 30);

        explain21.setFont(new java.awt.Font("SansSerif", 1, 12));
        explain21.setText("21. %(q21_explain)%");
        jPanel1.add(explain21);
        explain21.setBounds(20, 930, 650, 30);

        question21_explain.setCorrectColor(new java.awt.Color(0, 0, 0));
        question21_explain.setIncorrectColor(new java.awt.Color(0, 0, 0));
      //  jPanel1.add(question21_explain);
        question21_explain.setBounds(30, 960, 650, 90);
        
        
        instructions.setFont(new java.awt.Font("SansSerif", 1, 12));
        instructions.setForeground(new java.awt.Color(255, 0, 0));
        instructions.setText("<HTML>These are questions about yourself while you were tutoring your student, and NOT your participation in math class in general. Using the scale shown below, please indicate the extent to which you agree or disagree with each of the following statements by clicking the number that corresponds to your opinion. There are no right or wrong answers.");
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
        Done.setBounds(230, 1070, 250, 30);
        
        promptSuccess.setFont(new java.awt.Font("SansSerif", 1, 14));
        promptSuccess.setForeground(new java.awt.Color(0, 153, 0));
        jPanel1.add(promptSuccess);
        promptSuccess.setBounds(160, 1100, 520, 80);
               

        jPanel1.add(horizontalLine1);
        horizontalLine1.setBounds(30, 1200, 570, 10);
        
        
        add(jScrollPane1);
        jScrollPane1.setBounds(10, 70, 1000, 500);
        jScrollPane1.getAccessibleContext().setAccessibleName("");
        
        jScrollPane1.getVerticalScrollBar().setSize(15,15);
        
        question1_mastery.setBackground(Color.WHITE);
        question2_mastery.setBackground(Color.WHITE);
        question3_mastery.setBackground(Color.WHITE);
        question4_mastery.setBackground(Color.WHITE);
        question5_performance.setBackground(Color.WHITE);
        question6_performance.setBackground(Color.WHITE);
        question7_performance.setBackground(Color.WHITE);
        question8_performance.setBackground(Color.WHITE);
        question9_strategy.setBackground(Color.WHITE);
        question10_strategy.setBackground(Color.WHITE);
        question11_strategy.setBackground(Color.WHITE);
        question12_strategy.setBackground(Color.WHITE);
        question13_affect.setBackground(Color.WHITE);
        question14_affect.setBackground(Color.WHITE);
        question15_affect.setBackground(Color.WHITE);
        question16_affect.setBackground(Color.WHITE);
        question17_condition.setBackground(Color.WHITE);
        question18_condition.setBackground(Color.WHITE);
        question19_condition.setBackground(Color.WHITE);
        question20_condition.setBackground(Color.WHITE);
        
	}
	

    public static void main(String[] argv) {

    	CTAT_Launcher launch = new CTAT_Launcher(argv);
    //	launch.launch (new QuestionnaireMT());
    	
    	QuestionnaireMT questionaire = new QuestionnaireMT(launch.getController());
 
    	launch.launch (questionaire); 
    	
    	
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

         promptSuccess.setText("Congratulations! You've completed the Questionnaire!");

         
         DlgTest dlg=new DlgTest(this.brController);
         //grading.setText(gradeNow());
         //writeAnswerFile();
         
         Done.setEnabled(false);
     	 brController.closeApplication(true);
 
    }//GEN-LAST:event_promptNow
    
    public java.awt.Dimension getPreferredSize()

    {

        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();

        return new java.awt.Dimension(1012, (int)((tk.getScreenSize().height)*0.8));

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
    protected pact.CommWidgets.JCommLabel mastery1;
    protected pact.CommWidgets.JCommLabel mastery2;
    protected pact.CommWidgets.JCommLabel mastery3;
    protected pact.CommWidgets.JCommLabel mastery4;
    
    protected pact.CommWidgets.JCommMultipleChoice question1_mastery;
    protected pact.CommWidgets.JCommMultipleChoice question2_mastery;
    protected pact.CommWidgets.JCommMultipleChoice question3_mastery;
    protected pact.CommWidgets.JCommMultipleChoice question4_mastery;
    
    protected pact.CommWidgets.JCommLabel performance5;
    protected pact.CommWidgets.JCommLabel performance6;
    protected pact.CommWidgets.JCommLabel performance7;
    protected pact.CommWidgets.JCommLabel performance8;

    protected pact.CommWidgets.JCommMultipleChoice question5_performance;
    protected pact.CommWidgets.JCommMultipleChoice question6_performance;
    protected pact.CommWidgets.JCommMultipleChoice question7_performance;
    protected pact.CommWidgets.JCommMultipleChoice question8_performance;

    protected pact.CommWidgets.JCommLabel strategy9;
    protected pact.CommWidgets.JCommLabel strategy10;
    protected pact.CommWidgets.JCommLabel strategy11;
    protected pact.CommWidgets.JCommLabel strategy12;

    protected pact.CommWidgets.JCommMultipleChoice question9_strategy;
    protected pact.CommWidgets.JCommMultipleChoice question10_strategy;
    protected pact.CommWidgets.JCommMultipleChoice question11_strategy;
    protected pact.CommWidgets.JCommMultipleChoice question12_strategy;
    
    protected pact.CommWidgets.JCommLabel affect13;    
    protected pact.CommWidgets.JCommLabel affect14;    
    protected pact.CommWidgets.JCommLabel affect15;    
    protected pact.CommWidgets.JCommLabel affect16;

    protected pact.CommWidgets.JCommMultipleChoice question13_affect;
    protected pact.CommWidgets.JCommMultipleChoice question14_affect;
    protected pact.CommWidgets.JCommMultipleChoice question15_affect;
    protected pact.CommWidgets.JCommMultipleChoice question16_affect;
    
    protected pact.CommWidgets.JCommLabel condition17;
    protected pact.CommWidgets.JCommLabel condition18;
    protected pact.CommWidgets.JCommLabel condition19;
    protected pact.CommWidgets.JCommLabel condition20;

    protected pact.CommWidgets.JCommMultipleChoice question17_condition;
    protected pact.CommWidgets.JCommMultipleChoice question18_condition;
    protected pact.CommWidgets.JCommMultipleChoice question19_condition;
    protected pact.CommWidgets.JCommMultipleChoice question20_condition;
    
    protected pact.CommWidgets.JCommLabel explain21;
    
    protected pact.CommWidgets.JCommTextArea question21_explain;
    
    protected javax.swing.JPanel jPanel1;

    protected javax.swing.JScrollPane jScrollPane1;

    protected javax.swing.JLabel promptSuccess;
    protected pact.CommWidgets.JCommLabel test;

    protected pact.CommWidgets.HorizontalLine horizontalLine1;
    
    protected javax.swing.JLabel rating1;
    protected javax.swing.JLabel rating7;
}
