package edu.cmu.pact.BehaviorRecorder.View.VariableViewer;

import javax.swing.JTable;

import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTableModel;
import edu.cmu.pact.Utilities.trace;

/**
 * 
 */
public class SAITablePane extends JTable 
{
	private static final long serialVersionUID = -3384223968909233716L;
	
	private VariableTableModel vtm;
	private int instance;
	private String type = "SAITablePane";
	
	/**
	 * 
	 * @param controller
	 */
	public SAITablePane(BR_Controller controller)
	{
		vtm = new VariableTableModel(controller.getsaiTable());
		controller.getsaiTable().setModel(vtm);
		
		if (trace.getDebugCode("vtm")) 
			trace.outNT("vtm", "SAITablePane: Seeding TableModel #"+vtm.getInstance()+" with VariableTable #"+controller.getProblemModel().getVariableTable().getInstance());
		
		setEnabled(true);
		setCellSelectionEnabled(true);
		setModel(vtm);
		setToolTipText("Last Student Action");
		vtm.addTableModelListener(this);
	}
	/**
	 * 
	 */
	private void debug (String aMessage)
	{
		CTATBase.debug ("SAITablePane",aMessage);
	}
	/**
	 * 
	 * @return
	 */
	public String getType()
	{
		return type;
	}
	/**
	 * 
	 * @return
	 */
	public int getInstance()
	{
		return instance;
	}	
	/**
	 * 
	 */
	public void reset (BR_Controller controller)
	{
		debug ("reset ()");
		
		VariableTable vTable=controller.getsaiTable();
		
		resetVariableTable (vTable);
		
		vtm = new VariableTableModel(vTable);		
		controller.getsaiTable().setModel(vtm);
		setEnabled(true);
		setCellSelectionEnabled(true);
		setModel(vtm);		
	}
	/**
	 * This code needs to move the the BR Controller!!
	 */
	private void resetVariableTable (VariableTable vTable)
	{
		vTable.clear();    	
		vTable.put("selection[0]",null);
		vTable.put("action[0]", null);
		vTable.put("input[0]", null);
		vTable.put("type", null);	
	}
}
