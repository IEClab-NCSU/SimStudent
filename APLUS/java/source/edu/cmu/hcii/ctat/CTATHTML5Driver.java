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
 	
*/

package edu.cmu.hcii.ctat;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

/** 
 *
 */
public class CTATHTML5Driver extends CTATObjectTagDriver
{    		    			
	/**
	 *
	 */
    public CTATHTML5Driver () 
    {
    	setClassName ("CTATFlashDriver");
    	debug ("CTATFlashDriver ()");    	    	
    }      
	/**
	 *
	 */
    public String generateObjectTags (CTATProblem aProblem,CTATProblemSet aSet) 
    {
    	debug ("generateObjectTags (CTATProblem aProblem,CTATProblemSet aSet)");
    	    	
    	StringBuffer aPage=new StringBuffer ();
    	
    	aPage.append("<script>\n");

    	if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPIDISABLED)
    	{	
    		aPage.append("\nvar mobileAPI=new CTATMobileTutorHandler (\"swfFile\",\"disabled\");\n\n");
    	}    	
    	
    	if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPION)
    	{	
    		aPage.append("\nvar mobileAPI=new CTATMobileTutorHandler (\"swfFile\",\"on\");\n\n");
    	}
    	
    	if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPIAUTO)
    	{	
    		aPage.append("\nvar mobileAPI=new CTATMobileTutorHandler (\"swfFile\",\"auto\");\n\n");
    	}
    	
        aPage.append("var FlashVars=\n");
        aPage.append("{\n");
        
        if (this.getSCORMCompliant()==true)
        {
            aPage.append("	scorm_enabled:\"true\",\n");
        }
        
        aPage.append("	admit_code:\"ies\",\n");
        aPage.append("	authenticity_token:\"none\",\n");
        aPage.append("	auth_token:\"none\",\n");
        aPage.append("	BehaviorRecorderMode:\"'AuthorTimeTutoring\",\n");
        aPage.append("	class_name:\"'Default Class\",\n");
        
		aPage.append("	curriculum_service_url:\""+"http://127.0.0.1:"+CTATLink.wwwPort+"/lastproblem.cgi?position="+(aProblem.index+1)+"\",\n");
        aPage.append("	run_problem_url:\""+"http://127.0.0.1:"+CTATLink.wwwPort+"/nextproblem.cgi?position="+(aProblem.index+1)+"\",\n");		
        
        aPage.append("	dataset_level_name1:\""+aProblem.problem_path+"\",\n");
        aPage.append("	dataset_level_type1:\"ProblemSet\",\n");
        aPage.append("	dataset_name:\""+CTATLink.datasetName+"\",\n");
        aPage.append("	expire_logout_url:\"none\",\n");

    	if (this.getIncludeMenu()==true)        
    		aPage.append("	info:\""+aSet.buildMenuHTML()+"\",\n");

        aPage.append("	instructor_name:\"none\",\n");
        aPage.append("	instrumentation_log:\"off\",\n");
        aPage.append("	lcId:\""+System.currentTimeMillis ()+"\",\n");

    	if (CTATLink.allowWriting==false)
        	aPage.append("	Logging:\"None\", // 'ClientToService' or 'ClientToLogServer'\n");    	
    	else
        	aPage.append("	Logging:\"ClientToService\", // 'ClientToService' or 'ClientToLogServer'\n");

        aPage.append("	log_service_url:\""+CTATLink.datashopURL+"\",\n");
        aPage.append("	problem_name:\""+aProblem.name+"\",\n");
        aPage.append("	problem_position:\"none\",\n");
        aPage.append("	problem_started_url:\"none\",\n");
        aPage.append("	problem_state_status:\"empty\", //  'empty', 'complete', or 'incomplete'\n");

		String qf="";

		if(!CTATLink.remoteHost.equals("") || !CTATLink.remoteHost.equals("local"))
			qf=("http://localhost:"+CTATLink.wwwPort+"/"+aProblem.problem_path+"FinalBRDs/"+aProblem.problem_file);
		else
			qf=(CTATLink.htdocs+"/"+aProblem.problem_path+"FinalBRDs/"+aProblem.problem_file);

        aPage.append("	question_file:\""+qf+"\",\n");
        aPage.append("	refresh_session_url:\"none\",\n");
        aPage.append("	remoteSocketPort:\""+CTATLink.tsPort+"\",\n");
        aPage.append("	remoteSocketURL:\"127.0.0.1\",\n");
        aPage.append("	restore_problem_url:\"none\",\n");
        
		if (CTATLink.deployType==CTATLink.DEPLOYHTML5)
		{
			aPage.append("	reuse_swf:\"false\",\n");
		}
		else
		{
			aPage.append("	reuse_swf:\""+CTATLink.reuseSWF+"\",\n");
		}
		
        //aPage.append("	run_problem_url:\"none\",\n");
        aPage.append("	school_name:\"none\",\n");
        aPage.append("	SessionLog:\"true\",\n");

    	if (this.getSessionID().equals("undefined")==true)
    	{
    		UUID uuid = UUID.randomUUID();
    		this.setSessionID(uuid.toString());
    	}        

        aPage.append("	session_id:\""+this.getSessionID()+"\",\n");
        aPage.append("	session_timeout:\"none\",\n");

   		try 
   		{
   			aPage.append("	skills:\""+URLEncoder.encode(/*aSet.getSkillsXML()*/aProblem.getSkillsXML(), "UTF-8")+"\",\n");
   		} 
   		catch (UnsupportedEncodingException e) 
   		{
   			// Ignore; this should never happen because UTF-8 must be supported
   			aPage.append("	skills:\"\",\n");
   		}        

        aPage.append("	source_id:\"FLASH_PSEUDO_TUTOR\", // 'FLASH_PSEUDO_TUTOR' or 'CTAT_JAVA_TUTOR'\n");
        aPage.append("	student_interface:\"none\",\n");
        aPage.append("	student_problem_id:\"none\",\n");
        aPage.append("	study_condition_name:\"none\",\n");
        aPage.append("	study_condition_type:\"none\",\n");
        aPage.append("	study_conditon_description:\"none\",\n");
        aPage.append("	study_name:\"Study1\",\n");
        aPage.append("	target_frame:\"none\",\n");
        aPage.append("	TutorShopDeliveryMethod:\"sendandload\",\n");
        aPage.append("	user_guid:\""+CTATLink.userID+"\",\n");
        aPage.append("	wmode:\"opaque\",\n");
        aPage.append("	log_to_disk_directory:\"none\",\n");
        aPage.append("	DeliverUsingOLI:\"none\"\n");
        aPage.append("};\n");

        aPage.append("</script>\n");

		aPage.append("<div id=\"container\" name=\"container\" class=\"container\">\n");
		aPage.append("<canvas id=\"main-canvas\" width=\"320\" height=\"200\">Your browser does not support CTAT. Please update or replace your browser.</canvas>\n");
		aPage.append("</div>\n");

		aPage.append("<script>prepTutorArea ();</script>\n");

    	return aPage.toString ();
    }
    /**
     * 
     */
	@Override
	public String generateObjectTags(CTATProblem aProblem,
									 CTATProblemSet aSet,
									 int problemNumber,
									 String problemSetActivationStatus) 
	{
    	debug ("generateObjectTags (CTATProblem aProblem,CTATProblemSet aSet,int problemNumber,String problemSetActivationStatus)");
    	
    	String problemSetName=aSet.getName();
    	
    	StringBuffer aPage=new StringBuffer ();
    	
    	aPage.append("<script>\n");

    	if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPIDISABLED)
    	{	
    		aPage.append("\nvar mobileAPI=new CTATMobileTutorHandler (\"swfFile\",\"disabled\");\n\n");
    	}    	
    	
    	if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPION)
    	{	
    		aPage.append("\nvar mobileAPI=new CTATMobileTutorHandler (\"swfFile\",\"on\");\n\n");
    	}
    	
    	if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPIAUTO)
    	{	
    		aPage.append("\nvar mobileAPI=new CTATMobileTutorHandler (\"swfFile\",\"auto\");\n\n");
    	}    	
    	
        aPage.append("var FlashVars=\n");
        aPage.append("{\n");
        
        if (this.getSCORMCompliant()==true)
        {
            aPage.append("	scorm_enabled:\"true\",\n");
        }
        
        aPage.append("	admit_code:\"ies\",\n");
        aPage.append("	authenticity_token:\"none\",\n");
        aPage.append("	auth_token:\"none\",\n");
        aPage.append("	BehaviorRecorderMode:\"'AuthorTimeTutoring\",\n");
        aPage.append("	class_name:\"'Default Class\",\n");

		aPage.append("	curriculum_service_url:\"http://"+CTATLink.hostName+":"+CTATLink.wwwPort+"/process_student_assignment/"+problemSetName+"/"+problemNumber+"\",\n");

        aPage.append("	dataset_level_name1:\""+aProblem.problem_path+"\",\n");
        aPage.append("	dataset_level_type1:\"ProblemSet\",\n");
        aPage.append("	dataset_name:\""+CTATLink.datasetName+"\",\n");
        aPage.append("	expire_logout_url:\"none\",\n");

    	if (this.getIncludeMenu()==true)        
    		aPage.append("	info:\""+aSet.buildMenuHTML()+"\",\n");

        aPage.append("	instructor_name:\"none\",\n");
        aPage.append("	instrumentation_log:\"off\",\n");
        aPage.append("	lcId:\""+System.currentTimeMillis ()+"\",\n");

    	if (CTATLink.allowWriting==false)
        	aPage.append("	Logging:\"None\", // 'ClientToService' or 'ClientToLogServer'\n");    	
    	else
        	aPage.append("	Logging:\"ClientToService\", // 'ClientToService' or 'ClientToLogServer'\n");

        aPage.append("	log_service_url:\""+CTATLink.datashopURL+"\",\n");
        aPage.append("	problem_name:\""+aProblem.name+"\",\n");
        aPage.append("	problem_position:\"none\",\n");
        aPage.append("	problem_started_url:\"none\",\n");
        aPage.append("	problem_state_status:\"empty\", //  'empty', 'complete', or 'incomplete'\n");

        /*
		String qf="";

		if(!CTATLink.remoteHost.equals("") || !CTATLink.remoteHost.equals("local"))
			qf=("http://localhost:"+CTATLink.wwwPort+"/"+aProblem.problem_path+"FinalBRDs/"+aProblem.problem_file);
		else
			qf=(CTATLink.htdocs+"/"+aProblem.problem_path+"FinalBRDs/"+aProblem.problem_file);

        aPage.append("	question_file:\""+qf+"\",\n");
        */
        
        aPage.append("	question_file:\"http://"+CTATLink.hostName+":"+CTATLink.wwwPort+"/remoteBRDs/tutors/problem_sets/"+problemSetActivationStatus+"/"+aSet.getDirectory()+"/FinalBRDs/"+aProblem.problem_file+"\",\n");
                
        aPage.append("	refresh_session_url:\"none\",\n");
        aPage.append("	remoteSocketPort:\""+CTATLink.tsPort+"\",\n");
        aPage.append("	remoteSocketURL:\"127.0.0.1\",\n");
        aPage.append("	restore_problem_url:\"none\",\n");
        
		if (CTATLink.deployType==CTATLink.DEPLOYHTML5)
		{
			aPage.append("	reuse_swf:\"false\",\n");
		}
		else
		{
			aPage.append("	reuse_swf:\""+CTATLink.reuseSWF+"\",\n");
		}
		
        aPage.append("	run_problem_url:\"http://"+CTATLink.hostName+":"+CTATLink.wwwPort+"/process_student_assignment/"+problemSetName+"/"+problemNumber+"\",\n");
        aPage.append("	school_name:\"none\",\n");
        aPage.append("	SessionLog:\"true\",\n");

    	if (this.getSessionID().equals("undefined")==true)
    	{
    		UUID uuid = UUID.randomUUID();
    		this.setSessionID(uuid.toString());
    	}        

        aPage.append("	session_id:\""+this.getSessionID()+"\",\n");
        aPage.append("	session_timeout:\"none\",\n");

   		try 
   		{
   			aPage.append("	skills:\""+URLEncoder.encode(/*aSet.getSkillsXML()*/aProblem.getSkillsXML(), "UTF-8")+"\",\n");
   		} 
   		catch (UnsupportedEncodingException e) 
   		{
   			// Ignore; this should never happen because UTF-8 must be supported
   			aPage.append("	skills:\"\",\n");
   		}        

        aPage.append("	source_id:\"FLASH_PSEUDO_TUTOR\", // 'FLASH_PSEUDO_TUTOR' or 'CTAT_JAVA_TUTOR'\n");
        aPage.append("	student_interface:\"none\",\n");
        aPage.append("	student_problem_id:\"none\",\n");
        aPage.append("	study_condition_name:\"none\",\n");
        aPage.append("	study_condition_type:\"none\",\n");
        aPage.append("	study_conditon_description:\"none\",\n");
        aPage.append("	study_name:\"Study1\",\n");
        aPage.append("	target_frame:\"none\",\n");
        aPage.append("	TutorShopDeliveryMethod:\"sendandload\",\n");
        aPage.append("	user_guid:\""+CTATLink.userID+"\",\n");
        aPage.append("	wmode:\"opaque\",\n");
        aPage.append("	log_to_disk_directory:\"none\",\n");
        aPage.append("	DeliverUsingOLI:\"none\"\n");
        aPage.append("};\n");

        aPage.append("</script>\n");

		aPage.append("<div id=\"container\" name=\"container\" class=\"container\">\n");
		aPage.append("<canvas id=\"main-canvas\" width=\"320\" height=\"200\">Your browser does not support CTAT. Please update or replace your browser.</canvas>\n");
		aPage.append("</div>\n");

		aPage.append("<script>prepTutorArea ();</script>\n");

    	return aPage.toString ();
	}
	/**
	 * 
	 */
	@Override
	public String generateObjectTags(File aSWF, File aBRD) 
	{
    	debug ("generateObjectTags (File aSWF, File aBRD)");
    	    	
    	StringBuffer aPage=new StringBuffer ();
    	
    	aPage.append("<script>\n");

    	if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPIDISABLED)
    	{	
    		aPage.append("\nvar mobileAPI=new CTATMobileTutorHandler (\"swfFile\",\"disabled\");\n\n");
    	}    	
    	
    	if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPION)
    	{	
    		aPage.append("\nvar mobileAPI=new CTATMobileTutorHandler (\"swfFile\",\"on\");\n\n");
    	}
    	
    	if (CTATLink.deployMobileAPI==CTATLink.MOBILEAPIAUTO)
    	{	
    		aPage.append("\nvar mobileAPI=new CTATMobileTutorHandler (\"swfFile\",\"auto\");\n\n");
    	}    	
    	
        aPage.append("var FlashVars=\n");
        aPage.append("{\n");
        
        if (this.getSCORMCompliant()==true)
        {
            aPage.append("	scorm_enabled:\"true\",\n");
        }        
        
        aPage.append("	admit_code:\"ies\",\n");
        aPage.append("	authenticity_token:\"none\",\n");
        aPage.append("	auth_token:\"none\",\n");
        aPage.append("	BehaviorRecorderMode:\"'AuthorTimeTutoring\",\n");
        aPage.append("	class_name:\"'Default Class\",\n");

		aPage.append("	curriculum_service_url:\"scorm\",\n");

        aPage.append("	dataset_level_name1:\"ProblemSet\",\n");
        aPage.append("	dataset_level_type1:\"ProblemSet\",\n");
        aPage.append("	dataset_name:\""+CTATLink.datasetName+"\",\n");
        aPage.append("	expire_logout_url:\"none\",\n");

        aPage.append("	instructor_name:\"none\",\n");
        aPage.append("	instrumentation_log:\"off\",\n");
        aPage.append("	lcId:\""+System.currentTimeMillis ()+"\",\n");

    	if (CTATLink.allowWriting==false)
        	aPage.append("	Logging:\"None\", // 'ClientToService' or 'ClientToLogServer'\n");    	
    	else
        	aPage.append("	Logging:\"ClientToService\", // 'ClientToService' or 'ClientToLogServer'\n");

        aPage.append("	log_service_url:\""+CTATLink.datashopURL+"\",\n");
        aPage.append("	problem_name:\"none\",\n");
        aPage.append("	problem_position:\"none\",\n");
        aPage.append("	problem_started_url:\"none\",\n");
        aPage.append("	problem_state_status:\"empty\", //  'empty', 'complete', or 'incomplete'\n");
        
        aPage.append("	question_file:\""+aBRD.getName()+"\",\n");
                
        aPage.append("	refresh_session_url:\"none\",\n");
        aPage.append("	remoteSocketPort:\""+CTATLink.tsPort+"\",\n");
        aPage.append("	remoteSocketURL:\"127.0.0.1\",\n");
        aPage.append("	restore_problem_url:\"none\",\n");
        
		if (CTATLink.deployType==CTATLink.DEPLOYHTML5)
		{
			aPage.append("	reuse_swf:\"false\",\n");
		}
		else
		{
			aPage.append("	reuse_swf:\""+CTATLink.reuseSWF+"\",\n");
		}
		
        //aPage.append("	run_problem_url:\"http://"+CTATLink.hostName+":"+CTATLink.wwwPort+"/process_student_assignment/"+problemSetName+"/"+problemNumber+"\",\n");
        aPage.append("	school_name:\"none\",\n");
        aPage.append("	SessionLog:\"true\",\n");

    	if (this.getSessionID().equals("undefined")==true)
    	{
    		UUID uuid = UUID.randomUUID();
    		this.setSessionID(uuid.toString());
    	}        

        aPage.append("	session_id:\""+this.getSessionID()+"\",\n");
        aPage.append("	session_timeout:\"none\",\n");

        /*
   		try 
   		{
   			aPage.append("	skills:\""+URLEncoder.encode(aProblem.getSkillsXML(), "UTF-8")+"\",\n");
   		} 
   		catch (UnsupportedEncodingException e) 
   		{
   			// Ignore; this should never happen because UTF-8 must be supported
   			aPage.append("	skills:\"\",\n");
   		} 
   		*/       

        aPage.append("	source_id:\"FLASH_PSEUDO_TUTOR\", // 'FLASH_PSEUDO_TUTOR' or 'CTAT_JAVA_TUTOR'\n");
        aPage.append("	student_interface:\"none\",\n");
        aPage.append("	student_problem_id:\"none\",\n");
        aPage.append("	study_condition_name:\"none\",\n");
        aPage.append("	study_condition_type:\"none\",\n");
        aPage.append("	study_conditon_description:\"none\",\n");
        aPage.append("	study_name:\"Study1\",\n");
        aPage.append("	target_frame:\"none\",\n");
        aPage.append("	TutorShopDeliveryMethod:\"sendandload\",\n");
        aPage.append("	user_guid:\""+CTATLink.userID+"\",\n");
        aPage.append("	wmode:\"opaque\",\n");
        aPage.append("	log_to_disk_directory:\"none\",\n");
        aPage.append("	DeliverUsingOLI:\"none\"\n");
        aPage.append("};\n");

        aPage.append("</script>\n");

		aPage.append("<div id=\"container\" name=\"container\" class=\"container\">\n");
		aPage.append("<canvas id=\"main-canvas\" width=\"320\" height=\"200\">Your browser does not support CTAT. Please update or replace your browser.</canvas>\n");
		aPage.append("</div>\n");

		aPage.append("<script>prepTutorArea ();</script>\n");

    	return aPage.toString ();
	}	
}
