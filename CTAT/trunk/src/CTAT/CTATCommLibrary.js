/**-----------------------------------------------------------------------------
 $Author: mdb91 $
 $Date: 2017-05-18 15:33:20 -0500 (週四, 18 五月 2017) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTAT/CTATCommLibrary.js $
 $Revision: 24728 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

	Good information on CORS with examples:
	http://www.html5rocks.com/en/tutorials/cors/

 */
goog.provide('CTATCommLibrary');

goog.require('CTATBase');
goog.require('CTATConnection');
goog.require('CTATGlobals');
goog.require('CTATWSConnection');
goog.require('CTATScrim');
goog.require('CTAT.ToolTutor');
goog.require('CTATLanguageManager');
goog.require('CTATLMS');

var bundleFormatter="";
var inBundle=false;
var useBundling=false;
var useOLIEncoding=false;

/**
 * Maintain a list of outstanding and completed XHR requests, to be careful that they don't clobber each other.
 * @param {} aHandler an object having a function property processMessage() that accepts the response body
 * @param {boolean} aUseScrim whether to show the scrim on some errors; default is true
 */
CTATCommLibrary = function(aHandler, aUseScrim)
{
	CTATBase.call(this, "CTATCommLibrary", aHandler);

	authenticityToken = "";

	var xmlHeader='<?xml version="1.0" encoding="UTF-8"?>';
	var fixedURL="";
	var httpreqindex=0;
	var httprequests=[];
	var httphandler=(aHandler ? aHandler : null);
	var useScrim=(aUseScrim == null ? true : aUseScrim);
	var useCommSettings=true;
	var messageListener=null;
	var pointer = this;
	var socketType="http";
	var connectionRefusedMessage="ERROR_CONN_TS";
	var fileTobeLoaded="";

	/**
	*
	*/
	this.setUseCommSettings=function setUseCommSettings (aValue)
	{
		useCommSettings=aValue;
	};

	/**
	*
	*/
	this.getUseCommSettings=function getUseCommSettings ()
	{
		return (useCommSettings);
	};

	/**
	*
	*/
	this.setConnectionRefusedMessage=function setConnectionRefusedMessage (aValue)
	{
		connectionRefusedMessage=aValue;
	};

	/**
	*
	*/
	this.setSocketType=function setSocketType (aType)
	{
		socketType=aType;
	};
	/**
	* @return true if socketType is https or wss
	*/
	this.hasSecureSocket=function ()
	{
		return /https/i.test(socketType) || /wss/i.test(socketType);
	};
	/**
	* @return true if socketType includes "javascript"
	*/
	this.hasJavaScriptConnection=function ()
	{
		return /javascript/i.test(socketType);
	};
	/**
	* @return true if socketType includes "websocket"
	*/
	this.hasWebSocket=function ()
	{
		return /websocket/i.test(socketType);
	};

	/**
	 * See http://beradrian.wordpress.com/2007/07/19/passing-post-parameters-with-ajax/
	 * This setter's argument has the same semantics as the constructor's argument.
	 * @param {} aHandler an object having a function property processMessage() that accepts the response body
	 */
	this.assignHandler=function assignHandler (aHandler)
	{
		httphandler=aHandler;
	};
	/**
	*
	*/
	this.assignMessageListener=function assignMessageListener (aListener)
	{
		messageListener=aListener;
	};
	/**
	 *
	 */
	this.encodeVariables=function encodeVariables(variables)
	{
		pointer.ctatdebug ("encodeVariables ()");

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
	    	parameterString+=encodeURIComponent (variable.value);  // FIXME was encodeURI
	    }

	    return (parameterString);
	};
	/**
	 *
	 */
	this.encodeVariablesOLI=function encodeVariablesOLI(variables)
	{
		pointer.ctatdebug ("encodeVariablesOLI ()");

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
	 * @param aVars
	 * @param aURL
	 * @param {boolean} canBeWebSocket 
	 */
	this.createConnection=function createConnection (aVars,aURL,canBeWebSocket)
	{
		pointer.ctatdebug ('createConnection () canBeWebSocket '+canBeWebSocket);
		canBeWebSocket = (typeof(canBeWebSocket) == "undefined" ? true : canBeWebSocket !== false);

		if (!aURL)
		{
			return (new CTATConnection (aVars));
		}

		var	newConnection=null;

		if (canBeWebSocket && pointer.hasWebSocket())
		{
			// We need to find out if we already have a websocket object for the requested URL,
			// otherwise we keep opening them over and over again
			newConnection = this.findWSConnection(aURL);
			if (!newConnection)
			{
				newConnection=new CTATWSConnection (aVars);
				newConnection.setID (httpreqindex);
				newConnection.setURL (aURL);
				newConnection.assignReceiveFunction (this.processWSReply);
				newConnection.assignCloseFunction (this.processWSClose);

				httprequests.push(newConnection);

				httpreqindex++;
			}
			return (newConnection);
		}

		newConnection=new CTATConnection (aVars);
		newConnection.setID (httpreqindex);
		newConnection.setURL (aURL);
		newConnection.assignReceiveFunction (this.processReply);

		httprequests.push(newConnection);

		httpreqindex++;

		return (newConnection);
	};
	
	/**
	*	Find the websocket connection associated with the given URL
	*	@param aUrl the url to match against
	*	@returns the CTATWSConnection object, or null if no connection found
	**/
	this.findWSConnection = function(aURL)
	{
		ctatdebug('findWSConnection( '+aURL+' )');
		let sURL = pointer.editSocketURLForHTTPS(aURL);

		for (let request=0; request<httprequests.length; request++)
		{
			var testConnection=httprequests [request];
			ctatdebug('checking: socketType = '+testConnection.getSocketType ()+' URL = '+testConnection.getURL ());
			if ((testConnection.getSocketType ()=="ws") && (testConnection.getURL ()==sURL))
			{
				return (testConnection);
			}
		}
		return null;
	};
	
	/**
	*
	*/
	this.startBundle=function startBundle ()
	{
		pointer.ctatdebug ('startBundle ()');

		if (useBundling===false)
		{
			this.ctatdebug ("Not using bundling, bump");
			return;
		}

		bundleFormatter=xmlHeader+"<message><verb/><properties><MessageType>MessageBundle</MessageType><messages>";

		inBundle=true;
	};
	/**
	 *
	 */
	this.endBundle=function endBundle ()
	{
		pointer.ctatdebug ('endBundle ()');

		if (useBundling===false)
		{
			pointer.ctatdebug ("Not using bundling, bump");
			return;
		}

		inBundle=false;

		bundleFormatter+="</messages></properties></message>";

		pointer.sendXML (bundleFormatter);
	};
	/**
	*
	*/
	this.setFixedURL=function setFixedURL (aURL)
	{

		fixedURL=aURL;
	};
	/**
	 *
	 */
	this.getURL=function getURL ()
	{
		pointer.ctatdebug ("getURL ()");

		if (fixedURL!=="")
		{
			pointer.ctatdebug ("Returning fixedURL: " + fixedURL);
			return (fixedURL);
		}

		var vars=flashVars.getRawFlashVars ();

		var prefix="http://";

		//if (vars ['tutoring_service_communication']=='https')
		if (pointer.hasSecureSocket())
		{
			prefix="https://";
		}

		var url=prefix + vars ["remoteSocketURL"] + ":" + vars ["remoteSocketPort"];

		if (vars ["remoteSocketURL"].indexOf ("http")!=-1)
		{
			url=vars ["remoteSocketURL"] + ":" + vars ["remoteSocketPort"];
		}

		return (url);
	};
	/**
	 * @param {string} aMessage text to send
	 * @param {boolean} sendToCollaborators whether to send to collaborators; default value is true
	 */
	this.sendXML=function sendXML (aMessage, sendToCollaborators)
	{
	        sendToCollaborators = (typeof(sendToCollaborators) == "undefined" ? true : sendToCollaborators !== false);
		//useDebugging=true;
		pointer.ctatdebug ("Sending: " + aMessage);
		//useDebugging=false;

		pointer.ctatdebug ('sendXML ('+aMessage+')');

		if (useBundling===true)
		{
			if (inBundle===true)
			{
				pointer.ctatdebug ('Bundling ...');

				bundleFormatter+=aMessage;
				return;
			}
			else
			{
				bundleFormatter=aMessage;
			}
		}
		else
		{
			bundleFormatter=aMessage;
		}

		var vars=flashVars.getRawFlashVars ();

		var url=this.getURL ();

		var formatted=bundleFormatter;

		if (bundleFormatter.indexOf ("<?xml")==-1)
		{
			formatted=("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+bundleFormatter);
		}

		if (this.getUseCommSettings() && pointer.hasJavaScriptConnection())
		{
			if(sendToCollaborators && pointer.hasWebSocket())  // post to other collaborators, then trace
			{
				this.send_post (url,formatted);
			}
			CTAT.ToolTutor.sendToTutor (formatted);
		}
		else
		{
			this.send_post (url,formatted);
		}
	};
	/**
	*
	*/
	this.sendXMLNoBundle=function sendXMLNoBundle (aMessage)
	{
		pointer.ctatdebug ('sendXMLNoBundle ('+aMessage+')');

		bundleFormatter=aMessage;

		var url=this.getURL ();

		var formatted=aMessage;

		if (aMessage.indexOf ("<?xml")==-1)
		{
			formatted=("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+aMessage);
		}

		var vars=flashVars.getRawFlashVars ();

		if (this.getUseCommSettings() && pointer.hasJavaScriptConnection())
		{
			if(pointer.hasWebSocket())  // post to other collaborators, then trace
			{
				this.send_post (aURL,formatted);
			}
			CTAT.ToolTutor.sendToTutor (formatted);
		}
		else
		{
			this.send_post (url,formatted);
		}
	};
	/**
	 * Send a message to the tutor.
	 * @param {string} aMessage message to send
	 * @param {string} aURL tutoring service address
	 * @param {boolean} sendOnlyToCollaborators if true, do not send to local tracer
	 */
	this.sendXMLURL=function sendXMLURL (aMessage, aURL, sendOnlyToCollaborators)
	{
		ctatdebug ('sendXMLURL ('+aURL+')');

		var formatted=aMessage;

		if (aMessage.indexOf ("<?xml")==-1)
		{
			formatted=("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+aMessage);
		}

		var vars=flashVars.getRawFlashVars ();

		pointer.ctatdebug ("Sending: " + formatted);

		if (this.getUseCommSettings() && pointer.hasJavaScriptConnection())
		{
			if(pointer.hasWebSocket())  // post to other collaborators, then trace
			{
				this.send_post (aURL,formatted);
				if(sendOnlyToCollaborators)
				{
					return;
				}
			}
			CTAT.ToolTutor.sendToTutor (formatted);
		}
		else
		{
			this.send_post (aURL,formatted);
		}
	};
	/**
	* We have to be very careful here. Websockets do not support regular gets but we
	* do need to provide this for such functions as navigating to the next tutor or
	* to call any other GET based controller. So for now this method ignores the
	* connection type and will instantiate our default HTTP connection object
	*/
	this.send=function send (url)
	{
		pointer.ctatdebug ('send ('+url+')');

		if (CTATGlobals.CommDisabled===true)
		{
			pointer.ctatdebug ("Communications globally disabled, please check your settings");
			return;
		}

		var newConnection=new CTATConnection (null);
		newConnection.setID (httpreqindex);
		httpreqindex++;

		if (newConnection.getHTTPObject ()===null)
		{
			alert ('Cannot create XMLHTTP instance');
			return false;
		}

		httprequests.push(newConnection);

		newConnection.setURL (url);
		newConnection.assignReceiveFunction (this.processReply);

		try
		{
			newConnection.getHTTPObject ().open ('GET', url, true);
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
			newConnection.getHTTPObject ().send (null);
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
	this.send_post_variables=function send_post_variables (url,variables)
	{
		pointer.ctatdebug ('send_post_variables ('+url+')');

		var vars=flashVars.getRawFlashVars ();
		var res=url;

		var data="";

		if (useOLIEncoding===false)
		{
			data=this.encodeVariables(variables);
		}
		else
		{
			data=this.encodeVariablesOLI(variables);
		}

		this.ctatdebug ("Sending: " + data);

		if (CTATGlobals.CommDisabled===true)
		{
			pointer.ctatdebug ("Communications globally disabled, please check your settings");
			return;
		}

		var newConnection=pointer.createConnection (CTATConfiguration.getRawFlashVars(),url, false);
		newConnection.setContentType ("application/x-www-form-urlencoded");

		httpreqindex++;

		/*
		if (newConnection.getHTTPObject ()===null)
		{
			alert ('Cannot create XMLHTTP instance');
			return false;
		}
		*/

		httprequests.push(newConnection);

		pointer.ctatdebug (data);

		if (messageListener!==null)
		{
			messageListener.processOutgoing (data);
		}

		newConnection.setURL (res);
		newConnection.setData (data);  // or CTATCommLibrary.addAuthenticityToken(data)
		newConnection.assignReceiveFunction (this.processReply);
		newConnection.send ();
	};
	/**
	*
	*/
	this.send_post=function send_post (url,data)
	{
		//useDebugging=true;

		var newConnection=null;

		ctatdebug ('send_post ('+url+')');

		if (CTATGlobals.CommDisabled===true)
		{
			pointer.ctatdebug ("Communications globally disabled, please check your settings");
			return;
		}

		ctatdebug ("Outoing on wire: " + data);

		var vars=flashVars.getRawFlashVars ();
		var res=url;

		//if (vars ['tutoring_service_communication']=='websocket')
	 	if (pointer.hasWebSocket ())
		{
			ctatdebug('opening websocket connection: url '+url);

			res=url.replace ("http:", "ws:");
		    	res=pointer.editSocketURLForHTTPS(res);
			
			ctatdebug('opening websocket connection to '+res);

			newConnection=pointer.createConnection (vars,res);

			newConnection.setData (data);

			pointer.ctatdebug (data);

			if (messageListener!==null)
			{
				messageListener.processOutgoing (data);
			}
		}
		else
		{
			// This should always result in a new connection object
			newConnection=pointer.createConnection (vars,res);
			newConnection.setData (data);  // or CTATCommLibrary.addAuthenticityToken(data)

			if (messageListener!==null)
			{
				messageListener.processOutgoing (data);
			}
		}

		newConnection.send ();

		//useDebugging=false;
	};
	/**
	*
	*/
	this.processReply=function processReply (argument)
	{
		pointer.ctatdebug ('processReply ('+httprequests.length+','+argument+')');

		var i=0;
		var found=false;
		var stringDelivery=[];

		var request=0;

		for (request=0;request<httprequests.length;request++)
		{
			var testConnection=httprequests [request];
			if(!testConnection || typeof(testConnection.getHTTPObject) != "function")
			{
			    continue;
			}
			var testObject=testConnection.getHTTPObject ();

			pointer.ctatdebug ("Testing connection entry " + request + ", readyState: " + testObject.readyState + ", consumed: "+ testConnection.getConsumed () + ", status: " + testObject.status);

			//>---------------------------------------------------------------------------------

			if ((testObject.readyState==4) && (testConnection.getConsumed ()===false))
			{
				pointer.ctatdebug ("Investigating request response: " + i + " -> " + testObject.status + ", for: " + testConnection.getURL ());

				found=false;

				if (testObject.status===0)
				{
					found=true;

					pointer.ctatdebug ("Received message (status 0): (" + testObject.responseText + "), status: " + testObject.status);

					if(useScrim)
					{
						CTATScrim.scrim.errorScrimUp(CTATGlobals.languageManager.filterString (connectionRefusedMessage));
					}
				}

				// 408 timeout response
				if(testObject.status==408)
				{
					found=true;

					pointer.ctatdebug ("Received message (status 408): " + testObject.responseText);

					if(useScrim)
					{
						CTATScrim.scrim.scrimDown();
					}
				}

				if (testObject.status==502)
				{
					found=true;

					pointer.ctatdebug ("Received message (status 502): " + testObject.responseText);

					if(useScrim)
					{
						CTATScrim.scrim.errorScrimUp(CTATGlobals.languageManager.filterString (ERROR_502));
					}
				}

				if (testObject.status==200)
				{
					found=true;

					pointer.ctatdebug ("Processing 200 response ...");

					if (httphandler!==null)
					{
						//pointer.ctatdebug ("Received message (status 200): " + testObject.responseText);

						stringDelivery.push (testObject.responseText);

						if (messageListener!==null)
						{
							messageListener.processIncoming (testObject.responseText);
						}
					}
					else
					{
						pointer.ctatdebug ("Error: httphandler is null, can't process response!");
					}
				}
				else if(httphandler && httphandler.processError)
				{
					found=true;
					pointer.ctatdebug ("Processing non-200 response, status "+testObject.status);
					httphandler.processError(testObject.status, testObject.responseText);
				}

				if (found===false)
				{
					pointer.ctatdebug ("Error: status not handled for: " + testObject.status);
				}

				pointer.ctatdebug ("Marking connection as consumed ...");

				testConnection.setConsumed (true); // make sure we don't call it again!
			}
			else
			{
				if (testObject.readyState===0)
				{
					pointer.ctatdebug ("Received message (status 0, request not initialized)");
				}

				if (testObject.readyState===1)
				{
					pointer.ctatdebug ("Received message (status 1, server connection established)");
				}

				if (testObject.readyState===2)
				{
					pointer.ctatdebug ("Received message (status 2, request received)");
				}

				if (testObject.readyState===3)
				{
					pointer.ctatdebug ("Received message (status 3, processing request)");
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
				pointer.ctatdebug ("Processing incoming message: " + aMessage);

				httphandler.processMessage (aMessage);
			}
		}
	};

	/**
	 * Pass a WebSocket reply, which has no HTTP header, to the registered messageListener and httphandler.
	 * @param {string} aMessage expect data as string
	 */
	this.processWSReply = function(aMessage)
	{
		pointer.ctatdebug ("processWSReply() length "+(aMessage === null ? null : aMessage.toString().length));

		if(typeof(aMessage) == "string")
		{
			if (messageListener!==null)
			{
				messageListener.processIncoming (aMessage);
			}

			if (aMessage.indexOf ("status=success")!=-1)
			{
				pointer.ctatdebug ("processWSReply() logging success message received, not propagating to message handler: "+aMessage);
			}
			else
			{
				pointer.ctatdebug ("Processing incoming message: " + aMessage);

				httphandler.processMessage (aMessage);
			}
		}
	};

	/**
	 * Respond to a WebSocket close event by putting up a scrim asking the user to close
	 * the browser page.
	 * @param {object:Event} evt event from WebSocket interface
	 */
	this.processWSClose = function(evt)
	{
		pointer.ctatdebug("processWSClose("+evt+")");
		if(evt instanceof CloseEvent)
		{
			pointer.ctatdebug("CloseEvent: code "+evt.code+", reason "+evt.reason+", wasClean "+evt.wasClean);
		}
		if(CTATLMS.is.Authoring())
		{
			CTATScrim.scrim.scrimUp(CTATGlobals.languageManager.filterString("AUTHORPLEASECLOSE"));
		}
		else  		// FIXME for use with WebSockets at student time--refine?
		{
			CTATScrim.scrim.handleTSDisconnect();
		}
	};

	/**
	*
	*/
	this.cleanup=function cleanup ()
	{
		pointer.ctatdebug ("cleanup ()");

		//var i=0;
		var count=0;
		var found=false;
		var clean=false;

		while (clean===false)
		{
			found=this.checkEntries ();

			if (found===false)
			{
				clean=true;
			}
			else
			{
				count++;
			}
		}

		pointer.ctatdebug ("Removed " + count + " entries");
	};
	/**
	*
	*/
	this.checkEntries=function checkEntries ()
	{
		pointer.ctatdebug ("checkEntries ("+httprequests.length+")");

		var i=0;
		var requests=0;

		for (requests=0; requests<httprequests.length; requests++)
		{
			var testConnection=httprequests [requests];

			if(!testConnection)
			{
				continue;
			}
			else if (testConnection.getConsumed()===true)
			{
				pointer.ctatdebug ("Removing : " + testConnection.getID ());

				httprequests.splice(i, 1);
				return (true);  // why return? why not cleanup all completed?
			}
			else if(typeof(testConnection.getHTTPObject) == "function")
			{
				var testObject=testConnection.getHTTPObject ();

				pointer.ctatdebug ("Check, readyState: " + testObject.readyState + ", consumed: " + testConnection.getConsumed ());
			}

			i++;
		}

		return (false);
	};

	this.retrieveProblemFile = function(fileName, parser, handler)
	{
		var exRegex = /\.([A-z]*)$/;
		var extension = exRegex.exec(fileName)[1];
		if (extension.toLowerCase() === "brd") {
			console.log('got an xml file');
			this.retrieveXMLFile(fileName, parser, handler);
		} else if (extension.toLowerCase() === "nools") {
			console.log('got a nools file');
			handler.processNools(fileName);
		}
	}
	
	this.getFileRequest = function(fileName, successCbk, errCbk)
	{
		var toCheck = location.href ? location.href : fileName,
			isFileProto = (toCheck.indexOf("file://") != -1) ? true : false;
		
		if (!location.href)
		{
			pointer.ctatdebug ("We can't check location.href in the current configuration");
		}
		
		if (isFileProto) {
			var errXFile = new CTATTutorMessageBuilder().createErrorMessage("",
					"Unable to load: "+fileName+" - You are trying to use the file:// protocol, which is not allowed in this browser.");
			pointer.ctatdebug("onerror GET for fileName "+errXFile);
			CTAT.ToolTutor.sendToInterface(errXFile, true);
			return;
		}
		
		fileTobeLoaded=fileName;

		var newConnection=new CTATConnection ();
		var xmlhttp=newConnection.getHTTPObject ();
		xmlhttp.onreadystatechange = function()
		{
			pointer.ctatdebug("onready... GET for xmlFile xmlhttp.readyState "+xmlhttp.readyState+", .status "+xmlhttp.status+", parser "+parser);

			if (xmlhttp.readyState != 4)
			{
				return;
			}

			if (xmlhttp.status == 200)
			{
				successCbk(xmlhttp);
			}

			if (xmlhttp.status == 404)
			{
				var err404 = new CTATTutorMessageBuilder().createErrorMessage(
						"Unable to download file", "("+ fileTobeLoaded + ") not found");
				pointer.ctatdebug("Error loading xmlFile "+fileTobeLoaded+": "+err404);
				CTAT.ToolTutor.sendToInterface(err404, true);
				return;
			}
		};

		xmlhttp.onerror = errCbk;
		
		return xmlhttp;
	}
	
	/**
     * Get the BRD file; parse it on receipt. Returns as soon as request is posted.
	 * @param {string} XML file URL
	 * @param {CTATXML} an instance of the CTATXML parser
	 * @param {function} handler function for when the data has been retrieved, argument is the data
     */
	this.retrieveXMLFile = function retrieveXMLFile(xmlFile, parser, handler)
	{
		pointer.ctatdebug("retrieveXMLFile ("+xmlFile+")");
		var req = this.getFileRequest(xmlFile, function() {
				var xmlDoc = null;

				if (!req.responseXML)
				{
					pointer.ctatdebug("parsing brd xml using node");
					xmlDoc = (parser = new CTATXML()).parseXML(req.responseText);
				}
				else
				{
					pointer.ctatdebug("parsing brd xml using something else");
					xmlDoc = req.responseXML.documentElement;
				}
				if(xmlDoc === null)
				{
					var errXNull = new CTATTutorMessageBuilder().createErrorMessage(
							"Error parsing xmlFile "+ fileTobeLoaded);
					pointer.ctatdebug("Error parsing xmlFile "+fileTobeLoaded+": "+errXNull);
					CTAT.ToolTutor.sendToInterface(errXNull, true);
					return;
				}

				handler.processXML (xmlDoc);
			},function()
			{
				var errHTTP = new CTATTutorMessageBuilder().createErrorMessage("",
						"Unable to load: "+fileTobeLoaded+" - Either you are trying to use the  file:// protocol, there is a firewall between the tutor and BRD, or the BRD is on a different domain and permission to retrieve data from that server is denied.");
				pointer.ctatdebug("onerror GET for xmlFile "+errHTTP);
				CTAT.ToolTutor.sendToInterface(errHTTP, true);

				handler.processXML (null);
			}
		);

		req.open("GET", xmlFile, true);   // true => async
		req.setRequestHeader ("Content-type","text/plain");
		req.send();
	};

	/**
     * Get a JSON file. The file is not parsed but returned as a string instead
	 * @param {string} XML file URL
	 * @param {CTATXML} an instance of the CTATXML parser
	 * @param {function} handler function for when the data has been retrieved, argument is the data
     */
	this.retrieveJSONFile = function retrieveJSONFile(jsonFile, handler)
	{
		pointer.ctatdebug("retrieveJSONFile ("+jsonFile+")");

		var req = this.getFileRequest(jsonFile, function()
			{
				//pointer.ctatdebug("onready... GET for JSON File jsonhttp.readyState "+jsonhttp.readyState+", .status "+jsonhttp.status);

				if (req.readyState != 4)
				{
					//pointer.ctatdebug ("Error ("+jsonhttp.status+") retrieving file: " + jsonFile);
					return;
				}

				if (req.status == 200)
				{
					pointer.ctatdebug ("Successfully retrieved file: " + jsonFile);
					handler (JSON.parse(req.responseText));
					return;
				}
			}, function()
			{
				var errMsg = new CTATTutorMessageBuilder().createErrorMessage("", "Unable to load: "+fileTobeLoaded+" - Either you are trying to use the  file:// protocol, there is a firewall between the tutor and BRD, or the BRD is on a different domain and permission to retrieve data from that server is denied.");
				pointer.ctatdebug("onerror GET for xmlFile "+errMsg);
				CTAT.ToolTutor.sendToInterface(errMsg, true);
				return;
			}
		);

		req.open("GET", jsonFile, true);   // true => async
		req.setRequestHeader ("Content-type","text/plain");
		req.send();
	};

	/**
     * Get a (text formatted) file. The file is not parsed but returned as a string instead
	 * @param {string} file URL
	 * @param {function} handler function for when the data has been retrieved, argument is the data
     */
	this.retrieveFile = function retrieveFile(txtFile, handler)
	{
		pointer.ctatdebug("retrieveFile ("+txtFile+")");

		var txthttp=this.getFileRequest(txtFile, function()
			{
				//pointer.ctatdebug("onready... GET for JSON File txthttp.readyState "+txthttp.readyState+", .status "+txthttp.status);

				if (txthttp.readyState != 4)
				{
					//handler.processXML (null);
					//pointer.ctatdebug ("Error ("+txthttp.status+") retrieving file: " + txtFile);
					return;
				}

				if (txthttp.status == 200)
				{
					pointer.ctatdebug ("Successfully retrieved file: " + txtFile);
					handler (txthttp.responseText);
					return;
				}
			}, function()
			{
				var errHTTP = new CTATTutorMessageBuilder().createErrorMessage("",
						"Unable to load: "+fileTobeLoaded+" - Either you are trying to use the  file:// protocol, there is a firewall between the tutor and BRD, or the BRD is on a different domain and permission to retrieve data from that server is denied.");
				pointer.ctatdebug("onerror GET for xmlFile "+errHTTP);
				CTAT.ToolTutor.sendToInterface(errHTTP, true);
				return;
			}
		);

		txthttp.open("GET", txtFile, true);   // true => async
		txthttp.setRequestHeader ("Content-type","text/plain");
		txthttp.send();
	};
};

/**
 * @param {Array<string>} or string new value for CTATCommLibrary.authenticityToken.
 */
CTATCommLibrary.setAuthenticityToken = function(token)
{
	if(Array.isArray(token))
	{
		token = (token.length > 0 ? token[0] : "");
	}
	if(token)
	{
		CTATCommLibrary.authenticityToken = decodeURIComponent(token);
	}
};

/**
 * @param {string} data append to this string
 * @return {string} edited string
 */
CTATCommLibrary.getAuthenticityToken = function(data)
{
	return CTATCommLibrary.authenticityToken;
};

/**
 * @param {string} data append to this string
 * @return {string} edited string
 */
CTATCommLibrary.addAuthenticityToken = function(data)
{
	if (CTATCommLibrary.authenticityToken &&
		(data === "" ||
		 (data.search(/authenticity_token=/) < 0 &&
		  data.search(/^[?]?[^?&=><]+=?[^&=]*(&[^?&=><]+=?[^&=]*)*$/) >= 0)))
	{
		return "" + data + (data ? "&" : "") + "authenticity_token=" +
			encodeURIComponent(CTATCommLibrary.authenticityToken);
	}
	return data;
};

CTATCommLibrary.prototype = Object.create(CTATBase.prototype);
CTATCommLibrary.prototype.constructor = CTATCommLibrary;

/**
 * Adjust the protocol (to "wss:") and port (to the remoteSocketSecurePort) in a websocket URL for secure connections.
 * @param {string} url URL to edit
 * @return edited URL
 */
CTATCommLibrary.prototype.editSocketURLForHTTPS = function(url)
{
	if(url && window.top.location.protocol === 'https:')
	{
		ctatdebug("We're embedded in an https window, using wss...");  //replace protocol
		url = url.replace('ws:', 'wss:');		
		let securePort = CTATConfiguration.get('remoteSocketSecurePort');  //replace port
		if(securePort)
		{
		    let regex = /(wss:\/\/[^:]{1,}:)\d{1,}/;
		    url = url.replace(regex, "$1"+securePort);
		}
		ctatdebug('url after replace: '+url);
	}
	return url;
};
