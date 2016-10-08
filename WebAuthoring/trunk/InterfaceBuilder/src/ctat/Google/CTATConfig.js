/**
 * @NotOnlyCurrentDoc
 */
 
/**
* Values can be one of: 'ctat','google','undefined'
*/

var platform="google";
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
  debug ('Script properties:');
    
  var scriptProperties = PropertiesService.getScriptProperties();
  data = scriptProperties.getProperties();
  for (var scriptKkey in data) 
  {
    debug('[ScriptProperties] Key: %s, Value: %s', scriptKkey, data[scriptKkey]);
  }
  
  debug ('Document properties:');
  
  var documentProperties = PropertiesService.getDocumentProperties();
  data = documentProperties.getProperties();
  for (var documentKey in data) 
  {
    debug('[ScriptProperties] Key: %s, Value: %s', documentKey, data[documentKey]);
  }
  
  debug ('User properties:');
  
  var userProperties = PropertiesService.getUserProperties();
  data = userProperties.getProperties();
  for (var userKey in data) 
  {
    debug('[UserProperties] Key: %s, Value: %s', userKey, data[userKey]);
  }  
        
  debug('UserAgent: ' + UiApp.getUserAgent());
}
