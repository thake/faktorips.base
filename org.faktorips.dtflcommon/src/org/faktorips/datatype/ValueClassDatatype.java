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

package org.faktorips.datatype;


/**
 * A datatype that represents a Java class representing a value, for example java.lang.String.
 * 
 * @author Jan Ortmann
 */
public abstract class ValueClassDatatype extends ValueClassNameDatatype {

    private Class<?> clazz;

    public ValueClassDatatype(Class<?> clazz) {
        super(clazz.getName());
        this.clazz = clazz;
    }

    public ValueClassDatatype(Class<?> clazz, String name) {
        super(clazz.getName(), name);
        this.clazz = clazz;
    }

    public Class<?> getJavaClass() {
        return clazz;
    }

}
