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
function CTATCompBase (aClassName,
					   aName,
					   aDescription,
					   aX,
					   aY,
					   aWidth,
					   aHeight) 
{
	CTATBase.call (this, aClassName, aName);

	var grDescription=aDescription || genDesc;
	
	var x=aX || -2;
	var y=aY || -2;
	var width=aWidth || -2;
	var height=aHeight || -2;
	
	this.debug ("CTATCompBase" + " ("+x+","+y+","+width+","+height+")");
	
	var text="";
	var backgroundColor=globalBackgroundColor;
	var borderColor=globalBorderColor;	
	var fontColor=globalFontColor;
	var fontFamily=globalFontFamily;
	var fontSize=globalFontSize;
	var align=globalAlign;
	var initialized=false;
	var enabled=true;
	var showBorder=false;
	var borderStyle="solid";
	var divWrapper=null;
	var highlighted=false;
	var isBolded=false;
	var isItalicized=false;
	var isUnderlined=false;
	var disableOnCorrect=true;
	var disabledBGColor="white";
	var disabledTextColor="black";
	var showHintHighlight=true;
	var componentGroup="";

	var component=null;
	var label=null;
	var parameters=null;
	var styles=null;
	var pointer=this;
	
	var hasImages=false;	
	var backgroundColor=globalBackgroundColor;	
	var selected=false;	
	
	var componentStyle=new CTATCSS();
	var initialStyle="";
	generateBaseStyle();
	
	var subCanvas=document.createElement('canvas');
	var canvasVisible="hidden";
	
	var topDivZIndex=currentZIndex;
	var topDivIDIndex=currentIDIndex;
	
	var canvasZIndex=currentZIndex+1;
	var canvasIDIndex=currentIDIndex+1;
	
	// Let's give it some kind of default value
	var internalSAI=new CTATSAI ("this","ButtonPressed","-1");
	
	currentZIndex+=2;
	currentIDIndex+=2;

	/**
	 * 
	 */
	this.setSAI=function setSAI (aSelection,anAction,anInput)
	{
		internalSAI=new CTATSAI (aSelection,anAction,anInput,"");		
	};
	
	/**
	 * 
	 */
	this.getSAI=function getSAI ()
	{
		internalSAI.setSelection (pointer.getName());
		
		return (internalSAI);
	};
	
	/**
	*
	*/
	this.setStyle=function setStyle (aStyle,aValue)
	{
		componentStyle.addCSSAttribute(aStyle,aValue);	
	};
	
	/**
	*
	*/
	this.setStyleAll=function setStyleAll (aStyle,aValue)
	{
		subCanvas.setAttribute(aStyle,aValue);
		componentStyle.modifyCSSAttribute(aStyle,aValue);	
	};	
	
	/**
	*
	*/
	function generateBaseStyle()
	{
		pointer.debug ("generateBaseStyle()");
	
		componentStyle.addSelector(":focus");
		componentStyle.addSelectorAttribute(":focus", "outline", 0);
	
		componentStyle.addCSSAttribute("left", x+"px");
		componentStyle.addCSSAttribute("top", y+"px");
    componentStyle.addCSSAttribute("width", width+"px");
		componentStyle.addCSSAttribute("height", height+"px");
		
		componentStyle.addCSSAttribute("position", "absolute");
	}	
	
	/**
	 * 
	 * @param topDiv
	 */
	this.wrapComponent=function wrapComponent(topDiv)
	{
		pointer.debug ("wrapComponent ()");
	
		divWrapper=document.createElement('div');
		divWrapper.setAttribute('id', ('ctatdiv' + topDivIDIndex));
		divWrapper.setAttribute('name', grDescription.name);
		divWrapper.setAttribute('onkeypress', 'return noenter(event)');
		divWrapper.setAttribute('style', "z-index: "+topDivZIndex);
		topDiv.appendChild(divWrapper);
		
		pointer.debug ("Container div created, adding canvas ...");
		
		subCanvas.setAttribute('id', ('ctatdiv' + canvasIDIndex));
		subCanvas.setAttribute('onkeypress', 'return noenter(event)');
		subCanvas.setAttribute('width', width+canvasCalibrate+"px");
		subCanvas.setAttribute('height', height+canvasCalibrate+"px");
		subCanvas.setAttribute("style", "border: 1px #ff0000 solid; position: absolute; visibility:"+canvasVisible+"; left:"+x+"px; top:"+y+"px; z-index:"+canvasZIndex+";");

		divWrapper.appendChild(subCanvas);
		
		pointer.debug ("wrapComponent () done");		
	};
	
	/**
	 * http://ajaxian.com/archives/forcing-a-ui-redraw-from-javascript
	 */
	this.redraw=function redraw ()
	{
		var temp=divWrapper.style.display;
		divWrapper.style.display="none";
		var redrawFix = divWrapper.offsetHeight;
		divWrapper.style.display=temp; // or other value if required	
	};
	
	/**
	*
	*/
	this.setCanvasVisibility=function setCanvasVisibility(anAttrib)
	{
		canvasVisible=anAttrib;
	};
	
	/**
	*
	*/
	this.getSubCanvasCtx=function getSubCanvasCtx()
	{
		return subCanvas.getContext("2d");
	};

//Accessors-----------------------------------------------------------

	/**
	*
	*/
	this.getSubCanvas=function getSubCanvas ()
	{
		return (subCanvas);
	};

	this.getGrDescription=function getGrDescription()
	{
		return (grDescription);
	};

	this.getX=function getX() 
	{
		return (x);
	};

	this.getY=function getY()
	{
		return (y);
	};

	this.getWidth=function getWidth()
	{
		return (width);
	};
	
	this.getDisabledBGColor=function getDisabledBGColor()
	{
		return (disabledBGColor);
	};
	
	this.getDisabledTextColor=function getDisabledTextColor()
	{
		return (disabledTextColor);
	};
	
	this.getBorderStyle=function getBorderStyle()
	{
		return (borderStyle);
	};

	this.getHeight=function getHeight()
	{
		return (height);
	};

	this.getText=function getText()
	{
		return (text);
	};

	this.getBackgroundColor=function getBackgroundColor()
	{
		return (backgroundColor);
	};

	this.getBorderColor=function getBorderColor()
	{
		return (borderColor);
	};

	this.getFontColor=function getFontColor()
	{
		return (fontColor);
	};

	this.getFontFamily=function getFontFamily()
	{
		return (fontFamily);
	};

	this.getFontSize=function getFontSize()
	{
		return (fontSize);
	};

	this.getAlign=function getAlign()
	{
		return (align);
	};

	this.getInitialized=function getInitialized() 
	{
		return (initialized);
	};

	this.getEnabled=function getEnabled()
	{
		return (enabled);
	};

	this.getShowBorder=function getShowBorder()
	{
		return (showBorder);
	};

	this.getDivWrap=function getDivWrap()
	{
		return (divWrapper);
	};
	
	this.getComponent=function getComponent()
	{
		return (component);
	};
	
	this.getLabel=function getLabel()
	{
		return (label);
	};

	this.getHighlighted=function getHighlighted()
	{
		return (highlighted);
	};
	
	/**
	*
	*/
	this.getCanvasZIndex=function getCanvasZIndex ()
	{
		return (canvasZIndex);
	};
	
    /**
    * This method is primarily for checkboxes and radio buttons so that they
    * may be all grouped together.
    */
	this.getComponentGroup=function getComponentGroup()
	{
		return (componentGroup);
	};

	//Mutators-----------------------------------------------------------

	this.setGrDescription=function setGrDescription(aGrDescription)
	{
		grDescription=aGrDescription;
    };
    
    this.setComponent=function setComponent(aComponent)
    {
    	component=aComponent;
    };
    
    
    /**
    * This method is primarily for checkboxes and radio buttons so that they
    * may be all grouped together.
    */
    this.setComponentGroup=function setComponentGroup(aGroup)
    {
    	componentGroup=aGroup;
    	
    	if (component!=null)
    	{
    		component.name=aGroup;
    	}
    }
    
    this.setLabel=function setLabel(aLabel)
    {
    	label=aLabel;
    };
    
    this.setdivWrapper=function setDivWrapper(aWrapping) 
    {
        divWrapper=aWrapping;
    };

    this.assignText=function assignText(aText)
    {
    	text=aText;
    };
    
	//Needed because there is already a setEnabled method, which happens to be abstract...
	this.assignEnabled=function assignEnabled(aValue)
	{
		enabled=aValue;
	};
	
	this.getCompCSS=function getCompCSS()
	{
		return (componentStyle);
	};

	/**
	 * Even though the reference is called component we can also think 'div',
	 * since in a lot of cases that will be true.
	 */
	this.init=function init ()
	{
		pointer.debug("THIS IS THE BASE INIT METHOD");
	};
	
	/**
	*
	*/	
	this.addSafeEventListener=function addSafeEventListener(aType, aFunction, aTarget)
	{
		pointer.debug ("addSafeEventListener ("+aType+")");
		
		pointer.debug ("Adding event listener to: " + aTarget);
		
		pointer.debug ("Pointing event listener to function: " + aFunction);
	
		if (aTarget!=null)
		{
			aTarget.addEventListener (aType,aFunction);
			return;
		}
	
		if (component!=null)
		{
			component.addEventListener(aType,aFunction);
		}
		else
		{
			pointer.debug ("Error: pointer to component is null, can't add event listener");
		}
		
		pointer.debug ("addSafeEventListener () done");
	};	
	
	//Wrapper functions for the CSS stuff - so user doesn't need to keep using the getter
	this.addCSSAttribute=function addCSSAttribute(attrib, val)
	{
		componentStyle.addCSSAttribute(attrib, val);
		this.render ();
	};
	
	/**
	*
	*/	
	this.render=function render ()
	{
		pointer.debug ("render ()");
    pointer.debug ("("+x+","+y+","+width+","+height+")");
	
		if (component!=null)
		{
			component.setAttribute('style', componentStyle.toCSSString());
      pointer.debug(componentStyle.toCSSString());
		}
		else
		{
			pointer.debug ("Internal error, html component not available for rendering");
		}
		
		pointer.debug ("render () done");
	};
	
	/**
	*
	*/	
	this.clearCSS=function clearCSS ()
	{
		componentStyle.clearCSS();
		this.render ();
	};
	
	/**
	*
	*/	
	this.addStringCSS=function addStringCSS(str) 
	{
		componentStyle.addStringCSS(str);
		this.render ();
	};
	
	/**
	*
	*/	
	this.addSelector=function addSelector(sel)
	{
		componentStyle.addSelector(sel);
	};
	
	/**
	*
	*/	
	this.addSelectorAttribute=function addSelectorAttribute(sel, attrib, val)
	{
		componentStyle.addSelectorAttribute(sel, attrib, val);
	};
	
	/**
	*
	*/	
	this.modifyCSSAttribute=function modifyCSSAttribute(attrib, val)
	{
		componentStyle.modifyCSSAttribute(attrib, val);
		this.render ();
	};
	
	/**
	*
	*/	
	this.getCSS=function getCSS()
	{
		return componentStyle.toCSSString();
	};
	
	/**
	*
	*/	
    this.setInitialized=function setInitialized(aInitialized) 
    {
		pointer.debug ("setInitialized ("+aInitialized+")");
	
        initialized = aInitialized;
        
        if((initialized==true) && (divWrapper==null))
        {
			//useDebugging=true;
		
        	pointer.debug ("Wrapping a component and appending it to the top container div if one was never set ...");
			
			var aClip=findPointOfAttachment (this.getName ());
			
			if (aClip!=null)
			{
				pointer.debug ("Attaching component to existing MovieClip ...");
				
				pointer.wrapComponent(aClip.getDivWrapper ());
			}
			else
			{
				pointer.debug ("Attaching component to main div ...");
			
				//pointer.wrapComponent(getSafeElementById("container")); // TODO this was the original -- Kevin Jeffries
				pointer.wrapComponent(getSafeElementById("mygraphiccontainer")); // TODO this is my modified version -- Kevin Jeffries
			}
			
			//useDebugging=false;
        }
		else
		{
			pointer.debug ("Not wrapping, initialized=" + initialized + ", divWrapper = " + divWrapper);		
		}
		
		pointer.debug ("setInitialized () done");
    };
		
	/**
	*
	*/	
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
	*/	
	this.drawComponent = function drawComponent() 
	{
	    //debug ("drawComponent ()");

	    // Nop
	};

	/**
	*
	*/	
	this.addComponentReference = function addComponentReference (aComponent,aDiv)
	{
		pointer.debug ("addComponentReference ("+aComponent.getName ()+" -> "+aDiv.getAttribute ("id")+")");
					
		var newRef=new CTATComponentReference (aComponent,aDiv);

		pointer.debug ("created new component reference");
											
		componentReferences.push (newRef);
		
		pointer.debug ("addComponentReference () done");
	};

	/**
	*
	*/	
	this.getComponentFromID = function getComponentFromID (anID)
	{
		pointer.debug ("getComponentFromID ("+anID+") -> " + componentReferences.length + " references");
				
		for (var i=0;i<componentReferences.length;i++)
		{
			var ref=componentReferences [i];
						
			if (ref.getDiv ().getAttribute ("id")==anID)
			{				
				if (ref.getElement ()==null)
				{
					pointer.debug ("Error: found component reference is null! ("+ref.componentReference+")");
					return (null);
				}
				
				pointer.debug ("Found component reference: " + ref.getElement ().getName () + " for div: " + anID);
											
				return (ref.getElement ());
			}
		}
		
		pointer.debug ("Bottoming out ...");
		
		return (null);
	};

	/**
	 * 
	 */
	this.configFromDescription=function configFromDescription ()
	{
		pointer.debug ("configFromDescription ()");
		
		if (grDescription==null)
		{
			pointer.debug ("Error: no deserialized component description available");
			return;
		}
		
		pointer.setName(grDescription.name);
		
		//>-----------------------------------------------------------------------------
		
		this.parameters=grDescription.params;
		
		if (this.parameters!=null)
		{		
			for(var i=0;i<this.parameters.length;i++)
			{
				var aParam=this.parameters [i];
				
				if (aParam.paramName=="ShowHintHighlight")
				{
					pointer.setHintHighlight(aParam.paramValue);
				}
				
				if (aParam.paramName=="DisableOnCorrect")
				{
					disableOnCorrect=aParam.paramValue;
				}
				
				if (aParam.paramName=="tutorComponent")
				{
					pointer.setTutorComponent(aParam.paramValue);
				}
				
				if (aParam.paramName=="group")
				{
					pointer.setComponentGroup(aParam.paramValue);
				}
			}
		}
		
		//>-----------------------------------------------------------------------------		
			
		this.styles=grDescription.styles;
		
		if (this.styles!=null)
		{
			pointer.debug ("Processing " + this.styles.length + " styles ...");
			
			for (var i=0;i<this.styles.length;i++)
			{
				var aStyle=this.styles [i]; // CTATStyle
				
				//pointer.debug ("Processing style " + aStyle.styleName + "," + aStyle.styleValue);	
				
				if (aStyle.styleName=="backgroundColor")
				{
					pointer.setBackgroundColor("#"+aStyle.styleValue);
				}
				
				if (aStyle.styleName=="BorderColor")
				{
					pointer.setBorderColor("#"+aStyle.styleValue);
				}
				
				if (aStyle.styleName=="FontName")
				{
					pointer.setFontFamily(aStyle.styleValue);
				}
				
				if (aStyle.styleName=="FontSize")
				{
					pointer.setFontSize(aStyle.styleValue);
				}
				
				if (aStyle.styleName=="FontColor")
				{
					pointer.setFontColor(aStyle.styleValue);
				}
				
				if (aStyle.styleName=="FontBold")
				{
					isBolded=aStyle.styleValue;
					
					if(isBolded=="true")
					{
						componentStyle.addCSSAttribute("font-weight", "bold");
						
						this.render ();
					}
				}
				
				if (aStyle.styleName=="FontItalic")
				{
					isItalicized=aStyle.styleValue;
					
					if(isItalicized=="true")
					{
						componentStyle.addCSSAttribute("font-style", "italic");
						
						this.render ();
					}
				}
				
				if (aStyle.styleName=="FontUnderlined")
				{
					isUnderlined=aStyle.styleValue;

					if(isUnderlined=="true")
					{
						componentStyle.addCSSAttribute("text-decoration", "underline");
						
						this.render ();
					}
				}
				
				if (aStyle.styleName=="TextAlign")
				{
					pointer.setAlign(aStyle.styleValue);
				}

				if (aStyle.styleName=="disabledBackgroundColor")
				{
					disabledBGColor=aStyle.styleValue;
				}
				
				if (aStyle.styleName=="disabledTextColor")
				{
					disabledTextColor=aStyle.styleValue;
				}
			}
		}
		
		//>-----------------------------------------------------------------------------
		
		pointer.debug ("configFromDescription () done");
	};
	
	/**
	 * 
	 */
	this.processSerialization=function processSerialization()
	{
		//pointer.debug ("processSerialization()");
		
		// implement in child class
	};
	
	/**
	 * Ported from AS3
	 * @param w
	 * @param h
	 */
	this.move=function move(newX,newY) 
	{
		//pointer.debug ("move ("+newX+","+newY+")");
						
		this.x=newX;
		this.y=newY;

		componentStyle.modifyCSSAttribute("left", this.x+"px");
		componentStyle.modifyCSSAttribute("top", this.y+"px");		
		
		this.render ();
	};	
	/**
	 * Ported from AS3
	 * @param w
	 * @param h
	 */
	this.setSize=function setSize(w,h) 
	{
		pointer.debug ("setSize ("+w+","+h+")");
				
		this.width=w;
		this.height=h;		
				
		this.setStyleAll ('width',w+'px');
		this.setStyleAll ('height',h+'px');
		
		this.render ();		
	};
	/**
	 * Changes the visibility of the object. Ported from AS3
	 * @param aValue true/false visible or not.
	 */
	this.SetVisible=function SetVisible (aValue)
	{
		pointer.debug ("SetVisible ("+aValue+")","graphics");
	};
	
	/**
	 * Changes the visibility of the object. Ported from AS3
	 * @param aValue true/false visible or not.
	 */
	this.setVisible=function setVisible (aValue)
	{
		if (aValue==true)
			canvasVisible='block';
		else	
			canvasVisible='hidden';
	
		subCanvas.setAttribute("style", "border: 0px; position: absolute; visibility:"+canvasVisible+"; left:"+x+"px; top:"+y+"px; z-index:"+canvasZIndex+";");
	};
	
	/**
	 *  Ported from AS3
	 */		
	this.showCorrect=function showCorrect()
	{
		pointer.debug("showCorrect("+correctColor+")");
	
	    fontColor=correctColor;
	    showHintHighlight=false;
	    componentStyle.removeStringCSS(globalGlowString);
		componentStyle.modifyCSSAttribute("color", correctColor);
		
		if(disableOnCorrect=="true")
		{
			disabledTextColor=correctColor;
			pointer.setEnabled (false);
		}
		
		if(initialized==true)
		{
			this.render ();
		}
	};
	/**
	 * Ported from AS3
	 */		
	this.showInCorrect=function showInCorrect()
	{
		pointer.debug("showInCorrect("+incorrectColor+")");

		fontColor=incorrectColor;
		componentStyle.modifyCSSAttribute("color", incorrectColor);

		if(initialized==true)
		{
			pointer.debug ("Style: " + componentStyle.toCSSString());
		
			this.render ();
		}
		else
			pointer.debug ("Not initialized");
	};	
	/**
	 * Ported from AS3
	 */
	this.setHintHighlight=function setHintHighlight(newValue)
	{
		pointer.debug("setHintHighlight (" + newValue + ")");
		
		if (newValue==true)	
		{	
			showHintHighlight=true;
			componentStyle.addStringCSS(globalGlowString);
		}
		
		else 
		{
			showHintHighlight=false;
			componentStyle.removeStringCSS(globalGlowString);
		}
		
		if(initialized==true)
		{
			this.render ();
		}
	};
	/**
	 * Implement in child object
	 */
	this.reset=function reset ()
	{

	};
	/**
	 * 
	 */
	this.setX=function setX (aX)
	{		
		x=aX;
		componentStyle.modifyCSSAttribute("left", x+"px");
		
		if(initialized==true)
		{
			this.render ();
		}
	};

	this.setY=function setY (aY)
	{
		y=aY;
		componentStyle.modifyCSSAttribute("top", y+"px");
		
		if(initialized==true)
		{
			this.render ();
		}
	};

	this.setWidth=function setWidth (aWidth)
	{
		width=aWidth;
		componentStyle.modifyCSSAttribute("width", width+"px");
		
		if(initialized==true)
		{
			this.render ();
		}
	};

	this.setHeight=function setHeight (aHeight)
	{
		height=aHeight;
		componentStyle.modifyCSSAttribute("height", height+"px");
		
		if(initialized==true)
		{
			this.render ();
		}
	};

	this.setBackgroundColor=function setBackgroundColor (aColor)
	{
		backgroundColor=aColor;
		componentStyle.modifyCSSAttribute("background-color", backgroundColor);
		
		if(initialized==true)
		{
			this.render ();
		}
	};

	this.setBorderColor=function setBorderColor (aColor)
	{
		borderColor=aColor;
		componentStyle.modifyCSSAttribute("border-color", borderColor);
		componentStyle.modifyCSSAttribute("border-width", 1+"px");
		
		if(initialized==true)
		{
			this.render ();
		}
	};
	
	this.setBorderStyle=function setBorderStyle(aStyle)
	{
		borderStyle=aStyle;
		componentStyle.modifyCSSAttribute("border-style", borderStyle);
		componentStyle.modifyCSSAttribute("border-width", 1+"px");
		
		if(initialized==true)
		{
			this.render ();
		}
	};

	this.setFontColor=function setFontColor (aColor)
	{	
		fontColor=aColor;
		componentStyle.modifyCSSAttribute("color", fontColor);
		
		if(initialized==true)
		{
		
			//We want to set the font color. A component may use the font color for its inner
			//html, but if we have a label, then we need to set the label's font color.
			
			if(label==null)
			{
				this.render ();
			}
			
			else
			{
				label.setAttribute('style', "color: "+fontColor);
			}
		}
	};

	this.setFontFamily=function setFontFamily (aFont)
	{
		fontFamily=aFont;
		componentStyle.modifyCSSAttribute("font-family", fontFamily);
		
		if(initialized==true)
		{
			this.render ();
		}
	};

	this.setFontSize=function setFontSize (aSize)
	{
		fontSize=aSize;
		componentStyle.modifyCSSAttribute("font-size", fontSize+"pt");
		
		if(initialized==true)
		{
			this.render ();
		}
	};

	this.setEnabled=function setEnabled(aValue) 
	{
		pointer.assignEnabled(aValue);
			
		if (component==null)
			return;
			
		component.disabled=!aValue;	
	};

	this.setShowBorder=function setShowBorder (aValue)
	{
		showBorder=aValue;

		if(showBorder==true)
		{
			componentStyle.modifyCSSAttribute("border-style", borderStyle);
			componentStyle.modifyCSSAttribute("border-width", 1+"px");
		}
		
		else
		{
			componentStyle.modifyCSSAttribute("border-style", "hidden");
		}
		
		if(initialized==true)
		{
			this.render ();
		}
	};

	this.setAlign=function setAlign (anAlign)
	{
		align=anAlign;
		componentStyle.modifyCSSAttribute("text-align", align);
		
		if(initialized==true)
		{
			this.render ();
		}
	};

	this.setText=function setText(aText) 
	{
	    //Overrode in child objects
	};

	this.getValue=function getValue ()
	{
		if (component!=null)
			return (component.value);
		
		return "";
	};

	/**
	 * This method is called when a component gets focused. So keep in mind that
	 * we want to grade the component that lost focus, or in other words the
	 * previously focused component.
	 */
	this.processFocus=function processFocus (e)
	{
		pointer.debug ("processFocus ()");
		
		var id=e.currentTarget.getAttribute ("id");
		var comp=pointer.getComponentFromID (id);
		
		if (comp==null)
		{
			pointer.debug ("Error: component reference is null");
			return;
		}
		
		if (mobileAPI!=null)
		{
			if (mobileAPI.getEnabled ()==true)
			{
				pointer.hideKeyboard ();
			}	
		}	
		
		if (commShell!=null)
		{
			commShell.processComponentFocus (comp);
		}
		
		if (oldComponentFocus!=null)
		{
			pointer.debug ("old focus: " + oldComponentFocus.getName () + ", new focus:" + comp.getName ());
		
			if (oldComponentFocus==comp)
			{
				pointer.debug ("We're already there!");
				return;
			}
	
			if (commShell!=null)
			{
				commShell.gradeComponent (oldComponentFocus);
			}
			
			oldComponentFocus.setHintHighlight(false);
		}
		else
			pointer.debug ("No previously focused component yet, can't grade");
		
		oldComponentFocus=pointer;			
	};
	
	/**
	*
	*/
	this.hideKeyboard=function hideKeyboard () 
	{
		document.activeElement.blur();
		$("input").blur();
	};	
}

CTATCompBase.prototype = Object.create(CTATBase.prototype);
CTATCompBase.prototype.constructor = CTATCompBase;
