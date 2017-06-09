/**
 * @fileoverview Defines CTAT.ToolTutor which handles communication between
 * the tutoring service and the interface. This will used functions supplied
 * by external sources defined in the global namespace when they are available.
 *
 * @author $Author: mdb91 $
 * @version $Revision: 24393 $
 */
/*
 $Date: 2016-11-28 17:24:35 -0600 (週一, 28 十一月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTAT/ToolTutor.js $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTAT.ToolTutor');

goog.require('CTATGlobals');

CTAT.ToolTutor = {
		interfaceMessages: [],
		tutorMessages: []
};

/**
 * The current registered interface.
 */
Object.defineProperty(CTAT.ToolTutor, 'message_handler', {
	enumerable: true,
	get: function() {
		if (window.hasOwnProperty('getInterfaceObject') && typeof(window['getInterfaceObject'])==='function')
			return window['getInterfaceObject']();
		if (window.hasOwnProperty('interfaceObject'))
			return window['interfaceObject'];
		if (!this.hasOwnProperty('_interface'))
			this['_interface'] = null;
		return this['_interface'];
	},
	set: function(obj) {
		if (window.hasOwnProperty('interfaceObject'))
			window['interfaceObject'] = obj;
		else
			this['_interface'] = obj;
		return obj;
	}
});
/**
 * The current registered tutoring service.
 */
Object.defineProperty(CTAT.ToolTutor, 'tutor', {
	enumerable: true,
	get: function() {
		if (window.hasOwnProperty('getTutorObject') && typeof(window['getTutorObject'])==='function')
			return window['getTutorObject']();
		if (window.hasOwnProperty('tutorObject'))
			return window['tutorObject'];
		if (!this.hasOwnProperty('_tutor'))
			this['_tutor'] = null;
		return this['_tutor'];
	},
	set: function(obj) {
		if (window.hasOwnProperty('tutorObject'))
			window['tutorObject'] = obj;
		else
			this['_tutor'] = obj;
		return obj;
	}
});
/**
 * Specify a tutoring service
 * @param tutor
 */
CTAT.ToolTutor.registerTutor = function(tutor) {
	if (window.hasOwnProperty('registerTutor') && typeof(window['registerTutor'])==='function')
		return window['registerTutor'](tutor);
	//console.log('CTAT.ToolTutor.registerTutor', tutor);
	// TODO: if (tutor===undefined) tutor = new CTATExamleTracer(); // probably not so that we are not forced to include js tutoring service.
	this.tutor = tutor;
	if (this.tutor) {
		while(this.tutorMessages.length>0) {
			this.sendToTutor(this.tutorMessages.shift());
		}
	}
};
/**
 * Send a message to the interface
 * @param message {String}
 */
CTAT.ToolTutor.sendToInterface = function(message) {
	ctatdebug("CTAT.ToolTutor.sendToInterface()\n  "+message);
	if (window.hasOwnProperty('sendToInterface') && typeof(window['sendToInterface'])==='function')
		return window['sendToInterface'].apply(null,arguments); // needed for nodejs version of sendToInterface
	if (this.message_handler) {
		//console.log ('CTAT.ToolTutor.sendToInterface: interfaceObject available', message);
		return this.message_handler.receiveFromTutor(message);
	} else if(commMessageHandler) { // declared in CTATGlobals
		//console.log ('sendToInterface: no interfaceObject but commMessageHandler available', message);
		return commMessageHandler.processMessage (message);
	} else {
		//console.log ('sendToInterface: no interfaceObject or commMessageHandler: queue for later interfaceObject', message);
		return this.interfaceMessages.push(message);
	}
};
/**
 * Register an interface message handler.
 * @param message_handler {String,node}
 */
CTAT.ToolTutor.registerInterfaceMessageHandler = function(message_handler) {
	ctatdebug("CTAT.ToolTutor.registerInterfaceMessageHandler(" + message_handler + ")");
	if (typeof message_handler === 'string') {
		message_handler = document.getElementById(message_handler);
	}
	this.message_handler = message_handler;
	if (this.message_handler) { // if an interface is registered, dump all accumulated messages.
		while (this.interfaceMessages.length>0) {
			this.sendToInterface(this.interfaceMessages.shift());
		}
	}
};
/**
 * Register an interface.
 * @param message_handler {String,node}
 */
CTAT.ToolTutor.registerInterface = function(message_handler) {
	if (window.hasOwnProperty('registerInterface') && typeof(window['registerInterface'])==='function')
		return window.registerInterface(message_handler);
	this.registerInterfaceMessageHandler(message_handler);
};
/**
 * Send a message to the tutoring service.
 * @param message {String} the message to send
 * @returns
 */
CTAT.ToolTutor.sendToTutor = function(message) {
	if (window.hasOwnProperty('sendToTutor') && typeof(window['sendToTutor'])==='function')
		return window['sendToTutor'](message);
	//console.log("CTAT.ToolTutor.sendToTutor(" + message + ") tutorObject " + this.tutor);
	if (this.tutor) {
		return this.tutor.receiveFromInterface(message);
	} else {
		return this.tutorMessages.push(message);
	}
};
/**
 * Reboot the tool and tutor. If location.replace() and setTimeout() are
 * defined, delays before calling location.replace to let log flush.
 * @param {string} reason a caller identifier, for debugging
 * @return {boolean} true if able to call location.replace(location.href); else false
 */
CTAT.ToolTutor.reboot = function(reason) {
	var href = ((typeof(location) != "undefined" && location && location.href) ? location.href : null);
	//console.log("CTAT.ToolTutor.reboot() with reason: "+reason+"; href "+href);
	href && console.log('ToolTutor.reboot(), setting location to '+href);
	if(href && !CTATConfiguration.get('editorMode'))
	{
		if(typeof(setTimeout) == "function")  // delay to drain log
		{
			setTimeout(location.replace.bind(location), 600, href);
		}
		else
		{
			location.replace (href);  // say goodnight...
		}
		return true;
	}
	else
	{
		return false;             // see if the caller can do it
	}
};