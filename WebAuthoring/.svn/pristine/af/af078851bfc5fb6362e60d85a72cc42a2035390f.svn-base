/**-----------------------------------------------------------------------------
 $Author: vvelsen $
 $Date: 2015-02-12 14:14:28 -0500 (Thu, 12 Feb 2015) $
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

 http://mottie.github.io/tablesorter/docs/example-widget-resizable.html

 Quick edit to change the code so that SVN will pick up this revision
 */
goog.provide('CTATTutor');

goog.require('CTATAudioButton');
goog.require('CTATBinaryImages');
goog.require('CTATButton');
goog.require('CTATCheckBox');
goog.require('CTATComboBox');
goog.require('CTATCommLibrary');
goog.require('CTATCommShell');
goog.require('CTATConfig');
goog.require('CTATFlashVars');
goog.require('CTATFractionBar');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATGroupingComponent');
goog.require('CTATHintWindow');
goog.require('CTATHTMLManager');
goog.require('CTATImageButton');
goog.require('CTATJSON');
goog.require('CTATJumble');
goog.require('CTATMobileTutorHandler');
goog.require('CTATMovieClip');
goog.require('CTATNumberLine');
goog.require('CTATPieChart');
goog.require('CTATPlayButton');
goog.require('CTATRadioButton');
goog.require('CTATSAI');
goog.require('CTATSandboxDriver');
goog.require('CTATScrim');
goog.require('CTATScrollPaneComponent');
goog.require('CTATSkillSet');
goog.require('CTATSkillWindow');
goog.require('CTATTable');
goog.require('CTATTextArea');
goog.require('CTATTextField');
goog.require('CTATTextInput');
goog.require('CTATVideo');
goog.require('CTATXML');

var hintWindowWidth=350;
var hintWindowHeight=100;
var component=null;
var drawing=false;
var onMobile=false;

var ctatscrim=new CTATScrim();

var parser=null;

if (CTATConfig.parserType=="xml")
{
	//pointer.ctatdebug ("Creating XML parser ...");
	parser=new CTATXML ();
}
else
{
	//pointer.ctatdebug ("Creating JSON parser ...");
	parser=new CTATJSON ();
}

/**
 *
 */
function assignNameTranslator (aTranslator)
{
	nameTranslator=aTranslator;
}
/**
 *
 */
function clear ()
{
	ctatdebug ("clear ()");

	if (CTATConfig.platform=="ctat")
	{
		ctx.clearRect(0,0,canvasWidth,canvasHeight);
	}
}
/**
 *
 */
function drawTutor ()
{
	//ctatdebug ("drawTutor ()");

	if (drawing===true)
	{
		return;
	}

	drawing=true;

	/*
	if (commShell!=null)
	{
		var mHandler=commShell.getMessageHandler ();

		if (mHandler!=null)
		{
			mHandler.displayComponentList ();
		}
		else
			ctatdebug ("Error: can't get pointer to message handler");
	}
	 */

	clear ();

	for (var i=0;i<components.length;i++)
	{
		var aDesc=components [i];

		//ctatdebug ("Drawing component: " + i);

		var component=aDesc.getComponentPointer ();

		if (component!==null)
		{
			//ctatdebug ("Drawing component: " + aDesc.name);

			component.drawComponent ();
		}
	}
	drawing=false;
}
/**
 *
 */
function getComponentFromDescription (aDescription)
{
	var result=null;

	return (result);
}
/**
 *
 */
function getComponentDescriptionFromComponent (aComponent)
{
	var result=null;

	return (result);
}
/**
 *
 */
function addComponent (aComponent)
{
	ctatdebug ("addComponent ()");

	// We should already have the component description in the list
	// and a pointer to the component is already assigned to the
	// description in the component's constructor

	aComponent.init ();

	aComponent.processSerialization ();

	aComponent.render ();

	aComponent.processTabOrder ();
}
/**
 *
 */
function addTextInput (anX,anY,aWidth,aHeight)
{
	ctatdebug ("addTextInput ("+anX+","+anY+")");

	var textBox=new CTATTextField (canvas,ctx,anX,anY,aWidth,aHeight);
	textBox.init ();
}
/**
 *
 */
function createTextComponent (anX,anY,aWidth,aHeight)
{
	var inp=new CTATTextField (canvas,ctx,null,anX,anY,aWidth,aHeight);
	inp.fontFamily="Arial";
	inp.fontSize=20;
	inp.showBorder=true;
	inp.enabled=true;
	inp.align='center';

	addComponent (inp);

	return (inp);
}

/**
 *
 */
function initialize()
{
	var useragent = navigator.userAgent.toLowerCase();

	//alert ("User-agent header sent: " + navigator.userAgent);

	if (useragent.search("iphone")>0)
	{
		//alert ('ipone');
		onMobile=true;
	}
	else if (useragent.search("ipod")>0)
	{
		//alert ('ipod');
		onMobile=true;
	}
	else if (useragent.search("android")>0)
	{
		//alert ('android');
		onMobile=true;
	}

	//alert ("onMobile: " + onMobile);

	var isMobile=getSafeElementById("pageor");
	if (isMobile!==null)
	{
		if (onMobile===false)
		{
			//alert ('hiding page orientation ...');

			isMobile.style.display='none';
		}
	}

	ctatdebug ("initialize ()");

	commShell=new CTATCommShell ();
	commShell.init (this);

	canvasWidth=ctatcanvas.width;
	canvasHeight=ctatcanvas.height;

	ctatdebug ("Canvas: " +canvasWidth + "," + canvasHeight);
}

/**
 *
 */
this.createInterface=function createInterface ()
{
	ctatdebug ("createInterface ("+components.length+")");

	// First rebuild the basic interface from the serialized interface

	var decString="";
	var x=0;
	var y=0;
	var width=50;
	var height=50;
	var instName="";

	if (interfaceElement)
	{
		ctatdebug ("Re-creating interface ...");

		//var intProps=interfaceElement.childNodes;
		var intProps=parser.getElementChildren (interfaceElement);

		//useDebugging=true;

		this.createStaticInterface (null,intProps,null);

		//useDebugging=false;
	}

	// Next create all the CTAT components ...

	for (var i=0;i<components.length;i++)
	{
		var aDesc=components [i];
		//ctatdebug(aDesc);
		if (!aDesc)
		{
			alert ("Internal error parsing component at index " + i);
			return;
		}

		if (!aDesc.name)
		{
			alert ("Internal error parsing component at index " + i + " (no name attribute available)");
			return;
		}

		if (aDesc.name.indexOf ("null.")==-1)
		{
			ctatdebug ("Component: " + aDesc.name + ", type: " + aDesc.type);

			if (aDesc.type=="CTATCommShell")
			{
				if (commShell)
				{
					commShell.setName (aDesc.name);
				}

				if (aDesc.type=="CTATCommShell")
				{
					ctatdebug ("Tutor dimensions: " + aDesc.width + "x" + aDesc.height);

					//>---------------------------------------------------------------

					var tutorCanvas=getSafeElementById("main-canvas");

					if (tutorCanvas!==null)
					{
						ctatdebug ("Setting canvas dimensions from: " + tutorCanvas.width+"px, "+ tutorCanvas.height+"px, to: " + aDesc.width+"px, "+ aDesc.height+"px");

						tutorCanvas.width=aDesc.width;
						tutorCanvas.height=aDesc.height;

						tutorCanvas.style.width=aDesc.width;
						tutorCanvas.style.height=aDesc.height;

						ctatdebug ("Canvas dimensions now: " + tutorCanvas.width+"px, "+ tutorCanvas.height+"px");

						centerTutor(aDesc.width, aDesc.height);
						ctatscrim.resizeScrim(aDesc.width, aDesc.height);
					}
					else
						ctatdebug ("Error: tutor canvas is null, can't adjust size");

					//>---------------------------------------------------------------

					var tutorContainer=getSafeElementById("container");

					if (tutorContainer!==null)
					{
						tutorContainer.style.width=(aDesc.width+"px");
						tutorContainer.style.height=(aDesc.height+"px");
					}
					else
						ctatdebug ("Error: tutor container is null, can't adjust size");

					//>---------------------------------------------------------------
				}
			} else /*if (aDesc.type=="CTATSkillWindow") {
				ctatdebug ("Creating (CTATSkillWindow): " + aDesc.name);

				var skillWindow=new CTATSkillWindow (aDesc,
						aDesc.x,
						aDesc.y,
						aDesc.width,
						aDesc.height);
				skillWindow.setName (aDesc.name);

				aDesc.setComponentPointer (skillWindow);

				addComponent (skillWindow);
			}*/

			/*if (aDesc.type=="CTATHintWindow")
			{
				ctatdebug ("Creating (CTATHintWindow): " + aDesc.name);

				var hintWindow=new CTATHintWindow (aDesc,
						aDesc.x,
						aDesc.y,
						aDesc.width,
						aDesc.height);
				hintWindow.setName (aDesc.name);

				aDesc.setComponentPointer (hintWindow);

				addComponent (hintWindow);
				//ctatdebug(hintWindow.getDivWrap());
			}*/

			if (aDesc.type=="CTATHintButton")
			{
				ctatdebug ("Creating (CTATHintButton):" + aDesc.name);

				var hint=new CTATImageButton (aDesc,
						aDesc.x,
						aDesc.y,
						aDesc.width,
						aDesc.height);
				hint.setName ("hint");
				hint.setClassName ("CTATHintButton");
				hint.assignImages (hintDefault,hintHover,hintClick,hintDisabled);
				hint.setTabIndex (aDesc.tabIndex);

				aDesc.setComponentPointer (hint);

				addComponent (hint);

				// hint.addSafeEventListener ('click',hint.processClick,null);
			} else if (aDesc.type=="CTATDoneButton") {
				ctatdebug ("Creating (CTATDoneButton):" + aDesc.name);

				var done=new CTATImageButton (aDesc,
						aDesc.x,
						aDesc.y,
						aDesc.width,
						aDesc.height);
				done.setName("done");
				done.setClassName ("CTATDoneButton");
				done.assignImages (doneDefault,doneHover,doneClick,doneDisabled);
				done.setTabIndex (aDesc.tabIndex);

				aDesc.setComponentPointer (done);

				addComponent (done);

				// done.addSafeEventListener ('click',done.processClick,done);
			} else if(CTAT.ComponentRegistry.hasOwnProperty(aDesc.type)) {
				ctatdebug('Creating ('+aDesc.type+') :'+aDesc.name);
				var regComp = new CTAT.ComponentRegistry[aDesc.type] (aDesc,
						aDesc.x, aDesc.y, aDesc.width, aDesc.height);
				regComp.setName(aDesc.name);
				if ((aDesc.type=="CTATTextInput") || (aDesc.type=="CTATTextArea")) {
					// apparently text components need an explicit enable
					regComp.setEnabled (true);
				}
				// excluded from tab order by setting regComp.isTabIndexable = false
				// The default value is set in the component definitions.
				regComp.setTabIndex(aDesc.tabIndex);
				aDesc.setComponentPointer(regComp);
				addComponent(regComp);
				if ((aDesc.type=="CTATRadioButton") || (aDesc.type=="CTATCheckBox")) {
					// reset label placement.
					regComp.setLabelPlacement(regComp.getLabelPlacement());
				}
				ctatdebug(regComp.getDivWrap());
			} else {
				ctatdebug('ERROR: Unrecognized component type '+aDesc.type+' for '+aDesc.name);
			}

			/*if (aDesc.type=="CTATSubmitButton")
			{
				//useDebugging=true;

				ctatdebug ("Creating (CTATButton):" + aDesc.name);

				var buttonTest=new CTATSubmitButton (aDesc,
											   aDesc.x,
											   aDesc.y,
											   aDesc.width,
											   aDesc.height);
				buttonTest.setName(aDesc.name);
				buttonTest.setTabIndex (aDesc.tabIndex);

				aDesc.setComponentPointer (buttonTest);

				addComponent (buttonTest);

				ctatdebug(buttonTest.getDivWrap());
			}*/

		}
	}

	this.postProcess ();

	this.drawTutor ();

	ctatscrim.scrimDown ();
};
/**
 *
 */
this.postProcess=function postProcess ()
{
	ctatdebug ("postProcess ()");

	//>----------------------------------------------------------------------------------

	for (var i=0;i<components.length;i++)
	{
		var ref=components [i];

		//ctatdebug ("Obtaining component for " + ref.name + " with type: " + ref.type);

		var component=ref.getComponentPointer ();

		if (component!==null)
		{
			//>---------------------------------------------------------------------------------

			if (component.getClassName ()=="CTATTable")
			{
				component.adjustTableContents ();
			}

			//>---------------------------------------------------------------------------------

			if (
					(component.getClassName ()=="CTATScrollPaneComponent") ||
					(component.getClassName ()=="CTATComponentContainerReference") ||
					(component.getClassName ()=="CTATGroupingComponent")
			)
			{
				//useDebugging=true;
				component.postProcess ();
				//useDebugging=false;
			}
			//>---------------------------------------------------------------------------------
		}
		else
		{
			ctatdebug ("Error: component pointer "+i+" is null");
		}
	}
		var a = commMessageBuilder.createInterfaceAttributesMessage (version);

		var vars = flashVars.getRawFlashVars();
		commLibrary.setHandler(commMessageHandler);
		commLibrary.send_post(vars["remoteSocketURL"],a);//plugged in to servlet instead
	//>----------------------------------------------------------------------------------
};
/**
 *
 */
this.createStaticInterface=function createStaticInterface (aParent,intProps,aMovieClip)
{
	ctatdebug ("createStaticInterface ()");

	var parent=getSafeElementById("container");

	if (aParent!==null)
	{
		parent=aParent;
	}

	for (var t=0;t<intProps.length;t++)
	{
		var intNode=intProps [t];

		//ctatdebug (intNode.nodeName);
		ctatdebug (parser.getElementName (intNode));

		if (parser.getElementName (intNode)=="timeline")
		{
			ctatdebug ("Timeline node found, obtaining visual elements ...");

			var interf=parser.getElementChildren (intNode);

			this.createStaticInterface (null,interf,null);

			return; // we know there's nothing more in there here
		}


		if (parser.getElementName (intNode)=="ctatcomponent")
		{
			//var inst=intNode.attributes.getNamedItem("instance").value;
			var inst=parser.getElementAttr ("instance");

			if (aMovieClip)
			{
				ctatdebug ("Registering existence of CTAT component on MovieClip container: " + inst);

				aMovieClip.addComponent (inst);
			}
			//else
			//	ctatdebug ("Internal error: no MovieClip object available to attach CTAT component reference to");
		}

		//>--------------------------------------------------------------------

		var x,y;
		var width,height;
		var instName,descString;
		//if (intNode.nodeName=="shape")
		if (parser.getElementName (intNode)=="shape")
		{
			useDebugging=true;

			/*
			var x=intNode.attributes.getNamedItem("x").value;
			var y=intNode.attributes.getNamedItem("y").value;
			var width=intNode.attributes.getNamedItem("width").value;
			var height=intNode.attributes.getNamedItem("height").value;
			var instName=intNode.attributes.getNamedItem("instance").value;
			 */

			x=parser.getElementAttr (intNode,"x");
			y=parser.getElementAttr (intNode,"y");
			width=parser.getElementAttr (intNode,"width");
			height=parser.getElementAttr (intNode,"height");
			instName=parser.getElementAttr (intNode,"instance");

			ctatdebug ("Creating shape: " + instName + " at: " + x +","+ y+","+width+","+height);

			descString=("data:image/png;base64, "+parser.getNodeTextValue (intNode));

			var imgA=new Image();

			imgA.setAttribute("style", "position: absolute; top: " + y + "px; left:" + x + "px; z-index:"+CTATGlobalFunctions.gensym.z_index()+";");
			imgA.setAttribute('id',instName);
			imgA.setAttribute('src',descString);

			parent.appendChild(imgA);

			//currentZIndex++;

			useDebugging=false;
		}

		//>--------------------------------------------------------------------

		//if (intNode.nodeName=="statictext")
		if (parser.getElementName (intNode)=="statictext")
		{
			/*
			x=intNode.attributes.getNamedItem("x").value;
			y=intNode.attributes.getNamedItem("y").value;
			width=intNode.attributes.getNamedItem("width").value;
			height=intNode.attributes.getNamedItem("height").value;
			instName=intNode.attributes.getNamedItem("instance").value;
			 */

			x=parser.getElementAttr (intNode,"x");
			y=parser.getElementAttr (intNode,"y");
			width=parser.getElementAttr (intNode,"width");
			height=parser.getElementAttr (intNode,"height");
			instName=parser.getElementAttr (intNode,"instance");

			ctatdebug ("Creating static text: " + instName + " at: " + x +","+ y+","+width+","+height);

			descString=("data:image/png;base64, "+parser.getNodeTextValue (intNode));

			var imgB=new Image();

			imgB.setAttribute("style", "position: absolute; top: " + y + "px; left:" + x + "px; z-index:"+CTATGlobalFunctions.gensym.z_index()+";");
			imgB.setAttribute('id',instName);
			imgB.setAttribute('src',descString);

			parent.appendChild(imgB);

			//currentZIndex++;
		}

		//>--------------------------------------------------------------------

		//if (intNode.nodeName=="movieclip")
		if (parser.getElementName (intNode)=="statictext")
		{
			//instName=intNode.attributes.getNamedItem("instance").value;
			instName=parser.getElementAttr ("instance");

			ctatdebug ("Creating movieclip: " + instName);

			/*
			var aX=intNode.attributes.getNamedItem("x").value;
			var aY=intNode.attributes.getNamedItem("y").value;
			var aWidth=intNode.attributes.getNamedItem("width").value;
			var aHeight=intNode.attributes.getNamedItem("height").value;
			 */

			var aX=parser.getElementAttr (intNode,"x");
			var aY=parser.getElementAttr (intNode,"y");
			var aWidth=parser.getElementAttr (intNode,"width");
			var aHeight=parser.getElementAttr (intNode,"height");

			var aClip=new CTATMovieClip (instName,aX,aY,aWidth,aHeight);

			var newParent=aClip.wrapComponent (parent);

			ctatdebug ("Created movieclip: " + instName + " at: " + aClip.x +","+ aClip.y+","+aClip.width+","+aClip.height);

			movieclips.push (aClip);

			//var subProps=intNode.childNodes;
			var subProps=parser.getElementChildren (intNode);

			this.createStaticInterface (newParent,subProps,aClip);
		}

		//>--------------------------------------------------------------------
	}
};

/**
 *
 */
function runTutor (aVars)
{
	ctatdebug ("runTutor ()");

	if (CTATGlobals.tutorRunning===true)
	{
		ctatdebug ("The tutor is already running");
		return;
	}

	skillSet=new CTATSkillSet ();
	flashVars=new CTATFlashVars ();
	flashVars.assignRawFlashVars(aVars);

	window.onerror = function(errorMsg, url, lineNumber)
	{
		var formatter=new CTATHTMLManager ();

		ctatdebug(formatter.htmlEncode (errorMsg) + " in " + url + ", line " + lineNumber);
	};

	ctatcanvas = getSafeElementById("main-canvas");

	if (ctatcanvas===null)
	{
		alert ("Internal error: HTML5 canvas is null");
		return;
	}

	ctx = ctatcanvas.getContext('2d');

	if (!ctx)
	{
		alert ("Internal error: HTML5 canvas context is null");
		return;
	}

	initialize();

	ctatdebug ("runTutor () ... all set");

	CTATGlobals.tutorRunning=true;
}

/**
 *
 */
function initTutor ()
{
	ctatdebug ("initTutor ()");

	ctatscrim.waitScrimUp();

	var debugtraces=getSafeElementById("debugtraces");

	if (debugtraces!==null)
	{
		if (debugtraces.checked===true)
			useDebugging=true;
		else
			useDebugging=false;
	}

	var tempFlashVars=tutorPrep (FlashVars);

	mobileAPI=new CTATMobileTutorHandler ("keyboardUI",'disabled');

	ctatdebug ("initTutor ()");

	if (tempFlashVars ["session_id"]=="none")
		tempFlashVars ["session_id"]=("qa-test_"+guid());

	/*
	if (tempFlashVars ["connection"]=="javascript")
	{
		deployJava.runApplet({id: 'TSApplet', width: 150, height: 10}, {jnlp_href: '/ctat_applet/TSApplet.jnlp'}, '1.6');
	}
	 */

	runTutor (tempFlashVars);
}

/**
 * This is a function accessible by TutorShop
 */
function receiveFromTutor ()
{
	ctatdebug ("receiveFromTutor ()");
}

/**
 * This is a function accessible by TutorShop and is used to do a rapid
 * shutdown of a tutor whilst at the same time saving its state.
 */
function saveAndQuit ()
{
	ctatdebug ("saveAndQuit ()");
}

/**
 *
 */
function prepTutorArea ()
{
	//The first thing we will do is apply centering styles to the tutor
	centerTutor(600, 200);

	initTutor ();
}

window['prepTutorArea'] = prepTutorArea;

/**
 *
 */
function assignAnonymousGradingProcessor (aFunction)
{
	commShell.assignAnonymousGradingProcessor (aFunction);
}
/**
 *
 */
function gradeAnonymousComponent (aSelection,anAction,anInput)
{
	ctatdebug ("gradeAnonymousComponent ()");

	var tsMessage=new CTATSAI(aSelection,anAction,anInput);
	commShell.processComponentAction(tsMessage);
}

/**
 *
 */
function testTutor(aVars)
{
	ctatdebug ("testTutor ()");

	window.onerror = function(errorMsg, url, lineNumber)
	{
		//ctatdebugPointer = new CTATBase("", "");

		var formatter=new CTATHTMLManager ();

		//useDebugging=true; // This should always go through
		ctatdebug(formatter.htmlEncode (errorMsg) + " in " + url + ", line " + lineNumber);
		//useDebugging=false;
	};

	var internalFlashVars=tutorPrep (aVars);

	skillSet=new CTATSkillSet ();
	flashVars=new CTATFlashVars ();
	flashVars.assignRawFlashVars(aVars);

	var connector=new CTATCommLibrary ();
	//connector.send_post ("http://qa.pact.cs.cmu.edu/courses",'<?xml version=\"1.0\" encoding=\"UTF-8\"?><hello></hello>');
	connector.send ("http://augustus.pslc.cs.cmu.edu/crossdomain.xml");
}
