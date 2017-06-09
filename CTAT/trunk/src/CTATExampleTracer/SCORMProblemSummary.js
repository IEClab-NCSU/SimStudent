/**
 * Copyright (c) 2016 Carnegie Mellon University.
 */

goog.provide('SCORMProblemSummary');

goog.require('CTATBase');
goog.require('CTATMsgType');

SCORMProblemSummary = function()
{
	CTATBase.call(this, "SCORMProblemSummary", "");
};

/** Name of completion status data element. */
Object.defineProperty(SCORMProblemSummary, "LESSON_STATUS", {enumerable: false, configurable: false, writable: false, value: "cmi.core.lesson_status"});

/** Name of score data element. */
Object.defineProperty(SCORMProblemSummary, "RAW_SCORE", {enumerable: false, configurable: false, writable: false, value: "cmi.core.score.raw"});

/** Name of exit-reason data element. */
Object.defineProperty(SCORMProblemSummary, "EXIT", {enumerable: false, configurable: false, writable: false, value: "cmi.core.exit"});

/** Name of session time data element. */
Object.defineProperty(SCORMProblemSummary, "SESSION_TIME", {enumerable: false, configurable: false, writable: false, value: "cmi.core.session_time"});

/**
 * Prescribed values for data element EXIT. For done, the prescribed value is an empty string,
 * but Flash needs an argument, so we insert a single space.
 */
Object.defineProperty(SCORMProblemSummary, "ExitReason", {enumerable: false, configurable: false, writable: false, value: {
	timeout: "time-out",
	suspend: "suspend",
	logout:  "logout",
	done:    " "
}});

/**
 * Prescribed values for element LESSON_STATUS.
 */
Object.defineProperty(SCORMProblemSummary, "LessonStatus", {enumerable: false, configurable: false, writable: false, value: {
	passed: "passed",
	completed: "completed",
	failed: "failed",
	incomplete: "incomplete",
	browsed: "browsed",
	notAttempted: "not attempted"
}});

/**
 * Value for element LESSON_STATUS, from CTATProblemSummary.getCompletionStatus().
 * @param {CTATProblemSummary} ps
 * @return string suitable for SCORM
 */
SCORMProblemSummary.getLessonStatus = function(ps)
{
	return (CTATMsgType.CompletionValue[1] == ps.getCompletionStatus() ? SCORMProblemSummary.LessonStatus.completed : SCORMProblemSummary.LessonStatus.incomplete);
};

/**
 * Value for element {@value #RAW_SCORE}. Calculated as ProblemSummary.getUniqueCorrectUnassisted()/ProblemSummary.getUniqueSteps().
 * @param {CTATProblemSummary} ps
 * @return number in range [0,1] with 2 decimal places; 0 if ProblemSummary.getUniqueSteps() returns 0
 */
SCORMProblemSummary.getRawScore = function(ps)
{
	var uniqueSteps = ps.getUniqueSteps();
	if(uniqueSteps == 0)
	{
		return 0;                   // avoid divide-by-zero fault
	}
	return parseInt((ps.getUniqueCorrectUnassisted()/uniqueSteps+0.005)*100);
};

/**
 * Value for data element {@value #EXIT}.
 * @param {CTATProblemSummary} ps currently unused
 * @return {string} constant for SCORMProblem.ExitReason.suspend
 */
SCORMProblemSummary.getExitReason = function(ps)
{
	return SCORMProblemSummary.ExitReason.suspend;
};

/**
 * Value for element SESSION_TIME, from CTATProblemSummary.getTimeElapsed().
 * @param {CTATProblemSummary} ps
 * @return number of seconds, to 2 decimal places
 */
SCORMProblemSummary.getSessionTime = function(ps)
{
	var tms = ps.getTimeElapsed();
	var ms = tms % 1000;
	var s = (tms - ms)/1000;
	var m = s/60;
	var h = m/60;
	s = s % 60;
	if(h > 9999)
    {
		h = 9999; m = s = 99; ms = 99*10;
	}
	return sprintf("%04d:%02d:%02d.%02d", h, m, s, ms/10);
};

/**
 * Return a single string with all of the SCORM result elements.
 * @param {CTATProblemSummary} ps
 * @return {string} XML string
 */
SCORMProblemSummary.getProblemSummaryElements = function(ps)
{
	var ls = sprintf("<%s>%s</%s>", SCORMProblemSummary.LESSON_STATUS, SCORMProblemSummary.getLessonStatus(ps), SCORMProblemSummary.LESSON_STATUS);
	var rs = sprintf("<%s>%d</%s>", SCORMProblemSummary.RAW_SCORE, SCORMProblemSummary.getRawScore(ps), SCORMProblemSummary.RAW_SCORE);
	var ex = sprintf("<%s>%s</%s>", SCORMProblemSummary.EXIT, SCORMProblemSummary.getExitReason(ps), SCORMProblemSummary.EXIT);
	var st = sprintf("<%s>%s</%s>", SCORMProblemSummary.SESSION_TIME, SCORMProblemSummary.getSessionTime(ps), SCORMProblemSummary.SESSION_TIME);
	return ls+rs+ex+st;
};

CTATMsgType.prototype = Object.create(CTATBase.prototype);
CTATMsgType.prototype.constructor = SCORMProblemSummary;

if(typeof module !== 'undefined')
{
    module.exports = SCORMProblemSummary;
}

