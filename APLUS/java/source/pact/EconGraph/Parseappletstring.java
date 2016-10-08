package pact.EconGraph;

import java.awt.Color;

public class Parseappletstring {
    
    //parses string s...assumes string contains fields deliminated by commas ","
    //and that String values are proceeded by a "#"
    //returns the arrays in order of appearance
    
    public static void parseString(String s, int [] integers, String [] strings) {
        
    	int i,st,end,intnum,snum;
        String temp;
        
        //   Vector inttest = new Vector();
        
        
        
        intnum = 0;
        snum = 0;

        for (i = 0; i < s.length(); i++) {
            st = i;
            end = i+1;
            while ((end < s.length()) && (!s.substring(end,end+1).equals(",")))
                end++;
            temp = s.substring(st,end++);
            i = end-1;
            if (temp.substring(0,1).equals("#")) {
                strings[snum++] = temp.substring(1,temp.length());
            }
            else {
                integers[intnum++] = Integer.parseInt(temp);
                //         inttest.addElement(temp);//***
            }
        }
        
        
        //   for (int t = 0; t < inttest.size(); t++)
        //   {
        //      System.out.println(t+"="+Integer.parseInt(""+inttest.elementAt(t)));
        //   }
        
        //for (i = 0; i < intnum; i++) System.out.println("Int "+i+ ":"+integers[i]);
//        for (i = 0; i < intnum; i++) System.out.println("Str "+i+":***"+strings[i]+"***");
    }
    
    public static Color makeColor(String s) {
        Color linecolor;
        
        if (s.equals("blue")) linecolor = new Color(37,17,182);
        else
            if(s.equals("lightBlue")) linecolor=new Color(106,149,230);
        else
            if(s.equals("lb2")) linecolor= new Color(15, 40, 84);
            else
            if (s.equals("red")) linecolor = Color.red;
            else
                if (s.equals("green")) linecolor = Color.green;
                else
                    if (s.equals("magenta")) linecolor = Color.magenta;
                    else
                        if (s.equals("cyan")) linecolor = Color.cyan;
                        else
                            if (s.equals("pink")) linecolor = Color.pink;
                            else
                                if (s.equals("yellow")) linecolor = Color.yellow;
                                else
                                    if (s.equals("gray")) linecolor = Color.gray;
                                    else
                                        if (s.equals("orange")) linecolor = Color.orange;
                                        else
                                        if(s.equals("darkGray")) linecolor = Color.darkGray;
                                        else if(s.equals("lightGray")) linecolor= Color.lightGray;
                                        else if(s.equals("white")) linecolor=Color.white;
                                                                                else linecolor = Color.black;
        
        return linecolor;
    }
    
    
}