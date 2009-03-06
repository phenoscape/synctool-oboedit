package org.phenoscape.oboedit.synch;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.obo.datamodel.LinkDatabase;
import org.obo.datamodel.PathCapable;
import org.obo.datamodel.RootAlgorithm;
import org.oboedit.gui.ObjectSelector;
import org.oboedit.gui.Selection;
import org.oboedit.gui.event.ExpandCollapseListener;
import org.oboedit.gui.event.SelectionEvent;
import org.oboedit.gui.event.SelectionListener;

public class TermSelector implements ObjectSelector {

    private final List<SelectionListener> listeners = new ArrayList<SelectionListener>();
    private Selection selection = null;

    public void addExpansionListener(ExpandCollapseListener arg0) {}

    public void addSelectionListener(SelectionListener o) {
        this.listeners.add(o);
    }

    public LinkDatabase getLinkDatabase() {
        return null;
    }

    public RootAlgorithm getRootAlgorithm() {
        return null;
    }

    public Selection getSelection() {
        return this.selection;
    }

    public Selection getSelection(MouseEvent arg0) {
        return this.getSelection();
    }

    public Collection<PathCapable> getVisibleObjects() {
        return null;
    }

    public boolean hasCombinedTermsAndLinks() {
        return false;
    }

    public boolean isLive() {
        return false;
    }

    public void removeExpansionListener(ExpandCollapseListener arg0) {}

    public void removeSelectionListener(SelectionListener o) {
        this.listeners.remove(o);
    }

    public void select(Selection selection) {
        this.selection = selection;
        for (SelectionListener listener : this.listeners) {
            listener.selectionChanged(new SelectionEvent(this.selection));
        }
    }

    public void setLive(boolean arg0) {}

}