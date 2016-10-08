package edu.cmu.old_pact.dragdrop;


public class MouseUpSimulator implements Runnable{
	private Thread ownThread;
	private DragSource source;
	private int x;
	private int y;
	
	public MouseUpSimulator(DragSource s){ 
		source = s;
	}
	
	
	public void startThread(int x, int y){
		this.x = x;
		this.y = y;
		stopThread();
  		ownThread = new Thread(this);
  		ownThread.start();
  	}
  	
  	public void stopThread(){
  		if(ownThread != null && ownThread.isAlive()){
  			ownThread.stop();
  			ownThread = null;
  		}
  	}
  	
  	public void run(){
  		long timeMark = System.currentTimeMillis();
		while((System.currentTimeMillis() - timeMark) < 3000){
			try{
				Thread.sleep(500);
			} catch (InterruptedException e) { 
			}
			source.performMouseUp(x, y);
		}
	}
}
	
	
	