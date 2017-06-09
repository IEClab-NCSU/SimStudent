/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2016-06-28 15:07:11 -0500 (週二, 28 六月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATComponent.js $
 $Revision: 23782 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 	Apparently unused

 */

/**
 *
 * click
 * mousemove
 * mouseover
 * mouseout
 * keyup
 * keydown
 * focus
 * blur
 * select
 * load
 *
 */
goog.provide('CTATComponent');

goog.require('CTATCompBase');
goog.require('CTATGlobals');
/**
 *
 */
CTATComponent = function(aClassName,
					    aName,
					    aDescription,
					    aX,
					    aY,
					    aWidth,
					    aHeight)
{
	CTATCompBase.call(this,
					  aClassName,
					  aName,
					  aDescription,
					  aX,
					  aY,
					  aWidth,
					  aHeight);

	this.ctatdebug ("CTATComponent" + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	var moused=false;
	var pointer=this;
	var images = {};
	var sources =
	{
		buttonDefault: '',
		buttonHover: '',
		buttonClicked: '',
		buttonDisabled: ''
	};

	/**
	 *
	 */
	this.assignImages=function assignImages (imageDefault,imageClicked,imageDisabled,imageHover)
	{
		pointer.ctatdebug ("assignImages ()");

	    for(var src in sources)
	    {
	    	pointer.ctatdebug ("Check: " + sources[src]);
	    }

		sources.buttonDefault=imageDefault;
		sources.buttonClicked=imageClicked;
		sources.buttonDisabled=imageDisabled;
		sources.buttonHover=imageHover;
	};

	/**
	 *
	 */
	this.loadImages=function ()
	{
		pointer.ctatdebug ("loadImages ()");

	    var loadedImages = 0;
	    var numImages = 0;
	    var src=null;

	    for(src in sources)
	    {
	    	numImages++;
	    }

	    pointer.ctatdebug ("Loading " + numImages + " images ...");

	    var onloadimg = function()
    	{
    		pointer.ctatdebug ("Image " + images[src].src + " loaded");

    		if(++loadedImages >= numImages)
    		{
    			pointer.ctatdebug ("Images loaded!");

    			pointer.hasImages=true;
    			pointer.getGrCanvas().drawTutor ();
    		}
    	};

	    for(src in sources)
	    {
	  		pointer.ctatdebug ("Loading: " + sources [src] + " ...");

	    	images[src] = new Image();
	  	    images[src].src = sources[src];
	    	images[src].onload = onloadimg;
	    }
	};

	/**
	 *
	 */




	/**
	 *
	 */
	function click (mouseX,mouseY)
	{
		pointer.ctatdebug ("click ("+mouseX+","+mouseY+")");

		setSelected (true);
	}

	/**
	 *
	 */
	function hover (mouseX,mouseY)
	{
		//ctatdebug ("hover ("+mouseX+","+mouseY+")");

	}

	/**
	 *
	 */
	function mouseOut (mouseX,mouseY)
	{
		pointer.ctatdebug ("mouseOut ("+mouseX+","+mouseY+")");

		setSelected (false);

		pointer.getGrCanvas().drawTutor ();
	}

	/**
	 *
	 */
	function mouseIn (mouseX,mouseY)
	{
		pointer.ctatdebug ("mouseIn ("+mouseX+","+mouseY+")");

		setSelected (true);

		pointer.getGrCanvas().drawTutor ();
	}

	/**
	 *
	 */
	function setSelected (aVal)
	{
		pointer.ctatdebug ("setSelected ()");

		var selected=aVal;

		if (selected===true)
		{
			pointer.backgroundColor="#cccccc";
		}
		else
		{
			pointer.backgroundColor="#eeeeee";
		}
	}

	/**
	 *
	 */
	this.init=function ()
	{
		pointer.ctatdebug ("init ()");

		this.loadImages ();

		canvas.addEventListener('keydown', function(e)
		{
			pointer.ctatdebug ("keydown ("+getKey (e)+")");

		    switch (getKey (e))
		    {
		    	// key code for left arrow
	        	case 37:
	        			pointer.ctatdebug('left arrow key pressed!');
	        			break;

	        			// key code for right arrow
	        	case 39:
	        			pointer.ctatdebug('right arrow key pressed!');
	        			break;
		    }
		});

		canvas.addEventListener('click', function(e)
		{
			pointer.ctatdebug ("click ()");
			var ctatcanvas = getSafeElementById("main-canvas");
			var mouseX=e.pageX-ctatcanvas.offsetLeft;
			var mouseY=e.pageY-ctatcanvas.offsetTop;

			if ((mouseX>pointer.getX()) && (mouseX<(pointer.getX()+pointer.getWidth())) && (mouseY>pointer.getY()) && (mouseY<(pointer.getY()+pointer.getHeight())))
			{
				click (mouseX,mouseY);

				pointer.getGrCanvas.drawTutor ();
			}
		});

		canvas.addEventListener('mousemove', function(e)
		{
			//ctatdebug ("mousemove");
			var ctatcanvas = getSafeElementById("main-canvas");
			var mouseX=e.pageX-ctatcanvas.offsetLeft;
			var mouseY=e.pageY-ctatcanvas.offsetTop;

			if ((mouseX>pointer.getX()) && (mouseX<(pointer.getX()+pointer.getWidth())) && (mouseY>pointer.getY()) && (mouseY<(pointer.getY()+pointer.getHeight())))
			{
				hover (mouseX,mouseY);

				if (moused===false)
				{
					mouseIn (mouseX,mouseY);

					moused=true;
				}
			}
			else
			{
				if (moused===true)
				{
					mouseOut (mouseX,mouseY);

					moused=false;
				}
			}
		});
	};

	/**
	 *
	 */
	this.CTATInvalidate=function CTATInvalidate ()
	{
		pointer.getGrCanvas.drawTutor ();
	};
};

CTATComponent.prototype = Object.create(CTATCompBase);
CTATComponent.prototype.constructor = CTATComponent;
