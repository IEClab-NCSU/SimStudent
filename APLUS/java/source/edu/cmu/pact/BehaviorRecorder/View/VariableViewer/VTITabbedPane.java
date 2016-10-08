package edu.cmu.pact.BehaviorRecorder.View.VariableViewer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTabbedPane;

import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTableModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerEventListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerInterpretation;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerTracer;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerTracerChangedEvent;
import edu.cmu.pact.Utilities.trace;

/**
 * 
 */
public class VTITabbedPane extends JTabbedPane implements ExampleTracerEventListener 
{
	private static final long serialVersionUID = 5776521536223331624L;
	
	private ExampleTracerTracer tracer;
	private ArrayList<VTDisplayPane> panes;
	private VariableTablePane vtpane;
	private static int count =0;
	private int instance;
	
	/** 
	 * @param pm
	 */
	public VTITabbedPane(ProblemModel pm) 
	{
		debug ("VTITabbedPane ()");
		
		instance = count++;
		if (trace.getDebugCode("eti")) trace.outNT("eti", "VTITabbedPane()");
		tracer = pm.getExampleTracerGraph().getExampleTracer();
		if (trace.getDebugCode("eti")) trace.outNT("eti", "VTInerpretationTabbedPane has TracerTracer #"+tracer.getInstance());
		this.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		this.setTabPlacement(JTabbedPane.TOP);
		vtpane=new VariableTablePane(pm);
		vtpane.setName("VariableTablePane");
		vtpane.getTable().setName("VariableTable JTable");
		this.addTab("Active", vtpane);
		if (trace.getDebugCode("eti")) trace.outNT("eti", "adding "+vtpane.getType()+" #"+vtpane.getInstance()+" to tab #"+0);
		makePanes();
		addTabs();
		tracer.addExampleTracerEventListener(this);
		if (trace.getDebugCode("eti")) trace.outNT("eti", "subscribing VTITabbedPane #"+instance+" to the listener list of TracerTracer #"+tracer.getInstance());
	}
	/**
	 * 
	 */
	private void debug (String aMessage)
	{
		CTATBase.debug ("VTITabbedPane",aMessage);
	}
	/**
	 * adds all the panes to new tabs, should only ever be called after clearPanes and makePanes
	 */
	private void addTabs() 
	{
		debug ("addTabs ()");
		
		Iterator<VTDisplayPane> it = panes.iterator();
		
		for (int i=1;it.hasNext();i++)
		{
			VTDisplayPane vtdp = (VTDisplayPane)it.next();
			this.addTab("Interpretation "+i, vtdp);
			if (trace.getDebugCode("eti")) trace.outNT("eti", "adding "+vtdp.getType()+" #"+vtdp.getInstance()+" to tab #"+i);
		}
	}	
	/**
	 * creates a new set of InterpretationPanes based on the interpretations list it grabs from the tracer
	 */
	private void makePanes()
	{
		debug ("makePanes ()");
		
		if (trace.getDebugCode("vt")) 
			trace.outNT("vt", vtpane.getType()+" #"+vtpane.getInstance()+" has TableModel #"+((VariableTableModel)vtpane.getTable().getModel()).getInstance()+" which has VariableTable #"+((VariableTableModel)vtpane.getTable().getModel()).getVTInstance());
		
		ArrayList<ExampleTracerInterpretation> interpretations=tracer.getInterpretations();
		
		if (trace.getDebugCode("eti")) 
			trace.outNT("eti","makePanes()");
		
		Iterator<ExampleTracerInterpretation> it = interpretations.iterator();
		panes = new ArrayList<VTDisplayPane>();
		while(it.hasNext()){
			InterpretationPane interp = new InterpretationPane((ExampleTracerInterpretation) it.next());
			interp.setName("InterpretationPane " +interp.getInstance());
			interp.getTable().setName("InterpretationPane "+interp.getInstance()+" JTable");
			panes.add(interp);
			if (trace.getDebugCode("vt")) trace.outNT("vt", interp.getType()+" #"+interp.getInstance()+" has TableModel #"+((VariableTableModel)interp.getTable().getModel()).getInstance()+" which has VariableTable #"+((VariableTableModel)interp.getTable().getModel()).getVTInstance());
		}
	}
	/**
	 * removes all the current panes from their tabs
	 */	
	private void clearPanes()
	{
		Iterator<VTDisplayPane> it =panes.iterator();
		
		while(it.hasNext())
		{
			this.remove((VTDisplayPane)it.next());
		}
	}
	
	void setTableBackground(Color color) {
		this.vtpane.getTable().setBackground(color);
		for(int i = 0; i < panes.size(); i++) {
			InterpretationPane pane = (InterpretationPane)panes.get(i);
			pane.getTable().setBackground(color);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void ExampleTracerEventOccurred(ExampleTracerEvent e) 
	{
		if (trace.getDebugCode("eti")) trace.outNT("eti","ExampleTraceEventOccured()");
		//an ExampleTracerTracerChangedEvent message is effectively a "stop listening to me" message
		//so this grabs the new target to listen to
		if (e instanceof ExampleTracerTracerChangedEvent){
			if (trace.getDebugCode("eti")) trace.outNT("eti", "e was instanceof ExampleTracerTracerChangedEvent");
			tracer = ((ExampleTracerTracerChangedEvent) e).getNew();
			tracer.addExampleTracerEventListener(this);
			if (trace.getDebugCode("eti")) trace.outNT("eti", "subscribing VTITabbedPane #"+instance+" to the listener list of TracerTracer #"+tracer.getInstance());
		}
		//removes all the Interpretation panes in the panes ArrayList
		clearPanes();
		//recreates the panes ArrrayList and then builds a new set of InterpreationPanes based on the interpretations ArrayList
		makePanes();
		//adds all the panes in the panes ArrayList to tabs
		addTabs();
	}	
	/**
	 * returns the unique instance number for this VTITabbedPane
	 * 
	 * @return
	 */
	public int getInstance()
	{
		return instance;
	}
}
