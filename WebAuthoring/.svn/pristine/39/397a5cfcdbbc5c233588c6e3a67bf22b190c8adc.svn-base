/* This class manages the post requests to the server, for the case in which
   we need to send information from the web browser to the Java server. It uses
   JQuery to send post requests.
*/
goog.provide('CTATJavaPost');
goog.require('CTATBase');

CTATJavaPost = function()
{
  CTATBase.call(this, "CTATJavaPost", "javaPost");//debugging tools
  var vars = flashVars.getRawFlashVars();

  /* This method will take any string and send it to the server via post request.
     No processing done beforehand, and not put in name=value format because
     the CTATCommLibrary send_post method does not either do that either,
     so I'm staying consistent for the sake of the servlet code. In order
     to switch, modify
     $.post("TestServlet",message).done(succ).fail(fail);
     to
     $.post("TestServlet",{name:message}).done(succ).fail(fail);
  */
  this.sendSinglePostMessage=function sendSinglePostMessage(message,type)
  {
    var pointer=this;//so that we can debug inside succ and fail
    if(!type) type="text";
    this.ctatdebug("Sending post message");

    var succ = function(response)//called if the post request succeeds
    {
      useDebugging=true;
      pointer.ctatdebug("Successfully sent information with text:"+response);
      useDebugging=false;
    }

    var fail = function()//called if the post request fails
    {
      useDebugging=true;
      pointer.ctatdebug("Failed to send message");
      useDebugging=false;
    }

    //one way to add callback functions is $.post(...).done(f).fail(g)
    $.post(vars["remoteSocketURL"],message).done(succ).fail(fail);
  }
  /* This is a demo method called for the sake of sending the value of the
     text inputs to the server. Not really of any concern except to display
     the correctness of the connection
  */
  this.sendComponentInfo=function sendComponentInfo()
  {
    var s = "";
    for(var i = 0; i < components.length;i++)
    {
      var ref = components[i];
      if(ref.type != "CTATTextArea") continue;//components that aren't text inputs
      if(ref.getComponentPointer().getValue() == "") continue;//components that have no inputs
      s+="Name: "+ref.name+"; Text: "+ref.getComponentPointer().getValue()+"\n";
      console.log(ref);
    }
    this.ctatdebug(s);
    var pointer = this;
    $.post(vars["remoteSocketURL"],
          s,
          function (strings,success)//function called if post request succeeds
          {
            useDebugging=true;
            pointer.ctatdebug("Successfully sent information with text:"+strings);
            useDebugging=false;
          },
          "text"//response expects normal plain text as response
          );
  }

  /* This just takes a component and creates an object that
     contains any useful information we would need from the object. We can't
     just use ref itself since it contains too much info and in fact has
     a circular structure, making it an invalid JSON string
  */
  this.createJSONObject=function createJSONObject(ref)
  {
    var c=new Object();
    c.name=ref.name;
    c.type=ref.type;
    c.className=ref.getClassName();
    return c;
  }

  /* Basic method to send information about all the components to the server.
     Right now we simply iterate through the list of components, extract
     any information we need using createJSONObject, and add it into
     an array, which we send via JQuery post method as a JSON string.
  */
  this.sendComponentInfo2=function sendComponentInfo2()
  {
    this.ctatdebug("Retrieving components info");
    var comps=new Array();//will contain all component information
    for(var i=0;i<components.length;i++)
    {
      var ref=components[i];
      var a = this.createJSONObject(ref);
      if(a!=null)
        comps.push(a);//extract important information and adds it to array
    }
    var pointer = this;//so that we can debug inside the lambda function in post

    $.post(vars["remoteSocketURL"],
          JSON.stringify(comps),
          function (strings,success)//function called if post request succeeds
          {
            useDebugging=true;
            pointer.debug("Successfully sent information with text:"+strings);
            useDebugging=false;
          },
          "text"
          );
  }

}
CTATJavaPost.prototype = Object.create(CTATBase.prototype);
CTATJavaPost.prototype.constructor = CTATJavaPost;
