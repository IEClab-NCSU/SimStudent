package pact.CommWidgets;

import java.awt.Dimension;

import javax.swing.JComponent;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindowInterface;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pslc.logging.LogContext;

/**
 * @author sanket
 * 
 */
public class TutorWrapper extends TutorWindow implements StudentInterfaceWrapper {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4557612886946729912L;

	public TutorWrapper(TutorController controller) {
        super(controller);
        
//      CTAT1496 redundant: initTutorWindow() also sets up a listener
        
        super.initTutorWindow();
        
        wrapperSupport.getController().setStudentInterface(this);
        
            
    } 

    /**
     * @return {@link WrapperSupport#getLogger()} 
     * @see pact.CommWidgets.StudentInterfaceWrapper#getLogger()
     */
    public LogContext getLogger() {
    	return wrapperSupport.getLogger();
    }

    public void setLocation(int x, int y) {
        super.setLocation (x,y);
    }


    public CTAT_Options setTutorPanel(JComponent tutorPanel) {
    	CTAT_Options options = wrapperSupport.setTutorPanel(tutorPanel);
        loadPreferences();
        validate(); // "wh"
        trace.out("setTutorPanel() post validate() min "+
        		getContentPane().getMinimumSize()+", pref "+
				getContentPane().getPreferredSize()+", max "+
				getContentPane().getMaximumSize());
        repaint();
        
        return options;
    }
    
    public void paint(java.awt.Graphics g) {
    	if (trace.getDebugCode("paint")) trace.out("paint", "paint(): min " +getMinimumSize()+", pref "+
    			getPreferredSize()+", max "+getMaximumSize());
    	super.paint(g);
    }
    
    public void update(java.awt.Graphics g) {
    	if (trace.getDebugCode("inter")) trace.out("inter", "update(): min " +getMinimumSize()+", pref "+
    			getPreferredSize()+", max "+getMaximumSize());
    	super.update(g);
    }

    /**
     * Access to the {@link WrapperSupport} object with common methods.
     * @return WrapperSupport 
     */
    public WrapperSupport getWrapperSupport() {
		return wrapperSupport;
	}
    /**
     * @param loginEnabled
     */
    public void enableLMSLogin(final boolean loginEnabled) {
        lmsLoginMenuItem.setVisible(loginEnabled);
    }

    /**
     * Convenience method for access to hint interface.
     * @return {@link #wrapperSupport}.getHintInterface()
     */
    public HintWindowInterface getHintInterface() {
    	return wrapperSupport.getHintInterface();
    }

	/**
	 * Access to the student interface panel in the wrapper's container.
	 * @return tutorPanel the student interface panel
	 */
    public JComponent getTutorPanel() {
    	return wrapperSupport.getTutorPanel();
    }
    
    /**
     * 
     */
    private void loadPreferences() {
    	wrapperSupport.loadPreferences();

        String width = "Student Interface Width";
        String height = "Student Interface Height";

        Integer Width = getWrapperSupport().getController().getPreferencesModel().getIntegerValue(width);
        Integer Height = getWrapperSupport().getController().getPreferencesModel().getIntegerValue(height);

        if (Width != null && Height != null)
            setSize(Width.intValue(), Height.intValue());
        else if (! wrapperSupport.getUseSeparateHintWindow() && wrapperSupport.getHorizontalSplitPane() != null)
            setSize(wrapperSupport.getHorizontalSplitPane().getPreferredSize());
        else {
        	Dimension d = getContentPane().getPreferredSize();
        	d.width = Math.max(d.width, 400);   // ensure large enough to be visible
        	d.height = Math.max(d.height, 400);
            setSize(new Dimension(d.width+14, d.height+68)); // add space for title, menu, frame
        }
    }
    
	/**
	 * If student feedback is suppressed, may need to notify external environment
	 * @param suppressStudentFeedback
	 */
	public void setSuppressStudentFeedback(boolean suppressStudentFeedback) {}

    /**
     * Request a hint from the tutoring system.
     * FIXME  STUB!!
     * @see pact.CommWidgets.StudentInterfaceWrapper#requestHint()
     */
	public void requestHint() {}

	/**
	 * Another stub!
	 */
	public void requestDone() {
		return;
	}
	
	public void setTutorResizable(boolean resizable){
		super.setTutorResizable(resizable);
	}
}


