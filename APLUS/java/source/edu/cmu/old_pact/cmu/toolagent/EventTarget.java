package edu.cmu.old_pact.cmu.toolagent;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;

public class EventTarget {

    String host;

    String url;

    String targetType;

    String setserver;

    LispJavaConnection connectTo;

    String portStr;

    Socket pipe;

    int portNum;

    // sanket@cs.wpi.edu
    InterfaceProxy proxy = new InterfaceProxy();

    private static String objectString = "";

    private BR_Controller controller;

    // sanket@cs.wpi.edu

    EventTarget(LispJavaConnection ljc, BR_Controller controller)
            throws UnknownHostException, IOException {
        connectTo = ljc; // sewall: was LispJavaConnection.getSelf();
        this.controller = controller;
        pipe = connectTo.getSocket();
        // trace.out ("m", "EventTarget: pipe = " + pipe + " connectTo = " +
        // connectTo);
    }

    public void send(String data) {
        // sanket@cs.wpi.edu
        Vector vNames, vValues;
        String messageType = null;

        // sewall 6/15/04 connectTo.isJessConnection() replaces UTP.isUseJess()
        //
        // trace.out ("m", "EventTarget: send: " + data);
        // trace.printStack ("m");

        if (controller.getCtatModeModel().isJessTracing() && !data.equals("")) {
        	throw new IllegalStateException("EventTarget called to send Jess message (now sent by UTP); message:\n  "+data);
        }

        try {
            if (!data.equals("") && pipe != null) {
                PrintStream outputStream = new PrintStream(pipe
                        .getOutputStream());
                String fullText = data + "\r\n";
                // trace.out ("m", "output stream = " + outputStream);
                outputStream.print(fullText);
                outputStream.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // sanket@cs.wpi.edu
    }

    public DataInputStream receive(String data) {

        // mike s
        if (controller.getCtatModeModel().isJessMode())
            return null;

        DataInputStream in;
        in = null;
        try {
            in = new DataInputStream(pipe.getInputStream());
        } catch (UnknownHostException ex) {
            System.out.println("EventTarget receive " + ex.toString());
        } catch (IOException ex) {
            System.out.println("EventTarget receive " + ex.toString());
        }
        return in;
    }

    public DataInputStream receive() throws UnknownHostException, IOException {

        // mike s
        if (controller.getCtatModeModel().isJessMode())
            return null;

        DataInputStream inputStream = new DataInputStream(pipe.getInputStream());
        return inputStream;

    }
}
