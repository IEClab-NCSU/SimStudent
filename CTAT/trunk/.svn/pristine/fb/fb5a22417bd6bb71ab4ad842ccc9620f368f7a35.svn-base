/**
* @fileoverview defines the initial variable passed to initTutor, as well
*	as an onload function that sets up mutation observers to track changes
*	to the stage iframe's DOM
**/

var myVars = 
{
	tutoring_service_communication : "javascript",
	previewMode : true
};	

function ctatOnload ()
{		
	console.log("creation template onloadio ( )");
	MutationObserver = window.MutationObserver || window.WebkitMutationObserver;
	var domObserver = new MutationObserver(function(mutations, observer)
	{
		var numMutations = mutations.length;
		var init = false;
		var newNodes;
		for (var i = 0; i < numMutations; i++)
		{
			newNodes = mutations[i].addedNodes;
			var numNodes = newNodes.length
			
			for (var j = 0; j < numNodes; j++)
			{
				var addedNode = newNodes[j];
				if (addedNode.nodeType === Node.ELEMENT_NODE)
				{
					if (addedNode.parentNode)
					{
						var parentClass = addedNode.parentNode.getAttribute('class') || '';
						if (!parentClass.includes('ctat-gen-component') 
						&&  !parentClass.includes('CTATDragNDrop')
						&&	!parentClass.includes('CTATJumble')
						&&	!parentClass.includes('CTATTextField')
						&&	!parentClass.includes('CTATGroupingComponent'))
						{
							var nodeClass = addedNode.getAttribute('class') || '';
							if (parentClass.includes('CTAT'))
							{
								if (!nodeClass.includes('ui-resizable-handle')
								&&	!nodeClass.includes('ctat-gen-component'))
								{
									addedNode.setAttribute('class', nodeClass+' ctat-gen-component');
								}	
							}
						}
					}
				}
			}
		}
	});
	domObserver.observe(document.body,{
		subtree: true,
		childList: true,
		attributes: false
	});
	
	initTutor(myVars);
};
