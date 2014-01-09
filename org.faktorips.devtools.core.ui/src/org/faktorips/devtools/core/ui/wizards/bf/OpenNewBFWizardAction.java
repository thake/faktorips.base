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

package org.faktorips.devtools.core.ui.wizards.bf;

import org.eclipse.ui.INewWizard;
import org.faktorips.devtools.core.ui.wizards.OpenNewWizardAction;

// TODO has to be registered
public class OpenNewBFWizardAction extends OpenNewWizardAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public INewWizard createWizard() {
        return new NewBFWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        // nothing to do
    }

}
