/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATCommunication/CTATTutoringServiceMessageBuilder.js $
 $Revision: 21689 $

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
goog.require('CTATHTMLManager');
goog.require('CTATJSON');
goog.require('CTATMessage');
goog.require('CTATParameter');
goog.require('CTATSkillSet');
goog.require('CTATStyle');
goog.require('CTATXML');
/**
 *
 */
CTATTutoringServiceMessageBuilder = function()
{
	CTATBase.call(this, "CTATTutoringServiceMessageBuilder", "__undefined__");

	var xmlHeader='<?xml version="1.0" encoding="UTF-8"?>';

	/**
	*
	*/
	this.createStartProblemMessage=function createStartProblemMessage(problemName)
	{
		this.ctatdebug ("createStartProblemMessage ()");

		var vars=flashVars.getRawFlashVars ();
		return (xmlHeader+"<message><verb>NotePropertySet</verb><properties><MessageType>StartProblem</MessageType><ProblemName>" + problemName + "</ProblemName>"+"<session_id>" + vars ["session_id"] + "</session_id>"+"</properties></message>");
	};

	/**
	 *
	 */
	this.createInterfaceIdentificationMessage=function createInterfaceIdentificationMessage (anID)
	{
		this.ctatdebug ("createInterfaceIdentificationMessage ("+anID+")");
		var vars=flashVars.getRawFlashVars ();
		return (xmlHeader+"<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceIdentification</MessageType><Guid>" + anID + "</Guid>"+"<session_id>" + vars ["session_id"] + "</session_id>"+"</properties></message>");
	};

	/**
	*
	*/
	this.createInterfaceDescriptionMessage=function createInterfaceDescriptionMessage(components,tWidth,tHeight)
	{
		this.debug ("createInterfaceDescriptionMessage ()");
		var vars=flashVars.getRawFlashVars ();
		var message = "<message><verb>NotePropertySet</verb><properties><MessageType>MessageBundle</MessageType><messages>";

		// First we add the commshell which for the start state represents the tutor itself

		message += "<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceDescription</MessageType>";
		message += "<WidgetType>CTATCommShell</WidgetType>";
		message += "<CommName>theShell</CommName>";
		message += "<UpdateEachCycle>false</UpdateEachCycle>";
		message += '<jessDeftemplates><value>(deftemplate CTATCommShell (slot_name))</value></jessDeftemplates>';
		message += '<jessInstances><value>(assert (CTATCommShell (name CTATCommShell) (value "" )))</value></jessInstances>';
		message	+= '<serialized>';
		message +=("<CTATCommShell name=\"theShell\" x=\"0\" y=\"0\" width=\""+tWidth+"\" height=\""+tHeight+"\"></CTATCommShell>");
		message += '</serialized>';
    message += "<session_id>" + vars ["session_id"] + "</session_id>";
		message += "</properties></message>";

		// Since we don't do authoring from within HMTM5 we don't have
		// to send all the component interface descriptions

		/*
		for each(var comp:CTATComponentInterface in components)
		{
			message += createComponentDescriptionMessage(comp);
		}
		*/

		message += "</messages></properties></message>";

		return (xmlHeader+message);
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

		var vars=flashVars.getRawFlashVars ();

		var message = "<message><verb>NotePropertySet</verb><properties><MessageType>SetPreferences</MessageType>";

		message += "<log_service_url>" + vars ["log_service_url"] + "</log_service_url>";

		var logRemote = false;
		var logDisk = false;

		var logMethod=vars ["Logging"];

		switch (logMethod)
		{
			case "ServiceToLogServer" :
												logRemote = true;
												break;
			case "ServiceToDisk" :
												logDisk = true;
												break;
			case "ServiceToDiskAndLogServer" :
												logRemote = true;
												logDisk = true;
												break;
			default :
												break;
		}

		message += "<log_to_remote_server>" + logRemote + "</log_to_remote_server><log_to_disk>" + logDisk + "</log_to_disk>";

		message += "<log_to_disk_directory>" + vars ["log_to_disk_directory"] + "</log_to_disk_directory>";

		message += "<user_guid>" + vars ["user_guid"] + "</user_guid>";

		message += "<problem_name>" + vars ["problem_name"] + "</problem_name>";
		
		// Added by Shruti for demonstrating a valueTypeChecker input argument to Backend
		message += "<Argument>" + vars ["Argument"] + "</Argument>";

		message += "<question_file>" + vars ["question_file"] + "</question_file>";

		message += "<class_name>" + vars ["class_name"] + "</class_name>";

		message += "<school_name>" + vars ["school_name"] + "</school_name>";

		message += "<instructor_name>" + vars ["instructor_name"] + "</instructor_name>";

		message += "<session_id>" + vars ["session_id"] + "</session_id>";

		message += "<source_id>" + vars ["source_id"] + "</source_id>";

		message += "<sui><![CDATA[" + vars ["sui"] + "]]></sui>";

		message += "<problem_state_status>" + vars ["problem_state_status"] + "</problem_state_status>";

		message += "<back_dir>" + vars ["back_dir"] + "</back_dir>";

		message += "<back_entry>" + vars ["back_entry"] + "</back_entry>";

		message += "<wmes>";
		var notFiles = ["not_file1","not_file_2"];
		for(var i = 0; i < notFiles.length; i++)
			message += "<value>"+notFiles[i]+"</value>";
		message += "</wmes>";

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

		if (skillSet!=null)
		{
			message+=skillSet.toSetPreferencesXMLString ();
		}

		message += "<CommShellVersion>" + versionNum + "</CommShellVersion>";
		message += "</properties></message>";

		return xmlHeader+message;
	};

	/**
	*
	*/
	this.createInterfaceActionMessage=function createInterfaceActionMessage(transactionID,sai)
	{
		this.ctatdebug ("createInterfaceActionMessage ()");
		var vars=flashVars.getRawFlashVars ();
		var message = "<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceAction</MessageType>";

		message += "<transaction_id>" + transactionID + "</transaction_id>";

		message += sai.toXMLString(false);
    message += "<session_id>" + vars ["session_id"] + "</session_id>";
		message += "</properties></message>";

		return (xmlHeader+message);
	};

	/**
	*
	*/
	this.createUntutoredActionMessage=function createUntutoredActionMessage(transactionID,sai)
	{
		this.ctatdebug ("createUntutoredActionMessage ()");
		var vars=flashVars.getRawFlashVars ();
		var message = "<message><verb>NotePropertySet</verb><properties><MessageType>UntutoredAction</MessageType>";

		message += "<transaction_id>" + transactionID + "</transaction_id>";
		message += sai.toXMLString(false);
    message += "<session_id>" + vars ["session_id"] + "</session_id>";
		message += "</properties></message>";

		return (xmlHeader+message);
	};

	/**
	*
	*/
	this.createProblemSummaryRequestMessage=function createProblemSummaryRequestMessage()
	{
    var vars=flashVars.getRawFlashVars ();
		var message = "<message><verb>NotePropertySet</verb><properties><MessageType>ProblemSummaryRequest</MessageType>"+"<session_id>" + vars ["session_id"] + "</session_id>"+"</properties></message>";

		return (xmlHeader + message);
	};


	/**
	*
	*/
	this.createProblemRestoreEndMessage=function createProblemRestoreEndMessage()
	{
    var vars=flashVars.getRawFlashVars ();
		var message = "<message><verb>NotePropertySet</verb><properties><MessageType>ProblemRestoreEnd</MessageType>"+"<session_id>" + vars ["session_id"] + "</session_id>"+"</properties></message>";

		return (xmlHeader + message);
	};

	  this.createInterfaceAttributesMessage=function createInterfaceAttributesMessage()
	  {
	    var vars=flashVars.getRawFlashVars ();
	    var bundle = "<MessageBundle>";
	    var wrap = function(tag,val){
	      return "<"+tag+">"+val+"</"+tag+">";
	    };
	    for(var i = 0; i < components.length; i++)
	    {
	      var ref = components[i];
	      this.ctatdebug ("Obtaining component for " + ref.name + " with type: " + ref.type);
	      console.log(ref);
	      var comp=ref.getComponentPointer ();
	      if(comp == null) continue;

	      var message = "<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceAttribute</MessageType>";

	      message += "<component>"+comp.getName()+"</component>";
	      message += wrap("background_color",comp.getBackgroundColor());
	      message += wrap("border_color",comp.getBorderColor());
	      message += wrap("border_style",comp.getBorderStyle());
	      message += wrap("border_width",comp.getBorderWidth());
	      message += wrap("enabled",comp.getEnabled());
	      message += wrap("font_color",comp.getFontColor());
	      message += wrap("font_size",comp.getFontSize());
	      message += wrap("height",comp.getHeight());
	      message += wrap("hint_highlight",comp.getHighlighted());
	      message += wrap("text",comp.getText());
	      message += wrap("width",comp.getWidth());
	      message += wrap("x_coor",comp.getX());
	      message += wrap("y_coor",comp.getY());

	      message += "<session_id>" + vars ["session_id"] + "</session_id>";
	      message += "</properties></message>";
	      bundle += message;
	    }
	    bundle += "</MessageBundle>";
	    return bundle;
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
	this.createCorrectActionMessage=function createCorrectActionMessage(transactionID,	sai)
	{
		this.ctatdebug ("createCorrectActionMessage ()");

		var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>CorrectAction</MessageType>";

		message += "<transaction_id>" + transactionID + "</transaction_id>";
		message += sai.toXMLString(false);
		message += "</properties></message>";

		return (xmlHeader+message);
	};

        this.createAssociatedRulesMessage = function createAssociatedRulesMessage(indicatorValue,sai,studentSAI,ruleNames,stepID,transactionID)
        {
            var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>AssociatedRules</MessageType>";
            message += "<Indicator>"+indicatorValue+"</Indicator>";
            message += sai.toXMLString();
            var temp = studentSAI.toXMLString().replace("<Selection>","<StudentSelection>").replace("<Action>","<StudentAction>").replace("<Input>","<StudentInput>");
            temp = temp.replace("</Selection>","</StudentSelection>").replace("</Action>","</StudentAction>").replace("</Input>","</StudentInput>");
            message += temp;

            return (xmlHeader+message);
        }
}

CTATTutoringServiceMessageBuilder.prototype = Object.create(CTATBase.prototype);
CTATTutoringServiceMessageBuilder.prototype.constructor = CTATTutoringServiceMessageBuilder;
