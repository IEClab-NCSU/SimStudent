/*
 * Created on Mar 23, 2006
 *
 */
package edu.cmu.pact.BehaviorRecorder.jgraphwindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.ToolTipManager;

import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphLayoutCache;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTabManager;
import edu.cmu.pact.Utilities.trace;

/**
 * @author mpschnei
 * 
 * Created on: Mar 23, 2006
 * Modified on: Jun 13, 2012
 */
public class JGraphPanel extends JComponent {
    
    private static final long serialVersionUID = 1L;
    
    // These constants are used to control tooltip display timings. Measured in millseconds
    private static final int INITIAL_TOOLTIP_DELAY = 750;
    private static final int DISMISS_TOOLTIP_DELAY = 50000;
    private static final int RESHOW_TOOLTIP_DELAY = 1;
    
    /** Window's title, name. */
    private static final String WINDOW_NAME_PREFIX = "Graph ";
    
    protected JGraphController jgraphController;
    
    private BR_JGraph jgraph;
    
    private DefaultGraphModel graphModel = new DefaultGraphModel();
    
    private GraphLayoutCache graphView =
    		new GraphLayoutCache(graphModel, new BR_CellViewFactory());
    
    private int windowID;
    
    //  private JLabel statusLabel;
    
    
    /**
     * @param brController
     */
    public JGraphPanel(final CTAT_Launcher server,
    		BR_Controller brController, final int tabNumber) {
		if (trace.getDebugCode("graphPanel")) trace.out("graphPanel", "JGraphPanel constructor, CTAT_Launcher is " + server);
    	
       jgraph = new BR_JGraph(graphModel, graphView, brController);
        this.windowID = tabNumber;
        graphModel.addGraphModelListener(jgraph);
        jgraph.setAntiAliased(true);
        jgraph.getSelectionModel().setChildrenSelectable(true);
    	final CTATTabManager tabManager = server.getTabManager();
        jgraph.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mousePressed(MouseEvent e) {
        		tabManager.updateIfNewTabFocus(tabNumber);
        	}
        });
        jgraphController = new JGraphController(brController, this, graphModel, graphView);
        new EditContextGraphSynchronize(brController, jgraph);
        new RefreshGraphOnGroupChange(brController, jgraph);
        createInterface();
        setToolTipDelay();
        repaint();
    }
    
    /**
     * This method is used to set the delay timings for tooltip display on the Swing framework shared tooltip manager.
     * The constants used are declared above and the meaning of each parameter can be found in the documentation for 
     * the {@link javax.swing.ToolTipManager}.
     */
    private void setToolTipDelay() {
        // Show tool tips immediately and for a long time
        ToolTipManager.sharedInstance().setInitialDelay(INITIAL_TOOLTIP_DELAY);
        ToolTipManager.sharedInstance().setDismissDelay(DISMISS_TOOLTIP_DELAY);
        ToolTipManager.sharedInstance().setReshowDelay(RESHOW_TOOLTIP_DELAY);
    }

    /**
     * 
     */
    private void createInterface() {
        setBackground(Color.WHITE);
        setBounds(100, 100, 500, 400);
        setName(WINDOW_NAME_PREFIX + String.valueOf(this.windowID));
        Box box = new Box(BoxLayout.PAGE_AXIS);
        box.setBackground(Color.WHITE);
        
        
	//        Container contentPane = getContentPane();
        
        setLayout(new BorderLayout());
        add(box, BorderLayout.CENTER);
	
        
        JComponent zoomPanel = createZoomBox();
        
	///       add(zoomPanel, BorderLayout.NORTH);
        
	JGraphScrollPane brPane = new JGraphScrollPane(jgraph);
	jgraph.setBackground(getBackground());
	jgraph.setForeground(getForeground());
	if (windowID == 1) {
		jgraph.setName("BR_JGraph");
	} else {
		jgraph.setName("BR_JGraph" + String.valueOf(windowID));
	}
	trace.out ("background = " + getBackground());
	trace.out ("foreground = " + getForeground());
       box.add(brPane);

	//       statusLabel = new JLabel();
	//       statusLabel.setName("Status Label");
	//       statusLabel.setBorder(new EmptyBorder(3, 3, 3, 3));
	//       add(statusLabel, BorderLayout.SOUTH);
    }

    public void repaint() {
	//      updateStatusLabel();
        super.repaint();
    }
    
    /**
     * 
     */
 //   private void updateStatusLabel() {
 //       final Object[] vertices = BR_JGraphNode.getVertices(graphModel);
 //       Set edges = DefaultGraphModel.getEdges(graphModel, vertices);
 //       int edgeCount = edges.size();
 //       int nodeCount = vertices.length;
 //       String text = "Node count: " + nodeCount + "  Edge count: " + edgeCount + "  Scale factor = " + jgraph.getScale();
 //       if (statusLabel != null)
 //       	statusLabel.setText(text);
 //   }
    
    
    /**
     * @return
     */
    private JComponent createZoomBox() {
    	String[] zoomStrings = {"200%", "150%", "125%", "100%", "75%", "50%", "25%"};
        JComponent box2 = new Box (BoxLayout.LINE_AXIS);
    //    JButton zoomIn = new JButton ("+");
    //    JButton zoomOut = new JButton ("-");
        JComboBox zoomList = new JComboBox(zoomStrings);
        zoomList.setName("Scale Selection List");
        zoomList.setSelectedItem("100%");
    //    zoomList.setMaximumSize((new Dimension(25, 200)));

   //     box2.add(zoomIn);
   //     box2.add(zoomOut);
        box2.add(zoomList);
 
        zoomList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               	JComboBox zb = (JComboBox) e.getSource();
            	String zoomName = (String) zb.getSelectedItem();
		//  	trace.out("Selected Zoom factor = " + zoomName);
                jgraphController.zoomListActionPerfomed(zoomName);
                
            }
        });
 /**       
        zoomOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jgraphController.zoomOutActionPerfomed();
            }
        });

        zoomIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jgraphController.zoomInActionPerfomed();
            }
        });
**/
        return box2;
    }
    
    public static AbstractButton createModeButton(String title,
            ButtonGroup modeGroup, Box box, String toolTipText) {
        JToggleButton button = new JToggleButton(title);
        button.setOpaque(false);
        modeGroup.add(button);
        box.add(button);

        button.setRolloverEnabled(true);
        button.setToolTipText(toolTipText);

        return button;
    }


    public BR_JGraph getJGraph() {
	return jgraph;
    }
    
    public JGraphController getJGraphController() {
        return jgraphController;
    }

    /**
     * Get the bounds of the current viewport.
     * @return result of {JGraph#getViewPortBounds()}, converted to integers
     */
	public Rectangle getGraphViewPortBounds() {
		if(jgraph == null)
			return null;
		Rectangle2D r2d = jgraph.getViewPortBounds();
		if(r2d == null)
			return null;
		if(trace.getDebugCode("mg"))
			trace.out("mg", String.format("JGraphPanel.getGraphViewPortBounds() r2d [%6.2f %6.2f %6.2f %6.2f]",
					r2d.getX(), r2d.getY(), r2d.getWidth(), r2d.getHeight()));
		Rectangle r = new Rectangle((int) Math.floor(r2d.getX()),  (int) Math.floor(r2d.getY()),
			(int) Math.ceil(r2d.getWidth()), (int) Math.ceil(r2d.getHeight()));
		if(trace.getDebugCode("mg"))
			trace.out("mg", String.format("JGraphPanel.getGraphViewPortBounds() r   [%3d    %3d    %3d    %3d   ]",
					r.x, r.y, r.width, r.height));
		return r;
	}


}
