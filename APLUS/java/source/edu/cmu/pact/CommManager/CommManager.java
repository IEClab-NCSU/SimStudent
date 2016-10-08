package edu.cmu.pact.CommManager;

import java.util.Hashtable;

import pact.CommWidgets.JCommWidgetsToolProxy;
import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.pact.SocketProxy.XMLConverter;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.TutorController;


//////////////////////////////////////////////////////////////////////
// A hacky class for handling comm communication	
//////////////////////////////////////////////////////////////////////
public class CommManager {

	
	private static CommManager instance;
	private	Hashtable receivers = new Hashtable();
		
	//////////////////////////////////////////////////////////////////////
	//	
	//////////////////////////////////////////////////////////////////////
	private CommManager () {
	
	}

	/**
	 * Create a child proxy for a given component name and register a message receiver for it.
	 * @param rcvr receiver instance
	 * @param componentName name for the child proxy
	 * @param controller access to {@link UniversalToolProxy}
	 */
	public void addMessageReceiver(CommMessageReceiver rcvr, String componentName, TutorController controller) {
		if (!VersionInformation.includesCL())
			return;
		if (controller.getUniversalToolProxy() == null)
			return;
		JCommWidgetsToolProxy childProxy = new JCommWidgetsToolProxy("Component",
				componentName, controller.getUniversalToolProxy().getToolProxy(), componentName);
		registerMessageReceiver(rcvr, childProxy.toString());
	}
	
	//////////////////////////////////////////////////////////////////////
	// Register to receive a comm message		
	//////////////////////////////////////////////////////////////////////
	public void registerMessageReceiver (CommMessageReceiver m, String objectDescription) { 
		receivers.put (objectDescription, m);	
	}

	//////////////////////////////////////////////////////////////////////
	// Send a message to registered receivers
	//////////////////////////////////////////////////////////////////////	
	
	public void sendJavaMessage (edu.cmu.old_pact.dormin.MessageObject o) {
		
		String object = "";
		try {
			object = o.getParameter ("OBJECT").toString();
		} catch (edu.cmu.old_pact.dormin.MissingParameterException e) {
//			trace.out (5, this, "CommManager can't find OBJECT parameter for message " + o);
			return;
		}

		CommMessageReceiver dmr = (CommMessageReceiver) receivers.get (object);
		MessageObject mo = XMLConverter.commToNewMO(o);
		if (dmr != null)
			dmr.receiveMessage (mo);
	}
	
	public void sendJavaMessage (MessageObject o) {
	
		String object = "";
//		try {
//			object = o.getParameter ("OBJECT").toString();
			object = (String) o.getProperty("OBJECT");
//		} catch (edu.cmu.old_pact.dormin.MissingParameterException e) {
//			trace.out (5, this, "CommManager can't find OBJECT parameter for message " + o);
//			return;
//		}

		CommMessageReceiver dmr = (CommMessageReceiver) receivers.get (object);

		if (dmr != null)
//			dmr.receiveCommMessage (o);
			dmr.receiveMessage (o);
	}
	
	
	/**
	 * Create a receiver for Comm messages and register it
	 * @param componentName receiver's name for registration
	 * @param rcvr message receiver itself
	 * @param ctlr for access to {@link UniversalToolProxy}
	 */
	public static void addCommListener(String componentName, CommMessageReceiver rcvr,
			TutorController ctlr) {
		if (!VersionInformation.includesCL())
			return;
        if (ctlr==null ||
                ctlr.getUniversalToolProxy()==null ||
                ctlr.getUniversalToolProxy().getToolProxy()==null)
        	return;
	    JCommWidgetsToolProxy childProxy = new JCommWidgetsToolProxy(
	            "Component", componentName, ctlr.getUniversalToolProxy().getToolProxy(),
	            componentName);
	    instance().registerMessageReceiver(rcvr, childProxy.toString());
	}

	/**
	 * Find the "OBJECT" property in the given message, whose value should be a
	 * {@link JCommWidgetsToolProxy} instance, and return its widget name.
	 * @param mo message to scan
	 * @return {@link JCommWidgetsToolProxy#getWidgetName()} on the found object;
	 *         null if not found
	 */
	public static String extractComponentName(MessageObject mo) {
        Object object = mo.getProperty("OBJECT");
        if (object == null)
        	return null;
//      try {
//chc            object = o.getParameter("OBJECT");
//      } catch (CommException e) {
//          trace.out("can't find object for message " + o);
//          return;
//      }
      String commComponentName = ((JCommWidgetsToolProxy) object).getWidgetName();
      return commComponentName;
		
	}

	//////////////////////////////////////////////////////////////////////
	// Return singleton instance
	//////////////////////////////////////////////////////////////////////	
	public static CommManager instance() {
	
//		trace.out (5, "CommManager.CommManager", "retrieving commmanager instance");
		if (instance == null)
			instance = new CommManager();
		
		return instance;
	}

}
