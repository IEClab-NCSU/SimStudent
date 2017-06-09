console.log("handlers.js", "top");

// Get the Google bootstrap script that converts goog.require() as node's require()
require("../../third-party/google/closure-library/closure/goog/bootstrap/nodejs.js");

getSafeElementById = function getSafeElementById(anID) 
{
    return null;
}

///////CTAT Object vars/////
inNode = true;

//Other dependencies
var xml	= require("./libXML.js");

XMLSerializer	= require("xmldom").XMLSerializer;
XMLHttpRequest	= require('xhr2');
XMLParser	= require("xmldom").DOMParser;
Entities	= require('html-entities').XmlEntities;

require("../../src/polyfills.js");
require("../../src/set.js");
require("../../src/CTAT/CTATConfig.js");

var interfaceObject, tutorObject, interfaceMessages, tutorMessages;

interfaceObject = null;

tutorObject = null;

interfaceMessages = [];

tutorMessages = [];

sendToTutor = function(message) {
  console.log("sendToTutor(" + message + ") tutorObject " + tutorObject);
  if (tutorObject) {
	return tutorObject.receiveFromInterface(message);
  } else {
	return tutorMessages.push(message);
  }
};

registerTutor = function(tutor) {
  tutorObject = tutor;
  if (tutor)
  {
	tutorMessages.forEach(sendToTutor);
    tutorMessages = [];
  }
};

require("../../third-party/google/closure-library/closure/goog/base.js");
require("../../CTAT-deps.js");

goog.require('CTATGlobalFunctions');
goog.require('CTATExampleTracer');

customconsole = null;

useDebuggingBasic = true;
useDebugging = true;

var exampleTracer = new CTATExampleTracer();
//Request handlers go here

var httpResponse;
var response = "";

alert = function(txt)
{
    ctatdebug(txt);
}

ctatdebug = function(txt)
{
    console.log(txt);
}

sendToTutor = function(aMessage)
{
    console.log("sendToTutor() aMessage", aMessage);
    exampleTracer.receiveFromInterface(aMessage);
}

sendToInterface = function(reply,endOfTransaction)
{
    response+=reply;
    response+="\n";
    if(!endOfTransaction)
    {        
        return;
    }
    httpResponse.write(response);
    httpResponse.end();
    response = "";
    console.log("Reply:   "+reply);    
}

function handleSendToTutor(response, data)
{
    console.log("Request: "+data);    
    httpResponse = response;
    exampleTracer.receiveFromInterface(data); 
    //receiveFromTutor("Reply to "+data);    // will be sendToTutor()
}

//Echoes recieved data back to client    
function echo(response,data)
{   
    response.write(data);
    response.end();
    console.log("Data Echoed.");    
}


exports.handleSendToTutor = handleSendToTutor;
exports.echo = echo;
//xports.sendToInterface = sendToInterface;