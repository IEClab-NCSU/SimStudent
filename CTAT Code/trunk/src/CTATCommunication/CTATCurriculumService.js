/**-----------------------------------------------------------------------------
 $Author: sewall $
 $Date: 2017-03-28 22:57:24 -0500 (週二, 28 三月 2017) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATCommunication/CTATCurriculumService.js $
 $Revision: 24681 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATCurriculumService');

goog.require('CTATBase');
goog.require('CTATGlobals');
//goog.require('CTATLinkData');
//goog.require('CTATMessageHistory');
goog.require('CTATScrim');
goog.require('CTATVariable');
/**
 * CTATCurriculumService handles interaction with Curriculum Services, such as TutorShop.
 * <p>TutorShop is the primary Curriculum Service that CTAT is used with, as such the
 * problem-to-problem protocol defined here is modeled after the specifications expected
 * by TutorShop. It should be made clear that this class does not represent a
 * CurriculumService in itself but the way in which CTAT interfaces with one.</p>
 */
CTATCurriculumService = function(aCommLibrary)
{
	CTATBase.call(this, "CTATCurriculumService","curriculum_service");

	var commLibrary=aCommLibrary;
	var variables=[];
	var pointer = this;

	/**
	 *
	 */
	this.reset=function reset ()
	{
		variables=[];
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
	this.sendSummary=function sendSummary (aMessage)
	{
		pointer.ctatdebug("sendSummary ()");

		var generator=new CTATXML ();
		var problemXML=aMessage.getXMLObject ();
		var problemRaw=problemXML.getElementsByTagName("ProblemSummary");
		var problemStr=$('<div>').html(problemRaw [0].innerHTML).text();

		var problemSummary=generator.xmlToString (problemXML);
		var vars=flashVars.getRawFlashVars ();
		var url=vars ['curriculum_service_url'];

		var subProcessor=new CTATXML ();
		var root=subProcessor.parse (problemStr);
		var complete=subProcessor.getElementAttr (root,"CompletionStatus");

		if (url)
		{
			if (complete=="complete")
			{
				CTATScrim.scrim.nextProblemScrimUp ();

				commLibrary.assignMessageListener(pointer);
				commLibrary.assignHandler(pointer);  // avoid warning from CommMessageHandler if JSON received
			}

			variables=[];

			this.addVariable ('user_guid',vars ['user_guid']);
			this.addVariable ('session_id',vars ['session_id']);
			this.addVariable ('authenticity_token',vars ['authenticity_token']);
			this.addVariable ('school_name',vars ['school_name']);
			this.addVariable ('summary',problemStr);
//			this.addVariable ('problem_state',""); This will come later

			var targetFrame=vars ['target_frame'];
			var reuseSWF=vars ['reuse_swf'];
			var runProblemUrl=vars ['run_problem_url'];
			this.addVariable ('targetFrame',targetFrame);
			this.addVariable ('reuseSWF',reuseSWF);

			pointer.ctatdebug ("CTATCurriculumService.sendSummary about to send targetFrame = " + targetFrame + ", reuseSwf " + reuseSWF + ", runProblemUrl " + runProblemUrl);

			commLibrary.send_post_variables (url,variables);
		}
		else
		{
			if (complete=="complete")
			{
			    CTATScrim.scrim.scrimUp (CTATGlobals.languageManager.getString("CONGRATULATIONS_YOU_ARE_DONE"));
			}
		}
	};

	/**
	 * Called when the commLibrary object has sent a message. No-op.
	 * @param {string} msg message just sent
	 */
	this.processOutgoing = function(msg)
	{
		pointer.ctatdebug("CTATCurriculumService.processOutgoing(\""+msg+"\")");
	};

	/**
	 * Called when the commLibrary object has received a message.
	 * Should be activated only after a Done step that will advance the problem.
	 * Calls location.replace() on the parent frame, passing the run_problem_url from flashVars.
	 * which is assumed to be HTML.
	 * @param {string} msg message just received--not used
	 */
	this.processIncoming = function(msg)
	{
		var vars=flashVars.getRawFlashVars ();
		var runProblemUrl=vars ['run_problem_url'];
		pointer.ctatdebug("CTATCurriculumService.processIncoming("+(msg ? (msg.substring(0,12)+"..."): "")+") parent.location.replace "+parent.location.replace+", runProblemUrl \""+runProblemUrl+"\"");

		if(runProblemUrl && parent && parent.location)
		{
			parent.location.replace(runProblemUrl);
		}
	};

	/**
	 * Also called when the commLibrary object has received a message. No-op: response not used.
	 * @param {string} msg message just received--not used
	 */
	this.processMessage=function processMessage(msg)
	{
		this.ctatdebug ("CTATCurriculumService.processMessage("+(msg ? (msg.substring(0,30)+"..."): "")+")");
	};

	/**
	 *
	 */
	/*
	this.downloadComplete=function downloadComplete(event)
	{
		var data;

		pointer.ctatdebug("Download Complete");
		pointer.ctatdebug("Loader data : " + connectionManager.getProblemSummaryData());
		pointer.ctatdebug("loader data_format " + connectionManager.getProblemSummaryDataFormat());

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

				pointer.ctatdebug("Flashvar encoded = " + variableValue);

				variableValue = unescape(variableValue.replace(/\+/g, " "));//First convert plus signs to space, then unescape

				pointer.ctatdebug("Flashvar decoded = " + variableValue);

				data[variableName] = variableValue;
			}
		}
		else
		{
			pointer.ctatdebug("CTATCurriculumService has no idea what to do with binary data");
			return;
		}

		pointer.ctatdebug("reuse_swf access = " + CTATLinkData.flashVars.reuse_swf);
		pointer.ctatdebug("reuse_swf getFlashVar = " + CTATLinkData.flashVars.getFlashVar("reuse_swf"));
		pointer.ctatdebug("flashVars.student_interface = " + CTATLinkData.flashVars.student_interface);
		pointer.ctatdebug("data.student_interface = " + data.student_interface);

		//IF we aren't reuseing_swf, or current.student_interface != downloaded.student_interface, navigateTo a new URL
		//ELSE reset the current interface.
		if (!CTATLinkData.flashVars.reuse_swf || (CTATLinkData.flashVars.student_interface != data.student_interface))
		{
			//load the new swf
			var target_frame:String = (CTATLinkData.flashVars.checkIsValid("target_frame") ? CTATLinkData.flashVars.target_frame : "_self");

			//We are supposed to use run_problem_url, but older versions of tutorshop might
			//not be updated and will accept a second request to curriculum_service_url.
			var url:String = (CTATLinkData.flashVars.checkIsValid("run_problem_url") ? CTATLinkData.flashVars.run_problem_url : CTATLinkData.flashVars.curriculum_service_url);

			pointer.ctatdebug(" url is " + url);

			//We don't need to send any extra data for this request.
			var request:URLRequest = new URLRequest(url);
			request.method = "POST";

			try
			{
				//to_be_fixed. We should use target_frame here, rather then self.
				//Also, we should lock the interface incase reuse_swf is false.

				pointer.ctatdebug("Target_frame = " + target_frame);

				if(target_frame != null)
					navigateToURL(request, target_frame);
				else
					navigateToURL(request, "_self");
			}
			catch(err)
			{
				pointer.ctatdebug("ERROR " + err);
			}
		}
		else
		{
			CTATLinkData.scrim.scrimUp (CTATGlobals.languageManager.getString("NEXTPROBLEM"));

			//update the flashVars
			CTATLinkData.flashVars.updateFlashVars(data);

			//Updating the number-bar outside the SWF on tutorshop
			pointer.ctatdebug ("Info is : " + CTATLinkData.flashVars.info);

			if (CTATLinkData.flashVars.checkIsValid("info"))
			{
				pointer.ctatdebug("Valid info... calling javascript");

				//ExternalInterface.call("javaScriptInfo('"+CTATLinkData.flashVars.info+"')");

				pointer.ctatdebug("success.. I think?");
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
		pointer.ctatdebug("UpdateFlashVars ()");

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
	/*this.resetInterface=function resetInterface() // unused and not working
	{
		pointer.ctatdebug ("resetInterface ()");

		// Reset the message tank ...

		pointer.ctatdebug ("Resetting message tank ...");

		//
		//CTATLinkData.messageHistory=new CTATMessageHistory ();
		//

		// Call reset on every component ...

		pointer.ctatdebug ("Calling reset on every component ...");

		for (var i=0;i<this.components.length;i++)
		{
			var aComponent=this.components [i]; // CTATStyle

			aComponent.reset ();
		}

		pointer.ctatdebug ("Resetting feedback components ...");

		// This is partially to confirm that everything has been properly reset
		// and also confirms that the hint window gets properly reset.

		for (var j=0;j<this.feedbackComponents.length;j++)
		{
			var aFeedbackComponent=this.feedbackComponents [j]; // CTATStyle

			aFeedbackComponent.reset ();
		}

		// And finally reset the commshell itself ...

		pointer.ctatdebug ("Resetting the commshell ...");

		if (commShell!=null)
		{
			commShell.reset();
		}
	};*/

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

		if (result === null || flashVarURL.length >= 4096)
		{
			return (false);
		}

		return (true);
	};
};

CTATCurriculumService.prototype = Object.create(CTATBase.prototype);
CTATCurriculumService.prototype.constructor = CTATCurriculumService;
