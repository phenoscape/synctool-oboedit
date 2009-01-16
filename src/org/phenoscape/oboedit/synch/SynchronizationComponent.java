package org.phenoscape.oboedit.synch;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.bbop.framework.AbstractGUIComponent;
import org.obo.datamodel.Namespace;
import org.obo.datamodel.OBOSession;
import org.oboedit.controller.SessionManager;
import org.oboedit.gui.event.ReloadEvent;
import org.oboedit.gui.event.ReloadListener;
import org.oboedit.util.GUIUtil;

@SuppressWarnings("serial")
public class SynchronizationComponent extends AbstractGUIComponent {
     
    private JComboBox masterNamespaceChooser;
    private JComboBox referringNamespaceChooser;
    private JTabbedPane tabPane;
    private final List<SynchComponent> subcomponents = new ArrayList<SynchComponent>();

    public SynchronizationComponent(String id) {
        super(id);
    }

    @Override
    public void init() {
        super.init();
        this.subcomponents.add(new MissingXrefSynchComponent());
        this.subcomponents.add(new ConflictingDataSynchComponent());
        this.subcomponents.add(new MissingTermsSynchComponent());
        this.initializeInterface();
        GUIUtil.addReloadListener(new ReloadListener() {
            public void reload(ReloadEvent arg0) {
                configureNamespaceChoosers();
                //refreshSubcomponents();
            }
        });
    }
    
    private void initializeInterface() {
        this.setLayout(new GridBagLayout());
        this.masterNamespaceChooser = new JComboBox();
        this.masterNamespaceChooser.setAction(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setMasterNamespace((Namespace)(masterNamespaceChooser.getSelectedItem()));
            }
        });
        this.referringNamespaceChooser = new JComboBox();
        this.referringNamespaceChooser.setAction(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setReferringNamespace((Namespace)(referringNamespaceChooser.getSelectedItem()));            }
        });
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
                refreshSubcomponents();
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
        for (SynchComponent component : this.subcomponents) {
            this.tabPane.addTab(component.getTitle(), component.getComponent());
        }
        this.tabPane.addTab("Structural Differences", new JLabel("Not yet implemented."));
        this.configureNamespaceChoosers();
    }
    
    private void configureNamespaceChoosers() {
        final OBOSession session = SessionManager.getManager().getSession();
        this.masterNamespaceChooser.setModel(new DefaultComboBoxModel(session.getNamespaces().toArray()));
        this.referringNamespaceChooser.setModel(new DefaultComboBoxModel(session.getNamespaces().toArray()));
        this.setReferringNamespace((Namespace)(this.masterNamespaceChooser.getSelectedItem()));
        this.setReferringNamespace((Namespace)(this.referringNamespaceChooser.getSelectedItem()));
    }
    
    private void refreshSubcomponents() {
        for (SynchComponent component : this.subcomponents) {
            component.refreshView();
        }
    }
    
    private void setMasterNamespace(Namespace ns) {
        for (SynchComponent component : this.subcomponents) {
            component.setMasterNamespace(ns);
        }
    }
    
   private void setReferringNamespace(Namespace ns) {
       for (SynchComponent component : this.subcomponents) {
           component.setReferringNamespace(ns);
       }
    }
    
    @SuppressWarnings("unused")
    private Logger log() {
        return Logger.getLogger(this.getClass());
    }

}
