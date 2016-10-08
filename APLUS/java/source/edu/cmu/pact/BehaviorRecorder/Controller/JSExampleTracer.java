/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import edu.cmu.hcii.utilities.HttpToolEmulator;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.ProcessRunner;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

/**
 * Authoring tools interface to the JavaScript-based example tracer.
 */
public class JSExampleTracer {
	
	/** Default URL. */
	static final String DEFAULT_URL = HttpToolEmulator.DEFAULT_URL;
	
	/** XML prologue for messages. */
	private static String prologue = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	/** Closing tag of a message: part of {@link #messageDelimiter}. */
	private static final String messageClose = "</message>";

	/** Opening tag of a message: part of {@link #messageDelimiter}. */
	private static final String messageOpen = "<message>";
	
	/**
	 * Delimiter between messages: positive lookbehind (?<=) which will split on
	 * every zero-length string that is preceded by "<message>".
	 */
	private static Pattern messageDelimiter = Pattern.compile(messageClose+"\\s*"+messageOpen);

	/** The external process running JavaScript. */
	private ProcessRunner jsProcess = null;

	/** HTTP client for communicating to the external process. */
	private final HttpToolEmulator hte;
	
	/** To count instantiations for debugging. */
	private static int instanceNo = 0;

	/**
	 * Start the child process. Instantiate the HTTP client object.
	 * @param url address for {@link HttpToolEmulator#HttpToolEmulator(String)}
	 */
	JSExampleTracer(String url) {
		this(url, true);
	}	
	
	/**
	 * Optionally start the child process. Instantiate the HTTP client object.
	 * @param url address for {@link HttpToolEmulator#HttpToolEmulator(String)}
	 * @param startChild true if you want the constructor to start the child process
	 */
	JSExampleTracer(String url, boolean startChild) {
		++instanceNo;
		if(trace.getDebugCode("js"))
			trace.printStack("js", "instance["+instanceNo+"] JSExampleTracer("+url+","+startChild+")");
		if(startChild) {
			StringBuffer firstOutput = new StringBuffer();
			jsProcess = new ProcessRunner(trace.getDebugCode("js"));
			jsProcess.exec(new String[] {"nodejs", "../HTML5/src/Node/server.js"}, false, firstOutput);
			for(long now = System.currentTimeMillis(), then = now+20000, begin = now;
					now < then;
					now = System.currentTimeMillis()) {
				if(trace.getDebugCode("js"))
					trace.out("js", "JSExampleTracer waiting up to "+(then-now)+" ms for child process to start");
				try {
					Thread.sleep(then-now);  // wait for child process to bind its server port
				} catch(Exception ie) {
					trace.err(String.format("Exception after %d ms; firstOutput \"%s\": %s; cause %s",
							System.currentTimeMillis()-begin, firstOutput, ie, ie.getCause()));
					if(firstOutput.length() > 0)
						break;
				}
			}
		}
		hte = new HttpToolEmulator(url);
	}

	/**
	 * Send a message to the HTTP interface and split the responses into individual messages.
	 * @param req message to send
	 * @return response split by {@link #messageDelimiter} and parsed into {@link MessageObject}s
	 */
	List<MessageObject> getExampleTracerResponses(MessageObject req) {
		List<MessageObject> result = new ArrayList<MessageObject>();
		int i = -1;  // index initialized to "before 1st response"
		try {
			String response = hte.sendAndWait(prologue+req.toString());
			String[] msgTexts = messageDelimiter.split(response);
			for(i = 0; i < msgTexts.length; ++i) {
				String msgText = msgTexts[i];
				if(i > 0)
					msgText = messageOpen+msgText;  // restore delimiters
				if(i < msgTexts.length-1)
					msgText = msgText+messageClose;
				if(trace.getDebugCode("js"))
					trace.out("js", String.format("response[%2d] of %2d: %s",
							i, msgTexts.length, msgText));
				if(msgText.length() < messageDelimiter.pattern().length())
					continue;
				MessageObject msg = MessageObject.parse(msgText);
				result.add(msg);
			}
		} catch(Exception e) {
			trace.errStack("Error on response["+i+"]: "+e+"; cause "+e.getCause()+
					";\n  request was "+req, e);
		}
		return result;
	}


	public List<MessageObject> openBRD(String problemFileLocation) {
        URL url = Utils.getURL(problemFileLocation, this);
        trace.out("js", "openBRD("+problemFileLocation+") url = " + url);
        MessageObject setPrefs = MessageObject.create(MsgType.SET_PREFERENCES);
        String path = url.getPath();
        int colon = path.indexOf(':');
        if(colon >= 0)
        	path = path.substring(colon+1);
        setPrefs.addPropertyElement("question_file", "http://localhost:8888/"+path);
        setPrefs.setProperty(Logger.PROBLEM_NAME_PROPERTY, path);
		return getExampleTracerResponses(setPrefs);
	}

	/**
	 * Kill the {@link #jsProcess}, waiting up to 2000 ms for it to die.
	 * No-op if {@link #jsProcess} is null; returns -1 in that case.
	 * @return exit status from {@link ProcessRunner#kill(long)}
	 */
	public int killChild() {
		if(jsProcess == null)
			return -1;
		long now = System.currentTimeMillis();
		int result = jsProcess.kill(2000);
		if(trace.getDebugCode("js"))
			trace.out("js", String.format("jsProcess.kill() returns exit status %d after %d ms",
					result, System.currentTimeMillis()-now));
		return result;
	}
}
