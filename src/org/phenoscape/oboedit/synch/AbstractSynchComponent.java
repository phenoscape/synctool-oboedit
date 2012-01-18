package org.phenoscape.oboedit.synch;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.obo.datamodel.Dbxref;
import org.obo.datamodel.Namespace;
import org.obo.datamodel.OBOClass;
import org.obo.datamodel.OBOSession;
import org.obo.query.QueryEngine;
import org.obo.query.impl.NamespaceQuery;
import org.oboedit.controller.SessionManager;

public abstract class AbstractSynchComponent implements SynchComponent {
    
    private String masterNamespace;
    private String referringNamespace;
    private List<OBOClass> ignoredTerms = new ArrayList<OBOClass>();
    private File ignoredTermsFile;
    private final JComponent component = new JPanel();
    
    @Override
    public void setMasterNamespaceID(String namespace) {
        log().debug("Setting master namespace: " + namespace);
        this.masterNamespace = namespace;
    }
    
    @Override
    public Namespace getMasterNamespace() {
        log().debug("Getting master namespace: " + this.getSession().getNamespace(this.masterNamespace) + " using: " + this.masterNamespace);
        return this.getSession().getNamespace(this.masterNamespace);
    }

    @Override
    public JComponent getComponent() {
        return this.component;
    }

    @Override
    public void setReferringNamespaceID(String namespace) {
        this.referringNamespace = namespace;
    }
    
    @Override
    public Namespace getReferringNamespace() {
        return this.getSession().getNamespace(this.referringNamespace);
    }
    
    @Override
    public void setIgnoredTerms(List<OBOClass> terms) {
        this.ignoredTerms = terms;
    }
    
    @Override
    public List<OBOClass> getIgnoredTerms() {
        return this.ignoredTerms;
    }
    
    @Override
    public void setIgnoredTermsFile(File file) {
        this.ignoredTermsFile = file;
    }
    
    @Override
    public File getIgnoredTermsFile() {
        return this.ignoredTermsFile;
    }

    protected Collection<OBOClass> getMasterTerms(boolean includeObsoletes) {
        if (this.getMasterNamespace() != null) {
            final QueryEngine engine = new QueryEngine(this.getSession());
            final NamespaceQuery masterQuery = new NamespaceQuery(this.getMasterNamespace());
            masterQuery.setAllowObsoletes(includeObsoletes);
            return engine.query(masterQuery);
        } else {
            return Collections.emptyList();
        }
    }
    
    protected Collection<OBOClass> getReferringTerms(boolean includeObsoletes) {
        if (this.getReferringNamespace() != null) {
            final QueryEngine engine = new QueryEngine(this.getSession());
            final NamespaceQuery referringQuery = new NamespaceQuery(this.getReferringNamespace());
            referringQuery.setAllowObsoletes(includeObsoletes);
            return engine.query(referringQuery);
        } else {
            return Collections.emptyList();
        }
    }
    
    protected String makeOBOID(Dbxref xref) {
        return xref.getDatabase() + ":" + xref.getDatabaseID();
    }
    
    private OBOSession getSession() {
        return SessionManager.getManager().getSession();
    }
    
    protected Logger log() {
        return Logger.getLogger(this.getClass());
    }
    
}
