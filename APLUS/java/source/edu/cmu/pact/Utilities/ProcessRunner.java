/**
 $Author: sewall $ 
 $Date: 2014-09-02 19:31:33 -0400 (Tue, 02 Sep 2014) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.13  2012/06/08 23:21:42  sewall
 For local tutorshop's run.jar, meant to be an OS-independent means to launch the local tutorshop.

 Revision 1.12  2012/05/07 19:08:15  vvelsen
 Started some refactoring of our Java tree (with permission) First we'll do a bunch of small utilities that almost nobody uses, which seems to be the majority of our code

 Revision 1.11  2012/05/07 15:39:40  vvelsen
 Added some new diagnostic code to figure out where we can write data for the local tutorshop

 Revision 1.10  2012/03/27 17:28:30  vvelsen
 Added a method to copy files, upgraded the process runner slightly

 Revision 1.9  2012/03/21 17:12:30  vvelsen
 Some small upgrades and reformatting for readability.

 Revision 1.8  2012/02/10 20:53:43  vvelsen
 Started to integrate some of the code developed for the tutor monitor into our main class hierarchy. More to come.

 $RCSfile$ 
 $Revision: 21188 $ 
 $Source$ 
 $State$ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 
*/

package edu.cmu.pact.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import edu.cmu.hcii.ctat.CTATBase;

/**
 * @author sewall
 *
 * Initially this class was designed to run an external process and return its 
 * stdout. But more capabilities have been added such that global process
 * information can be obtained. Also more specific details about what a machine
 * supports can be retrieved through this class.
 *
 * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
 */
public class ProcessRunner extends CTATBase 
{

	//////////////////////////////////////////////////////////////////////
	//
	// Inner classes
	//
	//////////////////////////////////////////////////////////////////////

	/**
	 * Read an input stream in a separate Thread. This is needed to get
	 * the output from Process spawned by Runtime.exec().
	 */
	class StreamGobbler extends Thread
	{				
		/**
		 * Stream to read.
		 */
		InputStream is;

		/**
		 * Label for outut if printing to stdout; else unused.
		 */
		String label;

		/**
		 * Internal output stream.
		 */
		StringBuffer outBuf;

		/**
		 * Constructor will write contents of {@link #is} to System.out.
		 * @param  is    stream to read
		 * @param  label label for each line of output; if null or empty, no label
		 */
		StreamGobbler(InputStream is, String label)
		{
			this(is, label, null);
		}

		/**
		 * Constructor will write contents of {@link #is} to System.out.
		 * @param  is stream to read
		 * @param  label label for each line of output
		 * @param  outBuf Writer on which to create {@link #outBuf}
		 */
		StreamGobbler(InputStream is, String label, StringBuffer outBuf)
		{
			this.is = is;
			if (label != null && label.length() > 0)
				this.label = label + "> ";
			else
				this.label = "";
			this.outBuf = outBuf;
		}

		/**
		 * StreamGobbler thread body. Opens the input stream and reads
		 * until there's no more data.
		 */
		public void run()
		{
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line=null;
				while ( (line = br.readLine()) != null) {
					interruptOnFirstOutput(line);
					if (outBuf == null) {
						if("error".equalsIgnoreCase(label))
							System.err.println(label + line);
						else
							System.out.println(label + line);    
					} else {
						outBuf.append(line);
						outBuf.append('\n');
					}
				}
				if (outBuf != null && verbose)
					System.err.println("child process cumulative stdout "+label+":\n"+outBuf); 
			} catch (IOException ioe) {
				ioe.printStackTrace();  
			}
		}
	}


	//////////////////////////////////////////////////////////////////////
	//
	// Fields
	//
	//////////////////////////////////////////////////////////////////////

	/**
	 * Whether or not to print debugging info.
	 */
	private boolean verbose = false;
	
	/** Whether {@link Runtime#exec(String[])} has been called yet. */
	private boolean execDone = false;

	/** The child process. */
	private Process childProc = null;

	/** Copy of the command line, for debugging. */
	private List<String> cmd = null;

	/** If not null, to return the first output. */
	private StringBuffer firstOutput;

	/** Thread to {@link Thread#interrupt()} if {@link #firstOutput} requested. */
	private Thread callingThread;


	//////////////////////////////////////////////////////////////////////
	//
	// Constructors
	//
	//////////////////////////////////////////////////////////////////////

	/**
	 * Default constructor equivalent to
	 * {@link #ProcessRunner(boolean) ProcessRunner(false)}.
	 */
	public ProcessRunner() 
	{    	
		this(false);
	}

	/**
	 * Constructor sets verbose parameter.
	 */
	public ProcessRunner(boolean verbose) 
	{
    	setClassName ("ProcessRunner");
    	debug ("ProcessRunner ()");
		
		this.verbose = verbose;
	}

	//////////////////////////////////////////////////////////////////////
	//
	// Methods
	//
	//////////////////////////////////////////////////////////////////////

	/**
	 * Run the given process and return its stdout.
	 *
	 * @param  execCmd command line parsed into command (entry 0) and args
	 * @return stdout from process, as single String
	 */
	public String exec(String[] execCmd) {
		return exec(execCmd, true, null);
	}

	/**
	 * Run the given process and, if asked, return its stdout. Issues interrupt
	 * on calling thread if childHasOutput is not null.
	 * @param  execCmd command line parsed into command (entry 0) and args
	 * @param  awaitEnd if true, wait until the child process has exited
	 * @param  firstOutput if not null, set element[0] to true upon first output
	 * @return stdout from process, as single String, if awaitEnd is true
	 */
	public String exec(String[] execCmd, boolean awaitEnd, StringBuffer firstOutput) {
		this.cmd = Arrays.asList(execCmd);
		if(null != (this.firstOutput = firstOutput))
			this.callingThread = Thread.currentThread();

		StringBuffer cmdArr = new StringBuffer();
		try {
			for ( int i = 0; i < execCmd.length; i++ )
				cmdArr.append(execCmd[i]).append(' ');
			if (verbose)
				System.err.println("execCmd["+cmdArr+"]");
		 	childProc = Runtime.getRuntime().exec(execCmd);
		} catch(Exception e) {
			System.err.println("Exception on executing: " + execCmd);
			e.printStackTrace();
			return "";
		}

		StreamGobbler errorGobbler =
			new StreamGobbler(childProc.getErrorStream(), "ERROR");            

		StringBuffer sb = (awaitEnd ? new StringBuffer() : null);
		StreamGobbler outputGobbler =
			new StreamGobbler(childProc.getInputStream(), "OUTPUT", sb);

		errorGobbler.start();    // start each thread
		outputGobbler.start();

		execDone = true;
		if(!awaitEnd)
			return null;
		
		try {                           // await child process exit
			if (childProc.waitFor() != 0)
				System.err.println("process " + cmdArr + " exit value = " + childProc.exitValue());
		}
		catch (InterruptedException e) {
			System.err.println(e);
		}
		
		try {                               // wait up to 2 sec for finish
			outputGobbler.join(2000);       // before using output
		} catch (InterruptedException e) {}

		return sb.toString();
	}
	
	/**
	 * @return {@link #execDone}
	 */
	public boolean isExecDone() {
		return execDone;
	}

	/**
	*
	*/		
	public static boolean isLocalWindows() 
	{		
		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);
	}
	/**
	*
	*/	
	public static boolean isLocalMac() 
	{
		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return (os.indexOf("mac") >= 0);
	}
	/**
	*
	*/	 
	public static boolean isLocalUnix() 
	{
		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
	}
	/**
	*
	*/	 
	public static boolean isLocalSolaris() 
	{
		String os = System.getProperty("os.name").toLowerCase();
		// Solaris
		return (os.indexOf("sunos") >= 0);
	}			
	/**
	 * 
	 */
	public String getLocalProcesses ()
	{
		debug ("getLocalProcesses ()");
		
		StringBuffer buffer=new StringBuffer ();
		
		if (ProcessRunner.isLocalWindows()==true)
		{
			debug ("Executing Windows version of ps ...");
			
			try 
			{
				String line;
	        
				Process p = Runtime.getRuntime().exec (System.getenv("windir") +"\\system32\\"+"tasklist.exe");
	        
				BufferedReader input=new BufferedReader(new InputStreamReader(p.getInputStream()));
	        
				while ((line = input.readLine()) != null) 
				{
					//debug (line);
					buffer.append(line);
					buffer.append("\n");
				}
	        
				input.close();
			} 
			catch (Exception err) 
			{
				err.printStackTrace();
			}			
		}
		else
		{	
			debug ("Executing Unix version of ps ...");
			
			try
			{
				String line;
	        
				Process p = Runtime.getRuntime().exec("ps -e");
	        
				BufferedReader input=new BufferedReader(new InputStreamReader(p.getInputStream()));
	        
				while ((line = input.readLine()) != null) 
				{
					//debug (line);
					buffer.append(line);
					buffer.append("\n");
				}
	        
				input.close();
			} 
			catch (Exception err) 
			{
				err.printStackTrace();
			}
		}	
		
		return (buffer.toString());
	}	
	/**
	 * 
	 */
	public String getLocalNetstat ()
	{
		debug ("getLocalNetstat ()");
		
		StringBuffer buffer=new StringBuffer ();
		
		if (ProcessRunner.isLocalWindows()==true)
		{
			debug ("Executing Windows version of netstat ...");
			
			try 
			{
				String line;
	        
				Process p = Runtime.getRuntime().exec (System.getenv("windir") +"\\system32\\"+"netstat.exe -anot");
	        
				BufferedReader input=new BufferedReader(new InputStreamReader(p.getInputStream()));
	        
				while ((line = input.readLine()) != null) 
				{
					//debug (line);
					buffer.append(line);
					buffer.append("\n");
				}
	        
				input.close();
			} 
			catch (Exception err) 
			{
				err.printStackTrace();
			}			
		}
		else
		{	
			debug ("Executing Unix version of netstat ...");
			
			try
			{
				String line;
	        
				Process p = Runtime.getRuntime().exec("netstat -anop tcp");
	        
				BufferedReader input=new BufferedReader(new InputStreamReader(p.getInputStream()));
	        
				while ((line = input.readLine()) != null) 
				{
					//debug (line);
					buffer.append(line);
					buffer.append("\n");
				}
	        
				input.close();
			} 
			catch (Exception err) 
			{
				err.printStackTrace();
			}
		}	
		
		return (buffer.toString());
	}

	/**
	 * Try to kill the child process, with optional wait for exit.
	 * @param waitMs if less than 0, don't wait; otherwise argument for {@link Thread#join(long)}
	 * @return result from {@link Process#exitValue()}; -1 on error
	 */
	public int kill(long waitMs) {
		childProc.destroy();
		for(long now = System.currentTimeMillis(), begin = now, then = now+waitMs; now < then; ) {
			try {
				Thread.sleep(50);  // avoid unblocked spin
				int result = childProc.exitValue();
				if(trace.getDebugCode("js"))
					trace.out("js", String.format("ProcessRunner.kill(%s) returns %d after %d ms",
							cmd.toString(), result, System.currentTimeMillis()-begin));
				return result;
			} catch (Exception e) {
				now = System.currentTimeMillis();
				if(trace.getDebugCode("js"))
					trace.out("js", String.format("Error %s (cause %s) awaiting destroy(%s) after %d ms",
						e, e.getCause(), cmd, then-begin));
			}
		}
		return -1;
	}		

	/**
	 * Notify a calling thread if output from the child has begun. On first call, sets
	 * {@link #firstOutput} and calls {@link Thread#interrupt()} on {@link #callingThread}.
	 * No-op if line empty or {@link #firstOutput} is null. 
	 * @param line
	 */
	private void interruptOnFirstOutput(String line) {
		if(firstOutput == null || firstOutput.length() > 0)
			return;
		if(line.length() < 1)
			return;
		firstOutput.append(line);
		callingThread.interrupt();
	}
}
