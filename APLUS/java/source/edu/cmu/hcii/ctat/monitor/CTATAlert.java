/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATAlert.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATAlert.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.4  2012/04/03 18:49:09  vvelsen
 Bunch of small bug fixes to the internal management and housekeeping code. There are still a number of fragile pieces so do not rely on this commit for a live system

 Revision 1.3  2012/03/26 16:17:17  vvelsen
 Added a lot of safety features in case either the monitor or service deamon is shutdown or killed. Please see the CTATDeamon class for more information. Also added more checks and more leniency to the service checker so that it doesn't check to often and so that it has different timeouts for different types of services

 Revision 1.2  2012/03/16 15:18:22  vvelsen
 Lots of small upgrades to our socket infrastructure and internal housekeeping of services.

 Revision 1.1  2012/02/28 21:01:47  vvelsen
 Added alerting and reporting classes that work together with a php script to send email to sys admins in case servers go down. Also added logging.

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.hcii.ctat.monitor;

//import java.io.IOException;
import java.util.ArrayList;

/**
*
*/
public class CTATAlert extends CTATEmail 
{
	private String alertEmail="";
	private String alertSubject="";
	private String alertMessage="";
	
	private ArrayList<String> recipients=null;
	
	protected CTATStreamedTableDiskLogger logger=null;

	/**
	*
	*/	
	public CTATAlert ()
	{  
    	setClassName ("CTATAlert");
    	debug ("CTATAlert ()");
    	
    	recipients=new ArrayList<String> ();
    	logger=new CTATStreamedTableDiskLogger ();
    	
    	startLogging ();
	}
	/**
	*
	*/
	public void startLogging ()
	{
		debug ("startLogging ()");
		
		logger.setFileID("Alert-Log");
	}	
	/**
	*
	*/
	public String getAlertEmail() 
	{
		return alertEmail;
	}
	/**
	*
	*/
	public void setAlertEmail(String alertEmail) 
	{
		//this.alertEmail = alertEmail;
		recipients.add (alertEmail);
	}
	/**
	*
	*/
	public void addAlertEmail(String alertEmail) 
	{
		//this.alertEmail = alertEmail;
		recipients.add (alertEmail);
	}	
	/**
	*
	*/
	public String getAlertSubject() 
	{
		return alertSubject;
	}
	/**
	*
	*/
	public void setAlertSubject(String alertSubject) 
	{
		this.alertSubject = alertSubject;
	}
	/**
	*
	*/
	public String getAlertMessage() 
	{
		return alertMessage;
	}
	/**
	*
	*/
	public void setAlertMessage(String alertMessage) 
	{
		this.alertMessage = alertMessage;
	}
	/**
	*
	*/
	public void report ()
	{
		debug ("report ()");
		
		if (this.getAlertMessage().isEmpty()==true)
		{
			debug ("Error: no message body set, aborting sending");
			return;
		}
		
		/*
		for (int i=0;i<recipients.size();i++)
		{
			String recipient=recipients.get(i);
			
			String result=sendEmail(recipient,this.getAlertSubject(),this.getAlertMessage ());

			if (result==null)
			{
				debug ("Error sending alert report");
			}
			
			logger.addLine (this.getAlertSubject() + " : "  + this.getAlertMessage ());
		}
		*/	
		
		logger.addLine (this.getAlertSubject() + " : "  + this.getAlertMessage ());
	}
} 
