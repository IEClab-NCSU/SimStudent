/**-----------------------------------------------------------------------------
 $Author: vvelsen $
 $Date: 2015-02-12 14:14:28 -0500 (Thu, 12 Feb 2015) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATCommunication/CTATCommShell.js $
 $Revision: 21845 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATCommShell');

goog.require('CTATActionEvaluationData');
goog.require('CTATBase');
goog.require('CTATCommLibrary');
goog.require('CTATCurriculumService');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATGuid');
goog.require('CTATHTMLManager');
goog.require('CTATLoggingLibrary');
goog.require('CTATLogMessageBuilder');
goog.require('CTATMessage');
//goog.require('CTATMessageEvent');
goog.require('CTATMessageHandler');
goog.require('CTATSAI');
goog.require('CTATShellTools');
goog.require('CTATSkillSet')
goog.require('CTATTutoringServiceMessageBuilder');
goog.require('CTATXML');
goog.require('CTATNameTranslator');
goog.require('WatsonLogger');
goog.require('OLIMessageHandler');
goog.require('OLIComm');


/**
 *
 */
CTATCommShell = function()
{
	CTATBase.call(this, "CTATCommShell", "theShell");

	var tutorPointer=null;
	var tools=new CTATShellTools ();
	var pointer=this;
	//var messageParser=new CTATXML ();

	var messageParser=null;

	if (CTATConfig.parserType=="xml")
	{
		messageParser=new CTATXML ();
	}
	else
	{
		messageParser=new CTATJSON ();
	}

	var gradingProcessor=null;
	var eventListeners=new Array ();

	/**
	 *
	 */
	this.init=function init (aTutor)
	{
		this.ctatdebug ("init ()");
	
		var vars=flashVars.getRawFlashVars ();
		if (vars["tutor_env"] == "OLI") {
			
			vars["back_dir"] = "https://kona.pslc.cs.cmu.edu:8443/SimStudentServlet/WEB-INF/classes";
			vars["back_entry"] = "interaction.ModelTracerBackend";
			vars["remoteSocketURL"] = "https://kona.pslc.cs.cmu.edu:8443/SimStudentServlet/serv";	
			
			//vars["Argument"]="-traceOn -folder informallogic -problem if_p_or_q_then_r -ssTypeChecker informallogic.MyFeaturePredicate.valueTypeChecker";	
	
		}
		
		
		/*
		vars["back_dir"] = "http://localhost:9999/SimStudentServlet/WEB-INF/classes";
		vars["remoteSocketURL"] = "serv";
		*/
		var prefix="http://";

		if (vars ['connection']=='https')
		{
			prefix="https://";
		}

		if (vars ["remoteSocketURL"].indexOf ("http")!=-1)
		{
			prefix="";
		}

		//drawText ("Running HTML5 CTAT version: " + version,2,10);

		tutorPointer=aTutor;

		var generator=new CTATGuid ();

		contextGUID=generator.guid ();

		//this.ctatdebug ("A");

		processSkills ();

		//this.ctatdebug ("B");

		commMessageHandler=new CTATMessageHandler ();
		commMessageHandler.assignHandler (this);
		commMessageBuilder=new CTATTutoringServiceMessageBuilder ();
		commLogMessageBuilder=new CTATLogMessageBuilder ();
		commLibrary=new CTATCommLibrary ();
		commLMSService=new CTATCurriculumService (commLibrary);


		evntSource = new CTATJavaGet();


		//this.ctatdebug ("C2");

		commLoggingLibrary=new CTATLoggingLibrary ();

		//this.ctatdebug ("C3");

		var vars=flashVars.getRawFlashVars ();

		flashVars.setTimeZone (null); // Force detection of timezone

		//this.ctatdebug ("D");

		var htmlManager=new CTATHTMLManager ();

		if (vars ['info']!="")
		{
			/*
			if (javaScriptInfo!=undefined)
			{
				javaScriptInfo(htmlManager.entitiesConvert (vars ['info']));
			}
			*/
		}
		else
			this.ctatdebug ("There is no info flash var");

		if (vars["tutor_env"] == "OLI") {
			oliMessageHandler = new OLIMessageHandler();
			oliMessageHandler.assignHandler(this);
			oliComm = new OLIComm(vars["superactivity_url"]);
			oliComm.loadClientConfig();
		} else {
			var a = commMessageBuilder.createSetPreferencesMessage (version);
			commLibrary.setHandler(commMessageHandler);
			commLibrary.send_post(vars["remoteSocketURL"], a);
		}
	};
	/**
	*
	*/
	this.addEventListener=function addEventListener (aListenerComponent)
	{
		eventListeners.push (aListenerComponent);
	};
	/**
	*
	*/
	this.propagateShellEvent=function propagateShellEvent (anEvent,aMessage)
	{
		ctatdebug ("propagateShellEvent ("+anEvent+")");

		for (var i=0;i<eventListeners.length;i++)
		{
			eventListeners [i].processCommShellEvent (anEvent,aMessage);
		}
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
		pointer.ctatdebug ("reset ()");

		interfaceElement=null;

		commMessageHandler.reset ();
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
		pointer.ctatdebug ("processSkills ()");

		skillSet=new CTATSkillSet ();

		var vars=flashVars.getRawFlashVars ();

		if (vars ['skills']!=null)
		{
			skillSet.fromXMLString(vars ['skills']);
		}
	}
	/**
	 *
	 * @param anX
	 * @param anY
	 */
	function drawText (aString,anX,anY)
	{
		pointer.ctatdebug ("drawText ()");

    	ctx.fillText(aString,anX,anY);
	}
	/**
	 *
	 */
	this.focusNextComponent=function focusNextComponent (aComponent)
	{
		//useDebugging=true;

		this.ctatdebug ("focusNextComponent ()");

		if (oldComponentFocus!=null)
		{
			if (oldComponentFocus instanceof CTATCompBase)
			{
				oldComponentFocus.setHintHighlight (false,null,null);
			}
		}

		var currentTabIndex=aComponent.getTabIndex ();
		var newComponent=null;

		currentTabIndex++;

		this.ctatdebug ("Finding next component with tab index: "+ currentTabIndex + " or higher, examining " + components.length + " components ...");

		for (var i=0;i<components.length;i++)
		{
			var ref=components [i];

			var component=ref.getComponentPointer ();

			if (component!=null)
			{
				// getDebugger.ctatdebug ("Component: " + component.getName () + " of instance: " + component.getClassName ());

				if (component.getTabIndex ()==currentTabIndex)
				{
					this.ctatdebug ("Found a candidate component for new focus");
					newComponent=component;
				}
			}
			else
				this.ctatdebug ("Error: component pointer is null");
		}

		if (newComponent==null)
		{
			this.ctatdebug ("Internal error, unable to resolve currentComponentPointer");
		}
		else
		{
			//useDebugging=true;
			this.ctatdebug ("From: " + aComponent.getName () + ", to: " + newComponent.getName ());
			//useDebugging=false;

			newComponent.getComponent().focus();

			//newComponent.setHintHighlight (true);
		}

		//useDebugging=false;
	};
	/**
	 *
	 */
	this.gradeComponent=function gradeComponent (aComponent)
	{
		this.ctatdebug ("gradeComponent ("+aComponent.getName ()+","+aComponent.getClassName ()+")");

		// First let's reset the done button

		var doneButton=tools.findComponentByClass ("CTATDoneButton");

		if ((doneButton!=null) && (doneButton!=aComponent))
		{
			doneButton.setHintHighlight (false,null,null);
		}
		else
			pointer.ctatdebug("Info: no done button available to reset");

		// Next well run a name translation in case this is needed. For example in the
		// case of a spreadsheet where the cell name can have multiple designations

		if (nameTranslator!=null)
		{
			nameTranslator.translateFromCTAT (aComponent.getName ());
		}
		else
		{
			this.ctatdebug ("Info: no name translator provided, using as-is");
		}

		if (aComponent==null)
		{
			this.ctatdebug ("Internal error, provided component is null");
			return;
		}
		else
		{
			this.ctatdebug ("Info: we have a valid component, grading ...");
		}

		if(aComponent.getClassName ()=="CTATSubmitButton")//bypass 'Do no tutor'
		{
			var targets = aComponent.getTargets();
			ctatdebug (targets);

			for(var i = 0; i < targets.length; i++)
			{
				var comp = findComponent(targets[i]);

				comp.oldShowCorrect_0 = comp.showCorrect;
				comp.oldShowInCorrect_0 = comp.showInCorrect;
				comp.showCorrect = function()
				{
					comp.oldShowCorrect_0();
					aComponent.showCorrect();
					//restore old showCorrect to avoid infinite recursion
					comp.showCorrect = comp.oldShowCorrect_0;
				};

				comp.showInCorrect = function()
				{
					comp.oldShowInCorrect_0(null); // How to get a pointer to aMessage
					aComponent.showInCorrect(null); // How to get a pointer to aMessage
					//restore old showIncorrect to avoid infinite recursion
					comp.showInCorrect = comp.oldShowInCorrect_0;
				}

				pointer.gradeComponent(comp);
			}

			return;
		}

		if (aComponent.getTutorComponent ()=="Do not tutor")
		{
			// need to send messages untutored even if it is "Do not tutor"
			// to maintain state in the BR.  Also necessary for collaboration.
			pointer.processComponentAction(aComponent.getSAI(),false);
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

		this.ctatdebug ("Checking for back grading: " + aComponent.getName ());

		/*
		if(aComponent.getClassName ()=="CTATTextArea")
		{
			this.ctatdebug ("Backgrading ...");

			this.ctatdebug ("Grading value: " + aComponent.getValue ());

			if (isBlank (aComponent.getValue ())==true)
			{
				this.ctatdebug ("Empty component, nothing to grade");
				return;
			}

			var textSAI=new CTATSAI(aComponent.getName (), "UpdateTextArea", aComponent.getValue ());

			pointer.processComponentAction(textSAI);

			//aComponent.showCorrect();

			return;
		}
		*/

		if ((aComponent.getClassName ()=="CTATTextArea") ||
				(aComponent.getClassName ()=="CTATTextInput") ||
				(aComponent.getClassName ()=="CTATTextField"))
		{
			//useDebugging=true;

			this.ctatdebug ("Backgrading ...");

			this.ctatdebug ("Grading "+aComponent.getClassName () + " with value: " + aComponent.getValue ());

			if (isBlank (aComponent.getValue ())==true)
			{
				this.ctatdebug ("Empty component, nothing to grade");
				return;
			}

			var textSAI=null;

			if (aComponent.getClassName ()=="CTATTextField")
			{
				textSAI=new CTATSAI(aComponent.getName (), "UpdateTextField", aComponent.getValue ());
			}

			//Changed by dhruv for testing, 07/21/2014.
			if (aComponent.getClassName ()=="CTATTextInput")
			{
				textSAI=new CTATSAI(aComponent.getName (), "UpdateTextField", aComponent.getValue ());
			}

			if (aComponent.getClassName ()=="CTATTextArea")
			{
				textSAI=new CTATSAI(aComponent.getName (), "UpdateTextArea", aComponent.getValue ());
				//textSAI=new CTATSAI(aComponent.getName (), "UpdateTextField", aComponent.getValue ());
			}

			pointer.processComponentAction(textSAI);

			//useDebugging=false;

			return;
		}

		if (aComponent.getClassName ()=="CTATComboBox")
		{
			this.ctatdebug ("Grading combobox ...");

			//var combobox=aComponent.getHTMLComponent ();

			//var strUser = combobox.options[combobox.selectedIndex].value;

			//if (isBlank (strUser)==true)
			if (!aComponent.valid_selection())
			{
				this.ctatdebug ("Empty component, nothing to grade");
				return;
			}

			var tsMessage= aComponent.getSAI(); //new CTATSAI(aComponent.getName (), "UpdateComboBox", strUser);

			pointer.processComponentAction(tsMessage);

			return;
		}

		this.ctatdebug ("Grading regular component ...");

		if(aComponent.getClassName ()=="CTATRadioButton")
		{
			var tsMessage=new CTATSAI(aComponent.getComponentGroup (),
									  "UpdateRadioButton",
									  aComponent.getRadioInput ());

			pointer.processComponentAction(tsMessage);

			return;
		}

		if(aComponent.getClassName ()=="CTATTableGoogle")
		{
			var tsMessage=new CTATSAI(aComponent.getCurrentSelection (),
									  "UpdateTextArea",
									  aComponent.getCurrentValue ());

			pointer.processComponentAction(tsMessage);

			return;
		}

		if(aComponent.getClassName ()=="CTATCheckBox")
		{
			var checkboxes = tools.findComponent(aComponent.getComponentGroup());
			var cbinputs = [];
			for (var cbi=0; cbi<checkboxes.length; cbi++) {
				var cb = checkboxes[cbi];
				cbinputs.push(cb.getCheckBoxInput());
			}
			var tsMessage=new CTATSAI(aComponent.getComponentGroup (),
									  "UpdateCheckBox",
									  cbinputs.join(';'));

			pointer.processComponentAction(tsMessage);

			return;
		}

		if(aComponent.getClassName ()=="CTATSubmitButton")
		{
			var targets = aComponent.getTargets();

			for(var i = 0; i < targets.length; i++)
			{
				var comp = findComponent(targets[i]);
				comp.oldShowCorrect_0 = comp.showCorrect;
				comp.oldShowInCorrect_0 = comp.showInCorrect;
				comp.showCorrect = function(aMessage)
				{
				  comp.oldShowCorrect_0(aMessage); // How to get a pointer to aMessage
				  aComponent.showCorrect(aMessage); // How to get a pointer to aMessage
				  //restore old showCorrect to avoid infinite recursion
				  comp.showCorrect = comp.oldShowCorrect_0;
				};

				comp.showInCorrect = function(aMessage)
				{
				  comp.oldShowInCorrect_0(aMessage); // How to get a pointer to aMessage
				  aComponent.showInCorrect(aMessage); // How to get a pointer to aMessage
				  //restore old showIncorrect to avoid infinite recursion
				  comp.showInCorrect = comp.oldShowInCorrect_0;
				}

				pointer.gradeComponent(comp);
			  }

			  return;
		}

		var tsMessage=aComponent.getSAI();

		pointer.processComponentAction(tsMessage);
	};
	/**
	 *
	 */
	this.processMessage=function processMessage(aMessage)
	{
		this.ctatdebug ("processMessage ()");

		commMessageHandler.processMessage (aMessage);

		this.ctatdebug ("processMessage () done");
	};

	this.oliClientConfig = function oliClientConfig(logUrl)
	{
		var vars = flashVars.getRawFlashVars();
		watsonLogger = new WatsonLogger(logUrl);
		watsonLogger.loadOLILogger(vars["oli_auth_token"],
								   vars["session_id"],
								   vars["resource_type_id"],
								   vars["user_guid"]);
		oliComm.beginSession();

	};

	this.oliBeganSession = function oliBeganSession(resourceFileId)
	{
		oliComm.loadContentFile(resourceFileId);
	};

	this.oliLoadedContentFile = function oliLoadedContentFile(brdUrls)
	{
		var vars = flashVars.getRawFlashVars();
		// for now just one brd url
		commLibrary.setHandler(oliMessageHandler);
		commLibrary.send(brdUrls[0]);

	};

	this.oliRecievedBrd = function oliRecievedBrd(brd)
	{
		//this.debug ("Connecting to: " + "http://" + vars ["remoteSocketURL"] + ":" + vars ["remoteSocketPort"]);

		this.ctatdebug("Connecting to: SimStudentBaseServlet");

		var vars = flashVars.getRawFlashVars();
		vars["question_file"] = brd;
		var a = commMessageBuilder.createSetPreferencesMessage (version);
		//commLibrary.send_post ("http://" + vars ["remoteSocketURL"] + ":" + vars ["remoteSocketPort"],a);

	 	commLibrary.setHandler(commMessageHandler);
		commLibrary.send_post(vars["remoteSocketURL"],a);//plugged in to servlet instead
		console.log("sent brd to servlet!");
	}
	/**
	 *
	 */
	this.processStartProblem=function processStartProblem ()
	{
		this.ctatdebug ("processStartProblem ()");

		if (CTATConfig.external=="google")
		{
			this.showFeedback ("The tutor is starting, please wait ...");
		}
	}
	/**
	 *
	 */
	this.processStartState=function processStartState ()
	{
		this.ctatdebug ("processStartState ()");

		if (tutorPointer!=null)
		{
			this.ctatdebug ("Calling tutor.createInterface () ...");

			tutorPointer.createInterface ();
		}
		else
		{
			this.ctatdebug ("Error: no tutor object available, calling createInterface globally ...");

			if (window.hasOwnProperty('createInterface')) {
				window.createInterface ();
			}
		}

		this.ctatdebug ("Logging start of problem ...");

		if (watsonLogger != null)
		{
			var attemptNumber = 1;
			watsonLogger.startAttempt(attemptNumber);
		}

		if (commLoggingLibrary!=null)
		{
			commLoggingLibrary.startProblem ();
		}
		else
		{
			this.ctatdebug ("Info: no logging library available!");
		}

		this.ctatdebug ("End of start state, inspecting suppressStudentFeedback ...");

		if (suppressStudentFeedback==true)
		{
			this.ctatdebug ("Hiding hint button ...");

			var tools=new CTATShellTools ();
			var hintButton=tools.findComponentByClass ("CTATHintButton");

			if (hintButton!=null)
			{
				hintButton.SetVisible (false);
			}
		}

		if (CTATConfig.external=="google")
		{
			pointer.ctatdebug("Calling google app script hint request driver ...");

			try
			{
				google.script.run.withSuccessHandler(pointer.onNOPSuccess).
								  withFailureHandler(pointer.onNOPFailure).
								  resetOnEditQueue();
			}
			catch (err)
			{
				pointer.ctatdebug ("google.script.run: " + err.message);
			}

			this.showFeedback ("The tutor is ready, you can now freely interact with the spreadsheet");

			return;
		}

		this.ctatdebug ("processStartState () done");
	};
	/**
	 * Ported from AS3
	 */
	this.sendStartProblemMessage=function sendStartProblemMessage()
	{
		pointer.ctatdebug ("sendStartProblemMessage()");

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
		pointer.ctatdebug ("sendProblemSummaryRequest()");

		var builder = new CTATTutoringServiceMessageBuilder ();

		var tsMessage = builder.createProblemSummaryRequestMessage();

		commLibrary.setHandler(commMessageHandler);
		commLibrary.sendXML (tsMessage);
	};
	/**
	* Ported from AS3
	*/
	this.sendInterfaceDescriptionMessages=function sendInterfaceDescriptionMessages()
	{
		pointer.ctatdebug("sendInterfaceDescriptionMessages ()");

		// Not relevant for HTML5
	};
	/**
	 *
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.ctatdebug ("processSerialization()");

		// Process component specific pre-defined styles ...

		pointer.setText (this.label);

		// Process component custom styles ...

		//this.styles=aDescription.styles;

		this.styles=pointer.getGrDescription().styles;

		pointer.ctatdebug ("Processing " + this.styles.length + " styles ...");

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

			/*
			if(aStyle.styleName=="EnableDebugging")
			{
				pointer.setUseDebugging(aStyle.styleValue);
			};
			*/
		}
	};
	/**
	 *
	 */
	this.processComponentAction=function processComponentAction (sai,tutorComponent,behaviorRecord,component)
	{
		pointer.ctatdebug("processComponentAction(" +sai.getName() + " -> " +sai.getSelection() + "," + sai.getAction() + "," + sai.getInput() + ")");

		var generator=new CTATGuid ();

		var transactionID = generator.guid ();

	
	
		// First we log

		if (watsonLogger != null && !commMessageHandler.getInStartState())
		{
			
			pointer.ctatdebug("---->1");
			pointer.ctatdebug("---->1");
			pointer.ctatdebug("---->1");
			pointer.ctatdebug("---->1");

			if (sai.getSelection()=="scrim")
				{
					ctatdebug ("Not logging any scrim actions (for now)");
				}
				else
				{
					pointer.ctatdebug("(#(#(#(#(#(#(#(#(#(#");
					pointer.ctatdebug("(#(#(#(#(#(#(#(#(#(#");
					pointer.ctatdebug("(#(#(#(#(#(#(#(#(#(#");
			
					
					
					if ((sai.getSelection() == "hint") || (sai.getSelection()=="null.nextButton") || (sai.getSelection()=="null.previousButton"))
					{
						pointer.ctatdebug("---->2");
						pointer.ctatdebug("---->2");
						pointer.ctatdebug("---->2");
						pointer.ctatdebug("---->2");
						
						commLoggingLibrary.logSemanticEvent(transactionID, sai, "HINT_REQUEST", "");
						watsonLogger.logHint("hint","questionId","stepid","attemptNumber");
					}
					else
					{
						commLoggingLibrary.logSemanticEvent(transactionID, sai, "ATTEMPT", "");
					}
				}

		}
		else
		{
			this.ctatdebug ("Info: no logging library available!");
		}

		// Then we send to the TS

		//useDebugging=true;
		
		var builder = new CTATTutoringServiceMessageBuilder ();

		var tsMessage;
		if (tutorComponent!==false)
			tsMessage = builder.createInterfaceActionMessage(transactionID,sai);
		else
			tsMessage = builder.createUntutoredActionMessage(transactionID,sai);

		commLibrary.setHandler(commMessageHandler);
		commLibrary.sendXML (tsMessage);
		// Et Voila!

		//useDebugging=false;
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
				ctatdebug ("Not logging any scrim actions (for now)");
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
	*
	*/
	this.onEditSuccess=function onEditSuccess (selectedRange)
	{
		pointer.ctatdebug ("onEditSuccess ("+selectedRange+")");

		if (nameTranslator==null)
		{
			pointer.ctatdebug ("Error: CTAT name translator not available");
			return;
		}

		if (selectedRange.indexOf (":")!=-1)
		{
			pointer.ctatdebug ("Bump");
			pointer.showFeedback ("You're asking a hint for multiple cells, please select only a single cell.");

			return;
		}
		else
		{
			var mapped=nameTranslator.translateToCTAT (selectedRange);

			pointer.ctatdebug ("Info A1 notiation mapped to (if needed): " + mapped);

			var hintSAI = new CTATSAI("hint", "ButtonPressed", mapped);

			pointer.processComponentAction(hintSAI);

			pointer.propagateShellEvent ("REQUESTHINT",null);
		}

		pointer.ctatdebug ("onEditSuccess () done");
	};

	/**
	*
	*/
	this.onFailure=function onFailure (error)
	{
		pointer.ctatdebug ("onFailure ("+error.message+")");
	};

	/**
	*
	*/
	this.onNOPEditSuccess=function onNOPEditSuccess (selectedRange)
	{
		pointer.ctatdebug ("onNOPEditSuccess ("+selectedRange+")");
	};
	/**
	*
	*/
	this.onNOPFailure=function onNOPFailure (error)
	{
		pointer.ctatdebug ("onNOPFailure ("+error.message+")");
	};

	/**
	 * Requests a hint from the tutoring service.
	 * <p>Hint requests and normal component actions are very similar, however, a hint request requires knowledge of
	 * of the previous focus to help with tutoring service decide which hints to send in response.</p>
	 */
	this.requestHint=function requestHint()
	{
		//useDebugging=true;

		pointer.ctatdebug("requestHint(external -> "+CTATConfig.external+")");

		// Note: convert SAI to Complex SAI
		var hintSAI=null;
		if (oldComponentFocus!=null)
		{
			pointer.ctatdebug ("oldComponentFocus!=null, requesting hint for: " + oldComponentFocus.getSAI().getSelection());

			if (oldComponentFocus instanceof CTATCompBase)
			{
				hintSAI = new CTATSAI("hint", "ButtonPressed", -1);
								hintSAI.addSelection(oldComponentFocus.getSAI().getSelection());
								hintSAI.addAction("PreviousFocus");
			}
			else
			{
				pointer.ctatdebug ("Current focus is not a CTAT component, can't ask for a hint yet");

				//hintSAI.addSelectionActionInput((previousFocus as DisplayObject).name, "PreviousFocus");
			}
		}
		else
		{
			pointer.ctatdebug ("oldComponentFocus==null");

			hintSAI = new CTATSAI("hint", "ButtonPressed", -1);
		}


		this.processComponentAction(hintSAI);

		this.propagateShellEvent ("REQUESTHINT",null);

		pointer.ctatdebug ("'external' has configuration that doesn't match anything: " + CTATConfig.external);

		//useDebugging=false;
	};
	/**
	 *
	 */
	this.processDone=function processDone()
	{
		pointer.ctatdebug("processDone()");

		//this.globalReset ();

		if (confirmDone==true)
		{
			//this.showYesNoDialog ("Confirm Done","Are you sure you are done?",this.processDoneContinue);

			ctatscrim.confirmScrimUp ("Are you sure you are done?",this.processDoneContinue,this.processDoneCancel);
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
		pointer.ctatdebug("processDoneContinue()");

		ctatscrim.scrimDown();

		var doneSAI=new CTATSAI("done", "ButtonPressed", "-1");

		pointer.processComponentAction(doneSAI);
	}
	/**
	 *
	 */
	this.processDoneCancel=function processDoneCancel(aResult)
	{
		pointer.ctatdebug("processDoneCancel()");

		ctatscrim.scrimDown();
	}
	/**
	 *
	 */
	this.processCorrectAction=function processCorrectAction (aMessage)
	{
		pointer.ctatdebug("processCorrectAction()");

		aMessage.setGradeResult ("correct");

		this.clearFeedbackComponents ();

		if (gradingProcessor!=null)
		{
			ctatdebug ("Calling custom grading processor ...");
			//aMessage ();
			return;
		}

		var sel=aMessage.getSelection ();

		var compList=tools.findComponent (sel);

		if (compList!=null)
		{
			ctatdebug ("Processing " + compList.length + " components ...");

			for (var t=0;t<compList.length;t++)
			{
				ctatdebug ("Check " + compList [t]);

				if (compList [t])
				{
					compList [t].setHintHighlight (false,null,null);
					compList[t].executeSAI(aMessage); // necessary for replace an collaboration to work
					compList [t].showCorrect (aMessage);
				}
				else
				{
					pointer.ctatdebug ("Internal error, component pointer is null");
				}
			}

			if (sel=="done")
			{
				commLibrary.setHandler(commMessageHandler);
				commLibrary.sendXML (commMessageBuilder.createProblemSummaryRequestMessage());
			}
		}
		else
			pointer.ctatdebug("Error: component is null for selection " + sel);

		this.propagateShellEvent ("CORRECT",aMessage);
	};

	/**
	 *
	 */
	this.processInCorrectAction=function processInCorrectAction (aMessage)
	{
		pointer.ctatdebug("processInCorrectAction()");

		aMessage.setGradeResult ("incorrect");

		if (gradingProcessor!=null)
		{
			ctatdebug ("Calling custom grading processor ...");
			//aMessage ();
			return;
		}

		var sel=aMessage.getSelection ();

		var comp=tools.findComponent (sel);

		if (comp!=null)
		{
			for (var t=0;t<comp.length;t++)
			{
				comp[t].executeSAI(aMessage); // necessary for replace and collaboration to work
				comp[t].showInCorrect (aMessage);
			}
		}
		else
			pointer.ctatdebug("Error: component is null for selection " + sel);

		this.propagateShellEvent ("INCORRECT",aMessage);
	};

	/**
	 *
	 */
	this.processHighlightMsg=function processHighlightMsg (aMessage)
	{
		//useDebugging=true;

		pointer.ctatdebug("processHighlightMsg()");

		var sel=aMessage.getSelection ();

		var comp=tools.findComponent (sel);

		if (comp!=null)
		{
			for (var t=0;t<comp.length;t++)
			{
				comp [t].setHintHighlight (true,null,aMessage);
			}
		}
		else
			pointer.ctatdebug("Error: component is null for selection " + sel);

		this.propagateShellEvent ("HIGHLIGHT",aMessage);

		//useDebugging=false;
	};

	/**
	 *
	 */
	this.processUnHighlightMsg=function processUnHighlightMsg (aMessage)
	{
		pointer.ctatdebug("processUnHighlightMsg()");

		var sel=aMessage.getSelection ();

		var comp=tools.findComponent (sel);

		if (comp!=null)
		{
			for (var t=0;t<comp.length;t++)
			{
				comp [t].setHintHighlight (false,null,aMessage);
			}
		}
		else
			pointer.ctatdebug("Error: component is null for selection " + sel);

		this.propagateShellEvent ("UNHIGHLIGHT",aMessage);
	};

	/**
	 *
	 */
	this.processAssociatedRules=function processAssociatedRules (aMessage,indicator,advice)
	{
		pointer.ctatdebug("processAssociatedRules()");

		// Log the message ...
		if (commMessageHandler.getInStartState ()==false)
		{
			var logSAI=aMessage.getSAI();
			var semanticEvent="";
			var evalObj=new CTATActionEvaluationData("");
			//var advice=message.getProperty ("TutorAdvice");

			//aMessage.hasProperty ("Indicator");
			//listProperties (aMessage);

			pointer.ctatdebug ("Found tutor advice: " + advice);

			if ((indicator == "Hint") || (indicator == "HintWindow"))
			{
				ctatdebug ("Preparing log message to indicate a hint response","commShell");

				/*
				evalObj.setCurrentHintNumber (CTATLinkData.hints.getCurrentHintIndexForLogging());
				evalObj.setTotalHintsAvailable (CTATLinkData.hints.getHintCount());
				*/

				evalObj.setCurrentHintNumber (hintIndex);
				evalObj.setTotalHintsAvailable (hints.length);

				evalObj.setEvaluation ("HINT");
				semanticEvent = "HINT_MSG";
				//advice=CTATLinkData.hints.getCurrentHint();
				if (hints [hintIndex])
				{
					advice=hints [hintIndex];
				}
			}
			else
			{
				if (aMessage.getIndicator() == "Correct")
					evalObj.setEvaluation("CORRECT");
				else
					evalObj.setEvaluation("INCORRECT");

				semanticEvent = "RESULT";
			}

			ctatdebug ("Adding custom field names ...");

			commLogMessageBuilder.resetCustomFields();
			commLogMessageBuilder.addCustomField("step_id",aMessage.getProperty("StepID"));

			ctatdebug ("Sending log message ...");

			var skillObject=aMessage.getSkillsObject ();
			var skillsToLog=new Array ();
			if (skillObject!=null)
			{
				skillsToLog=skillObject.getTouched();
			}
			else
			{
				ctatdebug ("Error: skills object is null in message");
			}

			if (watsonLogger != null)
			{
				watsonLogger.logTutorResponse(logSAI.toString(),
											  "attemptNUmber",
											  evalObj.getEvaluation(),
											  "questionId",
											  "inputId");
			}
			if (commLoggingLibrary!=null)
			{
				commLoggingLibrary.logTutorResponse (aMessage.getTransactionID(),
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
				this.ctatdebug ("Info: no logging library available!");
			}
		}

		if (semanticEvent=="HINT_MSG")
		{
			this.showFeedback (advice);
		}

		this.updateSkillWindow ();
	};

	/**
	 *
	 */
	this.processBuggyMessage=function processBuggyMessage (aMessage)
	{
		pointer.ctatdebug("processBuggyMessage()");

		this.showFeedback (aMessage.getBuggyMsg ());
	};

	/**
	 *
	 */
	this.processSuccessMessage=function processSuccessMessage (aMessage)
	{
		pointer.ctatdebug("processSuccessMessage()");

		this.showFeedback (aMessage.getSuccessMessage ());
	};

	/**
	 *
	 */
	this.processInterfaceAction=function processInterfaceAction (aMessage)
	{
		pointer.ctatdebug("processInterfaceAction("+aMessage.getSelection ()+","+aMessage.getAction ()+","+aMessage.getInput ()+")","commShell");
		
		// Prep all the various variables and pointers ...

		var targetComponent=tools.findComponent (aMessage.getSelection ());

		if (targetComponent==null)
		{
			pointer.ctatdebug ("Internal error: unable to find pointer to component object");
			return;
		}

		pointer.ctatdebug ("Log the message ...");

		if (commMessageHandler.getInStartState ()==false)
		{
			pointer.ctatdebug("This is not a start state TPA so it should be logged","commShell");

			if (commLoggingLibrary!=null)
			{
				commLoggingLibrary.logSemanticEvent (aMessage.getTransactionID(), aMessage.getSAI(), "ATTEMPT", aMessage.getProperty("subtype"));
			}
			else
			{
				this.ctatdebug ("Info: no logging library available!");
			}
		}
		else
		{
			pointer.ctatdebug ("Info for incoming TPA, commMessageHandler.getInStartState (): "+commMessageHandler.getInStartState ());
		}

		pointer.ctatdebug ("Call the action on the component(s) -> ("+targetComponent.length+")...");

		for(var t=0;t<targetComponent.length;t++)
		{
			pointer.ctatdebug ("About to call " + aMessage.getAction () + " ("+aMessage.getInput ()+") on: " + aMessage.getSelection ());

			var target=targetComponent [t];

			target.executeSAI(aMessage);

			if (commMessageHandler.getInStartState ()==true)
			{
				// Commented out as per Ticket #511
				//if (lockWidget==true) // From the StateGraph message
				//{
					targetComponent [t].setEnabled (false);
				//}
			}
		}
	};

	/**
	 *
	 */
	this.processInterfaceIdentification=function processInterfaceIdentification (aMessage)
	{
		pointer.ctatdebug("processInterfaceIdentification()");

		// Not relevant for HTML5?
	};

	/**
	 *
	 */
	this.processAuthorModeChange=function processAuthorModeChange (aMessage)
	{
		pointer.ctatdebug("processAuthorModeChange()");

		// Not relevant for HTML5 (yet)?
	};

	/**
	 *
	 */
	this.processShowHintsMessage=function processShowHintsMessage (aMessage)
	{
		pointer.ctatdebug("processShowHintsMessage()");

		// Instead handled by associated rules
	};

	/**
	 *
	 */
	this.processConfirmDone=function processConfirmDone (aMessage)
	{
		pointer.ctatdebug("processConfirmDone()");

	};

	/**
	 *
	 */
	this.processVersionInfo=function processVersionInfo (messageProperties)
	{
		pointer.ctatdebug("processVersionInfo()");

		// Not relevant?
	};

	/**
	 *
	 */
	this.processTutoringServiceAlert=function processTutoringServiceAlert (messageProperties)
	{
		pointer.ctatdebug("processTutoringServiceAlert()");

		var aTitle="";
		var aMessage="";

		for (var t=0;t<messageProperties.length;t++)
		{
			var propNode=messageProperties [t];

			if (messageParser.getElementName (propNode)=="ErrorType")
			{
				aTitle=messageParser.getNodeTextValue (propNode)
			}

			if (messageParser.getElementName (propNode)=="Details")
			{
				aMessage=messageParser.getNodeTextValue (propNode)
			}
		}

		ctatscrim.scrimUp (aMessage);
	};

	/**
	 *
	 */
	this.processTutoringServiceError=function processTutoringServiceError (messageProperties)
	{
		pointer.ctatdebug("processTutoringServiceError()");

		var aTitle="";
		var aMessage="";

		for (var t=0;t<messageProperties.length;t++)
		{
			var propNode=messageProperties [t];

			if (messageParser.getElementName (propNode)=="ErrorType")
			{
				aTitle=messageParser.getNodeTextValue (propNode)
			}

			if (messageParser.getElementName (propNode)=="Details")
			{
				aMessage=messageParser.getNodeTextValue (propNode)
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
		pointer.ctatdebug("processProblemSummaryResponse()");

		//pointer.ctatdebug ("Check: " + aMessage);

		var generator=new CTATXML ();

		commLMSService.sendSummary	(generator.xmlToString (aMessage.getXMLObject ()));

		lastMessage=true;
	};

	/**
	 *
	 */
	this.processProblemRestoreEnd=function processProblemRestoreEnd (aMessage)
	{
		pointer.ctatdebug("processProblemRestoreEnd()");

	};
	/**
	*
	*/
	this.clearFeedbackComponents=function clearFeedbackComponents ()
	{
		pointer.ctatdebug ("clearFeedbackComponents ()");

		for (var i=0;i<feedbackComponents.length;i++)
		{
			var feedbackComponent=feedbackComponents [i];

			feedbackComponent.showHint (null);
		}
	}
	/**
	 *
	 */
	this.processHintResponse=function processHintResponse (aMessage,aHintArray)
	{
		//useDebugging=true;

		pointer.ctatdebug("processHintResponse()");

		// First process the array of hints ...

		//if(pointer.getEnabled()==true)
		//{
			for (var i=0;i<feedbackComponents.length;i++)
			{
				var feedbackComponent=feedbackComponents [i];

				pointer.ctatdebug ("Calling showHint () ...");

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
				pointer.ctatdebug ("Highlighting selection for hint: " + highlightSelection);

				var aComponent=tools.findComponent (highlightSelection);

				if (aComponent!=null)
				{
					for(var t=0;t<aComponent.length;t++)
					{
						aComponent [t].setHintHighlight (true,null,aMessage);
					}
				}
				else
					pointer.ctatdebug ("Unable to find component name in list: " + aComponent);
			}
			else
				pointer.ctatdebug ("Error: no highlight selection present in SAI");
		}
		else
			pointer.ctatdebug ("Warning: no SAI found in highlight message");

		// All done

		//useDebugging=false;
	};
	/**
	 *
	 */
	this.globalReset=function globalReset ()
	{
		pointer.ctatdebug ("globalReset ()");

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
				pointer.ctatdebug ("Calling reset on regular component ...");

				component.reset ();
			}
		}

		// Next iterate through all components that are not in our main list ...

		/*
		for (var j=0;j<feedbackComponents.length;j++)
		{
			var feedbackComponent=feedbackComponents [j];

			pointer.ctatdebug ("Calling reset on feedback component ...");

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
		pointer.ctatdebug("nextProblem ()");

		if (isBlank (aMessage))
		{
			pointer.ctatdebug("Message is blank, requesting next problem ...");

			var vars=flashVars.getRawFlashVars ();

			var url=vars ['run_problem_url'];

			commLibrary.send (url);
		}
		else
		{
			pointer.ctatdebug("Message contains html data, writing ...");

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
		pointer.ctatdebug("updateSkillWindow()");

		var skillWindow=tools.findComponentByClass ("CTATSkillWindow");

		if (skillWindow!=null)
		{
			//skillWindow.drawComponent ();
		}
		else
			pointer.ctatdebug("Info: no skill window available");
	};
	/**
	 *
	 */
	this.processComponentFocus=function processComponentFocus(aComponent)
	{
		//useDebugging=true;

		pointer.ctatdebug("processComponentFocus("+aComponent.getName ()+","+aComponent.getClassName ()+")");

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
	/**
	*
	*/
	this.showFeedback=function showFeedback(aMessage)
	{
		pointer.ctatdebug("showFeedback("+aMessage+")");

		for (var i=0;i<feedbackComponents.length;i++)
		{
			var feedbackComponent=feedbackComponents [i];

			pointer.ctatdebug ("Showing tutor advice ...");

			feedbackComponent.showFeedback (aMessage);
		}
	};

}

CTATCommShell.prototype = Object.create(CTATBase.prototype);
CTATCommShell.prototype.constructor = CTATCommShell;
