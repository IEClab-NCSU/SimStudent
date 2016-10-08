/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponentHierarchy/CTATBase.js $
 $Revision: 21689 $

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

	this.ctatdebug=function ctatdebug (msg, aClassName)
	{
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
		var output='';
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
	this.ctatdebugInternal=function debugInternal (msg,aClassName)
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

		if (useDebuggingBasic)
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
            else
            {
                console.log(txt);
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

		if (CTATConfig.platform=="google")
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
		var now = new Date();

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

	/*
	if (useDebugging)
	{
		pointer.ctatdebugInternal (aClassName + " ()",aClassName);
	}
	*/
}

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
	if(!useDebugging)
	{
		return;
	}

	if (aMessage===null)
	{
		aMessage="Empty message!";
		return;
	}

	if (aMessage=="")
	{
		aMessage="Empty message!";
		return;
	}

	if (CTATConfig.platform=="google")
	{
		Logger.log(formatLogMessageGoogle ("CTATTutor","tutor",aMessage));
		return;
	}

	if (debugPointer===null)
	{
		debugPointer = new CTATBase("CTATTutor", "tutor");
	}

	debugPointer.ctatdebug (aMessage,"CTATTutor");
}

if(typeof module !== 'undefined')
{
    module.exports = CTATBase;
}
