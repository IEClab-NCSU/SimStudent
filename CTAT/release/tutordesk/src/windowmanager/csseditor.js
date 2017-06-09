 /**
 *	@fileoverview a class representing a dialog window used to 
 *	edit the content of a <style> tag.  Inherits from CTATDialogBase
 */

 /**
 *	@constructor
 *	@param windowId the id of the ctatdialog DOM node in which the dialog lives
 */
var CTATCSSEdit = function(windowId)
{
	CTATDialogBase.call (this, windowId, "CTATCSSEdit", "cssedit", "MODAL", true);
	var pointer = this;
	var textarea = document.querySelector('#css-editor-input');
	
	var defaultCbk = function(input)
	{
		console.log('csseditor default cbk');
	};
	
	var confirmCbk = null;
	
	this.initEvents = function()
	{
		$('#css-editor-confirm').on('click', function()
		{
			pointer.confirm();
		});
		
		$('#css-editor-cancel').on('click', function()
		{
			pointer.cancel();
		});
		
		textarea.addEventListener('keydown', function(event)
		{
			if(event.keyCode===9)
			{
				event.preventDefault();
				var v=this.value,s=this.selectionStart,e=this.selectionEnd;
				this.value=v.substring(0, s)+'\t'+v.substring(e);
				this.selectionStart=this.selectionEnd=s+1;
				return false;
			}
		}.bind(textarea));
	}
	
	/**
	*	Close the window and apply the content to the selected node
	*/
	this.confirm = function()
	{
		var text = $('#css-editor-input').val();
		var bracketCnt = text.match(/[{}]/g).length;
		if (!(bracketCnt%2 === 0))
		{
			alert('Invalid CSS: unmatched "{" or "}"');
		}
		else
		{	
			confirmCbk(text);
			pointer.close();
		}
	};
	
	this.cancel = function()
	{
		pointer.close();
	};
		
	/**
	 *	@Override CTATDialogBase.show()
	 *	@param content the text to preload the input with
	 */
	var super_show = this.show;
	this.show = function(content, optCbk)
	{
		super_show();
		
		$('#css-editor-input').val(content);		
		confirmCbk = optCbk || defaultCbk;
	};

	this.initEvents();
};

