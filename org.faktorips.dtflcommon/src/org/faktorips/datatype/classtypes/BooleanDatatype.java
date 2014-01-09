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

package org.faktorips.datatype.classtypes;

import org.faktorips.datatype.ValueClassDatatype;

/**
 * Datatype for <code>Boolean</code>.
 * 
 * @author Jan Ortmann
 */
public class BooleanDatatype extends ValueClassDatatype {

    public BooleanDatatype() {
        super(Boolean.class);
    }

    public BooleanDatatype(String name) {
        super(Boolean.class, name);
    }

    @Override
    public Object getValue(String s) {
        if (s == null) {
            return null;
        }
        if (s.equalsIgnoreCase("false")) { //$NON-NLS-1$
            return Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("true")) { //$NON-NLS-1$
            return Boolean.TRUE;
        }
        throw new IllegalArgumentException("Can't parse " + s + " to Boolean!"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public boolean supportsCompare() {
        return true;
    }

}
