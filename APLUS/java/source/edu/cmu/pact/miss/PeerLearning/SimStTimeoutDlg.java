package edu.cmu.pact.miss.PeerLearning;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog; 
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;





public class SimStTimeoutDlg extends JDialog implements ActionListener,FocusListener{
	/*Options for the menu items*/
	static final String stuckTxtHint="I am stuck too, I don't know <reasoning>.";
	static final String stuckTxtFeedback="I am stuck, I don't know the answer to this question.";
	static final String confusedTxtFeedback="I know the answer but I don't know how to respond.";	
	static final String confusedTxtHint="I know <reasoning> but I don't know how to show it.";	
	static final String confusedTxtNoTutoring="I don't know what I am supposed to do here.";	
	static final String thinkingTxt="I am still thinking, I need more time."; 
	static final String boredTxt="I am bored."; 
	static final String otherTxt="Other:"; 
	public static final String reasoningForTransformation="the next step";
	
	/*Templates for the responses*/
	static final String stuckResponse="If you don't know <reasoning>, you can always click on Mr Williams. \n\nAlso, there are resources that can help you, like the Unit Overview and the Examples!";
	static final String stuckResponseFeedback="If you don't know the answer, you can always click on Mr Williams. \n\nAlso, there are resources that can help you, like the Unit Overview and the Examples!";
	static final String confusedResponse="If you don't know how to show <reasoning>, you may want to click on Mr Williams.";	
	static final String confusedResponseFeedback="If you don't know how to respond, you may want to click on Mr Williams.";
	static final String thinkingResponse="Ok, take your time! \nIf you don't know <reasoning>, you can always click on Mr Williams. \n\nAlso, there are resources that can help you, like the Unit Overview and the Examples!"; 
	static final String thinkingResponseFeedback="Ok, take your time! \nIf you don't know the answer, you can always click on Mr Williams. \n\nAlso, there are resources that can help you, like the Unit Overview and the Examples!";
	static final String boredResponse="Oh, I am sorry to hear that you are bored.\n\n SimStName is counting on your help to pass the Quiz, I really hope you can help SimStName!"; 
	static final String otherResponse="Thank you for your input. I hope you can continue teaching Joe. Ask your teacher if you need help."; 
	static final String confusedResponseNoTutoring="If you don't know what to do here, you may want to click on Mr Williams.";	
	static final String MrWilliams="img/mrWill_full.png";
	static final String HappyFace="img/happy-face.jpg";
	public static final String TYPE_TRANSFORMATION="transformation";
	public static final String TYPE_TYPEIN="type-in";
	static final String TITLE="Mr Williams says...";
	
	
	public static final Font SMALL_FONT = new Font("Comic Sans MS", Font.PLAIN, 12);
	public static final Font MED_FONT = new Font("Comic Sans MS", Font.PLAIN, 15);
	//public static final Font MED_FONT = new Font("Serif", Font.PLAIN, 22);
	public static final Font HUGE_FONT = new Font("Serif", Font.PLAIN, 68);
	boolean optionSelected=false;
	/*Radio buttons and groups*/
	ButtonGroup group;
	JRadioButton stuckOption;
	JRadioButton confusedOption;
	JRadioButton thinkingOption;
	JRadioButton boredOption;
	JRadioButton otherOption;
	
	/*Buttons*/
	JButton closeButton;
	JButton doMyBestButton;
	JButton quitButton;
	
	/*Panels*/
	JPanel backPanel;
	JPanel optionsPanel;
	JPanel responsePanel;
	/*Text panes*/
	JTextPane responseMsg;
	//JTextPane errorMsg;
	/*Text fields*/
	JTextField otherText;

	String response=null;
	boolean isTutoring; //boolean indicating if we timeout occurred while tutoring 
	boolean transformation;
	String reasoning;
	String simStName;
	
	String stuckTxtActual;
	private void setStuckTxtActual(String text){
		if (reasoning!=null)
			this.stuckTxtActual=text.replace("<reasoning>", reasoning);
		else
			this.stuckTxtActual=text;
			
	}
	String confusedTxtActual;
	private void setConfusedTxtActual(String text){
		if (reasoning!=null)
			this.confusedTxtActual=text.replace("<reasoning>", reasoning);
		else
			this.confusedTxtActual=text;
	}
	
	String confusedResponseTxtActual;
	private void setConfusedResponseTxtActual(String text,boolean isTransformation){
		
		if (reasoning!=null)
				if (isTransformation) this.confusedResponseTxtActual=text.replace("<reasoning>", reasoning);
				else this.confusedResponseTxtActual=text.replace("<reasoning>", reasoning.replace("how to", ""));
		else this.confusedResponseTxtActual=text;
		
	}
	
	String stuckResponseTxtActual;
	private void setStuckResponseTxtActual(String text){
		if (reasoning!=null)
			this.stuckResponseTxtActual=text.replace("<reasoning>", reasoning);
		else
			this.stuckResponseTxtActual=text;
	}
	
	String thikingResponseTxtActual;
	private void setThikingResponseTxtActual(String text){
		if (reasoning!=null)
			this.thikingResponseTxtActual=text.replace("<reasoning>", reasoning);
		else 
			this.thikingResponseTxtActual=text;	
	}
	
	
	
	BR_Controller brController;
	void setController(BR_Controller brController){this.brController=brController;}
	BR_Controller getController(){return this.brController;}
	
	SimStLogger logger;
	void setLogger(SimStLogger logger){ this.logger=logger;}
	SimStLogger getLogger(){return this.logger;}
	
	
	
	/**
	 * Method to define stuckTxt (for the stuck Option), confusedTxt (for the condfused option)
	 * @param isTutoring
	 * @param isHint
	 * @param isTransformation
	 */
	private void updateTextPanes(boolean isTutoring, boolean isHint, boolean isTransformation,String reasoning){
			
	   if (isTutoring && isHint){
		   //timeout while tutoring and SimStudent is waiting for hint.
			setStuckTxtActual(stuckTxtHint);
			setConfusedTxtActual(confusedTxtHint);
			setStuckResponseTxtActual(stuckResponse);
			setThikingResponseTxtActual(thinkingResponse);
			setConfusedResponseTxtActual(confusedResponse,isTransformation);
		}
		else if (isTutoring && !isHint){
			//timeout while tutoring and SimStudent is waiting for feedback
			setStuckTxtActual(stuckTxtFeedback);
			setConfusedTxtActual(confusedTxtFeedback);
			setStuckResponseTxtActual(stuckResponseFeedback);
			setThikingResponseTxtActual(thinkingResponseFeedback);
			setConfusedResponseTxtActual(confusedResponseFeedback,isTransformation);
		}
		else {
			setConfusedTxtActual(confusedTxtNoTutoring);
			setConfusedResponseTxtActual(confusedResponseNoTutoring,isTransformation);
		}
		
		
		
		
	}
	
	


	/**
	 * Constructor used to display window while waiting for hint
	 * @param simStName
	 * @param isHint
	 * @param isTransformation
	 * @param reasoning
	 */
	public SimStTimeoutDlg(BR_Controller brController, boolean isHint, boolean isTransformation, String reasoning){
		this(brController,true,isHint,isTransformation,reasoning);
	}
	/**
	 * Construction used to display window while tutoring and waiting for feedback 
	 * @param simStName
	 */
	public SimStTimeoutDlg(BR_Controller brController,boolean isHint){
		this(brController,true,isHint,true,null);
	}
	
	
	/**
	 * Construction used to display window while no tutoring 
	 * @param simStName
	 */
	public SimStTimeoutDlg(BR_Controller brController){
		this(brController,false,false,false,null);
	}
	

	/**
	 * Constructor used to display window while tutoring and waiting for hint
	 * @param simStName
	 * @param isTutoring
	 * @param isHint
	 * @param isTransformation
	 * @param reasoning
	 */
	public SimStTimeoutDlg(BR_Controller brController,boolean isTutoring, boolean isHint, boolean isTransformation, String reasoning){
		setPreferredSize(new Dimension(500,330));
		setTitle(TITLE);
		this.setBackground(Color.white);
		
		this.isTutoring=isTutoring;
		this.simStName=brController.getMissController().getSimSt().getSimStName();
		this.reasoning=reasoning;
		setController(brController);
		setLogger(new SimStLogger(brController));
		
		updateTextPanes(isTutoring,isHint,isTransformation,reasoning);
		
		
		backPanel=new JPanel();
		backPanel.setOpaque(false);
		backPanel.setSize(new Dimension(200,100));
		backPanel.setBorder(new EmptyBorder(20, 20, 20, 20) );
		getContentPane().add(backPanel);

		if (isTutoring)
			setUpOptionsPanel_tutoring();
		else
			setUpOptionsPanel_noTutoring();
			
		
		setUpResponsePanel();

	
		closeButton = new JButton("Submit");
		closeButton.addActionListener((ActionListener) this);
		closeButton.setBounds(200,245,88,36);
		closeButton.setEnabled(false);
		closeButton.setFont(MED_FONT);
		backPanel.add(closeButton);		    	
		
		
		doMyBestButton = new JButton("Ok, I'll do my best.");
		doMyBestButton.addActionListener((ActionListener) this);
		doMyBestButton.setBounds(22,239,192,36);
		doMyBestButton.setFont(SMALL_FONT);
		doMyBestButton.setEnabled(true);
		
		
		quitButton = new JButton("I want to quit. I'll talk with my teacher.");
		quitButton.addActionListener((ActionListener) this);
		quitButton.setBounds(226,239,256,36);
		quitButton.setFont(SMALL_FONT);
		quitButton.setEnabled(true);
			
		
		pack();
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setModal(true);
		setVisible(true);
	}

	
	private void setUpOptionsPanel_tutoring(){
		optionsPanel = new JPanel();
		optionsPanel.setFont(this.MED_FONT);
		
		optionsPanel.setBounds(22, 6, 460, 221);
	
		group = new ButtonGroup();
		stuckOption = new JRadioButton(stuckTxtActual);
		stuckOption.setBounds(0, 26, 460, 43);
		stuckOption.setFont(MED_FONT);
		confusedOption = new JRadioButton(confusedTxtActual);
		confusedOption.setBounds(0, 61, 460, 43);
		confusedOption.setFont(MED_FONT);
		thinkingOption = new JRadioButton(thinkingTxt);
		thinkingOption.setBounds(0, 101, 460, 43);
		thinkingOption.setFont(MED_FONT);
		boredOption = new JRadioButton(boredTxt);
		boredOption.setBounds(0, 140, 460, 43);
		boredOption.setFont(MED_FONT);
		otherOption= new JRadioButton(otherTxt);
		otherOption.setBounds(0, 177, 96, 64);
		otherOption.setFont(MED_FONT);
				
		stuckOption.addActionListener(this);
		confusedOption.addActionListener(this);
		thinkingOption.addActionListener(this);
		boredOption.addActionListener(this);
		otherOption.addActionListener(this);
		backPanel.setLayout(null);
		optionsPanel.setLayout(null);
		
		optionsPanel.add(stuckOption);
		group.add(stuckOption);
		optionsPanel.add(confusedOption);
		group.add(confusedOption);
		optionsPanel.add(thinkingOption);
		group.add(thinkingOption);
		optionsPanel.add(boredOption);
		group.add(boredOption);
		optionsPanel.add(otherOption);
		group.add(otherOption);
		
		
		otherText=new JTextField();
		otherText.setBounds(92, 195, 388, 29);
		optionsPanel.add(otherText);
		otherText.setPreferredSize( new Dimension( 200, 24 ) );
		otherText.setEnabled(false);
		otherText.addFocusListener(this);

		
		JTextPane titleMsg = new JTextPane();
		titleMsg.setBackground(Color.white);
		titleMsg.setSize(365, 21);
		titleMsg.setLocation(0, 0);
		titleMsg.setEditable(false);
		titleMsg.setForeground(Color.black);
		titleMsg.setText("What's wrong, is everything ok?");
		titleMsg.setFont(MED_FONT);
		optionsPanel.add(titleMsg);
		
		backPanel.add(optionsPanel); 
		
	}
	
	public String updateReasoning(String txt){
		String result="";
		txt=txt.replace("<reasoning>", this.reasoning);	
		return result;
	}
	
	
	private String replaceName(String msg){
	return msg.replace("SimStName", this.simStName);	
	}
	
	private void setUpOptionsPanel_noTutoring(){
		optionsPanel = new JPanel();
		optionsPanel.setFont(this.MED_FONT);
		optionsPanel.setBounds(22, 6, 460, 208);
		group = new ButtonGroup();
		//stuckOption = new JRadioButton(stuckTxt_noSkill);
		//stuckOption.setBounds(0, 6, 460, 43);
		confusedOption = new JRadioButton(confusedTxtActual);
		confusedOption.setBounds(0, 26, 460, 43);
		//thinkingOption = new JRadioButton(thinkingTxt);
		//thinkingOption.setBounds(0, 80, 460, 43);
		boredOption = new JRadioButton(boredTxt);
		boredOption.setBounds(0, 61, 460, 43);
		otherOption= new JRadioButton(otherTxt);
		otherOption.setBounds(0, 101, 90, 43);
		
		boredOption.setFont(MED_FONT);
		otherOption.setFont(MED_FONT);
		confusedOption.setFont(MED_FONT);
		
		confusedOption.addActionListener(this);

		boredOption.addActionListener(this);
		otherOption.addActionListener(this);
		backPanel.setLayout(null);
		optionsPanel.setLayout(null);
		

		optionsPanel.add(confusedOption);
		group.add(confusedOption);
		optionsPanel.add(boredOption);
		group.add(boredOption);
		optionsPanel.add(otherOption);
		group.add(otherOption);
		
		
		otherText=new JTextField();
		otherText.setBounds(92, 107, 388, 29);
		optionsPanel.add(otherText);
		otherText.setPreferredSize( new Dimension( 200, 24 ) );
		otherText.setEnabled(false);
		
		JTextPane titleMsg = new JTextPane();
		titleMsg.setBackground(Color.white);
		titleMsg.setSize(365, 25);
		titleMsg.setLocation(0, 0);
		titleMsg.setEditable(false);
		titleMsg.setForeground(Color.black);
		titleMsg.setText("What's wrong, is everything ok?");
		titleMsg.setFont(MED_FONT);
		optionsPanel.add(titleMsg);
		
		backPanel.add(optionsPanel); 
		
	}
	
	
	public ImageIcon createImageIcon(String path) {
		String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+path;
		URL url = this.getClass().getResource(file);

		return new ImageIcon(url); 

	}
	
	private void setUpResponsePanel(){
		
		
		
		
		responsePanel = new JPanel();
		responsePanel.setBounds(22, 6, 460, 381);
		responsePanel.setLayout(null);
		responsePanel.setBorder(new EmptyBorder(20, 20, 20, 20) );
		responsePanel.setBackground(Color.white);
		responseMsg = new JTextPane();
		responseMsg.setLocation(132, 6);
		responseMsg.setEditable(false);
		responseMsg.setSize(322,185);
		responseMsg.setForeground(Color.black);
		responseMsg.setOpaque(false);
		responseMsg.setFont(MED_FONT);
		
		StyledDocument doc1 = responseMsg.getStyledDocument();
		SimpleAttributeSet center1 = new SimpleAttributeSet();
		StyleConstants.setLineSpacing(center1, 0.6f);
		doc1.setParagraphAttributes(0, doc1.getLength(), center1, false);	
		responseMsg.setText(this.thinkingResponse);
		responsePanel.add(responseMsg);
			
		logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.APLUS_INACTIVE_WINDOW_RESPONSE, responseMsg.getText());
			
		
		ImageIcon splashIcon=createImageIcon(MrWilliams);
		JLabel metatutorLabel = new JLabel(createImageIcon(MrWilliams));
		metatutorLabel.setLayout(new BorderLayout());
		metatutorLabel.setBounds(0,0,splashIcon.getIconWidth(),splashIcon.getIconHeight());
		
		
		responsePanel.add(metatutorLabel);
		
		//just for testing purposes
		//backPanel.add(responsePanel); 
		
	}
	

	private void updateResponce(String response){	
		if (boredOption.isSelected()){
			backPanel.add(doMyBestButton);	
			backPanel.add(quitButton);	
			backPanel.remove(closeButton);
		}
		else closeButton.setText("Close");
		
		backPanel.remove(optionsPanel);
		//if (this.simStName!=null)
			responseMsg.setText(this.replaceName(response));	
		
		backPanel.add(responsePanel);
		backPanel.repaint();
		pack();		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if (stuckOption==arg0.getSource() ){
			logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.APLUS_INACTIVE_WINDOW_SELECTION, stuckOption.getText());
			
			otherText.setText("");
			otherText.setEnabled(false);
			response=this.stuckResponseTxtActual;
			closeButton.setEnabled(true);
			//updateResponce(this.stuckResponse);		
		}
		else if (confusedOption==arg0.getSource()){
			logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.APLUS_INACTIVE_WINDOW_SELECTION, confusedOption.getText());
			otherText.setText("");
			otherText.setEnabled(false);
			response=confusedResponseTxtActual;
			closeButton.setEnabled(true);
		}
		else if (thinkingOption==arg0.getSource()){
			logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.APLUS_INACTIVE_WINDOW_SELECTION, thinkingOption.getText());
			otherText.setText("");
			otherText.setEnabled(false);
			response=thikingResponseTxtActual;
			closeButton.setEnabled(true);
			//updateResponce(this.thinkingResponse);
		}
		else if (boredOption==arg0.getSource()){
			logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.APLUS_INACTIVE_WINDOW_SELECTION, boredOption.getText());
			otherText.setText("");
			otherText.setEnabled(false);
			response=this.boredResponse;
			closeButton.setEnabled(true);
			//updateResponce(this.boredResponse);
		}
		else if (otherOption==arg0.getSource()){
			otherText.setText("");
			otherText.setEnabled(true);
			otherText.setForeground(Color.gray);
			otherText.setText("Enter your reasoning here");
			closeButton.setEnabled(true);
			response=this.otherResponse;
			
		}
		else if (doMyBestButton==arg0.getSource()){
			logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.APLUS_INACTIVE_WINDOW_CLOSE, "");
			
			setVisible(false);
		}
		else if (this.quitButton==arg0.getSource()){
			

			logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.APLUS_INACTIVE_WINDOW_CLOSE, "");
			logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.QUIT_WINDOW_START, "");
			
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			double width = screenSize.getWidth();
			double height = screenSize.getHeight();
		
			final JDialog bright=new JDialog();
			bright.setUndecorated(true);
			bright.setPreferredSize(new Dimension(new Integer((int) width),new Integer((int) height)));
			bright.setLocation(0,0);
			//bright.setLocationRelativeTo(null);

			JLayeredPane layers = new JLayeredPane();
			layers.setPreferredSize(new Dimension(new Integer((int) width),new Integer((int) height)));
				
			ImageIcon brightIcon=createImageIcon(HappyFace);
			JLabel brightLabel = new JLabel(createImageIcon(HappyFace));
			brightLabel.setLayout(new BorderLayout());
			brightLabel.setBounds(0,0,brightIcon.getIconWidth(),brightIcon.getIconHeight());
			

			layers.add(brightLabel,new Integer(200));
			
			
			
			JTextPane titleMsg = new JTextPane();
			titleMsg.setBackground(Color.white);
			titleMsg.setSize(365, 21);
			titleMsg.setEditable(false);
			titleMsg.setForeground(Color.black);
			titleMsg.setText("Please wait quietly for your teacher");
			titleMsg.setOpaque(false);
			titleMsg.setBounds(brightIcon.getIconWidth()/2-200,brightIcon.getIconHeight()/2,1100,200);
			titleMsg.setFont(this.HUGE_FONT);
			layers.add(titleMsg,new Integer(330));		
			
			
			JButton invisibleClose=new JButton();
			invisibleClose.setOpaque(true);
			invisibleClose.setContentAreaFilled(false);
			invisibleClose.setBorderPainted(false);
			
			
			invisibleClose.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.QUIT_WINDOW_CLOSE, "");
						bright.setVisible(false);				
				} 		
			});
			
			invisibleClose.setBounds(0,0,100,100);
			invisibleClose.setEnabled(true);
			
			layers.add(invisibleClose,new Integer(300));
			
			
			bright.getContentPane().add(layers);
			
			bright.pack();
			bright.setAlwaysOnTop(true);
			bright.setVisible(true);
			
			setVisible(false);
			
		}
		else if (closeButton==arg0.getSource()){
			 
			String selection=null;
			 for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
		            AbstractButton button = buttons.nextElement();
		            if (button.isSelected()) {
		                selection=button.getText();
		            }
		     }
			 
			 if (selection!=null){
				 String txt=closeButton.getText();
				 if (txt.equals("Submit")) {
					 	if (selection.equals(otherTxt)){ 		
					 		logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.APLUS_INACTIVE_WINDOW_SELECTION, boredOption.getText()+otherText.getText());
					 	}
					 	updateResponce(response);
				 }
				 else{
					 logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.APLUS_INACTIVE_WINDOW_CLOSE, "");
					 setVisible(false);
				 }
			 }
			 else{
				// errorMsg.setText("Please select an option!");
			 }
		}		
		
	}


	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		otherText.setText("");
		otherText.setForeground(Color.black);
	}


	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}