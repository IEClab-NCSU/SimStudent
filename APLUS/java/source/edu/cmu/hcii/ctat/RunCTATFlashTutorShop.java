/**
 *
 */
package edu.cmu.hcii.ctat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import edu.cmu.hcii.ctat.CTATFlashTutorShop;
import edu.cmu.pact.Utilities.ProcessRunner;

/**
 * Launches {@link CTATFlashTutorShop#main(String[])}.
 */
public class RunCTATFlashTutorShop {

	/**
	 * .\jre\bin\java.exe -classpath .\lib;.\jre\lib;.\htdocs;
	 * .\htdocs\FlashTutors;.\lib\TutorShopUSB.jar;.\lib\ctat.jar;.\lib\jna.jar;. %*
	 *  edu.cmu.hcii.ctat.CTATFlashTutorShop -normal
	 * @param args -v sets "verbose" to {@link ProcessRunner}
	 */
	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append(".").
			append(File.separator).append("jre").
			append(File.separator).append("bin").
			append(File.separator).append("java");
		String java = sb.toString();

		sb = new StringBuilder();
		sb.append(".").append(File.separator).append("lib").
			append(File.pathSeparator).append(".").append(File.separator).append("jre").append(File.separator).append("lib").
			append(File.pathSeparator).append(".").append(File.separator).append("htdocs").
			append(File.pathSeparator).append(".").append(File.separator).append("htdocs").append(File.separator).append("FlashTutors").
			append(File.pathSeparator).append(".").append(File.separator).append("lib").append(File.separator).append("TutorShopUSB.jar").
			append(File.pathSeparator).append(".").append(File.separator).append("lib").append(File.separator).append("ctat.jar").
			append(File.pathSeparator).append(".").append(File.separator).append("lib").append(File.separator).append("jna.jar").
			append(File.pathSeparator).append(".");
		String classpath = sb.toString();

		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add(java);
		cmd.add("-classpath");
		cmd.add(classpath);
		cmd.add("edu.cmu.hcii.ctat.CTATFlashTutorShop");
		cmd.add("-normal");
		String[] cmdLine = cmd.toArray(new String[cmd.size()]);

		ProcessRunner pr = new ProcessRunner(Arrays.asList(args).contains("-v"));
		pr.exec(cmdLine);
	}
}
