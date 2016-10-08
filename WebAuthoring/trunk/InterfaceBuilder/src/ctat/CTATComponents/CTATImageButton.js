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

/**
 * 
 */
function CTATImageButton (aDescription,aX,aY,aWidth,aHeight)
{			
	CTATImageButtonComponent.call(this, 
					  			  "CTATImageButton",
					  			  "__undefined__",
					  			  aDescription,
					  			  aX,
					  			  aY,
					  			  aWidth,
					  			  aHeight);
		
	var pointer=this;
	var imgButton=null;
					
	this.debug (this.getClassName() + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");
	
	this.configFromDescription ();

	this.init=function init() 
	{
	    pointer.debug("init (" + pointer.getName() + ")");
	    
	    imgButton=new Image();
	    imgButton.name=pointer.getName(); // might be wrong
	    imgButton.setAttribute('id', ('ctatdiv' + currentIDIndex));
	    imgButton.setAttribute('onkeypress', 'return noenter(event)');
	    
	    pointer.setComponent(imgButton);

	    pointer.addComponentReference(pointer, imgButton);
	    
	    pointer.debug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    pointer.setInitialized(true);
	    
	    pointer.getDivWrap().appendChild(imgButton);

		pointer.assignActiveImage(pointer.getDefaultImage());
		
	    pointer.addCSSAttribute("z-index", currentZIndex);
		pointer.addCSSAttribute("width", pointer.getWidth());
		pointer.addCSSAttribute("height", pointer.getHeight());
		pointer.render();

		/*
		pointer.addSafeEventListener ('mouseover', pointer.processMouseOver,imgButton);
		pointer.addSafeEventListener ('mouseout', pointer.processMouseOut,imgButton);
		pointer.addSafeEventListener ('focus', pointer.processFocus,imgButton);
		*/
		
	    currentZIndex++;
	    currentIDIndex++;
	};
	
	/**
	 * 
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.debug ("processSerialization()");

		// Process component specific pre-defined styles ...
		
		pointer.setText (this.label);
		
		// Process component custom styles ...
		this.styles=aDescription.styles;

		this.styles=pointer.getGrDescription().styles;
		
		if (this.styles!=null)
		{		
			pointer.debug ("Processing " + this.styles.length + " styles ...");
			
			for (var i=0;i<this.styles.length;i++)
			{
				var aStyle=this.styles [i]; // CTATStyle
				
				if(aStyle.styleName=="labelText")
				{
					pointer.setText(aStyle.styleValue);
				}
				
				if(aStyle.styleName=="normalName")
				{
					if(aStyle.styleValue.length > 0)
					{
						pointer.setDefaultImage(aStyle.styleValue);
					}
				}
				
				if(aStyle.styleName=="hoverName")
				{
					if(aStyle.styleValue.length > 0)
					{
						pointer.setHoverImage(aStyle.styleValue);
					}
				}
				
				if(aStyle.styleName=="clickName")
				{
					if(aStyle.styleValue.length > 0)
					{
						pointer.setClickedImage(aStyle.styleValue);
					}
				}
				
				if(aStyle.styleName=="disabledName")
				{
					if(aStyle.styleValue.length > 0)
					{
						pointer.setDisabledImage(aStyle.styleValue);
					}
				}
				
				if(aStyle.styleName=="Scaling")
				{
					pointer.setScaling(aStyle.styleValue);
				}
			}	
		}	
	};	
}

CTATImageButton.prototype = Object.create(CTATImageButtonComponent.prototype);
CTATImageButton.prototype.constructor = CTATImageButton;
