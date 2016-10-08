/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATServiceChecker.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATServiceChecker.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.19  2012/09/06 17:30:45  akilbo
 Added some logic changes to run logging more efficiently

 Revision 1.18  2012/07/30 20:06:29  akilbo
 Redid logic on HTTP checker, now properly checks status of web service and displays number of characters received and if the service goes down. Fixed Bug that rapidly switched up/down status of HTTP checker when timeout occurred

 Revision 1.17  2012/04/25 19:35:47  vvelsen
 Major upgrade to the database system. Also added a Swing based manager for easy access, management and export

 Revision 1.16  2012/04/18 19:20:17  vvelsen
 Bunch of fixes to the logic. The code should also be a tiny bit faster.

 Revision 1.15  2012/04/06 17:53:41  vvelsen
 Fixed a bug in the TS where it wouldn't track inactive sessions properly

 Revision 1.14  2012/04/03 18:49:09  vvelsen
 Bunch of small bug fixes to the internal management and housekeeping code. There are still a number of fragile pieces so do not rely on this commit for a live system

 Revision 1.13  2012/03/30 18:26:21  vvelsen
 A bunch of changes to the way the server checks and verifies if services are down. There is still a tweak that needs to be made to TS testing since it doesn't always accurately detect if a TS is down

 Revision 1.12  2012/03/29 18:58:15  vvelsen
 Lots of bug fixes in the data representation and serialization of services.

 Revision 1.11  2012/03/26 16:17:17  vvelsen
 Added a lot of safety features in case either the monitor or service deamon is shutdown or killed. Please see the CTATDeamon class for more information. Also added more checks and more leniency to the service checker so that it doesn't check to often and so that it has different timeouts for different types of services

 Revision 1.10  2012/03/21 17:14:01  vvelsen
 Many networking fixes. The socket classes have been bolstered and more robust although it will require lots more testing. The housekeeping within the monitor server has been upgraded and cleaned a bit.

 Revision 1.9  2012/03/19 19:38:39  vvelsen
 Added a first very basic version of the database driver using the Java ho DB package.

 Revision 1.8  2012/03/16 15:18:22  vvelsen
 Lots of small upgrades to our socket infrastructure and internal housekeeping of services.

 Revision 1.7  2012/02/28 21:01:47  vvelsen
 Added alerting and reporting classes that work together with a php script to send email to sys admins in case servers go down. Also added logging.

 Revision 1.6  2012/02/27 18:57:22  vvelsen
 More complete version of our tutoring monitor system. It now gathers and logs data in an efficient manner.

 Revision 1.5  2012/02/16 19:51:45  vvelsen
 First version that seems to have stable housekeeping of all the services.

 Revision 1.4  2012/02/10 20:54:38  vvelsen
 Started to integrate some of the code developed for the tutor monitor into our main class hierarchy. More to come. Lots more functionality in our low level monitoring code.

 Revision 1.3  2012/02/10 16:12:41  vvelsen
 More detailed information is now passed back and forth. A number of nice tools have been added to wrap XML parsing and processing and a new class to handle socket connections easier.

 Revision 1.2  2012/02/09 19:32:04  vvelsen
 Added some new tools and cleaned up the current code to the point that most of our testing and checking mechanisms are in place or at least stubbed.

 Revision 1.1  2012/02/08 17:37:05  vvelsen
 Connected the incoming service message with a centralized maintenance mechanism that can inform all the monitors all at once.

 $RCSfile: CTATServiceChecker.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATServiceChecker.java,v $ 
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.TimerTask;

import edu.cmu.hcii.ctat.CTATBase;

/**
 * 
 */
public class CTATServiceChecker extends TimerTask
{		
	private CTATServiceRegistry sRegistry=null;
	private ArrayList <CTATMonitorEntry> monitors=null;
	private CTATSocketServerBase server=null;
	private Boolean checking=false;
	private CTATStreamedSocket socketHelper=null;
	private CTATAlert alert=null;
	
	private StringBuffer reporter=null;
	
	private int monitorUpdateInterval=5; // 5 seconds
	private int monitorUpdateIndex   =0; // To keep track of when to send memory and server status info to all monitors
	
	private int tcpCheckInterval=5; // 5 seconds
	private int tcpCheckIndex   =0; // To keep track of when to check for services connected through streamed sockets
	
	private int httpCheckInterval=10; // 10 seconds
	private int httpCheckIndex   =0; // To keep track of when to check for http availabilitty
	
	private int msCheckInterval=10; // 10 seconds
	private int msCheckIndex   =0; // To keep track of machines through a call to PS
	
	/**
	*
	*/	
	public CTATServiceChecker (CTATSocketServerBase aServer,CTATAlert anAlerter)
	{  
    	debug ("CTATServiceChecker ()");
    	setServer(aServer);
    	socketHelper=new CTATStreamedSocket ();
    	alert=anAlerter;
    	reporter=new StringBuffer ();
	}
	/**
	*
	*/
	protected void debug (String aMessage)
	{
		CTATBase.debug ("CTATServiceChecker",aMessage);
	}
	/**
	 * 
	 */
	public void setTCPCheckInterval (int aValue)
	{
		tcpCheckInterval=aValue;
	}
	/**
	 * 
	 */
	public void setHTTPCheckInterval (int aValue)
	{
		httpCheckInterval=aValue;
	}	
	/**
	 * 
	 */
	public void setMSCheckInterval (int aValue)
	{
		msCheckInterval=aValue;
	}	
	/**
	*
	*/
	private void resetReport ()
	{
		reporter=new StringBuffer ();
		reporter.append("");
	}
	/**
	*
	*/
	private void appendReport (String aMessage)
	{
		if (reporter==null)
			resetReport ();
		
		reporter.append (aMessage+". \n");
	}
	/**
	*
	*/
	private Boolean shouldReport ()
	{
		if (reporter==null)
			return (false);
			
		if (reporter.toString().isEmpty()==false)
			return (true);
		
		return (false);
	}
	/**
	*
	*/
	private String getReport ()
	{
		if (reporter==null)
			return ("");
			
		return (reporter.toString());
	}
	/**
	*
	*/	
	public CTATServiceRegistry getsRegistry() 
	{
		return sRegistry;
	}
	/**
	*
	*/	
	public void setsRegistry(CTATServiceRegistry sRegistry) 
	{
		this.sRegistry = sRegistry;
	}
	/**
	*
	*/	
	public ArrayList <CTATMonitorEntry> getMonitors() 
	{
		return monitors;
	}
	/**
	*
	*/	
	public void setMonitors(ArrayList <CTATMonitorEntry> monitors) 
	{
		this.monitors = monitors;
	}
	/**
	*
	*/	
	public CTATSocketServerBase getServer() 
	{
		return server;
	}
	/**
	*
	*/	
	public void setServer(CTATSocketServerBase server) 
	{
		this.server = server;
	}	
	/**
	*
	*/
	
	private Boolean executeTSCheck (CTATTSEntry ts)
	{
		//debug ("executeTSCheck ()");
		
		ts.setUpdateDelta(tcpCheckInterval);
		
		if (ts.isUpdating()==true)
		{
			debug ("This TS is currently updating, leaving alone for now ...");
			return (false);
		}
		
		tcpCheckIndex++;
		
		if (tcpCheckIndex<tcpCheckInterval)
		{
			//debug ("Current tcpCheckIndex: " + tcpCheckIndex);
			return (false);
		}
		else
			tcpCheckIndex=0; // reset
			
		//debug ("executeTSCheck () Activated");		
		
		resetReport ();
								
		Boolean result=ts.checkService();
		
		if (result==true)
		{
			ts.setUpdated(true);
			
			if (ts.getReport()!=null)
			{
				//debug("get ts report is not null");
				
				if (ts.getReport().isEmpty()==false)
				{
					debug("appending report with \"" + ts.getReport() + "\"");
					
					appendReport (ts.getReport ());
				}
				//else
					//debug("the ts report is empty");
			}
		}
		
		return (result);
	}
	/**
	*
	*/
	private Boolean executeHTTPCheck (CTATWSEntry ws)
	{
		//debug ("executeHTTPCheck ()");
		
		ws.setUpdateDelta(httpCheckInterval);
		
		httpCheckIndex++;
		
		if (httpCheckIndex<httpCheckInterval)
		{
			//debug ("Current httpCheckIndex: " + httpCheckIndex);
			return (false);
		}
		else
			httpCheckIndex=0; // reset
			
		//debug ("executeHTTPCheck () Activated");
		
		String oldStatus=ws.getStatus();
		
		//debug("old status is " + oldStatus);
		
		//ws.setStatus("UP");
		
		try
		{
			URL url=new URL("http://"+ws.getHostname()+":"+ws.getPort());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("GET");
			
			//debug ("Connecting to: " + url.toExternalForm());
			
			conn.connect();
			
			InputStream in=conn.getInputStream();

			String text=socketHelper.convertStreamToString (in);
			
			conn.disconnect();
			
			if (text.isEmpty()==true)
			{
				
				ws.setLastAmountReceived(0);
				
				//make sure we aren't repeating ourselves
				if((oldStatus.equals("DOWN"))==false)
					ws.setStatus ("DOWN");
				
				if (oldStatus.equals(ws.getStatus())==false)
				{
					// This way we make sure we only report a change in status and not the same thing every second
					appendReport ("Empty text received, marking web service at "+ws.getHostname()+":"+ws.getPort()+" as down");					
				}
			}		
			else
			{
				Integer oldReceived = ws.getLastAmountReceived();
				ws.setLastAmountReceived(text.length());
				
				//make sure we aren't repeating ourselves with a previous "up" status, however if there is a new amount of characters received we want to log that
				if(((oldStatus.equals("UP"))==false) || (oldReceived.equals(ws.getLastAmountReceived())==false)  )
					ws.setStatus("UP");
				
				debug ("Received "+text.length()+" characters");
			
			}
		}
		catch (IOException ex)
		{
			//ex.printStackTrace();
			
			ws.setLastAmountReceived(0);
			
			//make sure we aren't repeating ourselves
			if((oldStatus.equals("DOWN"))==false)
					ws.setStatus ("DOWN");
			
			if (oldStatus.equals(ws.getStatus())==false)
			{
				// This way we make sure we only report a change in status and not the same thing every second			
				appendReport ("IO exception while connecting to webserver at: " + ws.getHostname()+":"+ws.getPort());
			}			
			
			return (true);
		}		
		
		return (false);
	}	
	/**
	*
	*/
	private Boolean executeMSCheck (CTATMachineEntry ms)
	{
		//debug ("executeMSCheck ()");
		
		ms.setUpdateDelta(msCheckInterval);
		
		msCheckIndex++;
		
		if (msCheckIndex<msCheckInterval)
		{
			//debug ("Current msCheckIndex: " + msCheckIndex);
			return (false);
		}
		else
			msCheckIndex=0; // reset
			
		//debug ("executeMSCheck () Activated");			
		
		if (ms.getAutoPS()==true)
		{
			//debug("ms.getAutoPS=true");
			
			int ID=ms.getID(); // get the socket
		
			server.sendClient(ID,"<request command=\"ps\" />");
		}
	
		
		return (false);
	}
	/**
	* Here we manage all the open log files. If a service has a file open and we
	* roll over to the next day the file should be closed and re-opened with a
	* different name.
	*/
	private void checkLogging ()
	{
		//debug("checklogging()");
		
		ArrayList<CTATClientEntry> services=sRegistry.getEntries();
		
		if (services==null){
			debug("null services in checklogging()");
			return;
		}
		
		for (int i=0;i<services.size();i++)
		{
			//debug("checklogging() services#" + i);
			
			CTATClientEntry entry=services.get(i);
			
			//debug(entry.getGuid());
			
			CTATStreamedTableDiskLogger logger=entry.getLogger();
			if (logger!=null){
				
				if(entry.getShouldLog()){
				
				//debug("logger is not null");
				
				logger.checkLogging();
				
				}
			}
		}
	}
	/**
	 * 
	 */
	public void sendAllMonitors (String aMessage)
	{
		//debug ("sendAllMonitors ()");
		
		if (monitors.size()==0)
		{
			//debug ("Currently no monitors registered");			
			return;
		}
		
		if (server==null)
		{
			debug ("Internal error: no server object available to send message to all monitors");			
			return;
		}		
		
		for (int i=0;i<monitors.size();i++)
		{
			CTATMonitorEntry entry=monitors.get(i);
			if (entry.getMonitorType().equals("MONITOR")) // entry could also be a load test
				server.sendClient (entry.getSocketID(),aMessage);
		}		
	}		
	/**
	 * 
	 */
	public void updateMonitors ()
	{
		//debug ("updateMonitors ()");
				
		monitorUpdateIndex++;
		
		if (monitorUpdateIndex>monitorUpdateInterval)
		{
			monitorUpdateIndex=0;
			
			Long memTotal=Runtime.getRuntime().totalMemory();
			Long memMax=Runtime.getRuntime().maxMemory();
			Long memFree=Runtime.getRuntime().freeMemory();
			
			sendAllMonitors ("<status memtotal=\""+memTotal+"\" memmax=\""+memMax+"\" memfree=\""+memFree+"\" />");
		}
	}	
	/**
	*
	*/	
	@Override
	public void run() 
	{
		//debug ("Check ...");
		
		if (checking==true)
		{
			//debug ("Still checking ...");
			return;
		}
		
		checking=true;
		
		checkLogging ();
		
		updateMonitors ();
		
		ArrayList<CTATClientEntry> services=sRegistry.getEntries();
		
		if (server!=null)
		{		
			//debug ("run () -> Checking "+services.size()+" services ...");
			
			Boolean shouldUpdate=false;
				
			for (int i=0;i<services.size();i++)
			{
				CTATClientEntry entry=services.get(i);
				
				if (entry.getClientType().equals("webservice")==true)
				{
					CTATWSEntry ws=(CTATWSEntry) entry;
					if ((executeHTTPCheck (ws)==true) && (shouldUpdate==false))
						shouldUpdate=true;
				}
				
				if (entry.getClientType().equals("tutoringservice")==true)
				{
					CTATTSEntry ts=(CTATTSEntry) entry;
					if ((executeTSCheck (ts)==true) && (shouldUpdate==false))
							shouldUpdate=true;
					//ts.debugStatus ();
				}
				
				if (entry.getClientType().equals("machine")==true)
				{
					CTATMachineEntry ms=(CTATMachineEntry) entry;
					if ((executeMSCheck (ms)==true) && (shouldUpdate==false))
						shouldUpdate=true;
				}
			}
			
			if (shouldUpdate==true)
			{				
				
				
				if (shouldReport ()==true)
				{
					if (alert!=null)
					{
						alert.setAlertSubject("Monitor Report!");
						alert.setAlertMessage(this.getReport ());
						alert.report ();
					}
				}
				
				server.sendAllMonitors (sRegistry.toXMLUpdated());
				//server.sendAllMonitors (sRegistry.toXML());
				sRegistry.resetUpdates ();
			}	
		}	
		
		checking=false;
	}
}
