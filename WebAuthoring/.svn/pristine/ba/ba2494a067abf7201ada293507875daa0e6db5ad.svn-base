goog.provide('OLILogger');

goog.require('ActionLog');
goog.require('SupplementaryLog');

var OLILogger = function(authToken, resourceId, sessionId, userGuid) {

	this.authToken = authToken;
	this.resourceId = resourceId;
	this.sessionId = sessionId;
	this.prolog = '<?xml version="1.0" encoding="UTF-8"?>'

		
	console.log("*********** inside OLI Logger");
	console.log(authToken);
	console.log(resourceId);
	console.log(sessionId);
	console.log("*****************************");
	
	
	var hintSource = "button";
	var externalId = "externalId";

	// constants
	var source = "ASSESSMENT_ACTIVITY";

	// action constants
	var VIEW_HINT = "VIEW_HINT";
	var START_ATTEMPT = "START_ATTEMPT";
	var START_SESSION = "START_SESSION";
	var EVAL_QUESTION = "EVALUATE_QUESTION";
	var SAVE_ATTEMPT = "SAVE_ATTEMPT";
	var SUBMIT_ATTEMPT = "SUBMIT_ATTEMPT";
	var SET_AUTO_OUTCOME = "SET_AUTOMATIC_OUTCOME";
	var MARK_CORRECT = "MARK_CORRECT";
	var SCORE_QUESTION = "SCORE_QUESTION";
	var SCORE_ATTEMPT = "SCORE_ATTEMPT";
	var SAVE_QUESTION = "SAVE_QUESTION";
	var EVAL_RESPONSE = "EVALUATE_RESPONSE";

	this.logHint = function(hint, questionId, stepId, attemptNumber) {
		var attemptLog = new SupplementaryLog(VIEW_HINT, "attempt", attemptNumber);
		var hintLog = new SupplementaryLog(VIEW_HINT, questionStepId(questionId, stepId), hint);
		var sourceLog = new SupplementaryLog(VIEW_HINT, "source", hintSource);

		var actionLog = new ActionLog(this.sessionId, VIEW_HINT, externalId, this.resourceId);
		actionLog.addSupplements([attemptLog, hintLog, sourceLog]);

	
		console.log("Logging from log hint: " + actionLog.toXMLString());

		
		logActionLog(actionLog);
	}

	this.startAttempt = function(attemptNumber) {
		var attemptLog = new SupplementaryLog(START_ATTEMPT, "attempt", attemptNumber);

		var actionLog = new ActionLog(this.sessionId, START_ATTEMPT, externalId, this.resourceId);
		actionLog.addSupplement(attemptLog);
		console.log("Logging: " + actionLog.toXMLString());
		logActionLog(actionLog);
	}

	this.logStudentResponse = function(studentReponse, attemptNumber, questionId, inputId) {
		var questionLog = new SupplementaryLog(EVAL_QUESTION, "question", questionId);
		var attemptLog = new SupplementaryLog(EVAL_QUESTION, "attempt", attemptNumber);
		var responseLog = new SupplementaryLog(EVAL_QUESTION, questionInputId(questionId, inputId), studentResponse);

		var actionLog = new ActionLog(this.sessionId, EVAL_QUESTION, externalId, this.resourceId);
		actionLog.addSupplements([questionLog, attemptLog, responseLog]);
		console.log("Logging: " + actionLog.toXMLString());
		logActionLog(actionLog);
	};

	this.logTutorResponse = function(tutorResponse, attemptNumber, correct, questionId, inputId) {
		var questionLog = new SupplementaryLog(EVAL_RESPONSE, "question", questionId);
		var attemptLog = new SupplementaryLog(EVAL_RESPONSE, "attempt", attemptNumber);
		var correctLog = new SupplementaryLog(MARK_CORRECT, questionId, correct);
		var responseLog = new SupplementaryLog(EVAL_RESPONSE, questionInputId(questionId, inputId), studentResponse);

		var actionLog = new ActionLog(this.sessionId, EVAL_RESPONSE, externalId, this.resourceId);
		actionLog.addSupplements([questionLog, attemptLog, correctLog, responseLog]);
		console.log("Logging: " + actionLog.toXMLString());
		logActionLog(actionLog);
	}

	// postLog implemented by WatsonLogger.js
	var pointer = this;
	function logActionLog(actionLog) {
		actionLog.userGuid = this.userGuid;
		actionLog.sessionId = this.sessionId;
		pointer.postLog(actionLog.toXMLString());
	}

	function questionStepId(questionId, stepId) {
		return questionId + "/" + stepId;
	}

	function questionInputId(questionId, inputId) {
		return questionId + "/" + inputId;
	}
}
