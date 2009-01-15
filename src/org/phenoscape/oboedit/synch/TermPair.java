package org.phenoscape.oboedit.synch;

import org.obo.datamodel.OBOClass;

class TermPair {
    private final OBOClass master;
    private final OBOClass referrer;
    
    public TermPair(OBOClass master, OBOClass referrer) {
        this.master = master;
        this.referrer = referrer;
    }

    public OBOClass getMaster() {
        return this.master;
    }

    public OBOClass getReferrer() {
        return this.referrer;
    }
    
    public String toString() {
        return "master: " + this.master + " ; referrer: " + this.referrer;
    }
    
}
