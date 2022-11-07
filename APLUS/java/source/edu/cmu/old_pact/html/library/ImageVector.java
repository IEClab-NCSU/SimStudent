package edu.cmu.old_pact.html.library;


import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.net.URL;

class ImageVector implements ImageObserver
{
  private static final int ERR_WIDTH = 2;
  private static final int ERR_HEIGHT = 2;

  private static Toolkit tk = Toolkit.getDefaultToolkit();
  private static Image errImg;

  private URL dataURL[] = new URL[100];
  private int dataWidth[] = new int[100];
  private int dataHeight[] = new int[100];
  private Image dataImage[] = new Image[100];
  private int size = 0;
  private int index = 0;

  static // class initialization
  {
    byte reds[] = { (byte)255 };
    byte greens[] = { (byte)255 };
    byte blues[] = { (byte)255 };
    //byte greens[] = { (byte)0 };
    //byte blues[] = { (byte)0 };
    ColorModel cm = new IndexColorModel(1, 1, reds, greens, blues);

    byte pixels[] = new byte[ERR_HEIGHT * ERR_WIDTH];
    for (int i = 0; i < ERR_HEIGHT * ERR_WIDTH; i++)
      pixels[i] = (byte)0;

    errImg = tk.createImage(new MemoryImageSource(
			ERR_WIDTH, ERR_HEIGHT, cm, pixels, 0, ERR_WIDTH));
    tk.prepareImage(errImg, -1, -1, null);
   reds = null;
   greens = null;
   blues = null;
   pixels = null;
   
  }

  synchronized protected void addElement(URL base, String src, int w, int h)
  {
    URL url;
    Image img;

    if (size == dataURL.length)
      changeCapacity(2 * size);

    dataWidth[size] = w;
    dataHeight[size] = h;
    try
    {
 //trace.out("ImageVector src = "+src+" base = "+base);
    	if(	src.indexOf("http://") != -1 || 
    		src.indexOf("file:/") != -1 ||
    		src.indexOf("C:/") != -1)
    		base = null;
      url = new URL(base, src);
//trace.out("** ImageVector HTMLLib got url = "+url);
      img = tk.getImage(new URL(base, src));
//trace.out("img in ImageVector = "+img);
      tk.prepareImage(img, w, h, null);
    }
    catch (Exception e)
    {
    // start for CMU debug 
   // trace.out("ImageVector in HTMLLib: can't get image src = "+src+" base = "+base);
    //e.printStackTrace();
    // ens for CMU debug
     url = null;
      img = null;
      if (dataWidth[size] < 0)
	dataWidth[size] = ERR_WIDTH;
      if (dataHeight[size] < 0)
	dataHeight[size] = ERR_HEIGHT;
    }
    dataURL[size] = url;
    dataImage[size] = img;
    size++;
  }

  protected void trimToSize()
  {
    if (size < dataURL.length)
      changeCapacity(size);
  }

  protected void reset()
  {
    index = 0;
  }
  
  protected boolean hasMoreElements()
  {
    return index < size;
  }
  
  synchronized protected HtmlImage nextElement()
  {
    Image img;
    int w;
    int h;
    if (dataImage[index] != null)
    {
      img = dataImage[index];
      if (!imageSizeAvailable(img, dataWidth[index], dataHeight[index]))
      {
		dataURL[index] = null;
		w = ERR_WIDTH;
		h = ERR_HEIGHT;
      }
      else
      {
		w = img.getWidth(null);
		h = img.getHeight(null);
      }
      dataImage[index] = null;
      if (dataWidth[index] < 0)
		dataWidth[index] = w;
      if (dataHeight[index] < 0)
		dataHeight[index] = h;
    }
    if (dataURL[index] == null)
      img = errImg;
    else
      img = tk.getImage(dataURL[index]);
    tk.prepareImage(img, dataWidth[index], dataHeight[index], null);
    HtmlImage hi = new HtmlImage(-1, -1, dataWidth[index], dataHeight[index], img);
    index++;
    return hi;
  }

  private String statusString(int info)
  {
    String s = "";
    if ((info & ABORT) != 0) s += "ABORT ";
    if ((info & ALLBITS) != 0) s += "ALLBITS ";
    if ((info & ERROR) != 0) s += "ERROR ";
    if ((info & FRAMEBITS) != 0) s += "FRAMEBITS ";
    if ((info & HEIGHT) != 0) s += "HEIGHT ";
    if ((info & PROPERTIES) != 0) s += "PROPERTIES ";
    if ((info & SOMEBITS) != 0) s += "SOMEBITS ";
    if ((info & WIDTH) != 0) s += "WIDTH ";
    return s + info;
  }
  
  synchronized public boolean imageUpdate(Image img, int info, int x, int y, int w, int h)
  {
  	if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
  		return imageUpdateMac(img, info, x,y,w,h);
  	
  	else
  		return imageUpdateNotMac(img, info, x,y,w,h);
  	}
 
// works on MAC and Win
  synchronized public boolean imageUpdateMac(Image img, int info, int x, int y, int w, int h)
  {
    if ((info & (ABORT | ERROR)) != 0)
    {
      notify();
      return true;
    }
    info = tk.checkImage(img, -1, -1, null);
    if ((info & (WIDTH | HEIGHT)) == (WIDTH | HEIGHT))
    {
      notify();
      return true;
    }
    return false;
  }
  
  
  // works on NT and Win
  synchronized public boolean imageUpdateNotMac(Image img, int info, int x, int y, int w, int h)
  {
    if ((info & (ABORT | ERROR)) != 0)
    {
      notify();
      return false;
    }
    info = tk.checkImage(img, -1, -1, null);
   	if ((info & ALLBITS) != 0) 
    {
      notify();
      return false;
    }
    return true;
  }
  
  synchronized private boolean imageSizeAvailable(Image img, int w, int h)
  {
    for (;;)
    {
      try
      {
	int info = tk.checkImage(img, w, h, this);
//trace.out("** imageSizeAvailable info = "+info);
	if ((info & (ABORT | ERROR)) != 0){
//trace.out("IMV info & (ABORT | ERROR) ret FALSE");
	  return false;
	 }
	//if ((info & (WIDTH | HEIGHT)) == (WIDTH | HEIGHT))
	if ((info & ALLBITS) != 0) {
//trace.out("** imageSizeAvailable about to ret true");
	  return true;
	 }
//trace.out("IMV  about to wait ..");
	wait();
//trace.out("IMV  AFTER wait ..");
      }
      catch (InterruptedException e)
      {
      }
    }
  }

  private void changeCapacity(int newSize)
  {
    int tmp[] = new int[newSize];
    System.arraycopy(dataWidth, 0, tmp, 0, size);
    dataWidth = null;
    dataWidth = tmp;
    tmp = new int[newSize];
    System.arraycopy(dataHeight, 0, tmp, 0, size);
    dataHeight = null;
    dataHeight = tmp;
    URL tmpURL[] = new URL[newSize];
    System.arraycopy(dataURL, 0, tmpURL, 0, size);
    dataURL = null;
    dataURL = tmpURL;
    Image tmpImage[] = new Image[newSize];
    System.arraycopy(dataImage, 0, tmpImage, 0, size);
    dataImage = null;
    dataImage = tmpImage;
  }
  
  protected void delete(){
  	dataURL = null;
  	dataWidth = null;
  	dataHeight = null;
  	dataImage = null;
  	
  }
}
