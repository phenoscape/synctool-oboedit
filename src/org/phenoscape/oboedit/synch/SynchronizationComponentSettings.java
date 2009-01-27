package org.phenoscape.oboedit.synch;

import org.bbop.framework.ComponentConfiguration;

public class SynchronizationComponentSettings implements ComponentConfiguration {
    
    private String masterNamespaceID;
    private String referringNamespaceID;
    
    public SynchronizationComponentSettings() {
        super();
    }
    
    public void setMasterNamespace(String id) {
        this.masterNamespaceID = id;
    }
    
    public String getMasterNamespace() {
        return this.masterNamespaceID;
    }

    public void setReferringNamespace(String id) {
        this.referringNamespaceID = id;
    }
    
    public String getReferringNamespace() {
        return this.referringNamespaceID;
    }
    
    public String toString() {
        return "{Master Namespace: " + this.masterNamespaceID + " Referring Namespace: " + this.referringNamespaceID + "}";
    }
}
