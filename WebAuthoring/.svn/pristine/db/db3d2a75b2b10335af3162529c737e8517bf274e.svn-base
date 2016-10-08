/**------------------------------------------------------------------------------------
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
 * 
 */
function CTATAudioButton (aDescription,aX,aY,aWidth,aHeight)
{		
	CTATImageButtonComponent.call(this, 
					  			  "CTATAudioButton", 
					  			  "__undefined__",
					  			  aDescription,
					  			  aX,
					  			  aY,
					  			  aWidth,
					  			  aHeight);

	var hints=new Array ();

	var pointer=this;
	var audioElement=null; 
	var audioButton=null;
	var soundFile="";
					
	this.debug (this.getClassName() + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");
	
	this.configFromDescription ();

	this.init=function init() 
	{
	    pointer.debug("init (" + pointer.getName() + ")");

	    pointer.addCSSAttribute("z-index", currentZIndex);

	    audioButton=new Image();
	    audioButton.title=pointer.getName();
	    audioButton.name=pointer.getName();
	    audioButton.setAttribute('id', ('ctatdiv' + currentIDIndex));
	    audioButton.setAttribute('onkeypress', 'return noenter(event)');

	    pointer.addComponentReference(pointer, audioButton);

	    pointer.debug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    pointer.setInitialized(true);

	    pointer.getDivWrap().appendChild(audioButton);
	    
	    audioButton.src=pointer.getDefaultImage();

		var bgColor=pointer.getBackgroundColor();
		var backgroundColorString="rgba (" + hexToRgb (bgColor).r + "," + hexToRgb (bgColor).r + "," + hexToRgb (bgColor).r + "," + 0 + ")";
		
		pointer.modifyCSSAttribute("background-color", backgroundColorString);
		pointer.addStringCSS("filter: alpha(opacity=0);");

		pointer.addCSSAttribute("width", pointer.getWidth());
		pointer.addCSSAttribute("height", pointer.getHeight());

	    audioButton.setAttribute('style', pointer.getCSS());

	    currentZIndex++;
	    currentIDIndex++;
	    
	    pointer.addSafeEventListener ('click',pointer.processClick);
	    
	    //this.setEnabled (false);
	    
		audioElement=document.createElement('audio');
		audioElement.setAttribute('src', soundFile);
		
		pointer.debug ("Loading audio ...");
		
	    pointer.addSafeEventListener('mouseover', pointer.processMouseOver,audioButton);
	    pointer.addSafeEventListener('mouseout', pointer.processMouseOut,audioButton); 
		
		audioElement.addEventListener("load", function() 
		{ 
			pointer.debug ("Audio loaded");
			
			pointer.setEnabled (true);			
		}, true);
		
		pointer.render();
		
		audioElement.load();
	};
	
	/**
	 * 
	 * @param e
	 */
	this.processClick=function processClick (e)
	{
		pointer.debug ("processClick ("+e.currentTarget.getAttribute ("id")+" -> "+e.eventPhase+")");
				
		if (pointer.getEnabled()==true)
		{
			pointer.debug ("Playing audio file ...");
			
			audioElement.play();
			
			commShell.gradeComponent (pointer);
		}
		else
			pointer.debug ("Component is disabled, not grading");		
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
			
			if(aStyle.styleName=="SoundFile")
			{
				soundFile=aStyle.styleValue;
			}
			
			if(aStyle.styleName=="labelText")
			{
				pointer.setText(aStyle.styleValue);
			}
		}	
	};    
}

CTATAudioButton.prototype = Object.create(CTATImageButtonComponent.prototype);
CTATAudioButton.prototype.constructor = CTATAudioButton;
