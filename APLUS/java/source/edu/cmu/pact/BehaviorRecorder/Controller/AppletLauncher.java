/**
 * Copyright 2012 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.applet.AppletContext;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import netscape.javascript.JSObject;

import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindowInterface;
import edu.cmu.pact.TutoringService.TSLauncherServer;
import edu.cmu.pact.TutoringService.TransactionInfo.Single;
import edu.cmu.pact.Utilities.NtpClient;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.miss.SimStWrapperSupport;
import edu.cmu.pslc.logging.LogContext;

import pact.CommWidgets.StudentInterfaceWrapper;
import pact.CommWidgets.WrapperSupport;

/**
 * An applet for launching a Java student student interface.
 * @author sewall
 */
public class AppletLauncher extends JApplet implements StudentInterfaceWrapper {

	/** The parameter telling whether to turn on default debug tracing from {@link trace}. */
	private static final String SHOW_DEBUG_TRACES = "show_debug_traces";

	/** The parameter telling whether to log. */
	private static final String LOGGING_PARAM = "Logging";

	/** For {@link java.io.Serializable}. */
	private static final long serialVersionUID = 1L;
	
	/** The parameter holding the info for {@link #setCaption(String)}. */
	private static final String INFO_PARAM = "info";

	/** Name of the parameter carrying the URL to which the tutor should report end-of-problem. */
	public static final String CURRICULUM_SERVICE_URL_PARAM = "curriculum_service_url";

	/** Name of the parameter giving the address for the next problem. */
	static final String RUN_PROBLEM_URL_PARAM = "run_problem_url";

	/** Name of a parameter whose value must be returned to the LMS. */
	static final String AUTHENTICITY_TOKEN_PARAM = "authenticity_token";

	/** Call {@link URLDecoder#decode(String, String)} on this parameter. */
	static final String DECODE = "must URLDecode";

	/** Send this parameter as URL encoded. */
	static final String ENCODE = "must URLEncode";

	/** What frame the next problem should appear in. */
	static final String TARGET_FRAME = "target_frame";
	
	/** Student interface instance. */
	private JPanel studentInterface = null;
	
	/** List of command-line arguments for {@link SingleSessionLauncher#SingleSessionLauncher(String[], boolean)} . */
	private List<String> cmdLineList = null;

	/** Delegate for runtime infrastructure. */
	private WrapperSupport wrapperSupport;

	/** Reference to the JavaScript environment. */
	private JSObject window;

	/** Top-level object in the runtime. */
	private SingleSessionLauncher ctatLauncher;
	
	/** Launcher object for Preferences. */
	private TSLauncherServer ls = null;
	
	/**
	 * Currently a no-op.
	 * @see java.applet.Applet#init()
	 */
	public void init() {
		trace.addDebugCode("applet");
		if(trace.getDebugCode("applet"))
			trace.outNT("applet", "AppletLauncher.init()");

		ls = new TSLauncherServer() {
			public boolean removeSession(String guid) { return false; }

			public void updateTimeStamp(String guid) {}

			public Single createTransactionInfo(String sessionId) { return null; }

			public void updateTransactionInfo(String sessionId, Object info) {}

			public NtpClient getNtpClient() { return null; }
		};
	}

	/**
	 * Create {@link #cmdLineList} from the parameters of interest listed by {@link #getParameterInfo()}.
	 */
	private void makeCmdLine() {
		cmdLineList = new ArrayList<String>(getParameterInfo().length);
		for(String[] pInfo : getParameterInfo()) {
			String pName = 	pInfo[0];
			String pValue = getParameter(pName);
			if(pValue != null) {
				if(trace.getDebugCode("applet"))
					trace.outNT("applet", "AppletLauncher.makeCmdLine()["+cmdLineList.size()+
							"] "+pName+" = "+pValue);
				cmdLineList.add(paramToCmdLineArg(pName, pValue));
			}
		}
	}

	/**
	 * Set {@link #window} from {@link JSObject#getWindow(java.applet.Applet)}.
	 * @return {@link #window}
	 */
	private JSObject getWindow() {
		if(window == null)
			window = JSObject.getWindow(this);
		return window;
	}

	/**
	 * Write to the caption field above the tutor by calling a JavaScript function.
	 * @param info argument to JS function, from the {@value #INFO_PARAM} parameter;
	 *        no-op if null or empty
	 */
	private void setCaption(String info) {
		String jsfn = "javaScriptInfo";
		if(info == null || info.length() < 1)
			return;
		try {
			if(trace.getDebugCode("applet"))
				trace.outNT("applet", "AppletLauncher.setCaption("+info+")");
			Object[] args = {info};
			getWindow().call(jsfn, args);
		} catch(Exception e) {
			trace.err("Error calling JavaScript \""+jsfn+"\": "+
					e+"; cause: "+e.getCause()+";\n  to display \""+info+"\"");  
		}
	}

	/**
	 * Get the HTTP cookie string from Javascript. The HTML page must define a JS
	 * function with this method's name and taking no arguments.
	 * @return result of JS function "getDocumentCookies"
	 */
	String getDocumentCookies()
	{
		Object result = getWindow().call("getDocumentCookies", new Object[0]);
		if(trace.getDebugCode("applet"))
			trace.outNT("applet", "AppletLauncher.getDocumentCookies() result:\n  "+result);
		return (String) result;
	}
	
	/**
	 * Create a JPanel instance from the {@value Logger#STUDENT_INTERFACE_PROPERTY} parameter.
	 * @param studentInterfaceClassName
	 */
	private void createStudentInterface(String studentInterfaceClassName) {
		if(studentInterfaceClassName == null || studentInterfaceClassName.length() < 1)
			throw new IllegalArgumentException("Abort: parameter "+Logger.STUDENT_INTERFACE_PROPERTY+
					", which must give the student interface class name, is unset");
	
		JComponent si = TutorController.createStudentInterface(studentInterfaceClassName);
		if(!(si instanceof JPanel))
			throw new IllegalArgumentException("The "+Logger.STUDENT_INTERFACE_PROPERTY+" class "+
					(si == null ? null : si.getClass())+" must inherit from "+JPanel.class.getName());
		else
			studentInterface = (JPanel) si;
	}

	/**
	 * Generate a command-line argument in the form "-D<i>name</i>=<i>value</i>"
	 * from an applet parameter. Edit selected parameters as needed.
	 * @param pName
	 * @param pValue
	 * @return string in the form "-D<i>name</i>=<i>value</i>"
	 */
	private String paramToCmdLineArg(String pName, String pValue) {
		if(Logger.QUESTION_FILE_PROPERTY.equalsIgnoreCase(pName))
			return "-D"+pName+"="+editURLParameter(pValue);
		else if(LOGGING_PARAM.equalsIgnoreCase(pName))
			return "-D"+BR_Controller.USE_OLI_LOGGING+"="+(!("None".equalsIgnoreCase(pValue)));
		else if(SHOW_DEBUG_TRACES.equalsIgnoreCase(pName) && Boolean.parseBoolean(pValue))
		{
			trace.addDebugCodes("tsltsp,tsltstp,ll,br,applet,appletLauncher");
			return "-D"+BR_Controller.DEBUG_CODES+"=ll";
		}
		else if(SingleSessionLauncher.DEBUG_CODES.equalsIgnoreCase(pName))
		{
			if(pValue != null)
				trace.addDebugCodes(pValue.replaceAll("[, ]", ","));
			return "-"+SingleSessionLauncher.DEBUG_CODES;
		}
		else
			return "-D"+pName+"="+pValue;
	}

	/**
	 * Alter URL parameter values for the applet environment.
	 * @param pValue
	 * @return 
	 */
	private String editURLParameter(String pValue) {
		URL codeBase = getCodeBase(); 
		return codeBase.toString()+"/"+pValue;
//		return codeBase.getProtocol()+"://"+codeBase.getAuthority()+pValue;
	}

	/**
	 * @see java.applet.Applet#destroy()
	 */
	public void destroy() {
		if(trace.getDebugCode("applet"))
			trace.outNT("applet", "AppletLauncher.destroy()");
	}

	/**
	 * Should send problem summary to host and take down the objects created in {@link #start()}.
	 * @see java.applet.Applet#stop()
	 */
	public void stop() {
		if(trace.getDebugCode("applet"))
			trace.outNT("applet", "AppletLauncher.stop()");
		if(getController() != null)
			getController().closeStudentInterface();
		// dispose();  would call this if a JFrame, but there's no equivalent?
	}

	/**
	 * Convert parameters into command-line arguments for invoking
	 * {@link SingleSessionLauncher#SingleSessionLauncher(String[]), boolean} in {@link #start()}.
	 * Call {@link #createStudentInterface(String)}, {@link #setCaption(String)}. Instantiate 
	 * {@link SingleSessionLauncher#SingleSessionLauncher(String[], boolean) SingleSessionLauncher.SingleSessionLauncher(cmdLineList, false)}.
	 * Finally {@link SingleSessionLauncher#launch(javax.swing.JComponent) SingleSessionLauncher.launch(studentInterface)}.
	 * @see java.applet.Applet#start()
	 */
	public void start() {
		if(trace.getDebugCode("applet"))
			trace.outNT("applet", "AppletLauncher.start()");
		makeCmdLine();
		createStudentInterface(getParameter(Logger.STUDENT_INTERFACE_PROPERTY));		
		setCaption(getParameter(INFO_PARAM));
		createStudentInterfaceWrapper();
		String[] argv = cmdLineList.toArray(new String[0]);
		ctatLauncher = new SingleSessionLauncher(null, null, argv, false, ls, null, this);
		AppletAdvance appletAdvance = new AppletAdvance(this);
		wrapperSupport.addActionListener(appletAdvance);
		ctatLauncher.launchCL(this, studentInterface);
	}

	/**
	 * Print the height and width to stdout as "w=NNN h=MMM".
	 * @param args see {@link #usageExit(String)}
	 */
	public static void main(String[] args) {
		if(args.length < 1 || args[0].matches("^-[hH]"))
			usageExit("Help");
		for(int i=0; i < args.length; ++i) {
			try {
				Class cls = Class.forName(args[i]);
				Object obj = cls.newInstance();
				if(!(obj instanceof JPanel)) {
					System.err.printf("[%2s] Error: %s is not a JPanel subclass\n", i, args[i]);
					continue;
				}
				JPanel panel = (JPanel) obj;
				AppletLauncher al = new AppletLauncher();
				al.add(panel);
				al.validate();
				System.out.printf("w=%d h=%d\n", al.getWidth(), al.getHeight());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Display a help message and exit.
	 * @param errMsg optional error message to precede help.
	 * @return never
	 */
	private static void usageExit(String errMsg) {
		System.err.printf("%s%sUsage: java -cp ... %s interface\n"+
				"where--\n"+
				"  %s is the name of this class;\n"+
				"  interface is the full class name of an interface to size.\n"+
				"Writes 1 line \"w=NN h=MM\" where NN and MM are integer pixels.\n",
				errMsg != null ? errMsg : "", errMsg != null ? ". " : "",
				AppletLauncher.class.getName(), AppletLauncher.class.getName());
		System.exit(2);
	}

	/**
	 * Assemble all the infrastructure needed for supporting {@link StudentInterfaceWrapper}.
	 */
	private void createStudentInterfaceWrapper() {
        wrapperSupport = (VersionInformation.isRunningSimSt() && !Utils.isRuntime()
        		? new SimStWrapperSupport(getContentPane()) 
        		: new WrapperSupport(getContentPane()));
	}

	/**
	 * @return the {@link #wrapperSupport}
	 */
	public WrapperSupport getWrapperSupport() {
		return wrapperSupport;
	}

	public void mouseClicked(MouseEvent e) {
		wrapperSupport.mouseClicked(e);
	}

	public void mousePressed(MouseEvent e) {
		wrapperSupport.mousePressed(e);
	}

	public void mouseReleased(MouseEvent e) {
		wrapperSupport.mouseReleased(e);
	}

	public void mouseEntered(MouseEvent e) {
		wrapperSupport.mouseEntered(e);
	}

	public void mouseExited(MouseEvent e) {
		wrapperSupport.mouseExited(e);
	}

	public JComponent getTutorPanel() {
		return wrapperSupport.getTutorPanel();
	}

	public CTAT_Options setTutorPanel(JComponent tutorPanel) {
		add(tutorPanel);
		CTAT_Options result = wrapperSupport.setTutorPanel(tutorPanel);
		setVisible(true);
		return result;
	}

	public HintWindowInterface getHintInterface() {
		return wrapperSupport.getHintInterface();
	}

	public void enableLMSLogin(boolean loginEnabled) {
		// TODO Auto-generated method stub
	}

	public void showAdvanceProblemMenuItem() {
		// TODO Auto-generated method stub
		
	}

	public JFrame getActiveWindow() {
		return null;
	}

	public LogContext getLogger() {
		return wrapperSupport.getLogger();
	}

	public void requestHint() {
			// TODO Auto-generated method stub
		
	}

	public void requestDone() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return {@link #ctatLauncher}.{@link SingleSessionLauncher#getController() getController()}
	 */
	public BR_Controller getController() {
		if(ctatLauncher == null)
			return null;
		return ctatLauncher.getController();
	}

	/**
	 * @return {@link #ctatLauncher}.{@link SingleSessionLauncher#getController() getSessionId()}
	 */
	public Object getSessionId() {
		return ctatLauncher.getSessionId();
	}

	/**
	 * @return {@link SingleSessionLauncher#getProblemSummary()}
	 */
	public String getProblemSummary() {
		return ctatLauncher.getProblemSummary();
	}

	/** Parameter list. */
	public String[][] getParameterInfo() {
		return parameterInfo;
	}

	/**
	 * Override to deal with parameters that are URL-encoded.
	 * @param name parameter name
	 * @return result from super{@link #getParameter(String)}, decoded if marked
	 */
	public String getParameter(String name) {
		String result = super.getParameter(name);
		if(result == null)
			return result;
		String[] paramInfo = parameterInfoMap.get(name);
		if(paramInfo == null)
			return result;
		if(!paramInfo[1].contains(DECODE))
			return result;
		try {
			return URLDecoder.decode(result, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			trace.errStack("Error from AppletLauncher.getParameter("+name+"): "+e+"; cause "+e.getCause(), e);
			return result;
		}
	}
	
	/** Content for {@link #getParameterInfo()}. Elements are name, type, description. */
	private static final String[][] parameterInfo = {
		{ AUTHENTICITY_TOKEN_PARAM, DECODE, "TutorShop Rails token needed in call requests" },
		{ "lcId", "string", "Identifier used with TutorShop" },
		{ "reuse_swf", "boolean", "Reserved for future use" },
		{ TARGET_FRAME, "_self|etc", "For future use - should always be _self" },
		{ BR_Controller.PROBLEM_STATE_STATUS, "empty|etc", "For future use - should always be empty" },
		{ "SessionLog", "boolean", "For future use - should always be true" },
		{ LOGGING_PARAM, "None|ClientToService|etc", "Will try to log to "+Logger.LOG_SERVICE_URL_PROPERTY+" if not None" },
		{ Logger.LOG_SERVICE_URL_PROPERTY, "URL", "log_service_url" },
		{ CURRICULUM_SERVICE_URL_PARAM, "URL", "Send the problem summary to this URL" },
		{ RUN_PROBLEM_URL_PARAM, "URL", "Address for HTTP GET to show next problem" },
		{ "restore_problem_url", "URL", "Address for messages to restore an interrupted problem" },
		{ Logger.QUESTION_FILE_PROPERTY, DECODE, "Problem BRD file" },
		{ Logger.STUDENT_INTERFACE_PROPERTY, "URL", "Student Interface class" },
		{ "problem_position", "1,2,...", "problem_position" },
//		{ "BehaviorRecorderMode", "", "BehaviorRecorderMode" },  skip Flash "AuthorTimeTutoring"
		{ Logger.SOURCE_ID_PROPERTY, "CTAT_JAVA_TUTOR|etc", "DataShop logging parameter" },
		{ SHOW_DEBUG_TRACES, "boolean", "Turn on tracing" },
		{ SingleSessionLauncher.DEBUG_CODES, "applet,ls,tsltsp,tsltstp", "Comma-separated list of debug codes" },
		{ INFO_PARAM, DECODE, "Menu of problems in problem set" },
		{ "skills", DECODE, "skills" },
		{ Logger.STUDENT_NAME_PROPERTY, "userid", "Student identifier" },
		{ Logger.SESSION_ID_PROPERTY, "identifier", "session_id" },
		{ Logger.DATASET_NAME_PROPERTY, "DataShop identifier", "DataShop dataset name" },
		{ Logger.DATASET_LEVEL_TYPE+"1", "Assignment|Unit|etc", "DataShop dataset level type1" },
		{ Logger.DATASET_LEVEL_NAME+"1", "Assignment name|unit name|etc", "DataShop dataset level name1" },
		{ Logger.DATASET_LEVEL_TYPE+"2", "ProblemSet|module|etc", "DataShop dataset level type2" },
		{ Logger.DATASET_LEVEL_NAME+"2", "ProblemSet name|module name|etc", "DataShop dataset level name2" },
		{ Logger.PROBLEM_NAME_PROPERTY, "identifier", "DataShop problem name" },
		{ Logger.PROBLEM_CONTEXT_PROPERTY, "description", "DataShop problem context" },
		{ Logger.PROBLEM_TUTOR_FLAG_PROPERTY, "tutor|quiz", "DataShop problem tutor_flag" },
		{ Logger.CLASS_NAME_PROPERTY, "identifier", "TutorShop class name" },
		{ Logger.SCHOOL_NAME_PROPERTY, "identifier", "TutorShop school identifier" },
		{ Logger.INSTRUCTOR_NAME_PROPERTY, "userid", "TutorShop instructor name" },
		{ Logger.STUDY_CONDITION_NAME+"1", "independent variable value", "study_condition_name1" },
		{ Logger.STUDY_CONDITION_TYPE+"1", "independent variable name", "study_condition_type1" }
	};
	
	/** For hashed access to {@link #parameterInfo}. */
	private static Map<String, String[]> parameterInfoMap = new HashMap<String, String[]>();	
	static {                            // populate parameterInfoMap
		for(String[] pi : parameterInfo)
			parameterInfoMap.put(pi[0], pi);
	}
}
