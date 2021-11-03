package edu.cmu.pact.miss;

import java.util.Vector;



//17 April 2007
//this class is used for passing the SAI around more easily.

public class Sai {

    Vector selectionV;
    Vector actionV;
    Vector inputV;
    String ruleName;

    public Sai(Vector sV, Vector aV, Vector iV){
        selectionV = sV;
        actionV = aV;
        inputV = iV;
    }

    public Sai(String s, String a, String i){
        selectionV = new Vector();
        selectionV.add(s);
        actionV = new Vector();
        actionV.add(a);
        inputV = new Vector();
        inputV.add(i);
    }
    
    public Sai(String s, String a, String i, String ruleName){
        selectionV = new Vector();
        selectionV.add(s);
        actionV = new Vector();
        actionV.add(a);
        inputV = new Vector();
        inputV.add(i);
        this.ruleName = ruleName;
    }

    public Sai(String saiStr){
        String[] sp = saiStr.split(",");
        String s = sp[0];
        String a = sp[1];
        String i = sp[2];

        selectionV = new Vector();
        selectionV.add(s);
        actionV = new Vector();
        actionV.add(a);
        inputV = new Vector();
        inputV.add(i);
    }

    
    public String getS(){
        return (String) selectionV.get(0);
    }
    public String getA(){
        return (String) actionV.get(0);
    }
    public String getI(){
        return (String) inputV.get(0);
    }

    public String toString(){
        return getS() + ", " + getA() + ", " + getI();
    }
    
    public String getRuleName(){
        return ruleName;
    }
   
    //copied from SimSt.isStepModelTraced()
    public boolean matches(Sai sai2, SimSt simSt){
        return  (getS().equals(sai2.getS()) &&
                 getA().equals(sai2.getA()) &&
                simSt.compairInput(getI(), sai2.getI()));
    }
    
    public boolean equals(Object saiObj)
    {
    	if(!(saiObj instanceof Sai))
    		return false;
    	Sai sai = (Sai) saiObj;
    	return getS().equals(sai.getS()) && getA().equals(sai.getA()) && getI().equals(sai.getI());
    }
}
