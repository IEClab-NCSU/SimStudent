package edu.cmu.pact.TutoringService;

import java.io.BufferedReader;
import java.net.Socket;
import java.util.Date;

import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.hcii.ctat.CTATHTTPExchange;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.SocketProxy.HTTPActionHandler;
import edu.cmu.pact.SocketProxy.HTTPToolProxy;
import edu.cmu.pact.SocketProxy.SocketToolProxy;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.HTTPMessageObject;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

/**
 * Variant for HTTP connections.
 */
class HTTPSession extends LauncherServer.Session {

	/** Queue of messages for our Behavior Recorder. */
	private HTTPActionHandler actionHandler = null;
	
	/** LauncherServer reference, for {@link #createLauncher(BufferedReader)}. */
	private final TSLauncherServer launcherServer;

	/**
	 * A Session for HTTP
	 * @param launcherServer enclosing instance
	 * @param guid
	 * @param exchange should have a {@value MsgType#SET_PREFERENCES} request
	 * @param handler for error responses
	 */
	HTTPSession(LauncherServer launcherServer, String guid, CTATHTTPExchange exchange,
			LauncherHandler handler) {
		launcherServer.super((Socket) null);
		this.launcherServer = launcherServer;
		setGuid(guid);
		try {
			msgFormat = SocketToolProxy.deriveMsgFormat(exchange.getRequestBodyAsString());
		} catch(Exception e) {
			handler.sendResponse(exchange, "Error reading request: "+e+"; cause "+e.getCause(), 400);
			return;
		}
		// Need a temp SetPreferences msg to scan for brd and abort early if missing
		MessageObject tempSetPrefs = HTTPMessageObject.messageFromExchange(exchange, getMsgFormat());
		if(!findBRDFilename(tempSetPrefs)) {
			handler.sendResponse(exchange, "No graph file specified", 400);
			return;
		}
		if (trace.getDebugCode("ls"))
			trace.out("ls", "LauncherHandler: new session: guid "+guid+", school "+getSchoolName()+
					", userGuid "+getUserGuid()+"\n  BRD File = " + brdFile);

		setupController(null);
		
		// Once we have a controller, create and store the real SetPreferences msg 
		setSetPreferencesMsg(new HTTPMessageObject(exchange, getMsgFormat(), getController()));

		setLoggingProperties(getSetPreferencesMsg());
		getSetPreferencesMsg().setTransactionInfo(getTxInfo().create());

		processSetPreferences();
		setTimeStamp(new Date());
		setIPAddr(exchange.getIPAddress());

    	if (trace.getDebugCode("ls")) trace.out("ls", "HTTPSession about to addSession "+this);
		launcherServer.addSession(this);
	}

	/**
	 * @return the {@link #actionHandler}
	 */
	HTTPActionHandler getActionHandler() {
		return actionHandler;
	}

	/**
	 * @param actionHandler new value for {@link #actionHandler}
	 */
	void setActionHandler(HTTPActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	/**
	 * Call SingleSessionLauncher constructor with our arguments. Calls {@link #setLauncher(SingleSessionLauncher)}.
	 * @param br reader
	 * @see edu.cmu.pact.TutoringService.LauncherServer.Session#createLauncher(BufferedReader)
	 */
	protected void createLauncher(BufferedReader br) {
		String argv[] = {
				"-Dguid=" + getGuid(),
				"-DisOnline=true",
				"-"+SingleSessionLauncher.USE_HTTP,
				"-debugCodes", "ls",
				"-debugCodes", "tsltsp",
				"-debugCodes", "tsltstp"
		};
		setLauncher(new SingleSessionLauncher(null, null, argv, false, launcherServer, null, null));
	}

	/**
	 * Set {@link #actionHandler} and start the thread. Warn, stop the old thread and replace if
	 * {@link #actionHandler} is not null.
	 */
	public void startActionHandler() {
		BR_Controller controller = getController();
		if(actionHandler != null) {
			trace.err("HTTPSession warning: startActionHandler("+controller+") called when actionHandler "+
					actionHandler+" already set, queue length "+actionHandler.size()+"; exiting old one");
			actionHandler.halt();    // exit BR thread
		}
		actionHandler = new HTTPActionHandler(controller);
		Thread t = new Thread(actionHandler, "ActionHandler"+getGuid());
		t.start();
	}
}