
var CTATCreatePackage = function(windowId)
{
	CTATDialogBase.call (this, windowId, "CTATCreatePackage", "createpackage", "MODAL", true);
	
	var pointer = this;
	var saveCbk = function(){console.log('default save callback')};
	var mode = null;
	var pkgDropdown = null;
	var confirm = null;
	var noPkg = {id: 'noPkg', title: '--------'};
	var empty = false;
	var lastSelected = null;
	
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
		var pkg = {
			name: pkgDropdown.options[pkgDropdown.selectedIndex].innerHTML,
			id: $(pkgDropdown).val()
		};
		var interfaceName = $(filenameInput).val().trim();
		if (!interfaceName)
		{
			filenameInput.classList.add('require-field');
		}
		else
		{
			var fullName = interfaceName.includes('.ed.html') ? interfaceName : interfaceName + '.ed.html';
			cloudUtils.getIdFromName('HTML', pkg.id, function(id)
			{
				FileUtils.assertName(fullName, id, function(nameToUse)
				{
					var goAhead = true;
					if (nameToUse !== fullName)
					{
						goAhead = window.confirm('An interface file already exists with the name you provided.  Overwrite it?');
					}
					if (goAhead)
					{
						window.ctatFileChooser.publishInterface(interfaceName, pkg, saveCbk);					
						lastSelected = pkg.id;
						pointer.close();
					}
				});
				
			});
		}
	};
	
	this.cancel = function()
	{
		pointer.close();
	};
	
	var super_show = this.show;
	this.show = function(thisMode, cbk, toSelect)
	{
		console.log('createPackage.show()');
		cbk && pointer.setSaveCallback(cbk);
		setLoading(true);
		//clear interface name field
		$(filenameInput).val('');
		filenameInput.classList.remove('require-field');
		super_show();
		loadList(() => {
			if (toSelect)
				this.setSelected('name', toSelect);
			else
				this.setSelected('id', lastSelected);
			
			setLoading(false);
		});
	};
	
	this.setSelected = function(nameOrId, val)
	{
		console.log('setSelected( '+nameOrId+', '+val+' )');
		if (val)
		{
			if (nameOrId === 'name')
			{
				for (let i = 0; i < pkgDropdown.options.length; i++)
				{
					if (pkgDropdown.options[i].innerHTML === val)
					{
						pkgDropdown.selectedIndex = i;
						break;
					}
				}
			}
			else if (nameOrId === 'id')
			{
				if (pkgDropdown.querySelector('option[value="'+val+'"]'))
					$(pkgDropdown).val(val);
			}
		}
	}
	
	///// private //////
	
	function loadList(cbk, force)
	{
		//clear dropdown contents
		pkgDropdown.innerHTML = '';
		var pkgs = [];
		force = force || false;
		
		//callback when all packages have been loaded
		var done = function()
		{
			if (pkgs.length === 0)
			{
				empty = true;
				confirm.setAttribute('disabled', "true");
				addPkgOption(noPkg);
			}
			else
			{
				setPkgOptions(pkgs);
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
					pkgs.push(createPkgOption(folder));
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
	
	function createPkgOption(fileObj)
	{
		var option = document.createElement('option');
		option.setAttribute('value', fileObj.id);
		option.innerHTML = fileObj.title;
		return option;
	}
	
	function addPkgOption(file)
	{
		var idx = 0;
		var option = createPkgOption(file)
		var before = pkgDropdown.firstChild;
		while (before && before.innerHTML < option.innerHTML)
		{
			before = before.nextSibling;
			idx++;
		}
		if (before)
			pkgDropdown.insertBefore(option, before)
		else
			pkgDropdown.appendChild(option);
		
		return idx;
	}
	
	function setPkgOptions(options)
	{
		options.sort(function(a, b)
		{
			if (a.innerHTML > b.innerHTML) return 1;
			if (a.innerHTML < b.innerHTML) return -1;
			return 0;
		});
		
		$(pkgDropdown).empty().append( options );
	}
	
	function setLoading(isLoading)
	{
		if (isLoading)
			$('#loading-scrn').css('display', 'initial');
		else
			$('#loading-scrn').css('display', 'none');
	}
};
	