package edu.cmu.hcii.ctat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import edu.cmu.hcii.ctat.wizard.CTATWizardBase;

/** 
 * @author vvelsen
 *
 * This application generates Moodle compatible SCORM packages (zip files)
 * according to this specification:
 * 
 * http://www.imsglobal.org/content/packaging/cpv1p1p4/imscp_infov1p1p4.html
 */
public class CTATMoodleTools extends CTATWizardBase
{	
	protected class CTATFileEntry
	{
		public String basePath="";
		public String filePath="";
		public String fullPath="";
	}
	
	protected String flashTutorBasePath="";
    protected ArrayList<CTATFileEntry> fileList=null;
    	
	/**
	 * 
	 */
    public CTATMoodleTools () 
    {
    	setClassName ("CTATMoodleTools");
    	debug ("CTATMoodleTools ()"); 
    	
    }
    /**
     * Zip it
     * @param zipFile output ZIP file location
     */
    public void zipIt(String zipFile)
    {
    	debug ("zipIt ()");
 
    	byte[] buffer = new byte[1024];
 
    	try
    	{ 
    		FileOutputStream fos = new FileOutputStream(zipFile);
    		ZipOutputStream zos = new ZipOutputStream(fos);
 
    		debug ("Output to Zip : " + zipFile);
 
    		for(CTATFileEntry file : this.fileList)
    		{ 
    			debug ("File Added : " + file.filePath);
    			
    			ZipEntry ze= new ZipEntry (file.filePath);
    			zos.putNextEntry(ze);
 
    			FileInputStream in=null;
    			
    			if (file.fullPath.isEmpty()==false)
    				in = new FileInputStream(file.fullPath);
    			else
    				in = new FileInputStream(file.basePath+"/"+file.filePath);
 
    			int len;
    			
    			while ((len = in.read(buffer)) > 0) 
    			{
    				zos.write(buffer, 0, len);
    			}
 
    			in.close();
    		}
 
    		zos.closeEntry();
    		//	remember close it
    		zos.close();
 
    		debug ("Done");
    	}
    	catch(IOException ex)
    	{
    		ex.printStackTrace();   
    	}
    }
    /**
     * Traverse a directory and get all files,
     * and add the file into fileList  
     * @param node file or directory
     */
    public void generateFileList(File node)
    {
    	debug ("generateFileList ()");
    	
    	if(node.isFile())
    	{
    		String totalPath=node.getAbsolutePath();
    		
    		int index=totalPath.indexOf(flashTutorBasePath);
    		
    		if (index!=-1)
    		{    		
    			CTATFileEntry entry=new CTATFileEntry ();
    			entry.basePath=flashTutorBasePath;
    			entry.filePath=totalPath.substring(flashTutorBasePath.length()+1);
    		
    			debug ("Adding " + entry.basePath + " at ("+index+"): " + entry.filePath);
    		
    			fileList.add(entry);
    		}
    		else
    			debug ("Unable to find: " + flashTutorBasePath +" in: " + totalPath);
    	}
 
    	if(node.isDirectory())
    	{
    		String[] subNote = node.list();
    		
    		for(String filename : subNote)
    		{
    			generateFileList(new File(node, filename));
    		}
    	} 
    }
    /**
     * If you are using JDK 7 use the new Files.createTempDirectory class to 
     * create the temporary directory. Before JDK 7 this should do it.
     * @return
     * @throws IOException
     */
    public static File createTempDirectory() throws IOException
    {
        final File temp;

        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if(!(temp.delete()))
        {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if(!(temp.mkdir()))
        {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        return (temp);
    }
}
