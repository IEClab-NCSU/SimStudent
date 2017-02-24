/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATAudioButton.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
/**-----------------------------------------------------------------------------
*
* http://stackoverflow.com/questions/8179585/playing-audio-on-ipad
*
* Reference implementation:
*
* $(document).ready(function()
{
	var audioElement = document.createElement('audio');
	audioElement.setAttribute('src', 'Mogwai2009-04-29_acidjack_t16.ogg');
	audioElement.load()

	audioElement.addSafeEventListener("load", function()
	{
		audioElement.play();
		$(".duration span").html(audioElement.duration);
		$(".filename span").html(audioElement.src);
	}, true);

	$('.play').click(function()
	{
		audioElement.play();

	});

	$('.pause').click(function()
	{
		audioElement.pause();
	});

	$('.volumeMax').click(function()
	{
		audioElement.volume=1;
	});

	$('.volumestop').click(function()
	{
		audioElement.volume=0;
	});

	$('.playatTime').click(function()
	{
		audioElement.currentTime= 35;
		audioElement.play();
	});
});
*/




/**
  Notes:

  Events: click, mousemove, mouseover, mouseout, keyup, keydown,
  		  focus, blur, select, load

  CSS: http://tutobx.com/post/24806696944/raised-and-pressed-div-using-css
       http://stackoverflow.com/questions/5662178/opacity-of-divs-background-without-affecting-contained-element-in-ie-8

  Js:  http://www.quirksmode.org/js/this.html
       http://unschooled.org/2012/03/understanding-javascript-this/

  CTAT:

 		[48] [07:14:14] [CTATTextField] Processing style labelTextValue,
		[49] [07:14:14] [CTATTextField] Processing style inspBackgroundColor,ffffff
		[50] [07:14:14] [CTATTextField] Processing style inspBorderColor,999999
		[51] [07:14:14] [CTATTextField] Processing style inspFontName,Arial
		[52] [07:14:14] [CTATTextField] Processing style inspFontSize,20
		[53] [07:14:14] [CTATTextField] Processing style inspFontColor,0
		[54] [07:14:14] [CTATTextField] Processing style inspBold,FALSE
		[55] [07:14:14] [CTATTextField] Processing style inspItalic,FALSE
		[56] [07:14:14] [CTATTextField] Processing style inspUnderline,FALSE
		[57] [07:14:14] [CTATTextField] Processing style inspAlignment,left
		[58] [07:14:14] [CTATTextField] Processing style inspShowHintHighlight,true
		[59] [07:14:14] [CTATTextField] Processing style blockOnCorrect,true
		[60] [07:14:14] [CTATTextField] Processing style _tutorComponent,Tutor
		[61] [07:14:14] [CTATTextField] Processing style disabledBackgroundColor,ffffff
		[62] [07:14:14] [CTATTextField] Processing style disabledTextColor,0
		[63] [07:14:14] [CTATTextField] Processing style tutorComponent,Tutor
*/
goog.provide('CTATAudioButton');

goog.require('CTATButtonBasedComponent');
goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTAT.ComponentRegistry');
/**
 *
 */
CTATAudioButton = function(aDescription,
					 aX,
					 aY,
					 aWidth,
					 aHeight)
{
	CTATButtonBasedComponent.call(this,
								  "CTATAudioButton",
								  "audiobutton",
								  aDescription,
								  aX,
								  aY,
								  aWidth,
								  aHeight);

	var pointer=this;
	var borderRoundness=5;
	var buttonText="";

	pointer.setActionInput("play","-1");

	var audioElement=null;
	var soundFile="";

	this.ctatdebug ("CTATAudioButton" + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	this.configFromDescription();

	/**
	 *
	 */
	this.init=function init()
	{
	    pointer.ctatdebug("init (" + pointer.getName() + ")");
	    pointer.setSAI (pointer.getName(),"ButtonPressed","-1");

	    pointer.addCSSAttribute("z-index", CTATGlobalFunctions.gensym.z_index());
		pointer.addCSSAttribute("width", pointer.getWidth()+"px");
		pointer.addCSSAttribute("height", pointer.getHeight()+"px");
		pointer.render ();

		var button=document.createElement('button');
		button.value=pointer.getName();
	    button.name=pointer.getName(); // might be wrong
	    button.setAttribute('id', CTATGlobalFunctions.gensym.div_id());
	    button.setAttribute('onkeypress', 'return noenter(event)');

	    pointer.setInitialized(true);

	    pointer.setComponent(button);
	    pointer.addComponentReference(pointer, button);
	    pointer.getDivWrap().appendChild(button);

		//useDebgging=true;
	    pointer.ctatdebug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());
		//useDebugging=false;

	    //currentZIndex++;
	    //currentIDIndex++;

	    pointer.addSafeEventListener ('click',pointer.processClick,button);
	    pointer.addSafeEventListener ('focus', pointer.processFocus,button);

		// setup audio controls ...

		audioElement=document.createElement('audio');

		audioElement.addEventListener("load", function()
		{
			pointer.ctatdebug ("Audio loaded");

			pointer.setEnabled (true);
		}, true);
	};

	/**
	 *
	 */
	this.reset=function reset ()
	{
		pointer.configFromDescription();
		pointer.processSerialization();
		pointer.setEnabled(true);
	};

	/**
	 *
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.ctatdebug ("processSerialization()");

		// Process component specific pre-defined styles ...
		if (this.label!=null)
		{
			pointer.setText (this.label);
		}

		// Process component parameters ...

		var parameters=pointer.getGrDescription().parameters;

		for(var j=0;j<this.parameters.length;j++)
		{
			var aParam=this.parameters [j];

			pointer.ctatdebug ("Checking parameter name: " + aParam.paramName);

			if(aParam.paramName=="SoundFile")
			{
				pointer.ctatdebug ("Setting sound file to: " + aParam.paramValue);

				soundFile=aParam.paramValue;
			}

			if(aParam.paramName=="labelText")
			{
				pointer.setText(aParam.paramValue);
			}
		}

		// Process component custom styles ...

		this.styles=pointer.getGrDescription().styles;

		pointer.ctatdebug ("Processing " + this.styles.length + " styles ...");

		for (var i=0;i<this.styles.length;i++)
		{
			var aStyle=this.styles [i]; // CTATStyle

			if (aStyle.styleName=="labelText")
			{
				pointer.setText(aStyle.styleValue);
			}

			if(aStyle.styleName=="borderRoundness")
			{
				borderRoundness=aStyle.styleValue;
				pointer.addCSSAttribute("border-radius", borderRoundness+"px");
			}
		}
	};

	/**
	 * TPA
	 */
	 this.ButtonPressed=function ButtonPressed ()
	 {
	 	pointer.ctatdebug ("ButtonPressed ()");
	 };

	/**
	 *
	 * @param e
	 */
	this.processClick=function processClick (e)
	{
		//useDebugging=true;

		pointer.ctatdebug ("processClick ("+e.currentTarget.getAttribute ("id")+" -> "+e.eventPhase+")");

		if (pointer.getEnabled()==true)
		{
			pointer.ctatdebug ("Playing audio file: " + soundFile);

			audioElement.setAttribute('src', soundFile);

			audioElement.load();

			audioElement.play();

			pointer.grade();
		}
		else
			pointer.ctatdebug ("Component is disabled, not grading");

		//useDebugging=false;
    };

    /**
     * An Interface Action for starting the audio.
     */
    this.play = function () {
		audioElement.setAttribute('src', soundFile);
		audioElement.load();
		audioElement.play();
    };
    /**
     * An Interface Action for playing the specified audio.
     * @param {string} aURL	A url pointing to an audio file.
     */
    this.playClip = function (aURL) {
		audioElement.setAttribute('src', aURL);
		audioElement.load();
		audioElement.play();
    };
    /**
     * An Interface Action for pausing the audio.
     */
    this.pause = function () {
    	audioElement.pause();
    };
};

CTATAudioButton.prototype = Object.create(CTATButtonBasedComponent.prototype);
CTATAudioButton.prototype.constructor = CTATAudioButton;

CTAT.ComponentRegistry.addComponentType('CTATAudioButton',CTATAudioButton);