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

package org.faktorips.devtools.core.ui.actions;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.faktorips.devtools.core.model.IIpsElement;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.ui.IpsUIPlugin;
import org.faktorips.devtools.core.ui.dialogs.IpsPackageSortDefDialog;

/**
 * Contribute the context menu for editing the package sort order.
 * 
 * @author Markus Blum
 */
public class IpsEditSortOrderAction extends IpsAction {

    public IpsEditSortOrderAction(ISelectionProvider selectionProvider) {
        super(selectionProvider);
        super.setText(Messages.IpsEditSortOrderAction_text);
        super.setDescription(Messages.IpsEditSortOrderAction_description);
        super.setToolTipText(Messages.IpsEditSortOrderAction_tooltip);
        super.setImageDescriptor(IpsUIPlugin.getImageHandling().createImageDescriptor("elcl16/alphab_sort_co.gif")); //$NON-NLS-1$

    }

    @Override
    public void run(IStructuredSelection selection) {
        Object element = (selection).getFirstElement();

        if (element instanceof IIpsElement) {
            IIpsElement ipsElement = (IIpsElement)element;
            IIpsProject project = ipsElement.getIpsProject();

            if (project.isProductDefinitionProject()) {
                IpsPackageSortDefDialog dialog = new IpsPackageSortDefDialog(Display.getCurrent().getActiveShell(),
                        Messages.IpsEditSortOrderAction_dialogTitle, project);
                dialog.open();
            } else {
                MessageDialog dialog = new MessageDialog(Display.getCurrent().getActiveShell(),
                        Messages.IpsEditSortOrderAction_dialogTitle, (Image)null,
                        Messages.IpsEditSortOrderAction_dialogInfoText, MessageDialog.INFORMATION,
                        new String[] { IDialogConstants.OK_LABEL }, 0);
                dialog.open();
            }
        }

    }
}
