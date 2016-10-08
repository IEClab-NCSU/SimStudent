/* This object represents an CTATExampleTracerSAI */

goog.provide('CTATExampleTracerSAI');
goog.require('CTATBase');
goog.require('CTATMsgType');

/* LastModify: FranceskaXhakaj 07/14*/

/**
 * Constructor which takes the parameters as arrays.
 * @constructor
 * @param {array of strings} givenSelectionArray
 * @param {array of strings} givenActionArray
 * @param {array of strings} givenInputArray
 * @param {string} givenActor
 */
CTATExampleTracerSAI = function(givenSelectionArray, givenActionArray, givenInputArray, givenActor) 
{
	//calling the constructor of the object we are inheriting from: CTATBase
	CTATBase.call(this, "CTATExampleTracerSAI", "ExampleTracerSAI");

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	/**
	 * We check if the array has been defined and we do a shallow copy
     * @type {array}
     */
	var selection = (typeof(givenSelectionArray) === 'undefined' || givenSelectionArray === null ? null : givenSelectionArray.slice());
	
	/**
	 * We check if the array has been defined and we do a shallow copy
     * @type {array}
     */
	var action = (typeof(givenActionArray) === 'undefined' || givenActionArray === null ? null : givenActionArray.slice());
	
	/**
	 * We check if the array has been defined and we do a shallow copy
     * @type {array}
     */
	var input = (typeof(givenInputArray) === 'undefined' || givenInputArray === null ? null :  givenInputArray.slice());

	/**
     * @type {string}
     */
	var actor = CTATMsgType.DEFAULT_ACTOR;
	
	/**
     * @type {boolean}
     */
	var hintRequest = null;
	
	/**
     * Make the object available to private methods
     */
	var that = this;

/***************************** PRIVATE METHODS *****************************************************/

/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * @return {string} Or null, the selection as a String
	 */
	this.getSelectionAsString = function ()
	{
		//ctatdebug("CTATExampleTracerSAI --> in getSelectionAsString");

		//NOTE to self: this can never be undefined, we check before 
		if(typeof(selection) === 'undefined' || selection === null || selection.length < 1)
		{
			return null;
		}

		var obj = selection[0];

		//ctatdebug("CTATExampleTracerSAI --> out of getSelectionAsString");

		return (typeof(obj) === 'undefined' || obj === null ? null : obj.toString());
	};

	/**
	 * @return {array of strings} The selection as an array
	 */
	this.getSelectionAsArray = function ()
	{
		//ctatdebug("CTATExampleTracerSAI --> in getSelectionAsArray");

		return selection;
	};

	/**
	 * @return {string} Or null, the action as a String
	 */
	this.getActionAsString = function ()
	{
		//ctatdebug("CTATExampleTracerSAI --> in getActionAsString");

		//NOTE to self: this can never be undefined, we check before 
		if(typeof(action) === 'undefined' || action === null || action.length < 1)
		{
			return null;
		}

		var obj = action[0];

		//ctatdebug("CTATExampleTracerSAI --> out of getActionAsString");

		return (typeof(obj) === 'undefined' || obj === null ? null : obj.toString());
	};

	/**
	 * @return {array of strings} The action as an array
	 */
	this.getActionAsArray = function ()
	{
		//ctatdebug("CTATExampleTracerSAI --> in getActionAsArray");

		return action;
	};

	/**
	 * @return {string} The input as a String
	 */
	this.getInputAsString = function ()
	{
		//ctatdebug("CTATExampleTracerSAI --> in getInputAsString");

		//NOTE to self: this can never be undefined, we check before 
		if(typeof(input) === 'undefined' || input === null || input.length < 1)
		{
			return null;
		}

		var obj = input[0];

		//ctatdebug("CTATExampleTracerSAI --> out of getInputAsString");

		return (typeof(obj) === 'undefined' || obj === null ? null : obj.toString());
	};

	/**
	 * @return {array of strings} The input as an array
	 */
	this.getInputAsArray = function ()
	{
		//ctatdebug("CTATExampleTracerSAI --> in getInputAsArray");

		return input;
	};

	/**
	 * @return {string}
	 */
	this.getActor = function ()
	{
		//ctatdebug("CTATExampleTracerSAI --> in getActor");

		return actor;
	};

	/**
	 * Copy this instance.
	 * @return {CTATExampleTracerSAI}
	 */
	this.clone = function ()
	{
		//ctatdebug("CTATExampleTracerSAI --> in clone");

		var s = (that.getSelectionAsArray() === null || typeof(that.getSelectionAsArray()) === 'undefined' ? null : that.getSelectionAsArray().slice()); //that is how we clona/copy an array in JavaScript
		var a = (that.getActionAsArray() === null || typeof(that.getActionAsArray()) === 'undefined' ? null : that.getActionAsArray().slice());
		var i = (that.getInputAsArray() === null || typeof(that.getInputAsArray()) === 'undefined' ? null : that.getInputAsArray().slice());

		var result = new CTATExampleTracerSAI(s, a, i, that.getActor());

		result.setHintRequest(hintRequest); //set the hint request of the created instance to the current value
		
		//ctatdebug("CTATExampleTracerSAI --> out of clone");

		return result;
	};

	/**
	 * Method created to set the hintRequest private variable.
	 * @param {boolean} givenHintRequest
	 * @return {undefined}
	 */
	this.setHintRequest = function(givenHintRequest)
	{
		//ctatdebug("CTATExampleTracerSAI --> in setHintRequest");

		hintRequest = givenHintRequest;
	};


	/**
	 * @param {string} givActor New value for actor
	 * @return {undefined}
	 */
	this.setActor = function(givActor)
	{
		//ctatdebug("CTATExampleTracerSAI --> in setActor");

		if((CTATMsgType.DEFAULT_TOOL_ACTOR).toString().toUpperCase() === givActor.toString().toUpperCase())
		{
			actor = CTATMsgType.DEFAULT_TOOL_ACTOR;
		}
		else if ((CTATMsgType.UNGRADED_TOOL_ACTOR).toString().toUpperCase() === givActor.toString().toUpperCase())
		{
			actor = CTATMsgType.UNGRADED_TOOL_ACTOR;
		}
		else if ((CTATMsgType.DEFAULT_STUDENT_ACTOR).toString().toUpperCase() === givActor.toString().toUpperCase())
		{
			actor = CTATMsgType.DEFAULT_STUDENT_ACTOR;
		}
		else
		{
			actor = CTATMsgType.DEFAULT_ACTOR;
		}

		//ctatdebug("CTATExampleTracerSAI --> out of setActor");
	};

	/**
	 * Sets the input with the given array
	 * @param {array} givenInput
	 * @return {undefined}
	 */
	this.setInput = function(givenInput)
	{
		//ctatdebug("CTATExampleTracerSAI --> in setInput");

		if(input === null || typeof(input) === 'undefined')
		{
			input = [];
		}
		else
		{
			input.length = 0; //clearing input
		}
		
		input.push(givenInput);

		//ctatdebug("CTATExampleTracerSAI --> out of setInput");
	};
   
    /**
     * Dump contents for //ctatdebugging.
     * @return {string} Concatenated results of selection.toString(),
	 * action, etc.
     */
    this.toString = function()
    {
    	var sb = "[";
    	sb += selection + ",";
    	sb += action + ",";
    	sb += input + ",";
    	sb += actor + "]";

    	return sb;
    };



/****************************** CONSTRUCTOR CALLS ****************************************************/
	
	//calling the setActor method as part of the constructor
    this.setActor(givenActor);

/****************************** OTHER METHODS ****************************************************/

   /**
    * Method used for message passing
    * @return {string} Of selection, action, input
    * @author: Martin's code, modified Dhruv
    */  
    this.toXMLString = function () 
    {
    	//ctatdebug("CTATExampleTracerSAI --> in toXMLString");

		var formatter="";

        formatter+="<Selection>";
		for(var i =0; i<selection.length;i++)
		{
			formatter+= "<value>"+selection[i]+"</value>";
		}
		formatter+="</Selection>";

        formatter+="<Action>";
		for(var j =0; j<action.length;j++)
		{
			formatter+= "<value>"+action[j]+"</value>";
		}
		formatter+="</Action>";

        formatter+="<Input>";
		for(var k =0; k<input.length;k++)
		{
			formatter+= "<value fmt=\"text\" name=\""+" "+"\" type=\""+"String"+"\">"+input[k]+"</value>";
		}
		formatter+="</Input>";

        //ctatdebug("CTATExampleTracerSAI --> out of toXMLString");
        return (formatter);
    };

    //Duplicate function names for compatibility in CommShell [dhruv]
	this.getSelection = this.getSelectionAsString;
	this.getAction = this.getActionAsString;
	this.getInput = this.getInputAsString;
};

//setting up the inheritance
CTATExampleTracerSAI.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerSAI.prototype.constructor = CTATExampleTracerSAI;

if(typeof module !== 'undefined')
{
	module.exports = CTATExampleTracerSAI;
} 