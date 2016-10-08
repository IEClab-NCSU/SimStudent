/* 
	JCommButtonBeanInfo.java

	Author:			mpschnei
	Description:	BeanInfo for class JCommButton
*/

package pact.CommWidgets;
import java.beans.BeanDescriptor;
import java.io.File;
public class JCommAudioButtonBeanInfo extends java.beans.SimpleBeanInfo
{

	// Generated BeanInfo just gives the bean its icons.
	// Small icon is in JCommAudioButton.gif
	// Large icon is in CommAudioButtonL.gif
	// It is expected that the contents of the icon files will be changed to suit your bean.

    
    public java.beans.BeanDescriptor getBeanDescriptor() {
        BeanDescriptor desc = new BeanDescriptor(JCommAudioButton.class);
        desc.setValue("isContainer", Boolean.FALSE);
        return desc;
    }

    public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
	    		icon = loadImage("JCommAudioButton.gif");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("JCommAudioButtonL.gif");
			default:
				break;
		}
		return icon;
	}

    public String getLabel() {
    	return ("JavaWelcome.wav");
    }
    
	public File getAudioFileName() {
		 return (new File("JavaWelcome.wav"));
	}

        public boolean isContainer () { return false; }
}

/* JCommAudioButtonBeanInfo.java */
