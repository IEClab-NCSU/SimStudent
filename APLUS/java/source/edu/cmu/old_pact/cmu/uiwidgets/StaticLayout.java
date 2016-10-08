/*
 * @(#)StaticLayout.java
 */

package edu.cmu.old_pact.cmu.uiwidgets;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class StaticLayout implements LayoutManager	{

    public void 
    addLayoutComponent(
    	String name, 
    	Component comp)
    {
    
    }
    
    public void 
    removeLayoutComponent(
    	Component comp)
	{
	}
	
    public Dimension 
    preferredLayoutSize(
    	Container parent)
    {
    	return minimumLayoutSize(parent);
    }
    
   	public Dimension 
    minimumLayoutSize(
    	Container parent)
    {
    	return parent.size();
    }
    
    public void 
    layoutContainer(
    	Container parent)
    {
    }
    
}
