/**
 * @author kjeffries
 */

package edu.cmu.hcii.ctat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.pact.Utilities.trace;

/**
 * This class provides a way to cache resources identified with a URI. The cache
 * can be written to persistent storage and used again in later sessions. Content
 * is not validated for freshness, so it is assumed that all cached content is static.
 * 
 * Writing the content to persistent storage is slow, so this class does not do so immediately
 * as content is added to the cache. Instead, it creates a thread that writes the cached contents
 * to persistent storage every 10 seconds or so. Also, each cache entry is stored as two separate
 * files: one that contains the actual contents and one that contains the other parts of a cache
 * record (such as the corresponding URI). This way, time is saved when initially reading in the
 * cache from persistent storage because the actual contents do not need to be read.
 * 
 * @author kjeffries
 *
 */
public class CTATContentCache extends CTATBase 
{
	/**
	 * A query string having just decimal digits is a Rails timestamp, appended to
	 * a URI reference to a static file in dynamic output and changed whenever
	 * the static file changes, so that clients can assume that cached copies
	 * of these URIs are valid indefinitely. See {@link #alterForQueryString(String)}.
	 */
	private static final Pattern RailsTimeStampPattern = Pattern.compile("\\?[0-9]+$");
	
	/**
	 * A user ID suffix to a file name is a hyphen followed by the hexadecimal UID, which is usually 32 characters long (this regex matches UIDs of 30 or more characters).
	 * This "suffix" will not necessarily be at the end of the URI, because it may be followed by a file extension and/or query string.
	 */
	private static final Pattern UIDSuffixPattern = Pattern.compile("\\-[0-9a-fA-F]{30,}");
	
	private final File cacheDirectory; // the directory where the cache records are stored (null if cache is not written to disk)
	
	private final long maxBytesOnDisk; // max number of bytes that the cache can occupy on disk
	private volatile long bytesOnDisk; // number of bytes that the cache is currently occupying on disk. Remember to update this on all disk writes (and deletes).
	
	private final long maxBytesInMemory; // max number of bytes that the cache can occupy in memory; only the actual content is counted, not references, strings, etc.
	private volatile long bytesInMemory; // number of bytes occupied by the cached contents in memory
	
	private Object lock = new Object(); // synchronization lock for bytesOnDisk and bytesInMemory

	/** Thread to populate in-memory {@link CTATContentCache.CacheRecord}s from disk. */
	private CacheDiskReader initializer = null;
	
	/** Thread to save files to disk cache. */
	private CacheDiskWriter diskWriter = null;

	/**
	 * Metadata about, and possibly actual content of, a file in the local cache. 
	 * If {@link CTATContentCache.CacheRecord#contents} is not null, it holds the file's content.
	 */
	private static class CacheRecord implements Serializable // represents a single file to be cached
	{
		static final long serialVersionUID = 5051897423718905202L; // for backward compatibility
		
		/** Plain-text name, as retrieved from web. */
		public String fileURI;
		
		/** Actual contents. */
		public byte[] contents;
		
		/** Name of the file where this record is stored; null if record is not stored on disk. */
		public String recordFilename;

		/** Name of the file where this record's contents byte array is stored (or null). */
		public String contentsFilename;
		
		/** Timestamp from the Last-Modified header that retrieved the file. */
		public Date lastModified;

		/** This flag should be set true when reading/writing this record from/to disk. It is not serialized
		    (for backward compatability and because serializing it would be redundant -- serialized form is always on disk.) */
		public transient volatile boolean isOnDisk = false;
				
		/**
		 * Create an instance with a file's name, contents and date.
		 * @param fileURI
		 * @param contents
		 * @param lastModified will try to parse according to {@value CTATHTTPHandlerInterface#headerDateFmt}
		 */
		public CacheRecord(String fileURI, byte[] contents, String lastModifiedStr)
		{
			this(fileURI, contents, (Date) null);
			try {
				if (lastModifiedStr != null)
					lastModified = CTATWebTools.headerDateFmt.parse(lastModifiedStr);
			} catch (Exception e) {
				trace.err("Error parsing last-modified string as Date: "+e);
			}
		}

		/**
		 * Create an instance with a file's name, contents and date.
		 * @param fileURI
		 * @param contents
		 * @param lastModified
		 */
		public CacheRecord(String fileURI, byte[] contents, Date lastModified)
		{
			this.fileURI = fileURI;
			this.contents = contents;
			recordFilename = contentsFilename = null;
			this.lastModified = lastModified;
		}
	}
	
	/**
	 * information about a single cache entry; can be used by outside code
	 * for diagnostic/profiling purposes
	 */
	public static class CacheEntryInfo
	{
		public final String fileURI;
		public final int accessFrequency;
		public final int size;
		
		public CacheEntryInfo(String fileURI, int accessFrequency, int size)
		{
			this.fileURI = fileURI;
			this.accessFrequency = accessFrequency;
			this.size = size;
		}
	}
	
	/** This map fileURI => {@link CTATContentCache.CacheRecord} is the actual cache. */
	private HashMap<String, CacheRecord> records;

	/** Queue of most-recently-used files whose contents are cached in memory. */
	private LinkedList<CacheRecord> mruInMemoryQ = null;
	
	private FrequencyTable freqtable;

	/**
	 * Maximum number of files whose contents are cached in memory.
	 * Default value is {@value CTATLink#maxCachedFiles}. 
	 */
	private int maxCachedContents = CTATLink.maxCachedFiles;  // default

	/**
	 * Isolate the Swing and other UI elements in a separate class that needn't be 
	 * loaded by non-Swing platforms such as Android. 
	 * @author sewall
	 */
	public static class UI {

		private javax.swing.JTextArea console=null;
		
		private javax.swing.JProgressBar progressBar=null;

		public static CTATVisualProgressTask visualProgress=null;

		public static javax.swing.JProgressBar refreshprogbar = null; // will be used by LocalTSSystemTray class to show progress of cache refresh operation
	
		/**
		 * @return {@link #console} 
		 */
		public javax.swing.JTextArea getConsole() 
		{
			return console;
		}

		/**
		 * @param aConsole type must be {@link JTextArea}
		 */
		public void setConsole(Object aConsole) {
			if(!(aConsole instanceof javax.swing.JTextArea))
				throw new IllegalArgumentException("Console of wrong type: "+(aConsole.getClass()));
			this.console = (javax.swing.JTextArea) aConsole;
		}

		/**
		 * @param aBar type must be {@link JProgressBar}
		 */
		public void setProgressBar(Object aBar) {
			if(!(aBar instanceof javax.swing.JProgressBar))
				throw new IllegalArgumentException("ProgressBar of wrong type: "+(aBar.getClass()));
			this.progressBar = (javax.swing.JProgressBar) aBar;
		}

		/**
		 * @param string write this string to the {@link #console}.
		 */
		public void appendToConsole(final String string) {
			if (console!=null)
			{
				javax.swing.SwingUtilities.invokeLater(new Runnable()
				{
					public void run() 
					{
						console.append (string);
					}
				});
			}
		}

		public void initializeProgressBar(int i, int totalProgressBarTasks,
				int startProgressBarAt) {
			if (progressBar!=null)
			{
				progressBar.setMinimum(0);
				progressBar.setMaximum(totalProgressBarTasks);
				progressBar.setValue(startProgressBarAt);
			}
		}

		public void updateProgressBar(int current, int totalProgressBarTasks) {
			if (progressBar!=null)
			{			
				progressBar.setValue(current);
				progressBar.setString(current + " out of " + totalProgressBarTasks);
			}
		}
		
		public static boolean isEventDispatchThread() {
			return javax.swing.SwingUtilities.isEventDispatchThread();
		}

		public void updateInBackgroud(final CTATContentCache ccc, final String serverName) {
			javax.swing.SwingWorker<Boolean, Void> sw = new javax.swing.SwingWorker<Boolean, Void>() {
				public Boolean doInBackground() {
					setProgress(0);

					boolean swSuccess = true;
					synchronized(ccc.records)
					{
						int i = 0;
						for(CacheRecord record : ccc.records.values()) // for each record in the cache
						{
							swSuccess = ccc.refreshSingleRecord(serverName, record) && swSuccess;
							int oldval = getProgress();
							int newval = (++i)*100 / ccc.records.size();
							setProgress(newval);
							firePropertyChange("progress", oldval, newval);
						}
					}

					return swSuccess;
				}

				public void done()
				{
					LocalTSSystemTray systray = LocalTSSystemTray.getInstance();
					systray.doneRefreshing();
					systray.showBubbleIfPossible("The requested refresh operation is complete.");
				}
			};
			sw.addPropertyChangeListener(
					new java.beans.PropertyChangeListener() {
						public  void propertyChange(java.beans.PropertyChangeEvent evt) {
							if ("progress".equals(evt.getPropertyName())) {
								if(visualProgress != null)
								{
									visualProgress.setValue((Integer)evt.getNewValue());
								}
								else if(refreshprogbar != null)
								{
									refreshprogbar.setValue((Integer)evt.getNewValue());
									refreshprogbar.repaint();
								}
							}
						}
					});
			sw.execute();
		}

		public static void setVisualProgressOrProgressBar(int newval) {
			if(visualProgress != null)
				visualProgress.setValue(newval);
			else if(refreshprogbar != null)
				refreshprogbar.setValue(newval);
		}

		public static void setVisualProgress(int i) {
			if(UI.visualProgress != null)
				UI.visualProgress.setValue(i);
		}
	}
	
	private UI ui = null;

	/**
	 * Use this constructor if persistent storage is not to be used.
	 */
	public CTATContentCache()
	{
		this (null, false);
		
		setClassName ("CTATContentCache");
		debug ("CTATContentCache ()");		
	}
	
	/**
	 * Use this constructor when the cache is to be read from and/or written to persistent storage.
	 * 
	 * @param cacheDirectory the directory where the cache files are stored
	 * @param write Whether new additions to the cache should be written to persistent storage.
	 */
	public CTATContentCache(File cacheDirectory, boolean write)
	{
		setClassName ("CTATContentCache");
		debug ("CTATContentCache ()");		
		
		debug("CTATContentCache( dir="+cacheDirectory+", write="+write+"" );
		
		this.cacheDirectory = cacheDirectory;
		if(cacheDirectory != null && !cacheDirectory.exists())
			cacheDirectory.mkdirs();
		
		// find out how much space we're allowed to use
		long maxBytesTemp;
		try
		{
			maxBytesTemp = CTATDiagnostics.writableBytes(cacheDirectory);
		}
		catch(NoSuchMethodError e) // this is just in case an old version of ctat.jar is loaded.
		{
			try
			{
				maxBytesTemp = cacheDirectory.getUsableSpace();
			}
			catch(NoSuchMethodError e2) // this is in case we're in an old JRE version where java.io.File#getUsableSpace() doesn't exist
			{
				maxBytesTemp = Integer.MAX_VALUE; // arbitrary default limit on cache size
			}
		}
		maxBytesOnDisk = maxBytesTemp;
		
		try
		{
			maxBytesTemp = CTATDiagnostics.inMemoryCacheSize();
		}
		catch(NoSuchMethodError e)
		{
			// this is just in case an old version of ctat.jar is loaded.
			maxBytesTemp = Runtime.getRuntime().freeMemory();
		}
		maxBytesInMemory = maxBytesTemp;
		
		bytesOnDisk = 0;
		bytesInMemory = 0;
		
		// initialize the map by reading in the cached files that are already stored in cacheDirectory
		records = new HashMap<String, CacheRecord>();
		mruInMemoryQ = new LinkedList<CacheRecord>();
		maxCachedContents = CTATLink.maxCachedFiles;  // CTATLink value could change
		
		if(cacheDirectory != null && cacheDirectory.isDirectory())
		{
			initializer = new CacheDiskReader();
			//initializer.start();
			initializer.initializeCache();
			
			File freqfile = new File(cacheDirectory, "frequencies.txt");
			if(freqfile.exists())
			{
				try
				{
					freqtable = new FrequencyTable(freqfile);
				}
				catch(IOException e)
				{
					debug(e.toString());
					freqtable = new FrequencyTable();
				}
			}
			else
			{
				freqtable = new FrequencyTable();
			}
			
			if(write)
			{
				// start a thread that will write the contents of the cache to disk, allowing the same cache to be used in later sessions
				diskWriter = new CacheDiskWriter();
				diskWriter.setDaemon(true);
				diskWriter.start();
				Runtime.getRuntime().addShutdownHook(new FrequenciesWriter()); // on program exit, run FrequenciesWriter thread that writes to disk the frequencies with which cache entries have been accessed
			}
		}
		else
		{
			freqtable = new FrequencyTable();
		}
	}
	
	/**
	 * Add a console. If {@link #ui} is null, creates new {@link CTATContentCache.UI}.
	 * The argument type is Object to avoid Swing classes in this class's method signatures,
	 * so that this enclosing class remains ok with non-Swing platforms like Android.
	 * @param console for actual type, see @link CTATContentCache.UI#setConsole(JTextArea). 
	 */
	public void setConsole (Object console)
	{
		debug ("setConsole ()");
				
		if (console==null)
		{
			debug ("Error: console is null!");
			return;
		}
		
		if (ui!=null)
		{	
			debug ("Creating new UI object ...");
			
			ui = new UI();
		}	

		if (ui!=null)
		{
			ui.setConsole(console);
		}	
	}

	/**
	 * Add a progressBar. If {@link #ui} is null, creates new {@link CTATContentCache.UI}.
	 * The argument type is Object to avoid Swing classes in this class's method signatures,
	 * so that this enclosing class remains ok with non-Swing platforms like Android.
	 * @param console for actual type, see {@link CTATContentCache.UI#setProgressBar(Object)}
	 */
	public void setProgressBar (Object aBar) 
	{
		if (ui != null)
			ui = new UI();
		
		if (ui!=null)
		{
			ui.setProgressBar(aBar);
		}	
	}	

	/**
	 * Adds an entry to the cache. If the cache is full, the least recently used cache entry is
	 * kicked out (all cache entries stored from a previous session are considered equally old).
	 * 
	 * @param fileURI the URI of the content to be cached
	 * @param contents a byte array containing the entity to be cached
	 * @param lastModifiedStr timestamp for file's contents
	 * @param stripOutUID whether to remove the user ID suffix, which is a hyphen followed by a 32-hex-character ID, from the filename
	 */
	public void addToCache(String fileURI, byte[] contents, String lastModifiedStr, boolean stripOutUID)
	{
		debug ("addToCache ()");
		
		if(fileURI == null || contents == null)
			return;
		
		final String theFileURI = fileURI;

		if (ui != null)
			ui.appendToConsole("Adding to cache: " + theFileURI + "\n");
		
		if ((fileURI = alterForQueryString(fileURI)) == null)
			return;
		
		if(stripOutUID)
			fileURI = stripOutUID(fileURI);
		
		// don't exceed the max number of cache entries
		int numRecords;
		synchronized(records)
		{
			numRecords = records.size();
		}
		if(numRecords >= maxCachedContents)
		{
			removeLFU();
		}
		
		// check that the memory limit is not exceeded
		int count = 0;
		while(((bytesInMemory + contents.length) > maxBytesInMemory) && (count < 10)) // cap number of repetitions at 10 to avoid inadvertent infinite loop
		{
			freeSomeMemory();
			++count;
		}
		
		CacheRecord newRecord = new CacheRecord(fileURI, contents, lastModifiedStr);
		synchronized(records)
		{	
			records.put(newRecord.fileURI, newRecord);
		}
		if (diskWriter != null)
		{
			synchronized(diskWriter.recordsToWrite)
			{
				diskWriter.recordsToWrite.add(newRecord);
			}
		}
		synchronized(lock)
		{
			bytesInMemory += contents.length;
		}
	}
	
	private void freeSomeMemory()
	{
		// Just walk the set of records until one that is both in memory and on disk is found, and kick it out of memory
		int bytesFreed = 0;
		Set<Map.Entry<String, CacheRecord>> entrySet;
		synchronized(records)
		{
			entrySet = records.entrySet();
		}
		for(Map.Entry<String, CacheRecord> entry : entrySet)
		{
			CacheRecord cr = entry.getValue();
			synchronized(cr)
			{
				if(cr.contents != null && (cr.isOnDisk == true || diskWriter == null)) // if the contents are in memory and either also on disk or will never be written to disk
				{
					bytesFreed += cr.contents.length;
					cr.contents = null;
					break; // the method is freeSomeMemory, not freeAllTheMemory. The job is done at this point.
				}
			}
		}
		if(bytesFreed == 0)
		{
			if(diskWriter != null)
			{
				boolean shouldInterrupt = false;
				synchronized(diskWriter.recordsToWrite)
				{
					shouldInterrupt = !(diskWriter.recordsToWrite.isEmpty());
				}
				if(shouldInterrupt)
				{
					diskWriter.interrupt(); // write some stuff to disk; the diskWriter thread removes from memory all the contents that it writes
				}
			}
		}
		synchronized(lock)
		{
			bytesInMemory -= bytesFreed;
		}
		return;
	}
	
	/** Result status for #isFileUpToDate(String, String). */
	enum Status {
		CACHE_INVALID,    // there's no cache entry or it has no date
		READ_FROM_CACHE,  // the cache entry is later than the caller's
		NOT_MODIFIED         // the caller's file is up-to-date with the cache
	}
	
	/**
	 * Check whether a given file is up-to-date with respect to the cached copy.
	 * @param fileURI the URI of the content to be cached
	 * @param lastModifiedStr timestamp for file's contents
	 * @param stripOutUID whether to remove the user ID suffix, which is a hyphen followed by a 32-hex-character ID, from the filename
	 * @returns true if the  
	 */
	public Status isFileUpToDate(String fileURI, String lastModifiedStr, boolean stripOutUID)
	{
		debug ("isFileUpToDate ("+fileURI+","+lastModifiedStr+")");
		
		if(stripOutUID)
			fileURI = stripOutUID(fileURI);
		
		if ((fileURI = alterForQueryString(fileURI)) == null)
			return Status.CACHE_INVALID;
		
		CacheRecord cr = null;
		synchronized(records) {
			cr = records.get(fileURI);
		}
		debug("isFileUpToDate("+fileURI+","+lastModifiedStr+"): "+record2String(cr));
		if (cr == null || cr.lastModified == null)
			return Status.CACHE_INVALID;
		Date lastModified = null;
		try {
			lastModified = CTATWebTools.headerDateFmt.parse(lastModifiedStr);
		} catch (Exception e) {
			if (lastModifiedStr != null)
				trace.err("Error parsing last-modified date \""+lastModifiedStr+"\": "+e);
			return Status.READ_FROM_CACHE;
		}
		if (lastModified.getTime() < cr.lastModified.getTime())
			return Status.READ_FROM_CACHE;
		else
			return Status.NOT_MODIFIED;
	}

	/**
	 * Retrieve the contents of the given URI from the in-memory or local disk
	 * cache, if present. 
	 * @param fileURI key to {@link #records}
	 * @param stripOutUID whether to remove the user ID suffix, which is a hyphen followed by a 32-hex-character ID, from the filename
	 * @return saved contents as a byte array; null if contents are not in the cache
	 */
	public byte[] getBytesFromCache(String fileURI, boolean stripOutUID)
	{
		return getBytesFromCache(fileURI, null, stripOutUID);
	}

	/**
	 * Retrieve the contents of the given URI from the in-memory or local disk
	 * cache, if present. 
	 * @param fileURI key to {@link #records}
	 * @param lastModified optional array to return last-modified time
	 * @param stripOutUID whether to remove the user ID suffix, which is a hyphen followed by a 32-hex-character ID, from the filename
	 * @return saved contents as a byte array; null if contents are not in the cache
	 */
	public byte[] getBytesFromCache(String fileURI, String[] lastModified, boolean stripOutUID)
	{
		if (lastModified != null)      // initialize for early returns
			lastModified[0] = null;
		if(stripOutUID)
			fileURI = stripOutUID(fileURI);
		if ((fileURI = alterForQueryString(fileURI)) == null)
			return null;
		
		// Wait for the initial reads from disk to stop
		if(initializer != null)
		{
			try {
				initializer.join();
				initializer = null; // to avoid wasting time join()ing in the future
			} catch(InterruptedException e) {
				return null;
			}
		}
		
		// try to find the requested URI in the cache
		CacheRecord current = null;
		byte[] contents = null; // to be retrieved from disk
		long bytesRead = 0;
		synchronized(records)
		{
			current = records.get(fileURI);
			if (current != null)
			{
				synchronized(current)
				{
					setAsMRU(current);               // most-recently-used housekeeping
					freqtable.increaseFrequency(current.fileURI);         // most-frequently-used housekeeping
					if (current.contents != null)    // contents already in memory
						contents = current.contents;
					else if(cacheDirectory != null && current.contentsFilename != null) // contents are not in memory; should be on disk
					{
						try {
							ObjectInputStream contentsIn = new ObjectInputStream(new FileInputStream(new File(cacheDirectory, current.contentsFilename)));
							current.contents = (byte[]) contentsIn.readObject();
							bytesRead += current.contents.length;
							contentsIn.close();
						} catch (Exception e) {
							return null;
						}
						contents = current.contents;
					}
					else
					{
						if (cacheDirectory != null)
							trace.err("getBytesFromCache("+fileURI+
									"): CacheRecord found but contents[] and contentsFilename null");
						return null;
					}
				}
			}
		}
		debug("getBytesFromCache("+fileURI+") rtns "+(contents == null ? -1 : contents.length)+
				" bytes, "+bytesRead+" read from disk");
		if (lastModified != null)
			lastModified[0] = CTATWebTools.headerDateFmt.format(current.lastModified);
		synchronized(lock)
		{
			bytesInMemory += bytesRead;
		}
		
		// free some memory if necessary to make sure the memory limit is not exceeded
		int count = 0;
		while((bytesInMemory > maxBytesInMemory) && (count < 10)) // cap number of repetitions at 10 to avoid inadvertent infinite loop
		{
			freeSomeMemory();
			++count;
		}
		
		return contents;
	}

	/**
	 * Call this method to get diagnostic/profiling information on the entries in the cache
	 * @return a set of one CacheEntryInfo object for each entry in cache
	 */
	public Set<CacheEntryInfo> getInfoOnEntries()
	{
		if(initializer != null)
		{
			try
			{
				initializer.join();
			}
			catch(InterruptedException e)
			{
				return null;
			}
			initializer = null;
		}
		
		Set<CacheEntryInfo> set = new HashSet<CacheEntryInfo>();
		
		synchronized(records)
		{
			Set<java.util.Map.Entry<String, CacheRecord>> recordsSet = records.entrySet();
			for(java.util.Map.Entry<String, CacheRecord> entry : recordsSet)
			{
				CacheRecord record = entry.getValue();
				synchronized(record) // synchronize on the CacheRecord
				{
					String fileURI = record.fileURI;
					int accessFrequency = freqtable.getFrequencyOf(fileURI);
					int size;
					if(record.contents != null)
					{
						size = record.contents.length;
					}
					else if(record.contentsFilename != null)
					{
						try {
							ObjectInputStream contentsIn = new ObjectInputStream(new FileInputStream(new File(cacheDirectory, record.contentsFilename)));
							byte[] bytes = (byte[]) contentsIn.readObject();
							size = bytes.length;
							contentsIn.close();
						} catch (Exception e) {
							size = 0;
						}
					}
					else
					{
						size = 0; // not in memory or on disk. Oops.
					}
					set.add(new CacheEntryInfo(fileURI, accessFrequency, size));
				}
			}
		}
		
		return set;
	}
	
	/**
	 * Generally, URIs that contain query strings are not cached. But a query string
	 * containing only decimal digits is a Rails timestamp. In that case, strip the
	 * timestamp and cache the file anyhow. 
	 * @param fileURI URI possibly containing a query string
	 * @return null if the fileURI has a query string and so shouldn't be cached;
	 *         else fileURI either unchanged or with the Rails timestamp removed
	 */
	private String alterForQueryString(String fileURI) {
		if(!(fileURI.contains("?")))
			return fileURI;                     // no "?", so no query string
		Matcher m = RailsTimeStampPattern.matcher(fileURI);
		if (!m.find())
			return null;                        // a real query string, not a timestamp
		String result = fileURI.substring(0, m.start());
		debug("Stripped query string \""+fileURI.substring(m.start())+"\" from "+result);
		return result;
	}
	
	public static String stripOutUID(String fileURI) 
	{
		String altered = fileURI;
		
		if(altered != null) 
		{
			Matcher m = UIDSuffixPattern.matcher(fileURI);
			altered = m.replaceAll(""); // replace all UID suffixes with the empty string
		}
		return altered;
	}

	/**
	 * Position the given record as the most recently used in the in-memory cache list
	 * {@link #mruInMemoryQ}.  Will trim the list to size {@link #maxCachedContents}.
	 * @param current record to insert or reposition
	 */
	private void setAsMRU(CacheRecord current) {
		long bytesFreed = 0;
		synchronized(mruInMemoryQ)
		{
			if (!(mruInMemoryQ.remove(current)))      // false if wasn't in list
			{
				if (maxCachedContents <= mruInMemoryQ.size())   // if > max size
				{                                  // remove least recently used
					CacheRecord bounced = mruInMemoryQ.removeLast();
					if (bounced.contents != null)
					{
						synchronized(bounced)
						{
							bytesFreed += bounced.contents.length;
							bounced.contents = null;           // clear contents. //However, the record is actually in the cache and contents can be read from disk.
						}
					}
				}
			}
			mruInMemoryQ.addFirst(current);    // current now most recently used
		}
		synchronized(lock)
		{
			bytesInMemory -= bytesFreed;
		}
		debug("setAsMRU("+current.fileURI+"): mruInMemoryQ.size "+mruInMemoryQ.size()+
				", bytesFreed "+bytesFreed);
	}

	public String getStringFromCache(String fileURI, boolean stripOutUID)
	{
		byte[] bytes = getBytesFromCache(fileURI, stripOutUID);
		
		if(bytes == null)
		{
			return null;
		}
		
		try
		{
			// the byte sequence may not be valid UTF-8. In that case, just let the String constructor deal with the mess.
			return new String(bytes, "UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			return null;
		}
	}
	
	/**
	 * Refreshes every record in the cache with an updated version of the resource
	 * with the same URI at the specified server.
	 * 
	 * The resources are requested by a simple HTTP GET request that does not specify
	 * cookies or user authentication of any kind.
	 * 
	 * If CTATLink.visualProgress (or CTATLink.refreshprogbar) is not null, it is used as a progress bar for this operation.
	 * 
	 * @param serverName the domain name of the server that will provide the new content
	 * @return true for success, false for failure or only partial success
	 */
	public boolean refreshCache(final String serverName)
	{
		System.err.println("refreshCache("+serverName+")");
		
		if(cacheDirectory == null || !cacheDirectory.isDirectory())
		{
			return false;
		}
		
		if(initializer != null)
		{
			try
			{
				initializer.join();
				initializer = null;
			}
			catch(InterruptedException e)
			{
				return false;
			}
		}
		
		boolean success = true;
		HttpURLConnection.setFollowRedirects(false);
		
		synchronized(records)
		{
			if(records.size() == 0)
			{
				return true;
			}
		}

		if(UI.isEventDispatchThread()) 
		{
			if(ui == null)
				ui = new UI();
			
			ui.updateInBackgroud(this, serverName);
		}
		else
		{
			// no need to use SwingWorker
			synchronized(records)
			{
				int i = 0;
				for(CacheRecord record : records.values()) // for each record in the cache
				{
					success = refreshSingleRecord(serverName, record) && success;
					int newval = (++i)*100 / records.size();
					UI.setVisualProgressOrProgressBar(newval);
				}
			}
		}

		success = refreshRemoteBRDs("http://" + serverName, new File(CTATLink.htdocs, "remoteBRDs"), false) && success;
		
		return success;
		
		/*
		String lastRefreshDate; // don't refresh files that haven't been modified since last refresh
		try {
			FileReader reader = new FileReader(new File(cacheDirectory, "lastrefresh.txt"));
			lastRefreshDate = (new BufferedReader(reader)).readLine();
			reader.close();
		} catch (IOException e) {
			lastRefreshDate = null;
		}
		*/
		
		//String thisRefreshDate = null;
		
		
		
		/*
		// create a Swing interface that show the progress of the refresh operation
		javax.swing.JFrame frame = new javax.swing.JFrame();
		frame.add(new javax.swing.JLabel("Refreshing content"));
		javax.swing.JPanel panel = new javax.swing.JPanel();
		CTATVisualProgressTask progbar = new CTATVisualProgressTask(panel);
		frame.add(panel);
		frame.setVisible(true);
		frame.pack();
		*/
		 
		/*if(CTATLink.visualProgress != null)
		{
			CTATLink.visualProgress.setValue(0);
		}*/
		
		/*synchronized(records)
		{
			int i = 0;
			for(CacheRecord record : records.values()) // for each record in the cache
			{
				success = refreshSingleRecord(serverName, record) && success;
				if(CTATLink.visualProgress != null)
				{
					CTATLink.visualProgress.setValue((++i)*100 / records.size());
				}
			}
		} */
		
		// refresh the BRDs that originally came from the remote server
		
		
		/*
		if(success) {
			try {
				FileWriter writer = (new FileWriter(new File(cacheDirectory, "lastrefresh.txt")));
				writer.write(thisRefreshDate);
				writer.close();
			} catch(IOException e) { }
		}
		*/
		
		//return success;
	}
	
	/**
	 * Refresh the specified files in the cache, and optionally remove all files other than those specified from the cache.
	 * 
	 * If CTATLink.visualProgress is not null, it is used as a progress bar for this operation.
	 * If this method call is a subset of a larger task that the progress bar represents, consider
	 * using the 5-argument version, below.
	 * 
	 * @param serverName domain name of the server that will provide the updated content
	 * @param fileURIs the relative URIs of the files that will be updated
	 * @param deleteOtherFiles if true, all files that are not listed in the fileURIs array
	 * @param stripOutUIDs whether to remove the user ID suffix, which is a hyphen followed by a 32-hex-character ID, from the filenames
	 * @return true for success, false for failure or partial failure
	 */
	public boolean refreshCertainFiles (String serverName, 
										String[] fileURIs, 
										boolean deleteOtherFiles,
										boolean stripOutUIDs)
	{
		return refreshCertainFiles(serverName, fileURIs, deleteOtherFiles, stripOutUIDs, 0, fileURIs.length);
	}
	
	/**
	 * Same as above, but with two more arguments which allow customization of the progress bar.
	 * 
	 * @param serverName domain name of the server that will provide the updated content
	 * @param fileURIs the relative URIs of the files that will be updated
	 * @param deleteOtherFiles if true, all files that are not listed in the fileURIs array
	 * @param stripOutUIDs whether to remove the user ID suffix, which is a hyphen followed by a 32-hex-character ID, from the filenames
	 * @param startProgressBarAt the number of "tasks" that the progress bar already considers completed -- this method will start with a (startProgressBarAt/totalProgressBarTasks)*100% filled progress bar
	 * @param totalProgressBarTasks the number of total tasks, including each file to be refreshed by this method counting as 1 task each, that this progress bar represents -- this method will end with a ((startProgressBarAt+fileURIs.length)/totalProgressBarTasks)*100% filled progress bar
	 * @return true for success, false for failure or partial failure
	 */
	public boolean refreshCertainFiles (String serverName, 
			String[] fileURIs, 
			boolean deleteOtherFiles,
			boolean stripOutUIDs,
			int startProgressBarAt,
			int totalProgressBarTasks)
	{
		debug ("refreshCertainFiles ("+fileURIs.length+")");
		
		if (serverName==null)
		{
			debug("in refreshCertainFiles, serverName is null");
			return false;
		}
		
		/*
		if(fileURIs == null)
			return false;
		*/	
		
		if(startProgressBarAt < 0)
			startProgressBarAt = 0;
		if(totalProgressBarTasks < startProgressBarAt + fileURIs.length)
			totalProgressBarTasks = startProgressBarAt + fileURIs.length;
		
		boolean success = true;

		// put the fileURIs into a hash set for performance reasons
		HashSet<String> fileURIsHash = new HashSet<String>();
		
		for(String s : fileURIs) 
		{
			fileURIsHash.add(s);
		}

		UI.setVisualProgress(0);
		
		debug ("Fetching files ...");
		
		// update the cache entries for each of the fileURIs if the cache is out of date with respect to the version on the server
		int i = 0;

		if(ui != null)
			ui.initializeProgressBar(0, totalProgressBarTasks, startProgressBarAt);
		
		for (String fileURI : fileURIsHash) 
		{
			
			fileURI = alterForQueryString(fileURI);
			CacheRecord cr;
			synchronized(records)
			{
				cr = records.get(fileURI);
			}
			
			if(cr == null) 
			{
				// this file has not yet been added to the cache; request it from server and add to cache

				URL url; // Create a URL that represents this record
				
				debug ("Obtaining: " + "http://" + serverName + "/" + fileURI);
				
				try 
				{
					url = (new URI("http", serverName, fileURI, null)).toURL(); // URI is constructed first to escape illegal characters 
				} 
				catch(Exception e) 
				{
					e.printStackTrace();
					success = false;
					//continue; // this record could not be refreshed, but maybe others can
					return (false);
				}
				
				HttpURLConnection conn;
				try 
				{
					conn = (HttpURLConnection) url.openConnection();
					HttpURLConnection.setFollowRedirects(false);
				} 
				catch(IOException e) 
				{
					e.printStackTrace();
					success = false;
					//continue; // this record could not be refreshed, but maybe others can
					return (false);
				}
				
				String lastModified = conn.getHeaderField("Last-Modified");
				
				if(lastModified == null) 
				{
					lastModified = conn.getHeaderField("Date"); // if the server did not specify a last-modified time, use the current time
					
					if(lastModified == null) 
					{
						lastModified = CTATWebTools.headerDateFmt.format(new Date()); // if all else fails, use current time on local system
					}
				}

				BufferedInputStream bis;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				try 
				{
					bis = new BufferedInputStream(conn.getInputStream());
					int b;
					
					while((b = bis.read()) != -1) 
					{
						baos.write(b);
					}
				} 
				catch(IOException e) 
				{
					e.printStackTrace();
					success = false;
					//continue; // this record could not be refreshed, but maybe others can
					return (false);
				}
				
				byte[] bytes = baos.toByteArray();
				
				addToCache(fileURI, bytes, lastModified, stripOutUIDs);
			}
			else { // the file is already in the cache; update it if it has been modified on the server.
				success = refreshSingleRecord(serverName, cr) && success;
			}

			UI.setVisualProgress( (i)*100 / fileURIsHash.size() );
			
			i++;
			
			if(ui != null)
				ui.updateProgressBar(startProgressBarAt + i, totalProgressBarTasks);
		}

		if(deleteOtherFiles) 
		{
			ArrayList<CacheRecord> recordsToDelete = new ArrayList<CacheRecord>();
			
			synchronized(records) 
			{
				for(CacheRecord cr : records.values()) 
				{ 
					// for each record in cache...
					if(!fileURIsHash.contains(cr.fileURI)) 
					{
						recordsToDelete.add(cr);
					}
				}
			}
			
			for(CacheRecord cr : recordsToDelete) 
			{
				removeFromCache(cr);
			}
		}
		
		return success;
	}
	
	/**
	 * Refresh a BRD or a directory of BRDs (recursively) via the remote server.
	 * 
	 * @param remoteURL the network address of the file
	 * @param fileToRefresh the local File that is to be refreshed
	 * @param forceRefresh set true to refresh even if the local file has been modified more recently than the server's file
	 * @return true for success, false for failure or partial failure
	 */
	public boolean refreshRemoteBRDs (String remoteURL, File fileToRefresh, boolean forceRefresh)
	{	
		debug ("refreshRemoteBRDs ("+remoteURL+","+fileToRefresh+","+forceRefresh+")");

		if (!fileToRefresh.exists())
		{
			return true; // nothing to refresh, so it can't be true that the refresh failed
		}

		if (fileToRefresh.isDirectory())
		{
			File[] subfiles = fileToRefresh.listFiles();
			
			boolean success = true;
			
			for (File subfile : subfiles)
			{
				success = refreshRemoteBRDs(remoteURL + "/" + subfile.getName(), subfile, forceRefresh) && success;
			}
			
			return success;
		}

		// fileToRefresh is an actual file
		if (fileToRefresh.toString().endsWith(".brd"))
		{
			URL url = null;
			HttpURLConnection conn=null;

			try 
			{
				url = makeURL(remoteURL);
				conn = (HttpURLConnection) url.openConnection();
			} 
			catch (MalformedURLException e) 
			{
				debug ("MalformedURLException downloading BRD \""+url+"\": "+e);
				e.printStackTrace();
				return (false);
			} 
			catch (IOException e) 
			{
				debug ("IOException downloading BRD \""+url+"\"");
				e.printStackTrace();
				return (false);
			}

			String lastModified = CTATWebTools.headerDateFmt.format(new Date(fileToRefresh.lastModified()));

			if(!forceRefresh)
			{
				conn.setRequestProperty("If-Modified-Since", lastModified); // refresh only if the server's version has been modified more recently than the local version
			}

			int responseCode=0;

			try 
			{
				responseCode = conn.getResponseCode();
			} 
			catch (IOException e) 
			{
				debug ("IOException obtaining response code from connection for \""+url+"\"");
				e.printStackTrace();
				return (false);
			}

			if(responseCode != 200) // 200 = OK
			{
				if(responseCode == 304) // 304 = Not Modified; does not indicate failure
				{
					debug(url + " has not been modified since " + lastModified);
					return true;
				}
				else
				{
					debug(url + " returned response code " + responseCode);
					return false;
				}
			}

			InputStream brdIn=null;

			try 
			{
				brdIn = new BufferedInputStream(conn.getInputStream());
			} 
			catch (IOException e) 
			{
				debug ("IOException obtaining input stream from connection for \""+url+"\"");
				e.printStackTrace();
				return (false);
			}

			OutputStream brdOut=null;

			try 
			{
				brdOut = new BufferedOutputStream(new FileOutputStream(fileToRefresh));
			} 
			catch (FileNotFoundException e) 
			{				
				debug ("FileNotFoundException attempting to create output stream for BRD on disk for \""+url+"\"");
				e.printStackTrace();
				return (false);
			}

			int b;

			try 
			{
				while((b = brdIn.read()) != -1)
				{
					brdOut.write(b);
				}
			} 
			catch (IOException e) 
			{
				debug ("IOException writing bytes to disk \""+url+"\"");
				e.printStackTrace();
				return (false);
			}

			try 
			{
				brdIn.close();
				brdOut.close();
			} 
			catch (IOException e) 
			{
				debug ("IOException closing input or output stream for BRD \""+url+"\"");
				e.printStackTrace();
				return (false);
			}				

			/*
				if(CTATLink.BRDsAreEncrypted)
				{
					CTATLink.fManager.setContentsEncrypted(fileToRefresh.toString(), CTATLink.fManager.getContents(fileToRefresh.toString()));
				}
			 */
		}

		debug(remoteURL + " successfully refreshed.");

		return true;
	}
	
	/** Match the scheme and scheme-specific components of a URI, requiring a double slash "//". */
	private static final Pattern uriPattern = Pattern.compile("^([a-z][-a-z0-9+.]+):(//.+)");

	/**
	 * Generate a URL object by parsing a URL string through {@link URI#URI(String, String, String)}
	 * to escape characters in the path, like spaces, that can cause trouble.  
	 * @param url URL as string to parse and escape
	 * @return URL object
	 * @throws MalformedURLException
	 */
	URL makeURL(String url) throws MalformedURLException {
		if(url == null)
			throw new MalformedURLException("null host");

		Matcher m = uriPattern.matcher(url);
		if(!m.find())
			throw new MalformedURLException("missing scheme or authority");
		String scheme = m.group(1);
		String ssp = m.group(2);      // scheme-specific part 

		try
		{
			debug ("makeURL() new URI("+scheme+","+ssp+",null)");
			URI uri = new URI(scheme, ssp, null);  // null fragment
			return uri.toURL();
		}
		catch(URISyntaxException e)
		{
			throw new MalformedURLException("Error "+e+" on new URI("+scheme+","+ssp+",null)");
		}
	}

	/** Private method that is called by other methods whose job it is to refresh the cache.
	 *  This method refreshes a single record that already exists in the cache.
	 *  
	 * @param serverName the server that will provide the content
	 * @param record the CacheRecord to refresh
	 * @return true for success, false for failure
	 */
	private boolean refreshSingleRecord(String serverName, CacheRecord record)
	{
		debug ("refreshSingleRecord ()");
		
		if(serverName == null || record == null) {
			return false;
		}
		
		int memorySizeChange = 0;
		int diskSizeChange = 0;
		
		synchronized(record)
		{
			// Create a URL that represents this record
			URL url;
			try {
				url = new URL("http", serverName, record.fileURI);
			} catch (MalformedURLException e) {
				return false;
			}

			// Open a connection to the URL
			HttpURLConnection conn;
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(5000);
			} catch (IOException e) {
				return false;
			}

			if(record.lastModified != null)
			{
				conn.setRequestProperty("If-Modified-Since", CTATWebTools.headerDateFmt.format(record.lastModified));
			}
			
			// Refresh this cache record only if the server responded with "200 OK"
			int responseCode;
			try {
				responseCode = conn.getResponseCode();
			} catch (IOException e) {
				responseCode = 0;
			}
			if(responseCode != 200) // 200 = OK
			{
				if(responseCode == 304) // 304 = Not Modified; does not indicate failure
				{
					debug(record.fileURI + " has not been modified since " + record.lastModified);
					return true;
				}
				else
				{
					debug(record.fileURI + " returned response code " + responseCode);
					return false;
				}
			}
			
			String newLastModified = conn.getHeaderField("Last-Modified");
			if(newLastModified == null) {
				newLastModified = conn.getHeaderField("Date"); // if the server did not specify a last-modified time, use the current time
				if(newLastModified == null) {
					newLastModified = CTATWebTools.headerDateFmt.format(new Date()); // if all else fails, use current time on local system
				}
			}

			// Get an InputStream containing the resource's content
			InputStream contentIn;
			try {
				contentIn = new BufferedInputStream(conn.getInputStream());
			} catch (IOException e) {
				return false;
			}

			// Read the contents from the server
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b;
			try {
				while((b = contentIn.read()) != -1)
				{
					baos.write(b);
				}
			} catch (IOException e) {
				return false;
			} finally {
				try {
					contentIn.close();
				} catch (IOException e) { }
			}
			byte[] newContents = baos.toByteArray();

			// Set the record's contents to be the newly acquired contents. Also write
			// the new contents to disk, overwriting the old contents.
			if(record.contents != null)
			{
				memorySizeChange -= record.contents.length; // the current contents will be removed from memory, and the new contents will eventually only be on disk
			}
			record.contents = newContents;
			try {
				record.lastModified = CTATWebTools.headerDateFmt.parse(newLastModified);
			} catch (ParseException e) {
				debug("Could not parse date " + newLastModified);
				debug(e.toString());
				record.lastModified = null;
			}
			if(record.contentsFilename != null)
			{
				ObjectOutputStream contentsOut = null;
				try {
					File f = new File(cacheDirectory, record.contentsFilename);
					long bytesOverwritten = f.length();
					contentsOut = new ObjectOutputStream(new FileOutputStream(f));
					contentsOut.writeObject(record.contents);
					record.contents = null; // now that the contents are written to disk, take them out of memory to save space
					record.isOnDisk = true;
					diskSizeChange += f.length() - bytesOverwritten;
				} catch (IOException e) {
					return false;
				} finally {
					if(contentsOut != null)
					{
						try {
							contentsOut.close();
						} catch (IOException e) { }
					}
				}
			}
		}
		
		synchronized(lock)
		{
			bytesInMemory += memorySizeChange;
			bytesOnDisk += diskSizeChange;
		}
		
		debug(record.fileURI + " successfully refreshed.");
		return true;
	}
	
	/**
	 * Remove a record from the cache, deleting it from both memory and disk.
	 * Do not call this method while iterating over the records, or while holding a synchronization lock on the records.
	 * @param cr the CacheRecord that is to be removed from cache
	 */
	private void removeFromCache(CacheRecord cr)
	{
		debug ("removeFromCache ("+cr+")");
		
		// remove from in-memory cache
		synchronized(records) {
			records.remove(cr.fileURI);
			freqtable.remove(cr.fileURI);
			if(cr.contents != null)
			{
				bytesInMemory -= cr.contents.length;
			}
		}
		
		// remove from the disk queue if present
		if(diskWriter != null)
		{
			synchronized(diskWriter.recordsToWrite)
			{
				diskWriter.recordsToWrite.remove(cr);
			}
		}
		
		// remove from disk
		if(cr.isOnDisk) {
			deleteFromDisk(cr);
		}
	}
	
	private void deleteFromDisk(CacheRecord cr)
	{
		//debug ("deleteFromDisk ()");
		
		if(cacheDirectory != null)
		{
			long bytesDeleted = 0;
			synchronized(cr) {
				// remove "record" file
				if(cr.recordFilename != null) {
					File recordFile = new File(cacheDirectory, cr.recordFilename);
					bytesDeleted += recordFile.length();
					recordFile.delete();
				}
				if(cr.contentsFilename != null) {
					File contentsFile = new File(cacheDirectory, cr.contentsFilename);
					bytesDeleted += contentsFile.length();
					contentsFile.delete();
				}
			}
			synchronized(lock)
			{
				bytesOnDisk -= bytesDeleted;
			}
		}
	}
	
	private void removeLFU()
	{
		debug ("removeLFU ()");
		
		String LFU = freqtable.getLeastFrequent();
		if(LFU == null)
		{
			return;
		}
		CacheRecord cr;
		synchronized(records)
		{
			cr = records.get(LFU);
		}
		if(cr == null)
		{
			return;
		}
		removeFromCache(cr);
	}
		
	/**
	 * Wait until there are no cache items that have not been written to disk.
	 * Note that this may take multiple iterations of the disk-write procedure
	 * because as the cache entries are written to disk there may be more added simultaneously.
	 */
	public void waitForWrite()
	{
		debug ("waitForWrite ()");
		
		if(diskWriter != null)
		{
			boolean empty;
			synchronized(diskWriter.recordsToWrite)
			{
				empty = diskWriter.recordsToWrite.isEmpty();
			}
			while(!empty)
			{
				try
				{
					diskWriter.lock.wait(); // wait to be notified that the write is complete
				}
				catch(InterruptedException e)
				{
					// ignore
				}
				synchronized(diskWriter.recordsToWrite)
				{
					empty = diskWriter.recordsToWrite.isEmpty();
				}
			}
		}
		return;
	}
	/** Immediately write everything in the cache to disk. 
	 *  take all the stuff that's waiting in memory and immediately write it out.
	 */
	public void immediateWrite()
	{
		debug ("immediateWrite ()");
		
		if (diskWriter != null)
		{
			debug ("We have a disk writer");
			
			if (diskWriter.recordsToWrite.isEmpty()==true)
			{
				debug ("Nothing to write, exiting ...");
				return;
			}
			
			debug("Do interrupt ()");
			
			diskWriter.interrupt(); // make the disk writer do a write

			synchronized (diskWriter.lock) 
			{
				try
				{
					debug ("Waiting for write to be completed ...");
								
					diskWriter.lock.wait(); // wait to be notified that the write is complete
				
					debug ("Disk writer should be finished now");
				}
				catch(InterruptedException e)
				{
					debug ("Caught InterruptedException, ignoring ...");
					return;
				}
			}	
		}
		
		return;
	}	
	
	/**
	 * Thread to read all filenames in the {@link #cacheDirectory} into {@link #records}.
	 * Does not read files' contents.
	 */
	private class CacheDiskReader extends Thread
	{
		/**
		 * 
		 */
		public void run()
		{
			debug ("run ()");
			
			initializeCache ();
		}
		/**
		 * 
		 */
		public void initializeCache ()
		{
			if(cacheDirectory == null)
			{
				return;
			}
			
			bytesOnDisk = 0; // will count the bytes as they are read
			
			// read in all cache records to initialize cache
			if(cacheDirectory.isDirectory())
			{
				File[] cacheFiles = cacheDirectory.listFiles();
				if(cacheFiles == null)
				{
					return;
				}
				
				for(File cacheFile : cacheFiles)
				{
					if(cacheFile.isFile() && cacheFile.getName().startsWith("rec")) // only read in files whose name starts with the word record, indicating the file is a CacheRecord
					{
						try
						{
							ObjectInputStream cacheRecordIn = new ObjectInputStream(new FileInputStream(cacheFile));
							CacheRecord cacheRecord = (CacheRecord) cacheRecordIn.readObject();
							cacheRecord.isOnDisk = true;
							
							synchronized(lock)
							{
								bytesOnDisk += cacheFile.length(); // count the length of the "rec" file
								bytesOnDisk += (new File(cacheDirectory, cacheRecord.contentsFilename)).length(); // count the length of the content file
							}
							
							synchronized(records)
							{
								CacheRecord prev = records.put(cacheRecord.fileURI, cacheRecord); // add the newly read record to the set of records
								
								if(prev != null) // if a record with the same URI has already been read
								{
									// choose the more recently modified (i.e. more up-to-date) record as the one to keep, and delete the other
									CacheRecord recordToDelete;
									if(cacheRecord.lastModified.compareTo(prev.lastModified) >= 0)
									{
										recordToDelete = prev;
									}
									else
									{
										records.put(cacheRecord.fileURI, prev); // prev is the more recently modified -- add it back into cache
										recordToDelete = cacheRecord;
									}
									deleteFromDisk(recordToDelete);
								}
								
								/////////This is for debugging purposes. It writes a file listing all URIs that are cached.
								//FileWriter writer = new FileWriter(new File(cacheDirectory, "index.txt"), true);
								//writer.write(cacheRecord.fileURI + System.getProperty("line.separator"));
								//writer.close();
								//////////////////////////////////////////////////////////////////////////////
							}
							cacheRecordIn.close();
						} catch (Exception e) {
							debug(e.toString());
							continue;
						}
					}
				}
				
				debug("cache initializer: "+records.size()+" cache records stored");
			}			
		}
	}

	/**
	 * The class CacheDiskWriter takes care of writing the cache to disk so it can be used in a later
	 * session. It runs as its own thread because disk access, especially when writing to a 
	 * USB stick, is slow.
	 */
	private class CacheDiskWriter extends Thread
	{
		/** Work queue for this thread. Parent enqueues records to write. */
		private LinkedList<CacheRecord> recordsToWrite = new LinkedList<CacheRecord>();
		
		/** Time to sleep between {@link #run()}s. */
		private final int sleepTime = 10000; // 10 seconds
		
		// threads can wait on this lock to wait for a write cycle to be completed
		private Object lock = new Object(); 
		
		/**
		 * Loop forever over these steps:<ul>
		 * <li>dequeue {@link CTATContentCache.CacheRecord}s from {@link #recordsToWrite},</li>
		 * <li>write the files to disk and</li>
		 * <li>sleep {@link #sleepTime} ms.</li>
		 * </ul>
		 */
		public void run()
		{
			setPriority(MIN_PRIORITY); // don't want this thread to take CPU time away from other threads
			
			if(cacheDirectory == null)
			{
				return; // if the cache is not to be written to disk, there is no need for this thread
			}
			
			// Wait for the initial reads from disk to stop
			if(initializer != null)
			{
				try 
				{
					initializer.join();
				} 
				catch (InterruptedException e) 
				{
					return;
				}
			}
			
			while(true) // run forever, sleeping in between each iteration
			{
				ArrayList<CacheRecord> toWriteThisIteration = null;
				
				synchronized(recordsToWrite)
				{
					toWriteThisIteration = new ArrayList<CacheRecord>(recordsToWrite);
					recordsToWrite.clear();
				}
				
				debug ("Records to write: " + recordsToWrite.size());
				
				for (CacheRecord record : toWriteThisIteration)
				{
					long bytesWritten = 0;
					int bytesRemovedFromMemory = 0;
					
					synchronized(record)
					{
						if(record.recordFilename == null || record.contentsFilename == null) // if the record has not yet been written to disk, write it out
						{
							if(record.contents == null)
							{
								continue; // no need to write it to disk if there's nothing to write
							}
							
							// first check that the allotted disk space will not be exceeded
							
							long estimatedSizeOnDisk = record.contents.length + (6*1024); // estimate by adding 6KB to the amount of data (4KB for record file, 2KB for internal fragmentation)
							int count = 0;
							while((bytesOnDisk + estimatedSizeOnDisk) > maxBytesOnDisk && count < 10)
							{
								freeSomeDiskSpace();
								++count;
							}
							if((bytesOnDisk + estimatedSizeOnDisk) > maxBytesOnDisk)
							{
								removeFromCache(record); // couldn't free enough space to store this record, so it must be deleted
							}
							
							
							// write the contents to disk
							try 
							{
								debug("Writing cache contents with file URI " + record.fileURI + " to disk.");
								
								/*
								if (console!=null)
								{
									SwingUtilities.invokeLater(new Runnable()
									{
										public void run() 
										{
											console.append ("Writing cache contents: " + record.fileURI + "\n");
										}
									});
								}
								*/
								
								if(!cacheDirectory.exists())
									cacheDirectory.mkdirs();
								
								File contentsFile = File.createTempFile("content", ".file", cacheDirectory); // create a file with a unique name starting with "content" and ending with ".file"
								record.contentsFilename = contentsFile.getName();
								ObjectOutputStream contentsOut = new ObjectOutputStream(new FileOutputStream(contentsFile));
								contentsOut.writeObject(record.contents);
								contentsOut.close();
								bytesWritten += contentsFile.length();
							} 
							catch (Exception e) 
							{
								trace.err("Error writing cache contents for file URI " + record.fileURI + " to disk:\n  " +
										e + (e.getCause() == null ? "" : "; cause " + e.getCause()));
								continue;
							}
							
							// set the contents to null (to save space as the contents are already on disk) and write the record to disk
							bytesRemovedFromMemory += record.contents.length;
							record.contents = null;
							
							try 
							{
								debug("Writing cache record with file URI " + record.fileURI + " to disk.");
								
								if(!cacheDirectory.exists())
									cacheDirectory.mkdirs();
								
								File recordFile = File.createTempFile("record", ".file", cacheDirectory); // create a file with a unique name starting with "record" and ending with ".file"
								record.recordFilename = recordFile.getName();
								ObjectOutputStream recordOut = new ObjectOutputStream(new FileOutputStream(recordFile));
								recordOut.writeObject(record);
								recordOut.close();
								bytesWritten += recordFile.length();
							} 
							catch (Exception e) 
							{
								trace.err("Error writing cache record for file URI " + record.fileURI + " to disk:\n  " +
										e + (e.getCause() == null ? "" : "; cause " + e.getCause()));
								continue;
							}
							
							record.isOnDisk = true;
						}
					}
					
					synchronized(lock)
					{
						bytesOnDisk += bytesWritten;
						bytesInMemory -= bytesRemovedFromMemory;
					}
				}
				
				// sleep
				try 
				{
					synchronized(lock)
					{
						debug ("lock.notifyAll();");
						lock.notifyAll(); // let everyone know that a write has been done
					}
					
					debug ("Going into sleep mode ...");
					
					sleep(sleepTime);
					
				} 
				catch(InterruptedException e) 
				{
					// an interrupt means someone wants this thread to stop sleeping and write more 
					// cache entries to disk (probably to free up memory)
					continue; 
				}
			}
		}
	}	
	/**
	 * 
	 */
	private void freeSomeDiskSpace()
	{
		/*
		if(cacheDirectory == null)
			return;
		
		Set<Map.Entry<String, CacheRecord>> entrySet = records.entrySet();
		for(Map.Entry<String, CacheRecord> entry : entrySet)
		{
			CacheRecord cr = entry.getValue();
			if(cr.isOnDisk)
			{
				removeFromCache(cr);
				return;
			}
		}
		*/
		removeLFU();
	}
	
	private class FrequenciesWriter extends Thread
	{
		public void run()
		{
			if(cacheDirectory == null || !cacheDirectory.isDirectory())
				return;
			
			try
			{
				freqtable.writeTo(new File(cacheDirectory, "frequencies.txt"));
			}
			catch(IOException e)
			{
				/* not really much we can do here */
			}
		}
	}

	/** Table to hold the frequency with which cache entries are accessed.
	 *  This is useful for deciding what to kick out of the cache. Information
	 *  can be persistent between program sessions if it is written to a file
	 *  with the writeTo method and read in using the appropriate constructor.
	 */
	private static class FrequencyTable
	{
		private static final String charset = "UTF-8";
		
		/** Table entries will be held in a linked list
		 *  that is sorted from most frequently to
		 *  least frequently accessed entries.
		 */
		private class FrequencyTableEntry
		{
			public FrequencyTableEntry prev; // points to a more frequent (or equal freq) neighbor
			public FrequencyTableEntry next; // points to a less frequent (or equal freq) neighbor
			public String fileURI;
			public int frequency;
		}
		
		private FrequencyTableEntry head; // most frequent entry
		private FrequencyTableEntry tail; // least frequent entry
		private HashMap<String, FrequencyTableEntry> entries; // map from fileURI to the entry in the linked list
		
		/** Construct an empty frequency table */
		public FrequencyTable()
		{
			head = null;
			tail = null;
			entries = new HashMap<String, FrequencyTableEntry>();
		}
		
		/** Read in a file and construct the corresponding frequency table.
		 *  The file format is as specified in the writeTo method.
		 * @param input the file to read
		 * @throws IOException if there is an I/O problem or the file is of the wrong format
		 */
		public FrequencyTable(File input) throws IOException
		{
			this(); // call the default constructor first
			
			if(input == null)
			{
				return;
			}
			
			IOException exception = new IOException("Frequency table input file " + input + " is not formatted correctly.");
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input), charset));
			
			try
			{
				int b = reader.read();
				if(b == -1)
				{
					return;
				}
				while(true) // loop once for each line in the file
				{
					// read the string length field (represented as a decimal string)
					StringBuilder sb = new StringBuilder();
					while(b >= '0' && b <= '9')
					{
						sb.append((char)b);
						b = reader.read();
					}
					int uriLength = Integer.valueOf(sb.toString());

					// the string length should be followed by a space
					if(b != ' ')
					{
						throw exception;
					}

					// read the URI string
					char[] uriChars = new char[uriLength];
					int charsRead = reader.read(uriChars);
					if(charsRead != uriLength)
					{
						throw exception;
					}

					// consume the space
					if(reader.read() != ' ')
					{
						throw exception;
					}

					// read the frequency integer (represented as a decimal string)
					sb = new StringBuilder();
					b = reader.read();
					while(b >= '0' && b <= '9')
					{
						sb.append((char)b);
						b = reader.read();
					}
					int frequency = Integer.valueOf(sb.toString());

					FrequencyTableEntry fte = new FrequencyTableEntry();
					fte.fileURI = new String(uriChars);
					fte.frequency = frequency;
					addToList(fte);

					while(b == '\n' || b == '\r') // consume any remaining line terminators
					{
						b = reader.read();
					}

					if(b == -1)
					{
						break;
					}
				}
			}
			catch(NumberFormatException e)
			{
				throw exception;
			}
			finally
			{
				reader.close();
			}
		}
		
		/** Increases the frequency of a specified entry by 1.
		 *  Call this method when a cache entry is accessed.
		 *  If the specified entry is not already in the table,
		 *  it is assigned an initial frequency of 1.
		 *  
		 *  Integer overflow is always avoided. If possible this is done by
		 *  decreasing the frequencies of all entries by the same amount to keep
		 *  the numbers manageable. If doing such is not possible, the frequency of the
		 *  specified entry, if already at the max integer value, will not be increased.
		 *  
		 * @param fileURI the URI of the entry whose frequency is to be increased. Must not be null or empty string.
		 */
		public synchronized void increaseFrequency(String fileURI)
		{
			if(fileURI == null || fileURI.length() == 0 /*fileURI.isEmpty()*/) // String#isEmpty() is not used to maintain compatibility with Java 1.5
			{
				return;
			}
			
			FrequencyTableEntry fte = entries.get(fileURI);
			if(fte == null)
			{
				// create a new table entry with frequency 1
				fte = new FrequencyTableEntry();
				fte.fileURI = fileURI;
				fte.frequency = 1;
				
				// add it to the linked list
				addToList(fte);
			}
			else
			{
				if(fte.frequency < Integer.MAX_VALUE || reduceAllFrequencies() > 0) // if increasing the frequency will not cause integer overflow
				{
					fte.frequency += 1;
					floatUp(fte);
				}
			}
		}
		
		/** Get the least frequently accessed entry.
		 * @return the least frequent entry, or null if the table is empty
		 */
		public synchronized String getLeastFrequent()
		{
			return ((tail == null) ? null : tail.fileURI);
		}
		
		/** Remove an entry from the frequency table.
		 * @param s the fileURI of the entry 
		 * @return true if the entry was in the table and was removed, false if it was not in the table
		 */
		public synchronized boolean remove(String s)
		{
			FrequencyTableEntry fte = entries.remove(s);
			
			if(fte == null)
			{
				return false;
			}
			
			if(fte != head && fte != tail)
			{
				fte.prev.next = fte.next;
				fte.next.prev = fte.prev;
			}
			else if(fte == head && fte != tail)
			{
				head = fte.next;
				head.prev = null;
			}
			else if(fte != head && fte == tail)
			{
				tail = fte.prev;
				tail.next = null;
			}
			else // fte was the only element in the list
			{
				head = tail = null;
			}
			
			return true;
		}
		
		/**
		 * Get the frequency value for a certain cache entry
		 * @param s the fileURI of the cache entry
		 * @return the associated frequency, or -1 if an entry with the specified fileURI could not be found
		 */
		public synchronized int getFrequencyOf(String s)
		{
			FrequencyTableEntry fte = entries.get(s);
			if(fte == null)
			{
				return -1;
			}
			
			return fte.frequency;
		}
		
		/** Write the table to a file so it can be reconstructed in a later session.
		 *  The output file will consist of a series of lines, one for each table entry.
		 *  Each line starts with a decimal string indicating the string length of that entry's
		 *  URI, followed by a space and then the actual URI, and then a space and a decimal
		 *  string indicating the entry's frequency. Lines (including the last line) are terminated
		 *  with a single '\n' character.
		 *  
		 *  Entries are written in descending order by frequency, so as to speed up
		 *  reconstruction of the sorted list (because new entries are always added to tail of list)
		 *  
		 * @param output the file that the output will be written to
		 * @throws IOException if an I/O problem happens
		 */
		public synchronized void writeTo(File output) throws IOException
		{
			if(output == null)
			{
				return;
			}
			
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), charset));
			
			if(tail == null)
			{
				writer.close();
				return;
			}
			
			// write most frequent entry first, to make reading it back in easier
			for(FrequencyTableEntry fte = head; fte != null; fte = fte.next)
			{
				String line = fte.fileURI.length() + " " + fte.fileURI + " " + fte.frequency + "\n";
				writer.write(line);
			}
			writer.close();
			return;
		}
		
		/** Insert the entry both into the hash map and into its proper place in the linked list.
		 *  Note: Initially inserts at tail then floats up toward head. */
		private synchronized void addToList(FrequencyTableEntry fte)
		{
			entries.put(fte.fileURI, fte);
			if(tail != null) // if the list is not empty
			{
				fte.prev = tail;
				tail.next = fte;
				fte.next = null;
				tail = fte;
			}
			else
			{
				fte.prev = null;
				fte.next = null;
				head = tail = fte;
			}
			floatUp(fte);
		}
		
		/** Reduce the frequency of all entries by the same amount.
		 *  This method should be called to keep values manageable
		 *  in the face of an impending integer overflow. The frequency
		 *  value for the least frequent entry will be reduced to 0
		 *  and all other frequencies will be reduced by an equal amount.
		 *  
		 * @return the amount by which each frequency has been reduced (i.e. the positive value that has been subtracted from each) 
		 */
		private synchronized int reduceAllFrequencies()
		{
			if(head == null)
			{
				return 0;
			}
			
			int reduction = tail.frequency;
			if(reduction <= 0)
			{
				return 0;
			}
			
			// walk through the entire list, reducing each frequency
			for(FrequencyTableEntry fte = head; fte != null; fte = fte.next)
			{
				fte.frequency -= reduction;
			}
			
			return reduction;
		}
		
		/** Move an entry up the linked list (toward head) until it is in its proper place.
		 *  Will not move the entry down, even if that is necessary to achieve a proper ordering. */
		private synchronized void floatUp(FrequencyTableEntry fte)
		{
			while(fte.prev != null && fte.prev.frequency < fte.frequency) // higher-frequency entries should float up (toward head)
			{
				swapWithPrev(fte);
			}
		}
		
		/** Change the position in the linked list of this entry and its immediately previous neighbor.
		 *  No change is made if this entry has no previous neighbor (i.e. it is the head of the list).
		 * @param fte the entry that is to be swapped with its prev neighbor
		 */
		private synchronized void swapWithPrev(FrequencyTableEntry fte)
		{
			FrequencyTableEntry other = fte.prev; // `other` points to the one that was initially fte's `prev` and will end up as fte's `next`
			if(other == null)
			{
				return;
			}
			
			// first remove fte from the linked list
			other.next = fte.next;
			if(other.next != null)
			{
				other.next.prev = other;
			}
			else
			{
				tail = other;
			}
			
			// then insert it back in before `other`
			fte.prev = other.prev;
			other.prev = fte;
			fte.next = other;
			if(fte.prev != null)
			{
				fte.prev.next = fte;
			}
			else
			{
				head = fte;
			}

			return;
		}
	}
	
	/**
	 * Write a usage message and exit.
	 * @param errMsg optional error message.
	 * @return never returns
	 */
	private static void usageExit(String errMsg) 
	{
		if (errMsg != null)
			System.out.printf("\n%s. ", errMsg);
		trace.out(
				"Usage:\n  "+CTATContentCache.class.getSimpleName()+" [-d dir] -cmd file...\n"+
				"where--\n"+
				"  dir     is the cache directory; default is htdocs/cache;\n"+
				"  cmd is one of--\n"+
				"  "+Command.showall+"     means list all files in the cache directory;\n"+
				"  "+Command.list+"        means list the named files in the cache;\n"+
				"  "+Command.remove+"      means remove the named files from the cache;\n"+
				"  "+Command.output+"      means output the named files in the cache to stdout;\n"+
				"  "+Command.update+"      means update (replace) the named files in the cache;\n"+
				"  file... are individual file(s) to list, output or replace (ignored with -a).\n"+
				"  N.B.: always use the URI for files: if there is a leading slash, show it.\n"
		);
		System.exit(2);
	}	
	/**
	 * Dump a {@link CTATContentCache.CacheRecord}.
	 * @param record
	 * @return contents, bracketed with "[]"
	 */
	public static String record2String(CacheRecord record) 
	{
		if (record == null)
			return null;
		StringBuilder sb = new StringBuilder('[');
		sb.append(record.fileURI).append(',');
		sb.append(record.contents == null ? -1 : record.contents.length).append(',');
		sb.append(record.recordFilename).append(',');
		sb.append(record.contentsFilename).append(',');
		sb.append(record.lastModified == null ?
				null : CTATWebTools.headerDateFmt.format(record.lastModified)).append(']');
		return sb.toString();
	}

	/**
	 * The commands we can execute. See {@link CTATContentCache#usageExit(String)} and
	 * {@link CTATContentCache#main(String[]).
	 * Keep all names in lower case, and don't use any starting with "d" or "h" (except help);
	 */
	enum Command {
		showall,
		list,
		help,
		output,
		remove,
		update
	}

	/**
	 * Tool to read and maintain cache entries.
	 * @param sole argument is cacheDirectory
	 */
	public static void main(String[] args) 
	{
		String cacheDirectory = "htdocs"+File.separator+"cache";
		Command cmd = Command.showall;
		
		int i;
		
		for (i = 0; i < args.length && args[i].charAt(0) == '-'; ++i) 
		{
			if (args[i].length() < 2)
				usageExit("Invalid switch argument \""+args[i]+"\"");  // never returns
			
			switch (args[i].charAt(1)) 
			{
				case 'd': 
				case 'D':
							if (++i >= args.length)
								usageExit("No directory specified with \"-d\"");
							
							cacheDirectory = args[i];
							break;
			case 'h': 
			case 'H':
							cmd = Command.help;
							usageExit("Help message");
							break;            // not reached
			default:
							cmd = Command.valueOf(args[i].substring(1).toLowerCase());
							
							if(cmd == null)
								usageExit("Unknown switch argument \""+args[i]+"\"");  // never returns
							
							break;
			}
		}
		
		if (i >= args.length && cmd != Command.showall)
			usageExit("No files to check");

		CTATContentCache.outStream = null;  // turn off tracing
		CTATContentCache ccc = new CTATContentCache(new File(cacheDirectory),
		cmd == Command.update || cmd == Command.remove);
		
		while (true) 
		{
			try 
			{
				ccc.initializer.join();
				break;
			} 
			catch (InterruptedException ie) 
			{
				System.err.println("Exception awaiting initializer: "+ie+
						(ie.getCause() == null ? "" : ";\n  cause "+ie.getCause()));
			}
		}
		
		if (cmd == Command.showall) 
		{
			for (String name : ccc.records.keySet()) 
			{
				CacheRecord record = ccc.records.get(name);
				trace.out(record2String(record));
			}
			
			return;
		}
		
		for ( ; i < args.length; ++i) 
		{
			String alteredName = null;
			CacheRecord record = null;
			
			if(cmd != Command.update) 
			{
				alteredName = ccc.alterForQueryString(args[i]);
				record = (alteredName == null ? null : ccc.records.get(alteredName));
				
				if(record == null) 
				{
					System.err.printf("%s: skipping missing record for %s\n", cmd.toString(), args[i]);
					continue;
				}
			}
			
			switch(cmd) 
			{
				case output:
								if (record == null)
									System.err.printf("\n%-18s => %-18s => %s\n", args[i], alteredName,record2String(record));
								else
									ccc.writeToStdout(record);
								break;
				case remove:
								ccc.removeFromCache(record);
								break;
				case list:
								System.out.printf("\n%-18s => %-18s => %s\n", args[i], alteredName,record2String(record));
								break;
				case update:
								try 
								{
									StringBuilder path = new StringBuilder(args[i]);
									
									if('/' == path.charAt(0))
										path.deleteCharAt(0);
									
									File[] fileInfo = new File[1];
									byte[] contents = readFromDisk(path.toString(), fileInfo);
									
									ccc.addToCache(args[i],
												   contents, 
												   CTATWebTools.headerDateFmt.format(new Date(fileInfo[0].lastModified())),
												   true);
								} 
								catch(Exception e) 
								{
									e.printStackTrace();
								}
								
								break;				
				case help:		
								// for completeness
								break;
				case showall:
								// for completeness
								break;								
			}
		}
		
		ccc.immediateWrite();
		ccc.waitForWrite();
	}

	/**
	 * Write a cached file to stdout.
	 * @param record
	 */
	private void writeToStdout(CacheRecord record) {
		String[] lastModified = new String[1];
		byte[] content = getBytesFromCache(record.fileURI, lastModified, false);
		if (content == null) {
			System.err.printf("Null result retrieving %s; record:\n  %s\n",
					record.fileURI, record2String(record));	
			return;
		}
		if (content.length < 1) {
			System.err.printf("Empty result retrieving %s; record:\n  %s\n",
					record.fileURI, record2String(record));
			return;
		}
		try {
			System.out.write(content);
		} catch(Exception e) {
			System.err.printf("Error %s writing retrieving %s; cause, record:\n  %s\n;\n  %s\n",
					e.toString(), record.fileURI, e.getCause().toString(), record2String(record));
			e.printStackTrace();
		}
	}

	/**
	 * Read the given file from disk into a byte array.
	 * @param fileName relative path name
	 * @param fileInfo to return the File instance
	 * @return contents of file
	 * @throws Exception rethrows any error with the fileName in the message
	 */
	private static byte[] readFromDisk(String fileName, File[] fileInfo) throws Exception 
	{
		try 
		{
			File f = new File(fileName);
			
			if(fileInfo != null)
				fileInfo[0] = f;
			
			byte[] result = new byte[(int) f.length()];
			
			InputStream is = new BufferedInputStream(new FileInputStream(f));
			
			long readLen = is.read(result);
			
			if(trace.getDebugCode("cache"))
				trace.out("cache", "readFromDisk("+fileName+") readLen "+readLen);
		
			is.close();
			
			return result;
		} 
		catch(Exception e) 
		{
			throw new Exception("readFromDisk("+fileName+") error: "+e+"; cause "+e.getCause(), e);
		}		
	}
}
