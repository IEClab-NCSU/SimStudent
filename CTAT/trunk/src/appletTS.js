var interfaceObject = null;
var tutorObject = null;
var tutorMessages = [];

// workaround for older browsers
if ( !Array.prototype.forEach ) 
{
  Array.prototype.forEach = function(fn, scope) 
  {
    for(var i = 0, len = this.length; i < len; ++i) 
	{
      fn.call(scope, this[i], i, this);
    }
  }
}

// workaround for older browsers that have no console
if (!window.console)
{
	console = {log: function() {}};
}	

/**
*
*/
window.registerTutor = function(tutor) 
{
    console.log('registerTutor', tutor);
	
    if (tutorObject = tutor) 
	{
		tutorMessages.forEach(sendToTutor);
		tutorMessages = [];
    }
}

/**
*
*/
window.sendToInterface = function(message) 
{
	if (interfaceObject!=null)
	{
		console.log('sendToInterface', interfaceObject, message);
		interfaceObject.receiveFromTutor(message);
	}
	else	
	{
		console.log ('No interfaceObject available, assuming straight HTML5 js interface');
	
		commMessageHandler.processMessage (message);
	}
}

/**
*
*/
window.registerInterface = function(objectId) 
{
    console.log('registerInterface in ' + (parent === self ? 'parent' : 'dialog'), objectId);
	
    var object = document.getElementById(objectId);
	
    if (parent !== self) 
	{
		parent.registerInterface(object);
    }
	else 
	{
		interfaceObject = object;
    }
}

/**
*
*/
window.sendToTutor = function(message) 
{
    console.log('sendToTutor in ' + (parent === self ? 'parent' : 'dialog'), tutorObject, message);
	
    if (tutorObject) 
	{
      tutorObject.receiveFromInterface(message);
    }
	else if (parent !== self) 
	{
      parent.sendToTutor(message);
    }
	else 
	{
      tutorMessages.push(message);
    }
}
