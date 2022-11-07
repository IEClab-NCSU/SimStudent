package edu.cmu.pact.miss.storage;

import edu.cmu.pact.Utilities.trace;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.*;

public class FileUnZipper {
	
	public static void main (String argv[]) {
      
		try {
			
			final int BUFFER = 2048;
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream("//Users//rohanraizada//Desktop//myfigs.zip");
			CheckedInputStream checksum = new CheckedInputStream(fis, new Adler32());
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(checksum));
			ZipEntry entry;
         
			while((entry = zis.getNextEntry()) != null) {

				trace.out("Extracting: " +entry);
	            int count;
	            byte data[] = new byte[BUFFER];
      
	            // write the files to the disk
	            File file = new File("//Users//rohanraizada//Desktop//myfigs");
	            if(!file.exists())
	            	file.mkdirs();
	            
	            FileOutputStream fos = new FileOutputStream("//Users//rohanraizada//Desktop//myfigs//" + entry.getName());
	            dest = new BufferedOutputStream(fos,BUFFER);
	            while ((count = zis.read(data, 0, BUFFER)) != -1) {
	               dest.write(data, 0, count);	            
	            }
	            
	            dest.flush();
	            dest.close();
			}
			
			zis.close();         
			trace.out("Checksum: "+checksum.getChecksum().getValue());
			
      } catch(Exception e) {
         e.printStackTrace();
      }      
   }
}
