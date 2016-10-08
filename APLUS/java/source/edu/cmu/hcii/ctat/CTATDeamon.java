/**
 $Author: vvelsen $ 
 $Date: 2013-09-24 09:42:38 -0400 (Tue, 24 Sep 2013) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATDeamon.java,v 1.3 2012/05/31 15:09:36 blojasie Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATDeamon.java,v $
 Revision 1.3  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.2  2012/05/14 15:28:55  vvelsen
 Disabled automatic logging of critical events in CTATDeamon because it is the base class of CTATHTTPServer, which meant that anything that uses that class would automatically want to create a logfile

 Revision 1.1  2012/04/11 13:16:37  vvelsen
 Added missing files

 $RCSfile: CTATDeamon.java,v $ 
 $Revision: 19534 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATDeamon.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:

   http://www.ibm.com/developerworks/java/library/i-signalhandling/

*/

package edu.cmu.hcii.ctat;

import java.text.SimpleDateFormat;
import java.util.Date;

//import sun.misc.Signal;
//import sun.misc.SignalHandler;

/**
* Diagnostic Signal Handler class definition
*/
public class CTATDeamon extends CTATBase //implements SignalHandler 
{       
    //private SignalHandler oldHandler;
    private Boolean disabled=true;    
	private SimpleDateFormat critdf = new SimpleDateFormat ("EEE, d MMM yyyy HH:mm:ss Z");
	
	/**
	*
	*/	
	public CTATDeamon (Boolean disable)
	{  
    	setClassName ("CTATDeamon");
    	debug ("CTATDeamon ()"); 
    	
    	setDisabled (disable);    	
	}
	/**
	*
	*/	
	public CTATDeamon ()
	{  
    	setClassName ("CTATDeamon");
    	debug ("CTATDeamon ()");    	    	    	
	} 
	/**
	 * 
	 */
	public void startCriticalLogging ()
	{
		if (getDisabled ()==false)
		{
			if (getFManager ().openStream ("logs/critical.txt")==false)
			{
				setDisabled(true);
			}
			else
				writeToCritical ("System started on: " + critdf.format(new Date()));
		}	
	}
    /**
    * 
    */
	protected CTATDesktopFileManager getFManager ()
	{
    	if (CTATLink.fManager==null)
    		CTATLink.fManager=new CTATDesktopFileManager ();		
		
		return (CTATDesktopFileManager) (CTATLink.fManager);
	}
    /**
    * 
    */
	public Boolean getDisabled() 
	{
		return disabled;
	}
    /**
    * 
    */
	public void setDisabled(Boolean disabled) 
	{
		this.disabled = disabled;
	}	
    /**
    * 
    */
    public static CTATDeamon install (String signalName) 
    {    	
        //Signal diagSignal = new Signal (signalName);
        CTATDeamon diagHandler = new CTATDeamon ();
        
        /*
        diagHandler.startCriticalLogging ();
        diagHandler.oldHandler = Signal.handle(diagSignal,diagHandler);
        */
                
        return diagHandler;
    }
    /**
    * 
    */
    public void writeToCritical (String aMessage)
    {
    	if (getDisabled ()==false)
    	{
    		getFManager ().writeToStream (aMessage+"\n");
    	}	
    }
    /**
    * 
    */
    public void writeToCriticalWithTime (String aMessage)
    {
    	if (getDisabled ()==false)
    	{
    		getFManager ().writeToStream (aMessage+" (on: "+critdf.format(new Date())+")\n");
    	}	
    }    
    /**
    * 
    */
    /*
    public void handle (Signal sig) 
    {    	
        debug ("Diagnostic Signal handler called for signal " + sig);
        writeToCriticalWithTime ("Signal handler invoked");
        writeToCritical ("Diagnostic Signal handler called for signal " + sig);
        
        try 
        {
            // Output information for each thread
            Thread[] threadArray = new Thread[Thread.activeCount()];
            
            int numThreads = Thread.enumerate(threadArray);
            
            debug ("Current threads:");
            writeToCritical ("Current threads:");
            
            for (int i=0; i < numThreads; i++) 
            {
                debug ("    "+threadArray[i]);
                writeToCritical ("Thread: " + threadArray[i]);
            }
            
            // Chain back to previous handler, if one exists

            if ((oldHandler!=SIG_DFL) && (oldHandler!=SIG_IGN)) 
            {
                oldHandler.handle(sig);
            }            
        } 
        catch (Exception e) 
        {
            debug ("Signal handler failed, reason "+e);
            writeToCriticalWithTime ("Signal handler failed, reason "+e);
        }
    }
    */
}
