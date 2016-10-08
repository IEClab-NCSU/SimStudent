package edu.cmu.pact.Log.DataShopSampleSplitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.server.UID;

public class DataShopSampleSplitter{

	/** Parses the string passed as argument and stores the information in the array
	 * of strings passed as argument.
	 * @param fileLine the string to be parsed
	 * @param rowInfo the information in the string passed as argument after being parsed */
	private static void parseLine( String fileLine, String [] rowInfo )
	{
	 	String tab = new String( "	" );
		int index = 0, counter = 0;

		while( true )
		{
			index = fileLine.indexOf( tab );
			if( index >= 0 )
			{
				rowInfo[counter++] = fileLine.substring( 0, index );
				fileLine = fileLine.substring( index + 1 );
			}
			else
				break;
		}
	}

	/** Check is the file passed as argument is a tab-delimited DataShop sample file.
	 * @param inf the file to be checked
	 * @return true if the file passed as argument is a tab-delimited file, false otherwise
	 * throws IOException */
	 public static boolean isTabDelimited( File inf ) throws IOException
	 {
		FileReader fileReader = new FileReader( inf );
		BufferedReader bufferedReader = new BufferedReader( fileReader );
		StringBuffer stringBuffer;
		String line;
		String [] parsedLine;

		// Find the number of tabs in the first line
		int firstLineTabs = 0;
		if( bufferedReader.ready() )
		{
			stringBuffer = new StringBuffer( bufferedReader.readLine() );
			line = stringBuffer.toString();
			parsedLine = new String [40];
			parseLine( line, parsedLine );
			while( parsedLine[firstLineTabs++] != null )
				;
		}

		// Find the number of tabs in the second line
		int secondLineTabs = 0;
		if( bufferedReader.ready() )
		{
			parsedLine = new String[40];
			stringBuffer = new StringBuffer( bufferedReader.readLine() );
			line = stringBuffer.toString();
			parseLine( line, parsedLine );
			while( parsedLine[secondLineTabs++] != null )
				;
		}

		// Check if any of the first two lines have more than 5 tabs
		if( --firstLineTabs > 5 || --secondLineTabs > 5 )
			return true;
		else
		 	return false;
	 }

	/** Returns a RowIndices object that stores the clomun numbers for each piece
	 * of information we need from the tab-delimited file.
	 * @param fileLine the line in the tad-delimeted file that contains the names
	 * of the columns.
	 * @return a RowIndices object
	 * @throws IOException */
	private static RowIndices getRowIndices( String fileLine ) throws IOException
	{
	 	String tab = new String( "	" );
	 	String header = new String("");
		int index = 0, counter = 0;
		RowIndices rowIndices = new RowIndices();
		while( true )
		{
			// Parse the string passed as argument using the tab character as delimiter
			index = fileLine.indexOf( tab );
			if( index >= 0 )
			{
				header = fileLine.substring( 0, index );
				fileLine = fileLine.substring( index + 1 );
			}
			else
				break;

			// If the column name is the same as one of the column names we need in the
			// XML file, update is column number in the RowIndices object
			if ( new String( "Anon Student Id" ).equals( header ) )
				rowIndices.setUserGuid( counter );
			else if( new String("Session Id").equals( header ) )
				rowIndices.setSessionID( counter );
			else if ( new String( "Time" ).equals( header ) )
				rowIndices.setDateTime( counter );
			else if ( new String( "Time Zone" ).equals( header ) )
				rowIndices.setTimezone( counter );
			else if ( new String( "Student Response Type" ).equals( header ) )
				rowIndices.setTransactionName( counter );
			else if ( new String( "Problem Name" ).equals( header ) )
				rowIndices.setProblemName( counter );
			else if ( new String( "Selection" ).equals( header ) )
				rowIndices.setSelection( counter );
			else if ( new String( "Action" ).equals( header ) )
				rowIndices.setAction( counter );
			else if ( new String( "Input" ).equals( header ) )
				rowIndices.setInput( counter );
			counter++;
		}
		return rowIndices;
	}

	/** Returns the column number of the column whose name is passed as argument.
	 * @param fileLine the line with the column names (to be parsed)
	 * @param column the name of the column whose number is returned
	 * @return the number of the column  whose name id passed as argument */
	private static int getColumnIndex( String fileLine, String column )
	{
	 	String tab = new String( "	" );
		int index = 0, counter = 0;

		while( true )
		{
			// Find the index of the tab characted in the remaining string
			index = fileLine.indexOf( tab );
			if( index >= 0 )
			{
				// Whenever a column name that is the same as the one passed as argument
				// is found, it's number is returned
				if( fileLine.substring( 0, index ).equals( column ) )
					return counter;
				fileLine = fileLine.substring( index + 1 );
				counter++;
			}
			else
				// If we got to this point, the column name passed as argument could
				// not be found in the string passed as argument
				return -1;
		}
	}

	/** Splits the file passed as argument into separate files so that all rows
	 * in each separate file have the same data in the column passed as argument.
	 * The files will be put in the same folder where the input file is and their
	 * names will be the data in the file in the column passed as argument.
	 * NOTE: the file has to be sorted by the column passed as argument.
	 * @param path the path to the file to be split into separate files
	 * @param fileName the file to be split into separate files
	 * @param column the name of the column used as criterion for splitting the file
	 * @param separateFolder true if each file the original file is broken up into
	 * will be created in a folder by itself
	 * @throws IOException
	 * @throws FileNotFoundException */
	public static void breakUpSample( String path, String fileName, String column, boolean separateFolder )
			throws IOException,	FileNotFoundException
	{
		String tab = new String( "	" );
		String fileExt = new String( ".txt" );
		String [] rowInfo = new String[40];

		FileReader fileReader = new FileReader( path + File.separator + fileName );
		BufferedReader bufferedReader = new BufferedReader( fileReader );

		// Read the first line in the file
		String firstLine = new StringBuffer( bufferedReader.readLine() ).toString();

		// Read the line with the names of the columns
		String columnNamesLine = new StringBuffer( bufferedReader.readLine() ).toString();

		// Save the column number of the needed columns in a RowIndices object
		RowIndices rowIndices = getRowIndices( columnNamesLine );

		// Find the number of the column used for splitting the file
		int columnIndex = getColumnIndex( columnNamesLine, column );

		StringBuffer stringBuffer;
		String fileLine, newColumnName = null;
		File outputFile = null;
		FileWriter fileWriter = null;
		int index = 0, counter = 0;

		// Read the rest of the file and split it into separate files
		while ( bufferedReader.ready() )
		{
			// Read the next line in the file
			stringBuffer = new StringBuffer( bufferedReader.readLine() );
			fileLine = stringBuffer.toString();

			// Parse the line and store the data in the array of strings "rowInfo"
			parseLine( fileLine, rowInfo );

			// Every time new data is found in the specified column, a new file is
			// created, using the data in that particular column as file name
			if( !rowInfo[columnIndex].equals( newColumnName ) )
			{
				if( fileWriter != null )
					fileWriter.close();
				newColumnName = new String( rowInfo[columnIndex] );

				// Put the newly created files in folders with the same names if
				// the separateFolder flag is true
				if( separateFolder == true )
				{
					outputFile = new File( path + File.separator + newColumnName );
					outputFile.mkdir();
					outputFile = new File( outputFile.toString(), newColumnName + fileExt );
				}
				else
					outputFile = new File( path + File.separator + newColumnName + fileExt );

				// Write the first row (left unchanged, usually the name of the sample) and
				// the second row (the names of the columns) to the file
				fileWriter = new FileWriter( outputFile );
				fileWriter.write( firstLine + "\n" );
				fileWriter.write( columnNamesLine + "\n" );
			}
			// Write the rest of the rows to the file
			fileWriter.write( fileLine + "\n" );
		}
		// Close all open readers and writers
		bufferedReader.close();
		fileReader.close();
		fileWriter.close();
	 }

	/** Returns an XML-type string corresponding to the tab-delimited string passed
	 * as argument.
	 * @param rowIndices a RowIndices object which contains the numbers of the columns
	 * needed in the XML file
	 * @param rowInfo the array of strings which contains all the information on one
	 * row in the tab-delimited file
	 * @param contextMessageID the id of the context message (the same for all the
	 * transactions in a problem)
	 * @returns the XML equivalent of the tab-delimited string passed as argument
	 * @throws IOException */
	private static String getXMLString( RowIndices rowIndices, String [] rowInfo,
			String contextMessageID ) throws IOException
	{
		String s = new String( "" );

		s = s.concat( "<log_action auth_token=\"\" session_id=\"" );
		s = s.concat( rowInfo[rowIndices.getSessionID()] );
		s = s.concat( "\" user_guid=\"" );
		s = s.concat( rowInfo[rowIndices.getUserGuid()] );
		s = s.concat( "\" date_time=\"" );
		s = s.concat( rowInfo[rowIndices.getDateTime()] );
		s = s.concat( "\" timezone=\"" );
		s = s.concat( rowInfo[rowIndices.getTimezone()] );
		s = s.concat( "\" action_id=\"tool_message\" source_id=\"PACT_CTAT\" " );
		s = s.concat( "external_object_id=\"\" info_type=\"tutor_message.dtd\">" );
		s = s.concat( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
		s = s.concat( "<tutor_related_message_sequence version_number=\"4\">\n" );
		s = s.concat( "<tool_message context_message_id=\"" );
		s = s.concat( contextMessageID );
		s = s.concat( "\">\n\t<problem_name>" );
		s = s.concat( rowInfo[rowIndices.getProblemName()] );
		s = s.concat( "</problem_name>\n\t<semantic_event transaction_id=\"" );
		s = s.concat( new UID().toString() );
		s = s.concat( "\" name=\"" );
		s = s.concat( rowInfo[rowIndices.getTransactionName()] );
		s = s.concat( "\"/>\n\t<event_descriptor>\n\t\t<selection>" );
		s = s.concat( rowInfo[rowIndices.getSelection()] );
		s = s.concat( "</selection>\n\t\t<action>" );
		s = s.concat( rowInfo[rowIndices.getAction()] );
		s = s.concat( "</action>\n\t\t<input>" );
		s = s.concat( rowInfo[rowIndices.getInput()] );
		s = s.concat( "</input>\n\t</event_descriptor>\n</tool_message>\n" );
		s = s.concat( "</tutor_related_message_sequence>\n</log_action>\n\n" );

		return s;
	}

	/** Convert a tab-delimimted Data Shop file to an XML file. This method assumes
	 * that the title of the table is on the first line of the file and the names of
	 * the columns are on the second line.
	 * @param inf the file to be converted
	 * @return the converted file
	 * @throws IOException */
	 public static void convertTabDelimitedToXML( File input, File output ) throws IOException
	 {
		String [] rowInfo = new String[40];

		FileReader fileReader = new FileReader( input );
		FileWriter fileWriter = new FileWriter( output );
		BufferedReader bufferedReader = new BufferedReader( fileReader );

		// Get rid of the first line in the file
		StringBuffer stringBuffer = new StringBuffer( bufferedReader.readLine() );

		// Read the line with the names of the columns
		stringBuffer = new StringBuffer( bufferedReader.readLine() );
		String fileLine = stringBuffer.toString();

		// Save the column number of the needed columns in a RowIndices object
		RowIndices rowIndices = getRowIndices( fileLine );

		// Add the XML and root prologs to the XML file
		String s = new String( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n" );
		String contextMessageID = null, problemName = null;
		fileWriter.write( s );

		// Read the rest of the file and write the XML file
		while ( bufferedReader.ready() )
		{
			// Read the next line in the file
			stringBuffer = new StringBuffer( bufferedReader.readLine() );
			fileLine = stringBuffer.toString();

			// Parse it and store the data in the array of strings "rowInfo"
			parseLine( fileLine, rowInfo );

			// Make sure we have a new context message id for every new problem
			if( !rowInfo[rowIndices.getProblemName()].equals( problemName ) )
			{
				contextMessageID = new UID().toString();
				problemName = rowInfo[rowIndices.getProblemName()];
			}

			// Convert the tab-delimited line to an XML line
			s = getXMLString( rowIndices, rowInfo, contextMessageID );
			fileWriter.write(s);
		}
		s = new String( "</root>" );
		fileWriter.write( s );

		bufferedReader.close();
		fileReader.close();
		fileWriter.close();
	 }

	/** Splits the files in the folder passed as argument into separate files.
	 * @param folderPath the path to the file to be split into separate files
	 * @param columnName the name of the column used as criterion for splitting the file
	 * @param separateFolder true if each file the original file is broken up into
	 * will be created in a folder by itself
	 * @throws IOException */
	public static void splitFiles( String folderPath, String columnName, boolean separateFolders ) throws IOException
	{
		// The list of folders and files in the folder "folderPath"
    	String [] folderList = new File( folderPath ).list();

		// The list of files in each of the above-mentioned folders
    	String [] fileList;

		// The path to the actual files to be split (they're in the folders in "folderPath")
    	String inputFileParentPath;

		File inputFileParent;
		for( int outerIndex = 0; outerIndex < folderList.length; ++outerIndex )
		{
			inputFileParentPath = folderPath + File.separator + folderList[outerIndex];
			inputFileParent = new File( inputFileParentPath );
			if( inputFileParent.isDirectory() )
			{
				fileList = inputFileParent.list();
				for( int innerIndex = 0; innerIndex < fileList.length; ++innerIndex )
					// Check if the file is a DataShop tab-delimited file
					if( isTabDelimited( new File( inputFileParentPath + File.separator + fileList[innerIndex] ) ) )
						breakUpSample( inputFileParentPath, fileList[innerIndex], columnName, separateFolders );
			}
			else
				if( isTabDelimited( new File( inputFileParentPath ) ) )
					breakUpSample( folderPath, folderList[outerIndex], columnName, separateFolders );
		}
	}

	public static void main( String args[] ) throws IOException
	{
    	// This program assumes that the files to be split are in their separate
    	// folders inside a folder whose path is "folderPath" or directly in "folderPath"
    	String folderPath = "C:\\Downloads\\CTAT Files\\Parsed Files";

		// The name of the column you want to use as a criterion for splitting the file(s)
		// into separete files. All lines with the same data in that particular column
		// will be put in the same file. The initial file should be sorted by that column.
    	String columnName = "Problem Name";

		// True if the parsed files will be put in separate folders with the same names, false otherwise
		boolean separateFolders = false;

		splitFiles( folderPath, columnName, separateFolders );
    }
}