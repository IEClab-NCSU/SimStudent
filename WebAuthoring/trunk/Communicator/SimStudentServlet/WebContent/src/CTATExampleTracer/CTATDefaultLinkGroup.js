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

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

	/**
	 * @param givIsOrdered of type boolean
	 * @return undefined
	 */
	this.setOrdered = function(givIsOrdered)
	{
		//ctatdebug("CTATDefaultLinkGroup --> in setOrdered");
		isOrdered = givIsOrdered;
		//ctatdebug("CTATDefaultLinkGroup --> out of setOrdered");
	};

	/**
	 * @param name of type string
	 * @return undefined
	 */
	this.setName = function(name)
	{
		//ctatdebug("CTATDefaultLinkGroup --> in setName");
		groupName = name;
		//ctatdebug("CTATDefaultLinkGroup --> out of setName");
	};

	/**
	 * @param link of type CTATExampleTracerLink
	 * @return boolean
	 */
	this.containsLink = function(link)
	{
		//ctatdebug("CTATDefaultLinkGroup --> in containsLink");
		return links.has(link);
	};

	/**
	 * @param link of type CTATExampleTracerLink
	 * @return boolean
	 */
	this.removeLink = function(link)
	{
		//ctatdebug("CTATDefaultLinkGroup --> in removeLink");
		return links.delete(link);
	};

	/**
	 * @return CTATLinkGroup
	 */
	this.getParent = function()
	{
		//ctatdebug("CTATDefaultLinkGroup --> in getParent");
		return parent;
	};

	/**
	 * @param link of type CTATExampleTracerLink
	 * @return undefined
	 */
	this.addLink = function(link)
	{
		//ctatdebug("CTATDefaultLinkGroup --> in addLink");
		links.add(link);
		//ctatdebug("CTATDefaultLinkGroup --> out of addLink");
	};

	/**
	 * @return boolean
	 */
	this.getIsOrdered = function()
	{
		//ctatdebug("CTATDefaultLinkGroup --> in getIsOrdered");
		return isOrdered;
	};

	/**
	 * @return boolean
	 */
	this.getIsReenterable = function()
	{
		//ctatdebug("CTATDefaultLinkGroup --> in getIsReenterable");
		return isReenterable;
	};

	/**
	 * @return set of CTATLinkGroup
	 */
	this.getSubgroups = function()
	{
		//ctatdebug("CTATDefaultLinkGroup --> in getSubgroups");
		return subgroups;
	};

	/**
	 * @return set of CTATExampleTracerLink
	 */
	this.getLinks = function()
	{
		//ctatdebug("CTATDefaultLinkGroup --> in getLinks");
		return links;
	};
	
	/**
	 * @param givIsOrdered of type boolean
	 * @return undefined
	 */
	this.setOrdered = function(givIsOrdered)
	{
		//ctatdebug("CTATDefaultLinkGroup --> in setOrdered");
		isOrdered = givIsOrdered;
		//ctatdebug("CTATDefaultLinkGroup --> out of setOrdered");
	};

	/**
	 * @param givIsReentrable of type boolean
	 * @return undefined
	 */
	this.setReenterable = function(givIsReentrable)
	{
		//ctatdebug("CTATDefaultLinkGroup --> in setReenterable");
		isReenterable = givIsReentrable;
		//ctatdebug("CTATDefaultLinkGroup --> out of setReenterable");
	};

	/**
	 * @param group of type CTATLinkGroup
	 * @return undefined
	 */
	this.setParent = function(group)
	{
		//ctatdebug("CTATDefaultLinkGroup --> in setParent");
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
		//ctatdebug("CTATDefaultLinkGroup --> in addSubgroup groupName " + groupName);
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