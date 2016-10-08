/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.hcii.ctat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import edu.cmu.hcii.utilities.DiagTools;
import edu.cmu.pact.TutoringService.LauncherServer;
import edu.cmu.pact.TutoringService.Monitor;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.trace;

/**
 * 
 */
public class CTATBaseTutorShop extends CTATBase implements ExitableServer 
{	
	protected Boolean useSysTray=true;
	private BufferedReader br=null;
	protected CTATDiagnostics diags=null;
	protected CTATHTTPServer wserver=null;	
	protected CTATTutorUpdater updater=null;	
	private CTATHTTPHandlerInterface handler=null;

	/** Set by {@link #startExiting()}. */
	protected boolean nowExiting = false; 
	
	/** See {@link #startExiting()}: these are other server threads that may need to exit. */
	protected List<ExitableServer> otherServers = new ArrayList<ExitableServer>();

	/** The Tutoring Service. */
	protected LauncherServer ls = null;
	
	
	/**
	 * Set up {@link #ctatBase} for tracing.
	 */
	public CTATBaseTutorShop() 
	{
    	setClassName ("CTATBaseTutorShop");
    	debug ("CTATBaseTutorShop ()");    	       	
	}
    /**
     * 
     */
    protected void setUseTray (Boolean aValue)
    {
    	useSysTray=aValue;
    }    
    /**
     * 
     */    
	public CTATHTTPHandlerInterface getHandler() 
	{
		return handler;
	}
    /**
     * 
     */	
	public void setHandler(CTATHTTPHandlerInterface aHandler) 
	{
		this.handler = aHandler;
	}    
    /**
     * 
     */
    public void setProblemSetEndHandler (ProblemSetEndHandler pseh)
    {
    	CTATLink.problemSetEndHandler = pseh;
    }
    /**
     * 
     */
    public void setProblemEndHandler (ProblemEndHandler peh)
    {
    	CTATLink.problemEndHandler = peh;
    }    
    /**
     * 
     */
    protected void runPrep ()        
    {
    	debug ("runPrep ()");
    	
    	// Implemented in CTATFlashTutorShop
    }
	/**
	 * Top-level call.  
	 */    
    public void runBarebones()
    {
    	debug ("runBarebones ()");
    	
    	diags=new CTATDiagnostics ();
    	
      	br=new BufferedReader (new InputStreamReader(System.in));
    	      	    	    
      	runPrep ();
      	
    	runInternal ();    	
    }    
	/**
     * Top-level call.  
     */
    public void runService (ProblemSetEndHandler pseh, ProblemEndHandler peh)
    {
    	debug ("runService ()");
    	
    	CTATLink.problemSetEndHandler = pseh;
    	CTATLink.problemEndHandler = peh;
    	
    	runBasic();
    }    
	/**
	 * Top-level object. Initializes, kills previous instance and calls {@link #runInternal()}. 
	 */    
    public void runBasic()
    {
    	debug ("runBasic ()");
    	
    	diags=new CTATDiagnostics ();
      	br=new BufferedReader (new InputStreamReader(System.in));
      	killPreviousInstance();
    	
      	updater=new CTATTutorUpdater (null,null,null);
      	    	   
      	runPrep ();
      	
    	runInternal ();       	
    	
        if (CTATLink.appMode.equalsIgnoreCase("normal") || CTATLink.appMode.equalsIgnoreCase("admin"))
        	showExitUI();
    }    
	/**
	 *
	 */
    private void runInternal ()
    {
    	debug ("runInternal ("+CTATLink.appMode+")");
    	 
    	if (CTATLink.appMode.compareTo("update")==0)
    	{
    		debug ("Running update process, this might take a few minutes ...");    		
    		updater.runUpdate ();
    		return;
    	}
    	
    	if ((CTATLink.appMode.compareTo ("diagnostics")==0) || (CTATLink.appMode.compareTo ("diagnostic")==0))
    	{
    		diags.testSettings (true);
    		return;
    	}
    	else
    	{
    		if (diags.testSettings (false)==false)
    		{
    			System.out.println ("Error: " + diags.diagnosis);    		
    			return;
    		}
    		else
    			debug ("Diagnosis and tests passed, booting services ...");
    	}	
 
    	if (CTATLink.appMode.compareTo("safemode")==0)    		
    		waitForInput ("Press ENTER to continue");

        debug ("Starting local tutoring service ...");

        // launch the tutoring service on the port specified in CTATLink
        trace.addDebugCodes ("pr,sp,br,tsltsp,tsltstp,ls,log,et,logservice");

        // launch the tutoring service on the port specified in CTATLink
        setLoggingProperties();
        
        ArrayList <String>cmdLineArgs=new ArrayList<String> ();
                
        if (CTATLink.deployType==CTATLink.DEPLOYHTML5)
        {        	
        	Integer hPort=CTATLink.tsPort;
        	Integer tPort=CTATLink.tsPort+1;
        	
        	cmdLineArgs.add ("-h");
        	cmdLineArgs.add (hPort.toString());
        	cmdLineArgs.add ("-t");
        	cmdLineArgs.add (tPort.toString());
        }
        else
        {
        	Integer hPort=CTATLink.tsPort;
        	Integer tPort=CTATLink.tsPort+1;
        	
        	cmdLineArgs.add ("-t");
        	cmdLineArgs.add (hPort.toString());
        	cmdLineArgs.add ("-h");
        	cmdLineArgs.add (tPort.toString());
        }	
                
        String[] array = cmdLineArgs.toArray(new String[cmdLineArgs.size()]);
        
        ls = LauncherServer.create(array);
        
        otherServers.add(ls);
        
    	if (CTATLink.appMode.compareTo("safemode")==0)		
    		waitForInput ("Press ENTER to continue");		
		
        debug ("Launching local webservice ...");
        
        try
        {        	
        	installHandler ();
        	
        	wserver=new CTATHTTPServer (CTATLink.wwwPort,
        								CTATLink.htdocs,
        								CTATLink.logdir+"/access.log",
        								handler);
        	otherServers.add(wserver);
        	        	
        	if (wserver.startWebServer ()==false)
        	{
        		JOptionPane.showMessageDialog(null, "Unable to start the HTTP server, aborting local TutorShop");
        		System.exit(0);
        	}
        }
        catch(IOException e)
        {
        	e.printStackTrace();
        	JOptionPane.showMessageDialog(null, "Unable to start the HTTP handler, aborting local TutorShop");
        	System.exit(0);
        }
        
		//wserver.startCriticalLogging ();
		        
    	//>----------------------------------------------------------------------
        
    	if (CTATLink.appMode.compareTo("safemode")==0)        
    		waitForInput ("Press ENTER to continue");

    	createSysTrayIcon();
    	
    	//>----------------------------------------------------------------------
              
    	if (CTATLink.appMode.compareTo("safemode")==0)        
    		waitForInput ("Press ENTER to continue");        
        
        debug ("Launching desktop ...");
        debug ("active threads "+DiagTools.listThreads());
        
        invokeBrowserOnLocalWebServer();
                
    	//>----------------------------------------------------------------------
        
        monitorConnectivity();
    }

    /**
     * Try to kill a previous instance of this program. Sends shutdown
     * service request to {@link Monitor#MONITOR_PORT}. Handles any exception
     */
	protected void killPreviousInstance() 
	{
		debug ("killPreviousInstance()");
		
		try {
			String response = Monitor.request("<service cmd=\"shutdown\"/>",
				Monitor.MONITOR_PORT, 1000);  // wait 1000 ms for other to die
			if (trace.getDebugCode("kill"))
				trace.out("kill", "response from server: "+response);
		} catch (Exception e) {
			trace.err("Error trying to shutdown previous instance: "+e+"; cause "+e.getCause());
		}
	}
	/**
	 *
	 */
	private void waitForInput (String message)
	{
        debug (message);
        
        try
        {
        	br.readLine();
        }
        catch(IOException ioe) 
        {
        	//ioe.printStackTrace();
        	System.out.println ("Diagnostic, TutoShop is not allowed to read from the keyboard, aborting ...");
        	return;
        }        			
	}    
    /**
     * 
     */
    protected Boolean installHandler () throws IOException
    {
    	debug ("installHandler ()");
    	
    	// Implemented in CTATFlashTutorShop
    	
    	return (true);
    }
    /**
     * Set System properties from the logging parameters {@link CTATLink#logdir}
     * and {@link CTATLink#datashopURL}
     */
    protected void setLoggingProperties() {
    	if(CTATLink.datashopURL != null && CTATLink.datashopURL.length() > 0)
    		System.setProperty(Logger.LOG_SERVICE_URL_PROPERTY, CTATLink.datashopURL);
    	if(CTATLink.logdir != null && CTATLink.logdir.length() > 0)
    		System.setProperty(Logger.DISK_LOG_DIR_PROPERTY, CTATLink.logdir);
	}

	/**
	 * No-op in superclass.
	 */
	protected void createSysTrayIcon() {
		debug("createSysTrayIcon() no-op in superclass.");		
	}
	
	/**
	 * No-op in superclass.
	 */
	protected void invokeBrowserOnLocalWebServer() {
		debug("invokeBrowserOnLocalWebServer() no-op in superclass.");
	}
	
	/**
	 * No-op in superclass.
	 */
    protected void monitorConnectivity() {
		debug("monitorConnectivity() no-op in superclass.");    	
    }
	
	/**
	 * @return
	 * @see edu.cmu.hcii.ctat.ExitableServer#isExiting()
	 */
	public boolean isExiting() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return
	 * @see edu.cmu.hcii.ctat.ExitableServer#startExiting()
	 */
	public boolean startExiting() {
		// TODO Auto-generated method stub
		return false;
	}

    /**
     * No-op in superclass.
     */
    protected void showExitUI() 
    {
    	debug("showExitUI() no-op in superclass");
	}
}
