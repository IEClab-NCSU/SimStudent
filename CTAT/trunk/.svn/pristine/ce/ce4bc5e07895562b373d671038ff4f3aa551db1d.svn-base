/**
*	@fileoverview: A singleton object that manages tutor iframes.  Specifically geared towards iframes that
*		do not have a source document, but whose content is instead directly injected into the iframe element,
*		such as is used by the interface editor for 'demonstrate' mode. 
**/


goog.provide ("CTATIFrameManager");

CTATIFrameManager = function()
{
	//maps frameElement ids to objects storing data about the managed iframe
	var managedFrames = {};
	var pointer = this;
	
	/**
	*	Load data into a new iframe and add it to the managedFrames object
	*	@param {DOM Node} frame the iframe element
	*	@param {String} data the html string that will be loaded into frame
	*	@param {String} mode the value to set as CTATTarget in the frame context
	**/
	this.initFrame = function(frame, data, mode)
	{
		let id = frame ? frame.getAttribute('id') : null;
		if (id)
			managedFrames[id] = {'domNode': frame, 'initialDom': data, 'initialMode': mode};
		else
			console.error('iFrames managed by CTATIFrameManager must exist and have unique ids');
		
		frame.setAttribute('data-ctat-target', mode);
		this.writeData(frame, data);
	};
	
	/**
	*	Restore the initial state (i.e. the data param passed to initFrame) to an iframe
	*	@param {String} id the id of the iframe element to refresh
	**/
	this.refresh = function(id)
	{
		let frameObject = managedFrames[id];
		if (frameObject['domNode'])
		{
			frameObject['domNode'].setAttribute('src', 'about:blank');
			this.writeData(frameObject['domNode'], frameObject['initialDom']);
		}
		else
		{
			console.warn('CTATIFrameManager: iFrame id '+id+' not found');
		}
	}
	
	/**
	*	Write data into an iframe
	*	@param {DOM Node} frame the iframe element
	*	@param {String} data the html string to write into frame
	**/
	this.writeData = function(frame, data)
	{
		if (frame)
		{
			let doc = frame.contentDocument || frame.contentWindow.document;
			doc.open();
			doc.write(data);
			doc.close();
		}
	};
	
	/**
	*	managed iframes should dispatch a 'refreshIframe' event to their
	*	parent document to request a refresh.
	*	
	*	ex: refreshEvent = new CustomEvent('refreshIframe', {"detail": <myId>});
	**/
	document.addEventListener('refreshIframe', function(event)
		{
			let id = event.detail;
			console.log('refresh event for iframe '+id);
			pointer.refresh(id);
		});
};
