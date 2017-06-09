/* This object represents an CTATExampleTracerNode */
/* LastModify: FranceskaXhakaj 06/20/14*/


function CTATExampleTracerNode() 
{
	CTATBase.call(this, "CTATExampleTracerNode","visualdata");

/**************************** PUBLIC INSTANCE VARIABLES ******************************************************/


/**************************** PRIVATE INSTANCE VARIABLES ******************************************************/

	var visuals=null; // instance of ExampleTracerNodeVisualData

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

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

/****************************** PUBLIC METHODS ****************************************************/

}

CTATExampleTracerNode.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerNode.prototype.constructor = CTATExampleTracerNode;
