package pact.CommWidgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Dialogs.EditLabelNameDialog;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.TutorController;

public class JCommLabel extends JCommWidget {

	// private static final String UPDATE_ICON = "UpdateIcon";
	// private static final String UPDATE_TEXT = "UpdateText";
	// private static final String UPDATE_INVISIBLE = "UpdateInVisible";
	protected JLabel label;
	protected int labelsCreated;
	protected String imageName;

	// ////////////////////////////////////////////////////
	/**
	 * Constructor
	 */
	// ////////////////////////////////////////////////////
	public JCommLabel() {

		setLayout(new GridLayout(1, 1));
		label = new JLabel("JCommLabel");
		add(label);

		// MouseListener listener = new PressMouseAdapter();
		// label.addMouseListener(listener);
		addMouseListener(this);
		setActionName(UPDATE_TEXT);
	}

	/*
	 * private class PressMouseAdapter extends MouseAdapter { public void
	 * mouseClicked(MouseEvent e) { String text = ((JLabel)
	 * e.getComponent()).getText(); // trace.out("inter", "You clicked " + text
	 * + "!"); if (getController().getCtatModeModel().isDefiningStartState()) {
	 * // trace.out ("mps", "JCommButton NEED codes to modify the // btton
	 * Text"); JFrame frame = new JFrame("Modify Label Text");
	 * 
	 * String currentDir = System.getProperty("user.dir");
	 * 
	 * String title = "Please set the Label for widget " + commName + " : ";
	 * EditLabelNameDialog t = new EditLabelNameDialog(frame, title, //
	 * getText(), getIcon(), currentDir, true); getText(), getImageName(),
	 * currentDir, isInvisible(), true);
	 * 
	 * // setText(t.getNewLabel()); // setIcon(t.getIcon());
	 * 
	 * if (!t.getNewLabel().equals(getText())) { setText(t.getNewLabel()); dirty
	 * = true; setActionName(UPDATE_TEXT); sendValue(); }
	 * 
	 * if (t.getIcon() != null) { setIcon(t.getIcon()); //
	 * setImageName(t.getIcon().toString()); setImageName(t.getImageName());
	 * dirty = true; setActionName(UPDATE_ICON); sendValue(); }
	 * 
	 * if (isInvisible() != t.isInvisible()) { setInvisible(t.isInvisible());
	 * dirty = true; setActionName(UPDATE_INVISIBLE); sendValue(); }
	 * 
	 * 
	 * // trace.out ("mps", "JCommButton New Label = " + // t.getNewLabel());
	 * // setText("New Button"); } }
	 * 
	 * }
	 */
	public void addMouseListener(MouseListener l) {
		if (label != null)
			label.addMouseListener(l);
	}

	public void addMouseMotionListener(MouseMotionListener l) {
		if (label != null)
			label.addMouseMotionListener(l);
	}

	public MouseListener[] getMouseListeners() {
		if (label != null)
			return label.getMouseListeners();
		return null;
	}

	public MouseMotionListener[] getMouseMotionListeners() {
		if (label != null)
			return label.getMouseMotionListeners();
		return null;
	}

	public void removeMouseMotionListener(MouseMotionListener l) {
		if (label != null)
			label.removeMouseMotionListener(l);
	}

	public void removeMouseListener(MouseListener l) {
		if (label != null)
			label.removeMouseListener(l);
	}

	// ////////////////////////////////////////////////////
	/**
	 * Returns a comm message which describes this interface element.
	 */
	// ////////////////////////////////////////////////////
	public MessageObject getDescriptionMessage() {

		if (!initialize(getController())) {
			trace
					.out(
							5,
							this,
							"ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}

    	MessageObject mo = MessageObject.create("InterfaceDescription");
    	mo.setVerb("SendNoteProperty");


	mo.setProperty("WidgetType", "JCommLabel");
	mo.setProperty("CommName", commName);
	mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));
	
	Vector deftemplates = createJessDeftemplates();
	Vector instances = createJessInstances();
	
	if(deftemplates != null)  mo.setProperty("jessDeftemplates", deftemplates);

	
	if(instances != null)    mo.setProperty("jessInstances", instances);

	
	serializeGraphicalProperties(mo);

		return mo;
	}

	// sanket@cs.wpi.edu

	public Vector createJessDeftemplates() {
		Vector deftemplates = new Vector();

		String deftemplateStr = "(deftemplate label (slot name) (slot value))";
		deftemplates.add(deftemplateStr);

		return deftemplates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pact.CommWidgets.JCommWidget#createJessInstances()
	 */
	public Vector createJessInstances() {
		Vector instances = new Vector();

		String instanceStr = "(assert (label (name " + commName + ")))";
		instances.add(instanceStr);

		return instances;
	}

	// ////////////////////////////////////////////////////
	/**
	 * Used to process an InterfaceAction message
	 */
	// ////////////////////////////////////////////////////
	public void doInterfaceAction(String selection, String action, String input) {
		// trace.out (5, this, "doInterfaceAction: selection = " + selection);

		if (action.equalsIgnoreCase(UPDATE_ICON)) {
			if (trace.getDebugCode("inter"))
				trace.out("inter", "Load icon: " + input);
			setImageName(input);
		} else if (action.equalsIgnoreCase(UPDATE_TEXT)) {
			if (trace.getDebugCode("inter"))
				trace.out("inter", "UpdateText: " + input);
			setText(input);
		} else if (action.equalsIgnoreCase(UPDATE_INVISIBLE)) {
			if (trace.getDebugCode("inter"))
				trace.out("inter", "Set InVisible: " + input);
			if (input.equalsIgnoreCase("true"))
				setInvisible(true);
			else
				setInvisible(false);
			setVisible(!isInvisible());
			// setInvisible(input);
		} else if (SET_VISIBLE.equalsIgnoreCase(action)) { // suppress feedback
			if (trace.getDebugCode("inter"))
				trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		}
	}

	/**
	 * Process a correct action message.
	 * 
	 * @param selection
	 *            unused
	 * @param input
	 *            new text for the label
	 * @see CommWidgets#doCorrectAction(String,String)
	 */
	public void doCorrectAction(String selection, String action, String input) {
		if (trace.getDebugCode("inter"))
			trace.out("inter", "doCorrectAction(" + input + ") for action "
					+ getActionName());

		if (action.equalsIgnoreCase(UPDATE_ICON))
			setImageName(input);
		else if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter"))
				trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		} else
			setText(input);
	}

	// ////////////////////////////////////////////////////
	/**
	 * Return true if any cells are not empty, otherwise false
	 */
	// ////////////////////////////////////////////////////
	public boolean isChangedFromResetState() {
		if (getImageName() != null)
			return true;
		else if (getText() != null && getText().length() > 0)
			return true;
		else
			return false;
	}

	// ////////////////////////////////////////////////////
	/**
	 * Called by the sendValue method. Always returns the value of the last cell
	 * to get updated by the user.
	 */
	// ////////////////////////////////////////////////////
	public Object getValue() {
		if (getActionName().equalsIgnoreCase(UPDATE_ICON)
				&& getImageName() != null)
			return imageName;

		else if (getActionName().equalsIgnoreCase(UPDATE_TEXT)
				&& getText() != null && getText().length() > 0)
			return getText();
		else if (getActionName().equalsIgnoreCase(UPDATE_INVISIBLE))
			// return isVisible();
			return isInvisible();
		else
			return null;
	}

	// ////////////////////////////////////////////////////
	/**
	 * Creates a vector of comm messages which describe the current state of
	 * this object relative to the start state
	 */
	// ////////////////////////////////////////////////////
	public Vector getCurrentState() {

		if(trace.getDebugCode("label"))
			trace.out("label", "JCLabel.getCurrentState() image "+imageName+", text "+getText()+
					", invisible "+isInvisible());

		Vector v = new Vector();

		if (imageName != null) {
			setActionName(UPDATE_ICON);
			v.addElement(getCurrentStateMessage());
		}
		if (getText() != null && getText().length() > 0) {
			setActionName(UPDATE_TEXT);
			v.addElement(getCurrentStateMessage());
		}
		if (isInvisible()) {

			setActionName(UPDATE_INVISIBLE);
			v.addElement(getCurrentStateMessage());
		}
		return v;
	}

	// ////////////////////////////////////////////////////
	/**
	
	*/
	// ////////////////////////////////////////////////////
	public void setText(String text) {
		if (label != null)
			label.setText(text);
		setActionName(UPDATE_TEXT);
	}

	// ////////////////////////////////////////////////////
	/**
	
	*/
	// ////////////////////////////////////////////////////
	public String getText() {
		if (label != null)
			return label.getText();
		else
			return null;
	}

	// ////////////////////////////////////////////////////
	/**
	
	*/
	// ////////////////////////////////////////////////////
	public void setToolTipText(String text) {
		label.setToolTipText(text);
	}

	// ////////////////////////////////////////////////////
	/**
	
	*/
	// ////////////////////////////////////////////////////
	public String getToolTipText() {
		// trace.out (5, this, "getting tool tiptext");

		return label.getToolTipText();
	}

	// ////////////////////////////////////////////////////
	/**
	
	*/
	// ////////////////////////////////////////////////////
	public void setBackground(Color c) {
		if (label != null)
			label.setBackground(c);
	}

	/**
	 * Retrieve an {@link ImageIcon} instance from the given file or URL and
	 * call {@link #label}.{@link JLabel#setIcon(Icon) setIcon(imageIcon)}.
	 * Also calls {@link #setActionName(String) setActionName}({@value JCommWidget#UPDATE_ICON}).
	 * @param imageName file or URL with the image
	 */
	public void setImageName(String imageName) {
		if (trace.getDebugCode("inter")) trace.out("inter", "0. setImageName : " + imageName);

		if (imageName == null || imageName.length() < 1)
			return;
		String errMsg = null;
		ImageIcon imageIcon = null;
		try {
			if (imageName.startsWith("file:"))
				imageIcon = getIconFromURL(imageName);
			else {
				try {
					imageIcon = getIconFromFile(imageName);
				} catch (Exception e) {
					errMsg = e.getMessage();
				}
				if (imageIcon == null)
					imageIcon = getIconFromResource(imageName);
			}
		} catch (Exception e) {
			if (errMsg == null)          // report only 1st exception
				errMsg = e.getMessage();
		}
		if (imageIcon == null) {
			JOptionPane.showMessageDialog(this, errMsg,
					getCommName()+": Image Error", JOptionPane.ERROR_MESSAGE);
			trace.err("JCommLabel["+getCommName()+"].setImageName("+imageName+
					") cannot load image: "+errMsg);
			return;
		}
		if (label != null) {
			label.setIcon(imageIcon);
			setActionName(UPDATE_ICON);
		}
	}
	
	/**
	 * Try to get an image from URL address which starts with "file:".
	 * @param imageName relative URL
	 * @return icon loaded from URL; null on error
	 */
	private ImageIcon getIconFromURL(String imageName) throws Exception {
		String errMsg = null;
		URL imageURL = null;
		try {
			imageURL = new URL(imageName);
			return new ImageIcon(imageURL);
		} catch (MalformedURLException mal) {
			errMsg = "Cannot form image address from \""+imageName+"\":\n"+mal;
			trace.err("JCommLabel.setImageName() "+errMsg);
			throw new Exception(errMsg);
		} catch (Exception e) {
			errMsg = "Error getting image \""+imageName+"\"\nfrom address "+imageURL+":\n"+e;
			trace.err("JCommLabel.setImageName() "+errMsg);
			throw new Exception(errMsg);
		}
	}

	/**
	 * Try to get an image from physical path (from browser) or relative path.
	 * @param imageName filename
	 * @return icon loaded from file; null on error
	 */
	private ImageIcon getIconFromFile(String imageName) throws Exception {
		String errMsg = null;
		File imgFile = null;
		try {
			imgFile = new File(imageName);
			if (!imgFile.exists()) {
				String parent = getController().getPreferencesModel().getStringValue(TutorController.PROJECTS_DIRECTORY);
				imgFile = new File((parent.length() < 1 ? null : parent), imageName);
			}
		} catch (Exception e) {
			errMsg = "Error converting image name \""+imageName+"\" into filename:\n"+e;
			trace.errStack("JCommLabel.getIconFromFile() "+errMsg, e);				
			throw new Exception(errMsg);
		}
		if (!imgFile.exists()) {
			errMsg = "Cannot find file for image \""+imageName+"\"\nin filename "+imgFile.getAbsolutePath();
			trace.err("JCommLabel.getIconFromFile() "+errMsg);				
			throw new Exception(errMsg);
		}
		try {
			String absPath = imgFile.getCanonicalPath().replace('\\', '/');
			if (trace.getDebugCode("inter"))
				trace.out("inter", "setImageName("+imageName+") absPath="+absPath);
			return new ImageIcon(absPath);
		} catch (Exception e) {
			errMsg = "Error getting image \""+imageName+"\"\nfrom file "+imgFile.getAbsolutePath()+":\n"+e;
			trace.err("JCommLabel.getIconFromFile() "+errMsg);				
			throw new Exception(errMsg);
		}
	}				

	/**
	 * Try to get an image from a resource on the classpath.
	 * @param imageName filename
	 * @return icon loaded from resource; null on error
	 */
	private ImageIcon getIconFromResource(String imageName) throws Exception {
		String errMsg = null;
		URL imageURL = null;
		for (int i = 0; i < 3; ++i) {  // try alternatives until succeed
			try {
				switch(i) {
				case 0:
					imageURL = Utils.getURL(imageName, this); break;
				case 1:
					imageURL = JCommPicture.class.getResource(imageName); break;
				case 2:
					imageURL = this.getClass().getClassLoader().getResource(imageName); break;
				}
				if (trace.getDebugCode("inter"))
					trace.out("inter", "getIconFromResource"+i+"("+imageName+") imageURL: "+imageURL);
			} catch (Exception e) {
				errMsg = "Cannot form image address from \""+imageName+"\":\n"+e;
				trace.err("JCommLabel.getIconFromResource("+imageName+") "+errMsg);
			}
			if (imageURL == null)
				continue;
			try {
				Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
				return new ImageIcon(image, imageName);
			} catch (Exception e) {
				errMsg = "Error getting image \""+imageName+"\"\nfrom address "+imageURL+":\n"+e;
				trace.err("JCommLabel.getIconFromFile() "+errMsg);				
			}
		}
		throw new Exception(errMsg);
	}

	// ////////////////////////////////////////////////////
	/**
	
	*/
	// ////////////////////////////////////////////////////
	public String getImageName() {
		return imageName;
	}

	// ////////////////////////////////////////////////////
	/**
	
	*/
	// ////////////////////////////////////////////////////
	public void setFont(Font f) {
		if (label != null)
			label.setFont(f);
	}

	public Font getFont() {
		if (label != null)
			return label.getFont();
		else
			return super.getFont();
	}

	public void setSize(Dimension d) {
		super.setSize(d);
		if (label != null)
			label.setSize(d);
	}

	public void reset(TutorController controller) {
		// TODO Auto-generated method stub

	}

	// ////////////////////////////////////////////////////
	/**
	
	*/
	// ////////////////////////////////////////////////////
	public void setIcon(Icon icon) {
		if (label != null)
			label.setIcon(icon);
	}

	// ////////////////////////////////////////////////////
	/**
	
	*/
	// ////////////////////////////////////////////////////
	public Icon getIcon() {
		if (label != null)
			return label.getIcon();
		else
			return null;
	}

	public void mousePressed(MouseEvent e) {

		// trace.out("inter", "You clicked " + text + "!");
		if (e.isControlDown() && getController().isDefiningStartState()) {
			// trace.out ("mps", "JCommButton NEED codes to modify the
			// btton Text");
			JFrame frame = new JFrame("Modify Label Text");

			String currentDir = System.getProperty("user.dir");

			String text = getText(); // ((JLabel) e.getComponent()).getText();
			Vector selection = new Vector();
			Vector action = new Vector();
			Vector originalInput = null;

			selection.addElement(commSelection.trim());
			selection.addElement(getActionName().trim());
			MessageObject mo = getController()
					.getOriginalStartStateNodeMessage(selection, action);
			if (mo != null)
				originalInput = (Vector) mo.getProperty("Input");
			if (originalInput != null)
				text = originalInput.firstElement().toString();
			String title = "Please set the Label for widget " + commName
					+ " : ";
			EditLabelNameDialog t = new EditLabelNameDialog(frame, title,
			// getText(), getIcon(), currentDir, true);
					text, getImageName(), currentDir, isInvisible(), true);

			// setText(t.getNewLabel());
			// setIcon(t.getIcon());

			if (!t.getNewLabel().equals(text)) {
				setText(t.getNewLabel());
				dirty = true;
				setActionName(UPDATE_TEXT);
				sendValue();
			}

			if (t.getIcon() != null) {
				setIcon(t.getIcon());

				// setImageName(t.getImageName());
				imageName = t.getImageName();
				dirty = true;
				setActionName(UPDATE_ICON);
				sendValue();
			}

			if (isInvisible() != t.isInvisible()) {
				setInvisible(t.isInvisible());
				dirty = true;
				setActionName(UPDATE_INVISIBLE);
				sendValue();
			}

			// trace.out ("mps", "JCommButton New Label = " +
			// t.getNewLabel());
			// setText("New Button");
		}

	}
}
