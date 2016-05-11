/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/
package org.faktorips.devtools.core.internal.model.datatype;

import org.faktorips.codegen.DatatypeHelper;
import org.faktorips.datatype.joda.MonthDayDatatype;

public class MonthDayHelperFactory extends AbstractDateHelperFactory<MonthDayDatatype> {

    public MonthDayHelperFactory() {
        super(MonthDayDatatype.class);
    }

    @Override
    DatatypeHelper createDatatypeHelper(MonthDayDatatype datatype, LocalDateHelperVariant variant) {
        switch (variant) {
            case JAVA8:
                return new org.faktorips.codegen.dthelpers.java8.MonthDayHelper(datatype);
            default:
                return new org.faktorips.codegen.dthelpers.joda.MonthDayHelper(datatype);
        }
    }

}