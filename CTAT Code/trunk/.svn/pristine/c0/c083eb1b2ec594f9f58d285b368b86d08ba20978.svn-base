/**-----------------------------------------------------------------------------
 $Author$ 
 $Date$ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 
 -
 License:
 -
 ChangeLog:
 -
 Notes:

 view-source:http://jgraph.github.io/mxgraph/javascript/examples/helloworld.html
*/

var rootFolder="root";

// Model variables
var graphContainer=null;
var graphBRD=null;
var graphView=null;
var graphModel=null;
var graphParent=null;
var graphLayout=null;
var graph=null;
var rubberband = null;
var graphShown=false;

// width/height of nodes
var w = 80;
var h = 30;

// Interaction settings

var graphSnap=false;
var graphGrid=true;

var graphNodes=null;
var graphEdges=null;

var isGraphDemo=false;

/**
*
*/
function setContainer (container)
{
	graphContainer=container;
}

/**
*
*/
function view_br ()
{
	console.log ("view_br ()");
	
	if (!mxClient.isBrowserSupported())
	{
		// Displays an error message if the browser is not supported.
		mxUtils.error('Browser is not supported!', 200, false);
		return;
	}
	//init graph
	setContainer(document.getElementById('graphContainer'));
	generateGraph();
	
	if (isGraphDemo==true)
	{
		// Disables the built-in context menu
		mxEvent.disableContextMenu(document.getElementById('graphContainer'));
		// Adds cells to the model in a single step
		graph.getModel().beginUpdate();
		try
		{
			var v1 = graph.insertVertex(graphParent, null, 'Hello,', 20, 20, w, h);
			var v2 = graph.insertVertex(graphParent, null, 'World!', 200, 150, w, h);
			var e1 = graph.insertEdge(graphParent, null, '', v1, v2);
		}
		finally
		{
			// Updates the display
			graph.getModel().endUpdate();
		}
	}
	else
	{
		//$('#graph_menu').menubutton('enable');
		//$('#tutormode').disabled = false;
		loadBRD('https://preview.pact.cs.cmu.edu/tutordesk/tests/testBRDs/balloons.brd');
	}
}

/**
*
*/
function hide_br ()
{
	console.log ("hide_br ()");
	
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
		console.log ("graphShown: " + graphShown);
	
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
	console.log ("parseBRD ()");

	parseToDOM (data);
	buildModel ();
}

/**
*
*/
function parseToDOM (data)
{
	console.log ("parseToDOM ()");
	//Arrays to store nodes and edges
	graphNodes=new Array ();
	graphEdges=new Array ();
	//Object to parse xml data
	var xmlParser=new CTATXML ();
	
	//parse xml data
	xmlDoc=xmlParser.parseXML (data);
	if (xmlDoc===null)
	{
		console.log ("Error parsing xml");
		return;
	}			
	
	console.log ("Root: " + xmlParser.getElementName (xmlDoc));
	
	var rootChildren=xmlParser.getElementChildren (xmlDoc);
	for (var t=0;t<rootChildren.length;t++)
	{
		var entry=rootChildren [t];
		
		//>---------------   Process Node   --------------------
		if (xmlParser.getElementName (entry)=="node")
		{
			//console.log ("Found graph node");
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
		//>---------------    Process Edge   ------------------
		if (xmlParser.getElementName (entry)=="edge")
		{
			console.log ("Found edge node");
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
* Creates the graph inside the given container
*/
function generateGraph ()
{
	console.log ("generateGraph ()");
	if (graph===null)
	{
		console.log ("Creating new graph object ...");
		graph = new mxGraph(graphContainer);
		configureGraph (graph);
		graphView=graph.getView ();
		graphModel=graph.getModel();
		graphModel.addListener(mxEvent.CHANGE,propagateGraphEdits);
	
		//var config = mxUtils.load('mxgraph/editors/config/keyhandler-commons.xml').getDocumentElement();
		//var editor = new mxEditor(config);
		
		// Disables basic selection and cell handling
		graph.setEnabled(true);
		graph.setGridEnabled(graphSnap);
		// Gets the default graphParent for inserting new cells. This
		// is normally the first child of the root (ie. layer 0).
		clearGraph ();
		rubberband = new mxRubberband(graph);
		graph.gridSize = 40;
		// Creates a layout algorithm to be used with the graph
		graphLayout = new mxFastOrganicLayout(graph);
		// Moves stuff wider apart than usual
		graphLayout.forceConstant = 80;			
	}
}

/**
*
*/
function buildModel ()
{
	console.log ("buildModel ()");

	if (graph===null)
	{
		console.log ("Error: graph is null, can't build model");
		return;
	}
	// Adds cells to the model in a single step
	graphModel.beginUpdate();
	try
	{
		console.log ("Building ...");
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
				console.log ("Error: either the from or to node is null for this edge");
			}
		}	
		//graphLayout.execute(graphParent);
	}
	catch (err)
	{
		console.log('Error generating visual graph model: " + err.message');
		setStatus ("Error generating visual graph model: " + err.message);
	}
	finally
	{
		console.log ("Updating display ...");
		graphModel.endUpdate();
	}
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
	style[mxConstants.STYLE_FILLCOLOR]= 'white';
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
function enableSnap (snap)
{
	graphSnap = snap;
	if (graphSnap)
	{
		graph.setGridEnabled(true);
	}
	else
	{
		graph.setGridEnabled(false);
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
	console.log ("clearGraph ()");
	
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

