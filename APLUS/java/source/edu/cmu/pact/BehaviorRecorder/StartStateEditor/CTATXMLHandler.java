/**------------------------------------------------------------------------------------
 $Author: blojasie $ 
 $Date: 2012-05-31 11:09:39 -0400 (Thu, 31 May 2012) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.3  2011/08/31 16:31:42  sewall
 Add undo for text areas and text fields.

 Revision 1.2  2011/08/26 13:12:13  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.1  2011/07/01 19:31:40  vvelsen
 Added an XML viewer so we can debug both the entire start state as well as any outgoing messages.

 
 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.cmu.hcii.ctat.CTATBase;

/**
 * 
 */
public class CTATXMLHandler extends DefaultHandler 
{

	private JTree xmlJTree;
	//DefaultTreeModel treeModel;
	int lineCounter;
	DefaultMutableTreeNode base = new DefaultMutableTreeNode("XML Viewer");
	//static CTATXMLViewer treeViewer = null;
	//JTextField txtFile = null;

    /**
	 * 
	 */
	public void setTree (JTree aTree)
	{
		xmlJTree=aTree;
	}
    /**
	 * 
	 */
    private void debug (String aMessage)
    {
    	CTATBase.debug ("CTATXMLHandler",aMessage);
    }		
	/**
	 * 
	 */			
	@Override
	public void startElement(String uri, String localName, String tagName, Attributes attr) throws SAXException 
	{
		debug ("startElement ()");
		
		DefaultMutableTreeNode current = new DefaultMutableTreeNode(tagName);

		base.add(current);
		base = current;

		for (int i = 0; i < attr.getLength(); i++) 
		{
			DefaultMutableTreeNode currentAtt = new DefaultMutableTreeNode(attr.getLocalName(i) + " = "
					+ attr.getValue(i));
			base.add(currentAtt);
		}
	}
	/**
	 * 
	 */		
	public void skippedEntity(String name) throws SAXException 
	{
		debug ("Skipped Entity: '" + name + "'");
	}
	/**
	 * 
	 */		
	@Override
	public void startDocument() throws SAXException 
	{
		debug ("startDocument ()");
		
		super.startDocument();
		base=new DefaultMutableTreeNode("XML Viewer");
		DefaultTreeModel model=(DefaultTreeModel) xmlJTree.getModel();
		if (model!=null)
		 model.setRoot (base);
	}
	/**
	 * 
	 */		
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		String s = new String(ch, start, length).trim();
		if (!s.equals("")) 
		{
			DefaultMutableTreeNode current = new DefaultMutableTreeNode("Descrioption : " + s);
			base.add(current);

		}
	}
	/**
	 * 
	 */		
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException 
	{

		base = (DefaultMutableTreeNode) base.getParent();
	}
	/**
	 * 
	 */		
	/*
	public static void main(String[] args) 
	{
		treeViewer = new CTATXMLViewer();
		// treeViewer.xmlSetUp();
		treeViewer.createUI();

	}
	*/
	/**
	 * 
	 */		
	@Override
	public void endDocument() throws SAXException 
	{
		debug ("endDocument ()");
		
		// Refresh JTree
		((DefaultTreeModel) xmlJTree.getModel()).reload();
		//expandAll(xmlJTree);
	}

	public void xmlSetUp(String aStream) 
	{
		debug ("xmlSetUp ()");
		
		if (aStream==null)
		{
			debug ("Error: input is null");
			return;
		}
		
		SAXParserFactory fact=SAXParserFactory.newInstance();
		SAXParser parser=null;
				
		try 
		{
			parser = fact.newSAXParser();
		} catch (ParserConfigurationException e1) 
		{
			debug ("Error: ParserConfigurationException");
			e1.printStackTrace();
		} catch (SAXException e1) 
		{
			debug ("Error: SAXException");
			e1.printStackTrace();
		}
		
		InputStream is=new ByteArrayInputStream (aStream.getBytes());
		
		/*
		if (is==null)
		{
			debug ("Error: input stream is null");
			return;
		}
		*/
		
		if (parser==null)
		{
			debug ("No parser available, aborting");
			return;
		}
		
		try 
		{
			parser.parse (is,this);			
		} 
		catch (Exception e) 
		{
			debug ("Error parsing XML stream:" + e);
			e.printStackTrace();
		}
	}	
/*
	public void createUI() {

		treeModel = new DefaultTreeModel(base);
		xmlJTree = new JTree(treeModel);
		JScrollPane scrollPane = new JScrollPane(xmlJTree);

		JFrame windows = new JFrame();

		windows.setTitle("XML Tree Viewer using SAX Parser in JAVA");

		JPanel pnl = new JPanel();
		pnl.setLayout(null);
		JLabel lbl = new JLabel("File :");
		txtFile = new JTextField("Selected File Name Here");
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(txtFile); }
		JButton btn = new JButton("Select File");

		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JFileChooser fileopen = new JFileChooser();
				//FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
				//fileopen.addChoosableFileFilter(filter);
				fileopen.addChoosableFileFilter(null);

				int ret = fileopen.showDialog(null, "Open file");

				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fileopen.getSelectedFile();
					txtFile.setText(file.getPath() + File.separator + file.getName());
					xmlSetUp(file);
				}

			}
		});
		lbl.setBounds(0, 0, 100, 30);
		txtFile.setBounds(110, 0, 250, 30);
		btn.setBounds(360, 0, 100, 30);
		scrollPane.setBounds(0, 50, 500, 600);

		pnl.add(lbl);

		pnl.add(txtFile);
		pnl.add(btn);

		pnl.add(scrollPane);

		windows.add(pnl);
		windows.setSize(500, 700);
		windows.setVisible(true);
		windows.setDefaultCloseOperation( windows.EXIT_ON_CLOSE);
	}
*/
}
