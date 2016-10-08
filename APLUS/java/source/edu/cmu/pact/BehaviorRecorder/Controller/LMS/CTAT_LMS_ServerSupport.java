/*
 * Created on Aug 17, 2005
 *
 */
package edu.cmu.pact.BehaviorRecorder.Controller.LMS;

import java.net.UnknownHostException;

import cl.LMS.client.LMS_Client;
import cl.LMS.exception.LMS_Exception;
import cl.LMS.exception.LMS_StartupException;
import edu.cmu.pact.Utilities.trace;

public class CTAT_LMS_ServerSupport {
    ////////////////////////////////////////////////////////////////
    /**
     */
    ////////////////////////////////////////////////////////////////
    public static LMS_Client initLMSServer() throws LMS_Exception, LMS_StartupException, UnknownHostException {
        cl.LMS.server.I_Startup lms_starter;

        lms_starter = new cl.LMS.server.filedata.Startup();
       

        lms_starter.setStartParameters( "6543" );
        lms_starter.initialize();
        LauncherThreadGroup launcher_group_d =
                    new LauncherThreadGroup( "Launcher Threads" );

        Thread lms_server_thread_d = new Thread( launcher_group_d,
                lms_starter );
        lms_server_thread_d.setName( "LMS Server Thread" );
        lms_server_thread_d.setDaemon( true );
        lms_server_thread_d.start();   // STUB how to catch startup exceptions

        LMS_Client lmsClient = LMS_Client.create();
        return lmsClient;
    }

    ////////////////////////////////////////////////////////////////
    /**
     */
    ////////////////////////////////////////////////////////////////
    private static final class LauncherThreadGroup extends ThreadGroup
    {
        LauncherThreadGroup( String name )
        {
            super( name );
        }

        // all the same except we'll define the uncaughtException method
        public void uncaughtException( Thread t, Throwable e )
        {
            super.uncaughtException( t, e );

            if ( e instanceof ThreadDeath )
                return;

            trace.err( "LauncherThreadGroup has uncaughtException in one of its threads" );
            trace.err( e.toString() );
            trace.err( "FATAL and UNGRACEFUL SHUTDOWN !!! " );

            //  make sure no finalizers around to deadlock...
            //Runtime.runFinalizersOnExit( false );
            
            System.exit( 1 );
        }
    }
}
