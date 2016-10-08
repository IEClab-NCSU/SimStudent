package edu.cmu.old_pact.html.library;


class IntVector
{
  private int data[] = new int[100];
  private int size = 0;
  private int index = 0;

  protected void addElement(int i)
  {
    if (size == data.length)
    {
      int tmp[] = new int[2 * size];
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
      int tmp[] = new int[size];
      System.arraycopy(data, 0, tmp, 0, size);
      data = null;
      data = tmp;
    }
  }
 
  protected void delete(){
  	data = null;
  }

  protected void reset()
  {
    index = 0;
  }
  
  protected boolean hasMoreElements()
  {
    return index < size;
  }
  
  protected int nextElement()
  {
    return data[index++];
  }
}
