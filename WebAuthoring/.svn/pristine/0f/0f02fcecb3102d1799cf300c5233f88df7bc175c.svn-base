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
 
The scrim. Modeled after the scrim in ActionScript 3.0.

At the moment, a global variable called ctatscrim (CTATGlobals.js) should be used as the scrim.

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

function CTATScrim()
{
	CTATBase.call(this, "CTATScrim", "__undefined__");

	var scrimIsUp=false;
	var errorScrim=false;
	var waitScrim=true;
	var warnScrim=false;
	var connectionScrim=false;
	
	var authorTimeSet=true;
	var inAuthorTime=true;
	
	//Need an array of messages in the event the message is too long and
	//flows over the dialog box.
	var messageList=new Array();
	
	//The recursion in fitToDialogX gives us the message backwards. This is
	//used with messageList reverse that.
	var tempList=new Array();
	
	//Padding between text if it overflows
	var padding=1;
	
	var message="";
	
	var hasYesButton=false;
	var hasNoButton=false;
	var hasCloseButton=false;
	
	//Used to grab the handlers passed into the confirmScrim function
	var yesPtr=null;
	var noPtr=null;
	
	var padding=4;
	
	var scrimComponent=new CTATCanvasComponent("CTAT Scrim");
	var scrim=null;
	var dialog=null;
	var scrimMessage=null;
	
	var errorsAndWarnings=new Array();
	
	//See fitTextToDialogX method implementation for more info.
	var estPointToPixel=0;

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
		removeHTMLElements();
	}
	
	/**
	 * Inspects the input dialog text to see if it flows over the
	 * dialog box or not. If it does, then the message is split
	 * up into multiple messages that will instead be placed from
	 * top to bottom. This must be called before fitTextToDialogY.
	 */
	function fitTextToDialogX(dialogText, rightMargin, leftMargin)
	{
		var separatedMessage="";
		
		/**
		 * Converts font points to pixels. No *real* reason why this equation
		 * is the way it is, but it seems to work ok. Only an estimate.
		 */
		var fontPoint=scrimMessage[0].getTextSize();
		estPointToPixel=((fontPoint / 2) + 1);
		
		var textLength = leftMargin+(dialogText.length * estPointToPixel);
		
		if(textLength > rightMargin)
		{
			var textArray=dialogText.split(" ");
			
			for(var i=0; i<textArray.length;i++)
			{
				if(textArray[i]=="")
				{
					textArray.splice(i, (textArray.length-i));
				}
			}
			
			//If the dialogText.split() call returns one element, it is because what is left is
			//just one long token. We need to handle this case differently.
			if(textArray.length==1)
			{
				var t=textArray[0];
				
				for(var i=0; i < t.length; i++)
				{
					textArray[i]=t[i];
				}
			}
			
			
			//Find out where we get cut off, then say the new message is everything
			//after the cutoff point. Call this function again to check if the
			//new message will be able to fit in the dialog box or not.
			
			var textSizeSum=leftMargin;
			var temp="";

			for(var i=0; i < textArray.length; i++)
			{
				textSizeSum += (textArray[i].length * estPointToPixel);

				if((textSizeSum > rightMargin) || (textArray[i]=="\n"))
				{
					//Do not count the newline char, or you will get stack overflow
					if(textArray[i]=="\n") 
					{
						i++;
					}
					
					for(var j=i; j < textArray.length; j++)
					{
						separatedMessage += textArray[j] + " ";
					}

					textArray.splice(i, textArray.length);
					fitTextToDialogX(separatedMessage, rightMargin, leftMargin);
				}

				else
				{
					temp += (textArray[i] + " ");
				}
			}

			tempList.push(temp);
		}
		else
		{
			tempList.push(dialogText);
		}
	}
	
	/**
	 * Stretches the dialog box to fit the entire message, if necessary.
	 * Must call fitTextToDialogX first. Returns the new height.
	 */
	function fitTextToDialogY(y, botMargin, marginSize)
	{
		//How much space the text uses up
		var textSize=y+((estPointToPixel+padding) * (messageList.length+1));
		
		if(textSize > botMargin)
		{
			return (textSize+marginSize);
		}
		
		return (dialog.getHeight()+marginSize);
	}
	/**
	*
	*/	
	function displayDialog(aX, aY, aWidth, aHeight)
	{
		if (platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}	
	
		/**
		 * This should really be left alone. It seems that changing it messes up
		 * the dialog box. If it is crucial that the margin size be changed, it
		 * is probably necessary to re-write this function to be margin-sensitive.
		 */
		var marginRatio=1/8;
		
		var dialogLeftMargin=(aX+(aWidth * marginRatio));
		var dialogRightMargin=(aX+(aWidth-(aWidth * marginRatio)));
		
		var yMarginSize=(aHeight * marginRatio);
		
		var dialogTopMargin=(aY+yMarginSize);
		var dialogBottomMargin=(aY+(aHeight-yMarginSize));

		dialog=new CTATShape("container", 
							 "scrim dialog",
							 aX,
							 aY,
							 aWidth,
							 aHeight);
							 
		dialog.addPoint(0, 0);

		dialog.modifyCanvasCSS("z-index", "998");

		scrimMessage=new Array();
    
    	scrimComponent.addShape(dialog);
    	
       /**
    	* We need the text size to do text wrapping. Since the text uses the same font
    	* for all messages, we can just use a dummy object to extract this.
		*/
		scrimMessage[0]=new CTATShape("container", " ", [0], [0], 0, 0);
		 
		fitTextToDialogX(message, dialogRightMargin, dialogLeftMargin);
		
		scrimMessage[0].detatchCanvas();

		for(var i=tempList.length-1; i >= 0; i--)
		{
			messageList.push(tempList[i]);
		}
		
		dialog.setHeight(fitTextToDialogY(aY, dialogBottomMargin, yMarginSize));

		//If the text was bigger than the box, then the dialog box will need re-centered
		var mainCanvas=getSafeElementById("main-canvas");
		var newY=((mainCanvas.height/2)-(dialog.getHeight()/2));
		scrimComponent.moveShape(dialog.getName(), dialog.getXOffset(), newY);

		dialogTopMargin=(newY+(dialog.getHeight() * marginRatio));
		
		dialog.setLineColor(scrimBorderColor);
		dialog.setFillColor("white");
		dialog.setDrawWidth(scrimBorderWidth);
		dialog.setRadius(5);
		dialog.drawRoundedRectFilled();

		for(var i=0; i < messageList.length; i++)
		{
			var y=(dialogTopMargin+((estPointToPixel+padding)*(i+1)));
		
			scrimMessage[i]=new CTATShape("container", 
							 	   		  ("scrim message "+i),
							 	  		  dialogLeftMargin,
							       		  y,
							       		  aWidth,
							       		  aHeight);
							       		  
			scrimMessage[i].addPoint(0, 0);
		
			scrimMessage[i].modifyCanvasCSS("z-index", "999");       
			scrimMessage[i].setColor("black");
			scrimMessage[i].setTextFont("Verdana");
			scrimMessage[i].setTextSize(14);
			scrimMessage[i].setData(messageList[i]);
			scrimMessage[i].drawText();
			scrimComponent.addShape(scrimMessage[i]);
		}
							 
		if(hasCloseButton==true)
		{
			var buttonSizeY=20;
			var buttonSizeX=40;

			var lastMessage=scrimMessage[messageList.length-1];
			var spaceUsedUp=lastMessage.getYOffset()+lastMessage.getTextSize()+padding;
			
			if((spaceUsedUp + buttonSizeY) > dialogBottomMargin) 
			{
				yMarginSize=((spaceUsedUp+buttonSizeY) * marginRatio);
				dialog.setHeight(spaceUsedUp+buttonSizeY+yMarginSize+scrimBorderWidth);
				var placement = ((mainCanvas.height/2) - (dialog.getHeight()/2));
				
				scrimComponent.moveShape(dialog.getName(), dialog.getXOffset(), placement);
				
				dialog.setLineColor(scrimBorderColor);
				dialog.setFillColor("white");
				dialog.setDrawWidth(scrimBorderWidth);
				dialog.setRadius(5);
				dialog.drawRoundedRectFilled();
			}
			
			closeButton=makeHTMLButton(closeFunction, (aX+(dialog.getWidth()/2)-(buttonSizeX/2)), 
			                           spaceUsedUp, "close");
		}
		
		if(hasYesButton==true)
		{
			var buttonSizeY=20;
			var buttonSizeX=40;

			var lastMessage=scrimMessage[messageList.length-1];
			var spaceUsedUp=lastMessage.getYOffset()+lastMessage.getTextSize()+padding;
			
			if((spaceUsedUp + buttonSizeY) > dialogBottomMargin) 
			{
				yMarginSize=((spaceUsedUp+buttonSizeY) * marginRatio);
				dialog.setHeight(spaceUsedUp+buttonSizeY+yMarginSize+scrimBorderWidth);
				var placement = ((mainCanvas.height/2) - (dialog.getHeight()/2));
				
				scrimComponent.moveShape(dialog.getName(), dialog.getXOffset(), placement);
				
				dialog.setLineColor(scrimBorderColor);
				dialog.setFillColor("white");
				dialog.setDrawWidth(scrimBorderWidth);
				dialog.setRadius(5);
				dialog.drawRoundedRectFilled();
			}
			
			yesButton=makeHTMLButton(yesPtr, (aX+(dialog.getWidth()/2)-buttonSizeX), 
			                         spaceUsedUp, "yes");
		}
		
		if(hasNoButton==true)
		{
			var buttonSizeY=20;
			var buttonSizeX=40;

			var lastMessage=scrimMessage[messageList.length-1];
			var spaceUsedUp=lastMessage.getYOffset()+lastMessage.getTextSize()+padding;
			
			if((spaceUsedUp + buttonSizeY) > dialogBottomMargin) 
			{
				yMarginSize=((spaceUsedUp+buttonSizeY) * marginRatio);
				dialog.setHeight(spaceUsedUp+buttonSizeY+yMarginSize+scrimBorderWidth);
				var placement = ((mainCanvas.height/2) - (dialog.getHeight()/2));
				
				scrimComponent.moveShape(dialog.getName(), dialog.getXOffset(), placement);
				
				dialog.setLineColor(scrimBorderColor);
				dialog.setFillColor("white");
				dialog.setDrawWidth(scrimBorderWidth);
				dialog.setRadius(5);
				dialog.drawRoundedRectFilled();
			}
			
			noButton=makeHTMLButton(noPtr, (aX+(dialog.getWidth()/2)), 
			                        spaceUsedUp, "no");
		}
		
		if(waitScrim==true)
		{
			var spaceUsedUp=(newY+((messageList.length+1) * (estPointToPixel + padding)));
		
			//The gif we use is 64x64
			var waitGIFsize=64;
			var gifMid=(aX+(dialog.getWidth()/2)-(waitGIFsize/2));
			
			//If the gif is too big, re-size and re-position
			if((spaceUsedUp+waitGIFsize) > dialogBottomMargin)
			{
				yMarginSize=((spaceUsedUp+waitGIFsize) * marginRatio);
				dialog.setHeight(spaceUsedUp+waitGIFsize+yMarginSize);
				var placement = ((mainCanvas.height/2) - (dialog.getHeight()/2));
				
				scrimComponent.moveShape(dialog.getName(), dialog.getXOffset(), placement);
				
				dialog.setLineColor(scrimBorderColor);
				dialog.setFillColor("white");
				dialog.setRadius(5);
				dialog.drawRoundedRectFilled();
			}
		
			makeHTMLImage(rotatingLoad, gifMid, (spaceUsedUp+dialog.getTextSize()+padding));
		}
	}
	/**
	*
	*/	
	function drawScrim()
	{
		if (platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}	
		
		//Needed to adjust the scrim over a relatively positioned canvas
		var mainCanvas=getSafeElementById("main-canvas");

		scrim=new CTATShape("container", 
							"scrim",
							0,
							0,
							mainCanvas.width+1,
							mainCanvas.height+1);
							
		scrim.addPoint(0, 0);

		scrim.modifyCanvasCSS("z-index", "998");

		scrimComponent.addShape(scrim);
		
		scrim.setColor(scrimColor);
		scrim.drawRectangleFilled();
		
		var dialogWidth=mainCanvas.width * dialogWidthRatio;
		var dialogHeight=mainCanvas.height * dialogHeightRatio;
		
		var dialogX=((mainCanvas.width/2)-(dialogWidth/2));
		var dialogY=((mainCanvas.height/2)-(dialogHeight/2));

		displayDialog(dialogX, dialogY, dialogWidth, dialogHeight);
	}
	/**
	*
	*/	
	this.scrimUp=function scrimUp(aMessage)
	{
		if (platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}	
		
		pointer.debug("scrimUp ()");
		pointer.debug("Message: " + aMessage);

		if(scrimIsUp==true)
		{
			if(waitScrim==true)
			{
				message=aMessage;
			}
			
			else
			{
				message += " \n " + aMessage;
			}
			
			//Only need one scrim up at a time
			scrimComponent.removeComponent();
			messageList=new Array();
			tempList=new Array();
			removeHTMLElements();
		}
		
		else
		{
			message=aMessage;
			scrimIsUp=true;
		}
		
		drawScrim();
	};
	/**
	*
	*/	
	this.waitScrimUp=function waitScrimUp()
	{
		if (platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}	
		
		pointer.debug("waitScrimUp ()");
	
		waitScrimUp=true;
		scrimBorderColor=defaultColor;
	
		pointer.scrimUp("Please wait while the tutor is being loaded");
	};
	/**
	*
	*/	
	this.nextProblemScrimUp=function nextProblemScrimUp()
	{
		if (platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}		
	
		pointer.debug("nextProblemScrimUp ()");
	
		waitScrimUp=true;
		scrimBorderColor=defaultColor;
	
		pointer.scrimUp("Retrieving the Next Problem...");
	};	
	/**
	*
	*/	
	this.OKScrimUp=function OKScrimUp(aMessage, aFunction)
	{
		if (platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}	
		
		pointer.debug("OKScrimUp ("+aMessage+","+aFunction+")");
	
		scrimBorderColor="black";
		pointer.scrimUp(aMessage);
		
		var buttonSizeY=10;
		var buttonSizeX=20;
		
		var marginRatio=1/8;
		var yMarginSize=(dialog.getHeight() * marginRatio);
		var dialogBottomMargin=(dialog.getYOffset()+(dialog.getHeight()-yMarginSize));

		var lastMessage=scrimMessage[messageList.length-1];
		var spaceUsedUp=lastMessage.getYOffset(0)+lastMessage.getTextSize()+padding;
		
		var x=(dialog.getXOffset()+(dialog.getWidth()/2)-(buttonSizeX/2));
			
		if((spaceUsedUp + buttonSizeY) > dialogBottomMargin) 
		{
			yMarginSize=((spaceUsedUp+buttonSizeY) * marginRatio);
			dialog.setHeight(spaceUsedUp+buttonSizeY+yMarginSize+scrimBorderWidth);
			var placement=((mainCanvas.height/2) - (dialog.getHeight()/2));
				
			scrimComponent.moveShape(dialog.getName(), dialog.getXOffset(), placement);
				
			dialog.setLineColor(scrimBorderColor);
			dialog.setFillColor("white");
			dialog.setDrawWidth(scrimBorderWidth);
			dialog.setRadius(5);
			dialog.drawRoundedRectFilled();
		}
			
		okButton=makeHTMLButton(aFunction, x, spaceUsedUp, "OK");
	};
	/**
	*
	*/	
	this.confirmScrimUp=function confirmScrimUp(prompt, onYes, onNo)
	{
		if (platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}	
		
		pointer.debug("confirmScrimUp ("+prompt+","+onYes+","+onNo+")");
	
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
		if (platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}	
		
		pointer.debug("errScrimUp ("+aMessage+")");
	
		if(errorScrim==true)
		{
			scrimBorderColor=errorColor;
			
			if(hasCloseButton==true)
			{
				hasCloseButton=false;
				getSafeElementById("container").removeChild(closeButton);
			}
		}
		
		else if(warnScrim==true)
		{
			scrimBorderColor=warningColor;
			hasCloseButton=true;
		}

		pointer.scrimUp(aMessage);
	};
	/**
	*
	*/	
	this.errorScrimUp=function errorScrimUp(aMessage)
	{
		if (platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}	
		
		if(authorTimeSet==false)
		{
			pointer.debug("we don't know if we're in authorTime or not yet so we're just going to hold onto the messasge");
			errorsAndWarnings.push("ERROR: "+aMessage);
			errorScrim=true;
			return;
		}
		
		else if(inAuthorTime==false)
		{
			pointer.debug("We're not in authorTime, student's don't need to see our design mistakes");
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
		if (platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}	
		
		if(authorTimeSet==false)
		{
			pointer.debug("we don't know if we're in authorTime or not yet so we're just going to hold onto the messasge");
			errorsAndWarnings.push("WARNING: "+aMessage);
			warnScrim=true;
			return;
		}
		
		else if(inAuthorTime==false)
		{
			pointer.debug("We're not in authorTime, student's don't need to see our design mistakes");
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
		pointer.scrimUp("The tutor has disconnected. Please refresh the page.");
	};
	/**
	*
	*/	
	function removeHTMLElements()
	{
		var topDiv=getSafeElementById('container');

		if(img != null)
		{
			topDiv.removeChild(img);
			img=null;
		}
		
		if(yesButton != null)
		{
			topDiv.removeChild(yesButton);
			yesButton=null;
		}
		
		if(noButton != null)
		{
			topDiv.removeChild(noButton);
			noButton=null;
		}
		
		if(closeButton != null)
		{
			topDiv.removeChild(closeButton);
			closeButton=null;
		}
		
		if(okButton != null)
		{
			topDiv.removeChild(okButton);
			okButton=null;
		}
	}
	/**
	*
	*/
	this.scrimDown=function scrimDown()
	{
		if (platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}
		
		if (scrimIsUp==false) 
		{
			pointer.debug("The scrim isn't up, returning");
			return;
		}
			
		if (errorScrim==true) 
		{
			pointer.debug("The scrim is up to describe errors to the user, leave it up!");
			return;
		}
			
		if (warnScrim==true) 
		{
			pointer.debug("The scrim is up to display warnings to the user, leave it up!");
			return;
		}
			
		if (connectionScrim==true) 
		{
			pointer.debug("The scrim is up for a connection issue leave it up!");
			return;
		}
		/*
		if (waitForClick) 
		{
			pointer.debug("The scrim is waiting on user input, leave it up!");
			return;
		}*/
		
		scrimComponent.removeComponent();
		removeHTMLElements();
		scrimIsUp=false;
		waitScrim=false;
		message="";
		messageList=new Array();
		tempList=new Array();
	};
	
	//At the moment, calling this assumes the start state was read in.
	this.resizeScrim=function resizeScrim(newWidth, newHeight)
	{
		if (platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}	
		
		scrimComponent.removeComponent();
		removeHTMLElements();
		
		//Since we are re-rendering, we need to re-fit the message to the dialog
		messageList=new Array();
		tempList=new Array();

		drawScrim();
	};
	
	function makeHTMLImage(anImage, aX, aY)
	{
		if (platform=="google")
		{
			// This is not available for now when we're in this environment
			return;
		}	
		
		img=new Image();
		img.src=anImage;
		img.id='load gif';
		img.setAttribute('style', "left: "+aX+"px; top: "+aY+"px; z-index: 999; position: absolute;");
		
		getSafeElementById("container").appendChild(img);
	}
	
	/**
	*
	*/
	this.defaultClickHandler=function defaultClickHandler ()
	{
		pointer.debug("defaultClickHandler ()");
		pointer.scrimDown();
	};	
	
	/**
	*
	*/	
	function makeHTMLButton(clickHandle, aX, aY, btnType)
	{
		pointer.debug("makeHTMLButton ()");
	
		btn=document.createElement('input');
		btn.type='button';				
		btn.value=btnType;
		
		if (clickHandle!=null)
		{
			pointer.debug("clickHandle!=null");
		
			btn.onclick=clickHandle;
		}
		else
		{
			pointer.debug("clickHandle==null");
		
			btn.onclick=pointer.defaultClickHandler;
		}
				
		//Is it a yes, no, play, etc button
		btn.id=btnType;
		
		btn.setAttribute('style', "left: "+aX+"px; top: "+aY+"px; z-index: 999; position: absolute;");
		
		getSafeElementById("container").appendChild(btn);
		
		return (btn);
	}
		
	/**
	*
	*/	
	this.setInAuthorTime=function setInAuthorTime(theValue)
	{
		pointer.debug("setting inAuthorTime = " +theValue);
		
		authorTimeSet=true;
		authorTime=theValue;
		
		if (!theValue) 
		{
			errorScrim=false;
			warnScrim=false;
			
			if(hasCloseButton)
			{
				scrim.removeChild(closeButton);
			}
			
			scrimBorderColor=defaultColour;
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
}

CTATScrim.prototype = Object.create(CTATBase.prototype);
CTATScrim.prototype.constructor = CTATScrim;