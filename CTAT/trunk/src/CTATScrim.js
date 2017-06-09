/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2017-02-06 08:40:56 -0600 (週一, 06 二月 2017) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATScrim.js $
 $Revision: 24575 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

The scrim. Modeled after the scrim in ActionScript 3.0.

At the moment, a global variable called CTATScrim.scrim should be used as the scrim.

Here are is a list of the different methods:
	- scrimUp(aMessage) : Pulls up a scrim with a dialog that displays a message aMessage.
	- waitScrimUp() : Pulls up the wait scrim.
	- OKScrimUp(aMessage, aFunction) : Pulls up a scrim with a dialog message of aMessage, and executes aFunction when the ok button is clicked.
	- confirmScrimUp(prompt, onYes, onNo) : Pulls up a scrim with a prompt message, and function handlers for clicking on yes/no buttons.
	- errorScrimUp(aMessage) : Pulls up a non-removable scrim displaying an error message aMessage.
	- warningScrimUp(aMessage) : Pulls up scrim displaying a warning message aMessage, and may only be closed when the close button is clicked.
	- scrimDown() : Pulls down the scrim if it is in a state that allows it to be pulled down.
	- resizeScrim(newWidth, newHeight) : Resizes the scrim, and sets the width and height to newWidth and newHeight.

If the wait scrim is up, and you wish to pull up another scrim, you should pull down the wait scrim first for the time
being.

 */
goog.provide('CTATScrim');

goog.require('CTATBase');
goog.require('CTATCanvasComponent');
goog.require('CTATConfig');
goog.require('CTATSandboxDriver');
// goog.require('CTATShape'); unused
goog.require('CTATLanguageManager');

// We need the following variables in the global scope (at least for now), otherwise
// the following code will not work as per Trac Ticket #701
//
// var myScrim = new CTATScrim();
//
// myScrim.warningScrimUp('test');

var scrimIsUp=false;
var errorScrim=false;
var waitScrim=true;
var warnScrim=false;
var connectionScrim=false;

var authorTimeSet=true;
var inAuthorTime=true;

/**
*
*/
CTATScrim = function()
{
	CTATBase.call(this, "CTATScrim", "__undefined__");

	//Need an array of messages in the event the message is too long and
	//flows over the dialog box.
	var messageList=[];

	//The recursion in fitToDialogX gives us the message backwards. This is
	//used with messageList reverse that.
	var tempList=[];

	//Padding between text if it overflows
	var padding=4;

	var message="";

	var hasYesButton=false;
	var hasNoButton=false;
	var hasCloseButton=false;

	//Used to grab the handlers passed into the confirmScrim function
	var yesPtr=null;
	var noPtr=null;

	var scrimComponent=new CTATCanvasComponent("CTAT Scrim");
	var scrim=null;
	var dialog=null;
	var scrimMessage=null;

	var errorsAndWarnings=[];

	var scrimColor="rgba(0, 0, 0, 0.25)";
	var errorColor="rgb(255, 0, 0)";
	var warningColor="rgb(255, 255, 0)";
	var defaultColor="#CCCCCC";

	var scrimBorderColor=defaultColor;

	var dialogWidthRatio=6/8;
	var dialogHeightRatio=2/8;

	//Used to support placing scrim objects over the relatively positioned canvas
	var mainLeft=0;

	//The different HTML elements we use in the dialog box:
	var img=null;
	var yesButton=null;
	var noButton=null;
	var closeButton=null;
	var okButton=null;

	var scrimBorderWidth=5;

	var pointer=this;

	//Handler for the close button
	function closeFunction()
	{
		warnScrim=false;
		pointer.scrimDown();
	}

	function clearScrim()
	{
		scrimComponent.removeComponent();
		pointer.removeHTMLElements();
	}

	/**
	 *
	 */
	function displayDialog()
	{
		if (CTATConfig.platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}

		$("#scrimpanel").empty();
		$("#scrim").append('<div class="css3-windows-7"><div class="fenster"><h4 class="titel">CTAT Info Panel</h4><div id="scrimpanel" class="inhalt"><br>'+message+'<br><br></div></div></div>');

		if(hasCloseButton===true)
		{
			waitScrim=false;
			closeButton=makeHTMLButton(closeFunction, "close");
			$(closeButton).appendTo('#scrimpanel');
		}

		if(hasYesButton===true)
		{
			waitScrim=false;
			yesButton=makeHTMLButton(yesPtr, "yes");
			$(yesButton).appendTo('#scrimpanel');
			hasYesButton=false;
		}

		if(hasNoButton===true)
		{
			waitScrim=false;
			noButton=makeHTMLButton(noPtr, "no");
			$(noButton).appendTo('#scrimpanel');
			hasNoButton=false;
		}

		// Warning: Make sure the spinner is removed any time the waitScrim is hidden!
		if(waitScrim===true)
		{
			pointer.ctatdebug ("Adding spinner ...");

			$('#scrimpanel').append ('<br>');
			var spin_div = $('<div id="scrim_spin">');
			spin_div.css('width', '100%');
			spin_div.css('position', 'relative');
			$('#scrimpanel').append(spin_div);
			try {
				var spinner = new Spinner({className:'scrim_spinner'}).spin();
				$('#scrim_spin').append(spinner.el);
			} catch (err) {
				if (err instanceof ReferenceError || err instanceof TypeError) {
					pointer.ctatdebug('Spinner is not available, please include it in the build or add <script src="node_modules/spin.js/spin.min.js"></script> to the html file.',err, typeof(err));
				}
			}
		}
	}
	/**
	 *
	 */
	function drawScrim()
	{
		pointer.ctatdebug ("drawScrim ()");

		if (CTATConfig.platform=="google")
		{
			pointer.ctatdebug ("This is not available for now when we're in this environment");
			return;
		}

		$('<div id="scrim" class="ctatpageoverlay"></div>').appendTo('body');
		$("#scrim").css("z-index",1000);

		/*
		//Draggable
		$(function()
		{
			$(".fenster").draggable(
			{
				cancel: '.inhalt',
				containment: 'body',
				scroll: false,
				stack: { group: '.fenster', min: 1 }
			});
		});

		//Resizeable
		$(function()
		{
			$(".fenster").resizable(
			{
				handles: 'n, e, s, w, ne, se, sw, nw',
				containment: 'body',
				minHeight: 80,
				minWidth: 138,
				maxHeight: $(window).height(),
				maxWidth: $(window).width()
			});
		});
		*/

		displayDialog();
	}
	/**
	 *
	 */
	this.scrimUp=function scrimUp(aMessage)
	{
		if (CTATConfig.platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}

		pointer.ctatdebug("scrimUp ()");

		var mapped=CTATGlobals.languageManager.filterString (aMessage);

		pointer.ctatdebug("Message: " + mapped);

		if(scrimIsUp===true)
		{
			if(waitScrim===true)
			{
				message=mapped;
			}
			else
			{
				message += " \n " + mapped;
			}

			//Only need one scrim up at a time
			scrimComponent.removeComponent();
			messageList=[];
			tempList=[];
			pointer.removeHTMLElements();
		}

		else
		{
			message=mapped;
			scrimIsUp=true;
		}

		pointer.ctatdebug("scrimUp() to call drawScrim() scrimIsUp "+scrimIsUp);
		drawScrim();
	};
	/**
	 *
	 */
	this.waitScrimUp=function waitScrimUp()
	{
		pointer.ctatdebug("waitScrimUp () CTATConfig.platform "+CTATConfig.platform+", scrimIsUp "+scrimIsUp);

		if (CTATConfig.platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}

		// We're already showing a scrim, take the previous one down to
		// avoid state and graphics conflicts
		if (scrimIsUp===true)
		{
			pointer.scrimDownForced();
		}

		scrimBorderColor=defaultColor;
		waitScrim = true;

		//pointer.scrimUp("Please wait while the tutor is being loaded");
		pointer.scrimUp(CTATGlobals.languageManager.getString ("LOADING"));

		pointer.ctatdebug("return from waitScrimUp(): scrimIsUp "+scrimIsUp);
	};
	/**
	 *
	 */
	this.nextProblemScrimUp=function nextProblemScrimUp()
	{
		if (CTATConfig.platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}

		// We're already showing a scrim, take the previous one down to
		// avoid state and graphics conflicts
		if (scrimIsUp===true)
		{
			pointer.scrimDownForced();
		}

		pointer.ctatdebug("nextProblemScrimUp ()");

		scrimBorderColor=defaultColor;

		//pointer.scrimUp("Retrieving the Next Problem...");
		pointer.scrimUp(CTATGlobals.languageManager.getString ("NEXTPROBLEM"));
	};
	/**
	 *
	 */
	this.OKScrimUp=function OKScrimUp(aMessage, aFunction)
	{
		if (CTATConfig.platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}

		// We're already showing a scrim, take the previous one down to
		// avoid state and graphics conflicts
		if (scrimIsUp===true)
		{
			pointer.scrimDownForced();
		}

		pointer.ctatdebug("OKScrimUp ("+aMessage+","+aFunction+")");

		errorScrim=false;
		warnScrim=false;
		hasCloseButton=false;
		hasYesButton=false;
		hasNoButton=false;

		scrimBorderColor="black";

		waitScrim=false;
		pointer.scrimUp(aMessage);

		okButton=makeHTMLButton(aFunction, "OK");
		$(okButton).appendTo('#scrimpanel');
	};
	/**
	 *
	 */
	this.confirmScrimUp=function confirmScrimUp(prompt, onYes, onNo)
	{
		if (CTATConfig.platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}

		// We're already showing a scrim, take the previous one down to
		// avoid state and graphics conflicts
		if (scrimIsUp===true)
		{
			pointer.scrimDownForced();
		}

		pointer.ctatdebug("confirmScrimUp ("+prompt+","+onYes+","+onNo+")");

		scrimBorderColor=defaultColor;

		hasYesButton=true;
		hasNoButton=true;
		noPtr=onNo;
		yesPtr=onYes;

		pointer.scrimUp(prompt);
	};
	/**
	 *
	 */
	function errScrimUp(aMessage)
	{
		if (CTATConfig.platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}

		// We're already showing a scrim, take the previous one down to
		// avoid state and graphics conflicts
		if (scrimIsUp===true)
		{
			pointer.scrimDownForced();
		}

		pointer.ctatdebug("errScrimUp ("+aMessage+")");

		if(errorScrim===true)
		{
			scrimBorderColor=errorColor;

			if(hasCloseButton===true)
			{
				hasCloseButton=false;
				getSafeElementById(ctatcontainer).removeChild(closeButton);
			}
		}

		else if(warnScrim===true)
		{
			scrimBorderColor=warningColor;
			hasCloseButton=true;
		}

		pointer.scrimUp(aMessage);
	}
	/**
	 *
	 */
	this.errorScrimUp=function errorScrimUp(aMessage)
	{
		if (CTATConfig.platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}

		// We're already showing a scrim, take the previous one down to
		// avoid state and graphics conflicts
		if (scrimIsUp===true)
		{
			pointer.scrimDownForced();
		}

		if(authorTimeSet===false)
		{
			pointer.ctatdebug("we don't know if we're in authorTime or not yet so we're just going to hold onto the messasge");
			errorsAndWarnings.push("ERROR: "+aMessage);
			errorScrim=true;
			return;
		}

		else if(inAuthorTime===false)
		{
			pointer.ctatdebug("We're not in authorTime, student's don't need to see our design mistakes");
			// *** some kind of comm shell action goes here ***
			return;
		}

		errorScrim=true;
		errScrimUp("ERROR: "+aMessage);
	};
	/**
	 *
	 */
	this.warningScrimUp=function warningScrimUp(aMessage)
	{
		if (CTATConfig.platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}

		// We're already showing a scrim, take the previous one down to
		// avoid state and graphics conflicts
		if (scrimIsUp===true)
		{
			pointer.scrimDownForced();
		}

		if(authorTimeSet===false)
		{
			pointer.ctatdebug("we don't know if we're in authorTime or not yet so we're just going to hold onto the messasge");
			errorsAndWarnings.push("WARNING: "+aMessage);
			warnScrim=true;
			return;
		}

		else if(inAuthorTime===false)
		{
			pointer.ctatdebug("We're not in authorTime, student's don't need to see our design mistakes");
			// *** some kind of comm shell action goes here ***
			return;
		}

		warnScrim=true;
		errScrimUp("WARNING: "+aMessage);
	};
	/**
	 *
	 */
	this.handleTSDisconnect=function handleTSDisconnect()
	{
		connectionScrim=true;

		//pointer.scrimUp("The tutor has disconnected. Please refresh the page.");
		pointer.scrimUp(CTATGlobals.languageManager.getString ("TUTORDISCONNECTED"));
	};
	/**
	 *
	 */
	this.removeHTMLElements = function removeHTMLElements()
	{

	};
	/**
	 *
	 */
	this.scrimDown=function scrimDown()
	{
		if (CTATConfig.platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}

		if (scrimIsUp===false)
		{
			pointer.ctatdebug("The scrim isn't up, returning");
			return;
		}

		if (errorScrim===true)
		{
			pointer.ctatdebug("The scrim is up to describe errors to the user, leave it up!");
			return;
		}

		if (warnScrim===true)
		{
			pointer.ctatdebug("The scrim is up to display warnings to the user, leave it up!");
			return;
		}

		if (connectionScrim===true)
		{
			pointer.ctatdebug("The scrim is up for a connection issue leave it up!");
			return;
		}
		/*
		if (waitForClick)
		{
			pointer.ctatdebug("The scrim is waiting on user input, leave it up!");
			return;
		}*/

		pointer.scrimDownForced ();
	};

	/**
	 *
	*/
	this.scrimDownForced=function scrimDownForced()
	{
		pointer.ctatdebug("enter scrimDownForced() scrimIsUp "+scrimIsUp);

		$('#scrim').remove();

		scrimComponent.removeComponent();
		pointer.removeHTMLElements();
		scrimIsUp=false;
		waitScrim=false;
		message="";
		pointer.ctatdebug("exit scrimDownForced() scrimIsUp "+scrimIsUp);
	};

	/**
	 *
	 */
	this.defaultClickHandler=function defaultClickHandler ()
	{
		pointer.ctatdebug("defaultClickHandler ()");
		pointer.scrimDown();
	};

	/**
	 *
	 */
	function makeHTMLButton(clickHandle, btnType)
	{
		pointer.ctatdebug("makeHTMLButton ()");

		var btn=document.createElement('input');
		btn.type='button';
		btn.value=btnType;

		if (clickHandle)
		{
			pointer.ctatdebug("clickHandle!=null");

			btn.onclick=clickHandle;
		}
		else
		{
			pointer.ctatdebug("clickHandle==null");

			btn.onclick=pointer.defaultClickHandler;
		}

		//Is it a yes, no, play, etc button
		btn.id=btnType;
		btn.setAttribute('class', "scrimButton");

		return (btn);
	}

	/**
	 *
	 */
	this.setInAuthorTime=function setInAuthorTime(theValue)
	{
		pointer.ctatdebug("setting inAuthorTime = " +theValue);

		authorTimeSet=true;
		inAuthorTime=theValue;

		if (!theValue)
		{
			errorScrim=false;
			warnScrim=false;

			if(hasCloseButton)
			{
				scrim.removeChild(closeButton);
			}

			scrimBorderColor=defaultColor;
		}

		else
		{
			if(errorScrim || warnScrim)
			{
				for(var mess in errorsAndWarnings)
				{
					errScrimUp(mess);
				}
			}
		}
	};

	this.getInAuthorTime=function getInAuthorTime()
	{
		return (inAuthorTime);
	};
};

CTATScrim.prototype = Object.create(CTATBase.prototype);
CTATScrim.prototype.constructor = CTATScrim;

CTATGlobals.languageManager=CTATLanguageManager.theSingleton;

CTATScrim.scrim = new CTATScrim(); // global scrim, use this instead of ctatscrim
