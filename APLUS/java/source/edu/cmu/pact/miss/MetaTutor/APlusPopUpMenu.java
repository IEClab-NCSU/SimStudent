package edu.cmu.pact.miss.MetaTutor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import edu.cmu.pact.Utilities.trace;

/**
 *
 */
public class APlusPopUpMenu extends JPopupMenu implements ActionListener {

	/**	 */
	private JMenuItem menuItem;

	/**	 */
	private Component component;
	
	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	/**
	 * 
	 */
	public APlusPopUpMenu(){
		super();
		setUpLookAndFeel();
	}

	/**
	 * @param component
	 */
	public APlusPopUpMenu(Component component) {
		super();
		this.component = component;
		setUpLookAndFeel();
	}

	/**
	 * 
	 */
	private void setUpLookAndFeel() {

		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		setBackground(Color.lightGray);
	}

	public void createYesNoMenu(final Component comp,final ArrayList<ArrayList<String>> menuList){
		
		clear();
		JLabel message = new JLabel("Do you need my help or would like to try on your own?");
		message.setBackground(Color.lightGray);
		message.setFont(new Font("Monospace", Font.PLAIN, 14));
		JPanel buttonPanel = new JPanel();
		JButton yesButton = new JButton("Yes");
		JButton noButton = new JButton("No");
		buttonPanel.setLayout(new FlowLayout());
		yesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addMenuItem(comp,menuList);
			}
		});
		noButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		buttonPanel.add(yesButton);
		buttonPanel.add(noButton);
		this.add(message);
		this.add(buttonPanel);
		//this.show(comp,-(((int)this.getPreferredSize().getWidth()) - comp.getWidth()) , -((int)this.getPreferredSize().getHeight()));
	}
	
	/**
	 * 
	 */
	public void addMenuItem(Component comp, ArrayList<ArrayList<String>> menuList) {

		clear();
		if(menuList.isEmpty()) {
			trace.err("Nothing to be added to the PopUpMenu");
			return;
		}
		
		JLabel label = new JLabel("  Ask Mr. Williams");
		label.setBackground(Color.lightGray);
		label.setFont(new Font("Monospace", Font.BOLD, 14));
		add(label);
		add(new JSeparator());

		for(int i=0; i < menuList.size(); i++) {
			menuItem = new JMenuItem();
			menuItem.setHorizontalAlignment(SwingConstants.LEFT);
			menuItem.setActionCommand((menuList.get(i)).get(0).trim());
			menuItem.setText((menuList.get(i)).get(1).trim());
			menuItem.addActionListener(this);
			add(menuItem);
		}
		this.show(comp,-(((int)this.getPreferredSize().getWidth()) - comp.getWidth()) , -((int)this.getPreferredSize().getHeight()));
	}
	
	private void clear(){
		
		int count = this.getComponentCount();
		this.removeAll();
	}
	
	/**
	 * 
	 */
	public void actionPerformed(ActionEvent ae) {

		if(ae.getActionCommand().equalsIgnoreCase("CognitiveHint")) {
			((MetaTutorAvatarComponent)getComponent()).getSimStudent().getBrController().getAmt().handleInterfaceAction("hint", "MetaTutorClicked", "-1");
		} else if(ae.getActionCommand().equalsIgnoreCase("ContextHint")) {
			((MetaTutorAvatarComponent)getComponent()).getSimStudent().getBrController().getAmt().handleInterfaceAction("CLHint", "MetaTutorClicked", "-1");
		}
	}
}
