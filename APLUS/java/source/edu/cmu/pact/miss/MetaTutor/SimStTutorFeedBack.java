package edu.cmu.pact.miss.MetaTutor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import pact.CommWidgets.JCommTable.TableCell;


import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;

public class SimStTutorFeedBack implements Runnable {

	private static final long serialVersionUID = 1L;

	/**	 */
	private SimSt simSt;
	
	public SimSt getSimSt() {
		return simSt;
	}

	public void setSimSt(SimSt simSt) {
		this.simSt = simSt;
	}

	private Component highlightedWidget;
	
	public Component getHighlightedWidget() {
		return highlightedWidget;
	}

	public void setHighlightedWidget(Component highlightedWidget) {
		this.highlightedWidget = highlightedWidget;
	}

	/**	 */
	private Sai sai;
	
	public Sai getSai() {
		return sai;
	}

	public void setSai(Sai sai) {
		this.sai = sai;
	}

	/**	 */
	private BR_Controller brController;
	
	public BR_Controller getBrController() {
		return brController;
	}

	public void setBrController(BR_Controller brController) {
		this.brController = brController;
	}

	public SimStTutorFeedBack(SimSt ss, Component comp) {
		// TODO Auto-generated constructor stub
		simSt = ss;
		brController = simSt.getBrController();
		highlightedWidget = comp;
		sai = simSt.getInputChecker().checkInputHighlightedWidget(comp);
	}
	
	/**
	 * 
	 */
	@Override
	public void run() {

		/* long startTime = Calendar.getInstance().getTimeInMillis();
		ProblemNode currentNode = null, startNode = null, queryNode = null;
		String problemName = null;
		if(brController.getProblemModel() != null && brController.getProblemModel().getProblemGraph() != null){
			currentNode = getBrController().getCurrentNode();
			startNode = getBrController().getProblemModel().getStartNode();
			problemName = startNode.getName();
		}
		
		Vector<ProblemEdge> path = findPathDepthFirst(startNode, currentNode);
		if(path != null) {
			for(int i=0; i< path.size(); i++){
				Sai pathSai = path.get(i).getSai();
				if((pathSai.getS().equalsIgnoreCase(sai.getS())) && (pathSai.getI().equalsIgnoreCase(sai.getI()))){
					ProblemEdge edge = path.get(i);
					queryNode = edge.getSource();
				}
			}
		}
		
		String result = simSt.builtInInquiryClTutor(sai.getS(), sai.getA(), sai.getI(), queryNode, problemName);
		((TableCell)highlightedWidget).setBackground(Color.white);
		((TableCell)highlightedWidget).setHighlighted(false);
		((TableCell)highlightedWidget).removeCaretListener(((TableCell)highlightedWidget).getCaretListeners()[0]);
		
		if(result.equalsIgnoreCase(EdgeData.CLT_ERROR_ACTION)){
	    	Image img = null;
	    	String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+"img/false.png";
	    	URL url = this.getClass().getResource(file);
	    	img = new ImageIcon(url).getImage();
			((TableCell)highlightedWidget).setBorder(new FancyBorder(img));
		} else if(result.equalsIgnoreCase(EdgeData.CORRECT_ACTION)){
	    	Image img = null;
	    	String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+"img/true.png";
	    	URL url = this.getClass().getResource(file);
	    	img = new ImageIcon(url).getImage();
			((TableCell)highlightedWidget).setBorder(new FancyBorder(img));
		}
		((MetaTutorAvatarComponent)simSt.getMissController().getSimStPLE().getSimStPeerTutoringPlatform()
				.getMetaTutorComponent()).getBcf().setCheckStepCorrectness(0); */
	}
	
	/**
	 * 
	 * @param startNode
	 * @param endNode
	 * @return
	 */
	private Vector<ProblemEdge> findPathDepthFirst(ProblemNode startNode, ProblemNode endNode){
	
		if(startNode == null || endNode == null)
			return null;
		
		/* Base case */
		if(startNode == endNode)
			return null;
		
		/* Recursive case */
		ProblemEdge edge = null;
		if((edge = (startNode.isChildNode(endNode))) != null){
			Vector path = new Vector<ProblemEdge>();
			path.add(0, edge);
			return path;
		} else {
			Vector children = startNode.getChildren();
			if(children.isEmpty())
				return null;
			for(int i=0; i < children.size(); i++){
				ProblemNode childNode = (ProblemNode) children.elementAt(i);
				Vector<ProblemEdge> path = findPathDepthFirst(childNode, endNode);
				if(path != null) {
					path.add(0, startNode.isChildNode(childNode));
					return path;
				}
			}
			return null;
		}
	}
	
}
