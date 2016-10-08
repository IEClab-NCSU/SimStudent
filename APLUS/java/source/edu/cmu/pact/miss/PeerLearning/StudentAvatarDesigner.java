package edu.cmu.pact.miss.PeerLearning;

import java.awt.Dimension;
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

import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.miss.SimSt;


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
    
    JPanel optionPanel, buttonPanel;
    
    protected void createOptionPanel()
    {
    	optionPanel = new JPanel();
    	
    	optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.PAGE_AXIS));
        
        JPanel skinPanel = new JPanel();
        skinPanel.setLayout(new BoxLayout(skinPanel, BoxLayout.X_AXIS));
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
    
    protected void createButtonPanel()
    {
    	buttonPanel = new JPanel();
    	
    	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
 
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel,BoxLayout.X_AXIS));
        JLabel nameLabel = new JLabel(" Student Name: ");
        
        buttonPanel.add(namePanel);
        namePanel.add(nameLabel);
        name = new JTextField();
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(name); }
        namePanel.add(name);
        
		random = new JButton("I'm feeling lucky!");
		buttonPanel.add(random);
		random.addActionListener(this);

		save = new JButton("Save");
		buttonPanel.add(save);
		save.addActionListener(this);
		

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
        layeredPane.setPreferredSize(new Dimension(StudentAvatarDisplay.PREFERRED_WIDTH, StudentAvatarDisplay.PREFERRED_HEIGHT));

        backgroundLabel = new JLabel(icon);
        if (icon != null) {
            backgroundLabel.setBounds(0, 0,
                                icon.getIconWidth(),
                                icon.getIconHeight());
        } 
        layeredPane.add(backgroundLabel, new Integer(2), 0);

        hairLabel = new JLabel(icon2);
        if (icon2 != null) {
            hairLabel.setBounds(0, 0,
                                icon.getIconWidth(),
                                icon.getIconHeight());
        } 
        layeredPane.add(hairLabel, new Integer(3), 0);

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
        layeredPane.add(shirtLabel, new Integer(1), 0);

        faceLabel = new JLabel(icon6);
        if (icon != null) {
            faceLabel.setBounds(0, 0,
                                icon.getIconWidth(),
                                icon.getIconHeight());
        } 
        layeredPane.add(faceLabel, new Integer(3), 0);


    	
    }
    
    public JPanel getOptionPanel()
    {
    	return optionPanel;
    }
    public JPanel getButtonPanel()
    {
    	return buttonPanel;
    }
    public JLayeredPane getLayeredImage()
    {
    	return layeredPane;
    }
    
    public void setup()
    {
    	startTime = Calendar.getInstance().getTimeInMillis();
    	
    	setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

    	createOptionPanel();
    	add(getOptionPanel());
        
        randomize();
        
        createLayeredImage();

        add(Box.createRigidArea(new Dimension(0, 10)));
        //add(Box.createRigidArea(new Dimension(0, 10)));
        add(getLayeredImage());
        
        createButtonPanel();
        add(getButtonPanel());
        
        frame.setPreferredSize(new Dimension(300,400));
        
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
		backgroundIndex = (int) (Math.random()*silhouettes.length);
		hairIndex = (int) (Math.random()*hairdos.length);
		//expressionIndex = (int) (Math.random()*expressions.length);
		//noseIndex = (int) (Math.random()*noses.length);
		shirtIndex = (int) (Math.random()*shirts.length);		
    }
    
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == save)
		{
			int acceptableNameLength = 16;
			String saveString = "%";
			saveString += silhouettes[backgroundIndex]+"%";
        		saveString += hairdos[hairIndex]+"%";
        		//saveString += expressions[expressionIndex]+"%";
       		//saveString += noses[noseIndex]+"%";
        		saveString += shirts[shirtIndex];
			//System.out.println(saveString);
        	SimStPLE.STUDENT_IMAGE = saveString;
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
	        		String msg = ((AplusPlatform)gui).getSpeechText().getText();
	        		msg = msg.replaceAll("[Jj][Oo][Ee][:]","");
	        		msg = msg.replace('\n', ' ');
	        		((AplusPlatform)gui).clearSpeech();
	        		((AplusPlatform)gui).appendSpeech(msg, SimSt.SimStName);
	        	}
        	}
        	frame.setVisible(false);
        	
        	SimStPLE.saveAccountFile(accountName+".account");
        	
        	int duration = (int) ( Calendar.getInstance().getTimeInMillis() - startTime);
        	
        	logger.simStShortLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.AVATAR_SAVE_ACTION, name.getText(),saveString, duration);
        	
        	((AplusPlatform) gui).showSplashScreen();
        	
        	
			return;
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
