package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Vector;

import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;
import edu.cmu.old_pact.java.util.CntrlblQueueListDisplay;
import edu.cmu.old_pact.java.util.QueueListDisplay;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;

/*This is the dialog used to control the flow of Dormin messages
 between the interface and the cognitive model.  It is based on
 FakeLispInterface.  The companion class CntrldLispJavaConnection
 takes the place of LispJavaConnection.  Instead of sending messages
 directly on to the interface or to the cog model, it passes them
 here via this class's receiveMessage method.  This dialog displays
 the messages for the user to see.  It can also compare incoming
 messages agaist expected messages read in from a file.*/

public class ControlledLispInterface extends Dialog {
    public static final int INTTOCM = 0;

    public static final int CMTOINT = 1;

    public static final String dorminHead = "SE/1.2";

    public static final String delimiter[] = { "About to send : ",
            "XXXX Received message by Application0 : " };

    private static final String msgNumStr = "MESSAGENUMBER";

    private static final char msgDelimChar = '&';

    private QueueListDisplay messagesSent[];

    private CntrlblQueueListDisplay outgoingMessageQueue[];

    private final TextField numMessageField[];

    private final TextField waitTimeField[];

    private CntrlblQueueListDisplay fileMessageQueue[];

    private LispJavaConnection jlc;

    private Frame parent;

    private edu.cmu.old_pact.java.util.Queue messageLog;

    /* flags for whether to send queued messages automatically or manually */
    private boolean autoSend[];

    private Checkbox autoSendCB[];

    /*
     * flag for whether to pay attention to message numbers when deciding if two
     * messages match
     */
    private boolean mesNumMismatch;

    /*
     * flag used to allow the user to cancel a long string of "do these match"
     * questions by aborting an in-progess multi-message send
     */
    private boolean cancelSend = false;

    /*
     * we synchronize on this object to, e.g., prevent messages from getting by
     * under our noses while we're querying the user about whether two messages
     * match
     */
    private Object messageSync;

    /*
     * whether to show all the gory details (true) or just a send button & not
     * much more (false)
     */
    private boolean details = true;

    private BR_Controller controller;

    public ControlledLispInterface(LispJavaConnection channel, Frame parent,
            BR_Controller controller) {
        super(parent);
        this.controller = controller;
        this.parent = parent;
        Dimension d = new Dimension(250, 240);

        setTitle("Message Interface");
        jlc = channel;
        messageLog = new edu.cmu.old_pact.java.util.Queue();
        messageSync = new Object();

        Font areaFont = new Font("helvetica", Font.PLAIN, 9);

        messagesSent = new QueueListDisplay[2];
        outgoingMessageQueue = new CntrlblQueueListDisplay[2];
        fileMessageQueue = new CntrlblQueueListDisplay[2];
        numMessageField = new TextField[2];
        waitTimeField = new TextField[2];

        autoSend = new boolean[2];
        autoSendCB = new Checkbox[2];

        for (int i = 0; i < 2; i++) {
            autoSend[i] = false;
            autoSendCB[i] = createAutoSendBox(i);

            messagesSent[i] = new QueueListDisplay("Messages Sent", 300,
                    QueueListDisplay.FULL);

            numMessageField[i] = new TextField("1", 3);
            waitTimeField[i] = new TextField("0", 5);
            outgoingMessageQueue[i] = new CntrlblQueueListDisplay(
                    "Messages Received", 200, QueueListDisplay.FULL);
            fileMessageQueue[i] = new CntrlblQueueListDisplay(
                    "Messages Expected", 200, QueueListDisplay.FULL);
        }

        updateLayout();

        setLocation(0, 50);
    }

    private void updateLayout() {
        if (details) {
            showDetails();
        } else {
            hideDetails();
        }
    }

    private void showDetails() {
        removeAll();

        /* this panel holds the controls for sending messages to the interface */
        Panel interfaceSendPanel = new Panel();
        interfaceSendPanel.setLayout(new GridBagLayout());

        GridbagCon.viewset(interfaceSendPanel, new Label("Send", 0), 0, 0, 1,
                1, 5, 5, 0, 0);
        GridbagCon.viewset(interfaceSendPanel, numMessageField[CMTOINT], 1, 0,
                1, 1, 5, 0, 0, 0);
        GridbagCon.viewset(interfaceSendPanel, new Label("messages", 0), 2, 0,
                1, 1, 1, 5, 0, 0);
        GridbagCon.viewset(interfaceSendPanel, new Label("Wait", 0), 0, 1, 1,
                1, 1, 5, 0, 0);
        GridbagCon.viewset(interfaceSendPanel, waitTimeField[CMTOINT], 1, 1, 1,
                1, 1, 0, 0, 0);
        GridbagCon.viewset(interfaceSendPanel,
                createSendMessageButton(CMTOINT), 0, 2, 1, 1, 5, 0, 10, 0);
        GridbagCon.viewset(interfaceSendPanel,
                createSendAllMessageButton(CMTOINT), 1, 2, 1, 1, 5, 0, 10, 0);
        GridbagCon.viewset(interfaceSendPanel, autoSendCB[CMTOINT], 1, 3, 1, 1,
                5, 5, 0, 5);

        /* this panel holds the controls for sending messages to the tutor */
        Panel tutorSendPanel = new Panel();
        tutorSendPanel.setLayout(new GridBagLayout());

        GridbagCon.viewset(tutorSendPanel, new Label("Send", 0), 0, 0, 1, 1, 5,
                5, 0, 0);
        GridbagCon.viewset(tutorSendPanel, numMessageField[INTTOCM], 1, 0, 1,
                1, 5, 0, 0, 0);
        GridbagCon.viewset(tutorSendPanel, new Label("messages", 0), 2, 0, 1,
                1, 1, 5, 0, 0);
        GridbagCon.viewset(tutorSendPanel, new Label("Wait", 0), 0, 1, 1, 1, 1,
                5, 0, 0);
        GridbagCon.viewset(tutorSendPanel, waitTimeField[INTTOCM], 1, 1, 1, 1,
                1, 0, 0, 0);
        GridbagCon.viewset(tutorSendPanel, createSendMessageButton(INTTOCM), 0,
                2, 1, 1, 5, 0, 10, 0);
        GridbagCon.viewset(tutorSendPanel, createSendAllMessageButton(INTTOCM),
                1, 2, 1, 1, 5, 0, 10, 0);
        GridbagCon.viewset(tutorSendPanel, autoSendCB[INTTOCM], 1, 3, 1, 1, 5,
                5, 0, 5);

        /* this panel holds the message areas on the left of the interface */
        Panel LHS = new Panel();
        LHS.setLayout(new GridBagLayout());

        outgoingMessageQueue[CMTOINT].setLayoutType(QueueListDisplay.FULL);
        GridbagCon.viewset(LHS, outgoingMessageQueue[CMTOINT].getPanel(), 0, 0,
                1, 1, 5, 5, 0, 5);
        GridbagCon.viewset(LHS, createReorderButton(CMTOINT), 0, 1, 1, 1, 5, 5,
                0, 5);
        fileMessageQueue[CMTOINT].setLayoutType(QueueListDisplay.FULL);
        GridbagCon.viewset(LHS, fileMessageQueue[CMTOINT].getPanel(), 0, 2, 1,
                1, 5, 5, 0, 5);
        GridbagCon.viewset(LHS, new Label("Messages Sent"), 0, 3, 1, 1, 5, 5,
                0, 5);
        GridbagCon.viewset(LHS, messagesSent[INTTOCM].getPanel(), 0, 4, 1, 1,
                5, 5, 0, 5);

        /* this panel holds the message areas on the right of the interface */
        Panel RHS = new Panel();
        RHS.setLayout(new GridBagLayout());

        GridbagCon.viewset(RHS, new Label("Messages Sent"), 0, 0, 1, 1, 5, 5,
                0, 5);
        GridbagCon.viewset(RHS, messagesSent[CMTOINT].getPanel(), 0, 1, 1, 1,
                5, 5, 0, 5);
        fileMessageQueue[INTTOCM].setLayoutType(QueueListDisplay.FULL);
        GridbagCon.viewset(RHS, fileMessageQueue[INTTOCM].getPanel(), 0, 2, 1,
                1, 5, 5, 0, 5);
        GridbagCon.viewset(RHS, createReorderButton(INTTOCM), 0, 3, 1, 1, 5, 5,
                0, 5);
        outgoingMessageQueue[INTTOCM].setLayoutType(QueueListDisplay.FULL);
        GridbagCon.viewset(RHS, outgoingMessageQueue[INTTOCM].getPanel(), 0, 4,
                1, 1, 5, 5, 0, 5);

        Panel FileButtons = new Panel();
        FileButtons.setLayout(new GridBagLayout());

        GridbagCon.viewset(FileButtons, createMesNumMismatchBox(), 0, 0, 1, 1,
                5, 5, 0, 5);
        GridbagCon.viewset(FileButtons, createReadQueueButton(), 0, 1, 1, 1, 5,
                5, 0, 5);
        GridbagCon.viewset(FileButtons, createWriteQueueButton(), 0, 2, 1, 1,
                5, 5, 0, 5);

        setLayout(new GridBagLayout());
        GridbagCon
                .viewset(this, createMoreLessButton(), 0, 0, 5, 1, 5, 5, 0, 5);
        GridbagCon.viewset(this, new Label("Cognitive Model"), 0, 2, 1, 1, 0,
                5, 0, 5);
        GridbagCon.viewset(this, LHS, 1, 1, 1, 3, 0, 0, 0, 0);
        GridbagCon.viewset(this, interfaceSendPanel, 2, 1, 1, 1, 0, 0, 0, 0);
        GridbagCon.viewset(this, FileButtons, 2, 2, 1, 1, 5, 5, 0, 5);
        GridbagCon.viewset(this, tutorSendPanel, 2, 3, 1, 1, 0, 0, 0, 0);
        GridbagCon.viewset(this, RHS, 3, 1, 1, 3, 0, 0, 0, 0);
        GridbagCon
                .viewset(this, new Label("Interface"), 4, 2, 1, 1, 0, 5, 0, 5);

        pack();
    }

    private void hideDetails() {
        removeAll();
        setLayout(new GridBagLayout());

        /* this panel holds the controls for sending messages to the tutor */
        Panel tutorSendPanel = new Panel();
        tutorSendPanel.setLayout(new GridBagLayout());

        GridbagCon.viewset(tutorSendPanel, new Label("Send", 0), 0, 0, 1, 1, 5,
                5, 0, 0);
        GridbagCon.viewset(tutorSendPanel, numMessageField[INTTOCM], 1, 0, 1,
                1, 5, 0, 0, 0);
        GridbagCon.viewset(tutorSendPanel, new Label("messages", 0), 2, 0, 1,
                1, 1, 5, 0, 0);
        GridbagCon.viewset(tutorSendPanel, createSendMessageButton(INTTOCM), 0,
                2, 1, 1, 5, 0, 10, 0);
        GridbagCon.viewset(tutorSendPanel, createSendAllMessageButton(INTTOCM),
                1, 2, 1, 1, 5, 0, 10, 0);
        GridbagCon.viewset(tutorSendPanel, autoSendCB[INTTOCM], 1, 3, 1, 1, 5,
                5, 0, 5);

        GridbagCon
                .viewset(this, createMoreLessButton(), 0, 0, 1, 1, 5, 5, 0, 5);
        GridbagCon.viewset(this, tutorSendPanel, 0, 1, 1, 1, 5, 5, 0, 5);
        outgoingMessageQueue[INTTOCM].setLayoutType(QueueListDisplay.MINIMAL);
        fileMessageQueue[INTTOCM].setLayoutType(QueueListDisplay.MINIMAL);
        GridbagCon.viewset(this, fileMessageQueue[INTTOCM].getPanel(), 0, 2, 1,
                1, 5, 5, 0, 5);
        GridbagCon.viewset(this, outgoingMessageQueue[INTTOCM].getPanel(), 0,
                3, 1, 1, 5, 5, 0, 5);

        pack();
    }

    /*
     * sets the autosend flag for the specified direction and updates the
     * checkbox display
     */
    private void setAutoSend(int which, boolean b) {
        autoSend[which] = b;
        autoSendCB[which].setState(b);
    }

    public static void swapEls(Vector v, int n1, int n2) {
        Object temp = v.elementAt(n1);
        v.setElementAt(v.elementAt(n2), n1);
        v.setElementAt(temp, n2);
    }

    /*
     * given two vectors of Strings, this function will return a re-ordering of
     * the vector v sorted so that some (or if we're lucky, all) of the elements
     * are lined up with their corresponding elements in vStatic. (And if we're
     * unlucky, the returned vector == v) vRetSize and vStaticSize allow the
     * caller to limit the extent of the "swappable" portions of one or both of
     * the vectors
     */
    private Vector reorderToMatch(Vector vRet, int vRetSize, Vector vStatic,
            int vStaticSize) {
        // Vector vRet = (Vector)v.clone();
        boolean temp = mesNumMismatch;
        mesNumMismatch = false;

        if (vRetSize > vRet.size()) {
            System.out.println("reorderToMatch: reported size of vRet ("
                    + vRetSize + ") is larger than actual size (" + vRet.size()
                    + "); fixing.");
            vRetSize = vRet.size();
        }

        if (vStaticSize > vStatic.size()) {
            System.out.println("reorderToMatch: reported size of vStatic ("
                    + vStaticSize + ") is larger than actual size ("
                    + vStatic.size() + "); fixing.");
            vStaticSize = vStatic.size();
        }

        /*
         * System.out.println(">>> begin reorderToMatch");
         * System.out.println("vRet.size() = " + vRet.size() + "; vStatic.size() = " +
         * vStatic.size());
         */
        boolean changed = false;
        do {
            changed = false;
            // System.out.println("looping ...");
            for (int j = 0; j < vRetSize; j++) {
                // System.out.println(">>>>> vRet[" + j + "]");
                /*
                 * if((j < vStatic.size()) &&
                 * (messagesMatch((String)vRet.elementAt(j),
                 * (String)vStatic.elementAt(j), false,"") == 1)){
                 * System.out.println("vRet[" + j + "] already matches vStatic[" +
                 * j + "]"); } else{
                 */
                for (int i = 0; i < vStaticSize; i++) {
                    // System.out.println(">>>>>>> vStatic[" + i + "]");
                    if ((i != j)
                            && (messagesMatch((String) vRet.elementAt(j),
                                    (String) vStatic.elementAt(i), false, "") == 1)) {
                        // System.out.println("vRet[" + j + "] matches vStatic["
                        // + i + "]");
                        if (!(messagesMatch((String) vRet.elementAt(i),
                                (String) vStatic.elementAt(i), false, "") == 1)) {
                            /*
                             * System.out.println("swapping vRet[" + j + "] and
                             * vRet[" + i + "]");
                             */
                            swapEls(vRet, i, j);
                            changed = true;
                            break;
                        }
                        /*
                         * else{ System.out.println("but vRet[" + i + "] already
                         * matches vStatic[" + i + "]"); System.out.println(" --
                         * not swapping"); }
                         */
                        /*
                         * vRet.insertElementAt(vRet.elementAt(j),i); if(j < i){
                         * System.out.println("vRet[" + j + "]: " +
                         * vRet.elementAt(j));
                         * System.out.println("vRet.removeElementAt(" + j +
                         * ")"); vRet.removeElementAt(j); } else{
                         * System.out.println("vRet[" + j + "+1]: " +
                         * vRet.elementAt(j+1));
                         * System.out.println("vRet.removeElementAt(" + (j+1) +
                         * ")"); vRet.removeElementAt(j+1); } }
                         */
                    }
                }
            }
        } while (changed);
        // System.out.println(">>> end reorderToMatch");

        mesNumMismatch = temp;

        return vRet;
    }

    /*
     * returns the index of the first position where the two strings differ, or
     * -1 if they are the same
     */
    private int firstMismatchedChar(String m1, String m2) {
        int min = Math.min(m1.length(), m2.length());

        for (int i = 0; i < min; i++) {
            if (m1.charAt(i) != m2.charAt(i)) {
                return i;
            }
        }

        return -1;
    }

    /*
     * given two dormin messages, returns whether they match: 0: no 1: yes not
     * yet: 2: maybe (ask user)
     */
    private int messagesMatch(String m1, String m2, boolean askuser,
            String userPrompt) {
        String mes1, mes2;

        if (!mesNumMismatch) {
            int start1 = m1.indexOf(msgNumStr);
            int end1 = m1.indexOf(msgDelimChar, start1);
            mes1 = m1.substring(0, start1) + m1.substring(end1);

            int start2 = m2.indexOf(msgNumStr);
            int end2 = m2.indexOf(msgDelimChar, start2);
            mes2 = m2.substring(0, start2) + m2.substring(end2);
        } else {
            mes1 = m1;
            mes2 = m2;
        }

        int index = firstMismatchedChar(mes1, mes2);

        if (index == -1) {
            return 1;
        } else if (askuser && askUserMessagesMatch(m1, m2, userPrompt)) {
            // "\nexpected on top, actual on bottom")){
            return 1;
        } else {
            /*
             * System.out.println("comparing messages:\n\t" + m1 + "\n\t" + m2);
             * System.out.println("comparing messages:\n\t" + mes1 + "\n\t" +
             * mes2);
             */
            return 0;
        }
    }

    boolean askUserMessagesMatch(String m1, String m2, String text) {
        messagesMatchDialog m = new messagesMatchDialog(parent, m1, m2, text);

        int ret = m.getAnswer();
        if (ret == 1) {
            return true;
        } else {
            if (ret == -1) {
                cancelSend = true;
            }

            return false;
        }
    }

    /*
     * calls the appropriate function to send the message to the interface or
     * the cog model as appropriate, and adds the message to the appropriate
     * sent messages box. Assumes that the message has already been removed from
     * other box(es) in which it might have appeared.
     */
    private void sendMessage(String message, int which) {
        /* add to display */
        messagesSent[which].push(message);

        /* add to log */
        messageLog.push(new LoggedMessage(which, message));

        /* send */
        switch (which) {
        case CMTOINT:
            ((CntrldLispJavaConnection) jlc).getToolCommunicator()
                    .handleMessage(message);
            break;
        case INTTOCM:
            SendToLisp stl = new SendToLisp(jlc, message, controller);
            stl.run();
            break;
        }
    }

    /* adds the message to the appropriate incoming message display box */
    private void queueMessage(String message, int which) {
        synchronized (messageSync) {
            outgoingMessageQueue[which].push(message);
        }
    }

    /*
     * called when a message comes in from the interface or the cog. model.
     * places the message in the received window or the sent window, depending
     * on the current settings for auto-send.
     */
    public void receiveMessage(String message, int which) {
        synchronized (messageSync) {
            if (autoSend[which]) {
                if (!fileMessageQueue[which].empty()) {
                    /* there is an expected next message: make sure they match */
                    if (messagesMatch((String) fileMessageQueue[which].peek(),
                            message, true, which
                                    + ": expected on top, actual on bottom") == 1) {
                        sendMessage(message, which);
                        /*
                         * now we need to remove the expectation that has been
                         * met
                         */
                        fileMessageQueue[which].pop();
                    } else {
                        /*
                         * the user may have clicked "cancel" in the message
                         * comparison dialog; if so, disable auto-send.
                         */
                        if (cancelSend) {
                            setAutoSend(which, false);
                            cancelSend = false;
                        }

                        queueMessage(message, which);
                        System.out
                                .println("warning: incoming message does not match expected");
                    }
                } else {
                    /*
                     * we have no expectations ... so our expectations are easy
                     * to meet. send it on!
                     */
                    sendMessage(message, which);
                }
            } else {
                queueMessage(message, which);
            }
        }
    }

    /*
     * grab the next message off of the queue specified by "which" and update
     * the display correspondingly. Returns the message, or null if there is no
     * next message or if the user clicked "cancel" when asked if the pending
     * message matched the expected next message.
     */
    private String popNextMessage(int which) {
        String thisMessage = null;
        synchronized (messageSync) {
            if (!outgoingMessageQueue[which].empty()) {
                // System.out.println("popping outgoing message " + which);

                /*
                 * is there an expected message that needs to be compared to the
                 * one we're about to pop?
                 */
                if (!fileMessageQueue[which].empty()) {
                    /*
                     * is the outgoing message what we expect it to be? (if not,
                     * we do nothing, so we'll eventually return null
                     */
                    if (messagesMatch((String) fileMessageQueue[which].peek(),
                            (String) outgoingMessageQueue[which].peek(), true,
                            which + ": expected on top, actual on bottom") == 1) {
                        /* as expected, so pop from both queues */
                        thisMessage = (String) (outgoingMessageQueue[which]
                                .pop());
                        fileMessageQueue[which].pop();
                    } else if (cancelSend) {
                        /*
                         * the user clicked "cancel" in the message comparison
                         * dialog; disable auto-send.
                         */
                        setAutoSend(which, false);
                        cancelSend = false;
                    } else {
                        /*
                         * the user clicked "no" in the message comparison
                         * dialog; pop the message, but don't pop the
                         * corresponding expected message
                         */
                        thisMessage = (String) (outgoingMessageQueue[which]
                                .pop());
                    }
                } else {
                    /*
                     * there is no expected message, so just pop from the
                     * outgoing queue
                     */
                    thisMessage = (String) (outgoingMessageQueue[which].pop());
                }
            } else {
                System.out.println("no outgoing messages");
                if ((which == INTTOCM) && (!fileMessageQueue[INTTOCM].empty())) {
                    System.out.println("popping read message");
                    thisMessage = (String) (fileMessageQueue[INTTOCM].pop());
                } else {
                    System.out.println("not popping expected message");
                }
            }
        }

        return thisMessage;
    }

    public void sendNMessages(int number, int wait, int which) {
        try {
            for (int i = 0; i < number; ++i) {
                String thisMessage = popNextMessage(which);

                if (thisMessage == null) {
                    i = number;
                } else {
                    sendMessage(thisMessage, which);
                    if (wait > 0) {
                        try {
                            Thread.sleep(wait);
                        } catch (InterruptedException e) {
                            System.out
                                    .println("ControlledLispInterface sendNMessages "
                                            + which + " " + e.toString());
                        }
                    }
                }
            }
        } catch (EmptyStackException ex) {
            System.out.println("ControlledLispInterface sendNMessages " + which
                    + " " + ex.toString());
        }
    }

    private Button createSendMessageButton(int which) {
        Button theButton;
        theButton = new Button("Send");
        switch (which) {
        case CMTOINT:
            theButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int numMessages = Integer.parseInt(numMessageField[CMTOINT]
                            .getText());
                    int waitTime = Integer.parseInt(waitTimeField[CMTOINT]
                            .getText());
                    sendNMessages(numMessages, waitTime, CMTOINT);
                }
            });
            break;
        case INTTOCM:
            theButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int numMessages = Integer.parseInt(numMessageField[INTTOCM]
                            .getText());
                    int waitTime = Integer.parseInt(waitTimeField[INTTOCM]
                            .getText());
                    sendNMessages(numMessages, waitTime, INTTOCM);
                }
            });
            break;
        }

        return theButton;
    }

    private Button createSendAllMessageButton(int which) {
        Button theButton;
        theButton = new Button("Send All");
        switch (which) {
        case CMTOINT:
            theButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    synchronized (messageSync) {
                        int numMessages = outgoingMessageQueue[CMTOINT].size();
                        sendNMessages(numMessages, 0, CMTOINT);
                    }
                }
            });
            break;
        case INTTOCM:
            theButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    synchronized (messageSync) {
                        int numMessages = outgoingMessageQueue[INTTOCM].size()
                                + fileMessageQueue[INTTOCM].size();
                        sendNMessages(numMessages, 0, INTTOCM);
                    }
                }
            });
            break;
        }

        return theButton;
    }

    /* creates a checkbox that will toggle autoSend */
    private Checkbox createAutoSendBox(int which) {
        Checkbox c = null;
        switch (which) {
        case INTTOCM:
            c = new Checkbox("<--- auto send");
            c.setState(autoSend[which]);
            c.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    autoSend[INTTOCM] = !autoSend[INTTOCM];
                }
            });
            break;
        case CMTOINT:
            c = new Checkbox("auto send --->");
            c.setState(autoSend[which]);
            c.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    autoSend[CMTOINT] = !autoSend[CMTOINT];
                }
            });
            break;
        }

        return c;
    }

    /* creates a checkbox that will toggle mesNumMismatch */
    private Checkbox createMesNumMismatchBox() {
        Checkbox c = null;
        c = new Checkbox("verify message numbers");
        c.setState(mesNumMismatch);
        c.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                mesNumMismatch = !mesNumMismatch;
            }
        });

        return c;
    }

    private Button createReorderButton(int which) {
        Button theButton;
        theButton = new Button("Reorder Expected to match Received");
        switch (which) {
        case CMTOINT:
            theButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    synchronized (messageSync) {
                        int size = fileMessageQueue[CMTOINT].size();
                        if (size > outgoingMessageQueue[CMTOINT].size()) {
                            size = outgoingMessageQueue[CMTOINT].size();
                        }
                        reorderToMatch(fileMessageQueue[CMTOINT], size,
                                outgoingMessageQueue[CMTOINT], size);
                        /*
                         * reorderToMatch(fileMessageQueue[CMTOINT],
                         * fileMessageQueue[CMTOINT].size(),
                         * outgoingMessageQueue[CMTOINT],
                         * outgoingMessageQueue[CMTOINT].size());
                         */
                        fileMessageQueue[CMTOINT].refreshDisplay();
                    }
                }
            });
            break;
        case INTTOCM:
            theButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    synchronized (messageSync) {
                        int size = fileMessageQueue[INTTOCM].size();
                        if (size > outgoingMessageQueue[INTTOCM].size()) {
                            size = outgoingMessageQueue[INTTOCM].size();
                        }
                        reorderToMatch(fileMessageQueue[INTTOCM], size,
                                outgoingMessageQueue[INTTOCM], size);
                        /*
                         * reorderToMatch(fileMessageQueue[INTTOCM],
                         * fileMessageQueue[INTTOCM].size(),
                         * outgoingMessageQueue[INTTOCM],
                         * outgoingMessageQueue[INTTOCM].size());
                         */
                        fileMessageQueue[INTTOCM].refreshDisplay();
                    }
                }
            });
            break;
        }

        return theButton;
    }

    private Button createMoreLessButton() {
        Button theButton;
        if (details) {
            theButton = new Button("<< Hide Details <<");
        } else {
            theButton = new Button(">> Show Details >>");
        }
        theButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (details) {
                    details = false;
                    ((Button) e.getSource()).setLabel(">> Show Details >>");
                    updateLayout();
                } else {
                    details = true;
                    ((Button) e.getSource()).setLabel("<< Hide Details <<");
                    updateLayout();
                }
            }
        });
        return theButton;
    }

    private Button createWriteQueueButton() {
        Button theButton;
        theButton = new Button("Write");
        theButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileDialog outfileDlog = new FileDialog((Frame) getParent());
                outfileDlog.setVisible(true);
                String filename = outfileDlog.getFile();
                if (filename != null) {
                    writeMessageQueue(outfileDlog.getDirectory(), filename);
                }
            }
        });
        return theButton;
    }

    private Button createReadQueueButton() {
        Button theButton;
        theButton = new Button("Read");
        theButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileDialog infileDlog = new FileDialog((Frame) getParent());
                infileDlog.setVisible(true);
                String filename = infileDlog.getFile();
                if (filename != null) {
                    readMessageQueue(infileDlog.getDirectory(), filename);
                }
            }
        });
        return theButton;
    }

    /*
     * used by readMessageQueue to add messages to the Queues and display boxes.
     * Any existing messages are thrown away.
     */
    private void setOutgoingMessages(Vector messageList, int which) {
        synchronized (messageSync) {
            fileMessageQueue[which].removeAllElements();
            edu.cmu.old_pact.java.util.Queue q = fileMessageQueue[which];

            for (int i = 0; i < messageList.size(); ++i) {
                q.push(messageList.elementAt(i));
            }
        }
    }

    /*
     * reads messages from a file, divides them up by direction, and calls
     * setOutgoingMessages for each set
     */
    private void readMessageQueue(String directory, String filename) {
        try {
            String line = null;
            BufferedReader instream = new BufferedReader(new FileReader(
                    new File(directory, filename)));
            try {
                Vector messages[] = { new Vector(), new Vector() };
                StringBuffer text = new StringBuffer();
                while ((line = instream.readLine()) != null) {
                    if (line.indexOf(dorminHead) != -1) {
                        if (line.indexOf(delimiter[CMTOINT]) != -1) {
                            messages[CMTOINT].addElement(line.substring(line
                                    .indexOf(dorminHead)));
                        } else if (line.indexOf(delimiter[INTTOCM]) != -1) {
                            messages[INTTOCM].addElement(line.substring(line
                                    .indexOf(dorminHead)));
                        }
                    }
                    /*
                     * if(!line.startsWith(delimeter[CMTOINT])) line =
                     * "\n"+line; text.append(line);
                     */
                }

                setOutgoingMessages(messages[CMTOINT], CMTOINT);
                setOutgoingMessages(messages[INTTOCM], INTTOCM);
            } catch (EOFException ex) {
                System.out.println("ControlledLispInterface readMessageQueue "
                        + ex.toString());
            } catch (IOException ex) {
                System.out.println("ControlledLispInterface readMessageQueue "
                        + ex.toString());
            }
        } catch (FileNotFoundException ex) {
            System.out
                    .println("ControlledLispInterface readMessageQueue: Can't find file: "
                            + directory + filename);
        }
    }

    /* writes messages from the messageLog queue into the specified file */
    private void writeMessageQueue(String directory, String filename) {
        if (messageLog.empty()) {
            System.out.println("writeMessageQueue: nothing to write");
            return;
        }

        try {
            BufferedWriter outstream = new BufferedWriter(new FileWriter(
                    new File(directory, filename)));
            for (LoggedMessage msg = (LoggedMessage) messageLog.pop(); !messageLog
                    .empty(); msg = (LoggedMessage) messageLog.pop()) {
                outstream.write(msg.toString(), 0, msg.toString().length());
                outstream.newLine();
            }

            outstream.flush();
            outstream.close();
        } catch (FileNotFoundException ex) {
            System.out
                    .println("ControlledLispInterface readMessageQueue: Can't find file: "
                            + directory + filename);
        } catch (IOException ex) {
            System.out.println("ControlledLispInterface readMessageQueue "
                    + ex.toString());
        }
    }
}

/*
 * used internally to store the log of messages, so that they can be written out
 * in the order that they were sent.
 */

class LoggedMessage {
    private int which;

    private String text;

    public LoggedMessage(int dir, String msg) {
        which = dir;
        text = msg;
    }

    public String toString() {
        return ControlledLispInterface.delimiter[which] + text;
    }
}
