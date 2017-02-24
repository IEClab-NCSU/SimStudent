/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponentHierarchy/CTATImageButtonComponent.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

  Events: click, mousemove, mouseover, mouseout, keyup, keydown,
  		  focus, blur, select, load

  CSS: http://tutobx.com/post/24806696944/raised-and-pressed-div-using-css
       http://stackoverflow.com/questions/5662178/opacity-of-divs-background-without-affecting-contained-element-in-ie-8

  Js:  http://www.quirksmode.org/js/this.html
       http://unschooled.org/2012/03/understanding-javascript-this/

  CTAT:

 		[48] [07:14:14] [CTATTextField] Processing style labelTextValue,
		[49] [07:14:14] [CTATTextField] Processing style inspBackgroundColor,ffffff
		[50] [07:14:14] [CTATTextField] Processing style inspBorderColor,999999
		[51] [07:14:14] [CTATTextField] Processing style inspFontName,Arial
		[52] [07:14:14] [CTATTextField] Processing style inspFontSize,20
		[53] [07:14:14] [CTATTextField] Processing style inspFontColor,0
		[54] [07:14:14] [CTATTextField] Processing style inspBold,FALSE
		[55] [07:14:14] [CTATTextField] Processing style inspItalic,FALSE
		[56] [07:14:14] [CTATTextField] Processing style inspUnderline,FALSE
		[57] [07:14:14] [CTATTextField] Processing style inspAlignment,left
		[58] [07:14:14] [CTATTextField] Processing style inspShowHintHighlight,true
		[59] [07:14:14] [CTATTextField] Processing style blockOnCorrect,true
		[60] [07:14:14] [CTATTextField] Processing style _tutorComponent,Tutor
		[61] [07:14:14] [CTATTextField] Processing style disabledBackgroundColor,ffffff
		[62] [07:14:14] [CTATTextField] Processing style disabledTextColor,0
		[63] [07:14:14] [CTATTextField] Processing style tutorComponent,Tutor

	Then you can also include base64 data in img tags like this

	<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABYAAAAWCAIAAABL1vtsAAAY
 */
goog.provide('CTATImageButtonComponent');

goog.require('CTATButtonBasedComponent');

CTATImageButtonComponent = function(aClassName,
								  aName,
								  aDescription,
								  aX,
								  aY,
								  aWidth,
								  aHeight)
{
	CTATButtonBasedComponent.call(this,
					  			  aClassName,
					  			  aName,
					  			  aDescription,
					 			  aX,
					 			  aY,
					 			  aWidth,
					 			  aHeight);

	var defaultImage="";
	var clickedImage="";
	var hoverImage="";
	var disabledImage="";

	var imgButton=null;

	var mousing=false;

	/**
	 * In the case of the web, we don't need to worry about scaling from
	 * an image to a component, because the browser does this for us
	 * automatically. We do, however, have to scale from component to image.
	 */
	var scaling="Image to Component";

	var pointer=this;

	//this.setClickable(true);

	/**
	 *
	 */
	this.getDefaultImage=function getDefaultImage()
	{
		return (defaultImage);
	};

	/**
	 *
	 */
	this.setDefaultImage = function setDefaultImage(aImage)
	{
	    defaultImage=aImage;
	};

	/**
	 *
	 */
	this.getHoverImage=function getHoverImage()
	{
		return (hoverImage);
	};

	/**
	 *
	 */
	this.setHoverImage = function setHoverImage(aImage)
	{
	    hoverImage=aImage;
	};

	/**
	 *
	 */
	this.setClickedImage = function setClickedImage(aImage)
	{
	    clickedImage=aImage;
	};

	/**
	 *
	 */
	this.getClickedImage=function getClickedImage()
	{
		return (clickedImage);
	};

	/**
	 *
	 */
	this.setDisabledImage = function setDisabledImage(aImage)
	{
	    disabledImage=aImage;
	};

	/**
	 *
	 */
	this.getDisabledImage=function getDisabledImage()
	{
		return (disabledImage);
	};

	/**
	 *
	 */
	 this.setScaling=function setScaling(aScaling)
	 {
	 	scaling=aScaling;
	 };

	/**
	 *
	 */
	 this.getScaling=function getScaling()
	 {
	 	return (scaling);
	 };

	/**
	 *
	 */
	this.assignImages=function assignImages(aDefault, aClicked, aHover, aDisabled)
	{
	    pointer.ctatdebug("assignImages ()");

	    defaultImage=aDefault;
	    clickedImage=aClicked;
	    hoverImage=aHover;
	    disabledImage=aDisabled;

	    if (pointer.getComponent() != null)
	    {
	    	pointer.assignActiveImage (defaultImage);
	    }
	};
	/**
	 * Interface Action: assignImageURL
	 * @param {string} anImage	a url (or image string if only for HTML components).
	 */
	this.assignImageURL = function (anImage) {
		this.assignImages(anImage,anImage,anImage,anImage);
	};

	/**
	 *
	 */
	this.assignActiveImage=function assignActiveImage (anImage)
	{
		pointer.ctatdebug ("assignActiveImage ()")

		if (pointer.getComponent()==null)
		{
			pointer.ctatdebug ("Warning: pointer.getComponent() == null");
			return;
		}

   		pointer.getComponent().src=anImage;

   		if (scaling=="Component to Image")
   		{
   			var temp=new Image();
   			temp.src=anImage;

   			pointer.setWidth(temp.width);
   			pointer.setHeight(temp.height);
   		}

		ctatdebug ("Active image: " + pointer.getComponent().src);
	};

	/**
	 * Override the original setEnabled method
	 */
	this.setEnabled=function setEnabled (aValue)
	{
		pointer.ctatdebug("setEnabled ("+aValue+")");

		pointer.assignEnabled(aValue);

		if (pointer.getComponent()==null)
		{
			pointer.ctatdebug ("Error: pointer is null!");
			return;
		}

		/*
		if (pointer.getEnabled()==true)
		{
			pointer.getComponent().disabled=false;
		}
		else
		{
			pointer.getComponent().disabled=true;
		}
		*/

		pointer.getComponent ().disabled=!aValue;

   		if(pointer.getEnabled()==false)
   		{
   			pointer.assignActiveImage (disabledImage);
   		}
   		else
   		{
   			pointer.assignActiveImage (defaultImage);
   		}

		pointer.ctatdebug("setEnabled () -> " + pointer.getEnabled());

		//pointer.render ();
    };

    /**
     *
     */
    this.processMouseOver = function processMouseOver(e)
    {
        pointer.ctatdebug("processMouseOver (" + e.currentTarget.getAttribute("id") + " -> " + e.eventPhase + ")");

		if (mousing==true)
		{
			return;
		}

		mousing=true;

        if (pointer.getEnabled ()==true)
        {
       		pointer.assignActiveImage (hoverImage);
        }
        else
        	pointer.ctatdebug ("Error component is disabled");

		mousing=false;
    };

    /**
     *
     */
    this.processMouseOut = function processMouseOut(e)
    {
        pointer.ctatdebug("processMouseOut (" + e.currentTarget.getAttribute("id") + " -> " + e.eventPhase + ")");

		if (mousing==true)
		{
			return;
		}

		mousing=true;

        if(pointer.getEnabled ()==true)
        {
       		pointer.assignActiveImage (defaultImage);
        }

		mousing=false;
    };
}

CTATImageButtonComponent.prototype = Object.create(CTATButtonBasedComponent.prototype);
CTATImageButtonComponent.prototype.constructor = CTATImageButtonComponent;
