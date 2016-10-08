/**
 ------------------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 $RCSfile$ 
 $Revision$ 
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

package edu.cmu.hcii.ctat;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class CTATMilestoneManager extends CTATBase implements Serializable 
{	
	private static final long serialVersionUID = 7061551116978267821L;
	
	private ArrayList <CTATMilestone> milestones=null;
	
	/**
	 * 
	 */
	public CTATMilestoneManager ()
	{
    	setClassName ("CTATMilestoneManager");
    	debug ("CTATMilestoneManager ()");
    	
    	milestones=new ArrayList<CTATMilestone> ();
	}
	/**
	 * 
	 */
	public void initialize ()
	{
		debug ("initialize ()");
			
		CTATMilestone milestone=null;
		
		boolean success = readMilestoneXML(new File(CTATLink.etc + System.getProperty("file.separator") + "milestones.xml"));
		if(!success) {
			debug("Could not read etc/milestones.xml. Using default hard-coded milestones.");
			
			//milestone=addMilestone ("Scale to the Chief",CTATMilestone.TYPESKILL,"RIM-PROPDISTSCALE-FIND-DIST","Master the skill 'Find distance using scale factor' in Proportional Distance","basics.png");
			milestone=addMilestone ("Scale to the Chief",CTATMilestone.TYPESKILL,"Master the skill 'Find distance using scale factor' in Proportional Distance","find-dist-using-sf","basics.png");
			milestone.setTest("skill:rim-nxt-propdist-find-dist-using-sf");

			//milestone=addMilestone ("Going the Distance",CTATMilestone.TYPESKILL,"RIM-PROPDISTRATE-FIND-DIST","Master the skill 'Find distance using scale factor' in Proportional Distance","basics.png");
			milestone=addMilestone ("Going the Distance",CTATMilestone.TYPESKILL,"Master the skill 'Find distance using unit rate' in Proportional Distance","find-dist-using-ur","basics.png");
			milestone.setTest("skill:rim-nxt-propdist-find-dist-using-ur");

			//milestone=addMilestone ("You've Got the Power",CTATMilestone.TYPESKILL,"RIM-PROPSPEEDSCALE-FIND-POWER","Master the skill 'Find distance using scale factor' in Proportional Distance","basics.png");
			milestone=addMilestone ("You've Got the Power",CTATMilestone.TYPESKILL,"Master the skill 'Find power using scale factor' in Proportional Speed","find-power-using-sf","basics.png");
			milestone.setTest("skill:rim-nxt-propspeed-find-power-using-sf");

			//milestone=addMilestone ("Powered Up",CTATMilestone.TYPESKILL,"RIM-PROPSPEEDRATE-FIND-POWER","Master the skill 'Find distance using scale factor' in Proportional Distance","basics.png");
			milestone=addMilestone ("Powered Up",CTATMilestone.TYPESKILL,"Master the skill 'Find power using unit rate' in Proportional Speed","find-power-using-ur","basics.png");
			milestone.setTest("skill:rim-nxt-propspeed-find-power-using-ur");

			milestone=addMilestone ("NXT Basic Training",CTATMilestone.TYPEPSET,"RIM-BASICS-OPERATION","Master the skill 'Find distance using scale factor' in Proportional Distance","traning_op_MP.png");
			milestone.setTest("assignment_completed:nxt_operation");

			milestone=addMilestone ("NXT Move Block Programmer",CTATMilestone.TYPESKILL,"Complete the 'NXT Move Block' Basics lesson","RIM-BASICS-MOVE","traning_move_MP.png");
			milestone.setTest("assignment_completed:nxt_move_block");

			milestone=addMilestone ("NXTrained and Ready",CTATMilestone.TYPEMETA,"Complete both Basics lesson sequences","RIM-BASICS-COMPLETE","medium_basics.png");
			milestone.setTest("meta:RIM-BASICS-OPERATION:and:RIM-BASICS-MOVE");

			milestone=addMilestone ("To Ruler Them All",CTATMilestone.TYPESKILL,"Complete the 'Measuring Distance' lesson","RIM-MEASDIST-COMPLETE","measdist_mp.png");
			milestone.setTest("assignment_completed:measuring_distance");

			milestone=addMilestone ("Turn Measurer Minor",CTATMilestone.TYPESKILL,"Complete the 'Small Turns' portion of 'Measuring Turns'","RIM-MEASTURN-SMALLTURN","basics.png");
			milestone.setTest("problemset_completed:measturn2");

			milestone=addMilestone ("Turn Measurer Major",CTATMilestone.TYPESKILL,"Complete the 'Measuring Turns', including both small and large turns","RIM-MEASTURN-COMPLETE","measturn_mp.png");
			milestone.setTest("assignment_completed:measuring_turns");

			milestone=addMilestone ("Distance Over Time",CTATMilestone.TYPESKILL,"Complete the 'What is Speed?' portion of 'Measuring Speed'","RIM-MEASSPEED-WHATISSPEED","basics.png");
			milestone.setTest("problemset_completed:measspeed2");

			milestone=addMilestone ("Speed Limitless",CTATMilestone.TYPESKILL,"Complete the 'Measuring Speed' lesson","RIM-MEASSPEED-COMPLETE","measspeed_mp.png");
			milestone.setTest("assignment_completed:measuring_speed");

			milestone=addMilestone ("Measurably Accomplished",CTATMilestone.TYPEMETA,"Complete both main Measurement lessons","RIM-MEASUREMENT-COMPLETE","medium_meas.png");
			milestone.setTest("meta:RIM-MEASDIST-COMPLETE:and:RIM-MEASTURN-COMPLETE");

			milestone=addMilestone ("The nth Degree",CTATMilestone.TYPESKILL,"Master the skill 'Find angle using unit rate'","RIM-PROPTURNRATE-FIND-ANGLE","basics.png");
			milestone.setTest("skill:rim-nxt-propturn-find-angle-using-ur");

			milestone=addMilestone ("Scaling Spinning",CTATMilestone.TYPESKILL,"Master the skill 'Find angle using scale factor'","RIM-PROPTURNSCALE-FIND-ANGLE","basics.png");
			milestone.setTest("skill:rim-nxt-propturn-find-angle-using-sf");

			milestone=addMilestone ("To Boldly Roll",CTATMilestone.TYPESKILL,"Master the skill 'Find rotations using unit rate' in Proportional Distance","RIM-PROPDISTRATE-FIND-ROTS","basics.png"); // propdist-scale
			milestone.setTest("skill:rim-nxt-propdist-find-rots-using-ur");

			milestone=addMilestone ("Roll with the Times",CTATMilestone.TYPESKILL,"Master the skill 'Find rotations using scale factor' in Proportional Distance","RIM-PROPDISTSCALE-FIND-ROTS","basics.png");
			milestone.setTest("skill:rim-nxt-propdist-find-rots-using-sf");

			milestone=addMilestone ("One Good Turn Deserves Another",CTATMilestone.TYPESKILL,"Master the skill 'Find rotations using unit rate' in Proportional Turns","RIM-PROPTURNRATE-FIND-ROTS","basics.png");
			milestone.setTest("skill:rim-nxt-propturn-find-rots-using-ur");

			milestone=addMilestone ("A Turn for the Better",CTATMilestone.TYPESKILL,"Master the skill 'Find rotations using scale factor' in Proportional Turns","RIM-PROPTURNSCALE-FIND-ROTS","basics.png");
			milestone.setTest("skill:rim-nxt-propturn-find-rots-using-sf");

			milestone=addMilestone ("Ratio of Rate",CTATMilestone.TYPESKILL,"Master the skill 'Find speed using unit rate' in Proportional Speed","RIM-PROPSPEEDRATE-FIND-SPEED","basics.png");
			milestone.setTest("skill:rim-nxt-propspeed-find-speed-using-ur");

			milestone=addMilestone ("Fast Times",CTATMilestone.TYPESKILL,"Master the skill 'Find speed using scale factor' in Proportional Speed","RIM-PROPSPEEDSCALE-FIND-SPEED","basics.png");
			milestone.setTest("skill:rim-nxt-propspeed-find-speed-using-sf");

			milestone=addMilestone ("Proportional Distance: Rate",CTATMilestone.TYPESKILL,"Complete the 'Proportional Distance Rate' lesson","RIM-PROPDIST-RATE","ur_propdist.png");
			milestone.setTest("assignment_completed:proportional_distance_rate");

			milestone=addMilestone ("Proportional Distance: Scale",CTATMilestone.TYPESKILL,"Complete the 'Proportional Distance Scale' lesson","RIM-PROPDIST-SCALE","sf_propdist.png");
			milestone.setTest("assignment_completed:proportional_distance_scale");

			milestone=addMilestone ("Proportional Turns: Rate",CTATMilestone.TYPESKILL,"Complete the 'Proportional Turns Rate' lesson","RIM-PROPTURN-RATE","ur_propturn.png");
			milestone.setTest("assignment_completed:proportional_turns_rate");

			milestone=addMilestone ("Proportional Turns: Scale",CTATMilestone.TYPESKILL,"Complete the 'Proportional Turns Scale' lesson","RIM-PROPTURN-SCALE","sf_propturn.png");
			milestone.setTest("assignment_completed:proportional_turns_scale");

			milestone=addMilestone ("Proportional Speed: Rate",CTATMilestone.TYPESKILL,"Complete the 'Proportional Speed Rate' lesson","RIM-PROPSPEED-RATE","ur_propspeed.png");
			milestone.setTest("assignment_completed:proportional_speed_rate");
			//milestone.setState(CTATMilestone.ACCOMPLISHED);

			milestone=addMilestone ("Proportional Speed: Scale",CTATMilestone.TYPESKILL,"Complete the 'Proportional Speed Scale' lesson","RIM-PROPSPEED-SCALE","sf_propspeed.png");
			milestone.setTest("assignment_completed:proportional_speed_scale");
			//milestone.setState(CTATMilestone.ACCOMPLISHED);

			milestone=addMilestone ("Distance Relationship",CTATMilestone.TYPEMETA,"Complete one investigation into the proportional relationship between Motor Rotations and Distance","RIM-PROPDIST-SINGLE","propdist_1invest.png");
			milestone.setTest("meta:RIM-PROPDIST-RATE:or:RIM-PROPDIST-SCALE");

			milestone=addMilestone ("Distance Mastery",CTATMilestone.TYPEMETA,"Complete both investigations into the proportional relationship between Motor Rotations and Distance","RIM-PROPDIST-BOTH","propdist_2invest.png");
			milestone.setTest("meta:RIM-PROPDIST-RATE:and:RIM-PROPDIST-SCALE");

			milestone=addMilestone ("Turn, Turn, Turn",CTATMilestone.TYPEMETA,"Complete one investigation into the proportional relationship between Motor Rotations and Turn Angle","RIM-PROPTURN-SINGLE","propturn_1invest.png");
			milestone.setTest("meta:RIM-PROPTURN-RATE:or:RIM-PROPTURN-SCALE");

			milestone=addMilestone ("Turn Mastery",CTATMilestone.TYPEMETA,"Complete both investigations into the proportional relationship between Motor Rotations and Turn Angle","RIM-PROPTURN-BOTH","propturn_2invest.png");
			milestone.setTest("meta:RIM-PROPTURN-RATE:and:RIM-PROPTURN-SCALE");

			milestone=addMilestone ("Speed Control",CTATMilestone.TYPEMETA,"Complete one investigation into the proportional relationship between Motor Power and Speed","RIM-PROPSPEED-SINGLE","propspeed_1invest.png");
			milestone.setTest("meta:RIM-PROPSPEED-RATE:or:RIM-PROPSPEED-SCALE");

			milestone=addMilestone ("Speed Mastery",CTATMilestone.TYPEMETA,"Complete both investigations into the proportional relationship between Motor Power and Speed","RIM-PROPSPEED-BOTH","propspeed_2invest.png");
			milestone.setTest("meta:RIM-PROPSPEED-RATE:and:RIM-PROPSPEED-SCALE");

			milestone=addMilestone ("Highly Rated",CTATMilestone.TYPEMETA,"Complete all three investigations using Unit Rate","RIM-PROP-3RATE","ur_all.png");
			milestone.setTest("meta:RIM-PROPDIST-RATE:and:RIM-PROPTURN-RATE:and:RIM-PROPSPEED-RATE");

			milestone=addMilestone ("Large Scale",CTATMilestone.TYPEMETA,"Complete all three investigations using Scale Factor","RIM-PROP-3SCALE","sf_all.png");
			milestone.setTest("meta:RIM-PROPDIST-SCALE:and:RIM-PROPTURN-SCALE:and:RIM-PROPSPEED-SCALE");
			//milestone.setState(CTATMilestone.ACCOMPLISHED);

			milestone=addMilestone ("Best of Both Worlds",CTATMilestone.TYPEMETA,"Complete both main investigations using both Unit Rate and Scale Factor","RIM-PROP-FULL-MAIN","prop_everything1.png");
			milestone.setTest("meta:RIM-PROPDIST-RATE:and:RIM-PROPDIST-SCALE:and:RIM-PROPTURN-RATE:and:RIM-PROPTURN-SCALE");
			//milestone.setState(CTATMilestone.ACCOMPLISHED);

			milestone=addMilestone ("Proportionally Prepared",CTATMilestone.TYPEMETA,"Complete both main investigations using both Unit Rate and Scale Factor","RIM-PROPORTIONALITY-COMPLETE","medium_prop.png");
			milestone.setTest("meta:RIM-PROPDIST-SINGLE:and:RIM-PROPTURN-SINGLE");
			//milestone.setState(CTATMilestone.ACCOMPLISHED);

			milestone=addMilestone ("Robot Algebra",CTATMilestone.TYPEMETA,"Complete Robots in Motion, demonstrating mastery of measurement and proportional reasoning concepts, and their application in robotics programming","RIM-COMPLETE","large_rem.png");
			milestone.setTest("meta:RIM-MEASUREMENT-COMPLETE:and:RIM-PROPORTIONALITY-COMPLETE");
			//milestone.setState(CTATMilestone.ACCOMPLISHED);
		}
		//processMilestones ();
		
		checkMetaMilestones ();
	}
	/**
	 * Reads in the milestones from an XML file.
	 * The XML should be of this format:
	 * <milestones>
	 * 	<milestone>
	 * 		<name>NAME</name>
	 * 		<type>TYPE</type>
	 * 		<description>DESCRIPTION</description>
	 * 		<slug>SLUG</slug>
	 * 		<image>IMAGE</image>
	 * 		<test>TEST</test>
	 * 		<assignment>ASSIGNMENT</assignment>
	 * 	</milestone>
	 * 	<milestone>
	 * 		...
	 * 	</milestone>
	 * 	.
	 * 	.
	 * 	.
	 * </milestones>
	 * @param f milestones.xml file
	 * @return success/failure
	 */
	public boolean readMilestoneXML(File f) {
		debug("readMilestoneXML("+f+")");
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
			
			Node root = doc.getDocumentElement();
			if (!root.getNodeName().equalsIgnoreCase("milestones"))
			{
				return false;
			}
			
			NodeList docChildren = root.getChildNodes();
			int numDocChildren = docChildren.getLength();
			for(int i = 0; i < numDocChildren; i++) {
				Node docChild = docChildren.item(i);
				if(docChild.getNodeType() == Node.ELEMENT_NODE && docChild.getNodeName().equalsIgnoreCase("milestone")) {
					// process this milestone
					CTATMilestone milestone = new CTATMilestone();
					
					NodeList milestoneAttributes = docChild.getChildNodes();
					int numMilestoneAttributes = milestoneAttributes.getLength();
					for(int j = 0; j < numMilestoneAttributes; j++) {
						Node milestoneAttribute = milestoneAttributes.item(j);
						if(milestoneAttribute.getNodeType() == Node.ELEMENT_NODE) {
							if(milestoneAttribute.getNodeName().equalsIgnoreCase("name")) {
								milestone.setName(milestoneAttribute.getFirstChild().getNodeValue());
							}
							else if(milestoneAttribute.getNodeName().equalsIgnoreCase("type")) {
								String typeString = milestoneAttribute.getFirstChild().getNodeValue();
								if("unset".equalsIgnoreCase(typeString)) {
									milestone.setType(CTATMilestone.TYPEUNSET);
								}
								else if("time".equalsIgnoreCase(typeString)) {
									milestone.setType(CTATMilestone.TYPETIME);
								}
								else if("skill".equalsIgnoreCase(typeString)) {
									milestone.setType(CTATMilestone.TYPESKILL);
								}
								else if("meta".equalsIgnoreCase(typeString)) {
									milestone.setType(CTATMilestone.TYPEMETA);
								}
								else if("pset".equalsIgnoreCase(typeString)) {
									milestone.setType(CTATMilestone.TYPEPSET);
								}
								else if("assn".equalsIgnoreCase(typeString)) {
									milestone.setType(CTATMilestone.TYPEASSN);
								}
							}
							else if(milestoneAttribute.getNodeName().equalsIgnoreCase("description")) {
								milestone.setDescription(milestoneAttribute.getFirstChild().getNodeValue());
							}
							else if(milestoneAttribute.getNodeName().equalsIgnoreCase("slug")) {
								milestone.setSlug(milestoneAttribute.getFirstChild().getNodeValue());
							}
							else if(milestoneAttribute.getNodeName().equalsIgnoreCase("image")) {
								milestone.setImage(milestoneAttribute.getFirstChild().getNodeValue());
							}
							else if(milestoneAttribute.getNodeName().equalsIgnoreCase("test")) {
								milestone.setTest(milestoneAttribute.getFirstChild().getNodeValue());
							}
							else if(milestoneAttribute.getNodeName().equalsIgnoreCase("assignment")) {
								milestone.setAssignment(milestoneAttribute.getFirstChild().getNodeValue());
							}
						}
					}
					
					milestones.add(milestone);
				}
			}
			
			debug(milestones.toString());
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	/**
	 *
	 */
	public Boolean isEmpty ()
	{
		if (milestones.size()==0)
			return (true);
		
		return (false);
	}
	/**
	 * 
	 */
	public ArrayList<CTATMilestone> getMilestones ()
	{
		return (milestones);
	}
	/**
	 * 
	 */
	public CTATMilestone getMilestone (String aName)
	{
		for (int i=0;i<milestones.size();i++)
		{
			CTATMilestone test=milestones.get(i);
			
			if (test.getSlug().equalsIgnoreCase(aName)==true)
				return (test);
		}
		
		return (null);
	}
	/**
	 * Returns ALL milestones with slug equal to aName
	 */
	public ArrayList<CTATMilestone> getMilestonesWithName(String aName)
	{
		ArrayList<CTATMilestone> list = new ArrayList<CTATMilestone>();
		
		for (int i=0;i<milestones.size();i++)
		{
			CTATMilestone test=milestones.get(i);
			
			if (test.getSlug().equalsIgnoreCase(aName)==true)
				list.add(test);
		}
		
		return list;
	}
	/**
	 * 
	 */
	public CTATMilestone addMilestone (String aName,
									   int aType,
									   String aDescription,
									   String aSlug,
									   String anImage)
	{
		debug ("addMilestone ("+aName+")");
		
		CTATMilestone tester=getMilestone (aName);
		
		if (tester==null)
		{
			tester=new CTATMilestone ();
			tester.setName(aName);
			tester.setSlug(aSlug);
			tester.setType(aType);
			tester.setDescription(aDescription);
			tester.setImage(anImage);
			
			milestones.add(tester);
		}
		
		return (tester);
	}
	/**
	 * 
	 */
	public CTATMilestone addMilestone (String aName,
									   int aType,
									   String aDescription,
									   String aSlug)
	{
		debug ("addMilestone ("+aName+")");
		
		CTATMilestone tester=getMilestone (aSlug);
		
		if (tester==null)
		{
			tester=new CTATMilestone ();
			tester.setName(aName);
			tester.setSlug(aSlug);
			tester.setType(aType);
			tester.setDescription(aDescription);
			tester.setImage(aSlug);
		}
		
		return (tester);
	}
	
	/**
	 * 
	 */
	public CTATMilestone addMilesone(String aName,
									   int aType,
									   String aDescription,
									   String aSlug,
									   String anImage,
									   String assignment)
	{
		CTATMilestone m = addMilestone(aName, aType, aDescription, aSlug, anImage);
		m.setAssignment(assignment);
		return m;
	}
	/**
	 * Mark an assignment milestone accomplished.
	 */
	public ArrayList <CTATMilestone> checkAssignmentMilestone (String anAssignment)
	{
		debug ("checkAssignmentMilestone ("+anAssignment+")");
		
		anAssignment = anAssignment.replace(" ", "_"); // the milestone test conditions use underscores, not spaces.
		
		for (int j=0;j<milestones.size();j++)
		{
			CTATMilestone milestone=milestones.get(j);
			
			//debug ("Checking milestone: " + milestone.getSlug() + " has state: " + milestone.getState());
			
			if (milestone.getType()==CTATMilestone.TYPEASSN)
			{
				debug ("Checking problem set milestone ...");
				
				if (milestone.getState()==CTATMilestone.WAITING)
				{
					debug ("Found a waiting problem set milestone");
					
					if (milestone.getTest().equalsIgnoreCase(anAssignment)==true)
					{
						debug ("Matched a milestone to the problem set, marking accomplished");
						
						milestone.setState(CTATMilestone.ACCOMPLISHED);
					}
				}
			}
		}
		
		checkMetaMilestones ();

		return (null);
	}
	/**
	 * Mark a problem set milestone accomplished.
	 */
	public ArrayList <CTATMilestone> checkProblemSetMilestone (String aProblemSet)
	{
		debug ("checkProblemSetMilestone ("+aProblemSet+")");
		
		for (int j=0;j<milestones.size();j++)
		{
			CTATMilestone milestone=milestones.get(j);
			
			//debug ("Checking milestone: " + milestone.getSlug() + " has state: " + milestone.getState());
			
			if (milestone.getType()==CTATMilestone.TYPEPSET)
			{
				debug ("Checking problem set milestone ...");
				
				if (milestone.getState()==CTATMilestone.WAITING)
				{
					debug ("Found a waiting problem set milestone");
					
					if (milestone.getTest().equalsIgnoreCase(aProblemSet)==true)
					{
						debug ("Matched a milestone to the problem set, marking accomplished");
						
						milestone.setState(CTATMilestone.ACCOMPLISHED);
					}
				}
			}	
		}
		
		checkMetaMilestones ();
		
		return (null);
	}	
	/**
	 * Note that the current assignment must be specified to disambiguate similarly-named skills in different assignments.
	 * If currentAssignment is null, the first skill-based milestone found with the corresponding skill name will be checked.
	 */
	public ArrayList <CTATMilestone> checkSkillMilestone (String aSkillname, String currentAssignment)
	{
		debug ("checkSkillMilestone ("+aSkillname+", "+currentAssignment+")");
		
		//CTATMilestone aMilestone=getMilestone (aSkillname);
		ArrayList<CTATMilestone> milestones = getMilestonesWithName(aSkillname);
		
		for(CTATMilestone aMilestone : milestones)
		{
			if (aMilestone!=null && (aMilestone.getType()==CTATMilestone.TYPESKILL))
			{
				String milestoneAssignment = aMilestone.getAssignment();
				if(currentAssignment == null || milestoneAssignment == null || currentAssignment.equals(milestoneAssignment))
				{
					if ((aMilestone.getState()==CTATMilestone.WAITING))
					{
							aMilestone.setState (CTATMilestone.ACCOMPLISHED);
					}
					break;
				}
			}
		}
		
		checkMetaMilestones ();
		
		return (null);
	}
	/**
	 * 
	 */
	public ArrayList <CTATMilestone> checkTimeMilestone (long aTime)
	{
		debug ("checkTimeMilestone ("+aTime+")");
		
		checkMetaMilestones ();
		
		return (null);		
	}
	/**
	 * 
	 */
	public ArrayList <CTATMilestone> checkMetaMilestones ()
	{
		debug ("checkMetaMilestones ()");
		
		boolean somethingChanged;
		
		do
		{
			somethingChanged = false;
			for (int i=0;i<milestones.size();i++)
			{
				CTATMilestone milestone=milestones.get(i);

				if (milestone.getType()==CTATMilestone.TYPEMETA)
				{
					//debug ("Checking meta milestone: " + milestone.getSlug());

					Boolean valid=true;

					ArrayList <String> targets=milestone.getChildren();

					//>-------------------------------------------------------

					if (milestone.getMetaType()==CTATMilestone.METAOR)
					{
						valid=false;

						for (int j=0;j<targets.size();j++)
						{
							CTATMilestone tester=getMilestone (targets.get(j));

							if (tester!=null)
							{
								if (tester.getState()==CTATMilestone.ACCOMPLISHED)
								{
									// Since it's an OR we only need one to match for the whole
									// meta to be accomplished

									valid=true;
								}	
							}						
						}
					}

					//>-------------------------------------------------------				

					if (milestone.getMetaType()==CTATMilestone.METAAND)
					{
						valid=true;

						for (int k=0;k<targets.size();k++)
						{
							CTATMilestone tester=getMilestone (targets.get(k));

							if (tester!=null)
							{
								if (tester.getState()!=CTATMilestone.ACCOMPLISHED)
								{
									// Since it's an AND we know we've already failed

									valid=false;
								}
							}	
						}
					}

					//>-------------------------------------------------------

					if (valid==true)
					{
						// This milestone checks out

						debug ("Found an accomplished meta milestone: " + milestone.getSlug());
						
						if(milestone.getState() != CTATMilestone.ACCOMPLISHED)
						{
							milestone.setState(CTATMilestone.ACCOMPLISHED);
							somethingChanged = true;
						}
					}
				}
			}
		} while(somethingChanged);
		
		return (null);		
	}
	/**
	 * NOTE: Returns null if there are no milestones. This is so the caller can easily determine whether or not to use the data-milestones tag.
	 */
	public String generateMilestoneHTML ()
	{
		debug ("generateMilestoneHTML ("+milestones.size()+")");
		
		StringBuffer formatter=new StringBuffer ();
		
		int total=0;
		
		for (int j=0;j<milestones.size();j++)
		{
			CTATMilestone milestone=milestones.get(j);
			
			//debug ("Checking milestone: " + milestone.getSlug() + " has state: " + milestone.getState());
			
			if (milestone.getState()==CTATMilestone.ACCOMPLISHED)
			{
				total++;
			}
		}
		
		if(total == 0)
		{
			return null; // IMPORTANT!
		}
		
		int counter=0;
				
		for (int i=0;i<milestones.size();i++)
		{
			CTATMilestone milestone=milestones.get(i);
			
			//debug ("Checking milestone: " + milestone.getSlug() + " has state: " + milestone.getState());
			
			if (milestone.getState()==CTATMilestone.ACCOMPLISHED)
			{
				formatter.append("<div class=\"milestone\">");
				
					formatter.append("<div class=\"milestone-image\"><img alt=\""+milestone.getSlug()+"\" src=\"http://localhost:8080/images/badges/"+milestone.getImage()+"\" /></div>");
				
					formatter.append("<div class=\"milestone-count\">");
					formatter.append("  Achievement Earned ("+(counter+1)+"/"+total+")");
					formatter.append("</div>");
				
					formatter.append("<div class=\"milestone-text\">");
					
						formatter.append("<div class=\"milestone-name\">");
						formatter.append(milestone.getName());
						formatter.append("</div>");
						
						formatter.append("<div class=\"milestone-description\">");
						formatter.append(milestone.getDescription());
						formatter.append("</div>");
				    
					formatter.append("</div>");
				
					formatter.append("<div class=\"milestone-continue\">");
					
					if ((counter+1)<total)
						formatter.append("<a href=\"#\" data-close=\"false\"><img alt=\"Continue\" src=\"http://localhost:8080/images/badges/continue-button.png\" /></a>");
					else
						formatter.append("<a href=\"#\" data-close=\"true\"><img alt=\"Close\" src=\"http://localhost:8080/images/badges/continue-button.png\" /></a>");
					
					formatter.append("</div>");
				
				formatter.append("</div>");
				
				milestone.setState(CTATMilestone.SHOWN);
				
				counter++;
			}
		}
		
		debug (formatter.toString());
				
		return (formatter.toString());
	}	
	/**
	 * Mark all milestones that have been accomplished as "shown".
	 */
	public void markShown()
	{
		debug("markShown()");
		for(CTATMilestone milestone : milestones)
		{
			if (milestone.getState()==CTATMilestone.ACCOMPLISHED)
			{
				milestone.setState(CTATMilestone.SHOWN);
			}
		}
	}
	/**
	 * 
	 */
	public String generateMilestoneXML ()
	{
		debug ("generateMilestoneXML ()");
		
		StringBuffer formatter=new StringBuffer ();
		
		formatter.append ("<?xml version='1.0' encoding=\"UTF-8\" ?>");
		formatter.append("<milestones>");
						
		for (int i=0;i<milestones.size();i++)
		{			
			CTATMilestone milestone=milestones.get(i);
			
			debug ("Checking milestone: " + milestone.getSlug() + " has state: " + milestone.getState());
						
			if (milestone.getState()==CTATMilestone.ACCOMPLISHED)
			{
				formatter.append("<milestone>");
				
				formatter.append("<name>"+milestone.getName()+"</name>");
				formatter.append("<slug>"+milestone.getSlug()+"</slug>");
				formatter.append("<description>"+milestone.getDescription()+"</description>");
				formatter.append("<image>https://s3.amazonaws.com/cs2n-content-files-dev/milestones/traning_op_MP_original-7852dccaf650e3be3724fa599ab71dae.png?1346358835</image>");
				formatter.append("<achieved>1</achieved>");
				
				formatter.append("</milestone>");
				
				milestone.setState(CTATMilestone.SHOWN);
			}
		}
		
		formatter.append("</milestones>");
		
		return (formatter.toString());
	}
	/**
	 * 
	 */
	/*
	public void processMilestones ()
	{
		debug ("processMilestones ("+milestones.size()+")");
						
		for (int i=0;i<milestones.size();i++)
		{
			CTATMilestone milestone=milestones.get(i);
			
			if (milestone.getType()==CTATMilestone.TYPEMETA)
			{
				debug ("Setting up milestone graph for: " + milestone.getSlug());
				
				
			}
		}	
	}
	*/
	public String getDebugString()
	{
		StringBuilder sb = new StringBuilder();
		for(CTATMilestone milestone : milestones) {
			sb.append(milestone.getName() + ": " + milestone.getDescription() + "\n");
		}
		return sb.toString();
	}
}
