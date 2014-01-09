/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3
 * and if and when this source code belongs to the faktorips-runtime or faktorips-valuetype
 * component under the terms of the LGPL Lesser General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.ui.views.productstructureexplorer;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.faktorips.devtools.core.model.type.IAssociation;

public class AssociationSelectionDialog extends ListSelectionDialog {

    public AssociationSelectionDialog(Shell parentShell, List<IAssociation> associations, String message) {
        super(parentShell, associations, new SimpleContentProvider(associations), new WorkbenchLabelProvider(), message);
    }

    private static class SimpleContentProvider implements IStructuredContentProvider {

        private final Object[] associations;

        public SimpleContentProvider(List<IAssociation> associations) {
            this.associations = associations.toArray();
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return associations;
        }

        @Override
        public void dispose() {
            // Nothing to do
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // Nothing to do
        }

    }

}
