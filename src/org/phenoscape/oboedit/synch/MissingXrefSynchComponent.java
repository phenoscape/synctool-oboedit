package org.phenoscape.oboedit.synch;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.obo.datamodel.Dbxref;
import org.obo.datamodel.Link;
import org.obo.datamodel.LinkedObject;
import org.obo.datamodel.OBOClass;
import org.obo.datamodel.impl.DbxrefImpl;
import org.obo.history.AddDbxrefHistoryItem;
import org.oboedit.controller.SessionManager;
import org.oboedit.gui.DefaultSelection;
import org.oboedit.gui.components.TextEditor;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

public class MissingXrefSynchComponent extends AbstractSynchComponent {
    
    private static final String EXPLANATORY_TEXT = "<HTML>These terms have the same name, "
        + "but there is no Dbxref in the term from the referring namespace to the term "
        + "in the master namespace.</HTML>";
    private final EventList<TermPair> termsNeedingXrefs = new SortedList<TermPair>(new BasicEventList<TermPair>(), new Comparator<TermPair>() {
        public int compare(TermPair o1, TermPair o2) {
            return 0;
        }
    });
    private final EventSelectionModel<TermPair> xrefSelectionModel = new EventSelectionModel<TermPair>(this.termsNeedingXrefs);
    private JTable xrefsTable;
    private final TermSelector masterSelector = new TermSelector();
    private final TermSelector referrerSelector = new TermSelector();
    
    public MissingXrefSynchComponent() {
        this.initializeInterface();
    }
    
    public void refreshView() {
        this.termsNeedingXrefs.clear();
        this.termsNeedingXrefs.addAll(this.getMatchingTermNamesWithoutXrefs());    
    }
    
    public String getTitle() {
        return "Missing Dbxrefs";
    }
    
    @SuppressWarnings("serial")
    private void initializeInterface() {
        this.getComponent().setLayout(new GridBagLayout());
        final GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridy = 0;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.weightx = 1.0;
        this.getComponent().add(new JLabel(EXPLANATORY_TEXT), labelConstraints);
        this.xrefsTable = new JTable(new EventTableModel<TermPair>(this.termsNeedingXrefs, new TermPairTableFormat()));
        this.xrefsTable.setSelectionModel(this.xrefSelectionModel);        
        final TextEditor masterTextEditor = new TextEditor(UUID.randomUUID().toString());
        masterTextEditor.setBorder(BorderFactory.createTitledBorder("Master"));
        masterTextEditor.setObjectSelector(this.masterSelector);
        final TextEditor referrerTextEditor = new TextEditor(UUID.randomUUID().toString());
        referrerTextEditor.setBorder(BorderFactory.createTitledBorder("Referrer"));
        referrerTextEditor.setObjectSelector(this.referrerSelector);
        final JPanel inspectorPanel = new JPanel(new GridBagLayout());
        final GridBagConstraints inspectorConstraints = new GridBagConstraints();
        inspectorConstraints.gridx = 0;
        inspectorConstraints.weightx = 1.0;
        inspectorConstraints.weighty = 1.0;
        inspectorConstraints.fill = GridBagConstraints.BOTH;
        inspectorPanel.add(masterTextEditor, inspectorConstraints);
//        //inspectorPanel.add(masterTextEditor, inspectorConstraints);
        inspectorConstraints.gridx = 1;
        inspectorPanel.add(referrerTextEditor, inspectorConstraints);
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(this.xrefsTable), inspectorPanel);
        splitPane.setDividerLocation(0.5);
        splitPane.setContinuousLayout(true);
        final GridBagConstraints splitPaneConstraints = new GridBagConstraints();
        splitPaneConstraints.gridy = 1;
        splitPaneConstraints.fill = GridBagConstraints.BOTH;
        splitPaneConstraints.weightx = 1.0;
        splitPaneConstraints.weighty = 1.0;
        this.getComponent().add(splitPane, splitPaneConstraints);
        final JButton button = new JButton(new AbstractAction("Create Dbxref") {
            public void actionPerformed(ActionEvent e) {
                for (TermPair pair : xrefSelectionModel.getSelected()) {
                    addXref(pair);
                }
                refreshView();
            }
        });
        final GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridy = 2;
        buttonConstraints.anchor = GridBagConstraints.EAST;
        this.getComponent().add(button, buttonConstraints);
        this.xrefSelectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                final LinkedObject masterTerm;
                final LinkedObject referringTerm;
                if (!xrefSelectionModel.isSelectionEmpty() && !(xrefSelectionModel.getSelected().size() > 1)) {
                    masterTerm = xrefSelectionModel.getSelected().get(0).getMaster();
                    referringTerm = xrefSelectionModel.getSelected().get(0).getReferrer();                    
                } else {
                    masterTerm = null;
                    referringTerm = null;
                }
                final List<LinkedObject> masterTerms = new ArrayList<LinkedObject>();
                masterTerms.add(masterTerm);
                masterSelector.select(new DefaultSelection(getComponent(), new ArrayList<Link>(), masterTerms, null, null, null, null, null, masterTerm));
                final List<LinkedObject> referrerTerms = new ArrayList<LinkedObject>();
                referrerTerms.add(referringTerm);
                referrerSelector.select(new DefaultSelection(getComponent(), new ArrayList<Link>(), referrerTerms, null, null, null, null, null, referringTerm));
            }
        });
    }
    
    private List<TermPair> getMatchingTermNamesWithoutXrefs() {
        final List<TermPair> terms = new ArrayList<TermPair>();
        final Collection<OBOClass> referringTerms = this.getReferringTerms(false);
        for (OBOClass master : this.getMasterTerms(false)) {
            final OBOClass match = this.findTerm(master.getName(), referringTerms);
            if (match == null) continue;
            if (!this.doesTermReferenceTerm(match, master)) {
                terms.add(new TermPair(master, match));
            }
        }
        return terms;
    }

    private void addXref(TermPair pair) {
        SessionManager.getManager().apply(new AddDbxrefHistoryItem(pair.getReferrer().getID(), this.makeDbxref(pair.getMaster().getID()), false, null));
    }
    
    private Dbxref makeDbxref(String oboid) {
        final String[] pieces = oboid.split(":", 2);
        return new DbxrefImpl(pieces[0], pieces[1]);
    }
    
    private OBOClass findTerm(String name, Collection<OBOClass> terms) {
        for (OBOClass term : terms) {
            if (name.equals(term.getName())) {
                return term;
            }
        }
        return null;
    }
    
    private boolean doesTermReferenceTerm(OBOClass referrer, OBOClass master) {
        for (Dbxref xref : referrer.getDbxrefs()) {
            if (this.makeOBOID(xref).equals(master.getID())) {
                return true;
            }
        }
        return false;
    }

}
