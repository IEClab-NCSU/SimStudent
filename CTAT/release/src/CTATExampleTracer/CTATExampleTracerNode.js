/* This object represents a CTATExampleTracerNode */

goog.provide('CTATExampleTracerNode');

goog.require('CTATBase');
goog.require('CTATExampleTracerNodeVisualData');

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
	CTATBase.call(this, "CTATExampleTracerNode", givenNodeID);

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
	 * @type {string}
	 */
	var nodeName = "";

	/**
     * @type {Set<CTATExampleTracerLink>} all outgoing links
     */
	var outLinks = (givenOutLinks ? givenOutLinks : new Set());

	/**
	 * @type {Set<CTATExampleTracerLink>} outgoing links whose actionType is CORRECT
	 */
	var correctOutLinks = new Set();
	outLinks.forEach(function(k,link,s) {if(link.isCorrect()){correctOutLinks.add(link);}});

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
	 * @return {string} nodeName
	 */
	this.getNodeName = function()
	{
        // that.ctatdebug("CTATExampleTracerGraph --> getNodeName() returns "+nodeName);
        return nodeName;
	};

	/**
	 * @param {string} name new value for nodeName
	 */
	this.setNodeName = function(name)
	{
        that.ctatdebug("CTATExampleTracerGraph --> in setNodeName("+name+")");
        nodeName = name;
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
	 * @return {Set<CTATExampleTracerLink>} returns outLinks whose actionType is CORRECT
	 */
	this.getCorrectOutLinks = function()
	{
		return correctOutLinks;
	};

	/**
	 * Adds a link to the outLinks, possibly to correctOutLinks
	 * @param {CTATExampleTracerLink} link
	 * @return {undefined}
	 */
	this.addOutLink = function(link)
	{
		ctatdebug("CTATExampleTracerNode --> addOutLink( " + link + " )");
		outLinks.add(link);
		if(link.isCorrect())
		{
			correctOutLinks.add(link);
		}
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

	/**
     * @return {integer} Returns the number of outgoing links whose actionType is CORRECT
     */
     this.getCorrectOutDegree = function()
     {
		return correctOutLinks.size;
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