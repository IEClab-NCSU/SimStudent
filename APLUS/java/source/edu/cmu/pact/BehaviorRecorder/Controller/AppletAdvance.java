/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.applet.Applet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JApplet;

import pact.CommWidgets.StudentInterfaceWrapper;

import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * @author sewall
 *
 */
public class AppletAdvance implements ActionListener {
	
	/** Request next page via this appletLauncher. */
	private final AppletLauncher appletLauncher;

	/**
	 * @param appletLauncher value for {@link #appletLauncher}
	 */
	public AppletAdvance(AppletLauncher appletLauncher) {
		this.appletLauncher = appletLauncher;
	}

	/**
	 * Called when the student has completed the problem.
	 * @param e
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if(trace.getDebugCode("appletLauncher"))
			trace.outNT("appletLauncher", "AppletAdvance.actionPerformed("+e+":"+e.getSource()+")");
		if(StudentInterfaceWrapper.COMPLETE_ALL_ITEMS.equalsIgnoreCase(e.getActionCommand())) {
			Runnable psTransaction = new Runnable() {
				public void run() {
					sendProblemSummary();
					String path = null;
					try {
						path = appletLauncher.getParameter(AppletLauncher.RUN_PROBLEM_URL_PARAM);
						URL rpURL = makeCodeBaseURL(path);
						if(trace.getDebugCode("appletLauncher")) {
							trace.outNT("appletLauncher", "AppletAdvance pausing, rpURL "+rpURL);
							Thread.sleep(30*1000);
						}
//						String targetFrame = appletLauncher.getParameter(AppletLauncher.TARGET_FRAME);
						String targetFrame = "_self";     // must be _self for now
						appletLauncher.getAppletContext().showDocument(rpURL, targetFrame);
					} catch(Exception e) {
						Utils.showExceptionOccuredDialog(e, "Error advancing to next problem: "+e,
								"Error advancing to next problem");
					}
				}
			};
			(new Thread(psTransaction, "ProblemSummaryTransaction")).start();
		}
		
	}

	/**
	 * Create and send the problem summary to the TutorShop.
	 */
	private String sendProblemSummary() {
		String postContent = createProblemSummaryPost();
		if(trace.getDebugCode("appletLauncher"))
			trace.outNT("appletLauncher", "AppletAdvance.sendProblemSummary() postContent\n  "+postContent);
		StringWriter result = new StringWriter();
		String path = appletLauncher.getParameter(AppletLauncher.CURRICULUM_SERVICE_URL_PARAM);
		HttpURLConnection csConn = null;
		
		try {
			URL csURL = makeCodeBaseURL(path);
//			setCookieUsingCookieHandler(csURL);
			if(trace.getDebugCode("appletLauncher"))
				trace.outNT("appletLauncher", "AppletAdvance.sendProblemSummary() csURL "+csURL);
			csConn = (HttpURLConnection) csURL.openConnection();
			csConn.setDoOutput(true);
			csConn.setDoInput(true);
			csConn.setRequestMethod("POST");
			csConn.connect();
		} catch(Exception e) {
			trace.errStack("Error connecting to address "+path+": "+e+"; cause "+e.getCause(), e);
			return null;
		}
		try {
			BufferedOutputStream os = new BufferedOutputStream(csConn.getOutputStream());
			os.write(postContent.getBytes("UTF-8"));
			os.close();
			BufferedReader is = new BufferedReader(new InputStreamReader(csConn.getInputStream(), "UTF-8"));
			for(int c; 0 <= (c = is.read()); result.write(c));
			is.close();
			if(trace.getDebugCode("appletLauncher"))
				trace.outNT("appletLauncher", "AppletAdvance.sendProblemSummary() response "+result);
			if(csConn.HTTP_OK != csConn.getResponseCode())
				throw new Exception("Error from ProblemSummary response "+csConn.getResponseCode()+": "+result);
			return result.toString();
		} catch(Exception e) {
			trace.errStack("Error sending postContent to address "+path+": "+e+"; cause "+e.getCause(), e);
			return null;
		}
	}

	/**
	 * Assemble a URL from the {@link URL#getProtocol()} and {@link URL#getAuthority()}
	 * components of {@link #appletLauncher}'s {@link Applet#getCodeBase()}.
	 * @param path path relative to code base
	 * @return URL with given path;
	 *         returns path unchanged if it already begins with the protocol and authority
	 */
	private URL makeCodeBaseURL(String path) throws Exception {
		URL codeBase = null;
		URL result = null;
		try {
			try {
				result = new URL(path);
			} catch(MalformedURLException mue) {
				codeBase = appletLauncher.getCodeBase();
				String protocolAuthority = codeBase.getProtocol()+"://"+codeBase.getAuthority();
				result = new URL(protocolAuthority+"/"+path);
			}
			if(trace.getDebugCode("applet"))
				trace.outNT("applet", "makeCodeBaseURL("+path+") returning "+result);
			return result;
		} catch(Exception e) {
			throw new Exception("Error creating URL from codeBase "+codeBase+", path "+path+": "+e, e);
		}
	}

	/** Names of parameters to send. */
	private static Set<String> summaryParams = new LinkedHashSet<String>();
	static {
		summaryParams.add(Logger.SESSION_ID_PROPERTY);
		summaryParams.add(AppletLauncher.AUTHENTICITY_TOKEN_PARAM);
		summaryParams.add(Logger.SCHOOL_NAME_PROPERTY);
		summaryParams.add(Logger.STUDENT_NAME_PROPERTY);	
		summaryParams.add("problem_state");
	}

	/**
	 * Assemble the body of the problem summary request.
	 * @return String of the form "name=value&name=value&..."
	 */
	private String createProblemSummaryPost() {
		String ps = appletLauncher.getProblemSummary();
		if(trace.getDebugCode("appletLauncher"))
			trace.outNT("appletLauncher", "AppletAdvance.createProblemSummaryPost() ps "+ps);
		Map<String, String[]> params = new HashMap<String, String[]>();
		{
			String[] paramInfo = { "ProblemSummary", AppletLauncher.ENCODE, ps };
			params.put(paramInfo[0], paramInfo);
		}
		for(String[] pInfo : appletLauncher.getParameterInfo()) {
			if(!summaryParams.contains(pInfo[0]))
				continue;
			String[] paramInfo = { pInfo[0], pInfo[1], appletLauncher.getParameter(pInfo[0]) };
			params.put(paramInfo[0], paramInfo);
		}
		StringBuilder sb = new StringBuilder();
		for(String key : params.keySet()) {
			String[] paramInfo = params.get(key);
			sb.append(nameValue(key, paramInfo[1], paramInfo[2]));
		}
		return sb.toString();
	}

	/**
	 * Format a name,value pair for a POST response.
	 * @param pName key
	 * @param pType type, used for encoding
	 * @param pvalue value
	 * @return
	 */
	private String nameValue(String pName, String pType, String pValue) {
		StringBuffer sb = new StringBuffer();
		sb.append(pName).append('=');
		try {
			if(pValue == null)
				;   // sb.append("")
			else if(pType.contains(AppletLauncher.ENCODE))
				sb.append(URLEncoder.encode(pValue, "UTF-8"));
			else
				sb.append(pValue);
		} catch(UnsupportedEncodingException uee) {
			trace.errStack("should not happen with UTF-8", uee);
		}
		sb.append('&');
		return sb.toString();
	}
	
	private void setCookieUsingCookieHandler(URL csURL) {
	    try {
	        // instantiate CookieManager
	        CookieManager manager = new CookieManager();
	        CookieHandler.setDefault(manager);
	        CookieStore cookieJar =  manager.getCookieStore();

	        URI csURI = csURL.toURI();

	        // create cookies
	        String rawCookieStr = appletLauncher.getDocumentCookies();
	        String[] nvPairs = rawCookieStr.split(";");
	        for(String nvPair : nvPairs) {
	        	String[] nv = nvPair.split("=");
	        	String name = ((nv == null || nv.length < 1) ? null : nv[0]);
	        	String value = (nv.length > 1 ? nv[1] : null);
	        	if(trace.getDebugCode("appletLauncher"))
		        	trace.outNT("appletLauncher", "setCookie("+name+", "+value+")");
	        	if(name == null)
	        		continue;
	        	HttpCookie cookie = new HttpCookie(name, value);
		        cookieJar.add(csURI, cookie);
	        }
	    } catch(Exception e) {
	        trace.errStack("Error setting cookies: "+e+"; cause "+e.getCause(), e);
	    }
	}
}
