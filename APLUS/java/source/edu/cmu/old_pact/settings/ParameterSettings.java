package edu.cmu.old_pact.settings;

import java.awt.Color;
import java.awt.Font;
import java.util.StringTokenizer;
import java.util.Vector;

public class ParameterSettings {
	
	public static Color getColor(Object colorObj){
		Color color = null;
		if(colorObj instanceof Color)
				return (Color)colorObj;
		if(colorObj instanceof String){
			color = Settings.getColor((String)colorObj);
			if(color != null)
				return color;
			Vector cV = new Vector();
			StringTokenizer st = new StringTokenizer(colorObj.toString(), " ");
			while(st.hasMoreElements())
				cV.addElement(st.nextToken());
			colorObj = cV;	
		}
		if (colorObj instanceof Vector){
			Vector colorV  = (Vector)colorObj;
			color = new Color(	Integer.parseInt(colorV.elementAt(0).toString()),
								Integer.parseInt(colorV.elementAt(1).toString()),
								Integer.parseInt(colorV.elementAt(2).toString()));
		}
		return color;
	}
	
	
	public static Font getFont(Object fontObj){
		Font font = null;
		if(fontObj instanceof Font)
			font = (Font)fontObj;
		else if (fontObj instanceof Vector && ((Vector)fontObj).size() == 3){
			Vector fontV  = (Vector)fontObj;
			String sStyle = (String)fontV.elementAt(1);
			int intStyle = Font.PLAIN;
			if(sStyle.equalsIgnoreCase("ITALIC"))
				intStyle = Font.ITALIC;
			else if (sStyle.equalsIgnoreCase("BOLD"))
				intStyle = Font.BOLD;
			font = new Font((String)fontV.elementAt(0),
							intStyle,
							((Integer)fontV.elementAt(2)).intValue());
		}
		return font;
	}
	
	public static int getFontStyle(String strStyle){
		int intStyle = Font.PLAIN;
		if(strStyle.equalsIgnoreCase("ITALIC"))
			intStyle = Font.ITALIC;
		else if (strStyle.equalsIgnoreCase("BOLD"))
			intStyle = Font.BOLD;
		return intStyle;
	}
	
	public static String getWebColor(Color rgbColor) {
		return "#" + toHexString(rgbColor.getRed()) + 
					 toHexString(rgbColor.getGreen())+ 
					 toHexString(rgbColor.getBlue());
	}

	private static String toHexString(int n) {
    	String digits[] = 
    		{"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};

    	if (n > 255) n = 255;
    	if (n < 0) n = 0;

    	int d1 = (int)(n / 16);
    	int d2 = n - d1 * 16;

    	return digits[d1] + digits[d2];
   }
}	
		