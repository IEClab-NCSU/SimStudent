/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATCommunication/CTATTutorMessageBuilder.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATTutorMessageBuilder');

goog.require('CTATBase');
/**
 *
 */
CTATTutorMessageBuilder = function()
{
    CTATBase.call(this, "CTATTutorMessageBuilder", "__undefined__");

    /****************************** STATIC METHODS ****************************************************/

    /**
     * Tell whether an indicator value is a hint result.
     * @param {object} indicatorObj
     * @return {boolean} - true if indicatorObj.startsWith HINT ignoring case
     */
    CTATTutorMessageBuilder.isHint = function(indicatorObj)
    {
        if(indicatorObj === null || typeof(indicatorObj) === 'undefined')
        {
            return false;
        }

        var indicator = indicatorObj.toString().toLowerCase();

        return (indicator.indexOf(CTATTutorMessageBuilder.HINT.toLowerCase()) === 0);
    };

    /**
     * Tell whether an indicator value is a correct result.
     * @param {Object} indicatorObj
     * @return {boolean} - true if indicatorObj.startsWith({@value #CORRECT}) ignoring case
     */
    CTATTutorMessageBuilder.isCorrect = function(indicatorObj)
    {
        if(indicatorObj === null || typeof(indicatorObj) === 'undefined')
        {
            return false;
        }

        var indicator = indicatorObj.toString().toLowerCase();

        if(indicator.indexOf(CTATTutorMessageBuilder.CORRECT.toLowerCase()) === 0)
        {
            return true;
        }

        return (indicator.indexOf(CTATExampleTracerLink.SUCCESS.toLowerCase()) === 0);
    };
}

// this assignment must precede any addition of functions to the .prototype object
CTATTutorMessageBuilder.prototype = Object.create(CTATBase.prototype);

    CTATTutorMessageBuilder.prototype.createInCorrectActionMessage = function(transactionID, sai)
    {
        this.ctatdebug("createInCorrectActionMessage ()");

        var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>InCorrectAction</MessageType>";

        message += "<transaction_id>" + transactionID + "</transaction_id>";
        this.ctatdebug ("In CTATTutorMessageBuilder " + sai);
        message += sai.toXMLString(false);
        message += "</properties></message>";

        return (message);
    };

    /**
     *
     */
    CTATTutorMessageBuilder.prototype.createCorrectActionMessage = function(transactionID, sai)
    {
        this.ctatdebug("createCorrectActionMessage ()");

        var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>CorrectAction</MessageType>";
        message += "<transaction_id>" + transactionID + "</transaction_id>";
        message += sai.toXMLString(false);
        message += "</properties></message>";

        return (message);
    };

    /**
     *
     */
    CTATTutorMessageBuilder.prototype.createAssociatedRulesMessageForHint = function(link, sai, skillbarVector, transactionID)
    {
        var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>AssociatedRules</MessageType>";
        message += "<Indicator>Hint</Indicator>";
        message += sai.toXMLString();
        message += "<TutorAdvice>";
        var hints = link.getHints();
        var stepID = link.getUniqueID();
        for (var index = 0; index < hints.length; index++)
        {
            message += "<value>" + hints[index] + "</value>";
        }
        message += "</TutorAdvice><TotalHintsAvailable>" + hints.length + "</TotalHintsAvailable>" + "<CurrentHintNumber>1</CurrentHintNumber>";
        message += "<Actor>" + "Student" + "</Actor>";
        message += "<Skills>" + skillbarVector + "</Skills>";
        message += "<skillBarDelimiter>`</skillBarDelimiter>";
        message += "<StepID>" + stepID + "</StepID>";
        message += "<transaction_id>" + transactionID + "</transaction_id>";
        message += "<LogAsResult>true</LogAsResult>";
        message += "</properties></message>";
        return (message);
    };

    /**
     *
     */
    CTATTutorMessageBuilder.prototype.createAssociatedRulesMessageForAction = function(indicator, sai, studentSAI, skillbarVector, stepID, transactionID, tutorAdvice)
    {
        var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>AssociatedRules</MessageType>";
        message += "<Indicator>" + (CTATTutorMessageBuilder.isCorrect(indicator) ? "Correct" : "InCorrect")  + "</Indicator>";
        message += sai.toXMLString();
        var temp = studentSAI.toXMLString().replace("<Selection>", "<StudentSelection>").replace("<Action>", "<StudentAction>").replace("<Input>", "<StudentInput>");
        temp = temp.replace("</Selection>", "</StudentSelection>").replace("</Action>", "</StudentAction>").replace("</Input>", "</StudentInput>");
        message += temp;
        message += "<Actor>" + "Student" + "</Actor>";
        message += "<Skills>" + skillbarVector + "</Skills>";
        message += "<skillBarDelimiter>`</skillBarDelimiter>";
        message += "<StepID>" + stepID + "</StepID>";
        message += "<transaction_id>" + transactionID + "</transaction_id>";
		if (tutorAdvice)
		{
			message += "<TutorAdvice>" + tutorAdvice + "</TutorAdvice>";
		}
        message += "<LogAsResult>true</LogAsResult>";
        message += "<end_of_transaction>" + (tutorAdvice ? false : true) + "</end_of_transaction>";
        message += "</properties></message>";
        return (message);
    };

    /**
     *
     */
    CTATTutorMessageBuilder.prototype.createHintMessage = function(hints, sai, stepID, transactionID)
    {
        var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>ShowHintsMessage</MessageType>";
        message += "<HintsMessage>";
        for (var index = 0; index < hints.length; index++)
        {
            message += "<value>" + hints[index] + "</value>";
        }
        message += "</HintsMessage>";
        message += sai.toXMLString();
        message += "<transaction_id>" + transactionID + "</transaction_id>";
        message += "</properties></message>";
        return (message);
    };

    /**
     * Generate a TutoringServiceError message
     * @param errorType value for <ErrorType> element
     * @param details value for <Details> element
     * @return XML message as string
     */
    CTATTutorMessageBuilder.prototype.createErrorMessage = function(errorType, details)
    {
        var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>TutoringServiceError</MessageType><ErrorType>";
        message += (errorType ? errorType : "Unknown Error");
        message += "</ErrorType><Details>";
        message += (details ? details : "");
        message += "</Details></properties></message>";
        return (message);
    };

    /**
     * Generate a Tutoring Service Success message
     * @param txnID value for <transaction_id> element
     * @param successMsg value for <SuccessMsg> element
     * @return XML message as string
     */
    CTATTutorMessageBuilder.prototype.createSuccessMessage = function(txnID, successMsg)
    {
        var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>SuccessMessage</MessageType>";
        message += "<SuccessMsg>" + successMsg +"</SuccessMsg>";
        message += "<transaction_id>" + txnID + "</transaction_id>";
        message += "<end_of_transaction>true</end_of_transaction>";
        message += "</properties></message>";
        return (message);
    };

    /**
     * Generate a Tutoring Service Buggy message
     * @param txnID value for <transaction_id> element
     * @param buggyMsg value for <BuggyMsg> element
     * @return XML message as string
     */
    CTATTutorMessageBuilder.prototype.createBuggyMessage = function(txnID, buggyMsg)
    {
        var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>BuggyMessage</MessageType>";
        message += "<BuggyMsg>" + buggyMsg +"</BuggyMsg>";
        message += "<transaction_id>" + txnID + "</transaction_id>";
        message += "<end_of_transaction>true</end_of_transaction>";
        message += "</properties></message>";
        return (message);
    };

    /**
     * Create a SendWidgetLock message.
     * @param lockWidget boolean value for WidgetLockFlag property
     * @return message as XML string
     */
    CTATTutorMessageBuilder.prototype.createLockWidgetMsg = function(lockWidget)
    {
        var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>SendWidgetLock</MessageType>";
        message += "<WidgetLockFlag>" + lockWidget + "</WidgetLockFlag>";
        message += "</properties></message>";
        return (message);
    };

    /**
     *
     */
    CTATTutorMessageBuilder.prototype.createStateGraphMessage = function(caseInsensitive,isUnordered,lockWidget,suppressStudentFeedback,highlightRightSelection,confirmDone,skillBars)
    {
        var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>StateGraph</MessageType>";
        message += "<caseInsensitive>" + caseInsensitive +"</caseInsensitive>";
        message += "<unordered>" + isUnordered + "</unordered>";
        message += "<lockWidget>" + lockWidget + "</lockWidget>";
        message += "<suppressStudentFeedback>" + suppressStudentFeedback + "</suppressStudentFeedback>";
        message += "<highlightRightSelection>" + highlightRightSelection + "</highlightRightSelection>";
        message += "<confirmDone>" + confirmDone + "</confirmDone>";
        message += "<confirmDone>" + "`" + "</confirmDone>";
        if (skillBars !== undefined && skillBars !== null && skillBars.length > 0)
        {
            message += "<Skills>";
            for(var i = 0 ;i<skillBars.length;i++)
            {
                message +="<value>" + skillBars[i] + "</value>";
            }
            message += "</Skills>";
        }
        message += "</properties></message>";
        return (message);
    };


     /**
     *
     */
    CTATTutorMessageBuilder.prototype.createHighlightWidgetMessage = function(selection,action, txnID)
    {
        var message = "<message><verb>SendNoteProperty</verb><properties><MessageType>HighlightMsg</MessageType>";
        message += "<HighlightMsgText>" + this.getHighlightMsgText() +"</HighlightMsgText>";
        message += "<Selection><value>" + selection + "</value></Selection>";
        message += "<Action><value>" + action + "</value></Action>";
        message += "<transaction_id>" + txnID + "</transaction_id>";
        message += "<end_of_transaction>true</end_of_transaction>";
        message += "</properties></message>";
        return (message);
    };

	/**
	 * @return string "Instead of the step you are working on, please work on the highlighted step."
	 */
	CTATTutorMessageBuilder.prototype.getHighlightMsgText = function()
	{
		return "Instead of the step you are working on, please work on the highlighted step.";
	}

/****************************** CONSTANTS ****************************************************/


/** Property name for trigger parameter of SemanticEventElement
 * type String
 */
Object.defineProperty(CTATTutorMessageBuilder, "TRIGGER", {enumerable: false, configurable: false, writable: false, value: "trigger"});

/** Property name for subtype parameter of SemanticEventElement
 * type String
 */
Object.defineProperty(CTATTutorMessageBuilder, "SUBTYPE", {enumerable: false, configurable: false, writable: false, value: "subtype"});

/** Value for SemanticEventElement subtype indicating tutor performed step for student.
 * type String
 */
Object.defineProperty(CTATTutorMessageBuilder, "TUTOR_PERFORMED", {enumerable: false, configurable: false, writable: false, value: "tutor-performed"});

/**
 * Indicator value in buildAssociatedRules(..)
 * for hint responses.
 * type String
 */
Object.defineProperty(CTATTutorMessageBuilder, "HINT", {enumerable: false, configurable: false, writable: false, value: "Hint"});

/**
 * Indicator value in buildAssociatedRules(..)
 * type String
 * for correct evaluations.
 */
Object.defineProperty(CTATTutorMessageBuilder, "CORRECT", {enumerable: false, configurable: false, writable: false, value: "Correct"});


CTATTutorMessageBuilder.prototype.constructor = CTATTutorMessageBuilder;


if(typeof module !== 'undefined')
{
    module.exports = CTATTutorMessageBuilder;
}


