/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorzehn.org/f10-org:lizenzen:community eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn AG - initial API and implementation - http://www.faktorzehn.de
 *******************************************************************************/

package org.faktorips.devtools.core.ui.search.product.conditions.table;

import org.faktorips.devtools.core.ui.table.CellTrackingEditingSupport;
import org.faktorips.devtools.core.ui.table.LinkedColumnsTraversalStrategy;
import org.faktorips.devtools.core.ui.table.TraversalStrategy;

/**
 * This class is the {@link TraversalStrategy} for the table of search conditions.
 * 
 * @author dicker
 */
final class ConditionsTableTraversalStrategy extends
        LinkedColumnsTraversalStrategy<ProductSearchConditionPresentationModel> {

    ConditionsTableTraversalStrategy(CellTrackingEditingSupport<ProductSearchConditionPresentationModel> editingSupport) {
        super(editingSupport);
        editingSupport.setTraversalStrategy(this);
    }

    @Override
    protected ProductSearchConditionPresentationModel getPreviousVisibleViewItem(ProductSearchConditionPresentationModel currentViewItem) {
        return null;
    }

    @Override
    protected ProductSearchConditionPresentationModel getNextVisibleViewItem(ProductSearchConditionPresentationModel currentViewItem) {
        return null;
    }

    @Override
    protected int getColumnIndex() {
        return getEnhancedCellTrackingEditingSupport().getColumnIndex();
    }

    @Override
    protected boolean canEdit(ProductSearchConditionPresentationModel currentViewItem) {
        return getEnhancedCellTrackingEditingSupport().canEdit(currentViewItem);
    }

    private EnhancedCellTrackingEditingSupport getEnhancedCellTrackingEditingSupport() {
        return ((EnhancedCellTrackingEditingSupport)getEditingSupport());
    }
}