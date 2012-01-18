package org.phenoscape.oboedit.synch;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.bbop.framework.ConfigurationPanel;
import org.obo.datamodel.Namespace;
import org.obo.datamodel.OBOSession;
import org.oboedit.controller.SessionManager;

@SuppressWarnings("serial")
public class SynchronizationComponentConfigPanel extends ConfigurationPanel {

    private SynchronizationComponentSettings settings = new SynchronizationComponentSettings();
    private JComboBox masterNamespaceChooser;
    private JComboBox referringNamespaceChooser;
    private JTextField excludedTermsField;
    private File excludedTermsFile;

    public SynchronizationComponentConfigPanel() {
        super();
        this.setLayout(new GridBagLayout());
        this.masterNamespaceChooser = new JComboBox();
        this.referringNamespaceChooser = new JComboBox();
        this.excludedTermsField = new JTextField();
        final GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.anchor = GridBagConstraints.EAST;
        final GridBagConstraints popupConstraints = new GridBagConstraints();
        popupConstraints.gridx = 1;
        popupConstraints.gridwidth = 2;
        popupConstraints.gridy = 0;
        popupConstraints.fill = GridBagConstraints.HORIZONTAL;
        popupConstraints.weightx = 1.0;
        this.add(new JLabel("Master Namespace:"), labelConstraints);
        this.add(this.masterNamespaceChooser, popupConstraints);
        labelConstraints.gridy = 1;
        popupConstraints.gridy = 1;
        this.add(new JLabel("Referring Namespace:"), labelConstraints);
        this.add(this.referringNamespaceChooser, popupConstraints);
        labelConstraints.gridy = 2;
        this.add(new JLabel("Excluded terms file:"), labelConstraints);
        this.excludedTermsField.setEditable(false);
        popupConstraints.gridy = 2;
        popupConstraints.gridwidth = 1;
        this.add(this.excludedTermsField, popupConstraints);
        final GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridy = 2;
        buttonConstraints.gridx = 2;
        final JButton chooseButton = new JButton(new AbstractAction("Choose...") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                final JFileChooser chooser = new JFileChooser();
                final int result = chooser.showOpenDialog(SynchronizationComponentConfigPanel.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    excludedTermsFile = chooser.getSelectedFile();
                    excludedTermsField.setText(excludedTermsFile.getAbsolutePath());
                    settings.setExcludedTermsFile(excludedTermsFile);
                }
            }
        });
        this.add(chooseButton, buttonConstraints);
    }

    @Override
    public void init() {
        this.settings = (SynchronizationComponentSettings)(((SynchronizationComponent)(this.getComponent())).getSettings());
        this.masterNamespaceChooser.setModel(new DefaultComboBoxModel(this.getSession().getNamespaces().toArray()));
        this.referringNamespaceChooser.setModel(new DefaultComboBoxModel(this.getSession().getNamespaces().toArray()));
        this.masterNamespaceChooser.setSelectedItem(this.getSession().getNamespace(this.settings.getMasterNamespace()));
        this.referringNamespaceChooser.setSelectedItem(this.getSession().getNamespace(this.settings.getReferringNamespace()));
    }

    @Override
    public void commit() {
        this.setMasterNamespace((Namespace)(this.masterNamespaceChooser.getSelectedItem()));
        this.setReferringNamespace((Namespace)(this.referringNamespaceChooser.getSelectedItem()));
        ((SynchronizationComponent)(this.getComponent())).setSettings(this.settings);
    }

    private void setMasterNamespace(Namespace ns) {
        this.settings.setMasterNamespace(ns != null ? ns.getID() : null);
    }

    private void setReferringNamespace(Namespace ns) {
        this.settings.setReferringNamespace(ns != null ? ns.getID() : null);
    }

    private OBOSession getSession() {
        return SessionManager.getManager().getSession();
    }

}
