/**------------------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 
 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
*/

/**
 * CTATCurriculumService handles interaction with Curriculum Services, such as TutorShop.
 * <p>TutorShop is the primary Curriculum Service that CTAT is used with, as such the 
 * problem-to-problem protocol defined here is modeled after the specifications expected 
 * by TutorShop. It should be made clear that this class does not represent a 
 * CurriculumService in itself but the way in which CTAT interfaces with one.</p>
 */
function CTATCurriculumService (aCommLibrary)
{
	CTATBase.call(this, "CTATCurriculumService","curriculum_service");
	
	var commLibrary=aCommLibrary;
	var variables=new Array ();
	var pointer = this;
	
	/**
	 * 
	 */
	this.reset=function reset ()
	{
		variables=new Array ();
	};
	/**
	 * 
	 */
	this.addVariable=function addVariable (aName,aValue)
	{
		var variable=new CTATVariable ();
		variable.name=aName;
		variable.value=aValue;
		
		variables.push (variable);
	};
	/**
	 * Sends a problem summary to the Curriculum Service and initiates the problem-to-problem protocol.
	 * <p>The specifics of CTAT's behavior during the problem-to-problem protocol are defined by FlashVars provided by the
	 * Curriculum Service. If <code>reuse_swf</code> is set to <code>true</code> then the interface will reset itself and
	 * make a URLRequest to the <code>curriculum_service_url</code> for new flashVars. If <code>reuse_swf</code> is <code>false</code>
	 * then flash will navigate to a new URL to load the next problem.</p>
	 * @param	problemSummary	A ProblemSummary message received from the Tutoring Service.
	 */
	this.sendSummary=function sendSummary (problemSummary) 
	{			
		pointer.debug("Forwarding Summary to tutorshop: " + problemSummary);
						
		ctatscrim.nextProblemScrimUp ();
							
		var vars=flashVars.getRawFlashVars ();
		
		var url=vars ['curriculum_service_url'];
		
		variables=new Array ();

		this.addVariable ('user_guid',vars ['user_id']);
		this.addVariable ('session_id',vars ['session_id']);
		this.addVariable ('authenticity_token',vars ['authenticity_token']);
		this.addVariable ('school_name',vars ['school_name']);
		this.addVariable ('summary',encodeURIComponent(problemSummary));
		this.addVariable ('problem_state',""); // This will come later
		
		var targetFrame=vars ['target_frame'];
		var reuseSWF=vars ['reuse_swf'];
		
		this.addVariable ('targetFrame',targetFrame);
		this.addVariable ('reuseSWF',reuseSWF);
		
		pointer.debug ("TargetFrame = " + targetFrame + " reuseSwf " + reuseSWF);
		
		commLibrary.send_post_variables (url,variables);
	};

	/**
	 * 
	 */
	/*
	this.downloadComplete=function downloadComplete(event)
	{
		var data;
	
		pointer.debug("Download Complete");
		pointer.debug("Loader data : " + connectionManager.getProblemSummaryData());
		pointer.debug("loader data_format " + connectionManager.getProblemSummaryDataFormat());
			
		if(CTATLinkData.commShell.inSaveAndQuitMode)
		{
			CTATLinkData.commShell.exitTutor();
			return;
		}
			
		//if data is a URLVariables object then use it as one
		if (connectionManager.getProblemSummaryDataFormat() == URLLoaderDataFormat.VARIABLES) 
		{
			data = connectionManager.getProblemSummaryData();
		}
		//If data is not a URLVariables object then effectively make it one.
		else if (connectionManager.getProblemSummaryDataFormat() == URLLoaderDataFormat.TEXT) 
		{
			data = { };
			var tempArr:Array = connectionManager.getProblemSummaryData().split("&");
			
			for each (var variable_assignment:String in tempArr) 
			{
				//var pair:Array = variable.split("=");
				var delim:int = variable_assignment.indexOf("=");
				var variableName:String = variable_assignment.substring(0,delim);
				var variableValue:String = variable_assignment.substring(delim+1);
			
				pointer.debug("Flashvar encoded = " + variableValue);
				
				variableValue = unescape(variableValue.replace(/\+/g, " "));//First convert plus signs to space, then unescape
				
				pointer.debug("Flashvar decoded = " + variableValue);
				
				data[variableName] = variableValue;
			}
		}
		else 
		{
			pointer.debug("CTATCurriculumService has no idea what to do with binary data");
			return;
		}
			
		pointer.debug("reuse_swf access = " + CTATLinkData.flashVars.reuse_swf);
		pointer.debug("reuse_swf getFlashVar = " + CTATLinkData.flashVars.getFlashVar("reuse_swf"));
		pointer.debug("flashVars.student_interface = " + CTATLinkData.flashVars.student_interface);
		pointer.debug("data.student_interface = " + data.student_interface);
			
		//IF we aren't reuseing_swf, or current.student_interface != downloaded.student_interface, navigateTo a new URL
		//ELSE reset the current interface.
		if (!CTATLinkData.flashVars.reuse_swf || (CTATLinkData.flashVars.student_interface != data.student_interface)) 
		{
			//load the new swf
			var target_frame:String = (CTATLinkData.flashVars.checkIsValid("target_frame") ? CTATLinkData.flashVars.target_frame : "_self");

			//We are supposed to use run_problem_url, but older versions of tutorshop might
			//not be updated and will accept a second request to curriculum_service_url.
			var url:String = (CTATLinkData.flashVars.checkIsValid("run_problem_url") ? CTATLinkData.flashVars.run_problem_url : CTATLinkData.flashVars.curriculum_service_url);
				
			pointer.debug(" url is " + url);
				
			//We don't need to send any extra data for this request.
			var request:URLRequest = new URLRequest(url);
			request.method = "POST";
				
			try 
			{
				//to_be_fixed. We should use target_frame here, rather then self.
				//Also, we should lock the interface incase reuse_swf is false.
				
				pointer.debug("Target_frame = " + target_frame);
				
				if(target_frame != null)
					navigateToURL(request, target_frame);
				else
					navigateToURL(request, "_self");
			}
			catch(err) 
			{
				pointer.debug("ERROR " + err);
			}
		}
		else 
		{
			CTATLinkData.scrim.scrimUp ("Loading the Next Problem...");

			//update the flashVars
			CTATLinkData.flashVars.updateFlashVars(data);
				
			//Updating the number-bar outside the SWF on tutorshop
			pointer.debug ("Info is : " + CTATLinkData.flashVars.info);
			
			if (CTATLinkData.flashVars.checkIsValid("info"))
			{
				pointer.debug("Valid info... calling javascript");
				
				//ExternalInterface.call("javaScriptInfo('"+CTATLinkData.flashVars.info+"')");
				
				pointer.debug("success.. I think?");
			}
				
			this.resetInterface();
		}
	};
	*/
		
	/**
	 * Note the type of the param is Object because it may be a URLVariables object, but it 
	 * also might be an Associative Array, i.e. Object
	 * @param	variables
	 */
	this.updateFlashVars=function updateFlashVars(variables) 
	{
		pointer.debug("UpdateFlashVars ()");
		
		/*
		CTATLinkData.flashVars.updateFlashVars(variables);
		
		var xml:XML = new XML(decodeURIComponent(variables.skills));
		
		if(xml.skill != undefined)
				CTATLinkData.skills.fromXMLString(xml.skill);
		
		CTATLinkData.hints.resetHints();
		*/
	};
		
	/**
	 * This is a hard reset and should be used carefully. It will bring the
	 * interface back to the state where it was when it was first loaded.
	 */		
	this.resetInterface=function resetInterface() 
	{
		pointer.debug ("resetInterface ()");
			
		// Reset the message tank ...
			
		pointer.debug ("Resetting message tank ...");
		
		/*
		CTATLinkData.messageHistory=new CTATMessageHistory ();
		*/
						
		// Call reset on every component ...
			
		pointer.debug ("Calling reset on every component ...");
			
		for (var i=0;i<this.components.length;i++)
		{
			var aComponent=this.components [i]; // CTATStyle
			
			aComponent.reset ();
		}	
			
		pointer.debug ("Resetting feedback components ...");
			
		// This is partially to confirm that everything has been properly reset 
		// and also confirms that the hint window gets properly reset.
		
		for (var j=0;j<this.feedbackComponents.length;j++)
		{
			var aFeedbackComponent=this.feedbackComponents [j]; // CTATStyle
			
			aFeedbackComponent.reset ();
		}	
					
		// And finally reset the commshell itself ...
			
		pointer.debug ("Resetting the commshell ...");

		if (commShell!=null)
		{
			commShell.reset();
		}	
	};
		
	/**
	 * This code is borrowed from the adobe help reference on navigateToURL() it is used to validate a URL before
	 * calling navigatToURL
	 * AS3 Regular expression pattern match for URLs that start with http:// and https:// plus your domain name.
	 * @param	flashVarURL
	 * @return
	 */
	this.checkProtocol=function checkProtocol (flashVarURL) 
	{
		if ((flashVarURL == "localhost") || (flashVarURL == "127.0.0.1"))
			return true;
		
		/*
		// Get the domain name for the SWF if it is not known at compile time.
		// If the domain is known at compile time, then the following two lines can be replaced with a hard coded string.
		var my_lc:LocalConnection = new LocalConnection();
		var domainName:String = my_lc.domain;
		*/
		
		// Build the RegEx to test the URL.
		// This RegEx assumes that there is at least one "/" after the
		// domain. http://www.mysite.com will not match.
		
		var pattern=new RegExp("^http[s]?\:\\/\\/([^\\/]+)\\/");
		var result=pattern.exec(flashVarURL);
		
		if (result == null || flashVarURL.length >= 4096) 
		{
			return (false);
		}
		
		return (true);
	};
}

CTATCurriculumService.prototype = Object.create(CTATBase.prototype);
CTATCurriculumService.prototype.constructor = CTATCurriculumService;
