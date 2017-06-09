/**-----------------------------------------------------------------------------
 $Author$
 $Date$
 $HeadURL$
 $Revision$

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATWSConnection');

goog.require('CTATBase');
goog.require('CTATConnectionBase');

/**
 * @param {object} substVars needs property session_id if no real flashVars
 */
CTATWSConnection = function(substVars)
{
	CTATConnectionBase.call(this, "CTATWSConnection");

	var substituteFlashVars = substVars;
	var data="";

	var consumed=false;
	var pointer = this;

	var output;
	var websocket=null;

	var outgoingQueue=[];
	var ready=false;

	var before=0;
	var after=0;

	var receiveFunction=null;
	var closeFunction=null;

	pointer.setSocketType ("ws");

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
	};

	/**
	* @param {function} new value for var closeFunction
	*/
	this.assignCloseFunction = function(aFunction)
	{
		closeFunction=aFunction;
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
	this.init=function init ()
	{
		ctatdebug ("init ("+pointer.getURL ()+"); websocket "+websocket);

		if(websocket != null)
		{
			return;
		}
		
		websocket = new WebSocket(pointer.getURL());

		websocket.addEventListener('open', function(e)
		{
			ctatdebug('STATUS: open');

			ready=true;

			ctatdebug('Connection open, flushing outgoing queue ...');

			var timeDriver = new Date();
			before = timeDriver.getTime();

			if (outgoingQueue.length>0)
			{
				for (var i=0;i<outgoingQueue.length;i++)
				{
					websocket.send (outgoingQueue [i]);
				}

				outgoingQueue=[];
			}
		});

		websocket.addEventListener('message', function(e)
		{
			var timeDriver = new Date();
			after = timeDriver.getTime();

			ctatdebug('STATUS: message after '+(after-before)+' ms');
			ctatdebug('Received: ' + e.data);

			if (receiveFunction)
			{
				receiveFunction (e.data);
			}
			else
			{
				ctatdebug('Error: no processing function provided');
			}
		});

		/*
		 * Event handler for close events: call websocket.close() with code 1000, which appears
		 * to be the only standard (in range 1000-2000) code accepted by Chrome's WebSocket.
		 */
		websocket.addEventListener('close', function(e)
		{
			ctatdebug('STATUS: close; '+(e ? 'code '+e.code+', reason '+e.reason : 'no event'));

			ready=false;

			var reason = (e ? (e.reason ? e.reason : 'received close code '+e.code) : 'no close event received');

			websocket.close(1000, reason);  // 1000: see header comment

			if (closeFunction)
			{
				closeFunction (e);
			}
		});

		/*
		 * Event handler for error events: call websocket.close() with code 1000, which appears
		 * to be the only standard (in range 1000-2000) code accepted by Chrome's WebSocket.
		 */
		websocket.addEventListener('error', function(e)
		{
			ctatdebug('STATUS: error; '+(e ? e.type : 'no event'));

			ready=false;

			websocket.close(1000, 'client closing in response to error');  // 1000: see header comment
		});

		ctatdebug ("init () done");
	};

	/**
	*
	*/
	this.send=function send ()
	{
		ctatdebug('send ()');

		pointer.init ();

		if (ready===false)
		{
			ctatdebug('Connection not ready yet, storing ...');
			outgoingQueue.push (data);
		}
		else
		{
			ctatdebug('Connection ready, sending data ...');

			var timeDriver = new Date();
			before = timeDriver.getTime();
			websocket.send (data);
		}
	};
	
	this.getWebSocket = function()
	{
		return websocket;
	};
	
	this.close = function(opt_reason, opt_cbk)
	{
		ready=false;
		opt_reason = opt_reason || 'no reason';
		closeFunction = opt_cbk || closeFunction;
		websocket.close(1000, opt_reason);
	};
};

CTATWSConnection.prototype = Object.create(CTATConnectionBase.prototype);
CTATWSConnection.prototype.constructor = CTATWSConnection;
