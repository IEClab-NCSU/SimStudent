/**------------------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 
 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
*/

/**
 *  
 */
function CTATCommLibrary ()
{
	CTATBase.call(this, "CTATCommLibrary", "commlibrary");

	var httpreqindex=0;
	var httprequests=new Array ();
	var httphandler=null;
	var pointer = this;
	
	var requestList=new Array();
	
	/**
	 * http://beradrian.wordpress.com/2007/07/19/passing-post-parameters-with-ajax/
	 */
	this.assignHandler=function assignHandler (aHandler)
	{
		httphandler=aHandler;
	};
	/**
	 * 
	 */
	this.encodeVariables=function encodeVariables(variables)
	{
		this.debug ("encodeVariables ()");
		
	    var parameterString="";
	 	    
	    for (var i=0;i<variables.length;i++)
	    {
	    	var variable=variables [i];
	    	
	    	if (i>0)
	    	{
	    		parameterString+="&";
	    	}
	    	
	    	parameterString+=variable.name;
	    	parameterString+="=";
	    	parameterString+=encodeURI (variable.value);
	    }
	    
	    return (parameterString);
	};
	/**
	 * 
	 */
	this.sendXML=function sendXML (aMessage)
	{	
		this.debug ('sendXML ('+aMessage+')');
		
		var vars=flashVars.getRawFlashVars ();
				
		var prefix="http://";
		
		if (vars ['connection']=='https')
		{
			prefix="https://";
		}
				
		var url=prefix + vars ["remoteSocketURL"] + ":" + vars ["remoteSocketPort"];
		
		this.debug ('sendXML ('+url+')');
		
		var formatted=aMessage;
		
		if (aMessage.indexOf ("<?xml")==-1)		
			formatted=("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+aMessage);

		if (vars ['connection']=='javascript')
		{
			// See TutorBridge.js
			sendToTutor (formatted);
		}
		else		
		{
			this.send_post (url,formatted);
		}
	};
	/**
	 * 
	 */
	this.sendXMLURL=function sendXMLURL (aMessage,aURL)
	{
		this.debug ('sendXMLURL ('+aURL+')');
						
		var formatted=aMessage;
		
		if (aMessage.indexOf ("<?xml")==-1)	
		{
			formatted=("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+aMessage);
		}
			
		var vars=flashVars.getRawFlashVars ();			

		this.debug ("Sending: " + formatted);
		
		if (vars ['connection']=='javascript')
		{
			// See TutorBridge.js
			sendToTutor (formatted);
		}
		else		
		{
			this.send_post (aURL,formatted);
		}
	};
	/**
	 * 
	 */
	this.sendURLVariables=function sendURLVariables (aURL,variables)
	{
		this.debug ('sendURLVariables ('+aURL+')');
		
		var vars=flashVars.getRawFlashVars ();
				
		if (aMessage.indexOf ("<?xml")==-1)		
			this.send_post_variables (aURL,"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+aMessage,variables);
		else	
			this.send_post_variables (aURL,variables);
	};
	/**
	* 
	*/		
	this.send=function send (url) 
	{
		this.debug ('send ('+url+')');
		
		if (globalCommDisabled==true)
		{
			this.debug ("Communications globally disabled, please check your settings");
			return;
		}		
	
		var newConnection=new CTATConnection ();
		newConnection.id=httpreqindex;
		httpreqindex++;
		 
		if (newConnection.httpObject==null) 
		{
			alert ('Cannot create XMLHTTP instance');
			return false;
		}
		
		httprequests.push(newConnection);
			 	

		newConnection.url=url;
		newConnection.httpObject.onreadystatechange=this.processReply;	 
		
		try
		{
			newConnection.httpObject.open ('GET', url, true);
		}
		catch(err)
		{
			this.debug ("Error in newConnection.httpObject.open: " + err.message);
			return;			
		}	
		
		try
		{
			newConnection.init ();
		}
		catch(err)
		{
			this.debug ("Error in newConnection.init: " + err.message);
			return;			
		}					

		try
		{
			newConnection.httpObject.send (null);
		}
		catch(err)
		{
			this.debug ("Error in newConnection.httpObject.send: " + err.message);
			return;			
		}
	};	
	/**
	*
	*/
	this.send_post=function send_post (url,data) 
	{
		this.debug ('send_post ('+url+')');
		
		if (globalCommDisabled==true)
		{
			this.debug ("Communications globally disabled, please check your settings");
			return;
		}
		
		var newConnection=new CTATConnection ();
		newConnection.id=httpreqindex;
		httpreqindex++;		
		 
		if (newConnection.httpObject==null) 
		{
			alert ('Cannot create XMLHTTP instance');
			return false;
		}
		
		httprequests.push(newConnection);
					 			
		this.debug (data);
		
		newConnection.url=url;
		newConnection.data=data;
		newConnection.httpObject.onreadystatechange=this.processReply;
			
		try
		{			
			newConnection.httpObject.open ('POST', url, true);				
		}
		catch(err)
		{
			this.debug ("Error in newConnection.httpObject.open: " + err.message);
			return;			
		}			
			
		try
		{	
			newConnection.init ();			
		}
		catch(err)
		{
			this.debug ("Error in newConnection.init: " + err.message);		
			return;						
		}			
			
		try
		{	
			newConnection.httpObject.send (data);			
		}
		catch(err)
		{
			this.debug ("Error in newConnection.httpObject.send: " + err.message);		
			return;
		}
	};
	/**
	* 		var data=this.encodeVariables(variables);
	*/
	this.send_post_variables=function send_post_variables (url,variables) 
	{
		this.debug ('send_post_variables ('+url+')');

		var data=this.encodeVariables(variables);
		
		this.debug ("Sending: " + data);
		
		this.send_post (url,data);
	};	
	/**
	*
	*/
	this.processReply=function processReply () 
	{ 		
		pointer.debug ('processReply ('+httprequests.length+')');
				
		var i=0;
		var found=false;
		var stringDelivery=new Array ();
		
		var request=0;
		
		//for (req in httprequests)
		for (request=0;request<httprequests.length;request++)
		{    
			var testConnection=httprequests [request];
			var testObject=testConnection.httpObject;
		
			if ((testObject.readyState==4) && (testConnection.consumed==false)) 
			{
				pointer.debug ("Investigating request response: " + i + " -> " + testObject.status + ", for: " + testConnection.url);
				
				found=false;

				if (testObject.status==0)
				{				
					found=true;				
					
					pointer.debug (testObject.responseText);
					
					//ctatscrim.errorScrimUp("Error: Connection closed by foreign host.");
					//testConnection.consumed=true; // make sure we don't call it again!
				}
			
				// 408 timeout response
				if(testObject.status==408)
				{
					found=true;
				
					ctatscrim.scrimDown();

					// Re-send all requests after and including the one that timed out
					/*
					while(i < requestList.length)
					{
						requestList [i][0].send(requestList [i][1]);
						i++;
					}
					*/
					
					testConnection.consumed=true; // make sure we don't call it again!					
				}
				
				if (testObject.status==502)
				{				
					found=true;				
					
					ctatscrim.errorScrimUp("Error contacting the server, please refresh the page and try again (HTTP status 502: gateway response).");					
					testConnection.consumed=true; // make sure we don't call it again!
				}
				
				if (testObject.status==200) 
				{	
					found=true;
				
					pointer.debug ("Processing 200 response ...");
  					
					if (httphandler!=null)
					{
						//pointer.debug ("Received message: " + testObject.responseText);
					
						pointer.debug ("Received message");
												
						//pointer.debug ("Checking XML API: " + testObject.responseXML);
						
						stringDelivery.push (testObject.responseText);
					}
					else
						pointer.debug ("Error: httphandler is null, can't process response!");

					//Not yet implemented - if this is not a boolean, it is a function.
					/*
					if (httprequests [req][2]!=false)
					{
						httprequests [req][2]();
					}*/	   					
					
					testConnection.consumed=true; // make sure we don't call it again!
				}  
								
				if (found==false)
				{
					pointer.debug ("Error: status not handled for: " + testObject.status);
				}
			}
			else
			{
				if (testObject.readyState===0)
				{
					pointer.debug ("Ready state: request not initialized");
				}
				
				if (testObject.readyState===1)
				{
					pointer.debug ("Ready state: server connection established");
				}

				if (testObject.readyState===2)
				{
					pointer.debug ("Ready state: request received");
				}
				
				if (testObject.readyState===3)
				{
					pointer.debug ("Ready state: processing request");
				}				
			}	
									
			i++;
		} 
		
		pointer.cleanup();
		
		for (var t=0;t<stringDelivery.length;t++)
		{
			pointer.debug ("Processing incoming message: " +  t);
		
			var aMessage=stringDelivery [t];
							
			httphandler.processMessage (aMessage);
			
			/*			
			if (aMessage.indexOf ("<?xml")==-1)		
			{
				pointer.debug ("Processing incoming message (cleaned): " +  t);
			
				httphandler.processMessage ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+aMessage);
			}	
			else	
			{
				pointer.debug ("Processing incoming message: " +  t);
				
				httphandler.processMessage (aMessage);
			}*/			
		}	
	}	
	/**
	*
	*/
	this.cleanup=function cleanup () 
	{ 
		pointer.debug ("cleanup ()");
		
		var i=0;
		var count=0;
		var found=false;
		var clean=false;
		
		while (clean==false)
		{			
			//pointer.debug ("Checking ...");
		
			found=this.checkEntries ();
					
			//pointer.debug ("Found: " + found);
			
			if (found==false)
			{
				clean=true;
			}
			else
				count++;
		}	
		
		pointer.debug ("Removed " + count + " entries");
	};
	/**
	*
	*/
	this.checkEntries=function checkEntries ()
	{
		pointer.debug ("checkEntries ()");
	
		var i=0;	
		var requests=0;
	
		//for (req in httprequests)
		for (requests=0; requests<httprequests.length;requests++)
		{    
			var testConnection=httprequests [requests];
		
			if (testConnection.consumed==true)
			{
				pointer.debug ("Removing : " + testConnection.id);
			
				httprequests.splice(i, 1);
				return (true);
			}
			
			i++;
		}	
		
		return (false);
	};
}	

CTATCommLibrary.prototype = Object.create(CTATBase.prototype);
CTATCommLibrary.prototype.constructor = CTATCommLibrary;
