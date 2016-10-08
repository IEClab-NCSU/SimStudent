package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Frame;
import java.awt.Window;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.cmu.old_pact.dormin.Communicator;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;


public class LispJavaConnection {

    static LispJavaConnection selfConnection;

    ClientListener clientListener = null;

    FakeLispInterface fakeLispInterface = null;

    private Socket pipe;

    public static String theHost = "";

    protected boolean listenerStarted, disconnected, reconnectOnly, restart,
            socketOpen;

    protected int portNum;

    Hashtable Registry;

    Communicator toolCommunicator;

	/**
	 * Whether this is a Jess connection.
	 */
	private boolean jessConnection = false;

    protected BR_Controller controller;

    
	/**
	 * Main constructor.
	 *
	 * @param host
	 * @param port
	 * @param toolCommunicator
	 * @param isJessConnection value for {@link jessConnection}
	 */
    public LispJavaConnection(String host, int port,
							  Communicator toolCommunicator, BR_Controller controller) {
        theHost = host;
        portNum = port;
        selfConnection = this;
        this.toolCommunicator = toolCommunicator;
        Registry = new Hashtable();
        this.jessConnection = controller.getCtatModeModel().isJessMode();
        this.controller = controller;
    } 


    public LispJavaConnection(String host, Communicator toolCommunicator, BR_Controller controller) {
        
        this(host, 1001, toolCommunicator, controller);
    }

    public LispJavaConnection() {
        trace.out ("creating empty lisp java connection");
        trace.printStack("m");
    }

    public void registrate(String name, Object val) {

        Object v = Registry.get(name);
        if (v != null && v instanceof Frame) ((Frame) v).setVisible(false);
        Registry.put(name, val);
    }

    public Object getObject(String key) {
        return Registry.get(key);
    }

    public void unRegistrate() {
        for (Enumeration e = Registry.keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();

            Object obj = Registry.get(key);
            if (obj instanceof Window && ((Window) obj).isVisible()) {
                ((Window) obj).setVisible(false);
                ((Window) obj).dispose();
            }
            obj = null;
        }
        Registry.clear();
    }

    public void unRegistrate(String key) {
        Object obj = Registry.get(key);
        if (obj != null) {
            if (obj instanceof Window) ((Window) obj).setVisible(false);
            Registry.remove(key);
        }
    }

    public void firstConnection(boolean real_first, StudentInterface si)
            throws IOException {
        int numOfAttemps = 5;
        // sanket@cs.wpi.edu
        //		String caStr = System.getProperty("connectionAttempts");

        trace.out("lll","!!firstConnection!!");

        String caStr = "10";
        // sanket@cs.wpi.edu

        if (caStr != null) numOfAttemps = Integer.parseInt(caStr);
        int connectionAttempt = 0;
        boolean connected = false;

        // sanket@cs.wpi.edu
        //	    while (!connected) {
        if (!connected) {
            // sanket@cs.wpi.edu
            connected = true;
            try {
                setSocket(theHost, portNum);
            } catch (IOException e) {
                connected = false;
                connectionAttempt++;
                trace.err("connection attempt " + connectionAttempt
                        + " failed -- can't find cognitive modeler (" + theHost
                        + ":" + portNum + ")");
//                trace.addDebugCode("stack");
//                trace.printStack("stack");
                if (connectionAttempt < numOfAttemps) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                    };
                } else {
                    inforMationWindow iw = new inforMationWindow(si, this,
                            "Can't find cognitive model. Do you want to continue?");
                    iw.setVisible(true);
                    iw.toFront();
                    throw (new IOException("Can't find cognitive modeler"));
                }
            }
        }
    }

    public void firstConnection(boolean real_first, StudentInterface si,
            int arg1, boolean arg2) throws IOException {
        this.firstConnection(real_first, si);
    }

    public String getHost() {
        return theHost;
    }

    public void setListenerStarted(boolean wh) {
        listenerStarted = wh;
    }

    public void stopClientListener() {
        if (listenerStarted) {
            clientListener.stop();
            listenerStarted = false;
        }
    }

    public void startAgain(boolean just) throws IOException {
        disconnect();
        reconnectOnly = just;
        clientListener = (ClientListener) Registry.get("ClientListener");
        if (clientListener != null) {
            if (clientListener.isAlive()) clientListener.stop();
            listenerStarted = false;
            restart = true;
        }
        firstConnection(false, null);
    }

    public void connectClientListener(ClientListener all) {
        clientListener = all;
        listenerStarted = true;
    }

    public synchronized void waitForSocket() {
        try {
            while (pipe == null)
                wait();
        } catch (InterruptedException e) {
            trace.err("LispJavaConnection waitForSocket: " + e);
        }
    }

    public void searchReconnect() throws IOException {
        disconnect();
        firstConnection(true, null);
    }

    private void setSocket(String host, int po) throws IOException {

        if (controller.getCtatModeModel().isJessMode()) {
            socketOpen = true;
            disconnected = false;
            startSession();
            return;
        }

        trace.out ("m", "setSocket: port = " + po + " this = " + this);
        if (po > 1000) {
            try {
                portNum = po;
                theHost = host;
                pipe = new Socket(theHost, portNum);
                trace.out ("m", "socket = " + pipe + " this = " + this);
                socketOpen = true;
                disconnected = false;
                startSession();
            } catch (UnknownHostException e) {
                trace.err("LispJavaConnection: Unknown host " + host
                        + " in setSocket");
                throw (new IOException("Unknown host -- configuration error"));
            } catch (ConnectException e) {
                trace.err("LispJavaConnection: Connection to "
                        + theHost + ":" + portNum + " refused");
                e.printStackTrace();
                throw (new IOException("Connection Refused"));
            }
        }
        if (po == -1) trace.err("LispJavaConnection: No free ports");
    }

    private void startSession() {
        SendToLisp stl = new SendToLisp(this, "Connect", controller);
        stl.run();
        clientListener = new ClientListener(this, controller);
        clientListener.start();

    }

    Socket getSocket() {
        return pipe;
    }

    public LispJavaConnection connect() {
        return this;
    }

    public String getHostName() {
        return theHost;
    }

    public int getPortNum() {
        return portNum;
    }

    public void disconnect() {
        unRegistrate();
        if (pipe != null) {
            disconnected = true;
            try {
                String across = "Disconnect\r\n";
                java.io.PrintStream out = new java.io.PrintStream(pipe
                        .getOutputStream());
                out.print(across);
                socketOpen = false;
                pipe.close();
                pipe = null;
                trace.err("Disconnected from server");

            } catch (UnknownHostException ho) {
                trace.err("LispJavaConnection:  disconnect: " + ho);
            } catch (IOException e) {
                trace.err("LispJavaConnection:  disconnect: " + e);
            }
        }
    }

    public void messageReceived(String mess) {
        //trace.out("in LJC, message received *"+mess+"*");
        //		long startTime = System.currentTimeMillis();
        toolCommunicator.handleMessage(mess);
        //		getFakeInterface().receiveMessage(mess);
        //		long endTime = System.currentTimeMillis();
        //		long totalTime = endTime-startTime;
        //		if (totalTime > 1000)
        //			trace.out((endTime-startTime)+":"+mess);
    }

    public void showSendMessage(String message) {
        getFakeInterface().showSendMessage(message);
    }

    FakeLispInterface getFakeInterface() {
        if (fakeLispInterface == null) {
            Frame foo = new Frame("");
            fakeLispInterface = new FakeLispInterface(this, foo);
        }
        return fakeLispInterface;
    }

    public void sendMessage(String mess) {
        if (Boolean.getBoolean("doDebug") && !getFakeInterface().isVisible())
                getFakeInterface().setVisible(true);
        getFakeInterface().showSendMessage(mess);
        SendToLisp stl = new SendToLisp(this, mess, controller);
        stl.run();
    }

	
}
