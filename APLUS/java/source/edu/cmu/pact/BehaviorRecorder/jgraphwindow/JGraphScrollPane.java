package edu.cmu.pact.BehaviorRecorder.jgraphwindow;

import java.awt.Dimension;

import javax.swing.JScrollPane;

import org.jgraph.JGraph;

public class JGraphScrollPane extends JScrollPane {

	private static final long serialVersionUID = 1L;

	public JGraphScrollPane(JGraph graph) {

		super(graph);

		this.setPreferredSize(new Dimension(600, 400));
		this.setMinimumSize(new Dimension(10, 10));
	}

}