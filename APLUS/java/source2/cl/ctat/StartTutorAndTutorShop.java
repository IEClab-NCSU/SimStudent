/**
 * Copyright 2007 Carnegie Mellon University.
 */
package cl.ctat;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import cl.LMS.client.LMS_Client;
import cl.LMS.exception.LMS_Exception;
import cl.common.CL_Class;
import cl.common.CL_School;
import cl.common.CL_Student;
import cl.launcher.LauncherKeys;
import cl.launcher.StartTutor;
import cl.launcher.XPLauncher;
import cl.utilities.Logging.Logger;
import cl.utilities.basic.PlatformSettings;
import edu.cmu.hcii.utilities.DiagTools;
import edu.cmu.pact.Utilities.ProcessRunner;
import edu.cmu.pact.Utilities.Utils;

/**
 * Substitute for {@link cl.launcher.StartTutor} which launches the Local
 * TutorShop after login and before starting the application session.
 */
public class StartTutorAndTutorShop extends StartTutor {

	/** Distinctive name fragment in holding class, where students are initially enrolled. */
	private static final CharSequence TRANSFER_FROM_CLASS = "tank";

	/** Names of classes student may be transferred into. */
	private final String[] targetClassNames = {"A1", "A2", "G"};
	
	/** Return an instance of this class. */
	public static StartTutor getPlatformInstance() {
	    if ( PlatformSettings.isOSX ) {
	    	System.err.println("OSX not yet supported.");   
	    	System.exit(3);
	    }
		return new StartTutorAndTutorShop();
	}
	
	/**
	 * Empty constructor calls superclass constructor.
	 */
	public StartTutorAndTutorShop() {
		super();
	}
	
	/** Saved student identifier. */
	private String saved_student_login_d = null;
	
	/** Saved student curriculum identifier. */
	private String saved_curriculum_id_d = null;
	
	/** Saved school record. */
	private CL_School saved_school_d = null;
	
	/** Saved student record. */
	private CL_Student saved_student_d = null;

	/** List of classes in the school. */
	private List<CL_Class> classes = null;

	/** Result from {@link #runLocalTutorShop()}: class to enroll in. */
	private String tgtClass = null;

	/** Result from {@link #runLocalTutorShop()}: note for {@link #transferToNewClass(String, String)} */
	private String transferNote = "";
	
	/**
	 * Call the superclass implementation and save the student credentials.
	 */
	protected void doLogin() {
		super.doLogin();
		saved_student_login_d = login_d;
		saved_curriculum_id_d = curriculum_id_d;
		saved_school_d = school_d;
		saved_student_d = student_d;
		Logger.log("launcher", "STATS.doLogin(): saved_student_login_d="+saved_student_login_d+
				", saved_student_d="+saved_student_d+", saved_school_d="+saved_school_d+
				", saved_curriculum_id_d="+saved_curriculum_id_d);
	}

	/**
	 * Launch Local TutorShop unless {@link #alreadyEnrolled(CL_Student) returns true.
	 * In that case, call superclass implementation to proceed with the CL tutor. 
	 * @see cl.launcher.StartTutor#startApplicationSession()
	 */
	protected void startApplicationSession() {
		Logger.log("launcher", "STATS.startApplicationSession(): saved_student_login_d="+saved_student_login_d);
		if (alreadyEnrolled(student_d)) { 
			terminateSession(student_d);   // terminate old session to avoid trouble on new login
			startApplication(saved_student_login_d);
			return;
		}
		
		checkThreads(0, "STATS.startApplicationSession(): to runLocalTutorShop()");

		tgtClass = runLocalTutorShop();         // run the local tutorshop problems

		checkThreads(10000, "STATS.startApplicationSession(): after runLocalTutorShop()+sleep");

		if (tgtClass == null || tgtClass.length() < 1) {
			terminateSession(student_d);
			return;
		}

		transferToNewClass(tgtClass, transferNote);
		terminateSession(student_d);   // terminate old session to avoid trouble on new login

//		System.setProperty(AUTO_LOGIN_NAME, saved_student_login_d);
//		super.doLogin();                         // auto-login again with via system property
//		super.startApplicationSession();                    // run with the new login session
		startApplication(saved_student_login_d);
	}

	private void startApplication(String student_login_name) {
		class PRThread extends Thread {
			String[] cmd = null;
			ProcessRunner runner = null;
			PRThread(String student_login_name) {
				super("PRThread");
				try {
					cmd = createApplicationCmd("-D"+AUTO_LOGIN_NAME+"="+student_login_name);
				} catch (Exception e) {
					e.printStackTrace(System.out);
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error building command",
							JOptionPane.ERROR_MESSAGE);
					System.exit(99);
				}
			}
			public void run() {
				runner = new ProcessRunner();
				String stdout = runner.exec(cmd);
			}
			synchronized boolean isExecDone() {
				return (runner == null ? false : runner.isExecDone());
			}
		}
		PRThread prt = new PRThread(student_login_name);
		synchronized(prt) {
			prt.start();
			for (long ms = 5000, end = System.currentTimeMillis()+ms; ms > 0 && !prt.isExecDone(); ) {
				try {
					prt.wait(ms);
				} catch (InterruptedException ie) {
					ms = end - System.currentTimeMillis();
					Logger.log("launcher", "STATS.startApplication sleep interrupted with "+ms+" ms left");
				}
			}
		}
		Utils.sleep(5000);
		System.exit(0);
	}

	/**
	 * Read the file CognitiveTutor.lax from the current directory and make each of
	 * the lines a command-line parameter. 
	 * @param args extra VM args
	 * @return array of command-line arguments
	 * @throws Exception
	 */
	private String[] createApplicationCmd(String... args) throws Exception {
		List<String> cmd = getLaxProperties("Cognitive Tutor.lax", args);
		return cmd.toArray(new String[cmd.size()]);
	}

	/**
	 * Create command-line arguments from a .lax file:<ul>
	 * <li>convert <i>name</i>=<i>value</i> lines to "-D<i>name</i>=<i>value</i>";</li>
	 * <li>convert <i>name</i> lines to "-D<i>name</i>";</li>
	 * <li>convert "lax.class.path=<i>value</i>" to "-classpath <i>value</i>";</li>
	 * <li>convert "lax.main.class=<i>value</i>" to just "<i>value</i>" and put it last;</li>
	 * <li>convert "lax.nl.java.option.java.heap.size.max=<i>value</i>" to "-Xmx<i>value</i>";
	 * <li>omit other "lax." variables.</li>
	 * </ul>
	 * @param filename parameter file (*.lax) to read
	 * @return array of command-line arguments
	 * @throws Exception if bad file i/o or missing classpath or main class
	 */
	private List<String> getLaxProperties(String filename, String... args) throws Exception {
		Properties lax = new Properties();
		try {
			lax.load(new FileReader(new File(filename)));
		} catch (Exception e) {
			throw new Exception("Error trying to read file \""+filename+"\": "+e, e);
		}
		
		List<String> cmd = new ArrayList<String>();
		String vmPath =	lax.getProperty("lax.nl.current.vm", "java");
		Matcher anySlash = Pattern.compile("[\\\\/]").matcher(vmPath);
		String replacement = anySlash.quoteReplacement(File.separator);
		Logger.log("launcher", "getLaxProperties: vmPath before editing \""+vmPath+"\"; replacement "+replacement);
		vmPath = anySlash.replaceAll(replacement);
		cmd.add(vmPath);    // must be first

		String classpath = null;
		String mainClass = null;
		String maxHeap = null;
		String minHeap = null;
		for (Object key : lax.keySet()) {
			if ("lax.class.path".equals(key.toString()))
				classpath = lax.getProperty(key.toString());
			else if ("lax.main.class".equals(key.toString()))
				mainClass = lax.getProperty(key.toString());
			else if ("lax.nl.java.option.java.heap.size.initial".equals(key.toString()))
				minHeap = lax.getProperty(key.toString());
			else if ("lax.nl.java.option.java.heap.size.max".equals(key.toString()))
				maxHeap = lax.getProperty(key.toString());
			else if (key.toString().startsWith("lax."))
				continue;
			if (lax.get(key) == null || lax.get(key).toString().length() < 1)
				cmd.add("-D"+key);
			else
				cmd.add("-D"+key+"="+lax.getProperty(key.toString()));
		}
		cmd.add("-Xms"+minHeap);
		cmd.add("-Xmx"+maxHeap);

		if (classpath == null)
			throw new IllegalArgumentException("Did not find classpath (\""+classpath+
					"\") in lax file "+filename);
		if (mainClass == null)
			throw new IllegalArgumentException("Did not find main class (\""+mainClass+
					"\") in lax file "+filename);

		for (String arg : args)
			cmd.add(arg);   // insert caller's VM args before main class

		cmd.add("-classpath"); cmd.add(classpath);
		cmd.add(mainClass);  // must be last
		return cmd;
	}

	/**
	 * Debug routine to list active threads, maybe after a delay. If the delay is greater than 0,
	 * will start a separate thread so that the caller doesn't wait.
	 * @param delayLen length of delay in ms; can be 0
	 * @param label prefix for {@link Logger#log(String,String)} entries
	 */
	private void checkThreads(long delayLen, final String label) {
		if (delayLen < 1) {
			Logger.log("launcher", label+"; active threads "+DiagTools.listThreads());
			return;
		}
		class WaitThenGetThreads implements Runnable {
			private long delayLen;
			WaitThenGetThreads(long delayLen) {
				this.delayLen = delayLen;
			}
			public void run() {
				Utils.sleep(delayLen);
				Logger.log("launcher", label+"; active threads "+DiagTools.listThreads());
			}
		}
		(new Thread(new WaitThenGetThreads(delayLen), "WaitThenGetThreads")).start();
	}
	/**
	 * Call {@link LMS_Client#terminateSession(CL_Student)}.
	 * @param student_d
	 */
	private void terminateSession(CL_Student student_d) {
		try {                       
			Logger.log("launcher", "About to lmsC.terminateSession() with stu.sessionID "+
					student_d.getSessionID());
			boolean tr = getLmsClient().terminateSession(student_d);
			Logger.log("launcher", "lmsC.terminateSession() result "+tr+
					", stu.sessionID now "+student_d.getSessionID());
		} catch( Exception e ) {
			e.printStackTrace(System.out);
		}
	}

	/**
	 * Check whether the student is already assigned to one of the target curricula.
	 * Sets {@link #classes}.
	 * @param student_d
	 * @return false if student_d is enrolled in class {@link #TRANSFER_FROM_CLASS}
	 * ("{@value #TRANSFER_FROM_CLASS}")
	 */
	private boolean alreadyEnrolled(CL_Student student_d) {
		if (student_d == null) {
			Logger.log("launcher", "alreadyEnrolled(): student_d null");
			return true;
		}
		try {
			classes = (List<CL_Class>) getLmsClient().getAllClasses(school_d);
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
		if (classes == null) {
			Logger.log("launcher", "alreadyEnrolled("+student_d.getFullName()+"): classes null");
			return true;
		}
		CL_Class txfc = null;  // find transfer-from class
		for (CL_Class c : classes) {
			if(c.getClassName().toLowerCase().contains(TRANSFER_FROM_CLASS)) {
				txfc = c;
				break;
			}
		}
		if (txfc == null) {
			Logger.log("launcher", "alreadyEnrolled(): no class named "+TRANSFER_FROM_CLASS);
			return true;
		}
		List<CL_Student> students = null;
		try {
			students = getLmsClient().getStudentsForClass(school_d, txfc);
		} catch(Exception e) {
			e.printStackTrace(System.out);
		} 
		if (students == null) {
			Logger.log("launcher", "alreadyEnrolled(): "+TRANSFER_FROM_CLASS+" class students null");
			return true;
		}
		Logger.log("launcher", "alreadyEnrolled(): "+TRANSFER_FROM_CLASS+" class students.size() "+students.size());
		for (CL_Student s : students) {
			if (s.getLogin().equals(student_d.getLogin()))
				return false;
		}
		return true;
	}
	
	/**
	 * Run the local tutorshop until it has determined which class to place the student.
	 * Sets {@link #tgtClass}, {@link #transferNote}.
	 * @return target class name
	 */
	private String runLocalTutorShop() {
		Logger.log("launcher", "Starting local TutorShop:");
		long startTime = System.currentTimeMillis();
		Booth2012Bridge jbb = Booth2012Bridge.create();
		jbb.runBridge(saved_student_login_d, "");  // 2nd arg was ../bin/ctat/jb.jar
		synchronized(jbb) {
			int nExceptions = 0;
			while((tgtClass = jbb.getTargetClass()) == null) {
				try {
					jbb.wait();
				} catch(InterruptedException ie) {
					Logger.log("launcher", "STATS.launchLocalTutorShop nExceptions "+nExceptions+": this "+ie);
					ie.printStackTrace(System.out);
				}
			}
			transferNote = jbb.getScoreClasses().toString();
		}
		long endTime = System.currentTimeMillis();
		Logger.log("launcher", "Finished local TutorShop after "+(endTime-startTime)+
				" ms; tgtClass="+tgtClass);	
		jbb.startExiting();                          // stop all the local tutoring service tasks
		return tgtClass;
	}

	/**
	 * Call {@link #getOldAndNewClasses(LMS_Client, String)} to find the {@link CL_Class} instances
	 * for the {@value #TRANSFER_FROM_CLASS} and the given new class. Then call 
	 * {@link LMS_Client#transferStudent(List, CL_Class, CL_Class, cl.common.CL_Instructor, String)}
	 * to move the student.
	 * @param newClassName name of transfer-to class
	 * @param note note argument to transferStudent()
	 * @throws LMS_Exception
	 */
	private void transferToNewClass(String newClassName, String note) {
		LMS_Client lmsc = getLmsClient();
		CL_Class[] oldAndNewClasses = getOldAndNewClasses(lmsc, newClassName);
		CL_Class oldClass = oldAndNewClasses[0], newClass = oldAndNewClasses[1];
		Logger.log("launcher", "STATS.setNewCurriculum() oldClass={"+oldClass+
				"}\n, newClass={"+newClass+"}");
		
		if (oldClass == null || newClass == null)
			return;

		try {
			List<CL_Student> students = new LinkedList<CL_Student>();			
			students.add(student_d);
			int n = lmsc.transferStudent(students, oldClass, newClass, newClass.getInstructor(), note);
			Logger.log("launcher", "STATS.setNewCurriculum() transfer("+students+", "+oldClass.getClassName()+
					", "+newClass.getClassName()+", "+newClass.getInstructor()+", \""+note+"\") returns "+n);
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}

//      Older method: addStudentsToClass(), then deleteStudentsFromClass(), or vice versa      
//		
//		CL_Class.State clClass = new CL_Class.State();
//		clClass.addStudent(student_d);
//		boolean removed = lmsc.deleteStudentsFromClass(oldClass, clClass);
//		Logger.log("launcher", "STATS.setNewCurriculum() delete("+oldClass.getClassName()+
//				", "+clClass+") returns "+removed);
//
//		CL_Student.State clStudent = new CL_Student.State();
//		clStudent.setFirstName(student_d.getFirstName());
//		clStudent.setLastName(student_d.getLastName());
//		clStudent.setLogin(student_d.getLogin());
//		List<CL_Student.State> students = new LinkedList<CL_Student.State>();			
//		students.add(clStudent);
//		List<CL_Student.State> added = lmsc.addStudentsToClass(newClass, students);
//		Logger.log("launcher", "STATS.setNewCurriculum() add("+newClass.getClassName()+
//				", "+student_d+") returns "+added);
	}

	/**
	 * Find the {@link CL_Class} instances for the {@value #TRANSFER_FROM_CLASS} and the given new class.
	 * @param lmsc
	 * @param newClassName
	 * @return array with old class in element 0, new class in element 1
	 */
	private CL_Class[] getOldAndNewClasses(LMS_Client lmsc, String newClassName) {
		Logger.log("launcher", "STATS.getOldAndNewClasses(LMS_Client, "+newClassName+")");
		CL_Class[] result = new CL_Class[2];
		if (classes == null)
			return result;
		Iterator<CL_Class> it = classes.iterator();
		for(int nFound = 0; it.hasNext() && nFound < result.length;) {
			CL_Class c = it.next();
			Logger.log("launcher", "STATS.getOldAndNewClasses() class "+c);
			if(c.getClassName().toLowerCase().contains(TRANSFER_FROM_CLASS)) {
				result[0] = c;
				nFound++;
				continue;
			}
			if(c.getClassName().contains(newClassName)) {
				result[1] = c;
				nFound++;
				continue;
			}
		}
		return result;
	}

	/**
	 * @param args unused
	 */
	public static void main(String[] args) {

        show_splash_screen( LauncherKeys.getString( LauncherKeys.TUTOR_SPLASH_SCREEN ));

        XPLauncher launcher = getPlatformInstance();
        // no args expected.

        if( launcher == null )
        {
            System.exit( 1 );
        }

        try
        {
            launcher.startup( LauncherKeys.getString( LauncherKeys.WELCOME_TUTOR ) );
        }
        catch( Exception e )
        {
            //  uh oh some bad runtime thing happened.
            //  kill things and then die

            Logger.log( "Unexpected exception at the LAUNCHER TOP LEVEL" );
            Logger.log( e );

            if( launcher != null )
            {
                ((StartTutor) launcher).fatalError( "Unexpected exception at the LAUNCHER TOP LEVEL: " +
                                     e.toString(), true );

                launcher.shutDownAll();
            }
            // which will exit java
            System.exit( 1 );
        }
    }
}
