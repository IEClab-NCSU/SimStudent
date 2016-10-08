/**------------------------------------------------------------------------------------
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

/**
*
*/
function registerInterface (anID)
{
	useDebugging=true;
	
	debug ("registerInterface ("+anID+")");
	
	useDebugging=false;	
}

/**
*
*/
function sendToTutor (aMessage)
{
	useDebugging=true;
	
	debug ("sendToTutor ("+aMessage+")");
	
	
	useDebugging=false;	
}

/**
*
*/
function receiveFromTutor (aMessage)
{
	useDebugging=true;
	
	debug ("receiveFromTutor ("+aMessage+")");
	
	if (commMessageHandler!=null)
	{
		commMessageHandler.processMessage (aMessage);
	}
	else
		debug ("Error: no message handler available to process incoming message");
	
	useDebugging=false;	
}
