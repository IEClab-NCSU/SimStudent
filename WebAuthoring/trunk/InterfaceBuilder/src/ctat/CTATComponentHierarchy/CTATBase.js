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
function CTATBase(aClassName, aName) 
{
	var className=aClassName;
	var name=aName;
	var pointer=this;

	this.getClassName=function getClassName ()
	{
		return (className);
	};
	
	this.setClassName=function setClassName(aClass)
	{
		className=aClass;
	};

	this.setName=function setName (aName)
	{
		name=aName;		
	};
	
	this.getName=function getName ()
	{
		return (name);
	};
	
	this.getUseDebugging=function getUseDebugging()
	{
		return (useDebugging);
	};
	
	this.setUseDebugging=function setUseDebugging(aValue)
	{
		useDebugging=aValue;
	};

	this.debug=function debug (msg, aClassName)
	{
		//var aName=aClassName;
		var aMessage=msg;
				
		if (useDebuggingBasic==true)
		{
			pointer.debugInternal (aMessage,"UnknownClass");
			return;
		}
		
		/*
		if (aName===null)
		{
			try
			{
				pointer.getClassName();
			}
			catch (err)
			{	
				pointer.debugInternal ("UndefinedClass","Internal error, trying to call pointer.getClassName () on an object that does not have that method: " + err.message);
				pointer.debugInternal (aName,msg);
				return;
			}			
		
			if (pointer.getClassName()===null)
			{
				aName="UndefinedClass";
			}
			else
			{
				aName=pointer.getClassName();
			}
		}
		*/
		
		if (msg===null)
		{
			aMessage="No message provided";
		}
	
		if(useDebugging===true)
		{
			//pointer.debugInternal (aName,aMessage);
			pointer.debugInternal (aMessage,pointer.getClassName());			
		}
	};

	this.debugObject=function debugObject (object)
	{
		var output='';
		var index=0;
		
		for (property in object) 
		{
		  //output += property + ': ' + object[property]+'; \n ';
		  this.debug ("("+index+")" + property + ': ' + object[property]);
		  
		  index++;
		}				
	};

	this.debugInternal=function debugInternal (msg,aClassName)
	{
		var aMessage=msg;
		var txt="No msg assigned yet";
		
		if (aMessage==null)
		{
			aMessage="No message!";
		}
		
		if (aMessage=="")
		{
			aMessage="Empty message!";
		}	
		
		if (useDebuggingBasic==true)
		{		
			txt=formatLogMessage ("Unknown","undefined",aMessage);
						
			if (customconsole===null)
			{
				customconsole=getSafeElementById('customconsole');
			}	

			if (customconsole!==null)
			{			
				customconsole.innerHTML+=(txt+"<br>");
			
				customconsole.scrollTop = customconsole.scrollHeight;
			}
			
			return;
		}
		
		if (aClassName===null)
		{
			aClassName="UndefinedClass";
		}
				
		if (aMessage===null)
		{
			aMessage="No message";
		}		
	
		txt=formatLogMessage (aClassName,pointer.getName (),aMessage);
	
		try
		{
			console.log (txt);
		}
		catch (err)
		{
			// can't do much with this
		}
	
		if (platform=="google")
		{
			Logger.log(txt);
			return;
		}	
			
		if (customconsole===null)
		{
			customconsole=getSafeElementById('customconsole');
		}	

		if (customconsole!==null)
		{			
			customconsole.innerHTML+=(txt+"<br>");
		
			customconsole.scrollTop = customconsole.scrollHeight;
		}	
	};

	/**
	*
	*/
	this.debugObjectShallow=function debugObjectShallow (object)
	{
		var output = '';
	
		for (property in object) 
		{
		  output += property + ', ';
		}	
	
		pointer.debugInternal ("Object: " + output,"Global");
	};
	
	this.urldecode=function urldecode(str) 
	{
	   return decodeURIComponent((str+'').replace(/\+/g, '%20'));
	};
	
	/**
	*	
	*/
	this.entitiesConvert=function entitiesConvert (str) 
 	{
		this.debug ("entitiesConvert ()");
 		
		return (this.urldecode (unescape (str)));
 	};
	
 	/**
	*	
	*/
	this.entitiesGenerate=function entitiesGenerate (str) 
 	{
		temper=str;

		return (temper);
 	};
 	
	/**
	*
	*/
 	this.htmlEncode=function htmlEncode (value)
 	{
 		//create a in-memory div, set it's inner text(which jQuery automatically encodes)
 		//then grab the encoded contents back out.  The div never exists on the page.
		
 		return $('<div/>').text(value).html();
		
		//return value;
 	};

	/**
	*
	*/
 	this.htmlDecode=function htmlDecode (value)
 	{
 		return $('<div/>').html(value).text();
 	}; 		

	/**
	*
	*/
	function formatLogMessage (aClass,anInstance,aMessage)
	{		
		var now = new Date();
		
		if (aClass===null)
		{
			aClass="unknownclass";
		}
		
		if (anInstance===null)
		{
			anInstance="nullinstance";
		}
		
		var formatted=pointer.htmlEncode (aMessage);		
				
		var txt="["+dateFormat(now,"hh:MM:ss")+"] ["+aClass+":"+anInstance+"] "+formatted;
		
		return (txt);
	}
		
	/*	
	if (useDebugging===true)
	{
		pointer.debugInternal (aClassName + " ()",aClassName);
	}
	*/	
}

/**
*
*/
function formatLogMessageGoogle (aClass,anInstance,aMessage)
{
	//var formatted=formatter.htmlEncode (aMessage);		
	var formatted=pointer.htmlEncode (aMessage);		
	var txt="["+aClass+":"+anInstance+"] "+formatted;
	
	return (txt);
}	

/**
*
*/
function debug (aMessage) 
{  
	if(useDebugging===false)
	{
		return;
	}
	
	if (aMessage===null)
	{
		aMessage="Empty message!";
	}
	
	if (aMessage=="")
	{
		aMessage="Empty message!";	
	}

	if (platform=="google")
	{
		Logger.log(formatLogMessageGoogle ("CTATTutor","tutor",aMessage));
		return;
	}	
	
	if (debugPointer===null)
	{
		debugPointer = new CTATBase("CTATTutor", "tutor");
	}

	debugPointer.debug (aMessage,"CTATTutor");
}
