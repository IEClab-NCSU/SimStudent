/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATTutorMonitorServerThread.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATTutorMonitorServerThread.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.4  2012/03/21 17:14:01  vvelsen
 Many networking fixes. The socket classes have been bolstered and more robust although it will require lots more testing. The housekeeping within the monitor server has been upgraded and cleaned a bit.

 Revision 1.3  2012/02/27 18:57:22  vvelsen
 More complete version of our tutoring monitor system. It now gathers and logs data in an efficient manner.

 Revision 1.2  2012/02/16 19:51:45  vvelsen
 First version that seems to have stable housekeeping of all the services.

 Revision 1.1  2012/02/06 20:26:41  vvelsen
 First version of services monitor.

 $RCSfile: CTATTutorMonitorServerThread.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATTutorMonitorServerThread.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.hcii.ctat.monitor;

import java.net.*;
import java.io.*;

//import edu.cmu.pact.Utilities.SocketReader;

import edu.cmu.hcii.ctat.CTATBase;
//import edu.cmu.pact.Utilities.trace;

/**
*
*/
public class CTATTutorMonitorServerThread extends Thread
{  
	private CTATSocketServerBase	server	 =null;
	private CTATStreamedSocket		helper	=null;
	private Socket           		socket	 =null;
	private int              		ID		 =-1;
	private DataInputStream  		streamIn =null;
	private PrintWriter				outWriter=null;
	private Boolean					threadStopped=false;
	private String					ip="127.0.0.1";
	private Long					msgCount=(long) 0;
	
	/**
	 *
	 */	
	public CTATTutorMonitorServerThread (CTATSocketServerBase _server, Socket _socket)
	{  
		super();
		
		debug ("CTATTutorMonitorServerThread ()");
		
		server = _server;
		socket = _socket;
		ID     = socket.getPort();
		
		SocketAddress addr=socket.getRemoteSocketAddress();		
		ip=addr.toString().replaceAll("/","");
		
		helper=new CTATStreamedSocket ();
	}
	/**
	 *
	 */
	private void debug (String aMessage)
	{
		CTATBase.debug ("CTATTutorMonitorServerThread",aMessage);
	}
	/**
	 *
	 */	
	public String getIp() 
	{
		return ip;
	}	
	/**
	 *
	 */	
	public void send(String msg)
	{   
		debug ("send () >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
		debug (msg);
						
		if (outWriter==null)
		{
			debug ("Error: no open output stream available");
			return;
		}
			
		outWriter.print (msg);
		
		if (outWriter.checkError()==true)
		{
			debug ("Error printing message to socket");
			return;
		}
		
		outWriter.write('\0');
		
		if (outWriter.checkError()==true)
		{
			debug ("Error writing message to socket");
			return;
		}		
		
		outWriter.flush();
		
		if (outWriter.checkError()==true)
		{
			debug ("Error flushing socket");
			return;
		}		
	}
	/**
	 *
	 */
	public void stopThread ()
	{
		debug ("stopThread ()");
		threadStopped=true;
	}
	/**
	 *
	 */	
	public int getID()
	{  
		return ID;
	}
	/**
	 *
	 */	
	public void run()
	{  
		debug ("Server Thread " + ID + " running.");
		
		while (true && (threadStopped==false))
		{  
			try
			{  				
				String data=helper.readToEom (streamIn,0);
				if (data==null)
				{
					debug ("Null data received from readToEom, closing socket");
					
		            stopThread ();				
		            server.remove(ID);
				}
				else
				{
					msgCount++;
					
					debug ("Received " + msgCount + " for this thread so far");
					
					server.handle(ID,data);
				}
			}
			catch(IOException ioe)
			{  
				debug (ID + " ERROR reading: " + ioe.getMessage());
	            stopThread ();				
	            server.remove(ID);
			}
		}
	}
	/**
	 *
	 */	
	public void open() throws IOException
	{  
		debug ("open ()");
				
		streamIn = new DataInputStream (new BufferedInputStream(socket.getInputStream()));
		
		outWriter=new PrintWriter (new OutputStreamWriter (socket.getOutputStream(),"UTF-8"));
	}
	/**
	 *
	 */	
	public void close() throws IOException
	{  
		debug ("close ()");
				
		if (socket != null) 
			socket.close();
		
		if (streamIn != null)  
			streamIn.close();

		if (outWriter!=null)
			outWriter.close();
	}
}
