package edu.cmu.hcii.ctat;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 */
public class OnlineOfflineManager extends CTATBase
{	
	private static class StuffSender extends Thread
	{
		public void run()
		{
			while(true)
			{
				if(isOffline())
					return;
				
				Sendable current;
				
				synchronized(stuffToSend)
				{
					if(stuffToSend.isEmpty())
						return;
					
					current = stuffToSend.remove(0);
				}
				
				send(current.url, current.messageBody);
			}
		}
	}
	
	private static class Sendable
	{
		public final String url;
		public final byte[] messageBody;
		
		public Sendable(String url, byte[] messageBody) 
		{ 
			this.url = url; 
			this.messageBody = messageBody; 
		}
	}
	
	// queue of stuff waiting to be sent to a server
	private static List<Sendable> stuffToSend = new LinkedList<Sendable>();	
	private static volatile boolean online = true;	
	private static volatile boolean shouldRemainOnline = false;		
	private static Thread stuffSender = null;
	
	/**
	 * 
	 */
	public OnlineOfflineManager ()
	{
    	setClassName ("OnlineOfflineManager");
    	debug ("OnlineOfflineManager ()");	
	}
	/**
	 * 
	 */
	public static synchronized void goOnline()
	{
		LocalTSSystemTray.getInstance().showOnlineIcon();
		online = true;
		
		if(!stuffToSend.isEmpty() && (stuffSender == null || !stuffSender.isAlive()))
		{
			stuffSender = new StuffSender();
			stuffSender.start();
		}
	}
	/**
	 * 
	 */	
	public static synchronized void goOffline()
	{
		if(!shouldRemainOnline)
		{
			LocalTSSystemTray.getInstance().showOfflineIcon();
			online = false;
		}
		else
		{
			goOnline();
		}
	}
	/**
	 * remain online for the lifetime of the program
	 */	
	public static synchronized void remainOnline()
	{
		shouldRemainOnline = true;
		goOnline();
	}
	/**
	 * 
	 */	
	public static synchronized boolean isOnline()
	{
		return online;
	}
	/**
	 * 
	 */	
	public static synchronized boolean isOffline()
	{
		return !online;
	}
	/**
	 * send message to URL when online mode is restored. Note: if the program 
	 * ends before data is sent, it will be lost.
	 */	
	public static void sendStuffWhenOnline (String url, byte[] messageBody)
	{
		if(online)
		{
			send(url, messageBody);
		}
		else
		{
			synchronized(stuffToSend)
			{
				stuffToSend.add(new Sendable(url, messageBody));
			}
		}
	}
	/**
	 * returns server's response message as byte array
	 */	
	private static byte[] send (String url, byte[] messageBody)
	{
		byte[] response;

		if(url != null)
		{
			try
			{
				URLConnection conn = new URL(url).openConnection();
				
				// write
				if(messageBody != null)
				{
					conn.setDoOutput(true);
					OutputStream out = conn.getOutputStream();
					out.write(messageBody);
				}

				// read
				InputStream in = new BufferedInputStream(conn.getInputStream());
				int b;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while((b = in.read()) != -1)
					baos.write((byte)b);
				in.close();
				response = baos.toByteArray();
			}
			catch(Exception e)
			{
				System.err.println("OnlineOfflineManager: " + e);
				response = null;
			}
		}
		else 
			response = null;
		
		return response;
	}
}
