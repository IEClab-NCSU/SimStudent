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
function CTATScrollPaneComponent (aDescription,aX,aY,aWidth,aHeight)
{		
	CTATTutorableComponent.call(this, 
					 			"CTATScrollPaneComponent", 
					  			"__undefined__",
					  			aDescription,
					  			aX,
					  			aY,
					  			aWidth,
					  			aHeight);

	var hints=new Array ();
	var alpha=0.0;
	var pointer=this;
	var scrollpane=null;

	var skillSet=new Array ();
	
	/**
	*
	*/
	this.getAlpha=function getAlpha()
	{
		return (alpha);
	};
	/**
	*
	*/
	this.setAlpha=function setAlpha()
	{
		alpha=aAlpha;
	};
	/**
	*
	*/
	this.init=function init() 
	{
	    pointer.debug("init (" + pointer.getName() + ")");

	    pointer.addCSSAttribute("z-index", currentZIndex);
		//pointer.addCSSAttribute("overflow-y","scroll");

	    pointer.debug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());
		
		pointer.setInitialized (true);

	    pointer.debug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());		

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
					pointer.debug ("Found target movieclip, temporarily removing ...");

					if (aClip.getDivWrapper ().parentNode!=null)
					{
						try
						{
							aClip.getDivWrapper ().parentNode.removeChild (aClip.getDivWrapper ());
						}
						catch (err)
						{
							ctatscrim.errorScrimUp (err.message);
							return;
						}
					
						// First let's remove the canvas for this component, it is only in the way
						
						pointer.getSubCanvas ().parentNode.removeChild (pointer.getSubCanvas ());
					
						pointer.getDivWrap().setAttribute('id', 'scrollsubdiv');
						pointer.getDivWrap().setAttribute("style", "overflow-y: scroll; border: 4px; position: absolute; visibility: block; left:"+pointer.getX ()+"px; top: "+pointer.getY ()+"px; width:"+pointer.getWidth ()+"px; height: "+pointer.getHeight ()+"px; z-index:"+pointer.getCanvasZIndex ()+";");
					
						pointer.debug ("Adding ...");
																					
						pointer.getDivWrap().appendChild (aClip.getDivWrapper ());
						
						var index=pointer.getCanvasZIndex (); // slightly wrong because the canvas doesn't exist anymore
												
						index++;

						var xDiff=aClip.getDivWrapper ().style.left;
						var yDiff=aClip.getDivWrapper ().style.top;
						
						aClip.getDivWrapper ().style.left="0px";
						aClip.getDivWrapper ().style.top="0px";

						pointer.debug ("Moving canvas from: " + xDiff + "," + yDiff + " to: 0,0");
																																										
						var childNodes = aClip.getDivWrapper ().childNodes;

						for(var i=0; i<childNodes.length; i++) 
						{
							var aNode=childNodes[i];
							
							index++;							
						}
					}				
				}
				else
					pointer.debug ("Unable to find the target movieclip to reparent the content from");
			}
		}
		
		//useDebugging=false;	
	};
	
	this.configFromDescription ();
}

CTATScrollPaneComponent.prototype = Object.create(CTATTutorableComponent.prototype);
CTATScrollPaneComponent.prototype.constructor = CTATScrollPaneComponent;
