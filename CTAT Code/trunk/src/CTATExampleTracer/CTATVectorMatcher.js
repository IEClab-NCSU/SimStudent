/* This object represents an CTATVectorMatcher */

goog.provide('CTATVectorMatcher');
goog.require('CTATMsgType');
goog.require('CTATMatcher');
goog.require('CTATExactMatcher');
goog.require('CTATExpressionMatcher');

//goog.require('CTATVariableTable');//

/* LastModify: FranceskaXhakaj 07/14*/

/**
 * @param sMatcher of type array of CTATMatchers
 * @param aMatchers of type array of CTATMatchers
 * @param iMatchers of type array of CTATMatchers
 * @param actor of type string
*/
CTATVectorMatcher = function(sMatchers, aMatchers, iMatchers, actor)
{
	//calling the constructor of the object we are inheriting from: CTATMatcher
	CTATMatcher.call(this, true, CTATMatcher.VECTOR, true);

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	var selectionMatchers = sMatchers instanceof Array?sMatchers:[]; //array of CTATMatcher
	var actionMatchers = aMatchers instanceof Array?aMatchers:[]; //array of CTATMatcher
	var inputMatchers = iMatchers instanceof Array?iMatchers:[]; //array of CTATMatcher

	var matchers = []; //array of CTATMatcher
	matchers[CTATMatcher.SELECTION] = selectionMatchers;
	matchers[CTATMatcher.ACTION] = actionMatchers;
	matchers[CTATMatcher.INPUT] = inputMatchers;

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/

	/**
	 * Performs a match on each of selection, action, and input using the specified matchers
	 * for each (concatenation or by elements)
	 * @param {Array<string>} selection
	 * @param {Array<string>} action
	 * @param {Array<string>} input
	 * @param {CTATVariableTable} vt
	 * @param {array<boolean>} details optional: if present, test all 3 of s,a,i and return individual results
	 * @return boolean: whether link matches or not
	 */
	function _match(selection, action, input, vt, details)
	{
		that.ctatdebug("CTATVectorMatcher --> in _match("+selection+", "+action+", "+input+", "+vt+")");

		if(selectionMatchers.length === 0 || actionMatchers.length === 0 || inputMatchers.length === 0)
		{
			//ctatdebug("CTATVectorMatcher --> in _match inside if condition");

			//concat is always true, we have removed it
			selectionMatchers.push(new CTATExactMatcher(CTATMatcher.SELECTION, that.array2ConcatString(that.getDefaultSelectionArray())));
			actionMatchers.push(new CTATExactMatcher(CTATMatcher.ACTION, that.array2ConcatString(that.getDefaultActionArray())));
			inputMatchers.push(new CTATExactMatcher(CTATMatcher.INPUT, that.array2ConcatString(that.getDefaultInputArray())));
		}

		//ctatdebug("CTATVectorMatcher --> in _match outside if condition");

		var values = [];
		values[CTATMatcher.SELECTION] = selection;
		values[CTATMatcher.ACTION] = action;
		values[CTATMatcher.INPUT] = input;

		//concat always true, we have removed it totally

		for(var i = 0; i < 3; i++)
		{
			//that.ctatdebug("CTATVectorMatcher --> in _match in for loop: " + i);
			var m = matchers[i][0];

			//that.ctatdebug("CTATVectorMatcher --> in _match before concat m" + (m === null||m === undefined));
			var t = (m instanceof CTATExpressionMatcher ? m.matchConcatenation(selection, action, input, vt) : m.matchConcatenation(values[i]));
			that.ctatdebug("CTATVectorMatcher --> in _match["+i+"] after "+m+".concat returns: " + t);
			if(details)
			{
				details.push(t);
			}
			else if(t === false)
			{
				//ctatdebug("CTATVectorMatcher --> in _match returned false");
				return false;
			}
		}
		if(details)
		{
			for(var d = 0; d < details.length; ++d)
			{
				if(!details[d])
				{
					//ctatdebug("CTATVectorMatcher --> in _match returned false");
					return false;
				}
			}
		}
		//ctatdebug("CTATVectorMatcher --> in _match returned true");
		return true;
	}

/***************************** PRIVILEDGED METHODS *****************************************************/

	 /**
	  * Performs a match on each of selection, action, and input
	  * by calling the private _match() method.
	  * @param selection of type array
	  * @param action of type array
	  * @param input of type array
	  * @param actor of type string
	  * @param vt of type CTATVariableTable
	  * @return boolean: whether link matches or not
	  */
	this.match = function(selection, action, input, actor, vt)
	{
		//ctatdebug("CTATVectorMatcher --> in match");

		// CTAT2238: code used to test actor first, since that was the cheapest test;
		// but sometimes we're in here to run the matcher to evaluate a formula, and
		// we need to do that regardless of whether we match, as when we're traversing
		// steps defined as student-performed on our way to a student-begins-here state.
		var matched = _match(selection, action, input, vt);
		//ctatdebug("CTATVectorMatcher --> in match: Matched = " + matched);

		if(matched === false)
		{
			//ctatdebug("CTATVectorMatcher --> in match: is returning false");
			return false;
		}

		if(!(that.matchActor(actor)))
		{
			//ctatdebug("CTATVectorMatcher --> in match: is returning false, second if condition");
			return false;
		}

		//ctatdebug("CTATVectorMatcher --> in match: is returning true");

		return true;
	};

	/**
	 * @param vector (of type integer)- can be either SELECTION, ACTION, or INPUT
	 * @return array of CTATMatchers
	 */
	this.getMatchers = function(vector)
	{
		//ctatdebug("CTATVectorMatcher --> in getMatchers");
		return matchers[vector];
	};

	/**
	 * Return an SAI appropriate for a tutor_message response.
	 * @param {CTATSAI} studentSAI
	 * @param {CTATVariableTable} vt for matchConcatenation
	 * @param {string} grade one of CTATExampleTracerLink.CORRECT_ACTION, .BUGGY_ACTION, .FIREABLE_BUGGY_ACTION
	 * @return {CTATSAI} SAI matching some or all of studentSAI
	 */
	this.getTutorSAI = function(studentSAI, vt, grade)
	{
		that.ctatdebug("CTATVectorMatcher.getTutorSAI("+studentSAI+", vt, "+grade+")");

		if(grade && CTATExampleTracerLink.CORRECT_ACTION.toLowerCase() == grade.toLowerCase())
			return studentSAI;
		var details = [];
		var m = _match(studentSAI.getSelectionArray(), studentSAI.getActionArray(), studentSAI.getInputArray(), vt, details);
		if(m)
		{                                     // only on successful match, maybe replace input
			var result = studentSAI.clone();
			var replacedInput = that.evaluateReplacement(studentSAI, vt, null);
			if(replacedInput != null)
			{
				result.setInput(typeof(replacedInput) == "object" ? replacedInput.toString() : replacedInput);
			}
			return result;
		}
		var result = studentSAI.clone();
		for(var d = 0; d < details.length; d++)
		{
			switch(d)
			{
				case CTATMatcher.SELECTION:
					if(details[d])
						continue;
					result.setSelection(that.getDefaultSelection());
					result.setSelectionArray(that.getDefaultSelectionArray());
					break;
				case CTATMatcher.ACTION:
					if(details[d])
						continue;
					result.setAction(that.getDefaultAction());
					result.setActionArray(that.getDefaultActionArray());
					break;
				case CTATMatcher.INPUT:
					if(details[d])
						continue;
					result.setInput(that.getDefaultInput());
					result.setInputArray(that.getDefaultInputArray());
					break;
				default:
					console.log("CTATVectorMatcher.getTutorSAI() unexpected no. of details: "+d);
			}
		}
		return result;
	};

	/**
	 * @return string
	 */
	this.getInputMatcher = function()
	{
		//ctatdebug("CTATVectorMatcher --> in getInputMatcher");

		var inputMatcher = inputMatchers[0];

		if(inputMatcher instanceof CTATExactMatcher && inputMatcher.toString() === "".toString())
		{
			//ctatdebug("CTATVectorMatcher --> in getInputMatcher returning defaultInput");
			return that.getDefaultInput();
		}
		else
		{
			//ctatdebug("CTATVectorMatcher --> in getInputMatcher returning inputMatcher");
			return inputMatcher.toString();
		}
	};

	/**
	 * @return string
	 */
	this.getActionMatcher = function()
	{
		//ctatdebug("CTATVectorMatcher --> in getActionMatcher");

		var actionMatcher = actionMatchers[0];

		if(actionMatcher instanceof CTATExactMatcher && actionMatcher.toString() === "".toString())
		{
			//ctatdebug("CTATVectorMatcher --> in getActionMatcher returning getDefaultAction");
			return that.getDefaultAction();
		}
		else
		{
			//ctatdebug("CTATVectorMatcher --> in getActionMatcher returning actionMatcher");
			return actionMatcher.toString();
		}
	};

	/**
	 * @return string
	 */
	this.getSelectionMatcher = function()
	{
		//ctatdebug("CTATVectorMatcher --> in getSelectionMatcher");

		var selectionMatcher = selectionMatchers[0];

		if(selectionMatcher instanceof CTATExactMatcher && selectionMatcher.toString() === "".toString())
		{
			//ctatdebug("CTATVectorMatcher --> in getActionMatcher returning getDefaultSelection");
			return that.getDefaultSelection();
		}
		else
		{
			//ctatdebug("CTATVectorMatcher --> in getActionMatcher returning selectionMatcher");
			return selectionMatcher.toString();
		}
	};

    /**
     * Return one of the internal matchers.
     * @param which (of type string): one of "selection", "action", "input"
     * @return object of type CTATMatcher: the 0th matcher for the requested SAI element
     */
	this.getSingleMatcher = function(which)
	{
		//ctatdebug("CTATVectorMatcher --> in getSingleMatcher");

		if("selection".toString().toUpperCase() === which.toString().toUpperCase())
		{
			return selectionMatchers[0];
		}
		else if("action".toString().toUpperCase() === which.toString().toUpperCase())
		{
			return actionMatchers[0];
		}
		else
		{
			return inputMatchers[0];
		}

		//ctatdebug("CTATVectorMatcher --> out of getSingleMatcher");
	};

	/**
	 * Override CTATMatcher.getDefaultSAI to get individual matchers' defaults.
	 * @return {CTATSAI} defaultSAI
	 */
	var superclassGetDefaultSAI = that.getDefaultSAI;
	this.getDefaultSAI = function()
	{
		var df = superclassGetDefaultSAI.apply(that);
		var s = (selectionMatchers.length > 0 && selectionMatchers[0] instanceof CTATExactMatcher ? selectionMatchers[0].getSingle() : df.getSelection());
		var a = (actionMatchers.length > 0 && actionMatchers[0] instanceof CTATExactMatcher ? actionMatchers[0].getSingle() : df.getAction());
		var i = (inputMatchers.length > 0 && inputMatchers[0] instanceof CTATExactMatcher ? inputMatchers[0].getSingle() : df.getInput());
		return new CTATSAI(s, a, i);
	};

	/**
	 * @return string
	 */
	this.getSelection = function()
	{
		//ctatdebug("CTATVectorMatcher --> in getSelection");

		if(selectionMatchers.length === 0)
		{
			//ctatdebug("CTATVectorMatcher --> in getSelection if branch");
			return that.getDefaultSelection();
		}
		else
		{
			//ctatdebug("CTATVectorMatcher --> in getSelection else branch");
			//ctatdebug("CTATVectorMatcher --> in getSelection selectionMatchers[0]: " +  (selectionMatchers[0] instanceof CTATMatcher));
			//ctatdebug("CTATVectorMatcher --> in getSelection selectionMatchers[0]: " + (selectionMatchers[0] instanceof CTATVectorMatcher));
			//ctatdebug("CTATVectorMatcher --> in getSelection selectionMatchers[0]: " + (selectionMatchers[0] instanceof CTATExactMatcher));
			return selectionMatchers[0].toString();
		}

		//ctatdebug("CTATVectorMatcher --> out of getSelection");
	};

	/**
	 * @return string
	 */
	this.getAction = function()
	{
		//ctatdebug("CTATVectorMatcher --> in getAction");

		if(actionMatchers.length === 0)
		{
			return that.getDefaultAction();
		}
		else
		{
			return actionMatchers[0].getAction();
		}

		//ctatdebug("CTATVectorMatcher --> out of getAction");
	};

	/**
	 * @param {string} givenDefaultSelection The defaultSelection to set
	 * @return {undefined}
	 */
	this.setDefaultSelection = function(givenDefaultSelection)
	{
		if(selectionMatchers.length === 0)
		{
			return that.internalSetDefaultSelection(givenDefaultSelection);
		}
		else
		{
			return selectionMatchers[0].setDefaultSelection(givenDefaultSelection);
		}
	};

	/**
	 * @param {string} givenDefaultAction The defaultAction to set
	 * @return {undefined}
	 */
	this.setDefaultAction = function(givenDefaultAction)
	{
		if(actionMatchers.length === 0)
		{
			return that.internalSetDefaultAction(givenDefaultAction);
		}
		else
		{
			return actionMatchers[0].setDefaultAction(givenDefaultAction);
		}
	};

	/**
	 * @param {string} givenDefaultInput the default input to set
	 * @return {undefined}
	 */
	this.setDefaultInput = function(givenDefaultInput)
	{
		if(inputMatchers.length === 0)
		{
			return that.internalSetDefaultInput(givenDefaultInput);
		}
		else
		{
			return inputMatchers[0].setDefaultInput(givenDefaultInput);
		}
	};

	/**
	 * Get the value of a formula calculation specified for the input element.
	 * Override this implementation in CTATExpressionMatcher to return the
	 * result of the formula.
	 * @param {CTATSAI} givenSAI sai for evaluation
	 * @param {CTATVariableTable} vt for evaluation
	 * @return string: result of getInput() except for Expression Matcher with == operator
	 */
	this.getEvaluatedInput = function(givenSAI, vt)
	{
		//ctatdebug("CTATVectorMatcher --> in getEvaluatedInput");

		if(inputMatchers.length === 0)
		{
			return that. getInput();
		}

		var inputMatcher = inputMatchers[0];

		if((inputMatcher instanceof CTATExpressionMatcher) === false)
		{
			return that.getInput();
		}

		//no need to cast like in Java

		if(inputMatcher.isEqualRelation() === true)
		{
			return inputMatcher.getEvaluatedInput(givenSAI, vt);
		}
		else
		{
			return that.getInput();
		}

		//ctatdebug("CTATVectorMatcher --> out of getEvaluatedInput");
	};

	/**
	 * Method used to match hints in an example tracer tutor
	 * @param selection of type array
	 * @param action of type array
	 * @param actor of type string
	 * @param vt of type CTATVariableTable
	 * @return boolean
	 */
	this.matchForHint = function(selection, action, actor, vt)
	{
		ctatdebug("CTATVectorMatcher.matchForHint("+selection+", "+action+", "+actor+", vt)");

		if(!(that.matchActor(actor)))
		{
			return false;
		}

		var selectionsMatch = false; //of type boolean

		//concat is always true

		var sm = selectionMatchers[0];

		if(Array.isArray(selection))
		{
			selectionsMatch = sm.matchConcatenation(selection, that.getDefaultActionArray(), that.getDefaultInputArray(), vt);
		}
		else
		{
			selectionsMatch = sm.matchConcatenation([selection], that.getDefaultActionArray(), that.getDefaultInputArray(), vt);
		}

		//ctatdebug("CTATVectorMatcher --> out of matchForHint");
		return selectionsMatch;
	};

	/**
	 * Override to set case sensitivity in each member matcher.
	 * @param {givenCaseInsensitive} of type boolean
	 * @return {undefined}
	 */
	this.setCaseInsensitive = function(givenCaseInsensitive)
	{
		ctatdebug("CTATVectorMatcher.setCaseInsensitive("+givenCaseInsensitive+")");
		for(var m = 0; m < matchers.length; ++m)
		{
			if(Array.isArray(matchers[m]))
			{
				for(var v = 0; v < (matchers[m]).length; ++v)
				{
					((matchers[m])[v]).setCaseInsensitive(givenCaseInsensitive);
				}
		 	}
			else if(matchers[m] instanceof CTATMatcher)
			{
				(matchers[m]).setCaseInsensitive(givenCaseInsensitive);
			}
		}
	};

	/**
	 * @return a string object
	 */
	this.toString = function()
	{
		return "this is CTATVectorMatcher";
	};

	/**
	 * Override returns CTATMatcher.getReplacementFormula() for inputMatchers[0].
	 * @return string: null if inputMatchers[0] is null or undefined; else that Matcher's result.
	 */
	this.getReplacementFormula = function()
	{
		//ctatdebug("CTATVectorMatcher --> in getReplacementFormula");

		var inputMatcher = inputMatchers[0];

		if(inputMatcher === null || typeof(inputMatcher) === 'undefined')
		{
			return null;
		}

		//ctatdebug("CTATVectorMatcher --> out of getReplacementFormula");
		return inputMatcher.getReplacementFormula();
	};

	/**
	 * Execute a replace-input-with formula and return its result.
	 * @param {CTATSAI} sai
	 * @param {CTATVariableTable} vt
	 * @param {CTATExampleTracerTracer} tracer
	 * @return {string} result from input matcher's evaluateReplacement method
	 */
	this.evaluateReplacement = function(sai, vt, tracer)
	{
		var im = inputMatchers[0];
		return im.evaluateReplacement(sai, vt, tracer);
	};

/****************************** PUBLIC METHODS ****************************************************/

/****************************** CONSTRUCTOR CALLS ****************************************************/

	//call to the constructor to set the actor
	this.setActor(actor);
};

//setting up inheritance from CTATMatcher
CTATVectorMatcher.prototype = Object.create(CTATMatcher.prototype);
CTATVectorMatcher.prototype.constructor = CTATVectorMatcher;

if(typeof module !== 'undefined')
{
	module.exports = CTATVectorMatcher;
}