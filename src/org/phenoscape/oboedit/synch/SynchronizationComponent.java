package org.phenoscape.oboedit.synch;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.bbop.framework.AbstractGUIComponent;
import org.bbop.framework.ComponentConfiguration;
import org.bbop.framework.ConfigurationPanel;
import org.obo.datamodel.OBOClass;
import org.oboedit.controller.SessionManager;
import org.oboedit.gui.event.ReloadEvent;
import org.oboedit.gui.event.ReloadListener;
import org.oboedit.util.GUIUtil;

@SuppressWarnings("serial")
public class SynchronizationComponent extends AbstractGUIComponent {

    private JTabbedPane tabPane;
    private final List<SynchComponent> subcomponents = new ArrayList<SynchComponent>();
    private SynchronizationComponentSettings settings = new SynchronizationComponentSettings();
    private File excludedTermsFile = null;
    private final List<OBOClass> excludedTerms = new ArrayList<OBOClass>();

    public SynchronizationComponent(String id) {
        super(id);
    }

    @Override
    public void init() {
        super.init();
        this.subcomponents.add(new MissingXrefSynchComponent());
        this.subcomponents.add(new ConflictingDataSynchComponent());
        this.subcomponents.add(new MissingTermsSynchComponent());
        this.applyNamespacesToSubcomponents();
        this.initializeInterface();
        GUIUtil.addReloadListener(new ReloadListener() {
            @Override
            public void reload(ReloadEvent arg0) {
                refreshSubcomponents();
            }
        });
    }

    @Override
    public ConfigurationPanel getConfigurationPanel() {
        return new SynchronizationComponentConfigPanel();
    }

    /**
     * This has been changed from getConfiguration to getSettings to avoid OBO-Edit trying to 
     * save the configuration. This works around a classloading problem with OBO-Edit plugin configurations. 
     */
    public ComponentConfiguration getSettings() {
        //TODO get settings using Java Preferences API
        return this.settings;
    }

    /**
     * This has been changed from setConfiguration to setSettings to avoid OBO-Edit trying to 
     * save the configuration. This works around a classloading problem with OBO-Edit plugin configurations.
     */
    public void setSettings(ComponentConfiguration config) {
        //TODO save settings using Java Preferences API
        if ((config != null) && (config instanceof SynchronizationComponentSettings)) {
            this.settings = (SynchronizationComponentSettings)config;
        }
        this.excludedTermsFile = this.settings.getExcludedTermsFile();
        this.loadExcludedTerms();
        this.applyNamespacesToSubcomponents();
        this.refreshSubcomponents();
    }

    private void initializeInterface() {
        this.setLayout(new BorderLayout());
        this.tabPane = new JTabbedPane();
        this.add(this.tabPane, BorderLayout.CENTER);
        for (SynchComponent component : this.subcomponents) {
            this.tabPane.addTab(component.getTitle(), component.getComponent());
        }
        //this.tabPane.addTab("Structural Differences", new JLabel("Not yet implemented."));
    }

    private void applyNamespacesToSubcomponents() {
        for (SynchComponent component : this.subcomponents) {
            log().debug("Setting namespace for: " + component);
            component.setMasterNamespaceID(this.settings.getMasterNamespace());
            component.setReferringNamespaceID(this.settings.getReferringNamespace());
            component.setIgnoredTermsFile(excludedTermsFile);
            component.setIgnoredTerms(excludedTerms);
        }
    }

    private void refreshSubcomponents() {
        for (SynchComponent component : this.subcomponents) {
            component.refreshView();
        }
    } 
    
    private void loadExcludedTerms() {
        if (this.excludedTermsFile != null) {
            try {
                this.excludedTerms.clear();
                final BufferedReader reader = new BufferedReader(new FileReader(this.excludedTermsFile));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    final String termID = line.split("\\t")[0].trim();
                    final OBOClass term = (OBOClass)(SessionManager.getManager().getSession().getObject(termID));
                    this.excludedTerms.add(term);
                }
            } catch (FileNotFoundException e) {
                log().error("Failed to read excluded terms file", e);
            } catch (IOException e) {
                log().error("Failed to read excluded terms file", e);
            }
        }
    }

    private Logger log() {
        return Logger.getLogger(this.getClass());
    }

}
