package edu.cmu.pact.miss.PeerLearning.GameShow;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Connection {

	Socket socket;
	PrintWriter writer;
	BufferedReader reader;
	String name = "New";
	String img = "None";
	String userid = "default";
	ContestServer.ListenerThread serverListener;
	ContestOrganizer.ListenerThread contestListener;
	boolean valid = false;
	boolean outstandingChallenge = false;
	
	Connection(Socket sock,PrintWriter write, BufferedReader read)
	{
		socket = sock;
		writer = write;
		reader = read;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setImage(String img)
	{
		this.img = img;
	}
	
	public void setUserID(String id)
	{
		userid = id;
	}
	
	public String getUserID()
	{
		return userid;
	}
	
	public void setOutstandingChallenge(boolean outstanding)
	{
		outstandingChallenge = outstanding;
	}
	
	public boolean hasOutstandingChallenge()
	{
		return outstandingChallenge;
	}
	
	
	
}