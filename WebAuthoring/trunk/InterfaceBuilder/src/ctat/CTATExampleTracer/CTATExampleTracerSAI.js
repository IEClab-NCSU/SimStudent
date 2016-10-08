/* This object represents an CTATExampleTracerSAI */
/* LastModify: FranceskaXhakaj 06/20/14*/

/*
 * @param givenSelectionArray of type array of strings
 * @param givenActionArray of type array of strings
 * @param givenInputArray of type array of strings
 * @param givenActor of type string
 */
function CTATExampleTracerSAI(givenSelectionArray, givenActionArray, givenInputArray, givenActor) 
{

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/


/**************************** PRIVATE INSTACE VARIABLES ******************************************************/
	//we check if each of the arrays has been defined
	//we do a shallow copy
	var selection = (typeof(givenSelectionArray) === 'undefined' || givenSelectionArray === null ? null : givenSelectionArray.slice()); 
	var input = (typeof( givenInputArray) === 'undefined' || givenInputArray === null ? null :  givenInputArray.slice());
	var action = (typeof(givenActionArray) === 'undefined' || givenActionArray === null ? null : givenActionArray.slice());
	var actor;
	
	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/



/***************************** PRIVILEDGED METHODS *****************************************************/

	/*
	 * @return a string or null
	 */
	this.getSelectionAsString = function ()
	{
		//NOTE to self: this can never be undefined, we check before 
		if(typeof(selection) === 'undefined' || selection === null || selection.length < 1)
		{
			return null;
		}

		var obj = selection[0];

		return (typeof(obj) === 'undefined' || obj === null ? null : obj);
	};

	/*
	 * @return an array of strings
	 */
	this.getSelectionAsArray = function ()
	{
		return selection;
	};

	/*
	 * @return a string or null
	 */
	this.getActionAsString = function ()
	{
		//NOTE to self: this can never be undefined, we check before 
		if(typeof(action) === 'undefined' || action === null || action.length < 1)
		{
			return null;
		}

		var obj = action[0];

		return (typeof(obj) === 'undefined' || obj === null ? null : obj);
	};

	/*
	 * @return an array of strings
	 */
	this.getActionAsArray = function ()
	{
		return action;
	};

	/*
	 * @return a string
	 */
	this.getInputAsString = function ()
	{
		//NOTE to self: this can never be undefined, we check before 
		if(typeof(input) === 'undefined' || input === null || input.length < 1)
		{
			return null;
		}

		var obj = input[0];

		return (typeof(obj) === 'undefined' || obj === null ? null : obj);
	};

	/*
	 * @return an array of strings
	 */
	this.getInputAsArray = function ()
	{
		return input;
	};

	/*
	 * @return a string
	 */
	this.getActor = function ()
	{
		return actor;
	};

/****************************** PUBLIC METHODS ****************************************************/

}
