	//css classes for menus
	const contextMenuClassName = "rightclick-context-menu";
	const contextMenuItemClassName = "rightclick-context-menu-item";
	const contextMenuActive = "rightclick-context-menu--active";

	//click coordinate vars
	var clickCoords;
	var clickCoordsX;
	var clickCoordsY;

	//target of right click
	var lastTarget = null;
	
	//denotes whether last click was on a folder node
	var folderClicked = false;
	
	//denotes whether toCut id is a folder
	var folderCut = false;
	
	//targets of copy and cut commands
	var toCopy=null, toCut=null, destination=null;
	
	//handles on both menus and their items
	var fileMenu, fileMenuItems,
		silexMenu, silexMenuItems;
		
	//handle on currently displayed menu, if any
	var activeMenu = null;
	
	//menu style vars
	var menuWidth;
	var menuHeight;
	var menuPosition;
	var menuPositionX;
	var menuPositionY;

	var windowWidth;
	var windowHeight;
	
	/**
	* Initialise our application's code.
	*/
	function initRightClick() 
	{
		var stageDoc = silexApp.model.file.getContentDocument();
		fileMenu = document.querySelector("#file-rightclick-context-menu");
		fileMenuItems = document.querySelectorAll(".file-rightclick-context-menu-item");
		silexMenu = document.querySelector("#silex-rightclick-context-menu");
		silexMenuItems = document.querySelectorAll(".silex-rightclick-context-menu-item");

		console.log ("initRightClick ()");  
		//on right click over file dialog
		$(document).on('contextmenu', function(e)
		{
			folderClicked = false;
			if ($(e.target).closest(".jqx-grid-cell").length)
			{
				//file click
				destination = window.ctatFileChooser.getCurrentFolderId();
				lastTarget = cloudUtils.getIdFromName(window.ctatFileChooser.selectedFile, destination);
				setOptionsEnabled('files');
				showContextMenu(e, fileMenu);
				e.preventDefault();
			}
			else if ($(e.target).closest(".jstree-anchor").length)
			{
				//folder click
				folderClicked = true;
				var anchor = $(e.target).closest(".jstree-anchor")[0];
				var len = anchor.id.indexOf('_anchor');
				lastTarget = anchor.id.substring(0, len);
				if (lastTarget === 'root')
				{
					lastTarget = cloudUtils.getRootFolder();
				}
				destination = lastTarget
				setOptionsEnabled('files');
				showContextMenu(e, fileMenu);
				e.preventDefault();
			}
			else
			{
				hideContextMenu();
			}
		});
		//on right click over silex stage
		$(stageDoc).on('contextmenu', function(e)
		{
			lastTarget = e.target;
			setOptionsEnabled('silex');
			showContextMenu(e, silexMenu);
			e.preventDefault();
		});
		//init listeners on menu items
		for (var i = 0; i < silexMenuItems.length; i++)
		{
			silexMenuItems.item(i).addEventListener('click', handleSilexMenuItem);
		}
		for (var i = 0; i < fileMenuItems.length; i++)
		{
			fileMenuItems.item(i).addEventListener('click', handleFileMenuItem);
		}
		//listen for clicks outside menu to close italics
		document.addEventListener('click', hideContextMenu);
		stageDoc.addEventListener('click', hideContextMenu);
	}

	/**
	* Turns the custom context menu on.
	*/
	function showContextMenu(clickEvent, menu) 
	{
		console.log ("showContextMenu(), lastTarget = "+lastTarget);
		positionMenu(clickEvent, menu);
		if (menu === activeMenu)
		{
			return;
		} 
		else if (activeMenu)
		{
			//hide other one
			hideContextMenu();
		}
		menu.classList.add(contextMenuActive);
		activeMenu = menu;
	}

	/**
	* Turns the custom context menu off.
	*/
	function hideContextMenu() 
	{
		if (activeMenu)
		{
			activeMenu.classList.remove(contextMenuActive);
			activeMenu = null;
		}
	}

	function handleFileMenuItem(e) 
	{
		console.log('handleFileMenuItem() ');

		var menuText = e.target.getAttribute('data-silex-value');
		switch(menuText)
		{
			case 'cut':
				//record id for later paste
				toCut = lastTarget;
				toCopy = null;
				folderCut = folderClicked;
			break;
			case 'delete':
				window.ctatFileChooser.deleteFile(lastTarget, folderClicked);
			break;
			case 'copy':
				//record id for later paste
				toCopy = lastTarget;
				toCut = null;
			break;
			case 'paste':
				var refresh = function(toRefresh)
				{
					ctatFileChooser.processCTATFolder(toRefresh, ctatFileChooser.getFileType(), true);
				};
				if (toCopy)
				{
					cloudUtils.copyFile(toCopy,
									    destination,
									    function()
											{
												refresh(destination);
											});
				}
				else if (toCut)
				{
					cloudUtils.getParentId(toCut, function(parentId)
						{	
							cloudUtils.moveFile(toCut, 
												parentId,
												destination,
												function()
												{
													if (folderCut)
													{
														refresh(parentId);
														refresh(destination);
													}
													else
													{
														refresh(ctatFileChooser.getCurrentFolderId());
													}
													toCut = null;
												});
						});
				}
			break;
			case 'rename':
				var refresh = function()
				{
					var openFolder = ctatFileChooser.getCurrentFolderId();
					ctatFileChooser.processCTATFolder(openFolder, ctatFileChooser.getFileType(), true);
				};
				var doRename = function(newName)
				{
					cloudUtils.renameFile(lastTarget, 
										  newName,
										  refresh);
				};
				ctatFileChooser.fileDialogNewFolder(null, doRename, 'Rename File');
			break;
		}
		e.preventDefault();
	}
	
	function handleSilexMenuItem(e)
	{
		console.log('handleSilexMenuItem ');

		var menuText = e.target.getAttribute('data-silex-value');
		switch (menuText)
		{
			case 'copy':
				console.log('silexContextMenu -- Copy');
				silexApp.controller.editMenuController.copySelection();
			break;
			case 'delete':
				console.log('silexContextMenu -- Delete');
				silexApp.controller.editMenuController.removeSelectedElements();
			break;
			case 'paste':
				console.log('silexContextMenu -- Paste');
				silexApp.controller.editMenuController.pasteSelection();
			break;
		}
		
		e.preventDefault();
	}
	
	function setOptionsEnabled(menuType)
	{
		var option;
		if (menuType == 'files')
		{
			//paste
			if (!toCopy && !toCut)
			{
				$(fileMenu).find('li[data-silex-value="paste"]')[0].classList.add('rightclick-context-menu-item-disabled');
			}
			else
			{
				$(fileMenu).find('li[data-silex-value="paste"]')[0].classList.remove('rightclick-context-menu-item-disabled');
			}
			// copy
			if (folderClicked)
			{
				$(fileMenu).find('li[data-silex-value="copy"]')[0].classList.add('rightclick-context-menu-item-disabled');
			}
			else
			{
				$(fileMenu).find('li[data-silex-value="copy"]')[0].classList.remove('rightclick-context-menu-item-disabled');
			}
			//delete
			if (lastTarget === cloudUtils.getRootFolder())
			{
				$(fileMenu).find('li[data-silex-value="delete"]')[0].classList.add('rightclick-context-menu-item-disabled');
			}
			else
			{
				$(fileMenu).find('li[data-silex-value="delete"]')[0].classList.remove('rightclick-context-menu-item-disabled');
			}
		}
		else
		{
			//paste
			if(!silex.controller.ControllerBase.clipboard 
			|| silex.controller.ControllerBase.clipboard.length === 0)
			{
				$(silexMenu).find('li[data-silex-value="paste"]')[0].classList.add('rightclick-context-menu-item-disabled');
			}
			else
			{
				$(silexMenu).find('li[data-silex-value="paste"]')[0].classList.remove('rightclick-context-menu-item-disabled');
			}
			// copy / delete
		    if (lastTarget.tagName.toLowerCase() === 'body'
			||  lastTarget.getAttribute('data-silex-id') === 'background-initial')
			{
				$(silexMenu).find('li[data-silex-value="copy"]')[0].classList.add('rightclick-context-menu-item-disabled');
				$(silexMenu).find('li[data-silex-value="delete"]')[0].classList.add('rightclick-context-menu-item-disabled');
			}
			else
			{
				$(silexMenu).find('li[data-silex-value="copy"]')[0].classList.remove('rightclick-context-menu-item-disabled');
				$(silexMenu).find('li[data-silex-value="delete"]')[0].classList.remove('rightclick-context-menu-item-disabled');
			}
		}
	}
	
	/**
	* Positions the menu properly.
	* 
	* @param {Object} e The event
	*/
	function positionMenu(e, menu) 
	{
		clickCoords = getPosition(e);
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
    function getPosition(e) 
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
