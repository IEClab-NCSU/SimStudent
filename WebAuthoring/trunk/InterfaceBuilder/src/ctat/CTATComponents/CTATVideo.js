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
 
 http://dev.opera.com/articles/view/custom-html5-video-player-with-css3-and-jquery/
 
*/

/**
 * 
 */
function CTATVideo (aDescription,aX,aY,aWidth,aHeight)
{		
	CTATTutorableCompponent.call(this, 
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
	
	this.setAlpha=function setAlpha()
	{
		alpha=aAlpha;
	};
				
	this.debug (this.getClassName() + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");
	
	this.configFromDescription ();

	this.init=function init() 
	{
	    pointer.debug("init (" + pointer.getName() + ")");

	    pointer.addCSSAttribute("z-index", currentZIndex);

	    video=document.createElement('video');
	    //pointer.getComponent().title=pointer.getName();
	    video.src="http://augustus.pslc.cs.cmu.edu/ProportionalDistanceExercise.mp4";
	    video.controls=true;
	    video.autocontrols=false; // doesn't work
	    video.autoplay=true;	    
	    video.name=pointer.getName();
	    video.setAttribute('id', ('ctatdiv' + currentIDIndex));
	    video.setAttribute('onkeypress', 'return noenter(event)');
	    //pointer.getComponent ().conceal=function(){ /* nothing */ };

	    pointer.addComponentReference(pointer, video);

	    pointer.debug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    pointer.setInitialized(true);

	    pointer.getDivWrap().appendChild(video);

		var bgColor=pointer.getBackgroundColor();
		var backgroundColorString="rgba (" + hexToRgb (bgColor).r + "," + hexToRgb (bgColor).r + "," + hexToRgb (bgColor).r + "," + 0 + ")";
		
		pointer.modifyCSSAttribute("background-color", backgroundColorString);
		pointer.addStringCSS("filter: alpha(opacity=0);");

		pointer.addCSSAttribute("width", pointer.getWidth());
		pointer.addCSSAttribute("height", pointer.getHeight());

	    video.setAttribute('style', pointer.getCSS());

	    currentZIndex++;
	    currentIDIndex++;
	};
		
	/**
	 * 
	 */
	this.processSerialization=function processSerialization()
	{
		pointer.debug ("processSerialization()");

		// Process component specific pre-defined styles ...
		
		
		
		// Process component custom styles ...		

		this.styles=pointer.getGrDescription().styles;
		
		pointer.debug ("Processing " + this.styles.length + " styles ...");
		
		for (var i=0;i<this.styles.length;i++)
		{
			var aStyle=this.styles [i]; // CTATStyle
			
			if(aStyle.styleName=="borderRoundness")
			{
				borderRoundness=aStyle.styleValue;
				pointer.addCSSAttribute("border-radius", borderRoundess);
			}
		}	
	};		
}

CTATVideo.prototype = Object.create(CTATTutorableComponent.prototype);
CTATVideo.prototype.constructor = CTATVideo;
