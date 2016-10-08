package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import edu.cmu.old_pact.dormin.ObjectProxy;


public class HeaderGrid extends DorminGridElement{
	MatrixElement mElement;
	private int minWidth = 70;
	
	public HeaderGrid(MatrixElement mElement, int r, int c, int minW){
		super(r, c);

		textField = new HeaderTextField();
		setInit();
		this.mElement = mElement;
		this.addPropertyChangeListener((PropertyChangeListener)textField);
		mElement.addPropertyChangeListener(this);
		textField.addPropertyChangeListener(mElement);
		textField.addPropertyChangeListener(this);
		
		mElement.setOwnVisualCell(textField);
		
		initTextField(mElement.getAllProperties());
		
		if(minW != -1) {
			minWidth = minW;
		    textField.setMinWidth(minW);
		    textField.setWidth(minW);
		} 
		else
			if(c == 1) {  // row labels
				textField.setMinWidth(130);
		    	textField.setWidth(130);
		    	((CustomTextField)textField).setAlignment (AltTextField.ALIGN_RIGHT, 
		    											   AltTextField.ALIGN_CENTER);
		    }
	}

	public HeaderGrid(MatrixElement mElement, int r, int c){
		this(mElement, r, c, -1);
	}
	
	protected void initTextField(Hashtable cellProperties){
		super.initTextField(cellProperties);
		if(textField instanceof CustomTextField)
			((CustomTextField)textField).setAlignment (AltTextField.ALIGN_MIDDLE, AltTextField.ALIGN_CENTER);
	}

	public MatrixElement getMatrixElement(){
		return mElement;
	}
	
	protected String getHeaderName(){
		return mElement.getName();
	}
	
	public boolean hasFocus(){
		return mElement.isClicked();
	}
	
	public void requestFocus(){
		if(textField != null){
			//textField.setEditable(true);
			textField.setVisible(true);
			textField.requestFocus();
		}
	}
	public void createObjectProxy(ObjectProxy parent){
		super.createObjectProxy(parent);
		createDorminMatrixEl(parent);
	}
	
	public void createDorminMatrixEl(ObjectProxy parent){
		String type = "Column";
		if(mElement instanceof RowMatrixElement)
			createRowProxy(parent);
		else if(mElement instanceof DorminMatrixElement) 
			createColumnProxy(parent);
	}
	
	public void createRowProxy(ObjectProxy parent){
		((DorminMatrixElement)mElement).createObjectProxy(parent, "Row");
	}
	
	public void createColumnProxy(ObjectProxy parent){
		((DorminMatrixElement)mElement).createObjectProxy(parent, "Column");
	}	
		
	public int[] getPosition(){
		return mElement.getPosition();
	}
	
	public  int getPos(){
		return mElement.getPos();
	}


	public String getValue(){
		return mElement.getValue();
	}
	
	public Dimension preferredSize(){
		Dimension d;
		if(textField == null) 
			return new Dimension(minWidth, 20);
		
		if(textField.getWidth() == 0)
			return new Dimension(textField.getMinWidth(), textField.getMinHeight());
		else {
			return new Dimension(textField.getWidth(), textField.getHeight());	
		}	
	}
	
	public int getWidth(){
		int toret = minWidth;
		if(textField == null) 
			return toret;
		
		if(textField.getWidth() == 0)
			return textField.getMinWidth();
		else 
			return textField.getWidth();		
	}
	
	public int getHeight(){
		int toret = 20;
		if(textField == null) 
			return toret;
		
		if(textField.getWidth() == 0)
			return textField.getMinHeight();
		else 
			return textField.getHeight();		
	}
	
	public Dimension minimumSize(){
		return preferredSize();
	}
	
	public void clearAlways(){
		this.removePropertyChangeListener((PropertyChangeListener)textField);
		mElement.removePropertyChangeListener(this);
		if(textField != null){
			textField.removePropertyChangeListener(this);
			textField.removePropertyChangeListener((PropertyChangeListener)mElement);
			mElement.removeVisualCell(textField);
		}
		removeAll();
	}
	
	public void deleteAll(){
		//mElement =null;
		super.deleteAll();
	}

	public synchronized void clear(boolean delete_Proxy){
		//this.removePropertyChangeListener((PropertyChangeListener)textField);
		clearAlways();
		if(delete_Proxy && (mElement instanceof DorminMatrixElement)) 
			((DorminMatrixElement)mElement).deleteObjectProxy();
		if(!delete_Proxy){
			if( textField != null)
				textField.clear();
			mElement.setOwnVisualCell(null);
	//mElement = null;
			textField = null;
		}
		super.clear(delete_Proxy);
	}
	
	public  void propertyChange(PropertyChangeEvent evt){
		super.propertyChange(evt);
		if(evt.getPropertyName().equalsIgnoreCase("HEIGHT")) { 
			changes.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
		else if(evt.getPropertyName().equalsIgnoreCase("WIDTH")) { 
			changes.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
	}
}