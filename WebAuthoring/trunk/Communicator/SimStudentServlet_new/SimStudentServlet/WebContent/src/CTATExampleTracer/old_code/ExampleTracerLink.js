/* This object represents an CTATExampleTracerLink */
/* LastModify: FranceskaXhakaj 06/20/14*/


function CTATExampleTracerLink() 
{
	CTATBase.call(this, "CTATExampleTracerLink","visualdata");

/**************************** PUBLIC INSTACE VARIABLES ******************************************************/
	

/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	var visuals = null; // instance of CTATExampleTracerLinkVisualData

	var uniqueID; //of type integer
	var matcher ; //of type Matcher

	var interpolateSelection; //of type string
	var interpolateAction; //of type string
	var interpolateInput; //of type string

	var maxTraversals = 1;

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/

/***************************** PRIVILEDGED METHODS *****************************************************/

	/*
	 * @return an integer
	 */
	this.getUniqueID = function ()
	{
		return uniqueID;
	};

	/*
	 * @param sai of type ExampleTracerSAI
	 * @param vt of type VariableTable
	 * @return boolean
	 */
	this.matchesSAI = function (sai, vt)
	{
		var m = _getMatcher();

		setInterpolateSAI(sai.getSelectionAsString(), sai.getActionAsString(), sai.getInputAsString());

		var mResult = m.match(sai.getSelectionAsArray(),
    			sai.getActionAsArray(),
    			sai.getInputAsArray(),
    			sai.getActor(), vt);

		return mResult;
	};

	/*
	 * @return a Matcher object
	 */
	this.getMatcher = function ()
	{
		//for the moment remove this part 
		//related to case sensititvity
		//as we do not know what to do with
		// ProblemModel yet
		

		return matcher;
	};


	/*
	 * @param s of type string
	 * @param a of type string
	 * @param i of type string
	 * @return void
	 */
	this.setInterpolateSAI = function(s, a, i)
	{
		interpolateSelection = s;
        interpolateAction = a;
        interpolateInput = i;
	};

	/*
	 * This method returns the operative value for the maximum traversals 
     * permitted.
	 * @return integer
	 */
	this.getMaxTraversals = function ()
	{
		return maxTraversals;
	};


	/*
	 * Tell whether this edge represents the student's assertion that work on 
     * the problem is finished.
	 * @return boolean
	 */
	this.isDone = function ()
	{
		return false;
	};
	
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

CTATExampleTracerLink.prototype = Object.create(CTATBase.prototype);
CTATExampleTracerLink.prototype.constructor = CTATExampleTracerLink;

