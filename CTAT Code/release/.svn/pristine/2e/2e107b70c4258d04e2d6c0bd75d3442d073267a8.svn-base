/**
*	@fileoverview A class that represents a generic context menu displayed on right click
*/

/**	@constructor
*	@param menuId the ID of the menu node in the DOM
*	@param optionClass the class applied to options in the menu
*/
var rightClickMenu = function(menuId, optionClass)
{
	//CSS classes applied to menu
	const contextMenuClassName = "rightclick-context-menu";
	const contextMenuActive = "rightclick-context-menu--active";
	const contextMenuItemClassName = "rightclick-context-menu-item";
	const contextMenuDisabledItem = "rightclick-context-menu-item-disabled"
	
	//click coordinate vars
	var clickCoords;
	var clickCoordsX;
	var clickCoordsY;
	
	//menu style vars
	var menuWidth;
	var menuHeight;
	var menuPosition;
	var menuPositionX;
	var menuPositionY;

	var windowWidth;
	var windowHeight;
	
	//whether the menu is visible
	var isActive = false;
	
	//the function responding to the 'contextmenu' event
	var eventListener = null;
	
	//the DOM node of the menu
	var menu = document.querySelector("#"+menuId);
	
	//the DOM nodes of the menu options
	var options = document.querySelectorAll("."+optionClass);
	
	//mapping of option values to handler functions
	var optionHandlers = {};
	
	//whether each option is disabled or not
	var disabled = {};
	
	/**
	*	Set up the listener on the 'contextmenu' event
	*	@param target the DOM node that should be listening
	*	@param handler the function that will handle the event
	*/
	this.setHandler = function(target, handler)
	{
		eventListener = handler
		target.addEventListener('contextmenu', eventListener);
		target.addEventListener('click', this.hide);
	};
	
	/**
	*	Set a handler for an individual menu option
	*	@param optionName the value of the option's 'data-option-value' attribute
	*	@param handler the function that will handle the event
	*/
	this.setOptionHandler = function(optionName, handler)
	{
		optionHandlers[optionName] = handler;
	}
	
	/**
	* 	Turns the custom context menu on
	*	@param clickEvent the event that fired the 'contextmenu' event
	*/
	this.show = function(clickEvent) 
	{
		positionMenu(clickEvent);
		if (isActive)
		{
			return;
		} 
		menu.classList.add(contextMenuActive);
		isActive = true;
	}

	/**
	* 	Hide the context menu
	*/
	this.hide = function() 
	{
		if (isActive)
		{
			menu.classList.remove(contextMenuActive);
			isActive = false;
		}
	};

	/**
	*	Disable an option in the menu
	*	@param optionName the value of the option's 'data-option-value' attribute
	*/
	this.disableOption = function(optionName)
	{
		$(menu).find('li[data-option-value="'+optionName+'"]')[0].classList.add(contextMenuDisabledItem);
		disabled[optionName] = true;
	}
	
	/**
	*	Enable an option in the menu
	*	@param optionName the value of the option's 'data-option-value' attribute
	*/
	this.enableOption = function(optionName)
	{
		$(menu).find('li[data-option-value="'+optionName+'"]')[0].classList.remove(contextMenuDisabledItem);
		disabled[optionName] = false;
	}
	
	/**
	*	Fired when a menu option is clicked
	*	@param e the click event
	*/
	var handleOptionClick = function(e)
	{
		var optionText = e.target.getAttribute('data-option-value');
		if (!disabled[optionText])
			optionHandlers[optionText]();
		
		e.preventDefault();
	};
	
	/**
	* Positions the menu properly.
	* 
	* @param {Object} e The event
	*/
	var positionMenu = function positionMenu(e) 
	{
		clickCoords = this.getPosition(e);
		clickCoordsX = clickCoords.x;
		clickCoordsY = clickCoords.y;

		menuWidth = menu.offsetWidth + 4;
		menuHeight = menu.offsetHeight + 4;

		windowWidth = window.innerWidth;
		windowHeight = window.innerHeight;

		if ( (windowWidth - clickCoordsX) < menuWidth ) 
		{
			menu.style.left = windowWidth - menuWidth + "px";
		} 
		else 
		{
			menu.style.left = clickCoordsX + "px";
		}

		if ((windowHeight - clickCoordsY) < menuHeight)
		{
			menu.style.top = windowHeight - menuHeight + "px";
		} 
		else 
		{
			menu.style.top = clickCoordsY + "px";
		}
	}
  
  /**
   * Gets exact position of event.
   * 
   * @param {Object} e The event passed in
   * @return {Object} Returns the x and y position
   */
    var getPosition = function getPosition(e) 
    {
		var posx = 0;
		var posy = 0;

		if (!e) var e = window.event;
		
		if (e.pageX || e.pageY) {
		  posx = e.pageX;
		  posy = e.pageY;
		} else if (e.clientX || e.clientY) {
		  posx = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
		  posy = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
		}

		return {
		x: posx,
		y: posy
		};
	}
	this.getPosition = getPosition;
	
	//Add click listeners to menu options
	for (var i = 0; i < options.length; i++)
	{
		options.item(i).addEventListener('click', handleOptionClick);
	}
	
	//add window-wide listener to hide menu on left-click
	window.addEventListener('click', this.hide);
}