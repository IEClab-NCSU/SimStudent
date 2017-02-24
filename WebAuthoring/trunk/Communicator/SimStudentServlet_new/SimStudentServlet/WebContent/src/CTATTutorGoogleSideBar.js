/**-----------------------------------------------------------------------------
 $Author: vvelsen $
 $Date: 2014-10-22 10:06:23 -0400 (Wed, 22 Oct 2014) $
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
goog.provide('CTATTutorGoogleSideBar');

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
goog.require('CTATNameTranslator');

var hintWindowWidth=350;
var hintWindowHeight=100;
var component=null;
var drawing=false;
var onMobile=false;

var ctatscrim=new CTATScrim();

var parser=null;

var table=null;

if (CTATConfig.parserType=="xml")
{
	parser=new CTATXML ();
}
else
{
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

	if (drawing==true)
	{
		return;
	}

	drawing=true;

	clear ();

	for (var i=0;i<components.length;i++)
	{
		var aDesc=components [i];

		//ctatdebug ("Drawing component: " + i);

		var component=aDesc.getComponentPointer ();

		if (component!=null)
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
function createSidebarInterface (aCanvasWidth,aCanvasHeight)
{
	ctatdebug ("createSidebarInterface ("+aCanvasWidth+","+aCanvasHeight+")");

	var tutorWidth=aCanvasWidth-4; // assume a 2 pixel margin
	var tutorHeight=aCanvasHeight-4; // assume a 2 pixel margin

	var dummyDescription=new Object ();

	//>------------------------------------------------------

	ctatdebug ("Creating (CTATSkillWindow)...");

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

	ctatdebug ("Creating (CTATHintButton)...");

	dummyDescription.name="hint";
	dummyDescription.type="CTATHintButton";
	dummyDescription.x=5;
	dummyDescription.y=tutorHeight-61-5;
	dummyDescription.width=61;
	dummyDescription.height=61;

	var hint=new CTATImageButton (dummyDescription,5,tutorHeight-61,61,61);
	hint.setName ("hint");
	hint.setClassName ("CTATHintButton");
	hint.assignImages ("https://qa.pact.cs.cmu.edu/images/skindata/Hint-Default.png",
					   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Hover.png",
					   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Click.png",
					   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Disabled.png");

	addComponent (hint);

	// pointer.addSafeEventListener ('click',hint.processClick,hint);

	//>------------------------------------------------------

	ctatdebug ("Creating (CTATDoneButton)...");

	dummyDescription.name="done";
	dummyDescription.type="CTATDoneButton";
	dummyDescription.x=tutorWidth-61-5;
	dummyDescription.y=tutorHeight-61-5;
	dummyDescription.width=61;
	dummyDescription.height=61;

	var done=new CTATImageButton (dummyDescription,tutorWidth-61,tutorHeight-61,61,61);
	done.setName("done");
	done.setClassName ("CTATDoneButton");
	done.assignImages ("https://qa.pact.cs.cmu.edu/images/skindata/Done-Default.png",
					   "https://qa.pact.cs.cmu.edu/images/skindata/Done-Hover.png",
					   "https://qa.pact.cs.cmu.edu/images/skindata/Done-Click.png",
					   "https://qa.pact.cs.cmu.edu/images/skindata/Done-Disabled.png");

	addComponent (done);

	// pointer.addSafeEventListener ('click',done.processClick,done);

	//>------------------------------------------------------

	ctatdebug ("Creating (CTATHintWindow)...");

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
function initializeSidebar()
{
	ctatdebug ("initializeSidebar ()");

	var googleTrans=new CTATNameTranslator ();
	googleTrans.setPassthrough (true);
	assignNameTranslator (googleTrans);

	if (CTATConfig.platform=="ctat")
	{
		// Create our own version of the sidebar

		canvasWidth=ctatcanvas.width;
		canvasHeight=ctatcanvas.height;

		ctatdebug ("Canvas: " +canvasWidth + "," + canvasHeight);

		createSidebarInterface (canvasWidth,canvasHeight);
	}
	else
	{
		// Create the Google sidebar
	}

	commShell=new CTATCommShell ();
	//useDebugging=true;
	commShell.processStartProblem ();
	commShell.init (null);
	//useDebugging=false;
}
/**
 *
 */
function createInterface ()
{
	ctatdebug ("createInterface ("+components.length+")");

	var decString="";
	var x=0;
	var y=0;
	var width=50;
	var height=50;
	var instName="";

	// Create all the CTAT components ...

	for (var i=0;i<components.length;i++)
	{
		var aDesc=components [i];

		//ctatdebug(aDesc);

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
			ctatdebug ("Component: " + aDesc.name + ", type: " + aDesc.type);

			if (aDesc.type=="CTATTableGoogle")
			{
				ctatdebug ("Creating (CTATTableGoogle):" + aDesc.name);

				table=new CTATTableGoogle (aDesc,
										   aDesc.x,
										   aDesc.y,
										   aDesc.width,
										   aDesc.height);
				table.setName (aDesc.name);

				aDesc.setComponentPointer (table);

				addComponent (table);

				ctatdebug(table.getDivWrap());
			}
		}
	}

	ctatdebug ("Drawing tutor ...");

	drawTutor ();

	ctatdebug ("createInterface () done");
};

/**
 *
 */
function runTutorSidebar (aVars)
{
	ctatdebug ("runTutorSidebar ()");

	if (CTATGlobals.tutorRunning==true)
	{
		ctatdebug ("The tutor is already running");
		return;
	}

	CTATGlobals.tutorRunning=true;

	skillSet=new CTATSkillSet ();
	flashVars=new CTATFlashVars ();
	flashVars.assignRawFlashVars(aVars);

	if (CTATConfig.platform=="ctat")
	{
		window.onerror = function(errorMsg, url, lineNumber)
		{
			var formatter=new CTATHTMLManager ();

			ctatdebug(formatter.htmlEncode (errorMsg) + " in " + url + ", line " + lineNumber);
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

	initializeSidebar();

	ctatdebug ("runTutorSidebar () ... all set");
}

/**
*
*/
function initTutorSidebar (aVars)
{
	ctatdebug ("initTutorSidebar ()");

	// google.script.run.withFailureHandler(onFailure).setText("A1","Starting ...");

	var debugtraces=getSafeElementById("debugtraces");

	var internalFlashVars=tutorPrep (aVars);

	if (internalFlashVars ["session_id"]=="none")
	{
		internalFlashVars ["session_id"]=("qa-test_"+guid());
	}

	runTutorSidebar (internalFlashVars);
}
