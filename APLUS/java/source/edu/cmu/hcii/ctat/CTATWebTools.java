/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATWebTools.java,v 1.8 2012/09/21 13:19:01 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATWebTools.java,v $
 Revision 1.8  2012/09/21 13:19:01  vvelsen
 Quick checkin to get vital code into CVS for FIRE

 Revision 1.7  2012/08/23 21:07:10  kjeffries
 clean-ups

 Revision 1.6  2012/08/20 15:31:36  kjeffries
 restore some stuff that was lost in the merge

 Revision 1.4  2012/07/23 16:33:39  kjeffries
 fixed bug involving '=' in the value of a key=value pair

 Revision 1.3  2012/02/08 21:06:16  sewall
 First working version of 304 Not Modified responses, with +1 day Expires heading.

 Revision 1.2  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.3  2011/09/26 17:00:15  sewall
 Refactoring: add CTATLink.getAdminPassword(), use CTATHTTPHandler.sendResponse() more often. Ajax: add 'getpush.cgi' entry to CTATHTTPHandler.doPost().

 Revision 1.2  2011/04/01 20:09:57  vvelsen
 Further features and refinements in the problem sequencing code.

 Revision 1.1  2011/03/25 20:38:49  vvelsen
 Added much more capabilties to the USB TutorShop. We can now start problems defined in problem_set.xml files. Much more of the problem sequencing code has been finished but more needs to happen there. Some of the internals have been bolstered.

 $RCSfile: CTATWebTools.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATWebTools.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
 
*/

package edu.cmu.hcii.ctat;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 
 */
public class CTATWebTools extends CTATBase 
{
	/** Date format for HTTP headers. Format and time zone GMT are mandated by RFC 2616. */
	public static final DateFormat headerDateFmt =
		new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	static {
		headerDateFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	private ArrayList<String> entries=null;
	
	/**
	 *
	 */
	public CTATWebTools () 
	{
		setClassName ("CTATWebTools");
		debug ("CTATWebTools ()");   	
		
		setEntries(new ArrayList<String> ());
	}
	/**
	 *
	 */
	public Map<String, String> parseQuery (String aQuery) 
	{
		debug ("parseQuery ("+aQuery+")");
		
		String cleaned="";
		
		try 
		{
			cleaned = URLDecoder.decode (aQuery,"UTF-8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			// "UTF-8" should always be supported
			e.printStackTrace();
			return (null);
		}
		
		debug ("Cleaned: " + cleaned);
		
		return (getQueryMap (cleaned));		
	}
	/**
	 * String query = url.getQuery();  
	 * Map<String, String> map = getQueryMap(query);  
	 * Set<String> keys = map.keySet();  
	 * for (String key : keys)  
	 * {  
	 *     System.out.println("Name=" + key);  
	 *     System.out.println("Value=" + map.get(key));  
	 *  }  
	 */	
	public Map<String, String> getQueryMap( String query)  
	{  
		debug ("getQueryMap ()");
		
		String[] params = query.split("&");  
		Map<String, String> map = new HashMap<String, String>();
		
		for (String param : params)  
		{
			String name, value;
			int index = param.indexOf("=");
			if(index >= 0)
			{
				name = param.substring(0, index); // everything before the first equals sign
				value = param.substring(index+1); // everything after the first equals sign
			}
			else // no equals sign in string
			{
				name = param;
				value = "";
			} 
			map.put(name, value);  
		}

		debug ("getQueryMap () map: "+map);

		return map;  
	}  	
	/**
	 *
	 */
	public void showURI (URI aURI) 
	{
		debug ("showURI ()");
		
		debug ("URI (host):" + aURI.getHost());
		debug ("URI (port):" + aURI.getPort());
		debug ("URI (fragment):" + aURI.getFragment());
		debug ("URI (path):" + aURI.getPath());
		debug ("URI (query):" + aURI.getRawQuery());
		debug ("URI (scheme):" + aURI.getScheme());		
	}	
	/**
	 *
	 */		
	public void setEntries(ArrayList<String> entries) 
	{
		this.entries = entries;
	}
	/**
	 *
	 */	
	public ArrayList<String> getEntries() 
	{
		return entries;
	}	
}
