/*
 * Created on Sep 7, 2005
 *
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.awt.Dimension;

import javax.swing.JPanel;

import edu.cmu.pact.Utilities.trace;

public class CTAT_Options extends JPanel {

    private boolean separateHintWindow;
    private boolean showBehaviorRecorder = true;
    
    private boolean useExampleTracingTutor = true;
    private boolean useJESSCognitiveTutor;
    private boolean useTDKCognitiveTutor;
    private boolean useSimulatedStudent;
    
    private boolean showLoginWindow;
	private boolean connectToLispAtStartup;
//	private boolean enableJess = true;
	private boolean showAdvanceProblemMenu;
    private boolean enableLMSLogin;
    private int interfaceWidth;
    private int interfaceHeight;
 //   private JComboBox TutorType;
    public CTAT_Options () {
//        trace.out ("CREATE CTAT OPTIONS");
    	setVisible(false);
    	Dimension dim = new Dimension(1,1);
    	setMinimumSize(dim);
    	setPreferredSize(dim);
    	setMaximumSize(dim);
    }
    
	/**
     * @return Returns the enableLMSLogin.
     */
    public boolean getEnableLMSLogin() {
        return enableLMSLogin;
    }

    
    
    /**
     * @param enableLMSLogin The enableLMSLogin to set.
     */
    public void setEnableLMSLogin(boolean enableLMSLogin) {
        this.enableLMSLogin = enableLMSLogin;
    }

    public void setShowAdvanceProblemMenu (boolean showAdvanceProblemMenu) {
    	if (trace.getDebugCode("options")) trace.out("options", "set show advance problem menu: " + showAdvanceProblemMenu);
        this.showAdvanceProblemMenu = showAdvanceProblemMenu;
    }
    
    public boolean getShowAdvanceProblemMenu() {
    	if (trace.getDebugCode("options")) trace.out("options", "get show advance problem menu: " + showAdvanceProblemMenu);
    	trace.printStack("options");
        return showAdvanceProblemMenu;
    }
	
    public void setSeparateHintWindow (boolean separateHintWindow) {
        this.separateHintWindow = separateHintWindow;
    }
    
    public boolean getSeparateHintWindow() {
        return separateHintWindow;
    }

    /**
     * @param show The studentMode to set.
     */
    public void setShowBehaviorRecorder(boolean show) {
        trace.out ("inter", "show behavior recorder = " + show);
        this.showBehaviorRecorder = show;
    }

    /**
     * @return Returns the studentMode.
     */
    public boolean getShowBehaviorRecorder() {
        return showBehaviorRecorder;
    }

 

    public boolean getShowLoginWindow() {
        return showLoginWindow;
    }
    
    public void setShowLoginWindow(boolean showLoginWindow) {
        this.showLoginWindow = showLoginWindow;
    }

	public boolean getConnectToLispAtStartup() {
		return this.connectToLispAtStartup;
	}
	
	public void setConnectToLispAtStartup(boolean connect) {
		this.connectToLispAtStartup = connect;
	}

	/**
	 * @return Returns the useJess.
	 */
/*	public boolean getEnableJess() {
		return enableJess;
	}*/
	/**
	 * @param enableJess The useJess to set.
	 */
	/*public void setEnableJess(boolean enableJess) {
		this.enableJess = enableJess;
	}*/



    /**
     * Type is long to accommodate code disassembler that gets height and width.
     * @param interfaceHeight The interfaceHeight to set.
     */
    public void setInterfaceHeight(long interfaceHeight) {
        this.interfaceHeight = (int) interfaceHeight;
    }



    /**
     * @return Returns the interfaceHeight.
     */
    public int getInterfaceHeight() {
        return interfaceHeight;
    }



    /**
     * Type is long to accommodate code disassembler that gets height and width.
     * @param interfaceWidth The interfaceWidth to set.
     */
    public void setInterfaceWidth(long interfaceWidth) {
        this.interfaceWidth = (int) interfaceWidth;
    }



    /**
     * @return Returns the interfaceWidth.
     */
    public int getInterfaceWidth() {
        return interfaceWidth;
    }

 /*   public JComboBox getTutorType() {
		return TutorType;
	}

    public void setTutorType(JComboBox tutorType) {
		TutorType = tutorType;
	}*/

    /**
     * @param useExampleTracingTutor The useExampleTracingTutor to set.
     */
    public void setUseExampleTracingTutor(boolean useExampleTracingTutor) {
        this.useExampleTracingTutor = useExampleTracingTutor;
    }

    /**
     * @return Returns the useExampleTracingTutor.
     */
    public boolean isUseExampleTracingTutor() {
        return useExampleTracingTutor;
    }

 

    /**
     * @param useJESSCognitiveTutor The useJESSCognitiveTutor to set.
     */
    public void setUseJESSCognitiveTutor(boolean useJESSCognitiveTutor) {
        this.useJESSCognitiveTutor = useJESSCognitiveTutor;
    }

    /**
     * @return Returns the useJESSCognitiveTutor.
     */
    public boolean isUseJESSCognitiveTutor() {
        return useJESSCognitiveTutor;
    }
    
    /**
     * @param useJESSCognitiveTutor The useJESSCognitiveTutor to set.
     */
    public void setUseTDKCognitiveTutor(boolean useTDKCognitiveTutor) {
        this.useTDKCognitiveTutor = useTDKCognitiveTutor;
    }

    /**
     * @return Returns the useJESSCognitiveTutor.
     */
    public boolean isUseTDKCognitiveTutor() {
        return useTDKCognitiveTutor;
    }
    
	/**
	 * @return the useSimulatedStudent
	 */
	public boolean isUseSimulatedStudent() {
		return useSimulatedStudent;
	}

	/**
	 * @param useSimulatedStudent the useSimulatedStudent to set
	 */
	public void setUseSimulatedStudent(boolean useSimulatedStudent) {
		this.useSimulatedStudent = useSimulatedStudent;
	}
}
