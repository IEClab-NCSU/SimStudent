package edu.cmu.pact.TutoringService;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;

import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;

/**
 * Handler for service requests.
 */
class ServiceRequest extends RequestHandler 
{
	/** Top-level class of the Tutoring Service we're reporting on. */
	private LauncherServer ls;
	private LauncherHandler lh;

	/**
	 * @param ls the Tutoring Service we're reporting on
	 */
	ServiceRequest(LauncherServer ls) {
		this.ls = ls;
	}
	
	/**
	 * @param ls the Tutoring Service we're reporting on
	 */
	ServiceRequest(LauncherHandler lh) {
		this.lh = lh;
	}
	
	/** Attribute of shutdown response. */
	static final String SHUTDOWN_TIME = "shutdownTime";

	/** The tag name of the request element. */
	static final String NAME = "service";
				
	/**
	 * @return {@link RequestHandler#clone()}, cast to this class
	 * @throws CloneNotSupportedException
	 * @see edu.cmu.pact.TutoringService.Monitor.RequestHandler#clone()
	 */
	protected ServiceRequest clone() throws CloneNotSupportedException 
	{
		debug ("clone ()");
		return (ServiceRequest) super.clone();
	}
	
	/**
	 * Process a {@value #NAME} request.
	 * @param req
	 * @param ls enclosing server instance
	 * @see edu.cmu.pact.TutoringService.RequestHandler#handleRequest(org.jdom.Element, java.io.PrintWriter)
	 */
	public Element handleRequest(Element req) 
	{
		debug ("handleRequest ("+req+")");
		
		Element resp = new Element(NAME);
		String cmd = req.getAttributeValue("cmd");
		
		if ("status".equalsIgnoreCase(cmd)) 
		{
			resp.addContent(createVersionElement());
			resp.addContent(createSessions(false));
			resp.addContent(createMemoryElement());
			resp.addContent(createLoggingElement());
		} 
		else if ("detail".equalsIgnoreCase(cmd)) 
		{
			resp.addContent(createSessions(true));
		} 
		else if ("all".equalsIgnoreCase(cmd)) 
		{
			resp.addContent(createVersionElement());
			resp.addContent(createMemoryElement());
			resp.addContent(createLoggingElement());
			resp.addContent(createSessions(true));
		} 
		else if ("shutdown".equalsIgnoreCase(cmd)) 
		{
			resp.setAttribute(SHUTDOWN_TIME, (new Date()).toString());
			// delay shutdown(); until after response sent  
			Thread shutdown = new Thread() {
				public void run() {
					Utils.sleep(500);           // allow response to get transmitted
					ls.shutdown();              // never returns?
				}
			};
			shutdown.start();
		} 
		else 
		{
			resp.setAttribute("result", "error: unknown cmd "+cmd);
		}
		
		return resp;
	}
	
	/**
	 * Generate an element whose attributes convey the information from
	 * {@link VersionInformation#RELEASE_NAME},
	 * {@link VersionInformation#VERSION_NUMBER},
	 * {@link VersionInformation#BUILD_DATE}.
	 * @return new "&lt;version&gt;" element
	 */
	private Element createVersionElement() 
	{
		debug ("createVersionElement ()");
		
		Element version = new Element("version");
		version.setAttribute("releaseName", VersionInformation.RELEASE_NAME);
		version.setAttribute("versionNumber", VersionInformation.VERSION_NUMBER);
		version.setAttribute("buildDate", VersionInformation.BUILD_DATE);
		return version;
	}
	/**
	 * Generate an element with VM memory info.
	 * @return populated &lt;memory&gt; Element
	 */
	private Element createMemoryElement() 
	{
		debug ("createMemoryElement ()");
		
		Element memory = new Element("memory");
		memory.setAttribute("total", Long.toString(Runtime.getRuntime().totalMemory()));
		memory.setAttribute("max", Long.toString(Runtime.getRuntime().maxMemory()));
		memory.setAttribute("free", Long.toString(Runtime.getRuntime().freeMemory()));
		return memory;
	}
	/**
	 * Generate an element with summary logging info for the service.
	 * @return populated &lt;logging&gt; Element
	 */
	private Element createLoggingElement() 
	{
		debug ("createLoggingElement ()");
		
		Element logging = new Element("logging");
		Map<String, String> logInfo = ls.getSummaryLogInfo().toAttributes();
		
		for (String key : logInfo.keySet())
			logging.setAttribute(key, logInfo.get(key));
				
		return logging;
	}
	/**
	 * Generate an element with data on individual
	 * {@link LauncherServer.Session}s.
	 * @param wantDetails true to add a child Element for each session
	 * @return populated &lt;sessions&gt; Element
	 */
	private Element createSessions(boolean wantDetails) 
	{
		debug ("createSessions (LauncherServer,boolean)");
		
		Set<String> sessionKeys = ls.getSessionKeys();
		Element sessions = new Element("sessions");
		int nSessions = sessionKeys.size();
		sessions.setAttribute("count", Integer.toString(nSessions));
		if (trace.getDebugCode("ls")) 
			trace.outNT("ls", "ServiceRequest.handleRequest() count "+nSessions);
		if (!wantDetails)
			return sessions;
		for (String guid : sessionKeys) {
			Element sessElt = ls.generateSessionElement(guid);
			if (sessElt != null)
				sessions.addContent(sessElt);
		}
		return sessions;
	}
}