/* This object represents an CTATExampleTracerTracerChangedEvent */
/* LastModify: FranceskaXhakaj 07/14*/

/*
 * @param givenSource (of type Object):
 * @param givenOldTracer (of type CTATExampleTracerTracer)
 * @param givenNewTracer (of type CTATExampleTracerTracer)
 */
function CTATExampleTracerTracerChangedEvent(givenSource, givenOldTracer, givenNewTracer) 
{

	//following the Java implementation, the second parameter needs to be null
	CTATExampleTracerEvent.call(this, givenSource, null);
	
/**************************** PUBLIC INSTACE VARIABLES ******************************************************/


/**************************** PRIVATE INSTACE VARIABLES ******************************************************/

	var oldTracer = givenOldTracer;
	var newTracer = givenNewTracer;

	var that = this; // used to make the object available to the private methods

/***************************** PRIVATE METHODS *****************************************************/


/***************************** PRIVILEDGED METHODS *****************************************************/

/****************************** PUBLIC METHODS ****************************************************/

}

CTATExampleTracerTracerChangedEvent.prototype = Object.create(CTATExampleTracerEvent.prototype);
CTATExampleTracerTracerChangedEvent.prototype.constructor = CTATExampleTracerTracerChangedEvent;