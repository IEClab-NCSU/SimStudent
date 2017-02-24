/* This object represents an CTATExpressionMatcher */

goog.provide('CTATExpressionMatcher');
goog.require('CTATExampleTracerException');
goog.require('CTATExactMatcher');

/* LastModify: sewall 2014/10/31 */

/**
 * @constructor
 * @param {integer} givenVectorector s a or i
 * @param {string} givenText The matcher's toString()
 */
CTATExpressionMatcher = function(givenVector, givenText)
{
	//calling the constructor fo the super class from where we are inheriting
	CTATExactMatcher.call(this, givenVector, givenText);

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/
	

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

    /**
     * Default
     * @type {integer}
     */
	var relation = CTATExpressionMatcher.EQ_RELATION;

    /**
     * @type {String}
     */
	var expression = null;

	/**
	 * @type {CTATFormulaParser}
	 */
	var functions = null;

	/**
	 * @type {String}
	 */
	var lastInput = null;

	/**
	 * @type {String}
	 */
	var lastError = null;

	/**
	 * @type {Date}
	 */
	var lastEvaluationTime = null;

	/**
	 * @type {boolean}
	 */
	var lastComparison = false;

	/**
	 * @type {Object}
	 */
	var lastResult = null;

	/**
     * Make the object available to private methods
     */
	var that = this;

/***************************** PRIVATE METHODS *****************************************************/

	/**
	 * @private
	 */
	function initExpression()
	{
		if(givenText !== null && typeof(givenText) !== 'undefined')
		{
			var gExpression; //of type string
			var beginQuote = givenText.indexOf("\""); //of type int
			var endQuote = givenText.lastIndexOf("\""); //of type int

			if(beginQuote >= 0 && endQuote >= 0)
			{
				gExpression = givenText.substring(beginQuote + 1, endQuote);
				var testRel = givenText.substring(0, givenText.indexOf(" "));

				for(var i = 0; i < CTATExpressionMatcher.RELATIONS.length; i++)
				{
					if(testRel === CTATExpressionMatcher.RELATIONS[i])
					{
						relation = i;
						break;
					}
				}
			}
			else
			{
				gExpression = givenText;
			}

			that.setInputExpression(gExpression);
		}	
	}

	/**
     * 
     * @param {String} selection
     * @param {String} action
     * @param {String} input
     * @param {integer} vector
     * @param {CTATVariableTable} vt proper variable table for evaluate(String, String, String, VariableTable)
     * @return {boolean}
     */
     function testVector(selection, action, input, vector, vt)
     {
     	var result = that.evaluate(selection, action, input, vt);

     	var comparee = null; // of type string

     	switch(vector)
     	{
     		case CTATMatcher.SELECTION:
     			comparee = selection;
     			break;
     		case CTATMatcher.ACTION:
     			comparee = action;
     			break;
     		case CTATMatcher.INPUT:
     		case CTATMatcher.NON_SINGLE:
     			comparee = input; //the old default
     	}

     	lastResult = result;
     	lastInput = comparee; //this is used in the old ExpressionMatcherPanel
     	lastEvaluationTime = new Date();

     	try
     	{
     		if(relation === CTATExpressionMatcher.EQ_RELATION || relation === CTATExpressionMatcher.NOT_EQ_RELATION)
     		{
     			var rtnVal = CTATFormulaFunctions.equals(comparee, result);
     			return (relation === CTATExpressionMatcher.NOT_EQ_RELATION ? !rtnVal : rtnVal);
     		}
     		
     		if(relation === CTATExpressionMatcher.BOOL_RELATION)
     		{
     			//Note: checks only type "boolean", "Boolean" type NOT checked here
     			if(typeof(result) === 'boolean')
				{
 					return result; //Fran: check if boolean
 				}
				else if(result.toString().toLowerCase() === "true")
				{
    				return true; //Fran: check if string and parse true
    			}
				else if (result.toString().toLowerCase() === "false")
				{
    				return false; //Fran: check if string and parse false
    			}
     			else
     			{
     				return false; //Fran: Otherwise return false
     			}
     		}

     		var resultVal = Number(result); //of type double or NaN
     		var compareeVal = Number(comparee); //of type double or NaN

     		if(isNaN(resultVal) === false && isNaN(compareeVal) === false)
     		{
     			switch(relation)
     			{
     				case CTATExpressionMatcher.EQ_RELATION:
     					return (compareeVal === resultVal);
     				case CTATExpressionMatcher.NOT_EQ_RELATION:
     					return (compareeVal !== resultVal);
     				case CTATExpressionMatcher.LT_RELATION:
     					return (compareeVal < resultVal);
     				case CTATExpressionMatcher.GT_RELATION:
     					return (compareeVal > resultVal);
            		case CTATExpressionMatcher.LTE_RELATION:
     					return (compareeVal <= resultVal);
     				case CTATExpressionMatcher.GTE_RELATION:
     					return (compareeVal >= resultVal);
    
     			}
     		}

     		if(comparee === null || typeof(comparee) === 'undefined')
     		{
     			switch(relation)
     			{
     				case CTATExpressionMatcher.EQ_RELATION:
     					return (result === null || typeof(result) === 'undefined');
     				case CTATExpressionMatcher.NOT_EQ_RELATION:
     					return (result !== null && typeof(result) !== 'undefined');
     				case CTATExpressionMatcher.LT_RELATION:
     					return false;
     				case CTATExpressionMatcher.GT_RELATION:
     					return false;
            case CTATExpressionMatcher.LTE_RELATION:
     					return (result === null || typeof(result) === 'undefined');
     				case CTATExpressionMatcher.GTE_RELATION:
     					return (result === null || typeof(result) === 'undefined');
    
     			}
     		}

     		if(result === null || typeof(result) === 'undefined')
     		{
     			return false;
     		}
     		var comparison = (comparee.toString() === result.toString());

     		switch(relation)
     		{
     			case CTATExpressionMatcher.EQ_RELATION:
     				return (comparison === 0);
     			case CTATExpressionMatcher.NOT_EQ_RELATION:
     				return (comparison !== 0);
     			case CTATExpressionMatcher.LT_RELATION:
     				return (comparison < 0);
     			case CTATExpressionMatcher.GT_RELATION:
     				return (comparison > 0);
            	case CTATExpressionMatcher.LTE_RELATION:
     				return (comparison < 0 || CTATFormulaFunctions.equals(comparee, result));
     			case CTATExpressionMatcher.GTE_RELATION:
     				return (comparison > 0 || CTATFormulaFunctions.equals(comparee, result));
            }
     	}
     	catch(e)
     	{
     		//ctatdebug("CTATExpressionMatcher --> e.name + ': ' + e.message);
			lastError = e.message;
			return null;
     	}

     	return false;
     }
/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * @return {boolean}
	 */
	this.isEqualRelation = function()
	{
		//ctatdebug("CTATExpressionMatcher --> in isEqualRelation");
		return (relation === CTATExpressionMatcher.EQ_RELATION);
	};

	/**
	 * @return {string} LastResult as a String
	 */
	this.getEvaluatedInput = function()
	{
		//ctatdebug("CTATExpressionMatcher --> in getEvaluatedInput");

		if(that.getLastResult() === null || typeof(that.getLastResult()) === 'undefined')
		{
			return "";
		}
		else
		{
			return that.getLastResult().toString();
		}

		//ctatdebug("CTATExpressionMatcher --> out of getEvaluatedInput");
	};

	/**
	 * This is a String representation of the expression matcher
	 * @return {string}
	 */
	this.toString = function()
	{
		//ctatdebug("CTATExpressionMatcher --> in toString");
	
		return CTATExpressionMatcher.RELATIONS[relation] + " \"" + expression + "\"";
	};

	/**
	 * The only way to implement this is if we had access to the selection, action, and input vectors originally ...
	 * @param {string} s
	 * @return {boolean} 
	 */
	this.matchSingle = function(s)
	{
        //ctatdebug("CTATExpressionMatcher --> in matchSingle: s = " + s + " singleval = " + singleValue);
		
		throw new CTATExampleTracerException("UnsupportedOperationException");
	};

	/**
	 * @param {string} value 
	 * @return {undefined}
	 */
	this.setInputExpression = function(value)
	{
		expression = value;
	};

	/**
	 * @return Object
	 * @param {String} expression
	 * @param {String} selection
	 * @param {String} action
	 * @param {String} input
	 * @return {Object}
	 */
	this.interpolate = function(expression, selection, action, input)
	{
		try
		{
			return functions.interpolate(expression, selection, action, input);
		}
		catch(e)
		{
			//ctatdebug("CTATExpressionMatcher --> e.name + ': ' + e.message);
			lastError = e.message;
			return null;
		}

	};

    /**
     * @param {String} selection student's selection
     * @param {String} action student's action
     * @param {String} input student's input
     * @param {CTATVariableTable} vt if !null then given interp's vt, otherwise problem model(bestinterp)'s vt/
     * @return {Object} string, double, int, etc., that corresponds to the parsed version of either
     * the students selection action or input in the context of the interp's VT.
     */
	this.evaluate = function(selection, action, input, vt)
	{
		var tempfunc = null; //of type CTATFormulaParser

		if(vt !== null && typeof(vt) !== 'undefined')
		{
			tempfunc = new CTATFormulaParser(vt);
		}
		else
		{
			tempfunc = functions;
		}

		try
		{
			return tempfunc.evaluate(that.getInputExpression(), selection, action, input);
		}
		catch(e)
		{
			//ctatdebug("CTATExpressionMatcher --> e.name + ': ' + e.message);
			lastError = e.message;
			return null;
		}
	};

	/**
	 * @param {Array} s
	 * @param {Array} a
	 * @param {Array} i
	 * @param {CTATVariableTable} vt
	 * @return {boolean}
	 */
	this.matchConcatenation = function(s, a, i, vt)
	{
		//covers the function overloaded with boolean as parmeter case
		if(a === null || typeof(a) === 'undefined' || i === null || typeof(i) === 'undefined')
		{
			lastComparison = testVector(s[0], null, null, vector, vt);
		}
		else
		{
			lastComparison = testVector(s[0], a[0], i[0], vector, vt);
		}
		
		return lastComparison;
	};

	/**
	 * @param {array} selection
	 * @param {Array} action
	 * @param {array} input
	 * @param {string} actor
	 * @param {CTATVariableTable} vt
	 * @return {boolean}
	 */
	this.match = function(selection, action, input, actor, vt)
	{
		return that.matchConcatenation(selection, action, input, vt);
	};

	/**
	 * return {String}
	 */
	this.getInputExpression = function()
	{
		return expression;
	};
/****************************** CONSTRUCTOR METHODS ****************************************************/

	//method called as part of the constructor
	initExpression();
};

/**************************** CONSTANTS ******************************************************/

/**
 * 
 */
Object.defineProperty(CTATExpressionMatcher, "EQ_RELATION", {enumerable: false, configurable: false, writable: false, value: 0});
Object.defineProperty(CTATExpressionMatcher, "LT_RELATION", {enumerable: false, configurable: false, writable: false, value: 1});
Object.defineProperty(CTATExpressionMatcher, "GT_RELATION", {enumerable: false, configurable: false, writable: false, value: 2});
Object.defineProperty(CTATExpressionMatcher, "NOT_EQ_RELATION", {enumerable: false, configurable: false, writable: false, value: 3});
Object.defineProperty(CTATExpressionMatcher, "GTE_RELATION", {enumerable: false, configurable: false, writable: false, value: 4});
Object.defineProperty(CTATExpressionMatcher, "LTE_RELATION", {enumerable: false, configurable: false, writable: false, value: 5});
Object.defineProperty(CTATExpressionMatcher, "BOOL_RELATION", {enumerable: false, configurable: false, writable: false, value: 6});

/**
 * 
 */
Object.defineProperty(CTATExpressionMatcher, "RELATIONS", {enumerable: false, configurable: false, writable: false, value: ["=", "<", ">", "!=", ">=", "<=", "boolean"]});

//setting up inheritance
CTATExpressionMatcher.prototype = Object.create(CTATExactMatcher.prototype);
CTATExpressionMatcher.prototype.constructor = CTATExpressionMatcher;

if(typeof module !== 'undefined')
{
	module.exports = CTATExpressionMatcher;
}