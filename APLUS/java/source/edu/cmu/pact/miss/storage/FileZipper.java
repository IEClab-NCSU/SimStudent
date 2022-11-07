package edu.cmu.pact.miss.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.WebStartFileDownloader;

public class FileZipper {

	/** Buffer for efficient reading */
	private static final int BUFFER_SIZE = 2048;
	/**	Name of the current foil-log folder */
	private static final String FOIL_LOG_DIR = "foil-log";
	/**	Name of the current log folder */
	private static final String LOG_DIR = "log";
	/** Name of the current pr-age folder */
	private static final String PR_AGE_DIR = "PR-age";
	/**	 */
	private static String[] files = {
			WebStartFileDownloader.SimStWebStartDir + FOIL_LOG_DIR,
			WebStartFileDownloader.SimStWebStartDir + LOG_DIR,
			WebStartFileDownloader.SimStWebStartDir + PR_AGE_DIR
	};
	/**	 */
	private static boolean formattedDateInitialized = false;
	/**	 */
	private static String fmtDate = "";
	/**	Separator for the current underlying OS */
	public static final  String FILE_SEPARATOR = System.getProperty("file.separator");
	/**	 */
	private static boolean removeOldFiles = true;
	
	/**
	 * Method to archive the files {@link FileZipper#FOIL_LOG_DIR, FileZipper#LOG_DIR, FileZipper#PR_AGE_DIR}
	 * Zips the folders and produces the output in the userID_Date.zip
	 * @param userID
	 * @throws IOException
	 */
	public static void archiveFiles(String userID) throws IOException {

		long startTime = System.currentTimeMillis();
		FileOutputStream dest = null;
		CheckedOutputStream checksum = null;
		ZipOutputStream zos = null;
		BufferedInputStream origin = null;

		dest = new FileOutputStream(WebStartFileDownloader.SimStWebStartDir + userID + "_" + formattedDate() + ".zip");
		checksum = new CheckedOutputStream(dest, new Adler32());
		zos = new ZipOutputStream(new BufferedOutputStream(checksum));

		for (int i = 0; i < files.length; i++) {
			String fileName = files[i] + "_" + userID + "_" + formattedDate();
			File f = new File(fileName);
			visitAllFiles("", f, zos);
		}

		if(trace.getDebugCode("rr"))
			trace.out("rr", "Time to archive files took: " + (System.currentTimeMillis() - startTime) + " msec.");
		zos.flush();
		zos.close();
	}

	public static String formattedDate() {
		
		if(formattedDateInitialized)
			return fmtDate;
		
		formattedDateInitialized = true;
		Calendar cal = new GregorianCalendar();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		fmtDate =  dateFormat.format(cal.getTime());
		return fmtDate;
	}
	
	public static void removeOldFiles() {
		
		if(removeOldFiles) {
			for(int i = 0; i < files.length ; i++)
				deleteDir(new File(files[i]));
			removeOldFiles = false;
		}
	}
	
	/**
	 * 
	 * @param dir
	 * @return
	 */
	private static boolean deleteDir(File dir) {
		
		if(dir.isDirectory()) {
			String files[] = dir.list();
			for(int i = 0; i < files.length; i++) {
				boolean result = deleteDir(new File(dir, files[i]));
				if(!result)
					return false;
			}
		}
		
		return dir.delete();
	}
	
	private static void visitAllFiles(String path, File dir, ZipOutputStream zos)
			throws IOException {

		// trace.out("Enter visitAllFiles path: " + path + " dir: " +
		// dir + " dirName: " + dir.getName());
		if (dir != null && dir.list() != null && dir.list().length == 0) {
			zos.putNextEntry(new ZipEntry(path + FILE_SEPARATOR + dir.getName() + "/"));
			return;
		}

		if (dir != null && dir.list() != null) {
			for (String fileName : dir.list()) {
				// trace.out("fileName: " + fileName);
				if (path.equals("")) {
					addFileToZip(dir.getName(), dir.getAbsolutePath() + FILE_SEPARATOR + fileName, zos);
				} else {
					addFileToZip(path + FILE_SEPARATOR + dir.getName(), dir.getAbsolutePath() + FILE_SEPARATOR + fileName, zos);
				}
			}
		}
	}

	private static void addFileToZip(String path, String srcFile,
			ZipOutputStream zos) throws IOException {

		// trace.out("Enter addFileToZip path: " + path + " srcFile: "
		// + srcFile);
		File file = new File(srcFile);
		if (file.isDirectory()) {
			visitAllFiles(path, file, zos);
		} else {

			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			// trace.out("Adding the zip entry: " + (path + "\\" +
			// file.getName()));
			zos.putNextEntry(new ZipEntry(path + FILE_SEPARATOR + file.getName()));
			while ((len = in.read(buf)) > 0) {
				zos.write(buf, 0, len);
			}
		}
	}

	public static void main(String[] args) throws IOException {

		long startTime = System.currentTimeMillis();
		FileOutputStream dest = null;
		CheckedOutputStream checksum = null;
		ZipOutputStream zos = null;
		BufferedInputStream origin = null;
		String[] files = {
				"C:\\pact-cvs-tree\\Tutors\\SimSt\\WebStart\\SimStAlgebraV8\\foil-log",
				"C:\\pact-cvs-tree\\Tutors\\SimSt\\WebStart\\SimStAlgebraV8\\log",
				"C:\\pact-cvs-tree\\Tutors\\SimSt\\WebStart\\SimStAlgebraV8\\PR-age" };

		dest = new FileOutputStream(
				"C:\\pact-cvs-tree\\Tutors\\SimSt\\WebStart\\SimStAlgebraV8\\archive-TestSS.zip");
		checksum = new CheckedOutputStream(dest, new Adler32());
		zos = new ZipOutputStream(new BufferedOutputStream(checksum));

		for (int i = 0; i < files.length; i++) {
			File f = new File(files[i]);
			visitAllFiles("", f, zos);
		}

		trace.out("CheckSum: " + checksum.getChecksum().getValue()
				+ " duration: " + (System.currentTimeMillis() - startTime));
		zos.flush();
		zos.close();
	}

}
