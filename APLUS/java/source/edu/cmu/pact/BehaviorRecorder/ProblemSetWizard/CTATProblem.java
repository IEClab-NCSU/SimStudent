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

import edu.cmu.hcii.ctat.CTATBase;

public class CTATProblem extends CTATBase 
{		
	private String condition="";
	private String swf="";
	private String brd="";
	private String problemType="";
	
	/**
	 *
	 */ 
	public CTATProblem () 
	{		
		setClassName ("CTATProblem");
		debug ("CTATProblem ()");
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
	public void setSwf(String swf) 
	{
		this.swf = removeGarbage (swf);
	}
	/**
	 *
	 */
	public String getSwf() 
	{
		return swf;
	}
	/**
	 *
	 */
	public void setBrd(String brd) 
	{
		this.brd = removeGarbage (brd);
	}
	/**
	 *
	 */
	public String getBrd() 
	{
		return brd;
	}
	/**
	 *
	 */
	public void setProblemType(String problemType) 
	{
		this.problemType = removeGarbage (problemType);
	}
	/**
	 *
	 */
	public String getProblemType() 
	{
		return problemType;
	}		
}
