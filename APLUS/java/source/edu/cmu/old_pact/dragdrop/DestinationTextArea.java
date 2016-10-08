package edu.cmu.old_pact.dragdrop;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.TextArea;

import edu.cmu.old_pact.objectregistry.ObjectRegistry;


public class DestinationTextArea extends TextArea implements DragDestination{
	private int width = 250;
	private int height = 150;
	
	public DestinationTextArea(){
		super();
		setBackground(Color.white);
		register();
	}
	
	public void register(){
		ObjectRegistry.registerObject("DragDestination", this);
	}
	
	public Dimension preferredSize(){
		return new Dimension(width, height);
	}
	
	
	public Frame getFrame(){
		Component parent = getParent();
		Component root = null;
		while(parent != null){
			root = parent;
			parent = parent.getParent();
		}
		return (Frame)root;
	}
	
	public Component getDestination(){
		return this;
	}
	
	public void dragEntered(DragSession session){
	}
	
	public void dragMoved(DragSession session){
	}
	
	public void dragExited(DragSession session){
	}
	
	public boolean dragDropped(DragSession session){
		Object data = session.getDraggedData();
		if(data != null)
			appendText((String)data);
		return true;
	}	
}
	