package org.phenoscape.oboedit.synch;

import java.io.File;
import java.util.List;

import javax.swing.JComponent;

import org.obo.datamodel.Namespace;
import org.obo.datamodel.OBOClass;

public interface SynchComponent {
    
    public JComponent getComponent();
    
    public void setMasterNamespaceID(String namespace);
    
    public Namespace getMasterNamespace();
    
    public void setReferringNamespaceID(String namespace);
    
    public Namespace getReferringNamespace();
    
    public void setIgnoredTerms(List<OBOClass> terms);
    
    public void setIgnoredTermsFile(File file);
    
    public File getIgnoredTermsFile();
    
    public List<OBOClass> getIgnoredTerms();
    
    public void refreshView();
    
    public String getTitle();

}
