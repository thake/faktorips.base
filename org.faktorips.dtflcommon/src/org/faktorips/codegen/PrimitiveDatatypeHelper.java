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

package org.faktorips.codegen;

/**
 * Extended DatatypeHelper variant for datatypes representing Java's primitives.
 */
public interface PrimitiveDatatypeHelper extends DatatypeHelper {

    /**
     * Given a JavaCodeFragment containing an expression of the primitive type this is a helper for,
     * returns a JavaCodeFragment that converts the given expression to the appropriate wrapper
     * class.
     * 
     * @throws IllegalArgumentException if expression is null.
     */
    public JavaCodeFragment toWrapper(JavaCodeFragment expression);
}
