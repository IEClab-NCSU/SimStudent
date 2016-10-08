package edu.cmu.old_pact.html.library;


class StringVector
{
  private String data[] = new String[100];
  private int size = 0;
  private int index = 0;

  protected void addElement(String i)
  {
    if (size == data.length)
    {
      String tmp[] = new String[2 * size];
      System.arraycopy(data, 0, tmp, 0, size);
      data = null;
      data = tmp;
    }
    data[size++] = i;
  }
  
  protected void trimToSize()
  {
    if (size < data.length)
    {
      String tmp[] = new String[size];
      System.arraycopy(data, 0, tmp, 0, size);
      data = null;
      data = tmp;
    }
  }

  protected void reset()
  {
    index = 0;
  }
  
  protected boolean hasMoreElements()
  {
    return index < size;
  }
  
  protected String nextElement()
  {
    return data[index++];
  }
  
  protected void delete(){
  	data = null;
  }
}
