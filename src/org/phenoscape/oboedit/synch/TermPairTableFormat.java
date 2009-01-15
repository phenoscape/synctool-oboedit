package org.phenoscape.oboedit.synch;

import ca.odell.glazedlists.gui.TableFormat;

public class TermPairTableFormat implements TableFormat<TermPair> {

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int column) {
        switch(column) {
        case 0: return "Master";
        case 1: return "Referring";
        default: return null;
        }
    }

    public Object getColumnValue(TermPair pair, int column) {
        switch(column) {
        case 0: return pair.getMaster();
        case 1: return pair.getReferrer();
        default: return null;
        }
    }

}
