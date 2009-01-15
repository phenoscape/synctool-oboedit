package org.phenoscape.oboedit.synch;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.bbop.framework.AbstractGUIComponent;
import org.obo.datamodel.OBOSession;
import org.oboedit.controller.SessionManager;

@SuppressWarnings("serial")
public class SynchronizationComponent extends AbstractGUIComponent {
     
    private JComboBox masterNamespaceChooser;
    private JComboBox referringNamespaceChooser;
    private JTabbedPane tabPane;
    private final SynchComponent missingXrefs = new MissingXrefSynchComponent();
    private final SynchComponent confictingData = new ConflictingDataSynchComponent();
    private final SynchComponent missingTerms = new MissingTermsSynchComponent();

    public SynchronizationComponent(String id) {
        super(id);
    }

    @Override
    public void init() {
        super.init();
        this.initializeInterface();
    }
    
    private void initializeInterface() {
        this.setLayout(new GridBagLayout());
        
        final OBOSession session = SessionManager.getManager().getSession();
        this.masterNamespaceChooser = new JComboBox(session.getNamespaces().toArray());
        this.referringNamespaceChooser = new JComboBox(session.getNamespaces().toArray());
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
        
        final JButton refreshButton = new JButton(new AbstractAction("Refresh") {
            public void actionPerformed(ActionEvent e) {
                missingXrefs.refreshView();
                confictingData.refreshView();
                missingTerms.refreshView();
            }
        });
        final GridBagConstraints refreshConstraints = new GridBagConstraints();
        refreshConstraints.gridx = 2;
        refreshConstraints.gridheight = 2;
        refreshConstraints.gridy = 0;
        this.add(refreshButton, refreshConstraints);
        
        this.tabPane = new JTabbedPane();
        final GridBagConstraints tabPaneConstraints = new GridBagConstraints();
        tabPaneConstraints.gridy = 2;
        tabPaneConstraints.gridwidth = 3;
        tabPaneConstraints.fill = GridBagConstraints.BOTH;
        tabPaneConstraints.weightx = 1.0;
        tabPaneConstraints.weighty = 1.0;
        this.add(this.tabPane, tabPaneConstraints);
        this.tabPane.addTab(this.missingXrefs.getTitle(), this.missingXrefs.getComponent());
        this.tabPane.addTab(this.confictingData.getTitle(), this.confictingData.getComponent());
        this.tabPane.addTab(this.missingTerms.getTitle(), this.missingTerms.getComponent());
        this.tabPane.addTab("Structural Differences", new JLabel("Not yet implemented."));
    }
    
    @SuppressWarnings("unused")
    private Logger log() {
        return Logger.getLogger(this.getClass());
    }

}
