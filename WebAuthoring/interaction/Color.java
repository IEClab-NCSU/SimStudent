package interaction;
/**
 * Class representing a color, using its RGB values.
 * 
 * @author Patrick Nguyen
 *
 */
public class Color {
	private int R;
	private int G;
	private int B;
	
	/**
	 * Creates a color with the specified RGB values.
	 * 
	 * If the RGB values are not in the range 0 to 255 inclusive, a NumberFormatException is thrown.
	 * @param R - Red value
	 * @param G - Green value
	 * @param B - Blue value
	 */
	public Color(int R, int G, int B){
		if(!(0 <= R && R <= 255)
		|| !(0 <= G && G <= 255)
		|| !(0 <= B && B <= 255))
			throw new NumberFormatException("Value not in range [0,255]");
		this.R = R;
		this.G = G;
		this.B = B;
	}
	/**
	 * Creates a color with RGB value (0,0,0), meaning completely black.
	 */
	public Color(){
		this(0,0,0);
	}
	
	public Color(Color c){
		this(c.getR(),c.getG(),c.getB());
	}
	/**
	 * Gets the red value represented by this object.
	 * @return Red value
	 */
	public int getR() {
		return R;
	}
	/**
	 * Sets the red value represented by this object.
	 * 
	 * If the argument is not in the range 0 to 255 inclusive, a NumberFormatException is thrown.
	 * @param R Red value
	 */
	public void setR(int R) {
		if(!(0 <= R && R <= 255))
			throw new NumberFormatException("Value not in range [0,255]");
		this.R = R;
	}
	/**
	 * Gets the green value represented by this object.
	 * @return G Green value
	 */
	public int getG() {
		return G;
	}
	/**
	 * Sets the green value represented by this object.
	 * 
	 * If the argument is not in the range 0 to 255 inclusive, a NumberFormatException is thrown.
	 * @param G Green value
	 */
	public void setG(int G) {
		if(!(0 <= G && G <= 255))
			throw new NumberFormatException("Value not in range [0,255]");
		this.G = G;
	}
	/**
	 * Gets the blue value represented by this object.
	 * @return blue value
	 */
	public int getB() {
		return B;
	}
	/**
	 * Sets the blue value represented by this object.
	 * 
	 * If the argument is not in the range 0 to 255 inclusive, a NumberFormatException is thrown.
	 * @param B Blue value
	 */
	public void setB(int blue) {
		if(!(0 <= B && B <= 255))
			throw new NumberFormatException("Value not in range [0,255]");		
		this.B = blue;
	}
	
	/**
	 * Returns a String representation of this object in CSS format.
	 * @return String representation of this object in CSS format
	 */
	public String toCSS(){
		String red = String.format("%02X", this.R);
		String green = String.format("%02X", this.G);
		String blue = String.format("%02X", this.B);
		return "#"+red+green+blue;
	}
	
	/**
	 * Given a CSS string representing a color, create a Color object
	 * @param css CSS string representing a color
	 * @return Color object with RGB values corresponding to those of the string
	 */
	public static Color parseCSS(String css){
		if(css == null)
			return null;
		if(css.length() != 7 || css.charAt(0) != '#'){
			throw new IllegalArgumentException("Not a CSS color string");
		}
		
		int r = Integer.parseInt(css.substring(1,3),16);
		int g = Integer.parseInt(css.substring(3,5),16);
		int b = Integer.parseInt(css.substring(5,7),16);
		
		return new Color(r,g,b);
	}
}
