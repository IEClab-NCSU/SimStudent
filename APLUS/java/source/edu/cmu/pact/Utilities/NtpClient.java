package edu.cmu.pact.Utilities;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.cmu.hcii.ctat.ExitableServer;

import Networking.ntp.NTPUDPClient;
import Networking.ntp.TimeInfo;

public class NtpClient extends Thread implements ExitableServer {

	/**
	 * {@link System#getProperty(String)} property name for externally-specified
	 * {@link #setRefreshInterval(int)} value.
	 */
	public static final String REFRESH_INTERVAL_PROPERTY = "NtpClient.RefreshInterval";

	/**
	 * {@link System#getProperty(String)} property name for externally-specified
	 * {@link #setNtpServer(String)} value.
	 */
	public static final String NTP_SERVER_PROPERTY = "NtpClient.NtpServer";

	/** Default value for {@link #refreshIntervalMillis}. */
	private static final int DEFAULT_REFRESH_INTERVAL_SECS = 1800;

	/**	See {@link #main(String[])}: default number of iterations in test harness. */
	private static final int DEFAULT_TEST_REFRESH_COUNT = 4;

	/** Default time server hostname. */
	private static String DEFAULT_NTP_SERVER = "3.north-america.pool.ntp.org";

	/** Time server hostname. */
	private String ntpServer = DEFAULT_NTP_SERVER;

	/** Most recent value retrieved. */
	private TimeInfo lastTimeInfo = null;
	
	/** Local time that #lastTimeInfo was refreshed. */
	private Date lastTimeInfoAsOf = null;

	/** Name of server queried for current value of {@link #lastTimeInfo}. */
	private String lastTimeInfoServer = null;

	/** Seconds between calls to update {@link #lastTimeInfo} from the {@link #ntpServer}. */
	private int refreshInterval = DEFAULT_REFRESH_INTERVAL_SECS;

	/** Set by {@link #quit()} to stop the thread. */
	private boolean quitting = false;

	/** The currently-running thread. Used by {@link #quit()}. */
	private Thread myThread;

	/** See {@link #startExiting()}. */
	private volatile boolean nowExiting;

	/**
	 * Return the current {@link Date} adjusted by {@link #getTimeOffset()}. 
	 * @return current time adjusted by last-retrieved offset from {@link #getNtpServer()}
	 */
	public Date getNTPDate(){
		return getNTPDate(new Date(), null);
	}

	/**
	 * Return the current {@link Date} adjusted by {@link #getTimeOffset()}. 
	 * @param ntpServer if not null, will return the name of the server queried in
	 *         the array's 1st element
	 * @return current time adjusted by last-retrieved offset from {@link #getNtpServer()}
	 */
	public Date getNTPDate(String[] ntpServer){
		return getNTPDate(new Date(), ntpServer);
	}

	/**
	 * Return a {@link Date} adjusted by {@link #getTimeOffset()}. 
	 * @param dateToAdjust timestamp to adjust
	 * @param ntpServer if not null, will return the name of the server queried in
	 *         the array's 1st element
	 * @return argument adjusted by last-retrieved offset from {@link #getNtpServer()}
	 */
	public Date getNTPDate(Date dateToAdjust, String[] ntpServer){
		return new Date(dateToAdjust.getTime() + getTimeOffset(ntpServer));
	}

	/**
	 * Return the current adjusted time in the format of
	 * {@link System#currentTimeMillis()}, that is, the difference,
	 * measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
	 * @return current time adjusted by last-retrieved offset from {@link #getNtpServer()}
	 */
	public long getNTPTime(){
		return getNTPTime(System.currentTimeMillis(), null);
	}

	/**
	 * Return an adjusted timestamp. Argument should be in the format of
	 * {@link System#currentTimeMillis()}, that is, the difference,
	 * measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
	 * @param timeToAdjustMillis timestamp to adjust
	 * @param ntpServer if not null, will return the name of the server queried in
	 *         the array's 1st element
	 * @return argument adjusted by last-retrieved offset from {@link #getNtpServer()}
	 */
	public long getNTPTime(long timeToAdjustMillis, String[] ntpServer){
		return timeToAdjustMillis + getTimeOffset(ntpServer);
	}

	/**
	 * Get clock offset needed to adjust local clock to match remote clock. Does not
	 * query the NTP server, but instead reads the last-retrieved info. Result is the
	 * difference between the 2 clocks' readings for the number of
	 * milliseconds since midnight, January 1, 1970 UTC
	 * @return offset, in ms, given by last-retrieved info from {@link #getNtpServer()};
	 *         returns 0 on error, with diagnostics to {@link trace#err(String)}
	 */
	public long getTimeOffset() {
		return getTimeOffset(null);
	}

	/**
	 * Get clock offset needed to adjust local clock to match remote clock. Does not
	 * query the NTP server, but instead reads the last-retrieved info. Result is the
	 * difference between the 2 clocks' readings for the number of
	 * milliseconds since midnight, January 1, 1970 UTC
	 * @param ntpServer if not null, will return the name of the server queried in
	 *         the array's 1st element
	 * @return offset, in ms, given by last-retrieved info from {@link #getNtpServer()};
	 *         returns 0 on error, with diagnostics to {@link trace#err(String)}
	 */
	public long getTimeOffset(String[] ntpServer) {
		TimeInfo info = null;
		long asOf = -1;
		long offset = 0;
		
		synchronized(this) {
			info = lastTimeInfo;
			if (info != null) {
				offset = info.getOffset().longValue();
				if (lastTimeInfoAsOf != null)
					asOf = lastTimeInfoAsOf.getTime();
				if (ntpServer != null && ntpServer.length > 0)
					ntpServer[0] = this.lastTimeInfoServer;
			}
		}
		if (info == null)
			trace.err("Null TimeInfo from server "+getNtpServer());
		
		long d = System.currentTimeMillis() - asOf;
		if (asOf >= 0 && d > getRefreshInterval())
			trace.err("TimeInfo from server "+getNtpServer()+" is "+(d/1000)+"."+(d%1000)
					+" secs old; refresh interval is "+getRefreshInterval()+" secs");
		return offset;
	}
	
	/**
	 * Get the last-reported network delay for communicating with the server.
	 * @return {@link TimeInfo#getDelay()}; null if {@link #lastTimeInfoAsOf} null.
	 */
	public Long getDelay() {
		TimeInfo info = lastTimeInfo; 
		if (info == null)
			return null;
		else
			return info.getDelay();
	}

	/**
	 * Set {@link #lastTimeInfo}, {@link #lastTimeInfoAsOf} using {@link #refreshTimeInfo(String)}.
	 * First tries host given by {@link #getNtpServer()}.
	 * If that fails and {@link #ntpServer} is a different host, tries it.
	 * If that fails and {@link #DEFAULT_NTP_SERVER} is a different host, tries it.
	 * @return result of last trial
	 */
	private boolean refreshTimeInfo() {
		String ntpServer = this.getNtpServer();
		if (refreshTimeInfo(ntpServer))
			return true;
		if (this.ntpServer != null && !(this.ntpServer.equalsIgnoreCase(ntpServer))) {
			if (refreshTimeInfo(ntpServer = this.ntpServer))
				return true;
		}
		if (!(DEFAULT_NTP_SERVER.equalsIgnoreCase(ntpServer)))
			return refreshTimeInfo(ntpServer = DEFAULT_NTP_SERVER);
		return false;
	}

	/**
	 * Set {@link #lastTimeInfo} and {@link #lastTimeInfoAsOf} from given NTP server.
	 * @param ntpServer hostname of server to contact
	 * @return false if any error; else true
	 */
	private boolean refreshTimeInfo(String ntpServer) {
		InetAddress hostAddr = null;
		NTPUDPClient ntpClient = null;
		TimeInfo info = null;
		try {
			ntpClient = new NTPUDPClient();

			// We want to timeout if a response takes longer than 10 seconds
			ntpClient.setDefaultTimeout(10000);

			ntpClient.open();
			hostAddr = InetAddress.getByName(ntpServer);
			info = ntpClient.getTime(hostAddr); //Gets the current time
			info.computeDetails(); //Computes offset from local to ntp time, taking into account trip delays
			synchronized(this) {
				lastTimeInfo = info;
				lastTimeInfoAsOf = new Date();
				lastTimeInfoServer = ntpServer;
			}
		} 
		catch (Throwable e) {
			trace.errStack("Error retrieving time from server "+ntpServer+" (IP: "+hostAddr+"):"
					+e+(e.getCause() == null ? "" : "; cause "+e.getCause().toString()), e);
			return false;
		}
		finally {
			try { ntpClient.close(); } catch (Exception e) {}
		}
		if (trace.getDebugCode("ntp")) {
			if (info == null)
				if (trace.getDebugCode("ntp")) trace.outNT("ntp", "NtpClient timeInfo null");
			else
				if (trace.getDebugCode("ntp")) trace.outNT("ntp", "NtpClient timeInfo offset "+info.getOffset()
						+", delay "+info.getDelay()+", comments "+info.getComments());				
		}
		return true;
	}

	/**
	 * Return {@link System#getProperty(String) System.getProperty(NTP_SERVER_PROPERTY)} if set;
	 * else {@link #ntpServer}.
	 * @return System property or {@link #ntpServer}
	 */
	public String getNtpServer() {
		String prop = System.getProperty(NTP_SERVER_PROPERTY);
		if (prop != null && prop.length() > 0)
			return prop;
		else
			return ntpServer;
	}

	/**
	 * @param ntpServer new value for {@link #ntpServer}
	 */
	public void setNtpServer(String ntpServer) {
		this.ntpServer = ntpServer;
	}

	/**
	 * Call {@link #refreshTimeInfo()} every {@link #getRefreshInterval()} seconds
	 * until {@link #quit()} has been called. Calls {@link #notifyAll()} after each refresh.
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		myThread = Thread.currentThread();
		while (!quitting) {
			refreshTimeInfo();
			synchronized(this) {
				notifyAll();
			}
			try {
				if (quitting)
					continue;
				else
					sleep(getRefreshInterval());
			} catch (InterruptedException e) {
				if (!quitting)
					trace.err("NtpClient interrupted: "+e
							+(e.getCause() == null ? "" : "; cause "+e.getCause().toString()));
			}
		}
	}

	/**
	 * Quit the thread asynchronously. Sets {@link #quitting} and calls {@link #interrupt()}
	 * on {@link #myThread}, but doesn't wait for thread to stop.
	 */
	public void quit() {
		quitting = true;
		if (myThread != null)
			myThread.interrupt();
	}

	private static void usageExit(String[] args, int i, Throwable e) {
		System.out.printf("Error in command-line argument %d \"%s\": %s.\n"
				+"Usage:\n"
				+"    NtpClient [-h hostname] [-t timer] [-n count] [-c]\n"
				+"where--\n"
				+"    hostname is the NTP server (default %s);\n"
				+"    timer is the refresh interval in seconds (default %d);\n"
				+"    count is the number of refreshes to wait for (default %d);\n"
				+"    -c means run until count refreshes have occurred.\n",
				i, (args.length <= i ? "(range error)" : args[i]),
				(e == null ? "null" : e.toString()),
				DEFAULT_NTP_SERVER,
				DEFAULT_REFRESH_INTERVAL_SECS,
				DEFAULT_TEST_REFRESH_COUNT);
		System.exit(2);
	}
	/**
	 * Test harness. Gets the current time, adjusted.
	 * @param single arg is alternate {@link #setNtpServer(String)} name;
	 * 	      default {@value #DEFAULT_NTP_SERVER}
	 */
	public static void main(String[] args) throws InterruptedException {
		NtpClient nc = new NtpClient();
		boolean continuous = false;
		int nRefreshes = DEFAULT_TEST_REFRESH_COUNT;
		for (int i = 0; i < args.length && args[i].startsWith("-"); ++i) {
			try {
				char c = args[i].charAt(1);
				switch (c) {
				case 'h': case 'H':
					nc.setNtpServer(args[++i]); break;
				case 't': case 'T':
					nc.setRefreshInterval(Integer.parseInt(args[++i])); break;
				case 'n': case 'N':
					nRefreshes = Integer.parseInt(args[++i]); break;
				case 'c': case 'C':
					continuous = true; break;
				default:
					throw new IllegalArgumentException("Unknown command-line option "+c);
				}
			} catch (Exception e) {
				usageExit(args, i, e);
			}
		}
		if (!continuous) {
			nc.refreshTimeInfo();
			testClient(nc);
			return;
		}
		nc.start();
		for (int i = 0; i < nRefreshes; ++i) {
			synchronized(nc) {
				nc.wait();
			}
			testClient(nc);
		}
		System.out.printf("Thread state before quit:      %s.\n", nc.getState().toString());
		nc.quit();
		sleep(1000);
		System.out.printf("Thread state 1 sec after quit: %s.\n", nc.getState().toString());
	}

	/**
	 * Test {@link #getNTPDate(Date)} and {@link #getDelay()}. Prints to {@link System#out}.
	 * @param nc instance to test
	 */
	private static void testClient(NtpClient nc) {
		String[] ntpServer = new String[1];
		Date localNow = new Date();
		Date ntpNow = nc.getNTPDate(localNow, ntpServer);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ZZZ");
		System.out.printf("%6s = %s from %s\n%6s = %s, delta %d ms, delay %d\n",
				"NTP", df.format(ntpNow), ntpServer[0],
				"local", df.format(localNow),
				ntpNow.getTime()-localNow.getTime(), nc.getDelay());
	}

	/**
	 * Return {@link System#getProperty(String) System.getProperty(REFRESH_INTERVAL_PROPERTY)},
	 * converted to milliseconds, if set. Else return {@link #refreshIntervalMillis}.
	 * @return the refresh interval in seconds
	 */
	public int getRefreshInterval() {
		try {
			int result = Integer.parseInt(System.getProperty(REFRESH_INTERVAL_PROPERTY));
			return result * 1000;
		} catch (Exception e) {
			return refreshInterval * 1000;
		}
	}

	/**
	 * @param refreshIntervalSecs new interval in seconds
	 */
	public void setRefreshInterval(int refreshIntervalSecs) {
		this.refreshInterval = refreshIntervalSecs;
	}

	/**
	 * @return the {@link #lastTimeInfoServer}
	 */
	public synchronized String getLastTimeInfoServer() {
		return lastTimeInfoServer;
	}

	/**
	 * @return {@link #nowExiting}
	 */
	public boolean isExiting() {
		return nowExiting;
	}

	/**
	 * Set {@link #nowExiting} and call {@link #quit()}.
	 * @return prior value of {@link #nowExiting}
	 */
	public boolean startExiting() {
		boolean result = nowExiting;
		nowExiting = true;
		if( trace.getDebugCode("ls"))
			trace.out("ls", "NtpClient.startExiting(): previous nowExiting "+result+"; to quit()");
		quit();
		return result;
	}
}
