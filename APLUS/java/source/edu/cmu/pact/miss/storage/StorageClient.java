package edu.cmu.pact.miss.storage;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;

/**
 * StorageClient - The client end of a storage servlet which communicates over http to
 * store and retrieve text data from a server
 * 
 * @author Victoria Keiser
 * @version 2010.1015
 */
public class StorageClient extends StorageAccess {
	
	String locationURL;
	
	/**
	 * Default Constructor
	 * 
	 * Assumes that the servlet is located at http://mocha.pslc.cs.cmu.edu
	 * 
	 */
	public StorageClient()
	{
		//locationURL = "http://10.16.0.133:2401";
		 //locationURL = "http://127.0.0.1:8080";
		 locationURL = "http://kona.education.tamu.edu:2401";
		//locationURL = "http://mocha.pslc.cs.cmu.edu";
		//locationURL = "http://latte.pslc.cs.cmu.edu:8080";
		//locationURL = "http://10.0.64.50:8080";
		//locationURL = "http://172.17.4.1:2401";
	}
	
	/**
	 * Constructor
	 * 
	 * @param url The URL location of the storage servlet to store at
	 * 
	 */
	public StorageClient(String url)
	{
		locationURL = url;
	}
	
	/**
	 * retrieve String - fetches data from the server as a string
	 * @see edu.cmu.pact.miss.storage.StorageAccess#retrieveString(java.lang.String)
	 * @throws IOException If the URL fails to connect to the servlet
	 * @param key the key associated with the desired data, eg username
	 * @return the retrieved data as a String, or null if no data is associated with that key
	 */
	@Override
	public String retrieveString(String key) throws IOException
	{
		//String location = locationURL+"/SimStSVHS/stores?cmd=retrieve&userid="+key;		
		String location = locationURL+"/Servlet/stores?cmd=retrieve&userid="+key;
		URL servlet = new URL(location);
		URLConnection servletConnection = servlet.openConnection();
		
		InputStream input;
		try {
			input = servletConnection.getInputStream();
		} catch (FileNotFoundException e) {
			return null;
		}
		
		String dataString = "";
		int data = input.read();
		while(data != -1)
		{
			dataString = dataString + (char)data;
			data = input.read();
		}
		return dataString;
	}
	
	/**
	 * retrieveFile - fetches data from the server and stores it in a file
	 * 
	 * If the file does not exist, it will be created.  If it already exists, it will be
	 * overwritten without prompting.  If no data is retrieved, no file will be created 
	 * or overwritten. 
	 * 
	 * @see edu.cmu.pact.miss.storage.StorageAccess#retrieveFile(java.lang.String, java.lang.String)
	 * @throws IOException If the URL fails to connect to the servlet 
	 * @param key the key associated with the desired data, eg username
	 * @param filename the name of the file to store the data in
	 * @return true if the data was retrieved and file was created, false otherwise
	 */
	@Override
	public boolean retrieveFile(String key, String filename) throws IOException
	{
		//String location = locationURL+"/SimStSVHS/stores?cmd=retrieve&userid="+key;
		String location = locationURL+"/Servlet/stores?cmd=retrieve&userid="+key;
		URL servlet = new URL(location);
		URLConnection servletConnection = servlet.openConnection();
		InputStream input;
		try {
			input = servletConnection.getInputStream();
		} catch (FileNotFoundException e) {
			return false;
		}
		
		String dataString = "";
		int data = input.read();
		while(data != -1)
		{
			dataString = dataString + (char)data;
			data = input.read();
		}
		

    	FileWriter outputter;
    	try {
			outputter = new FileWriter(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
        } catch (IOException e) {
			return false;
		}

        try {
            writeMessage(dataString, outputter);
        } catch (Exception ex) {
        	return false;
        }

        try {
			outputter.close();
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * retrieveFile - fetches data from the server and stores it in a file given by 
	 * pathName + fileName
	 * 
	 * @throws IOException If the URL fails to connect to the servlet 
	 * @param key the key associated with the desired data, eg username
	 * @param filename the name of the file to store the data in
	 * @param pathName the name of the directory where this file needs to be saved
	 * @return true if the data was retrieved and file was created, false otherwise
	 */
	public boolean retrieveFile(String key, String filename, String pathName) throws IOException
	{
		//String location = locationURL+"/SimStSVHS/stores?cmd=retrieve&userid="+key;
		String location = locationURL+"/Servlet/stores?cmd=retrieve&userid="+key;
		URL servlet = new URL(location);
		URLConnection servletConnection = servlet.openConnection();
		InputStream input;
		try {
			input = servletConnection.getInputStream();
		} catch (FileNotFoundException e) {
			return false;
		}
	
		String dataString = "";
		int data = input.read();
		while(data != -1)
		{
			dataString = dataString + (char)data;
			data = input.read();
		}
		
		
    	FileWriter outputter;
    	try {
			outputter = new FileWriter(new File(pathName + System.getProperty("file.separator") + filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
        } catch (IOException e) {
			return false;
		}

        try {
            writeMessage(dataString, outputter);
        } catch (Exception ex) {
        	return false;
        }

        try {
			outputter.close();
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
    
	/**
	 * retrieveObject - fetches data from the server as an object
	 * @see edu.cmu.pact.miss.storage.StorageAccess#retrieveObject(java.lang.String)
	 * @throws IOException If the URL fails to connect to the servlet
	 * @param key the key associated with the desired data, eg username-object
	 * @return the retrieved data as an object, or null if no data is associated with that key
	 */
	@Override
	public Object retrieveObject(String key) throws IOException {
		
		//String location = locationURL+"/SimStSVHS/stores?cmd=retrieveObj&file="+key;
		String location = locationURL+"/Servlet/stores?cmd=retrieveObj&file="+key;
		
		URL servlet = new URL(location);
		URLConnection servletConnection = servlet.openConnection();
		trace.err("*** url is " + location);
		
		servletConnection.setUseCaches(false);
		servletConnection.setDefaultUseCaches(false);
		ObjectInputStream inputFromServlet=null;

		trace.err("*** servletConnection.getInputStream is " + servletConnection.getInputStream());
		
		try{
		 inputFromServlet = new ObjectInputStream(servletConnection.getInputStream());
		
		} catch (IOException e) {
			trace.err("### Exception while creating ObjectInputStream : " + e);
		}
		
		if (inputFromServlet==null)
			return null;

		Object object = null;
		try {
			trace.err("*** trying to read from inputFromServlet.readObject()");
			object = inputFromServlet.readObject();
			trace.err("*** Ok I read it!!!!");
		} catch (ClassNotFoundException e) {
			trace.err("### opa...." + e);
			e.printStackTrace();
		}
		
		System.out.println("*** I read:  " + object);
		// Close the stream
		inputFromServlet.close();
		return object;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		StorageAccess client = new StorageClient(/*"http://mocha.pslc.cs.cmu.edu"*/);
		try {
			client.storeString("hey", "instructions.txt");
			System.out.println(client.retrieveFile("hey", "tmp4.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * storeFile - stores the data contained in a given file on the server
	 * 
	 * If data was previously stored with that key, it will be overwritten.
	 * 
	 * @see edu.cmu.pact.miss.storage.StorageAccess#storeFile(java.lang.String, java.lang.String)
	 * @throws IOException if the file cannot be read or the URL cannot be connected to
	 * @param key the key to associate the file with for later retrieval, eg username
	 * @param filename the name of the file to be read and sent
	 */
	@Override
	public void storeFile(String key, String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String fileData = readFile(reader);
		//fileData = encode(fileData);
		//String location = locationURL+"/Servlet/stores?cmd=store&userid="+key+"&text="+fileData;
		//String location = locationURL+"/SimStSVHS/stores?cmd=store&userid="+key;
		String location = locationURL+"/Servlet/stores?cmd=store&userid="+key;
		URL servlet = new URL(location);
		URLConnection servletConnection = servlet.openConnection();
		
		servletConnection.setDoInput(true);
		servletConnection.setDoOutput(true);
		servletConnection.setUseCaches(false);
		servletConnection.setDefaultUseCaches(false);
		servletConnection.setRequestProperty("Content-Type", "text/ascii");
		
		OutputStream output = servletConnection.getOutputStream();
		/*ObjectOutputStream output = new ObjectOutputStream(servletConnection.getOutputStream());
		
		output.writeObject(fileData);
		output.flush();
		output.close();*/
		
		PrintWriter dataWriter = new PrintWriter(output);
		dataWriter.write(fileData);
		dataWriter.flush();
		/*System.out.println("Printed: "+fileData);*/
		
        try {
			reader.close();
			dataWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		InputStream input = servletConnection.getInputStream();

		String dataString = "";
		int data = input.read();
		while(data != -1)
		{
			dataString = dataString + (char)data;
			data = input.read();
		}

		//System.out.print(dataString);
	}
	
	public void storeZIPFile(String key, String filename) throws IOException {
	
		//String location = locationURL+"/SimStSVHS/stores?cmd=storeZip&file="+key;
		String location = locationURL+"/Servlet/stores?cmd=storeZip&file="+key;
		URL servlet = new URL(location);
		URLConnection servletConnection = servlet.openConnection();
		
		servletConnection.setDoInput(true);
		servletConnection.setDoOutput(true);
		servletConnection.setUseCaches(false);
		servletConnection.setDefaultUseCaches(false);
		servletConnection.setRequestProperty("Content-Type", "application/zip");
		
		InputStream is = new FileInputStream(filename);
		ZipInputStream zis = new ZipInputStream(is);
		BufferedInputStream bis = new BufferedInputStream(zis);
		
		ZipEntry entry;
		ZipOutputStream zos = new ZipOutputStream(servletConnection.getOutputStream());
		
		while((entry = zis.getNextEntry()) != null) {

			zos.putNextEntry(entry);

			byte[] buffer = new byte[2*1024];
			int readCount;
			while( (readCount = bis.read(buffer)) > 0) {
				zos.write(buffer, 0, readCount);
			}
			zos.flush();
		}
		
		zos.close();
		InputStream input = servletConnection.getInputStream();

		String dataString = "";
		int data = input.read();
		while(data != -1)
		{
			dataString = dataString + (char)data;
			data = input.read();
		}

		//System.out.print(dataString);		
	}
	
	/**
	 * storeFile - stores the data contained in a given file on the server
	 * 
	 * If data was previously stored with that key, it will be overwritten.
	 * 
	 * @see edu.cmu.pact.miss.storage.StorageAccess#storeFile(java.lang.String, java.lang.String)
	 * @throws IOException if the file cannot be read or the URL cannot be connected to
	 * @param key the key to associate the file with for later retrieval, eg username
	 * @param filename the name of the file to be read and sent
	 */
	@Override
	public void storeObject(String key, Object object) throws IOException {
		
		//String location = locationURL+"/SimStSVHS/stores?cmd=storeObj&file="+key;
		String location = locationURL+"/Servlet/stores?cmd=storeObj&file="+key;
		URL servlet = new URL(location);
		URLConnection servletConnection = servlet.openConnection();
		
		servletConnection.setDoInput(true);
		servletConnection.setDoOutput(true);
		servletConnection.setUseCaches(false);
		servletConnection.setDefaultUseCaches(false);
		servletConnection.setRequestProperty("Content-Type", "application/octet-stream");
		
		//OutputStream output = servletConnection.getOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(servletConnection.getOutputStream());
		
		output.writeObject(object);
		output.flush();
		output.close();		
		
		InputStream input = servletConnection.getInputStream();

		String dataString = "";
		int data = input.read();
		while(data != -1)
		{
			dataString = dataString + (char)data;
			data = input.read();
		}

		input.close();
		//System.out.print(dataString);
	}
	
	
	
	/**
	 * storeString - stores the given data on the server
	 * 
	 * If data was previously stored with that key, it will be overwritten.
	 * 
	 * @see edu.cmu.pact.miss.storage.StorageAccess#storeFile(java.lang.String, java.lang.String)
	 * @throws IOException if the URL cannot be connected to
	 * @param key the key to associate the data with for later retrieval, eg username
	 * @param toStore the data to be sent
	 */
	@Override
	public void storeString(String key, String toStore) throws IOException {

		toStore = encode(toStore);
		//String location = locationURL+"/SimStSVHS/stores?cmd=store&userid="+key+"&text="+toStore;
		String location = locationURL+"/Servlet/stores?cmd=store&userid="+key+"&text="+toStore;
		URL servlet = new URL(location);
		URLConnection servletConnection = servlet.openConnection();
		
		servletConnection.setDoInput(true);
		servletConnection.setDoOutput(true);
		servletConnection.setUseCaches(false);
		servletConnection.setDefaultUseCaches(false);
		servletConnection.setRequestProperty("Content-Type", "text/ascii");
		
		OutputStream output = servletConnection.getOutputStream();
		
		PrintWriter dataWriter = new PrintWriter(output);
		dataWriter.write(toStore);
		dataWriter.flush();
		dataWriter.close();
        
		InputStream input = servletConnection.getInputStream();
		
		String dataString = "";
		int data = input.read();
		while(data != -1)
		{
			dataString = dataString + (char)data;
			data = input.read();
		}
		//System.out.print(dataString);

	}

	/**
	 * Encode a given string according to
	 * {@link java.net.URLEncoder#encode(java.lang.String, java.lang.String).
	 * This is a convenience so callers needn't deal with the
	 * {@link UnsupportedEncodingException}. 
	 * @param s the String to encode
	 * @return encoded value; returns empty string if s is null
	 * @throws Exception if parameter is not found or is 0-length
	 *         and defaultValue is null
	 */
	public static String encode(String s)
			throws IOException {
		try {
			if (s == null)
				return "";
			else
				return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
			throw new IOException("Unexpected error in URL encoder");
		}
	}
	
}
