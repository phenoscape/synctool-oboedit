package org.phenoscape.oboedit.synch;

import java.util.Collection;
import java.util.Collections;

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
    private final JComponent component = new JPanel();
    
    public void setMasterNamespaceID(String namespace) {
        log().debug("Setting master namespace: " + namespace);
        this.masterNamespace = namespace;
    }
    
    public Namespace getMasterNamespace() {
        log().debug("Getting master namespace: " + this.getSession().getNamespace(this.masterNamespace) + " using: " + this.masterNamespace);
        return this.getSession().getNamespace(this.masterNamespace);
    }

    public JComponent getComponent() {
        return this.component;
    }

    public void setReferringNamespaceID(String namespace) {
        this.referringNamespace = namespace;
    }
    
    public Namespace getReferringNamespace() {
        return this.getSession().getNamespace(this.referringNamespace);
    }

    protected Collection<OBOClass> getMasterTerms() {
        if (this.getMasterNamespace() != null) {
            final QueryEngine engine = new QueryEngine(this.getSession());
            final NamespaceQuery masterQuery = new NamespaceQuery(this.getMasterNamespace());
            return engine.query(masterQuery);
        } else {
            return Collections.emptyList();
        }
    }
    
    protected Collection<OBOClass> getReferringTerms() {
        if (this.getReferringNamespace() != null) {
            final QueryEngine engine = new QueryEngine(this.getSession());
            final NamespaceQuery referringQuery = new NamespaceQuery(this.getReferringNamespace());
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
