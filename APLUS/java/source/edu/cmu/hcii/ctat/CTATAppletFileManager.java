/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.hcii.ctat;

import java.io.BufferedInputStream;
//import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.StringWriter;
//import java.io.UnsupportedEncodingException;
//import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
//import java.security.GeneralSecurityException;
//import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sewall
 *
 */
public class CTATAppletFileManager extends CTATGeneralizedIOManager {
	/**
	 *
	 */
	public CTATAppletFileManager () 
	{
		setClassName ("CTATAppletFileManager");
		debug ("CTATAppletFileManager ()");
		
		setOutputDestination(IOSource.JAR);
		setFileIOManager(this);
	} 
	
	/**
	 * @param aDirURI file to check
	 * @return result of {@link #openForRead(String)}; closes the stream
	 */	
	public boolean doesFileExist (String aFileURI)
	{	    
		debug ("doesFileExist ("+aFileURI+")");
		InputStream is = openForRead(aFileURI);
		if (is != null)
			try { is.close(); } catch (Exception ignored) {}
		return is != null;
	}
	
	/**
	 * @param aDirURI directory to create
	 * @throws {@link UnsupportedOperationException}
	 */
	public boolean createDirectory (String aDirURI) throws UnsupportedOperationException
	{
		debug ("createDirectory ("+aDirURI+")");
		try {
			throw new UnsupportedOperationException("createDirectory ("+aDirURI+")");
		} catch (Exception e) {
			debugStack ("CTATAppletFileManager: "+e, e);
		}
		return (false);
	}
	
	protected void doOutput(String path, byte[] data)
	{
		debug ("doOutput ("+path+")");
		try {
			throw new UnsupportedOperationException("doOutput ("+path+", "+
					(data==null ? null : "data["+data.length+"]")+")");
		} catch (Exception e) {
			debugStack ("CTATAppletFileManager: "+e, e);
		}
	}
	
	protected byte[] doInput(String path) throws IOException {

		debug ("doInput("+path+")");
		
		BufferedInputStream bis = openForRead(path);
		if (bis == null)
			return null;
		
		ByteArrayOutputStream contents = readStreamAndClose(bis, path);
		if (contents == null)
			return null;
		
		return contents.toByteArray();
	}		

	/**
	 * Read all bytes from the given stream until EOF.
	 * @param bis input stream to read
	 * @param aFileURI resource name, for error messages
	 * @return buffer with bytes
	 */
	private ByteArrayOutputStream readStreamAndClose(InputStream bis, String aFileURI) {
		debug ("readStreamAndClose("+bis+","+aFileURI+")");

		ByteArrayOutputStream baos = null;
		long len = 0;
		try {
			baos = new ByteArrayOutputStream();
			for( int c = -1; (c = bis.read()) >= 0; ++len )
				baos.write(c);
			bis.close();
			bis = null;
		} catch (Exception e) {
			debugStack("Error reading \""+aFileURI+"\" after "+len+" bytes:\n  "+e+"; cause "+e.getCause(), e);
			try { if (bis != null) bis.close(); } catch (Exception ignored) {}
			return null;
		}
		return baos;
	}
	
	/**
	 * Open a URI on the classpath for reading. Prefixes argument with "/" and
	 * calls {@link Class#getResource(String)}.
	 * @param aFileURI uri as string
	 * @return input stream if successful; null if not found or other error
	 */
	private BufferedInputStream openForRead(String aFileURI) {
		debug ("openForRead("+aFileURI+")");
		
		aFileURI = removeExtraSlashes(aFileURI);

		BufferedInputStream bis = null;
		try {
			URL url = getClass().getResource("/"+aFileURI);
			if (url == null)
				return null;
			URLConnection uConn = url.openConnection();
			InputStream is = uConn.getInputStream();
			bis = new BufferedInputStream(is);
			return bis;
		} catch (Exception e) {
			debugStack("Error opening \""+aFileURI+"\" to read:\n  "+e+"; cause "+e.getCause(), e);
			try { if (bis != null) bis.close(); } catch (Exception ignored) {}
			return null;
		}
	}

	/**
	 * For {@link #removeExtraSlashes(String)}: match 2 or more slashes preceded
	 * by some character other than a colon.
	 */
	private static final Pattern doubleSlash = Pattern.compile("([^:])//+");
	
	/**
	 * For editing filenames or URLs: replace any occurrence of multiple slashes ("/")
	 * with a single slash, except when a colon precedes (as in "http://..."). 
	 * @param aFileURI name to edit
	 * @return changed name
	 */
	private String removeExtraSlashes(String aFileURI) {
		Matcher m = doubleSlash.matcher(aFileURI);
		String result = m.replaceAll("$1/");
		debug("removeExtraSlashes("+aFileURI+") => ("+result+")");
		return result;
	}

	/**
	 * Read the configuration file into {@link CTATLink}
	 * @return false
	 */
	public boolean configureCTATLink()
	{
		debug ("configureCTATLink (): configFilePath "+CTATLink.configFilePath);
		
		String cfp = CTATLink.configFilePath;
		if (cfp == null)
			cfp = "./etc/config.data";
		if (cfp.startsWith("./"))
			cfp = cfp.substring(2);  // strip leading "./" for reading jar

		String decrypted = getContents/*Encrypted*/(cfp);
		if (decrypted==null)
		{
			debug ("Info: no config file available yet");
			return (false);
		}
		return CTATLink.parse(decrypted);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CTATFileManager fm = new CTATAppletFileManager();
		for (String arg : args) {
			boolean exists = fm.doesFileExist(arg);
			String contents = fm.getContents(arg);
			System.out.printf("\n    getContents(%s) => exists %b, length %d, content:\n%s\n",
					arg, exists, (contents == null ? -1 : contents.length()), contents);
			System.out.printf("    setContents(%s, \"junk\"):\n", arg);
			fm.setContents(arg, "junk");
		}
	}

	/**
	 * @param aFileURI file to open
	 * @return input stream to read file
	 */
	public InputStream getInputStream(String aFileURI)
	{
		debug ("getInputStream ("+aFileURI+")");
		
		// Implement in platform specific code
		
		return openForRead(aFileURI);		
	}
}
