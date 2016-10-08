/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATStreamedTableDiskLogger.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATStreamedTableDiskLogger.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.4  2012/04/06 17:53:41  vvelsen
 Fixed a bug in the TS where it wouldn't track inactive sessions properly

 Revision 1.3  2012/04/04 19:45:26  vvelsen
 Small fixes to how messages are processed and added logging of XML only to a dedicated file for debugging purposes

 Revision 1.2  2012/04/03 18:49:09  vvelsen
 Bunch of small bug fixes to the internal management and housekeeping code. There are still a number of fragile pieces so do not rely on this commit for a live system

 Revision 1.1  2012/02/28 21:01:47  vvelsen
 Added alerting and reporting classes that work together with a php script to send email to sys admins in case servers go down. Also added logging.

 $RCSfile: CTATStreamedTableDiskLogger.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATStreamedTableDiskLogger.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:

*/

package edu.cmu.hcii.ctat.monitor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.hcii.ctat.CTATDesktopFileManager;
import edu.cmu.hcii.ctat.CTATLink;
import edu.cmu.hcii.ctat.CTATLogSnoopInterface;

/**
* 
*/
public class CTATStreamedTableDiskLogger extends CTATBase implements CTATLogSnoopInterface
{       
	private String fileID="undefined";
	private CTATDesktopFileManager fManager=null;
	private String dateCheck="";
	private Boolean includeDate=true;
	
	/**
	*
	*/
	public CTATStreamedTableDiskLogger ()
	{
    	setClassName ("CTATStreamedTableDiskLogger");
    	debug ("CTATStreamedTableDiskLogger ()");
    	fManager=new CTATDesktopFileManager ();
	}
	/**
	*
	*/
	public Boolean getIncludeDate() 
	{
		return includeDate;
	}
	/**
	*
	*/
	public void setIncludeDate(Boolean includeDate) 
	{
		this.includeDate = includeDate;
	}	
	/**
	*
	*/
	public String getDateCheck() 
	{
		return dateCheck;
	}
	/**
	*
	*/
	public void setDateCheck(String dateCheck) 
	{
		debug ("setDateCheck ("+dateCheck+")");
		
		this.dateCheck = dateCheck;
	}	
	/**
	*
	*/
	public void checkLogging ()
	{
		//debug ("checkLogging ()");
		
		DateFormat dateFormatCheck=new SimpleDateFormat("dd");
		Date date = new Date();
		String check=dateFormatCheck.format(date);
		
		if (check.equals(getDateCheck())==false)
		{
			debug ("Rolling over log file ...");
			
			fManager.closeStream();
			
			if (includeDate==true)
			{
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
																
				fManager.openStream(CTATLink.logdir+"/"+this.fileID+"-"+dateFormat.format(date)+".txt");
			}
			else
				fManager.openStream(CTATLink.logdir+"/"+this.fileID+".txt");
			
			setDateCheck (dateFormatCheck.format(date));
		}
	}
	/**
	*
	*/
	public String getFileID() 
	{
		return fileID;
	}
	/**
	*
	*/
	public void setFileID(String fileID) 
	{
		debug ("setFileID ()");
		
		this.fileID=fileID;
		
		if (this.fileID.equals("undefined")==false)
		{
			if (fManager.isStreamOpen()==false)
			{
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
				Date date = new Date();
				
				DateFormat dateFormatCheck=new SimpleDateFormat("dd");
				setDateCheck (dateFormatCheck.format(date));
												
				if (includeDate==true)
				{																	
					fManager.openStream(CTATLink.logdir+"/"+this.fileID+"-"+dateFormat.format(date)+".txt");
				}
				else
					fManager.openStream(CTATLink.logdir+"/"+this.fileID+".txt");
			}	
		}
	} 
	/**
	* Whatever you do, do not call debug here!! It will get
	* you into an infinite loop if this class is assigned
	* to be the log snooper. See CTATLink and CTATBase
	*/
	public void addLine (String aLine)
	{		
		if (aLine.indexOf('\n')!=-1)
			fManager.writeToStream(aLine);
		else
			fManager.writeToStream(aLine+"\n");
	}
}
