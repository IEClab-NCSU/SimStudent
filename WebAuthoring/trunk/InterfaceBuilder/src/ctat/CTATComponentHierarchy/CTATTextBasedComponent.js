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

/**
 * 
 */
function CTATTextBasedComponent(aClassName, aName, aDescription, aX, aY, aWidth, aHeight)
{

	CTATTutorableComponent.call(this,
					  			aClassName, 
					  			aName,
					  			aDescription,
					 			aX,
					 			aY,
					 			aWidth,
					 			aHeight);
					 			
	var pointer=this;				 			
	var text="";
	var tabOnEnter=false;
	var maxCharacters=255;
	var editable=true;
	
	this.assignText=function assignText(aText)
	{
		text=aText;
	};
	
	this.setTabOnEnter=function setTabOnEnter(aValue)
	{
		tabOnEnter=aValue;
	};
	
	this.assignEditable=function assignEditable(aEditable)
	{
		editable=aEditable;
	};
	
	this.setMaxCharacters=function setMaxCharacters(aMax)
	{
		maxCharacters=aMax;
	};
	
	this.getText=function getText()
	{
		return (text);
	};
	
	this.getEditable=function getEditable()
	{
		return (editable);
	};
	
	this.getTabOnEnter=function getTabOnEnter()
	{
		return (tabOnEnter);
	};
	
	this.getMaxCharacters=function getMaxCharacters()
	{
		return (maxCharacters);
	};
	
	function getKey (e)
	{
	     var key;     
	     
		 if (platform=="google")
		 {
			return (0);
		 }		 
		 
	     if(window.event)
	          key = window.event.keyCode; //IE
	     else
	          key = e.which; //firefox
	     
	     return (key);
	}
	
	/**
	 * 
	 * @param aValue
	 */
	this.setEditable=function setEditable(aValue)
	{
		pointer.assignEditable(aValue);
				
		if (pointer.getComponent()==null)
			return;
		
		if (pointer.getEditable()==true)
			pointer.getComponent().contentEditable='true';
		else
			pointer.getComponent().contentEditable='false';
			
		pointer.setFontColor(pointer.getDisabledTextColor());
		pointer.setBackgroundColor(pointer.getDisabledBGColor());
	};
	
	/**
	 * Override from CTATCompBase because for text based components
	 * we also have to set them non-editable
	 * 
	 * @param aValue
	 */
	this.setEnabled=function setEnabled(aValue) 
	{
		pointer.assignEnabled(aValue);
			
		if (pointer.getComponent()==null)
			return;
			
		pointer.getComponent().disabled=!aValue;
		
		this.setEditable (aValue);
	};

	this.processKeypress=function processKeypress (e)
	{
		pointer.debug ("processKeypress ()");
		
		var id=e.target.getAttribute ("id");
		pointer.debug(id);
		var comp=pointer.getComponentFromID (id);
		
		var textElement=pointer.getComponent();
		//alert(textElement.innerHTML);
		if (comp==null)
		{
			pointer.debug ("Error: component reference is null");
			return;
		}
		
		pointer.debug (comp.name + " keydown ("+getKey (e)+" -> "+e.eventPhase+") " + "ID: " + id);
								
		currentComponent=id;
		currentComponentPointer=comp;
		
	    switch (getKey (e)) 
	    {
	    	// key code for left arrow
	       	case 37:
	       			pointer.debug('left arrow key pressed!');
	       			break;
		        
	       			// key code for right arrow
	       	case 39:
	       			pointer.debug('right arrow key pressed!');
	       			break;
	       			
	       	case 13:
	       			pointer.debug('Enter key pressed!');
	       			
	       			if(tabOnEnter==true)
	       			{
	       				commShell.focusNextComponent (comp);
	       			}
	       			
	       			commShell.gradeComponent (comp);
	       			return (false);
	       			
	       	default:
	       			pointer.debug('Key pressed!');
	       			
	       			/*
	       			if(this.getComponent().innerHTML.length>maxCharacters)
	       			{
	       				this.getComponent().innerHTML=this.getComponent().innerHTML.substring(0, maxCharacters);
	       				alert("You have exceeded the maximum characters allowed: " + maxCharacters);
	       			}
	       			*/
	    }
	};
}

CTATTextBasedComponent.prototype = Object.create(CTATTutorableComponent.prototype);
CTATTextBasedComponent.prototype.constructor = CTATTextBasedComponent;