/* This object represents an CTATExampleTracerGraph */
/* LastModify: FranceskaXhakaj 06/21/14*/


function CTATExampleTracerGraph() 
{
	CTATBase.call(this, "CTATExampleTracerGraph","visualdata");

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/
	

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	var visuals=null; // instance of ExampleTracerGraphVisualData
	
	var links; //array of ExampleTracerLink ONLY!
	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/

	function _getlinks ()
	{
		return links;
	};

	/*
	 * @param link of type ExampleTracerLink
	 */
	function _addLink(link)
	{
		links.push(link);
	};

/***************************** PRIVILEDGED METHODS *****************************************************/

	this.getlinks = function ()
	{
		return _getlinks();
	};

	//we do not need to return anything here do we?
	this.addLink = function(link)
	{
		_addLink(link);
	}
	
	/**
	*
	*/
	this.setVisualData (aData)
	{
		visuals=aData;
	}
	/**
	*
	*/
	this.getVisualData=function ()
	{
		return (visuals);
	};

/****************************** link METHODS ****************************************************/

}

CTATExampleTracerGraph.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerGraph.prototype.constructor = CTATExampleTracerGraph;
