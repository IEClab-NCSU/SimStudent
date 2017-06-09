goog.provide("CTATRuleTracer");

goog.require("CTATNoolsTracer");
goog.require("CTATLogger");

var CTATNoolsTracerLogger = new CTATLogger();

function setTracerLogFlag(flag) {
	CTATNoolsTracerLogger.set(flag);
}

function setTracerLogFlags(flags) {
	var toSet = (flags instanceof Array) ? flags : Array.prototype.slice.call(arguments);
	toSet.forEach((flag)=>{
		setTracerLogFlag(flag);
	});
}

function unsetTracerLogFlag(flag) {
	CTATNoolsTracerLogger.unset(flag);
}

function unsetTracerLogFlags(flags) {
	var toUnset = (flags instanceof Array) ? flags : Array.prototype.slice.call(arguments);
	toUnset.forEach((flag) => {
		unsetTracerLogFlag(flag)
	});
}

var CTATRuleTracer = function(m)
{
	var log = CTATNoolsTracerLogger.log;
	var pointer = this; 
	var mode = m;
	var engine = null;
		
	var sessionIDs = {};
	var sessionCnt = 0;
	var defaultBuggyMsg = null;
	
	/************ Public **************/
	
	/**
	*	Initialize the model, given a source file containing rules and types
	*	@param {String} srcFile a URL to the file containing the model
	*	@param {Function} cbk a function to call on successful compilation of the model
	*	@param {errCbk}	errCbk a function to call on error
	*/
	this.initEngine = function(srcFile, cbk, errCbk, optSessionName)
	{
		switch(mode)
		{
			case 'nools':
				engine = new CTATNoolsTracer(log);
			break;
		}
		engine && engine.init(srcFile, (sId, startStateMsgs, sgMsg) => {
				let sessionName = optSessionName || "session"+(++sessionCnt);
				sessionIDs[sessionName] = sId;
				cbk(startStateMsgs, sgMsg);
			}, errCbk);
	};
	
	/**
	*	assert facts
	*	@param {Array | Object} facts a single fact or list of facts to assert
	*		facts should be objects with a type property specifying a valid fact type,
	*			as well as a 'properties' property which is an object whose key->value pairs
	*			represent slot assignments.
	*/
	this.assert = function(facts)
	{
		if (!facts.constructor === Array)
			facts = [facts];
		
		engine.assert(facts);
	}
	
	/**
	*	Listen for events emitted by the engine
	*	@param {String} e the event type
	*	@param {Function} cbk the function to call when event fires 
	*/
	this.setListener = function(e, cbk)
	{
		engine.setListener(e, cbk);
	};
	
	/**
	*	Pass an action into the model to be evaluated
	*	@param {CTATExampleTracerEvent} result an object to store the result
	*	@param {Function} cbk function to call on complete
	*/
	this.evaluate = function(result, cbk)
	{
		var sai = result.getStudentSAI();
		//pass SAI to engine to evaluate
		var saiSimple = {
			selection: sai.getSelection(),
			action: sai.getAction(),
			input: sai.getInput()
		};
		engine.evaluate(saiSimple, _handleResult.bind(pointer, result, cbk), false);
	};
	
	/**
	*	Get hints from the rule engine
	*	@param {CTATExampleTracerEvent} resultObj object to hold the result
	*	@param transactionID
	*	@param {function} cbk the function to call on complete, resultObj and transactionID are passed 
	*/
	this.doHint = function(resultObj, transactionID, cbk)
	{
		var sai = resultObj.getStudentSAI(),
			saiSimple = _simplifySAI(sai),
			studentSelection = saiSimple.selection,
			tutorSAI,
			hints,
			triedFirstKey = false;
		
		engine.evaluate(saiSimple, (tutorSAIs, firstPredictedSelection) => {
			var getHintsFromBranch = function(sel) {
				for (let branch of tutorSAIs[sel]) {
					if (branch.hints.length > 0) {
						tutorSAI = branch.sai;
						hints = branch.hints.map((hint)=>hint.msg);
						break;
					}
				}
			};
			if (studentSelection && studentSelection !== "hint" && tutorSAIs[studentSelection]) {
				getHintsFromBranch(studentSelection); //try biased hint first
			}
			if (!hints) {
				getHintsFromBranch(firstPredictedSelection); //default to first branch generated
			}
			if (!hints) {
				hints = ["No hints are available for this step"];
				tutorSAI = {selection: "", action: "", input: ""};
			}
			resultObj.setTutorSAI(new CTATSAI(tutorSAI.selection, tutorSAI.action, tutorSAI.input));
			resultObj.setResult(CTATExampleTracerLink.HINT_ACTION);
			resultObj.setReportableHints(hints);
			resultObj.setActor(CTATMsgType.DEFAULT_STUDENT_ACTOR);
			resultObj.setStepID('['+tutorSAI.selection+','+tutorSAI.action+']');

			cbk(resultObj, transactionID)
		}, true);
	}
	
	/**
	*	Check if a given student step was out of order (no valid input exists for that selection)
	*	@param {CTATExampleTracerEvent} object storing student SAI and result
	*	@param {function} cbk function to call on complete, true is passed if step was out of order
	*/
	this.checkOutOfOrder = function(resultObj, cbk)
	{
		var studentInput = _simplifySAI(resultObj.getStudentSAI());
		var lastMatchTutorSAIs = engine.getTutorSAIs();
		var ret = !lastMatchTutorSAIs[studentInput.selection];
		cbk(ret);
	}
	
	/**
	*	Set the default buggy message sent to the interface
	*	@param {String} newMsg the message
	*/
	this.setDefaultBuggyMsg = function(newMsg)
	{
		(newMsg && typeof(newMsg) === "string") && (defaultBuggyMsg = newMsg);
	}
	
	/**
	*	Get the default buggy message
	*	@returns the msg
	*/
	this.getDefaultBuggyMsg = function()
	{
		return defaultBuggyMsg;
	}
	
	/**
	*	Reset the rule engine to its initial state
	*	@param {function} cbk function to call on complete
	*/
	this.reset = function(cbk) {
		
		engine.reset(cbk);
	}
	
	/********** Private ************/
	
	/**
	*	Process the result of an SAI evaluation
	*	@param {CTATExampleTracerEvent} resultEvent stores result of the check
	*	@param {Function} cbk function to call when done
	*	@param {String} result "correct", "incorrect", or "incorrect_fireable"
	*	@param {Object} tutorSAI the SAI predicted by the model
	*/
	function _handleResult(resultEvent, cbk, result, chainData)
	{
		var msg = engine.getSuccessOrBugMsg(),
			predictedSAI = chainData.sai,
			tSAI = new CTATSAI(predictedSAI.selection, predictedSAI.action, predictedSAI.input),
			sSAI = resultEvent.getStudentSAI();
		
		log("debug", 'ruleTracer.handleResult, result = '+result+
					 ', tutorSAI = '+JSON.stringify(predictedSAI)+
					 ', msg = '+msg+
					 ', associated rules = '+chainData.rules);
		
		resultEvent.setTutorSAI(tSAI);
		resultEvent.setSuccessOrBuggyMsg(msg);
		resultEvent.setAssociatedRules(chainData.rules);
		switch(result) {
			case "correct": 
				resultEvent.setResult(CTATExampleTracerLink.CORRECT_ACTION);
			break;
			case "bug":
				_handleIncorrect(resultEvent, sSAI, true);
			break;
			case "no_model":
				_handleIncorrect(resultEvent, sSAI, false);
			break;
		}

		cbk();
	}
	
	/**
	*	Handle an incorrect student input
	*	@param {CTATExampleTracerEvent} resultEvent object to store the result
	*	@param {Object} studentSAI the student input
	*	@param {boolean} hasMatch whether a matching buggy step was found for the student input
	*/
	function _handleIncorrect(resultEvent, studentSAI, hasMatch)
	{
		if (studentSAI.getSelection().includes("done")
		&&	studentSAI.getAction() === "ButtonPressed") //hit done out of order
		{
			resultEvent.setDoneStepFailed(true)
		}
		if (hasMatch)
			resultEvent.setResult(CTATExampleTracerLink.BUGGY_ACTION);
		else
			resultEvent.setResult(CTATExampleTracerLink.NO_MODEL);
	}
	
	/**
	*	--- not used currently ---
	*/
	function _handleIncorrectFireable(resultEvent)
	{
		resultEvent.setResult(CTATExampleTracerLink.FIREABLE_BUGGY_ACTION);
		return engine.getSuccessOrBugMsg();
	}
	
	/**
	*	Simplify a given CTATSAI object
	*	@param {CTATSAI} sai the sai to simplify
	*	@returns {Object} object with selection, action, and input properties
	*/
	function _simplifySAI(sai)
	{
		var saiSimple = {
			selection: sai.getSelection() || "",
			action: sai.getAction() || "",
			input: sai.getInput() || ""
		};
		
		return saiSimple;
	}
}