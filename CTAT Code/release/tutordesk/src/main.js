/**
*
*/

// Create managers ... 
var cloudUtils =new CloudUtils ();
var desktop = new CTATDesktop ();
var settingsManager = new CTATSettings ();
var windowManager = new CTATWindowManager ();
var sManager = null;
// OLI managers, will be moved to a different location once we are more
// able to handle dynamic extension and usage of the environment
var cManager = null;
var oManager = null;

// Create global check variables, we should find a way to remove this from
// the global scope
var retrievalSize=0;
var retrievalCounter=0;
var tutordeskInitialized=false;
	
/**
*
*/
function showSolidBlocker ()
{
	console.log ("showSolidBlocker()");
	
	$("#blocker").removeClass ("blocker");
	$("#blocker").addClass ("solidblocker");
}	
	
/**
*
*/
function toggleBlocker (shown)
{
	console.log ("toggleBlocker("+shown+")");

	if (shown==false)
	{		
		$("#blocker").removeClass ("solidblocker");
		$("#blocker").css('display', 'none');
	}
	else
	{	
		$("#blocker").addClass ("blocker");
		$("#blocker").css('display', 'block');
	}
}

/**
*
*/
function toggleProgressDialog (shown)
{
	console.log ("toggleProgressDialog("+shown+")");

	toggleBlocker (shown);

	if (shown==false)
	{		
		$("#loading").invisible();
	}
	else
	{
		$("#loading").visible();
		windowManager.centerWindow ("#loading");
	}
}

/**
*
*/
function goFullscreen ()
{
	console.log ("goFullscreen()");
	
	if 
	(
		document.fullscreenElement ||
		document.webkitFullscreenElement ||
		document.mozFullScreenElement ||
		document.msFullscreenElement
	) 
	{
		if (document.exitFullscreen) 
		{
			document.exitFullscreen();
		}
		else if (document.mozCancelFullScreen) 
		{
			document.mozCancelFullScreen();
		} 
		else if (document.webkitExitFullscreen) 
		{
			document.webkitExitFullscreen();
		}
		else if (document.msExitFullscreen) 
		{
			document.msExitFullscreen();
		}
	} 
	else 
	{
		element = $(document.body).get(0);
		
		if (element.requestFullscreen) 
		{
			element.requestFullscreen();
		} 
		else if (element.mozRequestFullScreen) 
		{
			element.mozRequestFullScreen();
		} 
		else if (element.webkitRequestFullscreen) 
		{
			element.webkitRequestFullscreen(Element.ALLOW_KEYBOARD_INPUT);
		} 
		else if (element.msRequestFullscreen) 
		{
			element.msRequestFullscreen();
		}
	}
}

/**
*
*/
function setStatus (anAuthorized,aMessage)
{
	var statDiv=document.getElementById ("status");

	if (statDiv)
	{
		if (anAuthorized==false)
		{
			statDiv.innerHTML=aMessage;
		}
		else
		{
			statDiv.innerHTML=aMessage;
		}
	}	
}	

/**
*
*/
function closeWizard ()
{
	console.log ("closeWizard ()");
	
	jQuery("#wizard").toggle();
	enableIconClick ()
	wizardActive=false;	
}

/**
*
*/
function processHelpIcon ()
{
	console.log ("processHelpIcon ()");
	
	window.open("http://ctat.pact.cs.cmu.edu");
}

/**
*
*/
function showAccount ()
{
	console.log ("showAccount ()");
	
	windowManager.addWindow ("#accountwindow");
	
	var mManager=new CTATAccountManager ();
	mManager.showChooser ();
}

/**
*
*/
function logOff ()
{
	console.log ("logOff ()");
	
	setStatus (false,"logging out ...");
	
	showSolidBlocker ();
	
	cloudUtils.disconnect(processLogOff);
}

/**
*
*/
function processLogOff ()
{
	console.log ("processLogOff ()");
	
	showAccount ();
}

/**
*
*/
function refreshAuth ()
{
	console.log ("refreshAuth ()");
	
	cloudUtils.reauthorize (refreshAuthResult);
}

/**
*
*/
function refreshAuthResult ()
{
	console.log ("refreshAuthResult ()");
}
	
/**
*
*/
$.fn.center = function () 
{
	this.css("left", ( $(window).width() - this.width() ) / 2+$(window).scrollLeft() + "px");
	return this;
}

/**
*
*/
function showSettings ()
{
	console.log ("showSettings ()");
	
	sManager = new CTATSettingsDialog ();
	sManager.showSettings ();
}

/**
*
*/	
function showFiles ()
{
	console.log ("showFiles()");
	
	if (!window.ctatFileChooser)
		window.ctatFileChooser = new CTATFileChooser();

	window.ctatFileChooser.show('DISPLAY');
}

/**
*
*/
function showDataShop ()
{
	console.log ("showDataShop ()");
		
	windowManager.addWindow ("#datashop",true);
}

/**
*
*/	
function startEditor ()
{
	console.log ("startEditor()");
		
	window.fEditor=new CTATFileEditor ();
	window.fEditor.init ();
}

/**
*
*/	
function showCTAT (callback)
{
	console.log ("showCTAT ()");	
	window.silexEditor = windowManager.addWindow ("#ctatwindow",true);
	if(!window.silexApp)
	{
		console.log("first time launching; init silex app");
	}	
	if (typeof callback === 'function')
		callback();
}

/**
*
*/
function initSilex()
{
	console.log('initSilex()');
	window.silexApp = new silex.App();
}	

/**
*
*/	
function showCTATAuthoring ()
{
	console.log ("showCTATAuthoring ()");
			
	windowManager.addWindow ("#ctateditor",true);
		
	//jQuery('#ctatauthoringlayout').layout();
	
	var $tabs=jQuery('#cc').tabs(
	{
		'fit': true,
		'overflow': 'auto'
	})

	var $tabs=jQuery('#tt').tabs(
	{
		'fit': true,
		'overflow': 'auto'
	})

	var $tabs=jQuery('#xx').tabs(
	{
		'fit': true,
		'overflow': 'auto'
	})	
	
	view_br ();
}

/**
*
*/
function systemsCheck ()
{
	console.log ("systemsCheck ()");
	
	if (!window.jQuery) 
	{	
		return (false);
	}	
	
	return (true);
}

/**
*
*/
function startTutordesk (aMode)
{
	console.log ("startTutordesk ()");

	useDebugging=true; // enable CTAT debugging	
	
	if (tutordeskInitialized==true)
	{
		console.log ("Tutordesk already initialized, bump");
		return;
	}
	
	if (systemsCheck ()==false)
	{
		console.log ("This system can't run tutordesk, bump");
		return;
	}
	
	var sData=settingsManager.getSettingsObject ();
	
	console.log ("Testing to see if the default application mode needs to be adjusted: " + settingsManager.getApplicationMode ());
	
	if (aMode)
	{
		sData.internal['mode']=aMode;
		
		console.log ("We're being started in a specific mode: " + aMode);
		
		if (aMode=="desktop")
		{
			console.log ("Starting in desktop mode ...");
			
			sData.internal['mode']=='browser';
		}
	}
	else
	{
		console.log ("We're not being started in a specific mode, assuming browser based execution.");
		sData.internal['mode']=='desktop';
	}
	
	console.log ("Executable application mode: " + settingsManager.getApplicationMode ());
	
	envSetup ();
		
	$(window).resize(function() 
	{
		$('#dashboard').center();
	});			
			
	$('#dashboard').center();
	
	
	$('#ctateditorcontent').resize(function()
	{
		console.log ("authoring tool resizio ...");
	});

	if (settingsManager.isDesktop ()==false)
	{		
		setStatus (false,"logging in ...");
	}
	else
	{
		$('#status').hide ();
	}

	desktop.init ();	
	
	initSilex();
	
	//initRightClick ();
	
	if (settingsManager.isDesktop ()==false)
	{
		var query = window.utils.parseQueryString(window.location.search);
		if (!query['login'])
		{
			showAccount ();
		}
		else
		{
			cloudUtils.initDrive(query['mode'], true);
			toggleBlocker(false);
		}
	}	
	else
	{
		toggleBlocker(false);
	}
	
	if (window.desktopCallback)
	{
		desktopCallback ();
	}
	
	tutordeskInitialized=true;
}

/**
 *
 */
if (window.jQuery) 
{
	$(window).on('load', function() 
	{
		if (window.studentDeskMode)
		{
			if (studentDeskMode==true)
			{
				startTutordesk ('desktop');
				return;
			}
		}
		
		startTutordesk ('browser');
	});
}
else
{
	alert ("Error: JQuery not available, can't execute $(window).load()");
}

/**
*
*/
function processCTATFolderCreate ()
{	
	ctatdebug ("processCTATFolderCreate ()");
	
	settingsManager.init ();
	
	toggleProgressDialog (false);
}
