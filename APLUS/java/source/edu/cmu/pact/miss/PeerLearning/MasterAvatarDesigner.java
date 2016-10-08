package edu.cmu.pact.miss.PeerLearning;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.storage.StorageClient;

public class MasterAvatarDesigner extends StudentAvatarDesigner implements DocumentListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected JTextField userID;
	protected JButton load;
	protected JLabel status;
	protected boolean webRetrieve = false;
	protected boolean webConnect = true;
	
    public void setup()
    {
    	
    	setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        add(Box.createRigidArea(new Dimension(10, 10)));
    	createOptionPanel();
    	add(getOptionPanel());
        
        randomize();
        
        createLayeredImage();

        add(Box.createRigidArea(new Dimension(10, 10)));
        
        JPanel avatarPanel = new JPanel();
        avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.X_AXIS));

        avatarPanel.add(Box.createRigidArea(new Dimension(30, 10)));
        
        avatarPanel.add(getLayeredImage());

		random = new JButton("Randomize");
		avatarPanel.add(random);
		random.addActionListener(this);

        avatarPanel.add(Box.createRigidArea(new Dimension(70, 10)));
        
        add(avatarPanel);
        
        createButtonPanel();
        add(getButtonPanel());

        add(Box.createRigidArea(new Dimension(100, 10)));
        status = new JLabel("Program Started");
        add(status);
        
        setBorder(BorderFactory.createLineBorder(Color.black, 1));
        
        frame.setPreferredSize(new Dimension(350,360));
        
        frame.setLocation(50,50);
    }
	
    @Override
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
        name.getDocument().addDocumentListener(this);
        namePanel.add(name);
        
		JPanel saveLoadPanel = new JPanel();
		saveLoadPanel.setLayout(new BoxLayout(saveLoadPanel, BoxLayout.X_AXIS));

		JLabel userIDLabel = new JLabel(" User ID: ");
		saveLoadPanel.add(userIDLabel);
		
		userID = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(userID); }
		saveLoadPanel.add(userID);
		userID.getDocument().addDocumentListener(this);
		
		load = new JButton("Load");
		saveLoadPanel.add(load);
		load.addActionListener(this);
		
		save = new JButton("Save");
		saveLoadPanel.add(save);
		save.addActionListener(this);
		
		buttonPanel.add(saveLoadPanel);

    }
    
	
	protected static void createAndShowGUI() {
        //Create and set up the window.
    	if(frame == null)
    	{
	        frame = new JFrame("Customize Your Student");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	}

        //Create and set up the content pane.
        JComponent newContentPane = new MasterAvatarDesigner();
        newContentPane.setOpaque(true); //content panes must be opaque
        
        frame.getContentPane().add(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == save)
		{
			String saveString = "%";
			saveString += silhouettes[backgroundIndex]+"%";
        		saveString += hairdos[hairIndex]+"%";
        		/*saveString += expressions[expressionIndex]+"%";
       		saveString += noses[noseIndex]+"%";*/
        		saveString += shirts[shirtIndex];
			//System.out.println(saveString);
        	SimStPLE.STUDENT_IMAGE = saveString;
        	if(name.getText().length() > 0)
        	{
	        	SimSt.setSimStName(name.getText());
        	}
        	accountName = userID.getText();
        	if(webConnect)
        	{
        		SimSt.WEBSTARTENABLED = true;
        		status.setText("Saved Account "+accountName+" to Web");
        	}
        	else
        	{
        		SimSt.WEBSTARTENABLED = false;
        		status.setText("Saved Account "+accountName+" to Local File");
        	}
        	SimStPLE.saveAccountFile(accountName+".account");

            setBorder(BorderFactory.createLineBorder(Color.black, 1));
			return;
		}
		if(arg0.getSource() == load)
		{
			loadAccountInfo();
            setBorder(BorderFactory.createLineBorder(Color.black, 1));
            return;
		}
		

        setBorder(BorderFactory.createLineBorder(Color.yellow, 1));
        if(!status.getText().endsWith("*"))
        	status.setText(status.getText()+" *");
		super.actionPerformed(arg0);
		
	}
	
	public void loadAccountInfo() {
		
		String user = userID.getText();
       	String accountInfo = user+".account"; 
		boolean successful = false;
		webRetrieve = false;
		try {
    		// Key associated when retrieving the .account file is the getSimSt().getUserID()+.account
			webRetrieve = new StorageClient().retrieveFile(user+".account", accountInfo,"." );
			webConnect = true;
		} catch (IOException e1) {
			e1.printStackTrace();
			webConnect = false;
		}
    	File accountFile = null;
    	
    		accountFile = new File(accountInfo);
        	if(accountFile != null && accountFile.exists())
        	{
        		successful = true;
            	try {
    				BufferedReader read = new BufferedReader(new FileReader(accountFile));
    				String charName = read.readLine();
    				name.setText(charName);
    				SimSt.setSimStName(charName);
    				String imgName = read.readLine();
    				SimStPLE.STUDENT_IMAGE = imgName;
    				setImage(imgName);
    				
    				String tmp = read.readLine();
    				if(tmp != null && tmp.length() > 0) {
    					SimStPLE.currentOverallProblem = Integer.parseInt(tmp);
    					
    				}
    				read.close();
    			} catch (FileNotFoundException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}

        	}
        	
        	if(!successful)
        	{
        		status.setText(" Account "+user+" Not Found");
        	}
        	else if(webRetrieve)
        	{
        		status.setText("Loaded Account "+user+" from Web");
        	}
        	else
        	{
        		status.setText("Loaded Account "+user+" from Local File");
        	}
    	
    }
	
	public void setImage(String img)
	{
		String[] imgParts = img.split("%");
	
		for(int i=0;i<silhouettes.length;i++)
		{
			if(imgParts[1].equals(silhouettes[i]))
				backgroundIndex = i;
		}
		for(int i=0;i<hairdos.length;i++)
		{
			if(imgParts[2].equals(hairdos[i]))
				hairIndex = i;
		}
		/*for(int i=0;i<expressions.length;i++)
		{
			if(imgParts[3].equals(expressions[i]))
				expressionIndex = i;
		}

		for(int i=0;i<noses.length;i++)
		{
			if(imgParts[4].equals(noses[i]))
				noseIndex = i;
		}*/

		for(int i=0;i<shirts.length;i++)
		{
			if(imgParts[5].equals(shirts[i]))
				shirtIndex = i;
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
	

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		createAndShowGUI();

	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
        setBorder(BorderFactory.createLineBorder(Color.yellow, 1));
        if(!status.getText().endsWith("*"))
        	status.setText(status.getText()+" *");
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
        setBorder(BorderFactory.createLineBorder(Color.yellow, 1));
        if(!status.getText().endsWith("*"))
        	status.setText(status.getText()+" *");
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
        setBorder(BorderFactory.createLineBorder(Color.yellow, 1));
        if(!status.getText().endsWith("*"))
        	status.setText(status.getText()+" *");
	}

}
