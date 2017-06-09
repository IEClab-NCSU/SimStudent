/**
 * @fileoverview Defines the base for all CTAT components.
 *
 * @author $Author: mdb91 $
 * @version $Revision: 24569 $
 */
/**-----------------------------------------------------------------------------
 $Date: 2017-01-31 10:28:17 -0600 (週二, 31 一月 2017) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATComponentHierarchy/CTATCompBase.js $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATCompBase');

goog.require('CTATBase');
goog.require('CTATCommShell');
goog.require('CTATComponentReference');
goog.require('CTATConfig');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATSandboxDriver');
/**
 * @param {String} aClassName	The type of CTAT component (eg) CTATButton
 * @param {String} aName	The instance name of the component
 * @param {Number} [aX=0]	The x coordinate of the component.
 * @param {Number} [aY=0]	The y coordinate of the component.
 * @param {Number} [aWidth=-2]	The width of the component.
 * @param {Number} [aHeight=-2]	The height of the component.
 */
CTATCompBase = function(aClassName,
						aName,
						aX,
						aY,
						aWidth,
						aHeight) {
	CTATBase.call (this, aClassName, aName);

	var x=aX || 0;
	var y=aY || 0;
	var width=aWidth || -2;
	var height=aHeight || -2;

	// Overwrite in each component, used by the online authoring tools when creating new components
	var defaultWidth=50;
	// Overwrite in each component, used by the online authoring tools when creating new components
	var defaultHeight=25;

	var abstractComponent=false;
	var isFeedbackComponent=false;
	var text="";
	var initialized=false;
	var enabled=true;
	var divWrapper=null;
	var componentGroup="";
	var tabIndex= null;
	this.isTabIndexable = true;
	var zIndex= 0;

	var component=null;
	//var label=null;
	var pointer=this;

	var subCanvas=null;
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
	this.isAbstractComponent=function() {return abstractComponent;};
	/**
	 *
	 */
	this.setIsFeedbackComponent=function (aValue)
	{
		isFeedbackComponent=aValue;
	};
	this.isFeedbackComponent=function() {return isFeedbackComponent;};
	/**
	 * Overwrite in each component, used by the online authoring tools when creating new components
	 */
	this.setTabIndex=function (aValue)
	{
		if (this.isTabIndexable===true) {
			tabIndex=aValue;

			if (tabIndex<0)
			{
				tabIndex=CTATGlobals.Tab.Tracker;

				CTATGlobals.Tab.Tracker++;
			}
			else
			{
				CTATGlobals.Tab.Tracker=tabIndex; // otherwise we might overwrite indexes
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
		return (tabIndex || this.getComponent().getAttribute('tabindex'));
	};

	/**
	 * Overwrite in each component, used by the online authoring tools when creating new components
	 */
	this.setZIndex=function setZIndex (aValue)
	{
		pointer.ctatdebug ("Setting z index to: " + aValue);

		zIndex=aValue;
		if (divWrapper!==null) {
			divWrapper.style.zIndex = aValue;
		}
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
	this.makeDivWrapper = function(topDiv) {
		divWrapper=document.createElement('div');
		//divWrapper.setAttribute('id', this.getName());
		divWrapper.id = this.getName();
		//divWrapper.setAttribute('name', this.getName());
		divWrapper.setAttribute('onkeypress', 'return noenter(event)');
		divWrapper.setAttribute('data-ctat-component',this.getClassName());
		divWrapper.style.position = 'absolute';
		divWrapper.style.left = x+'px';
		divWrapper.style.top = y+'px';
		divWrapper.style.zIndex = topDivZIndex;
		divWrapper.style.width = pointer.getWidth()+'px';
		divWrapper.style.height = pointer.getHeight()+'px';
		$(divWrapper).data('CTATComponent',this);
		//divWrapper.setAttribute('style', 'position: absolute;left:'+x+'px; top:'+y+'px; z-index: '+topDivZIndex+';width: '+pointer.getWidth()+'px;height: '+pointer.getHeight()+'px;');
		topDiv.appendChild(divWrapper);
	};
	this.setDivWrapper = function(aDiv) {
		divWrapper = aDiv;
		divWrapper.setAttribute('data-ctat-component',this.getClassName());
	};
	/**
	 *
	 */
	var super_setClassName = this.setClassName;
	this.setClassName = function(sClassName) {
		super_setClassName(sClassName);
		if (divWrapper) {
			divWrapper.setAttribute('data-ctat-component',this.getClassName());
		}
	};
	/**
	 *
	 * @param topDiv
	 */
	this.wrapComponent=function wrapComponent(topDiv)
	{
		pointer.ctatdebug ("wrapComponent ()");
		pointer.makeDivWrapper(topDiv);
		//pointer.ctatdebug ("Container div created, adding canvas ...");

		pointer.ctatdebug ("wrapComponent () done");
	};

	this.createCanvas=function()
	{
		pointer.ctatdebug ("createCanvas ()");

		subCanvas=document.createElement('canvas');
		subCanvas.setAttribute('id', canvasID);
		subCanvas.setAttribute('onkeypress', 'return noenter(event)');
		subCanvas.setAttribute('width', width+canvasCalibrate+"px");
		subCanvas.setAttribute('height', height+canvasCalibrate+"px");
		subCanvas.setAttribute("style","border: 1px "+this.borderColor+" solid; " +
				(canvasVisible=='hidden'?"visibility: hidden; ":'')+"z-index:"+canvasZIndex+";");

		if (divWrapper.firstChild) // make sure it is at the top.  Most likely the component.
			divWrapper.insertBefore(subCanvas, divWrapper.firstChild);
		else
			divWrapper.appendChild(subCanvas);

		return(subCanvas);
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

	/*this.getGrDescription=function getGrDescription()
	{
		return (grDescription);
	};*/

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
		if (width<=0) { // FIXME: just using derived height seems to cause odd behavior in generated components
			var cstyle;
			if (component)
				cstyle = window.getComputedStyle(component);
			else if (divWrapper)
				cstyle = window.getComputedStyle(divWrapper);

			if (cstyle)
				return parseFloat(cstyle.width); // cstyle.width returns num+'px'*/
		}
		return (width);
	};

	this.getHeight=function getHeight()
	{
		if (height<=0 && divWrapper) { // get calculated height on the div wrapper.
			var cstyle = window.getComputedStyle(divWrapper);
			//console.trace(cstyle.getPropertyValue('height'));
			//console.trace($(divWrapper).height()); // jquery method, not sure it will work if height not explicitly set.
			return parseFloat(cstyle.getPropertyValue('height'));
		}
		return (height);
	};

	this.getText=function getText()
	{
		return (text);
	};

	this.getEnabled=function getEnabled()
	{
		return (enabled);
	};

	//Needed because there is already a setEnabled method, which happens to be abstract...
	this.assignEnabled=function assignEnabled(aValue)
	{
		enabled=aValue;
		divWrapper.setAttribute('data-ctat-enabled',enabled);
	};

	this.setEnabled=function (aValue)
	{
		pointer.ctatdebug ("setEnabled ("+aValue+")");

		pointer.assignEnabled(aValue);

		if (!component)
		{
			pointer.ctatdebug ("Error: component pointer is null");
			return;
		}

		component.disabled=!enabled;
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

	/*this.getShowBorder=function getShowBorder()
	{
		return (showBorder);
	};*/

	this.getDivWrap=function getDivWrap()
	{
		return (divWrapper);
	};

	this.getComponent=function getComponent()
	{
		return (component);
	};

	/** @nocollapse */ this.component = null;
	Object.defineProperty(this,'component',{get: function() { return component; }});

	/*this.getLabel=function getLabel()
	{
		return (label);
	};*/

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

	this.setComponent=function setComponent(aComponent)
	{
		component=aComponent;
		//component.classList.add('CTAT-gen-component');
	    this.setEnabled(this.getEnabled()); // this will make sure to properly propagate the enabled setting
	};
	this.getTopDivZIndex=function(){
		return topDivZIndex;
	};
	this.getTopDivID=function(){
		return topDivID;
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

	/*this.setLabel=function setLabel(aLabel)
	{
		label=aLabel;
	};*/

	/*this.setdivWrapper=function setDivWrapper(aWrapping) // unused
	{
		divWrapper=aWrapping;
	};*/

	this.assignText=function assignText(aText)
	{
		text=aText;
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
	this.initialize = function()
	{
		pointer.ctatdebug ("initialize ()");

		pointer.configFromDescription();
		pointer.init();
		pointer.processSerialization();
		pointer.render();
		pointer.processTabOrder();

		pointer.ctatdebug ("initialize () done");
	};

	/**
	*
	*/
	this.getInitialized=function getInitialized()
	{
		return (initialized);
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
				var $ctatcontainer = $('#'+ctatcontainer);
				if ($ctatcontainer.length>0)
					pointer.wrapComponent(getSafeElementById(ctatcontainer));
				// FIXME: else do something if #container does not exist.
			}

		}
		else
		{
			pointer.ctatdebug ("Not wrapping, initialized=" + initialized + ", divWrapper = " + divWrapper);
		}

		pointer.getDivWrap().classList.add("CTATComponent");
		pointer.ctatdebug ("setInitialized () done");
		//useDebugging=false;
	};

	/**
	 *
	 */
	this.addSafeEventListener=function addSafeEventListener(aType, aFunction, aTarget)
	{
		pointer.ctatdebug ("addSafeEventListener ("+aType+")");

		pointer.ctatdebug ("Adding event listener to: " + aTarget);

		if (aTarget)
		{
			aTarget.addEventListener (aType,aFunction);
			return;
		}

		if (component)
		{
			pointer.ctatdebug ("Adding eventlistener to component instead of target");
			component.addEventListener(aType,aFunction);
		}
		else
		{
			pointer.ctatdebug ("Error: pointer to component is null, can't add event listener");
		}

		pointer.ctatdebug ("addSafeEventListener () done");
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

		//aComponent.tabIndex=this.getTabIndex ();
		CTATComponentReference.add(aComponent,aDiv);

		pointer.ctatdebug ("addComponentReference ()");
	};

	/**
	 *
	 */
	this.getComponentFromID = function getComponentFromID (anID)
	{
		return CTATComponentReference.getComponentFromID(anID);
		/*pointer.ctatdebug ("getComponentFromID ("+anID+") -> " + CTATGlobals.componentReferences.length + " references");

		for (var i=0;i<CTATGlobals.componentReferences.length;i++)
		{
			var ref=CTATGlobals.componentReferences [i];

			if (ref.getDiv ().getAttribute ("id")==anID)
			{
				if (!ref.getElement())
				{
					pointer.ctatdebug ("Error: found component reference is null! ("+ref.getElement()+")");
					return (null);
				}

				pointer.ctatdebug ("Found component reference: " + ref.getElement ().getName () + " for div: " + anID);

				return (ref.getElement ());
			}
		}

		pointer.ctatdebug ("Bottoming out ...");

		return (null);*/
	};


	/**
	 *
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.ctatdebug ("processSerialization()");

		pointer.ctatdebug ("implement in child class");
	};

	/**
	 * Interface Action: move
	 * @param {number} newX	the new x position.
	 * @param {number} newY	the new y position.
	 */
	this.move = function (newX,newY)
	{
		//pointer.ctatdebug ("move ("+newX+","+newY+")");

		x=Number(newX);
		y=Number(newY);
		
		if ((newY===undefined) || (newY===null) || isNaN(y)) {
			if (newX.indexOf(',')>=0) {
				var split = newX.split(',');
				x = Number(split[0]);
				y = Number(split[1]);
			} else {
				y = 0;
			}
		}
		x = isNaN(x)?0:x;
		y = isNaN(y)?0:y;

		//componentStyle.modifyCSSAttribute("left", this.x+"px");
		//componentStyle.modifyCSSAttribute("top", this.y+"px");
		if (divWrapper) {
			divWrapper.style.left = x+'px';
			divWrapper.style.top = y+'px';
		}
		//this.render ();
	};
	/**
	 * Ported from AS3
	 * @param w
	 * @param h
	 */
	this.setSize=function setSize(w,h)
	{
		var width = Number(w);
		var height = Number(h);
		
		if ((h===undefined) || (h===null) || isNaN(height)) {
			if (w.indexOf(',')>=0) {
				var split = w.split(',');
				width = Number(split[0]);
				height = Number(split[1]);
			}
		}
		
		pointer.ctatdebug ("setSize ("+width+","+height+")");

		if (!isNaN(width)) pointer.setWidth(width);
		if (!isNaN(height)) pointer.setHeight(height);
	};

	/**
	 * Changes the visibility of the object. Ported from AS3
	 * @param aValue true/false visible or not.
	 */
	this.setVisible=function setVisible (aValue)
	{ // Interface Action
		var vis = CTATGlobalFunctions.toBoolean(aValue);
		//console.trace(this.getName(),'setVisible',aValue,vis,vis===true);
		if (vis===true)
		{
			canvasVisible='block';
		}
		else
		{
			canvasVisible='hidden';
		}

		if (subCanvas)
			subCanvas.setAttribute("style", "border: 0px; position: absolute; "+(vis?'':"visibility:hidden; ")+"; left:"+x+"px; top:"+y+"px; z-index:"+canvasZIndex+";");

		if (vis===true)
		{
			//divWrapper.style.visibility=null;
			divWrapper.style.visibility='unset';
		}
		else
		{
			divWrapper.style.visibility='hidden';
		}
	};
	/**
	 * Changes the visibility of the object. Ported from AS3
	 * @param aValue true/false visible or not.
	 */
	this.SetVisible=this.setVisible;

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
	 * Implement in child object
	 * Interface Action
	 */
	this.reset=function reset ()
	{

	};
	/**
	 *
	 */
	this.setX=function setX (newX)
	{
		this.x=newX;
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
	this.setY=function setY (newY)
	{
		this.y=newY;

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
	this.setWidth=function setWidth (newWidth)
	{
		pointer.ctatdebug ("setWidth ("+newWidth+")");

		width=newWidth;

		if (divWrapper)
		{
			divWrapper.style.width = (width+"px");
		}
		else
		{
			pointer.ctatdebug ("Internal error: no div wrapper available to set width");
		}

		//this.render ();
	};

	/**
	 *
	 */
	this.setHeight=function setHeight (newHeight)
	{
		pointer.ctatdebug ("setHeight ("+newHeight+")");

		height=newHeight;

		if (divWrapper)
		{
			divWrapper.style.height = (height+"px");
		}
		else
		{
			pointer.ctatdebug ("Internal error: no div wrapper available to set height");
		}

		//this.render ();
	};

	/**
	 *
	 */
	this.setText=function setText(aText)
	{
		//Overrode in child objects
		text = aText;
		return this;
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

	this.backgrade = false; // set to true in components that should be back graded, (eg) CTATTextBasedComponents

	/**
	*
	*/
	this.unHighlightAll=function unHighlightAll ()
	{
		// Clear the hint highlights on all highlighted components.
		$('.CTAT--hint').each(function() {
			var $entity = $(this);
			var comp = null;
			// Walk up hierarchy until the CTATComponent reference is found.
			while ($entity && comp === null) {
				if ($entity.data('CTATComponent')) {
					comp = $entity.data('CTATComponent');
				} else {
					$entity = $entity.parent();
				}
			}

			if (comp && comp.setHintHighlight) {
				comp.setHintHighlight(false,null);
			}
		});
	};

	/**
	*
	*/
	this.processOnFocus=function processOnFocus ()
	{
		pointer.ctatdebug ("processOnFocus ("+pointer.getName ()+")");

		// Although sensible, removing all the highlights here was clashing with
		// the code that processes incoming highlight messages since those come
		// in before the focus result is processed.

		//pointer.unHighlightAll ();

		CTATGlobals.Tab.previousFocus=CTATGlobals.Tab.Focus;

		if (CTATGlobals.Tab.Focus!=pointer)
		{
			pointer.ctatdebug ("CTATGlobals.Tab.Focus!=pointer, updating CTATGlobals.Tab.Focus ...");

			CTATGlobals.Tab.Focus=pointer;
		}
		else
		{
			pointer.ctatdebug ("CTATGlobals.Tab.Focus==pointer");
		}

		pointer.ctatdebug ("processOnFocus () done");
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
		var comp = pointer;

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

		if (CTATCommShell.commShell)
		{
			CTATCommShell.commShell.processComponentFocus (comp);
		}

		if (CTATGlobals.Tab.Focus)
		{
			pointer.ctatdebug ("old focus: " + CTATGlobals.Tab.Focus.getName () + ", new focus:" + comp.getName () + ", correct: " + CTATGlobals.Tab.Focus.isCorrect ());

			if (CTATGlobals.Tab.Focus.isCorrect ()==true)
			{
				pointer.ctatdebug ("Previous focus is a correct component, don't backgrade!");
				return;
			}			
			
			if (CTATGlobals.Tab.Focus==comp)
			{
				pointer.ctatdebug ("We're already there!");
				return;
			}
			
			if (CTATGlobals.Tab.Focus.backgrade)
			{
				CTATGlobals.Tab.Focus.processAction();
			}
		}
		else
			pointer.ctatdebug ("No previously focused component yet, can't grade");

		if ((comp.getClassName ()=="hint") || // TODO: fix this, named hint and done components is probably not the best test
			 comp.getClassName()=="CTATHintButton")
		{
			pointer.ctatdebug ("Info: focus moved to hint, bump");
			return;
		}
		else
		{
			CTATGlobals.Tab.previousFocus=CTATGlobals.Tab.Focus;

			//if (CTATGlobals.Tab.previousFocus)
			//{
				//CTATGlobals.Tab.previousFocus.setHintHighlight (false,null,null);
			//}

			CTATGlobals.Tab.Focus=comp;
		}

		pointer.ctatdebug ("processFocus () done");
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
			if (tabIndex!==null) component.tabIndex=tabIndex;

			component.onfocus=pointer.processOnFocus;
		}
		else
		{
			pointer.ctatdebug ("Error: we don't have an html component yet, can't assign tab index");
		}
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
