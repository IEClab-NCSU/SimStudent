package edu.cmu.old_pact.settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;

import edu.cmu.pact.Utilities.trace;

public class Settings {

	public static Settings settings = new Settings();
	
	//FONTS
	public final static Font textFont = new Font("Arial",Font.PLAIN, 11);
	public final static Font boldTextFont = new Font("Arial",Font.BOLD, 11);
	public final static Font ssTextFont = new Font("Arial",Font.PLAIN, 9);
	public final static Font ssRowFont = new Font("Arial", Font.BOLD, 9);
	public final static Font inputFieldFont = new Font("Arial", Font.PLAIN, 10);
	public final static Font labelFont = new Font("Arial", Font.BOLD, 9);
	public final static Font axisFont = new Font("Arial", Font.PLAIN, 9);
	public final static Font dialogLabelFont = new Font("Dialog",Font.BOLD,11);
	public final static Font buttonFont = new Font("Dialog", Font.PLAIN, 10);
	public final static Font skillLabelFont = textFont;
	public final static Font factoringLabelFont = new Font("Arial",Font.BOLD,12);
	public final static Font factoringTextFieldFont = new Font("Arial",Font.PLAIN,12);
	
	public final static Font coordinateFont = new Font("Arial",Font.PLAIN,9);
	public final static Font bugMessageFont = new Font("Arial",Font.PLAIN,9);
	
	public static Font[] fonts ={	textFont,boldTextFont,ssTextFont,ssRowFont,
										inputFieldFont,labelFont,axisFont,dialogLabelFont,
										buttonFont,coordinateFont,bugMessageFont}; 
	public static String[] fontString = {	"textFont","boldTextFont","ssTextFont","ssRowFont",
										 		"inputFieldFont","labelFont","axisFont","dialogLabelFont",
										 		"buttonFont","coordinateFont","bugMessageFont"}; 
	
	//BASIC COLORS
	public final static Color teal = new Color(0,153,153);
	public final static Color hintOfPurple = new Color(235,245,255);
	public final static Color lightLightGray = new Color(242,242,255);
	public final static Color darkPurple = new Color(102,0,102);
	public final static Color redPurple = new Color(204,0,51);
	public final static Color bluePurple = new Color(102,56,102);
	public final static Color orange = new Color(255,171,66);
	public final static Color forest = new Color(46,140,33);
	public final static Color lighterGray = new Color(204,204,204);
	public final static Color rusty = new Color(187,0,0);
	
	//OBJECT COLORS
		//general colors
	public final static Color windowBGColor = hintOfPurple;
	public final static Color textColor = Color.black;
	public final static Color textHighlightColor = Color.lightGray;
	public final static Color inputFieldBGColor = Color.white;
	public final static Color defaultColor = Color.black; //used in GraphicsElement, always overridden
	public final static Color imageShadowTop = Color.white;
	public final static Color imageShadowBottom = Color.black;
	public final static Color scrollbarColor = Color.lightGray;
	public final static Color scrollbarBackgroundColor = windowBGColor;
	public final static Color scrollBarArrowColor = Color.black;
	public final static Color hasFocusColor = new Color(235,240,240);
	
		//message interface
	public final static Color delayColor = new Color(255,230,130); //Color.yellow;
	
		//statement colors
	public final static Color statementToolBarColor = new Color(51,0,102);
	public final static Color statementBoldColor = forest;
	
		//spreadsheet colors
	public final static Color selectedCellColor = Color.orange.brighter();
	public final static Color hasFocusCellColor = Color.orange;
	public final static Color backgroundCellColor = Color.white;
	public final static Color foregroundCellColor = Color.black;
	public final static Color unselectedCellColor = lightLightGray;
	public final static Color ssToolBarColor = new Color(0,153,153);
	public final static Color cellBorderColor=darkPurple;
	public final static Color cellInnerBorderColor=Color.white;
	public final static Color cellHighlightColor = teal;
	
		//grapher colors
	public final static Color grapherToolBarColor = redPurple;
	public final static Color grapherBackgroundColor = Color.white;
	//public final static Color graphBGColor=Color.lightGray;
	public final static Color graphBGColor=Color.white;
	public final static Color graphAxisLabelColor=Color.darkGray;
	//public final static Color gridColor=Color.white;
	public final static Color gridColor=Color.lightGray;
	public final static Color badGridColor=Color.white;
	public final static Color axisColor=teal;
	public final static Color graphLineColor1 = Color.blue;
	public final static Color graphLineColor2 = redPurple;
	public final static Color graphPointColor1 = graphLineColor1;
	public final static Color graphPointColor2 = graphLineColor2;
	public final static Color intersectionPointColor = redPurple;
	public final static Color draggingPointCrosshairColor = Color.gray;
	public final static Color pointDraggingColor = Color.blue;
	public final static Color pointFlaggedColor = Color.red;
	public final static Color pointShadowColor = Color.black; //shadow for 3D points
	public final static Color lineSelectionRectColor = grapherToolBarColor;
	public final static Color tracerTipColor = new Color(255,240,180);
	
		//solver colors
	public final static Color solverToolBarColor = new Color(255,102,0);
	
		//skillometer colors
	public final static Color skillBarBackgroundColor = lightLightGray;
	public final static Color skillTextColor = Color.black;
	public final static Color checkColor = Color.black;
	public final static Color skillBarOutlineColor = Color.black;
	public final static Color skillometerToolBarColor = new Color(255,204,0);
	public final static Color skillometerBackgroundColor = Color.white;
	
		//messages window colors
	public final static Color messageBackgroundColor = Color.white; //background of message pane
	public final static Color helpButtonColor = Color.lightGray;
	public final static Color helpButtonPanelColor = Color.lightGray;
	
		//main frame (menubar) colors
	public final static Color mainFrameBackgroundColor = windowBGColor; //lightGray?
	
		//spreadsheet cell bug message
	public final static Color bugMessageBackground = new Color(219,204,255);
	public final static Color bugMessageForeground = Color.black;
	
		//glossary
	public final static Color glossaryBackground = new Color(207,207,207);
	public final static Color glossaryToolBarColor = new Color(204,0,153);
	
		//reason tool
	public final static Color reasonBackground = new Color(207,207,207);
	public final static Color reasonToolBarColor = new Color(51,0,102);
	
		//diagram tool
	public final static Color diagramBackground = Color.white;
	public final static Color diagramToolBarColor = new Color(0,102,153); //255,102,0);
	public final static String select = "Select.gif";
	public final static String erase = "Erase.gif";
	public final static String padPoint = "PadPoint.gif";
	public final static String padBox = "Box.gif";
	public final static String HistogramBox = "HistogramBox.gif";
	public final static String smallBracket = "SmallBracket.gif";
	public final static String arrow = "Arrow.gif";
	
	public final static String largeBracket = "LargeBracket.gif";
	public final static String largeBracketLeft = "LargeBracket_left.gif";
	public final static String largeBracketRight = "LargeBracket_right.gif";
	public final static String largeBracketTop = "LargeBracket_top.gif";
	public final static String largeBracketTopRed = "bracket_top_red_solid.gif";
	public final static String largeBracketBottom = "LargeBracket_bottom.gif";
	public final static String largeBracketBottomRed = "bracket_bottom_red_solid.gif";
	
	public final static String protractor = "protractor.gif";
	public final static String help2 = "help2.gif";
	public final static String help2highlight = "help2highlight.gif";
	public final static String done = "done.gif";
	public final static String doneHighlight = "done highlight.gif";	
	public final static String divide = "PadDivide.gif";	
	
	// sorter
	public final static Color sorterBackground = new Color(230,230,230); //Color.white;
	public final static Color sorterBackgroundLight = new Color(248,248,248);
	public final static Color sorterToolBarColor = new Color(51,204,0);

	public final static String sorterLabel = "sorter.gif";
	public final static Dimension sorterLabelSize = new Dimension(19,68); 

		//ratio
	public final static Color ratioBackground = new Color(230,230,230); //Color.white;
	public final static Color ratioBackgroundLight = new Color(248,248,248);
	public final static Color ratioToolBarColor = new Color(51,204,0);

		//rationals
	public final static Color rationalsBackground = new Color(238,238,238);
	public final static Color rationalsBackgroundLight = new Color(248,248,248);
	public final static Color rationalsToolBarColor = new Color(51,0,102);
	
	//rational expression
	public final static Color focusColor = new Color(190,245,255);
	
		//factoring
	public final static Color factoringBackground = Color.white;
	public final static Color factoringToolBarColor = new Color(204,0,153);
	
		//quadratic formula
	public final static Color qFormulaBackground = Color.white;
	public final static Color qFormulaToolBarColor = new Color(255,102,0);
	
	public static Color[] colors = {teal,hintOfPurple,lightLightGray,darkPurple,redPurple,
									bluePurple,orange,forest,lighterGray,rusty,windowBGColor,
									textColor,textHighlightColor,inputFieldBGColor,defaultColor,
									imageShadowTop,imageShadowBottom,scrollbarColor,scrollbarBackgroundColor,
									scrollBarArrowColor,statementToolBarColor,statementBoldColor,
									selectedCellColor,hasFocusCellColor,backgroundCellColor,foregroundCellColor,
									unselectedCellColor,ssToolBarColor,cellBorderColor,
									cellInnerBorderColor,cellHighlightColor,grapherToolBarColor,
									graphBGColor,graphAxisLabelColor,gridColor,badGridColor,axisColor,
									graphLineColor1,graphLineColor2,graphPointColor1,graphPointColor2,
									intersectionPointColor,draggingPointCrosshairColor,pointDraggingColor,
									pointFlaggedColor,pointShadowColor,lineSelectionRectColor,
									solverToolBarColor,skillBarBackgroundColor,skillTextColor,checkColor,
									skillBarOutlineColor,skillometerToolBarColor,messageBackgroundColor,
									helpButtonColor,helpButtonPanelColor,mainFrameBackgroundColor,
									bugMessageBackground,bugMessageForeground};
	
	public static String[] colorString = {"teal","hintOfPurple","lightLightGray","darkPurple","redPurple",
									"bluePurple","orange","forest","lighterGray","rusty","windowBGColor",
									"textColor","textHighlightColor","inputFieldBGColor","defaultColor",
									"imageShadowTop","imageShadowBottom","scrollbarColor","scrollbarBackgroundColor",
									"scrollBarArrowColor","statementToolBarColor","statementBoldColor",
									"selectedCellColor","hasFocusCellColor","backgroundCellColor",
									"foregroundCellColor","unselectedCellColor","ssToolBarColor","cellBorderColor",
									"cellInnerBorderColor","cellHighlightColor","grapherToolBarColor",
									"graphBGColor","graphAxisLabelColor","gridColor","badGridColor","axisColor",
									"graphLineColor1","graphLineColor2","graphPointColor1","graphPointColor2",
									"intersectionPointColor","draggingPointCrosshairColor","pointDraggingColor",
									"pointFlaggedColor","pointShadowColor","lineSelectionRectColor",
									"solverToolBarColor","skillBarBackgroundColor","skillTextColor","checkColor",
									"skillBarOutlineColor","skillometerToolBarColor","messageBackgroundColor",
									"helpButtonColor","helpButtonPanelColor","mainFrameBackgroundColor",
									"bugMessageBackground","bugMessageForeground"};
	
	
	
	//HTML attributes
		//spreadsheet cell bug message
	public final static String bugBackground = "#CCCCFF";
	public final static String bugForeground = "#000000";
	
	//IMAGES
		//general
	//public final static String clLogo="Images//cllogo40.gif";
	public final static Dimension clLogoSize=new Dimension(40,40);
	public final static String help="Help.GIF";
	public final static String wrong="wrong.gif";
	
    //Solver
    public final static String equation="NewEq.gif";
    public final static String undo = "Undo.gif";
	public final static String cllogo40 = "cllogo40.gif";
	
		//spreadsheet
	public final static String wsLabel="worksheet.gif";
	public final static Dimension wsLabelSize=new Dimension(20,130); //148);
	
		//graph
	public final static String pointImage = "AddPoint.gif";
	public final static String lineImage = "AddLine.gif";
	public final static String interceptImage = "Intercpt.gif";
	public final static String pointLeftImage = "PntLeft.gif";
	public final static String pointRightImage = "PntRight.gif";
	public final static String displayPointsImage = "Points.gif";
	public final static String plotImage = "plot.gif";
	public final static String graphLabel = "graph.gif";
	public final static String zoomIn = "zoomOut.gif";
	public final static String zoomOut = "zoomIn.gif";
	public final static String quadraticImage = "AddParabola.gif";
	public final static String cubicImage = "AddCubic.gif";
	public final static String exponentialImage = "AddExponential.gif";
	public final static String shadingImage = "Shading.gif";
	public final static String intersectionImage = "Intersection.gif";
	//public final static Dimension graphLabelSize = new Dimension(23,88);
	public final static Dimension graphLabelSize = new Dimension(33,200);
	
		//statement
	public final static String statementLabel = "scenario.gif";
	public final static Dimension statementLabelSize = new Dimension(19,119);
	
		//solver
	public final static String solverLabel = "solver.gif";
	public final static Dimension solverLabelSize = new Dimension(19,91);
	
		//skillometer
	public final static String knownSkillBarImage = "knownskillbar.gif";
	public final static String skillBarImage = "skillbar.gif";
	public final static String skillometerLabel = "skills.gif";
	public final static Dimension skillometerLabelSize = new Dimension(19,78);
	
		//glossary
	public final static String glossaryLabel = "glossary.gif";
	public final static Dimension glossaryLabelSize = new Dimension(23,124);
	
		//factoringtool
	public final static String factoringLabel = "factoring.gif";
	public final static Dimension factoringLabelSize = new Dimension(23,115); //124);
	
		//diagram
	public final static String diagramLabel = "diagram.gif";
	public final static Dimension diagramLabelSize = new Dimension(23,100); // 123
	
	
		//quadratic formula tool
	public final static String qFormulaLabel = "q-solver.gif";
	public final static Dimension qFormulaLabelSize = new Dimension(23,110); //124);
	//public final static String plusMinusSquare = "plusminssquare.gif";
	public final static String qFormula = "qformula.gif";
	public final static String largeQFormula = "largeqform.gif";
	
		// ratio tool
	public final static String ratioLabel = "ratio-finder.gif";
	public final static Dimension ratioLabelSize = new Dimension(19,130); //154);

		//rationals tool
	public final static String rationalsLabel = "rationals.gif";
	public final static Dimension rationalsLabelSize = new Dimension(20,120);
	
	public final static Point dialogLocation = new Point(200,200);
	
	public final static int panelWidth = 32;
	
	public static Font getFont(String fontStr) {
		int y = 0, s = fontString.length;
		for(int i=0; i<s; i++){
			if(fontString[i].equalsIgnoreCase(fontStr)) {
				y = i;
				break;
			}
		}
		return fonts[y];
	}
	
	public static String getFontString(Font font) {
		int y = 0, s = fonts.length;
		for(int i=0; i<s; i++){
			if(fonts[i] == font) {
				y = i;
				break;
			}
		}
		return fontString[y];
	}	
	
	public static Color getColor(String colorStr) {
		int s = colorString.length;
		for(int i=0; i<s; i++){
			if(colorString[i].equalsIgnoreCase(colorStr)) {
				return colors[i];
			}
		}
		return null;
		
	}
	
	public static String getColorString(Color color) {
		int y = 0, s = colors.length;
		for(int i=0; i<s; i++){
			if(colors[i] == color) {
				y = i;
				break;
			}
		}
		return colorString[y];
	}
	
	public static Image loadImage (Component inComp, String inPath) {
		trace.err ("loading image " + inPath + " inComp = " + inComp);
  		Image image = null;
  		if (inComp == null) {
  			trace.err ("Error: can't load file: Component inComp is null");
  			return null;
  		}
  		try {
  			InputStream in = null;
   			in = settings.getClass().getResourceAsStream(inPath);
   			if(in == null)
   				in = settings.getClass().getResourceAsStream(inPath.toLowerCase());
			//trace.out (5, "Settings.java", "1: in = " + in);
   			byte[] data = new byte[in.available()];
   			in.read(data);
   			in.close();
   			//trace.out (5, "Settings.java", "2");
   			image = Toolkit.getDefaultToolkit().createImage(data);
   			
   			MediaTracker tracker = new MediaTracker(inComp);
   			//trace.out (5, "Settings.java", "3");
   			tracker.addImage(image, 0);
			//trace.out  (5, "Settings.java", "tracker = " + tracker + " image = " + image + " data = " + data + " inComp = " + inComp);
   			tracker.waitForAll();
  		} catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
  		//trace.out (5, "Settings.java", "done loading");
  		return image;
 	}					
}


