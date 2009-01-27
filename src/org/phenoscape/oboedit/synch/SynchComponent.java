package org.phenoscape.oboedit.synch;

import javax.swing.JComponent;

import org.obo.datamodel.Namespace;

public interface SynchComponent {
    
    public JComponent getComponent();
    
    public void setMasterNamespaceID(String namespace);
    
    public Namespace getMasterNamespace();
    
    public void setReferringNamespaceID(String namespace);
    
    public Namespace getReferringNamespace();
    
    public void refreshView();
    
    public String getTitle();

}
