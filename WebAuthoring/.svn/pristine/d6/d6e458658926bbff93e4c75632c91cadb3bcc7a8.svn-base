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
*/

/**
 * 
 */
function CTATButton (aDescription,
					 aX,
					 aY,
					 aWidth,
					 aHeight)
{	
	CTATButtonBasedComponent.call(this,
								  "CTATButton", 
								  "aButton",
								  aDescription,
								  aX,
								  aY,
								  aWidth,
								  aHeight);

	var pointer=this;
	var borderRoundness=5;
	var buttonText="";
					
	this.debug ("CTATButton" + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	this.configFromDescription();

	this.init=function init() 
	{
	    pointer.debug("init (" + pointer.getName() + ")");
	    
	    pointer.setSAI ("this","ButtonPressed","-1");
	    
	    pointer.addCSSAttribute("z-index", currentZIndex);
		pointer.modifyCSSAttribute("width", pointer.getWidth()+"px");
		pointer.modifyCSSAttribute("height", pointer.getHeight()+"px");
		pointer.render ();
	
		button=document.createElement('button');
		button.value=pointer.getName();
	    button.name=pointer.getName(); // might be wrong
	    button.setAttribute('id', ('ctatdiv' + currentIDIndex));
	    button.setAttribute('onkeypress', 'return noenter(event)');
	    
	    pointer.setInitialized(true);
	    
	    pointer.setComponent(button);
	    pointer.addComponentReference(pointer, button);
	    pointer.getDivWrap().appendChild(button);

	    pointer.debug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    currentZIndex++;
	    currentIDIndex++;
	    
	    pointer.addSafeEventListener ('click',pointer.processClick,button);
	    pointer.addSafeEventListener ('focus', pointer.processFocus,button);
	};

	this.reset=function reset ()
	{
		pointer.configFromDescription();
		pointer.processSerialization();
		pointer.setEnabled(true);
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

		this.styles=pointer.getGrDescription().styles;
		
		pointer.debug ("Processing " + this.styles.length + " styles ...");
		
		for (var i=0;i<this.styles.length;i++)
		{
			var aStyle=this.styles [i]; // CTATStyle
			
			if (aStyle.styleName=="labelText")
			{
				pointer.setText(aStyle.styleValue);
			}
			
			if(aStyle.styleName=="borderRoundness")
			{
				borderRoundness=aStyle.styleValue;
				pointer.addCSSAttribute("border-radius", borderRoundness+"px");
			}
		}	
	};
	
	/**
	 * TPA 
	 */
	 this.ButtonPressed=function ButtonPressed ()
	 {
	 	pointer.debug ("ButtonPressed ()");
	 };
}

CTATButton.prototype = Object.create(CTATButtonBasedComponent.prototype);
CTATButton.prototype.constructor = CTATButton;
