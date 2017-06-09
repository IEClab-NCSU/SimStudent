/**
 *	@fileoverview a class which represents a dialog window to be used for creating
 *	component groups in the HTML editor.  Inherits from CTATDialogBase
 */

/**
 *	@Constructor
 *	@param windowId the id of the ctatdialog DOM node where this instance will live
 */
var CTATGroupDialog = function(windowId)
{
	CTATDialogBase.call (this, windowId, "CTATGroupDialog", "groupdialog", "", true);
	
	var pointer = this;
	var compList = document.getElementById('group-dialog-member-list');
	var ids = [];
	var confirmBtn = document.getElementById('group-dialog-confirm');
	var deleteBtn = document.getElementById('group-dialog-delete');
	var cancelBtn = document.getElementById('group-dialog-cancel');
	var addBtn = document.getElementById('group-dialog-add-member');
	var idField = document.getElementById('group-dialog-id-field');
	var nameField = document.getElementById('group-dialog-name');
	var mode = null;
	var oldName = null;
	
/////// ---- PUBLIC METHODS ---- ///////
	
	/**
	 *	@Override CTATDialogBase.show
	 */
	var super_show = this.show;
	this.show = function(m, selectedElements, groupName)
	{
		super_show();
		
		clear();
		mode = m;
		this.selected = selectedElements;
		if (mode === 'create')
		{	
			$('#group-dialog-title').text('New Group');
			//create li for each of selectedElements that is a CTAT component
			for (var i = 0; i < selectedElements.length; i++)
			{
				if (selectedElements[i].className.includes('CTAT'))
					addToList(selectedElements[i].getAttribute('id'));
			}
			oldName = null;
			deleteBtn.setAttribute('disabled','true');
		}
		else
		{
			$('#group-dialog-title').text('Edit Group');
			nameField.value = groupName;
			for (var i = 0; i < selectedElements.length; i++)
			{
				addToList(selectedElements[i]);
			}
			oldName = groupName;
			deleteBtn.removeAttribute('disabled');
		}
	};
	
	
	/**
	 *	@Override CTATDialogBase.close()
	 */
	var super_close = this.close;
	this.close = function()
	{
		super_close();
	}
	
	/**
	 *	Close the window and use the input values to generate a ctatgroupingcomponent
	 */
	this.confirm = function()
	{
		var groupName = nameField.value;
		if (groupName) 
		{
			if (validateName(groupName) || mode === 'edit')
			{
				if (ids.length > 0)
				{
					var badIds = [];
					//validate ids
					for (var i = 0; i < ids.length; i++)
					{
						if (!silexApp.model.file.getContentDocument().getElementById(ids[i]))
						{
							badIds.push(ids[i]);
						}
					}
					if (badIds.length > 0)
					{
						alert('The following components do not exist: '+badIds.join());
					}
					else //if we get here, group will be created
					{
						var componentListStr = ids.join();
						console.log('comp list string: '+componentListStr);
						if (oldName)
						{
							editGroup(groupName, componentListStr);
						}
						else
						{
							createGroup(groupName, componentListStr);	
						}
						pointer.close();
					}
				}
			}
			else
			{
				displayErrMsg('BAD_NAME');
			}
		}
		else
		{
			displayErrMsg('NO_NAME');
		}
	};
	
	
	this.cancel = function()
	{
		pointer.close();
	};

/////// ---- PRIVATE METHODS ---- ///////
	
	/**
	 *	Reset input values for new question
	 */
	function clear()
	{
		nameField.value = '';
		idField.value = '';
		while (compList.firstChild)
		{
			compList.removeChild(compList.firstChild);
		}
		ids = [];
	};
	
	/**
	 *	Show an error message if user tries to confirm w/ fields missing
	 *	@param errCode a string denoting which field is missing
	 */
	function displayErrMsg(errCode)
	{
		switch(errCode)
		{
			case 'ID_IN_GROUP':
				idField.value = 'A component with that ID is already in the group';
				idField.setAttribute('error', 'true');
			break;
			case 'NO_NAME':
				nameField.value = 'Enter an ID for the group';
				nameField.setAttribute('error', 'true');
			break;
			case 'BAD_NAME':
				nameField.value = 'IDs must be unique, and cannot contain spaces';
				nameField.setAttribute('error', 'true');
			break;
		}
	};
	
	/**
	 *	Unset the 'error' attribute on one or all of the dialog's input fields
	 *	(When the 'error' attribute is set fields turn red)
	 *	@param errCode which input should be set, or "ALL" to reset all of them
	 */
	function resetErrors(errCode)
	{
		console.log('resetErrs');
		switch(errCode)
		{
			case 'ID_IN_GROUP':
				idField.removeAttribute('error');
			break;
			case 'NO_NAME':
			case 'BAD_NAME':
				nameField.removeAttribute('error');
		}
	};
	
	function validateId(id)
	{
		//check if already in group
		return !(ids.includes(id));
	};
	
	/**
	 *	Checks provided name against working document to make
	 *		sure it's unique
	 *	@param name the name
	 */
	function validateName(name)
	{
		if (name.includes(' '))
			return false;
		var stage = silexApp.model.file.getContentDocument();
		var el = stage.getElementById(name);
		if (el)
			return false;
		
		return true;
	};
	
	function addToList(id)
	{
		var li = document.createElement('li');
		li.setAttribute('id', 'group-member-'+id);
		var span = document.createElement('span');
		var removeBtn = document.createElement('button');
		li.appendChild(span);
		span.appendChild(document.createTextNode(id));
		removeBtn.appendChild(document.createTextNode('-'));
		span.appendChild(removeBtn);
		
		removeBtn.addEventListener('click', function(e)
			{
				compList.removeChild(li);
				ids.splice(ids.indexOf(id), 1);
			});
			
		compList.appendChild(li);
		ids.push(id);
	};
	
	function editGroup(newName, componentList)
	{
		var stageDoc = silexApp.model.file.getContentDocument();
		var oldGroup = stageDoc.getElementById(oldName);
		oldGroup.setAttribute('data-ctat-componentlist', componentList);
		if (oldName !== newName)
		{
			//rename groupingcomponent
			oldGroup.setAttribute('id', newName);
			//rename menu option
			silexApp.view.menu.setGroupName(oldName, newName);
		}
	}
	
	function createGroup(groupName, componentList)
	{
		//create ctatgroupingcomponent
		window.silexApp.model.element.createElement('group', 
		{'id': groupName,
		 'componentList': componentList}
		);
		//add group menu option
		silexApp.view.menu.addGroup(groupName);
	}
	
	function deleteGroup()
	{
		if (oldName)
		{
			var stageDoc = silexApp.model.file.getContentDocument();
			var oldGroup = stageDoc.getElementById(oldName);
			oldGroup.parentElement.removeChild(oldGroup);
			silexApp.view.menu.removeGroup(oldName);
			pointer.close()
		}
		else
		{
			console.warn('no group selected');
		}
	}
	
	/**
	 * Set up listeners on all the input elements / buttons
	 */
	function initEvents()
	{
		//close icon
		$(windowId+' > .windowclose').on('click', pointer.close);
		//confirm
		confirmBtn.addEventListener('click', pointer.confirm);
		//delete group
		deleteBtn.addEventListener('click', deleteGroup);
		//cancel
		cancelBtn.addEventListener('click', pointer.close);
		//add component
		addBtn.addEventListener('click', function()
			{
				var toAdd = idField.value;
				if (toAdd && validateId(toAdd))
				{
					addToList(toAdd);
					idField.value = '';
				}
				else if (toAdd)
				{
					displayErrMsg('ID_IN_GROUP');
				}
			});
		
		//idField 
		idField.addEventListener('input', function()
			{
				resetErrors('ID_IN_GROUP');
			});
		
		//nameField
		nameField.addEventListener('input', function()
			{
				resetErrors('NO_NAME');
			});
	};
	
	initEvents();
};

