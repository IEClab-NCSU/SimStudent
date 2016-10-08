/**
 * @fileoverview Defines the base for all CTAT components.
 *
 * @author $Author: mringenb $
 * @version $Revision: 21689 $
 */
/**-----------------------------------------------------------------------------
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponentHierarchy/CTATCompBase.js $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATCompBase');

goog.require('CTATBase');
goog.require('CTATComponentReference');
goog.require('CTATConfig');
goog.require('CTATCSS');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATSandboxDriver');
/**
 *
 */
CTATCompBase = function(aClassName,
		aName,
		aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	CTATBase.call (this, aClassName, aName);

	var grDescription=aDescription || genDesc;

	var x=aX || 0;
	var y=aY || 0;
	var width=aWidth || -2;
	var height=aHeight || -2;

	// Overwrite in each component, used by the online authoring tools when creating new components
	var defaultWidth=50;
	// Overwrite in each component, used by the online authoring tools when creating new components
	var defaultHeight=25;

	this.ctatdebug ("CTATCompBase" + " ("+x+","+y+","+width+","+height+")");

	var abstractComponent=false;
	var text="";
	var backgroundColor=globalBackgroundColor;
	var borderColor=globalBorderColor;
	var fontColor=CTATGlobals.Font.color;
	var fontFamily=CTATGlobals.Font.family;
	var fontSize=CTATGlobals.Font.size;
	var align=globalAlign;
	var initialized=false;
	var enabled=true;
	var showBorder=false;
	var borderStyle="solid";
	var borderWidth="1px";
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
	var padding = 0;
	var borderRadius = 0;
	var tabIndex= -1;
	this.isTabIndexable = true;
	var zIndex= 0;

	var component=null;
	var label=null;
	var parameters=null;
	var styles=null;
	var pointer=this;

	var hasImages=false;
	var selected=false;

	var componentStyle=new CTATCSS();
	var initialStyle="";
	generateBaseStyle();

	var subCanvas=document.createElement('canvas');
	var canvasVisible="hidden";

	var topDivZIndex=CTATGlobalFunctions.gensym.z_index();
	var topDivID=CTATGlobalFunctions.gensym.div_id();

	var canvasZIndex=CTATGlobalFunctions.gensym.z_index();
	var canvasID=CTATGlobalFunctions.gensym.div_id();

	/**
	 *
	 */
	this.setIsAbstractComponent=function (aValue)
	{
		abstractComponent=aValue;
	};
	/**
	 * Overwrite in each component, used by the online authoring tools when creating new components
	 */
	this.setTabIndex=function (aValue)
	{
		if (this.isTabIndexable===true) {
			tabIndex=aValue;

			if (tabIndex==-1)
			{
				tabIndex=globalTabTracker;

				globalTabTracker++;
			}
			else
			{
				globalTabTracker=tabIndex; // otherwise we might overwrite indexes
			}
		} else {
			tabIndex = -1;
		}
	};
	/**
	 * Overwrite in each component, used by the online authoring tools when creating new components
	 */
	this.getTabIndex = function ()
	{
		return (tabIndex);
	};

	/**
	 *
	 */
	this.processTabOrder=function processTabOrder ()
	{
		pointer.ctatdebug ("processTabOrder (" + tabIndex + ")");

		if (abstractComponent===true)
		{
			pointer.ctatdebug ("Component is an abstract component, bump");
			return;
		}

		if (component)
		{
			pointer.ctatdebug ("We have a component, actually assigning to html component ...");

			component.tabIndex=tabIndex;

			component.onfocus=function()
			{
				//useDebugging=true;

				pointer.ctatdebug ("Onfocus triggered for: " + pointer.getName ());

				if (oldComponentFocus!=pointer)
				{
					pointer.ctatdebug ("oldComponentFocus!=pointer, taking action ...");

					oldComponentFocus=pointer;
				}
				else
				{
					pointer.ctatdebug ("oldComponentFocus==pointer");
				}

				//useDebugging=false;
			};
		}
		else
		{
			pointer.ctatdebug ("Error: we don't have an html component yet, can't assign tab index");
		}
	};

	/**
	 * Overwrite in each component, used by the online authoring tools when creating new components
	 */
	this.setZIndex=function setZIndex (aValue)
	{
		pointer.ctatdebug ("Setting z index to: " + aValue);

		zIndex=aValue;
	};
	/**
	 * Overwrite in each component, used by the online authoring tools when creating new components
	 */
	this.getZIndex=function getZIndex ()
	{
		return (zIndex);
	};

	/**
	 * Overwrite in each component, used by the online authoring tools when creating new components
	 */
	this.setDefaultWidth=function setDefaultWidth (aValue)
	{
		defaultWidth=aValue;
	};
	/**
	 * Overwrite in each component, used by the online authoring tools when creating new components
	 */
	this.getDefaultWidth=function getDefaultWidth ()
	{
		return (defaultWidth);
	};

	/**
	 * Overwrite in each component, used by the online authoring tools when creating new components
	 */
	this.setDefaultHeight=function setDefaultHeight (aValue)
	{
		defaultHeight=aValue;
	};
	/**
	 * Overwrite in each component, used by the online authoring tools when creating new components
	 */
	this.getDefaultHeight=function getDefaultHeight ()
	{
		return (defaultHeight);
	};

	/**
	 * Overwrite this!
	 */
	this.processCommShellEvent=function processCommShellEvent (anEvent,aMessage)
	{
		pointer.ctatdebug ("processCommShellEvent ("+anEvent+")");

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
		if (subCanvas)
		{
			subCanvas.setAttribute(aStyle,aValue);
		}

		componentStyle.modifyCSSAttribute(aStyle,aValue);
	};

	/**
	 *
	 */
	function generateBaseStyle()
	{
		pointer.ctatdebug ("generateBaseStyle()");

		componentStyle.addSelector(":focus");
		componentStyle.addSelectorAttribute(":focus", "outline", 0);

		componentStyle.addCSSAttribute("left", 0+"px");
		componentStyle.addCSSAttribute("top", 0+"px");
		//componentStyle.addCSSAttribute("width", width+"px");
		//componentStyle.addCSSAttribute("height", height+"px");

		componentStyle.addCSSAttribute("padding","0px");
		componentStyle.addCSSAttribute("position", "absolute");
	}

	/**
	 *
	 */
	this.makeDivWrapper = function(topDiv) {
		divWrapper=document.createElement('div');
		divWrapper.setAttribute('id', topDivID);
		divWrapper.setAttribute('name', grDescription.name);
		divWrapper.setAttribute('onkeypress', 'return noenter(event)');
		divWrapper.style.position = 'absolute';
		divWrapper.style.left = x+'px';
		divWrapper.style.top = y+'px';
		divWrapper.style.zIndex = topDivZIndex;
		divWrapper.style.width = pointer.getWidth()+'px';
		divWrapper.style.height = pointer.getHeight()+'px';
		//divWrapper.setAttribute('style', 'position: absolute;left:'+x+'px; top:'+y+'px; z-index: '+topDivZIndex+';width: '+pointer.getWidth()+'px;height: '+pointer.getHeight()+'px;');
		topDiv.appendChild(divWrapper);
	};
	/**
	 *
	 * @param topDiv
	 */
	this.wrapComponent=function wrapComponent(topDiv)
	{
		pointer.ctatdebug ("wrapComponent ()");
		pointer.makeDivWrapper(topDiv);
		pointer.ctatdebug ("Container div created, adding canvas ...");

		subCanvas.setAttribute('id', canvasID);
		subCanvas.setAttribute('onkeypress', 'return noenter(event)');
		subCanvas.setAttribute('width', width+canvasCalibrate+"px");
		subCanvas.setAttribute('height', height+canvasCalibrate+"px");
		subCanvas.setAttribute("style","border: 1px "+borderColor+" solid; visibility:"+canvasVisible+"; z-index:"+canvasZIndex+";");

		divWrapper.appendChild(subCanvas);

		pointer.ctatdebug ("wrapComponent () done");
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
		if (subCanvas)
			return subCanvas.getContext("2d");
		return undefined;
	};

//	Accessors-----------------------------------------------------------

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

	//Needed because there is already a setEnabled method, which happens to be abstract...
	this.assignEnabled=function assignEnabled(aValue)
	{
		enabled=aValue;
	};

	this.setEnabled=function setEnabled(aValue)
	{
		pointer.ctatdebug ("setEnabled ("+aValue+")");

		pointer.assignEnabled(aValue);

		if (!component)
		{
			pointer.ctatdebug ("Error: component pointer is null");
			return;
		}

		component.disabled=!aValue;
	};
	/**
	 * @function lock
	 * An Interface Action that locks the component (sets enabled to false).
	 */
	this.lock = this.setEnabled.bind(pointer,false);
	/**
	 * @function unlock
	 * An Interface Action that unlocks the component (sets enabled to true).
	 */
	this.unlock = this.setEnabled.bind(pointer,true);

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

	this.getPadding=function getPadding()
	{
		return padding;
	};

	this.getBorderRoundness=function()
	{
		return borderRadius;
	};
	this.getUnderlined=function()
	{
		//return isUnderlined;
	};
	this.getDisableOnCorrect = function(){
		return disableOnCorrect;
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

	this.setGrDescription=function setGrDescription(aGrDescription)
	{
		grDescription=aGrDescription;
	};

	this.setComponent=function setComponent(aComponent)
	{
		component=aComponent;
	};
	this.getTopDivZIndex=function(){
		return topDivZIndex;
	};
	this.getTopDivID=function(){
		return topDivID;
	};
	this.getShowHintHighlight=function(){
		return showHintHighlight;
	};
	//Mutators-----------------------------------------------------------

	/**
	 * This method is primarily for checkboxes and radio buttons so that they
	 * may be all grouped together.
	 */
	this.setComponentGroup=function setComponentGroup(aGroup)
	{
		componentGroup=aGroup;

		if (component)
		{
			component.name=aGroup;
		}
	};

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
		pointer.ctatdebug("THIS IS THE BASE INIT METHOD");
	};

	/**
	 *
	 */
	this.addSafeEventListener=function addSafeEventListener(aType, aFunction, aTarget)
	{
		pointer.ctatdebug ("addSafeEventListener ("+aType+")");

		pointer.ctatdebug ("Adding event listener to: " + aTarget);

		//pointer.ctatdebug ("Pointing event listener to function: " + aFunction);

		if (aTarget)
		{
			aTarget.addEventListener (aType,aFunction);
			return;
		}

		if (component)
		{
			component.addEventListener(aType,aFunction);
		}
		else
		{
			pointer.ctatdebug ("Error: pointer to component is null, can't add event listener");
		}

		pointer.ctatdebug ("addSafeEventListener () done");
	};

	/**
	 * Wrapper functions for the CSS stuff - so user doesn't need to keep using the getter
	 */
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
		pointer.ctatdebug ("render ()");

		if (abstractComponent===true)
		{
			pointer.ctatdebug ("Component is an abstract component, bump");
			return;
		}

		pointer.ctatdebug ("("+x+","+y+","+width+","+height+")");

		// console.log ("("+x+","+y+","+width+","+height+")");

		if (component)
		{
			console.log(componentStyle.toCSSString());
			component.setAttribute('style', componentStyle.toCSSString());
			pointer.ctatdebug(componentStyle.toCSSString());
		}
		else
		{
			pointer.ctatdebug ("Internal error, html component not available for rendering");
		}

		pointer.ctatdebug ("render () done");
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
		//useDebugging=true;
		pointer.ctatdebug ("setInitialized ("+aInitialized+")");

		initialized = aInitialized;

		if((initialized===true) && (!divWrapper))
		{

			pointer.ctatdebug ("Wrapping a component and appending it to the top container div if one was never set ...");

			var aClip=findPointOfAttachment (this.getName ());

			if (aClip)
			{
				pointer.ctatdebug ("Attaching component to existing MovieClip ...");

				pointer.wrapComponent(aClip.getDivWrapper ());
			}
			else
			{
				pointer.ctatdebug ("Attaching component to main div ...");

				pointer.wrapComponent(getSafeElementById("container"));
			}

		}
		else
		{
			pointer.ctatdebug ("Not wrapping, initialized=" + initialized + ", divWrapper = " + divWrapper);
		}

		pointer.ctatdebug ("setInitialized () done");
		//useDebugging=false;
	};

	/**
	 *
	 */
	function getKey (e)
	{
		var key;

		if (CTATConfig.platform=="google")
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
		//ctatdebug ("drawComponent ()");

		// Nop
	};

	/**
	 *
	 */
	this.addComponentReference = function addComponentReference (aComponent,aDiv)
	{
		pointer.ctatdebug ("addComponentReference ("+aComponent.getName ()+" -> "+aDiv.getAttribute ("id")+")");

		var newRef=new CTATComponentReference (aComponent,aDiv);

		pointer.ctatdebug ("Assigning tab index (if set) ...");

		aComponent.tabIndex=this.getTabIndex ();

		pointer.ctatdebug ("created new component reference");

		componentReferences.push (newRef);

		pointer.ctatdebug ("addComponentReference () done");
	};

	/**
	 *
	 */
	this.getComponentFromID = function getComponentFromID (anID)
	{
		pointer.ctatdebug ("getComponentFromID ("+anID+") -> " + componentReferences.length + " references");

		for (var i=0;i<componentReferences.length;i++)
		{
			var ref=componentReferences [i];

			if (ref.getDiv ().getAttribute ("id")==anID)
			{
				if (!ref.getElement())
				{
					pointer.ctatdebug ("Error: found component reference is null! ("+ref.componentReference+")");
					return (null);
				}

				pointer.ctatdebug ("Found component reference: " + ref.getElement ().getName () + " for div: " + anID);

				return (ref.getElement ());
			}
		}

		pointer.ctatdebug ("Bottoming out ...");

		return (null);
	};

	/**
	 *
	 */
	this.configFromDescription=function configFromDescription ()
	{

		pointer.ctatdebug ("configFromDescription ()");

		if (!grDescription)
		{
			pointer.ctatdebug ("Error: no deserialized component description available");
			return;
		}
		pointer.setName(grDescription.name);
		//>-----------------------------------------------------------------------------

		this.parameters=grDescription.params;

		if (this.parameters)
		{
			for(var i=0;i<this.parameters.length;i++)
			{
				var aParam=this.parameters [i];

				if (aParam.paramName=="ShowHintHighlight")
				{
					//pointer.setHintHighlight(aParam.paramValue);
					showHintHighlight = aParam.paramValue == 'true';
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
		//console.log(grDescription.styles);

		if (this.styles)
		{
			pointer.ctatdebug ("Processing " + this.styles.length + " styles ...");

			for (var j=0;j<this.styles.length;j++)
			{
				var aStyle=this.styles [j]; // CTATStyle
				// aStyle.styleValue = aStyle.styleValue.trim();
				//pointer.ctatdebug ("Processing style " + aStyle.styleName + "," + aStyle.styleValue);

				if (aStyle.styleName=="backgroundColor")
				{
					pointer.setBackgroundColor(formatColor(aStyle.styleValue));
				}

				if (aStyle.styleName=="BorderColor")
				{
					pointer.setBorderColor(formatColor(aStyle.styleValue));
				}

				if (aStyle.styleName=="FontFace")
				{
					pointer.setFontFamily(aStyle.styleValue);
				}

				if (aStyle.styleName=="FontSize")
				{
					pointer.setFontSize(parseInt(aStyle.styleValue));
				}

				if (aStyle.styleName=="TextColor")
				{
					pointer.setFontColor(formatColor(aStyle.styleValue));
				}

				if (aStyle.styleName=="FontBold")
				{
					pointer.setBolded(aStyle.styleValue);
				}

				if (aStyle.styleName=="FontItalic")
				{
					pointer.setItalicized(aStyle.styleValue);
				}

				if (aStyle.styleName=="FontUnderlined")
				{
					pointer.setUnderlined(aStyle.styleValue);
				}

				if (aStyle.styleName=="TextAlign")
				{
					pointer.setAlign(aStyle.styleValue);
				}

				if (aStyle.styleName=="disabledBackgroundColor")
				{
					disabledBGColor=formatColor(aStyle.styleValue);
				}

				if (aStyle.styleName=="disabledTextColor")
				{
					disabledTextColor=formatColor(aStyle.styleValue);
				}

				if(aStyle.styleName=="showBorder")
				{
					pointer.setShowBorder(aStyle.styleValue=='true');
				}

				if(aStyle.styleName=="borderRoundness")
				{
					pointer.setBorderRoundness(parseInt(aStyle.styleValue));
				}
			}
		}

		//>-----------------------------------------------------------------------------

		pointer.ctatdebug ("configFromDescription () done");
	};

	/**
	 *
	 */
	this.processSerialization=function processSerialization()
	{
		//pointer.ctatdebug ("processSerialization()");

		// implement in child class
	};

	/**
	 * Interface Action: move
	 * @param {number} newX	the new x position.
	 * @param {number} newY	the new y position.
	 */
	this.move = function (newX,newY)
	{
		//pointer.ctatdebug ("move ("+newX+","+newY+")");

		x=newX;
		y=newY;

		//componentStyle.modifyCSSAttribute("left", this.x+"px");
		//componentStyle.modifyCSSAttribute("top", this.y+"px");
		divWrapper.style.left = x+'px';
		divWrapper.style.top = y+'px';
		//this.render ();
	};
	/**
	 * Ported from AS3
	 * @param w
	 * @param h
	 */
	this.setSize=function setSize(w,h)
	{
		pointer.ctatdebug ("setSize ("+w+","+h+")");

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
	{ // Interface Action
		pointer.ctatdebug ("SetVisible ("+aValue+")","graphics");

		this.setVisible (aValue);
	};

	/**
	 * Changes the visibility of the object. Ported from AS3
	 * @param aValue true/false visible or not.
	 */
	this.setVisible=function setVisible (aValue)
	{ // Interface Action
		if (aValue===true)
		{
			canvasVisible='block';
		}
		else
		{
			canvasVisible='hidden';
		}

		if (subCanvas)
			subCanvas.setAttribute("style", "border: 0px; position: absolute; visibility:"+canvasVisible+"; left:"+x+"px; top:"+y+"px; z-index:"+canvasZIndex+";");

		if (aValue===true)
		{
			divWrapper.style.visibility='block';
		}
		else
		{
			divWrapper.style.visibility='hidden';
		}
	};

	 this.setBorderWidth=function setBorderWidth(width)
	  {
	    borderWidth=width;
	    componentStyle.modifyCSSAttribute("border-width",borderWidth);

	    if(initialized==true)
	    {
	      this.render();
	    }
	  };

	  this.getBorderWidth=function getBorderWidth()
	  {
	    return (borderWidth);
	  };


	/**
	 * Interface Action: FadeIn
	 * TODO: impliment
	 * @param {number} fadeTime	Time in ms? to fade the component in.
	 */
	this.FadeIn = function (fadeTime) {
		pointer.SetVisible(true);
	};
	/**
	 * Interface Action: FadeOut
	 * TODO: impliment
	 * @param {number} fadeTime	Time in ms? to fade the component out.
	 */
	this.FadeOut = function (fadeTime) {
		pointer.SetVisible(false);
	};
	/**
	 *  Ported from AS3
	 *
	 * Does not work correctly for IE because disabled components have their own font color
	 */
	this.showCorrect=function showCorrect(aMessage)
	{
		pointer.ctatdebug("showCorrect("+correctColor+")");

		fontColor=correctColor;
		//showHintHighlight=false;
		pointer.setHintHighlight(false);
		componentStyle.removeStringCSS(globalGlowString);

		if (suppressStudentFeedback===false)
		{
			componentStyle.modifyCSSAttribute("color", correctColor);
		}

		if (suppressStudentFeedback===false)
		{
			if(disableOnCorrect=="true")
			{
				disabledTextColor=correctColor;
				pointer.setEnabled (false);

				if ((pointer.getClassName ()=="CTATTextArea") || (pointer.getClassName ()=="CTATTextInput") || (pointer.getClassName ()=="CTATTextField"))
				{
					pointer.setEditable (false);
				}
			}
		}

		if(initialized===true)
		{
			pointer.render ();
		}
	};
	/**
	 * Ported from AS3
	 */
	this.showInCorrect=function showInCorrect(aMessage)
	{
		pointer.ctatdebug("showInCorrect("+incorrectColor+")");

		fontColor=incorrectColor;

		if (suppressStudentFeedback===false)
		{
			componentStyle.modifyCSSAttribute("color", incorrectColor);
		}

		if(initialized===true)
		{
			pointer.ctatdebug ("Style: " + componentStyle.toCSSString());

			this.render ();
		}
		else
			pointer.ctatdebug ("Not initialized");

	};
	/**
	 * Ported from AS3
	 */
	this.setHintHighlight=function setHintHighlight(newValue,complex)
	{
		pointer.ctatdebug("setHintHighlight (" + newValue + ")");

		if(!showHintHighlight)
		{
			return;
		}

		highlighted = newValue;

		if(!complex)
		{
			if (newValue===true)
			{
				componentStyle.addStringCSS(globalGlowString);
			}
			else
			{
				componentStyle.removeStringCSS(globalGlowString);
			}

			if(initialized===true)
			{
				pointer.render ();
			}
		}
	};
	/**
	 * @function highlight
	 * An Interface Action for highlighting the component.
	 * @see CTATCompBase.setHingHighlight
	 */
	this.highlight = this.setHintHighlight.bind(pointer,true);
	/**
	 * @function unhighlight
	 * An Interface Action for removing highlighting.
	 * @see CTATCompBase.setHingHighlight
	 */
	this.unhighlight = this.setHintHighlight.bind(pointer,false);
	/**
	 * Implement in child object
	 * Interface Action
	 */
	this.reset=function reset ()
	{

	};
	/**
	 *
	 */
	this.setX=function setX (aX)
	{
		this.x=aX;
		//componentStyle.modifyCSSAttribute("left", x+"px");
		divWrapper.style.left = this.x+'px';

		if(initialized===true)
		{
			this.render ();
		}
	};

	/**
	 *
	 */
	this.setY=function setY (aY)
	{
		this.y=aY;

		//componentStyle.modifyCSSAttribute("top", y+"px");
		divWrapper.style.top = this.y+'px';

		if(initialized===true)
		{
			this.render ();
		}
	};

	/**
	 *
	 */
	this.setWidth=function setWidth (aWidth,complex)
	{
		width=aWidth;

		if(!complex)
		{
			componentStyle.modifyCSSAttribute("width", width+"px");

			if(initialized===true)
			{
				this.render ();
			}
		}
	};

	/**
	 *
	 */
	this.setHeight=function setHeight (aHeight,complex)
	{
		height=aHeight;

		if(!complex)
		{
			componentStyle.modifyCSSAttribute("height", height+"px");

			if(initialized===true)
			{
				this.render ();
			}
		}
	};

	/**
	 *
	 */
	this.setBackgroundColor=function setBackgroundColor (aColor,complex)
	{
		backgroundColor=aColor;
		console.log("setting background color to: " + aColor);
		if(!complex)
		{
			componentStyle.modifyCSSAttribute("background-color", backgroundColor);

			if(initialized===true)
			{
				this.render ();
			}
		}
	};

	/**
	 *
	 */
	this.setBorderColor=function setBorderColor (aColor)
	{
		console.log("setting border color to: " + aColor);

		borderColor=aColor;
		componentStyle.modifyCSSAttribute("border-color", borderColor);
		//componentStyle.modifyCSSAttribute("border-width", 1+"px");

		if(initialized==true)
		{
			this.render ();
		}
	};

	/**
	 *
	 */
	this.setBorderStyle=function setBorderStyle(aStyle)
	{
		borderStyle=aStyle;
		componentStyle.modifyCSSAttribute("border-style", borderStyle);
		componentStyle.modifyCSSAttribute("border-width", 1+"px");

		if(initialized===true)
		{
			this.render ();
		}
	};

	/**
	 *
	 */
	this.setFontColor=function setFontColor (aColor,complex)
	{
		// console.log(aColor);
		fontColor=aColor;

		if(!complex)
		{
			componentStyle.modifyCSSAttribute("color", fontColor);

			if(initialized===true)
			{
				//We want to set the font color. A component may use the font color for its inner
				//html, but if we have a label, then we need to set the label's font color.

				if(!label)
				{
					this.render ();
				}
				else
				{
					label.setAttribute('style', "color: "+fontColor);
				}
			}
		}
	};

	/**
	 *
	 */
	this.setFontFamily=function setFontFamily (aFont,complex)
	{
		fontFamily=aFont;

		if(!complex)
		{
			componentStyle.modifyCSSAttribute("font-family", fontFamily);

			if(initialized===true)
			{
				this.render ();
			}
		}
	};

	/**
	 *
	 */
	this.setFontSize=function setFontSize (aSize,complex)
	{
		fontSize=aSize;

		if(!complex)
		{
			componentStyle.modifyCSSAttribute("font-size", fontSize+"pt");

			if(initialized===true)
			{
				this.render ();
			}
		}
	};

	/**
	 *
	 */
	this.setShowBorder=function setShowBorder (aValue,complex)
	{
		showBorder=aValue;

		if(!complex)
		{
			if(showBorder===true)
			{
				componentStyle.modifyCSSAttribute("border-style", borderStyle);
				componentStyle.modifyCSSAttribute("border-width", 1+"px");
			}

			else
			{
				componentStyle.modifyCSSAttribute("border-style", "hidden");
			}

			if(initialized===true)
			{
				this.render ();
			}
		}
	};

	/**
	 *
	 */
	this.setAlign=function setAlign (anAlign)
	{
		align=anAlign;
		componentStyle.modifyCSSAttribute("text-align", align);

		if(initialized===true)
		{
			this.render ();
		}
	};

	/**
	 *
	 */
	this.setText=function setText(aText)
	{
		//Overrode in child objects
	};

	/**
	 *
	 */
	this.getValue=function getValue ()
	{
		if (component)
			return (component.value);

		return "";
	};

	/**
	 *
	 */
	this.setBolded=function(aBold, complex)
	{
		isBolded=aBold;

		if(!complex && isBolded=="true")
		{
			componentStyle.addCSSAttribute("font-weight", "bold");

			this.render ();
		}
	};

	/**
	 *
	 */
	this.getBolded=function()
	{
		return (isBolded);
	};

	/**
	 *
	 */
	this.setItalicized=function(aItalicized,complex)
	{
		isItalicized=aItalicized;

		if(!complex && isItalicized=="true")
		{
			componentStyle.addCSSAttribute("font-style", "italic");

			this.render ();
		}
	};

	/**
	 *
	 */
	this.getItalicized=function ()
	{
		return (isItalicized);
	};

	/**
	 *
	 */
	this.setUnderlined=function(aUnderlined,complex)
	{
		isUnderlined=aUnderlined;

		if(!complex && isUnderlined=='true')
		{
			componentStyle.addCSSAttribute("text-decoration", "underline");

			pointer.render ();
		}
	};

	/**
	 *
	 */
	this.getUnderlined=function()
	{
		return (isUnderlined);
	};

	/**
	 *
	 */
	this.setPadding=function(aPadding,complex)
	{
		padding=aPadding;

		if(!complex)
		{
			componentStyle.modifyCSSAttribute("padding", padding+"px");
			this.render();
		}
	};

	/**
	 *
	 */
	this.setBorderRoundness=function(aRadius,complex)
	{
		borderRadius=aRadius;

		if(!complex)
		{
			componentStyle.modifyCSSAttribute("moz-border-radius",borderRadius+"px");
			componentStyle.modifyCSSAttribute("border-radius",borderRadius+"px");
		}
	};

	/**
	 * This method is called when a component gets focused. So keep in mind that
	 * we want to grade the component that lost focus, or in other words the
	 * previously focused component.
	 */
	this.processFocus=function processFocus (e)
	{
		//useDebugging=true;

		pointer.ctatdebug ("processFocus ()");

		var id=e.currentTarget.getAttribute ("id");
		var comp=pointer.getComponentFromID (id);

		if (!comp)
		{
			pointer.ctatdebug ("Error: component reference is null");
			return;
		}

		if (mobileAPI)
		{
			if (mobileAPI.getEnabled ()===true)
			{
				pointer.hideKeyboard ();
			}
		}

		if (commShell)
		{
			commShell.processComponentFocus (comp);
		}

		if (oldComponentFocus)
		{
			pointer.ctatdebug ("old focus: " + oldComponentFocus.getName () + ", new focus:" + comp.getName ());

			if (oldComponentFocus==comp)
			{
				pointer.ctatdebug ("We're already there!");
				return;
			}

			if (commShell)
			{
				// Only backgrade text input components!

				if ((oldComponentFocus.getClassName ()=="CTATTextArea") || (oldComponentFocus.getClassName ()=="CTATTextInput"))
				{
					// commShell.gradeComponent (oldComponentFocus);
					//
					// oldComponentFocus.setHintHighlight(false);
				}
			}
		}
		else
			pointer.ctatdebug ("No previously focused component yet, can't grade");

		if ((comp.getName ()=="hint") || (comp.getName ()=="done"))
		{
			pointer.ctatdebug ("Info: focus moved to hint, bump");
			return;
		}
		else
		{
			oldComponentFocus=comp;
		}

		//useDebugging=true;
	};

	/**
	 *
	 */
	this.hideKeyboard=function hideKeyboard ()
	{
		document.activeElement.blur();
		$("input").blur();
	};
};

CTATCompBase.prototype = Object.create(CTATBase.prototype);
CTATCompBase.prototype.constructor = CTATCompBase;
