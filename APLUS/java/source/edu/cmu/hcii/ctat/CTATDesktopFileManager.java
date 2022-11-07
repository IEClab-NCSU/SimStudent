/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-10-03 12:23:19 -0400 (Thu, 03 Oct 2013) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATDesktopFileManager.java,v 1.23 2012/10/16 17:40:38 sewall Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATDesktopFileManager.java,v $
 Revision 1.23  2012/10/16 17:40:38  sewall
 Add listFiles(), delete().

 Revision 1.22  2012/10/05 15:06:40  vvelsen
 Added a new subclass of the local tutorshop that can work completely offline. Also added a patch for the situation where the applet version of the file manager was assigned as the default manager

 Revision 1.21  2012/10/03 17:50:26  sewall
 First working version with CTATAppletFileManager.

 Revision 1.20  2012/09/18 15:21:55  vvelsen
 We should have a complete and working asset download manager that can be run as part of the config panel (or command line)

 Revision 1.19  2012/09/18 00:47:01  sewall
 Now also save datasetName to config.data.

 Revision 1.18  2012/09/12 14:28:34  vvelsen
 Complete reworking of our http handling code. All file requests now go through our singleton file handler which can handle mounted jar files and as such creates a miniature vfsl

 Revision 1.17  2012/09/06 17:48:55  vvelsen
 Reworking of the local tutorshop to allow easier managment of runtime contexts. The completely local/demo http handler is now in its own file and the CLBridge should now be ready to be tested as a standalone class

 Revision 1.16  2012/08/24 20:09:35  kjeffries
 *** empty log message ***

 Revision 1.15  2012/08/17 22:23:19  kjeffries
 commit after Alvaro's merge -- should compile now but more work needs to be done to ensure that nothing important got lost in the merge

 Revision 1.14  2012/08/17 17:50:32  alvaro
 merging versions

 Revision 1.12  2012/06/12 19:44:45  kjeffries
 set CTATLink.initialized after initializing CTATLink according to config file

 Revision 1.11  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.10  2012/04/13 19:44:38  vvelsen
 Started adding some proper threading and visualization tools for our download manager

 Revision 1.9  2012/04/10 15:14:59  vvelsen
 Refined a bunch of classes that take care of file management and file downloading. We can now generate, save and compare file CRCs so that we can verify downloads, etc

 Revision 1.8  2012/03/29 18:59:07  vvelsen
 Bit of refinement in the desktop file manager, one method had the wrong debug call

 Revision 1.7  2012/03/27 17:28:30  vvelsen
 Added a method to copy files, upgraded the process runner slightly

 Revision 1.6  2012/02/29 17:44:53  vvelsen
 Refined our file classes and local tutorshop to behave better when managed by a loader class. Added some nice utility functions in the file manager

 Revision 1.5  2012/02/24 20:53:35  vvelsen
 Added a bunch of small tools and refinements to file management.

 Revision 1.4  2012/01/26 14:51:21  sewall
 Add accessors for configFilePath, to permit installer add-on to prefix a path.

 Revision 1.3  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.16  2011/11/28 06:33:43  sewall
 1) New args to SingleSessionLauncher constr. 2) Put debug-trace switch in config panel. 3) Add local path to brd files stored locally. 4) Add run.jar and launcher TutorShopUSB.

 Revision 1.15  2011/11/10 23:50:45  sewall
 1. maxCachedFiles now a CTATLink parameter. 2. no longer delete files from disk cache. 3. hash map lookup for cache. 4. ignore rails timestamps in file names. 5. kill old instance by monitor port request.

 Revision 1.14  2011/09/29 04:10:24  sewall
 Port changes from AuthoringTools/java/source/, where this code is now maintained.

 Revision 1.2  2011/09/29 03:24:41  sewall
 Remove hard-coded logging URL from Flash Vars.

 Revision 1.1  2011/08/26 13:12:13  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.13  2011/08/12 20:40:08  kjeffries
 Config file is not encrypted. This makes it easier for the installer to create a customized config file.

 Revision 1.12  2011/08/05 21:02:17  kjeffries
 Added option for using local tutoring service to config file

 Revision 1.11  2011/07/27 21:05:12  kjeffries
 better exception handling

 Revision 1.10  2011/07/22 20:47:05  kjeffries
 Added field to config file corresponding to CTATLink's allowWriting

 Revision 1.9  2011/07/07 20:58:40  kjeffries
 Added support for new remoteHost field in CTATLink

 Revision 1.8  2011/06/08 20:34:59  kjeffries
 Encryption key is now stored as a string rather than in a file.

 Revision 1.7  2011/06/03 20:33:54  kjeffries
 Added getContentsXMLEncrypted method for parsing encrypted XML files.

 Revision 1.6  2011/05/27 20:47:07  kjeffries
 Added getContentsEncrypted method to decrypt files, and methods to read and write a config file that is used to configure the CTATLink fields.

 Revision 1.5  2011/04/28 17:11:56  vvelsen
 Finally connected all the dots and made sure that we can access platform specific drivers (java classes) through a central registry. Please see CTATLink for more information.

 Revision 1.4  2011/03/25 20:38:48  vvelsen
 Added much more capabilties to the USB TutorShop. We can now start problems defined in problem_set.xml files. Much more of the problem sequencing code has been finished but more needs to happen there. Some of the internals have been bolstered.

 Revision 1.3  2011/02/16 16:42:10  vvelsen
 Added more refinement to the update class that will work either in the background or at a time when nobody is doing tutoring.

 Revision 1.2  2011/02/09 13:11:38  vvelsen
 Added proper logging handling to local disk (USB drive) and added encryption code.

 Revision 1.1  2011/02/07 19:50:15  vvelsen
 Added a number of files that can manage local files as well as generate html from parameters and templates.

 $RCSfile: CTATDesktopFileManager.java,v $ 
 $Revision: 19571 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATDesktopFileManager.java,v $ 
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.cmu.pact.Utilities.trace;

/** 
 * @author vvelsen
 */
public class CTATDesktopFileManager extends CTATFileManager
{
	private Writer streamOut=null;
	
	/**
	 *
	 */
	public CTATDesktopFileManager () 
	{
		setClassName ("CTATDesktopFileManager");
		debug ("CTATDesktopFileManager ()");
	} 	
	/**
	 * @return the {@link #configFilePath}
	 */
	public static String getConfigFilePath() 
	{
		return CTATLink.configFilePath;
	}
	/**
	 * @param configFilePath new value for {@link #configFilePath}
	 */
	public static void setConfigFilePath(String configFilePath) 
	{
		CTATLink.configFilePath = configFilePath;
	}
	/**
	 *
	 */  
	public boolean isStreamOpen ()
	{
		if (streamOut!=null)
			return (true);
		
		return (false);
	}
	/**
	 *
	 */  
	public boolean openStream (String aFileURI) 
	{
		debug ("openStream ("+aFileURI+")");
		
		if (streamOut!=null)
		{
			debug ("Stream already open");
			return (true);
		}
		
		File aFile=null;
		
		if (aFileURI.indexOf(".jar")!=-1)
		{
			debug ("Can't create a file in a jar yet");
			
			return (false);
		}
		else
			aFile=new File (aFileURI);
		
		try 
		{
			aFile.createNewFile();
		} 
		catch (IOException e1) 
		{		
			e1.printStackTrace();
			return (false);
		} 
						
		if (!aFile.exists()) 
		{
			debug ("File does not exist: " + aFile);
			return (false);
		}
				
		if (!aFile.isFile()) 
		{
			debug ("Should not be a directory: " + aFile);
			return (false);
		}
				
		if (!aFile.canWrite()) 
		{
			debug ("File cannot be written: " + aFile);
			return (false);
		}

		//use buffering
		try 
		{			
			streamOut=new BufferedWriter (new FileWriter(aFile));
		}
		catch (IOException e) 
		{
			return (false);			
		}			
		
		return (true);
	}	
	/**
	 *
	 */  
	public void closeStream ()
	{
		debug ("closeStream ()");
		
		if (streamOut!=null)
		{
			try 
			{
				streamOut.close();
				streamOut=null;
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				debug ("Error closing output stream");
			}
		}
	}
	/**
	 *
	 */ 
	public void writeToStream (String aContents)
	{
		if (streamOut!=null)
		{
			try 
			{
				streamOut.write (aContents);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			try 
			{
				streamOut.flush();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}			
		}
	}
	/**
	 * Format: ../lib/ctat.jar?afile.txt
	 */	
	public boolean doesFileExist (String aFileURI)
	{
		debug ("doesFileExist ("+aFileURI+")");
		
		File aFile=null;
		
		if (aFileURI.indexOf(".jar?")!=-1)
		{
			return (jarFileExist (aFileURI));			
		}
		else
			aFile=new File (aFileURI);
		
	    boolean exists = aFile.exists();
	    
	    if (!exists) 
	    {
	     return (false);   
	    }
	    
	    return (true);
	}  
	/**
	 *
	 */
	public boolean createDirectory (String aDirURI)
	{
		debug ("createDirectory ("+aDirURI+")");
		
		if (doesFileExist (aDirURI)==true)
		{
			debug ("Directory already exists");
		}
		else
		{
		    boolean success = (new File(aDirURI)).mkdir();
		    if (!success) 
		    {
		    	debug ("Unable to create directory");
		    	return (false);
		    }  			
		}
		
		return (true);
	}
	/** 
	 * @param is
	 * @return
	 */
	public static byte[] getBytesFromInputStream(InputStream is) throws IOException 
	{
		// Get the size of the file
		long length = is.available();

		if (length > Integer.MAX_VALUE) 
		{
				//File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) 
		{
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) 
		{
			throw new IOException("Could not completely read file ");
		}
		
		// Close the input stream and return bytes
		//is.close();
		return bytes;		
	}	
	/** 
	 * @param aFileURI
	 * @return
	 */
	public InputStream getInputStream (String aFileURI)
	{
		debug ("getContents ("+aFileURI+")");
		
		InputStream is=null;
		
		if (doesFileExist (aFileURI)==false)
		{
			debug ("Error: file does not exist");
			return (null);
		}
		
		File aFile=null;
		
		if (aFileURI.indexOf(".jar")!=-1)
		{
			return (getJarInputStream (aFileURI));			
		}
		else
		{
			aFile=new File (aFileURI);
			
			try 
			{
				is=new FileInputStream (aFile);
			} 
			catch (FileNotFoundException e) 
			{			
				e.printStackTrace();
				return (null);
			}
		}
		
		return (is);
	}
	/**
	 *
	 */	
	public String getContents (String aFileURI)
	{    
		debug ("getContents ("+aFileURI+")");
		
		if (doesFileExist (aFileURI)==false)
		{
			debug ("Error: file does not exist");
			return (null);
		}
		
		File aFile=null;
		
		if (aFileURI.indexOf(".jar")!=-1)
		{
			return (getJarContents (aFileURI));			
		}
		else
			aFile=new File (aFileURI);

		StringBuilder contents = new StringBuilder();
    
		try 
		{						
			BufferedReader input =  new BufferedReader (new FileReader(aFile));
						
			try 
			{
				String line = null; //not declared within while loop
				
				/* readLine is a bit quirky : it returns the content of a line 
				 * MINUS the newline. it returns null only for the END of the 
				 * stream. it returns an empty String if two newlines appear 
				 * in a row.
				 */
				while (( line = input.readLine()) != null)
				{
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			}
			catch (IOException e)
			{
				return (null);				
			}
			finally 
			{
				input.close();
			}
		}
		catch (IOException ex)
		{
			//ex.printStackTrace();
			return (null);
		}
    
		return (contents.toString());
	}
	/**
	 *
	 */ 	
	public Element getContentsXML (String aFileURI)
	{
		debug ("getContentsXML ("+aFileURI+")");
		
		if (this.doesFileExist(aFileURI)==false)
		{
			debug ("Error: file does not exist: " + aFileURI);
			return (null);
		}
		
		Document               document=null;
		DocumentBuilderFactory dbf     =null;
		DocumentBuilder        builder =null;  
		  
		try 
		{
			dbf    =DocumentBuilderFactory.newInstance();
			builder=dbf.newDocumentBuilder ();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return (null);
		} 
		 
		String toBeParsed=getContents (aFileURI); // this way we can get it from a Jar
		
		try
		{						
			document=builder.parse(new InputSource(new ByteArrayInputStream(toBeParsed.getBytes("utf-8"))));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return (null);   
		}    
		catch (SAXException e) 
		{
			trace.err("Error while parsing file: "+e+"; cause: "+e.getCause()+"; start, end of content:\n  "+
					(toBeParsed == null ? null : toBeParsed.substring(0, Math.min(70, toBeParsed.length())))+"\n  "+
					(toBeParsed == null ? null : toBeParsed.substring(toBeParsed.length()-Math.min(70, toBeParsed.length()))));
			return (null);   
		}   
		  
		Element root=document.getDocumentElement();
		
		return (root);
	}
	/**
	 *
	 */ 	
	public Element getContentsXMLEncrypted (String aFileURI)
	{
		debug ("getContentsXMLEncrypted ("+aFileURI+")");
		
		String decrypted = getContentsEncrypted(aFileURI);
		
		Document               document=null;
		DocumentBuilderFactory dbf     =null;
		DocumentBuilder        builder =null;  
		  
		try 
		{
			dbf    =DocumentBuilderFactory.newInstance();
			builder=dbf.newDocumentBuilder ();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return (null);
		} 
		  
		try
		{
			ByteArrayInputStream decryptedStream = new ByteArrayInputStream(decrypted.getBytes("UTF-8"));
			document=builder.parse (decryptedStream);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return (null);   
		}    
		catch (SAXException e) 
		{
			System.err.println ("Error while parsing file");
			return (null);
		}   
		  
		Element root=document.getDocumentElement();
		
		return (root);
	}
	/**
	 *
	 */  
	public boolean appendContents (String aFileURI,String aContents) 
	{
		debug ("appendContents ("+aFileURI+")");
		
		File aFile = new File (aFileURI);
		Writer output =null; 
			
/*		
		if (aFile == null) 
		{
			debug ("File should not be null.");
			return (false);
		}
*/		
						
		if (aFile.exists()==false) 
		{
			debug ("File does not exist, creating ...");
			
			try 
			{
				aFile.createNewFile();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				return (false);
			}
		}
		
				
		if (aFile.isDirectory()==true) 
		{
			debug ("Should not be a directory: " + aFile);
			return (false);
		}
				
		if (!aFile.canWrite()) 
		{
			debug ("File cannot be written: " + aFile);
			return (false);
		}

		//use buffering
		try 
		{			
			output=new BufferedWriter (new FileWriter(aFile));
		}
		catch (IOException e) 
		{
			return (false);			
		}			
		
		try 
		{
			// FileWriter always assumes default encoding is OK!
			output.write (aContents);
			output.flush();
			output.close();
		}
		catch (IOException e) 
		{
			try
			{
				output.close();
			}
			catch (IOException closeException)
			{
				return (false);
			}
			
			return (false);
		}
		
		return (true);
	}	
	/**
	 *
	 */  
	public boolean setContents (String aFileURI,String aContents) 
	{
		debug ("setContents ("+aFileURI+")");
		
		File aFile = new File (aFileURI);
		Writer output =null; 
			
/*		
		if (aFile == null) 
		{
			debug ("File should not be null.");
			return (false);
		}
*/		
				
		if (aFile.exists()==false) 
		{
			debug ("File does not exist, creating ...");
			
			try 
			{
				aFile.createNewFile();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				return (false);
			}
		}
				
		if (aFile.isDirectory()==true) 
		{
			debug ("Should not be a directory: " + aFile);
			return (false);
		}
				
		if (!aFile.canWrite()) 
		{
			debug ("File cannot be written: " + aFile);
			return (false);
		}

		//use buffering
		try 
		{			
			output=new BufferedWriter (new FileWriter(aFile));
		}
		catch (IOException e) 
		{
			return (false);			
		}			
		
		try 
		{
			// FileWriter always assumes default encoding is OK!
			output.write (aContents);
			output.flush();
			output.close();
		}
		catch (IOException e) 
		{
			try
			{
				output.close();
			}
			catch (IOException closeException)
			{
				return (false);
			}
			
			return (false);
		}
		
		return (true);
	}
	/**
	 *
	 */  
	public boolean setContentsEncrypted (String aFileURI,String aContents) 
	{
		debug ("setContents ("+aFileURI+")");
		
		String encrypted="";
		try
		{
			encrypted=CTATLink.crypto.encrypt(aContents,CTATLink.keyString);	
		} 
		catch (GeneralSecurityException e) 
		{
			e.printStackTrace();
			return (false);
		}
		
		File aFile = new File (aFileURI);
		Writer output =null; 
			
/*		
		if (aFile == null) 
		{
			debug ("File should not be null.");
			return (false);
		}
*/		
				
		if (!aFile.exists()) 
		{
			debug ("File does not exist: " + aFile);
			return (false);
		}
				
		if (!aFile.isFile()) 
		{
			debug ("Should not be a directory: " + aFile);
			return (false);
		}
				
		if (!aFile.canWrite()) 
		{
			debug ("File cannot be written: " + aFile);
			return (false);
		}

		//use buffering
		try 
		{			
			output=new BufferedWriter (new FileWriter(aFile));
		}
		catch (IOException e) 
		{
			return (false);			
		}			
		
		try 
		{
			// FileWriter always assumes default encoding is OK!
			output.write (encrypted);
			output.flush();
			output.close();
		}
		catch (IOException e) 
		{
			try
			{
				output.close();
			}
			catch (IOException closeException)
			{
				return (false);
			}
			
			return (false);
		}
		
		return (true);
	}
	/**
	 *
	 */ 
	public String getContentsEncrypted(String aFileURI)
	{
		debug("getContentsEncrypted (" + aFileURI + ")");
		
		File aFile = new File(aFileURI);
		
		if (!aFile.exists()) 
		{
			debug ("File does not exist: " + aFile);
			return null;
		}
				
		if (!aFile.isFile()) 
		{
			debug ("Should not be a directory: " + aFile);
			return null;
		}
				
		if (!aFile.canRead()) 
		{
			debug ("File cannot be read: " + aFile);
			return null;
		}
		
		/* Read the file contents into a string */
		
		try
		{
			Reader reader = new FileReader(aFile);
			Writer writer = new StringWriter((int) aFile.length());
			char[] buffer = new char[1024];
			int num;
			
			try
			{
				while((num = reader.read(buffer)) != -1)
				{
					writer.write(buffer, 0, num);
				}
			}
			finally
			{
				reader.close();
			}
			
			String fileContents = writer.toString();
			
			/* Decrypt the string */
			return CTATLink.crypto.decrypt(fileContents, CTATLink.keyString);
		}
		catch (FileNotFoundException e)
		{
			debug("File not found: " + e);
			return null;
		} catch (GeneralSecurityException e) {
			debug("GeneralSecurityException: " + e);
			return null;
		} catch (IOException e) {
			debug("IOException: " + e);
			return null;
		}
	}
	/**
	 * Configure the static fields of CTATLink according to a config file previously made by saveConfigData().
	 * If the config file does not exist, CTATLink retains its default values and 'false' is returned.
	 */ 
	public boolean configureCTATLink()
	{		
		debug("configureCTATLink ()");
		
		// Decrypt the config file.
		String decrypted = getContents/*Encrypted*/(CTATLink.configFilePath);

		return CTATLink.parse(decrypted);
	}
	/**
	 *
	 */
	public boolean saveConfigData()
	{
		return saveConfigData(CTATLink.configFilePath);
	}
	/**
	 * 
	 * @param path path to the config file to overwrite 
	 * @return success/failure
	 */
	public boolean saveConfigData(String path)
	{
		/* Save the values of the configurable static fields of CTATLink to a config file in encrypted form.
		 * This method needs to be called only once for each configuration.
		 * The return value indicates success/failure. 
		 */
		
		debug("saveConfigData ()");
		
		// Create a string that contains the current state of the configurable static fields of CTATLink.
		// Fields are tab-delimited.
		StringBuilder str = new StringBuilder();
		str.append(CTATLink.htdocs); str.append("\t");
		str.append(CTATLink.hostName); str.append("\t");
		str.append(CTATLink.wwwPort); str.append("\t");
		str.append(CTATLink.tsPort); str.append("\t");
		str.append(CTATLink.tsMonitorPort); str.append("\t");
		str.append(CTATLink.remoteHost); str.append("\t");
		str.append(CTATLink.etc); str.append("\t");
		str.append(CTATLink.datashopURL); str.append("\t");
		str.append(CTATLink.datashopFile); str.append("\t");
		str.append(CTATLink.crossDomainPolicy); str.append("\t");
		str.append(CTATLink.adminPasswordFilename); str.append("\t");
		str.append(CTATLink.allowWriting); str.append("\t");
		str.append(CTATLink.useLocalTutoringService); str.append("\t");
		str.append(CTATLink.maxCachedFiles); str.append("\t");
		str.append(CTATLink.printDebugMessages); str.append("\t");
		str.append(CTATLink.datasetName); str.append("\t");
		str.append(CTATLink.noNetwork); str.append("\t");
		str.append(CTATLink.logdir); str.append("\t");
		str.append(CTATLink.showNavButtons); str.append("\t");
		
		//>---------------------------------------------------
		
		if (CTATLink.deployType==CTATLink.DEPLOYFLASH)
			str.append("flash"); str.append("\t");
			
		if (CTATLink.deployType==CTATLink.DEPLOYHTML5)
			str.append("html5"); str.append("\t");
							
		//>---------------------------------------------------
				
		str.append(CTATLink.handlerConfig); str.append("\t");
		
		//>---------------------------------------------------
		
		if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPIDISABLED)
			str.append("disabled"); str.append("\t");
			
		if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPION)
			str.append("on"); str.append("\t");
				
		if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPIAUTO)
			str.append("auto"); str.append("\t");				
		
		//>---------------------------------------------------		
			
		// Encrypt the data and write it to the config file
		try {
			(new File(path)).createNewFile(); // create the config file if it does not already exist
		} catch (IOException e) {
			debug(e.toString());
			return false;
		}
				
		return setContents/*Encrypted*/(path, str.toString());
	}
	/*
	 * 
	 */
	public void getFileProperties (String aFile)
	{
	      // Create a File Object with the entered name.
	      // NOTE : Creating a File object in Java will *NOT* create
	      // the corresponding file on disk. The File object can be used
	      // extract properties about an existing file. If the file does
	      // not exist on disk, the File Object will be created nevertheless.
	 
	      File file = new File(aFile);
	      // Check if this file exists
	      if(! file.exists())
	      {
	          // File does not exist, give error and exit
	          debug ("The File \"" + aFile + "\" does not exist. Please enter a valid FileName.");
	          return;
	      }
	 
	 
	      debug ("**Properties of File " + file.getAbsolutePath() + "**");
	 
	      //First check if the File Object is a Directory.
	      //NOTE : The File object can be used to represent directories also.
	      if(file.isDirectory())
	    	  trace.out(file.getPath() + " is a directory.");
	      else
	      {
	    	  //Size of the file in bytes...
	    	  debug ("Size of file in bytes... " + file.length());
	       }

	       // Check permissions to read and write.
	       if(file.canWrite())
	       {
	            if(file.canRead())
	                debug (file.getPath() + " is read-write.");
	            else
	                debug (file.getPath() + " cannot be read from, but write permissions are allowed.");
	       }
	       else
	       {
	            if(file.canRead())
	                debug (file.getPath() + " is read-only");
	            else
	                debug ("You cannot read or write to " + file.getPath());
	       }
	 
	       // Check the parent of this file..
	       String parent = file.getParent();
	       if(parent == null)
	       {
	            debug (file.getPath() + " is a root directory.");
	       }
	       else
	       {
	            debug ("Parent of " + file.getPath() +" is "+parent+ ".");
	       }
	       // Check if file is hidden.
	       if(file.isHidden())
	       {
	            debug (file.getPath() + " is Hidden.");
	       }
	 
	       // Check when it was last modified..
	       debug (file.getPath() + " was last modified on " + new java.util.Date(file.lastModified()));	  		
	}
	/*
	 * 
	 */	
	public Boolean copyfile (String srFile, String dtFile)
	{
		debug ("copyFile ("+srFile+","+dtFile+")");
		
		try
		{
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);
		  
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
			
			in.close();
			out.close();
			
			debug ("File copied");
		}
		catch (FileNotFoundException ex)
		{
			debug (ex.getMessage() + " in the specified directory.");
			return (false);
		}
		catch(IOException e)
		{
			debug ("IO error copying file");
			debug (e.getMessage());
			return (false);
		}
		
		return (true);
	}	
	/*
	 * 
	 */	
	public Boolean saveFileChecksum (String filename,String checksumFileName,Boolean encrypted)
	{
		debug ("saveFileChecksum ("+filename+","+checksumFileName+")");
		
		String crc=null;
				
		try 
		{
			crc=getMD5Checksum (filename);
		} 
		catch (Exception e) 
		{		
			e.printStackTrace();
			return (false);
		}
		
		// Encrypt the data and write it to the config file
		try 
		{
			(new File(checksumFileName)).createNewFile(); // create the config file if it does not already exist
		} 
		catch (IOException e) 
		{
			debug(e.toString());
			return false;
		}		
		
		if (encrypted==true)
			return (setContentsEncrypted (checksumFileName,crc));
		
		return (setContents (checksumFileName,crc));
	}
	/*
	 * 
	 */	
	public byte[] createChecksum(String filename) throws Exception 
	{
		CTATBase.debug ("CTATDesktopFileManager","createChecksum ("+filename+")");
		
		if (doesFileExist (filename)==false)
		{
			debug ("File does not exist: " + filename);
			return (null);
		}
		
		InputStream fis=new FileInputStream(filename);

		byte[] buffer=new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do 
		{
			numRead = fis.read(buffer);
			
			if (numRead > 0) 
			{
				complete.update(buffer, 0, numRead);
			}
		} 
		while (numRead != -1);

		fis.close();
		return complete.digest();
	}
	/*
	 * see this How-to for a faster way to convert a byte array to a HEX string 
	 * trace.out(getMD5Checksum("apache-tomcat-5.5.17.exe"));
	 */
	public String getMD5Checksum (String filename) throws Exception 
	{
		CTATBase.debug ("CTATDesktopFileManager","getMD5Checksum ("+filename+")");
		
		byte[] b = createChecksum(filename);
		
		if (b==null)
			return (null);
		
		String result = "";

		for (int i=0; i < b.length; i++) 
		{
			result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		
		return result;
	}
	/**
	 * 
	 */
	public String cleanPath (String aPath)
	{
		while (aPath.indexOf("//")!=-1)
		{
			aPath=aPath.replaceAll("//","/");
		}
		
		String clean2=aPath.replaceAll("\\\\\\\\","\\\\");
		
		return (clean2);
	}
	/**
	 * 
	 */
	private String getJarName (String aFileURI)
	{
		int splitIndex=aFileURI.indexOf("?");
		
		if (splitIndex==-1)
			return (aFileURI);
		
		return (aFileURI.substring(0, splitIndex));
	}
	/**
	 * 
	 */
	private String getJarFileName (String aFileURI)
	{
		int splitIndex=aFileURI.indexOf("?");
		
		if (splitIndex==-1)
		{
			return (aFileURI);
		}
		
		String splitString=aFileURI.substring(splitIndex+1);
		
		return (cleanPath (splitString));		
	}	
	/**
	 * 
	 */	
	@SuppressWarnings("resource")
	private InputStream getJarInputStream (String aFileURI)
	{
		debug ("getJarInputStream ("+aFileURI+")");
		
		JarFile jarFile=null;
		
		String jarName=getJarName(aFileURI);
				
		if (jarName==null)
		{
			debug ("Error: jarName is null");
			return (null);
		}
		else
			debug ("Using jar: " + jarName);
		
		try 
		{
			jarFile = new JarFile(jarName);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (null);
		}
		
		String jarFileName=getJarFileName (aFileURI);
		
		if (jarFileName==null)
		{
			debug ("Error: jarFileName is null");
			
			try {jarFile.close();} catch (IOException e1) {e1.printStackTrace();}
			
			return (null);
		}
		else
			debug ("Using jar: " + jarFileName);
		
		JarEntry entry = jarFile.getJarEntry (jarFileName);
		
		if (entry==null)
		{
			debug ("Error: entry is null");
			
			try {jarFile.close();} catch (IOException e1) {e1.printStackTrace();}
			
			return (null);
		}
		
		InputStream input=null;
		
		try 
		{
			input=jarFile.getInputStream (entry);
		}
		catch (IOException e) 
		{
			debug ("Unable to obtain inputstream for entry in jar");
			
			e.printStackTrace();
			
			try {jarFile.close();} catch (IOException e1) {e1.printStackTrace();}
			
			return (null);
		}				
		
		//try {jarFile.close();} catch (IOException e1) {e1.printStackTrace();}
		
		debug ("We should now have an open input stream set on our jar file entry");
		
		// We should not close the jar here because there will be access on it
		// after this method returns.
		
		return (input);
	}
	/**
	 * 
	 */
	private String getJarContents (String aFileURI)
	{    
		debug ("getJarContents ("+aFileURI+")");
		
		JarFile jarFile=null;
		
		try 
		{
			jarFile = new JarFile(getJarName(aFileURI));
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ("");
		}
		
		JarEntry entry = jarFile.getJarEntry(getJarFileName (aFileURI));
		
		InputStream input=null;
		
		try 
		{
			input=jarFile.getInputStream(entry);
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			try {jarFile.close();} catch (IOException e1) {e1.printStackTrace();}
			
			return ("");
		}		
		
		InputStreamReader isr = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(isr);
		String line;
		
		StringBuffer formatter=new StringBuffer ();
		
		try 
		{
			while ((line = reader.readLine()) != null)
			{
			     //trace.out(line);
				formatter.append(line);
				formatter.append("\n");
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ("");
		}
		
		try {jarFile.close();} catch (IOException e1) {e1.printStackTrace();}
		
		return (formatter.toString());
	}	
	/**
	 * 
	 */
	@SuppressWarnings("resource")
	private Boolean jarFileExist (String aJarURI)
	{
		debug ("jarFileExist ("+aJarURI+")");
		
		String aJar=getJarName (aJarURI);
		String filePath=getJarFileName (aJarURI);
		
		debug ("Looking for " + filePath + " in " + aJar);
		
		JarFile jarFile=null;
		
		try 
		{
			jarFile=new JarFile(aJar);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
						
			return (false);
		}		
		
		Enumeration<JarEntry> enm = jarFile.entries();
		
		while (enm.hasMoreElements())
		{	         
	         JarEntry entry = (JarEntry) enm.nextElement();
	         
	         String name = entry.getName();
	         
	         if (name.equalsIgnoreCase(filePath)==true)
	         {
	        	 debug ("File found!");
	        	 
	        	 try {jarFile.close();} catch (IOException e1) {e1.printStackTrace();}
	        	 
	        	 return(true);
	         }	         
		}   
				
		debug ("File not found in jar!");
		
		try {jarFile.close();} catch (IOException e1) {e1.printStackTrace();}
		
		return (false);
	}
	/**
	 * 
	 */
	public void jarListContents (String aJarURI)
	{
		debug ("jarListContents ("+aJarURI+")");
		
		String aJar=getJarName (aJarURI);
		
		JarFile jarFile=null;
		
		try 
		{
			jarFile=new JarFile(aJar);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
						
			return;
		}
		
		Enumeration<JarEntry> enm = jarFile.entries();
		
		while (enm.hasMoreElements())
		{	         
	         JarEntry entry = (JarEntry) enm.nextElement();
	         String name = entry.getName();
	         long size = entry.getSize();
	         long compressedSize = entry.getCompressedSize();
	         //trace.out(name + "\t" + size + "\t" + compressedSize);
	         debug (name + "\t" + size + "\t" + compressedSize);
		}   
		
		try 
		{
			jarFile.close();
		} 
		catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}
	/**
	 * Just a test main to exersize various functions. Currently it's rigged to
	 * verify the crc generation code
	 */
	public static void main (String args[]) throws Exception 	
	{
	   	@SuppressWarnings("unused")
		CTATLink link = new CTATLink(new CTATDesktopFileManager ()); // run the CTATLink constructor;
	   	CTATLink.printDebugMessages=true;
		
		CTATBase.debug ("CTATDesktopFileManager","main ()");		
		
		String inputFile="";
		String outputFile="";
		Boolean enc=false;
		
		for (int i=0;i<args.length;i++)
		{
			if ((args [i].toLowerCase().equals("-input")==true) || (args [i].equals("-inputfile")==true))
			{
				inputFile=args [i+1];
			}
			
			if ((args [i].toLowerCase().equals("-output")==true) || (args [i].equals("-outputfile")==true))
			{
				outputFile=args [i+1];
			}			
			
			if (args [i].toLowerCase().equals("-encrypted")==true)
			{
				if (args [i+1].toLowerCase().equals("yes")==true)
					enc=true;
				else
					enc=false;
			}						
		}
		
		if (inputFile.isEmpty()==true)
		{
			CTATBase.debug ("CTATDesktopFileManager","Please specify an input file");
			return;
		}
		
		if (outputFile.isEmpty()==true)
		{
			CTATBase.debug ("CTATDesktopFileManager","No input specified, creating name from input file ...");
			outputFile=inputFile+".crc";
		}		
		
		CTATBase.debug ("CTATDesktopFileManager","Creating MD5 checksum from: " + inputFile + " and storing into: " + outputFile);
		
		CTATDesktopFileManager fManager=new CTATDesktopFileManager ();
		
		if (fManager.doesFileExist(inputFile)==false)
		{
			CTATBase.debug ("CTATDesktopFileManager","Error: input file does not exist: " + inputFile);
			return;
		}
		
		fManager.saveFileChecksum (inputFile,outputFile,enc);						
	}
	
	/**
	 * Return a list of the filenames in a directory. Items will be pathnames prefixed by dname.
	 * @param dname directory name
	 * @return result of {@link File#listFiles() File.list(dname)}
	 * @throws IOException
	 */
	public String[] listFiles(String dname) {
		File f = new File(dname);
		String[] files = f.list();
		debug("listFiles("+dname+") returns String["+
				(files == null ? "null" : Integer.toString(files.length))+"]" );
		return files;
	}
	
	/**
	 * Delete a file from its directory.
	 * @param fname file name
	 */
	public boolean delete(String fname) {
		File f = new File(fname);
		boolean result = f.delete();
		debug("delete("+fname+") returns "+result);
		return result;
	}
}
