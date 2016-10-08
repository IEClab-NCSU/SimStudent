package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;

public class ExampleTracerTracerChangedEvent extends ExampleTracerEvent {
	private ExampleTracerTracer oldTracer;
	private ExampleTracerTracer newTracer;
	
	
	public ExampleTracerTracerChangedEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}
	
	public ExampleTracerTracerChangedEvent(Object source, ExampleTracerTracer oldTracer, ExampleTracerTracer newTracer){
		super(source);
		this.oldTracer = oldTracer;
		this.newTracer = newTracer;
	}
	
	public ExampleTracerTracer getOld (){
		return oldTracer;
	}
	
	public ExampleTracerTracer getNew () {
		return newTracer;
	}

}
