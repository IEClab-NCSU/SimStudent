package edu.cmu.pact.TutoringService;

import org.jdom.Element;

import edu.cmu.pact.Utilities.trace;

/**
 * Handler for session requests.
 */
class SessionRequest extends RequestHandler 
{
	/** Top-level class of the Tutoring Service we're reporting on. */
	private LauncherServer ls;
	private LauncherHandler lh; // TODO Kevin 20130402 added to allow for new Class LauncherHandler 

	/**
	 * @param ls the Tutoring Service we're reporting on
	 */
	SessionRequest(LauncherServer ls) {
		this.ls = ls;
	}
	
	// TODO Kevin 20130402 added to allow for new Class LauncherHandler
	/**
	 * @param lh the Tutoring Service we're reporting on
	 */
	SessionRequest(LauncherHandler lh) {
		this.lh = lh;
	}

	/** The tag name of the request element. */
	static final String NAME = "session";
	
	/**
	 * @return {@link RequestHandler#clone()}, cast to this class
	 * @throws CloneNotSupportedException
	 * @see edu.cmu.pact.TutoringService.Monitor.RequestHandler#clone()
	 */
	protected SessionRequest clone() throws CloneNotSupportedException 
	{
		debug ("clone ()");
		return (SessionRequest) super.clone();
	}
	/**
	 * Process a {@value #NAME} request.
	 * @param req
	 * @param ls enclosing server instance
	 * @see edu.cmu.pact.TutoringService.RequestHandler#handleRequest(org.jdom.Element, java.io.PrintWriter)
	 */
	public Element handleRequest(Element req) 
	{
		debug ("handleRequest (Element)");
		
		Element resp = new Element(NAME);
		String guid = req.getAttributeValue("guid");
		
		if (guid == null || guid.length() < 1) 
		{
			resp.setAttribute("guid", guid == null ? "null" : guid);
			resp.setAttribute("result", "error: null or empty guid");
			return resp;
		}
		
		resp.setAttribute("guid", guid);
		String cmd = req.getAttributeValue("cmd");
		
		if ("remove".equalsIgnoreCase(cmd)) 
		{
			String result = (ls.removeSession(guid) ? "removed" : "not found" );
			
			if (trace.getDebugCode("ls"))
				trace.out("ls", "SessionRequest.handleRequest() guid "+guid+", result "+result);
			
			resp.setAttribute("result", result);
		} 
		else if ("detail".equalsIgnoreCase(cmd)) 
		{
			Element sessElt = ls.generateSessionElement(guid);
			if (sessElt != null) 
			{
				resp.addContent(sessElt);
			} 
			else 
			{
				String errMsg = "null session object for guid "+guid;
				trace.err(errMsg);
				resp.setAttribute("result", errMsg);
			}
		} 
		else 
		{
			resp.setAttribute("result", "error: unknown cmd "+cmd);
		}
		return resp;
	}
}