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
 */
function CTATMovieClip (anInstance,aX,aY,aWidth,aHeight)
{
	CTATBase.call(this, "CTATMovieClip", anInstance);

	var x=aX;
	var y=aY;
	var width=aWidth;
	var height=aHeight;	
			
	var topDivZIndex=currentZIndex;
	var topDivIDIndex=currentIDIndex;	
	
	var canvasZIndex=currentZIndex+1;
	var canvasIDIndex=currentIDIndex+1;
	
	var componentList=new Array ();
	
	var pointer=this;	
		
	var divWrapper=null;	
		
	/**
	 *
	 */
	this.wrapComponent=function wrapComponent(aParent)
	{
		divWrapper=document.createElement('div');
		divWrapper.setAttribute('id', ('ctatdiv' + topDivIDIndex));
		divWrapper.setAttribute('name', this.getName ());
		divWrapper.setAttribute('style', "z-index: "+topDivZIndex);
				
		divWrapper.setAttribute('width', width+"px");
		divWrapper.setAttribute('height', height+"px");

		divWrapper.setAttribute("style", "border: 0px; position: absolute; left:"+x+"px; top:"+y+"px; z-index:"+canvasZIndex+";");		
		divWrapper.setAttribute("style", "border: 0px; position: relative; left:"+x+"px; top:"+y+"px; z-index: inherit;");		
		
		aParent.appendChild(divWrapper);	

		return (divWrapper);		
	};
	
	/**
	*
	*/
	this.getDivWrapper=function getDivWrapper ()
	{
		return (divWrapper);
	};
	
	/**
	*
	*/
	this.addComponent=function addComponent (aComponentName)
	{
		pointer.debug ("addComponent ("+aComponentName+")");
		
		componentList.push(aComponentName);
	}
	/**
	*
	*/
	this.isRegistered=function isRegistered (aComponentName)
	{
		for (var i=0;i<componentList.length;i++)
		{
			if (componentList [i]==aComponentName)
			{
				return (true);
			}
		}	
	
		return (false);
	}
}

CTATMovieClip.prototype = Object.create(CTATBase.prototype);
CTATMovieClip.prototype.constructor = CTATMovieClip;
