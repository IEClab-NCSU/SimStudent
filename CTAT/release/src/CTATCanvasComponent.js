/**-----------------------------------------------------------------------------
 $Author: mringenb $
 $Date: 2016-02-02 13:28:40 -0600 (週二, 02 二月 2016) $
 $HeadURL: svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/branches/CTAT_4_2_Release/HTML5/src/CTATCanvasComponent.js $
 $Revision: 23157 $

 -
 License:
 -
 ChangeLog:
 -
 Notes:

 */
goog.provide('CTATCanvasComponent');

CTATCanvasComponent = function(aName)
{
	var pointer=this;

	var shapes=[];
	var componentName=aName || "__undefined__";

	this.addShape=function addShape(aShape)
	{
		shapes.push(aShape);
	};

	this.hideShape=function hideShape(aShapeName)
	{
		for(var i=0; i<shapes.length; i++)
		{
			if(shapes[i].getName()==aShapeName)
			{
				shapes[i].modifyCanvasCSS("visibility", "hidden");
				break;
			}
		}
	};

	this.showShape=function showShape(aShapeName)
	{
		for(var i=0; i<shapes.length; i++)
		{
			if(shapes[i].getName()==aShapeName)
			{
				shapes[i].modifyCanvasCSS("visibility", "visible");
				break;
			}
		}
	};

	this.hideComponent=function hideComponent()
	{
		for(var i=0; i<shapes.length; i++)
		{
			shapes[i].modifyCanvasCSS("visibility", "hidden");
		}
	};

	this.showComponent=function showComponent()
	{
		for(var i=0; i<shapes.length; i++)
		{
			shapes[i].modifyCanvasCSS("visibility", "visible");
		}
	};

	this.moveShape=function moveShape(aShapeName, toX, toY)
	{
		for(var i=0; i<shapes.length; i++)
		{
			if(shapes[i].getName()==aShapeName)
			{
				shapes[i].modifyCanvasCSS("left", toX+"px");
				shapes[i].modifyCanvasCSS("top", toY+"px");
				break;
			}
		}
	};

	this.moveComponent=function moveComponent(toX, toY)
	{
		for(var i=0; i<shapes.length; i++)
		{
			shapes[i].modifyCanvasCSS("left", toX+"px");
			shapes[i].modifyCanvasCSS("top", toY+"px");
		}
	};

	this.removeShape=function removeShape(aShapeName)
	{
		var removeIndex=0;

		for(var i=0; i<shapes.length; i++)
		{
			if(shapes[i].getName()==aShapeName)
			{
				removeIndex=i;
				break;
			}
		}
		shapes[removeIndex].detatchCanvas();
		shapes.splice(removeIndex, 1);
	};

	this.removeComponent=function removeComponent()
	{
		while(shapes.length > 0)
		{
			pointer.removeShape(shapes[0].getName());
		}
	};
};