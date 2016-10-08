/*
 * Copyright 2005 Carnegie Mellon University.
 */
package pact.CommWidgets;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import pact.CommWidgets.event.StudentActionEvent;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintPanel;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindow;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindowInterface;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.Hints;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.client.HintMessagesManager;
import edu.cmu.pact.client.HintMessagesManagerForClient;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.ctatview.CtatMenuBar;
import edu.cmu.pslc.logging.LogContext;

/**
 * Common code for both CTAT and CL student interfaces.
 * @author sewall
 */
public class WrapperSupport implements ActionListener, MouseListener, ComponentListener,
		AncestorListener, SendsDone {

	/** The ubiquitous controller. */
    protected TutorController controller = null;

    /** The specific student interface. */
    protected JComponent tutorPanel;

	/** The Swing container we're serving. */
	protected final Container container;

    private JSplitPane verticalSplitPane;

    private JSplitPane horizontalSplitPane;

    private JScrollPane skillsPane;

    private HintWindowInterface hintPanel;
    private HintMessagesManager hintMessagesManager;
    // True if there shouldn't be a hint window on the student interface
    // Needed for SimSt Peer Learning Environment
    private boolean runningSimStPLE = false;
    public boolean isRunningSimStPLE() { return runningSimStPLE; }
    public void setRunningSimStPLE(boolean diableHintWindow) {
        this.runningSimStPLE = diableHintWindow;
    }
    
    private boolean runningSimStGameShow = false;
    public boolean isRunningSimStGameShow() { return runningSimStGameShow; }
    public void setRunningSimStGameShow(boolean diableHintWindow) {
        this.runningSimStGameShow = diableHintWindow;
    }

    /** Whether to use a separate, popup hint window or an integrated one. */
    private boolean useSeparateHintWindow;
	
	/** Objects to receive doneButton actions. */
	List<ActionListener> doneListeners = new LinkedList<ActionListener>();

	/** A list of hint-related components. */
	private List<Object> hintComponents = new LinkedList<Object>();

	/** A list of Done buttons. */
	private List<JCommButton> doneButtons = new LinkedList<JCommButton>();
	
	/** A map {@link Class#getSimpleName()}=>{@link Integer} for {@link #checkForUniqueName(Component)}. */
	private Map<String, Integer> nameGenerator = new LinkedHashMap<String, Integer>();

	static final String RETRACT_LAST_STEP = CtatMenuBar.RETRACT_LAST_STEP;

	/**
	 * Set the internal container.
	 * @param container
	 */
	public WrapperSupport(Container container) {
		super();
		this.container = container;
		nameGenerator.clear();
	}
	
	/**
	 * Handle a {@link StudentActionEvent}. Actions:<ul>
	 * <li>if the event source is the Done button, call
	 * {@link #fireDoneActionPerformed(Object) fireDoneActionPerformed(source)} </li>
	 * </ul>
	 * @param sae the event
	 */
	public void studentActionPerformed(StudentActionEvent sae) {
		Object source = sae.getSource();
		if (source instanceof JCommButton) {
			JCommButton doneBtn = (JCommButton) source;
			if (HintPanel.DONE.equalsIgnoreCase(doneBtn.getCommName()))
				fireDoneActionPerformed(doneBtn);
		}
	}

	/**
	 * Add a listener interested in the Done action event.
	 * @param doneListener
	 */
	public synchronized void addActionListener(ActionListener doneListener) {
		doneListeners.add(doneListener);
	}

	/**
	 * Remove listener no longer interested in the Done action event.
	 * @param doneListener
	 */
	public synchronized void removeActionListener(ActionListener doneListener) {
		doneListeners.remove(doneListener);
	}
	
    /**
     * This method sets the "comm name" property of any comm widget in the
     * student interface panel to be equal to the declared name of the field.
     * Also sets the options of the interface using the ctat_options object.
     * @param controller 
     */ 
    public CTAT_Options examineInterface(Container container, TutorController controller) {
        
        CTAT_Options options = null;
        synchronized (container.getTreeLock()) { 
        	Component[] components = container.getComponents();
        	for (int j = 0; j < components.length; j++) {
        		Component component = components[j];
        		try {
        			CTAT_Options opts = null;
        			String name = component.getName();
        			if(name == null || name.length() < 1)
        				name = component.getClass().getSimpleName();
        			if (trace.getDebugCode("inter")) trace.out ("inter", "examineInterface(): obj = "+name+" "+component);
        			if (component instanceof JTabbedPane) {
        				opts = examineTabbedPane((JTabbedPane) component);
        			}
        			else if (component instanceof JCommWidget) {
        				final JCommWidget dw = ((JCommWidget) component);

        				if (trace.getDebugCode("inter"))
        					trace.out ("inter", "JCommWidget "+name+", getCommName() "+dw.getCommName()+", isHintBtn() "+dw.isHintBtn());
        				if(dw.getCommName() != null && dw.getCommName().length() > 0)
        					name = dw.getCommName();
        				name = checkForUniqueName(name);
        				dw.setCommName(name, controller);
        				if (HintPanel.DONE.equalsIgnoreCase(name)) {
        					dw.addStudentActionListener(this);
        					doneButtons.add((JCommButton) dw);
        				} else if (dw.isDoneButton()) {
        					doneButtons.add((JCommButton) dw);
        				}
        				if (dw.isHintBtn())
        					hintComponents.add(dw);
        			}
        			else if (component instanceof JScrollPane) {
        				JViewport viewport = ((JScrollPane) component).getViewport();
        				if (viewport != null && viewport.getView() instanceof Container)
        					opts = examineInterface((Container) viewport.getView(), controller);
        			}
        			//nbarba 01/16/2014: JLayeredPane support for SimStudent
        			else if (component instanceof JLayeredPane) {
        				opts = examineInterface((JLayeredPane) component, controller);
        			}
        			else if (component instanceof CTAT_Options) {
        				if (trace.getDebugCode("inter")) trace.out ("inter", "OPTIONS FOUND");
        				opts = (CTAT_Options) component;
        			}
        			else if (component instanceof JPanel) {
        				opts = examineInterface((JPanel) component, controller);
        			}
        			if (options == null && opts != null)
        				options = opts;                     // save first CTAT_Options found
        			if (component instanceof Component && !(component instanceof JCommWidget)) {
        				if (trace.getDebugCode("inter"))
        					trace.out ("inter", "hintComponent "+Hints.isHintComponent((Component) component));
        				if (Hints.isHintComponent((Component) component))
        					hintComponents.add(component);
        			}
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        	}
        }  // end synch()
        if(trace.getDebugCode("inter"))
        	trace.out("inter", "examineInterface() widget keys "+controller.getCommWidgetTable().keySet());
        return options;
    }

    /**
     * Ensure that a component name is unique by checking it against the table
     * {@link #nameGenerator}. If not, modify it to take the form <i>nameN</i>,
     * where <i>name</i> is the given argument, and <i>N</i> is a name-specific
     * serial number starting at 2. Hence "Done", "button2", etc. 
     * @param name
     * @return name if unique; else name suffixed with serial number starting at 2
     */
    private String checkForUniqueName(String name) {
		Integer nextSerialNo = nameGenerator.get(name);
		if(nextSerialNo == null)
			nextSerialNo = new Integer(1);
		else
			nextSerialNo = new Integer(nextSerialNo.intValue()+1);
		nameGenerator.put(name.toString(), nextSerialNo);
		if(nextSerialNo.intValue() > 1)
			return name+nextSerialNo.intValue();
		else
			return name;
	}
    
	/**
     * Setup needed when adding the student interface panel.
     * @param tutorPanel the student interface panel
     */
    public CTAT_Options setTutorPanel(JComponent tutorPanel) {

    	trace.out ("inter", "set tutor panel: " + tutorPanel);
        this.tutorPanel = tutorPanel;
		
        CTAT_Options options = null;
        doneButtons.clear();
        hintComponents.clear();
        if (tutorPanel instanceof JTabbedPane)
        	options = examineTabbedPane((JTabbedPane) tutorPanel);
        else
        	options = examineInterface(tutorPanel, controller);
        if (options == null)
            options = new CTAT_Options();
        
        useSeparateHintWindow = options.getSeparateHintWindow();

//        if (controller.getShowAdvanceProblemMenu()) {
//            showAdvanceProblemMenuItem();
//        }
        
		controller.initAllWidgets_movedFromCommWidget();
        
        tutorPanel.addMouseListener (this);

        // zz change 10/05/04: to view all of widgets on the interface panel
        Dimension tutorPanelPreferredSize = getTutorPanelPreferredSize();
        tutorPanel.setPreferredSize(tutorPanelPreferredSize);
        
        setupHintWindow(tutorPanel, tutorPanelPreferredSize);
        
        return options;
    }
    
    /**
     * Call {@link #examineInterface(Container, BR_Controller)} on each JPanel
     * in a Tabbed Pane.
     * @param tabbedPane
     * @param controller2
     * @return result of examineInterface on last tab pane
     */
    private CTAT_Options examineTabbedPane(JTabbedPane tabbedPane) {
    	CTAT_Options options = null;
    	int tabCount = tabbedPane.getTabCount();
		if (trace.getDebugCode("inter")) trace.out("inter", "examineTabbedPane tabCount"+tabCount);
    	for (int i = 0; i < tabCount; i++) {
    		Component c = tabbedPane.getComponentAt(i);
    		if (trace.getDebugCode("inter")) trace.out("inter", "examineTabbedPane c["+i+"] "+c);
    		if (c instanceof JScrollPane) { 
            	JViewport viewport = ((JScrollPane) c).getViewport();
            	if (viewport != null && viewport.getView() instanceof Container)
            		options = examineInterface((Container) viewport.getView(), controller);
    		}
    		else if (c instanceof Container)
    			options = examineInterface((Container) c, controller);
    	}
		return options;
	}

	/**
     * 
     */
    public void loadPreferences() {
    	if (trace.getDebugCode("br")) trace.out("br", "WrapperSupport.loadPreferences() now a no-op");
    }

    public boolean hasHintButton() {
    	Component[] allComponents = tutorPanel.getComponents();
        Component tempComponent;
        
        for (int i = 0; i < allComponents.length; i++)
            if ((tempComponent = allComponents[i]) instanceof JCommButton
            		&& ((JCommButton)tempComponent).getText().equalsIgnoreCase("Hint"))
            	return true;
        return false;
    }

    private Dimension getTutorPanelPreferredSize() {
    	Dimension result = getComponentPreferredSize(tutorPanel);
    	return new java.awt.Dimension(result.width + 15, result.height + 15);
    }
    
    private Dimension getComponentPreferredSize(JComponent panel){

        Component[] allComponents = panel.getComponents();
        Component tempComponent;
        int maxWidth = 0;
        int maxHeight = 0;

        if (trace.getDebugCode("wh")) trace.out("wh","start getComponentPreferredSize() for " + panel.getClass().getName());
        for (int i = 0; i < allComponents.length; i++) {
            tempComponent = allComponents[i];
            if (tempComponent instanceof JCommWidget) {
            	if (trace.getDebugCode("wh")) trace.out("wh","ui component " + tempComponent.getName()
            			+ ":" + tempComponent.getClass().getName()
            			+ " x " + tempComponent.getLocation().x + ", width "
            			+ tempComponent.getSize().width
            			+ ", y " + tempComponent.getLocation().y + ", height "
            			+ tempComponent.getSize().height);
            	maxWidth = Math.max(maxWidth, tempComponent.getLocation().x
            			+ tempComponent.getSize().width);
            	maxHeight = Math.max(maxHeight, tempComponent.getLocation().y
            			+ tempComponent.getSize().height);
            }
            else {
            	Dimension tempSize = getComponentPreferredSize((JComponent)tempComponent);
              	if (tempComponent instanceof JTabbedPane) {
            		int tabWidth = 128;
            		for (int j = 0; j < ((JTabbedPane)tempComponent).getTabCount(); j++) {
            			if (trace.getDebugCode("wh")) trace.out("wh", "tab: "+ j + " bounds: "
            					+ ((JTabbedPane)tempComponent).getBoundsAt(j));
            			tabWidth += ((JTabbedPane)tempComponent).getBoundsAt(j).width;
            		}
            		tempSize.width = Math.max(tempSize.width, tabWidth);

             		tempSize.width += 20;
              		tempSize.height += 20;
              	}
              	tempComponent.setPreferredSize(tempSize);

              	if (trace.getDebugCode("wh")) trace.out("wh","ui component " + tempComponent.getName()
            			+ ":" + tempComponent.getClass().getName()
            			+ " x " + tempComponent.getLocation().x + ", width "
            			+ tempComponent.getPreferredSize().width
            			+ ", y " + tempComponent.getLocation().y + ", height "
            			+ tempComponent.getPreferredSize().height);
            	maxWidth = Math.max(maxWidth, tempComponent.getLocation().x
            			+ tempComponent.getPreferredSize().width);
            	maxHeight = Math.max(maxHeight, tempComponent.getLocation().y
            			+ tempComponent.getPreferredSize().height);
            }
        }
        Dimension result = new java.awt.Dimension(maxWidth, maxHeight); 
        if (trace.getDebugCode("wh")) trace.out("wh","getComponentPreferredSize() for " + panel.getClass().getName()
        		+ " returns " + result);
        return result;
    }

    protected void setupHintWindow(JComponent tutorPanel, Dimension tutorPanelPreferredSize) { 

        JScrollPane sp = new JScrollPane(tutorPanel);
        sp.setMinimumSize(tutorPanelPreferredSize);
        if (trace.getDebugCode("wh")) trace.out("wh", "setupHintWindow("+tutorPanel+") min "+
                container.getMinimumSize()+", pref "+
                container.getPreferredSize()+", max "+
                container.getMaximumSize());
        if (getUseSeparateHintWindow()) { 
            Frame parentFrame = getOwnerFrame(container);
            hintPanel =
                new HintWindow(parentFrame, getHintMessagesManager());
            if (container instanceof JComponent)
                ((JComponent) container).addAncestorListener(this);
            if (VersionInformation.includesCL() && container instanceof cl.ui.tools.tutorable.CTATTool) {
                ((cl.ui.tools.tutorable.CTATTool) container).setMinimumSize(tutorPanelPreferredSize);
                ((cl.ui.tools.tutorable.CTATTool) container).setPreferredSize(tutorPanelPreferredSize);
                container.add(tutorPanel);
                if (trace.getDebugCode("wh")) trace.out("wh", "setupHintWindow() called container "+container+
                        "("+container.getClass().getName()+").add("+tutorPanel+")");
            } else {
                container.add(sp);
                if (trace.getDebugCode("wh")) trace.out("wh", "setupHintWindow() called container "+container+
                        "("+container.getClass().getName()+").add("+sp+")");
            }
        } else {
            hintPanel = new HintPanel(controller, getHintMessagesManager());
            JCommButton doneBtn = (JCommButton) hintPanel.getDoneButton();
            if (doneBtn != null) {
                doneBtn.addStudentActionListener(this);
                doneButtons.add(doneBtn);
            }
            JCommButton hintBtn = (JCommButton) hintPanel.getHintButton();
            if (hintBtn != null)
            	hintComponents.add(hintBtn);
            buildIntegratedHintPanel(sp);
        }
        if (trace.getDebugCode("wh")) trace.out("wh", "setTutorPanel() post setupHintWindow min "+
                container.getMinimumSize()+", pref "+
                container.getPreferredSize()+", max "+
                container.getMaximumSize());
    }
	
    public HintMessagesManager getHintMessagesManager() {
    	if(hintMessagesManager==null){
    		hintMessagesManager = controller.getHintMessagesManager();
    	}
    	return hintMessagesManager;
    }
    /**
     * Find the top-level JFrame in a container's containment hierarchy.
     * @param c container to analyze
     * @return top-level JFrame or null if none
     */
    public static Frame getOwnerFrame(Container container) {
    	Container owner = null;
		if (container == null)
			return null;
		if (container instanceof Window)
			owner = ((Window) container).getOwner();
		if (owner == null)
			owner = container.getParent();
		if (trace.getDebugCode("wh")) trace.out("wh", "getOwnerFrame() container "+
				(container == null ? "null" : container.getClass().getName())+
				", owner "+(owner == null ? "null" : owner.getClass().getName()));
		if (owner == null && container instanceof Frame)
			return (Frame) container;
		else if (owner == container && container instanceof Frame)
			return (Frame) container;
		else
			return getOwnerFrame(owner);
	}
    
    /**
     * @param sp
     */
    private void buildIntegratedHintPanel(JScrollPane sp) {
        String opSystem = System.getProperty("os.name");

        boolean winSystem = true;

        if (opSystem.indexOf("Windows") < 0)
            winSystem = false;

        int hintWinMiniLength = 250;
        if (!winSystem)
            hintWinMiniLength = 340;


        JComponent hintFrame = (JComponent) getHintInterface();
        hintFrame.setPreferredSize(
                new Dimension(hintWinMiniLength, 300));
        hintFrame.setMinimumSize(
                new Dimension(hintWinMiniLength, 150));

        hintFrame.addComponentListener(this);

        skillsPane = new JScrollPane(new JPanel());
        skillsPane.setMinimumSize(new Dimension(hintWinMiniLength, 150));
        skillsPane.setPreferredSize(new Dimension(hintWinMiniLength, 300));

        verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                hintFrame, skillsPane);
        verticalSplitPane.setDividerLocation(300);
        verticalSplitPane.setMinimumSize(new Dimension(hintWinMiniLength, 400));
        verticalSplitPane
                .setPreferredSize(new Dimension(hintWinMiniLength, 600));
        verticalSplitPane.setResizeWeight(0.5);

        horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp,
                verticalSplitPane);
        horizontalSplitPane.setDividerLocation(500);
        horizontalSplitPane.setPreferredSize(new Dimension(800, 600));
        horizontalSplitPane.setResizeWeight(0.7);
        horizontalSplitPane.setMinimumSize(new Dimension(500, 500));

        container.add(horizontalSplitPane);

        Integer verticalDividerLocation = getController().getPreferencesModel()
                .getIntegerValue("Tutor Wrapper Vertical Divider Location");
        Integer horizontalDividerLocation = getController().getPreferencesModel()
                .getIntegerValue("Tutor Wrapper Horizontal Divider Location");

        if (verticalDividerLocation != null)
            verticalSplitPane.setDividerLocation(verticalDividerLocation
                    .intValue());
        if (horizontalDividerLocation != null)
            horizontalSplitPane.setDividerLocation(horizontalDividerLocation
                    .intValue());


    }



    public void componentResized(ComponentEvent e) {
        if (horizontalSplitPane != null && verticalSplitPane != null) {
            // trace.out ("mps", "vertical " +
            // verticalSplitPane.getDividerLocation());
            // trace.out ("mps", "horizontal " +
            // horizontalSplitPane.getDividerLocation());
            getController().getPreferencesModel().setIntegerValue(
                    "Tutor Wrapper Vertical Divider Location",
                    verticalSplitPane.getDividerLocation());
            getController().getPreferencesModel().setIntegerValue(
                    "Tutor Wrapper Horizontal Divider Location",
                    horizontalSplitPane.getDividerLocation());
            getController().getPreferencesModel().saveToDisk();
        }
    }

    public void componentMoved(ComponentEvent e) {

    }

    /**
     * 
     */
    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {

    }

    public HintWindowInterface getHintInterface() {
        return hintPanel;
    }
	
    public JComponent getTutorPanel () {
        return tutorPanel;
    }

    public boolean getUseSeparateHintWindow() {
    	if (trace.getDebugCode("inter")) trace.out("inter", "getUseSeparateHintWindow() rtns "+useSeparateHintWindow);
        return useSeparateHintWindow;
    }

    /**
     * Access to the BR_Controller used by this instance.
     * @return value of {@link #controller}
     */
    public TutorController getController() {
        return controller;
    }

    /**
     * Access to the BR_Controller used by this instance.
     * @param new value for {@link #controller}
     */
	public void setController(TutorController controller) {
		this.controller = controller;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (trace.getDebugCode("br")) trace.out("br", "WrapperSupport.actionPerformed(): "+e);
		if (RETRACT_LAST_STEP.equalsIgnoreCase(e.getActionCommand())) {
	        MessageObject mo = MessageObject.create(MsgType.RETRACT_STEPS, "SendNoteProperty");
	        mo.setProperty("NumberOfSteps", "1");
	        getController().getUniversalToolProxy().sendMessage(mo);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	Component getHorizontalSplitPane() {
		return horizontalSplitPane;
	}
	
	/**
	 * Attempt to get the hint dialog's parent window.
	 */
	public void ancestorAdded(AncestorEvent event) {
	   	if (trace.getDebugCode("inter")) trace.out("inter", "ancestorAdded() ancestor "+event.getAncestor()+
	   			", parent "+event.getAncestorParent());
    	Frame parentFrame = getOwnerFrame(container);
    	hintPanel = new HintWindow(parentFrame, getHintMessagesManager());
    	if (trace.getDebugCode("inter")) trace.out("inter", "new HintPanel parent "+parentFrame);
	}

	public void ancestorRemoved(AncestorEvent event) {
	   	if (trace.getDebugCode("inter")) trace.out("inter", "ancestorRemoved("+event+")");
	}

	public void ancestorMoved(AncestorEvent event) {
	   	if (trace.getDebugCode("inter")) trace.out("inter", "ancestorMoved("+event+")");
	}

	/**
	 * Access to the {{@link #controller}'s Logger instance.
	 * @return controller's {@link BR_Controller#getLogger()} or null
	 */
	public LogContext getLogger() {
		if (controller != null)
			return controller.getLogger();
		else
			return null;
	}

	/**
	 * Notify all {@link #doneListeners} that the done step was successful.
	 * Calls {@link #fireDoneActionPerformed(Object) fireDoneActionPerformed(this)}.
	 */
	public void doneActionPerformed() {
		fireDoneActionPerformed(this);
	}

	/**
	 * Notify all {@link #doneListeners} that the done step was successful.
	 * @param source event source
	 */
	private void fireDoneActionPerformed(Object source) {
		if (trace.getDebugCode("inter")) trace.out("inter", "WrapperSupport.fireDoneActionPerformed() listeners "+doneListeners);
		for (Iterator<ActionListener> it = doneListeners.iterator(); it.hasNext(); ) {
			ActionEvent ae = new ActionEvent(source, ActionEvent.ACTION_PERFORMED, StudentInterfaceWrapper.COMPLETE_ALL_ITEMS);
			it.next().actionPerformed(ae);
		}
	}

	/**
	 * Disable the hint button in suppress feedback mode.
	 * Also prevent changing colors on the Done button.
	 * @param suppressStudentFeedback if true, disable button
	 */
	public void suppressFeedback(boolean suppressStudentFeedback) {
		if (trace.getDebugCode("inter"))
			trace.out("inter", "WrapperSupport.suppressFeedback("+suppressStudentFeedback+
					") hintComponents "+hintComponents+", doneButtons "+doneButtons);
		for (Object obj : hintComponents) {
			if (obj instanceof HintWindowInterface)
				((HintWindowInterface) obj).setSuppressFeedback(suppressStudentFeedback);
			else if (obj instanceof JCommWidget)
				((JCommWidget) obj).setEnabled(!suppressStudentFeedback);			
			else if (obj instanceof Component)
				((Component) obj).setEnabled(!suppressStudentFeedback);
			else
				trace.err("Unexpected type in hintComponents: "+(obj == null ? "null" : obj.getClass().getName()));
		}
		HintWindowInterface hwi = getHintInterface();
		if (hwi != null)
			hwi.setSuppressFeedback(suppressStudentFeedback);
		
		for (JCommButton butn : doneButtons)  // CTAT2862: avoid color changes on the Done button
			butn.setChangeButtonColor(!suppressStudentFeedback);
	}
}
