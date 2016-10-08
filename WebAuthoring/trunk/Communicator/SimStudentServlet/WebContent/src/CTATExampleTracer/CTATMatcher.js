/* This object represents an CTATMatcher */

goog.provide('CTATMatcher');
goog.require('CTATBase');
goog.require('CTATMsgType');
goog.require('CTATExampleTracerSAI');

/* LastModify: FranceskaXhakaj 07/14*/

/**
 * @param {boolean} givenSingle
 * @param {integer} givenVector 
 * @param {boolean} givenCaseInsensitive 
 */
CTATMatcher = function(givenSingle, givenVector, givenCaseInsensitive) 
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

	CTATBase.call(this, "CTATMatcher","");

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

   /**
    * True for the newer matching methods.
    * @type {boolean}
    */
	var single = givenSingle;

   /**
    * 
    * @type {integer}
    */	
	var vector = (givenVector === null || typeof(givenVector) === 'undefined' ? CTATMatcher.NON_SINGLE : givenVector);

	/**
    * 
    * @type {boolean}
    */
	var caseInsensitive = givenCaseInsensitive;

	//WE DECIDED THAT THIS VARIABLE IS NOT NEEDED
	//final variable, it cannot be changed after declaring it here. To access use CTATMatcher.concat
	//true if we are using concatenation matching (doesn't make sense if we're not using single matching)
	//Object.defineProperty(CTATMatcher, "concat", {enumerable: false, configurable: false, writable: false, value: (givenConcat === null || typeof(givenConcat) === 'undefined' ? false : givenConcat)});
	
   /**
    * 
    * @type {string}
    */
	var defaultSelection = "";

	/**
    * 
    * @type {string}
    */
	var defaultAction = "";

	/**
    * 
    * @type {string}
    */
	var defaultInput = "";

	/**
    * 
    * @type {string}
    */
	var defaultActor = CTATMsgType.DEFAULT_ACTOR; //of type string

	/**
    * As an invariant, the first of the defaultSelectionArray should be the defaultSelection string
    * @type {array of strings}
    */
	var defaultSelectionArray = [];

   /**
    * 
    * @type {array of strings}
    */
	var defaultActionArray = [];

   /**
    * 
    * @type {array of strings}
    */
	var defaultInputArray = [];

   /**
    * Available for replacing entered student input to send back to student interface.
    * @type {string}
    */
	var replacementFormula = null;

   /**
    * 
    * @type {Object}
    */
	var lastResult = null; //of type Object

   /**
    * 
    * @type {string}
    */
	var singleValue = null; //of type string

    //see isLinkTriggered()
    var linkTriggered = false;

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/

/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * This method will be empty.
	 * Reset this matcher to its initial state. This is a no-op for matchers that
	 * have no internal state. But see, e.g., CTATSolverMatcher#reset().
	 */
	this.resetMatcher = function ()
	{
		//ctatdebug("CTATMatcher --> in resetMatcher");

		//EMPTY -- implemented in child classes
	};

	/**
     * Get the Selection parameter as a scalar String. Subclasses may want
     * to override this implementation.
     * @return {string} result of getDefaultSelection()
     */
	this.getSelection = function()
	{
		//ctatdebug("CTATMatcher --> in getSelection");
		return that.getDefaultSelection();
	};

    /**
     * Get the Action parameter as a scalar String. Subclasses may want
     * to override this implementation.
     * @return {string} result of getDefaultAction()
     */
	this.getAction = function()
	{
		//ctatdebug("CTATMatcher --> in getAction");
		return that.getDefaultAction();
	};

    /**
     * @return {string} returns the defaulInput.
     */
	this.getDefaultInput = function()
	{
		//ctatdebug("CTATMatcher --> in getDefaultInput");
		return defaultInput;
	};

	/**
	 * @return {string} result of getDefaultActor()
	 */
	this.getActor = function()
	{
		//ctatdebug("CTATMatcher --> in getActor");
		return that.getDefaultActor();
	};


	/**
	 * @param {givenCaseInsensitive} of type boolean
	 * @return {undefined}
	 */
	this.setCaseInsensitive = function(givenCaseInsensitive)
	{
		//ctatdebug("CTATMatcher --> in setCaseInsensitive");
		caseInsensitive = givenCaseInsensitive;
	};

	/**
	 * Get the value of a formula calculation specified for the input element.
	 * Override this implementation in ExpressionMatcher to return the
	 * result of the formula.
	 * @return result of  getInput() 
	 */
	this.getEvaluatedInput = function ()
	{
		//ctatdebug("CTATMatcher --> in getEvaluatedInput");
		return that.getInput();
	};

	/**
	 * Tell how many traversals a visit to this link represents. For some
	 * matchers, such as CTATSolverMatcher, a visit may not be the same as a traversal.
	 * @return {integer} constant 1 for this default implementation
	 */
	this.getTraversalIncrement = function ()
	{
		//ctatdebug("CTATMatcher --> in getTraversalIncrement");
		return 1;
	};

	/**
	 * @return {string} the defaultSelection
	 */
	this.getDefaultSelection = function()
	{
		//ctatdebug("CTATMatcher --> in getDefaultSelection");
		return defaultSelection;
	};

	/**
	 * @return {string} the defaultAction
	 */
	this.getDefaultAction = function()
	{
		//ctatdebug("CTATMatcher --> in getDefaultAction");
		return defaultAction;
	};

	this.getDefaultSAI = function()
	{
		var saiCopy = new CTATExampleTracerSAI(that.getDefaultSelection(),that.getDefaultAction(),that.getDefaultInput(),that.getDefaultActor());
		that.ctatdebug("CTATMatcher.getDefaultSAI() " + saiCopy + "; toXML " + (saiCopy ? saiCopy.toXMLString() : "(null)"));
		return 
	};

	/**
	 * @return {string} the defaultActor
	 */
	this.getDefaultActor = function()
	{
		//ctatdebug("CTATMatcher --> in getDefaultActor");
		return defaultActor;
	};

    /**
     * Get the Input parameter as a scalar String. Subclasses may want
     * to override this implementation.
     * @return result of getDefaultInput() 
     */
	this.getInput = function()
	{
		//ctatdebug("CTATMatcher --> in getInput");
		return that.getDefaultInput();
	};

	/**
	 * @return {boolean}
	 */
	this.getCaseInsensitive = function()
	{
		//ctatdebug("CTATMatcher --> in getCaseInsensitive");
		return caseInsensitive;
	};

    /**
     * Test whether the given actor matches our getActor() element.
     * This default implementation mimics the test of the ExactMatcher
     * @param {string} actor
     * @return {boolean} true if match ok
     */
	this.matchActor = function(actor)
	{
		//ctatdebug("CTATMatcher --> in matchActor");
		var myActor = that.getActor();

		if(CTATMsgType.ANY_ACTOR.toString().toUpperCase() === myActor.toString().toUpperCase())
		{
			return true;
		}

		if(CTATMsgType.ANY_ACTOR.toString().toUpperCase() === actor.toString().toUpperCase()) // ANY may never occur as argument
		{
			return true;
		}

		if(myActor === null || typeof(myActor) === 'undefined')
		{
			myActor = that.getDefaultActor();
		}

		if(myActor === null || typeof(myActor) === 'undefined') // shouldn't happen
		{
			return (actor === null || actor === 'undefined');
		}

		if(CTATMsgType.UNGRADED_TOOL_ACTOR.toString().toUpperCase() === myActor.toString().toUpperCase() && CTATMsgType.DEFAULT_TOOL_ACTOR.toString().toUpperCase() === actor.toString().toUpperCase())
		{
			return true;
		}

		if(CTATMsgType.UNGRADED_TOOL_ACTOR.toString().toUpperCase() === actor.toString().toUpperCase() && CTATMsgType.DEFAULT_TOOL_ACTOR.toString().toUpperCase() === myActor.toString().toUpperCase())
		{
			return true;
		}

		//ctatdebug("CTATMatcher --> out of matchActor");
		return (myActor.toString().toUpperCase() === actor.toString().toUpperCase());
	};

	/**
	 * Whether to replace the student input with a formula result.
	 * @return {boolean} replacementFormula !== null
	 */
	this.replaceInput = function()
	{
		//ctatdebug("CTATMatcher --> in replaceInput");
		return (that.getReplacementFormula() !== null && typeof(that.getReplacementFormula()) !== 'undefined');
	};

	/**
	 * @return {string}
	 */
	this.getReplacementFormula = function()
	{
		//ctatdebug("CTATMatcher --> in getReplacementFormula");
		return replacementFormula;
	};

	/**
	 * @return {array}
	 */
	this.getDefaultSelectionArray = function()
	{
		//ctatdebug("CTATMatcher --> in getDefaultSelectionArray");

		if(defaultSelectionArray.length === 0)
		{
			var v = [];
			v.push(defaultSelection);
			return v;
		}

		return defaultSelectionArray;
	};

	/**
	 * @return {array}
	 */
	this.getDefaultActionArray = function()
	{
		//ctatdebug("CTATMatcher --> in getDefaultActionArray");

		if(defaultActionArray.length === 0)
		{
			var v = [];
			v.push(defaultAction);
			return v;
		}

		return defaultActionArray;
	};

	/**
	 * @return {array}
	 */
	this.getDefaultInputArray = function()
	{
		//ctatdebug("CTATMatcher --> in getDefaultInputArray");

		if(defaultInputArray.length === 0)
		{
			var v = [];
			v.push(defaultInput);
			return v;
		}

		return defaultInputArray;
	};

	/**
	 * @return {string}
	 */
	this.getLastResult = function()
	{
		//ctatdebug("CTATMatcher --> in getLastResult");
		//Note: the toString() here should do the same thing as in Java
		return (lastResult === null || typeof(lastResult) === 'undefined' ? "" : lastResult.toString());
	};


	/**
	 * Evaluate the getReplacementFormula() with the given arguments. 
	 * @param {array} selection element 0 is arg to CTATFunctions.evaluate(String, String, String, String)
	 * @param {array} action element 0 is arg to CTATFunctions.evaluate(String, String, String, String)
	 * @param {array} input element 0 is arg to CTATFunctions.evaluate(String, String, String, String)
	 * @param vt arg for CTATFunctions.CTATFunctions(VariableTable, ProblemModel, Parser)
	 * @return {array}
	 */
	this.evaluateReplacement = function(selection, action, input, vt)
	{
		//ctatdebug("CTATMatcher --> in evaluateReplacement");
		//...
		return null;
	};

    /**
     * Override if we actually deal with an entire array
     * @param {array} selection The entire array of selections
     * @return {undefined}
     */
	this.setDefaultSelectionArray = function(selection)
	{
		//ctatdebug("CTATMatcher --> in setDefaultSelectionArray");

		that.setDefaultSelection(selection[0]);
		defaultSelectionArray = selection;

		//ctatdebug("CTATMatcher --> out of setDefaultSelectionArray");
	};

    /**
     * Set the action vector
     * @param {array} action Array of actions
     * @return {undefined}
     */
	this.setDefaultActionArray = function(action)
	{
		//ctatdebug("CTATMatcher --> in setDefaultActionArray");

		that.setDefaultAction(action[0]);
		defaultActionArray = action;

		//ctatdebug("CTATMatcher --> out of setDefaultActionArray");
	};

    /**
     * Set the input vector
     * @param {array} input Array of inputs
     * @return {undefined}
     */
	this.setDefaultInputArray = function(input)
	{
		//ctatdebug("CTATMatcher --> in setDefaultInputArray");

		that.setDefaultInput(input[0]);
		defaultInputArray = input;

		//ctatdebug("CTATMatcher --> out of setDefaultInputArray");
	};

	/**
	 * @param {string} givenDefaultSelection The defaultSelection to set
	 * @return {undefined}
	 */
	this.setDefaultSelection = function(givenDefaultSelection)
	{
		//ctatdebug("CTATMatcher --> in setDefaultSelection");

		defaultSelection = givenDefaultSelection;
		defaultSelectionArray.length = 0; //clear array
		defaultSelectionArray.push(givenDefaultSelection);

		//ctatdebug("CTATMatcher --> out of setDefaultSelection");
	};

	/**
	 * @param {string} givenDefaultAction The defaultAction to set
	 * @return {undefined}
	 */
	this.setDefaultAction = function(givenDefaultAction)
	{
		//ctatdebug("CTATMatcher --> in setDefaultAction");

		defaultAction = givenDefaultAction;
		defaultActionArray.length = 0; //clear array
		defaultActionArray.push(givenDefaultAction);

		//ctatdebug("CTATMatcher --> out of setDefaultAction");
	};

	/**
	 * @param {string} givenDefaultInput the default input to set
	 * @return {undefined}
	 */
	this.setDefaultInput = function(givenDefaultInput)
	{
		//ctatdebug("CTATMatcher --> in setDefaultInput");

		defaultInput = givenDefaultInput;
		defaultInputArray.length = 0; //clear array
		defaultInputArray.push(givenDefaultInput);

		//ctatdebug("CTATMatcher --> out of setDefaultInput");
	};

    /**
     * Stores the current input.
     * @return {undefined}
     */
	this.setSingle = function(text)
	{
		//ctatdebug("CTATMatcher --> in setSingle");

		if(text === null || typeof(text) === 'undefined')
		{
			//ctatdebug("CTATMatcher --> in setSingle if condition");
			return;
		}

		singleValue = text;

		//ctatdebug("CTATMatcher --> out of setSingle");
	};

	/**
	 * @param {string} givenDefaultActor
	 * @return {undefined}
	 */
	this.setDefaultActor = function(givenDefaultActor)
	{
		//ctatdebug("CTATMatcher --> in setDefaultActor");

		if(givenDefaultActor === null || typeof(givenDefaultActor) === 'undefined')
		{
			givenDefaultActor = CTATMsgType.DEFAULT_ACTOR;
		}

		if(givenDefaultActor.toString() === "Tool".toString())
		{
			givenDefaultActor = "Tutor";
		}

		defaultActor = givenDefaultActor;

		//ctatdebug("CTATMatcher --> out of setDefaultActor");
	};

    /**
     * The concated string we use for exact, regex, and wildcard matchers
     * Also used to display the sai vectors in EditStudentInputDialog 
     * @param {array} v  
     * @return {string} - element each on their own line
     */

	this.array2ConcatString = function(v)
	{
		//ctatdebug("CTATMatcher --> in array2ConcatString");
		var concat = "";

		v.forEach(function(o)
		{
			concat += o.toString() + "\n";
		});

		return concat.substring(0, (concat.length > 0 ? concat.length - 1 : 0)); //don't need the last \n
	};

    /**
     * Imitates the exact match for the entirety of a single vector
     * Overridden by Expression and Range matchers for which it only makes sense
     * to take in a single element of a vector
     * @param {array of strings} v 
     * @return {boolean}
     */
	this.matchConcatenation = function(v)
	{
        //ctatdebug("CTATMatcher --> in matchConcatenation: v = " + v);
		
		return that.matchSingle(that.array2ConcatString(v));
	};

	/**
	 * @param {string} s
	 * @return {boolean} 
	 */
	this.matchSingle = function(s)
	{
        //ctatdebug("CTATMatcher --> in matchSingle: s = " + s + " singleval = " + singleValue);
		
		return (s.toString() === singleValue.toString());
	};

	/**
	 * Method added as getter of the single value.
	 * @return {boolean}
	 */
	this.getSingleBool = function()
	{
		return single;
	};

	/**
	 * Method added as getter of the singleValue.
	 * @return string
	 */
	this.getSingleStr = function()
	{
		return singleValue;
	};

    /**
     * If this step could be performed automatically by the tutor, tell whether it should
     * be link-triggered (when its source state is the destination state of a link just
     * traversed) or state-triggered (when its source state becomes the current state). 
     * @return {boolean} true if link-triggered, false if state-triggered
     */
	this.isLinkTriggered = function()
	{
		return linkTriggered;
	};

	/**
	 * @param {boolean} givenLinkTriggered new value for linkTriggered
	 */
	this.setLinkTriggered = function(givenLinkTriggered)
	{
		linkTriggered = givenLinkTriggered;
	};

	/**
	 * Returns a meaningles string.
	 * @return {string}
	 */
	CTATMatcher.prototype.toString = function()
	{
		//ctatdebug("This is CTATMatcher. You should not be here");
		return "This is CTATMatcher.";
	};

/****************************** STATIC METHODS ****************************************************/

	/**
	 * Tell whether the tutor is the actor.
	 * @param {string} actor
	 * @param {boolean} acceptAny if true, also return true for ANY_ACTOR  
	 * @return {boolean} true if actor matches DEFAULT_TOOL_ACTOR or UNGRADED_TOOL_ACTOR 
	 */
	CTATMatcher.isTutorActor = function(actor, acceptAny)
	{
		if(CTATMsgType.DEFAULT_TOOL_ACTOR.toUpperCase() === actor.toUpperCase())
		{
			return true;
		}

		if(CTATMsgType.UNGRADED_TOOL_ACTOR.toUpperCase() === actor.toUpperCase())
		{
			return true;
		}

		if(acceptAny && CTATMsgType.ANY_ACTOR.toUpperCase() === actor.toUpperCase())
		{
			return true;
		}

		return false;
	};

/****************************** PUBLIC METHODS ****************************************************/


/****************************** CONSTRUCTOR CALLS ****************************************************/

};

/**************************** CONSTANTS ******************************************************/

    /**
     * For single matchers, defines what vector type it is associated with, 
     * will be -1 for non-singles, and VECTOR for VectorMatchers
     * Note that we use the SAI constants for traversing well constructed arrays
     * Don't use the other two for it ...
     */
    Object.defineProperty(CTATMatcher, "NON_SINGLE", {enumerable: false, configurable: false, writable: false, value: -1});
    Object.defineProperty(CTATMatcher, "SELECTION", {enumerable: false, configurable: false, writable: false, value: 0});
    Object.defineProperty(CTATMatcher, "ACTION", {enumerable: false, configurable: false, writable: false, value: 1});
    Object.defineProperty(CTATMatcher, "INPUT", {enumerable: false, configurable: false, writable: false, value: 2});
	Object.defineProperty(CTATMatcher, "VECTOR", {enumerable: false, configurable: false, writable: false, value: 3});

	/** 
	 * Actor property in message. 
	 * @param {String}
	 */
	Object.defineProperty(CTATMatcher, "ACTOR", {enumerable: false, configurable: false, writable: false, value: "Actor"});

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATMatcher.prototype = Object.create(CTATBase.prototype);
CTATMatcher.prototype.constructor = CTATMatcher;

if(typeof module !== 'undefined')
{
	module.exports = CTATMatcher;
}