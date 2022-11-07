package edu.cmu.pact.miss.PeerLearning;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Calendar;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.miss.SimSt;
import java.awt.GridLayout;
import java.awt.FlowLayout;


public class StudentAvatarDesigner extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SimStPeerTutoringPlatform gui = null;
	static JFrame frame = null;

    private JLayeredPane layeredPane;
    protected JLabel backgroundLabel;
    protected JLabel hairLabel;
    //protected JLabel expressionLabel;
    //protected JLabel noseLabel;
    protected JLabel shirtLabel;
    protected JLabel faceLabel;
    private JButton nextBackground;
    private JButton nextHair;
    //private JButton nextEyes;
    //private JButton nextNose;
    private JButton nextShirt;
    private JButton prevBackground;
    private JButton prevHair;
    //private JButton prevEyes;
    //private JButton prevNose;
    private JButton prevShirt;
	protected JButton random;
	protected JButton save;
	int backgroundIndex = 0;
	int hairIndex = 0;
	//int expressionIndex = 0;
	//int noseIndex = 0;
	int shirtIndex = 0;
	int faceIndex = 0;
	protected JTextField name;
	
	protected String accountName = "default";
	
	SimStLogger logger;
	public static String saveString = "%";

	protected String[] silhouettes = { "img/head1.png","img/head2.png","img/head3.png","img/head4.png"};
	protected String[] hairdos = { "img/hair1.png","img/hair2.png","img/hair3.png","img/hair4.png","img/hair5.png","img/hair6.png",
		"img/hair7.png","img/hair8.png","img/hair9.png","img/hair10.png","img/hair11.png","img/hair12.png"};
	/*protected String[] expressions = { "img/happy.png", "img/think.png", "img/expression.png","img/expression2.png","img/expression3.png",
		"img/expression4.png","img/expression5.png" ,"img/expression6.png","img/expression7.png" ,"img/expression8.png" ,
		"img/expression9.png","img/expression10.png" ,"img/expression11.png" ,"img/expression12.png" ,"img/expression13.png"   };
	protected String[] noses = { "img/nose1.png", "img/nose2.png","img/nose3.png","img/nose4.png","img/nose5.png","img/nose6.png",
		"img/nose7.png","img/nose8.png"};*/
	protected String[] shirts = {"img/shirt1.png", "img/shirt2.png","img/shirt3.png","img/shirt4.png","img/shirt5.png","img/shirt6.png",
		"img/shirt7.png","img/shirt8.png" };
	protected String[] faces = {"img/face1.png", "img/face2.png", "img/face3.png", "img/face4.png", "img/face5.png"};
	
	protected long startTime;

		
    public StudentAvatarDesigner(SimStPeerTutoringPlatform gui, String userid)    {
    	this.gui = gui;
    	accountName = userid;
    	setup();
    	logger = new SimStLogger(gui.getBrController());
    }
    
    public StudentAvatarDesigner()    {
    	setup();
    }
    
    JPanel optionPanel, welcomePanel, avatarPanel, buttonPanel;
    private JLabel label;
    
    protected void createOptionPanel()
    {
    	optionPanel = new JPanel();
    	
    	optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.PAGE_AXIS));
        
        JPanel skinPanel = new JPanel();
        optionPanel.add(skinPanel);
        JPanel hairPanel = new JPanel();
        hairPanel.setLayout(new BoxLayout(hairPanel, BoxLayout.X_AXIS));
        optionPanel.add(hairPanel);
        /*JPanel eyesPanel = new JPanel();
        eyesPanel.setLayout(new BoxLayout(eyesPanel, BoxLayout.X_AXIS));
        optionPanel.add(eyesPanel);
        JPanel nosePanel = new JPanel();
        nosePanel.setLayout(new BoxLayout(nosePanel, BoxLayout.X_AXIS));
        optionPanel.add(nosePanel);*/
        JPanel shirtPanel = new JPanel();
        shirtPanel.setLayout(new BoxLayout(shirtPanel, BoxLayout.X_AXIS));
        optionPanel.add(shirtPanel);
        
        JLabel skinChangeLabel = new JLabel("   Skin Color   ");
        JLabel hairChangeLabel = new JLabel("   Hair   ");
        //JLabel eyeChangeLabel = new JLabel("   Eyes   ");
        //JLabel noseChangeLabel = new JLabel("   Nose   ");
        JLabel shirtChangeLabel = new JLabel("   Shirt   ");
        skinPanel.setLayout(new BoxLayout(skinPanel, BoxLayout.X_AXIS));
        
        prevBackground = new JButton("<");
        skinPanel.add(prevBackground);
        prevBackground.addActionListener(this);
        
        skinPanel.add(skinChangeLabel);
        
        prevHair = new JButton("<");
        hairPanel.add(prevHair);
        prevHair.addActionListener(this);

        hairPanel.add(hairChangeLabel);
        
        /*prevEyes = new JButton("<");
        eyesPanel.add(prevEyes);
        prevEyes.addActionListener(this);

        eyesPanel.add(eyeChangeLabel);
        
        prevNose = new JButton("<");
        nosePanel.add(prevNose);
        prevNose.addActionListener(this);

        nosePanel.add(noseChangeLabel);*/
        
        prevShirt = new JButton("<");
        shirtPanel.add(prevShirt);
        prevShirt.addActionListener(this);

        shirtPanel.add(shirtChangeLabel);
        
        
        nextBackground = new JButton(">");
        skinPanel.add(nextBackground);
        nextBackground.addActionListener(this);
        
        nextHair = new JButton(">");
        hairPanel.add(nextHair);
        nextHair.addActionListener(this);
        
        /*nextEyes = new JButton(">");
        eyesPanel.add(nextEyes);
        nextEyes.addActionListener(this);
        
        nextNose = new JButton(">");
        nosePanel.add(nextNose);
        nextNose.addActionListener(this);*/
        
        nextShirt = new JButton(">");
        shirtPanel.add(nextShirt);
        nextShirt.addActionListener(this);

    }
    
    protected void createWelcomePanel(){
    	welcomePanel= new JPanel();
    	welcomePanel.setBorder(null);
    	welcomePanel.setLayout(new BorderLayout());
        
        JPanel instructionPanel = new JPanel();
    	instructionPanel.setLayout(new BorderLayout(0, 0));
        //instructionPanel.setLayout(new)
      
    	//instructionPanel.add(Box.createHorizontalGlue());
    	JLabel task = new JLabel("<html>Your first task is to configure your peer. Select a skin color, hair, and shirt for the peer. Click on the 'Roll a die' button to make the peer randomly. Name your peer and click the save button.</html>");
    	task.setHorizontalAlignment(JLabel.LEFT);
    	task.setVerticalAlignment(JLabel.CENTER);
        task.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
        instructionPanel.add(task);
        welcomePanel.add(instructionPanel,BorderLayout.CENTER);
        //welcomePanel.add(Box.createHorizontalGlue());
        JLabel welcomeLabel = new JLabel(" Welcome!",SwingConstants.CENTER);
        welcomeLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        welcomeLabel.setBorder(new EmptyBorder(35,10,20,10));
        instructionPanel.add(welcomeLabel, BorderLayout.NORTH);
        welcomeLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
      //  instructionPanel.add(Box.createHorizontalGlue());*/
       
        
       /* JLabel instruction1 = new JLabel("Your first task is to configure your peer.",SwingConstants.CENTER);
        instruction1.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
        welcomePanel.add(instruction1, BorderLayout.CENTER);
        JLabel instruction2 = new JLabel("peer. Select a skin color, hair,and,shirt ",SwingConstants.CENTER);
        instruction2.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
        welcomePanel.add(instruction2, BorderLayout.);
        
       // welcomePanel.add(Box.createHorizontalGlue());*/
    }
    protected void createButtonPanel()
    {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(null);
 
        JPanel namePanel = new JPanel();
        namePanel.setBounds(0, 0, 309, 91);
        JLabel nameLabel = new JLabel(" Student Name");
        nameLabel.setBounds(27, 29, 124, 16);
        buttonPanel.add(namePanel);
        namePanel.setLayout(null);
        namePanel.add(nameLabel);
        name = new JTextField();
        name.setBounds(163, 23, 114, 28);
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(name); }
        
        //label = new JLabel("");
        //label.setBounds(197, 19, 0, 0);
        //namePanel.add(label);
        namePanel.add(name);
        save = new JButton("Save");
        save.setBounds(163, 63, 75, 29);
        namePanel.add(save);
        save.addActionListener(this);
        
		/*random = new JButton("I'm feeling lucky!");
		buttonPanel.add(random);
		random.addActionListener(this);*/
        
       /* JPanel savePanel = new JPanel();
        savePanel.setBounds(0, 91, 309, 91);
		savePanel.setLayout(null);
		buttonPanel.add(savePanel);*/
		

    }
    private void createAvatarPanel(){
    	avatarPanel = new JPanel(new GridLayout(3,1));
    	createOptionPanel();
        avatarPanel.add(getOptionPanel());
       
        avatarPanel.add(createRandomize());
        createButtonPanel();
        avatarPanel.add(getButtonPanel());
    	/*avatarPanel.setBorder(null);
    	avatarPanel.setLayout(new BoxLayout(avatarPanel,  BoxLayout.PAGE_AXIS));
    	//avatarPanel.setBackground(Color.blue);
    	 	//createOptionPanel();
    	//add(getOptionPanel());
    	createOptionPanel();
        avatarPanel.add(getOptionPanel());
        randomize();
        createLayeredImage();
        //avatarPanel.add(Box.createRigidArea(new Dimension(1, 10)));
        //avatarPanel.add(Box.createRigidArea(new Dimension(1, 10)));
        avatarPanel.add(getLayeredImage());
        random = new JButton("Roll a dice");
        random.setBounds(177, 90, 109, 29);
        layeredPane.add(random);
        /*JLabel nameLabel = new JLabel(" Student Name");
        nameLabel.setBounds(6, 191, 141, 28);
        layeredPane.add(nameLabel);
        name = new JTextField();
        name.setBounds(110, 191, 176, 28);
        layeredPane.add(name);
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(name); }
        
		

		save = new JButton("Save");
		save.setBounds(152, 231, 75, 29);
		layeredPane.add(save);
		save.addActionListener(this);*/
     /*   random.addActionListener(this);
       
       createButtonPanel();*/
    	
    }
    
    public JPanel createRandomize() {
    	JPanel randomize = new JPanel();
    	
    	createLayeredImage();
        randomize.setLayout(null);
        randomize.add(getLayeredImage());
        
       // randomize();
        random = new JButton("Roll a die");
        random.setBounds(160, 65, 109, 29);
        random.addActionListener(this);
        randomize.add(random);
        
        return randomize;
        
    }
    public void createLayeredImage()
    {

        //Create and load the duke icon.
        final ImageIcon icon = createImageIcon(silhouettes[backgroundIndex]);
        final ImageIcon icon2 = createImageIcon(hairdos[hairIndex]);
        //final ImageIcon icon3 = createImageIcon(expressions[expressionIndex]);
        //final ImageIcon icon4 = createImageIcon(noses[noseIndex]);
        final ImageIcon icon5 = createImageIcon(shirts[shirtIndex]);
        final ImageIcon icon6 = createImageIcon(faces[faceIndex]);

        //Create and set up the layered pane.
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(35, 5, 125, 140);
        layeredPane.setPreferredSize(new Dimension(StudentAvatarDisplay.PREFERRED_WIDTH, StudentAvatarDisplay.PREFERRED_HEIGHT));

        backgroundLabel = new JLabel(icon);
        if (icon != null) {
            backgroundLabel.setBounds(0, 0,
                                icon.getIconWidth(),
                                icon.getIconHeight());
        } 
        layeredPane.add(backgroundLabel, new Integer(2), 1);

        hairLabel = new JLabel(icon2);
        if (icon2 != null) {
            hairLabel.setBounds(0, 0,
                                icon.getIconWidth(),
                                icon.getIconHeight());
        } 
        layeredPane.add(hairLabel, new Integer(3), 1);

        /*expressionLabel = new JLabel(icon3);
        if (icon != null) {
            expressionLabel.setBounds(0, 0,
                                icon.getIconWidth(),
                                icon.getIconHeight());
        } 
        layeredPane.add(expressionLabel, new Integer(3), 0);
        

        noseLabel = new JLabel(icon4);
        if (icon != null) {
            noseLabel.setBounds(0, 0,
                                icon.getIconWidth(),
                                icon.getIconHeight());
        } 
        layeredPane.add(noseLabel, new Integer(3), 0);*/
        

        shirtLabel = new JLabel(icon5);
        if (icon != null) {
            shirtLabel.setBounds(0, 0,
                                icon.getIconWidth(),
                                icon.getIconHeight());
        } 
        layeredPane.add(shirtLabel, new Integer(1), 1);

        faceLabel = new JLabel(icon6);
        if (icon != null) {
            faceLabel.setBounds(0, 0,
                                icon.getIconWidth(),
                                icon.getIconHeight());
        } 
        layeredPane.add(faceLabel, new Integer(3), 1);

        random = new JButton("Roll a dice");
		layeredPane.add(random,new Integer(4), 1);
		random.addActionListener(this);
    	
    }
   
    public JPanel getOptionPanel()
    {
    	return optionPanel;
    }
   public JPanel getButtonPanel()
    {
	   return buttonPanel;
    }
    public JPanel getAvatarPanel(){
    	return avatarPanel;
    }
    public JLayeredPane getLayeredImage()
    {
    	return layeredPane;
    }
    
    public JPanel getWelcomePanel() {
		// TODO Auto-generated method stub
		return welcomePanel;
	}
    public void setup()
    {
    	//trace.out(" background Index : "+backgroundIndex+"  hairIndex : "+hairIndex+"  ShirtIndex : "+shirtIndex);
    	startTime = Calendar.getInstance().getTimeInMillis();
    	setLayout(new GridLayout(1,2)); 
    	setBorder(new EmptyBorder(30, 40, 10, 5));
    	createWelcomePanel();
         add(getWelcomePanel());
         
         /*JLabel instruction = new JLabel(" Select the color, hair, and shirt for the peer.");
         instruction.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
         welcomePanel.add(instruction);
         JLabel instruction3 = new JLabel("button.");
         instruction3.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
         welcomePanel.add(instruction3);
         JLabel instruction2 = new JLabel("Name your peer and click the save");
         instruction2.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
         welcomePanel.add(instruction2);
         JLabel instruction1 = new JLabel("Click on 'Roll on dice' button to make a peer randomly");
         instruction1.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
         welcomePanel.add(instruction1);*/
     
         createAvatarPanel();
         add(getAvatarPanel());
         
        /*createOptionPanel();
        add(getOptionPanel());
    	//createOptionPanel();
    	//add(getOptionPanel());
        randomize();
        
        createLayeredImage();

        add(Box.createRigidArea(new Dimension(0, 10)));
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(getLayeredImage());
       
       createButtonPanel();
       add(getButtonPanel());*/
        
        frame.setPreferredSize(new Dimension(650,400));
        frame.setResizable(false);
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((int)screenSize.width/2,(int)screenSize.height/4);
    }
    
  

	/** Returns an ImageIcon, or null if the path was invalid. */
    protected ImageIcon createImageIcon(String path) {
    	String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+path;
    	URL url = this.getClass().getResource(file);
    	
    	return new ImageIcon(url);
    	
    }
     

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    protected static void createAndShowGUI(SimStPeerTutoringPlatform gui, String userid) {
        //Create and set up the window.
    
    	
    	if(frame == null)
    	{
	        frame = new JFrame("Customize Your Student");
	        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	}
    	
        //Create and set up the content pane.
        JComponent newContentPane = new StudentAvatarDesigner(gui, userid);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        frame.setAlwaysOnTop(true);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        /*
        JDialog frame1 = new JDialog();
        frame1.getContentPane().add(frame);
        frame1.pack();
        frame1.setVisible(true);
        */
        
        
    }
    
    protected static void createAndShowGUI() {
        //Create and set up the window.
    	if(frame == null)
    	{
	        frame = new JFrame("Customize Your Student");
	        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    	}

        //Create and set up the content pane.
        JComponent newContentPane = new StudentAvatarDesigner();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    protected void randomize()
    {
    	//trace.out(" Random button called ");
		backgroundIndex = (int) (Math.random()*silhouettes.length);
		hairIndex = (int) (Math.random()*hairdos.length);
		//expressionIndex = (int) (Math.random()*expressions.length);
		//noseIndex = (int) (Math.random()*noses.length);
		shirtIndex = (int) (Math.random()*shirts.length);		
    }
    
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == save && name.getText().length() > 0)
		{
			int acceptableNameLength = 16;
			saveString += silhouettes[backgroundIndex]+"%";
        		saveString += hairdos[hairIndex]+"%";
        		//saveString += expressions[expressionIndex]+"%";
       		//saveString += noses[noseIndex]+"%";
        		saveString += shirts[shirtIndex];
			//trace.out(saveString);
        	SimStPLE.STUDENT_IMAGE = saveString;
        	//trace.out(" Avatar : "+SimStPLE.STUDENT_IMAGE);
        	if(gui != null && gui.getSimStAvatarLayerIcon() != null)
        		gui.setImage(saveString);
        	if(name.getText().length() > 0)
        	{
        		String ssName = name.getText();
        		if(ssName.length() > acceptableNameLength)
        			ssName = ssName.substring(0, acceptableNameLength);
	        	SimSt.setSimStName(ssName);
	        	if(gui != null && gui.getSimStNameLabel() != null)
	        		gui.setName(ssName);
	        	if(gui != null && gui instanceof AplusPlatform) {
	        		

	        		((AplusPlatform)gui).updateSkillometerLabelText(ssName);
	        		((AplusPlatform)gui).updateSectionMeterLabelText(ssName);        		
	        		//String msg = ((AplusPlatform)gui).getSpeechText().getText();
	        		String msg = ((AplusPlatform)gui).getConversationHistory().body().text();
	        		//msg = msg.replaceAll("[Jj][Oo][Ee][:]","");
	        		//msg = msg.replace('\n', ' ');
	        		((AplusPlatform)gui).clearSpeech();
	        		((AplusPlatform)gui).appendSpeech(msg, SimSt.SimStName);
	        		
	        	}
	        	frame.setVisible(false);
	        	
//	        	SimStPLE.saveAccountFile(accountName+".account");
	        	
	        	int duration = (int) (( Calendar.getInstance().getTimeInMillis() - startTime)/1000);
	        	
	        	logger.simStShortLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.AVATAR_SAVE_ACTION, name.getText(),saveString, duration);
	            
	        	((AplusPlatform) gui).getBrController().getStudentInterface().setVisible(true);
	        	((AplusPlatform) gui).showSplashScreen();
	        	
	        	
	        	
				return;
        	}
        	
		}

		
		if(arg0.getSource() == nextBackground)
			backgroundIndex++;
		if(backgroundIndex >= silhouettes.length)
			backgroundIndex = 0;
		if(arg0.getSource() == nextHair)
			hairIndex++;
		if(hairIndex >= hairdos.length)
			hairIndex = 0;
		/*if(arg0.getSource() == nextEyes)
			expressionIndex++;
		if(expressionIndex >= expressions.length)
			expressionIndex = 0;
		if(arg0.getSource() == nextNose)
			noseIndex++;
		if(noseIndex >= noses.length)
			noseIndex = 0;*/
		if(arg0.getSource() == nextShirt)
			shirtIndex++;
		if(shirtIndex >= shirts.length)
			shirtIndex = 0;
		

		if(arg0.getSource() == prevBackground)
			backgroundIndex--;
		if(backgroundIndex < 0)
			backgroundIndex = silhouettes.length-1;
		if(arg0.getSource() == prevHair)
			hairIndex--;
		if(hairIndex < 0)
			hairIndex = hairdos.length-1;
		/*if(arg0.getSource() == prevEyes)
			expressionIndex--;
		if(expressionIndex < 0)
			expressionIndex = expressions.length-1;
		if(arg0.getSource() == prevNose)
			noseIndex--;
		if(noseIndex < 0)
			noseIndex = noses.length-1;*/
		if(arg0.getSource() == prevShirt)
			shirtIndex--;
		if(shirtIndex < 0)
			shirtIndex = shirts.length-1;

		if(arg0.getSource() == random)
		{
			randomize();
			String randomString = "%"+silhouettes[backgroundIndex] +"%"+ hairdos[hairIndex] +"%"+ shirts[shirtIndex];
			int duration = (int) (( Calendar.getInstance().getTimeInMillis() - startTime)/1000);
			/**
			 *  Log  randomize event. 
			 */
        	logger.simStShortLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.RANDOMIZE_ACTION," ",randomString, duration);
		}

        final ImageIcon icon = createImageIcon(silhouettes[backgroundIndex]);
        final ImageIcon icon2 = createImageIcon(hairdos[hairIndex]);
        //final ImageIcon icon3 = createImageIcon(expressions[expressionIndex]);
        //final ImageIcon icon4 = createImageIcon(noses[noseIndex]);
        final ImageIcon icon5 = createImageIcon(shirts[shirtIndex]);
        final ImageIcon icon6 = createImageIcon(faces[faceIndex]);
        
        backgroundLabel.setIcon(icon);
        hairLabel.setIcon(icon2);
        //expressionLabel.setIcon(icon3);
        //noseLabel.setIcon(icon4);
        shirtLabel.setIcon(icon5);
        faceLabel.setIcon(icon6);
		
	}

	
	
}
