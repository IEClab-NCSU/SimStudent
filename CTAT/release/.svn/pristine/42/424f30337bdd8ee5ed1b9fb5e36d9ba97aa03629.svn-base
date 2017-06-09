/**
 * @fileoverview A class that represents the UI part of the cloud file system code
 *
 * @author $Author: $
 * @version $Revision: $
 */
 
//goog.require('CTATBase');

/**
*
*/
var CTATTutorPlayer = function() 
{		
	CTATDialogBase.call (this, "#tutorplayer", "CTATTutorPlayer", "fileui","MODAL");
	
	var pointer=this;
	
	/**
	*
	*/
	this.showOpenDialog = function showOpenDialog ()
	{
		pointer.ctatdebug ("showOpenDialog ()");
				
		pointer.show ();	
	};
	
	function initClose()
	{
		$('#tutorplayer > .ctatwindowclose').on('click', pointer.close);
	}
	
	var super_close = this.close;
	this.close = function()
	{
		$('#tutorplayer > .ctatcontent').html('');
		super_close();
	};
	
	initClose();
};

CTATTutorPlayer.prototype = Object.create(CTATDialogBase.prototype);
CTATTutorPlayer.prototype.constructor = CTATTutorPlayer;
