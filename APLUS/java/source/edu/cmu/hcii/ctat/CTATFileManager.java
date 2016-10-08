/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATFileManager.java,v 1.6 2012/10/03 17:50:26 sewall Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATFileManager.java,v $
 Revision 1.6  2012/10/03 17:50:26  sewall
 First working version with CTATAppletFileManager.

 Revision 1.5  2012/06/12 19:43:48  kjeffries
 made class abstract (use CTATDestkopFileManager, etc.) and added method configureCTATLink

 Revision 1.4  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.3  2012/02/29 17:44:53  vvelsen
 Refined our file classes and local tutorshop to behave better when managed by a loader class. Added some nice utility functions in the file manager

 Revision 1.2  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.2  2011/06/03 20:34:21  kjeffries
 Added the skeleton for methods getContentsXMLEncrypted and getContentsEncrypted.

 Revision 1.1  2011/04/28 17:11:56  vvelsen
 Finally connected all the dots and made sure that we can access platform specific drivers (java classes) through a central registry. Please see CTATLink for more information.

 Revision 1.4  2011/03/25 20:38:48  vvelsen
 Added much more capabilties to the USB TutorShop. We can now start problems defined in problem_set.xml files. Much more of the problem sequencing code has been finished but more needs to happen there. Some of the internals have been bolstered.

 Revision 1.3  2011/02/16 16:42:10  vvelsen
 Added more refinement to the update class that will work either in the background or at a time when nobody is doing tutoring.

 Revision 1.2  2011/02/09 13:11:38  vvelsen
 Added proper logging handling to local disk (USB drive) and added encryption code.

 Revision 1.1  2011/02/07 19:50:15  vvelsen
 Added a number of files that can manage local files as well as generate html from parameters and templates.

 $RCSfile: CTATFileManager.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATFileManager.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
*/

package edu.cmu.hcii.ctat;

import java.io.InputStream;

import org.w3c.dom.Element;

public abstract class CTATFileManager extends CTATBase
{
	/**
	 *
	 */
	public CTATFileManager () 
	{
		setClassName ("CTATFileManager");
		debug ("CTATFileManager ()");
	} 
	/**
	 *
	 */	
	public boolean doesFileExist (String aFileURI)
	{	    
		// Implement in platform specific code
	    return (false);
	}  
	/**
	 *
	 */
	public boolean createDirectory (String aDirURI)
	{
		debug ("createDirectory ("+aDirURI+")");
		
		// Implement in platform specific code		
		
		return (false);
	}
	/**
	 *
	 */	
	public String getContents (String aFileURI)
	{    
		debug ("getContents ("+aFileURI+")");
		
		// Implement in platform specific code
    
		return (null);
	}
	/**
	 *
	 */ 	
	public Element getContentsXML (String aFileURI)
	{
		debug ("getContentsXML ("+aFileURI+")");
		
		// Implement in platform specific code
		
		return (null);
	}
	/**
	 *
	 */
	public Element getContentsXMLEncrypted (String aFileURI)
	{
		debug ("getContentsXMLEncrypted ("+aFileURI+")");
		
		// Implement in platform specific code
		
		return (null);
	}
	/**
	 *
	 */
	public boolean setContents (String aFileURI,String aContents) 
	{
		debug ("setContents ("+aFileURI+")");
		
		// Implement in platform specific code
		
		return (false);
	}
	/**
	 *
	 */  
	public boolean setContentsEncrypted (String aFileURI,String aContents) 
	{
		debug ("setContents ("+aFileURI+")");
		
		// Implement in platform specific code
		
		return (false);
	}
	/**
	 *
	 */
	public String getContentsEncrypted(String aFileURI)
	{
		debug ("getContentsEncrypted ("+aFileURI+")");
		
		// Implement in platform specific code
		
		return (null);
	}	
	/**
	 * 
	 */
	public boolean configureCTATLink()
	{
		debug ("configureCTATLink ()");
		
		// Implement in platform specific code
		
		return false;
	}
	/**
	 * @param aFileURI file to open
	 * @return input stream to read file
	 */
	public InputStream getInputStream(String aFileURI)
	{
		debug ("getInputStream ("+aFileURI+")");
		
		// Implement in platform specific code
		
		return null;		
	}
} 
