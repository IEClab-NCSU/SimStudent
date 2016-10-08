/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2014-12-17 16:05:08 -0500 (Wed, 17 Dec 2014) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5/src/CTATComponents/CTATVideo.js $
 $Revision: 21689 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:


 http://dev.opera.com/articles/view/custom-html5-video-player-with-css3-and-jquery/

 */
goog.provide('CTATVideo');

goog.require('CTATGlobalFunctions');
goog.require('CTATGlobals');
goog.require('CTATTutorableComponent');
goog.require('CTAT.ComponentRegistry');
/**
 *
 */
CTATVideo = function(aDescription,aX,aY,aWidth,aHeight)
{
	CTATTutorableComponent.call(this,
					  			 "CTATVideo",
					  			 "__undefined__",
					  			 aDescription,
					  			 aX,
					  			 aY,
					  			 aWidth,
					  			 aHeight);

	var hints=new Array ();
	var alpha=0.0;
	var pointer=this;
	var video=null;

	var skillSet=new Array ();

	this.getAlpha=function getAlpha()
	{
		return (alpha);
	};

	this.setAlpha=function setAlpha(aAlpha)
	{
		alpha=aAlpha;
	};

	this.ctatdebug (this.getClassName() + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	this.configFromDescription ();

	this.init=function init()
	{
	    pointer.ctatdebug("init (" + pointer.getName() + ")");

	    pointer.addCSSAttribute("z-index", CTATGlobalFunctions.gensym.z_index());

	    video=document.createElement('video');
	    //pointer.getComponent().title=pointer.getName();
	    video.src="http://augustus.pslc.cs.cmu.edu/ProportionalDistanceExercise.mp4";
	    video.controls=true;
	    video.autocontrols=false; // doesn't work
	    video.autoplay=true;
	    video.name=pointer.getName();
	    video.setAttribute('id', CTATGlobalFunctions.gensym.div_id());
	    video.setAttribute('onkeypress', 'return noenter(event)');
	    //pointer.getComponent ().conceal=function(){ /* nothing */ };

	    pointer.addComponentReference(pointer, video);

	    pointer.ctatdebug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    pointer.setInitialized(true);

	    pointer.getDivWrap().appendChild(video);

		var bgColor=pointer.getBackgroundColor();
		var backgroundColorString="rgba (" + hexToRgb (bgColor).r + "," + hexToRgb (bgColor).r + "," + hexToRgb (bgColor).r + "," + 0 + ")";

		pointer.modifyCSSAttribute("background-color", backgroundColorString);
		pointer.addStringCSS("filter: alpha(opacity=0);");

		pointer.addCSSAttribute("width", pointer.getWidth());
		pointer.addCSSAttribute("height", pointer.getHeight());

	    video.setAttribute('style', pointer.getCSS());

	    //currentZIndex++;
	    //currentIDIndex++;
	};

	/**
	 *
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.ctatdebug ("processSerialization()");

		// Process component specific pre-defined styles ...



		// Process component custom styles ...

		this.styles=pointer.getGrDescription().styles;

		pointer.ctatdebug ("Processing " + this.styles.length + " styles ...");

		for (var i=0;i<this.styles.length;i++)
		{
			var aStyle=this.styles [i]; // CTATStyle

			if(aStyle.styleName=="borderRoundness")
			{
				var borderRoundness=aStyle.styleValue;
				pointer.addCSSAttribute("border-radius", borderRoundness);
			}
		}
	};

	/**
	 * An Interface Action for loading the specified video.
	 * @param {string} aURL	a valid url to a video.
	 */
	this.PlayMedia = function (aURL) {
		video.src = aURL;
	};
	/**
	 * An Interface Action for displaying or hiding the controls.
	 * @param {boolean} show	true: show the controls, false: hide controls.
	 */
	this.showControls = function (show) {
		video.controls=show;
	};

	/**
	 * An Interface Action for starting to play the video at a given time.
	 * @param {string|number} playtime	a time reference in seconds.
	 */
	this.play = function (playtime) {
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
	};
	/**
	 * An Interface Action for pausing the video at a given time.
	 * @param {string|number} playtime	a time reference in seconds.
	 */
	this.pause = function (playtime) {
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
	};
	/**
	 * An Interface Action for pausing the video at a given time.
	 * @param {string|number} playtime	a time reference in seconds.
	 * @see CTATVideo.pause
	 */
	this.stop = this.pause;
}

CTATVideo.prototype = Object.create(CTATTutorableComponent.prototype);
CTATVideo.prototype.constructor = CTATVideo;

CTAT.ComponentRegistry.addComponentType('CTATVideo',CTATVideo);