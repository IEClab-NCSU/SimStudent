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
 
*/

function CTATRadioButton(aDescription, aX, aY, aWidth, aHeight)
{
	CTATTutorableComponent.call(this, 
					  			"CTATRadioButton", 
					  			"__undefined__",
					  			aDescription,
					  			aX,
					  			aY,
					  			aWidth,
					  			aHeight);
					  
	this.debug (this.getClassName() + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	this.configFromDescription();
	
	var pointer=this;
	var radiobutton=null;
	var radioContainer=null;
	var label;
	var radioGroup;
	var radioText="";
	
	this.init=function init()
	{
		pointer.debug ("init ("+pointer.getName ()+")");
		
		pointer.addCSSAttribute("z-index", currentZIndex);
		
		radioContainer=document.createElement('div');

		radiobutton=document.createElement('input');
		radiobutton.type='radio';
		radiobutton.value=pointer.getName();
		radiobutton.name=pointer.getComponentGroup();
		radiobutton.setAttribute('id', ('ctatdiv'+currentIDIndex));

		pointer.assignEnabled(true);
		
		if (pointer.getEnabled()==true)
			radiobutton.disabled=false;
		else
			radiobutton.disabled=true;
				
		pointer.debug ("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());
		
		pointer.setInitialized(true);
		
		pointer.addComponentReference(pointer, radioContainer);
				
		pointer.getDivWrap().appendChild(radioContainer);
		
		label = document.createElement('label');
		label.htmlFor = "id";
		label.innerHTML=pointer.getText ();
		//label.appendChild(document.createTextNode('label'));
		
		pointer.setComponent(radioContainer);
		pointer.setLabel(label);
		
		radioContainer.appendChild (radiobutton);
		radioContainer.appendChild (label);
		
	    pointer.render ();
				
		currentZIndex++;
		currentIDIndex++;

		pointer.addSafeEventListener ('click',pointer.processClick,radiobutton);
		pointer.addSafeEventListener ('focus', pointer.processFocus,radiobutton);			
	};
	
	this.setText=function setText (aText)
	{		
		pointer.debug ("setText ("+aText+")");
		
		radioText=aText;
		
		if (radioContainer!=null)
		{
			radiobutton.value=aText;	
			label.innerHTML=aText;
		}
	};
	
	this.getText=function getText ()
	{
		return (radioText);
	}
	
	this.showCorrect=function showCorrect()
	{
		pointer.debug("showCorrect("+correctColor+")");
		
		pointer.setFontColor(correctColor);
		pointer.setEnabled (false);
	};
	
	/**
	 * Radiobutton groups are disabled when they are correct. We need to
	 * disable them when we received a CorrectAction. The action parameter
	 * is needed for this case, and is otherwise optional.
	 */
	this.setEnabled=function setEnabled(aValue, action) 
	{
		if (action==undefined)
		{
			if (radiobutton==null)
				return;
		
			radiobutton.disabled=!aValue;
		}
		
		else if (action=="CorrectAction")
		{
			for (var i=0;i<components.length;i++)
			{
				var aDesc=components [i];
				
				if(aDesc.groupName=="")
				{
					pointer.setEnabled(false);
				}
			}
		}
	};
	
	this.setChecked=function setChecked(aValue)
	{
		if (radiobutton==null)
			return;
		
		radiobutton.checked=aValue;
	};
	
	this.getChecked=function getChecked()
	{
		return (radiobutton.checked);
	};
	
	//SAI Input
	this.getRadioInput=function getRadioInput ()
	{
		return (pointer.getName()+": "+label.innerHTML);
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
			
			if(aStyle.styleName=="buttonLabel")
			{
				pointer.setText(aStyle.styleValue);
			}
		}	
	};	
	
	this.reset=function reset ()
	{
		pointer.debug (" reset ( " + pointer.getName () + ")");

		radiobutton.checked=false;
		pointer.setEnabled(true);
		label.setAttribute("style", " ");
	};
	
	/**
	 * TPA 
	 */
	 this.UpdateRadioButton=function UpdateRadioButton ()
	 {
	 	pointer.debug ("UpdateRadioButton ()");
	 };
}

CTATRadioButton.prototype = Object.create(CTATTutorableComponent.prototype);
CTATRadioButton.prototype.constructor = CTATRadioButton;