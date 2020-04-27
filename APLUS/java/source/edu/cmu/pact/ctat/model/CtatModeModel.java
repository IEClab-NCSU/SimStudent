/*
 * Created on Jun 23, 2006
 *
 */
package edu.cmu.pact.ctat.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelException;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.view.CtatFrame;

/**
 * Represent the different tutor types and authoring modes.
 */
public class CtatModeModel implements BR_Controller.WillNotifyListeners {

	/**	Author mode for initializing the student interface before start of problem. */
	public static final String DEFINING_START_STATE = "Set Start State";
	
	/**	Author mode for recording the steps in a solution. */
    public static final String DEMONSTRATING_SOLUTION = "Demonstrate";
    
	/**	Author mode for executing the tutor as a student would do. */
    public static final String TESTING_TUTOR = "Test Tutor";
    
    /** List of valid author modes {@link #DEFINING_START_STATE}, etc.  */
    public static final String[] authorModes = {
        DEFINING_START_STATE,
        DEMONSTRATING_SOLUTION,
        TESTING_TUTOR
    };
    
    //[Kevin Zhao](kzhao) - Added this to have the ability to go into a sort of mini-mode
    //						and to deal with the problem with the edgeID not being known
    /** SPECIAL MINI-AUTHOR MODE: Demonstrate This Link */
    private boolean demonstrateThisLinkMode = false;
    
    /**
     * The currentEdgeID will have a default value of -1, stating that indeed a current ID
     * has not been selected.
     */
    private int currentEdgeID = -1;
    ////////////////////////////////////////////////////////////////////////////////////////
    
    /** Tutor type for training or assessing Simulated Student rules. */
    public static final String SIMULATED_STUDENT_MODE = "Simulated Student";
    
    /** Tutor type for tutors using the Lisp-based TDK rule engine. */
    public static final String TDK_MODE = "Cognitive Tutor (TDK)";
    
    /** Tutor type for tutors using the Java-based Jess rule engine. */
    public static final String JESS_MODE = "Cognitive Tutor (Jess)";
    
    /** Tutor type for Example-tracing tutors. */
    public static final String EXAMPLE_TRACING_MODE = "Example-tracing Tutor";

//    public static final String DEMONSTRATE_MODE = "Demonstrate";
//    public static final String PRODUCTION_SYSTEM_MODE = "Tutor";
//    public static final String EXAMPLE_TRACING_MODE = "Pseudo-Tutor";

    /** List of valid tutor types {@link #EXAMPLE_TRACING_MODE}, etc. */
    private static final String[] modeTypes;
    static {
    	if (VersionInformation.includesJess() && VersionInformation.isRunningSimSt())
    		modeTypes = new String[] { EXAMPLE_TRACING_MODE, JESS_MODE, TDK_MODE, SIMULATED_STUDENT_MODE };
    	else if (VersionInformation.includesJess()) 
    		modeTypes = new String[] { EXAMPLE_TRACING_MODE, JESS_MODE, TDK_MODE };
    	else if (VersionInformation.isRunningSimSt())               // shouldn't happen: SimSt requires Jess
    		modeTypes = new String[] { EXAMPLE_TRACING_MODE, TDK_MODE, SIMULATED_STUDENT_MODE };
    	else
    		modeTypes = new String[] { EXAMPLE_TRACING_MODE, TDK_MODE };
    }
    
    private static final List<String> modeTypeList = Arrays.asList (modeTypes);
    private static final List<String> authorModeList = Arrays.asList (authorModes);

    /**
     * Customize {@link DefaultComboBoxModeModel#setSelectedItem(Object)} with prior checking
     * as to whether the mode change should take.
     */
    private class ComboBoxAuthorModeModel extends DefaultComboBoxModel {
    	
    	/** Avoid compiler warning. */
		private static final long serialVersionUID = 201402201505L;
		
		/**
		 * @param vector argument for {@link DefaultComboBoxModel#DefaultComboBoxModel(Object[])}
		 */
		public ComboBoxAuthorModeModel(Vector<String> vector) {
			super(vector);
		}
		
		/**
		 * Call {@link CtatModeModel#changeAuthorModeOk(String)} and veto the mode change if it
		 * reports an error. Else call {@link CtatModeModel#internalSetAuthorMode(String)} if ok. 
		 * @param newAuthorMode
		 * @throws PropertyVetoException
		 * @see javax.swing.DefaultComboBoxModel#setSelectedItem(java.lang.Object)
		 */
		public void setSelectedItem(Object newAuthorMode) {
			String problemName = requestProblemNameForAuthorModeChange();
			String errMsg = changeAuthorModeOk((String) newAuthorMode, problemName, true);
			if(trace.getDebugCode("mode"))
				trace.out("mode", "comboBoxAuthorModeModel.setSelectedItem("+newAuthorMode+
						") errMsg "+errMsg);
			if(errMsg == null) {
				internalSetAuthorMode((String) newAuthorMode);
				notifyListeners();
				return;
			}
			PropertyChangeEvent evt = new PropertyChangeEvent(CtatModeModel.this,
					"selectedItem", getCurrentAuthorMode(), newAuthorMode);
			new PropertyVetoException(errMsg, evt);
		}

		/**
		 * Retrieve the problem name from the start node or, if that's not defined, from
		 * {@link edu.cmu.pact.ctatview.CtatModePanel#queryForProblemName()}.
		 * @return problem name
		 */
		private String requestProblemNameForAuthorModeChange() {
			ProblemNode n0 = controller.getProblemModel().getStartNode();
			String result = null;
			if(n0 == null || (result = n0.getName()) == null) {
				if(isDefiningStartState())
					result = controller.getCtatFrameController().getDockedFrame().getCtatModePanel().queryForProblemName();
			}
			return result;
		}

		/**
		 * Access for enclosing class to super{@link #setSelectedItem(Object)}.
		 * @param newAuthorMode
		 */
		void nativeSetSelectedItem(String newAuthorMode) {
			super.setSelectedItem(newAuthorMode);
		}
    }

    private ComboBoxModel comboBoxModeModel;
    private ComboBoxAuthorModeModel comboBoxAuthorModeModel;

	/** Current value of {@link #comboBoxModeModel}. */
	private String currentMode = modeTypes[0];

	/** Current value of {@link #comboBoxAuthorModeModel}. */
	private String currentAuthorMode = authorModes[0];

	private edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller controller;
	private String previousMode = "";
	private String previousAuthorMode = "";
	
	/** Booleans to lock certain states */
	private boolean setStartStateLock = false;
	private boolean setDemonstrateLock = false;
	private boolean setTestTutorLock = false;

	/** Interlock to prevent events generated by gui during API calls to this class. */
	private boolean noticesBlocked;
    
    public CtatModeModel(BR_Controller controller) {
    	if (!Utils.isRuntime()) {
    		comboBoxModeModel = new DefaultComboBoxModel(new Vector (modeTypeList));
    		comboBoxAuthorModeModel = new ComboBoxAuthorModeModel(new Vector<String>(authorModeList));
    	}
        this.controller = controller;
    }

	public ComboBoxModel getModeComboBoxModel() {
        return comboBoxModeModel;
    }
    
    public ComboBoxModel getAuthorModeComboBoxModel() {
        return comboBoxAuthorModeModel;
    }
    
    /**
     * @return {@link #modeTypeList}
     */
    public static List<String> getModeTypeList() {
        return modeTypeList;
    }
    
    /**
     * @return {@link #authorModeList}
     */
    public static List<String> getAuthorModeList() {
        return authorModeList;
    }
    
    /**
     * Set the current author mode and possibly generate an event.
     * Calls {@link #changeAuthorModeOk(String, String)}.
     * @param newMode
     */
    public void setAuthorMode(String newMode) {
    	if (!authorModeList.contains(newMode)) 
    		throw new RuntimeException ("Unknown CTAT author mode: " + newMode);   	
    	 
    	noticesBlocked = true;
        String errMsg = changeAuthorModeOk(newMode, controller.getProblemName(),
        		false);  // FIXME: get name from start node instead? 
        noticesBlocked = false;
      
        if(errMsg == null) {
        	internalSetAuthorMode(newMode);
        	notifyListeners();
        }
    }
    
    /**
     * Represent the over-complicated results of {@link #findProblemName(String)}.
     */
    private class FindProblemName {
    	/** Data we need. */                                      String problemName = null;
    	/** Call {@link BR_Controller#modifyStartState(String).*/ boolean shouldModifyStartState = false;
    	/** Call {@link BR_Controller#createStartState(String).*/ boolean shouldCreateStartState = false;
    	/** User canceled the operation or error occurred.*/      boolean abortOperation = false;
    	public String toString() {
    		StringBuilder sb = new StringBuilder("[");
    		sb.append(problemName).append(", ");
    		sb.append(shouldModifyStartState).append(", ");
    		sb.append(shouldCreateStartState).append(", ");
    		sb.append(abortOperation).append("]");
    		return sb.toString();
    	}
    }

    /**
     * @param newMode new author mode requested
     * @param problemName to return problemName in first element;
     *        if returned value is null, don't do anything with the start state
     * @return true if need to modify the start state;
     *         false if need to create the start state or do nothing
     */
    private FindProblemName findProblemName(String newMode, String problemName) {
    	FindProblemName result = new FindProblemName();
    	if(trace.getDebugCode("mode"))
    		trace.printStack("mode", "CtatModeModel.FPN("+newMode+","+problemName+") prevMode "+previousAuthorMode+
    				", ctlr.isStartStateModified() "+controller.isStartStateModified()+
    				", startNode "+controller.getProblemModel().getStartNode());
//    	CTAT1900 no longer check || !controller.isStartStateModified()) BREAKS START STATE
    	if (!("Set Start State".equalsIgnoreCase(previousAuthorMode)))
    		return result;
    	if(problemName == null) {                  // user didn't enter a problem name                  
    		result.abortOperation = true;
    		return result;
    	}
		ProblemNode startNode = controller.getProblemModel().getStartNode();
		if (startNode != null)
			result.problemName = startNode.getName();  // FIXME use problemName arg if we have it?
		if (result.problemName != null)
			result.shouldModifyStartState = true;  // problem has a start state already
		else {
			if(trace.getDebugCode("mode"))
				trace.out("mode", "*! CMModel.findProblemName() would have queried for problem name");
			result.problemName = problemName;
			if (result.problemName != null)        // got a name for the new start state
				result.shouldCreateStartState = true;
			else                                   // user canceled the name-start-state dialog
				result.abortOperation = true;
		}
		return result;
    }
    
    protected String changeAuthorModeOk(String newMode, String problemName, boolean fromAuthorModeSwitch) {
		if(trace.getDebugCode("mode"))
			trace.out("mode", "CtatModeModel.changeAuthorMode("+newMode+") current mode "+getCurrentAuthorMode());

		//[Kevin Zhao](kzhao) - Implemented this to deal with 'Demonstrate this Link' FIXME == ineffective test
		if (newMode == DEFINING_START_STATE && setStartStateLock) {
    		JOptionPane.showMessageDialog(controller.getActiveWindow(),
    				"You have tried switching to 'Set Start State' mode.\n" + 
    				"An active feature may not work in 'Set Start State' mode.\n" +
    				"You have been switched back to the previous mode",
    				"Warning",
    				JOptionPane.WARNING_MESSAGE);
    		return "Tried "+DEFINING_START_STATE+" while setStartStateLock "+setStartStateLock;
		}
		//[Kevin Zhao](kzhao) - Implemented locks to deal with futher possibilities
		if (newMode == DEMONSTRATING_SOLUTION && setDemonstrateLock) {
    		JOptionPane.showMessageDialog(controller.getActiveWindow(),
    				"You have tried switching to 'Demonstrate' mode.\n" + 
    				"An active feature may not work in 'Demonstrate' mode.\n" +
    				"You have been switched back to the previous mode",
    				"Warning",
    				JOptionPane.WARNING_MESSAGE);
    		return "Tried "+DEMONSTRATING_SOLUTION+" while setDemonstrateLock "+setDemonstrateLock;
		}
		if (newMode == TESTING_TUTOR && setTestTutorLock) {
    		JOptionPane.showMessageDialog(controller.getActiveWindow(),
    				"You have tried switching to 'Test Tutor' mode.\n" + 
    				"An active feature may not work in 'Test Tutor' mode.\n" +
    				"You have been switched back to the previous mode",
    				"Warning",
    				JOptionPane.WARNING_MESSAGE);
    		return "Tried "+TESTING_TUTOR+" while setTestTutorLock "+setTestTutorLock;
		}
        if ("Set Start State".equalsIgnoreCase(newMode)) {
        	if(fromAuthorModeSwitch && !newMode.equals(previousAuthorMode))
        		controller.getUniversalToolProxy().setUserBeganStartStateEdit(true);
        	
            if (controller.getDockedFrame() != null)
            	((CtatFrame)controller.getDockedFrame()).getCtatMenuBar().enableCreateStartStateMenus(true);
        	//	controller.getCtatFrameController().getDockedFrame().getCtatMenuBar().enableCreateStartStateMenus(true);
        } else {
        	FindProblemName fpn = findProblemName(newMode, problemName);
        	if (trace.getDebugCode("mode"))
        		trace.out("mode", "CMM.changeAuthorModeOk("+newMode+") fpn "+fpn);
        	if (fpn.abortOperation)
        		return "User cancelled.";
        	
//        	controller.getUniversalToolProxy().rebootStartState(true);
     
            if (controller.getDockedFrame() != null)
            	((CtatFrame)controller.getDockedFrame()).getCtatMenuBar().enableCreateStartStateMenus(false);
        	//	controller.getCtatFrameController().getDockedFrame().getCtatMenuBar().enableCreateStartStateMenus(false);

        	if (fpn.shouldModifyStartState) {
        		if(trace.getDebugCode("mode"))
        			trace.out("mode", "*! CMModel.changeAuthorModeOk() fpn "+fpn+" to call BR_C.modifyStartState()");
        	  	
    			controller.modifyStartState(fpn.problemName);
    		  
    			if(!Utils.isRuntime()) {  // sewall Trac #334: redundant with later goToStartState() in openBRD...
    				if(trace.getDebugCode("mode"))
    					trace.out("mode", "*! CMModel.changeAuthorModeOk() fpn "+fpn+" to call BR_C.goToStartState()");
    			
    				controller.goToStartState();
    			
    			}
        	} else if (fpn.shouldCreateStartState) {
    			try {
    			  	 
    				Object createResult = controller.createStartState(fpn.problemName,
    						fromAuthorModeSwitch && isRuleEngineTracing(), this);
    			  	  
    				if(trace.getDebugCode("mode"))
    					trace.out("mode", "CMM.changeAuthorModeOk(): createStartState returns "+createResult);
    				if(createResult instanceof String)
    					return (String) createResult;  // will finish on other thread
    			} catch (ProblemModelException e) {
    				e.printStackTrace();
    				if(!Utils.isRuntime())
    					((CtatFrame)controller.getDockedFrame()).getCtatMenuBar().enableCreateStartStateMenus(true);
					return "Error from createStartState(): "+e;
    			}        		
        	}
        }
		if(trace.getDebugCode("mode"))
			trace.out("mode", "*! CMModel.changeAuthorModeOk("+newMode+") prev "+
					previousAuthorMode+" rtns null==OK");
    	return null;
	}
	
    /**
     * Change the current mode. Calls {@link #userSetMode(String)}. Generates an event. 
     * @param newMode
     */
    public void setMode (String newMode) {
    	// Fri Sep 15 00:09:11 LDT 2006 :: Noboru
        // When mode is set programably, and the attempt was made to set to the same mode, 
        // the Docking windows likely to got a NullPointerException
        // Thu Sep 21 13:02:30 LDT 2006 :: Noboru
        // Well, this apprently broke something else... Currently, the problem happens only 
        // when using SimSt, so I've decided to move this test into SimSt.java
        // if (getCurrentMode().equals(newMode)) return;
    	
    	/*
    	 * When the author has changes the UI widget, the comboBoxModeModel
    	 * has already been changed.  Hence can't set previousMode to the
    	 * current contents of comboBoxModeModel at the start of this.
    	 */
    	if (trace.getDebugCode("br")) trace.printStack("br", "setMode("+newMode+") previousMode "+previousMode);
    	
    	if (! modeTypeList.contains(newMode)) 
    	    throw new RuntimeException ("Unknown CTAT mode: " + newMode);
    	noticesBlocked = true;
    	userSetMode(newMode);
    	noticesBlocked = false;
    	notifyListeners();
    }
    
    /**
	 * Access for gui to set author mode.
	 */
    public String userSetMode(String newMode) {
		trace.out("mg", "CtatModeModel (userSetMode): working with controller "
				+ this.controller.getTabNumber() + ", old = " + getCurrentMode()
				+ ", new = " + newMode);
    	String result = previousMode;
    	/*
         * 10/22/06 sewall: always shift to demonstrate mode for SimStudent.
         * Don't do anything special when changing out of SimStudent.
         */ 
        if (SIMULATED_STUDENT_MODE.equals(newMode) && !newMode.equals(previousMode)) {
           // internalSetAuthorMode(DEMONSTRATING_SOLUTION);
        	// When switched to SimStudent Tutor Type the mode should always
        	// be set to "Set Start State"
        	internalSetAuthorMode(DEFINING_START_STATE);
        }

        internalSetMode(newMode);
    	notifyListeners();
    	return result;
    }

	/**
     * Set {@link #currentMode} and, if authoring tools are active, call
     * {@link #comboBoxModeModel}.{@link ComboBoxModel#setSelectedItem(Object)}.
     * @param newMode
     */
    private void internalSetMode(String newMode) {
        currentMode = newMode;
        if (!Utils.isRuntime())
        	comboBoxModeModel.setSelectedItem(newMode);
	}

	/**
     * Set {@link #currentAuthorMode} and, if authoring tools are active, call
     * {@link #comboBoxAuthorModeModel}.{@link ComboBoxModel#setSelectedItem(Object)}.
     * @param newAuthorMode
     */
	private void internalSetAuthorMode(String newAuthorMode) {
        currentAuthorMode = newAuthorMode;
        if (!Utils.isRuntime())
        	comboBoxAuthorModeModel.nativeSetSelectedItem(newAuthorMode);
	}

	public boolean isRuleEngineTracing() {
        return (JESS_MODE.equals(getCurrentMode()) || TDK_MODE.equals(getCurrentMode()) || SIMULATED_STUDENT_MODE.equals(getCurrentMode()));
    }

    public boolean isJessTracing() {
        return (JESS_MODE.equals(getCurrentMode()) 
        		|| SIMULATED_STUDENT_MODE.equals(getCurrentMode())
        		);
    }

    public boolean isJessMode() {
        return (JESS_MODE.equals(getCurrentMode()) 
//        		|| TYPE_SIMULATED_STUDENT.equals(getCurrentMode())
        		);
    }

    public boolean isExampleTracingMode() {
        return (EXAMPLE_TRACING_MODE.equals(getCurrentMode()));
    }

    public boolean isTDKMode() {
        return (TDK_MODE.equals(getCurrentMode()));
    }

    public boolean isSimStudentMode() {
    	return (SIMULATED_STUDENT_MODE.equals(getCurrentMode()));
    }

    public String getModeTitle() {
        return getCurrentMode();
    }


    /**
     * @return the currentMode
     */
    public String getCurrentMode() {
    	if (Utils.isRuntime())
    		return currentMode;
        return (String) comboBoxModeModel.getSelectedItem();
    }

    /**
     * @return the current author mode
     */
	public String getCurrentAuthorMode() {
    	if (Utils.isRuntime())
    		return currentAuthorMode;
		return (String) comboBoxAuthorModeModel.getSelectedItem(); 
	}

	/**
	 * Convenience for telling whether defining the start state.
	 * @return true if {@link #getCurrentAuthorMode()} is {@link #DEFINING_START_STATE}
	 */
	public boolean isDefiningStartState() {
		return DEFINING_START_STATE.equals(getCurrentAuthorMode());
	}

	/**
	 * Convenience for telling whether demonstrating a solution.
	 * @return true if {@link #getCurrentAuthorMode()} is {@link #DEMONSTRATING_SOLUTION}
	 */
	public boolean isDemonstratingSolution() {
		return DEMONSTRATING_SOLUTION.equals(getCurrentAuthorMode());
	}
	
	//[Kevin Zhao](kzhao) - These are all booleans to lock the states, but the actual mechanism
	//						is in the userSetAuthorMode() method
	/**
	 * This will lock the 'Set Start State' mode
	 */
	public void lockSetStartState() {
		setStartStateLock = true;
	}
	
	/**
	 * This will unlock the 'Set Start State' mode
	 */
	public void unlockSetStartState() {
		setStartStateLock = false;
	}
	
	/**
	 * This will return whether the 'Set Start State' mode is locked
	 */
    public boolean isSetStartStateLocked(){
    	return setStartStateLock;
    }
    
	/**
	 * This will lock the 'Demonstrate' mode
	 */
	public void lockDemonstrate() {
		setDemonstrateLock = true;
	}
	
	/**
	 * This will unlock the 'Demonstrate' mode
	 */
	public void unlockDemonstrate() {
		setDemonstrateLock = false;
	}
	
	/**
	 * This will return whether the 'Demonstrate' mode is locked
	 */
    public boolean isDemonstrateLocked(){
    	return setDemonstrateLock;
    }
    
	/**
	 * This will lock the 'TestTutor' mode
	 */
	public void lockTestTutor() {
		setTestTutorLock = true;
	}
	
	/**
	 * This will unlock the 'Set Start State' mode
	 */
	public void unlockTestTutor() {
		setTestTutorLock = false;
	}
	
	/**
	 * This will return whether the 'Set Start State' mode is locked
	 */
    public boolean isTestTutorLocked(){
    	return setTestTutorLock;
    }
    /////////////////////////////////////////////////////////////////////////
    
    //[Kevin Zhao](kzhao) - Creating a minimode for demonstrating a link
    /**
     * This will set the demonstrateThisLinkMode flag to be true, as well as lock
     * the start state.
     */
    public void enterDemonstrateThisLinkMode(int edgeID) {
    	demonstrateThisLinkMode = true;
    	currentEdgeID = edgeID;
    	lockSetStartState();
    	return;
    }
    /**
     * This will set the demonstrateThisLinkMode flag to be false, as well as unlock
     * the start state.
     */
    public void exitDemonstrateThisLinkMode() {
    	demonstrateThisLinkMode = false;
    	currentEdgeID = -1;
    	unlockSetStartState();
    	return;
    }
    
    /**
     * This will return the demonstrateThisLinkMode flag
     * @return
     */
    public boolean isDemonstrateThisLinkMode() {
    	return demonstrateThisLinkMode;
    }
    
    /**
     * @return the current edge ID
     */
    public int getCurrentEdgeID() {
    	return currentEdgeID;
    }
    /////////////////////////////////////////////////////////////////////////

        
    /**
	 * Sends notifications to the rest of the application of a tutor type or
	 * authoring mode change. No-op if the lock {@link #noticesBlocked} is set. 
	 */
	public void notifyListeners() {
		if(trace.getDebugCode("mode"))
			trace.out("mode", "*! CMModel.notifyListeners() noticesBlocked "+noticesBlocked);
	    if (noticesBlocked)            // FIXME thread safety?
	        return;

	    controller.fireCtatModeEvent(new CtatModeEvent.SetModeEvent(getCurrentMode(),
	            previousMode, getCurrentAuthorMode(), previousAuthorMode)); 

	    /** Postpone setting this until here. Don't return before setting. */
	    previousAuthorMode = getCurrentAuthorMode();
	    previousMode = getCurrentMode();
	}

	public String getPreviousAuthorMode() {
		return previousAuthorMode;
	}

	public void setPreviousAuthorMode(String previousAuthorMode) {
		this.previousAuthorMode = previousAuthorMode;
	}

	/**
	 * @param tutorType from .brd file or elsewhere
	 * @return true if argument is in {@link #modeTypeList}
	 */
	public static boolean isDefinedModeType(String tutorType) {
		return modeTypeList.contains(tutorType);
	}
}
