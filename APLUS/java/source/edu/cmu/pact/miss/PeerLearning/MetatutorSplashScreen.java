package edu.cmu.pact.miss.PeerLearning;

import javax.swing.JDialog; 

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import pact.CommWidgets.JCommTable;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Vector;

public class MetatutorSplashScreen extends JDialog implements ActionListener {
	private JButton closeButton= null;
	private JButton gotoVideoButton= null;
	private JButton goToTutoringButton= null;
	private JButton yesButton = null;
	private JButton noButton = null;
	private String SimStudentName=null;
	private JButton clickMeButton = null;
	private JLayeredPane layers;
	private JLabel metatutorLabel; 
	private String message;
	private JTextPane messagePane;
	private Vector<SplashFrame> frames;
	private int frameCnt=0;
	boolean isFinalChallengePassed=false;

	/*variable to hold the simStTutoringPlatform */
	SimStPeerTutoringPlatform simStPeerTutoringPlatform = null;
	public SimStPeerTutoringPlatform getSimStPeerTutoringPlatform() { return simStPeerTutoringPlatform; }
	public void setSimStPeerTutoringPlatform(SimStPeerTutoringPlatform simStPeerTutoringPlatform) {this.simStPeerTutoringPlatform = simStPeerTutoringPlatform;}


	/**
	 * Constructor to create the splash screen
	 * @param frame	parent frame
	 * @param modal   if we want dialog to be modal or not
	 * @param font    font to be used
	 * @param simStudentName  name of SimStudent 
	 * @param splashImage  image to show 
	 * @param splashMessage  message to show
	 * @param simStPeerTutoringPlatform  pointer to simStPeerTutoringPlatfrom object, so we can click quiz button from here.
	 * @param quizPassed  boolean indicating if quiz has been passed or not 
	 */
	public MetatutorSplashScreen(JFrame frame, boolean modal,String simStudentName,Vector<SplashFrame> frames, SimStPeerTutoringPlatform simStPeerTutoringPlatform, boolean quizPassed) {
		super(frame, modal);  
		setSimStPeerTutoringPlatform(simStPeerTutoringPlatform);
		this.SimStudentName=simStudentName;
		this.frames=frames;

		
		SplashFrame currentFrame=frames.get(frameCnt);
		/*Load first background image to determine width the height of window */ 
		String backgroundImageString= currentFrame.backgroundImg ;	
		ImageIcon splashIcon=createImageIcon(backgroundImageString);

		/*Create the layered pane (1at frame defines size) */
		layers = new JLayeredPane();
		layers.setPreferredSize(new Dimension(splashIcon.getIconWidth()+20,splashIcon.getIconHeight()));

		/*Create the panel to add the layers*/
		JPanel backPanel=new JPanel();
		backPanel.setOpaque(false);
		getContentPane().add(backPanel);

		/*add the Metatutor image layer*/
		metatutorLabel = new JLabel(createImageIcon(backgroundImageString));
		metatutorLabel.setLayout(new BorderLayout());
		metatutorLabel.setBounds(0,0,splashIcon.getIconWidth()+20,splashIcon.getIconHeight());
		layers.add(metatutorLabel,new Integer(100));

		/*prepare the text layer*/
		messagePane = new JTextPane();
		messagePane.setEditable(false);
		messagePane.setForeground(Color.black);
		messagePane.setOpaque(false);
		isFinalChallengePassed=quizPassed;
	//	if (!quizPassed)
			updateSpashWindow(messagePane,currentFrame);

		metatutorLabel.add(messagePane);

		if (!quizPassed){   
			/*create the click me button*/
			clickMeButton = new JButton();
			clickMeButton.addActionListener(this);
			clickMeButton.setOpaque(false);
			clickMeButton.setContentAreaFilled(false);
			clickMeButton.setBorderPainted(false); //<------ Change this line to paint the border of the clickable area (for debugging purposes)!
			clickMeButton.setBounds(currentFrame.clickAreaX, currentFrame.clickAreaY, currentFrame.clickAreaWidth, currentFrame.clickAreaHeight);
			clickMeButton.addMouseListener(new MouseAdapter() { 
				public void mouseEntered(MouseEvent me){
					/*change cursor to hand*/
					Cursor cursor = Cursor.getDefaultCursor();
					cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
				    setCursor(cursor); 
		            } 
				public void mouseExited(MouseEvent me){
					/*change cursor to hand*/
					Cursor cursor = Cursor.getDefaultCursor();
					cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); 
				    setCursor(cursor); 
		            } 
			});
			
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			

			//clickMeButton.setBounds(0,0,clickMeLayerWidth,clickMeLayerHeight);
			layers.add(clickMeButton,new Integer(200)); 

			
			
			if (!this.simStPeerTutoringPlatform.getSimStPLE().getSimSt().isSsAplusCtrlCogTutorMode())
				normalAplusButtonArrangement(splashIcon);
			else 
				aplusControlButtonArrangement(splashIcon);
			
		
			
			
		
			
			
		}
		else{
			
			yesButton = new JButton("Yes");
			int splashButtonHeight=40;
			int splashButtonWidth=80;
			yesButton.addActionListener(this);
			yesButton.setBounds(splashIcon.getIconWidth()/2-splashButtonWidth/2-2*splashButtonWidth/3,splashIcon.getIconHeight()-splashButtonHeight,splashButtonWidth,splashButtonHeight);
			layers.add(yesButton,new Integer(200)); 
			noButton = new JButton("No");
			noButton.addActionListener(this);
			noButton.setBounds(splashIcon.getIconWidth()/2-splashButtonWidth/2 + 2*splashButtonWidth/3 ,splashIcon.getIconHeight()-splashButtonHeight,splashButtonWidth,splashButtonHeight);
			layers.add(noButton,new Integer(200)); 	
		}

		backPanel.add(layers);

		pack();
		setLocationRelativeTo(frame);
		setAlwaysOnTop(true);
		setVisible(true);

	}

	void aplusControlButtonArrangement(ImageIcon splashIcon){
		
		/*create the close button*/
		closeButton = new JButton();
		int closeButtonHeight=60;
		int clodeButtonWidth=170;
		closeButton.addActionListener(this);
		closeButton.setOpaque(false);
		closeButton.setContentAreaFilled(false);
		closeButton.setBorderPainted(false);
	//	closeButton.setBounds(clodeButtonWidth+30,splashIcon.getIconHeight()-closeButtonHeight,clodeButtonWidth,closeButtonHeight);
		closeButton.setBounds(5,splashIcon.getIconHeight()-closeButtonHeight,clodeButtonWidth,closeButtonHeight);

		layers.add(closeButton,new Integer(200));
		
		int gotoVideoButtonHeight=60;
		int gotoVideoButtonWidth=170;
		
		goToTutoringButton = new JButton();	
		goToTutoringButton.addActionListener(this);
		goToTutoringButton.setOpaque(false);
		goToTutoringButton.setContentAreaFilled(false);
		goToTutoringButton.setBorderPainted(false);
		goToTutoringButton.setBounds(clodeButtonWidth + 30,splashIcon.getIconHeight()-gotoVideoButtonHeight,gotoVideoButtonWidth,gotoVideoButtonHeight);
		//gotoVideoButton.setBounds(splashIcon.getIconWidth()/2-clodeButtonWidth/2,splashIcon.getIconHeight()-closeButtonHeight,clodeButtonWidth,closeButtonHeight);
		layers.add(goToTutoringButton,new Integer(200));
		
		
		gotoVideoButton = new JButton();
	
		gotoVideoButton.addActionListener(this);
		gotoVideoButton.setOpaque(false);
		gotoVideoButton.setContentAreaFilled(false);
		gotoVideoButton.setBorderPainted(false);
		gotoVideoButton.setBounds(2*clodeButtonWidth+50,splashIcon.getIconHeight()-gotoVideoButtonHeight,gotoVideoButtonWidth,gotoVideoButtonHeight);
		layers.add(gotoVideoButton,new Integer(200));
		
		

		
		
	}
	
	
	void normalAplusButtonArrangement(ImageIcon splashIcon){
		
		/*create the close button*/
		closeButton = new JButton();
		int closeButtonHeight=60;
		int clodeButtonWidth=170;
		closeButton.addActionListener(this);
		closeButton.setOpaque(false);
		closeButton.setContentAreaFilled(false);
		closeButton.setBorderPainted(false);
		closeButton.setBounds(100,splashIcon.getIconHeight()-closeButtonHeight,clodeButtonWidth,closeButtonHeight);
		
		//closeButton.setBounds(splashIcon.getIconWidth()/2-clodeButtonWidth/2,splashIcon.getIconHeight()-closeButtonHeight,clodeButtonWidth,closeButtonHeight);
		layers.add(closeButton,new Integer(200));
		
		
		
		gotoVideoButton = new JButton();
		int gotoVideoButtonHeight=60;
		int gotoVideoButtonWidth=170;
		gotoVideoButton.addActionListener(this);
		gotoVideoButton.setOpaque(false);
		gotoVideoButton.setContentAreaFilled(false);
		gotoVideoButton.setBorderPainted(false);
		gotoVideoButton.setBounds(100+clodeButtonWidth + 40,splashIcon.getIconHeight()-gotoVideoButtonHeight,gotoVideoButtonWidth,gotoVideoButtonHeight);
		//gotoVideoButton.setBounds(splashIcon.getIconWidth()/2-clodeButtonWidth/2,splashIcon.getIconHeight()-closeButtonHeight,clodeButtonWidth,closeButtonHeight);
		layers.add(gotoVideoButton,new Integer(200));
		
	}

	/**
	 * Method for updating the contents of the splash window based on the current frame
	 * @param messagePane
	 * @param currentFrame
	 */
	private void updateSpashWindow(JTextPane messagePane, SplashFrame currentFrame){
		if (currentFrame.message!=null){
			messagePane.setFont(currentFrame.font);			
			messagePane.setForeground(currentFrame.fontColor);
			messagePane.setBorder(BorderFactory.createEmptyBorder(currentFrame.messageX, currentFrame.messageY, currentFrame.messageWidth, currentFrame.messageHeight));
			StyledDocument doc = messagePane.getStyledDocument();
			SimpleAttributeSet aligntAttibute = new SimpleAttributeSet();
			StyleConstants.setAlignment(aligntAttibute, currentFrame.align);
			doc.setParagraphAttributes(0, doc.getLength(), aligntAttibute, false);
			messagePane.setText(replaceName(currentFrame.message));	
		}
		else{
			messagePane.setText("");
		}

		if (clickMeButton!=null)
			clickMeButton.setBounds(currentFrame.clickAreaX, currentFrame.clickAreaY, currentFrame.clickAreaWidth, currentFrame.clickAreaHeight);


		/*if last frame reached, show close button*/
		if (frameCnt==frames.size()-1 && !isFinalChallengePassed){
			
			String closeButtonText=simStPeerTutoringPlatform.getSimStPLE().getSimSt().isSsAplusCtrlCogTutorMode()? SimStPLE.APLUS_SPLASH_OK_BUTTON_TXT_APLUS_CONTROL : SimStPLE.APLUS_SPLASH_OK_BUTTON_TXT;
			closeButton.setText(closeButtonText);
			closeButton.setOpaque(true);
			closeButton.setContentAreaFilled(true);
			closeButton.setBorderPainted(true);
			layers.remove(clickMeButton); 
			
			
			
			gotoVideoButton.setText(SimStPLE.APLUS_SPLASH_CANCEL_BUTTON_TXT);
			gotoVideoButton.setOpaque(true);
			gotoVideoButton.setContentAreaFilled(true);
			gotoVideoButton.setBorderPainted(true);
						
			
			if (this.getSimStPeerTutoringPlatform().getSimStPLE().getSimSt().isSsAplusCtrlCogTutorMode()){
				goToTutoringButton.setText(SimStPLE.APLUS_SPLASH_TUTORING_BUTTON_TXT_APLUS_CONTROL);
				goToTutoringButton.setOpaque(true);
				goToTutoringButton.setContentAreaFilled(true);
				goToTutoringButton.setBorderPainted(true);
			}
			
			
			//layers.remove(clickMeButton); 
			
		}
	}

	public void actionPerformed(ActionEvent e) {

		if (clickMeButton==e.getSource()){	  	
			frameCnt++;
			SplashFrame currentFrame=frames.get(frameCnt);
			/*update the background*/
			ImageIcon backgroundImage=createImageIcon(frames.get(frameCnt).backgroundImg);
			metatutorLabel.setIcon(backgroundImage);
			/*update message (if any)*/
			updateSpashWindow(messagePane,currentFrame);	 	   	 	   
		}
		else if(closeButton == e.getSource()) {  
			if (frameCnt==frames.size()-1)
				setVisible(false);
			
			
			/*if (getSimStPeerTutoringPlatform().getBrController().getMissController().getSimSt().isSsCogTutorMode()){
				
				if (!getSimStPeerTutoringPlatform().getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode())
					getSimStPeerTutoringPlatform().getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().giveNextProblem(true);
				
					getSimStPeerTutoringPlatform().getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().reviewExampleSection();
			}
			*/
						
			/* When splash screen is closed and we are in CogTutor mode than give next problem & review examples. If we are in Aplus Control mode, then 
			 * just close and show the examples */
			
			getSimStPeerTutoringPlatform().getSimStPLE().cogTutorLaunchComplete();

			
		}
		else if (goToTutoringButton == e.getSource()){
			if (frameCnt==frames.size()-1)
				setVisible(false);
			String ms=getSimStPeerTutoringPlatform().getBrController().getMissController().getSimStPLE().getConversation().getMessage(SimStConversation.COG_TUTOR_PRACTICE_FIRST);
			new SimStMessageDialog(new Frame(),getSimStPeerTutoringPlatform().getBrController().getMissController().getSimSt().getSimStLogger(), ms,false,SimStMessageDialog.SHOW_PRACTICE_EXAMPLES);
			
		}
		else if(gotoVideoButton == e.getSource()) {  
			if (frameCnt==frames.size()-1)
				setVisible(false);
			
			/*When splash screen is closed and we are in CogTutor mode then give next problem*/
			if (getSimStPeerTutoringPlatform().getBrController().getMissController().getSimSt().isSsCogTutorMode() && !getSimStPeerTutoringPlatform().getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){	
				getSimStPeerTutoringPlatform().getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().giveNextProblem(true);
	
			}
			
			if (getSimStPeerTutoringPlatform().getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode())
				AplusPlatform.VIDEO_TAB_INDEX=2;
			else if (getSimStPeerTutoringPlatform().getBrController().getMissController().getSimSt().isSsCogTutorMode())
				AplusPlatform.VIDEO_TAB_INDEX=1;
			else 
				AplusPlatform.VIDEO_TAB_INDEX=2;
			
			((AplusPlatform) getSimStPeerTutoringPlatform()).getAplusTabs().setSelectedIndex(AplusPlatform.VIDEO_TAB_INDEX);
			
			
		}
		else if(yesButton == e.getSource()) {
			setVisible(false);
			((AplusPlatform) getSimStPeerTutoringPlatform()).quizButtonQuiz.doClick();
		}
		else if (noButton==e.getSource()){  
			setVisible(false);
		}

	}

	/**
	 * MEthod to return the image icone
	 * @param path
	 * @return
	 */
	public ImageIcon createImageIcon(String path) {
		String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+path;
		URL url = this.getClass().getResource(file);

		return new ImageIcon(url); 

	}

	/**
	 * MEthdo to replace SimStudent name in msg
	 * @param msg
	 * @return
	 */
	private String replaceName(String msg){
		return msg.replace("SimStudent", this.SimStudentName);
	}

	/**
	 * Internal class to hold all the necessary parameters of a splash frame
	 * (splash screen consists of frames played one after the other)
	 * @author simstudent
	 *
	 */
	public  static class SplashFrame{
		String backgroundImg;
		String message=null;
		int messageX=0;
		int messageY=0;
		int messageWidth=0;
		int messageHeight=0;
		Font font;
		int align;
		int clickAreaX=0;
		int clickAreaY=0;
		int clickAreaWidth=0;
		int clickAreaHeight=0;
		Color fontColor=Color.black;
		

		public SplashFrame(String background,int clickAreaX, int clickAreaY, int clickAreaWidth, int clickAreaHeight){
			this.backgroundImg=background;
			this.clickAreaX=clickAreaX;
			this.clickAreaY=clickAreaY;
			this.clickAreaWidth=clickAreaWidth;
			this.clickAreaHeight=clickAreaHeight;
		}


		public SplashFrame(String background,String message,int messageX, int messageY, int messageWidth, int messageHeight,Font font, int align, int clickAreaX, int clickAreaY, int clickAreaWidth, int clickAreaHeight){
			this.backgroundImg=background;
			this.message=message;
			this.messageX=messageX;
			this.messageY=messageY;
			this.messageWidth=messageWidth;
			this.messageHeight=messageHeight;
			this.font=font;
			this.align=align;
			this.clickAreaX=clickAreaX;
			this.clickAreaY=clickAreaY;
			this.clickAreaWidth=clickAreaWidth;
			this.clickAreaHeight=clickAreaHeight;

		}

		public SplashFrame(String background,String message,int messageX, int messageY, int messageWidth, int messageHeight,Font font, int align,Color clr){
			this.backgroundImg=background;
			this.message=message;
			this.messageX=messageX;
			this.messageY=messageY;
			this.messageWidth=messageWidth;
			this.messageHeight=messageHeight;
			this.font=font;
			this.align=align;
			this.fontColor=clr;
		}

		
		
		
		
		
	}




}