package edu.cmu.pact.miss.MetaTutor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pact.CommWidgets.JCommWidget;
import pact.CommWidgets.event.StudentActionEvent;
import pact.CommWidgets.event.StudentActionListener;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.view.AvoidsBackGrading;
import edu.cmu.pact.miss.PeerLearning.AplusPlatform;
import edu.cmu.pact.miss.PeerLearning.AplusSpotlight;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.PeerLearning.SimStPeerTutoringPlatform;
import edu.cmu.pact.miss.PeerLearning.SimStRememberBubble;

public class APlusHintDialog extends JDialog implements ActionListener, StudentActionListener,
				APlusHintDialogInterface {

	/** Label on the JButton to receive next hint in sequence  */
	private static final String NEXT_HINT = " Tell me more >> "; // " Next Hint >> " 
	
	/** Label on the JButton to receive previous hint in sequence */
	private static final String PREVIOUS_HINT = " << Go back "; // " << Previous Hint "
	
	/** Label on the JButton to close the HintWindow in foucs */
	private static final String OK = " OK ";
	
	/**	Title for the dialog window displayed to the user */
	private static final String DIALOG_TITLE = " Mr. Williams says    	";
	
	/** Message to display */
	String message;
	
	private JPanel okCancelPanel = new JPanel();
	
	/**	Message display window, with support for HTML format message */
	private JEditorPane hintsJEditorPane;
	protected JScrollPane hintsJEditorScrollPane;
	
	private APlusHintDialog.HintJButton previousJButton = new APlusHintDialog.HintJButton(PREVIOUS_HINT);
	
	private APlusHintDialog.HintJButton nextJButton = new APlusHintDialog.HintJButton(NEXT_HINT);
	
	private APlusHintDialog.HintJButton okJButton = new APlusHintDialog.HintJButton(OK);
	
	/**	Default font family  */
	String fontFamily = JCommWidget.getDefaultFont().getFamily();
	
	/**	Default font size  */
	int fontSize = JCommWidget.getDefaultFont().getSize();
	
	APlusHintMessagesManager aPlusHintMessagesManger;
	
	Container contentPane = getContentPane();
	
	SimStLogger logger;
	
	int depth = 1;
	long openTime = -1;
	long hintStartTime = -1;
	

	/**	 */
	private MetaTutorAvatarComponent mtAvatar;
	
	/**	 */
	private boolean visibleFlag;
	Frame parentFrame=null;
			
	/**
	 * @param parent
	 */
	public APlusHintDialog(Frame parent, APlusHintMessagesManager hintMessagesManager, SimStLogger log, JComponent comp){
		super(parent, true); // Dialog is modal. To make it non-modal set it to false
		logger = log;
		mtAvatar = (MetaTutorAvatarComponent) comp;
		parentFrame=parent;
		init(hintMessagesManager);
		
		
	}
	
	/**
	 * @param hintMessagesManager
	 */
	private void init(APlusHintMessagesManager hintMessagesManager) {
	
		setTitle(DIALOG_TITLE);
		aPlusHintMessagesManger = hintMessagesManager;
		
		
		setLocation(new java.awt.Point(400,200));
		setSize(400,260);
		setResizable(true);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL); //  blocks all top-level windows from the same java application
		setAlwaysOnTop(true);

		contentPane.setLayout(new BorderLayout());
		
		hintsJEditorPane = new JEditorPane();
		hintsJEditorPane.setName("hintsJEditorPane");
		hintsJEditorPane.setContentType("text/html");
		hintsJEditorPane.setText("<html><br><br><br><br></html>");
		hintsJEditorPane.setAutoscrolls(true);
		hintsJEditorPane.setEditable(false);
		hintsJEditorPane.setFocusable(false);
		hintsJEditorPane.setMargin(new Insets(5, 5, 5, 2)); // Adds a padding in the order: top, left, bottom, right

		hintsJEditorScrollPane = new JScrollPane(hintsJEditorPane);
		contentPane.add(hintsJEditorPane, BorderLayout.CENTER);
		
        okCancelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        okCancelPanel.add(previousJButton);
        okCancelPanel.add(nextJButton);
        okCancelPanel.add(okJButton);

        contentPane.add(okCancelPanel, BorderLayout.SOUTH);

        previousJButton.addActionListener(this);
        nextJButton.addActionListener(this);
        okJButton.addActionListener(this);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
        	public void windowClosing(java.awt.event.WindowEvent e) {
        		long endTime = (new Date()).getTime();
                int duration = (int) ((endTime - openTime)/1000);
                String leavingMessage = hintsJEditorPane.getText();
        		leavingMessage = leavingMessage.replaceAll("\\<.*?>","");
                int durationHint = (int) ((endTime - hintStartTime)/1000);
        		
    			// Change the meta tutor image to normal
    			if(mtAvatar != null) {
    				mtAvatar.changeMetaTutorImage(SimStPLE.METATUTOR_IMAGE);
    			}
    			
    			//System.out.println(" Closing the pop up ");

                logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_LEFT_HINT_ACTION, "", ""+depth, "", durationHint, leavingMessage);
    			logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL,SimStLogger.METATUTOR_CLOSE_HINT_ACTION, "", ""+depth, "", duration);

    			//if (thinkBubble!=null){	thinkBubble.setVisible(false);}
    			if (spotlight!=null){	spotlight.removeSpotlight(); }
    			
        		aPlusHintMessagesManger.dialogCloseCleanUp();
        		reset();
        		
        		visibleFlag = false;
        		//System.out.println("Going to setVisible(false)");
        		setVisible(false);
        		//System.out.println("Going to set the previus tab");
        		if(proactiveMessage) {
    				if(getPreviousTab() != aplus.getAplusTabs().getSelectedIndex())
    					aplus.getAplusTabs().setSelectedIndex(previousTab);
    				mtAvatar.getSimStudent().getMissController().getSimStPLE().setModelTracer(true);
    				proactiveMessage = false;
    				//System.out.println("Enabled the Model Tracer ");
    			}
        		if (OkPressed.availablePermits() <= 0)
        			OkPressed.release();
        	}
        });
	}

	@Override
	public void displayBuggyMessage(String buggyMessage) {}

	@Override
	public void displaySuccessMessage() {}

	@Override
	public Component getDoneButton() {
		return null;
	}

	@Override
	public Component getHintButton() {
		return null;
	}

	@Override
	public JButton getNextHintButton() {
		return nextJButton;
	}

	@Override
	public JButton getPrevHintButton() {
		return previousJButton;
	}

	@Override
	public boolean getSuppressFeedback() {
		return false;
	}

	@Override
	public boolean isHintButton(Component c) {
		if(c == okJButton || c == previousJButton || c == nextJButton)
			return true;
		
		return false;
	}

	@Override
	public void reset() {
		depth = 1;
		openTime = -1;
		hintStartTime = -1;
		
		if(!visibleFlag)
			return;
		
		hintsJEditorPane.setText("");
		previousJButton.setEnabled(false);
		nextJButton.setEnabled(false);
		aPlusHintMessagesManger.reset();
		repaint();
				
		return;
	}

	@Override
	public void setDisplayHint(boolean displayHint) {}

	@Override
	public void setSuppressFeedback(boolean suppressFeedback) {}
	
	SimStRememberBubble thinkBubble=null;
	Point originalLocation=null;
	AplusSpotlight spotlight=null;
	private int previousTab = 0;
	AplusPlatform aplus;
	boolean proactiveMessage = false;
	
	@Override
	public void showThinkBubble(){	
		
		SimStPeerTutoringPlatform peerTutoringPlatform= mtAvatar.getSimStudent().getMissController().getSimStPLE().getSimStPeerTutoringPlatform();
		
		aplus =(AplusPlatform) mtAvatar.getSimStudent().getMissController().getSimStPLE().getSimStPeerTutoringPlatform();
		
		String[] javaVersionElements = System.getProperty("java.version").split("\\.");
		proactiveMessage = true;
		int major = Integer.parseInt(javaVersionElements[1]);
		//System.out.println("Tab no : "+aplus.getAplusTabs()+"  "+(aplus.getAplusTabs().getSelectedIndex()));
		//System.out.println(" Array : "+peerTutoringPlatform.getTargetWindow().split(":").toString());
		String targetWindow =peerTutoringPlatform.getTargetWindow().split(":")[1].trim(); 
		JPanel selectedTab = null;
		//MetaTutorAvatarComponent avatar = 
		//System.out.println("Target Window : "+targetWindow);
		//System.out.println("Previous Tab : "+aplus.getAplusTabs().getSelectedIndex());
		
		setPreviousTab(aplus.getAplusTabs().getSelectedIndex());
		if(targetWindow.equalsIgnoreCase("quiz")){
			//System.out.println("Tab set ");
			aplus.getAplusTabs().setSelectedIndex(5);
			selectedTab = (JPanel)aplus.getAplusTabs().getComponentAt(5);
		}
		else if(targetWindow.equalsIgnoreCase("Practice")) {
			aplus.getAplusTabs().setSelectedIndex(0);
			selectedTab = (JPanel)aplus.getAplusTabs().getComponentAt(0);
			
		}
		else if(targetWindow.equalsIgnoreCase("Unit overview")) {
			aplus.getAplusTabs().setSelectedIndex(3);
			selectedTab = (JPanel)aplus.getAplusTabs().getComponentAt(3);
		}
		else if(targetWindow.equalsIgnoreCase("examples")) {
			aplus.getAplusTabs().setSelectedIndex(4);
			selectedTab = (JPanel)aplus.getAplusTabs().getComponentAt(4);
			
		}
		else {
				aplus.getAplusTabs().setSelectedIndex(1);
				selectedTab = (JPanel)aplus.getAplusTabs().getComponentAt(1);
			}

		
		if (major>=7 ){
			if(aplus.getAplusTabs().getSelectedIndex() == 0)
				spotlight=new AplusSpotlight(peerTutoringPlatform,selectedTab,SimStRememberBubble.RIGHT,null);
			else
				spotlight=new AplusSpotlight(peerTutoringPlatform,selectedTab,SimStRememberBubble.LEFT,null);

		}
		
		
		originalLocation=this.getLocation();
		//Point mtAvatarLocation;
		//if(selectedTab.isDisplayable())
			//mtAvatarLocation= selectedTab.getLocationOnScreen();

		//Point mtAvatarLocation=aplus.getLocationOnScreen();
		//mtAvatarLocation.x=mtAvatarLocation.x-.getWidth();
		//mtAvatarLocation.y=mtAvatarLocation.y-selectedTab.getHeight()-30;
		//setLocation(mtAvatarLocation);
		setLocation(new Point(1000,400));
			
		
	}
	
	@Override
	public void showMessage(String message) {
		
		
		hintsJEditorPane.setText("");	
			
		
		if(message == null){
			reset();
			java.awt.EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					setVisible(false);
					if (spotlight!=null){
						spotlight.removeSpotlight();
					}
				}
			});
			return;
		}
		
		if(openTime == -1)
			openTime = (new Date()).getTime();
		hintStartTime = (new Date()).getTime();
		
		hintsJEditorPane.setFont(JCommWidget.getDefaultFont());
		hintsJEditorPane.setText(message);
		resetButtonEnables();
		
		if(message.trim().equals("")){
			visibleFlag = false;
			java.awt.EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					setVisible(visibleFlag);
					if (spotlight!=null && visibleFlag==false){
						spotlight.removeSpotlight();
					}
				}
			});
		} else {
			//visibleFlag = true;
			//setVisible(true);
			// Show the dialog using the event-dispatch thread
			final JDialog dlg=this;
			java.awt.EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					//SimStPeerTutoringPlatform peerTutoringPlatform= mtAvatar.getSimStudent().getMissController().getSimStPLE().getSimStPeerTutoringPlatform();
					//spotlight=new AplusSpotlight(peerTutoringPlatform, mtAvatar,SimStRememberBubble.LEFT,dlg);
					setVisible(true);
					validate();
				}
			});
		}
	}

	private void resetButtonEnables() {
	
		previousJButton.setEnabled(aPlusHintMessagesManger.hasPreviousMessage());
		nextJButton.setEnabled(aPlusHintMessagesManger.hasNextMessage());
		
		if(visibleFlag) {
			this.repaint();
			this.validate();
		}
		return;
	}

	/*
	final Color[] colors = {Color.RED, Color.GREEN, Color.BLUE};
	
	private int colorIndexGenerator() {
		return new Random().nextInt(3);
	}

	javax.swing.Timer tt = new javax.swing.Timer(2000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			ssPTP.getQuizButton().setBackground(colors[colorIndexGenerator()]);
		}
	});
	tt.start();
	*/

	@Override
	public void actionPerformed(ActionEvent ae) {

		JButton selectedButton = (JButton) ae.getSource();
		
		String leavingMessage = hintsJEditorPane.getText();
		leavingMessage = leavingMessage.replaceAll("\\<.*?>","");
		long endTime = (new Date()).getTime();
        int durationHint = (int) ((endTime - hintStartTime)/1000);
		
		if(selectedButton == okJButton) {
					
			//if (thinkBubble!=null){
			//	thinkBubble.setVisible(false);
				//if (originalLocation!=null) setLocation(originalLocation);
			//}
			if (spotlight!=null){
				spotlight.removeSpotlight();
			}
			
            int duration = (int) ((endTime - openTime)/1000);
           // System.out.println(" In action Performed : "+openTime+ "   End "+endTime);
            logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_LEFT_HINT_ACTION, "", ""+depth, "", durationHint, leavingMessage);
			logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL,SimStLogger.METATUTOR_CLOSE_HINT_ACTION, "", ""+depth, "", duration);
			aPlusHintMessagesManger.dialogCloseCleanUp();
			
			// Change the meta tutor image to normal
			if(mtAvatar != null) {
				mtAvatar.changeMetaTutorImage(SimStPLE.METATUTOR_IMAGE);
			}
			
			reset();
			visibleFlag = false;
			//System.out.println("Going to setVisible false");
			setVisible(false);
			//System.out.println(" Going to set to previous ttab : "+previousTab);
			if(proactiveMessage) {
				if(getPreviousTab() != aplus.getAplusTabs().getSelectedIndex())
					aplus.getAplusTabs().setSelectedIndex(previousTab);
				mtAvatar.getSimStudent().getMissController().getSimStPLE().setModelTracer(true);
				proactiveMessage = false;
				//System.out.println("Enabled the Model Tracer ");
			}
			if (OkPressed.availablePermits() <= 0)
				OkPressed.release();
			return;
		}
		
		if(selectedButton == nextJButton) {
			logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_LEFT_HINT_ACTION, "", ""+depth, "", durationHint, leavingMessage);
			logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL,SimStLogger.METATUTOR_NEXT_HINT_ACTION, ""+depth);
			message = aPlusHintMessagesManger.getNextMessage();
			depth++;
		} else if(selectedButton == previousJButton) {
			logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_LEFT_HINT_ACTION, "", ""+depth, "", durationHint, leavingMessage);
			logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL,SimStLogger.METATUTOR_PREVIOUS_HINT_ACTION, ""+depth);
			message = aPlusHintMessagesManger.getPreviousMessage();
			depth--;
		}
		
		showMessage(message);
		return;
	}

	// quizButton.setIcon(createImageIcon("img/quiz.png"));
    protected ImageIcon createImageIcon(String path) {
    	
    	String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+path;
    	URL url = this.getClass().getResource(file);
    	return new ImageIcon(url);
    }

	@Override
	public void studentActionPerformed(StudentActionEvent sae) {}

	

	public int getPreviousTab() {
		return previousTab;
	}

	public void setPreviousTab(int previousTab) {
		this.previousTab = previousTab;
	}



	/**
	 * Combine a JButton with a marker interface telling that it's a hint button.
	 */
	public static class HintJButton extends JButton implements AvoidsBackGrading {

		private static final long serialVersionUID = 1L;

		/** For superclass no-argument constructor. */
		public HintJButton() { super(); }
		
		/**
		 * For superclass constructor accepting a label.
		 * @param text label for this button
		 */
		public HintJButton(String text) { super(text); }
	}

}
