/**
 * 
 */
package edu.cmu.pact.BehaviorRecorder.Controller.LMS;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.JOptionPane;

import pact.CommWidgets.StudentInterfaceWrapper;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.tutorshop.authentication.Auth;
import edu.cmu.pact.tutorshop.datalayer.DbConstants;
import edu.cmu.pact.tutorshop.util.UtilConstants;

/**
 * @author sewall
 *
 */
public class TutorShopLMS implements LMS_Provider, ActionListener {
    
    /** Reference to the central object of the CTAT runtime. */
    private CTAT_Controller controller;

    /**
     * Set the interface's controller.
     * @param controller value for {@link #controller}
     */
    public TutorShopLMS(CTAT_Controller controller) {
    	this.controller = controller;
        StudentInterfaceWrapper siw = getController().getStudentInterface();
        if (siw != null && siw.getWrapperSupport() != null)
        	siw.getWrapperSupport().addActionListener(this);
	}

	/**
     * Respond to a successful "Done" student action.
     * @param ae event describing the action
     */
	public void actionPerformed(ActionEvent ae) {
		if (trace.getDebugCode("done")) trace.out("done", "got done "+ae);
		try {
			advanceProblem();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(getController().getActiveWindow(),
					"Cannot advance to next problem:\n"+e.getMessage(),
				"Error Requesting Next Problem", JOptionPane.ERROR_MESSAGE);
		}
		getController().closeApplication(false);  // never returns
	}

	/**
	 * Ask the default browser to send the doneNext msg to the tutorshop host.
	 * If succeeds, exits.
	 * @throw Exception with nested exception
	 */
	public void advanceProblem() throws Exception {
		BasicService bs = null;
	    try {
	        bs = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
	    } catch (UnavailableServiceException e) {
	    	trace.err("Lookup failed: " + e);
	    	throw new Exception("Browser service unavailable: "+e, e);
	    }
	    URL url = null;
	    boolean result = false;
	    try {
			String curriculumServiceURL = System.getProperty("curriculum_service_url");
	    	final String pslc = "http://pslc-qa.andrew.cmu.edu/tutorshop/TutorProblem2";
	    	final String local = "javascript: hello()";
	    	StringBuffer sb =
	    		new StringBuffer(curriculumServiceURL != null ? curriculumServiceURL : pslc);
	    	appendParameter(sb, Auth.USER_ID);
	    	appendParameter(sb, Auth.AUTH_TOKEN);
	    	appendParameter(sb, Auth.SESSION_ID);
			appendParameter(sb, DbConstants.ADMIT_CODE);
	    	appendParameter(sb, DbConstants.STUDENT_PROBLEM_ID);
	    	sb.append("&").append(UtilConstants.CMD).append("=doneNext");
	    	if (trace.getDebugCode("done")) trace.out("done", "url: "+sb);
	    	url = new URL(sb.toString());
    		if (trace.getDebugCode("done"))
    			JOptionPane.showMessageDialog(getController().getActiveWindow(),
    					"url is "+url, "debug", JOptionPane.INFORMATION_MESSAGE);
	    	result = bs.showDocument(url);
	    	if (result) {                                   // success
	    		getController().closeApplication(false);
	    		return;                                // not reached
	    	}
	    	throw new Exception("Unknown error invoking browser service.");
	    } catch (MalformedURLException mue) {
	    	throw new Exception( "Bad server address: "+mue, mue);
	    } catch (Exception e) {
	    	throw new Exception("Error invoking browser service: "+e, e);
	    }
	}

	/**
	 * Append a HTTP GET parameter to the given URL if there is a system
	 * property of the given name. No-op if property doesn't exist.
	 * @param partialURL buffer to append to
	 * @param propertyName system property name and parameter name
	 * @return partialURL, now modified
	 */
	private StringBuffer appendParameter(StringBuffer partialURL,
			String propertyName) {
		String propertyValue = System.getProperty(propertyName);
		if (propertyValue == null)
			return partialURL;
		if (partialURL.indexOf("?") < 0)  // separator ? or &
			partialURL.append("?");       // depends on whether any
		else                              // parameters already appended
			partialURL.append("&");
    	final String enc = "UTF-8";
    	try {
    		partialURL.append(URLEncoder.encode(propertyName, enc));
    		partialURL.append("=");
    		partialURL.append(URLEncoder.encode(propertyValue, enc));
    	} catch (UnsupportedEncodingException uee) {
    		trace.err("fix the encoding: "+uee);
    	}
		return partialURL;
	}

	/**
	 * Return the controller.
	 * @return value of {@link #controller}
	 */
	private CTAT_Controller getController() {
		CTAT_Controller c = controller;
		return controller;
	}

}
