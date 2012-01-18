package org.phenoscape.oboedit.synch;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.obo.datamodel.Dbxref;
import org.obo.datamodel.OBOClass;
import org.oboedit.controller.SelectionManager;
import org.oboedit.controller.SessionManager;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;


public class MissingTermsSynchComponent extends AbstractSynchComponent {

    private static final String MISSING_FROM_MASTER_EXPLANATORY_TEXT = "<HTML>These are terms in the referring namespace "
        + "that have no Dbxref to a term in the master namespace.  A subset of these may also be listed under the "
        + "\"Missing Dbxrefs\" tab.</HTML>";
    private static final String MISSING_FROM_REFERRING_EXPLANATORY_TEXT = "<HTML>These are terms in the master namespace "
        + "that are not Dbxref'd from any term in the referring namespace.  A subset of these may also be listed under the "
        + "\"Missing Dbxrefs\" tab.</HTML>";
    private static final String UNREFERRING = "unreferring";
    private static final String REFERRED_TO = "referredTo";
    private final EventList<OBOClass> termsMissingFromMaster = new SortedList<OBOClass>(new BasicEventList<OBOClass>(), new Comparator<OBOClass>() {
        @Override
        public int compare(OBOClass o1, OBOClass o2) {
            return 0;
        }
    });
    private final EventSelectionModel<OBOClass> missingFromMasterSelectionModel = new EventSelectionModel<OBOClass>(this.termsMissingFromMaster);
    private final EventList<OBOClass> termsMissingFromReferring = new SortedList<OBOClass>(new BasicEventList<OBOClass>(), new Comparator<OBOClass>() {
        @Override
        public int compare(OBOClass o1, OBOClass o2) {
            return 0;
        }
    });
    private final EventSelectionModel<OBOClass> missingFromReferringSelectionModel = new EventSelectionModel<OBOClass>(this.termsMissingFromReferring);
    private final Set<OBOClass> addToIgnoreFile = new HashSet<OBOClass>();

    public MissingTermsSynchComponent() {
        this.initializeInterface();
    }

    @Override
    public String getTitle() {
        return "Missing Terms";
    }

    @Override
    public void refreshView() {
        this.addToIgnoreFile.clear();
        final Map<String,List<OBOClass>> map = this.getReferredToAndUnreferringTerms();
        this.termsMissingFromReferring.clear();
        this.termsMissingFromReferring.addAll(this.getMasterTerms(false));
        this.termsMissingFromReferring.removeAll(map.get(REFERRED_TO));
        this.termsMissingFromReferring.removeAll(this.getIgnoredTerms());
        this.termsMissingFromMaster.clear();
        this.termsMissingFromMaster.addAll(map.get(UNREFERRING));
        this.termsMissingFromMaster.removeAll(this.getIgnoredTerms());
    }

    @SuppressWarnings("serial")
    private void initializeInterface() {
        this.getComponent().setLayout(new BorderLayout());
        final JTabbedPane tabPane = new JTabbedPane();
        this.getComponent().add(tabPane, BorderLayout.CENTER);
        final JTable missingFromMasterTable = new JTable(new EventTableModel<OBOClass>(this.termsMissingFromMaster, new TermTableFormat()));
        missingFromMasterTable.setSelectionModel(this.missingFromMasterSelectionModel);
        final JPanel missingFromMasterPanel = new JPanel(new BorderLayout());
        missingFromMasterPanel.add(new JScrollPane(missingFromMasterTable), BorderLayout.CENTER);
        missingFromMasterPanel.add(new JLabel(MISSING_FROM_MASTER_EXPLANATORY_TEXT), BorderLayout.NORTH);
        tabPane.addTab("Missing From Master", missingFromMasterPanel);

        final JTable missingFromReferringTable = new JTable(new EventTableModel<OBOClass>(this.termsMissingFromReferring, new TermTableFormat()));
        missingFromReferringTable.setSelectionModel(this.missingFromReferringSelectionModel);
        final JPanel missingFromReferringPanel = new JPanel(new BorderLayout());
        missingFromReferringPanel.add(new JScrollPane(missingFromReferringTable), BorderLayout.CENTER);
        missingFromReferringPanel.add(new JLabel(MISSING_FROM_REFERRING_EXPLANATORY_TEXT), BorderLayout.NORTH);
        tabPane.addTab("Missing From Referring", missingFromReferringPanel);

        final ListSelectionListener selectionListener = new ListSelectionListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void valueChanged(ListSelectionEvent e) {
                final EventSelectionModel<OBOClass> model = ((EventSelectionModel<OBOClass>)(e.getSource()));
                if (!model.isSelectionEmpty()) {
                    SelectionManager.selectTerm(getComponent(), model.getSelected().get(0));
                }
            }

        };
        this.missingFromMasterSelectionModel.addListSelectionListener(selectionListener);
        this.missingFromReferringSelectionModel.addListSelectionListener(selectionListener);


        this.getComponent().add(new JButton(new AbstractAction("Ignore selected terms") {

            @Override
            public void actionPerformed(ActionEvent event) {
                log().debug("Pressed ignore button.");
                writeOutIgnoredTermsFile();
            }

        }), BorderLayout.SOUTH);
    }

    private Map<String,List<OBOClass>> getReferredToAndUnreferringTerms() {
        final List<OBOClass> referredToTerms = new ArrayList<OBOClass>();
        final List<OBOClass> unreferringTerms = new ArrayList<OBOClass>();
        for (OBOClass referrer : this.getReferringTerms(false)) {
            boolean foundXref = false;
            for (Dbxref xref : referrer.getDbxrefs()) {
                final OBOClass candidate = (OBOClass)(SessionManager.getManager().getSession().getObject(this.makeOBOID(xref))); 
                if ((candidate != null) && (candidate.getNamespace().equals(this.getMasterNamespace()))) {
                    referredToTerms.add(candidate);
                    foundXref = true;
                    break;
                }
            }
            if (!foundXref) {
                unreferringTerms.add(referrer);
            }
        }
        final Map<String,List<OBOClass>> map = new HashMap<String,List<OBOClass>>();
        map.put(REFERRED_TO, referredToTerms);
        map.put(UNREFERRING, unreferringTerms);
        return map;
    }
    
    private void writeOutIgnoredTermsFile() {
        if (this.getIgnoredTermsFile() != null) {
            final List<OBOClass> allTermsToIgnore = new ArrayList<OBOClass>();
            allTermsToIgnore.addAll(this.getIgnoredTerms());
            allTermsToIgnore.addAll(this.addToIgnoreFile);
            this.setIgnoredTerms(allTermsToIgnore);
            try {
                final BufferedWriter writer = new BufferedWriter(new FileWriter(this.getIgnoredTermsFile()));
                for (OBOClass term : allTermsToIgnore) {
                    writer.write(term.getID());
                    writer.write("\t");
                    writer.write(term.getName());
                    writer.newLine();
                }
                writer.close();
            } catch (IOException e) {
                log().error("Failed to write ignored terms file", e);
            }
            this.refreshView();
        }
    }

    private class TermTableFormat implements AdvancedTableFormat<OBOClass>, WritableTableFormat<OBOClass> {

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            switch(column) {
            case 0: return " ";
            case 1: return "Term";
            default: return null;
            }

        }

        @Override
        public Object getColumnValue(OBOClass term, int column) {
            switch(column) {
            case 0: return addToIgnoreFile.contains(term);
            case 1: return term.getName();
            default: return null;
            }
        }

        @Override
        public Class<?> getColumnClass(int column) {
            switch(column) {
            case 0: return Boolean.class;
            case 1: return String.class;
            default: return null;
            }
        }

        @Override
        public Comparator<?> getColumnComparator(int column) {
            return GlazedLists.comparableComparator();
        }

        @Override
        public boolean isEditable(OBOClass item, int column) {
            return column == 0;
        }

        @Override
        public OBOClass setColumnValue(OBOClass item, Object value, int column) {
            if ((Boolean)value) {
                addToIgnoreFile.add(item);
            } else {
                addToIgnoreFile.remove(item);
            }
            return item;
        }

    }

}
