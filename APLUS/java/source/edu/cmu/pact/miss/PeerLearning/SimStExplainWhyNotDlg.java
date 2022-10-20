package edu.cmu.pact.miss.PeerLearning;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.awt.Color;

import javax.swing.JLabel;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.miss.Instruction;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.PeerLearning.SimStPLE.TextEntryListener;
import pact.CommWidgets.JCommButton;
import pact.CommWidgets.JCommComboBox;
import pact.CommWidgets.JCommLabel;
import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommTextArea;
import pact.CommWidgets.JCommTextField;
import pact.CommWidgets.JCommTable.TableCell;
import pact.CommWidgets.JCommTable.TableExpressionCell;

import java.awt.Font;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


public class SimStExplainWhyNotDlg extends JDialog implements ActionListener,FocusListener {

	//private final JPanel contentPanel = new JPanel();
	
	JComponent pastInterface;
	JComponent nowInterface;
	JComponent studentInterface;
	private JLabel lblIDidThis;
	private JLabel label;
	void setStudentInterface(JComponent studentInterface){this.studentInterface=studentInterface;}
	JComponent  getStudentInterface(){return this.studentInterface;}
	public static int BORDER_NONE=0;
	//public static int BORDER_PAST=1;
	//public static int BORDER_NOW=2;
	public static Color BORDER_PAST=Color.blue;
	public static Color BORDER_NOW=Color.red;
	public static String DEFAULT_ANSWER="Enter your explanation here and click submit to close this window";
	
	BR_Controller brController;
	void setController(BR_Controller brController){this.brController=brController;}
	BR_Controller getController(){return this.brController;}
	JTextField selfExplanationText;
	JTextPane selfExplanationSimStQuestion;
	JButton submitButton;
	
	public SimStExplainWhyNotDlg(JComponent parent,TutorController brController,Sai sai,Instruction inst,String question) {
	
		BR_Controller temp = (BR_Controller)brController;
		setController(temp);
		setStudentInterface(brController.getTutorPanel());
		
		Dimension prefs=brController.getTutorPanel().getPreferredSize();
		
		//this.setUndecorated(true);
		setBounds(600, 100, prefs.width*2+40, prefs.height+220);

		setLocationRelativeTo(parent);
		//setLocationRelativeTo(null);
		//this.setLocation(680, 500);
		
		getContentPane().setBackground( AplusPlatform.studentColor );
		
		
		getRootPane().setBorder( BorderFactory.createLineBorder(Color.black,2) );
		
		getContentPane().setLayout(new BorderLayout());
		this.setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		/*create the new interfaces to add to the dialog*/
		try {
			pastInterface = getStudentInterface().getClass().newInstance();
			nowInterface = getStudentInterface().getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		JPanel backPanel=new JPanel();
		backPanel.setOpaque(false);
		getContentPane().add(backPanel);


		
		/*Add the new interfaces to the window*/
		addSelfExplanationQuestion(backPanel,question); // responsible for showing chatbox in the popup.
		addPastInterface(backPanel);
		addNowInterface(backPanel);
		addSelfExplanationResponseTextArea(backPanel); // responsible for showing textbox in the popup.
	    
		setResizable(false);
		
		/*populate the two interfaces and show the window*/
		if (sai!=null && inst!=null){
			populateInterfaceFromSai(nowInterface,sai,BORDER_NOW,true);	
			populatePastInterface(inst);
			populateNowInterface(getController());
			setVisible(true);
		}

		
		
	}
	

	public SimStExplainWhyNotDlg(JComponent parent,TutorController brController,Sai sai,Instruction inst,String question, boolean isCTI) {
	
		BR_Controller temp = (BR_Controller)brController;
		setController(temp);
		setStudentInterface(brController.getTutorPanel());
		
		Dimension prefs=brController.getTutorPanel().getPreferredSize();
		
		//this.setUndecorated(true);
		setBounds(600, 100, prefs.width*2+40, prefs.height+100);

		setLocationRelativeTo(parent);
		//setLocationRelativeTo(null);
		//this.setLocation(680, 500);
		
		getContentPane().setBackground( AplusPlatform.studentColor );
		
		
		getRootPane().setBorder( BorderFactory.createLineBorder(Color.black,2) );
		
		getContentPane().setLayout(new BorderLayout());
		this.setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		/*create the new interfaces to add to the dialog*/
		try {
			pastInterface = getStudentInterface().getClass().newInstance();
			nowInterface = getStudentInterface().getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		JPanel backPanel=new JPanel();
		backPanel.setOpaque(false);
		getContentPane().add(backPanel);


		
		/*Add the new interfaces to the window*/
		//addSelfExplanationQuestion(backPanel,question); // responsible for showing chatbox in the popup.
		addPastInterface(backPanel);
		addNowInterface(backPanel);
		//addSelfExplanationResponseTextArea(backPanel); // responsible for showing textbox in the popup.
	    
		setResizable(false);
		
		/*populate the two interfaces and show the window*/
		if (sai!=null && inst!=null){
			populateInterfaceFromSai(nowInterface,sai,BORDER_NOW,true);	
			populatePastInterface(inst);
			populateNowInterface(getController());
			setVisible(true);
		}

		
		
	}
	

	void addSelfExplanationQuestion(JPanel backPanel,String question){
		
		
		JPanel selfExplanationPanel = new JPanel();

		GridBagConstraints layout = new GridBagConstraints();
		layout.gridy = 0;

		selfExplanationPanel.setLayout(new GridBagLayout());
			
		question=brController.getMissController().getSimStPLE().getSimStName() + ": "+question;
			
		/*add the self explanation */	
		selfExplanationSimStQuestion=new JTextPane();
		selfExplanationSimStQuestion.setBounds(92, 107, 388, 29);
		selfExplanationSimStQuestion.setBorder(BorderFactory.createLineBorder(Color.black));
		selfExplanationSimStQuestion.setPreferredSize( new Dimension( 880, 105 ) );
	//	selfExplanationSimStQuestion.setPreferredSize( new Dimension( 680, 24 ) );
		selfExplanationSimStQuestion.setEditable(false);
		selfExplanationSimStQuestion.setContentType("text/html");		
		selfExplanationSimStQuestion.setText(formatQuestion(question,brController.getMissController().getSimStPLE().getSimStName()));
		
		selfExplanationPanel.add(selfExplanationSimStQuestion);
	    
		
		backPanel.add(selfExplanationPanel);
		
	}
	
	String clearFormatedQuestion(String question){
		String formatedString=question.replace("[fontblue]", "");
		formatedString=formatedString.replace("[fontred]", "");
		formatedString=formatedString.replace("[fontend]", "");
		return formatedString;
	}
	
	
	String formatQuestion(String question,String name){
		String previousMessage=brController.getMissController().getSimStPLE().getSsInteractiveLearning().previousMessageGiven;
		String formatedString="<html><body style=\"font-family: Serif; font-size:13px; padding: 0.3cm 0.2cm 0.2cm 0.4cm;\">"+name+": " + previousMessage + " <br>"+name+": Hm, let me think...<br>"+question+"</body></html>";
  	   
		//String formatedString="<html><body style=\"font-family: Serif; font-size:13px; padding: 0.3cm 0.2cm 0.2cm 0.4cm;\">"+name+": Hm, let me think...<br>"+question+"</body></html>";
		formatedString=formatedString.replace("[fontblue]", "<font color=\"blue\">");
		formatedString=formatedString.replace("[fontred]", "<font color=\"red\">");
		formatedString=formatedString.replace("[fontend]", "</font>");
		return formatedString;
	}
	
	
	void addSelfExplanationResponseTextArea(JPanel backPanel){
		
		
		JPanel selfExplanationPanel = new JPanel();

		GridBagConstraints layout = new GridBagConstraints();
		layout.gridy = 0;

		selfExplanationPanel.setLayout(new GridBagLayout());
		
		
		/*add the self explanation */	
		selfExplanationText=new JTextField();
		selfExplanationText.setBounds(92, 107, 388, 29);
		selfExplanationText.setPreferredSize( new Dimension( 780, 24 ) );
		selfExplanationText.setEnabled(true);
    	//	selfExplanationText.requestFocus();
		selfExplanationText.setForeground(Color.gray);
		selfExplanationText.setText(DEFAULT_ANSWER);
		//selfExplanationText.addActionListener((ActionListener) this);
		selfExplanationText.addFocusListener(this);
	    selfExplanationPanel.add(selfExplanationText);
	    
	    
	    LinkedBlockingQueue<String> bucket = new LinkedBlockingQueue<String>();

		submitButton = new JButton("Submit");
		//submitButton.addActionListener((ActionListener) this);
		
		//TextEntryListener textEntryList=brController.getMissController().getSimStPLE().new TextEntryListener(bucket);
		//submitButton.addActionListener(textEntryList);
		submitButton.setBounds(200,245,88,36);
		submitButton.setEnabled(true);
		//submitButton.setFont(MED_FONT);
		selfExplanationPanel.add(submitButton);	
		
		backPanel.add(selfExplanationPanel);
		
		
	}
	
	
	
	  public String giveMessageSelectableResponse(String message, List<String> selections,String input)
	    {
	    	LinkedBlockingQueue<String> bucket = new LinkedBlockingQueue<String>();
	    	
	    	brController.getMissController().getSimStPLE().setAvatarAsking();
	    	
	    	
	    
	        brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().appendSpeech("Hm, let me think...",brController.getMissController().getSimStPLE().getSimStName());
	   
	    	
	    //	brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().showTextResponseOptions(true,selections);
	
	    	for(ActionListener al:submitButton.getActionListeners()/*brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getTextResponseSubmitButton().getActionListeners()*/)
	    	{
	    		//brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getTextResponseSubmitButton().removeActionListener(al);
	       		
	    		submitButton.removeActionListener(al);
	    	}
	    	
	    	//brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getTextResponseSubmitButton().addActionListener(new TextEntryListener(bucket));
	    	submitButton.addActionListener(new TextEntryListener(bucket));
	    	
	    	
			String response = "";

			brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().scrollPaneToBottom();
	    	
			try {
				response = bucket.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	
			
			brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().appendSpeech(clearFormatedQuestion(message), brController.getMissController().getSimStPLE().getSimStName());
			
			if(response.length() > 0)
				brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().appendSpeech(response, "Me");
			
			
			
			//getSimStPeerTutoringPlatform().getTextResponse().setText("");
			
			brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getTextResponse().setSelectedItem("");

		//	brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().showTextResponseOptions(false,null);
			brController.getMissController().getSimStPLE().setAvatarNormal();
	    	
			return response;
	    	

	    }
	  
	/**
	 * Method responsible for filling the "past" interface based on an instruction. It retrieves 
	 * the previous instructions in the list. Highlights only the first instruction.
	 * @param inst
	 */
	void populatePastInterface(Instruction inst){
	
	 	Instruction prev=inst;	
	 	boolean showBorder=true;
	 	
	 	//trace.err("Instruction used to populate is " + inst);
   		while (prev!=null){
			Color border=showBorder? BORDER_PAST : null;
   			populateInterfaceFromInstruction(this.pastInterface,prev,border);
   			showBorder=false;
   		   	String previousID=prev.getPreviousID();
   	
   		   	prev=this.getController().getMissController().getSimSt().getInstructionByID(previousID);
   		   //	trace.err("previous for next iteration is " + previousID  +"  which is  "+  prev);
   		}
   		
	}
	
	/**
	 * Method that populates the now interface, based on the current graph
	 * @param brController
	 */
	void populateNowInterface(BR_Controller brController){
		
		ProblemNode currentNode=brController.getCurrentNode();
		while (currentNode!=null && currentNode.getInDegree()>0){
				if (currentNode.getInDegree() > 0){			
					ProblemEdge tmp=currentNode.getIncomingEdges().get(0);
					populateInterfaceFromSai(nowInterface,tmp.getSai(),null,false);		
					currentNode=tmp.getSource();		
				}
		}
				
	}

	
	/**
	 * Method that fills a tutoring interface based on an instruction.
	 * @param tutorInterface
	 * @param inst
	 * @param borderType
	 */
	void populateInterfaceFromInstruction(JComponent tutorInterface, Instruction inst,Color borderColor){
		Vector foas=inst.getFocusOfAttention();
		trace.err("foas " + foas );
		for (int i=0;i<foas.size()  ;i++){
			String str=(String) foas.get(i);
			String[] foa=str.split("\\|");	//instruction foa is of the form WME|name(selection)|value(input)
			String selection=foa[1];
			String input=foa[2];
			
			if (selection!=null && input!=null){
				getController().getMissController().getSimStPLE().fillSelection(tutorInterface,selection,input,borderColor,false);
			}
		}
		

	}
	
	
	/**
	 * 
	 * @param tutorInterface
	 * @param sai
	 * @param border
	 * @param highlight
	 */
	void populateInterfaceFromSai(JComponent tutorInterface, Sai sai,Color borderColor,boolean highlight){

		/*get the foas for the current SAI*/
		Vector foas=getController().getMissController().getSimSt().getFoaGetter().foaGetter(getController(), sai.getS(), sai.getA(), sai.getI(), null);
		
		/*fill the values of the foas*/
		for (int i=0;i<foas.size();i++){
			String foaInput="";
			String foaName="";
			if (foas.elementAt(i) instanceof JCommComboBox ){
				foaInput = (String) ((JCommComboBox)foas.elementAt(i)).getValue();
				foaName = (String) ((JCommComboBox)foas.elementAt(i)).getCommName();
			}
			else if (foas.elementAt(i) instanceof JCommTextField  ){
				foaInput = ((JCommTextField )foas.elementAt(i)).getText();
				foaName = (String) ((JCommTextField)foas.elementAt(i)).getCommName();
			}
			else {
				foaInput = ((TableExpressionCell)foas.elementAt(i)).getText();
				foaName = (String) ((TableExpressionCell)foas.elementAt(i)).getCommName();
			}
	
			getController().getMissController().getSimStPLE().fillSelection(tutorInterface,foaName,foaInput,borderColor,false);
		}
			/*fill the value of the Sai*/
			getController().getMissController().getSimStPLE().fillSelection(tutorInterface,sai.getS(),sai.getI(),borderColor,highlight);	
	
	}

	
	/**
	 * Method that adds the past tutoring interface on the back panel
	 * @param backPanel
	 */
	void addPastInterface(JPanel backPanel){

		JPanel pastPanel = new JPanel();

		GridBagConstraints layout = new GridBagConstraints();
		layout.gridy = 0;

		pastPanel.setLayout(new GridBagLayout());

		lblIDidThis = new JLabel("I did this in the past:   (Figure A)");
		lblIDidThis.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		lblIDidThis.setForeground(Color.BLUE);
		pastPanel.add(lblIDidThis,layout);

		layout.gridx = GridBagConstraints.RELATIVE;
		layout.gridy = 2;


		pastInterface.setPreferredSize(getStudentInterface().getPreferredSize());
		pastInterface.setBackground(AplusPlatform.studentColor);
		SimStPLE.setComponentEnabled(false, pastInterface);
		pastInterface.setAlignmentX(CENTER_ALIGNMENT);
		GridBagConstraints ipConst = new GridBagConstraints();
		ipConst.gridx = 0;
		ipConst.gridy = 0;
		ipConst.gridwidth = 1;
		ipConst.gridheight = 2;
		ipConst.weightx = .75;
		ipConst.weighty = .75;
		ipConst.fill = GridBagConstraints.BOTH;
		ipConst.anchor = GridBagConstraints.PAGE_START;
		pastPanel.add(pastInterface, layout);

		layout.gridx = GridBagConstraints.RELATIVE;
		layout.gridy = 3;

		//JLabel labelA = new JLabel("Figure A");
		//labelA.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		//labelA.setForeground(Color.BLUE);
		//pastPanel.add(labelA,layout);

		backPanel.add(pastPanel);

	}
	
	/**
	 * Method that adds the now tutoring interface on the back panel
	 * @param backPanel
	 */
	void addNowInterface(JPanel backPanel){
		
		JPanel nowPanel = new JPanel();
		
		GridBagConstraints layout = new GridBagConstraints();
		layout.gridy = 0;
		
		nowPanel.setLayout(new GridBagLayout());
		
		label = new JLabel("Now I am thinking about this:   (Figure B)");
		label.setForeground(Color.RED);
		label.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		nowPanel.add(label,layout);
		
		layout.gridx = GridBagConstraints.RELATIVE;
		layout.gridy = 2;
		
		nowInterface.setPreferredSize(getStudentInterface().getPreferredSize());
		nowInterface.setBackground(AplusPlatform.studentColor);
		SimStPLE.setComponentEnabled(false, nowInterface);
		nowInterface.setAlignmentX(CENTER_ALIGNMENT);
		GridBagConstraints ipConst = new GridBagConstraints();
		ipConst.gridx = 0;
		ipConst.gridy = 0;
		ipConst.gridwidth = 1;
		ipConst.gridheight = 2;
		ipConst.weightx = .75;
		ipConst.weighty = .75;
		ipConst.fill = GridBagConstraints.BOTH;
		ipConst.anchor = GridBagConstraints.PAGE_START;
		nowPanel.add(nowInterface, layout);
		
		
		layout.gridx = GridBagConstraints.RELATIVE;
		layout.gridy = 3;
		
		//JLabel labelB = new JLabel("Figure B");
		//labelB.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		//labelB.setForeground(Color.RED);
		//nowPanel.add(labelB,layout);
		
		backPanel.add(nowPanel);

	}
	

	
	class TextEntryListener implements ActionListener
    {

    	BlockingQueue<String> bucket;
    	
    	TextEntryListener(BlockingQueue<String> bucket)
    	{		
    		this.bucket = bucket;
    	}
    	
		@Override
		public void actionPerformed(ActionEvent e) 
		{ 	
	
			//JOptionPane.showMessageDialog(null, "22action" + e.getSource());
			
			String response = "";
			if(e.getSource() instanceof JTextField)
			{
				response = ((JTextField) e.getSource()).getText();
			}
			if(e.getSource() instanceof JComboBox)
			{
				if(((JComboBox)	e.getSource()).getSelectedItem() == null)
					return;
				if(((JComboBox)	e.getSource()).getSelectedItem().equals(SimStPLE.SELECT_OPTION))
					response = "";
				else
					response = (String)((JComboBox) e.getSource()).getSelectedItem();
			}
			if(e.getSource() ==  submitButton)
			{	
				
				if (selfExplanationText!=null && selfExplanationText.getText().length()>0 && !selfExplanationText.getText().equals(DEFAULT_ANSWER))
					response=selfExplanationText.getText();
				
			}
			try {
		
				bucket.put(response);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			submitButton.removeActionListener(this);
			
		}
    	
    }



	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		JOptionPane.showMessageDialog(null, e.getSource());
		
		
		
	}
	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
		//JOptionPane.showMessageDialog(null, e.getSource());
		selfExplanationText.setText("");
		selfExplanationText.setForeground(Color.black);
	}
	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
}
