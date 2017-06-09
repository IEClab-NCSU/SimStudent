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
 * @fileoverview Silex config
 */


goog.provide('silex.Config');

goog.require('goog.events.KeyCodes');
goog.require('goog.ui.KeyboardShortcutHandler');

// display an apple on mac and ctrl on windows and linux
// var ctrlKeyMacDisplay = goog.userAgent.MAC ? '⌘' : '';
var altKeyMacDisplay = goog.userAgent.MAC ? '⌥' : '';
var ctrlKeyPCDisplay = goog.userAgent.MAC ? '' : 'Ctrl+';
// var altKeyPCDisplay = goog.userAgent.MAC ? '' : 'Alt+';
var ctrlKeyDisplay = goog.userAgent.MAC ? '⌘' : 'Ctrl+';
var altKeyDisplay = goog.userAgent.MAC ? '⌥' : 'Alt+';
// for shortcuts, use "apple key" on mac and ctrl on windows and linux
// var ctrlKeyMac = goog.userAgent.MAC ? goog.ui.KeyboardShortcutHandler.Modifiers.META : null;
var altKeyMac = goog.userAgent.MAC ? goog.ui.KeyboardShortcutHandler.Modifiers.ALT : null;
var ctrlKeyPC = goog.userAgent.MAC ? null : goog.ui.KeyboardShortcutHandler.Modifiers.CTRL;
// var altKeyPC = goog.userAgent.MAC ? null : goog.ui.KeyboardShortcutHandler.Modifiers.ALT;
// same shortcuts on mac and other
var ctrlKeyModifyer = goog.userAgent.MAC ? goog.ui.KeyboardShortcutHandler.Modifiers.META : goog.ui.KeyboardShortcutHandler.Modifiers.CTRL;
var altKeyModifyer = goog.userAgent.MAC ? goog.ui.KeyboardShortcutHandler.Modifiers.ALT : goog.ui.KeyboardShortcutHandler.Modifiers.ALT;


/**
 * The debug data
 * @struct
 */
silex.Config.debug = {
  /**
   * true if the app is in debug mode
   * if false then all other params are not used
   * debug mode is set to true in debug.html (src/html/debug.jade)
   * @type {boolean}
   */
  debugMode: false,
  /**
   * @type {boolean}
   */
  preventQuit: false,
};


/**
 * Link of the menu
 * @const
 */
silex.Config.ABOUT_SILEX = 'http://www.silex.me/';


/**
 * Link of the menu
 * @const
 */
silex.Config.ISSUES_SILEX = 'https://github.com/silexlabs/Silex/issues?state=open';


/**
 * Link of the menu
 * @const
 */
silex.Config.DOWNLOADS_TEMPLATE_SILEX = 'https://github.com/silexlabs/Silex/issues?labels=template&state=open';


/**
 * Link of the menu
 * @const
 */
silex.Config.DOWNLOADS_WIDGET_SILEX = 'https://github.com/silexlabs/Silex/issues?labels=widget&state=open';


/**
 * Link of the menu
 * @const
 */
silex.Config.ABOUT_SILEX_LABS = 'http://www.silexlabs.org/';


/**
 * Link of the menu
 * @const
 */
silex.Config.SUBSCRIBE_SILEX_LABS = 'http://eepurl.com/F48q5';


/**
 * Link of the menu
 * @const
 */
silex.Config.SOCIAL_GPLUS = 'https://plus.google.com/communities/107373636457908189681';


/**
 * Link of the menu
 * @const
 */
silex.Config.SOCIAL_TWITTER = 'http://twitter.com/silexlabs';


/**
 * Link of the menu
 * @const
 */
silex.Config.SOCIAL_FB = 'http://www.facebook.com/silexlabs';


/**
 * Link of the menu
 * @const
 */
silex.Config.FORK_CODE = 'https://github.com/silexlabs/Silex';


/**
 * Link of the menu
 * @const
 */
silex.Config.CONTRIBUTE = 'https://github.com/silexlabs/Silex/blob/master/docs/contribute.md';


/**
 * Link of the menu
 * @const
 */
silex.Config.CONTRIBUTORS = 'https://github.com/silexlabs/Silex/blob/master/docs/contributors.md';


/**
 * The main application menu
 */
silex.Config.menu = {
  names: [
    {
      label: 'File',
      className: 'menu-item-file'
    },
    {
      label: 'Edit',
      className: 'menu-item-edit'
    },
    {
      label: 'View',
      className: 'menu-item-view'
    },
    {
      label: 'Insert',
      className: 'menu-item-insert'
    },
    {
      label: 'Help',
      className: 'menu-item-help'
    }
  ],
  options: [
    [ //file
	  {
		label: 'New Package',
		id: 'file.newpkg',
		className: 'menu-item-file-newpkg',
		globalKey: goog.events.KeyCodes.P,
        shortcut: [[goog.events.KeyCodes.P, altKeyMac || ctrlKeyPC]],
        tooltip: altKeyMacDisplay + ctrlKeyPCDisplay + 'p',
        mnemonic: goog.events.KeyCodes.P,
        accelerator: 'p'
	  },
	  null,
      {
        label: 'New File',
        id: 'file.new',
        className: 'menu-item-file-new',
        globalKey: goog.events.KeyCodes.N,
        shortcut: [[goog.events.KeyCodes.N, altKeyMac || ctrlKeyPC]],
        tooltip: altKeyMacDisplay + ctrlKeyPCDisplay + 'n',
        mnemonic: goog.events.KeyCodes.N,
        accelerator: 'n'
      },
      {
        label: 'Open File',
        id: 'file.open',
        className: 'menu-item-file-open',
        globalKey: goog.events.KeyCodes.O,
        shortcut: [[goog.events.KeyCodes.O, ctrlKeyModifyer]],
        tooltip: ctrlKeyDisplay + 'o',
        mnemonic: goog.events.KeyCodes.O,
        accelerator: 'o'
      },
      {
        label: 'Save',
        id: 'file.save',
        className: 'menu-item-file-save',
        globalKey: goog.events.KeyCodes.S,
        shortcut: [[goog.events.KeyCodes.S, ctrlKeyModifyer]],
        tooltip: ctrlKeyDisplay + 's',
        mnemonic: goog.events.KeyCodes.S,
        accelerator: 's'
      },
      {
        label: 'Save As...',
        id: 'file.saveas',
        className: 'menu-item-file-saveas'
      },
      null,
	  {
		label: 'Download Package',
		id: 'file.downloadpkg',
		className: 'menu-item-file-downloadpkg'
	  }
    ],
    [ //edit
      {
        label: 'Copy',
        id: 'edit.copy.selection',
        className: 'menu-item-edit-copy-selection',
        shortcut: [[goog.events.KeyCodes.C, ctrlKeyModifyer]],
        tooltip: ctrlKeyDisplay + 'C',
        mnemonic: goog.events.KeyCodes.C,
        accelerator: 'c'
      },
      {
        label: 'Paste',
        id: 'edit.paste.selection',
        className: 'menu-item-edit-paste-selection',
        shortcut: [[goog.events.KeyCodes.V, ctrlKeyModifyer]],
        tooltip: ctrlKeyDisplay + 'V',
        mnemonic: goog.events.KeyCodes.V,
        accelerator: 'v'
      },
      {
        label: 'Undo',
        id: 'edit.undo',
        className: 'menu-item-edit-undo',
        shortcut: [[goog.events.KeyCodes.Z, ctrlKeyModifyer]],
        tooltip: ctrlKeyDisplay + 'Z',
        mnemonic: goog.events.KeyCodes.Z,
        accelerator: 'z'
      },
      {
        label: 'Redo',
        id: 'edit.redo',
        className: 'menu-item-edit-redo',
        shortcut: [[goog.events.KeyCodes.Z, ctrlKeyModifyer + goog.ui.KeyboardShortcutHandler.Modifiers.SHIFT]],
        tooltip: ctrlKeyDisplay + '⇧ Z'
      },
     {
        label: 'Delete selection',
        id: 'edit.delete.selection',
        className: 'menu-item-edit-delete-selection',
        shortcut: [[goog.events.KeyCodes.DELETE], [goog.events.KeyCodes.BACKSPACE]],
        tooltip: 'Del',
        mnemonic: goog.events.KeyCodes.R,
        accelerator: 'r'
      },
      null,
	  {
		  label: 'Disable Snap to Grid',
		  id: 'edit.snap.to.grid',
		  className: 'menu-item-edit-snap-to-grid'
	  },
      {
        label: 'Bring to front',
        id: 'edit.move.to.top',
        className: 'menu-item-edit-move-to-top',
        shortcut: [[goog.events.KeyCodes.UP, altKeyModifyer + goog.ui.KeyboardShortcutHandler.Modifiers.SHIFT]],
        tooltip: altKeyDisplay + '⇧ Up'
      },
      {
        label: 'Bring forward',
        id: 'edit.move.up',
        className: 'menu-item-edit-move-up',
        shortcut: [[goog.events.KeyCodes.UP, altKeyModifyer]],
        tooltip: altKeyDisplay + 'Up',
        mnemonic: goog.events.KeyCodes.UP
      },
      {
        label: 'Send backward',
        id: 'edit.move.down',
        className: 'menu-item-edit-move-down',
        shortcut: [[goog.events.KeyCodes.DOWN, altKeyModifyer]],
        tooltip: altKeyDisplay + 'Down',
        mnemonic: goog.events.KeyCodes.DOWN
      },
      {
        label: 'Send to back',
        id: 'edit.move.to.bottom',
        className: 'menu-item-edit-move-to-bottom',
        shortcut: [[goog.events.KeyCodes.DOWN, altKeyModifyer + goog.ui.KeyboardShortcutHandler.Modifiers.SHIFT]],
        tooltip: altKeyDisplay + '⇧ Down'
      },
	  null,
	  {
		  label: 'Arrange',
		  id: 'edit.arrange',
		  className: 'menu-item-edit-arrange',
		  subMenu: {
			  options: [
				{
				  label: 'Align left',
				  id: 'edit.arrange.align.left',
				  className: 'menu-item-edit-arrange-align-left',
				  description: 'Align a group of elements to the left side of their bounding box'
				},
				{
					label: 'Align center (horizontal)',
					id: 'edit.arrange.align.center.x',
					className: 'menu-item-edit-arrange-align-center-x',
					description: 'Align a group of elements to the horizontal center of their bounding box'
				},
				{
					label: 'Align right',
					id: 'edit.arrange.align.right',
					className: 'menu-item-edit-arrange-align-right',
					description: 'Align a group of elements to the right side of their bounding box'
				},
				null,
				{
					label: 'Align top',
					id: 'edit.arrange.align.top',
					className: 'menu-item-edit-arrange-align-top',
					description: 'Align a group of elements to the top of their bounding box'
				},
				{
					label: 'Align center (vertical)',
					id: 'edit.arrange.align.center.y',
					className: 'menu-item-edit-arrange-align-center-y',
					description: 'Align a group of elements to the vertical center of their bounding box'
				},
				{
					label: 'Align bottom',
					id: 'edit.arrange.align.bottom',
					className: 'menu-item-edit-arrange-align-bottom',
					description: 'Align a group of elements to the bottom of their bounding box'
				},
				null,
				{
					label: 'Distribute horizontal',
					id: 'edit.arrange.distribute.x',
					className: 'menu-item-edit-arrange-distribute-x',
					description: 'Equally space elements horizontally'
				},
				{
					label: 'Distribute vertical',
					id: 'edit.arrange.distribute.y',
					className: 'menu-item-edit-arrange-distribute-y',
					description: 'Equally space elements vertically'
				}
			  ]
		  }
	  }
    ],
    [ //view
      {
        label: 'Launch Interface',
        id: 'view.demonstrate',
        className: 'menu-item-view-demonstrate',
        shortcut: [[goog.events.KeyCodes.V, altKeyModifyer]],
        tooltip: altKeyDisplay + 'V',
        mnemonic: goog.events.KeyCodes.V,
        accelerator: 'v'
      },
      {
        label: 'Show Tab Order',
        id: 'view.taborder',
        className: 'menu-item-view-taborder',
        checkable: true,
        shortcut: [[goog.events.KeyCodes.A, altKeyModifyer + ctrlKeyModifyer + goog.ui.KeyboardShortcutHandler.Modifiers.SHIFT]],
        tooltip: ctrlKeyDisplay + altKeyDisplay + '⇧ A',
        mnemonic: goog.events.KeyCodes.A,
        accelerator: 'a'
      },
	  {
		  label: 'Groups',
		  id: 'view.groups',
		  className: 'menu-item-view-groups',
		  subMenu: {
			  options: [
				{
					label: '<No Groups>',
					id: 'view.groups.nogrp',
					className: 'menu-item-view-no-groups'
				}
			  ]
		  }
	  },
	  {
		  label: 'Stylesheets',
		  id: 'view.stylesheets',
		  className: 'menu-item-view-stylesheets',
		  subMenu: {
			  options: [
				{
					label: '<No Stylesheets>',
					id: 'view.stylesheets.nostyle',
					className: 'menu-item-view-no-styles'
				}
			  ]
		  }
	  },
	  {
		  label: 'Scripts',
		  id: 'view.scripts',
		  className: 'menu-item-view-scripts',
		  subMenu: {
			  options: [
				{
					label: '<No Scripts>',
					id: 'view.scripts.noscript',
					className: 'menu-item-view-no-scripts'
				}
			  ]
		  }
	  },
	  null,
	  {
		  label: 'Custom CSS',
		  id: 'view.css',
		  className: 'menu-item-view-css'
	  }
    ],
    [ //insert
      {
        label: 'Image',
        id: 'insert.image',
        className: 'menu-item-insert-image',
        globalKey: goog.events.KeyCodes.I,
        shortcut: [[goog.events.KeyCodes.I, altKeyModifyer]],
        tooltip: altKeyDisplay + 'I',
        mnemonic: goog.events.KeyCodes.I,
        accelerator: 'i'
      },
      {
        label: 'Container',
        id: 'insert.container',
        className: 'menu-item-insert-container',
        shortcut: [[goog.events.KeyCodes.C, altKeyModifyer]],
        tooltip: altKeyDisplay + 'C',
        mnemonic: goog.events.KeyCodes.C,
        accelerator: 'c'
      },
      null,
	  {
        label: 'CTAT Component',
        id: 'insert.ctat',
        className: 'submenu-item-insert-ctat',
		subMenu:
		{
			options:
			[
				{
					label: "Audio Button",
					id: "insert.ctat.audiobutton",
					className: "menu-item-insert-ctat-audiobutton",
					description: "A button that can be used to trigger the playing of an audio sample"
				},
				{
					label: "Button",
					id: "insert.ctat.button",
					className: "menu-item-insert-ctat-button",
					description: "A generic button"
				},
				{
					label: "Checkbox",
					id: "insert.ctat.checkbox",
					className: "menu-item-insert-ctat-checkbox",
					description: "A generic checkbox"
				},
				{
					label: "Combo Box",
					id: "insert.ctat.combobox",
					className: "menu-item-insert-ctat-combobox",
					description: "A generic dropdown combo box"
				},
				{
					label: "Done Button",
					id: "insert.ctat.donebutton",
					className: "menu-item-insert-ctat-donebutton",
					description: "A button to signify the student is finished"
				},
				{
					label: "Drag N' Drop",
					id: "insert.ctat.dragndrop",
					className: "menu-item-insert-ctat-dragndrop",
					description: "A pane which non-interactive elements can be dragged into and out of"
				},
				{
					label: "Fraction Bar",
					id: "insert.ctat.fractionbar",
					className: "menu-item-insert-ctat-fractionbar",
					description: "An interactive, rectangular display of fractional values"
				},
				{
					label: "Grouping Container",
					id: "insert.ctat.groupingcontainer",
					className: "menu-item-insert-ctat-groupingcontainer",
					description: "A container which can be used to control child components with tutor-performed steps"
				},
				{
					label: "Hint Button",
					id: "insert.ctat.hintbutton",
					className: "menu-item-insert-ctat-hintbutton",
					description: "A button to trigger tutor hints"
				},
				{
					label: "Hint Widget",
					id: "insert.ctat.hintwidget",
					className: "menu-item-insert-ctat-hintwidget",
					description: "A cluster of elements including a hint button, a hint display window, and a done button"
				},
				{
					label: "Hint Window",
					id: "insert.ctat.hintwindow",
					className: "menu-item-insert-ctat-hintwindow",
					description: "A pane for displaying tutor hints"
				},
				{
					label: "Image Button",
					id: "insert.ctat.imagebutton",
					className: "menu-item-insert-ctat-imagebutton",
					description: "A button that can be customized to display different images when clicked, disabled, or hovered over"
				},
				{
					label: "Jumble",
					id: "insert.ctat.jumble",
					className: "menu-item-insert-ctat-jumble",
					description: "A pane in which non-interactive elements can be re-arranged by clicking and dragging"
				},
				{
					label: "Number Line",
					id: "insert.ctat.numberline",
					className: "menu-insert-ctat-numberline",
					description: "An interactive display of a range of numerical values"
				},
				{
					label: 'Numeric Stepper',
					id: "insert.ctat.numericstepper",
					className: "menu-insert-ctat-numericstepper",
					description: "An input that accepts numeric values only"
				},
				{
					label: "Pie Chart",
					id: "insert.ctat.piechart",
					className: "menu-insert-ctat-piechart",
					description: "An interactive, circular display of fractional values"
				},
				{
					label: "Radio Button",
					id: "insert.ctat.radiobutton",
					className: "menu-item-insert-ctat-radiobutton",
					description: "A generic radio button"
				},
				{
					label: "Skill Window",
					id: "insert.ctat.skillwindow",
					className: "menu-item-insert-ctat-skillwindow",
					description: "A pane in which skill meters relevant to the problem can be displayed"
				},
				{
					label: "Submit Button",
					id: "insert.ctat.submitbutton",
					className: "menu-item-insert-ctat-submitbutton",
					description: "A button that can be used to trigger grading on other components or groups of components"
				},
				{
					label: "Table",
					id: "insert.ctat.table",
					className: "menu-item-insert-ctat-table",
					description: "A grid of interactive text inputs"
				},
				{
					label: "Text Area",
					id: "insert.ctat.textarea",
					className: "menu-item-insert-ctat-textarea",
					description: "A multi-line, interactive text box"
				},
				{
					label: "Text Field",
					id: "insert.text",
					className: "menu-item-insert-ctat-textfield",
					description: "A non-interactive, static text block"
				},
				{
					label: "Text Input",
					id: "insert.ctat.textinput",
					className: "menu-item-insert-ctat-textinput",
					description: "A single-line, interactive text box"
				},
				{
					label: "Video",
					id: "insert.ctat.video",
					className: "menu-item-insert-ctat-video",
					description: "An embedded video player"
				}
			]
		}
      },
	  {
		label: 'Grouping List',
		id: 'insert.group',
		className: 'menu-item-edit-create-group',
		description: 'Define a list of existing components to be controlled together with tutor-performed steps'
	  },
	  {
		label: "Multiple Choice",
		id: 'insert.question.multchoice',
		className: 'menu-item-insert-question-multchoice',
		description: 'Create a multiple-choice question'
	  },
	  null,
	  {
		label: 'Stylesheet',
		id: 'insert.stylesheet',
		className: 'submenu-item-insert-stylesheet',
		description: 'Import a stylesheet from cloud storage'
	  },
	  {
		label: 'JS Script',
		id: 'insert.script',
		className: 'submenu-item-insert-script',
		description: 'Import a script from cloud storage'
	  }
    ],
    [ //help
      {
        label: 'CTAT HTML Documentation',
        id: 'help.about',
        className: 'menu-item-help-about'
      },
      {
        label: 'Report a bug or ask a question',
        id: 'help.issues',
        className: 'menu-item-help-issues'
      }
    ]
  ]
};


/**
 * The list of fonts the user can select
 */
silex.Config.fonts = {


  'Roboto Condensed': {
    //the url to load the font file
    href: 'http://fonts.googleapis.com/css?family=Roboto+Condensed:300italic,400italic,700italic,400,300,700',
    //the value for the CSS font-family value
    value: 'Roboto Condensed'
  },
  'Roboto': {

    href: 'http://fonts.googleapis.com/css?family=Roboto:400,100,100italic,300,300italic,400italic,500,500italic,700,700italic,900,900italic',

    value: 'Roboto'
  },
  'Days One': {

    href: 'http://fonts.googleapis.com/css?family=Days+One',

    value: 'Days One'
  },
  'Sintony': {

    href: 'http://fonts.googleapis.com/css?family=Sintony:400,700',

    value: 'Sintony'
  },
  'Junge': {

    href: 'http://fonts.googleapis.com/css?family=Junge',

    value: 'Junge'
  },
  'Istok Web': {

    href: 'http://fonts.googleapis.com/css?family=Istok+Web:400,700,400italic,700italic',

    value: 'Istok Web'
  },
  'Oswald': {

    href: 'http://fonts.googleapis.com/css?family=Oswald:400,300,700',

    value: 'Oswald'
  },
  'Cantata': {

    href: 'http://fonts.googleapis.com/css?family=Cantata+One',

    value: 'Cantata'
  },
  'Oranienbaum': {

    href: 'http://fonts.googleapis.com/css?family=Oranienbaum',

    value: 'Oranienbaum'
  },
  'Londrina Solid': {

    href: 'http://fonts.googleapis.com/css?family=Londrina+Solid',

    value: 'Londrina Solid'
  },
  'Noticia Text': {

    href: 'http://fonts.googleapis.com/css?family=Noticia+Text:400,400italic,700,700italic',

    value: 'Noticia Text'
  },
  'Codystar': {

    href: 'http://fonts.googleapis.com/css?family=Codystar:300,400',

    value: 'Codystar'
  },
  'Titillium Web': {

    href: 'http://fonts.googleapis.com/css?family=Titillium+Web:400,200,200italic,300,300italic,400italic,600,600italic,700,700italic,900',

    value: 'Titillium Web'
  },
  'Sarina': {

    href: 'http://fonts.googleapis.com/css?family=Sarina',

    value: 'Sarina'
  },
  'Bree Serif': {

    href: 'http://fonts.googleapis.com/css?family=Bree+Serif',

    value: 'Bree Serif'
  }
};

/**
*	Map of whether certain ui components should be active based on current selection
*	className -> [disabled-comp1, disabled-comp2, ...]
*	'default' is an array of all possible disabled components
*/
silex.Config.UIDisabled = {
	'default': ['fgColor', 'bgColor', 'position', 'border', 'bgImage'],
	'body-initial': ['fgColor', 'border', 'position'],
	'background-initial': ['fgColor', 'position'],
	'CTATHintWidget': ['fgColor', 'bgColor', 'bgImage'],
	'CTATDragNDrop': ['fgColor'],
	'CTATJumble': ['fgColor'],
	'CTATImageButton': ['bgImage'],
	'CTATVideo': ['bgImage', 'fgColor', 'bgColor'],
	'CTATCheckBox': ['bgImage'],
	'CTATComboBox': ['bgImage'],
	'CTATFractionBar': ['bgImage'],
	'CTATPieChart': ['bgImage'],
	'CTATHintButton': ['bgImage'],
	'CTATHintWindow': ['bgImage'],
	'CTATNumberLine': ['bgImage'],
	'CTATNumericStepper': ['bgImage'],
	'CTATRadioButton': ['bgImage'],
	'CTATSkillWindow': ['bgImage'],
	'CTATTable': ['bgImage'],
	'CTATTextArea': ['bgImage'],
	'CTATTextInput': ['bgImage'],
	'image-element': ['fgColor', 'bgColor', 'bgImage']
};

/**
*	These classes are used internally by the editor/CTAT code, so
*	are restricted from appearing in the 'classes' field of the property tool
*/
silex.Config.RestrictedClasses = {
	'editable-style': true,
	'container-element': true,
	'question.multchoice-element': true,
	'prevent-draggable': true,
	'background': true,
	'background-initial': true,
	'silex-just-added': true,
	'silex-selected': true,
	'page-page-1': true,
	'paged-element': true,
	'text-element': true,
	'CTATComponent': true,
	'image-element': true,
	'CTATTextField': true,
	'CTATTextArea': true,
	'CTATTextInput': true,
	'CTATDragNDrop': true,
	'CTATJumble': true,
	'CTATHintWidget': true,
	'CTATFractionBar': true,
	'CTATPieChart': true,
	'CTATComboBox': true,
	'CTATCheckBox': true,
	'CTATRadioButton': true,
	'CTATNumberLine': true,
	'CTATButton': true,
	'CTATNumericStepper': true,
	'CTATVideo': true,
	'CTATAudioButton': true,
	'CTATImageButton': true,
	'CTATSubmitButton': true,
	'CTATTable': true,
	'CTATDoneButton': true,
	'CTATHintButton': true,
	'CTATHintWindow': true,
	'CTATSkillWindow': true,
	'CTATGroupingComponent': true,
	'body-initial': true,
	'pageable-plugin-created': true,
	'drop-zone-candidate': true,
	'dragging-pending': true,
	'paged-element-visible': true
};

silex.Config.componentPaletteList = {
	html: [
		{
			'name': 'Container',
			'type': 'container',
			'size': '100x100',
			'description': 'Insert a container'
		},
		{
			'name': 'Image',
			'type': 'image',
			'size': '100x100',
			'description': 'Insert an image'
		}
	],
	ctatStandard : [
		{
			'name': 'Audio Button',
			'type': 'ctat.audiobutton',
			'size': '155x30',
			'description': "A button that can be used to trigger the playing of an audio sample"
		},
		{
			'name': 'Button',
			'type': 'ctat.button',
			'size': '60x30',
			'description': "A generic button"
		},
		{
			'name': 'Checkbox',
			'type': 'ctat.checkbox',
			'size': '100x23',
			'description': "A generic checkbox"
		},
		{
			'name': 'Combo Box',
			'type': 'ctat.combobox',
			'size': '100x23',
			'description': "A generic dropdown combo box"
		},
		{
			'name': 'Done Button',
			'type': 'ctat.donebutton',
			'size': '64x64',
			'description': "A button to signify the student is finished"
		},
		{
			'name': 'Drag N\' Drop',
			'type': 'ctat.dragndrop',
			'size': '200x110',
			'description': "A pane which non-interactive elements can be dragged into and out of"
		},
		{
			'name': 'Fraction Bar',
			'type': 'ctat.fractionbar',
			'size': '240x70',
			'description': "An interactive, rectangular display of fractional values"
		},
		{
			'name': 'Grouping Container',
			'type': 'ctat.groupingcontainer',
			'size': '100x100',
			'description': "A container which can be used to control child components with tutor-performed steps"
		},
		{
			'name': 'Hint Button',
			'type': 'ctat.hintbutton',
			'size': '64x64',
			'description': "A button to trigger tutor hints"
		},
		{
			'name': 'Hint Window',
			'type': 'ctat.hintwindow',
			'size': '240x140',
			'description': "A pane for displaying tutor hints"
		},
		{
			'name': 'Image Button',
			'type': 'ctat.imagebutton',
			'size': '64x64',
			'description': "A button that can be customized to display different images when clicked, disabled, or hovered over"
		},
		{
			'name': 'Jumble',
			'type': 'ctat.jumble',
			'size': '150x40',
			'description': "A pane in which non-interactive elements can be re-arranged by clicking and dragging"
		},
		{
			'name': 'Numberline',
			'type': 'ctat.numberline',
			'size': '360x90',
			'description': "An interactive display of a range of numerical values"
		},
		{
			'name': 'Numeric Stepper',
			'type': 'ctat.numericstepper',
			'size': '60x23',
			'description': 'An input that accepts numeric values only'
		},
		{
			'name': 'Pie Chart',
			'type': 'ctat.piechart',
			'size': '100x100',
			'description': "An interactive, circular display of fractional values"
		},
		{
			'name': 'Radio Button',
			'type': 'ctat.radiobutton',
			'size': '100x23',
			'description': "A generic radio button"
		},
		{
			'name': 'Skill Window',
			'type': 'ctat.skillwindow',
			'size': '240x140',
			'description': "A pane in which skill meters relevant to the problem can be displayed"
		},
		{
			'name': 'Submit Button',
			'type': 'ctat.submitbutton',
			'size': '60x30',
			'description': "A button that can be used to trigger grading on other components or groups of components"
		},
		{
			'name': 'Table',
			'type': 'ctat.table',
			'size': '132x42',
			'description': "A grid of interactive text inputs"
		},
		{
			'name': 'Text Area',
			'type': 'ctat.textarea',
			'size': '100x32',
			'description': "A multi-line, interactive text box"
		},
		{
			'name': 'Text Field',
			'type': 'text',
			'size': '100x100',
			'description': "A non-interactive, static text block"
		},
		{
			'name': 'Text Input',
			'type': 'ctat.textinput',
			'size': '100x23',
			'description': "A single-line, interactive text box"
		},
		{
			'name': 'Video',
			'type': 'ctat.video',
			'size': '250x200',
			'description': 'An embedded video player'
		}
	],
	ctatComposite: [
		{
			'name': 'Grouping List',
			'type': 'ctat.groupingcomponent',
			'size': '0x0',
			'description': "Group existing components to be graded together"
		},
		{
			'name': 'Hint Widget',
			'type': 'ctat.hintwidget',
			'size': '306x140',
			'description': "A cluster of elements including a hint button, a hint display window, and a done button"
		},
		{
			'name': 'Multiple Choice',
			'type': 'question.multchoice',
			'size': '200x100',
			'description': "A generated multiple choice question"
		}
	]
}

/*
	Map of CTAT class names that need to be re-rendered on resize
*/
silex.Config.needsRendered = {
	'CTATFractionBar': true,
	'CTATPieChart': true,
	'CTATNumberLine': true,
	'CTATVideo': true
}
