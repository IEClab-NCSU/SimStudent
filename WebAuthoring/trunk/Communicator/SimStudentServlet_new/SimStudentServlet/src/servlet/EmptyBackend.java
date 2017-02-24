package servlet;

import java.util.List;

import interaction.Backend;
import interaction.InterfaceEvent;

/*
 * Empty backend to be used if none is found
 */
public class EmptyBackend extends Backend {
	
	
	//Added parameter to match the parent constructor - Shruti
	public EmptyBackend(String[] argV) {
		super(argV);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void processInterfaceEvent(InterfaceEvent ie) {
		System.out.println("No backend provided");
		
	}

	//Added unimplemented method - Shruti
	@Override
	public void parseArgument(String[] arg) {
		// TODO Auto-generated method stub
		
	}


}
