/* This object represents /n CTATExampleTracerLink */

goog.provide('CTATExampleTracerLink');
goog.require('CTATBase');
goog.require('CTATExampleTracerException');
goog.require('CTATMatcher');
goog.require('CTATMsgType');

//goog.require('CTATVariableTable');//
//goog.require('CTATExactMatcher');//
//goog.require('CTATExampleTracerSAI');//
//goog.require('CTATExampleTracerEvent');//
//goog.require('CTATExampleTracerLinkVisualData');//

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

	//this will not be part of CTATExampleTracerLink anymore
	//instead it will be part of CTATExampleTracerEvent
	//var interpolatedHints = []; //array of strings

	/**
	 * @type {array of strings}
	 */
	var hints = [];

	/**
	 * 
	 */
	var studentInput = null;

	/**
	 * 
	 */
	var studentAction = null;

	/**
	 * 
	 */
	var studentSelection = null;

	/**
	 * @type {boolean}
	 */
	var isPreferredLink = null;

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
	 * Checks whether the link's sai matches given sai
	 * @param {CTATExampleTracerSAI} sai input to matcher
	 * @param {CTATVariableTable} vt interp specific vt, if null refers to problemmodel(best interp)'s vt
	 * @return {boolean} true or false depending whether sai matches or not
	 */
	this.matchesSAI = function (sai, vt)
	{
		var m = that.getMatcher();
		that.ctatdebug("CTATExampleTracerLink --> in matchesSAI(); matcher " + m + ", typeof " + typeof(m));

		//Note: this belongs to CTATExampleTracerEvent now
		//This call is fixed in CTATExampleTracerTracer
		//that.setInterpolateSAI(sai.getSelectionAsString(), sai.getActionAsString(), sai.getInputAsString());

		//in the case of CTATExactMatcher, the vt will be ignored
		var mResult = m.match(sai.getSelectionAsArray(), sai.getActionAsArray(), sai.getInputAsArray(), sai.getActor(), vt); 

		that.ctatdebug("CTATExampleTracerLink --> out of matchesSAI: result = " + mResult);

		return mResult;
	};


	/**
	 * Tries to match just the selection and actor elements of the SAI. 
     * Even if matches ok, rejects if hint texts are desired, but the link has no hints.
     * To make this check fast, we don't evaluate hints, only check whether any hint
     * is defined.
     * @param {CTATExampleTracerSAI} sai
     * @param {CTATExampleTracerEvent} result
     * @param {CTATVariableTable} vt
     * @return {boolean} true if match and has hints if wanted
	 */
	this.matchesSAIforHint = function (sai, result, vt)
	{
		that.ctatdebug("CTATExampleTracerLink --> in matchesSAIforHint");

		if(that.getMatcher().matchForHint(sai.getSelectionAsArray(), sai.getActionAsArray(), sai.getActor(), vt) === false)
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
		//ctatdebug("CTATExampleTracerLink --> in getDepth");

		return depth;
	};

	/**
	 * @return String
	 */
	this.getBuggyMsg = function()
	{
		//ctatdebug("CTATExampleTracerLink --> in getBuggyMsg");

		return buggyMsg;
	};

	/**
	 * @return integer
	 */
	this.getSuccessMsg = function()
	{
		//ctatdebug("CTATExampleTracerLink --> in getSuccessMsg");

		return successMsg;
	};

	/**
	 * @return object of type string: returns the type of the link
	 */
	this.getType = function ()
	{
		//ctatdebug("CTATExampleTracerLink --> in getType");

		return actionType;
	};

	/** 
	 * @return integer: the id of the previous node
	 */
	this.getPrevNode = function()
	{
		//ctatdebug("CTATExampleTracerLink --> in getPrevNode");

		return prevNode;
	};
	
	/** 
	 * @return integer: the id of the next node
	 */
	this.getNextNode = function()
	{
		//ctatdebug("CTATExampleTracerLink --> in getNextNode");

		return nextNode;
	};

		/**
	 * Sets the prevNode with the given id
	 * @param prevNodeId of type integer 
	 * @return undefined
	 */
	this.setPrevNode = function(prevNodeId)
	{
		//ctatdebug("CTATExampleTracerLink --> in setPrevNode");
		prevNode = prevNodeId;
		//ctatdebug("CTATExampleTracerLink --> out of setPrevNode");
	};

	/**
	 * Sets the nextNode with the given id
	 * @param nextNodeId of type integer 
	 * @return undefined
	 */
	this.setNextNode = function(nextNodeId)
	{
		//ctatdebug("CTATExampleTracerLink --> in setNextNode");
		nextNode = nextNodeId;
		//ctatdebug("CTATExampleTracerLink --> out of setNextNode");
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
	 * @param givenDepth of type integer
	 * @return undefined
	 */
	this.setDepth = function(givenDepth)
	{
		depth = givenDepth;
	};

/********************** CODE BELONGS ORIBINALLY TO EDGEDATA.JAVA ******************************/


	/*
	 * Function has been removed from here and has been put in CTATExampleTracerEvent
	 * @param s of type string
	 * @param a of type string
	 * @param i of type string
	 * @return void
	 */
	/*this.setInterpolateSAI = function(s, a, i)
	{
		interpolateSelection = s;
        interpolateAction = a;
        interpolateInput = i;
	};*/

	/**
	 * This method returns the operative value for the maximum traversals 
     * permitted.
	 * @return integer
	 */
	this.getMaxTraversals = function ()
	{
		//ctatdebug("CTATExampleTracerLink --> in getMaxTraversals");

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
		//ctatdebug("CTATExampleTracerLink --> in isDone");

		var s = that.getSelection();
		var a = that.getAction();

		//ctatdebug("in isDone: s and a");
		//console.log(s);
		//console.log(a);

		if(s.length > 0 && a.length > 0)
		{
			//ctatdebug("in isDone inside if condition");
			return (CTATMsgType.DONE.toString().toUpperCase() === s[0].toString().toUpperCase() && CTATMsgType.BUTTON_PRESSED.toString().toUpperCase() === a[0].toString().toUpperCase());
		}

		//ctatdebug("CTATExampleTracerLink --> out of isDone");

		return false;
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
		//ctatdebug("CTATExampleTracerLink --> interpolateHints");

		//BELONGS to CTATExampleTracerEvent now
		//overloaded function, need to check this to decide which function we are calling
		/*if(s !== null && a !== null && i !== null && typeof(s) !== 'undefined' && typeof(a) !== 'undefined' && typeof(i) !== 'undefined')
		{
			that.setInterpolateSAI(s, a, i);
		}*/

		var interpolatedHints = []; //array of strings

		if(hints === null || typeof(hints) === 'undefined' || hints.length === 0)
		{
			//ctatdebug("in interpolateHints first if condition");
			return interpolatedHints;
		}

		//VariableTable: Left out because from the function we call it will never be null
		//In addition we do not use the ProblemModel anymore, that's why this code will not be needed
		
		//CTATFunction: Jonathan and Octav

		hints.forEach(function(hint){
			//Note: for now I have left out the functions: Jonathan and Octav
			interpolatedHints.push(hint);
		});

		//ctatdebug("CTATExampleTracerLink --> out of interpolateHints");

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
		//ctatdebug("CTATExampleTracerLink --> in getAllNonEmptyHints");

		var nonEmptyHints = []; //array of strings

		hints.forEach(function(hint)
		{
			//JavaScript uses the trim method similar to Java
			if(hint.trim().toString() !== "".toString())
			{
				nonEmptyHints.push(hint);
			}
		});

		//ctatdebug("CTATExampleTracerLink --> out of getAllNonEmptyHints");

		return nonEmptyHints;
	};

    /**
     * This is called to hold the actual input entered by the student. This is
     * not always the same as what the Author has entered since we added new
     * matching options.
     * 
     * @param object of type object
     * @return undefined
     */
	this.setStudentInput = function(object)
	{
		//ctatdebug("CTATExampleTracerLink --> in setStudentInput");

		studentInput = object;

		//ctatdebug("CTATExampleTracerLink --> out of setStudentInput");
	};

	/** 
	 * @param object (of type Object): 
	 * @return undefined
	 */
	this.setStudentAction = function(object)
	{
		//ctatdebug("CTATExampleTracerLink --> in setStudentAction");

		studentAction = object;

		//ctatdebug("CTATExampleTracerLink --> out of setStudentAction");
	};

	/** 
	 * @return undefined
	 */
	this.setStudentSelection = function(object)
	{
		//ctatdebug("CTATExampleTracerLink --> in setStudentSelection");

		studentSelection = object;

		//ctatdebug("CTATExampleTracerLink --> out of setStudentSelection");
	};


    /**
     * Tell whether this link should replace the student's input in the tutor response.
     * @return boolean: false if not a CORRECT_ACTION or  FIREABLE_BUGGY_ACTION link;
     * else result of replaceInput()
     */
	this.replaceInput = function()
	{
		//ctatdebug("CTATExampleTracerLink --> in replaceInput");

		if(CTATExampleTracerLink.CORRECT_ACTION.toString().toUpperCase() !== that.getActionType().toString().toUpperCase() && CTATExampleTracerLink.FIREABLE_BUGGY_ACTION.toString().toUpperCase() !== that.getActionType().toString().toUpperCase())
		{
			//ctatdebug("in if condition replace input of link");
			return false;
		}

		//ctatdebug("CTATExampleTracerLink --> out of replaceInput");

		return that.getMatcher().replaceInput();
	};

	/**
	 * @param selection (of type array)
	 * @param action (of type array)
	 * @param input (of type array)
	 * @param vt (of type CTATVariableTable)
	 * @return array
     */
	this.evaluateReplacement = function(selection, action, input, vt)
	{
		//ctatdebug("CTATExampleTracerLink --> in evaluateReplacement");

		//NOTE: the original evaluateReplacement on the matcher takes 
		//as the last element the ProblemModel
		//we will not have that in this implementation
		return that.getMatcher().evaluateReplacement(selection, action, input, vt);
	};

   /**
	* @return array
	*/
	this.getSelection = function()
	{
		//ctatdebug("CTATExampleTracerLink --> in getSelection");

		return that.getMatcher().getDefaultSelectionArray();
	};

	/**
	 * @return array
	 */
	this.getAction = function()
	{
		//ctatdebug("CTATExampleTracerLink --> in getAction");

		return that.getMatcher().getDefaultActionArray();
	};

	/**
	 * @return array
	 */
	this.getInput = function()
	{
		//ctatdebug("CTATExampleTracerLink --> in getInput");

		return that.getMatcher().getDefaultInputArray();
	};

	/**
	 * @return {string}
	 */
	this.getActor = function()
	{
		return that.getMatcher().getDefaultActor();
	};

	this.getSAI = function()
	{
		var sai = that.getMatcher().getDefaultSAI();
		that.ctatdebug("CTATExampleTracerLink " + that + ": getDefaultSAI() " + sai + "; sai.toXMLString() " + (sai ? sai.toXMLString() : "null"));
		return sai;
	};
	/**
	 * @return string
	 */ 
	this.getActionType = function()
	{
		//ctatdebug("CTATExampleTracerLink --> in getActionType");

		return actionType;
	};

    /**
     * This method returns the operative value for the minimum traversals permitted.
     * @return integer: minTraversals a value of 0 means traversal is optional.
     */
	this.getMinTraversals = function()
	{
		//ctatdebug("CTATExampleTracerLink --> in getMinTraversals");

		return minTraversals;
	};

	/**
	 * @return boolean
	 */
	this.getIsPreferredLink = function()
	{
		//ctatdebug("CTATExampleTracerLink --> in getIsPreferredLink");

		return isPreferredLink;
	};

	/**
	 * Get all hints, including the empty strings.
	 * @return array of strings
	 */
	this.getAllHints = function()
	{
		//ctatdebug("CTATExampleTracerLink --> in getAllHints");

		return hints;
	};

	/**
	 * @param m of type CTATMatcher
	 * @return undefiend
	 */
	this.setMatcher = function(m)
	{
		//ctatdebug("CTATExampleTracerLink --> in setMatcher");

		matcher = m;
		//m.setExternalResources(null, null, null); Jonathan says this is a possible bug and is not needed here

		//ctatdebug("CTATExampleTracerLink --> out of setMatcher");
	};


	/**
	 * @param newHint of type string
	 * @return undefined
	 */
	this.addHint = function(newHint)
	{
		//ctatdebug("CTATExampleTracerLink --> in addHint");

		if(newHint !== null && typeof(newHint) !== 'undefined')
		{
			if(newHint.toString() !== "".toString())
			{
				hints.push(newHint);
				//no need to set interpolatedHints to null, it is initialised like that
			}
		}

		//that.updateToolTip(); //We are not implementing this

		//ctatdebug("CTATExampleTracerLink --> out of addHint");
		return;
	};

	/**
	 * @param actor of type string
	 * @return undefined
	 */
	this.setActor = function(actor)
	{
		//ctatdebug("CTATExampleTracerLink --> in setActor");
		that.getMatcher().setDefaultActor(actor);
		//ctatdebug("CTATExampleTracerLink --> out of setActor");
	};

	/**
	 * @param selection of type array
	 * @return undefined
	 */
	this.setSelection = function(selection)
	{
		//ctatdebug("CTATExampleTracerLink --> in setSelection");
		that.getMatcher().setDefaultSelection(selection[0] === null || typeof(selection[0]) === 'undefined' ? "" : selection[0].toString());
		//ctatdebug("CTATExampleTracerLink --> in setSelection");
	};

	/**
	 * @param action of type array
	 * @return undefined
	 */
	this.setAction = function(action)
	{
		//ctatdebug("CTATExampleTracerLink --> in setAction");
		that.getMatcher().setDefaultAction(action[0] === null || typeof(action[0]) === 'undefined' ? "" : action[0].toString());
		//ctatdebug("CTATExampleTracerLink --> in setAction");
	};

	/**
	 * @param input of type array
	 * @return undefined
	 */
	this.setInput = function(input)
	{
		//ctatdebug("CTATExampleTracerLink --> in setInput");
		that.getMatcher().setDefaultInput(input[0] === null || typeof(input[0]) === 'undefined' ? "" : input[0].toString());
		//ctatdebug("CTATExampleTracerLink --> in setInput");
	};

	/**
	 * @param givenActionType of type string
	 * @return undefined
	 */
	this.setActionType = function(givenActionType)
	{
		//ctatdebug("CTATExampleTracerLink --> in setActionType");
		if(givenActionType === null || typeof(givenActionType) === 'undefined' || ((givenActionType.toString() !== CTATExampleTracerLink.CORRECT_ACTION.toString()) && (givenActionType.toString() !== CTATExampleTracerLink.FIREABLE_BUGGY_ACTION.toString()) && (givenActionType.toString() !== CTATExampleTracerLink.BUGGY_ACTION.toString()) && (givenActionType.toString() !== CTATExampleTracerLink.HINT_ACTION.toString()) && (givenActionType.toString() !== CTATExampleTracerLink.UNTRACEABLE_ERROR.toString()) && (givenActionType.toString() !== CTATExampleTracerLink.CLT_ERROR_ACTION.toString()) && (givenActionType.toString() !== CTATExampleTracerLink.GIVEN_ACTION.toString())))
		{
			throw new CTATExampleTracerException("invalid action type " + actionType);
		}

		actionType = givenActionType;

		//We are not keeping ActionLabel in the JS implementation

		//ctatdebug("CTATExampleTracerLink --> out of setActionType");
	};

	/**
	 * @param givenBuggyMsg of type string
	 * @return undefined
	 */
	this.setBuggyMsg = function(givenBuggyMsg)
	{
		//ctatdebug("CTATExampleTracerLink --> in setBuggyMsg");
		buggyMsg = givenBuggyMsg;
		//ctatdebug("CTATExampleTracerLink --> out of setBuggyMsg");
	};

	/**
	 * @param givenSuccessMsg of type string
	 * @return undefined
	 */
	this.setSuccessMsg = function(givenSuccessMsg)
	{
		//ctatdebug("CTATExampleTracerLink --> in setSuccessMsg");
		successMsg = givenSuccessMsg;
		//ctatdebug("CTATExampleTracerLink --> out of setSuccessMsg");
	};

	/**
	 * Set minTraversals and minTraversalsStr
     * The argument can be a variable, which sets minTraversals to the default value.
	 * @param givenMinTraversals of type string: if not numeric, effective value is 1
	 * @return undefined
	 */
	this.setMinTraversalsStr = function(givenMinTraversals)
	{
		//ctatdebug("CTATExampleTracerLink --> in setMinTraversalsStr");

		//If input is null or empty, set min to 1
    	//(Lowest sensible value)
    	if(givenMinTraversals === null || typeof(givenMinTraversals) === 'undefined' || givenMinTraversals.length < 1)
    	{
    		//ctatdebug("CTATExampleTracerLink --> in setMinTraversalsStr in if branch");
    		minTraversalsStr = "1";
    		minTraversals = 1;
    	}
    	else
    	{
    		//ctatdebug("CTATExampleTracerLink --> in setMinTraversalsStr in else branch");
    		try
    		{
    			//ctatdebug("CTATExampleTracerLink --> in setMinTraversalsStr in try branch");
    			//Try to parse input
    			minTraversalsStr = givenMinTraversals;
    			minTraversals = parseInt(givenMinTraversals.trim());
    		}
    		catch(e)
    		{
    			//ctatdebug("CTATExampleTracerLink --> in setMinTraversalsStr in catch branch");
    			//If input is not numeric, set min to 1
    	    	//(Lowest sensible value)
    			minTraversals = 1;
    		}
    	}

    	//ctatdebug("CTATExampleTracerLink --> out of setMinTraversalsStr");
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
		//ctatdebug("CTATExampleTracerLink --> in setMaxTraversalsStr");

		//If input is null or empty, set max to current min value
		//(CANNOT go lower than current min value)
    	if(givenMaxTraversals === null || typeof(givenMaxTraversals) === 'undefined' || givenMaxTraversals.length < 1)
    	{
    		//ctatdebug("CTATExampleTracerLink --> in setMaxTraversalsStr in if branch");
    		maxTraversalsStr = minTraversalsStr;
    		maxTraversals = minTraversals;
    	}
    	else
    	{
    		//ctatdebug("CTATExampleTracerLink --> in setMaxTraversalsStr in else branch");

    		try
    		{
    			//ctatdebug("CTATExampleTracerLink --> in setMaxTraversalsStr in try branch");
    			//Try to parse input
    			maxTraversalsStr = givenMaxTraversals;
    			maxTraversals = parseInt(givenMaxTraversals.trim());
    		}
    		catch(e)
    		{
    			//ctatdebug("CTATExampleTracerLink --> in setMaxTraversalsStr in catch branch");
				//If input is not numeric, set max to current min value
				//(CANNOT go lower than current min value)
    			maxTraversals = minTraversals;
    		}
    	}

    	//ctatdebug("CTATExampleTracerLink --> out of setMaxTraversalsStr");
	};

	/**
	 * Set localUniqueID. 
	 * @param givenID of type integer 
	 * @return undefined
	 */
	this.setUniqueID = function(givenID)
	{
		//ctatdebug("CTATExampleTracerLink --> in setUniqueID");
		uniqueID = givenID;
		//No Problem Model
		//ctatdebug("CTATExampleTracerLink --> out of setUniqueID");
	};

	/**
	 * Add the given name to skillNames. Will not add a duplicate name.
	 * @param {String} ruleLabelText
	 * @return {undefined}
	 */
	this.addSkillName = function(skillLabelText)
	{
		ctatdebug("ETLink.addSkillName(" + skillLabelText + ")");

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

		ctatdebug("ETLink.addSkillName() return with skillNames.length " + skillNames.length);
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


/****************************** MARTIN'S CODE ****************************************************/

	/**
	*
	*/
	this.setVisualData = function(aData)
	{
		//ctatdebug("CTATExampleTracerLink --> in setVisualData");

		visuals = aData;

		//ctatdebug("CTATExampleTracerLink --> out of setVisualData");

	};

	/**
	*
	*/
	this.getVisualData = function ()
	{
		//ctatdebug("CTATExampleTracerLink --> in getVisualData");

		return (visuals);
	};

/****************************** STATIC METHODS ****************************************************/

	/**
	 * Static method. Can be called as CTATExampleTracerLink.compareLinkTypes(s1, s2)
	 * without the need of an instance of the class
	 * Compare 2 link types. The arguments should be Strings from this list:
	 * CTATExampleTracerLink.CORRECT_ACTION (best) 
	 * CTATExampleTracerLink.FIREABLE_BUGGY_ACTION}
	 * CTATExampleTracerLink.BUGGY_ACTION} (worst)
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
