/**
 * Copyright 2007-2012 Carnegie Mellon University.
 */
package pact.CommWidgets;

import java.net.URL;

import javax.swing.ImageIcon;

import edu.cmu.pact.Utilities.trace;

/**
 * Keep track of the state of the connection between the Behavior Recorder
 * and the student interface.
 */
public enum StudentInterfaceConnectionStatus {
	
	/** Indicates no connection between Behavior Recorder and student interface. */
	Disconnected("Disconnected"),
	
	/** Indicates connection established but compatibility with currently-loaded problem unknown. */
	NewlyConnected("Now Connected"),
	
	/** Indicates connection established, compatibility with currently-loaded problem known. */
	Connected("Connected");
	
	/** Icon cache for {@link #getIcon()}. */
	private static final ImageIcon[] icons = new ImageIcon[values().length];
	
	/** Text for {@link #toString()}. */
	private final String text;
	
	/**
	 * @param text value for {@link #text}, used by {@link #toString()}
	 */
	private StudentInterfaceConnectionStatus(String text) {
		this.text = text;
	}
	
	/**
	 * @return true if not {@value #Disconnected}
	 */
	public boolean isConnected() {
		return (this != Disconnected);
	}
	
	/**
	 * Return a string indicating connection status
	 * @return connection status
	 */
	public String getConnectionStatus() {
		return (this == Disconnected ? "Not Connected" : "Connected");
	}

	/**
	 * Return an icon associated with this connection status.
	 * Uses cache {@link #icons}; loads it if necessary.
	 * @return icon loaded from URL; null on error
	 */
	public ImageIcon getIcon() {
		if (icons[ordinal()] == null) {  // load cache if not yet loaded
			synchronized(icons) {
				String baseName = (this == Disconnected ?
						"interface_disconnected" : "interface_connected");
				String imageName = "pact/"+baseName+".png";
				URL imageURL = null;
				try {
					imageURL = getClass().getClassLoader().getResource(imageName);
					icons[ordinal()] = new ImageIcon(imageURL);
				} catch (Exception e) {
					trace.err(this+" error getting image \""+imageName+
							"\" from address "+imageURL+":\n  "+e+
							(e.getCause() == null ? "" : "; cause: "+e.getCause()));
				}
			}
		}
		return icons[ordinal()];
	}

	/**
	 * Override to return user-friendly text.
	 * @return {@link #text}
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return text;
	}
	
	/**
	 * Test harness.
	 * @param args strings to try with {@link #valueOf(String)}
	 */
	public static void main(String[] args) {
		trace.out("values()\n");
		for(StudentInterfaceConnectionStatus s : values())
			System.out.printf("[%d] %-17s %s\n", s.ordinal(), s, s.toString());
		trace.out();
		
		for(String arg : args) {
			try {
				StudentInterfaceConnectionStatus s = valueOf(arg);
				System.out.printf("from %-20s: [%d] %-17s %s\n", arg, s.ordinal(), s, s.toString());
			} catch (Exception e) {
				System.out.printf("exception on valueOf(\"%s\"): %s;\n  cause: \n",
						arg, e.toString(), (e.getCause() == null ? "null" : e.getCause().toString()));
			}
		}
	}
}
