/**-----------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2014-11-11 08:42:12 -0500 (Tue, 11 Nov 2014) $ 
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATExampleTracer_Skills.js $ 
 $Revision: 21568 $ 

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
goog.provide('CTATExampleTracer_Skills');

goog.require('CTATHintPolicyEnum');
goog.require('CTATExampleTracerEvent');
goog.require('CTATExampleTracerSAI');
goog.require('CTATGraphParser');
goog.require('CTATMessage');
goog.require('CTATProblemSummary');
goog.require('CTATExampleTracerSkill');
goog.require('CTATSkills');
goog.require('CTATTutorMessageBuilder');
goog.require('CTATXML');

CTATExampleTracer = function()
{

    var that = this;
    var exampleTracer;
    var graph;
    var parser = new CTATXML();
    var serializer = new XMLSerializer();
    var errorSAI = null;
    var groupModel;
    var hintPolicy;
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
        //useDebugging = true;
        var xml = new CTATXML().parseXML(aMessage);
        var message = new CTATMessage();
        message.parseStringMessage(aMessage);

        //Switch, depending on type of message.
        if (message.getMessageType() == "SetPreferences")
        {
            console.log("SetPreferences received.");
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
                else
                {
                    var errMsg = new CTATTutorMessageBuilder().createErrorMessage("Load Problem Error", xmlhttp.responseText);
                    sendToInterface(errMsg, true);
                }
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
            var transactionID;
            console.log("sendToTutor (" + (aMessage) + ")");
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
                var assocMsg = new CTATTutorMessageBuilder().createAssociatedRulesMessageForHint(new CTATExampleTracerSAI(edge,result.getTutorSelection(), result.getTutorAction(), result.getTutorInput(), "student"), getSkillBarVector(), transactionID);
                sendToInterface(assocMsg, true);
            }
            else    //Process a interface action that is not a hint
            {
                var sai = new CTATExampleTracerSAI(selection, action, input, "student");
                var isCorrect = exampleTracer.evaluate(-1, sai);
                ctatdebug("Evaluate returned : " + isCorrect);
                finishExampleTrace(isCorrect, sai, "student", transactionID);
            }
        }
        //useDebugging = false;
    };

    
    
    function parseSkills(element)
    {
        var skillList = [];
        var skills = parser.getElementChildren(element);
        for (var k = 0; k < skills.length; k++)
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
    }

    this.sendStartStateBundle = function(startStateMsgs)
    {
        console.log("processStartState()");                
        for (var index = 0; index < startStateMsgs.length - 1; index++)
        {
                ctatdebug("Sent start state message, child #" + index);
                sendToInterface(startStateMsgs[index], false);
 
        }
        sendToInterface(startStateMsgs[startStateMsgs.length - 1], true);
    };

    this.setHintPolicy  = function(thePolicy)
    {
        hintPolicy = thePolicy;
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

    function finishExampleTrace(sai, actor, transactionID)
    {
        ctatdebug("In CTATExampleTracer_Skills.js: finishExampleTrace() exampleTracer " + exampleTracer);
        ctatdebug("In CTATExampleTracer_Skills.js: finishExampleTrace() exampleTracer " + sai);
        ctatdebug("In CTATExampleTracer_Skills.js: finishExampleTrace() exampleTracer " + actor);
        ctatdebug("In CTATExampleTracer_Skills.js: finishExampleTrace() exampleTracer " + transactionID);
        
        var result = exampleTracer.getResult();
        var link = result.getReportableLink();
        var stepID = "";
        
        var actionMessage;
        var assocMessage; 
        var studentSAI = new CTATExampleTracerSAI(result.getTutorSelection(), result.getTutorAction(), result.getTutorInput());   
        var indicator = evaluateResult === true ? "Correct" : "Incorrect";

        if (resultEvent.getResult() === CTATExampleTracerLink.CORRECT_ACTION) { //correct action
            //Update skills on Link
            if(link!== null)
            {
            link = "" + stepID;
            updateSkills(CTATExampleTracerSkill.CORRECT,link.getSkills(),link.getUniqueID());
            }
       
            //Create action message
            actionMessage = new CTATTutorMessageBuilder().createCorrectActionMessage(transactionID, sai);
            sendToInterface(actionMessage, false);
            
            if( link.getSuccessMsg() != "")
            {
                var successMessage = new CTATTutorMessageBuilder().createSuccessMessage(transactionID,link.getSuccessMsg());
                sendToInterface(successMessage, false);
            }
            
            assocMessage = new CTATTutorMessageBuilder().createAssociatedRulesMessageForAction(CTATExampleTracerSkill.CORRECT,sai,studentSAI,getSkillBarVector(),stepID,transactionID);
            sendToInterface(assocMessage, true);
            errorSAI = null;
        }
        else  //Incorrect action
        {
            var replacementSAI;
            //Suboptimal Action
            if(resultEvent.getResult() === CTATExampleTracerLink.FIREABLE_BUGGY_ACTION)
            {
                //replacementSAI = replaceInput(link,sai);
            }
            
            if(replacementSAI == undefined)
                replacementSAI = sai;
            actionMessage = new CTATTutorMessageBuilder().createInCorrectActionMessage(transactionID, replacementSAI);
            
            if(link !== null & typeof(link)!=='undefined') //matched an incorrect link
            {
                var buggyMessageText = link.getBuggyMsg();
                if(buggyMessageText !== null & typeof(buggyMessageText)!==undefined)
                {
                    var buggyMessage = CTATTutorMessageBuilder.createBuggyMessage(transactionID, buggyMessageText);
                    sendToInterface(buggyMessage, false);
                }
            }
            
            //Silent hint cycle
            var hintResult = new CTATExampleTracerEvent(this,sai);
            var hintLink = exampletracer.traceForHint(sai);
            
            var depthSoFar = (hintResult.getReportableLink() === null || typeof(hintResult.getReportableLink()) === 'undefined') ? -1 : hintResult.getReportableLink().getDepth();
            
            if(hintLink == null)
                hintLink = exampleTracer.getBestNextLink(false, new CTATExampleTracerEvent(this,sai));
                
            //Skill Update
            updateSkills(CTATExampleTracerSkill.INCORRECT,hintLink.getSkills(),hintLink.getUniqueID());
            
            
            if(resultEvent.getResult() === CTATExampleTracerLink.FIREABLE_BUGGY_ACTION)
            {
                var assocMsg = CTATTutorMessageBuilder.createAssociatedRulesMessageForAction(CTATExampleTracerSkill.INCORRECT,sai,studentSAI,getSkillBarVector(),stepID,transactionID);
                sendToInterface(assocMsg, true);
            }
            
            else
            { 
                var hintSelection = (hintLink == null? hintLink.getSelection():null);
                var hintSelection0 = (hintSelection!=null && hintSelection.size>0 ? hintSelection[0] : null);
                
                if(result.isDoneStepFailed())
                {
                    var notDoneMsg = CTATTutorMessageBuilder.createBuggyMessage(transactionID, "I'm sorry, but you are not done yet. Please continue working."); //replace with enum
                    sendToInterface(notDoneMsg, false);
                }
                else
                {
                    if(resultEvent.getResult() === CTATExampleTracerLink.NO_MODEL && result.isOutOfOrder() &&hintSelection0 != null && hintSelection0 != result.getSelectionAsString())
                    {
                        if(isHighlightRightSelection())
                        {
                           var highlightMsg = CTATTutorMessageBuilder.createHighlightWidgetMessage(hintSelection, hintAction, transactionID);
                           sendToInterface(highlightMsg, false);
                        }
                        else
                        {
                            var otherStepsMsg = CTATTutorMessageBuilder.createBuggyMessage(transactionID,"You need to do other steps first, before doing the step you just worked on. You might request a hint for more help.");
                            sendToInterface(otherStepsMsg, false);
                        }
                    }
                    
                }
                
                if(hintLink != null)
                {
                    var assocMsg =  CTATTutorMessageBuilder.createAssociatedRulesMessageForAction(CTATExampleTracerSkill.INCORRECT,sai,studentSAI,getSkillBarVector(),stepID,transactionID);
                    sendToInterface(assocMsg, true);
                }
                
            }
            errorSAI = sai;
        }
        ctatdebug("finishExampleTrace() to call sendToInterface(" + actionMessage + ", true)");
       
    }
}

if (typeof module !== 'undefined')
    module.exports = CTATExampleTracer;
