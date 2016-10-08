// UUDecode.java
//
// You may use and distribute this example. This program is 
// provided WITHOUT WARRANTY either expressed or implied.
//
// Copyright Henrik Bjorkman 1998
//
//
// History:
// 0.0 Created by Henrik Bjorkman 1998-07-16 (from MakeHtmlLinks.java)
// 0.1 Single class version. Henrik 1998-07-19
// 0.2 fixed a bug that caused StringIndexOutOfBoundsException Henrik 21/11.

package edu.cmu.hcii.ctat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CTATUUDecode extends CTATBase 
{
  public BufferedReader in;
  public DataOutputStream out;

  // To get a string without the first n words in string str.
  public static String skipWords(String str, int n)
  {
    int i=0;

    while (i<str.length() && Character.isSpaceChar(str.charAt(i))) {i++;}

    while (n>0)
    {
      while (i<str.length() && !Character.isSpaceChar(str.charAt(i))) {i++;}
      while (i<str.length() && Character.isSpaceChar(str.charAt(i))) {i++;}
      n--;
    }
    
    return(str.substring(i));
  }

  // To get the first word in a string. Returns a string with all characters 
  // found before the first space character.
  public static String getFirstWord(String str) 
  {
    int i=0;
    while (i<str.length() && !Character.isSpaceChar(str.charAt(i))) {i++;}
    return(str.substring(0,i));
  }

  public static String getWord(String str, int n)
  {
    return(getFirstWord(skipWords(str,n)));
  }  
    
  public CTATUUDecode (InputStream in)
  {
	setClassName ("CTATUUDecode");
	debug ("CTATUUDecode ()"); 
		
    this.in = new BufferedReader(new InputStreamReader(in));
    this.out = null;
    //this.start ();
  }

  void printBin8(int d) throws IOException
  {
    for (int i=0;i<8;i++)
    {
      out.write((((d<<i)&0x80)==0)?'0':'1');
    }
    out.write(' ');
  }

  void decodeString3(String str)
  {
    int c0=str.charAt(0)^0x20;
    int c1=str.charAt(1)^0x20;
    int c2=str.charAt(2)^0x20;
    int c3=str.charAt(3)^0x20;

    try
    {
      out.write( ((c0<<2) & 0xfc) | ((c1>>4) & 0x3) );
      out.write( ((c1<<4) & 0xf0) | ((c2>>2) & 0xf) );
      out.write( ((c2<<6) & 0xc0) | ((c3) & 0x3f) );
    }
    catch (IOException e) 
    {
    	debug ("run: "+e);
    }
  }

  void decodeString2(String str)
  {
    int c0=str.charAt(0)^0x20;
    int c1=str.charAt(1)^0x20;
    int c2=str.charAt(2)^0x20;

    try
    {
      out.write( ((c0<<2) & 0xfc) | ((c1>>4) & 0x3) );
      out.write( ((c1<<4) & 0xf0) | ((c2>>2) & 0xf) );
    }
    catch (IOException e) 
    {
    	debug ("run: "+e);
    }
  }

  void decodeString1(String str)
  {
    int c0=str.charAt(0)^0x20;
    int c1=str.charAt(1)^0x20;

    try
    {
      out.write( ((c0<<2) & 0xfc) | ((c1>>4) & 0x3) );
    }
    catch (IOException e) 
    {
    	debug ("run: "+e);
    }
  }
    
  public void run() 
  {
    String str;
    boolean more=true;
    //int n=0;

    try 
    {
      while(more) 
      {
        // read in a line
        str = in.readLine();
        if (str == null) {more=false;break;}

        if ( str.startsWith("begin ") )
        {  
          debug(str);
          String fileName=getWord(str,2);
          //debug(fileName);
          if (fileName.length()==0) break;

          out=new DataOutputStream(new FileOutputStream(fileName));

          for(;;) 
          {
            str = in.readLine();
            if (str == null) {more=false;break;}
            if (str.equals("end")) break;

            int pos=1;
            int d=0;

            int len=((str.charAt(0)&0x3f)^0x20);
            //debug("len "+len +" "+str.length());

            while ((d+3<=len) && (pos+4<=str.length()))
            {
              decodeString3(str.substring(pos,pos+4));
              pos+=4;
              d+=3;
            }            

            if ((d+2<=len) && (pos+3<=str.length()))
            {
              decodeString2(str.substring(pos,pos+3));
              pos+=3;
              d+=2;
            }            

            if ((d+1<=len) && (pos+2<=str.length()))
            {
              decodeString1(str.substring(pos,pos+2));
              pos+=2;
              d+=1;
            }            

            if (d!=len) {debug("did not get all");break;}
          }

          out.close();

          //n++;
        }
      }
    }
    catch (IOException e) 
    {
    	debug ("run: "+e);
    }    
    finally 
    { 
      try 
      {
        if (in!=null) {in.close();}
        if (out!=null) {out.close();}
      } 
      catch (IOException e) 
      {
      	debug ("run: "+e);
      }
    }
  }

  /*
  public static void main(String args[]) 
  {
    if (args.length < 1 )
    {
      System.out.println("Usage: java UUDecode <filename>");
      System.exit(0);
    }
        
    try 
    {
       new UUDecode(new FileInputStream(args[0]));
    }
    catch (IOException e) { System.err.println("UUDecode: " + e); }
  }
  */
}

