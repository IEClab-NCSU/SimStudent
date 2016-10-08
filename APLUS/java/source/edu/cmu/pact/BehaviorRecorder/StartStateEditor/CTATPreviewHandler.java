/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-12-05 11:07:56 -0500 (Wed, 05 Dec 2012) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.4  2012/02/07 23:05:46  sewall
 Remove use of com.sun.image.codec.jpeg.* in anticipation of Java 1.7, which doesn't have it.

 Revision 1.3  2011/09/02 17:01:14  vvelsen
 Fixed a number of import bugs and re-worked part of the layout of the start state editor. It will now always show the preview window no matter what edit pane you're on and it should be a scaleable window now.

 Revision 1.2  2011/09/02 16:16:29  vvelsen
 Finalized the code that sends a preview of the Flash tutor to the Start State Editor.

 Revision 1.1  2011/08/26 13:12:13  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 $RCSfile$ 
 $Revision: 18579 $ 
 $Source$ 
 $State$ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

//import java.io.IOException;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.hcii.ctat.CTATHTTPExchange;
import edu.cmu.hcii.ctat.CTATHTTPHandlerInterface;
import edu.cmu.hcii.ctat.CTATLink;

class CTATPreviewHandler extends CTATBase implements CTATHTTPHandlerInterface
{		
	private CTATTutorPreview viewer=null;
		
	/**
	 *
	 */	
	public CTATPreviewHandler (CTATTutorPreview aViewer)
	{
    	setClassName ("CTATPreviewHandler");
    	debug ("CTATPreviewHandler ()");
    	
    	setViewer(aViewer);
	}	
	/**
	 * @throws IOException 
	 *
	 */		
	private Image getImageData(CTATHTTPExchange arg0) throws IOException
	{
		debug ("getImageData ()");
				
		InputStream din=arg0.getRequestBody();
		
		if (din==null)
		{
			debug ("Error POST input stream is null");
			return (null);
		}
		
		if(din.available()>0)
		{
			debug ("We've got data ["+din.available()+"] available, reading ...");	
		}
		else
		{
			debug ("Error: no data available on input stream, aborting ...");
			return (null);
		}
				
		ByteArrayOutputStream baos = new ByteArrayOutputStream() {
			public byte[] toByteArray() {
				return buf;          // allow access without making a copy of the buffer
			}
		};
		for (int b = -1; 0 <= (b = din.read()); )  // read entire image before rendering 
			baos.write(b);
		
		byte[] imageBytes = baos.toByteArray();
		if (imageBytes.length > 0)
			debug ("Read "+imageBytes.length+" image bytes from stream...");
		else {
			debug ("Read no image bytes from stream; returning null...");
			return null;
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
		BufferedImage image = ImageIO.read(bais);
		if (image==null)
			debug ("Conversion of image data from input stream failed; returning null");
		else
			debug ("We appear to have a valid image; returning it");

		return (image);
	}

	/**
	 *
	 */	
	public boolean handle (CTATHTTPExchange arg0)
	{
		debug ("handle ()");
		
    	CTATSSELink.preview=null; // Let's be careful and at least set it to NULL
		
		String requestMethod = arg0.getRequestMethod();
								
		debug("Request method: " + requestMethod + ", Request URI: " + arg0.getRequestURI());
		
		String fileURI=arg0.getRequestURI().toString();
		
		//>--------------------------------------------------------------
		
		if (requestMethod.equalsIgnoreCase ("post"))
		{
	    	debug ("Processing POST ...");
	    	
	    	try 
	    	{
				CTATSSELink.preview=getImageData (arg0);
			} 
	    	catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	if (viewer!=null)
	    		viewer.updatePreview ();
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
				    return true;
		    	}
		    	catch(Exception e)
		    	{
		    		debug ("Exception in trying to write back crossdomain.xml");		    	
		    	}
		    }
		}
		return false;
	}
	/**
	 *
	 */
	public CTATTutorPreview getViewer() 
	{
		return viewer;
	}
	/**
	 *
	 */
	public void setViewer(CTATTutorPreview viewer) 
	{
		this.viewer = viewer;
	}
}
