/**------------------------------------------------------------------------------------
 $Author: blojasie $ 
 $Date: 2012-05-31 11:09:39 -0400 (Thu, 31 May 2012) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.4  2011/08/26 13:12:12  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.3  2011/06/11 14:12:17  vvelsen
 First version of start state editor that fully processes and displays the start state coming from an AS3 based tutor.

 Revision 1.2  2011/06/09 17:55:36  vvelsen
 Added the start state editor into the docking pane and changed the w3c parser to the jdom parser.

 Revision 1.1  2011/05/26 16:12:06  vvelsen
 Added first version of the start state editor. There's a test rig under the test directory.

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.cmu.hcii.ctat.CTATBase;

public class CTATFileManager extends CTATBase
{
	/**------------------------------------------------------------------------------------
	 *
	 */
	public CTATFileManager () 
	{
		setClassName ("CTATFileManager");
		debug ("CTATFileManager ()");
	} 
	/**------------------------------------------------------------------------------------
	 *
	 */	
	public boolean doesFileExist (String aFileURI)
	{
		debug ("getContents ("+aFileURI+")");
		
		File file=new File (aFileURI);
		
	    boolean exists = file.exists();
	    if (!exists) 
	    {
	     return (false);   
	    }
	    
	    return (true);
	}  
	/**------------------------------------------------------------------------------------
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
	/**------------------------------------------------------------------------------------
	 *
	 */	
	public String loadContents (String aFileURI)
	{    
		debug ("loadContents ("+aFileURI+")");
		
		File aFile=new File(aFileURI);

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
			ex.printStackTrace();
			return (null);
		}
    
		return (contents.toString());
	}
	/**------------------------------------------------------------------------------------
	 *
	 */ 	
	public Element loadContentsXML (String aFileURI)
	{
		debug ("loadContentsXML ("+aFileURI+")");
		
        SAXBuilder builder = new SAXBuilder();
        Document doc=null;
		try {
			doc = builder.build(aFileURI);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return (doc.getRootElement());
	}
	/**------------------------------------------------------------------------------------
	 *
	 */  
	public boolean saveContents (String aFileURI,String aContents) 
	{
		debug ("saveContents ("+aFileURI+")");
		
		File aFile = new File (aFileURI);
		Writer output =null; 
		
		/*
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
		*/

		//use buffering
		try 
		{			
			output=new BufferedWriter (new FileWriter(aFile));
		}
		catch (IOException e) 
		{
			debug ("Exception: IOException while opening output file");
			return (false);			
		}			
		
		try 
		{
			// FileWriter always assumes default encoding is OK!
			output.write (aContents);			
		}
		catch (IOException e) 
		{
			debug ("Exception: IOException while writing contents to disk");
			
			try
			{
				output.close();
			}
			catch (IOException closeException)
			{
				debug ("Exception: closeException while closing file writer");
				return (false);
			}
			
			return (false);
		}
		
		try
		{
			output.flush();
		}
		catch (IOException e) 
		{
			debug ("Exception: IOException while flushing contents to disk");
		}
		
		try
		{
			output.close();
		}
		catch (IOException e) 
		{
			debug ("Exception: IOException closing output file");
		}
		
		return (true);
	}
	//-------------------------------------------------------------------------------------	
} 
