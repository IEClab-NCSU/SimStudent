/**-----------------------------------------------------------------------------
 $Author: vvelsen $
 $Date: 2016-11-08 12:58:28 -0600 (週二, 08 十一月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATCommunication/CTATLoggingLibrary.js $
 $Revision: 24364 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 For OLI, make sure you use the correct session id, otherwise the server will respond with:

	status=failure
	cause=NOT_AUTHENTICATED
	message=com.beginmind.login.service.InvalidSessionException: invalid session token

 */
goog.provide('CTATLoggingLibrary');

goog.require('CTATBase');
goog.require('CTATGlobals');
goog.require('CTATGuid');
goog.require('CTATFlashVars');
goog.require('CTATCommLibrary');
goog.require('CTATLogMessageBuilder');
goog.require('CTATCustomLogElementObject');
goog.require('CTATLanguageManager');
goog.require('CTATLMS');

var loggingDisabled=false; // Be very careful with this flag, it will do a hard disable on logging!

/**
 * 
 */
CTATLoggingLibrary = function(anInternalUsage)
{
	CTATBase.call(this, "CTATLoggingLibrary", "logginglibrary");

	var pointer=this;

	// The current version of this LoggingLibrary.
	var version="3.Beta";

	// The version of the DataShop DTD specification that this LoggingLibrary conforms with.
	var DTDVersion="4";

	// I just copy pasted this off of DataShop's website, its probably right but it may not even be necessary to include.
	var nameSpace="xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:noNamespaceSchemaLocation='http://learnlab.web.cmu.edu/dtd/tutor_message_v4.xsd'";

	// The standard XMLProlog, we actually reference this a lot.
	var xmlProlog='<?xml version="1.0" encoding="UTF-8"?>';

	var useSessionLog=true;
	var useInternal=false;
	var useInternalConfigured=false;
	var useForcedSessionID="";
	var useVars=[];

	var logclassname="undefined";
	var school="undefined";
	var period="undefined";
	var description="undefined";
	var instructor="undefined";
	var problem_name="undefined";
	var problem_context="undefined";
	var userid="undefined";
	var datasetName="UnassignedDataset";
	var datasetLevelName="UnassignedLevelName";
	var datasetLevelType="UnassignedLevelType";

	var logListener=null;
	
	var lastSAI=null;
	
	//var gen=new CTATGuid ();
	userid=CTATGuid.guid ();

	if ((anInternalUsage!=undefined) && (anInternalUsage!=null))
	{
		useInternal=anInternalUsage;

		if (useInternal==false)
		{
			CTATLogMessageBuilder.commLogMessageBuilder=new CTATLogMessageBuilder ();
		}
	}
	else
	{
		CTATLogMessageBuilder.commLogMessageBuilder=new CTATLogMessageBuilder ();
	}

	var loggingCommLibrary=new CTATCommLibrary ();
	loggingCommLibrary.setName ("commLoggingLibrary");
	loggingCommLibrary.setUseCommSettings (false);
	loggingCommLibrary.setConnectionRefusedMessage ("ERROR_CONN_LS");
	loggingCommLibrary.assignHandler (this);

	/**
	*
	*/
	this.getLastSAI=function getLastSAI ()
	{
		return (lastSAI);
	};
	
	/**
	*
	*/
	this.assignLogListener=function assignLogListener (aListener)
	{
		logListener=aListener;
	};

	/**
	*
	*/
	this.generateSession=function generateSession ()
	{
		//var generator=new CTATGuid ();

		useVars ['session_id']=("ctat_session_"+CTATGuid.guid ());

		return (useVars ['session_id']);
	};
	/**
	*
	*/
	this.setLogClassName=function setLogClassName (aValue)
	{
		logclassname=aValue;
	};

	/**
	*
	*/
	this.setDatasetName=function setDatasetName (aValue)
	{
		datasetName=aValue;
	};

	/**
	*
	*/
	this.setDatasetLevelName=function setdDtasetLevelName (aValue)
	{
		datasetLevelName=aValue;
	};

	/**
	*
	*/
	this.setDatasetLevelType=function setDatasetLevelType (aValue)
	{
		datasetLevelType=aValue;
	};

	/**
	*
	*/
	this.setSchool=function setSchool (aValue)
	{
		school=aValue;
	};

	/**
	*
	*/
	this.setPeriod=function setPeriod (aValue)
	{
		period=aValue;
	};

	/**
	*
	*/
	this.setDescription=function setDescription (aValue)
	{
		description=aValue;
	};

	/**
	*
	*/
	this.setInstructor=function setInstructor (aValue)
	{
		instructor=aValue;
	};


	/**
	*
	*/
	this.setProblemName=function setProblemName (aValue)
	{
		problem_name=aValue;
	};

	/**
	*
	*/
	this.setProblemContext=function setProblemContext (aValue)
	{
		problem_context=aValue;
	};

	/**
	*
	*/
	this.setUserID=function setUserID (aValue)
	{
		userid=aValue;
	};

	/**
	*
	*/
	this.getLoggingCommLibrary=function getLoggingCommLibrary ()
	{
		return (loggingCommLibrary);
	};
	/**
	*
	*/
	this.setUseSessionLog=function setUseSessionLog (aValue)
	{
		useSessionLog=aValue;
	};
	/**
	*
	*/
	this.setLoggingURL=function setLoggingURL (aURL)
	{
		pointer.getLoggingCommLibrary ().setFixedURL (aURL);
	};
	/**
	*
	*/
	this.getSessionIdentifierBundle=function getSessionIdentifierBundle()
	{
		var aBundle=[];
		aBundle ['class_name']=logclassname;
		aBundle ['school_name']=school;
		aBundle ['period_name']=period;
		aBundle ['class_description']=description;
		aBundle ['instructor_name']=instructor;
		aBundle ['dataset_name']=datasetName;

		aBundle ['problem_name']=problem_name;
		aBundle ['problem_context']=problem_context;

		aBundle ['auth_token']='';
		aBundle ['user_guid']=userid;

		aBundle ['session_id']=useVars ['session_id'];
		aBundle ['source_id']='tutor'; // Mainly for OLI

		aBundle ['dataset_level_name1']=datasetLevelName;
		aBundle ['dataset_level_type1']=datasetLevelType;
		
		return (aBundle);
	};
	/**	
	*
	*/
	this.setupExternalLibraryUsage=function setupExternalLibraryUsage ()
	{
		pointer.ctatdebug ("setupExternalLibraryUsage ()");
			
		useVars ['class_name']=logclassname;
		useVars ['school_name']=school;
		useVars ['period_name']=period;
		useVars ['class_description']=description;
		useVars ['instructor_name']=instructor;
		useVars ['dataset_name']=datasetName;

		useVars ['problem_name']=problem_name;
		useVars ['problem_context']=problem_context;

		useVars ['auth_token']='';
		useVars ['user_guid']=userid;

		//var generator=new CTATGuid ();

		useVars ['session_id']=("ctat_session_"+CTATGuid.guid ());
		useVars ['source_id']='tutor'; // Mainly for OLI

		useVars ['dataset_level_name1']=datasetLevelName;
		useVars ['dataset_level_type1']=datasetLevelType;

		/*
		if (useOLILogging==true)
		{
			useVars ['DeliverUsingOLI']='true';
		}
		else
		{
			useVars ['DeliverUsingOLI']='false';
		}
		*/

		flashVars=new CTATFlashVars ();
		flashVars.assignRawFlashVars(useVars);
	};
	/**
	*
	*/
	this.initCheck=function initCheck ()
	{
		pointer.ctatdebug ("initCheck ()");

		if (useInternal==false)
		{
			if (useInternalConfigured==false)
			{
				pointer.setupExternalLibraryUsage ();
				useInternalConfigured=true;
			}
		}
	};
	/**
	*
	*/
	this.sendMessage=function sendMessage (message)
	{
		pointer.ctatdebug ("sendMessage ()");

		//useDebugging=true;
		pointer.ctatdebug ("Raw log message to send: " + message);
		//useDebugging=false;

		this.sendMessageInternal (message);

		if (logListener!=null)
		{
			logListener (message);
		}
	};
	/**
	*
	*/
	this.sendMessageInternal=function sendMessageInternal (message)
	{
		pointer.ctatdebug ("sendMessageInternal ()");

		if (loggingDisabled===true)
		{
			pointer.ctatdebug ("Warning: loggingDisabled==true");
			return;
		}
		
		/**
			WARNING! The following code is executed every time we send a message! We
			need to find a better way to handle this.
		*/
		if (useInternal==true)
		{
			var logging = CTATConfiguration.get('Logging');
			
			if ((logging !='ClientToService') && (logging!='ClientToLogServer'))
			{
				pointer.ctatdebug ("Logging is turned off, as per: " + logging);
				return;
			}
			
			var tsc = CTATConfiguration.get('tutoring_service_communication');
			
			if ((logging=='ClientToService') && ((tsc=='http') || (tsc=='https')))
			{
				var logURL = CTATConfiguration.get('remoteSocketURL')+":"+CTATConfiguration.get('remoteSocketPort')
				pointer.setLoggingURL (logURL);

				pointer.ctatdebug ("Reconfigured the logging url to be: " + logURL);
			}

			pointer.ctatdebug ("Pre encoded log message: " + message);
			
			if (message.indexOf ("<log_session_start")<0)
			{
				message = xmlProlog + '<tutor_related_message_sequence version_number="' + DTDVersion + '">' + message + '</tutor_related_message_sequence>';
				message = CTATLogMessageBuilder.commLogMessageBuilder.wrapForOLI (message);
			}
			
			pointer.ctatdebug ("Encoded log message: " + message);
			
			CTATLMS.logEvent(message);

			if (CTATConfiguration.get('log_service_url'))
			{
				loggingCommLibrary.sendXMLNoBundle (message);
			}	
		}
		else
		{
			pointer.ctatdebug ("Use internal: " + useInternal);			
		}
	};

	/**
	 *
	 */
	this.startProblem=function startProblem ()
	{
		pointer.ctatdebug ("startProblem ()");

		pointer.initCheck ();

		//useDebugging=true;
		pointer.logSessionStart ();

		pointer.sendMessage (CTATLogMessageBuilder.commLogMessageBuilder.createContextMessage(true));

		//useDebugging=false;
	};

	/**
	*
	*/
	this.logSessionStart=function logSessionStart ()
	{
		pointer.ctatdebug ("logSessionStart ()");

		pointer.initCheck ();

		//useOLILogging=true;

		var vars=flashVars.getRawFlashVars ();

		/*
		if (useInternal==true)
		{
			var vars=flashVars.getRawFlashVars ();

			if ((vars ['DeliverUsingOLI']!=undefined) && (vars ['DeliverUsingOLI']!=null))
			{
				if (vars ['DeliverUsingOLI']=='true')
				{
					pointer.ctatdebug ('Turning useOLILogging on ...');
					useOLILogging=true;
				}
				else
				{
					pointer.ctatdebug ('Turning useOLILogging off ...');
					useOLILogging=false;
				}
			}
		}
		*/

		if (vars ['SessionLog']!=undefined)
		{
			if (vars ['SessionLog']=='false' || (typeof (vars ['SessionLog'])=='boolean' && vars ['SessionLog']===false))
			{
				pointer.ctatdebug ('Turning SessionLog off ...');
				useSessionLog=false;
			}
			else
			{
				pointer.ctatdebug ('Turning SessionLog on ...');
				useSessionLog=true;
			}
		}

		// Don't send this if we're running in OLI because the OLI environment
		// well send one for us!
		if (useSessionLog===true)
		{
			this.sendMessage (CTATLogMessageBuilder.commLogMessageBuilder.createLogSessionStart());
		}
	};

	/**
	 * @private
	 * This is the internal version of Semantic Event logging for within CTAT itself and won't be visible in the API.
	 * NOTE:We will also end up using this one for hints.
	 * @param	transactionID			A GUID for the transaction
	 * @param	sai						A CTATSAI for the action.
	 * @param	semanticEventName		A name for the Semantic Event, usually "ATTEMPT"
	 * @param	semanticEventSubtype	A subtype for the Semantic Event, commonly used to refer to tutor-performed actions
	 */
	this.logSemanticEvent=function logSemanticEvent (transactionID,
									  				 sai,
									  				 semanticEventName,
									  				 semanticEventSubtype,
									  				 aCustomFieldNames,
									  				 aCustomFieldValues,
													 aTrigger)
	{
		pointer.ctatdebug ("logSemanticEvent ("+aTrigger+")");

		pointer.initCheck ();

		lastSAI=sai;
		
		//var timeStamp = new Date(Date.UTC ());
		var timeStamp = new Date();

		CTATLogMessageBuilder.commLogMessageBuilder.resetCustomFields ();
		CTATLogMessageBuilder.commLogMessageBuilder.addCustomFields (aCustomFieldNames,aCustomFieldValues);
		CTATLogMessageBuilder.commLogMessageBuilder.addCustomField ("tool_event_time",CTATLogMessageBuilder.commLogMessageBuilder.formatTimeStamp (timeStamp) + " UTC");

		var message=CTATLogMessageBuilder.commLogMessageBuilder.createSemanticEventToolMessage (sai,
																		  transactionID,
																		  semanticEventName,
																		  semanticEventSubtype,
																		  true,
																		  aTrigger);
		this.sendMessage (message);
	};
	/**
	 * @private
	 * This is the internal method to send a tutor_message. It will not show up in the external API.
	 * NOTE: we will end up using this for hints as well, though you can use one of the public version as well.
	 * @param	transactionID
	 * @param	sai
	 * @param	anEval
	 * @param	feedBack
	 * @param	aSkillObject
	 */
	this.logTutorResponse=function logTutorResponse (transactionID,
							  						 sai,
							  						 semanticName,
							  						 semanticSubtype,
							  						 anEval,
							  						 feedBack,
							  						 aSkillObject,
									  				 aCustomFieldNames,
									  				 aCustomFieldValues)
	{
		pointer.ctatdebug("logTutorResponse ()");

		pointer.initCheck ();

		lastSAI=sai;

		var timeStamp =  new Date();

		CTATLogMessageBuilder.commLogMessageBuilder.resetCustomFields ();
		CTATLogMessageBuilder.commLogMessageBuilder.addCustomFields (aCustomFieldNames,aCustomFieldValues);
		CTATLogMessageBuilder.commLogMessageBuilder.addCustomField ("tutor_event_time",CTATLogMessageBuilder.commLogMessageBuilder.formatTimeStamp (timeStamp) + " UTC");

		pointer.ctatdebug("Formatting feedback ...");

		var formattedFeedback="";

		if ((feedBack!=undefined) && (feedBack!=null))
		{
			var preFeedback=CTATGlobals.languageManager.filterString (feedBack);

			/*
			if ((preFeedback.indexOf("'")!=-1) ||
				(preFeedback.indexOf("\"")!=-1) ||
				(preFeedback.indexOf("<")!=-1) ||
				(preFeedback.indexOf(">")!=-1) ||
				(preFeedback.indexOf("&")!=-1))
			{
				pointer.ctatdebug("Feedback message contains invalid characters, wrapping in CDATA ...");
				formattedFeedback="<![CDATA["+preFeedback+"]]>";
			}
			else
			{
				pointer.ctatdebug("Feedback message doesn't contain any invalid characters, using as-is");
				formattedFeedback=preFeedback;
			}
			*/

			formattedFeedback="<![CDATA["+preFeedback+"]]>";
		}
		else
		{
			pointer.ctatdebug("No feedback provided, using empty string");
			formattedFeedback="";
		}

		pointer.ctatdebug("Creating tutor message ...");

		var message=CTATLogMessageBuilder.commLogMessageBuilder.createTutorMessage (sai,
															  transactionID,
															  semanticName,
															  anEval,
															  formattedFeedback,
															  semanticSubtype,
															  aSkillObject,
															  true);
		this.sendMessage (message);
	};

	/**
	*
	*/
	this.processMessage=function processMessage (aMessage)
	{
		pointer.ctatdebug("processMessage ()");

		pointer.ctatdebug("Response from log server: " + aMessage);
	};

	//------------------------------------------------------------------------------
	// Public API methods start here
	//------------------------------------------------------------------------------

	/**
	* Part of the public API. Use this function to both start a session and indicate
	* to a log server that the user has started working on a problem. The methods
	* called in this convenience method can also be called separately
	*/
	this.start=function start ()
	{
		pointer.ctatdebug ("start ()");

		var sessionTag=pointer.generateSession ();
		pointer.startProblem ();

		//CTATScrim.scrim.scrimDown (); // Just in case

		return (sessionTag);
	};

	/**
	* Convenience function for the public API, not used by the CTAT library itself. 
	* DO NOT REMOVE THIS METHOD. We depend on it for testing
	*/
	this.logInterfaceAttempt=function logInterfaceAttempt (aSelection,anAction,anInput,aCustomElementObject)
	{
		pointer.ctatdebug ("logInterfaceAttempt ()");

		//var generator=new CTATGuid ();

		var transactionID = CTATGuid.guid ();

		var sai=new CTATSAI (aSelection,anAction,anInput);
		sai.setInput (anInput);
		
		lastSAI=sai;

		this.logSemanticEvent (transactionID,sai,"ATTEMPT","");

		return (transactionID);
	};

	/**
	* Convenience function for the public API, not used by the CTAT library itself. 
	* DO NOT REMOVE THIS METHOD. We depend on it for testing
	*/
	this.logInterfaceAttemptSAI=function logInterfaceAttemptSAI (anSAI,aCustomElementObject)
	{
		pointer.ctatdebug ("logInterfaceAttemptSAI ()");

		lastSAI=anSAI;
		
		var transactionID = CTATGuid.guid ();

		this.logSemanticEvent (transactionID,anSAI,"ATTEMPT","");

		return (transactionID);
	};

	/**
	* Convenience function for the public API, not used by the CTAT library itself. Be
	* careful using this function since it might not be actively maintained!
	* DO NOT REMOVE THIS METHOD. We depend on it for testing
	*/
	this.logResponse=function logResponse (transactionID,
							  			   aSelection,anAction,anInput,
							  			   semanticName,
							  			   anEvaluation,
										   anAdvice,
										   aCustomElementObject)
	{
		pointer.ctatdebug ("logResponse ()");

		var sai=new CTATSAI (aSelection,anAction,anInput);
		sai.setInput (anInput);

		var evalObj=new CTATActionEvaluationData("");
		evalObj.setEvaluation (anEvaluation);

		if (aCustomElementObject==undefined)
		{
			this.logTutorResponse (transactionID,
								   sai,
								   semanticName,
								   "",
								   evalObj,
								   anAdvice);
		}
		else
		{
			this.logTutorResponse (transactionID,
								   sai,
								   semanticName,
								   "",
								   evalObj,
								   anAdvice,
								   null, // Skills object
								   aCustomElementObject.getCustomElementNames (),
								   aCustomElementObject.getCustomElementTypes ());
		}
	};
	/**
	* Convenience function for the public API, not used by the CTAT library itself. Be
	* careful using this function since it might not be actively maintained!
	* DO NOT REMOVE THIS METHOD. We depend on it for testing	
	*/
	this.logResponseSAI=function logResponseSAI (transactionID,
												 anSAI,
												 semanticName,
												 anEvaluation,
												 anAdvice,
												 aCustomElementObject)
	{
		pointer.ctatdebug ("logResponse ()");

		var evalObj=new CTATActionEvaluationData("");
		evalObj.setEvaluation (anEvaluation);

		if (aCustomElementObject==undefined)
		{
			this.logTutorResponse (transactionID,
								   anSAI,
								   semanticName,
								   "",
								   evalObj,
								   anAdvice);
		}
		else
		{
			this.logTutorResponse (transactionID,
								   anSAI,
								   semanticName,
								   "",
								   evalObj,
								   anAdvice,
								   null, // Skills object
								   aCustomElementObject.getCustomElementNames (),
								   aCustomElementObject.getCustomElementTypes ());
		}
	};

	/**
	*
	*/
	this.endSession=function endSession ()
	{
		this.generateSession (); // just in case
	};
};

CTATLoggingLibrary.prototype = Object.create(CTATBase.prototype);
CTATLoggingLibrary.prototype.constructor = CTATLoggingLibrary;
