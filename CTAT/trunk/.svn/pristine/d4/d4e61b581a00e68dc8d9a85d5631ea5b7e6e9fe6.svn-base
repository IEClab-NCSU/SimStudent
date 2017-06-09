/**
*	@fileoverview A class representing a context menu displayed on right click on the editor stage
*/

goog.provide('silex.SilexRightClickMenu');
silexRightClickMenu = function()
{
	//call super
	rightClickMenu.call(this, "silex-rightclick-context-menu", "silex-rightclick-context-menu-item");
	
	//last element right-clicked
	var lastTarget = null;
	
	var pointer = this;

	/**
	*	Reset the 'contextmenu' event listener.  Needs to be called on file open b/c
	*	the listener is set on the document element of the working file
	*/
	this.resetHandler = function()
	{
		var stageDoc = silexApp.model.file.getContentDocument();
		pointer.setHandler(stageDoc, function(e)
		{
			lastTarget = e.target;
			setEnabled();
			pointer.show(e);
			e.preventDefault();
		});
	};

	/**
	*	Enable or disable menu options based on event context
	*/
	var setEnabled = function()
	{
		//paste
		if(!silex.controller.ControllerBase.clipboard 
		||  silex.controller.ControllerBase.clipboard.length === 0)
		{
			pointer.disableOption('paste');
		}
		else
		{
			pointer.enableOption('paste');
		}
		// copy / delete
		if (lastTarget.tagName.toLowerCase() === 'body'
		||  lastTarget.getAttribute('data-silex-id') === 'background-initial')
		{
			pointer.disableOption('copy');
			pointer.disableOption('delete');
		}
		else
		{
			pointer.enableOption('copy');
			pointer.enableOption('delete');
		}
	};
	
	/**
	*	Called when 'copy' is clicked
	*/
	var handleCopy = function()
	{
		silexApp.controller.editMenuController.copySelection();
	};
	
	/**
	*	Called when 'paste' is clicked
	*/
	var handlePaste = function()
	{
		silexApp.controller.editMenuController.pasteSelection();
	};
	
	/**
	*	Called when 'delete' is clicked
	*/
	var handleDelete = function()
	{
		silexApp.controller.editMenuController.removeSelectedElements();
	};
	
	pointer.setOptionHandler('copy', handleCopy);
	pointer.setOptionHandler('paste', handlePaste);
	pointer.setOptionHandler('delete', handleDelete);
};