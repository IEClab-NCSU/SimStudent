package pact.CommWidgets;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import cl.ui.tools.tutorable.geometrypad.GeometryPad;
import cl.ui.tools.tutorable.geometrypad.SelectionEvent;
import cl.ui.tools.tutorable.geometrypad.SelectionListener;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.Utilities.VersionInformation;

public class WidgetSwingComponent
	implements ChangeListener, DocumentListener, ItemListener, ListDataListener, SelectionListener {

	/**
	 * Delegate for state changes.
	 */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/** Whether user has interacted with this widget's component. */
	private boolean answered = false;
	
	/** JCommWidget container. */
	JCommWidget dw;
	
	/** Text field component. */
	JTextField textField;
    
    /** ComboBox component */
    JComboBox cb;

	WidgetSwingComponent(JCommWidget dw) {
		this.dw = dw;
		if (trace.getDebugCode("dw")) trace.out("dw", "WSC: widget = " + dw);
		if (dw instanceof JCommRadioButton) {
			JRadioButton butn = (JRadioButton) dw.getComponent(0);
			butn.addChangeListener(this);
			if (trace.getDebugCode("dw")) trace.out("dw", "WSC: JCommRadioButton: JButton = " + butn);
		} else if (dw instanceof JCommTextField) {
			textField = (JTextField) dw.getComponent(0);
			textField.getDocument().addDocumentListener(this);
			if (trace.getDebugCode("dw")) trace.out("dw", "WSC: JCommTextField: JTextField = " + textField);
		} else if (dw instanceof JCommQuestionTextField) {
			textField = (JTextField) dw.getComponent(1);
			textField.getDocument().addDocumentListener(this);
			if (trace.getDebugCode("dw")) trace.out("dw", "WSC: JCommQuestionTextField: JTextField = " + textField);
		} else if (dw instanceof JCommComboBox) {
		    cb = (JComboBox) dw.getComponent(0);
            cb.addItemListener(this);
            if (trace.getDebugCode("dw")) trace.out("dw", "WSC: JCommComboBox: JComboBox = " + cb);
        } else if (dw instanceof JCommComposer) {
        	DefaultListModel model = (DefaultListModel) ((JList) ((JScrollPane) dw.getComponent(0)).getViewport().getView()).getModel();
            model.addListDataListener(this);
            if (trace.getDebugCode("dw")) trace.out("dw", "WSC: JCommComposer: Model = " + model);
        } else if (VersionInformation.includesCL() && dw instanceof JCommGeoSelectionDiagram) {
        	GeometryPad diagram = ((JCommGeoSelectionDiagram) dw).getDiagram();
        	diagram.addSelectionListener(this);
        }
	}
	
	/**
	 * Sets {@link #answered}
	 * @param answered new value 
	 */
	void setAnswered(boolean answered) {
		if (trace.getDebugCode("dw")) trace.out("dw", dw + " WSC setAnswered: " + answered);
		boolean oldValue = this.answered; 
		this.answered = answered;
		propertyChangeSupport.firePropertyChange("answered", oldValue, answered);
	}
	
	/**
	 * Increments {@link #answered}
	 * @param ae
	 */
//	public void actionPerformed(ActionEvent ae) {
//		trace.out("inter", this.dw.getClass() + "WSC received action event " + ae);
//		setAnswered(true);
//	}

	/**
	 * Increments {@link #answered} for {@link JCommRadioButton}
	 */
	public void stateChanged(ChangeEvent ce) {
		if (trace.getDebugCode("dw")) trace.out("dw", dw + " WSC received change event " + ce);
		setAnswered(((JRadioButton) ce.getSource()).isSelected());
	}

	/**
	 * Increment {@link #answered} if {@link #textField} is nonempty for {@link CommTextFiled} and {@link JCommQuestionTextField}
	 */
	public void changedUpdate(DocumentEvent de) {
		if (trace.getDebugCode("dw")) trace.out("dw", dw + " WSC received document event " + de);
		String text = textField.getText();
		setAnswered(text != null && text.trim().length() > 0);
	}

	/**
	 * Same as {@link #changedUpdate(DocumentEvent)} for {@link CommTextFiled} and {@link JCommQuestionTextField}
	 */
	public void insertUpdate(DocumentEvent de) {changedUpdate(de);}

	/**
	 * Same as {@link #changedUpdate(DocumentEvent)} for {@link CommTextFiled} and {@link JCommQuestionTextField}
	 */
	public void removeUpdate(DocumentEvent de) {changedUpdate(de);}

	/**
	 * Increment {@link answered} if valid choice for {@link JCommComboBox}
	 */
    public void itemStateChanged(ItemEvent ie) {
        if (trace.getDebugCode("dw")) trace.out("dw", dw + " WSC received item event " + ie);
        setAnswered(cb.getSelectedItem() != null && !cb.getSelectedItem().toString().trim().startsWith("--"));
    }

    /**
     * Increments {@link #answered} if list has elements for {@link JCommComposer}
     */
    public void contentsChanged(ListDataEvent lde) {
    	setAnswered(((JCommComposer)dw).hasValidValue());
    }

    /**
     * Same as {@link #contentsChanged(ListDataEvent)} for {@link JCommComposer}
     */
    public void intervalAdded(ListDataEvent lde) {contentsChanged(lde);}

    /**
     * Same as {@link #contentsChanged(ListDataEvent)} for {@link JCommComposer}
     */
    public void intervalRemoved(ListDataEvent lde) {contentsChanged(lde);}

    /**
     * Increments {@link answered if selections present for {@link JCommGeoSelectionDiagram}
     */
    public void selectionOccurred(SelectionEvent se) {
    	if (trace.getDebugCode("dw")) trace.out("dw", dw + " WSC received selection event " + se);
    	if (VersionInformation.includesCL())
    			setAnswered(((JCommGeoSelectionDiagram) dw).getCurrentStudentSelection().length() > 0);
	}

	/**
	 * Tell whether user has interacted with this widget.
	 * @return ({@link #answered} > 0)
	 */
	public boolean hasBeenAnswered() {
		if (trace.getDebugCode("dw")) trace.out("dw", dw + ".hasBeenAnswered: answered = " + answered);
		return answered;
	}

	public void addPropertyChangeListener(PropertyChangeListener widget) {
		propertyChangeSupport.addPropertyChangeListener(widget);
	}
}
