package edu.cmu.old_pact.cl.utilities.Startable;


public interface Startable extends Runnable
{
    //  inherits run();

    //  port to use to communicate between tutor and interfaces
    public static final int DEFAULT_PORT = 1030;
    public void setPort( int port_to_tutor );

    /**
     **  return TRUE if this startable is finished.
     **  Launcher will be checking...
     **/
    public boolean isFinished();
    
    public void setIsFinished(boolean f);
    
    // string to display in the "About" box
    public void setVersionString( String version );
    
}
