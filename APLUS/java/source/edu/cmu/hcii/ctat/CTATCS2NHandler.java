/**
 -
 License:
 -
 ChangeLog:
 $Log: CTATCS2NHandler.java,v $
 Revision 1.9  2012/10/16 20:01:34  vvelsen
 Lots of changes to align the various http handlers. Most of the work has been done in the DVD handler for FIRE which now uses the progress database to also store user information

 Revision 1.8  2012/10/11 19:13:48  vvelsen
 Started reworking the local http handler to work with the CS2N specific DVD handler

 Revision 1.7  2012/10/11 14:50:32  vvelsen
 Fixed the syntax error that broke the build. Due to a design decision by the Eclipse team you can no longer see error stats in the Navigator pane, which prevents you from seeing problems with your code

 Revision 1.6  2012/09/07 18:15:05  vvelsen
 Quick checkin so that Jonathan can test his CL bridge code

 Revision 1.5  2012/08/30 15:25:33  sewall
 Fix-ups after Alvaro's 2012/08/17 merge.

 -
 Notes:
 -
*/

package edu.cmu.hcii.ctat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.io.OutputStream;
//import java.nio.charset.Charset;
import java.util.Map;

import edu.cmu.pact.Utilities.Utils;

/**
 * Use this handler specifically with the FIRE offline version of the local
 * tutorshop. It is designed to work together with the local html pages
 * that manage purely local accounts when in CDROM mode. Do not use this
 * class as a base for more generic local handlers. Instead start with
 * the base class CTATHTTPLocalHandler
 */
class CTATCS2NHandler extends CTATOfflineHTTPHandler implements CTATHTTPHandlerInterface
{		
	/**
	 *
	 */	
	public CTATCS2NHandler (String aLogFile,
			   				UserProgressDatabase upd, 
			   				CTATCurriculum aCurriculum) throws IOException 
	{
		super (aLogFile,upd,aCurriculum);
		
    	setClassName ("CTATCS2NHandler");
    	debug ("CTATCS2NHandler ()");
	}	
	/**
	 * @param exchange A CTATHTTPExchange object representing a request that this method will respond to
	 * @return whether or not this method could handle the exchange
	 */
	public boolean handle (CTATHTTPExchange arg0)
	{
		debug ("handle ()");
		
		if(getCurriculum () == null)
		{
			debug ("Error: curriculum object is null");
			return false;
		}		
		
		Boolean processed=false;
		
		String path = arg0.getRequestURI().getPath();
		
		// use special handling if this is a local login request
		if(path.equals("/logincs2n.cgi"))
		{
			CTATLink.userID = handleLogin(arg0);
			debug ("Handled: true");
			return (true);
		}		
		
		if(path.startsWith("/createlocal.cgi")) //  should be of the form /logincs2n.cgi?username=...&password=...
		{
			CTATLink.userID = handleCreate(arg0);
			debug ("Handled: true");
			return (true);
		}
		
		processed=super.handle(arg0);
		
		return (processed);
	}	
	/**
	 * Validate a username/password pair
	 * @param username
	 * @param password
	 * @param skipPassword if true, don't require the passwords to match
	 * @return success/failure
	 */
	private boolean login (String username, String password, boolean skipPassword)
	{
		debug ("login ()"); 
		
		if(username == null || password == null) 
			return false;
		
		CTATUserData targetUser=localUserDB.getUser(username);
		
		if (targetUser==null)
		{
			debug ("Error: username doesn't exists");
			return false;
		}
		
		if (!skipPassword && !targetUser.password.equals(password))
		{
			debug ("Error password is not correct");
			return false;
		}
		
		CTATLink.userID=username;
		
		return true;
	}	
	/**
	 * Validate a username/password pair
	 * @param username
	 * @param password
	 * @return success/failure
	 */
	public String createlogin(String username, 
							   String password,
							   String firstname,
							   String lastname,
							   String section)
	{
		debug ("createlogin ("+username+","+password+","+firstname+","+lastname+","+section+")"); 
		
		if ((username == null) || (password == null)) 
			return "Error: username or password is not specified";
		
		if (localUserDB.getUser(username.toLowerCase())!=null)
		{
			return "Error: username already exists";
		}
		
		CTATUserData newUser=localUserDB.addUser(username.toLowerCase());
		
		newUser.password=password;
		newUser.firstName=firstname;
		newUser.lastName=lastname;
		newUser.section=section;
		
		try 
		{
			localUserDB.saveUserProgress (username.toLowerCase());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return "Error: unable to save user data";
		}
				
		return null;
	}		
	/**
	 * Handles a login HTTP request, and responds to the request either by redirecting to the main page
	 * or by redirecting to a failed-login page.
	 * @param exchange the HTTP exchange object that the request came in on
	 * @return the username if the login succeeded, or null if the login failed
	 */
	public String handleLogin(CTATHTTPExchange exchange)
	{
		debug ("handleLogin ()"); 
		
		// login credentials should come in on a GET or post request
		String requestMethod = exchange.getRequestMethod();
		
		if(requestMethod.equalsIgnoreCase("GET"))
		{
			String query = exchange.getRequestURI().getQuery();
			
			if(query == null) return null; // no login credentials were provided in query string

			CTATWebTools webtools = new CTATWebTools();
			Map<String, String> queryMap = webtools.parseQuery(query);
			String username = queryMap.get("username");
			String password = queryMap.get("password");
			
			if(login(username, password, false))
			{
				//redirectTo(exchange, "/");
				sendString (exchange, "OK");
				return username;
			}
			else
			{
				sendString (exchange, "Error: The login attempt failed.");
				return null;
			}
		}
		else if(requestMethod.equalsIgnoreCase("POST"))
		{
			InputStream in = exchange.getRequestBody();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b;
			String query;
			try
			{
				while((b = in.read()) != -1)
				{
					baos.write(b);
				}
				in.close();
				query = baos.toString("UTF-8");
			} catch(IOException e) { return null; }
			
			CTATWebTools webtools = new CTATWebTools();
			Map<String, String> queryMap = webtools.parseQuery(query);
			String username = queryMap.get("username");
			String password = queryMap.get("password");
			if(login(username, password, false))
			{
				redirectTo(exchange, "/");
				return username;
			}
			else
			{
				sendString(exchange, "The login attempt failed.");
				return null;
			}
		}
		else return null;
	}	
	/**
	 * Handles a login HTTP request, and responds to the request either by redirecting to the main page
	 * or by redirecting to a failed-login page.
	 * @param exchange the HTTP exchange object that the request came in on
	 * @return the username if the login succeeded, or null if the login failed
	 */
	public String handleCreate(CTATHTTPExchange exchange)
	{
		debug ("handleCreate ()"); 
		
		// login credentials should come in on a GET or post request
		String requestMethod = exchange.getRequestMethod();
		
		if(requestMethod.equalsIgnoreCase("GET"))
		{
			String req = exchange.getRequestURI().toString();
			
			debug ("Query: " + req);
			
			String path = exchange.getRequestURI().getPath();
			
			debug ("Query path: " + path);
			
			if(req.contains("?"))
			{
				String query = req.substring(req.indexOf("?")+1);
				
				CTATWebTools webtools = new CTATWebTools();
				Map<String, String> queryMap = webtools.parseQuery(query);
				String username = queryMap.get("username");
				String password = queryMap.get("password");
				String firstname = queryMap.get("firstname");
				String lastname = queryMap.get("lastname");				
				String section = queryMap.get("section");
				
				String result=createlogin(username, password,firstname,lastname,section);
				
				if(result==null)
				{
					//redirectTo(exchange, "/");
					sendString (exchange, "OK");
					return username;
				}
				else
				{
					sendString (exchange,result);
					return null;
				}
			}
			else 
			{
				sendString(exchange, "No query string, so no login credentials were provided.");
				return null; // no query string, so no login credentials were provided
			}
		}
		else if(requestMethod.equalsIgnoreCase("POST"))
		{
			InputStream in = exchange.getRequestBody();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b;
			String query;
			try
			{
				while((b = in.read()) != -1)
				{
					baos.write(b);
				}
				in.close();
				query = baos.toString("UTF-8");
			} catch(IOException e) { return null; }
			
			CTATWebTools webtools = new CTATWebTools();
			Map<String, String> queryMap = webtools.parseQuery(query);
			String username = queryMap.get("username");
			String password = queryMap.get("password");
			String firstname = queryMap.get("firstname");
			String lastname = queryMap.get("lastname");				
			String section = queryMap.get("section");
			
			String result=createlogin(username, password,firstname,lastname,section);
			
			if(result==null)
			{
				redirectTo(exchange, "/");
				return username;
			}
			else
			{
				sendString(exchange, result);
				return null;
			}
		}
		else return null;
	}		
	/**
	 * Send a redirect (302) response
	 * @param exchange The object representing this HTTP exchange
	 * @param path the *relative* path to redirect to (should begin with a slash)
	 * @return success/failure
	 */
	/*
	private boolean redirectTo(CTATHTTPExchange exchange, String path)
	{
		debug ("redirectTo ()"); 
		
		String fullPath = "http://" + CTATLink.hostName + ":" + CTATLink.wwwPort + path;
		
		exchange.addResponseHeader("Location", fullPath);
		exchange.sendResponseHeaders(302, 0);
		//exchange.close();
		return true;
	}
	*/	
	/**
	 * Send a string as an HTTP response
	 * @param exchange The object representing this HTTP exchange
	 * @param message The message to send as a response (null means empty string)
	 * @return success/failure
	 */
	/*
	private boolean sendString(CTATHTTPExchange exchange, String message)
	{
		debug ("sendString ()"); 
		
		boolean success = true;
		
		if(message == null) message = "";
		
		try
		{
			byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
			exchange.sendResponseHeaders(200, bytes.length); // HTTP 200 = OK
			OutputStream out = exchange.getOutputStream();
			out.write(bytes);
			out.flush();
			//exchange.close();
		}
		catch(Exception e)
		{
			success = false;
		}
		
		return success;
	}
	*/

	/**
	 * For already-authenticated users, login or create an account given only
	 * a user identifier.
	 * @param userid
	 */
	protected void setUserid(String userid) {
		if(userid == null || userid.length() < 1)
			return;
		if(login(userid, "", true))  // true => skip password check
			return;                  // user already in db
		
		if(CTATLink.requirePredefinedUserid) {
			CTATCurriculum curr = getCurriculum();
			if(curr == null || !(curr.isUserRegistered(userid))) {
				String errMsg = "Curriculum missing or username \""+userid+"\" not recognized";
				Utils.showExceptionOccuredDialog(null, "Username \""+userid+"\" not recognized",
					"Login Failed");
				throw new IllegalArgumentException(errMsg);
			}
		}
		
		String error = this.createlogin(userid, "", "offline_user", userid, "offline_class");
		if(error != null)
			Utils.showExceptionOccuredDialog(null,
					"Error creating user database entry for \""+userid+"\": "+error+
					". Your work might not be saved.", "Login Error");
		else
			CTATLink.userID = userid;
	}	
}
