/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATSocketServerBase.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATSocketServerBase.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.19  2012/10/05 19:49:21  akilbo
 Added some comments

 Revision 1.18  2012/08/09 19:44:34  akilbo
 Fixed Bug in AddClient that was causing the array of clients to beocme aligned incorrectly

 Revision 1.17  2012/08/01 20:48:51  akilbo
 fixed add() on addThread(), should no longer have the possibility  to give indexoutofbounds exceptions when adding a new thread.

 Revision 1.16  2012/08/01 19:28:31  akilbo
 Changed clients array into an dynamically resizing arraylist, may have to change addthread fuction to use .add() instead of .put()

 Revision 1.15  2012/04/25 19:35:47  vvelsen
 Major upgrade to the database system. Also added a Swing based manager for easy access, management and export

 Revision 1.14  2012/04/11 13:20:16  vvelsen
 Some refactoring to allow other servers to derive from CTATDeamon. We should be able now to handle file uploads with crc generation coming from Flex or Flash applications

 Revision 1.13  2012/04/06 17:53:41  vvelsen
 Fixed a bug in the TS where it wouldn't track inactive sessions properly

 Revision 1.12  2012/04/04 19:45:26  vvelsen
 Small fixes to how messages are processed and added logging of XML only to a dedicated file for debugging purposes

 Revision 1.11  2012/03/26 16:17:17  vvelsen
 Added a lot of safety features in case either the monitor or service deamon is shutdown or killed. Please see the CTATDeamon class for more information. Also added more checks and more leniency to the service checker so that it doesn't check to often and so that it has different timeouts for different types of services

 Revision 1.10  2012/03/21 17:14:01  vvelsen
 Many networking fixes. The socket classes have been bolstered and more robust although it will require lots more testing. The housekeeping within the monitor server has been upgraded and cleaned a bit.

 Revision 1.9  2012/03/19 19:38:39  vvelsen
 Added a first very basic version of the database driver using the Java Berkeley DB package.

 Revision 1.8  2012/02/27 18:57:22  vvelsen
 More complete version of our tutoring monitor system. It now gathers and logs data in an efficient manner.

 Revision 1.7  2012/02/21 15:55:41  vvelsen
 Small fixes to the socket and service administration code. Seems we're pretty stable now. Load testing also works in this version.

 Revision 1.6  2012/02/16 19:51:45  vvelsen
 First version that seems to have stable housekeeping of all the services.

 Revision 1.5  2012/02/10 20:54:38  vvelsen
 Started to integrate some of the code developed for the tutor monitor into our main class hierarchy. More to come. Lots more functionality in our low level monitoring code.

 Revision 1.4  2012/02/10 16:12:41  vvelsen
 More detailed information is now passed back and forth. A number of nice tools have been added to wrap XML parsing and processing and a new class to handle socket connections easier.

 Revision 1.3  2012/02/09 19:32:04  vvelsen
 Added some new tools and cleaned up the current code to the point that most of our testing and checking mechanisms are in place or at least stubbed.

 Revision 1.2  2012/02/08 17:37:05  vvelsen
 Connected the incoming service message with a centralized maintenance mechanism that can inform all the monitors all at once.

 Revision 1.1  2012/02/06 20:26:41  vvelsen
 First version of services monitor.

 $RCSfile: CTATSocketServerBase.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATSocketServerBase.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:

   http://www.ibm.com/developerworks/java/library/i-signalhandling/

*/

package edu.cmu.hcii.ctat.monitor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

//import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.hcii.ctat.CTATDeamon;
import edu.cmu.hcii.ctat.CTATLink;
//import edu.cmu.hcii.ctat.CTATDeamon;

/**
*
*/
public class CTATSocketServerBase extends CTATDeamon implements Runnable
{			
	//private CTATTutorMonitorServerThread clients []=new CTATTutorMonitorServerThread [150]; // MAKE THIS DYNAMIC!!
	
	private ArrayList<CTATTutorMonitorServerThread> clients  = new ArrayList<CTATTutorMonitorServerThread>(150);
	private ServerSocket server = null;
	private Thread       thread = null;
	private int clientCount = 0;
	private int localPort=8080;
	
	protected CTATStreamedTableDiskLogger logger=null;
	private Boolean logXML=false;

	/**
	*
	*/	
	public CTATSocketServerBase ()
	{  
    	setClassName ("CTATSocketServerBase");
    	debug ("CTATSocketServerBase ()");    	
    	
    	logger=new CTATStreamedTableDiskLogger ();
    	logger.setIncludeDate(false);    	    	
	}
	/**
	*
	*/	
	public int getLocalPort() 
	{
		return localPort;
	}
	/**
	*
	*/	
	public void setLocalPort (int localPort) 
	{
		this.localPort = localPort;
	}
	/**
	*
	*/	
	public Boolean getLogXML() 
	{
		return logXML;
	}
	/**
	*
	*/	
	public void setLogXML(Boolean logXML) 
	{
		this.logXML = logXML;
		
		if (this.logXML==true)
		{
			logger.setFileID("XML-Log");			
		}
	}	
	/**
	*
	*/
	public Boolean runServer ()
	{
		debug ("runServer ()");
		
		setLocalPort (getLocalPort());

		try	     
		{  
			debug ("Binding to port " + getLocalPort() + ", please wait  ...");
			
	        server = new ServerSocket (getLocalPort());
	        	        	       
	        start ();
	        
	        debug ("Server started: " + server.toString());
		}
		catch(IOException ioe)
		{  
			debug ("Can not bind to port " + getLocalPort() + ": " + ioe.getMessage());
			return (false);
		}		
		
		return (true);
	}
	/**
	*
	*/	
	public void run()
	{  
		debug ("run ()");
		
		while (thread != null)
		{  
			try
			{  
				debug ("Waiting for a client ...");
				
				addThread (server.accept()); 
			}
			catch(IOException ioe)
			{  
				debug ("Server accept error: " + ioe); 
				stop(); 
			}
		}
		
		debug ("Exiting server ...");
		
		shutdown ();
	}
	/**
	*
	*/
	protected void shutdown ()
	{
		debug ("shutdown ()");
		
		// implement in child class
	}
	/**
	*
	*/	   
	public void start()  
	{ 
		debug ("start ()");
		
		if (thread == null)
		{  
			thread = new Thread(this); 
			thread.start();
		} 
	}
	/**
	*
	*/	   
	public void stop()   
	{ 
		debug ("stop ()");
		
		if (thread != null)
		{  
			CTATTutorMonitorServerThread stopper=(CTATTutorMonitorServerThread) thread;
			stopper.stopThread (); 
			thread = null;
		}
	}
	/**
	*
	*/	   
	public CTATTutorMonitorServerThread getClientThread(int ID)
	{  
		debug ("getClientThread ()");
		
		for (int i = 0; i < clientCount; i++)
		{
		//	if (clients[i].getID()==ID)
			if (clients.get(i).getID()==ID)
			{
				//return (clients [i]);
				return(clients.get(i));
			}
		}
		   
		return null;
	}	
	/**
	*
	*/	   
	private int findClient(int ID)
	{  
		debug ("findClient ()");
		
		for (int i = 0; i < clientCount; i++)
		{
			//if (clients[i].getID()==ID)
			if(clients.get(i).getID()==ID)
				
				return i;
		}
		   
		return -1;
	}
	/**
	*
	*/
	public void sendAllSockets (String message)
	{
		debug ("sendAllSockets ()");
		
		logger.addLine("[OUT] <?xml version=\"1.0\" encoding=\"UTF-8\"?>" + message);
		
		for (int i = 0; i < clientCount; i++)
		{
			//clients [i].send("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+message);
			clients.get(i).send("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+message);
		}						
	}
	/**
	*
	*/	
	public void sendClient (int ID,String message)
	{
		debug ("sendClient ("+ID+")");
		
		if (findClient(ID)==-1)
		{
			debug ("Error: unable to find socket thread with id: " + ID);
			return;
		}
		
		logger.addLine("[OUT] <?xml version=\"1.0\" encoding=\"UTF-8\"?>" + message);
				
		//clients [findClient(ID)].send("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+message);
		clients.get(findClient(ID)).send("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+message);
	}
	/**
	 * 
	 */
	public void sendAllMonitors (String aMessage)
	{
		// to be implemented in child class, called by CTATServiceChecker
	}
	/**
	*
	*/	   
	public synchronized void handle (int ID, String input)
	{  
		debug ("handle () <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		
		if (input==null)
		{
			return;
		}
		
		debug (input);
		
		if (input.equals("")==true)
		{
			debug ("Received empty string, removing client ...");
			remove (ID);
			return;
		}
		
		if (input.indexOf("<policy-file-request/>")!=-1)
		{
			debug ("Processing policy request ...");
			sendClient (ID,CTATLink.crossDomainPolicy);
			//sendServers (ID);
			return;
		}
		
		debug ("Moving data processing to higher level ...");
		
		logger.addLine("[IN] <?xml version=\"1.0\" encoding=\"UTF-8\"?>" + input);
		debug("Input Recieved: \"" + input +"\"");
		
		CTATXMLBase helper=new CTATXMLBase ();
				
		Document document=helper.loadXMLFromString (input);		
		
		if (document!=null)
		{
			Element root=document.getDocumentElement();
		  
			fromXML (ID,root);
		}	
	}
	/**
	 * 
	 */
	 public Boolean fromXML (int ID,Element root)
	 {
		 debug ("fromXML ()");
	  	  
		 // Override this!
		 
		 return (true);
	 }
	 /**
	  *
	  */	   
	 public void shutdownService (int ID)
	 {  
		 debug ("shutdownService ("+ID+")");
		 
		 // Implement in child class!!
	 }
	 /**
	  *
	  */	   
	 public synchronized void remove(int ID)
	 {  
		 debug ("remove ("+ID+")");
		
		 shutdownService (ID);
		
		 int pos = findClient(ID);
		
		 if (pos >= 0)
		 {  
			// CTATTutorMonitorServerThread toTerminate = clients[pos];
			 CTATTutorMonitorServerThread toTerminate = clients.get(pos);
			 debug ("Removing client thread " + ID + " at " + pos);
			
			 if (pos < clientCount-1)
			 {
				 for (int i = pos+1; i < clientCount; i++){
					  //clients[i-1] = clients[i];
					 clients.set(i-1, clients.get(i));
				 }
					
					 
			 }
			 
			 clientCount--;
	         
			 try
			 {  
				 toTerminate.close(); 
			 }
			 catch(IOException ioe)
			 {  
				 debug ("Error closing thread: " + ioe); 
			 }
	         
			 toTerminate.stopThread(); 
		 }
	 }
	 /**
	  *
	  */	   	
	 private void addThread(Socket socket)
	 {  
		 debug ("addThread ()");
		
		 //if (clientCount < clients.length)
		 //{
			debug ("Client accepted: " + socket);
			
			CTATTutorMonitorServerThread client= new CTATTutorMonitorServerThread (this, socket);
			//clients[clientCount]=client;
			
			//add client at client count index
			clients.add(clientCount,client);
			debug(client.toString());
			
			try
			{  
				//clients[clientCount].open(); 
	            //clients[clientCount].start();  
				clients.get(clientCount).open();
				clients.get(clientCount).start();
				
	            clientCount++; 
			}
			catch(IOException ioe)
			{  
				debug ("Error opening thread: " + ioe); 
			} 
		/*}
		else
	         debug ("Client refused: maximum " + clients.length + " reached.");
	         */
	}	
}
