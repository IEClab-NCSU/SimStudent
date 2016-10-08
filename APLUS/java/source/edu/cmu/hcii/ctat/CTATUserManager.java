/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATUserManager.java,v 1.2 2012/09/12 14:28:34 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATUserManager.java,v $
 Revision 1.2  2012/09/12 14:28:34  vvelsen
 Complete reworking of our http handling code. All file requests now go through our singleton file handler which can handle mounted jar files and as such creates a miniature vfsl

 Revision 1.1  2012/09/06 17:48:55  vvelsen
 Reworking of the local tutorshop to allow easier managment of runtime contexts. The completely local/demo http handler is now in its own file and the CLBridge should now be ready to be tested as a standalone class

 $RCSfile: CTATUserManager.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATUserManager.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
*/

package edu.cmu.hcii.ctat;

import java.util.ArrayList;

/**
 * 
 */
public class CTATUserManager extends CTATBase
{
	private ArrayList <CTATUser> users=null;
	
	/**
	 *
	 */	
	public CTATUserManager ()
	{
    	setClassName ("CTATUserManager");
    	debug ("CTATUserManager ()");
    	
		users=new ArrayList<CTATUser> ();
	}
	/**
	 *
	 */
	public ArrayList <CTATUser> getUsers ()
	{
		return (users);
	}
	/**
	 * 
	 */
	public CTATUser retrieveUser (String anID)
	{
		for (int i=0;i<users.size();i++)
		{
			CTATUser aUser=users.get(i);
			
			if (aUser.getUserID().equals(anID)==true)
				return (aUser);
		}
		
		return (null);
	}
	/**
	 * 
	 */
	public String getProfileDir() 
	{
		return CTATLink.profileDir;
	}
	/**
	 * 
	 */	
	public void setProfileDir(String profileDir) 
	{
		CTATLink.profileDir = profileDir;
	}
}
