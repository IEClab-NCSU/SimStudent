package edu.cmu.pact.Utilities;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import pact.CommWidgets.JCommPicture;
public class PictureTransferHandler extends TransferHandler {
  DataFlavor pictureFlavor = DataFlavor.imageFlavor;
  public JCommPicture sourcePic;
  public JCommPicture destPic;
  
  boolean shouldRemove;
  /**
   * Constructor.
   * simply initializes instance variable
   */
  public PictureTransferHandler(JCommPicture pic) {
    this.setDestPic(pic);
  }
  
  public boolean importData(JComponent c, Transferable t) {
	  ImageIcon image;
    
    System.err.println("Enter importData: " + ((JCommPicture) c).getCommName() + " +++ " + t); 
    if (canImport(c, t.getTransferDataFlavors())) {
    //	JCommPicture pic = (JCommPicture) c;
    	destPic = (JCommPicture) c;
      //Don't drop on myself.
      if (sourcePic == destPic) {
    	  System.out.println("importData: don't drop to the same picture");  
        shouldRemove = false;
        return true;
      }
      try {
          System.out.println("importData: " + destPic.getCommName() ); 
        image = (ImageIcon) t.getTransferData(pictureFlavor);
        //Set the component to the new picture.
        destPic.setImage(image);
        return true;
      } catch (UnsupportedFlavorException ufe) {
        System.out.println("importData: unsupported data flavor");
      } catch (IOException ioe) {
        System.out.println("importData: I/O exception");
      }
    }
    return false;
  }
  public Transferable createTransferable(JComponent c) {
	  System.err.println("createTransferable: " + ((JCommPicture) c).getCommName()); 
    sourcePic = (JCommPicture) c;
    shouldRemove = true;
    return new PictureTransferable(sourcePic);
  }
  public int getSourceActions(JComponent c) {
	  System.err.println("getSourceActions: " + ((JCommPicture) c).getCommName() ); 
    return COPY_OR_MOVE;
  }
  protected void exportDone(JComponent c, Transferable data, int action) {
	  System.err.println("exportDone: " + ((JCommPicture) c).getCommName() ); 
    if (shouldRemove && (action == MOVE)) {
      sourcePic.setImage(null);
    }
    sourcePic = null;
  }
  public boolean canImport(JComponent c, DataFlavor[] flavors) {
	  System.err.println("canImport: " + ((JCommPicture) c).getCommName() ); 
    for (int i = 0; i < flavors.length; i++) {
      if (pictureFlavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }
  class PictureTransferable implements Transferable {
    private ImageIcon image;
    PictureTransferable(JCommPicture pic) {
      image = pic.image;
    }
    public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException {
     	 System.err.println("getTransferData: " + flavor ); 
      if (!isDataFlavorSupported(flavor)) {
        throw new UnsupportedFlavorException(flavor);
      }
      return image;
    }
    public DataFlavor[] getTransferDataFlavors() {
    	System.err.println("getTransferDataFlavors: ");
      return new DataFlavor[] { pictureFlavor };
    }
    public boolean isDataFlavorSupported(DataFlavor flavor) {
    	System.err.println("isDataFlavorSupported: ");
      return pictureFlavor.equals(flavor);
    }
  }
public JCommPicture getSourcePic() {
	return sourcePic;
}
public void setSourcePic(JCommPicture sourcePic) {
	this.sourcePic = sourcePic;
}
public JCommPicture getDestPic() {
	return destPic;
}
public void setDestPic(JCommPicture destPic) {
	this.destPic = destPic;
}
}
