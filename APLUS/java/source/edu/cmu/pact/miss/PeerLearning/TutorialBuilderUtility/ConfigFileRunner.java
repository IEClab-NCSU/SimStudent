package edu.cmu.pact.miss.PeerLearning.TutorialBuilderUtility;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.aliasi.util.Arrays;

public class ConfigFileRunner implements ComponentListener {

	public enum RETURN_CODE
	{
		SUCCESS, FILE_NOT_FOUND, FILE_WRITTEN_WRONG, IO_ERROR;
	}
	
	public static final String COMMANDS[] = {"HIGHLIGHT"/*, "SHOW", "PLAY", "CLOSE", "TITLE"*/};
	private static final String HIGHLIGHT = "HIGHLIGHT";
	private static final String SHOW = "SHOW";
	private static final String PLAY = "PLAY";
	private static final String CLOSE = "CLOSE";
	private static final String TITLE = "TITLE";
	
	private final static int ELEMENT_INDEX = 0;
	private final static int COLOR_INDEX = 1;

	/**
	 * Tree which contains the GUI to act upon
	 */
	private DOMTree tree;
	
	/**
	 * Path of the config file
	 */
	private String path;
	
	/**
	 * Parse status
	 */
	private boolean parsed = false;
	
	protected ConfigFileRunner(String path, DOMTree tree)
	{
		this.tree = tree;
		this.path = path;
	}
	
	/**
	 * Outputs an error of some type; Changing the output method here will apply to all errors in this class
	 * @param message Error message to output
	 */
	private void error(String message)
	{
		JOptionPane.showMessageDialog(null, message);
	}
	
	
	
	/**
	 * Parses an Aplus tutorial config file (.aptconf or .txt)
	 * @param path Path to the config file
	 * @return Whether or not the parse was successful
	 */
	private void parse(String path)
	{	
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			error("The provided tutorial config file was not found.");
		}
		String line, command;
		String[] arguments;
		
		try{
			while((line = reader.readLine()) != null)
			{
				command = line.split(":")[0];
				arguments = line.split("\\|");
				arguments[0] = arguments[0].split(":")[1];
								
				if(command.equals(HIGHLIGHT))
				{
					highlight(arguments);
					//FIXME Add something that will make this delay a certain amount of seconds while not blocking GUI drawing
					JOptionPane.showMessageDialog(null, "Press ok to continue");
				}
				else if(command.equals(SHOW))
				{
					
				}
				else if(command.equals(PLAY))
				{
					
				}
				else if(command.equals(CLOSE))
				{
					
				}
				else if(command.equals(TITLE))
				{
					
				}
				else {
					reader.close();
					error("The provided tutorial config file was not written in the correct way. Please reference the documentation and look at the file again.\n The line the error occured on was as follows: " + line);
				}
				
			}
			
			reader.close();

		}
		catch(IOException e) {
			e.printStackTrace();
		}		

		parsed = true;
	}
	
	private void highlight(String[] args)
	{
		DOMNode toHighlight = tree.findElementByID(args[ELEMENT_INDEX]);
				
		if(toHighlight == null)
		{
			error("The component that you specified (" + args[ELEMENT_INDEX] + ") isn't valid. Try looking at the tree again.");
			return;
		}
		
		Color color = parseColor(args[COLOR_INDEX]);
		
		GUIUtility.clearAndDraw((JComponent) toHighlight.getContents(), color);
	}
	
	private Color parseColor(String color)
	{
		color = color.toLowerCase();
		
		if(color.equals("red"))
			return Color.red;
		else if(color.equals("blue"))
			return Color.blue;
		else if(color.equals("green"))
			return Color.green;
		else if(color.equals("black"))
			return Color.black;
		else if(color.equals("cyan"))
			return Color.cyan;
		else if(color.equals("darkgray"))
			return Color.darkGray;
		else if(color.equals("gray"))
			return Color.gray;
		else if(color.equals("lightgray"))
			return Color.lightGray;
		else if(color.equals("magenta"))
			return Color.magenta;
		else if(color.equals("orange"))
			return Color.orange;
		else if(color.equals("pink"))
			return Color.pink;
		else if(color.equals("white"))
			return Color.white;
		else if(color.equals("yellow"))
			return Color.yellow;
		else
		{
			error("The color you specified (" + color + ") isn't valid. Look at the documentation for colors you can use.");
			return null;
		}
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		if(!parsed)
			parse(path);
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		if(!parsed)
			parse(path);
		
	}
	
}
