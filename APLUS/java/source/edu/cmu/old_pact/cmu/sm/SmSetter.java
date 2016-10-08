package edu.cmu.old_pact.cmu.sm;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import edu.cmu.old_pact.cmu.uiwidgets.StackLayout;

//This file provides a dialog which allows the user to set various properties of
//a SymbolManipulator

public class SmSetter extends Frame {
	//private Panel calcPanel;
	private Panel standardPanel;
	private Panel simplifyPanel;
	private Panel optionsPanel;
	private Panel mainPanel;
	private static SymbolManipulator sm;

	public SmSetter(SymbolManipulator symbolGuy) {
		super("Set calculation parameters");
		sm = symbolGuy;
		initialize();
	}
	
	public void initialize() {
		standardPanel = createStandardizePanel();
		simplifyPanel = createSimplifyPanel();
		optionsPanel = createOptionsPanel();
		/*if (SolverFrame.getSelf().getTypeInMode()) {
		  disableContents(standardPanel);
		  disableContents(simplifyPanel);
		  disableContents(optionsPanel);
		  }
		  else*/ if (sm.autoStandardize || sm.autoSimplify){
			disableContents(optionsPanel);
			if (sm.autoStandardize){
				disableContents(simplifyPanel);
			}
		}
		mainPanel = new Panel();
		mainPanel.setLayout(new StackLayout(2));
		mainPanel.add(standardPanel);
		mainPanel.add(simplifyPanel);
		mainPanel.add(optionsPanel);
		mainPanel.add(createOKButton());
		mainPanel.resize(400,200);
		add(mainPanel);
		pack();
		setLocation(50,50);
	}
	
	private static void disableContents(Panel thePanel) {
		Component comps[] = thePanel.getComponents();
		for (int i=0;i<comps.length;++i) {
			if(comps[i] instanceof Checkbox){
				//((Checkbox)comps[i]).setState(false);
				comps[i].setEnabled(false);
			}
		}
		thePanel.repaint();
	}

	private static void enableContents(Panel thePanel) {
		Component comps[] = thePanel.getComponents();
		for (int i=0;i<comps.length;++i) {
			if(comps[i] instanceof Checkbox)
				comps[i].setEnabled(true);
		}
		thePanel.repaint();
	}

	private Panel createStandardizePanel() {
		Panel standPanel = new Panel();
		Checkbox standard = new Checkbox("Standardize",sm.autoStandardize);
		standard.addItemListener (new ItemListener() {
										public void itemStateChanged(ItemEvent e) {
											if (e.getStateChange() == ItemEvent.SELECTED) {
												sm.autoStandardize = true;
												disableContents(optionsPanel);
												disableContents(simplifyPanel);
											}
											else {
												sm.autoStandardize = false;
												enableContents(simplifyPanel);
												if(!sm.autoSimplify){
												   enableContents(optionsPanel);
												}
											}
										}
									});
		standPanel.add(standard);

		Checkbox sort = new Checkbox("Sort",sm.autoSort);
		sort.addItemListener (new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						sm.autoSort = true;
					}
					else {
						sm.autoSort = false;
					}
				}
			});
		standPanel.add(sort);
		return standPanel;
	}
										
										
	private Panel createSimplifyPanel() {
		Panel simpPanel = new Panel();
		Checkbox simp = new Checkbox("Simplify",sm.autoSimplify);
		simp.addItemListener (new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						sm.autoSimplify = true;
						disableContents(optionsPanel);
					}
					else {
						sm.autoSimplify = false;
						enableContents(optionsPanel);
					}
				}
			});
		simpPanel.add(simp);

		Checkbox d = new Checkbox("Distribute",sm.autoDistribute);
		d.addItemListener (new ItemListener() {
									public void itemStateChanged(ItemEvent e) {
										if (e.getStateChange() == ItemEvent.SELECTED) {
											sm.autoDistribute = true;
										}
										else {
											sm.autoDistribute = false;
										}
									}
								});
		simpPanel.add(d);								

		return simpPanel;
	}
										
										
	private Panel createOptionsPanel() {
		Panel optionPanel = new Panel(new GridLayout(4,2));

		Checkbox clt = new Checkbox("Combine Like Terms",sm.autoCombineLikeTerms);
		clt.addItemListener (new ItemListener() {
									public void itemStateChanged(ItemEvent e) {
										if (e.getStateChange() == ItemEvent.SELECTED) {
											sm.autoCombineLikeTerms = true;
										}
										else {
											sm.autoCombineLikeTerms = false;
										}
									}
								});
		optionPanel.add(clt);								
								
		Checkbox rf = new Checkbox("Reduce Fractions",sm.autoReduceFractions);
		rf.addItemListener (new ItemListener() {
									public void itemStateChanged(ItemEvent e) {
										if (e.getStateChange() == ItemEvent.SELECTED) {
											sm.autoReduceFractions = true;
										}
										else {
											sm.autoReduceFractions = false;
										}
									}
								});
		optionPanel.add(rf);								

		Checkbox mt = new Checkbox("Perform Multiplication",sm.autoMultiplyThrough);
		mt.addItemListener (new ItemListener() {
									public void itemStateChanged(ItemEvent e) {
										if (e.getStateChange() == ItemEvent.SELECTED) {
											sm.autoMultiplyThrough = true;
										}
										else {
											sm.autoMultiplyThrough = false;
										}
									}
								});
		optionPanel.add(mt);								

		Checkbox ee = new Checkbox("Expand Exponents",sm.autoExpandExponent);
		ee.addItemListener (new ItemListener() {
									public void itemStateChanged(ItemEvent e) {
										if (e.getStateChange() == ItemEvent.SELECTED) {
											sm.autoExpandExponent = true;
										}
										else {
											sm.autoExpandExponent = false;
										}
									}
								});
		optionPanel.add(ee);								

		Checkbox dd = new Checkbox("Distribute Division",sm.distributeDenominator);
		dd.addItemListener (new ItemListener() {
									public void itemStateChanged(ItemEvent e) {
										if (e.getStateChange() == ItemEvent.SELECTED) {
											sm.distributeDenominator = true;
										}
										else {
											sm.distributeDenominator = false;
										}
									}
								});
		optionPanel.add(dd);								

		Checkbox rds = new Checkbox("Remove Double Signs",!sm.allowDoubleSigns);
		rds.addItemListener (new ItemListener() {
									public void itemStateChanged(ItemEvent e) {
										if (e.getStateChange() == ItemEvent.SELECTED) {
											sm.allowDoubleSigns = false;
										}
										else {
											sm.allowDoubleSigns = true;
										}
									}
								});
		optionPanel.add(rds);								

		Checkbox rep = new Checkbox("Remove Extra Parentheses",!sm.allowExtraParens);
		rep.addItemListener (new ItemListener() {
									public void itemStateChanged(ItemEvent e) {
										if (e.getStateChange() == ItemEvent.SELECTED) {
											sm.allowExtraParens = false;
										}
										else {
											sm.allowExtraParens = true;
										}
									}
								});
		optionPanel.add(rep);
		return optionPanel;								
	}
	
	public Panel createOKButton() {
		Panel OKPanel = new Panel();
		Button okButton = new Button("OK");
		okButton.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											hide();
											dispose();
										}
									});
		OKPanel.setLayout(new FlowLayout(1));
		OKPanel.add(okButton);
		return OKPanel;
	}

}
