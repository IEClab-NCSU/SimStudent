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
 *  
 */
function CTATTutoringServiceMessageBuilder ()
{
	CTATBase.call(this, "CTATTutoringServiceMessageBuilder", "__undefined__");

	var xmlHeader='<?xml version="1.0" encoding="UTF-8"?>';	

	/**
	*
	*/		
	this.createStartProblemMessage=function createStartProblemMessage(problemName) 
	{
		this.debug ("createStartProblemMessage ()");
		
		return (xmlHeader+"<message><verb>NotePropertySet</verb><properties><MessageType>StartProblem</MessageType><ProblemName>" + problemName + "</ProblemName></properties></message>");
	};
	
	/**
	 * 
	 */
	this.createInterfaceIdentificationMessage=function createInterfaceIdentificationMessage (anID)
	{
		this.debug ("createInterfaceIdentificationMessage ("+anID+")");
		
		return (xmlHeader+"<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceIdentification</MessageType><Guid>" + anID + "</Guid></properties></message>");
	};
	
	/**
	*
	*/		
	this.createInterfaceDescriptionMessage=function createInterfaceDescriptionMessage(components,tWidth,tHeight) 
	{
		this.debug ("createInterfaceDescriptionMessage ()");
		
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
		this.debug ("createSetPreferencesMessage ()");
			
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
		
		message += "<question_file>" + vars ["question_file"] + "</question_file>";
		
		message += "<class_name>" + vars ["class_name"] + "</class_name>";
		
		message += "<school_name>" + vars ["school_name"] + "</school_name>";
		
		message += "<instructor_name>" + vars ["instructor_name"] + "</instructor_name>";
		
		message += "<session_id>" + vars ["session_id"] + "</session_id>";
																			
		message += "<source_id>" + vars ["source_id"] + "</source_id>";
		
		message += "<sui><![CDATA[" + vars ["sui"] + "]]></sui>";		
				
		message += "<problem_state_status>" + vars ["problem_state_status"] + "</problem_state_status>";		
									
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
		this.debug ("createInterfaceActionMessage ()");
		
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
		this.debug ("createUntutoredActionMessage ()");
		
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
}

CTATTutoringServiceMessageBuilder.prototype = Object.create(CTATBase.prototype);
CTATTutoringServiceMessageBuilder.prototype.constructor = CTATTutoringServiceMessageBuilder;
