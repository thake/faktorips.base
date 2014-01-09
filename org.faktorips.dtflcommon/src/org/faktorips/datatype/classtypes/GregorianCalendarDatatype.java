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

import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.faktorips.datatype.ValueClassDatatype;
import org.faktorips.values.DateUtil;

public class GregorianCalendarDatatype extends ValueClassDatatype {

    private final boolean timeInfoIncluded;

    public GregorianCalendarDatatype(String name, boolean timeInfoIncluded) {
        super(GregorianCalendar.class, name);
        this.timeInfoIncluded = timeInfoIncluded;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Object getValue(String value) {
        if (timeInfoIncluded) {
            throw new RuntimeException("Not implemented yet"); //$NON-NLS-1$
        }
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (!DateUtil.isIsoDate(value)) {
            throw new IllegalArgumentException("Date value must have the format YYYY-MM-DD"); //$NON-NLS-1$
        }
        try {
            StringTokenizer tokenizer = new StringTokenizer(value, "-"); //$NON-NLS-1$
            int year = Integer.parseInt(tokenizer.nextToken());
            int month = Integer.parseInt(tokenizer.nextToken());
            int date = Integer.parseInt(tokenizer.nextToken());
            return new GregorianCalendar(year, month - 1, date);
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't parse " + value + " to a date!"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    @Override
    public boolean isParsable(String value) {
        return StringUtils.isEmpty(value) || DateUtil.isIsoDate(value);
    }

    @Override
    public String valueToString(Object value) {
        if (timeInfoIncluded) {
            throw new RuntimeException("Not implemented yet"); //$NON-NLS-1$
        }
        GregorianCalendar calendar = (GregorianCalendar)value;
        if (calendar == null) {
            return ""; //$NON-NLS-1$
        }
        int month = calendar.get(GregorianCalendar.MONTH) + 1;
        int date = calendar.get(GregorianCalendar.DATE);
        return calendar.get(GregorianCalendar.YEAR) + "-" + (month < 10 ? "0" + month : "" + month) + "-" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + (date < 10 ? "0" + date : "" + date); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public boolean supportsCompare() {
        return true;
    }

}
