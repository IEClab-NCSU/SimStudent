/* This object represents a CTATMessageTank */
/* LastModify: FranceskaXhakaj 11/14*/

goog.provide('CTATMessageTank');
goog.require('CTATBase');
goog.require('CTATMessage');
goog.require('CTATETEvent');

/**
 * Represents the CTATMessageTank.
 * @constructor
 * @augments CTATBase
 * @param {CTATExampleTracerTracer} givenExampleTracer 
 */
CTATMessageTank = function(givenExampleTracer) 
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

	//calling the constructor of the super class
	CTATBase.call(this, "CTATMessageTank","");

/**************************** PUBLIC INSTANCE VARIABLES ******************************************************/


/**************************** PRIVATE INSTANCE VARIABLES ******************************************************/

   /**
    * Reference to the example tracer.
    * @type {CTATExampleTracerTracer}
    */
    var exTracer = givenExampleTracer;


   /**
    * True if the tank is logically empty.
    * @type {boolean}
    */
	var tankEmpty = true;

   /**
    * True if the tank is logically empty.
    * @type {Map<String, CTATMessage>}
    */
	var delayedFeedback = null;

   /**
    * Holds the response Comm messages.
    * @type {array with pairs (msg: CTATMessage, evt: CTATETEvent)}
    */
	var messageTank = null; 

   /**
    * Message types to defer. 
    * @type {Set of strings}
    */
	var msgTypesToDefer = new Set();
	msgTypesToDefer.add("SuccessMessage");
	msgTypesToDefer.add("BuggyMessage");
	msgTypesToDefer.add("HighlightMsg");
	msgTypesToDefer.add("NotDoneMessage");
	
   /**
    * Holds the response Comm messages.
    * @type {String}
    */
	transaction_id = null;

   /**
    * To turn off logging: use with care. Values true & false have effect; no-op if null.
    * @type {boolean}
    */
	var suppressLogging = null; 

	/**
     * Make the object available to private methods
     */
	var that = this; 

/***************************** PRIVATE METHODS *****************************************************/

    /**
     * Send a message to the student interface. This API is meant for tutor-performed actions and
     * side effects generated from a link match. Creates a new transaction_id value for the message. 
     * Adds the new message to the tank.
     * @param {String} messageType
     * @param {array} selection
     * @param {array} action
     * @param {array} input
     * @param {String} subtype
     * @return {String} new message's transaction_id
     */
	function enqueueMessageToStudent (messageType, selection, action, input, subtype)
	{
		var mo = CTATMessage.create(messageType, "NotePropertySet");

		mo.setSelection(selection);
		mo.setAction(action);
		mo.setInput(input);

		mo.setProperty(CTATTutorMessageBuilder.TRIGGER, "DATA");
		mo.setProperty(CTATTutorMessageBuilder.SUBTYPE, (subtype === null || typeof(subtype) === 'undefined' || subtype.length < 1 ? CTATTutorMessageBuilder.TUTOR_PERFORMED : subtype));
		
		mo.lockTransactionId(CTATMessageObject.makeTransactionId());

		var evt = new CTATExampleTracerEvent(null, new CTATExampleTracerSAI(selection, action, input, CTATMsgType.DEFAULT_TOOL_ACTOR));

		addToMessageTank(mo, evt);

		return mo.getTransactionId();
	}

	/**
	 * Collect results from other messages into the "AssociatedRules" message.
	 * @param {CTATProblemSummary} ps summary results to update
	 * @return {undefined}
	 */
	function consolidateMessageTank(ps)
	{
		//we do not need a respondMsgs; we can simply use a getMessageTank()

		var associatedSkillsMsg = null;
		var success = "";
		var buggy = "";

		messageTank.forEach(function(messg)
		{
			if(messg["msg"].getMessageType() === "AssociatedRules")
			{
				associatedSkillsMsg = msg;
			}
			else
			{
				if(messg["msg"].getMessageType() === "SuccessMessage")
				{
					success = messg["msg"].getProperty("SuccessMsg");
				}
				else
				{
					if(messg["msg"].getMessageType() === "BuggyMessage")
					{
						buggy = messg["msg"].getProperty("BuggyMsg");
					}

					if(messg["msg"].getMessageType() === "HighlightMsg")
					{
						buggy = messg["msg"].getProperty("HighlightMsgText");
					}

					if(messg["msg"].getMessageType() === "NotDoneMessage")
					{
						buggy = messg["msg"].getProperty("Message");
					}
				}
			}
		});

		if(associatedSkillsMsg !== null && typeof(associatedSkillsMsg) !== 'undefined')
		{
			if(success !== "")
			{
				associatedSkillsMsg.setProperty("TutorAdvice", success);
			}
			else if(buggy !== "")
			{
				associatedSkillsMsg.setProperty("TutorAdvice", buggy);
			}

			associatedSkillsMsg.setProperty("LogAsResult", "true");

			updateProblemSummary(ps, associatedSkillsMsg);
		}
	}

	/**
	 * For selected messages, set selection variables to input values in the
	 * ProblemModel's variable table.
	 * @param {CTATMessage} msg
	 * @return {undefined}
	 */
	function processInterfaceVariables(msg)
	{
		var msgType = msg.getMessageType();

		if(msgType === null || typeof(msgType) === 'undefined')
		{
			return;
		}

		msgType = msgType.toLowerCase();

		if(msgType.indexOf("correct") === 0 || msgType.indexOf("interfaceaction") === 0)
		{
			exTracer.addInterfaceVariables(msg);
		}
	}

    /**
     * Send a message by forwarding it through the {{@link #controller}.
     * @param {CTATMessage} newMessage
     * @param {boolean} endOfTx true if this is the last message in the transaction
     * @return {undefined}
     */
	function sendMessage(newMessage, endOfTx)
	{

		if (suppressLogging !== null || typeof(suppressLogging) !== 'undefined')
		{
			newMessage.suppressLogging(suppressLogging);
		}

		//request is always an HTTPMessageObject in our case
		//we call sendToInterface method from handlers.js
		sendToInterface(newMessage, endOfTx);
	}

	/**
	 * Update the summary counts for this problem according to the results of the 
	 * current transaction
	 * @param {CTATProblemSummary} ps
	 * @param {CTATMessage} assocRulesResp currently must be AssociatedRules msg 
	 */
	function updateProblemSummary(ps, assocRulesResp)
	{
		if(ps === null || typeof(ps) === 'undefined' || assocRulesResp === null || typeof(assocRulesResp) === 'undefined')
		{
			return;
		}

		//Suppressed student feedback code goes here

		var eventMsgTank = null;

		//mimics the get method given a key
		messageTank.forEach(function(messg)
		{
			if(messg["msg"] === assocRulesResp)
			{
				eventMsgTank = messg["evt"];
			}
		});

		var stepIDobj = assocRulesResp.getProperty(CTATTutorMessageBuilder.STEP_ID);

		if(stepIDobj === null || typeof(stepIDobj) === 'undefined')
		{
			return;
		}

		var stepID = stepIDobj.toString(); //stepIDobj should be a string anyways

		var indicatorObj = assocRulesResp.getProperty(CTATTutorMessageBuilder.INDICATOR);

		if(evt !== null && typeof(evt) !== 'undefined' && evt.isSolverResult())
		{
			//stepID = evt.getSolverStepID(stepID); // We will not implement EquationSolver in JS
			
			indicatorObj = evt.getResult();
		}

		// The completion status could change to 'complete' when the student presses
    	// Done with feedback suppressed. If the student then replies "no" to the
    	// ConfirmDone prompt, then we'll reset the completion status to 'incomplete'
    	// with the very next request. 
		var cv = CTATMsgType.CompletionValue[0]; //0 is incomplete, 1 is complete
		
		ps.setCompletionStatus(cv, confirmDone);

		var correct = [false]; //array of booleans with only one element
		//correct.length = 1;

		var doneStep = CTATTutorMessageBuilder.isDoneStep(assocRulesResp, correct);

		if(doneStep)
		{
			cv = (correct[0] ? CTATMsgType.CompletionValue[1] : CTATMsgType.CompletionValue[0]);
			
			//Suppress Student Feedback goes here

			ps.setCompletionStatus(cv, true);

		}

		if(cv === CTATMsgType.CompletionValue[1])
		{
			ps.stopTimer(); // sewall 2012/12/04: stop timer here: could finish on tutor-performed step
		}
		else
		{
			ps.restartTimer();
		}

		var actor = assocRulesResp.getProeprty(CTATMatcher.ACTOR);

		if(actor !== null && typeof(actor) !== 'undefined' && actor.toString().toLowerCase().indexOf("t") === 0)
		{
			return; // sewall 2012/12/04: don't let tutor-performed steps affect the problem summary
		}

		if(CTATTutorMessageBuilder.isHint(indicatorObj))
		{
			ps.addHint(stepID);
		}
		else if(CTATTutorMessageBuilder.isCorrect(indicatorObj))
		{
			ps.addError(stepID);
		}
		else if(indicatorObj !== null && typeof(indicatorObj) !== 'undefined')
		{
			ps.addCorrect(stepID);
		}
	}


/***************************** PRIVILEDGED METHODS *****************************************************/
	
    /**
     * Create a CTATMsgType.INTERFACE_ACTION message for a tutor-performed step and
     * add it to the tank.
     * @param {array} selection 
     * @param {array} action
     * @param {array} input
     * @param {String} subtype
     * @return {String} new message's transaction_id
     */
	this.enqueueToolActionToStudent = function(selection, action, input, subtype)
	{
		return enqueueMessageToStudent(CTATMsgType.INTERFACE_ACTION, selection, action, input, subtype);
	};

    /**
     * Add a new entry to the messageTank with a (possibly null) CTATExampleTracerEvent instance.
     * @param {CTATMessage} newMessage
     * @param {CTATExampleTracerEvent} givenEvent example tracer result describing the creation of this message
     * @return {undefined}
     */
	this.addToMessageTank = function(newMessage, givenEvent)
	{
		if(newMessage === null || typeof(newMessage) === 'undefined')
		{
			return;
		}

		if(tankEmpty)
		{
			messageTank = []; //array with pairs {msg: CTATMessage, evt: CTATETEvent} object
		}

		//creating and adding new pair
		var newPair = {};
		newPair["msg"] = newMessage;
		newPair["evt"] = givenEvent;
		messageTank.push(newPair);

		tankEmpty = false;
	};

	/**
	 * Send all the messages in the tank to the student interface.
	 * @param {CTATProblemSummary} ps if not null, problem summary to update
	 * @param {boolean} endOfTransaction set to false if this is not the end of the transaction
	 * @return {undefined}
	 */
	this.flushMessageTank = function(ps, endOfTransaction)
	{
		if(tankEmpty)
		{
			return;
		}

		consolidateMessageTank(ps);

		messageTank.sort(function(o1, o2){

			var m1 = o1["msg"].getMessageType();
			var m2 = o2["msg"].getMessageType();

			if(msgTypesToDefer.has(m1))
			{
				return (msgTypesToDefer.has(m2) ? 0 : 1); // o2 <= o1
			}

			if(msgTypesToDefer.has(m2))
			{
				return (msgTypesToDefer.has(m1) ? 0 : -1);  // o1 <= o2
			}

			return 0;
		});

		//Leaving out suppressed student feedback
		//Tgether with the big loop that decides whether to SHOW_ALL_FEEDBACK, DELAY_FEEDBACK, HIDE_ALL_FEEDBACK

		// send group of msgs
		var messg = null;

		for(var i = 0; i < messageTank.length; i++)
		{
			messg = messageTank[i]["msg"];

			if(transaction_id === null || typeof(transaction_id) === 'undefined')
			{
				messg.setTransactionId(transaction_id);

			}

			var setEndOfTx = (endOfTransaction && (i >= messageTank.length - 1)); //of type boolean
			messg.setProperty(CTATMessageTank.END_OF_TRANSACTION, setEndOfTx.toString());
			processInterfaceVariables(messg);
			sendMessage(messg, setEndOfTx);
			
		}

		transaction_id = null; //avoid dupl transaction_id in confirmDone
		tankEmpty = true;
	};

	/**
	 * Send contents of delayedFeedback list and clear the list.
	 * @return {undefined}
	 */
	this.flushDelayedFeedback = function()
	{
		if(delayedFeedback === null || typeof(delayedFeedback) === 'undefined')
		{
			return;
		}

		for(var mo in delayedFeedback) //iterate through the properties
		{
			if(delayedFeedback.hasOwnProperty(mo))
    		{
		    	processInterfaceVariables(mo);
		    	sendMessage(mo, false); // false: so far, these are always sent in an enclosing transaction
    		}
		}

		//clearing the map
		for (var member in delayedFeedback) 
		{
			delete delayedFeedback[member];
		}

	};

/****************************** PUBLIC METHODS ****************************************************/


/****************************** CONSTRUCTOR CALLS ****************************************************/

};

/****************************** CONSTANTS ****************************************************/

	/** 
	 * 
	 * @param {String}
	 */
	Object.defineProperty(CTATMessageTank, "END_OF_TRANSACTION", {enumerable: false, configurable: false, writable: false, value: "end_of_transaction"});


/**************************** SETTING UP INHERITANCE ******************************************************/

CTATMessageTank.prototype = Object.create(CTATBase.prototype);
CTATMessageTank.prototype.constructor = CTATMessageTank;

if(typeof module !== 'undefined')
{
	module.exports = CTATMessageTank;
}