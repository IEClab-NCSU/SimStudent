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
		this.ctatdebug ("encodeVariables ()");
		
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
		var vars=flashVars.getRawFlashVars ();
		
		var prefix="http://";
		
		if (vars ['tutoring_service_communication']=='https')
		{
			prefix="https://";
		}		
		
		var url=prefix + vars ["remoteSocketURL"] + ":" + vars ["remoteSocketPort"];
		
		this.ctatdebug ('sendXML ('+url+')');
		
		if (aMessage.indexOf ("<?xml")==-1)		
			this.send_post (url,"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+aMessage)
		else	
			this.send_post (url,aMessage)
	};
	/**
	 * 
	 */
	this.sendXMLURL=function sendXMLURL (aMessage,aURL)
	{
		this.ctatdebug ('sendXMLURL ('+aURL+')');
		
		var vars=flashVars.getRawFlashVars ();
				
		if (aMessage.indexOf ("<?xml")==-1)		
			this.send_post (aURL,"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+aMessage);
		else
			this.send_post (aURL,aMessage);
	};
	/**
	 * 
	 */
	this.sendURLVariables=function sendURLVariables (aURL,variables)
	{
		this.ctatdebug ('sendURLVariables ('+aURL+')');
		
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
		this.ctatdebug ('send ('+url+')');
	
		if (CTATGlobals.CommDisabled===true)
		{
			this.ctatdebug ("Communications globally disabled, please check your settings");
			return;
		}	
	
		var newConnection=new CTATConnection ();
		newConnection.id=httpreqindex;
		httpreqindex++;
		 		
		httprequests.push(newConnection);
			 	
		/*		
		try
		{
			newConnection.url=url;
			newConnection.httpObject.onreadystatechange=this.processReply;	 
			newConnection.httpObject.open ('GET', url, true);
			newConnection.init ();
			newConnection.httpObject.send (null);
		}
		catch(err)
		{
			this.ctatdebug ("Error description: " + err.message);		
		}
		*/
		
		var result = UrlFetchApp.fetch(url);
		
		newConnection.data=result;
		
		this.processReply ();		
	};	
	/**
	* https://developers.google.com/apps-script/reference/url-fetch/http-response
	*/
	this.send_post=function send_post (url,data) 
	{
		this.ctatdebug ('send_post ('+url+')');
		
		if (CTATGlobals.CommDisabled===true)
		{
			this.ctatdebug ("Communications globally disabled, please check your settings");
			return;
		}		
		
		var newConnection=new CTATConnection ();
		newConnection.url=url;
		newConnection.data=data;
		newConnection.id=httpreqindex;
		httpreqindex++;		
		 		
		httprequests.push(newConnection);

		/*
		try
		{
			newConnection.url=url;
			newConnection.data=data;
			newConnection.httpObject.onreadystatechange=this.processReply;	 
			newConnection.httpObject.open ('POST', url, true);				
			newConnection.init ();
			newConnection.httpObject.send (data);			
		}
		catch(err)
		{
			this.ctatdebug ("Error description: " + err.message);		
		}
		*/		
		
		var payload =
		{
			"data" : data
		};

		// Because payload is a JavaScript object, it will be interpreted as
		// an HTML form. (We do not need to specify contentType; it will
		// automatically default to either 'application/x-www-form-urlencoded'
		// or 'multipart/form-data')

		var options =
		{
			"method" : "post",
			//"payload" : payload
			"payload" : data
		};
		
		/*
		oauthConfig.setConsumerKey("anonymous");
		oauthConfig.setConsumerSecret("anonymous");
		oauthConfig.setRequestTokenUrl("https://www.google.com/accounts/OAuthGetRequestToken?scope="+scope);
		oauthConfig.setAuthorizationUrl("https://accounts.google.com/OAuthAuthorizeToken");    
		oauthConfig.setAccessTokenUrl("https://www.google.com/accounts/OAuthGetAccessToken");  
 
		var requestData = 
		{
			"oAuthServiceName": "spreadsheets",
			"oAuthUseToken": "always",
		};
		*/

		var result = UrlFetchApp.fetch(url, options);
		
		newConnection.data=result;
		
		this.processReply ();
	};
	/**
	* 		var data=this.encodeVariables(variables);
	*/
	this.send_post_variables=function send_post_variables (url,variables) 
	{
		this.ctatdebug ('send_post_variables ('+url+')');

		var data=this.encodeVariables(variables);
		
		this.ctatdebug ("Sending: " + data);
		
		this.send_post (url,data);
	};	
	/**
	*
	*/
	this.processReply=function processReply () 
	{ 
		pointer.ctatdebug ('processReply ('+httprequests.length+')');
				
		var i=0;
		var found=false;
		var stringDelivery=new Array ();
		
		for (req in httprequests)
		{    
			var testConnection=httprequests [req];
			var testObject=testConnection.data;
		
			if (testConnection.consumed==false) 
			{
				pointer.ctatdebug ("Investigating request response: " + i + " -> " + testConnection.url);
				
				found=false;

				if (testObject.getResponseCode()==0)
				{				
					found=true;				
					
					pointer.ctatdebug (testObject.responseText);
					
					//ctatscrim.errorScrimUp("Error: Connection closed by foreign host.");
					alert("Error: Connection closed by foreign host.");
					testConnection.consumed=true; // make sure we don't call it again!
				}
			
				// 408 timeout response
				if(testObject.getResponseCode()==408)
				{
					found=true;
				
					//ctatscrim.scrimDown();

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
				
				if (testObject.getResponseCode()==502)
				{				
					found=true;				
					
					//ctatscrim.errorScrimUp("Error contacting the server, please refresh the page and try again (HTTP status 502: gateway response).");					
					alert("Error contacting the server, please refresh the page and try again (HTTP status 502: gateway response).");					
					testConnection.consumed=true; // make sure we don't call it again!
				}
				
				if (testObject.getResponseCode()==200) 
				{	
					found=true;
				
					pointer.ctatdebug ("Processing 200 response ...");
  					
					if (httphandler!=null)
					{
						pointer.ctatdebug ("Received message: " + testObject.getContentText());
												
						stringDelivery.push (testObject.getContentText());
					}
					else
						pointer.ctatdebug ("Error: httphandler is null, can't process response!");

					testConnection.consumed=true; // make sure we don't call it again!
				}  
								
				if (found==false)
				{
					pointer.ctatdebug ("Error: status not handled for: " + testObject.status);
				}
			}
			else
				pointer.ctatdebug ("Ready state: " + testObject.readyState + ", already processed: " + testConnection.consumed);
									
			i++;
		} 
		
		pointer.cleanup();
		
		for (var t=0;t<stringDelivery.length;t++)
		{
			pointer.ctatdebug ("Processing incoming message: " +  t);
		
			var aMessage=stringDelivery [t];
							
			httphandler.processMessage (aMessage);
			
			/*			
			if (aMessage.indexOf ("<?xml")==-1)		
			{
				pointer.ctatdebug ("Processing incoming message (cleaned): " +  t);
			
				httphandler.processMessage ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+aMessage);
			}	
			else	
			{
				pointer.ctatdebug ("Processing incoming message: " +  t);
				
				httphandler.processMessage (aMessage);
			}*/			
		}	
	}	
	/**
	*
	*/
	this.cleanup=function cleanup () 
	{ 
		pointer.ctatdebug ("cleanup ()");
		
		var i=0;
		var count=0;
		var found=false;
		var clean=false;
		
		while (clean==false)
		{			
			//pointer.ctatdebug ("Checking ...");
		
			found=this.checkEntries ();
					
			//pointer.ctatdebug ("Found: " + found);
			
			if (found==false)
			{
				clean=true;
			}
			else
				count++;
		}	
		
		pointer.ctatdebug ("Removed " + count + " entries");
	};
	/**
	*
	*/
	this.checkEntries=function checkEntries ()
	{
		pointer.ctatdebug ("checkEntries ()");
	
		var i=0;
	
		for (req in httprequests)
		{    
			var testConnection=httprequests [req];
		
			if (testConnection.consumed==true)
			{
				pointer.ctatdebug ("Removing : " + testConnection.id);
			
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
