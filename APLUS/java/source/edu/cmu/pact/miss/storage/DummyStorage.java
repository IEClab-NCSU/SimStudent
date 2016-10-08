package edu.cmu.pact.miss.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

/*
 * DummyStorage - creates a mock StorageAccess with a hashtable
 */
public class DummyStorage extends StorageAccess {

	Hashtable<String,String> storage = new Hashtable<String,String>(); 
	Hashtable<String,Object> storageObj = new Hashtable<String,Object>(); 
	
	@Override
	public boolean retrieveFile(String key, String filename) throws IOException {

		if(!storage.containsKey(key))
			return false;
		
		String dataString = storage.get(key);
		
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

	@Override
	public String retrieveString(String key) throws IOException {

		if(!storage.containsKey(key))
			return null;
		
		return storage.get(key);
		
	}

	@Override
	public void storeFile(String key, String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String fileData = readFile(reader);
		storage.put(key, fileData);
	}

	@Override
	public void storeString(String key, String toStore) throws IOException {
		storage.put(key, toStore);

	}

	@Override
	public Object retrieveObject(String key) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeObject(String key, Object object) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void storeZIPFile(String key, String filename) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
