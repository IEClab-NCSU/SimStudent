package edu.cmu.hcii.ctat.monitor;

import edu.cmu.hcii.ctat.CTATDeamon;


/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATVMHook.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATVMHook.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.3  2012/04/30 18:21:39  vvelsen
 Added some support code around the database management system. This will allow users and developers to choose date ranges in the database

 Revision 1.2  2012/04/11 13:20:16  vvelsen
 Some refactoring to allow other servers to derive from CTATDeamon. We should be able now to handle file uploads with crc generation coming from Flex or Flash applications

 Revision 1.1  2012/03/26 16:17:17  vvelsen
 Added a lot of safety features in case either the monitor or service deamon is shutdown or killed. Please see the CTATDeamon class for more information. Also added more checks and more leniency to the service checker so that it doesn't check to often and so that it has different timeouts for different types of services

 $RCSfile: CTATVMHook.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATVMHook.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:

*/

//import edu.cmu.hcii.ctat.CTATDeamon;

/** 
 * @author vvelsen
 *
 */
public class CTATVMHook extends Thread 
{
	private CTATDeamon reporter=null;
	
	/** 
	 * @param reporter
	 */
	public CTATVMHook (CTATDeamon aReporter)
	{
		reporter=aReporter;
	}
	/**
	 * 
	 */
	public void run() 
	{
		if (reporter!=null)
			reporter.writeToCriticalWithTime ("Detected JVM shutdown");
		else
			System.out.println ("Internal error: JVM monitor does not have a reference to the server object");
			
		System.out.println ("Running Clean Up...");			
	}
}
