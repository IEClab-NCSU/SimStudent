/* This object represents an CTATMsgType */

goog.provide('CTATMsgType');
goog.require('CTATBase');

/* LastModify: FranceskaXhakaj 07/14*/


CTATMsgType = function() 
{
	CTATBase.call(this, "CTATMsgType","");
	/**
	 * Static method to find the message type given a message as a string.
	 * @param  aMessage
	 * @return text of MessageType element; returns empty string if not found
	 */
	CTATMsgType.getMessageType = function(aMessage)
    {
        var start = aMessage.indexOf("<MessageType>") + "<MessageType>".length;
        var end   = aMessage.indexOf("</MessageType>");

		debug("getMessageType("+aMessage+") start "+start+", end "+end);

        if(start < "<MessageType>".length || end < 0)
        {
        	return "";
        }
        
        return aMessage.slice(start, end);
	};

};

/**************************** CONSTANTS ******************************************************/

Object.defineProperty(CTATMsgType, "DONE", {enumerable: false, configurable: false, writable: false, value: "Done"});
Object.defineProperty(CTATMsgType, "BUTTON_PRESSED", {enumerable: false, configurable: false, writable: false, value: "ButtonPressed"});

/*** Belonged to CTATMatcher originally ***/

/** Default student value "Student" for actor in match tuple. */
Object.defineProperty(CTATMsgType, "DEFAULT_STUDENT_ACTOR", {enumerable: false, configurable: false, writable: false, value: "Student"});

/** Default tool-actor value "Tool" for actor in match tuple. */
Object.defineProperty(CTATMsgType, "DEFAULT_TOOL_ACTOR", {enumerable: false, configurable: false, writable: false, value: "Tutor"});

/** Tool-actor value for tutor-performed actions that shouldn't be graded Correct. */
Object.defineProperty(CTATMsgType, "UNGRADED_TOOL_ACTOR", {enumerable: false, configurable: false, writable: false, value: "Tutor (unevaluated)"});

/** Default value DEFAULT_STUDENT_ACTOR for actor in match tuple. */
Object.defineProperty(CTATMsgType, "DEFAULT_ACTOR", {enumerable: false, configurable: false, writable: false, value: CTATMsgType.DEFAULT_STUDENT_ACTOR});

/** Any-actor value "Any" for actor in match tuple. */
Object.defineProperty(CTATMsgType, "ANY_ACTOR", {enumerable: false, configurable: false, writable: false, value: "Any"});

/*** Belonged to CTATFeedbackEnum originally ***/

// show all feedback as it's sent by the tutor
Object.defineProperty(CTATMsgType, "SHOW_ALL_FEEDBACK", {enumerable: false, configurable: false, writable: false, value: "Show All Feedback"});

// delay feedback until the Done button is pressed
Object.defineProperty(CTATMsgType, "DELAY_FEEDBACK", {enumerable: false, configurable: false, writable: false, value: "Delay Feedback"});

// never show feedback
Object.defineProperty(CTATMsgType, "HIDE_ALL_FEEDBACK", {enumerable: false, configurable: false, writable: false, value: "Hide All Feedback"});

Object.defineProperty(CTATMsgType, "DEFAULT", {enumerable: false, configurable: false, writable: false, value: CTATMsgType.SHOW_ALL_FEEDBACK});

/*** Belonged to CTATHintMessagesManager originally ***/

Object.defineProperty(CTATMsgType, "PREVIOUS_FOCUS", {enumerable: false, configurable: false, writable: false, value: "PreviousFocus"});

/** Transmit a student (or tutor) action on the student interface. */
Object.defineProperty(CTATMsgType, "INTERFACE_ACTION", {enumerable: false, configurable: false, writable: false, value: "InterfaceAction"});


/** 
 * Values for completionStatus.
 * 0 : incomplete
 * 1 : complete 
 * @param {array of strings}
 */
Object.defineProperty(CTATMsgType, "CompletionValue", {enumerable: false, configurable: false, writable: false, value: ["incomplete", "complete"]});


CTATMsgType.prototype = Object.create(CTATBase.prototype);
CTATMsgType.prototype.constructor = CTATMsgType;

if(typeof module !== 'undefined')
{
	module.exports = CTATMsgType;
} 