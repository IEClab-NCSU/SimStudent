/**
* Program starts here. Creates a sample graph in the
* DOM node with the specified ID. This function is invoked
* from the onLoad event handler of the document (see below).
*/

// Model variables
var graphContainer=null;
var graphBRD=null;
var graphView=null;
var graphModel=null;
var graphParent=null;
var graphLayout=null;
var graph=null;
var graphShown=false;

// Visual variables
var w = 30;
var h = 30;

// Interaction settings

var graphSnap=false;
var graphGrid=true;

var graphNodes=null;
var graphEdges=null;

/**
*
*/
function view_br ()
{
	debug ("view_br ()");
	
	view_br_panel.show ();
	view_graph(document.getElementById('graphContainer'));
	
	$('#graph_menu').menubutton('enable');
	$('#tutormode').disabled = false;
	
	drive.getFile("1416.brd",rootFolder,parseBRD);	
}

/**
*
*/
function hide_br ()
{
	debug ("hide_br ()");

	clearGraph ();
	
	$('#graph_menu').menubutton('disable');
	$('#tutormode').disabled = true;
}

function createBR (Y)
{
	view_br_panel = new Y.Panel(
	{
		srcNode: '#view_br',
		headerContent: 'Behavior Recorder',
		width: 650,
		height: 550,
		centered: true,
		modal: false,
		visible: false,
		zIndex : 1020,
		render: true
	});

	var view_br_resize = new Y.Resize(
	{
		node: '#view_br',
		preserveRatio: true,
		wrap: true,
		handles: 'br'
	});	
	
	view_br_panel.plug(Y.Plugin.Drag, 
	{
		handles: ['.yui3-widget-hd']
	});	
	
	view_br_panel.on('visibleChange',function(o) 
	{
		debug ("graphShown: " + graphShown);
	
		if (graphShown===true)
		{
			hide_br ();
			graphShown=false;
		}
		else
		{
			graphShown=true;
		}
	});
}

/**
* For example: http://augustus.pslc.cs.cmu.edu/html5/HTML5TestFiles/BasicTests/FinalBRDs/1416.brd
*/
function loadBRD (aURL)
{
	$.get((aURL), function (data)
	{		
		parseBRD (data);
	});	
}

/**
*
*/
function parseBRD (data)
{
	debug ("parseBRD ()");

	parseToDOM (data);

	generateGraph ();
	
	buildModel ();
}

/**
*
*/
function parseToDOM (data)
{
	debug ("parseToDOM ()");
	
	graphNodes=new Array ();
	graphEdges=new Array ();
	
	var xmlParser=new CTATXML ();
	
	xmlDoc=xmlParser.parseXML (data);

	if (xmlDoc===null)
	{
		debug ("Error parsing xml");
		return;
	}			
	
	debug ("Root: " + xmlParser.getElementName (xmlDoc));
	
	var rootChildren=xmlParser.getElementChildren (xmlDoc);
	
	for (var t=0;t<rootChildren.length;t++)
	{
		var entry=rootChildren [t];
		
		//>---------------------------------------------------------------
		
		if (xmlParser.getElementName (entry)=="node")
		{
			//debug ("Found graph node");
			
			var newNode=new CTATExampleTracerNodeVisualData ();
			graphNodes.push (newNode);
			
			var nodeChildren=xmlParser.getElementChildren (entry);
			
			for (var i=0;i<nodeChildren.length;i++)
			{
				var nodeElement=nodeChildren [i];
								
				if (xmlParser.getElementName (nodeElement)=="text")
				{
					newNode.setLabel (xmlParser.getNodeTextValue (nodeElement));
				}					
				
				if (xmlParser.getElementName (nodeElement)=="uniqueID")
				{
					newNode.setID (xmlParser.getNodeTextValue (nodeElement));
				}									
				
				if (xmlParser.getElementName (nodeElement)=="dimension")
				{
					var dimensionChildren=xmlParser.getElementChildren (nodeElement);
								
					for (var j=0;j<dimensionChildren.length;j++)
					{
						var coordinate=dimensionChildren [j];
								
						if (xmlParser.getElementName (coordinate)=="x")
						{
							var xValue=xmlParser.getNodeTextValue (coordinate);
														
							newNode.setX (xValue);
						}
						
						if (xmlParser.getElementName (coordinate)=="y")
						{
							var yValue=xmlParser.getNodeTextValue (coordinate);

							newNode.setY (yValue);
						}						
					}	
				}
			}	
		}
		
		//>---------------------------------------------------------------
		
		if (xmlParser.getElementName (entry)=="edge")
		{
			debug ("Found edge node");
			
			var newEdge=new CTATExampleTracerLinkVisualData ();									
			graphEdges.push (newEdge);
			
			var edgeChildren=xmlParser.getElementChildren (entry);
			
			for (var w=0;w<edgeChildren.length;w++)
			{
				var edgeElement=edgeChildren [w];
								
				if (xmlParser.getElementName (edgeElement)=="sourceID")
				{
					newEdge.setSource (xmlParser.getNodeTextValue (edgeElement));
				}
				
				if (xmlParser.getElementName (edgeElement)=="destID")
				{
					newEdge.setDestination (xmlParser.getNodeTextValue (edgeElement));
				}				
			}	
		}
		
		//>---------------------------------------------------------------		
	}	
}

/**
*
*/
function view_graph (container)
{
	graphContainer=container;

	if (!mxClient.isBrowserSupported())
	{
		mxUtils.error('Browser is not supported!', 200, false);
	}
	else
	{
		generateGraph ();
	}
}

/**
* Creates the graph inside the given container
*/
function generateGraph ()
{
	debug ("generateGraph ()");

	if (graph===null)
	{
		debug ("Creating new graph object ...");
	
		graph = new mxGraph(graphContainer);
				
		configureGraph (graph);
		
		graphView=graph.getView ();

		graphModel=graph.getModel();
					
		graphModel.addListener(mxEvent.CHANGE,propagateGraphEdits);
	
		//var config = mxUtils.load('mxgraph/editors/config/keyhandler-commons.xml').getDocumentElement();
		//var editor = new mxEditor(config);
		
		// Disables basic selection and cell handling
		graph.setEnabled(true);
					
		new mxRubberband(graph);
		
		graph.gridSize = 40;
		
		graph.setGridEnabled(graphSnap);
		
		// Gets the default graphParent for inserting new cells. This
		// is normally the first child of the root (ie. layer 0).
		clearGraph ();
		
		new mxRubberband(graph);
	
		graph.gridSize = 40;
		
		// Creates a layout algorithm to be used
		// with the graph
		graphLayout = new mxFastOrganicLayout(graph);

		// Moves stuff wider apart than usual
		graphLayout.forceConstant = 80;			
	}
		
	//buildModel ();
}

/**
*
*/
function configureGraph (graph)
{
	// Changes the default vertex style in-place
	var style = graph.getStylesheet().getDefaultVertexStyle();
	style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
	style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter ;
	//style[mxConstants.STYLE_GRADIENTCOLOR] = 'white';
	style [mxConstants.STYLE_FILLCOLOR]= 'white';
	style[mxConstants.STYLE_AUTOSIZE] = "true";
	style[mxConstants.STYLE_FONTSIZE] = '10';					
}
/**
*
*/
function propagateGraphEdits (sender, evt)
{
	setStatus ("propagateGraphEdits ()");
	
	var changes = evt.getProperty('edit').changes;
	var nodes = [];
	var codec = new mxCodec();

	for (var i = 0; i < changes.length; i++)
	{
		nodes.push(codec.encode(changes[i]));
	}
	
	// do something with the nodes	
}

/**
*
*/
function enableSnap ()
{
	if (graphSnap===true)
	{
		graph.setGridEnabled(false);
		graphSnap=false;
	}
	else
	{
		graph.setGridEnabled(true);
		graphSnap=true;
	}
}

/**
*
*/
function showGrid ()
{
	if (graphGrid===true)
	{
		graphGrid=false;
	}
	else
	{
		graphGrid=true;
	}
}

/**
*
*/
function clearGraph ()
{
	debug ("clearGraph ()");
	
	// Clear any existing cells if they exist
	if (graph!==null)
	{
		graph.getModel ().clear ();
		graphParent = graph.getDefaultParent();
	}	
}

/**
*
*/
function getNode (anID)
{
	for (var i=0;i<graphNodes.length;i++)
	{
		var aNode=graphNodes [i];
		
		if (aNode.getID ()==anID)
		{
			return (aNode);
		}
	}
	
	return (null);
}

/**
*
*/
function buildModel ()
{
	debug ("buildModel ()");

	if (graph===null)
	{
		debug ("Error: graph is null, can't build model");
		return;
	}
		
	// Adds cells to the model in a single step
	graphModel.beginUpdate();
	
	try
	{
		debug ("Building ...");
		
		for (var i=0;i<graphNodes.length;i++)
		{
			var aNode=graphNodes [i];
			
			var vizReference=graph.insertVertex(graphParent, null, aNode.getLabel (), aNode.getX (), aNode.getY (), w, h);
			aNode.setVizReference (vizReference);
		}
	
		for (var j=0;j<graphEdges.length;j++)
		{
			var anEdge=graphEdges [j];
			
			var fromNode=getNode (anEdge.getSource ());
			var toNode=getNode (anEdge.getDestination ());
			
			if ((fromNode!==null) && (toNode!==null))
			{
				graph.insertEdge(graphParent, null, 'ab', fromNode.getVizReference (), toNode.getVizReference ());
			}
			else
			{
				debug ("Error: either the from or to node is null for this edge");
			}
		}	
	
		//graphLayout.execute(graphParent);
	}
	catch (err)
	{
		setStatus ("Error generating visual graph model: " + err.message);
	}
	finally
	{
		debug ("Updating display ...");
		graphModel.endUpdate();
	}
}