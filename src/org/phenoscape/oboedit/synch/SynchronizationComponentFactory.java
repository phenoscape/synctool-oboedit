package org.phenoscape.oboedit.synch;

import org.bbop.framework.AbstractComponentFactory;

public class SynchronizationComponentFactory extends AbstractComponentFactory<SynchronizationComponent> {

    @Override
    public SynchronizationComponent doCreateComponent(String id) {
        return new SynchronizationComponent(id);
    }

    public org.bbop.framework.GUIComponentFactory.FactoryCategory getCategory() {
        return FactoryCategory.TOOLS;
    }

    public String getID() {
        return "synchronization_tool";
    }

    public String getName() {
        return "Synchronization Tool";
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public boolean showInMenus() {
        return true;
    }
    
}
