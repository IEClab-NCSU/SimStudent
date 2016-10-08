/* This object represents a CTATExampleTracerNode */

goog.provide('CTATExampleTracerNode');

goog.require('CTATBase');

//goog.require('CTATExampleTracerNodeVisualData');//

/* LastModify: FranceskaXhakaj 07/14*/

/**
 * Represents the Nodes in the BehaviorGraph
 * @constructor
 * @augments CTATBase
 * @param {integer} givenNodeID 
 * @param {Set of CTATExampleTracerLink} givenOutLinks
 */
CTATExampleTracerNode = function(givenNodeID, givenOutLinks) 
{
	//calling the constructor of the super class
	CTATBase.call(this, "CTATExampleTracerNode","visualdata");

/**************************** PUBLIC INSTANCE VARIABLES ******************************************************/


/**************************** PRIVATE INSTANCE VARIABLES ******************************************************/

    /**
     * @type {CTATExampleTracerNodeVisualData}
     */
	var visuals = null; // Martin's code

	/**
     * @type {integer}
     */
	var nodeID = givenNodeID; 

	/**
     * @type Set of CTATExampleTracerLink
     */
	var outLinks = givenOutLinks; 

	/**
     * @type {Set of CTATExampleTracerLink}
     */
	var inLinks = new Set();
	
	/**
     * Make the object available to private methods
     */
	var that = this; 

/***************************** PRIVATE METHODS *****************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

    /**
	 * @return string with #nodeID
	 */
	this.toString = function()
	{
		return "node" + nodeID;
	};

	/**
	 * Method to get the node ID.
	 * @return {integer} the nodeID
	 */
	this.getNodeID = function()
	{
		//ctatdebug("CTATExampleTracerNode --> getNodeID");
		return nodeID;
	};

	/**
	 * @return {Set of CTATExampleTracerLink} returns the outLinks for the current node
	 * NOTE: originally outlinks used to be an array, we decided to use a Set instead
	 */
	this.getOutLinks = function()
	{
		ctatdebug("CTATExampleTracerNode --> getOutLinks");
		return outLinks;
	};

	/**
	 * Adds a link to the outLinks
	 * @param {CTATExampleTracerLink} link
	 * @return {undefined}
	 */
	this.addOutLink = function(link)
	{
		ctatdebug("CTATExampleTracerNode --> addOutLink( " + link + " )");
		outLinks.add(link);
	};

	/**
	 * Empties inLinks
	 * @return {undefined}
	 */
	this.clearInLinks = function()
	{
		inLinks = new Set();
	};

	/**
	 * @param {CTATExampleTracerLink} link 
	 * @return {undefined}
	 */
	this.addInLink = function(link)
	{
		inLinks.add(link);
	};

	/**
	 * @return {Set of CTATExampleTracerLink}
	 */
	this.getInLinks = function()
	{
		return inLinks;
	};

	/**
     * @return {integer} Returns the outDegree; the number of
     * outgoing links from the given node.
     */
     this.getOutDegree = function()
     {
     	return outLinks.size;
     };

/****************************** PUBLIC METHODS ****************************************************/


/****************************** MARTIN'S METHODS ****************************************************/
	/**
	*
	*/
	this.setVisualData = function(aData)
	{
		//ctatdebug("CTATExampleTracerNode --> setVisualData");

		visuals = aData;
	};
	
	/**
	*
	*/
	this.getVisualData=function ()
	{
		//ctatdebug("CTATExampleTracerNode --> getVisualData");
		
		return (visuals);
	};

};

//setting up inheritance from CTATBase
CTATExampleTracerNode.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerNode.prototype.constructor = CTATExampleTracerNode;

if(typeof module !== 'undefined')
{
	module.exports = CTATExampleTracerNode;
}