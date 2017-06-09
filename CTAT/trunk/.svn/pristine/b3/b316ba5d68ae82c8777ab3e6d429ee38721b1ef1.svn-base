/**-----------------------------------------------------------------------------
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
var CTATDesktop = function()
{
	CTATBase.call(this, "CTATDesktop","desktopmanager");

	var pointer=this;
	var icons=[];
	
	var margin_x=10;
	var margin_y=10;
	
	var index_x=margin_x;
	var index_y=margin_y;
	
	var iconPadding=4;
	var iconSpacing=48;
	
	var generator=CTATGuid;
	
	var iconSize=32;
	
	/**
	*
	*/
	this.getIcon = function getIcon (anID)
	{
		for (var i=0;i<icons.length;i++)
		{
			var testIcon=icons [i];
			
			if (testIcon.id==anID)
			{
				return (testIcon);
			}
		}
		
		return (null);
	}
	
	/**
	*
	*/
	this.addIcon=function addIcon (anImage,aFunction,aLabel)
	{
		pointer.ctatdebug ("addIcon ()");

		var uuid=CTATGuid.guid();
		
		pointer.ctatdebug ("Adding icon with id: " + uuid);
		
		var iconData=
		{
			'id' : uuid,
			'image' : anImage,
			'callback' : aFunction,
			'label' : aLabel,
			xCell : aCellX,
			yCell : aCellY,			
			x : index_x,
			y : index_y
		};
		
		icons.push (iconData);
		
		$("#desktop").append ('<div id="'+uuid+'" class="desktop_icon" style="position: absolute; top: '+index_y+'px; left: '+index_x+'px;"><div class="desktop_icon_row"><div class="icon"><img src="'+anImage+'" border="0" style="margin: 0px; padding: 2px; width: 32px; height: 32px;"/></div></div><div class="desktop_icon_row"><div class="icon desktop_label">'+aLabel+'</div></div></div>');
				
		index_x+=(iconSize+iconPadding+iconSpacing);
	};
	
	/**
	*
	*/
	this.addIconPlace=function addIconPlace (anImage,aFunction,aLabel,aCellX,aCellY)
	{
		pointer.ctatdebug ("addIconPlace ()");

		var uuid=CTATGuid.guid();
		
		pointer.ctatdebug ("Adding icon with id: " + uuid);
		
		var iconData=
		{
			'id' : uuid,
			'image' : anImage,
			'callback' : aFunction,
			'label' : aLabel,
			xCell : aCellX,
			yCell : aCellY,
			x : (aCellX*(iconSize+iconPadding+iconSpacing)) + margin_x,
			y : (aCellY*(iconSize+iconPadding+iconSpacing)) + margin_y
		};
		
		icons.push (iconData);
		
		$("#desktop").append ('<div id="'+uuid+'" class="desktop_icon" style="position: absolute; top: '+iconData.y+'px; left: '+iconData.x+'px;"><div class="desktop_icon_row"><div class="icon"><img src="'+anImage+'" border="0" style="margin: 0px; padding: 2px; width: 32px; height: 32px;"/></div></div><div class="desktop_icon_row"><div class="icon desktop_label">'+aLabel+'</div></div></div>');
				
		if (iconData.x>=index_x)		
		{
			index_x=iconData.x;
		}
		
		if (iconData.y>=index_y)
		{
			index_y=iconData.y;
		}		
	};	
	
	/**
	*
	*/
	this.loadDesktopIcons = function loadDesktopIcons (aCallback)
	{
		pointer.ctatdebug ("loadDesktopIcons ()");	
		
		var jqxhr = $.getJSON("desktop.json", function(data)
		{
			pointer.ctatdebug ("success: adding icons...");
		  			
			$.each(data, function(k, v)
			{				
				if (v.length>3)
				{
					pointer.addIconPlace (v [0],v [1],v [2],v [3],v [4]);
				}
				else
				{
					pointer.addIcon (v [0],v [1],v [2]);					
				}
			});			

			if(aCallback)
			{
				aCallback ();
			}
			
		}).done(function() 
		{
			pointer.ctatdebug ("done");
		}).fail(function()
		{
			ctatdebug('missing desktop.json file');
			pointer.ctatdebug ("fail");
		}).always(function()
		{
			pointer.ctatdebug ("always");
		});
		 
		// Set another completion function for the request above
		/*
		jqxhr.complete(function() 
		{
			console.log("Complete event handler");
		});
		*/
	};
	
	/**
	*
	*/
	this.continueInit = function continueInit ()
	{
		pointer.ctatdebug ("continueInit ()");
		
		$(".desktop_icon").draggable(
		{	
			containment: 'window',
			cursor: 'default',
			scroll: false,
			grid: [ iconSize, iconSize ]
		});	
		
		$(".desktop_icon").mouseover(function()
		{
			$(this).addClass('desktop_icon_highlighted');
		});

		$(".desktop_icon").mouseout(function()
		{
			$(this).removeClass('desktop_icon_highlighted');
		});

		$(".desktop_icon").dblclick(function processIconDoubleClick ()
		{
			pointer.ctatdebug ("processIconDoubleClick ("+$(this).attr("id")+")");
			
			var targetIconID=$(this).attr("id");
			
			var targetIcon=pointer.getIcon (targetIconID);
			
			if (targetIcon!=null)
			{
				var icnCallback=targetIcon.callback;
				
				pointer.ctatdebug ("typeof icnCallback: " + typeof icnCallback);
				
				if (typeof icnCallback === "string")
				{
					window [icnCallback]();
				}
				else
				{
					icnCallback ();
				}	
			}
			else
			{
				pointer.ctatdebug ("Internal error: unable to find target icon");
			}
		});			
	};
	
	/**
	* http://api.jqueryui.com/draggable/#option-cursor
	*/
	this.init = function init ()
	{	
		pointer.ctatdebug ("init ()");
		
		pointer.loadDesktopIcons (pointer.continueInit);		
	};
};

CTATDesktop.prototype = Object.create (CTATBase.prototype);
CTATDesktop.prototype.constructor = CTATDesktop;
