package edu.cmu.pact.miss.storage;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * StorageClient - The client end of some remote storage mechanism for text data
 * 
 * @author Victoria Keiser
 * @version 2010.1015
 */
public abstract class StorageAccess {

	/**
	 * retrieve String - fetches data as a string
	 * @throws IOException If the client fails to make the remote connection
	 * @param key the key associated with the desired data, eg username
	 * @return the retrieved data as a String, or null if no data is associated with that key
	 */
	public abstract String retrieveString(String key) throws IOException;
	
	/**
	 * retrieveFile - fetches data and stores it in a file
	 * @throws IOException If the client fails to make the remote connection or read the file
	 * @param key the key associated with the desired data, eg username
	 * @param filename the name of the file to store the data in
	 * @return true if the data was retrieved and file was created, false otherwise
	 */
	public abstract boolean retrieveFile(String key, String filename) throws IOException;
	

	/**
	 * retrieveObject - fetches a stored data object
	 * @throws IOException If the client fails to make the remote connection
	 * @param key the key associated with the desired data, eg username-object
	 * @return the object retrieved from the server, null if none found
	 */
	public abstract Object retrieveObject(String key) throws IOException;
	
	/**
	 * storeString - stores the provided data
	 * @throws IOException if the remote connection fails
	 * @param key the key to associate the data with for later retrieval, eg username
	 * @param toStore the data to be sent
	 */
	public abstract void storeString(String key, String toStore) throws IOException;
	
	/**
	 * storeFile - stores the data provided by a specified file
	 * @throws IOException if the file cannot be read or the remote connection fails
	 * @param key the key to associate the file with for later retrieval, eg username
	 * @param filename the name of the file to be read and sent
	 */
	public abstract void storeFile(String key, String filename) throws IOException;

	/**
	 * storeObject - stores the data provided by a specified file
	 * @throws IOException if the file cannot be read or the remote connection fails
	 * @param key the key to associate the file with for later retrieval, eg username-object
	 * @param object the object to be sent
	 */
	public abstract void storeObject(String key, Object object) throws IOException;
	
	/**
	 * storeZipFile - stores the zip file specified by the filename
	 * @param key the key to associate the file with for later retrieval
	 * @param filename the name of the zip file to be read and sent
	 * @throws IOException
	 */
	public abstract void storeZIPFile(String key, String filename) throws IOException;
	
	/**
	 * writeMessage - writes a message to a file and flushes it
	 * 
	 * If no message is given, nothing will be written
	 * 
	 * @throws IOException if the file cannot be written
	 * @param msg The message to write to file
	 * @param writer A FileWriter associated with the open file to write to
	 */
    protected void writeMessage(String msg, FileWriter writer)	throws IOException
    {
		if(msg == null)
			return;
		writer.write(msg);
		writer.flush();
    }
    
	/**
	 * readMessage - reads a message from a file
	 * 
	 * @throws IOException if the file cannot be read
	 * @param reader A BufferedReader associated with the open file to read from
	 * @return the read message
	 */
    protected String readFile(BufferedReader reader)	throws IOException
	{
		String msg = "";
		String line = reader.readLine();
		while(line != null)
		{
			msg = msg + line + "\n";
			line = reader.readLine();
		}
		return msg;
	}

	
}
