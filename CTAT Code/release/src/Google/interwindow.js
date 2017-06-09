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

var ctatTag="cookie-msg-ctat";
var cookieCheckTimeout=5000;
var cookieMessageHandler="";
var cookieSessionID=CTATGuid.guid ();
var cookies=null;
var RPCMarker="RPC:";
var running=false;

/**
*
*/
function rpcConsole (aMessage)
{
	//document.getElementById ("console").innerHTML=aMessage;
}

/**
*
*/
function setCookie(value) 
{
	ctatdebug ("setCookie ("+value+")");
	
	//var formatted=cookieSessionID+":"+ctatTag + "=" + value + "; path=/ ; expires=Thu, 01 Jan 1970 00:00:01 GMT;";																			
	var formatted=cookieSessionID+":"+ctatTag + "=" + value + "; domain=.google.com; path=/;";
	
	ctatdebug (formatted);
	
	try
	{
		document.cookie = formatted;
	}
	catch (err)
	{
		ctatdebug ("Error setting cookie: " + err);
	}
}

/**
*
*/
function setCookieWithID(value,anID) 
{
	ctatdebug ("setCookie ("+value+")");
	
	//var formatted=anID+":"+ctatTag + "=" + value + "; path=/ ; expires=Thu, 01 Jan 1970 00:00:01 GMT;";																			
	var formatted=anID+":"+ctatTag + "=" + value + "; domain=.google.com; path=/;";
	
	ctatdebug (formatted);
	
	try
	{
		document.cookie = formatted;
	}
	catch (err)
	{
		ctatdebug ("Error setting cookie: " + err);
	}
}

/**
*
*/
function loadCookies ()
{
	ctatdebug ("loadCookies ()");
	
	cookies=new Array ();
	
	ctatdebug ("Cookie: " + document.cookie);
	
	if ((document.cookie=="") || (document.cookie===undefined))
	{
		ctatdebug ("No cookie defined yet");
		return;
	}

    var ca = document.cookie.split(';');
		
	for (var i=0;i<ca.length;i++)
	{
		var raw=ca [i];
		
		var kv = raw.split('=');
		
		cookies [kv [0].trim()]=kv[1];
	}
}

/**
*
*/
function checkRPCCookies()
{
	ctatdebug ("checkRPCCookies () >>>>>>>>>>>>>>>>>");
	
	loadCookies ();

	processRPC ();
	
	listCookies ();
		
	ctatdebug ("checkRPCCookies () <<<<<<<<<<<<<<<<");
}

/**
*
*/
function processRPC ()
{
	ctatdebug ("processRPC ()");
	
	for (var key in cookies)
	{		
		if (cookies [key].indexOf (RPCMarker)!=-1)
		{
			var keyChecker=key.split (":");
			
			ctatdebug ("Checking session IDs: " + keyChecker [0] + "," + cookieSessionID);
			
			if (keyChecker [0]!=cookieSessionID)
			{		
				var functionSplitter=cookies [key].split (":");

				ctatdebug ("Calling: " + functionSplitter [1]);
				
				if (functionSplitter [0]=="RPC")
				{			
					ctatdebug ("Found an RPC call, checking ...");
				
					if (functionSplitter [1]!="NOP")
					{
						ctatdebug ("Calling: " + functionSplitter [1]);
					
						if (functionSplitter.length>2)
						{
							window [functionSplitter [1]] (functionSplitter[2]);
						}
						else
						{
							window [functionSplitter [1]] ();
						}
					
						// Make sure we clear it out otherwise we will keep calling the function																
						setCookieWithID ("NOP",keyChecker [0]);
					}
					else
					{
						ctatdebug ("Function already executed");
					}
				}	
			}
			else
			{
				ctatdebug ("Don't call method on own window");
			}
			
			return;
		}
	}
}

/**
*
*/
function listCookies ()
{
	ctatdebug ("listCookies ()");
	
	loadCookies ();
	
	var index=0;
	
	for (var key in cookies)
	{
		ctatdebug ("["+index+"] " + key + " => " + cookies [key]);

		index++;
	}
}

/**
*
*/
function setCookieMessageHandler (aHandler)
{
	cookieMessageHandler=aHandler;
}

/**
*
*/
function sendCookieMessage (aMessage)
{
	setCookie (aMessage);
}

/**
*
*/
function updateMessageSender () 
{
	ctatdebug ("updateMessageSender ()");

    var t = document.forms['sender'].elements['message'];
	
    setCookie(t.value);
	
    setTimeout(updateMessageClient, cookieCheckTimeout);
}

/**
*
*/
function updateMessageReceiver ()
{
	ctatdebug ("updateMessageReceiver ()");

	checkRPCCookies();

	if (running==true)
	{
		setTimeout(updateMessageReceiver, cookieCheckTimeout);
	}	
}

/**
*
*/
function startCookieRPC ()
{	
	ctatdebug ("startCookieRPC ()");

	rpcConsole (cookieSessionID);
	
	running=true;

	setTimeout(updateMessageReceiver, cookieCheckTimeout);
}

/**
*
*/
function stopCookieRPC ()
{
	ctatdebug ("stopCookieRPC ()");

	running=false;
}

/**
*
*/
function callFunction (aFunction,anArgument)
{
	ctatdebug ("callFunction ("+anArgument+")");

	setCookie (RPCMarker+aFunction+":"+anArgument);
}

/**
*
*/
function dummyFunction ()
{
	alert ("success");
}
