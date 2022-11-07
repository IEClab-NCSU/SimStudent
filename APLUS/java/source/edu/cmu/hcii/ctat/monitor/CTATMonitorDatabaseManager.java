/**
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATMonitorDatabaseManager.java,v 1.3 2012/10/08 14:21:00 akilbo Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATMonitorDatabaseManager.java,v $
 Revision 1.3  2012/10/08 14:21:00  akilbo
 Changed the CTATlink() to include a new filemanager object as the old constructer has been deprecated.

 Revision 1.2  2012/10/08 13:04:17  akilbo
 Added Correct Package Info

 Revision 1.1  2012/10/05 20:16:30  vvelsen
 Started the re-packaging of the monitor source classes

 Revision 1.7  2012/09/18 20:01:30  akilbo
 The change resolution combo box is now more intuitive, filtered out excess material appearing in the jlist on the left side of the dbmanager to only the basic items necessary for viewing.

 Revision 1.6  2012/09/17 20:46:21  akilbo
 Added a combo box to change between the different time scales of a given TS. Functional, but not pretty at the moment.

 Revision 1.5  2012/05/07 19:09:12  vvelsen
 Added some visual tools to better manage our performance database

 Revision 1.4  2012/05/03 15:32:51  vvelsen
 Some refactoring of the classes that hold our database data. Tried to make the code more generic although given the way that the Berkeley serialzation work we might not be able to make it completely general. For example right now we explicitly store tables of CTATTSMemory and CTATTSSession objects in the database. I don't right now see an easy way of storing one type of object that can then be cast to a different more specific instance

 Revision 1.3  2012/04/30 18:21:39  vvelsen
 Added some support code around the database management system. This will allow users and developers to choose date ranges in the database

 Revision 1.2  2012/04/27 18:19:51  vvelsen
 The database code is now in a pretty good state. We can stream and obtain serialized data objects that represent critical values of our servers. Will still need more testing but it's looking good. A swing gui is included which can be used for database management tasks and allows someone to export our data to a spreadsheet if needed

 Revision 1.1  2012/04/25 19:35:47  vvelsen
 Major upgrade to the database system. Also added a Swing based manager for easy access, management and export

 $RCSfile: CTATMonitorDatabaseManager.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/monitor/CTATMonitorDatabaseManager.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:

*/

package edu.cmu.hcii.ctat.monitor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.toedter.calendar.JDateChooser;

import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.hcii.ctat.CTATDesktopFileManager;
import edu.cmu.hcii.ctat.CTATLink;

/**
 * 
 */
public class CTATMonitorDatabaseManager extends JFrame implements MouseListener, ActionListener 
{
	private static final long serialVersionUID = -1353911499968570785L;

	private class ExitListener extends WindowAdapter 
	{
		  public void windowClosing(WindowEvent event) 
		  {
			  if (driver!=null)
			  {
				  try 
				  {
					  driver.close();
				  } 
				  catch (Exception e) 
				  {
					  debug ("Error shutting down database driver");
					  e.printStackTrace();
				  }
			  }
			  
			  System.exit(0);
		  }
	}
	
	private SimpleDateFormat df = new SimpleDateFormat ("EEE d MMM yyyy HH:mm:ss.SSS Z");
	
	private JList coreList=null;
	//private JList tableList=null;
	private CTATBerkeleyDB driver=null;
	private JTextArea dbData=null;
	private JTable vizTable=null;
	
    private String[] dbheader = new String[] {"Index","Date", "Entry"};
    private String[][] dbdata = new String[][] {};	
    private String [] resolutionoptons = new String [] {"All" ,"Hour", "Day", "Week", "Month"}; 
    
    private DefaultTableModel model = null;
    
	private CTATBerkeleyDBInstance sessionDB=null;
	private CTATBerkeleyDBInstance memoryDB=null;
	private CTATBerkeleyDBInstance dummyDB=null;
	
	private StoredSortedMap<Long, CTATTSSession> sessionMap=null;
	private StoredSortedMap<Long, CTATTSMemory> memoryMap=null;	    
	private StoredSortedMap<Long, String> dummyMap=null;
	
	private SortedMap<Long, CTATTSSession> sessionMapRanged=null;
	private SortedMap<Long, CTATTSMemory> memoryMapRanged=null;	    
	private SortedMap<Long, String> dummyMapRanged=null;
	
	private Object[] sessionObjectMap=null;
	private Object[] memoryObjectMap=null;
	private Object[] dummyObjectMap=null;
	
	private Object currentDB;
    
    //private	SortedMap<Long, String> map=null;
    private Boolean showDates=false; 
    
    private JCheckBox toggleDateFormat=null;    
    private JTextField status=null;
    private JTextField statusBar=null;
    private JTextField tableInfo=null;
    private JTextField minRange=null;
    private JTextField maxRange=null;    
    private JButton setRange=null;    
    private JButton setDateRange=null;
    private JButton previousSet=null;
    private JButton nextSet=null;
    private JComboBox changeResolution=null;
    
    private JButton exportSet=null;
    
    private JRadioButton exportAllButton=null;
    private JRadioButton exportSelectedButton=null;    
    
    private Boolean exportAll=false;
    
    private JDateChooser startDate=null;
    private JDateChooser endDate=null;
        
    public static final int BYRANGE = 1;
    public static final int BYDATE = 2;
    
    private int sortby=BYRANGE;
    
	/**
	 * 
	 */	
	public CTATMonitorDatabaseManager ()
	{
		this.setTitle("CTAT Monitor Database Manager");
	    this.setSize (640,450);
	    
	    Border border=BorderFactory.createLineBorder(Color.black);
	    
	    JTabbedPane tabbedPane = new JTabbedPane();
	    	    
		this.setContentPane(tabbedPane);
		
	    JPanel panel=new JPanel ();
		
		tabbedPane.addTab("Data Manager",null,panel,"Load, edit and export data");
		
		panel.setLayout(new BoxLayout (panel,BoxLayout.Y_AXIS));
			    
		
		coreList = new JList ();
		coreList.setMinimumSize (new Dimension (100,50));
		coreList.addMouseListener (this);
		coreList.setFont(new Font("Dialog", 1, 10));
	    JScrollPane posScrollList=new JScrollPane (coreList);
	    	    	    	    
	    Box bottomContainer=new Box (BoxLayout.Y_AXIS);
	    	    
	    Box buttonBar=new Box (BoxLayout.X_AXIS);
	    buttonBar.setMinimumSize(new Dimension (50,20));
	    buttonBar.setMaximumSize(new Dimension (50000,20));
	    
	    toggleDateFormat=new JCheckBox ();
	    toggleDateFormat.setText("Show Date/Raw");
	    toggleDateFormat.setFont(new Font("Dialog", 1, 10));
	    toggleDateFormat.addActionListener(this);
	    buttonBar.add(toggleDateFormat);
	    
	    changeResolution= new JComboBox(resolutionoptons);
	    changeResolution.addActionListener(this);
	    buttonBar.add(changeResolution);
	    
	    
	    buttonBar.add(new JSeparator(SwingConstants.VERTICAL));
	    
	    minRange=new JTextField ();
	    minRange.setText("0");
	    minRange.setFont(new Font("Dialog", 1, 10));
	    minRange.setMinimumSize(new Dimension (50,18));
	    minRange.setPreferredSize(new Dimension (50,18));
	    buttonBar.add(minRange);
	    	    	    	    
	    maxRange=new JTextField ();
	    maxRange.setText("100");
	    maxRange.setFont(new Font("Dialog", 1, 10));
	    maxRange.setMinimumSize(new Dimension (50,20));
	    maxRange.setPreferredSize(new Dimension (50,20));
	    buttonBar.add(maxRange);
	    	    
	    setRange=new JButton ();
	    setRange.setMargin(new Insets(1,1,1,1));
	    setRange.setText("Set");
	    setRange.setFont(new Font("Dialog", 1, 10));
	    setRange.setMinimumSize(new Dimension (60,20));
	    setRange.setPreferredSize(new Dimension (60,20));
	    setRange.addActionListener(this);
	    buttonBar.add(setRange);	    
	    
	    previousSet=new JButton ();
	    previousSet.setMargin(new Insets(1,1,1,1));
	    previousSet.setText("Previous");
	    previousSet.setFont(new Font("Dialog", 1, 10));
	    previousSet.setMinimumSize(new Dimension (60,20));
	    previousSet.setPreferredSize(new Dimension (60,20));
	    previousSet.addActionListener(this);
	    buttonBar.add(previousSet);
	    
	    nextSet=new JButton ();
	    nextSet.setMargin(new Insets(1,1,1,1));  
	    nextSet.setText("Next");
	    nextSet.setFont(new Font("Dialog", 1, 10));
	    nextSet.setMinimumSize(new Dimension (60,20));
	    nextSet.setPreferredSize(new Dimension (60,20));
	    nextSet.addActionListener(this);
	    buttonBar.add(nextSet);	    
	    
	    buttonBar.add(new JSeparator(SwingConstants.VERTICAL));
	    
	    startDate=new JDateChooser(new Date());
	    startDate.setFont(new Font("Dialog", 1, 10));
	    startDate.setMinimumSize(new Dimension (100,20));
	    startDate.setPreferredSize(new Dimension (100,20));
	    buttonBar.add (startDate);

	    endDate=new JDateChooser(new Date());
	    endDate.setFont(new Font("Dialog", 1, 10));
	    endDate.setMinimumSize(new Dimension (100,20));
	    endDate.setPreferredSize(new Dimension (100,20));
	    buttonBar.add(endDate);	    
	    
	    setDateRange=new JButton ();
	    setDateRange.setMargin(new Insets(1,1,1,1));
	    setDateRange.setText("Set Dates");
	    setDateRange.setFont(new Font("Dialog", 1, 10));
	    setDateRange.setMinimumSize(new Dimension (60,20));
	    setDateRange.setPreferredSize(new Dimension (60,20));
	    setDateRange.addActionListener(this);
	    buttonBar.add(setDateRange);
	    
	    buttonBar.add(new JSeparator(SwingConstants.VERTICAL));
	    	    	    
	    status=new JTextField ();
	    status.setText("Status: OK");
	    status.setEditable(false);
	    status.setBorder(border);
	    status.setFont(new Font("Dialog", 1, 10));
	    status.setMinimumSize(new Dimension (20,20));
	    status.setPreferredSize(new Dimension (50000,20));
	    buttonBar.add(status);	    
	    
	    model = new DefaultTableModel(dbdata,dbheader);
	    
	    vizTable=new JTable (model);
		vizTable.setMinimumSize(new Dimension (100,50));
		vizTable.setFont(new Font("Dialog", 1, 10));
		vizTable.getColumnModel().getColumn(0).setPreferredWidth(27);
		vizTable.getColumnModel().getColumn(1).setPreferredWidth(120);

	    JScrollPane tableScrollList = new JScrollPane (vizTable);
	    tableScrollList.setMinimumSize(new Dimension (50,50));
	    tableScrollList.setMaximumSize(new Dimension (5000,5000));
	    
	    Box toolBar=new Box (BoxLayout.X_AXIS);
	    toolBar.setMinimumSize(new Dimension (50,20));
	    toolBar.setMaximumSize(new Dimension (50000,20));
	    
	    exportAllButton = new JRadioButton();
	    exportAllButton.setText("All");
	    exportAllButton.setFont(new Font("Dialog", 1, 10));
	    exportAllButton.addActionListener(this);
	    toolBar.add(exportAllButton);
	    
	    exportSelectedButton = new JRadioButton();
	    exportSelectedButton.setText("Selected");
	    exportSelectedButton.setFont(new Font("Dialog", 1, 10));
	    exportSelectedButton.setSelected(true);
	    exportSelectedButton.addActionListener(this);
	    toolBar.add(exportSelectedButton);

	    //Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
	    group.add(exportAllButton);
	    group.add(exportSelectedButton);
	    	    	   	    	    
	    exportSet=new JButton ();
	    exportSet.setMargin(new Insets(1,1,1,1));  
	    exportSet.setText("Export ...");
	    exportSet.setFont(new Font("Dialog", 1, 10));
	    exportSet.setMinimumSize(new Dimension (70,20));
	    exportSet.setPreferredSize(new Dimension (70,20));
	    exportSet.addActionListener(this);
	    toolBar.add(exportSet);
	    
	    JButton importSet=new JButton ();
	    importSet.setMargin(new Insets(1,1,1,1));  
	    importSet.setText("Import ...");
	    importSet.setFont(new Font("Dialog", 1, 10));
	    importSet.setMinimumSize(new Dimension (70,20));
	    importSet.setPreferredSize(new Dimension (70,20));
	    importSet.addActionListener(this);
	    toolBar.add(importSet);	    
	    	    
	    JTextField filler2=new JTextField ();
	    filler2.setText("Status: OK");
	    filler2.setEditable(false);
	    filler2.setBorder(border);
	    filler2.setFont(new Font("Dialog", 1, 10));
	    filler2.setMinimumSize(new Dimension (20,18));
	    filler2.setPreferredSize(new Dimension (50000,20));
	    toolBar.add(filler2);
	    	    	    
	    tableInfo=new JTextField ();
	    tableInfo.setText("");
	    tableInfo.setEditable(false);
	    tableInfo.setBorder(BorderFactory.createLoweredBevelBorder());
	    tableInfo.setFont(new Font("Dialog", 1, 10));
	    tableInfo.setMinimumSize(new Dimension (20,20));
	    tableInfo.setPreferredSize(new Dimension (50000,20));
	    tableInfo.setMaximumSize(new Dimension (50000,20));
	    
	    bottomContainer.add (buttonBar);
	    bottomContainer.add (tableInfo);
	    bottomContainer.add (tableScrollList);
	    bottomContainer.add (toolBar);
	    
	    dbData=new JTextArea ();
	    dbData.setFont(new Font("Courier", 1, 10));	    
	    JScrollPane dbDataScroller = new JScrollPane (dbData);
	    	    
	    JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,dbDataScroller, bottomContainer);
	    rightSplitPane.setOneTouchExpandable(true);
	    rightSplitPane.setDividerLocation(150);
	    	    	    
	    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,posScrollList, rightSplitPane);
	    splitPane.setOneTouchExpandable(true);
	    splitPane.setDividerLocation(150);	    
	    splitPane.setMinimumSize(new Dimension (20,20));
	    splitPane.setMaximumSize(new Dimension (50000,50000));	    
		splitPane.setBorder(border);
	    
	    panel.add(splitPane);
	    
	    /*
	    statusBar=new JTextField ();
	    statusBar.setText("Status: OK");
	    statusBar.setEditable(false);
	    statusBar.setBorder(border);
	    statusBar.setFont(new Font("Dialog", 1, 10));
	    statusBar.setMinimumSize(new Dimension (20,18));
	    statusBar.setMaximumSize(new Dimension (50000,18));
	    panel.add(statusBar);
	    */	    
	    
	    JPanel genPanel=new JPanel ();
		
		tabbedPane.addTab("Data Generator",null,genPanel,"Generate simulated data");
		
		genPanel.setLayout(new BoxLayout (genPanel,BoxLayout.Y_AXIS));
			    
	    this.addWindowListener(new ExitListener());
	    this.setVisible(true);
	    
	    driver=new CTATBerkeleyDB ();
	    driver.startDBService ();
	    
	    getDBInfo ();
	}
	/**
	 *
	 */
	private void debug (String aMessage)
	{
		CTATBase.debug ("CTATMonitorDatabaseManager",aMessage);	
	}
	/**
	 * 
	 */
	private void showMessage (String aMessage)
	{
		debug (aMessage);
		JOptionPane.showMessageDialog(this,aMessage);
	}	
	/**
	 * 
	 */		
	private void reset ()
	{
		minRange.setText("0");
		maxRange.setText("100");			
		sortby=BYRANGE;			
		tableInfo.setText ("");
	}	
	/**
	 * 
	 */	
	public Boolean getExportAll() 
	{
		return exportAll;
	}
	/**
	 * 
	 */	
	public void setExportAll(Boolean exportAll) 
	{
		this.exportAll = exportAll;
	}	
	/**
	 * 
	 */	
	private void getDBInfo ()
	{
		debug ("getDBInfo ()");
		
		dbData.setText(driver.getStatus ());
		
		ArrayList<String> dbs=driver.getDatabases ();
		
		ArrayList<String> dbUUIDS = new ArrayList<String>();
		
		int i = 0; 
		
		
		//this while loop uses a regex to check for UUID. and ONLY adds the /all db's to the list of DB's to present, when one the list, the /all is truncated off of the string for presentation. Other fields are accessed from the Change Resolution Combo Box.
		while(i<dbs.size()){
			debug(dbs.get(i));
			if (dbs.get(i).matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}.*?/All")){
				debug(dbs.get(i));
				
				dbUUIDS.add(dbs.get(i).substring(0, (dbs.get(i).length()-4)));
			}
			i++;
		}
		
		coreList.setModel(modelFromList(dbUUIDS));		
		
	}
	/**
	 *
	 */	
	public DefaultListModel modelFromList(ArrayList<String> dbs)
	{
		debug ("modelFromArray ()");
		
		DefaultListModel mdl=new DefaultListModel ();
		
		for (int i=0;i<dbs.size();i++)
		{
			mdl.addElement(dbs.get(i));
		}
		
		return (mdl);
	}
	/**
	 *
	 */	
	public DefaultListModel modelFromMap(Map<Long, String> aMap)
	{
		debug ("modelFromArray ()");
		
		DefaultListModel mdl=new DefaultListModel ();
						
        Iterator<Map.Entry<Long, String>> iter=aMap.entrySet().iterator();
                        
        while (iter.hasNext()) 
        {
            Map.Entry<Long, String> entry = iter.next();
            mdl.addElement(entry.getKey().toString());
        }    			
		
		return (mdl);
	} 
	/**
	 *
	 */	
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		if (e.getClickCount()==2)
		{			
			reset ();
			
			int index = coreList.locationToIndex(e.getPoint());
			ListModel dlm = coreList.getModel();
			Object item = dlm.getElementAt(index);
			coreList.ensureIndexIsVisible(index);
			currentDB = item;
			
			//the db item will be a UUID and the a "-TSSession or TSMemory string, we need to add "/All" to have it open an actual database
			loadDB(item + "/All");
		}
		
	}
		
	
	//seperated these steps from mouseClicked event so it could be used in the change resulution combo box as well
		private void loadDB(Object item){
			debug ("loadDB: " + item);
			
			sessionDB=null;
			memoryDB=null;
			dummyDB=null;
			
			sessionMap=null;
			memoryMap=null;
			dummyMap=null;
			
			CTATBerkeleyDBInstance inst=null;
			
			try 
			{
				inst = driver.accessDB (item.toString());
			} 
			catch (Exception e1) 
			{
				debug ("Error accessing or creating database: " + item.toString());
				e1.printStackTrace();
			}
			
			if (inst!=null)
			{
				if (item.toString().indexOf("-TSSession")!=-1)
				{
					debug ("Mapping session db ...");
										
					try 
					{
						sessionDB=driver.accessDB(item.toString());
					} 
					catch (Exception e1) 
					{
						debug ("Error accessing or creating session data " + item.toString());
						e1.printStackTrace();
					}
					
					if (sessionDB!=null)
					{
						LongBinding keyBinding = new LongBinding();
						EntryBinding<CTATTSSession> sessionBinding = new SerialBinding<CTATTSSession>(driver.getClassCatalog(), CTATTSSession.class);
			        
						sessionMap = new StoredSortedMap<Long, CTATTSSession> (sessionDB.getDB (), keyBinding, sessionBinding, true);						
					}
					else
						debug ("Error: unable to access (session) database");					
				}

				if (item.toString().indexOf("-TSMemory")!=-1)
				{
					debug ("Mapping memory db ...");
					
					try 
					{
						memoryDB=driver.accessDB(item.toString());
					} 
					catch (Exception e1) 
					{
						debug ("Error accessing or creating memory data " + item.toString());
						e1.printStackTrace();
					}
					
					if (memoryDB!=null)
					{
						LongBinding keyBinding = new LongBinding();
						EntryBinding<CTATTSMemory> memoryBinding = new SerialBinding<CTATTSMemory>(driver.getClassCatalog(), CTATTSMemory.class);
					
						memoryMap = new StoredSortedMap <Long, CTATTSMemory> (memoryDB.getDB (), keyBinding, memoryBinding, true);
					}
					else
						debug ("Error: unable to access (memory) database");
				}				
								
				if (item.toString().indexOf("-TSDummy")!=-1)
				{
					debug ("Mapping dummy db ...");
					
					try 
					{
						dummyDB=driver.accessDB(item.toString());
					} 
					catch (Exception e1) 
					{
						debug ("Error accessing or creating dummy data " + item.toString());
						e1.printStackTrace();
					}
					
					if (dummyDB!=null)
					{
						LongBinding keyBinding = new LongBinding();
						StringBinding valueBinding = new StringBinding();

						dummyMap=new StoredSortedMap<Long, String> (dummyDB.getDB (), keyBinding, valueBinding, true);
					}
					else
						debug ("Error: unable to access (dummy) database");
				}							
				
				refreshTable ();		
				
				if (sessionMap!=null)
				{
					@SuppressWarnings("unchecked")
					java.util.Map.Entry<Long, CTATTSSession> entry = (java.util.Map.Entry<Long, CTATTSSession>) sessionObjectMap[0];
					Long fromKey = entry.getKey();					
					
					startDate.setDate(new Date (fromKey));
					
					entry = (Entry<Long, CTATTSSession>) sessionObjectMap[sessionObjectMap.length-1];
					Long toKey = entry.getKey();					
					
					endDate.setDate(new Date (toKey));
					
					tableInfo.setText("Table date range is from \""+df.format(fromKey)+"\" to \""+df.format(toKey)+"\"");
				}
				
				if (memoryMap!=null)
				{
					@SuppressWarnings("unchecked")
					java.util.Map.Entry<Long, CTATTSMemory> entry = (java.util.Map.Entry<Long, CTATTSMemory>) memoryObjectMap[0];
					Long fromKey = entry.getKey();					
					
					startDate.setDate(new Date (fromKey));
					
					entry = (Entry<Long, CTATTSMemory>) memoryObjectMap[memoryObjectMap.length-1];
					Long toKey = entry.getKey();					
					
					endDate.setDate(new Date (toKey));
					
					tableInfo.setText("Table date range is from \""+df.format(fromKey)+"\" to \""+df.format(toKey)+"\"");
			}				
		}
		else{
			debug ("Error: unable to obtain handle to database: "+item.toString ());
			if (statusBar!=null)
			statusBar.setText("Error: unable to obtain handle to database: "+item.toString ());
		}
	}		
	/**
	 * 
	 */
	private void refreshTable ()
	{
		debug ("refreshTable ()");
				
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		createRange ();
		
		if (sessionMap!=null)
		{				
			if (statusBar!=null)
				statusBar.setText("Creating Session Map from DB ...");
			
			status.setText ("Table size: " + sessionMap.entrySet().size());
			
			Long start=Long.parseLong(minRange.getText());
			Long range=Long.parseLong(maxRange.getText());
			
			if (sessionMap.entrySet ().size ()<(start+range))
			{
			    minRange.setEnabled(false);
			    maxRange.setEnabled(false);
			    setRange.setEnabled(false);    
			    previousSet.setEnabled(false);
			    nextSet.setEnabled(false);
			}
			else
			{
			    minRange.setEnabled(true);
			    maxRange.setEnabled(true);
			    setRange.setEnabled(true);
			    previousSet.setEnabled(true);
			    nextSet.setEnabled(true);				
			}
			
			// First remove everything we already have in the table
			
			model.setRowCount(0);
			
			if (sortby==BYRANGE)
				sessionArrayToTable (sessionObjectMap,model);
			else
				sessionMapToTable (sessionMapRanged,model);
			
			if (statusBar!=null)
				statusBar.setText("Map available");
		}		
		
		if (memoryMap!=null)
		{		
			if (statusBar!=null)
				statusBar.setText("Creating Memory Map from DB ...");
			
			status.setText ("Table size: " + memoryMap.entrySet().size());
			
			Long start=Long.parseLong(minRange.getText());
			Long range=Long.parseLong(maxRange.getText());
			
			if (memoryMap.entrySet ().size ()<(start+range))
			{
			    minRange.setEnabled(false);
			    maxRange.setEnabled(false);
			    setRange.setEnabled(false);    
			    previousSet.setEnabled(false);
			    nextSet.setEnabled(false);
			}
			else
			{
			    minRange.setEnabled(true);
			    maxRange.setEnabled(true);
			    setRange.setEnabled(true);
			    previousSet.setEnabled(true);
			    nextSet.setEnabled(true);				
			}
			
			// First remove everything we already have in the table
			
			model.setRowCount(0);
			
			if (sortby==BYRANGE)
				memoryArrayToTable (memoryObjectMap,model);
			else
				memoryMapToTable (memoryMapRanged,model);
			
			if (statusBar!=null)
				statusBar.setText("Map available");
		}				
		
		if (dummyMap!=null)
		{		
			if (statusBar!=null)
				statusBar.setText("Creating Dummy Map from DB ...");
			
			status.setText ("Table size: " + dummyMap.entrySet().size());
			
			Long start=Long.parseLong(minRange.getText());
			Long range=Long.parseLong(maxRange.getText());
			
			if (dummyMap.entrySet ().size ()<(start+range))
			{
			    minRange.setEnabled(false);
			    maxRange.setEnabled(false);
			    setRange.setEnabled(false);    
			    previousSet.setEnabled(false);
			    nextSet.setEnabled(false);
			}
			else
			{
			    minRange.setEnabled(true);
			    maxRange.setEnabled(true);
			    setRange.setEnabled(true);
			    previousSet.setEnabled(true);
			    nextSet.setEnabled(true);				
			}
			
			// First remove everything we already have in the table
			
			model.setRowCount(0);
						
			//mapToTable (dummyMapRanged,model);
			//dummyArrayToTable (dummyObjectMap,model);
			
			if (statusBar!=null)
				statusBar.setText("Map available");
		}	
				
		vizTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		vizTable.getColumnModel().getColumn(0).setWidth(27);
		vizTable.getColumnModel().getColumn(1).setWidth(120);
		
		//vizTable.getColumnModel().getColumn(0).setPreferredWidth(27);
		//vizTable.getColumnModel().getColumn(1).setPreferredWidth(120);
		
		this.setCursor(Cursor.getDefaultCursor());
	}
	/**
	 * 
	 */
	private void sessionMapToTable (Map<Long, CTATTSSession> aMap,DefaultTableModel aModel)
	{
		debug ("sessionMapToTable ()");
		
		//Long start=Long.parseLong(minRange.getText());
		Long range=Long.parseLong(maxRange.getText());
		
        Iterator<Map.Entry<Long, CTATTSSession>> iter=aMap.entrySet().iterator();
        
        Long counter=(long) 0;
        
        while ((iter.hasNext()) && (counter<range)) 
        {
            Map.Entry<Long, CTATTSSession> entry = iter.next();
            
            if (entry!=null)
            {
            	if (showDates==true)
            	{
            		Date shower=new Date (entry.getKey());
            		String aDateString=df.format(shower);
            
            		aModel.insertRow(vizTable.getRowCount(),new Object[]{counter.toString(),aDateString,entry.getValue().toString()});
            	}
            	else
            		aModel.insertRow(vizTable.getRowCount(),new Object[]{counter.toString(),entry.getKey ().toString (),entry.getValue().toString()});
            }
            else
            	debug ("Error getting session from map");
            
            counter++;
        }    				
        
        debug ("sessionMapToTable () Done");
	}
	/**
	 * 
	 */	
	private void memoryMapToTable (Map<Long, CTATTSMemory> aMap,DefaultTableModel aModel)
	{
		debug ("memoryMapToTable ("+aMap.entrySet().size()+")");
		
		//Long start=Long.parseLong(minRange.getText());
		Long range=Long.parseLong(maxRange.getText());
		
        Iterator<Map.Entry<Long, CTATTSMemory>> iter=aMap.entrySet().iterator();
        
        Long counter=(long) 0;
        
        debug ("Transforming data ...");
        
        while ((iter.hasNext()) && (counter<range)) 
        {
            Map.Entry<Long, CTATTSMemory> entry = iter.next();
            
            //Map.Entry entry = (Map.Entry) iter.next();
            
            if (entry!=null)
            {            
            	//trace.out(".");
            	
            	if (showDates==true)
            	{
            		Date shower=new Date (entry.getKey());
            		String aDateString=df.format(shower);
            
            		aModel.insertRow(vizTable.getRowCount(),new Object[]{counter.toString(),aDateString,entry.getValue().toString()});
            	}
            	else
            		aModel.insertRow(vizTable.getRowCount(),new Object[]{counter.toString(),entry.getKey ().toString (),entry.getValue().toString()});
            }
            else
            	debug ("Error, unable to get memory entry from map");
            
            counter++;
        }    		
        
        debug ("memoryMapToTable () Done");
	}	
	/**
	 * 
	 */
	/*
	private void dummyMapToTable (Map<Long, CTATTSMemory> aMap,DefaultTableModel aModel)
	{
		debug ("dMapToTable ("+aMap.entrySet().size()+")");
		
		//Long start=Long.parseLong(minRange.getText());
		Long range=Long.parseLong(maxRange.getText());
		
        Iterator<Map.Entry<Long, CTATTSMemory>> iter=aMap.entrySet().iterator();
        
        int counter=0;
        
        debug ("Transforming data ...");
        
        while ((iter.hasNext()) && (counter<range)) 
        {
            Map.Entry<Long, CTATTSMemory> entry = iter.next();
            
            //Map.Entry entry = (Map.Entry) iter.next();
            
            if (entry!=null)
            {            
            	//trace.out(".");
            	
            	if (showDates==true)
            	{
            		Date shower=new Date (entry.getKey());
            		String aDateString=df.format(shower);
            
            		aModel.insertRow(vizTable.getRowCount(),new Object[]{aDateString,entry.getValue().toString()});
            	}
            	else
            		aModel.insertRow(vizTable.getRowCount(),new Object[]{entry.getKey ().toString (),entry.getValue().toString()});
            }
            else
            	debug ("Error, unable to get memory entry from map");
            
            counter++;
        }    		
        
        debug ("memoryMapToTable () Done");
	}
	*/		
	/**
	 * 
	 */
	/*
	private void mapToTable (Map<Long, String> aMap,DefaultTableModel aModel)
	{
		debug ("mapToTable ()");
		
		//Long start=Long.parseLong(minRange.getText());
		Long range=Long.parseLong(maxRange.getText());
		
        Iterator<Map.Entry<Long, String>> iter=aMap.entrySet().iterator();
        
        int counter=0;
        
        while ((iter.hasNext()) && (counter<range)) 
        {
            Map.Entry<Long, String> entry = iter.next();
            
            if (showDates==true)
            {
            	Date shower=new Date (entry.getKey());
            	String aDateString=df.format(shower);
            
            	aModel.insertRow(vizTable.getRowCount(),new Object[]{aDateString,entry.getValue().toString()});
            }
            else
            	aModel.insertRow(vizTable.getRowCount(),new Object[]{entry.getKey ().toString (),entry.getValue().toString()});
            
            counter++;
        }    								
	}	
	*/
	/**
	 * 
	 */
	private void sessionArrayToTable (Object [] anArray,DefaultTableModel aModel)
	{
		debug ("sessionArrayToTable ()");
		
		Long start=Long.parseLong(minRange.getText());
		Long range=Long.parseLong(maxRange.getText());
				
		for (Long i=start;i<range;i++)
		{
			int mappedIndex=i.intValue(); // !!! For large databases this might get out of range!
			
			@SuppressWarnings("unchecked")
			java.util.Map.Entry<Long, CTATTSSession> entry = (java.util.Map.Entry<Long, CTATTSSession>) anArray[mappedIndex];
			Long key = entry.getKey();
			CTATTSSession value = entry.getValue();
						
            if (showDates==true)
            {
            	Date shower=new Date (key);
            	String aDateString=df.format(shower);
	            
            	aModel.insertRow(vizTable.getRowCount(),new Object[]{i.toString(),aDateString,value.toString()});
            }
            else
            	aModel.insertRow(vizTable.getRowCount(),new Object[]{i.toString(),key.toString(),value.toString()});
		}
	}
	/**
	 * 
	 */
	private void memoryArrayToTable (Object [] anArray,DefaultTableModel aModel)
	{
		debug ("memoryArrayToTable ()");
		
		Long start=Long.parseLong(minRange.getText());
		Long range=Long.parseLong(maxRange.getText());
				
		for (Long i=start;i<range;i++)
		{
			int mappedIndex=i.intValue(); // !!! For large databases this might get out of range!
			
			@SuppressWarnings("unchecked")
			java.util.Map.Entry<Long, CTATTSMemory> entry = (java.util.Map.Entry<Long, CTATTSMemory>) anArray[mappedIndex];
			Long key = entry.getKey();
			CTATTSMemory value = entry.getValue();
			
			
            if (showDates==true)
            {
            	Date shower=new Date (key);
            	String aDateString=df.format(shower);
	                        	
            	aModel.insertRow(vizTable.getRowCount(),new Object[]{i.toString(),aDateString,value.toString()});
            }
            else
            	aModel.insertRow(vizTable.getRowCount(),new Object[]{i.toString(),key.toString(),value.toString()});
		}
	}	
	/**
	 * 
	 */
	/*
	private void dummyArrayToTable (Object [] anArray,DefaultTableModel aModel)
	{
		debug ("dummyArrayToTable ()");
		
		Long start=Long.parseLong(minRange.getText());
		Long range=Long.parseLong(maxRange.getText());
				
		for (Long i=start;i<range;i++)
		{
			int mappedIndex=i.intValue(); // !!! For large databases this might get out of range!
			
			@SuppressWarnings("unchecked")
			java.util.Map.Entry<Long, String> entry = (java.util.Map.Entry<Long, String>) anArray[mappedIndex];
			Long key = entry.getKey();
			String value = entry.getValue();
			
			
            if (showDates==true)
            {
            	Date shower=new Date (key);
            	String aDateString=df.format(shower);
	            
            	aModel.insertRow(vizTable.getRowCount(),new Object[]{aDateString,value});
            }
            else
            	aModel.insertRow(vizTable.getRowCount(),new Object[]{key.toString(),value});
		}
	}	
	*/	
	/**
	 *
	 */	
	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
		//debug ("mouseEntered ()");		
	}
	/**
	 *
	 */	
	@Override
	public void mouseExited(MouseEvent arg0) 
	{
		//debug ("mouseExited ()");		
	}
	/**
	 *
	 */	
	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		//debug ("mousePressed ()");		
	}
	/**
	 *
	 */	
	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		//debug ("mouseReleased ()");		
	}
	/**
	 *
	 */	
	@Override
	public void actionPerformed(ActionEvent event) 
	{
		debug ("actionPerformed ("+event.getActionCommand()+")");
		
		//String act=event.getActionCommand();
		
		if (event.getSource ()==toggleDateFormat)
		{		
			if (toggleDateFormat.isSelected()==true)
				showDates=true;
			else
				showDates=false;
				
			refreshTable ();
			
			return;
		}
		
		if (event.getSource ()==changeResolution){
			
			
			//this will take the UUID and the session or memory and add the /Day, /Hour etc to the end of the string from memory, and then load that up with the loadDB() function
			
			debug("changeResolution");
			
			debug("current db is " + currentDB);
			
			String selectedResolution= (String)changeResolution.getSelectedItem();

			String dbToOpen = currentDB + "/" + selectedResolution; 
			
			debug("Opening " + dbToOpen);
			
			loadDB(dbToOpen);
		
			
			
		}
		
		if (event.getSource()==setRange)
		{
			sortby=BYRANGE;
			
			createRange ();
			
			refreshTable ();
			
			return;
		}
		
		if (event.getSource()==setDateRange)
		{			
			sortby=BYDATE;
			
			refreshTable ();
			
			return;
		}		
		
		if (event.getSource()==exportAllButton)
		{
			setExportAll(true);
			return;
		}
		
		if (event.getSource()==exportSelectedButton)
		{
			setExportAll(false);
			return;
		}			
		
		if (event.getSource()==exportSet)
		{
			CTATDBFileFilter filter1 = new CTATDBFileFilter("Delimited spreadsheet files", new String[] { "CSV" });
			
			final JFileChooser fc = new JFileChooser();
			fc.setFileFilter(filter1);
			
			int returnVal = fc.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) 
			{
				File file = fc.getSelectedFile();

				debug ("Opening: " + file.getName() + ".");
				
				exportData (file.getAbsolutePath());
	        } 
			else 
			{
				debug ("Open command cancelled by user.");
			}		
			
			return;
		}
		
		if (event.getSource ()==setRange)
		{
			//Long start=Long.parseLong(minRange.getText());
			//Long range=Long.parseLong(maxRange.getText());
						
			refreshTable ();
			
			return;
		}
		
		if (event.getSource ()==previousSet)
		{
			Long start=Long.parseLong(minRange.getText());
			Long range=Long.parseLong(maxRange.getText());
			
			start-=100;
			range-=100;
			
			minRange.setText (start.toString ());
			maxRange.setText (range.toString ());
			
			refreshTable ();
			
			return;
		}
		
		if (event.getSource ()==nextSet)
		{
			Long start=Long.parseLong(minRange.getText());
			Long range=Long.parseLong(maxRange.getText());
			
			start+=100;
			range+=100;
			
			minRange.setText (start.toString ());
			maxRange.setText (range.toString ());
			
			refreshTable ();
			
			return;
		}		
	}
	/**
	* Returns a view of the portion of this sorted map whose keys range from fromKey, inclusive, 
	* to toKey, exclusive. (If fromKey and toKey are equal, the returned sorted map is empty.) 
	* The returned sorted map is backed by this sorted map, so changes in the returned sorted 
	* map are reflected in this sorted map, and vice-versa. The returned Map supports all 
	* optional map operations that this sorted map supports.
	* 
	* The map returned by this method will throw an IllegalArgumentException if the user attempts 
	* to insert a key outside the specified range.
	* 
	* Note: this method always returns a half-open range (which includes its low endpoint but not 
	* its high endpoint). If you need a closed range (which includes both endpoints), and the key 
	* type allows for calculation of the successor a given key, merely request the subrange from 
	* lowEndpoint to successor(highEndpoint). For example, suppose that m is a map whose keys are 
	* strings. The following idiom obtains a view containing all of the key-value mappings in m 
	* whose keys are between low and high, inclusive:
	* 
	* Map sub = m.subMap(low, high+"\0");
	* 
	* A similarly technique can be used to generate an open range (which contains neither endpoint). 
	* The following idiom obtains a view containing all of the key-value mappings in m whose keys 
	* are between low and high, exclusive:
	* 
	* Map sub = m.subMap(low+"\0", high);
	*/
	private void createRange ()
	{
		debug ("createRange ()");
		
		Long minTest=Long.parseLong(minRange.getText());
		Long maxTest=Long.parseLong(maxRange.getText());
		
		if (minTest>maxTest)
		{
			showMessage ("Error: maximum range needs to be larger than minimum range");			
			return;
		}
		
		if (minTest==maxTest)
		{
			showMessage ("Error: Start index is equal to end index");
			return;
		}			
		
		if (minTest<0)
		{
			showMessage ("Error: Can't enter negative numbers");
			return;
		}
		
		if (sessionMap!=null)
		{
			if (maxTest>sessionMap.size())
			{
				showMessage ("Error: max range is larger than dataset, capping ...");
				String formed=String.format("%d",sessionMap.size ());				
				maxRange.setText(formed);
				return;
			}
			
			try
			{
				sessionMapRanged=sessionMap.subMap (minTest,maxTest);
			}
			catch (IllegalArgumentException e)
			{
				showMessage ("One of the range indexes is out of bounds");
				//return ("Out of Range");
				return;
			}
										
			sessionMapRanged=sessionMap.subMap (minTest,maxTest);			
			sessionObjectMap=sessionMap.entrySet().toArray();						
		}	
		
		if (memoryMap!=null)
		{
			if (maxTest>memoryMap.size())
			{
				showMessage ("Error: max range is larger than dataset, capping ...");
				String formed=String.format("%d",memoryMap.size ());				
				maxRange.setText(formed);
				return;
			}
						
			try
			{
				memoryMapRanged=memoryMap.subMap (minTest,maxTest);
			}
			catch (IllegalArgumentException e)
			{
				showMessage ("One of the range indexes is out of bounds");
				//return ("Out of Range");
				return;
			}
			
			memoryObjectMap=memoryMap.entrySet().toArray();
		}	
		
		if (dummyMap!=null)
		{
			if (maxTest>dummyMap.size())
			{
				debug ("Error: max range is larger than dataset");
				return;
			}
			
			try
			{
				dummyMapRanged=dummyMap.subMap (minTest,maxTest);
			}
			catch (IllegalArgumentException e)
			{
				showMessage ("One of the range indexes is out of bounds");
				return;
			}
			
			dummyObjectMap=dummyMap.entrySet().toArray();
		}						
		
		debug ("createRange () Done");
	}
	/** 
	 * @param aFile
	 */
	private void exportData (String aFile)
	{
		debug ("exportData ()");
		
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
		String fileFormatted=aFile;
		
		if (aFile.toLowerCase().indexOf("csv")==-1)
			fileFormatted=aFile+".csv";
		
		CTATDesktopFileManager fManager=new CTATDesktopFileManager ();
		
		if (fManager.openStream(fileFormatted)==false)
		{
			debug ("Error opening file for writing");
			this.setCursor(Cursor.getDefaultCursor());
			return;
		}
		
		if (exportAll==true)
		{
			if (sessionMap!=null)
			{
				CTATTSSession headerGenerator=new CTATTSSession ();
				fManager.writeToStream("Time,");
				fManager.writeToStream(headerGenerator.toCSVHeader());
				
				Iterator<Map.Entry<Long, CTATTSSession>> iter=sessionMap.entrySet().iterator();
	        	        	
				while (iter.hasNext()) 
				{
					Map.Entry<Long, CTATTSSession> entry = iter.next();
	            
					if (entry!=null)
					{
						if (showDates==true)
						{
							Date shower=new Date (entry.getKey());
							String aDateString=df.format(shower);
	            		
							fManager.writeToStream(aDateString+","+entry.getValue().toCSV());	            
						}
						else
						{
							fManager.writeToStream(entry.getKey ().toString ()+","+entry.getValue().toCSV());
						}	
						
						fManager.writeToStream("\n");
					}
					else
						debug ("Error getting session from map");	            	        
				}
			}
			
			if (memoryMap!=null)
			{		
				CTATTSMemory headerGenerator=new CTATTSMemory ();
				fManager.writeToStream("Time,");
				fManager.writeToStream(headerGenerator.toCSVHeader());				
				
				Iterator<Map.Entry<Long, CTATTSMemory>> iter=memoryMap.entrySet().iterator();
	        	        	
				while (iter.hasNext()) 
				{
					Map.Entry<Long, CTATTSMemory> entry = iter.next();
	            
					if (entry!=null)
					{
						if (showDates==true)
						{
							Date shower=new Date (entry.getKey());
							String aDateString=df.format(shower);
	            		
							fManager.writeToStream(aDateString+","+entry.getValue().toCSV());	            
						}
						else
						{
							fManager.writeToStream(entry.getKey ().toString ()+","+entry.getValue().toCSV());
						}
						
						fManager.writeToStream("\n");
					}
					else
						debug ("Error getting session from map");
				}
			}			
		}
		else
		{
			Long minTest=Long.parseLong(minRange.getText());
			Long maxTest=Long.parseLong(maxRange.getText());
		}
		
		fManager.closeStream();
		
		this.setCursor(Cursor.getDefaultCursor());
	}
	/**
	 * 
	 */	
	public static void main(String[] args) 
	{
    	@SuppressWarnings("unused")    	    
		CTATLink link=new CTATLink(new CTATDesktopFileManager ()); // Need at least one instance, might as well be the first object made
    	CTATLink.printDebugMessages=true;
    			    	
		@SuppressWarnings("unused")
		CTATMonitorDatabaseManager f=new CTATMonitorDatabaseManager();
	}	
} 