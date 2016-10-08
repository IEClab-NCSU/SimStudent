/* This class manages polling for new messages from the Java server. It uses
   EventSource to receive server-side messages. These should come as JSON strings
   representing objects with "id" and "message" as fields. We use "id" to identify
   separate messages so that we don't parse the same message more than once (which
   is guaranteed to happen otherwise since we poll for messages). We store the "id",
   "message" pairs in an associative array to keep track of it all and only parse
   the message if the id is not stored as a key yet.
*/

goog.provide('CTATJavaGet');
goog.require('CTATBase');
CTATJavaGet = function()
{
  CTATBase.call(this, "CTATJavaGet", "javaGet");//use for debugging
  var vars=flashVars.getRawFlashVars ();
  var evtSources = new Array();
  var delayTime = 200;//milliseconds



  /* Made separate so that you can simply expand this method without needing to touch
     the eventSource.onmessage method
  */
  this.processMessage=function processMessage(message)
  {
    //useDebugging=true;
    this.ctatdebug(message);
    //console.log(message);
    //useDebugging=false;
    commMessageHandler.processMessage(message);
  };

  this.init=function init()
  {
    var pointer = this;//so that we can parse the message from inside the lambda function
    var onmessage = function(event) {
        var message = event.data;
        if(message && message.length>0){//case that there is a message in the first place
          pointer.processMessage(message);//do whatever we want with the message
        }
      };

    var push = function(){
      var eventSource = new EventSource(vars["remoteSocketURL"] + "?session_id="+vars["session_id"]);
      evtSources.push(eventSource);
      eventSource.onmessage = onmessage;
    };

    //create multiple EventSource objects with delays in between to increase polling rate
    for(var x = 0; x < (3000 / delayTime); x++){//3000 ms is polling rate of one EventSource object
      window.setTimeout(push,delayTime * x);

    }
  };
  this.init();


}
CTATJavaGet.prototype = Object.create(CTATBase.prototype);
CTATJavaGet.prototype.constructor = CTATJavaGet;
