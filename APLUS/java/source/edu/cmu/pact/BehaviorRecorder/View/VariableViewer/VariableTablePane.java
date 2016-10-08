package edu.cmu.pact.BehaviorRecorder.View.VariableViewer;

import java.util.Comparator;

import javax.swing.JTable;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTableChangeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTableModel;
import edu.cmu.pact.Utilities.trace;

public class VariableTablePane extends VTDisplayPane implements ProblemModelListener{
	private JTable table;
	private VariableTableModel vtm;
	private String type= "VariableTablePane";
	private static int count = 0;
	private int instance;
	
	public VariableTablePane(ProblemModel pm) {
		if (trace.getDebugCode("vtm")) trace.outNT("vtm", "VariableTablePane constructor");
		instance = count++;
		
		vtm = new VariableTableModel(pm.getVariableTable());
		pm.getVariableTable().setModel(vtm);
		if (trace.getDebugCode("vtm")) trace.outNT("vtm", "VariableTablePane: Seeding TableModel #"+vtm.getInstance()+" with VariableTable #"+pm.getVariableTable().getInstance());
		table = new JTable();
		
		table.setEnabled(true);
		table.setCellSelectionEnabled(true);
		table.setToolTipText("Variable Table");
		/*try {
			table.setAutoCreateRowSorter(false);
			javax.swing.table.TableRowSorter<TableModel> sorter = new javax.swing.table.TableRowSorter<TableModel>(vtm);
			sorter.setComparator(0, new KeyListComparator());
			sorter.setComparator(1, new KeyListComparator());
			table.setRowSorter(sorter);
		}
		catch (NoClassDefFoundError e){
			trace.err("RowSorter not supported in Java 5: "+e);
		}*/
		table.setModel(vtm);
		pm.addProblemModelListener(this);
		vtm.addTableModelListener(table);
		setViewportView(table);
	}
	
	public JTable getTable(){
		return table;
	}

	@Override
	public void problemModelEventOccurred(ProblemModelEvent e) {
		//parse the event and update etc.
		if (e instanceof VariableTableChangeEvent){
			VariableTable vt = (VariableTable)e.getNewValue();
			vtm = new VariableTableModel(vt);
			vt.setModel(vtm);
			/*try {
				javax.swing.table.TableRowSorter<TableModel> sorter = new javax.swing.table.TableRowSorter<TableModel>(vtm);
				sorter.setComparator(0, new KeyListComparator());
				sorter.setComparator(1, new KeyListComparator());
				table.setRowSorter(sorter);
			}
			catch (NoClassDefFoundError err){
				trace.err("RowSorter class not supported in Java 5: "+err);
			}*/
			table.setModel(vtm);
			vtm.addTableModelListener(table);
			if (trace.getDebugCode("vtm")) trace.printStack("vtm", "new VTM(VT #"+vt.getInstance()+")");
		}
	}	

	
	//this is a comparator that is used to sort VariableTable keys so that all "link" variables 
	//show up at the bottom of a list
	//it is currently not used because of compatibility issues
	private class KeyListComparator implements Comparator<Object> {
		private boolean aislink;
		private boolean bislink;
		
		public int compare(String a, String b) {
			if (trace.getDebugCode("klc")) trace.outNT("klc", "compare ("+a+","+b+")");
			//if the variable name is more than 4 chars long check if it is a link
			if (a.length()>=4)
				aislink=a.startsWith("link");
			else
				aislink=false;
			//if the variable name is more than 4 chars long check if it is a link
			if (b.length()>=4)
				bislink=b.startsWith("link");
			else
				bislink=false;	
			//if a is a link it should be beyond b
			if(aislink&&!bislink)
				return 1;
			//if b is a link it should be beyond a
			else if(!aislink&&bislink)
				return -1;
			//if they are both links then they should be sorted numerically
			else if (aislink&&bislink)						
				return a.compareTo(b);
			//if neither of them are links then they should be sorted alphabetically
			else
				return a.compareTo(b);
		}

		@Override
		public int compare(Object a, Object b) {
			if (a==null){
				if (b==null)
					return 0;
				else
					return 1;
			}
			else if (b==null)
				return -1;
			else 
				return compare(a.toString(),b.toString());
		}
		
	}
	
	public String getType(){
		return type;
	}
	
	public int getInstance(){
		return instance;
	}
}
