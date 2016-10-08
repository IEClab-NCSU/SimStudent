package edu.cmu.pact.miss;

import java.lang.reflect.Method;

import pact.CommWidgets.JCommWidget;

//Gustavo 24jan2007

/**
 * A wrapper class for JCommWidget, for the purposes of bootstrapping the Stoich 
 * Flash Tutor, where a FoA element could have an empty value even when an actual 
 * entity (i.e., the corresponding working element) has a real value.  SimSt in
 * general requires WME to get value prior to learning, whereas Stoich Flash Tutor
 * passes all WME as FoA for every steps.  However, we wanted to give a value only 
 * for those WMEs that have corresponding demonstration step that has been performed.
 * 
 */
public class SsFoaElement {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Fields 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    private JCommWidget dw;
    public JCommWidget getCommWidget(){
        return dw;
    }
    public String getCommName(){
        return getCommWidget().getCommName();
    }
    
    // The default value of SsFoaElement
    private String value = "nil"; 
    public String getValue(){
        return value;
    }
    public void setValue(String value){
        this.value = value;
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public SsFoaElement(JCommWidget dw){
        this.dw = dw;
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public void removeHighlightXX() {
        Method resetHighlightWidget = null;
        try {
            //Changed to removeHighlight since it is defined in commWidget
            resetHighlightWidget = dw.getClass().getMethod( "removeHighlight", (Class[])null );
            resetHighlightWidget.invoke( dw, (Object[])null );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
