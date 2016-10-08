package edu.cmu.pact.BehaviorRecorder.View.VariableViewer;

import javax.swing.JSplitPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import java.awt.Color;

/**
 * 
 */
public class VariableViewer extends JSplitPane implements TableModelListener 
{
	private static final long serialVersionUID = 4576272728118341908L;
	
	private SAITablePane saiTablePane;
	private CTAT_Launcher server;
	private VTITabbedPane vtiTabbedPane;
	private static final Color ACTIVE_COLOR = Color.WHITE;
	private static final Color INACTIVE_COLOR = new Color(240,240,240);
	
	/**
	 * 
	 * @param controller
	 */
	public VariableViewer(CTAT_Launcher server) 
	{
		this.setOrientation(VERTICAL_SPLIT);
		this.setName("Variable Viewer Pane");
		this.server = server;
		initComponents();
	}
	/**
	 * 
	 */
	private void initComponents()
	{
		BR_Controller controller = this.server.getFocusedController();
		saiTablePane = new SAITablePane(controller);
		vtiTabbedPane = new VTITabbedPane(controller.getProblemModel());
		saiTablePane.setName("SAI JTable");
		vtiTabbedPane.setName("InterpretationTabbedPane");
		this.setTopComponent(saiTablePane);
		this.setBottomComponent(vtiTabbedPane);
		saiTablePane.getModel().addTableModelListener(this);
		setComponentsEnabled(controller.getInterfaceLoaded());
	}
	
	/**
	 * Enable or disable the VariableViewer pane according to whether or not
	 * the currently focused problem graph is connected to a student interface.
	 */
	public void refresh() {
		// BR_Controller controller = this.server.getFocusedController();
		// FIXME: if controller isn't connected to a student interface, set disabled/gray out;
		// otherwise, refresh
		if(this.server.getFocusedController().getUniversalToolProxy() != null &&
				this.server.getFocusedController().getUniversalToolProxy().getStudentInterfaceConnectionStatus().isConnected()) {
			initComponents();
			setComponentsEnabled(true);
		} else {
			initComponents();
			setComponentsEnabled(false);
		}
	}
	
	/**
	 * FIXME: find the correct components to enable/disable!
	 * These don't take the correct ones.
	 * @param enabled		true if the currently focused problem graph is
	 * 						connected to a student interface; false otherwise
	 */
	private void setComponentsEnabled(boolean enabled) {
		Color backgroundColor = enabled ? ACTIVE_COLOR : INACTIVE_COLOR;
		this.saiTablePane.setBackground(backgroundColor);
		this.vtiTabbedPane.setTableBackground(backgroundColor);
	}
	
	/**
	 * 
	 */
	@Override
	public void tableChanged(TableModelEvent arg0) 
	{
		this.setDividerLocation(saiTablePane.getRowHeight()*saiTablePane.getModel().getRowCount()+saiTablePane.getRowMargin());
	}	
	/**
	 * 
	 */
	public void reset ()
	{
		saiTablePane.reset(this.server.getFocusedController()); // does SAITablePanel need to be edited too?
	}
}
