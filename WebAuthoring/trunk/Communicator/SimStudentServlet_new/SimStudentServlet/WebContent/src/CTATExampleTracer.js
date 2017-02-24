/**-----------------------------------------------------------------------------
 $Author: sewall $ 
 $Date: 2014-11-18 13:20:19 -0500 (Tue, 18 Nov 2014) $ 
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATExampleTracer.js $ 
 $Revision: 21617 $ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
/*
 * This file represents the core javascript example tracer, with wich the interface communicates.
 * NOTE: The tutor launchers must be run off a web server. Opening them directly in a browser off the filesystem
 * would cause AJAX requests to fail (due to browser security restrictions), and the tutor will not load.
 * 
 */
goog.provide('CTATExampleTracer');

goog.require('CTATBase');
goog.require('CTATHintPolicyEnum');
goog.require('CTATExampleTracerSAI');
goog.require('CTATExampleTracerSkill');
goog.require('CTATGraphParser');
goog.require('CTATMessage');
goog.require('CTATProblemSummary');
goog.require('CTATSkills');
goog.require('CTATTutorMessageBuilder');
goog.require('CTATXML');

CTATExampleTracer = function()
{
	CTATExampleTracer.prototype = Object.create(CTATBase.prototype);

	CTATBase.call(this, "CTATExampleTracer", "tracer");

    var xmlHeader = '<?xml version="1.0" encoding="UTF-8"?>';

    var that = this;
    var exampleTracer;
    var graph;
    var parser = new CTATXML();
    var serializer = new XMLSerializer();
    var errorSAI = null;
    var groupModel;
    var hintPolicy;
	var highlightRightSelection = true;
    //Problem Model vars
    var restoreProblemState = false;
    var feedbackSuppressed = false;
    var problemSummary = null; //of type CTATProblemSummary
    var problemName = null; //of type string
    /**
     *
     */
    this.receiveFromInterface = function(aMessage)
    {

        that.ctatdebug("CTATExampleTracer.receiveFromInterface(" + aMessage + ")");

        var xml = new CTATXML().parseXML(aMessage);
        var message = new CTATMessage(xml);

        that.ctatdebug("CTATExampleTracer.receiveFromInterface(msgType " + message.getMessageType() + ")");

        //Switch, depending on type of message.
        if (message.getMessageType() == "SetPreferences")
        {
            console.log("SetPreferences received.");

			problemName = message.getProperty('problem_name');
			that.ctatdebug("SetPreferences.problem_name = " + problemName);

            var setPrefChildren = parser.getElementChildren(parser.getElementChildren(xml)[1]);
            for (var i = 0; i < setPrefChildren.length; i++)
            {
                if (parser.getElementName(setPrefChildren[i]) == "question_file")
                {
                    questionFile = parser.getNodeTextValue(setPrefChildren[i]);
                }
                if (parser.getElementName(setPrefChildren[i]) == "skills")
                {
                    parseSkills(setPrefChildren[i]);
                }
                
            }

            var xmlhttp;
            if (typeof (XMLHttpRequest) !== 'undefined')
            {
                xmlhttp = new XMLHttpRequest();
            }
            else if (window.XMLHttpRequest)
            {// code for IE7+, Firefox, Chrome, Opera, Safari
                xmlhttp = new XMLHttpRequest();
            }
            else if (ActiveXObject)
            {// code for IE6, IE5
                xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
            }

            xmlhttp.onreadystatechange = function()
            {
                if (xmlhttp.readyState != 4)
                    return;
                if (xmlhttp.status == 200)
                {
                    if (typeof (xmlhttp.responseXML) === 'undefined' || xmlhttp.responseXML == null)
                    {
                        console.log("parsing brd xml using node");
                        var xmlDoc = new CTATXML().parseXML(xmlhttp.responseText);
                    }
                    else
                    {
                        console.log("parsing brd xml using something else");
						var xmlDoc = xmlhttp.responseXML.documentElement;
                    }
					console.log("No of children" + parser.getElementChildren(xmlDoc).length);
					var gp = new CTATGraphParser();
					var parserResponse = gp.parseGraph(xmlDoc, that);
                    graph = parserResponse[0];
                    exampleTracer = parserResponse[1];
					feedbackSuppressed = (gp.getSuppressStudentFeedback() === CTATMsgType.HIDE_ALL_FEEDBACK);
                }
            }

            xmlhttp.onerror = function()
            {
                var errMsg = new CTATTutorMessageBuilder().createErrorMessage("Load Problem Error", xmlhttp.status + " " + xmlhttp.statusText);
                sendToInterface(errMsg, true);
            }
            xmlhttp.open("GET", questionFile, true);
            xmlhttp.send();
        }    
    

        if (message.getMessageType() == "InterfaceAction")
        {
            console.log("Interface action received.");
            var selection = [];
            var action = [];
            var input = [];
            var transactionID = message.getTransactionID();
            console.log("sendToTutor (" + (aMessage) + "), getTransactionID " + transactionID);
            var messageChildren = parser.getElementChildren(parser.getElementChildren(xml)[1]);
            for (var k = 0; k < messageChildren.length; k++)
            {
                if (parser.getElementName(messageChildren[k]) == "transaction_id")
                {
                    transaction_id = parser.getNodeTextValue(messageChildren[k]);
                }
                if (parser.getElementName(messageChildren[k]) == "Selection")
                {
                    var selections = parser.getElementChildren(messageChildren[k]);
                    for (var j = 0; j < selections.length; j++)
                    {
                        selection.push(parser.getNodeTextValue(selections[j]));
                    }
                }

                if (parser.getElementName(messageChildren[k]) == "Action")
                {
                    var actions = parser.getElementChildren(messageChildren[k]);
                    for (var m = 0; m < actions.length; m++)
                    {
                        action.push(parser.getNodeTextValue(actions[m]));
                    }
                }
                if (parser.getElementName(messageChildren[k]) == "Input")
                {
                    var inputs = parser.getElementChildren(messageChildren[k]);
                    for (var n = 0; n < inputs.length; n++)
                    {
                        input.push(parser.getNodeTextValue(inputs[n]));
                    }
                }
            }

            if (selection[0] === "hint")//Process an interface action that is a hint/
            {
                var sai;
                if (hintPolicy === CTATHintPolicyEnum.HINTS_UNBIASED)
                {
                    if (selection.length == 2)
                        selection[1] = "";
                    if (action.length == 2)
                        action[1] = "";
                }
                else if (hintPolicy === CTATHintPolicyEnum.HINTS_BIASED_BY_CURRENT_SELECTION_ONLY)
                {
                }
                else if (hintPolicy === CTATHintPolicyEnum.HINTS_BIASED_BY_PRIOR_ERROR_ONLY)
                {
                    if (errorSAI !== null)
                    {
                        if (selection.length == 2)
                            selection[1] = "";
                        if (action.length == 2)
                            action[1] = "";
                        selection.push(errorSAI.getSelectionAsString());
                        action.push("PreviousFocus");
                    }
                    else
                    {
                        selection[1] = "";
                        action[1] = "";
                    }
                }
                else if (hintPolicy === CTATHintPolicyEnum.HINTS_BIASED_BY_ALL)
                {
                    if (errorSAI !== null)
                    {
                        if (selection.length == 2)
                            selection.pop();
                        if (action.length == 2)
                            action.pop();
                        selection.push(errorSAI.getSelectionAsString());
                        action.push("PreviousFocus");
                    }
                }
                var eventArray = []; //Create an eventArray that would be populated by doHint
                var edge = exampleTracer.doHint(selection, action, input, "student", eventArray, true);
                var result = eventArray[0];
                var hints = edge.getHints();
                console.log("Before building hint message: hints = " + hints + "edge # " + edge.getUniqueID());
                var hintMessage = new CTATTutorMessageBuilder().createHintMessage(hints, result.getTutorSAI(), edge.getUniqueID(), transactionID);
                sendToInterface(hintMessage, false);
                var assocMsg = new CTATTutorMessageBuilder().createAssociatedRulesMessageForHint(new CTATExampleTracerSAI(result.getTutorSelection(), result.getTutorAction(), result.getTutorInput(), "student"), hints, edge.getUniqueID(), transactionID);
                sendToInterface(assocMsg, true);
            }
            else    //Process a interface action that is not a hint
            {
                var sai = new CTATExampleTracerSAI(selection, action, input, "student");
                var isCorrect = exampleTracer.evaluate(-1, sai);
                that.ctatdebug("Evaluate returned : " + isCorrect);
                finishExampleTrace(isCorrect, sai, "student", transactionID);
            }
        }

		if (message.getMessageType() == "ProblemSummaryRequest")
        {
			setTimeout(function(problemName)  // delay response 5 sec to allow done step's logging to complete
			{
				var psResp = '<message><verb>NotePropertySet</verb><properties><MessageType>ProblemSummaryResponse</MessageType>';
				psResp += '<cmi.core.lesson_status>completed</cmi.core.lesson_status>';
				psResp += '<cmi.core.score.raw>50</cmi.core.score.raw>';
				psResp += '<cmi.core.exit>suspend</cmi.core.exit>';
				psResp += '<cmi.core.session_time>0000:00:30.00</cmi.core.session_time>';
				psResp += '<ProblemSummary>&lt;ProblemSummary ProblemName="' + problemName + '" CompletionStatus="complete" Correct="2" UniqueCorrect="2" UniqueCorrectUnassisted="1" Hints="0" UniqueHints="0" HintsOnly="0" Errors="1" UniqueErrors="1" ErrorsOnly="1" UniqueSteps="2" TimeElapsed="30000" /&gt;</ProblemSummary>';
				psResp += '<end_of_transaction>true</end_of_transaction>';
				psResp += '</properties></message>';

				var psResps = [];
				psResps.push(psResp);
				that.sendBundle(psResps);
				that.ctatdebug("CTATExampleTracer.receiveFromInterface() sent result '" + psResp + "';");
			}, 5000);
		}
    };

    
    
    function parseSkills(element)
    {
        var skillList = [];
        var skills = parser.getElementChildren(element);
        for (var index = 0; index < skills.length; index++)
        {
            var label,pSlip,description,pKnown,category,pLearn,name,pGuess;
            if (parser.getElementName(skills[index]) === "skill")
            {
                label = parser.getElementAttr(skills[index],"label");
                pSlip = parser.getElementAttr(skills[index],"pSlip");
                description = parser.getElementAttr(skills[index],"description");
                pKnown = parser.getElementAttr(skills[index],"pKnown");
                category = parser.getElementAttr(skills[index],"category");
                pLearn = parser.getElementAttr(skills[index],"pLearn");
                name = parser.getElementAttr(skills[index],"name");
                pGuess = parser.getElementAttr(skills[index],"pGuess");
            }
            var skill = new CTATExampleTracerSkill(name,pGuess,pKnown,pSlip,pLearn);
            skill.setLabel(label);
            skill.setDescription(description);
            skillList.push(skill);
        }        
        that.getProblemSummary().setSkills(new CTATSkills(skillList));
    };

    /**
     * Send an array of messages via sendToInterface().
     * @param msgs the array to send; no-op if empty
     */
    this.sendBundle = function(msgs)
    {
        console.log("sending bundle #msgs = " + msgs.length);
        for (var index = 0; index < msgs.length; index++)
        {
            that.ctatdebug("Sending message #" + index);
            var msg = msgs[index];
            if (index <= 0)
                msg = xmlHeader + msg;
            sendToInterface(msg, (index >= msgs.length - 1)); 
        }
    };

    this.setHintPolicy  = function(thePolicy)
    {
        hintPolicy = thePolicy;
    };

	/**
	 * @return highlightRightSelection
	 */
	this.isHighlightRightSelection = function()
	{
		return highlightRightSelection;
	}

	/**
	 * @param {boolean} b new value for highlightRightSelection
	 */
	this.setHighlightRightSelection = function(b)
	{
		highlightRightSelection = b;
	}

    /**
     * Update a list of skills according to the given transaction result.
     * @param transactionResult (of type String): one of Skill.CORRECT(), Skill.INCORRECT, Skill.HINT
     * @param skillNames (of type set of strings): skills to update
     * @param stepID (of type string): identifier for this step, to ensure no step is updated more than once
     * @return array of CTATExampleTracerSkill: list of skills modified
     */
    this.updateSkills = function(transactionResult, skillNames, stepID)
    {
        var result = []; //array of CTATExampleTracerSkill

        //we will not have a controller
        //instead we have a boolean restoreProblemState that will be set always to false

        var skills = that.getProblemSummary().getSkills();

        if (skills === null || typeof (skills) === 'undefined')
        {
            return result;
        }

        skillNames.forEach(function(skillName)
        {
            var modifiedSkill = skills.updateSkill(transactionResult, skillName, stepID);

            if (modifiedSkill !== null && typeof (modifiedSkill) !== 'undefined')
            {
                result.push(modifiedSkill);
            }
        });

        return result;
    };

    /**
     * @return object of type CTATProblemSummary
     */
    this.getProblemSummary = function()
    {
        //rule production goes here
        //catalog commented out for now

        if (problemSummary === null || typeof (problemSummary) === 'undefined')
        {
            var problemName = that.getProblemName();

            if (problemName === null || typeof (problemName) === 'undefined' || problemName.length < 1)
            {
                problemName = "NoProblemDefined";
                problemSummary = new CTATProblemSummary(problemName, null, that.isFeedbackSuppressed());
            }
			else
			{
				problemSummary = new CTATProblemSummary(problemName, null, that.isFeedbackSuppressed());
			}
        }

        //skills are external and are determined at author time
        /*if(problemSummary.getSkills() === null || typeof(problemSummary.getSkills()) === 'undefined')
         {
         //rules go here
         
         var skills = new CTATSkills([]);
         
         //We do not have a BR_CONTROLLER, this goes away
         //skills.setVersion(); 
         
         //rules go here
         
         
         problemSummary.setSkills(skills);
         }*/

        return problemSummary;
    };

    /**
     * @return problemName
     */
    this.getProblemName = function()
    {
        return problemName;
    };

    /**
     * @return true if suppressStudentFeedback matched CTATMsgType.HIDE_ALL_FEEDBACK
     */
    this.isFeedbackSuppressed = function()
    {
        return feedbackSuppressed;
    };

    /**
     * @return array of strings: returns null if no skills
     */
    this.getSkillBarVector = function()
    {
        var skills = this.getProblemSummary().getSkills();

        if (skills === null || typeof (skills) === 'undefined')
        {
            return null; // not tracing any skills
        }
        else
        {
            return skills.getSkillBarVector(false, false);
        }
    };

    /**
     * Set a new serial number for skill updates. Call this at the start of each transaction.
     */
    this.startSkillTransaction = function()
    {
        var skills = that.getProblemSummary().getSkills();
        if(skills == null || skills == undefined)
            return;
        skills.startTransaction();
    };

    function finishExampleTrace(isCorrect, studentSAI, actor, transactionID)
    {
        that.ctatdebug("In CTATExampleTracer.js: finishExampleTrace() exampleTracer " + exampleTracer);
        that.ctatdebug("In CTATExampleTracer.js: finishExampleTrace() isCorrect " + isCorrect);
        that.ctatdebug("In CTATExampleTracer.js: finishExampleTrace() studentSAI " + studentSAI);
        that.ctatdebug("In CTATExampleTracer.js: finishExampleTrace() actor " + actor);
        that.ctatdebug("In CTATExampleTracer.js: finishExampleTrace() transactionID " + transactionID);
        
        var resultEvent = exampleTracer.getResult();
		resultEvent.setTransactionID(transactionID);
        var link = resultEvent.getReportableLink();
        var stepID = "";
        if (link !== null)
        {
            stepID = "" + link.getUniqueID();  // integer as string
            //Update skills on that Link
            //updateSkills(CTATExampleTracerSkill.CORRECT,skillnames,link.getUniqueID());
        }
        var sai = new CTATExampleTracerSAI(resultEvent.getTutorSelection(), resultEvent.getTutorAction(), resultEvent.getTutorInput(), actor);
        var msgBuilder = new CTATTutorMessageBuilder();
        var respMsgs = [];

        if (resultEvent.getResult() === CTATExampleTracerLink.CORRECT_ACTION) { //correct action
            respMsgs.push(msgBuilder.createCorrectActionMessage(transactionID, sai));
			var successMsg = "";
            if (link !== null)
			{
				successMsg = link.getSuccessMsg();
			}
            respMsgs.push(msgBuilder.createAssociatedRulesMessageForAction(resultEvent.getResult(), sai, studentSAI, "" /*skill.getsbvector*/, stepID, transactionID, successMsg));
            if (successMsg != "")
            {
                respMsgs.push(msgBuilder.createSuccessMessage(transactionID, successMsg));
            }
            errorSAI = null;
        }
        else  //Incorrect action
        {
            respMsgs.push(msgBuilder.createInCorrectActionMessage(transactionID, studentSAI));
            
			var buggyMsg = "";
            if (link !== null)
			{
				buggyMsg = link.getBuggyMsg();
			}

            //Silent hint cycle
			var hintResult = new CTATExampleTracerEvent(this, studentSAI);
            var hintLink = exampleTracer.traceForHint(hintResult);
			sai = (hintResult ? hintResult.getTutorSAI() : null);
			that.ctatdebug("silent hint result: " + hintResult + "; sai " + sai);
            if(hintLink == null)  // found no correct step for student selection
			{
				hintResult = new CTATExampleTracerEvent(this, studentSAI);
                hintLink = exampleTracer.getBestNextLink(false, hintResult);
				sai = (hintResult ? hintResult.getTutorSAI() : null);
 				that.ctatdebug("getBestNextLink result: " + hintResult + "; sai " + sai)
			}
	        if (hintLink !== null)
    	    {
        	    stepID = "" + hintLink.getUniqueID();  // integer as string
			}

			var textResp = createBuggyMessage(resultEvent, hintResult, msgBuilder);

            respMsgs.push(msgBuilder.createAssociatedRulesMessageForAction(resultEvent.getResult(), (sai && sai.getSelection() ? sai : studentSAI), studentSAI, "" /*skill.getsbvector*/, stepID, transactionID, textResp.tutorAdvice));

            if (textResp.msg)
            {
                respMsgs.push(textResp.msg);
            }
            errorSAI = studentSAI;
        }
        that.sendBundle(respMsgs);
        that.ctatdebug("finishExampleTrace() sent result " + resultEvent.getResult() + ", #respMsgs " + respMsgs.length + ", studentSAI " + studentSAI);
    }

	/**
	 * Create a message for any available feedback text. Returns an object with properties<ul>
	 * <li>tutorAdvice: the feedback text for the AssociatedRules message; null if none;</li>
	 * <li>msg: an entire message with MessageType BuggyMessage or HighlightMsg; null if none to send.</li>
	 * </ul>
	 * @param {CTATTutorMessageBuilder} msgBuilder
	 * @param {CTATExampleTracerEvent} resultEvent result from the original trace
	 * @param {CTATExampleTracerLink} hintLink result from  the silent hint cycle
	 * @return {{tutorAdvice: string|null, msg: string|null}}
	 */
	function createBuggyMessage(resultEvent, hintResult, msgBuilder)
	{
		var rtn = {tutorAdvice: null, msg: null};
		if(resultEvent.getReportableLink())
		{
			rtn.tutorAdvice = resultEvent.getReportableLink().getBuggyMsg();
			if(rtn.tutorAdvice && rtn.tutorAdvice.trim() !== "")
			{
				rtn.msg = msgBuilder.createBuggyMessage(resultEvent.getTransactionID(), rtn.tutorAdvice);
			}
			else
			{
				rtn.tutorAdvice = null;  // buggy link has no buggy message
			}
			return rtn;
		}
		if(resultEvent.isDoneStepFailed())
		{
			rtn.tutorAdvice = "I'm sorry, but you are not done yet. Please continue working.";
			rtn.msg = msgBuilder.createBuggyMessage(resultEvent.getTransactionID(), rtn.tutorAdvice);
			return rtn;
		}

		// Out of order text: only if hint cycle recommends a step on a different selection.

		var hintSelection0 = (hintResult && hintResult.getTutorSAI() ? hintResult.getTutorSAI().getSelectionAsString() : null);
		hintSelection0 = (hintSelection0 && hintSelection0.trim() !== "" ? hintSelection0 : null);

		var studentSelection0 = (resultEvent.getStudentSAI() ? resultEvent.getStudentSAI().getSelectionAsString() : null);
		studentSelection0 = (studentSelection0 && studentSelection0.trim() !== "" ? studentSelection0 : null);

		if(resultEvent.getResult() === CTATExampleTracerLink.NO_MODEL && resultEvent.isOutOfOrder() && hintSelection0 && hintSelection0 != studentSelection0())
		{
			if(isHighlightRightSelection())
			{
				rtn.tutorAdvice = msgBuilder.getHighlightMsgText();
				rtn.msg = msgBuilder.createHighlightWidgetMessage(hintSelection0, hintResult.getTutorSAI().getActionAsString(), resultEvent.getTransactionID());
			}
			else
			{
				rtn.tutorAdvice = "You need to do other steps first, before doing the step you just worked on. You might request a hint for more help.";
				rtn.msg = CTATTutorMessageBuilder.createBuggyMessage(resultEvent.getTransactionID, rtn.tutorAdvice);
			}
		}
		return rtn;
	}

/******************************************************/
//Tutor performed actions

    /**
     * Check whether there's a tutor-performed step at the given node in the
     * graph and, if so, do it with traverseEdge()
     * @param {CTATExampleTracerNode} priorLinkDest the destination node of the last-matched link; can be null
     * @param {CTATExampleTracerNode} currNode the current node, as calculated by the example-tracer algo
     * @param {array} selection possible arg to doNewExampleTrace()
     * @param {array} action same use as selection
     * @param {array} input same use as selection
     * @return {CTATExampleTracerEvent} result from the last step traversed
     */
    function checkForTutorAction(priorLinkDest, currNode, selection, action, input)
    {
        var links = []; //contains CTATExampleTracerLinks
        var length = 2; // max 2 origin nodes
        links[0] = nodeTutorActionFires(priorLinkDest, true);
        links[1] = nodeTutorActionFires(currNode, false);

        var i = 0;

        if(links[0] === links[1])
        {
            links[1] = null; // fire the tutor action only once
        }
        else if(links[0] === null || typeof(links[0]) === 'undefined')
        {
            i = 1; // skip null entry
        }

        var result = null; //of type CTATExampleTracerEvent

        for( ; i < length && links[i] !== null && typeof(links[i]) !== 'undefined'; ++i)
        {
            if(selection === null || typeof(selection) === 'undefined')
            {
                result = doNewExampleTrace(links[i], links[i].getSelection(), links[i].getAction(), links[i].getInput(), links[i].getActor(), true);
            }
            else
            {
                result = doNewExampleTrace(links[i], selection, action, input, links[i].getActor(), true);
            }
        }

        return result;
    }

    /**
     * Tell whether the given state has an outgoing tutor-performed action to fire. 
     * @param {CTATExampleTracerNode} src source state whose outgoing link(s) would be candidates
     * @param {boolean} linkTriggered true if action must be link-triggered, false if state-triggered
     * @return {CTATExampleTracerLink} object if action should fire; else null if not
     */
    function nodeTutorActionFires (src, linkTriggered)
    {
        if(src === null || typeof(src) === 'undefined')
        {
            return null;
        }

        if(src.getOutDegree() !== 1) 
        {
            return null; // must be exactly one outgoing link
        }
        
        var nextLink = src.getOutLinks().values().next().value; //of type CTATEXampleTracerLink

        if(nextLink.isTutorPerformed(linkTriggered))
        {
            return nextLink;
        }
        else
        {
            return null;
        }
    }

    /**
     * Perform an example-trace on a preselected edge, as when doing a tutor-performed step.
     * @param {CTATExampleTracerLink} link edge already selected
     * @param {array} selection student selection from prior step
     * @param {array} action student action from prior step
     * @param {array} input student input from prior step
     * @param {String} actor
     * @param {boolean} doTutorPerformedSteps
     * @return {CTATExampleTracerEvent} result from the step traversed
     */
    function doNewExampleTrace (link, selection, action, input, actor, doTutorPerformedSteps)
    {
        startSkillTransaction(); //FRAN: ISSUE FOR CALLING IT

        exampleTracer.evaluate(link.getUniqueID(), new CTATExampleTracerSAI(link.getSelection(), link.getAction(), input, actor));

        var result = exampleTracer.getResult(); //of type CTATExampleTracerEvent

        var transaction_id = enqueueToolActionToStudent(result.getTutorSelection(), result.getTutorAction(), result.getTutorInput());

        setTransactionId(transaction_id);

        return finishNewExampleTrace(); //FRAN: ISSUE FOR CALLING IT

    }

    /**
     * Generate a ToolMessage log entry, to create
     * a DataShop transaction where we don't normally have one. E.g., for tutor-
     * performed steps.
     * @param {array} selection 
     * @param {array} action
     * @param {array} input
     * @return {String} new message's transaction_id
     */
    function enqueueToolActionToStudent(selection, action, input)
    {
       //MessageTank code goes here 
    }

    /**
     * @param {String} semanticEventId
     * @return undefined
     */
    function setTransactionId (semanticEventId)
    {
       /* if(semanticEventId !== null && typeof(semanticEventId) !== 'undefined')
        {
            //MessageTank code goes here
        }*/
    }

    /**
     * Factored out common conclusion steps to
     * {doNewExampleTrace(CTATExampleTracerLink, array, array, array, String)
     * @param {array} selection
     * @param {array} action
     * @param {array} input
     * @param {String} actor
     * @param {boolean} doTutorPerformedSteps whether to go on to traverse a tutor-performed step
     * @return {CtatExampleTracerEvent} result of the last step traversed, including tutor-performed steps
     */
    function finishNewExampleTrace(selection, action, input, actor, doTutorPerformedSteps)
    {

        var result = exampleTracer.getResult(); //of type CTATExampleTracerEvent
        var depthSoFar = -1; //of type integer

        //MORE CODE GOES HERE

        var links = [];
        var length = 1;

        if(result.getReportableLink() !== null && typeof(result.getReportableLink()) !== 'undefined')
        {
            links[0] = result.getReportableLink();
        }
        else 
        {
            links[0] = null;
        }

        if(result.isSolverResult())
        {
            var transactionResult = (CTATExampleTracerTracer.CORRECT_ACTION.toString() === result.getResult().toString ? CTATExampleTracerSkill.CORRECT : CTATExampleTracerSkill.INCORRECT); //of type String
        }
    };
	
	/**
     * Public access to the graph.
     */
    this.getGraph = function()
    {
		return graph;
	}
	
	/**
     * Public access to the exampleTracer.
     */
    this.getTracer = function()
    {
		return exampleTracer;
	}
}

CTATExampleTracer.prototype.constructor = CTATExampleTracer;

if (typeof module !== 'undefined')
{
    module.exports = CTATExampleTracer;
}

registerTutor(new CTATExampleTracer());  // sets global tutorObject
