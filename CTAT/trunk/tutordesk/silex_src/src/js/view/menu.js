/**
 * Silex, live web creation
 * http://projects.silexlabs.org/?/silex/
 *
 * Copyright (c) 2012 Silex Labs
 * http://www.silexlabs.org/
 *
 * Silex is available under the GPL license
 * http://www.silexlabs.org/silex/silex-licensing/
 */

/**
 * @fileoverview
 * the Silex menu
 * based on closure menu class
 *
 */


goog.provide('silex.view.Menu');
//goog.require('goog.ui.Tooltip');
goog.require('goog.events.KeyCodes');
goog.require('goog.events.KeyHandler');
goog.require('goog.ui.KeyboardShortcutHandler');
goog.require('goog.ui.Menu');
goog.require('goog.ui.SubMenu');
goog.require('goog.ui.MenuButton');
goog.require('goog.ui.MenuItem');
goog.require('goog.ui.menuBar');
goog.require('silex.Config');



/**
 * @constructor
 * @param {Element} element   container to render the UI
 * @param  {!silex.types.Model} model  model class which holds
 *                                  the model instances - views use it for read operation only
 * @param  {!silex.types.Controller} controller  structure which holds
 *                                  the controller instances
 */
silex.view.Menu = function(element, model, controller, view) {
  // store references
  /**
   * @type {Element}
   */
  this.element = element;
  /**
   * @type {!silex.types.Model}
   */
  this.model = model;
  /**
   * @type {!silex.types.Controller}
   */
  this.controller = controller;
  
  this.view = view;
};


/**
 * reference to the menu class of the closure library
 */
silex.view.Menu.prototype.menu = null;

silex.view.Menu.prototype.groupMenu = null;

silex.view.Menu.prototype.noGroups = true;

silex.view.Menu.prototype.noStyles = true;

silex.view.Menu.prototype.noScripts = true;


/**
 * create the menu with closure API
 * called by the app constructor
 */
silex.view.Menu.prototype.buildUi = function() {

  this.menu = goog.ui.menuBar.create();

  // shortcut handler
  var shortcutHandler = new goog.ui.KeyboardShortcutHandler(document);
  var globalKeys = [];

  // create the menu items
  for (let i in silex.Config.menu.names) {
    // Create the drop down menu with a few suboptions.
    var menu = new goog.ui.Menu();
    goog.array.forEach(silex.Config.menu.options[i], (itemData) => {
      this.addToMenu(itemData, menu, shortcutHandler, globalKeys);
    }, this);

    // Create a button inside menubar.
    var menuItemData = silex.Config.menu.names[i];
    var btn = new goog.ui.MenuButton(menuItemData.label, menu);
    btn.addClassName(menuItemData.className);
    btn.setDispatchTransitionEvents(goog.ui.Component.State.ALL, true);
    this.menu.addChild(btn, true);
  }

  shortcutHandler.setAlwaysPreventDefault(false);
  //  shortcutHandler.setAllShortcutsAreGlobal(false);
  shortcutHandler.setModifierShortcutsAreGlobal(false);

  // shortcuts
  shortcutHandler.setGlobalKeys(globalKeys);
  goog.events.listen(
      shortcutHandler,
      goog.ui.KeyboardShortcutHandler.EventType.SHORTCUT_TRIGGERED,
      goog.bind(function(event) {
        if (!silex.utils.Notification.isActive) {
          event.preventDefault();
          this.onMenuEvent(event.identifier);
        }
      }, this)
  );
  // enter and escape shortcuts
  var keyHandler = new goog.events.KeyHandler(document);
  goog.events.listen(keyHandler, 'key', goog.bind(function(event) {
    if (!silex.utils.Notification.isActive) {
      // Allow ENTER to be used as shortcut for silex
      if (event.keyCode === goog.events.KeyCodes.ENTER &&
          event.shiftKey === false &&
          event.altKey === false &&
          event.ctrlKey === false) {
        // but not in text inputs
        if (event.target.tagName.toUpperCase() !== 'INPUT' &&
            event.target.tagName.toUpperCase() !== 'TEXTAREA') {
          // silex takes an action
          event.preventDefault();
          this.onMenuEvent('view.open.editor');
        }
        // else  {
          // let browser handle
        // }
      }
    }
  }, this));


  // render the menu
  this.menu.render(this.element);
  // event handling
  goog.events.listen(this.menu, goog.ui.Component.EventType.ACTION, function(e) {
    this.onMenuEvent(e.target.getId());
  }, false, this);
};


/**
 * add an item to the menu
 * @param {{mnemonic:goog.events.KeyCodes.<number>,checkable:boolean,id:string,shortcut:Array.<number>, globalKey:string, tooltip:goog.events.KeyCodes.<number>}} itemData menu item as defined in config.js
 * @param {goog.ui.Menu|goog.ui.SubMenu} menu
 * @param {goog.ui.KeyboardShortcutHandler} shortcutHandler
 * @param {Array.<Object>} globalKeys
 */
silex.view.Menu.prototype.addToMenu = function(itemData, menu, shortcutHandler, globalKeys) {
  var item;
  if (itemData) {
    // create the menu item
    var label = itemData.label;
    var id = itemData.id;
    if (itemData.subMenu)
    {
	  //make another menu...
      item = new goog.ui.SubMenu(label);
      if (itemData.id == 'view.groups')
	  {
		  this.groupMenu = item;
		  this.setHighlightHandler(item);
	  }
	  else if (itemData.id == 'view.stylesheets')
	  {
		  this.styleMenu = item;
	  }
	  else if (itemData.id == 'view.scripts')
	  {
		  this.scriptMenu = item;
	  }
	  var subItems = itemData.subMenu.options;
	  var subItem;
	  for (var i = 0; i < subItems.length; i++)
	  {
		  if (subItems[i])
		  {
			subItem = new goog.ui.MenuItem(subItems[i].label);
			subItem.setId(subItems[i].id);
			subItem.addClassName(subItems[i].className);
			if (subItems[i].id == 'view.groups.nogrp'
			||	subItems[i].id == 'view.stylesheets.nostyle'
			||	subItems[i].id == 'view.scripts.noscript')
			{
				subItem.setEnabled(false);
			}
		  }
		  else subItem = new goog.ui.MenuSeparator()
		 
		  item.addItem(subItem);
		  
		  if (subItems[i] && subItems[i].description)
		  {
			subItem.getElement().setAttribute('title', subItems[i].description);
		  }
	  }
    }
    else
    {
    	item = new goog.ui.MenuItem(label);
    }
    item.setId(id);
    item.addClassName(itemData.className);
    // checkable
    if (itemData.checkable) {
      item.setCheckable(true);
    }
    // mnemonic (access to an item with keyboard when the menu is open)
    if (itemData.mnemonic) {
      item.setMnemonic(itemData.mnemonic);
    }
    // shortcut
    if (itemData.shortcut) {
      for (let idx in itemData.shortcut) {
        try {
          shortcutHandler.registerShortcut(itemData.id, itemData.shortcut[idx]);
        }
        catch (e) {
          console.error('Catched error for shortcut', id, '. Error: ', e);
        }
        if (itemData.globalKey) {
          globalKeys.push(itemData.globalKey);
        }
      }
    }
  } else {
    item = new goog.ui.MenuSeparator();
  }
  //item.setDispatchTransitionEvents(goog.ui.Component.State.ALL, true);
  // add the menu item
  if(!itemData || !itemData.subMenu)
  {
  	menu.addChild(item, true);
  }
  else
  {
  	menu.addItem(item);
  }
  // add tooltip (has to be after menu.addItem)
  // TODO: add accelerator (only display shortcut here, could not get it to work automatically with closure's accelerator concept)
  if (itemData && itemData.tooltip) {
    // add label
    var div = goog.dom.createElement('span');
    div.innerHTML = itemData.tooltip;
    div.className = 'goog-menuitem-accel';
    item.getElement().appendChild(div);
    // add a real tooltip
    //new goog.ui.Tooltip(item.getElement(), itemData.tooltip);
  }
  if (itemData && itemData.description)
  {
	item.getElement().setAttribute('title', itemData.description);
  }
};

silex.view.Menu.prototype.setHighlightHandler = function(subMenu)
{
	var actualMenu = subMenu.getMenu();
	var super_handleHighlightItem = actualMenu.handleHighlightItem.bind(actualMenu);
	var super_handleUnHighlightItem = actualMenu.handleUnHighlightItem.bind(actualMenu);
	var menuPtr = this;
	
	actualMenu.handleHighlightItem = function(e)
		{
			super_handleHighlightItem(e);
			let highlightedEl = actualMenu.getHighlighted().getElement();
			let highlightedId = highlightedEl.getAttribute('id'); //view.groups.<groupName>
			menuPtr.view.stage.toggleGroupHighlight(highlightedId.substring(12), true); 
		};

	actualMenu.handleUnHighlightItem = function(e)
		{
			super_handleUnHighlightItem(e);
			let unHighlighted = e.target.getElement();
			let unHighlightedId = unHighlighted.getAttribute('id'); //view.groups.<groupName>
			menuPtr.view.stage.toggleGroupHighlight(unHighlightedId.substring(12), false);
		};
}

/**
 * redraw the menu
 * @param   {Array.<Element>} selectedElements the elements currently selected
 * @param   {Array.<string>} pageNames   the names of the pages which appear in the current HTML file
 * @param   {string}  currentPageName   the name of the current page
 */
silex.view.Menu.prototype.redraw = function(selectedElements, pageNames, currentPageName) {
};


/**
 * handles click events
 * calls onStatus to notify the controller
 * @param {string} type
 */
silex.view.Menu.prototype.onMenuEvent = function(type) {
  //deselect palette
  this.controller.componentPaletteController.unsetSelection();
  if (type.includes("insert.ctat") || type.includes("insert.question"))
  {	  
	this.controller.insertMenuController.addElement(type.substr(7));
  }
  else if (type.includes('view.groups.'))
  {
	this.controller.viewMenuController.editGroups(type.substr(12));
  }
  else if (type.includes('view.stylesheets.'))
  {
	this.removeAsset(type.substr(17), 'stylesheet');
  }
  else if (type.includes('view.scripts.'))
  {
	this.removeAsset(type.substr(13), 'script');
  }
  else if (type.includes('edit.arrange.'))
  {
	this.controller.editMenuController.arrange(type.substr(13));
  }
  else
  {
	switch (type) {
    case 'file.close':
    case 'file.new':
      this.controller.fileMenuController.newFile();
      break;
	case 'file.newpkg':
	  this.controller.fileMenuController.newPackage();
	  break;
    case 'file.saveas':
      this.controller.fileMenuController.save(true);
      break;
	case 'file.downloadpkg':
	  this.controller.fileMenuController.downloadPkg();
	  break;
    case 'file.save':
      this.controller.fileMenuController.save(false);
      break;
    case 'file.open':
      this.controller.fileMenuController.openFile();
      break;
    case 'view.demonstrate':
      this.controller.fileMenuController.demonstrate();
      break;
	case 'view.taborder':
	  this.controller.viewMenuController.displayTabOrder();
	  break;
	case 'view.css':
	  this.controller.viewMenuController.openCssEditor();
	  break;
    case 'tools.advanced.activate':
      this.controller.toolMenuController.toggleAdvanced();
      break;
    case 'insert.page':
      this.controller.insertMenuController.createPage();
      break;
    case 'insert.text':
      this.controller.insertMenuController.addElement(silex.model.Element.TYPE_TEXT);
      break;
    case 'insert.image':
      this.view.stage.showFileSourceWindow('image', 
										   this.controller.propertyToolController.setImgUrl,
										   function(fileData)
										   {
											 this.setBlobImgUrl(fileData);
										   }.bind(this.controller.propertyToolController));
      break;
    case 'insert.container':
      this.controller.insertMenuController.addElement(silex.model.Element.TYPE_CONTAINER);
      break;
	case 'insert.scrollcontainer':
	  this.controller.insertMenuController.addElement(silex.model.Element.TYPE_SCROLLCONTAINER);
	  break;
	case 'insert.group':
		this.controller.insertMenuController.addGroup();
	  break;
	case 'insert.stylesheet':
	  this.controller.insertMenuController.pickFile('stylesheet');
	  break;
	case 'insert.script':
	  this.controller.insertMenuController.pickFile('script');
	  break;
    case 'edit.delete.selection':
      // delete component
      this.controller.editMenuController.removeSelectedElements();
      break;
    case 'edit.copy.selection':
      this.controller.editMenuController.copySelection();
      break;
    case 'edit.paste.selection':
      this.controller.editMenuController.pasteSelection();
      break;
    case 'edit.undo':
      this.controller.editMenuController.undo();
      break;
    case 'edit.redo':
      this.controller.editMenuController.redo();
      break;
    case 'edit.move.up':
      this.controller.editMenuController.moveUp();
      break;
    case 'edit.move.down':
      this.controller.editMenuController.moveDown();
      break;
    case 'edit.move.to.top':
      this.controller.editMenuController.moveToTop();
      break;
    case 'edit.move.to.bottom':
      this.controller.editMenuController.moveToBottom();
      break;
	case 'edit.snap.to.grid':
		this.controller.editMenuController.toggleSnapToGrid();
	  break;
    // Help menu
    case 'help.about':
      window.open("https://github.com/CMUCTAT/CTAT/wiki"); 
      break;
    case 'help.issues':
      window.open("http://ctat.pact.cs.cmu.edu/index.php?id=contact"); 
      break;
    default:
      console.warn('menu type not found', type);
    }
  }
};

/**
*	Add an entry representing an asset (stylesheet or script) to the appropriate menu
*	@param assetName the canonical name of the file, used as menu item text
*	@param assetType the type of the file, either 'stylesheet' or 'script'
*/
silex.view.Menu.prototype.addAsset = function(assetName, assetType)
{
	var id, className, label;
	//set menu item id and classname
	if (assetType === 'stylesheet')
	{
		className = 'menu-item-view-remove-stylesheet';
		if (assetName === '<No Stylesheets>')
		{
			id = 'view.stylesheets.nostyle';
			label = assetName;
		}
		else
		{
			id = 'view.stylesheets.'+assetName;
			label = 'Remove '+assetName;
		}
	}
	else if (assetType === 'script')
	{
		className = 'menu-item-view-remove-script';
		if (assetName === '<No Scripts>')
		{
			id = 'view.scripts.noscript';
			label = assetName;
		}
		else
		{
			id = 'view.scripts.'+assetName;
			label = 'Remove '+assetName;
		}
	}
	let menu = (assetType === 'stylesheet') ? this.styleMenu : this.scriptMenu;
	//check if already in menu
	let cnt = menu.getItemCount();
	var found = false;
	for (let i = 0; i < cnt; i++)
	{
		if (menu.getItemAt(i).getId() === id)
		{
			found = true;
			break;
		}
	}
	//add to menu
	if (!found)
	{
		//menu item obj
		var menuItem = new goog.ui.MenuItem(label);
		menuItem.setId(id);
		menuItem.addClassName(className);
			
		menu.addItem(menuItem, true);
		if (assetType === 'stylesheet')
		{
			if (this.noStyles)
			{
				this.removeAsset('nostyle', 'stylesheet');
				this.noStyles = false;
			}
			else if (assetName === '<No Stylesheets>')
			{
				this.noStyles = true;
				menuItem.setEnabled(false);
			}
		}
		else if (assetType === 'script')
		{
			if (this.noScripts)
			{
				this.removeAsset('noscript', 'script');
				this.noScripts = false;
			}
			else if (assetName === '<No Scripts>')
			{
				this.noScripts = true;
				menuItem.setEnabled(false);
			}
		}
	}
};

/**
*	Remove an asset's entry from its menu
*	@param assetName the name of the asset file
*	@param assetType the type of the asset, either 'stylesheet' or 'script'
*/
silex.view.Menu.prototype.removeAsset = function(assetName, assetType)
{
	console.log('removeAsset: '+assetName);
	//remove from DOM
	var doc = this.model.file.getContentDocument();
	let tagName = (assetType === 'stylesheet') ? 'style' : 'script';
	var node = doc.head.querySelector(tagName+'[id="'+assetName+'"]');
	if (!node)
	{
		console.log('Couldn\'t find asset node');
	}
	else
	{
		doc.head.removeChild(node);
	}
	//remove from menu
	let menu = (assetType === 'stylesheet') ? this.styleMenu : this.scriptMenu;
	let itemId = (assetType === 'stylesheet') ? 'view.stylesheets.'+assetName : 'view.scripts.'+assetName ; 
	menu.removeItem(itemId);
	if (menu.getItemCount() == 0)
	{
		let noItem = (assetType === 'stylesheet') ? '<No Stylesheets>' : '<No Scripts>';
		this.addAsset(noItem, assetType);
	}
	
	this.view.stage.setStatus(assetName+' removed');
};

/**
*	Called when files are loaded, creates menu entries for all assets found in file
*	@param assetType the type of assets to load, 'script' or 'stylesheet'
*/
silex.view.Menu.prototype.populateAssetMenu = function(assetType)
{
	var doc = this.model.file.getContentDocument();
	var assets = (assetType === 'stylesheet') ? doc.querySelectorAll('.user-stylesheet') : doc.querySelectorAll('.user-script');
	var emptyFlag = (assetType === 'stylesheet') ? this.noStyles : this.noScripts;
	var menu = (assetType === 'stylesheet') ? this.styleMenu : this.scriptMenu;
	if (!emptyFlag)
	{
		//clear menu
		this.clearMenu(assetType, menu);
		
		if (assets.length == 0)
		{
			//add <no assets> item
			let noItem = (assetType === 'stylesheet') ? '<No Stylesheets>' : '<No Scripts>' ;
			this.addAsset(noItem, assetType);
		}
	}
	
	for (let i = 0; i < assets.length; i++)
	{
		let asset = assets.item(i);
		this.addAsset(asset.getAttribute('id'), assetType);
	}
};

/**
*	Populates View -> Groups menu with all groups in open file
*/
silex.view.Menu.prototype.populateGroupMenu = function()
{
	var doc = this.model.file.getContentDocument();
	var groupingComps = doc.querySelectorAll('.CTATGroupingComponent');
	if (!this.noGroups)
	{
		//clear menu
		this.clearMenu(null, this.groupMenu);
		
		if (groupingComps.length == 0)
		{
			//add <no assets> item
			this.addGroup('<No Groups>');
		}
	}
	
	for (let i = 0; i < groupingComps.length; i++)
	{
		let group = groupingComps.item(i);
		this.addGroup(group.getAttribute('id'));
	}
}

/**
*	Convenience function; calls populate functions for group, script, and stylesheet menus
*/
silex.view.Menu.prototype.populateMenus = function()
{
	this.populateAssetMenu('script');
	this.populateAssetMenu('stylesheet');
	this.populateGroupMenu();
}

/**
*	Clears all entries in a given menu
*	@param menuType the menu to clear, either 'script', 'stylesheet', or 'group'
*	@param menuObj the actual menu object
*	@param doAddEmptyItem whether or not to add the placeholder 'no <asset type>' to the empty menu
*/
silex.view.Menu.prototype.clearMenu = function(menuType, menuObj, doAddEmptyItem)
{
	console.log('clearMenu '+menuType+', doAddEmptyItem = '+doAddEmptyItem);
	if (!menuObj)
	{
		switch(menuType)
		{
			case 'stylesheet':
				menuObj = this.styleMenu;
			break;
			case 'script':
				menuObj = this.scriptMenu;
			break;
			case 'group':
				menuObj = this.groupMenu;
			break;
		}
	}
	var emptyItem;
	if (menuObj)
	{
		while(menuObj.getItemAt(0))
		{
			menuObj.removeItemAt(0);
		}
		if (doAddEmptyItem)
		{
			switch(menuType)
			{
				case 'stylesheet':
					emptyItem = '<No Stylesheets>';
					this.noStyles = false;
					this.addAsset(emptyItem, menuType);
				break;
				case 'script':
					emptyItem = '<No Scripts>';
					this.noScripts = false;
					this.addAsset(emptyItem, menuType);
				break;
				case 'group':
					emptyItem = '<No Groups>';
					this.noGroups = false;
					this.addGroup(emptyItem);
				break;
			}
		}
	}
};

/**
*	Convenience function; calls clear menu on group, stylesheet, and script menus
*/
silex.view.Menu.prototype.clearMenus = function()
{
	this.clearMenu('group', null, true);
	this.clearMenu('stylesheet', null, true);
	this.clearMenu('script', null, true);
};

/**
*	Add a menu item to the view -> groups submenu
*	@param groupName the name of the group (will be the label of the new menu item)
*/
silex.view.Menu.prototype.addGroup = function(groupName)
{
	console.log('menu.addGroup, childCount before = '+this.groupMenu.getItemCount());
	var id = (groupName === '<No Groups>') ? 'view.groups.nogrp' : 'view.groups.'+groupName;
	var menuItem = new goog.ui.MenuItem(groupName);
	menuItem.setId(id);
	menuItem.addClassName('menu-item-view-edit-group');
	this.groupMenu.addItem(menuItem, true);
	console.log('menu.addGroup, childCount after = '+this.groupMenu.getItemCount());
	if (this.noGroups)
	{
		this.removeGroup('nogrp');
		this.noGroups = false;
	}
	else if (groupName === '<No Groups>')
	{
		this.noGroups = true;
		menuItem.setEnabled(false);
	}
};


/**
*	Edit an existing group menu item
*	@param oldName the old name of the group
*	@param newName the new name for the group
*/
silex.view.Menu.prototype.setGroupName = function(oldName, newName)
{
	this.groupMenu.removeItem('view.groups.'+oldName);
	var newMenuItem = new goog.ui.MenuItem(newName);
	newMenuItem.setId('view.groups.'+newName);
	newMenuItem.addClassName('menu-item-view-edit-group');
	this.groupMenu.addItem(newMenuItem);
};

/**
*	Remove an item from the view -> groups submenu
*	@param groupName the name of the group to remove
*/
silex.view.Menu.prototype.removeGroup = function(groupName)
{
	this.groupMenu.removeItem('view.groups.'+groupName);
	console.log('group '+groupName+' removed, childCount = '+this.groupMenu.getItemCount());
	if (this.groupMenu.getItemCount() == 0)
	{
		this.addGroup('<No Groups>');
	}
};