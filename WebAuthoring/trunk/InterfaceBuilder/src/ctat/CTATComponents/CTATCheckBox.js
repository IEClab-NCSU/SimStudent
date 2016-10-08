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
function CTATCheckBox (aDescription,aX,aY,aWidth,aHeight)
{

	CTATTutorableComponent.call(this, 
					  "CTATCheckBox", 
					  "__undefined__",
					  aDescription,
					  aX,
					  aY,
					  aWidth,
					  aHeight);
	
	var checkbox=null;
	var label=null;
	var checkboxContainer=null;

	var pointer=this;

	this.getCheckBox=function getCheckBox()
	{
		return (checkbox);
	};

	this.setCheckBox=function setCheckBox(aCheckBox)
	{
		checkbox=aCheckBox;
	};
					
	this.debug (this.getClassName() + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");
	
	this.configFromDescription ();

	this.init=function init ()
	{
		pointer.debug ("init ("+pointer.getName ()+")");
													
		pointer.modifyCSSAttribute("z-index", currentZIndex);
		
		checkboxContainer=document.createElement('div');
		
		checkbox=document.createElement('input');
		checkbox.type='checkbox';
		checkbox.value=pointer.getName ();	    
		checkbox.name=pointer.getComponentGroup (); // might be wrong
		checkbox.setAttribute ('id', ('ctatdiv'+currentIDIndex));
		//component.setAttribute ('onkeypress','return noenter(event)');

		pointer.assignEnabled(true);
				
		if (pointer.getEnabled()==true)
			checkbox.disabled=false;
		else
			checkbox.disabled=true;
				
		pointer.debug ("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());		
		
		//component.innerHTML=text;
		
		pointer.setInitialized(true);
		
		pointer.addComponentReference (pointer,checkbox);
				
		pointer.getDivWrap().appendChild(checkboxContainer);
		
		label = document.createElement('label');
		label.htmlFor = "id";
		label.innerHTML=this.getName ();
		//label.appendChild(document.createTextNode('label'));
		
		pointer.setComponent(checkboxContainer);
		pointer.setLabel(label);
		
		checkboxContainer.appendChild (checkbox);
		checkboxContainer.appendChild (label);
		
	    checkboxContainer.setAttribute('style', pointer.getCSS());
				
		currentZIndex++;
		currentIDIndex++;
		
		pointer.addSafeEventListener ('click', pointer.processClick,checkbox);
		pointer.addSafeEventListener ('focus', pointer.processFocus,checkbox);
	};
	
	this.setText=function setText (aText)
	{		
		pointer.debug ("setText ("+aText+")");
		
		if (checkboxContainer!=null)
		{
			checkbox.value=aText;	
			label.innerHTML=aText;
		}	
		else
			pointer.debug ("Error: component is null, can't set label!");
	};
	
	this.showCorrect=function showCorrect()
	{
		pointer.debug("showCorrect("+correctColor+")");
		
		pointer.setFontColor(correctColor);
		pointer.setEnabled (false);
	};
	
	this.setEnabled=function setEnabled(aValue) 
	{
		if (checkbox==null)
			return;
		
		checkbox.disabled=!aValue;
	};
	
	this.getCheckBoxInput=function getCheckBoxInput ()
	{
		return (label.innerHTML+": "+checkbox.checked);
	};
	
	/**
	 * 
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.debug ("processSerialization()");

		// Process component specific pre-defined styles ...
		
		// Process component custom styles ...		

		this.styles=pointer.getGrDescription().styles;
		
		pointer.debug ("Processing " + this.styles.length + " styles ...");
		
		for (var i=0;i<this.styles.length;i++)
		{
			var aStyle=this.styles [i]; // CTATStyle
			
			if(aStyle.styleName=="labelText")
			{
				pointer.setText(aStyle.styleValue);
			}
		}	
	};	
	
	this.reset=function reset ()
	{
		checkbox.checked=false;
		pointer.setEnabled(true);
		label.setAttribute("style", " ");
	};
}

CTATCheckBox.prototype = Object.create(CTATTutorableComponent.prototype);
CTATCheckBox.prototype.constructor = CTATCheckBox;