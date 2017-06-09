
var flexEnabled=true;

/**
*
*/
$.fn.drags = function (opt) 
{
	var dragging=false;
	
	ctatdebug ("Trying to assign draggable functionality to gripper ...");
	
	if (flexEnabled==false)
	{
		ctatdebug ("Flex layout resizing currently disabled");
		return;
	}
	
	opt = $.extend(
	{
		handle: '',
		cursor: 'ew-resize',
		min: 10
	},opt);
	
	if (opt.handle === '') 
	{
		var $el = this;
	}
	else 
	{
		var $el = this.find(opt.handle);
	}
	
	// Store the prior cursor, jus tin case
	var priorCursor = $('body').css('cursor');

	return $el.css('cursor', opt.cursor).on('mousedown', function (e) 
	{
		//ctatdebug ("mousedown ()");
		
		priorCursor = $('body').css('cursor');
	
		$('body').css('cursor', opt.cursor);
	
		if (opt.handle === '') 
		{
			var $drag = $(this).addClass('draggable');
		} 
		else 
		{
			var $drag = $(this).addClass('active-handle').parent().addClass('draggable');
		}
	
		var z_idx = $drag.css('z-index');
		var	drg_h = $drag.outerHeight();
		var drg_w = $drag.outerWidth();
		var pos_y = $drag.offset().top + drg_h - e.pageY;
		var	pos_x = $drag.offset().left + drg_w - e.pageX;
		
		dragging=true;
		
		$drag.css('z-index', 1000).parents().on('mousemove', function (e) 
		{
			//ctatdebug ("mousemove ()");
			
			if (dragging==false)
			{
				//ctatdebug ("Not dragging");
				return;
			}
		
			var prev = $('.draggable').prev();
			var next = $('.draggable').next();
			var total = prev.outerWidth() + next.outerWidth();
			
			//ctatdebug('l: ' + prev.outerWidth() + ', r:' + next.outerWidth());
			
			var leftPercentage = (e.pageX - prev.offset().left + (pos_x - drg_w / 2)) / total;
			var rightPercentage = 1 - leftPercentage;

			if (leftPercentage * 100 < opt.min || rightPercentage * 100 < opt.min) 
			{
				return;
			}
			
			//ctatdebug('l: ' + leftPercentage + ', r:' + rightPercentage);
		
			prev.css('flex', leftPercentage.toString());
			next.css('flex', rightPercentage.toString());
			
			$(document).on('mouseup', function () 
			{
				//ctatdebug ("mouseup ()");
				
				$('body').css('cursor', priorCursor);
				$('.draggable').removeClass('draggable').css('z-index', z_idx);
				
				dragging=false;
			});
		});
		
		e.preventDefault();
	});
};
