package servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;



/**
 * Class to hold various utility methods used for debugging, etc
 * @author Alex Xiao
 *
 */

public class Utilities {
	
	/*
	 * Normally the JAXP library parse method takes in a uri or otherwise a File/InputSource object. This
	 * method takes a string representing xml and wraps an InputSource around it so that we can use the
	 * parse method. Note that we care about content only, so we set the DocumentBuilderFactory to take
	 * away extraneous information.
	 */
	public static Document loadXMLFromString(String xml)
	{
		try{
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    //set factory to make content reading easier
		    factory.setIgnoringComments(true);
		    factory.setIgnoringElementContentWhitespace(true);
		    factory.setCoalescing(true);
		    factory.setExpandEntityReferences(true);
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    InputSource is = new InputSource(new StringReader(xml));
		    Document doc = builder.parse(is);
		    doc.getDocumentElement().normalize();
		    clearEmptyNodes(doc);
		    return doc;
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Request not valid xml");
			return null;
		}
	}
	
	public static void clearEmptyNodes(Document doc) throws XPathExpressionException{
		  XPathFactory xpathFactory = XPathFactory.newInstance();
		  // XPath to find empty text nodes.
		  XPathExpression xpathExp = xpathFactory.newXPath().compile(
		          "//text()[normalize-space(.) = '']");  
		  NodeList emptyTextNodes = (NodeList) 
		          xpathExp.evaluate(doc, XPathConstants.NODESET);

		  // Remove each empty text node from document.
		  for (int i = 0; i < emptyTextNodes.getLength(); i++) {
		      Node emptyTextNode = emptyTextNodes.item(i);
		      emptyTextNode.getParentNode().removeChild(emptyTextNode);
		  }
		 }
	
	public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	    transformer.transform(new DOMSource(doc), 
	         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}
	
	
	public static boolean isUrl(String s) {
		String pattern = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(s);
		return m.find();
	}
	
	// prints human readable xml, useful for debugging and testing
	public static void prettyPrintXMLAsString(String xml) {
		
		try {
            final InputSource src = new InputSource(new StringReader(xml));
            final Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
            final Boolean keepDeclaration = Boolean.valueOf(xml.startsWith("<?xml"));

        //May need this: System.setProperty(DOMImplementationRegistry.PROPERTY,"com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");


            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            final LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); // Set this to true if the output needs to be beautified.
            writer.getDomConfig().setParameter("xml-declaration", keepDeclaration); // Set this to true if the declaration is needed to be outputted.

            System.out.println(writer.writeToString(document));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
	
	public static void printTabs(int count, StringBuffer stringBuffer) {
	    for (int i = 0; i < count; i++) {
	        stringBuffer.append("\t");
	    }
	}
}
