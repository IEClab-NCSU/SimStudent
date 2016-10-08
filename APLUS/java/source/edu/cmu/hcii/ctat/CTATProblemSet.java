/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-09-23 12:26:26 -0400 (Mon, 23 Sep 2013) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATProblemSet.java,v 1.11 2012/09/14 22:20:46 sewall Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATProblemSet.java,v $
 Revision 1.11  2012/09/14 22:20:46  sewall
 Now display htdocs/endofproblemset.html after last problem in problem set.

 Revision 1.10  2012/09/10 22:31:35  sewall
 Now show problem description instead of problem name on navigation bar seen by students.

 Revision 1.9  2012/09/06 17:48:55  vvelsen
 Reworking of the local tutorshop to allow easier managment of runtime contexts. The completely local/demo http handler is now in its own file and the CLBridge should now be ready to be tested as a standalone class

 Revision 1.8  2012/08/24 21:46:58  kjeffries
 added getAllProblems() method

 Revision 1.7  2012/08/23 21:04:59  kjeffries
 clean-ups

 Revision 1.6  2012/08/17 17:50:32  alvaro
 merging versions

 Revision 1.4  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.3  2012/03/05 08:37:06  sewall
 Restore optional use of htdocs/FlashTutors/directory.txt to select and sequence problem sets found in htdocs/FlashTutors.

 Revision 1.2  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.10  2011/07/20 20:03:45  kjeffries
 Can now read skills from problem_set.xml, both for entire problem set and for individual problems.

 Revision 1.9  2011/07/18 15:08:00  kjeffries
 Can now read both encrypted and unencrypted problem_set.xml's, depending on the value of a static variable in CTATLink

 Revision 1.8  2011/06/03 20:35:59  kjeffries
 This class now accepts an encrypted problem_set.xml

 Revision 1.7  2011/05/27 20:54:24  kjeffries
 Added getNumProblems method.

 Revision 1.6  2011/05/20 16:23:10  kjeffries
 Added code to prevent loading the problem set twice

 Revision 1.5  2011/05/18 15:32:33  kjeffries
 Incorporated 'directory', 'name', and 'description' attributes from CTATDirectoryEntry, and a readProblemSet method that sets these attributes.

 Revision 1.4  2011/04/28 17:11:56  vvelsen
 Finally connected all the dots and made sure that we can access platform specific drivers (java classes) through a central registry. Please see CTATLink for more information.

 Revision 1.3  2011/04/01 20:09:57  vvelsen
 Further features and refinements in the problem sequencing code.

 Revision 1.2  2011/03/30 16:25:55  vvelsen
 Almost finished problem selection and problem sequencing. The tutor now also properly sets the top navigation menu.

 Revision 1.1  2011/03/25 20:38:49  vvelsen
 Added much more capabilties to the USB TutorShop. We can now start problems defined in problem_set.xml files. Much more of the problem sequencing code has been finished but more needs to happen there. Some of the internals have been bolstered.

 $RCSfile: CTATProblemSet.java,v $ 
 $Revision: 19526 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATProblemSet.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
 
*/

package edu.cmu.hcii.ctat;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class CTATProblemSet extends CTATBase 
{	
	private ArrayList <CTATProblem> problems;
	private int current=-1;
	private ArrayList <CTATSkill> skills;
	private int counter=0;
	private boolean loaded=false;
	
	private String directory="";
	private String name="";
	private String description="";	
	
	private Element root; // represents the problem_set.xml file
		
	public Integer initialSequence=-1;
	
	/**
	 *
	 */
	public CTATProblemSet () 
	{
		setClassName ("CTATProblemSet");
		debug ("CTATProblemSet ()");   		
		
		//CTATLink.fManager=new CTATDesktopFileManager ();		
		problems=new ArrayList <CTATProblem> ();
		setSkills(new ArrayList <CTATSkill> ());		
	}
	public void deActivate ()
	{
		debug ("deActivate ("+this.name+")");
		
		for (int i=0;i<problems.size();i++)
		{
			CTATProblem problem=problems.get(i);
			problem.setActive(false);
		}		
	}
	/**
	 * 
	 */
	public void listProblemSet ()
	{
		debug ("listProblemSet ("+this.name+")");
			
		for (int i=0;i<problems.size();i++)
		{
			CTATProblem problem=problems.get(i);
			debug (problem.index + " : " + problem.description + " : " + problem.name);
		}		
	}
	/**
	 * 
	 */
	public String listProblemSetHTML ()
	{
		debug ("listProblemSetHTML ("+this.name+")");
		
		StringBuffer formatted=new StringBuffer ();
		
		formatted.append("<ul>");
		
		for (int i=0;i<problems.size();i++)
		{
			CTATProblem problem=problems.get(i);
			
			if (problem.getActive()==true)
				formatted.append("<li><font color=\"#ff0000\">" + problem.name + " : " + problem.description + "</font>");
			else
				formatted.append("<li>" + problem.name + " : " + problem.description);
		}
		
		formatted.append("</ul>");
		
		return (formatted.toString());
	}	
	/**
	 *
	 */
	public void reset ()
	{
		debug ("reset ()");
		
		current=-1;
	}
	/**
	 *
	 */
	public void setLoaded (boolean aVal)
	{
		debug ("setLoaded ("+aVal+")");
		
		loaded=aVal;
	}	
	/**
	 *
	 */
	public boolean isLoaded ()
	{
		debug ("isLoaded ()");
		
		return (loaded);
	}	
	/**
	 *
	 */
	public int getNextIndex ()
	{
		debug ("getNextIndex ()");
		
		return (current+2);
	}	
	/**
	 *
	 */
	public void setCurrentIndex (int anIndex)
	{
		debug ("setCurrentIndex ("+anIndex+")");
		
		current=anIndex;
	}	
	/**
	 *
	 */
	public CTATProblem getNextProblem ()
	{				
		current++;
		
		debug ("getNextProblem ("+current+")");
		
		listProblemSet ();		
						
		try 
		{
			CTATProblem nextProblem=problems.get(current);
			
			debug("The next problem is: " + nextProblem.name);
			
			return (nextProblem);
		} 
		catch (IndexOutOfBoundsException e) 
		{
			return null;
		}
	}
	/**
	 *
	 */
	public int getNumProblems()
	{
		debug("getNumProblems ()");
		
		return problems.size();
	}
	/**
	 * Get a list of the problems in this set. This is a shallow copy -- the problems themselves are not cloned.
	 * @return list of problems. If problem set has not been loaded, returns empty list.
	 */
	public List<CTATProblem> getAllProblems()
	{
		List<CTATProblem> list;
		
		if(problems == null)
		{
			list = Collections.emptyList();
		}
		else
		{
			list = new ArrayList<CTATProblem>(problems.size());
			for(CTATProblem prob : problems)
			{
				list.add(prob);
			}
		}
		
		return list;
	}
	/**
	 *
	 */
	public boolean processNewProblemXML (Node aProblem)
	{
		debug ("processNewProblemXML ()");
			
		CTATProblem newProblem=new CTATProblem ();
		newProblem.problem_path = "FlashTutors/" + this.directory + "/";
		
		NamedNodeMap attr=aProblem.getAttributes();
		
		for (int i = 0 ; i<attr.getLength() ; i++) 
		{
	        Attr attribute = (Attr)attr.item(i);     
	        debug ("Attribute: " + attribute.getName()+" = "+attribute.getValue());
	        
	        if (attribute.getName ().equals("name"))
	           	newProblem.name=attribute.getValue();
	        
	        if (attribute.getName ().equals("tutor_flag"))
	           	newProblem.tutor_flag=attribute.getValue();
	        
	        if (attribute.getName ().equals("problem_file"))
	           	newProblem.problem_file=attribute.getValue();
	        
	        if (attribute.getName ().equals("description"))
	           	newProblem.description=attribute.getValue();
	        
	        if (attribute.getName ().equals("student_interface"))
	           	newProblem.student_interface=attribute.getValue();	        
		}
		
		NodeList children = aProblem.getChildNodes();
		NodeList skillNodes = null;
		
		for(int j = 0; j < children.getLength(); j++)
		{
			Node childNode = children.item(j);
			if(childNode.getNodeName().equals("Skills"))
			{
				skillNodes = childNode.getChildNodes(); // childNode is "Skills"; skillNodes is a list of nodes, each of which is a "Skill".
				break;
			}
		}
		
		// Process all the skills for this problem. The skills for the overall problem set need to already have been read from the XML.
		if(skillNodes != null)
		{
			for (int j=0;j<skillNodes.getLength();j++)
			{
				Node skillNode=skillNodes.item (j);

				if(skillNode.getNodeName().equals("Skill"))
				{
					NamedNodeMap skillAttributes = skillNode.getAttributes();

					Node skillNameNode = skillAttributes.getNamedItem("name");
					if(skillNameNode == null)
					{
						continue; // skill has no name attribute, so it can't be processed
					}

					String skillName = skillNameNode.getNodeValue();
					if(skillName == null)
					{
						continue;
					}

					// try to find a "skill of interest" (problem set skill) with a matching name; skills of interest should have already been loaded by loadProblemSet
					CTATSkill matchingSkill = null; // matching skill of interest
					for(CTATSkill skill : this.skills)
					{
						if(skill.name.equals(skillName))
						{
							matchingSkill = skill;
							break;
						}
					}
					if(matchingSkill == null)
					{
						continue; // no skill of interest was found with a matching name
					}

					// A matching skill of interest has been found; associate it with the new problem, with the number of "occurrences" as specified by the XML
					int occurrences;
					Node occurrencesNode = skillAttributes.getNamedItem("occurrences");
					if(occurrencesNode == null)
					{
						occurrences = 1; // "occurrences" was not specified in the XML; default to 1
					}
					else
					{
						try {
							occurrences = Integer.valueOf(occurrencesNode.getNodeValue());
						} catch (Exception e) {
							occurrences = 1; // default to 1
						}
					}

					newProblem.addSkill(matchingSkill, occurrences);
				}
			}		
		}
		
		newProblem.index=counter;
		problems.add(newProblem);
		counter++;
		
		return (true);
	}
	/**
	 *
	 */	
	public boolean loadProblemSet (String aFile)
	{
		debug ("loadProblemSet ("+aFile+")");
		
		if(loaded) 
		{
			debug ("Already loaded");
			return true; // If it's already loaded, don't load it again
		}
		
		// read problem_set.xml and get the directory, name, and description of the problem set
		if (readProblemSet(aFile) == false) 
		{
			debug ("Can't read problem set!");
			return false;
		}
		
		if (root==null)
		{
			debug ("Error: can't load: " + aFile);
			return (false);
		}
		
		if (root.getNodeName ().equals ("ProblemSet"))
		{	
			debug ("Found ProblemSet element");
						
			NamedNodeMap attr=root.getAttributes();
			
			for (int t=0;t<attr.getLength();t++) 
			{		        
		        Attr attribute = (Attr)attr.item(t);     
		        debug ("Attribute: " + attribute.getName()+" = "+attribute.getValue());				
				
		        if (attribute.getName ().equals("initial_sequence"))
		        {
		        	this.initialSequence=Integer.parseInt(attribute.getValue());
		        	debug ("Initial sequence: " + this.initialSequence);
		        }
			}    
			
			ArrayList<Node> problemsToProcess = new ArrayList<Node>(); // will hold the problems as they are found until they can be processed
			
			NodeList problemSet=root.getChildNodes ();
			
			for (int i=0;i<problemSet.getLength();i++)
			{
				Node node=problemSet.item (i);
				
				if (node.getNodeName ().equals ("Problems"))
				{
					debug ("Found Problems element");
					
					NodeList problems=node.getChildNodes ();
					
					for (int j=0;j<problems.getLength();j++)
					{
						Node problem=problems.item (j);
						
						if (problem.getNodeName ().equals ("Problem"))
							problemsToProcess.add(problem); // do not process the problem yet; the skills have to be read first
					}
				}
				
				if(node.getNodeName().equals("Skills"))
				{
					debug ("Found Skills element for problem set.");
					
					NodeList skillsList = node.getChildNodes(); // do not confuse skillsList with this.skills
					for(int j = 0; j < skillsList.getLength(); j++)
					{
						Node skillNode = skillsList.item(j);
						
						if(skillNode.getNodeName().equals("Skill"))
						{
							CTATSkill skill = new CTATSkill(skillNode);
							this.skills.add(skill);
						}
					}
				}
			}
			
			// process all the problems that were found
			for(Node problem : problemsToProcess)
			{
				processNewProblemXML (problem);
			}
		}
		
		loaded=true;
		
		return (true);
	}
	/**
	 *
	 */	
	public String buildMenuHTML ()
	{
		debug ("buildMenuHTML  ()");
		
		StringBuffer menu=new StringBuffer ();		
		String encoded="";
		
		menu.append("<ul>");
		
		for (int i=0;i<problems.size();i++)
		{
			CTATProblem problem=problems.get(i);
			menu.append ("<li><a href=\"http://127.0.0.1:"+CTATLink.wwwPort+"/gotoproblem.cgi?position="+problem.index+"\">"+problem.description+"</a></li>");
		}
		
		menu.append("</ul><br style=\"clear: left\" />");
		
		try 
		{
			encoded=URLEncoder.encode(menu.toString(),"UTF-8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			// UTF-8 should always be supported
			e.printStackTrace();
			return ("");
		}
		
		return (encoded);
	}	
	/**
	 *
	 */	
	public void setSkills(ArrayList <CTATSkill> skills) 
	{
		this.skills = skills;
	}
	/**
	 *
	 */	
	public ArrayList <CTATSkill> getSkills() 
	{
		return skills;
	}
	/**
	 *
	 */
	public boolean readProblemSet(String aFile) 
	{
		/* Read in a problem_set.xml and get the attributes (directory, name, description)
		 * without actually loading the problem set and the problems it contains. */
		
		debug ("readProblemSet ("+aFile+")");
		
		loaded = false; // the new problem_set.xml is not being loaded
		
		if (CTATLink.problemSetXMLsAreEncrypted)
		{
			debug ("Attempting load encrypted problem set ...");
			
			root=CTATLink.fManager.getContentsXMLEncrypted (aFile);
		}
		else
		{
			debug ("Attempting load non-encrypted problem set ...");
			
			root=CTATLink.fManager.getContentsXML (aFile);
		}
		
		if (root==null)
		{
			debug ("Error: can't load: " + aFile);
			return (false);
		}
		
		if (root.getNodeName ().equals ("ProblemSet"))
		{
			this.directory = (new File(aFile)).getParentFile().getName(); // get name of dir that contains XML
			/* Read name and description from the XML file */
			this.name = root.getAttribute("name");
			this.description = root.getAttribute("description");
			debug("Directory: " + directory + "; Name: " + name + "; Description: " + description);
		} 
		
		return true;
	}
	/**
	 *
	 */
	public String getDirectory() 
	{
		return directory;
	}
	/**
	 * @param directory new value for {@link #directory}
	 */
	void setDirectory(String directory) {
		this.directory = directory;
	}
	/**
	 *
	 */
	public String getName() 
	{
		return name;
	}
	/**
	 * @param name new value for {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 *
	 */
	public String getDescription() 
	{
		return description;
	}
	/**
	 * @param description new value for {@link #description}
	 */
	void setDescription(String description) {
		this.description = description;
	}
	/**
	 * Returns an XML string describing this problem set's skills
	 */
	public String getSkillsXML()
	{
		StringBuilder returnValue = new StringBuilder("<Skills>");
		
		for(CTATSkill skill : skills)
		{
			returnValue.append(skill.toString());
		}
		
		returnValue.append("</Skills>");
		
		return returnValue.toString();
	}
	/**
	 * @return {@link #getNumProblems()} less {@link #current}, the number of problems remaining
	 */
	public int countRemainingProblems() {

		return getNumProblems() - current - 1;  // -1 because current is an index, not a count
	}	
}
