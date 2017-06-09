/**-----------------------------------------------------------------------------
 $Author: sewall $
 $Date: 2016-11-19 13:05:13 -0600 (週六, 19 十一月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATComponents/CTATVideo.js $
 $Revision: 24380 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:


 http://dev.opera.com/articles/view/custom-html5-video-player-with-css3-and-jquery/

 */
goog.provide('CTATVideo');

goog.require('CTATCommShell');
goog.require('CTATGlobalFunctions');
//goog.require('CTATGlobals');
goog.require('CTAT.Component.Base.Tutorable');
goog.require('CTAT.ComponentRegistry');
/**
 *
 */
CTATVideo = function(aDescription,aX,aY,aWidth,aHeight)
{
	CTAT.Component.Base.Tutorable.call(this,
			"CTATVideo",
			"__undefined__",
			aDescription,
			aX,
			aY,
			aWidth,
			aHeight);

	//var hints=[];
	var alpha=0.0;
	var pointer=this;
	var video=null;
	var lastCommand="";

	this.getAlpha=function getAlpha()
	{
		return (alpha);
	};

	this.setAlpha=function setAlpha(aAlpha)
	{
		alpha=aAlpha;
	};

	this.ctatdebug (this.getClassName() + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	// By default this component doesn't grade
	this.setTutorComponent (false);

	this.configFromDescription ();

	/**
	 *
	 */
	this.init=function init()
	{
		pointer.ctatdebug("init (" + pointer.getName() + ")");

		pointer.addCSSAttribute("z-index", CTATGlobalFunctions.gensym.z_index());

		video=document.createElement('video');

		if (this.getDivWrap() && $(this.getDivWrap()).attr('src'))
		{
			video.src = $(this.getDivWrap()).attr('src');
		}
		else
		{
			video.src="http://augustus.pslc.cs.cmu.edu/ProportionalDistanceExercise.mp4";
		}

		if (this.getDivWrap() && $(pointer.getDivWrap()).attr('data-ctat-controls'))
		{
			video.controls = CTATGlobalFunctions.toBoolean($(this.getDivWrap()).attr('data-ctat-controls'));
		}
		else
		{
			video.controls=true;
		}

		video.autocontrols=false; // doesn't work

		pointer.ctatdebug ("Auto play: " + $(pointer.getDivWrap()).attr('data-ctat-autoplay'));

		if (this.getDivWrap() && $(pointer.getDivWrap()).attr('data-ctat-autoplay'))
		{
			video.autoplay = CTATGlobalFunctions.toBoolean($(this.getDivWrap()).attr('data-ctat-autoplay'));
		}
		else
		{
			video.autoplay=true;
		}

		pointer.setComponent(video);

		pointer.ctatdebug ("Auto play: " + video.autoplay);

		video.name=pointer.getName();
		video.setAttribute('id', CTATGlobalFunctions.gensym.div_id());
		video.setAttribute('onkeypress', 'return noenter(event)');

		pointer.addComponentReference(pointer, video);

		pointer.ctatdebug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

		pointer.setInitialized(true);
		pointer.getDivWrap().appendChild(video);

		pointer.ctatdebug ("Resizing component to: " + $(pointer.getDivWrap()).width () +","+$(pointer.getDivWrap()).height ());

		video.addEventListener('focus', this.processFocus);
		video.addEventListener("canplay", function() {
			pointer.ctatdebug ("Video loaded and ready for play");
		}, true);
		video.addEventListener("play", function() {
				pointer.ctatdebug ("Video loaded and ready for play");
				pointer.logVideoEvent ("play");
		}, true);
		video.addEventListener("pause", function() {
			pointer.ctatdebug ("Video loaded and ready for play");
			pointer.logVideoEvent ("pause");
		}, true);
		video.setAttribute('width', $(pointer.getDivWrap()).width ());
		video.setAttribute('height', $(pointer.getDivWrap()).height ());
	};

	this.getConfigurationActions = function () {
		var actions = [];
		var sai;
		if ($(pointer.getDivWrap()).attr('data-ctat-controls')) {
			sai = new CTATSAI();
			sai.setSelection(this.getName());
			sai.setAction('showControls');
			sai.setInput($(pointer.getDivWrap()).attr('data-ctat-controls'));
			actions.push(sai);
		}
		if ($(pointer.getDivWrap()).attr('data-ctat-autoplay')) {
			sai = new CTATSAI();
			sai.setSelection(this.getName());
			sai.setAction('setAutoplay');
			sai.setInput($(pointer.getDivWrap()).attr('data-ctat-autoplay'));
			actions.push(sai);
		}
		if ($(this.getDivWrap()).attr('src')) {
			sai = new CTATSAI();
			sai.setSelection(this.getName());
			sai.setAction('setURL');
			sai.setInput($(this.getDivWrap()).attr('src'));
			actions.push(sai);
		}
		return actions;
	};

	/**
	 *
	 */
	this.logVideoEvent=function logVideoEvent (eventName)
	{
		pointer.ctatdebug ("logVideoEvent ("+eventName+")");

		if (video.duration!==undefined)
		{
			if (CTATCommShell.commShell!==null)
			{
				var audioSAI = new CTATSAI (pointer.getName (),
						eventName,
						pointer.toHHMMSS (video.currentTime)+".000",
						"");
				audioSAI.addSelection (encodeURIComponent (video.currentSrc),"media_file");
				audioSAI.addSelection (pointer.toHHMMSS (video.duration)+".000","clip_length");

				var tempInput=audioSAI.getInputObject ();
				if (tempInput!==null)
				{
					tempInput.setType ("time");
				}

				CTATCommShell.commShell.processComponentAction (audioSAI,pointer.getTutorComponent,true,pointer,"VIDEO_ACTION","USER");
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
	 * An Interface Action for loading the specified video.
	 * @param {string} aURL	a valid url to a video.
	 */
	this.PlayMedia = function (aURL)
	{
		video.src = aURL;

		lastCommand="play";
		//pointer.logVideoEvent ("play");
	};
	/**
	 * An Interface Action for displaying or hiding the controls.
	 * @param {boolean} show	true: show the controls, false: hide controls.
	 */
	this.showControls = function (show)
	{
		show = CTATGlobalFunctions.toBoolean(show);
		$(pointer.getDivWrap()).attr('data-ctat-controls',show);
		video.controls=show;
	};
	/**
	 * An Interface Action for setting the autoplay parameter.
	 * @param {boolean} play	true: enable autoplay.
	 */
	this.setAutoplay = function (play) {
		play = CTATGlobalFunctions.toBoolean(play);
		$(pointer.getDivWrap()).attr('data-ctat-autoplay',play);
		video.autoplay=play;
	};
	/**
	 * An Interface Action for setting the src attribute.
	 * @param {String} aURL	the new url of the video to show.
	 */
	this.setURL = function (aURL) {
		$(this.getDivWrap()).attr('src',aURL);
		video.src = aURL;
	};

	/**
	 * An Interface Action for starting to play the video at a given time.
	 * @param {string|number} playtime	a time reference in seconds.
	 */
	this.play = function (playtime)
	{
		pointer.ctatdebug ("play ()");

		var starttime = "0";
		if (playtime) {
			if (typeof(playtime)==='number') {
				starttime = String(playtime);
			} else if (typeof(playtime)==='string') {
				starttime = playtime;
			} else {
				starttime = "0";
			}
		}
		video.currentTime = starttime;
		video.play();

		lastCommand="play";
	};
	/**
	 * An Interface Action for pausing the video at a given time.
	 * @param {string|number} playtime	a time reference in seconds.
	 */
	this.pause = function (playtime)
	{
		pointer.ctatdebug ("pause ()");

		video.pause();
		var starttime = "0";
		if (playtime) {
			if (typeof(playtime)==='number') {
				starttime = String(playtime);
			} else if (typeof(playtime)==='string') {
				starttime = playtime;
			} else {
				starttime = "0";
			}
		}
		video.currentTime=starttime;

		lastCommand="pause";
		//pointer.logVideoEvent ("pause");
	};
	/**
	 * An Interface Action for pausing the video at a given time.
	 * @param {string|number} playtime	a time reference in seconds.
	 * @see CTATVideo.pause
	 */
	this.stop = function ()
	{
		pointer.ctatdebug ("stop ()");

		lastCommand="stop";
		pointer.pause ();
		//pointer.logVideoEvent ("stop");
	};
};

CTATVideo.prototype = Object.create(CTAT.Component.Base.Tutorable.prototype);
CTATVideo.prototype.constructor = CTATVideo;

CTAT.ComponentRegistry.addComponentType('CTATVideo',CTATVideo);
