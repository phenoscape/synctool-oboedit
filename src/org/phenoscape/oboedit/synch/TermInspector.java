package org.phenoscape.oboedit.synch;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.obo.datamodel.OBOClass;

@SuppressWarnings("serial")
class TermInspector extends JPanel {
    
    public OBOClass term;
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextArea definitionField = new JTextArea();

    public TermInspector() {
        this.idField.setEditable(false);
        this.nameField.setEditable(false);
        this.definitionField.setEditable(false);
        this.definitionField.setLineWrap(true);
        this.definitionField.setWrapStyleWord(true);
        
        this.setLayout(new GridBagLayout());
        final GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.EAST;
        labelConstraints.gridy = 0;
        final GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = 0;
        
        this.add(new JLabel("ID:"), labelConstraints);
        this.add(this.idField, fieldConstraints);
        
        labelConstraints.gridy++;
        this.add(new JLabel("Name:"), labelConstraints);
        fieldConstraints.gridy++;
        this.add(this.nameField, fieldConstraints);
        
        labelConstraints.gridy++;
        labelConstraints.anchor = GridBagConstraints.NORTH;
        this.add(new JLabel("Definition:"), labelConstraints);
        fieldConstraints.gridy++;
        fieldConstraints.fill = GridBagConstraints.BOTH;
        fieldConstraints.weighty = 1.0;
        this.add(new JScrollPane(this.definitionField), fieldConstraints);
    }

    public OBOClass getTerm() {
        return this.term;
    }

    public void setTerm(OBOClass term) {
        this.term = term;
        if (this.term != null) {
            this.idField.setText(this.term.getID());
            this.nameField.setText(this.term.getName());
            this.definitionField.setText(this.term.getDefinition());
        } else {
            this.clearFields();
        }
    }
    
    private void clearFields() {
        this.idField.setText(null);
        this.nameField.setText(null);
        this.definitionField.setText(null);
    }
    
}
