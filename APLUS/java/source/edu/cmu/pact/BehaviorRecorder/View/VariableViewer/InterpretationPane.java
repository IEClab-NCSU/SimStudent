package edu.cmu.pact.BehaviorRecorder.View.VariableViewer;

import javax.swing.JTable;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTableModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerInterpretation;
import edu.cmu.pact.Utilities.trace;

public class InterpretationPane extends VTDisplayPane {
	private JTable table;
	private VariableTableModel vtm;
	private ExampleTracerInterpretation eti;
	private static int count = 0;
	private int instance;
	private String type = "InterpretationPane";
	
	public InterpretationPane(ExampleTracerInterpretation eti) {
		instance = count++;
		this.eti=eti;
		vtm = new VariableTableModel(eti.getVariableTable());
		eti.getVariableTable().setModel(vtm);
		if (trace.getDebugCode("eti")) trace.outNT("eti", "InterpretationPane: Seeding TableModel #"+vtm.getInstance()+" with VariableTable #"+eti.getVariableTable().getInstance());
		table = new JTable();	
		table.setEnabled(true);
		table.setCellSelectionEnabled(true);
		table.setToolTipText("Variable Table");
		table.setModel(vtm);
		vtm.addTableModelListener(table);
		setViewportView(table);
		this.setName("InterpretationPane "+instance);
		table.setName("Interpretation "+instance+" JTable");
	}
	
	public JTable getTable(){
		return table;
	}
	
	public ExampleTracerInterpretation getInterpretation(){
		return eti;
	}
	
	public void changeInterpretation(ExampleTracerInterpretation eti){
		this.eti=eti;
		vtm = new VariableTableModel(eti.getVariableTable());
		eti.getVariableTable().setModel(vtm);
		table.setModel(vtm);
		vtm.addTableModelListener(table);
	}
	
	public int getInstance(){
		return instance;
	}

	@Override
	public String getType() {
		return type;
	}
}
