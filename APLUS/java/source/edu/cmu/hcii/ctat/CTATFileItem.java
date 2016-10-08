/**
 -
 License:
 -
 ChangeLog:
 $Log: CTATBase.java,v $
 Revision 1.15  2012/09/12 14:28:34  vvelsen
 Complete reworking of our http handling code. All file requests now go through our singleton file handler which can handle mounted jar files and as such creates a miniature vfsl

 Revision 1.14  2012/08/30 15:25:33  sewall
 Fix-ups after Alvaro's 2012/08/17 merge.

 -
 Notes:
 
*/

package edu.cmu.hcii.ctat;

import javax.swing.JMenuItem;

public class CTATFileItem extends CTATBase
{
	private String fileName="";
	private String directory="";
	private JMenuItem visual=null;	
	
	
	/**
	 *
	 */
    public CTATFileItem () 
    {
    	setClassName ("CTATFileItem");
    	debug ("CTATFileItem ()");       	
    }
	/**
	 *
	 */
	public String getFileName() 
	{
		return fileName;
	}
	/**
	 *
	 */
	public void setFileName(String fileName) 
	{
		this.fileName = fileName;
	}
	/**
	 *
	 */
	public String getDirectory() 
	{
		return directory;
	}
	/**
	 *
	 */
	public void setDirectory(String directory) 
	{
		this.directory = sanitizePath (directory);
	}
	/**
	 *
	 */
	public String getFullPath ()
	{
		return (directory + fileName);
	}
	/**
	 * 
	 * @return
	 */
	public JMenuItem getVisual() 
	{
		return visual;
	}
	/**
	 * 
	 * @param visual
	 */
	public void setVisual(JMenuItem visual) 
	{
		this.visual = visual;
	}
	/**
	 * 
	 */
	public String sanitizePath (String aPath)
	{
		String result=aPath.replace("\\", "/").replace("//","/");
		
		debug ("sanitizePath ("+aPath+"->"+result+")");
		
		return (result);
	}	
}
