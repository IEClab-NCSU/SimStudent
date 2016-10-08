/**
 ------------------------------------------------------------------------------------
 $Author: sewall $ 
 $Date: 2014-05-21 14:47:59 -0400 (Wed, 21 May 2014) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATHTTPServer.java,v 1.12 2012/09/28 13:37:56 sewall Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATHTTPServer.java,v $
 Revision 1.12  2012/09/28 13:37:56  sewall
 Changes for Julie Booth integration with CL.

 Revision 1.11  2012/09/21 13:19:01  vvelsen
 Quick checkin to get vital code into CVS for FIRE

 Revision 1.10  2012/09/06 17:48:55  vvelsen
 Reworking of the local tutorshop to allow easier managment of runtime contexts. The completely local/demo http handler is now in its own file and the CLBridge should now be ready to be tested as a standalone class

 Revision 1.9  2012/08/23 21:04:35  kjeffries
 clean-ups

 Revision 1.8  2012/08/17 17:50:32  alvaro
 merging versions

 Revision 1.6  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.5  2012/05/14 15:28:55  vvelsen
 Disabled automatic logging of critical events in CTATDeamon because it is the base class of CTATHTTPServer, which meant that anything that uses that class would automatically want to create a logfile

 Revision 1.4  2012/04/11 13:16:37  vvelsen
 Added missing files

 Revision 1.3  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.7  2011/09/29 15:52:16  sewall
 Now replace browser page when user exits server via Swing gui.

 Revision 1.6  2011/07/07 21:01:09  kjeffries
 Removed an unnecessary import statement that was preventing compatibility with Java 1.5.

 Revision 1.5  2011/06/28 21:26:28  kjeffries
 Changed to use class CTATHTTPExchange rather than HttpExchange which is available only in Java version 6

 Revision 1.4  2011/03/25 20:38:49  vvelsen
 Added much more capabilties to the USB TutorShop. We can now start problems defined in problem_set.xml files. Much more of the problem sequencing code has been finished but more needs to happen there. Some of the internals have been bolstered.

 Revision 1.3  2011/02/09 23:41:43  sewall
 Changes to get the tutoring service working. New interface LauncherServer implemented by CTATLauncherServer.

 Revision 1.2  2011/02/09 13:11:38  vvelsen
 Added proper logging handling to local disk (USB drive) and added encryption code.

 Revision 1.1  2011/02/06 16:55:27  vvelsen
 Added a first working version of a standalone USB based TutorShop.

 $RCSfile: CTATHTTPServer.java,v $ 
 $Revision: 20362 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATHTTPServer.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
 */

package edu.cmu.hcii.ctat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class CTATHTTPServer extends CTATBase implements Runnable, ExitableServer
{	
	/**
	 * Will call this instance's {@link CTATHTTPHandlerInterface#handle(CTATHTTPExchange)}
	 * on each incoming HTTP connection. 
	 */
	private CTATHTTPHandlerInterface handler=null;
	protected int port; // ports to listen on
	protected static String pathToRoot;
	protected static String logFileName;
	protected Socket socket = null;

	/** Server socket bound to the port for incoming connections. */
	private ServerSocket serverSocket = null;
	
	/** Set this flag to tell the server to quit. */
	protected volatile boolean nowExiting = false;

	/** Timestamp for exit request. */
	protected Date shutdownTime = null;

	// TODO Auto-generated constructor stub
	/**
	 *
	 */
	public CTATHTTPServer(Socket s, CTATHTTPHandlerInterface handler)
	{
    	setClassName ("CTATHTTPServer");
    	debug ("CTATHTTPServer ()");
    	
		socket = s;
		this.handler = handler;
	}	
	/**
	 * Same as {@link #CTATHTTPServer(int, String, String, CTATHTTPHandlerInterface) CTATHTTPServer(port, pathToRoot, logFileName, null)}.
	 * @param port value for {@link #port}
	 * @param pathToRoot value for {@link #pathToRoot}
	 * @param logFileName value for {@link #logFileName}
	 */
	public CTATHTTPServer (int port, String pathToRoot, String logFileName)
	{
		this(port, pathToRoot, logFileName, null);
	}
	
	/**
	 * Set the given fields and start a thread running
	 * {@link #CTATHTTPServer()}. Not sure why, but the thread doesn't run
	 * the instance created here.
	 * @param ports values for {@link #ports} array
	 * @param pathToRoot value for {@link #pathToRoot}
	 * @param logFileName value for {@link #logFileName}
	 * @param aHandler value for {@link #handler}; if null, creates from
	 *        {@link CTATHTTPHandler#CTATHTTPHandler(String, String) CTATHTTPHandler.CTATHTTPHandler(pathToRoot, logFileName)}
	 */
	public CTATHTTPServer (int port, 
						   String pathToRoot, 
						   String logFileName,
						   CTATHTTPHandlerInterface aHandler)
	{
    	setClassName ("CTATHTTPServer");
    	debug ("CTATHTTPServer ()");
    	
		this.port = port;
		CTATHTTPServer.pathToRoot = pathToRoot;
		CTATHTTPServer.logFileName = logFileName;
		this.handler=aHandler;
	}
	/**
	 * 
	 */
	public boolean startWebServer ()
	{
		if(handler == null) {
			debug("startWebServer(), handler is null");
		}
		else if(handler instanceof edu.cmu.pact.TutoringService.LauncherHandler) {
			debug("startWebServer() where handler is a LauncherHandler; port: "+port);
		}
		else {
			debug("startWebServer() with handler class "+handler.getClass().getSimpleName());
		}
		
		if (handler==null)
		{
			debug ("No handler assigned, creating default ...");
			
			try
			{
				handler = new CTATHTTPHandler(CTATHTTPServer.pathToRoot, CTATHTTPServer.logFileName);
			}
			catch (IOException e)
			{
				debug ("Failed to open logFile, exiting HTTP Server: "+e+"\n");
				e.printStackTrace();
				return (false);
			}
		}
		
		socket = null;                                     // to become server thread in run()
		(new Thread(this, "CTATHTTPServer")).start(); // start a new thread to act as a server
		
		return (true);
	}
	
	public void run()
	{
		if(socket == null) // if this thread is not handling a specific connection
		{
			Thread.currentThread().setName("HTTPServer");
			try 
			{
				// start a listener on the port that we want to listen on
				serverSocket = new ServerSocket(port);
				debug("to open HTTPServer listening on port " + port);
				// accept incoming connections and serve them with new threads
				while(!nowExiting)
				{
					socket = serverSocket.accept();
					if(socket != null)
					{
						(new Thread(new CTATHTTPServer(socket, getHandler()))).start();
					}
				}
			}
			catch (Exception uhe) 
			{
				debug ("Failed to open HTTPserver on localhost:" + /*port +*/ " error = " + uhe);
				uhe.printStackTrace(System.out);
			}
			debug("run(): nowExiting "+nowExiting+", shutdownTime "+shutdownTime);
		}
		else // there is a specific connection that this thread is to serve
		{
			debug("Accepted a connection on the socket, creating new HTTP Exchange Object ...");
			
			CTATHTTPExchange exchange=new CTATHTTPExchange ();
			
			if (exchange.processSocket(socket)==false)
			{
				debug ("Error creating CTATHTTPExchange");
				return;
			}
			
			if (exchange.isInitialized())
			{
				if (handler!=null)
				{					
					if (handler.handle(exchange)==true)
					{
						debug ("The handler indicated that the server is completely done with the request. closing socket ...");
						exchange.close();
					}
					else
						debug ("The handler indicated that the server isn't completely done with the request, keeping the socket open");
				}
				else
					debug ("Error: no HTTP handler installed!");
				}
				else
				{
					debug("CTATHTTPExchange could not be initialized.");
			}
			
			debug("HTTP Exchange handled");
		}
	}

	/**
	 * @return {@link #nowExiting}
	 * @see edu.cmu.hcii.ctat.ExitableServer#isExiting()
	 */
	@Override
	public synchronized boolean isExiting() 
	{
		return nowExiting;
	}

	/**
	 * Set {@link #nowExiting} true and {@link #shutdownTime} to the current time.
	 * Close {@link #serverSocket} to exit the principal loop in {@link #run()}.
	 * @return previous value of {@link #nowExiting} 
	 * @see edu.cmu.hcii.ctat.ExitableServer#startExiting()
	 */
	@Override
	public synchronized boolean startExiting() 
	{
		debug ("startExiting ()");
		
		boolean result = nowExiting;
		nowExiting = true;
		shutdownTime = new Date();
		debug("startExiting() previous nowExiting "+result+", server socket to close "+
				serverSocket);
		try {
			serverSocket.close();
			serverSocket = null;
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return result;
	}
	
	/**
	 * @return the handler
	 */
	public CTATHTTPHandlerInterface getHandler() 
	{
		return handler;
	}
}
