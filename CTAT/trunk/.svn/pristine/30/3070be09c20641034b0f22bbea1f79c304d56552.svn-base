/**
 * @NotOnlyCurrentDoc
 */
//goog.provide('CTATConfig');  ?? want this??

/**
 * platform values can be one of: 'ctat','google','undefined'.
 * parserType values can be one of: 'xml','json'.
 */
CTATConfig = function()
{
}

Object.defineProperty(CTATConfig, "platform", {enumerable: false, configurable: false, writable: false, value: "ctat"});

// Either one of json or xml, this only configures the format of the expected incoming message from the tracer. Outgoing
// messages will still be in xml format.
Object.defineProperty(CTATConfig, "parserType", {enumerable: false, configurable: false, writable: false, value: "json"}); 

// Either one of true or false. Use this if you want to disable the use of the baked-in images that can be found in
// CTATBinaryImages.js. Those images are used by the done button, hint button and hint window. If you set this to false
// then image urls pointing to qa.pslc will be used instead by default
Object.defineProperty(CTATConfig, "embedImages", {enumerable: false, configurable: false, writable: false, value: "true"}); 

// var platform="google";
// var parserType="xml";
var started=false;
var window=new Object ();
window.event=new Object ();
window.event.keyCode=new Object ();
window.event.keyCode=0;

/**
*
*/
function alert (aMessage)
{
	Browser.msgBox (aMessage);
}

/**
*
*/
function inspectEnvironment ()
{
  ctatdebug ('Script properties:');
    
  var scriptProperties = PropertiesService.getScriptProperties();
  data = scriptProperties.getProperties();
  for (var scriptKkey in data) 
  {
    ctatdebug('[ScriptProperties] Key: %s, Value: %s', scriptKkey, data[scriptKkey]);
  }
  
  ctatdebug ('Document properties:');
  
  var documentProperties = PropertiesService.getDocumentProperties();
  data = documentProperties.getProperties();
  for (var documentKey in data) 
  {
    ctatdebug('[ScriptProperties] Key: %s, Value: %s', documentKey, data[documentKey]);
  }
  
  ctatdebug ('User properties:');
  
  var userProperties = PropertiesService.getUserProperties();
  data = userProperties.getProperties();
  for (var userKey in data) 
  {
    ctatdebug('[UserProperties] Key: %s, Value: %s', userKey, data[userKey]);
  }  
        
  ctatdebug('UserAgent: ' + UiApp.getUserAgent());
}
