/**------------------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

/**
 *
 */
function CTATConnection () 
{	
	CTATBase.call(this, "CTATConnection","connection");
	
	this.id=-1;
	this.consumed=false;
	this.url="";
	this.data="";
	this.httpObject=null;
				
	var pointer = this;
	
	/**
	*
	*/	
	this.createHTTPObject=function createHTTPObject ()
	{
		pointer.debug ("createHTTPObject ()");
	
		this.httpObject=new XMLHttpRequest(); 
			
		if (window.XMLHttpRequest) 
		{		
			pointer.debug ("Creating regular XMLHttpRequest ...");
		
			this.httpObject=new XMLHttpRequest(); 
		
			if (this.httpObject.overrideMimeType) 
			{	
				this.httpObject.overrideMimeType('text/html');
			}		
		} 
		else if (window.ActiveXObject) 
		{
			pointer.debug ("Detected window.ActiveXObject ...");
		
			// IE
			try 
			{
				pointer.debug ("Creating Msxml2.XMLHTTP ...");
			
				this.httpObject=new ActiveXObject ("Msxml2.XMLHTTP");
			} 
			catch (e) 
			{					
				try 
				{
					pointer.debug ("Creating Microsoft.XMLHTTP ...");
				
					this.httpObject=new ActiveXObject("Microsoft.XMLHTTP");
				} 
				catch (e) 
				{
					alert ('Error: Unable to create HTTP Request Object: ' + e.message);
				}
			}
		}		
	};
	/**
	* Do not call this method before open is called on the http object. If you do you will
	* get a Javascript exception that says: "an attempt was made to use an object that is 
	* not or is no longer usable"
	*/
	this.init=function init ()
	{
		pointer.debug ("init ()");
							
		if (this.httpObject!=null)
		{	
			var fVars=flashVars.getRawFlashVars ();	
			var aSession=fVars ['session_id'];
	
			try
			{
				this.httpObject.setRequestHeader ("Access-Control-Allow-Origin","*");
				//this.httpObject.setRequestHeader ("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");
				this.httpObject.setRequestHeader ("ctatsession",aSession);
				//this.httpObject.setRequestHeader ("Content-type","application/x-www-form-urlencoded");
				//this.httpObject.setRequestHeader ("Content-type","application/xml");
				this.httpObject.setRequestHeader ("Content-type","*/*");
			}
			catch (err)
			{
				alert ("HTTP object creation error: " + err.message);
			}
		}
		else	
			alert ("Internal error: http object is null right after creation");	
			
		pointer.debug ("init () done");
	};	
	
	this.createHTTPObject ();
}

CTATConnection.prototype = Object.create(CTATBase.prototype);
CTATConnection.prototype.constructor = CTATConnection;
