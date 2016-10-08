package pact.CommWidgets;

import edu.cmu.pact.SocketProxy.ActionHandler;
import edu.cmu.pact.ctat.MessageObject;

public interface RemoteProxy {
	public void start();
	public ActionHandler getActionHandler();
	public UniversalToolProxy getToolProxy();
	public void setupLogServlet(MessageObject setPrefsMsg);
}
