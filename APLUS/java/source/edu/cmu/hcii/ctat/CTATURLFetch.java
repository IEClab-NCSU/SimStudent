/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-09-23 12:26:26 -0400 (Mon, 23 Sep 2013) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATURLFetch.java,v 1.14 2012/09/18 15:21:55 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATURLFetch.java,v $
 Revision 1.14  2012/09/18 15:21:55  vvelsen
 We should have a complete and working asset download manager that can be run as part of the config panel (or command line)

 Revision 1.13  2012/09/14 13:05:48  vvelsen
 Started migrating the curriculum.xml to Octav's new format

 Revision 1.12  2012/08/23 21:06:08  kjeffries
 clean-ups

 Revision 1.11  2012/08/17 17:50:32  alvaro
 merging versions

 Revision 1.9  2012/06/12 19:24:06  kjeffries
 added code to check for internet connectivity

 Revision 1.8  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.7  2012/05/04 14:25:32  kjeffries
 fixed integer-division problem when updating progress bar in getHTTPBinaryFile

 Revision 1.6  2012/04/16 16:37:35  vvelsen
 Added a SwingWorker class that can be accessed through CTATLink. It's pretty much the only way in which random code can access a progress bar and update it the way Swing wants you to do it

 Revision 1.5  2012/04/13 19:44:38  vvelsen
 Started adding some proper threading and visualization tools for our download manager

 Revision 1.4  2012/04/10 15:14:59  vvelsen
 Refined a bunch of classes that take care of file management and file downloading. We can now generate, save and compare file CRCs so that we can verify downloads, etc

 Revision 1.3  2012/03/16 15:15:28  vvelsen
 Small fixes here and there to the monitor, link and other support classes. Mostly reformatting work.

 Revision 1.2  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.3  2011/02/16 16:42:10  vvelsen
 Added more refinement to the update class that will work either in the background or at a time when nobody is doing tutoring.

 Revision 1.2  2011/02/15 20:38:35  vvelsen
 Fixed a bug in the url fetch class that would throw errors left and right because it wasn't using the proper java classes. Did some cleaning on debugging as well.

 Revision 1.1  2011/02/15 15:20:42  vvelsen
 Added proper datashop logging to disk through http in the built-in web server. Added encryption code for logfiles, text based streams, etc. Added a first update scheduler that can be used to both retrieve new versions of the application as well as new content and student info.

 $RCSfile: CTATURLFetch.java,v $ 
 $Revision: 19526 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATURLFetch.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.hcii.ctat;

import java.awt.Component;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import java.io.OutputStreamWriter;
//import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
//import java.net.URLEncoder;

import javax.swing.JProgressBar;

import edu.cmu.hcii.ctat.CTATContentCache.UI;
import edu.cmu.pact.Utilities.trace;

public class CTATURLFetch extends CTATBase implements PropertyChangeListener 
{
	private JProgressBar updater=null;	
	//private InputStream binaryIn=null;
	private byte[] binaryData=null;
	//private String binaryLocation=null;
	
	private String internalURLCache="";
		
	/**
	 *
	 */
	public CTATURLFetch ()
	{
    	setClassName ("CTATURLFetch");
    	debug ("CTATURLFetch ()");		
	}
	/**
	 * 
	 */
	public String getObtainedURL ()
	{
		return (internalURLCache);
	}
	/**
	 * 
	 */
	public static String prepURL (String address)
	{
		CTATLink.debug("CTATURLFetch","(static) prepURL (String address)");
		
		String result=null;
		
		if (address.indexOf("https")==-1)
		{
			result="https://"+address;
		}
		else
			result=address;
		
		/*
		try 
		{
			//result = URLEncoder.encode(address, "ISO-8859-1");
			result = URLEncoder.encode(address, "UTF-8");			
		} 
		catch (UnsupportedEncodingException e) 
		{	
			//e.printStackTrace();
			CTATLink.debug("CTATURLFetch","Error preparing URL: UnsupportedEncodingException");
		}
		
		CTATLink.debug("CTATURLFetch","Encoded: " + result);
		*/
		
		return (result);
	}
	/**
	 *
	 */
	public String fetchURL (String address)  throws MalformedURLException, IOException 
	{
		debug ("fetchURL ("+address+")");
   	
		internalURLCache=prepURL (address);
   	
		debug ("Fetching prepped url: " + internalURLCache);
   	
		URL url = new URL(internalURLCache);
   	    	
		StringBuffer total=new StringBuffer ();
       
		URLConnection conn=url.openConnection();
       
		conn.setDoInput(true);
       
		try 
		{
			conn.connect();
		}
		catch (Exception e)
		{
			CTATLink.lastError=("Cannot obtain: " + address + " : " + e.getMessage());
			debug (CTATLink.lastError);
			return ("");
		}
       
		int blocks=0;
		
		try 
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String inputLine;

			while ((inputLine = in.readLine()) != null)
			{
				trace.out(inputLine);
				total.append(inputLine);
				
				blocks++;
			}
       	
			in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return ("");
		}      
		
		debug ("Received ("+blocks+" blocks): " + total.toString());
               
		return (total.toString());
	}	
	/**
	 *
	 */
	public String fetchURL (String aServer,String aPath)  throws MalformedURLException, IOException 
	{
   		debug ("fetchURL (http , "+aServer+" , " + aPath + ")");
   	
   		//String prepped=prepURL (aPath);
   	
   		internalURLCache="http://"+aServer+aPath;
   		
   		debug ("Fetching prepped url: " + internalURLCache);
   	
   		URL url = new URL(internalURLCache);
       
   		StringBuffer total=new StringBuffer ();
       
   		URLConnection conn=url.openConnection();
       
   		conn.setDoInput(true);
       
   		try 
   		{
   			conn.connect();
   		}
   		catch (Exception e)
   		{
       		debug ("cannot obtain: " + internalURLCache);
       	
       		return ("");
   		}
       
   		try 
   		{
   			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

   			String inputLine;

   			while ((inputLine = in.readLine()) != null)
   			{
   				//trace.out(inputLine);
   				total.append(inputLine);
   			}
       	
   			in.close();
   		}
   		catch(Exception e)
   		{
   			e.printStackTrace();
   			return ("");
   		}        
               
   		return (total.toString());
   	}	
	/**
	 *
	 */
	public Boolean fetchURLBinary (String aServer,String aPath)  throws MalformedURLException, IOException 
	{
   		internalURLCache="http://"+aServer+aPath;
   		
		return (getHTTPBinaryFile (aServer,aPath));
  	}		
	/**
	 *
	 */
    public Image fetchImage (String address, Component c) throws MalformedURLException, IOException 
    {
    	debug ("fetchImage ("+address+")");
    	
        URL url = new URL(address);
        return c.createImage((java.awt.image.ImageProducer)url.getContent());
    }
	/**
	 *
	 */
    public boolean sendData (String address,String aData)  throws MalformedURLException, IOException 
    {
    	debug ("sendData ("+address+")");
    	
    	try 
    	{
    	    URL url = new URL(address);
    	    URLConnection conn = url.openConnection();
    	    conn.setDoOutput(true);
    	    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    	    wr.write(aData);
    	    wr.flush();

    	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

    	    @SuppressWarnings("unused")
			String line=null;
    	    
    	    while ((line = rd.readLine()) != null) 
    	    {
    	        // Process line...
    	    }
    	    wr.close();
    	    rd.close();
    	} 
    	catch (Exception e) 
    	{
    		
    	}
    	    	
    	return (true);
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange (PropertyChangeEvent evt) 
    {
    	debug ("propertyChange ()");
    	
        if ("progress" == evt.getPropertyName()) 
        {
            int progress = (Integer) evt.getNewValue();
            
           	if (updater!=null)
         		updater.setValue(progress);
           	else
           		debug ("Error no progress bar object available");
        } 
    }	
	/**
	 *
	 */	
	public Boolean getHTTPBinaryFile (String aURL,String aLocation)
	{
		debug ("getHTTPBinaryFile ("+aURL+")");
		
		URL u=null;
		
		try 
		{
			u = new URL(aURL);
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
			return (false);
		}
		
	    URLConnection uc=null;
	    
		try 
		{
			uc = u.openConnection();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return (false);
		}

	    String contentType = uc.getContentType();
	    int contentLength = uc.getContentLength();
	    
	    if (contentType.startsWith ("text/") || contentLength == -1) 
	    {
	      debug ("This is not a binary file.");
	      return (false);
	    }
	    
	    InputStream raw=null;
	    
		try 
		{
			raw = uc.getInputStream();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return (false);
		}
		
	    InputStream in = new BufferedInputStream(raw);
	    byte[] data = new byte [contentLength];
	    int bytesRead = 0;
	    int offset = 0;

	    UI.setVisualProgress(0);
	    
	    while (offset < contentLength) 
	    {	    	    		    	
	    	try 
	    	{
				bytesRead = in.read(data, offset, data.length - offset);
			} 
	    	catch (IOException e) 
	    	{
				e.printStackTrace();
				return (false);
			}
	    	
	    	if (bytesRead == -1)
	    		break;
	    	
	    	offset += bytesRead;

	    	UI.setVisualProgress( (offset*100)/contentLength );
		    
	    	trace.out(".");

	    }
	    
	    trace.out("\n");
	    
	    try 
	    {
			in.close();
		} 
	    catch (IOException e) 
	    {
			e.printStackTrace();
			return (false);
		}

	    if (offset != contentLength) 
	    {
	      debug ("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
	      return (false);
	    }

	    FileOutputStream out=null;
	    
		try 
		{
			out = new FileOutputStream(aLocation);
		} 
		catch (FileNotFoundException e) 
		{		
			e.printStackTrace();
			return (false);
		}
	    
	    try 
	    {
			out.write(data);
		} 
	    catch (IOException e) 
	    {		
			e.printStackTrace();
			
			try 
			{
				out.close();
			} 
			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			return (false);
		}
	    
	    try 
	    {
			out.flush();
		} 
	    catch (IOException e) 
	    {		
			e.printStackTrace();
			
			try 
			{
				out.close();
			} 
			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			
			return (false);
		}
	    
	    try 
	    {
			out.close();
		} 
	    catch (IOException e) 
	    {
			e.printStackTrace();
			return (false);
		}	 
	    
	    return (true);
	}     
	/** 
	 * @return
	 */
	public byte[] getData ()
	{
		return (binaryData);
	}
	/**
	 * Sends an HTTP HEAD request to verify internet connectivity.
	 * @param url the URL to send the request to
	 * @return number of milliseconds from sending of request to receipt of response,
	 *         or negative if request failed (i.e. no connectivity) or URL's protocol is not HTTP
	 */
	public long checkConnectivity(URL url)
	{
		try
		{
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000); // don't wait more than 5 seconds
			conn.setRequestMethod("HEAD");
			long start = System.currentTimeMillis();
			conn.connect();
			long end = System.currentTimeMillis();
			conn.disconnect();
			return end - start;
		}
		catch (Exception e)
		{
			return -1;
		}
	}
}
