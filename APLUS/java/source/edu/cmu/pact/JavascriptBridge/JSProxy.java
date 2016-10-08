package edu.cmu.pact.JavascriptBridge;

import pact.CommWidgets.RemoteProxy;
import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.SocketProxy.ActionHandler;
import edu.cmu.pact.SocketProxy.LogServlet;
import edu.cmu.pact.TutoringService.TSLauncherServer;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

public class JSProxy implements RemoteProxy {
	
	private BR_Controller controller;
	private JSToolProxy tp;
	private ActionHandler actionHandler;
	
	private LogServlet logServlet = null;
	
	public JSProxy(BR_Controller controller) {
		this.controller = controller;
		this.tp = new JSToolProxy(controller);
	}
	
	public void receiveMessageFromInterface(String msg) {
		if(trace.getDebugCode("applet"))
			trace.outNT("applet", "Received: "+msg);
		if (LogServlet.handleLogRecord(msg, null, logServlet))
			return;
		MessageObject mo = MessageObject.parse(msg);
		if (logServlet == null && MsgType.SET_PREFERENCES.equalsIgnoreCase(mo.getMessageType()))
			setupLogServlet(mo);
		actionHandler.enqueue(mo);
	}
	
	public void start() {
		actionHandler = new ActionHandler(this.controller);
		(new Thread(actionHandler)).start();
	}
	
	//FIXME: Duplicated code in all RemoteProxys
	public ActionHandler getActionHandler() {
		return actionHandler;
	}
	
	//FIXME: Duplicated code in all RemoteProxys
	public UniversalToolProxy getToolProxy() {
		return tp;
	}
	
	/**
	 * Set {@link #logServlet} with a new instance whose inTutoringService
	 * parameter is false. The {@link LogServlet}'s behavior will be determined by
	 * preferences from InstallationPreferences.xml.
	 * @param setPrefsMsg message with logging parameters for this session
	 * @see pact.CommWidgets.RemoteProxy#setupLogServlet(edu.cmu.pact.ctat.MessageObject)
	 */
	public void setupLogServlet(MessageObject setPrefsMsg) {
		this.logServlet =
			new LogServlet(controller.getPreferencesModel(), setPrefsMsg, false, null);
		TSLauncherServer ls = this.controller.getLauncher().getLauncherServer();
		if (ls != null)
			logServlet.setLogInfo(ls.getLogInfo(null));
		if (trace.getDebugCode("log")) trace.out("log", "SocketProxy.setLogServlet("+logServlet+")");
		(new Thread(logServlet)).start();
	}

	/**
	 * Send a quit message to the Behavior Recorder.
	 */
	public void quitActionHandler() {
		actionHandler.enqueue(MessageObject.makeQuitMessage());
	}	
}
