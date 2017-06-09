/**
 * This object is meant to serve the purpose of the Java enum BR_Controller.ProblemStateStatus.
 * That is, an enum for problem_state_status with methods to tell whether to suppress output.
 */
goog.provide('CTATProblemStateStatus');

goog.require('CTATBase');
goog.require('CTATMsgType');

CTATProblemStateStatus = function()
{
	/** Current status: one of "empty", "incompleteStartState", "incomplete", "goingToState", "complete", "completedEarlier". */
	var status = "empty";

	/**
	 * @return {string} status
	 */
	this.getStatus = function()
	{
		return status;
	};

	/**
	 * @param {string} newStatus new value for status
	 */
	this.setStatus = function(newStatus)
	{
		status = newStatus;
	};
};

/** Problem restore finished. */
Object.defineProperty(CTATProblemStateStatus, "empty", {enumerable: false, configurable: false, writable: false, value: "empty"});

/** Sending start state before problem restore replay begins. */
Object.defineProperty(CTATProblemStateStatus, "incompleteStartState", {enumerable: false, configurable: false, writable: false, value: "incompleteStartState"});

/** Sending start state at other times. */
Object.defineProperty(CTATProblemStateStatus, "startState", {enumerable: false, configurable: false, writable: false, value: "startState"});

/** Restoring problem with output suppressed. */
Object.defineProperty(CTATProblemStateStatus, "incomplete", {enumerable: false, configurable: false, writable: false, value: "incomplete"});

/** Transitioning to a state with feedback suppressed. */
Object.defineProperty(CTATProblemStateStatus, "goingToState", {enumerable: false, configurable: false, writable: false, value: "goingToState"});

/** Normal feedback. */
Object.defineProperty(CTATProblemStateStatus, "normalFeedback", {enumerable: false, configurable: false, writable: false, value: "normalFeedback"});

/** Problem just finished. */
Object.defineProperty(CTATProblemStateStatus, "complete", {enumerable: false, configurable: false, writable: false, value: "complete"});

/** Problem finished in a prior session. */
Object.defineProperty(CTATProblemStateStatus, "completedEarlier", {enumerable: false, configurable: false, writable: false, value: "completedEarlier"});

CTATProblemStateStatus.prototype = Object.create(CTATBase.prototype);
CTATProblemStateStatus.prototype.constructor = CTATProblemStateStatus;

/**
 * Tell whether the current state requires that we actually send saved messages for restore.
 * @return {boolean} true if should save for restore
 */
CTATProblemStateStatus.prototype.isSendingSavedMsgsForRestore = function()
{
	switch(this.getStatus())
	{
	case CTATProblemStateStatus.normalFeedback:
	case CTATProblemStateStatus.complete:
		return true;
	default:
		return false;
	}
};

/**
 * Tell whether the current state requires that we save messages for restore
 * @return {boolean} true if should save for restore
 */
CTATProblemStateStatus.prototype.mustSaveForRestore = function()
{
	switch(this.getStatus())
	{
	case CTATProblemStateStatus.incomplete:
	case CTATProblemStateStatus.incompleteStartState:
	case CTATProblemStateStatus.goingToState:
	case CTATProblemStateStatus.completedEarlier:
		return false;
	case CTATProblemStateStatus.startState:
	case CTATProblemStateStatus.normalFeedback:
	case CTATProblemStateStatus.complete:
	default:
		return true;
	}
};

/**
 * Tell whether the current state requires that we update correct and incorrect counts in the Problem Summary
 * @return {boolean} true if should save for restore
 */
CTATProblemStateStatus.prototype.countForProblemSummary = function()
{
	switch(this.getStatus())
	{
	case CTATProblemStateStatus.incompleteStartState:
	case CTATProblemStateStatus.goingToState:
	case CTATProblemStateStatus.startState:
	case CTATProblemStateStatus.completedEarlier:
		return false;
	case CTATProblemStateStatus.incomplete:
	case CTATProblemStateStatus.normalFeedback:
	case CTATProblemStateStatus.complete:
	default:
		return true;
	}
};

/**
 * Tell whether the current status value suppresses output
 * @return {boolean} true if output to the student UI should be suppressed
 */
CTATProblemStateStatus.prototype.isOutputSuppressed = function()
{
	switch(this.getStatus())
	{
	case CTATProblemStateStatus.goingToState:
		return true;
	case CTATProblemStateStatus.incompleteStartState:
	case CTATProblemStateStatus.incomplete:
	default:
		return false;
	}
};

/**
 * Tell whether we're currently reviewing state from previously-completed problem.
 * @return {boolean} true if status is complete
 */
CTATProblemStateStatus.prototype.isComplete = function()
{
	switch(this.getStatus())
	{
	case CTATProblemStateStatus.complete:
	case CTATProblemStateStatus.completedEarlier:
		return true;
	default:
		return false;
	}
};

/**
 * Tell whether we could be restoring state from a prior problem.
 * @return {boolean} true if status is incomplete or incompleteStartState or complete
 */
CTATProblemStateStatus.prototype.canRestore = function()
{
	switch(this.getStatus())
	{
	case CTATProblemStateStatus.incompleteStartState:
	case CTATProblemStateStatus.incomplete:
	case CTATProblemStateStatus.complete:
	case CTATProblemStateStatus.completedEarlier:
		return true;
	default:
		return false;
	}
};

/**
 * Tell whether the current status value means we should restore the problem.
 * @return {boolean} true if output to the student UI should be suppressed
 */
CTATProblemStateStatus.prototype.mustRetrieveProblemState = function()
{
	switch(this.getStatus())
	{
	case CTATProblemStateStatus.incompleteStartState:
	case CTATProblemStateStatus.incomplete:
	case CTATProblemStateStatus.complete:
	case CTATProblemStateStatus.completedEarlier:
		return true;
	default:
		return false;
	}
};

/**
 * Change the status property in response to a given message type and problem_state_status.
 * @param {string} msgType
 * @param {string} problemStateStatus optional; expected only when msgType is SetPreferences
 * @return {string} new value for status property
 */
CTATProblemStateStatus.prototype.transition = function(msgType, problemStateStatus)
{
	var oldStatus = this.getStatus();
	if(CTATProblemStateStatus.completedEarlier == oldStatus)
	{
		return;                                 // no transitions if already complete
	}
	if(CTATProblemStateStatus.complete == oldStatus)
	{				                               // only 1 transition once complete
		if(problemStateStatus == CTATProblemStateStatus.completedEarlier)
		{
			this.setStatus(CTATProblemStateStatus.completedEarlier);
			return;
		}
	}
	switch(msgType)
	{
	case CTATMsgType.SET_PREFERENCES:
		if(CTATProblemStateStatus[problemStateStatus])
		{
			if(CTATProblemStateStatus.incomplete == CTATProblemStateStatus[problemStateStatus])
				this.setStatus(CTATProblemStateStatus.incompleteStartState);
			else if(CTATProblemStateStatus.complete == CTATProblemStateStatus[problemStateStatus])
				this.setStatus(CTATProblemStateStatus.completedEarlier);
			else
				this.setStatus(CTATProblemStateStatus.startState);
		}
		break;
	case CTATMsgType.STATE_GRAPH:
		if(this.getStatus() == CTATProblemStateStatus.incomplete || this.getStatus() == CTATProblemStateStatus.incompleteStartState)
			this.setStatus(CTATProblemStateStatus.incompleteStartState);
		else
			this.setStatus(CTATProblemStateStatus.startState);
		break;
	case CTATMsgType.START_STATE_END:
		if(this.getStatus() == CTATProblemStateStatus.incompleteStartState)
			this.setStatus(CTATProblemStateStatus.incomplete);
		else
			this.setStatus(CTATProblemStateStatus.normalFeedback);
		break;
	case CTATMsgType.PROBLEM_RESTORE_END:
	case CTATMsgType.END_GO_TO_STATE:
		this.setStatus(CTATProblemStateStatus.normalFeedback);
		break;
	case CTATMsgType.BEGIN_GO_TO_STATE:
		this.setStatus(CTATProblemStateStatus.startState);
		break;
	case CTATMsgType.BEGIN_RESTORE:
		if(this.getStatus() != CTATProblemStateStatus.incompleteStartState)
			this.setStatus(CTATProblemStateStatus.incomplete);
		break;
	default:
		break;
	}
	ctatdebug("CTATProblemStateStatus.transition("+msgType+", "+problemStateStatus+") old "+oldStatus+" => new "+this.getStatus());
	return this.getStatus();
};

/**
 * Dump status for debugging.
 * @return {string}
 */
CTATProblemStateStatus.prototype.toString = function()
{
	return "{" + this.getStatus() + "}";
};
