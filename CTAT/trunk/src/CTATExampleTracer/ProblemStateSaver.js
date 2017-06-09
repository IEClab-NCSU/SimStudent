/**
 * Maintain during problem execution and transmit to the LMS the information
 * needed to restore the problem state if execution is interrupted.
 */

goog.provide('ProblemStateSaver');

goog.require('CTATBase');
goog.require('CTATCommLibrary');
goog.require('CTATMsgType');
goog.require('CTAT.ToolTutor');
goog.require('CTATLMS');

/**
 * Constructor.
 * @param {CTATExampleTracer} tracer
 */
ProblemStateSaver = function(tracer)
{
	CTATBase.call(this, "ProblemStateSaver", tracer);

	this.tracer = tracer;

	/** Pointer to self. */
	var that = this;

	/** {Array<string>} The problem state so far, encoded, one message or bundle per element. */
	var problemState = [];

	/** {Map<string, Number>} Associate indices in problemState with step identifiers. */
	var stepToIndexMap = null;

	/** {string} URL for the save-state messages. */
	var curriculum_service_url = ""; // "http://localhost:8080/cgi-bin/logcatc.sh";

	/** {string} POST parameter needed by Rails for the save-state messages. */
	var authenticity_token = "";

	/** {CTATCommLibrary} Maintains queue of XHR instances, to prevent clobbering. */
	var commLibrary = null;

	/** {integer} Unique identifier for the last save request. */
	var serialNo = 0;

	/**
	 * Create the POST data to send with the save-as-you-go request.
	 * @param {CTATProblemSummary} ps problem summary to encode
	 * @return {object} with properties to send
	 */
	function createPostData(ps)
	{
		var result = {};

		result["session_id"]         = tracer.getSessionID();
		result["authenticity_token"] = authenticity_token;
		result["summary"]            = ps.toXML(false);

		result["problem_state"] = ProblemStateSaver.MESSAGES_TAG;

		for(var i = 0; i < problemState.length; ++i)             // FIXME? length
		{
			if(problemState[i])                                  // can be null
			{
	            result["problem_state"] += problemState[i];
			}
		}

		result["problem_state"] += ProblemStateSaver.MESSAGES_TAG.replace('<', '</');

		return result;
	}

	/**
	 * Send a request to the curriculum_service_url.
	 * @param {CTATProblemSummary} ps problem summary for createPostData()
	 */
	this.saveAsYouGo = function(ps)
	{
		that.ctatdebug("saveAsYouGo() curriculum_service_url "+curriculum_service_url+", tracer "+tracer);

		if(!tracer || !ps)
		{
			that.ctatdebug("Error: no tracer or no problem summary available, aborting save as you go");
			return;
		}

		var postData = createPostData(ps);
		++serialNo;
		that.ctatdebug("saveAsYouGo["+serialNo+"] to send problem_state length "+(postData["problem_state"]).length);

		if (CTATLMS.saveProblemState)
		{
			CTATLMS.saveProblemState(postData);
		}
		if (CTATLMS.gradeStudent)
		{
			CTATLMS.gradeStudent(parseInt(ps.getCorrect()),
					parseInt(ps.getRequiredSteps()));
		}
		if (CTATLMS.commit)
		{
			CTATLMS.commit();
		}
	};

	/**
	 * @param {string} csUrl new value for curriculum_service_url
	 */
	this.setCurriculumServiceUrl = function(csUrl)
    {
		that.ctatdebug("ProblemStateSaver.setCurriculumServiceUrl() old "+curriculum_service_url+", new "+csUrl);
		curriculum_service_url = csUrl;
    };

	/**
	 * @param {string} authTkn new value for authenticity_token
	 */
	this.setAuthenticityToken = function(authTkn)
    {
		that.ctatdebug("ProblemStateSaver.setAuthenticityToken() old "+authenticity_token+", new "+authTkn);
		if(authTkn)
		{
			authenticity_token = authTkn;
		}
    };

	/**
	 * @return {object<Map>} stepToIndexMap; creates map if var is null
	 */
	this.getStepToIndexMap = function()
	{
		if(stepToIndexMap === null)
		{
			stepToIndexMap = new Map();
		}
		return stepToIndexMap;
	};

	/**
	 * @return {string} problemState;
	 */
	this.getProblemState = function()
	{
		return problemState;
	};

	/**
	 * @return {integer} messageCount;
	 */
	this.getProblemStateLength = function()
	{
		return problemState.length;
	};
};

ProblemStateSaver.prototype = Object.create(CTATBase.prototype);
ProblemStateSaver.prototype.constructor = ProblemStateSaver;

/**
 * Add a message to the set to be saved for restoring the problem state.
 * @param {string} message encode and append this message to the problem state
 * @param {object} outputStatus governs whether to send, whether to save for restore
 * @param {object<CTATSAI>} sai student SAI with key for stepToIndexMap
 * @param {boolean} append if true, simply append the message: don't use stepToIndexMap
 */
ProblemStateSaver.prototype.replaceInProblemState = function(message, outputStatus, sai, append)
{
	this.ctatdebug("PSS.replaceInProblemState("+CTATMsgType.getMessageType(message)+", "+sai+", "+append+")");
	if(outputStatus.mustSaveForRestore() && ProblemStateSaver.mustSaveForRestore(CTATMsgType.getMessageType(message)))
	{
		var msgs = this.getProblemState();
		var startIndex = 0;
		var msg = this.editForProblemState(message);
		if(!append)
		{
			var stepKey = ProblemStateSaver.makeStepKey(sai);
			var stiMap = this.getStepToIndexMap();
			this.ctatdebug("PSS.replaceInProblemState() stepKey"+stepKey+", index "+stiMap.get(stepKey)+")");
			if(stiMap.has(stepKey))  // do not simply test .get(), for 0 is a positive value
			{
				var oldIndex = stiMap.get(stepKey);
				msgs[oldIndex] = null;                   // blank prior entry for this step
			}
			stiMap.set(stepKey, msgs.length);            // new index at end
		}
		msgs.push(msg);
	}
};

/**
 * Add a message to the set to be saved for restoring the problem state.
 * @param {string} message encode and append this message to the problem state
 * @param {object} outputStatus governs whether to send, whether to save for restore
 * @param {boolean} force optional parameter to force save
 */
ProblemStateSaver.prototype.appendToProblemState = function(message, outputStatus, force)
{
	if(force || (outputStatus.mustSaveForRestore() && ProblemStateSaver.mustSaveForRestore(CTATMsgType.getMessageType(message))))
	{
		var msgs = this.getProblemState();
		var startIndex = 0;
		var msg = this.editForProblemState(message);
		msgs.push(msg);
	}
};

/** Regular expression to match an XML prologue. */
Object.defineProperty(ProblemStateSaver, "XML_PROLOGUE", {enumerable: false, configurable: false, writable: false, value: /<\? *[xX][mM][lL][^?]*\?>/});

/** Regular expression to match leading white space on lines. */
Object.defineProperty(ProblemStateSaver, "LEADING_WHITE_SPACE", {enumerable: false, configurable: false, writable: false, value: /^\s+</m});

/** Root XML tag name for saved image. */
Object.defineProperty(ProblemStateSaver, "MESSAGES_TAG", {enumerable: false, configurable: false, writable: false, value: "<messages>"});

/**
 * @param {string} xmlStr message as XML string
 * @return altered message, still XML but with XML prologue and leading white space on lines removed
 */
ProblemStateSaver.prototype.editForProblemState = function(xmlStr)
{
	var resultArr = xmlStr.split(ProblemStateSaver.XML_PROLOGUE);
	var result = resultArr[0];
	var i;
	for(i = 1; i < resultArr.length; ++i)
		result += resultArr[i];

	resultArr = result.split(ProblemStateSaver.LEADING_WHITE_SPACE);
	result = resultArr[0];
	for(i = 1; i < resultArr.length; ++i)
	{                              // sorry: cannot seem to get line terminators to match in regex
		if(result.endsWith("\n")) result = result.slice(0, -1);
		if(result.endsWith("\r")) result = result.slice(0, -1);
		result += "<" + resultArr[i];                      // regex matches the "<", so replace it
	}

	this.ctatdebug("ProblemStateSaver.editForProblemState("+xmlStr+")\n returns "+result);
	return result;
};

/**
 * Static object has list of message types that should not be saved.
 */
ProblemStateSaver.omitFromRestore = { init: false };

/**
 * Tell whether the messages of the given type need to be saved for problem restore.
 * @param {string} msgType
 * @return true if should save; default is false
 */
ProblemStateSaver.mustSaveForRestore = function(msgType)
{
	if(!ProblemStateSaver.omitFromRestore.init)  // initialize on first use, then leave fixed
	{
		ProblemStateSaver.omitFromRestore[CTATMsgType.ASSOCIATED_RULES.toLowerCase()] = true;
		ProblemStateSaver.omitFromRestore[CTATMsgType.BUGGY_MESSAGE.toLowerCase()] = true;
		ProblemStateSaver.omitFromRestore[CTATMsgType.CORRECT_ACTION.toLowerCase()] = true;
		ProblemStateSaver.omitFromRestore[CTATMsgType.INCORRECT_ACTION.toLowerCase()] = true;
		ProblemStateSaver.omitFromRestore[CTATMsgType.INTERFACE_IDENTIFICATION.toLowerCase()] = true;
		ProblemStateSaver.omitFromRestore[CTATMsgType.PROBLEM_RESTORE_END.toLowerCase()] = true;
		ProblemStateSaver.omitFromRestore[CTATMsgType.PROBLEM_SUMMARY_REQUEST.toLowerCase()] = true;
		ProblemStateSaver.omitFromRestore[CTATMsgType.PROBLEM_SUMMARY_RESPONSE.toLowerCase()] = true;
		ProblemStateSaver.omitFromRestore[CTATMsgType.SET_PREFERENCES.toLowerCase()] = true;
		ProblemStateSaver.omitFromRestore[CTATMsgType.SHOW_HINTS_MESSAGE.toLowerCase()] = true;
		ProblemStateSaver.omitFromRestore[CTATMsgType.SUCCESS_MESSAGE.toLowerCase()] = true;
		ProblemStateSaver.omitFromRestore.init = true;
	}
	if(msgType && ProblemStateSaver.omitFromRestore[msgType.toLowerCase()])
		return false;
	else
		return true;
};

/**
 * Record a message for problem-restore and send it to the user interface using the global CTAT.ToolTutor.sendToInterface().
 * @param {string} msg
 * @param {boolean} endOfTransaction used only with nodejs implementation of sendToInterface
 * @param {object} outputStatus governs whether to send, whether to save for restore
 * @return {boolean} true if sent the message
 */
ProblemStateSaver.prototype.forwardToInterface = function(msg, endOfTransaction, outputStatus)
{
	var msgType = CTATMsgType.getMessageType(msg);
	this.ctatdebug("PSS.forwardToInterface("+msgType+") problemState.length "+this.getProblemState().length+", outputStatus "+outputStatus);
	if(outputStatus)
	{
		if(outputStatus.isOutputSuppressed())
			return false;
	}
	CTAT.ToolTutor.sendToInterface(msg, endOfTransaction);
	if(outputStatus)
		outputStatus.transition(CTATMsgType.getMessageType(msg));
	return true;
};

/**
 * Process the host's response to a save-as-you-go request.
 * @param {string} the response body
 */
ProblemStateSaver.prototype.processMessage = function(responseBody)
{
	this.ctatdebug("ProblemStateSaver.processMessage("+responseBody+")");
};

/**
 * Static function creates step key from SAI.
 * @param {object<CTATSAI>} sai
 * @return {string} selection and action from sai, space-separated
 */
ProblemStateSaver.makeStepKey = function(sai)
{
	if(!sai)
	{
		return " ";
	}
	var selection = sai.getSelection();
	var action = sai.getAction();
	selection = (typeof(selection) != "string" || selection.length < 1 ? " " : selection);
	action = (typeof(action) != "string" || action.length < 1 ? " " : action);
	return selection + " " + action;
};

if(typeof module !== 'undefined')
{
	module.exports = ProblemStateSaver;
}
