/**
 ------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATSkill.java,v 1.2 2012/01/06 22:09:23 sewall Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATSkill.java,v $
 Revision 1.2  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.2  2011/07/20 20:04:37  kjeffries
 Skills can be read from/written to XML

 Revision 1.1  2011/03/25 20:38:49  vvelsen
 Added much more capabilties to the USB TutorShop. We can now start problems defined in problem_set.xml files. Much more of the problem sequencing code has been finished but more needs to happen there. Some of the internals have been bolstered.

 $RCSfile: CTATSkill.java,v $ 
 $Revision: 18458 $ 
 $Source: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATSkill.java,v $ 
 $State: Exp $ 

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 
 pKnown="0.25" 
 pLearn="0.2" 
 description="Choose Operation" 
 label="Choose Operation" 
 pGuess="0.2" 
 name="Choose-Operation" 
 category="Decimal-Addition-and-Subtraction" 
 pSlip="0.1"
 
 ------------------------------------------------------------------------------------
*/

package edu.cmu.hcii.ctat;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class CTATSkill extends CTATBase 
{		
	public String pKnown="0.25"; 
	public String pLearn="0.2";
	public String description="Choose Operation"; 
	public String label="Choose Operation";
	public String pGuess="0.2";
	public String name="Choose-Operation"; 
	public String category="Decimal-Addition-and-Subtraction"; 
	public String pSlip="0.1";
	
	/**------------------------------------------------------------------------------------
	 *
	 */
	public CTATSkill () 
	{
		setClassName ("CTATSkill");
		debug ("CTATSkill ()");	
	}
	/**------------------------------------------------------------------------------------
	 * Set this skills fields according to the given XML element
	 */
	public CTATSkill(Node skillNode)
	{
		NamedNodeMap nodeMap = skillNode.getAttributes();
		if(nodeMap == null)
		{
			debug("The specified skillNode is not an XML Element.");
			return;
		}
		
		for(int i = 0; i < nodeMap.getLength(); i++)
		{
			Attr attribute = (Attr) nodeMap.item(i); 
			
			String attrName = attribute.getName();
			String attrValue = attribute.getValue();
			
			if(attrName.equals("pKnown"))
				pKnown = attrValue;
			else if(attrName.equals("pLearn"))
				pLearn = attrValue;
			else if(attrName.equals("description"))
				description = attrValue;
			else if(attrName.equals("label"))
				label = attrValue;
			else if(attrName.equals("pGuess"))
				pGuess = attrValue;
			else if(attrName.equals("name"))
				name = attrValue;
			else if(attrName.equals("category"))
				category = attrValue;
			else if(attrName.equals("pSlip"))
				pSlip = attrValue;
		}
	}
	/**------------------------------------------------------------------------------------
	 * Convert this skill to a String in the form of an XML element
	 */
	public String toString()
	{
		return "<Skill pKnown=\"" + pKnown + 
			"\" pLearn=\"" + pLearn +
			"\" description=\"" + description +
			"\" label=\"" + label +
			"\" pGuess=\"" + pGuess +
			"\" name=\"" + name +
			"\" category=\"" + category +
			"\" pSlip=\"" + pSlip + "\"/>";
	}
	//-------------------------------------------------------------------------------------	
}
