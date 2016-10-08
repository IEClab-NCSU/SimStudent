package edu.cmu.old_pact.html.library;


public class HTMLTag {
	public static final int TEXT 	= 0;
  	public static final int A		= 1+TEXT;
  	public static final int ADDRESS	= 1+A;
  	public static final int B		= 1+ADDRESS;
  	public static final int BASE	= 1+B;
  	public static final int BIG		= 1+BASE;
  	public static final int BLOCKQUOTE	= 1+BIG;
  	public static final int BODY	= 1+BLOCKQUOTE;
  	public static final int BR		= 1+BODY;
  	public static final int CENTER	= 1+BR;
  	public static final int CITE	= 1+CENTER;   //10
  	public static final int CODE	= 1+CITE;
  	public static final int DD		= 1+CODE;
  	public static final int DIR		= 1+DD;
  	public static final int DL		= 1+DIR;
  	public static final int DT		= 1+DL;
  	public static final int EM		= 1+DT;
  	public static final int FONT	= 1+EM;
  	public static final int H1		= 1+FONT;
  	public static final int H2		= 1+H1;
  	public static final int H3		= 1+H2;			//20
  	public static final int H4		= 1+H3;
  	public static final int H5		= 1+H4;
  	public static final int H6		= 1+H5;
  	public static final int HR		= 1+H6;
  	public static final int I		= 1+HR;
  	public static final int IMG		= 1+I;
  	public static final int KBD		= 1+IMG;
  	public static final int LI		= 1+KBD;
  	public static final int LISTING	= 1+LI;
  	public static final int MENU	= 1+LISTING;		//30
  	public static final int OL		= 1+MENU;
  	public static final int P		= 1+OL;
  	public static final int PRE		= 1+P;
  	public static final int SAMP	= 1+PRE;
  	public static final int SMALL	= 1+SAMP;
  	public static final int STRONG	= 1+SMALL;
  	public static final int TITLE	= 1+STRONG;
  	public static final int TT		= 1+TITLE;
  	public static final int UL		= 1+TT;
  	public static final int VAR		= 1+UL;			//40
  	public static final int XMP		= 1+VAR;
  	public static final int HTML	= 1+XMP;
  	public static final int HEADER	= 1+HTML;
  	public static final int DRAG	= 1+HEADER;
  	public static final int MATHML	= 1+DRAG;
  	public static final int EXPRESSION	= 1+MATHML;
  	public static final int LASTTAG = 1+EXPRESSION;
  	public static final int MAXCODES = 1+LASTTAG;
  	
  	public static final String names[] = 
  {
    null,
    "A",
    "ADDRESS",
    "B",
    "BASE",
    "BIG",
    "BLOCKQUOTE",
    "BODY",
    "BR",
    "CENTER",
    "CITE",
    "CODE",
    "DD",
    "DIR",
    "DL",
    "DT",
    "EM",
    "FONT",
    "H1",
    "H2",
    "H3",
    "H4",
    "H5",
    "H6",
    "HR",
    "I",
    "IMG",
    "KBD",
    "LI",
    "LISTING",
    "MENU",
    "OL",
    "P",
    "PRE",
    "SAMP",
    "SMALL",
    "STRONG",
    "TITLE",
    "TT",
    "UL",
    "VAR",
    "XMP",
    "HTML",
    "HEADER",
    "DRAG",
    "MATHML",
    "EXPRESSION",
    "LASTTAG"
  };

}