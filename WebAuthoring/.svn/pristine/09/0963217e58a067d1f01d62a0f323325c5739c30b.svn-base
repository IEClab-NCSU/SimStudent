/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTAT/CTATCommLibrary.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATCommLibrary');

goog.require('CTATBase');
goog.require('CTATConnection');
//goog.require('CTATGlobals');

/**
 *
 */
CTATCommLibrary = function()
{
	CTATBase.call(this, "CTATCommLibrary", "commlibrary");

	var httpreqindex=0;
	var httprequests=new Array ();
	var currentHandler=null;
	var pointer = this;

	var requestList=new Array();

	/**
	 * http://beradrian.wordpress.com/2007/07/19/passing-post-parameters-with-ajax/
	 */


	this.setHandler = function setHandler(handler)
	{
		currentHandler = handler;
	};
	/**
	 *
	 */
	this.encodeVariables=function encodeVariables(variables)
	{
		this.ctatdebug ("encodeVariables ()");

	    var parameterString="";

	    for (var i=0;i<variables.length;i++)
	    {
	    	var variable=variables [i];

	    	if (i>0)
	    	{
	    		parameterString+="&";
	    	}

	    	parameterString+=variable.name;
	    	parameterString+="=";
	    	parameterString+=encodeURIComponent(variable.value);
	    }

	    return (parameterString);
	};
	/**
	 *
	 */
	this.sendXML=function sendXML (aMessage)
	{
		this.ctatdebug ('sendXML ('+aMessage+')');

		var vars=flashVars.getRawFlashVars ();

		//var url="http://" + vars ["remoteSocketURL"] + ":" + vars ["remoteSocketPort"];
		var url = vars["remoteSocketURL"]

		this.ctatdebug ('sendXML ('+url+')');

		if (aMessage.indexOf ("<?xml")==-1)
			this.send_post (url,"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+aMessage)
		else
			this.send_post (url,aMessage)

//		var vars=flashVars.getRawFlashVars ();
//
//		var prefix="http://";
//
//		if (vars ['connection']=='https')
//		{
//			prefix="https://";
//		}
//
//		var url=prefix + vars ["remoteSocketURL"] + ":" + vars ["remoteSocketPort"];
//
//		if (vars ["remoteSocketURL"].indexOf ("http")!=-1)
//		{
//			url=vars ["remoteSocketURL"] + ":" + vars ["remoteSocketPort"];
//		}
//
//		this.ctatdebug ('sendXML ('+url+')');
//
//		var formatted=aMessage;
//
//		if (aMessage.indexOf ("<?xml")==-1)
//			formatted=("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+aMessage);
//
//		if (vars ['connection']=='javascript')
//		{
//			// See TutorBridge.js
//			sendToTutor (formatted);
//		}
//		else
//		{
//			this.send_post (url,formatted);
//		}
	};
	/**
	 *
	 */
	this.sendXMLURL=function sendXMLURL (aMessage,aURL)
	{
		this.ctatdebug ('sendXMLURL ('+aURL+')');

		var vars=flashVars.getRawFlashVars ();

	if (aMessage.indexOf ("<?xml")==-1)
		this.send_post (aURL,"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+aMessage);
	else
		this.send_post (aURL,aMessage);
	};
	/**
	 *
	 */
	this.sendURLVariables=function sendURLVariables (aURL,variables)
	{
		this.ctatdebug ('sendURLVariables ('+aURL+')');

		var vars=flashVars.getRawFlashVars ();

		//if (aMessage.indexOf ("<?xml")==-1) // broken code that would not work even if it was called
		//	this.send_post_variables (aURL,"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+aMessage,variables);
		//else
			this.send_post_variables (aURL,variables);
	};

	/**
	*
	*/
	this.send=function send (url)
	{
		this.ctatdebug ('send ('+url+')');

		if (globalCommDisabled==true)
		{
			this.ctatdebug ("Communications globally disabled, please check your settings");
			return;
		}

		var newConnection=new CTATConnection ();
		newConnection.id=httpreqindex;
		httpreqindex++;

		if (newConnection.httpObject==null)
		{
			alert ('Cannot create XMLHTTP instance');
			return false;
		}

		httprequests.push(newConnection);

		newConnection.url=url;
		newConnection.httpObject.onreadystatechange=this.processReply(currentHandler);

		try
		{
			newConnection.httpObject.open ('GET', url, true);
		}
		catch(err)
		{
			this.ctatdebug ("Error in newConnection.httpObject.open: " + err.message);
			return;
		}

		try
		{
			newConnection.init ();
		}
		catch(err)
		{
			this.ctatdebug ("Error in newConnection.init: " + err.message);
			return;
		}

		try
		{
			newConnection.httpObject.send (null);
		}
		catch(err)
		{
			this.ctatdebug ("Error in newConnection.httpObject.send: " + err.message);
			return;
		}
	};
	/**
	*
	*/
	this.send_post=function send_post (url,data)
	{
		this.ctatdebug ('send_post ('+url+')');
		console.log(url);
		console.log(data);

		if (globalCommDisabled==true)
		{
			this.ctatdebug ("Communications globally disabled, please check your settings");
			return;
		}

		var newConnection=new CTATConnection ();
		newConnection.id=httpreqindex;
		httpreqindex++;

		if (newConnection.httpObject==null)
		{
			alert ('Cannot create XMLHTTP instance');
			return false;
		}

		httprequests.push(newConnection);

		this.ctatdebug (data);

		newConnection.url=url;
		newConnection.data=data;
		newConnection.httpObject.onreadystatechange=this.processReply(currentHandler);

		try
		{
			newConnection.httpObject.open ('POST', url, true);
		}
		catch(err)
		{
			this.ctatdebug ("Error in newConnection.httpObject.open: " + err.message);
			return;
		}

		try
		{
			newConnection.init ();
		}
		catch(err)
		{

			this.ctatdebug ("Error in newConnection.init: " + err.message);
			return;
		}
		try
		{
			newConnection.httpObject.send (data);
		}
		catch(err)
		{
			console.log(err.message);

			this.ctatdebug ("Error in newConnection.httpObject.send: " + err.message);
			return;
		}
	};
	/**
	* 		var data=this.encodeVariables(variables);
	*/
	this.send_post_variables=function send_post_variables (url,variables)
	{
		this.ctatdebug ('send_post_variables ('+url+')');

		var data=this.encodeVariables(variables);

		this.ctatdebug ("Sending: " + data);

		this.send_post (url,data);
	};
	/**
	*
	*/
	this.processReply = function processReply(handler)
	{
		return function(argument)
		{
			pointer.ctatdebug ('processReply ('+httprequests.length+','+argument+')');

			var i=0;
			var found=false;
			var stringDelivery=new Array ();

			var request=0;

			//for (req in httprequests)
			for (request=0;request<httprequests.length;request++)
			{
				var testConnection=httprequests [request];
				var testObject=testConnection.httpObject;

				pointer.ctatdebug ("Testing connection entry " + request + ", readyState: " + testObject.readyState + ", testConnection.consumed: "+ testConnection.consumed);

				if ((testObject.readyState==4) && (testConnection.consumed==false))
				{
					pointer.ctatdebug ("Investigating request response: " + i + " -> " + testObject.status + ", for: " + testConnection.url);

					found=false;

					if (testObject.status==0)
					{
						found=true;

						pointer.ctatdebug ("Received message (status 0): " + testObject.responseText);

						ctatscrim.errorScrimUp("Error: Connection closed by foreign host.");
						testConnection.consumed=true; // make sure we don't call it again!
					}

					// 408 timeout response
					if(testObject.status==408)
					{
						found=true;

						ctatscrim.scrimDown();

						// Re-send all requests after and including the one that timed out
						/*
						while(i < requestList.length)
						{
							requestList [i][0].send(requestList [i][1]);
							i++;
						}
						*/

						testConnection.consumed=true; // make sure we don't call it again!
					}

					if (testObject.status==502)
					{
						found=true;

						ctatscrim.errorScrimUp("Error contacting the server, please refresh the page and try again (HTTP status 502: gateway response).");
						testConnection.consumed=true; // make sure we don't call it again!
					}

					if (testObject.status==200)
					{
						found=true;

						pointer.ctatdebug ("Processing 200 response ...");

						if (handler !=null)
						{
							//pointer.ctatdebug ("Received message: " + testObject.responseText);

							pointer.ctatdebug ("Received message (status 200): " + testObject.responseText);

							//pointer.ctatdebug ("Checking XML API: " + testObject.responseXML);

							stringDelivery.push (testObject.responseText);
						}
						else
							pointer.ctatdebug ("Error: handler is null, can't process response!");

						//Not yet implemented - if this is not a boolean, it is a function.
						/*
						if (httprequests [req][2]!=false)
						{
							httprequests [req][2]();
						}*/

						testConnection.consumed=true; // make sure we don't call it again!
					}

					if (found==false)
					{
						pointer.ctatdebug ("Error: status not handled for: " + testObject.status);
					}
				}
				else
				{
					if (testObject.readyState===0)
					{
						pointer.ctatdebug ("Ready state: request not initialized");
						pointer.ctatdebug ("Received message (status 0): " + testObject.responseText);
					}

					if (testObject.readyState===1)
					{
						pointer.ctatdebug ("Ready state: server connection established");
						pointer.ctatdebug ("Received message (status 1): " + testObject.responseText);
					}

					if (testObject.readyState===2)
					{
						pointer.ctatdebug ("Ready state: request received");
						pointer.ctatdebug ("Received message (status 2): " + testObject.responseText);
					}

					if (testObject.readyState===3)
					{
						pointer.ctatdebug ("Ready state: processing request");
						pointer.ctatdebug ("Received message (status 3): " + testObject.responseText);
					}
				}

				i++;
			}

			pointer.cleanup();

			for (var t=0;t<stringDelivery.length;t++)
			{
				pointer.ctatdebug ("Processing incoming message: " +  t);

				var aMessage=stringDelivery [t];

				if (aMessage.indexOf ("status=success")!=-1)
				{
					pointer.ctatdebug ("Info: logging success message received, not propagating to message handler");
				}
				else
				{
					//useDebugging=true;
					pointer.ctatdebug ("Processing incoming message: " + aMessage);
					//useDebugging=false;

					handler.processMessage (aMessage);
				}

				/*
				if (aMessage.indexOf ("<?xml")==-1)
				{
					pointer.ctatdebug ("Processing incoming message (cleaned): " +  t);

					httphandler.processMessage ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+aMessage);
				}
				else
				{
					pointer.ctatdebug ("Processing incoming message: " +  t);

					httphandler.processMessage (aMessage);
				}*/
			}
		};
	};
	/**
	*
	*/
	this.cleanup=function cleanup ()
	{
		pointer.ctatdebug ("cleanup ()");

		var i=0;
		var count=0;
		var found=false;
		var clean=false;

		while (clean==false)
		{
			//pointer.ctatdebug ("Checking ...");

			found=this.checkEntries ();

			//pointer.ctatdebug ("Found: " + found);

			if (found==false)
			{
				clean=true;
			}
			else
				count++;
		}

		pointer.ctatdebug ("Removed " + count + " entries");
	};
	/**
	*
	*/
	this.checkEntries=function checkEntries ()
	{
		pointer.ctatdebug ("checkEntries ()");

		var i=0;
		var requests=0;

		//for (req in httprequests)
		for (requests=0; requests<httprequests.length;requests++)
		{
			var testConnection=httprequests [requests];

			if (testConnection.consumed==true)
			{
				pointer.ctatdebug ("Removing : " + testConnection.id);

				httprequests.splice(i, 1);
				return (true);
			}

			i++;
		}

		return (false);
	};
}

CTATCommLibrary.prototype = Object.create(CTATBase.prototype);
CTATCommLibrary.prototype.constructor = CTATCommLibrary;
