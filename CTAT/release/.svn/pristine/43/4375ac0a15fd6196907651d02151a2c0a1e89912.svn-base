
var CTATCreatePackage = function(windowId)
{
	CTATDialogBase.call (this, windowId, "CTATCreatePackage", "createpackage", "MODAL", true);
	
	var pointer = this;
	var saveCbk = function(){console.log('default save callback')};
	var mode = null;
	var pkgDropdown = null;
	var confirm = null;
	var noPkg = {id: 'noPkg', title: '--------'};
	var listLength = 0;
	var empty = false;
	
	this.setSaveCallback = function(callback)
	{
		saveCbk = callback;
	};
	
	this.initEvents = function()
	{
		pkgDropdown = document.querySelector('#create-package-pkgname');
		confirm = document.querySelector('#create-package-confirm');
		filenameInput = document.querySelector('#create-package-interfacename');
		
		confirm.addEventListener('click', pointer.confirm);
		$('#create-package-cancel').on('click', pointer.cancel);
		$('#create-package-newpkg').on('click', pointer.createNew);
		$(windowId + '>'+'.windowclose').on('click', pointer.cancel);
		$('#create-package-refresh').on('click', loadList.bind(pointer, null, true));
	};
	
	this.createNew = function()
	{
		//show new folder dialog
		window.ctatFileChooser.fileDialogNewFolder(null, function(name)
		{
			//so can't confirm until package has been created
			confirm.setAttribute('disabled', 'true');
			window.ctatFileChooser.createPackage(name, function(pkgInfo)
			{
				if (empty)
				{
					pkgDropdown.removeChild(pkgDropdown.firstChild);
					listLength=0;
					empty=false;
				}
				//add to dropdown list
				var i = addPkgOption({'id': pkgInfo['pkgId'], 'title': name});
				console.log('package added at index '+i);
				pkgDropdown.selectedIndex = i;
				confirm.removeAttribute('disabled');
			});
			
		}, 'New Package', 'Creating a package creates a new set of empty folders with the recommended structure for a new CTAT tutor.'); 
	};
	
	this.confirm = function()
	{
		var pkgName = $(pkgDropdown).val();
		var interfaceName = $(filenameInput).val().trim();
		if (!interfaceName)
		{
			filenameInput.classList.add('require-field');
		}
		else
		{
			window.ctatFileChooser.publishInterface(interfaceName, pkgName, false);					
			pointer.close();
		}
	};
	
	this.cancel = function()
	{
		pointer.close();
	};
	
	var super_show = this.show;
	this.show = function(thisMode)
	{
		console.log('createPackage.show()');
		setLoading(true);
		//clear interface name field
		$(filenameInput).val('');
		filenameInput.classList.remove('require-field');
		super_show();
		loadList(function()
		{
			setLoading(false);
		});
	};
	
	function loadList(cbk, force)
	{
		//clear dropdown contents
		pkgDropdown.innerHTML = '';
		var numPkgs=0;
		listLength=0;
		force = force || false;
		
		//callback when all packages have been loaded
		var done = function()
		{
			if (numPkgs === 0)
			{
				empty = true;
				confirm.setAttribute('disabled', "true");
				addPkgOption(noPkg);
			}
			else
			{
				confirm.removeAttribute('disabled');
				empty = false;
			}
			
			if (cbk && typeof(cbk) === 'function')
				cbk();
		};
		
		//get folder listing and populate dropdown
		cloudUtils.listFolders(cloudUtils.getRootFolder(), function(folders)
		{
			var keys = Object.keys(folders);
			var count = keys.length;
			var key;
			var handleValidation = function(folder, valid)
			{
				if (valid)
				{
					addPkgOption(folder);
					numPkgs++;
				}
				count--;
				if (count === 0)
				{
					done();
				}
			};
			
			if (keys.length > 0)
			{
				for (var i = 0; i < keys.length; i++)
				{
					key = keys[i]
					//only add valid CTAT packages
					window.ctatFileChooser.validatePkg(folders[key].id, function(f, v)
					{
						handleValidation(f, v)
					}.bind(pointer, folders[key]));
				}
			}
			else
			{
				done();
			}
		}, force);
	}
	
	function addPkgOption(fileObj)
	{
		var option = document.createElement('option');
		option.setAttribute('value', fileObj.id);
		option.innerHTML = fileObj.title;
		pkgDropdown.appendChild(option);
		var idx = listLength;
		listLength++;
		return idx;
	}
	
	function setLoading(isLoading)
	{
		if (isLoading)
			$('#loading-scrn').css('display', 'initial');
		else
			$('#loading-scrn').css('display', 'none');
	}
};
	