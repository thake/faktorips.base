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

package org.faktorips.devtools.core.model.value;

/**
 * Enum for the result of the check of {@link ValueType}
 * 
 * @author frank
 * @since 3.9
 */
public enum ValueTypeMismatch {

    NO_MISMATCH,
    STRING_TO_INTERNATIONAL_STRING,
    INTERNATIONAL_STRING_TO_STRING;

    public static ValueTypeMismatch getMismatch(IValue<?> value, boolean multilingual) {
        if (ValueType.getValueType(value).equals(ValueType.STRING) && multilingual) {
            return STRING_TO_INTERNATIONAL_STRING;
        } else if (ValueType.getValueType(value).equals(ValueType.INTERNATIONAL_STRING) && !multilingual) {
            return INTERNATIONAL_STRING_TO_STRING;
        } else {
            return NO_MISMATCH;
        }
    }
}