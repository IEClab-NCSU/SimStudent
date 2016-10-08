/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATUtil/CTATShellTools.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATShellTools');

goog.require('CTATBase');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
/**
 *
 */
CTATShellTools = function()
{
	CTATBase.call(this, "CTATShellTools","shelltools");

	var pointer=this;
	var groupArray=null;

	/**
	 *
	 */
	this.listComponents=function listComponents ()
	{
		var getDebugger = new CTATBase("", "");

		getDebugger.ctatdebug ("listComponents ()");

		for (var i=0;i<components.length;i++)
		{
			var ref=components [i];

			getDebugger.ctatdebug ("Obtaining component for " + ref.name + " with type: " + ref.type);

			var component=ref.getComponentPointer ();

			if (component!=null)
			{
				getDebugger.ctatdebug ("Component: " + component.getName () + " of instance: " + component.getClassName ());
			}
			else
				getDebugger.ctatdebug ("Error: component pointer is null");
		}
	};
	/**
	 * Helper function to get the current date in milliseconds.
	 * @return number of milliseconds in current time.
	 */
	this.getCurrentMs=function getCurrentMs()
	{
		var now = new Date();

		return (now.valueOf());
	};

	/**
	 * Used to grab the component name from <value> tag in the SAI for
	 * grouped components such as radio buttons. This allows us to include
	 * colons in a group name (provided it is allowed in AS3).
	 */
	 this.getNameFromGroup=function getNameFromGroup (nameLabelPair)
	 {
		pointer.ctatdebug ("getNameFromGroup ("+nameLabelPair+")");

	 	var pair=nameLabelPair.split(" ");

		if (pair.length==1)
		{
			return (nameLabelPair);
		}

	 	return (pair[0].substring(0, pair[0].length-1));
	 };

	/**
	 * aCompName is only needed for component groups such as radio buttons. We only
	 * get the name of the component group, which is only half useful. The actual
	 * component name is also needed.
	 */
	this.findComponent=function findComponent (aName, aCompName)
	{
		//pointer.ctatdebug("findComponent("+aName+") -> " + components.length);
		pointer.ctatdebug("findComponent("+aName+")");

		groupArray=new Array ();

		for (var i=0;i<components.length;i++)
		{
			var aDesc=components [i];

			if (aDesc==null)
			{
				pointer.ctatdebug ("Internal error parsing component at index " + i);
				return (null);
			}

			if (aDesc.name==null)
			{
				pointer.ctatdebug ("Internal error parsing component at index " + i + " (no name attribute available)");
				return (null);
			}

			if (aDesc.name==aName)
			{
				pointer.ctatdebug ("Found a component description for ["+aDesc.name+"], returning pointer ("+aDesc.getComponentPointer ()+") ...");

				groupArray=new Array ();
				groupArray.push (aDesc.getComponentPointer ());

				return (groupArray);
			}

			// Needed for things like radio buttons, and check boxes
			// In this case, aName is the group name, not component name
			if (aDesc.groupName==aName)
			{
				pointer.ctatdebug ("Found the component group ("+aDesc.groupName+"), adding component instance for " + aDesc.name+ " ...");

				if (groupArray==null)
				{
					groupArray=new Array ();
				}

				groupArray.push (aDesc.getComponentPointer ());
			}
		}

		if (groupArray==null)
		{
			pointer.ctatdebug ("Info (groupArray==null), no appropriate component found, perhaps this is a group component");

			if (aName.indexOf (".")!=-1)
			{
				var splitter=aName.split (".");

				return (this.findComponent (splitter [0]));
			}

			return (groupArray);
		}

		if (groupArray.length==0)
		{
			pointer.ctatdebug ("Info (groupArray.length==0), no appropriate component found, perhaps this is a group component");

			if (aName.indexOf (".")!=-1)
			{
				var splitter=aName.split (".");

				return (this.findComponent (splitter [0]));
			}

			return (groupArray);
		}

		return (groupArray);
	};
	/**
	 *
	 */
	this.findComponentByClass=function findComponentByClass (aClass)
	{
		pointer.ctatdebug("findComponentByClass("+aClass+") -> " + components.length);

		for (var i=0;i<components.length;i++)
		{
			var aDesc=components [i];

			if (aDesc==null)
			{
				pointer.ctatdebug ("Internal error parsing component at index " + i);
				return;
			}

			if (aDesc.getComponentPointer ()!=null)
			{
				if (aDesc.getComponentPointer ().getClassName ()==aClass)
				{
					pointer.ctatdebug ("Found a component description, returning pointer ...");

					return (aDesc.getComponentPointer ());
				}
			}
		}

		return (null);
	};
}

CTATShellTools.prototype = Object.create(CTATBase.prototype);
CTATShellTools.prototype.constructor = CTATShellTools;
