package edu.cmu.pact.miss;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.cmu.pact.Utilities.trace;

public class WebStartFileDownloader {

	public static final String SimStAlgebraPackage = "SimStAlgebraV8";
	public static final  String output_folder = System.getProperty("user.home");
	public static final  String separator = System.getProperty("file.separator");
	public static final String SimStWebStartDir = output_folder + separator + "Public" + separator + SimStAlgebraPackage + separator;

	public String findFile(String name) {
		
		if(checkFileExistsLocally(name))
			return name;
		else {
	    	ClassLoader cl = this.getClass().getClassLoader();
	    	InputStream is = cl.getResourceAsStream(SimStAlgebraPackage + "/" + name);
	    	if(is != null) { 
		        FileOutputStream fos = null;
		        String file = null;
		       	try {
		       			file = output_folder + separator + "Public" + separator +  SimStAlgebraPackage + separator + name;
		       			File parentDir = new File(output_folder + separator + "Public" + separator + SimStAlgebraPackage);
		       			if(!parentDir.exists()) {
		       				parentDir.mkdirs();
		       			}
		        		fos = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
						e.printStackTrace();
				}
			    byte[] buffer = new byte[4096];
			    try {
			       	int bytesRead = is.read(buffer);
			       	while(bytesRead != -1) {
			       		fos.write(buffer, 0, bytesRead);
			       		bytesRead = is.read(buffer);
			       	}
			       	is.close();
			       	fos.close();
			    } catch (IOException e) {
			       	e.printStackTrace();
			    }
			    
			    return file;
			} else {
				if(trace.getDebugCode("rr"))
					trace.err("File " + name + " was not found!!!!!");
				return null;
			}
		}
	}
	
	public String findTabbedTestPrefsFile(String name) {
		
		if(checkFileExistsLocally(name))
			return name;
		else {
	    	ClassLoader cl = this.getClass().getClassLoader();
	    	InputStream is = cl.getResourceAsStream("TabbedPreTest" + "/" + name);
	    	if(is != null) { 
		        FileOutputStream fos = null;
		        String file = null;
		       	try {
		       			file = output_folder + separator + "Public" + separator +  SimStAlgebraPackage + separator + name;
		       			File parentDir = new File(output_folder + separator + "Public" + separator + SimStAlgebraPackage);
		       			if(!parentDir.exists()) {
		       				parentDir.mkdirs();
		       			}
		        		fos = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
						e.printStackTrace();
				}
			    byte[] buffer = new byte[4096];
			    try {
			       	int bytesRead = is.read(buffer);
			       	while(bytesRead != -1) {
			       		fos.write(buffer, 0, bytesRead);
			       		bytesRead = is.read(buffer);
			       	}
			       	is.close();
			       	fos.close();
			    } catch (IOException e) {
			       	e.printStackTrace();
			    }
			    
			    return file;
			} else {
				if(trace.getDebugCode("rr"))
					trace.err("File " + name + " was not found!!!!!");
				return null;
			}
		}
	}

	public boolean checkFileExistsLocally(String fileName) {
        
		File f = null;
        f = new File(new File(".").getAbsolutePath(), fileName);
        if(f != null && f.isAbsolute() && f.exists()) 
            return true;
        else
        	return false;
	}
}
