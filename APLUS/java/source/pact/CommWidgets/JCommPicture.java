package pact.CommWidgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Highlighter;

import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.EditLabelNameDialog;

import javax.accessibility.Accessible;

import edu.cmu.pact.Utilities.PictureTransferHandler;
import edu.cmu.pact.Utilities.StringTransferable;
import edu.cmu.pact.Utilities.TransferActionListener;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;

//////////////////////////////////////////////////////
/**
 * <p>
 * Title: JCommPicture
 * </p>
 * <p>
 * Description: A text area which automatically sends user's input to Lisp via
 * Comm
 * </p>
 */
//////////////////////////////////////////////////////
//////////////////////////////////////////////////////
/**
 * This class will automatically send user's input to Lisp.
 */
/////////////////////////////////////////////////////
public class JCommPicture extends JCommWidget implements FocusListener,
		MouseListener, MouseMotionListener, Accessible {

//	private static final String UPDATE_TEXT = "UpdateText";
//	private static final String UPDATE_ICON = "UpdateIcon";
//	private static final String UPDATE_INVISIBLE = "UpdateInVisible";

	///    protected JTextArea textArea;

	protected JScrollPane textAreaScrollPane;

	protected String previousValue, resetValue = "";

	protected Highlighter defaultHighlighter;

	protected JLabel label;
	public ImageIcon image;
	private ImageIcon initImage = null;
	public String imageFile = "NoImage.gif";
	public String initImageFile = "NoImage.gif";
	private int preferredWidth;
	private int preferredHeight;
	private MouseEvent firstMouseEvent = null;
	private DropTargetDragEvent dragMouseEvent = null;
	private static boolean installInputMapBindings = true;
	private boolean correct = false, incorrect = false;
	// static PictureTransferHandler picHandler  = new PictureTransferHandler();

	PictureTransferHandler picHandler;
	private DragSource dragSource;
	private DropTargetListener dtListener;
	private DropTarget dropTarget;
	private int acceptableActions = DnDConstants.ACTION_COPY;
	private DragGestureListener dgListener;
	private DragSourceListener dsListener;
	public String Data;
	private boolean imageLock = false;
	private static String currentDir = System.getProperty("user.dir");

	//////////////////////////////////////////////////////
	/**
	 * Constructor
	 */
	//////////////////////////////////////////////////////
	public JCommPicture() {

		setPreferredSize(new Dimension(180, 180));
		label = new JLabel(""); // default with empty label name
		add(label);

		picHandler = new PictureTransferHandler(this); // handle drag and drop
		dragSource = DragSource.getDefaultDragSource();
		dtListener = new DTListener();
		dgListener = new DGListener();
		dsListener = new DSListener();

		// component, ops, listener, accepting
		this.dropTarget = new DropTarget(this, this.acceptableActions,
				this.dtListener, true);

		// component, action, listener
		this.dragSource.createDefaultDragGestureRecognizer(this,
				this.acceptableActions, this.dgListener);

		setInstallInputMapBindings(false);

		preferredWidth = (int) getPreferredSize().getWidth();

		preferredHeight = (int) getPreferredSize().getHeight();

		//		System.err.println("[W , H] = [" + preferredWidth + " , "	+ preferredHeight +"]");

		imageFile = initImageFile; // default is NoImage.gif
		actionName = DRAG_INTO;
		setImage(loadImage(imageFile));
		setFocusable(true);

		addMouseListener(this);
		addFocusListener(this);
		addMouseMotionListener(this);
		locked = false;
		setTransferHandler(picHandler);

	}

	public void setCommPicture(ImageIcon image) {

		this.image = image;
		setFocusable(true);

		addMouseListener(this);

		// addMouseListener(new MouseAdapter() {
		//        	public void mousePressed(MouseEvent event) {
		//        	JComponent component = (JComponent) event.getSource();
		//        	TransferHandler handler = component.getTransferHandler();
		//        	handler.exportAsDrag(component, event, TransferHandler.COPY);
		//        	}
		//        });

		addFocusListener(this);
		addMouseMotionListener(this);

		//Add the cut/copy/paste key bindings to the input map.
		//Note that this step is redundant if you are installing
		//menu accelerators that cause these actions to be invoked.
		//DragPictureDemo does not use menu accelerators and, since
		//the default value of installInputMapBindings is true,
		//the bindings are installed. DragPictureDemo2 does use
		//menu accelerators and so calls setInstallInputMapBindings
		//with a value of false. Your program would do one or the
		//other, but not both.
		if (installInputMapBindings) {
			InputMap imap = this.getInputMap();
			imap.put(KeyStroke.getKeyStroke("ctrl X"), TransferHandler
					.getCutAction().getValue(Action.NAME));
			imap.put(KeyStroke.getKeyStroke("ctrl C"), TransferHandler
					.getCopyAction().getValue(Action.NAME));
			imap.put(KeyStroke.getKeyStroke("ctrl V"), TransferHandler
					.getPasteAction().getValue(Action.NAME));
		}

		//Add the cut/copy/paste actions to the action map.
		//This step is necessary because the menu's action listener
		//looks for these actions to fire.
		ActionMap map = this.getActionMap();
		map.put(TransferHandler.getCutAction().getValue(Action.NAME),
				TransferHandler.getCutAction());
		map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
				TransferHandler.getCopyAction());
		map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
				TransferHandler.getPasteAction());

	}

	//////////////////////////////////////////////////////
	/**
	 * Used to process an InterfaceAction message
	 */
	//////////////////////////////////////////////////////
	public void doInterfaceAction(String selection, String action, String input) {
		System.err.println("doInterfaceAction  " + action + "  " + input
				+ " --> " + selection);
		if (action.equalsIgnoreCase(UPDATE_ICON)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "update Image: " + input);
			setImageFile(input);
		} else if (action.equalsIgnoreCase(DRAG_INTO)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Drag Image: " + input);
			setImageFile(input);
		} else if (action.equalsIgnoreCase(UPDATE_INVISIBLE)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Set InVisible: " + input);
			if (input.equalsIgnoreCase("true"))
				setInvisible(true);
			else
				setInvisible(false);
			setVisible(!isInvisible());
		} else if (action.equalsIgnoreCase(SET_VISIBLE)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Set Visible: " + input);
			if (input.equalsIgnoreCase("true"))
				setInvisible(false);
			else
				setInvisible(true);
			setVisible(!isInvisible());
		} else if (action.equalsIgnoreCase(UPDATE_TEXT)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "UpdateText: " + input);
			setText(input);
		}
		repaint();
		trace.out("**Error**: don't know interface action " + action);
	}

	// ////////////////////////////////////////////////////
	/**
	 * Returns a comm message which describes this interface element.
	 */
	//////////////////////////////////////////////////////
	public MessageObject getDescriptionMessage() {

		MessageObject mo = MessageObject.create("InterfaceDescription");
		mo.setVerb("SendNoteProperty");

		if (!initialize(getController())) {
			trace
					.out("ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return mo;
		}

		mo.setProperty("WidgetType", "JCommPicture");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));

		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();

		if (deftemplates != null)
			mo.setProperty("jessDeftemplates", deftemplates);

		if (instances != null)
			mo.setProperty("jessInstances", instances);

		serializeGraphicalProperties(mo);

		return mo;
	}

	// sanket@cs.wpi.edu

	public Vector createJessDeftemplates() {
		Vector deftemplates = new Vector();

		String deftemplateStr = "(deftemplate textArea (slot name) (slot value))";
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

		String instanceStr = "(assert (textArea (name " + commName + ")))";
		instances.add(instanceStr);

		return instances;
	}

	// sanket@cs.wpi.edu

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void doCorrectAction(String selection, String action, String input) {
		// System.err.println("doCorrectAction load " + input + " to " +
		// selection);

		if (action.equalsIgnoreCase(UPDATE_INVISIBLE)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Set InVisible: " + input);
			if (input.equalsIgnoreCase("true"))
				setInvisible(true);
			else
				setInvisible(false);
			setVisible(!isInvisible());
		} else if (action.equalsIgnoreCase(SET_VISIBLE)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Set Visible: " + input);
			if (input.equalsIgnoreCase("true"))
				setInvisible(false);
			else
				setInvisible(true);
			setVisible(!isInvisible());
		}
		else if (this.getCommName().equals(selection)) {
			setImageFile(input);
			correct = true;
			//			setForeground(correctColor);
			//			this.setForeground(correctColor);
			//			this.setCorrectColor(correctColor);
			//			
			//			this.backgroundHighlightColor = correctColor;
			//			this.createImage();
						
			//			this.paintBorder(this.getGraphics());
		    // setColor(Color.green);
			repaint();
		}

		///		((JCommDocument) textArea.getDocument()).locked = false;

		///        textArea.setText(input);
		///        textArea.setForeground(correctColor);

		if (getController().getUniversalToolProxy().lockWidget()) {
			///            ((JCommDocument) textArea.getDocument()).locked = true;
			///            textArea.setHighlighter(null);
			locked = true;
			//			System.err.println("Lock = " + locked);
			removeHighlight("");
			setFocusable(false);
		}

		///        if (correctFont != null) textArea.setFont(correctFont);
	}

	public boolean getLock(String selection) {
		///        return ((JCommDocument) textArea.getDocument()).locked;
		return false;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void doLISPCheckAction(String selection, String input) {
		//		System.err.println("doLISPCheckAction  " + input + " --> " + selection);
		///        textArea.setText(input);
		///        textArea.setForeground(LISPCheckColor);


		if (getController().getUniversalToolProxy().lockWidget())
			///            ((JCommDocument) textArea.getDocument()).locked = true;

			///       textArea.setHighlighter(null);
			setFocusable(false);
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void doIncorrectAction(String selection, String input) {
		//		System.err.println("doIncorrectAction ??? " + input + " to " + selection);
		if (this.getCommName().equals(selection)) {
			setImageFile(input);
			incorrect = true;
			this.setIncorrectColor(incorrectColor);
			repaint();
		}
		///        textArea.setText(input);

		///        textArea.setForeground(incorrectColor);
		///        ((JCommDocument) textArea.getDocument()).locked = false;
		///        textArea.setHighlighter(defaultHighlighter);
		locked = false;
		//		System.err.println("Lock = " + locked);
		setFocusable(true);

		///        if (incorrectFont != null) textArea.setFont(incorrectFont);
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void focusGained(FocusEvent e) {

		/*        if (!((JCommDocument) textArea.getDocument()).locked) {
		 previousValue = textArea.getText();

		 previousFont = textArea.getFont();
		 previousColor = textArea.getForeground();

		
		 * zz 09/11/04 textArea.setForeground(startColor); if (startFont !=
		 * null) textArea.setFont(startFont);
		
		 }

		 super.focusGained(e);
		 //textArea.setBackground(backgroundNormalColor);
		 */
		this.repaint();
	}

	public void highlight(String commComponentName, Border highlightBorder) {
		setBorder(highlightBorder);
	}

	//////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////
	public void removeHighlight(String subElement) {
		setBorder(originalBorder);
	}

	public void setFocus(String subWidgetName) {
		///        textArea.requestFocus();
	}

	//////////////////////////////////////////////////////
	/**
	 * If focus lost permanently, and text has changed, send value to lisp
	 */
	//////////////////////////////////////////////////////
	public void focusLost(FocusEvent e) {
		/*    	
		 if (getController().getCtatModeModel().isDefiningStartState()) {
		 if (!previousValue.equals(getText())) {
		 setText(getText());
		 dirty = true;
		 setActionName(UPDATE_TEXT);
		 sendValue();
		 return;
		 }
		 }
		 if (e.isTemporary()) return;

		 if (getController().getUniversalToolProxy().getAutoCapitalize() == true
		 || getAutoCapitalize() == true) {
		 textArea.setText(textArea.getText().toUpperCase());
		 }

		 if (!textArea.getText().equals(previousValue)) dirty = true;

		 if (dirty) sendValue();

		 dirty = false;
		 //previousValue = textArea.getText();

		 super.focusLost(e);
		 */
		this.repaint();
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public Object getValue() {
		if (getActionName().equalsIgnoreCase(UPDATE_ICON))
			return getImageFile();

		else if (getActionName().equalsIgnoreCase(UPDATE_TEXT)
				&& getText() != null && getText().length() > 0)
			return getText();
		else if (getActionName().equalsIgnoreCase(UPDATE_INVISIBLE))
			return isInvisible();
		else if (getActionName().equalsIgnoreCase(DRAG_INTO)) {
			return Data;
			//			return (getImageFile() != null ? Data : "NoImage.gif");
		}

		else
			return null;
	}

	//////////////////////////////////////////////////////
	/**
		Creates a vector of comm messages which describe the
		current state of this object relative to the start state
	 */
	//////////////////////////////////////////////////////
	public Vector getCurrentState() {

		Vector v = new Vector();

		if (image != null) {
			setActionName(UPDATE_ICON);
			v.addElement(getCurrentStateMessage());
		} else {
			setActionName(DRAG_INTO);
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
		setActionName(DRAG_INTO);
		return v;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	///    public String getText() {
	///        return textArea.getText();
	///    }
	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	///    public void setText(String text) {
	///        textArea.setText(text);
	///        previousValue = text;
	///    }
	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setFont(Font f) {
		///        if (textArea != null) textArea.setFont(f);
		super.setFont(f);
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public boolean isChangedFromResetState() {
		///        if (!textArea.getText().equals(resetValue)) {
		///            ((JCommDocument) textArea.getDocument()).locked = true;
		///            textArea.setHighlighter(null);
		///            setFocusable(false);

		///            return true;
		///        }

		//		if (getImageFile().equals(resetValue))
		//			return false;
		//		else
		return true;

	}

	public boolean resetStartStateLock(boolean startStateLock) {
		///        if (!textArea.getText().equals(resetValue)) {
		/// //       	System.out.println("Reset TextField [" + commName + "] - " + textArea.getText() + startStateLock);
		///            ((JCommDocument) textArea.getDocument()).locked = startStateLock;
		///            textArea.setHighlighter(null);
		///            setFocusable(!startStateLock);
		///            
		///            return true;
		///        }

		return false;
	}

	public void setFocusable(boolean focusFlag) {
		super.setFocusable(focusFlag);
		//        textArea.setFocusable(focusFlag);

	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void reset(TutorController controller) {
		initialize(controller);
		///        ((JCommDocument) textArea.getDocument()).locked = false;
		///        textArea.setHighlighter(defaultHighlighter);
		///        setFocusable(true);
		///        textArea.setText(resetValue);
		///        previousValue = resetValue;
		///        textArea.setForeground(startColor);
		locked = false;
		image = initImage;
		imageFile = initImageFile;
		//		System.err.println("Reset " + commName + " ==> "+ imageFile + " | image = " + image + " ]");
		this.repaint();

	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void keyTyped(KeyEvent e) {

	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void keyPressed(KeyEvent e) {

	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void changedUpdate(DocumentEvent e) {
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void insertUpdate(DocumentEvent e) {
		dirty = true;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void removeUpdate(DocumentEvent e) {
		dirty = true;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setBackground(Color c) {
		///        if (textArea != null) textArea.setBackground(c);
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setToolTipText(String text) {
		///        textArea.setToolTipText(text);
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public String getToolTipText() {
		///        return textArea.getToolTipText();
		return ("temp");
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	/*    public void addFocusListener(FocusListener l) {
	 //        super.addFocusListener(l);
	 //        if (textArea != null) image.addFocusListener(l);
	 //        if (image != null) addFocusListener(l);
	 }

	 public void addMouseListener(MouseListener l) {
	 //        if (textArea != null) textArea.addMouseListener(l);
	 //    	if (image != null) addMouseListener(l);
	 }

	 public void addMouseMotionListener(MouseMotionListener l) {
	 //        if (textArea != null) textArea.addMouseMotionListener(l);
	 //    	if (image != null) addMouseMotionListener(l);
	 }

	 public MouseListener[] getMouseListeners() {
	 //        if (textArea != null) return textArea.getMouseListeners();
	 if (image != null) return getMouseListeners();
	 return null;
	 }

	 public MouseMotionListener[] getMouseMotionListeners() {
	 //        if (textArea != null)
	 //        return textArea.getMouseMotionListeners();
	 if (image != null) return getMouseMotionListeners();
	 return null;
	 }

	 public void removeMouseMotionListener(MouseMotionListener l) {
	 //        if (textArea != null) textArea.removeMouseMotionListener(l);
	 if (image != null) removeMouseMotionListener(l);
	 }

	 public void removeMouseListener(MouseListener l) {
	 //        if (textArea != null) textArea.removeMouseListener(l);
	 if (image != null) removeMouseListener(l);
	 }
	 */
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		//		removeHighlight ("");
		//		trace.out("log","mouseClicked");

		// trace.out ("mps", "JCommButton NEED codes to modify the
		// btton Text");
		if (arg0.isControlDown() && getController().isDefiningStartState()) {
			JFrame frame = new JFrame("Modify Label Text");

			//		    currentDir = System.getProperty("user.dir");
			String text = getText(); // ((JLabel) e.getComponent()).getText();
			Vector selection = new Vector();
			Vector action = new Vector();
			Vector originalInput = null;

			selection.addElement(commSelection.trim());
			selection.addElement(getActionName().trim());
			MessageObject mo = getController().getOriginalStartStateNodeMessage(selection, action);
			if (mo != null)
				originalInput = (Vector) mo.getProperty("Input");
			if (originalInput != null)
				text = originalInput.firstElement().toString();
			String title = "Please set the Label for widget " + commName
					+ " : ";
			EditLabelNameDialog t = new EditLabelNameDialog(frame, title, text,
					imageFile, currentDir, isInvisible(), true);

			// setText(t.getNewLabel());
			// setIcon(t.getIcon());
			if (!t.getNewLabel().equals(getText())) {
				setText(t.getNewLabel());
				dirty = true;
				setActionName(UPDATE_TEXT);
				sendValue();
			}

			if (t.getIcon() != null) {
				// setIcon(t.getIcon());
				//				setImageFile(t.getImageName()); // t.getIcon().toString());
				setImage(t.getIcon());
				this.imageFile = t.getImageName();
				//				initImage = image = t.getIcon();
				//				initImageFile = imageFile;
				System.err.println("imageFile = " + imageFile);
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
		} else {
			//			if (!getImageFile().equals(previousValue)) dirty = true;
			//			else dirty = false;
			//			setActionName(DRAG_INTO);
			//			sendValue();			
		}

		// trace.out ("mps", "JCommButton New Label = " +
		// t.getNewLabel());
		// setText("New Button");

		requestFocusInWindow();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if (trace.getDebugCode("log")) trace.out("log", "mouseEntered");

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if (trace.getDebugCode("log")) trace.out("log", "mouseExited");

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */

	protected void paintComponent(Graphics graphics) {
		//		System.err.println("paintComponent > " + this.getCommName() + " => " + imageFile + " | " + image + " <");
		Graphics g = graphics.create();

		//Draw in our entire space, even if isOpaque is false.

		//		g.fillRect(0, 0, image == null ? 180 : image.getIconWidth(),
		//				image == null ? 180 : image.getIconHeight());

		// System.err.println("[ " + image.getIconWidth()  + " , " + image.getIconHeight() + "]" + image.getImageLoadStatus());
		//		if (image != null) {
		//Draw image at its natural size of 125x125.
		//	g.drawImage(image, 0, 0, this);

		if (imageFile.contains("NoImage.gif")) { // image.getImageLoadStatus() == 4) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, preferredWidth, preferredHeight);
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, preferredWidth, preferredHeight);
			g.setColor(Color.GREEN);
			if (commName != null)
				g.drawString(commName, 45, 90);
			else
				g.drawString("No Image", 45, 90);

		}

		else {
			//	g.fillRect(0, 0, preferredWidth, preferredHeight);
			//	image.paintIcon(this, g, image.getIconWidth(), image.getIconHeight());
			if (image != null)
				image.paintIcon(this, g, 0, 0);
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, preferredWidth, preferredHeight);
		}

		//		} else {
		//			g.setColor(Color.WHITE);
		//			g.fillRect(0, 0, preferredWidth, preferredHeight);
		//			g.setColor(Color.BLACK); 
		//			g.drawRect(0, 0, preferredWidth, preferredHeight);
		//			g.setColor(Color.RED);
		//			if (commName != null)
		//			             g.drawString(commName, 45, 90);
		//			else g.drawString("No Image", 45, 90); 
		//		}

		//Add a border, red if picture currently has focus
		if (isFocusOwner()) {
			//			System.err.println(" --> RED");
			g.setColor(Color.RED);
		} else if (correct) {
					System.err.println(" --> GREEN");
			g.setColor(correctColor);
			g.drawRect(0, 0, preferredWidth, preferredHeight);  // turn it off, if you don't want color
			correct = false;
		} else if (incorrect) {
						System.err.println(" --> RED");
			g.setColor(incorrectColor);
			g.drawRect(0, 0, preferredWidth, preferredHeight); // turn it off, if you don't want color
			incorrect = false;
		} else {
			//			System.err.println(" --> BLACK");
			g.setColor(Color.BLACK);

		}
		//		g.drawRect(0, 0, image == null ? 180 : image.getIconWidth(),
		//				image == null ? 180 : image.getIconHeight());
		g.dispose();
	}

	public void mousePressed(MouseEvent e) {
		//Don't bother to drag if there is no image.
		//		trace.out("log","mousePressed");
		if (image == null)
			return;

		firstMouseEvent = e;
		e.consume();
		//		System.out.println("mousePressed image = " + imageFile.toString()); //  + " [" + firstMouseEvent + "]");
	}

	public void mouseDragged(MouseEvent e) {
		//Don't bother to drag if the component displays no image.
		//		trace.out("log","mouseDragged");
		if (image == null)
			return;

		/*		if (firstMouseEvent != null) {
		 e.consume();
		 System.out.println("mouseDragged image = " + image);
		 //If they are holding down the control key, COPY rather than MOVE
		 int ctrlMask = InputEvent.CTRL_DOWN_MASK;
		 int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask) ? TransferHandler.COPY
		 : TransferHandler.MOVE;

		 int dx = Math.abs(e.getX() - firstMouseEvent.getX());
		 int dy = Math.abs(e.getY() - firstMouseEvent.getY());
		 //Arbitrarily define a 5-pixel shift as the
		 //official beginning of a drag.
		 if (dx > 5 || dy > 5) {
		 //This is a drag, not a click.
		 JCommPicture c = (JCommPicture) e.getSource();

		 //	    	  TransferHandler handler = c.getTransferHandler();

		 //Tell the transfer handler to initiate the drag.
		 // handler.exportAsDrag(c, firstMouseEvent, action);
		 c.getPicHandler().exportAsDrag(c, firstMouseEvent, action);
		 firstMouseEvent = null;
		 System.out.println("mouseDragged Done ");

		 System.out.println("mouseDragged handler = "
		 + c.getPicHandler().getSourcePic().getCommName()
		 + " Component:[" + c.getCommName() + "] Event:["
		 + firstMouseEvent + " || " + action);

		 }
		 }*/
	}

	public void mouseReleased(MouseEvent e) {
		//		trace.out("log", "mouseReleased");
		//		firstMouseEvent = null;
		///		dirty = true;
		///		sendValue();
	}

	public void mouseMoved(MouseEvent e) {
		//		trace.out("log", "mouseMoved " );
	}

	//This method is necessary because DragPictureDemo and
	//DragPictureDemo2 both use this class and DragPictureDemo
	//needs to have the input map bindings installed for
	//cut/copy/paste. DragPictureDemo2 uses menu accelerators
	//and does not need to have the input map bindings installed.
	//Your program would use one approach or the other, but not
	//both. The default for installInputMapBindings is true.
	public static void setInstallInputMapBindings(boolean flag) {
		installInputMapBindings = flag;
	}

	public static boolean getInstallInputMapBindingds() { //for completeness
		return installInputMapBindings;
	}

	private ImageIcon getImage() {
		return image;
	}

	public void setImage(ImageIcon image) {

		this.image = image;
		//		this.repaint();
	}

	//			public void setImage(String imageName) {
	//				String imageFileName = "images/" + imageName + ".jpg";
	//				File f = new File(imageFileName);
	//				trace.err("image file = " + f.getAbsolutePath() );
	//				createImageIcon(imageFileName, imageName).getImage();
	//				this.image = image;
	//			}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path, String description) {
		java.net.URL imageURL = JCommPicture.class.getResource(path);

		if (imageURL == null) {
			//			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return new ImageIcon(imageURL, description);
		}
	}

	//Create an Edit menu to support cut/copy/paste.
	public JMenuBar createMenuBar() {
		JMenuItem menuItem = null;
		JMenuBar menuBar = new JMenuBar();
		JMenu mainMenu = new JMenu("Edit");
		mainMenu.setMnemonic(KeyEvent.VK_E);
		TransferActionListener actionListener = new TransferActionListener();

		menuItem = new JMenuItem("Cut");
		menuItem.setActionCommand((String) TransferHandler.getCutAction()
				.getValue(Action.NAME));
		menuItem.addActionListener(actionListener);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				ActionEvent.CTRL_MASK));
		menuItem.setMnemonic(KeyEvent.VK_T);
		mainMenu.add(menuItem);
		menuItem = new JMenuItem("Copy");
		menuItem.setActionCommand((String) TransferHandler.getCopyAction()
				.getValue(Action.NAME));
		menuItem.addActionListener(actionListener);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.CTRL_MASK));
		menuItem.setMnemonic(KeyEvent.VK_C);
		mainMenu.add(menuItem);
		menuItem = new JMenuItem("Paste");
		menuItem.setActionCommand((String) TransferHandler.getPasteAction()
				.getValue(Action.NAME));
		menuItem.addActionListener(actionListener);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				ActionEvent.CTRL_MASK));
		menuItem.setMnemonic(KeyEvent.VK_P);
		mainMenu.add(menuItem);

		menuBar.add(mainMenu);
		return menuBar;
	}

	public String getImageFile() {
		return imageFile;
	}

	public void setImageFile(String imageFile) {
		if (imageFile != null)
			this.imageFile = imageFile;
		else
			this.imageFile = "NoImage.gif";

		System.out.println("imageFile = " + imageFile);
		setImage(loadImage(imageFile));
		//		initImageFile = imageFile;
		//		initImage = image;
	}

	/**
	 * Waits until the image is comlitely loaded.
	 * @param component - the component on which the image will be drawn.
	 * @param image - the image to be tracked.
	 */
	public static void waitForImage(Component component, Image image) {
		MediaTracker tracker = new MediaTracker(component);
		try {
			tracker.addImage(image, 0);
			tracker.waitForID(0);
		} catch (InterruptedException e) {
		}
	}

	protected ImageIcon loadImage(String imageName) {

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
					System.err
							.println("Creat icon from physical/relative address");
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
//			System.err.println("Creat icon from resource file : " + imageName);
		}

		if (imageURL == null) {
			// Get image from resource .jar file ??? may be redundant, need to verified from webstart
			imageURL = JCommPicture.class.getResource(imageName);
//			System.err.println("Creat icon from resource file : " + imageName);
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

	// ////////////////////////////////////////////////////
	/**
	 * Get the destination domrin name which will be sent to lisp. (Not necessarily the same
	 * as which is displayed in the interface builder)
	 */
	// ////////////////////////////////////////////////////
	public String getCommNameToSend() {
		return commName;
	}

	public PictureTransferHandler getPicHandler() {
		return picHandler;
	}

	public void setPicHandler(PictureTransferHandler picHandler) {
		this.picHandler = picHandler;
	}

	class DTListener implements DropTargetListener {

		/**
		 * Called by isDragOk
		 * Checks to see if the flavor drag flavor is acceptable
		 * @param e the DropTargetDragEvent object
		 * @return whether the flavor is acceptable
		 */
		/*    	    private boolean isDragFlavorSupported(DropTargetDragEvent e) {
		 boolean ok=false;
		 //    	      if (e.isDataFlavorSupported(StringTransferable.plainTextFlavor)) {
		 //    		ok=true;
		 //    	      } else if (e.isDataFlavorSupported(
		 //    		StringTransferable.localStringFlavor)) {
		 //    		ok=true;
		 //    	      } else if (e.isDataFlavorSupported(DataFlavor.stringFlavor)) {	  
		 //    		ok=true;
		 //    	      } else if (e.isDataFlavorSupported(DataFlavor.plainTextFlavor)) {
		 //    		ok=true;
		 //    	      }
		 ok = true;
		 return ok;
		 }*/
		/**
		 * Called by drop
		 * Checks the flavors and operations
		 * @param e the DropTargetDropEvent object
		 * @return the chosen DataFlavor or null if none match
		 */
		private DataFlavor chooseDropFlavor(DropTargetDropEvent e) {
			if (e.isLocalTransfer() == true
					&& e
							.isDataFlavorSupported(StringTransferable.localStringFlavor)) {
				return StringTransferable.localStringFlavor;
			}
			DataFlavor chosen = null;
			if (e.isDataFlavorSupported(StringTransferable.plainTextFlavor)) {
				chosen = StringTransferable.plainTextFlavor;
			} else if (e
					.isDataFlavorSupported(StringTransferable.localStringFlavor)) {
				chosen = StringTransferable.localStringFlavor;
			} else if (e.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				chosen = DataFlavor.stringFlavor;
			} else if (e.isDataFlavorSupported(DataFlavor.plainTextFlavor)) {
				chosen = DataFlavor.plainTextFlavor;
			}
			return chosen;
		}

		/**
		 * start "drag under" feedback on component
		 * invoke acceptDrag or rejectDrag based on isDragOk
		 */
		public void dragEnter(DropTargetDragEvent e) {

			//			System.out.println("dt enter: accepting " + e.getDropAction());
			e.acceptDrag(e.getDropAction());
			// if (image != null && !imageFile.contains("NoImage.gif"))
			if (imageLock || locked)
				return;

			dragMouseEvent = e;

		}

		/**
		 * continue "drag under" feedback on component invoke acceptDrag or
		 * rejectDrag based on isDragOk
		 */
		public void dragOver(DropTargetDragEvent e) {
			//   	      System.out.println( "dt over: accepting");
			e.acceptDrag(e.getDropAction());
		}

		public void dropActionChanged(DropTargetDragEvent e) {
			//   	      System.out.println( "dt changed: accepting"+e.getDropAction());
			e.acceptDrag(e.getDropAction());

		}

		public void dragExit(DropTargetEvent e) {
			//   	      System.out.println( "dtlistener dragExit");
			//    	      DropLabel.this.borderColor=Color.green;            
			//    	      showBorder(false);
		}

		/**
		 * perform action from getSourceActions on
		 * the transferrable
		 * invoke acceptDrop or rejectDrop
		 * invoke dropComplete
		 * if its a local (same JVM) transfer, use StringTransferable.localStringFlavor
		 * find a match for the flavor
		 * check the operation
		 * get the transferable according to the chosen flavor
		 * do the transfer
		 */
		public void drop(DropTargetDropEvent e) {

			//  			if (image != null && !imageFile.contains("NoImage.gif"))
			if (imageLock || locked)
				return;
			System.out.println("dtlistener drop [ " + getImageFile() + " ]");
			if (dragMouseEvent != null) {
				//				e.consume();

				//If they are holding down the control key, COPY rather than MOVE
				int ctrlMask = InputEvent.CTRL_DOWN_MASK;
				int action = ((e.getSourceActions() & ctrlMask) == ctrlMask) ? TransferHandler.COPY
						: TransferHandler.MOVE; // e.getModifiersEx()

				int dx = (int) Math.abs(e.getLocation().getX()
						- dragMouseEvent.getLocation().getX());
				int dy = (int) Math.abs(e.getLocation().getY()
						- dragMouseEvent.getLocation().getY());

				//Arbitrarily define a 5-pixel shift as the
				//official beginning of a drag.
				if (dx > 5 || dy > 5) {

					DataFlavor chosen = chooseDropFlavor(e);
					//		    	      System.err.println("chosen = " + chosen);
					if (chosen == null) {
						//		    		System.err.println( "No flavor match found" );
						e.rejectDrop();
						return;
					}
					try {
						Data = (String) e.getTransferable().getTransferData(
								chosen);

						//		    	    System.err.println("Data = " + Data.toString());
						//		    	    setCommPicture(createImageIcon((String) Data, "DropInto")
						//	    					.getImage());
						//		    	    image = createImageIcon(Data, "DropInto").getImage();
						image = new ImageIcon(Data);
						repaint();
					} catch (Throwable tw) {
						System.err.println("Couldn't get transfer data: "
								+ tw.getMessage());
						tw.printStackTrace();
						e.dropComplete(false);

						return;
					}

					//This is a drag, not a click.
					DropTarget t = (DropTarget) e.getSource();

					JCommPicture c = (JCommPicture) t.getComponent();

					//					System.out.println("JCommPicture Drop = " + c.getCommName() + " [ " + firstMouseEvent + " ]");
					c.getPicHandler().exportAsDrag(c, firstMouseEvent, action);
					dragMouseEvent = null;
					//					System.out.println("mouseDragged Done ");

					//					System.out.println("mouseDragged handler = "
					//							+ c.getPicHandler().getSourcePic().getCommName()
					//							+ " Component:[" + c.getCommName() + "] Event:["
					//							+ dragMouseEvent + " || " + action);
					e.dropComplete(true);
					//		    	      showBorder(false);   

					//		    	     if (imageFile != null) 

					dirty = true;
					sendValue();
				}
			}

		}

	}

	/**
	 * DGListener
	 * a listener that will start the drag.
	 * has access to top level's dsListener and dragSource
	 * @see java.awt.dnd.DragGestureListener
	 * @see java.awt.dnd.DragSource
	 * @see java.awt.datatransfer.StringSelection      
	 */
	class DGListener implements DragGestureListener {
		/**
		 * Start the drag if the operation is ok.
		 * uses java.awt.datatransfer.StringSelection to transfer
		 * the label's data
		 * @param e the event object
		 */
		public void dragGestureRecognized(DragGestureEvent e) {

			// if the action is ok we go ahead
			// otherwise we punt
			//          System.out.println(e.getDragAction());
			if ((e.getDragAction() & JCommPicture.this.acceptableActions) == 0)
				return;
			//          System.out.println( "kicking off drag");

			// get the label's text and put it inside a Transferable
			// Transferable transferable = new StringSelection( DragLabel.this.getText() );
			//   PictureTransferHandler  pictureTransferHandler = new PictureTransferHandler( JCommPicture.this ); 
			//   Transferable transferable = pictureTransferHandler.createTransferable((JComponent) JCommPicture.this); 
			Transferable transferable = new StringTransferable(
					JCommPicture.this.getImageFile().toString());
			//          Transferable transferable = picHandler.createTransferable((JComponent) JCommPicture.this);     

			// now kick off the drag
			try {
				// initial cursor, transferrable, dsource listener      
				e.startDrag(DragSource.DefaultCopyNoDrop, transferable,
						JCommPicture.this.dsListener);

				// or if dragSource is a variable
				// dragSource.startDrag(e, DragSource.DefaultCopyDrop, transferable, dsListener);

				// or if you'd like to use a drag image if supported

				/*
				  if(DragSource.isDragImageSupported() )
				  // cursor, image, point, transferrable, dsource listener	
				  e.startDrag(DragSource.DefaultCopyDrop, image, point, transferable, dsListener);
				 */

			} catch (InvalidDnDOperationException idoe) {
				System.err.println(idoe);
			}
		}

	}

	/**
	 * DSListener
	 * a listener that will track the state of the DnD operation
	 * 
	 * @see java.awt.dnd.DragSourceListener
	 * @see java.awt.dnd.DragSource
	 * @see java.awt.datatransfer.StringSelection      
	 */
	class DSListener implements DragSourceListener {

		/**
		 * @param e the event
		 */
		public void dragDropEnd(DragSourceDropEvent e) {
			if (e.getDropSuccess() == false) {
				System.out.println("not successful");
				return;
			}

			/*
			 * the dropAction should be what the drop target specified
			 * in acceptDrop
			 */
			//          System.out.println( "dragdropend action " +  picHandler.getDestPic().getCommName());
			//	      dirty = true;
			//	  		sendValue();
			// this is the action selected by the drop target
			if (e.getDropAction() == DnDConstants.ACTION_MOVE)
				JCommPicture.this.setImage(null);

		}

		/**
		 * @param e the event
		 */
		public void dragEnter(DragSourceDragEvent e) {
			//         System.out.println( "draglabel enter " + e);
			DragSourceContext context = e.getDragSourceContext();
			//intersection of the users selected action, and the source and target actions
			int myaction = e.getDropAction();
			if ((myaction & JCommPicture.this.acceptableActions) != 0) {
				context.setCursor(DragSource.DefaultCopyDrop);
			} else {
				context.setCursor(DragSource.DefaultCopyNoDrop);
			}
		}

		/**
		 * @param e the event
		 */
		public void dragOver(DragSourceDragEvent e) {
			DragSourceContext context = e.getDragSourceContext();
			int sa = context.getSourceActions();
			int ua = e.getUserAction();
			int da = e.getDropAction();
			int ta = e.getTargetActions();
			//         System.out.println("dl dragOver source actions" + sa);
			//         System.out.println("user action" + ua);
			//         System.out.println("drop actions" + da);
			//         System.out.println("target actions" + ta);      
		}

		/**
		 * @param e the event
		 */
		public void dragExit(DragSourceEvent e) {
			//         System.out.println( "draglabel exit " + e);      
			DragSourceContext context = e.getDragSourceContext();
		}

		/**
		 * for example, press shift during drag to change to
		 * a link action
		 * @param e the event     
		 */
		public void dropActionChanged(DragSourceDragEvent e) {
			DragSourceContext context = e.getDragSourceContext();
			context.setCursor(DragSource.DefaultCopyNoDrop);
		}
	}

	public boolean isimageLock() {
		return imageLock;
	}

	public void setimageLock(boolean imageLock) {
		this.imageLock = imageLock;
	}

	//////////////////////////////////////////////////////
	/**
	
	 */
	//////////////////////////////////////////////////////
	public void setText(String text) {
		if (label != null)
			label.setText(text);
		setActionName(UPDATE_TEXT);
	}

	//////////////////////////////////////////////////////
	/**
	
	 */
	//////////////////////////////////////////////////////
	public String getText() {
		if (label != null)
			return label.getText();
		else
			return null;
	}

}
