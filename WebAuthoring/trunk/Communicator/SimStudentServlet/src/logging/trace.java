package logging;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;


/**
 * This class is used for printing debug statements. There are 2 kinds of calls
 * for selecting debug statements to print:
 * <ul>
 * <li>calls whose labels (string tags, such as "sp", "br") choose statements;</li> 
 * <li>numeric level-of-detail calls, where a threshold number chooses statements.</li>
 * </ul>
 * 
 * <p><b>Calls whose labels choose statements</b></p>
 * 
 * These calls are meant to trace named functional areas of the code. They have the form
 * 
 * <tt>if (trace.getDebugCode("functions"))
 *         trace.outln("functions", "evaluating "+getReplacementFormula()+" with s="+s+", a="+a+", i="+i);</tt>
 *
 * To activate them, set the system property DebugCodes to include the tag "functions".
 * You can do this on the command line using the "-D" VM argument. Tags are case-sensitive. 
 * As shown in this example, you can set multiple tags, separated by commas:  
 * 
 * <tt>java -DDebugCodes=functions,br -cp ... edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher</tt>
 * 
 * Here's a partial list of tags and their functional areas in use at this writing (CTATv3.1, 2012-05-31):
 * <ul>
 * <li>br: msgs from the BehaviorRecorder (single broadest area, most common tag);</li>
 * <li>pm: msgs for the ProblemModel package;<li>
 * <li>tsltsp: msgs from Flash;</li>
 * <li>tsltstp: msgs to Flash;</li>
 * <li>ls: LauncherServer;</li>
 * <li>sp: SocketProxy;</li>
 * <li>functions: CTAT_Functions and author-callable matching functions;</li>
 * <li>et: msgs for the Example Tracer;</li>
 * <li>ET: msgs for the Example Tracer;</li>
 * <li>mt: Jess top-level;</li>
 * <li>mtt: more Jess;</li>
 * <li>ps: ProblemSummary;</li>
 * <li>pr: preferences, I think;</li>
 * <li>inter: Java student UI;</li>
 * <li>log: most stuff related to logging, except;</li>
 * <li>logserver: the LogServlet code that acts as an OLI-style logger.</li>
 * <li>ll: stands for Low Level. This will enable very low level class and function based tracing</li>
 * </ul>
 *  
 * <p><b>Numeric level-of-detail calls</b></p>
 * 
 * Each statement such as <tt>
 * 
 * <tt>trace.out (10, this, "pixels per inch = " + ppi);</tt>
 * 
 * will print out the specified string if the general trace level (set by the
 * trace.setTraceLevel() method) is greater or equal to the trace level in the
 * above statement.
 * 
 * The output will look something like: 
 * [timestamp] +trace+ 6: pixels per inch = 96 geometrypad.PadCanvas
 * 
 * The number "6" in this statement shows that this is the 6th trace statement
 * to be printed. geometrypad.PadCanvas is the class which initiated this trace
 * statement.
 * 
 * To turn off ALL trace statements, set the trace level to 0 as in
 * trace.setTraceLevel(0) and leave the DebugCode property empty or null.
 * 
 * For the "standard" level of numeric trace statements, set the trace level to 10.
 * trace.setTraceLevel(10);
 * 
 * To turn on way too many trace statements, set the trace level to a high
 * number of your choosing. trace.setTraceLevel(100);
 *  
 */

public class trace {

	public static final String ALL_CODES = "all";

	protected static int tracelevel = 0;

	/** Stream for non-error messages. */
	protected static PrintStream outStream = System.out;

    /** Stream for error messages. */
    protected static PrintStream errStream = System.err;

    protected static int traceNumber = 0, traceNumber2 = 0;

    /** Codes to activate {@link #out(String, String)} calls. */
    protected static Set<String> debugCodes = new HashSet<String>();

    /** Codes to activate {@link #printStack(String, String)} calls. */
    protected static Set<String> sTraceCodes = new HashSet<String>();
    
    /** Initialize {@link #debugCodes}, {@link #sTraceCodes} from {@link System#getProperties()}. */
    static {
    	try {
    		Properties sysProps = System.getProperties();
    		for(Iterator<Object> it = sysProps.keySet().iterator(); it.hasNext(); ) {
    			Object sysProp = it.next();
    			if(sysProp == null) continue;
    			String s = sysProp.toString().toLowerCase();
    			if(s.startsWith("debugcode"))
    				addDebugCodes(sysProps.getProperty(sysProp.toString()), debugCodes, "debug");
    			else if(s.startsWith("stracecode"))
    				addDebugCodes(sysProps.getProperty(sysProp.toString()), sTraceCodes, "sTrace");
    		}
    	} catch(Exception e)  {  // could be Security Mgr if running as applet
    		outStream.println("Error getting system properties for trace codes: "+e+"; cause "+e.getCause());
    	}
    }

    /**
     * Storage for the last debug code presented to {@link #getDebugCode(String)}, used
     * by {@link #outA(String)} and {@link #outANT(String)}.
     */
	private static ThreadLocal<String> lastGetDebugCode = new ThreadLocal<String>() {
		protected String initialValue() { return "traceA"; }
	};

    /**
     * @param outStream new value for {@link #outStream}
     */
    public static void setOutStream(PrintStream outStream) {
    	trace.outStream = outStream;
    }

    /**
     * @param errStream new value for {@link #errStream}
     */
    public static void setErrStream(PrintStream errStream) {
    	trace.errStream = errStream;
    }

    /////////////////////////////////////////////////////////////////////////
    /**
     * @param tracelevel
     *            New trace level
     */
    /////////////////////////////////////////////////////////////////////////
    public static void setTraceLevel(int _tracelevel) {
        tracelevel = _tracelevel;
        tracelevel = Math.max(0, tracelevel);
        trace.out (0, "trace", "new trace level = " + tracelevel);
    }

    /**
     * Tell whether a given debug code is active.
     * @param  debugCode String to lookup
     * @return true if arg is in {@link #debugCodes} or {@link #sTraceCodes}; false otherwise
     */
    public static boolean getDebugCode(String debugCode) {
    	if (debugCode == null || debugCode.length() < 1)
    		return false;
    	debugCode = debugCode.toLowerCase();
    	boolean result = (debugCodes.contains(debugCode) || sTraceCodes.contains(debugCode));
    	if(result) {
    		lastGetDebugCode.set(debugCode);	
    	}
        return result;
    }
    
    /**
     * Accept a list and call {@link #addDebugCode(String)} for each.
     * @param codeList space- or comma-delimited list of debug codes
     */
    public static void addDebugCodes(String codeList) {
    	addDebugCodes(codeList, debugCodes, "debug");
    }
    
    /**
     * Accept a list and call {@link #addDebugCode(String)} for each.
     * @param codeList space- or comma-delimited list of debug codes
     * @param codeSet set to add these codes to
     * @param codeSetName name of the set
     */
    public static void addDebugCodes(String codeList, Set<String> codeSet, String codeSetName) {
        if (codeList == null)
            return;
        String[] codes = codeList.split("[, ]+");
        for (int i = 0; i < codes.length; ++i)
            addDebugCode(codes[i], codeSet, codeSetName);
    }
    
    public static void addDebugCode(String code) {
    	addDebugCode(code, debugCodes, "debug");
    }

    /**
     * Add this code to the given code set & print a diag.
     * @param code
     * @param codeSet
     * @param codeSetName
     */
    public static void addDebugCode(String code, Set<String> codeSet, String codeSetName) {
    	if(code == null)
    		return;
    	
    	String key = code.toLowerCase();
        
    	if (codeSet.contains(key))
    		return;
    	
    	codeSet.add(key);

        StackTraceElement[] stackTrace = null;
        try {
            throw new Exception();
        } catch (Exception e) {
            stackTrace = e.getStackTrace();
        }

        int count = 0;
        while (stackTrace[count].getClassName().indexOf("trace") != -1)
            count++;

        int lineNum = stackTrace[count].getLineNumber();
        String classname = stackTrace[count].getClassName();

        String traceString;
        traceString = new String("+trace+ Added "+codeSetName+" code "+code+" <"+classname+":"+lineNum+">");

        outStream.println(traceString);
        outStream.flush();
    }

    public static void removeDebugCode(String code) {
    	if(code == null)
    		return;
        debugCodes.remove(code.toLowerCase());
        sTraceCodes.remove(code.toLowerCase());
    }
    
    public static void resetDebugCodes(){
    	debugCodes = new HashSet<String>();
    	sTraceCodes = new HashSet<String>();
    }
    
    /**
	 * Print a stack trace instead of a trace statement.
     * @param  debugCode print only if this debugCode has been entered
     */
    public static void printStack(String debugCode) {
		printStack(debugCode, null);
    }
    
    /**
	 * Print a stack trace with the trace statement. If this debugCode is not in
	 * {@link #sTraceCodes}, then call {@link #out(String, String) out(debugCode, statement)}
	 * instead.
     * @param  debugCode print only if this debugCode has been entered
	 * @param  statement trace label
     */
    public static void printStack(String debugCode, String statement) {
		if (sTraceCodes.size() < 1 ||
				!(sTraceCodes.contains(ALL_CODES)) && 
				!(sTraceCodes.contains(debugCode.toLowerCase()))) {
			out(debugCode, statement);
			return;
		}
        printStackInternal(debugCode, statement);
    }
    
    /**
	 * Print a stack trace with the trace statement to stderr.
	 * @param  statement trace label
	 * @param  e error for which to print stack trace
     */
    public static void errStack(String statement, Throwable e) {
    	err(statement);
    	e.printStackTrace(errStream);
    	outStream.flush();
    }

	private static void printStackInternal(String debugCode, String statement) {
		try {
			traceNumber += 1;
			String traceString = "printStack:\n"+
					new StringBuffer(dateFmt.format(new Date())) +
					" +" + debugCode + "+ " +
					traceNumber + (statement == null ? "" : ": " + statement);
            throw new Exception(traceString);
        } catch (Exception e) {
        	e.printStackTrace(outStream);
        	outStream.flush();
        }
	}

    /////////////////////////////////////////////////////////////////////////
    /**
     * Print a trace statement based on trace level
     * 
     * @param level
     *            Tracelevel for this statement
     * @param callObject
     *            Object calling this trace (use string for null object, this
     *            for non-null object)
     * @param statement
     *            Statement to print
     */
    /////////////////////////////////////////////////////////////////////////
    public static void out(int level, Object callObject, String statement) {
        out(level, callObject.toString(), statement);
    }

    public static void outln(String statement) {
        out(5, "", statement+"\n");
    }
    
    public static void out(String statement) {
        out(5, "", statement);
    }

    public static void out() {
        out("");
    }

    public static void out(int num) {
        out("" + num);
    }

    public static void out(int level, String statement) {
    	out(level, "", statement);
    }
    
    public static void out(int level, String callObject, String statement) {
        out(level, callObject, statement, null);
    }

    public static void outln(String debugCode, String message) {
        out (5, "", message+"\n", debugCode);
    }
    
    public static void out(String debugCode, String message) {
        out (5, "", message, debugCode);
    }
    
    public static void outA(String message) {
        out (-1, "", message, lastGetDebugCode.get());
    }
    
    public static void out(String traceClassName, String debugCode, String message) {
        out (traceClassName, 5, "", message, debugCode);
    }

    public static void outNT(String debugCode, String message) {
        outNT(5, "", message, debugCode);
    }

    public static void outANT(String message) {
        outNT(-1, "", message, lastGetDebugCode.get());
    }

    /////////////////////////////////////////////////////////////////////////
    /**
     * Print a trace statement based on trace level. Do not show caller's line no.
     * This is a lighter-weight trace that doesn't throw an exception to get a
     * stack trace.
     * 
     * @param level
     *            Tracelevel for this statement
     * @param callObject
     *            Object calling this trace (use string for null object, this
     *            for non-null object)
     * @param statement
     *            Statement to print
     */
    /////////////////////////////////////////////////////////////////////////
    /**
     * @param level
     * @param callObject
     * @param statement
     * @param debugCode
     */
    public static void outNT(int level, String callObject, String statement,
            String debugCode) {

        if ((debugCode != null) &&
        		!(debugCodes.contains(ALL_CODES)) &&
        		!(debugCodes.contains(debugCode.toLowerCase())) &&
        		!(debugCode.equalsIgnoreCase ("error"))) 
            return;

        level = Math.max(1, level);

        if (debugCode == null && level > tracelevel) 
            return;

        traceNumber += 1;

        StringBuffer traceString = new StringBuffer(dateFmt.format(new Date()));
        if (debugCode == null)
        	traceString.append(" +trace+ ").append(traceNumber);
        else
            traceString.append(" +").append(debugCode).append("+ ").append(traceNumber);
        traceString.append(": ").append(statement);
            
        outStream.println(traceString);
        outStream.flush();
    }
    
    /** For timestamps. */
    private static final DateFormat dateFmt = new SimpleDateFormat("[HH:mm:ss]");
    
    /**
	 * @return the {@link #datefmt}
	 */
	public static DateFormat getDateFmt() {
		return dateFmt;
	}

	/**
     * Test harness.
     * @param codes each argument a debug code
     */
    public static void main(String[] codes) {
    	for (String code : codes)
    		debugCodes.add(code.toLowerCase());
    	outNT("tsltsp", "here's the request");
    	outNT("tsltstp", "here's the response");
    }
	
    /////////////////////////////////////////////////////////////////////////
    /**
     * Print a trace statement based on trace level
     * 
     * @param level
     *            Tracelevel for this statement
     * @param callObject
     *            Object calling this trace (use string for null object, this
     *            for non-null object)
     * @param statement
     *            Statement to print
     */
    /////////////////////////////////////////////////////////////////////////
    public static void out(int level, String callObject, String statement,
            String debugCode) {
    	out(null, level, callObject, statement, debugCode);
    }
	
    /////////////////////////////////////////////////////////////////////////
    /**
     * Print a trace statement based on trace level
     * @param traceClassName stop scanning stack trace when reach this class name
     * @param level Tracelevel for this statement
     * @param callObject Object calling this trace (use string for null object, this
     *            for non-null object)
     * @param statement Statement to print
     * @param debugCode no-op if this string not among {@link #debugCodes}
     */
    /////////////////////////////////////////////////////////////////////////
    public static void out(String traceClassName, int level, String callObject, String statement,
            String debugCode) {

        if ((debugCode != null) &&
                (level != -1) && 
        		!(debugCodes.contains(ALL_CODES)) &&
        		!(debugCodes.contains(debugCode.toLowerCase()))) 
            return;

        level = Math.max(-1, level);

        if (debugCode == null && level > tracelevel) 
            return;

        traceNumber += 1;

        StackTraceElement[] stackTrace = null;
        try {
            throw new Exception();
        } catch (Exception e) {
            stackTrace = e.getStackTrace();
        }

        int count = 0;
        while (stackTrace[count].getClassName().indexOf("trace") != -1)
            count++;
        while (traceClassName != null
        		&& stackTrace[count].getClassName().indexOf(traceClassName) != -1)
            count++;

        int lineNum = stackTrace[count].getLineNumber();
        String classname = stackTrace[count].getClassName();

        Calendar calendar = Calendar.getInstance();
        StringWriter timeStamp = new StringWriter(); 
        (new PrintWriter(timeStamp)).printf("[%02d:%02d:%02d]", calendar.get(Calendar.HOUR_OF_DAY),
        		calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        
        StringBuffer traceString = new StringBuffer(timeStamp.toString());
        traceString.append(" ");
        if (debugCode == null)
        	traceString.append(level < 0 ? "+err" : "+trace").append("+ " );
        else
            traceString.append("+").append(debugCode).append("+ " );
        traceString.append(traceNumber).append(": ").append(statement);
        traceString.append(" <").append(classname).append(":").append(lineNum).append(">");
        traceString.append(Thread.currentThread().getName());
        
        if (level >= 0) {
        	outStream.println(traceString.toString());
        	outStream.flush();
        } else {
        	errStream.println(traceString.toString());
        	errStream.flush();
        }
    }

    /**
     * @param string
     */
    public static void err(String string) {
        out(-1, "", string);
    }

	public static void printStack() {
		printStackInternal("stack", "print debug stack");
	}

    public static void printStackWithStatement(String statement) {
        printStackInternal ("stack", statement);
    }

	/**
	 * For debugging: to avoid, e.g. the JComponent.toString() mess, use this to dump just "class@hashCode".
	 * @param c instance to dump
	 * @return simple class name + "@" + hashCode
	 */
	public static String nh(Object c) {
		if(c == null)
			return null;
		return c.getClass().getSimpleName()+"@"+c.hashCode();
	}

} //end
