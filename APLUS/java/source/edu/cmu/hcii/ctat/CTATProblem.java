/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-09-23 12:26:26 -0400 (Mon, 23 Sep 2013) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATProblem.java,v 1.2 2012/01/06 22:09:23 sewall Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATProblem.java,v $
 Revision 1.2  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.4  2011/07/27 21:08:15  kjeffries
 Added attributes to the XML string returned by getSkillsXML

 Revision 1.3  2011/07/20 20:00:25  kjeffries
 Better handling for skills for a single problem

 Revision 1.2  2011/04/01 20:09:57  vvelsen
 Further features and refinements in the problem sequencing code.

 Revision 1.1  2011/03/25 20:38:49  vvelsen
 Added much more capabilties to the USB TutorShop. We can now start problems defined in problem_set.xml files. Much more of the problem sequencing code has been finished but more needs to happen there. Some of the internals have been bolstered.

 $RCSfile: CTATProblem.java,v $ 
 $Revision: 19526 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATProblem.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 
 tutor_flag="tutor" problem_file="birthday.brd" description="Randy spent $57.23 on a birthday party. After the party, Randy had gotten $200.35 as presents." name="birthday" student_interface="6.7 interface.swf"
 
 ------------------------------------------------------------------------------------
*/

package edu.cmu.hcii.ctat;

import java.util.ArrayList;

public class CTATProblem extends CTATBase 
{		
	public int index=-1;
	
	public String tutor_flag="";
	public String problem_file="";
	public String description="";
	public String name="";
	public String student_interface="";
	
	public String problem_path= "" /*"FlashTutors/ProblemOne/"*/ ;
	
	private Boolean active=false;	
	
	private class ProblemSkill // each skill for a problem has an associated number of occurrences, as specified in problem_set.xml
	{
		CTATSkill skill;
		int occurrences;
	}
	
	private ArrayList <ProblemSkill> problemSkills;
	
	/**
	 *
	 */
	public CTATProblem () 
	{
		setClassName ("CTATProblem");
		debug ("CTATProblem ()");	
		
		setSkills(new ArrayList <CTATSkill> ());
	}
	/**
	 * 
	 */	
	public Boolean getActive() 
	{
		return active;
	}
	/**
	 * 
	 */	
	public void setActive(Boolean active) 
	{
		this.active = active;
	}		
	/**
	 *
	 */	
	public void setSkills(ArrayList <CTATSkill> skills) 
	{
		problemSkills = new ArrayList<ProblemSkill>(skills.size());
		
		// add the given skills to this.skills, each with an 'occurrences' value of 1
		for(CTATSkill skill : skills)
		{
			ProblemSkill problemSkill = new ProblemSkill();
			problemSkill.skill = skill;
			problemSkill.occurrences = 1; // assume 1 as the default
			problemSkills.add(problemSkill);
		}
	}
	/**
	 *
	 */	
	public void addSkill(CTATSkill skill, int occurrences)
	{
		ProblemSkill problemSkill = new ProblemSkill();
		problemSkill.skill = skill;
		problemSkill.occurrences = occurrences;
		problemSkills.add(problemSkill);
	}
	/**
	 *
	 */	
	public ArrayList <CTATSkill> getSkills() 
	{
		ArrayList<CTATSkill> returnValue = new ArrayList<CTATSkill>(problemSkills.size());
		
		for(ProblemSkill problemSkill : problemSkills)
		{
			returnValue.add(problemSkill.skill);
		}
		
		return returnValue;
	}
	/**
	 * @return All the skills for this problem, represented as an XML element
	 */
	public String getSkillsXML()
	{
		StringBuilder str = new StringBuilder("<Skills>");
		
		for(ProblemSkill problemSkill : problemSkills)
		{
			CTATSkill skill = problemSkill.skill;
			
			str.append("<Skill name=\"");
			if(skill.name != null)
				str.append(skill.name);
			
			str.append("\" category=\"");
			if(skill.category != null)
				str.append(skill.category);
			
			str.append("\" occurrences=\"");
			str.append(problemSkill.occurrences);
			
			///////////////////////////////////////the following attributes are included in the problem_set.xml for the problem set's skills of interest, but not for the individual problem's skills
			str.append("\" pKnown=\"");
			if(skill.pKnown != null)
				str.append(skill.pKnown);
			
			str.append("\" pLearn=\"");
			if(skill.pLearn != null)
				str.append(skill.pLearn);
			
			str.append("\" description=\"");
			if(skill.description != null)
				str.append(skill.description);
			
			str.append("\" label=\"");
			if(skill.label != null)
				str.append(skill.label);
			
			str.append("\" pGuess=\"");
			if(skill.pGuess != null)
				str.append(skill.pGuess);
			
			str.append("\" pSlip=\"");
			if(skill.pSlip != null)
				str.append(skill.pSlip);
			///////////////////////////////////////
			
			str.append("\"/>");
		}
		
		str.append("</Skills>");
		
		return str.toString();
	}	
}
