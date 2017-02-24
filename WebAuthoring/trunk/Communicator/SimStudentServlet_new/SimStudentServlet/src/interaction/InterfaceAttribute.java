package interaction;
import java.util.HashMap;
import java.util.Map;

import servlet.RequestMessage;

/**
 * Class which represents a particular component on the interface. Simply
 * getting and modifying an object of this class will not modify the component; you must
 * send it to the interface using Backend's modifyInterface() method.
 * 
 * @author Patrick Nguyen
 *
 */
public class InterfaceAttribute extends RequestMessage{
	private String component;
	private Color backgroundColor;
	private Color borderColor;
	private Style borderStyle;
	private int borderWidth;
	private boolean enabled;
	private Color fontColor;
	private int fontSize;
	private int height;
	private boolean hintHighlight;
	private int width;
	private int xCoor;
	private int yCoor;
	
	/**
	 * Enum for representing different border styles that can be set on the interface components.
	 * The border styles are exactly as they look.
	 *
	 */
	public enum Style{
		HIDDEN, DOTTED, DASHED, SOLID, DOUBLE
	}
	
	private Map<String,String> modifications;//useful for implementing toXML()
	
	/**
	 * Creates a modification object for the specified component.
	 * @param component Name of the component that will be modified
	 */
	public InterfaceAttribute(String component){
		this.component = component;
		modifications = new HashMap<String,String>();
		setMessageType("InterfaceAttribute");
	}
	/**
	 * Gets the name of the component.
	 * @return The name of the component whose modification this object represents.
	 */
	public String getName() {
		return component;
	}
	/**
	 * Sets the name of the component.
	 * 
	 * @param name The name of the component whose modification this object represents.
	 */
	public void setName(String name) {
		this.component = name;
	}
	/**
	 * Gets the background color that the component has or 
	 * will have after modification.
	 * 
	 * @return Object representing RGB value of background color modification
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	/**
	 * Modify the component to have the specified background color.
	 * 
	 * @param backgroundColor Object representing RGB value of background color modification
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = new Color(backgroundColor);
		modifications.put("background_color", backgroundColor.toCSS());
	}
	/**
	 * Gets the border color that the component has or 
	 * will have after modification.
	 * 
	 * @return Object representing RGB value of border color modification
	 */
	public Color getBorderColor() {
		return borderColor;
	}
	/**
	 * Modify the component to have the specified border color.
	 * 
	 * @param borderColor Object representing RGB value of border color modification
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = new Color(borderColor);
		modifications.put("border_color", borderColor.toCSS());
	}
	/**
	 * Gets the border style that the component has or 
	 * will have after modification.
	 * 
	 * @return Enum value representing the style modification
	 */
	public Style getBorderStyle() {
		return borderStyle;
	}
	/**
	 * Modify the component to have the specified border style.
	 * 
	 * @param borderStyle Enum value representing the style modification
	 */
	public void setBorderStyle(Style borderStyle) {
		this.borderStyle = borderStyle;
		if(borderStyle == null){
			modifications.remove("border_style");
			return;
		}
		String style = null;
		switch(borderStyle){
			case HIDDEN:
				style = "hidden";
				break;
			case DOTTED:
				style = "dotted";
				break;
			case DASHED:
				style = "dashed";
				break;
			case SOLID:
				style = "solid";
				break;
			case DOUBLE:
				style = "double";
				break;
		}
		modifications.put("border_style", style);
	}
	/**
	 * Gets the border width of the component after the modification, in number of pixels.
	 * 
	 * @return Integer for the border width modification, in number of pixels.
	 */
	public int getBorderWidth() {
		return borderWidth;
	}
	/**
	 * Modify the component to have the specified width, in number of pixels. 
	 * 
	 * @param borderWidth Integer for the border width modification, in number of pixels.
	 */
	public void setBorderWidth(int borderWidth){
		this.borderWidth = borderWidth;
		modifications.put("border_width",""+borderWidth);
	}
	/**
	 * Gets whether or not the component is set to be enabled.
	 * @return Boolean for the enable modification
	 */
	public Boolean getIsEnabled() {
		return enabled;
	}
	/**
	 * Modify the component by enabling or disabling it.
	 * 
	 * @param enabled Boolean for the enable modification
	 */
	public void setIsEnabled(boolean enabled) {
		this.enabled = enabled;
		modifications.put("enabled", ""+enabled);
	}
	/**
	 * Gets the font color that the component has or 
	 * will have after modification.
	 * 
	 * @return Object representing RGB value of font color modification
	 */
	public Color getFontColor() {
		return fontColor;
	}
	/**
	 * Modify the component to have the specified font color.
	 * 
	 * @param fontColor Object representing RGB value of font color modification
	 */
	public void setFontColor(Color fontColor) {
		this.fontColor = new Color(fontColor);
		modifications.put("font_color",fontColor.toCSS());
	}
	/**
	 * Gets the font size the component has or 
	 * will have after modification.
	 * 
	 * @return Integer representing the font size modification
	 */
	public int getFontSize() {
		return fontSize;
	}
	/**
	 * Modify the component to have the specified font size.
	 * 
	 * @param fontSize Integer representing the font size modification
	 */
	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
		modifications.put("font_size", ""+fontSize);
	}
	/**
	 * Gets the height the component has or 
	 * will have after modification.
	 * 
	 * @return Integer representing the height modification
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * Modify the component to have the specified height, in number of pixels.
	 * 
	 * @param height Integer representing the height modification
	 */
	public void setHeight(int height) {
		this.height = height;
		modifications.put("height", ""+height);
	}
	/**
	 * Gets whether the component is set to have a hint highlight.
	 * 
	 * @return Boolean for the hint highlight modification
	 */
	public boolean getIsHintHighlight() {
		return hintHighlight;
	}
	/**
	 * Modify the component to show or lack a hint highlight.
	 * 
	 * @param hintHighlight Boolean for the hint highlight modification
	 */
	public void setIsHintHighlight(boolean hintHighlight) {
		this.hintHighlight = hintHighlight;
		modifications.put("hint_highlight", ""+hintHighlight);
	}

	/**
	 * Gets the width the component is set to have after modification, in number of pixels.
	 * 
	 * @return Integer representing the width modification, in number of pixels
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * Modify the component to have the specified width, in number of pixels.
	 * 
	 * @param width Integer representing the width modification, in number of pixels
	 */
	public void setWidth(int width) {
		this.width = width;
		modifications.put("width",""+width);
	}
	/**
	 * Gets the x coordinate the componet is set to have after modification.
	 * 
	 * Coordinates are measured with (0,0) in the top left corner and the x axis running horizontally.
	 * @return Integer representing the x coordinate modification
	 */
	public int getX() {
		return xCoor;
	}
	/**
	 * Modify the component to have the specified x coordinate.
	 * 
	 * Coordinates are measured with (0,0) in the top left corner and the x axis running horizontally.
	 * @param xCoor Integer representing the x coordinate modification
	 */
	public void setX(int xCoor) {
		this.xCoor = xCoor;
		modifications.put("x_coor",""+xCoor);
	}
	/**
	 * Gets the y coordinate the componet is set to have after modification.
	 * 
	 * Coordinates are measured with (0,0) in the top left corner and the y axis running vertically.
	 * @return Integer representing the y coordinate modification
	 */
	public int getY() {
		return yCoor;
	}
	/**
	 * Modify the component to have the specified y coordinate.
	 * 
	 * Coordinates are measured with (0,0) in the top left corner and the y axis running vertically.
	 * @param yCoor Integer representing the y coordinate modification
	 */
	public void setY(int yCoor) {
		this.yCoor = yCoor;
		modifications.put("y_coor", ""+yCoor);
	}
	/**
	 * Gets a map of all modifications made on this component
	 * @return Map of all modifications on this component
	 */
	public Map<String, String> getModifications() {
		return modifications;
	}
	/**
	 * Sets the map of modifications. Use carefully.
	 * @param modifications Map of all modifications on this component.
	 */
	public void setModifications(Map<String, String> modifications) {
		this.modifications = modifications;
	}
	
	/**
	 * Returns an xml string representing this modification object, to be sent to the interface.
	 * @return String representing this modification object in xml
	 */
	public String modificationXML(){
		String xml = "<message><verb>DUMMY</verb><properties><MessageType>"+getMessageType()+"</MessageType>";
		xml += "<component>"+component+"</component>";
		for(String tag : modifications.keySet()){
			String val = modifications.get(tag);
			if(tag.equals("border_width"))
				val+="px";
			xml += "<"+tag+">"+val+"</"+tag+">";
		}
		xml += "</properties></message>";
		return xml;
	}
	
	@Override
	public String toString(){
		String s = "component: "+getName();
		s+="\nenabled: "+getIsEnabled();
		s+="\nfont color: "+getFontColor();
		s+="\nfont size: "+getFontSize();
		s+="\nheight: "+getHeight();
		s+="\nhint highlight: "+getIsHintHighlight();
		s+="\nwidth: "+getWidth();
		s+="\nx: "+getX();
		s+="\ny: "+getY();
		return s;
	}
	
}
