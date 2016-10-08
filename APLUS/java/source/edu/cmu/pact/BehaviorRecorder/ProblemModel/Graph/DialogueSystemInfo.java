package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

public class DialogueSystemInfo {
	private static final String PARAM_DELIMITER = "&";
	private String student_Hint_Request;
    private String step_Successful_Completion;
    private String step_Student_Error;
	
	public static final int STEP_SUCCESSFUL = 0;
	public static final int STEP_HINT = 1;
	public static final int STEP_ERROR = 2;
	
	public static final String INVOKE_BROWSER_ON_EXTERNAL_URL = "Invoke Browser on External URL";
	public static final String EXTERNAL_URL_FOR_EDGE_TRAVERSAL = "External URL for Edge Traversal";
	
	protected BR_Controller controller;
    
    /**
     * Enable a calling routine to set transient fields after deserializing.
     * @param controller value for {@link #controller}
     */
    public void restoreTransients(BR_Controller controller) {
    	this.controller = controller;
    }
	
	public DialogueSystemInfo(BR_Controller controller) {
		this.controller = controller;
		
		student_Hint_Request = "";
		step_Successful_Completion = "";
		step_Student_Error = "";
	}
    
    /**
     * Tell whether an external dialogue system is in use. A true result
     * does not mean that the external system is in fact connected or
     * working; it simply means that we will try to send a message to the
     * system when we have occasion to do so.
     * @return preference {@link DialogueSystemInfo#INVOKE_BROWSER_ON_EXTERNAL_URL}
     */
    public static boolean getUseDialogSystem(BR_Controller controller) {
    	PreferencesModel pm = controller.getPreferencesModel();
    	Boolean extSys = pm.getBooleanValue(DialogueSystemInfo.INVOKE_BROWSER_ON_EXTERNAL_URL);
    	return (extSys != null && extSys.booleanValue());
    }
	
	/**
	 * Process the dialogue request.
	 * 
	 * @param type: request type. {@link #STEP_SUCCESSFUL} 
     * or {@link #STEP_HINT} or{@link #STEP_ERROR}.
     * 
     * @return true If the request is successfully processed
     * 			false otherwise.
	 */
	
	public boolean processInfo(int type) {

		boolean findInfo = false;
		
		if (!hasInfo(type))
			return false;
		
		PreferencesModel pm = controller.getPreferencesModel();
		Boolean invBr = pm.getBooleanValue(INVOKE_BROWSER_ON_EXTERNAL_URL);
	    if (invBr != null && invBr.booleanValue()) {
	        StringBuffer url = new StringBuffer();
	        String prefix = pm.getStringValue(EXTERNAL_URL_FOR_EDGE_TRAVERSAL);

			// main part 
			if (prefix != null)
	            url.append(prefix);
	        else
				if (trace.getDebugCode("msg")) trace.out("msg", "prefix part is null");

			// student name part
	        if (url.indexOf("?") < 0)
	        	url.append("?");         // "?" separates path from args in URL
	        else
	        	url.append(PARAM_DELIMITER);   // already have a "?"
	        String studentName = null;
	        Logger logger = controller.getLogger();
	        if (logger != null)
				studentName = logger.getStudentName();
	        if (studentName == null || studentName.length() == 0) {
	        	// zz any other action?
				trace.err(getClass().getName()+".processInfo("+type+
						"): student login name is null or empty");
				studentName = "";
	        }
	        url.append("name=").append(studentName);

	        // dialogue part
	        if (url.indexOf("?") < 0)
	        	url.append("?");         // "?" separates path from args in URL
	        else
	        	url.append(PARAM_DELIMITER);   // already have a "?"
			if (type == STEP_SUCCESSFUL)
				url.append(this.getStep_Successful_Completion());
			else if (type == STEP_HINT)
				url.append(this.getStudent_Hint_Request());
			else
				url.append(this.getStep_Student_Error());
			
			Utils.invokeBrowser(url.toString());
			
			findInfo = true;
	    }
		
		return findInfo;
	}
	
	/**
	 * Tell whether we have a message for a specific type, that is
	 * {@link #STEP_HINT}, {@link #STEP_ERROR} or {@link #STEP_SUCCESSFUL}.
	 * @param type desired
	 * @return whether there's a message of the given type
	 */
	public boolean hasInfo(int type) {
		return (type == STEP_SUCCESSFUL && isStep_Successful_Completion())
			|| (type == STEP_HINT && isStudent_Hint_Request())
			|| (type == STEP_ERROR && isStep_Student_Error());	
	}
	
	/**
	 * Tell whether we have any message.
	 * @return {@link #isStep_Student_Error()} OR
	 *         {@link #isStudent_Hint_Request()} OR
	 *         {@link #isStep_Successful_Completion()
	 * @param type
	 * @return
	 */
	public boolean hasInfo() {
		return isStep_Student_Error() || isStudent_Hint_Request() ||
				isStep_Successful_Completion();
	}
	
	private String getInfo(int type) {
		if (type == STEP_SUCCESSFUL) 
			return getStep_Successful_Completion();
		else if (type == STEP_HINT)
			return getStudent_Hint_Request();
		else if (type == STEP_ERROR)
			return getStep_Student_Error();		
		else 
			return null;
	}
	
	public void setStudent_Hint_Request(String hint) {
		student_Hint_Request = hint;
		return;
	}
	
	public String getStudent_Hint_Request() {
		return student_Hint_Request;
	}
	
	public boolean isStudent_Hint_Request() {
		return !(student_Hint_Request.equals(""));
	}
	
	public void setStep_Successful_Completion (String successful) {
		step_Successful_Completion = successful;
		return;
	}
	
	public String getStep_Successful_Completion () {
		return step_Successful_Completion;
	}
	
	public boolean isStep_Successful_Completion() {
		return !(step_Successful_Completion.equals(""));
	}
	
	public void setStep_Student_Error (String error) {
		step_Student_Error = error;
		return;
	}
	
	public String getStep_Student_Error () {
		return step_Student_Error;
	}
	
	public boolean isStep_Student_Error() {
		return !(step_Student_Error.equals(""));
	}

}
