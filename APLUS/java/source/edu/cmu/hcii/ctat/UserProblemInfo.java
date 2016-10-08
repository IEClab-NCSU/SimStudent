/**

 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/UserProblemInfo.java,v 1.1 2012/10/17 20:31:53 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: UserProblemInfo.java,v $
 Revision 1.1  2012/10/17 20:31:53  vvelsen
 Added missing files

 $RCSfile: UserProblemInfo.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/UserProblemInfo.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
   
*/

package edu.cmu.hcii.ctat;

import java.io.Serializable;

/**
 * Holds information for a single problem for a certain user.
 */
public class UserProblemInfo implements Serializable
{
	public static final long serialVersionUID = 0;
		
	public String problemState=null;
	public String problemSummary=null;
		
	public UserProblemInfo(String state, String summary)
	{
		problemState = state;
		problemSummary = summary;
	}
}	