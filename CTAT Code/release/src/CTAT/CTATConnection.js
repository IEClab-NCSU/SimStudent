/**-----------------------------------------------------------------------------
 $Author: vvelsen $
 $Date: 2016-02-29 14:43:47 -0600 (週一, 29 二月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTAT/CTATConnection.js $
 $Revision: 23255 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATConnection');

goog.require('CTATBase');
goog.require('CTATConnectionBase');

/**
 * @param {object} substVars needs property session_id if no real flashVars
 */
CTATConnection = function(substVars)
{
	CTATConnectionBase.call(this, "CTATConnection");

	var substituteFlashVars = substVars;

	var data=null;
	var httpObject=null;

	var consumed=false;
	var pointer = this;

	var receiveFunction=null;

	var contentType="text/plain";

	pointer.setSocketType ("http");

	/**
	*
	*/
	this.setContentType=function setContentType (aVal)
	{
		contentType=aVal;
	};

	/**
	*
	*/
	this.getContentType=function getContentType ()
	{
		return (contentType);
	};

	/**
	*
	*/
	this.setConsumed=function setConsumed (aVal)
	{
		consumed=aVal;

		pointer.ctatdebug ("consumed: " + consumed);
	};

	/**
	*
	*/
	this.getConsumed=function getConsumed ()
	{
		pointer.ctatdebug ("consumed: " + consumed);

		return (consumed);
	};

	/**
	*
	*/
	this.assignReceiveFunction = function assignReceiveFunction(aFunction)
	{
		receiveFunction=aFunction;

		httpObject.onreadystatechange=aFunction;
	};

	/**
	*
	*/
	this.setData=function setData (aData)
	{
		data=aData;
	};
	/**
	*
	*/
	this.getData=function getData ()
	{
		return (data);
	};
	/**
	*
	*/
	this.getHTTPObject=function getHTTPObject ()
	{
		return (httpObject);
	};
	/**
	*
	*/
	this.createHTTPObject=function createHTTPObject ()
	{
		pointer.ctatdebug ("createHTTPObject ()");

		httpObject=new XMLHttpRequest();

		if (window.XMLHttpRequest)
		{
			pointer.ctatdebug ("Creating regular XMLHttpRequest ...");

			httpObject=new XMLHttpRequest();

			if (httpObject.overrideMimeType)
			{
				httpObject.overrideMimeType('text/html');
			}
		}
		else
		{
			pointer.ctatdebug ("Trying alternative HTTP object creation ...");

			if (window.ActiveXObject)
			{
				pointer.ctatdebug ("Detected window.ActiveXObject ...");

				// IE
				try
				{
					pointer.ctatdebug ("Creating Msxml2.XMLHTTP ...");

					httpObject=new ActiveXObject ("Msxml2.XMLHTTP");
				}
				catch (e)
				{
					try
					{
						pointer.ctatdebug ("Creating Microsoft.XMLHTTP ...");

						httpObject=new ActiveXObject("Microsoft.XMLHTTP");
					}
					catch (e)
					{
						alert ('Error: Unable to create HTTP Request Object: ' + e.message);
					}
				}
			}
			else
			{
				alert ("Internal error: an HTTP connection object could not be created");
			}
		}
	};
	/**
	* Do not call this method before open is called on the http object. If you do you will
	* get a Javascript exception that says: "an attempt was made to use an object that is
	* not or is no longer usable"
	*/
	this.init=function init ()
	{
		pointer.ctatdebug ("init ()");

		if (httpObject!==null)
		{
			var fVars=(flashVars ? flashVars.getRawFlashVars () : substituteFlashVars);

			var aSession=(fVars['session_id'] ? fVars['session_id'] : "dummySession");

			if (aSession=='dummySession')
			{
				pointer.ctatdebug ("Unable to find CTAT session information from environment, trying OLI ...");
			}

			try
			{
				/*
				if (useOLIEncoding==false)  // default for CORS compatibility, but OLI needs urlencoded, only
				{
					httpObject.setRequestHeader ("Content-type","text/plain");
				}
				else
				{
					httpObject.setRequestHeader ("Content-type","application/x-www-form-urlencoded");
				}
				*/

				httpObject.setRequestHeader ("Content-type",contentType);

				//httpObject.setRequestHeader ("Access-Control-Allow-Origin","*");
				//httpObject.setRequestHeader ("Access-Control-Allow-Headers","X-Custom-Header");
				httpObject.setRequestHeader ("ctatsession",aSession);
			}
			catch (err)
			{
				alert ("HTTP object creation error: " + err.message);
			}
		}
		else
			alert ("Internal error: http object is null right after creation");

		pointer.ctatdebug ("init () done");
	};

	/**
	*
	*/
	this.send=function send ()
	{
		pointer.ctatdebug ("send ()");

		pointer.getHTTPObject ().onerror = function()
		{
			pointer.ctatdebug ("Networking error!");
		};

		try
		{
			pointer.getHTTPObject ().open ('POST', pointer.getURL (), true);
		}
		catch(err)
		{
			pointer.ctatdebug ("Error in newConnection.httpObject.open: " + err.message);
			return;
		}

		pointer.init ();

		try
		{
			if (data)
			{
				pointer.getHTTPObject ().send (data);
			}
			else
			{
				pointer.getHTTPObject ().send ();
			}
		}
		catch(err)
		{
			this.ctatdebug ("Error in newConnection.httpObject.send: " + err.message);
			return;
		}
	};

	pointer.createHTTPObject ();
};

CTATConnection.prototype = Object.create(CTATConnectionBase.prototype);
CTATConnection.prototype.constructor = CTATConnection;
