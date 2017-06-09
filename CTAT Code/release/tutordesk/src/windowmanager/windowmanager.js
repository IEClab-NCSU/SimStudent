/**-----------------------------------------------------------------------------
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
var CTATWindowManager = function(anID,aClass,anInstance,aMode,aTitle) 
{
	CTATBase.call (this, "CTATWindowManager", "wmanager");

	var activeWindow="";
	var windowStack=[];
	var pointer=this;
	
	/**
	*
	*/
	this.updateBlocker = function updateBlocker (aMessage)
	{
		//pointer.ctatdebug ("updateBlocker ()");
		
		document.getElementById ("infocontent").innerHTML+=aMessage;
	};

	/**
	*
	*/
	this.centerWindow = function centerWindow(anID) 
	{
		//pointer.ctatdebug ("centerWindow ("+anID+")");
		
		$(anID).css('left', jQuery(window).width()/2 - jQuery(anID).width()/2);
		$(anID).css('top', jQuery(window).height()/2 - jQuery(anID).height()/2);
	};

	/**
	*
	*/
	this.findWindow = function findWindow (anID)
	{
		//pointer.ctatdebug ("findWindow ("+anID+")");

		for (var i=0;i<windowStack.length;i++)
		{
			var windowObject=windowStack [i];
			
			if (windowObject.getWindowID ()==anID)
			{
				//pointer.ctatdebug ("Found existing window object, returning ...");
				return (windowObject);
			}
		}
		
		return (null);
	};

	/**
	*
	*/
	this.removeWindow = function removeWindow (anID)
	{
		//pointer.ctatdebug ("removeWindow ("+anID+")");
		
		for (var i=0;i<windowStack.length;i++)
		{
			var windowObject=windowStack [i];
			//pointer.ctatdebug('checking window '+windowObject.getWindowID());
			
			if (windowObject.getWindowID ()==anID)
			{
				//pointer.ctatdebug ("Found existing window object, removing from stack ...");

				windowStack.splice (i,1);
				
				return;
			}
		}	
	};

	/**
	*
	*/
	this.addWindow = function addWindow (anID,aCenter,mode)
	{
		//pointer.ctatdebug ("addWindow ("+anID+","+aCenter+")");

		var windowObject=pointer.findWindow (anID);

		if (windowObject!=null)
		{
			//pointer.ctatdebug ("we're all set, we need to push the window on the top though");
		}
		else
		{	
			//pointer.ctatdebug ("No such window known, registering ...");
		
			windowObject=new CTATWindow ();
			windowObject.setWindowID (anID);
			windowStack.push (windowObject);
					
			//if (!jQuery(anID).draggable('instance')) // Temporarily removed these lines since they clash with certain versions of jQuery we're using in student desk
			//{
				jQuery(anID).draggable(
				{	
					handle: 'h4',
					cancel: '.ctatcontent',
					containment: 'window',
					scroll: false		
				});
			//}

			//if (!jQuery(anID).resizable('instance')) // Temporarily removed these lines since they clash with certain versions of jQuery we're using in student desk
			//{
				jQuery(anID).resizable(
				{
					handles: 'n, e, s, w, ne, se, sw, nw',
					containment: 'body',
					minHeight:150,
					minWidth: 120,
					maxHeight: jQuery(window).height(),
					maxWidth: jQuery(window).width()
				});		
			//}
			
			//>-----------------------------------------------------------------
		
			var titleDiv=jQuery(anID).find('.ctattitle');
			
			titleDiv.click (function processTitleClick ()
			{
				pointer.selectWindow ("#"+jQuery(this).parent().attr ("id"));
			});	
			
			//>-----------------------------------------------------------------	
		
			var closeButton=jQuery(anID).find('.ctatwindowclose');
		
			if (closeButton)
			{
				console.log ("We have a close button, attaching click event ...");
			
				closeButton.click (function processCloseClick ()
				{
					var targetWindowID=('#'+jQuery(this).parent().attr ("id"));
				
					console.log ("processCloseClick ("+targetWindowID+")");

					var targetWindow=pointer.findWindow (targetWindowID);
					
					if (targetWindow!=null)
					{
						pointer.closeWindow (targetWindowID);
					}
					else
					{
						console.log ("Internal error: target window now found in window stack!");
					}
				});
			}	
			
			//>-----------------------------------------------------------------
		
			var minmaxButton=jQuery(anID).find('.ctatwindowmaximize');
			
			if (minmaxButton)
			{
				console.log ("We have a minmaxButton button, attaching click event ...");
			
				minmaxButton.click (function processMaximizeClick ()
				{
					var targetWindowID=('#'+jQuery(this).parent().attr ("id"));
				
					console.log ("processMaximizeClick ("+targetWindowID+")");

					var targetWindow=pointer.findWindow (targetWindowID);
					
					if (targetWindow!=null)
					{
						if (targetWindow.getWindowState ()=="DEFAULT")
						{
							targetWindow.setWindowState ("MAXIMIZED");
							targetWindow.storeDimensions (jQuery(anID).css('left'), jQuery(anID).css('top'), jQuery(anID).css('width'), jQuery(anID).css('height'));
							pointer.maximizeWindow (anID);
							jQuery(anID).draggable('disable');
						}
						else
						{
							if (targetWindow.getWindowState ()=="MAXIMIZED")
							{
								pointer.restoreWindow (anID,
													   targetWindow.getStoredX (),
													   targetWindow.getStoredY (),
												  	   targetWindow.getStoredWidth (),
													   targetWindow.getStoredHeight ());
								targetWindow.setWindowState ("DEFAULT");
								jQuery(anID).draggable('enable');
							}
						}	
					}
					else
					{
						console.log ("Internal error: target window now found in window stack!");
					}
				});
			}			

			//>-----------------------------------------------------------------
		}
		
		jQuery(anID).visible();
		
		if (aCenter === true)
		{
			//pointer.ctatdebug('centering window...');
			pointer.centerWindow (anID);
		}
		
		pointer.selectWindow (anID, mode);
		
		activeWindow=anID;
		
		return (windowObject);
	};

	/**
	*
	*/
	this.maximizeWindow = function maximizeWindow (anID)
	{
		//pointer.ctatdebug ("maximizeWindow ()");

		jQuery(anID).css('left', "5px");
		jQuery(anID).css('top', "5px");	
		jQuery(anID).css('width', (jQuery("#sizeReference").width()-10)+"px");
		jQuery(anID).css('height', (jQuery("#sizeReference").height()-60)+"px");
	};

	/**
	*
	*/
	this.restoreWindow = function restoreWindow (anID,anX,anY,aWidth,aHeight)
	{
		//pointer.ctatdebug ("restoreWindow ("+anID + "," + anX + "," + anY + "," + aWidth + "," + aHeight+")");

		jQuery(anID).css('left', anX);
		jQuery(anID).css('top', anY);
		jQuery(anID).css('width', aWidth);
		jQuery(anID).css('height', aHeight);	
	};

	/**
	*
	*/
	this.closeWindow = function closeWindow (anID)
	{
		//pointer.ctatdebug ("closeWindow ("+anID+")");
		
		var windowObject = null;
		var windex;
		var nextWindow = null;
		
		//get ref to window object and index in stack
		for (windex=0; windex < windowStack.length; windex++)
		{
			windowObject=windowStack [windex];
			if (windowObject.getWindowID () == anID)
			{
				break;
			}
			windowObject = null;
		}

		if (windowObject!=null)
		{
			//turn off blocker
			if (windowObject.getWindowMode ()=="MODAL")
			{
				toggleBlocker (false);
			}
			
			//get ref to next window in stack 
			if (windex == 0)
			{
				if (windowStack[1])
				{
					nextWindow = windowStack[1];
				}
			}
			else if (windowStack[windex-1])
			{
				nextWindow = windowStack[windex-1];
			}
			
			//remove window from stack
			windowStack.splice (windex,1);
		}
		
		//hide window
		jQuery(anID).invisible();
		
		//hide blocker
		toggleBlocker(false);
		
		//select next in stack, if there
		if (nextWindow)
		{
			//pointer.ctatdebug('selecting next window in stack: '+nextWindow.getWindowID());
			pointer.selectWindow(nextWindow.getWindowID(), nextWindow.getWindowMode());
		}
	};

	/**
	*
	*/
	this.deselectAll = function deselectAll ()
	{
		//pointer.ctatdebug ("deselectAll ("+windowStack.length+")");
		
		for (var i=0;i<windowStack.length;i++)
		{
			var windowObject=windowStack [i];
			
			//pointer.ctatdebug ("Deselecting: " + windowObject.getWindowID () + " ...");
			
			/*
			$(windowObject.getWindowID ()).find("h4").toggleClass ("ctattitle",true);
			$(windowObject.getWindowID ()).find("h4").toggleClass ("ctattitleselected",false);
			*/
			
			jQuery(windowObject.getWindowID ()).css('zIndex', ((i+1)*100));
			
			jQuery(windowObject.getWindowID ()).css('background-color',"rgba(160,160,160,0.7)"); 
		}
	};

	/**
	*
	*/
	this.selectWindow = function selectWindow (anID, mode)
	{
		//pointer.ctatdebug ("selectWindow ("+anID+")");
		
		pointer.deselectAll ();
		
		jQuery(anID).css('background-color',"rgba(200,216,224,0.7)"); 
		jQuery(anID).css('zIndex', ((windowStack.length+1)*100)+1001);
		
		if (mode === "MODAL")
		{
			toggleBlocker(true);
		}
	};
};

CTATWindowManager.prototype = Object.create(CTATBase.prototype);
CTATWindowManager.prototype.constructor = CTATWindowManager;
