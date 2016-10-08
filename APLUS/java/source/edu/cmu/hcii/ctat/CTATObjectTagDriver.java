/**
 ------------------------------------------------------------------------------------
 $Author: sewall $ 
 $Date: 2013-05-19 14:59:53 -0400 (Sun, 19 May 2013) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATFlashDriver.java,v 1.11 2012/10/03 17:50:26 sewall Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATFlashDriver.java,v $
 Revision 1.11  2012/10/03 17:50:26  sewall
 First working version with CTATAppletFileManager.

 Revision 1.10  2012/09/14 22:20:46  sewall
 Now display htdocs/endofproblemset.html after last problem in problem set.

 Revision 1.9  2012/09/12 14:28:34  vvelsen
 Complete reworking of our http handling code. All file requests now go through our singleton file handler which can handle mounted jar files and as such creates a miniature vfsl

 Revision 1.8  2012/09/11 13:16:18  vvelsen
 Made various changes to the local http handler. It can now use both a directory.txt file as well as a curriculum.xml file. Added a way to indicate if the server should generate flashvars with an info field. When the info field is generated it creates the horizontal menu bar at the top of the screen that shows all available problems

 Revision 1.7  2012/09/07 18:15:05  vvelsen
 Quick checkin so that Jonathan can test his CL bridge code

 Revision 1.6  2012/09/06 17:48:55  vvelsen
 Reworking of the local tutorshop to allow easier managment of runtime contexts. The completely local/demo http handler is now in its own file and the CLBridge should now be ready to be tested as a standalone class

 Revision 1.5  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.4  2012/03/05 05:54:50  sewall
 Fix NPE in generateFlashVars() on end of problem set.

 Revision 1.3  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.9  2011/09/30 20:25:56  sewall
 Add FlashVars dataset_name, dataset_level_type1, dataset_level_name1, problem_name SessionLog

 Revision 1.8  2011/09/29 04:10:24  sewall
 Port changes from AuthoringTools/java/source/, where this code is now maintained.

 Revision 1.2  2011/09/29 03:24:41  sewall
 Remove hard-coded logging URL from Flash Vars.

 Revision 1.1  2011/08/26 13:12:13  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.7  2011/07/27 21:06:05  kjeffries
 skills flashvar now indicates skills for the specific problem, not the entire problem set

 Revision 1.6  2011/07/20 19:58:56  kjeffries
 Modified flashvars to handle the case where there are no more problems in the set, and added a "skills" flashvar

 Revision 1.5  2011/03/30 16:25:55  vvelsen
 Almost finished problem selection and problem sequencing. The tutor now also properly sets the top navigation menu.

 Revision 1.4  2011/03/29 18:59:53  vvelsen
 More capabilities in the management of a problem set.

 Revision 1.3  2011/03/25 20:38:49  vvelsen
 Added much more capabilties to the USB TutorShop. We can now start problems defined in problem_set.xml files. Much more of the problem sequencing code has been finished but more needs to happen there. Some of the internals have been bolstered.

 Revision 1.2  2011/02/08 14:42:54  vvelsen
 More features added. The server now properly generates html that can load swf files. SWF files loaded through that html connect back to the built-in tutoring service but the code still needs a lot of work to deal with brd path checking and Flash security handling. Web server log files are now properly generated.

 Revision 1.1  2011/02/07 19:50:15  vvelsen
 Added a number of files that can manage local files as well as generate html from parameters and templates.

 $RCSfile: CTATFlashDriver.java,v $ 
 $Revision: 19035 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATFlashDriver.java,v $ 
 $State: Exp $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 	
 	Object tag:
 
    	<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" 
    			id="7a10a40aaeec6c1144ffe776b9fb4cdb" 
    			codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,14,0" 
    			width="550" 
    			height="400">
    			<param	name="movie" value="/data/vvelsen/SimpleAudioButton/Flash/SimpleAudioButton.swf" >
    			<param	name="SeamlessTabbing" value="false" >
    			<param	name="quality" value="high" >
    			<param	name="bgcolor" value="#ffffff" >
    			<param	name="allowScriptAccess" value="sameDomain" >
    			<param	name="flashvars" value="" >
    			<embed	src="/data/vvelsen/SimpleAudioButton/Flash/SimpleAudioButton.swf" 
    					quality="high" 
    					SeamlessTabbing="false" 
    					bgcolor="#ffffff" 
    					width="550" 
    					height="400" 
    					allowScriptAccess="sameDomain" 
    					type="application/x-shockwave-flash" 
    					flashvars="" 
    					name="7a10a40aaeec6c1144ffe776b9fb4cdb" 
    					pluginspage="http://www.macromedia.com/go/getflashplayer">
    			</embed>
    	</object>
    	
	FlashVars:
    	 
    	 MAX_FILE_SIZE=1024000&
    	 lcId=7a10a40aaeec6c1144ffe776b9fb4cdb&
    	 mode=save&
    	 tutorid=-1&
    	 font=&
    	 info=&
    	 dimension_group=Auto&
    	 icon_file=undefined&
    	 VideoPath=&
    	 MediaPath=&
    	 TutorCuePointTime=5&
    	 TutorCuePointName=TutorCuePoint&
    	 RandomizeAnswers=false&
    	 BehaviorRecorderMode=AuthorTimeTutoring&
    	 dataset_level_name1=Unit1&
    	 dataset_level_type1=unit&
    	 dataset_level_name2=Section1&
    	 dataset_level_type2=section&
    	 Logging=ClientToLogServer&
    	 log_service_url_group=SandBox&
    	 log_service_url=http://digger.pslc.cs.cmu.edu/log/server/sandboxlogger.php&
    	 problem_name=CTAT_Example_Problem&
    	 user_guid=CTAT_Example_User&
    	 session_id=SimpleAudioButton&
    	 problem_context=&
    	 problem_tutorflag=tutor&
    	 problem_otherproblemflag=&
    	 dataset_name=&
    	 dataset_level_name=1&
    	 dataset_level_type=1&
    	 course_name=&
    	 unit_name=&
    	 section_name=&
    	 school_name=&
    	 class_name=&
    	 period_name=&
    	 class_description=&
    	 instructor_name=&
    	 study_condition_name=1&
    	 study_condition_type=1&
    	 auth_token=myAuth_token&
    	 container_id=myContainer&
    	 source_id=PACT_CTAT_FLASH&
    	 external_object_id=myExternalId&
    	 log_to_disk_directory=.&
    	 cognitive_model_folder=&
    	 remoteSocketURL=digger.pslc.cs.cmu.edu&
    	 remoteSocketPort=1502&
    	 study_name=&
    	 admit_code=&
    	 student_problem_id=&
    	 question_file=data/vvelsen/SimpleAudioButton/FinalBRDs/Template.brd&
    	 curriculum_service_url=%2Ftutornext.php%3Fposition%3D1%26self%3Ddata%2Fvvelsen%2FSimpleAudioButton&viewed=0&
    	 student_interface=data/vvelsen/SimpleAudioButton/Flash/&
 
 
*/

package edu.cmu.hcii.ctat;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

/** 
 *
 */
public abstract class CTATObjectTagDriver extends CTATBase
{    		    	
	private String sessionID="undefined";
	private Boolean includeMenu=false;
	private Boolean SCORMCompliant=false;	
	
	/**
	 *
	 */
    public CTATObjectTagDriver () 
    {
    	setClassName ("CTATObjectTagDriver");
    	debug ("CTATObjectTagDriver ()");
    	
    	UUID uuid=UUID.randomUUID();
    	sessionID=uuid.toString();    	
    }
    /**
     * 
     * @param aValue
     */
    public void setSCORMCompliant (Boolean aValue)
    {
    	SCORMCompliant=aValue;
    }
    /**
     * 
     */
    public Boolean getSCORMCompliant ()
    {
    	return (SCORMCompliant);
    }
    /**
     * 
     */
    public void setIncludeMenu (Boolean aValue)
    {
    	includeMenu=aValue;
    }
    /**
     * 
     */
    public Boolean getIncludeMenu ()
    {
    	return (includeMenu);
    }
	/**
	 *
	 */
    public void setSessionID (String anID)
    {
    	sessionID=anID;
    }
	/**
	 *
	 */
    public String getSessionID ()
    {
    	return (sessionID);
    }
	/**
	 *
	 */
    public String generateFlashVars (CTATProblem aProblem,CTATProblemSet aSet) 
    {
    	debug ("generateFlashVars ("+CTATLink.allowWriting+")");
   	
    	return (generateFlashVars (aProblem,aSet,false));
    }    
	/**
	 *
	 */
    public String generateFlashVars (String aSWF,String aBRD) 
    {
    	debug ("generateFlashVars (String aSWF,String aBRD)");
   	
    	StringBuffer flashvars=new StringBuffer ();
   	
    	flashvars.append ("BehaviorRecorderMode=AuthorTimeTutoring&");
   	
    	if (CTATLink.allowWriting==false)
    		flashvars.append ("Logging=None&");
    	else
    		flashvars.append ("Logging=ClientToService&");
   	
    	flashvars.append ("SessionLog=true&");
    	flashvars.append ("log_service_url_group=TutorShopUSB&");
    	flashvars.append ("log_service_url="+CTATLink.datashopURL+"&");
    	flashvars.append ("user_guid="+CTATLink.userID+"&");
   	
    	if (this.getSessionID().equals("undefined")==true)
    	{
    		UUID uuid = UUID.randomUUID();
    		this.setSessionID (uuid.toString());
    	}
   	
    	flashvars.append ("session_id="+this.getSessionID()+"&");
    	flashvars.append ("source_id=PACT_CTAT_FLASH&");
    	flashvars.append ("TutorShopDeliveryMethod=sendandload&");
    	flashvars.append ("lcId="+System.currentTimeMillis ()+"&");      	
    	flashvars.append ("remoteSocketURL="+CTATLink.hostName+"&");
    	flashvars.append ("remoteSocketPort="+CTATLink.tsPort+"&");
   	
   		flashvars.append ("dataset_name="+CTATLink.datasetName+"&");
   		flashvars.append ("dataset_level_type1=ProblemSet&");
   		flashvars.append ("dataset_level_name1=ProblemSet&");
   		flashvars.append ("problem_name=ProblemName&");    	
		flashvars.append ("question_file="+aBRD+"&");    		
   		flashvars.append ("reuse_swf=true&");      // for Android, do always
   		flashvars.append ("target_frame=_self&");   // for Android, do always
		flashvars.append ("student_interface="+aSWF+"&");
		
        if (this.getSCORMCompliant()==true)
        {
            flashvars.append ("scorm_enabled=true&");
        } 
       		   	   	
    	return (flashvars.toString ());
    }
	/**
	 *
	 */
   public String generateFlashVars (CTATProblem aProblem,
   								 	CTATProblemSet aSet,
   								 	boolean isLastProblem) 
   {
   		debug ("generateFlashVars (CTATProblem aProblem,CTATProblemSet aSet,boolean isLastProblem)");
  	
   		StringBuffer flashvars=new StringBuffer ();
  	
   		flashvars.append ("BehaviorRecorderMode=AuthorTimeTutoring&");
  	
   		if (CTATLink.allowWriting==false)
   			flashvars.append ("Logging=None&");
   		else
   			flashvars.append ("Logging=ClientToService&");
  	
   		flashvars.append ("SessionLog=true&");
   		flashvars.append ("log_service_url_group=TutorShopUSB&");
   		flashvars.append ("log_service_url="+CTATLink.datashopURL+"&");
   		flashvars.append ("user_guid="+CTATLink.userID+"&");
  	
   		if (this.getSessionID().equals("undefined")==true)
   		{
   			UUID uuid = UUID.randomUUID();
   			this.setSessionID (uuid.toString());
   		}
  	
   		flashvars.append ("session_id="+this.getSessionID()+"&");
   		flashvars.append ("source_id=PACT_CTAT_FLASH&");
   		flashvars.append ("TutorShopDeliveryMethod=sendandload&");
   		flashvars.append ("lcId="+System.currentTimeMillis ()+"&");
  	
   		if (this.getIncludeMenu()==true)
   			flashvars.append ("info="+aSet.buildMenuHTML()+"&");
  	
   		flashvars.append ("remoteSocketURL=127.0.0.1&");
   		flashvars.append ("remoteSocketPort="+CTATLink.tsPort+"&");
  	
   		if(aProblem != null)
   		{
   			flashvars.append ("dataset_name="+CTATLink.datasetName+"&");
   			flashvars.append ("dataset_level_type1=ProblemSet&");
   			flashvars.append ("dataset_level_name1="+aProblem.problem_path+"&");
   			flashvars.append ("problem_name="+aProblem.name+"&");
      	
   			{
   				String qf = null;
   			
   				if(!CTATLink.remoteHost.equals("") || !CTATLink.remoteHost.equals("local"))
   					flashvars.append ("question_file="+(qf="http://localhost:"+CTATLink.wwwPort+"/"+aProblem.problem_path+"FinalBRDs/"+aProblem.problem_file)+"&");
   				else
   					flashvars.append ("question_file="+(qf=CTATLink.htdocs+"/"+aProblem.problem_path+"FinalBRDs/"+aProblem.problem_file)+"&");
          	
   				debug ("Creating question_file flashvar based on remoteHost: "+CTATLink.remoteHost+"; result "+qf);
   			}

   			if(!isLastProblem)
   				flashvars.append ("curriculum_service_url=http://127.0.0.1:"+CTATLink.wwwPort+"/problemselect.cgi?position="+(aProblem.index+1)+"&");
   			else 
   			{
   				flashvars.append ("curriculum_service_url=http://127.0.0.1:"+CTATLink.wwwPort+"/lastproblem.cgi?position="+(aProblem.index+1)+"&");
   			}
   		
      		flashvars.append ("reuse_swf=true&");      // for Android, do always
      		flashvars.append ("target_frame=_self&");   // for Android, do always
      		
      		flashvars.append ("student_interface="+("/"+aProblem.problem_path+"Flash/"+aProblem.student_interface)+"&");
      		
      		try 
      		{
      			flashvars.append("skills=" + URLEncoder.encode(/*aSet.getSkillsXML()*/aProblem.getSkillsXML(), "UTF-8"));
      		} 
      		catch (UnsupportedEncodingException e) 
      		{
      			// Ignore; this should never happen because UTF-8 must be supported
      		}
   		}
   		else // aProblem == null; take that to mean the end of the problem set has been reached
   		{
   			flashvars.append("question_file=" + CTATLink.htdocs + "/doesnotexist.brd&");
   			flashvars.append("curriculum_service_url=http://127.0.0.1:" + CTATLink.wwwPort + "/problemselect.cgi?position=-1&");
   			flashvars.append("student_interface=doesnotexist.swf&");
   		}
   	
   		if (this.getSCORMCompliant()==true)
   		{
   			flashvars.append ("scorm_enabled=true&");
   		}    	
  	   	
   		return (flashvars.toString ());
   	}    
	/**
	 *
	 */
   	public String generateFlashVars (CTATProblem aProblem,
   								 	 CTATProblemSet aSet,
   								 	 int problemNumber,
   								 	 String problemSetActivationStatus) 
   	{
   		debug ("generateFlashVars (CTATProblem aProblem,CTATProblemSet aSet,int problemNumber,String problemSetActivationStatus)");
  	
		String problemSetName=aSet.getName();
   		
   		StringBuffer flashvars=new StringBuffer ();
  	
   		flashvars.append ("BehaviorRecorderMode=AuthorTimeTutoring&");
  	
   		if (CTATLink.allowWriting==false)
   			flashvars.append ("Logging=None&");
   		else
   			flashvars.append ("Logging=ClientToService&");
  	
   		flashvars.append ("SessionLog=true&");
   		flashvars.append ("log_service_url_group=TutorShopUSB&");
   		flashvars.append ("log_service_url="+CTATLink.datashopURL+"&");    	
   		flashvars.append ("user_guid="+CTATLink.userID+"&");
  	
   		if (this.getSessionID().equals("undefined")==true)
   		{
   			UUID uuid = UUID.randomUUID();
   			this.setSessionID (uuid.toString());
   		}
  	
   		flashvars.append ("session_id="+this.getSessionID()+"&");
   		flashvars.append ("source_id=PACT_CTAT_FLASH&");
   		flashvars.append ("TutorShopDeliveryMethod=sendandload&");
   		flashvars.append ("lcId="+System.currentTimeMillis ()+"&");
  	
   		if (this.getIncludeMenu()==true)
   			flashvars.append ("info="+aSet.buildMenuHTML()+"&");
  	
   		flashvars.append ("remoteSocketURL="+CTATLink.hostName+"&");
   		flashvars.append ("remoteSocketPort="+CTATLink.tsPort+"&");
  	
   		if(aProblem != null)
   		{
   			flashvars.append ("dataset_name="+CTATLink.datasetName+"&");
   			flashvars.append ("dataset_level_type1=ProblemSet&");
   			flashvars.append ("dataset_level_name1="+aProblem.problem_path+"&");
   			flashvars.append ("problem_name="+aProblem.name+"&");      	        
			flashvars.append ("question_file=http://"+CTATLink.hostName+":"+CTATLink.wwwPort+"/tutors/problem_sets/"+problemSetActivationStatus+"/"+problemSetName+"/FinalBRDs/"+aProblem.problem_file+"&");   				   			
   			flashvars.append ("curriculum_service_url=http://"+CTATLink.hostName+":"+CTATLink.wwwPort+"/process_student_assignment/"+problemSetName+"/"+problemNumber+"&");   		
      		flashvars.append ("reuse_swf=true&");      // for Android, do always
      		flashvars.append ("target_frame=_self&");   // for Android, do always      		      		
      		flashvars.append ("student_interface=/tutors/problem_sets/"+problemSetActivationStatus+"/"+problemSetName+"/Flash/"+aProblem.student_interface+"&");
      		
      		try 
      		{
      			flashvars.append("skills=" + URLEncoder.encode(aProblem.getSkillsXML(), "UTF-8"));
      		} 
      		catch (UnsupportedEncodingException e) 
      		{
      			// Ignore; this should never happen because UTF-8 must be supported
      		}
   		}
   		else // aProblem == null; take that to mean the end of the problem set has been reached
   		{
   			flashvars.append("question_file=" + CTATLink.htdocs + "/doesnotexist.brd&");
   			flashvars.append("curriculum_service_url=http://127.0.0.1:" + CTATLink.wwwPort + "/problemselect.cgi?position=-1&");
   			flashvars.append("student_interface=doesnotexist.swf&");
   		}
   		
        if (this.getSCORMCompliant()==true)
        {
            flashvars.append ("scorm_enabled=true&");
        }    		
  	   	
   		return (flashvars.toString ());
   	}  
    /**
     * 
     */
    public String generateObjectTags (String template,
    								  CTATCurriculum aCurriculum,
    								  CTATProblem aProblem,
    								  CTATProblemSet aSet,
    								  int problemNumber,
    								  String problemSetActivationStatus)
    {
    	debug ("generateObjectTags ()");
    	
		String response = template;
		
		String problemSetName=aSet.getName();
		
		response = response.replace("HOST_NAME", CTATLink.hostName);
		response = response.replace("PORT_NUMBER", ""+CTATLink.wwwPort);
		response = response.replace("TS_PORT", ""+CTATLink.tsPort);
		response = response.replace("HOST_NAME", CTATLink.hostName);
		response = response.replace("PROBLEM_STATE_STATUS", CTATLink.userProgress.getProblemStateStatus(CTATLink.userID, aCurriculum.getAssignment(problemSetName), problemSetName, problemNumber));
		response = response.replace("QUESTION_FILE", "http://"+CTATLink.hostName+":"+CTATLink.wwwPort+"/tutors/problem_sets/"+problemSetActivationStatus+"/"+problemSetName+"/FinalBRDs/"+aProblem.problem_file);
		response = response.replace("STUDENT_INTERFACE", "/tutors/problem_sets/"+problemSetActivationStatus+"/"+problemSetName+"/Flash/"+aProblem.student_interface);
		response = response.replace("CS2NUID", CTATLink.userID);
		response = response.replace("INFO",(CTATLink.showNavButtons ? generateNavButtons(aCurriculum,problemSetName, problemNumber) : ""));
		response = response.replace("SESSION_ID", this.getSessionID());
		response = response.replace("STUDENT_ASSIGNMENT_ID", problemSetName);
		response = response.replace("PROBLEM_NUMBER", ""+ problemNumber);
		
		response = response.replace("LOG_SERVICE_URL", CTATLink.datashopURL);
		
		if (CTATLink.allowWriting==false)
			response = response.replace("LOGGINGSETTING","None");
		else
			response = response.replace("LOGGINGSETTING", "ClientToService");

		response = response.replace("DATASET_NAME", CTATLink.datasetName);
		response = response.replace("DATASET_LEVEL_NAME1", aCurriculum.getAssignment(problemSetName));  // assignment
		response = response.replace("DATASET_LEVEL_NAME2", aSet.getName());
		response = response.replace("PROBLEM_NAME", aProblem.getName ());
		response = response.replace("CLASS_NAME", aCurriculum.getAssignedClass());
		response = response.replace("SCHOOL_NAME", aCurriculum.getSchoolName());  // FIXME put school in this.getCurriculum() or config.data
		response = response.replace("INSTRUCTOR_NAME", aCurriculum.getInstructorName());
		response = response.replace("STUDY_CONDITION_NAME1", aCurriculum.getConditionName(problemSetName));  // FIXME put condition in this.getCurriculum() or config.data
																		
		CTATUserData aUser=CTATLink.userProgress.getUser(CTATLink.userID);
		
		boolean milestoneHtmlGenerated = false;
		boolean milestonesExist = false;
		
		if (aUser!=null)
		{
			debug ("We have a user and we have skills, adding them to page ...");
			
			debug ("Skill XML: " + aUser.skillXML);
			
			response = response.replace("SKILLS", ""+aUser.skillXML);
								
			String milestoneString=aUser.getMilestoneManager().generateMilestoneHTML();
			
			milestonesExist = (milestoneString != null);
			
			if(milestonesExist)
			{
				String milestonesEncoded=encodeHTML (milestoneString);

				response = response.replace ("MILESTONES", milestonesEncoded);
				milestoneHtmlGenerated = true;

				debug ("Generated html: " + response);
			}
		}
		else
		{
			// leave data-milestones tag as-is so it can be removed later
			
			debug ("Generated html: " + response);					
		}
		
		if(!milestonesExist)
		{
			debug("No milestones; removing data-milestones tag");
			response = response.replaceAll("data-milestones=\'MILESTONES\'", "");
		}
		
		if(milestoneHtmlGenerated && aUser != null)
		{
			aUser.milestoneManager.markShown();
		}		
		
		return (response);
    }
    /**
     * 
     */
    public static String createIndexFile ()
    {
    	String indexFile="index.html";
    	
		if (CTATLink.deployType==CTATLink.DEPLOYFLASH)
		{
			indexFile="index-flash.html";	
		}
		
		if (CTATLink.deployType==CTATLink.DEPLOYHTML5)
		{
			indexFile="index-html5.html";	
		}
		
		return (indexFile);
    }
    /**
     * 
     */
	/**
	 * 
	 */
	public static String encodeHTML(String s)
	{
	    StringBuffer out = new StringBuffer();
	    
	    for(int i=0; i<s.length(); i++)
	    {
	        char c = s.charAt(i);
	        if(c > 127 || c=='"' || c=='<' || c=='>')
	        {
	           out.append("&#"+(int)c+";");
	        }
	        else
	        {
	            out.append(c);
	        }
	    }
	    
	    return out.toString();
	}	
	/**
	 * Generate the HTML for the navigation buttons at the buttom of the tutor.
	 * @param problemSet problem set that the user is currently running
	 * @param problemNumber number within the problem set of the problem that the user is currently running
	 * @return HTML, already URL-encoded
	 */
	private String generateNavButtons(CTATCurriculum aCurriculum,
									  String problemSet, 
									  int problemNumber)
	{
		debug ("generateNavButtons ()");
		
		StringBuilder sb = new StringBuilder();
		
		String assignmentName = aCurriculum.getAssignment(problemSet);
		
		//UserProgressDatabase.PositionWithinAssignment pwa = CTATLink.userProgress.getCurrentProblem(cs2n_uid, assignmentName);
		PositionWithinAssignment pwa = CTATLink.userProgress.getCurrentProblem(CTATLink.userID, assignmentName);
		
		String lastStartedProblemSet = ((pwa != null) ? (pwa.problemSet) : (problemSet));
		int lastStartedProblemPosition = ((pwa != null) ? (pwa.position) : (problemNumber));
		
		//>------------------------------ regular problem sets
		
		String currentProbset=null;
		
		try
		{
			currentProbset = aCurriculum.getFirstProblemSet(assignmentName);
		} 
		catch(NumberFormatException e) 
		{ 
			return ""; 
		}
		
		boolean visitedLastStartedProblem = false; // becomes true when the button-generating loop visits the last problem that the user has begun
		
		while(currentProbset != null) // loop once for each problem set in this assignment
		{
			CTATProblemSet probset = aCurriculum.getProblemSet(currentProbset);
			
			sb.append("<div class='section'><div class='title'>"+probset.getDescription()+"</div>");
			
			int numProblems = probset.getNumProblems();
			
			debug ("Building menu for initial real problems: " + probset.initialSequence);
									
			boolean currentProbsetIsActive;
			if(currentProbset.equals(problemSet)) currentProbsetIsActive = true;
			else currentProbsetIsActive = false;
			
			boolean currentProbsetIsLastStarted;
			if(currentProbset.equals(lastStartedProblemSet)) currentProbsetIsLastStarted = true;
			else currentProbsetIsLastStarted = false;
									
			int i=0;
			Boolean golded=false;
			
			for(i = 1; i <= numProblems; i++)
			{
				boolean thisIsTheActiveProblem = false;
				boolean thisIsTheLastStartedProblem = false;
				
				String status="step";
				
				if((currentProbsetIsActive==true) && (i==problemNumber))
				{
					thisIsTheActiveProblem=true;
				}
				
				if((currentProbsetIsLastStarted==true) && (i==lastStartedProblemPosition))
				{
					thisIsTheLastStartedProblem=true;
				}
				
				if (isGuest ()==true)
				{
					status="current step";					
				}
				else
				{
					if ((i-1)<probset.initialSequence)
					{
						if(thisIsTheActiveProblem)
						{
							status = "current step";
						}
						else if(!visitedLastStartedProblem)
						{
							status = "step";
						}
						else
						{
							debug ("We have a normal account");
							status = "disabled step";
						}
					}
					else
						status= "step gold";
				}
				
				if (status.equals("step gold")==false)
				{
					probset.setCurrentIndex(i-1); // -1 because it starts at 0
				
					sb.append("<div class='"+status+"' title='"+probset.getNextProblem().description+"'>");
				
					if (isGuest ()==true)
					{
						sb.append("<a href=\"/run_student_assignment/"+currentProbset+"/"+i+"\" data-active=\""+thisIsTheActiveProblem+"\" data-name=\"tutor-"+currentProbset+"-"+i+"\" data-src=\"\">");
					}
					else
					{
						if (!visitedLastStartedProblem)
							sb.append("<a href=\"/run_student_assignment/"+currentProbset+"/"+i+"\" data-active=\""+thisIsTheActiveProblem+"\" data-name=\"tutor-"+currentProbset+"-"+i+"\" data-src=\"\">");
					}	
				
					sb.append("<img alt=\""+i+"\" src=\"/assets/navigation/bubble-74cdd8414c90c4906aefc4b750c957d4.png\" />");
					sb.append("<span>"+i+"</span>");
				
					if (isGuest ()==true)
						sb.append("</a>");
					else
					{				
						if(!visitedLastStartedProblem)
							sb.append("</a>");
					}	
				
					sb.append("</div>");
				
					if(thisIsTheLastStartedProblem)
					{
						visitedLastStartedProblem = true;
					}
				}
				else
				{
					if (golded==false)
					{												
						sb.append("<div title=\"Practice\" class=\"step gold\">");										      
						sb.append("<a data-practice=\""+currentProbset+"\" href=\"#\">");
						sb.append("<img src=\"/assets/navigation/gold-bubble-61776757d61d980982ccc7adab024c58.png\" alt=\"P\" />");
						sb.append("<span>P</span>");
						sb.append("</a>");
				        sb.append("</div>");						
						
						golded=true; // we only want one of these
					}	
				}				
			}
			
			sb.append("</div>");
												
			currentProbset = aCurriculum.getNextStudentAssignment(currentProbset);
			
			if(currentProbset != null) // i.e. if there's another problem set after the one just processed
			{
				sb.append("<div class=\"separator\"><img alt=\"Small-arrow\" src=\"/assets/navigation/small-arrow-4423d4ee586332329a9d12f5a61c957c.png\" /></div>"); // add a separator arrow
			}			
		}
		
		//>------------------------------ practice problem sets
		
		currentProbset=null;
		
		try
		{
			currentProbset = aCurriculum.getFirstProblemSet(assignmentName);
		} 
		catch(NumberFormatException e) 
		{ 
			return ""; 
		}
		
		visitedLastStartedProblem = false; // becomes true when the button-generating loop visits the last problem that the user has begun
		
		while(currentProbset != null) // loop once for each problem set in this assignment
		{
			CTATProblemSet probset = aCurriculum.getProblemSet(currentProbset);		
		
			sb.append("<div id=\""+currentProbset+"\" class=\"section popup\">");
		
			int numProblems = probset.getNumProblems();
												
			boolean currentProbsetIsActive;
			if(currentProbset.equals(problemSet)) currentProbsetIsActive = true;
			else currentProbsetIsActive = false;
															
			for(int i = 1; i <= numProblems; i++)
			{
				if (currentProbsetIsActive==true)
					sb.append("<div title=\"Practice Problem\" class=\"step\">");
				else
					sb.append("<div title=\"Practice Problem\" class=\"disabled step\">");
				
				sb.append("  <img src=\"/assets/navigation/bubble-74cdd8414c90c4906aefc4b750c957d4.png\" alt=\"+(i+1)+\" />");
				sb.append("  <span>"+(i+1)+"</span>");
				sb.append("</div>");
			}
			
			sb.append("</div>");
			
			currentProbset = aCurriculum.getNextStudentAssignment(currentProbset);
		}
						
		//>-----------------------------------------------------
		
		debug ("Generated problem navigation menu: " + sb.toString());
		
		try
		{
			return URLEncoder.encode(sb.toString(), "UTF-8");
		} 
		catch (UnsupportedEncodingException e) 
		{ 
			return null; 
		}
	}
	/**
	 * 
	 */
	private Boolean isGuest ()
	{
		if (CTATLink.userID.length()>30)
			return (true);
		
		return (false);
	}	
	/**
	 *
	 */
    public abstract String generateObjectTags (CTATProblem aProblem,CTATProblemSet aSet);
    
	/**
	 *
	 */
    public abstract String generateObjectTags (CTATProblem aProblem,CTATProblemSet aSet,int problemNumber,String problemSetActivationStatus);
    
	/**
	 *
	 */
    public abstract String generateObjectTags (File aSWF,File aBRD);
}
