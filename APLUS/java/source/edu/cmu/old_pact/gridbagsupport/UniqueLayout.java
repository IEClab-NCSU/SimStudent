
/* A new Custom LayoutManager which  implements  all functionalities
  of GridBagLayout and also Some of the extra functionalities */

package edu.cmu.old_pact.gridbagsupport;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Hashtable;


class UniqueLayoutInfo implements java.io.Serializable {
  int width, height;		
  int startx, starty;		
  int minWidth[];		
  int minHeight[];		
  double weightX[];		
  double weightY[];		

  UniqueLayoutInfo () {
    minWidth = new int[UniqueLayout.MAXGRIDSIZE];
    minHeight = new int[UniqueLayout.MAXGRIDSIZE];
    weightX = new double[UniqueLayout.MAXGRIDSIZE];
    weightY = new double[UniqueLayout.MAXGRIDSIZE];
  }
}

public class UniqueLayout implements LayoutManager2, java.io.Serializable {
    int hgap;            
    int vgap;            
    Dimension gridSize;  
    Hashtable comptable; 
    protected GridBagConstraints defaultConstraints;
    protected static final int MINSIZE = 1;
    protected static final int PREFERREDSIZE = 2;
    protected static final int MAXGRIDSIZE = 512;
    protected UniqueLayoutInfo layoutInfo;
    public int columnWidths[];
    public int rowHeights[];
    public double columnWeights[];
    public double rowWeights[];

    

    public UniqueLayout() {
        this(new Dimension(10,10));
        comptable = new Hashtable();
        defaultConstraints = new GridBagConstraints();
    }
    
   
    public UniqueLayout(Dimension gridSize) {
        this(gridSize, 0, 0);
        comptable = new Hashtable();
        defaultConstraints = new GridBagConstraints();
    }
    
    
    //Layout Constructor with  hgap horizontal padding &  vgap vertical padding
    
    public UniqueLayout(Dimension gridSize, int hgap, int vgap) {
        if ((gridSize.width <= 0) || (gridSize.height <= 0)) {
            throw new IllegalArgumentException(
                "dimensions must be greater than zero");
        }
        this.gridSize = new Dimension(gridSize);
        this.hgap = hgap;
        this.vgap = vgap;
        comptable = new Hashtable();
    }
    
   
    public Dimension getGridSize() {
        return new Dimension( gridSize );
    }

    
     // Set the size 
     
    public void setGridSize( Dimension d ) {
        setGridSize( d.width, d.height );
    }
    
    
    //  Set the size 
    
    public void setGridSize( int width, int height ) {
        gridSize = new Dimension( width, height );
    }
    
    public void setConstraints(Component comp, GridBagConstraints constraints) {
        comptable.put(comp,constraints.clone());
    }
    
    public GridBagConstraints getConstraints(Component comp) {
    GridBagConstraints constraints = (GridBagConstraints)comptable.get(comp);
    if (constraints == null) {
      setConstraints(comp, defaultConstraints);
      constraints = (GridBagConstraints)comptable.get(comp);
    }
    return (GridBagConstraints)constraints.clone();
  }
    
    
    protected GridBagConstraints lookupConstraints(Component comp) {
        GridBagConstraints constraints = (GridBagConstraints)comptable.get(comp);
    if (constraints == null) {
        setConstraints(comp, defaultConstraints);
        constraints = (GridBagConstraints)comptable.get(comp);
    }
    return constraints;
  }
  
     public Point getLayoutOrigin () {
    Point origin = new Point(0,0);
    if (layoutInfo != null) {
      origin.x = layoutInfo.startx;
      origin.y = layoutInfo.starty;
    }
    return origin;
  }
  

  public int [][] getLayoutDimensions () {
    if (layoutInfo == null)
      return new int[2][0];

    int dim[][] = new int [2][];
    dim[0] = new int[layoutInfo.width];
    dim[1] = new int[layoutInfo.height];

    System.arraycopy(layoutInfo.minWidth, 0, dim[0], 0, layoutInfo.width);
    System.arraycopy(layoutInfo.minHeight, 0, dim[1], 0, layoutInfo.height);

    return dim;
  }
  
  
  
  public double [][] getLayoutWeights () {
    if (layoutInfo == null)
      return new double[2][0];

    double weights[][] = new double [2][];
    weights[0] = new double[layoutInfo.width];
    weights[1] = new double[layoutInfo.height];

    System.arraycopy(layoutInfo.weightX, 0, weights[0], 0, layoutInfo.width);
    System.arraycopy(layoutInfo.weightY, 0, weights[1], 0, layoutInfo.height);

    return weights;
  }
     
    public void addLayoutComponent(String name, Component comp) {
    }
      
      
       public void addLayoutComponent(Component comp, Object constraints) {
      if (constraints instanceof GridBagConstraints) {
	    setConstraints(comp, (GridBagConstraints)constraints);
	}else if (constraints instanceof String){
		//Do Nothing here
	}else if (constraints != null) {
	    throw new IllegalArgumentException("cannot add to layout: constraint must be a GridBagConstraint");
	}
    }
    
    
        public void removeLayoutComponent(Component comp) {
        comptable.remove(comp);    }


    
    /*
     Calculates the preferred & Min  size dimensions for the specified 
     panel given the components in the specified parent container.
     */
    public Dimension preferredLayoutSize(Container parent) {
        return getLayoutSize(parent, true);
    }

    
    public Dimension minimumLayoutSize(Container parent) {
        return getLayoutSize(parent,false);
    }

    // Calculation of layoutSize either minimum or preferred
    protected Dimension getLayoutSize(Container parent, boolean b) {
        Dimension largestSize = getLargestCellSize(parent, b);
        Insets insets = parent.getInsets();    
        largestSize.width = ( largestSize.width * gridSize.width ) +
            (hgap * ( gridSize.width + 1 ) ) + insets.left + insets.right;
        largestSize.height = ( largestSize.height * gridSize.height ) +
            ( vgap * ( gridSize.height + 1 ) ) + insets.top + insets.bottom;
        return largestSize;
    }
    
    
     // Calculation of largest of  Preferred & Minimum cell sizes
      
    protected Dimension getLargestCellSize(Container parent,boolean b) {
        GridBagConstraints constraints;
        int ncomponents = parent.getComponentCount();
        Dimension maxCellSize = new Dimension(0,0);
        for ( int i = 0; i < ncomponents; i++ ) {
            Component c = parent.getComponent(i);
             constraints = lookupConstraints(c);
            if ( c != null && constraints != null ) {
                Dimension componentSize;
                if (b) {
                    componentSize = c.preferredSize();
                } else {
                    componentSize = c.minimumSize();
                }
                if (constraints.gridheight == 0 || constraints.gridwidth == 0){ System.out.println("Error"); }
                else {
                maxCellSize.width = Math.max(maxCellSize.width,componentSize.width / constraints.gridwidth);
                maxCellSize.height = Math.max(maxCellSize.height,componentSize.height / constraints.gridheight);
                }
            }
        }
        return maxCellSize;
    }

   
     // Lays out the container in the specified container.
     
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            Rectangle r = new Rectangle();
            int ncomps = parent.getComponentCount();
            if (ncomps == 0) {
                return;
            }
            GridBagConstraints constraints;
            // Total parent dimensions
            Dimension size = parent.getSize();
            int totalW = size.width - (insets.left + insets.right);
            int totalH = size.height - (insets.top + insets.bottom);         
            
            // Cell dimensions, including padding
            int totalCellW = totalW / gridSize.width;
            int totalCellH = totalH / gridSize.height;
            
              // Cell dimensions, without padding
            int cellW = (totalW - ( (gridSize.width + 1) * hgap) )
                    / gridSize.width;
            int cellH = (totalH - ( (gridSize.height + 1) * vgap) )
                    / gridSize.height;
    
            for ( int i = 0; i < ncomps; i++ ) {
                Component c = parent.getComponent(i);
                 constraints = lookupConstraints(c);
                if ( constraints != null ) {
                    r.x = insets.left + ( totalCellW * constraints.gridx ) + hgap;
                    r.y = insets.top + ( totalCellH * constraints.gridy ) + vgap;
                    r.width = ( cellW * constraints.gridwidth ) - hgap;
                    r.height = ( cellH * constraints.gridheight ) - vgap;
          //AdjustForGravity(constraints, r);                   
                    c.setBounds(r.x, r.y, r.width, r.height);
                }
            }
        }
    }
    
    
    /*
   // Adjusts x,y,width,height to correct values     
      
   
    */
     
     // Returns the maximum size of this component.
     
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }


    /*
      Returns the alignment along the x  or y axis.  This specifies how
      the component would like to be aligned relative to other 
      components.  The value should be a number between 0 and 1
      where 0 represents alignment along the origin, 1 is aligned
      the furthest away from the origin, 0.5 is centered, etc.
     */
     
    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    
    public void invalidateLayout(Container target) {
        // Do nothing
    }
}


    /*
       Adds the specified component to the layout, using the specified
       constraint object.
     
       
     */
    




