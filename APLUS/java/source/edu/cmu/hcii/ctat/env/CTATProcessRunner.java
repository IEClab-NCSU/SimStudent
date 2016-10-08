/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/env/CTATProcessRunner.java,v 1.2 2012/05/31 15:09:38 blojasie Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATProcessRunner.java,v $
 Revision 1.2  2012/05/31 15:09:38  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.1  2012/05/07 19:08:15  vvelsen
 Started some refactoring of our Java tree (with permission) First we'll do a bunch of small utilities that almost nobody uses, which seems to be the majority of our code

 Revision 1.11  2012/05/07 15:39:40  vvelsen
 Added some new diagnostic code to figure out where we can write data for the local tutorshop

 Revision 1.10  2012/03/27 17:28:30  vvelsen
 Added a method to copy files, upgraded the process runner slightly

 Revision 1.9  2012/03/21 17:12:30  vvelsen
 Some small upgrades and reformatting for readability.

 Revision 1.8  2012/02/10 20:53:43  vvelsen
 Started to integrate some of the code developed for the tutor monitor into our main class hierarchy. More to come.

 $RCSfile: CTATProcessRunner.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/env/CTATProcessRunner.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 
*/

package edu.cmu.hcii.ctat.env;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
public class CTATProcessRunner extends CTATEnvironment
{
	/**
	 * Whether or not to print debugging info.
	 */
	private boolean verbose = false;
	
	/**
	 * Default constructor equivalent to
	 * {@link #ProcessRunner(boolean) ProcessRunner(false)}.
	 */
	public CTATProcessRunner() 
	{    	
		this(false);
	}
	/**
	 * Constructor sets verbose parameter.
	 */
	public CTATProcessRunner(boolean verbose) 
	{
    	setClassName ("CTATProcessRunner");
    	debug ("CTATProcessRunner ()");
		
		this.verbose = verbose;
	}
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
		 *
		 * @param  is    stream to read
		 * @param  label label for each line of output
		 */
		StreamGobbler(InputStream is, String label)
		{
			this(is, label, null);
		}

		/**
		 * Constructor will write contents of {@link #is} to System.out.
		 *
		 * @param  is    stream to read
		 * @param  w     Writer on which to create {@link #outBuf}
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
					if (outBuf == null)
						debug (label + line);    
					else {
						outBuf.append(line);
						outBuf.append('\n');
					}
				}
				if (outBuf != null && verbose)
					System.err.println("debug " + label + ":\n" + outBuf); 
			} catch (IOException ioe) {
				ioe.printStackTrace();  
			}
		}
	}

	/**
	 * Run the given process and return its stdout.
	 *
	 * @param  execCmd command line parsed into command (entry 0) and args
	 * @return stdout from process, as single String
	 */
	public String exec(String[] execCmd) {

		Process cvsProc = null;
		StringBuffer cmd = new StringBuffer();
		try {
			for ( int i = 0; i < execCmd.length; i++ )
				cmd.append(execCmd[i]).append(' ');
			if (verbose)
				System.err.println("execCmd["+cmd+"]");
		 	cvsProc = Runtime.getRuntime().exec(execCmd);
		} catch(Exception e) {
			System.err.println("Exception on executing: " + execCmd);
			e.printStackTrace();
			return "";
		}

		StreamGobbler errorGobbler =
			new StreamGobbler(cvsProc.getErrorStream(), "ERROR");            

		StringBuffer sb = new StringBuffer();
		StreamGobbler outputGobbler =
			new StreamGobbler(cvsProc.getInputStream(), "OUTPUT", sb);

		errorGobbler.start();    // start each thread
		outputGobbler.start();

		try {                           // await child process exit
			if (cvsProc.waitFor() != 0)
				System.err.println("process " + cmd + " exit value = " + cvsProc.exitValue());
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
	*
	*/		
	public static boolean isLocalWindows() 
	{		
		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);
	}
	/**
	* java.vendor.url=http://www.android.com/
	* java.vm.vendor.url=http://www.android.com/
	* java.home=/system
	* java.vm.name=Dalvik
	* java.runtime.name=Android Runtime
	* java.specification.vendor=The Android Project
	* java.vm.specification.vendor=The Android Project
	* java.vm.vendor=The Android Project
	* android.vm.dexfile=true
	* java.specification.name=Dalvik Core Library
	* java.vendor=The Android Project
	* java.vm.specification.name=Dalvik Virtual Machine Specification
	* 
	*/	
	public static boolean isLocalAndroid() 
	{
		String os = System.getProperty("java.vendor").toLowerCase();
		// Android
		return (os.indexOf("android") >= 0);
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
		
		if (CTATProcessRunner.isLocalWindows()==true)
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
		
		if (CTATProcessRunner.isLocalWindows()==true)
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
}
