/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2011-09-01 14:04:52 -0400 (Thu, 01 Sep 2011) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.2  2011/08/26 13:12:12  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.1  2011/07/06 19:57:39  vvelsen
 Added an experiment design tool.

 $RCSfile$ 
 $Revision: 13027 $ 
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

package edu.cmu.pact.BehaviorRecorder.ProblemSetWizard;

import java.util.ArrayList;

import edu.cmu.hcii.ctat.CTATBase;

public class CTATProblemSet extends CTATBase 
{		
	private String condition="Undefined";
	private String description="Undefined";
	private ArrayList <CTATProblem> problems=null;
	
	/**
	 *
	 */ 
	public CTATProblemSet () 
	{		
		setClassName ("CTATProblemSet");
		debug ("CTATProblemSet ()");
		
		setProblems(new ArrayList<CTATProblem> ());
	}
	/**
	 *
	 */
	public void setProblems(ArrayList <CTATProblem> problems) 
	{
		this.problems = problems;
	}
	/**
	 *
	 */
	public ArrayList <CTATProblem> getProblems() 
	{
		return problems;
	}
	/**
	 *
	 */	
	public void setCondition(String condition) 
	{
		this.condition = removeGarbage (condition);
	}
	/**
	 *
	 */	
	public String getCondition() 
	{
		return condition;
	}
	/**
	 *
	 */	
	public void setDescription(String description) 
	{
		this.description = removeGarbage (description);
	}
	/**
	 *
	 */	
	public String getDescription() 
	{
		return description;
	}			
}
