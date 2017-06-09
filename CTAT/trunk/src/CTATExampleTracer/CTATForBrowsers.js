/**
 * This file initializes the JavaScript Example Tracer object. If the tutor is a flash tutor,
 * this function also sets up an interfaceObject for communcation with Flash.
 */

goog.provide('CTATForBrowsers');

/*
 * Last Modified: sewall, 13 August 2014
 */

// workaround for older browsers
if ( !Array.prototype.forEach ) 
{
  Array.prototype.forEach = function(fn, scope) 
  {
    for(var i = 0, len = this.length; i < len; ++i) 
	{
      fn.call(scope, this[i], i, this);
    }
  };
}

// workaround for older browsers that have no console
if (!window.console)
{
	console = {log: function() {}};
}	

var interfaceMessages, sendToTutor, tutorMessages, tutorObject;
var CTATForBrowsers = true;

interfaceObject = null;

tutorObject = null;

interfaceMessages = [];

tutorMessages = [];

window.getInterfaceObject = function() {
  return interfaceObject;
};

window.getTutorObject = function() {
  return tutorObject;
};

window.registerTutor = function(tutor) {
  //console.log ('registerTutor: tutorObject', tutor);
  tutorObject = tutor;
  if (tutor)
  {
	tutorMessages.forEach(sendToTutor);
    tutorMessages = [];
  }
};

window.sendToInterface = function(message) {
  if (interfaceObject) {
	//console.log ('sendToInterface: interfaceObject available', message);
	return interfaceObject.receiveFromTutor(message);
  } else if(commMessageHandler) {
	//console.log ('sendToInterface: no interfaceObject but commMessageHandler available', message);
	commMessageHandler.processMessage (message);
  } else {
	//console.log ('sendToInterface: no interfaceObject or commMessageHandler: queue for later interfaceObject', message);
	return interfaceMessages.push(message);
  }
};

window.registerInterface = function(object) {
  //console.log("window.registerInterface(" + object + ")");
  if (typeof object === 'string') {
	object = document.getElementById(object);
  }
  interfaceObject = object;
  if (interfaceObject)
  {
	interfaceMessages.forEach(sendToInterface);
    interfaceMessages = [];
  }
};

window.sendToTutor = function(message) {
  //console.log("sendToTutor(" + message + ") tutorObject " + tutorObject);
  if (tutorObject) {
	return tutorObject.receiveFromInterface(message);
  } else {
	return tutorMessages.push(message);
  }
};
