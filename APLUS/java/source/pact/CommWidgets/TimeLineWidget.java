/*
 * TimeLineWidget.java
 *
 * Created on November 24, 2003, 1:32 PM
 */
package pact.CommWidgets;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JFrame;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;
/**
 * @author Ethan Croteau
 */
public class TimeLineWidget extends JCommWidget
implements java.awt.event.ActionListener {
	protected TimeLine timeline;
	protected boolean studentMode = false;
	// ***************************************************************************//
	/** Creates a new instance of TimeLineWidget */
	public TimeLineWidget() {
		// This method is where we set up the GUI
		super();
		timeline = new TimeLine(0, 0, 0, 0);
		timeline.setSize(624, 45);
		this.add(timeline);
	}
	// ***************************************************************************//
	public void setStudentMode(boolean b)
	{
		studentMode = b;
		if (studentMode)
		{
			timeline.setVisible(false);
		}
	}
	// ***************************************************************************//
	public MessageObject getDescriptionMessage() {
		if (!initialize(getController())) {
			trace
					.out(
							5,
							this,
							"ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}
		MessageObject mo = MessageObject.create("InterfaceDescription");
		mo.setVerb("SendNoteProperty");
		mo.setProperty("WidgetType", "TimeLineWidget");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));
		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();
		if (deftemplates != null)
			mo.setProperty("jessDeftemplates", deftemplates);
		if (instances != null)
			mo.setProperty("jessInstances", instances);
		serializeGraphicalProperties(mo);
		// hide timeline when start state is created
		timeline.setVisible(false);
		return mo;
	}
	// ***************************************************************************//
	public Vector createJessDeftemplates() {
		Vector deftemplates = new Vector();
		String deftemplateStr1 = "(deftemplate timeline (slot name)"
		+ " (slot startHour) (slot startMinutes) (slot startAMPM)"
		+ " (slot endHour) (slot endMinutes) (slot endAMPM)"
		+ " (multislot intervals))";
		deftemplates.add(deftemplateStr1);
		String deftemplateStr2 = "(deftemplate interval (slot name)"
		+ " (slot startHour) (slot startMinutes) (slot startAMPM)"
		+ " (slot endHour) (slot endMinutes) (slot endAMPM))";
		deftemplates.add(deftemplateStr2);
		return deftemplates;
	}
	// ***************************************************************************//
	public Vector createJessInstances() {
		Vector instances = new Vector();
		String instance = "(assert (timeline (name " + getCommName() + ")))";
		instances.add(instance);
		return instances;
	}
	// ***************************************************************************//
	public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
	}
	// ***************************************************************************//
	public void doCorrectAction(String selection, String action, String input) {
	}
	// ***************************************************************************//
	public void doIncorrectAction(String selection, String input) {
	}
	// ***************************************************************************//
	public void doInterfaceAction(String selection, String action, String input) {

		if (action.equals("MakeVisible"))
			timeline.setVisible(true);
		else if (action.equalsIgnoreCase(SET_VISIBLE))
			timeline.setVisible(Boolean.parseBoolean(input));
		else if (action.equals("SetStartTime") || action.equals("SetEndTime"))
		{
			String[] time = input.split("-");
			int hour = new Integer(time[0]).intValue();
			int minutes = new Integer(time[1]).intValue();
			if (time[2].equals("PM"))
				hour += 12;
			if (action.equals("SetStartTime"))
				timeline.setStartTime(hour, minutes);
			else
				timeline.setEndTime(hour, minutes);
		}
	}
	// ***************************************************************************//
	public void doLISPCheckAction(String selection, String input) {
	}
	// ***************************************************************************//
	public String getCommNameToSend() {
		return getCommName();
	}
	// ***************************************************************************//
	public Object getValue() {
		return null;
	}
	// ***************************************************************************//
	public boolean isChangedFromResetState() {
		return false;
	}
	// ***************************************************************************//
	public void reset(TutorController controller) {
		// hide timeline when a new problem is loaded
		timeline.setVisible(false);
	}
	// ***************************************************************************//
	public void makeVisible() {
		// show timeline
		timeline.setVisible(true);
		timeline.repaint();
	}
	// ***************************************************************************//
	public boolean isVisible() {
		return (timeline.isVisible());
	}
	// ***************************************************************************//
	public void setStartTime(int start, int end) {
		timeline.setStartTime(start, end);
	}
	// ***************************************************************************//
	public void setEndTime(int start, int end) {
		timeline.setEndTime(start, end);
	}
	// ***************************************************************************//
	public void addInterval(int startHour, int startMinutes, int endHour,
			int endMinutes) {
		timeline.addInterval(startHour, startMinutes, endHour, endMinutes);
		timeline.repaint();
	}
	// ***************************************************************************//
	public void timelineClear()
	{
		timeline.timelineClear();
	}
	// ***************************************************************************//
	public void sendValue() {
	}
	// ***************************************************************************//
	/**
	 * to perform unit testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TimeLineWidget tlw = new TimeLineWidget();
		JFrame f = new JFrame();
		f.addWindowListener(new java.awt.event.WindowAdapter()
		{
			// handle close window "X" button
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				System.exit(0);
			};
		});
		f.getContentPane().add(tlw);
		f.pack();
		f.show();
	}
	public void mousePressed(MouseEvent e) {
	}
}
// ***************************************************************************//
// ***************************************************************************//
class TimeLine extends Canvas
{
	/** X position for timeline */
	protected int locX;
	/** Y position for timeline */
	protected int locY;
	/** timeline length=12*24, 12 5-minute intervals and 24 hours in day */
	protected int length = 12 * 24;
	/** font size for numbers displayed on timeline */
	protected int timeLineFontSize;
	/** Starting hour for timeline */
	protected int startHour = 0;
	/** Starting minutes for timeline */
	protected int startMinutes = 0;
	/** Ending hour for timeline */
	protected int endHour = 0;
	/** Ending minutes for timeline */
	protected int endMinutes = 0;
	private ArrayList intervals = new ArrayList();
	private Image greenArrow;
	private Image redArrow;
	// ***************************************************************************//
	public TimeLine()
	{
		locX = 24;
		locY = 20;
		timeLineFontSize = 10;
		// add start and end arrows
		MediaTracker media = new MediaTracker(this);
		greenArrow = Toolkit.getDefaultToolkit().getImage(".\\green_arrow.gif");
		redArrow = Toolkit.getDefaultToolkit().getImage(".\\red_arrow.gif");
		media.addImage(greenArrow, 0);
		media.addImage(redArrow, 0);
		try
		{
			media.waitForID(0);
		} catch (Exception e) {
		}
	}
	// ***************************************************************************//
	public TimeLine(int startHour, int startMinutes, int endHour, int endMinutes)
	{
		this();
		this.startHour = startHour;
		this.startMinutes = startMinutes;
		this.endHour = endHour;
		this.endMinutes = endMinutes;
	}
	// ***************************************************************************//
	public void setStartTime(int startHour, int startMinutes)
	{
		this.startHour = startHour;
		this.startMinutes = startMinutes;
		this.repaint();
	}
	// ***************************************************************************//
	public void setEndTime(int endHour, int endMinutes)
	{
		this.endHour = endHour;
		this.endMinutes = endMinutes;
		this.repaint();
	}
	// ***************************************************************************//
	public void paint(Graphics g)
	{
		drawTimeLine(g);
		drawStartEnd(g);
		drawIntervals(g);
	}
	// ***************************************************************************//
	public void drawTimeLine(Graphics g)
	{
		// draw timeline
		for (int i = locY - 3; i <= locY + 3; i++)
		{
			g.drawLine(locX, i, length * 2 + locX, i);
		}
		// draw hour ticker marks
		for (int i = locX; i <= length * 2 + locX; i += 24)
		{
			g.drawLine(i, locY - 10, i, locY + 10);
		}
		g.setFont(new Font(null, Font.PLAIN, timeLineFontSize));
		FontMetrics f = g.getFontMetrics();
		for (int i = locX; i <= length * 2 + locX; i += 24)
		{
			String s = null;
			if (((i / 24) - 1) > 12)
				s = Integer.toString((i / 24) - 13);
			else
				s = Integer.toString((i / 24) - 1);
			if (s.equals("0"))
				s = new String("12am");
			if (i == (length + 24))
				s = new String("12pm");
			if (i == length * 2 + 24)
				s = new String("12am");
			g.drawString(s,
			(int) (i - (f.stringWidth(s) / 2.0)),
			(int) (locY - f.getFont().getSize() - 2));
		}
	}
	// ***************************************************************************//
	public void drawStartEnd(Graphics g)
	{
		// trace.out("drawStartEnd");
		g.setColor(Color.gray);
		// starting time position before ending time position
		if (((startHour == endHour) && (startMinutes < endMinutes)) ||
		(startHour < endHour))
		{
			// draw inactive region upto startHour and startMinutes
			for (int i = locY - 3; i <= locY + 3; i++)
			{
				g.drawLine(locX, i, locX + (startHour * 24)
						+ (startMinutes / 5 * 2), i);
			}
			// draw inactive region after endHour and endMinutes
			for (int i = locY - 3; i <= locY + 3; i++)
			{
				g.drawLine(locX + (endHour * 24) + (endMinutes / 5 * 2), i,
						length * 2 + locX, i);
			}
		}
		// ending time position before starting time position
		if (((startHour == endHour) && (endMinutes < startMinutes)) ||
		(endHour < startHour))
		{
			for (int i = locY - 3; i <= locY + 3; i++)
			{
				g.drawLine(locX + (endHour * 24) + (endMinutes / 5 * 2), i,
				locX + (startHour * 24) + (startMinutes / 5 * 2), i);
			}
		}
		g.drawImage(redArrow, (locX - 3) + (endHour * 24)
				+ (endMinutes / 5 * 2), locY + 10, this);
		g.drawImage(greenArrow, (locX - 3) + (startHour * 24)
				+ (startMinutes / 5 * 2), locY + 10, this);
	}
	// ***************************************************************************//
	public void drawIntervals(Graphics g)
	{
		// trace.out("drawIntervals");
		g.setColor(Color.red);
		for (int j = 0; j < intervals.size(); j++)
		{
			Interval interval = (Interval) intervals.get(j);
			// starting time position before ending time position
			if (((interval.getStartHour() == interval.getEndHour()) &&
			(interval.getStartMinutes() < interval.getEndMinutes())) ||
			(interval.getStartHour() < interval.getEndHour()))
			{
				for (int i = locY - 3; i <= locY + 3; i++)
				{
					g.drawLine(locX + (interval.getStartHour() * 24) +
					(interval.getStartMinutes() / 5 * 2), i,
					locX + (interval.getEndHour() * 24) +
					(interval.getEndMinutes() / 5 * 2), i);
				}
			}
			// ending time position before starting time position
			if (((interval.getStartHour() == interval.getEndHour()) &&
			(interval.getEndMinutes() < interval.getStartMinutes())) ||
			(interval.getEndHour() < interval.getStartHour()))
			{
				for (int i = locY - 3; i <= locY + 3; i++)
				{
					g.drawLine(locX + (interval.getStartHour() * 24) +
					(interval.getStartMinutes() / 5 * 2), i, length * 2 + locX,
							i);
				}
				for (int i = locY - 3; i <= locY + 3; i++)
				{
					g.drawLine(locX, i, locX + (interval.getEndHour() * 24) +
					(interval.getEndMinutes() / 5 * 2), i);
				}
			}
		}
	}
	// ***************************************************************************//
	public void addInterval(int startHour, int startMinutes, int endHour,
			int endMinutes)
	{
		Interval i = new Interval(startHour, startMinutes, endHour, endMinutes);
		intervals.add(i);
	}
	// ***************************************************************************//
	public void timelineClear()
	{
		intervals.clear();
		repaint();
	}
}
// ***************************************************************************//
// ***************************************************************************//
class Interval
{
	/** Starting hour for interval */
	private int startHour = 0;
	/** Starting minutes for interval */
	private int startMinutes = 0;
	/** Ending hour for interval */
	private int endHour = 0;
	/** Ending minutes for interval */
	private int endMinutes = 0;
	public Interval(int startHour, int startMinutes, int endHour, int endMinutes)
	{
		this.startHour = startHour;
		this.startMinutes = startMinutes;
		this.endHour = endHour;
		this.endMinutes = endMinutes;
	}
	public int getStartHour()
	{
		return startHour;
	}
	public int getStartMinutes()
	{
		return startMinutes;
	}
	public int getEndHour()
	{
		return endHour;
	}
	public int getEndMinutes()
	{
		return endMinutes;
	}
}
