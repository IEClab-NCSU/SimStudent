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

	var selectionMatchers = sMatchers; //array of CTATMatcher
	var actionMatchers = aMatchers; //array of CTATMatcher
	var inputMatchers = iMatchers; //array of CTATMatcher

	var matchers = []; //array of CTATMatcher
	matchers[CTATMatcher.SELECTION] = selectionMatchers;
	matchers[CTATMatcher.ACTION] = actionMatchers;
	matchers[CTATMatcher.INPUT] = inputMatchers;

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/

	 /**
	  * Performs a match on each of selection, action, and input using the specified matchers
	  * for each (concatenation or by elements)
	  * @param selection of type array
	  * @param action of type array
	  * @param input of type array
	  * @param vt of type CTATVariableTable
	  * @return boolean: whether link matches or not
	  */
	function _match(selection, action, input, vt)
	{
		//ctatdebug("CTATVectorMatcher --> in _match");

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
			//ctatdebug("CTATVectorMatcher --> in _match in for loop: " + i);
			var m = matchers[i][0];

			//ctatdebug("CTATVectorMatcher --> in _match before concat m" + (m === null||m === undefined));
			var t = m.matchConcatenation(values[i]);
			//ctatdebug("CTATVectorMatcher --> in _match after concat returns: " + t);

			if(t === false)
			{
				//ctatdebug("CTATVectorMatcher --> in _match returned false");
				return false;
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

		if(actor === null || typeof(actor) === 'undefined' || (actor.toString().toUpperCase() !== that.getDefaultActor().toString().toUpperCase() && CTATMsgType.ANY_ACTOR.toString().toUpperCase() !== that.getDefaultActor().toString().toUpperCase()))
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
			return actionMatchers[0].toString();
		}

		//ctatdebug("CTATVectorMatcher --> out of getAction");
	};

	/**
	 * Get the value of a formula calculation specified for the input element.
	 * Override this implementation in CTATExpressionMatcher to return the
	 * result of the formula.
	 * @return string: result of getInput() 
	 */
	this.getEvaluatedInput = function()
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
			return inputMatcher.getEvaluatedInput();
		}
		else
		{
			return that.getInput();
		}

		//ctatdebug("CTATVectorMatcher --> out of getEvaluatedInput");
	};

	/**
	 * @return string
	 */
	this.getInput = function()
	{
		//ctatdebug("CTATVectorMatcher --> in getInput");

		if(inputMatchers.length === 0)
		{
			return that.getDefaultInput();
		}
		else
		{
			return inputMatchers[0].toString();
		}		

		//ctatdebug("CTATVectorMatcher --> out of getInput");
	};

	/**
	 * Override returns CTATMatcher.replaceInput() for inputMatchers[0].
	 * @return boolean: false if inputMatchers[0] is null or undefined; else that Matcher's result.
	 */
	this.replaceInput = function()
	{
		//ctatdebug("CTATVectorMatcher --> in replaceInput");

		var inputMatcher = inputMatchers[0];

		if(inputMatcher === null || typeof(inputMatcher) === 'undefined')
		{
			return false;
		}

		//ctatdebug("CTATVectorMatcher --> out of replaceInput");
		return inputMatcher.replaceInput();
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
	 * Method used to match hints in an example tracer tutor
	 * @param selection of type array
	 * @param action of type array
	 * @param actor of type string
	 * @param vt of type CTATVariableTable
	 * @return boolean
	 */
	this.matchForHint = function(selection, action, actor, vt)
	{
		//ctatdebug("CTATVectorMatcher --> in matchForHint");
		
		if(that.matchActor(actor) === false)
		{
			return false;
		}

		var selectionsMatch = false; //of type boolean
		var actionsMatch = true; //of type boolean

		//concat is always true

		var sm = selectionMatchers[0];
		var am = actionMatchers[0];

		if(sm instanceof CTATExpressionMatcher)
		{
			//temporary....
		}
		else if(selection !== null && typeof(selection) !== 'undefined')
		{
			selectionsMatch = sm.matchSingle(that.array2ConcatString(selection));
			
			if(action !== null && typeof(action) !== 'undefined')
			{
				actionsMatch = am.matchSingle(that.array2ConcatString(action));
			}
		}
		else
		{
			return false;
		}

		//ctatdebug("CTATVectorMatcher --> out of matchForHint");
		return (selectionsMatch && actionsMatch);
	};

	/**
	 * @return a string object
	 */
	this.toString = function()
	{
		return "this is CTATVectorMatcher";
	};

/****************************** PUBLIC METHODS ****************************************************/

/****************************** CONSTRUCTOR CALLS ****************************************************/

	//call to the constructor to set the actor
	this.setDefaultActor(actor);
}

//setting up inheritance from CTATMatcher
CTATVectorMatcher.prototype = Object.create(CTATMatcher.prototype);
CTATVectorMatcher.prototype.constructor = CTATVectorMatcher;

if(typeof module !== 'undefined')
{
	module.exports = CTATVectorMatcher;
}