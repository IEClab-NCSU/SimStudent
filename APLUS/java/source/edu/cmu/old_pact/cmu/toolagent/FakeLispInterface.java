//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/FakeLispInterface.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.Vector;

import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;
import edu.cmu.pact.Utilities.trace;

public class FakeLispInterface extends Dialog {
	private TextArea messageQueueArea;
	private TextArea messagesSent;
	private TextArea messagesReceived;
	private Stack outgoingMessageStack;
	private LispJavaConnection jlc;
	private final TextField numMessageField;
	private final TextField waitTimeField;
	private Label messagesLeftLabel = new Label();
	private String delimeter = "SE/1.2";

	public FakeLispInterface(LispJavaConnection channel, Frame parent) {
		super(parent);
		setTitle("Message Interface");
		jlc = channel;
		Font areaFont = new Font("helvetica", Font.PLAIN, 9);
		messageQueueArea = new TextArea(10,30);
		messageQueueArea.setFont(areaFont);
		messagesSent = new TextArea(10,30);
		messagesSent.setFont(areaFont);
		messagesReceived = new TextArea(10,30);
		messagesReceived.setFont(areaFont);
		numMessageField = new TextField("1",3);
		waitTimeField = new TextField("0",5);
		outgoingMessageStack = new Stack();
		
		Panel messagePanel = new Panel();
		messagePanel.setLayout(new GridBagLayout());
		Panel queuePanel = new Panel();
		queuePanel.add(new Label("Message Queue"));
		queuePanel.add(createReadQueueButton());
		GridbagCon.viewset(messagePanel,queuePanel, 0, 0, 1, 1, 5, 5, 0, 5);
		GridbagCon.viewset(messagePanel,new Label("Messages Sent"), 1, 0, 1, 1, 5, 5, 0, 5);
		GridbagCon.viewset(messagePanel,new Label("Messages Received"), 2, 0, 1, 1, 5, 5, 0, 5); 
		GridbagCon.viewset(messagePanel,messageQueueArea, 0, 1, 1, 1, 5, 5, 0, 5);
		GridbagCon.viewset(messagePanel,messagesSent, 1, 1, 1, 1, 5, 5, 0, 5);
		GridbagCon.viewset(messagePanel,messagesReceived, 2, 1, 1, 1, 5, 5, 0, 5);
		
		Panel bottomPanel = new Panel();
		bottomPanel.setLayout(new GridBagLayout());
		GridbagCon.viewset(bottomPanel, new Label("Send", 0), 0, 0, 1, 1, 5, 5, 0, 0);
		GridbagCon.viewset(bottomPanel, numMessageField, 1, 0, 1, 1, 5, 0, 0, 0);
		GridbagCon.viewset(bottomPanel, new Label("messages", 0), 2, 0, 1, 1, 1, 5, 0, 0);
		GridbagCon.viewset(bottomPanel, new Label("Wait", 0), 0, 1, 1, 1, 1, 5, 0, 0);
		GridbagCon.viewset(bottomPanel, waitTimeField, 1, 1, 1, 1, 1, 0, 0, 0);
		GridbagCon.viewset(bottomPanel, createSendMessageButton(), 0, 2, 1, 1, 5, 0, 10, 0);
		GridbagCon.viewset(bottomPanel, createSendAllMessageButton(), 1, 2, 1, 1, 5, 0, 10, 0);
		GridbagCon.viewset(bottomPanel, messagesLeftLabel, 0, 3, 2, 1, 5, 0, 10, 0);
		
		setLayout(new GridBagLayout());
		GridbagCon.viewset(this, messagePanel, 0, 0, 1, 1, 0, 0, 0, 0);
		GridbagCon.viewset(this, bottomPanel, 0, 1, 1, 1, 0, 0, 0, 0);
		
		pack();
		setLocation(20, 50);
	}
	
	private void setOutgoingMessages(Vector messageList) {
		String[] array = new String[messageList.size()];
		for (int i=0;i<messageList.size();++i)
			array[i] = (String)(messageList.elementAt(i));
		setOutgoingMessages(array);
	}
	
	public void setOutgoingMessages(String[] messageList) {
	
		StringBuffer tempBuff = new StringBuffer();
		for (int i=0;i<messageList.length;++i) {
			tempBuff.append(messageList[i]+'\n');
			outgoingMessageStack.push(messageList[(messageList.length)-i-1]);
		}
		messageQueueArea.setText(tempBuff.toString());
		messagesLeftLabel.setText("("+messageList.length+" messages left)");
		messagesLeftLabel.setSize(messagesLeftLabel.getPreferredSize());
	}
	
	public void receiveMessage(String message) {
		messagesReceived.append(message+'\n');
	}
	
	private String popNextMessage() {
		String thisMessage = (String)(outgoingMessageStack.pop());
		//remove the text from the display
		StringBuffer tempBuff = new StringBuffer();
		int s = outgoingMessageStack.size();
		for(int i=1; i<=s; i++)
			tempBuff.append((String)outgoingMessageStack.elementAt(s-i)+'\n');
		messageQueueArea.setText(tempBuff.toString());
		
		messagesLeftLabel.setText("("+outgoingMessageStack.size()+" messages left)");
		return thisMessage;
	}
	
	public void sendNMessages(int number,int wait) {
		try{
		for (int i=0;i<number;++i) {
			String thisMessage = popNextMessage();
			//thisMessage = thisMessage.substring(0, thisMessage.length()-1);
			//handle the message
			jlc.messageReceived(thisMessage);
			messagesSent.append(thisMessage+'\n');
			if (wait > 0) {
				try {
					Thread.sleep(wait);
				}
				catch (InterruptedException e) {
					trace.out("FakeLispInterface sendNMessages "+e.toString());
				}
			}
		}
		} catch (EmptyStackException ex) { 
			trace.out("FakeLispInterface sendNMessages "+ex.toString());
		}
	}
	
	public void showSendMessage(String theMessage){
		messagesSent.append(theMessage+'\n');
	}
	
	private Button createSendMessageButton() {
		Button theButton;
		theButton = new Button("Send");
		theButton.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											int numMessages = Integer.parseInt(numMessageField.getText());
											int waitTime = Integer.parseInt(waitTimeField.getText());
											sendNMessages(numMessages,waitTime);
										}
									});
		return theButton;
	}

	private Button createSendAllMessageButton() {
		Button theButton;
		theButton = new Button("Send All");
		theButton.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											int numMessages = outgoingMessageStack.size();
											sendNMessages(numMessages,0);
										}
									});
		return theButton;
	}
	
	private Button createReadQueueButton() {
		Button theButton;
		theButton = new Button("Read");
		theButton.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											FileDialog infileDlog = new FileDialog((Frame)getParent());
											infileDlog.setVisible(true);
											String filename = infileDlog.getFile();
											if (filename != null) {
												outgoingMessageStack = new Stack();
												messageQueueArea.setText("");
												messagesSent.setText("");
												readMessageQueue(infileDlog.getDirectory(),filename);
											}
										}
									});
		return theButton;
	}
	
	private void readMessageQueue(String directory, String filename) {
		try {
			String line = null;
			BufferedReader instream = new BufferedReader(new FileReader(new File(directory,filename)));
			try {
				boolean linesToGo=true;
				Vector messages = new Vector();
				StringBuffer text = new StringBuffer();
				while((line = instream.readLine()) != null) {
					if(!line.startsWith(delimeter))
						line = "\n"+line;
					text.append(line);
				}
				NewStringTokenizer st = new NewStringTokenizer(text.toString(), delimeter, true);
				String curr;
				while(st.hasMoreElements()) {
					curr = (String)st.nextToken();
					messages.addElement((String)curr);
				}
				
				setOutgoingMessages(messages);

			}
			catch (EOFException ex) {
				System.out.println("FakeLispInterface readMessageQueue "+ex.toString());
			}
			catch (IOException ex) {
				System.out.println("FakeLispInterface readMessageQueue "+ex.toString());
			}
		}
		catch (FileNotFoundException ex) {
			System.out.println("FakeLispInterface readMessageQueue: Can't find file: "+directory+filename);
		}
	}
}
	
