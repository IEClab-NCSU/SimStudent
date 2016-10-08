package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import pact.CommWidgets.StudentInterfaceWrapper;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

public class SaveFileDialog {

	private File destDir;

    /** Name of preference that records last brd load or save location. */
    public static final String BRD_OTHER_DIR_KEY = "BRD File other location";

    private BR_Controller controller;

    private boolean cancelled;

	private String problemName;

    /**
     * Public interface to this class. Performs the save-file function using
     * either a ProblemsOrganizer-style dialog or {@link DialogUtilities#chooseFile(String, FileFilter, String, String, BR_Controller)}
     * depending on the result from
     * {@link #getProblemsOrganizer(BR_Controller). 
     * 
     * @param controller
     *            the ubiquitous controller
     * @param problemNameP
     *            value for {@link #problemName}
     */
    public static boolean doDialog(BR_Controller controller, String problemNameP) {
    	String dirName = getBrdDirectoryToSuggest(controller);
        File promptDir = new File(dirName);
        if (trace.getDebugCode("br"))
        	trace.out("br", "prompted folder for "+problemNameP+" = " + promptDir);
        SaveFileDialog d = new SaveFileDialog(controller, problemNameP, promptDir);
        return d.doSaveFileOtherLocation();
    }

    /**
     * Suggest a directory in which to save the current problem file.
     * @param controller
     * @return path name of current .brd directory, if any;
     *         else preference {@value #BRD_OTHER_DIR_KEY};
     *         else result from {@link #getProjectsDirectory(BR_Controller)}
     */
    public static String getBrdDirectoryToSuggest(BR_Controller controller) {
    	String dirName = null;
    	String fullName = controller.getProblemModel().getProblemFullName();	
    	if(fullName != null) {
    		int sep = fullName.lastIndexOf(File.separatorChar);
    		if(sep < 0)
    			sep = fullName.lastIndexOf('/');
    		if(sep > 0)
    			dirName = fullName.substring(0, sep);
    	}
    	if(trace.getDebugCode("save"))
    		trace.out("save", "SFD.getBrdDirectoryToSuggest() fullName "+fullName+", brdOther "+
    				controller.getPreferencesModel().getStringValue(BRD_OTHER_DIR_KEY)+", projs "+
    				getProjectsDirectory(controller));
    	if(dirName == null || dirName.length() < 1)
    		dirName = controller.getPreferencesModel().getStringValue(BRD_OTHER_DIR_KEY);
    	if(dirName == null || dirName.length() < 1)
    		dirName = getProjectsDirectory(controller);
    	return dirName;
    }

	/**
     * For public access to this class, see
     * {@link #doDialog(BR_Controller, String)}.
     * 
     * @param controller
     * @param problemNameP value for {@link #problemName}
     * @param destDir value for {@link #destDir}
     *            directory object for ProblemsOrganizer/
     */
    private SaveFileDialog(BR_Controller controller, String problemName, File destDir) {

    	this.problemName = problemName;
        this.destDir = destDir;

        if (trace.getDebugCode("br"))
        	trace.out("br", "SaveFileDialog("+problemName+"): destDir " + destDir.getPath());

        this.controller = controller;

        if (!destDir.exists())
            destDir.mkdir();
    }

    /**
     * Try to find a suitable Projects directory.
     * @return preference {@value BR_Controller#PROJECTS_DIR}
     */
    public static String getProjectsDirectory(BR_Controller controller) {
    	String projectsStr = controller.getPreferencesModel().getStringValue(
                BR_Controller.PROJECTS_DIR);
        if (trace.getDebugCode("br"))
        	trace.out("br", "projectsStr[pref] = " + projectsStr);
        if (projectsStr == null || projectsStr.length() < 1) {
            
            projectsStr = getStudentInterfaceParentDir(controller);
            if (trace.getDebugCode("br")) 
            	trace.out("br", "projectsStr[studentInterface] = " + projectsStr);
        }
        if (projectsStr == null || projectsStr.length() < 1) {
            projectsStr = System.getProperty("user.dir");
            if (trace.getDebugCode("br"))
            	trace.out("br", "projectsStr[user.dir] = " + projectsStr);
        }
        return projectsStr;
	}

	/**
     * Try to return the path to the parent directory of the currently- loaded
     * student interface, if any. If the System property
     * {@link Utils#INTERFACE_HOME_PROPERTY} is set, use it. Else if a student
     * interface is loaded, try to resolve it relative to the current directory.
     * 
     * @param controller
     *            for {@link BR_Controller#getStudentInterface()}
     * @return absolute path to the parent; null if any of a whole bunch of
     *         links is broken
     */
    private static String getStudentInterfaceParentDir(BR_Controller controller) {
        URL studentInterfaceURL = null;
        String studentPanelURLstr =
        		(String) controller.getProperties().getProperty(Utils.INTERFACE_HOME_PROPERTY);
        try {
            if (studentPanelURLstr != null && studentPanelURLstr.length() > 0)
                studentInterfaceURL = new URL(studentPanelURLstr);
        } catch (MalformedURLException mue) {
            trace.err("Bad URL in System property "
                    + Utils.INTERFACE_HOME_PROPERTY + ": " + mue);
            studentInterfaceURL = null;
        }
        if (studentInterfaceURL == null) {
            StudentInterfaceWrapper studentInterface = controller.getStudentInterface();
            if (studentInterface != null) {
//                Object studentPanel = studentInterface.getTutorPanel();
//                if (studentPanel != null)
                    studentInterfaceURL = studentInterface.getClass().getResource("/");
            }
        }
        if (trace.getDebugCode("br"))
        	trace.out ("SFD.getStudentInterfaceParentDir() studentInterfaceURL "+studentInterfaceURL);
        if (studentInterfaceURL != null) {
            File studentInterfaceFile = Utils.getFileAsResource(studentInterfaceURL);
            if (trace.getDebugCode("br"))
            	trace.out ("SFD.getStudentInterfaceParentDir() studentInterfaceFile = " + studentInterfaceFile);
            if (studentInterfaceFile != null)
                return studentInterfaceFile.getAbsolutePath();
        }
        return null;
    }

    public boolean wasCancelled() {
        return cancelled;
    }


    /**
     * 
     */
    private boolean doSaveFileOtherLocation() {

        File chosenFile = DialogUtilities.chooseFile(destDir.getPath(), problemName,
        		new BrdFilter(), "Please set the file name", "Save", controller); 

        if (trace.getDebugCode("inter"))
        	trace.out("inter", "doSaveFileOtherLocation() chosenFile "+chosenFile);
        if (chosenFile == null)
            return false;

        String chosenFileName = chosenFile.getName();
        String chosenDir = chosenFile.getParent();

        String problemName = chosenFileName; 

        if (problemName.indexOf('/') != -1 || problemName.indexOf('\\') != -1
                || problemName.indexOf('*') != -1) {
            String newProblemName = problemName.replace('/', '_').replace('\\',
                    '_').replace('*', '_');
            if (JOptionPane.showConfirmDialog(controller.getActiveWindow(),
                    "The filename contains invalid characters.\nWould you like to use the name "
                            + newProblemName + "?", "Invalid filename",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
                problemName = newProblemName;
            else
            	return false;
        }
        if (!problemName.endsWith(".brd"))
        	problemName = problemName+".brd";
        String problemFullName = chosenDir + File.separator + problemName;
        
        chosenFile = new File(problemFullName);
        if (chosenFile.exists()) {
            int ans = JOptionPane.showConfirmDialog(controller.getActiveWindow(),
                    "The file already exists. Overwrite?", "File exists",
                    JOptionPane.OK_CANCEL_OPTION);
            if (ans != JOptionPane.OK_OPTION)
            	return false;
        }
        
        controller.getProblemModel().setProblemName(problemName);
        controller.getProblemModel().setProblemFullName(problemFullName);

        controller.getProblemModel().setCourseName("");
        controller.getProblemModel().setUnitName("");
        controller.getProblemModel().setSectionName("");

        String filename = controller.getProblemStateWriter().saveBRDFile(
                controller.getProblemModel().getProblemFullName());
		if(!Utils.isRuntime())
			controller.getServer().getDockManager().refreshGraphTitle(controller.getTabNumber());

        String prefDir = controller.getPreferencesModel().getStringValue(
                SaveFileDialog.BRD_OTHER_DIR_KEY);
        if (chosenDir != null && !(chosenDir.equals(prefDir))) {
            controller.getPreferencesModel().setStringValue(
                    SaveFileDialog.BRD_OTHER_DIR_KEY, chosenDir);
            controller.getPreferencesModel().saveToDisk();
        }
        return true;
        
    }
    
    
    /**
     * Return the initial directory for the BRD File Chooser dialog.
     * @param controller
     * @return value of preference {@link #BRD_OTHER_DIR_KEY} or
     *         value of property {@link Utils#INTERFACE_HOME_PROPERTY} or
     *         value of {@link #getProjectsDirectory(BR_Controller)} or
     *         current working directory or
     *         user's home directory
     */
    public static String getBrdFileOtherLocation(BR_Controller controller) {
        String targetDir = controller.getPreferencesModel().getStringValue(BRD_OTHER_DIR_KEY);
        String interfaceHome = (String) controller.getProperties().getProperty(Utils.INTERFACE_HOME_PROPERTY);
        String projectsDir = getProjectsDirectory(controller);
        
        if (targetDir != null && !(new File(targetDir)).isAbsolute())
        	targetDir = (new File(projectsDir, targetDir)).getAbsolutePath();
        if (trace.getDebugCode("br"))
        	trace.out("br", "SFD.getBrdFileOtherLocation: BRD_OTHER "+targetDir+
        			", INTERFACE_HOME "+interfaceHome+", getProjectsDirectory() "+projectsDir);
        if (targetDir == null || !(new File(targetDir)).isDirectory())
        	targetDir = interfaceHome;
        if (targetDir == null || !(new File(targetDir)).isDirectory())
        	targetDir = projectsDir;
        if (targetDir != null)
        	return targetDir;
        {
            String defaultDir = System.getProperty("user.dir");
            if (defaultDir == null)
                defaultDir = System.getProperty("user.home");
            if (targetDir == null)
                targetDir = defaultDir;
        }
        return targetDir;
    }
}
