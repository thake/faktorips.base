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

package org.faktorips.codegen.dthelpers;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.faktorips.codegen.DatatypeHelper;
import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.datatype.classtypes.StringDatatype;

/**
 * {@link DatatypeHelper} for {@link StringDatatype}.
 */
public class StringHelper extends AbstractDatatypeHelper {

    /**
     * Constructs a new helper for the string datatype.
     */
    public StringHelper() {
        super();
    }

    /**
     * Constructs a new helper for the given string datatype.
     * 
     * @throws IllegalArgumentException if datatype is <code>null</code>.
     */
    public StringHelper(StringDatatype datatype) {
        super(datatype);
    }

    public JavaCodeFragment newInstance(String value) {
        if (value == null) {
            return nullExpression();
        }
        JavaCodeFragment fragment = new JavaCodeFragment();
        fragment.appendQuoted(StringEscapeUtils.escapeJava(value));
        return fragment;
    }

    @Override
    public JavaCodeFragment newInstanceFromExpression(String expression) {
        return valueOfExpression(expression);
    }

    @Override
    protected JavaCodeFragment valueOfExpression(String expression) {
        if (StringUtils.isEmpty(expression)) {
            return nullExpression();
        }
        JavaCodeFragment fragment = new JavaCodeFragment();
        fragment.append(expression);
        return fragment;
    }

    @Override
    public JavaCodeFragment getToStringExpression(String fieldName) {
        return new JavaCodeFragment(fieldName);
    }

}
