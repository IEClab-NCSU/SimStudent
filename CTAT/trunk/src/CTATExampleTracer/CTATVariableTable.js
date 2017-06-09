/* This object represents an CTATVariableTable */

goog.provide('CTATVariableTable');
goog.require('CTATBase');

/* LastModify: sewall 03/2015*/

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

	/**
	 * A simple recursive copy of all properties of the given object.
	 * @param obj object to clone
	 * @return the clone
     */
	function cloneObj(obj) {
		var copy = {}; //new instance
		for(var key in obj)
		{
			if(obj.hasOwnProperty(key) === true)
			{
			    if(typeof(obj[key]) !== 'object')
			    {
			        copy[key] = obj[key];           // copy primitive values
			    }
			    else
			    {
				    copy[key] = cloneObj(obj[key]); // deep copy all the properties
			    }
			}
		}
		return copy;
	}


/**************************** PUBLIC INSTACE VARIABLES ******************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * Override prototype's toString() for debugging with "{ name=value[, name=value...] }".
	 * @return {string} formatted dump of contents
	 */
	this.toString = function()
	{
		var result = "";
		for(var v in vt)
		{
			if(result.length > 0)
			{
				result = result + ", ";
			}
			result = result + v + "=" + vt[v];
		}
		return "{ " + result + " }";
	};

	/**
	 * Returns a copy of the table with a deep copy of the internal object.
	 * @return {CTATVariableTable}
	 */
	this.clone = function()
	{
		ctatdebug("CTATVariableTable --> in clone; this "+this+", this.getTable() "+this.getTable);

		var copy = new CTATVariableTable(); //new instance
		ctatdebug("CTATVariableTable --> in clone; copy "+copy+", copy.setTable() "+copy.setTable);

		copy.setTable(cloneObj(vt));

		ctatdebug("CTATVariableTable --> out of clone");
		return copy; //return the clone
	};

	/**
	 * Sets the value in the variable table at the end of an object chain
	 * specified by the list of identifiers in varName.
	 * It goes through the list of properties, which are separated by "."
	 * and makes and object for the property specified by each identifier
	 * if one is not already present. The value will be set on the last property.
	 * The '.' notation means the subsequent identifier needs to be a property of the previous one.
	 * @param {String} varName
	 * @param {Object} value
	 * @return {CTATVariableTable} it returns the entire CTATVariableTable object it is called on
	 */
	this.put = function(varName, value)
	{
		that.ctatdebug("CTATVariableTable.put("+varName+", "+value+") typeof value "+typeof(value));

		//temporary variable
		var currObject;

		if(!varName)
		{
			console.log("CTATVariableTable.put() warning: null or empty variable name '"+varName+"'");
		}
		else if(varName.indexOf(".") < 0) //if the string has no dots (i.e., a single property)
		{
			//adds if the property is not there
			//substitutes the value if the property is already there
			vt[varName] = value;
		}
		else
		{
			//splits the properties based on the "." and saves them into an array
			var splitStr = varName.split(".");

			//adding the first property and the only one that will be stored as a key in the variable table
			if(typeof(vt[splitStr[0]]) !== 'object' || vt[splitStr[0]] === null)
			{
					vt[splitStr[0]] = {}; //even if it is a string/number/boolean we create a new object
			}

			currObject = vt[splitStr[0]];

			for(var i = 1; i < splitStr.length - 1; i++)
			{
				if(typeof(currObject[splitStr[i]]) === 'object' && currObject[splitStr[i]] !== null)
				{
					continue;
				}
				else
				{
					currObject[splitStr[i]] = {}; //even if it is a string/number/boolean we create a new object
				}

				currObject = currObject[splitStr[i]];
			}

			currObject[splitStr[i]] = value;
		}

		//following the Map in JS, return the entire CTATVariableTable object
		return that;
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
	 * Replace the internal table vt.
	 * @param newVt replacement for our vt map
	 */
	this.setTable = function(newVt)
	{
		vt = newVt;
	};

	/**
	 * Get the value, accounting for the fact that the varName could be
	 * a string of identifiers separated by '.'.  The function goes through
	 * the list of properties and gets the successive values.
	 * If some of the values are not defined it returns null.
	 * @param {String} varName
	 * @return {Object}
	 */
	this.get = function(varName)
	{
		//temporary variable
		var currObject;

		//if the string has not dots (namley, a single property)
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
	 		//splits the properties based on the "." and saves them into an array
	 		var splitStr = varName.split(".");

	 		//the first property and the only one that will be stored as a key in the variable table
	 		if(vt[splitStr[0]] === null || typeof(vt[splitStr[0]]) === 'undefined')
	 		{
	 			return null;
	 		}

	 		//starting from the first property, find the successive ones
	 		currObject = vt[splitStr[0]];

	 		for(var i = 1; i < splitStr.length - 1; i++)
	 		{
	 			if(currObject[splitStr[i]] === null || typeof(currObject[splitStr[i]]) === 'undefined')
	 			{
	 				return null;
	 			}

	 			//move to the successive property
	 			currObject = currObject[splitStr[i]];

	 		}

	 		if(currObject[splitStr[i]] === null || typeof(currObject[splitStr[i]]) === 'undefined')
	 		{
	 			return null;
	 		}
	 		else
	 		{
	 			return currObject[splitStr[i]];
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
 * @var {String} serialVersionUID
 */
Object.defineProperty(CTATVariableTable, "serialVersionUID", {enumerable: false, configurable: false, writable: false, value: "201403071830L"});

/**
 * Convert a value read as a string (from an input SAI or BRD, e.g.) to a standard type.
 * Except for empty or all-whitespace strings, if it can be interpreted as a number, make it a
 * number. If it parses as a JavaScript boolean, make it one of those. If it's null or undefined,
 * make it null. Else it's a string.
 * @param {string} input
 * @return possibly-converted value
 */
CTATVariableTable.standardizeType = function(input)
{
	if(typeof input != "string" || input.trim().length < 1)
	{
		return input;
	}
	if(Boolean(true).toString() == input)
	{
		return true;
	}
	if(Boolean(false).toString() == input)
	{
		return false;
	}
	if("null" == input)
	{
		return null;
	}
    var toN = Number(input);            // automatic conversion to Number -- use regex to remove 1,000 commas?
    return isNaN(toN) ? input : toN;    // don't use Number.isNaN() -- Safari doesn't have it
};

/**
 * @param {object} value
 * @return {string} value if string, "" if null or undefined, value.toString()
 */
CTATVariableTable.valueAsString = function(value)
{
	return (typeof value == "string" ? value : String(value));
};

/**
 * @param {string} name
 * @return {string} name+"_String"; null if name empty, null or undefined
 */
CTATVariableTable.nameAsString = function(name)
{
	return (name ? name+"_String" : null);
};

CTATVariableTable.prototype = Object.create(CTATBase.prototype);
CTATVariableTable.prototype.constructor = CTATVariableTable;

if(typeof module !== 'undefined')
{
	module.exports = CTATVariableTable;
}

