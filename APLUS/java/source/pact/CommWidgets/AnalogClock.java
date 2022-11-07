/*
 * AnalogClock.java
 *
 * Created on November 22, 2003, 5:56 PM
 */
package pact.CommWidgets;
import java.awt.Canvas;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import edu.cmu.pact.ctat.MessageObject;     // import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;
/**
 * @author Jonathan Freyberger
 */
public class AnalogClock extends JCommWidget
implements ActionListener, Serializable {
	protected Clock clock;
	protected JSpinner hourSpinner;
	protected JSpinner minuteSpinner;
	protected JButton toggleAMPMButton;
	protected JLabel AMPMLabel;
	protected JLabel hourLabel;
	protected JLabel minuteLabel;
	protected boolean studentMode;
	// ***************************************************************************//
	/** Creates a new instance of AnalogClock */
	public AnalogClock() {
		// trace.out("AnalogClock\n");
		studentMode = false;
		initialized = false;
		actionName = "UpdateClock";
		this.setLayout(new FlowLayout());
		JPanel main = new JPanel();
		main.setLayout(new GridLayout(1, 2, 10, 10));
		JPanel one = new JPanel();
		clock = new Clock();
		clock.setHour(12);
		clock.setMinutes(0);
		clock.setAMPM("AM");
		one.add(clock);
		main.add(one);
		SpinnerChange spc = new SpinnerChange();
		JPanel configPanel = new JPanel();
		JPanel two = new JPanel();
		two.setLayout(new GridLayout(3, 2, 0, 0));
		hourLabel = new JLabel("H");
		two.add(hourLabel);
		hourSpinner = new JSpinner();
		Object hour[] = new Object[12];
		for (int i = 0; i < 12; i++)
			hour[i] = new Integer(i + 1);
		CyclingSpinnerListModel cslmHour = new CyclingSpinnerListModel(hour);
		cslmHour.setValue(new Integer(12));
		hourSpinner.setModel(cslmHour);
		hourSpinner.addChangeListener(spc);
		two.add(hourSpinner);
		minuteLabel = new JLabel("M");
		two.add(minuteLabel);
		minuteSpinner = new JSpinner();
		Object minutes[] = new Object[12];
		for (int i = 0; i < 12; i++)
			minutes[i] = new Integer(i * 5);
		CyclingSpinnerListModel cslmMinutes = new CyclingSpinnerListModel(
				minutes);
		cslmMinutes.setValue(new Integer(0));
		minuteSpinner.setModel(cslmMinutes);
		minuteSpinner.addChangeListener(spc);
		two.add(minuteSpinner);
		AMPMLabel = new JLabel("AM");
		two.add(AMPMLabel);
		toggleAMPMButton = new JButton("AM/PM");
		toggleAMPMButton.addActionListener(new ToggleAMPM());
		two.add(toggleAMPMButton);
		configPanel.add(two);
		main.add(configPanel);
		add(main);
	}
	// ***************************************************************************//
	public void setStudentMode(boolean b)
	{
		studentMode = b;
		if (studentMode)
		{
			hideConfigComponents();
		}
	}
	// ***************************************************************************//
	public MessageObject getDescriptionMessage() {
		// trace.out("START MessageObject\n");
		if (!initialize(getController()))
		{
			trace.out(5, this, "Error!!!!!!!!!!!!!!!!!");
			return null;
		}
		
        MessageObject mo = MessageObject.create("InterfaceDescription");
        mo.setVerb("SendNoteProperty");
		mo.setProperty("WidgetType", "AnalogClock");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));
		
		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();
		if(deftemplates != null)  mo.setProperty("jessDeftemplates", deftemplates);
		
		if(instances != null)    mo.setProperty("jessInstances", instances);
		serializeGraphicalProperties(mo);
		// trace.out("END MessageObject\n");
		if (!studentMode)
		{
			hideConfigComponents();
		}
		return mo;
	}
	// ***************************************************************************//
	public Vector createJessDeftemplates()
	{
		// trace.out("START createJessDefttemplates\n");
		Vector deftemplates = new Vector();
		String deftemplateStr =
		"(deftemplate analogclock (slot name)"
		+ " (slot hour) (slot minutes) (slot AMPM) (slot value))";
		deftemplates.add(deftemplateStr);
		// trace.out("END createJessDeftemplates");
		return deftemplates;
	}
	// ***************************************************************************//
	public Vector createJessInstances()
	{
		// trace.out("START createJessInstances\n");
		Vector instances = new Vector();
		String instanceStr = "(assert (analogclock "
		+ "(name " + commName + ") "
		+ "(hour " + clock.getHour() + ") "
		+ "(minutes " + clock.getMinutes() + ") "
		+ "(AMPM " + clock.getAMPM() + ") "
		+ "(value " + clock.getHour() + "-" + clock.getMinutes()
		+ "-" + clock.getAMPM() + ")))";
		instances.add(instanceStr);
		// trace.out("END createJessInstances\n");
		return instances;
	}
	// ***************************************************************************//
	public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
	}
	// ***************************************************************************//
	public void doCorrectAction(String str, String str2, String str3) {
		// trace.out("Do correct action\n");
	}
	// ***************************************************************************//
	public void doIncorrectAction(String str, String str1) {
		// trace.out("Do incorrect action\n");
	}
	// ***************************************************************************//
	public void doInterfaceAction(String str, String str1, String str2)
	{
		// trace.out("Do interface action\n");
		// trace.out("Str = " + str);
		// trace.out("Str1 = " + str1);
		// trace.out("Str2 = " + str2);
		// trace.out("\n");
		if (str1.equals("UpdateClock"))
		{
			String[] values = str2.split("-");
			// trace.out("blah 1 = " + values[0]);
			// trace.out("blah 2 = " + values[1]);
			// trace.out("blah 3 = " + values[2]);
			clock.setHour(Integer.parseInt(values[0]));
			clock.setMinutes(Integer.parseInt(values[1]));
			clock.setAMPM(values[2]);
			AMPMLabel.setText(values[2]);
		}
		repaint();
	}
	// ***************************************************************************//
	public void doLISPCheckAction(String str, String str1) {
		// trace.out("Do lisp check action\n");
	}
	// ***************************************************************************//
	public Object getValue() {
		// trace.out("Get value\n");
		return "" + clock.getHour()
		+ "-"
		+ clock.getMinutes()
		+ "-"
		+ clock.getAMPM();
	}
	public boolean isChangedFromResetState()
	{
		return true;
	}
	// ***************************************************************************//
	public void reset(TutorController controller) {
		initialize(controller);
		hideConfigComponents();
	}
	// ***************************************************************************//
	public int getHour() {
		return (clock.getHour());
	}
	// ***************************************************************************//
	public int getMinutes() {
		return (clock.getMinutes());
	}
	// ***************************************************************************//
	public String getAMPM() {
		return (clock.getAMPM());
	}
	// ***************************************************************************//
	public void hideConfigComponents()
	{
		hourLabel.setVisible(false);
		minuteLabel.setVisible(false);
		minuteSpinner.setVisible(false);
		hourSpinner.setVisible(false);
		toggleAMPMButton.setVisible(false);
		studentMode = true;
	}
	// ***************************************************************************//
	protected class SpinnerChange implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			if (e.getSource() == minuteSpinner)
			{
				clock.setMinutes(
				((Integer) minuteSpinner.getValue())
				.intValue());
			}
			else if (e.getSource() == hourSpinner)
			{
				clock.setHour(
				((Integer) hourSpinner.getValue())
				.intValue());
			}
		}
	}
	// ***************************************************************************//
	protected class ToggleAMPM implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (AMPMLabel.getText().equals("PM"))
			{
				AMPMLabel.setText("AM");
				clock.setAMPM("AM");
			}
			else
			{
				AMPMLabel.setText("PM");
				clock.setAMPM("PM");
			}
		}
	}
	public void mousePressed(MouseEvent e) {
	}
}
// ***************************************************************************//
// ***************************************************************************//
class Clock extends Canvas implements Serializable
{
	/** X position for clock */
	protected double locX;
	/** Y position for clock */
	protected double locY;
	/** radius for the clock */
	protected int radius;
	/** font size for numbers displayed on clock */
	protected int clockFontSize;
	/** the minute hand */
	protected MinuteHand minuteHand;
	/** the hour hand */
	protected HourHand hourHand;
	/** AM/PM */
	protected String AMPM;
	// ***************************************************************************//
	public Clock()
	{
		AMPM = "AM";
		locX = 0;
		locY = 0;
		radius = 75;
		clockFontSize = 14;
		minuteHand = new MinuteHand((int) (locX + radius),
		(int) (locY + radius),
		(int) (radius * 0.75), 3,
		(int) (radius * 0.10), 9);
		minuteHand.setMinutes(0);
		hourHand = new HourHand((int) (locX + radius),
		(int) (locY + radius),
		(int) (radius * 0.40), 3,
		(int) (radius * 0.10), 9,
		minuteHand);
		hourHand.setHour(12);
		this.setSize(radius * 2, radius * 2);
	}
	// ***************************************************************************//
	public void paint(Graphics g)
	{
		drawClockFace(g);
		minuteHand.draw(g);
		hourHand.draw(g);
	}
	// ***************************************************************************//
	public void drawClockFace(Graphics g)
	{
		g.setFont(new Font(null, Font.PLAIN, clockFontSize));
		FontMetrics f = g.getFontMetrics();
		int a = (int) (Math.cos(Math.toRadians(30)) * radius);
		int b = (int) (Math.cos(Math.toRadians(60)) * radius);
		g.drawOval((int) locX, (int) locY, radius * 2, radius * 2);
		g.drawString("12",
		(int) (locX + radius - (f.stringWidth("12") / 2.0)),
		(int) (locY + f.getFont().getSize() - 1));
		g.drawString("6",
		(int) (locX + radius - (f.stringWidth("6") / 2.0)),
		(int) (locY + (radius * 2)));
		g.drawString("9", (int) locX,
		(int) (locY + radius + (f.getFont().getSize() / 2.0) - 1));
		g.drawString("3", (int) (locX + (radius * 2) - (f.stringWidth("3"))),
		(int) (locY + radius + (f.getFont().getSize() / 2.0) - 1));
		g.drawString("2", (int) (locX + a + radius - (f.stringWidth("2")) + 2),
		(int) (locY + radius - b + (f.getFont().getSize())));
		g.drawString("1", (int) (locX + b + radius - (f.stringWidth("1")) + 2),
		(int) (locY + radius - a + (f.getFont().getSize())));
		g.drawString("4", (int) (locX + a + radius - (f.stringWidth("4")) + 1),
		(int) (locY + radius + b));
		g.drawString("5", (int) (locX + b + radius - (f.stringWidth("5")) + 1),
		(int) (locY + radius + a));
		g.drawString("8", (int) (locX - a + radius - 1),
		(int) (locY + radius + b));
		g.drawString("7", (int) (locX - b + radius - 1),
		(int) (locY + radius + a));
		g.drawString("10", (int) (locX - a + radius - 2),
		(int) (locY + radius - b + (f.getFont().getSize())));
		g.drawString("11", (int) (locX - b + radius - 2),
		(int) (locY + radius - a + (f.getFont().getSize())));
	}
	// ***************************************************************************//
	public int getRadius()
	{
		return radius;
	}
	// ***************************************************************************//
	public void setRadius(int r)
	{
		radius = r;
	}
	// ***************************************************************************//
	public void setHour(int h)
	{
		hourHand.setHour(h);
		repaint();
	}
	// ***************************************************************************//
	public int getHour()
	{
		return hourHand.getHour();
	}
	// ***************************************************************************//
	public void setMinutes(int m)
	{
		minuteHand.setMinutes(m);
		hourHand.update();
		repaint();
	}
	// ***************************************************************************//
	public int getMinutes()
	{
		return minuteHand.getMinutes();
	}
	// ***************************************************************************//
	public String getAMPM()
	{
		return AMPM;
	}
	// ***************************************************************************//
	public void setAMPM(String s)
	{
		AMPM = s;
		repaint();
	}
}
// ***************************************************************************//
// ***************************************************************************//
class ClockHand implements Serializable
{
	protected int bodyLength;
	protected int bodyWidth;
	protected int arrowHeadLength;
	protected int arrowHeadWidth;
	protected int locX;
	protected int locY;
	protected Polygon hand;
	protected double degreesToRotate;
	// ***************************************************************************//
	public ClockHand(int x, int y, int blength,
	int bwidth, int alength, int awidth)
	{
		locX = x;
		locY = y;
		bodyLength = blength;
		bodyWidth = bwidth;
		arrowHeadLength = alength;
		arrowHeadWidth = awidth;
		degreesToRotate = 0;
		createPolygon();
	}
	// ***************************************************************************//
	protected void createPolygon()
	{
		hand = new Polygon();
		hand.addPoint((int) (locX + (bodyWidth / 2.0)), locY);
		hand.addPoint((int) (locX - (bodyWidth / 2.0)), locY);
		hand.addPoint((int) (locX - (bodyWidth / 2.0)), locY - bodyLength);
		hand.addPoint((int) (locX - (arrowHeadWidth / 2.0)), locY - bodyLength);
		hand.addPoint(locX, locY - bodyLength - arrowHeadLength);
		hand.addPoint((int) (locX + (arrowHeadWidth / 2.0)), locY - bodyLength);
		hand.addPoint((int) (locX + (bodyWidth / 2.0)), locY - bodyLength);
	}
	// ***************************************************************************//
	public void draw(Graphics g)
	{
		Polygon p = new Polygon();
		PathIterator path;
		double cord[] = new double[6];
		path = hand.getPathIterator(
		AffineTransform.getRotateInstance(
		Math.toRadians(degreesToRotate), locX, locY));
		while (!path.isDone() &&
		(path.currentSegment(cord) != PathIterator.SEG_CLOSE))
		{
			p.addPoint((int) cord[0], (int) cord[1]);
			path.next();
		}
		g.fillPolygon(p);
	}
	// ***************************************************************************//
	public void setDegreesToRotate(double degrees)
	{
		degreesToRotate = degrees;
	}
}
// ***************************************************************************//
// ***************************************************************************//
class MinuteHand extends ClockHand
{
	protected int minutes;
	// ***************************************************************************//
	public MinuteHand(int x, int y, int blength,
	int bwidth, int alength, int awidth)
	{
		super(x, y, blength, bwidth, alength, awidth);
		minutes = 0;
	}
	// ***************************************************************************//
	public void setMinutes(int m)
	{
		minutes = ((m >= 60) || (m < 0)) ? 0 : m;
		setDegreesToRotate(minutes * 6.0);
	}
	// ***************************************************************************//
	public int getMinutes()
	{
		return minutes;
	}
}
// ***************************************************************************//
// ***************************************************************************//
class HourHand extends ClockHand
{
	protected int hour;
	protected MinuteHand minuteHand;
	// ***************************************************************************//
	public HourHand(int x, int y, int blength,
	int bwidth, int alength, int awidth,
	MinuteHand mh)
	{
		super(x, y, blength, bwidth, alength, awidth);
		hour = 0;
		minuteHand = mh;
		update();
	}
	// ***************************************************************************//
	public void update()
	{
		setDegreesToRotate((hour * 30.0) +
		(minuteHand.getMinutes() * 0.5));
	}
	// ***************************************************************************//
	public void setHour(int h)
	{
		hour = ((h > 12) || (h < 1)) ? 12 : h;
		update();
	}
	// ***************************************************************************//
	public int getHour()
	{
		return hour;
	}
}
// ***************************************************************************//
// ***************************************************************************//
class CyclingSpinnerListModel extends SpinnerListModel
{
	Object firstValue, lastValue;
	// ***************************************************************************//
	public CyclingSpinnerListModel(Object[] values)
	{
		super(values);
		firstValue = values[0];
		lastValue = values[values.length - 1];
	}
	// ***************************************************************************//
	public Object getNextValue()
	{
		Object value = super.getNextValue();
		if (value == null)
		{
			value = firstValue;
		}
		return value;
	}
	// ***************************************************************************//
	public Object getPreviousValue()
	{
		Object value = super.getPreviousValue();
		if (value == null)
		{
			value = lastValue;
		}
		return value;
	}
}
