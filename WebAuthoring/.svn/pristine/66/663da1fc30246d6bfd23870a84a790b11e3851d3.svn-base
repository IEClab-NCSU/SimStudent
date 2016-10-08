/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATScrollPaneComponent.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

The $innerListItem.position().top is actually relative to the .scrollTop() of its first
positioned ancestor. So the way to calculate the correct $parentDiv.scrollTop() value is
to begin by making sure that $parentDiv is positioned. If it doesn't already have an
explicit position, use position: relative. The elements $innerListItem and all its
ancestors up to $parentDiv need to have no explicit position. Now you can scroll to the
$innerListItem with:

// Scroll to the top
$parentDiv.scrollTop($parentDiv.scrollTop() + $innerListItem.position().top);

// Scroll to the center
$parentDiv.scrollTop($parentDiv.scrollTop() + $innerListItem.position().top
    - $parentDiv.height()/2 + $innerListItem.height()/2);

Adjusted to account for the fact that overflow div might not be at the top of the page.

parentDiv.scrollTop(parentDiv.scrollTop() + (innerListItem.position().top - parentDiv.position().top) - (parentDiv.height()/2) + (innerListItem.height()/2) )

Plugin version:

jQuery.fn.scrollTo = function(elem, speed)
{
    $(this).animate(
	{
        scrollTop:  $(this).scrollTop() - $(this).offset().top + $(elem).offset().top
    }, speed == undefined ? 1000 : speed);

    return this;
};

How to use:

$("#overflow_div").scrollTo("#innerItem");
$("#overflow_div").scrollTo("#innerItem", 2000); //custom animation speed

 */
goog.provide('CTATScrollPaneComponent');

goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATTutorableComponent');
goog.require('CTAT.ComponentRegistry');
/**
 *
 */
CTATScrollPaneComponent = function(aDescription,aX,aY,aWidth,aHeight)
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
	pointer.isTabIndexable=false;
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
	this.setAlpha=function setAlpha(aAlpha)
	{
		alpha=aAlpha;
	};
	/**
	*
	*/
	this.init=function init()
	{
	    pointer.ctatdebug("init (" + pointer.getName() + ")");

	    pointer.addCSSAttribute("z-index", CTATGlobalFunctions.gensym.z_index());
		//pointer.addCSSAttribute("overflow-y","scroll");

	    pointer.ctatdebug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

		pointer.setInitialized (true);

	    pointer.ctatdebug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    //currentZIndex++;
	    //currentIDIndex++;

		commShell.addEventListener (this);
	};

	/**
	 *
	 */
	this.processCommShellEvent=function processCommShellEvent (anEvent,aMessage)
	{
		pointer.ctatdebug ("processCommShellEvent ("+anEvent+")");

	};
	/**
	 *
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.ctatdebug ("processSerialization()");
	};

	/**
	*
	*/
	this.postProcess=function postProcess ()
	{
		pointer.ctatdebug ("postProcess ()");

		//useDebugging=true;

		// Process component specific pre-defined styles ...

		for(var j=0;j<this.parameters.length;j++)
		{
			var aParam=this.parameters [j];

			pointer.ctatdebug ("Checking style name: " + aParam.paramName);

			if (aParam.paramName=="TargetMovieClip")
			{
				pointer.ctatdebug ("Loading sub element: [" + aParam.paramValue.trim () + "] ...");

				var aClip=findMovieClip (aParam.paramValue.trim ());

				if (aClip!=null)
				{
					pointer.ctatdebug ("Found target movieclip, temporarily removing ...");

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
						pointer.getDivWrap().setAttribute("style", "overflow-y: scroll; overflow-x:hidden; border: 4px; position: absolute; visibility: block; left:"+pointer.getX ()+"px; top: "+pointer.getY ()+"px; width:"+pointer.getWidth ()+"px; height: "+pointer.getHeight ()+"px; z-index:"+pointer.getCanvasZIndex ()+";");

						pointer.ctatdebug ("Adding ...");

						pointer.getDivWrap().appendChild (aClip.getDivWrapper ());

						var index=pointer.getCanvasZIndex (); // slightly wrong because the canvas doesn't exist anymore

						index++;

						var xDiff=aClip.getDivWrapper ().style.left;
						var yDiff=aClip.getDivWrapper ().style.top;

						aClip.getDivWrapper ().style.left="0px";
						aClip.getDivWrapper ().style.top="0px";

						pointer.ctatdebug ("Moving canvas from: " + xDiff + "," + yDiff + " to: 0,0");

						var childNodes = aClip.getDivWrapper ().childNodes;

						for(var i=0; i<childNodes.length; i++)
						{
							var aNode=childNodes[i];

							index++;
						}
					}
				}
				else
					pointer.ctatdebug ("Unable to find the target movieclip to reparent the content from");
			}
		}

		//useDebugging=false;
	};

	this.configFromDescription ();
}

CTATScrollPaneComponent.prototype = Object.create(CTATTutorableComponent.prototype);
CTATScrollPaneComponent.prototype.constructor = CTATScrollPaneComponent;

CTAT.ComponentRegistry.addComponentType('CTATScrollPaneComponent',CTATScrollPaneComponent);