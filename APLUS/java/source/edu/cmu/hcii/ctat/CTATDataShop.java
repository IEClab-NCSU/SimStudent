/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATDataShop.java,v 1.5 2012/08/24 20:09:35 kjeffries Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATDataShop.java,v $
 Revision 1.5  2012/08/24 20:09:35  kjeffries
 *** empty log message ***

 Revision 1.4  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.3  2012/04/10 15:14:59  vvelsen
 Refined a bunch of classes that take care of file management and file downloading. We can now generate, save and compare file CRCs so that we can verify downloads, etc

 Revision 1.2  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.5  2011/07/18 14:39:01  kjeffries
 Fixed null-pointer bug involving Date lastUpload

 Revision 1.4  2011/07/01 16:59:00  kjeffries
 Now returns false on failure

 Revision 1.3  2011/05/27 20:43:54  kjeffries
 The local log is now cleared when the data is uploaded to DataShop. Also added facility to remember when the last upload was performed.

 Revision 1.2  2011/02/16 21:31:05  vvelsen
 Small fix to the datashop upload class to send one log message at a time instead of the whole file all at once.

 Revision 1.1  2011/02/16 16:42:10  vvelsen
 Added more refinement to the update class that will work either in the background or at a time when nobody is doing tutoring.

 $RCSfile: CTATDataShop.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATDataShop.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
*/

package edu.cmu.hcii.ctat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

public class CTATDataShop extends CTATBase
{
	private static Date lastUpload = null;
	
	/**
	 *
	 */
    public CTATDataShop () 
    {
    	setClassName ("CTATDataShop");
    	debug ("CTATDataShop ()");
    }
	/**
	 *
	 */
    public boolean migrateData () 
    {
    	debug ("migratedata ()");
    	
    	lastUpload = new Date(); // update the time of last upload
    
    	String datashopData="";
    	
    	// First read the log file ...
    	
    	CTATDesktopFileManager fileManager=new CTATDesktopFileManager();
    	
    	if (fileManager.doesFileExist(CTATLink.datashopFile)==false)
    	{
    		debug ("Log file does not exist, skipping task ...");
    		return (false);
    	}
    	
    	datashopData=fileManager.getContents (CTATLink.datashopFile);
    	
        String split[];

        split=datashopData.split("\\n");

    	// Then blast it to the log server ...
        
        for (int i=0;i<split.length;i++)
        {    	
        	debug ("Sending: " + split [i]);
        	
        	CTATURLFetch dataShopStream=new CTATURLFetch ();
        	try 
        	{
        		dataShopStream.sendData(CTATLink.datashopURL,split [i]);
        	} 
        	catch (MalformedURLException e) 
        	{
        		e.printStackTrace();
        		return false;
        	} 
        	catch (IOException e) 
        	{
        		e.printStackTrace();
        		return false;
        	}
        }
        
        // For now, just clear the local log file after it has been sent.
        // In the future a backlog may be desirable.
        fileManager.setContents(CTATLink.datashopFile, "");
    	
    	return (true);
    }
    /**
	 *
	 */
    public static Date getLastUploadTime()
    {
    	if(lastUpload != null)
    	{
    		return (Date) lastUpload.clone();
    	}
    	else
    	{
    		return null;
    	}
    }    
}
