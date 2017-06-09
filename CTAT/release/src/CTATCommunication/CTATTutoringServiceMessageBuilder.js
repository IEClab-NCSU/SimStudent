/**-----------------------------------------------------------------------------
 $Author: sewall $
 $Date: 2016-12-01 16:36:13 -0600 (週四, 01 十二月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATCommunication/CTATTutoringServiceMessageBuilder.js $
 $Revision: 24394 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATTutoringServiceMessageBuilder');

goog.require('CTATBase');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATJSON');
goog.require('CTATMessage');
//goog.require('CTATParameter');
goog.require('CTATSkillSet');
//goog.require('CTATStyle');
goog.require('CTATXML');
/**
 *
 */
CTATTutoringServiceMessageBuilder = function()
{
	CTATBase.call(this, "CTATTutoringServiceMessageBuilder", "__undefined__");

	//var xmlHeader='<?xml version="1.0" encoding="UTF-8"?>';
	var xmlHeader='';

	/**
	*
	*/
	this.createStartProblemMessage=function createStartProblemMessage(problemName)
	{
		this.ctatdebug ("createStartProblemMessage ()");

		return (xmlHeader+"<message><verb>NotePropertySet</verb><properties><MessageType>StartProblem</MessageType><ProblemName>" + problemName + "</ProblemName></properties></message>");
	};

	/**
	 *
	 */
	this.createInterfaceIdentificationMessage=function createInterfaceIdentificationMessage (anID)
	{
		this.ctatdebug ("createInterfaceIdentificationMessage ("+anID+")");

		return (xmlHeader+"<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceIdentification</MessageType><Guid>" + anID + "</Guid></properties></message>");
	};

	/**
	* FIXME revise when really creating InterfaceDescription msgs
	*/
	this.createInterfaceDescriptionMessage=function createInterfaceDescriptionMessage(aComponent)
	{
		this.ctatdebug ("createInterfaceDescriptionMessage ()");

		message = "<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceDescription</MessageType>";
		message += "<WidgetType>"+aComponent.getClassName ()+"</WidgetType>";
		message += "<CommName>"+aComponent.getName ()+"</CommName>";
		message += "<UpdateEachCycle>false</UpdateEachCycle>";
		//message += '<jessDeftemplates><value>(deftemplate CTATCommShell (slot_name))</value></jessDeftemplates>';
		//message += '<jessInstances><value>(assert (CTATCommShell (name CTATCommShell) (value "" )))</value></jessInstances>';
		message	+= '<serialized>';

		message	+='<'+aComponent.getClassName ()+' name="'+aComponent.getName ()+'" x="0" y="0" width="50" height="30" scaleX="1" scaleY="1" originalWidth="50" originalHeight="30" zIndex="-1" tabIndex="-1">';
		message +='<Parameters><selection>';

		if ((aComponent.getClassName ()=="CTATRadioButton") || (aComponent.getClassName ()=="CTATCheckBox"))
		{
			// this is a total hack to get radio button and check box grouping information communicated to the BR

			message +='<CTATComponentParameter>';
			message +='<name>group</name>';
			message +='<value fmt="text" name="Group Name" type="String" includein="sparse">'+aComponent.getComponentGroup ()+'</value>';
			message +='</CTATComponentParameter>';
		}

		message +='</selection></Parameters>';
		message +='</'+aComponent.getClassName ()+'>';

		message += '</serialized>';
		message += "</properties></message>";

		return (message);
	};

	/**
	 * There will probably need to be more fields in this method. Builds a SetPreferences message to be sent to the tutoring service.
	 * @param	versionNum	The current CommShell version number, which should be available at CommShell.version
	 * @param	context		The CTATContextData object for the session.
	 * @param	skills		The CTATSkillSet of the current session.
	 * @param	logURL		The logServiceURL being used by CTAT
	 * @param	logMethod	The Logging method of the session, which should be available at CommShell.Logging
	 * @return	A properly formated SetPreferences message to be sent to the tutoring service.
	 */
	this.createSetPreferencesMessage=function createSetPreferencesMessage (versionNum)
	{
		this.ctatdebug ("createSetPreferencesMessage ()");

		var vars=CTATConfiguration.getRawFlashVars ();

		var message = "<message><verb>NotePropertySet</verb><properties><MessageType>SetPreferences</MessageType>";

		message += "<log_service_url>" + vars ["log_service_url"] + "</log_service_url>";

		var logRemote = false;
		var logDisk = false;

		var logMethod=vars ["Logging"];

		this.ctatdebug ("Parsing and processing logMethod: " + logMethod);

		switch (logMethod)
		{
			case "ServiceToLogServer":
				logRemote = true;
				break;
			case "ServiceToDisk":
				logDisk = true;
				break;
			case "ServiceToDiskAndLogServer":
				logRemote = true;
				logDisk = true;
				break;
			case "ClientToLogServer":
				logRemote = true;
				logDisk = true;
				break;
			default:
				break;
		}

		message += "<log_to_remote_server>" + logRemote + "</log_to_remote_server><log_to_disk>" + logDisk + "</log_to_disk>";

		message += "<log_to_disk_directory>" + vars ["log_to_disk_directory"] + "</log_to_disk_directory>";

		message += "<logging>" + vars ["Logging"] + "</logging>";

		message += "<user_guid>" + vars ["user_guid"] + "</user_guid>";

		message += "<problem_name>" + vars ["problem_name"] + "</problem_name>";

		//message += "<question_file>" + urldecode (vars ["question_file"]) + "</question_file>";
		message += "<question_file><![CDATA[" + encodeURI (vars ["question_file"]) + "]]></question_file>";

		message += "<class_name>" + vars ["class_name"] + "</class_name>";

		message += "<school_name>" + vars ["school_name"] + "</school_name>";

		message += "<instructor_name>" + vars ["instructor_name"] + "</instructor_name>";

		message += "<session_id>" + vars ["session_id"] + "</session_id>";

		message += "<source_id>" + vars ["source_id"] + "</source_id>";

		message += "<sui><![CDATA[" + vars ["sui"] + "]]></sui>";

		message += "<problem_state_status>" + vars ["problem_state_status"] + "</problem_state_status>";

		message += "<curriculum_service_url>" + vars ["curriculum_service_url"] + "</curriculum_service_url>";

		message += "<restore_problem_url>" + vars ["restore_problem_url"] + "</restore_problem_url>";

		message += "<collaborators>" + vars ["collaborators"] + "</collaborators>";

		/*
		if (context.hasDatasetName())
		{
			message += "<dataset_name>" + context.DatasetName + "</dataset_name>";
			var dataSetLevelNames:Vector.<String> = context.DatasetLevelNames;
			var dataSetLevelTypes:Vector.<String> = context.DatasetLevelTypes;

			for (var i:int = 0; i < dataSetLevelNames.length; i++)
			{
				message += "<dataset_level_name" + (i + 1) + ">" + dataSetLevelNames[i] + "</dataset_level_name" + (i + 1) + ">";
				message += "<dataset_level_type" + (i + 1) + ">" + dataSetLevelTypes[i] + "</dataset_level_type" + (i + 1) + ">";
			}
		}
		*/

		/*
		if (context.hasConditions())
		{
			var dataConditionNames:Vector.<String> = context.conditionNames;
			var dataConditionTypes:Vector.<String> = context.conditionTypes;

			for (var j:int = 0; j < dataConditionNames.length; j++)
			{
				message += "<study_condition_name" + (j + 1) + ">" + dataConditionNames[j] + "</study_condition_name" + (j + 1) + ">";
				message += "<study_condition_type" + (j + 1) + ">" + dataConditionTypes[j] + "</study_condition_type" + (j + 1) + ">";
			}
		}
		*/

		if (CTATSkillSet.skills!==null)
		{
			message+=CTATSkillSet.skills.toSetPreferencesXMLString ();
		}

		message += "<CommShellVersion>" + versionNum + "</CommShellVersion>";
		message += "</properties></message>";

		return xmlHeader+message;
	};

	/**
	*
	*/
	this.createTracerActionMessage=function createTracerActionMessage(transactionID,sai)
	{
		this.ctatdebug ("createTracerActionMessage ()");

		var message = "<message><verb>NotePropertySet</verb><properties><MessageType>TracerAction</MessageType>";

		message += "<transaction_id>" + transactionID + "</transaction_id>";
		message += sai.toXMLString(false);
		message += "</properties></message>";

		return (xmlHeader+message);
	};

	/**
	*
	*/
	this.createInterfaceActionMessage=function createInterfaceActionMessage(transactionID,sai)
	{
		this.ctatdebug ("createInterfaceActionMessage ()");

		var message = "<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceAction</MessageType>";

		message += "<transaction_id>" + transactionID + "</transaction_id>";
		message += sai.toXMLString(false);
		message += "</properties></message>";

		return (xmlHeader+message);
	};

	/**
	*
	*/
	this.createUntutoredActionMessage=function createUntutoredActionMessage(transactionID,sai)
	{
		this.ctatdebug ("createUntutoredActionMessage ("+transactionID+")");

		var message = "<message><verb>NotePropertySet</verb><properties><MessageType>UntutoredAction</MessageType>";

		message += "<transaction_id>" + transactionID + "</transaction_id>";
		message += sai.toXMLString(false);
		message += "</properties></message>";

		return (xmlHeader+message);
	};

	/**
	*
	*/
	this.createProblemSummaryRequestMessage=function createProblemSummaryRequestMessage()
	{
		var message = "<message><verb>NotePropertySet</verb><properties><MessageType>ProblemSummaryRequest</MessageType></properties></message>";

		return (xmlHeader + message);
	};

	/**
	*
	*/
	this.createProblemRestoreEndMessage=function createProblemRestoreEndMessage()
	{
		var message = "<message><verb>NotePropertySet</verb><properties><MessageType>ProblemRestoreEnd</MessageType></properties></message>";

		return (xmlHeader + message);
	};

	/**
	 * @return {string} message with MessageType InterfaceConfigurationEnd
	 */
	this.createInterfaceConfigurationEnd = function()
	{
		var message = "<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceConfigurationEnd</MessageType></properties></message>";
		return (xmlHeader + message);
	};

	/**
	*
	*/
	this.createInCorrectActionMessage=function createInCorrectActionMessage(transactionID,	sai)
	{
		this.ctatdebug ("createInCorrectActionMessage ()");

		var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>InCorrectAction</MessageType>";

		message += "<transaction_id>" + transactionID + "</transaction_id>";
		message += sai.toXMLString(false);
		message += "</properties></message>";

		return (xmlHeader+message);
	};

	/**
	*
	*/
	this.createURLResponse=function createURLResponse(aURL,aData)
	{
		CTATGlobal.debug(classType, "createURLResponse ()");

		return xmlHeader+"<message><verb>NotePropertySet</verb><properties><MessageType>GetURLResponse</MessageType><URL><![CDATA[ "+aURL+" ]]></URL><content><![CDATA[ "+btoa (aData)+" ]]></content></properties></message>";
	}

	/**
	*
	*/
	this.createCorrectActionMessage=function createCorrectActionMessage(transactionID,	sai)
	{
		this.ctatdebug ("createCorrectActionMessage ()");

		var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>CorrectAction</MessageType>";

		message += "<transaction_id>" + transactionID + "</transaction_id>";
		message += sai.toXMLString(false);
		message += "</properties></message>";

		return (xmlHeader+message);
	};
};

CTATTutoringServiceMessageBuilder.prototype = Object.create(CTATBase.prototype);
CTATTutoringServiceMessageBuilder.prototype.constructor = CTATTutoringServiceMessageBuilder;
