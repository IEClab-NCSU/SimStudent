/**

 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/PositionWithinAssignment.java,v 1.1 2012/10/17 20:31:53 vvelsen Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: PositionWithinAssignment.java,v $
 Revision 1.1  2012/10/17 20:31:53  vvelsen
 Added missing files

 $RCSfile: PositionWithinAssignment.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/PositionWithinAssignment.java,v $ 
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
 * Represents the position of a problem within an assignment. Can be used to mark the
 * current problem or as an index to the problem state and summary for a certain problem.
 */
public class PositionWithinAssignment implements Serializable
{
	public static final long serialVersionUID = 0;
		
	public String problemSet;
	public int position; // position within the problem set (1 for first problem of problem set, 2 for second, ...)
		
	public PositionWithinAssignment(String ps, int pos)
	{
		problemSet = ps;
		position = pos;
	}
		
	public PositionWithinAssignment() 
	{ 
			
	}
		
	public boolean equals(Object obj)
	{
		if(obj instanceof PositionWithinAssignment)
		{
			PositionWithinAssignment that = (PositionWithinAssignment) obj;
				
			//compare problemSet
			if(this.problemSet == null)
			{
				if(that.problemSet != null)
				{
					return false;
				}
			}
			else
			{
				if(!(this.problemSet.equals(that.problemSet)))
				{
					return false;
				}
			}
				
			//compare position
			return (this.position == that.position);
		}
		else return false;
	}
		
	public int hashCode()
	{
		return ((problemSet != null) ? problemSet.hashCode() : 0) + position;
	}
		
	public String toString() { return problemSet + ", " + position; }
}
	