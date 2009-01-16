package org.phenoscape.oboedit.synch;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.obo.datamodel.Dbxref;
import org.obo.datamodel.Namespace;
import org.obo.datamodel.OBOClass;
import org.obo.query.QueryEngine;
import org.obo.query.impl.NamespaceQuery;
import org.oboedit.controller.SessionManager;

public abstract class AbstractSynchComponent implements SynchComponent {
    
    private Namespace masterNamespace;
    private Namespace referringNamespace;
    private final JComponent component = new JPanel();
    
    public void setMasterNamespace(Namespace namespace) {
        this.masterNamespace = namespace;
    }
    
    public Namespace getMasterNamespace() {
        return this.masterNamespace;
    }

    public JComponent getComponent() {
        return this.component;
    }

    public void setReferringNamespace(Namespace namespace) {
        this.referringNamespace = namespace;
    }
    
    public Namespace getReferringNamespace() {
        return this.referringNamespace;
    }

    protected Collection<OBOClass> getMasterTerms() {
        final QueryEngine engine = new QueryEngine(SessionManager.getManager().getSession());
        final NamespaceQuery masterQuery = new NamespaceQuery(this.getMasterNamespace());
        return engine.query(masterQuery);
    }
    
    protected Collection<OBOClass> getReferringTerms() {
        final QueryEngine engine = new QueryEngine(SessionManager.getManager().getSession());
        final NamespaceQuery referringQuery = new NamespaceQuery(this.getReferringNamespace());
        return engine.query(referringQuery);
    }
    
    protected String makeOBOID(Dbxref xref) {
        return xref.getDatabase() + ":" + xref.getDatabaseID();
    }
    
}
