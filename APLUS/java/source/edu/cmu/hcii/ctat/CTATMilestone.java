/**
 ------------------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 $RCSfile$ 
 $Revision$ 
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

package edu.cmu.hcii.ctat;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 */
public class CTATMilestone extends CTATBase implements Serializable 
{	
	private static final long serialVersionUID = -356274976381830723L;
	
	public static int TYPEUNSET=0;
	public static int TYPETIME=1;
	public static int TYPESKILL=2;
	public static int TYPEMETA=3;
	public static int TYPEPSET=4;
	public static int TYPEASSN=5;
	
	public static int WAITING=0;
	public static int ACCOMPLISHED=1;
	public static int SHOWN=2;
	
	public static int METAAND=0;
	public static int METAOR=1;
		
	private int type=TYPESKILL;
	private String slug=""; // whatever the hell that means
	private String description="";
	private String image="CT30.png";
	private ArrayList <String> children=null;	
	private String test="nop"; // The actual check to perform e.g. a skill check or a meta check
	private int metaType=METAAND;
	
	private String assignment = null; // Use this for skill-based milestones, to disambiguate skills with the same name but in different assignments
	
	private int state=WAITING;
	
	/**
	 * 
	 */
	public CTATMilestone ()
	{
    	setClassName ("CTATMilestone");
    	debug ("CTATMilestone ()"); 		
    	
    	children=new ArrayList<String> ();
	}
	/**
	 * 
	 */	
	public int getMetaType() 
	{
		return metaType;
	}
	/**
	 * 
	 */	
	public void setMetaType(int metaType) 
	{
		this.metaType = metaType;
	}	
	/**
	 * 
	 */
	public ArrayList<String> getChildren ()
	{
		return (children);
	}
	/**
	 * 
	 */
	public String getDescription() 
	{
		return description;
	}
	/**
	 * 
	 */
	public void setDescription(String description) 
	{
		this.description = description;
	}
	/**
	 * 
	 */
	public int getType() 
	{
		return type;
	}
	/**
	 * 
	 */
	public void setType(int type) 
	{
		this.type = type;
	}
	/**
	 * 
	 */
	public String getImage() 
	{
		return image;
	}
	/**
	 * 
	 */
	public void setImage(String image) 
	{
		this.image = image;
	}
	/**
	 * 
	 */
	public int getState() 
	{
		return state;
	}
	/**
	 * 
	 */	
	public void setState(int state) 
	{
		this.state = state;
	}
	/**
	 * 
	 */
	public String getSlug() 
	{
		return slug;
	}
	/**
	 * 
	 */
	public void setSlug(String slug) 
	{
		this.slug = slug;
	}
	/**
	 * 
	 */
	public String getTest() 
	{
		return test;
	}
	/**
	 * 
	 */	
	public void setTest(String aTest) 
	{		
		debug ("setTest ("+aTest+")");
		
		if (aTest.startsWith("skill:")==true)
		{	
			this.test = aTest.substring(6);
			
			type=TYPESKILL;
			
			debug ("test: " + test);
					
			return;
		}
		
		if (aTest.startsWith("assignment_completed:")==true)
		{
			this.test = aTest.substring(21);
			
			type=TYPEASSN;
			
			debug ("test: " + test);
			
			return;
		}
		
		if (aTest.startsWith("problemset_completed:")==true)
		{
			this.test = aTest.substring(21);
			
			type=TYPEPSET;
						
			debug ("test: " + test);
			
			return;
		}
		
		if (aTest.startsWith("meta:")==true)
		{
			this.test = aTest.substring(5);
			
			type=TYPEMETA;
			
			debug ("test: " + test);
			
			parseMeta (test);
			
			return;
		}		
	}
	
	/**
	 * Get the assignment that is relevant to this milestone.
	 * This is useful because some skills have the same name but are in different assignments and trigger different milestones. 
	 */
	public String getAssignment()
	{
		return assignment;
	}
	/**
	 * Set the assignment that is relevant to this milestone.
	 * This is useful because some skills have the same name but are in different assignments and trigger different milestones.
	 */
	public void setAssignment(String assignment)
	{
		this.assignment = assignment;
	}
	
	/**
	 * 
	 */
	private void parseMeta (String test)
	{
		debug ("parseMeta ("+test+")");
		
		// For now we can assume that a meta test either is an AND operation or
		// an OR operation but not a combination of the two
		
		if (test.indexOf(":or:")!=-1)
		{
			this.setMetaType(METAOR);
			
			String splitterA []=test.split(":or:");
			
			debug ("Found " + splitterA.length + " terms for OR meta");
			
			children=new ArrayList<String> ();
			
			for (int i=0;i<splitterA.length;i++)
			{
				children.add(splitterA [i]);
			}
		}
		else
		{
			this.setMetaType(METAAND);
			
			String splitterB []=test.split(":and:");
			
			debug ("Found " + splitterB.length + " terms for AND meta");
			
			children=new ArrayList<String> ();
			
			for (int i=0;i<splitterB.length;i++)
			{
				children.add(splitterB [i]);			}			
		}
	}
	
	/**
	 * Use this only for debugging.
	 */
	public String toString() {
		return "Type: "+type+"; Slug: "+slug+"; Description: "+description+"; Image: "+image+"; Children: "+children+"; Test: "+test+"; MetaType: "+metaType+"; Assignment: "+assignment+"; State: "+state;
	}
}
