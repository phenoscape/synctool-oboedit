package org.phenoscape.oboedit.synch;

import javax.swing.JComponent;

import org.obo.datamodel.Namespace;

public interface SynchComponent {
    
    public JComponent getComponent();
    
    public void setMasterNamespace(Namespace namespace);
    
    public Namespace getMasterNamespace();
    
    public void setReferringNamespace(Namespace namespace);
    
    public Namespace getReferringNamespace();
    
    public void refreshView();
    
    public String getTitle();

}
