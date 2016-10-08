/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATUser.java,v 1.1 2012/09/06 17:48:55 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATUser.java,v $
 Revision 1.1  2012/09/06 17:48:55  vvelsen
 Reworking of the local tutorshop to allow easier managment of runtime contexts. The completely local/demo http handler is now in its own file and the CLBridge should now be ready to be tested as a standalone class

 $RCSfile: CTATUser.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATUser.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
*/

package edu.cmu.hcii.ctat;

/**
 * 
 */
public class CTATUser extends CTATBase
{
	private String userID="";
	
	/**
	 * 
	 */	
	public CTATUser ()
	{
    	setClassName ("CTATUser");
    	debug ("CTATUser ()");
	}
	/**
	 * 
	 */
	public String getUserID() 
	{
		return userID;
	}
	/**
	 * 
	 */
	public void setUserID(String userID) 
	{
		this.userID = userID;
	}
}
