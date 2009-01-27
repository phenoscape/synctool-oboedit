package org.phenoscape.oboedit.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.obo.datamodel.OBOObject;

/**
 * A table cell renderer for displaying cell values which are ontology terms (OBOClass).
 * The term name is displayed, and the term ID is provided as a tooltip.
 * @author Jim Balhoff
 */
@SuppressWarnings("serial")
public class TermRenderer extends DefaultTableCellRenderer {

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (value instanceof OBOObject) {
          final OBOObject term = (OBOObject)value;
          this.setToolTipText(term.getID());
          final Component component = super.getTableCellRendererComponent(table, (term.getName() != null ? term.getName() : term.getID()), isSelected, hasFocus, row, column);
          if (term.isObsolete()) {
              component.setForeground(Color.RED);
              this.setToolTipText("Obsolete Term: " + term.getID());
          }
          return component;
      } else {
          this.setToolTipText(null);
          return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      }  
  }

}
