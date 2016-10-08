/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATEmail.java,v 1.2 2012/10/08 13:04:17 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATEmail.java,v $
 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.2  2012/03/26 16:17:17  vvelsen
 Added a lot of safety features in case either the monitor or service deamon is shutdown or killed. Please see the CTATDeamon class for more information. Also added more checks and more leniency to the service checker so that it doesn't check to often and so that it has different timeouts for different types of services

 Revision 1.1  2012/02/28 21:01:47  vvelsen
 Added alerting and reporting classes that work together with a php script to send email to sys admins in case servers go down. Also added logging.

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.hcii.ctat.monitor;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import edu.cmu.hcii.ctat.CTATBase;


/**
*
*/
public class CTATEmail extends CTATBase 
{
	private ArrayList <String> contacts=null;
	
	/**
	*
	*/	
	public CTATEmail ()
	{  
    	setClassName ("CTATEmail");
    	debug ("CTATEmail ()");
    	
    	contacts=new ArrayList<String> ();
    	//contacts.add("http://ctat.pact.cs.cmu.edu/email.php");
    	//contacts.add("http://pact-cvs1.pact.cs.cmu.edu/email.php");
	}
	/**
	*
	*/
	public void addContactServer (String aContact)
	{
		debug ("addContactServer ("+aContact+")");
		
		contacts.add(aContact);
	}
	/**
	*
	*/
	protected String sendEmail (String a_to,
								String a_subject,
								String a_body)
	{
		debug ("sendEmail ()");
		
		for (int i=0;i<contacts.size();i++)
		{
			String aURL=contacts.get(i);
			debug ("Trying contact: " + aURL);
			String result=sendEmailToServer (aURL,a_to,a_subject,a_body);
			if (result!=null)
				return (result);
		}
		
		return (null);
	}
	/**
	*
	*/
	protected String sendEmailToServer (String aURL,
										String a_to,
										String a_subject,
										String a_body)
	{
		debug ("sendEmailToServer ()");
		
		StringBuffer reply=new StringBuffer ();
		
		URL url=null;
		URLConnection conn=null;
	    OutputStreamWriter wr=null;
		BufferedReader in=null;
		String line=null;	
	    String data=null;
	    
		try 
		{
			data = URLEncoder.encode("to", "UTF-8") + "=" + URLEncoder.encode(a_to, "UTF-8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (null);
		}
		
	    try 
	    {
			data += "&" + URLEncoder.encode("subject", "UTF-8") + "=" + URLEncoder.encode(a_subject, "UTF-8");
		} 
	    catch (UnsupportedEncodingException e) 
	    {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (null);
		}
	    
	    try 
	    {
			data += "&" + URLEncoder.encode("body", "UTF-8") + "=" + URLEncoder.encode(a_body, "UTF-8");
		} 
	    catch (UnsupportedEncodingException e) 
	    {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (null);
		}
		   	    
		try 
		{
			url = new URL (aURL);
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (null);
		}
	    
		try 
		{
			conn = url.openConnection();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (null);
		}
		
		conn.setDoOutput(true);
	    
		try 
		{
			wr = new OutputStreamWriter(conn.getOutputStream());
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (null);
		}
		
	    try 
	    {
			wr.write(data);
		} 
	    catch (IOException e) 
	    {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (null);
		}
	    
	    try 
	    {
			wr.flush();
		} 
	    catch (IOException e) 
	    {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (null);
		}
				
		try 
		{
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (null);
		}
		
		try 
		{
			while ((line = in.readLine()) != null) 
			{
				reply.append (line); 
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (null);
		}
				
		debug (reply.toString());
		
		return reply.toString();
	}	  
} 
