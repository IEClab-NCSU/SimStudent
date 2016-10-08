//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/URLCoder.java
package edu.cmu.old_pact.cmu.toolagent;


public class URLCoder extends Object {

   public URLCoder()  { }

	static private byte HexToByte (char c) {
		byte b = 0x00;
	
		switch (Character.toLowerCase(c)) {
			case '0': b = 0x00;
					  break;
			case '1': b = 0x01;
					  break;
			case '2': b = 0x02;
					  break;
			case '3': b = 0x03;
					  break;
			case '4': b = 0x04;
					  break;
			case '5': b = 0x05;
					  break;
			case '6': b = 0x06;
					  break;
			case '7': b = 0x07;
					  break;
			case '8': b = 0x08;
					  break;
			case '9': b = 0x09;
					  break;
			case 'a': b = 0x0a;
					  break;
			case 'b': b = 0x0b;
					  break;
			case 'c': b = 0x0c;
					  break;
			case 'd': b = 0x0d;
					  break;
			case 'e': b = 0x0e;
					  break;
			case 'f': b = 0x0f;
					  break;}
		return b;
		}
	   

	static public String decode ( String code ) {
		int i;
		String new_string = "";
		char new_char;
	
		for (i = 0 ; i < code.length() ; i++) {
			switch (code.charAt(i)) {
				case '+': new_char = ' ';
						  break;
				case '%': new_char = (char) ((HexToByte(code.charAt(i+1)) << 4) | HexToByte(code.charAt(i+2)));
						  i = i + 2;
						  break;
				default: new_char = code.charAt(i);
						 break;
				}
			new_string = new_string + new_char;
			}
		
		//System.out.println("in urldecoder "+new_string);	
		return new_string;
	    }

	static public String encode ( String code ) {
		String new_string = "";
		new_string = java.net.URLEncoder.encode(code );
		return new_string;
		}
	}