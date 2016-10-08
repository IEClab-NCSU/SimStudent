package edu.cmu.pact.BehaviorRecorder.Controller;

public class CTAT_ReducedLauncher extends SingleSessionLauncher {

	/**
	 * Launches CTAT in reduced mode by adding the command line option "-reduced"
	 * to whatever other command line options you may have entered.
	 * @param args
	 */
	public static void main(String[] args)
	{
		String[] args2 = new String[args.length + 1];
		for(int i = 0; i < args.length; i++)
			args2[i] = args[i];
		args2[args2.length - 1] = "-reduced";
		SingleSessionLauncher cl = new SingleSessionLauncher(args2);
		cl.launch();
	}

}
