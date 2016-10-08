package edu.cmu.pact.BehaviorRecorder.Controller.LMS;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import pact.CommWidgets.SkillometerManager;
import pact.CommWidgets.StudentInterfaceWrapper;
import pact.CommWidgets.TutorWindow;
import cl.LMS.client.LMS_Client;
import cl.LMS.exception.LMS_Exception;
import cl.common.CL_Class;
import cl.common.CL_Constants;
import cl.common.CL_Curriculum;
import cl.common.CL_Instructor;
import cl.common.CL_School;
import cl.common.CL_Section;
import cl.common.CL_SectionSkill;
import cl.common.CL_Student;
import cl.common.CL_StudentSkill;
import cl.common.CL_StudentStatus;
import cl.common.CL_Unit;
import edu.cmu.pact.CTAT_LMS_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.OpenInterfaceDialog;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.CtatLMSClient;
import edu.cmu.pact.ctat.model.CtatModeModel;

/**
 * This class used to load and manage the Carnegie Learning LMS system.
 * 
 * mps 8/17/05
 * 
 */

public class CTAT_LMS implements ActionListener, CtatLMSClient {

    private String initialStateFolder;

    private boolean initialized;

    private LMS_Client lmsClient;

    private CL_School clSchool;

    private List classList;

    private List studentList;

    private CL_Class clClass;

    private CL_Curriculum clCurriculum;

    private CL_Student clStudent;

    private CL_StudentStatus clStudentStatus;

    private CL_Unit clUnit;

    private boolean loggedIn;

    private String lastSectionName;

    private String lastUnitName;

    private String currentSectionName;

    private String currentUnitName;

    private BR_Controller controller;

    private HashMap interfaceTable = new HashMap();

    private SkillometerManager skillometerManager;

    private CTAT_LMS_Launcher ctatCarnegieLearningLauncher;

    // //////////////////////////////////////////////////////////////
    /**
     */
    // //////////////////////////////////////////////////////////////
    public CTAT_LMS(BR_Controller controller,
            SkillometerManager skillometerManager) {
        this.controller = controller;
        this.skillometerManager = skillometerManager;

        
    }

    // //////////////////////////////////////////////////////////////
    /**
     * public method which brings up a dialog box allowing the user to choose a
     * class and a student name.
     */
    // //////////////////////////////////////////////////////////////
    public void login() {

    }

    // //////////////////////////////////////////////////////////////
    /**
     */
    // //////////////////////////////////////////////////////////////
    private void loginStudent(String studentName) {
        LMS_Client.Student_N_Curricula curricula = null;

        try {

            // Re-create the student log with the LMS login name
            controller.getLoggingSupport().setStudentName(studentName);
            controller.getLoggingSupport().resetLogger();
            // Disable author logging to make things faster
            controller.getLoggingSupport().setEnableAuthorLog(false);

            if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "school = " + clSchool + " student =  "
                    + studentName);
            curricula = lmsClient.loginStudent(clSchool, studentName, null,
                    null, false);

            if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "curricula = "
                    + curricula.activeCurriculaList);

            if (curricula.activeCurriculaList.size() <= 0) {
                String title = "Error loading curricula";
                String message = "<html>No valid curricula were found for the student "
                        + studentName
                        + "<br>"
                        + "at root.ref location "
                        + getRootRef();
                JOptionPane.showMessageDialog(controller.getJGraphWindow(), message,
                        title, JOptionPane.WARNING_MESSAGE);
                return;
            }
            clCurriculum = (CL_Curriculum) curricula.activeCurriculaList.get(0);

            if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "clCurriculum.getId() = "
                    + clCurriculum.getId());

            loadInterfaceMapFile((String) clCurriculum.getId());

            clStudent = lmsClient.getStudentPosition(clSchool,
                    curricula.student, clCurriculum);
            if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "clStudent = " + clStudent);

            clStudentStatus = lmsClient.getCurrentProblemState(clSchool,
                    clStudent, clCurriculum);

            if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "clStudentStatus = " + clStudentStatus);

            clUnit = clStudentStatus.getCurrentUnit();
            if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "clUnit= " + clUnit);

            CL_Section clSection = clStudentStatus.getCurrentSection();
            if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "clSection = " + clSection);

            if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "problem list = "
                    + clStudentStatus.getCompiledProblemList());

            CL_Instructor clInstructor = (CL_Instructor) lmsClient
                    .getAllInstructors(clSchool).get(0);
            if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "clInstructor = " + clInstructor);

            // if the name starts with _ then it's a read-only student and
            // there's no need to reset to the beginning
            if (!(clStudent.getFirstName().charAt(0) == '_')) {
                int answer = JOptionPane.showConfirmDialog(null,
                        "Would you like to reset the unit \n "
                                + "and section to the beginning?",
                        "Reset unit/section?", JOptionPane.YES_NO_OPTION);

                if (answer == JOptionPane.YES_OPTION)
                    lmsClient.changeStudentPlacement(clStudent, clCurriculum,
                            clUnit, clSection, null, clInstructor, "");
            }

            clStudent = lmsClient.getStudentPosition(clSchool,
                    curricula.student, clCurriculum);

//            initSkillometer();

        } catch (LMS_Exception e) {
            e.printStackTrace(); // To change body of catch statement use
            // Options | File Templates.
            return;
        }

        loadInterfaceAndProblem();

        loggedIn = true;
    }

    private void loadInterface(String interfaceName) {
        controller.closeCurrentInterface();

        trace.out ("inter", "load interface");
        String filename = getInitialStateFolder() + interfaceName + ".class";
//
//        trace.out ("inter", "filename = " + filename);
//        File f = new File(filename);
//
//        Class interfaceClass = OpenInterfaceDialog.getInterfaceClass(f, controller);
//        trace.out ("inter", "interface class = " + interfaceClass);
//        JPanel tutorPanel;
//        try {
//            tutorPanel = (JPanel) interfaceClass.newInstance();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//            return;
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//            return;
//        }
//        trace.out("inter", "tutor panel = " + tutorPanel);
//        
//        controller.getLauncher().launch(tutorPanel);
        
        StudentInterfaceWrapper window2 = controller.getStudentInterface();
        Point p = null;
        if (window2 instanceof TutorWindow) {
            p = ((TutorWindow) window2).getLocation();
            window2.setVisible(false);
            ((TutorWindow) window2).dispose();
        }
        StudentInterfaceWrapper window = OpenInterfaceDialog.openInterface(new File(
                filename), controller);
        if (p != null && window instanceof TutorWindow)
        	((TutorWindow) window).setLocation(p);
        if (window != null)
            window.setVisible(true);
    }

    private void loadInterfaceMapFile(String unitID) {

        String rootRef = getRootRef();
        if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "root.ref = " + rootRef);

        String filename = rootRef + File.separator + "Administrative"
                + File.separator + "Curricula" + File.separator + unitID
                + ".interfaces";
        if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "file = " + filename);

        SAXBuilder builder = new SAXBuilder();

        Document doc;
        try {
            doc = builder.build(filename);
            loadInterfaceMapDocument(doc);
        } catch (Exception e) {
            if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "error reading file " + filename + ": " + e);
            e.printStackTrace();
            return;
        }
    }

    private String getRootRef() {
        String ref = System.getProperty("root.ref");
        if (ref != null)
            return ref;
        trace
                .out("ctat_lms",
                        "root.ref system property not set, trying Root.ref file in working directory");
        try {
            BufferedReader in = new BufferedReader(new FileReader("Root.ref"));
            String str;
            ref = in.readLine();
            in.close();
        } catch (IOException e) {
            if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms",
                            "File Root.ref not found.  Cannot load interfaces correctly.");
             e.printStackTrace();
            return null;
        }
        if (trace.getDebugCode("inter")) trace.out("inter", " root.ref = " + ref);
        return ref;

    }

    private void loadInterfaceMapDocument(Document doc) {
//        trace.out("ctat_lms", "LOADING INTERFACE MAP");
        Element root = doc.getRootElement();
        Iterator interfaces = root.getChildren().iterator();
        while (interfaces.hasNext()) {
            Element el = (Element) interfaces.next();
            Iterator problems = el.getChildren().iterator();
            String interfaceName = el.getAttributeValue("name");
            while (problems.hasNext()) {
                Element prob = (Element) problems.next();
                String problemName = prob.getAttributeValue("name")
                        .toUpperCase();
//                trace.out("ctat_lms", "prob = " + problemName + " interface = "
//                        + interfaceName);
                interfaceTable.put(problemName.toUpperCase(), interfaceName);
            }
        }
    }

    // //////////////////////////////////////////////////////////////
    /**
     */
    // //////////////////////////////////////////////////////////////
    private void loadBRDFile(String problemName) {
        String filename = getInitialStateFolder() + problemName + ".brd";

        if (trace.getDebugCode("inter")) trace.out("inter", "load new brd file: " + filename);

        controller.reset();

        try {
        	problemName = getCLProblemName(problemName);
            boolean result =
            	controller.openBRDFileAndSendStartState(filename, problemName);
        } catch (Exception e) {
            e.printStackTrace();
            String message = "<html>The file could not be found or the format of the file is not recognized. <br>"
                + "Please check the file and try again.";
            String title = "Error loading file";
            Utils.showExceptionOccuredDialog(e, message, title);
        }

        controller.getProblemModel().setProblemName(problemName);

    }

    /**
     * Get the problemName as defined in {@link #clStudentStatus}.
     * @param problemName value to return if CL problem name empty or null
     * @return problemName from {@link CL_StudentStatus#getCurrentProblem()}
     */
    private String getCLProblemName(String problemName) {
    	if (clStudentStatus == null)
    		return problemName;
    	if (clStudentStatus.getCurrentProblem() == null)
    		return problemName;
    	String clProblemName = clStudentStatus.getCurrentProblem().getProblemName();
    	if (clProblemName == null || clProblemName.length() < 1)
    		return problemName;
    	return clProblemName;
	}

	// //////////////////////////////////////////////////////////////
    /**
     */
    // //////////////////////////////////////////////////////////////
    public boolean isStudentLoggedIn() {
        return loggedIn;
    }
    
    // //////////////////////////////////////////////////////////////
    /**
     */
    // //////////////////////////////////////////////////////////////
    public void logout() {
        if (ctatCarnegieLearningLauncher == null)
            return;
        ctatCarnegieLearningLauncher.setIsFinished(true);
    }

    // //////////////////////////////////////////////////////////////
    /**
     * Store the student's skills and intermediate problem state with LMS Action
     * must be CL_StudentStatus.DONE, CL_StudentStatus.QUIT, or
     * CL_StudentStatus.SKIP
     */
    // //////////////////////////////////////////////////////////////
    private void storeStudentState(String action) {
        CL_StudentStatus.State newState = new CL_StudentStatus.State();
        newState.setAction(action);

        String problemState = "current node here";
        // ((Vertex) ESE_Frame.instance().currNode.getElement()).getText();
        // Store the current state of the Behavior Diagram
        newState.setCurrentProblemState(problemState);
        if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "STORING STUDENT STATE: problem state = "
                + problemState);

        // Store the skills
        
        if (skillometerManager != null) {
        Vector names = skillometerManager.getSkillNames();
            for (int i = 0; i < names.size(); i++) {
                String skillName = (String) names.elementAt(i);
                float pKnown = skillometerManager.getSkillValue(skillName);
                if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "adding skill " + skillName + " pKnown = "
                        + pKnown);
                newState.addStudentSkill(skillName, pKnown);
            }
        }
        newState.setHints(0);
        newState.setErrors(0);
        newState.setUniqueErrors(0);
        newState.setUniqueHints(0);

        // Store into LMS
        try {
            clStudentStatus.mergeUpdates(newState);
            lmsClient.putStudentProblemData(clSchool, clStudent, clCurriculum,
                    clStudentStatus);
        } catch (LMS_Exception e) {
            if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "Error writing student problem data");
            e.printStackTrace(); // To change body of catch statement use
                                    // Options | File Templates.
        }

    }

    // //////////////////////////////////////////////////////////////
    /**
     * advance the problem in lms, update the skillometer if it's a new unit,
     * and load the new BRD problem file into the interface
     * 
     */
    // //////////////////////////////////////////////////////////////
    public void advanceProblem() {

        if (trace.getDebugCode("inter")) trace.out("inter", "**** advance problem now");

        if (!loggedIn) {
            trace.err("Cannot advance problem when student is not logged in.");
            return;
        }
        try {

            storeStudentState(CL_StudentStatus.DONE);

            // UniversalToolProxy.instance().logProblemStop(
            // clStudentStatus.getCurrentProblem().getProblemName(),
            // clCurriculum.getCurriculumName(),
            // clStudentStatus.getCurrentUnit().getUnitName(),
            // clStudentStatus.getCurrentSection().getSectionName());

            lastSectionName = clStudentStatus.getCurrentSection()
                    .getSectionName();
            lastUnitName = clStudentStatus.getCurrentUnit().getUnitName();

            LMS_Client.Problem_N_Activity_Status activityStatus = lmsClient
                    .advanceToNextProblem(clSchool, clStudent, clCurriculum);

            clStudentStatus = lmsClient.getCurrentProblemState(clSchool,
                    clStudent, clCurriculum);

            currentSectionName = clStudentStatus.getCurrentSection()
                    .getSectionName();
            currentUnitName = clStudentStatus.getCurrentUnit().getUnitName();
            
            if (trace.getDebugCode("inter")) trace.out("inter", "*** lastSectionName "+lastSectionName+", lastUnitName "+
            		lastUnitName+", currentSectionName "+currentSectionName+", currentUnitName "+
            		currentUnitName+", problem_status_d "+activityStatus.problem_status_d+
					", activity_status_d "+activityStatus.activity_status_d);
            
            // Check for new unit, new section
            if (activityStatus.problem_status_d
                    .equals(CL_Constants.NEW_SECTION)) {
            	boolean keepGoing = nextSection(activityStatus);
            	if (!keepGoing)
            		return;
            } else if (activityStatus.problem_status_d
                    .equals(CL_Constants.NEW_UNIT)) {
                boolean keepGoing = nextUnit(activityStatus);
                if (!keepGoing)
                    return;
            }
            else if (activityStatus.problem_status_d
                    .equals(CL_Constants.END_CURRICULUM)) {
                endCurriculum();
                return;
            }
        } catch (LMS_Exception e) {
            e.printStackTrace();
            return;
        }
        loadInterfaceAndProblem();
        return;
    }

    /**
     * 
     */
    public void loadInterfaceAndProblem() {
        String problemName = clStudentStatus.getCurrentProblem()
                .getProblemName();

        String interfaceName = (String) interfaceTable.get(problemName
                .toUpperCase());

        if (trace.getDebugCode("inter")) trace.out("inter", "!!!!  loading interface: new problem = " + problemName
                + " interface = " + interfaceName);

        loadInterface(interfaceName);

        loadBRDFile(problemName);

        controller.setBehaviorRecorderMode(CtatModeModel.EXAMPLE_TRACING_MODE);
    }

    private void endCurriculum() {
        JOptionPane.showMessageDialog(controller.getStudentInterface().getActiveWindow(),
                "Congratulations.  You have now completed this curriculum.\n"
                        + "You will be automatically logged out.");
        logout();
    }

    /**
     * Display a dialog appropriate  the end of a curriculum section. Calls
     * {@link #sameExecutable(LMS_Client.Problem_N_Activity_Status)}
     * to decide whether we can go on in this executable or must change
     * executables. FIXME: in this version of CL code, a change of executable
     * requires a logout.  
     * @param activityStatus argument for sameExecutable()
     * @return true if we can continue in this executable
     */
     private boolean nextSection(LMS_Client.Problem_N_Activity_Status activityStatus) {
        
    	boolean keepGoing = sameExecutable(activityStatus);
    	if (keepGoing) {
            JOptionPane.showMessageDialog(controller.getStudentInterface().getActiveWindow(),
            		"You have finished the section " + lastSectionName
					+ ".\nNow you will begin the section "
					+ currentSectionName + ".");
//	        initSkillometer();
    	} else {
            JOptionPane.showMessageDialog(controller.getStudentInterface().getActiveWindow(),
            		"You have finished the section " + lastSectionName
					+ ".\nYou must log in again to continue with the next section.");
    		logout();  // FIXME: see header note
    	}
        return keepGoing;
    }

    /**
     * Display a dialog appropriate at the end of a curriculum unit. Calls
     * {@link #sameExecutable(LMS_Client.Problem_N_Activity_Status)}
     * to decide whether we can go on in this executable or must change
     * executables. FIXME: in this version of CL code, a change of executable
     * requires a logout.  
     * @param activityStatus argument for sameExecutable()
     * @return true if we can continue in this executable
     */
     private boolean nextUnit(LMS_Client.Problem_N_Activity_Status activityStatus) {
        
    	boolean keepGoing = sameExecutable(activityStatus);
    	if (keepGoing) {
    		JOptionPane.showMessageDialog(controller.getStudentInterface().getActiveWindow(),
    				"You have finished the unit " + lastUnitName
					+ ".\nNow you will begin the unit " + currentUnitName
					+ ".");
    	} else {
    		JOptionPane.showMessageDialog(controller.getStudentInterface().getActiveWindow(),
    				"You have finished the unit " + lastUnitName
					+ ".\nYou must now log in again to continue with the next unit.");
    		logout();  // FIXME: see header note
    	}
        return keepGoing;
    }
    
    /**
     * Tell whether the next activity will use the
     * same executable as our own {@link #ctatCarnegieLearningLauncher}.
     * Returns true if {@link #ctatCarnegieLearningLauncher} is null because,
     * I think, in that case our own launcher launched us and
     * CL's launcher was not involved.
     * @param activityStatus current activity status
     * @return true if {@link #ctatCarnegieLearningLauncher} is null
     *         or {@link LMS_Client.Problem_N_Activity_Status#activity_status_d}
     *         is {@link CL_Constants#CONTINUE} 
     */
    private boolean sameExecutable(LMS_Client.Problem_N_Activity_Status activityStatus) {
    	boolean result = false;
        if (ctatCarnegieLearningLauncher == null)
        	result = true;
        else if (activityStatus == null)
        	result = false;
        else
        	result = CL_Constants.CONTINUE.equals(activityStatus.activity_status_d);
        if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "sameExecutable() returning "+result);
        return result;
    }
	
    // //////////////////////////////////////////////////////////////
    /**
     */
    // //////////////////////////////////////////////////////////////
    public void actionPerformed(ActionEvent e) {

        Component c = (Component) e.getSource();
        ListBox listBox = (ListBox) SwingUtilities.getRoot(c);
        listBox.setVisible(false);
        if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "action command = " + e.getActionCommand());
        if (e.getActionCommand().equals("OK")) {
            if (listBox.getTitle().equals("Select a Class"))
                pickStudent(listBox.getSelectedItem());
            if (listBox.getTitle().equals("Select a Student"))
                loginStudent(listBox.getSelectedItem());
        }
    }

    // //////////////////////////////////////////////////////////////
    /**
     */
    // //////////////////////////////////////////////////////////////
    private void pickStudent(String className) {
        if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "class name = " + className);

        for (Iterator i = classList.iterator(); i.hasNext();) {
            CL_Class myClass = (CL_Class) i.next();
            if (myClass.getClassName().equals(className)) {
                clClass = myClass;
                break;
            }
        }

        try {
            studentList = lmsClient.getStudentsForClass(clSchool, clClass);
        } catch (LMS_Exception e) {
            e.printStackTrace(); // To change body of catch statement use
            // Options | File Templates.
            return;
        }

        Vector studentNames = new Vector();
        for (Iterator i = studentList.iterator(); i.hasNext();) {
            CL_Student student = (CL_Student) i.next();
            studentNames.addElement(student.getFirstName() + " "
                    + student.getLastName());
        }

        ListBox dialog = new ListBox("Select a Student",
                "Please select a student from this class", studentNames, this);
        dialog.pack();
        dialog.setLocationRelativeTo(controller.getStudentInterface().getActiveWindow());
        dialog.setVisible(true);

    }

    // //////////////////////////////////////////////////////////////
    /**
     */
    // //////////////////////////////////////////////////////////////
    private void initSkillometer() {

        skillometerManager.reset();
        try {
            clStudentStatus = lmsClient.getCurrentProblemState(clSchool,
                    clStudent, clCurriculum);

            List skills = lmsClient.getSectionSkills(clSchool, clStudent,
                    clCurriculum);

            for (Iterator i = skills.iterator(); i.hasNext();) {
                CL_SectionSkill sectionSkill = (CL_SectionSkill) i.next();
                String skillName = sectionSkill.getSkill().getSkillName();
                // trace.out ("ctat_lms", "initialized skill " + skillName);
                /*
                 * skillometerManager.initializeSkill(skillName,
                 * sectionSkill.getPLearn().floatValue(),
                 * sectionSkill.getInitialPKnown().floatValue(),
                 * sectionSkill.getPGuess().floatValue(),
                 * sectionSkill.getPSlip().floatValue());
                 */
                skillometerManager.initializeSkill(skillName);
            }
            List studentSkills = lmsClient.getStudentSkills(clSchool,
                    clStudent, clCurriculum);
            // trace.out ("ctat_lms", "STUDENT SKILLS = " + studentSkills);

            if (studentSkills != null)
                for (Iterator i = studentSkills.iterator(); i.hasNext();) {
                    CL_StudentSkill studentSkill = (CL_StudentSkill) i.next();
                    String skillName = studentSkill.getSkill().getSkillName();
                    Float value = studentSkill.getPKnown();
                    // trace.out ("ctat_lms", "INIT SKILLOMETER: updating
                    // STUDENT
                    // skill " + skillName + " with initial value " + value);
                    skillometerManager.updateSkill(skillName, value
                            .floatValue());
                }
        } catch (LMS_Exception e) {
            e.printStackTrace(); // To change body of catch statement use
            // Options | File Templates.
        }
        skillometerManager.show();
    }

    // //////////////////////////////////////////////////////////////
    /**
     */
    // //////////////////////////////////////////////////////////////
    private class ListBox extends JDialog {
        private JComboBox cb;

        private String title;

        public ListBox(String title, String labelText, List items,
                ActionListener listener) {
            super();
            setTitle(title);
            this.title = title;

            JLabel label = new JLabel(labelText);
            cb = new JComboBox();
            JButton okButton;
            JButton cancelButton;
            okButton = new JButton("OK");
            cancelButton = new JButton("Cancel");
            JPanel buttonPanel;
            buttonPanel = new JPanel();

            cb.setEditable(false);
            okButton.addActionListener(listener);
            cancelButton.addActionListener(listener);

            for (Iterator i = items.iterator(); i.hasNext();) {
                cb.addItem(i.next());
            }

            getContentPane().setLayout(new GridLayout(3, 0, 5, 5));
            setResizable(false);

            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);

            getContentPane().add(label);
            getContentPane().add(cb);
            getContentPane().add(buttonPanel);
        }

        public String getTitle() {
            return title;
        }

        public String getSelectedItem() {
            return (String) cb.getModel().getSelectedItem();
        }
    }

    public String getCurrentProblemName() {
        return clStudentStatus.getCurrentProblem().toShortString();
    }

    public void setLMSClient(LMS_Client c) throws LMS_Exception {
        this.lmsClient = c;
        if (trace.getDebugCode("ctat_lms")) trace.out("ctat_lms", "lms client = " + c);

        clStudentStatus = lmsClient.getCurrentProblemState(clSchool,
                clStudent, clCurriculum); 
    }

    public void setSchool(CL_School school) {
        trace.out ("ctat_lms", "school = " + school);
        this.clSchool = school;
    }

    public void setCurriculum(CL_Curriculum curriculum) {
        trace.out ("ctat_lms", "curriculum = " + curriculum);
        this.clCurriculum = curriculum;
        loadInterfaceMapFile((String) clCurriculum.getId());

    }

    public void setStudent(CL_Student clStudent) {
        this.clStudent = clStudent;
    }


    /**
     * Causes current thread to block until the problem is advanced.
     * 
     * @return True if there are more problems in the curriculum, false otherwise
     */
    public boolean waitForProblemFinished() {
        try {
            trace.out ("inter", "sleep for 5 sec");
            Thread.sleep (5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @param initialStateFolder The initialStateFolder to set.
     */
    private void setInitialStateFolder(String initialStateFolder) {
        this.initialStateFolder = initialStateFolder;
    }

    /**
     * @return Returns the initialStateFolder.
     */
    private String getInitialStateFolder() {
        if (initialStateFolder == null)
            setInitialStateFolder(getRootRef() + File.separator + "Administrative"
                    + File.separator + "Curricula" + File.separator
                    + "Problem Data" + File.separator + "Initial State"
                    + File.separator);
        
        return initialStateFolder;
    }

    
    /**
     * For use by the CTAT_LMS_Launcher, which is the layer that sits between 
     * the Carnegie Learning launcher and the CTAT launcher
     * 
     * @param launcher
     */
    public void setUseCL_Launcher(CTAT_LMS_Launcher launcher) {
        loggedIn = true;
        this.ctatCarnegieLearningLauncher = launcher;
    }

}
