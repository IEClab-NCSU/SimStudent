/* This object represents an CTATDefaultLinkGroup */

goog.provide('CTATDefaultLinkGroup');

goog.require('CTATLinkGroup');

//goog.require('CTATExampleTracerLink');//
//goog.require('CTATBase');

/* LastModify: FranceskaXhakaj 07/14*/

/**
 * A set of links with defined ordering constraints.
 * Groups can nest--that is, a group can have another group
 * completely inside it--to any depth. But groups cannot overlap.
 * @param givenGroupName of type string
 * @param givenIsOrdered of type boolean
 * @param givenIsReenterable of type boolean
 * @param givenLinks of type set of CTATExampleTracerLink
 */

CTATDefaultLinkGroup = function(givenGroupName, givenIsOrdered, givenIsReenterable, givenLinks)
{

/**************************** INHERITED CONSTRUCTOR ******************************************************/

	//calling the constructor of the super class
	CTATLinkGroup.call(this, 'CTATDefaultLinkGroup', givenGroupName);

/**************************** PUBLIC INSTANCE VARIABLES ******************************************************/


/**************************** PRIVATE INSTANCE VARIABLES ******************************************************/

	var groupName = givenGroupName; //of type string

	//True means ordered, false means unordered
	var isOrdered = givenIsOrdered; //of type boolean

	var isReenterable = givenIsReenterable; //of type boolean

	var links = new Set(); //of type set of CTATExampleTracerLink

	if(givenLinks !== null && typeof(givenLinks) !== 'undefined')
	{
		givenLinks.forEach(function(el)
		{
			links.add(el);
		});
	}

	var subgroups = new Set(); //of type set of CTATLinkGroup

	var parent = null; //of type CTATLinkGroup

	/**
	 * A default message to display on no-model trace results; null if none.
	 * @type {string}
	 */
	var defaultBuggyMsg = null;

	var that = this; // used to make the object available to the private methods

	that.ctatdebug("CTATDefaultLinkGroup constructor group name: " + givenGroupName);
	that.ctatdebug("CTATDefaultLinkGroup constructor group isOrdered: " + givenIsOrdered);
	that.ctatdebug("CTATDefaultLinkGroup constructor group isReenterable: " + givenIsReenterable);


/***************************** PRIVATE METHODS *****************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * @param givIsOrdered of type boolean
	 * @return undefined
	 */
	this.setOrdered = function(givIsOrdered)
	{
		that.ctatdebug("CTATDefaultLinkGroup --> in setOrdered group name: " + groupName);
		that.ctatdebug("CTATDefaultLinkGroup --> in setOrdered:: " + givIsOrdered);
		isOrdered = givIsOrdered;
		//ctatdebug("CTATDefaultLinkGroup --> out of setOrdered");
	};

	/**
	 * @param name of type string
	 * @return undefined
	 */
	this.setName = function(name)
	{
		that.ctatdebug("CTATDefaultLinkGroup --> in setName: " + name);
		groupName = name;
		//ctatdebug("CTATDefaultLinkGroup --> out of setName");
	};

	/**
	 * @param link of type CTATExampleTracerLink
	 * @return boolean
	 */
	this.containsLink = function(link)
	{
		that.ctatdebug("CTATDefaultLinkGroup --> in containsLink: " + link.getUniqueID());
		return links.has(link);
	};

	/**
	 * @param link of type CTATExampleTracerLink
	 * @return boolean
	 */
	this.removeLink = function(link)
	{
		that.ctatdebug("CTATDefaultLinkGroup --> in removeLink: " + link.getUniqueID());
		return links.delete(link);
	};

	/**
	 * @return CTATLinkGroup
	 */
	this.getParent = function()
	{
		that.ctatdebug("CTATDefaultLinkGroup --> in getParent");
		return parent;
	};

	/**
	 * @param link of type CTATExampleTracerLink
	 * @return undefined
	 */
	this.addLink = function(link)
	{
		that.ctatdebug("CTATDefaultLinkGroup --> in addLink: " + link.getUniqueID());
		links.add(link);
		//ctatdebug("CTATDefaultLinkGroup --> out of addLink");
	};

	/**
	 * @return boolean
	 */
	this.getIsOrdered = function()
	{
		that.ctatdebug("CTATDefaultLinkGroup --> in getIsOrdered: " + isOrdered);
		return isOrdered;
	};

	/**
	 * @return boolean
	 */
	this.getIsReenterable = function()
	{
		that.ctatdebug("CTATDefaultLinkGroup --> in getIsReenterable: " + isReenterable);
		return isReenterable;
	};

	/**
	 * @return set of CTATLinkGroup
	 */
	this.getSubgroups = function()
	{
		that.ctatdebug("CTATDefaultLinkGroup --> in getSubgroups");
		return subgroups;
	};

	/**
	 * @return set of CTATExampleTracerLink
	 */
	this.getLinks = function()
	{
		that.ctatdebug("CTATDefaultLinkGroup --> in getLinks");
		return links;
	};


	/**
	 * @param givIsReentrable of type boolean
	 * @return undefined
	 */
	this.setReenterable = function(givIsReentrable)
	{
		that.ctatdebug("CTATDefaultLinkGroup --> in setReenterable: " + givIsReentrable);
		isReenterable = givIsReentrable;
		//ctatdebug("CTATDefaultLinkGroup --> out of setReenterable");
	};

	/**
	 * @param group of type CTATLinkGroup
	 * @return undefined
	 */
	this.setParent = function(group)
	{
		that.ctatdebug("CTATDefaultLinkGroup --> in setParent: " + group.getName());
		parent = group;
		//ctatdebug("CTATDefaultLinkGroup --> out of setParent");
	};

	/**
	 * @param group of type CTATLinkGroup
	 * @return undefined
	 */
	this.addSubgroup = function(toBeAdded)
	{
		//ctatdebug("CTATDefaultLinkGroup --> subgroups has before: " + subgroups.size);
		//ctatdebug("CTATDefaultLinkGroup --> subgroups has before: " + subgroups.has(toBeAdded));
		//ctatdebug("CTATDefaultLinkGroup --> in addSubgroup toBeAdded " + toBeAdded.getName());
		that.ctatdebug("CTATDefaultLinkGroup --> in addSubgroup groupName: " + toBeAdded.getName());

		subgroups.add(toBeAdded);

		//ctatdebug("CTATDefaultLinkGroup --> subgroups has after: " + subgroups.has(toBeAdded));
		//ctatdebug("CTATDefaultLinkGroup --> subgroups has after: " + subgroups.size);
		//ctatdebug("CTATDefaultLinkGroup --> out of addSubgroup");
	};

	/**
	 * @return type string
	 */
	this.getName = function()
	{
		return groupName;
	};

	/**
	 * @return {string} default message to display on no-model trace results; null if none
	 */
	this.getDefaultBuggyMsg = function()
	{
		return defaultBuggyMsg;
	};

	/**
	 * @param {string} msg new default message to display on no-model trace results; null if none
	 */
	this.setDefaultBuggyMsg = function(msg)
	{
		defaultBuggyMsg = (msg == "" ? null : msg);
	};

    this.toString = function()
    {
        return "Group Name: "+ this.getName() ;
    };

/****************************** PUBLIC METHODS ****************************************************/


    /****************************** CONSTRUCTOR CALLS ****************************************************/

};

/**************************** SETTING UP INHERITANCE ******************************************************/

CTATDefaultLinkGroup.prototype = Object.create(CTATLinkGroup.prototype);
CTATDefaultLinkGroup.prototype.constructor = CTATDefaultLinkGroup;

if(typeof module !== 'undefined')
{
	module.exports = CTATDefaultLinkGroup;
}