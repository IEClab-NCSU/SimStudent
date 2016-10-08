/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.trace;

/**
 * @author sewall
 *
 */
public class MergeFromSetPrefs {
	
	/** UTC hour adjustment. */
	private int UTC_HH_Adjustment = 4;  // 4 hrs for EDT
	
	/** Base date for timestamps. */
	private int yyyy, mm, dd;
	
	/** Format for local time zone date parsing and printing. */
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/** Format for given time zone date parsing and printing. */
	private static DateFormat dfz = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
	
	/**
	 * Convert a time in HH:MM:SS format to a Date.
	 * Shifts by {@link #UTC_HH_Adjustment}. Adds 24 hrs. to times between 0000 and 0700 to
	 * give monotonic time, since traces all restarted at 0700 UTC.
	 * @param time
	 * @return
	 */
	Date convToAdjUTCsecs(String time) {
		try {
			String[] hhmmss = time.split(":");
			int HH = Integer.parseInt(hhmmss[0]);
			int MM = Integer.parseInt(hhmmss[1]);
			int SS = Integer.parseInt(hhmmss[2]);
			if (HH < 0 || 23 < HH || MM < 0 || 59 < MM || SS < 0 || 60 < SS)
				throw new Exception("HH "+HH+" or MM "+MM+" or SS "+SS+" out of range");
			int dd2 = (HH < 3 ? dd+1 : dd);  // traces restart at 3 am daily
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.printf("%04d-%02d-%02d %02d:%02d:%02d", yyyy, mm, dd2, HH, MM, SS);
			return df.parse(sw.toString());
		} catch (Exception e) {
			throw new RuntimeException("Bad time: date "+yyyy+"-"+mm+"-"+dd+", time "+time+"; "+e);
		}
	}
	
	/** Timestamp for running a brd file. */
	private class BrdTimestamp {
		private final Date timestamp;  // secs since midnight UTC; 0000-0700 adjusted to next day
		private final String brdFilename;    // question_file value
		BrdTimestamp(String time, String brdFilename) {
			timestamp = convToAdjUTCsecs(time);
			this.brdFilename = brdFilename;
		}
		public BrdTimestamp(String brdFilename, Date timestamp) {
			this.timestamp = timestamp;
			this.brdFilename = brdFilename;
		}
		public String toString() {
			return dfz.format(timestamp)+", "+brdFilename;
		}
	}
	
	/** Problem name &lt;problemName&gt; and number &lt;problem_name&gt;. */
	private class ProblemName {
		private final LinkedList<BrdTimestamp> brdTimestamps;
		private final String problemName;
		private String brdName0 = null;   // if set, single brdName 
		ProblemName(String brdName, String problemName, String time) {
			if (brdName == null || brdName.length() < 1)
				throw new IllegalArgumentException("brdName empty for problem_name "+problemName);
			brdTimestamps = new LinkedList<BrdTimestamp>();
			brdTimestamps.add(new BrdTimestamp(time, brdName));
			this.brdName0 = brdName;
			this.problemName = problemName;
		}
		public String toString() {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.printf("%23s, %s", problemName, brdName0);
			for (BrdTimestamp bt : brdTimestamps)
				pw.printf("\n %s", bt);
			return sw.toString();
		}
		void add(String brdName, String time) {
			if (!brdName.equals(brdName0))
				brdName0 = null;   // means there are different brds for this problemName 
			Date timestamp = convToAdjUTCsecs(time);
			ListIterator<BrdTimestamp> it = brdTimestamps.listIterator();
			while (it.hasNext()) {
				BrdTimestamp bt = it.next();
				if (bt.timestamp.compareTo(timestamp) > 0) {
					it.previous();
					break;
				}
			}
			it.add(new BrdTimestamp(brdName,timestamp));
		}
		String getBrdFilename(Date timestamp) {
			if (brdName0 != null)
				return brdName0;
			Iterator<BrdTimestamp> it = brdTimestamps.descendingIterator();
			while (it.hasNext()) {
				BrdTimestamp bt = it.next();
				if (bt.timestamp.compareTo(timestamp) <= 0)
					return bt.brdFilename;
			}
			throw new RuntimeException("no trace timestamp before "+timestamp+" for problem "+
					problemName);
		}
	}
	
	/** Holds the data we need from a SetPreferences msg. */
	private class SessionProblems {
		private final String sessionId;
		private Map<String, ProblemName> brdNames = null;
		SessionProblems(String sessionId, String brdName, String problemName, String time)
				throws RuntimeException {
			if (sessionId == null | sessionId.length() < 1)
				throw new IllegalArgumentException("sessionId null or empty on session");
			this.sessionId = sessionId;
			this.brdNames = new LinkedHashMap<String, ProblemName>();
			addProblem(brdName, problemName, time);
		}
		public String toString() {
			StringBuffer sb = new StringBuffer(sessionId);
			sb.append(" [");
			for (String pn : brdNames.keySet())
				sb.append("\n ").append(brdNames.get(pn));
			sb.append(" ]");
			return sb.toString();
		}		
		/**
		 * Add a problem to the list {@link #brdNames}, except don't add if same as the last.
		 * @param brdName problem to add
		 * @param problemName entry in problem_name, really the sequence no. w/in the problem set
		 * @param time local time HH:MM:SS of handleSetPreferences trace 
		 * @return number of problems in list now
		 */
		int addProblem(String brdName, String problemName, String time) {
			if (brdName == null | brdName.length() < 1)
				throw new IllegalArgumentException("brdName null or empty on session "+sessionId);
			if (problemName == null | problemName.length() < 1)
				throw new IllegalArgumentException("problemName null or empty on session "+sessionId);
			ProblemName opn = brdNames.get(problemName);
			if (opn == null)
				brdNames.put(problemName, new ProblemName(brdName, problemName, time));
			else
				opn.add(brdName, time);
			return brdNames.size();
		}
		
		String getBrdFilename(String problemName, Date timestamp) {
			ProblemName pn = brdNames.get(problemName);
			if (pn == null)
				throw new IllegalArgumentException("no problem found for session "+sessionId+
						", problem "+problemName);
			return pn.getBrdFilename(timestamp);
		}
	}
	
	/** Elements created from SetPreferences msgs. */
	private static Map<String, SessionProblems> sessionsTbl = new TreeMap<String, SessionProblems>();
	
	/** Initialize fields. */
	public MergeFromSetPrefs(String yyyymmdd) {
		yyyy = Integer.parseInt(yyyymmdd.substring(0,4));
		mm = Integer.parseInt(yyyymmdd.substring(4,6));
		dd = Integer.parseInt(yyyymmdd.substring(6,8));
	}
	
	/**
	 * Populate {@link #sessionsTbl}.
	 * @param spFile XML file of SetPreferences msgs
	 * @return number of records read
	 */
	private int buildSetPrefsTbl(File inFile) throws Exception	{
		SAXBuilder saxb = new SAXBuilder();
		Document doc = saxb.build(inFile);
		StringBuffer errs = new StringBuffer();
		int childNo = 0;
		for (Object child : doc.getRootElement().getChildren()) {
			Element msg = (Element) child;
			childNo++;
			if (! "msg".equalsIgnoreCase(msg.getName())) {
				errs.append("Element ").append(childNo).append(" not a <msg>: ").append(msg.getName()).append("\n");
				continue;
			}
			String sessionId = msg.getChildText(Logger.SESSION_ID_PROPERTY);
			String brdName = msg.getChildText("question_file");
			String problemName = msg.getChildText("problem_name");
			String time = msg.getChildText("time");
			// if (!isBrdWanted(brdName))
			//	continue;
			SessionProblems oldSp = sessionsTbl.get(sessionId);
			if (oldSp != null)
				oldSp.addProblem(brdName, problemName, time);
			else
				sessionsTbl.put(sessionId, new SessionProblems(sessionId, brdName, problemName, time));
		}
		if (errs.length() > 0)
			throw new RuntimeException(errs.toString());
		for (String sess : sessionsTbl.keySet())
			trace.outNT("log", sessionsTbl.get(sess).toString()+"\n");
		return childNo;
	}
	
	/**
	 * Filter for data; given a path, tell whether a BRD filename is wanted.
	 * @param brdName
	 * @return true if passes the filter; false if unwanted
	 */
	private static boolean isBrdWanted(String brdName) {
		if (brdName == null | brdName.length() < 1)
			throw new IllegalArgumentException("isBrdWanted(): brdName null or empty");
		return brdName.contains("/FractionStudy")
			|| brdName.contains("/MartinaFract")
			|| brdName.contains("/Tests");
	}

	/**
	 * @param args filenames to process
	 */
	public static void main(String[] args) {
		int nErrs = 0;
		for (String filename : args) {
			try {
				int yyyyPos = filename.indexOf("2009"); 
				String yyyymmdd = filename.substring(yyyyPos, yyyyPos+8);
				MergeFromSetPrefs m = new MergeFromSetPrefs(yyyymmdd);
				int nSetPrefs = m.buildSetPrefsTbl(new File(filename));
				trace.outln("log", filename+"\t"+nSetPrefs);
			} catch (Exception e) {
				nErrs++;
				trace.err("Error on "+filename+": "+e+"; "+
						(e.getCause() == null ? "" : e.getCause()));
				e.printStackTrace();
			}
		}
		if (nErrs > 0)
			System.exit(nErrs);
		
		StreamTokenizer lineTkzr =
			new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));
		lineTkzr.wordChars('\0','\u00FF');
		lineTkzr.whitespaceChars('\n','\n');
		lineTkzr.whitespaceChars('\r','\r');
		try {
			PrintStream writer = new PrintStream(new FileOutputStream(new File("out.txt")));
			if (lineTkzr.TT_EOF != lineTkzr.nextToken())
				convertHeader(lineTkzr.sval, writer);
			for (int lineNo = 2; lineTkzr.TT_EOF != lineTkzr.nextToken(); ++lineNo)
				convertTransaction(lineNo, lineTkzr.sval, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final int ANON_STUDENT_ID2_COLUMN = 2;
	private static final int SESSION_COLUMN = 3;
	private static final int TIMESTAMP_COLUMN = 4;
	private static final int TZ_COLUMN = 5;
	private static final int UNIT_COLUMN = 10;
	private static final int SECTION_COLUMN = 11;
	private static final int PROBLEM_NAME_COLUMN = 12;
	private static final int ALL_REMAINING_COLUMN = 13;
	private static final int N_COLUMNS = ALL_REMAINING_COLUMN+1;

	/**
	 * Revise the Level(Unit) and Level(Section) column headers with a single
	 * Level(ProblemSet).
	 * @param hdrLine
	 * @param writer
	 * @return
	 */
	private static boolean convertHeader(String hdrLine, PrintStream writer) {
		String[] hdrs = hdrLine.split("\t", N_COLUMNS);
		if (hdrs == null)
			return false;
		for (int i = 0; i < hdrs.length-1; ++i) {
			if (i == UNIT_COLUMN)
				writer.append("Level(ProblemSet)");
			else if (i == SECTION_COLUMN)
				continue;
			else
				writer.append(hdrs[i]);
			writer.write('\t');
		}
		writer.append(hdrs[hdrs.length-1]);
		writer.append("\r\n");
		return true;
	}

	/**
	 * In a single exported transaction, replace the Unit and Section values
	 * with a single ProblemSet value.
	 * @param lineNo
	 * @param transLine
	 * @param writer
	 * @return true if converted successfully
	 */
	private static boolean convertTransaction(int lineNo, String transLine, PrintStream writer) {
		String[] fields = transLine.split("\t", N_COLUMNS);
		if (fields == null || fields.length < N_COLUMNS) {
			trace.err("line "+lineNo+": too few fields:\n  "+transLine+"\n;");
			return false;
		}
		String sessionId = fields[SESSION_COLUMN]; 
		String problemName = fields[PROBLEM_NAME_COLUMN]; 
		String timeAndZone = fields[TIMESTAMP_COLUMN] + " " + fields[TZ_COLUMN];
		Date timestamp = null;
		String brdFilename = null;
		String problemSet = null;
		try {
			timestamp = dfz.parse(timeAndZone);
			brdFilename = lookupBrdName(sessionId, problemName, timestamp);
			if (brdFilename != null) {
				String[] path = brdFilename.split("/");
				if (path.length > 3)
					problemSet = path[3];
			}
		} catch (ParseException pe) {
			trace.err("line "+lineNo+": error parsing timestamp \""+timestamp+"\": "+pe);
		} catch (RuntimeException re) {
			trace.err("line "+lineNo+": "+re);
		}
		if (problemSet == null) {
			trace.err("line "+lineNo+" skipped; session "+sessionId+", problemName "+problemName+
					", "+dfz.format(timestamp)+", brd "+brdFilename);
			return false;
		}
		for (int i = 0; i < fields.length-1; ++i) {
			if (i == UNIT_COLUMN)
				writer.append(problemSet);
			else if (i == SECTION_COLUMN)
				continue;
			else
				writer.append(fields[i].length() < 1 ? " " : fields[i]);
			writer.write('\t');
		}
		writer.append(fields[fields.length-1]);
		writer.append("\r\n");
		return true;
	}

	/**
	 * Find a BRD filename for a given (session, problem_name, time). Reads {@link #sessionsTbl}.
	 * @param sessionId
	 * @param problemName
	 * @param timestamp
	 * @return
	 */
	private static String lookupBrdName(String sessionId, String problemName, Date timestamp) {
		SessionProblems sp = sessionsTbl.get(sessionId);
		if (sp == null)
			throw new RuntimeException("session not found in traces: "+sessionId);
		String brdFilename = sp.getBrdFilename(problemName, timestamp);
		return brdFilename;
	}
}
