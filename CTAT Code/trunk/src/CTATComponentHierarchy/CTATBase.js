/**-----------------------------------------------------------------------------
 $Author: sewall $
 $Date: 2017-01-04 15:39:26 -0600 (週三, 04 一月 2017) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponentHierarchy/CTATBase.js $
 $Revision: 24468 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATBase');

goog.require('CTATConfig');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATSandboxDriver');


/**
 *
 */
CTATBase = function(aClassName, aName)
{
	var className=aClassName;
	var name=aName;
	var pointer=this;
	var myDebugging=true;

	if(CTATBase['DebuggingFilter'][className] != "undefined" && CTATBase['DebuggingFilter'][className])
	{
		myDebugging = false;
	}

	this.getClassName=function getClassName ()
	{
		return (className);
	};

	// A lot of people accidentally use the function above with a lowercase N. Hence
	// this backup
	this.getClassname=function getClassname ()
	{
		return (className);
	};

	this.setClassName=function setClassName(aClass)
	{
		className=aClass;
	};

	this.setName=function setName (sName)
	{
		name=sName;
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
	
	

	/**
	 *
	 */
	this.toHHMMSS = function (aValue)
	{
		var sec_num = parseInt(aValue, 10); // don't forget the second param
		var hours   = Math.floor(sec_num / 3600);
		var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
		var seconds = sec_num - (hours * 3600) - (minutes * 60);

		if (hours   < 10) {hours   = "0"+hours;}
		if (minutes < 10) {minutes = "0"+minutes;}
		if (seconds < 10) {seconds = "0"+seconds;}
		return hours+':'+minutes+':'+seconds;
	};
	
	

	/**
	*
	*/
	this.ctatdebug=function ctatdebug (msg)
	{
		if(!myDebugging)
		{
			return;
		}

		//var aName=aClassName;
		var aMessage=msg;

		if (useDebuggingBasic)
		{
			pointer.ctatdebugInternal (aMessage,"UnknownClass");
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
				pointer.ctatdebugInternal ("UndefinedClass","Internal error, trying to call pointer.getClassName () on an object that does not have that method: " + err.message);
				pointer.ctatdebugInternal (aName,msg);
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

		if(useDebugging)
		{
			pointer.ctatdebugInternal (aMessage,pointer.getClassName());
		}
	};

	/**
	*
	*/
	this.ctatdebugObject=function debugObject (object)
	{
		//var output='';
		var index=0;

		for (var property in object)
		{
		  //output += property + ': ' + object[property]+'; \n ';
		  this.ctatdebug ("("+index+")" + property + ': ' + object[property]);

		  index++;
		}
	};

	/**
	*
	*/
	this.ctatdebugInternal=function debugInternal (msg,sClassName)
	{
		var aMessage=msg;
		var txt="No msg assigned yet";

		if (aMessage===null || aMessage===undefined)
		{
			aMessage="No message!";
		}

		if (aMessage==="")
		{
			aMessage="Empty message!";
		}

		if (useDebuggingBasic)
		{
			txt=formatLogMessage ("Unknown","undefined",aMessage);

			if (!CTATBase.customconsole)
			{
				CTATBase.customconsole=getSafeElementById('customconsole');
			}

			if (CTATBase.customconsole)
			{
				CTATBase.customconsole.innerHTML+=(txt+"<br>");

				CTATBase.customconsole.scrollTop = CTATBase.customconsole.scrollHeight;
			}
			else
			{
			    console.log(txt);
			}

			return;
		}

		if (sClassName===null)
		{
			sClassName="UndefinedClass";
		}

		if (aMessage===null)
		{
			aMessage="No message";
		}

		txt=formatLogMessage (sClassName,pointer.getName (),aMessage);

		try
		{
			console.trace (txt);
		}
		catch (err)
		{
			// can't do much with this
		}

		if (CTATConfig.platform=="google")
		{
			Logger.log(txt);
			return;
		}

		if (!CTATBase.customconsole)
		{
			//console.log ("Getting console pointer ...");

			CTATBase.customconsole=getSafeElementById('customconsole');
		}

		if (CTATBase.customconsole!==null)
		{
			//console.log ("We have a console ...");

			CTATBase.customconsole.innerHTML+=(txt+"<br>");

			CTATBase.customconsole.scrollTop = CTATBase.customconsole.scrollHeight;
		}
	};

	/**
	*
	*/
	this.ctatdebugObjectShallow=function debugObjectShallow (object)
	{
		var output = '';

		for (var property in object)
		{
		  output += property + ', ';
		}

		pointer.ctatdebugInternal ("Object: " + output,"Global");
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
		this.ctatdebug ("entitiesConvert ()");

		return (this.urldecode (unescape (str)));
 	};

 	/**
	*
	*/
	this.entitiesGenerate=function entitiesGenerate (str)
 	{
		var temper=str;

		return (temper);
 	};

	/**
	*
	*/
	function formatLogMessage (aClass,anInstance,aMessage)
	{
		//var now = new Date();

		if (aClass===null)
		{
			aClass="unknownclass";
		}

		if (anInstance===null)
		{
			anInstance="nullinstance";
		}

		//var formatted=pointer.htmlEncode (aMessage);
		var formatted=aMessage;

		//var txt="["+now.format("hh:MM:ss")+"] ["+aClass+":"+anInstance+"] "+formatted;
		var txt="["+aClass+":"+anInstance+"] "+formatted;

		return (txt);
	}

};

/**
 *
 */
function formatLogMessageGoogle (aClass,anInstance,aMessage)
{
	//var formatted=formatter.htmlEncode (aMessage);
	var base = new CTATBase(aClass,anInstance);
	var formatted=base.htmlEncode (aMessage);
	var txt="["+aClass+":"+anInstance+"] "+formatted;

	return (txt);
}

/**
 *
 */
function ctatdebug (aMessage)
{
	if(!useDebugging && !useDebuggingBasic)
	{
		return;
	}

	if (aMessage===null)
	{
		aMessage="Empty message!";
		return;
	}

	if (CTATConfig.platform=="google")
	{
		Logger.log(formatLogMessageGoogle ("CTATTutor","tutor",aMessage));
		return;
	}

	if (!ctatdebug.debugPointer)
	{
		ctatdebug.debugPointer = new CTATBase("CTATTutor", "tutor");
	}

	ctatdebug.debugPointer.ctatdebug (aMessage,"CTATTutor");
}

/** List of booleans for filtering ctatdebug() traces. */
Object.defineProperty(CTATBase, "DebuggingFilter", {enumerable: false, configurable: false, writable: true, value: []});

if(typeof module !== 'undefined')
{
    module.exports = CTATBase;
}
