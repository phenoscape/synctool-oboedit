package org.phenoscape.oboedit.synch;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.bbop.framework.ConfigurationPanel;
import org.obo.datamodel.Namespace;
import org.obo.datamodel.OBOSession;
import org.oboedit.controller.SessionManager;

@SuppressWarnings("serial")
public class SynchronizationComponentConfigPanel extends ConfigurationPanel {

    private SynchronizationComponentSettings settings = new SynchronizationComponentSettings();
    private JComboBox masterNamespaceChooser;
    private JComboBox referringNamespaceChooser;

    public SynchronizationComponentConfigPanel() {
        super();
        this.setLayout(new GridBagLayout());
        this.masterNamespaceChooser = new JComboBox();
        this.referringNamespaceChooser = new JComboBox();
        final GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.anchor = GridBagConstraints.EAST;
        final GridBagConstraints popupConstraints = new GridBagConstraints();
        popupConstraints.gridx = 1;
        popupConstraints.gridy = 0;
        popupConstraints.fill = GridBagConstraints.HORIZONTAL;
        popupConstraints.weightx = 1.0;
        this.add(new JLabel("Master Namespace:"), labelConstraints);
        this.add(this.masterNamespaceChooser, popupConstraints);
        labelConstraints.gridy = 1;
        popupConstraints.gridy = 1;
        this.add(new JLabel("Referring Namespace:"), labelConstraints);
        this.add(this.referringNamespaceChooser, popupConstraints);
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
