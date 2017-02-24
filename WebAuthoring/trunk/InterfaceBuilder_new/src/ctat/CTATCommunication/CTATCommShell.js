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
function CTATCommShell ()
{
	CTATBase.call(this, "CTATCommShell", "theShell");
	
	var tutor=null;
	var tools=new CTATShellTools ();
	var pointer=this;
	var xmlParser=new CTATXML ();
	var gradingProcessor=null;
			
	/**
	 * 
	 */
	this.init=function init (aTutor)
	{
		this.debug ("init ()");
		
		var vars=flashVars.getRawFlashVars ();
		
		var prefix="http://";
		
		if (vars ['connection']=='https')
		{
			prefix="https://";
		}
				
		//drawText ("Running HTML5 CTAT version: " + version,2,10);
		
		tutor=aTutor;
		
		var generator=new CTATGuid ();
		
		contextGUID=generator.guid ();
		
		//this.debug ("A");
				
		processSkills ();
		
		//this.debug ("B");
		
		commMessageHandler=new CTATMessageHandler ();
		commMessageHandler.assignHandler (this);
		commMessageBuilder=new CTATTutoringServiceMessageBuilder ();			
		commLogMessageBuilder=new CTATLogMessageBuilder ();
		commLibrary=new CTATCommLibrary ();
		commLibrary.assignHandler (this);
		commLMSService=new CTATCurriculumService (commLibrary);
				
		//this.debug ("C2");
		
		//commLoggingLibrary=new CTATLoggingLibrary ();
		
		//this.debug ("C3");
		
		var vars=flashVars.getRawFlashVars ();
				
		flashVars.setTimeZone (null); // Force detection of timezone
		
		//this.debug ("D");
		
		var htmlManager=new CTATHTMLManager ();
				
		if (vars ['info']!="")
		{
			javaScriptInfo(htmlManager.entitiesConvert (vars ['info']));
		}
		else
			this.debug ("There is no info flash var");
				
		this.debug ("Connecting to: " + prefix + vars ["remoteSocketURL"] + ":" + vars ["remoteSocketPort"]);
				
		commLibrary.sendXMLURL (commMessageBuilder.createSetPreferencesMessage (version),prefix + vars ["remoteSocketURL"] + ":" + vars ["remoteSocketPort"]);

		var saiTest=new CTATSAI ();		
	};
	/**
	*
	*/
	this.assignAnonymousGradingProcessor=function assignAnonymousGradingProcessor (aFunction)
	{
		gradingProcessor=aFunction;
	};
	/**
	 * 
	 */
	this.reset=function reset ()
	{
		pointer.debug ("reset ()");
		
		interfaceElement=null;
	}
	/**
	 *
	 */
	this.getMessageHandler=function getMessageHandler ()
	{
		return (this.commMessageHandler);
	};
	/**
	 *
	 */
	function processSkills ()
	{
		pointer.debug ("processSkills ()");

		skillSet=new CTATSkillSet ();

		var vars=flashVars.getRawFlashVars ();

		skillSet.fromXMLString(vars ['skills']);
	}
	/**
	 * 
	 * @param anX
	 * @param anY
	 */
	function drawText (aString,anX,anY)
	{			
		pointer.debug ("drawText ()");
		
    	ctx.fillText(aString,anX,anY);
	}
	/**
	 * 
	 */
	this.focusNextComponent=function focusNextComponent (aComponent)
	{
		this.debug ("focusNextComponent ()");
			
		if (aComponent==null)
		{
			this.debug ("Internal error, unable to resolve currentComponentPointer");
		}
		else
		{								
			this.debug ("From: " + aComponent.getName () + ", to: " + "TBD");
			
			aComponent.setHintHighlight (true);
		}	
	};
	/**
	 * 
	 */
	this.gradeComponent=function gradeComponent (aComponent)
	{
		this.debug ("gradeComponent ("+aComponent.getName ()+","+aComponent.getClassName ()+")");
			
		if (nameTranslator!=null)
		{
			nameTranslator.translateFromCTAT (aComponent.getName ());
		}
		else
			this.debug ("Info: no name translator provided, using as-is");
			
		if (aComponent==null)
		{
			this.debug ("Internal error, provided component is null");
			return;
		}
		
		if (aComponent.getTutorComponent ()=="Do not tutor")
		{
			return;
		}
				
		if ((aComponent.getName ()=="hint") || (aComponent.getClassName ()=="CTATHintButton"))
		{
			this.requestHint();
			return;
		}
		
		if ((aComponent.getName ()=="done") || (aComponent.getClassName ()=="CTATDoneButton"))
		{
			this.processDone ();
			return;
		}		
		
		if (aComponent.getName ()=="previous")
		{
	    	if(aComponent.getEnabled()==true)
	    	{
				for (var i=0;i<feedbackComponents.length;i++)
				{
					var feedbackComponent=feedbackComponents [i];
					
					// Log the message ...
					
					// Make it happen ...
					
					feedbackComponent.goPrevious ();
				}
			}			
			
			return;
		}

		if (aComponent.getName ()=="next")
		{
	    	if(aComponent.getEnabled()==true)
	    	{
				for (var i=0;i<feedbackComponents.length;i++)
				{
					var feedbackComponent=feedbackComponents [i];
					
					// Log the message ...
					
					// Make it happen ...					
					
					feedbackComponent.goNext ();
				}
			}			
			
			return;
		}		
		
		this.debug ("Checking for back grading: " + aComponent.getName ());
		
		/*
		if(aComponent.getClassName ()=="CTATTextArea")
		{
			this.debug ("Backgrading ...");
			
			this.debug ("Grading value: " + aComponent.getValue ());

			if (isBlank (aComponent.getValue ())==true)
			{
				this.debug ("Empty component, nothing to grade");
				return;
			}

			var textSAI=new CTATSAI(aComponent.getName (), "UpdateTextArea", aComponent.getValue ());
			
			pointer.processComponentAction(textSAI);
			
			//aComponent.showCorrect();
			
			return;
		}
		*/
		
		if ((aComponent.getClassName ()=="CTATTextArea") || (aComponent.getClassName ()=="CTATTextInput") || (aComponent.getClassName ()=="CTATTextField"))
		{
			this.debug ("Backgrading ...");
			
			this.debug ("Grading value: " + aComponent.getValue ());

			if (isBlank (aComponent.getValue ())==true)
			{
				this.debug ("Empty component, nothing to grade");
				return;
			}

			var textSAI=null;
			
			if (aComponent.getClassName ()=="CTATTextField")
				textSAI=new CTATSAI(aComponent.getName (), "UpdateTextField", aComponent.getValue ());
				
			if (aComponent.getClassName ()=="CTATTextInput")
				textSAI=new CTATSAI(aComponent.getName (), "UpdateTextArea", aComponent.getValue ());

			if (aComponent.getClassName ()=="CTATTextArea")
				textSAI=new CTATSAI(aComponent.getName (), "UpdateTextArea", aComponent.getValue ());
			
			pointer.processComponentAction(textSAI);
						
			return;
		}
		
		if (aComponent.getClassName ()=="CTATComboBox")
		{
			this.debug ("Grading combobox ...");
			
			var combobox=aComponent.getHTMLComponent ();
			
			var strUser = combobox.options[combobox.selectedIndex].value;
			
			if (isBlank (strUser)==true)
			{
				this.debug ("Empty component, nothing to grade");
				return;
			}
			
			var tsMessage=new CTATSAI(aComponent.getName (), "UpdateComboBox", strUser);
			
			pointer.processComponentAction(tsMessage);
			
			return;
		}	

		this.debug ("Grading regular component ...");
		
		//this.debugObjectShallow (aComponent.getBaseElement ());
		
		if(aComponent.getClassName ()=="CTATButton")
		{	
			var tsMessage=new CTATSAI(aComponent.getName (), "ButtonPressed", -1);
			
			pointer.processComponentAction(tsMessage);
			
			return;
		}
		
		if(aComponent.getClassName ()=="CTATRadioButton")
		{
			var tsMessage=new CTATSAI(aComponent.getComponentGroup (), 
									  "UpdateRadioButton", 
									  aComponent.getRadioInput ());
									  
			pointer.processComponentAction(tsMessage);
			
			return;
		}
		
		if(aComponent.getClassName ()=="CTATCheckBox")
		{
			var tsMessage=new CTATSAI(aComponent.getComponentGroup (),
									  "UpdateCheckBox",
									  aComponent.getCheckBoxInput ());
					
			pointer.processComponentAction(tsMessage);	
			
			return;
		}
		
		var tsMessage=new CTATSAI(aComponent.getName (), "ButtonPressed", -1);
		pointer.processComponentAction(tsMessage);
	};
	/**
	 * 
	 */
	this.processMessage=function processMessage(aMessage)
	{
		this.debug ("processMessage ()");
				
		commMessageHandler.processMessage (aMessage);
		
		this.debug ("processMessage () done");
	};
	/**
	 * 
	 */
	this.processStartState=function processStartState ()
	{
		this.debug ("processStartState ()");
		
		if (tutor!=null)
		{
			tutor.createInterface ();
			
			//useDebugging=true;
			
			if (commLoggingLibrary!=null)
			{
				commLoggingLibrary.startProblem ();
			}
			else
			{
				this.debug ("Info: no logging library available!");
			}
			
			//useDebugging=false;
		}
	};
	/**
	 * Ported from AS3
	 */						
	this.sendStartProblemMessage=function sendStartProblemMessage() 
	{
		pointer.debug ("sendStartProblemMessage()");
		
		/*
		var problemName:String = context.ProblemName;
		var message:String = CTATTutoringServiceMessageBuilder.createStartProblemMessage(problemName);
		connectionManager.sendMainConnectionMessage(message);
		*/
		
		// Not relevant for HTML5		
	};
	/**
	 * Ported from AS3
	 */
	this.sendProblemSummaryRequest=function sendProblemSummaryRequest (callBack)
	{
		pointer.debug ("sendProblemSummaryRequest()");
				
		var builder = new CTATTutoringServiceMessageBuilder ();
		
		var tsMessage = builder.createProblemSummaryRequestMessage();
		
		commLibrary.sendXML (tsMessage);
	};
	/**
	* Ported from AS3
	*/
	this.sendInterfaceDescriptionMessages=function sendInterfaceDescriptionMessages() 
	{
		pointer.debug("sendInterfaceDescriptionMessages ()");
		
		// Not relevant for HTML5
	};
	/**
	 * 
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.debug ("processSerialization()");

		// Process component specific pre-defined styles ...
		
		pointer.setText (this.label);
		
		// Process component custom styles ...		

		this.styles=aDescription.styles;

		this.styles=pointer.getGrDescription().styles;
		
		pointer.debug ("Processing " + this.styles.length + " styles ...");
		
		for (var i=0;i<this.styles.length;i++)
		{
			var aStyle=this.styles [i]; // CTATStyle
			
			if(aStyle.styleName=="CorrectColor")
			{
				correctColor=aStyle.styleValue;
			}
			
			if(aStyle.styleName=="IncorrectColor")
			{
				incorrectColor=aStyle.styleValue;
			}
			
			if(aStyle.styleName=="HintColor")
			{
				hintColor=aStyle.styleValue;
			}
			
			if(aStyle.styleName=="EnableDebugging")
			{
				pointer.setUseDebugging(aStyle.styleValue);
			};
		}	
	};
	/**
	 * 
	 */
	this.processComponentAction=function processComponentAction (sai,tutorComponent,behaviorRecord,component)
	{
		pointer.debug("processComponentAction(" +sai.getSelection() + "," + sai.getAction() + "," + sai.getInput() + ")");
				
		var generator=new CTATGuid ();
		
		var transactionID = generator.guid ();
		
		// First we log
		
		if (commLoggingLibrary!=null)
		{
			if (commMessageHandler.getInStartState ()==false)
			{
				//if ((useFlashSideLogging) && (!inAuthorTime))
				//{
					pointer.debug("logging the action ...");
				
					if (sai.getSelection()=="scrim")
					{
						debug ("Not logging any scrim actions (for now)");
					}
					else
					{
						if ((sai.getSelection() == "hint") || (sai.getSelection()=="null.nextButton") || (sai.getSelection()=="null.previousButton"))
						{
							commLoggingLibrary.logSemanticEvent(transactionID, sai, "HINT_REQUEST", "");
						}
						else
							commLoggingLibrary.logSemanticEvent(transactionID, sai, "ATTEMPT", "");
					}			
				//}
			}		
		}
		else
		{
			this.debug ("Info: no logging library available!");
		}		
		
		// Then we send to the TS
		
		var builder = new CTATTutoringServiceMessageBuilder ();
		
		var tsMessage = builder.createInterfaceActionMessage(transactionID,sai);
		
		commLibrary.sendXML (tsMessage);
		// Et Voila!
	};
	/**
	 * This is the method accessed by CTATComponents to inform all of CTAT's resources of actions. The
	 * CommShell will use parameters set at run time to determine how to process logging and will use parameters passed by the component
	 * in question to determine how to process tutoring. The default tutoring behavior is the be recorded and send InterfaceAction 
	 * messages, which will be reflected within a brd in CTAT. If tutorComponent is set to false then the action will be recorded as 
	 * Untutored, in which case CTAT will be informed of the action but will not consider it as correct or incorrect. This is useful for
	 * providing more contextual hints. There is also an option to have the action not be recorded by CTAT at all. In this case a log
	 * message will be sent to the logging service but CTAT will not be aware of the action.
	 * 
	 * @param	sai				A CTATSAI containing the selection, action, and input of the action taken by a component
	 * @param	tutorComponent	A boolean for whether you want the action to be tutored (InterfaceAction), or untutored (UntutoredAction)
	 * @param	behaviorRecord	A boolean for whether you want the action recorded by the Java side behavior recorder. Useful for actions you only want logged, in which case you would set this to false.
	 * @eventType CTATMessageEvent.sendingMessage
	 * @eventType CTATMessageEvent.messageSent
	 */
	/*
	this.processComponentAction=function processComponentAction (sai, 
													 			 tutorComponent, 
													 			 behaviorRecord, 
													 			 component) 
	{
		debug("processComponentAction(" +sai.getSelection() + "," + sai.getAction() + "," + sai.getInput() + ")","commShell");		
		debug("ProcessingConfirmDone : " + processingConfirmDone + " sai.getSelection().toLowerCase() = " +sai.getSelection().toLowerCase());
		debug("confirmdone = " + confirmDone);
		
		if (!processingConfirmDone && sai.getSelection().toLowerCase() == "done" && confirmDone)
		{				
			showConfirmDoneDialog(sai, tutorComponent, behaviorRecord, component);
			return;
		}
		
		if(processingConfirmDone)
			processingConfirmDone = false;
					
		lastStudentAction = getCurrentMs();
		var transactionID = CTATGuid.create(16);
		
		if ((useFlashSideLogging) && (!inAuthorTime))
		{
			debug("logging the action" + logger,"commShell");
			
			if (sai.getSelection()=="scrim")
			{
				debug ("Not logging any scrim actions (for now)");
			}
			else
			{
				//if ((sai.getSelection() == "hint") || (sai.getSelection()=="null.nextButton") || (sai.getSelection()=="null.previousButton"))
				//{
				//	logger.logSemanticEvent(transactionID, sai, "HINT_REQUEST", "");
				//}
				//else
				//	logger.logSemanticEvent(transactionID, sai, "ATTEMPT", "");	
			}
		}

		if (behaviorRecord) 
		{
			debug("recording the action","commShell");
			debug(sai.toXMLString(true));
			
			var tsMessage="";
			var ctatMess=null;
			
			if (tutorComponent) 
			{
				tsMessage = CTATTutoringServiceMessageBuilder.createInterfaceActionMessage(transactionID, sai);
				ctatMess = new CTATMessage(tsMessage);
				
				if (CTATLinkData.messageHistory != null) 
					CTATLinkData.messageHistory.addToolMessage(ctatMess);
			}
			else 
			{
				tsMessage = CTATTutoringServiceMessageBuilder.createUntutoredActionMessage(transactionID, sai);
				ctatMess = new CTATMessage(tsMessage);
				
				if (CTATLinkData.messageHistory != null)
					CTATLinkData.messageHistory.addUnpairedMessage(ctatMess);
			}
			
			dispatchEvent(new CTATMessageEvent(CTATMessageEvent.SENDING_MESSAGE, ctatMess));
			
			try 
			{
				debug("connectionManager ....");
				
				if(connectionManager.isConnected("MAIN"))
					connectionManager.sendMainConnectionMessage(tsMessage);
			}
			catch (error:Error) 
			{
				if (error.errorID == 2002)
				{
					displayWarning("The socket to the behavior recorder was closed, make sure the authoring tools are open and try again.");
				}
				else
					handleFlashError(error);
				return;
			}
			
			//dispatchEvent(new CTATMessageEvent(CTATMessageEvent.MESSAGE_SENT, ctatMess));
		}
	};
	*/
	/**
	 * Requests a hint from the tutoring service.
	 * <p>Hint requests and normal component actions are very similar, however, a hint request requires knowledge of
	 * of the previous focus to help with tutoring service decide which hints to send in response.</p>
	 */
	this.requestHint=function requestHint() 
	{
		pointer.debug("requestHint()");
		
		// Note: convert SAI to Complex SAI
		var hintSAI=new CTATSAI("hint", "ButtonPressed", "-1");
		
		/*
		if (previousFocus!=null) 
		{
			if (previousFocus instanceof CTATCompBase)
			{
				hintSAI.addSelectionActionInput(previousFocus.getSAI().getSelection(),"PreviousFocus");
			}	
			else
			{
				pointer.debug ("Current focus is not a CTAT component, can't ask for a hint yet");
				
				//hintSAI.addSelectionActionInput((previousFocus as DisplayObject).name, "PreviousFocus");
			}
		}
		*/
						
		this.processComponentAction(hintSAI);		
	};
	/**
	 * 
	 */
	this.processDone=function processDone() 
	{
		pointer.debug("processDone()");
		
		//this.globalReset ();
				
		if (confirmDone==true)
		{
			showYesNoDialog ("Confirm Done","Are you sure you are done?",this.processDoneContinue);	
		}
		else
		{
			var doneSAI=new CTATSAI("done", "ButtonPressed", "-1");
							
			pointer.processComponentAction(doneSAI);
		}
	};
	/**
	 * 
	 */
	this.processDoneContinue=function processDoneContinue(aResult) 
	{
		pointer.debug("processDoneContinue()");
		
		if (aResult==true)
		{
			var doneSAI=new CTATSAI("done", "ButtonPressed", "-1");

			pointer.processComponentAction(doneSAI);						
		}	
	}	
	/**
	 * 
	 */
	this.processCorrectAction=function processCorrectAction (aMessage)
	{
		pointer.debug("processCorrectAction()");
		
		aMessage.setGradeResult ("correct");
		
		if (gradingProcessor!=null)
		{
			debug ("Calling custom grading processor ...");
			aMessage ();
			return;
		}
		
		var sel=aMessage.getSelection ();
		
		var comp=tools.findComponent (sel, aMessage.getInput ());
		
		if (comp!=null)
		{
			comp.showCorrect ();
			
			if (sel=="done")
			{
				commLibrary.sendXML (commMessageBuilder.createProblemSummaryRequestMessage());
			}
		}
		else
			pointer.debug("Error: component is null for selection " + sel);			
	};
	
	/**
	 * 
	 */
	this.processInCorrectAction=function processInCorrectAction (aMessage)
	{
		pointer.debug("processInCorrectAction()");

		aMessage.setGradeResult ("incorrect");
		
		if (gradingProcessor!=null)
		{
			debug ("Calling custom grading processor ...");
			aMessage ();
			return;
		}		
		
		var sel=aMessage.getSelection ();

		var comp=tools.findComponent (sel, aMessage.getInput ());

		if (comp!=null)
		{
			comp.showInCorrect ();
		}
		else
			pointer.debug("Error: component is null for selection " + sel);		
	};

	/**
	 *
	 */
	this.processHighlightMsg=function processHighlightMsg (aMessage)
	{
		pointer.debug("processHighlightMsg()");
		
		var sel=aMessage.getSelection ();
		
		var comp=tools.findComponent (sel);
		
		if (comp!=null)
		{
			comp.setHintHighlight (true);
		}
		else
			pointer.debug("Error: component is null for selection " + sel);
	};
	
	/**
	 *
	 */
	this.processUnHighlightMsg=function processUnHighlightMsg (aMessage)
	{
		pointer.debug("processUnHighlightMsg()");
		
		var sel=aMessage.getSelection ();
		
		var comp=tools.findComponent (sel);
		
		if (comp!=null)
		{
			comp.setHintHighlight (false);
		}
		else
			pointer.debug("Error: component is null for selection " + sel);		
	};
	
	/**
	 * 
	 */
	this.processAssociatedRules=function processAssociatedRules (aMessage,indicator,advice)
	{
		pointer.debug("processAssociatedRules()");
		
		// Log the message ...
		if (commMessageHandler.getInStartState ()==true)
		{
			var logSAI=message.getSAI();
			var semanticEvent="";
			var evalObj=new CTATActionEvaluationData("");
			//var advice=message.getProperty ("TutorAdvice");
			
			pointer.debug ("Found tutor advice: " + advice);
			
			if (message.getIndicator() == "Hint" || message.getIndicator() == "HintWindow") 
			{
				debug ("Preparing log message to indicate a hint response","commShell");
				
				/*
				evalObj.setCurrentHintNumber (CTATLinkData.hints.getCurrentHintIndexForLogging());
				evalObj.setTotalHintsAvailable (CTATLinkData.hints.getHintCount());
				*/
				
				evalObj.setCurrentHintNumber (hintIndex);
				evalObj.setHintsAvailable (hints.length);
				
				evalObj.setEvaluation ("HINT");
				semanticEvent = "HINT_MSG";
				//advice=CTATLinkData.hints.getCurrentHint();
				advice=hints [hintIndex];
			}
			else 
			{
				if (message.getIndicator() == "Correct")
					evalObj.setEvaluation("CORRECT");
				else
					evalObj.setEvaluation("INCORRECT");
					
				semanticEvent = "RESULT";
			}
			
			debug ("Adding custom field names ...");
			
			commLogMessageBuilder.resetCustomFields();
			commLogMessageBuilder.addCustomField("step_id",message.getProperty("StepID"));
						
			debug ("Sending log message ...");
										
			if (commLoggingLibrary!=null)
			{
				commLoggingLibrary.logTutorResponse (transactionID,
													 logSAI,
													 semanticEvent,
													 "",
													 evalObj,
													 advice,
													 skillsToLog,
													 commLogMessageBuilder.getCustomFieldNames(),
													 commLogMessageBuilder.getCustomFieldValues ());
			}
			else
			{
				this.debug ("Info: no logging library available!");
			}
		}		
		
		// Continue with processing ...
		/*
		if (indicator=="InCorrect")
		{
			//this.processInCorrectAction (aMessage);
		}
	
		if (indicator=="Correct")
		{
			//this.processCorrectAction (aMessage);
		}
		*/
		
		for (var i=0;i<feedbackComponents.length;i++)
		{
			var feedbackComponent=feedbackComponents [i];
			
			pointer.debug ("Showing tutor advice ...");

			feedbackComponent.showFeedback (advice);
		}
		
		this.updateSkillWindow ();	
	};
	
	/**
	 * 
	 */
	this.processBuggyMessage=function processBuggyMessage (aMessage)
	{
		pointer.debug("processBuggyMessage()");
	};
	
	/**
	 * 
	 */
	this.processSuccessMessage=function processSuccessMessage (aMessage)
	{
		pointer.debug("processSuccessMessage()");
	};
	
	/**
	 * 
	 */
	this.processInterfaceAction=function processInterfaceAction (aMessage)
	{			
		pointer.debug("processInterfaceAction("+aMessage.getSelection ()+","+aMessage.getAction ()+","+aMessage.getInput ()+")","commShell");

		// Prep all the various variables and pointers ...
		
		var targetComponent=tools.findComponent (aMessage.getSelection ());
		
		if (targetComponent==null)
		{
			pointer.debug ("Internal error: unable to find pointer to component object");
			return;
		}
		
		// Log the message ...
		
		if (commMessageHandler.getInStartState ()==false)
		{
			pointer.debug("This is not a start state TPA so it should be logged","commShell");
			
			if (commLoggingLibrary!=null)
			{
				commLoggingLibrary.logSemanticEvent (message.getTransactionID(), message.getSAI(), "ATTEMPT", message.getProperty("subtype"));
			}
			else
			{
				this.debug ("Info: no logging library available!");
			}
		}
		else
			pointer.debug ("Info for incoming TPA, commMessageHandler.getInStartState (): "+commMessageHandler.getInStartState ());		
		
		// Call the action on the component ...
		
		targetComponent [aMessage.getAction ()] (aMessage.getInput ());				
	};
	
	/**
	 * 
	 */
	this.processInterfaceIdentification=function processInterfaceIdentification (aMessage)
	{
		pointer.debug("processInterfaceIdentification()");
		
		// Not relevant for HTML5?
	};
	
	/**
	 * 
	 */
	this.processAuthorModeChange=function processAuthorModeChange (aMessage)
	{
		pointer.debug("processAuthorModeChange()");
		
		// Not relevant for HTML5 (yet)?		
	};

	/**
	 * 
	 */
	this.processShowHintsMessage=function processShowHintsMessage (aMessage)
	{
		pointer.debug("processShowHintsMessage()");
		
		// Instead handled by associated rules
	};
	
	/**
	 * 
	 */
	this.processConfirmDone=function processConfirmDone (aMessage)
	{
		pointer.debug("processConfirmDone()");
		
	};
	
	/**
	 * 
	 */
	this.processVersionInfo=function processVersionInfo (messageProperties)
	{
		pointer.debug("processVersionInfo()");
		
		// Not relevant?
	};
	
	/**
	 * 
	 */
	this.processTutoringServiceAlert=function processTutoringServiceAlert (messageProperties)
	{
		pointer.debug("processTutoringServiceAlert()");
	
		var aTitle="";
		var aMessage="";
		
		for (var t=0;t<messageProperties.length;t++)
		{
			var propNode=messageProperties [t];
													
			if (xmlParser.getElementName (propNode)=="ErrorType")
			{
				aTitle=xmlParser.getNodeTextValue (propNode)
			}
			
			if (xmlParser.getElementName (propNode)=="Details")
			{
				aMessage=xmlParser.getNodeTextValue (propNode)
			}			
		}	
		
		ctatscrim.scrimUp (aMessage);
	};
	
	/**
	 * 
	 */
	this.processTutoringServiceError=function processTutoringServiceError (messageProperties)
	{
		pointer.debug("processTutoringServiceError()");
		
		var aTitle="";
		var aMessage="";

		for (var t=0;t<messageProperties.length;t++)
		{
			var propNode=messageProperties [t];
													
			if (xmlParser.getElementName (propNode)=="ErrorType")
			{
				aTitle=xmlParser.getNodeTextValue (propNode)
			}
			
			if (xmlParser.getElementName (propNode)=="Details")
			{
				aMessage=xmlParser.getNodeTextValue (propNode)
			}
		}
		
		ctatscrim.scrimDown();
		ctatscrim.errorScrimUp(aTitle+" - "+aMessage);
	};
	
	/**
	 * 
	 */
	this.processProblemSummaryResponse=function processProblemSummaryResponse (aMessage)
	{
		pointer.debug("processProblemSummaryResponse()");
	
		//pointer.debug ("Check: " + aMessage);
	
		var generator=new CTATXML ();
	
		commLMSService.sendSummary	(generator.xmlToString (aMessage.getXMLObject ()));
		
		lastMessage=true;
	};

	/**
	 * 
	 */
	this.processProblemRestoreEnd=function processProblemRestoreEnd (aMessage)
	{
		pointer.debug("processProblemRestoreEnd()");
		
	};
	/**
	 * 
	 */
	this.processHintResponse=function processHintResponse (aMessage,aHintArray)
	{
		pointer.debug("processHintResponse()");
				
		// First process the array of hints ...
		
		//if(pointer.getEnabled()==true)
		//{		
			for (var i=0;i<feedbackComponents.length;i++)
			{
				var feedbackComponent=feedbackComponents [i];
				
				pointer.debug ("Calling showHint () ...");

				feedbackComponent.showHint (aHintArray);
			}
		//}
			
		// Next show any hints if necessary ...
			
		var highlightSAI=aMessage.getSAI();
		
		if (highlightSAI!=null)
		{
			var highlightSelection=highlightSAI.getSelection ();
			
			if (highlightSelection!=null)
			{
				var aComponent=tools.findComponent (highlightSelection);
				
				if (aComponent!=null)
					aComponent.setHintHighlight (true);
				else
					pointer.debug ("Unable to find component name in list: " + aComponent);
			}
			else
				pointer.debug ("Error: no highlight selection present in SAI");
		}
		else
			pointer.debug ("Warning: no SAI found in highlight message");
		
		// All done
	};
	/**
	 * 
	 */
	this.globalReset=function globalReset ()
	{
		pointer.debug ("globalReset ()");
		
		// Reset global variables ...
		
		scriptElement="";
		
		var tools=new CTATShellTools ();
		
		tools.listComponents ();
		
		// First iterate through all the regular gradeable components ...
		
		for (var i=0;i<components.length;i++)
		{
			var aDesc=components [i];
						
			var component=aDesc.getComponentPointer ();
			
			if (component!=null)
			{
				pointer.debug ("Calling reset on regular component ...");				
				
				component.reset ();
			}
		}
		
		// Next iterate through all components that are not in our main list ...
		
		/*
		for (var j=0;j<feedbackComponents.length;j++)
		{
			var feedbackComponent=feedbackComponents [j];
			
			pointer.debug ("Calling reset on feedback component ...");

			feedbackComponent.reset ();
		}
		*/		
		
		// Finally reset the CommShell ...
		
		this.reset ();
	};
	/**
	* See the following url for more information on the tutor <-> tutoring service protocol:
	* https://docs.google.com/document/d/1B4r8jf4vv8dDkL5ULl1aSMngpmS5TevB1qsGyjSweho/edit
	*/
	this.nextProblem=function nextProblem (aMessage)
	{
		pointer.debug("nextProblem ()");
	
		if (isBlank (aMessage))
		{
			pointer.debug("Message is blank, requesting next problem ...");
		
			var vars=flashVars.getRawFlashVars ();
									
			var url=vars ['run_problem_url'];
						
			commLibrary.send (url);
		}
		else
		{
			pointer.debug("Message contains html data, writing ...");
							
			try
			{
				document.close(); // if open
			}
			catch (err)			
			{
				alert ("Error closing document: " + err.message);
			}
			
			try
			{
				document.write (aMessage);
			}
			catch (err)			
			{
				alert ("Error writing document: " + err.message);
			}			
		}	
	};
	/**
	 *  
	 */
	this.updateSkillWindow=function updateSkillWindow ()
	{
		pointer.debug("updateSkillWindow()");
		
		var skillWindow=tools.findComponentByClass ("CTATSkillWindow");
		
		if (skillWindow!=null)
		{
			//skillWindow.drawComponent ();
		}
		else
			pointer.debug("Info: no skill window available");
	};
	/**
	 * 
	 */
	this.processComponentFocus=function processComponentFocus(aComponent)
	{
		//useDebugging=true;
		
		pointer.debug("processComponentFocus("+aComponent.getName ()+","+aComponent.getClassName ()+")");
		
		if ((aComponent.getClassName ()=="CTATTextInput") || (aComponent.getClassName ()=="CTATTextField") || (aComponent.getClassName ()=="CTATTextArea"))
		{
			selectedTextInput=aComponent;
			
			// Trigger custom keyboard on mobile devices ...
			
			if (mobileAPI!=null)
			{
				if (mobileAPI.getEnabled ()==true)
				{
					mobileAPI.processTextFocus (aComponent.getX (),
												aComponent.getY (),
												aComponent.getWidth (),
												aComponent.getHeight ());
				}	
			}
		}
		else
			selectedTextInput=null;
		
		//useDebugging=false;
	};
}

CTATCommShell.prototype = Object.create(CTATBase.prototype);
CTATCommShell.prototype.constructor = CTATCommShell;
