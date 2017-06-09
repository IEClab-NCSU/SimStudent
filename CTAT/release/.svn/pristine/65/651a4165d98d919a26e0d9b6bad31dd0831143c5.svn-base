module.exports = function(grunt) 
{
	// Project configuration.
	grunt.initConfig(
	{
		pkg: grunt.file.readJSON('package.json'),
		concat: 
		{
			options: 
			{
				separator: ';',
			},
			dist: 
			{			
				src: [
					  'src/windowmanager/dialog.js',
					  'src/windowmanager/fileutils.js',
					  'src/system/link-list.js',
					  'src/system/requestqueue.js',
					  'src/system/drive.js',
					  'src/system/box.js',
					  'src/system/right-click-menu.js',
					  'src/system/file-dialog-right-click-menu.js',
					  'src/settings.js',
					  'src/windowmanager/desktop.js',
					  'src/script.js',
					  'src/right-click.js',
					  'src/player.js',
					  'src/envwizard.js',
					  'src/graph.js',
					  'src/windowmanager/window.js',
					  'src/windowmanager/fnewfolder.js',
					  'src/windowmanager/fchooser.js',
					  'src/windowmanager/fcontrols.js',
					  'src/windowmanager/visibility.js',					  
					  'src/windowmanager/texteditor.js',
					  'src/windowmanager/csseditor.js',
					  'src/windowmanager/multchoice.js',
					  'src/windowmanager/imagesource.js',
					  'src/windowmanager/createpkg.js',
					  'src/windowmanager/groupdialog.js',
					  'src/system/account.js',
					  'src/system/cloudutils.js',
					  'src/system.js',
					  'src/system/dropbox.js',
					  'src/windowmanager/windowmanager.js',
					  'src/windowmanager/flexdraggable.js',
					  'src/system/settingswindow.js',
					  'src/image.js',
					  'src/editor.js',
					  'src/main.js'
				     ],
				dest: 'build/tutordesk.js'
			},
		},		
		uglify: 
		{
			tutordesk:
			{
				options: 
				{
					banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n',
					sourceMap: true,
					sourceMapName: 'build/tutordesk.min.map',					
					mangle: false
				},
				files: 
				{
					'build/tutordesk.min.js' :
					[
						'src/windowmanager/dialog.js',
						'src/windowmanager/fileutils.js',
					  	'src/system/link-list.js',
						'src/system/requestqueue.js',
						'src/system/drive.js',
						'src/system/box.js',
						'src/system/right-click-menu.js',
						'src/system/file-dialog-right-click-menu.js',
						'src/settings.js',
						'src/windowmanager/desktop.js',
						'src/script.js',
						'src/right-click.js',
						'src/player.js',
						'src/envwizard.js',
						'src/graph.js',
						'src/windowmanager/window.js',
						'src/windowmanager/fnewfolder.js',					  
						'src/windowmanager/fchooser.js',
						'src/windowmanager/fcontrols.js',
						'src/windowmanager/visibility.js',
						'src/windowmanager/texteditor.js',
					  	'src/windowmanager/csseditor.js',
						'src/windowmanager/multchoice.js',
						'src/windowmanager/groupdialog.js',
						'src/system/account.js',
						'src/system/cloudutils.js',
						'src/system.js',
						'src/system/dropbox.js',
						'src/windowmanager/windowmanager.js',
						'src/windowmanager/flexdraggable.js',					  
						'src/system/settingswindow.js',				  
						'src/image.js',
						'src/editor.js',
						'src/main.js'				  
					],
				}
			},	
			wmanager:
			{
				options: 
				{
					banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n',
					sourceMap: true,
					sourceMapName: 'build/wmanager.min.map',					
					mangle: false					
				},
				files: 
				{
					'build/wmanager.min.js' :
					[
					  'src/windowmanager/dialog.js',
					  'src/windowmanager/fileutils.js',
					  'src/windowmanager/window.js',
					  'src/windowmanager/fnewfolder.js',
					  'src/windowmanager/fcontrols.js',
					  'src/windowmanager/visibility.js',
					  'src/windowmanager/texteditor.js',
					  'src/windowmanager/multchoice.js',
					  'src/windowmanager/groupdialog.js',
					  'src/windowmanager/windowmanager.js',
					  'src/windowmanager/flexdraggable.js',
					],
				}
			}
		}	
	});

	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-contrib-uglify');

	// A very basic default task.
	grunt.registerTask('check', 'Log some stuff.', function() 
	{
		grunt.log.write('Logging some stuff...').ok();
	});
	
	grunt.registerTask('default', ['concat','uglify:tutordesk','uglify:wmanager','check']);
};
