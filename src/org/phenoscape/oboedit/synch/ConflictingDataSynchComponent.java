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

import org.apache.commons.lang.ObjectUtils;
import org.obo.datamodel.Dbxref;
import org.obo.datamodel.Link;
import org.obo.datamodel.LinkedObject;
import org.obo.datamodel.OBOClass;
import org.obo.history.DefinitionChangeHistoryItem;
import org.obo.history.NameChangeHistoryItem;
import org.oboedit.controller.SessionManager;
import org.oboedit.gui.DefaultSelection;
import org.oboedit.gui.components.TextEditor;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

public class ConflictingDataSynchComponent extends AbstractSynchComponent {

    private static final String EXPLANATORY_TEXT = "<HTML>These terms have a Dbxref from a term in the referring namespace "
        + "to a term in the master namespace; however they differ in their name, definition, definition Dbxrefs, comment, or synonyms.</HTML>";
    private final EventList<TermPair> termsWithConflictingData = new SortedList<TermPair>(new BasicEventList<TermPair>(), new Comparator<TermPair>() {
        public int compare(TermPair o1, TermPair o2) {
            return 0;
        }
    });
    private final EventSelectionModel<TermPair> conflictsSelectionModel = new EventSelectionModel<TermPair>(this.termsWithConflictingData);
    private JTable xrefsTable;
    private final TermSelector masterSelector = new TermSelector();
    private final TermSelector referrerSelector = new TermSelector();

    public ConflictingDataSynchComponent() {
        this.initializeInterface();
    }

    public void refreshView() {
        log().debug("Refreshing view in ConflictingData component");
        this.termsWithConflictingData.clear();
        this.termsWithConflictingData.addAll(this.getXrefTermsWithConflictingData());
    }

    public String getTitle() {
        return "Conflicting Data";
    }

    @SuppressWarnings("serial")
    private void initializeInterface() {
        this.getComponent().setLayout(new GridBagLayout());
        final GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.gridwidth = 2;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.weightx = 1.0;
        this.getComponent().add(new JLabel(EXPLANATORY_TEXT), labelConstraints);
        this.xrefsTable = new JTable(new EventTableModel<TermPair>(this.termsWithConflictingData, new TermPairTableFormat()));
        this.xrefsTable.setSelectionModel(this.conflictsSelectionModel);
        final TextEditor masterTextEditor = new TextEditor(UUID.randomUUID().toString());
        masterTextEditor.setBorder(BorderFactory.createTitledBorder("Master"));
        masterTextEditor.setObjectSelector(this.masterSelector);
        final TextEditor referrerTextEditor = new TextEditor(UUID.randomUUID().toString());
        referrerTextEditor.setBorder(BorderFactory.createTitledBorder("Referrer"));
        referrerTextEditor.setObjectSelector(this.referrerSelector);
        // this is a hacky workaround to make the texteditor selection work - something happens when creating the other texteditors with their own objectselectors 
        new TextEditor(UUID.randomUUID().toString());
      
        final JPanel inspectorPanel = new JPanel(new GridBagLayout());
        final GridBagConstraints inspectorConstraints = new GridBagConstraints();
        inspectorConstraints.gridx = 0;
        inspectorConstraints.weightx = 1.0;
        inspectorConstraints.weighty = 1.0;
        inspectorConstraints.fill = GridBagConstraints.BOTH;
        inspectorPanel.add(masterTextEditor, inspectorConstraints);
        inspectorConstraints.gridx = 1;
        inspectorPanel.add(referrerTextEditor, inspectorConstraints);
        final GridBagConstraints splitPaneConstraints = new GridBagConstraints();
        splitPaneConstraints.fill = GridBagConstraints.BOTH;
        splitPaneConstraints.weighty = 1.0;
        splitPaneConstraints.weightx = 1.0;
        splitPaneConstraints.gridwidth = 2;
        splitPaneConstraints.gridy = 1;
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(this.xrefsTable), inspectorPanel);
        splitPane.setDividerLocation(0.5);
        splitPane.setContinuousLayout(true);
        this.getComponent().add(splitPane, splitPaneConstraints);
        final JButton copyFromMaster = new JButton(new AbstractAction("Copy Data ->") {
            public void actionPerformed(ActionEvent e) {
                for (TermPair pair : conflictsSelectionModel.getSelected()) {
                    copyAllData(pair.getMaster(), pair.getReferrer());
                }
                refreshView();
            }
        });
        final JButton copyFromReferrer = new JButton(new AbstractAction("<- Copy Data") {
            public void actionPerformed(ActionEvent e) {
                for (TermPair pair : conflictsSelectionModel.getSelected()) {
                    copyAllData(pair.getReferrer(), pair.getMaster());
                }
                refreshView();
            }
        });
        final GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 0;
        buttonConstraints.gridy = 2;
        buttonConstraints.anchor = GridBagConstraints.CENTER;
        buttonConstraints.weightx = 1.0;
        this.getComponent().add(copyFromMaster, buttonConstraints);
        buttonConstraints.gridx = 1;
        this.getComponent().add(copyFromReferrer, buttonConstraints);
        this.conflictsSelectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                final LinkedObject masterTerm;
                final LinkedObject referringTerm;
                if (!conflictsSelectionModel.isSelectionEmpty() && !(conflictsSelectionModel.getSelected().size() > 1)) {
                    masterTerm = conflictsSelectionModel.getSelected().get(0).getMaster();
                    referringTerm = conflictsSelectionModel.getSelected().get(0).getReferrer();                    
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

    private List<TermPair> getXrefTermsWithConflictingData() {
        final List<TermPair> terms = new ArrayList<TermPair>();
        final Collection<OBOClass> masterTerms = this.getMasterTerms(false);
        for (OBOClass referrer : this.getReferringTerms(false)) {
            for (Dbxref xref : referrer.getDbxrefs()) {
                final OBOClass masterCandidate = (OBOClass)(SessionManager.getManager().getSession().getObject(this.makeOBOID(xref)));
                if (masterTerms.contains(masterCandidate)) {
                    if (!this.termsAreEquivalent(masterCandidate, referrer)) {
                        terms.add(new TermPair(masterCandidate, referrer));
                    }
                }
            }
        }
        return terms;
    }

    private void copyAllData(OBOClass from, OBOClass to) {
        SessionManager.getManager().apply(new NameChangeHistoryItem(to, from.getName()));
        SessionManager.getManager().apply(new DefinitionChangeHistoryItem(to, from.getDefinition()));
    }

    private boolean termsAreEquivalent(OBOClass term1, OBOClass term2) {
        if (!ObjectUtils.equals(term1.getName(), term2.getName())) {
            return false;
        }
        if (!ObjectUtils.equals(term1.getDefinition(), term2.getDefinition())) {
            return false;
        }
        if (!ObjectUtils.equals(term1.getComment(), term2.getComment())) {
            return false;
        }
        if (!ObjectUtils.equals(term1.getDefDbxrefs(), term2.getDefDbxrefs())) {
            return false;
        }
        if (!ObjectUtils.equals(term1.getSynonyms(), term2.getSynonyms())) {
            return false;
        }
        return true;
    }

}
