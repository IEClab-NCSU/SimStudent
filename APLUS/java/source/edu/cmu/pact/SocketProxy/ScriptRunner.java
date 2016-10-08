/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.SocketProxy;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.server.UID;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.pact.Utilities.SocketReader;

public class ScriptRunner {

	/**
	 * Listener that waits for the messages that CTAT will send.
	 */
	static class Listener extends Thread {

		/** Socket to read. */
		private final Socket sock;
		/** Client sender. */
		private final Client client;

		Listener(Socket sock, Client client) {
			this.sock = sock;
			this.client = client;
		}

		/**
		 * Stay listening forever until CTAT sends back a message.
		 */
		public void run() {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
				int i = 0;
				while (!isStopping()) {
					System.out.println("\nListener call #"+(++i)+" to readToEom()");
					String msg = SocketReader.readToEom(in, eom);
					if (verbose)
						System.out.println("\nListener received:\n" +msg);
					if (msg.length() < 1)     // empty message => socket closed
						stopClient(client);
					else
						client.nReceived++;
				}
			} catch (Exception e) {
				System.out.println("error from input reader: "+e);
				e.printStackTrace(System.out);
				try { if (in != null) in.close();
				} catch (IOException ioe) { System.out.println("error closing input reader: "+ioe); }
				try { if (sock != null) sock.close();
				} catch (IOException ioe) { System.out.println("error closing socket: "+ioe); }
			}
		}
	}

	/** For replacing an attribute value that needs to be changed to a unique value in each thread. */
	private static Pattern substituteAttributePattern = null;

	/** For replacing element content that needs to be changed to a unique value in each thread. */
	private static Pattern substituteElementPattern = null;

	public String reportLineF;

	/**
	 * Thread client that sands a message to CTATs TutoringService.exe
	 */
	static class Client extends Thread {

		/** Count messages sent. */
		protected int nSent = 0;

		/** Count responses received. */
		protected int nReceived = 0;
		
		/** File of input messages to send, one per line. */
		protected String scriptName;

		private Listener listener;
		private final int clientPort;//Server Port
		private final String host;//localhost
		private Socket outSocket = null;
		private PrintWriter outStream = null;
		
		/** Time to delay between actions, in milliseconds. */
		protected long lineNo = 0;
		protected final int threadNo;
		private String substValue;

		/**
		 * 
		 * @param h
		 * @param p
		 * @param scriptName
		 */
		Client (int threadNo, String h, int p, String scriptName){
			this.threadNo = threadNo;
			clientPort = p;
			host = h;
			this.scriptName = scriptName;
			try {
				init();
			} catch (Exception e) {
				System.out.println("error creating client for host "+h+", port "+p+": "+e);
			};
			if (substituteAttributePattern != null)
				substValue = "S"+(new UID()).toString();  // ensure leading character is alphabetic
		}

		/**
		 * For any initialization before processing the script. This one sets
		 * {@link #outSocket} and {@link #outStream}, creates and starts the {@link #listener}.
		 * @throws Exception
		 */
		protected void init() throws Exception {
			InetAddress addr = InetAddress.getByName(host);
			outSocket = new Socket(addr,clientPort);
			outStream = new PrintWriter(outSocket.getOutputStream(), false);
			listener = new Listener(getOutSocket(), this);
			listener.start(); //sets thread client running
		}

		/**
		 * Method that reads lines from the script file and sends each line as a message in XML format
		 * to the tutoring service. Delays 2 seconds between messages.
		 */
		public void run(){
			try {
				BufferedReader script = new BufferedReader(new FileReader(scriptName));
				if (debug.contains("line"))
					System.err.printf("%3d: +line+ scriptName %s, script %s\n", lineNo, scriptName, script);
				String msg = "teste";
				while (!isStopping() && (msg = script.readLine()) != null) { 
					lineNo++;
					if (msg.length() < 1)
						continue;
					if (debug.contains("line")) System.err.printf("%3d: +line+ %s\n", lineNo, msg);
					if (startChar >= 0) {
						int startCharPos = msg.indexOf(startChar);
						if (startCharPos >= 0)
							msg = msg.substring(startCharPos);
					}
					msg = substituteTagOrAttribute(msg);
					sendMsg(msg);
					delay(delayMs);
				}
				scriptFinished();
			} catch (IOException ioe) {
				System.out.println("Error on file "+scriptName);
				ioe.printStackTrace(System.out);
			}
		}

		/**
		 * Search for an XML element or attribute and change its value to {@link #substValue} using
		 * {@link ScriptRunner#substituteAttributePattern} or {@link ScriptRunner#substituteElementPattern}.
		 * @param msg line to edit
		 * @return revised line
		 */
		protected String substituteTagOrAttribute(String msg) {
			String result = msg;
			if (substituteElementPattern != null)
				result = substitute(substituteElementPattern, msg);
			if (substituteAttributePattern != null && result.equals(msg))
				result = substitute(substituteAttributePattern, msg);
			return result;
		}

		/**
		 * Substitute {@link #substValue} for the 2nd group in a regex that matches
		 * the entire input.
		 * @param substPattern must have 3 capturing groups
		 * @param text line to edit
		 * @return revised text
		 */
		private String substitute(Pattern substPattern, String text) {
			Matcher m = substPattern.matcher(text);
			if (!m.matches())
				return text;         // no substitutions needed
			else
				return m.group(1)+substValue+m.group(3);
		}

		/**
		 * Call this method for any housekeeping needed after processing the script.
		 * @throws IOException
		 */
		protected void scriptFinished() throws IOException {
			if (outStream != null)
				outStream.close();
			if (outSocket != null)
				outSocket.close();
			synchronized(this) {
				notifyAll();
			}
		}

		/**
		 * Pause this thread for the given amount of time.
		 * @param delayMs pause length in milliseconds
		 * @return number of times we received an {@link InterruptedException}
		 */
		protected int delay(long delayMs) {
			if (delayMs < 1)
				return 0;
			int interruptCount = 0;
			long now = System.currentTimeMillis();
			long then = now+delayMs;
			while (now < then) {
				try {
					Thread.sleep(then-now);
					break;      // didn't get interrupted
				} catch (InterruptedException ie) {
					interruptCount++;
					if (isStopping())
						return interruptCount;
					now = System.currentTimeMillis();
				}
			}
			if (debug.contains("delay"))
				System.err.printf("%3d: delayMs(%d) returns %d\n", lineNo, delayMs, interruptCount);
			return interruptCount;
		}

		protected String sendMsg(String msg) {
			outStream.printf("%s\0", msg);   // trailing null byte \0 delimits msg
			outStream.flush();
			nSent++;
			return null;
		}
		
		public void report(PrintStream ps) {
			ps.printf(reportLineFmt, threadNo, nSent, nReceived);
		}

		/**
		 * @return the {@link #outSocket}
		 */
		public synchronized Socket getOutSocket() {
			return outSocket;
		}

		/**
		 * Stop this thread.
		 */
		protected void quit() { 
			interrupt();
			if (listener != null)
				listener.interrupt();
		}
	}

	/**
	 * Sender using HTTP POST messages.
	 */
	static class HttpClient extends Client {
		
		/** Server address. */
		protected final String urlStr;
		
		HttpClient(int threadNo, String urlStr, String scriptName) {
			super(threadNo, null, -1, scriptName);
			this.urlStr = urlStr;
		}

		protected void init() { }
		
		/**
		 * Send a message using HTTP.
		 * @param msg body to send
		 * @see edu.cmu.pact.SocketProxy.ScriptRunner.Client#sendMsg(java.lang.String)
		 */
		protected String sendMsg(String msg) {
			String response = null;        // value to return
			HttpURLConnection conn = null;
			URL url = null;
			try {
				url = new URL(urlStr);
				conn = openConnection(url);
				OutputStream os = conn.getOutputStream();
				os.write(msg.getBytes("UTF-8"));
				if (eom >= 0)
					os.write(eom);
				os.flush();
				nSent++;
				os.close();
			} catch (Exception ex) {
				System.err.println("HttpClient["+threadNo+"].sendMsg() error sending "+
						scriptName+" line #"+lineNo+": "+ex+
						(ex.getCause() == null ? "" : "; cause: "+ex.getCause()));
				ex.printStackTrace();
			}
			int charNo = 0;
			try {
				InputStream is = conn.getInputStream();
				ByteArrayOutputStream readBuf = new ByteArrayOutputStream();
				for (int c = is.read(); c >= 0; charNo++, c = is.read())
					readBuf.write(c);
				response = readBuf.toString("UTF-8");
				if (debug.contains("http"))
					System.err.println("content of "+urlStr+" response;\n  headers "+
							conn.getHeaderFields()+"\n  body: "+
							(response == null ? null : response.trim()));
				if (conn.HTTP_OK == conn.getResponseCode())
					nReceived++;
				is.close();
				if (verbose)
					System.out.printf("line %3d: server response code %d, body \"%s\"\n", lineNo,
							conn.getResponseCode(), (response == null ? null : response.trim()));
				return response;
			} catch (Exception ex) {
				System.err.println("HttpClient["+threadNo+"] error getting response for "+
						scriptName+" line #"+lineNo+": received "+charNo+" bytes"+ex+
						(ex.getCause() == null ? "" : "; cause: "+ex.getCause()));
				ex.printStackTrace();
		        response = null;
			}
		    return response;
		}

		/**
		 * Open a URL for an HTTP connection. Sets method POST, Content-Type text/xml.
		 * Tries {@link URLConnection#connect()}. From edu/cmu/oli/log/client/StreamLogger.java.
		 * @param url
		 * @return connection
		 * @throws IOException
		 */
		private HttpURLConnection openConnection(URL url) throws IOException {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/xml");
			conn.connect();
			return conn;
		}
			
	}
	
	/** For tracing, use VM parameter -Ddebug=http,log,... */
	private static List<String> debug = Arrays.asList(System.getProperty("debug", "").split(",")); 
       
	/** Flag set by other thread to tell listener to quit. */
	private static volatile boolean stopping = false;

	/**
	 * Set the {@link #stopping} flag and interrupt this thread.
	 */
	private static synchronized void stopClient(Client client) {
		stopping = true;
		if (client != null)
			client.quit();
	}

	/**
	 * Get the {@link #stopping} flag value.
	 * @return {@link #stopping}
	 */
	private static boolean isStopping() {
		return stopping;
	}
	
	/** Whether to print messages received. */
	private static boolean verbose = false;
	
	/** Start-of-input character: if defined, omit all chars before this one on input lines. */
	private static int startChar = -1;

	/** End-of-message character. */
	private static int eom = '\0';

	/** Default delay between sends: {@value #DEFAULT_DELAY_MS} */
	private static final long DEFAULT_DELAY_MS = 2000;

	/** Actual delay between sends. */
	private static long delayMs = DEFAULT_DELAY_MS;

	/** Default script file. */
	private static final String defaultScript = "test/ScriptRunner.xml";
	
	/** Host to connect to. */
	private static final String serverHost = "localhost";
	
	/** Default port to which client connects. */
	private static int clientPort = 1502;

	/** Printf format for a report line. */
	private static final String reportLineFmt = "%7d\t%7d\t%7d\n";

	/** Printf format for the report header. */
	private static final String reportHdrFmt = "%-7s\t%-7s\t%-7s\n";
        
        /**
         *creates the Listener that receives the massages from CTAT Behavior Recorder
         *creates the Client that sends a message to CTAT TutoringService.exe
         */
	public static void main(String args[]) {
		String host = serverHost;
		int port = clientPort;
		String url = "http://"+host+":"+port+"/";
		Class clientClass = Client.class;  // default client class
		String scriptName = defaultScript;
		int nThreads = 1;
		boolean printReport = false;
		int i = 0;
		try {
			for (i = 0; i < args.length && args[i].startsWith("-"); ++i) {
				switch (args[i].charAt(1)) {
				case 'c': case 'C':
					if (debug.contains("startChar"))
						System.err.println("+startChar+ -c arg is \""+args[i+1]+"\"");
					int j; for (j=0; (startChar = args[++i].charAt(j)) =='\"' || startChar=='\''; j++);
					if (args[i].length() <= j) startChar = -1; break;
				case 'd': case 'D':
					try { delayMs = Long.parseLong(args[++i]); break; }
					catch (NumberFormatException nfe) { throw new Exception("bad delay ms: "+nfe); }
				case 'e': case 'E':
					try { eom = Integer.parseInt(args[++i], 16); break; }
					catch (NumberFormatException nfe) { throw new Exception("bad hex value for eom: "+nfe); }
				case 'h': case 'H':
					host = args[++i]; break;
				case 'p': case 'P':
					port = Integer.parseInt(args[++i]); break;
				case 'r': case 'R':
					printReport = true; break;
				case 's': case 'S':
					createSubstitutionPatterns(args[++i]); break;
				case 't': case 'T':
					try { nThreads = Integer.parseInt(args[++i]); break; }
					catch (NumberFormatException nfe) { throw new Exception("bad thread count: "+nfe); }
				case 'u': case 'U':
					url = args[++i]; clientClass = HttpClient.class; eom = -1; break;
				case 'v': case 'V':
					verbose = true; break;
				case '?':
					usageExit("Help message."); break;
				default:
					usageExit("Invalid option '"+args[i].charAt(1)+"'."); break;
				}
			}
			if (args.length > i)
				scriptName = args[i++]; 
			File script = new File(scriptName);
			if (!script.canRead() || !script.isFile())
				throw new IOException(scriptName+" does not exist or is not readable");
		} catch (Exception e) {
			usageExit("Error processing command-line arguments: "+e+".");
		}
		promptForInput("Press Enter to start "+nThreads+" thread(s) sending "+(clientClass == HttpClient.class ? "HTTP" : "TCP")+
				" messsages from script "+scriptName+"\nevery "+delayMs+" ms. Press Enter a 2nd time to stop.");
		final Client[] clients = new Client[nThreads];
		for (int t = 0; t < nThreads; t++) {
			clients[t] = (clientClass == HttpClient.class ?
					new HttpClient(t, url, scriptName) : new Client(t, host, port, scriptName));
			clients[t].start();
		}

		Thread awaitStopSignal = new Thread() {
			public void run() {
				promptForInput(null);
				for (Client client : clients)
					stopClient(client);
			}
		};
		awaitStopSignal.start();

		int nIncompleteResponseThreads = 0;
		if (verbose || printReport)
			System.out.printf(reportHdrFmt, "Thread#", "No.Sent", "No.Acks");
		for (Client client : clients) {
			try {
				client.join();
				if (verbose || printReport)
					client.report(System.out);
				if (client.nReceived < client.nSent)
					nIncompleteResponseThreads++;
			} catch (InterruptedException ie) { }
		}
		awaitStopSignal.interrupt();
		System.out.printf("Number of client threads missing responses: %d\n", nIncompleteResponseThreads);
	}

	private static Pattern delimiters = Pattern.compile("([\"=><])");
	private static void createSubstitutionPatterns(String tagOrAttrName) {
		if (tagOrAttrName == null || tagOrAttrName.length() < 1)
			throw new IllegalArgumentException("Missing or null element or attribute for substitution");
		Matcher m = delimiters.matcher(tagOrAttrName);
		if (m.find())
			throw new IllegalArgumentException("Illegal delimiter \""+m.group()+
					"\" in element or attribute for substitution");
		substituteAttributePattern = Pattern.compile("(^.* "+tagOrAttrName+"=\")([^\"][^\"]*)(\".*$)");
		substituteElementPattern = Pattern.compile("(^.*<"+tagOrAttrName+">)([^<][^<]*)(</"+tagOrAttrName+">.*$)");
	}

	/**
	 * Write a prompt to stderr and wait for the user to press Enter.
	 * @param prompt writes this string to prompt the user
	 */
	private static int promptForInput(String prompt) {
		if (prompt != null)
			System.err.println(prompt);
		try {
			while (true) {
				byte[] chars = new byte[10];
				int nChars = System.in.read(chars);
				for (int i = 0; i < nChars; ++i) {
					if (chars[i] == '\r' || chars[i] == '\n') {
						if (debug.contains("input"))
							System.err.printf("promptForInput buffer %s returning '%c'\n", 
									Arrays.asList(chars).toString(), chars[i]);
						return chars[i];
					}
				}
			}
		} catch (Exception e) {
			if (!Thread.interrupted()) {
				System.err.println("Error reading stdin for prompt \"+prompt+\": "+e);
				e.printStackTrace();
			}
		}
		return -1;
	}

	/**
	 * Print a usage message and call {@link System#exit(int) System.exit(2)}
	 * @param errMsg
	 */
	private static void usageExit(String errMsg) {
		if (errMsg != null)
			System.err.printf("%s\n", errMsg);
		System.err.println("Usage:\n"+
				"   java -cp ... "+ScriptRunner.class.getName()+" [-?] [-v] [-r] [-h host] [-p port] [-u url] \\\n"+
				"      [-s substitute] [-c startChar] [-t threads] [-d delay] [-e eom] [script]\n"+
				"where--\n"+
				"   -? means to print this help message;\n"+
				"   -v (verbose) means to print additional information;\n"+
				"   -r means to print a one-line report for each client thread;\n"+
				"   host is the server for a TCP socket connection (default "+serverHost+");\n"+
				"   port is the port for a TCP socket connection (default "+clientPort+");\n"+
				"   url is the logging service URL for an HTTP connection (no default);\n"+
				"   substitute is the name of an element or attribute whose value should be replaced with a value unique\n"+
				"      to each thread (e.g., SessionID); omit delimiters '=', '<', etc. (no default);\n"+
				"   startChar means omit characters prior to this one on input lines; e.g., if input lines have a\n"+
				"      timestamp before the desired XML data, use \"-c <\" to start at the XML left angle bracket;\n"+
				"   threads is the number of message-sending threads to launch (default 1);\n"+
				"   delay is the wait time between messages, in milliseconds (default "+DEFAULT_DELAY_MS+");\n"+
				"   eom is the end-of-message character, entered as 2 hex digits;\n"+
				"      use \"-1\" for no terminator; the default is 0x"+Integer.toHexString(eom)+";\n"+
				"   script is the script file name (default "+defaultScript+").");
		System.exit(2);
	}

	
	protected void scriptFinished() { }
	
}