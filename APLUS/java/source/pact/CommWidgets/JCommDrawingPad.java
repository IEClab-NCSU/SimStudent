package pact.CommWidgets;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Highlighter;

import pact.CommWidgets.event.StudentActionEvent;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.DialogUtilities;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.TutorController;

//////////////////////////////////////////////////////
/**
 * <p>
 * Title: JCommDrawingPad
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
public class JCommDrawingPad extends JCommWidget  implements FocusListener, ActionListener // , StudentActionListener
{

//	private static final String UPDATE_TEXT = "UpdateText";
	
	protected int DrawingPadWidth  = 500;
	protected int DrawingPadHeight = 500;
	protected Frame scribbleFrame = new Frame();
	protected Scribble  scribble;
	
    protected JPanel drawingPanel = new JPanel();
	protected JPanel buttonsPanel = new JPanel();
    protected JScrollPane DrawingPadScrollPane;

    JButton clearButton  = new JButton("  Clear  ");        // The Clear button.
    JButton saveButton   = new JButton("  Submit  ");       // The Save button.
    
    protected String previousValue, resetValue = "";

    protected Highlighter defaultHighlighter;
   

    protected short last_x, last_y;                // Coordinates of last click.
    
//    protected Vector lines = new Vector(256,256);  // Store the scribbles.

    protected Color current_color = Color.black;   // Current drawing color.
    protected int width, height;                   // The preferred size.
    
    protected PopupMenu popup;                     // The popup menu.
    
    protected Frame frame = new Frame();                         // The frame we are within.
    
	BufferedImage scribbleImage;


	

    //////////////////////////////////////////////////////
    /**
     * Constructor
     */
    //////////////////////////////////////////////////////
    public JCommDrawingPad() {

		drawingPanel.setBackground(Color.WHITE);
		setLayout(new BorderLayout());

		scribble = new Scribble(scribbleFrame, DrawingPadWidth, DrawingPadHeight, this);
//		scribble.setFileSavingDirectory(getSavingFolder());
		drawingPanel.add(scribble);
		createButtonPanel();

		add(drawingPanel, BorderLayout.CENTER);

		clearButton.addActionListener(this);
		saveButton.addActionListener(this);

		actionName = "UpdateDrawingPad";
		// defaultHighlighter = DrawingPad.getHighlighter();
		// originalBorder = DrawingPad.getBorder();


		
/*		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		String currentDir = System.getProperty("user.dir");
		chooser.setCurrentDirectory(new File(currentDir)); // new File(".")

		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				String name = f.getName().toLowerCase();
				return f.isDirectory() || name.endsWith(".jpeg")
						|| name.endsWith(".jpg") || name.endsWith(".gif");
			}

			public String getDescription() {
				return "Image Files";
			} // emm not sure why this is needed

		});
*/		
		addFocusListener(this);
		
//		addStudentActionListener(this);
//		getController().getLauncher().getWrapper().getWrapperSupport().addActionListener(this);
	}

    protected void createButtonPanel(){
        if (buttonsPanel != null)
		buttonsPanel.removeAll();
        
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
       
        add(buttonsPanel, BorderLayout.SOUTH);

        buttonsPanel.add(clearButton); 
        buttonsPanel.add(saveButton);   
        
    }
    //////////////////////////////////////////////////////
    /**
     * Used to process an InterfaceAction message
     */
    //////////////////////////////////////////////////////
    public void doInterfaceAction(String selection, String action, String input) {
    	
    	if (action.equalsIgnoreCase("UpdateDrawingPad")) {
            setText(input);

            if (getController().isStartStateInterface()) {
//                ((JCommDocument) DrawingPad.getDocument()).locked = true;
//                DrawingPad.setHighlighter(null);
                setFocusable(false);
            }

            return;
        }
		else if (action.equalsIgnoreCase(UPDATE_TEXT)) {
//			trace.out("inter", "Load text: " + input);
			setText(input);
		}
		else if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			scribble.clear();
			setVisible(input);
		}
        
        trace.out("**Error**: don't know interface action " + action);
    }

    //////////////////////////////////////////////////////
    /**
     * Returns a comm message which describes this interface element.
     */
    //////////////////////////////////////////////////////
    public MessageObject getDescriptionMessage() {

        MessageObject mo = MessageObject.create("InterfaceDescription");
        mo.setVerb("SendNoteProperty");

		if (!initialize(getController())) {
			trace.out(
				5,
				this,
				"ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}

		mo.setProperty("WidgetType", "JCommDrawingPad");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));
		
		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();
		
		if(deftemplates != null)  mo.setProperty("jessDeftemplates", deftemplates);

		
		if(instances != null)    mo.setProperty("jessInstances", instances);

		
		serializeGraphicalProperties(mo);

        return mo;
    }

    // chc@cs.cmu.edu

    public Vector createJessDeftemplates() {
        Vector deftemplates = new Vector();

        String deftemplateStr = "(deftemplate DrawingPad (slot name) (slot value))";
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

        String instanceStr = "(assert (DrawingPad (name " + commName + ")))";
        instances.add(instanceStr);

        return instances;
    }

    // chc@cs.cmu.edu


    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
	public void doCorrectAction(String selection, String action, String input) {
		// ((JCommDocument) DrawingPad.getDocument()).locked = false;
		//		
		// DrawingPad.setText(input);
		// DrawingPad.setForeground(correctColor);
		if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		} else {
			if (getController().getUniversalToolProxy().lockWidget()) {

				// ((JCommDocument) DrawingPad.getDocument()).locked = true;
				// DrawingPad.setHighlighter(null);
				removeHighlight("");
				setFocusable(false);
			}
		}
		// if (correctFont != null) DrawingPad.setFont(correctFont);
	}

    public boolean getLock(String selection) {
//        return ((JCommDocument) DrawingPad.getDocument()).locked;
    	return true;
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void doLISPCheckAction(String selection, String input) {
//        DrawingPad.setText(input);
//        DrawingPad.setForeground(LISPCheckColor);
        
//        if (getController().getUniversalToolProxy().lockWidget())
//            ((JCommDocument) DrawingPad.getDocument()).locked = true;
        
//        DrawingPad.setHighlighter(null);
        setFocusable(false);
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void doIncorrectAction(String selection, String input) {
//        DrawingPad.setText(input);
//
//        DrawingPad.setForeground(incorrectColor);
//        ((JCommDocument) DrawingPad.getDocument()).locked = false;
//        DrawingPad.setHighlighter(defaultHighlighter);
        setFocusable(true);

//        if (incorrectFont != null) DrawingPad.setFont(incorrectFont);
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
//    public void focusGained(FocusEvent e) {
//
//        if (!((JCommDocument) DrawingPad.getDocument()).locked) {
//            previousValue = DrawingPad.getText();
//
//            previousFont = DrawingPad.getFont();
//            previousColor = DrawingPad.getForeground();
//
//            /*
//             * zz 09/11/04 DrawingPad.setForeground(startColor); if (startFont !=
//             * null) DrawingPad.setFont(startFont);
//             */
//        }
//
//        super.focusGained(e);
//        //DrawingPad.setBackground(backgroundNormalColor);
//    }

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
    	 saveButton.requestFocus();
    }

    //////////////////////////////////////////////////////
    /**
     * If focus lost permanently, and text has changed, send value to lisp
     */
    //////////////////////////////////////////////////////
//    public void focusLost(FocusEvent e) {
//    	
//    	if (getController().getCtatModeModel().isDefiningStartState()) {
//			if (!previousValue.equals(getText())) {
//				setText(getText());
//				dirty = true;
//				setActionName(UPDATE_TEXT);
//				sendValue();
//				return;
//			}
//    	}
//        if (e.isTemporary()) return;
//
//        if (getController().getUniversalToolProxy().getAutoCapitalize() == true
//			|| getAutoCapitalize() == true) {
//            DrawingPad.setText(DrawingPad.getText().toUpperCase());
//        }
//
//        if (!DrawingPad.getText().equals(previousValue)) dirty = true;
//
//        if (dirty) sendValue();
//
//        dirty = false;
//        //previousValue = DrawingPad.getText();
//
//        super.focusLost(e);
//    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public Object getValue() {
 //       DrawingPad.setForeground(startColor);

//        return DrawingPad.getText().trim();
        return "test";
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public String getText() {
//     return DrawingPad.getText();
        return "test";
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void setText(String text) {
//        DrawingPad.setText(text);
        previousValue = text;
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void setFont(Font f) {
//        if (DrawingPad != null) DrawingPad.setFont(f);
        super.setFont(f);
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public boolean isChangedFromResetState() {
//        if (!DrawingPad.getText().equals(resetValue)) {
//            ((JCommDocument) DrawingPad.getDocument()).locked = true;
//            DrawingPad.setHighlighter(null);
//            setFocusable(false);
//
//            return true;
//        }

        return false;
    }

    public boolean resetStartStateLock(boolean startStateLock) {
//        if (!DrawingPad.getText().equals(resetValue)) {
// //       	System.out.println("Reset TextField [" + commName + "] - " + DrawingPad.getText() + startStateLock);
//            ((JCommDocument) DrawingPad.getDocument()).locked = startStateLock;
//            DrawingPad.setHighlighter(null);
//            setFocusable(!startStateLock);
//            
//            return true;
//        }
        
        return false;
    }

    public void setFocusable(boolean focusFlag) {
        super.setFocusable(focusFlag);
//        DrawingPad.setFocusable(focusFlag);
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void reset(TutorController controller) {
        initialize(controller);
        
//        ((JCommDocument) DrawingPad.getDocument()).locked = false;
//        DrawingPad.setHighlighter(defaultHighlighter);
//        setFocusable(true);
//        DrawingPad.setText(resetValue);
//        previousValue = resetValue;
//        DrawingPad.setForeground(startColor);
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
//        if (DrawingPad != null) DrawingPad.setBackground(c);
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void setToolTipText(String text) {
//        DrawingPad.setToolTipText(text);
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public String getToolTipText() {
//        return DrawingPad.getToolTipText();
    	return "test";
    }


    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////

		  /** This is the ActionListener method invoked by the popup menu items */
		  public void actionPerformed(ActionEvent event) {
		    // Get the "action command" of the event, and dispatch based on that.
		    // This method calls a lot of the interesting methods in this class.
		    	
		    String command = event.getActionCommand();
		    
//		    System.err.println("actionPerformed = " + command);
		    
		    scribble.controller = getController();
//			fileSavingDirectory = scribble.getScribbleSavingFolder();
//			scribble.setImageFileName(fileSavingDirectory);
		    
		    if (command.equals(clearButton.getText())) scribble.clear();
//		    else if (command.equals("print")) print();
		    else if (command.equals(saveButton.getText())) {
//		    	String filename = "test.jpeg";
		    	scribble.saveImage(scribble.getFileSavingDirectory() + "/" + scribble.getImageFileName() + ".jpeg");
		    	scribble.clear();
				dirty = true;
				sendValue();
		    }
		    else if (command.equals("Save")) {
		    	scribble.saveImage(null);
				dirty = true;
				sendValue();
		    }
//		    else if (command.equals("Load")) scribble.loadImage();
		  }

		  /** Draw all the saved lines of the scribble, in the appropriate colors */
//		  public void paint(Graphics g) {
//			  System.out.println("paint = " + backgroundNormalColor);
////			  DrawingPad.setBackground(backgroundNormalColor);
//
//		    for(int i = 0; i < lines.size(); i++) {
//		      Line l = (Line)lines.elementAt(i);
//		      g.setColor(l.color);
//		      g.drawLine(l.x1, l.y1, l.x2, l.y2);
//		    }
////		    DrawingPad.repaint();
//		    buttonPane.repaint();
//
//		  }
//	

//			public void studentactionPerformed (ActionEvent sae) {
//				
//				System.err.println("Launch next Problem");
//				
//
//			}
//
//			/**
//			 * 
//			 */
//			private void addDoneListeners() {
//				JCommButton btn = null;
//		        for (Iterator i = getController().getCommWidgetTable().values().iterator(); i.hasNext(); ) {
//					JCommWidget widget = (JCommWidget) i.next();
//					if(widget.getCommName().equalsIgnoreCase("done")){
//						if(widget instanceof JCommButton){
//							btn = (JCommButton)widget;
//							btn.removeAllProblemDoneListeners();
//							btn.addProblemDoneListener((ProblemDoneListener) this);
//						}
//						widget.setEnabled(false);
//					}
//				}
//			}
//			
//			/* (non-Javadoc)
//			 * @see pact.CommWidgets.event.ProblemDoneListener#problemDone(pact.CommWidgets.event.ProblemDoneEvent)
//			 */
//			public void problemDone(ProblemDoneEvent e) {
//				JCommButton btn = null;
//				System.err.println("JCommDrawingPad AutoDone");
//		        for (Iterator i = getController().getCommWidgetTable().values().iterator(); i.hasNext(); ) {
//		            JCommWidget widget = (JCommWidget) i.next();
//					if(widget.getCommName().equalsIgnoreCase("done")){
//						
//				    	scribble.saveImage(scribble.getImageFileName());
//				    	scribble.clear();
//					}
//				}
//			}
//			
//			public void studentActionPerform(StudentActionEvent e) {
//				scribble.saveImage(scribble.getImageFileName());
//		    	scribble.clear();
//			}

		  /**
			 * A class to store the coordinates and color of one scribbled line.
			 * The complete scribble is stored as a Vector of these objects
			 */
//		  static class Line implements Serializable {
//		    public short x1, y1, x2, y2;
//		    public Color color;
//		    public Line(short x1, short y1, short x2, short y2, Color c) {
//		      this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2; this.color = c;
//		    }
//		  }	

			protected void paintComponent(Graphics graphics) {
				super.paintComponent(graphics ); // call superclass's paintComponent
//				System.err.println("paintComponent > " + this.getName() +" [ " 
//						+ scribble.getHeight() + " , " + scribble.getWidth() + " ]");
				
				graphics.setColor(Color.WHITE);
				graphics.fillRect(0, 0, scribble.getHeight(), scribble.getWidth());
				
				graphics.setColor(Color.BLACK); 
				graphics.drawRect(0, 0, scribble.getHeight(), scribble.getWidth());
					
			}

		public int getDrawingPadWidth() {
			return DrawingPadWidth;
		}

		public void setDrawingPadWidth(int drawingPadWidth) {
			DrawingPadWidth = drawingPadWidth;
		}

		public int getDrawingPadHeight() {
			return DrawingPadHeight;
		}

		public void setDrawingPadHeight(int drawingPadHeight) {
			DrawingPadHeight = drawingPadHeight;
		}

		public void studentActionPerformed(StudentActionEvent sae) {
			// TODO Auto-generated method stub
			
		}

		public Scribble getScribble() {
			return scribble;
		}

		
		public void autoSave() {
		scribble.controller = getController();
		scribble.saveImage(scribble.getFileSavingDirectory() + "/" +scribble.getImageFileName() + "_A.jpeg");
		
    	scribble.clear();
	}
			
		public String getSavingFolder() {
			String defaultMappingDiskName = "L:";
			String studentFolderName = "CCWTemp";
			String problemFile = "test";
			
			if (getController() != null && getController().getLogger() != null) {
			 studentFolderName = getController().getLogger().getStudentName();//add later to logcontext/datashoptracer... maybe logger.
			 problemFile = getController().getLogger().getProblemName();
			}
			System.out.println("studentFolderName =" + studentFolderName);
			System.out.println("problemFile =" + problemFile);
			String	currentDir = defaultMappingDiskName + "/" + studentFolderName;  // "L:/CCWTemp/"
			
		    // Create multiple directories
	 // 	  String serverLocation = "L:/temp/";
	  	  boolean success = (new File(currentDir)).mkdirs();
	  	    if (success) {

	  	       System.out.println("Remote Disk Directories: " + currentDir + " created");
	  	    }
	  	    else {
	  	    	currentDir = System.getProperty("user.dir");
	  			System.out.println("currentDir =" + currentDir);
	  			new File(currentDir);
	  	    }


		    	    
			return currentDir;

		}

		public void mousePressed(MouseEvent e) {
		}
}

/**
 * This class is a custom component that supports scribbling. It also has a
 * popup menu that allows the scribble color to be set and provides access to
 * printing, cut-and-paste, and file loading and saving facilities. Note that it
 * extends Component rather than Canvas, making it "lightweight."
 */
class Scribble extends Component implements ActionListener {
  private static final Color colorArray = null;
  protected short last_x, last_y;                // Coordinates of last click.
  protected Vector lines = new Vector(256,256);  // Store the scribbles.
  protected Color current_color = Color.black;   // Current drawing color.
  protected int width, height;                   // The preferred size.
  protected PopupMenu popup;                     // The popup menu.
  protected Frame frame;                         // The frame we are within.
  protected JCommDrawingPad drawingPad;
  
  edu.cmu.oli.log.client.CurriculumLog CurriculumLog = new edu.cmu.oli.log.client.CurriculumLog();
	protected String  imageFileName = CurriculumLog.getUserGuid();  
	protected int     imageFileNameIndex = 0;
	Graphics g;
	// Color purple = g.setColor(0X551A8B); Color.getHSBColor(80.0F, 25.0F,131.0F),
	
	final  Color[] colorList = {Color.BLACK,  Color.RED, 
			Color.ORANGE, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.PINK, Color.GRAY};
	TutorController controller;
	private String fileSavingDirectory = ".";
	boolean directoryCreated = false;
	boolean imageSaved = false;   // if default set to true, will prevent to save all empty file.
  /** This constructor requires a Frame and a desired size */
  public Scribble(Frame frame, int width, int height, JCommDrawingPad drawingPad) {
    this.frame = frame;
    this.width = width;
    this.height = height;
    this.drawingPad = drawingPad;

    if (imageFileName == null) imageFileName = "test";
    // We handle scribbling with low-level events, so we must specify
    // which events we are interested in.
    this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    this.enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);

    // Create the popup menu using a loop.  Note the separation of menu
    // "action command" string from menu label.  Good for internationalization.
    String[] labels = new String[] {
//      "Clear", "Print", "Save", "Load", "Cut", "Copy", "Paste" };
     "Clear", "Submit"};
    String[] commands = new String[] {
//      "clear", "print", "save", "load", "cut", "copy", "paste" };
     "clear", "save"}; 
    popup = new PopupMenu();                   // Create the menu
    for(int i = 0; i < labels.length; i++) {
      MenuItem mi = new MenuItem(labels[i]);   // Create a menu item.
      mi.setActionCommand(commands[i]);        // Set its action command.
      mi.addActionListener(this);              // And its action listener.
      popup.add(mi);                           // Add item to the popup menu.
    }
    Menu colors = new Menu("Color");           // Create a submenu.
//    popup.add(colors);                         // And add it to the popup.
    String[] colornames = new String[] { "Black", "Red", "Green", "Blue"};
    for(int i = 0; i < colornames.length; i++) {
      MenuItem mi = new MenuItem(colornames[i]);  // Create the submenu items
      mi.setActionCommand(colornames[i]);         // in the same way.
      mi.addActionListener(this);
      colors.add(mi);
    }

    // Finally, register the popup menu with the component it appears over
    this.add(popup);

  }

	protected void paintComponent(Graphics graphics) {
//		System.err.println("paintComponent > " + this.getName() +" [ " 
//				+ width + " , " + height + " ]");
		
		Graphics g = graphics.create();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		
		g.setColor(Color.BLACK); 
		g.drawRect(0, 0, width, height);
			

		g.dispose();
	};
	
  /** Specifies big the component would like to be.  It always returns the
   *  preferred size passed to the Scribble() constructor */
  public Dimension getPreferredSize() { return new Dimension(width, height); }

  /** This is the ActionListener method invoked by the popup menu items */
  public void actionPerformed(ActionEvent event) {
    // Get the "action command" of the event, and dispatch based on that.
    // This method calls a lot of the interesting methods in this class.
    String command = event.getActionCommand();
    if (command.equals("clear")) clear();
    else if (command.equals("print")) print();
    else if (command.equals("save")) // save();
    {   
    	autoSave();
//		dirty = true;
//		sendValue();
    }
    else if (command.equals("load")) load();
    else if (command.equals("cut")) cut();
    else if (command.equals("copy")) copy();
    else if (command.equals("paste")) paste();
    else if (command.equals("Black")) current_color = Color.black;
    else if (command.equals("Red")) current_color = Color.red;
    else if (command.equals("Green")) current_color = Color.green;
    else if (command.equals("Blue")) current_color = Color.blue;
  }

  /** Draw all the saved lines of the scribble, in the appropriate colors */
  public void paint(Graphics g) {
    for(int i = 0; i < lines.size(); i++) {
      Line l = (Line)lines.elementAt(i);
      g.setColor(l.color);
      g.drawLine(l.x1, l.y1, l.x2, l.y2);
    }
  }

  /**
   * This is the low-level event-handling method called on mouse events
   * that do not involve mouse motion.  Note the use of isPopupTrigger()
   * to check for the platform-dependent popup menu posting event, and of
   * the show() method to make the popup visible.  If the menu is not posted,
   * then this method saves the coordinates of a mouse click or invokes
   * the superclass method.
   */
  public void processMouseEvent(MouseEvent e) {
    if (e.isPopupTrigger())                              // If popup trigger,
      popup.show(this, e.getX(), e.getY()); 
// pop up the menu.
    else if (e.getID() == MouseEvent.MOUSE_PRESSED) {
    //	saveImage(getImageFileName());
    	current_color = colorList[getImageFileNameIndex() % colorList.length];
//    	System.err.println("MOUSE_PRESSED");
      last_x = (short)e.getX(); last_y = (short)e.getY(); // Save position.
      imageSaved = false;
    }
    else super.processMouseEvent(e);  // Pass other event types on.
  }

  /**
   * This method is called for mouse motion events.  It adds a line to the
   * scribble, on screen, and in the saved representation
   */
  public void processMouseMotionEvent(MouseEvent e) {
   

    if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
//    	System.out.println("MOUSE_DRAGGED");
       g = getGraphics();                     // Object to draw with.
 //     g.setColor(current_color);                      // Set the current color.
       g.setColor(Color.BLACK);
 //      g.setFont(new Font("sansserif", Font.BOLD, 14));
      g.drawLine(last_x, last_y, e.getX(), e.getY()); // Draw this line
      lines.addElement(new Line(last_x, last_y,       // and save it, too.
                                (short) e.getX(), (short)e.getY(),
                                current_color));
      last_x = (short) e.getX();  // Remember current mouse coordinates.
      last_y = (short) e.getY();

    }
    else super.processMouseMotionEvent(e);  // Important!

  }

  /** Clear the scribble.  Invoked by popup menu */
  public void clear() {
    lines.removeAllElements();   // Throw out the saved scribble
    imageSaved = true;
    imageFileNameIndex = 0;
    repaint();                   // and redraw everything.
  }

  /** Print out the scribble.  Invoked by popup menu. */
  void print() {
    // Obtain a PrintJob object.  This posts a Print dialog.
    // printprefs (created below) stores user printing preferences.
    Toolkit toolkit = this.getToolkit();
    PrintJob job = toolkit.getPrintJob(frame, "Scribble", printprefs);

    // If the user clicked Cancel in the print dialog, then do nothing.
    if (job == null) return;

    // Get a Graphics object for the first page of output.
    Graphics page = job.getGraphics();

    // Check the size of the scribble component and of the page.
    Dimension size = this.getSize();
    Dimension pagesize = job.getPageDimension();

    // Center the output on the page.  Otherwise it would be
    // be scrunched up in the upper-left corner of the page.
    page.translate((pagesize.width - size.width)/2,
                   (pagesize.height - size.height)/2);

    // Draw a border around the output area, so it looks neat.
    page.drawRect(-1, -1, size.width+1, size.height+1);

    // Set a clipping region so our scribbles don't go outside the border.
    // On-screen this clipping happens automatically, but not on paper.
    page.setClip(0, 0, size.width, size.height);

    // Print this Scribble component.  By default this will just call paint().
    // This method is named print(), too, but that is just coincidence.
    this.print(page);

    // Finish up printing.
    page.dispose();   // End the page--send it to the printer.
    job.end();        // End the print job.
  }

  /** This Properties object stores the user print dialog settings. */
  private static Properties printprefs = new Properties();

  /**
   * The DataFlavor used for our particular type of cut-and-paste data.
   * This one will transfer data in the form of a serialized Vector object.
   * Note that in Java 1.1.1, this works intra-application, but not between
   * applications.  Java 1.1.1 inter-application data transfer is limited to
   * the pre-defined string and text data flavors.
   */
  public static final DataFlavor dataFlavor =
      new DataFlavor(Vector.class, "ScribbleVectorOfLines");

  /**
   * Copy the current scribble and store it in a SimpleSelection object
   * (defined below).  Then put that object on the clipboard for pasting.
   */
  public void copy() {
    // Get system clipboard
    Clipboard c = this.getToolkit().getSystemClipboard();
    // Copy and save the scribble in a Transferable object
    SimpleSelection s = new SimpleSelection(lines.clone(), dataFlavor);
    // Put that object on the clipboard
    c.setContents(s, s);
  }

  /** Cut is just like a copy, except we erase the scribble afterwards */
  public void cut() { copy(); clear();  }

  /**
   * Ask for the Transferable contents of the system clipboard, then ask that
   * object for the scribble data it represents.  If either step fails, beep!
   */
  public void paste() {
    Clipboard c = this.getToolkit().getSystemClipboard();  // Get clipboard.
    Transferable t = c.getContents(this);                  // Get its contents.
    if (t == null) {              // If there is nothing to paste, beep.
      this.getToolkit().beep();
      return;
    }
    try {
      // Ask for clipboard contents to be converted to our data flavor.
      // This will throw an exception if our flavor is not supported.
      Vector newlines = (Vector) t.getTransferData(dataFlavor);
      // Add all those pasted lines to our scribble.
      for(int i = 0; i < newlines.size(); i++)
        lines.addElement(newlines.elementAt(i));
      // And redraw the whole thing
      repaint();
    }
    catch (UnsupportedFlavorException e) {
      this.getToolkit().beep();   // If clipboard has some other type of data
    }
    catch (Exception e) {
      this.getToolkit().beep();   // Or if anything else goes wrong...
    }
  }

  /**
   * This nested class implements the Transferable and ClipboardOwner
   * interfaces used in data transfer.  It is a simple class that remembers a
   * selected object and makes it available in only one specified flavor.
   */
  static class SimpleSelection implements Transferable, ClipboardOwner {
    protected Object selection;    // The data to be transferred.
    protected DataFlavor flavor;   // The one data flavor supported.
    public SimpleSelection(Object selection, DataFlavor flavor) {
      this.selection = selection;  // Specify data.
      this.flavor = flavor;        // Specify flavor.
    }

    /** Return the list of supported flavors.  Just one in this case */
    public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] { flavor };
    }
    /** Check whether we support a specified flavor */
    public boolean isDataFlavorSupported(DataFlavor f) {
      return f.equals(flavor);
    }
    /** If the flavor is right, transfer the data (i.e. return it) */
    public Object getTransferData(DataFlavor f)
         throws UnsupportedFlavorException {
      if (f.equals(flavor)) return selection;
      else throw new UnsupportedFlavorException(f);
    }

    /** This is the ClipboardOwner method.  Called when the data is no
     *  longer on the clipboard.  In this case, we don't need to do much. */
    public void lostOwnership(Clipboard c, Transferable t) {
      selection = null;
    }
  }

  /**
   * Prompt the user for a filename, and save the scribble in that file.
   * Serialize the vector of lines with an ObjectOutputStream.
   * Compress the serialized objects with a GZIPOutputStream.
   * Write the compressed, serialized data to a file with a FileOutputStream.
   * Don't forget to flush and close the stream.
   */
  public void save() {
    // Create a file dialog to query the user for a filename.
	File file = DialogUtilities.chooseFile(null, null, "Save Scribble", "Save",
			(BR_Controller) controller);

	if (file != null) {           // If user didn't click "Cancel".
      try {
        // Create the necessary output streams to save the scribble.
        FileOutputStream fos = new FileOutputStream(file); // Save to file
        GZIPOutputStream gzos = new GZIPOutputStream(fos);     // Compressed
        ObjectOutputStream out = new ObjectOutputStream(gzos); // Save objects
        out.writeObject(lines);      // Write the entire Vector of scribbles
        out.flush();                 // Always flush the output.
        out.close();                 // And close the stream.
      }
      // Print out exceptions.  We should really display them in a dialog...
      catch (IOException e) { System.out.println(e); }
    }
  }

  public void autoSave() {
	  this.controller = drawingPad.getController();
//  	String filename = "test.jpeg";
  	saveImage(getFileSavingDirectory() + "/" + getImageFileName() + ".jpeg");
  	clear();

  
  }
  /**
   * Prompt for a filename, and load a scribble from that file.
   * Read compressed, serialized data with a FileInputStream.
   * Uncompress that data with a GZIPInputStream.
   * Deserialize the vector of lines with a ObjectInputStream.
   * Replace current data with new data, and redraw everything.
   */
  public void load() {
    // Create a file dialog to query the user for a filename.
	File file = DialogUtilities.chooseFile(null, null, "Load Scribble", "Load",
			(BR_Controller) controller);
    if (file != null) {           // If user didn't click "Cancel".
      try {
        // Create necessary input streams
        FileInputStream fis = new FileInputStream(file); // Read from file
        GZIPInputStream gzis = new GZIPInputStream(fis);     // Uncompress
        ObjectInputStream in = new ObjectInputStream(gzis);  // Read objects
        // Read in an object.  It should be a vector of scribbles
        Vector newlines = (Vector)in.readObject();
        in.close();                    // Close the stream.
        lines = newlines;              // Set the Vector of lines.
        repaint();                     // And redisplay the scribble.
      }
      // Print out exceptions.  We should really display them in a dialog...
      catch (Exception e) { System.out.println(e); }
    }
  }

  /** A class to store the coordinates and color of one scribbled line.
   *  The complete scribble is stored as a Vector of these objects */
  static class Line implements Serializable {
    public short x1, y1, x2;
    public Color color;
    public Line(short x1, short y1, short x2, short y2, Color c) {
      this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2; this.color = c;
    }
	public short y2;
  }
  
  public void saveImage(String fileName) {

//		if (fileName == null) {
//			int returnVal = chooser.showSaveDialog(frame);
//			File f;
//			if (returnVal == JFileChooser.APPROVE_OPTION) {
//				f = chooser.getSelectedFile();
//				fileName = f.getName(); // Get the user's response
//			}
//		}
	    if (imageSaved)
	    	return;
		if (fileName == null)  // User clicked "Cancel".
			return;
		if (trace.getDebugCode("inter")) trace.out("inter", "saveImage filename = " + fileName);
		try {
			int width = getWidth(), height = getHeight();
			Image image = createImage(width, height);
			paint(image.getGraphics());
			int thumbWidth = getWidth(), thumbHeight = getHeight();
			// draw original image to thumbnail image object and
			// scale it to the new size on-the-fly
			BufferedImage thumbImage = new BufferedImage(thumbWidth,
					thumbHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = thumbImage.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.drawImage(image, 0, 0, width, height, null);
			// save thumbnail image to OUTFILE
			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
			if (writers == null || !writers.hasNext()) {
				trace.err("JCommDrawingPad.saveImage(\""+fileName+"\"): no image writers for jpeg");
				return;
			}
			ImageWriter writer = (ImageWriter)writers.next();
			File f = new File(fileName);
			ImageOutputStream ios = ImageIO.createImageOutputStream(f);
			writer.setOutput(ios);
			ImageWriteParam iwp = new ImageWriteParam() {
				{ compressionQuality = (float) 1.0; }
			};
			writer.write(null, new IIOImage(thumbImage, null, null), iwp);
			ios.flush();
			ios.close();
		}
		// Print out exceptions. We should really display them in a
		// dialog...
		catch (Exception e) {
			System.out.println(e);
		}
	}
  
  
 /* public void loadImage()  {
		int returnVal = chooser.showOpenDialog(frame);
		File f = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			f = chooser.getSelectedFile();
		}

		String filename = f.getName(); // Get the user's response
		System.out.println("Load filename = " + filename);
		if (filename != null) {
			int thumbWidth = 125, thumbHeight = 125;

			Image image = Toolkit.getDefaultToolkit().getImage(filename);

		    g = this.getGraphics();	    
		    boolean display  = g.drawImage(image, 125, 125, DrawingPad);
		    System.out.println("display = " + display);
			DrawingPad.repaint();   
		}
	}*/

	public String getImageFileName() {
		String problemFile = controller.getLogger().getProblemName();
		String FileNameHeader = ((problemFile.length() == 0) ? Character.toString((char) ((getImageFileNameIndex() + 65) % 90)) : problemFile);
		Date lastEvaluationTime = new Date();
		

        System.out.println("FileNameHeader =" + FileNameHeader);
		String FileNameIndex = FileNameHeader + "_" +
							   Integer.toString(lastEvaluationTime.getDate()) + "_" +
							   Integer.toString(lastEvaluationTime.getHours()) + "_" +
							   Integer.toString(lastEvaluationTime.getMinutes()) +  "_" +
							   Integer.toString(lastEvaluationTime.getSeconds()); //  + ".jpeg";
		System.out.println("FileNameIndex =" + FileNameIndex);
		return  FileNameIndex ; // getImageFileNameIndex() 
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	public int getImageFileNameIndex() {
		return imageFileNameIndex++;
	}

	public void setImageFileNameIndex(int imageFileNameIndex) {
		this.imageFileNameIndex = imageFileNameIndex;
	}
	
	// fileSavingDirectory = getScribbleSavingFolder();
	// scribble.setImageFileName(fileSavingDirectory);
	
	public String getScribbleSavingFolder() {
		String defaultMappingDiskName = "L:";
		String studentFolderName = "CCWTemp";
		String problemFile = "test";
		
		if (controller.getLogger() != null) {
		 studentFolderName = controller.getLogger().getStudentName();
		 problemFile = controller.getLogger().getProblemName();
		}
		System.out.println("studentFolderName =" + studentFolderName);
		System.out.println("problemFile =" + problemFile);
		String	currentDir = defaultMappingDiskName + "/" + studentFolderName;  // "L:/CCWTemp/"
		
	    // Create multiple directories
 // 	  String serverLocation = "L:/temp/";
      
		File file = new File(currentDir);
		if (!file.exists()) {
			boolean success = (new File(currentDir)).mkdirs();
			if (success) {
 // 	    	directoryCreated = true;
 // 	    	fileSavingDirectory = currentDir;
  	       System.out.println("Remote Disk Directories: " + fileSavingDirectory + " created");
			}
//  	    else {
//  	    	currentDir = System.getProperty("user.dir");
//  			System.out.println("currentDir =" + currentDir);
//  			new File(currentDir);
//  	    	 }
			}

	    	    
		return currentDir;    // fileSavingDirectory;

	}

	public String getFileSavingDirectory() {
		if (!directoryCreated)
		   fileSavingDirectory = getScribbleSavingFolder();
		return fileSavingDirectory;
	}

	public void setFileSavingDirectory(String fileSavingDirectory) {
		this.fileSavingDirectory = fileSavingDirectory;
	}
}
