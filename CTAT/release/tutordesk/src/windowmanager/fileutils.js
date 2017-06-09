var FileUtils = {
	
	extToTypeMap : {
		'txt' : 'text/plain',
		'html': 'text/html',
		'ed.html':'text/html',
		'css' : 'text/css',
		'js'  : 'text/javascript',
		'png' : 'image',
		'jpg' : 'image',
		'gif' : 'image'
	},

	typeToExtMap : {
		'text/html': '.html',
		'text/plain': '.txt',
		'text/css': '.css',
		'text/javascript': '.js'
	}
};

FileUtils.getExtension = function(filename)
{
	var len = filename.indexOf('.');
	return (len > -1) ? filename.substring(len+1, filename.length) : null;
}

FileUtils.hasExtension = function(filename)
{
	var ext = FileUtils.getExtension(filename);
	return (!!FileUtils.extToTypeMap[ext]);
}

FileUtils.extensionToMimeType = function(ext)
{
	return (FileUtils.extToTypeMap[ext] ? FileUtils.extToTypeMap[ext] : '');
};

FileUtils.mimeTypeToExtension = function(type)
{
	return (FileUtils.typeToExtMap[type] ? FileUtils.typeToExtMap[type] : '.txt');
}

FileUtils.parseDate = function (date, mode)
{
	var regex = FileUtils.dateFormats[mode];
	var m = regex.exec(date);
	var year   = +m[1];
	var month  = +m[2];
	var day    = +m[3];
	var hour   = +m[4];
	var minute = +m[5];
	var second = +m[6];
	if (mode == 'googledrive')
	{
		var msec   = +m[7];
		var tzHour = +m[8];
		var tzMin  = +m[9];
		var tzOffset = new Date().getTimezoneOffset() + tzHour * 60 + tzMin;

		return new Date(year, month - 1, day, hour, minute - tzOffset, second, msec);
	}
	else if (mode == 'dropbox')
	{
		return new Date(year, month - 1, day, hour, minute, second, 0);
	}
};
	
FileUtils.dateFormats = {	
	'dropbox': /(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})Z/,
	'googledrive': /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})\.(\d{3})([+-]\d{2}):(\d{2})$/
};

/**
* Initialize object used to populate jstree pane in fchooser window
*/
FileUtils.prepTreeObject = function prepTreeObject()
{		
	var treeObject =
	{
		'core' : 
		{
			'check_callback': true,
			'multiple' : false,				
			'data' : [
						{
							'id' : 'root',
							'type': 'folder',
							'text' : 'CTAT',
							'state' : { 
										'opened' : true,
										'selected' : true 
									},
							'children' : []
						}
					]
		},
		'types':
		{
			'folder': {
				'valid_children': ['file', 'folder']
			},
			'file': {
				'valid_children': []
			}
		}
	};
	
	return treeObject;
};

FileUtils.convertFileFormat = function(inFile, inFileFormat)
{
	var file = {};
	switch(inFileFormat)
	{
		case 'dropbox':
			file.id = inFile['path_lower']; 
			file.title = inFile['name'];
			file.mimeType = (inFile['.tag'] === 'folder') ? 'folder' : 
					FileUtils.extensionToMimeType(FileUtils.getExtension(inFile['name']));
			file.modifiedTime = inFile['client_modified'];
			file.fileSize = inFile['size'];
		break;
		case 'box':
			file.id = inFile['id'];
			file.title = inFile['name'];
			file.modifiedTime = inFile['modified_at'];
			file.createdTime = inFile['created_at'];
			file.mimeType = (inFile['type'] === 'folder') ? 'folder' : 
					FileUtils.extensionToMimeType(FileUtils.getExtension(inFile['name']));
			file.fileSize = inFile['size'];
	}
	return file;
};

FileUtils.assertName = function(name, parent, cbk)
{
	cloudUtils.getIdFromName(name, parent, function(id)
		{
			if (id)
			{
				var num = /(.*)\(([0-9]*)\)/.exec(name);
				if (num)
				{
					name = num[1]+'('+(parseInt(num[2], 10)+1)+')';
				}
				else
				{					
					name += '('+1+')';
				}
				FileUtils.assertName(name, parent, cbk);
			}
			else
			{
				cbk(name);
			}
		});
};