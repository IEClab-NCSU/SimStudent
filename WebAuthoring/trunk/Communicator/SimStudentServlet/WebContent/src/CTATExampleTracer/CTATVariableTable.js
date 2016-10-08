/* This object represents an CTATVariableTable */

goog.provide('CTATVariableTable');
goog.require('CTATBase');

/* LastModify: FranceskaXhakaj 07/14*/


/**
 * Clone method for the Object class.
 */
Object.prototype.clone = function()
{
	return this;
};


/**************************** GLOBAL VARIABLES ******************************************************/

/**
 * @global
 */
var CTATVariableTableCount = 0;

 /**
  * Constructor for a CTATVariableTable object.
  * @constructor
  */
CTATVariableTable = function() 
{
    CTATBase.call(this, "CTATVariableTable", "");

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	/**
     * @type {integer}
     */
	var instance = CTATVariableTableCount++;

	/**
	 * Map of keys and values.
     * @type {Map}
     */
	var vt = {};

	/**
     * Make the object available to private methods
     */
	var that = this;

/***************************** PRIVATE METHODS *****************************************************/


/**************************** PUBLIC INSTACE VARIABLES ******************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * Returns a copy of the table with a deep copy of the internal object.  
	 * Asimple recursive copy of all properties of those objects.
	 * @return {CTATVariableTable}
	 */
	this.clone = function()
	{
		//ctatdebug("CTATVariableTable --> in clone");
		
		var copy = new CTATVariableTable(); //new instance

		//mimicking the functionality of the private constructor that we have removed
		for(var key in vt)
		{
			if(vt.hasOwnProperty(key) === true)
			{
				copy.put(key, vt[key].clone()); //deep copy all the properties
			}
		}

		//ctatdebug("CTATVariableTable --> out of clone");

		return copy; //return the clone
	};

	/**
	 * Sets the value in the variable table at the end of an object chain 
	 * specified by the list of identifiers in varName.
	 * It needs to go through the list and make an object for the property 
	 * specified by each identifier if one is not already present. 
	 * The value will be set on the last property. 
	 * @param {String} varName
	 * @param {Object} value
	 * @return {undefined}
	 */
	 this.put = function(varName, value)
	 {
	 	//if the string has not dots
	 	if(varName.indexOf(".") < 0)
	 	{
	 		//adds if the property is not there
	 		//substitutes the value if the property is already there
	 		vt[varName] = value;
	 	}
	 	else
	 	{
	 		var splitStr = varName.split(".");

	 		for(var i = 0; i < splitStr.length - 1; i++)
	 		{
	 			if(typeof(vt[splitStr[i]]) === 'object' && vt[splitStr[i]] !== null)
	 			{
	 				continue;
	 			}
	 			else
	 			{
	 				vt[splitStr[i]] = {}; //even if it is a string/number/boolean we create a new object
	 			}
	 		}

	 		vt[splitStr[i]] =  value;
	 	}
	 };

	/**
	 * Returns the internal table as a JS object.
	 * @return {Map}
	 */
	this.getTable = function()
	{
		return vt;
	};

	/**
	 * Get the value, accounting for the fact that the varName could be 
	 * a string of identifiers separated by '.'.  The function goes through 
	 * the list and gets the successive values. 
	 * If some of the values are not defined it returns null
	 * @param {String} varName
	 * @return {Object}
	 */
	this.get = function(varName)
	{
		//if the string has not dots
	 	if(varName.indexOf(".") < 0)
	 	{
	 		if(vt[varName] === null || typeof(vt[varName]) === 'undefined')
	 		{
	 			return null;
	 		}
	 		else
	 		{
	 			return vt[varName];
	 		}
	 	}
	 	else
	 	{
	 		var splitStr = varName.split(".");

	 		for(var i = 0; i < splitStr.length - 1; i++)
	 		{
	 			if(vt[varName] === null || typeof(vt[splitStr[i]]) === 'undefined')
	 			{
	 				return null;
	 			}
	 			else
	 			{
	 				continue;
	 			}
	 		}

	 		if(vt[splitStr[i]] === null || typeof(vt[splitStr[i]]) === 'undefined')
	 		{
	 			return null;
	 		}
	 		else
	 		{
	 			return vt[splitStr[i]];
	 		}
	 	}
	};


/****************************** OLD METHODS ****************************************************/

	//old clone function
	/** 
	 * Method that clones the current instance and
	 * creates a new instance with the same key to value 
	 * mappings
	 * @return an object of type CTATVariableTable
	 */
	/*this.clone = function ()
	{
		//ctatdebug("CTATVariableTable --> in clone");
		
		var copy = new CTATVariableTable(); //new instance

		//mimicking the functionality of the private constructor that we have removed
		for(var key in vt)
		{
			if(vt.hasOwnProperty(key) === true)
			{
				copy.put(key, vt[key]); //copy all the properties
			}
		}

		//ctatdebug("CTATVariableTable --> out of clone");

		return copy; //return the clone
	};*/

	//old put function
	/**
	 * Map a key to a value in the current object
	 * @param key (of type string)
	 * @param value (of type object) 
	 * @return object: the previous value associated with the key 
	 * or null if there was no mapping of the key (according to the 
	 * Java API for the put method in HashMap)
	 */
	/*this.put = function(key, value)
	{
		//ctatdebug("CTATVariableTable --> in put: the (" + key + "," + value + ")");
	
		//NOTE: We are not using the model variable
		//the code belonging to that will be removed
		
		var returnVal = vt[key]; //we do that to mimic the return value of the put method in Java

		vt[key] = value;

		if(returnVal === null || typeof(returnVal) === 'undefined')
		{
			return null;
		}

		//ctatdebug("CTATVariableTable --> out of put");

		return returnVal; //we return the previous value associated with the key
	};*/

/****************************** PUBLIC METHODS ****************************************************/

};

    /**************************** CONSTANTS ******************************************************/

    /**
     * @param {String} serialVersionUID
     */
    Object.defineProperty(CTATVariableTable, "serialVersionUID", {enumerable: false, configurable: false, writable: false, value: "201403071830L"});



CTATVariableTable.prototype = Object.create(CTATBase.prototype);
CTATVariableTable.prototype.constructor = CTATVariableTable;

if(typeof module !== 'undefined')
{
	module.exports = CTATVariableTable;
} 