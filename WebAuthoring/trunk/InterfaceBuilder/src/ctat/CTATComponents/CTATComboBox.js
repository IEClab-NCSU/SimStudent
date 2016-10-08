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
function CTATComboBox (aDescription,aX,aY,aWidth,aHeight)
{
	CTATTutorableComponent.call(this, 
					  			"CTATComboBox", 
					  			"__undefined__",
					  			aDescription,
					  			aX,
					  			aY,
					  			aWidth,
					  			aHeight);

	var pointer=this;
	var combobox=null;

	this.debug (pointer.getClassName() + " ("+pointer.getX()+","+pointer.getY()+","+pointer.getWidth()+","+pointer.getHeight()+")");

	this.configFromDescription();

	/**
	 * 
	 */
	this.init=function init() 
	{
	    pointer.debug("init (" + pointer.getName() + ")");

	    pointer.addCSSAttribute("z-index", currentZIndex);

	    combobox=document.createElement("select");
	    combobox.name=pointer.getName(); // might be wrong
	    combobox.setAttribute('id', ('ctatdiv' + currentIDIndex));
	    combobox.setAttribute('onkeypress', 'return noenter(event)');
	    //combobox.setAttribute('onchange','processComboSelection();');
	    combobox.onchange=this.processComboSelection;

		pointer.assignEnabled(true);

	    if (pointer.getEnabled()==true)
	        combobox.disabled=false;
	    else
	        combobox.disabled=true;

	    pointer.debug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    pointer.setInitialized(true);
	    
	    pointer.addComponentReference(pointer, combobox);
	    
	    pointer.setComponent(combobox);

	    pointer.getDivWrap().appendChild(combobox);
	    
		pointer.modifyCSSAttribute("width", pointer.getWidth());
		pointer.modifyCSSAttribute("height", pointer.getHeight());

	    pointer.render();

	    currentZIndex++;
	    currentIDIndex++;    	    
	};

	/**
	 * 
	 */
	this.getHTMLComponent=function getHTMLComponent ()
	{
		return (combobox);
	}
	
	/**
	 * 
	 * @param aValue
	 */
	this.addItem=function addItem(aValue) 
	{
	    pointer.debug("addItem (" + aValue + ")");

	    option=document.createElement("option");
	    option.setAttribute("value", aValue);
	    option.innerHTML=aValue;

	    combobox.appendChild(option);
	};	
	
	/**
	 * 
	 */
	this.processSerialization=function processSerialization()
	{
		//useDebugging=true;
		
		pointer.debug ("processSerialization()");

		if (combobox==null)
		{
			pointer.debug ("Error: Internal weirdness: combobox object is null in serialization method");
			return;
		}
		
		// Process component specific pre-defined styles ...
		

		
		// Process component custom styles ...		

		if (this.styles==null)
		{
			pointer.debug ("Error: styles structure is null");
			return;
		}
		
		var i=0;
		var aStyle=null;
		var splitCharacter=',';
				
		pointer.debug ("PRE Processing " + this.styles.length + " styles ...");
		
		for (i=0;i<this.styles.length;i++)
		{
			aStyle=this.styles [i]; // CTATStyle
			
			pointer.debug ("Processing style " + aStyle.styleName + "," + aStyle.styleValue);
			
			if (aStyle.styleName=="SplitCharacter")
			{
				pointer.debug ("Setting SplitCharacter to: " + aStyle.styleValue);
								
				splitCharacter=aStyle.styleValue;
			}	
		}
		
		pointer.debug ("Processing " + this.styles.length + " styles ...");
		
		for (i=0;i<this.styles.length;i++)
		{
			aStyle=this.styles [i]; // CTATStyle
			
			pointer.debug ("Processing style " + aStyle.styleName + "," + aStyle.styleValue);
			
			if (aStyle.styleName=="Labels")
			{
				pointer.debug ("Setting Labels to: " + aStyle.styleValue + " using split character: " + splitCharacter);
								
				var n=aStyle.styleValue.split(splitCharacter); 
				
				for (var j=0;j<n.length;j++)
				{
					try
					{
						var aLabel=n [j];
						
						pointer.debug ("label: " + aLabel);
				
						this.addItem (aLabel);											
					}
					catch (err)
					{
						pointer.debug ("Exception: " + err.message);
					}
				}	
			}	
		}	
		
		//useDebugging=false;
	};
	
	/**
	 * 
	 */
	this.processComboSelection=function processComboSelection ()
	{
		pointer.debug ("processComboSelection ()");

		commShell.gradeComponent (pointer);
	};
}

CTATComboBox.prototype = Object.create(CTATTutorableComponent.prototype);
CTATComboBox.prototype.constructor = CTATComboBox;