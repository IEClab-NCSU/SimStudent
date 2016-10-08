package edu.cmu.pact.miss;

/*
 * Created on May 30, 2005
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This is a sample template file to use when creating a new test case.
 * 
 */
public class SimStudentValidationTest extends TestCase {

    // private String QAdir =
    // "f:/Project/CTAT/CVS-TREE/QA/Tests/TestTutors/Projects";
    private String QAdir = "../../QA/Tests/TestTutors/Projects";

    private String projectDir = QAdir + "/SimStEquation";

    private String shScript = projectDir + "/simStSmokeTest.sh";

    private String logFile = projectDir + "/simStSmokeTest.log";

    private String bashExe = "c:/usrLocal/bin/bash.exe";

    public void testSimStudentValidation() throws IOException,
            InterruptedException {

        System.out.println(new File(".").getAbsolutePath());

        // Process p =
        // Runtime.getRuntime().exec("..\\..\\QA\\Tests\\TestTutors\\Projects\\SimStEquation\\simStSmokeTest.sh");
        // Process p =
        // Runtime.getRuntime().exec("..\\..\\QA\\Tests\\TestTutors\\Projects\\SimStEquation\\runBR2.bat");
        // Process p = Runtime.getRuntime().exec("..\\java\\runBR2.bat");
        // String cmd[] = new String[2];
        // cmd[1] = shScript;
        // cmd[0] = bashExe;
        // Process p = Runtime.getRuntime().exec( cmd );
        // p.waitFor();

        /*
        Process smokeTestProcess = null;
        try {
            String[] bash = { bashExe, "--" };
            smokeTestProcess = Runtime.getRuntime().exec(bash);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fos = null;
        FileInputStream fis = null;
        StreamGobbler outputGobbler = null;
        StreamGobbler inputGobbler = null;
        try {
            fos = new FileOutputStream(logFile);
            outputGobbler = new StreamGobbler(
                    smokeTestProcess.getInputStream(), fos, true);

            fis = new FileInputStream(shScript);
            inputGobbler = new StreamGobbler(fis, smokeTestProcess
                    .getOutputStream());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        */

        // Fri Oct  6 09:51:37 LDT 2006 :: Noboru
        // This test is currently disabled.
        // This test must be converted into true junit tests
        // Also, we plan to make a Marathon test for SimSt 
        
//        System.out.println("running...");
//        inputGobbler.start();
//        outputGobbler.start();
//        outputGobbler.waitForComplition();
//        System.out.println("done!");
//
//        smokeTestProcess.waitFor();
//        assertEquals(0, smokeTestProcess.exitValue());

    }

    protected void setUp() {
    }

    public static Test suite() {
        // Any void method that starts with "test"
        // will be run automatically using this construct
        return new TestSuite(SimStudentValidationTest.class);
    }

    // Redirect input and ouput for an external process
    // http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
    //  
    class StreamGobbler extends Thread {

        InputStream is;

        String type;

        OutputStream os;

        boolean notifyWhenDone = false;

        boolean completed = false;

        StreamGobbler(InputStream is, OutputStream redirect) {
            this(is, redirect, false);
        }

        StreamGobbler(InputStream is, OutputStream redirect,
                boolean notifyWhenDone) {
            this.is = is;
            this.os = redirect;
            this.notifyWhenDone = notifyWhenDone;
        }

        public void waitForComplition() {
            while (!completed) {
                ;
            }
        }

        public void run() {

            try {

                PrintWriter pw = null;
                if (os != null)
                    pw = new PrintWriter(os);

                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                String line = null;
                while ((line = br.readLine()) != null) {
                    line += "\n";
                    if (pw != null) {
                        pw.print(line);
                        pw.flush();
                    }
                    /*
                     * if ( notifyWhenDone ) { System.out.println(">> " + line); }
                     */
                }

                if (pw != null) {
                    pw.flush();
                    pw.close();
                }

                completed = true;

                /*
                 * if ( notifyWhenDone ) { notifyAll(); System.out.println("I'm
                 * done!!"); }
                 */

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public void tearDown() {
    	bashExe = null;
    	logFile = null;
    	projectDir = null;
    	QAdir = null;
    	shScript = null;
    }
}
