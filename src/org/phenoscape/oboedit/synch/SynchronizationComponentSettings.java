package org.phenoscape.oboedit.synch;

import java.io.File;

import org.bbop.framework.ComponentConfiguration;

public class SynchronizationComponentSettings implements ComponentConfiguration {
    
    private String masterNamespaceID;
    private String referringNamespaceID;
    private File excludedTermsFile;
    
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
    
    public void setExcludedTermsFile(File file) {
        this.excludedTermsFile = file;
    }
    
    public File getExcludedTermsFile() {
        return this.excludedTermsFile;
    }
    
    @Override
    public String toString() {
        return "{Master Namespace: " + this.masterNamespaceID + " Referring Namespace: " + this.referringNamespaceID + "}";
    }
    
}
