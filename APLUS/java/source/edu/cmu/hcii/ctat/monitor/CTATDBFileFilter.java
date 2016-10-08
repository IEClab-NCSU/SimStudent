/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATDBFileFilter.java,v 1.3 2012/10/11 15:20:08 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATDBFileFilter.java,v $
 Revision 1.3  2012/10/11 15:20:08  akilbo
 Made them public so that they may be imported to the classes in TutorMonitorServer

 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.1  2012/04/30 18:21:39  vvelsen
 Added some support code around the database management system. This will allow users and developers to choose date ranges in the database

 $RCSfile: CTATDBFileFilter.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATDBFileFilter.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:

*/


package edu.cmu.hcii.ctat.monitor;
import java.io.File;

import javax.swing.filechooser.FileFilter;

/** 
 * @author vvelsen
 *
 */
public class CTATDBFileFilter extends FileFilter 
{
	private String description="";
  	//private String extensions[]=new String[]{"csv", "txt", "dat"};
	private String extensions[];

  	/** 
  	 * @param description
  	 * @param extension
  	 */
  	public CTATDBFileFilter(String description, String extension) 
  	{
  		this(description, new String[] 
  		{ 
  			extension 
  		});
  	}	
  	/** 
  	 * @param description
  	 * @param extensions
  	 */
  	public CTATDBFileFilter(String description, String extensions[]) 
  	{
  		if (description == null) 
  		{
  			this.description = extensions[0];
  		} 
  		else 
  		{
  			this.description = description;
  		}
  		
  		this.extensions = (String[]) extensions.clone();
  		toLower(this.extensions);
  	}
  	/** 
  	 * @param array
  	 */
  	private void toLower(String array[]) 
  	{
  		for (int i = 0, n = array.length; i < n; i++) 
  		{
  			array[i] = array[i].toLowerCase();
  		}
  	}
  	/** 
  	 * @return
  	 */
  	public String getDescription() 
  	{  		
  		return description;
  	}
  	/** 
  	 * @param file
  	 * @return
  	 */
  	public boolean accept(File file) 
  	{
  		if (file.isDirectory()) 
  		{
  			return true;
  		} 
  		else 
  		{
  			String path = file.getAbsolutePath().toLowerCase();
  			
  			for (int i = 0, n = extensions.length; i < n; i++) 
  			{
  				String extension = extensions[i];
  				
  				if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) 
  				{
  					return true;
  				}
  			}
  		}
  		
  		return false;
  	}
}
