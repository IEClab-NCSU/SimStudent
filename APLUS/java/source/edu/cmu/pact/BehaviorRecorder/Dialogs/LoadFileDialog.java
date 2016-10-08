package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;

import org.jdom.Element;

import pact.CommWidgets.UniversalToolProxy;

import edu.cmu.hcii.ctat.CTATFileItem;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTab;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTabManager;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.TutoringService.Monitor;
import edu.cmu.pact.TutoringService.RequestHandler;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctatview.CtatMenuBar;

public class LoadFileDialog extends RequestHandler {

	/** Attribute for result in response to request */
	public static final String RESULT_ATTR = "result";

	/** Attribute for filename in request. */
	public static final String FILE_ATTR = "file";

	/** Name for {@link RequestHandler}. */
	public static final String NAME = "loadFileDialog";

	/** Command-line argument bearing a brd file name. */
	public static final String PROBLEM_FILE_LOCATION = "ProblemFileLocation";

	/** Alternate argument bearing a brd file name. */
	public static final String PROBLEM_FILE_URL = "ProblemFileURL";
	
    private BR_Controller controller;
    private CTAT_Launcher server;
    

    public static CTATFileItem doDialog(BR_Controller controller, boolean loadIntoMainWindow) {
    	return doDialog(controller.getServer(), loadIntoMainWindow);
    }
    
    /**
     * Display a JFileChooser to open a .brd file.
     */
    public static CTATFileItem doDialog(CTAT_Launcher server, boolean loadIntoMainWindow) {
        LoadFileDialog lfd = new LoadFileDialog(server);
        return lfd.doOkButtonOtherLocation();
    }
    


    public LoadFileDialog(CTAT_Launcher server) {
    	this(server, server.getTabManager().getFocusedTab().getController());
    }

    public LoadFileDialog(CTAT_Launcher server, BR_Controller controller) {
    	this.server = server;
        this.controller = controller;
    }

    /**
     * Select a filename using {@link SaveFileDialog#getBrdFileOtherLocation(BR_Controller)}.
     * @return file chosen; null if none or canceled
     */
    private File getBRDOtherLocation() {
        String targetDir = SaveFileDialog.getBrdFileOtherLocation(controller);
    	return DialogUtilities.chooseFile(targetDir, new BrdFilter(),
    			"Open Behavior Graph", "Open", controller);
    }

    /**
     * 
     */
    /* Old version (single window) */
    private CTATFileItem doOkButtonOtherLocation() 
    {    
    	File fd = getBRDOtherLocation();
    	
    	if (fd == null)
    		return (null);
    	
    	String directory = fd.getParent();
    	
    	if (directory == null) // relative pathname?
    		directory = "";
    	else if (!directory.endsWith(File.separator))
    		directory = directory+File.separator;

        // Tue Oct 25 15:18:18 2005:: Noboru
        // Code for actually loading a BRD file is defined as a
        // separate method to be used for a batch mode
        doLoadBRDFile(getServer(), fd.getName(), directory, true);

        saveBRDDirectory(directory);
        //controller.getActiveWindow().setTitle("Cognitive Tutor Authoring Tools - " + controller.getProblemModel().getProblemFullName ());
        
        CTATFileItem anItem=new CTATFileItem ();
        anItem.setDirectory(directory);
        anItem.setFileName(fd.getName());
        
        return (anItem);
    }
    
    /**
     * Save the given directory to the preference "{@value SaveFileDialog#BRD_OTHER_DIR_KEY}".
     * @param directory
     */
    private void saveBRDDirectory(String directory) 
    {
        String prefDir=controller.getPreferencesModel().getStringValue(SaveFileDialog.BRD_OTHER_DIR_KEY);
        
        if ((directory!=null) && !(directory.equals(prefDir))) 
        {
            controller.getPreferencesModel ().setStringValue (SaveFileDialog.BRD_OTHER_DIR_KEY,directory);
            controller.getPreferencesModel ().saveToDisk ();
        }
	}

    // Modified 06/2013 to handle multiple graph windows (syyang)
    /**
     * Open a BRD file. Should be phased out in favor of calling the controller's server
     * as the first argument.
     * @param controller
     * @param problemName base name of file
     * @param directory 
     * @return result from {@link BR_Controller#openBRDFileAndSendStartState(String, String, edu.cmu.pact.ctat.model.Skills)}
     */
    @Deprecated
    public static boolean doLoadBRDFile(BR_Controller controller,
    		String problemName, String directory, boolean loadIntoMain) {
    	return doLoadBRDFile(controller.getServer(),
    			problemName, directory, loadIntoMain);
    }

    /**
     * Open a BRD file.
     * @param server
     * @param problemName
     * @param directory
     * @param loadIntoMain
     * @return result from {@link #doLoadBRDFile(CTAT_Launcher, BR_Controller, String, String, boolean)}
     */
    public static boolean doLoadBRDFile(CTAT_Launcher server,
    		String problemName, String directory, boolean loadIntoMain) {
        if (trace.getDebugCode("loadfile")) 
        	trace.out( "loadfile", "(doLoadBRDFile): problemName="+problemName+" directory="+directory);
        
        int tabNumber = 0;
        CTATTabManager tabManager = server.getTabManager();
        // if the file has already been loaded, reload it
        if(tabManager.hasLoadedFile(directory + problemName)) {
        	tabNumber = tabManager.getFocusedTab().getTabNumber();
        } else {
        	tabNumber = server.getDockManager().newGraphTab();
        }
        
        // otherwise, open a new tab (or work on the open one if this is the first (blank) tab
        if (tabNumber > 0) {
        	BR_Controller controller = tabManager.getTabByNumber(tabNumber).getController();
        	if(!controller.getProblemModel().isEmpty()) {
        		if(controller.saveCurrentProblemWithUserPrompt(true) == JOptionPane.CANCEL_OPTION) {
        			return false;
        		}
        	}
        	tabManager.setFocusedTab(tabManager.getTabByNumber(tabNumber), true);
        	boolean result = doLoadBRDFile(server, controller, problemName,
        			directory, loadIntoMain);
        	server.getDockManager().showGraphWindow(tabNumber);
        	
        	UniversalToolProxy utp = controller.getUniversalToolProxy();
        	if(utp != null)
        		utp.initUnmatchedSelectionsDialog();

        	return result;
        }
        
        return false;
    }

    
    private static boolean doLoadBRDFile(CTAT_Launcher server,
    			BR_Controller controller, String problemName, String directory,
    			boolean loadIntoMain) {
    	ProblemModel newProblemModel = controller.getProblemModel();
    	
        newProblemModel.setProblemName(problemName);
        newProblemModel.setCourseName("");
        newProblemModel.setUnitName("");
        newProblemModel.setSectionName("");
        newProblemModel.setProblemFullName(directory + problemName);
        
        //Gustavo 18 May 2007: if SimStudent is on, inform it about the BRD path.
        if (server!=null &&
        		server.getMissController()!=null &&
        				server.getMissController().getSimSt()!=null && loadIntoMain){
        	server.getMissController().getSimSt().setCurrentBrdPath(
            		newProblemModel.getProblemFullName());
        }
        boolean openedBRD = false;
        try {
        	ProblemModel pm = newProblemModel;
        	openedBRD =
        			controller.openBRDFileAndSendStartState(pm.getProblemFullName(), null, null);
        	ActionEvent ae = new ActionEvent(controller, 0, "Loaded problem "+pm.getProblemFullName());
        	controller.getUndoPacket().getInitializeAction().actionPerformed(ae);
        	controller.clearUnmatchedComponentsAndReviseConnectionStatus();

        	if (!Utils.isRuntime())
        	{
        		File converter=new File (controller.getProblemModel().getProblemFullName());            
        		controller.getCtatFrameController().getDockedFrame().getCtatMenuBar().addRecentfile (converter.getParent(),converter.getName());
        		getInterfaceDescriptionsIfNeeded(controller.getUniversalToolProxy());
        	}	
        	
        } catch (Exception e) {
            e.printStackTrace();
            String message = "<html>The file could not be found or the format of the file is not recognized. <br>"
        		+ "Please check the file and try again.";
            String title = "Error loading file";
            Utils.showExceptionOccuredDialog(e, message, title);
            
        }

        //added by dtasse@andrew.cmu.edu on 6/15/06
        server.getLoggingSupport().authorActionLog(AuthorActionLog.BEHAVIOR_RECORDER,
        		BR_Controller.OPEN_GRAPH, newProblemModel.getProblemFullName());

        return openedBRD;
    }

    /**
     * If student interface is connected, but we've received no component definitions from it,
     * try to retrieve them via a {@value MsgType#GET_ALL_INTERFACE_DESCRIPTIONS} request.
     * @param utp
     */
    private static void getInterfaceDescriptionsIfNeeded(UniversalToolProxy utp) {
    	if(utp == null || utp.hasInterfaceDescriptions())
    		return;
    	utp.getAllInterfaceDescriptions();
	}

	/**
     * Handle a remote request to load a brd file.
     * @param req the request, as an XML element
     * @return response, with result attribute
     * @see edu.cmu.pact.TutoringService.RequestHandler#handleRequest(org.jdom.Element)
     */
	public Element handleRequest(Element req) 
	{
		this.controller = this.server.getFocusedController();
		Element resp = new Element(NAME);
		String path = req.getAttributeValue(FILE_ATTR);
		resp.setAttribute(RESULT_ATTR, "Request received");
		(new Load(path)).start();
		
		if (trace.getDebugCode("loadfile"))
			trace.out("loadfile", "Load(\""+path+"\") started");
		
		/*
		File converter=new File (path);		
		controller.getDockedFrame().getCtatMenuBar().addRecentfile (converter.getParent(),converter.getName());
		*/
		
		return resp;		
	}

	/**
	 * Load a file in a separate thread, since may have to prompt whether to 
	 * save a file already loaded and edited.
	 */
	public class Load extends Thread {
		final String path;
		public Load(String path) {
			this.path = path;
		}
		public void run() {
			handleLoadFileRequest(path);
		}
	}

	/**
	 * Check whether this invocation should try, and if so pass a file name from the
	 * given command-line arguments to another instance of the program already running.
	 * @param testArg no-op if no element in argv contains this string
	 * @param argv arguments to test
	 * @return true if passed args: in this case, the current instance can exit
	 */
	public static boolean passFileNameToOtherInstance(String testArg, String[] argv) {
		boolean hasTestArg = false;
		int fileNameArg = -1;
		testArg = testArg.toLowerCase();
		String problem_file_location = PROBLEM_FILE_LOCATION.toLowerCase();
		String problem_file_url = PROBLEM_FILE_URL.toLowerCase();

		if (argv == null)
			return false;
		int i = 0;  // argv index
		for (i = 0; i < argv.length; ++i) {
			String arg = argv[i].toLowerCase();
			if (arg.contains(testArg))
				hasTestArg = true;
			else if (arg.contains(problem_file_location))
				fileNameArg = i;
			else if (arg.contains(problem_file_url))
				fileNameArg = i;
		}
		if (!hasTestArg)      // multiple instances of this version can coexist
			return false;
		if (fileNameArg < 0)    // no filename to pass
			return false;
		String filename = null;
		int eq = argv[fileNameArg].indexOf('=');
		if (eq >= 0)
			filename = argv[fileNameArg].substring(eq+1);
		else if (++fileNameArg < argv.length)
			filename = argv[fileNameArg];
		if (filename == null || filename.length() < 1)
			return false;

		String response = null;
		String msg = "<"+NAME+" "+FILE_ATTR+"=\""+filename+"\"/>";
		try {
			if (trace.getDebugCode("loadfile"))
				trace.out("loadfile", "to send to monitor: "+msg);
			response = Monitor.request(msg);
		} catch (Exception e) {
			if (trace.getDebugCode("loadfile"))
				trace.errStack("Error sending \""+msg+"\" to remote:\n  "+e+"; cause "+e.getCause(), e);
		}
		if (trace.getDebugCode("loadfile"))
			trace.out("loadfile", "response from monitor: "+response);
		return (response != null);
	}

	/**
	 * Process a request to load a .brd file. This method is built for 
	 * calls that result from a user double-clicking on a file.  It prompts
	 * the user to ask whether to save any currently-open graph.
	 * @param path filename supplied by the request
	 */
	public void handleLoadFileRequest(String path) {
		String dir = "", file = null;
		try {
			File absFile = new File(path);
			if (absFile.getParent() != null)
				dir = absFile.getParent()+File.separator;
			file = absFile.getName();
			if (!absFile.canRead())
				throw new Exception("File not found or not readable");
		} catch (Exception e) {
			String errMsg = "Error on request to load "+path+":\n"+e+
					(e.getCause() == null ? "" : "; cause "+e.getCause());
			trace.errStack(errMsg, e);
			Utils.showExceptionOccuredDialog(e, errMsg, "Error loading file");
			return;
		}			

		// may not need this - LoadFileDialog.doLoadBRDFile(CTAT_Launcher, String, String, boolean)
		// handles the case of tabs with already-loaded graphs
		/*
		ProblemModel pm = controller.getProblemModel();
		int result = JOptionPane.OK_OPTION;
    	if (pm != null && pm.getProblemGraph().getNodeCount() > 0)
    		result = controller.saveCurrentProblemWithUserPrompt(true);
		if (result == JOptionPane.CANCEL_OPTION)
			return;
		*/
		
		try {
	        CTATTabManager tabManager = server.getTabManager();
			if(tabManager.hasLoadedFile(dir + file)) return;
			boolean loaded = doLoadBRDFile(getServer(), file, dir, true);
			if (trace.getDebugCode("loadfile"))
				trace.out("loadfile", "doLoadBRDFile(ctlr, "+file+", "+dir+") loaded "+loaded);
	        saveBRDDirectory(dir);
	        if(loaded) {
	        	CtatMenuBar mb = server.getCtatMenuBar();
	        	mb.enableCreateStartStateMenus(false);
	        	mb.enableGotoStartStateMenus(true);
	        	mb.enablePrintGraphMenus(true);
	        	mb.enableSaveGraphMenus(true);
	        }
		} catch (Exception e) {
			String errMsg = "Error on request to load file "+file+" from "+dir+":\n"+e+
					(e.getCause() == null ? "" : "; cause "+e.getCause());
			trace.errStack(errMsg, e);
			Utils.showExceptionOccuredDialog(e, errMsg, "Error loading file");
		}			
	}
	
	private CTAT_Launcher getServer() {
		return this.server;
	}
	
}
