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

package org.faktorips.fl.operations;

import org.faktorips.codegen.CodeFragment;
import org.faktorips.datatype.Datatype;
import org.faktorips.fl.Operation;
import org.faktorips.fl.UnaryOperation;

/**
 * Abstract implementation of {@link UnaryOperation}.
 * 
 * @param <T> a {@link CodeFragment} implementation for a specific target language
 */
public abstract class AbstractUnaryOperation<T extends CodeFragment> implements UnaryOperation<T> {

    private Datatype datatype;
    private String operator;

    /**
     * Creates a new unary operation for the indicated {@link Operation}.
     */
    public AbstractUnaryOperation(Operation operation) {
        this.datatype = operation.getOperand();
        this.operator = operation.getOperator();
    }

    /**
     * Creates a new unary operation for the indicated operator and {@link Datatype data type}.
     */
    public AbstractUnaryOperation(Datatype datatype, String operator) {
        this.datatype = datatype;
        this.operator = operator;
    }

    /**
     * Overridden method.
     * 
     * @see org.faktorips.fl.UnaryOperation#getDatatype()
     */
    public Datatype getDatatype() {
        return datatype;
    }

    /**
     * Overridden method.
     * 
     * @see org.faktorips.fl.UnaryOperation#getOperator()
     */
    public String getOperator() {
        return operator;
    }

}
