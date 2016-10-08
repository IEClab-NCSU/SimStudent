package cl.utilities.Logging;

import java.util.Date;

import edu.cmu.pact.Utilities.trace;

/**
 * A substitute for the same class in Carnegie Learning's utilities.jar.
 * This simply calls methods in CTAT's trace class.
 * @author sewall
 */
public class Logger {

	/** Other classes should only read this value. */
	public static boolean LoggingOn = true;

	/**
	 * Tell the tracer to output log entries with the given code.
	 * @param debugCode argument to {@link trace#addDebugCode(String)}
	 * @param unused
	 */
	public static void addLoggerProperty(String debugCode, String unused) {
		trace.addDebugCode(debugCode);
	}
	
	/**
	 * Call {@link trace#out(String)} or {@link trace#err(String)}.
	 * @param debugCode 1st argument to {@link trace#out(String, String)}
	 * @param data if an instance of {@link Throwable}, use {@link trace#errStack(String, Throwable)}
	 */
	public static void log(String debugCode, Object data) {
		if (data instanceof Throwable)
			trace.errStack(debugCode, (Throwable) data);
		else
			trace.out("Logger", debugCode, data == null ? "null" : data.toString());
	}

	/**
	 * Call {@link trace#out(String)} or {@link trace#err(String)}.
	 * @param data
	 */
	public static void log(Object data) {
		if (data instanceof Throwable)
			trace.errStack(data.toString(), (Throwable) data);
		else {
			if(trace.getDebugCode("cl"))
				trace.out("cl", data == null ? "null" : data.toString());
		}
	}

	/**
	 * No-op.
	 * @param logname
	 */
    public static void setLogName( String logname ) {}

    /**
     * No-op.
     */
    public static void shutdown() {}

    /**
     * A bunch of System properties.
     */
    public static void print_java_stuff(){
        trace.err( "Java Version  : " + System.getProperty( "java.version" ) );
        trace.err( "os name       : " + System.getProperty( "os.name" ) );
        trace.err( "Memory        : \ttotal  : " + Runtime.getRuntime().totalMemory() +
                    "\t free  :  " + Runtime.getRuntime().freeMemory() );
        trace.err( "java.compiler : " + System.getProperty( "java.compiler" ) );
        trace.err( "Temp dir      : " + System.getProperty( "java.io.tmpdir" ) );
        trace.err( "User dir      : " + System.getProperty( "user.dir" ) );
        trace.err( "User home     : " + System.getProperty( "user.home" ) );
		trace.err( "USE HTTP : " + System.getProperty( "USE_HTTP_TUNNEL" ));
		trace.err( "USE GZIP COMPRESSION : " + System.getProperty( "USE_GZIP_COMPRESSION" ));
    }
    
    /**
     * Same as {@link #log(String, Object) log(msg_type, threadname)}
     * @param start unused
     * @param threadname data for {@link #log(String, Object)}
     * @param msg_type debugCode for {@link #log(String, Object)}
     */
    public static void log( Date start, String threadname, String msg_type ) {}
	
	/**
	 * @return {@link #LoggingOn}
	 */
	public static boolean isLoggingOn() {
		return LoggingOn ;
	}

	/**
	 * @param ignored
	 * @return {@link #isLoggingOn()}
	 */
	public static boolean isLoggingOn(String ignored) {
		return isLoggingOn();
	}

	/**
	 * @param loggingOn new value for {@link #LoggingOn}
	 */
	public static void setLoggingOn(boolean loggingOn) {
		LoggingOn = loggingOn;
		if (loggingOn)
			trace.addDebugCode("cl");
		else
			trace.removeDebugCode("cl");
	}
}
