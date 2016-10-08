/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-09-23 12:26:26 -0400 (Mon, 23 Sep 2013) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/TutorShopUSB/src/edu/cmu/hcii/ctat/CTATDirectoryTXT.java,v 1.3 2011/04/28 

17:11:56 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATDirectoryTXT.java,v $
 Revision 1.8  2012/09/11 13:16:18  vvelsen
 Made various changes to the local http handler. It can now use both a directory.txt file as well as a curriculum.xml file. Added a way to indicate if the server should generate flashvars with an info field. When the info field is generated it creates the horizontal menu bar at the top of the screen that shows all available problems

 Revision 1.7  2012/09/06 17:48:55  vvelsen
 Reworking of the local tutorshop to allow easier managment of runtime contexts. The completely local/demo http handler is now in its own file and the CLBridge should now be ready to be tested as a standalone class

 Revision 1.6  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.5  2012/05/04 14:23:02  kjeffries
 fixed bug where the name field instead of directory field of directory.txt entries was used to order problem set directories

 Revision 1.4  2012/03/05 08:37:06  sewall
 Restore optional use of htdocs/FlashTutors/directory.txt to select and sequence problem sets found in htdocs/FlashTutors.

 Revision 1.3  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.6  2011/05/20 16:21:46  kjeffries
 Method loadDirectoryTXT now actually loads the problem sets rather than just reading in their attributes.

 Revision 1.5  2011/05/18 15:28:01  kjeffries
 Eliminated use of class CTATDirectoryEntry. Instead, each problem set is represented directly as a CTATProblemSet.

 Revision 1.4  2011/05/17 15:21:16  kjeffries
 made change so method loadDirectoryTXT no longer actually reads directory.txt; instead it considers every subdirectory that contains problem_set.xml to be a problem set. However, this loses the "name" and "description" attributes that were provided in directory.txt

 Revision 1.3  2011/04/28 17:11:56  vvelsen
 Finally connected all the dots and made sure that we can access platform specific drivers (java classes) through a central 

registry. Please see CTATLink for more information.

 Revision 1.2  2011/04/08 15:31:30  vvelsen
 Full implementation now of problem set navigation using the floating menu. The code has now changed to ensure that all 

problem set navigation is derived from the directory.txt index file.

 Revision 1.1  2011/04/08 14:19:17  vvelsen
 Added code that reads in the directory.txt file in the FlashTutors directory. It will also show the contents of this file as 

a floating navigational menu in the top left of every page.

 $RCSfile: CTATDirectoryTXT.java,v $ 
 $Revision: 19526 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATDirectoryTXT.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
 
*/

package edu.cmu.hcii.ctat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class CTATDirectoryTXT extends CTATBase 
{
	private ArrayList<CTATProblemSet> entries=null;
	private boolean loaded=false;
	private int setIndex=0;
	
	/**
	 *
	 */
	public CTATDirectoryTXT () 
	{
		setClassName ("CTATDirectoryTXT");
		debug ("CTATDirectoryTXT ()");
					
		entries=new ArrayList<CTATProblemSet> ();
	}
	/**
	 *
	 */
	public ArrayList<CTATProblemSet> getEntries ()
	{
		return (entries);
	}
	/**
	 * 
	 */
	public CTATProblemSet getNextEntry ()
	{
		setIndex++;
		
		if (setIndex<entries.size())
		{
			return (entries.get(setIndex));
		}
		
		return (null);
	}
	/**
	 *
	 */	
	public void setLoaded(boolean loaded) 
	{
		this.loaded = loaded;
	}
	/**
	 *
	 */	
	public boolean isLoaded() 
	{
		return loaded;
	}	
	/**
	 *
	 */	
	public CTATProblemSet getProblemSet (String aProblemSet)
	{
		debug ("getProblemSet ("+aProblemSet+")");
		
		for (int i=0;i<entries.size();i++)
		{
			CTATProblemSet entry=entries.get(i);
			
			if (entry.getDirectory().equals(aProblemSet)==true)
				return (entry);
		}
		
		return (null);
	}
	/**
	 *
	 */
	public boolean loadDirectoryTXT (String aFile)
	{
		debug ("loadDirectoryTXT ()");
						
		if(aFile == null || aFile.equals("")) 
		{
			return false;
		}

		// sewall 2012/03/05: interim use of directory.txt for ordering local problem sets
		Map<String, CTATDirectoryEntry> dirTxtMap = CTATDirectoryEntry.createMapFromFile(aFile);
		Map<String, CTATProblemSet> dirPSMap = new HashMap<String, CTATProblemSet>();
		
		File directory = (new File(aFile)).getParentFile(); /* get the parent directory of where directory.txt should be */
		
		File[] subdirs = directory.listFiles(); /* Each subdirectory is a potential problem set */
		
		if(subdirs == null) 
		{
			return false;
		}

		/* consider each subdirectory */
		for(int i = 0; i < subdirs.length; i++)
		{
			if(!subdirs[i].isDirectory()) 
			{
				continue; /* if the "subdirectory" is a file, not a directory, move on to the next subdir */
			}

			CTATDirectoryEntry dirTxtEntry = null;
			if (dirTxtMap != null) 
			{
				dirTxtEntry = dirTxtMap.get(subdirs[i].getName());
				if (dirTxtEntry == null)
					continue;             // this problem set not listed in directory.txt
			}
			
			String[] contents = subdirs[i].list(); /* get an array of the filenames in the subdir */
			if(contents == null) 
			{
				continue; /* this subdirectory is not a problem set */
			}

			boolean isProblemSet = false; /* is this subdirectory a problem set? */

			/* Search for problem_set.xml; if it exists, this subdirectory is a problem set */
			for(int j = 0; j < contents.length; j++)
			{
				if(contents[j].equals("problem_set.xml"))
				{
					isProblemSet = true;
					break;
				}
			}

			if(isProblemSet) 
			{
				CTATProblemSet entry = new CTATProblemSet();
				
				if (entry.loadProblemSet (subdirs[i].toString() + "/problem_set.xml")==false)
				{
					debug ("Error loading problem set");
					return (false);
				}
				
				debug("Problem set directory: " + subdirs[i].getName());
				
				if (dirTxtMap != null)
					dirPSMap.put(subdirs[i].getName(), entry);
				else 
				{
					entries.add(entry); // add the newly loaded problem set to the collection of problem sets
					loaded = true;
				}
			}
		}
		
		// if we have a directory.txt, reorder to match it
		if (dirTxtMap != null) 
		{
			orderEntriesAccordingToMap(dirTxtMap, dirPSMap);
		}

		return true;
	}
	/**
	 * Order the {@link #entries} by the sequence from a directory.txt file.
	 * Allow name and description in directory.txt to override those in problem_set.xml.
	 * @param dirTxtMap map of directory names with desired or
	 * @param dirPSMap map of directory names to {@link CTATProblemSet}s
	 */
	private void orderEntriesAccordingToMap (Map<String,CTATDirectoryEntry> dirTxtMap, 
											 Map<String,CTATProblemSet> dirPSMap) 
	{
		for (CTATDirectoryEntry dirEntry : dirTxtMap.values()) 
		{
			CTATProblemSet entry = dirPSMap.get(dirEntry.getDirectory());   // dirPSMap is indexed by directory name, not problem set name
			if (entry == null)
				continue;
			if (dirEntry.getName() != null && dirEntry.getName().length() > 0)
				entry.setName(dirEntry.getName());
			if (dirEntry.getDescription() != null && dirEntry.getDescription().length() > 0)
				entry.setDescription(dirEntry.getDescription());
			entries.add(entry);
			loaded = true;
		}
	}
}
