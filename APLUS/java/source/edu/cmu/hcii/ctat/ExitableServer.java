package edu.cmu.hcii.ctat;

/**
 * Methods by which a caller can prompt a server instance, perhaps running in
 * a separate thread, to exit.
 */
public interface ExitableServer {

	/**
	 * Whether this instance has begun the process of quitting.
	 * @return true if now exiting; else false
	 */
	public boolean isExiting();

	/**
	 * Tell this instance to exit. This may entail interrupting a thread to tell it to exit.
	 * @return previous value of {@link #isExiting()}
	 */
	public boolean startExiting();

}