/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.cmu.pact.Utilities.trace;

/**
 * Send requests to a tutor engine running behind an HTTP daemon. Use this class to test
 * the JavaScript-based example tracer running under node.js.
 */
public class HttpToolEmulator {

	/** Default address of the tutor engine. */
	static final String DEFAULT_URL = "http://localhost:8888/sendToTutor";
	
	/** My address for the tutor engine. */
	private final String url;

	/**
	 * Equivalent to {@link #HttpToolEmulator(String)} with argument {@value #DEFAULT_URL}.
	 */
	public HttpToolEmulator() {
		this(DEFAULT_URL); 
	}

	/**
	 * @param url value for {@link #url}
	 */
	public HttpToolEmulator(String url) {
		this.url = (url == null ? DEFAULT_URL : url); 
	}

	/**
	 * See {@link #usageExit(String, Throwable)} for arguments.
	 * @param args with no arguments uses {@value #DEFAULT_URL}
	 */
	public static void main(String[] args) {
		String url = DEFAULT_URL;
		int i = 0;
		try {
			for(i = 0;  i < args.length && '-' == args[i].charAt(0); ++i) {
				char c = args[i].charAt(1);
				switch(Character.toLowerCase(c)) {
				case 'u':
					url = args[++i]; break;
				default:
					throw new IllegalArgumentException("Command-line option '-"+c+"' undefined");
				}
			}
		} catch(Exception e) {
			usageExit("Error processing argument["+i+"]", e);
		}
		HttpToolEmulator hte = new HttpToolEmulator(url); 
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			String line = null;
			try {
				line = br.readLine();
				if(line == null)
					return;
				String reply = hte.sendAndWait(line);
				System.out.printf("Response, length %d:\n%s\n", (reply == null ? -1 : reply.length()), reply);
			} catch(Exception e) {
				System.err.printf("Error %s (cause %s) on this line:\n  %s\n", e, e.getCause(), line);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Send a string via HTTP POST and await the response.
	 * @param msg POST content
	 * @return content of response
	 * @throws Exception
	 */
	public String sendAndWait(String msg) throws Exception{
		if(trace.getDebugCode("http"))
			trace.out("http", "send "+msg);
		URL addr = new URL(new URL("http://"), url);
		java.net.HttpURLConnection conn = (HttpURLConnection) addr.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.getOutputStream().write(msg.getBytes("UTF-8")); 
		conn.getOutputStream().close();
		conn.connect();
		if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new Exception("Error from HTTP : response code "+conn.getResponseCode()+
					" \""+conn.getResponseMessage()+"\" after this line:\n  "+msg);
		}
		int i = 0;
		String thisLine;
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			for(++i; (thisLine = reader.readLine()) != null; ++i)
				sb.append(thisLine).append('\n');
			if(trace.getDebugCode("http"))
				trace.out("http", "recv "+sb);
			return sb.toString();
		} catch(Exception e) {
			Exception ex = new Exception("Error reading response at line["+i+"]: "+e+"; line:\n  "+msg);
			ex.setStackTrace(e.getStackTrace());
			throw ex;
		} finally {
			if(reader != null)
				reader.close();
		}
	}

	/**
	 * Print a usage message and exit. Never returns.
	 * @param errMsg optional error message
	 * @param e optional exception; will print stack backtrace
	 */
	private static void usageExit(String errMsg, Throwable e) {
		if(e != null)
			e.printStackTrace(System.err);
		System.err.printf("\n%s%sUsage:\n"+
				"    java -cp ... %s [-u url]\n"+
				"where--\n"+
				"    url is the tutor engine address; default %s.\n",
				(errMsg == null ? "" : errMsg),
				(errMsg == null ? "" : (e == null ? ". " : ": "+e+".")),
				HttpToolEmulator.class.getName(),
				DEFAULT_URL);
		System.exit(2);
	}

}
