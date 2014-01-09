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

package org.faktorips.devtools.core.model.ipsobject;

/**
 * Implements the <em>Null-Object</em> pattern for {@link QualifiedNameType}.
 * 
 * @since 3.6
 * 
 * @author Alexander Weickmann
 * 
 * @see QualifiedNameType
 */
public class NullQualifiedNameType extends QualifiedNameType {

    private static final long serialVersionUID = -7931101082356961576L;

    public NullQualifiedNameType() {
        super("", new NullIpsObjectType()); //$NON-NLS-1$
    }

    private static class NullIpsObjectType extends IpsObjectType {

        protected NullIpsObjectType() {
            super("", "", "", "", "", false, false, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        }

    }

}
