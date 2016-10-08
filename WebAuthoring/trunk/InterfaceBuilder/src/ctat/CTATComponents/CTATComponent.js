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
 
*/

/**
 * 
 * click
 * mousemove
 * mouseover
 * mouseout
 * keyup
 * keydown
 * focus
 * blur
 * select
 * load
 * 
 */

/**
 * 
 */
function CTATComponent (aClassName,
					    aName,
					    aDescription,
					    aX,
					    aY,
					    aWidth,
					    aHeight)
{
	CTATCompBase.call(this,
					  aClassName,
					  aName,
					  aDescription,
					  aX,
					  aY,
					  aWidth,
					  aHeight);

	this.debug ("CTATComponent" + " ("+this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight()+")");

	var moused=false;
	var pointer=this;	
	var images = {};
	var sources = 
	{
		buttonDefault: '',
		buttonHover: '',
		buttonClicked: '',
		buttonDisabled: ''			
	};	
	
	/**
	 * 
	 */
	this.assignImages=function assignImages (imageDefault,imageClicked,imageDisabled,imageHover)
	{
		pointer.debug ("assignImages ()");
		
	    for(src in sources) 
	    {
	    	pointer.debug ("Check: " + sources[src]);	  
	    }
			
		sources.buttonDefault=imageDefault;	
		sources.buttonClicked=imageClicked;
		sources.buttonDisabled=imageDisabled;
		sources.buttonHover=imageHover;
	};
	
	/**
	 *  
	 */
	this.loadImage=function loadImages ()
	{
		pointer.debug ("loadImages ()");
		
	    var loadedImages = 0;
	    var numImages = 0;
	    var src=null;
	    
	    for(src in sources) 
	    {
	    	numImages++;
	    }
	    
	    pointer.debug ("Loading " + numImages + " images ...");
	    
	    for(src in sources) 
	    {
	  		pointer.debug ("Loading: " + sources [src] + " ...");
	    	
	    	images[src] = new Image();
	  	    images[src].src = sources[src];	      
	    	images[src].onload = function() 
	    	{    	  
	    		pointer.debug ("Image " + images[src].src + " loaded");
	    		
	    		if(++loadedImages >= numImages) 
	    		{
	    			pointer.debug ("Images loaded!");
	    			
	    			hasImages=true;
	    			pointer.getGrCanvas().drawTutor ();
	    		}
	    	};	      	      
	    }
	};
	
	/**
	 * 
	 */

	
	
	
	/**
	 * 
	 */
	function click (mouseX,mouseY)
	{		
		pointer.debug ("click ("+mouseX+","+mouseY+")");
		
		setSelected (true);
	}
	
	/**
	 * 
	 */
	function hover (mouseX,mouseY)
	{		
		//debug ("hover ("+mouseX+","+mouseY+")");
						
	}
	
	/**
	 * 
	 */
	function mouseOut (mouseX,mouseY)
	{		
		pointer.debug ("mouseOut ("+mouseX+","+mouseY+")");
				
		setSelected (false);
		
		pointer.getGrCanvas().drawTutor ();
	}
	
	/**
	 * 
	 */
	function mouseIn (mouseX,mouseY)
	{		
		pointer.debug ("mouseIn ("+mouseX+","+mouseY+")");
						
		setSelected (true);
		
		pointer.getGrCanvas().drawTutor ();
	}
	
	/**
	 * 
	 */
	function setSelected (aVal)
	{
		pointer.debug ("setSelected ()");
		
		selected=aVal;
		
		if (selected==true)
		{			
			backgroundColor="#cccccc";
		}
		else
		{
			backgroundColor="#eeeeee";
		}
	}
	
	/**
	 * 
	 */
	this.init=init;
	function init ()
	{
		pointer.debug ("init ()");
		
		loadImages ();
		
		canvas.addEventListener('keydown', function(e) 
		{
			pointer.debug ("keydown ("+getKey (e)+")");
			
		    switch (getKey (e)) 
		    {
		    	// key code for left arrow
	        	case 37:
	        			pointer.debug('left arrow key pressed!');
	        			break;
	        
	        			// key code for right arrow
	        	case 39:
	        			pointer.debug('right arrow key pressed!');
	        			break;
		    }			
		});
		
		canvas.addEventListener('click', function(e) 
		{
			pointer.debug ("click ()");
			
			var mouseX=e.pageX-ctatcanvas.offsetLeft;
			var mouseY=e.pageY-ctatcanvas.offsetTop;
					
			if ((mouseX>pointer.getX()) && (mouseX<(pointer.getX()+pointer.getWidth())) && (mouseY>pointer.getY()) && (mouseY<(pointer.getY()+pointer.getHeight())))
			{
				click (mouseX,mouseY);
						
				pointer.getGrCanvas.drawTutor ();
			}		
		});

		canvas.addEventListener('mousemove', function(e) 
		{				
			//debug ("mousemove");
					
			var mouseX=e.pageX-ctatcanvas.offsetLeft;
			var mouseY=e.pageY-ctatcanvas.offsetTop;
							
			if ((mouseX>pointer.getX()) && (mouseX<(pointer.getX()+pointer.getWidth())) && (mouseY>pointer.getY()) && (mouseY<(pointer.getY()+pointer.getHeight())))
			{
				hover (mouseX,mouseY);
						
				if (moused==false)
				{
					mouseIn (mouseX,mouseY);
							
					moused=true;
				}			
			}	
			else
			{
				if (moused==true)
				{
					mouseOut (mouseX,mouseY);
						
					moused=false;
				}			
			}							
		});		
	}

	/**
	 * 
	 */
	this.CTATInvalidate=function CTATInvalidate ()
	{
		pointer.getGrCanvas.drawTutor ();
	}
}

CTATComponent.prototype = Object.create(CTATCompBase);
CTATComponent.prototype.constructor = CTATComponent;
