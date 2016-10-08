/**
 $Author: vvelsen $ 
 $Date: 2012-12-05 11:07:56 -0500 (Wed, 05 Dec 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATHTTPUploadHandler.java,v 1.3 2012/10/11 15:20:08 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATHTTPUploadHandler.java,v $
 Revision 1.3  2012/10/11 15:20:08  akilbo
 Made them public so that they may be imported to the classes in TutorMonitorServer

 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.3  2012/10/04 15:31:54  akilbo
 Updated to match changes in the base class

 Revision 1.2  2012/04/30 18:21:39  vvelsen
 Added some support code around the database management system. This will allow users and developers to choose date ranges in the database

 Revision 1.1  2012/04/11 13:20:16  vvelsen
 Some refactoring to allow other servers to derive from CTATDeamon. We should be able now to handle file uploads with crc generation coming from Flex or Flash applications

 $RCSfile: CTATHTTPUploadHandler.java,v $ 
 $Revision: 18579 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATHTTPUploadHandler.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:

*/
package edu.cmu.hcii.ctat.monitor;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//import javax.imageio.ImageIO;

import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.hcii.ctat.CTATDesktopFileManager;
import edu.cmu.hcii.ctat.CTATHTTPExchange;
import edu.cmu.hcii.ctat.CTATHTTPHandlerInterface;
import edu.cmu.hcii.ctat.CTATLink;
//import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATSSELink;

/** 
 * @author vvelsen
 *
 */
public class CTATHTTPUploadHandler extends CTATBase implements CTATHTTPHandlerInterface
{
	private String uploadPath=".";
	
	/**
	 * 
	 */	
	public String getUploadPath() 
	{
		return uploadPath;
	}
	/**
	 * 
	 */
	public void setUploadPath(String uploadPath) 
	{
		this.uploadPath = uploadPath;
	}	
	/**
	 * @return 
	 * 
	 */
	@Override
	public boolean handle(CTATHTTPExchange arg0) 
	{
		debug ("handle ()");
		
		String requestMethod = arg0.getRequestMethod();
		
		debug("Request method: " + requestMethod + ", Request URI: " + arg0.getRequestURI());
		
		String fileURI=arg0.getRequestURI().toString();
		
		//>--------------------------------------------------------------
		
		if (requestMethod.equalsIgnoreCase ("post"))
		{
	    	debug ("Processing POST ...");
	    	
			InputStream din=arg0.getRequestBody();
			
			if (din==null)
			{
				debug ("Error POST input stream is null");
				return false;
			}
			
			try 
			{
				if(din.available()>0)
				{
					debug ("We've got data ["+din.available()+"] available, reading ...");	
				}
				else
				{
					debug ("Error: no data available on input stream, aborting ...");
					return false;	    	
				}
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			try
			{
				File f=new File(getUploadPath()+"/ctat.jar");
				
				OutputStream out=new FileOutputStream(f);
				byte buf[]=new byte[1024];
				int len;
				
				while((len=din.read(buf))>0)
					out.write(buf,0,len);
				
				out.close();
				din.close();

			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();				
			}			
			
			debug ("Creating MD5 checksum from: " + getUploadPath()+"/ctat.jar");
			
			CTATDesktopFileManager fManager=new CTATDesktopFileManager ();
			
			if (fManager.doesFileExist(getUploadPath()+"/ctat.jar")==false)
			{
				CTATBase.debug ("CTATDesktopFileManager","Error: input file does not exist: " + getUploadPath()+"/ctat.jar");
				return false;
			}
			
			fManager.saveFileChecksum (getUploadPath()+"/ctat.jar",getUploadPath()+"/ctat.jar.crc",false);						
		}		
		
		//>---------------------------------------------------------------
		
		if (requestMethod.equalsIgnoreCase ("get"))
		{
	    	debug ("Processing GET request ...");
	    		    	    	
		    /*Crossdomain.xml Requested.
		     *For successful exchange:
		     *->Crossdomain.xml might need to be up-to-date with flash policies
		     *->Valid content-type must be specified in the responseheader:
		     */
				    		    	
		    if (fileURI.equalsIgnoreCase ("/crossdomain.xml"))
		    {
		    	debug ("Processing crossdomain request ...");
		    			    	
		    	debug ("Writing back the crossdomain policy...");
		    	try
		    	{
		    		arg0.addResponseHeader("Content-Type", "application/xml");
		    		arg0.sendResponseHeaders(200,CTATLink.crossDomainPolicy.getBytes().length);
		    		//arg0.getResponseBody().write(CTATLink.crossDomainPolicy.getBytes());
		    		arg0.getOutputStream().write(CTATLink.crossDomainPolicy.getBytes());
		    		debug ("Wrote back Crossdomain.xml..");
		    		arg0.close();
		    	}
		    	catch(Exception e)
		    	{
		    		debug ("Exception in trying to write back crossdomain.xml");		    	
		    	}
		    }
		}    		
		return true;
		//>--------------------------------------------------------------
	}		
}
