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

function CTATImageButtonComponent(aClassName, 
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
	    pointer.debug("assignImages ()");
	    
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
	 * 	
	 */
	this.assignActiveImage=function assignActiveImage (anImage)
	{
		//pointer.debug ("assignActiveImage ()")
		
   		pointer.getComponent().src=anImage;

   		if(scaling=="Component to Image")
   		{
   			var temp=new Image();
   			temp.src=anImage;
   			
   			pointer.setWidth(temp.width);
   			pointer.setHeight(temp.height);
   		}
	};
	
	/**
	 * Override the original setEnabled method
	 */
	this.setEnabled=function setEnabled (aValue)
	{
		pointer.debug("setEnabled ("+aValue+")");
		
		pointer.assignEnabled(aValue);
		
		if (pointer.getComponent()==null)
		{
			pointer.debug ("Error: pointer is null!");
			return;
		}
		
		if (pointer.getEnabled()==true)
			pointer.getComponent().disabled=false;
		else
			pointer.getComponent().disabled=true;
		
   		if(pointer.getEnabled()==false)
   		{   			
   			pointer.assignActiveImage (disabledImage);
   		}
   		else
   		{  			
   			pointer.assignActiveImage (defaultImage);
   		}
    };
    
    /**
     * 
     */
    this.processMouseOver = function processMouseOver(e) 
    {
        //pointer.debug("processMouseOver (" + e.currentTarget.getAttribute("id") + " -> " + e.eventPhase + ")");

        if (pointer.getEnabled ()==true)
        {
        	if (pointer != null)
        	{
        		pointer.assignActiveImage (hoverImage);
        	}
        	else
        		pointer.debug ("Error component is null");
        }
        else
        	pointer.debug ("Error component is disabled");
    };
        	
    /**
     * 
     */
    this.processMouseOut = function processMouseOut(e) 
    {
        //pointer.debug("processMouseOut (" + e.currentTarget.getAttribute("id") + " -> " + e.eventPhase + ")");

        if(pointer.getEnabled ()==true)
        {
        	if (pointer != null)
        	{
        		pointer.assignActiveImage (defaultImage);
        	}
        	else
        		pointer.debug ("Error component is null");        	
        }
    };
}

CTATImageButtonComponent.prototype = Object.create(CTATButtonBasedComponent.prototype);
CTATImageButtonComponent.prototype.constructor = CTATImageButtonComponent;
