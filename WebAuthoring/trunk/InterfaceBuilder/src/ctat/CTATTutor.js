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
 
 http://mottie.github.io/tablesorter/docs/example-widget-resizable.html
 
*/

var hintWindowWidth=350;
var hintWindowHeight=100;
var component=null;
var drawing=false;
var onMobile=false;

var ctatscrim=new CTATScrim();
var parser=new CTATXML ();

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
	debug ("clear ()");
				
	if (platform=="ctat")
	{	
		ctx.clearRect(0,0,canvasWidth,canvasHeight);
	}	
}
/**
 * 
 */
function drawTutor ()
{
	//debug ("drawTutor ()");
	
	if (drawing==true)
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
			debug ("Error: can't get pointer to message handler");
	}
	*/	
	
	clear ();
		
	for (var i=0;i<components.length;i++)
	{
		var aDesc=components [i];
		
		//debug ("Drawing component: " + i);
		
		var component=aDesc.getComponentPointer ();
		
		if (component!=null)
		{
			//debug ("Drawing component: " + aDesc.name);
			
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
	debug ("addComponent ()");
	
	// We should already have the component description in the list
	// and a pointer to the component is already assigned to the
	// description in the component's constructor

	aComponent.init ();
	aComponent.processSerialization ();
	aComponent.render ();
}
/**
 * 
 */
function addTextInput (anX,anY,aWidth,aHeight)
{
	debug ("addTextInput ("+anX+","+anY+")");
	
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
	
	addComponent (inp4);
	
	return (inp);
}
/**
*
*/
function createSidebarInterface (aCanvasWidth,aCanvasHeight)
{
	debug ("createSidebarInterface ("+aCanvasWidth+","+aCanvasHeight+")");
	
	var tutorWidth=aCanvasWidth-4; // assume a 2 pixel margin
	var tutorHeight=aCanvasHeight-4; // assume a 2 pixel margin
	
	var dummyDescription=new Object ();
	
	//>------------------------------------------------------
	
	debug ("Creating (CTATSkillWindow)...");
	
	dummyDescription.name="skillwindow";
	dummyDescription.type="CTATSkillWindow";
	dummyDescription.x=5;
	dummyDescription.y=5;
	dummyDescription.width=tutorWidth-10;
	dummyDescription.height=100;
	
	var skillWindow=new CTATSkillWindow (dummyDescription,5,5,tutorWidth-10,100);
	skillWindow.setName ("skillwindow");
		
	addComponent (skillWindow);
		
	//>------------------------------------------------------

	debug ("Creating (CTATHintButton)...");
	
	dummyDescription.name="hint";
	dummyDescription.type="CTATHintButton";
	dummyDescription.x=5;
	dummyDescription.y=tutorHeight-61-5;
	dummyDescription.width=61;
	dummyDescription.height=61;	
	
	var hint=new CTATImageButton (dummyDescription,5,tutorHeight-61,61,61);
	hint.setName ("hint");
	hint.setClassName ("CTATHintButton");
	//hint.assignImages (hintDefault,hintHover,hintClick,hintDisabled);
	
	hint.assignImages ("https://qa.pact.cs.cmu.edu/images/skindata/Hint-Default.png",
					   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Hover.png",
					   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Click.png",
					   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Disabled.png");
		
	addComponent (hint);

	//pointer.addSafeEventListener ('click',hint.processClick,hint);

	//>------------------------------------------------------

	debug ("Creating (CTATDoneButton)...");

	dummyDescription.name="done";
	dummyDescription.type="CTATDoneButton";
	dummyDescription.x=tutorWidth-61-5;
	dummyDescription.y=tutorHeight-61-5;
	dummyDescription.width=61;
	dummyDescription.height=61;		
	
	var done=new CTATImageButton (dummyDescription,tutorWidth-61,tutorHeight-61,61,61);
	done.setName("done");
	done.setClassName ("CTATDoneButton");
	//done.assignImages (doneDefault,doneHover,doneClick,doneDisabled);
	done.assignImages ("https://qa.pact.cs.cmu.edu/images/skindata/Done-Default.png",
					   "https://qa.pact.cs.cmu.edu/images/skindata/Done-Hover.png",
					   "https://qa.pact.cs.cmu.edu/images/skindata/Done-Click.png",
					   "https://qa.pact.cs.cmu.edu/images/skindata/Done-Disabled.png");
		
	addComponent (done);			

	//pointer.addSafeEventListener ('click',done.processClick,done);

	//>------------------------------------------------------
	
	debug ("Creating (CTATHintWindow)...");
	
	dummyDescription.name="done";
	dummyDescription.type="CTATDoneButton";
	dummyDescription.x=5;
	dummyDescription.y=115;
	dummyDescription.width=tutorWidth-10;
	dummyDescription.height=tutorHeight-61-25-100;		
		
	var hintWindow=new CTATHintWindow (dummyDescription,5,115,tutorWidth-10,tutorHeight-61-25-100);
	hintWindow.setName ("hintwindow");
		
	addComponent (hintWindow);	
	
	//>------------------------------------------------------			
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
	if (isMobile!=null)
	{
		if (onMobile==false)
		{
			//alert ('hiding page orientation ...');
			
			isMobile.style.display='none';
		}
	}
	
	debug ("initialize ()");
	
	commShell=new CTATCommShell ();
	commShell.init (this);

	canvasWidth=ctatcanvas.width;
	canvasHeight=ctatcanvas.height;
	
	debug ("Canvas: " +canvasWidth + "," + canvasHeight);		
}
/**
*
*/	
function initializeSidebar() 
{					
	debug ("initializeSidebar ()");
	
	commShell=new CTATCommShell ();
	commShell.init (this);	
	
	if (platform=="ctat")
	{
		// Create our own version of the sidebar
		
		canvasWidth=ctatcanvas.width;
		canvasHeight=ctatcanvas.height;
	
		debug ("Canvas: " +canvasWidth + "," + canvasHeight);
				
		createSidebarInterface (canvasWidth,canvasHeight);
	}
	else
	{
		// Create the Google sidebar
	}		
}
/**
 * 
 */
this.createInterface=function createInterface ()
{
	debug ("createInterface ("+components.length+")");
	
	// First rebuild the basic interface from the serialized interface
		
	var decString="";
	var x=0;
	var y=0;
	var width=50;
	var height=50;
	var instName="";
		
	if (interfaceElement!=null)
	{
		debug ("Re-creating interface ...");
		
		var intProps=interfaceElement.childNodes;
		
		//useDebugging=true;
		
		this.createStaticInterface (null,intProps,null);				
		
		//useDebugging=false;
	}
		
	// Next create all the CTAT components ... 
	
	for (var i=0;i<components.length;i++)
	{
		var aDesc=components [i];

		if (aDesc==null)
		{
			alert ("Internal error parsing component at index " + i);
			return;
		}
		
		if (aDesc.name==null)
		{
			alert ("Internal error parsing component at index " + i + " (no name attribute available)");
			return;
		}
		
		if (aDesc.name.indexOf ("null.")==-1)
		{
			debug ("Component: " + aDesc.name + ", type: " + aDesc.type);

			if (aDesc.type=="CTATCommShell")
			{
				if (commShell!=null)
				{
					commShell.setName (aDesc.name);
				}
				
				if (aDesc.type=="CTATCommShell")
				{
					debug ("Tutor dimensions: " + aDesc.width + "x" + aDesc.height);
					
					//>---------------------------------------------------------------
					
					var tutorCanvas=getSafeElementById("main-canvas");
					
					if (tutorCanvas!=null)
					{
						debug ("Setting canvas dimensions from: " + tutorCanvas.width+"px, "+ tutorCanvas.height+"px, to: " + aDesc.width+"px, "+ aDesc.height+"px");

						tutorCanvas.width=aDesc.width;
						tutorCanvas.height=aDesc.height;

						tutorCanvas.style.width=aDesc.width;
						tutorCanvas.style.height=aDesc.height;
										
						debug ("Canvas dimensions now: " + tutorCanvas.width+"px, "+ tutorCanvas.height+"px");

						centerTutor(aDesc.width, aDesc.height);
						ctatscrim.resizeScrim(aDesc.width, aDesc.height);
					}
					else
						debug ("Error: tutor canvas is null, can't adjust size");
					
					//>---------------------------------------------------------------				
					
					var tutorContainer=getSafeElementById("container");
					
					if (tutorContainer!=null)
					{
						tutorContainer.style.width=(aDesc.width+"px");
						tutorContainer.style.height=(aDesc.height+"px");
					}
					else
						debug ("Error: tutor container is null, can't adjust size");
					
				//>---------------------------------------------------------------				
				}
			}

			if ((aDesc.type=="CTATTextInput") || (aDesc.type=="CTATTextArea")) // we'll use the same code for now
			{
				debug ("Creating: " + aDesc.name);
								
				var comp=new CTATTextInput (aDesc,
											aDesc.x,
											aDesc.y,
											aDesc.width,
											aDesc.height);
				comp.setName (aDesc.name);
				comp.setEnabled (true);
				
				aDesc.setComponentPointer (comp);
				
				addComponent (comp);				
			}
			
			if (aDesc.type=="CTATButton")								
			{
				//useDebugging=true;
				
				debug ("Creating (CTATButton):" + aDesc.name);
			
				var buttonTest=new CTATButton (aDesc,
											   aDesc.x,
											   aDesc.y,
											   aDesc.width,
											   aDesc.height);
				buttonTest.setName(aDesc.name);
				
				aDesc.setComponentPointer (buttonTest);
				
				addComponent (buttonTest);
				
				//useDebugging=false;
			}	
		
			if (aDesc.type=="CTATImageButton")
			{
				debug ("Creating (CTATImageButton):" + aDesc.name);
				
				/*
				var imageButton=new CTATImageButton (canvas,ctx,aDesc,aDesc.x,aDesc.y,aDesc.width,aDesc.height);
				imageButton.setName ("imageButton");
				imageButton.assignImages ('skindata/Hint-Default.png','skindata/Hint-Hover.png','skindata/Hint-Click.png','skindata/Hint-Disabled.png');														
				addComponent (hint);
				*/			
			}
		
			if (aDesc.type=="CTATSkillWindow")
			{								
				debug ("Creating (CTATSkillWindow): " + aDesc.name);
				
				var skillWindow=new CTATSkillWindow (aDesc,
													 aDesc.x,
													 aDesc.y,
													 aDesc.width,
													 aDesc.height);
				skillWindow.setName (aDesc.name);
				
				aDesc.setComponentPointer (skillWindow);
				
				addComponent (skillWindow);			
			}
		
			if (aDesc.type=="CTATHintWindow")
			{								
				debug ("Creating (CTATHintWindow): " + aDesc.name);
				
				var hintWindow=new CTATHintWindow (aDesc,
												   aDesc.x,
												   aDesc.y,
												   aDesc.width,
												   aDesc.height);
				hintWindow.setName (aDesc.name);
				
				aDesc.setComponentPointer (hintWindow);
				
				addComponent (hintWindow);
			}
		
			if (aDesc.type=="CTATHintButton")
			{
				debug ("Creating (CTATHintButton):" + aDesc.name);
				
				var hint=new CTATImageButton (aDesc,
											  aDesc.x,
											  aDesc.y,
											  aDesc.width,
											  aDesc.height);
				hint.setName ("hint");
				hint.setClassName ("CTATHintButton");
				hint.assignImages (hintDefault,hintHover,hintClick,hintDisabled);
				
				aDesc.setComponentPointer (hint);
				
				addComponent (hint);

				hint.addSafeEventListener ('click',hint.processClick,null);
			}
		
			if (aDesc.type=="CTATDoneButton")
			{
				debug ("Creating (CTATDoneButton):" + aDesc.name);

				var done=new CTATImageButton (aDesc,
											  aDesc.x,
											  aDesc.y,
											  aDesc.width,
											  aDesc.height);
				done.setName("done");
				done.setClassName ("CTATDoneButton");
				done.assignImages (doneDefault,doneHover,doneClick,doneDisabled);
				
				aDesc.setComponentPointer (done);
				
				addComponent (done);			

				hint.addSafeEventListener ('click',done.processClick,done);
			}						
			
			if (aDesc.type=="CTATAudioButton")
			{
				debug ("Creating (CTATAudioButton):" + aDesc.name);
				
				var audioButton=new CTATAudioButton (aDesc,
													 aDesc.x,
													 aDesc.y,
													 aDesc.width,
													 aDesc.height);
				audioButton.setName (aDesc.name);								
				audioButton.assignImages('/skindata/audio-default.png',null, '/skindata/audio-hover.png', null);
				
				aDesc.setComponentPointer (audioButton);
				
				addComponent (audioButton);				
			}
			
			if (aDesc.type=="CTATTable")
			{
				debug ("Creating (CTATTable):" + aDesc.name);
				
				var table=new CTATTable (aDesc,
										 aDesc.x,
										 aDesc.y,
										 aDesc.width,
										 aDesc.height);
				table.setName (aDesc.name);
				
				aDesc.setComponentPointer (table);
				
				addComponent (table);				
			}
			
			if (aDesc.type=="CTATVideo")
			{
				debug ("Creating (CTATVideo):" + aDesc.name);
				
				var video=new CTATVideo (aDesc,
										 aDesc.x,
										 aDesc.y,
										 aDesc.width,
										 aDesc.height);
				video.setName (aDesc.name);
				
				aDesc.setComponentPointer (video);
				
				addComponent (video);				
			}
			
			if (aDesc.type=="CTATRadioButton")
			{
				debug ("Creating (CTATRadioButton):" + aDesc.name);
				
				var radio=new CTATRadioButton (aDesc,
											   aDesc.x,
											   aDesc.y,
											   aDesc.width,
											   aDesc.height);
				radio.setName (aDesc.name);
				
				aDesc.setComponentPointer (radio);
				
				addComponent (radio);				
			}			
			
			if (aDesc.type=="CTATComboBox")
			{
				debug ("Creating (CTATComboBox):" + aDesc.name);
				
				var combo=new CTATComboBox (aDesc,
											aDesc.x,
											aDesc.y,
											aDesc.width,
											aDesc.height);
				combo.setName (aDesc.name);
				
				aDesc.setComponentPointer (combo);
				
				addComponent (combo);				
			}			
						
			if (aDesc.type=="CTATCheckBox")
			{
				debug ("Creating (CTATCheckBox):" + aDesc.name);
				
				var checkbox=new CTATCheckBox (aDesc,
											   aDesc.x,
											   aDesc.y,
											   aDesc.width,
											   aDesc.height);
				checkbox.setName (aDesc.name);
				
				aDesc.setComponentPointer (checkbox);
				
				addComponent (checkbox);				
			}
			
			if (aDesc.type=="CTATScrollPaneComponent")
			{
				debug ("Creating (CTATScrollPaneComponent):" + aDesc.name);
				
				var scrollpane=new CTATScrollPaneComponent (aDesc,
															aDesc.x,
															aDesc.y,
															aDesc.width,
															aDesc.height);
				scrollpane.setName (aDesc.name);
				
				aDesc.setComponentPointer (scrollpane);
				
				addComponent (scrollpane);				
			}			
			
			if (aDesc.type=="CTATComponentContainerReference")
			{
				debug ("Creating (CTATComponentContainerReference):" + aDesc.name);
				
				var group=new CTATGroupingComponent (aDesc.name);
				
				addComponent (group);
			}
		}
	}

	/*
	useDebugging=true;	
	var tools=new CTATShellTools ();
	tools.listComponents ();	
	useDebugging=false;
	*/	
	
	this.postProcess ();
	
	this.drawTutor ();
	
	ctatscrim.scrimDown ();
};
/**
*
*/
this.postProcess=function postProcess ()
{
	debug ("postProcess ()");
	
	//>----------------------------------------------------------------------------------
	
	for (var i=0;i<components.length;i++)
	{
		var ref=components [i];
			
		//debug ("Obtaining component for " + ref.name + " with type: " + ref.type);
			
		var component=ref.getComponentPointer ();
			
		if (component!=null)
		{
			//>---------------------------------------------------------------------------------
		
			if (component.getClassName ()=="CTATTable")
			{
				component.adjustTableContents ();				
			}	
			
			//>---------------------------------------------------------------------------------

			if ((component.getClassName ()=="CTATScrollPaneComponent") || (component.getClassName ()=="CTATComponentContainerReference"))
			{
				component.postProcess ();
			}
			//>---------------------------------------------------------------------------------
		}
		else
		{
			debug ("Error: component pointer is null");
		}
	}
	
	//>----------------------------------------------------------------------------------	
};
/**
*
*/
this.createStaticInterface=function createStaticInterface (aParent,intProps,aMovieClip)
{
	debug ("createStaticInterface ()");

	var parent=getSafeElementById("container");

	if (aParent!=null)
		parent=aParent;

	for (var t=0;t<intProps.length;t++)
	{
		var intNode=intProps [t];
									
		debug (intNode.nodeName);
			
		if (intNode.nodeName=="timeline")
		{
			debug ("Timeline node found, obtaining visual elements ...");
							
			this.createStaticInterface (null,intNode.childNodes,null);
			
			return; // we know there's nothing more in there here
		}	
			
					
		if (intNode.nodeName=="ctatcomponent")
		{		
			var inst=intNode.attributes.getNamedItem("instance").value;
								
			if (aMovieClip!=null)
			{
				debug ("Registering existence of CTAT component on MovieClip container: " + inst);
						
				aMovieClip.addComponent (inst);
			}
			//else
			//	debug ("Internal error: no MovieClip object available to attach CTAT component reference to");
		}
					
		//>--------------------------------------------------------------------
					
		if (intNode.nodeName=="shape")
		{												
			x=intNode.attributes.getNamedItem("x").value;
			y=intNode.attributes.getNamedItem("y").value;
			width=intNode.attributes.getNamedItem("width").value;
			height=intNode.attributes.getNamedItem("height").value;
			instName=intNode.attributes.getNamedItem("instance").value;
												
			debug ("Creating shape: " + instName + " at: " + x +","+ y+","+width+","+height);
						
			descString=("data:image/png;base64, "+parser.getNodeTextValue (intNode));
																
			var imgA=new Image();

			imgA.setAttribute("style", "position: absolute; top: " + y + "px; left:" + x + "px; z-index:"+currentZIndex+";");
			imgA.setAttribute('id',instName);
			imgA.setAttribute('src',descString);
					
			parent.appendChild(imgA);
						
			currentZIndex++;
		}
					
		//>--------------------------------------------------------------------					
					
		if (intNode.nodeName=="statictext")
		{				
			x=intNode.attributes.getNamedItem("x").value;
			y=intNode.attributes.getNamedItem("y").value;
			width=intNode.attributes.getNamedItem("width").value;
			height=intNode.attributes.getNamedItem("height").value;		
			instName=intNode.attributes.getNamedItem("instance").value;
						
			debug ("Creating static text: " + instName + " at: " + x +","+ y+","+width+","+height);
						
			descString=("data:image/png;base64, "+parser.getNodeTextValue (intNode));
											
			var imgB=new Image();

			imgB.setAttribute("style", "position: absolute; top: " + y + "px; left:" + x + "px; z-index:"+currentZIndex+";");
			imgB.setAttribute('id',instName);
			imgB.setAttribute('src',descString);
					
			parent.appendChild(imgB);
						
			currentZIndex++;					
		}
					
		//>--------------------------------------------------------------------
					
		if (intNode.nodeName=="movieclip")
		{
			instName=intNode.attributes.getNamedItem("instance").value;
					
			debug ("Creating movieclip: " + instName);
																
			var aX=intNode.attributes.getNamedItem("x").value;
			var aY=intNode.attributes.getNamedItem("y").value;
			var aWidth=intNode.attributes.getNamedItem("width").value;
			var aHeight=intNode.attributes.getNamedItem("height").value;								
			
			var aClip=new CTATMovieClip (instName,aX,aY,aWidth,aHeight);

			var newParent=aClip.wrapComponent (parent);			
					
			debug ("Created movieclip: " + instName + " at: " + aClip.x +","+ aClip.y+","+aClip.width+","+aClip.height);										
										
			movieclips.push (aClip);
					
			var subProps=intNode.childNodes;
						
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
	debug ("runTutor ()");

	if (tutorRunning==true)
	{
		debug ("The tutor is already running");
		return;
	}	
	
	skillSet=new CTATSkillSet ();
	flashVars=new CTATFlashVars ();
	flashVars.assignRawFlashVars(aVars);
		
	window.onerror = function(errorMsg, url, lineNumber) 
	{
	    //var debugPointer = new CTATBase("", "");
	    
	    var formatter=new CTATHTMLManager ();
	    
	    debug(formatter.htmlEncode (errorMsg) + " in " + url + ", line " + lineNumber);	    
	};
	
	ctatcanvas = getSafeElementById("main-canvas");
		
	if (ctatcanvas==null)
	{
		alert ("Internal error: HTML5 canvas is null");
		return;
	}
	
	ctx = ctatcanvas.getContext('2d');
	
	if (ctx==null)
	{
		alert ("Internal error: HTML5 canvas context is null");
		return;
	}	
	
	//useDebugging=true;
	
	initialize();
	
	debug ("runTutor () ... all set");
	
	tutorRunning=true;
}
/**
 * 
 */
function runTutorSidebar (aVars)
{
	debug ("runTutorSidebar ()");
	
	if (tutorRunning==true)
	{
		debug ("The tutor is already running");
		return;
	}
	
	tutorRunning=true;	
	
	skillSet=new CTATSkillSet ();
	flashVars=new CTATFlashVars ();
	flashVars.assignRawFlashVars(aVars);
		
	if (platform=="ctat")
	{	
		window.onerror = function(errorMsg, url, lineNumber) 
		{	    
			var formatter=new CTATHTMLManager ();
			
			useDebugging=true; // This should always go through
			debug(formatter.htmlEncode (errorMsg) + " in " + url + ", line " + lineNumber);	    
			useDebugging=false;
		};
	
		ctatcanvas = getSafeElementById("main-canvas");
		
		if (ctatcanvas==null)
		{
			alert ("Internal error: HTML5 canvas is null");
			return;
		}
	
		ctx = ctatcanvas.getContext('2d');
	
		if (ctx==null)
		{
			alert ("Internal error: HTML5 canvas context is null");
			return;
		}
	}

	//testTutor ();

	initializeSidebar();
	
	debug ("runTutor () ... all set");	
}
/**
*
*/
function initTutor ()
{
	debug ("initTutor ()");
	 
	ctatscrim.waitScrimUp();
		
	var debugtraces=getSafeElementById("debugtraces");
	
	if (debugtraces!=null)
	{
		if (debugtraces.checked==true)
			useDebugging=true;
		else
			useDebugging=false;		
	}
	
	var tempFlashVars=tutorPrep (FlashVars);
	
	mobileAPI=new CTATMobileTutorHandler ("keyboardUI",tempFlashVars ['keyboard']);	

	debug ("initTutor ()");
	
	if (tempFlashVars ["session_id"]=="none")
		tempFlashVars ["session_id"]=("qa-test_"+guid());
		
	if (tempFlashVars ["connection"]=="javascript")
	{
		deployJava.runApplet({id: 'TSApplet', width: 150, height: 10}, {jnlp_href: '/ctat_applet/TSApplet.jnlp'}, '1.6');
	}		
		    	
	runTutor (tempFlashVars);
}
/**
*
*/
function initTutorSidebar (aVars)
{	
	debug ("initTutorSidebar ()");
	
	//ctatscrim.waitScrimUp();
		
	var debugtraces=getSafeElementById("debugtraces");
		
	var internalFlashVars=tutorPrep (aVars);
			
	if (internalFlashVars ["session_id"]=="none")
		internalFlashVars ["session_id"]=("qa-test_"+guid());
		    	
	runTutorSidebar (internalFlashVars);
}
/**
 * This is a function accessible by TutorShop
 */
function receiveFromTutor ()
{
	debug ("receiveFromTutor ()");
}

/**
 * This is a function accessible by TutorShop and is used to do a rapid
 * shutdown of a tutor whilst at the same time saving its state. 
 */
function saveAndQuit ()
{
	debug ("saveAndQuit ()");	
}

/**
*
*/
function prepTutorArea ()
{
	//The first thing we will do is apply centering styles to the tutor
	centerTutor(600, 200);

	//Will do a check to see if the Autorun box was checked. If it was, start the tutor.
	var args = parseQueryString ();
	
	if (args["AUTORUN"]=="on")
	{
		initTutor ();
	}
	
	else 
	{
		//This will drape a play button over the main canvas, like a scrim.
		var canvasToPlayButton=new CTATPlayButton();
	}	
}

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
	debug ("gradeAnonymousComponent ()");

	var tsMessage=new CTATSAI(aSelection,anAction,anInput);
	commShell.processComponentAction(tsMessage);
}

/**
*
*/
function testTutor(aVars)
{
	debug ("testTutor ()");
	
	window.onerror = function(errorMsg, url, lineNumber) 
	{
		//debugPointer = new CTATBase("", "");
	
		var formatter=new CTATHTMLManager ();
		
		useDebugging=true; // This should always go through
		debug(formatter.htmlEncode (errorMsg) + " in " + url + ", line " + lineNumber);	    
		useDebugging=false;
	};	
	
	var internalFlashVars=tutorPrep (aVars);	
	
	skillSet=new CTATSkillSet ();
	flashVars=new CTATFlashVars ();
	flashVars.assignRawFlashVars(aVars);	

	var connector=new CTATCommLibrary ();
	//connector.send_post ("http://qa.pact.cs.cmu.edu/courses",'<?xml version=\"1.0\" encoding=\"UTF-8\"?><hello></hello>');
	connector.send ("http://augustus.pslc.cs.cmu.edu/crossdomain.xml");
}
