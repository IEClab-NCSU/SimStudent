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
 
//Holds the components local to the Hint Window.
var localComponents=new Array();
 
//A hard-coded ComponentDescription that will be used for components with no description
var genDesc=new CTATComponentDescription();

genDesc.name="Generic name";
genDesc.styles.push(new CTATStyle("inspBackgroundColor", "#ffffff"));
genDesc.styles.push(new CTATStyle("BorderColor", "#999999"));
genDesc.styles.push(new CTATStyle("FontName", globalFontFamily));
genDesc.styles.push(new CTATStyle("FontSize", globalFontSize));
genDesc.styles.push(new CTATStyle("FontColor", globalFontColor));
genDesc.styles.push(new CTATStyle("FontBold", false));
genDesc.styles.push(new CTATStyle("FontItalic", false));
genDesc.styles.push(new CTATStyle("FontUnderlined", false));
genDesc.styles.push(new CTATStyle("TextAlign", globalAlign));
genDesc.styles.push(new CTATStyle("ShowHintHighlight", false));
genDesc.styles.push(new CTATStyle("blockOnCorrect", false));
genDesc.styles.push(new CTATStyle("disabledBackgroundColor", "#999999"));
genDesc.styles.push(new CTATStyle("disabledTextColor", "#999999"));
genDesc.styles.push(new CTATStyle("tutorComponent", null));

/**
 * 
 */
function CTATHintWindow (aDescription,aX,aY,aWidth,aHeight)
{		
	CTATCompBase.call(this, 
					  "CTATHintWindow", 
					  "__undefined__",
					  aDescription,
					  aX,
					  aY,
					  aWidth,
					  aHeight);	
	var alpha=0.0;
	var pointer=this;
	var hintContent=null;
	var previous=null;
	var next=null;
	var hintwindow=null;
	var outerBorderColor="#408080";
	var borderRoundness=5;
	
	var graphicsTools=null;
	
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

	/**
	*
	*/
	this.init=function init() 
	{
	    pointer.debug("init (" + pointer.getName() + ")");

		pointer.setCanvasVisibility("visible");
	    pointer.addCSSAttribute("z-index", currentZIndex);

	    hintwindow=document.createElement('div');

	    hintwindow.setAttribute('id', ('ctatdiv' + currentIDIndex));
	    hintwindow.setAttribute('onkeypress', 'return noenter(event)');
	    pointer.setComponent(hintwindow);

	    pointer.addComponentReference(pointer, hintwindow);

	    pointer.debug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    pointer.setInitialized(true);
	    
	    graphicsTools=new CTATGraphicsTools(pointer.getSubCanvasCtx());

	    pointer.getDivWrap().appendChild(hintwindow);
		
	    currentZIndex++;
	    currentIDIndex++;
				
	    //> ------------- Hintwindow specific code ---------------------------------------
	    
		genDesc.name="hintcontent";
		genDesc.type="CTATTextField";
				
		hintContent=new CTATTextField (genDesc,aX+4,4,aWidth-8-4,aHeight-21-12);
		//hintContent=new CTATTextField (genDesc,4,4,aWidth-8-4,aHeight-21-12);
		hintContent.setName("hintcontent");
		//hintContent.setInitialized (true);		
		hintContent.wrapComponent(hintwindow);
		hintContent.setCanvasVisibility("hidden");
		hintContent.setAlpha(1.0);
		hintContent.setShowBorder (true);
		hintContent.setBackgroundColor ('#ffffff');
		hintContent.addCSSAttribute("overflow", "auto");
		pointer.addComponent (hintContent);
				
		pointer.debug ("Jumping out of init ()");		
				
		//> ------------- Previous button specific code ----------------------------------		
		
		genDesc.name="previous";
		genDesc.type="CTATImageButton";
				
		previous=new CTATImageButton (genDesc,aX+4,aHeight-24,82,21);
		previous.setName("previous");
		//previous.setInitialized (true);  // Don't call this
		previous.wrapComponent(hintwindow);
		previous.setCanvasVisibility("hidden");
				
		/*
		previous.assignImages (hintPreviousDefault,
							   hintPreviousHover,
							   hintPreviousHover,
							   hintPreviousDisabled);
		*/
							   
		previous.assignImages ("https://qa.pact.cs.cmu.edu/images/skindata/Hint-Previous.png",
							   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Previous-Hover.png",
							   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Previous-Hover.png",
							   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Previous-Disabled.png");							   
							   
		pointer.addComponent (previous);
		//previous.addSafeEventListener ('click',previous.processPreviousButton);
		
		//> ------------- Next specific code ---------------------------------------
		
		genDesc.name="next";
		genDesc.type="CTATImageButton";
		
		next=new CTATImageButton (genDesc,aX+aWidth-82-windowPadding,aHeight-24,82,21);
		next.setName("next");
		//next.setInitialized (true); // Don't call this
		next.wrapComponent(hintwindow);
		next.setCanvasVisibility("hidden");
		
		/*
		next.assignImages (hintNextDefault,
						   hintNextHover,
						   hintNextHover,
						   hintNextDisabled);
		*/
						   
		next.assignImages ("https://qa.pact.cs.cmu.edu/images/skindata/Hint-Next.png",
						   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Next-Hover.png",
						   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Next-Hover.png",
						   "https://qa.pact.cs.cmu.edu/images/skindata/Hint-Next-Disabled.png");						   
						   
		pointer.addComponent (next);
		//next.addSafeEventListener ('click',next.processNextButton);
		
	    //> ------------------------------------------------------------------------		
				
		feedbackComponents.push(this);
		
		pointer.debug("Disabling previous and next ...");
		
		previous.setEnabled (false);
		next.setEnabled (false);
	};
	/**
	*
	*/	
	this.processSerialization=function processSerialization()
	{
		pointer.debug ("processSerialization()");

		// Process component specific pre-defined styles ...
		
		pointer.setText (this.label);
		
		// Process component custom styles ...
		this.styles=aDescription.styles;

		this.styles=pointer.getGrDescription().styles;
		
		if (this.styles!=null)
		{		
			pointer.debug ("Processing " + this.styles.length + " styles ...");
			
			for (var i=0;i<this.styles.length;i++)
			{
				var aStyle=this.styles [i]; // CTATStyle

				if(aStyle.styleName=="borderRoundness")
				{
					borderRoundness=aStyle.styleValue;
				}

				if(aStyle.styleName=="OuterBorderColor")
				{
					outerBorderColor=aStyle.styleValue;
				}
			}	
		}	
	};
	
	/**
	*
	*/		
	this.showFeedback=function showFeedback (aMessage)
	{	
		pointer.debug("showFeedback ("+aMessage+")");
				
		hintContent.setText (aMessage);
		
		previous.setEnabled (false);
		next.setEnabled (false);
	};	
	
	/**
	 * 
	 * @param hintList
	 */
	this.showHint=function showHint (hintList)
	{	
		pointer.debug("showHint ()");
		
		hints=hintList;
		hintIndex=0;
		
		previous.setEnabled (false);
		next.setEnabled (false);
		
		hintContent.setText (hints [hintIndex]); // Show first hint
		
		if (hints.length>1)
		{
			next.setEnabled (true);
		}
	};
	
	/**
	 * Not sure if we need to override this
	 * @param aValue
	 */	
	this.setEnabled=function setEnabled(aValue)
	{
		pointer.assignEnabled(aValue);
		
		if (pointer.getComponent()==null)
			return;
		
		/*
		if (pointer.getEnabled()==true)
			pointer.getComponent().contentEditable='true';
		else
			pointer.getComponent().contentEditable='false';
		*/	
	};

	/**
	 * 
	 */
	this.addComponent=function addComponent (aComponent)
	{
		pointer.debug ("addComponent ()");

		localComponents.push (aComponent);

		aComponent.processSerialization ();		
		aComponent.init ();
		
		pointer.debug ("addComponent () done");
	};
	
	/**
	 * 
	 */
	this.goPrevious=function goPrevious ()
	{
		pointer.debug ("addComponent ()");
		
		hintIndex--;
		
		previous.setEnabled (true);
		next.setEnabled (true);
		
		if (hintIndex<=0)
		{
			hintIndex=0;
						
			previous.setEnabled (false);			
		}
		
		hintContent.setText (hints [hintIndex]);
	};
	
	/**
	 * 
	 */
	this.goNext=function goNext ()
	{
		pointer.debug ("goNext ()");
		
		hintIndex++;
		
		previous.setEnabled (true);
		next.setEnabled (true);
		
		if (hintIndex>(hints.length-1))
		{
			hintIndex=(hints.length-1);			
		}	
		
		if (hintIndex>(hints.length-2))
		{
			next.setEnabled (false);
		}
		
		hintContent.setText (hints [hintIndex]);
	};
	
	/**
	*
	*/
	this.drawComponent=function drawComponent()
	{
		//pointer.debug ("drawComponent + ("+pointer.getX()+","+pointer.getY()+","+pointer.getWidth()+","+pointer.getHeight()+")");
		
		//pointer.debug ("drawComponent - ("+xOffset+","+yOffset+","+pointer.getWidth()+","+pointer.getHeight()+")");
		
		graphicsTools.setLineColor(outerBorderColor);

		graphicsTools.drawRoundedRectFilled (1,1,
									   		 pointer.getWidth(),
									   		 pointer.getHeight(),
									  		 5);
	};
}

CTATHintWindow.prototype = Object.create(CTATCompBase.prototype);
CTATHintWindow.prototype.constructor = CTATHintWindow;
