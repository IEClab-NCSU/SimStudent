/**-----------------------------------------------------------------------------
 $Author: mdb91 $
 $Date: 2016-11-28 17:24:35 -0600 (週一, 28 十一月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATAudioButton.js $
 $Revision: 24393 $

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
 */
goog.provide('CTATAudioButton');

goog.require('CTATButtonBasedComponent');
goog.require('CTATCommShell');
goog.require('CTATGlobalFunctions');
//goog.require('CTATGlobals');
goog.require('CTATSAI');
goog.require('CTAT.ComponentRegistry');
/**
 *
 */
CTATAudioButton = function(aDescription,
		aX,
		aY,
		aWidth,
		aHeight) {
	CTATButtonBasedComponent.call(this,
			"CTATAudioButton",
			"audiobutton",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	var pointer=this;

	pointer.setActionInput("play","-1");

	var audioElement=null;
	var lastCommand="";
	var previewMode = CTATConfiguration.get('previewMode');
	/**
	 * Interface Action to set the source url.
	 * @param {String} aUrl	The url of an audio file.
	 */
	this.setSource = function (aUrl) {
		this.getDivWrap().setAttribute('data-ctat-src', aUrl);
		return this;
	};
	/**
	 * Get the current audio file from the data-ctat-src attribute.
	 * @returns {String} a url.
	 */
	this.getSource = function () {
		return this.getDivWrap().getAttribute('data-ctat-src');
	};
	this.setParameterHandler('SoundFile', this.setSource);
	//this.data_ctat_handlers['src'] = function(aUrl) { soundFile=aUrl; };

	/**
	 *
	 */
	this.init=function init()
	{
		var button=document.createElement('button');
		button.value='-1';
		button.name=pointer.getName(); // might be wrong
		button.setAttribute('id', CTATGlobalFunctions.gensym.div_id());
		button.setAttribute('onkeypress', 'return noenter(event)');
		button.classList.add('CTAT-button');
		if (this.getDivWrap().getAttribute('data-ctat-label'))
			button.textContent = this.getDivWrap().getAttribute('data-ctat-label');
		else if (pointer.getText())
			button.textContent = pointer.getText();
		else if (pointer.getDivWrap() && pointer.getDivWrap().innerHTML && !previewMode) {
			var insides = pointer.getDivWrap().innerHTML;
			pointer.getDivWrap().innerHTML = '';
			button.innerHTML = insides;
		}

		pointer.setInitialized(true);

		pointer.setComponent(button);
		pointer.addComponentReference(pointer, button);
		pointer.getDivWrap().appendChild(button);

		pointer.ctatdebug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());
		
		if (!previewMode)
		{
			button.addEventListener ('click', pointer.processClick);
			button.addEventListener ('focus', pointer.processFocus);
		}
		// setup audio controls ...

		audioElement=document.createElement('audio');

		audioElement.addEventListener("canplay", function()	{
			pointer.ctatdebug ("Audio loaded and ready for play");
			//pointer.setEnabled (pointer.getEnabled());

			if (lastCommand!=="")
			{
				pointer.logAudioEvent (lastCommand);
				lastCommand="";
			}

		}, true);

		audioElement.onended = function() {
			pointer.ctatdebug ("Audio ended");

			pointer.logAudioEvent ("end");
		};
		this.getDivWrap().appendChild(audioElement);
	};

	/**
	 * This is run during the generation of InterfaceDescription messages and
	 * it generates interface actions for options set by the author in the
	 * html code.
	 * @returns {Array<CTATSAI>} of SAIs.
	 */
	this.getConfigurationActions = function () {
		var actions = [];
		var sai;
		if (this.component.innerHTML.trim().length>0) {
			sai = new CTATSAI();
			sai.setSelection(this.getName());
			sai.setInput(this.component.innerHTML.toString());
			sai.setAction('setText');
			actions.push(sai);
		}
	    if (this.getSource().trim().length>0) {
			sai = new CTATSAI();
			sai.setSelection(this.getName());
	    	sai.setInput(this.getSource());
	    	sai.setAction('setSource');
	    	actions.push(sai);
	    }
	    return actions;
	};
	/**
	 *
	 */
	this.reset=function reset ()
	{
		pointer.configFromDescription();
		pointer.processSerialization();
		pointer.setEnabled(true);

		lastCommand="reset";
	};

	/**
	 *
	 */
	this.logAudioEvent=function logAudioEvent (eventName)
	{
		pointer.ctatdebug ("logAudioEvent ("+eventName+")");

		if (audioElement.duration!==undefined)
		{
			if (CTATCommShell.commShell!==null)
			{
				var audioSAI = new CTATSAI (pointer.getName (),
											eventName,
											pointer.toHHMMSS (audioElement.currentTime)+".000",
											"");
				audioSAI.addSelection (encodeURIComponent (audioElement.currentSrc),"media_file");
				audioSAI.addSelection (pointer.toHHMMSS (audioElement.duration)+".000","clip_length");

				var tempInput=audioSAI.getInputObject ();
				if (tempInput!==null)
				{
					tempInput.setType ("time");
				}

				CTATCommShell.commShell.processComponentAction (audioSAI,false,true,pointer,"AUDIO_ACTION","USER");
			}
			else
			{
				pointer.ctatdebug ("Error: commShell is null, can't send untutored tool message");
			}
		}
		else
		{
			pointer.ctatdebug ("Error: audio file not loaded yet, can't obtain duration");
		}
	};

	/**
	 *
	 * @param e
	 */
	this.processClick=function processClick (e)
	{
		pointer.ctatdebug ("processClick ("+e.currentTarget.getAttribute ("id")+" -> "+e.eventPhase+")");

		if (pointer.getEnabled()===true)
		{
			var soundFile = this.getSource();
			pointer.ctatdebug ("Playing audio file: " + soundFile);

			audioElement.setAttribute('src', soundFile);

			//audioElement.load();

			audioElement.play();

			// Process a tool action signifying that audio has started playing. This will
			// also result in a needed tool log message

			lastCommand="play";

			// Process the button press

			pointer.processAction();
		}
		else
			pointer.ctatdebug ("Component is disabled, not grading");
	}.bind(this);

	/**
	 * An Interface Action for starting the audio.
	 */
	this.play = function ()
	{
		pointer.ctatdebug ("play");

		var soundFile = this.getSource();
		audioElement.setAttribute('src', soundFile);
		audioElement.load();
		audioElement.play();

		lastCommand="play";
	};
	/**
	 * An Interface Action for playing the specified audio.
	 * @param {string} aURL	A url pointing to an audio file.
	 */
	this.playClip = function (aURL)
	{
		pointer.ctatdebug ("playClip");

		//this.getDivWrap().setAttribute('data-ctat-src',aURL); // AS3 does not set the clip.
		audioElement.setAttribute('src', aURL);
		audioElement.load();
		audioElement.play();

		lastCommand="play";
	};
	/**
	 * An Interface Action for pausing the audio.
	 */
	this.pause = function ()
	{
		pointer.ctatdebug ("pause");

		audioElement.pause();

		lastCommand="pause";
	};
};

CTATAudioButton.prototype = Object.create(CTATButtonBasedComponent.prototype);
CTATAudioButton.prototype.constructor = CTATAudioButton;

CTAT.ComponentRegistry.addComponentType('CTATAudioButton',CTATAudioButton);