/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-09-23 12:26:26 -0400 (Mon, 23 Sep 2013) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATDiagnostics.java,v 1.21 2012/10/16 20:01:34 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATDiagnostics.java,v $
 Revision 1.21  2012/10/16 20:01:34  vvelsen
 Lots of changes to align the various http handlers. Most of the work has been done in the DVD handler for FIRE which now uses the progress database to also store user information

 Revision 1.20  2012/09/12 14:28:34  vvelsen
 Complete reworking of our http handling code. All file requests now go through our singleton file handler which can handle mounted jar files and as such creates a miniature vfsl

 Revision 1.19  2012/08/23 21:04:01  kjeffries
 clean-ups, and fix of writable-directory-checking on Linux

 Revision 1.18  2012/08/20 15:31:36  kjeffries
 restore some stuff that was lost in the merge

 Revision 1.16  2012/08/10 21:24:05  kjeffries
 better diagnostics for Mac

 Revision 1.15  2012/08/07 21:07:32  kjeffries
 writability of directory is now tested by actually writing to it

 Revision 1.14  2012/07/23 16:16:35  kjeffries
 expanded diagnostic tests whose results can be dumped to file

 Revision 1.13  2012/06/26 13:59:38  kjeffries
 in connectivity checker, sleep at beginning rather than end of loop so program startup goes more smoothly

 Revision 1.12  2012/06/18 22:00:58  kjeffries
 added connectivity checker, which runs in the background and notices loss of internet connection

 Revision 1.11  2012/06/08 18:28:48  kjeffries
 enhanced filesystem diagnostics

 Revision 1.10  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.9  2012/05/14 15:28:55  vvelsen
 Disabled automatic logging of critical events in CTATDeamon because it is the base class of CTATHTTPServer, which meant that anything that uses that class would automatically want to create a logfile

 Revision 1.8  2012/05/07 19:08:15  vvelsen
 Started some refactoring of our Java tree (with permission) First we'll do a bunch of small utilities that almost nobody uses, which seems to be the majority of our code

 Revision 1.7  2012/05/07 15:39:40  vvelsen
 Added some new diagnostic code to figure out where we can write data for the local tutorshop

 Revision 1.6  2012/04/13 19:44:38  vvelsen
 Started adding some proper threading and visualization tools for our download manager

 Revision 1.5  2012/03/06 21:28:01  sewall
 In testSettings(), don't fail if can't bind the Flash Security port 843, since can live w/o and requires root privilege.

 Revision 1.4  2012/02/15 20:22:43  sewall
 Add findServerPort() to choose next port if one of ours is occupied.

 Revision 1.3  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.7  2012/01/06 19:12:19  sewall
 Cleanups to remove compiler warnings.

 Revision 1.6  2011/06/08 20:35:36  kjeffries
 Encryption key is no longer stored in a file.

 Revision 1.5  2011/04/28 17:11:56  vvelsen
 Finally connected all the dots and made sure that we can access platform specific drivers (java classes) through a central registry. Please see CTATLink for more information.

 Revision 1.4  2011/03/25 20:38:49  vvelsen
 Added much more capabilties to the USB TutorShop. We can now start problems defined in problem_set.xml files. Much more of the problem sequencing code has been finished but more needs to happen there. Some of the internals have been bolstered.

 Revision 1.3  2011/02/09 13:11:38  vvelsen
 Added proper logging handling to local disk (USB drive) and added encryption code.

 Revision 1.2  2011/02/07 19:50:15  vvelsen
 Added a number of files that can manage local files as well as generate html from parameters and templates.

 Revision 1.1  2011/02/06 16:55:27  vvelsen
 Added a first working version of a standalone USB based TutorShop.

 $RCSfile: CTATDiagnostics.java,v $ 
 $Revision: 19526 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATDiagnostics.java,v $ 
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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import edu.cmu.hcii.ctat.env.CTATEnvironment;

public class CTATDiagnostics extends CTATBase
{    		    
	private boolean verbose=true;
	public String diagnosis="All's well -- all required ports are open";
	public ArrayList <String> writableDirs=null;
	
	/**
	 *
	 */
    public CTATDiagnostics () 
    {
    	setClassName ("CTATDiagnostics");
    	debug ("CTATDiagnostics ()");
    	    	
    	if (CTATLink.crypto==null)
    		CTATLink.crypto=new CTATCryptoUtils ();
    	
    	writableDirs=new ArrayList<String> ();
    } 
    /**
     * 
     */
    public ArrayList <String> getWritables ()
    {
    	return (writableDirs);
    }
	/**
	 *
	 */
    public boolean configEnvironment ()
    {
    	debug ("configEnvironment ()");
    	
    	if(CTATLink.fManager == null)
    	{
    		CTATLink.fManager = new CTATDesktopFileManager(); // assume desktop if unknown
    	}
    	
    	if (CTATLink.fManager.doesFileExist (CTATLink.etc)==false)
    	{
    		if (CTATLink.fManager.createDirectory (CTATLink.etc)==false)
    		{
    			debug ("Error unable to create etc directory");
    			return (false);
    		}
    	}
    
    	return (true);
    }
	/**
	 *
	 */
    public boolean testSettings(boolean aVerbose)
    {
    	return testSettings(aVerbose, true);
    }
    /** 
     * @param aVerbose
     * @param callConfigEnvironment
     * @return
     */
    public boolean testSettings (boolean aVerbose, boolean callConfigEnvironment)
    {
    	debug ("testSettings ()");
    	
    	verbose=aVerbose;
    	
    	if(callConfigEnvironment)
    	{
    		if (configEnvironment ()==false)
    			return (false);
    	}
    	
    	getInetInfo ();
    	
    	getEnvInfo ();
    	
    	if ((CTATLink.tsPort = findServerPort(CTATLink.tsPort)) < 0)
    	{
    		diagnosis="Not allowed or not able to launch server on tutoring server port";
    		return (false);
    	}
    	
    	if ((CTATLink.tsMonitorPort = findServerPort(java.lang.Math.max(CTATLink.tsMonitorPort, CTATLink.tsPort + 1))) < 0)
    	{
    		diagnosis="Not allowed or not able to launch server on tutoring server monitor port";    		
    		return (false);
    	}
    	
    	if (!(testServerPort (843)))
    	{
    		diagnosis="Not allowed or not able to launch server on Flash security port";
//    		return (false);  sewall 2012/03/06: don't fail here, since we can do without this
    	}
    	
    	if ((CTATLink.wwwPort = findServerPort(CTATLink.wwwPort)) < 0)
    	{
    		diagnosis="Not allowed or not able to launch server on http server port";
    		return (false);
    	}
    	    	   	        
        return (true);
    }
	/**
	 *
	 */    
    private String getInetInfo ()
    {
    	if (verbose==true)
    		debug ("getInetInfo ()");
    	
    	StringBuilder sb = new StringBuilder();
    	final String newline = System.getProperty("line.separator");
    	
        try 
        {
        	Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

        	while(e.hasMoreElements()) 
        	{
        		NetworkInterface ni = (NetworkInterface) e.nextElement();
        		if (verbose==true)
        		{
        			debug ("Net interface: "+ni.getName());
        			sb.append("Net interface: "+ni.getName() + newline);
        		}

        		Enumeration<InetAddress> e2 = ni.getInetAddresses();

       			while (e2.hasMoreElements())
       			{
       				InetAddress ip = (InetAddress) e2.nextElement();
       				if (verbose==true)
       				{
       					debug ("IP address: "+ ip.toString());
       					sb.append("IP address: "+ ip.toString() + newline);
       				}
       			}
       		}
       	}
       	catch (Exception e) 
       	{
            e.printStackTrace();
       	}
        
       	return sb.toString();
    }
	/**
	 *
	 */    
    private void getEnvInfo ()
    {
    	if (verbose==true)
    		debug ("getEnvInfo ()");
    	
        Properties systemProps=System.getProperties();
		
        Set<Entry<Object, Object>> sets = systemProps.entrySet ();
																								
		int index=0;
								
        for (Entry<Object,Object> entry : sets) 
		{
        	if (verbose==true)
        		debug ("["+index+"] name: " + entry.getKey () + ", value: " + entry.getValue());
        	index++;
        }    	
    }
	/**
	 *
	 */    
    private int findServerPort (int portNumber)
    {
    	if (verbose==true)
    		debug ("testServerPort startint at port "+portNumber);

        for (int limit = portNumber+20; portNumber < limit; portNumber++)
        {
        	if (testServerPort(portNumber))
        		return (portNumber);
        }
		diagnosis="Not allowed to open a listening socket on: " + portNumber;
		return (-1);
    }

    /**
     * Check whether a given port is available as a {@link ServerSocket}.
     * @param portNumber
     * @return
     */
    private boolean testServerPort(int portNumber) 
    {
    	ServerSocket testSocket = null;
    	try 
    	{
    		testSocket=new ServerSocket (portNumber);
    	} 
    	catch (IOException e) 
    	{
    		error("Exception trying to bind port "+portNumber+": "+e);
    		return false;
    	} 
    	finally 
    	{ 
    		// Clean up
    		try 
    		{
    			if (testSocket != null) 
    				testSocket.close();
    		}
    		catch (IOException e) {}
    	}
    	
    	debug ("Success: server port "+portNumber+" accessible for listening");
    	return true;
	}
    /**
     * 
     */
    public String getProperty (String aProp)
    {
    	return (System.getProperty(aProp.toLowerCase()));
    }
    /**
     * 
     */
    public String getEnvironment (String aProp)
    {
    	return (System.getenv(aProp.toLowerCase()));
    }
    /**
     * Run this method to obtain a list of directories where your application
     * is allowed to write. The method itself returns a boolean indicating
     * if the code itself was able to execute. If no writable directories
     * are found the method will return false and an empty list. Please
     * examine the member variable writableDirs for a list of directories. 
     * These directories are sorted in order of most likely to be stable
     * and permanent locations. 
     * 
     * http://msdn.microsoft.com/en-us/library/ms995853.aspx
     * 
     * Possible values of os.name:
     * 
     * AIX
     * Digital Unix
     * FreeBSD
     * HP UX
     * Irix
     * Linux
     * Mac OS
     * Mac OS X
     * MPE/iX
     * Netware 4.11
     * OS/2
     * Solaris
     * Windows 2000
     * Windows 95
     * Windows 98
     * Windows NT
     * Windows Vista
     * Windows XP
     * 
     */
    public Boolean getWritableDirectory ()
    {
    	debug ("getWritableDirectory ()");
    	
    	writableDirs=new ArrayList<String> ();
    	
		//Properties systemProps = System.getProperties();
		//Set<Entry<Object, Object>> sets = systemProps.entrySet();
		Map<String,String> env=System.getenv();
		
		/*
		debug ("Java properties: ");
		
		for (Entry<Object,Object> entry : sets) 
		{
			debug ("Inspecting Java property: " + entry.getKey().toString() +"="+entry.getValue().toString());								
		}    	
				
		debug ("Environment properties: ");
		
        for (String envName : env.keySet()) 
        {
        	debug ("Inspecting Environment property: " + envName +"="+env.get(envName));        	
        }
        */
		
		String option="";
    	    	
    	if (CTATEnvironment.isLocalWindows()==true)
    	{
    		//String result=Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData);
    		//option=System.getenv("CSIDL_COMMON_APPDATA");
    		
    		option=env.get("APPDATA"); // Something like: "C:\Users\vvelsen\AppData\Roaming"
    		
    		if(option != null) writableDirs.add(option);
    		
    		option=System.getProperty("user.home"); // Something like : "C:\Users\vvelsen"
    		
    		if(option != null) writableDirs.add(option);
    		
    		option=env.get("ALLUSERSPROFILE"); // Something like: "C:\ProgramData"
    		
    		if(option != null) writableDirs.add(option);
    		    		
    		option=env.get("HOMEPATH"); // Something like: "\Users\vvelsen"
    		
    		if(option != null) writableDirs.add(option);
    		
    		option=env.get("ProgramFiles");
    		if(option != null) writableDirs.add(option);
    		    		    		
    		//option=env.get("PUBLIC"); // Something like: "C:\Users\Public" DOES NOT WORK ON XP
    		
    		//writableDirs.add(option);    		
    	}	
    	
    	if (CTATEnvironment.isLocalMac()==true)
    	{
    		/*
    		 * mkdir ~/Public
    		 * chmod 755 ~/Public
    		 */
    		
    		option=System.getenv("user.home");
    		if(option != null) writableDirs.add(option);
    		
    		option=System.getenv("HOME");
    		if(option != null) writableDirs.add(option);
    		
    		option=System.getProperty("user.home");
    		if(option != null) writableDirs.add(option);
    		
    		option = "/Applications";
    		if(option != null) writableDirs.add(option);
    	}	
    	
    	if (CTATEnvironment.isLocalAndroid()==true)
    	{
    		
//    		option=System.getenv("user.home");
//    		
//    		if(option != null) writableDirs.add(option);
    		
    		option=System.getProperty("java.io.tmpdir"); // was user.home
    		
    		if(option != null) writableDirs.add(option);
    	}	    	
    	
    	if (CTATEnvironment.isLocalUnix()==true)
    	{
    		option=System.getenv("user.home");
    		
    		if(option != null) writableDirs.add(option);
    	}	
    	
    	if (CTATEnvironment.isLocalSolaris()==true)
    	{
    		option=System.getenv("user.home");
    		
    		if(option != null) writableDirs.add(option);
    	}
    	
    	// remove writableDirs that are not actually writable
    	int i = 0;
    	while(i < writableDirs.size())
    	{
    		String s = writableDirs.get(i);
    		File f = new File(s);
    		if(!isDirWritable(f))
    		{
    			writableDirs.remove(i);
    			continue;
    		}
    		++i;
    	}
    	    	
    	return (true);
    }
	/**
	 * How much space can/should this program take on the disk?
	 * @param path the path to the directory where writes will take place
	 */ 
    public static long writableBytes(String path)
    {
    	if(path == null)
    	{
    		path = ""; // prevent NullPointerException
    	}
    	return writableBytes(new File(path));
    }
    /**
	 * How much space can/should this program take on the disk?
	 * @param path the path to the directory where writes will take place
	 */
    public static long writableBytes(File path)
    {
    	if(path == null)
    	{
    		path = new File(""); // if no path is specified, assume present working directory
    	}
    	
    	long usableSpace = path.getUsableSpace();
    	long freeSpace = path.getFreeSpace();
    	return Math.min(usableSpace, (long)(.1 * freeSpace)); // can use all the usable space but no more than 10% of the total free space on disk
    }
    /**
     * Decide how much main memory the cache is allowed to use
     * @return number of bytes
     */
    public static long inMemoryCacheSize()
    {
    	long memory = Runtime.getRuntime().totalMemory();
    	return (memory / 2); // earmark half of the heap size to be used for the content cache
    }
    /**
     * Output some diagnostic info, e.g. to a file
     * @out a PrintWriter where the diagnostic info should be written
     */
    public void dumpDiagnostics(PrintWriter out)
    {
    	out.println("Diagnostic output generated at " + new java.util.Date());
    	out.println();
    	
    	// network diagnostics
    	testSettings(false, false);
    	out.println(diagnosis);
    	out.println("tsPort: " + CTATLink.tsPort);
    	out.println("tsMonitorPort: " + CTATLink.tsMonitorPort);
    	out.println("wwwPort: " + CTATLink.wwwPort);
    	out.println(getInetInfo());
    	CTATURLFetch fetcher = new CTATURLFetch();
    	String host = (CTATLink.remoteHost.equals("") || CTATLink.remoteHost.equals("local")) ? "www.google.com" :  CTATLink.remoteHost; // use CTATLink.remoteHost unless it does not specify a host name
    	URL url;
    	try {
    		url = new URL("http", host, "/");
    	} catch(MalformedURLException e) { url = null; /* ignore the exception */ }
    	int num = 5;
    	out.print("Milliseconds taken to connect to " + host + " on " + num + " attempts (0 or negative indicates failure): ");
    	for(int i = 0; i < num; ++i)
    	{
    		out.print(fetcher.checkConnectivity(url) + " ");
    	}
    	out.println();
    	out.println();
    	
    	// writable directories
    	if(getWritableDirectory())
    	{
    		out.println("Writable directories, in order of decreasing preference:");
    		for(String writableDir : writableDirs)
    		{
    			out.println(writableDir);
    		}
    	}
    	else
    	{
    		out.println("Could not find list of writable directories.");
    	}
    	out.println();
    	
    	// available space on all drives
    	out.println("Available filesystem roots, possibly excluding network drives:");
    	File[] roots = File.listRoots();
    	for(File root : roots)
    	{
    		out.println("Root \"" + root + "\" " + (root.canWrite() ? "(writable)" : "(NOT directly writable)"));
    		out.println("\tTotal space: " + root.getTotalSpace() + " bytes.");
    		out.println("\tFree space: " + root.getFreeSpace() + " bytes.");
    		out.println("\tUsable space: " + root.getUsableSpace() + " bytes.");
    	}
    	out.println();
    	
    	// Java properties
    	System.getProperties().list(out);
    	out.println();
    	
    	// environment variables
    	out.println("Environment variables:");
    	Map<String, String> map = System.getenv();
		Set<Entry<String, String>> entryset = map.entrySet();
		for(Entry<String, String> entry : entryset)
		{
			out.println(entry.getKey() + ": " + entry.getValue());
		}
		out.println();
		
		// cache defaults
		out.println("Cache limits");
		out.println("On-disk cache limit, determined by software based on amount of free space on disk: ");
		
		for(File root : roots)
    	{
			out.println("\tAssuming root \"" + root + "\": " + CTATDiagnostics.writableBytes(root) + " byte limit.");
    	}
		
		out.println("In-memory cache limit, based on amount of available RAM: " + CTATDiagnostics.inMemoryCacheSize() + " bytes.");
		out.println("Maximum number of cache entries, default or decided by user (configurable in config mode): " + CTATLink.maxCachedFiles);
		out.println();
		
		// cache stats
		out.println("Cache statistics regarding history of cache usage on current machine");
		
		CTATContentCache cache;
		
		if(CTATLink.cache != null)
		{
			cache = CTATLink.cache;
		}
		else
		{
			cache = CTATLink.cache = new CTATContentCache(new File("./htdocs/cache"), true);
		}
		
		Set<CTATContentCache.CacheEntryInfo> infoSet = cache.getInfoOnEntries();
		if(infoSet != null)
		{
			long totalCacheSize = 0;
			long totalAccesses = 0;
			for(CTATContentCache.CacheEntryInfo cei : infoSet)
			{
				totalCacheSize += cei.size;
				totalAccesses += cei.accessFrequency;
			}
			out.println("Total cache size is " + totalCacheSize + " bytes.");
			/* Sort the cache entry info by cache entry access frequency */
			ArrayList<CTATContentCache.CacheEntryInfo> sortedByAccessFreq = new ArrayList<CTATContentCache.CacheEntryInfo>(infoSet);
			Collections.sort(sortedByAccessFreq, new java.util.Comparator<CTATContentCache.CacheEntryInfo>() {
				public int compare(CTATContentCache.CacheEntryInfo t1, CTATContentCache.CacheEntryInfo t2)
				{
					return t1.accessFrequency - t2.accessFrequency;
				}
			});
			/* Calculate and print stats on the more frequently accessed half of the cache entries */
			long topHalfAccesses = 0;
			long topHalfSize = 0;
			for(int i = sortedByAccessFreq.size() / 2; i < sortedByAccessFreq.size(); ++i)
			{
				CTATContentCache.CacheEntryInfo cei = sortedByAccessFreq.get(i);
				topHalfAccesses += cei.accessFrequency;
				topHalfSize += cei.size;
			}
			if(totalCacheSize > 0 && totalAccesses > 0) // avoid division by 0
			{
				out.println("Top 50% most accessed cache entries constitute " + (topHalfAccesses*100 / totalAccesses) +
						"% of all cache hits and take up " + (topHalfSize*100 / totalCacheSize) + "% of the total cache size.");
				out.println("Total number of cache entries: " + infoSet.size());
				out.println("Average size of a single cache entry, unweighted: " + (totalCacheSize/infoSet.size()) + " bytes.");
				long avgSizeWeighted = 0;
				for(CTATContentCache.CacheEntryInfo cei : infoSet)
				{
					avgSizeWeighted += cei.size * cei.accessFrequency / totalAccesses;
				}
				out.println("Average size of a single cache entry, weighted by access frequency: " + avgSizeWeighted + " bytes.");
			}
		}
		
    	out.flush();
    }
    
    /**
     * 
     * @param f
     * @return
     */
    private boolean isDirWritable(File f)
    {
    	if(!f.isDirectory()) return false;
    	
    	// try writing to it
    	File temp = null;
    	try
    	{
    		temp = File.createTempFile("ctat", null, f);
    		String dateString = (new Date()).toString();

    		Writer writer = new FileWriter(temp);
    		writer.write(dateString);
    		writer.close();

    		// and read back from it to see if the write worked
    		Reader reader = new FileReader(temp);
    		char[] chars = new char[dateString.length()];
    		reader.read(chars);
    		reader.close();
    		if(dateString.equals(new String(chars)))
    			return true;
    		else
    			return false;
    	}
    	catch(IOException e)
    	{
    		return false;
    	}
    	finally
    	{
    		if(temp != null) temp.delete();
    	}
    }
    
    /**
     * 
     */
    private static Thread cc = null; // connectivity checker; want only one instance
    public void runBackgroundConnectivityChecker()
    {
    	if(cc == null)
    	{
    		cc = new ConnectivityChecker();
    		cc.setDaemon(true);
    		cc.setPriority(Thread.MIN_PRIORITY);
    		cc.start();
    	}
    }
    
    /**
     * 
     * @author vvelsen
     *
     */
    private class ConnectivityChecker extends Thread
    {
    	public void run()
    	{
    		final int sleepMillis = 30000;
    		final int tolerableResponseLatencyMillis = 3000;
    		final CTATURLFetch fetcher = new CTATURLFetch();
    		boolean connectivityLost = false;
    		
    		while(true)
    		{
    			try
    			{
    				sleep(sleepMillis);
    			} catch(InterruptedException e) { return; }
    			
    			String remoteHost = CTATLink.remoteHost;
    			if(!(remoteHost == null || remoteHost.equals("") || remoteHost.equals("local")))
    			{
    				URL url;
    				try {
						url = new URL("http", remoteHost, "/");
					} catch (MalformedURLException e) { url = null; /* will never happen; HTTP should always be a valid protocol */ }
					
					long result = fetcher.checkConnectivity(url);
					if(!connectivityLost)
					{
						if(result < 0 || result > tolerableResponseLatencyMillis)
						{
							connectivityLost = true;
							lostConnectivity(remoteHost);
						}
					}
					else // connectivityLost == true
					{
						if(result >= 0 && result <= tolerableResponseLatencyMillis)
						{
							connectivityLost = false;
							if(OnlineOfflineManager.isOffline()) // TODO implement offline mode
							{
								regainedConnectivity();
							}
						}
					}
    			}
    		}
    	}
    	
    	/**
    	 * 
    	 * @param remoteHost
    	 */
    	private void lostConnectivity(String remoteHost)
    	{
    		javax.swing.JFrame frame = new javax.swing.JFrame(); // This is just a
    		frame.setAlwaysOnTop(true);                          // trick to send the
    		frame.setVisible(true);                              // JOptionPane to the
    		frame.setVisible(false);                             // front of the screen.

    		int response = JOptionPane.showOptionDialog(frame, "The connection to "+remoteHost+" has been lost or is very slow.\n"+
    				"Would you like to switch to offline mode?", "Local TutorShop -- Bad connection",
    				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
    		
    		if(response == JOptionPane.YES_OPTION)
    		{
    			OnlineOfflineManager.goOffline();  // TODO implement offline mode
    		}
    		
    		frame.dispose();
    	}
    	
    	/**
    	 * 
    	 */
    	private void regainedConnectivity()
    	{
    		javax.swing.JFrame frame = new javax.swing.JFrame(); // This is just a
    		frame.setAlwaysOnTop(true);                          // trick to send the
    		frame.setVisible(true);                              // JOptionPane to the
    		frame.setVisible(false);                             // front of the screen.
    		
    		JOptionPane.showMessageDialog(frame, "Connectivity has been regained. Switching back to online mode...");
    		OnlineOfflineManager.goOnline();
    		
    		frame.dispose();
    	}
    }
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args)
    {
    	javax.swing.JOptionPane.showMessageDialog(null, "Please specify a destination file for diagnostic output when prompted.");
    	
    	javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
    	int result = fc.showSaveDialog(null);
    	if(result == JFileChooser.APPROVE_OPTION)
    	{
    		File f = fc.getSelectedFile();
    		try
    		{
    			f.createNewFile();
    			(new CTATDiagnostics()).dumpDiagnostics(new java.io.PrintWriter(f));
    			javax.swing.JOptionPane.showMessageDialog(null, "Diagnostics info has been written to " + f);
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    			javax.swing.JOptionPane.showMessageDialog(null, e.toString() + "; " + e.getCause() + "; " + e.getMessage());
    			return;
    		}
    	}
    }
/*    
    public static boolean portAvailable (int port) 
    {
    	debug ("portAvailable ()");
    	
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) 
        {
            debug ("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        
        try 
        {
            ss = new ServerSocket (port);
            ss.setReuseAddress (true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } 
        catch (IOException e) 
        {
        	
        } 
        finally 
        {
            if (ds != null) 
            {
                ds.close();
            }

            if (ss != null) 
            {
                try 
                {
                    ss.close();
                } 
                catch (IOException e) 
                {
                    // should not be thrown
                }
            }
        }

        return false;
    }
*/    
}
