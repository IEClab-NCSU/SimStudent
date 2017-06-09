/**
 * Copyright 2016 Carnegie Mellon University
 */
goog.provide('CTATTransactionListener');

goog.require('CTAT.ToolTutor');
goog.require('CTATBase');
goog.require('CTATConfiguration');
goog.require('CTATGuid');
goog.require('CTATJSON');
goog.require('CTATLogMessageBuilder');
goog.require('CTATMessage');
goog.require('CTATSAI');
goog.require('CTATSkillSet');
goog.require('CTATXML');

/**
 * Assemble transactions and pass them to the given Worker script.
 * @param {string} transactionURL destination URL for the mail-worker
 */
CTATTransactionListener = function(transactionURL)
{
    CTATBase.call(this, "CTATTransactionListener", "");

    /** Stable this. */
    var pointer = this;

    /** URL for Worker that forwards transactions. */
    var process_transactions_url = transactionURL;

    /** Mailer proxy: contains workers for postMessage(). */
    var mailerProxy = null;

    /** Whether we are still initializing, before student actions begin. */
    var initializing = true;

    /** Problem-level information to copy into each transaction. */
    var problemInfo = null;

    /** Partial transactions awaiting completion. */
    var incompleteTransactions = {};

    /** For debugging: current size of incompleteTransactions. */
    var nIncompleteTransactions = 0;

    /** For parsing input messages. */
    var messageParser = (CTATConfig.parserType_is_XML() ? new CTATXML () : new CTATJSON ());

    /**
     * Remove and return any property of incompleteTransactions identified by the given transaction id.
     * @param {string} txId
     * @return {object} incompleteTransactions[txId]; null if not defined
     */
    function removeIncompleteTransaction(txId)
    {
        var result = incompleteTransactions[txId];
        if(result)
        {
            delete incompleteTransactions[txId];
            --nIncompleteTransactions;
            pointer.ctatdebug("CTATTransactionListener.removeIncompleteTransaction("+txId+") count now "+nIncompleteTransactions);
            return result;
        }
        return null;
    };

    /**
     * Store a new value in incompleteTransactions identified by the given transaction id.
     * @param {string} txId property identifier
     * @param {object} tx value to store
     * @return null indicates tx stored, not returned; see makeOrCompleteTransaction()
     */
    function storeIncompleteTransaction(txId, tx)
    {
        var oldTx = incompleteTransactions[txId];
        if(oldTx)  // shouldn't happen
        {
            console.log("Warning: CTATTransactionListener.storeIncompleteTransaction() found tx with ID "+txId, oldTx);
            --nIncompleteTransactions;  // adjust count since about to increment
        }
        incompleteTransactions[txId] = tx;
        ++nIncompleteTransactions;
        pointer.ctatdebug("CTATTransactionListener.storeIncompleteTransaction("+txId+") count now "+nIncompleteTransactions);
        
        return null;  // must return null
    };

    /**
     * Tell whether a given event type has or is a tutor response.
     * @param {string} eventType
     * @return {boolean} true if event type has or is a tutor response
     */
    function hasTutorResponse(eventType)
    {
        return (eventType != "UntutoredAction");
    };
    
    /**
     * Create a mailer Worker. Sets mailerProxy.
     */
    function makeMailer()
    {
	if(typeof(TransactionMailerUsers) != "undefined" && typeof(TransactionMailerUsers.create) == "function")  // users in this frame, if any
	{
	    try
	    {
		var result = TransactionMailerUsers.create("Assets", process_transactions_url, CTATConfiguration.get("process_detectors_url"), CTATConfiguration.get("authenticity_token"));
		return mailerProxy = result;
            } catch(error) {
		console.trace("Error creating transaction mailer: "+error);
		return mailerProxy = null;
            }
	}
    };

    /**
     * @return mailerProxy, used for posting transactions to the mailer
     */
    this.getMailer = function()
    {
        return mailerProxy;
    };

    /**
     * Function to receive requests from detectors.
     */
    this.onmessage = function(e)
    {
	pointer.ctatdebug("CTATTransactionListener.onmessage("+e.toString()+")");
	var sai = (e.data && e.data.length >= 3 ? new CTATSAI(e.data[0], e.data[1], e.data[2]) : null);
	if(sai)
	{
	    CTATCommShell.shell.processComponentAction(sai, false, true, null, "ATTEMPT", "Detector");
	}
    };

    /*
     * Constructor execution: find the mailer in the parent if available; else create one. Also set up the MailerUsers.
     */
    if(!mailerProxy)  // didn't find one or create in parent
    {
        mailerProxy = makeMailer();
    }

    /**
     * @return {boolean} true if Worker contact established
     */
    this.isReady = function()
    {
        return pointer.mailerProxy != null;
    };

    /**
     * @return {string} for debugging
     */
    this.toString = function()
    {
        return 'transactions sent to ['+pointer.process_transactions_url+':]';
    };

    /**
     * @param {string} eventType message type or other event name
     * @param {object<CTATMessage>} or {object<Element>} or {string} aMessage message sent or received; can be null
     * @param {string} actor
     */
    this.processCommShellEvent = function(eventType, aMessage, actor)
    {
        console.trace("CTATTransactionListener.processCommShellEvent("+eventType+", "+aMessage+", "+actor+")");//FIXME pointer.ctatdebug
        try
        {
            var tx = null;
            switch(eventType)
            {
            case 'InterfaceAction':
            case 'AssociatedRules':
            case 'UntutoredAction':
            case 'NextPressed':
            case 'PreviousPressed':
                var msg = aMessage;
                if(aMessage && aMessage.constructor != CTATMessage)
                {
                    msg = (aMessage.constructor != Element ?  messageParser.parse(msg) : msg);
                    msg = new CTATMessage (msg);
                }
                tx = makeOrCompleteTransaction(eventType, msg, actor);
                break;
            case 'StartProblem':
                pointer.problemInfo = createContextMessage();
                break;
            case 'ProblemRestoreEnd':
                pointer.initializing = false;
                break;
            default:
            }

            if(tx && !pointer.initializing)
            {
                mailerProxy.sendTransaction(tx);  // post to listener in other thread
            }
        }
        catch(error)  // don't let errors here hold up any other processing
        {
            console.log("Error in CTATTransactionListener.processCommShellEvent("+eventType+"): "+error, error.stack);
        }
    };

    /**
     * Copy the problemInfo properties into a new object, add tool and tutor data.
     * @param {string} eventType message type
     * @param {object} msg message with new data
     * @param {string} actor "tutor" if tutor-performed
     * @return {object} transaction object
     */
    function makeOrCompleteTransaction(eventType, msg, actor)
    {
        pointer.ctatdebug("CTATTransactionListener.makeOrCompleteTransaction("+eventType+") pointer "+pointer+", problemInfo "+problemInfo+", pointer.problemInfo "+(pointer?pointer.problemInfo:null));
        var result = null;
        var txId = null;
        var isComplete = false;
        if(!(txId = getTransactionId(msg)))
        {
            return null;
        }
        if((isComplete = !hasTutorResponse(eventType)) || !((result = removeIncompleteTransaction(txId)) && (isComplete = true)))
        {
            result = { meta: pointer.problemInfo.meta, context: pointer.problemInfo.context };  // new tx
        }
        switch(eventType)
        {
        case 'AssociatedRules':
        case 'NextPressed':
        case 'PreviousPressed':
            addTutorInfo(eventType, msg, actor, result);
            break;
        default:
            result.meta.date_time = formatTimeStamp (new Date());  // timestamp is for tool info
            addToolInfo(eventType, msg, actor, result);
            break;
        }
        return (isComplete ? result : storeIncompleteTransaction(txId, result));
    };

    /**
     * Extract the transaction identifier from the given CTATMessage instance.
     * @param {CTATMessage} msg 
     * @return {string} transaction_id content; null if not found
     */
    function getTransactionId(msg)
    {
        return (msg && typeof(msg.getTransactionID) == 'function' ? msg.getTransactionID() : null);
    };

    /**
     * Formats Date objects into Datashop's prefered format, then adds UTC time zone.
     * @param   stamp   A Date object.
     * @return  {string} in the proper format, with UTC time
     *
     * http://www.w3schools.com/jsref/jsref_obj_date.asp
     */
    function formatTimeStamp (stamp)
    {
        pointer.ctatdebug ("formatTimeStamp (" + stamp + ")");

        var s=sprintf("%04d-%02d-%02d %02d:%02d:%02d.%03d UTC",
                stamp.getUTCFullYear(),
                stamp.getUTCMonth()+1,
                stamp.getUTCDate(),
                stamp.getUTCHours(),
                stamp.getUTCMinutes(),
                stamp.getUTCSeconds(),
                stamp.getUTCMilliseconds());

        return s;
    };

    /**
     * @param {Date} timeStamp
     * @return meta object
     */
    function makeMetaElement (timeStamp)
    {
        pointer.ctatdebug ("TX.makeMetaElement ()");

        var meta={};

        meta.date_time=formatTimeStamp(timeStamp);
        meta.session_id=CTATConfiguration.get('session_id');
        meta.user_guid=CTATConfiguration.get('user_guid');

        return meta;
    };

    /**
     * Requires that CTATLogMessageBuilder.contextGUID be set already.
     * @param {object} ctxt existing context object
     * @return ctxt, with added properties
     */
    function addContextMessageID(ctxt)
    {
        var id = null;
        if(CTATLogMessageBuilder && CTATLogMessageBuilder.commLogMessageBuilder)
        {
            id = CTATLogMessageBuilder.commLogMessageBuilder.getContextName();
        }
        if(!id)
        {
            id = CTATLogMessageBuilder.contextGUID;
        }
        if(!id)
        {
            throw new Error("Context message ID not yet defined");
        }
        ctxt.context_message_id=id;
        return ctxt;
    };

    /**
     * Add the fields from a multiple-instance context parameter whose individual
     * instances are pairs of <i>prefix</i>_name and <i>prefix</i>_type values.
     * Use this for dataset_level_*[1-9] parameters.
     * @param {object} ctxt context object to augment
     * @param {string} prefix the base field name
     * @return {object} ctxt, now with more properties
     */
    function addMultilevelProperties(ctxt, prefix)
    {
        var levelType = null, levelName = null;
        for(var i = 1; true; ++i)  // will break when no more levels
        {
            var t = prefix+'_type'+i, n=prefix+'_name'+i;
            levelType = CTATConfiguration.get(t);
            levelName = CTATConfiguration.get(n);
            if(!levelType || !levelName)
            {
                break;
            }
            ctxt[t] = levelType;
            ctxt[n] = levelName;
        }
        if(i == 1) // failed to find any numbered properties
        {
            levelType = CTATConfiguration.get(prefix+'_type');
            levelName = CTATConfiguration.get(prefix+'_name');
            ctxt[t] = (levelType ? levelType : "");
            ctxt[n] = (levelName ? levelName : "");
        }
        return ctxt;
    };

    /**
     * Create the dataset properties of the context property of a transaction.
     * @param {object} ctxt object whose dataset properties we should set
     * @return {object} ctxt, now augmented
     */
    function addDatasetProperties(ctxt)
    {
        var dataset = CTATConfiguration.get('dataset_name');
        ctxt.dataset_name = (dataset ? dataset : "");

        addMultilevelProperties(ctxt, 'dataset_level');

        prop = CTATConfiguration.get('problem_name');
        ctxt.problem_name = (prop ? prop : "");
        prop = CTATConfiguration.get('problem_context');
        ctxt.problem_context = (prop ? prop : "");
        prop = CTATConfiguration.get('problem_tutorflag');
        ctxt.problem_tutorFlag = (prop ? prop : "");

        addMultilevelProperties(ctxt, "study_condition");

        return ctxt;
    };

    /**
     * Add the class_ properties to the given context object.
     * @param {object} ctxt context object
     * @return ctxt, with added properties
     */
    function addClassProperties(ctxt)
    {
        var prop = null;

        prop = CTATConfiguration.get('class_name');
        ctxt.class_name = (prop ? prop : "");

        prop = CTATConfiguration.get('school_name');
        ctxt.class_school = (prop ? prop : "");

        prop = CTATConfiguration.get('period_name');
        ctxt.class_period_name = (prop ? prop : "");

        prop = CTATConfiguration.get('class_description');
        ctxt.class_description = (prop ? prop : "");

        prop = CTATConfiguration.get('instructor_name');
        ctxt.class_instructor_name = (prop ? prop : "");

        return ctxt;
    };

    /**
     * @return {object} with meta and context properties initialized
     */
    function createContextMessage ()
    {
        pointer.ctatdebug("CTATTransactionListener.createContextMessage()");

        var result = {}

        var now=new Date();
        result.meta = makeMetaElement(now);

        result.context = {};
        addContextMessageID(result.context);
        addClassProperties(result.context);
        addDatasetProperties(result.context);

        return result;
    };

    /**
     * Add the tool-generated properties to the given transaction. Throws error if no transaction_id.
     * @param {string} eventType
     * @param {object} msg
     * @param {string} actor "tutor" if tutor-performed
     * @param {object} tx transaction to augment: sets the tool_data property and some others
     */
    function addToolInfo(eventType, msg, actor, tx)
    {
        if(!(tx.transaction_id = getTransactionId(msg)))
        {
            throw new Error("Message has no transaction_id: eventType "+eventType+", msg\n  "+msg);
        }
        tx.semantic_event = (eventType == "UntutoredAction" ? "UNTUTORED-ACTION" : (isHint(eventType, msg) ? "HINT-REQUEST-HINTS" : "ATTEMPT-RESULT"));
        tx.actor = (actor ? actor : "student");
    
        if(tx.tool_data)
        {
            console.log("Warning: CTATTransactionListener.addToolInfo("+eventType+") tx has tool_data with time "+tx.tool_data.tool_event_time);
        }
        tx.tool_data = {};
        tx.tool_data.tool_event_time = formatTimeStamp(new Date());

        tx.tool_data.selection = getProperty(msg, "Selection");
        tx.tool_data.action = getProperty(msg, "Action");
        tx.tool_data.input = getProperty(msg, "Input");
    };

    /**
     * Add the tutor-generated properties to the given transaction.
     * @param {string} eventType
     * @param {object} msg
     * @param {string} actor not used
     * @param {object} tx transaction to augment: sets the tutor_data property
     */
    function addTutorInfo(eventType, msg, actor, tx)
    {
        if(tx.tutor_data)
        {
            console.log("Warning: CTATTransactionListener.addTutorInfo("+eventType+") tx has tutor_data with time "+tx.tutor_data.tutor_event_time);
        }
        tx.tutor_data = {};
        tx.tutor_data.tutor_event_time = formatTimeStamp(new Date());

        tx.tutor_data.selection = getProperty(msg, "Selection");
        tx.tutor_data.action = getProperty(msg, "Action");
        tx.tutor_data.input = getProperty(msg, "Input");

        tx.tutor_data.action_evaluation = msg.getIndicator();
        if(tx.tutor_data.action_evaluation.toLowerCase().indexOf("hint") >= 0)
        {
            tx.tutor_data.current_hint_number = getProperty(msg, "CurrentHintNumber");
            tx.tutor_data.total_hints_available = getProperty(msg, "TotalHintsAvailable");
        }
        tx.tutor_data.tutor_advice = getProperty(msg, "TutorAdvice");

        tx.tutor_data.skills = createSkills(msg);
        // skills, et al.
    };

    /**
     * Extract updated skill probabilities from a message and return them as a JavaScript array.
     * @param {CTATMessage} msg
     * @returun {Array<object>} each element a formatted skill; can be empty
     */
    function createSkills(msg)
    {
        var result = [];
	var tracerSS = (CTAT.ToolTutor.tutor && CTAT.ToolTutor.tutor.getProblemSummary() ? CTAT.ToolTutor.tutor.getProblemSummary().getSkills() : null);
	var skObj = msg.getSkillsObject();
	var skList = skObj.getSkillSet();
	for(var i=0; i<skList.length; ++i)
	{
	    var sk = skList[i];
	    var ssSk = (CTATSkillSet.skills ? CTATSkillSet.skills.getSkill(sk.getSkillName()) : null);
	    if(ssSk)
	    {
		var skElt = {};
		skElt.name = ssSk.getSkillName();
		skElt.category = ssSk.getCategory();
		var fullName = (skElt.category == null || skElt.category.trim() == "" ? skElt.name : skElt.name+" "+skElt.category);
		var tracerSk = (tracerSS ? tracerSS.getSkill(fullName) : null);
		var p;
		if(tracerSk)
		{
		    skElt.pKnown = (isNaN(p = tracerSk.getPKnown()) ? "" : p);
		    skElt.pGuess = (isNaN(p = tracerSk.getPGuess()) ? "" : p);
		    skElt.pSlip = (isNaN(p = tracerSk.getPSlip()) ? "" : p);
		    skElt.pLearn = (isNaN(p = tracerSk.getPLearn()) ? "" : p);
		    skElt.history = (isNaN(p = tracerSk.getHistory()) ? "" : p);
		    skElt.opportunityCount = (isNaN(p = tracerSk.getOpportunityCount()) ? "" : p);
		}
		else
		{
		    skElt.pKnown = (isNaN(p = ssSk.getPKnown()) ? "" : p);
		    skElt.pGuess = (isNaN(p = ssSk.getPGuess()) ? "" : p);
		    skElt.pSlip = (isNaN(p = ssSk.getPSlip()) ? "" : p);
		    skElt.pLearn = (isNaN(p = ssSk.getPLearn()) ? "" : p);
		}
		result.push(skElt);
	    }
	}
        return result;
    };

    /**
     * Tell whether a message is hint-related.
     * @param {string} eventType message type
     * @param {object} msg message content
     * @return {boolean} true if any reference to hints here
     */
    function isHint(eventType, msg)
    {
        if(eventType && typeof(eventType.toLowerCase) == "function" && eventType.toLowerCase().indexOf("hint") >= 0)
        {
            return true;
        }
        if(!msg)
        {
            return false;
        }
        if(msg.getMessageType().toLowerCase().indexOf("hint") >= 0)
        {
            return true;
        }
        var selection = msg.getSelection().toLowerCase();
        var action = msg.getAction().toLowerCase();
        return action.indexOf("buttonpress") >= 0 && (selection.indexOf("hint") >= 0 || selection.indexOf("help") >= 0);
    };

    /**
     * Retrieve a property value from a message. If the value is an array of &lt;value&gt; elements,
     * as with selection-action-input properties, concatenates the elements without delimiters.
     * @param {string} msg either an XML (or JSON?) string
     * @param {string} propName element name Selection, Action or Input
     * @return {string} value of the named element; "" if no SAI elements found
     */
    function getProperty(msg, propName)
    {
        var withValues = msg.getProperty(propName);
        var valueElts = (withValues ? withValues.split(CTATTransactionListener.valueDelimiter) : [""]);
        var result = "";
        for(var i = 0; i < valueElts.length; ++i)
        {
            result = result+valueElts[i];
        }
        pointer.ctatdebug("CTATTransactionListener.getProperty("+propName+") withValues "+withValues+", valueElts "+valueElts+", result "+result);
        return result;
    };

    ctatdebug("**<end of CTATTransactionListener constructor, process_transactions_url "+process_transactions_url+", mailerProxy "+mailerProxy);
};

/** Parameter name for the consumer script to spawn. */
Object.defineProperty(CTATTransactionListener, "scriptParam", {enumerable: false, configurable: false, writable: false, value: "process_transactions_url"});

/** XML tag delimiting individual elements of array-valued properties in messages. */
Object.defineProperty(CTATTransactionListener, "valueDelimiter", {enumerable: false, configurable: false, writable: false, value: /<\/?value>/ });

CTATTransactionListener.prototype = Object.create(CTATBase.prototype);
CTATTransactionListener.prototype.constructor = CTATTransactionListener;

if(typeof module !== 'undefined')
{
    module.exports = CTATTransactionListener;
}


/**
 * Factory to create instance for a given scriptURL.
 * @param {string} scriptURL web address of listener script
 * @return instance of this class; null if scriptURL null or empty
 */
CTATTransactionListener.create = function(scriptURL)
{
    var result = null;
    if(!scriptURL)
    {
        return null;
    }
    try {
        result = new CTATTransactionListener(scriptURL);
        if(!result.getMailer())
        {
            throw new Error("Transaction listener postMessage() not defined.");
        }
    } catch(error) {
        console.trace("Error creating or connecting to transaction listener", error);
        result = null;
    }
    return result;
};
