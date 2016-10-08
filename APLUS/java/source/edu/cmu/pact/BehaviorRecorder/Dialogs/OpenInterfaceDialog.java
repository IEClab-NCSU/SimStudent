package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.event.WindowListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import pact.CommWidgets.StudentInterfaceWrapper;
import pact.CommWidgets.TutorWrapper;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.StudentInterfacePanel;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.ctat.view.CtatFrame;

/*
 * This class loads a Tutor Window interface into the Behavior Recorder. The
 * user must specify a .class file which represents a valid Authoring Tools
 * tutor interface. It must inherit from the pact.CommWidgets.TutorWindow
 * class.
 * 
 * @author mpschnei
 * 
 *  
 */
public class OpenInterfaceDialog {

    private static final String INTERFACE_DIRECTORY = "Interface Directory";

    private static final String CustomFileOp = "Open interface file";

    private OpenInterfaceDialog() {
    }

    public static int CloseCurrentInterfaceWithUserPrompt(BR_Controller controller) {
        String message[] = { "Only one student interface can be open at a time.  " +
        		"Do you want to close the current student interface?",
                " " };

        int value = JOptionPane.showConfirmDialog(controller.getActiveWindow(),
                    message, "Close Current Student Interface",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
        

        if (value != JOptionPane.YES_OPTION)
            return value; 


        return value;
    }
    
    /*
     * Display a file chooser dialog, and open and initialize the chosen
     * interface.
     */
    public static boolean openInterface(CTAT_Launcher server) {
    	BR_Controller controller = server.getFocusedController();
        if (controller.getStudentInterface() != null) {
            int result = CloseCurrentInterfaceWithUserPrompt(controller);
            if (result == JOptionPane.NO_OPTION )
                return false;
            else 
            {   
            	controller.closeStudentInterface();
            	server.getCtatFrameController().getDockedFrame().getCtatMenuBar().enableInterfaceMenus(true);
            }
        }
        
        try {
            String currentDir = controller.getPreferencesModel()
                    .getStringValue(INTERFACE_DIRECTORY);

            if (currentDir == null || currentDir.length() < 1)
                currentDir = controller.getPreferencesModel().getStringValue(
                        BR_Controller.PROJECTS_DIR);

            if (currentDir == null || currentDir.length() < 1)
                currentDir = System.getProperty("user.dir");

            if (trace.getDebugCode("inter")) trace.out("inter", "currentDir " + currentDir);

            File file = DialogUtilities.chooseFile(currentDir, new ClassFilter(),
            		CustomFileOp, CustomFileOp, controller);
            if (file == null)
                return true;           // user pressed Cancel

            /** added 6/15/06 by dtasse */
            controller.getLoggingSupport().authorActionLog(AuthorActionLog.BEHAVIOR_RECORDER,
            		BR_Controller.OPEN_INTERFACE, file.getAbsolutePath());
            
            currentDir = file.getParent();
            controller.getPreferencesModel().setStringValue(
                    INTERFACE_DIRECTORY, currentDir);

            StudentInterfaceWrapper window = openInterface(file, controller);
            
            if (window != null) 
            {
                window.setVisible(true);
                //trace.out("mg", "OpenInterfaceDialog (openInterface): working with controller " + controller.getTabNumber());
                controller.setStudentInterface(window);
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            if (trace.getDebugCode("mps")) trace.out("mps", "oops");
            return false;
        }

        return true;

    }
    /*
    public static boolean openInterface(BR_Controller controller) {
        if (controller.getStudentInterface() != null) {
            int result = CloseCurrentInterfaceWithUserPrompt(controller);
            if (result == JOptionPane.NO_OPTION )
                return false;
            else 
            {   
            	controller.closeStudentInterface();
            	controller.getCtatFrameController().getDockedFrame().getCtatMenuBar().enableInterfaceMenus(true);
            }
        }
        
        try {
            String currentDir = controller.getPreferencesModel()
                    .getStringValue(INTERFACE_DIRECTORY);

            if (currentDir == null || currentDir.length() < 1)
                currentDir = controller.getPreferencesModel().getStringValue(
                        BR_Controller.PROJECTS_DIR);

            if (currentDir == null || currentDir.length() < 1)
                currentDir = System.getProperty("user.dir");

            if (trace.getDebugCode("inter")) trace.out("inter", "currentDir " + currentDir);

            File file = DialogUtilities.chooseFile(currentDir, new ClassFilter(),
            		CustomFileOp, CustomFileOp, controller);
            if (file == null)
                return true;           // user pressed Cancel

            // added 6/15/06 by dtasse
            controller.getLoggingSupport().authorActionLog(AuthorActionLog.BEHAVIOR_RECORDER,
            		BR_Controller.OPEN_INTERFACE, file.getAbsolutePath());
            
            currentDir = file.getParent();
            controller.getPreferencesModel().setStringValue(
                    INTERFACE_DIRECTORY, currentDir);

            StudentInterfaceWrapper window = openInterface(file, controller);
            
            if (window != null) 
            {
                window.setVisible(true);
                controller.setStudentInterface(window);
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            if (trace.getDebugCode("mps")) trace.out("mps", "oops");
            return false;
        }

        return true;

    }
    */

    /**
     * Open a student interface window from a .class file. Calls
     * {@link #openInterface(Class, BR_Controller)}.
     * @param file the .class file
     * @param controller controller to connect to
     * @return container for the new student interface
     */
    public static StudentInterfaceWrapper openInterface(File file, BR_Controller controller) {

        if (trace.getDebugCode("inter")) trace.out("inter", "opening file " + file.getName());

        Class interfaceClass = getInterfaceClass(file, controller);

        return openInterface(interfaceClass, controller, file.toString());
    }
    

    /**
     * Open a student interface window from a .class file.
     * @param file the .class file
     * @param controller controller to connect to
     * @param interfaceFileName name to display to user if can't load the file
     * @return container for the new student interface
     */
    public static StudentInterfaceWrapper openInterface(Class interfaceClass, BR_Controller controller,
    		String interfaceFileName) {

        String errorMessage = "<html><b>Error:</b> could not load interface <br>"
                + (interfaceFileName == null ? interfaceClass.getName() : interfaceFileName)
                + "."
                + "<br><br>Make sure your class extends <b>JPanel</b> <br>";
        
        String superclassName;
        try {
            superclassName = interfaceClass.getSuperclass().getName();
        } catch (NullPointerException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(controller
                    .getActiveWindow(), errorMessage,
                    "Error loading interface", JOptionPane.INFORMATION_MESSAGE);
            throw new RuntimeException("Can't load file");
        }
        TutorWrapper tutorWrapper = null;

        JPanel tutorPanel = null;

        controller.clearUnmatchedComponentsAndReviseConnectionStatus();
        
        if (superclassName.equals("javax.swing.JPanel")) {
            controller.resetAllWidgets();

            tutorWrapper = new TutorWrapper(controller);

            try {
                tutorPanel = (JPanel) interfaceClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
                return null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }

            if (tutorPanel instanceof StudentInterfacePanel){
            	((StudentInterfacePanel) tutorPanel).setController(controller);
            	
            }
            	
            controller.setStudentInterface(tutorWrapper);
            CTAT_Options options = tutorWrapper.setTutorPanel(tutorPanel);
            controller.setOptions(options);
            
            CtatModeModel cmm = controller.getCtatModeModel();
            ProblemModel pm = controller.getProblemModel();
            
            if (!(CtatModeModel.DEFINING_START_STATE.equals(cmm.getCurrentAuthorMode()))
            		&& pm != null && pm.getNodeCount() > 0) {
            	controller.goToStartState();
            }

        } else {

            JOptionPane.showMessageDialog(controller
                    .getActiveWindow(), errorMessage,
                    "Error loading interface", JOptionPane.INFORMATION_MESSAGE);
            trace.printStack("Error loading interface");
            throw new RuntimeException("Can't load file: superclass == "
                    + superclassName);
        }

        WindowListener[] listeners = tutorWrapper.getWindowListeners();
        for (int i = 0; i < listeners.length; i++) {
            // trace.out (5, "xx", "listener = " + listeners[i]);
            tutorWrapper.removeWindowListener(listeners[i]);
        }
        tutorWrapper.addWindowListener(((CtatFrame)controller.getDockedFrame()).getCtatMenuBar());

        if(interfaceFileName != null) {
        	controller.getPreferencesModel().setStringValue("Interface File", interfaceFileName);
        	controller.getPreferencesModel().saveToDisk();
        }

        ((CtatFrame)controller.getDockedFrame()).getCtatMenuBar().enableInterfaceMenus(true);

        if(controller.getUniversalToolProxy() != null)
        	controller.getUniversalToolProxy().handleMessage(MessageObject.create(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS));

        return tutorWrapper;

    }

    /**
     * @param file
     * @param controller
     * @return
     */
    public static Class getInterfaceClass(File file, BR_Controller controller) {
        // This loop tries to determine the package name of the class without
        // the user
        // having to specify it. It walks up the directory tree and tries to
        // load
        // the class at each level.
        Class interfaceClass = null;
        File currentDir = file;
        URL url = null;
        boolean found = false;
        File homeDir = currentDir.getParentFile(); // save for studentLog
        controller.setHomeDir(homeDir.getAbsolutePath() + File.separator);
    	PreferencesModel pm = controller.getPreferencesModel();
    	if (pm != null)
    		pm.setStringValue(BR_Controller.PROBLEM_DIRECTORY,
    				homeDir.getAbsolutePath());
        while (!found) {

            currentDir = currentDir.getParentFile();
            if (trace.getDebugCode("inter")) trace.out("inter", "current dir = " + currentDir);

            // We got to the top of the directory hierarchy and
            // we weren't able to load the file from any of the directories
            if (currentDir == null) {
                String message = "<html>Error: could not load interface <br>"
                        + file
                        + "."
                        + "<br><br>Make sure your class file is located in a folder <br>hierarchy that matches the package name.";

                JOptionPane.showMessageDialog(controller
                        .getActiveWindow(), message, "Error loading interface",
                        JOptionPane.INFORMATION_MESSAGE);
                trace.printStack("Error loading interface");
                throw new RuntimeException("Can't load file");
            }

            String classname = file.toString().substring(
                    currentDir.toString().length() + 1,
                    file.toString().length() - 6);
            classname = classname.replace('\\', '.');
            classname = classname.replace('/', '.');

            try {
                url = currentDir.toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // String fileName = url.getQuery();
            URL[] urlList = new URL[1];
            urlList[0] = url;
            URLClassLoader loader = new URLClassLoader(urlList);

            try {
                found = true;
                if (trace.getDebugCode("inter")) trace.out("inter", "loading class: " + classname);
                // System.out.println( "loading class: " + classname );
                interfaceClass = loader.loadClass(classname);
            } catch (ClassNotFoundException e1) {
                found = false;
            } catch (NoClassDefFoundError e2) {
                if (trace.getDebugCode("inter")) trace.out("inter", "" + e2.getMessage());
                if (e2.getMessage().equals("pact/CommWidgets/TutorPanel"))
                    showIncompatibleInterfaceDialog(controller
                            .getActiveWindow());
                found = false;
            } catch (SecurityException e) {
                found = false;
            }
        }

        // Set the INTERFACE_HOME_PROPERTY, to display
        // in the preferences panel.
        if (trace.getDebugCode("inter")) trace.out("inter", "System.setProperty("
                + Utils.INTERFACE_HOME_PROPERTY + ", " + url.toString() + ")");
        System.setProperty(Utils.INTERFACE_HOME_PROPERTY, url.toString());
        controller.getProperties().setProperty(Utils.INTERFACE_HOME_PROPERTY, url.toString());
        return interfaceClass;
    }

    private static void showIncompatibleInterfaceDialog(JFrame d) {

        String message = "<html>"
                + "This interfaces references pact/CommWidgets/TutorPanel.  <br>"
                + "This type of interface is not supported past CTAT version 1.5. <br><br>"
                + "Please see the CTAT help page for information on how to upgrade your interface.";
        String title = "Incompatible Interface";
        JOptionPane.showMessageDialog(null, message, title,
                JOptionPane.WARNING_MESSAGE);
        throw new RuntimeException(
                "Incompatible interface: references pact/CommWidgets/TutorPanel");
    }

    public static class ClassFilter extends FileFilter {
    	
    	/**
    	 * Accept directories and .class files.
    	 * @param f
    	 * @return
    	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
    	 */
    	public boolean accept (File f) {
    		return (f.isDirectory() || "class".equalsIgnoreCase(getExtension(f)));
    	}

    	/**
    	 * From Swing implementation. Not used in AWT.
    	 * @return label for drop-down
    	 */
        public String getDescription() {
            return "Java class files";
        }

        private String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');
            if (i > 0 && i < s.length() - 1) {
                ext = s.substring(i + 1).toLowerCase();
            }
            return ext;
        } // The description of this filter

    }
}
