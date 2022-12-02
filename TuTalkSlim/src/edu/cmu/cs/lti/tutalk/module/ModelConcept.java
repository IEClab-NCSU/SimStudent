/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.cs.lti.tutalk.module;

import java.util.Collection;
import java.util.Map;

import basilica2.side.listeners.LightSideMessageAnnotator;
import edu.cmu.cs.lti.tutalk.script.Concept;
//import edu.cmu.side.model.data.DocumentList;
//import edu.cmu.side.model.data.PredictionResult;

/**
 *
 * @author dadamson
 */
public class ModelConcept extends Concept
{
    /**
     * a source of concept analysis/classification
     */
    public interface Predictor
    {
        /**
         *
         * @param instance the turn to classify
         * @return a map of concept-labels to prediction-likelihoods between zero (no match) and 1.0
         */
        public Map<String, Double> getPredictions(String instance);
        /**
         *
         * @return a unique name for this predictor instance
         */
        public String getName();
    }


    private Predictor predictor;
    private String annotatorType;
    // LightSideMessageAnnotator object added by Tasmia to make a call to side model.
    private LightSideMessageAnnotator sideMessageAnnotator;
    private KeyTermAnnotator keyTermAnnotator;
    private DoneStepAnnotator doneStepAnnotator;
    public ModelConcept(String label, Predictor predictor)
    {
        super(label);
        this.predictor = predictor;
       
    }
    // Constructor added by Tasmia to instantiate LightSideMessageAnnotator object.
    // This constructor is called from Scenario.java line 203.
    public ModelConcept(String label, LightSideMessageAnnotator sideMessageAnnotator)
    {
        super(label);
        this.sideMessageAnnotator = sideMessageAnnotator;
        annotatorType = "LightSide";
    }
    // Another Constructor added by Tasmia to instantiate KeyTermContain object.
    // This constructor is called from Scenario.java line 203.
    public ModelConcept(String label, KeyTermAnnotator keyTermAnnotator)
    {
        super(label);
        this.keyTermAnnotator = keyTermAnnotator;
        annotatorType = "KeyTerm";
    }
    public ModelConcept(String label, LightSideMessageAnnotator sideMessageAnnotator,  KeyTermAnnotator keyTermAnnotator)
    {
        super(label);
        this.keyTermAnnotator = keyTermAnnotator;
        this.sideMessageAnnotator = sideMessageAnnotator;
        annotatorType = "SideKeyTerm";
    }
    
    public ModelConcept(String label, DoneStepAnnotator doneStepAnnotator)
    {
        super(label);
        this.doneStepAnnotator = doneStepAnnotator;
        annotatorType = "DoneStep";
    }
    public ModelConcept(String label, LightSideMessageAnnotator sideMessageAnnotator,  DoneStepAnnotator doneStepAnnotator)
    {
        super(label);
        this.doneStepAnnotator = doneStepAnnotator;
        this.sideMessageAnnotator = sideMessageAnnotator;
        annotatorType = "SideDoneStep";
    }
    public ModelConcept(String label, LightSideMessageAnnotator sideMessageAnnotator,  KeyTermAnnotator keyTermAnnotator, DoneStepAnnotator doneStepAnnotator)
    {
        super(label);
        this.doneStepAnnotator = doneStepAnnotator;
        this.sideMessageAnnotator = sideMessageAnnotator;
        this.keyTermAnnotator = keyTermAnnotator;
        annotatorType = "SideKeyDone";
    }
    public String getExtendName() {
    	return "ModelConcept";
    }
    @Override
    public double match(String instance, Collection<String> annotations)
    {
        // Comment added by Tasmia
    	// This match function returns a double value between 0 to 1 returned by the predictor class.
    	// I have hooked up LightSIDEMessageAnnotator code here to call the SIDE trained model.
    	/* Actual TuTalk Code
    	 *
    	 * if(predictor.getPredictions(instance).containsKey(label))
            return predictor.getPredictions(instance).get(label);
        else
            return 0;*/
    	// This match function can be better. The entire addition of DoneStepAnnotator and KeyTermAnnotator need to be structured.
    	String label_text = "";
    	if(!annotatorType.equals("SideKeyTerm")) {
    		if(!annotatorType.equals("SideDoneStep")) {
    			if(!annotatorType.equals("SideKeyDone")) {
			    	if(annotatorType.equals("LightSide")) {
				        label_text = sideMessageAnnotator.annotateText(instance);
				        
			        }
			    	else if(annotatorType.equals("KeyTerm")) {
			    		label_text = keyTermAnnotator.hasKeyTerm(instance); //sideMessageAnnotator.annotateText(instance);
				        
			    	}
			    	else if(annotatorType.equals("DoneStep")) {
			    		label_text = doneStepAnnotator.isDoneState(instance); //sideMessageAnnotator.annotateText(instance);
				        
			    	}
			    	String[] split_label = label_text.split(",");
			        for(int i=0; i<split_label.length; i++) {
			        	if(split_label[i].indexOf(getLabel().trim())!= -1) {
			        		String prediction = split_label[i].split("-")[1];
			        		return Double.parseDouble(prediction);
			        	}
			        }
			        return 0.0;
    			}
    			else {
    				// If side key done all three are present
    				String[] labels = getLabel().trim().split("&");
            		label_text = sideMessageAnnotator.annotateText(instance);
            		String[] split_label = label_text.split(",");
        	        for(int i=0; i<split_label.length; i++) {
        	        	if(split_label[i].indexOf(labels[0])!= -1) {
        	        		String prediction = split_label[i].split("-")[1];
        	        		Double pred = Double.parseDouble(prediction);
        	        		if(pred == 1.0) {
        	        			String key_term_text = keyTermAnnotator.hasKeyTerm(instance);
        	        			String[] key_term_label = key_term_text.split(",");
        	        	        for(int j=0; j<key_term_label.length; j++) {
        	        	        	if(key_term_label[j].indexOf(labels[1])!= -1) {
        	        	        		prediction = key_term_label[j].split("-")[1];
        	        	        		Double pred_1 = Double.parseDouble(prediction);
        	        	        		if(pred_1 == 1.0) {
        	        	        			String done_step = doneStepAnnotator.isDoneState(instance);
        	        	        			String[] done_step_label = done_step.split(",");
        	        	        	        for(int k=0; k<done_step_label.length; k++) {
        	        	        	        	if(done_step_label[k].indexOf(labels[2])!= -1) {
        	        	        	        		prediction = done_step_label[k].split("-")[1];
        	        	        	        		return Double.parseDouble(prediction);
        	        	        	        	}
        	        	        	        }
        	        	        		}
        	        	        	}
        	        	        }
        	        			
        	        		}
        	        	}
        	        }
    				
    			}
    		}
    		else {
    			// If side and solved state both
    			String[] labels = getLabel().trim().split("&");
        		label_text = sideMessageAnnotator.annotateText(instance);
        		String[] split_label = label_text.split(",");
    	        for(int i=0; i<split_label.length; i++) {
    	        	if(split_label[i].indexOf(labels[0])!= -1) {
    	        		String prediction = split_label[i].split("-")[1];
    	        		Double pred = Double.parseDouble(prediction);
    	        		if(pred == 1.0) {
    	        			String done_step = doneStepAnnotator.isDoneState(instance);
    	        			String[] done_step_label = done_step.split(",");
    	        	        for(int j=0; j<done_step_label.length; j++) {
    	        	        	if(done_step_label[j].indexOf(labels[1])!= -1) {
    	        	        		prediction = done_step_label[j].split("-")[1];
    	        	        		return Double.parseDouble(prediction);
    	        	        	}
    	        	        }
    	        			
    	        		}
    	        	}
    	        }
    			
    		}
    	}
    	else {
    		// SideKeyTerm = If side and Key terms both
    		String[] labels = getLabel().trim().split("&");
    		label_text = sideMessageAnnotator.annotateText(instance);
    		String[] split_label = label_text.split(",");
	        for(int i=0; i<split_label.length; i++) {
	        	if(split_label[i].indexOf(labels[0])!= -1) {
	        		String prediction = split_label[i].split("-")[1];
	        		Double pred = Double.parseDouble(prediction);
	        		if(pred == 1.0) {
	        			String key_term_text = keyTermAnnotator.hasKeyTerm(instance);
	        			String[] key_term_label = key_term_text.split(",");
	        	        for(int j=0; j<key_term_label.length; j++) {
	        	        	if(key_term_label[j].indexOf(labels[1])!= -1) {
	        	        		prediction = key_term_label[j].split("-")[1];
	        	        		return Double.parseDouble(prediction);
	        	        	}
	        	        }
	        			
	        		}
	        	}
	        }
    	}
    	return 0.0;

    }
    public String toString()
    {
    	return "Classifier Concept "+label;
    }
}
