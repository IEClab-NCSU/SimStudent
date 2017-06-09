/* This object represents an CTATMsgType */

goog.provide('CTATMsgType');
goog.require('CTATBase');
goog.require('CTATXML');
goog.require('CTATLanguageManager');

/* LastModify: FranceskaXhakaj 07/14*/


CTATMsgType = function()
{
	CTATBase.call(this, "CTATMsgType","");
};

/**
 * Static method to find a named property value in a message given as a string.
 * content including tags.
 * @param {string} aMessage
 * @param {string} propertyName
 * @return {start:number, end:number} object with indices of start and end of value; null if not found
 */
CTATMsgType.findProperty = function(aMessage, propertyName)
{
    var msgLC = aMessage.toLowerCase();
    var tag   = "<"+propertyName.toLowerCase()+">";
    var start = msgLC.indexOf(tag) + tag.length;
    var end   = msgLC.indexOf("</"+propertyName.toLowerCase()+">");
    if(start < tag.length || end < 0) return null;
    var result = {};
    result.start = start;
    result.end = end;
    return result;
};

/**
 * Static method to convert a value into a property string, with array handling. N.B.: does not recurse
 * for nested arrays.
 * @param v value to convert
 * @return empty string if v is null; v if v is a string; XML string if v is an element;
 *         string <i>&lt;value&gt;v[0]&lt;/value&gt;[&lt;value&gt;v[1]&lt;/value&gt;...} if v an array;
 *         else v.toString()
 */
CTATMsgType.makeValues = function(v)
{
	if(v == null)                  // == null because also want to compare to undefined
		return "";
	if(typeof(v) == "string")
		return v;
	if(v.constructor && v.constructor.name == "Array")
	{
		if(!v.length)
			return "";             // empty array
		var i = 0;
		var vi;
		var result = "<value>" + ((vi = v[i++]) == null ? "" : vi.toString());
		while(i < v.length)
		{
			result += "</value><value>" + ((vi = v[i++]) == null ? "" : vi.toString());
		}
		return result += "</value>";
	}
	if(v.outerHTML)
		return v.outerHTML;        // XML element
	return v.toString();
};

/**
 * Static method to set a property value in a message given as a string.
 * If the property value has child elements, as in &lt;value&gt; lists, returns the entire
 * content including tags.
 * @param {string} aMessage
 * @param {string} propertyName
 * @param {string} propertyValue
 * @return revised aMessage
 */
CTATMsgType.setProperty = function(aMessage, propertyName, propertyValue)
{
    var indices = CTATMsgType.findProperty(aMessage, propertyName);
	var result = "";
    if(indices)
    {
        result = aMessage.slice(0, indices.start);
        result += CTATMsgType.makeValues(propertyValue);
        result += aMessage.slice(indices.end, aMessage.length);
    }
    else
    {
        var endProps = aMessage.indexOf("</properties>");
        result = aMessage.slice(0, endProps);
        result += "<" + propertyName + ">" + CTATMsgType.makeValues(propertyValue) + "</" + propertyName + ">";
        result += aMessage.slice(endProps, aMessage.length);
    }
    return result;
};

/**
 * Static method to find a named property value in a message given as a string.
 * If the property value has child elements, as in &lt;value&gt; lists, returns the entire
 * content including tags.
 * @param {string} aMessage
 * @param {string} propertyName
 * @return text of named element; returns empty string if not found
 */
CTATMsgType.getProperty = function(aMessage, propertyName)
{
    var indices = CTATMsgType.findProperty(aMessage, propertyName);
    if(indices) return aMessage.slice(indices.start, indices.end);
    return "";
};

/**
 * Static method to create a string array from a &lt;value&gt; list.
 * @param {string} str string of the from &lt;value&gt;v0&lt;/value&gt;&lt;value&gt;v1&lt;/value&gt;...
 * @return {array<string>} where each element is the text of a &lt;value&gt element
 */
CTATMsgType.valueToArray = function(str)
{
    if(!str.startsWith("<value>")) return null;
    var s = str.substring("<value>".length);
    if(!str.endsWith("</value>")) return null;
    s = s.substring(0, s.length-"</value>".length);
    var sA = s.split("</value><value>");
    ctatdebug("valueToArray("+str+") returns "+sA);
    return sA;
};

/**
 * Static method to extract an indexed value from a &lt;value&gt; list.
 * @param {string} str string of the from &lt;value&gt;v0&lt;/value&gt;&lt;value&gt;v1&lt;/value&gt;...
 * @param {number} i zero-based index of wanted value
 * @return {string} text of the <i>i</i>th element; null if none or string of wrong format
 */
CTATMsgType.getValue = function(str, i)
{
    var sA = CTATMsgType.valueToArray(str);
    if(!sA) return null;
    if(sA.length <= i) return null;
    return sA[i];
};

/**
 * Static method to find the message type given a message as a string.
 * @param {string} aMessage
 * @return text of MessageType element; returns empty string if not found
 */
CTATMsgType.getMessageType = function(aMessage)
{
    return CTATMsgType.getProperty(aMessage, "MessageType");
};

/**
 * Convenience method for getting the transactionID.
 * @param {string} aMessage
 * @return text of CTATMessage.TRANSACTION_ID_TAG element; returns empty string if not found
 */
CTATMsgType.getTransactionID = function(aMessage)
{
	return CTATMsgType.getProperty(aMessage, CTATMessage.TRANSACTION_ID_TAG);
};

/**
 * Convenience method for setting the transactionID.
 * @param {string} aMessage
 * @param {string} transaction_id
 * @return revised aMessage
 */
CTATMsgType.setTransactionID = function(aMessage, transaction_id)
{
	return CTATMsgType.setProperty(aMessage, CTATMessage.TRANSACTION_ID_TAG, transaction_id);
};

/**
 * @param {string} mt message type
 * @return true if messages of this type have "correct" or "incorrect" feedback
 */
CTATMsgType.isCorrectOrIncorrect = function(mt)
{
    return typeof(mt) == "string" ? (CTATMsgType.CorrectTypes[mt.toLowerCase()] ? true : false) : false;
};

/**
 * @param {string} mt message type
 * @return true if messages of this type have a success message, buggy message or other text feedback
 */
CTATMsgType.hasTextFeedback = function(mt)
{
    return typeof(mt) == "string" ? (CTATMsgType.TextFeedbackTypes[mt.toLowerCase()] ? true : false) : false;
};

/**
 * Tell whether this object's message type is related to text feedback.
 * Tests as {@link #isMessageType(MessageObject, String[])} against {@link #textFeedbackTypes}:
 * {@value #textFeedbackTypes}.
 * @param mo message to test
 * @return true if hint related
 */
CTATMsgType.isHintResponse = function(mt)
{
    return typeof(mt) == "string" ? (CTATMsgType.HintResponseTypes[mt.toLowerCase()] ? true : false) : false;
};

/**
 * Tell whether this message records the Done step. Tests selection against DONE
 * @param {string} msg message to test, as string
 * @return true if first selection element matches {@value #DONE}, case-insensitive
 */
CTATMsgType.isDoneMessage = function(msg) {
    var s = CTATMsgType.getProperty(msg, "Selection");
    var a = CTATMsgType.getProperty(msg, "Action");
    if (!s || !a) return false;
    s = s.toString().toLowerCase();
    a = a.toString().toLowerCase();
    var doneLC = CTATMsgType.DONE.toLowerCase();
    if(doneLC != s && doneLC != CTATMsgType.getValue(s, 0)) return false;
    var bpLC = CTATMsgType.BUTTON_PRESSED.toLowerCase();
    if(bpLC != a && bpLC != CTATMsgType.getValue(a, 0)) return false;
    return true;
};

/**
 * Extract the entire Selection, Action and Input lists from a  CTAT message, in XML.
 * @param {Element} message or propertiesElement properties element from a CTAT message, in XML
 * @param {CTATXML} parser
 * @param {Element} propertiesElement properties element from a CTAT message, in XML
 * @return { {selection: [], action: [], input: [] } }
 */
CTATMsgType.getSAIArraysFromElement = function(propertiesElement, parser)
{
	var sai = { selection: [], action: [], input: [] };
	var messageChildren = parser.getElementChildren(propertiesElement);
	for (var k = 0; k < messageChildren.length; k++)
	{
		var childElt = messageChildren[k];
		var childEltName = parser.getElementName(childElt);
		switch(childEltName)
		{
		case "Selection":
			var selections = parser.getElementChildren(childElt);
			for (var j = 0; j < selections.length; j++)
			{
				(sai.selection).push(parser.getNodeTextValue(selections[j]));
			}
			break;
		case "Action":
			var actions = parser.getElementChildren(childElt);
			for (var m = 0; m < actions.length; m++)
			{
				(sai.action).push(parser.getNodeTextValue(actions[m]));
			}
			break;
		case "Input":
			var inputs = parser.getElementChildren(childElt);
			for (var n = 0; n < inputs.length; n++)
			{
				(sai.input).push(parser.getNodeTextValue(inputs[n]));
			}
			break;
		case "properties":     // was called on parent, not properties element
			return CTATMsgType.getSAIArraysFromElement(childElt, parser);
		default:
			break;
		}
	}
	return sai;
};

/**************************** CONSTANTS ******************************************************/

/** Message types that provide correct or incorrect feedback -- that is, that grade a single step. */
Object.defineProperty(CTATMsgType, "CorrectTypes", {enumerable: false, configurable: false, writable: false,
        value: { correctaction:1, incorrectaction:1, lispcheckaction:1 } });

/** Message types that provide text feedback, including hints. */
Object.defineProperty(CTATMsgType, "TextFeedbackTypes", {enumerable: false, configurable: false, writable: false,
        value: { showhintsmessage:1, successmessage:1, buggymessage:1, wrongusermessage:1, nohintmessage:1, highlightmsg:1, showhintsmessagefromlisp:1 } });

/** Message types that provide hints. */
Object.defineProperty(CTATMsgType, "HintResponseTypes", {enumerable: false, configurable: false, writable: false,
        value: { showhintsmessage:1, nohintmessage:1, showhintsmessagefromlisp:1 } });

/** Property name for the text feedback in BuggyMessages. */
Object.defineProperty(CTATMsgType, "BUGGY_MSG", {enumerable: false, configurable: false, writable: false, value: "BuggyMsg"});

/** Fixed selection name for the Done button. */
Object.defineProperty(CTATMsgType, "DONE", {enumerable: false, configurable: false, writable: false, value: "Done"});

/** Fixed action name for button presses. */
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

/** Show all feedback as it's sent by the tutor. */
Object.defineProperty(CTATMsgType, "SHOW_ALL_FEEDBACK", {enumerable: false, configurable: false, writable: false, value: "Show All Feedback"});

/** Delay feedback until the Done button is pressed. */
Object.defineProperty(CTATMsgType, "DELAY_FEEDBACK", {enumerable: false, configurable: false, writable: false, value: "Delay Feedback"});

/** Never show feedback. */
Object.defineProperty(CTATMsgType, "HIDE_ALL_FEEDBACK", {enumerable: false, configurable: false, writable: false, value: "Hide All Feedback"});

/** Hide, block Done before completion. */
Object.defineProperty(CTATMsgType, "HIDE_BUT_COMPLETE", {enumerable: false, configurable: false, writable: false, value: "Hide feedback but require all steps"});

/** Hide but enforce min-max, ordering. */
Object.defineProperty(CTATMsgType, "HIDE_BUT_ENFORCE", {enumerable: false, configurable: false, writable: false, value: "Hide feedback but enforce constraints"});

//Object.defineProperty(CTATMsgType, "DEFAULT_OUT_OF_ORDER_MESSAGE", {enumerable: false, configurable: false, writable: false, value: "Instead of the step you are working on, please work on the highlighted step."});
Object.defineProperty(CTATMsgType, "DEFAULT_OUT_OF_ORDER_MESSAGE", {enumerable: false, configurable: false, writable: false, value: CTATLanguageManager.theSingleton.filterString("HIGHLIGHTEDSTEP")});

/** Text of buggy message when Done button clicked prematurely. */
//Object.defineProperty(CTATMsgType, "NOT_DONE_MSG", {enumerable: false, configurable: false, writable: false, value: "I'm sorry, but you are not done yet. Please continue working."});
Object.defineProperty(CTATMsgType, "NOT_DONE_MSG", {enumerable: false, configurable: false, writable: false, value: CTATLanguageManager.theSingleton.filterString("NOTDONE")});

/*** Belonged to CTATHintMessagesManager originally ***/

Object.defineProperty(CTATMsgType, "PREVIOUS_FOCUS", {enumerable: false, configurable: false, writable: false, value: "PreviousFocus"});

/** Tutor result, for logging and skills update. */
Object.defineProperty(CTATMsgType, "ASSOCIATED_RULES", {enumerable: false, configurable: false, writable: false, value: "AssociatedRules"});

/** Start restoring messages from a previous session. */
Object.defineProperty(CTATMsgType, "BEGIN_RESTORE", {enumerable: false, configurable: false, writable: false, value: "BeginRestore"});

/** Initiate a transtion to a new state in the behavior graph. */
Object.defineProperty(CTATMsgType, "BEGIN_GO_TO_STATE", {enumerable: false, configurable: false, writable: false, value: "BeginGoToState"});

/** Completed a transtion to a new state in the behavior graph. */
Object.defineProperty(CTATMsgType, "END_GO_TO_STATE", {enumerable: false, configurable: false, writable: false, value: "EndGoToState"});

/** Error message after incorrect step. */
Object.defineProperty(CTATMsgType, "BUGGY_MESSAGE", {enumerable: false, configurable: false, writable: false, value: "BuggyMessage"});

/** Provide feedback that a student (or tutor) action was right. */
Object.defineProperty(CTATMsgType, "CORRECT_ACTION", {enumerable: false, configurable: false, writable: false, value: "CorrectAction"});

/** Provide a suggestion for the next student step. */
Object.defineProperty(CTATMsgType, "HINT_REQUEST", {enumerable: false, configurable: false, writable: false, value: "HintRequest"});

/** Provide feedback that the last student action was wrong. */
Object.defineProperty(CTATMsgType, "INCORRECT_ACTION", {enumerable: false, configurable: false, writable: false, value: "InCorrectAction"});

/** Transmit a student (or tutor) action on the student interface. */
Object.defineProperty(CTATMsgType, "INTERFACE_ACTION", {enumerable: false, configurable: false, writable: false, value: "InterfaceAction"});

/** Transmit a student (or tutor) action on the student interface. */
Object.defineProperty(CTATMsgType, "INTERFACE_IDENTIFICATION", {enumerable: false, configurable: false, writable: false, value: "InterfaceIdentification"});

/** Mark the end of a problem-restore message sequence. */
Object.defineProperty(CTATMsgType, "PROBLEM_RESTORE_END", {enumerable: false, configurable: false, writable: false, value: "ProblemRestoreEnd"});

/** The student user interface asks for a problem summary. */
Object.defineProperty(CTATMsgType, "PROBLEM_SUMMARY_REQUEST", {enumerable: false, configurable: false, writable: false, value: "ProblemSummaryRequest"});

/** Send a problem summary to the student user interface. */
Object.defineProperty(CTATMsgType, "PROBLEM_SUMMARY_RESPONSE", {enumerable: false, configurable: false, writable: false, value: "ProblemSummaryResponse"});

/** Provide initialization settings. */
Object.defineProperty(CTATMsgType, "SET_PREFERENCES", {enumerable: false, configurable: false, writable: false, value: "SetPreferences"});

/** Hint response. */
Object.defineProperty(CTATMsgType, "SHOW_HINTS_MESSAGE", {enumerable: false, configurable: false, writable: false, value: "ShowHintsMessage"});

/** Success message after correct step. */
Object.defineProperty(CTATMsgType, "SUCCESS_MESSAGE", {enumerable: false, configurable: false, writable: false, value: "SuccessMessage"});

/** Signal the end of the start state messages. */
Object.defineProperty(CTATMsgType, "START_STATE_END", {enumerable: false, configurable: false, writable: false, value: "StartStateEnd"});

/** The first of the start state messages. */
Object.defineProperty(CTATMsgType, "STATE_GRAPH", {enumerable: false, configurable: false, writable: false, value: "StateGraph"});

/** Transmit a student action on the student interface. */
Object.defineProperty(CTATMsgType, "UNTUTORED_ACTION", {enumerable: false, configurable: false, writable: false, value: "UntutoredAction"});

/** Go to a named state in the behavior graph. */
Object.defineProperty(CTATMsgType, "GO_TO_STATE", {enumerable: false, configurable: false, writable: false, value: "GoToState"});

/** Reload the page. */
Object.defineProperty(CTATMsgType, "INTERFACE_REBOOT", {enumerable: false, configurable: false, writable: false, value: "InterfaceReboot"});


/**
 * Values for completionStatus.
 * 0 : incomplete
 * 1 : complete
 * @var {array of strings}
 */
Object.defineProperty(CTATMsgType, "CompletionValue", {enumerable: false, configurable: false, writable: false, value: ["incomplete", "complete"]});

CTATMsgType.prototype = Object.create(CTATBase.prototype);
CTATMsgType.prototype.constructor = CTATMsgType;

if(typeof module !== 'undefined')
{
    module.exports = CTATMsgType;
}
