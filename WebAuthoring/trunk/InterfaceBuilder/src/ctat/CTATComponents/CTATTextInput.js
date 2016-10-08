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
function CTATTextInput (aDescription,
						aX,
						aY,
						aWidth,
						aHeight)
{	

	CTATTextBasedComponent.call(this, 
					  			"CTATTextInput", 
					  			"__undefined__",
					  			aDescription,
					  			aX,
					  			aY,
					  			aWidth,
					  			aHeight);

	var pointer=this;
	var textinput=null;
	var cellContainer=null;

	this.debug (this.getClassName() + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	this.configFromDescription();

	/**
	 * 
	 */
	this.init=function init() 
	{
	    this.debug("init (" + pointer.getName() + ")");

	    this.addCSSAttribute("z-index", currentZIndex);

	    textinput=document.createElement("input");	    
	    textinput.type="text";
	    textinput.name=aDescription.name;
	    textinput.setAttribute('maxlength', pointer.getMaxCharacters());
	    textinput.setAttribute('id', ('ctatdiv' + currentIDIndex));
	    textinput.setAttribute('onkeypress', 'return noenter(event)');
	    
	    pointer.setComponent(textinput);
	    
		/*
	    if (pointer.getEnabled() == true)
	        pointer.getComponent().contentEditable='true';
	    else
	        pointer.getComponent().contentEditable='false';

	    pointer.getComponent().contentEditable='true';
		*/

	    this.debug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    textinput.value=this.getText();

	    pointer.setInitialized(true);
	    
	    pointer.addComponentReference(pointer, textinput);

	    pointer.getDivWrap().appendChild(textinput);

		//this.addCSSAttribute("width", pointer.getWidth()+"px");
		//this.addCSSAttribute("height", pointer.getHeight()+"px");

	    pointer.render();

	    currentZIndex++;
	    currentIDIndex++;

	    pointer.addSafeEventListener ('keypress', pointer.processKeypress,textinput);
	    pointer.addSafeEventListener ('focus', pointer.processFocus,textinput);
	    pointer.addSafeEventListener ('click',pointer.processClick,textinput);
	};
	
	this.setCellContainer=function setCellContainer(aContainer) 
	{
		cellContainer=aContainer;
	};

	this.getCellContainer=function getCellContainer()
	{
		return (cellContainer);
	};	
	
	/**
	 * 
	 */
	this.setText = function setText(aText) 
	{
	    pointer.debug("setText (" + aText + ")");
	    
 		pointer.assignText(aText);
		if(textinput) { textinput.value=aText; }
 		
 		// this.redraw (); // Very very experimental
	};


	this.reset=function reset ()
	{
		pointer.configFromDescription();
		pointer.processSerialization();
		textinput.value="";
	}
	
	/**
	 * 
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.debug ("processSerialization()");

		// Process component specific pre-defined styles ...
		
		this.styles=pointer.getGrDescription().styles;
		
		// Process component custom styles ...		

		if (this.styles==null)
		{
			pointer.debug ("Error: styles structure is null");
			return;
		}
		
		pointer.debug ("Processing " + this.styles.length + " styles ...");
		
		for (var i=0;i<this.styles.length;i++)
		{
			var aStyle=this.styles [i]; // CTATStyle
			
			if(aStyle.styleName=="MaxCharacters")
			{
				pointer.setMaxCharacters(aStyle.styleValue);
			}
			
			if(aStyle.styleName=="showBorder")
			{
				pointer.setShowBorder(true);
			}
			
			if(aStyle.styleName=="BorderStyle")
			{
				pointer.setBorderStyle(aStyle.styleValue);
			}
			
			if(aStyle.styleName=="SolidBorderColor")
			{
				pointer.setBorderColor(aStyle.styleValue);
			}
			
			/*
			if(aStyle.styleName=="DisplayHTMLText")
			{
				
			}
			*/
		}	
	};	
	/**
	 * TPA 
	 */
	this.UpdateTextArea=function UpdateTextArea (aText)
	{
		pointer.debug ("UpdateTextArea ("+aText+")");
		this.setText(aText);
	}
}

CTATTextInput.prototype = Object.create(CTATTextBasedComponent.prototype);
CTATTextInput.prototype.constructor = CTATTextInput;
