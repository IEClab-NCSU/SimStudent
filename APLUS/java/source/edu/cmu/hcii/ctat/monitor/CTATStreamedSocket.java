/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATStreamedSocket.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATStreamedSocket.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.8  2012/03/27 17:13:30  vvelsen
 Added more safety features in case either the deamon or the monitor server goes down. Added redundency and failover mechanisms. Everything can now be configured either through command line arguments or a configuration file.

 Revision 1.7  2012/03/26 16:17:17  vvelsen
 Added a lot of safety features in case either the monitor or service deamon is shutdown or killed. Please see the CTATDeamon class for more information. Also added more checks and more leniency to the service checker so that it doesn't check to often and so that it has different timeouts for different types of services

 Revision 1.6  2012/03/21 17:14:01  vvelsen
 Many networking fixes. The socket classes have been bolstered and more robust although it will require lots more testing. The housekeeping within the monitor server has been upgraded and cleaned a bit.

 Revision 1.5  2012/03/19 19:38:39  vvelsen
 Added a first very basic version of the database driver using the Java Berkeley DB package.

 Revision 1.4  2012/03/16 15:18:22  vvelsen
 Lots of small upgrades to our socket infrastructure and internal housekeeping of services.

 Revision 1.3  2012/02/27 18:57:22  vvelsen
 More complete version of our tutoring monitor system. It now gathers and logs data in an efficient manner.

 Revision 1.2  2012/02/16 19:51:45  vvelsen
 First version that seems to have stable housekeeping of all the services.

 Revision 1.1  2012/02/10 16:12:41  vvelsen
 More detailed information is now passed back and forth. A number of nice tools have been added to wrap XML parsing and processing and a new class to handle socket connections easier.

 $RCSfile: CTATStreamedSocket.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATStreamedSocket.java,v $ 
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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import edu.cmu.hcii.ctat.CTATBase;

public class CTATStreamedSocket extends CTATBase implements Runnable
{	
	private String data="";
	private PrintWriter outWriter=null;
	private Socket clientSocket=null;
	private BufferedReader in = null;
	private int socketTimeout=2000; // Just two seconds, we want to keep it tight
	private CTATMessageReceiver receiver;
	private Boolean threadRunning=false;
	
	/**
	*
	*/	
	public CTATStreamedSocket ()
	{  
    	setClassName ("CTATStreamedSocket");
    	debug ("CTATStreamedSocket ()");

	}
	
	/**
	 * To convert the InputStream to String we use the
	 * Reader.read(char[] buffer) method. We iterate until the
	 * Reader return -1 which means there's no more data to
	 * read. We use the StringWriter class to produce the string.
	*/	
    public String convertStreamToString (InputStream is)
    {
    	debug ("convertStreamToString ()");
    	
        if (is != null) 
        {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            
            try 
            {
                Reader reader=null;
                
				try 
				{
					reader = new BufferedReader (new InputStreamReader(is, "UTF-8"));
				}
				catch (UnsupportedEncodingException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					debug ("UnsupportedEncodingException");
				}
                int n;
                
                try 
                {
					while ((n = reader.read(buffer)) != -1) 
					{
					    writer.write (buffer, 0, n);
					}
				} 
                catch (IOException e) 
                {
					// TODO Auto-generated catch block
					e.printStackTrace();
					debug ("IOException");
				}
            } 
            finally 
            {
                try 
                {
					is.close();
				} 
                catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					debug ("IOException");
				}
            }
            
            return writer.toString();
        } 
        else 
        {        
            return "";
        }
    }	
	/**
	 * 
	 */
	public String sendAndReceiveXML (String aHost,int aPort,String aMessage)
	{
		debug ("sendAndReceive ("+aHost+","+aPort+")");
			
		return (sendAndReceive (aHost,aPort,"<?xml version=\"1.0\" encoding=\"utf-8\"?>"+aMessage));		
	}	        
	/**
	 * 
	 */
	public String sendAndReceive (String aHost,int aPort,String aMessage)
	{
		debug ("sendAndReceive ("+aHost+","+aPort+")");
		
		InputStream is=null;
		
		if (clientSocket==null)
		{
			outWriter=null; // Reset
			in=null; // Reset
					
			try
			{				
				SocketAddress sockaddr = new InetSocketAddress(aHost,aPort);
				clientSocket=new Socket();
				clientSocket.connect (sockaddr,socketTimeout);				
				
				outWriter = new PrintWriter (clientSocket.getOutputStream(),true);
				is=clientSocket.getInputStream();
				
				if (is==null)
				{
					return ("");
				}
			} 
			catch (UnknownHostException e) 
			{
				debug ("Unknown host: " + aHost);
				return ("");
			} 
			catch  (IOException e) 
			{
				debug ("No I/O or connection timeout");
				return ("");
			}
		}	
				
		outWriter.println (aMessage+"\0");
		
		debug ("Sent: " + aMessage);
		
		data=convertStreamToString (is);
				
		return data;
	}
	/**
	 * 
	 */
	public Boolean sendAndKeepOpen (String aHost,
									int aPort,
									String aMessage,
									CTATMessageReceiver aReceiver)
	{
		debug ("sendAndKeepOpen ("+aHost+","+aPort+")");
						
		receiver=aReceiver;
		
		if (clientSocket==null)
		{
			debug ("Creating connection ...");
			
			close (); // Or a reset. Same thing
			
			try
			{
				debug ("Creating socket ...");
								
				SocketAddress sockaddr = new InetSocketAddress(aHost,aPort);
				clientSocket=new Socket();
				clientSocket.connect (sockaddr,socketTimeout);
								
				debug ("Created socket, creating printwriter ...");
				
				outWriter=new PrintWriter (clientSocket.getOutputStream(),true);
								
				debug ("Created socket, connecting input stream ...");
				
				in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));				
			} 
			catch (UnknownHostException e) 
			{
				debug ("Unknown host: " + aHost);
				
				close ();
				
				if (receiver!=null)
					receiver.handleConnectionClosed();	
				
				return (false);
			} 
			catch (SecurityException e)
			{
				debug ("A security exception occurred while connecting to the remote host");
				
				close ();
				
				if (receiver!=null)
					receiver.handleConnectionClosed();	
				
				return (false);				
			}
			catch  (IOException e) 
			{
				debug ("No I/O or connection timeout");
				
				close ();
				
				if (receiver!=null)
					receiver.handleConnectionClosed();	
				
				return (false);
			}
								
			// Create the thread supplying it with the runnable object
			Thread thread=new Thread(this);

			threadRunning=true;
			
			// Start the thread
			thread.start();			
		}

		debug ("Connection open, sending ...");
		
		if (outWriter!=null)
		{
			outWriter.print (aMessage+"\0");
			outWriter.flush();
			debug ("Sent " +(aMessage.length()+1)+" characters");
		}
		else
		{
			debug ("Internal error: outwriter is null");
			return (false);
		}
							
		return (true);
	}	
	/**
	 * 
	 */
	/*
	public String receiveFromOpenSocket ()
	{
		if (is!=null)
		{
			return (convertStreamToString (is));
		}
		
		return (null);
	}
	*/
	/**
	 * 
	 */
	public void close ()
	{
		debug ("close ()");
		
		if (clientSocket!=null)
		{
			try 
			{
				clientSocket.close();
			} 
			catch (IOException e) 
			{				
				debug ("Error closing socket");
				e.printStackTrace();
			}			
		}
		
		if (in!=null)
		{
			try 
			{
				in.close();
			} 
			catch (IOException e) 
			{
				debug ("Error closing input stream");
				e.printStackTrace();
			}
		}
		
		outWriter=null;
		in=null;
		clientSocket=null;	
		threadRunning=false;		
	}	
	/**Read the characters from a Reader into a String. Reads until receives the
	 * given end-of-message character, which it consumes without returning.
	 * 
	 * @param rdr
	 *            Reader to read; should be BufferedReader or equivalent for
	 *            efficiency
	 * @param eom
	 *            end-of-message character; not returned with String result
	 * @return String with all characters from Reader
	 * @exception IOException
	 */
	private String readToEom(Reader rdr, int eom) throws IOException 
	{
		debug ("readToEom (Reader rdr, int eom)");
		
		StringWriter result = new StringWriter(4096);
		int c;
		int count = 0;
		
		while (0<=(c=rdr.read()) && (c!=eom)) 
		{
			count++;
			
			if (c == '\r')
			{
				debug ("CR return is found at offset " + count);
			}
			
			//debug ("C: " + c);
			
			result.write(c);
		}
		
		if (count==0)
			return (null);
			
		return result.toString();
	}	
	/**
	 * 
	 * Read the characters from a Reader into a String. Reads until receives the
	 * given end-of-message character, which it consumes without returning.
	 * 
	 * @param rdr
	 *            Reader to read; should be DataInputStream or equivalent for
	 *            efficiency
	 * @param eom
	 *            end-of-message character; not returned with String result
	 * @return String with all characters from Reader
	 * @exception IOException
	 */
	public String readToEom (DataInputStream rdr, int eom) throws IOException 
	{
		debug ("readToEom (DataInputStream rdr, int eom)");
		
		StringWriter result=new StringWriter (4096);
		
		int c;
		int count = 0;
		
		while (0 <= (c = rdr.read()) && c != eom) 
		{			
			count++;
			
			if (c == '\r')
			{
				debug ("CR return is found at offset " + count);
			}	
			
			result.write(c);			
		}
		
		if (count==0)
			return (null);
				
		return result.toString();
	}
	/**
	 * 
	 */
	@Override
	public void run() 
	{
		debug ("run ()");
						
		while (threadRunning==true)
		{
			try 
			{
				//in.ready();
				data=readToEom (in,0);
			} 
			catch (IOException e) 
			{
				//e.printStackTrace();

				debug ("Error reading data, most likely connection reset or closed");
				
				close ();
				
				if (receiver!=null)
					receiver.handleConnectionClosed();			

				threadRunning=false;	
				
				return;				
			}
			
			if (data!=null)
			{
				if (receiver!=null)
				{
					if (data.isEmpty()==true)
					{
						debug ("Internal error: empty string received!");
						close ();
					}
					else
						receiver.handleIncomingData(data);
				}					
			}			
			else
			{
				debug ("Received null data, probably closed the socket");
																	
				if (receiver!=null)
					receiver.handleConnectionClosed();
				
				close ();
								
				return;
			}
		}		
	}		
}
