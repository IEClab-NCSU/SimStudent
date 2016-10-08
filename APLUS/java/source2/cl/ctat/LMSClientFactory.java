/**
 * Copyright 2007 Carnegie Mellon University.
 */
package cl.ctat;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import cl.LMS.client.LMS_Client;
import edu.cmu.pact.Utilities.trace;

/**
 * Create and retain an LMS_Client instance for future use.
 */
public class LMSClientFactory {
	
	/** Default LMS host. */
	private static final String DEFAULT_LMS_HOST = "127.0.0.1";
	
	/** Default LMS TCP port. */
	private static final int DEFAULT_LMS_PORT = 57008;

	/** Property file name. Assumes in current directory. */
	private static final String LAUNCHER_CONFIG = "Launcher.config";

	/** Status to LMS indicating no proxy server on this connection. I think. */
	public static final String NO_PROXY = "NOPROXY";
	
	/** LMS's DNS hostname. */
	private static String lmsHost = DEFAULT_LMS_HOST;
	
	/** LMS's DNS hostname. */
	private static int lmsPort = DEFAULT_LMS_PORT;
	
	/** Last client instance created. */
	private static LMS_Client lastInstance;

	/** Contents of {@link #LAUNCHER_CONFIG} file. */
	private static Properties launcherConfig;

	/**
	 * Return the {@link #lastInstance} if set. Else create a new one and set
	 * {@link #lastInstance}.
	 * @return
	 */
	public static LMS_Client getClient() {
		if (lastInstance != null)
			return lastInstance;
		
		createLauncherConfig();
		
		try {
			LMS_Client lc = LMS_Client.create(lmsHost, lmsPort, launcherConfig);
			lastInstance = lc;
			return lastInstance;
		} catch (Exception e) {
			trace.errStack("Error creating LMS_Client("+lmsHost+","+lmsPort+"): "+e+
					";\n  cause: "+e.getCause(), e);
			return null;
		}
	}

	/**
	 * Read file {@value #LAUNCHER_CONFIG} into {@link #launcherConfig}.
	 */
	private static void createLauncherConfig() {
		launcherConfig = new Properties();
		FileInputStream is = null;
		try {
			File lcf = new File(LAUNCHER_CONFIG);
			is = new FileInputStream(lcf);
			launcherConfig.load(is);
		} catch (Exception e) {
			trace.errStack("Error reading file LMS_Client("+lmsHost+","+lmsPort+"): "+e+
					";\n  cause: "+e.getCause(), e);			
			// leave launcherConfig empty
		} finally {
			try { if (is != null) is.close(); } catch (Exception e) {}
		}
	}
}
