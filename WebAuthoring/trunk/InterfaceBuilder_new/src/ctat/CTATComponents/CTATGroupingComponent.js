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
function CTATGroupingComponent (aDescription,aX,aY,aWidth,aHeight)
{		
	CTATTutorableComponent.call(this, 
					 			"CTATGroupingComponent", 
					  			"__undefined__",
					  			aDescription,
					  			aX,
					  			aY,
					  			aWidth,
					  			aHeight);

	var pointer=this;
	var targetClip=null;

	/**
	*
	*/
	this.init=function init() 
	{
	    pointer.debug("init (" + pointer.getName() + ")");

	    pointer.addCSSAttribute("z-index", currentZIndex);
		
		pointer.setInitialized (true);

	    currentZIndex++;
	    currentIDIndex++;
	};
		
	/**
	 * 
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.debug ("processSerialization()");
	};

	/**
	*
	*/
	this.postProcess=function postProcess ()
	{
		pointer.debug ("postProcess ()");

		//useDebugging=true;

		// Process component specific pre-defined styles ...
		
		for(var j=0;j<this.parameters.length;j++)
		{
			var aParam=this.parameters [j];
			
			pointer.debug ("Checking style name: " + aParam.paramName);
			
			if (aParam.paramName=="TargetMovieClip")
			{
				pointer.debug ("Loading sub element: " + aParam.paramValue + " ...");
				
				var aClip=findMovieClip (aParam.paramValue);
				
				if (aClip!=null)
				{
					pointer.debug ("Found target movieclip, storing for future reference ...");
					
					targetClip=aClip;
				}
			}
		}	
						
		//useDebugging=false;	
	};
	
	/**
	 * Temporarily disables the component until tempLock(false) is called.
	 * This is for temporarily locking a component with the assumption that the flash
	 * tutor will automatically unlock it. This is to be used when user input is not
	 * wanted for a temporary amount of time (i.e. component already being graded, error
	 * scrim is up, tutor is disconnected). See permlock for action-driven locking.
	 * @param bool to lock or unlock
	 * @return true if perm or temp locked.
	 */
	this.setTempLock=function setTempLock(bool)
	{
		pointer.debug ("setTemplock ("+bool+")");
			 
		if (targetClip!=null)
		{
		
		}
		else
			pointer.debug ("Error: no target MovieClip assigned to this grouping component");			 
			 
		return (true);
	};

	/**
	 * Is a component disabled or not...
	 * @return is comp disabled
	 */		
	this.isLocked=function isLocked()
	{
		return (isPermLocked || isTempLocked);
	};	
	
	/**
	 * Overwrite from CTATCompBase
	 */
	this.move=function move(w,h) 
	{
		pointer.debug ("move ("+w+","+h+")");
				
		if (targetClip!=null)
		{
		
		}
		else
			pointer.debug ("Error: no target MovieClip assigned to this grouping component");
	};	
	
	/**
	 * Overwrite from CTATCompBase
	 */
	this.size=function size(w,h) 
	{
		this.setSize (w,h);
	};	
	
	/**
	 * Overwrite from CTATCompBase
	 */
	this.setSize=function setSize(w,h) 
	{
		pointer.debug ("setSize ("+w+","+h+")");
				
		if (targetClip!=null)
		{
		
		}
		else
			pointer.debug ("Error: no target MovieClip assigned to this grouping component");
	};
	
	/**
	 * Overwrite from CTATCompBase
	 */
	this.SetVisible=function SetVisible (aValue)
	{
		pointer.debug ("SetVisible ("+aValue+")","graphics");
		
		if (targetClip!=null)
		{
		
		}
		else
			pointer.debug ("Error: no target MovieClip assigned to this grouping component");		
	};
	
	/**
	 * Overwrite from CTATCompBase
	 */
	this.setVisible=function setVisible (aValue)
	{
		pointer.debug ("setVisible ("+aValue+")","graphics");
		
		if (targetClip!=null)
		{
		
		}
		else
			pointer.debug ("Error: no target MovieClip assigned to this grouping component");		
	};	
	
	this.configFromDescription ();
}

CTATGroupingComponent.prototype = Object.create(CTATTutorableComponent.prototype);
CTATGroupingComponent.prototype.constructor = CTATGroupingComponent;
