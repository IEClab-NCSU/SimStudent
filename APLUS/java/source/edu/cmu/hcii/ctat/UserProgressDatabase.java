package edu.cmu.hcii.ctat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.ctat.model.Skill;
import edu.cmu.pact.ctat.model.Skills;

/**
 * Keeps a database of users and how far they have gone into each assignment. For each user 
 * and assignment, remember the next problem to be completed by recording the problem set 
 * and position within that problem set. This can be used by the offline mode of Local 
 * Tutor Shop to decide which problems to run.
 */
public class UserProgressDatabase extends CTATBase
{					
	private Boolean memOnly=false;
	
	// Map from user ID to map from assignment name to info for this user-assignment pair
	// private Map<String, Map<String, UserAssignmentInfo>> table;  	
	private Map<String,CTATUserData> table=null;
	
	// Change this to a location we know we're allowed to write to
	// private String userprogressPath = CTATLink.htdocs + "/userprogress/";
	private String userprogressPath = "etc/userprogress/";
	
	// Keep track of current (i.e. last updated in database) assignment for each user to ensure proper milestone-checking.
	// This information does not need to persist across sessions.
	// Map from user ID to assingment name.
	private Map<String, String> currentAssignments = new HashMap<String, String>();
		
	/** 
	 * @throws IOException
	 */
	public UserProgressDatabase(Boolean aMemOnly) throws IOException
	{
    	setClassName ("UserProgressDatabase");
    	debug ("UserProgressDatabase ("+aMemOnly+")");
    	
    	this.memOnly=aMemOnly;
    	
    	if (this.memOnly==false)
    		initializeUserProgress();
    	else
    	{
    		table = new HashMap<String, CTATUserData>();
    		
    		table.put(CTATLink.userID, castTableEntry(new CTATUserData ()));
    	}
	}	
	/** 
	 * @throws IOException
	 */
	public UserProgressDatabase() throws IOException
	{
    	setClassName ("UserProgressDatabase");
    	debug ("UserProgressDatabase ()");
    	
		// assume default directory is okay in no-arg constructor
		initializeUserProgress();
	}
	/** 
	 * @param path
	 * @throws IOException
	 */
	public UserProgressDatabase(String path) throws IOException
	{
    	setClassName ("UserProgressDatabase");
    	debug ("UserProgressDatabase ()");
    	
		userprogressPath = path;
		
		(new File(userprogressPath)).mkdirs(); // create the directory, and all its parents, if it does not yet exist
		
		initializeUserProgress();
	}
	/**
	 * Read from disk the progress (current position within each assignment) for every user.
	 * @throws IOException
	 */
	private synchronized void initializeUserProgress()
	{
		debug ("initializeUserProgress ()");
		
		if (memOnly==true)
		{
			debug ("We're in memOnly mode, aborting ...");
			return;
		}
		
		CTATDiagnostics diags=new CTATDiagnostics ();
		if (diags.getWritableDirectory ()==false)
		{
			debug ("Error: unable to obtain a list of writable directories!");
			return;
		}
		
		ArrayList <String> writableDirs=diags.getWritables();
		
		String finalUserprogressPath = writableDirs.get(0)+"/.ctat/userprogress/";
		
		debug ("Set user db directory to: " + finalUserprogressPath);
						
		//if(userprogressDir.mkdirs())
		userprogressPath = finalUserprogressPath;
		
		File userprogressDir = new File(userprogressPath);
		
		if (userprogressDir.exists()==false)
		{
			debug ("User progress directory doesn't exist yet, creating ...");
			
			userprogressDir.mkdirs();
		}
		
		//table = new HashMap<String, Map<String, UserAssignmentInfo>>();
		table = new HashMap<String, CTATUserData>();
		
		File[] subfiles = userprogressDir.listFiles();
		
		if (subfiles==null)
		{
			debug ("No files found!");
			return;
		}
		
		for(File f : subfiles)
		{
			debug ("Loading potential user data file: " + f.getName());
			
			String name = f.getName();
			
			if(name.endsWith(".data"))
			{
				String userID = name.substring(0, name.length() - ".data".length());
				
				debug ("Determined username to be: " + userID);
				
				try
				{
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));

					CTATUserData streamedObject = (CTATUserData) ois.readObject();
					
					try
					{						
						table.put(userID, castTableEntry(streamedObject));
					} 
					catch(ClassCastException e) 
					{ 						
						debug ("Error: the class stored in " + name + " is not what is expected to be loaded");
					}
					
					ois.close();
					
					// Check and initialize if needed ...
					
					CTATMilestoneManager manager=streamedObject.getMilestoneManager();
					
					if (manager!=null)
					{
						if (manager.isEmpty()==true)
						{
							// Create the milestone structure, but only once

							manager.initialize();
						}
					}
					else
					{
						debug ("Error: user info doesn't contain a milestone manager");
					}
				}
				catch(IOException e) { }
				catch(ClassNotFoundException e) { }
			}
			else
				debug ("File " + name + " does not end in .data");
		}
		
		debug ("Loaded " + table.size() + " users");
	}	
	/**
	 * 
	 */
	public CTATUserData getUser (String anID)
	{
		return (table.get(anID.toLowerCase()));
	}
	/**
	 * 
	 */
	public CTATUserData addUser (String anID)
	{
		debug ("addUser ("+anID+")");
		
		CTATUserData aUser=new CTATUserData ();
		
		aUser.username=anID.toLowerCase();
		
		CTATMilestoneManager manager=aUser.getMilestoneManager();
		
		if (manager!=null)
		{
			manager.initialize();
		}
		
		//aUser.createAssignmentInfo ();
		
		table.put(anID, aUser);
		
		return (aUser);
	}
	/** 
	 * @param o
	 * @return
	 */
	//@SuppressWarnings("unchecked")
	private CTATUserData castTableEntry(Object serializedObject)
	{
		return (CTATUserData) serializedObject;
	}		
	/**
	 * 
	 */
	public Boolean getMemOnly() 
	{
		return memOnly;
	}
	/**
	 * 
	 */
	public void setMemOnly(Boolean memOnly) 
	{
		this.memOnly = memOnly;
	}				
	/**
	 * Sets the current position in an assignment for the specified user.
	 * The String parameters may not be null.
	 * @param userID
	 * @param assignmentName
	 * @param problemSetName
	 * @param position (within problem set) of the next problem to be completed
	 * @param writeToDisk true if you want to immediately write the change to disk
	 * @throws IOException if the write to disk fails
	 */
	public synchronized void setCurrentProblem (String userID, 
												String assignmentName, 
												String problemSetName, 
												int position, 
												boolean writeToDisk) throws IOException
	{
		debug ("setCurrentProblem ()");
		
		if(userID == null || assignmentName == null || problemSetName == null)
		{
			throw new IllegalArgumentException("UserProgressDatabase.setCurrentProblem does not accept null arguments");
		}
		
		currentAssignments.put(userID, assignmentName); // mark this assignment as current for this user
		
		UserAssignmentInfo uai = getUserAssignmentInfo(userID, assignmentName, true);
		if(uai == null)
		{
			throw new IllegalArgumentException("User \""+userID+"\" does not exist.");
		}
		
		uai.currentProblemPosition = new PositionWithinAssignment(problemSetName, position);
		
		if ((writeToDisk) && (memOnly==false))
		{
			saveUserProgress(userID);
		}
	}	
	/**
	 * Get the current position (problem that should be run next) for the given user and assignment
	 * @param userID
	 * @param assignmentName
	 * @return the position of the current problem for the specified user and assignment, or null if there is no information on that user-assignment pair
	 */
	public synchronized PositionWithinAssignment getCurrentProblem(String userID, String assignmentName)
	{
		debug ("getCurrentProblem ()");
		
		UserAssignmentInfo uai = getUserAssignmentInfo(userID, assignmentName, false);
		
		if(uai == null || uai.currentProblemPosition == null)
		{
			return null;
		}
		
		currentAssignments.put(userID, assignmentName); // mark this assignment as current for this user
		
		return new PositionWithinAssignment(uai.currentProblemPosition.problemSet, uai.currentProblemPosition.position);
	}	
	/**
	 * Get the current position (problem that should be run next) for the given user and problem set. If this problem set 
	 * is included in multiple assignments, the return value may be the position within that problem set in ANY ONE of 
	 * the assignments that includes it.
	 * @param userID
	 * @param problemSetName
	 * @return the position of the next problem for the specified user and problem set, or -1 if there is no information on that user-problemset pair
	 */
	public synchronized int getPositionWithinProblemSet(String userID, String problemSetName)
	{
		debug ("getPositionWithinProblemSet ("+userID+","+problemSetName+")");
		
		if(userID == null || problemSetName == null)
		{
			return -1;
		}
		
		CTATUserData aUser=table.get(userID.toLowerCase());
		
		if(aUser == null)
		{
			return -1;
		}
		
		Map<String, UserAssignmentInfo> thisUsersInfo = aUser.getAssignmentInfo();
		
		if(thisUsersInfo == null)
		{
			return -1;
		}
		
		Collection<String> keys = thisUsersInfo.keySet();
		
		for(String assignmentName : keys)
		{
			UserAssignmentInfo UAI = thisUsersInfo.get(assignmentName);
			PositionWithinAssignment currentPosition = UAI.currentProblemPosition;
			if(currentPosition != null && problemSetName.equals(currentPosition.problemSet))
			{
				currentAssignments.put(userID, assignmentName); // mark this assignment as current for this user
				return currentPosition.position;
			}
		}
		
		return -1;
	}	
	/**
	 * Set the problem_state for the indicated problem.
	 * @param userID must not be null
	 * @param assignmentName must not be null
	 * @param problemSetName must not be null
	 * @param position
	 * @param problem_state
	 * @param writeToDisk
	 */
	public synchronized void setProblemState(String userID, String assignmentName, String problemSetName, int position, String problem_state, boolean writeToDisk) throws IOException
	{
		debug ("setProblemState ()");
		
		if(userID == null || assignmentName == null || problemSetName == null)
		{
			throw new IllegalArgumentException("userID, assignmentName, and problemSetName cannot be null in UserProgressDatabase.setProblemState");
		}
		
		currentAssignments.put(userID, assignmentName); // mark this assignment as current for this user
		
		UserAssignmentInfo uai = getUserAssignmentInfo(userID, assignmentName, true);
		PositionWithinAssignment pwa = new PositionWithinAssignment(problemSetName, position);
		
		UserProblemInfo upi = uai.problemInfoMap.get(pwa);
		if(upi == null)
		{
			upi = new UserProblemInfo(problem_state, null);
			uai.problemInfoMap.put(pwa, upi);
		}
		else
		{
			upi.problemState = problem_state;
		}
		
		if ((writeToDisk) && (memOnly==false))
		{
			saveUserProgress(userID);
		}
	}	
	/**
	 * Set the summary for the indicated problem.
	 * @param userID must not be null
	 * @param assignmentName must not be null
	 * @param problemSetName must not be null
	 * @param position
	 * @param summary
	 * @param writeToDisk
	 */
	public synchronized void setProblemSummary(String userID, 
											   String assignmentName,
											   String problemSetName,
											   int position,
											   String summary,
											   boolean writeToDisk) throws IOException
	{
		debug ("setProblemSummary ()");
				
		if(userID == null)
		{
			debug ("Error: userID is null");
			throw new IllegalArgumentException("userID, assignmentName, and problemSetName cannot be null in UserProgressDatabase.setProblemSummary");
		}
		
		if(assignmentName == null)
		{
			debug ("Error: userID is assignmentName");
			throw new IllegalArgumentException("userID, assignmentName, and problemSetName cannot be null in UserProgressDatabase.setProblemSummary");
		}		
		
		if(problemSetName == null)
		{
			debug ("Error: userID is problemSetName");
			throw new IllegalArgumentException("userID, assignmentName, and problemSetName cannot be null in UserProgressDatabase.setProblemSummary");
		}		
		
		currentAssignments.put(userID, assignmentName); // mark this assignment as current for this user
		
		UserAssignmentInfo uai = getUserAssignmentInfo(userID, assignmentName, true);
		PositionWithinAssignment pwa = new PositionWithinAssignment(problemSetName, position);
		
		UserProblemInfo upi = uai.problemInfoMap.get(pwa);
		
		CTATUserData aUser=table.get(userID.toLowerCase());
		
		if (aUser!=null)
		{
			String extractedSkills=parseSkills (summary);
			
			if (extractedSkills.isEmpty()==true)
			{
				debug ("Info: no skills in problem summary");
			}
			else
			{
				debug ("Extracted skills: " + extractedSkills);
			
				aUser.skillXML=extractedSkills;
			
				try 
				{
					Skills newSkills=Skills.factory (extractedSkills);
				
					updateSkills (newSkills,aUser);
				} 
				catch (Exception e) 
				{
					debug ("Error extracting skills from problem summary xml");
					e.printStackTrace();
				}
			}	
		}
		else
			debug ("Error no user object found for: " + aUser);
		
		if (upi == null)
		{
			upi = new UserProblemInfo(null, summary);
			uai.problemInfoMap.put(pwa, upi);
		}
		else
		{			
			upi.problemSummary = summary;
		}
		
		if ((writeToDisk) && (memOnly==false))
		{
			saveUserProgress(userID);
		}
	}	
	/**
	 * 
	 */
	private String parseSkills (String aSummary)
	{
		debug ("parseSkills ()");
		
		StringBuffer pref=new StringBuffer ();
		
		pref.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		
		try 
		{
			pref.append(URLDecoder.decode(aSummary, "UTF-8"));
		} 
		catch (UnsupportedEncodingException e) 
		{		
			e.printStackTrace();
			return "";
		}

		debug ("Parsing: " + pref.toString());
		
		//String extracted=aSummary; // Backup
 	
		SAXBuilder builder = new SAXBuilder();
		
		Document doc=null;
		
		try 
		{
			doc = builder.build(new StringReader (pref.toString()));
		} 
		catch (JDOMException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Element root = doc.getRootElement();
	    Element skillElement = root.getChild("Skills");
						
		if (skillElement!=null)
		{
			XMLOutputter outputter = new XMLOutputter();			  
			String result = outputter.outputString(skillElement);
			
			return (result);
		}

		debug ("No Skills element found in problem summary!");		
		return "";
	}
	/**
	 * Get the problem_state for the indicated problem.
	 * @param userID
	 * @param assignmentName
	 * @param problemSetName
	 * @param position
	 * @return the problem_state, or null if the problem could not be found
	 */
	public synchronized String getProblemState(String userID, String assignmentName, String problemSetName, int position)
	{
		debug ("getProblemState ()");
		
		UserAssignmentInfo uai = getUserAssignmentInfo(userID, assignmentName, false);
		if(uai == null)
		{
			return null;
		}
		
		if(problemSetName == null)
		{
			return null;
		}
		
		currentAssignments.put(userID, assignmentName); // mark this assignment as current for this user
		
		PositionWithinAssignment pwa = new PositionWithinAssignment(problemSetName, position);
		
		UserProblemInfo upi = uai.problemInfoMap.get(pwa);
		if(upi == null)
		{
			return null;
		}
		
		return upi.problemState;
	}
	/**
	 * Get the summary for the indicated problem.
	 * @param userID
	 * @param assignmentName
	 * @param problemSetName
	 * @param position
	 * @return the summary, or null if the problem could not be found
	 */
	public synchronized String getProblemSummary(String userID, String assignmentName, String problemSetName, int position)
	{
		debug ("getProblemSummary ()");
		
		UserAssignmentInfo uai = getUserAssignmentInfo(userID, assignmentName, false);
		if(uai == null)
		{
			return null;
		}
		
		if(problemSetName == null)
		{
			return null;
		}
		
		currentAssignments.put(userID, assignmentName); // mark this assignment as current for this user
		
		PositionWithinAssignment pwa = new PositionWithinAssignment(problemSetName, position);
		
		UserProblemInfo upi = uai.problemInfoMap.get(pwa);
		if(upi == null)
		{
			return null;
		}
		
		return upi.problemSummary;
	}
	
	/**
	 * Get the status of the indicated problem for the indicated user.
	 * @param userID
	 * @param assignmentName
	 * @param problemSetName
	 * @param position
	 * @return one of "empty", "incomplete", or "complete"
	 */
	public synchronized String getProblemStateStatus(String userID, String assignmentName, String problemSetName, int position)
	{
		debug ("getProblemStateStatus ()");
		
		currentAssignments.put(userID, assignmentName); // mark this assignment as current for this user
		
		final String EMPTY = "empty", INCOMPLETE = "incomplete", COMPLETE = "complete";
		
		String summary = getProblemSummary(userID, assignmentName, problemSetName, position);
		if(summary == null)
		{
			return EMPTY;
		}
		
		try
		{
			summary = URLDecoder.decode(summary, "UTF-8");
			if(summary.contains("CompletionStatus=\"complete\""))
			{
				return COMPLETE;
			}
			else
			{
				return INCOMPLETE;
			}
		}
		catch(UnsupportedEncodingException e)
		{
			return null; // should never happen
		}
	}
	/**
	 * For the given user, get the assignment that was last updated or accessed in the database.
	 */
	public synchronized String getCurrentAssignment(String userID)
	{
		return currentAssignments.get(userID);
	}
	/**
	 * Get the UserAssignmentInfo object with the indicated user ID and assignment name, optionally creating it if it does not exist.
	 * @param userID
	 * @param assignmentName
	 * @param createIfMissing set true to create and add to database any non-existent object that is encountered
	 * @return the UserAssignmentInfo, or null
	 */
	private synchronized UserAssignmentInfo getUserAssignmentInfo(String userID, 
																  String assignmentName, 
																  boolean createIfMissing)
	{
		debug ("getUserAssignmentInfo ("+userID+","+assignmentName+")");
		
		if(userID==null)
		{
			debug ("Error: userID is null");
			return null;
		}
		
		if(assignmentName==null)
		{
			debug ("Error: assignmentName is null");
			return null;
		}		
		
		CTATUserData aUser=table.get(userID.toLowerCase());
		
		if(aUser == null)
			return null; // if the user account has not yet been created, there's nothing we can do here. It must be created at login time.
		
		Map<String, UserAssignmentInfo> thisUsersInfo = aUser.getAssignmentInfo();
		
		if (thisUsersInfo==null)
		{
			debug ("thisUsersInfo is null, see if we should create it ...");
			
			if(createIfMissing)
			{
				thisUsersInfo = new HashMap<String, UserAssignmentInfo>();
				aUser.createAssignmentInfo();
				//table.put(userID, thisUsersInfo);
			}			
			else 
			{
				debug ("Error: thisUserInfo is null but we're not allowed to create it");
				return null;
			}
		}
		
		UserAssignmentInfo uai = thisUsersInfo.get(assignmentName);
		if(uai == null)
		{
			debug ("UserAssignmentInfo is null, see if we should create it ...");
			
			if(createIfMissing)
			{
				uai = new UserAssignmentInfo();
				thisUsersInfo.put(assignmentName, uai);
			}
			else 
			{
				debug ("Error: UserAssignmentInfo is null but we're not allowed to create it");
				return null;
			}
		}
		
		return uai;
	}	
	/**
	 * Save the progress (current position within each assignment) for every user.
	 * Each user's progress is saved in its own file.
	 * @return success (if false, may have been partially successful)
	 */
	public synchronized boolean saveUserProgress()
	{
		debug ("saveUserProgress ()");
		
		Set<String> userIDs = table.keySet();
		boolean success = true;
		for(String id : userIDs)
		{
			try
			{
				saveUserProgress(id);
			}
			catch(IOException e)
			{
				success = false;
				debug ("Error saving user progress database");
			}
		}
		
		return success;
	}	
	/**
	 * Save to disk the information for a certain user. The information is stored in 
	 * a file named with the user ID followed by ".data"
	 * @param userID
	 * @throws IOException
	 */
	public synchronized void saveUserProgress(String userID) throws IOException
	{
		debug ("saveUserProgress ("+userID+")");
		
		if(userID == null)
			return;
		
		CTATUserData aUser=table.get (userID.toLowerCase());
		
		if (aUser==null)
		{
			debug ("Error: no user data object associated with this user!");
			return;
		}
		
		/*
		Map<String, UserAssignmentInfo> thisUsersInfo = aUser.getAssignmentInfo();
		
		if (thisUsersInfo == null)
		{
			debug ("Error: no user assignment info associated with this user!");
			return;
		}
		*/
		
		File f = new File(userprogressPath, userID+".data");
		
		debug ("Writing file: " + f.getAbsolutePath());
		
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
		oos.writeObject(aUser);
		oos.close();
		
		debug ("All done");
	}
	/**
	 * 
	 */
	private void updateSkills (Skills newSkillObject,CTATUserData aUser)
	{
		debug ("updateSkills ()");
	
		/*
		CTATMilestoneManager mManager=aUser.milestoneManager;
		
		Skills targetSkillObject=aUser.skillObject;
		List<Skill> targetSkills=targetSkillObject.getAllSkills();
				
		for (int i=0;i<targetSkills.size();i++)
		{
			Skill aSkill=targetSkills.get(i);
			
			Skill aTestSkill=newSkillObject.getSkill(aSkill.getName());
			
			if (aTestSkill!=null)
			{
				debug ("Updating skill ("+aTestSkill.getName()+") in user data with new pKnown: " + aTestSkill.getPKnown());
			
				if (aTestSkill.getPKnown()>aTestSkill.getMasteryThreshold())
				{
					mManager.checkSkillMilestone (aTestSkill.getName());					
				}
				
				//aSkill.setPKnown(aTestSkill.getPKnown());				
			}
		}
		*/
		
		// First to make sure the mechanism works we'll do a direct checkoff of
		// only the skills coming on in this method. Since the milestone manager
		// provides the house keeping, and since the rest of the local TS doesn't
		// care about maintaining skills, it's sufficient for now.
		
		CTATMilestoneManager mManager=aUser.milestoneManager;
						
		List<Skill> targetSkills=newSkillObject.getAllSkills();
		
		for (int i=0;i<targetSkills.size();i++)
		{
			Skill aSkill=targetSkills.get(i);
					
			if (aSkill.getPKnown()>aSkill.getMasteryThreshold())
			{
				mManager.checkSkillMilestone (aSkill.getName(), currentAssignments.get(aUser.username));					
			}				
		}		
	}
}
