/**-----------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 
 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
*/
goog.provide('TutorBridge');

goog.require('CTATGlobals');
/**
*
*/
function registerInterface (anID)
{
	//useDebugging=true;
	
	ctatdebug ("registerInterface ("+anID+")");
	
	//useDebugging=false;	
}

/**
*
*/
function sendToTutor (aMessage)
{
	//useDebugging=true;
	
	ctatdebug ("sendToTutor ("+aMessage+")");
		
	//useDebugging=false;	
}

/**
*
*/
function receiveFromTutor (aMessage)
{
	//useDebugging=true;
	
	ctatdebug ("receiveFromTutor ("+aMessage+")");
	
	if (commMessageHandler!=null)
	{
		commMessageHandler.processMessage (aMessage);
	}
	else
		ctatdebug ("Error: no message handler available to process incoming message");
	
	//useDebugging=false;	
}
