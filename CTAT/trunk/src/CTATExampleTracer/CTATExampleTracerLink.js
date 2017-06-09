/* This object represents /n CTATExampleTracerLink */

goog.provide('CTATExampleTracerLink');
goog.require('CTATBase');
goog.require('CTATExampleTracerException');
goog.require('CTATMatcher');
goog.require('CTATMsgType');
goog.require('CTATFormulaParser');
goog.require('CTATVariableTable');
goog.require('CTATExampleTracerLinkVisualData');//

//goog.require('CTATVariableTable');//
//goog.require('CTATExactMatcher');//
//goog.require('CTATSAI');//
//goog.require('CTATExampleTracerEvent');//

/* LastModify: DhruvChand 08/26*/

/**
 * @param {integer} givenUniqueID
 * @param {integer} givenPrevNode
 * @param {integer} givenNextNode
 */
CTATExampleTracerLink = function(givenUniqueID, givenPrevNode, givenNextNode)
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

	//calling the constructor of the super class
	CTATBase.call(this, "CTATExampleTracerLink", "(" + givenPrevNode + "-" + givenNextNode + ")");

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/


/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	/**
	 * @type {integer}
	 */
	var uniqueID = (typeof(givenUniqueID) === 'undefined' || givenUniqueID === null ? null : givenUniqueID);

	/**
	 * @type {integer}
	 */
	var depth = null;

	/**
	 * @type {integer}
	 */
	var prevNode = (typeof(givenPrevNode) === 'undefined' || givenPrevNode === null ? null : givenPrevNode);

	/**
	 * @type {integer}
	 */
	var nextNode = (typeof(givenNextNode) === 'undefined' || givenNextNode === null ? null : givenNextNode);

	/**
	 * @type {CTATMatcher}
	 */
	var matcher = null;

	//this will not be part of CTATExampleTracerLink anymore
	//instead it will be part of CTATExampleTracerEvent
	//var interpolateSelection = null; //of type string
	//var interpolateAction = null; //of type string
	//var interpolateInput = null; //of type string

	/**
	 * Maximum number of times this link may be traced. Value 0 prevents traversal.
	 * @type {integer}
	 */
	var maxTraversals = 1;

	/**
	 * Minimum number of times this link may be traced. Value 0 means traversal is optional.
	 * @type {integer}
	 */
	var minTraversals = 1;

	/**
	 * @type {string}
	 */
	var actionType = "CORRECT_ACTION";

	/**
	 * @type {array of strings}
	 */
	var hints = [];

	/**
	 * Note: The first time it is assigned a value,
	 * it is set to true or false.
	 * @type {boolean}
	 */
	var isPreferredLink = false;

	/**
	 * @type {string}
	 */
	var successMsg = null;

	/**
	 * @type {string}
	 */
	var buggyMsg = "";

	/**
	 * For storing variables in minTraversals.
	 * @type {string}
	 */
	var minTraversalsStr = minTraversals.toString();

	/**
	 * For storing variables in maxTraversals.
	 * @type {string}
	 */
    var maxTraversalsStr = maxTraversals.toString();

	/**
	 * @type {CTATExampleTracerLinkVisualData}
	 */
	var visuals = null;

	/**
	 * List of associated rules or skills. Used outside of author's UI.
	 * @type {array of strings}
	 */
	var skillNames = null;

	/**
     * Make the object available to private methods
     */
	var that = this;

/***************************** PRIVATE METHODS *****************************************************/

/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * @return {integer}
	 */
	this.getUniqueID = function ()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in getUniqueID");
		return uniqueID;
	};

	/**
	 * @return {CTATMatcher} return CTATMatcher object (or one of its child classes)
	 */
	this.getMatcher = function ()
	{
		that.ctatdebug("CTATExampleTracerLink --> in getMatcher");
		//Modified from the original implementatoion so that
		//we can set the sensitivty when we create the matcher
		//rather than taking it form the problem model
		return matcher;
	};

	/**
	 * @return {boolean} actionType == CTATExampleTracerLink.CORRECT_ACTION
	 */
	this.isCorrect = function()
	{
		return actionType == CTATExampleTracerLink.CORRECT_ACTION;
	};

	/**
	 * Checks whether the link's sai matches given sai
	 * @param {CTATSAI} sai input to matcher
	 * @param {string} actor input to matcher
	 * @param {CTATVariableTable} vt interp specific vt, if null refers to problemmodel(best interp)'s vt
	 * @return {boolean} true or false depending whether sai matches or not
	 */
	this.matchesSAI = function (sai, actor, vt)
	{
		var m = that.getMatcher();
		that.ctatdebug("CTATExampleTracerLink --> in matchesSAI(); matcher " + m + ", typeof " + typeof(m) + ", sai " + sai);

		//in the case of CTATExactMatcher, the vt will be ignored
		var mResult = m.match(sai.getSelectionArray(), sai.getActionArray(), sai.getInputArray(), actor, vt);

		that.ctatdebug("CTATExampleTracerLink --> out of matchesSAI: result = " + mResult);

		return mResult;
	};


	/**
	 * Tries to match just the selection and actor elements of the SAI.
     * Even if matches ok, rejects if hint texts are desired, but the link has no hints.
     * To make this check fast, we don't evaluate hints, only check whether any hint
     * is defined.
     * @param {CTATSAI} sai
	 * @param {string} actor input to matcher
     * @param {CTATExampleTracerEvent} result
     * @param {CTATVariableTable} vt
     * @return {boolean} true if match and has hints if wanted
	 */
	this.matchesSAIforHint = function (sai, actor, result, vt)
	{
		that.ctatdebug("CTATExampleTracerLink --> in matchesSAIforHint("+sai+", "+actor+") selectionArray "+sai.getSelectionArray());

		if(that.getMatcher().matchForHint(sai.getSelectionArray(), sai.getActionArray(), actor, vt) === false)
		{
			that.ctatdebug("CTATExampleTracerLink --> matchesSAIforHint() returning false because no match");
			return false;
		}

		if(result !== null && typeof(result) !== 'undefined' && result.getWantReportableHints() === true)
		{
			var nStaticHints = (that.getAllHints() === null || typeof(that.getAllHints()) === 'undefined' ? 0 : that.getAllHints().length);

			if(nStaticHints < 1)
			{
				that.ctatdebug("CTATExampleTracerLink --> matchesSAIforHint() returning false nStaticHints " + nStaticHints + "< 1");
				return false; // caller wants hints, but this link doesn't have any
			}
		}

		that.ctatdebug("CTATExampleTracerLink --> matchesSAIforHint() returning true");
		return true;
	};


	/**
	 * @return String
	 */
	this.getDepth = function()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in getDepth");

		return depth;
	};

	/**
	 * @return String
	 */
	this.getBuggyMsg = function()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in getBuggyMsg");

		return buggyMsg;
	};

	/**
	 * @return integer
	 */
	this.getSuccessMsg = function()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in getSuccessMsg");

		return successMsg;
	};

	/**
	 * @return object of type string: returns the type of the link
	 */
	this.getType = function ()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in getType");

		return actionType;
	};

	/**
	 * @return integer: the id of the previous node
	 */
	this.getPrevNode = function()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in getPrevNode");

		return prevNode;
	};

	/**
	 * @return integer: the id of the next node
	 */
	this.getNextNode = function()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in getNextNode");

		return nextNode;
	};

		/**
	 * Sets the prevNode with the given id
	 * @param prevNodeId of type integer
	 * @return undefined
	 */
	this.setPrevNode = function(prevNodeId)
	{
		//that.ctatdebug("CTATExampleTracerLink --> in setPrevNode");
		prevNode = prevNodeId;
		//that.ctatdebug("CTATExampleTracerLink --> out of setPrevNode");
	};

	/**
	 * Sets the nextNode with the given id
	 * @param nextNodeId of type integer
	 * @return undefined
	 */
	this.setNextNode = function(nextNodeId)
	{
		//that.ctatdebug("CTATExampleTracerLink --> in setNextNode");
		nextNode = nextNodeId;
		//that.ctatdebug("CTATExampleTracerLink --> out of setNextNode");
	};


	/**
	 * @return string
	 */
	this.toString = function()
	{
		var sb = "[link ";
		sb += that.getUniqueID();
		sb += " (" + that.getPrevNode() + "-" + that.getNextNode();
		sb += ") " + that.getType();

		//No action label in JS implementation

		sb += "]";

		return sb;
	};

	/**
	 * Return an SAI appropriate for a tutor_message response.
	 * @param {CTATSAI} studentSAI
	 * @param {CTATVariableTable} vt for matchConcatenation
	 * @return {CTATSAI} SAI matching some or all of studentSAI
	 */
	this.getTutorSAI = function(studentSAI, vt)
	{
		return that.getMatcher().getTutorSAI(studentSAI, vt, that.getActionType());
	};

	/**
	 * Evaluate the input parameter with a given SAI and variable table.
	 * @param {CTATSAI} givenSAI sai for evaluation
	 * @param {CTATVariableTable} vt for evaluation
	 * @return {string} input
	 */
	this.getEvaluatedInput = function(givenSAI, vt)
	{
		return that.getMatcher().getEvaluatedInput(givenSAI, vt);
	};

	/**
	 * @param givenDepth of type integer
	 * @return undefined
	 */
	this.setDepth = function(givenDepth)
	{
		depth = givenDepth;
	};

/********************** CODE BELONGS ORIBINALLY TO EDGEDATA.JAVA ******************************/


	/**
	 * This method returns the operative value for the maximum traversals
     * permitted.
	 * @return integer
	 */
	this.getMaxTraversals = function ()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in getMaxTraversals");

		return maxTraversals;
	};


	/**
	 * Tell whether this link represents the student's assertion that work on
     * the problem is finished.
	 * @return boolean: true if the first element of getSelection() is "Done"
     * and the corresponding element of getAction() is
	 */
	this.isDone = function ()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in isDone");

		var s = String(that.getDefaultSAI().getSelection()).toLowerCase();
		var a = String(that.getDefaultSAI().getAction()).toLowerCase();

		var doneStep = (CTATMsgType.DONE.toLowerCase() == s && CTATMsgType.BUTTON_PRESSED.toLowerCase() == a);

		that.ctatdebug("CTATExampleTracerLink.isDone() returns "+doneStep);
		return doneStep;
	};

	/**
	 * The method will calculate the interpolated hints locally,
	 * and return an array of hints or null.
	 * We got rid of the original interpolatedHints global variables
	 * as we expect the CTATExampleTracerLink object to be invariable.
	 * In addition we get rid of the interpolatedSelection, interpolatedAction
	 * and interpolatedVariables, therefore we do not call setInterpolatesSAI(..)
	 * function here anymore.
	 * @param vt of type VariableTable
	 * @return empty array if there are no hints, otherwise array of hints
	 */
	this.interpolateHints = function (vt)
	{
		that.ctatdebug("CTATExampleTracerLink --> interpolateHints("+vt+")");

		var interpolatedHints = []; //array of strings

		if(hints === null || typeof(hints) === 'undefined' || hints.length === 0)
		{
			//that.ctatdebug("in interpolateHints first if condition");
			return interpolatedHints;
		}

		//VariableTable: Left out because from the function we call it will never be null
		//In addition we do not use the ProblemModel anymore, that's why this code will not be needed

		if(vt == null) vt = new CTATVariableTable();
		var fp = new CTATFormulaParser(vt);

		hints.forEach(function(hint){
			if(String(hint).trim().length < 1)
				return;
			var sel = that.getDefaultSAI().getSelection();
			var act = that.getDefaultSAI().getAction();
			var inp = CTATVariableTable.standardizeType(that.getDefaultSAI().getInput());
			that.ctatdebug("CTATExampleTracerLink.interpolateHints() calling fp.interpolate("+hint+", "+sel+", "+act+", "+inp+")");
			var interpHint = fp.interpolate(hint, sel, act, inp);
			that.ctatdebug("CTATExampleTracerLink.interpolateHints() calling fp.interpolate() returns "+interpHint);
			interpolatedHints.push(interpHint);
		});

		that.ctatdebug("CTATExampleTracerLink --> out of interpolateHints "+interpolatedHints);

		return interpolatedHints;
	};


	/**
	 * Get the hints that are not the empty string.
	 * @return array of strings
	 */
	this.getHints = function ()
	{
		that.ctatdebug("CTATExampleTracerLink --> in getHints");

		var v = []; //array of strings

		//var interpHints = that.interpolateHints()

		/*if(interpHints === null || typeof(interpHints) === 'undefined')
		{
			that.ctatdebug("in getHints: WE SHOULD NEVER GET HERE!");
			return v;
		}*/

		that.interpolateHints(null).forEach(function(hint)
		{
			that.ctatdebug("CTATExampleTracerLink --> in getHints loop");
			if(hint.toString() !== "".toString())
			{
				v.push(hint);
			}
		});

		that.ctatdebug("CTATExampleTracerLink --> out of getHints");

		return v;
	};

	/**
	 * Get all hints, excluding the empty strings.
	 * @return array of strings that represent the nonEmptyHints
	 */
	this.getAllNonEmptyHints = function()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in getAllNonEmptyHints");

		var nonEmptyHints = []; //array of strings

		hints.forEach(function(hint)
		{
			//JavaScript uses the trim method similar to Java
			if(hint.trim().toString() !== "".toString())
			{
				nonEmptyHints.push(hint);
			}
		});

		//that.ctatdebug("CTATExampleTracerLink --> out of getAllNonEmptyHints");

		return nonEmptyHints;
	};

	/**
	 * Tell whether this link can be traced across, so that the graph state could reach its destination state.
	 * @return {boolean} true if CORRECT_ or FIREABLE_BUGGY_ACTION and maxTraversals > 0
	 */
	this.isTraversable = function()
	{
		if(that.getMaxTraversals() < 1)
			return false;
		if(CTATExampleTracerLink.CORRECT_ACTION.toString().toUpperCase() == that.getActionType().toString().toUpperCase())
			return true;
		if(CTATExampleTracerLink.FIREABLE_BUGGY_ACTION.toString().toUpperCase() == that.getActionType().toString().toUpperCase())
			return true;
		return false;
	};

    /**
     * Tell whether this link should replace the student's input in the tutor response.
     * @return boolean: false if not a CORRECT_ACTION or  FIREABLE_BUGGY_ACTION link;
     * else result of replaceInput()
     */
	this.replaceInput = function()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in replaceInput");

		if(CTATExampleTracerLink.CORRECT_ACTION.toString().toUpperCase() !== that.getActionType().toString().toUpperCase() && CTATExampleTracerLink.FIREABLE_BUGGY_ACTION.toString().toUpperCase() !== that.getActionType().toString().toUpperCase())
		{
			//that.ctatdebug("in if condition replace input of link");
			return false;
		}

		//that.ctatdebug("CTATExampleTracerLink --> out of replaceInput");

		return that.getMatcher().replaceInput();
	};

	/**
	 * @param {CTATSAI}	sai
	 * @param {CTATVariableTable} vt
	 * @param {CTATExampleTracerTracer} tracer
	 * @return {string} result of evaluation, or null
     */
	this.evaluateReplacement = function(sai, vt, tracer)
	{
		//that.ctatdebug("CTATExampleTracerLink --> in evaluateReplacement");

		//NOTE: the original evaluateReplacement on the matcher takes
		//as the last element the ProblemModel
		//we will not have that in this implementation
		return that.getMatcher().evaluateReplacement(sai, vt, tracer);
	};

	/**
	 * @return {string}
	 */
	this.getActor = function()
	{
		return that.getMatcher().getActor();
	};

	/**
	 * @return {CTATSAI} sai
	 */
	this.getDefaultSAI = function()
	{
		that.ctatdebug(that.toString() + ".getSAI(): " + (matcher ? matcher.getDefaultSAI() : "(matcher null)"));
		return matcher.getDefaultSAI();
	};

	/**
	 * @return string
	 */
	this.getActionType = function()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in getActionType");

		return actionType;
	};

    /**
     * This method returns the operative value for the minimum traversals permitted.
     * @return integer: minTraversals a value of 0 means traversal is optional.
     */
	this.getMinTraversals = function()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in getMinTraversals");

		return minTraversals;
	};

	/**
	 * @return {boolean}
	 */
	this.getIsPreferredLink = function()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in getIsPreferredLink");

		return isPreferredLink;
	};

	/**
	 * Get all hints, including the empty strings.
	 * @return array of strings
	 */
	this.getAllHints = function()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in getAllHints");

		return hints;
	};

	/**
	 * @param m of type CTATMatcher
	 * @return undefiend
	 */
	this.setMatcher = function(m)
	{
		//that.ctatdebug("CTATExampleTracerLink --> in setMatcher");

		matcher = m;
		//m.setExternalResources(null, null, null); Jonathan says this is a possible bug and is not needed here

		//that.ctatdebug("CTATExampleTracerLink --> out of setMatcher");
	};


	/**
	 * @param newHint of type string
	 * @return undefined
	 */
	this.addHint = function(newHint)
	{
		//that.ctatdebug("CTATExampleTracerLink --> in addHint");

		if(newHint !== null && typeof(newHint) !== 'undefined')
		{
			if(newHint.toString() !== "".toString())
			{
				hints.push(newHint);
				//no need to set interpolatedHints to null, it is initialised like that
			}
		}

		//that.updateToolTip(); //We are not implementing this

		//that.ctatdebug("CTATExampleTracerLink --> out of addHint");
		return;
	};

	/**
	 * @param selection of type array
	 * @return undefined
	 */
	this.setSelection = function(selection)
	{
		//that.ctatdebug("CTATExampleTracerLink --> in setSelection");
		that.getMatcher().setDefaultSelection(selection[0] === null || typeof(selection[0]) === 'undefined' ? "" : selection[0].toString());
		//that.ctatdebug("CTATExampleTracerLink --> in setSelection");
	};

	/**
	 * @param action of type array
	 * @return undefined
	 */
	this.setAction = function(action)
	{
		//that.ctatdebug("CTATExampleTracerLink --> in setAction");
		that.getMatcher().setDefaultAction(action[0] === null || typeof(action[0]) === 'undefined' ? "" : action[0].toString());
		//that.ctatdebug("CTATExampleTracerLink --> in setAction");
	};

	/**
	 * @param givenActionType of type string
	 * @return undefined
	 */
	this.setActionType = function(givenActionType)
	{
		//that.ctatdebug("CTATExampleTracerLink --> in setActionType");
		if(givenActionType === null || typeof(givenActionType) === 'undefined' || ((givenActionType.toString() !== CTATExampleTracerLink.CORRECT_ACTION.toString()) && (givenActionType.toString() !== CTATExampleTracerLink.FIREABLE_BUGGY_ACTION.toString()) && (givenActionType.toString() !== CTATExampleTracerLink.BUGGY_ACTION.toString()) && (givenActionType.toString() !== CTATExampleTracerLink.HINT_ACTION.toString()) && (givenActionType.toString() !== CTATExampleTracerLink.UNTRACEABLE_ERROR.toString()) && (givenActionType.toString() !== CTATExampleTracerLink.CLT_ERROR_ACTION.toString()) && (givenActionType.toString() !== CTATExampleTracerLink.GIVEN_ACTION.toString())))
		{
			throw new CTATExampleTracerException("invalid action type " + actionType);
		}

		actionType = givenActionType;

		//We are not keeping ActionLabel in the JS implementation

		//that.ctatdebug("CTATExampleTracerLink --> out of setActionType");
	};

	/**
	 * @param givenBuggyMsg of type string
	 * @return undefined
	 */
	this.setBuggyMsg = function(givenBuggyMsg)
	{
		//that.ctatdebug("CTATExampleTracerLink --> in setBuggyMsg");
		buggyMsg = givenBuggyMsg;
		//that.ctatdebug("CTATExampleTracerLink --> out of setBuggyMsg");
	};

	/**
	 * @param givenSuccessMsg of type string
	 * @return undefined
	 */
	this.setSuccessMsg = function(givenSuccessMsg)
	{
		//that.ctatdebug("CTATExampleTracerLink --> in setSuccessMsg");
		successMsg = givenSuccessMsg;
		//that.ctatdebug("CTATExampleTracerLink --> out of setSuccessMsg");
	};

	/**
	 * Set minTraversals and minTraversalsStr
     * The argument can be a variable, which sets minTraversals to the default value.
	 * @param givenMinTraversals of type string: if not numeric, effective value is 1
	 * @return undefined
	 */
	this.setMinTraversalsStr = function(givenMinTraversals)
	{
		//that.ctatdebug("CTATExampleTracerLink --> in setMinTraversalsStr");

		//If input is null or empty, set min to 1
    	//(Lowest sensible value)
    	if(givenMinTraversals === null || typeof(givenMinTraversals) === 'undefined' || givenMinTraversals.length < 1)
    	{
    		//that.ctatdebug("CTATExampleTracerLink --> in setMinTraversalsStr in if branch");
    		minTraversalsStr = "1";
    		minTraversals = 1;
    	}
    	else
    	{
    		//that.ctatdebug("CTATExampleTracerLink --> in setMinTraversalsStr in else branch");
    		try
    		{
    			//that.ctatdebug("CTATExampleTracerLink --> in setMinTraversalsStr in try branch");
    			//Try to parse input
    			minTraversalsStr = givenMinTraversals;
    			minTraversals = parseInt(givenMinTraversals.trim());
				if(isNaN(minTraversals))
				{
					minTraversals = 1;
				}
    		}
    		catch(e)
    		{
    			//that.ctatdebug("CTATExampleTracerLink --> in setMinTraversalsStr in catch branch");
    			//If input is not numeric, set min to 1
    	    	//(Lowest sensible value)
    			minTraversals = 1;
    		}
    	}

    	//that.ctatdebug("CTATExampleTracerLink --> out of setMinTraversalsStr");
	};

	/**
	 * Set maxTraversals and  maxTraversalsStr.
     * The argument can be a variable, which sets maxTraversals
     * to the default value.
	 * @param givenMaxTraversals of type string: if not numeric, effective value is 1
	 * @return undefined
	 */
	this.setMaxTraversalsStr = function(givenMaxTraversals)
	{
		//that.ctatdebug("CTATExampleTracerLink --> in setMaxTraversalsStr");

		//If input is null or empty, set max to current min value
		//(CANNOT go lower than current min value)
    	if(givenMaxTraversals === null || typeof(givenMaxTraversals) === 'undefined' || givenMaxTraversals.length < 1)
    	{
    		//that.ctatdebug("CTATExampleTracerLink --> in setMaxTraversalsStr in if branch");
    		maxTraversalsStr = minTraversalsStr;
    		maxTraversals = minTraversals;
    	}
    	else
    	{
    		//that.ctatdebug("CTATExampleTracerLink --> in setMaxTraversalsStr in else branch");

    		try
    		{
    			//that.ctatdebug("CTATExampleTracerLink --> in setMaxTraversalsStr in try branch");
    			//Try to parse input
    			maxTraversalsStr = givenMaxTraversals;
    			maxTraversals = parseInt(givenMaxTraversals.trim());
				if(isNaN(maxTraversals))
				{
					maxTraversals = 1;
				}
    		}
    		catch(e)
    		{
    			//that.ctatdebug("CTATExampleTracerLink --> in setMaxTraversalsStr in catch branch");
				//If input is not numeric, set max to current min value
				//(CANNOT go lower than current min value)
    			maxTraversals = minTraversals;
    		}
    	}

    	//that.ctatdebug("CTATExampleTracerLink --> out of setMaxTraversalsStr");
	};

	/**
	 * Set localUniqueID.
	 * @param givenID of type integer
	 * @return undefined
	 */
	this.setUniqueID = function(givenID)
	{
		//that.ctatdebug("CTATExampleTracerLink --> in setUniqueID");
		uniqueID = givenID;
		//No Problem Model
		//that.ctatdebug("CTATExampleTracerLink --> out of setUniqueID");
	};

	/**
	 * Add the given name to skillNames. Will not add a duplicate name.
	 * @param {String} ruleLabelText
	 * @return {undefined}
	 */
	this.addSkillName = function(skillLabelText)
	{
		that.ctatdebug("ETLink["+uniqueID+"].addSkillName(" + skillLabelText + ")");

        if(skillLabelText === "unnamed")
        {
            return;
        }
		if(skillNames === null || typeof(skillNames) === 'undefined')
		{
			skillNames = [];
		}
		if(skillNames.indexOf(skillLabelText) > -1)
		{
			return;
		}
		skillNames.push(skillLabelText);

		that.ctatdebug("ETLink.addSkillName() return with skillNames " + skillNames);
	};

	/**
	 * @return {array of Strings}
	 */
	this.getSkillNames = function()
	{
		if(skillNames === null || typeof(skillNames) === 'undefined')
		{
			skillNames = [];
		}

		return skillNames;
	};

	/**
	 * Tell whether this link could be tutor-performed. Does not check number of sibling links.
	 * Instead, tests getActionType() is CORRECT_ACTION, linkTriggered argument, if not null,
	 * matches Matcher.isLinkTriggered() link Matcher.isTutorActor(String, boolean) returns
	 * true on  getActor().
	 * @param {boolean} linkTriggered if not null, Matcher.isLinkTriggered() must match the value;
	 * if null, test not applied
	 * @return {boolean} true if could be tutor-performed
	 */
	this.isTutorPerformed = function(linkTriggered)
	{
		if(CTATExampleTracerLink.CORRECT_ACTION.toUpperCase() !== that.getActionType().toUpperCase())
		{
			return false; // must be a correct step
		}

		var m = that.getMatcher();

		if(m === null || typeof(m) === 'undefined')
		{
			return false;
		}

		if(linkTriggered !== null && typeof(linkTriggered) !== 'undefined' && linkTriggered !== m.isLinkTriggered())
		{
			return false; // trigger must match arg
		}

		var actor = that.getActor(); //of type string

		if(CTATMatcher.isTutorActor(actor, true))
		{
			return true; // actor must be tutor or any
		}

		return false;
	};

	/**
	 * @param {boolean} givenIsPreferredLink new value for isPreferredLink
	 * @return {undefined}
	 */
	this.setIsPreferredLink = function(givenIsPreferredLink)
	{
		that.ctatdebug("ETLink.setIsPreferredLink() was "+isPreferredLink+", now "+givenIsPreferredLink);
		isPreferredLink = givenIsPreferredLink;
	};


/****************************** MARTIN'S CODE ****************************************************/

	/**
	*
	*/
	this.setVisualData = function(aData)
	{
		//that.ctatdebug("CTATExampleTracerLink --> in setVisualData");

		visuals = aData;

		//that.ctatdebug("CTATExampleTracerLink --> out of setVisualData");

	};

	/**
	*
	*/
	this.getVisualData = function ()
	{
		//that.ctatdebug("CTATExampleTracerLink --> in getVisualData");

		return (visuals);
	};

};

/****************************** STATIC METHODS ****************************************************/

	/**
	 * Static method. Can be called as CTATExampleTracerLink.compareLinkTypes(s1, s2)
	 * without the need of an instance of the class
	 * Compare 2 link types. The arguments should be Strings from this list:
	 * CTATExampleTracerLink.CORRECT_ACTION (best)
	 * CTATExampleTracerLink.FIREABLE_BUGGY_ACTION
	 * CTATExampleTracerLink.BUGGY_ACTION (worst)
	 * @static
	 * @param {string} t1
	 * @param {string} t2
	 * @return {integer} -1 if t1 is preferred to t2; 1 if t2 is preferred; else 0
	 */
	CTATExampleTracerLink.compareLinkTypes = function(t1, t2)
	{
		ctatdebug("CTATExampleTracerLink --> in compareLinkTypes(" + t1 + ", " + t2 + ")");

		if(t1 === null || typeof(t1) === 'undefined')
		{
			return (t2 === null || typeof(t2) === 'undefined' ? 0 : 1);
		}

		if(t2 === null || typeof(t2) === 'undefined')
		{
			return -1;
		}

		if(t1.toString() === t2.toString())
		{
			return 0;
		}

		if(t1.toString() === CTATExampleTracerLink.CORRECT_ACTION.toString())
		{
			return -1;
		}
		else if(t2.toString() === CTATExampleTracerLink.CORRECT_ACTION.toString())
		{
			return 1;
		}
		else if(t1.toString() === CTATExampleTracerLink.FIREABLE_BUGGY_ACTION.toString())
		{
			return -1;
		}
		else if(t2.toString() === CTATExampleTracerLink.FIREABLE_BUGGY_ACTION.toString())
		{
			return 1;
		}
		else if(t1.toString() === CTATExampleTracerLink.BUGGY_ACTION.toString())
		{
			return -1;
		}
		else if(t2.toString() === CTATExampleTracerLink.BUGGY_ACTION.toString())
		{
			return 1;
		}

		//ctatdebug("CTATExampleTracerLink --> returning from compareLinkTypes");

		return 0;
	};

/**************************** CONSTANTS ******************************************************/

    Object.defineProperty(CTATExampleTracerLink, "BUGGY_ACTION", {enumerable: false, configurable: false, writable: false, value: "Buggy Action"});
    Object.defineProperty(CTATExampleTracerLink, "FIREABLE_BUGGY_ACTION", {enumerable: false, configurable: false, writable: false, value: "Fireable Buggy Action"});
    Object.defineProperty(CTATExampleTracerLink, "CORRECT_ACTION", {enumerable: false, configurable: false, writable: false, value: "Correct Action"});
    Object.defineProperty(CTATExampleTracerLink, "NO_MODEL", {enumerable: false, configurable: false, writable: false, value: "NO-MODEL"});
    Object.defineProperty(CTATExampleTracerLink, "HINT_ACTION", {enumerable: false, configurable: false, writable: false, value: "Hint Action"});
    Object.defineProperty(CTATExampleTracerLink, "UNTRACEABLE_ERROR", {enumerable: false, configurable: false, writable: false, value: "Untraceable Error"});
    Object.defineProperty(CTATExampleTracerLink, "CLT_ERROR_ACTION", {enumerable: false, configurable: false, writable: false, value: "Error Action"});
    Object.defineProperty(CTATExampleTracerLink, "GIVEN_ACTION", {enumerable: false, configurable: false, writable: false, value: "Given Action"});
    Object.defineProperty(CTATExampleTracerLink, "SUCCESS", {enumerable: false, configurable: false, writable: false, value: "SUCCESS"});


CTATExampleTracerLink.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerLink.prototype.constructor = CTATExampleTracerLink;

if(typeof module !== 'undefined')
{
	module.exports = CTATExampleTracerLink;
}
