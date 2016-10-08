package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SelectionDialog {
	
	private static final String DEFAULT_TITLE = "Make your selection...";
	private static final Boolean DEFAULT_VALUE = false;
	
	public static <T> Map<T, Boolean> doElementSelection(
			String message, 
			List<T> elements,
			String title,
			Boolean defaultSelectionValue)
	{
		List<String> strings = new ArrayList<String>();
		for (T e : elements)
		{
			strings.add(e.toString());
		}
		return doElementSelection(message, zip(elements, strings), title, defaultSelectionValue);
	}
	
	public static <T> Map<T, Boolean> doElementSelection(
			String message,
			Map<T, String> elementLabelMap,
			String title,
			Boolean defaultSelectionValue)
	{
		String diagTitle = title != null ? title : DEFAULT_TITLE;
		Boolean defaultValue = defaultSelectionValue != null ? defaultSelectionValue : DEFAULT_VALUE;
		
		Product diagUI = buildDiag(message, elementLabelMap, defaultValue);
		JPanel msg = diagUI.getPanel();
		@SuppressWarnings("unchecked")
		Map<JCheckBox, T> boxMap = diagUI.getBoxMap();
		
		
		int response = JOptionPane.showConfirmDialog(null, message, diagTitle, JOptionPane.OK_CANCEL_OPTION);
		if (response == JOptionPane.OK_OPTION)
		{
			Map<T, Boolean> selections = new HashMap<T, Boolean>();
			for (JCheckBox box : boxMap.keySet())
			{
				selections.put(boxMap.get(box), box.isSelected());
			}
			return selections;
		}
		else
		{
			return null;
		}
	}
	
	private static <T> Product buildDiag(String message, Map<T, String> elements, boolean defaultSelectionValue)
	{
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		
		JPanel messagePanel = new JPanel(new FlowLayout());
		messagePanel.add(new JLabel(message));
		
		JPanel selectionPanel = new JPanel();
		selectionPanel.setBackground(Color.white);
		selectionPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
		// create all the checkboxes and find the max width for layout purposes
		Map<JCheckBox, T> boxMap = new HashMap<JCheckBox, T>();
		int maxWidth = 0;
		for (Entry<T,String> ent : elements.entrySet())
		{
			JCheckBox newBox = new JCheckBox(ent.getValue(), defaultSelectionValue);
			newBox.setHorizontalAlignment(SwingConstants.LEFT);
			boxMap.put(newBox, ent.getKey());
			if (newBox.getWidth() > maxWidth)
			{
				maxWidth = newBox.getWidth();
			}
		}
		// now loop through all the checkboxes and fill in filler space so
		// they all have uniform width
		for (JCheckBox box : boxMap.keySet())
		{
			int deficit = maxWidth - box.getWidth();
			Component filler = Box.createRigidArea(new Dimension(deficit, box.getHeight()));
			JPanel boxPanel = new JPanel(new FlowLayout());
			boxPanel.add(box);
			boxPanel.add(filler);
			selectionPanel.add(boxPanel);
		}
		
		
		topPanel.add(messagePanel);
		topPanel.add(selectionPanel);
		
		
		return new SelectionDialog.Product<T>(topPanel, boxMap);
		
	}
	
	private static List<Integer> xrange(int numElements)
	{
		List<Integer> l = new ArrayList<Integer>();
		for (int i = 0; i < numElements; i++)
		{
			l.add(i);
		}
		return l;
	}
	
	private static <K, V> Map<K, V> zip(List<K> keys, List<V> values)
	{
		if ((keys == null || values == null) || (keys.size() != values.size())) return null;
		Map<K, V> map = new HashMap<K, V>();
		for (Integer i : xrange(keys.size()))
		{
			map.put(keys.get(i), values.get(i));
		}
		return map;
	}
	
	private static class Product<T>
	{
		private final JPanel panel;
		private final Map<JCheckBox, T> boxMap;
		public Product(JPanel panel, Map<JCheckBox, T> boxMap)//List<JCheckBox> boxes)
		{
			this.panel = panel;
			this.boxMap = boxMap;
		}
		
		public JPanel getPanel()
		{
			return this.panel;
		}
		
		public Map<JCheckBox, T> getBoxMap()
		{
			return this.boxMap;
		}
	}
}