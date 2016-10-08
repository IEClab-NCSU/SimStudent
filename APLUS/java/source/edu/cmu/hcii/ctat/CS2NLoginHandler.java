package edu.cmu.hcii.ctat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

public class CS2NLoginHandler {

	/**
	 * Validate a username/password pair
	 * @param username
	 * @param password
	 * @return success/failure
	 */
	public static boolean login(String username, String password)
	{
		if(username == null || password == null) return false;
		
		// TODO validate password, and return false if it's incorrect
		
		return true;
	}
	
	/**
	 * Validate a username/password pair
	 * @param username
	 * @param password
	 * @return success/failure
	 */
	public static boolean createlogin(String username, 
									  String password,
									  String firstname,
									  String lastname,
									  String section)
	{
		if(username == null || password == null) return false;
		
		// TODO validate password, and return false if it's incorrect
		
		return true;
	}	
	
	/**
	 * Handles a login HTTP request, and responds to the request either by redirecting to the main page
	 * or by redirecting to a failed-login page.
	 * @param exchange the HTTP exchange object that the request came in on
	 * @return the username if the login succeeded, or null if the login failed
	 */
	public static String handleLogin(CTATHTTPExchange exchange)
	{
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
			if(login(username, password))
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
			if(login(username, password))
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
	public static String handleCreate(CTATHTTPExchange exchange)
	{
		// login credentials should come in on a GET or post request
		String requestMethod = exchange.getRequestMethod();
		if(requestMethod.equalsIgnoreCase("GET"))
		{
			String path = exchange.getRequestURI().getPath();
			if(path.contains("?"))
			{
				String query = path.substring(path.indexOf("?"));
				
				CTATWebTools webtools = new CTATWebTools();
				Map<String, String> queryMap = webtools.parseQuery(query);
				String username = queryMap.get("username");
				String password = queryMap.get("password");
				String firstname = queryMap.get("firstname");
				String lastname = queryMap.get("lastname");				
				String section = queryMap.get("section");
				if(createlogin(username, password,firstname,lastname,section))
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
			else return null; // no query string, so no login credentials were provided
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
			if(createlogin(username, password,firstname,lastname,section))
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
	 * Send a redirect (302) response
	 * @param exchange The object representing this HTTP exchange
	 * @param path the *relative* path to redirect to (should begin with a slash)
	 * @return success/failure
	 */
	private static boolean redirectTo(CTATHTTPExchange exchange, String path)
	{
		String fullPath = "http://" + CTATLink.hostName + ":" + CTATLink.wwwPort + path;

		exchange.addResponseHeader("Location", fullPath);
		exchange.sendResponseHeaders(302, 0);
		exchange.close();
		return true;
	}
	
	/**
	 * Send a string as an HTTP response
	 * @param exchange The object representing this HTTP exchange
	 * @param message The message to send as a response (null means empty string)
	 * @return success/failure
	 */
	private static boolean sendString(CTATHTTPExchange exchange, String message)
	{
		boolean success = true;
		
		if(message == null) message = "";
		
		try
		{
			byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
			exchange.sendResponseHeaders(200, bytes.length); // HTTP 200 = OK
			//OutputStream out = exchange.getResponseBody();
			OutputStream out = exchange.getOutputStream();
			out.write(bytes);
			//out.close();
			exchange.close();
		}
		catch(Exception e)
		{
			success = false;
		}
		
		return success;
	}

}
