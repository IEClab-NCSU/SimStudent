package edu.cmu.pact.JavascriptBridge;

import java.applet.Applet;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import netscape.javascript.JSObject;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.TutoringService.TSLauncherServer;
import edu.cmu.pact.TutoringService.TransactionInfo.Single;
import edu.cmu.pact.Utilities.NtpClient;
import edu.cmu.pact.Utilities.trace;

public class JSBridge extends Applet {
	
	private static final long serialVersionUID = -3215783702142896523L;
	
	private static URL codeBaseURL;
	
	private static final String[] DEFAULT_LAUNCHER_ARGUMENTS = {
		"-"+SingleSessionLauncher.USE_JS_BRIDGE,
		"-"+SingleSessionLauncher.DEBUG_CODES, "applet" //,
//		"-debugCodes", "tsltsp",
//		"-debugCodes", "tsltstp",
//		"-debugCodes", "mt",
//		"-debugCodes", "mtt",
//		"-debugCodes", "ls",
//		"-debugCodes", "util"
	};

	private JSProxy jsProxy;
	private JSObject window;
	private TSLauncherServer ls = null;
	
	public static URL getCodeBaseURL() {
		return codeBaseURL;
	}
	
	public JSBridge() {
		super();
		System.out.printf("[%s] +applet+ JSBridge constructor hashCode %d\n",
				trace.getDateFmt().format(new Date()), hashCode());
	}
	
	public void init() {
		codeBaseURL = this.getCodeBase();
		ls = new TSLauncherServer() {
			public boolean removeSession(String guid) { return false; }

			public void updateTimeStamp(String guid) {}

			public Single createTransactionInfo(String sessionId) { return null; }

			public void updateTransactionInfo(String sessionId, Object info) {}

			public NtpClient getNtpClient() { return null; }
		};
	}

	/**
	 * Called when the applet is no longer on the screen, as when the end user goes to another web page.
	 */
	public void stop() {
		if(trace.getDebugCode("applet"))
			trace.out("applet", "JSBridge.stop(): calling jsProxy.quitActionHandler(); jsProxy "+jsProxy+", hashCode "+hashCode());
		jsProxy.quitActionHandler();
	}

	/**
	 * Called when the browser is exiting.
	 */
	public void destroy() {
		if(trace.getDebugCode("applet"))
			trace.out("applet", "JSBridge.destroy(): calling jsProxy.quitActionHandler(); jsProxy "+jsProxy+", hashCode "+hashCode());
		jsProxy.quitActionHandler();
	}
	
	public void start() {
		String[] argv = makeArgv(DEFAULT_LAUNCHER_ARGUMENTS);
		window = JSObject.getWindow(this);

		SingleSessionLauncher launcher =
				new SingleSessionLauncher(null, null, argv, false, ls, null, this);
		
		jsProxy = (JSProxy) launcher.getController().getRemoteProxy();

		if(trace.getDebugCode("applet"))
			trace.out("applet", "JSBridge.start(): window "+window+", launcher "+launcher+
					", .inAppletMode() "+launcher.inAppletMode()+", jsProxy "+jsProxy+", hashCode "+hashCode());
		
		((JSToolProxy) jsProxy.getToolProxy()).setBridge(this);
		
//		commManager = (JSObject) window.eval("CTAT.commManager");
		Object[] args = {this};
		window.call("registerTutor", args);
	}

	/**
	 * Assemble an argument array like the command-line invocation. Looks for these parameters:<ul>
	 * <li>debugCodes - splits parameter by commas or spaces and appends "-debugCodes" and each value.</li>
	 * </ul>
	 * @param defaultLauncherArguments
	 * @return completed argument array
	 */
	private String[] makeArgv(String[] defaultLauncherArguments) {
		List<String> result = new ArrayList<String>(Arrays.asList(defaultLauncherArguments));
		String debugCodes = getParameter(SingleSessionLauncher.DEBUG_CODES);
		if(debugCodes != null) {
			String[] codes = debugCodes.split("[ ,]");
			result.add("-"+SingleSessionLauncher.DEBUG_CODES);  // command line prefixes a hyphen
			for(int j = 0; j < codes.length; j++) {
				if(codes[j].length() > 0)
					result.add(codes[j]);
			}
		}
		if(trace.getDebugCode("applet"))
			trace.out("applet", "JSBridge.makeArgv() result "+result);
		return result.toArray(new String[result.size()]);
	}

	public void receiveFromInterface(String msg) {
		if(trace.getDebugCode("applet"))
			trace.out("applet", "JSBridge.receiveFromInterface(\""+msg+"\"), window "+window+", hashCode() "+hashCode());
		try {
			jsProxy.receiveMessageFromInterface(msg);
		} catch (Exception e) {
			sendMessage("Caught Exception: "+e.getMessage()+"\n");
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String msg) {
		Object[] args = {msg};
		if(trace.getDebugCode("applet"))
			trace.out("applet", "JSBridge.sendMessage("+msg+") len "+(msg==null?-1:msg.length())+
					", window "+window+", hashCode "+hashCode());
		try {
			window.call("sendToInterface", args);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public JSProxy getJSProxy() {
		return jsProxy;
	}
}
