/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATCommunication/CTATLoggingLibrary.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATLoggingLibrary');

goog.require('CTATBase');
goog.require('CTATGlobals');
/**
 *
 */
CTATLoggingLibrary = function()
{
	CTATBase.call(this, "CTATLoggingLibrary", "logginglibrary");

	var loggingDisabled=true; // Be very careful with this flag, it will do a hard disable on logging!

	var pointer=this;

	// The current version of this LoggingLibrary.
	var version="3.Beta";

	// The version of the DataShop DTD specification that this LoggingLibrary conforms with.
	var DTDVersion="4";

	// I just copy pasted this off of DataShop's website, its probably right but it may not even be necessary to include.
	var nameSpace="xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:noNamespaceSchemaLocation='http://learnlab.web.cmu.edu/dtd/tutor_message_v4.xsd'";

	// The standard XMLProlog, we actually reference this a lot.
	var xmlProlog='<?xml version="1.0" encoding="UTF-8"?>';

	var useOLILogging=true;

	/**
	 *
	 */
	this.startProblem=function startProblem ()
	{
		//useDebugging=true;

		pointer.ctatdebug ("startProblem ()");

		this.sendMessage (commLogMessageBuilder.createContextMessage(true));

		//useDebugging=false;
	};
	/**
	*
	*/
	this.sendMessage=function sendMessage (message)
	{
		//useDebugging=true;

		pointer.ctatdebug ("sendMessage ()");

		if (loggingDisabled==true)
		{
			console.log("loggingDisabled!");
			pointer.ctatdebug ("Warning: loggingDisabled==true");
			return;
		}

		var vars=flashVars.getRawFlashVars ();

		if (vars!=null)
		{

			if ((vars ['Logging']!='ClientToService') && (vars ['Logging']!='ClientToLogServer'))
			{
				// currently logging doesn't work because it goes in here
				pointer.ctatdebug ("Logging is turned off, as per: " + vars ['Logging']);
				return;
			}
		}

		message = xmlProlog + '<tutor_related_message_sequence version_number="' + DTDVersion + '">' + message + '</tutor_related_message_sequence>';

		pointer.ctatdebug ("Pre encoded log message: " + message);

		if (useOLILogging==true)
		{	
			message = commLogMessageBuilder.wrapForOLI (message);
		}

		pointer.ctatdebug ("Encoded log message: " + message);
		console.log("Trying to log " + message);
		var vars=flashVars.getRawFlashVars ();
		commLibrary.setHandler(commMessageHandler);
		commLibrary.sendXML (message);

		//useDebugging=false;
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
									  				 customFieldNames,
									  				 customFieldValues)
	{
		var timeStamp = new Date();

		commLogMessageBuilder.resetCustomFields ();
		commLogMessageBuilder.addCustomField ("tool_event_time",commLogMessageBuilder.formatTimeStamp (timeStamp));

		var message=commLogMessageBuilder.createSemanticEventToolMessage (sai,
																		  transactionID,
																		  semanticEventName,
																		  semanticEventSubtype,
																		  useOLILogging,
																		  customFieldNames,
																		  customFieldValues);
		console.log("logging semantic event")
		console.log("Logging: " + message);
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
	 * @param	skills
	 */
	this.logTutorResponse=function logTutorResponse (transactionID,
							  						 sai,
							  						 semanticName,
							  						 semanticSubtype,
							  						 anEval,
							  						 feedBack,
							  						 skills,
													 customFieldNames,
									  				 customFieldValues)
	{
		pointer.ctatdebug("CTATLoggingLibrary","logTutorResponse ()");

		var timeStamp = new Date();

		pointer.ctatdebug("CTATLoggingLibrary","Adding custom field names ...");

		commLogMessageBuilder.resetCustomFields ();
		commLogMessageBuilder.addCustomField ("tutor_event_time",commLogMessageBuilder.formatTimeStamp (timeStamp));

		pointer.ctatdebug("CTATLoggingLibrary","Formatting feedback ...");

		var formattedFeedback=feedBack;

		if ((feedBack.indexOf("'")!=-1) ||
			(feedBack.indexOf("\"")!=-1) ||
			(feedBack.indexOf("<")!=-1) ||
			(feedBack.indexOf(">")!=-1) ||
			(feedBack.indexOf("&")!=-1))
		{
			pointer.ctatdebug("CTATLoggingLibrary","Feedback message contains invalid characters, wrapping in CDATA ...");
			formattedFeedback="<![CDATA["+feedBack+"]]>";
		}
		else
			pointer.ctatdebug("CTATLoggingLibrary","Feedback message doesn't contain any invalid characters, using as-is");

		pointer.ctatdebug("CTATLoggingLibrary","Creating tutor message ...");

		var message=commLogMessageBuilder.createTutorMessage (sai,
															  transactionID,
															  semanticName,
															  anEval,
															  formattedFeedback,
															  semanticSubtype,
															  skills,
															  useOLILogging,
															  customFieldNames,
															  customFieldValues);
		console.log("logging tutor response");
		this.sendMessage (message);
	};
}

CTATLoggingLibrary.prototype = Object.create(CTATBase.prototype);
CTATLoggingLibrary.prototype.constructor = CTATLoggingLibrary;
