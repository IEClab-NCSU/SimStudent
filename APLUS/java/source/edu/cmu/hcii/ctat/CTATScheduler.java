/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATScheduler.java,v 1.4 2012/05/31 15:09:36 blojasie Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATScheduler.java,v $
 Revision 1.4  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.3  2012/03/16 15:15:28  vvelsen
 Small fixes here and there to the monitor, link and other support classes. Mostly reformatting work.

 Revision 1.2  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.1  2011/02/15 15:20:42  vvelsen
 Added proper datashop logging to disk through http in the built-in web server. Added encryption code for logfiles, text based streams, etc. Added a first update scheduler that can be used to both retrieve new versions of the application as well as new content and student info.

 $RCSfile: CTATScheduler.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATScheduler.java,v $ 
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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.tiling.scheduling.Scheduler;
import org.tiling.scheduling.SchedulerTask;
import org.tiling.scheduling.examples.iterators.DailyIterator;

public class CTATScheduler extends CTATBase 
{
    private final Scheduler scheduler = new Scheduler();
    private final SimpleDateFormat dateFormat =new SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS");
    private final int hourOfDay, minute, second;
    private CTATTutorUpdater updater=null;

	/**
	 *
	 */
    public CTATScheduler (CTATTutorUpdater anUpdater,
    					  int hourOfDay, 
    					  int minute, 
    					  int second) 
    {
    	setClassName ("CTATScheduler");
    	debug ("CTATScheduler ()");    	
    	
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.second = second;
        
        updater=anUpdater;
    }
	/**
	 *
	 */
    public boolean runUpdate () 
    {
    	debug ("update ()");
    	
        if (updater!=null)
        {
        	return (updater.runUpdate());
        }
        
        return (false);
    }
	/**
	 *
	 */
    public void start() 
    {
        scheduler.schedule (new SchedulerTask() 
        {
            public void run() 
            {
                soundAlarm();
            }
            
            private void soundAlarm() 
            {
                System.out.println ("Starting update cycle, on: " + dateFormat.format(new Date()));

                runUpdate ();

                // Start a new thread to sound an alarm...
            }
        }, new DailyIterator(hourOfDay, minute, second));
    }
	//-------------------------------------------------------------------------------------
}
