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

package org.faktorips.devtools.core.ui.editors;

import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPart;

/**
 * Interface for listeners which want to be notified on ips object part deletion.
 * 
 * @author Thorsten Guenther
 */
public interface IDeleteListener {

    /**
     * Called before the method <code>IIpsObjectPart.delete()</code> is called. The method have to
     * return true otherwise the deletion will be interrupted
     * 
     * @param part The part that will be deleted.
     * @return true to continue deletion or false to interrupt
     */
    public boolean aboutToDelete(IIpsObjectPart part);

    /**
     * Called after the part was deleted.
     * 
     * @param part The deleted part.
     */
    public void deleted(IIpsObjectPart part);

}
