package edu.cmu.pact.miss.storage;

import java.awt.Color;
import java.io.IOException;

import javax.swing.JOptionPane;


public class ConcurrencyTest implements Runnable {
	
		StorageAccess client;
		String user;
	

		public ConcurrencyTest(String name)
		{
			client = new StorageClient("http://mocha.pslc.cs.cmu.edu");
			user = name;
		}
	
	    public void run() 
	    {
	    	int count = 0;
	    	while(count < 100000)
	    	{
		    	try {
					Thread.sleep((long) (/*1+*/(Math.random()*2)));
					if(Math.random() < .5)
					{
						client.storeFile(user, user+".txt");
					}
					else
					{
						System.out.println(client.retrieveFile(user, user+".txt"));
					}
					count++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
				}
	    	}
	    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {

    	new Thread(new ConcurrencyTest("name1")).start(); 
    	new Thread(new ConcurrencyTest("name2")).start(); 
    	new Thread(new ConcurrencyTest("name3")).start(); 
    	new Thread(new ConcurrencyTest("name4")).start(); 
    	new Thread(new ConcurrencyTest("name5")).start(); 
    	new Thread(new ConcurrencyTest("name6")).start(); 

	}

}
