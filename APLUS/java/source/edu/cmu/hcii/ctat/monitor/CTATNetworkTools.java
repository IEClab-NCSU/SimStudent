/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATNetworkTools.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATNetworkTools.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.4  2012/04/25 19:35:47  vvelsen
 Major upgrade to the database system. Also added a Swing based manager for easy access, management and export

 Revision 1.3  2012/04/03 18:49:09  vvelsen
 Bunch of small bug fixes to the internal management and housekeeping code. There are still a number of fragile pieces so do not rely on this commit for a live system

 Revision 1.2  2012/02/10 16:12:41  vvelsen
 More detailed information is now passed back and forth. A number of nice tools have been added to wrap XML parsing and processing and a new class to handle socket connections easier.

 Revision 1.1  2012/02/09 19:32:04  vvelsen
 Added some new tools and cleaned up the current code to the point that most of our testing and checking mechanisms are in place or at least stubbed.

 $RCSfile: CTATNetworkTools.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATNetworkTools.java,v $ 
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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.hcii.ctat.CTATLink;

/**
 * 
 */
public class CTATNetworkTools extends CTATBase
{		
	/**
	*
	*/	
	public CTATNetworkTools ()
	{  
    	setClassName ("CTATNetworkTools");
    	//debug ("CTATNetworkTools ()");

	}
	/**
	*
	*/	
	public void showAddresses ()
	{
		debug ("showAddresses ()");
		
		Enumeration <NetworkInterface>netInterfaces=null;
		
		try 
		{
			debug ("Obtaining network interfaces ...");
			netInterfaces = NetworkInterface.getNetworkInterfaces();
		} 
		catch (SocketException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		InetAddress ip =null;
		
		debug ("Finding proper host info in network interfaces ...");
		
		Boolean found=false;
		
		while ((netInterfaces.hasMoreElements()) && (found==false))
		{
			NetworkInterface ni=(NetworkInterface) netInterfaces.nextElement();
			
			try 
			{
				if ((ni.isVirtual()==false) && (ni.isUp()==true))
				{
					debug ("Examining interface: "+ni.getName());
				
					Enumeration <InetAddress>ips=ni.getInetAddresses();
				
					while ((ips.hasMoreElements()) && (found==false))
					{
						ip=(InetAddress) ips.nextElement();
					
						debug ("isSiteLocalAddress: " + ip.isSiteLocalAddress()+", isLoopbackAddress: "+ip.isLoopbackAddress()+","+ip.getHostAddress());				
					}
				}
			} 
			catch (SocketException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}			
	}
	/**
	*
	*/	
	public String getHostname() 
	{
		debug ("getHostname ()");
		
		if (CTATLink.hostName.equals("localhost")==false)
		{
			debug ("Hostname already configured");
			return (CTATLink.hostName);
		}
		
		Enumeration <NetworkInterface>netInterfaces=null;
		
		try 
		{
			debug ("Obtaining network interfaces ...");
			netInterfaces = NetworkInterface.getNetworkInterfaces();
		} 
		catch (SocketException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		InetAddress ip =null;
		
		debug ("Finding proper host info in network interfaces ...");
		
		Boolean found=false;
		
		while ((netInterfaces.hasMoreElements()) && (found==false))
		{
			NetworkInterface ni=(NetworkInterface) netInterfaces.nextElement();
			
			try 
			{
				if ((ni.isVirtual()==false) && (ni.isUp()==true))
				{
					debug ("Examining interface: "+ni.getName());
				
					Enumeration <InetAddress>ips=ni.getInetAddresses();
				
					while ((ips.hasMoreElements()) && (found==false))
					{
						ip=(InetAddress) ips.nextElement();
					
						debug (ip.isSiteLocalAddress()+","+ip.isLoopbackAddress()+","+ip.getHostAddress());
				
						if (!ip.isLinkLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":")==-1)
						{
							if (ip.isSiteLocalAddress()==true)
							{
								if (ip.getHostAddress().equals("127.0.0.1")==false)
								{
									debug ("Interface "+ni.getName()+" seems to be the proper InternetInterface. I'll take it...");
									found=true;
								}	
							}	
							else
							{
								debug ("Interface "+ni.getName()+" seems to be the proper InternetInterface. I'll take it...");
								found=true;								
							}
						}
					}
				}
			} 
			catch (SocketException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}	
		
		if (ip!=null)
		{
			if ((CTATLink.hostName.equals ("localhost")==true) || (CTATLink.hostName.equals ("localhost.localdomain")==true) || (CTATLink.hostName.equals ("127.0.0.1")==true))
			{
				debug ("Detected that the hostname is a local or loopback address, reverting to canonical name ...");
				
				if ((ip.getCanonicalHostName ().equals ("localhost")==true) || (ip.getCanonicalHostName ().equals ("localhost.localdomain")==true) || (ip.getCanonicalHostName ().equals ("127.0.0.1")==true))
				{
					debug ("Detected that the canonical name is a local or loopback address, reverting to ip address ...");
				
					CTATLink.hostName=ip.getHostAddress();
				}
				else
					CTATLink.hostName=ip.getCanonicalHostName();
							    
		    	debug ("hostname: "+ CTATLink.hostName); 			
			}
		}	
		
		return CTATLink.hostName;
	}	
}
