/**------------------------------------------------------------------------------------
*
*/

/**
 * 
 */
function CTATSkillWindow (aDescription,aX,aY,aWidth,aHeight)
{		
	CTATCompBase.call(this, 
					  "CTATSkillWindow", 
					  "__undefined__",
					  aDescription,
					  aX,
					  aY,
					  aWidth,
					  aHeight);

	var hints=new Array ();
	var alpha=0.0;
	var pointer=this;
	var graphicsTools=null;
	
	// Skill window specific variables
	
	var marginX		   =6;
	var marginY		   =6;
	var barDistance	   =4;
	var barHeight	   =15;
	var barMaxX		   =150;
	var borderRoundness =5;
	
	var spThreshold	=0.95;
	
	var inspSkillBarBorderColor   ="#cccccc";		
	var inspSkillBarColor         ="#ffffcc";		
	var inspSkillBarThresholdColor="#66cc33";		
	var inspSkillVerticalStroke   =true;
	var outerBorderColor="#408080";

	var skillwindow=null;

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
	    
	    pointer.setCanvasVisibility("visible");

	    pointer.debug("Final location: " + pointer.getX() + "," + pointer.getY() + " with text: " + pointer.getText());

	    pointer.setInitialized(true);
	    
		graphicsTools=new CTATGraphicsTools(pointer.getSubCanvasCtx());

	    currentZIndex++;
	    currentIDIndex++;
		
		this.drawComponent ();		
	};
		
	/**
	 * 
	 */
	this.assignSkillSet=function assignSkillSet (aSkillSet)
	{
		skillSet=aSkillSet;
		
		this.drawComponent ();
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
				
				if(aStyle.styleName=="SkillBarBorderColor")
				{
					inspSkillBarBorderColor=aStyle.styleValue;
				}
				
				if(aStyle.styleName=="SkillBarColor")
				{
					inspSkillBarColor=aStyle.styleValue;
				}
				
				if(aStyle.styleName=="SkillMasteryColor")
				{
					inspSkillBarThresholdColor=aStyle.styleValue;
				}
				
				if(aStyle.styleName=="SkillBarWidth")
				{
					barMaxX=aStyle.styleValue;
				}
				
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
	 * Override from CTATComponent.js 
	 */
	this.drawComponent=function drawComponent ()
	{	
		pointer.debug ("drawComponent ()");
		
		if (skillSet==null)
		{
			debug ("Info: no skillSet object available, bumping out");
			return;
		}

		graphicsTools.save();
		
		graphicsTools.setLineColor(outerBorderColor);

		//For some reason, using borderRoundness will mess this function up...
		graphicsTools.drawRoundedRectFilled (1,1,
									   		pointer.getWidth(),
									   		pointer.getHeight(),
									  		5);

		graphicsTools.clip();
		
		var top=marginY;
		
		// Draw each skill, one at a time ...
		
		var skillList=skillSet.getSkillSet ();
		
		if (skillList==null)
		{
			pointer.debug ("Error: list of skills is null in skills object");
			return;
		}
		
		if (skillList.length==0)
		{
			pointer.debug ("Error: list of skills is 0 length");
			return;
		}		
						
		for (var i=0;i<skillList.length;i++)
		{
			var skill=skillList [i];
			
			pointer.debug ("Drawing skill "+i+" "+skill.getDisplayName () + " level: " + skill.getLevel ()+" ...");
						
			graphicsTools.setLineColor(inspSkillBarBorderColor);
			graphicsTools.drawRectangle (marginX,top,barMaxX,barHeight);
			
			if (skill.getDisplayName() == null)
			{
				graphicsTools.setLineColor("#000000");
				graphicsTools.drawText (marginX+1+barMaxX+2,top+(barHeight/2),"no-name");
			}	
			else
			{
				graphicsTools.setLineColor("#000000");
				graphicsTools.drawText (marginX+1+barMaxX+2,top+(barHeight/2),skill.getDisplayName());
			}	
					
			if (skill.getLevel ()>spThreshold)
			{
				graphicsTools.setFillColor(inspSkillBarThresholdColor);
				graphicsTools.drawRectangleFilled (marginX+1,top+1,skill.getLevel ()*(barMaxX-2),barHeight-2);
				//this.drawRect (marginX+1,top+1,skill.getLevel ()*(barMaxX-2),barHeight-2,'#000000');				
			}
			else
			{		
				graphicsTools.setFillColor(inspSkillBarColor);		
				graphicsTools.drawRectangleFilled (marginX+1,top+1,skill.getLevel ()*(barMaxX-2),barHeight-2);
				//this.drawRect (marginX+1,top+1,skill.getLevel ()*(barMaxX-2),barHeight-2,'#000000');
			}
			
			/*
			if (inspSkillVerticalStroke==true)
			{
				//sTools.lineColor=inspSkillBarThresholdColor;
				//this.drawLine (marginX+1+calcWidth (skill.getLevel (),barMaxX-2),top+1,marginX+1+calcWidth (skill.getLevel (),barMaxX-2),top+1+barHeight-2);
			}
			*/

			top+=(barHeight+barDistance);
		}
		
		graphicsTools.restore();
	};	
}

CTATSkillWindow.prototype = Object.create(CTATCompBase.prototype);
CTATSkillWindow.prototype.constructor = CTATSkillWindow;
