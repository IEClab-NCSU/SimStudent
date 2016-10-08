/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-09-23 12:26:26 -0400 (Mon, 23 Sep 2013) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATTutorUpdater.java,v 1.31 2012/10/16 20:01:34 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATTutorUpdater.java,v $
 Revision 1.31  2012/10/16 20:01:34  vvelsen
 Lots of changes to align the various http handlers. Most of the work has been done in the DVD handler for FIRE which now uses the progress database to also store user information

 Revision 1.30  2012/10/12 14:34:36  kjeffries
 more GUI/threading fixes

 Revision 1.29  2012/10/10 14:11:56  vvelsen
 Started patching the download code to make sure it works well with the config panel. Lots of bugs right now and at this point in time we should not rely on the download results

 Revision 1.28  2012/10/05 15:06:40  vvelsen
 Added a new subclass of the local tutorshop that can work completely offline. Also added a patch for the situation where the applet version of the file manager was assigned as the default manager

 Revision 1.27  2012/10/05 14:23:35  kjeffries
 fixed some threading issues

 Revision 1.26  2012/09/28 14:15:40  kjeffries
 do updating from curriculum XML in a separate thread

 Revision 1.25  2012/09/28 13:26:34  kjeffries
 fixed null pointer exception

 Revision 1.24  2012/09/21 14:19:59  kjeffries
 when done downloading content, make sure the cached stuff is actually written to disk

 Revision 1.23  2012/09/21 13:19:01  vvelsen
 Quick checkin to get vital code into CVS for FIRE

 Revision 1.22  2012/09/19 12:03:38  vvelsen
 Fix to the syntax in the system tray class, which for some reason wasn't picked up by Eclipse

 Revision 1.21  2012/09/18 15:21:55  vvelsen
 We should have a complete and working asset download manager that can be run as part of the config panel (or command line)

 Revision 1.20  2012/09/14 13:05:48  vvelsen
 Started migrating the curriculum.xml to Octav's new format

 Revision 1.19  2012/08/23 21:05:46  kjeffries
 clean-ups

 Revision 1.18  2012/08/21 21:45:54  kjeffries
 can now update all content that will be required for the curriculum as specified in curriculum.xml file

 Revision 1.17  2012/08/20 15:31:36  kjeffries
 restore some stuff that was lost in the merge

 Revision 1.14  2012/08/15 16:52:21  kjeffries
 URIs for required cache entries can be listed in "required.txt" in cache directory

 Revision 1.13  2012/07/23 16:31:14  kjeffries
 change backslashes to forward slases in path names. other assorted changes.

 Revision 1.12  2012/06/18 22:03:01  kjeffries
 check for connectivity before prompting user to update cache

 Revision 1.11  2012/06/12 19:27:27  kjeffries
 cached content can now be refreshed through this class. the exact mechanics of cache refreshing may need to be improved upon

 Revision 1.10  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.9  2012/05/07 21:10:04  kjeffries
 Copy ctat.jar file rather than simply renaming it when creating backups. Otherwise the rename would fail because ctat.jar is in the classpath.

 Revision 1.8  2012/05/04 20:23:09  kjeffries
 deletes excessive backups of ctat.jar to avoid taking up too much space

 Revision 1.7  2012/04/16 16:37:35  vvelsen
 Added a SwingWorker class that can be accessed through CTATLink. It's pretty much the only way in which random code can access a progress bar and update it the way Swing wants you to do it

 Revision 1.6  2012/04/13 19:44:38  vvelsen
 Started adding some proper threading and visualization tools for our download manager

 Revision 1.5  2012/04/10 15:14:59  vvelsen
 Refined a bunch of classes that take care of file management and file downloading. We can now generate, save and compare file CRCs so that we can verify downloads, etc

 Revision 1.4  2012/03/16 15:15:28  vvelsen
 Small fixes here and there to the monitor, link and other support classes. Mostly reformatting work.

 Revision 1.3  2012/02/29 17:44:53  vvelsen
 Refined our file classes and local tutorshop to behave better when managed by a loader class. Added some nice utility functions in the file manager

 Revision 1.2  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.3  2011/02/16 16:42:10  vvelsen
 Added more refinement to the update class that will work either in the background or at a time when nobody is doing tutoring.

 Revision 1.2  2011/02/15 20:38:35  vvelsen
 Fixed a bug in the url fetch class that would throw errors left and right because it wasn't using the proper java classes. Did some cleaning on debugging as well.

 Revision 1.1  2011/02/15 15:20:42  vvelsen
 Added proper datashop logging to disk through http in the built-in web server. Added encryption code for logfiles, text based streams, etc. Added a first update scheduler that can be used to both retrieve new versions of the application as well as new content and student info.

 $RCSfile: CTATTutorUpdater.java,v $ 
 $Revision: 19526 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATTutorUpdater.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
*/

package edu.cmu.hcii.ctat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;


//import org.jdom.Element;
import org.xml.sax.SAXException;

public class CTATTutorUpdater extends CTATBase
{    		    		
	private CTATURLFetch fetcher=null;
	private CTATDesktopFileManager fManager=null;
	private String rollBack="";
	private JFrame mainPanel=null;
	public String curriculumXML="";
	
	private String updatedContentList="";
	private JTextArea console=null;
	private JProgressBar progressBar=null;
	
	private boolean updateSuccess; // was the update successful?
	
	/**
	 *
	 */
    public CTATTutorUpdater (JFrame aMainPanel,JTextArea aConsole,JProgressBar aProgress) 
    {
    	setClassName ("CTATTutorUpdater");
    	debug ("CTATTutorUpdater ()");

    	mainPanel=aMainPanel;
    	console=aConsole;
    	progressBar=aProgress;
    	
    	fManager=new CTATDesktopFileManager ();
    	fetcher=new CTATURLFetch ();
    }
    /**
     * 
     */
    public void debug (String aMessage)
    {
    	super.debug(aMessage);
    	
    	final String theMessage = aMessage;
    	
    	if (console!=null)
    	{
    		SwingUtilities.invokeLater(new Runnable(){
    			public void run() {
    				console.append(theMessage+"\n");
    			}}
    		);
    	}
    }
    /**
     * 
     */
	public String getUpdatedContentList() 
	{
		return updatedContentList;
	}
	/**
	 * 
	 */
	public String getCurriculumXML ()
	{
		return (curriculumXML);
	}
	/**
	 *
	 */    
    public boolean updateStudentData ()
    {
    	String indexFile="null";
    	    
		try 
		{
			indexFile = fetcher.fetchURL("http://digger.pslc.cs.cmu.edu/tsupdate.xml");
		} 
		catch (MalformedURLException e) 
		{
			debug ("Update file is corrupted or malformed. Please notify the system administrator");
			e.printStackTrace();
	    	CTATLink.appState="normal";
			return (false);
		} 
		catch (IOException e) 
		{
			debug ("Unable to obtain update file from server. Please notify the system administrator");
			e.printStackTrace();
	    	CTATLink.appState="normal";			
			return (false);
		}
		
		debug (indexFile);
		
		// Process the index file and compare with what we already have ...
		
    	return (true);
    }
	/**
	 *
	 */    
    public boolean updateDataShop ()
    {
    	debug ("updateStudentData ()");
    	
    	CTATDataShop dataLogger=new CTATDataShop ();
    	return (dataLogger.migrateData ());   	
    }
	/**
    *  
    */    
	public Boolean backupJar ()
	{
		debug ("backupJar ()");
   			
		File renamer=new File ("download/ctat.jar");
		if (renamer.exists()==false)
		{
			debug ("File doesn't exist yet, clean install");
			return (true);
		}
   	
		rollBack="download/ctat-rollback-"+renamer.lastModified()+".jar";
		
		/*
		if (renamer.renameTo(new File (rollBack))==false)
		{
			debug ("Unable to create backup jar");
			return (false);
		}
		*/
		
		// copy, rather than just rename, the old version to a rollback file. Renaming doesn't work because it's part of the classpath.
		File rollBackFile = new File(rollBack);
		try {
			rollBackFile.createNewFile();
		} catch (IOException e) {
			debug("IOException when creating backup");
			return false;
		}
		InputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(renamer));
		} catch (FileNotFoundException e) {
			// This should never happen; it should be caught above.
			debug("could not find download/ctat.jar");
			return false;
		}
		OutputStream out;
		try {
			out = new BufferedOutputStream(new FileOutputStream(rollBack));
		} catch (FileNotFoundException e) {
			// This shouldn't happen either
			debug("could not find rollback file " + rollBack);
			try { in.close(); } catch(IOException e2) { }
			return false;
		}
		try {
			int b; // b for byte
			while((b = in.read()) != -1) {
				out.write(b);
			}
			out.flush();
			out.close();
			in.close();
		} catch (IOException e) {
			debug("IOException while trying to create backup. Deleting what was written to backup, if any.");
			try {
				out.close();
				in.close();
			} catch (IOException e1) { /* ignore */ }
			if(!rollBackFile.delete()) { // delete any part that was written to the backup file if the whole thing was not written
				debug("Delete failed.");
			}
			return false;
		}
		
		// delete all but the two most recent backups, so as to prevent buildup of old jar files
		File[] downloadFiles = (new File("download")).listFiles();
		Set<File> backups = new HashSet<File>();
		File mostRecentBackup = new File(""), secondMostRecentBackup = new File("");
		for(File f : downloadFiles) { // find out which files are backups and keep track of which are the two most recent
			String name = f.getName();
			if(name.startsWith("ctat-rollback-")) {
				backups.add(f);
				if(name.compareTo(mostRecentBackup.getName()) > 0) {
					secondMostRecentBackup = mostRecentBackup;
					mostRecentBackup = f;
				}
				else if(name.compareTo(secondMostRecentBackup.getName()) > 0) {
					secondMostRecentBackup = f;
				}
			}
		}
		
		for(File g : backups) { // delete all backups that are not one of the two most recent
			if(!g.equals(mostRecentBackup) && !g.equals(secondMostRecentBackup) && !g.getName().equalsIgnoreCase(rollBack)) {
				g.delete();
			}
		}
		
		return (true);	
	}  
	/**
	 *
	 */
	private Boolean rollBackJar ()
	{
		debug ("rollBackJar ()");
		
		File renamer=new File (rollBack);
		
		if (renamer.exists()==false)
		{
			debug ("Error: rollback doesn't exist, fatal error");
			return (false);
		}
		
		if (renamer.renameTo(new File ("download/ctat.jar"))==false)
		{
			debug ("Unable to rollback " + rollBack + " to " + "download/ctat.jar");
			return (false);
		}
		
		return (true);			
	}
	/**
	 * 
	 */
	public boolean updateContent()
	{
		//TODO finalize the decision of how content-updating will actually be done
		
		// initialize CTATLink if necessary
		if(CTATLink.initialized == false)
		{
			new CTATLink(new CTATDesktopFileManager ()); // run the constructor to ensure that the file manager exists
			CTATLink.fManager.configureCTATLink(); // use the file manager to configure CTATLink according to the config file
		}
		
		if(CTATLink.remoteHost.equals("") || CTATLink.remoteHost.equals("local"))
		{
			// nothing to update; source of all content is local
			JOptionPane.showMessageDialog(null, "Refreshing the cache is unnecessary when running in local or offline mode.");
			LocalTSSystemTray.getInstance().doneRefreshing();
			return true;
		}
		
		// Create a cache object for the cache directory that is to be refreshed
		CTATContentCache cache;
		
		if(CTATLink.cache == null)
		{
			File cacheDirectory = new File(CTATLink.htdocs + "/cache/");
			if(!cacheDirectory.exists())
			{
				cacheDirectory.mkdir();
			}
			cache = new CTATContentCache(cacheDirectory, CTATLink.allowWriting);

			CTATLink.cache = cache;
		}
		else
		{
			cache = CTATLink.cache;
		}
		
		// Ask the server what to cache. A list of cacheable filenames should be returned by filesToDownload.php
		boolean success;
		
		try 
		{
			URL url = new URL("http", CTATLink.remoteHost, "/filesToDownload.php");
			java.net.HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if(conn.getResponseCode() >= 400)
			{
				File requiredEntries = new File(CTATLink.htdocs, "cache/required.txt");
				if(requiredEntries.exists())
				{
					BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(requiredEntries)));
					ArrayList<String> lines = new ArrayList<String>();
					String thisLine;
					while((thisLine = reader.readLine()) != null)
					{
						if(thisLine.length() > 0)
							lines.add(thisLine);
					}
					reader.close();
					success = cache.refreshCertainFiles(CTATLink.remoteHost, 
														lines.toArray(new String[0]), 
														false,
														true);
				}
				else
				{
					success = cache.refreshCache(CTATLink.remoteHost);
				}
			}
			else
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				ArrayList<String> lines = new ArrayList<String>();
				String thisLine;
				while((thisLine = reader.readLine()) != null)
				{
					if(thisLine.length() > 0)
						lines.add("/" + thisLine);
				}
				success = cache.refreshCertainFiles(CTATLink.remoteHost,
													lines.toArray(new String[0]),
													true,
													true);
			}
		}
		catch (MalformedURLException e) { debug(e.toString()); success = false; }
		catch (IOException e) { debug(e.toString()); success = false; }
		
    	if(success)
    	{
    		//JOptionPane.showMessageDialog(null, "The refreshing is complete.");
    	}
    	else
    	{
    		JOptionPane.showMessageDialog(null, "An exception occurred which prevented some of the data from being transferred. You may want to try refreshing the cache at a later date.");
    	}
    	
    	return success;
	}
	/**
	 * 
	 */
	private CTATCurriculum buildCurriculumFromXML (String aCurr)
	{
		debug ("buildCurriculumFromXML ()");
						
		CTATCurriculum newCurriculum=null;
		
		try 
		{
			newCurriculum = new CTATCurriculum (aCurr);
		} 
		catch (SAXException e) 
		{
			CTATLink.lastError=e.getMessage();
			e.printStackTrace();
			return (null);
		} 
		catch (IOException e) 
		{
			CTATLink.lastError=e.getMessage();
			e.printStackTrace();
			return (null);
		} 
		catch (ParserConfigurationException e) 
		{
			CTATLink.lastError=e.getMessage();
			e.printStackTrace();
			return (null);
		} 
		catch (FactoryConfigurationError e) 
		{
			CTATLink.lastError=e.getMessage();
			e.printStackTrace();
			return (null);
		}
		
		return (newCurriculum);
	}
	/**
	 * 
	 */
	public CTATCurriculum getCourse (String aCourse)
	{
		debug ("getCourse ("+aCourse+")");
						
		String cleanedCourse=CTATURLParamEncoder.encode(aCourse);
						
		CTATURLFetch fetcher=new CTATURLFetch ();
		
		curriculumXML=null;
		
		try 
		{
			//curriculumXML = fetcher.fetchURL("dev.robotsinmotion.cs2n.org","/courses/"+cleanedCourse+"/course.xml");
			curriculumXML = fetcher.fetchURL(CTATLink.remoteHost,"/courses/"+cleanedCourse+"/course.xml");
		} 
		catch (MalformedURLException e) 
		{		
			//e.printStackTrace();
			debug ("MalformedURLException Error getting: " + fetcher.getObtainedURL ());
			return (null);
		} 
		catch (IOException e) 
		{
			//e.printStackTrace();
			debug ("IOException Error getting: " + fetcher.getObtainedURL ());
			return (null);
		}
				
		//debug (curriculumXML);
		
		CTATCurriculum currResult=buildCurriculumFromXML (curriculumXML);
		
		if(currResult != null)
			currResult.setAssignedClass (cleanedCourse);
		else
		{
			debug("buildCurriculumFromXML returned null");
			return null;
		}
		
		currResult.loadAllProblemSets();
		
		return (currResult);
	}

	/**
	 * http://dev.robotsinmotion.cs2n.org/tutors/problem_sets/Current/measdist1.xml
	 * 
	 * Note: does the updating as a separate thread, to avoid locking up the GUI. Because of this,
	 * the method returns before the update is complete.
	 * 
	 * @param curriculum
	 * @param cache
	 * @param BRDdirectory
	 * @param server
	 * @param skipGlobalAssets if true, get only the files specified in the curriculum
	 * @param doWhenDone
	 */
	public void updateContentFromCurriculum (CTATCurriculum curriculum,
											 CTATContentCache cache, 
											 File BRDdirectory, 
											 String server,
											 boolean skipGlobalAssets,
											 boolean useSwingWorker,
											 Runnable doWhenDone /* some code to execute upon completion of the update */)
	{
		debug ("updateContentFromCurriculum ()");
		
		// declare all the arguments as "final" so they can be accessed from the thread
		final CTATCurriculum aCurriculum = curriculum;
		final CTATContentCache aCache = cache;
		final File aBRDdirectory = BRDdirectory; 
		final String aServer = server;
		final boolean aSkipGlobalAssets = skipGlobalAssets;
		final Runnable aDoWhenDone = doWhenDone;
		
		final CTATTutorUpdater tutorUpdater = this;
		
		class UpdateWorker implements Runnable {
			
			public void run()
			{
				debug ("updateContentFromCurriculum.UpdateWorker ("+
						(aSkipGlobalAssets ? "skipGlobalAssets" : "")+")");
				
				boolean success = true;

				//>-----------------------------------------------------------------
				// Download global assets first ...

				String pathList []=null;

				if(!aSkipGlobalAssets)
				{					
					CTATAssetManager assetManager=new CTATAssetManager ();

					ArrayList <String> toCache=new ArrayList<String> ();

					toCache=assetManager.downloadGlobalAssets (toCache,"/tutors/problem_sets/Assets.xml",aServer);
					toCache=assetManager.downloadGlobalAssets (toCache,"/assets.xml",aServer);
					toCache=assetManager.downloadGlobalAssets (toCache,"/images.xml",aServer);
					toCache=assetManager.downloadGlobalAssets (toCache,"/navigation.xml",aServer);

					if (toCache.size()==0)
					{
						//debug ("Error downloading global assets");
						JOptionPane.showMessageDialog(null,"Error downloading global assets.\n"+CTATLink.lastError);
						tutorUpdater.updateSuccess = false;
						return;
					}
					else
					{
						debug ("Processing "+toCache.size()+" files ...");

						progressBar.setMaximum(toCache.size());
						progressBar.setString("0 out of " + toCache.size());

						pathList=toCache.toArray(new String[toCache.size()]);

						if (pathList==null)
						{
							debug ("Unable to transform file list to file array!");
							//return (false);
							tutorUpdater.updateSuccess = false;
							return;
						}

						debug ("All set for cache transfer");						
					}
				}

				//>-----------------------------------------------------------------
				// Now download problem set xml files ...

				debug ("Getting eligible problem sets; 1st assignment "+aCurriculum.getFirstAssignment());

				Collection<String> probsets=aCurriculum.getRequiredProblemSets();

				debug ("Found " + probsets.size() + " eligible problem sets");

				for (String probset : probsets)
				{
					try
					{
						if(probset.equals("/tutors/problem_sets/166/propspeed-rate/problem_set.xml"))
							continue; // this specific problem set has an error in the xml on the server. Don't download it. TODO make this work-around more general

						URL url = new URL ("http", aServer, probset);

						InputStream in = new BufferedInputStream(url.openStream());

						debug ("Writing: " + aBRDdirectory +" / " + probset);

						File f = new File(aBRDdirectory, probset);

						f.getParentFile().mkdirs();
						f.createNewFile();

						OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
						int b;

						while((b = in.read()) != -1)
						{
							out.write((byte)b);
						}

						in.close();
						out.close();				
					}
					catch(Exception e)
					{
						e.printStackTrace();
						success = false;
					}
				}

				//>-----------------------------------------------------------------
				// Download actual content ...

				success=true;
				
				String newList []=null;

				Collection<String> Assets = aCurriculum.getRequiredAssets(aServer);

				if (Assets!=null)
				{
					newList=appendList (pathList,Assets);
				}	
				else
				{
					newList=pathList;
				}

				Collection<String> SWFs = aCurriculum.getRequiredSWFs();

				if (SWFs!=null)
				{
					newList=appendList (newList,SWFs);
				}	
				
				Collection<String> BRDs = aCurriculum.getRequiredBRDs();

				success = aCache.refreshCertainFiles (aServer,
													  newList,
													  false,
													  true,
													  0,
													  newList.length+BRDs.size());
				
				int BRDDownloaded=0;
				
				for (String BRD : BRDs)
				{
					debug ("Downloading BRD: " + BRD);
					
					success=true;
					
					File f = new File(aBRDdirectory, BRD);
					
					f.getParentFile().mkdirs();
					
					if (f.exists()==false)
					{					
						try 
						{ 
							f.createNewFile(); 
						} 
						catch (IOException e) 
						{ 
							e.printStackTrace(); 
							success=false; 
						}
					}
					//else
					//	debug ("File already exists, no need to create");
					
					if (success==true)
					{
						success = aCache.refreshRemoteBRDs ("http://"+aServer+BRD,f,true);
					}
					else
						debug ("Unable to prepare empty BRD file");
					
					if (success==true)
					{
						debug ("BRD downloaded, marking on progress bar ...");
						progressBar.setValue(newList.length+BRDDownloaded+1);
						progressBar.setString((newList.length+BRDDownloaded+1) +" out of " + (newList.length+BRDs.size()));
						BRDDownloaded++;
					}
					else
						debug ("Error downloading BRD");
				}
								
				//>-----------------------------------------------------------------
				// Finally write the report of all that has been downloaded ...

				StringBuffer formatter=new StringBuffer ();

				for (int i=0;i<newList.length;i++)
				{
					formatter.append(newList [i]);
					formatter.append("\n");
				}
				    
				updatedContentList=formatter.toString();

				CTATDesktopFileManager fManager=(CTATDesktopFileManager) CTATLink.fManager;

				fManager.appendContents (CTATLink.logdir+"downloadlist.txt",updatedContentList);

				debug ("Going into immediate write ...");
				
				aCache.immediateWrite(); // make sure everything that should be cached is actually on disk, not just waiting in memory
				
				debug ("Done with immediate write");
				
				// do the thing that we were asked to do after the download (this probably involves re-activating some GUI components)
				if(aDoWhenDone != null)
				{
					try 
					{
						SwingUtilities.invokeAndWait(aDoWhenDone);
					} catch(Exception e) { success = false; }
				}

				//>-----------------------------------------------------------------
				// And we should be all done

				tutorUpdater.updateSuccess=success;
				
				debug ("Success: " + success);
				
				if ((tutorUpdater.updateSuccess==true) && (CTATLink.generateHTMLIndex==true))
				{						
					tutorUpdater.updateSuccess=generateIndexFromTemplate (aCurriculum);
				}
				
				debug ("Showing result message box ...");
				
				try
				{
					SwingUtilities.invokeAndWait(new Runnable()
					{
						public void run() 
						{
							if(tutorUpdater.updateSuccess)
							{
								JOptionPane.showMessageDialog(null, "Content downloaded successfully.");
							}
							else
								JOptionPane.showMessageDialog(null, "Content download was not completely successful.");
						}
					});
				}
				catch(Exception e)
				{
					debug(e.toString());
				}
			}
		}

		if(useSwingWorker) {
			SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>()
			{
				public Void doInBackground()
				{
					(new UpdateWorker()).run();
					
					return null;
				}
			};

			sw.execute();

			debug ("Just after SwingWorker.execute(): isDone() "+sw.isDone());
		}
		else
		{
			Thread tw = new Thread(new UpdateWorker(), "UpdateWorker");
			tw.start();
			debug ("Just after UpdateWorker.start(); to call join()");
			try {
				tw.join();
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}
	/**
	 * 
	 */
	private String [] appendList (String [] existingAssets,Collection<String> newAssets)
	{		
		debug ("appendList ()");
		
		String [] temp=newAssets.toArray(new String[0]);
		
		debug ("Appending " + temp.length + " entries to " + 
				(existingAssets == null ? "null" : Integer.toString(existingAssets.length)) + " existing entries");

		if (existingAssets == null)
			return temp;
		
		String [] total=new String [existingAssets.length+newAssets.size()];
		
		for (int i=0;i<existingAssets.length;i++)
		{
			total [i]=existingAssets [i];
		}
		
		for (int j=0;j<temp.length;j++)
		{
			total [j+existingAssets.length]=temp [j];
		}		
		
		return (total);
	}
	/**
	 * 
	 */
	public Boolean shouldUpdateCode (Boolean aVisual)
	{
		debug ("shouldUpdateCode ()");
		
		if (aVisual==true)
		{
			boolean updateExists;
			try
			{
				updateExists = newCodeExists();
			}
			catch(Exception e)
			{
				updateExists = true; // err on the safe side
			}
			if(updateExists)
			{
				Object[] options = {"Yes","No"};
				int n = JOptionPane.showOptionDialog(mainPanel,
						"A new update is available for your software, would you like to download\n and install this update?",
						"Info Panel",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[1]);

				debug ("N: " + n);

				if (n!=0) // if not yes (i.e. if "no" or if window is closed without making a selection)
				{
					debug ("We don't want to run the updater right now");
					return (false);
				}
			}
			else
			{
				debug("Already up to date");
				return false;
			}
		}
		
		return (true);
	}
	/**
	 * 
	 */
	private boolean shouldUpdateContent()
	{
		// first, check connectivity
		if(CTATLink.initialized == false)
		{
			new CTATLink(new CTATDesktopFileManager ()); // run the constructor to ensure that the file manager exists
			CTATLink.fManager.configureCTATLink(); // use the file manager to configure CTATLink according to the config file
		}
		URL url;
		try {
			url = new URL("http", CTATLink.remoteHost, "");
		} catch(MalformedURLException e) { url = null; /* will never happen; http is always a valid protocol */ }
		long howfast = fetcher.checkConnectivity(url);
		if(howfast < 0 || howfast > 2000) // if connectivity check fails or takes longer than 2000 milliseconds
		{
			JOptionPane.showMessageDialog(mainPanel, "Connection to the FIRE server is very slow. This may affect this program's performance.");
			return false;
		}
		
		// next, ask user
		int selection = JOptionPane.showConfirmDialog(mainPanel,
				"Would you like to refresh the local cache? Note that this should be done only with a fast and reliable internet connection.",
				"Refresh cached content?",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		if(selection == 0) // 0 means YES
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 * Is there a new version of ctat.jar on the server?
	 * Newness is defined as being modified more recently than our local copy, download/ctat.jar
	 */
	private boolean newCodeExists() throws MalformedURLException, IOException
	{
		//return true;
		File downloadedjar = new File("download/ctat.jar");
		
		if(!downloadedjar.exists())
		{
			return true; // need to update if it has not yet been updated
		}
		
		java.net.URL url = new java.net.URL(CTATLink.updateURL+"/ctat.jar");
		java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
		conn.setRequestMethod("HEAD");
		conn.setRequestProperty("If-Modified-Since", CTATWebTools.headerDateFmt.format(new java.util.Date(downloadedjar.lastModified())));
		conn.connect();
		if(conn.getResponseCode() == 304 /* not modified */)
		{
			return false; // already up-to-date
		}
		else return true;
	}
	/**
	 *
	 */
	private Boolean getNewJar (Boolean isVisual)
	{
		debug ("getNewJar ()");
		
		backupJar ();
				
		String crc=null;
		
		try 
		{
			crc=fetcher.fetchURL (CTATLink.updateURL+"/ctat.jar.crc");
		}
		catch (Exception e)
		{
			debug ("Error getting the crc file for the latest version of the ctat.jar file");
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "The update could not be downloaded.");
			return (rollBackJar ());
		}
				
		debug ("Obtained CRC: " + crc);
				
		try 
		{
			fetcher.getHTTPBinaryFile (CTATLink.updateURL+"/ctat.jar","download/ctat.jar");			
		}
		catch (Exception e)
		{
			debug ("Error getting the latest version of the ctat.jar file");
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "The update could not be downloaded.");
			return (rollBackJar ());
		}
		
		// Now compare CRCs ...
		
		String checkCRC=null;
		
		try 
		{
			checkCRC=fManager.getMD5Checksum ("download/ctat.jar");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "The update could not be downloaded.");
			return (rollBackJar ());
		}
		
		if (checkCRC==null)
		{
			debug ("Error: Unable to calculate CRC downloaded release file");
			JOptionPane.showMessageDialog(null, "The update could not be downloaded.");
			return (rollBackJar ());
		}
		
		if (crc.equals(checkCRC)==false)
		{
			debug ("Error: CRC check failed for downloaded release file ("+crc+" compared to: " +checkCRC+")");
			JOptionPane.showMessageDialog(null, "The update could not be downloaded.");
			return (rollBackJar ());
		}
		
		debug ("Verified CRC, continuing ...");
				
		fManager.getFileProperties ("download/ctat.jar");
				
		return (true);
	}
	/**
	 * update both code and content, if necessary and requested by user
	 * @return true for success, false for failure
	 */
    public boolean runUpdate () 
    {
    	debug ("runUpdate ()");
    	
    	CTATLink.appState="updating";
    	    	    	
    	//updateStudentData ();
    	
    	/**
    	 * We have to be careful with updateStudentData because it will dump whatever
    	 * logs it finds directly into whatever url is provided in CTATLink. By
    	 * default this url is a safe one but you might be uploading garbage data 
    	 * to the real datashop.
    	 */
    	    	
    	//updateDataShop ();
    	
    	boolean success = true;
    	
    	if (shouldUpdateCode (true)==true)
    	{
        	success = getNewJar(true);   		
    	}
    	
    	if(shouldUpdateContent())
    	{
    		success = updateContent() && success;
    	}
    	    	
    	CTATLink.appState="normal";
    	
    	return success;
    }
    /**
     * 
     */
    private boolean generateIndexFromTemplate (CTATCurriculum curriculum)
    {
    	debug ("generateIndexFromTemplate ()");
    	
    	String filename="index.html";
    	String template=CTATLink.htdocs+"templates/start_page.html";
    	
    	if (CTATLink.deployType==CTATLink.DEPLOYFLASH)
    	{
    		filename=CTATLink.htdocs+"index-flash.html";
    	}
    	
    	if (CTATLink.deployType==CTATLink.DEPLOYHTML5)
    	{
    		filename=CTATLink.htdocs+"index-html5.html";
    	}
    	
    	debug ("Generating: " + filename + ", from: " + template);
    	
    	CTATDesktopFileManager copyist=new CTATDesktopFileManager ();
    	
    	if (copyist.copyfile(template,filename)==false)
    	{
    		return (false);
    	}
    	    	
		String indexTemplate=CTATLink.fManager.getContents (filename);

		if (indexTemplate==null)
		{
			debug ("Error: can't load index template");
			return (false);
		}

		debug ("Setting first assignment to: " + curriculum.getFirstAssignment());

		String composite=indexTemplate.replaceFirst ("FIRSTPROBLEM",CTATURLParamEncoder.encode(curriculum.getFirstAssignment()));

		return (CTATLink.fManager.setContents(filename,composite));		
    }
}
