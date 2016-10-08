/**

 $Author: vvelsen $ 
 $Date: 2012-12-06 13:48:41 -0500 (Thu, 06 Dec 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATUserData.java,v 1.2 2012/10/16 20:01:34 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATUserData.java,v $
 Revision 1.2  2012/10/16 20:01:34  vvelsen
 Lots of changes to align the various http handlers. Most of the work has been done in the DVD handler for FIRE which now uses the progress database to also store user information

 $RCSfile: CTATUserData.java,v $ 
 $Revision: 18585 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATUserData.java,v $ 
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
//import java.util.HashMap;
import java.util.Map;

import edu.cmu.pact.ctat.model.Skills;

public class CTATUserData extends CTATBase implements Serializable
{
	private static final long serialVersionUID = 5171433320576778240L;
				
	public String firstName="";
	public String lastName="";
	public String email="";
	public String username="";
	public String password="";
	public String section="";
	public String skillXML="";
	
	public Skills skillObject=null;
	
	public Map<String, UserAssignmentInfo> assignmentInfo=null;
	public CTATMilestoneManager milestoneManager=null;
	
	/**
	 *
	 */
    public CTATUserData () 
    {
    	setClassName ("CTATUserData");
    	debug ("CTATUserData ()");
    	
    	milestoneManager=new CTATMilestoneManager ();
    	
    	skillObject=new Skills ();
    	
    	createAssignmentInfo ();
    }
    /**
     * 
     */
    public Map<String, UserAssignmentInfo> getAssignmentInfo ()
    {
    	return (assignmentInfo);
    }
    /**
     * 
     */
    public void createAssignmentInfo ()
    {
    	debug ("createAssignmentInfo ()");
    	
    	if (assignmentInfo==null)
    		assignmentInfo= new HashMap<String, UserAssignmentInfo>();
    }
    /**
     * 
     */
    public CTATMilestoneManager getMilestoneManager ()
    {
    	return (milestoneManager);
    }
}
