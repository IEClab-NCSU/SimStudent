
/* 
	JCommTextAreaBeanInfo.java

	Author:			mpschnei
	Description:	BeanInfo for class JCommTextArea
*/

package pact.CommWidgets;

public class JCommPictureBeanInfo extends java.beans.SimpleBeanInfo
{

	// Generated BeanInfo just gives the bean its icons.
	// Small icon is in JCommPicture.gif
	// Large icon is in CommPictureL.gif
	// It is expected that the contents of the icon files will be changed to suit your bean.

    public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
	    		icon = loadImage("JCommPicture.gif");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("JCommPictureL.gif");
			default:
				break;
		}
		return icon;
	}
}

/* JCommPictureBeanInfo.java */
