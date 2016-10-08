package pact.CommWidgets;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.TextEvent;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.Highlighter;

import pact.CommWidgets.event.StudentActionEvent;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.client.HintMessagesManagerForClient;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.miss.MissControllerExternal;

//////////////////////////////////////////////////////
/**
 * A table (of text fields) which will automatically send the user's input to
 * Lisp whenever a user changes the value of a field.
 */
// ////////////////////////////////////////////////////
public class JCommTable extends JCommWidget implements FocusListener,
        KeyListener {

//	private static final String UPDATE_INVISIBLE = "UpdateInVisible";
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -6167079835123103907L;

	private static final int INITIAL_NO_ROWS = 4;

    private static final int INITIAL_NO_COLS = 4;

    private static final int DEFAULT_CELL_WIDTH = 40;

    private static final int DEFAULT_CELL_HEIGHT = 20;

    // The following constants defines the status of TableExpressionCell.
    // TableExpressionCell has two additional fields besides all the
    // textfield properties. It can display images within itself for
    // correct answers. And it can display expression tooltip.
    // The following constants defines the status of these two features.
    private static final int CELL_FORMAT_DO_NONE = 0;

    private static final int CELL_DISPLAY_TOOLTIP_ONLY = 1;

    private static final int CELL_DISPLAY_CELL_IMAGE_ONLY = 2;

    private static final int CELL_FORMAT_DO_BOTH = 3;

    protected boolean tableCreated, tableInitialized;

    // Bapuji (from CarnegieLearning) changed TableCell to
    // TableExpressionCell which displays a window with formatted
    // mathematical expressions.

    // protected TableCell[][] textField;
    protected TableExpressionCell[][] cells;

    public TableExpressionCell[][] getCells() {
        return cells;
    }

    protected int rows, columns, spacing, currentRow, currentColumn;

    protected String resetValue = "";

    protected Font startFont;

    protected static int tablesCreated;

    private boolean highlighted;

    private int highlightRow;

    private int highlightCol;

    // -sanket
    Vector selectedCellsList = new Vector();

    Vector selectedValues = new Vector();

    JFrame selectedCellsFrame;

    JList nameList, valuesList;

    DefaultListModel nameModel;

    DefaultListModel valuesModel;

    // -sanket

    // TableLayout variables - Declared by Bapuji (Carnegie Learning).
    protected double row_heights[], col_widths[];

    protected TableLayout tableLayout;

    // **Be careful when changing the name of tableCellFeatures**
    // There is some problem in NetBeans.
    // NetBeans generates code for form view editor ptoperties in
    // alphabetical order. setRows() and setColumns() builds all the
    // table cells. So expression tooltip status is reset.
    // If name ToolTipStatusForCells comes before the property names,
    // "rows" and "columns", expression tooltip values are reset at runtime
    // ignoring the user values.
    // So it is better not to change its name. If you have to make sure it
    // comes after properties "rows" and "columns" in alphabetical order.
    // or if any additional logic is implemented to take care of this
    // situation. - Bapuji V (Carnegie Learning).
    protected String tableCellFeatures[];

    // tableCellSize is used to save the default or user set cell size at
    // design time.
    private Dimension tableCellSize;

    private boolean insideAdjustSize;

    // ---------------------------------------------- //

    public static int getTableCount() {
        return tablesCreated;
    }

    // ////////////////////////////////////////////////////
    /**
     * Constructor
     */
    // ////////////////////////////////////////////////////
    public JCommTable() {
        actionName = UPDATE_TABLE;
        spacing = 1;
        setRows(JCommTable.INITIAL_NO_ROWS);
        setColumns(JCommTable.INITIAL_NO_COLS);
        addFocusListener(this);
        tablesCreated++;

        // sanket@cs.wpi.edu
        constructSelectedCellsFrame();
        // sanket@cs.wpi.edu

    }

    public TableCell getCell(int row, int col) {
        return cells[row][col];
    }

	private void sendSelectedCells() {

		if (getUniversalToolProxy() == null) {
			JOptionPane
					.showMessageDialog(
							null,
							"Warning: The Connection to the Production System should be made before sending the selection elements. \n Open the Behavior Recorder to establish a connection.",
							"Warning", JOptionPane.WARNING_MESSAGE);
		} else {

			// construct the Comm Message Containing the cell selections
			MessageObject mo = MessageObject.create("SendSelectedElements");
			mo.setVerb("SendSelectedElements");
			mo.setProperty("SelectedElements", selectedCellsList);
			mo.setProperty("SelectedElementsValues", selectedValues);

			getUniversalToolProxy().sendMessage(mo);

		}

	}

    private void constructSelectedCellsFrame() {
        selectedCellsFrame = new JFrame();

        nameModel = new DefaultListModel();
        valuesModel = new DefaultListModel();
        nameList = new JList(nameModel);
        valuesList = new JList(valuesModel);

        selectedCellsFrame.getContentPane().setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BorderLayout());
        namePanel.add(new JLabel("Name"), BorderLayout.NORTH);
        namePanel.add(nameList, BorderLayout.CENTER);
        namePanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel valuesPanel = new JPanel();
        valuesPanel.setLayout(new BorderLayout());
        valuesPanel.add(new JLabel("Value"), BorderLayout.NORTH);
        valuesPanel.add(valuesList, BorderLayout.CENTER);
        valuesPanel.setBorder(BorderFactory.createEtchedBorder());

        JButton okBtn = new JButton("Send");
        okBtn.addActionListener(new ActionListener() {
            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent arg0) {
                // send the selected cells and values to ESE_Frame
                sendSelectedCells();
                selectedCellsFrame.hide();
            }
        });
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent arg0) {
                selectedCellsFrame.hide();
            }

        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);

        centerPanel.setLayout(new GridLayout(1, 1));
        centerPanel.add(namePanel);
        centerPanel.add(valuesPanel);

        selectedCellsFrame.getContentPane().add(centerPanel,
                BorderLayout.CENTER);
        selectedCellsFrame.getContentPane().add(btnPanel, BorderLayout.SOUTH);
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    protected boolean initialize() {
        if (!super.initialize(getController())) {
            trace.err("can't initializer super");
            return false;
        }
        if (tableInitialized)
            return true;
        // We must add a widget name for each cell in this table, since
        // the selection encodes both the table name and the cell name
        for (int j = 0; j < columns; j++)
            for (int i = 0; i < rows; i++) {
                addCommWidgetName(commName + "_C" + (j + 1) + "R" + (i + 1));
                addCommListener(i, j);
            }
        if (getController().isShowWidgetInfo())
            setToolTipWidgetInfo();
        tableInitialized = true;
        return true;
    }

    // ////////////////////////////////////////////////////
    /**
     * This is old version of createTextFields(). Now there is new version below
     * this code that creates JCommTable with TableExpressionCells and uses
     * TableLayout as layout manager.
     */
    // ////////////////////////////////////////////////////
    /*
     * public void createTextFields() { this.removeAll(); textField = null;
     * 
     * textField = new TableCell[rows][];
     * 
     * GridLayout g = new GridLayout(rows, columns); g.setVgap(spacing);
     * g.setHgap(spacing);
     * 
     * setLayout(g); for (int i = 0; i < rows; i++) textField[i] = new
     * TableCell[columns];
     * 
     * for (int i = 0; i < rows; i++) { for (int j = 0; j < columns; j++) {
     * textField[i][j] = new TableCell(); textField[i][j].addKeyListener(this);
     * textField[i][j].addFocusListener(this); textField[i][j].row = i;
     * textField[i][j].column = j; //textField[i][j].setHorizontalAlignment(0); //
     * Bapuji (CL) moved this line to TableCell constructor //
     * textField[i][j].setDocument(new JCommDocument());
     * textField[i][j].setFont(getFont());
     * textField[i][j].setToolTipText(getToolTipText());
     * 
     * textField[i][j].oldHighlighter = textField[i][j].getHighlighter();
     * 
     * textField[i][j].addMouseListener(new MouseListenerClass());
     * add(textField[i][j]); } } tableCreated = true; originalBorder =
     * textField[0][0].getBorder(); }
     */
    /**
     * In this new version of createTextFields(), TableLayout has been used to
     * arrange textfields in table format. Initially JCommTable panel is
     * divided into rows and columns. And textfields are placed. Horizontal and
     * Vertical spacing is also done by creating additional columns and rows
     * respectively. By using this method, setSpacing() is easily manipulated
     * with recreating all the textfields. - Bapuji (Carnegie Learning)
     */
    public void createTextFields() {
        // Do nothing for incorrect row and column values.
        if (rows <= 0 || columns <= 0)
            return;
        removeAll();
        cells = new TableExpressionCell[rows][columns];
        tableCellFeatures = new String[rows * columns];
        row_heights = new double[rows];
        col_widths = new double[columns];

        // Save default cell size.
        tableCellSize = new Dimension(JCommTable.DEFAULT_CELL_WIDTH,
                JCommTable.DEFAULT_CELL_HEIGHT);

        tableLayout = new TableLayout();

        // Reset table cell sizes
        resetTableCellSizes();

        // ****** Set layout to panel ****** //

        setSpacing(spacing);
        setLayout(tableLayout);

        TableLayoutConstraints cell_constraints = new TableLayoutConstraints(0,
                0, 0, 0, TableLayout.FULL, TableLayout.FULL);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j] = new TableExpressionCell(this, i, j);

                tableCellFeatures[i * columns + j] = String.valueOf(cells[i][j]
                        .getCellFormatStatus());

                /*
                 * if(textField[i][j].isDisplayToolTip())
                 * tableCellFeatures[i*columns+j] = "t" + CELL_FORMAT_DO_NONE;
                 * else tableCellFeatures[i*columns+j] = "f" +
                 * CELL_FORMAT_DO_NONE;
                 */

                /*
                 * -- Bapuji (Carnegie Learning) moved setDocument() line to --
                 * TableCell constructor. This line needs to be kept in -- that
                 * constructor because expression tooltip (a feature -- to
                 * display formatted mathematical expressions in a window --
                 * window as user types in) implements DocumentListener and --
                 * this document is attched to Expression ToolTip at the time --
                 * of instantiation.
                 */
                // textField[i][j].setDocument(new JCommDocument());
                cells[i][j].setFont(getFont());
                cells[i][j].setToolTipText(getToolTipText());
                cells[i][j].oldHighlighter = cells[i][j].getHighlighter();

                cells[i][j].addMouseListener(new MouseListenerClass());
                cells[i][j].addKeyListener(this);
                cells[i][j].addFocusListener(this);

                // ** Set table layout constraints ** //
                cell_constraints.row1 = i;
                cell_constraints.row2 = i;
                cell_constraints.col1 = j;
                cell_constraints.col2 = j;

                add(cells[i][j], cell_constraints);
            }
        }
        // tableCreated is never been used. Is it needed?
        tableCreated = true;
        originalBorder = cells[0][0].getBorder();
//        setBorder(originalBorder);

        this.adjustPanelSize();
    }

    /**
     * This method resets table cell sizes.
     */
    public void resetTableCellSizes() {
        // Set row heights.
        for (int i = 0; i < row_heights.length; i++)
            row_heights[i] = tableCellSize.height;

        // Set column widths.
        for (int i = 0; i < col_widths.length; i++)
            col_widths[i] = tableCellSize.width;

        tableLayout.setColumn(this.col_widths);
        tableLayout.setRow(this.row_heights);
    }

    public void setBounds(int x, int y, int width, int height) {
        // This condition is needed in NetBeans. Otherwise JCommTable's size
        // becomes (0,0)
        // when JCommTable panel is moved manually in NetBeans form view.
        if (width != 0 && height != 0) {
            // setSize(), internally, calls setBounds(). setSize() is called
            // in adjustPanelSize(). so we needs to just call super.setBounds()
            // without calculating anything.
            // 'insideAdjustSize' is set to true in adjustPanelSize().
            if (insideAdjustSize) {
                super.setBounds(x, y, width, height);
            } else {
                // Calculate column width and row height for new wodth and
                // height.
                int columnWidth = (width - (columns - 1)
                        * tableLayout.getHGap())
                        / columns;
                int rowHeight = (height - (rows - 1) * tableLayout.getVGap())
                        / rows;

                // tableCellSize is used when resetting table cell sizes.
                tableCellSize.width = columnWidth;
                tableCellSize.height = rowHeight;
                this.resetTableCellSizes();

                Dimension prefSize = tableLayout.preferredLayoutSize(this);
                super.setBounds(x, y, prefSize.width, prefSize.height);
                tableLayout.layoutContainer(this);
            }
        }
    }

    /**
     * SetCellSize() sets new size for the cell requested by row and col values.
     * It also calls adjustPanelSize() to ajust layout and calls repaint() on
     * required cells.
     * 
     * @param row -
     *            row of the cell.
     * @param col -
     *            column of the cell.
     * @param cellsize -
     *            new size for the requested cell.
     */
    public void setCellSize(int row, int col, Dimension cellsize) {
        if (tableLayout != null) {
            // set size only if new cell size is different.
            if (!cellsize.equals(getCellSize(row, col))) {
                tableLayout.setRow(row, cellsize.height);
                tableLayout.setColumn(col, cellsize.width);

                // request to update layout
                tableLayout.layoutContainer(this);

                row_heights = tableLayout.getRow();
                col_widths = tableLayout.getColumn();

                adjustPanelSize();

                // due to change in size, other cells of this row may
                // need to be aligned. Alignment is done only for cells
                // having image painted inside the textfield.
                for (int r = 0; r < rows; r++) {
                    // Do not call alignImage() on currently handled cell (row,
                    // col)
                    // this cell might not be ready for painting yet.
                    if (r != row)
                        cells[r][col].alignImage();
                }

                // due to change in size, other cells of this column
                // needs to be aligned. Alignment is done only for cells
                // having image painted inside the textfield.
                for (int c = 0; c < columns; c++) {
                    // Do not call alignImage() on currently handled cell (row,
                    // col)
                    // this cell might not be ready for painting yet.
                    if (c != col)
                        cells[row][c].alignImage();
                }
            }
        }
    }

    public Dimension getCellSize(int row, int col) {
        int width, height;
        width = (int) tableLayout.getColumn(col);
        height = (int) tableLayout.getRow(row);
        return new Dimension(width, height);
    }

    /**
     * adjustPanelSize() takes care of panel size and layout.
     */
    public void adjustPanelSize() {
        Dimension prefSize = tableLayout.preferredLayoutSize(this);
        insideAdjustSize = true;
        this.setSize(prefSize);
        insideAdjustSize = false;
        tableLayout.layoutContainer(this);
    }

    public void addMouseListener(MouseListener m) {
        super.addMouseListener(m);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cells[i][j] == null)
                    continue;
                cells[i][j].addMouseListener(m);
            }
        }
    }

    public boolean getLock(String selection) {
        Vector v = getRowColVector(selection);
        int row = ((Integer) v.elementAt(0)).intValue();
        int col = ((Integer) v.elementAt(1)).intValue();
        if (trace.getDebugCode("table"))
    		trace.printStack("table", "getLock("+row+","+col+") document.locked "+
    				((JCommDocument) cells[row][col].getDocument()).locked);
        return ((JCommDocument) cells[row][col].getDocument()).locked;
    }

    // ////////////////////////////////////////////////////
    /**
     * Add the given cell to the comm listener to receive comm messages
     */
    // ////////////////////////////////////////////////////
    public void addCommListener(int row, int col) {
        String componentName = commName + "_C" + Integer.toString(col + 1)
                + "R" + Integer.toString(row + 1);
    	addCommListener(componentName, getController());
    }

    /**
     * Returns a comm message which describes this interface element.
     * @see JCommWidget#getDescriptionMessage(String)
     * @param widgetName name as known to caller
     * @return null if widgetName not same as {@link #commName}; else
     *         build InterfaceDescription msg that describes all cells, columns
     */
	public MessageObject getDescriptionMessage(String widgetName) {
		if (trace.getDebugCode("comm"))
			trace.out("comm", "widget name = " + widgetName
					+ " comm name = " + commName);
		if (widgetName == null || !(widgetName.equalsIgnoreCase(commName))) {
			return null;
		}
		MessageObject mo = MessageObject.create("InterfaceDescription");
		if (!initialize()) {
			trace
					.out("ERROR!: Can't create Comm message because can't initialize."
							+ "  Returning empty comm message");
			return null;
		}
		mo.setVerb("SendNoteProperty");

		mo.setProperty("WidgetType", "JCommTable");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));
		mo.setProperty("Rows", new Integer(rows));
		mo.setProperty("Columns", new Integer(columns));

		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();

		if (deftemplates != null)
			mo.setProperty("jessDeftemplates", deftemplates);

		if (instances != null)
			mo.setProperty("jessInstances", instances);

		serializeGraphicalProperties(mo);

		trace.out("comm", "mo = " + mo);
		return mo;
	}

    // sanket@cs.wpi.edu
    /**
     * This method creates the jess deftemplates for the table widget
     * 
     * @return - returns a vector of deftemplates for the table widget the
     *         deftemplates vector contains deftemplates for table, column and
     *         cell
     */
    public Vector createJessDeftemplates() {
        Vector deftemplates = new Vector();
        String tableTemplate = "(deftemplate table (slot name) (multislot columns))";
        String columnTemplate = "(deftemplate column (slot name) (multislot cells) (slot position) (slot description))";
        String cellTemplate = "(deftemplate cell (slot name) (slot value) (slot description) (slot row-number) (slot column-number))";
        deftemplates.add(tableTemplate);
        deftemplates.add(columnTemplate);
        deftemplates.add(cellTemplate);
        return deftemplates;
    }

    /**
     * this method is for creating the instances corresponding to the table
     * widget In particular this method demonstrates the technique to get the
     * references to the facts and store them as the slot values of other facts.
     * 
     * @return
     */
    public Vector createJessInstances() {
        // System.out.println("createJessInstance: " + getCommName());
        Vector instances = new Vector();
        // columns contains list of variables for the columns
        String columns = "";
        for (int j = 1; j <= getColumns(); j++) {
            // cells contains a list of variables for the cells of a particular
            // column
            String cells = "";
            for (int i = 1; i <= rows; i++) {
                String cellName = getCommName() + "_C" + j + "R" + i;
                // Fri May 27 15:12:36 2005: Noboru
                // Added "row-number"
                String str = "(bind ?" + cellName + " (assert (cell (name "
                        + cellName + ") (row-number " + i + ") (column-number "
                        + j + "))))";
                cells += "?" + cellName + " ";
                instances.add(str);
            }
            String columnName = getCommName() + "_Column" + j;
            String columnStr = "(bind ?" + columnName
                    + "(assert (column (name " + columnName + ") (cells "
                    + cells + ") (position " + j + "))))";
            instances.add(columnStr);
            columns += "?" + columnName + " ";
        }
        String tableName = getCommName();
        String tableInstance = "(assert (table (name " + tableName
                + ") (columns " + columns + ")))";
        instances.add(tableInstance);
        return instances;
    }

    // ////////////////////////////////////////////////////
    /**
     * Used to process an InterfaceAction message
     */
    // ////////////////////////////////////////////////////
	public void doInterfaceAction(String selection, String action, String input) {

		if (action.equalsIgnoreCase("UpdateTable")) {
			Vector v = getRowColVector(selection);
			int row = ((Integer) v.elementAt(0)).intValue();
			int col = ((Integer) v.elementAt(1)).intValue();

			((JCommDocument) cells[row][col].getDocument()).locked = false;  // so can setText()
			cells[row][col].setText(input);

	    	if (trace.getDebugCode("table"))
	    		trace.out("table", "doInterfaceAction("+row+","+col+") input "+input);

			cells[row][col].previousValue = cells[row][col].getText();
			// Thu Oct 06 18:29:42 2005: Noboru
			// The cell value must be locked (no matter what) when Sim. St. is
			// up
			if (getController().isStartStateInterface()|| getController().isSimStudentMode())
				((JCommDocument) cells[row][col].getDocument()).locked = true;
			return;
		} else if (action.equalsIgnoreCase(SET_VISIBLE)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Set InVisible: " + input);
			if (input.equalsIgnoreCase("true"))
				setInvisible(true);
			else
				setInvisible(false);
			setVisible(!isInvisible());
			// setInvisible(input);
			return;
		}
		throw new Error("**Error**: don't know interface action " + action);
	}

    // ////////////////////////////////////////////////////
    /**
     */
    // ////////////////////////////////////////////////////
	public void doCorrectAction(String selection, String action, String input) {
		
	
		if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		} else {
			if (trace.getDebugCode("inter")) trace.out("inter", "do correct action: input = " + input
					+ " selection = " + selection);

			Vector v = getRowColVector(selection);
			int row = ((Integer) v.elementAt(0)).intValue();
			int col = ((Integer) v.elementAt(1)).intValue();

			((JCommDocument) cells[row][col].getDocument()).locked = false;
	    	if (trace.getDebugCode("table"))
	    		trace.out("table", "doCorrectAction("+row+","+col+") setForeground("+correctColor+")");

			cells[row][col].setText(input);

			cells[row][col].setForeground(correctColor);
				
			cells[row][col].oldHighlighter = cells[row][col].getHighlighter();
			cells[row][col].setHighlighter(null);
			cells[row][col].setBackground(backgroundNormalColor);
			
			
			if (correctFont != null)
				cells[row][col].setFont(correctFont);
			if (getController().getUniversalToolProxy().lockWidget()) {
				((JCommDocument) cells[row][col].getDocument()).locked = true;
				// Create image (formatted mathematical expression image) to
				// display
				// for correct answer. Bapuji inserted this line.
				cells[row][col].createImage();
			} else {
				// Fri Oct 07 22:37:55 2005:: Noboru
				// Just for debugging...
				// System.out.println("doCorrectAction: Cell [" + row + "][" +
				// col +
				// "] must be LOCKED!!");
			}
	    	fireStudentAction(new StudentActionEvent(this));
		}
	}

    // ////////////////////////////////////////////////////
    /**
     */
    // ////////////////////////////////////////////////////
    public void doLISPCheckAction(String selection, String input) {
        Vector v = getRowColVector(selection);
        int row = ((Integer) v.elementAt(0)).intValue();
        int col = ((Integer) v.elementAt(1)).intValue();
        cells[row][col].setText(input);
        cells[row][col].setForeground(LISPCheckColor);

        if (getController().getUniversalToolProxy().lockWidget())
            ((JCommDocument) cells[row][col].getDocument()).locked = true;
    }

    // ////////////////////////////////////////////////////
    /**
     */
    // ////////////////////////////////////////////////////
    public void doIncorrectAction(String selection, String input) {
    	Vector v = getRowColVector(selection);
        int row = ((Integer) v.elementAt(0)).intValue();
        int col = ((Integer) v.elementAt(1)).intValue();
        if (trace.getDebugCode("dw")) trace.out("dw", "table.doIncorrectAction("+ selection + "," +
        		input + "): row " + row + ", col " + col + ", incorrect color " + incorrectColor);
        cells[row][col].setText(input);
        cells[row][col].setForeground(incorrectColor);
        cells[row][col].setEditable(true);
        cells[row][col].setHighlighter(cells[row][col].oldHighlighter);
        if (incorrectFont != null)
            cells[row][col].setFont(incorrectFont);

        // Fri Oct 07 22:38:34 2005:: Noboru
        // When Sim. St. is up, every demonstrated step is
        // also model-traced. If the step is not in the
        // model, it gets red hence the cell gets unlocked.
        // However, this cause a trouble in specifying a focus
        // of attention; the author can not double click more
        // than one cell (trying to double click on the 2nd
        // cell makes the 1st cell focus lost). Therefore,
        // this statement must be conditioned.
        if (getController().isSimStudentMode()) {
            // System.out.println("doIncorrectAction: Cell[" + row + "][" + col+
            // "] LOCKED!! @@@@@@@@@");
            ((JCommDocument) cells[row][col].getDocument()).locked = true;
        } else {
            ((JCommDocument) cells[row][col].getDocument()).locked = false;
        }
    	if (trace.getDebugCode("table"))
    		trace.out("table", "doIncorrectAction("+row+","+col+") document.locked "+
    				((JCommDocument) cells[row][col].getDocument()).locked);

        cells[row][col].doIncorrectAction();
    }

    public boolean getLocked(String selection) {
        Vector v = getRowColVector(selection);
        int row = ((Integer) v.elementAt(0)).intValue();
        int col = ((Integer) v.elementAt(1)).intValue();
        if (trace.getDebugCode("table"))
    		trace.printStack("table", "getLocked("+row+","+col+") document.locked "+
    				((JCommDocument) cells[row][col].getDocument()).locked);
        return ((JCommDocument) cells[row][col].getDocument()).locked;
    }

    // ////////////////////////////////////////////////////
    /**
     */
    // ////////////////////////////////////////////////////
    public void setProperty(MessageObject o) {
    }

    // ////////////////////////////////////////////////////
    /**
     */
    // ////////////////////////////////////////////////////
    public void focusGained(FocusEvent e) {
        if (!(e.getComponent() instanceof TableCell))
            return;
        if (e.isTemporary())
            return;
        TableCell cell = (TableCell) e.getComponent();
        
        if (trace.getDebugCode("inter")) {
        	trace.out("inter", "JCommTable: focusGained; current cell C"+
        			currentColumn+"R"+currentRow+", new focus C"+cell.column+"R"+cell.row+
        			", locked "+((JCommDocument) cells[cell.row][cell.column].getDocument()).locked+
        			"; if unlocked, to setForeground("+startColor+")");
//        	try {
//        		Thread.sleep(6*1000);
//        	} catch (InterruptedException ie) {}
        }
        
        if (!((JCommDocument) cells[cell.row][cell.column].getDocument()).locked) {
            cell.previousValue = cell.getText();
//          cells[cell.row][cell.column].setForeground(startColor);  CTAT2870: don't change color after hint
            if (startFont != null)
                cells[cell.row][cell.column].setFont(startFont);
            currentRow = cell.row + 1;
            currentColumn = cell.column + 1;
            super.focusGained(e);
        }
        // if (highlighted)
        // removeHighlight("");
        // trace.out ("dw", " current row = " + currentRow + " current col = " +
        // currentColumn);
        repaint();
    }

    // ////////////////////////////////////////////////////
    /**
     * Called by cells of this table. If a cell loses focus permanently, and the
     * cell's value has changed since it was last sent to lisp, then send the
     * value to lisp.
     */
    // ////////////////////////////////////////////////////
    public void focusLost(FocusEvent e) {
//        trace.out("gusIL", "entered commTable.focusLost(e)");
    	
        if (commName==null)
 	    	commName=this.getName();
    	 
    	 
    	TableCell cell = null;
    	if (e.getComponent() instanceof TableCell)
        	cell = (TableCell) e.getComponent();

    	if (trace.getDebugCode("inter")) trace.out("inter", "JCommTable: focusLost(temp "+e.isTemporary()+
    			"); current cell C"+currentColumn+"R"+currentRow+
    			", new focus "+(cell == null ? "null" : "C"+cell.column+"R"+cell.row)+
				", locked "+((JCommDocument) cells[cell.row][cell.column].getDocument()).locked);

        if (e.isTemporary()) {
            return;
        }
        
        //If focus is lost to the undo button, we don't want to process typed in changes
        //keiser 6/22/2010
        if(e.getOppositeComponent() instanceof JButton)
        {
        	if(((JButton)e.getOppositeComponent()).getActionCommand().equals(MissControllerExternal.UNDO))
        	{
        		return;
        	}
        }
//        trace.out("gusIL", "871");
        if (!(e.getComponent() instanceof TableCell))
            return;
//        trace.out("gusIL", "874");

        Component oppComponent = e.getOppositeComponent();
        if (trace.getDebugCode("inter")) trace.out("inter", "JCommTable: focusLost oppComponent "+oppComponent);
        if (!focusTriggersBackGrading(oppComponent))
        	return;
        
        currentRow = cell.row + 1;
        currentColumn = cell.column + 1;
        if (!((JCommDocument) cells[cell.row][cell.column].getDocument()).locked) {

//            trace.out("gusIL", "entered if");

            if (!cell.getText().trim().equals(""))
                dirty = true;
            else
                dirty = false;

            autoCapitalize(cells[cell.row][cell.column]);
        	
            if (cell.getText().trim().equals("")) {
                dirty = false;
                //always return false followed by noop
                if (getController().isDefiningStartState())
                	getController().setStartStateModified(true); // in case of delete cell value
            }
            if (trace.getDebugCode("inter")) trace.out("inter", "JCommTable: focusLost current cell C"+currentColumn+
                    "R"+currentRow+", dirty "+dirty+", text "+cell.getText());

        
    
       		
            if (dirty==false){
           	 if (getController()!=null && getController().getMissController()!=null &&  getController().getMissController().getSimSt()!=null &&  getController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){
           		 if (getController().getMissController().getSimStPLE()!=null && getController().getMissController().getSimStPLE().getSsCognitiveTutor()!=null){
           			 getController().getMissController().getSimStPLE().getSsCognitiveTutor().removeQuizStep(cell.getCommName());
           		 }
           		 
//          		JCommButton doneButton = (JCommButton) getController().lookupWidgetByName("Done");
//          		if (doneButton!=null){
//           			doneButton.setText("Problem is Solved");
//          		}
           		 	 
           	 }
           	 
           	 
           	 
           	 
           }
            	
            	
            if (dirty){
                if (trace.getDebugCode("inter")) trace.out("inter", "calling JCommWidget.sendValue()");
                sendValue();
            }
            dirty = false;
            
          
           
            
        }
        super.focusLost(e);
    }

    /**
     * Set the contents of the cell to upper case, if
     * {@link UniversalToolProxy#getAutoCapitalize()} or
     * {@link JCommWidget#getAutoCapitalize()} returns true.
     * Does not set {@link JCommWidget#dirty}.
     * @param cell
     */
	private void autoCapitalize(TableCell cell) {
        if (getController().getUniversalToolProxy().getAutoCapitalize() == true
                || getAutoCapitalize() == true) {
        	String text = cell.getText();
            cell.setTextInternal(text.toUpperCase());
        }
	}

	// ////////////////////////////////////////////////////
    /**
     * Called by the sendValue method. Always returns the value of the last cell
     * to get updated by the user.
     */
    // ////////////////////////////////////////////////////
    public Object getValue() {
        if (currentRow < 1 || currentColumn < 1 || currentRow > cells.length
                || currentColumn > cells[currentRow - 1].length)
            return null;
        cells[currentRow - 1][currentColumn - 1].setForeground(startColor);
        return cells[currentRow - 1][currentColumn - 1].getText().trim();
    }

    // ////////////////////////////////////////////////////
    /**
     */
    // ////////////////////////////////////////////////////
    public void textValueChanged(TextEvent e) {
        dirty = true;
    }

    // ////////////////////////////////////////////////////
    /**
     * Return true if any cells are not empty, otherwise false
     */
    // ////////////////////////////////////////////////////
    public boolean isChangedFromResetState() {
    	LinkedList<String> debugList = null;
    	if (trace.getDebugCode("table"))
    		debugList = new LinkedList<String>();
        boolean changedFlag = false;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) {
                if (cells[i][j].getText().equals(resetValue) == false) {
                    ((JCommDocument) cells[i][j].getDocument()).locked = true;
                    changedFlag = true;
                }
            }
        return changedFlag;
    }

    public boolean resetStartStateLock(boolean startStateLock) {
        boolean changedFlag = false;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) {
                if (cells[i][j].getText().equals(resetValue) == false) {
                    ((JCommDocument) cells[i][j].getDocument()).locked = startStateLock;
                    changedFlag = true;
                }
            }
        return changedFlag;
    }
    
    // reset a single cell only
    public void singleCellReset(String selection) {
        Vector v = getRowColVector(selection);
        int row = ((Integer) v.elementAt(0)).intValue();
        int col = ((Integer) v.elementAt(1)).intValue();
        cellReset(row, col);
    }

    private void cellReset(int row, int col) {
    	if (trace.getDebugCode("table"))
    		trace.printStack("table", "cellReset("+row+","+col+") setForeground("+startColor+")");
        ((JCommDocument) cells[row][col].getDocument()).locked = false;
        cells[row][col].setForeground(startColor);
        
        cells[row][col].setText(resetValue);        
        
        cells[row][col].setEditable(true);
        cells[row][col].setHighlighter(cells[row][col].oldHighlighter);
        cells[row][col].setFocusable(true);
        TableExpressionCell r = cells[row][col];
        // call reset on textfield.
        // Bapuji (Carnegire Learning) added this line.
        if (r.isDisplayToolTip()) {
            ((grant.widgets.ToolTip.SwingExprToolTip) r.exprToolTip).attach();
        }
        return;
    }

    // ////////////////////////////////////////////////////
    /**
     * reset the state of this widget to the default state (empty)
     */
    // ////////////////////////////////////////////////////
    public void reset(TutorController controller) {
        initialize();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) {
                cellReset(i, j);
            }
        // Reset the JCommTable's size and layout - Bapuji (Carnegie Learning)
        this.resetTableCellSizes();
        this.adjustPanelSize();
    }

    // ////////////////////////////////////////////////////
    /**
     * Creates a vector of comm messages which describe the current state of
     * this object relative to the start state
     */
    // ////////////////////////////////////////////////////
    public Vector getCurrentState() {
        int oldCurrentRow = currentRow;
        int oldCurrentColumn = currentColumn;
        Vector v = new Vector();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                if (cells[i][j].getText().equals(resetValue) == false) {
                    currentRow = i + 1;
                    currentColumn = j + 1;
                    v.addElement(getCurrentStateMessage());
                }
        currentRow = oldCurrentRow;
        currentColumn = oldCurrentColumn;

        return v;
    }

    // ////////////////////////////////////////////////////
    /**
     */
    // Looks like this method does not need to be overridden.
    // ////////////////////////////////////////////////////
    // This method is taken out by Bapuji (Carnegie Learning)
    /*
     * public void setSize(Dimension d) { super.setSize(d); // Bapuji commented
     * the following 2 lines // if (rows > 0 && columns > 0) //
     * createTextFields(); }
     */

    // ////////////////////////////////////////////////////
    /**
     */
    // ////////////////////////////////////////////////////
    public void keyReleased(KeyEvent e) {
        dirty = true;
        if (e.getKeyCode() == 10) {
            e.getComponent().transferFocus();
        }
    }

    // ////////////////////////////////////////////////////
    /**
     */
    // ////////////////////////////////////////////////////
    public void keyTyped(KeyEvent e) {
    }

    // ////////////////////////////////////////////////////
    /**
     */
    // ////////////////////////////////////////////////////
    public void keyPressed(KeyEvent e) {
    }

    // ////////////////////////////////////////////////////
    /**
     * Set # of rows in table
     */
    // ////////////////////////////////////////////////////
    public void setRows(int rows) {
        if (rows <= 0)
            return;
        this.rows = rows;
        if (columns > 0)
            createTextFields();
    }

    // ////////////////////////////////////////////////////
    /**
     * Get # of rows in table
     */
    // ////////////////////////////////////////////////////
    public int getRows() {
        return rows;
    }

    // ////////////////////////////////////////////////////
    /**
     * set alignment
     */
    // ////////////////////////////////////////////////////
    public void setJustification(int value) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j].setHorizontalAlignment(value);
            }
        }
    }

    // ////////////////////////////////////////////////////
    /**
     * get alignment
     */
    // ////////////////////////////////////////////////////
    public int getJustification() {
        return 0;
    }

    // ////////////////////////////////////////////////////
    /**
     * Set # of cols in table
     */
    // ////////////////////////////////////////////////////
    public void setColumns(int columns) {
        if (columns <= 0)
            return;
        this.columns = columns;
        if (rows > 0)
            createTextFields();
    }

    // ////////////////////////////////////////////////////
    /**
     * Extracts the row and column of a commName in the format
     * "table1_CellROWCOL"
     */
    // ////////////////////////////////////////////////////
    protected Vector getRowColVector(String selection) {
    	Vector v = new Vector();
        String col = selection.substring(selection.lastIndexOf("_C") + 2, selection
                .lastIndexOf("R"));
        String row = selection.substring(selection.lastIndexOf("R") + 1,
                selection.length());
        v.addElement(new Integer(new Integer(row).intValue() - 1));
        v.addElement(new Integer(new Integer(col).intValue() - 1));
        return v;
    }

    // ////////////////////////////////////////////////////
    /**
     * Return a name which describes the widget and the current row and column
     * which can be sent to lisp
     */
    // ////////////////////////////////////////////////////
    public String getCommNameToSend() {
        String s = commName + "_C" + currentColumn + "R" + currentRow;
        return s;
    }
    
    /**
     * Override to enter cell names in the widget list, too.
     * @param name
     * @param controller
     * @see pact.CommWidgets.JCommWidget#setCommName(java.lang.String, edu.cmu.pact.ctat.TutorController)
     */
    public void setCommName(String name, TutorController controller) {
    	super.setCommName(name, controller);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
            	TableCell cell = getCell(i, j);
            	addCommWidgetName(cell.getCommName());
            }
        }
    }

    public void setFocus(String subWidgetName) {
        Vector rowcol = getRowColVector(subWidgetName);
        int row = ((Integer) rowcol.elementAt(0)).intValue();
        int col = ((Integer) rowcol.elementAt(1)).intValue();

        cells[row][col].requestFocus();
    }

    // ////////////////////////////////////////////////////
    /**
     */
    // ////////////////////////////////////////////////////
    public void highlight(String subElement, Border highlightBorder) {

        if (trace.getDebugCode("mps"))
        	trace.printStack("mps", "set highlight: subElement = " + subElement);
        Vector rowcol = getRowColVector(subElement);
        int row = ((Integer) rowcol.elementAt(0)).intValue();
        int col = ((Integer) rowcol.elementAt(1)).intValue();
        cells[row][col].setBorder(highlightBorder);
        highlighted = true;
        highlightRow = row;
        highlightCol = col;
    }

    // ////////////////////////////////////////////////////
    /**
     */
    // ////////////////////////////////////////////////////
    public void removeHighlight(String subElement) {
    	if(trace.getDebugCode("inter"))
    		trace.printStack("inter", "JCommTable.removeHighlight("+subElement+") highlightRow "+
    				highlightRow+", Col "+highlightCol);
        cells[highlightRow][highlightCol].setBorder(originalBorder);
    }

    // ////////////////////////////////////////////////////
    /**
     * Get # of cols in table
     */
    // ////////////////////////////////////////////////////
    public int getColumns() {
        return columns;
    }

    // ////////////////////////////////////////////////////
    /**
     */
    // ////////////////////////////////////////////////////
    public void setSpacing(int spacing) {
        this.spacing = spacing;

        // Bapuji (from Carnegie Learning) commented this following line
        // and inserted the other code to handle spacing without
        // calling createTextFields(). createTextFields() instantiates
        // table cells, which is not required in this case.

        // createTextFields();

        if (tableLayout != null) {
            tableLayout.setVGap(spacing);
            tableLayout.setHGap(spacing);
            adjustPanelSize();
        }
    }

    // ////////////////////////////////////////////////////
    /**
     */
    // ////////////////////////////////////////////////////
    public int getSpacing() {
        return spacing;
    }

    // ////////////////////////////////////////////////////
    /**
     * Set font for this table
     */
    // ////////////////////////////////////////////////////
    public void setFont(Font f) {
        startFont = f;
        super.setFont(f);
        if (tableInitialized)
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < columns; j++)
                    cells[i][j].setFont(f);
    }

    // /////////////////////////////////////////////////////
    /**
     * Handling double clicking when SimStudent is active
     */
    // /////////////////////////////////////////////////////
    
    // Sat May 21 21:47:12 2005: Noboru
    // Called upon mouse click when Sim. Student is active
    public void mouseDoubleClickedWhenMissActive(MouseEvent me) {

        TableCell cell = (TableCell) me.getComponent();
        // JCommTable table = (JCommTable)cell.getParent();

        /*
         * String cellName = commName + "_C" + (cell.row + 1) + "R" +
         * (cell.column + 1); System.out.println("cell: " + cellName);
         * System.out.println("Row: " + cell.row + ", Col: " + cell.column);
         */

        // Following two actions are toggled-actions, i.e., the first
        // click specifies that the clicked cell is a focus of
        // attention, but the second click cancels it
        toggleHighlight(cell);
        // Inform SimSt about this action.. you have exact same problem in other function with solution borg.
        getController().toggleWidgetFocusForSimSt(cell);
    }

    // Sat May 21 22:58:47 2005: Noboru
    // Toggle highlight a specified table cell
    private void toggleHighlight(TableCell cell) {

        if (cell.isHighlighted()) {
            cell.removeHighlight();
        } else {
            cell.highlight();
        }
    }
    
    // -sanket
    private class MouseListenerClass implements MouseListener {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent me) {
            // if (highlighted)
            // removeHighlight("");

            if (me.isControlDown()) {
                TableCell tc = (TableCell) me.getComponent();

                tc.previousValue = tc.getText();
                cells[tc.row][tc.column].setForeground(startColor);
                if (startFont != null)
                    cells[tc.row][tc.column].setFont(startFont);
                currentRow = tc.row + 1;
                currentColumn = tc.column + 1;
                if (trace.getDebugCode("dw")) trace.out("dw", " current row = " + currentRow
                        + " current col = " + currentColumn);

                repaint();

                if (tc.selected) {
                    tc.setBackground(Color.WHITE);
                    tc.selected = false;
                    selectedCellsList.remove(getCommNameToSend());
                    selectedValues.remove(getValue());
                    nameModel.removeElement(getCommNameToSend());
                    valuesModel.removeElement(getValue());
                } else {
                    tc.setBackground(Color.PINK);
                    tc.selected = true;
                    addToSelectedList(getCommNameToSend());
                    addToSelectedValues(getValue());
                }
                selectedCellsFrame.validate();
                selectedCellsFrame.pack();
                selectedCellsFrame.show();
            }

            // Tue Mar 28 23:24:11 2006 Noboru
            // This is absolete for now a double-clicking when SimStudent is active is 
            // trapped by the TutorWindow, which further 
            // calls HintMessagesManager.tutorWindowClicked(), which 
            // then calls JCommWidget.mouseDoubleClickedWhenMissActive()
            if (me.getClickCount() == -2) {

                // Sat May 21 21:41:17 2005: Noboru
                // Patch to trap a mouse click when Sim. Student is active...
            	if (getController().isSimStudentMode()) {
                    // System.out.println("JCommTable:
                    // mouseDoubleClickedWhenMissActive()");
                    mouseDoubleClickedWhenMissActive(me);
                }

                // JCommTable dt = (JCommTable)me.getComponent().getParent();
                // clearSelectedList();
                // clearSelectedValues();
                // for(int i = 0; i < dt.rows; i++){
                // for(int j = 0; j < dt.columns; j++){
                // if(dt.textField[i][j].selected){
                // dt.currentColumn = j + 1;
                // dt.currentRow = i + 1;
                // addToSelectedList(getCommNameToSend());
                // addToSelectedValues(getValue());
                // }
                // }
                // }
                // sendSelectedCells();
            }
        }

        // Sat May 21 21:47:12 2005: Noboru
        // Called upon mouse click when Sim. Student is active
        public void mouseDoubleClickedWhenMissActive(MouseEvent me) {

            TableCell cell = (TableCell) me.getComponent();
            // JCommTable table = (JCommTable)cell.getParent();

            /*
             * String cellName = commName + "_C" + (cell.row + 1) + "R" +
             * (cell.column + 1); System.out.println("cell: " + cellName);
             * System.out.println("Row: " + cell.row + ", Col: " + cell.column);
             */

            // Following two actions are toggled-actions, i.e., the first
            // click specifies that the clicked cell is a focus of
            // attention, but the second click cancels it
            toggleHighlight(cell);
            // Inform SimSt about this action
            getController().toggleWidgetFocusForSimSt(cell);
        }

        // Sat May 21 22:58:47 2005: Noboru
        // Toggle highlight a specified table cell
        private void toggleHighlight(TableCell cell) {

            if (cell.isHighlighted()) {
                cell.removeHighlight();
            } else {
                cell.highlight();
            }
        }

        /**
         * 
         */
        private void clearSelectedValues() {
            selectedValues.removeAll(selectedValues);
        }

        /**
         * @param o
         */
        private void addToSelectedValues(Object o) {
            selectedValues.add(o);
            valuesModel.addElement(o);
        }

        /**
         * 
         */
        private void clearSelectedList() {
            selectedCellsList.removeAll(selectedCellsList);
        }

        /**
         * @param cellName
         */
        private void addToSelectedList(String cellName) {
            selectedCellsList.add(cellName);
            nameModel.addElement(cellName);

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
         */
        public void mouseEntered(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        public void mouseExited(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent arg0) {
        }
    }

    // -sanket

    public String[] getTableCellFeatures() {
        return this.tableCellFeatures;
    }

    public void setTableCellFeatures(String[] tableCellFeatures) {
        // Update table cells with new value
        int row, col;
        int newstatus;
        for (int i = 0; i < tableCellFeatures.length; i++) {
            if (i < rows * columns) {
                row = i / columns;
                col = i % columns;

                newstatus = Integer.parseInt(tableCellFeatures[i]);
                if (newstatus == JCommTable.CELL_FORMAT_DO_BOTH) {
                    cells[row][col].setDisplayCellImage(true);
                    cells[row][col].setDisplayToolTip(true);
                } else if (newstatus == JCommTable.CELL_DISPLAY_CELL_IMAGE_ONLY) {
                    cells[row][col].setDisplayCellImage(true);
                    cells[row][col].setDisplayToolTip(false);
                } else if (newstatus == JCommTable.CELL_DISPLAY_TOOLTIP_ONLY) {
                    cells[row][col].setDisplayCellImage(false);
                    cells[row][col].setDisplayToolTip(true);
                } else {
                    cells[row][col].setDisplayCellImage(false);
                    cells[row][col].setDisplayToolTip(false);
                }
                this.tableCellFeatures[i] = String.valueOf(cells[row][col]
                        .getCellFormatStatus());
                if (trace.getDebugCode("mps")) trace.out("mps", "setTableCellFeatures["+row+"]["+col+"] status "+
                		this.tableCellFeatures[i]+", cell "+cells[row][col]);
            }
        }
    }

    // ////////////////////////////////////////////////////
    /**
     * Helper class
     */
    // ////////////////////////////////////////////////////
    public class TableCell extends JTextField implements KeyListener {

        int row, column;
        public int getRow() { return row; }
        public int getColumn() { return column; }

        String previousValue = "";

        boolean selected = false;

        Highlighter oldHighlighter;


        public TableCell() {
        	JCommDocument doc = new JCommDocument();
            this.setDocument(doc);
            addKeyListener(this);
        }

        public void setText(String text) {
//            new Exception().printStackTrace();
//            trace.out ("feb19", "new text = "+ text);
//            trace.out ("feb19", "old text = "+ getText ());
//            trace.out ("feb19", "previousValue = "+ previousValue);
//            trace.out ("feb19", "getCommName() = "+getCommName());
        	if(trace.getDebugCode("inter"))
        		//trace.printStack("inter", "TableCell.setText("+text+") commName "+getCommName());
        		trace.out("inter", "TableCell.setText("+text+") commName "+getCommName());
        	setTextInternal(text);
            dirty = true;
        }

        /**
         * Set the text field. Does not set {@link CommWidgets#dirty}.
         * @param string
         */
        private void setTextInternal(String string) {
        	super.setText(string);
		}

        // Sun May 22 14:24:22 2005: Noboru
        // Return "CommName" for the table cell
        public String getCommName() {
            return commName + "_C" + (column + 1) + "R" + (row + 1);
        }

        // Returns a "value" in this table cell
        public String getValue() {
            return getText();
        }

        // Sat May 21 23:05:43 2005: Noboru
        // flag showing if this table cell is highlighted or not
        boolean highlighted = false;

        boolean isHighlighted() {
            return this.highlighted;
        }

        void setHighlighted(boolean flag) {
            this.highlighted = flag;
        }

        // Sat May 21 23:07:45 2005: Noboru
        void highlight() {
            setBorder(HintMessagesManagerForClient.defaultBorder);
            setHighlighted(true);
        }

        // Mon May 23 14:58:37 2005: Noboru
        // This method is called by SimSt. hence must be public
        public void removeHighlight() {
            setBorder(originalBorder);
            setHighlighted(false);
        }

        /**
         * Currently a no-op.
         * @param e
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
    	public void keyReleased(KeyEvent e) {}
     
    	/**
    	 * If field unlocked, sets foreground to {super#startColor}.
    	 * @param e
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
    	 */
        public void keyTyped(KeyEvent e) {
            if (!((JCommDocument) getDocument()).locked)
                setForeground (startColor);
        }

        /**
         * Currently a no-op.
         * @param e
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public void keyPressed(KeyEvent e) {}
    }

	public void mousePressed(MouseEvent e) {
	}
	
    /**
     * TableExpressionCell is a subclass of TableCell and it has the following
     * additional features: 1) Displays Image within cell for correct answers.
     * 2) Displays expression tooltip.
     */
    public class TableExpressionCell extends TableCell {
	
        private static final int IMAGE_VERTICAL_PADDING = 2;

        private static final int IMAGE_HORIZONTAL_PADDING = 2;

        // if 'true', expression tooltip is displayed.
        private boolean displayToolTip;

        // if 'true', image is displayed within the cell for correct answer.
        private boolean displayCellImage;

        private Object exprToolTip;   // grant.widgets.ToolTip.SwingExprToolTip

        private JComponent parent;
        public JCommTable getTable() { return (JCommTable)parent; }

		private Image expression_image;

        private int expr_img_x, expr_img_y;

        public TableExpressionCell(JComponent parent, int i, int j) {
            super();
            this.parent = parent;
            row = i;
            column = j;

        }

        /**
         * This overloaded method paints formatted mathematical expression image
         * within TableExpressionCell if the cell is locked and expression_image
         * is not null. Or else it calls super method for normal behavior.
         * 
         * @param gr -
         *            graphics object for painting.
         */
        public void paintComponent(Graphics gr) {
            if (((JCommDocument) this.getDocument()).locked) {
                if (exprToolTip != null)
                    ((grant.widgets.ToolTip.SwingExprToolTip) exprToolTip).detach();
                if (isDisplayCellImage() && expression_image != null) {
                    Dimension cellSize = JCommTable.this.getCellSize(row,
                            column);
                    Color currColor = gr.getColor();
                    gr.setColor(this.getBackground());
                    gr.fillRect(0, 0, cellSize.width, cellSize.height);
                    gr
                            .drawImage(expression_image, expr_img_x,
                                    expr_img_y, this);
                    gr.setColor(currColor);
                    return;
                }
            }
            super.paintComponent(gr);
        }

        // Create image creates webeq image.
        public void createImage() {
            if (VersionInformation.includesCL() && this.isDisplayCellImage()) {
                // Create image only if expression_image is null.
                if (expression_image == null) {
                    // Instantiate and prepare image writer
                    webeq3.app.ImageWriter imgWriter = new webeq3.app.ImageWriter();
                    imgWriter.setPointsize(this.getFont().getSize());
                    imgWriter.setBGColor(this.getBackground().getRed(), this
                            .getBackground().getGreen(), this.getBackground()
                            .getBlue());
                    // generate mathml for mathematical expression
                    String mathML = grant.utilities.WebEQUtils.convertExprToMathML(this
                            .getText(), this.getFont(), this.getForeground());
                    if (!mathML.equalsIgnoreCase("")) {
                        expression_image = grant.utilities.WebEQUtils.createWebEQImage(
                                imgWriter, mathML);
                        if (expression_image == null)
                            return;
                    } else
                        expression_image = null;
                }
                Dimension table_cell_size = JCommTable.this.getCellSize(row,
                        column);

                int imgWidth = expression_image.getWidth(JCommTable.this);
                int imgHeight = expression_image.getHeight(JCommTable.this);

                // Wait until image is prepared completely
                while (imgWidth == -1 || imgHeight == -1) {
                    imgWidth = expression_image.getWidth(JCommTable.this);
                    imgHeight = expression_image.getHeight(JCommTable.this);
                }
                int newWidth, newHeight;
                int imgWidthwithPadding = imgWidth + 2
                        * TableExpressionCell.IMAGE_HORIZONTAL_PADDING;
                int imgHeightwithPadding = imgHeight + 2
                        * TableExpressionCell.IMAGE_VERTICAL_PADDING;
                newWidth = (table_cell_size.getWidth() > imgWidthwithPadding) ? (int) table_cell_size
                        .getWidth()
                        : imgWidthwithPadding;
                newHeight = (table_cell_size.getHeight() > imgHeightwithPadding) ? (int) table_cell_size
                        .getHeight()
                        : imgHeightwithPadding;

                JCommTable.this.setCellSize(row, column, new Dimension(
                        newWidth, newHeight));

                alignImage();
            }
        }

        public void doIncorrectAction() {
            expression_image = null;
        }

        /**
         * This method is called when row or column size is changed.
         */
        public void alignImage() {
            if (expression_image == null)
                return;

            Dimension table_cell_size = JCommTable.this.getCellSize(row,
                    column);
            // Calculate expression image position for painting
            expr_img_x = TableExpressionCell.IMAGE_HORIZONTAL_PADDING;
            expr_img_y = (table_cell_size.height - expression_image
                    .getHeight(JCommTable.this)) / 2;

            repaint();
        }

        public int getCellFormatStatus() {
            if (this.isDisplayCellImage() && this.isDisplayToolTip())
                return JCommTable.CELL_FORMAT_DO_BOTH;
            else if (this.isDisplayCellImage() && !this.isDisplayToolTip())
                return JCommTable.CELL_DISPLAY_CELL_IMAGE_ONLY;
            else if (!this.isDisplayCellImage() && this.isDisplayToolTip())
                return JCommTable.CELL_DISPLAY_TOOLTIP_ONLY;
            else
                return JCommTable.CELL_FORMAT_DO_NONE;
        }

        public boolean isDisplayCellImage() {
            return displayCellImage;
        }

        public void setDisplayCellImage(boolean displayCellImage) {
            this.displayCellImage = displayCellImage;
            expression_image = null;
        }

        public boolean isDisplayToolTip() {
            return this.displayToolTip;
        }

        public void setDisplayToolTip(boolean displayToolTip) {
        	if (!VersionInformation.includesCL()) {
        		this.displayToolTip = false;
        		this.exprToolTip = null;
        		return;
        	}
            this.displayToolTip = displayToolTip;
            // --- If doExpressionFormatting is set true, attach the textField
            // --- to ExpressionToolTip.
            if (this.displayToolTip) {
                /***************************************************************
                 * SwingExprToolTip is a class that is used to display formatted *
                 * expresisons. Create an instance of SWExpressionToolTip and
                 * call the * method attch() to make SWExpressionToolTip
                 * instance listen to the * key events of textfield where user
                 * enters the values. * Even though exprToolTip is instantiated
                 * at this point, TextField is * attached to ExpressionToolTip
                 * in setDoExpressionFormatting(). *
                 **************************************************************/
                if (exprToolTip == null) {
                	grant.widgets.ToolTip.SwingExprToolTip ett = new grant.widgets.ToolTip.SwingExprToolTip(parent, this);
                    exprToolTip = ett;
                    ett.attach();
                }
            } else {
                if (exprToolTip != null) {
                    ((grant.widgets.ToolTip.SwingExprToolTip) exprToolTip).detach();
                    exprToolTip = null;
                }
            }
        }
    }
}
