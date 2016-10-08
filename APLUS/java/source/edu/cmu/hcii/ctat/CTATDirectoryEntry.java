/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATDirectoryEntry.java,v 1.4 2012/09/11 13:16:18 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATDirectoryEntry.java,v $
 Revision 1.4  2012/09/11 13:16:18  vvelsen
 Made various changes to the local http handler. It can now use both a directory.txt file as well as a curriculum.xml file. Added a way to indicate if the server should generate flashvars with an info field. When the info field is generated it creates the horizontal menu bar at the top of the screen that shows all available problems

 Revision 1.3  2012/03/05 08:37:06  sewall
 Restore optional use of htdocs/FlashTutors/directory.txt to select and sequence problem sets found in htdocs/FlashTutors.

 Revision 1.2  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.1  2011/04/08 14:19:17  vvelsen
 Added code that reads in the directory.txt file in the FlashTutors directory. It will also show the contents of this file as a floating navigational menu in the top left of every page.

 $RCSfile: CTATDirectoryEntry.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATDirectoryEntry.java,v $ 
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

import java.util.LinkedHashMap;
import java.util.Map;

import edu.cmu.pact.Utilities.trace;

public class CTATDirectoryEntry extends CTATBase 
{
	private String directory="";
	private String name="";
	private String description="";
	private int index=-1;  // position in file
	
	/**
	 * @return the {@link #index}
	 */
	int getIndex() {
		return index;
	}
	/**------------------------------------------------------------------------------------
	 *
	 */
	public CTATDirectoryEntry () 
	{
		setClassName ("CTATDirectoryEntry");
		debug ("CTATDirectoryEntry ()");   			
	}
	/**------------------------------------------------------------------------------------
	 *
	 */
	public void setDirectory(String directory) 
	{
		this.directory = directory;
	}
	/**------------------------------------------------------------------------------------
	 *
	 */
	public String getDirectory() 
	{
		return directory;
	}
	/**------------------------------------------------------------------------------------
	 *
	 */
	public void setName(String name) 
	{
		this.name = name;
	}
	/**------------------------------------------------------------------------------------
	 *
	 */
	public String getName() 
	{
		return name;
	}
	/**------------------------------------------------------------------------------------
	 *
	 */
	public void setDescription(String description) 
	{
		this.description = description;
	}
	/**------------------------------------------------------------------------------------
	 *
	 */
	public String getDescription() 
	{
		return description;
	}
	//-------------------------------------------------------------------------------------	
	
	/**
	 * Create a map with key directory name and value {@link CTATDirectoryEntry}
	 * from a directory.txt file, whose format must be, on each line
	 * "name|directory|description".
	 * An iterator on the returned map's key set will return entries in the
	 * order originally listed in the file.
	 * @param filename
	 * @return list of {@link CTATDirectoryEntry}
	 */
	static Map<String, CTATDirectoryEntry> createMapFromFile(String filename)
	{
		String dirTXT=CTATLink.fManager.getContents (filename);
		
		if (dirTXT==null)  // file not found or I/O error
			return null;
		
		Map<String, CTATDirectoryEntry> entries =
			new LinkedHashMap<String, CTATDirectoryEntry> ();

		String lines[] = dirTXT.split("\\r?\\n");
		for (int i=0, j=0; i<lines.length; i++)
		{
			String pieces []=lines [i].split("\\|");
			if (trace.getDebugCode("localts"))
				trace.out("localts", "Name: " + pieces [0]+ ", Dir: " + pieces [1] + ", Desc: " + pieces [2]);
			String key = pieces [1];
			if (key == null || key.length() < 1)
				continue;
			CTATDirectoryEntry entry=new CTATDirectoryEntry ();
			entry.setName (pieces [0]);
			entry.setDirectory (key);
			entry.setDescription (pieces [2]);
			entry.index = j++;
			entries.put(key, entry);
		}
		return entries;
	}
}
