/* This object represents an CTATExampleTracerGraph */
/* LastModify: FranceskaXhakaj 06/21/14*/


function CTATExampleTracerGraph() 
{
	CTATBase.call(this, "CTATExampleTracerGraph","visualdata");

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/
	

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	var visuals = null; // instance of CTATExampleTracerGraphVisualData
	
	var links; //array of ExampleTracerLink ONLY!

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/

/***************************** PRIVILEDGED METHODS *****************************************************/

	/*
	 * @return an array of type ExampleTracerLink
	 */
	this.getlinks = function ()
	{
		return links;
	};

	/*
	 * @param link of type ExampleTracerLink
	 * @return undefined
	 */
	this.addLink = function(link)
	{
		links.push(link);
		//...
	}
	
	/**
	*
	*/
	this.setVisualData = function(aData)
	{
		visuals = aData;
	}
	/**
	*
	*/
	this.getVisualData = function ()
	{
		return (visuals);
	};

/****************************** link METHODS ****************************************************/

}

CTATExampleTracerGraph.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerGraph.prototype.constructor = CTATExampleTracerGraph;
