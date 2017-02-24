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
 * CTATLogMessageBuilder is used to create xml log messages that conform to DataShop's specifications.
 * <p> This class contains static factory methods that are used to create the different types of log messages used in the 
 * DataShop specification.</p>
 * Example:
 	<tutor_related_message_sequence version_number="4">
		<context_message context_message_id="98F2147D22FAA521048373A69AB06C23B347866" name="LOAD_TUTOR">
			<class>
				<name>Default Class</name>
				<school>Admin</school>
				<instructor>admin</instructor>
			</class>
			<dataset>
				<name>Mathtutor</name>
				<level type="ProblemSet">
					<name>QA2.0_debug_enabled_tutor</name>
					<problem>
						<name>1415-error</name>
					</problem>
				</level>
			</dataset>
		</context_message>
	</tutor_related_message_sequence>
*/
function CTATLogMessageBuilder ()
{
	CTATBase.call (this,"CTATLogMessageBuilder","logmessagebuilder");
	
	var pointer=this;
		
	// Blanked this out to kill a bug that generated extra XML headers. Used to be = '<?xml version="1.0" encoding="UTF-8"?>'
	var xmlHeader = '';
	
	/**
	 * The standard XMLProlog, we actually reference this a lot.
	 */
	var xmlProlog = '<?xml version="1.0" encoding="UTF-8"?>';	
	
	var customFieldNames=new Array ();
	var customFieldValues=new Array ();
	
	/**
	 * <b>[Required]</b> The &#60;name&#62; attribute of the current context.
	 * <p>The name attribute is used to indicate where the student is in the process of working on a tutor or problem. 
	 * The PSLC DataShop team has established some canonical values for this attribute that should be used,
	 * displayed in <a href="http://pslcdatashop.web.cmu.edu/dtd/guide/context_message.html#table.context_message.name.values">Table 1, “Recommended values for the &#60;context_message&#62; name attribute”</a>.</p>
	 * @param	context_name	A name for the current context, see table for a set  of recommended names.
	 */
	this.setContextName=function setContextName(context_name) 
	{ 
		contextGUID = context_name; 
	};
		
	this.getContextName=function getContextName()
	{
		return contextGUID;
	};
	
	/**
	*	Builds a context message to the Data Shop specifications.
	*	@see http://pslcdatashop.web.cmu.edu/dtd/guide/context_message.html 
	*	DataShop specification for context_message
	*/
	this.createContextMessage=function createContextMessage (aWrapForOLI) 
	{
		pointer.debug("createContextMessage()");
					
		var now=new Date();
		
		var vars=flashVars.getRawFlashVars ();
		
		var messageString=xmlHeader+'<context_message context_message_id="'+vars ['session_id']+'" name="'+ this.getContextName()+'">';

		if(!aWrapForOLI) 
		{
			messageString += this.makeMetaElement(now);			
		}
		
		/*

		//<class>
		if (contextObj.hasClassField()) 
		{
			var classS:String = '<class>';
			if(contextObj.hasClassName()) {classS += '<name>'+contextObj.ClassName+'</name>';}
			if(contextObj.hasClassSchool()) {classS += '<school>'+contextObj.ClassSchool+'</school>';}
			if(contextObj.hasClassPeriod()) {classS += '<period>'+contextObj.ClassPeriod+'</period>';}
			if(contextObj.hasClassDescription()) {classS += '<description>'+contextObj.ClassDescription+'</description>';}
			if (contextObj.hasClassInstructor()) { classS += '<instructor>' + contextObj.ClassInstructor + '</instructor>';}
			classS += '</class>';
			messageString += classS;
		}			
		
		//<dataset>
		var dataset:String = '<dataset>';
		dataset += '<name>'+contextObj.DatasetName+'</name>';
		var datasetLevelTypes:Vector.<String> = contextObj.DatasetLevelTypes;
		var datasetLevelNames:Vector.<String> = contextObj.DatasetLevelNames;
		for (var level:String in datasetLevelTypes) 
		{
			dataset += '<level type="' + datasetLevelTypes[level] + '">';
			dataset += '<name>' + datasetLevelNames[level] + '</name>';
		}
		
		//<dataset><problem>
		dataset += '<problem';
							
		if ((CTATLinkData.flashVars.getFlashVar ("problem_tutorflag")!="") || (CTATLinkData.flashVars.getFlashVar ("problem_otherproblemflag")!=""))
		{
			if (CTATLinkData.flashVars.getFlashVar ("problem_tutorflag")!="")
			{
				dataset+=' tutorFlag="'+CTATLinkData.flashVars.getFlashVar ("problem_tutorflag")+'"';
			}
			else
			{
				dataset+=' tutorFlag="other"';
			}
		}
		
		dataset += '>';
		dataset += '<name>'+contextObj.ProblemName+'</name>';		
		dataset += '<context>' + contextObj.ProblemContext + '</context>';
		dataset += '</problem>';
		
		for (var i:int = 0;  i < datasetLevelTypes.length;i++ ) 
		{
			dataset += '</level>';
		}
		
		dataset += '</dataset>';
		
		messageString += dataset;
						
		//<condition>
		if (contextObj.hasConditions()) 
		{
			var condition:String="";
			var conditionNames:Vector.<String> = contextObj.getConditionNames();
			var conditionTypes:Vector.<String> = contextObj.getConditionTypes();
			var conditionDescriptions:Vector.<String> = contextObj.getConditionDescriptions();
			
			for (var cond:String in conditionNames) 
			{
				condition += '<condition><name>'+conditionNames[cond]+'</name>';
				condition += (conditionTypes[cond] == "" ? "" : '<type>'+conditionTypes[cond]+'</type>');
				condition += (conditionDescriptions[cond] == "" ? "" : '<desc>'+conditionDescriptions[cond]+'</desc>');
				condition += '</condition>';
			}
			messageString += condition;
		}
		
		*/
		
		//messageString+=this.createCustomFields (customFieldNames,customFieldValues);
		messageString+=this.createCustomFields (null,null);
		
		messageString += "</context_message>";
		
		if (aWrapForOLI==true) 
		{
			messageString = this.wrapForOLI (messageString);
		}
		
		pointer.debug ("messageString = " + messageString);
		
		return messageString;
	};

	/**
	*	Builds a tool message to the Data Shop specifications, for tool messages with semantic_event fields.
	* 	<p>The basic difference between the tool messages is that a Semantic Event message contains data that is
	* 	useful in a tutoring context where a UI_Event does not.</p>
	*	@see http://pslcdatashop.web.cmu.edu/dtd/guide/tool_message.html DataShop specification for tool_message
	*/
	this.createSemanticEventToolMessage=function createSemanticEventToolMessage(sai, 
														  						semanticTransactionID,
														  						semanticName,
														  						semanticSubType,
														  						wrapForOLI,
														  						customFieldNames, 
														  						customFieldValues) 
	{
		pointer.debug ("createSemanticEventToolMessage()");
				
		var now=new Date();
		var vars=flashVars.getRawFlashVars ();
		var messageString=xmlHeader+'<tool_message context_message_id="'+vars ['session_id']+'">';
		
		//<meta>
		if (!wrapForOLI) 
		{
			messageString += this.makeMetaElement (now);
		}
		
		//<semantic_event> (1+)
		var semantic='<semantic_event transaction_id="'+semanticTransactionID+ '" name="' + semanticName + '"';
		
		if (semanticSubType != "")
			semantic += ' subtype="' + semanticSubType + '"';
			
		semantic+='/>';
		messageString+=semantic;
					
		//<event_descriptor>(0+)
		var eventDescriptor = '<event_descriptor>';
		eventDescriptor+=sai.toXMLString(true);
		eventDescriptor+='</event_descriptor>';
		messageString+=eventDescriptor;
		
		messageString+=this.createCustomFields (customFieldNames,customFieldValues);
		
		messageString += '</tool_message>';
		
		if (wrapForOLI) 
		{
			messageString = this.wrapForOLI(messageString);
		}
		
		pointer.debug ("messageString = "+messageString);
		
		return messageString;
	};
	
	/**
	*	Builds a tool message to the Data Shop specifications, for tool messages with ui_event fields.
	* 	<p>The basic difference between the tool messages is that a Semantic Event message contains data that is
	* 	useful in a tutoring context where a UI_Event does not.</p>
	*	@see http://pslcdatashop.web.cmu.edu/dtd/guide/tool_message.html DataShop specification for tool_message
	*/
	this.createUIEventToolMessage=function createUIEventToolMessage (sai,
																	 uiEventName,
																	 uiEventField,
																	 wrapForOLI,
																	 customFieldNames, 
																	 customFieldValues) 
	{
		pointer.debug ("createUIEventToolMessage()");
		
		var now = new Date();
		var vars=flashVars.getRawFlashVars ();
		var messageString = xmlHeader+'<tool_message context_message_id="'+vars ['session_id']+'">';
		
		//<meta>
		if (!wrapForOLI) 
		{
			messageString += this.makeMetaElement (now);
		}
			
		//<ui_event> (1+)
		var uiEvent = '<ui_event name="'+uiEventName+'">'+uiEventField+'</ui_event>';
		messageString += uiEvent;
		
		//<event_descriptor>(0+)
		var eventDescriptor = '<event_descriptor>';
		eventDescriptor+=sai.toSerializedString();
		eventDescriptor+='</event_descriptor>';
		
		messageString+=eventDescriptor;
		
		messageString+=this.createCustomFields (customFieldNames,customFieldValues);
		
		messageString+='</tool_message>';
		
		if (wrapForOLI)
		{
			messageString = this.wrapForOLI(messageString);
		}
		
		pointer.debug ("messageString = "+messageString);
		
		return messageString;
	};
	
	/**
	*	Builds a tutor message to the Data Shop specifications.
	*	@see http://pslcdatashop.web.cmu.edu/dtd/guide/tutor_message.html DataShop specification for tutor_message
	*/
	this.createTutorMessage=function createTutorMessage (sai,
											   			 semanticTransactionID,
											   			 semanticName,
											   			 evalObj,
											   			 advice,
											   			 semanticSubType,
											   			 skillList,
											   			 wrapForOLI,
											   			 customFieldNames, 
											   			 customFieldValues) 
	{
		pointer.debug ("createTutorMessage()");
		
		var now = new Date();
		var vars=flashVars.getRawFlashVars ();
		var messageString = xmlHeader+'<tutor_message context_message_id="'+vars ['session_id']+'">';
		
		//<meta>
		if (!wrapForOLI) 
		{
			messageString += this.makeMetaElement(now);
		}
		
		//<semantic_event> (1+)
		var semantic = '<semantic_event transaction_id="' + semanticTransactionID + '" name="' + semanticName + '"';
		
		if (semanticSubType != "") 
		{ 
			semantic += ' subtype="' + semanticSubType + '"'; 
		}
		
		semantic+='/>';
		messageString+=semantic;
		
		//<event_descriptor>(0+)
		var eventDescriptor = '<event_descriptor>';
		eventDescriptor+=sai.toXMLString(true);
		eventDescriptor+='</event_descriptor>';
		messageString+=eventDescriptor;

		//<action_evaluation> (0+)
		var actionEvaluation = '<action_evaluation ';
		if (evalObj.hasClassification())
		{
			if (evalObj.getAttributeString()!=null)					
				actionEvaluation += evalObj.getAttributeString();
		}
		
		actionEvaluation += '>'+evalObj.getEvaluation()+'</action_evaluation>';
		messageString += actionEvaluation;

		//<tutor_advice>(0+)
		if (advice != "") 
		{
			messageString += '<tutor_advice>'+advice+'</tutor_advice>';
		}

		//<skill> (0+)
		/*
		if (skillList != null ) 
		{
			var skills = "";
			
			for each (var skill in skillList) 
			{
				skills += "<skill probability=\""+skill.getLevel()+"\"><name>"+skill.getSkillName()+"</name>";
				
				if (skill.hasCategory()) 
				{
					skills +='<category>' + skill.getCategory() + '</category>';
				}
				
				if (skill.hasModelName()) 
				{ 
				 	skills += '<model_name>' + skill.getModelName() + '</model_name>'; 
				}					
				
				skills += '</skill>';
			}
			
			messageString += skills;
		}
		*/
		
		messageString+=this.createCustomFields (customFieldNames,customFieldValues);
		
		messageString += '</tutor_message>';
		
		if (wrapForOLI) 
		{
			messageString = this.wrapForOLI(messageString);
		}
		
		pointer.debug(classType,"messageString = "+messageString);
		
		return messageString;
	};
		
	/**
	*	Builds a generic message to the Data Shop specifications.
	*	<p>In practice this will only assure that the message compiles with general XML specifications and that
	*	it contains the minimal requirements for a "message" in Data Shop's specifications.</p>
	*	@see http://pslcdatashop.web.cmu.edu/dtd/guide/message_message.html DataShop specification for message
	*/
	this.createGenericMessage=function createGenericMessage(logMessage,wrapForOLI) 
	{
		pointer.debug ("createGenericMessage()");
		
		var vars=flashVars.getRawFlashVars ();		
		
		var messageString = xmlHeader+'<message context_message_id="'+vars ['session_id']+'">';
		messageString+=logMessage;
		messageString+='</message>';
		
		if (wrapForOLI==true) 
		{
			messageString = this.wrapForOLI (messageString);
		}
		
		pointer.debug ("messageString = "+messageString);
		
		return messageString;
	};
	
	/**
	 * 
	 */
	this.makeMetaElement=function makeMetaElement (timeStamp) 
	{					
		var vars=flashVars.getRawFlashVars ();
		
		var meta='<meta>';
		meta += '<user_id>'+vars ['user_guid']+'</user_id>';
		meta += '<session_id>'+vars ['session_id']+'</session_id>';
		meta += '<time>'+this.formatTimeStamp(timeStamp)+'</time>';
		meta += '<time_zone>'+flashVars.getTimeZone ()+'</time_zone></meta>';
		
		return meta;
	};
	
	/**
	*
	*/				
	this.wrapForOLI=function wrapForOLI(messageString) 
	{	
		var now=new Date();
		
		var vars=flashVars.getRawFlashVars ();
		
		messageString = encodeURIComponent(messageString);
		
		var wrapper = xmlProlog + '<log_action ';
		wrapper += 'auth_token="'+vars ['auth_token']+'" ';
		wrapper += 'session_id="' + vars ['session_id'] + '" ';
		wrapper += 'user_guid="' + vars ['user_guid'] + '" ';
		wrapper += 'date_time="' + this.formatTimeStamp(now) + '" ';
		wrapper += 'timezone="' + flashVars.getTimeZone () + '" '; 
		wrapper += 'source_id="' + vars ['source_id'] + '" ';
		wrapper += 'external_object_id="" info_type="tutor_message.dtd">';
		messageString = wrapper + messageString + "</log_action>";
		
		return messageString;
	};
	
	/**
	 * Creates a SessionStart message.
	 * <p>session_log messages are part of logging to an OLI framework.</p>
	 * @param	sessionObj	The CTATSessionData object, this is a member of CTATContextData 
	 * @return	Returns a session_log message.
	 */
	this.createLogSessionStart=function createLogSessionStart ()
	{
		var now=new Date();
		
		var message='<log_session_start timezone="' + flashVars.getTimeZone() + '" ';

		var vars=flashVars.getRawFlashVars ();
		
		message += 'date_time="'+this.formatTimeStamp(now) + '" ';
		message += 'auth_token="' +vars ['auth_token'] + '" ';
		message += 'session_id="' + vars ['session_id'] + '" ';
		message += 'user_guid="' + vars ['user_guid'] + '" ';
		message += 'class_id="" treatment_id="" assignment_id="" info_type="tutor_message.dtd"/>';
		
		return message;
	};
	
	/**
	 * Formats Date objects into Datashop's prefered format.
	 * @param	stamp	A Date object.
	 * @return	A String in the proper format
	 * 
	 * http://www.w3schools.com/jsref/jsref_obj_date.asp
	 */
	this.formatTimeStamp=function formatTimeStamp (stamp) 
	{
		var s="";
		var year= stamp.getFullYear();
		s += year+"/";

		var month=stamp.getMonth();
		month++;		
		s += ((month<10) ? ("0"+month) : month)+"/";

		var date = stamp.getDate();
		s += ((date<10) ? ("0"+date) : date)+" ";

		var hours = stamp.getHours();
		s += ((hours<10) ? ("0"+hours) : hours)+":";

		var mins = stamp.getMinutes();
		s += ((mins<10) ? ("0"+mins) : mins)+":";

		var secs = stamp.getSeconds();
		s += ((secs<10) ? ("0"+secs) : secs);

		var msec = stamp.getMilliseconds ();
		s+=".";
		s+=msec;

		return s;
	};
	/**
	 * 
	 * @param customFieldNames
	 * @param customFieldValues
	 * @returns {String}
	 */
	this.createCustomFields=function createCustomFields (aCustomFieldNames, 
											   			 aCustomFieldValues) 
	{
		pointer.debug ("createCustomFields ()");
		
		if ((aCustomFieldNames==null) || (aCustomFieldValues==null))
		{
			return ("");
		}
		
		var message='';
		
		for (var dex=0; dex < aCustomFieldNames.length; dex++) 
		{
			pointer.debug ("Adding custom field: ["+aCustomFieldNames[dex]+"],["+aCustomFieldValues[dex]+"]");
			
			message += '<custom_field>';
			message += '<name>' + aCustomFieldNames[dex] + '</name>';
			message += '<value>' + aCustomFieldValues[dex] + '</value>';
			message += '</custom_field>';
		}
		
		return message;
	};	
	/**
	 * 
	 */
	this.resetCustomFields=function resetCustomFields ()
	{
		this.customFieldNames=new Array ();
		this.customFieldValues=new Array ();				
	};
	/**
	 * 
	 */
	this.addCustomField=function addCustomfield (aName,aValue)
	{
		this.customFieldNames.push (aName);
		this.customFieldValues.push (aValue);
	};
	/**
	 * 
	 */
	this.getCustomFieldNames=function getCustomFieldNames ()
	{
		return (customFieldNames);
	};
	/**
	 * 
	 */
	this.getCustomFieldValues=function getCustomFieldValues ()
	{
		return (customFieldValues);
	};
}

CTATLogMessageBuilder.prototype = Object.create(CTATBase.prototype);
CTATLogMessageBuilder.prototype.constructor = CTATLogMessageBuilder;
