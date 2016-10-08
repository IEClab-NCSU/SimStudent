/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-09-23 12:26:26 -0400 (Mon, 23 Sep 2013) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATAssetManager.java,v 1.2 2012/09/19 12:03:38 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATAssetManager.java,v $
 Revision 1.2  2012/09/19 12:03:38  vvelsen
 Fix to the syntax in the system tray class, which for some reason wasn't picked up by Eclipse

 Revision 1.1  2012/09/18 15:21:55  vvelsen
 We should have a complete and working asset download manager that can be run as part of the config panel (or command line)

 $RCSfile: CTATAssetManager.java,v $ 
 $Revision: 19526 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATAssetManager.java,v $ 
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
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

/**
 * 
 */
public class CTATAssetManager extends CTATBase
{				
	/**
	*
	*/	
	public CTATAssetManager ()
	{  
    	setClassName ("CTATAssetManager");
    	debug ("CTATAssetManager ()");
	}	
	/**
	 * 
	 */
	public ArrayList <String> downloadGlobalAssets (ArrayList<String> toCache,
													String assetPath,
													String server)
	{
		debug ("downloadGlobalAssets ("+assetPath+","+server+")");
		
		ArrayList<String> copyCache=toCache;
		
		ArrayList<String> newCache=downloadAssets (assetPath,server);
		
		if (newCache.size()==0)
		{
			return (toCache); // Nothing changed, no harm done
		}
		
		for (int i=0;i<newCache.size();i++)
		{
			copyCache.add(newCache.get (i));
		}
						
		return (copyCache);
	}
	/**
	 * 
	 */
	public ArrayList <String> downloadAssets (String assetPath,
											  String server)
	{
		debug ("downloadAssets ("+assetPath+","+server+")");
		
		String assetXML="";
		
		ArrayList <String> toCache=new ArrayList<String> ();
		
		CTATURLFetch fetcher=new CTATURLFetch ();
						
		try 
		{
			assetXML = fetcher.fetchURL (server + assetPath);
		} 
		catch (MalformedURLException e) 
		{		
			CTATLink.lastError=("MalformedURLException Error getting: " + fetcher.getObtainedURL ());
			debug (CTATLink.lastError);
			return (toCache);
		} 
		catch (IOException e) 
		{
			CTATLink.lastError=("IOException Error getting: " + fetcher.getObtainedURL ());
			debug (CTATLink.lastError);
			return (toCache);
		}

		if (assetXML.isEmpty()==true)
		{
			return (toCache);
		}
		
		debug (assetXML);		
		
		CTATXMLDriver driver=new CTATXMLDriver ();
				
		Element root=driver.loadXMLFromString (assetXML);
				
		if (root==null)
		{
			CTATLink.lastError="Error: unable to parse incoming XML";
			debug (CTATLink.lastError);
			return (toCache);
		}
				
		if (root.getName().equalsIgnoreCase("Directory")==false)
		{
			CTATLink.lastError=("Error: element name is not Directory, instead it is: " + root.getName());
			debug (CTATLink.lastError);
			return (toCache);
		}		
		
		String basePath=root.getAttributeValue("path");
		
		debug ("Loading assets from path: " + basePath);
		
		Element entries=root.getChild("Entries");
		
		if (entries==null)
		{
			CTATLink.lastError="Error obtaining Entries field from XML";
			debug (CTATLink.lastError);
			return (toCache);
		}
				
		@SuppressWarnings("unchecked")
		List<Element> list = entries.getChildren();
		 
		for (int i=0;i<list.size();i++) 
		{
		   Element node = (Element) list.get(i);
 
		   if (node.getName().equalsIgnoreCase("Entry")==true)
		   {			   
			   if (node.getAttributeValue("type").equalsIgnoreCase("file")==true)
			   {				   
				   toCache.add("/"+basePath + "/" + node.getValue());				   
			   }
			   
			   if (node.getAttributeValue("type").equalsIgnoreCase("directory")==true)
			   {
				   debug ("Processing directory: " + "/" + basePath + "/" + node.getValue() + ".xml" + " from server: " + server);
				   
				   ArrayList <String> tempCache=downloadAssets ("/"+basePath + "/" + node.getValue()+".xml",server);

				   for (int t=0;t<tempCache.size();t++)
				   {
					   //toCache.add("/"+basePath + "/" + node.getValue() + "/" + tempCache.get(t));
					   toCache.add(tempCache.get(t));
				   }
			   }			   
		   }
		}
						
		debug ("Add done");
		
		return (toCache);
	}
}
