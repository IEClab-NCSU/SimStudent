/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTAT/CTATConnection.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATConnection');

goog.require('CTATBase');
//goog.require('CTATGlobals');
/**
 *
 */
CTATConnection = function()
{
	CTATBase.call(this, "CTATConnection","connection");

	this.id=-1;
	this.consumed=false;
	this.url="";
	this.data="";
	this.httpObject=null;

	var pointer = this;

	/**
	*
	*/
	this.createHTTPObject=function createHTTPObject ()
	{
		pointer.ctatdebug ("createHTTPObject ()");

		this.httpObject=new XMLHttpRequest();

		if (window.XMLHttpRequest)
		{
			pointer.ctatdebug ("Creating regular XMLHttpRequest ...");

			this.httpObject=new XMLHttpRequest();
			//this.withCredentials = "true";

			if (this.httpObject.overrideMimeType)
			{
				this.httpObject.overrideMimeType('text/html');
			}
		}
		else if (window.ActiveXObject)
		{
			pointer.ctatdebug ("Detected window.ActiveXObject ...");

			// IE
			try
			{
				pointer.ctatdebug ("Creating Msxml2.XMLHTTP ...");

				this.httpObject=new ActiveXObject ("Msxml2.XMLHTTP");
			}
			catch (e)
			{
				try
				{
					pointer.ctatdebug ("Creating Microsoft.XMLHTTP ...");

					this.httpObject=new ActiveXObject("Microsoft.XMLHTTP");
				}
				catch (e)
				{
					alert ('Error: Unable to create HTTP Request Object: ' + e.message);
				}
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

		if (this.httpObject!=null)
		{
			var fVars=flashVars.getRawFlashVars ();
			var aSession=fVars ['session_id'];

			try
			{
				this.httpObject.setRequestHeader("Access-Control-Allow-Origin","*");
				//this.httpObject.setRequestHeader ("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");
				this.httpObject.setRequestHeader ("ctatsession",aSession);
				this.httpObject.setRequestHeader ("Content-type","application/x-www-form-urlencoded");
				//this.httpObject.setRequestHeader ("Content-type","application/xml");
				//this.httpObject.setRequestHeader ("Content-type","*/*");
				//this.httpObject.setRequestHeader ("Content-type","text/plain");
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

	this.createHTTPObject ();
}

CTATConnection.prototype = Object.create(CTATBase.prototype);
CTATConnection.prototype.constructor = CTATConnection;
