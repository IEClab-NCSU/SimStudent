/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-10-30 13:39:04 -0400 (Wed, 30 Oct 2013) $ 
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
 $Revision: 19672 $ 
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
//import java.io.InputStream;

//import com.brooksandrus.utils.swf.SWFHeader;

/** 
 *
 */
public class CTATFlashDriver extends CTATObjectTagDriver
{    		    			
	/**
	 *
	 */
    public CTATFlashDriver () 
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
    	
    	debug ("Examining flash file: "+(CTATLink.htdocs+aProblem.problem_path+"Flash/"+aProblem.student_interface));
    	    	
    	CTATSWFObject swfInfo=CTATSWFObject.getInfo(CTATLink.htdocs+aProblem.problem_path+"Flash/"+aProblem.student_interface);
			
    	String flashVarString=generateFlashVars (aProblem,aSet);
    	
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
    	
        aPage.append("</script>\n");
    	
    	aPage.append("<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" "); 
    	aPage.append("codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0\" "); 
    	aPage.append("width=\""+swfInfo.width+"\" height=\""+swfInfo.height+"\" >"); 
    	aPage.append("<param name=\"movie\" value=\""+aProblem.problem_path+"Flash/"+aProblem.student_interface+"\" />"); 
    	aPage.append("<param name=\"quality\" value=\"high\" /> ");
    	aPage.append("<param name=\"SeamlessTabbing\" value=\"false\" /> ");    	
    	aPage.append("<param name=\"bgcolor\" value=\"#ffffff\" /> ");
    	aPage.append("<param name=\"allowScriptAccess\" value=\"always\" /> ");    	
    	aPage.append("<param name=\"flashVars\" value=\""+flashVarString+"\" /> ");    	
    	aPage.append("<param name=\"wmode\" value=\"opaque\" /> ");
    	
    	aPage.append("<embed src=\""+aProblem.problem_path+"Flash/"+aProblem.student_interface+"\" quality=\"high\" bgcolor=\"#ffffff\" ");
    	aPage.append("width=\""+swfInfo.width+"\" ");
    	aPage.append("height=\""+swfInfo.height+"\" ");
    	aPage.append("id=\"swfFile\" ");
    	aPage.append("name=\"swfFile\" ");
    	aPage.append("align=\"\" ");
    	aPage.append("SeamlessTabbing=\"false\" ");
    	aPage.append("allowScriptAccess=\"always\" ");    	
    	aPage.append("type=\"application/x-shockwave-flash\" ");
    	aPage.append("pluginspage=\"http://www.macromedia.com/go/getflashplayer\" ");
    	aPage.append("flashVars=\""+flashVarString+"\"");    	
    	aPage.append("wmode=\"opaque\" >");
        aPage.append("</embed>"); 
        
        aPage.append("</object>");     	
    	
    	return aPage.toString ();
    }    
	/**
	 *
	 */
   public String generateObjectTags (CTATProblem aProblem,
		   							 CTATProblemSet aSet,
		   							 int problemNumber,
		   							 String problemSetActivationStatus) 
   {
   		debug ("generateObjectTags (CTATProblem aProblem,CTATProblemSet aSet,int problemNumber,String problemSetActivationStatus)");
   	
   		debug (aSet.getDirectory() + ", " + aProblem.problem_path + ", " + aProblem.getInstanceName());
   		
   		StringBuffer aPage=new StringBuffer ();
   	
   		debug ("Examining flash file: "+(CTATLink.htdocs+aProblem.problem_path+"Flash/"+aProblem.student_interface));
   	   		
  		CTATSWFObject swfInfo=CTATSWFObject.getInfo(CTATLink.htdocs+aProblem.problem_path+"Flash/"+aProblem.student_interface);
			
		String flashVarString=generateFlashVars (aProblem,aSet,problemNumber,problemSetActivationStatus);
   	
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
   	
		aPage.append("</script>\n");
   	
		aPage.append("<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" "); 
		aPage.append("codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0\" ");		
		aPage.append("width=\""+swfInfo.width+"\" height=\""+swfInfo.height+"\" >");		
		aPage.append("<param name=\"movie\" value=\""+"/tutors/problem_sets/"+problemSetActivationStatus+"/"+aSet.getDirectory()+"/Flash/"+aProblem.student_interface+"\" />"); 
		aPage.append("<param name=\"quality\" value=\"high\" /> ");
		aPage.append("<param name=\"SeamlessTabbing\" value=\"false\" /> ");    	
		aPage.append("<param name=\"bgcolor\" value=\"#ffffff\" /> ");
		aPage.append("<param name=\"allowScriptAccess\" value=\"always\" /> ");    	
		aPage.append("<param name=\"flashVars\" value=\""+flashVarString+"\" /> ");    	
		aPage.append("<param name=\"wmode\" value=\"opaque\" /> ");
   	
		aPage.append("<embed src=\""+"/tutors/problem_sets/"+problemSetActivationStatus+"/"+aSet.getDirectory()+"/Flash/"+aProblem.student_interface+"\" quality=\"high\" bgcolor=\"#ffffff\" ");
		
		aPage.append("width=\""+swfInfo.width+"\" ");
		aPage.append("height=\""+swfInfo.height+"\" ");				
		aPage.append("id=\"swfFile\" ");
		aPage.append("name=\"swfFile\" ");
		aPage.append("align=\"\" ");
		aPage.append("SeamlessTabbing=\"false\" ");
		aPage.append("allowScriptAccess=\"always\" ");    	
		aPage.append("type=\"application/x-shockwave-flash\" ");
		aPage.append("pluginspage=\"http://www.macromedia.com/go/getflashplayer\" ");
		aPage.append("flashVars=\""+flashVarString+"\"");    	
		aPage.append("wmode=\"opaque\" >");
		aPage.append("</embed>"); 
       
		aPage.append("</object>");     	
   	
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
  		
  		CTATSWFObject swfInfo=CTATSWFObject.getInfo(aSWF.getParent () + "/" + aSWF.getName());
  		
  		debug ("The dimensions of the requested SWF file are: " + swfInfo.width + "," + swfInfo.height);
  					
		String flashVarString=generateFlashVars (aSWF.getName(),aBRD.getName ());
  	
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
  	
		aPage.append("</script>\n");
  	
		aPage.append("<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" "); 
		aPage.append("codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0\" ");
		
		aPage.append("width=\""+swfInfo.width+"\" height=\""+swfInfo.height+"\" >");
		
		aPage.append("<param name=\"movie\" value=\""+aSWF.getName()+"\" />"); 
		aPage.append("<param name=\"quality\" value=\"high\" /> ");
		aPage.append("<param name=\"SeamlessTabbing\" value=\"false\" /> ");    	
		aPage.append("<param name=\"bgcolor\" value=\"#ffffff\" /> ");
		aPage.append("<param name=\"allowScriptAccess\" value=\"always\" /> ");    	
		aPage.append("<param name=\"flashVars\" value=\""+flashVarString+"\" /> ");    	
		aPage.append("<param name=\"wmode\" value=\"opaque\" /> ");
  	
		aPage.append("<embed src=\""+aSWF.getName()+"\" quality=\"high\" bgcolor=\"#ffffff\" ");		
		aPage.append("width=\""+swfInfo.width+"\" ");
		aPage.append("height=\""+swfInfo.height+"\" ");				
		aPage.append("id=\"swfFile\" ");
		aPage.append("name=\"swfFile\" ");
		aPage.append("align=\"\" ");
		aPage.append("SeamlessTabbing=\"false\" ");
		aPage.append("allowScriptAccess=\"always\" ");    	
		aPage.append("type=\"application/x-shockwave-flash\" ");
		aPage.append("pluginspage=\"http://www.macromedia.com/go/getflashplayer\" ");
		aPage.append("flashVars=\""+flashVarString+"\"");    	
		aPage.append("wmode=\"opaque\" >");
		aPage.append("</embed>"); 
      
		aPage.append("</object>");     	
  	
		return aPage.toString ();
   }
}
