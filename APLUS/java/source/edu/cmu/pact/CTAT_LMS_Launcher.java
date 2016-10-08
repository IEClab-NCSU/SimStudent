/*
 * Created on Nov 21, 2005
 *
 */
package edu.cmu.pact;

import java.util.List;

import cl.LMS.client.LMS_Client;
import cl.LMS.exception.LMS_Exception;
import cl.common.CL_Curriculum;
import cl.common.CL_Instructor;
import cl.common.CL_School;
import cl.common.CL_Student;
import cl.launcher.LMSable;
import cl.launcher.XPLauncher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.Controller.LMS.CTAT_LMS;
import edu.cmu.pact.Utilities.trace;

/**
 * This file provides a translation layer between the Carnegie Learning 
 * Launcher and the CTAT launcher.
 * 
 * It is used as the entry point to CTAT tutors from within the Carnegie Learning
 * launcher and curriculum.
 * 
 * @author mpschnei
 *
 * Created on: Dec 7, 2005
 */
public class CTAT_LMS_Launcher implements LMSable {

    private CL_Student clStudent;
 
    private BR_Controller controller;

    private boolean finished;

    private CTAT_LMS ctatLMS;

    public CTAT_LMS_Launcher () {
        trace.addDebugCode("testlms");
        trace.addDebugCode("inter");
        trace.addDebugCode("ctat_lms");
        trace.setTraceLevel(10);
        trace.out ("constructor");
        
        SingleSessionLauncher ctatLauncher = new SingleSessionLauncher();
        controller = ctatLauncher.getController();

        this.ctatLMS = (CTAT_LMS) controller.getCTAT_LMS();
        ((CTAT_LMS) controller.getCTAT_LMS()).setUseCL_Launcher(this);
    }
    
    /**
     * This method is called by the Carnegie Learning Launcher to start this tutor.
     */
    public void run() {

        try {
            boolean newProblemFound = true;
            trace.out ("lms" , "run tutor now");
    
//            while (newProblemFound) {
                ctatLMS.loadInterfaceAndProblem();
//                newProblemFound = ctatLMS.waitForProblemFinished();
//            }
//            setIsFinished(true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setStudent(CL_Student student) {
        trace.out("lms", "student = " + student);
        this.clStudent = student;
        ctatLMS.setStudent(clStudent);
    }

    public void setSchool(CL_School school) {
        ctatLMS.setSchool (school);
    }

    public void setActiveCurricula(List curricula) {
    	if (curricula == null || curricula.size() < 1)
    		return;
        ctatLMS.setCurriculum((CL_Curriculum) curricula.get(0));
    }

    public void setInactiveCurricula(List curricula) {
    	trace.out("lms", "called setInactiveCurricula("+curricula+")");
    }
    
    public void setInstructor(CL_Instructor instructor) {
    }

    public void setLMSClient(LMS_Client c) {
        try {
            ctatLMS.setLMSClient(c);
        } catch (LMS_Exception e) {
            e.printStackTrace();
        }
    }

    public void setPort(int port_to_tutor) {
    }

    public boolean isFinished() {
        return finished;
    }

    public void setIsFinished(boolean f) {
        this.finished = f;
    }

    public void setVersionString(String version) {
    }
    
    public void setLauncher(XPLauncher launcher) {
    	trace.out("lms", "called setLauncher("+launcher+")");
    }

}
