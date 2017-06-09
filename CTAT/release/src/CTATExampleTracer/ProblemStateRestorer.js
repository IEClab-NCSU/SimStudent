/**
 * Get restore bundle asyncly: queue all; in workTheQueue, if(pss==complete) just send.
 */

goog.provide('ProblemStateRestorer');

goog.require('CTATBase');
goog.require('CTATCommLibrary');
goog.require('CTATSAI');
goog.require('CTATMsgType');
goog.require('ProblemStateSaver');
goog.require('CTAT.ToolTutor');
goog.require('CTATLMS');

/**
 * Constructor.
 * @param {CTATExampleTracer} tracer
 */
ProblemStateRestorer = function(exTracer)
{
	CTATBase.call(this, "ProblemStateRestorer", exTracer);

	/**
	 * The tracer we serve.
	 * @type {CTATExampleTracer}
	 */
	var tracer = exTracer;

	/** Pointer to self. */
	var that = this;

	/**
	 * URL to get the saved messages.
     * @type {string}
     */
    var restoreProblemUrl = null;

	/**
	 * Number of messages retrieved.
	 * @type {integer}
	 */
	var nMsgs = 0;

	/**
	 * Decide whether to remove this message from the replay.
	 * @param {string} msgType MessageType property of msg
	 * @param {string} msg the message itself, as an XML string
	 * @param {number} psaIndex index of msg in msgArr
	 * @param {Array<string>} msgArr all the messages
	 */
	function omitThisMsg(msgType, msg, psaIndex, msgArr)
	{
		if(CTATMsgType.INTERFACE_ACTION == msgType && psaIndex == msgArr.length-1 && CTATMsgType.isDoneMessage(msg))
		{
			return tracer.getOutputStatus().isComplete();
		}
		return false;
	}

	/**
	 * Parse a saved problem state into an array of strings.
	 * @param {string} problemState
	 * @return {Array<string>} texts of messages; empty array if invalid input
	 */
	function parseRestoreString(problemState)
	{
		var restoreMsgs = [];
		if(typeof problemState != 'string')
		{
			return restoreMsgs;
		}
		var msgsArr = problemState.split(/<\/?messages>/);            // remove root element, front and back
		that.ctatdebug("msgsArr.length "+msgsArr.length+", msgsArr[1].length "+(msgsArr[1] ? msgsArr[1].length : -1));
		if(msgsArr.length < 2)
		{
			return restoreMsgs;                                       // nothing to restore
		}
		var problemStateArr = msgsArr[1].split("<message>");
		that.ctatdebug("problemStateArr.length "+problemStateArr.length);
		var omitCount = 0, omitting = false;
		for(var i=1; i<problemStateArr.length; ++i)  // i=1 omits arr[0] == ""
		{                                               // restore delimiter & store in next save set
			var restoreMsg = "<message>"+problemStateArr[i];

			var msgType = CTATMsgType.getMessageType(restoreMsg);
			if(CTATMsgType.STATE_GRAPH == msgType)                // FIXME: old code to
				omitting = true;                                  // remove start state
			else if(CTATMsgType.START_STATE_END == msgType)       // from problem states
				omitting = false;								  // that should omit it
			else if(omitting)
				omitCount++;
			else if(!omitThisMsg(msgType, restoreMsg, i, problemStateArr))
			{
				tracer.getProblemStateSaver().appendToProblemState(restoreMsg, null, true);  // true=>force
				restoreMsgs.push(restoreMsg);
			}
			that.ctatdebug("problemStateArr["+i+"] "+(omitting?"omit ":"keep ")+msgType+" omitted "+omitCount+" kept "+restoreMsgs.length);
		}
		return restoreMsgs;
	}

	/**
	 * Decode entity-escaped XML input. This converts &lt; back to <, &gt; to >, etc.
	 * From http://stackoverflow.com/questions/1912501/unescape-html-entities-in-javascript/34064434#34064434
	 * @param {string} input string to decode
	 * @return {string} decoded input
	 */
	function htmlDecode(input)
	{
	  var doc = new DOMParser().parseFromString(input, "text/html");
	  return doc.documentElement.textContent;
	}

	/**
	 * @return {string} restoreProblemUrl
	 */
	this.getRestoreProblemUrl = function()
	{
		return restoreProblemUrl;
	};

	/**
	 * @param {string} newRestoreProblemUrl new value for restoreProblemUrl
	 */
	this.setRestoreProblemUrl = function(newRestoreProblemUrl)
	{
		restoreProblemUrl = newRestoreProblemUrl;
	};

	/**
	 * @return {string} with url and number of messages retrieved
	 */
	this.toString = function()
	{
		return "["+restoreProblemUrl+": "+nMsgs+"]";
	};

	/**
	 * Get the messages from LMS and queue them to the tracer.
	 * @param {CTATProblemStateStatus} outputStatus from SetPreferences problem_state_status
	 */
	this.retrieveMessages = function(outputStatus)
	{
		//that.ctatdebug("retrieveMessages("+outputStatus+", ...)");
		console.log("retrieveMessages("+outputStatus+", ...)");

		if(!outputStatus.mustRetrieveProblemState())
		{
			//that.ctatdebug ("Retrieval status indicates that we should not retrieve");
			console.log ("Retrieval status indicates that we should not retrieve");
			return;
		}

		tracer.stopWorking(1); // assume async get.
		//process calls methods that do a startWorking so this should not
		//cause problems in the synchronous cases like xblock or scorm
		if (CTATLMS.getProblemState)
			CTATLMS.getProblemState(that.process);
	};

	/**
	 * Replay messages from a retrieved problem state through the tracer.
	 * @param {string} retrieved problem state, as a string
	 */
	this.process = function(problemState)
	{
		tracer.getOutputStatus().transition(CTATMsgType.BEGIN_RESTORE);
		if(typeof(problemState) != "string")
		{
			console.log("ProblemStateRestorer.process() called with parameter not a string: ", typeof(problemState));
			tracer.enqueueForRestore([]);                    // quit restore
			return;
		}
		if(problemState.slice(0, ProblemStateSaver.MESSAGES_TAG.length) != ProblemStateSaver.MESSAGES_TAG)
		{
			console.log("ProblemStateRestorer.process() called with unrecognized string, to try htmlDecode(): ", problemState.slice(0,20));
			problemState = htmlDecode(problemState);
			if(problemState.slice(0, ProblemStateSaver.MESSAGES_TAG.length) != ProblemStateSaver.MESSAGES_TAG)
			{
				console.log("ProblemStateRestorer.process() called with unrecognized string: ", problemState.slice(0,20));
				tracer.enqueueForRestore([]);                    // quit restore
				return;
			}
		}
		var restoreMsgs = parseRestoreString(problemState);  // returns array
		tracer.enqueueForRestore(restoreMsgs);               // enqueue() calls startWorking()
	};
};

ProblemStateRestorer.prototype = Object.create(CTATBase.prototype);
ProblemStateRestorer.prototype.constructor = ProblemStateRestorer;

/**
 * Browser-independent way to get an XMLHttpRequest object?
 * @return XMLHttpRequest.
 */
 /*
ProblemStateRestorer.getXHR = function()
{
	var xhr = null;
	if (typeof (XMLHttpRequest) !== 'undefined')
	{
		xhr = new XMLHttpRequest();
	}
	else if (window.XMLHttpRequest)
	{// code for IE7+, Firefox, Chrome, Opera, Safari
		xhr = new XMLHttpRequest();
	}
	else if (ActiveXObject)
	{// code for IE6, IE5
		xhr = new ActiveXObject("Microsoft.XMLHTTP");
	}
	return xhr;
};
*/

/*if(typeof module !== 'undefined')
{
	module.exports = ProblemStateRestorer;
}*/
