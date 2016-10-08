package edu.cmu.pact.ctat.view;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;

import pact.CommWidgets.TutorWrapper;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Log.AuthorLogListener;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.trace;

//////////////////////////////////////////////////////
/**
 * This class saves the location and the size of any window that 
 * extends it into the .brPrefs (Behavior Recorder Preferences) file.
 * 
 * Any class that uses this must call setName("example window name")
 * so that CTATWindow can identify the window.
 * 
 * It must also call applyPreferences() to apply the preferences 
 * to this window.
 * 
 */
//////////////////////////////////////////////////////

public abstract class AbstractCtatWindow 
    extends JFrame 
    implements ComponentListener, WindowFocusListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8372065524741088804L;

	public void addWindowListener(WindowListener wl) {
	super.addWindowListener(wl);
    }
	
	
    public JMenuItem showBR;
    public JMenuItem showWMEEditorWindow;
    public JMenuItem showJessConsoleWindow;
    public JMenuItem showConflictTreeWindow;
    public JMenuItem showStudentInterface;
    private boolean isDockable;
	
    public final static String DEFAULT_JESS_EDITOR = "Jess File Editor";
    public final static String AUTHORINGTOOLS_LISTENING_PORT = "Authoring Tools Listening Port";
    
    //Constants for logging
    public static final String FOCUS_LOST = "FOCUS_LOST";
    public static final String FOCUS_GAINED = "FOCUS_GAINED";
    public static final String CLOSE = "CLOSE";
    public static final String ICONIFY = "ICONIFY";
    public static final String DEICONIFY = "DEICONIFY";
    
    protected CTAT_Controller controller;
    protected CTAT_Launcher server;
    
	public AbstractCtatWindow(CTAT_Launcher server) {
	
		addComponentListener(this);
		isDockable = true;
		this.server = server;
	
		this.addWindowFocusListener(this);
		if(this.server.getLoggingSupport()!=null)
			this.addWindowListener(new AuthorLogListener(this.server.getLoggingSupport()));
			
	    Image image = new ImageIcon("ctaticon.png").getImage();
	    
	    if (image != null && image.getHeight(null) != -1) {
	        setIconImage(image);
	    }
	}
	
    //////////////////////////////////////////////////////
    /**
     * Reads window-related data from the preferences 
     * model and applies it to this window
     */
    //////////////////////////////////////////////////////
    public void applyPreferences() {
	
	
	trace.out ("wh", "!! apply preferences for " + getName());
	PreferencesModel model = this.server.getPreferencesModel();
	
	String x_loc = getName() + " Location X";
	String y_loc = getName() + " Location Y";
	String width = getName() + " Width";
	String height = getName() + " Height";
	
	Integer X = model.getIntegerValue(x_loc);
	Integer Y = model.getIntegerValue(y_loc);
	
	int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().width;
	
        
	screenWidth -= 500;
	screenHeight -= 500;
	if ((X == null || Y == null) && this instanceof TutorWrapper) {
	    setLocation (100, 100);
	} else if (X == null || Y == null) {
	    int x = 50 + 75;
	    int y = 50 + 75;
	    if (x > screenWidth)
		x = screenWidth;
	    if (y > screenHeight)
		y = screenHeight;
	    
	    setLocation(new Point(x, y));
	} else {
	    Point p = new Point(X.intValue(), Y.intValue());
	    setLocation(p);
	}
	
	
	Integer Width = model.getIntegerValue(width);
	Integer Height = model.getIntegerValue(height);
	
	trace.out ("wh", "height = " + Height + " width = " + Width);
	
	if (Height != null && Width != null) {
	    this.setSize(new Dimension(Width.intValue(), Height.intValue()));
	}
	
	trace.out ("wh", "size = " + this.getSize() + " location = " + this.getLocation());
	
    }
    
    public void windowGainedFocus(WindowEvent e) {
    	if(getServer().getLoggingSupport()!=null)
    		getServer().getLoggingSupport().authorActionLog(AuthorActionLog.CTAT_WINDOW, 
    			AbstractCtatWindow.FOCUS_GAINED,this.getName(), "", "");
	
	return;
    }
    
    public void windowLostFocus(WindowEvent e) {
    	if(getServer().getLoggingSupport()!=null)
    		getServer().getLoggingSupport().authorActionLog(AuthorActionLog.CTAT_WINDOW, 
			    AbstractCtatWindow.FOCUS_LOST, this.getName(), "", "");
    }
    
    
    // If we are in docked windows mode, then return the docked window
    // frame.  Else return this frame.
    public JFrame getActiveWindow() {
    	JFrame result = getServer().getDockedFrame();
    	if (result == null)
    		return this;
    	else
    		return result; 
    }
    
    
    public void setDockable(boolean dockable) {
	isDockable = dockable;
    }
    
    /**
     * @return
     */
    public boolean isDockable() {
	return isDockable;
    }
    
    
    //	public void validate() {
    //		controller.getDockedFrame().validate();
    //	}
    //	
    public void storeLocation() {
	
	String loc_x = this.getName() + " Location X";
	String loc_y = this.getName() + " Location Y";
	
	Point p = this.getLocation();
	
	trace.out ("wh", "store location for " + this.getName() 
		   + " p = " + p);
	
	getServer().getPreferencesModel().setIntegerValue(loc_x, new Integer(p.x));
	getServer().getPreferencesModel().setIntegerValue(loc_y, new Integer(p.y));
    }
    
    public void storeSize() {
	
	String width = this.getName() + " Width";
	String height = this.getName() + " Height";
	
	
	Dimension d = this.getSize();
	trace.out ("wh", "store size for " + this.getName() +
		   " d = " + d);
	
	
	getServer().getPreferencesModel().setIntegerValue (width,new Integer(d.width));
	getServer().getPreferencesModel().setIntegerValue (height,new Integer(d.height));
    }

    public void componentHidden(ComponentEvent e) { }
    
    public void componentMoved(ComponentEvent e) 
    {
    	storeLocation();
    }

    public void componentResized(ComponentEvent e) 
    {
    	storeSize();
    }
    
    public void componentShown(ComponentEvent e) { }
    
    private CTAT_Launcher getServer() {
    	return this.server;
    }
}
