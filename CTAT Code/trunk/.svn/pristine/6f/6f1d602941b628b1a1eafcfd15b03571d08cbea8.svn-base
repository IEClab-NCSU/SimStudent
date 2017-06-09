
var CTATConfirmDialog = function(windowId)
{
	CTATDialogBase.call (this, windowId, "CTATConfirmDialog", "confirmdialog", "MODAL", true);
	var pointer = this;
	var optionCbkMap = {};
	
	var super_show = this.show;
	this.show = function(msg, options)
	{
		optionCbkMap = {};
		var msgContainer = document.querySelector('#confirm-dialog-msg');
		var btnContainer = document.querySelector('#confirm-dialog-button-zone');
		var btn;
		btnContainer.innerHTML = '';
		msgContainer.textContent = msg;
		for (var i = 0; i < options.length; i++)
		{
			btn = document.createElement('button');
			btn.classList.add('confirm-dialog-option');
			btn.innerHTML = options[i].label;
			btn.addEventListener('click', function(optCbk){
				optCbk();
				pointer.close();
			}.bind(pointer, options[i].cbk));
			btnContainer.appendChild(btn);
		}
		
		super_show();
	}
}