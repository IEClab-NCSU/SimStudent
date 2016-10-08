package edu.cmu.hcii.ctat.wizard;

import java.util.ArrayList;

import javax.swing.JPanel;

import edu.cmu.hcii.ctat.CTATBase;

/** 
 *
 */
public class CTATWizardPanelDescription extends CTATBase
{	
	private String panelLabel="Undefined";
	private ArrayList <JPanel> alternatives=new ArrayList<JPanel> ();
	private int panelIndex=0;
	
	/**
	 * 
	 */
	public String getPanelLabel() 
	{
		return panelLabel;
	}
	/**
	 * 
	 */	
	public void setPanelLabel(String panelLabel) 
	{
		this.panelLabel = panelLabel;
	}
	/**
	 * 
	 */	
	public JPanel getPanelContent() 
	{
		debug ("getPanelContent ("+alternatives.size()+"->"+panelIndex+")");
		
		return alternatives.get(panelIndex);
	}
	/**
	 * 
	 */	
	public void setPanelContent(JPanel panelContent) 
	{
		alternatives.add(panelContent);		
	}
	/**
	 * 
	 */
	public ArrayList<JPanel> getAlternatives ()
	{
		return (alternatives);
	}
	/**
	 * 
	 */
	public int getPanelIndex() 
	{
		return panelIndex;
	}
	/**
	 * 
	 * @param panelIndex
	 */
	public void setPanelIndex(int panelIndex) 
	{
		this.panelIndex = panelIndex;
	}
}
