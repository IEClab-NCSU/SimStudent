/*

This test class tests communication between Java and a Node.js javascript based server 
on localhost. 

Primarily, it reads a BRD file from local disk, sends the XML as POST data via the NodeComm class
and compares the deconstructed-and-reconstructed XML that arrives as a response from the Node.Js
 */
package edu.cmu.hcii.ctat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author dhruv
 */
public class NodeCommTest extends TestCase {
    
    public NodeCommTest() {
    }
       
    public void XMLFileTest() throws IOException, JDOMException
    {
        NodeComm obj = new NodeComm("http://localhost:8888/test");
         String data,temp;temp = "";data = "";        
        
        BufferedReader bf = new BufferedReader(new FileReader("/home/dhruv/Desktop/test.brd"));
        while ((temp = bf.readLine()) != null) {
            data += temp;
        }
        
        data = data.trim().replaceFirst("^([\\W]+)<","<");
        String out = obj.sendFile("/home/dhruv/Desktop/test.brd").trim().replaceFirst("^([\\W]+)<","<");    
        
        SAXBuilder builder = new SAXBuilder();
        Document document1 =    builder.build((Reader) new StringReader(data));
        Document document2 = builder.build((Reader) new StringReader(out));    
        
        assertEquals("Checks if send and recieved data are same", new XMLOutputter().outputString(document1),  new XMLOutputter().outputString(document2));
        
    }
}
