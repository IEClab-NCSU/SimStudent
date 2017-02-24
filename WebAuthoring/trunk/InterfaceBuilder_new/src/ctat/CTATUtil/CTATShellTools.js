/**
 * 
 */
function CTATShellTools ()
{	
	CTATBase.call(this, "CTATShellTools","shelltools");
	
	var pointer=this;
	
	/**
	 * 
	 */
	this.listComponents=function listComponents ()
	{
		var getDebugger = new CTATBase("", "");
		
		getDebugger.debug ("listComponents ()");
		
		for (var i=0;i<components.length;i++)
		{
			var ref=components [i];
			
			getDebugger.debug ("Obtaining component for " + ref.name + " with type: " + ref.type);
			
			var component=ref.getComponentPointer ();
			
			if (component!=null)
			{
				getDebugger.debug ("Component: " + component.getName () + " of instance: " + component.getClassName ());
			}
			else
				getDebugger.debug ("Error: component pointer is null");
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
	 	var pair=nameLabelPair.split(" ");
	 	return (pair[0].substring(0, pair[0].length-1));
	 };
	
	/**
	 * aCompName is only needed for component groups such as radio buttons. We only
	 * get the name of the component group, which is only half useful. The actual
	 * component name is also needed.
	 */
	this.findComponent=function findComponent (aName, aCompName)
	{
		pointer.debug("findComponent("+aName+") -> " + components.length);

		for (var i=0;i<components.length;i++)
		{
			var aDesc=components [i];

			if (aDesc==null)
			{
				pointer.debug ("Internal error parsing component at index " + i);
				return;
			}
			
			if (aDesc.name==null)
			{

				pointer.debug ("Internal error parsing component at index " + i + " (no name attribute available)");
				return;
			}
									
			if (aDesc.name==aName)
			{
				pointer.debug ("Found a component description, returning pointer ...");
				return (aDesc.getComponentPointer ());
			}
			
			//Needed for things like radio buttons, and check boxes
			//*In this case, aName is the group name, not component name
			if (aDesc.groupName==aName)
			{
				pointer.debug ("Found the component group, searching for component ...");
				
				if(aDesc.name==pointer.getNameFromGroup (aCompName))
				{
					return (aDesc.getComponentPointer ());
				}
			}
		}
		
		return (null);
	};
	/**
	 * 
	 */
	this.findComponentByClass=function findComponentByClass (aClass)
	{
		pointer.debug("findComponentByClass("+aClass+") -> " + components.length);
		
		for (var i=0;i<components.length;i++)
		{
			var aDesc=components [i];

			if (aDesc==null)
			{
				pointer.debug ("Internal error parsing component at index " + i);
				return;
			}
			
			if (aDesc.getComponentPointer ()!=null)
			{
				if (aDesc.getComponentPointer ().getClassName ()==aClass)
				{
					pointer.debug ("Found a component description, returning pointer ...");
				
					return (aDesc.getComponentPointer ());
				}
			}	
		}
		
		return (null);
	};
}

CTATShellTools.prototype = Object.create(CTATBase.prototype);
CTATShellTools.prototype.constructor = CTATShellTools;
