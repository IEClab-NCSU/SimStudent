package pact.CommWidgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import pact.CommWidgets.event.StudentActionEvent;
import edu.cmu.pact.ctat.MessageObject;     // import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.BehaviorRecorder.Dialogs.EditAudioButtonDialog;
import edu.cmu.pact.Utilities.DelayedAction;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

public class JCommAudioButton extends JCommWidget implements ActionListener{

//	private static final String UPDATE_ICON = "UpdateIcon";
//	private static final String UPDATE_TEXT = "UpdateText";

//	private static final String UPDATE_INVISIBLE = "UpdateInVisible";
	protected JButton button;
	protected JPanel container;
	protected boolean changeColor;
	protected Font startFont;
	protected boolean locked;
	protected int     maxNumberPlay = -1;
	protected int     numberPlay = 0;
	public String     imageName = "JCommAudioButton.gif";
	public String       audioFileName;
    public ImageIcon  defaultIcon;
    
    //////////////////////////////////////////////////////
	/**
		Constructor
	*/
	//////////////////////////////////////////////////////
	public JCommAudioButton() {

		setLayout(new GridLayout(1, 1));
		button = new JButton();
		button.addActionListener(this);
		add(button);

		backgroundNormalColor = button.getBackground();

		setActionName(PLAY_AUDIO);
		locked = false;

		changeColor = true;
		addFocusListener(this);
		originalBorder = button.getBorder();
//		setText ("Comm Audio Button");
//		setImageName(imageName);


//		setIcon(createImageIcon("images/sound.gif","Sound"));
		setIcon(createImageIcon(imageName,imageName));
//		setImage(createImageIcon("images/sound.jpg","Sound").getImage());
		setAudioFileName("JavaWelcome.wav");
//		setAudioFileName(button.getLabel());
//		setIcon("");
/*		Image image = null;
 		if (new File(imageName).canRead())
			image = Toolkit.getDefaultToolkit().getImage(imageName);
 		else {
 			URL imageURL = Utils.getURL(imageName, this);
 			if (imageURL != null)
				image = Toolkit.getDefaultToolkit().getImage(imageURL);
 			else {
 				trace.err("Error: cannot find icon " + imageName);
 				return;
 			}
 		}
		defaultIcon = new ImageIcon(image, imageName);
		setIcon(defaultIcon);*/
	}
        
	  /** Returns an ImageIcon, or null if the path was invalid. */
	  protected static ImageIcon createImageIcon(String path, String description) {
	    java.net.URL imageURL = JCommPicture.class.getResource(path);

	    if (imageURL == null) {
	      System.err.println("Resource not found: " + path);
	      return null;
	    } else {
	      return new ImageIcon(imageURL, description);
	    }
	  }
	  
	//////////////////////////////////////////////////////
	/**
		Returns a comm message which describes this interface
		element.
	*/
	//////////////////////////////////////////////////////
	public MessageObject getDescriptionMessage() {

		if (!initialize(getController())) {
			trace.out(
				5,
				this,
				"ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}

        MessageObject mo = MessageObject.create("InterfaceDescription");
        mo.setVerb("SendNoteProperty");

		mo.setProperty("WidgetType", "JCommAudioButton");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));
		
		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();
		
		if(deftemplates != null)  mo.setProperty("jessDeftemplates", deftemplates);
		
		if(instances != null)    mo.setProperty("jessInstances", instances);
		
		serializeGraphicalProperties(mo);

		return mo;
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.JCommWidget#createJessDeftemplates()
	 */
	public Vector createJessDeftemplates() {
		Vector deftemplates = new Vector();
		
		String deftemplateStr = "(deftemplate audioButton (slot name))";
		deftemplates.add(deftemplateStr);
		
		return deftemplates;
	}
	
	/* (non-Javadoc)
	 * @see pact.CommWidgets.JCommWidget#createJessInstances()
	 */
	public Vector createJessInstances() {
		Vector instances = new Vector();
		
		String instanceStr = "(assert (audioButton (name " + commName + ")))";
		instances.add(instanceStr);
		
		return instances;
	}

	public void addMouseListener(MouseListener l) {
		if (button != null)
			button.addMouseListener(l);
	}

	public void addMouseMotionListener(MouseMotionListener l) {
		if (button != null)
			button.addMouseMotionListener(l);
	}

	public MouseListener[] getMouseListeners() {
		if (button != null)
			return button.getMouseListeners();
		return null;
	}

	public MouseMotionListener[] getMouseMotionListeners() {
		if (button != null)
			return button.getMouseMotionListeners();
		return null;
	}

	public void removeMouseMotionListener(MouseMotionListener l) {
		if (button != null)
			button.removeMouseMotionListener(l);
	}

	public void removeMouseListener(MouseListener l) {
		if (button != null)
			button.removeMouseListener(l);
	}

	//////////////////////////////////////////////////////
	/**
		Used to process an InterfaceAction message
	*/
	//////////////////////////////////////////////////////
	public void doInterfaceAction(
		String selection,
		String action,
		String input) {

		if (action.equalsIgnoreCase(UPDATE_AUDIO)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Load Audio: " + input);
			setAudioFileName(input);
			numberPlay = 0;
		} else if (action.equalsIgnoreCase(UPDATE_ICON)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Load icon: " + input);
			setImageName(input);
		} else if (action.equalsIgnoreCase(UPDATE_TEXT)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "UpdateText: " + input);
			setText(input);
		}
		else if (action.equalsIgnoreCase(UPDATE_INVISIBLE)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Set InVisible: "+input);
			if (input.equalsIgnoreCase("true"))
				setInvisible(true);
			else setInvisible(false);
			setVisible(!isInvisible());
			// setInvisible(input);
		}
//		else  {  //  if (action.equalsIgnoreCase(Button_Pressed)) 
//			SimpleAudioPlayer(getAudioFileName());
//			System.err.println("Play sound");
//		}

	}

	public void doLISPCheckAction(String selection, String input) {
		//if (changeColor && !commName.equalsIgnoreCase("Done"))
		if (changeColor)
			button.setForeground(LISPCheckColor);
                
		if (getController().getUniversalToolProxy().lockWidget()) {
			if (!(commName.equalsIgnoreCase("Hint")
				|| commName.equalsIgnoreCase("Help")
				|| commName.equalsIgnoreCase("Done"))) {
                                setFocusable(false);
				locked = true;
                        }
		}
	}

	public void setChangeButtonColor(boolean changeFlag) {
		this.changeColor = changeFlag;

	}

	public boolean getChangeButtonColor() {
		return this.changeColor;
	}

	public void doCorrectAction(String selection, String action, String input) {

		 if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		} else {

			if (trace.getDebugCode("inter")) trace.out("inter", "doCorrectAction Play Audio: " + input);
			// setAudioFileName(new File(input));

			if (changeColor) {
				// if (!commName.equalsIgnoreCase("Done"))
				button.setForeground(correctColor);

				if (correctFont != null)
					button.setFont(correctFont);
			}

			if (getController().getUniversalToolProxy().lockWidget()) {
				if (!(commName.equalsIgnoreCase("Hint")
						|| commName.equalsIgnoreCase("Help") || commName
						.equalsIgnoreCase("Done"))) {
					setFocusable(false);
					locked = true;
				}
			}

			DelayedAction dA = new DelayedAction(new Runnable() {
				public void run() {
					SimpleAudioPlayer(getAudioFileName());
					System.err.println("Play sound");

				}
			});

			dA.setDelayTime(0);
			dA.start();

			// Added 9/9/06 for CL 2006 integration.
			fireStudentAction(new StudentActionEvent(this));
		}
	}

	public void doIncorrectAction(String selection, String input) {
		if (changeColor) {
			button.setForeground(incorrectColor);

			if (incorrectFont != null)
				button.setFont(incorrectFont);
		}

		if (locked)
			locked = false;
                
                setFocusable(true);
	}

	//////////////////////////////////////////////////////
	/**
		Called when button pressed.  Sends event to Lisp.
	*/
	//////////////////////////////////////////////////////
	public void actionPerformed(ActionEvent e) {
		// trace.out ("mps", "action performed");
		removeHighlight(commName);
		numberPlay++;
		if (trace.getDebugCode("br")) trace.out("br", "numberPlay = " + numberPlay);


		if (getController().isDefiningStartState()) {

			JFrame frame = new JFrame("Modify Label Text");

			String currentDir = System.getProperty("user.dir");

			String title = "Please set the Label for widget " + commName
					+ " : ";
			EditAudioButtonDialog t = new EditAudioButtonDialog(frame, title,
					getText(), getIcon(), getAudioFileName().toString(),
					currentDir, isInvisible(), true);

			if (trace.getDebugCode("inter")) trace.out("inter", ">>> " + getText() + " -> " + t.getNewLabel());
			if (t.getIcon() != null)
				if (trace.getDebugCode("inter")) trace.out("inter", ">>> " + getIcon() + " -> "
						+ t.getIcon().toString());
			if (t.getAudioFileName() != null)
				if (trace.getDebugCode("inter")) trace.out("inter", ">>> " + getAudioFileName() + " -> "
						+ t.getAudioFileName().toString());

			if (t.getAudioFileName() != null) { // &&
												// !t.getAudioFileName().equals(getAudioFileName()))
												// {
				setAudioFileName(t.getAudioFileName());
				dirty = true;
				setActionName(UPDATE_AUDIO);
				sendValue();
			}

			if (!t.getNewLabel().equals(getText())) {
				setText(t.getNewLabel());
				dirty = true;
				setActionName(UPDATE_TEXT);
				sendValue();
			}

			if (t.getIcon() != null) {
				setIcon(t.getIcon());
				setImageName(t.getIcon().toString());

				dirty = true;
				setActionName(UPDATE_ICON);
				sendValue();
			}
			
			if (isInvisible()  !=  t.isInvisible()) {
				setInvisible(t.isInvisible());
				setVisible(!isInvisible());
				dirty = true;
				setActionName(UPDATE_INVISIBLE);
				sendValue();
			}

			// trace.out ("mps", "JCommAudioButton New Label = " +
			// t.getNewLabel());
			setActionName(PLAY_AUDIO); // set to default action to get
											// default input (-1)
			numberPlay = 0;
		} 
		else if ((getMaxNumberPlay() == -1) || (numberPlay < getMaxNumberPlay())) {
			System.err.println("[ " + numberPlay + " , " + maxNumberPlay + " ]");
			dirty = true;
			sendValue();
			SimpleAudioPlayer(getAudioFileName());
			return;
		} else {
			System.err.println("[ " + numberPlay + " , " + maxNumberPlay + " ]");
//			dirty = true;
//			sendValue();
//			SimpleAudioPlayer(getAudioFileName());
		}
}


	// ////////////////////////////////////////////////////
	/**
	 * Return true if any cells are not empty, otherwise false
	 */
	//////////////////////////////////////////////////////
	public boolean isChangedFromResetState() {
		if (getImageName() != null)
			return true;
		else if (getText() != null && getText().length() > 0)
			return true;
		else
			return false;
	}
	
	
	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////

	
	public Object getValue() {
		String tempValue = getText();

		if (getActionName().equalsIgnoreCase(UPDATE_ICON)
				&& getImageName() != null)
			return imageName;

		else if ((getActionName().equalsIgnoreCase(UPDATE_AUDIO) ||
				(getActionName().equalsIgnoreCase(PLAY_AUDIO))
				)
				&& getAudioFileName() != null && getAudioFileName().length() > 0)
			return getAudioFileName().toString();
		else if (getActionName().equalsIgnoreCase(UPDATE_INVISIBLE))
			return isInvisible();
		else {
			if (commName.equalsIgnoreCase("Hint")
					|| commName.equalsIgnoreCase("Help")
					|| commName.equalsIgnoreCase("Done"))

				return "-1";

			else
				return tempValue;
		}

	}

	public void reset(TutorController controller) {
		initialize(controller);

		if (changeColor) {
			button.setForeground(startColor);

			if (startFont != null)
				button.setFont(startFont);
		}

		locked = false;
                
                setFocusable(true);
	}

	public boolean getLock(String selection) {
		return locked;
	}
	
	//////////////////////////////////////////////////////
	/**
		Creates a vector of comm messages which describe the
		current state of this object relative to the start state
	*/
	//////////////////////////////////////////////////////
	public Vector getCurrentState() {

		Vector v = new Vector();

	    if (getText() != null && getText().length() > 0) {
	    	setActionName(UPDATE_TEXT);
			v.addElement(getCurrentStateMessage());
	    }
	    else if (getAudioFileName() != null) {
	    	setActionName(UPDATE_AUDIO);
			v.addElement(getCurrentStateMessage());
	    }
	    else if (imageName != null) {
			setActionName(UPDATE_ICON);
			v.addElement(getCurrentStateMessage());
		}
	    else if (isInvisible()) {

			setActionName(UPDATE_INVISIBLE);
			v.addElement(getCurrentStateMessage());
		}
		setActionName(PLAY_AUDIO); // set to default action to get default input (-1)

		return v;
	}
	
	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setText(String text) {
		button.setText(text);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public String getText() {
		return button.getText();
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setToolTipText(String text) {
		button.setToolTipText(text);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public String getToolTipText() {
		return button.getToolTipText();
	}
	//////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////
	public void highlight(String subElement, Border highlightBorder) {
	    	button.setBorder(highlightBorder);
	}

	//////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////
	public void removeHighlight(String subElement) {
	    button.setBorder(originalBorder);
	}
	

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setImageName(String imageName) {
//		System.err.println("setImageName = " + imageName + this.getClass().getResource(name));
		this.imageName = imageName;
		if (imageName == null || imageName.length() < 1) {
			if (button != null)
				button.setIcon(null);
//			setActionName(PLAY_AUDIO);
			return;
		}
		
		Image image = null;
		File  imageFile = new File(imageName);
 		if  (imageFile.canRead())
			image = Toolkit.getDefaultToolkit().getImage(imageName);
 		else {
 			URL imageURL = Utils.getURL(imageName, this);
 			if (imageURL != null)
				image = Toolkit.getDefaultToolkit().getImage(imageURL);
 			else {
 				trace.err("Error: cannot find icon " + imageFile.getAbsolutePath());
 				
 				return;
 			}
 		}
 		
		ImageIcon icon = new ImageIcon(image, imageName);
		if (button != null) {
			button.setIcon(icon);
//			setActionName(UPDATE_ICON);
		}
	}
	
	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public String getImageName() {
		return imageName;
	}
	
	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setFocus(String subWidgetName) {
		button.requestFocus();
		return;
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setBackground(Color c) {
		if (button != null)
			button.setBackground(c);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setIcon(Icon icon) {
		if (button != null)
			button.setIcon(icon);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public Icon getIcon() {
		if (button != null)
			return button.getIcon();
		else
			return null;
	}

        
        public void moveFocus() {
            button.transferFocus();
        }
	
        
        //////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setFont(Font f) {
		if (f != null) {
			startFont = f;
		} else
			startFont = super.getFont();

		super.setFont(startFont);

		if (button != null)
			button.setFont(startFont);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void addFocusListener(FocusListener l) {
		super.addFocusListener(l);
		if (button != null)
			button.addFocusListener(l);
	}


    public void doClick() {
        button.doClick();
    }


	public String getAudioFileName() {
		return audioFileName;
	}


	public void setAudioFileName(String audioFileName) {
		this.audioFileName = audioFileName;
	}
	
	 private AudioFormat getAudioFormat(){
		    float sampleRate = 22050.0F;     // 22050
		    int sampleSizeInBits = 16;
		    int channels = 1;
		    boolean signed = true;
		    boolean bigEndian = false;
		    return new AudioFormat(
		                      sampleRate,
		                      sampleSizeInBits,
		                      channels,
		                      signed,
		                      bigEndian);
		  }//end getAudioFormat


	 
	public void SimpleAudioPlayer(String audioFileName)
	{
		final int EXTERNAL_BUFFER_SIZE = 128000;
		URL url = null;
		File soundFile = new File(audioFileName);

		/*
		 * We have to read in the sound file.
		 */
		AudioInputStream audioInputStream = null;
		try {
			
			  audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			  
		} catch (Exception ee) {
			/*
			 * In case of an exception, we dump the exception including the
			 * stack trace to the console output. Then, we exit the program.
			 */
			// ee.printStackTrace();
//			
			url = Thread.currentThread().getContextClassLoader().getResource(getAudioFileName()); // ChineseCharacters/01_shou/01.GIF
			try {
			audioInputStream = AudioSystem.getAudioInputStream(url);
			
			} catch (Exception e2) {
				String msg = "Warning: Can't find Audio File from " + soundFile.getAbsolutePath() ;
				JOptionPane.showMessageDialog(
						null,
						msg,
						"Warning", JOptionPane.WARNING_MESSAGE);
			}
			
//			String msg = "Warning: Can't find Audio File " + soundFile.getAbsolutePath() + 
//			         ",\n          you can edit audio file at start state.";
//			JOptionPane.showMessageDialog(
//					null,
//					msg,
//					"Warning", JOptionPane.WARNING_MESSAGE);
//			return;
		}

		/*
		 * From the AudioInputStream, i.e. from the sound file, we fetch
		 * information about the format of the audio data. These information
		 * include the sampling frequency, the number of channels and the size
		 * of the samples. These information are needed to ask Java Sound for a
		 * suitable output line for this audio file.
		 */
		AudioFormat audioFormat = audioInputStream.getFormat();

		/*
		 * Asking for a line is a rather tricky thing. We have to construct an
		 * Info object that specifies the desired properties for the line.
		 * First, we have to say which kind of line we want. The possibilities
		 * are: SourceDataLine (for playback), Clip (for repeated playback) and
		 * TargetDataLine (for recording). Here, we want to do normal playback,
		 * so we ask for a SourceDataLine. Then, we have to pass an AudioFormat
		 * object, so that the Line knows which format the data passed to it
		 * will have. Furthermore, we can give Java Sound a hint about how big
		 * the internal buffer for the line should be. This isn't used here,
		 * signaling that we don't care about the exact size. Java Sound will
		 * use some default value for the buffer size.
		 */
		SourceDataLine line = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat);
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);

			/*
			 * The line is there, but it is not yet ready to receive audio data.
			 * We have to open the line.
			 */
			line.open(audioFormat);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			// System.exit(1);
		}

		/*
		 * Still not enough. The line now can receive data, but will not pass
		 * them on to the audio output device (which means to your sound card).
		 * This has to be activated.
		 */
		line.start();

		/*
		 * Ok, finally the line is prepared. Now comes the real job: we have to
		 * write data to the line. We do this in a loop. First, we read data
		 * from the AudioInputStream to a buffer. Then, we write from this
		 * buffer to the Line. This is done until the end of the file is
		 * reached, which is detected by a return value of -1 from the read
		 * method of the AudioInputStream.
		 */
		int nBytesRead = 0;
		byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
		while (nBytesRead != -1) {
			try {
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (nBytesRead >= 0) {
				int nBytesWritten = line.write(abData, 0, nBytesRead);
			}
		}

		/*
		 * Wait until all data are played. This is only necessary because of the
		 * bug noted below. (If we do not wait, we would interrupt the playback
		 * by prematurely closing the line and exiting the VM.)
		 * 
		 * Thanks to Margie Fitch for bringing me on the right path to this
		 * solution.
		 */
		line.drain();

		/*
		 * All data are played. We can close the shop.
		 */
		line.close();

		/*
		 * There is a bug in the jdk1.3/1.4. It prevents correct termination of
		 * the VM. So we have to exit ourselves.
		 */
		// System.exit(0);
	}


	public int getMaxNumberPlay() {
		return maxNumberPlay;
	}


	public void setMaxNumberPlay(int maxNumberPlay) {
		this.maxNumberPlay = maxNumberPlay;
	}

	public void setNumberPlay(int numberPlay) {
		this.numberPlay = numberPlay;
	}

	public void mousePressed(MouseEvent e) {
	}
}
