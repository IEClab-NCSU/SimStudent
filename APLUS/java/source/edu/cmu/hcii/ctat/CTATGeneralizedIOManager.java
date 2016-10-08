package edu.cmu.hcii.ctat;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * An I/O manager that can read/write to/from (1) file system, 
 * (2) on-disk cache (CTATContentCache), or (3) JAR file as necessary.
 * 
 * @author Kevin Jeffries
 *
 */
public class CTATGeneralizedIOManager extends CTATFileManager {
	
	public enum IOSource { FILE, CACHE, JAR } ;
	
	private IOSource outputDest = IOSource.FILE;
	
	private CTATFileManager fileIOManager = new CTATDesktopFileManager(); // this will perform the file-system I/O. Never let this be null. Default is desktop file manager, but this can be changed by setFileIOManager
	
	private CTATContentCache cache = null;
	private File jarfile = null;
	
	public IOSource getOutputDestination()
	{
		return outputDest;
	}
	
	public void setOutputDestination(IOSource outputDestination)
	{
		this.outputDest = outputDestination;
	}
	
	public CTATFileManager getFileIOManager()
	{
		return fileIOManager;
	}
	
	public boolean setFileIOManager(CTATFileManager fileIOManager)
	{
		if(fileIOManager != null)
		{
			this.fileIOManager = fileIOManager;
			return true;
		}
		else return false;
	}
	
	public CTATContentCache getCache() {
		return cache;
	}

	public void setCache(CTATContentCache cache) {
		this.cache = cache;
	}

	public File getJarfile() {
		return jarfile;
	}

	public void setJarfile(File jarfile) {
		this.jarfile = jarfile;
	}

	public boolean doesFileExist (String aFileURI)
	{
		byte[] bytes;
		
		try {
			bytes = doInput(aFileURI);
		} catch(IOException e) { return false; }
		
		return (bytes != null);
	}
	
	public boolean createDirectory (String aDirURI)
	{
		switch(outputDest)
		{
		case FILE:
			return (new File(aDirURI).mkdirs());
		case CACHE:
			return true; // no need to actually make a dir in the cache -- it will be "created" when something is added to it
		case JAR:
			return false; // cannot write to jar file (yet)
		default:
			return false; // should happen
		}
	}
	
	public String getContents (String aFileURI)
	{
		byte[] bytes;
		
		try {
			bytes = doInput(aFileURI);
			if (bytes == null)
				return null;
		} catch(IOException e) { return null; }
		
		return new String(bytes);
	}
	
	public Element getContentsXML (String aFileURI)
	{
		byte[] bytes;
		try {
			bytes = doInput(aFileURI);
			if (bytes == null)
				return null;
		} catch(IOException e) { return null; }
		
		return parseXML(bytes);
	}
	
	public Element getContentsXMLEncrypted (String aFileURI)
	{
		byte[] bytes;
		try {
			bytes = doInput(aFileURI);
			if (bytes == null)
				return null;
		} catch(IOException e) { return null; }
		
		try {
			bytes = decrypt(bytes);
		} catch(GeneralSecurityException e) { return null; }
		
		return parseXML(bytes);
	}
	
	public boolean setContents (String aFileURI,String aContents)
	{
		if(aContents == null) return false;
		
		try {
			doOutput(aFileURI, aContents.getBytes());
			return true;
		} catch(IOException e) { return false; }
	}
	
	public boolean setContentsEncrypted (String aFileURI,String aContents)
	{
		if(aContents == null) return false;
		
		byte[] encrypted;
		try {
			encrypted = encrypt(aContents.getBytes());
		} catch(GeneralSecurityException e) { return false; }
		if(encrypted == null) return false;
		
		try {
			doOutput(aFileURI, encrypted);
			return true;
		} catch(IOException e) { return false; }
	}
	
	public String getContentsEncrypted(String aFileURI)
	{
		byte[] bytes;
		try 
		{
			bytes = doInput(aFileURI);
			
			if (bytes == null)
				return null;
		} 
		catch(IOException e) 
		{ 
			return null; 
		}
		
		//if(bytes == null) return null;
		
		byte[] decrypted;
		
		try 
		{
			decrypted = decrypt(bytes);
		} catch(GeneralSecurityException e) { return null; }
		
		return decrypted == null ? null : new String(decrypted);
	}
	
	/**
	 * 
	 */
	public boolean configureCTATLink()
	{
		debug ("configureCTATLink()");
		
		return fileIOManager.configureCTATLink();
	}
	
	protected void doOutput(String path, byte[] data) throws IOException
	{
		if(path == null || data == null) return;
		
		switch(outputDest)
		{
		case FILE:
			boolean success = fileIOManager.setContents(path, new String(data));
			if(!success) throw new IOException();
			break;
		case CACHE:
			cache.addToCache(path, data, CTATWebTools.headerDateFmt.format(new Date()), true); // (3rd argument is a last-modified timestamp, representing the current time)
			break;
		case JAR:
			throw new IOException(); // can't write to jar files yet. Maybe this should be added in the future, maybe not.
		}
		
		return;
	}
	
	protected byte[] doInput(String path) throws IOException
	{
		if(path == null) return null;
		
		// first check the file system
		String contents = fileIOManager.getContents(path);
		if(contents != null)
			return contents.getBytes();
		
		// then check the cache
		if(cache != null)
		{
			byte[] bytes = cache.getBytesFromCache(path, true);
			if(bytes != null)
			{
				return bytes;
			}
		}
		
		// finally check the jar file
		if(jarfile != null)
		{
			JarFile jf = new JarFile(jarfile);
			ZipEntry entry = jf.getEntry(path);
			if(entry != null)
			{
				InputStream in = new BufferedInputStream(jf.getInputStream(entry));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int b;
				while((b = in.read()) != -1)
				{
					baos.write(b);
				}
				in.close();
				return baos.toByteArray();
			}
			jf.close();
		}
		
		// could not find the data
		return null;
	}
	
	protected Element parseXML(byte[] bytes)
	{
		Element documentElement = null;
		
		if(bytes != null)
		{
			InputStream is = new ByteArrayInputStream(bytes);
			Document doc;
			try {
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			} catch(Exception e) { return null; }
			
			documentElement =  doc.getDocumentElement();
		}
		
		return documentElement;
	}
	
	protected byte[] decrypt(byte[] encrypted) throws GeneralSecurityException
	{
		if(encrypted == null) return null;
		CTATCryptoUtils crypto = ((CTATLink.crypto == null) ? (new CTATCryptoUtils()) : (CTATLink.crypto)); 
		String decrypted = crypto.decrypt(new String(encrypted), CTATLink.keyString);
		return decrypted == null ? null : decrypted.getBytes();
	}
	
	protected byte[] encrypt(byte[] decrypted) throws GeneralSecurityException
	{
		if(decrypted == null) return null;
		CTATCryptoUtils crypto = ((CTATLink.crypto == null) ? (new CTATCryptoUtils()) : (CTATLink.crypto));
		String encrypted = crypto.encrypt(new String(decrypted), CTATLink.keyString);
		return encrypted == null ? null : encrypted.getBytes();
	}
}
