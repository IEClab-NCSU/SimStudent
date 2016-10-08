package edu.cmu.pact.BehaviorRecorder.Dialogs;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import pact.CommWidgets.JCommPicture;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

public class EditLabelNameDialog extends JDialog implements ActionListener {
	
	private JFrame frame;
	
	private JCheckBox  InvisibleCheckBox;
	private JButton replace;
	private JButton cancel;
	private JButton browse;
	
	private JTextField newLabelText;
	private JTextField iconImageFile;
    private String newLabel;
    private String imageName;
	private ImageIcon icon = null;
	private String currentDirectory;
//	private JFileChooser chooser;
	private boolean   invisible;
	
	private String defaultnewLabelText = "";
	
	/*public void EditLabelNameDialog(JFrame parent, String title, String oldLabel, Icon oldIcon, String currentDirectory,  boolean modal)
	{
		super(parent, title, modal);
		newLabel = oldLabel;
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		

		chooser.setCurrentDirectory(new File(currentDirectory));   // new File(".")

	    chooser.setFileFilter(new FileFilter() {
	      public boolean accept(File f) {
	        String name = f.getName().toLowerCase();
	//        return name.endsWith(".gif" + ".png"); //  || f.isDirectory();
	        return f.isDirectory() || name.endsWith(".gif") || name.endsWith(".png") || name.endsWith(".jpg");
	      }
	      public String getDescription()
	      {return "Image Files";}//emm not sure why this is needed
	    });
	    
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
		
		browse = new JButton("Browse");
		browse.addActionListener(this);
		c.gridx = 3;
		c.gridy = 3;
		mainPane.add(browse, c);
		
		if (oldIcon != null)
			iconImageFile = new JTextField(oldIcon.toString(), 25);
		else
		    iconImageFile = new JTextField(25);
	    { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(iconImageFile); }
		iconImageFile.setName("iconImageFile");
		c.gridx = 0;
		c.gridwidth = 3;
		mainPane.add(iconImageFile, c);

		
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
		
//		frame = new JFrame("Modify Label Text");
		setContentPane(mainPane);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null); //umm should this be relative to something else??
		pack();
		setVisible(true);
	}
*/	
	/**
	 * Filter for file chooser to screen for .gif, .png, .jpg files.
	 */
	private class ImageFilter extends FileFilter {
		public boolean accept(File f) {
			String name = f.getName().toLowerCase();
			return f.isDirectory() || name.endsWith(".gif")
					|| name.endsWith(".png")
					|| name.endsWith(".jpg");
		}
		public String getDescription() {
			return "Image Files";
		}
	}
	
	public EditLabelNameDialog(JFrame parent, String title, String oldLabel, String oldImageName, String currentDirectory,boolean invisible, boolean modal)
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
		
		browse = new JButton("Browse");
		browse.addActionListener(this);
		c.gridx = 3;
		c.gridy = 3;
		mainPane.add(browse, c);
		
		if (oldImageName != "")
			iconImageFile = new JTextField(oldImageName, 25);
		else
			iconImageFile = new JTextField(25);
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(iconImageFile); }
		iconImageFile.setName("iconImageFile");
		c.gridx = 0;
		c.gridwidth = 3;
		mainPane.add(iconImageFile, c);

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
		
//		frame = new JFrame("Modify Label Text");
		setContentPane(mainPane);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null); //umm should this be relative to something else??
		pack();
		setVisible(true);
	}
	
	public EditLabelNameDialog(JFrame parent, String title, String oldLabel, String oldImageName, String currentDirectory, boolean modal)
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
		
		browse = new JButton("Browse");
		browse.addActionListener(this);
		c.gridx = 3;
		c.gridy = 3;
		mainPane.add(browse, c);
		
		if (oldImageName != "")
			iconImageFile = new JTextField(oldImageName, 25);
		else
			iconImageFile = new JTextField(25);
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(iconImageFile); }
		iconImageFile.setName("iconImageFile");
		c.gridx = 0;
		c.gridwidth = 3;
		mainPane.add(iconImageFile, c);
		
		InvisibleCheckBox = new JCheckBox("Invisible", null, invisible); // but doesn't show
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
		
//		frame = new JFrame("Modify Label Text");
		setContentPane(mainPane);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null); //umm should this be relative to something else??
		pack();
		setVisible(true);
	}
	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == browse)
		{
			File f = DialogUtilities.chooseFile(currentDirectory, null, new ImageFilter(),
					"Find files", "Open", frame);
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
			String[] args = new String[2];
			args[0] = newLabelText.getText();
//			if ((args[0] == null) || (newLabel.equals(args[0]))) {
//				args[1] = iconImageFile.getText();
//				
//		        ImageIcon icon = createImageIcon(args[1],   // "images/middle.gif", // 
//                "a pretty but meaningless splat");
		        
//				setIcon(icon);

//			}
//			else setNewLabel(args[0]);
			
//			if (!newLabel.equals(args[0])) setNewLabel(args[0]);
//			args[1] = iconImageFile.getText();
//			ImageIcon icon = createImageIcon(args[1],   // "images/middle.gif", // 
//	                "a pretty but meaningless splat");
//			setIcon(icon);
			
			String newLabel = newLabelText.getText();
//			if (!newLabel.equals(""))  
			setNewLabel(newLabelText.getText());
			String newImageFileName = iconImageFile.getText();
			if (!newImageFileName.equals("")) {
			setImageName(newImageFileName);
			ImageIcon icon = createImageIcon(newImageFileName,    
            "a pretty but meaningless splat");
			setIcon(icon);
//			invisible = InvisibleCheckBox.isSelected();
			
			}
			setInvisible(InvisibleCheckBox.isSelected());
			setVisible(false);
			dispose();
			
//			if(status != null && !status.equals(""))
//				JOptionPane.showMessageDialog(frame, status, "Warning!", JOptionPane.WARNING_MESSAGE);
//			else
//				JOptionPane.showMessageDialog(frame, "Success!");
		}
	}

	public String getNewLabel() {
		return newLabel;
	}

	public void setNewLabel(String newLabel) {
		this.newLabel = newLabel;
	}
	
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected  ImageIcon oldcreateImageIcon(String path,
                                               String description) {
    	File imgFile = null;
 
    	try {
    		 imgFile = new File(path);
    		 if (imgFile.exists()) {
    			 String newName = imgFile.getCanonicalPath().replace('\\', '/');
    			 return new ImageIcon(newName);
    		 } else {
    			 java.net.URL newName = Utils.getURL(path, this);
    			 return new ImageIcon(newName);
    		 }
    		 
    	}
    	catch (Exception e) { 
    		trace.err("Can't find file " + path);
    		return null;
    	}
    }

    // /Users/chc/Desktop/ChineseCharacters/01_shou/01a.GIF :Creat icon from physical address
    // 01_shou/01a.GIF :Creat icon from physical directory
    // ChineseCharacters/01_shou/01a.GIF : from current directory
    // file:///Users/chc/Desktop/ChineseCharacters/01_shou/01.GIF : from URL


	protected ImageIcon createImageIcon(String imageName, String description) {

		if (imageName == null || imageName.length() < 1)
			return null;

		File imgFile = null;
		URL imageURL = null;

		try {
			if (!imageName.startsWith("file:")) {
				// Try to get image from physical path (from browser) or
				// relative path
				imgFile = new File(imageName);
				if (imgFile.exists()) {
					// String newName = imgFile.getCanonicalPath().replace('\\',
					// '/');
//					System.err.println("Creat icon from physical/relative address");
					// return new ImageIcon(newName);
					return new ImageIcon(imageName);
				}
			}
		} catch (Exception e) {
			trace.err("Can't find file " + imageName);
		}

		try {
			// Try to get image from URL address which start with file:
			imageURL = new URL(imageName);
//			System.err.println("Creat icon from URL address");
		} catch (MalformedURLException mal) {
			if (trace.getDebugCode("log")) trace.out("log", "MalformedURLException message = "
					+ mal.getMessage());
		}

		if (imageURL == null) {
			// Get image from current directory
			imageURL = Utils.getURL(imageName, this);
//			System.err.println("Creat icon from resource .jar file");
		}

		if (imageURL == null) {
			// Get image from resource .jar file ??? may be redundant, need to verified from webstart
			imageURL = JCommPicture.class.getResource(imageName);
//			System.err.println("Creat icon from resource .jar file");
		}

		if (imageURL == null) {
			trace.err("Error: cannot find image "
					+ new File(imageName).getAbsolutePath());

			return null;
		}

		Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
		/*
		 * if (new File(imageName).canRead()) image =
		 * Toolkit.getDefaultToolkit().getImage(imageName); else { imageURL =
		 * Utils.getURL(imageName, this); if (imageURL != null) image =
		 * Toolkit.getDefaultToolkit().getImage(imageURL); else { // try to load
		 * resource which contained in .jar file imageURL =
		 * Thread.currentThread().getContextClassLoader()
		 * .getResource(imageName);
		 * 
		 * if (imageURL == null) { trace.err("Error: cannot find image " + new
		 * File(imageName).getAbsolutePath());
		 * 
		 * return null; } } }
		 */
		ImageIcon imageIcon = new ImageIcon(image, imageName);

		return imageIcon;
	}

	protected ImageIcon savecreateImageIcon(String imageName, String description) {

		if (imageName == null || imageName.length() < 1)
			return null;

		File imgFile = null;
		URL imageURL = null;

		try {
			if (!imageName.startsWith("file:")) {
				// Try to get image from physical path (from browser) or relative path
				imgFile = new File(imageName);
				if (imgFile.exists()) {
//					String newName = imgFile.getCanonicalPath().replace('\\',
//							'/');
					System.err.println("Creat icon from physical address");
//					return new ImageIcon(newName);
					return new ImageIcon(imageName);
				}

			} else {
				// Try to get image from URL address which start with file:
				imageURL = new URL(imageName);

			}

		} 
			catch (MalformedURLException mal) {

			if (trace.getDebugCode("log")) trace.out("log", "MalformedURLException message = "
					+ mal.getMessage());
		} 
		catch (Exception e) {
			trace.err("Can't find file " + imageName);

		}

		if (imageURL == null)
			// Get image from current directory
			imageURL = Utils.getURL(imageName, this); 
		else System.err.println("Creat icon from URL address");												// from
		
		if (imageURL == null)
			// Get image from resource .jar file
			imageURL = JCommPicture.class.getResource(imageName); 
		else System.err.println("Creat icon from current directory");
		
		if (imageURL == null) {
			trace.err("Error: cannot find image "
					+ new File(imageName).getAbsolutePath());

			return null;
		}

		
		Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
		/*
		 * if (new File(imageName).canRead()) image =
		 * Toolkit.getDefaultToolkit().getImage(imageName); else { imageURL =
		 * Utils.getURL(imageName, this); if (imageURL != null) image =
		 * Toolkit.getDefaultToolkit().getImage(imageURL); else { // try to load
		 * resource which contained in .jar file imageURL =
		 * Thread.currentThread().getContextClassLoader()
		 * .getResource(imageName);
		 * 
		 * if (imageURL == null) { trace.err("Error: cannot find image " + new
		 * File(imageName).getAbsolutePath());
		 * 
		 * return null; } } }
		 */
		ImageIcon imageIcon = new ImageIcon(image, imageName);

		return imageIcon;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}



	public boolean isInvisible() {
		return invisible;
	}

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}
}
