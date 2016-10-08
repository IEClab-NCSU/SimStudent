package edu.cmu.hcii.ctat;

import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import edu.cmu.hcii.ctat.CTATContentCache.UI;
import edu.cmu.hcii.ctat.CTATHTTPHandler.PushResponse;
import edu.cmu.pact.Utilities.trace;

/**
 * Display system tray icon for Local TutorShop.
 * @author Kevin Jeffries
 */
public class LocalTSSystemTray implements ActionListener/*, SysTrayMenuListener*/ 
{	
	private static LocalTSSystemTray singleton = null;
	private static Object lock = new Object();
	
	private boolean displayed = false;
	
	private TrayIcon awtMain = null; // the main component of the system tray, using Swing/AWT (Java 1.6 and later)
	//private SysTrayMenu st4jMain = null; // the main component of the system tray, using systray4j (Java 1.5 and earlier on Windows)
	
	private JFrame progframe = null; // will be used to hold progress bar indicator for cache refresh
	
	private boolean refreshing = false; // is a cache refresh currently underway?	
	
	private final String REFRESH = "Refresh local cache";
	private final String EXIT = "Exit Local TutorShop";
	private final String OFFLINE = "Use offline mode";
	private final String ONLINE = "Return to online mode";
	private final String RESTART = "Return to start page";
	private CTATHTTPServer wserver;
	
	//private final String DIAGNOSTICS = "Enter diagnostics mode";
	//private final String LEAVEDIAGS = "Leave diagnostics mode";
	
	/**
	 * 
	 */
	private LocalTSSystemTray()
	{
		// do nothing; private constructor ensures that there will be only one instance
	}
	/**
	 * 
	 */	
	public static LocalTSSystemTray getInstance()
	{
		if(singleton == null)
		{
			singleton = new LocalTSSystemTray();
		}
		
		return singleton;
	}
	/**
	 * 
	 * @param wserver
	 */
	public void setWebServer(CTATHTTPServer wserver) 
	{
		this.wserver = wserver;
	}	
	/**
	 * 
	 */	
	// returns whether the icon could be displayed
	public boolean display()
	{
		synchronized(lock) // to avoid race conditions on `displayed`
		{
			if(displayed)
			{
				return true;
			}

			String version = System.getProperty("java.version");
			boolean canUseAWT; // AWT support for system tray is in Java 1.6 and later
			try
			{
				String[] versionSplit = version.split("\\.");
				int major = Integer.valueOf(versionSplit[0]);
				int minor = Integer.valueOf(versionSplit[1]);
				if(major > 1 || minor >= 6)
				{
					if(SystemTray.isSupported())
					{
						canUseAWT = true;
					}
					else
					{
						canUseAWT = false;
					}
				}
				else
				{
					canUseAWT = false;
				}
			}
			catch(Exception e) // either ArrayOutOfBoundsException or NumberFormatException
			{
				canUseAWT = false;
			}
			
			if(canUseAWT)
			{
				Image img;
				try
				{
					img = ImageIO.read(new java.io.File("ctat-icon.ico"));
				}
				catch(java.io.IOException e)
				{
					JOptionPane.showMessageDialog(null, "Couldn't find tray icon image: ctat-icon.ico"); // TODO remove
					return false;
				}

				TrayIcon icon = new TrayIcon(img, "CTAT Local TutorShop");
				PopupMenu pm = new PopupMenu();
				pm.add(REFRESH);
				pm.add(OFFLINE);
				pm.add(RESTART);
				pm.add(EXIT);
				pm.addActionListener(this);
				icon.setPopupMenu(pm);

				try
				{
					SystemTray.getSystemTray().add(icon);
					awtMain = icon; // keep a reference to this system tray icon
				}
				catch(AWTException e)
				{
					System.err.println(e);
					return false;
				}
			}
			else // need to use platform-specific code
			{
				/*if(CTATEnvironment.isLocalWindows()) // use SysTray4J on Windows
				{
					SysTrayMenuIcon icon = new SysTrayMenuIcon("ctat-icon");

					SysTrayMenuItem menuitem1 = new SysTrayMenuItem(EXIT, EXIT);
					menuitem1.addSysTrayMenuListener(this);

					SysTrayMenuItem menuitem2 = new SysTrayMenuItem(OFFLINE, OFFLINE);
					menuitem2.addSysTrayMenuListener(this);

					SysTrayMenuItem menuitem3 = new SysTrayMenuItem(REFRESH, REFRESH);
					menuitem3.addSysTrayMenuListener(this);

					SysTrayMenu menu = new SysTrayMenu(icon, "CTAT Local TutorShop");
					menu.addItem(menuitem1);
					menu.addItem(menuitem2);
					menu.addItem(menuitem3);
					
					st4jMain = menu; // keep a reference to this system tray icon
				}
				else if(CTATEnvironment.isLocalMac())
				{
					// TODO do Mac stufff
				}
				else
				{
					return false;
				}*/
			}
			displayed = true;
			return true;
		}
	}
	/**
	 * 
	 */	
	public boolean isDisplayed()
	{
		return displayed;
	}
	/**
	 * 
	 */	
	public boolean showBubbleIfPossible(String message)
	{
		return showBubbleIfPossible(null, message, TrayIcon.MessageType.INFO);
	}
	/**
	 * 
	 */	
	public boolean showBubbleIfPossible(String caption, String message, TrayIcon.MessageType type)
	{
		if(!displayed)
			return false;
		
		if(awtMain != null) // bubble can be shown only when using Swing/AWT
		{
			((TrayIcon)awtMain).displayMessage(caption, (message != null) ? message : "", type);
			return true;
		}
		else return false;
	}
	/**
	 * 
	 */	
	public void doneRefreshing() // will be called by CTATContentCache upon completion of refresh
	{
		synchronized(lock) // to avoid race conditions on `refreshing`
		{
			if(!refreshing)
				return;
			
			if(progframe != null)
			{
				progframe.dispose();
				UI.refreshprogbar = null;
				progframe = null;
			}
			
			refreshing = false;
		}
	}
	/**
	 * 
	 */	
	public void showOfflineIcon()
	{
		if(awtMain != null)
		{
			Image img;
			try
			{
				img = ImageIO.read(new java.io.File("ctat-iconOFFLINE.ico"));
			}
			catch(java.io.IOException e)
			{
				JOptionPane.showMessageDialog(null, "Couldn't find tray icon image: ctat-iconOFFLINE.ico"); // TODO remove
				return;
			}
			
			((TrayIcon)awtMain).setImage(img);
			((TrayIcon)awtMain).setToolTip("CTAT Local TutorShop -- offline mode");
			
			MenuItem mi = ((TrayIcon)awtMain).getPopupMenu().getItem(1); // 1 is the index of the "offline" menu; TODO don't hard code this number
			mi.setLabel(ONLINE);
			mi.setActionCommand(ONLINE);
		}
		/*else if(st4jMain != null)
		{ 
			st4jMain.setIcon(new SysTrayMenuIcon("ctat-iconOFFLINE"));
			st4jMain.setToolTip("CTAT Local TutorShop -- offline mode");
			
			SysTrayMenuItem stmi = st4jMain.getItem(OFFLINE);
			stmi.setLabel(ONLINE);
			stmi.setActionCommand(ONLINE);
		}*/
	}
	/**
	 * 
	 */	
	public void showOnlineIcon()
	{
		if(awtMain != null)
		{
			Image img;
			try
			{
				img = ImageIO.read(new java.io.File("ctat-icon.ico"));
			}
			catch(java.io.IOException e)
			{
				JOptionPane.showMessageDialog(null, "Couldn't find tray icon image: ctat-icon.ico"); // TODO remove
				return;
			}
			
			((TrayIcon)awtMain).setImage(img);
			((TrayIcon)awtMain).setToolTip("CTAT Local TutorShop");
			
			MenuItem mi = ((TrayIcon)awtMain).getPopupMenu().getItem(1); // 1 is the index of the "online" menu; TODO don't hard code this number
			mi.setLabel(OFFLINE);
			mi.setActionCommand(OFFLINE);
		}
		/*else if(st4jMain != null)
		{
			st4jMain.setIcon(new SysTrayMenuIcon("ctat-icon"));
			st4jMain.setToolTip("CTAT Local TutorShop");
			
			SysTrayMenuItem stmi = st4jMain.getItem(ONLINE);
			stmi.setLabel(OFFLINE);
			stmi.setActionCommand(OFFLINE);
		}*/
	}
	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e)
	{
		handleMenuChoice(e.getActionCommand());
	}
	/**
	 * 
	 */	
	private void handleMenuChoice(String choice)
	{
		if(choice.equals(REFRESH))
		{
			synchronized(lock) // to avoid race conditions on `refreshing`
			{
				if(refreshing)
					return; // refresh operation is already underway
			
				refreshing = true;
			}
			
			JFrame frame = new JFrame();                         // This is just a
    		frame.setAlwaysOnTop(true);                          // trick to send the
    		frame.setVisible(true);                              // JOptionPane to the
    		frame.setVisible(false);                             // front of the screen.
    		
    		int yesno = JOptionPane.showConfirmDialog(frame, "Are you sure you want to refresh the locally cached content?\n"+
    				"This should be done only with a reliable and fast internet connection",
    				"Refresh local cache?", JOptionPane.YES_NO_OPTION);
			
    		frame.dispose();
    		
    		if(yesno == JOptionPane.YES_OPTION)
    		{
    			progframe = new JFrame("TutorShop");
    			//progframe.add(new JLabel("Refreshing content"));
    			JPanel panel = new JPanel();
    			//CTATLink.visualProgress = new CTATVisualProgressTask(panel);
    			UI.refreshprogbar = new JProgressBar(0,100);
    			UI.refreshprogbar.setMinimumSize(new Dimension(320,20));
    			UI.refreshprogbar.setPreferredSize(new Dimension(320,20));
    			UI.refreshprogbar.setMaximumSize(new Dimension(5000,20));
    			UI.refreshprogbar.setString("Refreshing contents");
    			UI.refreshprogbar.setStringPainted(true);
    			UI.refreshprogbar.setValue(0);
    			panel.add(UI.refreshprogbar);
    			progframe.add(panel);
    			progframe.setVisible(true);
    			progframe.pack();

    			// do the actual refresh
    			(new CTATTutorUpdater(null,null,null)).updateContent();
    			
    			// `refreshing` will be set to false upon completion of the refresh, when CTATContentCache calls this class's doneRefreshing method
    		}
    		else
    		{
    			refreshing = false;
    		}
		}
		else if(choice.equals(OFFLINE))
		{
			showOfflineIcon();
			OnlineOfflineManager.goOffline();
		}
		else if(choice.equals(ONLINE))
		{
			OnlineOfflineManager.goOnline();
			
			showOnlineIcon();
		}
		else if(choice.equals(EXIT))
		{		
			/*
			int okCancel = JOptionPane.showConfirmDialog(null,
					"Are you sure you want to quit TutorShop?",
					"Confirm Exit TutorShop", 
					JOptionPane.OK_CANCEL_OPTION);
			if (okCancel != JOptionPane.OK_OPTION)
				return;
			*/	
			
			if (wserver != null && wserver.getHandler() instanceof CTATHTTPHandler)
				((CTATHTTPHandler) wserver.getHandler()).enqueuePushResponse(PushResponse.Exit);
			long ms = CTATLink.pushPollingInterval+2000;
			try {
				Thread.sleep(ms);
			} catch (InterruptedException ie) {
				trace.errStack("Error while sleeping "+ms+"ms awaiting poll for exit", ie);
			}
			System.exit(0);
		}
		else if (choice.equals (RESTART))
		{
			CTATFlashTutorShop.launchBrowser ();
		}
	}
}
