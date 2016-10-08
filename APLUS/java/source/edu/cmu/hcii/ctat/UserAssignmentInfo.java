/**

 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/UserAssignmentInfo.java,v 1.1 2012/10/17 20:31:53 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: UserAssignmentInfo.java,v $
 Revision 1.1  2012/10/17 20:31:53  vvelsen
 Added missing files

 $RCSfile: UserAssignmentInfo.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/UserAssignmentInfo.java,v $ 
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
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the information for a single assignment for a certain user.
 */
public class UserAssignmentInfo implements Serializable
{
	public static final long serialVersionUID = 0;
		
	public PositionWithinAssignment currentProblemPosition; // position of current/next problem
	public Map<PositionWithinAssignment, UserProblemInfo> problemInfoMap; // info on each of the problems that have been started
	
	public UserAssignmentInfo()
	{
		currentProblemPosition = null;
		problemInfoMap = new HashMap<PositionWithinAssignment, UserProblemInfo>();
	}
}
