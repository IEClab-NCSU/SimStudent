package edu.cmu.pact.Utilities;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.View.BRPanel;
import edu.cmu.pact.jess.MT;



public class PersonnelInfo {
	
	public final static String[] personnelList = {
		"Mike Adams",
		"Ravi Aggarwal",
		"Vincent Aleven",
		"Chris Aniszczyk",
		"Melissa Butler",
		"Sanket Choksey",
		"Mike Czapik",
		"Sandy Demi", 
		"Vanessa De Gennaro",
		"Laurens Feenstra",
		"Mingyu Feng",
		"Jonathan Eric Freyberger",
		"Erik Harpstead",
		"Neil Heffernan",
		"Chang Hsin-Chang",
		"Matt Jarvis",
		"Kevin Jeffries",
		"Goss Nuzzo Jones",
		"Victoria Keiser",
		"Wing-Hong Ko",
		"Ken Koedinger",
		"Gustavo Lacerda",
		"John laPlante",
		"Brett Leber",
		"Tom Livak",
		"Borg Lojasiewicz",
		"Collin Lynch",
		"Dipti Mandalia",
		"Noboru Matsuda",
		"Bruce McLaren",
		"Alvaro Silva Pereira",
		"Ethan Pfeifer",
		"Gus Prevas",
		"Rohan Raizada",
		"Jim Rankin",
		"Leena Razzaq",
		"Michael Ringenberg",
		"Mike Schneider",
		"Eric Schwelm",
		"Jonathan Sewall",
		"Noble Shore",
		"Alida Skogsholm",
		"Shawn Snyder",
		"Michael Sobczak",
		"Ross Strader",
		"Dan Tasse",
		"Ruta Upalekar",
		"Martin van Velsen",
		"Michael Weber",
		"Stephanie Yang",
		"Jian Xiong (Daryl) Yeo",
		"Kevin Zhang",
		"Zhenhua Zhang",
		"Kevin Zhao"
	};
	  
	public static void showAboutBox(JFrame parent) {
		
		String createdBy = "";
		String temp = "";
		int lineLength = 50;
		int length = PersonnelInfo.personnelList.length;
		
		for (int i = 0; i < length - 1; i++) {
			temp += PersonnelInfo.personnelList[i] + ", ";
			if (temp.length() > lineLength) {
				createdBy += temp + "<br>";
				temp = "";
			}
		}
		createdBy += temp;
		createdBy += " and " + PersonnelInfo.personnelList[length - 1] + ".<br>";
		
		String javaVersion = System.getProperty("java.version");
		String javaHome = System.getProperty ("java.home");
		String javaVendor = System.getProperty ("java.vendor");
		String jessVersion = (VersionInformation.includesJess() ? MT.getJessVersion() : null);
		
		JOptionPane.showMessageDialog(
			parent,
			"<html><b>Cognitive Tutor Authoring Tools</b><br><br>"
				+ "(c) 2004-2012 PACT Lab, Human Computer Interaction Institute,<br>"
				+ "Carnegie Mellon Universty, Pittsburgh PA.<br>All rights reserved.<br><br>"
				+ VersionInformation.getFileReferenceString()
				+ "Version: "
				+ edu.cmu.pact.Utilities.VersionInformation.RELEASE_NAME
				+ "<br>"
				+ "Build Number: "
				+ edu.cmu.pact.Utilities.VersionInformation.VERSION_NUMBER
				+ "<br>"
				+ "Build Date: "
				+ edu.cmu.pact.Utilities.VersionInformation.BUILD_DATE
				+ "<br><br>"  
				+ "For more information visit: "
				+ BRPanel.HOME_URL
				+ "<br><br>"
				+ "Using java version: " + javaVersion  + ", " + javaVendor  
				+ "<br>"
				+ "located at: " + javaHome
				+ "<br><br>"
			    + "Uses compiled portions of Jess, the Java Expert System<br>"
				+ "Shell. Jess is a separately licensed application and is<br>"
				+ "not available for Open Source licensing under any GPL<br>"
				+ "license. To license and download Jess, please visit the<br>"
				+ "Jess website at http://www.jessrules.com/."
				+ (jessVersion != null ? "<br>"+jessVersion : "")
				+ "<br><br>"
				+ "Created by: "  
				+ createdBy 
				+ "<br></html>",
			"About...",
			JOptionPane.INFORMATION_MESSAGE);
	}
}
