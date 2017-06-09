/**
*
*/
var CTATFileControls = function() 
{
	CTATBase.call (this, "CTATFileControls", "fcontrols");
	
	var pointer=this;
	var checkRoot=false;
	var driveObject = null;
	var initialized = false;
	
	/**
	*	Populate the jstree and jqxGrid panes in the dialog window
	*	based on file listing retrieved from drive
	*	@param divID the id of the DOM element the fchooser dialog lives in
	*	@param data an object containing the file data with which to populate the dialog
	*/
	this.displayFileTree=function displayFileTree (divID, data)
	{
		pointer.ctatdebug ("displayFileTree ("+divID+")");
		
		//init jstree object
		driveObject=FileUtils.prepTreeObject ();
		if ((divID==undefined) || (divID==null))
		{
			console.warn("divID is undefined or null");
			divID = "gdrive"; //default to googledrive window
		}
		var fileElement=null; //will store file data for jstree		
		var vizlist=[];		//will store file data for jqxGrid
		for (entry in data) 
		{
			if (data.hasOwnProperty(entry)) 
			{
				var fileObject=data [entry];						
				if (fileObject)
				{
					if (!pointer.checkExclusion(fileObject.title))
					{
						if (fileObject.mimeType.includes("folder"))
						{
							//folders go in jstree
							var fileElement = this.buildJstreeObj(fileObject);
							driveObject.core.data [0].children.push (fileElement);	
						}
						else
						{
							//files go in jqxGrid
							var fileArray= this.buildJqxgridObj(fileObject);							
							vizlist.push(fileArray);
						}
					}
				}					
				//else console.log ("Error obtaining file object!");
			}
		}
		
		ctatdebug ("We've got all the items, displaying ...");

		$('#'+divID).jstree (driveObject);			
		this.fillJqxGrid('#'+divID+"detailstt", vizlist);
		
		initialized = true;
	};
	
	/**
	 *	Updates contents of one particular folder in the fchooser dialog
	 *	(called when a folder is clicked to load that folder's contents)
	 *	@param divId the id of the DOM element where the fchooser dialog lives
	 *	@param dirId the id of the folder that has been selected
	 *	@param newData the contents of the folder
	 */
	this.updateFileTree = function(divId, dirId, newData)
	{
		console.log('updateFileTree( )');
		if (!initialized)
		{
			console.log('not init\'d yet, calling display instead');
			pointer.displayFileTree(divId, newData);
			return;
		}
		if (dirId == cloudUtils.getRootFolder())
			dirId = 'root';
		
		console.log('updating dir w/ id = '+dirId);
		
		var vizlist = []; //will store data for jqxGrid
		if (dirId)
		{
			//check for deleted folders
			var nodeJSON = $('#'+divId).jstree('get_json', dirId);
			var node = eval(nodeJSON);
			for (var i = 0; i < node.children.length; i++)
			{
				if (node.children[i] && !newData[node.children[i].id])
				{
					ctatdebug('deleting node: '+node.children[i].id);
					$('#'+divId).jstree('delete_node', node.children[i]);
				}
			}
			
			//update jstree data
			for (entry in newData)
			{
				if (newData.hasOwnProperty(entry))
				{
					var file = newData[entry];
					var newNode;
					if (!pointer.checkExclusion(file.title))
					{
						if (file.mimeType.includes("folder"))
						{
							//folders go in jstree
							//need to check if already there first
							if (!($('#'+divId).jstree('get_node', file.id)))
							{
								ctatdebug('adding node: '+file.id);
								newNode = this.buildJstreeObj(file);
								$('#'+divId).jstree('create_node', dirId, newNode, 'last');
							}
						}
						else
						{
							//files go in jqxGrid
							newNode = this.buildJqxgridObj(file);
							vizlist.push(newNode);
						}
					}
				}
			}
			//set updated dir to open
			$('#'+divId).jstree('open_node', dirId);
			//redraw jqxGrid
			this.fillJqxGrid('#'+divId+"detailstt", vizlist);
		}
		else
		{
			console.log("ERROR, couldn't find jstree node for dir "+dirId);
		}
	};
	
	/** ---Internal---
	 *	Populates jqxGrid pane with file data
	 *	@param listid the id of the DOM node where the jqxGrid lives
	 *	@param srcData an object representing the contents of the grid
	 */
	this.fillJqxGrid = function(listid, srcData)
	{
		var source =
		{
			localdata: srcData,
			datafields: [
				{ name: 'icon', type: 'string', map: '0'},
				{ name: 'name', type: 'string', map: '1' },
				{ name: 'created', type: 'string', map: '2' },
				{ name: 'modified', type: 'string', map: '3' },
				{ name: 'size', type: 'string', map: '4' }
			],
			datatype: "array"
		};

		var dataAdapter = new $.jqx.dataAdapter(source);
   
		$(listid).jqxGrid(
		{             
			width: '100%',
			height: '100%',
			source: dataAdapter,
			columnsresize: true,
			sortable: true,
			columns: 
			[
				{ text: ' ', datafield: 'icon', width: 24 },
				{ text: 'File Name', datafield: 'name', width: 150 },
				{ text: 'Created', datafield: 'created', width: 100 },
				{ text: 'Modified', datafield: 'modified', width: 100 },
				{ text: 'Size', datafield: 'size', width: 100 }
			]
		});
	};
	
	/** ---Internal---
	 *	Given a drive file object, builds a corresponding jstree node
	 *	@param fileObject the drive file object
	 */
	this.buildJstreeObj = function(fileObject)
	{	
		return {
			text: fileObject.title,
			id: fileObject.id,
			children: [],
			type: 'folder',
			valid_children: 'folder'
		};
	};
	
	/** ---Internal---
	 *	Given a drive file object, builds a corresponding jqxgrid node
	 *	@param fileObject the drive file object
	 */
	this.buildJqxgridObj = function(fileObject)
	{
		var jsDateCreated='--';
		if (fileObject.createdTime)
		{
			jsDateCreated=FileUtils.parseDate(fileObject.createdTime, cloudUtils.getMode());
		}
		
		var jsDateModified='--';
		if (fileObject.modifiedTime)
		{
			jsDateModified=FileUtils.parseDate(fileObject.modifiedTime, cloudUtils.getMode());
		}
		
		var fileArray =	[
			('<img src="'+pointer.getExtensionImage (fileObject.title)+'" />'),							
			fileObject.title,
			jsDateCreated.toString (),
			jsDateModified.toString (),
			(fileObject.fileSize/1000)+"K"
		];
		
		return fileArray;
	};
	
	/** ---Internal---
	*	Given a filename, return the proper icon to display in jstree
	*	@param aFilename the name of the file
	*/
	this.getExtensionImage=function getExtensionImage (aFilename)
	{
		//pointer.ctatdebug ("getExtensionImage ()");
		var ext = FileUtils.getExtension(aFilename);
		var src;
		switch(ext)
		{
			case 'ed.html':
				src = "css/jstree/ed-html.png";
			break;
			case 'html':
				src = "css/jstree/html.png";
			break;
			case 'css':
				src = "css/jstree/css.png"
			break;
			case 'brd':
				src = "css/jstree/brd.gif";
			break;
			case 'jpg':
			case 'png':
			case 'gif':
				src = "css/jstree/image.png";
			break;
			case 'mp3':
				src = "css/jstree/audio.png";
			break;
			default:
				src = 'css/jstree/file.png';
		}
		
		return src;
	};
	
	/** ---Internal---
	*	Determine whether or not a file should be displayed
	*	@param aFilename the name of the file
	*	@returns true if the file should NOT be displayed
	*/
	this.checkExclusion=function checkExclusion (aFilename)
	{
		if (aFilename==null)
		{
			return (true);
		}
		
		if (aFilename==".settings")
		{
			return (true);
		}
		
		return (false);
	};
};

CTATFileControls.prototype = Object.create(CTATBase.prototype);
CTATFileControls.prototype.constructor = CTATFileControls;
