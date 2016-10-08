package edu.cmu.pact.BehaviorRecorder.Dialogs;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

public class EditAudioButtonDialog extends JDialog implements ActionListener {
	
	private JCheckBox  InvisibleCheckBox;
	private JButton replace;
	private JButton cancel;
	private JButton browseAudio;
	private JButton browseIcon;
	
	private JTextField newLabelText;
	private JTextField iconImageFile;
	private JTextField newAudioFile;
	
    private String newLabel;
    private String audioFileName;
	private ImageIcon icon = null;
	private String currentDirectory;
	private boolean   invisible;
	
	private String defaultnewLabelText = "";
	private String defaultnewAudioFile = "";
	
	public EditAudioButtonDialog(JFrame parent, String title, String oldLabel, Icon oldIcon, String oldAudioFileName, String currentDirectory, boolean invisible, boolean modal)
	{

		super(parent, title, modal);
		newLabel = oldLabel;
		this.currentDirectory = currentDirectory;
	    
		JPanel mainPane = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 2;
		c.insets = new Insets(5, 5, 5, 5);
		
		c.gridx = 0;
		c.gridy = 0;
		

		mainPane.add(new JLabel("New Text: "), c);
		
		newLabelText = new JTextField(newLabel, 25);
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(newLabelText); }
		newLabelText.setName("newLabelText");
		c.gridy = 1;
		c.gridwidth = 3;
		mainPane.add(newLabelText, c);
		
		c.gridy = 2;
		mainPane.add(new JLabel("Image File:"), c);
		
		browseIcon = new JButton("Browse");
		browseIcon.addActionListener(this);
		c.gridx = 3;
		c.gridy = 3;
		mainPane.add(browseIcon, c);
		
		if (oldIcon != null)
			iconImageFile = new JTextField(oldIcon.toString(), 25);
		else
			iconImageFile = new JTextField(25);
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(iconImageFile); }
		iconImageFile.setName("iconImageFile");
		c.gridx = 0;
		c.gridwidth = 3;
		mainPane.add(iconImageFile, c);


		

		
		c.gridx = 0;
		c.gridy = 4;
		

		mainPane.add(new JLabel("New Audio File: "), c);
		
		if (oldAudioFileName != null)
			newAudioFile = new JTextField(oldAudioFileName, 25);
		else
			newAudioFile = new JTextField(audioFileName, 25);
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(newAudioFile); }
		
		newAudioFile.setName("newAudioFile");
		c.gridy = 5;
		c.gridwidth = 3;
		mainPane.add(newAudioFile, c);
		
		browseAudio = new JButton("browseAudio");
		browseAudio.addActionListener(this);
		
		c.gridx = 3;
		c.gridy = 5;
		mainPane.add(browseAudio, c);
		
		InvisibleCheckBox = new JCheckBox("Invisible", null, invisible);
		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 1;
		mainPane.add(InvisibleCheckBox, c);
		
		//the buttons
		replace = new JButton("Replace");
		replace.addActionListener(this);
		c.gridx = 2;
		c.gridy = 9;
		c.gridwidth = 1;
		mainPane.add(replace, c);
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		c.gridx = 3;
		mainPane.add(cancel, c);
		
		setContentPane(mainPane);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null); //umm should this be relative to something else??
		pack();
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == browseAudio)
		{
		    class SoundFilter extends FileFilter {
			      public boolean accept(File f) {
			        String name = f.getName().toLowerCase();
			        return f.isDirectory() || name.endsWith(".aif")
			        		|| name.endsWith(".aiff")
			        		|| name.endsWith(".wav");
			      }
			      public String getDescription() {
			    	  return "Sound Files";}
			    };
		    
			File f = DialogUtilities.chooseFile(currentDirectory, new SoundFilter(),
					"Choose a sound file", "Open", null);
	        if(f != null)
	        {
	        	newAudioFile.setText(f.getAbsolutePath());
	        	trace.err("Save newAudioFile" + newAudioFile.getText());
	        	setAudioFileName(f.getAbsolutePath());
	        }
	    }
		else if (ae.getSource() == browseIcon)
		{
		    class ImageFilter extends FileFilter {
			      public boolean accept(File f) {
			        String name = f.getName().toLowerCase();
			        return f.isDirectory() || name.endsWith(".gif") || name.endsWith(".png") || name.endsWith(".jpg");
			      }
			      public String getDescription() {
			    	  return "Image Files";
			      }
		    };
			File f = DialogUtilities.chooseFile(currentDirectory, new ImageFilter(),
					"Choose an image file", "Open", null);
	        if(f != null)
	        	iconImageFile.setText(f.getAbsolutePath());
	    }
		else if(ae.getSource() == cancel)
		{
			setVisible(false);
			dispose();
		}
		else if(ae.getSource() == replace)
		{
			String[] args = new String[3];
			args[0] = newLabelText.getText();
//			if ((args[0] == null) || (audioFileName.equals(args[0]))) {
//				args[1] = iconImageFile.getText();
//				
//		        ImageIcon icon = createImageIcon(args[1],   // "images/middle.gif", // 
//                "a pretty but meaningless splat");
		        
//				setIcon(icon);

//			}
//			else setaudioFileName(args[0]);
			
//			if (!audioFileName.equals(args[0])) setaudioFileName(args[0]);
//			args[1] = iconImageFile.getText();
//			ImageIcon icon = createImageIcon(args[1],   // "images/middle.gif", // 
//	                "a pretty but meaningless splat");
//			setIcon(icon);
			String newLabel = newLabelText.getText();
			if (!newLabel.equals(""))  setNewLabel(newLabelText.getText());
			
			String audioFileName = newAudioFile.getText();
        	
			if (!audioFileName.equals(""))  setAudioFileName(newAudioFile.getText());
			trace.err("Replace newAudioFile to " + this.getAudioFileName());
			String newImageFileName = iconImageFile.getText();
			if (!newImageFileName.equals("")) {
			ImageIcon icon = createImageIcon(newImageFileName,    
            "a pretty but meaningless splat");
			setIcon(icon);
			
			}
			setInvisible(InvisibleCheckBox.isSelected());
			setVisible(false);
			dispose();
		}
	}

	public String getAudioFileName() {
		return audioFileName;
	}

	public void setAudioFileName(String audioFileName) {
		this.audioFileName = audioFileName;
	}
	
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected  ImageIcon createImageIcon(String path,
                                               String description) {
    	File imgFile = null;
 
    	try {
    		 imgFile = new File(path);
    		 if (imgFile.exists()) {
    			 String newName = imgFile.getCanonicalPath().replace('\\', '/');
    			 return new ImageIcon(newName);
    		 } else {
    			 java.net.URL newName = Utils.getURL(path, this);
    			 if (newName == null) 
    				 newName = EditAudioButtonDialog.class.getResource(path); // Get image from resource .jar file
    			 if (newName != null) return new ImageIcon(newName);
    			 else return null;
    		 }
    		 
    	}
    	catch (Exception e) { 
    		trace.err("Can't find file" + path);
    		return null;
    	}
    }

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
	
	public String getNewLabel() {
		return newLabel;
	}

	public void setNewLabel(String newLabel) {
		this.newLabel = newLabel;
	}
	
	public boolean isInvisible() {
		return invisible;
	}

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}
}
