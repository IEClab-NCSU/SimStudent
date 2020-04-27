package edu.cmu.pact.miss.PeerLearning;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dialog;
import java.net.URL;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLDocument;

import pact.CommWidgets.JCommWidget;
import edu.cmu.pact.miss.MetaTutor.APlusHintDialog;

public class SimStMessageDialog extends JDialog implements ActionListener{
	
	
		/** Label on the JButton to close the HintWindow in focus */
		private static final String OK = " OK ";
		private static final String EDIT_MY_SOLUTION = "Edit my Solution";
		public static final String REVIEW_EXAMPLES = "Review Examples";
		private static final String START_PRACTICE = "Start the Practice";
		private static final String START_SOLVING = "Proceed to Next Section";
		private static final String PRACTICE_EQUATIONS = "Practice Equations";
		private static final String TRY_NEXT_QUIZ = "Proceed to Next Section";
		public static final int SHOW_NORMAL_APPEARANCE=0;
		public static final int SHOW_PRACTICE_EXAMPLES=1;
		public static final int SHOW_TRHREE_BUTTONS=2;
		public static final int SHOW_QUIZ_EXAMPLES=3;
		public static final int SHOW_PRACTICE_EXAMPLES_COGTUTOR=4;
		private static final String DONT_SHOW_AGAIN="Don't show this again";
		private static final String okText = "<html><centre>I would like to edit my solution and resubmit.</centre></html>";
		private static final String practiceText = "<html><centre>I would like to practice equations.</centre></html>";
		private static final String reviewText = "<html><centre>I would like to review examples.</centre></html>";
		
		/** Message to display */
		String message;
		
		private JPanel okCancelPanel = new JPanel();
		private JPanel dontShowPanel = new JPanel();
		private JPanel bottomOptionsPanel = new JPanel();
		
		
		/**	Message display window, with support for HTML format message */
		private JEditorPane hintsJEditorPane;
		protected JScrollPane hintsJEditorScrollPane;
		
		private APlusHintDialog.HintJButton okJButton = new APlusHintDialog.HintJButton(OK);
		
		
		private APlusHintDialog.HintJButton utilityJButton1 = new APlusHintDialog.HintJButton(EDIT_MY_SOLUTION);
		private APlusHintDialog.HintJButton utilityJButton2 = new APlusHintDialog.HintJButton(EDIT_MY_SOLUTION);

		
		/**	Default font family  */
		String fontFamily = JCommWidget.getDefaultFont().getFamily();
		
		/**	Default font size  */
		int fontSize = JCommWidget.getDefaultFont().getSize();
		
		Container contentPane = getContentPane();
		
		SimStLogger logger;
		
		int depth = 0;
		long openTime = -1;
		long hintStartTime = -1;
		

		/**	 */
		private boolean visibleFlag;
		JCheckBox dontShowThisAgain;
		boolean showCheckbox=false;
		
		boolean showTwoButtons=false;
		
		String buttonText=null; 
		
		int appearanceType=SHOW_NORMAL_APPEARANCE;

			
/***
 * Main Constructors
 * */
		
		public SimStMessageDialog(Frame parent, SimStLogger log,String message,boolean showCheckbox, boolean showTwoButtons,String buttonText){
			this(parent,log,message,showCheckbox,SHOW_NORMAL_APPEARANCE,showTwoButtons,buttonText);	
		}

		public SimStMessageDialog(Frame parent, SimStLogger log,String message,boolean showCheckbox, int appearanceType){
			this(parent,log,message,showCheckbox,appearanceType,false,null);
			
		}
		

		public SimStMessageDialog(Frame parent, SimStLogger log,String message,boolean showCheckbox){
			this(parent,log,message,showCheckbox,SHOW_NORMAL_APPEARANCE,false,null);
		}
		

		public SimStMessageDialog(Frame parent, SimStLogger log,String message){
			this(parent,log,message,false,SHOW_NORMAL_APPEARANCE,false,null);		
		}
		
		public SimStMessageDialog(Frame parent, SimStLogger log){
			this(parent,log,null,false,SHOW_NORMAL_APPEARANCE,false,null);		

		}
		
		/**
		 * Main constructor 
		 * @param parent 			
		 * @param log				
		 * @param message			
		 * @param showCheckBox  	
		 * @param appearanceType     
		 * @param ShowTwoButtons	
		 * @param buttonText		the text for the 2nd button.
		 */
		public SimStMessageDialog(Frame parent, SimStLogger log,String message, boolean showCheckBox ,int appearanceType,boolean ShowTwoButton,String buttonText){
			super(parent, true); 
			logger = log;
			this.showCheckbox=showCheckBox;
			this.appearanceType=appearanceType;
			this.showTwoButtons=ShowTwoButton;
			this.buttonText=buttonText;
			init(message);
			
		}	
		
	
		/**
		 * @param hintMessagesManager
		 */
		private void init(String message) {
		
			setTitle(" Mr. Williams says    	");
			
			
			setLocation(new java.awt.Point(400,200));
			setSize(400,260);
			setResizable(true);
			setModalityType(Dialog.ModalityType.APPLICATION_MODAL); //  blocks all top-level windows from the same java application
			setAlwaysOnTop(true);

			if (message!=null)
			setLocationRelativeTo(null);


			if(this.appearanceType == SHOW_TRHREE_BUTTONS) {
				
				setSize(350,312);
				setTitle(" What would I do next ?");
				contentPane.setLayout(new BorderLayout());
					
				JPanel buttonPanel = new JPanel(new GridLayout(3,1,0,20));
				contentPane.add(buttonPanel, BorderLayout.CENTER);
				Border border = buttonPanel.getBorder();
				Border margin = new EmptyBorder(35,50,50,50);
				buttonPanel.setBorder(new CompoundBorder(border, margin));
				
				okJButton.setText(okText);
				buttonPanel.add(okJButton);
				
				utilityJButton1.setText(practiceText);
				buttonPanel.add(utilityJButton1);

				
	        	utilityJButton2.setText(reviewText);
				buttonPanel.add(utilityJButton2);

				
				okJButton.addActionListener(this);
		        utilityJButton1.addActionListener(this);
		        utilityJButton2.addActionListener(this);
		        
				setVisible(true);
				
				
				
			}
				
			else {
				contentPane.setLayout(new BorderLayout());
				
				hintsJEditorPane = new JEditorPane();
				hintsJEditorPane.setName("hintsJEditorPane");
				hintsJEditorPane.setContentType("text/html");
				hintsJEditorPane.setText("<html><br><br><br><br></html>");
				hintsJEditorPane.setAutoscrolls(true);
				hintsJEditorPane.setEditable(false);
				hintsJEditorPane.setFocusable(false);
				hintsJEditorPane.setMargin(new Insets(20,20,20,20));

				
				
				hintsJEditorScrollPane = new JScrollPane(hintsJEditorPane);
				contentPane.add(hintsJEditorPane, BorderLayout.CENTER);
				bottomOptionsPanel.setLayout(new BorderLayout());
				contentPane.add(bottomOptionsPanel, BorderLayout.SOUTH);
		        okCancelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		        
		        if (!this.showCheckbox)
		        	okCancelPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
		        
		        okJButton.setBackground(new Color(209,217,225)); 
		        utilityJButton1.setBackground(new Color(209,217,225));   
		        utilityJButton2.setBackground(new Color(209,217,225)); 	        
		            
		        if (this.appearanceType==SHOW_PRACTICE_EXAMPLES){
		        	okJButton.setText(START_PRACTICE);
		        	utilityJButton1.setText(REVIEW_EXAMPLES);
			        okCancelPanel.add(okJButton);
			        okCancelPanel.add(utilityJButton1);
		        }
		        else if (this.appearanceType==SHOW_PRACTICE_EXAMPLES_COGTUTOR){
		        	okJButton.setText(START_SOLVING);
		        	utilityJButton1.setText(REVIEW_EXAMPLES);
			        okCancelPanel.add(okJButton);
			        okCancelPanel.add(utilityJButton1);
		        }
		        else if (this.appearanceType==SHOW_QUIZ_EXAMPLES){
		        	okJButton.setText(TRY_NEXT_QUIZ);
		        	utilityJButton1.setText(REVIEW_EXAMPLES);
			        okCancelPanel.add(okJButton);
			        okCancelPanel.add(utilityJButton1);
		        }
		        else {
		        	if (this.buttonText!=null)
		        		okJButton.setText(this.buttonText);
			        okCancelPanel.add(okJButton);

		        }
		        	
		        
		        
		        bottomOptionsPanel.add(okCancelPanel,BorderLayout.CENTER);
		        
				if (this.showCheckbox){
					dontShowThisAgain = new JCheckBox(DONT_SHOW_AGAIN);
					dontShowThisAgain.setFont(AplusPlatform.MED_FONT);
					dontShowThisAgain.setBackground(Color.WHITE);
					dontShowThisAgain.addActionListener(this);
			        dontShowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				    dontShowPanel.add(dontShowThisAgain);
			        bottomOptionsPanel.add(dontShowPanel,BorderLayout.SOUTH);
				}
					
				
				okJButton.addActionListener(this);
		        utilityJButton1.addActionListener(this);
		        utilityJButton2.addActionListener(this);
		        
		        
		        if (message!=null){
		        	hintsJEditorPane.setText(message);
		        	
		        	Font font=AplusPlatform.MED_FONT;// new Font("Comic Sans MS", Font.PLAIN,34);
		        	 String bodyRule = "body { font-family: " + font.getFamily() + "; " +
		        	            "font-size: " + font.getSize() + "pt; line-height: 120%; }";
		        	    ((HTMLDocument)hintsJEditorPane.getDocument()).getStyleSheet().addRule(bodyRule);
		        	    
		 	        setVisible(true);
		        }
			}
				

	        
	        
	        addWindowListener(new java.awt.event.WindowAdapter() {
	        	public void windowClosing(java.awt.event.WindowEvent e) {
	        		long endTime = (new Date()).getTime();
	        		System.out.println(" Open Time : "+openTime+"  end Time : "+endTime);
	                int duration = (int) ((endTime - openTime)/1000);
	                String leavingMessage = hintsJEditorPane.getText();
	        		leavingMessage = leavingMessage.replaceAll("\\<.*?>","");
	                int durationHint = (int) ((endTime - hintStartTime)/1000);
	        		
	                logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_LEFT_HINT_ACTION, "", ""+depth, "", durationHint, leavingMessage);
	    			logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL,SimStLogger.METATUTOR_CLOSE_HINT_ACTION, "", ""+depth, "", duration);
	    			
	        		visibleFlag = false;
	        		setVisible(false);
	        	}
	        });
	        
	       
		}

		public void showMessage(String message) {
			
			hintsJEditorPane.setText("");
			
			if(message == null){
				java.awt.EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						setVisible(false);					
					}
				});
				return;
			}
			
			hintsJEditorPane.setFont(JCommWidget.getDefaultFont());
			hintsJEditorPane.setText(message);
			
			if(message.trim().equals(""))
			{
				visibleFlag = false;
				java.awt.EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						setVisible(visibleFlag);					
					}
				});
			} else {
				//visibleFlag = true;
				//setVisible(true);
				// Show the dialog using the event-dispatch thread
				java.awt.EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						setVisible(true);
						validate();
					}
				});
			}
		}
		

		@Override
		public void actionPerformed(ActionEvent ae) {

			//JButton selectedButton = (JButton) ae.getSource();
			
			//System.out.println(" Action Listener called : ");
			if (ae.getSource() instanceof JCheckBox){
				logger.brController.getMissController().getSimStPLE().setShowPopupAtExamples(!dontShowThisAgain.isSelected());
				return;
			}
			
			JButton selectedButton = (JButton) ae.getSource();

			if (selectedButton.getText().equals(REVIEW_EXAMPLES))
				logger.brController.getMissController().getSimStPLE().setReviewExamplesAfterFail(true);
			else 
				logger.brController.getMissController().getSimStPLE().setReviewExamplesAfterFail(false);

			
			String leavingMessage = "" ;
			if(this.appearanceType == SHOW_TRHREE_BUTTONS)
				leavingMessage = selectedButton.getText();
			else
				leavingMessage = hintsJEditorPane.getText();
			leavingMessage = leavingMessage.replaceAll("\\<.*?>","");
			long endTime = (new Date()).getTime();
	        int durationHint = (int) (endTime - hintStartTime);
			
			if(selectedButton == okJButton || selectedButton == utilityJButton1 || selectedButton == utilityJButton2) {
	            int duration = (int) (endTime - openTime);				
				visibleFlag = false;
				setVisible(false);
				
				if ((selectedButton.getText().equals(REVIEW_EXAMPLES) && this.appearanceType==SHOW_PRACTICE_EXAMPLES) || selectedButton.getText().equals(reviewText))
					logger.brController.getMissController().getSimStPLE().getSsCognitiveTutor().reviewExampleSection();
				else if (selectedButton.getText().equals(PRACTICE_EQUATIONS) || selectedButton.getText().equals(practiceText))
					logger.brController.getMissController().getSimStPLE().getSsCognitiveTutor().goToPractice();

					
				
				return;
			}
			
			showMessage(message);
			return;
		}

}
