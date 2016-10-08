/**
 * Copyright 2007 Carnegie Mellon University.
 */
package cl.ctat;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import cl.LMS.LMS_Constants;
import cl.LMS.client.LMS_Client;
import cl.LMS.client.LMS_Client.Student_N_Curricula;
import cl.LMS.client.LMS_Client.VersionStatusInfo;
import cl.LMS.exception.LMS_InvalidLogin;
import cl.LMS.exception.LMS_NoEligibleCurriculum;
import cl.LMS.exception.LMS_StudentLockException;
import cl.common.CL_School;
import cl.common.CL_Student;
import cl.common.PlatformType;
import edu.cmu.hcii.login.LoginResult;
import edu.cmu.hcii.login.LoginService;
import edu.cmu.pact.Utilities.trace;

/**
 * @author sewall
 *
 */
public class LMSLoginService implements LoginService {

	/**
	 * Abstraction of student or user record.
	 */
	class Student implements LoginService.User {
		
		/** Underlying CL record. */
		final CL_Student delegate;
		
		/** Time of creation. */
		final Date timeCreated;
		
		/**
		 * @param delegate value for {@link #delegate}
		 */
		Student(CL_Student delegate) {
			this.delegate = delegate;
			timeCreated = new Date();
		}
		
		/**
		 * @return {@link #delegate}.{@link CL_Student#getLogin()}
		 * @see cl.ctat.LoginService.User#getUserid()
		 */
		public String getUserid() {
			return delegate.getLogin();
		}
	}

	/** Carnegie Learning version, found to be of the form "yyyy.mm.dd". */
	private static String clientVersion = "2010.05.05";

	/** Use this value for {@link #schoolId} if no other available. */
	private static final String DEFAULT_SCHOOL_ID = "Local";

	/**
	 * Curriculum identifier for
	 * {@link LMS_Client#loginStudent(CL_School, String, String, String, boolean)}
	 */
	private static final String DEFAULT_CURRICULUM_ID = "Algebra I";

	/** School identifier for LMS. */
	private String schoolId;

	/** School information from LMS. */
	private CL_School school;
	
	/** Interface to LMS. */
	private final LMS_Client lmsClient;

	/**
	 * Student records from latest login: key is userid, value is LoginService.Student
	 * instance holding the CL_Student record.
	 */
	private Map<String, Student> students =
			Collections.synchronizedMap(new HashMap<String, Student>());

	/** Properties from Launcher.config */
	private Properties props;

	/**
	 * Same as {@link #LMSLoginService(String) LMSLoginService(null)}.
	 */
	public LMSLoginService() {
		this(null);
	}

	/**
	 * Set the school identifier {@link #schoolId} from local data. 
	 * @param schoolId uses {@value #DEFAULT_SCHOOL_ID} if null or empty
	 */
	public LMSLoginService(String schoolId) {
		this.schoolId =
				(schoolId != null && schoolId.length() > 0 ? schoolId : DEFAULT_SCHOOL_ID);
		if (trace.getDebugCode("lms"))
			trace.out("lms", "LMSLoginService("+schoolId+") to get lmsClient");
		lmsClient = LMSClientFactory.getClient();
		if (trace.getDebugCode("lms"))
			trace.out("lms", "LMSLoginService("+schoolId+") lmsClient.isNetworkClient() "+lmsClient.isNetworkClient());
		props = gather_interesting_properties();
	}

	/**
	 * Modeled on XPLauncher.gather_interesting_properties(), with properties chosen
	 * as seen in clients for CL version 2010.05.05.
	 * @return properties
	 */
	private Properties gather_interesting_properties()
	{
		Properties props_to_lms = new Properties();
		// we're already sending platform over; somehow directory and version go, too
		props_to_lms.setProperty("proxyType", LMSClientFactory.NO_PROXY);
		props_to_lms.setProperty("java.version", System.getProperty( "java.version" ));
		props_to_lms.setProperty("client_time", new Date().toString());
		props_to_lms.setProperty("os.version", System.getProperty( "os.version" ));
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		props_to_lms.setProperty("screen_size", ("W="+d.width+",H="+d.height) );
		props_to_lms.setProperty("screen_resolution", Integer.toString( Toolkit.getDefaultToolkit().getScreenResolution()) );
		return props_to_lms;
	}
	
	/**
	 * @param userid
	 * @param password
	 * @param specificReply
	 * @return
	 * @see cl.ctat.LoginService#login(java.lang.String, java.lang.String, java.lang.String[])
	 */
	public LoginResult login(String userid, String password,
			String[] specificReply) {

		String[] mySpecReply =
				(specificReply != null && specificReply.length > 0 ? specificReply : new String[1]);
		LoginResult result = null;

		result = checkApplicationVersion(mySpecReply);
		if (trace.getDebugCode("lms"))
			trace.out("lms", "checkApplicationVersion() returns "+result+": "+mySpecReply[0]);
		if (LoginResult.Success != result)
			return result;

		result = getSchoolPreferences(mySpecReply);
		if (LoginResult.Success != result)
			return result;

		result = loginStudent(userid, password, mySpecReply);
		return result;
	}

	/**
	 * End a student session.
	 * @param userid identifies the student to logout
	 * @param specificReply
	 * @return 
	 */
	public LoginResult logout(String userid, String[] specificReply) {
		String[] mySpecReply =
				(specificReply != null && specificReply.length > 0 ? specificReply : new String[1]);
		Student student = students.remove(userid);
		CL_Student clStudent = null;
		if (student != null)
			clStudent = student.delegate;
		else {  // this might not work
			try {
				clStudent = CL_Student.create(userid, null);
			} catch (Exception e) {
				trace.errStack("logout(): Error creating student record for \""+userid+"\"", e);
				mySpecReply[0] = "Error retrieving local student record for terminateSession(): "+e;
				return LoginResult.ClientErrorNoRetry;
			}
		}

		try {
			boolean result = lmsClient.terminateSession(student.delegate);
			mySpecReply[0] = "Result from session logout: "+result;
			return LoginResult.Success;
		} catch (Exception e) {			
			trace.errStack("Error from terminateSession("+student+")", e);
			mySpecReply[0] = "Error from terminateSession(): "+e;
			return LoginResult.ServerErrorNoRetry;
		}
	}

	/**
	 * Call {@link LMS_Client#loginStudent(CL_School, String, String, String, boolean)}
	 * @param userid
	 * @param password
	 * @param mySpecReply
	 * @return
	 */
	private LoginResult loginStudent(String userid, String password,
			String[] mySpecReply) {
		Student_N_Curricula snc = null;
		try {
			snc = lmsClient.loginStudent(school, userid, password,
					DEFAULT_CURRICULUM_ID, false);  // false: don't check student lock
			if (snc != null && snc.student != null) {
				Student student = new Student(snc.student);
				students.put(userid, student);
				mySpecReply[0] = "Welcome, "+student.delegate.getFirstName()+"!";
				return LoginResult.Success;
			} else {
				mySpecReply[0] = "No student record returned from server.";
				return LoginResult.InvalidUserid;
			}
		} catch (LMS_NoEligibleCurriculum nece) {
			mySpecReply[0] = nece.getMessage();
			return LoginResult.DisabledUserid;
		} catch (LMS_InvalidLogin ile) {
			mySpecReply[0] = ile.getMessage();
			return LoginResult.InvalidUserid;
		} catch (LMS_StudentLockException sle) {
			mySpecReply[0] = sle.getMessage();
			return LoginResult.AlreadyLoggedIn;
		} catch (Exception e) {			
			trace.errStack("Error from loginStudent("+schoolId+","+userid+","+password+")", e);
			mySpecReply[0] = "Error from loginStudent(): "+e;
			return LoginResult.ServerErrorNoRetry;
		}
	}

	/**
	 * Call {@link LMS_Client#getSchoolPreferences(String)}.
	 * @param mySpecReply writes school name to entry [0]
	 * @return {@link LoginResult#Success} if retrieved,
	 * 		   else {@link LoginResult#ServerErrorNoRetry} 
	 */
	private LoginResult getSchoolPreferences(String[] mySpecReply) {
		if (trace.getDebugCode("lms"))
			trace.out("lms", "to call getSchoolPreferences("+schoolId+"): lmsClient "+lmsClient);
		try {
			school = lmsClient.getSchoolPreferences(schoolId);
			if (trace.getDebugCode("lms"))
				trace.out("lms", "getSchoolPreferences() reply: "+school);
			mySpecReply[0] = school.getSchoolName();
			return LoginResult.Success;
		} catch (Exception e) {
			trace.errStack("Error from checkApplicationVersion("+schoolId+")", e);
			mySpecReply[0] = "Error from checkApplicationVersion(): "+e;
			return LoginResult.ServerErrorNoRetry;
		}
	}

	/**
	 * Call {@link LMS_Client#checkApplicationVersion(PlatformType, String, String, boolean, java.util.Properties)}
	 * @param mySpecReply writes error message to entry [0]
	 * @return {@link LoginResult#Success} if remote returns {@value LMS_Constants#LMS_OK};
	 *         else {@link LoginResult#ServerErrorNoRetry} with message
	 */
	private LoginResult checkApplicationVersion(String[] mySpecReply) {
		if (trace.getDebugCode("lms"))
			trace.out("lms", "to call checkApplicationVersion(): lmsClient "+lmsClient);
		if (lmsClient == null) {
			mySpecReply[0] = "null lmsClient";
			return LoginResult.ServerErrorRetry;
		}
		PlatformType platform = PlatformType.getCurrentPlatform();
		String applicationDir = System.getProperty("user.dir");
		try {
			VersionStatusInfo vsi = lmsClient.checkApplicationVersion(platform, applicationDir,
					clientVersion, true, props);
			mySpecReply[0] = "";
			if (trace.getDebugCode("lms"))
				trace.out("lms", "checkApplictionVersion() reply: "+
						(vsi == null ? vsi : "status "+vsi.status_d+
								", errorMessage "+vsi.errorMessage_d+", URL "+vsi.URL_d));
			if (vsi == null || vsi.errorMessage_d == null || vsi.errorMessage_d.length() < 1)
				return LoginResult.Success;
			mySpecReply[0] = "Error from checkApplicationVersion(): "+vsi.errorMessage_d;
			return LoginResult.ServerErrorNoRetry;
		} catch (Exception e) {
			trace.errStack("Error from checkApplicationVersion("+platform+","+applicationDir+
					","+clientVersion+",true,properties...)", e);
			mySpecReply[0] = "Error from checkApplicationVersion(): "+e;
			return LoginResult.ServerErrorNoRetry;
		}
	}

	/**
	 * @param args see {@link #usageExit(String)}. 
	 */
	public static void main(String[] args) {
		String schoolId = DEFAULT_SCHOOL_ID;
		boolean usePwds = false;
		int i = 0; 
		if (args.length <= i)
			usageExit("Userid argument missing.");
		for (; i < args.length && args[i].startsWith("-"); ++i) {
			if (args[i].length() < 2)
				usageExit("Bad switch argument \""+args[i]+"\".");
			switch(args[i].charAt(1)) {
			case 'h': case 'H':
				usageExit("Help message:"); break;
			case 's': case 'S':
				if (i >= args.length-1)
					usageExit("Missing school id on -s.");
				schoolId = args[++i]; break;
			case 'p': case 'P':
				usePwds = true; break;
			default:
				usageExit("Unknown switch argument \""+args[i]+"\".");
			}
		}
		LMSLoginService ls = new LMSLoginService(schoolId);
		String[] specReply = new String[1];
		for (; i < args.length; ++i) {
			String userid = args[i], password = null;
			if (usePwds) {
				String[] pair = args[i].split(",", 2);
				userid = pair[0];
				if (pair.length > 1)
					password = pair[1];
			}
			LoginResult result = ls.login(userid, password, specReply);
			System.out.printf("login(%-12s, %-12s) =>\n  %s, %s: \"%s\"\n", 
					userid, password, result, result.getUserText(), specReply[0]);
			if (result == LoginResult.Success || result == LoginResult.AlreadyLoggedIn) {
				try { Thread.sleep(2000); } catch (InterruptedException ie) {} 
				result = ls.logout(userid, specReply);
				System.out.printf("logout() =>\n  %s, %s: \"%s\"\n", 
						result, result.getUserText(), specReply[0]);
			}
		}
	}

	/**
	 * Print a usage message to stderr and exit with status 2.
	 * @param errMsg optional error message
	 * @return never returns
	 */
	private static void usageExit(String errMsg) {
		Class<LMSLoginService> cls = LMSLoginService.class;
		if (errMsg != null)
			System.err.printf("%s ", errMsg);
		System.err.println("Usage:\n"+
				"   java -cp ... "+cls.getName()+" [-s schoolId] [-p] userid[,password] ...\n"+
				"where--\n"+
				"   schoolId is a school identifier (default "+DEFAULT_SCHOOL_ID+");\n"+
				"   -p means following arguments are userid,password pairs;\n"+
				"   userid is a student name to test;\n"+
				"   password is the optional password for that userid.\n");
		System.exit(2);
	}

}
