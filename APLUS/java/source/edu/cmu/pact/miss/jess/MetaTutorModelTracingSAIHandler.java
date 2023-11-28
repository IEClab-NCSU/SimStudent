package edu.cmu.pact.miss.jess;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.RuleActivationNode;
import edu.cmu.pact.miss.AskHint;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;


/**
 *
 */
public class MetaTutorModelTracingSAIHandler extends SAIHandler {

	/**
	 *
	 */
	private static class ExecuteInstance {
		
		final String selection, action, input;
		final ModelTracer amt;
		final CTAT_Controller controller;
		
		public ExecuteInstance(String sel, String act, String inp, ModelTracer mt,
				CTAT_Controller controller) {
			selection = sel;
			action = act;
			input = inp;
			amt = mt;
			this.controller = controller;
		}
		
		/**
		 * 
		 */
		void execute(){
			//if (trace.getDebugCode("rr")) trace.out("rr","Enter in execute");
			MetaTutorModelTracingSAIHandler handler = null;
			handler = new MetaTutorModelTracingSAIHandler(selection, action, input, amt, controller);
			handler.processSAI();
			//if (trace.getDebugCode("rr")) trace.out("rr","Exit from execute");
		}
	}
	/**
	 * 
	 */
	static class Queue implements Runnable {

		private java.util.Queue<ExecuteInstance> queue = new LinkedList<ExecuteInstance>();
		
	    private static final int MAX_THREAD_COUNT = 1;
	    
		/**	Thread pool with MAX_THREAD_COUNT available threads at any point of time. */
		private static ExecutorService saiThreadPool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

		private volatile boolean isActive = false;
		
	
		
		@Override
		public void run() {
			while(true){
				synchronized (this) {
					isActive = true;
					if(queue.peek() == null){
						isActive = false;
						//if (trace.getDebugCode("rr")) trace.out("rr","Terminating the thread " + Thread.currentThread());
						return;
					}
					ExecuteInstance ei = queue.remove();
					ei.execute();
				}
			}
		}
		
		int add(String sel, String act, String inp, ModelTracer amt, CTAT_Controller controller){
			return add(new ExecuteInstance(sel, act, inp, amt, controller));
		}
		
		int add(ExecuteInstance ei){
			synchronized (this) {
				//if (trace.getDebugCode("rr")) trace.out("rr","In add in ProdSysSAIHandler.Queue with ExecuteInstance");
				queue.add(ei);
				if(!isActive){
					//if (trace.getDebugCode("rr")) trace.out("rr","Creating a new Thread");
					Thread t = new Thread(this);
					t.start();
				}
				//if (trace.getDebugCode("rr")) trace.out("rr","Return from ProdSysSAIHandler.Queue with " + queue.size());
				return queue.size();
			}
		}
	}


	private Vector<ModelTracingListener> listeners=new Vector<ModelTracingListener>();
	synchronized public void addListenter(ModelTracingListener listener){
			listeners.add(listener);
	}
	
	protected void notifyListeners(ModelTracingEvent event){
		
		for (ModelTracingListener listener : listeners){
			listener.modelTracingController(event);
		}
	}
	
	
	public MetaTutorModelTracingSAIHandler(String sel, String act, String inp, ModelTracer amt, CTAT_Controller controller){
		super(sel, act, inp, amt, controller);
		messages = new Vector();
		this.addListenter(controller.getMissController().getSimSt().getBrController().getAmt());
		
	}
	
	public String processSAI(){
		try {
			
			
			if(ModelTracer.isSAIToBeModelTraced(selection, action)){	
				result = amt.runModelTrace(false, false, selection,action,input,messages);		
					
		    } else if(action.equalsIgnoreCase("MetaTutorClicked") && ((selection.equalsIgnoreCase("hint"))|| (selection.equalsIgnoreCase("activations")))){
				result = amt.runModelTrace(true, false, selection, "", "", messages);

		    } else if(action.equalsIgnoreCase("MetaTutorClicked") && (selection.equalsIgnoreCase("CLHint"))) {
		
				result = amt.runModelTrace(false, true, selection, "", "", messages);
		    }
			/*trigger event to send results to the listeners */
		
			ModelTracingEvent e=new ModelTracingEvent(this,selection,action,input,result,amt.getMatchedNode(),messages);
		//	notifyListeners(e);
			getController().getMissController().getSimSt().getBrController().getAmt().modelTracingController(e);
			amt.setMatchedNode(null);
			
		} catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}

	public Vector<String> getMessages(){
		return this.messages == null ? new Vector<String>() : this.messages;
	}

	private Vector messages;
	@Override
	public void sendResult() {
		// TODO Auto-generated method stub
		
	}
}
