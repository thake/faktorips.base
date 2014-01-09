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

package org.faktorips.devtools.core.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.model.ipsobject.IIpsObject;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.ui.views.ipshierarchy.IpsHierarchyView;

/**
 * ShowHierarchyHandler is a defaultHandler for the command id:
 * org.faktorips.devtools.core.ui.actions.showHierarchy in plugin.xml Extensions
 * org.eclipse.ui.commands Opens or updates IpsHierarchyView
 * 
 * @author stoll
 */
public class ShowHierarchyHandler extends IpsAbstractHandler {

    @Override
    public void execute(ExecutionEvent event, IWorkbenchPage activePage, IIpsSrcFile ipsSrcFile) {
        try {
            IIpsObject ipsObject = ipsSrcFile.getIpsObject();
            if (IpsHierarchyView.supports(ipsObject)) {
                try {
                    IViewPart hierarchyView = activePage.showView(IpsHierarchyView.EXTENSION_ID, null,
                            IWorkbenchPage.VIEW_ACTIVATE);
                    ((IpsHierarchyView)hierarchyView).showHierarchy(ipsObject);
                } catch (PartInitException e) {
                    IpsPlugin.logAndShowErrorDialog(e);
                }
            }
        } catch (CoreException e) {
            IpsPlugin.log(e);
        }

    }
}
