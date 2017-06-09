 /**
 *	@fileoverview a class representing a dialog window used to 
 *	edit the content of a text block.  Inherits from CTATDialogBase
 */

 /**
 *	@constructor
 *	@param windowId the id of the ctatdialog DOM node in which the dialog lives
 */
var CTATTextEdit = function(windowId)
{
	CTATDialogBase.call (this, windowId, "CTATTextEdit", "textedit", "MODAL", true);
	
	var pointer = this;
	this.selected = null;

	var defaultCbk = function(input)
	{
		var contentNode = $(pointer.selected).find('.silex-element-content')[0];
		input = input.replace(/\n/g, '<br>');
		contentNode.innerHTML = input;
	};
	
	var confirmCbk;
	
	this.initEvents = function()
	{
		$('#text-editor-confirm').on('click', function()
		{
			pointer.confirm();
		});
		
		$('#text-editor-cancel').on('click', function()
		{
			pointer.cancel();
		});
	/*
		$('#text-editor-font-size').on('change', function(event)
		{
			pointer.setFontSize($(event.target).val());
		});
		
		$('#text-edit-fontselect').fontSelector({
			'hide_fallbacks' : true,
			'initial' : 'Courier New,Courier New,Courier,monospace',
			'selected' : function(style) 
				{ 
					pointer.setFontFamily(style); 
				},
			'opened' : function(style) { console.log('font selector opened'); },
			'closed' : function(style) { console.log('font selector closed'); },
			'fonts' : [
				'Arial,Arial,Helvetica,sans-serif',
				'Arial Black,Arial Black,Gadget,sans-serif',
				'Comic Sans MS,Comic Sans MS,cursive',
				'Courier New,Courier New,Courier,monospace',
				'Georgia,Georgia,serif',
				'Impact,Charcoal,sans-serif',
				'Lucida Console,Monaco,monospace',
				'Lucida Sans Unicode,Lucida Grande,sans-serif',
				'Palatino Linotype,Book Antiqua,Palatino,serif',
				'Tahoma,Geneva,sans-serif',
				'Times New Roman,Times,serif',
				'Trebuchet MS,Helvetica,sans-serif',
				'Verdana,Geneva,sans-serif',
				'Gill Sans,Geneva,sans-serif'
			]
		});
		
		$('#text-editor-bold').on('click', pointer.toggleBold);
		
		$('#text-editor-italic').on('click', pointer.toggleItalic);
		
		$('#text-editor-italic > .windowclose').on('click', pointer.close);
	*/
	}
	
	/**
	*	Close the window and apply the content to the selected node
	*/
	this.confirm = function()
	{
		var text = $('#text-editor-input').val();
		confirmCbk(text);
		pointer.close();
	};
	
	this.cancel = function()
	{
		pointer.close();
	};
	
	this.toggleBold = function()
	{
		console.log('toggleBold');
		if (pointer.fontWeight === 'bold')
		{
			pointer.setFontWeight('normal');
		}
		else
		{
			pointer.setFontWeight('bold');
		}
	};
	
	this.toggleItalic = function()
	{
		console.log('toggleItalic');
		if (pointer.fontStyle === 'italic')
		{
			pointer.setFontStyle('normal');
		}
		else
		{
			pointer.setFontStyle('italic');
		}
	};
	
	/**
	 *	@Override CTATDialogBase.show()
	 *	@param selectedElement the currently selected DOM node
	 */
	var super_show = this.show;
	this.show = function(selectedElement, optCbk)
	{
		super_show();
		//fill dialog inputs in w/ values from selected node
		pointer.selected = selectedElement;
		
		//text content
		var contentNode = $(pointer.selected).find('.silex-element-content')[0];
		var htmlContent = contentNode.innerHTML;
		htmlContent = htmlContent.replace(/<br>/g, '\n');
		$('#text-editor-input').val(htmlContent);
	/*	
		//font size
		var sizeStr = window.silexApp.model.element.getStyle(selectedElement, 'font-size') || '16px';
		sizeStr = sizeStr.replace('px', '');
		this.setFontSize(sizeStr);
		$('#text-editor-font-size').val(sizeStr);
		//font family
		pointer.fontFamily = window.silexApp.model.element.getStyle(selectedElement, 'font-family') ||
			'Arial, Helvetica, sans-serif';
		console.log('font family = '+pointer.fontFamily);
		$('#text-edit-fontselect').fontSelector('select', pointer.fontFamily);	
		//bold
		this.setFontWeight(window.silexApp.model.element.getStyle(selectedElement, 'font-weight'));		
		//italic
		this.setFontStyle(window.silexApp.model.element.getStyle(selectedElement, 'font-style'));
	*/	
		confirmCbk = optCbk || defaultCbk;
	};
	
	this.setFontSize = function(size)
	{
		var regex = /\d{1,}/;
		if (regex.test(size))
		{
			pointer.fontSize = size+'px';
			$('#text-editor-input').css('font-size', pointer.fontSize);
		}
	}
	
	this.setFontFamily = function(family)
	{
		pointer.fontFamily = family;
		$('#text-editor-input').css('font-family', pointer.fontFamily);
	};
	
	this.setFontWeight = function(weight)
	{
		pointer.fontWeight = (weight === 'normal' || weight === 'bold') ? weight : 'normal';
		$('#text-editor-input').css('font-weight', pointer.fontWeight);
	};
	
	this.setFontStyle = function(style)
	{
		pointer.fontStyle = (style === 'normal' || style === 'italic') ? style : 'normal';
		$('#text-editor-input').css('font-style', pointer.fontStyle);	
	};
	
	this.initEvents();
};

