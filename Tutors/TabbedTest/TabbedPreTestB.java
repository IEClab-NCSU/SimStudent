/*
 * TutorTemplate.java
 *
 * Created on October 18, 2005, 5:33 PM
 */

//package TabbedPreTest;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import javax.swing.JTextArea;
import java.awt.Color;

/**
 * A template for Java-language student interfaces. For use with TutorTemplate.form and NetBeans v6.x.
 * @author  mpschnei
 */
public class TabbedPreTestB extends javax.swing.JPanel{
    
    /** Creates new form TutorTemplate */

public TabbedPreTestB() {
        initComponents();
        ((JTextArea)demonstration_problem1_box.getTextArea()).setLineWrap(true);
        ((JTextArea)demonstration_problem2_box.getTextArea()).setLineWrap(true);
        ((JTextArea)demonstration_problem3_box.getTextArea()).setLineWrap(true);
        ((JTextArea)demonstration_problem4_box.getTextArea()).setLineWrap(true);
        ((JTextArea)demonstration_problem5_box.getTextArea()).setLineWrap(true);

         demonstration_problem1.setBackground(Color.WHITE);
         demonstration_problem2.setBackground(Color.WHITE);
         demonstration_problem3.setBackground(Color.WHITE);
         demonstration_problem4.setBackground(Color.WHITE);
         demonstration_problem5.setBackground(Color.WHITE);
         LT_problem1_option1.setBackground(Color.WHITE);
         LT_problem1_option2.setBackground(Color.WHITE);
         LT_problem1_option3.setBackground(Color.WHITE);
         LT_problem1_option4.setBackground(Color.WHITE);
         LT_problem1_option5.setBackground(Color.WHITE);
         LT_problem1_option6.setBackground(Color.WHITE);
         LT_problem1_option7.setBackground(Color.WHITE);
         LT_problem2_option1.setBackground(Color.WHITE);
         LT_problem2_option2.setBackground(Color.WHITE);
         LT_problem2_option3.setBackground(Color.WHITE);
         LT_problem2_option4.setBackground(Color.WHITE);
         LT_problem2_option5.setBackground(Color.WHITE);
         LT_problem2_option6.setBackground(Color.WHITE);
         LT_problem2_option7.setBackground(Color.WHITE);
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
         LT_problem5_option1.setBackground(Color.WHITE);
         LT_problem5_option2.setBackground(Color.WHITE);
         LT_problem5_option3.setBackground(Color.WHITE);
         LT_problem5_option4.setBackground(Color.WHITE);
         LT_problem5_option5.setBackground(Color.WHITE);
         LT_problem5_option6.setBackground(Color.WHITE);
         LT_problem6_option1.setBackground(Color.WHITE);
         LT_problem6_option2.setBackground(Color.WHITE);
         LT_problem6_option3.setBackground(Color.WHITE);
         LT_problem6_option4.setBackground(Color.WHITE);
         LT_problem6_option5.setBackground(Color.WHITE);
         LT_problem6_option6.setBackground(Color.WHITE);
         effective_problem1_option1.setBackground(Color.WHITE);
         effective_problem1_option2.setBackground(Color.WHITE);
         effective_problem1_option3.setBackground(Color.WHITE);
         effective_problem1_option4.setBackground(Color.WHITE);
         effective_problem2_option1.setBackground(Color.WHITE);
         effective_problem2_option2.setBackground(Color.WHITE);
         effective_problem2_option3.setBackground(Color.WHITE);
         effective_problem2_option4.setBackground(Color.WHITE);
         effective_problem3_option1.setBackground(Color.WHITE);
         effective_problem3_option2.setBackground(Color.WHITE);
         effective_problem3_option3.setBackground(Color.WHITE);
         effective_problem3_option4.setBackground(Color.WHITE);
         eqexp_problem1_option1.setBackground(Color.WHITE);
         eqexp_problem1_option2.setBackground(Color.WHITE);
         eqexp_problem1_option3.setBackground(Color.WHITE);
         eqexp_problem1_option4.setBackground(Color.WHITE);
         eqexp_problem1_option5.setBackground(Color.WHITE);
         eqexp_problem2_option1.setBackground(Color.WHITE);
         eqexp_problem2_option2.setBackground(Color.WHITE);
         eqexp_problem2_option3.setBackground(Color.WHITE);
         eqexp_problem2_option4.setBackground(Color.WHITE);
         eqexp_problem2_option5.setBackground(Color.WHITE);


    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cTAT_Options1 = new edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options();
        cTAT_Options2 = new edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options();
        dorminLabel7 = new pact.DorminWidgets.DorminLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        eq_problem1_box = new pact.DorminWidgets.DorminTextField();
        eq_problem2_box = new pact.DorminWidgets.DorminTextField();
        eq_problem3_box = new pact.DorminWidgets.DorminTextField();
        eq_problem4_box = new pact.DorminWidgets.DorminTextField();
        eq_problem5_box = new pact.DorminWidgets.DorminTextField();
        eq_problem6_box = new pact.DorminWidgets.DorminTextField();
        horizontalLine6 = new pact.DorminWidgets.HorizontalLine();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dorminLabel8 = new pact.DorminWidgets.DorminLabel();
        dorminLabel19 = new pact.DorminWidgets.DorminLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        dorminLabel58 = new pact.DorminWidgets.DorminLabel();
        dorminLabel81 = new pact.DorminWidgets.DorminLabel();
        dorminLabel100 = new pact.DorminWidgets.DorminLabel();
        jLabel4 = new javax.swing.JLabel();
        horizontalLine8 = new pact.DorminWidgets.HorizontalLine();
        jLabel6 = new javax.swing.JLabel();
        LT_problem2_option1 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem1_option2 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem1_option3 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem1_option4 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem1_option5 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem1_option6 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem1_option7 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem1_option1 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem2_option2 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem2_option3 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem2_option4 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem2_option5 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem2_option6 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem2_option7 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem3_option1 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem3_option2 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem3_option3 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem3_option4 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem3_option5 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem3_option6 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem4_option1 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem4_option2 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem4_option3 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem4_option4 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem4_option5 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem4_option6 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem5_option1 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem5_option2 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem5_option3 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem5_option4 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem5_option5 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem6_option1 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem6_option2 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem6_option3 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem6_option4 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem6_option5 = new pact.DorminWidgets.DorminMultipleChoice();
        LT_problem6_option6 = new pact.DorminWidgets.DorminMultipleChoice();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        dorminLabel9 = new pact.DorminWidgets.DorminLabel();
        LT_problem5_option6 = new pact.DorminWidgets.DorminMultipleChoice();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        dorminLabel133 = new pact.DorminWidgets.DorminLabel();
        jLabel5 = new javax.swing.JLabel();
        horizontalLine9 = new pact.DorminWidgets.HorizontalLine();
        jLabel10 = new javax.swing.JLabel();
        effective_problem1_option1 = new pact.DorminWidgets.DorminMultipleChoice();
        effective_problem1_option2 = new pact.DorminWidgets.DorminMultipleChoice();
        effective_problem1_option3 = new pact.DorminWidgets.DorminMultipleChoice();
        effective_problem1_option4 = new pact.DorminWidgets.DorminMultipleChoice();
        effective_problem2_option1 = new pact.DorminWidgets.DorminMultipleChoice();
        effective_problem2_option2 = new pact.DorminWidgets.DorminMultipleChoice();
        effective_problem2_option3 = new pact.DorminWidgets.DorminMultipleChoice();
        effective_problem2_option4 = new pact.DorminWidgets.DorminMultipleChoice();
        effective_problem3_option1 = new pact.DorminWidgets.DorminMultipleChoice();
        effective_problem3_option2 = new pact.DorminWidgets.DorminMultipleChoice();
        effective_problem3_option3 = new pact.DorminWidgets.DorminMultipleChoice();
        effective_problem3_option4 = new pact.DorminWidgets.DorminMultipleChoice();
        eqexp_problem1_option1 = new pact.DorminWidgets.DorminMultipleChoice();
        eqexp_problem1_option2 = new pact.DorminWidgets.DorminMultipleChoice();
        eqexp_problem1_option3 = new pact.DorminWidgets.DorminMultipleChoice();
        eqexp_problem1_option4 = new pact.DorminWidgets.DorminMultipleChoice();
        eqexp_problem1_option5 = new pact.DorminWidgets.DorminMultipleChoice();
        eqexp_problem2_option1 = new pact.DorminWidgets.DorminMultipleChoice();
        eqexp_problem2_option2 = new pact.DorminWidgets.DorminMultipleChoice();
        eqexp_problem2_option3 = new pact.DorminWidgets.DorminMultipleChoice();
        eqexp_problem2_option4 = new pact.DorminWidgets.DorminMultipleChoice();
        eqexp_problem2_option5 = new pact.DorminWidgets.DorminMultipleChoice();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        dorminLabel17 = new pact.DorminWidgets.DorminLabel();
        dorminLabel30 = new pact.DorminWidgets.DorminLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        dorminLabel111 = new pact.DorminWidgets.DorminLabel();
        horizontalLine88 = new pact.DorminWidgets.HorizontalLine();
        dorminLabel117 = new pact.DorminWidgets.DorminLabel();
        demonstration_problem1_box = new pact.DorminWidgets.DorminTextArea();
        dorminLabel141 = new pact.DorminWidgets.DorminLabel();
        horizontalLine125 = new pact.DorminWidgets.HorizontalLine();
        demonstration_problem2_box = new pact.DorminWidgets.DorminTextArea();
        dorminLabel144 = new pact.DorminWidgets.DorminLabel();
        horizontalLine126 = new pact.DorminWidgets.HorizontalLine();
        demonstration_problem3_box = new pact.DorminWidgets.DorminTextArea();
        dorminLabel147 = new pact.DorminWidgets.DorminLabel();
        horizontalLine127 = new pact.DorminWidgets.HorizontalLine();
        demonstration_problem4_box = new pact.DorminWidgets.DorminTextArea();
        dorminLabel150 = new pact.DorminWidgets.DorminLabel();
        horizontalLine128 = new pact.DorminWidgets.HorizontalLine();
        demonstration_problem5_box = new pact.DorminWidgets.DorminTextArea();
        jLabel1 = new javax.swing.JLabel();
        horizontalLine10 = new pact.DorminWidgets.HorizontalLine();
        jLabel11 = new javax.swing.JLabel();
        demonstration_problem1_step1 = new pact.DorminWidgets.DorminLabel();
        demonstration_problem2_step1 = new pact.DorminWidgets.DorminLabel();
        demonstration_problem3_step1 = new pact.DorminWidgets.DorminLabel();
        demonstration_problem4_step1 = new pact.DorminWidgets.DorminLabel();
        demonstration_problem5_step1 = new pact.DorminWidgets.DorminLabel();
        jLabel17 = new javax.swing.JLabel();
        demonstration_problem1 = new pact.DorminWidgets.DorminTextField();
        dorminLabel142 = new pact.DorminWidgets.DorminLabel();
        jLabel18 = new javax.swing.JLabel();
        demonstration_problem2 = new pact.DorminWidgets.DorminTextField();
        dorminLabel143 = new pact.DorminWidgets.DorminLabel();
        jLabel19 = new javax.swing.JLabel();
        demonstration_problem3 = new pact.DorminWidgets.DorminTextField();
        dorminLabel145 = new pact.DorminWidgets.DorminLabel();
        jLabel20 = new javax.swing.JLabel();
        demonstration_problem4 = new pact.DorminWidgets.DorminTextField();
        dorminLabel146 = new pact.DorminWidgets.DorminLabel();
        jLabel21 = new javax.swing.JLabel();
        demonstration_problem5 = new pact.DorminWidgets.DorminTextField();
        dorminLabel18 = new pact.DorminWidgets.DorminLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        Done = new pact.DorminWidgets.DorminButton();
        jLabel9 = new javax.swing.JLabel();
        promptSuccess = new javax.swing.JLabel();

        cTAT_Options1.setSeparateHintWindow(true);

        cTAT_Options2.setSeparateHintWindow(true);

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(200, 200));
        setMinimumSize(new java.awt.Dimension(200, 200));
        setPreferredSize(new java.awt.Dimension(200, 200));
        setLayout(null);

        dorminLabel7.setFont(new java.awt.Font("Tahoma", 0, 18));
        dorminLabel7.setText("<HTML><b>Pretest</b> (Version %(test_version)%)");
        add(dorminLabel7);
        dorminLabel7.setBounds(20, 10, 420, 50);

        jTabbedPane1.setBackground(new java.awt.Color(204, 204, 255));
        jTabbedPane1.setForeground(new java.awt.Color(0, 51, 204));
        jTabbedPane1.setAutoscrolls(true);
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(630, 640));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel1.setLayout(null);

        eq_problem1_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem1_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem1_box.setShowBorder(false);
        jPanel1.add(eq_problem1_box);
        eq_problem1_box.setBounds(510, 120, 90, 30);

        eq_problem2_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem2_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem2_box.setShowBorder(false);
        jPanel1.add(eq_problem2_box);
        eq_problem2_box.setBounds(510, 180, 90, 30);

        eq_problem3_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem3_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem3_box.setShowBorder(false);
        jPanel1.add(eq_problem3_box);
        eq_problem3_box.setBounds(510, 240, 90, 30);

        eq_problem4_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem4_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem4_box.setShowBorder(false);
        jPanel1.add(eq_problem4_box);
        eq_problem4_box.setBounds(510, 300, 90, 30);

        eq_problem5_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem5_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem5_box.setShowBorder(false);
        jPanel1.add(eq_problem5_box);
        eq_problem5_box.setBounds(510, 360, 90, 30);

        eq_problem6_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem6_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eq_problem6_box.setShowBorder(false);
        jPanel1.add(eq_problem6_box);
        eq_problem6_box.setBounds(510, 420, 90, 30);
        jPanel1.add(horizontalLine6);
        horizontalLine6.setBounds(30, 550, 570, 10);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 0, 0));
        jLabel3.setText("<HTML>Please proceed to the next page by clicking the tab");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(30, 580, 320, 80);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("<HTML><b> Solve the following equations, show your work on the test form and enter answers in corresponding boxes below.</b>");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(60, 10, 490, 60);

        dorminLabel8.setImageName("go_page2.png");
        dorminLabel8.setText("");
        jPanel1.add(dorminLabel8);
        dorminLabel8.setBounds(360, 600, 80, 40);

	dorminLabel19.setImageName("B_eq_problem0.png");
        dorminLabel19.setText("");
        jPanel1.add(dorminLabel19);
        dorminLabel19.setBounds(30, 90, 480, 390);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 0, 0));
        jLabel7.setText("<HTML>at the top.");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(440, 580, 110, 80);

        jScrollPane1.setViewportView(jPanel1);

        jTabbedPane1.addTab("Page 1", jScrollPane1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(0, 3500));
        jPanel2.setLayout(null);

        dorminLabel58.setFont(new java.awt.Font("Tahoma", 0, 12));
	dorminLabel58.setImageName("B_LT_problem1.png");
        dorminLabel58.setText("");
        jPanel2.add(dorminLabel58);
        dorminLabel58.setBounds(40, 80, 390, 550);

        dorminLabel81.setFont(new java.awt.Font("Tahoma", 0, 12));
	dorminLabel81.setImageName("B_LT_problem3.png");
        dorminLabel81.setText("");
        jPanel2.add(dorminLabel81);
        dorminLabel81.setBounds(40, 670, 380, 490);

	dorminLabel100.setImageName("B_LT_problem5.png");
        dorminLabel100.setText("");
        jPanel2.add(dorminLabel100);
        dorminLabel100.setBounds(50, 1240, 310, 500);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 0, 0));
        jLabel4.setText("<HTML>at the top.");
        jPanel2.add(jLabel4);
        jLabel4.setBounds(440, 1800, 80, 60);
        jPanel2.add(horizontalLine8);
        horizontalLine8.setBounds(110, 1860, 320, 10);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 0, 0));
        jLabel6.setText("<HTML><B>Please be sure to work through all items. </B>");
        jPanel2.add(jLabel6);
        jLabel6.setBounds(10, 10, 600, 30);

        LT_problem2_option1.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem2_option1.setNChoices(2);
        LT_problem2_option1.setChoiceLayout(1);
        LT_problem2_option1.setChoiceTexts("True,False");
        LT_problem2_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option1.setQuestionText("");
        jPanel2.add(LT_problem2_option1);
        LT_problem2_option1.setBounds(430, 390, 130, 30);

        LT_problem1_option2.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem1_option2.setNChoices(2);
        LT_problem1_option2.setChoiceLayout(1);
        LT_problem1_option2.setChoiceTexts("True,False");
        LT_problem1_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option2.setQuestionText("");
        jPanel2.add(LT_problem1_option2);
        LT_problem1_option2.setBounds(430, 150, 130, 30);

        LT_problem1_option3.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem1_option3.setNChoices(2);
        LT_problem1_option3.setChoiceLayout(1);
        LT_problem1_option3.setChoiceTexts("True,False");
        LT_problem1_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option3.setQuestionText("");
        jPanel2.add(LT_problem1_option3);
        LT_problem1_option3.setBounds(430, 180, 130, 30);

        LT_problem1_option4.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem1_option4.setNChoices(2);
        LT_problem1_option4.setChoiceLayout(1);
        LT_problem1_option4.setChoiceTexts("True,False");
        LT_problem1_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option4.setQuestionText("");
        jPanel2.add(LT_problem1_option4);
        LT_problem1_option4.setBounds(430, 210, 130, 30);

        LT_problem1_option5.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem1_option5.setNChoices(2);
        LT_problem1_option5.setChoiceLayout(1);
        LT_problem1_option5.setChoiceTexts("True,False");
        LT_problem1_option5.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option5.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option5.setQuestionText("");
        jPanel2.add(LT_problem1_option5);
        LT_problem1_option5.setBounds(430, 240, 130, 30);

        LT_problem1_option6.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem1_option6.setNChoices(2);
        LT_problem1_option6.setChoiceLayout(1);
        LT_problem1_option6.setChoiceTexts("True,False");
        LT_problem1_option6.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option6.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option6.setQuestionText("");
        jPanel2.add(LT_problem1_option6);
        LT_problem1_option6.setBounds(430, 270, 130, 30);

        LT_problem1_option7.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem1_option7.setNChoices(2);
        LT_problem1_option7.setChoiceLayout(1);
        LT_problem1_option7.setChoiceTexts("True,False");
        LT_problem1_option7.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option7.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option7.setQuestionText("");
        jPanel2.add(LT_problem1_option7);
        LT_problem1_option7.setBounds(430, 300, 130, 30);

        LT_problem1_option1.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem1_option1.setNChoices(2);
        LT_problem1_option1.setChoiceLayout(1);
        LT_problem1_option1.setChoiceTexts("True,False");
        LT_problem1_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem1_option1.setQuestionText("");
        jPanel2.add(LT_problem1_option1);
        LT_problem1_option1.setBounds(430, 120, 130, 30);

        LT_problem2_option2.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem2_option2.setNChoices(2);
        LT_problem2_option2.setChoiceLayout(1);
        LT_problem2_option2.setChoiceTexts("True,False");
        LT_problem2_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option2.setQuestionText("");
        jPanel2.add(LT_problem2_option2);
        LT_problem2_option2.setBounds(430, 420, 130, 30);

        LT_problem2_option3.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem2_option3.setNChoices(2);
        LT_problem2_option3.setChoiceLayout(1);
        LT_problem2_option3.setChoiceTexts("True,False");
        LT_problem2_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option3.setQuestionText("");
        jPanel2.add(LT_problem2_option3);
        LT_problem2_option3.setBounds(430, 450, 130, 30);

        LT_problem2_option4.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem2_option4.setNChoices(2);
        LT_problem2_option4.setChoiceLayout(1);
        LT_problem2_option4.setChoiceTexts("True,False");
        LT_problem2_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option4.setQuestionText("");
        jPanel2.add(LT_problem2_option4);
        LT_problem2_option4.setBounds(430, 480, 130, 30);

        LT_problem2_option5.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem2_option5.setNChoices(2);
        LT_problem2_option5.setChoiceLayout(1);
        LT_problem2_option5.setChoiceTexts("True,False");
        LT_problem2_option5.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option5.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option5.setQuestionText("");
        jPanel2.add(LT_problem2_option5);
        LT_problem2_option5.setBounds(430, 510, 130, 30);

        LT_problem2_option6.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem2_option6.setNChoices(2);
        LT_problem2_option6.setChoiceLayout(1);
        LT_problem2_option6.setChoiceTexts("True,False");
        LT_problem2_option6.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option6.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option6.setQuestionText("");
        jPanel2.add(LT_problem2_option6);
        LT_problem2_option6.setBounds(430, 540, 130, 30);

        LT_problem2_option7.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem2_option7.setNChoices(2);
        LT_problem2_option7.setChoiceLayout(1);
        LT_problem2_option7.setChoiceTexts("True,False");
        LT_problem2_option7.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option7.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem2_option7.setQuestionText("");
        jPanel2.add(LT_problem2_option7);
        LT_problem2_option7.setBounds(430, 570, 130, 30);

        LT_problem3_option1.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem3_option1.setNChoices(2);
        LT_problem3_option1.setChoiceLayout(1);
        LT_problem3_option1.setChoiceTexts("True,False");
        LT_problem3_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option1.setQuestionText("");
        jPanel2.add(LT_problem3_option1);
        LT_problem3_option1.setBounds(430, 710, 130, 30);

        LT_problem3_option2.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem3_option2.setNChoices(2);
        LT_problem3_option2.setChoiceLayout(1);
        LT_problem3_option2.setChoiceTexts("True,False");
        LT_problem3_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option2.setQuestionText("");
        jPanel2.add(LT_problem3_option2);
        LT_problem3_option2.setBounds(430, 740, 130, 30);

        LT_problem3_option3.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem3_option3.setNChoices(2);
        LT_problem3_option3.setChoiceLayout(1);
        LT_problem3_option3.setChoiceTexts("True,False");
        LT_problem3_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option3.setQuestionText("");
        jPanel2.add(LT_problem3_option3);
        LT_problem3_option3.setBounds(430, 770, 130, 30);

        LT_problem3_option4.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem3_option4.setNChoices(2);
        LT_problem3_option4.setChoiceLayout(1);
        LT_problem3_option4.setChoiceTexts("True,False");
        LT_problem3_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option4.setQuestionText("");
        jPanel2.add(LT_problem3_option4);
        LT_problem3_option4.setBounds(430, 800, 130, 30);

        LT_problem3_option5.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem3_option5.setNChoices(2);
        LT_problem3_option5.setChoiceLayout(1);
        LT_problem3_option5.setChoiceTexts("True,False");
        LT_problem3_option5.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option5.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option5.setQuestionText("");
        jPanel2.add(LT_problem3_option5);
        LT_problem3_option5.setBounds(430, 830, 130, 30);

        LT_problem3_option6.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem3_option6.setNChoices(2);
        LT_problem3_option6.setChoiceLayout(1);
        LT_problem3_option6.setChoiceTexts("True,False");
        LT_problem3_option6.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option6.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem3_option6.setQuestionText("");
        jPanel2.add(LT_problem3_option6);
        LT_problem3_option6.setBounds(430, 860, 130, 30);

        LT_problem4_option1.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem4_option1.setNChoices(2);
        LT_problem4_option1.setChoiceLayout(1);
        LT_problem4_option1.setChoiceTexts("True,False");
        LT_problem4_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option1.setQuestionText("");
        jPanel2.add(LT_problem4_option1);
        LT_problem4_option1.setBounds(430, 960, 130, 30);

        LT_problem4_option2.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem4_option2.setNChoices(2);
        LT_problem4_option2.setChoiceLayout(1);
        LT_problem4_option2.setChoiceTexts("True,False");
        LT_problem4_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option2.setQuestionText("");
        jPanel2.add(LT_problem4_option2);
        LT_problem4_option2.setBounds(430, 990, 130, 30);

        LT_problem4_option3.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem4_option3.setNChoices(2);
        LT_problem4_option3.setChoiceLayout(1);
        LT_problem4_option3.setChoiceTexts("True,False");
        LT_problem4_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option3.setQuestionText("");
        jPanel2.add(LT_problem4_option3);
        LT_problem4_option3.setBounds(430, 1020, 130, 30);

        LT_problem4_option4.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem4_option4.setNChoices(2);
        LT_problem4_option4.setChoiceLayout(1);
        LT_problem4_option4.setChoiceTexts("True,False");
        LT_problem4_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option4.setQuestionText("");
        jPanel2.add(LT_problem4_option4);
        LT_problem4_option4.setBounds(430, 1050, 130, 30);

        LT_problem4_option5.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem4_option5.setNChoices(2);
        LT_problem4_option5.setChoiceLayout(1);
        LT_problem4_option5.setChoiceTexts("True,False");
        LT_problem4_option5.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option5.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option5.setQuestionText("");
        jPanel2.add(LT_problem4_option5);
        LT_problem4_option5.setBounds(430, 1080, 130, 30);

        LT_problem4_option6.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem4_option6.setNChoices(2);
        LT_problem4_option6.setChoiceLayout(1);
        LT_problem4_option6.setChoiceTexts("True,False");
        LT_problem4_option6.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option6.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem4_option6.setQuestionText("");
        jPanel2.add(LT_problem4_option6);
        LT_problem4_option6.setBounds(430, 1110, 130, 40);

        LT_problem5_option1.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem5_option1.setNChoices(2);
        LT_problem5_option1.setChoiceLayout(1);
        LT_problem5_option1.setChoiceTexts("True,False");
        LT_problem5_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem5_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem5_option1.setQuestionText("");
        jPanel2.add(LT_problem5_option1);
        LT_problem5_option1.setBounds(370, 1290, 150, 30);

        LT_problem5_option2.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem5_option2.setNChoices(2);
        LT_problem5_option2.setChoiceLayout(1);
        LT_problem5_option2.setChoiceTexts("True,False");
        LT_problem5_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem5_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem5_option2.setQuestionText("");
        jPanel2.add(LT_problem5_option2);
        LT_problem5_option2.setBounds(370, 1320, 150, 30);

        LT_problem5_option3.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem5_option3.setNChoices(2);
        LT_problem5_option3.setChoiceLayout(1);
        LT_problem5_option3.setChoiceTexts("True,False");
        LT_problem5_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem5_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem5_option3.setQuestionText("");
        jPanel2.add(LT_problem5_option3);
        LT_problem5_option3.setBounds(370, 1350, 150, 30);

        LT_problem5_option4.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem5_option4.setNChoices(2);
        LT_problem5_option4.setChoiceLayout(1);
        LT_problem5_option4.setChoiceTexts("True,False");
        LT_problem5_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem5_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem5_option4.setQuestionText("");
        jPanel2.add(LT_problem5_option4);
        LT_problem5_option4.setBounds(370, 1380, 150, 30);

        LT_problem5_option5.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem5_option5.setNChoices(2);
        LT_problem5_option5.setChoiceLayout(1);
        LT_problem5_option5.setChoiceTexts("True,False");
        LT_problem5_option5.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem5_option5.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem5_option5.setQuestionText("");
        jPanel2.add(LT_problem5_option5);
        LT_problem5_option5.setBounds(370, 1410, 150, 30);

        LT_problem6_option1.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem6_option1.setNChoices(2);
        LT_problem6_option1.setChoiceLayout(1);
        LT_problem6_option1.setChoiceTexts("True,False");
        LT_problem6_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem6_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem6_option1.setQuestionText("");
        jPanel2.add(LT_problem6_option1);
        LT_problem6_option1.setBounds(370, 1530, 150, 30);

        LT_problem6_option2.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem6_option2.setNChoices(2);
        LT_problem6_option2.setChoiceLayout(1);
        LT_problem6_option2.setChoiceTexts("True,False");
        LT_problem6_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem6_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem6_option2.setQuestionText("");
        jPanel2.add(LT_problem6_option2);
        LT_problem6_option2.setBounds(370, 1560, 150, 30);

        LT_problem6_option3.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem6_option3.setNChoices(2);
        LT_problem6_option3.setChoiceLayout(1);
        LT_problem6_option3.setChoiceTexts("True,False");
        LT_problem6_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem6_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem6_option3.setQuestionText("");
        jPanel2.add(LT_problem6_option3);
        LT_problem6_option3.setBounds(370, 1590, 150, 30);

        LT_problem6_option4.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem6_option4.setNChoices(2);
        LT_problem6_option4.setChoiceLayout(1);
        LT_problem6_option4.setChoiceTexts("True,False");
        LT_problem6_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem6_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem6_option4.setQuestionText("");
        jPanel2.add(LT_problem6_option4);
        LT_problem6_option4.setBounds(370, 1620, 150, 30);

        LT_problem6_option5.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem6_option5.setNChoices(2);
        LT_problem6_option5.setChoiceLayout(1);
        LT_problem6_option5.setChoiceTexts("True,False");
        LT_problem6_option5.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem6_option5.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem6_option5.setQuestionText("");
        jPanel2.add(LT_problem6_option5);
        LT_problem6_option5.setBounds(370, 1650, 150, 30);

        LT_problem6_option6.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem6_option6.setNChoices(2);
        LT_problem6_option6.setChoiceLayout(1);
        LT_problem6_option6.setChoiceTexts("True,False");
        LT_problem6_option6.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem6_option6.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem6_option6.setQuestionText("");
        jPanel2.add(LT_problem6_option6);
        LT_problem6_option6.setBounds(370, 1680, 150, 30);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel13.setForeground(new java.awt.Color(255, 0, 0));
        jLabel13.setText("For each of the following, click TRUE if the statement is true and click FALSE otherwise.");
        jPanel2.add(jLabel13);
        jLabel13.setBounds(30, 1190, 610, 30);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 0, 0));
        jLabel14.setText("For each of the following, click TRUE if the statement is true and click FALSE otherwise.");
        jPanel2.add(jLabel14);
        jLabel14.setBounds(10, 40, 610, 30);

        dorminLabel9.setImageName("go_page3.png");
        dorminLabel9.setText("");
        jPanel2.add(dorminLabel9);
        dorminLabel9.setBounds(360, 1810, 70, 40);

        LT_problem5_option6.setBackground(new java.awt.Color(255, 255, 255));
        LT_problem5_option6.setNChoices(2);
        LT_problem5_option6.setChoiceLayout(1);
        LT_problem5_option6.setChoiceTexts("True,False");
        LT_problem5_option6.setCorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem5_option6.setIncorrectColor(new java.awt.Color(0, 0, 0));
        LT_problem5_option6.setQuestionText("");
        jPanel2.add(LT_problem5_option6);
        LT_problem5_option6.setBounds(370, 1440, 150, 30);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 0, 0));
        jLabel8.setText("<HTML>Please proceed to the next page by clicking the tab");
        jPanel2.add(jLabel8);
        jLabel8.setBounds(30, 1800, 330, 60);

        jScrollPane2.setViewportView(jPanel2);

        jTabbedPane1.addTab("Page 2", jScrollPane2);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setAutoscrolls(true);
        jPanel4.setPreferredSize(new java.awt.Dimension(0, 3500));
        jPanel4.setLayout(null);

        dorminLabel133.setFont(new java.awt.Font("Tahoma", 0, 12));
	dorminLabel133.setImageName("B_eqexp_problem1.png");
        dorminLabel133.setText("");
        jPanel4.add(dorminLabel133);
        dorminLabel133.setBounds(40, 780, 370, 570);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 0, 0));
        jLabel5.setText("<HTML>at the top.");
        jPanel4.add(jLabel5);
        jLabel5.setBounds(440, 1370, 80, 60);
        jPanel4.add(horizontalLine9);
        horizontalLine9.setBounds(160, 1430, 260, 10);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 0, 0));
        jLabel10.setText("<HTML><B>Please be sure to work through all items. </B>");
        jPanel4.add(jLabel10);
        jLabel10.setBounds(10, 20, 600, 30);

        effective_problem1_option1.setBackground(new java.awt.Color(255, 255, 255));
        effective_problem1_option1.setNChoices(2);
        effective_problem1_option1.setChoiceLayout(1);
        effective_problem1_option1.setChoiceTexts("Agree,Disagree");
        effective_problem1_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option1.setQuestionText("");
        jPanel4.add(effective_problem1_option1);
        effective_problem1_option1.setBounds(350, 180, 170, 30);

        effective_problem1_option2.setBackground(new java.awt.Color(255, 255, 255));
        effective_problem1_option2.setNChoices(2);
        effective_problem1_option2.setChoiceLayout(1);
        effective_problem1_option2.setChoiceTexts("Agree,Disagree");
        effective_problem1_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option2.setQuestionText("");
        jPanel4.add(effective_problem1_option2);
        effective_problem1_option2.setBounds(350, 210, 170, 30);

        effective_problem1_option3.setBackground(new java.awt.Color(255, 255, 255));
        effective_problem1_option3.setNChoices(2);
        effective_problem1_option3.setChoiceLayout(1);
        effective_problem1_option3.setChoiceTexts("Agree,Disagree");
        effective_problem1_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option3.setQuestionText("");
        jPanel4.add(effective_problem1_option3);
        effective_problem1_option3.setBounds(350, 240, 170, 30);

        effective_problem1_option4.setBackground(new java.awt.Color(255, 255, 255));
        effective_problem1_option4.setNChoices(2);
        effective_problem1_option4.setChoiceLayout(1);
        effective_problem1_option4.setChoiceTexts("Agree,Disagree");
        effective_problem1_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem1_option4.setQuestionText("");
        jPanel4.add(effective_problem1_option4);
        effective_problem1_option4.setBounds(350, 270, 170, 30);

        effective_problem2_option1.setBackground(new java.awt.Color(255, 255, 255));
        effective_problem2_option1.setNChoices(2);
        effective_problem2_option1.setChoiceLayout(1);
        effective_problem2_option1.setChoiceTexts("Agree,Disagree");
        effective_problem2_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option1.setQuestionText("");
        jPanel4.add(effective_problem2_option1);
        effective_problem2_option1.setBounds(350, 360, 170, 30);

        effective_problem2_option2.setBackground(new java.awt.Color(255, 255, 255));
        effective_problem2_option2.setNChoices(2);
        effective_problem2_option2.setChoiceLayout(1);
        effective_problem2_option2.setChoiceTexts("Agree,Disagree");
        effective_problem2_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option2.setQuestionText("");
        jPanel4.add(effective_problem2_option2);
        effective_problem2_option2.setBounds(350, 390, 170, 30);

        effective_problem2_option3.setBackground(new java.awt.Color(255, 255, 255));
        effective_problem2_option3.setNChoices(2);
        effective_problem2_option3.setChoiceLayout(1);
        effective_problem2_option3.setChoiceTexts("Agree,Disagree");
        effective_problem2_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option3.setQuestionText("");
        jPanel4.add(effective_problem2_option3);
        effective_problem2_option3.setBounds(350, 420, 170, 30);

        effective_problem2_option4.setBackground(new java.awt.Color(255, 255, 255));
        effective_problem2_option4.setNChoices(2);
        effective_problem2_option4.setChoiceLayout(1);
        effective_problem2_option4.setChoiceTexts("Agree,Disagree");
        effective_problem2_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem2_option4.setQuestionText("");
        jPanel4.add(effective_problem2_option4);
        effective_problem2_option4.setBounds(350, 450, 170, 30);

        effective_problem3_option1.setBackground(new java.awt.Color(255, 255, 255));
        effective_problem3_option1.setNChoices(2);
        effective_problem3_option1.setChoiceLayout(1);
        effective_problem3_option1.setChoiceTexts("Agree,Disagree");
        effective_problem3_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem3_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem3_option1.setQuestionText("");
        jPanel4.add(effective_problem3_option1);
        effective_problem3_option1.setBounds(350, 570, 170, 30);

        effective_problem3_option2.setBackground(new java.awt.Color(255, 255, 255));
        effective_problem3_option2.setNChoices(2);
        effective_problem3_option2.setChoiceLayout(1);
        effective_problem3_option2.setChoiceTexts("Agree,Disagree");
        effective_problem3_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem3_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem3_option2.setQuestionText("");
        jPanel4.add(effective_problem3_option2);
        effective_problem3_option2.setBounds(350, 600, 170, 30);

        effective_problem3_option3.setBackground(new java.awt.Color(255, 255, 255));
        effective_problem3_option3.setNChoices(2);
        effective_problem3_option3.setChoiceLayout(1);
        effective_problem3_option3.setChoiceTexts("Agree,Disagree");
        effective_problem3_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem3_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem3_option3.setQuestionText("");
        jPanel4.add(effective_problem3_option3);
        effective_problem3_option3.setBounds(350, 630, 170, 30);

        effective_problem3_option4.setBackground(new java.awt.Color(255, 255, 255));
        effective_problem3_option4.setNChoices(2);
        effective_problem3_option4.setChoiceLayout(1);
        effective_problem3_option4.setChoiceTexts("Agree,Disagree");
        effective_problem3_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem3_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        effective_problem3_option4.setQuestionText("");
        jPanel4.add(effective_problem3_option4);
        effective_problem3_option4.setBounds(350, 660, 170, 30);

        eqexp_problem1_option1.setBackground(new java.awt.Color(255, 255, 255));
        eqexp_problem1_option1.setNChoices(2);
        eqexp_problem1_option1.setChoiceLayout(1);
        eqexp_problem1_option1.setChoiceTexts("True,False");
        eqexp_problem1_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem1_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem1_option1.setQuestionText("");
        jPanel4.add(eqexp_problem1_option1);
        eqexp_problem1_option1.setBounds(410, 830, 150, 30);

        eqexp_problem1_option2.setBackground(new java.awt.Color(255, 255, 255));
        eqexp_problem1_option2.setNChoices(2);
        eqexp_problem1_option2.setChoiceLayout(1);
        eqexp_problem1_option2.setChoiceTexts("True,False");
        eqexp_problem1_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem1_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem1_option2.setQuestionText("");
        jPanel4.add(eqexp_problem1_option2);
        eqexp_problem1_option2.setBounds(410, 860, 150, 30);

        eqexp_problem1_option3.setBackground(new java.awt.Color(255, 255, 255));
        eqexp_problem1_option3.setNChoices(2);
        eqexp_problem1_option3.setChoiceLayout(1);
        eqexp_problem1_option3.setChoiceTexts("True,False");
        eqexp_problem1_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem1_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem1_option3.setQuestionText("");
        jPanel4.add(eqexp_problem1_option3);
        eqexp_problem1_option3.setBounds(410, 890, 150, 30);

        eqexp_problem1_option4.setBackground(new java.awt.Color(255, 255, 255));
        eqexp_problem1_option4.setNChoices(2);
        eqexp_problem1_option4.setChoiceLayout(1);
        eqexp_problem1_option4.setChoiceTexts("True,False");
        eqexp_problem1_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem1_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem1_option4.setQuestionText("");
        jPanel4.add(eqexp_problem1_option4);
        eqexp_problem1_option4.setBounds(410, 920, 150, 30);

        eqexp_problem1_option5.setBackground(new java.awt.Color(255, 255, 255));
        eqexp_problem1_option5.setNChoices(2);
        eqexp_problem1_option5.setChoiceLayout(1);
        eqexp_problem1_option5.setChoiceTexts("True,False");
        eqexp_problem1_option5.setCorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem1_option5.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem1_option5.setQuestionText("");
        jPanel4.add(eqexp_problem1_option5);
        eqexp_problem1_option5.setBounds(410, 950, 150, 30);

        eqexp_problem2_option1.setBackground(new java.awt.Color(255, 255, 255));
        eqexp_problem2_option1.setNChoices(2);
        eqexp_problem2_option1.setChoiceLayout(1);
        eqexp_problem2_option1.setChoiceTexts("True,False");
        eqexp_problem2_option1.setCorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem2_option1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem2_option1.setQuestionText("");
        jPanel4.add(eqexp_problem2_option1);
        eqexp_problem2_option1.setBounds(410, 1050, 150, 30);

        eqexp_problem2_option2.setBackground(new java.awt.Color(255, 255, 255));
        eqexp_problem2_option2.setNChoices(2);
        eqexp_problem2_option2.setChoiceLayout(1);
        eqexp_problem2_option2.setChoiceTexts("True,False");
        eqexp_problem2_option2.setCorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem2_option2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem2_option2.setQuestionText("");
        jPanel4.add(eqexp_problem2_option2);
        eqexp_problem2_option2.setBounds(410, 1100, 150, 30);

        eqexp_problem2_option3.setBackground(new java.awt.Color(255, 255, 255));
        eqexp_problem2_option3.setNChoices(2);
        eqexp_problem2_option3.setChoiceLayout(1);
        eqexp_problem2_option3.setChoiceTexts("True,False");
        eqexp_problem2_option3.setCorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem2_option3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem2_option3.setQuestionText("");
        jPanel4.add(eqexp_problem2_option3);
        eqexp_problem2_option3.setBounds(410, 1160, 150, 30);

        eqexp_problem2_option4.setBackground(new java.awt.Color(255, 255, 255));
        eqexp_problem2_option4.setNChoices(2);
        eqexp_problem2_option4.setChoiceLayout(1);
        eqexp_problem2_option4.setChoiceTexts("True,False");
        eqexp_problem2_option4.setCorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem2_option4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem2_option4.setQuestionText("");
        jPanel4.add(eqexp_problem2_option4);
        eqexp_problem2_option4.setBounds(410, 1210, 150, 30);

        eqexp_problem2_option5.setBackground(new java.awt.Color(255, 255, 255));
        eqexp_problem2_option5.setNChoices(2);
        eqexp_problem2_option5.setChoiceLayout(1);
        eqexp_problem2_option5.setChoiceTexts("True,False");
        eqexp_problem2_option5.setCorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem2_option5.setIncorrectColor(new java.awt.Color(0, 0, 0));
        eqexp_problem2_option5.setQuestionText("");
        jPanel4.add(eqexp_problem2_option5);
        eqexp_problem2_option5.setBounds(410, 1270, 150, 30);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel15.setForeground(new java.awt.Color(255, 0, 0));
        jLabel15.setText("<HTML>For each of the following, click TRUE if the statement is true and click FALSE otherwise.");
        jPanel4.add(jLabel15);
        jLabel15.setBounds(30, 720, 610, 80);

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel16.setForeground(new java.awt.Color(255, 0, 0));
        jLabel16.setText("<HTML>Adding 3 to both sides of 2x-3=5 is a good move to solve the equation, but adding 5 to both sides of 2x-3=5 is not a good move.  For each of the following questions, state if a suggested move is a good move or not. Click AGREE if you think it is a good move, and DISAGREE Otherwise");
        jPanel4.add(jLabel16);
        jLabel16.setBounds(10, 50, 590, 80);
        jLabel16.getAccessibleContext().setAccessibleName("<HTML>Adding 3 to both sides of 2x-3=5 is a good move to solve the equation, but adding 5 to both sides of 2x-3=5 is not a good move.  For each of the following questions, state if a suggested move is a good move or not. Click AGREE if you think it is a good move, and DISAGREE otherwise.");

        dorminLabel17.setImageName("go_page4.png");
        dorminLabel17.setText("");
        jPanel4.add(dorminLabel17);
        dorminLabel17.setBounds(360, 1380, 80, 40);

	dorminLabel30.setImageName("B_effective_problem1.png");
        dorminLabel30.setText("");
        jPanel4.add(dorminLabel30);
        dorminLabel30.setBounds(40, 120, 310, 630);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 0, 0));
        jLabel12.setText("<HTML>Please proceed to the next page by clicking the tab");
        jPanel4.add(jLabel12);
        jLabel12.setBounds(30, 1370, 330, 60);

        jScrollPane4.setViewportView(jPanel4);

        jTabbedPane1.addTab("Page 3", jScrollPane4);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setMinimumSize(new java.awt.Dimension(0, 3500));
        jPanel3.setLayout(null);

        dorminLabel111.setFont(new java.awt.Font("Tahoma", 0, 12));
        dorminLabel111.setText("<HTML> <b>18. %(demonstration_problem1_char)% was asked to solve an equation, and %(demonstration_problem1_char)% came up with the following solution, which is incorrect:</b>");
        jPanel3.add(dorminLabel111);
        dorminLabel111.setBounds(30, 40, 570, 60);
        jPanel3.add(horizontalLine88);
        horizontalLine88.setBounds(30, 540, 560, 10);

        dorminLabel117.setFont(new java.awt.Font("Tahoma", 1, 12));
        dorminLabel117.setText("<HTML>Please explain in the following box why the step you chose is incorrect:");
        jPanel3.add(dorminLabel117);
        dorminLabel117.setBounds(30, 390, 560, 30);

        demonstration_problem1_box.setAutoscrolls(true);
        demonstration_problem1_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        demonstration_problem1_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(demonstration_problem1_box);
        demonstration_problem1_box.setBounds(30, 430, 560, 96);

        dorminLabel141.setFont(new java.awt.Font("Tahoma", 0, 12));
        dorminLabel141.setText("<HTML> <b>19. %(demonstration_problem2_char)% was asked to solve an equation, and %(demonstration_problem2_char)% came up with the following solution, which is incorrect:</b>");
        jPanel3.add(dorminLabel141);
        dorminLabel141.setBounds(20, 560, 570, 60);
        jPanel3.add(horizontalLine125);
        horizontalLine125.setBounds(40, 1070, 540, 10);

        demonstration_problem2_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        demonstration_problem2_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(demonstration_problem2_box);
        demonstration_problem2_box.setBounds(40, 950, 540, 96);

        dorminLabel144.setFont(new java.awt.Font("Tahoma", 0, 12));
        dorminLabel144.setText("<HTML> <b>20. %(demonstration_problem3_char)% was asked to solve an equation, and %(demonstration_problem3_char)% came up with the following solution, which is incorrect:</b>");
        jPanel3.add(dorminLabel144);
        dorminLabel144.setBounds(30, 1090, 570, 70);
        jPanel3.add(horizontalLine126);
        horizontalLine126.setBounds(50, 1590, 530, 10);

        demonstration_problem3_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        demonstration_problem3_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(demonstration_problem3_box);
        demonstration_problem3_box.setBounds(40, 1470, 540, 96);

        dorminLabel147.setFont(new java.awt.Font("Tahoma", 0, 12));
        dorminLabel147.setText("<HTML> <b>21. %(demonstration_problem4_char)% was asked to solve an equation, and %(demonstration_problem4_char)% came up with the following solution, which is incorrect:</b>");
        jPanel3.add(dorminLabel147);
        dorminLabel147.setBounds(30, 1610, 570, 60);
        jPanel3.add(horizontalLine127);
        horizontalLine127.setBounds(40, 2130, 540, 10);

        demonstration_problem4_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        demonstration_problem4_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(demonstration_problem4_box);
        demonstration_problem4_box.setBounds(40, 2000, 540, 96);

        dorminLabel150.setFont(new java.awt.Font("Tahoma", 0, 12));
        dorminLabel150.setText("<HTML> <b>22. %(demonstration_problem5_char)% was asked to solve an equation, and %(demonstration_problem5_char)% came up with the following solution, which is incorrect:</b>");
        jPanel3.add(dorminLabel150);
        dorminLabel150.setBounds(30, 2140, 560, 60);
        jPanel3.add(horizontalLine128);
        horizontalLine128.setBounds(50, 2690, 530, 10);

        demonstration_problem5_box.setCorrectColor(new java.awt.Color(0, 0, 0));
        demonstration_problem5_box.setIncorrectColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(demonstration_problem5_box);
        demonstration_problem5_box.setBounds(50, 2580, 530, 96);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("<HTML><b>at the top.</b>");
        jPanel3.add(jLabel1);
        jLabel1.setBounds(530, 2720, 70, 40);
        jPanel3.add(horizontalLine10);
        horizontalLine10.setBounds(140, 2770, 280, 10);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel11.setForeground(new java.awt.Color(255, 0, 0));
        jLabel11.setText("<HTML><B>Please be sure to work through all items. </B>");
        jPanel3.add(jLabel11);
        jLabel11.setBounds(10, 10, 600, 30);

	demonstration_problem1_step1.setImageName("B_demonstration_problem1.png");
        demonstration_problem1_step1.setText("");
        jPanel3.add(demonstration_problem1_step1);
        demonstration_problem1_step1.setBounds(110, 110, 370, 230);

	demonstration_problem2_step1.setImageName("B_demonstration_problem2.png");
        demonstration_problem2_step1.setText("");
        jPanel3.add(demonstration_problem2_step1);
        demonstration_problem2_step1.setBounds(90, 630, 380, 220);

	demonstration_problem3_step1.setImageName("B_demonstration_problem3.png");
        demonstration_problem3_step1.setText("");
        jPanel3.add(demonstration_problem3_step1);
        demonstration_problem3_step1.setBounds(100, 1160, 330, 190);

	demonstration_problem4_step1.setImageName("B_demonstration_problem4.png");
        demonstration_problem4_step1.setText("");
        jPanel3.add(demonstration_problem4_step1);
        demonstration_problem4_step1.setBounds(120, 1680, 290, 210);

	demonstration_problem5_step1.setImageName("B_demonstration_problem5.png");
        demonstration_problem5_step1.setText("");
        jPanel3.add(demonstration_problem5_step1);
        demonstration_problem5_step1.setBounds(120, 2220, 300, 240);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel17.setText("Which step is incorrect?  Enter the line number  ");
        jPanel3.add(jLabel17);
        jLabel17.setBounds(30, 340, 300, 50);

        demonstration_problem1.setCorrectColor(new java.awt.Color(0, 0, 0));
        demonstration_problem1.setIncorrectColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(demonstration_problem1);
        demonstration_problem1.setBounds(330, 350, 50, 30);

        dorminLabel142.setFont(new java.awt.Font("Tahoma", 1, 12));
        dorminLabel142.setText("<HTML>Please explain in the following box why the step you chose is incorrect:");
        jPanel3.add(dorminLabel142);
        dorminLabel142.setBounds(40, 910, 540, 30);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel18.setText("Which step is incorrect?  Enter the line number  ");
        jPanel3.add(jLabel18);
        jLabel18.setBounds(40, 860, 300, 50);

        demonstration_problem2.setCorrectColor(new java.awt.Color(0, 0, 0));
        demonstration_problem2.setIncorrectColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(demonstration_problem2);
        demonstration_problem2.setBounds(340, 870, 50, 30);

        dorminLabel143.setFont(new java.awt.Font("Tahoma", 1, 12));
        dorminLabel143.setText("<HTML>Please explain in the following box why the step you chose is incorrect:");
        jPanel3.add(dorminLabel143);
        dorminLabel143.setBounds(40, 1430, 540, 30);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel19.setText("Which step is incorrect?  Enter the line number  ");
        jPanel3.add(jLabel19);
        jLabel19.setBounds(40, 1380, 300, 50);

        demonstration_problem3.setCorrectColor(new java.awt.Color(0, 0, 0));
        demonstration_problem3.setIncorrectColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(demonstration_problem3);
        demonstration_problem3.setBounds(340, 1390, 50, 30);

        dorminLabel145.setFont(new java.awt.Font("Tahoma", 1, 12));
        dorminLabel145.setText("<HTML>Please explain in the following box why the step you chose is incorrect:");
        jPanel3.add(dorminLabel145);
        dorminLabel145.setBounds(40, 1960, 540, 30);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel20.setText("Which step is incorrect?  Enter the line number  ");
        jPanel3.add(jLabel20);
        jLabel20.setBounds(40, 1910, 300, 50);

        demonstration_problem4.setCorrectColor(new java.awt.Color(0, 0, 0));
        demonstration_problem4.setIncorrectColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(demonstration_problem4);
        demonstration_problem4.setBounds(340, 1920, 50, 30);

        dorminLabel146.setFont(new java.awt.Font("Tahoma", 1, 12));
        dorminLabel146.setText("<HTML>Please explain in the following box why the step you chose is incorrect:");
        jPanel3.add(dorminLabel146);
        dorminLabel146.setBounds(50, 2540, 530, 30);

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel21.setText("Which step is incorrect?  Enter the line number  ");
        jPanel3.add(jLabel21);
        jLabel21.setBounds(50, 2490, 300, 50);

        demonstration_problem5.setCorrectColor(new java.awt.Color(0, 0, 0));
        demonstration_problem5.setIncorrectColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(demonstration_problem5);
        demonstration_problem5.setBounds(350, 2500, 50, 30);

        dorminLabel18.setImageName("go_doneSubmit.png");
        dorminLabel18.setText("");
        jPanel3.add(dorminLabel18);
        dorminLabel18.setBounds(390, 2720, 140, 40);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 0, 0));
        jLabel22.setText("<HTML><b>Please go to the [Done and Submit] page by clicking the tab</b>");
        jPanel3.add(jLabel22);
        jLabel22.setBounds(10, 2720, 380, 40);

        jScrollPane3.setViewportView(jPanel3);

        jTabbedPane1.addTab("Page 4", jScrollPane3);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Done.setText("<HTML><b>I'm Done</b>, Submit My Answers");
        Done.addStudentActionListener(new pact.DorminWidgets.event.StudentActionListener() {
            public void studentActionPerformed(pact.DorminWidgets.event.StudentActionEvent evt) {
                promptNow(evt);
            }
        });
        jPanel5.add(Done, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 290, -1, -1));

        jLabel9.setForeground(new java.awt.Color(255, 0, 0));
        jLabel9.setText("<HTML><b>Click on the button that says [I'm done, Submit My Answers] to officially submit your responses.</b>");
        jPanel5.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, 560, 100));

        promptSuccess.setFont(new java.awt.Font("Tahoma", 1, 14));
        promptSuccess.setForeground(new java.awt.Color(0, 153, 0));
        jPanel5.add(promptSuccess, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 360, 320, 80));

        jTabbedPane1.addTab("Done and Submit", jPanel5);

        add(jTabbedPane1);
        jTabbedPane1.setBounds(10, 70, 620, 510);
        jTabbedPane1.getAccessibleContext().setAccessibleName("");
    }// </editor-fold>//GEN-END:initComponents

    private void promptNow(pact.DorminWidgets.event.StudentActionEvent evt) {//GEN-FIRST:event_promptNow
        // TODO add your handling code here:
         promptSuccess.setText("Congratulations! You've completed the Test!");
         Done.setEnabled(false);
    }//GEN-LAST:event_promptNow

    public static void main(String[] argv) {
new CTAT_Launcher(argv).launch (new TabbedPreTestB());    }

    public java.awt.Dimension getPreferredSize()
    {
        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
        return new java.awt.Dimension(682, (int)((tk.getScreenSize().height)*0.8));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pact.DorminWidgets.DorminButton Done;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem1_option1;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem1_option2;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem1_option3;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem1_option4;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem1_option5;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem1_option6;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem1_option7;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem2_option1;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem2_option2;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem2_option3;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem2_option4;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem2_option5;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem2_option6;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem2_option7;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem3_option1;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem3_option2;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem3_option3;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem3_option4;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem3_option5;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem3_option6;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem4_option1;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem4_option2;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem4_option3;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem4_option4;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem4_option5;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem4_option6;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem5_option1;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem5_option2;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem5_option3;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem5_option4;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem5_option5;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem5_option6;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem6_option1;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem6_option2;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem6_option3;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem6_option4;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem6_option5;
    private pact.DorminWidgets.DorminMultipleChoice LT_problem6_option6;
    private edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options cTAT_Options1;
    private edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options cTAT_Options2;
    private pact.DorminWidgets.DorminTextField demonstration_problem1;
    private pact.DorminWidgets.DorminTextArea demonstration_problem1_box;
    private pact.DorminWidgets.DorminLabel demonstration_problem1_step1;
    private pact.DorminWidgets.DorminTextField demonstration_problem2;
    private pact.DorminWidgets.DorminTextArea demonstration_problem2_box;
    private pact.DorminWidgets.DorminLabel demonstration_problem2_step1;
    private pact.DorminWidgets.DorminTextField demonstration_problem3;
    private pact.DorminWidgets.DorminTextArea demonstration_problem3_box;
    private pact.DorminWidgets.DorminLabel demonstration_problem3_step1;
    private pact.DorminWidgets.DorminTextField demonstration_problem4;
    private pact.DorminWidgets.DorminTextArea demonstration_problem4_box;
    private pact.DorminWidgets.DorminLabel demonstration_problem4_step1;
    private pact.DorminWidgets.DorminTextField demonstration_problem5;
    private pact.DorminWidgets.DorminTextArea demonstration_problem5_box;
    private pact.DorminWidgets.DorminLabel demonstration_problem5_step1;
    private pact.DorminWidgets.DorminLabel dorminLabel100;
    private pact.DorminWidgets.DorminLabel dorminLabel111;
    private pact.DorminWidgets.DorminLabel dorminLabel117;
    private pact.DorminWidgets.DorminLabel dorminLabel133;
    private pact.DorminWidgets.DorminLabel dorminLabel141;
    private pact.DorminWidgets.DorminLabel dorminLabel142;
    private pact.DorminWidgets.DorminLabel dorminLabel143;
    private pact.DorminWidgets.DorminLabel dorminLabel144;
    private pact.DorminWidgets.DorminLabel dorminLabel145;
    private pact.DorminWidgets.DorminLabel dorminLabel146;
    private pact.DorminWidgets.DorminLabel dorminLabel147;
    private pact.DorminWidgets.DorminLabel dorminLabel150;
    private pact.DorminWidgets.DorminLabel dorminLabel17;
    private pact.DorminWidgets.DorminLabel dorminLabel18;
    private pact.DorminWidgets.DorminLabel dorminLabel19;
    private pact.DorminWidgets.DorminLabel dorminLabel30;
    private pact.DorminWidgets.DorminLabel dorminLabel58;
    private pact.DorminWidgets.DorminLabel dorminLabel7;
    private pact.DorminWidgets.DorminLabel dorminLabel8;
    private pact.DorminWidgets.DorminLabel dorminLabel81;
    private pact.DorminWidgets.DorminLabel dorminLabel9;
    private pact.DorminWidgets.DorminMultipleChoice effective_problem1_option1;
    private pact.DorminWidgets.DorminMultipleChoice effective_problem1_option2;
    private pact.DorminWidgets.DorminMultipleChoice effective_problem1_option3;
    private pact.DorminWidgets.DorminMultipleChoice effective_problem1_option4;
    private pact.DorminWidgets.DorminMultipleChoice effective_problem2_option1;
    private pact.DorminWidgets.DorminMultipleChoice effective_problem2_option2;
    private pact.DorminWidgets.DorminMultipleChoice effective_problem2_option3;
    private pact.DorminWidgets.DorminMultipleChoice effective_problem2_option4;
    private pact.DorminWidgets.DorminMultipleChoice effective_problem3_option1;
    private pact.DorminWidgets.DorminMultipleChoice effective_problem3_option2;
    private pact.DorminWidgets.DorminMultipleChoice effective_problem3_option3;
    private pact.DorminWidgets.DorminMultipleChoice effective_problem3_option4;
    private pact.DorminWidgets.DorminTextField eq_problem1_box;
    private pact.DorminWidgets.DorminTextField eq_problem2_box;
    private pact.DorminWidgets.DorminTextField eq_problem3_box;
    private pact.DorminWidgets.DorminTextField eq_problem4_box;
    private pact.DorminWidgets.DorminTextField eq_problem5_box;
    private pact.DorminWidgets.DorminTextField eq_problem6_box;
    private pact.DorminWidgets.DorminMultipleChoice eqexp_problem1_option1;
    private pact.DorminWidgets.DorminMultipleChoice eqexp_problem1_option2;
    private pact.DorminWidgets.DorminMultipleChoice eqexp_problem1_option3;
    private pact.DorminWidgets.DorminMultipleChoice eqexp_problem1_option4;
    private pact.DorminWidgets.DorminMultipleChoice eqexp_problem1_option5;
    private pact.DorminWidgets.DorminMultipleChoice eqexp_problem2_option1;
    private pact.DorminWidgets.DorminMultipleChoice eqexp_problem2_option2;
    private pact.DorminWidgets.DorminMultipleChoice eqexp_problem2_option3;
    private pact.DorminWidgets.DorminMultipleChoice eqexp_problem2_option4;
    private pact.DorminWidgets.DorminMultipleChoice eqexp_problem2_option5;
    private pact.DorminWidgets.HorizontalLine horizontalLine10;
    private pact.DorminWidgets.HorizontalLine horizontalLine125;
    private pact.DorminWidgets.HorizontalLine horizontalLine126;
    private pact.DorminWidgets.HorizontalLine horizontalLine127;
    private pact.DorminWidgets.HorizontalLine horizontalLine128;
    private pact.DorminWidgets.HorizontalLine horizontalLine6;
    private pact.DorminWidgets.HorizontalLine horizontalLine8;
    private pact.DorminWidgets.HorizontalLine horizontalLine88;
    private pact.DorminWidgets.HorizontalLine horizontalLine9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel promptSuccess;
    // End of variables declaration//GEN-END:variables
    
}
